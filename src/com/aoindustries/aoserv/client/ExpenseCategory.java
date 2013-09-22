/*
 * Copyright 2000-2013 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.io.*;
import java.io.*;
import java.sql.*;

/**
 * For AO Industries use only.
 *
 * @author  AO Industries, Inc.
 */
final public class ExpenseCategory extends CachedObjectStringKey<ExpenseCategory> {

    static final int COLUMN_EXPENSE_CODE=0;
    static final String COLUMN_EXPENSE_CODE_name = "expense_code";

    Object getColumnImpl(int i) {
	if(i==COLUMN_EXPENSE_CODE) return pkey;
	throw new IllegalArgumentException("Invalid index: "+i);
    }

    public String getExpenseCode() {
	return pkey;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.EXPENSE_CATEGORIES;
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