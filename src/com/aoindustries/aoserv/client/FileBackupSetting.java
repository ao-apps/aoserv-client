package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * A <code>FileBackupSetting</code> overrides the default backup behavior.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class FileBackupSetting extends CachedObjectIntegerKey<FileBackupSetting> implements Removable {

    static final int
        COLUMN_PKEY=0,
        COLUMN_SERVER=1
    ;

    int server;
    String path;
    private int package_num;
    short backup_level;
    private short backup_retention;
    boolean recurse;

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();
        Server se=getServer();
        if(
            !table
                .connector
                .getThisBusinessAdministrator()
                .getUsername()
                .getPackage()
                .getBusiness()
                .getBusinessServer(se)
                .canConfigureBackup()
        ) reasons.add(new CannotRemoveReason<Server>("Not allowed to configure backup settings on "+se.getHostname(), se));
        return reasons;
    }

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_SERVER: return Integer.valueOf(server);
            case 2: return path;
            case 3: return Integer.valueOf(package_num);
            case 4: return Short.valueOf(backup_level);
            case 5: return Short.valueOf(backup_retention);
            case 6: return recurse?Boolean.TRUE:Boolean.FALSE;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public Server getServer() {
        Server se=table.connector.servers.get(server);
        if(se==null) throw new WrappedException(new SQLException("Unable to find Server: "+server));
        return se;
    }

    public String getPath() {
        return path;
    }
    
    public Package getPackage() {
        Package pk=table.connector.packages.get(package_num);
        if(pk==null) throw new WrappedException(new SQLException("Unable to find Package: "+package_num));
        return pk;
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
    
    public boolean isRecursible() {
        return recurse;
    }

    protected int getTableIDImpl() {
	return SchemaTable.FILE_BACKUP_SETTINGS;
    }

    void initImpl(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
        server=result.getInt(2);
        path=result.getString(3);
        package_num=result.getInt(4);
        backup_level=result.getShort(5);
        backup_retention=result.getShort(6);
        recurse=result.getBoolean(7);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        server=in.readCompressedInt();
        path=in.readUTF();
        package_num=in.readCompressedInt();
        backup_level=in.readShort();
        backup_retention=in.readShort();
        recurse=in.readBoolean();
    }

    public void remove() {
	table.connector.requestUpdateIL(
            AOServProtocol.REMOVE,
            SchemaTable.FILE_BACKUP_SETTINGS,
            pkey
	);
    }

    public void setSettings(
        String path,
        Package packageObj,
        BackupLevel backupLevel,
        BackupRetention backupRetention,
        boolean recurse
    ) {
        table.connector.requestUpdateIL(
            AOServProtocol.SET_FILE_BACKUP_SETTINGS,
            pkey,
            path,
            packageObj.pkey,
            backupLevel.level,
            backupRetention.days,
            recurse
        );
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeCompressedInt(server);
        out.writeUTF(path);
        out.writeCompressedInt(package_num);
        out.writeShort(backup_level);
        out.writeShort(backup_retention);
        out.writeBoolean(recurse);
    }
}