/*
 * Copyright 2001-2009, 2016 by AO Industries, Inc.,
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
 * A <code>MySQLReservedWord</code> cannot be used for database or
 * table names.
 *
 * @see  MySQLDatabase
 *
 * @author  AO Industries, Inc.
 */
final public class MySQLReservedWord extends GlobalObjectStringKey<MySQLReservedWord> {

	static final int COLUMN_WORD=0;
	static final String COLUMN_WORD_name = "word";

	@Override
	Object getColumnImpl(int i) {
		if(i==COLUMN_WORD) return pkey;
		throw new IllegalArgumentException("Invalid index: "+i);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.MYSQL_RESERVED_WORDS;
	}

	public String getWord() {
		return pkey;
	}

	@Override
	public void init(ResultSet results) throws SQLException {
		pkey=results.getString(1);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readUTF();
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeUTF(pkey);
	}
}
