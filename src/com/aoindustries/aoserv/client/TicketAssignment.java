/*
 * Copyright 2009-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.Set;

/**
 * @see  Ticket
 *
 * @author  AO Industries, Inc.
 */
final public class TicketAssignment extends AOServObjectIntegerKey<TicketAssignment> implements BeanFactory<com.aoindustries.aoserv.client.beans.TicketAssignment> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private int ticket;
    final private AccountingCode reseller;
    final private UserId administrator;

    public TicketAssignment(
        TicketAssignmentService<?,?> service,
        int pkey,
        int ticket,
        AccountingCode reseller,
        UserId administrator
    ) {
        super(service, pkey);
        this.ticket = ticket;
        this.reseller = reseller.intern();
        this.administrator = administrator.intern();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(TicketAssignment other) throws RemoteException {
        int diff = ticket==other.ticket ? 0 : getTicket().compareTo(other.getTicket());
        if(diff!=0) return diff;
        return reseller.equals(other.reseller) ? 0 : getReseller().compareTo(other.getReseller());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="pkey", index=IndexType.PRIMARY_KEY, description="a generated unique id")
    public int getPkey() {
        return key;
    }

    static final String COLUMN_TICKET = "ticket";
    @SchemaColumn(order=1, name=COLUMN_TICKET, index=IndexType.INDEXED, description="the ticket id that is assigned")
    public Ticket getTicket() throws RemoteException {
        return getService().getConnector().getTickets().get(ticket);
    }

    static final String COLUMN_RESELLER = "reseller";
    @SchemaColumn(order=2, name=COLUMN_RESELLER, index=IndexType.INDEXED, description="the reseller for the assignment")
    public Reseller getReseller() throws RemoteException {
        return getService().getConnector().getResellers().get(reseller);
    }

    @SchemaColumn(order=3, name="administrator", description="the individual that the ticket is assigned to within the reseller")
    public BusinessAdministrator getBusinessAdministrator() throws RemoteException {
        return getService().getConnector().getBusinessAdministrators().get(administrator);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.TicketAssignment getBean() {
        return new com.aoindustries.aoserv.client.beans.TicketAssignment(key, ticket, reseller.getBean(), administrator.getBean());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependencies() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getTicket(),
            getReseller(),
            getBusinessAdministrator()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl(Locale userLocale) {
        return ticket+"|"+key+'|'+reseller+'|'+administrator;
    }
    // </editor-fold>
}
