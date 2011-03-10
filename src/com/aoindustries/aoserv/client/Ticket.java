/*
 * Copyright 2000-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.command.*;
import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.UnionSet;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.NoSuchElementException;

/**
 * The <code>Ticket</code> system allows clients to submit support
 * requests and monitor the progress of the work.
 *
 * @see  TicketAction
 * @see  TicketPriority
 * @see  TicketType
 *
 * @author  AO Industries, Inc.
 */
final public class Ticket extends AOServObjectIntegerKey implements Comparable<Ticket>, DtoFactory<com.aoindustries.aoserv.client.dto.Ticket> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private AccountingCode brand;
    private AccountingCode reseller;
    private AccountingCode accounting;
    private String language;
    private UserId createdBy;
    final private Integer category;
    private String ticketType;
    private Email fromAddress;
    private String summary;
    transient private boolean detailsLoaded;
    transient private String details;
    transient private boolean rawEmailLoaded;
    transient private String rawEmail;
    final private long openDate;
    private String clientPriority;
    private String adminPriority;
    private String status;
    final private Long statusTimeout;
    final private String contactEmails;
    final private String contactPhoneNumbers;
    transient private boolean internalNotesLoaded;
    transient private String internalNotes;

    public Ticket(
        AOServConnector connector,
        int ticketId,
        AccountingCode brand,
        AccountingCode reseller,
        AccountingCode accounting,
        String language,
        UserId createdBy,
        Integer category,
        String ticketType,
        Email fromAddress,
        String summary,
        long openDate,
        String clientPriority,
        String adminPriority,
        String status,
        Long statusTimeout,
        String contactEmails,
        String contactPhoneNumbers
    ) {
        super(connector, ticketId);
        this.brand = brand;
        this.reseller = reseller;
        this.accounting = accounting;
        this.language = language;
        this.createdBy = createdBy;
        this.category = category;
        this.ticketType = ticketType;
        this.fromAddress = fromAddress;
        this.summary = summary;
        this.openDate = openDate;
        this.clientPriority = clientPriority;
        this.adminPriority = adminPriority;
        this.status = status;
        this.statusTimeout = statusTimeout;
        this.contactEmails = contactEmails;
        this.contactPhoneNumbers = contactPhoneNumbers;
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        brand = intern(brand);
        reseller = intern(reseller);
        accounting = intern(accounting);
        language = intern(language);
        createdBy = intern(createdBy);
        ticketType = intern(ticketType);
        fromAddress = intern(fromAddress);
        summary = intern(summary);
        clientPriority = intern(clientPriority);
        adminPriority = intern(adminPriority);
        status = intern(status);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(Ticket other) {
        int diff = AOServObjectUtils.compare(other.openDate, openDate); // Descending
        if(diff!=0) return diff;
        return AOServObjectUtils.compare(other.key, key); // Descending
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="ticket_id", index=IndexType.PRIMARY_KEY, description="a generated unique id")
    public int getTicketId() {
        return key;
    }

    static final String COLUMN_BRAND = "brand";
    @SchemaColumn(order=1, name=COLUMN_BRAND, index=IndexType.INDEXED, description="the brand that created the ticket")
    public Brand getBrand() throws RemoteException {
        return getConnector().getBrands().get(brand);
    }

    /**
     * May be filtered.
     */
    static final String COLUMN_RESELLER = "reseller";
    @SchemaColumn(order=2, name=COLUMN_RESELLER, index=IndexType.INDEXED, description="the reseller that received the ticket")
    public Reseller getReseller() throws RemoteException {
        if(reseller==null) return null;
        try {
            return getConnector().getResellers().get(reseller);
        } catch(NoSuchElementException err) {
            return null;
        }
    }

    static final String COLUMN_ACCOUNTING = "accounting";
    @SchemaColumn(order=3, name=COLUMN_ACCOUNTING, index=IndexType.INDEXED, description="the business the ticket is for (optional)")
    public Business getBusiness() throws RemoteException {
        if(accounting==null) return null;
        return getConnector().getBusinesses().get(accounting);
    }

    static final String COLUMN_LANGUAGE = "language";
    @SchemaColumn(order=4, name=COLUMN_LANGUAGE, index=IndexType.INDEXED, description="the language of the ticket")
    public Language getLanguage() throws RemoteException {
        return getConnector().getLanguages().get(language);
    }

    static final String COLUMN_CREATED_BY = "created_by";
    @SchemaColumn(order=5, name=COLUMN_CREATED_BY, index=IndexType.INDEXED, description="the person who created the ticket")
    public BusinessAdministrator getCreatedBy() throws RemoteException {
        if(createdBy==null) return null;
        return getConnector().getBusinessAdministrators().get(createdBy);
    }

    static final String COLUMN_CATEGORY = "category";
    @SchemaColumn(order=6, name=COLUMN_CATEGORY, index=IndexType.INDEXED, description="the category of the ticket")
    public TicketCategory getCategory() throws RemoteException {
        if(category==null) return null;
        return getConnector().getTicketCategories().get(category);
    }

    static final String COLUMN_TICKET_TYPE = "ticket_type";
    @SchemaColumn(order=7, name=COLUMN_TICKET_TYPE, index=IndexType.INDEXED, description="the type of the ticket")
    public TicketType getTicketType() throws RemoteException {
        return getConnector().getTicketTypes().get(ticketType);
    }

    @SchemaColumn(order=8, name="from_address", description="the from address of the ticket")
    public Email getFromAddress() {
        return fromAddress;
    }

    @SchemaColumn(order=9, name="summary", description="a brief, one-line summary of the ticket")
    public String getSummary() {
        return summary;
    }

    @SchemaColumn(order=10, name="details", description="the details of the ticket")
    synchronized public String getDetails() throws RemoteException {
        if(!detailsLoaded) {
            details = new GetTicketDetailsCommand(this).execute(getConnector());
            detailsLoaded = true;
        }
        return details;
    }

    /* TODO
    @SchemaColumn(order=11, name="raw_email", description="the raw email content of the original ticket requeset")
    synchronized public String getRawEmail() throws IOException, SQLException {
        if(!rawEmailLoaded) {
            rawEmail = getConnector().requestNullLongStringQuery(true, AOServProtocol.CommandID.GET_TICKET_RAW_EMAIL, key);
            rawEmailLoaded = true;
        }
        return rawEmail;
    }*/

    @SchemaColumn(order=11, name="open_date", description="the time the ticket was opened")
    public Timestamp getOpenDate() {
        return new Timestamp(openDate);
    }

    static final String COLUMN_CLIENT_PRIORITY = "client_priority";
    @SchemaColumn(order=12, name=COLUMN_CLIENT_PRIORITY, index=IndexType.INDEXED, description="the priority assigned by the client")
    public TicketPriority getClientPriority() throws RemoteException {
        return getConnector().getTicketPriorities().get(clientPriority);
    }

    static final String COLUMN_ADMIN_PRIORITY = "admin_priority";
    @SchemaColumn(order=13, name=COLUMN_ADMIN_PRIORITY, index=IndexType.INDEXED, description="the priority assigned by the administrator")
    public TicketPriority getAdminPriority() throws RemoteException {
        if(adminPriority==null) return null;
        return getConnector().getTicketPriorities().get(adminPriority);
    }

    static final String COLUMN_STATUS = "status";
    @SchemaColumn(order=14, name=COLUMN_STATUS, index=IndexType.INDEXED, description="the status of the ticket")
    public TicketStatus getStatus() throws RemoteException {
        return getConnector().getTicketStatuses().get(status);
    }

    @SchemaColumn(order=15, name="status_timeout", description="the time the ticket status will automatically return to \"opened\"")
    public Timestamp getStatusTimeout() {
        return statusTimeout==null ? null : new Timestamp(statusTimeout);
    }

    @SchemaColumn(order=16, name="contact_emails", description="the set of email addresses that will be notified for the ticket")
    public String getContactEmails() {
        return contactEmails;
    }

    @SchemaColumn(order=17, name="contact_phone_numbers", description="the set of phone numbers that may be used in handling the ticket request")
    public String getContactPhoneNumbers() {
        return contactPhoneNumbers;
    }

    /* TODO
    @SchemaColumn(order=19, name="internal_notes", description="the internal notes used while handling the ticket")
    synchronized public String getInternalNotes() throws IOException, SQLException {
        if(!internalNotesLoaded) {
            internalNotes = getConnector().requestLongStringQuery(true, AOServProtocol.CommandID.GET_TICKET_INTERNAL_NOTES, key);
            internalNotesLoaded = true;
        }
        return internalNotes;
    }*/
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public Ticket(AOServConnector connector, com.aoindustries.aoserv.client.dto.Ticket dto) throws ValidationException {
        this(
            connector,
            dto.getTicketId(),
            getAccountingCode(dto.getBrand()),
            getAccountingCode(dto.getReseller()),
            getAccountingCode(dto.getAccounting()),
            dto.getLanguage(),
            getUserId(dto.getCreatedBy()),
            dto.getCategory(),
            dto.getTicketType(),
            getEmail(dto.getFromAddress()),
            dto.getSummary(),
            getTimeMillis(dto.getOpenDate()),
            dto.getClientPriority(),
            dto.getAdminPriority(),
            dto.getStatus(),
            getTimeMillis(dto.getStatusTimeout()),
            dto.getContactEmails(),
            dto.getContactPhoneNumbers()
        );
    }

    @Override
    public com.aoindustries.aoserv.client.dto.Ticket getDto() {
        return new com.aoindustries.aoserv.client.dto.Ticket(
            key,
            getDto(brand),
            getDto(reseller),
            getDto(accounting),
            language,
            getDto(createdBy),
            category,
            ticketType,
            getDto(fromAddress),
            summary,
            openDate,
            clientPriority,
            adminPriority,
            status,
            statusTimeout,
            contactEmails,
            contactPhoneNumbers
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionSet<AOServObject<?>> addDependencies(UnionSet<AOServObject<?>> unionSet) throws RemoteException {
        unionSet = super.addDependencies(unionSet);
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getBrand());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getReseller());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getBusiness());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getLanguage());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getCreatedBy());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getCategory());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getTicketType());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getClientPriority());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getAdminPriority());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getStatus());
        return unionSet;
    }

    @Override
    protected UnionSet<AOServObject<?>> addDependentObjects(UnionSet<AOServObject<?>> unionSet) throws RemoteException {
        unionSet = super.addDependentObjects(unionSet);
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getTicketActions());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getTicketAssignments());
        return unionSet;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() {
        return key+"|"+brand+'/'+accounting+'|'+status+"->"+reseller;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public IndexedSet<TicketAction> getTicketActions() throws RemoteException {
        return getConnector().getTicketActions().filterIndexed(TicketAction.COLUMN_TICKET, this);
    }

    public IndexedSet<TicketAssignment> getTicketAssignments() throws RemoteException {
        return getConnector().getTicketAssignments().filterIndexed(TicketAssignment.COLUMN_TICKET, this);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
    // TODO
//    /*
//    public void actBounceTicket(BusinessAdministrator business_administrator, String comments) throws IOException, SQLException {
//        getConnector().requestUpdateIL(true, AOServProtocol.CommandID.BOUNCE_TICKET, pkey, business_administrator.pkey, comments);
//    }*/
//
//    public void actChangeAdminPriority(TicketPriority priority, BusinessAdministrator business_administrator, String comments) throws IOException, SQLException {
//        getConnector().requestUpdateIL(true, AOServProtocol.CommandID.CHANGE_TICKET_ADMIN_PRIORITY, key, priority==null ? "" : priority.pkey, business_administrator.pkey, comments);
//    }
//
//    public void actAssignTo(BusinessAdministrator assignedTo, BusinessAdministrator business_administrator, String comments) throws IOException, SQLException {
//        getConnector().requestUpdateIL(true, AOServProtocol.CommandID.SET_TICKET_ASSIGNED_TO, key, assignedTo==null?"":assignedTo.getUsername().getUsername(), business_administrator.pkey, comments);
//    }
//
//    /*
//    public void actCompleteTicket(BusinessAdministrator business_administrator, String comments) throws IOException, SQLException {
//        getConnector().requestUpdateIL(true, AOServProtocol.CommandID.COMPLETE_TICKET, pkey, business_administrator.pkey, comments);
//    }*/
//
//    /*
//    public void actHoldTicket(String comments) throws IOException, SQLException {
//        getConnector().requestUpdateIL(true, AOServProtocol.CommandID.HOLD_TICKET, pkey, comments);
//    }*/
//
//    /*
//    public void actKillTicket(BusinessAdministrator business_administrator, String comments) throws IOException, SQLException {
//        getConnector().requestUpdateIL(true, AOServProtocol.CommandID.KILL_TICKET, pkey, business_administrator.pkey, comments);
//    }*/
//
//    /*
//    public void actReactivateTicket(BusinessAdministrator business_administrator, String comments) throws IOException, SQLException {
//        getConnector().requestUpdateIL(true, AOServProtocol.CommandID.REACTIVATE_TICKET, pkey, business_administrator.pkey, comments);
//    }*/
//
//    public void actWorkEntry(BusinessAdministrator business_administrator, String comments) throws IOException, SQLException {
//        getConnector().requestUpdateIL(true, AOServProtocol.CommandID.TICKET_WORK, pkey, business_administrator.pkey, comments);
//    }
//
//
//    /**
//     * Updates the ticket type if the old value matches the current value.
//     *
//     * @return <code>true</code> if successfully updated or <code>false</code> if oldType doesn't match the current type.
//     */
//    public boolean setTicketType(TicketType oldType, TicketType newType) throws IOException, SQLException {
//        return getConnector().requestBooleanQueryIL(true, AOServProtocol.CommandID.CHANGE_TICKET_TYPE, key, oldType.pkey, newType.pkey);
//        // getConnector().requestUpdateIL(true, AOServProtocol.CommandID.CHANGE_TICKET_TYPE, pkey, ticket_type.pkey, business_administrator.pkey, comments);
//   }
//
//    /**
//     * Updates the ticket status if the old status matches the current value.
//     *
//     * @return <code>true</code> if successfully updated or <code>false</code> if oldStatus doesn't match the current status.
//     */
//    public boolean setStatus(TicketStatus oldStatus, TicketStatus newStatus, long statusTimeout) throws IOException, SQLException {
//        return getConnector().requestBooleanQueryIL(true, AOServProtocol.CommandID.SET_TICKET_STATUS, key, oldStatus.pkey, newStatus.pkey, statusTimeout);
//    }
//
//    /**
//     * Updates the internal notes if the old value matches the current value.
//     *
//     * @return <code>true</code> if successfully updated or <code>false</code> if oldInternalNotes doesn't match the current internal notes.
//     */
//    public boolean setInternalNotes(final String oldInternalNotes, final String newInternalNotes) throws IOException, SQLException {
//        return getConnector().requestResult(
//            true,
//            new AOServConnector.ResultRequest<Boolean>() {
//                boolean result;
//                IntList invalidateList;
//
//                public void writeRequest(CompressedDataOutputStream out) throws IOException {
//                    out.writeCompressedInt(AOServProtocol.CommandID.SET_TICKET_INTERNAL_NOTES.ordinal());
//                    out.writeCompressedInt(key);
//                    out.writeLongUTF(oldInternalNotes);
//                    out.writeLongUTF(newInternalNotes);
//                }
//
//                public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
//                    int code = in.readByte();
//                    if(code==AOServProtocol.DONE) {
//                        result = in.readBoolean();
//                        invalidateList = AOServConnector.readInvalidateList(in);
//                    } else {
//                        AOServProtocol.checkResult(code, in);
//                        throw new IOException("Unexpected response code: "+code);
//                    }
//                }
//
//                public Boolean afterRelease() {
//                    getConnector().tablesUpdated(invalidateList);
//                    return result;
//                }
//            }
//        );
//    }
    // </editor-fold>
}