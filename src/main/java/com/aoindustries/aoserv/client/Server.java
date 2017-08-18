/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2013, 2016, 2017  AO Industries, Inc.
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

import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.aoserv.client.validator.FirewalldZoneName;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.net.Port;
import com.aoindustries.util.WrappedException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

/**
 * A <code>Server</code> stores the details about a single, physical server.
 *
 * @author  AO Industries, Inc.
 */
final public class Server extends CachedObjectIntegerKey<Server> implements Comparable<Server> {

	static final int
		COLUMN_PKEY=0,
		COLUMN_PACKAGE=4
	;
	static final String COLUMN_PACKAGE_name = "package";
	static final String COLUMN_NAME_name = "name";

	/**
	 * The daemon key is only available to <code>MasterUser</code>s.  This value is used
	 * in place of the key when not accessible.
	 */
	public static final String HIDDEN_PASSWORD="*";

	String farm;
	private String description;
	int operating_system_version;
	private int packageId;
	private String name;
	private boolean monitoring_enabled;

	public void addBusiness(
		AccountingCode accounting,
		String contractVersion,
		Business parent,
		boolean can_add_backup_servers,
		boolean can_add_businesses,
		boolean can_see_prices,
		boolean billParent
	) throws IOException, SQLException {
		table.connector.getBusinesses().addBusiness(
			accounting,
			contractVersion,
			this,
			parent.pkey,
			can_add_backup_servers,
			can_add_businesses,
			can_see_prices,
			billParent
		);
	}

	public int addNetBind(
		Package pk,
		IPAddress ia,
		Port port,
		Protocol appProtocol,
		boolean monitoringEnabled,
		Set<FirewalldZoneName> firewalldZones
	) throws IOException, SQLException {
		return table.connector.getNetBinds().addNetBind(
			this,
			pk,
			ia,
			port,
			appProtocol,
			monitoringEnabled,
			firewalldZones
		);
	}

	public AOServer getAOServer() throws IOException, SQLException {
		return table.connector.getAoServers().get(pkey);
	}

	public PhysicalServer getPhysicalServer() throws IOException, SQLException {
		return table.connector.getPhysicalServers().get(pkey);
	}

	public VirtualServer getVirtualServer() throws IOException, SQLException {
		return table.connector.getVirtualServers().get(pkey);
	}

	public List<Business> getBusinesses() throws IOException, SQLException {
		return table.connector.getBusinessServers().getBusinesses(this);
	}

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case 1: return farm;
			case 2: return description;
			case 3: return operating_system_version==-1 ? null : operating_system_version;
			case COLUMN_PACKAGE: return packageId;
			case 5: return name;
			case 6: return monitoring_enabled;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public OperatingSystemVersion getOperatingSystemVersion() throws SQLException, IOException {
		if(operating_system_version==-1) return null;
		OperatingSystemVersion osv=table.connector.getOperatingSystemVersions().get(operating_system_version);
		if(osv==null) throw new SQLException("Unable to find OperatingSystemVersion: "+operating_system_version);
		return osv;
	}

	/**
	 * May be filtered.
	 *
	 * @see #getPackageId()
	 */
	public Package getPackage() throws IOException, SQLException {
		return table.connector.getPackages().get(packageId);
	}

	/**
	 * Gets the package id, will not be filtered.
	 *
	 * @see #getPackage()
	 */
	public int getPackageId() {
		return packageId;
	}

	public String getName() {
		return name;
	}

	public boolean isMonitoringEnabled() {
		return monitoring_enabled;
	}

	public ServerFarm getServerFarm() throws SQLException, IOException {
		ServerFarm sf=table.connector.getServerFarms().get(farm);
		if(sf==null) throw new SQLException("Unable to find ServerFarm: "+farm);
		return sf;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.SERVERS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey = result.getInt(1);
		farm = result.getString(2);
		description = result.getString(3);
		operating_system_version=result.getInt(4);
		if(result.wasNull()) operating_system_version = -1;
		packageId = result.getInt(5);
		name = result.getString(6);
		monitoring_enabled = result.getBoolean(7);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readCompressedInt();
		farm=in.readUTF().intern();
		description = in.readUTF();
		operating_system_version=in.readCompressedInt();
		packageId = in.readCompressedInt();
		name = in.readUTF();
		monitoring_enabled = in.readBoolean();
	}

	@Override
	String toStringImpl() throws IOException, SQLException {
		AOServer aoServer = getAOServer();
		if(aoServer!=null) return aoServer.toStringImpl();
		Package pk = getPackage();
		if(pk!=null) return pk.getName().toString()+'/'+name;
		return Integer.toString(pkey);
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeCompressedInt(pkey);
		if(version.compareTo(AOServProtocol.Version.VERSION_1_30)<=0) {
			out.writeUTF(name); // hostname
		}
		out.writeUTF(farm);
		if(version.compareTo(AOServProtocol.Version.VERSION_1_30)<=0) {
			out.writeUTF("AOINDUSTRIES"); // owner
			out.writeUTF("orion"); // administrator
		}
		out.writeUTF(description);
		if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_107)<=0) out.writeUTF(Architecture.I686);
		if(version.compareTo(AOServProtocol.Version.VERSION_1_30)<=0) {
			out.writeCompressedInt(0); // backup_hour
			out.writeLong(-1); // last_backup_time
		}
		if(version.compareTo(AOServProtocol.Version.VERSION_1_30)<=0) {
			out.writeCompressedInt(operating_system_version==-1 ? OperatingSystemVersion.MANDRIVA_2006_0_I586 : operating_system_version);
		} else {
			out.writeCompressedInt(operating_system_version);
		}
		if(
			version.compareTo(AOServProtocol.Version.VERSION_1_0_A_108)>=0
			&& version.compareTo(AOServProtocol.Version.VERSION_1_30)<=0
		) {
			out.writeNullUTF(null); // asset_label
		}
		if(
			version.compareTo(AOServProtocol.Version.VERSION_1_16)>=0
			&& version.compareTo(AOServProtocol.Version.VERSION_1_30)<=0
		) {
			out.writeFloat(Float.NaN); // minimum_power
			out.writeFloat(Float.NaN); // maximum_power
		}
		if(version.compareTo(AOServProtocol.Version.VERSION_1_31)>=0) {
			out.writeCompressedInt(packageId);
			out.writeUTF(name);
		}
		if(version.compareTo(AOServProtocol.Version.VERSION_1_32)>=0) {
			out.writeBoolean(monitoring_enabled);
		}
	}

	/**
	 * Gets the list of all replications coming from this server.
	 */
	public List<FailoverFileReplication> getFailoverFileReplications() throws IOException, SQLException {
		return table.connector.getFailoverFileReplications().getFailoverFileReplications(this);
	}

	public List<FirewalldZone> getFirewalldZones() throws IOException, SQLException {
		return table.connector.getFirewalldZones().getFirewalldZones(this);
	}

	public NetBind getNetBind(IPAddress ipAddress, Port port) throws IOException, SQLException {
		return table.connector.getNetBinds().getNetBind(this, ipAddress, port);
	}

	public List<NetBind> getNetBinds() throws IOException, SQLException {
		return table.connector.getNetBinds().getNetBinds(this);
	}

	public List<NetBind> getNetBinds(IPAddress ipAddress) throws IOException, SQLException {
		return table.connector.getNetBinds().getNetBinds(this, ipAddress);
	}

	public List<NetBind> getNetBinds(Protocol protocol) throws IOException, SQLException {
		return table.connector.getNetBinds().getNetBinds(this, protocol);
	}

	public NetDevice getNetDevice(String deviceID) throws IOException, SQLException {
		return table.connector.getNetDevices().getNetDevice(this, deviceID);
	}

	public List<NetDevice> getNetDevices() throws IOException, SQLException {
		return table.connector.getNetDevices().getNetDevices(this);
	}

	public List<IPAddress> getIPAddresses() throws IOException, SQLException {
		return table.connector.getIpAddresses().getIPAddresses(this);
	}

	public IPAddress getAvailableIPAddress() throws SQLException, IOException {
		for(IPAddress ip : getIPAddresses()) {
			if(
				ip.isAvailable()
				&& ip.isAlias()
				&& !ip.getNetDevice().getNetDeviceID().isLoopback()
			) return ip;
		}
		return null;
	}

	@Override
	public int compareTo(Server o) {
		try {
			Package pk1 = getPackage();
			Package pk2 = o.getPackage();
			if(pk1==null || pk2==null) {
				int id1 = getPackageId();
				int id2 = o.getPackageId();
				return (id1<id2 ? -1 : (id1==id2 ? 0 : 1));
			} else {
				int diff = pk1.compareTo(pk2);
				if(diff!=0) return diff;
				diff = name.compareToIgnoreCase(o.name);
				if(diff!=0) return diff;
				return name.compareTo(o.name);
			}
		} catch(IOException | SQLException err) {
			throw new WrappedException(err);
		}
	}
}
