/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2013, 2016, 2017, 2018  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.net;

import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.net.monitoring.IpAddressMonitoring;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.lang.ObjectUtils;
import com.aoindustries.net.DomainName;
import com.aoindustries.net.InetAddress;
import com.aoindustries.util.StringUtility;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Each <code>IPAddress</code> represents a unique IPv4 address.  Two of the IP
 * addresses exist on every server, <code>WILDCARD_IP</code> and <code>LOOPBACK_IP</code>.
 * Every other IP address is assigned to a specific <code>Server</code>.  IP
 * addresses may be assigned to a specific <code>Package</code> and may have
 * a monthly rate associated with them.
 *
 * @see  Host
 * @see  Package
 * @see  Bind
 * @see  PrivateFTPServer
 *
 * @author  AO Industries, Inc.
 */
final public class IpAddress extends CachedObjectIntegerKey<IpAddress> {

	static final int
		COLUMN_ID = 0,
		COLUMN_DEVICE = 2,
		COLUMN_PACKAGE = 5
	;
	public static final String COLUMN_IP_ADDRESS_name = "inetAddress";
	public static final String COLUMN_DEVICE_name = "device";

	public static final String
		LOOPBACK_IP="127.0.0.1",
		WILDCARD_IP="0.0.0.0"
	;

	// TODO: Should have an upper bound to this cache to avoid memory leak
	private static final ConcurrentMap<String,Integer> intForIPAddressCache = new ConcurrentHashMap<>();

	public static Integer getIntForIPAddress(String ipAddress) throws IllegalArgumentException {
		Integer result = intForIPAddressCache.get(ipAddress);
		if(result==null) {
			// There must be four octets with . between
			List<String> octets=StringUtility.splitString(ipAddress, '.');
			if(octets.size()!=4) throw new IllegalArgumentException("Invalid IP address: "+ipAddress);

			// Each octet should be from 1 to 3 digits, all numbers
			// and should have a value between 0 and 255 inclusive
			for(int c=0;c<4;c++) {
				String tet=octets.get(c);
				int tetLen=tet.length();
				if(tetLen<1 || tetLen>3) throw new IllegalArgumentException("Invalid IP address: "+ipAddress);
				for(int d=0;d<tetLen;d++) {
					char ch=tet.charAt(d);
					if(ch<'0' || ch>'9') throw new IllegalArgumentException("Invalid IP address: "+ipAddress);
				}
				int val=Integer.parseInt(tet);
				if(val<0 || val>255) throw new IllegalArgumentException("Invalid IP address: "+ipAddress);
			}
			result =
				(Integer.parseInt(octets.get(0))<<24)
				| (Integer.parseInt(octets.get(1))<<16)
				| (Integer.parseInt(octets.get(2))<<8)
				| (Integer.parseInt(octets.get(3))&255)
			;
			Integer existing = intForIPAddressCache.putIfAbsent(ipAddress, result);
			if(existing!=null) result = existing;
		}
		return result;
	}

	public static String getIPAddressForInt(int i) {
		return
			new StringBuilder(15)
			.append((i>>>24)&255)
			.append('.')
			.append((i>>>16)&255)
			.append('.')
			.append((i>>>8)&255)
			.append('.')
			.append(i&255)
			.toString()
		;
	}

	//private static final ConcurrentMap<String,String> getReverseDnsQueryCache = new ConcurrentHashMap<String,String>();

	/**
	 * Gets the arpa address to be used for reverse DNS queries.
	 */
	/*public static String getReverseDnsQuery(String ip) {
		String arpa = getReverseDnsQueryCache.get(ip);
		if(arpa==null) {
			int bits = getIntForIPAddress(ip);
			arpa =
				new StringBuilder(29)
				.append(bits&255)
				.append('.')
				.append((bits>>>8)&255)
				.append('.')
				.append((bits>>>16)&255)
				.append('.')
				.append((bits>>>24)&255)
				.append(".in-addr.arpa.")
				.toString()
			;
			String existingArpa = getReverseDnsQueryCache.putIfAbsent(ip, arpa);
			if(existingArpa!=null) arpa = existingArpa;
		}
		return arpa;
	}*/

	private InetAddress inetAddress;
	private int device;
	private boolean isAlias;
	private DomainName hostname;
	private int package_id;
	private long created;
	private boolean isAvailable;
	private boolean isOverflow;
	private boolean isDhcp;
	private InetAddress externalInetAddress;
	private String netmask;
	// Protocol conversion
	private AccountingCode packageName;
	private boolean pingMonitorEnabled;
	private boolean checkBlacklistsOverSmtp;
	private boolean monitoringEnabled;

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_ID: return pkey;
			case 1: return inetAddress;
			case COLUMN_DEVICE: return device == -1 ? null : device;
			case 3: return isAlias;
			case 4: return hostname;
			case COLUMN_PACKAGE: return package_id;
			case 6: return getCreated();
			case 7: return isAvailable;
			case 8: return isOverflow;
			case 9: return isDhcp;
			case 10: return externalInetAddress;
			case 11: return netmask;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	public int getId() {
		return pkey;
	}

	public InetAddress getInetAddress() {
		return inetAddress;
	}

	public int getDevice_id() {
		return device;
	}

	public Device getDevice() throws SQLException, IOException {
		if(device == -1) return null;
		Device nd = table.getConnector().getNet().getDevice().get(device);
		if (nd == null) throw new SQLException("Unable to find NetDevice: " + device);
		return nd;
	}

	public boolean isAlias() {
		return isAlias;
	}

	public DomainName getHostname() {
		return hostname;
	}

	public int getPackage_id() {
		return package_id;
	}

	public Package getPackage() throws IOException, SQLException {
		// May be null when filtered
		return table.getConnector().getBilling().getPackage().get(package_id);
	}

	// TODO: Add this type of shortcut in other places where Timestamp is wrapped and returned
	public long getCreated_time() {
		return created;
	}

	/**
	 * Determines when this <code>IPAddress</code> was created.  The created time
	 * is reset when the address is allocated to a different <code>Package</code>,
	 * which allows the automated accounting to start the billing on the correct
	 * day of the month.
	 */
	public Timestamp getCreated() {
		return new Timestamp(created);
	}


	public List<Bind> getNetBinds() throws IOException, SQLException {
		return table.getConnector().getNet().getBind().getNetBinds(this);
	}

	public boolean isAvailable() {
		return isAvailable;
	}

	public boolean isOverflow() {
		return isOverflow;
	}

	public boolean isDhcp() {
		return isDhcp;
	}

	/**
	 * Gets the external IP address, if different than ip_address.
	 */
	public InetAddress getExternalInetAddress() {
		return externalInetAddress;
	}

	public String getNetMask() {
		return netmask;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.IP_ADDRESSES;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			int pos = 1;
			pkey = result.getInt(pos++);
			inetAddress = InetAddress.valueOf(result.getString(pos++));
			device = result.getInt(pos++);
			if(result.wasNull()) device = -1;
			isAlias = result.getBoolean(pos++);
			hostname = DomainName.valueOf(result.getString(pos++));
			package_id = result.getInt(pos++);
			created = result.getTimestamp(pos++).getTime();
			isAvailable = result.getBoolean(pos++);
			isOverflow = result.getBoolean(pos++);
			isDhcp = result.getBoolean(pos++);
			externalInetAddress = InetAddress.valueOf(result.getString(pos++));
			netmask = result.getString(pos++);
			// Protocol conversion
			packageName = AccountingCode.valueOf(result.getString(pos++));
			pingMonitorEnabled = result.getBoolean(pos++);
			checkBlacklistsOverSmtp = result.getBoolean(pos++);
			monitoringEnabled = result.getBoolean(pos++);
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			pkey = in.readCompressedInt();
			inetAddress = InetAddress.valueOf(in.readUTF()).intern();
			device = in.readCompressedInt();
			isAlias = in.readBoolean();
			hostname = DomainName.valueOf(in.readNullUTF());
			package_id = in.readCompressedInt();
			created = in.readLong();
			isAvailable = in.readBoolean();
			isOverflow = in.readBoolean();
			isDhcp = in.readBoolean();
			externalInetAddress = InetAddress.valueOf(in.readNullUTF());
			netmask = in.readUTF().intern();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void write(CompressedDataOutputStream out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_68) <= 0) out.writeUTF(inetAddress.isUnspecified() ? "0.0.0.0" : inetAddress.toString());
		else out.writeUTF(inetAddress.toString());
		out.writeCompressedInt(device);
		out.writeBoolean(isAlias);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_68) <= 0) {
			out.writeUTF(hostname==null ? "*" : hostname.toString());
		} else {
			out.writeNullUTF(ObjectUtils.toString(hostname));
		}
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_81_17) <= 0) {
			out.writeUTF(packageName.toString());
		} else {
			out.writeCompressedInt(package_id);
		}
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_122) <= 0) out.writeCompressedInt(0);
		out.writeLong(created);
		out.writeBoolean(isAvailable);
		out.writeBoolean(isOverflow);
		out.writeBoolean(isDhcp);
		if(
			protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_30) >= 0
			&& protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_81_17) <= 0
		) {
			out.writeBoolean(pingMonitorEnabled);
		}
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_34) >= 0) out.writeNullUTF(ObjectUtils.toString(externalInetAddress));
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_38) >= 0) out.writeUTF(netmask);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_81_17) <= 0) {
			if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_75) >= 0) out.writeBoolean(checkBlacklistsOverSmtp);
			if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_81_17) >= 0) out.writeBoolean(monitoringEnabled);
		}
	}

	public boolean isUsed() throws IOException, SQLException {
		return !getNetBinds().isEmpty();
	}

	/**
	 * @deprecated  Replace with getInetAddress().isUnspecified()
	 */
	@Deprecated
	public boolean isWildcard() {
		return inetAddress.isUnspecified();
	}

	public void moveTo(Host server) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.MOVE_IP_ADDRESS, inetAddress.toString(), server.getPkey());
	}

	/**
	 * Sets the hostname for this <code>IPAddress</code>.
	 */
	public void setHostname(DomainName hostname) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.SET_IP_ADDRESS_HOSTNAME, pkey, hostname);
	}

	/**
	 * Sets the <code>Package</code>.  The package may only be set if the IP Address is not used
	 * by other resources.
	 */
	public void setPackage(Package pk) throws IOException, SQLException {
		if(isUsed()) throw new SQLException("Unable to set Package, IPAddress in use: #"+pkey);

		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.SET_IP_ADDRESS_PACKAGE, pkey, pk.getName());
	}

	public void setDHCPAddress(InetAddress ipAddress) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.SET_IP_ADDRESS_DHCP_ADDRESS, pkey, ipAddress);
	}

	public IpAddressMonitoring getMonitoring() throws IOException, SQLException {
		return table.getConnector().getNet().getMonitoring().getIpAddressMonitoring().get(pkey);
	}
}
