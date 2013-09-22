/*
 * Copyright 2001-2013 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Each <code>NetBind</code> is listening on a <code>NetProtocol</code>.  The
 * protocols include <code>TCP</code>, <code>UDP</code>, and <code>RAW</code>.
 *
 * @see  NetBind
 *
 * @author  AO Industries, Inc.
 */
final public class NetProtocol extends GlobalObjectStringKey<NetProtocol> {

	static final int COLUMN_PROTOCOL=0;
	static final String COLUMN_PROTOCOL_name = "protocol";

	public static final String
		RAW="raw",
		UDP="udp",
		TCP="tcp"
	;

	Object getColumnImpl(int i) {
		if(i==COLUMN_PROTOCOL) return pkey;
		throw new IllegalArgumentException("Invalid index: "+i);
	}

	public String getProtocol() {
		return pkey;
	}

	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.NET_PROTOCOLS;
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