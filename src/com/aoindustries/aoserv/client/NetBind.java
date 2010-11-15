/*
 * Copyright 2001-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.StringUtility;
import com.aoindustries.util.UnionSet;
import com.aoindustries.util.WrappedException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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
final public class NetBind
extends AOServObjectIntegerKey
implements
    Comparable<NetBind>,
    DtoFactory<com.aoindustries.aoserv.client.dto.NetBind> /*implements Removable*/ {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private int businessServer;
    final private Integer ipAddress;
    final private NetPort port;
    private String netProtocol;
    private String appProtocol;
    final private boolean openFirewall;
    final private boolean monitoringEnabled;
    private String monitoringParameters;

    public NetBind(
        AOServConnector connector,
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
        super(connector, pkey);
        this.businessServer = businessServer;
        this.ipAddress = ipAddress;
        this.port = port;
        this.netProtocol = netProtocol;
        this.appProtocol = appProtocol;
        this.openFirewall = openFirewall;
        this.monitoringEnabled = monitoringEnabled;
        this.monitoringParameters = monitoringParameters;
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        netProtocol = intern(netProtocol);
        appProtocol = intern(appProtocol);
        monitoringParameters = intern(monitoringParameters);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(NetBind other) {
        try {
            int diff = businessServer==other.businessServer ? 0 : getBusinessServer().compareTo(other.getBusinessServer());
            if(diff!=0) return diff;
            diff = StringUtility.equals(ipAddress, other.ipAddress) ? 0 : AOServObjectUtils.compare(getIpAddress(), other.getIpAddress());
            if(diff!=0) return diff;
            diff = port.compareTo(other.port);
            if(diff!=0) return diff;
            return netProtocol==other.netProtocol ? 0 : getNetProtocol().compareTo(other.getNetProtocol()); // OK - interned
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="pkey", index=IndexType.PRIMARY_KEY, description="a generated pkey")
    public int getPkey() {
        return key;
    }

    static final String COLUMN_BUSINESS_SERVER = "business_server";
    @SchemaColumn(order=1, name=COLUMN_BUSINESS_SERVER, index=IndexType.INDEXED, description="the business and server this bind is on")
    public BusinessServer getBusinessServer() throws RemoteException {
        return getConnector().getBusinessServers().get(businessServer);
    }

    static final String COLUMN_IP_ADDRESS = "ip_address";
    /**
     * Gets the IP address this bind is on or <code>null</code> if should listen to all available
     * addresses on the server.
     */
    @SchemaColumn(order=2, name=COLUMN_IP_ADDRESS, index=IndexType.INDEXED, description="the pkey of the IP address that is bound to")
    public IPAddress getIpAddress() throws RemoteException {
        if(ipAddress==null) return null;
        return getConnector().getIpAddresses().get(ipAddress);
    }

    @SchemaColumn(order=3, name="port", description="the port number that is bound")
    public NetPort getPort() {
        return port;
    }

    static final String COLUMN_NET_PROTOCOL = "net_protocol";
    @SchemaColumn(order=4, name=COLUMN_NET_PROTOCOL, index=IndexType.INDEXED, description="the network protocol (<code>net_protocols</code>)")
    public NetProtocol getNetProtocol() throws RemoteException {
        return getConnector().getNetProtocols().get(netProtocol);
    }

    static final String COLUMN_APP_PROTOCOL = "app_protocol";
    @SchemaColumn(order=5, name=COLUMN_APP_PROTOCOL, index=IndexType.INDEXED, description="the application protocol (<code>protocols</code>)")
    public Protocol getAppProtocol() throws RemoteException {
        return getConnector().getProtocols().get(appProtocol);
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

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public NetBind(AOServConnector connector, com.aoindustries.aoserv.client.dto.NetBind dto) throws ValidationException {
        this(
            connector,
            dto.getPkey(),
            dto.getBusinessServer(),
            dto.getIpAddress(),
            getNetPort(dto.getPort()),
            dto.getNetProtocol(),
            dto.getAppProtocol(),
            dto.isOpenFirewall(),
            dto.isMonitoringEnabled(),
            dto.getMonitoringParameters()
        );
    }

    @Override
    public com.aoindustries.aoserv.client.dto.NetBind getDto() {
        return new com.aoindustries.aoserv.client.dto.NetBind(
            key,
            businessServer,
            ipAddress,
            getDto(port),
            netProtocol,
            appProtocol,
            openFirewall,
            monitoringEnabled,
            monitoringParameters
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionSet<AOServObject> addDependencies(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = super.addDependencies(unionSet);
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getBusinessServer());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getIpAddress());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getNetProtocol());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getAppProtocol());
        return unionSet;
    }

    @Override
    protected UnionSet<AOServObject> addDependentObjects(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = super.addDependentObjects(unionSet);
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getAOServerByDaemonNetBind());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getAOServerByDaemonConnectNetBind());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getAOServerByJilterNetBind());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getBrandByAowebStrutsVncBind());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getMySQLServer());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getNetTcpRedirect());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getPostgresServer());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getPrivateFtpServer());
        // TODO: unionSet = AOServObjectUtils.addDependencySet(unionSet, getEmailSmartHost(),
        // TODO: unionSet = AOServObjectUtils.addDependencySet(unionSet, getHttpdBind(),
        // TODO: unionSet = AOServObjectUtils.addDependencySet(unionSet, getHttpdJBossSiteByJNPPort(),
        // TODO: unionSet = AOServObjectUtils.addDependencySet(unionSet, getHttpdJBossSiteByWebserverPort(),
        // TODO: unionSet = AOServObjectUtils.addDependencySet(unionSet, getHttpdJBossSiteByRMIPort(),
        // TODO: unionSet = AOServObjectUtils.addDependencySet(unionSet, getHttpdJBossSiteByHypersonicPort(),
        // TODO: unionSet = AOServObjectUtils.addDependencySet(unionSet, getHttpdJBossSiteByJMXPort(),
        // TODO: unionSet = AOServObjectUtils.addDependencySet(unionSet, getHttpdSharedTomcatByShutdownPort(),
        // TODO: unionSet = AOServObjectUtils.addDependencySet(unionSet, getHttpdWorker(),
        // TODO: unionSet = AOServObjectUtils.addDependencySet(unionSet, getHttpdTomcatStdSiteByShutdownPort(),
        return unionSet;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() throws RemoteException {
        IPAddress ip = getIpAddress();
        if(ip==null) return "*:"+getPort()+'/'+getNetProtocol();
        String address = ip.getIpAddress().toString();
        if(address.indexOf(':')==-1) return address+':'+getPort()+'/'+getNetProtocol();
        else return '['+address+"]:"+getPort()+'/'+getNetProtocol();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public AOServer getAOServerByDaemonNetBind() throws RemoteException {
        return getConnector().getAoServers().filterUnique(AOServer.COLUMN_DAEMON_BIND, this);
    }

    public AOServer getAOServerByDaemonConnectNetBind() throws RemoteException {
        return getConnector().getAoServers().filterUnique(AOServer.COLUMN_DAEMON_CONNECT_BIND, this);
    }

    public AOServer getAOServerByJilterNetBind() throws RemoteException {
        return getConnector().getAoServers().filterUnique(AOServer.COLUMN_JILTER_BIND, this);
    }

    public Brand getBrandByAowebStrutsVncBind() throws RemoteException {
        return getConnector().getBrands().filterUnique(Brand.COLUMN_AOWEB_STRUTS_VNC_BIND, this);
    }

    public MySQLServer getMySQLServer() throws RemoteException {
        return getConnector().getMysqlServers().filterUnique(MySQLServer.COLUMN_NET_BIND, this);
    }

    public NetTcpRedirect getNetTcpRedirect() throws RemoteException {
        return getConnector().getNetTcpRedirects().filterUnique(NetTcpRedirect.COLUMN_NET_BIND, this);
    }

    public PostgresServer getPostgresServer() throws RemoteException {
        return getConnector().getPostgresServers().filterUnique(PostgresServer.COLUMN_NET_BIND, this);
    }

    public PrivateFtpServer getPrivateFtpServer() throws RemoteException {
        return getConnector().getPrivateFtpServers().filterUnique(PrivateFtpServer.COLUMN_NET_BIND, this);
    }

    /* TODO
    public HttpdBind getHttpdBind() throws IOException, SQLException {
        return getConnector().getHttpdBinds().get(pkey);
    }

    public HttpdJBossSite getHttpdJBossSiteByJNPPort() throws IOException, SQLException {
        return getConnector().getHttpdJBossSites().getHttpdJBossSiteByJNPPort(this);
    }

    public HttpdJBossSite getHttpdJBossSiteByWebserverPort() throws IOException, SQLException {
        return getConnector().getHttpdJBossSites().getHttpdJBossSiteByWebserverPort(this);
    }

    public HttpdJBossSite getHttpdJBossSiteByRMIPort() throws IOException, SQLException {
        return getConnector().getHttpdJBossSites().getHttpdJBossSiteByRMIPort(this);
    }

    public HttpdJBossSite getHttpdJBossSiteByHypersonicPort() throws IOException, SQLException {
        return getConnector().getHttpdJBossSites().getHttpdJBossSiteByHypersonicPort(this);
    }

    public HttpdJBossSite getHttpdJBossSiteByJMXPort() throws IOException, SQLException {
        return getConnector().getHttpdJBossSites().getHttpdJBossSiteByJMXPort(this);
    }

    public HttpdWorker getHttpdWorker() throws IOException, SQLException {
        return getConnector().getHttpdWorkers().getHttpdWorker(this);
    }

    public HttpdSharedTomcat getHttpdSharedTomcatByShutdownPort() throws SQLException, IOException {
        return getConnector().getHttpdSharedTomcats().getHttpdSharedTomcatByShutdownPort(this);
    }

    public HttpdTomcatStdSite getHttpdTomcatStdSiteByShutdownPort() throws IOException, SQLException {
        return getConnector().getHttpdTomcatStdSites().getHttpdTomcatStdSiteByShutdownPort(this);
    }

    public EmailSmtpSmartHost getEmailSmartHost() throws IOException, SQLException {
        return getConnector().getEmailSmtpSmartHosts().getUniqueRow(EmailSmtpSmartHost.COLUMN_NET_BIND, pkey);
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
        if(ps!=null) return "PostgreSQL version "+ps.getPostgresVersion().getTechnologyVersion(getConnector()).getVersion()+" in "+ps.getDataDirectory();

        HttpdWorker hw=getHttpdWorker();
        if(hw!=null) {
            HttpdSharedTomcat hst=hw.getHttpdSharedTomcat();
            if(hst!=null) {
                return
                    hw.getHttpdJKProtocol(getConnector()).getProtocol(getConnector()).getProtocol()
                    + " connector for Multi-Site Tomcat JVM version "
                    + hst.getHttpdTomcatVersion().getTechnologyVersion(getConnector()).getVersion()
                    + " in "
                    + hst.getInstallDirectory()
                ;
            }
            HttpdTomcatSite hts=hw.getHttpdTomcatSite();
            if(hts!=null) {
                return
                    hw.getHttpdJKProtocol(getConnector()).getProtocol(getConnector()).getProtocol()
                    + " connector for Single-Site Tomcat JVM version "
                    + hts.getHttpdTomcatVersion().getTechnologyVersion(getConnector()).getVersion()
                    + " in "
                    + hts.getHttpdSite().getInstallDirectory()
                ;
            }
        }

        HttpdSharedTomcat hst=getHttpdSharedTomcatByShutdownPort();
        if(hst!=null) {
            return
                "Shutdown port for Multi-Site Tomcat JVM version "
                + hst.getHttpdTomcatVersion().getTechnologyVersion(getConnector()).getVersion()
                + " in "
                + hst.getInstallDirectory()
            ;
        }

        HttpdTomcatStdSite htss=getHttpdTomcatStdSiteByShutdownPort();
        if(htss!=null) {
            return
                "Shutdown port for Single-Site Tomcat JVM version "
                + htss.getHttpdTomcatSite().getHttpdTomcatVersion().getTechnologyVersion(getConnector()).getVersion()
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
                + hjs.getHttpdJBossVersion().getTechnologyVersion(getConnector()).getVersion()
                + " in "
                + hjs.getHttpdTomcatSite().getHttpdSite().getInstallDirectory()
            ;
        }

        HttpdJBossSite hjbs=getHttpdJBossSiteByWebserverPort();
        if(hjbs!=null) {
            return
                "Webserver port for JBoss version "
                + hjbs.getHttpdJBossVersion().getTechnologyVersion(getConnector()).getVersion()
                + " in "
                + hjbs.getHttpdTomcatSite().getHttpdSite().getInstallDirectory()
            ;
        }

        hjbs=getHttpdJBossSiteByRMIPort();
        if(hjbs!=null) {
            return
                "RMI port for JBoss version "
                + hjbs.getHttpdJBossVersion().getTechnologyVersion(getConnector()).getVersion()
                + " in "
                + hjbs.getHttpdTomcatSite().getHttpdSite().getInstallDirectory()
            ;
        }

        hjbs=getHttpdJBossSiteByHypersonicPort();
        if(hjbs!=null) {
            return
                "Hypersonic port for JBoss version "
                + hjbs.getHttpdJBossVersion().getTechnologyVersion(getConnector()).getVersion()
                + " in "
                + hjbs.getHttpdTomcatSite().getHttpdSite().getInstallDirectory()
            ;
        }

        hjbs=getHttpdJBossSiteByJMXPort();
        if(hjbs!=null) {
            return
                "JMX port for JBoss version "
                + hjbs.getHttpdJBossVersion().getTechnologyVersion(getConnector()).getVersion()
                + " in "
                + hjbs.getHttpdTomcatSite().getHttpdSite().getInstallDirectory()
            ;
        }

        NetTcpRedirect ntr=getNetTcpRedirect();
        if(ntr!=null) return "Port redirected to "+ntr.getDestinationHost()+':'+ntr.getDestinationPort().getPort();

        PrivateFtpServer pfs=getPrivateFtpServer();
        if(pfs!=null) return "Private FTP server in "+pfs.getLinuxServerAccount().getHome();

        return null;
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() throws IOException, SQLException {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();

        AOServConnector conn=getConnector();

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
            if(equals(ms.getNetBind())) reasons.add(new CannotRemoveReason<MySQLServer>("Used for MySQL server "+ms.getName()+" on "+ms.getAoServer().getHostname(), ms));
        }

        // postgres_servers
        for(PostgresServer ps : conn.getPostgresServers().getRows()) {
            if(equals(ps.getNetBind())) reasons.add(new CannotRemoveReason<PostgresServer>("Used for PostgreSQL server "+ps.getName()+" on "+ps.getAOServer().getHostname(), ps));
        }

        return reasons;
    }

    public void remove() throws IOException, SQLException {
        getConnector().requestUpdateIL(
            true,
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.NET_BINDS,
            pkey
        );
    }

    public void setMonitoringEnabled(boolean monitoring_enabled) throws IOException, SQLException {
        getConnector().requestUpdateIL(
            true,
            AOServProtocol.CommandID.SET_NET_BIND_MONITORING,
            pkey,
            monitoring_enabled
        );
    }

    public void setOpenFirewall(boolean open_firewall) throws IOException, SQLException {
        getConnector().requestUpdateIL(
            true,
            AOServProtocol.CommandID.SET_NET_BIND_OPEN_FIREWALL,
            pkey,
            open_firewall
        );
    }
     */
    // </editor-fold>
}
