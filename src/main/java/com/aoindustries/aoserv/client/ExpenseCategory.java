/*
 * Copyright 2000-2013, 2016 by AO Industries, Inc.,
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
 * For AO Industries use only.
 *
 * @author  AO Industries, Inc.
 */
final public class ExpenseCategory extends CachedObjectStringKey<ExpenseCategory> {

	static final int COLUMN_EXPENSE_CODE=0;
	static final String COLUMN_EXPENSE_CODE_name = "expense_code";

	@Override
	Object getColumnImpl(int i) {
		if(i==COLUMN_EXPENSE_CODE) return pkey;
		throw new IllegalArgumentException("Invalid index: "+i);
	}

	public String getExpenseCode() {
		return pkey;
	}

	@Override
		public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.EXPENSE_CATEGORIES;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey=result.getString(1);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readUTF().intern();
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeUTF(pkey);
	}
}
