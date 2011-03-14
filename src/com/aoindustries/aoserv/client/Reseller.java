/*
 * Copyright 2009-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;

/**
 * A reseller may handle support tickets..
 *
 * @see  Business
 * @see  Brand
 *
 * @author  AO Industries, Inc.
 */
final public class Reseller extends AOServObjectAccountingCodeKey implements Comparable<Reseller>, DtoFactory<com.aoindustries.aoserv.client.dto.Reseller> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    // TODO: private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private boolean ticketAutoEscalate;

    public Reseller(AOServConnector connector, AccountingCode accounting, boolean ticketAutoEscalate) {
        super(connector, accounting);
        this.ticketAutoEscalate = ticketAutoEscalate;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(Reseller other) {
        return getKey().compareTo(other.getKey());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    public static final MethodColumn COLUMN_BRAND = getMethodColumn(Reseller.class, "brand");
    @DependencySingleton
    @SchemaColumn(order=0, index=IndexType.PRIMARY_KEY, description="the brand of this reseller")
    public Brand getBrand() throws RemoteException {
        return getConnector().getBrands().get(getKey());
    }

    @SchemaColumn(order=1, description="indicates this reseller does not handle tickets directly and that they are automatically escalated to the parent reseller")
    public boolean getTicketAutoEscalate() {
        return ticketAutoEscalate;
    }

    public static final MethodColumn COLUMN_PARENT_RESELLER = getMethodColumn(Reseller.class, "parentReseller");
    @SchemaColumn(order=2, index=IndexType.INDEXED, description="the immediate parent of this reseller or <code>null</code> if none available")
    public Reseller getParentReseller() throws RemoteException {
        Business bu = getBrand().getBusiness();
        if(bu==null) return null;
        Business parent = bu.getParentBusiness();
        while(parent!=null) {
            Brand parentBrand = parent.getBrand();
            if(parentBrand!=null) {
                Reseller parentReseller = parentBrand.getReseller();
                if(parentReseller!=null) return parentReseller;
            }
        }
        return null;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public Reseller(AOServConnector connector, com.aoindustries.aoserv.client.dto.Reseller dto) throws ValidationException {
        this(
            connector,
            getAccountingCode(dto.getAccounting()),
            dto.isTicketAutoEscalate()
        );
    }

    @Override
    public com.aoindustries.aoserv.client.dto.Reseller getDto() {
        return new com.aoindustries.aoserv.client.dto.Reseller(getDto(getKey()), ticketAutoEscalate);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    @DependentObjectSet
    public IndexedSet<TicketAssignment> getTicketAssignments() throws RemoteException {
        return getConnector().getTicketAssignments().filterIndexed(TicketAssignment.COLUMN_RESELLER, this);
    }

    /**
     * The children of the reseller are any resellers that have their closest parent
     * business (that is a reseller) equal to this one.
     */
    public IndexedSet<Reseller> getChildResellers() throws RemoteException {
        return getConnector().getResellers().filterIndexed(COLUMN_PARENT_RESELLER, this);
    }

    @DependentObjectSet
    public IndexedSet<Ticket> getTickets() throws RemoteException {
        return getConnector().getTickets().filterIndexed(Ticket.COLUMN_RESELLER, this);
    }
    // </editor-fold>
}
