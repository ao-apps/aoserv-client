package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2007 by AO Industries, Inc.,
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
final public class ExpenseCategory extends CachedObjectStringKey<ExpenseCategory> {

    static final int COLUMN_CODE=0;

    public Object getColumn(int i) {
	if(i==COLUMN_CODE) return pkey;
	throw new IllegalArgumentException("Invalid index: "+i);
    }

    public String getExpenseCode() {
	return pkey;
    }

    protected int getTableIDImpl() {
	return SchemaTable.EXPENSE_CATEGORIES;
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