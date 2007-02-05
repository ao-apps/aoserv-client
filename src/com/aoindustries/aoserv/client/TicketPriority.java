package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
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

    protected int getTableIDImpl() {
	return SchemaTable.TICKET_PRIORITIES;
    }

    void initImpl(ResultSet result) throws SQLException {
	pkey = result.getString(1);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readUTF();
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeUTF(pkey);
    }
}