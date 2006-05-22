package com.aoindustries.aoserv.client;

/*
 * Copyright 2002-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.profiler.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * A <code>CvsRepository</code> represents on repository directory for the CVS pserver.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class CvsRepository extends CachedObjectIntegerKey<CvsRepository> implements Removable, Disablable {

    static final int
        COLUMN_PKEY=0,
        COLUMN_LINUX_SERVER_ACCOUNT=2
    ;

    /**
     * The default number of days to keep backups.
     */
    public static final short DEFAULT_BACKUP_LEVEL=BackupLevel.DEFAULT_BACKUP_LEVEL;
    public static final short DEFAULT_BACKUP_RETENTION=BackupRetention.DEFAULT_BACKUP_RETENTION;

    /**
     * The default permissions for a CVS repository.
     */
    public static final long DEFAULT_MODE=0770;

    private static final long[] validModes={
        0700,
        0750,
        DEFAULT_MODE,
        0755,
        0775,
        3770
    };

    public static long[] getValidModes() {
        return validModes;
    }

    public static boolean isValidPath(String path) {
        if(
            path==null
            || path.length()<=1
            || path.charAt(0)!='/'
            || path.indexOf("//")!=-1
            || path.indexOf("..")!=-1
        ) return false;
        int len=path.length();
        for(int c=1;c<len;c++) {
            char ch=path.charAt(c);
            if(
                (ch<'a' || ch>'z')
                && (ch<'A' || ch>'Z')
                && (ch<'0' || ch>'9')
                && ch!='_'
                && ch!='.'
                && ch!='-'
                && ch!='/'
            ) return false;
        }
        if(path.charAt(path.length()-1)=='/') return false;
        return true;
    }

    String path;
    int linux_server_account;
    int linux_server_group;
    private long mode;
    private long created;
    private short backup_level;
    private short backup_retention;
    int disable_log;

    public boolean canDisable() {
        return disable_log==-1;
    }

    public boolean canEnable() {
        DisableLog dl=getDisableLog();
        if(dl==null) return false;
        else return dl.canEnable() && getLinuxServerAccount().disable_log==-1;
    }

    public void disable(DisableLog dl) {
        table.connector.requestUpdateIL(AOServProtocol.DISABLE, SchemaTable.CVS_REPOSITORIES, dl.pkey, pkey);
    }
    
    public void enable() {
        table.connector.requestUpdateIL(AOServProtocol.ENABLE, SchemaTable.CVS_REPOSITORIES, pkey);
    }

    public BackupLevel getBackupLevel() {
        Profiler.startProfile(Profiler.FAST, CvsRepository.class, "getBackupLevel()", null);
        try {
            BackupLevel bl=table.connector.backupLevels.get(backup_level);
            if(bl==null) throw new WrappedException(new SQLException("Unable to find BackupLevel: "+backup_level));
            return bl;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public BackupRetention getBackupRetention() {
        Profiler.startProfile(Profiler.FAST, CvsRepository.class, "getBackupRetention()", null);
        try {
            BackupRetention br=table.connector.backupRetentions.get(backup_retention);
            if(br==null) throw new WrappedException(new SQLException("Unable to find BackupRetention: "+backup_retention));
            return br;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case 1: return path;
            case COLUMN_LINUX_SERVER_ACCOUNT: return Integer.valueOf(linux_server_account);
            case 3: return Integer.valueOf(linux_server_group);
            case 4: return Long.valueOf(mode);
            case 5: return new java.sql.Date(created);
            case 6: return Short.valueOf(backup_level);
            case 7: return Short.valueOf(backup_retention);
            case 8: return disable_log==-1?null:Integer.valueOf(disable_log);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public DisableLog getDisableLog() {
        if(disable_log==-1) return null;
        DisableLog obj=table.connector.disableLogs.get(disable_log);
        if(obj==null) throw new WrappedException(new SQLException("Unable to find DisableLog: "+disable_log));
        return obj;
    }

    public String getPath() {
        return path;
    }
    
    public LinuxServerAccount getLinuxServerAccount() {
        LinuxServerAccount lsa=table.connector.linuxServerAccounts.get(linux_server_account);
        if(lsa==null) throw new WrappedException(new SQLException("Unable to find LinuxServerAccount: "+linux_server_account));
        return lsa;
    }

    public LinuxServerGroup getLinuxServerGroup() {
        LinuxServerGroup lsg=table.connector.linuxServerGroups.get(linux_server_group);
        if(lsg==null) throw new WrappedException(new SQLException("Unable to find LinuxServerGroup: "+linux_server_group));
        return lsg;
    }
    
    public long getMode() {
        return mode;
    }
    
    public long getCreated() {
        return created;
    }

    protected int getTableIDImpl() {
	return SchemaTable.CVS_REPOSITORIES;
    }

    void initImpl(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
        path=result.getString(2);
        linux_server_account=result.getInt(3);
        linux_server_group=result.getInt(4);
        mode=result.getLong(5);
        created=result.getTimestamp(6).getTime();
        backup_level=result.getShort(7);
        backup_retention=result.getShort(8);
        disable_log=result.getInt(9);
        if(result.wasNull()) disable_log=-1;
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        path=in.readUTF();
        linux_server_account=in.readCompressedInt();
        linux_server_group=in.readCompressedInt();
        mode=in.readLong();
        created=in.readLong();
        backup_level=in.readShort();
        backup_retention=in.readShort();
        disable_log=in.readCompressedInt();
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        return Collections.emptyList();
    }

    public void remove() {
	table.connector.requestUpdateIL(AOServProtocol.REMOVE, SchemaTable.CVS_REPOSITORIES, pkey);
    }

    public void setBackupRetention(short days) {
        Profiler.startProfile(Profiler.UNKNOWN, CvsRepository.class, "setBackupRetention(short)", null);
        try {
            table.connector.requestUpdateIL(AOServProtocol.SET_BACKUP_RETENTION, days, SchemaTable.CVS_REPOSITORIES, pkey);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public void setMode(long mode) {
	table.connector.requestUpdateIL(AOServProtocol.SET_CVS_REPOSITORY_MODE, pkey, mode);
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeUTF(path);
        out.writeCompressedInt(linux_server_account);
        out.writeCompressedInt(linux_server_group);
        out.writeLong(mode);
        out.writeLong(created);
        out.writeShort(backup_level);
        out.writeShort(backup_retention);
        out.writeCompressedInt(disable_log);
    }
}