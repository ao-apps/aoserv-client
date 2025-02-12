/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2003-2013, 2015, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2023, 2025  AO Industries, Inc.
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

import com.aoapps.hodgepodge.io.BitRateProvider;
import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.util.BufferManager;
import com.aoapps.lang.util.InternUtils;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.net.HostAddress;
import com.aoapps.net.InetAddress;
import com.aoapps.net.Port;
import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.linux.LinuxId;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.net.Host;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/**
 * Causes a server to replicate itself to another machine on a regular basis.
 *
 * @author  AO Industries, Inc.
 */
public final class FileReplication extends CachedObjectIntegerKey<FileReplication> implements BitRateProvider {

  static final int COLUMN_PKEY = 0;
  static final int COLUMN_SERVER = 1;
  static final String COLUMN_SERVER_name = "server";
  static final String COLUMN_BACKUP_PARTITION_name = "backup_partition";

  private int server;
  private int backupPartition;
  private Long maxBitRate;
  private boolean useCompression;
  private short retention;
  private HostAddress connectAddress;
  private InetAddress connectFrom;
  private boolean enabled;
  private LinuxId quotaGid;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public FileReplication() {
    // Do nothing
  }

  public int addFailoverFileLog(long startTime, long endTime, int scanned, int updated, long bytes, boolean isSuccessful) throws IOException, SQLException {
    return table.getConnector().getBackup().getFileReplicationLog().addFailoverFileLog(this, startTime, endTime, scanned, updated, bytes, isSuccessful);
  }

  @Override
  public Long getBitRate() {
    return maxBitRate;
  }

  public void setBitRate(Long bitRate) throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(true, AoservProtocol.CommandId.SET_FAILOVER_FILE_REPLICATION_BIT_RATE, pkey, bitRate == null ? -1 : bitRate);
  }

  @Override
  public int getBlockSize() {
    return BufferManager.BUFFER_SIZE;
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_PKEY:
        return pkey;
      case COLUMN_SERVER:
        return server;
      case 2:
        return backupPartition;
      case 3:
        return maxBitRate;
      case 4:
        return useCompression;
      case 5:
        return retention;
      case 6:
        return connectAddress;
      case 7:
        return connectFrom;
      case 8:
        return enabled;
      case 9:
        return quotaGid;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public List<FileReplicationSchedule> getFailoverFileSchedules() throws IOException, SQLException {
    return table.getConnector().getBackup().getFileReplicationSchedule().getFailoverFileSchedules(this);
  }

  public Host getHost() throws SQLException, IOException {
    Host se = table.getConnector().getNet().getHost().get(server);
    if (se == null) {
      throw new SQLException("Unable to find Host: " + server);
    }
    return se;
  }

  /**
   * May be filtered.
   */
  public BackupPartition getBackupPartition() throws SQLException, IOException {
    return table.getConnector().getBackup().getBackupPartition().get(backupPartition);
  }

  /**
   * Gets the most recent (by start time) log entries for failover file replications, up to the
   * maximum number of rows.  May return less than this number of rows.  The results
   * are sorted by start_time descending (most recent at index zero).
   */
  public List<FileReplicationLog> getFailoverFileLogs(int maxRows) throws IOException, SQLException {
    return table.getConnector().getBackup().getFileReplicationLog().getFailoverFileLogs(this, maxRows);
  }

  public List<MysqlReplication> getFailoverMysqlReplications() throws IOException, SQLException {
    return table.getConnector().getBackup().getMysqlReplication().getFailoverMysqlReplications(this);
  }

  public boolean getUseCompression() {
    return useCompression;
  }

  public BackupRetention getRetention() throws SQLException, IOException {
    BackupRetention br = table.getConnector().getBackup().getBackupRetention().get(retention);
    if (br == null) {
      throw new SQLException("Unable to find BackupRetention: " + retention);
    }
    return br;
  }

  /**
   * Gets a connect address that should override the normal address resolution mechanisms.  This allows
   * a replication to be specifically sent through a gigabit connection or alternate route.
   */
  public HostAddress getConnectAddress() {
    return connectAddress;
  }

  /**
   * Gets the address connections should be made from that overrides the normal address resolution mechanism.  This
   * allows a replication to be specifically sent through a gigabit connection or alternate route.
   */
  public InetAddress getConnectFrom() {
    return connectFrom;
  }

  /**
   * Gets the enabled flag for this replication.
   */
  public boolean getEnabled() {
    return enabled;
  }

  /**
   * When set, the group ID will always be set to this value, regardless what the client sends.
   * This gid is only unique per backup_partition, thus on a single host the same gid
   * may be used for different accounts.  Also, the gid will not exist in /etc/groups and has
   * nothing to do with the shell accounts on the server.  This is to track quotas per backup
   * partition by group ID.  This may only be set (and must be set) when stored on a
   * backup_partition with quota_enabled.
   */
  public LinuxId getQuotaGid() {
    return quotaGid;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.FAILOVER_FILE_REPLICATIONS;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    try {
      int pos = 1;
      pkey = result.getInt(pos++);
      server = result.getInt(pos++);
      backupPartition = result.getInt(pos++);
      long maxBitRateLong = result.getLong(pos++);
      maxBitRate = result.wasNull() ? null : maxBitRateLong;
      useCompression = result.getBoolean(pos++);
      retention = result.getShort(pos++);
      connectAddress = HostAddress.valueOf(result.getString(pos++));
      connectFrom = InetAddress.valueOf(result.getString(pos++));
      enabled = result.getBoolean(pos++);
        {
          int i = result.getInt(pos++);
          quotaGid = result.wasNull() ? null : LinuxId.valueOf(i);
        }
    } catch (ValidationException e) {
      throw new SQLException(e);
    }
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    try {
      pkey = in.readCompressedInt();
      server = in.readCompressedInt();
      backupPartition = in.readCompressedInt();
      long maxBitRateLong = in.readLong();
      maxBitRate = maxBitRateLong == -1 ? null : maxBitRateLong;
      useCompression = in.readBoolean();
      retention = in.readShort();
      connectAddress = InternUtils.intern(HostAddress.valueOf(in.readNullUTF()));
      connectFrom = InternUtils.intern(InetAddress.valueOf(in.readNullUTF()));
      enabled = in.readBoolean();
        {
          int i = in.readCompressedInt();
          quotaGid = (i == -1) ? null : LinuxId.valueOf(i);
        }
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  @Override
  public String toStringImpl() throws SQLException, IOException {
    return getHost().toStringImpl() + "→" + getBackupPartition().toStringImpl();
  }

  @Override
  @SuppressWarnings("null") // Should not be necessary, bug in NetBeans 12.0?
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeCompressedInt(server);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_30) <= 0) {
      out.writeCompressedInt(149);
    } // to_server (hard-coded xen2.mob.aoindustries.com)
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_31) >= 0) {
      out.writeCompressedInt(backupPartition);
    }
    if (
        protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_105) >= 0
            && protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_61) <= 0
    ) {
      int maxBitRateInt;
      if (maxBitRate == null) {
        maxBitRateInt = -1;
      } else if (maxBitRate > Integer.MAX_VALUE) {
        maxBitRateInt = Integer.MAX_VALUE;
      } else if (maxBitRate < 0) {
        throw new IOException("Illegal bit rate: " + maxBitRate);
      } else {
        maxBitRateInt = maxBitRate.intValue();
      }
      out.writeInt(maxBitRateInt);
    } else if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_62) >= 0) {
      out.writeLong((maxBitRate == null) ? -1 : maxBitRate);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_30) <= 0) {
      out.writeLong(-1);
    } // last_start_time
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_9) >= 0) {
      out.writeBoolean(useCompression);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_13) >= 0) {
      out.writeShort(retention);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_14) >= 0) {
      out.writeNullUTF(Objects.toString(connectAddress, null));
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_22) >= 0) {
      out.writeNullUTF(Objects.toString(connectFrom, null));
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_15) >= 0) {
      out.writeBoolean(enabled);
    }
    if (
        protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_17) >= 0
            && protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_30) <= 0
    ) {
      out.writeUTF("/var/backup"); // to_path (hard-coded /var/backup like found on xen2.mob.aoindustries.com)
      out.writeBoolean(false); // chunk_always
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_31) >= 0) {
      out.writeCompressedInt(quotaGid == null ? -1 : quotaGid.getId());
    }
  }

  public int addFileBackupSetting(String path, boolean backupEnabled, boolean required) throws IOException, SQLException {
    return table.getConnector().getBackup().getFileReplicationSetting().addFileBackupSetting(this, path, backupEnabled, required);
  }

  public FileReplicationSetting getFileBackupSetting(String path) throws IOException, SQLException {
    return table.getConnector().getBackup().getFileReplicationSetting().getFileBackupSetting(this, path);
  }

  public List<FileReplicationSetting> getFileBackupSettings() throws IOException, SQLException {
    return table.getConnector().getBackup().getFileReplicationSetting().getFileBackupSettings(this);
  }

  public void setFailoverFileSchedules(List<Short> hours, List<Short> minutes) throws IOException, SQLException {
    table.getConnector().getBackup().getFileReplicationSchedule().setFailoverFileSchedules(this, hours, minutes);
  }

  public void setFileBackupSettings(List<String> paths, List<Boolean> backupEnableds, List<Boolean> requireds) throws IOException, SQLException {
    table.getConnector().getBackup().getFileReplicationSetting().setFileBackupSettings(this, paths, backupEnableds, requireds);
  }

  public Server.DaemonAccess requestReplicationDaemonAccess() throws IOException, SQLException {
    return table.getConnector().requestResult(
        true,
        AoservProtocol.CommandId.REQUEST_REPLICATION_DAEMON_ACCESS,
        new AoservConnector.ResultRequest<>() {
          private Server.DaemonAccess daemonAccess;

          @Override
          public void writeRequest(StreamableOutput out) throws IOException {
            out.writeCompressedInt(pkey);
          }

          @Override
          public void readResponse(StreamableInput in) throws IOException, SQLException {
            int code = in.readByte();
            if (code == AoservProtocol.DONE) {
              try {
                daemonAccess = new Server.DaemonAccess(
                    in.readUTF(),
                    HostAddress.valueOf(in.readUTF()),
                    Port.valueOf(
                        in.readCompressedInt(),
                        com.aoapps.net.Protocol.TCP
                    ),
                    in.readLong()
                );
              } catch (ValidationException e) {
                throw new IOException(e);
              }
            } else {
              AoservProtocol.checkResult(code, in);
              throw new IOException("Unexpected response code: " + code);
            }
          }

          @Override
          public Server.DaemonAccess afterRelease() {
            return daemonAccess;
          }
        }
    );
  }

  public static class Activity {
    private final long timeSince;
    private final String message;

    private Activity(long timeSince, String message) {
      this.timeSince = timeSince;
      this.message = message;
    }

    /**
     * Gets the amount of time since the activity was logged or <code>-1</code> if no activity.
     */
    public long getTimeSince() {
      return timeSince;
    }

    /**
     * Gets the message for the activity or <code>""</code> if no activity.
     */
    public String getMessage() {
      return message;
    }
  }

  public Activity getActivity() throws IOException, SQLException {
    return table.getConnector().requestResult(
        true,
        AoservProtocol.CommandId.GET_FAILOVER_FILE_REPLICATION_ACTIVITY,
        new AoservConnector.ResultRequest<>() {
          private Activity activity;

          @Override
          public void writeRequest(StreamableOutput out) throws IOException {
            out.writeCompressedInt(pkey);
          }

          @Override
          public void readResponse(StreamableInput in) throws IOException, SQLException {
            int code = in.readByte();
            if (code == AoservProtocol.DONE) {
              activity = new Activity(
                  in.readLong(),
                  in.readUTF()
              );
            } else {
              AoservProtocol.checkResult(code, in);
              throw new IOException("Unexpected response code: " + code);
            }
          }

          @Override
          public Activity afterRelease() {
            return activity;
          }
        }
    );
  }
}
