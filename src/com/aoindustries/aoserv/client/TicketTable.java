package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.util.IntList;
import com.aoindustries.util.WrappedException;
import java.io.IOException;
import java.io.InputStream;
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
        new OrderBy(Ticket.COLUMN_PKEY_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    int addTicket(
        Business business,
        BusinessAdministrator businessAdministratorObj,
        String ticket_type,
        String details,
        long deadline,
        String client_priority,
        String admin_priority,
        String technology,
        BusinessAdministrator assigned_to,
        String contact_emails,
        String contact_phone_numbers
    ) throws IOException, SQLException {
        int pkey;
        IntList invalidateList;
        AOServConnection connection=connector.getConnection();
        try {
            CompressedDataOutputStream out=connection.getOutputStream();
            out.writeCompressedInt(AOServProtocol.CommandID.ADD.ordinal());
            out.writeCompressedInt(SchemaTable.TableID.TICKETS.ordinal());
            out.writeBoolean(business!=null); if(business!=null) out.writeUTF(business.pkey);
            out.writeUTF(businessAdministratorObj.pkey);
            out.writeUTF(ticket_type);
            out.writeUTF(details);
            out.writeLong(deadline);
            out.writeUTF(client_priority);
            out.writeUTF(admin_priority==null ? "" : admin_priority);
            out.writeBoolean(technology!=null); if(technology!=null) out.writeUTF(technology);
            out.writeBoolean(assigned_to!=null); if(assigned_to!=null) out.writeUTF(assigned_to.pkey);
            out.writeUTF(contact_emails);
            out.writeUTF(contact_phone_numbers);
            out.flush();

            CompressedDataInputStream in=connection.getInputStream();
            int code=in.readByte();
            if(code==AOServProtocol.DONE) {
                pkey=in.readCompressedInt();
                invalidateList=AOServConnector.readInvalidateList(in);
            } else {
                AOServProtocol.checkResult(code, in);
                throw new IOException("Unexpected response code: "+code);
            }
        } catch(IOException err) {
            connection.close();
            throw err;
        } finally {
            connector.releaseConnection(connection);
        }
        connector.tablesUpdated(invalidateList);
        return pkey;
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.TICKETS;
    }

    public Ticket get(Object pkey) {
        try {
            return getUniqueRow(Ticket.COLUMN_PKEY, pkey);
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public Ticket get(int pkey) throws IOException, SQLException {
        return getUniqueRow(Ticket.COLUMN_PKEY, pkey);
    }

    List<Ticket> getTickets(Business business) throws IOException, SQLException {
        return getIndexedRows(Ticket.COLUMN_ACCOUNTING, business.pkey);
    }

    List<Ticket> getCreatedTickets(BusinessAdministrator ba) throws IOException, SQLException {
        return getIndexedRows(Ticket.COLUMN_CREATED_BY, ba.pkey);
    }

    @Override
    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
        String command=args[0];
        if(command.equalsIgnoreCase(AOSHCommand.ADD_TICKET)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_TICKET, args, 11, err)) {
                int pkey=connector.simpleAOClient.addTicket(
                    args[1],
                    args[2],
                    args[3],
                    args[4],
                    args[5].length()==0?Ticket.NO_DEADLINE:AOSH.parseDate(args[5], "deadline"),
                    args[6],
                    args[7],
                    args[8],
                    args[9].length()==0?null:args[9],
                    args[10],
                    args[11]
                );
                out.println(pkey);
                out.flush();
            }
            return true;
    	} else if(command.equalsIgnoreCase(AOSHCommand.ADD_TICKET_WORK)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_TICKET_WORK, args, 3, err)) {
                connector.simpleAOClient.addTicketWork(
                    AOSH.parseInt(args[1], "ticket_id"),
                    args[2],
                    args[3]
                );
            }
            return true;
    	} else if(command.equalsIgnoreCase(AOSHCommand.BOUNCE_TICKET)) {
            if(AOSH.checkParamCount(AOSHCommand.BOUNCE_TICKET, args, 3, err)) {
                connector.simpleAOClient.bounceTicket(
                    AOSH.parseInt(args[1], "ticket_id"),
                    args[2],
                    args[3]
                );
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.CHANGE_TICKET_ADMIN_PRIORITY)) {
            if(AOSH.checkParamCount(AOSHCommand.CHANGE_TICKET_ADMIN_PRIORITY, args, 4, err)) {
                connector.simpleAOClient.changeTicketAdminPriority(
                    AOSH.parseInt(args[1], "ticket_id"),
                    args[2],
                    args[3],
                    args[4]
                );
            }
            return true;
    	} else if(command.equalsIgnoreCase(AOSHCommand.CHANGE_TICKET_CLIENT_PRIORITY)) {
            if(AOSH.checkParamCount(AOSHCommand.CHANGE_TICKET_CLIENT_PRIORITY, args, 4, err)) {
                connector.simpleAOClient.changeTicketClientPriority(
                    AOSH.parseInt(args[1], "ticket_id"),
                    args[2],
                    args[3],
                    args[4]
                );
            }
            return true;
    	} else if(command.equalsIgnoreCase(AOSHCommand.CHANGE_TICKET_DEADLINE)) {
            if(AOSH.checkParamCount(AOSHCommand.CHANGE_TICKET_DEADLINE, args, 4, err)) {
                connector.simpleAOClient.changeTicketDeadline(
                    AOSH.parseInt(args[1], "ticket_id"),
                    args[2].length()==0?Ticket.NO_DEADLINE:AOSH.parseDate(args[2], "deadline"),
                    args[3],
                    args[4]
                );
            }
            return true;
    	} else if(command.equalsIgnoreCase(AOSHCommand.CHANGE_TICKET_TECHNOLOGY)) {
            if(AOSH.checkParamCount(AOSHCommand.CHANGE_TICKET_TECHNOLOGY, args, 4, err)) {
                connector.simpleAOClient.changeTicketTechnology(
                    AOSH.parseInt(args[1], "ticket_id"),
                    args[2],
                    args[3],
                    args[4]
                );
            }
            return true;
    	} else if(command.equalsIgnoreCase(AOSHCommand.CHANGE_TICKET_TYPE)) {
            if(AOSH.checkParamCount(AOSHCommand.CHANGE_TICKET_TYPE, args, 4, err)) {
                connector.simpleAOClient.changeTicketType(
                    AOSH.parseInt(args[1], "ticket_id"),
                    args[2],
                    args[3],
                    args[4]
                );
            }
            return true;
    	} else if(command.equalsIgnoreCase(AOSHCommand.COMPLETE_TICKET)) {
            if(AOSH.checkParamCount(AOSHCommand.COMPLETE_TICKET, args, 3, err)) {
                connector.simpleAOClient.completeTicket(
                    AOSH.parseInt(args[1], "ticket_id"),
                    args[2],
                    args[3]
                );
            }
            return true;
    	} else if(command.equalsIgnoreCase(AOSHCommand.HOLD_TICKET)) {
            if(AOSH.checkParamCount(AOSHCommand.HOLD_TICKET, args, 2, err)) {
                connector.simpleAOClient.holdTicket(
                    AOSH.parseInt(args[1], "ticket_id"),
                    args[2]
                );
            }
            return true;
    	} else if(command.equalsIgnoreCase(AOSHCommand.KILL_TICKET)) {
            if(AOSH.checkParamCount(AOSHCommand.KILL_TICKET, args, 3, err)) {
                connector.simpleAOClient.killTicket(
                    AOSH.parseInt(args[1], "ticket_id"),
                    args[2],
                    args[3]
                );
            }
            return true;
    	} else if(command.equalsIgnoreCase(AOSHCommand.REACTIVATE_TICKET)) {
            if(AOSH.checkParamCount(AOSHCommand.REACTIVATE_TICKET, args, 3, err)) {
                connector.simpleAOClient.reactivateTicket(
                    AOSH.parseInt(args[1], "ticket_id"),
                    args[2],
                    args[3]
                );
            }
            return true;
        }
        return false;
    }
}
