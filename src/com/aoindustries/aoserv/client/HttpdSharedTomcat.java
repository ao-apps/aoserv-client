package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
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

    static final int
        COLUMN_PKEY=0,
        COLUMN_AO_SERVER=2,
        COLUMN_LINUX_SERVER_ACCOUNT=4,
        COLUMN_TOMCAT4_WORKER=9,
        COLUMN_TOMCAT4_SHUTDOWN_PORT=10
    ;
    static final String COLUMN_NAME_name = "name";
    static final String COLUMN_AO_SERVER_name = "ao_server";

    /**
     * The directory that www groups are stored in.
     */
    public static final String WWW_GROUP_DIR="/wwwgroup";

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
            reasons.add(new CannotRemoveReason<HttpdTomcatSharedSite>("Used by Multi-Site Tomcat website "+hs.getInstallDirectory()+" on "+hs.getAOServer().getHostname(), htss));
        }

        return reasons;
    }

    public void disable(DisableLog dl) {
        table.connector.requestUpdateIL(AOServProtocol.CommandID.DISABLE, SchemaTable.TableID.HTTPD_SHARED_TOMCATS, dl.pkey, pkey);
    }
    
    public void enable() {
        table.connector.requestUpdateIL(AOServProtocol.CommandID.ENABLE, SchemaTable.TableID.HTTPD_SHARED_TOMCATS, pkey);
    }

    public String getInstallDirectory() {
        Profiler.startProfile(Profiler.FAST, HttpdSharedTomcat.class, "getInstallDirectory()", null);
        try {
            return WWW_GROUP_DIR+'/'+name;
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
            case 8: return disable_log==-1?null:Integer.valueOf(disable_log);
            case COLUMN_TOMCAT4_WORKER: return tomcat4_worker==-1?null:Integer.valueOf(tomcat4_worker);
            case COLUMN_TOMCAT4_SHUTDOWN_PORT: return tomcat4_shutdown_port==-1?null:Integer.valueOf(tomcat4_shutdown_port);
            case 11: return tomcat4_shutdown_key;
            case 12: return isManual?Boolean.TRUE:Boolean.FALSE;
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

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.HTTPD_SHARED_TOMCATS;
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
        int pos = 1;
        pkey=result.getInt(pos++);
        name=result.getString(pos++);
        ao_server=result.getInt(pos++);
        version=result.getInt(pos++);
        linux_server_account=result.getInt(pos++);
        linux_server_group=result.getInt(pos++);
        isSecure = result.getBoolean(pos++);
        isOverflow = result.getBoolean(pos++);
        disable_log=result.getInt(pos++);
        if(result.wasNull()) disable_log=-1;
        tomcat4_worker=result.getInt(pos++);
        if(result.wasNull()) tomcat4_worker=-1;
        tomcat4_shutdown_port=result.getInt(pos++);
        if(result.wasNull()) tomcat4_shutdown_port=-1;
        tomcat4_shutdown_key=result.getString(pos++);
        isManual=result.getBoolean(pos++);
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
        disable_log=in.readCompressedInt();
        tomcat4_worker=in.readCompressedInt();
        tomcat4_shutdown_port=in.readCompressedInt();
        tomcat4_shutdown_key=in.readNullUTF();
        isManual=in.readBoolean();
    }

    public void remove() {
	table.connector.requestUpdateIL(AOServProtocol.CommandID.REMOVE, SchemaTable.TableID.HTTPD_SHARED_TOMCATS, pkey);
    }

    public void setIsManual(boolean isManual) {
        Profiler.startProfile(Profiler.UNKNOWN, HttpdSharedTomcat.class, "setIsManual(boolean)", null);
        try {
            table.connector.requestUpdateIL(AOServProtocol.CommandID.SET_HTTPD_SHARED_TOMCAT_IS_MANUAL, pkey, isManual);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    String toStringImpl() {
        return name+" on "+getAOServer().getHostname();
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
        if(AOServProtocol.compareVersions(protocolVersion, AOServProtocol.VERSION_1_30)<=0) {
            out.writeShort(0);
            out.writeShort(7);
            out.writeShort(0);
            out.writeShort(7);
            out.writeShort(0);
            out.writeShort(7);
        }
        out.writeCompressedInt(disable_log);
        out.writeCompressedInt(tomcat4_worker);
        out.writeCompressedInt(tomcat4_shutdown_port);
        out.writeNullUTF(tomcat4_shutdown_key);
        out.writeBoolean(isManual);
    }
}