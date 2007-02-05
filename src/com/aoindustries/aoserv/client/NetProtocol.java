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
 * Each <code>NetBind</code> is listening on a <code>NetProtocol</code>.  The
 * protocols include <code>TCP</code>, <code>UDP</code>, and <code>RAW</code>.
 *
 * @see  NetBind
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class NetProtocol extends GlobalObjectStringKey<NetProtocol> {

    static final int COLUMN_PROTOCOL=0;

    public static final String
        RAW="raw",
        UDP="udp",
        TCP="tcp"
    ;

    public Object getColumn(int i) {
	if(i==COLUMN_PROTOCOL) return pkey;
	throw new IllegalArgumentException("Invalid index: "+i);
    }

    public String getProtocol() {
	return pkey;
    }

    protected int getTableIDImpl() {
	return SchemaTable.NET_PROTOCOLS;
    }

    void initImpl(ResultSet result) throws SQLException {
	pkey=result.getString(1);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readUTF();
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeUTF(pkey);
    }
}