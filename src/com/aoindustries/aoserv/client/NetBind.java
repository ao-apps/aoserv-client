package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * All listening network ports must be registered as a <code>NetBind</code>.  The
 * <code>NetBind</code> information is also used for internel server and external
 * network monitoring.  If either a network port is not listening that should,
 * or a network port is listening that should not, monitoring personnel are notified
 * to remove the discrepancy.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class NetBind extends CachedObjectIntegerKey<NetBind> implements Removable {

    static final int
        COLUMN_PKEY=0,
        COLUMN_PACKAGE=1,
        COLUMN_AO_SERVER=2,
        COLUMN_IP_ADDRESS=3
    ;

    String packageName;
    int ao_server;
    int ip_address;
    int port;
    String net_protocol;
    String app_protocol;
    private boolean open_firewall;
    private boolean monitoring_enabled;

    public Protocol getAppProtocol() {
	Protocol obj=table.connector.protocols.get(app_protocol);
	if(obj==null) throw new WrappedException(new SQLException("Unable to find Protocol: "+app_protocol));
	return obj;
    }

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_PACKAGE: return packageName;
            case COLUMN_AO_SERVER: return Integer.valueOf(ao_server);
            case COLUMN_IP_ADDRESS: return Integer.valueOf(ip_address);
            case 4: return Integer.valueOf(port);
            case 5: return net_protocol;
            case 6: return app_protocol;
            case 7: return open_firewall?Boolean.TRUE:Boolean.FALSE;
            case 8: return monitoring_enabled?Boolean.TRUE:Boolean.FALSE;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public String getDetails() {
        AOServer aoServer=getAOServerByDaemonNetBind();
        if(aoServer!=null) return "AOServDaemon";

        PostgresServer ps=getPostgresServer();
        if(ps!=null) return "PostgreSQL version "+ps.getPostgresVersion().getTechnologyVersion(table.connector).getVersion()+" in "+ps.getDataDirectory();

        HttpdWorker hw=getHttpdWorker();
        if(hw!=null) {
            HttpdSharedTomcat hst=hw.getHttpdSharedTomcat();
            if(hst!=null) {
                return
                    hw.getHttpdJKProtocol(table.connector).getProtocol(table.connector).getProtocol()
                    + " connector for Multi-Site Tomcat JVM version "
                    + hst.getHttpdTomcatVersion().getTechnologyVersion(table.connector).getVersion()
                    + " in "
                    + hst.getInstallDirectory()
                ;
            }
            HttpdTomcatSite hts=hw.getHttpdTomcatSite();
            if(hts!=null) {
                return
                    hw.getHttpdJKProtocol(table.connector).getProtocol(table.connector).getProtocol()
                    + " connector for Single-Site Tomcat JVM version "
                    + hts.getHttpdTomcatVersion().getTechnologyVersion(table.connector).getVersion()
                    + " in "
                    + hts.getHttpdSite().getInstallDirectory()
                ;
            }
        }

        HttpdSharedTomcat hst=getHttpdSharedTomcatByShutdownPort();
        if(hst!=null) {
            return
                "Shutdown port for Multi-Site Tomcat JVM version "
                + hst.getHttpdTomcatVersion().getTechnologyVersion(table.connector).getVersion()
                + " in "
                + hst.getInstallDirectory()
            ;
        }

        HttpdTomcatStdSite htss=getHttpdTomcatStdSiteByShutdownPort();
        if(htss!=null) {
            return
                "Shutdown port for Single-Site Tomcat JVM version "
                + htss.getHttpdTomcatSite().getHttpdTomcatVersion().getTechnologyVersion(table.connector).getVersion()
                + " in "
                + htss.getHttpdTomcatSite().getHttpdSite().getInstallDirectory()
            ;
        }

        HttpdBind hb=getHttpdBind();
        if(hb!=null) {
            HttpdServer hs=hb.getHttpdServer();
            return
                "Apache server #"
                + hs.getNumber()
                + " configured in /etc/httpd/conf/httpd"
                + hs.getNumber()
                + ".conf"
            ;
        }

        HttpdJBossSite hjs=getHttpdJBossSiteByJNPPort();
        if(hjs!=null) {
            return
                "JNP port for JBoss version "
                + hjs.getHttpdJBossVersion().getTechnologyVersion(table.connector).getVersion()
                + " in "
                + hjs.getHttpdTomcatSite().getHttpdSite().getInstallDirectory()
            ;
        }

        HttpdJBossSite hjbs=getHttpdJBossSiteByWebserverPort();
        if(hjbs!=null) {
            return
                "Webserver port for JBoss version "
                + hjbs.getHttpdJBossVersion().getTechnologyVersion(table.connector).getVersion()
                + " in "
                + hjbs.getHttpdTomcatSite().getHttpdSite().getInstallDirectory()
            ;
        }

        hjbs=getHttpdJBossSiteByRMIPort();
        if(hjbs!=null) {
            return
                "RMI port for JBoss version "
                + hjbs.getHttpdJBossVersion().getTechnologyVersion(table.connector).getVersion()
                + " in "
                + hjbs.getHttpdTomcatSite().getHttpdSite().getInstallDirectory()
            ;
        }

        hjbs=getHttpdJBossSiteByHypersonicPort();
        if(hjbs!=null) {
            return
                "Hypersonic port for JBoss version "
                + hjbs.getHttpdJBossVersion().getTechnologyVersion(table.connector).getVersion()
                + " in "
                + hjbs.getHttpdTomcatSite().getHttpdSite().getInstallDirectory()
            ;
        }

        hjbs=getHttpdJBossSiteByJMXPort();
        if(hjbs!=null) {
            return
                "JMX port for JBoss version "
                + hjbs.getHttpdJBossVersion().getTechnologyVersion(table.connector).getVersion()
                + " in "
                + hjbs.getHttpdTomcatSite().getHttpdSite().getInstallDirectory()
            ;
        }

        NetTcpRedirect ntr=getNetTcpRedirect();
        if(ntr!=null) return "Port redirected to "+ntr.getDestinationHost()+':'+ntr.getDestinationPort().getPort();

        PrivateFTPServer pfs=getPrivateFTPServer();
        if(pfs!=null) return "Private FTP server in "+pfs.getRoot();

        return null;
    }
    
    public AOServer getAOServerByDaemonNetBind() {
        return table.connector.aoServers.getAOServerByDaemonNetBind(this);
    }

    public HttpdBind getHttpdBind() {
        return table.connector.httpdBinds.get(pkey);
    }

    public HttpdJBossSite getHttpdJBossSiteByJNPPort() {
        return table.connector.httpdJBossSites.getHttpdJBossSiteByJNPPort(this);
    }

    public HttpdJBossSite getHttpdJBossSiteByWebserverPort() {
        return table.connector.httpdJBossSites.getHttpdJBossSiteByWebserverPort(this);
    }

    public HttpdJBossSite getHttpdJBossSiteByRMIPort() {
        return table.connector.httpdJBossSites.getHttpdJBossSiteByRMIPort(this);
    }

    public HttpdJBossSite getHttpdJBossSiteByHypersonicPort() {
        return table.connector.httpdJBossSites.getHttpdJBossSiteByHypersonicPort(this);
    }

    public HttpdJBossSite getHttpdJBossSiteByJMXPort() {
        return table.connector.httpdJBossSites.getHttpdJBossSiteByJMXPort(this);
    }

    public HttpdWorker getHttpdWorker() {
        return table.connector.httpdWorkers.getHttpdWorker(this);
    }

    public HttpdSharedTomcat getHttpdSharedTomcatByShutdownPort() {
        return table.connector.httpdSharedTomcats.getHttpdSharedTomcatByShutdownPort(this);
    }
    
    public HttpdTomcatStdSite getHttpdTomcatStdSiteByShutdownPort() {
        return table.connector.httpdTomcatStdSites.getHttpdTomcatStdSiteByShutdownPort(this);
    }

    public IPAddress getIPAddress() {
        IPAddress obj=table.connector.ipAddresses.get(ip_address);
        if(obj==null) throw new WrappedException(new SQLException("Unable to find IPAddress: "+ip_address));
        return obj;
    }

    public boolean isMonitoringEnabled() {
        return monitoring_enabled;
    }

    public NetProtocol getNetProtocol() {
        NetProtocol obj=table.connector.netProtocols.get(net_protocol);
        if(obj==null) throw new WrappedException(new SQLException("Unable to find NetProtocol: "+net_protocol));
        return obj;
    }

    public NetTcpRedirect getNetTcpRedirect() {
        return table.connector.netTcpRedirects.get(pkey);
    }

    public Package getPackage() {
        // May be filtered
        return table.connector.packages.get(packageName);
    }
    
    public PostgresServer getPostgresServer() {
        return table.connector.postgresServers.getPostgresServer(this);
    }

    public PrivateFTPServer getPrivateFTPServer() {
        return table.connector.privateFTPServers.get(pkey);
    }

    public NetPort getPort() {
        NetPort obj=table.connector.netPorts.get(port);
        if(obj==null) throw new WrappedException(new SQLException("Unable to find NetPort: "+port));
        return obj;
    }

    public AOServer getAOServer() {
        AOServer obj=table.connector.aoServers.get(ao_server);
        if(obj==null) throw new WrappedException(new SQLException("Unable to find AOServer: "+ao_server));
        return obj;
    }

    protected int getTableIDImpl() {
        return SchemaTable.NET_BINDS;
    }

    void initImpl(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
        packageName=result.getString(2);
        ao_server=result.getInt(3);
        ip_address=result.getInt(4);
        port=result.getInt(5);
        net_protocol=result.getString(6);
        app_protocol=result.getString(7);
        open_firewall=result.getBoolean(8);
        monitoring_enabled=result.getBoolean(9);
    }

    public boolean isFirewallOpen() {
        return open_firewall;
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        packageName=in.readUTF();
        ao_server=in.readCompressedInt();
        ip_address=in.readCompressedInt();
        port=in.readCompressedInt();
        net_protocol=in.readUTF();
        app_protocol=in.readUTF();
        open_firewall=in.readBoolean();
        monitoring_enabled=in.readBoolean();
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();

        AOServConnector conn=table.connector;

        // Must be able to access package
        if(getPackage()==null) reasons.add(new CannotRemoveReason<Package>("Unable to access package: "+packageName));

        // ao_servers
        for(AOServer ao : conn.aoServers.getRows()) {
            if(
                pkey==ao.daemon_bind
                || pkey==ao.daemon_connect_bind
            ) reasons.add(new CannotRemoveReason<AOServer>("Used as aoserv-daemon port for server: "+ao.getServer().getHostname(), ao));
        }

        // httpd_binds
        for(HttpdBind hb : conn.httpdBinds.getRows()) {
            if(equals(hb.getNetBind())) {
                HttpdServer hs=hb.getHttpdServer();
                reasons.add(new CannotRemoveReason<HttpdBind>("Used by Apache server #"+hs.getNumber()+" on "+hs.getAOServer().getServer().getHostname(), hb));
            }
        }

        // httpd_jboss_sites
        for(HttpdJBossSite hjb : conn.httpdJBossSites.getRows()) {
            HttpdSite hs=hjb.getHttpdTomcatSite().getHttpdSite();
            if(equals(hjb.getJnpBind())) reasons.add(new CannotRemoveReason<HttpdJBossSite>("Used as JNP port for JBoss site "+hs.getInstallDirectory()+" on "+hs.getAOServer().getServer().getHostname(), hjb));
            if(equals(hjb.getWebserverBind())) reasons.add(new CannotRemoveReason<HttpdJBossSite>("Used as Webserver port for JBoss site "+hs.getInstallDirectory()+" on "+hs.getAOServer().getServer().getHostname(), hjb));
            if(equals(hjb.getRmiBind())) reasons.add(new CannotRemoveReason<HttpdJBossSite>("Used as RMI port for JBoss site "+hs.getInstallDirectory()+" on "+hs.getAOServer().getServer().getHostname(), hjb));
            if(equals(hjb.getHypersonicBind())) reasons.add(new CannotRemoveReason<HttpdJBossSite>("Used as Hypersonic port for JBoss site "+hs.getInstallDirectory()+" on "+hs.getAOServer().getServer().getHostname(), hjb));
            if(equals(hjb.getJmxBind())) reasons.add(new CannotRemoveReason<HttpdJBossSite>("Used as JMX port for JBoss site "+hs.getInstallDirectory()+" on "+hs.getAOServer().getServer().getHostname(), hjb));
        }
        
        // httpd_shared_tomcats
        for(HttpdSharedTomcat hst : conn.httpdSharedTomcats.getRows()) {
            if(equals(hst.getTomcat4ShutdownPort())) reasons.add(new CannotRemoveReason<HttpdSharedTomcat>("Used as shutdown port for Multi-Site Tomcat JVM "+hst.getInstallDirectory()+" on "+hst.getAOServer().getServer().getHostname(), hst));
        }
        
        // httpd_tomcat_std_sites
        for(HttpdTomcatStdSite hts : conn.httpdTomcatStdSites.getRows()) {
            HttpdSite hs=hts.getHttpdTomcatSite().getHttpdSite();
            if(equals(hts.getTomcat4ShutdownPort())) reasons.add(new CannotRemoveReason<HttpdTomcatStdSite>("Used as shutdown port for Single-Site Tomcat JVM "+hs.getInstallDirectory()+" on "+hs.getAOServer().getServer().getHostname(), hts));
        }

        // httpd_workers
        for(HttpdWorker hw : conn.httpdWorkers.getRows()) {
            if(equals(hw.getNetBind())) {
                HttpdSharedTomcat hst=hw.getHttpdSharedTomcat();
                if(hst!=null) reasons.add(new CannotRemoveReason<HttpdSharedTomcat>("Used as mod_jk worker for Multi-Site Tomcat JVM "+hst.getInstallDirectory()+" on "+hst.getAOServer().getServer().getHostname(), hst));
                
                HttpdTomcatSite hts=hw.getHttpdTomcatSite();
                if(hts!=null) {
                    HttpdSite hs=hts.getHttpdSite();
                    reasons.add(new CannotRemoveReason<HttpdTomcatSite>("Used as mod_jk worker for Tomcat JVM "+hs.getInstallDirectory()+" on "+hs.getAOServer().getServer().getHostname(), hts));
                }
            }
        }
        
        // mysql_servers
        for(MySQLServer ms : conn.mysqlServers.getRows()) {
            if(equals(ms.getNetBind())) reasons.add(new CannotRemoveReason<MySQLServer>("Used for MySQL server "+ms.getName()+" on "+ms.getAOServer().getServer().getHostname(), ms));
        }

        // postgres_servers
        for(PostgresServer ps : conn.postgresServers.getRows()) {
            if(equals(ps.getNetBind())) reasons.add(new CannotRemoveReason<PostgresServer>("Used for PostgreSQL server "+ps.getName()+" on "+ps.getAOServer().getServer().getHostname(), ps));
        }

        return reasons;
    }

    public void remove() {
        table.connector.requestUpdateIL(
            AOServProtocol.REMOVE,
            SchemaTable.NET_BINDS,
            pkey
        );
    }

    public void setMonitoringEnabled(boolean monitoring_enabled) {
        table.connector.requestUpdateIL(
            AOServProtocol.SET_NET_BIND_MONITORING,
            pkey,
            monitoring_enabled
        );
    }

    public void setOpenFirewall(boolean open_firewall) {
        table.connector.requestUpdateIL(
            AOServProtocol.SET_NET_BIND_OPEN_FIREWALL,
            pkey,
            open_firewall
        );
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeUTF(packageName);
        out.writeCompressedInt(ao_server);
        out.writeCompressedInt(ip_address);
        out.writeCompressedInt(port);
        out.writeUTF(net_protocol);
        out.writeUTF(app_protocol);
        out.writeBoolean(open_firewall);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_0_A_104)>=0) {
            out.writeBoolean(monitoring_enabled);
        } else {
            out.writeCompressedInt(monitoring_enabled?300000:-1);
            writeNullUTF(out, null);
            writeNullUTF(out, null);
            writeNullUTF(out, null);
        }
    }
}