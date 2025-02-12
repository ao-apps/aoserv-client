/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2025  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.distribution;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.sql.SQLStreamables;
import com.aoapps.sql.UnmodifiableTimestamp;
import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.GlobalObjectIntegerKey;
import com.aoindustries.aoserv.client.account.User;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.web.tomcat.Version;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Each <code>TechnologyName</code> may have multiple versions installed.
 * Each of those versions is a <code>TechnologyVersion</code>.
 *
 * @see  Software
 *
 * @author  AO Industries, Inc.
 */
public final class SoftwareVersion extends GlobalObjectIntegerKey<SoftwareVersion> {

  static final int COLUMN_PKEY = 0;
  public static final String COLUMN_VERSION_name = "version";
  static final String COLUMN_NAME_name = "name";

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public SoftwareVersion() {
    // Do nothing
  }

  private String name;
  private String version;
  private UnmodifiableTimestamp updated;
  private User.Name owner;
  private int operatingSystemVersion;
  private UnmodifiableTimestamp disableTime;
  private String disableReason;

  @Override
  @SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_PKEY:
        return pkey;
      case 1:
        return name;
      case 2:
        return version;
      case 3:
        return updated;
      case 4:
        return owner;
      case 5:
        return operatingSystemVersion == -1 ? null : operatingSystemVersion;
      case 6:
        return disableTime;
      case 7:
        return disableReason;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public Version getHttpdTomcatVersion(AoservConnector connector) throws IOException, SQLException {
    return connector.getWeb_tomcat().getVersion().get(pkey);
  }

  public com.aoindustries.aoserv.client.master.User getOwner(AoservConnector connector) throws SQLException, IOException {
    // May be filtered
    if (owner == null) {
      return null;
    }
    com.aoindustries.aoserv.client.master.User obj = connector.getMaster().getUser().get(owner);
    if (obj == null) {
      throw new SQLException("Unable to find MasterUser: " + owner);
    }
    return obj;
  }

  public int getOperatingSystemVersion_id() {
    return operatingSystemVersion;
  }

  public OperatingSystemVersion getOperatingSystemVersion(AoservConnector conn) throws SQLException, IOException {
    OperatingSystemVersion osv = conn.getDistribution().getOperatingSystemVersion().get(operatingSystemVersion);
    if (osv == null) {
      throw new SQLException("Unable to find OperatingSystemVersion: " + operatingSystemVersion);
    }
    return osv;
  }

  /**
   * Checks if enabled at the given time.
   */
  public boolean isEnabled(long time) {
    return disableTime == null || disableTime.getTime() > time;
  }

  @SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
  public UnmodifiableTimestamp getDisableTime() {
    return disableTime;
  }

  public String getDisableReason() {
    return disableReason;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.TECHNOLOGY_VERSIONS;
  }

  public String getTechnologyName_name() {
    return name;
  }

  public Software getTechnologyName(AoservConnector connector) throws SQLException, IOException {
    Software technologyName = connector.getDistribution().getSoftware().get(name);
    if (technologyName == null) {
      throw new SQLException("Unable to find TechnologyName: " + name);
    }
    return technologyName;
  }

  @SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
  public UnmodifiableTimestamp getUpdated() {
    return updated;
  }

  public String getVersion() {
    return version;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    try {
      pkey = result.getInt(1);
      name = result.getString(2);
      version = result.getString(3);
      updated = UnmodifiableTimestamp.valueOf(result.getTimestamp(4));
        {
          String s = result.getString(5);
          owner = AoservProtocol.FILTERED.equals(s) ? null : User.Name.valueOf(s);
        }
      operatingSystemVersion = result.getInt(6);
      if (result.wasNull()) {
        operatingSystemVersion = -1;
      }
      disableTime = UnmodifiableTimestamp.valueOf(result.getTimestamp(7));
      disableReason = result.getString(8);
    } catch (ValidationException e) {
      throw new SQLException(e);
    }
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    try {
      pkey = in.readCompressedInt();
      name = in.readUTF().intern();
      version = in.readUTF();
      updated = SQLStreamables.readUnmodifiableTimestamp(in);
        {
          String s = in.readUTF();
          if (AoservProtocol.FILTERED.equals(s)) {
            owner = null;
          } else {
            owner = User.Name.valueOf(s).intern();
          }
        }
      operatingSystemVersion = in.readCompressedInt();
      disableTime = SQLStreamables.readNullUnmodifiableTimestamp(in);
      disableReason = in.readNullUTF();
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeUTF(name);
    out.writeUTF(version);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
      out.writeLong(updated.getTime());
    } else {
      SQLStreamables.writeTimestamp(updated, out);
    }
    out.writeUTF(owner == null ? AoservProtocol.FILTERED : owner.toString());
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_108) >= 0) {
      out.writeCompressedInt(operatingSystemVersion);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_78) >= 0) {
      if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
        out.writeLong(disableTime == null ? -1 : disableTime.getTime());
      } else {
        SQLStreamables.writeNullTimestamp(disableTime, out);
      }
      out.writeNullUTF(disableReason);
    }
  }
}
