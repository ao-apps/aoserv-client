package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.sql.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.List;

/**
 * Causes a server to replicate itself to another machine on a regular basis.
 *
 * @version  1.0a
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
    private int max_bit_rate;
    private boolean use_compression;
    private short retention;
    private String connect_address;
    private String connect_from;
    private boolean enabled;
    private int quota_gid;

    public int addFailoverFileLog(long startTime, long endTime, int scanned, int updated, long bytes, boolean isSuccessful) {
	return table.connector.failoverFileLogs.addFailoverFileLog(this, startTime, endTime, scanned, updated, bytes, isSuccessful);
    }

    public int getBitRate() {
        return max_bit_rate;
    }

    public void setBitRate(int bitRate) {
        table.connector.requestUpdateIL(AOServProtocol.CommandID.SET_FAILOVER_FILE_REPLICATION_BIT_RATE, pkey, bitRate);
    }

    public int getBlockSize() {
        return BufferManager.BUFFER_SIZE;
    }

    @Override
    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_SERVER: return server;
            case 2: return backup_partition;
            case 3: return max_bit_rate==-1?null:Integer.valueOf(max_bit_rate);
            case 4: return use_compression;
            case 5: return retention;
            case 6: return connect_address;
            case 7: return connect_from;
            case 8: return enabled;
            case 9: return quota_gid==-1?null:Integer.valueOf(quota_gid);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public List<FailoverFileSchedule> getFailoverFileSchedules() {
        return table.connector.failoverFileSchedules.getFailoverFileSchedules(this);
    }

    public Server getServer() throws SQLException {
        Server se=table.connector.servers.get(server);
        if(se==null) throw new SQLException("Unable to find Server: "+server);
        return se;
    }

    public BackupPartition getBackupPartition() throws SQLException {
        BackupPartition bp = table.connector.backupPartitions.get(backup_partition);
        if(bp==null) throw new SQLException("Unable to find BackupPartition: "+backup_partition);
        return bp;
    }

    /**
     * Gets the most recent (by start time) log entries for failover file replications, up to the
     * maximum number of rows.  May return less than this number of rows.  The results
     * are sorted by start_time descending (most recent at index zero).
     */
    public List<FailoverFileLog> getFailoverFileLogs(int maxRows) {
        return table.connector.failoverFileLogs.getFailoverFileLogs(this, maxRows);
    }

    public List<FailoverMySQLReplication> getFailoverMySQLReplications() {
        return table.connector.failoverMySQLReplications.getFailoverMySQLReplications(this);
    }

    public boolean getUseCompression() {
        return use_compression;
    }
    
    public BackupRetention getRetention() {
        BackupRetention br=table.connector.backupRetentions.get(retention);
        if(br==null) throw new WrappedException(new SQLException("Unable to find BackupRetention: "+retention));
        return br;
    }
    
    /**
     * Gets a connect address that should override the normal address resolution mechanisms.  This allows
     * a replication to be specifically sent through a gigabit connection or alternate route.
     */
    public String getConnectAddress() {
        return connect_address;
    }

    /**
     * Gets the address connections should be made from that overrides the normal address resolution mechanism.  This
     * allows a replication to be specifically sent through a gigabit connection or alternate route.
     */
    public String getConnectFrom() {
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
        LinuxID lid = table.connector.linuxIDs.get(quota_gid);
        if(lid==null) throw new SQLException("Unable to find LinuxID: "+quota_gid);
        return lid;
    }

    @Override
    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.FAILOVER_FILE_REPLICATIONS;
    }

    @Override
    void initImpl(ResultSet result) throws SQLException {
        int pos = 1;
        pkey=result.getInt(pos++);
        server=result.getInt(pos++);
        backup_partition=result.getInt(pos++);
        max_bit_rate=result.getInt(pos++);
        if(result.wasNull()) max_bit_rate=-1;
        use_compression=result.getBoolean(pos++);
        retention=result.getShort(pos++);
        connect_address=result.getString(pos++);
        connect_from=result.getString(pos++);
        enabled=result.getBoolean(pos++);
        quota_gid=result.getInt(pos++);
        if(result.wasNull()) quota_gid=-1;
    }

    @Override
    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        server=in.readCompressedInt();
        backup_partition=in.readCompressedInt();
        max_bit_rate=in.readInt();
        use_compression=in.readBoolean();
        retention=in.readShort();
        connect_address=StringUtility.intern(in.readNullUTF());
        connect_from=StringUtility.intern(in.readNullUTF());
        enabled=in.readBoolean();
        quota_gid=in.readCompressedInt();
    }

    @Override
    String toStringImpl() {
        try {
            return getServer()+"->"+getBackupPartition();
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    @Override
    public void write(CompressedDataOutputStream out, String version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeCompressedInt(server);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_30)<=0) out.writeCompressedInt(149); // to_server (hard-coded xen2.mob.aoindustries.com)
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_31)>=0) out.writeCompressedInt(backup_partition);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_0_A_105)>=0) out.writeInt(max_bit_rate);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_30)<=0) out.writeLong(-1); // last_start_time
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_9)>=0) out.writeBoolean(use_compression);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_13)>=0) out.writeShort(retention);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_14)>=0) out.writeNullUTF(connect_address);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_22)>=0) out.writeNullUTF(connect_from);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_15)>=0) out.writeBoolean(enabled);
        if(
            AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_17)>=0
            && AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_30)<=0
        ) {
            out.writeUTF("/var/backup"); // to_path (hard-coded /var/backup like found on xen2.mob.aoindustries.com)
            out.writeBoolean(false); // chunk_always
        }
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_31)>=0) out.writeCompressedInt(quota_gid);
    }

    public int addFileBackupSetting(String path, boolean backupEnabled) {
        return table.connector.fileBackupSettings.addFileBackupSetting(this, path, backupEnabled);
    }

    public FileBackupSetting getFileBackupSetting(String path) {
        return table.connector.fileBackupSettings.getFileBackupSetting(this, path);
    }

    public List<FileBackupSetting> getFileBackupSettings() {
        return table.connector.fileBackupSettings.getFileBackupSettings(this);
    }
    
    public void setFailoverFileSchedules(List<Short> hours, List<Short> minutes) throws IOException, SQLException {
        table.connector.failoverFileSchedules.setFailoverFileSchedules(this, hours, minutes);
    }

    public void setFileBackupSettings(List<String> paths, List<Boolean> backupEnableds) throws IOException, SQLException {
        table.connector.fileBackupSettings.setFileBackupSettings(this, paths, backupEnableds);
    }
}