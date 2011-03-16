/*
 * Copyright 2001-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.io.FastExternalizable;
import com.aoindustries.io.FastObjectInput;
import com.aoindustries.io.FastObjectOutput;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.WrappedException;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.NoSuchElementException;

/**
 * <code>TicketAction</code>s represent a complete history of the changes that have been made to a ticket.
 * When a ticket is initially created it has no actions.  Any change from its initial state will cause
 * an action to be logged.
 *
 * @see  Ticket
 *
 * @author  AO Industries, Inc.
 */
final public class TicketAction extends AOServObjectIntegerKey implements Comparable<TicketAction>, DtoFactory<com.aoindustries.aoserv.client.dto.TicketAction>, FastExternalizable {

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private int ticket;
    private UserId administrator;
    private long time;
    private String actionType;
    private AccountingCode oldAccounting;
    private AccountingCode newAccounting;
    private String oldPriority;
    private String newPriority;
    private String oldType;
    private String newType;
    private String oldStatus;
    private String newStatus;
    private UserId oldAssignedTo;
    private UserId newAssignedTo;
    private Integer oldCategory;
    private Integer newCategory;
    transient private boolean oldValueLoaded;
    transient private String oldValue;
    transient private boolean newValueLoaded;
    transient private String newValue;
    private Email fromAddress;
    private String summary;
    transient private boolean detailsLoaded;
    transient private String details;
    transient private boolean rawEmailLoaded;
    transient private String rawEmail;

    public TicketAction(
        AOServConnector connector,
        int pkey,
        int ticket,
        UserId administrator,
        long time,
        String actionType,
        AccountingCode oldAccounting,
        AccountingCode newAccounting,
        String oldPriority,
        String newPriority,
        String oldType,
        String newType,
        String oldStatus,
        String newStatus,
        UserId oldAssignedTo,
        UserId newAssignedTo,
        Integer oldCategory,
        Integer newCategory,
        Email fromAddress,
        String summary
    ) {
        super(connector, pkey);
        this.ticket = ticket;
        this.administrator = administrator;
        this.time = time;
        this.actionType = actionType;
        this.oldAccounting = oldAccounting;
        this.newAccounting = newAccounting;
        this.oldPriority = oldPriority;
        this.newPriority = newPriority;
        this.oldType = oldType;
        this.newType = newType;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.oldAssignedTo = oldAssignedTo;
        this.newAssignedTo = newAssignedTo;
        this.oldCategory = oldCategory;
        this.newCategory = newCategory;
        this.fromAddress = fromAddress;
        this.summary = summary;
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        administrator = intern(administrator);
        actionType = intern(actionType);
        oldAccounting = intern(oldAccounting);
        newAccounting = intern(newAccounting);
        oldPriority = intern(oldPriority);
        newPriority = intern(newPriority);
        oldType = intern(oldType);
        newType = intern(newType);
        oldStatus = intern(oldStatus);
        newStatus = intern(newStatus);
        oldAssignedTo = intern(oldAssignedTo);
        newAssignedTo = intern(newAssignedTo);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="FastExternalizable">
    private static final long serialVersionUID = 1131056974029566850L;

    public TicketAction() {
    }

    @Override
    public long getSerialVersionUID() {
        return super.getSerialVersionUID() ^ serialVersionUID;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        FastObjectOutput fastOut = FastObjectOutput.wrap(out);
        try {
            super.writeExternal(fastOut);
            fastOut.writeInt(ticket);
            fastOut.writeFastUTF(administrator==null ? null : administrator.toString());
            fastOut.writeLong(time);
            fastOut.writeFastUTF(actionType);
            fastOut.writeFastUTF(oldAccounting==null ? null : oldAccounting.toString());
            fastOut.writeFastUTF(newAccounting==null ? null : newAccounting.toString());
            fastOut.writeFastUTF(oldPriority);
            fastOut.writeFastUTF(newPriority);
            fastOut.writeFastUTF(oldType);
            fastOut.writeFastUTF(newType);
            fastOut.writeFastUTF(oldStatus);
            fastOut.writeFastUTF(newStatus);
            fastOut.writeFastUTF(oldAssignedTo==null ? null : oldAssignedTo.toString());
            fastOut.writeFastUTF(newAssignedTo==null ? null : newAssignedTo.toString());
            writeNullInteger(fastOut, oldCategory);
            writeNullInteger(fastOut, newCategory);
            if(fromAddress!=null) {
                fastOut.writeBoolean(true);
                fastOut.writeUTF(fromAddress.getLocalPart());
                fastOut.writeUTF(fromAddress.getDomain().toString());
            } else {
                fastOut.writeBoolean(false);
            }
            writeNullUTF(fastOut, summary);
        } finally {
            fastOut.unwrap();
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        FastObjectInput fastIn = FastObjectInput.wrap(in);
        try {
            super.readExternal(fastIn);
            try {
                ticket = fastIn.readInt();
                administrator = UserId.valueOf(fastIn.readFastUTF());
                time = fastIn.readLong();
                actionType = fastIn.readFastUTF();
                oldAccounting = AccountingCode.valueOf(fastIn.readFastUTF());
                newAccounting = AccountingCode.valueOf(fastIn.readFastUTF());
                oldPriority = fastIn.readFastUTF();
                newPriority = fastIn.readFastUTF();
                oldType = fastIn.readFastUTF();
                newType = fastIn.readFastUTF();
                oldStatus = fastIn.readFastUTF();
                newStatus = fastIn.readFastUTF();
                oldAssignedTo = UserId.valueOf(fastIn.readFastUTF());
                newAssignedTo = UserId.valueOf(fastIn.readFastUTF());
                oldCategory = readNullInteger(fastIn);
                newCategory = readNullInteger(fastIn);
                fromAddress = fastIn.readBoolean() ? Email.valueOf(fastIn.readUTF(), DomainName.valueOf(fastIn.readUTF())) : null;
                summary = readNullUTF(fastIn);
                intern();
            } catch(ValidationException exc) {
                throw new IOException(exc);
            }
        } finally {
            fastIn.unwrap();
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(TicketAction other) {
        try {
            int diff = ticket==other.ticket ? 0 : getTicket().compareTo(other.getTicket());
            if(diff!=0) return diff;
            diff = compare(time, other.time);
            if(diff!=0) return diff;
            return compare(getKeyInt(), other.getKeyInt());
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

    public static final MethodColumn COLUMN_TICKET = getMethodColumn(TicketAction.class, "ticket");
    @DependencySingleton
    @SchemaColumn(order=1, index=IndexType.INDEXED, description="the ticket this action is part of")
    public Ticket getTicket() throws RemoteException {
        return getConnector().getTickets().get(ticket);
    }

    public static final MethodColumn COLUMN_ADMINISTRATOR = getMethodColumn(TicketAction.class, "administrator");
    @DependencySingleton
    @SchemaColumn(order=2, index=IndexType.INDEXED, description="the administrator who performed this action")
    public BusinessAdministrator getAdministrator() throws RemoteException {
        if(administrator==null) return null;
        return getConnector().getBusinessAdministrators().get(administrator);
    }

    @SchemaColumn(order=3, description="the time this action was performed")
    public Timestamp getTime() {
        return new Timestamp(time);
    }

    public static final MethodColumn COLUMN_ACTION_TYPE = getMethodColumn(TicketAction.class, "actionType");
    @DependencySingleton
    @SchemaColumn(order=4, index=IndexType.INDEXED, description="the type of action performed")
    public TicketActionType getActionType() throws RemoteException {
        return getConnector().getTicketActionTypes().get(actionType);
    }

    public static final MethodColumn COLUMN_OLD_BUSINESS = getMethodColumn(TicketAction.class, "oldBusiness");
    /**
     * May be filtered.
     */
    @DependencySingleton
    @SchemaColumn(order=5, index=IndexType.INDEXED, description="if changed, contains the old accounting code")
    public Business getOldBusiness() throws RemoteException {
        if(oldAccounting==null) return null;
        return getConnector().getBusinesses().filterUnique(Business.COLUMN_ACCOUNTING, oldAccounting);
    }

    public static final MethodColumn COLUMN_NEW_BUSINESS = getMethodColumn(TicketAction.class, "newBusiness");
    /**
     * May be filtered.
     */
    @DependencySingleton
    @SchemaColumn(order=6, index=IndexType.INDEXED, description="if changed, contains the new accounting code")
    public Business getNewBusiness() throws RemoteException {
        if(oldAccounting==null) return null;
        return getConnector().getBusinesses().get(oldAccounting);
    }

    public static final MethodColumn COLUMN_OLD_PRIORITY = getMethodColumn(TicketAction.class, "oldPriority");
    @DependencySingleton
    @SchemaColumn(order=7, index=IndexType.INDEXED, description="if changed, contains the old priority")
    public TicketPriority getOldPriority() throws RemoteException {
        if(oldPriority==null) return null;
        return getConnector().getTicketPriorities().get(oldPriority);
    }

    public static final MethodColumn COLUMN_NEW_PRIORITY = getMethodColumn(TicketAction.class, "newPriority");
    @DependencySingleton
    @SchemaColumn(order=8, index=IndexType.INDEXED, description="if changed, contains the new priority")
    public TicketPriority getNewPriority() throws RemoteException {
        if(newPriority==null) return null;
        return getConnector().getTicketPriorities().get(newPriority);
    }

    public static final MethodColumn COLUMN_OLD_TYPE = getMethodColumn(TicketAction.class, "oldType");
    @DependencySingleton
    @SchemaColumn(order=9, index=IndexType.INDEXED, description="if changed, contains the old ticket type")
    public TicketType getOldType() throws RemoteException {
        if(oldType==null) return null;
        return getConnector().getTicketTypes().get(oldType);
    }

    public static final MethodColumn COLUMN_NEW_TYPE = getMethodColumn(TicketAction.class, "newType");
    @DependencySingleton
    @SchemaColumn(order=10, index=IndexType.INDEXED, description="if changed, contains the new ticket type")
    public TicketType getNewType() throws RemoteException {
        if(newType==null) return null;
        return getConnector().getTicketTypes().get(newType);
    }

    public static final MethodColumn COLUMN_OLD_STATUS = getMethodColumn(TicketAction.class, "oldStatus");
    @DependencySingleton
    @SchemaColumn(order=11, index=IndexType.INDEXED, description="if changed, contains the old ticket status")
    public TicketStatus getOldStatus() throws RemoteException {
        if(oldStatus==null) return null;
        return getConnector().getTicketStatuses().get(oldStatus);
    }

    public static final MethodColumn COLUMN_NEW_STATUS = getMethodColumn(TicketAction.class, "newStatus");
    @DependencySingleton
    @SchemaColumn(order=12, index=IndexType.INDEXED, description="if changed, contains the new ticket status")
    public TicketStatus getNewStatus() throws RemoteException {
        if(newStatus==null) return null;
        return getConnector().getTicketStatuses().get(newStatus);
    }

    public static final MethodColumn COLUMN_OLD_ASSIGNED_TO = getMethodColumn(TicketAction.class, "oldAssignedTo");
    /**
     * May be filtered.
     */
    @DependencySingleton
    @SchemaColumn(order=13, index=IndexType.INDEXED, description="if changed, contains the old assignment")
    public BusinessAdministrator getOldAssignedTo() throws RemoteException {
        if(oldAssignedTo==null) return null;
        try {
            return getConnector().getBusinessAdministrators().get(oldAssignedTo);
        } catch(NoSuchElementException err) {
            return null;
        }
    }

    public static final MethodColumn COLUMN_NEW_ASSIGNED_TO = getMethodColumn(TicketAction.class, "newAssignedTo");
    /**
     * May be filtered.
     */
    @DependencySingleton
    @SchemaColumn(order=14, index=IndexType.INDEXED, description="if changed, contains the new assignment")
    public BusinessAdministrator getNewAssignedTo() throws RemoteException {
        if(newAssignedTo==null) return null;
        try {
            return getConnector().getBusinessAdministrators().get(newAssignedTo);
        } catch(NoSuchElementException err) {
            return null;
        }
    }

    public static final MethodColumn COLUMN_OLD_CATEGORY = getMethodColumn(TicketAction.class, "oldCategory");
    @DependencySingleton
    @SchemaColumn(order=15, index=IndexType.INDEXED, description="if changed, contains the old category")
    public TicketCategory getOldCategory() throws RemoteException {
        if(oldCategory==null) return null;
        return getConnector().getTicketCategories().get(oldCategory);
    }

    public static final MethodColumn COLUMN_NEW_CATEGORY = getMethodColumn(TicketAction.class, "newCategory");
    @DependencySingleton
    @SchemaColumn(order=16, index=IndexType.INDEXED, description="if changed, contains the new category")
    public TicketCategory getNewCategory() throws RemoteException {
        if(newCategory==null) return null;
        return getConnector().getTicketCategories().get(newCategory);
    }

    /* TODO
    @SchemaColumn(order=17, description="if changed, contains the old value")
    synchronized public String getOldValue() throws RemoteException {
        if(!oldValueLoaded) {
            // Only perform the query for action types that have old values
            if(
                actionType==TicketActionType.SET_CONTACT_EMAILS // OK - interned
                || actionType==TicketActionType.SET_CONTACT_PHONE_NUMBERS // OK - interned
                || actionType==TicketActionType.SET_SUMMARY // OK - interned
                || actionType==TicketActionType.SET_INTERNAL_NOTES // OK - interned
            ) {
                oldValue = getConnector().requestNullLongStringQuery(true, AOServProtocol.CommandID.GET_TICKET_ACTION_OLD_VALUE, pkey);
            } else {
                oldValue = null;
            }
            oldValueLoaded = true;
        }
        return oldValue;
    }
     */

    /* TODO
    @SchemaColumn(order=18, description="if changed, contains the new value")
    synchronized public String getNewValue() throws RemoteException {
        if(!newValueLoaded) {
            // Only perform the query for action types that have new values
            if(
                actionType==TicketActionType.SET_CONTACT_EMAILS // OK - interned
                || actionType==TicketActionType.SET_CONTACT_PHONE_NUMBERS // OK - interned
                || actionType==TicketActionType.SET_SUMMARY // OK - interned
                || actionType==TicketActionType.SET_INTERNAL_NOTES // OK - interned
            ) {
                newValue = getConnector().requestNullLongStringQuery(true, AOServProtocol.CommandID.GET_TICKET_ACTION_NEW_VALUE, pkey);
            } else {
                newValue = null;
            }
            newValueLoaded = true;
        }
        return newValue;
    }
     */

    @SchemaColumn(order=17, description="the from address of the email used to create the action")
    public Email getFromAddress() {
        return fromAddress;
    }

    /**
     * Gets the summary, may be generated for certain action types.
     */
    @SchemaColumn(order=18, description="a summary of the action")
    public String getSummary() throws RemoteException {
        if(summary!=null) return summary;
        final String myOldValue;
        final String myNewValue;
        if(actionType==TicketActionType.SET_BUSINESS) { // OK - interned
            myOldValue = oldAccounting==null ? null : oldAccounting.toString();
            myNewValue = newAccounting==null ? null : newAccounting.toString();
        } else if(
            actionType==TicketActionType.SET_CLIENT_PRIORITY // OK - interned
            || actionType==TicketActionType.SET_ADMIN_PRIORITY // OK - interned
        ) {
            myOldValue = oldPriority;
            myNewValue = newPriority;
        } else if(actionType==TicketActionType.SET_TYPE) { // OK - interned
            myOldValue = getOldType().toStringImpl();
            myNewValue = getNewType().toStringImpl();
        } else if(actionType==TicketActionType.SET_STATUS) { // OK - interned
            myOldValue = getOldStatus().toStringImpl();
            myNewValue = getNewStatus().toStringImpl();
        } else if(actionType==TicketActionType.ASSIGN) { // OK - interned
            BusinessAdministrator myOldAssignedTo = getOldAssignedTo();
            BusinessAdministrator myNewAssignedTo = getNewAssignedTo();
            myOldValue = myOldAssignedTo!=null ? myOldAssignedTo.getName() : myOldAssignedTo!=null ? ApplicationResources.accessor.getMessage("TicketAction.old_assigned_to.filtered") : null;
            myNewValue = myNewAssignedTo!=null ? myNewAssignedTo.getName() : myNewAssignedTo!=null ? ApplicationResources.accessor.getMessage("TicketAction.new_assigned_to.filtered") : null;
        } else if(actionType==TicketActionType.SET_CATEGORY) { // OK - interned
            TicketCategory myOldCategory = getOldCategory();
            TicketCategory myNewCategory = getNewCategory();
            myOldValue = myOldCategory!=null ? myOldCategory.toStringImpl() : null;
            myNewValue = myNewCategory!=null ? myNewCategory.toStringImpl() : null;
        } else if(
            actionType==TicketActionType.SET_CONTACT_EMAILS // OK - interned
            || actionType==TicketActionType.SET_CONTACT_PHONE_NUMBERS // OK - interned
            || actionType==TicketActionType.SET_SUMMARY // OK - interned
            || actionType==TicketActionType.SET_INTERNAL_NOTES // OK - interned
            || actionType==TicketActionType.ADD_ANNOTATION // OK - interned
        ) {
            // These either have no old/new value or their value is not altered in any way
            /* TODO
            oldValue = getOldValue();
            newValue = getNewValue();
             */
            myOldValue = null;
            myNewValue = null;
        } else {
            throw new AssertionError("Unexpected value for action_type: "+actionType);
        }
        return getActionType().generateSummary(getConnector(), myOldValue, myNewValue);
    }

    /* TODO
    @SchemaColumn(order=21, description="the details of the action")
    synchronized public String getDetails() throws RemoteException {
        if(!detailsLoaded) {
            // Only perform the query for action types that have details
            if(actionType==TicketActionType.ADD_ANNOTATION) { // OK - interned
                details = getConnector().requestNullLongStringQuery(true, AOServProtocol.CommandID.GET_TICKET_ACTION_DETAILS, pkey);
            } else {
                details = null;
            }
            detailsLoaded = true;
        }
        return details;
    }
     */

    /* TODO
    @SchemaColumn(order=22, description="the raw email used to create the action")
    synchronized public String getRawEmail() throws RemoteException {
        if(!rawEmailLoaded) {
            // Only perform the query for action types that may have raw email
            if(actionType==TicketActionType.ADD_ANNOTATION) { // OK - interned
                rawEmail = getConnector().requestNullLongStringQuery(true, AOServProtocol.CommandID.GET_TICKET_ACTION_RAW_EMAIL, pkey);
            } else {
                rawEmail = null;
            }
            rawEmailLoaded = true;
        }
        return rawEmail;
    }
     */
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public TicketAction(AOServConnector connector, com.aoindustries.aoserv.client.dto.TicketAction dto) throws ValidationException {
        this(
            connector,
            dto.getPkey(),
            dto.getTicket(),
            getUserId(dto.getAdministrator()),
            getTimeMillis(dto.getTime()),
            dto.getActionType(),
            getAccountingCode(dto.getOldAccounting()),
            getAccountingCode(dto.getNewAccounting()),
            dto.getOldPriority(),
            dto.getNewPriority(),
            dto.getOldType(),
            dto.getNewType(),
            dto.getOldStatus(),
            dto.getNewStatus(),
            getUserId(dto.getOldAssignedTo()),
            getUserId(dto.getNewAssignedTo()),
            dto.getOldCategory(),
            dto.getNewCategory(),
            getEmail(dto.getFromAddress()),
            dto.getSummary()
        );
    }

    @Override
    public com.aoindustries.aoserv.client.dto.TicketAction getDto() {
        return new com.aoindustries.aoserv.client.dto.TicketAction(
            getKeyInt(),
            ticket,
            getDto(administrator),
            time,
            actionType,
            getDto(oldAccounting),
            getDto(newAccounting),
            oldPriority,
            newPriority,
            oldType,
            newType,
            oldStatus,
            newStatus,
            getDto(oldAssignedTo),
            getDto(newAssignedTo),
            oldCategory,
            newCategory,
            getDto(fromAddress),
            summary
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() {
        return ticket+"|"+getKeyInt()+'|'+actionType+'|'+administrator;
    }
    // </editor-fold>
}
