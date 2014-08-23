/*
 * Copyright 2000-2013 by AO Industries, Inc.,
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
 * When using Apache's <code>mod_jk</code>, each connection to a servlet
 * container is assigned a unique two-character identifier.  This
 * identifier is represented by an <code>HttpdJKCode</code>.
 *
 * @see  HttpdWorker
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdJKCode extends GlobalObjectStringKey<HttpdJKCode> {

	static final int COLUMN_CODE=0;
	static final String COLUMN_CODE_name = "code";

	public String getCode() {
		return pkey;
	}

	Object getColumnImpl(int i) {
		if(i==COLUMN_CODE) return pkey;
		throw new IllegalArgumentException("Invalid index: "+i);
	}

	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.HTTPD_JK_CODES;
	}

	public void init(ResultSet result) throws SQLException {
		pkey=result.getString(1);
	}

	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readUTF();
	}

	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeUTF(pkey);
	}
}