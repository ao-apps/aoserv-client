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
        return pkey+"|"+accounting+'|'+status+"->"+reseller;
    }
    // </editor-fold>

    // <editor-fold desc="AOServObject implementation">
    static final int COLUMN_PKEY = 0;
    static final int COLUMN_ACCOUNTING = 2;
    static final int COLUMN_CREATED_BY = 4;
    static final String COLUMN_PKEY_name = "pkey";

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case 1: return reseller;
            case COLUMN_ACCOUNTING: return accounting;
            case 3: return language;
            case COLUMN_CREATED_BY: return created_by;
            case 5: return category==-1 ? null : category;
            case 6: return ticket_type;
            case 7: return from_address;
            case 8: return summary;
            case 9: return getDetails();
            case 10: return getRawEmail();
            case 11: return new java.sql.Date(open_date);
            case 12: return client_priority;
            case 13: return admin_priority;
            case 14: return status;
            case 15: return status_timeout==-1 ? null : new java.sql.Date(status_timeout);
            case 16: return contact_emails;
            case 17: return contact_phone_numbers;
            case 18: return getInternalNotes();
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.TICKETS;
    }

    public void init(ResultSet result) throws SQLException {
        int pos = 1;
        pkey = result.getInt(pos++);
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

    public Reseller getReseller() throws SQLException {
        Reseller re = table.connector.getResellers().get(reseller);
        if (re == null) throw new SQLException("Unable to find Reseller: " + reseller);
        return re;
    }

    public Business getBusiness() throws SQLException {
        if(accounting==null) return null;
        Business bu = table.connector.getBusinesses().get(accounting);
        if (bu == null) throw new SQLException("Unable to find Business: " + accounting);
        return bu;
    }

    public Language getLanguage() throws SQLException {
        Language la = table.connector.getLanguages().get(language);
        if(la==null) throw new SQLException("Unable to find Language: "+language);
        return la;
    }

    public BusinessAdministrator getCreatedBy() {
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

    synchronized public String getDetails() throws IOException {
        if(!detailsLoaded) {
            details = table.connector.requestNullLongStringQuery(AOServProtocol.CommandID.GET_TICKET_DETAILS, pkey);
            detailsLoaded = true;
        }
        return details;
    }

    synchronized public String getRawEmail() throws IOException {
        if(!rawEmailLoaded) {
            raw_email = table.connector.requestNullLongStringQuery(AOServProtocol.CommandID.GET_TICKET_RAW_EMAIL, pkey);
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

    synchronized public String getInternalNotes() throws IOException {
        if(!internalNotesLoaded) {
            internal_notes = table.connector.requestLongStringQuery(AOServProtocol.CommandID.GET_TICKET_INTERNAL_NOTES, pkey);
            detailsLoaded = true;
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
    public void actBounceTicket(BusinessAdministrator business_administrator, String comments) throws IOException, SQLException {
        table.connector.requestUpdateIL(AOServProtocol.CommandID.BOUNCE_TICKET, pkey, business_administrator.pkey, comments);
    }

    public void actChangeAdminPriority(TicketPriority priority, BusinessAdministrator business_administrator, String comments) throws IOException, SQLException {
        table.connector.requestUpdateIL(AOServProtocol.CommandID.CHANGE_TICKET_ADMIN_PRIORITY, pkey, priority==null ? "" : priority.pkey, business_administrator.pkey, comments);
    }

    public void actChangeClientPriority(TicketPriority priority, BusinessAdministrator business_administrator, String comments) throws IOException, SQLException {
        table.connector.requestUpdateIL(AOServProtocol.CommandID.CHANGE_TICKET_CLIENT_PRIORITY, pkey, priority.pkey, business_administrator.pkey, comments);
    }

    public void actChangeDeadline(long deadline, BusinessAdministrator business_administrator, String comments) throws IOException, SQLException {
        table.connector.requestUpdateIL(AOServProtocol.CommandID.CHANGE_TICKET_DEADLINE, pkey, deadline, business_administrator.pkey, comments);
    }

    public void actChangeTechnology(TechnologyName technology, BusinessAdministrator business_administrator, String comments) throws IOException, SQLException {
        IntList invalidateList;
        AOServConnection connection=table.connector.getConnection();
        try {
            CompressedDataOutputStream out = connection.getOutputStream();
            out.writeCompressedInt(AOServProtocol.CommandID.CHANGE_TICKET_TECHNOLOGY.ordinal());
            out.writeCompressedInt(pkey);
            out.writeBoolean(technology!=null);
            if(technology!=null) out.writeUTF(technology.pkey);
            out.writeUTF(business_administrator.pkey);
            out.writeUTF(comments);
            out.flush();

            CompressedDataInputStream in=connection.getInputStream();
            int code=in.readByte();
            if(code==AOServProtocol.DONE) invalidateList=AOServConnector.readInvalidateList(in);
            else {
                AOServProtocol.checkResult(code, in);
                throw new IOException("Unexpected response code: "+code);
            }
        } catch(IOException err) {
            connection.close();
            throw err;
        } finally {
            table.connector.releaseConnection(connection);
        }
        table.connector.tablesUpdated(invalidateList);
    }

    public void actChangeTicketType(TicketType ticket_type, BusinessAdministrator business_administrator, String comments) throws IOException, SQLException {
        table.connector.requestUpdateIL(AOServProtocol.CommandID.CHANGE_TICKET_TYPE, pkey, ticket_type.pkey, business_administrator.pkey, comments);
    }

    public void actAssignTo(BusinessAdministrator assignedTo, BusinessAdministrator business_administrator, String comments) throws IOException, SQLException {
        table.connector.requestUpdateIL(AOServProtocol.CommandID.SET_TICKET_ASSIGNED_TO, pkey, assignedTo==null?"":assignedTo.getUsername().getUsername(), business_administrator.pkey, comments);
    }

    public void actSetContactEmails(String contactEmails, BusinessAdministrator business_administrator, String comments) throws IOException, SQLException {
        table.connector.requestUpdateIL(AOServProtocol.CommandID.SET_TICKET_CONTACT_EMAILS, pkey, contactEmails, business_administrator.pkey, comments);
    }

    public void actSetContactPhoneNumbers(String contactPhoneNumbers, BusinessAdministrator business_administrator, String comments) throws IOException, SQLException {
        table.connector.requestUpdateIL(AOServProtocol.CommandID.SET_TICKET_CONTACT_PHONE_NUMBERS, pkey, contactPhoneNumbers, business_administrator.pkey, comments);
    }

    public void actCompleteTicket(BusinessAdministrator business_administrator, String comments) throws IOException, SQLException {
        table.connector.requestUpdateIL(AOServProtocol.CommandID.COMPLETE_TICKET, pkey, business_administrator.pkey, comments);
    }

    public void actHoldTicket(String comments) throws IOException, SQLException {
        table.connector.requestUpdateIL(AOServProtocol.CommandID.HOLD_TICKET, pkey, comments);
    }

    public void actKillTicket(BusinessAdministrator business_administrator, String comments) throws IOException, SQLException {
        table.connector.requestUpdateIL(AOServProtocol.CommandID.KILL_TICKET, pkey, business_administrator.pkey, comments);
    }

    public void actReactivateTicket(BusinessAdministrator business_administrator, String comments) throws IOException, SQLException {
        table.connector.requestUpdateIL(AOServProtocol.CommandID.REACTIVATE_TICKET, pkey, business_administrator.pkey, comments);
    }

    public void actWorkEntry(BusinessAdministrator business_administrator, String comments) throws IOException, SQLException {
        table.connector.requestUpdateIL(AOServProtocol.CommandID.TICKET_WORK, pkey, business_administrator.pkey, comments);
    }

    public void setBusiness(Business business, BusinessAdministrator business_administrator, String comments) throws IOException, SQLException {
        table.connector.requestUpdateIL(AOServProtocol.CommandID.SET_TICKET_BUSINESS, pkey, business==null?"":business.getAccounting(), business_administrator.pkey, comments);
    }
    // </editor-fold>
}