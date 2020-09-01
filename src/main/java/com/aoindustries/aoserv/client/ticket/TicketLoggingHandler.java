/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2009-2012, 2016, 2017, 2018, 2019, 2020  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aoserv-client.
 *
 * aoserv-client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aoserv-client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aoserv-client.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.aoserv.client.ticket;

import com.aoindustries.aoserv.client.AOServClientConfiguration;
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.account.User;
import com.aoindustries.aoserv.client.reseller.Brand;
import com.aoindustries.aoserv.client.reseller.Category;
import com.aoindustries.exception.ConfigurationException;
import com.aoindustries.lang.Strings;
import com.aoindustries.util.logging.QueuedHandler;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;

/**
 * <p>
 * An implementation of {@link Handler} that logs to the ticket system.
 * It queues log entries and logs them in the background.  The log entries
 * are added in the order received, regardless of priority.
 * </p>
 * <p>
 * Will first look for any open/hold/bounced ticket that is for the same
 * brand, account, language, type, level, prefix, classname, method, and category.
 * If found, it will annotate that ticket.  If not found, it will create a new
 * ticket.
 * </p>
 * <p>
 * To minimize resource consumption, this shares one {@link ExecutorService} for all handlers,
 * which means tickets are fed to the master(s) sequentially, even across many
 * different connectors.
 * </p>
 *
 * @author  AO Industries, Inc.
 */
public class TicketLoggingHandler extends QueuedHandler {

	private static final boolean DEBUG = false;

	private static final List<WeakReference<TicketLoggingHandler>> handlers = new ArrayList<>();

	/**
	 * Shares one queue across all handlers.
	 * This is created when first accessed, and released when the last handler
	 * is {@linkplain #close() closed}.
	 */
	private static ExecutorService executor;

	private static ExecutorService getExecutor() {
		synchronized(handlers) {
			if(executor == null) {
				executor = Executors.newSingleThreadExecutor(
					(Runnable r) -> {
						Thread thread = new Thread(r);
						thread.setName("Ticket Logger");
						thread.setDaemon(true);
						thread.setPriority(Thread.NORM_PRIORITY - 1);
						return thread;
					}
				);
			}
			return executor;
		}
	}

	/**
	 * Only one TicketLoggingHandler will be created per unique summaryPrefix,
	 * AOServConnector, and categoryDotPath.
	 */
	public static TicketLoggingHandler getHandler(String summaryPrefix, AOServConnector connector, String categoryDotPath) {
		synchronized(handlers) {
			TicketLoggingHandler handler = null;
			Iterator<WeakReference<TicketLoggingHandler>> iter = handlers.iterator();
			while(iter.hasNext()) {
				WeakReference<TicketLoggingHandler> ref = iter.next();
				TicketLoggingHandler h = ref.get();
				if(h == null) {
					// Garbage collected
					iter.remove();
				} else {
					if(
						handler == null // Duplicates in list are possible, since the list is added-to by the protected / public constructors, too
						&& h.connector == connector
						&& Objects.equals(h.summaryPrefix, summaryPrefix)
						&& Objects.equals(h.categoryDotPath, categoryDotPath)
					) {
						handler = h;
					}
				}
			}
			if(handler == null) {
				handler = new TicketLoggingHandler(
					summaryPrefix,
					connector,
					categoryDotPath
				);
			}
			return handler;
		}
	}

	private final String summaryPrefix;
	private final AOServConnector connector;
	private final String categoryDotPath;

	protected TicketLoggingHandler(String summaryPrefix, AOServConnector connector, String categoryDotPath) {
		super(getExecutor());
		// super("Ticket logger for " + connector.toString());
		synchronized(handlers) {
			handlers.add(new WeakReference<>(this));
		}
		this.summaryPrefix = Strings.nullIfEmpty(summaryPrefix);
		this.connector = connector;
		this.categoryDotPath = Strings.nullIfEmpty(categoryDotPath);
		debug();
	}

	/**
	 * Public constructor required so can be specified in <code>logging.properties</code>.
	 * Supports the following optional settings in <code>logging.properties</code>:
	 * <ul>
	 * <li><code>(classname).summaryPrefix</code> - the summary prefix for tickets.</li>
	 * <li><code>(classname).username</code> - the username to login as.  When not
	 *     set, the username from <code>aoserv-client.properties</code> is used.</li>
	 * <li><code>(classname).password</code> - the password to login with.  When not
	 *     set, the password from <code>aoserv-client.properties</code> is used.</li>
	 * <li><code>(classname).categoryDotPath</code> - the {@linkplain Category#getDotPath() category dot path} for tickets.</li>
	 * </ul>
	 */
	public TicketLoggingHandler() throws ConfigurationException {
		super(getExecutor());
		synchronized(handlers) {
			handlers.add(new WeakReference<>(this));
		}
		try {
			LogManager manager = LogManager.getLogManager();
			String cname = getClass().getName();

			this.summaryPrefix = Strings.trimNullIfEmpty(
				manager.getProperty(cname + ".summaryPrefix")
			);

			User.Name username = User.Name.valueOf(
				Strings.trimNullIfEmpty(
					manager.getProperty(cname + ".username")
				)
			);
			if(username == null) username = AOServClientConfiguration.getUsername();

			String password = Strings.trimNullIfEmpty(
				manager.getProperty(cname + ".password")
			);
			if(password == null) password = AOServClientConfiguration.getPassword();

			this.connector = AOServConnector.getConnector(username, password);

			this.categoryDotPath = Strings.trimNullIfEmpty(
				manager.getProperty(cname + ".categoryDotPath")
			);
		} catch(ValidationException e) {
			throw new ConfigurationException(e);
		}
		debug();
	}

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	private void debug() {
		if(DEBUG) {
			System.err.println();
			System.err.println("TicketLoggingHandler: ");
			System.err.println("    class...........: " + getClass().getName());
			System.err.println("    summaryPrefix...: " + summaryPrefix);
			System.err.println("    connector.......: " + connector);
			System.err.println("    categoryDotPath.: " + categoryDotPath);
			System.err.println();
		}
	}

	/**
	 * Clean-up this handler and any that were garbage collected.
	 */
	@Override
	public void close() throws SecurityException {
		super.close();
		synchronized(handlers) {
			boolean hasHandler = false;
			Iterator<WeakReference<TicketLoggingHandler>> iter = handlers.iterator();
			while(iter.hasNext()) {
				WeakReference<TicketLoggingHandler> ref = iter.next();
				TicketLoggingHandler handler = ref.get();
				if(
					// Garbage collected
					handler == null
					// This one
					|| handler == this
				) {
					iter.remove();
				} else {
					hasHandler = true;
				}
			}
			if(!hasHandler) {
				assert handlers.isEmpty();
				if(executor != null) {
					shutdownExecutor(executor);
					executor = null;
				}
			}
		}
	}

	@Override
	protected void backgroundPublish(Formatter formatter, LogRecord record, String fullReport) throws IOException, SQLException {
		// Look-up things
		Account account = connector.getCurrentAdministrator().getUsername().getPackage().getAccount();
		Brand brand = account.getBrand();
		if(brand == null) throw new SQLException("Unable to find Brand for connector: " + connector);
		Language language = connector.getTicket().getLanguage().get(Language.EN);
		if(language == null) throw new SQLException("Unable to find Language: " + Language.EN);
		TicketType ticketType = connector.getTicket().getTicketType().get(TicketType.LOGS);
		if(ticketType == null) throw new SQLException("Unable to find TicketType: " + TicketType.LOGS);
		Category category;
		if(categoryDotPath != null) {
			category = connector.getReseller().getCategory().getTicketCategoryByDotPath(categoryDotPath);
			if(category == null) throw new SQLException("Unable to find Category: " + categoryDotPath);
		} else {
			category = null;
		}
		Level level = record.getLevel();
		// Generate the summary from level, prefix classname, method
		StringBuilder tempSB = new StringBuilder();
		tempSB.append('[').append(level).append(']');
		if(summaryPrefix != null) tempSB.append(' ').append(summaryPrefix);
		tempSB.append(" - ").append(record.getSourceClassName()).append(" - ").append(record.getSourceMethodName());
		String summary = tempSB.toString();
		// Look for an existing ticket to append
		Ticket existingTicket = null;
		for(Ticket ticket : connector.getTicket().getTicket()) {
			String status = ticket.getStatus().getStatus();
			if(
				(
					Status.OPEN.equals(status)
					|| Status.HOLD.equals(status)
					|| Status.BOUNCED.equals(status)
				) && brand.equals(ticket.getBrand())
				&& account.equals(ticket.getAccount())
				&& language.equals(ticket.getLanguage())
				&& ticketType.equals(ticket.getTicketType())
				&& ticket.getSummary().equals(summary) // level, prefix, classname, and method
				&& Objects.equals(category, ticket.getCategory())
			) {
				existingTicket = ticket;
				break;
			}
		}
		if(existingTicket != null) {
			existingTicket.addAnnotation(
				generateActionSummary(formatter, record),
				fullReport
			);
		} else {
			// The priority depends on the log level
			String priorityName = getPriorityName(level);
			Priority priority = connector.getTicket().getPriority().get(priorityName);
			if(priority == null) throw new SQLException("Unable to find TicketPriority: " + priorityName);
			connector.getTicket().getTicket().addTicket(
				brand,
				account,
				language,
				category,
				ticketType,
				null,
				summary,
				fullReport,
				priority,
				Collections.emptySet(),
				""
			);
		}
	}

	public static String generateActionSummary(Formatter formatter, LogRecord record) {
		// Generate the annotation summary as localized message + thrown
		StringBuilder tempSB = new StringBuilder();
		String message = formatter.formatMessage(record);
		if(message != null) {
			message = message.trim();
			int eol = message.indexOf('\n');
			boolean doEllipsis = false;
			if(eol != -1) {
				message = message.substring(0, eol).trim();
				doEllipsis = true;
			}
			if(message.length()>0) {
				tempSB.append(message);
				if(doEllipsis) tempSB.append('\u2026');
			}
		}
		Throwable thrown = record.getThrown();
		if(thrown != null) {
			if(tempSB.length() > 0) tempSB.append(" - ");
			String thrownMessage = thrown.getMessage();
			boolean doEllipsis = false;
			if(thrownMessage != null) {
				thrownMessage = thrownMessage.trim();
				int eol = thrownMessage.indexOf('\n');
				if(eol != -1) {
					thrownMessage = thrownMessage.substring(0, eol).trim();
					doEllipsis = true;
				}
			}
			if(thrownMessage != null && thrownMessage.length() > 0) {
				tempSB.append(thrownMessage);
				if(doEllipsis) tempSB.append('\u2026');
			} else {
				tempSB.append(thrown.toString());
			}
		}
		return tempSB.toString();
	}

	/**
	 * Gets the name of a {@link Priority} that corresponds to the given
	 * {@link Level}.
	 */
	public static String getPriorityName(Level level) {
		String priorityName;
		int intLevel = level.intValue();
		if     (intLevel <= Level.CONFIG .intValue()) priorityName = Priority.LOW;    //           level <= CONFIG
		else if(intLevel <= Level.INFO   .intValue()) priorityName = Priority.NORMAL; // CONFIG  < level <= INFO
		else if(intLevel <= Level.WARNING.intValue()) priorityName = Priority.HIGH;   // INFO    < level <= WARNING
		else                                          priorityName = Priority.URGENT; // WARNING < level
		return priorityName;
	}
}
