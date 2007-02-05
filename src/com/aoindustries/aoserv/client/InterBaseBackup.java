package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.Collections;
import java.util.List;

/**
 * The InterBase databases are backed-up to a backup server on a daily basis.
 * These backups are then kept for a minimum of seven days.  A
 * <code>MasterUser</code> who is a backup administrator may trigger
 * the backup of a database at any time.  Either a backup administrator
 * or a <code>BusinessAdministrator</code> who owns the database may
 * retrieve the backup data.
 *
 * @see  BusinessAdministrator
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class InterBaseBackup extends CachedObjectIntegerKey<InterBaseBackup> implements Removable, Dumpable {

    static final int
        COLUMN_PKEY=0,
        COLUMN_AO_SERVER=4
    ;

    private int packageNum;
    private String db_group_name;
    private String db_name;
    int ao_server;
    private long
        start_time,
        end_time
    ;
    private int backup_data;
    private short backup_level;
    private short backup_retention;

    public void dump(PrintWriter out) {
	getDatabaseBackup(out, true);
    }

    public BackupData getBackupData() {
	BackupData obj=table.connector.backupDatas.get(backup_data);
	if(obj==null) throw new WrappedException(new WrappedException(new SQLException("Unable to find BackupData: "+backup_data)));
	return obj;
    }

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case 1: return Integer.valueOf(packageNum);
            case 2: return db_group_name;
            case 3: return db_name;
            case COLUMN_AO_SERVER: return Integer.valueOf(ao_server);
            case 5: return new java.sql.Date(start_time);
            case 6: return new java.sql.Date(end_time);
            case 7: return Integer.valueOf(backup_data);
            case 8: return Short.valueOf(backup_level);
            case 9: return Short.valueOf(backup_retention);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public void getDatabaseBackup(OutputStream out, boolean decompress) {
        getBackupData().getData(out, decompress, 0, null);
    }

    public void getDatabaseBackup(Writer out, boolean decompress) {
        getBackupData().getData(out, decompress, 0, null);
    }

    public String getDBGroupName() {
        return db_group_name;
    }

    public String getDatabaseName() {
	return db_name;
    }

    public AOServer getAOServer() {
	AOServer obj=table.connector.aoServers.get(ao_server);
	if(obj==null) throw new WrappedException(new WrappedException(new SQLException("Unable to find AOServer: "+ao_server)));
	return obj;
    }

    public long getEndTime() {
	return end_time;
    }

    public BackupLevel getBackupLevel() {
        BackupLevel bl=table.connector.backupLevels.get(backup_level);
        if(bl==null) throw new WrappedException(new SQLException("Unable to find BackupLevel: "+backup_level));
        return bl;
    }

    public BackupRetention getBackupRetention() {
        BackupRetention br=table.connector.backupRetentions.get(backup_retention);
        if(br==null) throw new WrappedException(new SQLException("Unable to find BackupRetention: "+backup_retention));
        return br;
    }

    public Package getPackage() {
	Package obj=table.connector.packages.get(packageNum);
	if(obj==null) throw new WrappedException(new SQLException("Unable to find Package: "+packageNum));
	return obj;
    }

    public long getStartTime() {
	return start_time;
    }

    protected int getTableIDImpl() {
	return SchemaTable.INTERBASE_BACKUPS;
    }

    void initImpl(ResultSet result) throws SQLException {
	pkey=result.getInt(1);
	packageNum=result.getInt(2);
        db_group_name=result.getString(3);
	db_name=result.getString(4);
	ao_server=result.getInt(5);
	start_time=result.getTimestamp(6).getTime();
	end_time=result.getTimestamp(7).getTime();
        backup_data=result.getInt(8);
        backup_level=result.getShort(9);
        backup_retention=result.getShort(10);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readCompressedInt();
	packageNum=in.readCompressedInt();
        db_group_name=in.readUTF();
	db_name=in.readUTF();
	ao_server=in.readCompressedInt();
	start_time=in.readLong();
	end_time=in.readLong();
        backup_data=in.readCompressedInt();
        backup_level=in.readShort();
        backup_retention=in.readShort();
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        return Collections.emptyList();
    }

    public void remove() {
	table.connector.requestUpdateIL(
            AOServProtocol.REMOVE,
            SchemaTable.INTERBASE_BACKUPS,
            pkey
	);
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeCompressedInt(pkey);
	out.writeCompressedInt(packageNum);
        out.writeUTF(db_group_name);
	out.writeUTF(db_name);
	out.writeCompressedInt(ao_server);
	out.writeLong(start_time);
	out.writeLong(end_time);
        out.writeCompressedInt(backup_data);
        out.writeShort(backup_level);
        out.writeShort(backup_retention);
    }
}
