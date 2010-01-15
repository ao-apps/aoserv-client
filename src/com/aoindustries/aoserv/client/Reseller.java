/*
 * Copyright 2009-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;
import java.util.Set;

/**
 * A reseller may handle support tickets..
 *
 * @see  Business
 * @see  Brand
 *
 * @author  AO Industries, Inc.
 */
final public class Reseller extends AOServObjectAccountingCodeKey<Reseller> implements BeanFactory<com.aoindustries.aoserv.client.beans.Reseller> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private boolean ticketAutoEscalate;

    public Reseller(ResellerService<?,?> service, AccountingCode accounting, boolean ticketAutoEscalate) {
        super(service, accounting);
        this.ticketAutoEscalate = ticketAutoEscalate;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    static final String COLUMN_ACCOUNTING = "accounting";
    @SchemaColumn(order=0, name=COLUMN_ACCOUNTING, index=IndexType.PRIMARY_KEY, description="the brand of this reseller")
    public Brand getBrand() throws RemoteException {
        return getService().getConnector().getBrands().get(key);
    }

    @SchemaColumn(order=1, name="ticket_auto_escalate", description="indicates this reseller does not handle tickets directly and that they are automatically escalated to the parent reseller")
    public boolean getTicketAutoEscalate() {
        return ticketAutoEscalate;
    }

    static final String COLUMN_PARENT = "parent";
    @SchemaColumn(order=2, name=COLUMN_PARENT, index=IndexType.INDEXED, description="the immediate parent of this reseller or <code>null</code> if none available")
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

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.Reseller getBean() {
        return new com.aoindustries.aoserv.client.beans.Reseller(key.getBean(), ticketAutoEscalate);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependencies() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getBrand()
        );
    }

    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getTickets(),
            getTicketAssignments()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public IndexedSet<TicketAssignment> getTicketAssignments() throws RemoteException {
        return getService().getConnector().getTicketAssignments().filterIndexed(TicketAssignment.COLUMN_RESELLER, this);
    }

    /**
     * The children of the reseller are any resellers that have their closest parent
     * business (that is a reseller) equal to this one.
     */
    public IndexedSet<Reseller> getChildResellers() throws RemoteException {
        return getService().filterIndexed(COLUMN_PARENT, this);
    }

    public IndexedSet<Ticket> getTickets() throws RemoteException {
        return getService().getConnector().getTickets().filterIndexed(Ticket.COLUMN_RESELLER, this);
    }
    // </editor-fold>
}
