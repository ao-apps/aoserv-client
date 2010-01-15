/*
 * Copyright 2000-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.command.AddTicketAnnotationCommand;
import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.aoserv.client.validator.Email;
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Locale;
import java.util.Set;

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
final public class Ticket extends AOServObjectIntegerKey<Ticket> implements BeanFactory<com.aoindustries.aoserv.client.beans.Ticket> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private AccountingCode brand;
    final private AccountingCode reseller;
    final private AccountingCode accounting;
    final private String language;
    final private UserId createdBy;
    final private Integer category;
    final private String ticketType;
    final private Email fromAddress;
    final private String summary;
    transient private boolean detailsLoaded;
    transient private String details;
    transient private boolean rawEmailLoaded;
    transient private String rawEmail;
    final private Timestamp openDate;
    final private String clientPriority;
    final private String adminPriority;
    final private String status;
    final private Timestamp statusTimeout;
    final private Set<Email> contactEmails;
    final private Set<String> contactPhoneNumbers;
    transient private boolean internalNotesLoaded;
    transient private String internalNotes;

    public Ticket(
        TicketService<?,?> service,
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
        Timestamp openDate,
        String clientPriority,
        String adminPriority,
        String status,
        Timestamp statusTimeout,
        Set<Email> contactEmails,
        Set<String> contactPhoneNumbers
    ) {
        super(service, ticketId);
        this.brand = brand.intern();
        this.reseller = reseller.intern();
        this.accounting = accounting==null ? null : accounting.intern();
        this.language = language.intern();
        this.createdBy = createdBy==null ? null : createdBy.intern();
        this.category = category;
        this.ticketType = ticketType.intern();
        this.fromAddress = fromAddress==null ? null : fromAddress.intern();
        this.summary = summary;
        this.openDate = openDate;
        this.clientPriority = clientPriority.intern();
        this.adminPriority = adminPriority==null ? null : adminPriority.intern();
        this.status = status.intern();
        this.statusTimeout = statusTimeout;
        this.contactEmails = contactEmails;
        this.contactPhoneNumbers = contactPhoneNumbers;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(Ticket other) {
        int diff = other.openDate.compareTo(openDate); // Descending
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
        return getService().getConnector().getBrands().get(brand);
    }

    static final String COLUMN_RESELLER = "reseller";
    @SchemaColumn(order=2, name=COLUMN_RESELLER, index=IndexType.INDEXED, description="the reseller that received the ticket")
    public Reseller getReseller() throws RemoteException {
        return getService().getConnector().getResellers().get(reseller);
    }

    @SchemaColumn(order=3, name="accounting", description="the business the ticket is for (optional)")
    public Business getBusiness() throws RemoteException {
        if(accounting==null) return null;
        return getService().getConnector().getBusinesses().get(accounting);
    }

    @SchemaColumn(order=4, name="language", description="the language of the ticket")
    public Language getLanguage() throws RemoteException {
        return getService().getConnector().getLanguages().get(language);
    }

    @SchemaColumn(order=5, name="created_by", description="the person who created the ticket")
    public BusinessAdministrator getCreatedBy() throws RemoteException {
        if(createdBy==null) return null;
        return getService().getConnector().getBusinessAdministrators().get(createdBy);
    }

    @SchemaColumn(order=6, name="category", description="the category of the ticket")
    public TicketCategory getCategory() throws RemoteException {
        if(category==null) return null;
        return getService().getConnector().getTicketCategories().get(category);
    }

    @SchemaColumn(order=7, name="ticket_type", description="the type of the ticket")
    public TicketType getTicketType() throws RemoteException {
        return getService().getConnector().getTicketTypes().get(ticketType);
    }

    @SchemaColumn(order=8, name="from_address", description="the from address of the ticket")
    public Email getFromAddress() {
        return fromAddress;
    }

    @SchemaColumn(order=9, name="summary", description="a brief, one-line summary of the ticket")
    public String getSummary() {
        return summary;
    }

    /* TODO
    @SchemaColumn(order=10, name="details", description="the details of the ticket")
    synchronized public String getDetails() throws IOException, SQLException {
        if(!detailsLoaded) {
            details = getService().getConnector().requestNullLongStringQuery(true, AOServProtocol.CommandID.GET_TICKET_DETAILS, key);
            detailsLoaded = true;
        }
        return details;
    }*/

    /* TODO
    @SchemaColumn(order=11, name="raw_email", description="the raw email content of the original ticket requeset")
    synchronized public String getRawEmail() throws IOException, SQLException {
        if(!rawEmailLoaded) {
            rawEmail = getService().getConnector().requestNullLongStringQuery(true, AOServProtocol.CommandID.GET_TICKET_RAW_EMAIL, key);
            rawEmailLoaded = true;
        }
        return rawEmail;
    }*/

    @SchemaColumn(order=10, name="open_date", description="the time the ticket was opened")
    public Timestamp getOpenDate() {
        return openDate;
    }

    @SchemaColumn(order=11, name="client_priority", description="the priority assigned by the client")
    public TicketPriority getClientPriority() throws RemoteException {
        return getService().getConnector().getTicketPriorities().get(clientPriority);
    }

    @SchemaColumn(order=12, name="admin_priority", description="the priority assigned by the administrator")
    public TicketPriority getAdminPriority() throws RemoteException {
        if(adminPriority==null) return null;
        return getService().getConnector().getTicketPriorities().get(adminPriority);
    }

    @SchemaColumn(order=13, name="status", description="the status of the ticket")
    public TicketStatus getStatus() throws RemoteException {
        return getService().getConnector().getTicketStatuses().get(status);
    }

    @SchemaColumn(order=14, name="status_timeout", description="the time the ticket status will automatically return to \"opened\"")
    public Timestamp getStatusTimeout() {
        return statusTimeout;
    }

    @SchemaColumn(order=15, name="contact_emails", description="the set of email addresses that will be notified for the ticket")
    public Set<Email> getContactEmails() {
        return contactEmails;
    }

    @SchemaColumn(order=16, name="contact_phone_numbers", description="the set of phone numbers that may be used in handling the ticket request")
    public Set<String> getContactPhoneNumbers() {
        return contactPhoneNumbers;
    }

    /* TODO
    @SchemaColumn(order=19, name="internal_notes", description="the internal notes used while handling the ticket")
    synchronized public String getInternalNotes() throws IOException, SQLException {
        if(!internalNotesLoaded) {
            internalNotes = getService().getConnector().requestLongStringQuery(true, AOServProtocol.CommandID.GET_TICKET_INTERNAL_NOTES, key);
            internalNotesLoaded = true;
        }
        return internalNotes;
    }*/
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.Ticket getBean() {
        com.aoindustries.aoserv.client.beans.Email[] emails;
        if(contactEmails==null) emails = null;
        else {
            emails = new com.aoindustries.aoserv.client.beans.Email[contactEmails.size()];
            int index = 0;
            for(Email email : contactEmails) emails[index++] = email.getBean();
        }
        return new com.aoindustries.aoserv.client.beans.Ticket(
            key,
            brand.getBean(),
            reseller.getBean(),
            accounting==null ? null : accounting.getBean(),
            language,
            createdBy==null ? null : createdBy.getBean(),
            category,
            ticketType,
            fromAddress==null ? null : fromAddress.getBean(),
            summary,
            openDate,
            clientPriority,
            adminPriority,
            status,
            statusTimeout,
            emails,
            contactPhoneNumbers==null ? null : contactPhoneNumbers.toArray(new String[contactPhoneNumbers.size()])
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependencies() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getBrand(),
            getReseller(),
            getBusiness(),
            getCreatedBy(),
            getCategory()
        );
    }

    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            // TODO: getTicketActions(),
            getTicketAssignments()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl(Locale userLocale) {
        return key+"|"+brand+'/'+accounting+'|'+status+"->"+reseller;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    /* TODO
    public List<TicketAction> getTicketActions() throws IOException, SQLException {
        return getService().getConnector().getTicketActions().getActions(this);
    }
    */
    public IndexedSet<TicketAssignment> getTicketAssignments() throws RemoteException {
        return getService().getConnector().getTicketAssignments().filterIndexed(TicketAssignment.COLUMN_TICKET, this);
    }
    // </editor-fold>

    // <editor-fold desc="Commands">
    public int addAnnotation(String summary, String details) throws RemoteException {
        return new AddTicketAnnotationCommand(
            key,
            summary,
            details
        ).execute(getService().getConnector());
    }
    // TODO
//    /*
//    public void actBounceTicket(BusinessAdministrator business_administrator, String comments) throws IOException, SQLException {
//        getService().getConnector().requestUpdateIL(true, AOServProtocol.CommandID.BOUNCE_TICKET, pkey, business_administrator.pkey, comments);
//    }*/
//
//    public void actChangeAdminPriority(TicketPriority priority, BusinessAdministrator business_administrator, String comments) throws IOException, SQLException {
//        getService().getConnector().requestUpdateIL(true, AOServProtocol.CommandID.CHANGE_TICKET_ADMIN_PRIORITY, key, priority==null ? "" : priority.pkey, business_administrator.pkey, comments);
//    }
//
//    public void setClientPriority(TicketPriority clientPriority) throws IOException, SQLException {
//        getService().getConnector().requestUpdateIL(true, AOServProtocol.CommandID.CHANGE_TICKET_CLIENT_PRIORITY, key, clientPriority.pkey);
//    }
//
//    public void setSummary(String summary) throws IOException, SQLException {
//        getService().getConnector().requestUpdateIL(true, AOServProtocol.CommandID.SET_TICKET_SUMMARY, key, summary);
//    }
//
//    public void actAssignTo(BusinessAdministrator assignedTo, BusinessAdministrator business_administrator, String comments) throws IOException, SQLException {
//        getService().getConnector().requestUpdateIL(true, AOServProtocol.CommandID.SET_TICKET_ASSIGNED_TO, key, assignedTo==null?"":assignedTo.getUsername().getUsername(), business_administrator.pkey, comments);
//    }
//
//    public void setContactEmails(String contactEmails) throws IOException, SQLException {
//        getService().getConnector().requestUpdateIL(true, AOServProtocol.CommandID.SET_TICKET_CONTACT_EMAILS, key, contactEmails);
//    }
//
//    public void setContactPhoneNumbers(String contactPhoneNumbers) throws IOException, SQLException {
//        getService().getConnector().requestUpdateIL(true, AOServProtocol.CommandID.SET_TICKET_CONTACT_PHONE_NUMBERS, key, contactPhoneNumbers);
//    }
//
//    /*
//    public void actCompleteTicket(BusinessAdministrator business_administrator, String comments) throws IOException, SQLException {
//        getService().getConnector().requestUpdateIL(true, AOServProtocol.CommandID.COMPLETE_TICKET, pkey, business_administrator.pkey, comments);
//    }*/
//
//    /*
//    public void actHoldTicket(String comments) throws IOException, SQLException {
//        getService().getConnector().requestUpdateIL(true, AOServProtocol.CommandID.HOLD_TICKET, pkey, comments);
//    }*/
//
//    /*
//    public void actKillTicket(BusinessAdministrator business_administrator, String comments) throws IOException, SQLException {
//        getService().getConnector().requestUpdateIL(true, AOServProtocol.CommandID.KILL_TICKET, pkey, business_administrator.pkey, comments);
//    }*/
//
//    /*
//    public void actReactivateTicket(BusinessAdministrator business_administrator, String comments) throws IOException, SQLException {
//        getService().getConnector().requestUpdateIL(true, AOServProtocol.CommandID.REACTIVATE_TICKET, pkey, business_administrator.pkey, comments);
//    }*/
//
//    public void actWorkEntry(BusinessAdministrator business_administrator, String comments) throws IOException, SQLException {
//        getService().getConnector().requestUpdateIL(true, AOServProtocol.CommandID.TICKET_WORK, pkey, business_administrator.pkey, comments);
//    }
//
//    /**
//     * Updates the ticket business if the old business matches the current value.
//     *
//     * @return <code>true</code> if successfully updated or <code>false</code> if oldBusiness doesn't match the current business.
//     */
//    public boolean setBusiness(Business oldBusiness, Business newBusiness) throws IOException, SQLException {
//        return getService().getConnector().requestBooleanQueryIL(
//            true,
//            AOServProtocol.CommandID.SET_TICKET_BUSINESS,
//            key,
//            oldBusiness==null ? "" : oldBusiness.getAccounting(),
//            newBusiness==null ? "" : newBusiness.getAccounting()
//        );
//    }
//
//    /**
//     * Updates the ticket type if the old value matches the current value.
//     *
//     * @return <code>true</code> if successfully updated or <code>false</code> if oldType doesn't match the current type.
//     */
//    public boolean setTicketType(TicketType oldType, TicketType newType) throws IOException, SQLException {
//        return getService().getConnector().requestBooleanQueryIL(true, AOServProtocol.CommandID.CHANGE_TICKET_TYPE, key, oldType.pkey, newType.pkey);
//        // getService().getConnector().requestUpdateIL(true, AOServProtocol.CommandID.CHANGE_TICKET_TYPE, pkey, ticket_type.pkey, business_administrator.pkey, comments);
//   }
//
//    /**
//     * Updates the ticket status if the old status matches the current value.
//     *
//     * @return <code>true</code> if successfully updated or <code>false</code> if oldStatus doesn't match the current status.
//     */
//    public boolean setStatus(TicketStatus oldStatus, TicketStatus newStatus, long statusTimeout) throws IOException, SQLException {
//        return getService().getConnector().requestBooleanQueryIL(true, AOServProtocol.CommandID.SET_TICKET_STATUS, key, oldStatus.pkey, newStatus.pkey, statusTimeout);
//    }
//
//    /**
//     * Updates the internal notes if the old value matches the current value.
//     *
//     * @return <code>true</code> if successfully updated or <code>false</code> if oldInternalNotes doesn't match the current internal notes.
//     */
//    public boolean setInternalNotes(final String oldInternalNotes, final String newInternalNotes) throws IOException, SQLException {
//        return getService().getConnector().requestResult(
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
//                    getService().getConnector().tablesUpdated(invalidateList);
//                    return result;
//                }
//            }
//        );
//    }
    // </editor-fold>
}