package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import java.io.*;
import java.sql.*;

/**
 * A <code>MySQLReservedWord</code> cannot be used for database or
 * table names.
 *
 * @see  MySQLDatabase
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class MySQLReservedWord extends GlobalObjectStringKey<MySQLReservedWord> {

    static final int COLUMN_WORD=0;

    public Object getColumn(int i) {
	if(i==COLUMN_WORD) return pkey;
	throw new IllegalArgumentException("Invalid index: "+i);
    }

    protected int getTableIDImpl() {
	return SchemaTable.MYSQL_RESERVED_WORDS;
    }

    public String getWord() {
	return pkey;
    }

    void initImpl(ResultSet results) throws SQLException {
	pkey=results.getString(1);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readUTF();
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeUTF(pkey);
    }
}