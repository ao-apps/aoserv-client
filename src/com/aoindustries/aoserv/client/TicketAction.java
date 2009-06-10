package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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
            case 5: return getOldValue();
            case 6: return from_address;
            case 7: return summary;
            case 8: return getDetails();
            case 9: return getRawEmail();
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public Ticket getTicket() throws IOException, SQLException {
        Ticket t = table.connector.getTickets().get(ticket);
        if(t==null) throw new SQLException("Unable to find Ticket: "+ticket);
        return t;
    }

    public BusinessAdministrator getAdministrator() throws IOException, SQLException {
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

    synchronized public String getOldValue() throws IOException, SQLException {
        if(!oldValueLoaded) {
            old_value = table.connector.requestNullLongStringQuery(true, AOServProtocol.CommandID.GET_TICKET_ACTION_OLD_VALUE, pkey);
            oldValueLoaded = true;
        }
        return old_value;
    }

    synchronized public String getNewValue() throws IOException, SQLException {
        if(!newValueLoaded) {
            new_value = table.connector.requestNullLongStringQuery(true, AOServProtocol.CommandID.GET_TICKET_ACTION_NEW_VALUE, pkey);
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
        return summary!=null ? summary : getTicketActionType().generateSummary(table.connector, userLocale, getOldValue(), getNewValue());
    }

    synchronized public String getDetails() throws IOException, SQLException {
        if(!detailsLoaded) {
            details = table.connector.requestNullLongStringQuery(true, AOServProtocol.CommandID.GET_TICKET_ACTION_DETAILS, pkey);
            detailsLoaded = true;
        }
        return details;
    }

    synchronized public String getRawEmail() throws IOException, SQLException {
        if(!rawEmailLoaded) {
            raw_email = table.connector.requestNullLongStringQuery(true, AOServProtocol.CommandID.GET_TICKET_ACTION_RAW_EMAIL, pkey);
            rawEmailLoaded = true;
        }
        return raw_email;
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.TICKET_ACTIONS;
    }

    public void init(ResultSet result) throws SQLException {
        pkey = result.getInt(1);
        ticket = result.getInt(2);
        administrator = result.getString(3);
        Timestamp temp = result.getTimestamp(4);
        time = temp == null ? -1 : temp.getTime();
        action_type = result.getString(5);
        // Loaded only when needed: old_value = result.getString(6);
        // Loaded only when needed: new_value = result.getString(6);
        from_address = result.getString(6);
        summary = result.getString(7);
        // Loaded only when needed: details = result.getString(9);
        // Loaded only when needed: raw_email = result.getString(10);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey = in.readCompressedInt();
        ticket = in.readCompressedInt();
        administrator = in.readUTF().intern();
        time = in.readLong();
        action_type = in.readUTF().intern();
        from_address = in.readNullUTF();
        summary = in.readNullUTF();
    }

    @Override
    String toStringImpl() {
        return ticket+"|"+pkey+'|'+action_type+'|'+administrator;
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeCompressedInt(ticket);
        out.writeUTF(administrator);
        out.writeLong(time);
        out.writeUTF(action_type);
        out.writeNullUTF(from_address);
        out.writeNullUTF(summary);
    }
}