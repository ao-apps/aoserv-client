package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.util.StringUtility;
import com.aoindustries.util.WrappedException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * All listening network ports must be registered as a <code>NetBind</code>.  The
 * <code>NetBind</code> information is also used for internel server and external
 * network monitoring.  If either a network port is not listening that should,
 * or a network port is listening that should not, monitoring personnel are notified
 * to remove the discrepancy.
 *
 * @author  AO Industries, Inc.
 */
final public class NetBind extends AOServObjectIntegerKey<NetBind> /*implements Removable*/ {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private int business_server;
    final private int ip_address;
    final private int port;
    final private String net_protocol;
    final private String app_protocol;
    final private boolean open_firewall;
    final private boolean monitoring_enabled;
    final private String monitoring_parameters;

    public NetBind(
        NetBindService<?,?> service,
        int pkey,
        int business_server,
        int ip_address,
        int port,
        String net_protocol,
        String app_protocol,
        boolean open_firewall,
        boolean monitoring_enabled,
        String monitoring_parameters
    ) {
        super(service, pkey);
        this.business_server = business_server;
        this.ip_address = ip_address;
        this.port = port;
        this.net_protocol = net_protocol.intern();
        this.app_protocol = app_protocol.intern();
        this.open_firewall = open_firewall;
        this.monitoring_enabled = monitoring_enabled;
        this.monitoring_parameters = monitoring_parameters;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(NetBind other) throws RemoteException {
        // TODO: int diff = getBusinessServer().compareTo(other.getBusinessServer());
        // TODO: if(diff!=0) return diff;
        // TODO: diff = getIPAddress().compareTo(other.getIPAddress());
        // TODO: if(diff!=0) return diff;
        int diff = compare(port, other.port);
        if(diff!=0) return diff;
        return getNetProtocol().compareTo(other.getNetProtocol());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="pkey", unique=true, description="a generated pkey")
    public int getPkey() {
        return key;
    }

    /**
     * May be filtered.
     */
    /* TODO
    @SchemaColumn(order=1, name="business_server", description="the business and server this bind is on")
    public BusinessServer getBusinessServer() throws IOException, SQLException {
        return getService().getConnector().getBusinessServers().get(business_server);
    }*/

    /* TODO
    @SchemaColumn(order=2, name="ip_address", description="the pkey of the IP address that is bound to")
    public IPAddress getIPAddress() throws SQLException, IOException {
        IPAddress obj=getService().getConnector().getIpAddresses().get(ip_address);
        if(obj==null) throw new SQLException("Unable to find IPAddress: "+ip_address);
        return obj;
    }*/

    /* TODO
    @SchemaColumn(order=3, name="port", description="the port number that is bound")
    public NetPort getPort() throws SQLException {
        NetPort obj=getService().getConnector().getNetPorts().get(port);
        if(obj==null) throw new SQLException("Unable to find NetPort: "+port);
        return obj;
    }*/

    @SchemaColumn(order=1, name="net_protocol", description="the network protocol (<code>net_protocols</code>)")
    public NetProtocol getNetProtocol() throws RemoteException {
        return getService().getConnector().getNetProtocols().get(net_protocol);
    }

    @SchemaColumn(order=2, name="app_protocol", description="the application protocol (<code>protocols</code>)")
    public Protocol getAppProtocol() throws SQLException, IOException {
        return getService().getConnector().getProtocols().get(app_protocol);
    }

    @SchemaColumn(order=3, name="open_firewall", description="flags if the firewall should be opened for this port")
    public boolean isFirewallOpen() {
        return open_firewall;
    }

    @SchemaColumn(order=4, name="monitoring_enabled", description="turns on monitoring of the port")
    public boolean isMonitoringEnabled() {
        return monitoring_enabled;
    }

    /**
     * Gets the unmodifiable map of parameters for this bind.
     */
    @SchemaColumn(order=8, name="monitoring_parameters", description="the URL-encoded name=value pairs of monitoring parameters")
    public Map<String,String> getMonitoringParameters() {
        String myParamString = monitoring_parameters;
        if(myParamString==null) return Collections.emptyMap();
        Map<String,String> params = getMonitoringParametersCache.get(myParamString);
        if(params==null) {
            params = Collections.unmodifiableMap(decodeParameters(myParamString));
            Map<String,String> previous = getMonitoringParametersCache.putIfAbsent(myParamString, params);
            if(previous!=null) params = previous;
        }
        return params;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependencies() throws RemoteException {
        return createDependencySet(
            // TODO: getBusinessServer(),
            // TODO: getIPAddress()
        );
    }

    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return createDependencySet(
            // TODO: getAOServerByDaemonNetBind(),
            // TODO: getAOServerByDaemonConnectNetBind(),
            // TODO: getAOServerByJilterNetBind(),
            // TODO: getBrandByAowebStrutsVncBind(),
            // TODO: getNetTcpRedirect(),
            // TODO: getEmailSmartHost(),
            // TODO: getHttpdBind(),
            // TODO: getHttpdJBossSiteByJNPPort(),
            // TODO: getHttpdJBossSiteByWebserverPort(),
            // TODO: getHttpdJBossSiteByRMIPort(),
            // TODO: getHttpdJBossSiteByHypersonicPort(),
            // TODO: getHttpdJBossSiteByJMXPort(),
            // TODO: getHttpdSharedTomcatByShutdownPort(),
            // TODO: getHttpdWorker(),
            // TODO: getHttpdTomcatStdSiteByShutdownPort(),
            // TODO: getMySQLServer(),
            // TODO: getPrivateFTPServer(),
            // TODO: getPostgresServer()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    /* TODO
    public AOServer getAOServerByDaemonNetBind() throws IOException, SQLException {
        return getService().getConnector().getAoServers().getAOServerByDaemonNetBind(this);
    }

    public AOServer getAOServerByDaemonConnectNetBind() throws IOException, SQLException {
        return getService().getConnector().getAoServers().getAOServerByDaemonConnectNetBind(this);
    }

    public AOServer getAOServerByJilterNetBind() throws IOException, SQLException {
        return getService().getConnector().getAoServers().getAOServerByJilterNetBind(this);
    }

    public HttpdBind getHttpdBind() throws IOException, SQLException {
        return getService().getConnector().getHttpdBinds().get(pkey);
    }

    public HttpdJBossSite getHttpdJBossSiteByJNPPort() throws IOException, SQLException {
        return getService().getConnector().getHttpdJBossSites().getHttpdJBossSiteByJNPPort(this);
    }

    public HttpdJBossSite getHttpdJBossSiteByWebserverPort() throws IOException, SQLException {
        return getService().getConnector().getHttpdJBossSites().getHttpdJBossSiteByWebserverPort(this);
    }

    public HttpdJBossSite getHttpdJBossSiteByRMIPort() throws IOException, SQLException {
        return getService().getConnector().getHttpdJBossSites().getHttpdJBossSiteByRMIPort(this);
    }

    public HttpdJBossSite getHttpdJBossSiteByHypersonicPort() throws IOException, SQLException {
        return getService().getConnector().getHttpdJBossSites().getHttpdJBossSiteByHypersonicPort(this);
    }

    public HttpdJBossSite getHttpdJBossSiteByJMXPort() throws IOException, SQLException {
        return getService().getConnector().getHttpdJBossSites().getHttpdJBossSiteByJMXPort(this);
    }

    public HttpdWorker getHttpdWorker() throws IOException, SQLException {
        return getService().getConnector().getHttpdWorkers().getHttpdWorker(this);
    }

    public HttpdSharedTomcat getHttpdSharedTomcatByShutdownPort() throws SQLException, IOException {
        return getService().getConnector().getHttpdSharedTomcats().getHttpdSharedTomcatByShutdownPort(this);
    }

    public HttpdTomcatStdSite getHttpdTomcatStdSiteByShutdownPort() throws IOException, SQLException {
        return getService().getConnector().getHttpdTomcatStdSites().getHttpdTomcatStdSiteByShutdownPort(this);
    }

    public NetTcpRedirect getNetTcpRedirect() throws IOException, SQLException {
        return getService().getConnector().getNetTcpRedirects().get(pkey);
    }

    public PostgresServer getPostgresServer() throws IOException, SQLException {
        return getService().getConnector().getPostgresServers().getPostgresServer(this);
    }

    public PrivateFTPServer getPrivateFTPServer() throws IOException, SQLException {
        return getService().getConnector().getPrivateFTPServers().get(pkey);
    }

    public Brand getBrandByAowebStrutsVncBind() throws IOException, SQLException {
        return getService().getConnector().getBrands().getUniqueRow(Brand.COLUMN_AOWEB_STRUTS_VNC_BIND, pkey);
    }

    public EmailSmtpSmartHost getEmailSmartHost() throws IOException, SQLException {
        return getService().getConnector().getEmailSmtpSmartHosts().getUniqueRow(EmailSmtpSmartHost.COLUMN_NET_BIND, pkey);
    }

    public MySQLServer getMySQLServer() throws IOException, SQLException {
        return getService().getConnector().getMysqlServers().getUniqueRow(MySQLServer.COLUMN_NET_BIND, pkey);
    }
     */
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Parameter Encoding">
    private static final ConcurrentMap<String,Map<String,String>> getMonitoringParametersCache = new ConcurrentHashMap<String,Map<String,String>>();

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
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
    /* TODO
    public String getDetails() throws SQLException, IOException {
        AOServer aoServer=getAOServerByDaemonNetBind();
        if(aoServer!=null) return "AOServDaemon";

        AOServer jilterServer=getAOServerByJilterNetBind();
        if(jilterServer!=null) return "AOServDaemon.JilterManager";

        PostgresServer ps=getPostgresServer();
        if(ps!=null) return "PostgreSQL version "+ps.getPostgresVersion().getTechnologyVersion(getService().getConnector()).getVersion()+" in "+ps.getDataDirectory();

        HttpdWorker hw=getHttpdWorker();
        if(hw!=null) {
            HttpdSharedTomcat hst=hw.getHttpdSharedTomcat();
            if(hst!=null) {
                return
                    hw.getHttpdJKProtocol(getService().getConnector()).getProtocol(getService().getConnector()).getProtocol()
                    + " connector for Multi-Site Tomcat JVM version "
                    + hst.getHttpdTomcatVersion().getTechnologyVersion(getService().getConnector()).getVersion()
                    + " in "
                    + hst.getInstallDirectory()
                ;
            }
            HttpdTomcatSite hts=hw.getHttpdTomcatSite();
            if(hts!=null) {
                return
                    hw.getHttpdJKProtocol(getService().getConnector()).getProtocol(getService().getConnector()).getProtocol()
                    + " connector for Single-Site Tomcat JVM version "
                    + hts.getHttpdTomcatVersion().getTechnologyVersion(getService().getConnector()).getVersion()
                    + " in "
                    + hts.getHttpdSite().getInstallDirectory()
                ;
            }
        }

        HttpdSharedTomcat hst=getHttpdSharedTomcatByShutdownPort();
        if(hst!=null) {
            return
                "Shutdown port for Multi-Site Tomcat JVM version "
                + hst.getHttpdTomcatVersion().getTechnologyVersion(getService().getConnector()).getVersion()
                + " in "
                + hst.getInstallDirectory()
            ;
        }

        HttpdTomcatStdSite htss=getHttpdTomcatStdSiteByShutdownPort();
        if(htss!=null) {
            return
                "Shutdown port for Single-Site Tomcat JVM version "
                + htss.getHttpdTomcatSite().getHttpdTomcatVersion().getTechnologyVersion(getService().getConnector()).getVersion()
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
                + hjs.getHttpdJBossVersion().getTechnologyVersion(getService().getConnector()).getVersion()
                + " in "
                + hjs.getHttpdTomcatSite().getHttpdSite().getInstallDirectory()
            ;
        }

        HttpdJBossSite hjbs=getHttpdJBossSiteByWebserverPort();
        if(hjbs!=null) {
            return
                "Webserver port for JBoss version "
                + hjbs.getHttpdJBossVersion().getTechnologyVersion(getService().getConnector()).getVersion()
                + " in "
                + hjbs.getHttpdTomcatSite().getHttpdSite().getInstallDirectory()
            ;
        }

        hjbs=getHttpdJBossSiteByRMIPort();
        if(hjbs!=null) {
            return
                "RMI port for JBoss version "
                + hjbs.getHttpdJBossVersion().getTechnologyVersion(getService().getConnector()).getVersion()
                + " in "
                + hjbs.getHttpdTomcatSite().getHttpdSite().getInstallDirectory()
            ;
        }

        hjbs=getHttpdJBossSiteByHypersonicPort();
        if(hjbs!=null) {
            return
                "Hypersonic port for JBoss version "
                + hjbs.getHttpdJBossVersion().getTechnologyVersion(getService().getConnector()).getVersion()
                + " in "
                + hjbs.getHttpdTomcatSite().getHttpdSite().getInstallDirectory()
            ;
        }

        hjbs=getHttpdJBossSiteByJMXPort();
        if(hjbs!=null) {
            return
                "JMX port for JBoss version "
                + hjbs.getHttpdJBossVersion().getTechnologyVersion(getService().getConnector()).getVersion()
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
    */
    /**
     * Encodes the parameters.  Will not return null.
     */
    /* TODO
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

    public List<CannotRemoveReason> getCannotRemoveReasons(Locale userLocale) throws IOException, SQLException {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();

        AOServConnector conn=getService().getConnector();

        // Must be able to access business
        BusinessServer bs = getBusinessServer();
        if(bs==null) reasons.add(new CannotRemoveReason<Business>("Unable to access business_server: "+business_server));
        else if(bs.getBusiness()==null) reasons.add(new CannotRemoveReason<Business>("Unable to access business: "+bs.accounting));

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
            if(equals(ms.getNetBind())) reasons.add(new CannotRemoveReason<MySQLServer>("Used for MySQL server "+ms.getName()+" on "+ms.getAoServerResource().getAoServer().getHostname(), ms));
        }

        // postgres_servers
        for(PostgresServer ps : conn.getPostgresServers().getRows()) {
            if(equals(ps.getNetBind())) reasons.add(new CannotRemoveReason<PostgresServer>("Used for PostgreSQL server "+ps.getName()+" on "+ps.getAOServer().getHostname(), ps));
        }

        return reasons;
    }

    public void remove() throws IOException, SQLException {
        getService().getConnector().requestUpdateIL(
            true,
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.NET_BINDS,
            pkey
        );
    }

    public void setMonitoringEnabled(boolean monitoring_enabled) throws IOException, SQLException {
        getService().getConnector().requestUpdateIL(
            true,
            AOServProtocol.CommandID.SET_NET_BIND_MONITORING,
            pkey,
            monitoring_enabled
        );
    }

    public void setOpenFirewall(boolean open_firewall) throws IOException, SQLException {
        getService().getConnector().requestUpdateIL(
            true,
            AOServProtocol.CommandID.SET_NET_BIND_OPEN_FIREWALL,
            pkey,
            open_firewall
        );
    }
     */
    // </editor-fold>
}
