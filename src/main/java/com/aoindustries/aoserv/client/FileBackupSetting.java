/*
 * Copyright 2003-2012 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.io.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * A <code>FileBackupSetting</code> overrides the default backup behavior.
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
    private boolean required;

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        return Collections.emptyList();
    }

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_REPLICATION: return Integer.valueOf(replication);
            case 2: return path;
            case 3: return backup_enabled;
            case 4: return required;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public FailoverFileReplication getReplication() throws SQLException, IOException {
        FailoverFileReplication ffr = table.connector.getFailoverFileReplications().get(replication);
        if(ffr==null) throw new SQLException("Unable to find FailoverFileReplication: "+replication);
        return ffr;
    }

    public String getPath() {
        return path;
    }
    
    public boolean getBackupEnabled() {
        return backup_enabled;
    }

    /**
     * All required file backup settings must match in the filesystem for the
     * backup to be considered successful.  This is used to detect missing filesystems
     * or files during a backup pass.
     */
    public boolean isRequired() {
        return required;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.FILE_BACKUP_SETTINGS;
    }

    public void init(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
        replication=result.getInt(2);
        path=result.getString(3);
        backup_enabled = result.getBoolean(4);
        required = result.getBoolean(5);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        replication=in.readCompressedInt();
        path=in.readUTF();
        backup_enabled = in.readBoolean();
        required = in.readBoolean();
    }

    public void remove() throws IOException, SQLException {
        table.connector.requestUpdateIL(
            true,
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.FILE_BACKUP_SETTINGS,
            pkey
    	);
    }

    public void setSettings(
        String path,
        boolean backupEnabled,
        boolean required
    ) throws IOException, SQLException {
        table.connector.requestUpdateIL(
            true,
            AOServProtocol.CommandID.SET_FILE_BACKUP_SETTINGS,
            pkey,
            path,
            backupEnabled,
            required
        );
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeCompressedInt(pkey);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_31)>=0) {
            out.writeCompressedInt(replication);
        } else {
            out.writeCompressedInt(-1); // server
        }
        out.writeUTF(path);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_31)>=0) {
            out.writeBoolean(backup_enabled);
        } else {
            out.writeCompressedInt(308); // package (hard-coded AOINDUSTRIES)
            out.writeShort(backup_enabled ? 1 : 0); // backup_level
            out.writeShort(7); // backup_retention
            out.writeBoolean(backup_enabled); // recurse
        }
        if(version.compareTo(AOServProtocol.Version.VERSION_1_62)>=0) {
            out.writeBoolean(required);
        }
    }
}