package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.StringUtility;
import java.io.*;
import java.sql.*;

/**
 * <code>Ticket</code>s are prioritized by both the client and
 * support personnel.  Each priority is set as a <code>TicketPriority</code>.
 *
 * @see  Ticket
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class TicketPriority extends GlobalObjectStringKey<TicketPriority> {

    static final int COLUMN_PRIORITY=0;
    static final String COLUMN_PRIORITY_name = "priority";

    /**
     * The possible ticket priorities.
     */
    public static final String
        LOW="0-Low",
        NORMAL="1-Normal",
        HIGH="2-High",
        URGENT="3-Urgent"
    ;

    public Object getColumn(int i) {
	if(i==COLUMN_PRIORITY) return pkey;
	throw new IllegalArgumentException("Invalid index: "+i);
    }

    public String getPriority() {
	return pkey;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.TICKET_PRIORITIES;
    }

    public void init(ResultSet result) throws SQLException {
	pkey = result.getString(1);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readUTF().intern();
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
	out.writeUTF(pkey);
    }
}