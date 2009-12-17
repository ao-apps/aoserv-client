package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.util.StringUtility;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;

/**
 * <code>TicketAction</code>s represent a complete history of the changes that have been made to a ticket.
 * When a ticket is initially created it has no actions.  Any change from its initial state will cause
 * an action to be logged.
 *
 * @see  Ticket
 *
 * @author  AO Industries, Inc.
 */
final public class TicketAction extends CachedObjectIntegerKey<TicketAction> {

    static final int
        COLUMN_PKEY=0,
        COLUMN_TICKET=1,
        COLUMN_ADMINISTRATOR=2,
        COLUMN_TIME=3
    ;
    static final String COLUMN_TICKET_name = "ticket";
    static final String COLUMN_TIME_name = "time";
    static final String COLUMN_PKEY_name = "pkey";

    private int ticket;
    private String administrator;
    private long time;
    private String action_type;
    private String old_accounting;
    private String new_accounting;
    private String old_priority;
    private String new_priority;
    private String old_type;
    private String new_type;
    private String old_status;
    private String new_status;
    private String old_assigned_to;
    private String new_assigned_to;
    private int old_category;
    private int new_category;
    private boolean oldValueLoaded;
    private String old_value;
    private boolean newValueLoaded;
    private String new_value;
    private String from_address;
    private String summary;
    private boolean detailsLoaded;
    private String details;
    private boolean rawEmailLoaded;
    private String raw_email;

    Object getColumnImpl(int i) throws IOException, SQLException {
        switch(i) {
            case COLUMN_PKEY: return pkey;
            case COLUMN_TICKET: return ticket;
            case COLUMN_ADMINISTRATOR: return administrator;
            case COLUMN_TIME: return new java.sql.Date(time);
            case 4: return action_type;
            case 5: return old_accounting;
            case 6: return new_accounting;
            case 7: return old_priority;
            case 8: return new_priority;
            case 9: return old_type;
            case 10: return new_type;
            case 11: return old_status;
            case 12: return new_status;
            case 13: return old_assigned_to;
            case 14: return new_assigned_to;
            case 15: return old_category==-1 ? null : Integer.valueOf(old_category);
            case 16: return new_category==-1 ? null : Integer.valueOf(new_category);
            case 17: return getOldValue();
            case 18: return getNewValue();
            case 19: return from_address;
            case 20: return summary;
            case 21: return getDetails();
            case 22: return getRawEmail();
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public Ticket getTicket() throws IOException, SQLException {
        Ticket t = table.connector.getTickets().get(ticket);
        if(t==null) throw new SQLException("Unable to find Ticket: "+ticket);
        return t;
    }

    public BusinessAdministrator getAdministrator() throws IOException, SQLException {
        if(administrator==null) return null;
        return table.connector.getBusinessAdministrators().get(administrator);
    }

    public long getTime() {
        return time;
    }

    public TicketActionType getTicketActionType() throws SQLException, IOException {
        TicketActionType type=table.connector.getTicketActionTypes().get(action_type);
        if(type==null) throw new SQLException("Unable to find TicketActionType: "+action_type);
        return type;
    }

    /**
     * May be null due to filtering
     */
    public Business getOldBusiness() throws IOException, SQLException {
        if(old_accounting==null) return null;
        return table.connector.getBusinesses().get(old_accounting);
    }

    /**
     * May be null due to filtering
     */
    public Business getNewBusiness() throws IOException, SQLException {
        if(new_accounting==null) return null;
        return table.connector.getBusinesses().get(new_accounting);
    }

    public TicketPriority getOldPriority() throws IOException, SQLException {
        if(old_priority==null) return null;
        TicketPriority tp = table.connector.getTicketPriorities().get(old_priority);
        if(tp==null) throw new SQLException("Unable to find TicketPriority: "+old_priority);
        return tp;
    }

    public TicketPriority getNewPriority() throws IOException, SQLException {
        if(new_priority==null) return null;
        TicketPriority tp = table.connector.getTicketPriorities().get(new_priority);
        if(tp==null) throw new SQLException("Unable to find TicketPriority: "+new_priority);
        return tp;
    }

    public TicketType getOldType() throws IOException, SQLException {
        if(old_type==null) return null;
        TicketType tt = table.connector.getTicketTypes().get(old_type);
        if(tt==null) throw new SQLException("Unable to find TicketType: "+old_type);
        return tt;
    }

    public TicketType getNewType() throws IOException, SQLException {
        if(new_type==null) return null;
        TicketType tt = table.connector.getTicketTypes().get(new_type);
        if(tt==null) throw new SQLException("Unable to find TicketType: "+new_type);
        return tt;
    }

    public TicketStatus getOldStatus() throws IOException, SQLException {
        if(old_status==null) return null;
        TicketStatus ts = table.connector.getTicketStatuses().get(old_status);
        if(ts==null) throw new SQLException("Unable to find TicketStatus: "+old_status);
        return ts;
    }

    public TicketStatus getNewStatus() throws IOException, SQLException {
        if(new_status==null) return null;
        TicketStatus ts = table.connector.getTicketStatuses().get(new_status);
        if(ts==null) throw new SQLException("Unable to find TicketStatus: "+new_status);
        return ts;
    }

    /**
     * May be null due to filtering
     */
    public BusinessAdministrator getOldAssignedTo() throws IOException, SQLException {
        if(old_assigned_to==null) return null;
        return table.connector.getBusinessAdministrators().get(old_assigned_to);
    }

    /**
     * May be null due to filtering
     */
    public BusinessAdministrator getNewAssignedTo() throws IOException, SQLException {
        if(new_assigned_to==null) return null;
        return table.connector.getBusinessAdministrators().get(new_assigned_to);
    }

    public TicketCategory getOldCategory() throws IOException, SQLException {
        if(old_category==-1) return null;
        TicketCategory tc = table.connector.getTicketCategories().get(old_category);
        if(tc==null) throw new SQLException("Unable to find TicketCategory: "+old_category);
        return tc;
    }

    public TicketCategory getNewCategory() throws IOException, SQLException {
        if(new_category==-1) return null;
        TicketCategory tc = table.connector.getTicketCategories().get(new_category);
        if(tc==null) throw new SQLException("Unable to find TicketCategory: "+new_category);
        return tc;
    }

    synchronized public String getOldValue() throws IOException, SQLException {
        if(!oldValueLoaded) {
            // Only perform the query for action types that have old values
            if(
                action_type.equals(TicketActionType.SET_CONTACT_EMAILS)
                || action_type.equals(TicketActionType.SET_CONTACT_PHONE_NUMBERS)
                || action_type.equals(TicketActionType.SET_SUMMARY)
                || action_type.equals(TicketActionType.SET_INTERNAL_NOTES)
            ) {
                old_value = table.connector.requestNullLongStringQuery(true, AOServProtocol.CommandID.GET_TICKET_ACTION_OLD_VALUE, pkey);
            } else {
                old_value = null;
            }
            oldValueLoaded = true;
        }
        return old_value;
    }

    synchronized public String getNewValue() throws IOException, SQLException {
        if(!newValueLoaded) {
            // Only perform the query for action types that have new values
            if(
                action_type.equals(TicketActionType.SET_CONTACT_EMAILS)
                || action_type.equals(TicketActionType.SET_CONTACT_PHONE_NUMBERS)
                || action_type.equals(TicketActionType.SET_SUMMARY)
                || action_type.equals(TicketActionType.SET_INTERNAL_NOTES)
            ) {
                new_value = table.connector.requestNullLongStringQuery(true, AOServProtocol.CommandID.GET_TICKET_ACTION_NEW_VALUE, pkey);
            } else {
                new_value = null;
            }
            newValueLoaded = true;
        }
        return new_value;
    }

    public String getFromAddress() {
        return from_address;
    }

    /**
     * Gets the summary for the provided Locale, may be generated for certain action types.
     */
    public String getSummary(Locale userLocale) throws IOException, SQLException {
        if(summary!=null) return summary;
        final String oldValue;
        final String newValue;
        if(action_type.equals(TicketActionType.SET_BUSINESS)) {
            oldValue = old_accounting;
            newValue = new_accounting;
        } else if(
            action_type.equals(TicketActionType.SET_CLIENT_PRIORITY)
            || action_type.equals(TicketActionType.SET_ADMIN_PRIORITY)
        ) {
            oldValue = old_priority;
            newValue = new_priority;
        } else if(action_type.equals(TicketActionType.SET_TYPE)) {
            oldValue = getOldType().toStringImpl(userLocale);
            newValue = getNewType().toStringImpl(userLocale);
        } else if(action_type.equals(TicketActionType.SET_STATUS)) {
            oldValue = getOldStatus().toStringImpl(userLocale);
            newValue = getNewStatus().toStringImpl(userLocale);
        } else if(action_type.equals(TicketActionType.ASSIGN)) {
            BusinessAdministrator oldAssignedTo = getOldAssignedTo();
            BusinessAdministrator newAssignedTo = getNewAssignedTo();
            oldValue = oldAssignedTo!=null ? oldAssignedTo.getName() : old_assigned_to!=null ? ApplicationResources.accessor.getMessage(userLocale, "TicketAction.old_assigned_to.filtered") : null;
            newValue = newAssignedTo!=null ? newAssignedTo.getName() : new_assigned_to!=null ? ApplicationResources.accessor.getMessage(userLocale, "TicketAction.new_assigned_to.filtered") : null;
        } else if(action_type.equals(TicketActionType.SET_CATEGORY)) {
            TicketCategory oldCategory = getOldCategory();
            TicketCategory newCategory = getNewCategory();
            oldValue = oldCategory!=null ? oldCategory.toStringImpl(userLocale) : null;
            newValue = newCategory!=null ? newCategory.toStringImpl(userLocale) : null;
        } else if(
            action_type.equals(TicketActionType.SET_CONTACT_EMAILS)
            || action_type.equals(TicketActionType.SET_CONTACT_PHONE_NUMBERS)
            || action_type.equals(TicketActionType.SET_SUMMARY)
            || action_type.equals(TicketActionType.SET_INTERNAL_NOTES)
            || action_type.equals(TicketActionType.ADD_ANNOTATION)
        ) {
            // These either have no old/new value or their value is not altered in any way
            oldValue = getOldValue();
            newValue = getNewValue();
        } else {
            throw new SQLException("Unexpected value for action_type: "+action_type);
        }
        return getTicketActionType().generateSummary(table.connector, userLocale, oldValue, newValue);
    }

    synchronized public String getDetails() throws IOException, SQLException {
        if(!detailsLoaded) {
            // Only perform the query for action types that have details
            if(action_type.equals(TicketActionType.ADD_ANNOTATION)) {
                details = table.connector.requestNullLongStringQuery(true, AOServProtocol.CommandID.GET_TICKET_ACTION_DETAILS, pkey);
            } else {
                details = null;
            }
            detailsLoaded = true;
        }
        return details;
    }

    synchronized public String getRawEmail() throws IOException, SQLException {
        if(!rawEmailLoaded) {
            // Only perform the query for action types that may have raw email
            if(action_type.equals(TicketActionType.ADD_ANNOTATION)) {
                raw_email = table.connector.requestNullLongStringQuery(true, AOServProtocol.CommandID.GET_TICKET_ACTION_RAW_EMAIL, pkey);
            } else {
                raw_email = null;
            }
            rawEmailLoaded = true;
        }
        return raw_email;
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.TICKET_ACTIONS;
    }

    public void init(ResultSet result) throws SQLException {
        int pos = 1;
        pkey = result.getInt(pos++);
        ticket = result.getInt(pos++);
        administrator = result.getString(pos++);
        Timestamp temp = result.getTimestamp(pos++);
        time = temp == null ? -1 : temp.getTime();
        action_type = result.getString(pos++);
        old_accounting = result.getString(pos++);
        new_accounting = result.getString(pos++);
        old_priority = result.getString(pos++);
        new_priority = result.getString(pos++);
        old_type = result.getString(pos++);
        new_type = result.getString(pos++);
        old_status = result.getString(pos++);
        new_status = result.getString(pos++);
        old_assigned_to = result.getString(pos++);
        new_assigned_to = result.getString(pos++);
        old_category = result.getInt(pos++);
        if(result.wasNull()) old_category = -1;
        new_category = result.getInt(pos++);
        if(result.wasNull()) new_category = -1;
        // Loaded only when needed: old_value = result.getString(pos++);
        // Loaded only when needed: new_value = result.getString(pos++);
        from_address = result.getString(pos++);
        summary = result.getString(pos++);
        // Loaded only when needed: details = result.getString(pos++);
        // Loaded only when needed: raw_email = result.getString(pos++);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey = in.readCompressedInt();
        ticket = in.readCompressedInt();
        administrator = StringUtility.intern(in.readNullUTF());
        time = in.readLong();
        action_type = in.readUTF().intern();
        old_accounting = StringUtility.intern(in.readNullUTF());
        new_accounting = StringUtility.intern(in.readNullUTF());
        old_priority = StringUtility.intern(in.readNullUTF());
        new_priority = StringUtility.intern(in.readNullUTF());
        old_type = StringUtility.intern(in.readNullUTF());
        new_type = StringUtility.intern(in.readNullUTF());
        old_status = StringUtility.intern(in.readNullUTF());
        new_status = StringUtility.intern(in.readNullUTF());
        old_assigned_to = StringUtility.intern(in.readNullUTF());
        new_assigned_to = StringUtility.intern(in.readNullUTF());
        old_category = in.readCompressedInt();
        new_category = in.readCompressedInt();
        // Loaded only when needed: old_value
        // Loaded only when needed: new_value
        from_address = in.readNullUTF();
        summary = in.readNullUTF();
        // Loaded only when needed: details
        // Loaded only when needed: raw_email
    }

    public List<? extends AOServObject> getDependencies() throws IOException, SQLException {
        return createDependencyList(
            getTicket(),
            getAdministrator(),
            getOldBusiness(),
            getNewBusiness(),
            getOldAssignedTo(),
            getNewAssignedTo(),
            getOldCategory(),
            getNewCategory()
        );
    }

    public List<? extends AOServObject> getDependentObjects() throws IOException, SQLException {
        return createDependencyList(
        );
    }

    @Override
    String toStringImpl(Locale userLocale) {
        return ticket+"|"+pkey+'|'+action_type+'|'+administrator;
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeCompressedInt(ticket);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_50)>=0) out.writeNullUTF(administrator);
        else out.writeUTF(administrator==null ? "aoadmin" : administrator);
        out.writeLong(time);
        out.writeUTF(action_type);
        out.writeNullUTF(old_accounting);
        out.writeNullUTF(new_accounting);
        out.writeNullUTF(old_priority);
        out.writeNullUTF(new_priority);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_49)>=0) {
            out.writeNullUTF(old_type);
            out.writeNullUTF(new_type);
        }
        out.writeNullUTF(old_status);
        out.writeNullUTF(new_status);
        out.writeNullUTF(old_assigned_to);
        out.writeNullUTF(new_assigned_to);
        out.writeCompressedInt(old_category);
        out.writeCompressedInt(new_category);
        // Loaded only when needed: old_value
        // Loaded only when needed: new_value
        out.writeNullUTF(from_address);
        out.writeNullUTF(summary);
        // Loaded only when needed: details
        // Loaded only when needed: raw_email
    }
}
