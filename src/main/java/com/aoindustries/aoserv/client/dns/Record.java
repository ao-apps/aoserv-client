/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2014, 2016, 2017, 2018, 2019  AO Industries, Inc.
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

import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.CannotRemoveReason;
import com.aoindustries.aoserv.client.Removable;
import com.aoindustries.aoserv.client.net.IpAddress;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.io.stream.StreamableInput;
import com.aoindustries.io.stream.StreamableOutput;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * A <code>DNSRecord</code> is one line of a <code>DNSZone</code>
 * (name server zone file).
 *
 * @see  Zone
 *
 * @author  AO Industries, Inc.
 */
final public class Record extends CachedObjectIntegerKey<Record> implements Removable {

	static final int
		COLUMN_ID = 0,
		COLUMN_ZONE = 1
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

	private String zone;
	private String domain;
	private String type;
	private int priority;
	private int weight;
	private int port;
	private String destination;
	private int dhcpAddress;
	private int ttl;

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_ID: return pkey;
			case COLUMN_ZONE: return zone;
			case 2: return domain;
			case 3: return type;
			case 4: return priority==NO_PRIORITY ? null : priority;
			case 5: return weight==NO_WEIGHT     ? null : weight;
			case 6: return port==NO_PORT         ? null : port;
			case 7: return destination;
			case 8: return dhcpAddress==-1       ? null : dhcpAddress;
			case 9: return ttl==NO_TTL           ? null : ttl;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	public int getId() {
		return pkey;
	}

	public String getZone_zone() {
		return zone;
	}

	public Zone getZone() throws SQLException, IOException {
		Zone obj = table.getConnector().getDns().getZone().get(zone);
		if(obj == null) throw new SQLException("Unable to find DNSZone: " + zone);
		return obj;
	}

	public String getDomain() {
		return domain;
	}

	public String getType_type() {
		return type;
	}

	public RecordType getType() throws SQLException, IOException {
		RecordType obj = table.getConnector().getDns().getRecordType().get(type);
		if(obj == null) throw new SQLException("Unable to find DNSType: " + type);
		return obj;
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

	public String getDestination() {
		return destination;
	}

	public Integer getDhcpAddress_id() {
		return dhcpAddress == -1 ? null : dhcpAddress;
	}

	public IpAddress getDhcpAddress() throws SQLException, IOException {
		if(dhcpAddress == -1) return null;
		IpAddress ia = table.getConnector().getNet().getIpAddress().get(dhcpAddress);
		if(ia == null) throw new SQLException("Unable to find IPAddress: " + dhcpAddress);
		return ia;
	}

	public int getTtl() {
		return ttl;
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
	public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
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
	public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeUTF(zone);
		out.writeUTF(domain);
		out.writeUTF(type);
		out.writeCompressedInt(priority);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_72)>=0) {
			out.writeCompressedInt(weight);
			out.writeCompressedInt(port);
		}
		out.writeUTF(destination);
		out.writeCompressedInt(dhcpAddress);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_127)>=0) {
			out.writeCompressedInt(ttl);
		}
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
	 * Strips a destination of characters not allowed in TXT records.
	 * Removes any double quotes, anything below space <code>' '</code>,
	 * or anything <code>&gt;= (char)0x7f</code>.  Also trims the entry after
	 * characters are escaped.
	 */
	static String cleanTxt(String destination) {
		int len = destination.length();
		StringBuilder txt = new StringBuilder(len);
		for(int i = 0; i < len; i++) {
			char ch = destination.charAt(i);
			if(
				ch != '"'
				&& ch >= ' '
				&& ch < (char)0x7f
			) txt.append(ch);
		}
		String cleaned = txt.length() == len ? destination : txt.toString();
		return cleaned.trim();
	}

	private static boolean isSpf1(String destination) {
		String txt = cleanTxt(destination);
		return txt.equals("v=spf1") || txt.startsWith("v=spf1 ");
	}

	/**
	 * Checks if this record conflicts with the provided record, meaning they may not both exist
	 * in a zone file at the same time.  The current conflicts checked are:
	 * <ol>
	 *   <li>CNAME must exist by itself, and only one CNAME maximum, per domain</li>
	 *   <li>
	 *     Multiple TXT entries of "v=spf1", with or without surrounded by quotes, see
	 *     <a href="http://www.openspf.org/RFC_4408#version">4.5. Selecting Records</a>.
	 *   </li>
	 * </ol>
	 *
	 * @return <code>true</code> if there is a conflict, <code>false</code> if the records may coexist.
	 */
	public boolean hasConflict(Record other) {
		String domain1 = getAbsoluteDomain();
		String domain2 = other.getAbsoluteDomain();

		// Look for CNAME conflict
		if(domain1.equals(domain2)) {
			// If either (or both) are CNAME, there is a conflict
			if(
				type.equals(RecordType.CNAME)
				|| other.type.equals(RecordType.CNAME)
			) {
				return true;
			}
			// If both are TXT types, and v=spf1, there is a conflict
			if(
				type.equals(RecordType.TXT)
				&& other.type.equals(RecordType.TXT)
				&& isSpf1(destination)
				&& isSpf1(other.destination)
			) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.DNS_RECORDS;
	}

	@Override
	public List<CannotRemoveReason<?>> getCannotRemoveReasons() {
		return Collections.emptyList();
	}

	@Override
	public void remove() throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.REMOVE, Table.TableID.DNS_RECORDS, pkey);
	}

	@Override
	public String toStringImpl() {
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
}
