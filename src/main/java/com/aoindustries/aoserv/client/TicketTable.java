/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2001-2013, 2016, 2017  AO Industries, Inc.
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
package com.aoindustries.aoserv.client;

import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.util.IntList;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

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
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	public int addTicket(
		final Brand brand,
		final Business business,
		final Language language,
		final TicketCategory category,
		final TicketType ticketType,
		final String fromAddress,
		final String summary,
		final String details,
		final TicketPriority clientPriority,
		final String contactEmails,
		final String contactPhoneNumbers
	) throws IOException, SQLException {
		return connector.requestResult(
			true,
			AOServProtocol.CommandID.ADD,
			new AOServConnector.ResultRequest<Integer>() {
				int pkey;
				IntList invalidateList;

				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(SchemaTable.TableID.TICKETS.ordinal());
					out.writeUTF(brand.pkey.toString());
					out.writeNullUTF(business==null ? null : business.pkey.toString());
					out.writeUTF(language.pkey);
					out.writeCompressedInt(category==null ? -1 : category.pkey);
					out.writeUTF(ticketType.pkey);
					out.writeNullUTF(fromAddress);
					out.writeUTF(summary);
					out.writeNullLongUTF(details);
					out.writeUTF(clientPriority.pkey);
					out.writeUTF(contactEmails);
					out.writeUTF(contactPhoneNumbers);
				}

				@Override
				public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AOServProtocol.DONE) {
						pkey=in.readCompressedInt();
						invalidateList=AOServConnector.readInvalidateList(in);
					} else {
						AOServProtocol.checkResult(code, in);
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
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.TICKETS;
	}

	@Override
	public Ticket get(int pkey) throws IOException, SQLException {
		return getUniqueRow(Ticket.COLUMN_PKEY, pkey);
	}

	List<Ticket> getTickets(Business business) throws IOException, SQLException {
		return getIndexedRows(Ticket.COLUMN_ACCOUNTING, business.pkey);
	}

	List<Ticket> getCreatedTickets(BusinessAdministrator ba) throws IOException, SQLException {
		return getIndexedRows(Ticket.COLUMN_CREATED_BY, ba.pkey);
	}

	/*
	@Override
	boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(AOSHCommand.ADD_TICKET)) {
			if(AOSH.checkParamCount(AOSHCommand.ADD_TICKET, args, 9, err)) {
				int pkey=connector.getSimpleAOClient().addTicket(
					args[1],
					args[2],
					args[3],
					args[4],
					args[5],
					args[6],
					args[7],
					args[8],
					args[9]
				);
				out.println(pkey);
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
