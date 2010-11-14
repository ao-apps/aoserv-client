/*
 * Copyright 2001-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * @see  Ticket
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.tickets)
public interface TicketService extends AOServService<Integer,Ticket> {

    /* TODO
    @Override
    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
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
