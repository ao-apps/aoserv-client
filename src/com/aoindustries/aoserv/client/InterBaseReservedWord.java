package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;

/**
 * An <code>InterBaseReservedWord</code> cannot be used for table names.
 *
 * @see  InterBaseDatabase
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class InterBaseReservedWord extends GlobalObjectStringKey<InterBaseReservedWord> {

    static final int COLUMN_WORD=0;

    public Object getColumn(int i) {
	if(i==COLUMN_WORD) return pkey;
	throw new IllegalArgumentException("Invalid index: "+i);
    }

    protected int getTableIDImpl() {
	return SchemaTable.INTERBASE_RESERVED_WORDS;
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