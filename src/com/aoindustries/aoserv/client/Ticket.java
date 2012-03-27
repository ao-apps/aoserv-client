package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.util.IntList;
import com.aoindustries.util.StringUtility;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

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
final public class Ticket extends CachedObjectIntegerKey<Ticket> {

    // <editor-fold desc="Fields">
    private String brand;
    private String reseller;
    private String accounting;
    private String language;
    private String created_by;
    private int category;
    private String ticket_type;
    private String from_address;
    private String summary;
    private boolean detailsLoaded;
    private String details;
    private boolean rawEmailLoaded;
    private String raw_email;
    private long open_date;
    private String client_priority;
    private String admin_priority;
    private String status;
    private long status_timeout;
    private String contact_emails;
    private String contact_phone_numbers;
    private boolean internalNotesLoaded;
    private String internal_notes;
    // </editor-fold>

    // <editor-fold desc="Object implementation">
    @Override
    String toStringImpl() {
        return pkey+"|"+brand+'/'+accounting+'|'+status+"->"+reseller;
    }
    // </editor-fold>

    // <editor-fold desc="AOServObject implementation">
    static final int COLUMN_PKEY = 0;
    static final int COLUMN_ACCOUNTING = 3;
    static final int COLUMN_CREATED_BY = 5;
    static final int COLUMN_OPEN_DATE = 12;
    static final String COLUMN_PKEY_name = "pkey";
    static final String COLUMN_OPEN_DATE_name = "open_date";

    Object getColumnImpl(int i) throws IOException, SQLException {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case 1: return brand;
            case 2: return reseller;
            case COLUMN_ACCOUNTING: return accounting;
            case 4: return language;
            case COLUMN_CREATED_BY: return created_by;
            case 6: return category==-1 ? null : category;
            case 7: return ticket_type;
            case 8: return from_address;
            case 9: return summary;
            case 10: return getDetails();
            case 11: return getRawEmail();
            case COLUMN_OPEN_DATE: return new java.sql.Date(open_date);
            case 13: return client_priority;
            case 14: return admin_priority;
            case 15: return status;
            case 16: return status_timeout==-1 ? null : new java.sql.Date(status_timeout);
            case 17: return contact_emails;
            case 18: return contact_phone_numbers;
            case 19: return getInternalNotes();
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.TICKETS;
    }

    public void init(ResultSet result) throws SQLException {
        int pos = 1;
        pkey = result.getInt(pos++);
        brand = result.getString(pos++);
        reseller = result.getString(pos++);
        accounting = result.getString(pos++);
        language = result.getString(pos++);
        created_by = result.getString(pos++);
        category = result.getInt(pos++); if(result.wasNull()) category = -1;
        ticket_type = result.getString(pos++);
        from_address = result.getString(pos++);
        summary = result.getString(pos++);
        Timestamp temp = result.getTimestamp(pos++);
        open_date = temp == null ? -1 : temp.getTime();
        client_priority = result.getString(pos++);
        admin_priority = result.getString(pos++);
        status = result.getString(pos++);
        temp = result.getTimestamp(pos++);
        status_timeout = temp == null ? -1 : temp.getTime();
        contact_emails = result.getString(pos++);
        contact_phone_numbers = result.getString(pos++);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey = in.readCompressedInt();
        brand = in.readUTF().intern();
        reseller = in.readUTF().intern();
        accounting = StringUtility.intern(in.readNullUTF());
        language = in.readUTF().intern();
    	created_by = StringUtility.intern(in.readNullUTF());
        category = in.readCompressedInt();
    	ticket_type = in.readUTF().intern();
        from_address = in.readNullUTF();
        summary = in.readUTF();
        open_date = in.readLong();
        client_priority = in.readUTF().intern();
        admin_priority = StringUtility.intern(in.readNullUTF());
        status = in.readUTF().intern();
        status_timeout = in.readLong();
        contact_emails = in.readUTF();
        contact_phone_numbers = in.readUTF();
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeCompressedInt(pkey);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_46)>=0) out.writeUTF(brand);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_44)>=0) out.writeUTF(reseller);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_125)<=0) out.writeUTF(accounting==null ? "" : accounting);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_126)>=0) out.writeNullUTF(accounting);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_44)>=0) out.writeUTF(language);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_125)<=0) out.writeUTF(created_by==null ? "" : created_by);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_126)>=0) out.writeNullUTF(created_by);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_44)>=0) out.writeCompressedInt(category);
    	out.writeUTF(ticket_type);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_44)>=0) out.writeNullUTF(from_address);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_44)>=0) out.writeUTF(summary);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_14)<=0) out.writeUTF("");
        if(version.compareTo(AOServProtocol.Version.VERSION_1_15)>=0 && version.compareTo(AOServProtocol.Version.VERSION_1_43)<=0) out.writeCompressedInt(0); // details
    	out.writeLong(open_date);
    	if(version.compareTo(AOServProtocol.Version.VERSION_1_43)<=0) out.writeLong(-1);
    	if(version.compareTo(AOServProtocol.Version.VERSION_1_43)<=0) out.writeLong(-1);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_43)<=0) out.writeNullUTF(null);
        out.writeUTF(client_priority);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_10)<0) {
            out.writeUTF(admin_priority==null ? client_priority : admin_priority);
        } else {
            out.writeNullUTF(admin_priority);
        }
    	if(version.compareTo(AOServProtocol.Version.VERSION_1_43)<=0) out.writeNullUTF(null); // technology
        out.writeUTF(status);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_44)>=0) out.writeLong(status_timeout);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_125)>=0 && version.compareTo(AOServProtocol.Version.VERSION_1_43)<=0) out.writeNullUTF(null); // assigned_to
        if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_125)>=0) {
            out.writeUTF(contact_emails);
            out.writeUTF(contact_phone_numbers);
        }
    }
    // </editor-fold>

    // <editor-fold desc="Accessors">
    public int getTicketID() {
        return pkey;
    }

    /**
     * May be filtered.
     */
    public Brand getBrand() throws IOException, SQLException {
        return table.connector.getBrands().get(brand);
    }

    /**
     * May be filtered.
     */
    public Reseller getReseller() throws SQLException, IOException {
        return table.connector.getResellers().get(reseller);
    }

    /**
     * May be null if not set or filtered.
     */
    public Business getBusiness() throws SQLException, IOException {
        if(accounting==null) return null;
        return table.connector.getBusinesses().get(accounting);
    }

    public Language getLanguage() throws SQLException, IOException {
        Language la = table.connector.getLanguages().get(language);
        if(la==null) throw new SQLException("Unable to find Language: "+language);
        return la;
    }

    public BusinessAdministrator getCreatedBy() throws IOException, SQLException {
        if(created_by==null) return null;
        // Data may be filtered by APIs
        return table.connector.getBusinessAdministrators().get(created_by);
    }

    public TicketCategory getCategory() throws IOException, SQLException {
        if(category==-1) return null;
        TicketCategory tc = table.connector.getTicketCategories().get(category);
        if(tc==null) throw new SQLException("Unable to find TicketCategory: "+category);
        return tc;
    }

    public TicketType getTicketType() throws IOException, SQLException {
        TicketType ticketTypeObject = table.connector.getTicketTypes().get(ticket_type);
        if (ticketTypeObject  == null) throw new SQLException("Unable to find TicketType: " + ticket_type);
        return ticketTypeObject;
    }

    public String getFromAddress() {
        return from_address;
    }

    public String getSummary() {
        return summary;
    }

    synchronized public String getDetails() throws IOException, SQLException {
        if(!detailsLoaded) {
            details = table.connector.requestNullLongStringQuery(true, AOServProtocol.CommandID.GET_TICKET_DETAILS, pkey);
            detailsLoaded = true;
        }
        return details;
    }

    synchronized public String getRawEmail() throws IOException, SQLException {
        if(!rawEmailLoaded) {
            raw_email = table.connector.requestNullLongStringQuery(true, AOServProtocol.CommandID.GET_TICKET_RAW_EMAIL, pkey);
            rawEmailLoaded = true;
        }
        return raw_email;
    }

    public long getOpenDate() {
        return open_date;
    }

    public TicketPriority getClientPriority() throws IOException, SQLException {
        TicketPriority clientPriorityObject = table.connector.getTicketPriorities().get(client_priority);
        if (clientPriorityObject == null) throw new SQLException("Unable to find Priority: " + client_priority);
        return clientPriorityObject;
    }

    public TicketPriority getAdminPriority() throws IOException, SQLException {
        if(admin_priority==null) return null;
        TicketPriority adminPriorityObject = table.connector.getTicketPriorities().get(admin_priority);
        if (adminPriorityObject == null) throw new SQLException("Unable to find Priority: " + admin_priority);
        return adminPriorityObject;
    }

    public TicketStatus getStatus() throws IOException, SQLException {
        TicketStatus statusObject = table.connector.getTicketStatuses().get(status);
        if (statusObject == null) throw new SQLException("Unable to find status: " + status);
        return statusObject;
    }

    public long getStatusTimeout() {
        return status_timeout;
    }

    public String getContactEmails() {
        return contact_emails;
    }

    public String getContactPhoneNumbers() {
        return contact_phone_numbers;
    }

    synchronized public String getInternalNotes() throws IOException, SQLException {
        if(!internalNotesLoaded) {
            internal_notes = table.connector.requestLongStringQuery(true, AOServProtocol.CommandID.GET_TICKET_INTERNAL_NOTES, pkey);
            internalNotesLoaded = true;
        }
        return internal_notes;
    }
    // </editor-fold>

    // <editor-fold desc="Data Access">
    public List<TicketAction> getTicketActions() throws IOException, SQLException {
        return table.connector.getTicketActions().getActions(this);
    }

    public List<TicketAssignment> getTicketAssignments() throws IOException, SQLException {
        return table.connector.getTicketAssignments().getTicketAssignments(this);
    }
    // </editor-fold>

    // <editor-fold desc="Ticket Actions">
    /*
    public void actBounceTicket(BusinessAdministrator business_administrator, String comments) throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.BOUNCE_TICKET, pkey, business_administrator.pkey, comments);
    }*/

    public void actChangeAdminPriority(TicketPriority priority, BusinessAdministrator business_administrator, String comments) throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.CHANGE_TICKET_ADMIN_PRIORITY, pkey, priority==null ? "" : priority.pkey, business_administrator.pkey, comments);
    }

    public void setClientPriority(TicketPriority clientPriority) throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.CHANGE_TICKET_CLIENT_PRIORITY, pkey, clientPriority.pkey);
    }

    public void setSummary(String summary) throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.SET_TICKET_SUMMARY, pkey, summary);
    }

    public void addAnnotation(final String summary, final String details) throws IOException, SQLException {
        table.connector.requestUpdate(
            true,
            new AOServConnector.UpdateRequest() {
                IntList invalidateList;

                public void writeRequest(CompressedDataOutputStream out) throws IOException {
                    out.writeCompressedInt(AOServProtocol.CommandID.ADD_TICKET_ANNOTATION.ordinal());
                    out.writeCompressedInt(pkey);
                    out.writeUTF(summary);
                    out.writeNullLongUTF(details);
                }

                public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
                    int code=in.readByte();
                    if(code==AOServProtocol.DONE) {
                        invalidateList=AOServConnector.readInvalidateList(in);
                    } else {
                        AOServProtocol.checkResult(code, in);
                        throw new IOException("Unexpected response code: "+code);
                    }
                }

                public void afterRelease() {
                    table.connector.tablesUpdated(invalidateList);
                }
            }
        );
    }

    public void actAssignTo(BusinessAdministrator assignedTo, BusinessAdministrator business_administrator, String comments) throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.SET_TICKET_ASSIGNED_TO, pkey, assignedTo==null?"":assignedTo.getUsername().getUsername(), business_administrator.pkey, comments);
    }

    public void setContactEmails(String contactEmails) throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.SET_TICKET_CONTACT_EMAILS, pkey, contactEmails);
    }

    public void setContactPhoneNumbers(String contactPhoneNumbers) throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.SET_TICKET_CONTACT_PHONE_NUMBERS, pkey, contactPhoneNumbers);
    }

    /*
    public void actCompleteTicket(BusinessAdministrator business_administrator, String comments) throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.COMPLETE_TICKET, pkey, business_administrator.pkey, comments);
    }*/

    /*
    public void actHoldTicket(String comments) throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.HOLD_TICKET, pkey, comments);
    }*/

    /*
    public void actKillTicket(BusinessAdministrator business_administrator, String comments) throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.KILL_TICKET, pkey, business_administrator.pkey, comments);
    }*/

    /*
    public void actReactivateTicket(BusinessAdministrator business_administrator, String comments) throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.REACTIVATE_TICKET, pkey, business_administrator.pkey, comments);
    }*/

    public void actWorkEntry(BusinessAdministrator business_administrator, String comments) throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.TICKET_WORK, pkey, business_administrator.pkey, comments);
    }

    /**
     * Updates the ticket business if the old business matches the current value.
     *
     * @return <code>true</code> if successfully updated or <code>false</code> if oldBusiness doesn't match the current business.
     */
    public boolean setBusiness(Business oldBusiness, Business newBusiness) throws IOException, SQLException {
        return table.connector.requestBooleanQueryIL(
            true,
            AOServProtocol.CommandID.SET_TICKET_BUSINESS,
            pkey,
            oldBusiness==null ? "" : oldBusiness.getAccounting(),
            newBusiness==null ? "" : newBusiness.getAccounting()
        );
    }

    /**
     * Updates the ticket type if the old value matches the current value.
     *
     * @return <code>true</code> if successfully updated or <code>false</code> if oldType doesn't match the current type.
     */
    public boolean setTicketType(TicketType oldType, TicketType newType) throws IOException, SQLException {
        return table.connector.requestBooleanQueryIL(true, AOServProtocol.CommandID.CHANGE_TICKET_TYPE, pkey, oldType.pkey, newType.pkey);
        // table.connector.requestUpdateIL(true, AOServProtocol.CommandID.CHANGE_TICKET_TYPE, pkey, ticket_type.pkey, business_administrator.pkey, comments);
   }

    /**
     * Updates the ticket status if the old status matches the current value.
     *
     * @return <code>true</code> if successfully updated or <code>false</code> if oldStatus doesn't match the current status.
     */
    public boolean setStatus(TicketStatus oldStatus, TicketStatus newStatus, long statusTimeout) throws IOException, SQLException {
        return table.connector.requestBooleanQueryIL(true, AOServProtocol.CommandID.SET_TICKET_STATUS, pkey, oldStatus.pkey, newStatus.pkey, statusTimeout);
    }

    /**
     * Updates the internal notes if the old value matches the current value.
     *
     * @return <code>true</code> if successfully updated or <code>false</code> if oldInternalNotes doesn't match the current internal notes.
     */
    public boolean setInternalNotes(final String oldInternalNotes, final String newInternalNotes) throws IOException, SQLException {
        return table.connector.requestResult(
            true,
            new AOServConnector.ResultRequest<Boolean>() {
                boolean result;
                IntList invalidateList;

                public void writeRequest(CompressedDataOutputStream out) throws IOException {
                    out.writeCompressedInt(AOServProtocol.CommandID.SET_TICKET_INTERNAL_NOTES.ordinal());
                    out.writeCompressedInt(pkey);
                    out.writeLongUTF(oldInternalNotes);
                    out.writeLongUTF(newInternalNotes);
                }

                public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
                    int code = in.readByte();
                    if(code==AOServProtocol.DONE) {
                        result = in.readBoolean();
                        invalidateList = AOServConnector.readInvalidateList(in);
                    } else {
                        AOServProtocol.checkResult(code, in);
                        throw new IOException("Unexpected response code: "+code);
                    }
                }

                public Boolean afterRelease() {
                    table.connector.tablesUpdated(invalidateList);
                    return result;
                }
            }
        );
    }
    // </editor-fold>
}