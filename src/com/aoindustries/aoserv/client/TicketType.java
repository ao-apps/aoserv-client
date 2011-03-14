/*
 * Copyright 2001-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;

/**
 * Each <code>Ticket</code> is of a specific <code>TicketType</code>.
 *
 * @see  Ticket
 *
 * @author  AO Industries, Inc.
 */
final public class TicketType extends AOServObjectStringKey implements Comparable<TicketType>, DtoFactory<com.aoindustries.aoserv.client.dto.TicketType> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
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
    private static final long serialVersionUID = -2716897618625919303L;

    public TicketType(AOServConnector connector, String type) {
        super(connector, type);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(TicketType other) {
        return compareIgnoreCaseConsistentWithEquals(getKey(), other.getKey());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, index=IndexType.PRIMARY_KEY, description="the unique type name")
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
