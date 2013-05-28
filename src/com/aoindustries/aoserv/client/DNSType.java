/*
 * Copyright 2001-2013 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.InetAddress;
import com.aoindustries.aoserv.client.validator.ValidationException;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The <code>DNSType</code> associated with a <code>DNSRecord</code> provides
 * details about which values should be used in the destination field, and whether
 * a MX priority should exist.
 *
 * @see  DNSRecord
 *
 * @author  AO Industries, Inc.
 */
final public class DNSType extends GlobalObjectStringKey<DNSType> {

	static final int COLUMN_TYPE=0;
	static final String COLUMN_DESCRIPTION_name = "description";

	/**
	 * The possible <code>DNSType</code>s.
	 */
	public static final String
		A="A",
		AAAA="AAAA",
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
		checkDestination(pkey, destination);
	}

	public static void checkDestination(String type, String destination) throws IllegalArgumentException {
		String origDest=destination;
		if(destination.length()==0) throw new IllegalArgumentException("Destination may not by empty");

		if(type.equals(A)) {
			try {
				InetAddress parsed = InetAddress.valueOf(destination);
				if(!parsed.isIPv4()) throw new IllegalArgumentException("A type requires IPv4 address: "+destination);
			} catch(ValidationException e) {
				throw new IllegalArgumentException(e.getLocalizedMessage(), e);
			}
		} else if(type.equals(AAAA)) {
			try {
				InetAddress parsed = InetAddress.valueOf(destination);
				if(!parsed.isIPv6()) throw new IllegalArgumentException("AAAA type requires IPv6 address: "+destination);
			} catch(ValidationException e) {
				throw new IllegalArgumentException(e.getLocalizedMessage(), e);
			}
		} else {
			// May end with a single .
			if(destination.charAt(destination.length()-1)=='.') destination=destination.substring(0, destination.length()-1);
			if(
				!DNSZoneTable.isValidHostnamePart(destination)
				&& !EmailDomain.isValidFormat(destination)
			) throw new IllegalArgumentException("Invalid destination hostname: "+origDest);
		}
	}

	Object getColumnImpl(int i) {
		if(i==COLUMN_TYPE) return pkey;
		if(i==1) return description;
		if(i==2) return is_mx?Boolean.TRUE:Boolean.FALSE;
		if(i==3) return param_ip?Boolean.TRUE:Boolean.FALSE;
		throw new IllegalArgumentException("Invalid index: "+i);
	}

	public String getDescription() {
		return description;
	}

	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.DNS_TYPES;
	}

	public String getType() {
		return pkey;
	}

	public void init(ResultSet result) throws SQLException {
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
		pkey=in.readUTF().intern();
		description=in.readUTF();
		is_mx=in.readBoolean();
		param_ip=in.readBoolean();
	}

	@Override
	String toStringImpl() {
		return description;
	}

	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeUTF(pkey);
		out.writeUTF(description);
		out.writeBoolean(is_mx);
		out.writeBoolean(param_ip);
	}
}