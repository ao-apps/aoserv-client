package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;

/**
 * A <code>BackupReport</code> is generated once per day per package and per server.  This information
 * is averaged through a month and used for account billing.  The reports are processed at or near 2:00am
 * and basically represent the report for the previous day.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class BackupReport extends AOServObject<Integer,BackupReport> implements SingleTableObject<Integer,BackupReport> {

    static final int COLUMN_PKEY=0;

    /**
     * The hour of the day (in master server time zone) that backup reports will be created.
     */
    public static final int BACKUP_REPORT_HOUR=2;

    /**
     * The minute (in master server time zone) that backup reports will be created.
     */
    public static final int BACKUP_REPORT_MINUTE=15;

    /**
     * The maximum number of days that reports will be maintained.  This is roughly one year.
     */
    public static final int MAX_REPORT_AGE=366;

    private int pkey;
    int server;
    int packageNum;
    private long date;
    private int file_count;
    private long uncompressed_size;
    private long compressed_size;
    private long disk_size;

    protected AOServTable<Integer,BackupReport> table;

    boolean equalsImpl(Object O) {
	return
            O instanceof BackupReport
            && ((BackupReport)O).pkey==pkey
	;
    }

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case 1: return Integer.valueOf(server);
            case 2: return Integer.valueOf(packageNum);
            case 3: return new Date(date);
            case 4: return Integer.valueOf(file_count);
            case 5: return Long.valueOf(uncompressed_size);
            case 6: return Long.valueOf(compressed_size);
            case 7: return Long.valueOf(disk_size);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public int getPKey() {
        return pkey;
    }

    public Server getServer() {
        Server se=table.connector.servers.get(server);
        if(se==null) throw new WrappedException(new SQLException("Unable to find Server: "+server));
        return se;
    }

    public Package getPackage() {
        Package pk=table.connector.packages.get(packageNum);
        if(pk==null) throw new WrappedException(new SQLException("Unable to find Package: "+packageNum));
        return pk;
    }

    public long getDate() {
        return date;
    }

    public int getFileCount() {
        return file_count;
    }
    
    public long getUncompressedSize() {
        return uncompressed_size;
    }
    
    public long getCompressedSize() {
        return compressed_size;
    }
    
    public long getDiskSize() {
        return disk_size;
    }

    public Integer getKey() {
	return pkey;
    }

    final public AOServTable<Integer,BackupReport> getTable() {
        return table;
    }

    protected int getTableIDImpl() {
	return SchemaTable.BACKUP_REPORTS;
    }

    int hashCodeImpl() {
	return pkey;
    }

    public void initImpl(ResultSet result) throws SQLException {
	pkey=result.getInt(1);
        server=result.getInt(2);
        packageNum=result.getInt(3);
        date=result.getDate(4).getTime();
        file_count=result.getInt(5);
        uncompressed_size=result.getLong(6);
        compressed_size=result.getLong(7);
        disk_size=result.getLong(8);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readCompressedInt();
        server=in.readCompressedInt();
        packageNum=in.readCompressedInt();
        date=in.readLong();
        file_count=in.readInt();
        uncompressed_size=in.readLong();
        compressed_size=in.readLong();
        disk_size=in.readLong();
    }

    public void setTable(AOServTable<Integer,BackupReport> table) {
	if(this.table!=null) throw new IllegalStateException("table already set");
	this.table=table;
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeCompressedInt(pkey);
        out.writeCompressedInt(server);
        out.writeCompressedInt(packageNum);
        out.writeLong(date);
        out.writeInt(file_count);
        out.writeLong(uncompressed_size);
        out.writeLong(compressed_size);
        out.writeLong(disk_size);
    }
}