/*
 * Copyright 2009-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.UnionSet;
import com.aoindustries.util.WrappedException;
import java.rmi.RemoteException;

/**
 * @see  Ticket
 *
 * @author  AO Industries, Inc.
 */
final public class TicketAssignment extends AOServObjectIntegerKey<TicketAssignment> implements Comparable<TicketAssignment>, DtoFactory<com.aoindustries.aoserv.client.dto.TicketAssignment> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private int ticket;
    private AccountingCode reseller;
    private UserId administrator;

    public TicketAssignment(
        TicketAssignmentService<?,?> service,
        int pkey,
        int ticket,
        AccountingCode reseller,
        UserId administrator
    ) {
        super(service, pkey);
        this.ticket = ticket;
        this.reseller = reseller;
        this.administrator = administrator;
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        reseller = intern(reseller);
        administrator = intern(administrator);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(TicketAssignment other) {
        try {
            int diff = ticket==other.ticket ? 0 : getTicket().compareTo(other.getTicket());
            if(diff!=0) return diff;
            return reseller==other.reseller ? 0 : getReseller().compareTo(other.getReseller());
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
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

    static final String COLUMN_ADMINISTRATOR = "administrator";
    @SchemaColumn(order=3, name=COLUMN_ADMINISTRATOR, index=IndexType.INDEXED, description="the individual that the ticket is assigned to within the reseller")
    public BusinessAdministrator getBusinessAdministrator() throws RemoteException {
        return getService().getConnector().getBusinessAdministrators().get(administrator);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    @Override
    public com.aoindustries.aoserv.client.dto.TicketAssignment getDto() {
        return new com.aoindustries.aoserv.client.dto.TicketAssignment(key, ticket, getDto(reseller), getDto(administrator));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionSet<AOServObject> addDependencies(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getTicket());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getReseller());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getBusinessAdministrator());
        return unionSet;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() {
        return ticket+"|"+key+'|'+reseller+'|'+administrator;
    }
    // </editor-fold>
}
