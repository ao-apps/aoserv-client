/*
 * Copyright 2001-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import com.aoindustries.util.UnionSet;
import java.rmi.RemoteException;

/**
 * The <code>TicketStatus</code> of a <code>Ticket</code> changes
 * through each step of its life cycle.
 *
 * @see  Ticket
 *
 * @author  AO Industries, Inc.
 */
final public class TicketStatus extends AOServObjectStringKey<TicketStatus> implements Comparable<TicketStatus>, DtoFactory<com.aoindustries.aoserv.client.dto.TicketStatus> {

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

    public TicketStatus(TicketStatusService<?,?> table, String status, short sortOrder) {
        super(table, status);
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
    @Override
    public com.aoindustries.aoserv.client.dto.TicketStatus getDto() {
        return new com.aoindustries.aoserv.client.dto.TicketStatus(getKey(), sortOrder);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionSet<AOServObject> addDependentObjects(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getTicketActionsByOldStatus());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getTicketActionsByNewStatus());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getTickets());
        return unionSet;
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
    public IndexedSet<TicketAction> getTicketActionsByOldStatus() throws RemoteException {
        return getService().getConnector().getTicketActions().filterIndexed(TicketAction.COLUMN_OLD_STATUS, this);
    }

    public IndexedSet<TicketAction> getTicketActionsByNewStatus() throws RemoteException {
        return getService().getConnector().getTicketActions().filterIndexed(TicketAction.COLUMN_NEW_STATUS, this);
    }

    public IndexedSet<Ticket> getTickets() throws RemoteException {
        return getService().getConnector().getTickets().filterIndexed(Ticket.COLUMN_STATUS, this);
    }
    // </editor-fold>
}