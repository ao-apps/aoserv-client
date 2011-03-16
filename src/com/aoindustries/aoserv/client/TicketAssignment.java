/*
 * Copyright 2009-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.WrappedException;
import java.rmi.RemoteException;

/**
 * @see  Ticket
 *
 * @author  AO Industries, Inc.
 */
final public class TicketAssignment extends AOServObjectIntegerKey implements Comparable<TicketAssignment>, DtoFactory<com.aoindustries.aoserv.client.dto.TicketAssignment> {

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private static final long serialVersionUID = 1426621734817219461L;

    final private int ticket;
    private AccountingCode reseller;
    private UserId administrator;

    public TicketAssignment(
        AOServConnector connector,
        int pkey,
        int ticket,
        AccountingCode reseller,
        UserId administrator
    ) {
        super(connector, pkey);
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
    @SchemaColumn(order=0, index=IndexType.PRIMARY_KEY, description="a generated unique id")
    public int getPkey() {
        return getKeyInt();
    }

    public static final MethodColumn COLUMN_TICKET = getMethodColumn(TicketAssignment.class, "ticket");
    @DependencySingleton
    @SchemaColumn(order=1, index=IndexType.INDEXED, description="the ticket id that is assigned")
    public Ticket getTicket() throws RemoteException {
        return getConnector().getTickets().get(ticket);
    }

    public static final MethodColumn COLUMN_RESELLER = getMethodColumn(TicketAssignment.class, "reseller");
    @DependencySingleton
    @SchemaColumn(order=2, index=IndexType.INDEXED, description="the reseller for the assignment")
    public Reseller getReseller() throws RemoteException {
        return getConnector().getResellers().get(reseller);
    }

    public static final MethodColumn COLUMN_ADMINISTRATOR = getMethodColumn(TicketAssignment.class, "administrator");
    @DependencySingleton
    @SchemaColumn(order=3, index=IndexType.INDEXED, description="the individual that the ticket is assigned to within the reseller")
    public BusinessAdministrator getAdministrator() throws RemoteException {
        return getConnector().getBusinessAdministrators().get(administrator);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public TicketAssignment(AOServConnector connector, com.aoindustries.aoserv.client.dto.TicketAssignment dto) throws ValidationException {
        this(
            connector,
            dto.getPkey(),
            dto.getTicket(),
            getAccountingCode(dto.getReseller()),
            getUserId(dto.getAdministrator())
        );
    }

    @Override
    public com.aoindustries.aoserv.client.dto.TicketAssignment getDto() {
        return new com.aoindustries.aoserv.client.dto.TicketAssignment(getKeyInt(), ticket, getDto(reseller), getDto(administrator));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() {
        return ticket+"|"+getKeyInt()+'|'+reseller+'|'+administrator;
    }
    // </editor-fold>
}
