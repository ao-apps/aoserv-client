/*
 * Copyright 2001-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * @see TicketAction
 * @see Ticket
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.ticket_actions)
public interface TicketActionService extends AOServService<Integer,TicketAction> {
}
