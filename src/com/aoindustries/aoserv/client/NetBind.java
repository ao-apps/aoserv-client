package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.validator.NetPort;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.StringUtility;
import com.aoindustries.util.WrappedException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.rmi.RemoteException;
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
final public class NetBind extends AOServObjectIntegerKey<NetBind> implements BeanFactory<com.aoindustries.aoserv.client.beans.NetBind> /*implements Removable*/ {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private int businessServer;
    final private Integer ipAddress;
    final private NetPort port;
    final private String netProtocol;
    final private String appProtocol;
    final private boolean openFirewall;
    final private boolean monitoringEnabled;
    final private String monitoringParameters;

    public NetBind(
        NetBindService<?,?> service,
        int pkey,
        int businessServer,
        Integer ipAddress,
        NetPort port,
        String netProtocol,
        String appProtocol,
        boolean openFirewall,
        boolean monitoringEnabled,
        String monitoringParameters
    ) {
        super(service, pkey);
        this.businessServer = businessServer;
        this.ipAddress = ipAddress;
        this.port = port;
        this.netProtocol = netProtocol.intern();
        this.appProtocol = appProtocol.intern();
        this.openFirewall = openFirewall;
        this.monitoringEnabled = monitoringEnabled;
        this.monitoringParameters = monitoringParameters;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(NetBind other) throws RemoteException {
        int diff = businessServer==other.businessServer ? 0 : getBusinessServer().compareTo(other.getBusinessServer());
        if(diff!=0) return diff;
        diff = StringUtility.equals(ipAddress, other.ipAddress) ? 0 : AOServObjectUtils.compare(getIpAddress(), other.getIpAddress());
        if(diff!=0) return diff;
        diff = port.compareTo(other.port);
        if(diff!=0) return diff;
        return netProtocol.equals(other.netProtocol) ? 0 : getNetProtocol().compareTo(other.getNetProtocol());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="pkey", index=IndexType.PRIMARY_KEY, description="a generated pkey")
    public int getPkey() {
        return key;
    }

    /**
     * May be filtered.
     */
    static final String COLUMN_BUSINESS_SERVER = "business_server";
    @SchemaColumn(order=1, name=COLUMN_BUSINESS_SERVER, index=IndexType.INDEXED, description="the business and server this bind is on")
    public BusinessServer getBusinessServer() throws RemoteException {
        return getService().getConnector().getBusinessServers().get(businessServer);
    }

    /**
     * Gets the IP address this bind is on or <code>null</code> if should listen to all available
     * addresses on the server.
     */
    static final String COLUMN_IP_ADDRESS = "ip_address";
    @SchemaColumn(order=2, name=COLUMN_IP_ADDRESS, index=IndexType.INDEXED, description="the pkey of the IP address that is bound to")
    public IPAddress getIpAddress() throws RemoteException {
        if(ipAddress==null) return null;
        return getService().getConnector().getIpAddresses().get(ipAddress);
    }

    @SchemaColumn(order=3, name="port", description="the port number that is bound")
    public NetPort getPort() {
        return port;
    }

    static final String COLUMN_NET_PROTOCOL = "net_protocol";
    @SchemaColumn(order=4, name=COLUMN_NET_PROTOCOL, index=IndexType.INDEXED, description="the network protocol (<code>net_protocols</code>)")
    public NetProtocol getNetProtocol() throws RemoteException {
        return getService().getConnector().getNetProtocols().get(netProtocol);
    }

    static final String COLUMN_APP_PROTOCOL = "app_protocol";
    @SchemaColumn(order=5, name=COLUMN_APP_PROTOCOL, index=IndexType.INDEXED, description="the application protocol (<code>protocols</code>)")
    public Protocol getAppProtocol() throws RemoteException {
        return getService().getConnector().getProtocols().get(appProtocol);
    }

    @SchemaColumn(order=6, name="open_firewall", description="flags if the firewall should be opened for this port")
    public boolean isFirewallOpen() {
        return openFirewall;
    }

    @SchemaColumn(order=7, name="monitoring_enabled", description="turns on monitoring of the port")
    public boolean isMonitoringEnabled() {
        return monitoringEnabled;
    }

    /**
     * Gets the unmodifiable map of parameters for this bind.
     */
    @SchemaColumn(order=8, name="monitoring_parameters", description="the URL-encoded name=value pairs of monitoring parameters")
    public Map<String,String> getMonitoringParameters() {
        String myParamString = monitoringParameters;
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

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.NetBind getBean() {
        return new com.aoindustries.aoserv.client.beans.NetBind(key, businessServer, ipAddress, port.getBean(), netProtocol, appProtocol, openFirewall, monitoringEnabled, monitoringParameters);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependencies() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getBusinessServer(),
            getIpAddress(),
            getNetProtocol(),
            getAppProtocol()
        );
    }

    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getAOServerByDaemonNetBind(),
            getAOServerByDaemonConnectNetBind(),
            getAOServerByJilterNetBind(),
            getMySQLServer(),
            getPostgresServer()
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
            // TODO: getPrivateFTPServer(),
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public AOServer getAOServerByDaemonNetBind() throws RemoteException {
        return getService().getConnector().getAoServers().filterUnique(AOServer.COLUMN_DAEMON_BIND, this);
    }

    public AOServer getAOServerByDaemonConnectNetBind() throws RemoteException {
        return getService().getConnector().getAoServers().filterUnique(AOServer.COLUMN_DAEMON_CONNECT_BIND, this);
    }

    public AOServer getAOServerByJilterNetBind() throws RemoteException {
        return getService().getConnector().getAoServers().filterUnique(AOServer.COLUMN_JILTER_BIND, this);
    }

    public MySQLServer getMySQLServer() throws RemoteException {
        return getService().getConnector().getMysqlServers().filterUnique(MySQLServer.COLUMN_NET_BIND, this);
    }

    public PostgresServer getPostgresServer() throws RemoteException {
        return getService().getConnector().getPostgresServers().filterUnique(PostgresServer.COLUMN_NET_BIND, this);
    }

    /* TODO
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

    public PrivateFTPServer getPrivateFTPServer() throws IOException, SQLException {
        return getService().getConnector().getPrivateFTPServers().get(pkey);
    }

    public Brand getBrandByAowebStrutsVncBind() throws IOException, SQLException {
        return getService().getConnector().getBrands().getUniqueRow(Brand.COLUMN_AOWEB_STRUTS_VNC_BIND, pkey);
    }

    public EmailSmtpSmartHost getEmailSmartHost() throws IOException, SQLException {
        return getService().getConnector().getEmailSmtpSmartHosts().getUniqueRow(EmailSmtpSmartHost.COLUMN_NET_BIND, pkey);
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
