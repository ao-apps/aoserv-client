/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2003-2013, 2015, 2016, 2017, 2018, 2019, 2020  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.backup;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.linux.LinuxId;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.net.Host;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.io.BitRateProvider;
import com.aoindustries.io.stream.StreamableInput;
import com.aoindustries.io.stream.StreamableOutput;
import com.aoindustries.net.HostAddress;
import com.aoindustries.net.InetAddress;
import com.aoindustries.net.Port;
import com.aoindustries.util.BufferManager;
import com.aoindustries.util.InternUtils;
import com.aoindustries.validation.ValidationException;
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
final public class FileReplication extends CachedObjectIntegerKey<FileReplication> implements BitRateProvider {

	static final int COLUMN_PKEY=0;
	static final int COLUMN_SERVER=1;
	static final String COLUMN_SERVER_name = "server";
	static final String COLUMN_BACKUP_PARTITION_name = "backup_partition";

	private int server;
	private int backup_partition;
	private Long max_bit_rate;
	private boolean use_compression;
	private short retention;
	private HostAddress connect_address;
	private InetAddress connect_from;
	private boolean enabled;
	private LinuxId quota_gid;

	public int addFailoverFileLog(long startTime, long endTime, int scanned, int updated, long bytes, boolean isSuccessful) throws IOException, SQLException {
		return table.getConnector().getBackup().getFileReplicationLog().addFailoverFileLog(this, startTime, endTime, scanned, updated, bytes, isSuccessful);
	}

	@Override
	public Long getBitRate() {
		return max_bit_rate;
	}

	public void setBitRate(Long bitRate) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.SET_FAILOVER_FILE_REPLICATION_BIT_RATE, pkey, bitRate==null ? -1 : bitRate);
	}

	@Override
	public int getBlockSize() {
		return BufferManager.BUFFER_SIZE;
	}

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_SERVER: return server;
			case 2: return backup_partition;
			case 3: return max_bit_rate;
			case 4: return use_compression;
			case 5: return retention;
			case 6: return connect_address;
			case 7: return connect_from;
			case 8: return enabled;
			case 9: return quota_gid;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	public List<FileReplicationSchedule> getFailoverFileSchedules() throws IOException, SQLException {
		return table.getConnector().getBackup().getFileReplicationSchedule().getFailoverFileSchedules(this);
	}

	public Host getHost() throws SQLException, IOException {
		Host se=table.getConnector().getNet().getHost().get(server);
		if(se==null) throw new SQLException("Unable to find Host: "+server);
		return se;
	}

	/**
	 * May be filtered.
	 */
	public BackupPartition getBackupPartition() throws SQLException, IOException {
		return table.getConnector().getBackup().getBackupPartition().get(backup_partition);
	}

	/**
	 * Gets the most recent (by start time) log entries for failover file replications, up to the
	 * maximum number of rows.  May return less than this number of rows.  The results
	 * are sorted by start_time descending (most recent at index zero).
	 */
	public List<FileReplicationLog> getFailoverFileLogs(int maxRows) throws IOException, SQLException {
		return table.getConnector().getBackup().getFileReplicationLog().getFailoverFileLogs(this, maxRows);
	}

	public List<MysqlReplication> getFailoverMySQLReplications() throws IOException, SQLException {
		return table.getConnector().getBackup().getMysqlReplication().getFailoverMySQLReplications(this);
	}

	public boolean getUseCompression() {
		return use_compression;
	}

	public BackupRetention getRetention() throws SQLException, IOException {
		BackupRetention br=table.getConnector().getBackup().getBackupRetention().get(retention);
		if(br==null) throw new SQLException("Unable to find BackupRetention: "+retention);
		return br;
	}

	/**
	 * Gets a connect address that should override the normal address resolution mechanisms.  This allows
	 * a replication to be specifically sent through a gigabit connection or alternate route.
	 */
	public HostAddress getConnectAddress() {
		return connect_address;
	}

	/**
	 * Gets the address connections should be made from that overrides the normal address resolution mechanism.  This
	 * allows a replication to be specifically sent through a gigabit connection or alternate route.
	 */
	public InetAddress getConnectFrom() {
		return connect_from;
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
	public LinuxId getQuotaGID() {
		return quota_gid;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.FAILOVER_FILE_REPLICATIONS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			int pos = 1;
			pkey=result.getInt(pos++);
			server=result.getInt(pos++);
			backup_partition=result.getInt(pos++);
			long maxBitRateLong = result.getLong(pos++);
			max_bit_rate = result.wasNull() ? null : maxBitRateLong;
			use_compression=result.getBoolean(pos++);
			retention=result.getShort(pos++);
			connect_address=HostAddress.valueOf(result.getString(pos++));
			connect_from=InetAddress.valueOf(result.getString(pos++));
			enabled=result.getBoolean(pos++);
			{
				int i = result.getInt(pos++);
				quota_gid = result.wasNull() ? null : LinuxId.valueOf(i);
			}
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
		try {
			pkey=in.readCompressedInt();
			server=in.readCompressedInt();
			backup_partition=in.readCompressedInt();
			long maxBitRateLong = in.readLong();
			max_bit_rate = maxBitRateLong==-1 ? null : maxBitRateLong;
			use_compression=in.readBoolean();
			retention=in.readShort();
			connect_address=InternUtils.intern(HostAddress.valueOf(in.readNullUTF()));
			connect_from=InternUtils.intern(InetAddress.valueOf(in.readNullUTF()));
			enabled=in.readBoolean();
			{
				int i = in.readCompressedInt();
				quota_gid = (i == -1) ? null : LinuxId.valueOf(i);
			}
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public String toStringImpl() throws SQLException, IOException {
		return getHost().toStringImpl()+"->"+getBackupPartition().toStringImpl();
	}

	@Override
	@SuppressWarnings("null") // Should not be necessary, bug in NetBeans 12.0?
	public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(server);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_30)<=0) out.writeCompressedInt(149); // to_server (hard-coded xen2.mob.aoindustries.com)
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_31)>=0) out.writeCompressedInt(backup_partition);
		if(
			protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_105)>=0
			&& protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_61)<=0
		) {
			int maxBitRateInt;
			if(max_bit_rate==null) maxBitRateInt = -1;
			else if(max_bit_rate>Integer.MAX_VALUE) maxBitRateInt = Integer.MAX_VALUE;
			else if(max_bit_rate<0) throw new IOException("Illegal bit rate: " + max_bit_rate);
			else maxBitRateInt = max_bit_rate.intValue();
			out.writeInt(maxBitRateInt);
		} else if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_62)>=0) {
			out.writeLong((max_bit_rate == null) ? -1 : max_bit_rate);
		}
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_30)<=0) out.writeLong(-1); // last_start_time
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_9)>=0) out.writeBoolean(use_compression);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_13)>=0) out.writeShort(retention);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_14)>=0) out.writeNullUTF(Objects.toString(connect_address, null));
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_22)>=0) out.writeNullUTF(Objects.toString(connect_from, null));
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_15)>=0) out.writeBoolean(enabled);
		if(
			protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_17)>=0
			&& protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_30)<=0
		) {
			out.writeUTF("/var/backup"); // to_path (hard-coded /var/backup like found on xen2.mob.aoindustries.com)
			out.writeBoolean(false); // chunk_always
		}
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_31)>=0) {
			out.writeCompressedInt(quota_gid == null ? -1 : quota_gid.getId());
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
			AoservProtocol.CommandID.REQUEST_REPLICATION_DAEMON_ACCESS,
			new AOServConnector.ResultRequest<Server.DaemonAccess>() {
				private Server.DaemonAccess daemonAccess;

				@Override
				public void writeRequest(StreamableOutput out) throws IOException {
					out.writeCompressedInt(pkey);
				}

				@Override
				public void readResponse(StreamableInput in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AoservProtocol.DONE) {
						try {
							daemonAccess = new Server.DaemonAccess(
								in.readUTF(),
								HostAddress.valueOf(in.readUTF()),
								Port.valueOf(
									in.readCompressedInt(),
									com.aoindustries.net.Protocol.TCP
								),
								in.readLong()
							);
						} catch(ValidationException e) {
							throw new IOException(e);
						}
					} else {
						AoservProtocol.checkResult(code, in);
						throw new IOException("Unexpected response code: "+code);
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
			AoservProtocol.CommandID.GET_FAILOVER_FILE_REPLICATION_ACTIVITY,
			new AOServConnector.ResultRequest<Activity>() {
				private Activity activity;

				@Override
				public void writeRequest(StreamableOutput out) throws IOException {
					out.writeCompressedInt(pkey);
				}

				@Override
				public void readResponse(StreamableInput in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AoservProtocol.DONE) {
						activity = new Activity(
							in.readLong(),
							in.readUTF()
						);
					} else {
						AoservProtocol.checkResult(code, in);
						throw new IOException("Unexpected response code: "+code);
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
