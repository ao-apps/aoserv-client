package com.aoindustries.aoserv.client;

/*
 * Copyright 2005-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.sql.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;

/**
 * A <code>TransactionType</code> is one type that may be used
 * in a <code>Transaction</code>.  Each <code>PackageDefinition</code>
 * and <code>PackageDefinitionLimit</code> defines which type will be
 * used for billing.
 *
 * @see  PackageDefinition
 * @see  PackageDefinitionLimit
 * @see  Transaction
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
public final class TransactionType extends GlobalObjectStringKey<TransactionType> {

    static final int COLUMN_NAME=0;

    public static final String
        HTTPD="httpd",
        PAYMENT="payment",
        VIRTUAL="virtual"
    ;

    private String
        display,
        description,
        unit
    ;

    /**
     * If <code>true</code> this <code>TransactionType</code> represents a credit to
     * an account and will be listed in payments received reports.
     */
    private boolean isCredit;

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_NAME: return pkey;
            case 1: return display;
            case 2: return description;
            case 3: return unit;
            case 4: return isCredit?Boolean.TRUE:Boolean.FALSE;
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
	return SchemaTable.TRANSACTION_TYPES;
    }

    public String getUnit() {
	return unit;
    }

    void initImpl(ResultSet result) throws SQLException {
	pkey = result.getString(1);
	display = result.getString(2);
	description = result.getString(3);
	unit = result.getString(4);
	isCredit = result.getBoolean(5);
    }

    public boolean isCredit() {
	return isCredit;
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readUTF();
	display=in.readUTF();
	description=in.readUTF();
	unit=in.readUTF();
	isCredit=in.readBoolean();
    }

    String toStringImpl() {
	return display;
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeUTF(pkey);
	out.writeUTF(display);
	out.writeUTF(description);
	out.writeUTF(unit);
	out.writeBoolean(isCredit);
    }
}
