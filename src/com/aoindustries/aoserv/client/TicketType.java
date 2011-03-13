/*
 * Copyright 2001-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import com.aoindustries.util.UnionMethodSet;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Each <code>Ticket</code> is of a specific <code>TicketType</code>.
 *
 * @see  Ticket
 *
 * @author  AO Industries, Inc.
 */
final public class TicketType extends AOServObjectStringKey implements Comparable<TicketType>, DtoFactory<com.aoindustries.aoserv.client.dto.TicketType> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    /**
     * The types of <code>Ticket</code>s.
     */
    public static final String
        CONTACT="contact",
        LOGS="logs",
        SUPPORT="support",
        PROJECTS="projects",
        INTERNAL="internal"
    ;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    public TicketType(AOServConnector connector, String type) {
        super(connector, type);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(TicketType other) {
        return AOServObjectUtils.compareIgnoreCaseConsistentWithEquals(getKey(), other.getKey());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="type", index=IndexType.PRIMARY_KEY, description="the unique type name")
    public String getType() {
        return getKey();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public TicketType(AOServConnector connector, com.aoindustries.aoserv.client.dto.TicketType dto) {
        this(connector, dto.getType());
    }

    @Override
    public com.aoindustries.aoserv.client.dto.TicketType getDto() {
        return new com.aoindustries.aoserv.client.dto.TicketType(getKey());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    private static final Map<Class<? extends AOServObject<?>>, ? extends List<? extends UnionMethodSet.Method<? extends AOServObject<?>>>> getDependentObjectsMethods
         = getDependentObjectsMethods(TicketType.class);

    @Override
    @SuppressWarnings("unchecked")
    public Set<? extends AOServObject<?>> getDependentObjects() throws RemoteException {
        return new UnionMethodSet<AOServObject<?>>(this, (Class)AOServObject.class, getDependentObjectsMethods);
    }

    /*
    @Override
    protected UnionClassSet<AOServObject<?>> addDependentObjects(UnionClassSet<AOServObject<?>> unionSet) throws RemoteException {
        unionSet = super.addDependentObjects(null);

        UnionSet<TicketAction> ticketActions = null;
        ticketActions = AOServObjectUtils.addDependencyUnionSet(ticketActions, getTicketActionsByOldType());
        ticketActions = AOServObjectUtils.addDependencyUnionSet(ticketActions, getTicketActionsByNewType());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, ticketActions);

        unionSet = AOServObjectUtils.addDependencySet(unionSet, getTickets());
        return unionSet;
    }
     */
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() {
        return ApplicationResources.accessor.getMessage("TicketType."+getKey()+".toString");
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    @DependentObjectSet
    public IndexedSet<TicketAction> getTicketActionsByOldType() throws RemoteException {
        return getConnector().getTicketActions().filterIndexed(TicketAction.COLUMN_OLD_TYPE, this);
    }

    @DependentObjectSet
    public IndexedSet<TicketAction> getTicketActionsByNewType() throws RemoteException {
        return getConnector().getTicketActions().filterIndexed(TicketAction.COLUMN_NEW_TYPE, this);
    }

    @DependentObjectSet
    public IndexedSet<Ticket> getTickets() throws RemoteException {
        return getConnector().getTickets().filterIndexed(Ticket.COLUMN_TICKET_TYPE, this);
    }
    // </editor-fold>
}
