package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.sql.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * The <code>Ticket</code> system allows clients to submit support
 * requests and monitor the progress of the work.
 *
 * @see  Action
 * @see  TicketPriority
 * @see  TicketType
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class Ticket extends AOServObject<Integer,Ticket> implements SingleTableObject<Integer,Ticket> {

    static final int COLUMN_PKEY=0;
    static final String COLUMN_PKEY_name = "pkey";

    protected AOServTable<Integer,Ticket> table;
    public static final long NO_DEADLINE=-1;

    int pkey;
    String accounting;
    private String created_by;
    private String ticket_type;
    private String details;
    private long open_date;
    private long deadline;
    private long close_date = -1;
    private String closed_by;
    private String client_priority;
    private String admin_priority;
    private String technology;
    private String status;
    private String assigned_to;
    private String contact_emails;
    private String contact_phone_numbers;

    public void actBounceTicket(BusinessAdministrator business_administrator, String comments) {
	table.connector.requestUpdateIL(AOServProtocol.CommandID.BOUNCE_TICKET, pkey, business_administrator.pkey, comments);
    }

    public void actChangeAdminPriority(TicketPriority priority, BusinessAdministrator business_administrator, String comments) {
	table.connector.requestUpdateIL(AOServProtocol.CommandID.CHANGE_TICKET_ADMIN_PRIORITY, pkey, priority==null ? "" : priority.pkey, business_administrator.pkey, comments);
    }

    public void actChangeClientPriority(TicketPriority priority, BusinessAdministrator business_administrator, String comments) {
	table.connector.requestUpdateIL(AOServProtocol.CommandID.CHANGE_TICKET_CLIENT_PRIORITY, pkey, priority.pkey, business_administrator.pkey, comments);
    }

    public void actChangeDeadline(long deadline, BusinessAdministrator business_administrator, String comments) {
	table.connector.requestUpdateIL(AOServProtocol.CommandID.CHANGE_TICKET_DEADLINE, pkey, deadline, business_administrator.pkey, comments);
    }

    public void actChangeTechnology(TechnologyName technology, BusinessAdministrator business_administrator, String comments) {
        try {
            IntList invalidateList;
            AOServConnection connection=table.connector.getConnection();
            try {
                CompressedDataOutputStream out=connection.getOutputStream();
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
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public void actChangeTicketType(TicketType ticket_type, BusinessAdministrator business_administrator, String comments) {
	table.connector.requestUpdateIL(AOServProtocol.CommandID.CHANGE_TICKET_TYPE, pkey, ticket_type.pkey, business_administrator.pkey, comments);
    }

    public void actAssignTo(BusinessAdministrator assignedTo, BusinessAdministrator business_administrator, String comments) {
	table.connector.requestUpdateIL(AOServProtocol.CommandID.SET_TICKET_ASSIGNED_TO, pkey, assignedTo==null?"":assignedTo.getUsername().getUsername(), business_administrator.pkey, comments);
    }

    public void setBusiness(Business business, BusinessAdministrator business_administrator, String comments) {
	table.connector.requestUpdateIL(AOServProtocol.CommandID.SET_TICKET_BUSINESS, pkey, business==null?"":business.getAccounting(), business_administrator.pkey, comments);
    }

    public void actSetContactEmails(String contactEmails, BusinessAdministrator business_administrator, String comments) {
	table.connector.requestUpdateIL(AOServProtocol.CommandID.SET_TICKET_CONTACT_EMAILS, pkey, contactEmails, business_administrator.pkey, comments);
    }

    public void actSetContactPhoneNumbers(String contactPhoneNumbers, BusinessAdministrator business_administrator, String comments) {
	table.connector.requestUpdateIL(AOServProtocol.CommandID.SET_TICKET_CONTACT_PHONE_NUMBERS, pkey, contactPhoneNumbers, business_administrator.pkey, comments);
    }

    public void actCompleteTicket(BusinessAdministrator business_administrator, String comments) {
	table.connector.requestUpdateIL(AOServProtocol.CommandID.COMPLETE_TICKET, pkey, business_administrator.pkey, comments);
    }

    public void actHoldTicket(String comments) {
	table.connector.requestUpdateIL(AOServProtocol.CommandID.HOLD_TICKET, pkey, comments);
    }

    public void actKillTicket(BusinessAdministrator business_administrator, String comments) {
	table.connector.requestUpdateIL(AOServProtocol.CommandID.KILL_TICKET, pkey, business_administrator.pkey, comments);
    }

    public void actReactivateTicket(BusinessAdministrator business_administrator, String comments) {
	table.connector.requestUpdateIL(AOServProtocol.CommandID.REACTIVATE_TICKET, pkey, business_administrator.pkey, comments);
    }

    public void actWorkEntry(BusinessAdministrator business_administrator, String comments) {
	table.connector.requestUpdateIL(AOServProtocol.CommandID.TICKET_WORK, pkey, business_administrator.pkey, comments);
    }

    boolean equalsImpl(Object O) {
	return
            O instanceof Ticket
            && ((Ticket)O).pkey==pkey
	;
    }

    public List<Action> getActions() {
	return table.connector.actions.getActions(this);
    }

    public TicketPriority getAdminPriority() {
        if(admin_priority==null) return null;
	TicketPriority adminPriorityObject = table.connector.ticketPriorities.get(admin_priority);
	if (adminPriorityObject == null) throw new WrappedException(new SQLException("Unable to find Priority: " + admin_priority));
	return adminPriorityObject;
    }

    public BusinessAdministrator getCreatedBy() {
        if(created_by==null) return null;
        // Data may be filtered by APIs
        return table.connector.businessAdministrators.get(created_by);
    }

    public TicketPriority getClientPriority() {
	TicketPriority clientPriorityObject = table.connector.ticketPriorities.get(client_priority);
	if (clientPriorityObject == null) throw new WrappedException(new SQLException("Unable to find Priority: " + client_priority));
	return clientPriorityObject;
    }

    public long getCloseDate() {
	return close_date;
    }

    public BusinessAdministrator getClosedBy() {
	if (closed_by==null) return null;
        Username un=table.connector.usernames.get(closed_by);
        // Data may be filtered by APIs
        if(un==null) return null;
        return un.getBusinessAdministrator();
    }

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case 1: return accounting;
            case 2: return created_by;
            case 3: return ticket_type;
            case 4: return details;
            case 5: return new java.sql.Date(open_date);
            case 6: return deadline==NO_DEADLINE?null:new java.sql.Date(deadline);
            case 7: return close_date==-1?null:new java.sql.Date(close_date);
            case 8: return closed_by;
            case 9: return client_priority;
            case 10: return admin_priority;
            case 11: return technology;
            case 12: return status;
            case 13: return assigned_to;
            case 14: return contact_emails;
            case 15: return contact_phone_numbers;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public long getDeadline() {
	return deadline;
    }

    public String getDetails() {
	return details;
    }

    public long getOpenDate() {
	return open_date;
    }

    public Business getBusiness() {
        if(accounting==null) return null;
	Business bu = table.connector.businesses.get(accounting);
	if (bu == null) throw new WrappedException(new SQLException("Unable to find Business: " + accounting));
	return bu;
    }

    public Integer getKey() {
	return pkey;
    }

    public TicketStatus getStatus() {
	TicketStatus statusObject = table.connector.ticketStatuses.get(status);
	if (statusObject == null) throw new WrappedException(new SQLException("Unable to find status: " + status));
	return statusObject;
    }

    public BusinessAdministrator getAssignedTo() {
	if (assigned_to==null) return null;
        Username un=table.connector.usernames.get(assigned_to);
        // Data may be filtered by APIs
        if(un==null) return null;
        return un.getBusinessAdministrator();
    }

    public String getContactEmails() {
        return contact_emails;
    }
    
    public String getContactPhoneNumbers() {
        return contact_phone_numbers;
    }

    /**
     * Gets the <code>AOServTable</code> that contains this <code>AOServObject</code>.
     *
     * @return  the <code>AOServTable</code>.
     */
    final public AOServTable<Integer,Ticket> getTable() {
	return table;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.TICKETS;
    }

    public TechnologyName getTechnology() {
	if (technology==null) return null;
	TechnologyName technologyObject = table.connector.technologyNames.get(technology);
	if (technologyObject == null) throw new WrappedException(new SQLException("Unable to find technology: " + technology));
	return technologyObject;
    }

    public int getTicketID() {
	return pkey;
    }

    public TicketType getTicketType() {
	TicketType ticketTypeObject = table.connector.ticketTypes.get(ticket_type);
	if (ticketTypeObject  == null) throw new WrappedException(new SQLException("Unable to find TicketType: " + ticket_type));
	return ticketTypeObject;
    }

    int hashCodeImpl() {
	return pkey;
    }

    public void init(ResultSet result) throws SQLException {
	pkey = result.getInt(1);
	accounting = result.getString(2);
	created_by = result.getString(3);
	ticket_type = result.getString(4);
	details = result.getString(5);

	Timestamp temp = result.getTimestamp(6);
	open_date = temp == null ? -1 : temp.getTime();
	temp = result.getTimestamp(7);
	deadline = temp == null ? -1 : temp.getTime();
	temp = result.getTimestamp(8);
	close_date = temp == null ? -1 : temp.getTime();
	
	closed_by = result.getString(9);
	client_priority = result.getString(10);
	admin_priority = result.getString(11);
	technology = result.getString(12);
	status = result.getString(13);
        assigned_to = result.getString(14);
        contact_emails = result.getString(15);
        contact_phone_numbers = result.getString(16);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readCompressedInt();
	accounting=in.readNullUTF();
	created_by=in.readNullUTF();
	ticket_type=in.readUTF().intern();

        // details workaround for readUTF 64k limit
        int len=in.readCompressedInt();
        char[] chars = new char[len];
        for(int c=0;c<chars.length;c++) chars[c]=in.readChar();
	details=new String(chars);

        open_date=in.readLong();
	deadline=in.readLong();
	close_date=in.readLong();
	closed_by=StringUtility.intern(in.readNullUTF());
	client_priority=in.readUTF().intern();
	admin_priority=StringUtility.intern(in.readNullUTF());
	technology=StringUtility.intern(in.readNullUTF());
	status=in.readUTF().intern();
        assigned_to = StringUtility.intern(in.readNullUTF());
        contact_emails = in.readUTF();
        contact_phone_numbers = in.readUTF();
    }

    public void setTable(AOServTable<Integer,Ticket> table) {
	if(this.table!=null) throw new IllegalStateException("table already set");
	this.table=table;
    }

    String toStringImpl() {
	return pkey+"|"+accounting+'|'+status;
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
	out.writeCompressedInt(pkey);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_126)>=0) {
            out.writeNullUTF(accounting);
            out.writeNullUTF(created_by);
        } else {
            out.writeUTF(accounting==null ? "":accounting);
            out.writeUTF(created_by==null ? "":created_by);
        }
	out.writeUTF(ticket_type);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_15)<0) {
            out.writeUTF(details);
        } else {
            // details workaround for readUTF 64k limit
            out.writeCompressedInt(details.length());
            out.writeChars(details);
        }
	out.writeLong(open_date);
	out.writeLong(deadline);
	out.writeLong(close_date);
	out.writeNullUTF(closed_by);
	out.writeUTF(client_priority);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_10)<0) {
            out.writeUTF(admin_priority==null ? client_priority : admin_priority);
        } else {
            out.writeNullUTF(admin_priority);
        }
	out.writeNullUTF(technology);
	out.writeUTF(status);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_125)>=0) {
            out.writeNullUTF(assigned_to);
            out.writeUTF(contact_emails);
            out.writeUTF(contact_phone_numbers);
        }
    }
}