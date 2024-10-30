/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2008, 2009, 2014, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2024  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.infrastructure;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.account.AccountHost;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

/**
 * A <code>VirtualDisk</code> is a block device for a <code>VirtualServer</code>.
 *
 * @author  AO Industries, Inc.
 */
public final class VirtualDisk extends CachedObjectIntegerKey<VirtualDisk> {

  static final int COLUMN_PKEY = 0;
  static final int COLUMN_VIRTUAL_SERVER = 1;
  static final int COLUMN_DEVICE = 2;

  static final String COLUMN_VIRTUAL_SERVER_name = "virtual_server";
  static final String COLUMN_DEVICE_name = "device";

  private int virtualServer;
  private String device;
  private int minimumDiskSpeed;
  private int minimumDiskSpeedTarget;
  private int extents;
  private short weight;
  private short weightTarget;
  // TODO: smallint or enum
  private int verifyDayOfWeek;
  // TODO: smallint
  private int verifyHourOfDay;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated // Java 9: (forRemoval = true)
  public VirtualDisk() {
    // Do nothing
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_PKEY:
        return pkey;
      case COLUMN_VIRTUAL_SERVER:
        return virtualServer;
      case COLUMN_DEVICE:
        return device;
      case 3:
        return minimumDiskSpeed == -1 ? null : minimumDiskSpeed;
      case 4:
        return minimumDiskSpeedTarget == -1 ? null : minimumDiskSpeedTarget;
      case 5:
        return extents;
      case 6:
        return weight;
      case 7:
        return weightTarget;
      case 8:
        return verifyDayOfWeek;
      case 9:
        return verifyHourOfDay;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public VirtualServer getVirtualServer() throws SQLException, IOException {
    VirtualServer vs = table.getConnector().getInfrastructure().getVirtualServer().get(virtualServer);
    if (vs == null) {
      throw new SQLException("Unable to find VirtualServer: " + virtualServer);
    }
    return vs;
  }

  /**
   * Gets the per-VirtualServer unique device (without the /dev/ prefix), such
   * as <code>xvda</code> or <code>xvdb</code>.
   */
  public String getDevice() {
    return device;
  }

  /**
   * Gets the minimum disk speed or <code>-1</code> if doesn't matter.
   */
  public int getMinimumDiskSpeed() {
    return minimumDiskSpeed;
  }

  /**
   * Gets the minimum disk speed target or <code>-1</code> if doesn't matter.
   */
  public int getMinimumDiskSpeedTarget() {
    return minimumDiskSpeedTarget;
  }

  /**
   * Gets the total extents required by this device.
   */
  public int getExtents() {
    return extents;
  }

  /**
   * Gets the disk weight.
   */
  public short getWeight() {
    return weight;
  }

  /**
   * Gets the disk weight target.
   */
  public short getWeightTarget() {
    return weightTarget;
  }

  /**
   * Gets the day of the week verification will begin
   * interpreted by the virtual server's time zone setting
   * and <code>Calendar</code>.
   *
   * @see  Calendar
   */
  public int getVerifyDayOfWeek() {
    return verifyDayOfWeek;
  }

  /**
   * Gets the hour of day verification will begin
   * interpreted by the virtual server's time zone setting
   * and <code>Calendar</code>.
   *
   * @see  Calendar
   */
  public int getVerifyHourOfDay() {
    return verifyHourOfDay;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.VIRTUAL_DISKS;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    int pos = 1;
    pkey = result.getInt(pos++);
    virtualServer = result.getInt(pos++);
    device = result.getString(pos++);
    minimumDiskSpeed = result.getInt(pos++);
    if (result.wasNull()) {
      minimumDiskSpeed = -1;
    }
    minimumDiskSpeedTarget = result.getInt(pos++);
    if (result.wasNull()) {
      minimumDiskSpeedTarget = -1;
    }
    extents         = result.getInt(pos++);
    weight          = result.getShort(pos++);
    weightTarget    = result.getShort(pos++);
    verifyDayOfWeek = result.getInt(pos++);
    verifyHourOfDay = result.getInt(pos++);
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    pkey = in.readCompressedInt();
    virtualServer = in.readCompressedInt();
    device = in.readUTF().intern();
    minimumDiskSpeed = in.readCompressedInt();
    minimumDiskSpeedTarget = in.readCompressedInt();
    extents = in.readCompressedInt();
    weight = in.readShort();
    weightTarget = in.readShort();
    verifyDayOfWeek = in.readCompressedInt();
    verifyHourOfDay = in.readCompressedInt();
  }

  @Override
  public String toStringImpl() throws SQLException, IOException {
    return getVirtualServer().toStringImpl() + ":/dev/" + device;
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeCompressedInt(virtualServer);
    out.writeUTF(device);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_41) <= 0) {
      out.writeNullUTF(null);
    } // primaryMinimumRaidType
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_40) <= 0) {
      out.writeNullUTF(null);
    } // secondaryMinimumRaidType
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_41) <= 0) {
      out.writeNullUTF(null);
    } // primaryMinimumDiskType
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_40) <= 0) {
      out.writeNullUTF(null);
    } // secondaryMinimumDiskType
    out.writeCompressedInt(minimumDiskSpeed);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_43) >= 0) {
      out.writeCompressedInt(minimumDiskSpeedTarget);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_40) <= 0) {
      out.writeCompressedInt(minimumDiskSpeed);
    }
    out.writeCompressedInt(extents);
    out.writeShort(weight);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_43) >= 0) {
      out.writeShort(weightTarget);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_40) <= 0) {
      out.writeShort(weight);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_42) <= 0) {
      out.writeBoolean(false); // primaryPhysicalVolumesLocked
      out.writeBoolean(false); // secondaryPhysicalVolumesLocked
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_72) >= 0) {
      out.writeCompressedInt(verifyDayOfWeek);
      out.writeCompressedInt(verifyHourOfDay);
    }
  }

  /**
   * Begins a verification of the redundancy of the virtual disk.
   *
   * <p>User must have control_virtual_server permissions on this server.</p>
   *
   * @see  AccountHost#canControlVirtualServer()
   *
   * @return  The time the verification began, which may be in the past if a verification was already in progress
   */
  public long verify() throws SQLException, IOException {
    return table.getConnector().requestLongQuery(true, AoservProtocol.CommandId.VERIFY_VIRTUAL_DISK, this.getPkey());
  }
}
