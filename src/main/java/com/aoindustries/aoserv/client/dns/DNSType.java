/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2014, 2016, 2017, 2018  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aoserv-client.
 *
 * aoserv-client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aoserv-client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aoserv-client.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.aoserv.client.dns;

import com.aoindustries.aoserv.client.GlobalObjectStringKey;
import com.aoindustries.aoserv.client.schema.AOServProtocol;
import com.aoindustries.aoserv.client.schema.SchemaTable;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.net.AddressFamily;
import com.aoindustries.net.DomainName;
import com.aoindustries.net.InetAddress;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The <code>DNSType</code> associated with a <code>DNSRecord</code> provides
 * details about which values should be used in the destination field, and whether
 * a priority, weight, and port should exist.
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
		A     = "A",
		AAAA  = "AAAA",
		CNAME = "CNAME",
		MX    = "MX",
		NS    = "NS",
		PTR   = "PTR",
		SRV   = "SRV",
		TXT   = "TXT"
	;

	private String description;
	private boolean
		has_priority,
		has_weight,
		has_port,
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
				if(parsed.getAddressFamily() != AddressFamily.INET) throw new IllegalArgumentException("A type requires IPv4 address: "+destination);
			} catch(ValidationException e) {
				throw new IllegalArgumentException(e.getLocalizedMessage(), e);
			}
		} else if(type.equals(AAAA)) {
			try {
				InetAddress parsed = InetAddress.valueOf(destination);
				if(parsed.getAddressFamily() != AddressFamily.INET6) throw new IllegalArgumentException("AAAA type requires IPv6 address: "+destination);
			} catch(ValidationException e) {
				throw new IllegalArgumentException(e.getLocalizedMessage(), e);
			}
		} else if(type.equals(TXT)) {
			// Pretty much anything goes?
			// TODO: What are the rules for what is allowed in TXT?  Where do we enforce this currently?
		} else {
			// May end with a single .
			if(destination.charAt(destination.length()-1)=='.') destination=destination.substring(0, destination.length()-1);
			if(
				!DNSZoneTable.isValidHostnamePart(destination)
				&& !DomainName.validate(destination).isValid()
			) throw new IllegalArgumentException("Invalid destination hostname: "+origDest);
		}
	}

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_TYPE : return pkey;
			case 1 : return description;
			case 2 : return has_priority;
			case 3 : return has_weight;
			case 4 : return has_port;
			case 5 : return param_ip;
			default : throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public String getDescription() {
		return description;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.DNS_TYPES;
	}

	public String getType() {
		return pkey;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey         = result.getString(1);
		description  = result.getString(2);
		has_priority = result.getBoolean(3);
		has_weight   = result.getBoolean(4);
		has_port     = result.getBoolean(5);
		param_ip     = result.getBoolean(6);
	}

	public boolean hasPriority() {
		return has_priority;
	}

	public boolean hasWeight() {
		return has_weight;
	}

	public boolean hasPort() {
		return has_port;
	}

	public boolean isParamIP() {
		return param_ip;
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey         = in.readUTF().intern();
		description  = in.readUTF();
		has_priority = in.readBoolean();
		has_weight   = in.readBoolean();
		has_port     = in.readBoolean();
		param_ip     = in.readBoolean();
	}

	@Override
	public String toStringImpl() {
		return description;
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version protocolVersion) throws IOException {
		out.writeUTF(pkey);
		out.writeUTF(description);
		out.writeBoolean(has_priority);
		if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_72) >= 0) {
			out.writeBoolean(has_weight);
			out.writeBoolean(has_port);
		}
		out.writeBoolean(param_ip);
	}
}
