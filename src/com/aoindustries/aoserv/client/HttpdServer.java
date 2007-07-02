package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.sql.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.List;

/**
 * An <code>HttpdServer</code> represents one running instance of the
 * Apache web server.  Each physical server may run any number of
 * Apache web servers, and each of those may respond to multiple
 * IP addresses and ports, and serve content for many sites.
 *
 * @see  HttpdBind
 * @see  HttpdSite
 * @see  HttpdSiteBind
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdServer extends CachedObjectIntegerKey<HttpdServer> {

    static final int
        COLUMN_PKEY=0,
        COLUMN_AO_SERVER=1,
        COLUMN_PACKAGE=10
    ;

    /**
     * The highest recommended number of sites to bind in one server.
     */
    public static final int RECOMMENDED_MAXIMUM_BINDS=128;

    int ao_server;
    private int number;
    private boolean can_add_sites;
    private boolean is_mod_jk;
    private int max_binds;
    int linux_server_account;
    int linux_server_group;
    private int mod_php_version;
    private boolean use_suexec;
    private int packageNum;
    private boolean is_shared;
    private boolean use_mod_perl;
    private int timeout;

    public boolean canAddSites() {
	return can_add_sites;
    }

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_AO_SERVER: return Integer.valueOf(ao_server);
            case 2: return Integer.valueOf(number);
            case 3: return can_add_sites?Boolean.TRUE:Boolean.FALSE;
            case 4: return is_mod_jk?Boolean.TRUE:Boolean.FALSE;
            case 5: return Integer.valueOf(max_binds);
            case 6: return Integer.valueOf(linux_server_account);
            case 7: return Integer.valueOf(linux_server_group);
            case 8: return mod_php_version==-1?null:Integer.valueOf(mod_php_version);
            case 9: return use_suexec?Boolean.TRUE:Boolean.FALSE;
            case COLUMN_PACKAGE: return Integer.valueOf(packageNum);
            case 11: return is_shared?Boolean.TRUE:Boolean.FALSE;
            case 12: return use_mod_perl?Boolean.TRUE:Boolean.FALSE;
            case 13: return Integer.valueOf(timeout);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public List<HttpdBind> getHttpdBinds() {
	return table.connector.httpdBinds.getHttpdBinds(this);
    }

    public List<HttpdSite> getHttpdSites() {
	return table.connector.httpdSites.getHttpdSites(this);
    }

    public List<HttpdWorker> getHttpdWorkers() {
	return table.connector.httpdWorkers.getHttpdWorkers(this);
    }

    public int getMaxBinds() {
        return max_binds;
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

    public TechnologyVersion getModPhpVersion() {
        if(mod_php_version==-1) return null;
        TechnologyVersion tv=table.connector.technologyVersions.get(mod_php_version);
        if(tv==null) throw new WrappedException(new SQLException("Unable to find TechnologyVersion: "+mod_php_version));
        return tv;
    }

    public boolean useSuexec() {
        return use_suexec;
    }

    public Package getPackage() {
        // Package may be filtered
        return table.connector.packages.get(packageNum);
    }

    public boolean isShared() {
        return is_shared;
    }
    
    public boolean useModPERL() {
        return use_mod_perl;
    }
    
    /**
     * Gets the timeout value in seconds.
     */
    public int getTimeOut() {
        return timeout;
    }

    public int getNumber() {
	return number;
    }

    public AOServer getAOServer() {
	AOServer obj=table.connector.aoServers.get(ao_server);
	if(obj==null) throw new WrappedException(new SQLException("Unable to find AOServer: "+ao_server));
	return obj;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.HTTPD_SERVERS;
    }

    void initImpl(ResultSet result) throws SQLException {
	pkey=result.getInt(1);
	ao_server=result.getInt(2);
	number=result.getInt(3);
	can_add_sites=result.getBoolean(4);
        is_mod_jk=result.getBoolean(5);
        max_binds=result.getInt(6);
        linux_server_account=result.getInt(7);
        linux_server_group=result.getInt(8);
        mod_php_version=result.getInt(9);
        if(result.wasNull()) mod_php_version=-1;
        use_suexec=result.getBoolean(10);
        packageNum=result.getInt(11);
        is_shared=result.getBoolean(12);
        use_mod_perl=result.getBoolean(13);
        timeout=result.getInt(14);
    }

    public boolean isModJK() {
        return is_mod_jk;
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readCompressedInt();
	ao_server=in.readCompressedInt();
	number=in.readCompressedInt();
	can_add_sites=in.readBoolean();
        is_mod_jk=in.readBoolean();
        max_binds=in.readCompressedInt();
        linux_server_account=in.readCompressedInt();
        linux_server_group=in.readCompressedInt();
        mod_php_version=in.readCompressedInt();
        use_suexec=in.readBoolean();
        packageNum=in.readCompressedInt();
        is_shared=in.readBoolean();
        use_mod_perl=in.readBoolean();
        timeout=in.readCompressedInt();
    }

    String toStringImpl() {
	return "httpd"+number;
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeCompressedInt(pkey);
	out.writeCompressedInt(ao_server);
	out.writeCompressedInt(number);
	out.writeBoolean(can_add_sites);
        out.writeBoolean(is_mod_jk);
        out.writeCompressedInt(max_binds);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_0_A_102)>=0) {
            out.writeCompressedInt(linux_server_account);
            out.writeCompressedInt(linux_server_group);
            out.writeCompressedInt(mod_php_version);
            out.writeBoolean(use_suexec);
            out.writeCompressedInt(packageNum);
            if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_0_A_122)<=0) out.writeCompressedInt(-1);
            out.writeBoolean(is_shared);
        }
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_0_A_103)>=0) {
            out.writeBoolean(use_mod_perl);
        }
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_0_A_130)>=0) {
            out.writeCompressedInt(timeout);
        }
    }
}
