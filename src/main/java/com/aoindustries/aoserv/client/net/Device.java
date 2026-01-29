/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2025  AO Industries, Inc.
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
 * along with aoserv-client.  If not, see <https://www.gnu.org/licenses/>.
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
public final class Device extends CachedObjectIntegerKey<Device> {

  static final int COLUMN_ID = 0;
  static final int COLUMN_SERVER = 1;
  public static final String COLUMN_SERVER_name = "server";
  public static final String COLUMN_DEVICE_ID_name = "deviceId";

  private int server;
  private String deviceId;
  private String description;
  private String deleteRoute;
  private InetAddress gateway;
  private InetAddress network;
  private InetAddress broadcast;
  private MacAddress macAddress;
  private long maxBitRate;
  private long monitoringBitRateLow;
  private long monitoringBitTateMedium;
  private long monitoringBitRateHigh;
  private long monitoringBitRateCritical;
  private boolean monitoringEnabled;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  Device#init(java.sql.ResultSet)
   * @see  Device#read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public Device() {
    // Do nothing
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_ID:
        return pkey;
      case COLUMN_SERVER:
        return server;
      case 2:
        return deviceId;
      case 3:
        return description;
      case 4:
        return deleteRoute;
      case 5:
        return gateway;
      case 6:
        return network;
      case 7:
        return broadcast;
      case 8:
        return macAddress;
      case 9:
        return maxBitRate == -1 ? null : maxBitRate;
      case 10:
        return monitoringBitRateLow == -1 ? null : monitoringBitRateLow;
      case 11:
        return monitoringBitTateMedium == -1 ? null : monitoringBitTateMedium;
      case 12:
        return monitoringBitRateHigh == -1 ? null : monitoringBitRateHigh;
      case 13:
        return monitoringBitRateCritical == -1 ? null : monitoringBitRateCritical;
      case 14:
        return monitoringEnabled;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
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
    if (se == null) {
      throw new SQLException("Unable to find Host: " + server);
    }
    return se;
  }

  public String getDeviceId_name() {
    return deviceId;
  }

  public DeviceId getDeviceId() throws SQLException, IOException {
    DeviceId obj = table.getConnector().getNet().getDeviceId().get(deviceId);
    if (obj == null) {
      throw new SQLException("Unable to find DeviceId: " + deviceId);
    }
    return obj;
  }

  public String getDescription() {
    return description;
  }

  public String getDeleteRoute() {
    return deleteRoute;
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
    return macAddress;
  }

  /**
   * Gets the maximum bit rate this interface can support or <code>-1</code>
   * if unknown.
   */
  public long getMaxBitRate() {
    return maxBitRate;
  }

  /**
   * Gets the 5-minute average that is considered a low-priority alert or
   * <code>-1</code> if no alert allowed at this level.
   */
  public long getMonitoringBitRateLow() {
    return monitoringBitRateLow;
  }

  /**
   * Gets the 5-minute average that is considered a medium-priority alert or
   * <code>-1</code> if no alert allowed at this level.
   */
  public long getMonitoringBitRateMedium() {
    return monitoringBitTateMedium;
  }

  /**
   * Gets the 5-minute average that is considered a high-priority alert or
   * <code>-1</code> if no alert allowed at this level.
   */
  public long getMonitoringBitRateHigh() {
    return monitoringBitRateHigh;
  }

  /**
   * Gets the 5-minute average that is considered a critical-priority alert or
   * <code>-1</code> if no alert allowed at this level.  This is the level
   * that will alert people 24x7.
   */
  public long getMonitoringBitRateCritical() {
    return monitoringBitRateCritical;
  }

  /**
   * The monitoring of a net_devices may be enabled or disabled.
   */
  public boolean isMonitoringEnabled() {
    return monitoringEnabled;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    try {
      int pos = 1;
      pkey = result.getInt(pos++);
      server = result.getInt(pos++);
      deviceId = result.getString(pos++);
      description = result.getString(pos++);
      deleteRoute = result.getString(pos++);
      gateway = InetAddress.valueOf(result.getString(pos++));
      network = InetAddress.valueOf(result.getString(pos++));
      broadcast = InetAddress.valueOf(result.getString(pos++));
      macAddress = MacAddress.valueOf(result.getString(pos++));
      maxBitRate = result.getLong(pos++);
      if (result.wasNull()) {
        maxBitRate = -1;
      }
      monitoringBitRateLow = result.getLong(pos++);
      if (result.wasNull()) {
        monitoringBitRateLow = -1;
      }
      monitoringBitTateMedium = result.getLong(pos++);
      if (result.wasNull()) {
        monitoringBitTateMedium = -1;
      }
      monitoringBitRateHigh = result.getLong(pos++);
      if (result.wasNull()) {
        monitoringBitRateHigh = -1;
      }
      monitoringBitRateCritical = result.getLong(pos++);
      if (result.wasNull()) {
        monitoringBitRateCritical = -1;
      }
      monitoringEnabled = result.getBoolean(pos++);
    } catch (ValidationException e) {
      throw new SQLException(e);
    }
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    try {
      pkey = in.readCompressedInt();
      server = in.readCompressedInt();
      deviceId = in.readUTF().intern();
      description = in.readUTF();
      deleteRoute = InternUtils.intern(in.readNullUTF());
      gateway = InternUtils.intern(InetAddress.valueOf(in.readNullUTF()));
      network = InternUtils.intern(InetAddress.valueOf(in.readNullUTF()));
      broadcast = InternUtils.intern(InetAddress.valueOf(in.readNullUTF()));
      macAddress = MacAddress.valueOf(in.readNullUTF());
      maxBitRate = in.readLong();
      monitoringBitRateLow = in.readLong();
      monitoringBitTateMedium = in.readLong();
      monitoringBitRateHigh = in.readLong();
      monitoringBitRateCritical = in.readLong();
      monitoringEnabled = in.readBoolean();
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeCompressedInt(server);
    out.writeUTF(deviceId);
    out.writeUTF(description);
    out.writeNullUTF(deleteRoute);
    out.writeNullUTF(Objects.toString(gateway, null));
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_37) <= 0) {
      out.writeUTF("255.255.255.0");
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_112) >= 0) {
      out.writeNullUTF(Objects.toString(network, null));
      out.writeNullUTF(Objects.toString(broadcast, null));
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_128) >= 0) {
      out.writeNullUTF(Objects.toString(macAddress, null));
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_2) >= 0) {
      out.writeLong(maxBitRate);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_35) >= 0) {
      out.writeLong(monitoringBitRateLow);
      out.writeLong(monitoringBitTateMedium);
      out.writeLong(monitoringBitRateHigh);
      out.writeLong(monitoringBitRateCritical);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_70) >= 0) {
      out.writeBoolean(monitoringEnabled);
    }
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.NET_DEVICES;
  }

  @Override
  public String toStringImpl() throws SQLException, IOException {
    return getHost().toStringImpl() + '|' + deviceId;
  }

  public IpAddress getIpAddress(InetAddress inetAddress) throws IOException, SQLException {
    return table.getConnector().getNet().getIpAddress().getIpAddress(this, inetAddress);
  }

  public List<IpAddress> getIpAddresses() throws IOException, SQLException {
    return table.getConnector().getNet().getIpAddress().getIpAddresses(this);
  }

  public IpAddress getPrimaryIpAddress() throws SQLException, IOException {
    List<IpAddress> ips = getIpAddresses();
    List<IpAddress> matches = new ArrayList<>();
    for (IpAddress ip : ips) {
      if (!ip.isAlias()) {
        matches.add(ip);
      }
    }
    if (matches.isEmpty()) {
      throw new SQLException("Unable to find primary IpAddress for NetDevice: " + deviceId + " on " + server);
    }
    if (matches.size() > 1) {
      throw new SQLException("Found more than one primary IpAddress for NetDevice: " + deviceId + " on " + server);
    }
    return matches.get(0);
  }

  /**
   * Gets the bonding report from <code>/proc/net/bonding/[p]bond#</code>
   * or {@code null} if not a bonded device.
   */
  public String getBondingReport() throws IOException, SQLException {
    if (!deviceId.startsWith("bond")) {
      return null;
    }
    return table.getConnector().requestStringQuery(true, AoservProtocol.CommandId.GET_NET_DEVICE_BONDING_REPORT, pkey);
  }

  /**
   * Gets the report from <code>/sys/class/net/<i>device</i>/statistics/...</code>
   * or {@code null} if not a {@link Server}.
   */
  public String getStatisticsReport() throws IOException, SQLException {
    return table.getConnector().requestStringQuery(true, AoservProtocol.CommandId.GET_NET_DEVICE_STATISTICS_REPORT, pkey);
  }
}
