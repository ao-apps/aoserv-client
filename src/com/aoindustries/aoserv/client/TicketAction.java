/*
 * Copyright 2001-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.aoserv.client.validator.Email;
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Locale;
import java.util.Set;

/**
 * <code>TicketAction</code>s represent a complete history of the changes that have been made to a ticket.
 * When a ticket is initially created it has no actions.  Any change from its initial state will cause
 * an action to be logged.
 *
 * @see  Ticket
 *
 * @author  AO Industries, Inc.
 */
final public class TicketAction extends AOServObjectIntegerKey<TicketAction> implements BeanFactory<com.aoindustries.aoserv.client.beans.TicketAction> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private int ticket;
    private UserId administrator;
    final private Timestamp time;
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
    final private Integer oldCategory;
    final private Integer newCategory;
    transient private boolean oldValueLoaded;
    transient private String oldValue;
    transient private boolean newValueLoaded;
    transient private String newValue;
    final private Email fromAddress;
    final private String summary;
    transient private boolean detailsLoaded;
    transient private String details;
    transient private boolean rawEmailLoaded;
    transient private String rawEmail;

    public TicketAction(
        TicketActionService<?,?> service,
        int pkey,
        int ticket,
        UserId administrator,
        Timestamp time,
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
        super(service, pkey);
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

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(TicketAction other) throws RemoteException {
        int diff = ticket==other.ticket ? 0 : getTicket().compareTo(other.getTicket());
        if(diff!=0) return diff;
        diff = time.compareTo(other.time);
        if(diff!=0) return diff;
        return AOServObjectUtils.compare(key, other.key);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="pkey", index=IndexType.PRIMARY_KEY, description="a generated unique id")
    public int getPkey() {
        return key;
    }

    static final String COLUMN_TICKET = "ticket";
    @SchemaColumn(order=1, name=COLUMN_TICKET, index=IndexType.INDEXED, description="the ticket this action is part of")
    public Ticket getTicket() throws RemoteException {
        return getService().getConnector().getTickets().get(ticket);
    }

    static final String COLUMN_ADMINISTRATOR = "administrator";
    @SchemaColumn(order=2, name=COLUMN_ADMINISTRATOR, index=IndexType.INDEXED, description="the administrator who performed this action")
    public BusinessAdministrator getAdministrator() throws RemoteException {
        if(administrator==null) return null;
        return getService().getConnector().getBusinessAdministrators().get(administrator);
    }

    @SchemaColumn(order=3, name="time", description="the time this action was performed")
    public Timestamp getTime() {
        return time;
    }

    static final String COLUMN_ACTION_TYPE = "action_type";
    @SchemaColumn(order=4, name=COLUMN_ACTION_TYPE, index=IndexType.INDEXED, description="the type of action performed")
    public TicketActionType getActionType() throws RemoteException {
        return getService().getConnector().getTicketActionTypes().get(actionType);
    }

    static final String COLUMN_OLD_ACCOUNTING = "old_accounting";
    /**
     * May be filtered.
     */
    @SchemaColumn(order=5, name=COLUMN_OLD_ACCOUNTING, index=IndexType.INDEXED, description="if changed, contains the old accounting code")
    public Business getOldBusiness() throws RemoteException {
        if(oldAccounting==null) return null;
        return getService().getConnector().getBusinesses().filterUnique(Business.COLUMN_ACCOUNTING, oldAccounting);
    }

    static final String COLUMN_NEW_ACCOUNTING = "new_accounting";
    /**
     * May be filtered.
     */
    @SchemaColumn(order=6, name=COLUMN_NEW_ACCOUNTING, index=IndexType.INDEXED, description="if changed, contains the new accounting code")
    public Business getNewBusiness() throws RemoteException {
        if(oldAccounting==null) return null;
        return getService().getConnector().getBusinesses().get(oldAccounting);
    }

    static final String COLUMN_OLD_PRIORITY = "old_priority";
    @SchemaColumn(order=7, name=COLUMN_OLD_PRIORITY, index=IndexType.INDEXED, description="if changed, contains the old priority")
    public TicketPriority getOldPriority() throws RemoteException {
        if(oldPriority==null) return null;
        return getService().getConnector().getTicketPriorities().get(oldPriority);
    }

    static final String COLUMN_NEW_PRIORITY = "new_priority";
    @SchemaColumn(order=8, name=COLUMN_NEW_PRIORITY, index=IndexType.INDEXED, description="if changed, contains the new priority")
    public TicketPriority getNewPriority() throws RemoteException {
        if(newPriority==null) return null;
        return getService().getConnector().getTicketPriorities().get(newPriority);
    }

    static final String COLUMN_OLD_TYPE = "old_type";
    @SchemaColumn(order=9, name=COLUMN_OLD_TYPE, index=IndexType.INDEXED, description="if changed, contains the old ticket type")
    public TicketType getOldType() throws RemoteException {
        if(oldType==null) return null;
        return getService().getConnector().getTicketTypes().get(oldType);
    }

    static final String COLUMN_NEW_TYPE = "new_type";
    @SchemaColumn(order=10, name=COLUMN_NEW_TYPE, index=IndexType.INDEXED, description="if changed, contains the new ticket type")
    public TicketType getNewType() throws RemoteException {
        if(newType==null) return null;
        return getService().getConnector().getTicketTypes().get(newType);
    }

    static final String COLUMN_OLD_STATUS = "old_status";
    @SchemaColumn(order=11, name=COLUMN_OLD_STATUS, index=IndexType.INDEXED, description="if changed, contains the old ticket status")
    public TicketStatus getOldStatus() throws RemoteException {
        if(oldStatus==null) return null;
        return getService().getConnector().getTicketStatuses().get(oldStatus);
    }

    static final String COLUMN_NEW_STATUS = "new_status";
    @SchemaColumn(order=12, name=COLUMN_NEW_STATUS, index=IndexType.INDEXED, description="if changed, contains the new ticket status")
    public TicketStatus getNewStatus() throws RemoteException {
        if(newStatus==null) return null;
        return getService().getConnector().getTicketStatuses().get(newStatus);
    }

    static final String COLUMN_OLD_ASSIGNED_TO = "old_assigned_to";
    /**
     * May be filtered.
     */
    @SchemaColumn(order=13, name=COLUMN_OLD_ASSIGNED_TO, index=IndexType.INDEXED, description="if changed, contains the old assignment")
    public BusinessAdministrator getOldAssignedTo() throws RemoteException {
        if(oldAssignedTo==null) return null;
        return getService().getConnector().getBusinessAdministrators().filterUnique(BusinessAdministrator.COLUMN_USERNAME, oldAssignedTo);
    }

    static final String COLUMN_NEW_ASSIGNED_TO = "new_assigned_to";
    /**
     * May be filtered.
     */
    @SchemaColumn(order=14, name=COLUMN_NEW_ASSIGNED_TO, index=IndexType.INDEXED, description="if changed, contains the new assignment")
    public BusinessAdministrator getNewAssignedTo() throws RemoteException {
        if(newAssignedTo==null) return null;
        return getService().getConnector().getBusinessAdministrators().filterUnique(BusinessAdministrator.COLUMN_USERNAME, newAssignedTo);
    }

    static final String COLUMN_OLD_CATEGORY = "old_category";
    @SchemaColumn(order=15, name=COLUMN_OLD_CATEGORY, index=IndexType.INDEXED, description="if changed, contains the old category")
    public TicketCategory getOldCategory() throws RemoteException {
        if(oldCategory==null) return null;
        return getService().getConnector().getTicketCategories().get(oldCategory);
    }

    static final String COLUMN_NEW_CATEGORY = "new_category";
    @SchemaColumn(order=16, name=COLUMN_NEW_CATEGORY, index=IndexType.INDEXED, description="if changed, contains the new category")
    public TicketCategory getNewCategory() throws RemoteException {
        if(newCategory==null) return null;
        return getService().getConnector().getTicketCategories().get(newCategory);
    }

    /* TODO
    @SchemaColumn(order=17, name="old_value", description="if changed, contains the old value")
    synchronized public String getOldValue() throws RemoteException {
        if(!oldValueLoaded) {
            // Only perform the query for action types that have old values
            if(
                actionType.equals(TicketActionType.SET_CONTACT_EMAILS)
                || actionType.equals(TicketActionType.SET_CONTACT_PHONE_NUMBERS)
                || actionType.equals(TicketActionType.SET_SUMMARY)
                || actionType.equals(TicketActionType.SET_INTERNAL_NOTES)
            ) {
                oldValue = getService().getConnector().requestNullLongStringQuery(true, AOServProtocol.CommandID.GET_TICKET_ACTION_OLD_VALUE, pkey);
            } else {
                oldValue = null;
            }
            oldValueLoaded = true;
        }
        return oldValue;
    }
     */

    /* TODO
    @SchemaColumn(order=18, name="new_value", description="if changed, contains the new value")
    synchronized public String getNewValue() throws RemoteException {
        if(!newValueLoaded) {
            // Only perform the query for action types that have new values
            if(
                actionType.equals(TicketActionType.SET_CONTACT_EMAILS)
                || actionType.equals(TicketActionType.SET_CONTACT_PHONE_NUMBERS)
                || actionType.equals(TicketActionType.SET_SUMMARY)
                || actionType.equals(TicketActionType.SET_INTERNAL_NOTES)
            ) {
                newValue = getService().getConnector().requestNullLongStringQuery(true, AOServProtocol.CommandID.GET_TICKET_ACTION_NEW_VALUE, pkey);
            } else {
                newValue = null;
            }
            newValueLoaded = true;
        }
        return newValue;
    }
     */

    @SchemaColumn(order=17, name="from_address", description="the from address of the email used to create the action")
    public Email getFromAddress() {
        return fromAddress;
    }

    /**
     * Gets the summary for the Locale of the connector, may be generated for certain action types.
     */
    @SchemaColumn(order=18, name="summary", description="a summary of the action")
    public String getSummary() throws RemoteException {
        Locale userLocale = getService().getConnector().getLocale();
        if(summary!=null) return summary;
        final String myOldValue;
        final String myNewValue;
        if(actionType.equals(TicketActionType.SET_BUSINESS)) {
            myOldValue = oldAccounting==null ? null : oldAccounting.getAccounting();
            myNewValue = newAccounting==null ? null : newAccounting.getAccounting();
        } else if(
            actionType.equals(TicketActionType.SET_CLIENT_PRIORITY)
            || actionType.equals(TicketActionType.SET_ADMIN_PRIORITY)
        ) {
            myOldValue = oldPriority;
            myNewValue = newPriority;
        } else if(actionType.equals(TicketActionType.SET_TYPE)) {
            myOldValue = getOldType().toStringImpl(userLocale);
            myNewValue = getNewType().toStringImpl(userLocale);
        } else if(actionType.equals(TicketActionType.SET_STATUS)) {
            myOldValue = getOldStatus().toStringImpl(userLocale);
            myNewValue = getNewStatus().toStringImpl(userLocale);
        } else if(actionType.equals(TicketActionType.ASSIGN)) {
            BusinessAdministrator myOldAssignedTo = getOldAssignedTo();
            BusinessAdministrator myNewAssignedTo = getNewAssignedTo();
            myOldValue = myOldAssignedTo!=null ? myOldAssignedTo.getName() : myOldAssignedTo!=null ? ApplicationResources.accessor.getMessage(userLocale, "TicketAction.old_assigned_to.filtered") : null;
            myNewValue = myNewAssignedTo!=null ? myNewAssignedTo.getName() : myNewAssignedTo!=null ? ApplicationResources.accessor.getMessage(userLocale, "TicketAction.new_assigned_to.filtered") : null;
        } else if(actionType.equals(TicketActionType.SET_CATEGORY)) {
            TicketCategory myOldCategory = getOldCategory();
            TicketCategory myNewCategory = getNewCategory();
            myOldValue = myOldCategory!=null ? myOldCategory.toStringImpl(userLocale) : null;
            myNewValue = myNewCategory!=null ? myNewCategory.toStringImpl(userLocale) : null;
        } else if(
            actionType.equals(TicketActionType.SET_CONTACT_EMAILS)
            || actionType.equals(TicketActionType.SET_CONTACT_PHONE_NUMBERS)
            || actionType.equals(TicketActionType.SET_SUMMARY)
            || actionType.equals(TicketActionType.SET_INTERNAL_NOTES)
            || actionType.equals(TicketActionType.ADD_ANNOTATION)
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
        return getActionType().generateSummary(getService().getConnector(), userLocale, myOldValue, myNewValue);
    }

    /* TODO
    @SchemaColumn(order=21, name="details", description="the details of the action")
    synchronized public String getDetails() throws RemoteException {
        if(!detailsLoaded) {
            // Only perform the query for action types that have details
            if(actionType.equals(TicketActionType.ADD_ANNOTATION)) {
                details = getService().getConnector().requestNullLongStringQuery(true, AOServProtocol.CommandID.GET_TICKET_ACTION_DETAILS, pkey);
            } else {
                details = null;
            }
            detailsLoaded = true;
        }
        return details;
    }
     */

    /* TODO
    @SchemaColumn(order=22, name="raw_email", description="the raw email used to create the action")
    synchronized public String getRawEmail() throws RemoteException {
        if(!rawEmailLoaded) {
            // Only perform the query for action types that may have raw email
            if(actionType.equals(TicketActionType.ADD_ANNOTATION)) {
                rawEmail = getService().getConnector().requestNullLongStringQuery(true, AOServProtocol.CommandID.GET_TICKET_ACTION_RAW_EMAIL, pkey);
            } else {
                rawEmail = null;
            }
            rawEmailLoaded = true;
        }
        return rawEmail;
    }
     */
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.TicketAction getBean() {
        return new com.aoindustries.aoserv.client.beans.TicketAction(
            key,
            ticket,
            getBean(administrator),
            time,
            actionType,
            getBean(oldAccounting),
            getBean(newAccounting),
            oldPriority,
            newPriority,
            oldType,
            newType,
            oldStatus,
            newStatus,
            getBean(oldAssignedTo),
            getBean(newAssignedTo),
            oldCategory,
            newCategory,
            getBean(fromAddress),
            summary
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependencies() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getTicket(),
            getAdministrator(),
            getActionType(),
            getOldBusiness(),
            getNewBusiness(),
            getOldPriority(),
            getNewPriority(),
            getOldType(),
            getNewType(),
            getOldStatus(),
            getNewStatus(),
            getOldAssignedTo(),
            getNewAssignedTo(),
            getOldCategory(),
            getNewCategory()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl(Locale userLocale) {
        return ticket+"|"+key+'|'+actionType+'|'+administrator;
    }
    // </editor-fold>
}
