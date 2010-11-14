package com.aoindustries.aoserv.client;

/*
 * Copyright 2009-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.command.*;
import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.util.ErrorPrinter;
import com.aoindustries.util.StringUtility;
import com.aoindustries.util.logging.QueuedHandler;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * <p>
 * An implementation of <code>Handler</code> that logs to the ticket system.
 * It queues log entries and logs them in the background.  The log entries
 * are added in the order received, regardless of priority.
 * </p>
 * <p>
 * Will first look for any open/hold/bounced ticket that is for the same
 * brand, business, language, type, level, prefix, classname, method, and category.
 * If found, it will annotate that ticket.  If not found, it will create a new
 * ticket.
 * </p>
 * 
 * @author  AO Industries, Inc.
 */
final public class TicketLoggingHandler extends QueuedHandler {

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

    private TicketLoggingHandler(String summaryPrefix, final AOServConnector connector, TicketCategory category) throws IOException, SQLException {
        super(
            "Console logger for "+connector.toString(),
            "Ticket logger for "+connector.toString()
        );
        this.summaryPrefix = summaryPrefix;
        this.connector = connector;
        this.category = category;
        // Look-up things in advance to reduce possible round-trips during logging
        business = connector.getThisBusinessAdministrator().getUsername().getBusiness();
        brand = business.getBrand();
        if(brand==null) throw new NoSuchElementException("Unable to find Brand for connector: "+connector);
        language = connector.getLanguages().get(Language.EN);
        ticketType = connector.getTicketTypes().get(TicketType.LOGS);
    }

    @Override
    protected boolean useCustomLogging(LogRecord record) {
        return record.getLevel().intValue()>Level.FINE.intValue();
    }

    @Override
    protected void doCustomLogging(Formatter formatter, LogRecord record, String fullReport) {
        try {
            Level level = record.getLevel();
            // Generate the summary from level, prefix classname, method
            StringBuilder tempSB = new StringBuilder();
            tempSB.append('[').append(level).append(']');
            if(summaryPrefix!=null && summaryPrefix.length()>0) tempSB.append(' ').append(summaryPrefix);
            tempSB.append(" - ").append(record.getSourceClassName()).append(" - ").append(record.getSourceMethodName());
            String summary = tempSB.toString();
            // Look for an existing ticket to append
            Ticket existingTicket = null;
            for(Ticket ticket : connector.getTickets().getSet()) {
                String status = ticket.getStatus().getStatus();
                if(
                    (
                        TicketStatus.OPEN==status // OK - interned
                        || TicketStatus.HOLD==status // OK - interned
                        || TicketStatus.BOUNCED==status // OK - interned
                    ) && brand==ticket.getBrand() // OK - interned
                    && business.equals(ticket.getBusiness())
                    && language.equals(ticket.getLanguage())
                    && ticketType.equals(ticket.getTicketType())
                    && ticket.getSummary().equals(summary) // level, prefix, classname, and method
                    && StringUtility.equals(category, ticket.getCategory())
                ) {
                    existingTicket = ticket;
                    break;
                }
            }
            if(existingTicket!=null) {
                new AddTicketAnnotationCommand(
                    existingTicket,
                    generateActionSummary(formatter, record),
                    fullReport
                ).execute(existingTicket.getConnector());
            } else {
                // The priority depends on the log level
                String priorityName;
                int intLevel = level.intValue();
                if(intLevel<=Level.CONFIG.intValue()) priorityName = TicketPriority.LOW;           // FINE < level <= CONFIG
                else if(intLevel<=Level.INFO.intValue()) priorityName = TicketPriority.NORMAL;     // CONFIG < level <=INFO
                else if(intLevel<=Level.WARNING.intValue()) priorityName = TicketPriority.HIGH;    // INFO < level <=WARNING
                else priorityName = TicketPriority.URGENT;                                         // WARNING < level
                TicketPriority priority = connector.getTicketPriorities().get(priorityName);
                new AddTicketCommand(
                    brand,
                    business,
                    language,
                    category,
                    ticketType,
                    (Email)null,
                    summary,
                    fullReport,
                    priority,
                    "",
                    ""
                ).execute(connector);
            }
        } catch(Exception err) {
            ErrorPrinter.printStackTraces(err);
        }
    }

    public static final String generateActionSummary(Formatter formatter, LogRecord record) {
        // Generate the annotation summary as localized message + thrown
        StringBuilder tempSB = new StringBuilder();
        String message = formatter.formatMessage(record);
        if(message!=null) {
            message = message.trim();
            int eol = message.indexOf('\n');
            boolean doEllipsis = false;
            if(eol!=-1) {
                message = message.substring(0, eol).trim();
                doEllipsis = true;
            }
            if(message.length()>0) {
                tempSB.append(message);
                if(doEllipsis) tempSB.append('\u2026');
            }
        }
        Throwable thrown = record.getThrown();
        if(thrown!=null) {
            if(tempSB.length()>0) tempSB.append(" - ");
            String thrownMessage = thrown.getMessage();
            boolean doEllipsis = false;
            if(thrownMessage!=null) {
                thrownMessage = thrownMessage.trim();
                int eol = thrownMessage.indexOf('\n');
                if(eol!=-1) {
                    thrownMessage = thrownMessage.substring(0, eol).trim();
                    doEllipsis = true;
                }
            }
            if(thrownMessage!=null && thrownMessage.length()>0) {
                tempSB.append(thrownMessage);
                if(doEllipsis) tempSB.append('\u2026');
            } else {
                tempSB.append(thrown.toString());
            }
        }
        return tempSB.toString();
    }
}
