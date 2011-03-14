/*
 * Copyright 2001-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;

/**
 * <code>Ticket</code>s are prioritized by both the client and
 * support personnel.  Each priority is set as a <code>TicketPriority</code>.
 *
 * @see  Ticket
 *
 * @author  AO Industries, Inc.
 */
final public class TicketPriority extends AOServObjectStringKey implements Comparable<TicketPriority>, DtoFactory<com.aoindustries.aoserv.client.dto.TicketPriority> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    /**
     * The possible ticket priorities.
     */
    public static final String
        LOW="0-Low",
        NORMAL="1-Normal",
        HIGH="2-High",
        URGENT="3-Urgent"
    ;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private static final long serialVersionUID = 3068599948005442526L;

    public TicketPriority(AOServConnector connector, String priority) {
        super(connector, priority);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(TicketPriority other) {
        return compareIgnoreCaseConsistentWithEquals(getKey(), other.getKey());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, index=IndexType.PRIMARY_KEY, description="the unique priority")
    public String getPriority() {
    	return getKey();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public TicketPriority(AOServConnector connector, com.aoindustries.aoserv.client.dto.TicketPriority dto) {
        this(connector, dto.getPriority());
    }

    @Override
    public com.aoindustries.aoserv.client.dto.TicketPriority getDto() {
        return new com.aoindustries.aoserv.client.dto.TicketPriority(getKey());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    @DependentObjectSet
    public IndexedSet<TicketAction> getTicketActionsByOldPriority() throws RemoteException {
        return getConnector().getTicketActions().filterIndexed(TicketAction.COLUMN_OLD_PRIORITY, this);
    }

    @DependentObjectSet
    public IndexedSet<TicketAction> getTicketActionsByNewPriority() throws RemoteException {
        return getConnector().getTicketActions().filterIndexed(TicketAction.COLUMN_NEW_PRIORITY, this);
    }

    @DependentObjectSet
    public IndexedSet<Ticket> getTicketsByClientPriority() throws RemoteException {
        return getConnector().getTickets().filterIndexed(Ticket.COLUMN_CLIENT_PRIORITY, this);
    }

    @DependentObjectSet
    public IndexedSet<Ticket> getTicketsByAdminPriority() throws RemoteException {
        return getConnector().getTickets().filterIndexed(Ticket.COLUMN_ADMIN_PRIORITY, this);
    }
    // </editor-fold>
}
