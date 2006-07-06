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
final public class BankTransactionType extends CachedObjectStringKey<BankTransactionType> {

    static final int COLUMN_NAME=0;

    private String
        display,
        description
    ;

    private boolean isNegative;

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_NAME: return pkey;
            case 1: return display;
            case 2: return description;
            case 3: return isNegative?Boolean.TRUE:Boolean.FALSE;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public String getDescription() {
	return description;
    }

    public String getDisplay() {
	return display;
    }

    public String getName() {
	return pkey;
    }

    protected int getTableIDImpl() {
	return SchemaTable.BANK_TRANSACTION_TYPES;
    }

    void initImpl(ResultSet result) throws SQLException {
	pkey = result.getString(1);
	display = result.getString(2);
	description = result.getString(3);
	isNegative = result.getBoolean(4);
    }

    public boolean isNegative() {
	return isNegative;
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readUTF();
	display=in.readUTF();
	description=in.readUTF();
	isNegative=in.readBoolean();
    }

    String toStringImpl() {
	return display;
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeUTF(pkey);
	out.writeUTF(display);
	out.writeUTF(description);
	out.writeBoolean(isNegative);
    }
}