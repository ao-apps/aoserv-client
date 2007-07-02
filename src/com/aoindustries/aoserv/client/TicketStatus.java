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
 * The <code>TicketStatus</code> of a <code>Ticket</code> changes
 * through each step of its life cycle.
 *
 * @see  Ticket
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class TicketStatus extends GlobalObjectStringKey<TicketStatus> {

    static final int COLUMN_STATUS=0;

    /**
     * The different ticket statuses.
     */
    public static final String
        NEW="New",
        UNDERWAY="Underway",
        BOUNCED="Bounced",
        ADMIN_HOLD="Admin Hold",
        CLIENT_HOLD="Client Hold",
        ADMIN_KILL="Admin Kill",
        CLIENT_KILL="Client Kill",
        COMPLETED="Completed"
    ;

    private String description;

    public Object getColumn(int i) {
	if(i==COLUMN_STATUS) return pkey;
	if(i==1) return description;
	throw new IllegalArgumentException("Invalid index: "+i);
    }

    public String getDescription() {
	return description;
    }

    public String getStatus() {
	return pkey;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.TICKET_STATI;
    }

    void initImpl(ResultSet result) throws SQLException {
	pkey = result.getString(1);
	description = result.getString(2);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readUTF().intern();
	description=in.readUTF();
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeUTF(pkey);
	out.writeUTF(description);
    }
}