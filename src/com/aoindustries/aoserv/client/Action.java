package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import java.io.*;
import java.sql.*;

/**
 * <code>Action</code>s represent a complete history of the changes that have been made to a ticket.
 * When a ticket is initially created, a series of actions are also made to bring the
 * ticket to its initial state.  When later changes are made, each change is logged
 * separately as an <code>Action</code>.
 *
 * @see  Ticket
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class Action extends AOServObject<Integer,Action> implements SingleTableObject<Integer,Action> {

    static final String COLUMN_TICKET_ID_name = "ticket_id";
    static final String COLUMN_TIME_name = "time";
    static final String COLUMN_PKEY_name = "pkey";

    private int pkey;
    private int ticket_id;
    private String administrator;
    private long time;
    private String action_type;
    private String old_value;
    private String comments;
    protected AOServTable<Integer,Action> table;

    @Override
    boolean equalsImpl(Object O) {
        return
            O instanceof Action
            && ((Action)O).pkey==pkey
        ;
    }

    public ActionType getActionType() throws SQLException {
        ActionType type=table.connector.actionTypes.get(action_type);
        if(type==null) throw new SQLException("Unable to find ActionType: "+action_type);
        return type;
    }

    public BusinessAdministrator getBusinessAdministrator() {
        Username un=table.connector.usernames.get(administrator);
        if(un==null) return null;
        return un.getBusinessAdministrator();
    }

    Object getColumnImpl(int i) {
        switch(i) {
            case 0: return Integer.valueOf(pkey);
            case 1: return Integer.valueOf(ticket_id);
            case 2: return administrator;
            case 3: return new java.sql.Date(time);
            case 4: return action_type;
            case 5: return old_value;
            case 6: return comments;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public String getComments() {
        return comments;
    }

    public String getOldValue() {
        return old_value;
    }

    public int getPkey() {
        return pkey;
    }

    public Integer getKey() {
        return pkey;
    }

    /**
     * Gets the <code>AOServTable</code> that contains this <code>AOServObject</code>.
     *
     * @return  the <code>AOServTable</code>.
     */
    final public AOServTable<Integer,Action> getTable() {
        return table;
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.ACTIONS;
    }

    public Ticket getTicket() throws IOException, SQLException {
        Ticket t = table.connector.tickets.get(ticket_id);
        if(t==null) throw new SQLException("Unable to find Ticket: "+ticket_id);
        return t;
    }

    public long getTime() {
        return time;
    }

    @Override
    int hashCodeImpl() {
        return pkey;
    }

    public void init(ResultSet result) throws SQLException {
        pkey = result.getInt(1);
        ticket_id = result.getInt(2);
        administrator = result.getString(3);
        Timestamp temp = result.getTimestamp(4);
        time = temp == null ? -1 : temp.getTime();
        action_type = result.getString(5);
        old_value = result.getString(6);
        comments = result.getString(7);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        ticket_id=in.readCompressedInt();
        administrator=in.readUTF().intern();
        time=in.readLong();
        action_type=in.readUTF().intern();
        old_value=in.readNullUTF();
        comments=in.readUTF();
    }

    public void setTable(AOServTable<Integer,Action> table) {
        if(this.table!=null) throw new IllegalStateException("table already set");
        this.table=table;
    }

    @Override
    String toStringImpl() {
        return ticket_id+"|"+pkey+'|'+action_type+'|'+administrator;
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeCompressedInt(ticket_id);
        out.writeUTF(administrator);
        out.writeLong(time);
        out.writeUTF(action_type);
        out.writeNullUTF(old_value);
        out.writeUTF(comments);
    }
}