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
    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_TYPE: return pkey;
            case 1: return description;
            default: throw new IllegalArgumentException("Invalid index: "+i);
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

    public void init(ResultSet result) throws SQLException {
        pkey = result.getString(1);
        description = result.getString(2);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readUTF().intern();
        description=in.readUTF();
    }

    @Override
    String toStringImpl() {
        return description;
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeUTF(pkey);
        out.writeUTF(description);
    }
}