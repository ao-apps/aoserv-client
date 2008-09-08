package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import java.io.*;
import java.sql.*;

/**
 * A <code>PostgresReservedWord</code> cannot be used for database or
 * table names.
 *
 * @see  PostgresDatabase
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class PostgresReservedWord extends GlobalObjectStringKey<PostgresReservedWord> {

    static final int COLUMN_WORD=0;
    static final String COLUMN_WORD_name = "word";

    public Object getColumn(int i) {
	if(i==COLUMN_WORD) return pkey;
	throw new IllegalArgumentException("Invalid index: "+i);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.POSTGRES_RESERVED_WORDS;
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