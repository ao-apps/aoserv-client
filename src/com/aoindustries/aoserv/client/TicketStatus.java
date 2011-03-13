/*
 * Copyright 2001-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;

/**
 * The <code>TicketStatus</code> of a <code>Ticket</code> changes
 * through each step of its life cycle.
 *
 * @see  Ticket
 *
 * @author  AO Industries, Inc.
 */
final public class TicketStatus extends AOServObjectStringKey implements Comparable<TicketStatus>, DtoFactory<com.aoindustries.aoserv.client.dto.TicketStatus> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    /**
     * The different ticket statuses.
     */
    public static final String
        JUNK="junk",
        DELETED="deleted",
        CLOSED="closed",
        BOUNCED="bounced",
        HOLD="hold",
        OPEN="open"
    ;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private short sortOrder;

    public TicketStatus(AOServConnector connector, String status, short sortOrder) {
        super(connector, status);
        this.sortOrder = sortOrder;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(TicketStatus other) {
        return AOServObjectUtils.compare(sortOrder, other.sortOrder);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="status", index=IndexType.PRIMARY_KEY, description="the name of this status")
    public String getStatus() {
        return getKey();
    }

    @SchemaColumn(order=1, name="sort_order", index=IndexType.UNIQUE, description="the default sort ordering")
    public short getSortOrder() {
        return sortOrder;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public TicketStatus(AOServConnector connector, com.aoindustries.aoserv.client.dto.TicketStatus dto) {
        this(connector, dto.getStatus(), dto.getSortOrder());
    }

    @Override
    public com.aoindustries.aoserv.client.dto.TicketStatus getDto() {
        return new com.aoindustries.aoserv.client.dto.TicketStatus(getKey(), sortOrder);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() {
        return ApplicationResources.accessor.getMessage("TicketStatus."+getKey()+".toString");
    }

    /**
     * Localized description.
     */
    public String getDescription() {
        return ApplicationResources.accessor.getMessage("TicketStatus."+getKey()+".description");
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    @DependentObjectSet
    public IndexedSet<TicketAction> getTicketActionsByOldStatus() throws RemoteException {
        return getConnector().getTicketActions().filterIndexed(TicketAction.COLUMN_OLD_STATUS, this);
    }

    @DependentObjectSet
    public IndexedSet<TicketAction> getTicketActionsByNewStatus() throws RemoteException {
        return getConnector().getTicketActions().filterIndexed(TicketAction.COLUMN_NEW_STATUS, this);
    }

    @DependentObjectSet
    public IndexedSet<Ticket> getTickets() throws RemoteException {
        return getConnector().getTickets().filterIndexed(Ticket.COLUMN_STATUS, this);
    }
    // </editor-fold>
}