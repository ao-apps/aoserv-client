package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.profiler.*;
import java.io.*;
import java.sql.*;

/**
 * All of the types of ticket changes are represented by these
 * <code>ActionType</code>s.
 *
 * @see Action
 * @see Ticket
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class ActionType extends GlobalObjectStringKey<ActionType> {

    static final int COLUMN_TYPE=0;
    static final String COLUMN_DESCRIPTION_name = "description";

    private String description;

    public static final String
        ADMIN_HOLD="AH",
        ADMIN_KILL="AK",
        ADMIN_PRIORITY_CHANGE="AP",
        ASSIGNED="AS",
        BOUNCED="BO",
        CLIENT_HOLD="CH",
        CLIENT_KILLED="CK",
        CLIENT_PRIORITY_CHANGE="CP",
        COMPLETE_TICKET="CO",
        OPEN_TICKET="OP",
        REACTIVATE_TICKET="RE",
        TECHNOLOGY_CHANGE="TC",
        DEADLINE_CHANGE="DL",
        SET_BUSINESS="SB",
        SET_CONTACT_EMAILS="SE",
        SET_CONTACT_PHONES="SP",
        TYPE_CHANGE="TY",
        WORK_ENTRY="WK"
    ;

    @Override
    public Object getColumn(int i) {
        Profiler.startProfile(Profiler.FAST, ActionType.class, "getColValueImpl(int)", null);
        try {
            switch(i) {
                case COLUMN_TYPE: return pkey;
                case 1: return description;
                default: throw new IllegalArgumentException("Invalid index: "+i);
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public String getDescription() {
        return description;
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.ACTION_TYPES;
    }

    public String getType() {
        return pkey;
    }

    void initImpl(ResultSet result) throws SQLException {
        Profiler.startProfile(Profiler.FAST, ActionType.class, "initImpl(ResultSet)", null);
        try {
            pkey = result.getString(1);
            description = result.getString(2);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public void read(CompressedDataInputStream in) throws IOException {
        Profiler.startProfile(Profiler.IO, ActionType.class, "read(CompressedDataInputStream)", null);
        try {
            pkey=in.readUTF().intern();
            description=in.readUTF();
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    String toStringImpl() {
        return description;
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        Profiler.startProfile(Profiler.IO, ActionType.class, "write(CompressedDataOutputStream,String)", null);
        try {
            out.writeUTF(pkey);
            out.writeUTF(description);
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }
}