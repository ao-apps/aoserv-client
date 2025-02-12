/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2008-2013, 2016, 2017, 2018, 2019, 2021, 2022, 2025  AO Industries, Inc.
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
import com.aoapps.lang.util.InternUtils;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.net.Host;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A <code>PhysicalServer</code> consumes space and electricity in a rack
 * and provides resources.
 *
 * @author  AO Industries, Inc.
 */
public final class PhysicalServer extends CachedObjectIntegerKey<PhysicalServer> {

  static final int COLUMN_SERVER = 0;

  static final String COLUMN_SERVER_name = "server";

  private int rack;
  private short rackUnits;
  private int ram;
  private String processorType;
  private int processorSpeed;
  private int processorCores;
  private float maxPower;
  private Boolean supportsHvm;

  // Matches aoserv-master-db/aoindustries/infrastructure/PhysicalServer.UpsType-type.sql
  public enum UpsType {
    /**
     * No UPS is supporting this device.
     */
    none,

    /**
     * The UPS is provided by the datacenter, but cannot be monitored for clean shutdown.
     */
    datacenter,

    /**
     * The UPS is an APC model and can be monitored for clean shutdown.
     */
    apc
  }

  private UpsType upsType;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public PhysicalServer() {
    // Do nothing
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_SERVER:
        return pkey;
      case 1:
        return rack == -1 ? null : rack;
      case 2:
        return rackUnits == -1 ? null : rackUnits;
      case 3:
        return ram == -1 ? null : ram;
      case 4:
        return processorType;
      case 5:
        return processorSpeed == -1 ? null : processorSpeed;
      case 6:
        return processorCores == -1 ? null : processorCores;
      case 7:
        return Float.isNaN(maxPower) ? null : maxPower;
      case 8:
        return supportsHvm;
      case 9:
        return upsType.name();
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public Host getHost() throws SQLException, IOException {
    Host se = table.getConnector().getNet().getHost().get(pkey);
    if (se == null) {
      throw new SQLException("Unable to find Host: " + pkey);
    }
    return se;
  }

  /**
   * Gets the rack this server is part of or {@code null} if not in a rack.
   */
  public Rack getRack() throws SQLException, IOException {
    if (rack == -1) {
      return null;
    }
    Rack ra = table.getConnector().getInfrastructure().getRack().get(rack);
    if (ra == null) {
      throw new SQLException("Unable to find Rack: " + rack);
    }
    return ra;
  }

  /**
   * Gets the number of rack units used by this server or <code>-1</code> if unknown
   * or not applicable.
   */
  public short getRackUnits() {
    return rackUnits;
  }

  /**
   * Gets the number of megabytes of RAM in this server or <code>-1</code> if not applicable.
   */
  public int getRam() {
    return ram;
  }

  /**
   * Gets the processor type or {@code null} if not applicable.
   */
  public ProcessorType getProcessorType() throws SQLException, IOException {
    if (processorType == null) {
      return null;
    }
    ProcessorType pt = table.getConnector().getInfrastructure().getProcessorType().get(processorType);
    if (pt == null) {
      throw new SQLException("Unable to find ProcessorType: " + processorType);
    }
    return pt;
  }

  /**
   * Gets the processor speed in MHz or <code>-1</code> if not applicable.
   */
  public int getProcessorSpeed() {
    return processorSpeed;
  }

  /**
   * Gets the total number of processor cores or <code>-1</code> if not applicable,
   * different hyperthreads are counted as separate cores.
   */
  public int getProcessorCores() {
    return processorCores;
  }

  /**
   * Gets the maximum electricity current or <code>Float.NaN</code> if not known.
   */
  public float getMaxPower() {
    return maxPower;
  }

  /**
   * Gets if this supports HVM or {@code null} if not applicable.
   */
  public Boolean getSupportsHvm() {
    return supportsHvm;
  }

  /**
   * Gets the UPS type powering this server.
   */
  public UpsType getUpsType() {
    return upsType;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.PHYSICAL_SERVERS;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    int pos = 1;
    pkey = result.getInt(pos++);
    rack = result.getInt(pos++);
    if (result.wasNull()) {
      rack = -1;
    }
    rackUnits = result.getShort(pos++);
    if (result.wasNull()) {
      rackUnits = -1;
    }
    ram = result.getInt(pos++);
    if (result.wasNull()) {
      ram = -1;
    }
    processorType = result.getString(pos++);
    processorSpeed = result.getInt(pos++);
    if (result.wasNull()) {
      processorSpeed = -1;
    }
    processorCores = result.getInt(pos++);
    if (result.wasNull()) {
      processorCores = -1;
    }
    maxPower = result.getFloat(pos++);
    if (result.wasNull()) {
      maxPower = Float.NaN;
    }
    supportsHvm = result.getBoolean(pos++);
    if (result.wasNull()) {
      supportsHvm = null;
    }
    upsType = UpsType.valueOf(result.getString(pos++));
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    pkey = in.readCompressedInt();
    rack = in.readCompressedInt();
    rackUnits = in.readShort();
    ram = in.readCompressedInt();
    processorType = InternUtils.intern(in.readNullUTF());
    processorSpeed = in.readCompressedInt();
    processorCores = in.readCompressedInt();
    maxPower = in.readFloat();
    supportsHvm = in.readBoolean() ? in.readBoolean() : null;
    upsType = UpsType.valueOf(in.readUTF());
  }

  @Override
  public String toStringImpl() throws SQLException, IOException {
    return getHost().toStringImpl();
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeCompressedInt(rack);
    out.writeShort(rackUnits);
    out.writeCompressedInt(ram);
    out.writeNullUTF(processorType);
    out.writeCompressedInt(processorSpeed);
    out.writeCompressedInt(processorCores);
    out.writeFloat(maxPower);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_37) >= 0) {
      out.writeBoolean(supportsHvm != null);
      if (supportsHvm != null) {
        out.writeBoolean(supportsHvm);
      }
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_63) >= 0) {
      out.writeUTF(upsType.name());
    }
  }
}
