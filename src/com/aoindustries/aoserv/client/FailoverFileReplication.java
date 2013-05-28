/*
 * Copyright 2003-2013 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.HostAddress;
import com.aoindustries.aoserv.client.validator.InetAddress;
import com.aoindustries.aoserv.client.validator.ValidationException;
import com.aoindustries.io.BitRateProvider;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.lang.ObjectUtils;
import com.aoindustries.util.BufferManager;
import com.aoindustries.util.InternUtils;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Causes a server to replicate itself to another machine on a regular basis.
 *
 * @author  AO Industries, Inc.
 */
final public class FailoverFileReplication extends CachedObjectIntegerKey<FailoverFileReplication> implements BitRateProvider {

    static final int COLUMN_PKEY=0;
    static final int COLUMN_SERVER=1;
    static final String COLUMN_SERVER_name = "server";
    static final String COLUMN_BACKUP_PARTITION_name = "backup_partition";

    int server;
    private int backup_partition;
    private Long max_bit_rate;
    private boolean use_compression;
    private short retention;
    private HostAddress connect_address;
    private InetAddress connect_from;
    private boolean enabled;
    private int quota_gid;

    public int addFailoverFileLog(long startTime, long endTime, int scanned, int updated, long bytes, boolean isSuccessful) throws IOException, SQLException {
	return table.connector.getFailoverFileLogs().addFailoverFileLog(this, startTime, endTime, scanned, updated, bytes, isSuccessful);
    }

    @Override
    public Long getBitRate() {
        return max_bit_rate;
    }

    public void setBitRate(Long bitRate) throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.SET_FAILOVER_FILE_REPLICATION_BIT_RATE, pkey, bitRate==null ? -1 : bitRate.longValue());
    }

    public int getBlockSize() {
        return BufferManager.BUFFER_SIZE;
    }

    @Override
    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_SERVER: return server;
            case 2: return backup_partition;
            case 3: return max_bit_rate;
            case 4: return use_compression;
            case 5: return retention;
            case 6: return connect_address;
            case 7: return connect_from;
            case 8: return enabled;
            case 9: return quota_gid==-1?null:Integer.valueOf(quota_gid);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public List<FailoverFileSchedule> getFailoverFileSchedules() throws IOException, SQLException {
        return table.connector.getFailoverFileSchedules().getFailoverFileSchedules(this);
    }

    public Server getServer() throws SQLException, IOException {
        Server se=table.connector.getServers().get(server);
        if(se==null) throw new SQLException("Unable to find Server: "+server);
        return se;
    }

    /**
     * May be filtered.
     */
    public BackupPartition getBackupPartition() throws SQLException, IOException {
        return table.connector.getBackupPartitions().get(backup_partition);
    }

    /**
     * Gets the most recent (by start time) log entries for failover file replications, up to the
     * maximum number of rows.  May return less than this number of rows.  The results
     * are sorted by start_time descending (most recent at index zero).
     */
    public List<FailoverFileLog> getFailoverFileLogs(int maxRows) throws IOException, SQLException {
        return table.connector.getFailoverFileLogs().getFailoverFileLogs(this, maxRows);
    }

    public List<FailoverMySQLReplication> getFailoverMySQLReplications() throws IOException, SQLException {
        return table.connector.getFailoverMySQLReplications().getFailoverMySQLReplications(this);
    }

    public boolean getUseCompression() {
        return use_compression;
    }

    public BackupRetention getRetention() throws SQLException, IOException {
        BackupRetention br=table.connector.getBackupRetentions().get(retention);
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
    public LinuxID getQuotaGID() throws SQLException {
        if(quota_gid==-1) return null;
        LinuxID lid = table.connector.getLinuxIDs().get(quota_gid);
        if(lid==null) throw new SQLException("Unable to find LinuxID: "+quota_gid);
        return lid;
    }

    @Override
    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.FAILOVER_FILE_REPLICATIONS;
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
            quota_gid=result.getInt(pos++);
            if(result.wasNull()) quota_gid=-1;
        } catch(ValidationException e) {
            SQLException exc = new SQLException(e.getLocalizedMessage());
            exc.initCause(e);
            throw exc;
        }
    }

    @Override
    public void read(CompressedDataInputStream in) throws IOException {
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
            quota_gid=in.readCompressedInt();
        } catch(ValidationException e) {
            IOException exc = new IOException(e.getLocalizedMessage());
            exc.initCause(e);
            throw exc;
        }
    }

    @Override
    String toStringImpl() throws SQLException, IOException {
        return getServer().toStringImpl()+"->"+getBackupPartition().toStringImpl();
    }

    @Override
    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeCompressedInt(server);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_30)<=0) out.writeCompressedInt(149); // to_server (hard-coded xen2.mob.aoindustries.com)
        if(version.compareTo(AOServProtocol.Version.VERSION_1_31)>=0) out.writeCompressedInt(backup_partition);
        if(
            version.compareTo(AOServProtocol.Version.VERSION_1_0_A_105)>=0
            && version.compareTo(AOServProtocol.Version.VERSION_1_61)<=0
        ) {
            int maxBitRateInt;
            if(max_bit_rate==null) maxBitRateInt = -1;
            else if(max_bit_rate>Integer.MAX_VALUE) maxBitRateInt = Integer.MAX_VALUE;
            else if(max_bit_rate<0) throw new IOException("Illegal bit rate: " + max_bit_rate);
            else maxBitRateInt = max_bit_rate.intValue();
            out.writeInt(maxBitRateInt);
        } else if(version.compareTo(AOServProtocol.Version.VERSION_1_62)>=0) {
            out.writeLong(max_bit_rate==null ? -1 : max_bit_rate);
        }
        if(version.compareTo(AOServProtocol.Version.VERSION_1_30)<=0) out.writeLong(-1); // last_start_time
        if(version.compareTo(AOServProtocol.Version.VERSION_1_9)>=0) out.writeBoolean(use_compression);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_13)>=0) out.writeShort(retention);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_14)>=0) out.writeNullUTF(ObjectUtils.toString(connect_address));
        if(version.compareTo(AOServProtocol.Version.VERSION_1_22)>=0) out.writeNullUTF(ObjectUtils.toString(connect_from));
        if(version.compareTo(AOServProtocol.Version.VERSION_1_15)>=0) out.writeBoolean(enabled);
        if(
            version.compareTo(AOServProtocol.Version.VERSION_1_17)>=0
            && version.compareTo(AOServProtocol.Version.VERSION_1_30)<=0
        ) {
            out.writeUTF("/var/backup"); // to_path (hard-coded /var/backup like found on xen2.mob.aoindustries.com)
            out.writeBoolean(false); // chunk_always
        }
        if(version.compareTo(AOServProtocol.Version.VERSION_1_31)>=0) out.writeCompressedInt(quota_gid);
    }

    public int addFileBackupSetting(String path, boolean backupEnabled, boolean required) throws IOException, SQLException {
        return table.connector.getFileBackupSettings().addFileBackupSetting(this, path, backupEnabled, required);
    }

    public FileBackupSetting getFileBackupSetting(String path) throws IOException, SQLException {
        return table.connector.getFileBackupSettings().getFileBackupSetting(this, path);
    }

    public List<FileBackupSetting> getFileBackupSettings() throws IOException, SQLException {
        return table.connector.getFileBackupSettings().getFileBackupSettings(this);
    }
    
    public void setFailoverFileSchedules(List<Short> hours, List<Short> minutes) throws IOException, SQLException {
        table.connector.getFailoverFileSchedules().setFailoverFileSchedules(this, hours, minutes);
    }

    public void setFileBackupSettings(List<String> paths, List<Boolean> backupEnableds, List<Boolean> requireds) throws IOException, SQLException {
        table.connector.getFileBackupSettings().setFileBackupSettings(this, paths, backupEnableds, requireds);
    }

    public AOServer.DaemonAccess requestReplicationDaemonAccess() throws IOException, SQLException {
        return table.connector.requestResult(
            true,
            new AOServConnector.ResultRequest<AOServer.DaemonAccess>() {
                private AOServer.DaemonAccess daemonAccess;
                public void writeRequest(CompressedDataOutputStream out) throws IOException {
                    out.writeCompressedInt(AOServProtocol.CommandID.REQUEST_REPLICATION_DAEMON_ACCESS.ordinal());
                    out.writeCompressedInt(pkey);
                }
                public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
                    int code=in.readByte();
                    if(code==AOServProtocol.DONE) {
                        try {
                            daemonAccess = new AOServer.DaemonAccess(
                                in.readUTF(),
                                HostAddress.valueOf(in.readUTF()),
                                in.readCompressedInt(),
                                in.readLong()
                            );
                        } catch(ValidationException e) {
                            IOException exc = new IOException(e.getLocalizedMessage());
                            exc.initCause(e);
                            throw exc;
                        }
                    } else {
                        AOServProtocol.checkResult(code, in);
                        throw new IOException("Unexpected response code: "+code);
                    }
                }
                public AOServer.DaemonAccess afterRelease() {
                    return daemonAccess;
                }
            }
        );
    }
}