package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.StringUtility;
import java.io.*;
import java.sql.*;

/**
 * For AO Industries use only.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class Bank extends CachedObjectStringKey<Bank> {

    static final int COLUMN_NAME=0;

    private String
        display
    ;

    public Object getColumn(int i) {
	if(i==COLUMN_NAME) return pkey;
	if(i==1) return display;
	throw new IllegalArgumentException("Invalid index: "+i);
    }

    public String getDisplay() {
	return display;
    }

    public String getName() {
	return pkey;
    }

    protected int getTableIDImpl() {
	return SchemaTable.BANKS;
    }

    void initImpl(ResultSet result) throws SQLException {
	pkey = result.getString(1);
	display = result.getString(2);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readUTF();
	display=in.readUTF();
    }

    String toStringImpl() {
	return display;
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeUTF(pkey);
	out.writeUTF(display);
    }
}