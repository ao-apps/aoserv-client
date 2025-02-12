/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2003-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2025  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.backup;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoindustries.aoserv.client.AoservObject;
import com.aoindustries.aoserv.client.AoservTable;
import com.aoindustries.aoserv.client.SingleTableObject;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.net.Host;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A <code>BackupReport</code> is generated once per day per package and per server.  This information
 * is averaged through a month and used for account billing.  The reports are processed at or near 2:00am
 * and basically represent the report for the previous day.
 *
 * @author  AO Industries, Inc.
 */
public final class BackupReport extends AoservObject<Integer, BackupReport> implements SingleTableObject<Integer, BackupReport> {

  static final int COLUMN_PKEY = 0;
  static final String COLUMN_DATE_name = "date";
  static final String COLUMN_SERVER_name = "server";
  static final String COLUMN_PACKAGE_name = "package";

  /**
   * The hour of the day (in master server time zone) that backup reports will be created.
   */
  public static final int BACKUP_REPORT_HOUR = 2;

  /**
   * The minute (in master server time zone) that backup reports will be created.
   */
  public static final int BACKUP_REPORT_MINUTE = 15;

  /**
   * The maximum number of days that reports will be maintained.  This is roughly 5 years.
   */
  public static final int MAX_REPORT_AGE = 2 * 366 + 3 * 365; // Assumes worst-case of two leap years in 5-year span.

  private int pkey;
  private int host_id;
  private int package_id;
  private long date;
  private int fileCount;
  private long diskSize;

  private AoservTable<Integer, BackupReport> table;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public BackupReport() {
    // Do nothing
  }

  @Override
  public boolean equals(Object obj) {
    return
        (obj instanceof BackupReport)
            && ((BackupReport) obj).pkey == pkey;
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_PKEY:
        return pkey;
      case 1:
        return host_id;
      case 2:
        return package_id;
      case 3:
        return getDate();
      case 4:
        return fileCount;
      case 5:
        return diskSize;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public int getPkey() {
    return pkey;
  }

  public int getHost_id() {
    return host_id;
  }

  public Host getHost() throws SQLException, IOException {
    Host se = table.getConnector().getNet().getHost().get(host_id);
    if (se == null) {
      throw new SQLException("Unable to find Host: " + host_id);
    }
    return se;
  }

  public int getPackage_id() {
    return package_id;
  }

  public Package getPackage() throws IOException, SQLException {
    Package pk = table.getConnector().getBilling().getPackage().get(package_id);
    if (pk == null) {
      throw new SQLException("Unable to find Package: " + package_id);
    }
    return pk;
  }

  public Date getDate() {
    return new Date(date);
  }

  public int getFileCount() {
    return fileCount;
  }

  public long getDiskSize() {
    return diskSize;
  }

  @Override
  public Integer getKey() {
    return pkey;
  }

  @Override
  public AoservTable<Integer, BackupReport> getTable() {
    return table;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.BACKUP_REPORTS;
  }

  @Override
  public int hashCode() {
    return pkey;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    pkey = result.getInt(1);
    host_id = result.getInt(2);
    package_id = result.getInt(3);
    date = result.getDate(4).getTime();
    fileCount = result.getInt(5);
    diskSize = result.getLong(6);
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    pkey = in.readCompressedInt();
    host_id = in.readCompressedInt();
    package_id = in.readCompressedInt();
    date = in.readLong();
    fileCount = in.readInt();
    diskSize = in.readLong();
  }

  @Override
  public void setTable(AoservTable<Integer, BackupReport> table) {
    if (this.table != null) {
      throw new IllegalStateException("table already set");
    }
    this.table = table;
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeCompressedInt(host_id);
    out.writeCompressedInt(package_id);
    out.writeLong(date);
    out.writeInt(fileCount);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_30) <= 0) {
      out.writeLong(0); // uncompressed_size
      out.writeLong(0); // compressed_size
    }
    out.writeLong(diskSize);
  }
}
