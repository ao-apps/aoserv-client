/*
 * Copyright 2009-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * @see TicketAssignment
 * @see Ticket
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.ticket_assignments)
public interface TicketAssignmentService extends AOServService<Integer,TicketAssignment> {
}
