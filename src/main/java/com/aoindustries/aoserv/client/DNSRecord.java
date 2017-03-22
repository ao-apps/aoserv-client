/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2001-2013, 2014, 2016, 2017  AO Industries, Inc.
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
package com.aoindustries.aoserv.client;

import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * A <code>DNSRecord</code> is one line of a <code>DNSZone</code>
 * (name server zone file).
 *
 * @see  DNSZone
 *
 * @author  AO Industries, Inc.
 */
final public class DNSRecord extends CachedObjectIntegerKey<DNSRecord> implements Removable {

	static final int
		COLUMN_PKEY=0,
		COLUMN_ZONE=1
	;
	static final String COLUMN_ZONE_name        = "zone";
	static final String COLUMN_DOMAIN_name      = "domain";
	static final String COLUMN_TYPE_name        = "type";
	static final String COLUMN_PRIORITY_name    = "priority";
	static final String COLUMN_WEIGHT_name      = "weight";
	static final String COLUMN_DESTINATION_name = "destination";

	public static final int NO_PRIORITY = -1;
	public static final int NO_WEIGHT   = -1;
	public static final int NO_PORT     = -1;
	public static final int NO_TTL      = -1;

	String
		zone,
		domain,
		type
	;
	int priority;
	int weight;
	int port;
	String destination;
	int dhcpAddress;
	int ttl;

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_ZONE: return zone;
			case 2: return domain;
			case 3: return type;
			case 4: return priority==NO_PRIORITY ? null : priority;
			case 5: return weight==NO_WEIGHT     ? null : weight;
			case 6: return port==NO_PORT         ? null : port;
			case 7: return destination;
			case 8: return dhcpAddress==-1       ? null : dhcpAddress;
			case 9: return ttl==NO_TTL           ? null : ttl;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public String getDestination() {
		return destination;
	}

	public IPAddress getDHCPAddress() throws SQLException, IOException {
		if(dhcpAddress==-1) return null;
		IPAddress ia=table.connector.getIpAddresses().get(dhcpAddress);
		if(ia==null) throw new SQLException("Unable to find IPAddress: "+dhcpAddress);
		return ia;
	}

	public int getTTL() {
		return ttl;
	}

	public String getDomain() {
		return domain;
	}

	/**
	 * Gets the domain, but in fully-qualified, absolute path (with trailing period).
	 */
	public String getAbsoluteDomain() {
		if(domain.equals("@")) return zone;
		if(domain.endsWith(".")) return domain;
		return domain+'.'+zone;
	}

	/**
	 * Checks if this record conflicts with the provided record, meaning they may not both exist
	 * in a zone file at the same time.  The current conflicts checked are:
	 * <ol>
	 *   <li>CNAME must exist by itself, and only one CNAME maximum, per domain</li>
	 * </ol>
	 *
	 * @return <code>true</code> if there is a conflict, <code>false</code> if the records may coexist.
	 */
	public boolean hasConflict(DNSRecord other) {
		String domain1 = getAbsoluteDomain();
		String domain2 = other.getAbsoluteDomain();

		// Look for CNAME conflict
		if(domain1.equals(domain2)) {
			// If either (or both) are CNAME, there is a conflict
			if(
				type.equals(DNSType.CNAME)
				|| other.type.equals(DNSType.CNAME)
			) {
				return true;
			}
		}
		return false;
	}

	public int getPriority() {
		return priority;
	}

	public int getWeight() {
		return weight;
	}

	public int getPort() {
		return port;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.DNS_RECORDS;
	}

	public DNSType getType() throws SQLException, IOException {
		DNSType obj=table.connector.getDnsTypes().get(type);
		if(obj==null) throw new SQLException("Unable to find DNSType: "+type);
		return obj;
	}

	public DNSZone getZone() throws SQLException, IOException {
		DNSZone obj=table.connector.getDnsZones().get(zone);
		if(obj==null) throw new SQLException("Unable to find DNSZone: "+zone);
		return obj;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		int pos = 1;
		pkey        = result.getInt(pos++);
		zone        = result.getString(pos++);
		domain      = result.getString(pos++);
		type        = result.getString(pos++);
		priority    = result.getInt(pos++);
		if(result.wasNull()) priority    = NO_PRIORITY;
		weight      = result.getInt(pos++);
		if(result.wasNull()) weight      = NO_WEIGHT;
		port        = result.getInt(pos++);
		if(result.wasNull()) port        = NO_PORT;
		destination = result.getString(pos++);
		dhcpAddress = result.getInt(pos++);
		if(result.wasNull()) dhcpAddress = -1;
		ttl         = result.getInt(pos++);
		if(result.wasNull()) ttl=NO_TTL;
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey        = in.readCompressedInt();
		zone        = in.readUTF().intern();
		domain      = in.readUTF().intern();
		type        = in.readUTF().intern();
		priority    = in.readCompressedInt();
		weight      = in.readCompressedInt();
		port        = in.readCompressedInt();
		destination = in.readUTF().intern();
		dhcpAddress = in.readCompressedInt();
		ttl         = in.readCompressedInt();
	}

	@Override
	public List<CannotRemoveReason<?>> getCannotRemoveReasons() {
		return Collections.emptyList();
	}

	@Override
	public void remove() throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.REMOVE, SchemaTable.TableID.DNS_RECORDS, pkey);
	}

	@Override
	String toStringImpl() {
		StringBuilder SB=new StringBuilder();
		SB.append(zone).append(": ").append(domain);
		if(ttl != NO_TTL) SB.append(' ').append(ttl);
		SB.append(" IN ").append(type);
		if(priority != NO_PRIORITY) SB.append(' ').append(priority);
		if(weight   != NO_WEIGHT)   SB.append(' ').append(weight);
		if(port     != NO_PORT)     SB.append(' ').append(port);
		SB.append(' ').append(destination);
		return SB.toString();
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeUTF(zone);
		out.writeUTF(domain);
		out.writeUTF(type);
		out.writeCompressedInt(priority);
		if(version.compareTo(AOServProtocol.Version.VERSION_1_72)>=0) {
			out.writeCompressedInt(weight);
			out.writeCompressedInt(port);
		}
		out.writeUTF(destination);
		out.writeCompressedInt(dhcpAddress);
		if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_127)>=0) {
			out.writeCompressedInt(ttl);
		}
	}
}
