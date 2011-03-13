/*
 * Copyright 2001-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * The table containing all of the possible types of actions that may
 * be performed on a ticket.
 *
 * @see TicketAction
 * @see Ticket
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.ticket_action_types)
public interface TicketActionTypeService extends AOServService<String,TicketActionType> {
}