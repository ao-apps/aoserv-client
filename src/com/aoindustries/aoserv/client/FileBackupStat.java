package com.aoindustries.aoserv.client;

/*
 * Copyright 2002-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;

/**
 * A <code>FileBackupStat</code> stores file backup statistics.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class FileBackupStat extends CachedObjectIntegerKey<FileBackupStat> {

    static final int
        COLUMN_PKEY=0,
        COLUMN_SERVER=1
    ;

    int server;
    private long start_time;
    private long end_time;
    private int scanned=0;
    private int file_backup_attribute_matches=0;
    private int not_matched_md5_files=0;
    private int not_matched_md5_failures=0;
    private int send_missing_backup_data_files=0;
    private int send_missing_backup_data_failures=0;
    private int temp_files=0;
    private int temp_send_backup_data_files=0;
    private int temp_failures=0;
    private int added=0;
    private int deleted=0;
    private boolean is_successful=false;

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_SERVER: return Integer.valueOf(server);
            case 2: return new java.sql.Date(start_time);
            case 3: return new java.sql.Date(end_time);
            case 4: return Integer.valueOf(scanned);
            case 5: return Integer.valueOf(file_backup_attribute_matches);
            case 6: return Integer.valueOf(not_matched_md5_files);
            case 7: return Integer.valueOf(not_matched_md5_failures);
            case 8: return Integer.valueOf(send_missing_backup_data_files);
            case 9: return Integer.valueOf(send_missing_backup_data_failures);
            case 10: return Integer.valueOf(temp_files);
            case 11: return Integer.valueOf(temp_send_backup_data_files);
            case 12: return Integer.valueOf(temp_failures);
            case 13: return Integer.valueOf(added);
            case 14: return Integer.valueOf(deleted);
            case 15: return is_successful?Boolean.TRUE:Boolean.FALSE;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public Server getServer() {
        Server se=table.connector.servers.get(server);
        if(se==null) throw new WrappedException(new SQLException("Unable to find Server: "+server));
        return se;
    }

    public long getStartTime() {
        return start_time;
    }
    
    public long getEndTime() {
        return end_time;
    }

    public int getScanned() {
        return scanned;
    }

    public int getFileBackupAttributeMatches() {
        return file_backup_attribute_matches;
    }

    public int getNotMatchedMD5Files() {
        return not_matched_md5_files;
    }

    public int getNotMatchedMD5Failures() {
        return not_matched_md5_failures;
    }

    public int getSendMissingBackupDataFiles() {
        return send_missing_backup_data_files;
    }

    public int getSendMissingBackuDataFailures() {
        return send_missing_backup_data_failures;
    }

    public int getTempFiles() {
        return temp_files;
    }

    public int getTempSendBackupDataFiles() {
        return temp_send_backup_data_files;
    }

    public int getTempFailures() {
        return temp_failures;
    }

    public int getAdded() {
        return added;
    }

    public int getDeleted() {
        return deleted;
    }

    public boolean isSuccessful() {
        return is_successful;
    }

    protected int getTableIDImpl() {
	return SchemaTable.FILE_BACKUP_STATS;
    }

    void initImpl(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
        server=result.getInt(2);
        start_time=result.getTimestamp(3).getTime();
        end_time=result.getTimestamp(4).getTime();
        scanned=result.getInt(5);
        file_backup_attribute_matches=result.getInt(6);
        not_matched_md5_files=result.getInt(7);
        not_matched_md5_failures=result.getInt(8);
        send_missing_backup_data_files=result.getInt(9);
        send_missing_backup_data_failures=result.getInt(10);
        temp_files=result.getInt(11);
        temp_send_backup_data_files=result.getInt(12);
        temp_failures=result.getInt(13);
        added=result.getInt(14);
        deleted=result.getInt(15);
        is_successful=result.getBoolean(16);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        server=in.readCompressedInt();
        start_time=in.readLong();
        end_time=in.readLong();
        scanned=in.readCompressedInt();
        file_backup_attribute_matches=in.readCompressedInt();
        not_matched_md5_files=in.readCompressedInt();
        not_matched_md5_failures=in.readCompressedInt();
        send_missing_backup_data_files=in.readCompressedInt();
        send_missing_backup_data_failures=in.readCompressedInt();
        temp_files=in.readCompressedInt();
        temp_send_backup_data_files=in.readCompressedInt();
        temp_failures=in.readCompressedInt();
        added=in.readCompressedInt();
        deleted=in.readCompressedInt();
        is_successful=in.readBoolean();
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeCompressedInt(server);
        out.writeLong(start_time);
        out.writeLong(end_time);
        out.writeCompressedInt(scanned);
        out.writeCompressedInt(file_backup_attribute_matches);
        out.writeCompressedInt(not_matched_md5_files);
        out.writeCompressedInt(not_matched_md5_failures);
        out.writeCompressedInt(send_missing_backup_data_files);
        out.writeCompressedInt(send_missing_backup_data_failures);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_0_A_108)<=0) out.writeCompressedInt(0);
        out.writeCompressedInt(temp_files);
        out.writeCompressedInt(temp_send_backup_data_files);
        out.writeCompressedInt(temp_failures);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_0_A_108)<=0) out.writeCompressedInt(0);
        out.writeCompressedInt(added);
        out.writeCompressedInt(deleted);
        out.writeBoolean(is_successful);
    }
}