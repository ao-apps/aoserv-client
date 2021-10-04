/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2013, 2016, 2017, 2018, 2019, 2020, 2021  AO Industries, Inc.
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

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.exception.WrappedException;
import com.aoapps.net.Port;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.backup.FileReplication;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.distribution.Architecture;
import com.aoindustries.aoserv.client.distribution.OperatingSystemVersion;
import com.aoindustries.aoserv.client.infrastructure.PhysicalServer;
import com.aoindustries.aoserv.client.infrastructure.ServerFarm;
import com.aoindustries.aoserv.client.infrastructure.VirtualServer;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
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
public final class Host extends CachedObjectIntegerKey<Host> implements Comparable<Host> {

	static final int
		COLUMN_PKEY=0,
		COLUMN_PACKAGE=4
	;
	public static final String COLUMN_PACKAGE_name = "package";
	public static final String COLUMN_NAME_name = "name";

	/**
	 * The daemon key is only available to <code>MasterUser</code>s.  This value is used
	 * in place of the key when not accessible.
	 */
	public static final String HIDDEN_PASSWORD="*";

	private String farm;
	private String description;
	private int operating_system_version;
	private int packageId;
	private String name;
	private boolean monitoring_enabled;

	// TODO: No longer add to a server by default, move this method to correct place
	public void addAccount(
		Account.Name accounting,
		String contractVersion,
		Account parent,
		boolean can_add_backup_servers,
		boolean can_add_businesses,
		boolean can_see_prices,
		boolean billParent
	) throws IOException, SQLException {
		table.getConnector().getAccount().getAccount().addAccount(
			accounting,
			contractVersion,
			this,
			parent.getName(),
			can_add_backup_servers,
			can_add_businesses,
			can_see_prices,
			billParent
		);
	}

	public int addNetBind(
		Package pk,
		IpAddress ia,
		Port port,
		AppProtocol appProtocol,
		boolean monitoringEnabled,
		Set<FirewallZone.Name> firewalldZones
	) throws IOException, SQLException {
		return table.getConnector().getNet().getBind().addNetBind(
			this,
			pk,
			ia,
			port,
			appProtocol,
			monitoringEnabled,
			firewalldZones
		);
	}

	public Server getLinuxServer() throws IOException, SQLException {
		return table.getConnector().getLinux().getServer().get(pkey);
	}

	public PhysicalServer getPhysicalServer() throws IOException, SQLException {
		return table.getConnector().getInfrastructure().getPhysicalServer().get(pkey);
	}

	public VirtualServer getVirtualServer() throws IOException, SQLException {
		return table.getConnector().getInfrastructure().getVirtualServer().get(pkey);
	}

	public List<Account> getAccounts() throws IOException, SQLException {
		return table.getConnector().getAccount().getAccountHost().getAccounts(this);
	}

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case 1: return farm;
			case 2: return description;
			case 3: return operating_system_version==-1 ? null : operating_system_version;
			case COLUMN_PACKAGE: return packageId;
			case 5: return name;
			case 6: return monitoring_enabled;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	public int getOperatingSystemVersion_id() {
		return operating_system_version;
	}

	public OperatingSystemVersion getOperatingSystemVersion() throws SQLException, IOException {
		if(operating_system_version==-1) return null;
		OperatingSystemVersion osv=table.getConnector().getDistribution().getOperatingSystemVersion().get(operating_system_version);
		if(osv==null) throw new SQLException("Unable to find OperatingSystemVersion: "+operating_system_version);
		return osv;
	}

	/**
	 * May be filtered.
	 *
	 * @see #getPackageId()
	 */
	public Package getPackage() throws IOException, SQLException {
		return table.getConnector().getBilling().getPackage().get(packageId);
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
		ServerFarm sf=table.getConnector().getInfrastructure().getServerFarm().get(farm);
		if(sf==null) throw new SQLException("Unable to find ServerFarm: "+farm);
		return sf;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.SERVERS;
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
	public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
		pkey=in.readCompressedInt();
		farm=in.readUTF().intern();
		description = in.readUTF();
		operating_system_version=in.readCompressedInt();
		packageId = in.readCompressedInt();
		name = in.readUTF();
		monitoring_enabled = in.readBoolean();
	}

	@Override
	public String toStringImpl() throws IOException, SQLException {
		Server aoServer = getLinuxServer();
		if(aoServer!=null) return aoServer.toStringImpl();
		Package pk = getPackage();
		if(pk!=null) return pk.getName().toString()+'/'+name;
		return Integer.toString(pkey);
	}

	@Override
	public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_30)<=0) {
			out.writeUTF(name); // hostname
		}
		out.writeUTF(farm);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_30)<=0) {
			out.writeUTF("AOINDUSTRIES"); // owner
			out.writeUTF("orion"); // administrator
		}
		out.writeUTF(description);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_107)<=0) out.writeUTF(Architecture.I686);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_30)<=0) {
			out.writeCompressedInt(0); // backup_hour
			out.writeLong(-1); // last_backup_time
		}
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_30)<=0) {
			out.writeCompressedInt(operating_system_version==-1 ? OperatingSystemVersion.CENTOS_7_X86_64 : operating_system_version);
		} else {
			out.writeCompressedInt(operating_system_version);
		}
		if(
			protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_108)>=0
			&& protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_30)<=0
		) {
			out.writeNullUTF(null); // asset_label
		}
		if(
			protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_16)>=0
			&& protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_30)<=0
		) {
			out.writeFloat(Float.NaN); // minimum_power
			out.writeFloat(Float.NaN); // maximum_power
		}
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_31)>=0) {
			out.writeCompressedInt(packageId);
			out.writeUTF(name);
		}
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_32)>=0) {
			out.writeBoolean(monitoring_enabled);
		}
	}

	/**
	 * Gets the list of all replications coming from this server.
	 */
	public List<FileReplication> getFailoverFileReplications() throws IOException, SQLException {
		return table.getConnector().getBackup().getFileReplication().getFailoverFileReplications(this);
	}

	public List<FirewallZone> getFirewalldZones() throws IOException, SQLException {
		return table.getConnector().getNet().getFirewallZone().getFirewalldZones(this);
	}

	public Bind getNetBind(IpAddress ipAddress, Port port) throws IOException, SQLException {
		return table.getConnector().getNet().getBind().getNetBind(this, ipAddress, port);
	}

	public List<Bind> getNetBinds() throws IOException, SQLException {
		return table.getConnector().getNet().getBind().getNetBinds(this);
	}

	public List<Bind> getNetBinds(IpAddress ipAddress) throws IOException, SQLException {
		return table.getConnector().getNet().getBind().getNetBinds(this, ipAddress);
	}

	public List<Bind> getNetBinds(AppProtocol protocol) throws IOException, SQLException {
		return table.getConnector().getNet().getBind().getNetBinds(this, protocol);
	}

	public Device getNetDevice(String deviceID) throws IOException, SQLException {
		return table.getConnector().getNet().getDevice().getNetDevice(this, deviceID);
	}

	public List<Device> getNetDevices() throws IOException, SQLException {
		return table.getConnector().getNet().getDevice().getNetDevices(this);
	}

	public List<IpAddress> getIPAddresses() throws IOException, SQLException {
		return table.getConnector().getNet().getIpAddress().getIPAddresses(this);
	}

	public IpAddress getAvailableIPAddress() throws SQLException, IOException {
		for(IpAddress ip : getIPAddresses()) {
			if(
				ip.isAvailable()
				&& ip.isAlias()
				&& !ip.getDevice().getDeviceId().isLoopback()
			) return ip;
		}
		return null;
	}

	@Override
	public int compareTo(Host o) {
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
