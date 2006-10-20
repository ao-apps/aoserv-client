package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2006 by AO Industries, Inc.,
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

    /**
     * The minimum amount of time between file replications.  Must be greater than
     * one hour else a schedule replication might occur multiple times in a row.
     */
    public static final long MINIMUM_INTERVAL=2L*60*60*1000;

    static final int COLUMN_PKEY=0;

    int from_server;
    private int to_server;
    private int max_bit_rate;
    private long last_start_time;
    private boolean use_compression;
    private short retention;
    private String connect_address;
    private boolean enabled;
    private String to_path;
    private boolean chunk_always;

    public int addFailoverFileLog(long startTime, long endTime, int scanned, int updated, long bytes, boolean isSuccessful) {
	return table.connector.failoverFileLogs.addFailoverFileLog(this, startTime, endTime, scanned, updated, bytes, isSuccessful);
    }

    public int getBitRate() {
        return max_bit_rate;
    }

    public int getBlockSize() {
        return BufferManager.BUFFER_SIZE;
    }

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case 1: return Integer.valueOf(from_server);
            case 2: return Integer.valueOf(to_server);
            case 3: return max_bit_rate==-1?null:Integer.valueOf(max_bit_rate);
            case 4: return last_start_time==-1?null:new java.sql.Date(last_start_time);
            case 5: return use_compression;
            case 6: return retention;
            case 7: return connect_address;
            case 8: return enabled;
            case 9: return to_path;
            case 10: return chunk_always;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public List<FailoverFileSchedule> getFailoverFileSchedules() {
        return table.connector.failoverFileSchedules.getFailoverFileSchedules(this);
    }

    public AOServer getFromAOServer() {
        AOServer ao=table.connector.aoServers.get(from_server);
        if(ao==null) throw new WrappedException(new SQLException("Unable to find AOServer: "+from_server));
        return ao;
    }

    public AOServer getToAOServer() {
        AOServer ao=table.connector.aoServers.get(to_server);
        if(ao==null) throw new WrappedException(new SQLException("Unable to find AOServer: "+to_server));
        return ao;
    }
 
    public List<FailoverFileLog> getFailoverFileLogs(int maxRows) {
        return table.connector.failoverFileLogs.getFailoverFileLogs(this, maxRows);
    }

    public long getLastStartTime() {
        return last_start_time;
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
     * Gets the enabled flag for this replication.
     */
    public boolean getEnabled() {
        return enabled;
    }

    /**
     * Gets the destination folder (/var/failover for replication=1 or /var/backup, /var/backup1, ...)
     */
    public String getToPath() {
        return to_path;
    }

    /**
     * When set to <code>true</code>, chunking will always be performed (mtime+length will not be considered a sufficient match).
     */
    public boolean getChunkAlways() {
        return chunk_always;
    }

    protected int getTableIDImpl() {
	return SchemaTable.FAILOVER_FILE_REPLICATIONS;
    }

    void initImpl(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
        from_server=result.getInt(2);
        to_server=result.getInt(3);
        max_bit_rate=result.getInt(4);
        if(result.wasNull()) max_bit_rate=-1;
        Timestamp T=result.getTimestamp(5);
        last_start_time=T==null?-1:T.getTime();
        use_compression=result.getBoolean(6);
        retention=result.getShort(7);
        connect_address=result.getString(8);
        enabled=result.getBoolean(9);
        to_path=result.getString(10);
        chunk_always=result.getBoolean(11);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        from_server=in.readCompressedInt();
        to_server=in.readCompressedInt();
        max_bit_rate=in.readInt();
        last_start_time=in.readLong();
        use_compression=in.readBoolean();
        retention=in.readShort();
        connect_address=readNullUTF(in);
        enabled=in.readBoolean();
        to_path=in.readUTF();
        chunk_always=in.readBoolean();
    }

    public void setLastStartTime(long time) {
        table.connector.requestUpdateIL(
            AOServProtocol.SET_LAST_FAILOVER_REPLICATION_TIME,
            pkey,
            time
        );
    }

    String toStringImpl() {
        return getFromAOServer().getServer().getHostname()+"->"+getToAOServer().getServer().getHostname();
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeCompressedInt(from_server);
        out.writeCompressedInt(to_server);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_0_A_105)>=0) out.writeInt(max_bit_rate);
        out.writeLong(last_start_time);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_9)>=0) out.writeBoolean(use_compression);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_13)>=0) out.writeShort(retention);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_14)>=0) writeNullUTF(out, connect_address);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_15)>=0) out.writeBoolean(enabled);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_17)>=0) {
            out.writeUTF(to_path);
            out.writeBoolean(chunk_always);
        }
    }
}