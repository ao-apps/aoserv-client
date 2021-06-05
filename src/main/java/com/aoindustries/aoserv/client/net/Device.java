/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018, 2019, 2020, 2021  AO Industries, Inc.
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
import com.aoapps.lang.util.InternUtils;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.net.InetAddress;
import com.aoapps.net.MacAddress;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Each server has multiple network devices, each listening on different
 * IP addresses.
 *
 * @author  AO Industries, Inc.
 */
final public class Device extends CachedObjectIntegerKey<Device> {

	static final int
		COLUMN_ID = 0,
		COLUMN_SERVER = 1
	;
	public static final String COLUMN_SERVER_name = "server";
	public static final String COLUMN_DEVICE_ID_name = "deviceId";

	private int server;
	private String deviceId;
	private String description;
	private String delete_route;
	private InetAddress gateway;
	private InetAddress network;
	private InetAddress broadcast;
	private MacAddress mac_address;
	private long max_bit_rate;
	private long monitoring_bit_rate_low;
	private long monitoring_bit_rate_medium;
	private long monitoring_bit_rate_high;
	private long monitoring_bit_rate_critical;
	private boolean monitoring_enabled;

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_ID: return pkey;
			case COLUMN_SERVER: return server;
			case 2: return deviceId;
			case 3: return description;
			case 4: return delete_route;
			case 5: return gateway;
			case 6: return network;
			case 7: return broadcast;
			case 8: return mac_address;
			case 9: return max_bit_rate==-1 ? null : max_bit_rate;
			case 10: return monitoring_bit_rate_low==-1 ? null : monitoring_bit_rate_low;
			case 11: return monitoring_bit_rate_medium==-1 ? null : monitoring_bit_rate_medium;
			case 12: return monitoring_bit_rate_high==-1 ? null : monitoring_bit_rate_high;
			case 13: return monitoring_bit_rate_critical==-1 ? null : monitoring_bit_rate_critical;
			case 14: return monitoring_enabled;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	public int getId() {
		return pkey;
	}

	public int getServer_pkey() {
		return server;
	}

	public Host getHost() throws SQLException, IOException {
		Host se = table.getConnector().getNet().getHost().get(server);
		if(se == null) throw new SQLException("Unable to find Host: " + server);
		return se;
	}

	public String getDeviceId_name() {
		return deviceId;
	}

	public DeviceId getDeviceId() throws SQLException, IOException {
		DeviceId obj = table.getConnector().getNet().getDeviceId().get(deviceId);
		if(obj == null) throw new SQLException("Unable to find NetDeviceID: " + deviceId);
		return obj;
	}

	public String getDescription() {
		return description;
	}

	public String getDeleteRoute() {
		return delete_route;
	}

	public InetAddress getGateway() {
		return gateway;
	}

	public InetAddress getNetwork() {
		return network;
	}

	public InetAddress getBroadcast() {
		return broadcast;
	}

	public MacAddress getMacAddress() {
		return mac_address;
	}

	/**
	 * Gets the maximum bit rate this interface can support or <code>-1</code>
	 * if unknown.
	 */
	public long getMaxBitRate() {
		return max_bit_rate;
	}

	/**
	 * Gets the 5-minute average that is considered a low-priority alert or
	 * <code>-1</code> if no alert allowed at this level.
	 */
	public long getMonitoringBitRateLow() {
		return monitoring_bit_rate_low;
	}

	/**
	 * Gets the 5-minute average that is considered a medium-priority alert or
	 * <code>-1</code> if no alert allowed at this level.
	 */
	public long getMonitoringBitRateMedium() {
		return monitoring_bit_rate_medium;
	}

	/**
	 * Gets the 5-minute average that is considered a high-priority alert or
	 * <code>-1</code> if no alert allowed at this level.
	 */
	public long getMonitoringBitRateHigh() {
		return monitoring_bit_rate_high;
	}

	/**
	 * Gets the 5-minute average that is considered a critical-priority alert or
	 * <code>-1</code> if no alert allowed at this level.  This is the level
	 * that will alert people 24x7.
	 */
	public long getMonitoringBitRateCritical() {
		return monitoring_bit_rate_critical;
	}

	/**
	 * The monitoring of a net_devices may be enabled or disabled.
	 */
	public boolean isMonitoringEnabled() {
		return monitoring_enabled;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			int pos = 1;
			pkey=result.getInt(pos++);
			server=result.getInt(pos++);
			deviceId = result.getString(pos++);
			description=result.getString(pos++);
			delete_route=result.getString(pos++);
			gateway=InetAddress.valueOf(result.getString(pos++));
			network=InetAddress.valueOf(result.getString(pos++));
			broadcast=InetAddress.valueOf(result.getString(pos++));
			mac_address = MacAddress.valueOf(result.getString(pos++));
			max_bit_rate=result.getLong(pos++);
			if(result.wasNull()) max_bit_rate=-1;
			monitoring_bit_rate_low = result.getLong(pos++);
			if(result.wasNull()) monitoring_bit_rate_low = -1;
			monitoring_bit_rate_medium = result.getLong(pos++);
			if(result.wasNull()) monitoring_bit_rate_medium = -1;
			monitoring_bit_rate_high = result.getLong(pos++);
			if(result.wasNull()) monitoring_bit_rate_high = -1;
			monitoring_bit_rate_critical = result.getLong(pos++);
			if(result.wasNull()) monitoring_bit_rate_critical = -1;
			monitoring_enabled = result.getBoolean(pos++);
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
		try {
			pkey=in.readCompressedInt();
			server=in.readCompressedInt();
			deviceId = in.readUTF().intern();
			description=in.readUTF();
			delete_route=InternUtils.intern(in.readNullUTF());
			gateway=InternUtils.intern(InetAddress.valueOf(in.readNullUTF()));
			network=InternUtils.intern(InetAddress.valueOf(in.readNullUTF()));
			broadcast=InternUtils.intern(InetAddress.valueOf(in.readNullUTF()));
			mac_address = MacAddress.valueOf(in.readNullUTF());
			max_bit_rate=in.readLong();
			monitoring_bit_rate_low = in.readLong();
			monitoring_bit_rate_medium = in.readLong();
			monitoring_bit_rate_high = in.readLong();
			monitoring_bit_rate_critical = in.readLong();
			monitoring_enabled = in.readBoolean();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(server);
		out.writeUTF(deviceId);
		out.writeUTF(description);
		out.writeNullUTF(delete_route);
		out.writeNullUTF(Objects.toString(gateway, null));
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_37)<=0) out.writeUTF("255.255.255.0");
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_112)>=0) {
			out.writeNullUTF(Objects.toString(network, null));
			out.writeNullUTF(Objects.toString(broadcast, null));
		}
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_128)>=0) {
			out.writeNullUTF(Objects.toString(mac_address, null));
		}
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_2)>=0) {
			out.writeLong(max_bit_rate);
		}
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_35)>=0) {
			out.writeLong(monitoring_bit_rate_low);
			out.writeLong(monitoring_bit_rate_medium);
			out.writeLong(monitoring_bit_rate_high);
			out.writeLong(monitoring_bit_rate_critical);
		}
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_70)>=0) {
			out.writeBoolean(monitoring_enabled);
		}
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.NET_DEVICES;
	}

	@Override
	public String toStringImpl() throws SQLException, IOException {
		return getHost().toStringImpl()+'|'+deviceId;
	}

	public IpAddress getIPAddress(InetAddress inetAddress) throws IOException, SQLException {
		return table.getConnector().getNet().getIpAddress().getIPAddress(this, inetAddress);
	}

	public List<IpAddress> getIPAddresses() throws IOException, SQLException {
		return table.getConnector().getNet().getIpAddress().getIPAddresses(this);
	}

	public IpAddress getPrimaryIPAddress() throws SQLException, IOException {
		List<IpAddress> ips=getIPAddresses();
		List<IpAddress> matches=new ArrayList<>();
		for (IpAddress ip : ips) {
			if(!ip.isAlias()) matches.add(ip);
		}
		if(matches.isEmpty()) throw new SQLException("Unable to find primary IPAddress for NetDevice: "+deviceId+" on "+server);
		if(matches.size()>1) throw new SQLException("Found more than one primary IPAddress for NetDevice: "+deviceId+" on "+server);
		return matches.get(0);
	}

	/**
	 * Gets the bonding report from <code>/proc/net/bonding/[p]bond#</code>
	 * or {@code null} if not a bonded device.
	 */
	public String getBondingReport() throws IOException, SQLException {
		if(!deviceId.startsWith("bond")) return null;
		return table.getConnector().requestStringQuery(true, AoservProtocol.CommandID.GET_NET_DEVICE_BONDING_REPORT, pkey);
	}

	/**
	 * Gets the report from <code>/sys/class/net/<i>device</i>/statistics/...</code>
	 * or {@code null} if not a {@link Server}.
	 */
	public String getStatisticsReport() throws IOException, SQLException {
		return table.getConnector().requestStringQuery(true, AoservProtocol.CommandID.GET_NET_DEVICE_STATISTICS_REPORT, pkey);
	}
}
