/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2003-2009, 2014, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.monitoring.AlertLevel;
import com.aoindustries.aoserv.client.mysql.Server;
import com.aoindustries.aoserv.client.net.Host;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents MySQL replication for one a <code>FailoverFileReplication</code> or {@link Server}.
 *
 * @author  AO Industries, Inc.
 */
public final class MysqlReplication extends CachedObjectIntegerKey<MysqlReplication> {

  static final int COLUMN_PKEY = 0;
  static final int COLUMN_AO_SERVER = 1;
  static final int COLUMN_REPLICATION = 2;
  static final int COLUMN_MYSQL_SERVER = 3;
  static final String COLUMN_AO_SERVER_name = "ao_server";
  static final String COLUMN_REPLICATION_name = "replication";
  static final String COLUMN_MYSQL_SERVER_name = "mysql_server";

  private int aoServer;
  private int replication;
  private int mysqlServer;
  private int monitoringSecondsBehindLow;
  private int monitoringSecondsBehindMedium;
  private int monitoringSecondsBehindHigh;
  private int monitoringSecondsBehindCritical;
  private AlertLevel maxAlertLevel;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated // Java 9: (forRemoval = true)
  public MysqlReplication() {
    // Do nothing
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_PKEY:
        return pkey;
      case COLUMN_AO_SERVER:
        return aoServer == -1 ? null : aoServer;
      case COLUMN_REPLICATION:
        return replication == -1 ? null : replication;
      case COLUMN_MYSQL_SERVER:
        return mysqlServer;
      case 4:
        return monitoringSecondsBehindLow == -1 ? null : monitoringSecondsBehindLow;
      case 5:
        return monitoringSecondsBehindMedium == -1 ? null : monitoringSecondsBehindMedium;
      case 6:
        return monitoringSecondsBehindHigh == -1 ? null : monitoringSecondsBehindHigh;
      case 7:
        return monitoringSecondsBehindCritical == -1 ? null : monitoringSecondsBehindCritical;
      case 8:
        return maxAlertLevel.name();
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public com.aoindustries.aoserv.client.linux.Server getLinuxServer() throws SQLException, IOException {
    if (aoServer == -1) {
      return null;
    }
    com.aoindustries.aoserv.client.linux.Server ao = table.getConnector().getLinux().getServer().get(aoServer);
    if (ao == null) {
      throw new SQLException("Unable to find linux.Server: " + aoServer);
    }
    return ao;
  }

  public FileReplication getFailoverFileReplication() throws SQLException, IOException {
    if (replication == -1) {
      return null;
    }
    FileReplication ffr = table.getConnector().getBackup().getFileReplication().get(replication);
    if (ffr == null) {
      throw new SQLException("Unable to find FailoverFileReplication: " + replication);
    }
    return ffr;
  }

  public Server getMysqlServer() throws IOException, SQLException {
    Server ms = table.getConnector().getMysql().getServer().get(mysqlServer);
    if (ms == null) {
      throw new SQLException("Unable to find MysqlServer: " + mysqlServer);
    }
    return ms;
  }

  public int getMonitoringSecondsBehindLow() {
    return monitoringSecondsBehindLow;
  }

  public int getMonitoringSecondsBehindMedium() {
    return monitoringSecondsBehindMedium;
  }

  public int getMonitoringSecondsBehindHigh() {
    return monitoringSecondsBehindHigh;
  }

  public int getMonitoringSecondsBehindCritical() {
    return monitoringSecondsBehindCritical;
  }

  /**
   * Determines if monitoring is enabled for this replication.
   * This is based on {@link Host#isMonitoringEnabled()} derived from either
   * {@link #getLinuxServer()} or {@link #getFailoverFileReplication()}.
   */
  public boolean isMonitoringEnabled() throws SQLException, IOException {
    com.aoindustries.aoserv.client.linux.Server linuxServer =
        (aoServer != -1)
            ? getLinuxServer()
            : getFailoverFileReplication().getBackupPartition().getLinuxServer();
    return linuxServer.getHost().isMonitoringEnabled();
  }

  public AlertLevel getMaxAlertLevel() {
    return maxAlertLevel;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.FAILOVER_MYSQL_REPLICATIONS;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    int pos = 1;
    pkey = result.getInt(pos++);
    aoServer = result.getInt(pos++);
    if (result.wasNull()) {
      aoServer = -1;
    }
    replication = result.getInt(pos++);
    if (result.wasNull()) {
      replication = -1;
    }
    mysqlServer = result.getInt(pos++);
    monitoringSecondsBehindLow = result.getInt(pos++);
    if (result.wasNull()) {
      monitoringSecondsBehindLow = -1;
    }
    monitoringSecondsBehindMedium = result.getInt(pos++);
    if (result.wasNull()) {
      monitoringSecondsBehindMedium = -1;
    }
    monitoringSecondsBehindHigh = result.getInt(pos++);
    if (result.wasNull()) {
      monitoringSecondsBehindHigh = -1;
    }
    monitoringSecondsBehindCritical = result.getInt(pos++);
    if (result.wasNull()) {
      monitoringSecondsBehindCritical = -1;
    }
    maxAlertLevel = AlertLevel.valueOf(result.getString(pos++));
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    pkey = in.readCompressedInt();
    aoServer = in.readCompressedInt();
    replication = in.readCompressedInt();
    mysqlServer = in.readCompressedInt();
    monitoringSecondsBehindLow = in.readCompressedInt();
    monitoringSecondsBehindMedium = in.readCompressedInt();
    monitoringSecondsBehindHigh = in.readCompressedInt();
    monitoringSecondsBehindCritical = in.readCompressedInt();
    maxAlertLevel = AlertLevel.valueOf(in.readCompressedUTF());
  }

  @Override
  public String toStringImpl() throws IOException, SQLException {
    if (aoServer != -1) {
      return getMysqlServer().toStringImpl() + "->" + getLinuxServer().toStringImpl();
    } else {
      return getMysqlServer().toStringImpl() + "->" + getFailoverFileReplication().toStringImpl();
    }
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_59) >= 0) {
      out.writeCompressedInt(aoServer);
    }
    out.writeCompressedInt(replication);
    out.writeCompressedInt(mysqlServer);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_56) >= 0) {
      out.writeCompressedInt(monitoringSecondsBehindLow);
      out.writeCompressedInt(monitoringSecondsBehindMedium);
      out.writeCompressedInt(monitoringSecondsBehindHigh);
      out.writeCompressedInt(monitoringSecondsBehindCritical);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_74) >= 0) {
      out.writeCompressedUTF(maxAlertLevel.name());
    }
  }

  public static final class SlaveStatus {

    private final String slaveIoState;
    private final String masterLogFile;
    private final String readMasterLogPos;
    private final String relayLogFile;
    private final String relayLogPos;
    private final String relayMasterLogFile;
    private final String slaveIoRunning;
    private final String slaveSqlRunning;
    private final String lastErrno;
    private final String lastError;
    private final String skipCounter;
    private final String execMasterLogPos;
    private final String relayLogSpace;
    private final String secondsBehindMaster;

    public SlaveStatus(
        String slaveIoState,
        String masterLogFile,
        String readMasterLogPos,
        String relayLogFile,
        String relayLogPos,
        String relayMasterLogFile,
        String slaveIoRunning,
        String slaveSqlRunning,
        String lastErrno,
        String lastError,
        String skipCounter,
        String execMasterLogPos,
        String relayLogSpace,
        String secondsBehindMaster
    ) {
      this.slaveIoState = slaveIoState;
      this.masterLogFile = masterLogFile;
      this.readMasterLogPos = readMasterLogPos;
      this.relayLogFile = relayLogFile;
      this.relayLogPos = relayLogPos;
      this.relayMasterLogFile = relayMasterLogFile;
      this.slaveIoRunning = slaveIoRunning;
      this.slaveSqlRunning = slaveSqlRunning;
      this.lastErrno = lastErrno;
      this.lastError = lastError;
      this.skipCounter = skipCounter;
      this.execMasterLogPos = execMasterLogPos;
      this.relayLogSpace = relayLogSpace;
      this.secondsBehindMaster = secondsBehindMaster;
    }

    public String getSlaveIoState() {
      return slaveIoState;
    }

    public String getMasterLogFile() {
      return masterLogFile;
    }

    public String getReadMasterLogPos() {
      return readMasterLogPos;
    }

    public String getRelayLogFile() {
      return relayLogFile;
    }

    public String getRelayLogPos() {
      return relayLogPos;
    }

    public String getRelayMasterLogFile() {
      return relayMasterLogFile;
    }

    public String getSlaveIoRunning() {
      return slaveIoRunning;
    }

    public String getSlaveSqlRunning() {
      return slaveSqlRunning;
    }

    public String getLastErrno() {
      return lastErrno;
    }

    public String getLastError() {
      return lastError;
    }

    public String getSkipCounter() {
      return skipCounter;
    }

    public String getExecMasterLogPos() {
      return execMasterLogPos;
    }

    public String getRelayLogSpace() {
      return relayLogSpace;
    }

    public String getSecondsBehindMaster() {
      return secondsBehindMaster;
    }
  }

  /**
   * Gets the slave status or {@code null} if no slave status provided by MySQL.  If any error occurs, throws either
   * IOException or SQLException.
   */
  public SlaveStatus getSlaveStatus() throws IOException, SQLException {
    return table.getConnector().requestResult(
        true,
        AoservProtocol.CommandId.GET_MYSQL_SLAVE_STATUS,
        // Java 9: new AoservConnector.ResultRequest<>
        new AoservConnector.ResultRequest<SlaveStatus>() {
          private SlaveStatus result;

          @Override
          public void writeRequest(StreamableOutput out) throws IOException {
            out.writeCompressedInt(pkey);
          }

          @Override
          public void readResponse(StreamableInput in) throws IOException, SQLException {
            int code = in.readByte();
            if (code == AoservProtocol.NEXT) {
              result = new SlaveStatus(
                  in.readNullUTF(),
                  in.readNullUTF(),
                  in.readNullUTF(),
                  in.readNullUTF(),
                  in.readNullUTF(),
                  in.readNullUTF(),
                  in.readNullUTF(),
                  in.readNullUTF(),
                  in.readNullUTF(),
                  in.readNullUTF(),
                  in.readNullUTF(),
                  in.readNullUTF(),
                  in.readNullUTF(),
                  in.readNullUTF()
              );
            } else if (code == AoservProtocol.DONE) {
              result = null;
            } else {
              AoservProtocol.checkResult(code, in);
              throw new IOException("Unexpected response code: " + code);
            }
          }

          @Override
          public SlaveStatus afterRelease() {
            return result;
          }
        }
    );
  }
}
