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
 * <code>Ticket</code>s are prioritized by both the client and
 * support personnel.  Each priority is set as a <code>TicketPriority</code>.
 *
 * @see  Ticket
 *
 * @author  AO Industries, Inc.
 */
final public class TicketPriority extends AOServObjectStringKey<TicketPriority> implements DtoFactory<com.aoindustries.aoserv.client.dto.TicketPriority> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

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
    public TicketPriority(TicketPriorityService<?,?> table, String priority) {
        super(table, priority);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="priority", index=IndexType.PRIMARY_KEY, description="the unique priority")
    public String getPriority() {
    	return getKey();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    @Override
    public com.aoindustries.aoserv.client.dto.TicketPriority getDto() {
        return new com.aoindustries.aoserv.client.dto.TicketPriority(getKey());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionSet<AOServObject> addDependentObjects(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getTicketActionsByOldPriority());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getTicketActionsByNewPriority());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getTicketsByClientPriority());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getTicketsByAdminPriority());
        return unionSet;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public IndexedSet<TicketAction> getTicketActionsByOldPriority() throws RemoteException {
        return getService().getConnector().getTicketActions().filterIndexed(TicketAction.COLUMN_OLD_PRIORITY, this);
    }

    public IndexedSet<TicketAction> getTicketActionsByNewPriority() throws RemoteException {
        return getService().getConnector().getTicketActions().filterIndexed(TicketAction.COLUMN_NEW_PRIORITY, this);
    }

    public IndexedSet<Ticket> getTicketsByClientPriority() throws RemoteException {
        return getService().getConnector().getTickets().filterIndexed(Ticket.COLUMN_CLIENT_PRIORITY, this);
    }

    public IndexedSet<Ticket> getTicketsByAdminPriority() throws RemoteException {
        return getService().getConnector().getTickets().filterIndexed(Ticket.COLUMN_ADMIN_PRIORITY, this);
    }
    // </editor-fold>
}
