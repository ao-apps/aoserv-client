/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2009-2012, 2016, 2017, 2018, 2019  AO Industries, Inc.
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

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.reseller.Brand;
import com.aoindustries.aoserv.client.reseller.Category;
import com.aoindustries.net.Email;
import com.aoindustries.util.ErrorPrinter;
import com.aoindustries.util.logging.QueuedHandler;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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

	private static final List<TicketLoggingHandler> handlers = new ArrayList<>();

	/**
	 * Only one TicketLoggingHandler will be created per unique summaryPrefix,
	 * AOServConnector, and category.
	 */
	public static Handler getHandler(String summaryPrefix, AOServConnector connector, Category category) throws IOException, SQLException {
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
	private final Category category;
	private final Account business;
	private final Brand brand;
	private final Language language;
	private final TicketType ticketType;

	private TicketLoggingHandler(String summaryPrefix, final AOServConnector connector, Category category) throws IOException, SQLException {
		super(
			"Console logger for "+connector.toString(),
			"Ticket logger for "+connector.toString()
		);
		this.summaryPrefix = summaryPrefix;
		this.connector = connector;
		this.category = category;
		// Look-up things in advance to reduce possible round-trips during logging
		business = connector.getThisBusinessAdministrator().getUsername().getPackage().getBusiness();
		brand = business.getBrand();
		if(brand==null) throw new SQLException("Unable to find Brand for connector: "+connector);
		language = connector.getTicket().getLanguage().get(Language.EN);
		if(language==null) throw new SQLException("Unable to find Language: "+Language.EN);
		ticketType = connector.getTicket().getTicketType().get(TicketType.LOGS);
		if(ticketType==null) throw new SQLException("Unable to find TicketType: "+TicketType.LOGS);
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
			for(Ticket ticket : connector.getTicket().getTicket()) {
				String status = ticket.getStatus().getStatus();
				if(
					(
						Status.OPEN.equals(status)
						|| Status.HOLD.equals(status)
						|| Status.BOUNCED.equals(status)
					) && brand.equals(ticket.getBrand())
					&& business.equals(ticket.getBusiness())
					&& language.equals(ticket.getLanguage())
					&& ticketType.equals(ticket.getTicketType())
					&& ticket.getSummary().equals(summary) // level, prefix, classname, and method
					&& Objects.equals(category, ticket.getCategory())
				) {
					existingTicket = ticket;
					break;
				}
			}
			if(existingTicket!=null) {
				existingTicket.addAnnotation(
					generateActionSummary(formatter, record),
					fullReport
				);
			} else {
				// The priority depends on the log level
				String priorityName;
				int intLevel = level.intValue();
				if(intLevel<=Level.CONFIG.intValue()) priorityName = Priority.LOW;           // FINE < level <= CONFIG
				else if(intLevel<=Level.INFO.intValue()) priorityName = Priority.NORMAL;     // CONFIG < level <=INFO
				else if(intLevel<=Level.WARNING.intValue()) priorityName = Priority.HIGH;    // INFO < level <=WARNING
				else priorityName = Priority.URGENT;                                         // WARNING < level
				Priority priority = connector.getTicket().getPriority().get(priorityName);
				if(priority==null) throw new SQLException("Unable to find TicketPriority: "+priorityName);
				Set<Email> noContacts = Collections.emptySet();
				connector.getTicket().getTicket().addTicket(
					brand,
					business,
					language,
					category,
					ticketType,
					null,
					summary,
					fullReport,
					priority,
					noContacts,
					""
				);
			}
		} catch(Exception err) {
			ErrorPrinter.printStackTraces(err);
		}
	}

	public static String generateActionSummary(Formatter formatter, LogRecord record) {
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
