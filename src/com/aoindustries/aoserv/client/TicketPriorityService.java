package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * @see  TicketPriority
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.ticket_priorities)
public interface TicketPriorityService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends AOServService<C,F,String,TicketPriority> {
}
