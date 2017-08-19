/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aoserv-client.
 *
 * aoserv-client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aoserv-client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aoserv-client.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.aoserv.client.validator.FirewalldZoneName;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.net.EmptyParameters;
import com.aoindustries.net.HttpParameters;
import com.aoindustries.net.HttpParametersMap;
import com.aoindustries.net.Port;
import com.aoindustries.net.UnmodifiableHttpParameters;
import com.aoindustries.util.IntList;
import com.aoindustries.util.WrappedException;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
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

	AccountingCode packageName;
	int server;
	int ip_address;
	Port port;
	String app_protocol;
	private boolean monitoring_enabled;
	private String monitoring_parameters;

	/**
	 * For compatibility with older clients only.
	 */
	private boolean open_firewall;

	public Protocol getAppProtocol() throws SQLException, IOException {
		Protocol obj=table.connector.getProtocols().get(app_protocol);
		if(obj==null) throw new SQLException("Unable to find Protocol: "+app_protocol);
		return obj;
	}

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_PACKAGE: return packageName;
			case COLUMN_SERVER: return server;
			case COLUMN_IP_ADDRESS: return ip_address;
			case 4: return port;
			case 5: return app_protocol;
			case 6: return monitoring_enabled;
			case 7: return monitoring_parameters;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	@Override
	String toStringImpl() throws IOException, SQLException {
		return getServer().toStringImpl() + "|" + getIPAddress().toStringImpl() + "|" + getPort();
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
		if(ntr!=null) return "Port redirected to "+ntr.getDestinationHost().toBracketedString()+':'+ntr.getDestinationPort().getPort();

		PrivateFTPServer pfs=getPrivateFTPServer();
		if(pfs!=null) return "Private FTP server in "+pfs.getLinuxServerAccount().getHome();

		return null;
	}

	/**
	 * A net_bind is disabled when all Disablable uses of it are disabled.
	 * If there are no Disablable uses, it is considered enabled.
	 * 
	 * @see  Disablable
	 */
	public boolean isDisabled() throws SQLException, IOException {
		boolean foundDisablable = false;
		HttpdWorker hw = getHttpdWorker();
		if(hw != null) {
			HttpdSharedTomcat hst = hw.getHttpdSharedTomcat();
			if(hst != null) {
				// Must also have at least one enabled site
				boolean hasEnabledSite = false;
				for(HttpdTomcatSharedSite htss : hst.getHttpdTomcatSharedSites()) {
					if(!htss.getHttpdTomcatSite().getHttpdSite().isDisabled()) {
						hasEnabledSite = true;
						break;
					}
				}
				if(!hst.isDisabled() && hasEnabledSite) return false;
				foundDisablable = true;
			}
			HttpdTomcatSite hts = hw.getHttpdTomcatSite();
			if(hts != null) {
				if(!hts.getHttpdSite().isDisabled()) return false;
				foundDisablable = true;
			}
		}

		HttpdSharedTomcat hst = getHttpdSharedTomcatByShutdownPort();
		if(hst != null) {
			// Must also have at least one enabled site
			boolean hasEnabledSite = false;
			for(HttpdTomcatSharedSite htss : hst.getHttpdTomcatSharedSites()) {
				if(!htss.getHttpdTomcatSite().getHttpdSite().isDisabled()) {
					hasEnabledSite = true;
					break;
				}
			}
			if(!hst.isDisabled() && hasEnabledSite) return false;
			foundDisablable = true;
		}

		HttpdTomcatStdSite htss=getHttpdTomcatStdSiteByShutdownPort();
		if(htss!=null) {
			if(!htss.getHttpdTomcatSite().getHttpdSite().isDisabled()) return false;
			foundDisablable = true;
		}

		HttpdJBossSite hjbs=getHttpdJBossSiteByJNPPort();
		if(hjbs!=null) {
			if(!hjbs.getHttpdTomcatSite().getHttpdSite().isDisabled()) return false;
			foundDisablable = true;
		}

		hjbs=getHttpdJBossSiteByWebserverPort();
		if(hjbs!=null) {
			if(!hjbs.getHttpdTomcatSite().getHttpdSite().isDisabled()) return false;
			foundDisablable = true;
		}

		hjbs=getHttpdJBossSiteByRMIPort();
		if(hjbs!=null) {
			if(!hjbs.getHttpdTomcatSite().getHttpdSite().isDisabled()) return false;
			foundDisablable = true;
		}

		hjbs=getHttpdJBossSiteByHypersonicPort();
		if(hjbs!=null) {
			if(!hjbs.getHttpdTomcatSite().getHttpdSite().isDisabled()) return false;
			foundDisablable = true;
		}

		hjbs=getHttpdJBossSiteByJMXPort();
		if(hjbs!=null) {
			if(!hjbs.getHttpdTomcatSite().getHttpdSite().isDisabled()) return false;
			foundDisablable = true;
		}

		return foundDisablable;
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

	public List<NetBindFirewalldZone> getNetBindFirewalldZones() throws IOException, SQLException {
		return table.connector.getNetBindFirewalldZones().getNetBindFirewalldZones(this);
	}

	public List<FirewalldZone> getFirewalldZones() throws IOException, SQLException {
		List<NetBindFirewalldZone> nbfzs = getNetBindFirewalldZones();
		List<FirewalldZone> fzs = new ArrayList<>(nbfzs.size());
		for(NetBindFirewalldZone nbfz : nbfzs) {
			fzs.add(nbfz.getFirewalldZone());
		}
		return fzs;
	}

	public Set<FirewalldZoneName> getFirewalldZoneNames() throws IOException, SQLException {
		List<NetBindFirewalldZone> nbfzs = getNetBindFirewalldZones();
		Set<FirewalldZoneName> fzns = new LinkedHashSet<>(nbfzs.size()*4/3+1);
		for(NetBindFirewalldZone nbfz : nbfzs) {
			fzns.add(nbfz.getFirewalldZone().getName());
		}
		return fzns;
	}

	public IPAddress getIPAddress() throws SQLException, IOException {
		IPAddress obj=table.connector.getIpAddresses().get(ip_address);
		if(obj==null) throw new SQLException("Unable to find IPAddress: "+ip_address);
		return obj;
	}

	public boolean isMonitoringEnabled() {
		return monitoring_enabled;
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

	public Port getPort() {
		return port;
	}

	public Server getServer() throws SQLException, IOException {
		Server obj=table.connector.getServers().get(server);
		if(obj==null) throw new SQLException("Unable to find Server: "+server);
		return obj;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.NET_BINDS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			pkey = result.getInt(1);
			packageName = AccountingCode.valueOf(result.getString(2));
			server = result.getInt(3);
			ip_address = result.getInt(4);
			port = Port.valueOf(
				result.getInt(5),
				com.aoindustries.net.Protocol.valueOf(result.getString(6).toUpperCase(Locale.ROOT))
			);
			app_protocol = result.getString(7);
			monitoring_enabled = result.getBoolean(8);
			monitoring_parameters = result.getString(9);
			open_firewall = result.getBoolean(10);
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	/**
	 * Encodes the parameters in UTF-8.  Will not return null.
	 */
	public static String encodeParameters(HttpParameters monitoringParameters) {
		try {
			StringBuilder SB = new StringBuilder();
			for(Map.Entry<String,List<String>> entry : monitoringParameters.getParameterMap().entrySet()) {
				String name = entry.getKey();
				for(String value : entry.getValue()) {
					if(SB.length()>0) SB.append('&');
					SB.append(URLEncoder.encode(name, "UTF-8")).append('=').append(URLEncoder.encode(value, "UTF-8"));
				}
			}
			return SB.toString();
		} catch(UnsupportedEncodingException err) {
			throw new WrappedException(err);
		}
	}

	/**
	 * Decodes the parameters in UTF-8.
	 */
	public static HttpParameters decodeParameters(String monitoringParameters) {
		if(monitoringParameters==null) {
			return EmptyParameters.getInstance();
		} else {
			try {
				return new HttpParametersMap(monitoringParameters, "UTF-8");
			} catch(UnsupportedEncodingException e) {
				throw new AssertionError("UTF-8 should existing on all platforms", e);
			}
			/*
			try {
				List<String> nameValues = StringUtility.splitString(monitoringParameters, '&');
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
			}*/
		}
	}

	private static final ConcurrentMap<String,HttpParameters> getMonitoringParametersCache = new ConcurrentHashMap<>();

	/**
	 * Gets the unmodifiable map of parameters for this bind.
	 */
	public HttpParameters getMonitoringParameters() {
		String myParamString = monitoring_parameters;
		if(myParamString==null) {
			return EmptyParameters.getInstance();
		} else {
			HttpParameters params = getMonitoringParametersCache.get(myParamString);
			if(params==null) {
				params = UnmodifiableHttpParameters.wrap(decodeParameters(myParamString));
				HttpParameters previous = getMonitoringParametersCache.putIfAbsent(myParamString, params);
				if(previous!=null) params = previous;
			}
			return params;
		}
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			pkey = in.readCompressedInt();
			packageName = AccountingCode.valueOf(in.readUTF()).intern();
			server = in.readCompressedInt();
			ip_address = in.readCompressedInt();
			port = Port.valueOf(
				in.readCompressedInt(),
				in.readEnum(com.aoindustries.net.Protocol.class)
			);
			app_protocol = in.readUTF().intern();
			monitoring_enabled = in.readBoolean();
			monitoring_parameters = in.readNullUTF();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public List<CannotRemoveReason<?>> getCannotRemoveReasons() throws IOException, SQLException {
		List<CannotRemoveReason<?>> reasons=new ArrayList<>();

		AOServConnector conn=table.connector;

		// Must be able to access package
		if(getPackage()==null) reasons.add(new CannotRemoveReason<Package>("Unable to access package: "+packageName));

		// ao_servers
		for(AOServer ao : conn.getAoServers().getRows()) {
			if(
				pkey==ao.daemon_bind
				|| pkey==ao.daemon_connect_bind
			) reasons.add(new CannotRemoveReason<>("Used as aoserv-daemon port for server: "+ao.getHostname(), ao));
			if(pkey==ao.jilter_bind) reasons.add(new CannotRemoveReason<>("Used as aoserv-daemon jilter port for server: "+ao.getHostname(), ao));
		}

		// httpd_binds
		for(HttpdBind hb : conn.getHttpdBinds().getRows()) {
			if(equals(hb.getNetBind())) {
				HttpdServer hs=hb.getHttpdServer();
				reasons.add(new CannotRemoveReason<>("Used by Apache server #"+hs.getNumber()+" on "+hs.getAOServer().getHostname(), hb));
			}
		}

		// httpd_jboss_sites
		for(HttpdJBossSite hjb : conn.getHttpdJBossSites().getRows()) {
			HttpdSite hs=hjb.getHttpdTomcatSite().getHttpdSite();
			if(equals(hjb.getJnpBind())) reasons.add(new CannotRemoveReason<>("Used as JNP port for JBoss site "+hs.getInstallDirectory()+" on "+hs.getAOServer().getHostname(), hjb));
			if(equals(hjb.getWebserverBind())) reasons.add(new CannotRemoveReason<>("Used as Webserver port for JBoss site "+hs.getInstallDirectory()+" on "+hs.getAOServer().getHostname(), hjb));
			if(equals(hjb.getRmiBind())) reasons.add(new CannotRemoveReason<>("Used as RMI port for JBoss site "+hs.getInstallDirectory()+" on "+hs.getAOServer().getHostname(), hjb));
			if(equals(hjb.getHypersonicBind())) reasons.add(new CannotRemoveReason<>("Used as Hypersonic port for JBoss site "+hs.getInstallDirectory()+" on "+hs.getAOServer().getHostname(), hjb));
			if(equals(hjb.getJmxBind())) reasons.add(new CannotRemoveReason<>("Used as JMX port for JBoss site "+hs.getInstallDirectory()+" on "+hs.getAOServer().getHostname(), hjb));
		}

		// httpd_shared_tomcats
		for(HttpdSharedTomcat hst : conn.getHttpdSharedTomcats().getRows()) {
			if(equals(hst.getTomcat4ShutdownPort())) reasons.add(new CannotRemoveReason<>("Used as shutdown port for Multi-Site Tomcat JVM "+hst.getInstallDirectory()+" on "+hst.getAOServer().getHostname(), hst));
		}

		// httpd_tomcat_std_sites
		for(HttpdTomcatStdSite hts : conn.getHttpdTomcatStdSites().getRows()) {
			HttpdSite hs=hts.getHttpdTomcatSite().getHttpdSite();
			if(equals(hts.getTomcat4ShutdownPort())) reasons.add(new CannotRemoveReason<>("Used as shutdown port for Single-Site Tomcat JVM "+hs.getInstallDirectory()+" on "+hs.getAOServer().getHostname(), hts));
		}

		// httpd_workers
		for(HttpdWorker hw : conn.getHttpdWorkers().getRows()) {
			if(equals(hw.getNetBind())) {
				HttpdSharedTomcat hst=hw.getHttpdSharedTomcat();
				if(hst!=null) reasons.add(new CannotRemoveReason<>("Used as mod_jk worker for Multi-Site Tomcat JVM "+hst.getInstallDirectory()+" on "+hst.getAOServer().getHostname(), hst));

				HttpdTomcatSite hts=hw.getHttpdTomcatSite();
				if(hts!=null) {
					HttpdSite hs=hts.getHttpdSite();
					reasons.add(new CannotRemoveReason<>("Used as mod_jk worker for Tomcat JVM "+hs.getInstallDirectory()+" on "+hs.getAOServer().getHostname(), hts));
				}
			}
		}

		// mysql_servers
		for(MySQLServer ms : conn.getMysqlServers().getRows()) {
			if(equals(ms.getNetBind())) reasons.add(new CannotRemoveReason<>("Used for MySQL server "+ms.getName()+" on "+ms.getAOServer().getHostname(), ms));
		}

		// postgres_servers
		for(PostgresServer ps : conn.getPostgresServers().getRows()) {
			if(equals(ps.getNetBind())) reasons.add(new CannotRemoveReason<>("Used for PostgreSQL server "+ps.getName()+" on "+ps.getAOServer().getHostname(), ps));
		}

		return reasons;
	}

	@Override
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

	public void setFirewalldZones(final Set<FirewalldZoneName> firewalldZones) throws IOException, SQLException {
		table.connector.requestUpdate(
			true,
			AOServProtocol.CommandID.SET_NET_BIND_FIREWALLD_ZONES,
			new AOServConnector.UpdateRequest() {
				IntList invalidateList;

				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(pkey);
					int size = firewalldZones.size();
					out.writeCompressedInt(size);
					int count = 0;
					for(FirewalldZoneName firewalldZone : firewalldZones) {
						out.writeUTF(firewalldZone.toString());
						count++;
					}
					if(size != count) throw new ConcurrentModificationException();
				}

				@Override
				public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
					int code = in.readByte();
					if(code == AOServProtocol.DONE) {
						invalidateList = AOServConnector.readInvalidateList(in);
					} else {
						AOServProtocol.checkResult(code, in);
						throw new IOException("Unexpected response code: " + code);
					}
				}

				@Override
				public void afterRelease() {
					table.connector.tablesUpdated(invalidateList);
				}
			}
		);
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeUTF(packageName.toString());
		out.writeCompressedInt(server);
		out.writeCompressedInt(ip_address);
		out.writeCompressedInt(port.getPort());
		if(version.compareTo(AOServProtocol.Version.VERSION_1_80_0) < 0) {
			out.writeUTF(port.getProtocol().name().toLowerCase(Locale.ROOT));
		} else {
			out.writeEnum(port.getProtocol());
		}
		out.writeUTF(app_protocol);
		if(version.compareTo(AOServProtocol.Version.VERSION_1_80_2) <= 0) {
			out.writeBoolean(open_firewall);
		}
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
