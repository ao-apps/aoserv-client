package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.util.StringUtility;
import com.aoindustries.util.WrappedException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
        COLUMN_SERVER=2,
        COLUMN_IP_ADDRESS=3
    ;
    static final String COLUMN_SERVER_name = "server";
    static final String COLUMN_IP_ADDRESS_name = "ip_address";
    static final String COLUMN_PORT_name = "port";
    static final String COLUMN_NET_PROTOCOL_name = "net_protocol";

    String packageName;
    int server;
    int ip_address;
    int port;
    String net_protocol;
    String app_protocol;
    private boolean open_firewall;
    private boolean monitoring_enabled;
    private String monitoring_parameters;

    public Protocol getAppProtocol() throws SQLException, IOException {
	Protocol obj=table.connector.getProtocols().get(app_protocol);
	if(obj==null) throw new SQLException("Unable to find Protocol: "+app_protocol);
	return obj;
    }

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_PACKAGE: return packageName;
            case COLUMN_SERVER: return Integer.valueOf(server);
            case COLUMN_IP_ADDRESS: return Integer.valueOf(ip_address);
            case 4: return Integer.valueOf(port);
            case 5: return net_protocol;
            case 6: return app_protocol;
            case 7: return open_firewall?Boolean.TRUE:Boolean.FALSE;
            case 8: return monitoring_enabled?Boolean.TRUE:Boolean.FALSE;
            case 9: return monitoring_parameters;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public String getDetails() throws SQLException, IOException {
        AOServer aoServer=getAOServerByDaemonNetBind();
        if(aoServer!=null) return "AOServDaemon";

        AOServer jilterServer=getAOServerByJilterNetBind();
        if(jilterServer!=null) return "AOServDaemon.JilterManager";
        
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
        if(pfs!=null) return "Private FTP server in "+pfs.getLinuxServerAccount().getHome();

        return null;
    }
    
    public AOServer getAOServerByDaemonNetBind() throws IOException, SQLException {
        return table.connector.getAoServers().getAOServerByDaemonNetBind(this);
    }

    public AOServer getAOServerByJilterNetBind() throws IOException, SQLException {
        return table.connector.getAoServers().getAOServerByJilterNetBind(this);
    }

    public HttpdBind getHttpdBind() throws IOException, SQLException {
        return table.connector.getHttpdBinds().get(pkey);
    }

    public HttpdJBossSite getHttpdJBossSiteByJNPPort() throws IOException, SQLException {
        return table.connector.getHttpdJBossSites().getHttpdJBossSiteByJNPPort(this);
    }

    public HttpdJBossSite getHttpdJBossSiteByWebserverPort() throws IOException, SQLException {
        return table.connector.getHttpdJBossSites().getHttpdJBossSiteByWebserverPort(this);
    }

    public HttpdJBossSite getHttpdJBossSiteByRMIPort() throws IOException, SQLException {
        return table.connector.getHttpdJBossSites().getHttpdJBossSiteByRMIPort(this);
    }

    public HttpdJBossSite getHttpdJBossSiteByHypersonicPort() throws IOException, SQLException {
        return table.connector.getHttpdJBossSites().getHttpdJBossSiteByHypersonicPort(this);
    }

    public HttpdJBossSite getHttpdJBossSiteByJMXPort() throws IOException, SQLException {
        return table.connector.getHttpdJBossSites().getHttpdJBossSiteByJMXPort(this);
    }

    public HttpdWorker getHttpdWorker() throws IOException, SQLException {
        return table.connector.getHttpdWorkers().getHttpdWorker(this);
    }

    public HttpdSharedTomcat getHttpdSharedTomcatByShutdownPort() throws SQLException, IOException {
        return table.connector.getHttpdSharedTomcats().getHttpdSharedTomcatByShutdownPort(this);
    }
    
    public HttpdTomcatStdSite getHttpdTomcatStdSiteByShutdownPort() throws IOException, SQLException {
        return table.connector.getHttpdTomcatStdSites().getHttpdTomcatStdSiteByShutdownPort(this);
    }

    public IPAddress getIPAddress() throws SQLException, IOException {
        IPAddress obj=table.connector.getIpAddresses().get(ip_address);
        if(obj==null) throw new SQLException("Unable to find IPAddress: "+ip_address);
        return obj;
    }

    public boolean isMonitoringEnabled() {
        return monitoring_enabled;
    }

    public NetProtocol getNetProtocol() throws SQLException, IOException {
        NetProtocol obj=table.connector.getNetProtocols().get(net_protocol);
        if(obj==null) throw new SQLException("Unable to find NetProtocol: "+net_protocol);
        return obj;
    }

    public NetTcpRedirect getNetTcpRedirect() throws IOException, SQLException {
        return table.connector.getNetTcpRedirects().get(pkey);
    }

    public Package getPackage() throws IOException, SQLException {
        // May be filtered
        return table.connector.getPackages().get(packageName);
    }
    
    public PostgresServer getPostgresServer() throws IOException, SQLException {
        return table.connector.getPostgresServers().getPostgresServer(this);
    }

    public PrivateFTPServer getPrivateFTPServer() throws IOException, SQLException {
        return table.connector.getPrivateFTPServers().get(pkey);
    }

    public NetPort getPort() throws SQLException {
        NetPort obj=table.connector.getNetPorts().get(port);
        if(obj==null) throw new SQLException("Unable to find NetPort: "+port);
        return obj;
    }

    public Server getServer() throws SQLException, IOException {
        Server obj=table.connector.getServers().get(server);
        if(obj==null) throw new SQLException("Unable to find Server: "+server);
        return obj;
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.NET_BINDS;
    }

    public void init(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
        packageName=result.getString(2);
        server=result.getInt(3);
        ip_address=result.getInt(4);
        port=result.getInt(5);
        net_protocol=result.getString(6);
        app_protocol=result.getString(7);
        open_firewall=result.getBoolean(8);
        monitoring_enabled=result.getBoolean(9);
        monitoring_parameters=result.getString(10);
        getMonitoringParametersCache = null;
    }

    public boolean isFirewallOpen() {
        return open_firewall;
    }

    /**
     * Encodes the parameters.  Will not return null.
     */
    public static String encodeParameters(Map<String,String> monitoringParameters) {
        try {
            StringBuilder SB = new StringBuilder();
            for(Map.Entry<String,String> entry : monitoringParameters.entrySet()) {
                String name = entry.getKey();
                String value = entry.getValue();
                if(SB.length()>0) SB.append('&');
                SB.append(URLEncoder.encode(name, "UTF-8")).append('=').append(URLEncoder.encode(value, "UTF-8"));
            }
            return SB.toString();
        } catch(UnsupportedEncodingException err) {
            throw new WrappedException(err);
        }
    }

    public static Map<String,String> decodeParameters(String monitoringParameters) {
        if(monitoringParameters==null) return Collections.emptyMap();
        else {
            try {
                String[] nameValues = StringUtility.splitString(monitoringParameters, '&');
                Map<String,String> newMap = new HashMap<String,String>(nameValues.length*4/3+1);
                for(String nameValue : nameValues) {
                    String name;
                    String value;
                    int pos = nameValue.indexOf('=');
                    if(pos==-1) {
                        name = URLDecoder.decode(nameValue, "UTF-8");
                        value = "";
                    } else {
                        name = URLDecoder.decode(nameValue.substring(0, pos), "UTF-8");
                        value = URLDecoder.decode(nameValue.substring(pos+1), "UTF-8");
                    }
                    if(name.length()>0 || value.length()>0) newMap.put(name, value);
                }
                return newMap;
            } catch(UnsupportedEncodingException err) {
                throw new WrappedException(err);
            }
        }
    }

    private volatile Map<String,String> getMonitoringParametersCache;

    /**
     * Gets the unmodifiable map of parameters for this bind.
     */
    public Map<String,String> getMonitoringParameters() {
        if(getMonitoringParametersCache==null) getMonitoringParametersCache=Collections.unmodifiableMap(decodeParameters(monitoring_parameters));
        return getMonitoringParametersCache;
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        packageName=in.readUTF().intern();
        server=in.readCompressedInt();
        ip_address=in.readCompressedInt();
        port=in.readCompressedInt();
        net_protocol=in.readUTF().intern();
        app_protocol=in.readUTF().intern();
        open_firewall=in.readBoolean();
        monitoring_enabled=in.readBoolean();
        monitoring_parameters=in.readNullUTF();
        getMonitoringParametersCache = null;
    }

    public List<CannotRemoveReason> getCannotRemoveReasons(Locale userLocale) throws IOException, SQLException {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();

        AOServConnector conn=table.connector;

        // Must be able to access package
        if(getPackage()==null) reasons.add(new CannotRemoveReason<Package>("Unable to access package: "+packageName));

        // ao_servers
        for(AOServer ao : conn.getAoServers().getRows()) {
            if(
                pkey==ao.daemon_bind
                || pkey==ao.daemon_connect_bind
            ) reasons.add(new CannotRemoveReason<AOServer>("Used as aoserv-daemon port for server: "+ao.getHostname(), ao));
            if(pkey==ao.jilter_bind) reasons.add(new CannotRemoveReason<AOServer>("Used as aoserv-daemon jilter port for server: "+ao.getHostname(), ao));
        }

        // httpd_binds
        for(HttpdBind hb : conn.getHttpdBinds().getRows()) {
            if(equals(hb.getNetBind())) {
                HttpdServer hs=hb.getHttpdServer();
                reasons.add(new CannotRemoveReason<HttpdBind>("Used by Apache server #"+hs.getNumber()+" on "+hs.getAOServer().getHostname(), hb));
            }
        }

        // httpd_jboss_sites
        for(HttpdJBossSite hjb : conn.getHttpdJBossSites().getRows()) {
            HttpdSite hs=hjb.getHttpdTomcatSite().getHttpdSite();
            if(equals(hjb.getJnpBind())) reasons.add(new CannotRemoveReason<HttpdJBossSite>("Used as JNP port for JBoss site "+hs.getInstallDirectory()+" on "+hs.getAOServer().getHostname(), hjb));
            if(equals(hjb.getWebserverBind())) reasons.add(new CannotRemoveReason<HttpdJBossSite>("Used as Webserver port for JBoss site "+hs.getInstallDirectory()+" on "+hs.getAOServer().getHostname(), hjb));
            if(equals(hjb.getRmiBind())) reasons.add(new CannotRemoveReason<HttpdJBossSite>("Used as RMI port for JBoss site "+hs.getInstallDirectory()+" on "+hs.getAOServer().getHostname(), hjb));
            if(equals(hjb.getHypersonicBind())) reasons.add(new CannotRemoveReason<HttpdJBossSite>("Used as Hypersonic port for JBoss site "+hs.getInstallDirectory()+" on "+hs.getAOServer().getHostname(), hjb));
            if(equals(hjb.getJmxBind())) reasons.add(new CannotRemoveReason<HttpdJBossSite>("Used as JMX port for JBoss site "+hs.getInstallDirectory()+" on "+hs.getAOServer().getHostname(), hjb));
        }
        
        // httpd_shared_tomcats
        for(HttpdSharedTomcat hst : conn.getHttpdSharedTomcats().getRows()) {
            if(equals(hst.getTomcat4ShutdownPort())) reasons.add(new CannotRemoveReason<HttpdSharedTomcat>("Used as shutdown port for Multi-Site Tomcat JVM "+hst.getInstallDirectory()+" on "+hst.getAOServer().getHostname(), hst));
        }
        
        // httpd_tomcat_std_sites
        for(HttpdTomcatStdSite hts : conn.getHttpdTomcatStdSites().getRows()) {
            HttpdSite hs=hts.getHttpdTomcatSite().getHttpdSite();
            if(equals(hts.getTomcat4ShutdownPort())) reasons.add(new CannotRemoveReason<HttpdTomcatStdSite>("Used as shutdown port for Single-Site Tomcat JVM "+hs.getInstallDirectory()+" on "+hs.getAOServer().getHostname(), hts));
        }

        // httpd_workers
        for(HttpdWorker hw : conn.getHttpdWorkers().getRows()) {
            if(equals(hw.getNetBind())) {
                HttpdSharedTomcat hst=hw.getHttpdSharedTomcat();
                if(hst!=null) reasons.add(new CannotRemoveReason<HttpdSharedTomcat>("Used as mod_jk worker for Multi-Site Tomcat JVM "+hst.getInstallDirectory()+" on "+hst.getAOServer().getHostname(), hst));
                
                HttpdTomcatSite hts=hw.getHttpdTomcatSite();
                if(hts!=null) {
                    HttpdSite hs=hts.getHttpdSite();
                    reasons.add(new CannotRemoveReason<HttpdTomcatSite>("Used as mod_jk worker for Tomcat JVM "+hs.getInstallDirectory()+" on "+hs.getAOServer().getHostname(), hts));
                }
            }
        }
        
        // mysql_servers
        for(MySQLServer ms : conn.getMysqlServers().getRows()) {
            if(equals(ms.getNetBind())) reasons.add(new CannotRemoveReason<MySQLServer>("Used for MySQL server "+ms.getName()+" on "+ms.getAOServer().getHostname(), ms));
        }

        // postgres_servers
        for(PostgresServer ps : conn.getPostgresServers().getRows()) {
            if(equals(ps.getNetBind())) reasons.add(new CannotRemoveReason<PostgresServer>("Used for PostgreSQL server "+ps.getName()+" on "+ps.getAOServer().getHostname(), ps));
        }

        return reasons;
    }

    public void remove() throws IOException, SQLException {
        table.connector.requestUpdateIL(
            true,
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.NET_BINDS,
            pkey
        );
    }

    public void setMonitoringEnabled(boolean monitoring_enabled) throws IOException, SQLException {
        table.connector.requestUpdateIL(
            true,
            AOServProtocol.CommandID.SET_NET_BIND_MONITORING,
            pkey,
            monitoring_enabled
        );
    }

    public void setOpenFirewall(boolean open_firewall) throws IOException, SQLException {
        table.connector.requestUpdateIL(
            true,
            AOServProtocol.CommandID.SET_NET_BIND_OPEN_FIREWALL,
            pkey,
            open_firewall
        );
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeUTF(packageName);
        out.writeCompressedInt(server);
        out.writeCompressedInt(ip_address);
        out.writeCompressedInt(port);
        out.writeUTF(net_protocol);
        out.writeUTF(app_protocol);
        out.writeBoolean(open_firewall);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_104)>=0) {
            out.writeBoolean(monitoring_enabled);
        } else {
            out.writeCompressedInt(monitoring_enabled?300000:-1);
            out.writeNullUTF(null);
            out.writeNullUTF(null);
            out.writeNullUTF(null);
        }
        if(version.compareTo(AOServProtocol.Version.VERSION_1_58)>=0) out.writeNullUTF(monitoring_parameters);
    }
}