package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.profiler.*;
import com.aoindustries.sql.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * An <code>HttpdSharedTomcat</code> stores configuration information
 * about the Jakarta Tomcat JVM under which run one or more
 * <code>HttpdTomcatSharedSite</code>s.
 *
 * @see  HttpdTomcatSharedSite
 * @see  HttpdTomcatSite
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdSharedTomcat extends CachedObjectIntegerKey<HttpdSharedTomcat> implements Disablable, Removable {

    /**
     * The directory that www groups are stored in.
     */
    public static final String WWW_GROUP_DIR="/wwwgroup";

    static final int
        COLUMN_PKEY=0,
        COLUMN_AO_SERVER=2,
        COLUMN_LINUX_SERVER_ACCOUNT=4,
        COLUMN_CONFIG_BACKUP_LEVEL=8,
        COLUMN_CONFIG_BACKUP_RETENTION=9,
        COLUMN_FILE_BACKUP_LEVEL=10,
        COLUMN_FILE_BACKUP_RETENTION=11,
        COLUMN_LOG_BACKUP_LEVEL=12,
        COLUMN_LOG_BACKUP_RETENTION=13,
        COLUMN_TOMCAT4_WORKER=15,
        COLUMN_TOMCAT4_SHUTDOWN_PORT=16
    ;

    /**
     * The default number of days to keep backups.
     */
    public static final short
        DEFAULT_CONFIG_BACKUP_LEVEL=BackupLevel.DEFAULT_BACKUP_LEVEL,
        DEFAULT_CONFIG_BACKUP_RETENTION=BackupRetention.DEFAULT_BACKUP_RETENTION,
        DEFAULT_FILE_BACKUP_LEVEL=BackupLevel.DEFAULT_BACKUP_LEVEL,
        DEFAULT_FILE_BACKUP_RETENTION=BackupRetention.DEFAULT_BACKUP_RETENTION,
        DEFAULT_LOG_BACKUP_LEVEL=1,
        DEFAULT_LOG_BACKUP_RETENTION=1
    ;

    /**
     * The maximum number of sites allowed in one <code>HttpdSharedTomcat</code>.
     */
    public static final int MAX_SITES=LinuxGroupAccount.MAX_GROUPS-1;

    public static final int MAX_NAME_LENGTH=32;

    public static final String OVERFLOW_TEMPLATE="tomcat";

    public static final String DEFAULT_TOMCAT_VERSION_PREFIX=HttpdTomcatVersion.VERSION_4_1_PREFIX;

    private String name;
    int ao_server;
    private int version;
    int linux_server_account;
    int linux_server_group;
    private boolean isSecure;
    private boolean isOverflow;
    private short
        config_backup_level,
        config_backup_retention,
        file_backup_level,
        file_backup_retention,
        log_backup_level,
        log_backup_retention
    ;
    int disable_log;
    int tomcat4_worker;
    int tomcat4_shutdown_port;
    private String tomcat4_shutdown_key;
    private boolean isManual;

    public boolean canDisable() {
        return disable_log==-1;
    }
    
    public boolean canEnable() {
        DisableLog dl=getDisableLog();
        if(dl==null) return false;
        else return
            dl.canEnable()
            && getLinuxServerGroup().getLinuxGroup().getPackage().disable_log==-1
            && getLinuxServerAccount().disable_log==-1
        ;
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();

        for(HttpdTomcatSharedSite htss : getHttpdTomcatSharedSites()) {
            HttpdSite hs=htss.getHttpdTomcatSite().getHttpdSite();
            reasons.add(new CannotRemoveReason<HttpdTomcatSharedSite>("Used by Multi-Site Tomcat website "+hs.getInstallDirectory()+" on "+hs.getAOServer().getServer().getHostname(), htss));
        }

        return reasons;
    }

    public void disable(DisableLog dl) {
        table.connector.requestUpdateIL(AOServProtocol.DISABLE, SchemaTable.HTTPD_SHARED_TOMCATS, dl.pkey, pkey);
    }
    
    public void enable() {
        table.connector.requestUpdateIL(AOServProtocol.ENABLE, SchemaTable.HTTPD_SHARED_TOMCATS, pkey);
    }

    public BackupLevel getConfigBackupLevel() {
        Profiler.startProfile(Profiler.FAST, HttpdSharedTomcat.class, "getConfigBackupLevel()", null);
        try {
            BackupLevel bl=table.connector.backupLevels.get(config_backup_level);
            if(bl==null) throw new WrappedException(new SQLException("Unable to find BackupLevel: "+config_backup_level));
            return bl;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public BackupRetention getConfigBackupRetention() {
        Profiler.startProfile(Profiler.FAST, HttpdSharedTomcat.class, "getConfigBackupRetention()", null);
        try {
            BackupRetention br=table.connector.backupRetentions.get(config_backup_retention);
            if(br==null) throw new WrappedException(new SQLException("Unable to find BackupRetention: "+config_backup_retention));
            return br;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public BackupLevel getFileBackupLevel() {
        Profiler.startProfile(Profiler.FAST, HttpdSharedTomcat.class, "getFileBackupLevel()", null);
        try {
            BackupLevel bl=table.connector.backupLevels.get(file_backup_level);
            if(bl==null) throw new WrappedException(new SQLException("Unable to find BackupLevel: "+file_backup_level));
            return bl;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public BackupRetention getFileBackupRetention() {
        Profiler.startProfile(Profiler.FAST, HttpdSharedTomcat.class, "getFileBackupRetention()", null);
        try {
            BackupRetention br=table.connector.backupRetentions.get(file_backup_retention);
            if(br==null) throw new WrappedException(new SQLException("Unable to find BackupRetention: "+file_backup_retention));
            return br;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public String getInstallDirectory() {
        Profiler.startProfile(Profiler.FAST, HttpdSharedTomcat.class, "getInstallDirectory()", null);
        try {
            return WWW_GROUP_DIR+'/'+name;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public BackupLevel getLogBackupLevel() {
        Profiler.startProfile(Profiler.FAST, HttpdSharedTomcat.class, "getLogBackupLevel()", null);
        try {
            BackupLevel bl=table.connector.backupLevels.get(log_backup_level);
            if(bl==null) throw new WrappedException(new SQLException("Unable to find BackupLevel: "+log_backup_level));
            return bl;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public BackupRetention getLogBackupRetention() {
        Profiler.startProfile(Profiler.FAST, HttpdSharedTomcat.class, "getLogBackupRetention()", null);
        try {
            BackupRetention br=table.connector.backupRetentions.get(log_backup_retention);
            if(br==null) throw new WrappedException(new SQLException("Unable to find BackupRetention: "+log_backup_retention));
            return br;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case 1: return name;
            case COLUMN_AO_SERVER: return Integer.valueOf(ao_server);
            case 3: return Integer.valueOf(version);
            case COLUMN_LINUX_SERVER_ACCOUNT: return Integer.valueOf(linux_server_account);
            case 5: return Integer.valueOf(linux_server_group);
            case 6: return isSecure?Boolean.TRUE:Boolean.FALSE;
            case 7: return isOverflow?Boolean.TRUE:Boolean.FALSE;
            case COLUMN_CONFIG_BACKUP_LEVEL: return Short.valueOf(config_backup_level);
            case COLUMN_CONFIG_BACKUP_RETENTION: return Short.valueOf(config_backup_retention);
            case COLUMN_FILE_BACKUP_LEVEL: return Short.valueOf(file_backup_level);
            case COLUMN_FILE_BACKUP_RETENTION: return Short.valueOf(file_backup_retention);
            case COLUMN_LOG_BACKUP_LEVEL: return Short.valueOf(log_backup_level);
            case COLUMN_LOG_BACKUP_RETENTION: return Short.valueOf(log_backup_retention);
            case 14: return disable_log==-1?null:Integer.valueOf(disable_log);
            case COLUMN_TOMCAT4_WORKER: return tomcat4_worker==-1?null:Integer.valueOf(tomcat4_worker);
            case COLUMN_TOMCAT4_SHUTDOWN_PORT: return tomcat4_shutdown_port==-1?null:Integer.valueOf(tomcat4_shutdown_port);
            case 17: return tomcat4_shutdown_key;
            case 18: return isManual?Boolean.TRUE:Boolean.FALSE;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public DisableLog getDisableLog() {
        if(disable_log==-1) return null;
        DisableLog obj=table.connector.disableLogs.get(disable_log);
        if(obj==null) throw new WrappedException(new SQLException("Unable to find DisableLog: "+disable_log));
        return obj;
    }

    public List<HttpdTomcatSharedSite> getHttpdTomcatSharedSites() {
        return table.connector.httpdTomcatSharedSites.getHttpdTomcatSharedSites(this);
    }

    public HttpdTomcatVersion getHttpdTomcatVersion() {
        HttpdTomcatVersion obj=table.connector.httpdTomcatVersions.get(version);
        if(obj==null) throw new WrappedException(new SQLException("Unable to find HttpdTomcatVersion: "+version));
        return obj;
    }

    public LinuxServerAccount getLinuxServerAccount() {
        LinuxServerAccount obj=table.connector.linuxServerAccounts.get(linux_server_account);
        if(obj==null) throw new WrappedException(new SQLException("Unable to find LinuxServerAccount: "+linux_server_account));
        return obj;
    }

    public LinuxServerGroup getLinuxServerGroup() {
        LinuxServerGroup obj=table.connector.linuxServerGroups.get(linux_server_group);
        if(obj==null) throw new WrappedException(new SQLException("Unable to find LinuxServerGroup: "+linux_server_group));
        return obj;
    }

    public String getName() {
        return name;
    }

    public AOServer getAOServer() {
        AOServer obj=table.connector.aoServers.get(ao_server);
        if(obj==null) throw new WrappedException(new SQLException("Unable to find AOServer: "+ao_server));
        return obj;
    }

    protected int getTableIDImpl() {
        return SchemaTable.HTTPD_SHARED_TOMCATS;
    }

    public HttpdWorker getTomcat4Worker() {
        if(tomcat4_worker==-1) return null;
        HttpdWorker hw=table.connector.httpdWorkers.get(tomcat4_worker);
        if(hw==null) throw new WrappedException(new SQLException("Unable to find HttpdWorker: "+tomcat4_worker));
        return hw;
    }

    public String getTomcat4ShutdownKey() {
        return tomcat4_shutdown_key;
    }

    public NetBind getTomcat4ShutdownPort() {
        if(tomcat4_shutdown_port==-1) return null;
        NetBind nb=table.connector.netBinds.get(tomcat4_shutdown_port);
        if(nb==null) throw new WrappedException(new SQLException("Unable to find NetBind: "+tomcat4_shutdown_port));
        return nb;
    }

    void initImpl(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
        name=result.getString(2);
        ao_server=result.getInt(3);
        version=result.getInt(4);
        linux_server_account=result.getInt(5);
        linux_server_group=result.getInt(6);
        isSecure = result.getBoolean(7);
        isOverflow = result.getBoolean(8);
        config_backup_level=result.getShort(9);
        config_backup_retention=result.getShort(10);
        file_backup_level=result.getShort(11);
        file_backup_retention=result.getShort(12);
        log_backup_level=result.getShort(13);
        log_backup_retention=result.getShort(14);
        disable_log=result.getInt(15);
        if(result.wasNull()) disable_log=-1;
        tomcat4_worker=result.getInt(16);
        if(result.wasNull()) tomcat4_worker=-1;
        tomcat4_shutdown_port=result.getInt(17);
        if(result.wasNull()) tomcat4_shutdown_port=-1;
        tomcat4_shutdown_key=result.getString(18);
        isManual=result.getBoolean(19);
    }

    public boolean isManual() {
        return isManual;
    }

    public boolean isOverflow() {
        return isOverflow;
    }

    public boolean isSecure() {
        return isSecure;
    }

    /**
     * Checks the format of the name of the shared Tomcat, as used in the <code>/wwwgroup</code>
     * directory.  The name must be 12 characters or less, and comprised of
     * only <code>a-z</code>,<code>0-9</code>, or <code>-</code>.  The first
     * character must be <code>a-z</code>.
     */
    public static boolean isValidSharedTomcatName(String name) {
        int len = name.length();
        if (len == 0 || len > MAX_NAME_LENGTH)
            return false;
        // The first character must be [a-z]
        char ch = name.charAt(0);
        if (ch < 'a' || ch > 'z')
            return false;
        // The rest may have additional characters
        for (int c = 1; c < len; c++) {
            ch = name.charAt(c);
            if ((ch < 'a' || ch > 'z') && (ch < '0' || ch > '9') && ch != '-')
                return false;
        }
        return true;
    }

    /**
     * readImpl method comment.
     */
    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        name=in.readUTF();
        ao_server=in.readCompressedInt();
        version=in.readCompressedInt();
        linux_server_account=in.readCompressedInt();
        linux_server_group=in.readCompressedInt();
        isSecure = in.readBoolean();
        isOverflow = in.readBoolean();
        config_backup_level=in.readShort();
        config_backup_retention=in.readShort();
        file_backup_level=in.readShort();
        file_backup_retention=in.readShort();
        log_backup_level=in.readShort();
        log_backup_retention=in.readShort();
        disable_log=in.readCompressedInt();
        tomcat4_worker=in.readCompressedInt();
        tomcat4_shutdown_port=in.readCompressedInt();
        tomcat4_shutdown_key=readNullUTF(in);
        isManual=in.readBoolean();
    }

    public void remove() {
	table.connector.requestUpdateIL(AOServProtocol.REMOVE, SchemaTable.HTTPD_SHARED_TOMCATS, pkey);
    }

    public void setConfigBackupRetention(short days) {
        Profiler.startProfile(Profiler.UNKNOWN, HttpdSharedTomcat.class, "setConfigBackupRetention(short)", null);
        try {
            table.connector.requestUpdateIL(AOServProtocol.SET_BACKUP_RETENTION, days, SchemaTable.HTTPD_SHARED_TOMCATS, pkey, COLUMN_CONFIG_BACKUP_RETENTION);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public void setFileBackupRetention(short days) {
        Profiler.startProfile(Profiler.UNKNOWN, HttpdSharedTomcat.class, "setFileBackupRetention(short)", null);
        try {
            table.connector.requestUpdateIL(AOServProtocol.SET_BACKUP_RETENTION, days, SchemaTable.HTTPD_SHARED_TOMCATS, pkey, COLUMN_FILE_BACKUP_RETENTION);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public void setIsManual(boolean isManual) {
        Profiler.startProfile(Profiler.UNKNOWN, HttpdSharedTomcat.class, "setIsManual(boolean)", null);
        try {
            table.connector.requestUpdateIL(AOServProtocol.SET_HTTPD_SHARED_TOMCAT_IS_MANUAL, pkey, isManual);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public void setLogBackupRetention(short days) {
        Profiler.startProfile(Profiler.UNKNOWN, HttpdSharedTomcat.class, "setLogBackupRetention(short)", null);
        try {
            table.connector.requestUpdateIL(AOServProtocol.SET_BACKUP_RETENTION, days, SchemaTable.HTTPD_SHARED_TOMCATS, pkey, COLUMN_LOG_BACKUP_RETENTION);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    String toStringImpl() {
        return name+" on "+getAOServer().getServer().getHostname();
    }

    public void write(CompressedDataOutputStream out, String protocolVersion) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeUTF(name);
        out.writeCompressedInt(ao_server);
        out.writeCompressedInt(version);
        out.writeCompressedInt(linux_server_account);
        out.writeCompressedInt(linux_server_group);
        out.writeBoolean(isSecure);
        out.writeBoolean(isOverflow);
        out.writeShort(config_backup_level);
        out.writeShort(config_backup_retention);
        out.writeShort(file_backup_level);
        out.writeShort(file_backup_retention);
        out.writeShort(log_backup_level);
        out.writeShort(log_backup_retention);
        out.writeCompressedInt(disable_log);
        out.writeCompressedInt(tomcat4_worker);
        out.writeCompressedInt(tomcat4_shutdown_port);
        writeNullUTF(out, tomcat4_shutdown_key);
        out.writeBoolean(isManual);
    }
}