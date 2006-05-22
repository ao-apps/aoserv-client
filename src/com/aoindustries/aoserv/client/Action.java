package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.profiler.*;
import com.aoindustries.util.*;
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

    private int pkey;
    private int ticket_id;
    private String administrator;
    private long time;
    private String action_type;
    private String old_value;
    private String comments;
    protected AOServTable<Integer,Action> table;

    boolean equalsImpl(Object O) {
        Profiler.startProfile(Profiler.FAST, Action.class, "equalsImpl(Object)", null);
        try {
            return
                O instanceof Action
                && ((Action)O).pkey==pkey
            ;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public ActionType getActionType() {
        Profiler.startProfile(Profiler.FAST, Action.class, "getActionType()", null);
        try {
            ActionType type=table.connector.actionTypes.get(action_type);
            if(type==null) throw new WrappedException(new SQLException("Unable to find ActionType: "+action_type));
            return type;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public BusinessAdministrator getBusinessAdministrator() {
        Profiler.startProfile(Profiler.FAST, Action.class, "getBusinessAdministrator()", null);
        try {
            Username un=table.connector.usernames.get(administrator);
            if(un==null) return null;
            return un.getBusinessAdministrator();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public Object getColumn(int i) {
        Profiler.startProfile(Profiler.FAST, Action.class, "getColValueImpl()", null);
        try {
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
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public String getComments() {
        return comments;
    }

    public String getOldValue() {
        return old_value;
    }

    public int getPKey() {
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

    protected int getTableIDImpl() {
        return SchemaTable.ACTIONS;
    }

    public Ticket getTicket() {
        Profiler.startProfile(Profiler.FAST, Action.class, "getTicket()", null);
        try {
            return table.connector.tickets.get(ticket_id);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public long getTime() {
        return time;
    }

    int hashCodeImpl() {
        return pkey;
    }

    public void initImpl(ResultSet result) throws SQLException {
        Profiler.startProfile(Profiler.FAST, Action.class, "initImpl(ResultSet)", null);
        try {
            pkey = result.getInt(1);
            ticket_id = result.getInt(2);
            administrator = result.getString(3);
            Timestamp temp = result.getTimestamp(4);
            time = temp == null ? -1 : temp.getTime();
            action_type = result.getString(5);
            old_value = result.getString(6);
            comments = result.getString(7);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public void read(CompressedDataInputStream in) throws IOException {
        Profiler.startProfile(Profiler.IO, Action.class, "read(CompressedDataInputStream)", null);
        try {
            pkey=in.readCompressedInt();
            ticket_id=in.readCompressedInt();
            administrator=in.readUTF();
            time=in.readLong();
            action_type=in.readUTF();
            old_value=readNullUTF(in);
            comments=in.readUTF();
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public void setTable(AOServTable<Integer,Action> table) {
        Profiler.startProfile(Profiler.FAST, Action.class, "setTable(AOServTable<Integer,Action>)", null);
        try {
            if(this.table!=null) throw new IllegalStateException("table already set");
            this.table=table;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    String toStringImpl() {
        Profiler.startProfile(Profiler.FAST, Action.class, "toStringImpl()", null);
        try {
            return ticket_id+"|"+pkey+'|'+action_type+'|'+administrator;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        Profiler.startProfile(Profiler.IO, Action.class, "write(CompressedDataOutputStream,String)", null);
        try {
            out.writeCompressedInt(pkey);
            out.writeCompressedInt(ticket_id);
            out.writeUTF(administrator);
            out.writeLong(time);
            out.writeUTF(action_type);
            writeNullUTF(out, old_value);
            out.writeUTF(comments);
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }
}