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
        COLUMN_REPLICATION=1
    ;
    static final String COLUMN_REPLICATION_name = "replication";
    static final String COLUMN_PATH_name = "path";

    int replication;
    String path;
    private boolean backup_enabled;

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        return Collections.emptyList();
    }

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_REPLICATION: return Integer.valueOf(replication);
            case 2: return path;
            case 3: return backup_enabled;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public FailoverFileReplication getReplication() {
        FailoverFileReplication ffr = table.connector.failoverFileReplications.get(replication);
        if(ffr==null) throw new WrappedException(new SQLException("Unable to find FailoverFileReplication: "+replication));
        return ffr;
    }

    public String getPath() {
        return path;
    }
    
    public boolean getBackupEnabled() {
        return backup_enabled;
    }
    
    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.FILE_BACKUP_SETTINGS;
    }

    void initImpl(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
        replication=result.getInt(2);
        path=result.getString(3);
        backup_enabled = result.getBoolean(4);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        replication=in.readCompressedInt();
        path=in.readUTF();
        backup_enabled = in.readBoolean();
    }

    public void remove() {
	table.connector.requestUpdateIL(
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.FILE_BACKUP_SETTINGS,
            pkey
	);
    }

    public void setSettings(
        String path,
        boolean backupEnabled
    ) {
        table.connector.requestUpdateIL(
            AOServProtocol.CommandID.SET_FILE_BACKUP_SETTINGS,
            pkey,
            path,
            backupEnabled
        );
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        out.writeCompressedInt(pkey);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_31)>=0) {
            out.writeCompressedInt(replication);
        } else {
            out.writeCompressedInt(-1); // server
        }
        out.writeUTF(path);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_31)>=0) {
            out.writeBoolean(backup_enabled);
        } else {
            out.writeCompressedInt(308); // package (hard-coded AOINDUSTRIES)
            out.writeShort(backup_enabled ? 1 : 0); // backup_level
            out.writeShort(7); // backup_retention
            out.writeBoolean(backup_enabled); // recurse
        }
    }
}