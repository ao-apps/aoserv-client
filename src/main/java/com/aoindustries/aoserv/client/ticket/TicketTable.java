/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018, 2019  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.account.Administrator;
import com.aoindustries.aoserv.client.reseller.Brand;
import com.aoindustries.aoserv.client.reseller.Category;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.net.Email;
import com.aoindustries.util.IntList;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @see  Ticket
 *
 * @author  AO Industries, Inc.
 */
final public class TicketTable extends CachedTableIntegerKey<Ticket> {

	TicketTable(AOServConnector connector) {
		super(connector, Ticket.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(Ticket.COLUMN_OPEN_DATE_name, DESCENDING),
		new OrderBy(Ticket.COLUMN_PKEY_name, DESCENDING)
	};
	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	public int addTicket(
		final Brand brand,
		final Account business,
		final Language language,
		final Category category,
		final TicketType ticketType,
		final Email fromAddress,
		final String summary,
		final String details,
		final Priority clientPriority,
		final Set<Email> contactEmails,
		final String contactPhoneNumbers
	) throws IOException, SQLException {
		return connector.requestResult(true,
			AoservProtocol.CommandID.ADD,
			new AOServConnector.ResultRequest<Integer>() {
				int pkey;
				IntList invalidateList;

				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(Table.TableID.TICKETS.ordinal());
					out.writeUTF(brand.getBusiness_accounting().toString());
					out.writeNullUTF(business==null ? null : business.getName().toString());
					out.writeUTF(language.getCode());
					out.writeCompressedInt(category==null ? -1 : category.getPkey());
					out.writeUTF(ticketType.getType());
					out.writeNullUTF(Objects.toString(fromAddress, null));
					out.writeUTF(summary);
					out.writeNullLongUTF(details);
					out.writeUTF(clientPriority.getPriority());
					int size = contactEmails.size();
					out.writeCompressedInt(size);
					for(Email email : contactEmails) out.writeUTF(email.toString());
					out.writeUTF(contactPhoneNumbers);
				}

				@Override
				public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AoservProtocol.DONE) {
						pkey=in.readCompressedInt();
						invalidateList=AOServConnector.readInvalidateList(in);
					} else {
						AoservProtocol.checkResult(code, in);
						throw new IOException("Unexpected response code: "+code);
					}
				}

				@Override
				public Integer afterRelease() {
					connector.tablesUpdated(invalidateList);
					return pkey;
				}
			}
		);
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.TICKETS;
	}

	@Override
	public Ticket get(int pkey) throws IOException, SQLException {
		return getUniqueRow(Ticket.COLUMN_PKEY, pkey);
	}

	public List<Ticket> getTickets(Account business) throws IOException, SQLException {
		return getIndexedRows(Ticket.COLUMN_ACCOUNTING, business.getName());
	}

	public List<Ticket> getCreatedTickets(Administrator ba) throws IOException, SQLException {
		return getIndexedRows(Ticket.COLUMN_CREATED_BY, ba.getUsername_userId());
	}

	/*
	@Override
	public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(AOSHCommand.ADD_TICKET)) {
			if(AOSH.checkParamCount(AOSHCommand.ADD_TICKET, args, 9, err)) {
				out.println(
					connector.getSimpleAOClient().addTicket(
						args[1],
						args[2],
						args[3],
						args[4],
						args[5],
						args[6],
						args[7],
						args[8],
						args[9]
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.ADD_TICKET_WORK)) {
			if(AOSH.checkParamCount(AOSHCommand.ADD_TICKET_WORK, args, 3, err)) {
				connector.getSimpleAOClient().addTicketWork(
					AOSH.parseInt(args[1], "ticket_id"),
					args[2],
					args[3]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.BOUNCE_TICKET)) {
			if(AOSH.checkParamCount(AOSHCommand.BOUNCE_TICKET, args, 3, err)) {
				connector.getSimpleAOClient().bounceTicket(
					AOSH.parseInt(args[1], "ticket_id"),
					args[2],
					args[3]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.CHANGE_TICKET_ADMIN_PRIORITY)) {
			if(AOSH.checkParamCount(AOSHCommand.CHANGE_TICKET_ADMIN_PRIORITY, args, 4, err)) {
				connector.getSimpleAOClient().changeTicketAdminPriority(
					AOSH.parseInt(args[1], "ticket_id"),
					args[2],
					args[3],
					args[4]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.CHANGE_TICKET_CLIENT_PRIORITY)) {
			if(AOSH.checkParamCount(AOSHCommand.CHANGE_TICKET_CLIENT_PRIORITY, args, 4, err)) {
				connector.getSimpleAOClient().changeTicketClientPriority(
					AOSH.parseInt(args[1], "ticket_id"),
					args[2],
					args[3],
					args[4]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.CHANGE_TICKET_TYPE)) {
			if(AOSH.checkParamCount(AOSHCommand.CHANGE_TICKET_TYPE, args, 4, err)) {
				connector.getSimpleAOClient().changeTicketType(
					AOSH.parseInt(args[1], "ticket_id"),
					args[2],
					args[3],
					args[4]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.COMPLETE_TICKET)) {
			if(AOSH.checkParamCount(AOSHCommand.COMPLETE_TICKET, args, 3, err)) {
				connector.getSimpleAOClient().completeTicket(
					AOSH.parseInt(args[1], "ticket_id"),
					args[2],
					args[3]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.HOLD_TICKET)) {
			if(AOSH.checkParamCount(AOSHCommand.HOLD_TICKET, args, 2, err)) {
				connector.getSimpleAOClient().holdTicket(
					AOSH.parseInt(args[1], "ticket_id"),
					args[2]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.KILL_TICKET)) {
			if(AOSH.checkParamCount(AOSHCommand.KILL_TICKET, args, 3, err)) {
				connector.getSimpleAOClient().killTicket(
					AOSH.parseInt(args[1], "ticket_id"),
					args[2],
					args[3]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.REACTIVATE_TICKET)) {
			if(AOSH.checkParamCount(AOSHCommand.REACTIVATE_TICKET, args, 3, err)) {
				connector.getSimpleAOClient().reactivateTicket(
					AOSH.parseInt(args[1], "ticket_id"),
					args[2],
					args[3]
				);
			}
			return true;
		}
		return false;
	}*/
}
