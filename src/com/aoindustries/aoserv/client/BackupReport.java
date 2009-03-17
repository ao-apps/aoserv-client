package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

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
    static final String COLUMN_DATE_name = "date";
    static final String COLUMN_SERVER_name = "server";
    static final String COLUMN_PACKAGE_name = "package";

    /**
     * The hour of the day (in master server time zone) that backup reports will be created.
     */
    public static final int BACKUP_REPORT_HOUR=2;

    /**
     * The minute (in master server time zone) that backup reports will be created.
     */
    public static final int BACKUP_REPORT_MINUTE=15;

    /**
     * The maximum number of days that reports will be maintained.  This is roughly 5 years.
     */
    public static final int MAX_REPORT_AGE=2*366+3*365; // Assumes worst-case of two leap years in 5-year span.

    private int pkey;
    int server;
    int packageNum;
    private long date;
    private int file_count;
    private long disk_size;

    protected AOServTable<Integer,BackupReport> table;

    @Override
    boolean equalsImpl(Object O) {
	return
            O instanceof BackupReport
            && ((BackupReport)O).pkey==pkey
	;
    }

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case 1: return Integer.valueOf(server);
            case 2: return Integer.valueOf(packageNum);
            case 3: return new Date(date);
            case 4: return Integer.valueOf(file_count);
            case 5: return Long.valueOf(disk_size);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public int getPkey() {
        return pkey;
    }

    public Server getServer() throws SQLException, IOException {
        Server se=table.connector.servers.get(server);
        if(se==null) throw new SQLException("Unable to find Server: "+server);
        return se;
    }

    public Package getPackage() throws IOException, SQLException {
        Package pk=table.connector.packages.get(packageNum);
        if(pk==null) throw new SQLException("Unable to find Package: "+packageNum);
        return pk;
    }

    public long getDate() {
        return date;
    }

    public int getFileCount() {
        return file_count;
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

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.BACKUP_REPORTS;
    }

    @Override
    int hashCodeImpl() {
	return pkey;
    }

    public void init(ResultSet result) throws SQLException {
	pkey=result.getInt(1);
        server=result.getInt(2);
        packageNum=result.getInt(3);
        date=result.getDate(4).getTime();
        file_count=result.getInt(5);
        disk_size=result.getLong(6);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readCompressedInt();
        server=in.readCompressedInt();
        packageNum=in.readCompressedInt();
        date=in.readLong();
        file_count=in.readInt();
        disk_size=in.readLong();
    }

    public void setTable(AOServTable<Integer,BackupReport> table) {
	if(this.table!=null) throw new IllegalStateException("table already set");
	this.table=table;
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
	out.writeCompressedInt(pkey);
        out.writeCompressedInt(server);
        out.writeCompressedInt(packageNum);
        out.writeLong(date);
        out.writeInt(file_count);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_30)<=0) {
            out.writeLong(0); // uncompressed_size
            out.writeLong(0); // compressed_size
        }
        out.writeLong(disk_size);
    }
}
