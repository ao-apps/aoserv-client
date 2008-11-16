package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;

/**
 * A <code>CountryCode</code> is a simple wrapper for country
 * code and name mappings.  Each code is a two-digit ISO 3166-1 alpha-2 country
 * code.
 *
 * See <a href="http://en.wikipedia.org/wiki/ISO_3166-1_alpha-2">http://en.wikipedia.org/wiki/ISO_3166-1_alpha-2</a>
 *
 * @author  AO Industries, Inc.
 */
final public class CountryCode extends GlobalObjectStringKey<CountryCode> {

    static final int COLUMN_CODE=0;
    static final String COLUMN_NAME_name = "name";

    /**
     * <code>CountryCode</code>s used as constants.
     */
    public static final String US="US";

    private String name;
    private boolean charge_com_supported;
    private String charge_com_name;

    /**
     * Gets the two-character unique code for this country.
     */
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

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.COUNTRY_CODES;
    }

    public void init(ResultSet result) throws SQLException {
	pkey = result.getString(1);
	name = result.getString(2);
        charge_com_supported = result.getBoolean(3);
        charge_com_name = result.getString(4);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readUTF().intern();
	name=in.readUTF();
        charge_com_supported = in.readBoolean();
        charge_com_name = in.readNullUTF();
    }

    String toStringImpl() {
	return getName();
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
	out.writeUTF(pkey);
	out.writeUTF(name);
        out.writeBoolean(charge_com_supported);
        out.writeBoolean(charge_com_name!=null);
        if (charge_com_name!=null) out.writeUTF(charge_com_name);
    }
}