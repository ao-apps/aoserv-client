package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.StringUtility;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * The <code>DNSType</code> associated with a <code>DNSRecord</code> provides
 * details about which values should be used in the destination field, and whether
 * a MX priority should exist.
 *
 * @see  DNSRecord
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class DNSType extends GlobalObjectStringKey<DNSType> {

    static final int COLUMN_TYPE=0;

    /**
     * The possible <code>DNSType</code>s.
     */
    public static final String
        A="A",
        CNAME="CNAME",
        MX="MX",
        NS="NS",
        PTR="PTR",
        TXT="TXT"
    ;

    private String description;
    private boolean
        is_mx,
        param_ip
    ;

    public void checkDestination(String destination) throws IllegalArgumentException {
	checkDestination(destination, param_ip);
    }

    public static void checkDestination(String destination, boolean isParamIP) throws IllegalArgumentException {
	String origDest=destination;
	if(destination.length()==0) throw new IllegalArgumentException("Destination may not by empty");

	if(isParamIP) {
            if(!IPAddress.isValidIPAddress(destination)) throw new IllegalArgumentException("Invalid destination IP address: "+destination);
	} else {
            // May end with a single .
            if(destination.charAt(destination.length()-1)=='.') destination=destination.substring(0, destination.length()-1);
            if(
                !DNSZoneTable.isValidHostnamePart(destination)
                && !EmailDomain.isValidFormat(destination)
            ) throw new IllegalArgumentException("Invalid destination hostname: "+origDest);
	}
    }

    public Object getColumn(int i) {
	if(i==COLUMN_TYPE) return pkey;
	if(i==1) return description;
	if(i==2) return is_mx?Boolean.TRUE:Boolean.FALSE;
	if(i==3) return param_ip?Boolean.TRUE:Boolean.FALSE;
	throw new IllegalArgumentException("Invalid index: "+i);
    }

    public String getDescription() {
	return description;
    }

    protected int getTableIDImpl() {
	return SchemaTable.DNS_TYPES;
    }

    public String getType() {
	return pkey;
    }

    void initImpl(ResultSet result) throws SQLException {
	pkey=result.getString(1);
	description=result.getString(2);
	is_mx=result.getBoolean(3);
	param_ip=result.getBoolean(4);
    }

    public boolean isMX() {
	return is_mx;
    }

    public boolean isParamIP() {
	return param_ip;
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readUTF();
	description=in.readUTF();
	is_mx=in.readBoolean();
	param_ip=in.readBoolean();
    }

    String toStringImpl() {
	return description;
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeUTF(pkey);
	out.writeUTF(description);
	out.writeBoolean(is_mx);
	out.writeBoolean(param_ip);
    }
}