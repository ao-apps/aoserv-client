package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.StringUtility;
import java.io.*;
import java.sql.*;
import java.util.List;

/**
 * For AO Industries use only.
 *
 * @version  1.0a
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

    public List<AOServObject> getDependencies() throws IOException, SQLException {
        return createDependencyList(
        );
    }

    public List<AOServObject> getDependentObjects() throws IOException, SQLException {
        return createDependencyList(
        );
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
    	out.writeUTF(pkey);
    }
}