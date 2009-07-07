package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.util.ErrorPrinter;
import com.aoindustries.util.logging.ErrorPrinterFormatter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.XMLFormatter;

/**
 * An implementation of <code>Handler</code> that logs to the ticket system.
 * It queues log entries and logs them in the background.  The log entries
 * are added in the order received, regardless of priority.
 *
 * Defaults to using ErrorPrinterFormatter to System.err and
 * XMLFormatter to the ticket.  Setting the formatter will only
 * alter the output to System.err, the tickets are always created
 * with an XML format.
 *
 * @see ErrorPrinterFormatter
 * 
 * @author  AO Industries, Inc.
 */
final public class TicketLoggingHandler extends Handler {

    private static final List<TicketLoggingHandler> handlers = new ArrayList<TicketLoggingHandler>();

    /**
     * Only one TicketLoggingHandler will be created per unique summaryPrefix,
     * AOServConnector, and category.
     */
    public static Handler getHandler(String summaryPrefix, AOServConnector connector, TicketCategory category) throws IOException, SQLException {
        synchronized(handlers) {
            for(TicketLoggingHandler handler : handlers) {
                if(
                    handler.summaryPrefix.equals(summaryPrefix)
                    && handler.connector==connector
                    && handler.category.equals(category)
                ) return handler;
            }
            TicketLoggingHandler handler = new TicketLoggingHandler(summaryPrefix, connector, category);
            handlers.add(handler);
            return handler;
        }
    }

    private final String summaryPrefix;
    private final AOServConnector connector;
    private final TicketCategory category;
    private final Business business;
    private final Brand brand;
    private final Language language;
    private final TicketType ticketType;

    private final ExecutorService systemErrExecutor;
    private final ExecutorService ticketExecutor;

    private XMLFormatter xmlFormatter;

    private TicketLoggingHandler(String summaryPrefix, AOServConnector connector, TicketCategory category) throws IOException, SQLException {
        this.summaryPrefix = summaryPrefix;
        this.connector = connector;
        this.category = category;
        setFormatter(ErrorPrinterFormatter.getInstance());
        // Look-up things in advance to reduce possible round-trips during logging
        business = connector.getThisBusinessAdministrator().getUsername().getPackage().getBusiness();
        brand = business.getBrand();
        if(brand==null) throw new SQLException("Unable to find Brand for connector: "+connector);
        language = connector.getLanguages().get(Language.EN);
        if(language==null) throw new SQLException("Unable to find Language: "+Language.EN);
        ticketType = connector.getTicketTypes().get(TicketType.LOGS);
        if(ticketType==null) throw new SQLException("Unable to find TicketType: "+TicketType.LOGS);
        // Ready to run - create the executors
        systemErrExecutor = Executors.newSingleThreadExecutor(
            new ThreadFactory() {
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setName("System.err logger for "+TicketLoggingHandler.this.connector.toString());
                    thread.setDaemon(true);
                    thread.setPriority(Thread.NORM_PRIORITY+1);
                    return thread;
                }
            }
        );
        ticketExecutor = Executors.newSingleThreadExecutor(
            new ThreadFactory() {
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setName("Tiket logger for "+TicketLoggingHandler.this.connector.toString());
                    thread.setDaemon(true);
                    thread.setPriority(Thread.NORM_PRIORITY+1);
                    return thread;
                }
            }
        );
    }

    @Override
    public void publish(final LogRecord record) {
        // Call getSourceClassName and getSourceMethodName to set their values before the background processing.
        record.getSourceClassName();
        record.getSourceMethodName();

        // Queue for System.err output
        systemErrExecutor.submit(
            new Runnable() {
                @Override
                public void run() {
                    Formatter formatter = getFormatter();
                    String printme = formatter.format(record);
                    synchronized(System.err) {
                        System.err.print(printme);
                    }
                }
            }
        );
        // Queue for Ticket creation
        ticketExecutor.submit(
            new Runnable() {
                @Override
                public void run() {
                    try {
                        // The priority depends on the log level
                        int level = record.getLevel().intValue();
                        String priorityName;
                        if(level<=Level.CONFIG.intValue()) priorityName = TicketPriority.LOW;
                        else if(level<=Level.INFO.intValue()) priorityName = TicketPriority.NORMAL;
                        else if(level<=Level.WARNING.intValue()) priorityName = TicketPriority.HIGH;
                        else priorityName = TicketPriority.URGENT;
                        TicketPriority priority = connector.getTicketPriorities().get(priorityName);
                        if(priority==null) throw new SQLException("Unable to find TicketPriority: "+priorityName);
                        // Generate the summary
                        StringBuilder summary = new StringBuilder(summaryPrefix);
                        if(summary.length()>0) summary.append(" - ");
                        summary.append(record.getSequenceNumber());
                        Throwable thrown = record.getThrown();
                        if(thrown!=null) summary.append(" - ").append(thrown.toString());
                        // Only create when first needed
                        if(xmlFormatter==null) xmlFormatter = new XMLFormatter();
                        connector.getTickets().addTicket(
                            brand,
                            business,
                            language,
                            category,
                            ticketType,
                            null,
                            summary.toString(),
                            xmlFormatter.format(record),
                            priority,
                            "",
                            ""
                        );
                        xmlFormatter.format(record);
                    } catch(Exception err) {
                        ErrorPrinter.printStackTraces(err);
                    }
                }
            }
        );
    }

    @Override
    public void flush() {
        System.err.flush();
        // Alternately, could wait until all queued log records up to this moment have been handled.  If
        // no log records are added while we wait, don't wait for them.
    }

    @Override
    public void close() throws SecurityException {
        systemErrExecutor.shutdown();
        ticketExecutor.shutdown();
        try {
            // Wait up to one minute for System.err to complete its tasks
            systemErrExecutor.awaitTermination(1, TimeUnit.MINUTES);
        } catch(InterruptedException err) {
            // Ignored
        }
        try {
            // Wait up to one minute for tickets to complete its tasks
            ticketExecutor.awaitTermination(1, TimeUnit.MINUTES);
        } catch(InterruptedException err) {
            // Ignored
        }
    }
}
