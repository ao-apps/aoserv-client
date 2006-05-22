package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.profiler.*;
import com.aoindustries.sql.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.Collections;
import java.util.List;

/**
 * An <code>HttpdSite</code> is one unique set of web content and resides in
 * its own directory under <code>/www</code>.  Each <code>HttpdSite</code>
 * has a unique name per server, and may be served simultaneously on any
 * number of <code>HttpdBind</code>s through any number of
 * <code>HttpdServer</code>s.
 * <p>
 * An <code>HttpdSite</code> only stores the information that is common to
 * all site types.  The site will always reference one, and only one, other
 * type of entry, indicating the type of site and providing the rest of the
 * information about the site.
 *
 * @see  HttpdSiteBind
 * @see  HttpdBind
 * @see  HttpdServer
 * @see  HttpdStaticSite
 * @see  HttpdTomcatSite
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdSite extends CachedObjectIntegerKey<HttpdSite> implements Disablable, Removable {

    static final int
        COLUMN_PKEY=0,
        COLUMN_AO_SERVER=1,
        COLUMN_PACKAGE=4,
        COLUMN_CONFIG_BACKUP_LEVEL=9,
        COLUMN_CONFIG_BACKUP_RETENTION=10,
        COLUMN_FILE_BACKUP_LEVEL=11,
        COLUMN_FILE_BACKUP_RETENTION=12,
        COLUMN_FTP_BACKUP_LEVEL=13,
        COLUMN_FTP_BACKUP_RETENTION=14,
        COLUMN_LOG_BACKUP_LEVEL=15,
        COLUMN_LOG_BACKUP_RETENTION=16
    ;

    /**
     * The default number of days to keep backups.
     */
    public static final short
        DEFAULT_CONFIG_BACKUP_LEVEL=BackupLevel.DEFAULT_BACKUP_LEVEL,
        DEFAULT_CONFIG_BACKUP_RETENTION=BackupRetention.DEFAULT_BACKUP_RETENTION,
        DEFAULT_FILE_BACKUP_LEVEL=BackupLevel.DEFAULT_BACKUP_LEVEL,
        DEFAULT_FILE_BACKUP_RETENTION=BackupRetention.DEFAULT_BACKUP_RETENTION,
        DEFAULT_FTP_BACKUP_LEVEL=BackupLevel.DEFAULT_BACKUP_LEVEL,
        DEFAULT_FTP_BACKUP_RETENTION=BackupRetention.DEFAULT_BACKUP_RETENTION,
        DEFAULT_LOG_BACKUP_LEVEL=1,
        DEFAULT_LOG_BACKUP_RETENTION=1
    ;

    public static final int MAX_SITE_NAME_LENGTH=255;

    public static final String WWW_DIRECTORY="/www";

    /**
     * The site name used when an account is disabled.
     */
    public static final String DISABLED="disabled";
    
    int ao_server;
    String site_name;
    private boolean list_first;
    String packageName;
    String linuxAccount;
    String linuxGroup;
    private String
        serverAdmin,
        contentSrc
    ;
    private short
        config_backup_level,
        config_backup_retention,
        file_backup_level,
        file_backup_retention,
        ftp_backup_level,
        ftp_backup_retention,
        log_backup_level,
        log_backup_retention
    ;
    int disable_log;
    private boolean isManual;
    private String awstatsSkipFiles;

    public boolean canDisable() {
        if(disable_log!=-1) return false;
        for(HttpdSiteBind hsb : getHttpdSiteBinds()) if(hsb.disable_log==-1) return false;
        return true;
    }

    public boolean canEnable() {
        DisableLog dl=getDisableLog();
        if(dl==null) return false;
        else return
            dl.canEnable()
            && getPackage().disable_log==-1
            && getLinuxServerAccount().disable_log==-1
        ;
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        return Collections.emptyList();
    }

    public void disable(DisableLog dl) {
        table.connector.requestUpdateIL(AOServProtocol.DISABLE, SchemaTable.HTTPD_SITES, dl.pkey, pkey);
    }
    
    public void enable() {
        table.connector.requestUpdateIL(AOServProtocol.ENABLE, SchemaTable.HTTPD_SITES, pkey);
    }

    public BackupLevel getConfigBackupLevel() {
        Profiler.startProfile(Profiler.FAST, HttpdSite.class, "getConfigBackupLevel()", null);
        try {
            BackupLevel bl=table.connector.backupLevels.get(config_backup_level);
            if(bl==null) throw new WrappedException(new SQLException("Unable to find BackupLevel: "+config_backup_level));
            return bl;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public BackupRetention getConfigBackupRetention() {
        Profiler.startProfile(Profiler.FAST, HttpdSite.class, "getConfigBackupRetention()", null);
        try {
            BackupRetention br=table.connector.backupRetentions.get(config_backup_retention);
            if(br==null) throw new WrappedException(new SQLException("Unable to find BackupRetention: "+config_backup_retention));
            return br;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public BackupLevel getFileBackupLevel() {
        Profiler.startProfile(Profiler.FAST, HttpdSite.class, "getFileBackupLevel()", null);
        try {
            BackupLevel bl=table.connector.backupLevels.get(file_backup_level);
            if(bl==null) throw new WrappedException(new SQLException("Unable to find BackupLevel: "+file_backup_level));
            return bl;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public BackupRetention getFileBackupRetention() {
        Profiler.startProfile(Profiler.FAST, HttpdSite.class, "getFileBackupRetention()", null);
        try {
            BackupRetention br=table.connector.backupRetentions.get(file_backup_retention);
            if(br==null) throw new WrappedException(new SQLException("Unable to find BackupRetention: "+file_backup_retention));
            return br;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public BackupLevel getFtpBackupLevel() {
        Profiler.startProfile(Profiler.FAST, HttpdSite.class, "getFtpBackupLevel()", null);
        try {
            BackupLevel bl=table.connector.backupLevels.get(ftp_backup_level);
            if(bl==null) throw new WrappedException(new SQLException("Unable to find BackupLevel: "+ftp_backup_level));
            return bl;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public BackupRetention getFtpBackupRetention() {
        Profiler.startProfile(Profiler.FAST, HttpdSite.class, "getFtpBackupRetention()", null);
        try {
            BackupRetention br=table.connector.backupRetentions.get(ftp_backup_retention);
            if(br==null) throw new WrappedException(new SQLException("Unable to find BackupRetention: "+ftp_backup_retention));
            return br;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public String getInstallDirectory() {
        Profiler.startProfile(Profiler.FAST, HttpdSite.class, "getInstallDirectory()", null);
        try {
            return WWW_DIRECTORY+'/'+site_name;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public BackupLevel getLogBackupLevel() {
        Profiler.startProfile(Profiler.FAST, HttpdSite.class, "getLogBackupLevel()", null);
        try {
            BackupLevel bl=table.connector.backupLevels.get(log_backup_level);
            if(bl==null) throw new WrappedException(new SQLException("Unable to find BackupLevel: "+log_backup_level));
            return bl;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public BackupRetention getLogBackupRetention() {
        Profiler.startProfile(Profiler.FAST, HttpdSite.class, "getLogBackupRetention()", null);
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
            case COLUMN_AO_SERVER: return Integer.valueOf(ao_server);
            case 2: return site_name;
            case 3: return list_first?Boolean.TRUE:Boolean.FALSE;
            case COLUMN_PACKAGE: return packageName;
            case 5: return linuxAccount;
            case 6: return linuxGroup;
            case 7: return serverAdmin;
            case 8: return contentSrc;
            case COLUMN_CONFIG_BACKUP_LEVEL: return Short.valueOf(config_backup_level);
            case COLUMN_CONFIG_BACKUP_RETENTION: return Short.valueOf(config_backup_retention);
            case COLUMN_FILE_BACKUP_LEVEL: return Short.valueOf(file_backup_level);
            case COLUMN_FILE_BACKUP_RETENTION: return Short.valueOf(file_backup_retention);
            case COLUMN_FTP_BACKUP_LEVEL: return Short.valueOf(ftp_backup_level);
            case COLUMN_FTP_BACKUP_RETENTION: return Short.valueOf(ftp_backup_retention);
            case COLUMN_LOG_BACKUP_LEVEL: return Short.valueOf(log_backup_level);
            case COLUMN_LOG_BACKUP_RETENTION: return Short.valueOf(log_backup_retention);
            case 17: return disable_log==-1?null:Integer.valueOf(disable_log);
            case 18: return isManual?Boolean.TRUE:Boolean.FALSE;
            case 19: return awstatsSkipFiles;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public String getContentSrc() {
        return contentSrc;
    }

    public DisableLog getDisableLog() {
        if(disable_log==-1) return null;
        DisableLog obj=table.connector.disableLogs.get(disable_log);
        if(obj==null) throw new WrappedException(new SQLException("Unable to find DisableLog: "+disable_log));
        return obj;
    }

    public List<HttpdSiteBind> getHttpdSiteBinds() {
        return table.connector.httpdSiteBinds.getHttpdSiteBinds(this);
    }

    public List<HttpdSiteBind> getHttpdSiteBinds(HttpdServer server) {
        return table.connector.httpdSiteBinds.getHttpdSiteBinds(this, server);
    }

    public HttpdStaticSite getHttpdStaticSite() {
        return table.connector.httpdStaticSites.get(pkey);
    }

    public HttpdTomcatSite getHttpdTomcatSite() {
        return table.connector.httpdTomcatSites.get(pkey);
    }

    public LinuxServerAccount getLinuxServerAccount() {
        // May be filtered
        LinuxAccount obj=table.connector.linuxAccounts.get(linuxAccount);
        if(obj==null) return null;

        LinuxServerAccount lsa = obj.getLinuxServerAccount(getAOServer());
        if (lsa==null) throw new WrappedException(new SQLException("Unable to find LinuxServerAccount: "+linuxAccount+" on "+ao_server));
        return lsa;
    }

    public LinuxServerGroup getLinuxServerGroup() {
        LinuxGroup obj=table.connector.linuxGroups.get(linuxGroup);
        if(obj==null) throw new WrappedException(new SQLException("Unable to find LinuxGroup: "+linuxGroup));
        LinuxServerGroup lsg = obj.getLinuxServerGroup(getAOServer());
        if(lsg==null) throw new WrappedException(new SQLException("Unable to find LinuxServerGroup: "+linuxGroup+" on "+ao_server));
        return lsg;
    }

    public Package getPackage() {
        Package obj=table.connector.packages.get(packageName);
        if(obj==null) throw new WrappedException(new SQLException("Unable to find Package: "+packageName));
        return obj;
    }

    public HttpdSiteURL getPrimaryHttpdSiteURL() {
        List<HttpdSiteBind> binds=getHttpdSiteBinds();
        if(binds.isEmpty()) return null;

        // Find the first one that binds to the default HTTP port, if one exists
        NetPort httpPort=table.connector.protocols.get(Protocol.HTTP).getPort(table.connector);

        int index=-1;
        for(int c=0;c<binds.size();c++) {
            HttpdSiteBind bind=binds.get(c);
            if(bind.getHttpdBind().getNetBind().getPort().equals(httpPort)) {
                index=c;
                break;
            }
        }
        if(index==-1) index=0;

        return binds.get(index).getPrimaryHttpdSiteURL();
    }

    public AOServer getAOServer() {
        AOServer obj=table.connector.aoServers.get(ao_server);
        if(obj==null) throw new WrappedException(new SQLException("Unable to find AOServer: "+ao_server));
        return obj;
    }

    public String getServerAdmin() {
        return serverAdmin;
    }

    public String getSiteName() {
        return site_name;
    }

    protected int getTableIDImpl() {
        return SchemaTable.HTTPD_SITES;
    }

    //public void initializePasswdFile(String username, String password) {
    //    table.connector.requestUpdate(AOServProtocol.INITIALIZE_HTTPD_SITE_PASSWD_FILE, pkey, username, UnixCrypt.crypt(username, password));
    //}

    void initImpl(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
        ao_server=result.getInt(2);
        site_name=result.getString(3);
        list_first=result.getBoolean(4);
        packageName=result.getString(5);
        linuxAccount=result.getString(6);
        linuxGroup=result.getString(7);
        serverAdmin=result.getString(8);
        contentSrc=result.getString(9);
        config_backup_level=result.getShort(10);
        config_backup_retention=result.getShort(11);
        file_backup_level=result.getShort(12);
        file_backup_retention=result.getShort(13);
        ftp_backup_level=result.getShort(14);
        ftp_backup_retention=result.getShort(15);
        log_backup_level=result.getShort(16);
        log_backup_retention=result.getShort(17);
        disable_log=result.getInt(18);
        if(result.wasNull()) disable_log=-1;
        isManual=result.getBoolean(19);
        awstatsSkipFiles=result.getString(20);
    }

    public boolean isManual() {
        return isManual;
    }

    public String getAwstatsSkipFiles() {
        return awstatsSkipFiles;
    }

    /**
     * Checks the format of the name of the site, as used in the <code>/www</code>
     * directory.  The site name must be 255 characters or less, and comprised of
     * only <code>a-z</code>, <code>0-9</code>, <code>.</code> or <code>-</code>.  The first
     * character must be <code>a-z</code> or <code>0-9</code>.
     */
    public static boolean isValidSiteName(String name) {
        // These are the other files/directories that may exist under /www.  To avoid
        // potential conflicts, these may not be used as site names.
        if(
            "lost+found".equals(name)
            || ".backup".equals(name)
            || "aquota.user".equals(name)
        ) return false;

        int len = name.length();
        if (len == 0 || len > MAX_SITE_NAME_LENGTH)
                return false;
        // The first character must be [a-z] or [0-9]
        char ch = name.charAt(0);
        if (
            (ch < 'a' || ch > 'z')
            && (ch<'0' || ch>'9')
        ) return false;
        // The rest may have additional characters
        for (int c = 1; c < len; c++) {
            ch = name.charAt(c);
            if(
                (ch < 'a' || ch > 'z')
                && (ch < '0' || ch > '9')
                && ch != '.'
                && ch != '-'
            ) return false;
        }
        return true;
    }

    public boolean listFirst() {
        return list_first;
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        ao_server=in.readCompressedInt();
        site_name=in.readUTF();
        list_first=in.readBoolean();
        packageName=in.readUTF();
        linuxAccount=in.readUTF();
        linuxGroup=in.readUTF();
        serverAdmin=in.readUTF();
        contentSrc=readNullUTF(in);
        config_backup_level=in.readShort();
        config_backup_retention=in.readShort();
        file_backup_level=in.readShort();
        file_backup_retention=in.readShort();
        ftp_backup_level=in.readShort();
        ftp_backup_retention=in.readShort();
        log_backup_level=in.readShort();
        log_backup_retention=in.readShort();
        disable_log=in.readCompressedInt();
        isManual=in.readBoolean();
        awstatsSkipFiles=readNullUTF(in);
    }

    public void remove() {
	table.connector.requestUpdateIL(AOServProtocol.REMOVE, SchemaTable.HTTPD_SITES, pkey);
    }

    public void setConfigBackupRetention(short days) {
        Profiler.startProfile(Profiler.UNKNOWN, HttpdSite.class, "setConfigBackupRetention(short)", null);
        try {
            table.connector.requestUpdateIL(AOServProtocol.SET_BACKUP_RETENTION, days, SchemaTable.HTTPD_SITES, pkey, COLUMN_CONFIG_BACKUP_RETENTION);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public void setFileBackupRetention(short days) {
        Profiler.startProfile(Profiler.UNKNOWN, HttpdSite.class, "setFileBackupRetention(short)", null);
        try {
            table.connector.requestUpdateIL(AOServProtocol.SET_BACKUP_RETENTION, days, SchemaTable.HTTPD_SITES, pkey, COLUMN_FILE_BACKUP_RETENTION);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public void setFtpBackupRetention(short days) {
        Profiler.startProfile(Profiler.UNKNOWN, HttpdSite.class, "setFtpBackupRetention(short)", null);
        try {
            table.connector.requestUpdateIL(AOServProtocol.SET_BACKUP_RETENTION, days, SchemaTable.HTTPD_SITES, pkey, COLUMN_FTP_BACKUP_RETENTION);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public void setIsManual(boolean isManual) {
        Profiler.startProfile(Profiler.UNKNOWN, HttpdSite.class, "setIsManual(boolean)", null);
        try {
            table.connector.requestUpdateIL(AOServProtocol.SET_HTTPD_SITE_IS_MANUAL, pkey, isManual);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public void setLogBackupRetention(short days) {
        Profiler.startProfile(Profiler.UNKNOWN, HttpdSite.class, "setLogBackupRetention(short)", null);
        try {
            table.connector.requestUpdateIL(AOServProtocol.SET_BACKUP_RETENTION, days, SchemaTable.HTTPD_SITES, pkey, COLUMN_LOG_BACKUP_RETENTION);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public void setServerAdmin(String address) {
        table.connector.requestUpdateIL(AOServProtocol.SET_HTTPD_SITE_SERVER_ADMIN, pkey, address);
    }

    String toStringImpl() {
        return site_name+" on "+getAOServer().getServer().getHostname();
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeCompressedInt(ao_server);
        out.writeUTF(site_name);
        out.writeBoolean(list_first);
        out.writeUTF(packageName);
        out.writeUTF(linuxAccount);
        out.writeUTF(linuxGroup);
        out.writeUTF(serverAdmin);
        writeNullUTF(out, contentSrc);
        out.writeShort(config_backup_level);
        out.writeShort(config_backup_retention);
        out.writeShort(file_backup_level);
        out.writeShort(file_backup_retention);
        out.writeShort(ftp_backup_level);
        out.writeShort(ftp_backup_retention);
        out.writeShort(log_backup_level);
        out.writeShort(log_backup_retention);
        out.writeCompressedInt(disable_log);
        out.writeBoolean(isManual);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_0_A_129)>=0) writeNullUTF(out, awstatsSkipFiles);
        
    }

    public void getAWStatsFile(String path, String queryString, OutputStream out) {
        try {
            AOServConnection connection=table.connector.getConnection();
            try {
                CompressedDataOutputStream masterOut=connection.getOutputStream();
                masterOut.writeCompressedInt(AOServProtocol.GET_AWSTATS_FILE);
                masterOut.writeCompressedInt(pkey);
                masterOut.writeUTF(path);
                masterOut.writeUTF(queryString==null ? "" : queryString);
                masterOut.flush();

                CompressedDataInputStream in=connection.getInputStream();
                byte[] buff=BufferManager.getBytes();
                try {
                    int code;
                    while((code=in.readByte())==AOServProtocol.NEXT) {
                        int len=in.readShort();
                        in.readFully(buff, 0, len);
                        out.write(buff, 0, len);
                    }
                    AOServProtocol.checkResult(code, in);
                } finally {
                    BufferManager.release(buff);
                }
            } catch(IOException err) {
                connection.close();
                throw err;
            } finally {
                table.connector.releaseConnection(connection);
            }
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }
}