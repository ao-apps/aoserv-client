package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;

/**
 * A <code>CountryCode</code> is a simple wrapper for country
 * code and name mappings.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class CountryCode extends GlobalObjectStringKey<CountryCode> {

    static final int COLUMN_CODE=0;

    /**
     * <code>CountryCode</code>s used as constants.
     */
    public static final String US="US";

    private String name;
    private boolean charge_com_supported;
    private String charge_com_name;

    public String getCode() {
	return pkey;
    }

    public Object getColumn(int i) {
	if(i==COLUMN_CODE) return pkey;
	if(i==1) return name;
        if(i==2) return charge_com_supported?Boolean.TRUE:Boolean.FALSE;
        if(i==3) return charge_com_name;
	throw new IllegalArgumentException("Invalid index: "+i);
    }

    public String getName() {
	return name;
    }

    public boolean getChargeComSupported() {
	return charge_com_supported;
    }

    public String getChargeComName() {
	return charge_com_name==null?name:charge_com_name;
    }

    protected int getTableIDImpl() {
	return SchemaTable.COUNTRY_CODES;
    }

    void initImpl(ResultSet result) throws SQLException {
	pkey = result.getString(1);
	name = result.getString(2);
        charge_com_supported = result.getBoolean(3);
        charge_com_name = result.getString(4);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readUTF();
	name=in.readUTF();
        charge_com_supported = in.readBoolean();
        charge_com_name = in.readBoolean()?in.readUTF():null;
    }

    String toStringImpl() {
	return getName();
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeUTF(pkey);
	out.writeUTF(name);
        out.writeBoolean(charge_com_supported);
        out.writeBoolean(charge_com_name!=null);
        if (charge_com_name!=null) out.writeUTF(charge_com_name);
    }
}