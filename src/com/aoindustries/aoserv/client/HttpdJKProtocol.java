package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import java.io.*;
import java.sql.*;

/**
 * Apache's <code>mod_jk</code> supports multiple versions of the
 * Apache JServ Protocol.  Both Apache and Tomcat must be using
 * the same protocol for communication.  The protocol is represented
 * by an <code>HttpdJKProtocol</code>.
 *
 * @see  HttpdWorker
 * @see  Protocol
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdJKProtocol extends GlobalObjectStringKey<HttpdJKProtocol> {

    static final int COLUMN_PROTOCOL=0;
    static final String COLUMN_PROTOCOL_name = "protocol";

    public static final String
        AJP12="ajp12",
        AJP13="ajp13"
    ;

    Object getColumnImpl(int i) {
	if(i==COLUMN_PROTOCOL) return pkey;
	throw new IllegalArgumentException("Invalid index: "+i);
    }

    public Protocol getProtocol(AOServConnector connector) throws SQLException {
	Protocol protocol=connector.protocols.get(pkey);
	if(protocol==null) throw new SQLException("Unable to find Protocol: "+pkey);
	return protocol;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.HTTPD_JK_PROTOCOLS;
    }

    public void init(ResultSet result) throws SQLException {
	pkey=result.getString(1);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readUTF().intern();
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
	out.writeUTF(pkey);
    }
}