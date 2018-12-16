/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2014, 2015, 2016, 2017, 2018  AO Industries, Inc.
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

import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.account.AccountHost;
import com.aoindustries.aoserv.client.account.Administrator;
import com.aoindustries.aoserv.client.account.DisableLog;
import com.aoindustries.aoserv.client.account.Profile;
import com.aoindustries.aoserv.client.account.Username;
import com.aoindustries.aoserv.client.backup.BackupPartition;
import com.aoindustries.aoserv.client.backup.FileReplication;
import com.aoindustries.aoserv.client.backup.FileReplicationSetting;
import com.aoindustries.aoserv.client.billing.NoticeLog;
import com.aoindustries.aoserv.client.billing.NoticeType;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.billing.PackageDefinition;
import com.aoindustries.aoserv.client.billing.Transaction;
import com.aoindustries.aoserv.client.billing.TransactionType;
import com.aoindustries.aoserv.client.distribution.Architecture;
import com.aoindustries.aoserv.client.distribution.OperatingSystem;
import com.aoindustries.aoserv.client.distribution.OperatingSystemVersion;
import com.aoindustries.aoserv.client.distribution.Software;
import com.aoindustries.aoserv.client.distribution.SoftwareVersion;
import com.aoindustries.aoserv.client.dns.Record;
import com.aoindustries.aoserv.client.dns.RecordType;
import com.aoindustries.aoserv.client.dns.Zone;
import com.aoindustries.aoserv.client.email.Address;
import com.aoindustries.aoserv.client.email.BlackholeAddress;
import com.aoindustries.aoserv.client.email.Domain;
import com.aoindustries.aoserv.client.email.Forwarding;
import com.aoindustries.aoserv.client.email.InboxAddress;
import com.aoindustries.aoserv.client.email.InboxAttributes;
import com.aoindustries.aoserv.client.email.ListAddress;
import com.aoindustries.aoserv.client.email.MajordomoList;
import com.aoindustries.aoserv.client.email.MajordomoServer;
import com.aoindustries.aoserv.client.email.MajordomoVersion;
import com.aoindustries.aoserv.client.email.Pipe;
import com.aoindustries.aoserv.client.email.PipeAddress;
import com.aoindustries.aoserv.client.email.SmtpRelay;
import com.aoindustries.aoserv.client.email.SmtpRelayType;
import com.aoindustries.aoserv.client.email.SpamAssassinMode;
import com.aoindustries.aoserv.client.ftp.GuestUser;
import com.aoindustries.aoserv.client.infrastructure.ServerFarm;
import com.aoindustries.aoserv.client.infrastructure.VirtualDisk;
import com.aoindustries.aoserv.client.infrastructure.VirtualServer;
import com.aoindustries.aoserv.client.linux.Group;
import com.aoindustries.aoserv.client.linux.GroupServer;
import com.aoindustries.aoserv.client.linux.GroupType;
import com.aoindustries.aoserv.client.linux.GroupUser;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.linux.Shell;
import com.aoindustries.aoserv.client.linux.User;
import com.aoindustries.aoserv.client.linux.UserServer;
import com.aoindustries.aoserv.client.linux.UserType;
import com.aoindustries.aoserv.client.mysql.Database;
import com.aoindustries.aoserv.client.mysql.DatabaseUser;
import com.aoindustries.aoserv.client.net.AppProtocol;
import com.aoindustries.aoserv.client.net.Bind;
import com.aoindustries.aoserv.client.net.Device;
import com.aoindustries.aoserv.client.net.Host;
import com.aoindustries.aoserv.client.net.IpAddress;
import com.aoindustries.aoserv.client.password.PasswordChecker;
import com.aoindustries.aoserv.client.password.PasswordGenerator;
import com.aoindustries.aoserv.client.password.PasswordProtected;
import com.aoindustries.aoserv.client.payment.CreditCard;
import com.aoindustries.aoserv.client.payment.PaymentType;
import com.aoindustries.aoserv.client.payment.Processor;
import com.aoindustries.aoserv.client.pki.Certificate;
import com.aoindustries.aoserv.client.postgresql.Encoding;
import com.aoindustries.aoserv.client.postgresql.Version;
import com.aoindustries.aoserv.client.reseller.Category;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.scm.CvsRepository;
import com.aoindustries.aoserv.client.ticket.Language;
import com.aoindustries.aoserv.client.ticket.Priority;
import com.aoindustries.aoserv.client.ticket.TicketType;
import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.aoserv.client.validator.FirewalldZoneName;
import com.aoindustries.aoserv.client.validator.Gecos;
import com.aoindustries.aoserv.client.validator.GroupId;
import com.aoindustries.aoserv.client.validator.HashedPassword;
import com.aoindustries.aoserv.client.validator.MySQLDatabaseName;
import com.aoindustries.aoserv.client.validator.MySQLServerName;
import com.aoindustries.aoserv.client.validator.MySQLUserId;
import com.aoindustries.aoserv.client.validator.PostgresDatabaseName;
import com.aoindustries.aoserv.client.validator.PostgresServerName;
import com.aoindustries.aoserv.client.validator.PostgresUserId;
import com.aoindustries.aoserv.client.validator.UnixPath;
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.aoserv.client.web.HttpdServer;
import com.aoindustries.aoserv.client.web.Location;
import com.aoindustries.aoserv.client.web.Site;
import com.aoindustries.aoserv.client.web.VirtualHost;
import com.aoindustries.aoserv.client.web.VirtualHostName;
import com.aoindustries.aoserv.client.web.tomcat.Context;
import com.aoindustries.aoserv.client.web.tomcat.ContextDataSource;
import com.aoindustries.aoserv.client.web.tomcat.ContextParameter;
import com.aoindustries.aoserv.client.web.tomcat.JkMount;
import com.aoindustries.aoserv.client.web.tomcat.PrivateTomcatSite;
import com.aoindustries.aoserv.client.web.tomcat.SharedTomcat;
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.lang.ObjectUtils;
import com.aoindustries.net.DomainName;
import com.aoindustries.net.Email;
import com.aoindustries.net.HostAddress;
import com.aoindustries.net.InetAddress;
import com.aoindustries.net.Port;
import com.aoindustries.util.SortedArrayList;
import com.aoindustries.util.StringUtility;
import com.aoindustries.util.WrappedException;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * <code>SimpleAOClient</code> is a simplified interface into the client
 * code.  Not all information is available, but less knowledge is required
 * to accomplish some common tasks.  All methods are invoked using standard
 * data types.  The underlying implementation changes over time, but
 * this access point does not change as frequently.
 * <p>
 * Most of the <code>AOSH</code> commands resolve to these method calls.
 *
 * @see  AOSH
 * @see  AOServConnector
 *
 * @author  AO Industries, Inc.
 */
// TODO: This 8700 line monstrosity should be split into appropriate structure
// TODO: as it is used primarily by AOSH.  Either do this directly in AOSH,
// TODO: or have an aoserv-client-simple project that is used by AOSH.
final public class SimpleAOClient {

	final AOServConnector connector;

	/**
	 * Creates a new <code>AOServClient</code> using the provided
	 * <code>AOServConnector</code>.
	 *
	 * @param  connector  the <code>AOServConnector</code> that will be
	 *					used for communication with the server.
	 *
	 * @see  AOServConnector#getConnector()
	 * @see  TCPConnector#getTCPConnector
	 * @see  SSLConnector#getSSLConnector
	 */
	public SimpleAOClient(AOServConnector connector) {
		this.connector=connector;
	}

	private Architecture getArchitecture(String architecture) throws IllegalArgumentException, IOException, SQLException {
		Architecture ar=connector.getDistribution().getArchitectures().get(architecture);
		if(ar==null) throw new IllegalArgumentException("Unable to find Architecture: "+architecture);
		return ar;
	}

	private Server getAOServer(String hostname) throws IllegalArgumentException, IOException, SQLException {
		try {
			Server ao=DomainName.validate(hostname).isValid() ? connector.getLinux().getAoServers().get(DomainName.valueOf(hostname)) : null;
			if(ao==null) throw new IllegalArgumentException("Server is not an AOServer: "+hostname);
			return ao;
		} catch(ValidationException e) {
			// Should not happen since isValid checked first
			throw new WrappedException(e);
		}
	}

	private Account getBusiness(AccountingCode accounting) throws IllegalArgumentException, IOException, SQLException {
		Account bu=connector.getAccount().getBusinesses().get(accounting);
		if(bu==null) throw new IllegalArgumentException("Unable to find Business: "+accounting);
		return bu;
	}

	private Zone getDNSZone(String zone) throws IllegalArgumentException, IOException, SQLException {
		Zone dz=connector.getDns().getDnsZones().get(zone);
		if(dz==null) throw new IllegalArgumentException("Unable to find DNSZone: "+zone);
		return dz;
	}

	private Address getEmailAddress(String aoServer, DomainName domain, String address) throws IllegalArgumentException, IOException, SQLException {
		Address ea=getEmailDomain(aoServer, domain).getEmailAddress(address);
		if(ea==null) throw new IllegalArgumentException("Unable to find EmailAddress: "+address+'@'+domain+" on "+aoServer);
		return ea;
	}

	private Domain getEmailDomain(String aoServer, DomainName domain) throws IllegalArgumentException, IOException, SQLException {
		Domain ed=getAOServer(aoServer).getEmailDomain(domain);
		if(ed==null) throw new IllegalArgumentException("Unable to find EmailDomain: "+domain+" on "+aoServer);
		return ed;
	}

	private com.aoindustries.aoserv.client.email.List getEmailList(String aoServer, UnixPath path) throws IllegalArgumentException, IOException, SQLException {
		com.aoindustries.aoserv.client.email.List el=getAOServer(aoServer).getEmailList(path);
		if(el==null) throw new IllegalArgumentException("Unable to find EmailList: "+path+" on "+aoServer);
		return el;
	}

	private SpamAssassinMode getEmailSpamAssassinIntegrationMode(String mode) throws IllegalArgumentException, IOException, SQLException {
		SpamAssassinMode esaim=connector.getEmail().getEmailSpamAssassinIntegrationModes().get(mode);
		if(esaim==null) throw new IllegalArgumentException("Unable to find EmailSpamAssassinIntegrationMode: "+mode);
		return esaim;
	}

	private FileReplication getFailoverFileReplication(String fromServer, String toServer, String path) throws IllegalArgumentException, IOException, SQLException {
		Host fromSe = getServer(fromServer);
		BackupPartition bp=getAOServer(toServer).getBackupPartitionForPath(path);
		if(bp==null) throw new IllegalArgumentException("Unable to find BackupPartition: "+path+" on "+toServer);
		FileReplication replication = null;
		for(FileReplication ffr : fromSe.getFailoverFileReplications()) {
			if(ffr.getBackupPartition().equals(bp)) {
				replication = ffr;
				break;
			}
		}
		if(replication==null) throw new IllegalArgumentException("Unable to find FailoverFileReplication: From "+fromServer+" to "+toServer+" at "+path);
		return replication;
	}

	@SuppressWarnings("deprecation") // Java 1.7: Do not suppress
	private HttpdServer getHttpdServer(String aoServer, String name) throws IllegalArgumentException, IOException, SQLException {
		for(HttpdServer hs : getAOServer(aoServer).getHttpdServers()) {
			if(ObjectUtils.equals(name, hs.getName())) {
				return hs;
			}
		}
		throw new IllegalArgumentException("Unable to find HttpdServer: " + (name == null ? "\"\"" : name) + " on " + aoServer);
	}

	private SharedTomcat getHttpdSharedTomcat(String aoServer, String name) throws IllegalArgumentException, IOException, SQLException {
		SharedTomcat hst=getAOServer(aoServer).getHttpdSharedTomcat(name);
		if(hst==null) throw new IllegalArgumentException("Unable to find HttpdSharedTomcat: "+name+" on "+aoServer);
		return hst;
	}

	private Site getHttpdSite(String aoServer, String siteName) throws IllegalArgumentException, IOException, SQLException {
		Site hs=getAOServer(aoServer).getHttpdSite(siteName);
		if(hs==null) throw new IllegalArgumentException("Unable to find HttpdSite: "+siteName+" on "+aoServer);
		return hs;
	}

	private IpAddress getIPAddress(String server, String netDevice, InetAddress ipAddress) throws IllegalArgumentException, SQLException, IOException {
		IpAddress ia=getNetDevice(server, netDevice).getIPAddress(ipAddress);
		if(ia==null) throw new IllegalArgumentException("Unable to find IPAddress: "+ipAddress+" on "+netDevice+" on "+server);
		return ia;
	}

	private Language getLanguage(String code) throws IllegalArgumentException, IOException, SQLException {
		Language la = connector.getTicket().getLanguages().get(code);
		if(la==null) throw new IllegalArgumentException("Unable to find Language: "+code);
		return la;
	}

	private User getLinuxAccount(UserId username) throws IllegalArgumentException, IOException, SQLException {
		User la=getUsername(username).getLinuxAccount();
		if(la==null) throw new IllegalArgumentException("Unable to find LinuxAccount: "+username);
		return la;
	}

	private Group getLinuxGroup(GroupId name) throws IllegalArgumentException, IOException, SQLException {
		Group lg=connector.getLinux().getLinuxGroups().get(name);
		if(lg==null) throw new IllegalArgumentException("Unable to find LinuxGroup: "+name);
		return lg;
	}

	private UserServer getLinuxServerAccount(String aoServer, UserId username) throws IllegalArgumentException, IOException, SQLException {
		UserServer lsa=getAOServer(aoServer).getLinuxServerAccount(username);
		if(lsa==null) throw new IllegalArgumentException("Unable to find LinuxServerAccount: "+username+" on "+aoServer);
		return lsa;
	}

	private GroupServer getLinuxServerGroup(String server, GroupId name) throws IllegalArgumentException, IOException, SQLException {
		GroupServer lsg=getAOServer(server).getLinuxServerGroup(name);
		if(lsg==null) throw new IllegalArgumentException("Unable to find LinuxServerGroup: "+name+" on "+server);
		return lsg;
	}

	private com.aoindustries.aoserv.client.mysql.Server getMySQLServer(String aoServer, MySQLServerName name) throws IllegalArgumentException, IOException, SQLException {
		com.aoindustries.aoserv.client.mysql.Server ms=getAOServer(aoServer).getMySQLServer(name);
		if(ms==null) throw new IllegalArgumentException("Unable to find MySQLServer: "+name+" on "+aoServer);
		return ms;
	}

	private Database getMySQLDatabase(String aoServer, MySQLServerName mysqlServer, MySQLDatabaseName name) throws IllegalArgumentException, IOException, SQLException {
		com.aoindustries.aoserv.client.mysql.Server ms=getMySQLServer(aoServer, mysqlServer);
		Database md=ms.getMySQLDatabase(name);
		if(md==null) throw new IllegalArgumentException("Unable to find MySQLDatabase: "+name+" on "+mysqlServer+" on "+aoServer);
		return md;
	}

	private com.aoindustries.aoserv.client.mysql.UserServer getMySQLServerUser(String aoServer, MySQLServerName mysqlServer, MySQLUserId username) throws IllegalArgumentException, IOException, SQLException {
		com.aoindustries.aoserv.client.mysql.UserServer msu=getMySQLServer(aoServer, mysqlServer).getMySQLServerUser(username);
		if(msu==null) throw new IllegalArgumentException("Unable to find MySQLServerUser: "+username+" on "+aoServer);
		return msu;
	}

	private com.aoindustries.aoserv.client.mysql.User getMySQLUser(MySQLUserId username) throws IllegalArgumentException, IOException, SQLException {
		com.aoindustries.aoserv.client.mysql.User mu=getUsername(username).getMySQLUser();
		if(mu==null) throw new IllegalArgumentException("Unable to find MySQLUser: "+username);
		return mu;
	}

	private Bind getNetBind(int pkey) throws IllegalArgumentException, IOException, SQLException {
		Bind nb=connector.getNet().getNetBinds().get(pkey);
		if(nb==null) throw new IllegalArgumentException("Unable to find NetBind: "+pkey);
		return nb;
	}

	private Device getNetDevice(String server, String netDevice) throws IllegalArgumentException, SQLException, IOException {
		Device nd=getServer(server).getNetDevice(netDevice);
		if(nd==null) throw new IllegalArgumentException("Unable to find NetDevice: "+netDevice+" on "+server);
		return nd;
	}

	private OperatingSystem getOperatingSystem(String name) throws IllegalArgumentException, IOException, SQLException {
		OperatingSystem os=connector.getDistribution().getOperatingSystems().get(name);
		if(os==null) throw new IllegalArgumentException("Unable to find OperatingSystem: "+name);
		return os;
	}

	private OperatingSystemVersion getOperatingSystemVersion(String name, String version, Architecture architecture) throws IllegalArgumentException, IOException, SQLException {
		OperatingSystemVersion ov=getOperatingSystem(name).getOperatingSystemVersion(connector, version, architecture);
		if(ov==null) throw new IllegalArgumentException("Unable to find OperatingSystemVersion: "+name+" version "+version+" for architecture of "+architecture);
		return ov;
	}

	private PackageDefinition getPackageDefinition(int packageDefinition) throws IllegalArgumentException, IOException, SQLException {
		PackageDefinition pd=connector.getBilling().getPackageDefinitions().get(packageDefinition);
		if(pd==null) throw new IllegalArgumentException("Unable to find PackageDefinition: "+packageDefinition);
		return pd;
	}

	private Package getPackage(AccountingCode name) throws IllegalArgumentException, IOException, SQLException {
		Package pk=connector.getBilling().getPackages().get(name);
		if(pk==null) throw new IllegalArgumentException("Unable to find Package: "+name);
		return pk;
	}

	private com.aoindustries.aoserv.client.postgresql.Database getPostgresDatabase(String aoServer, PostgresServerName postgres_server, PostgresDatabaseName name) throws IllegalArgumentException, IOException, SQLException {
		com.aoindustries.aoserv.client.postgresql.Server ps=getPostgresServer(aoServer, postgres_server);
		com.aoindustries.aoserv.client.postgresql.Database pd=ps.getPostgresDatabase(name);
		if(pd==null) throw new IllegalArgumentException("Unable to find PostgresDatabase: "+name+" on "+postgres_server+" on "+aoServer);
		return pd;
	}

	private com.aoindustries.aoserv.client.postgresql.Server getPostgresServer(String aoServer, PostgresServerName name) throws IllegalArgumentException, IOException, SQLException {
		com.aoindustries.aoserv.client.postgresql.Server ps=getAOServer(aoServer).getPostgresServer(name);
		if(ps==null) throw new IllegalArgumentException("Unable to find PostgresServer: "+name+" on "+aoServer);
		return ps;
	}

	private com.aoindustries.aoserv.client.postgresql.UserServer getPostgresServerUser(String aoServer, PostgresServerName postgres_server, PostgresUserId username) throws IllegalArgumentException, IOException, SQLException {
		com.aoindustries.aoserv.client.postgresql.UserServer psu=getPostgresServer(aoServer, postgres_server).getPostgresServerUser(username);
		if(psu==null) throw new IllegalArgumentException("Unable to find PostgresServerUser: "+username+" on "+postgres_server+" on "+aoServer);
		return psu;
	}

	private com.aoindustries.aoserv.client.postgresql.User getPostgresUser(PostgresUserId username) throws IllegalArgumentException, IOException, SQLException {
		com.aoindustries.aoserv.client.postgresql.User pu=getUsername(username).getPostgresUser();
		if(pu==null) throw new IllegalArgumentException("Unable to find PostgresUser: "+username);
		return pu;
	}

	private Host getServer(String server) throws IllegalArgumentException, SQLException, IOException {
		Host se=connector.getNet().getServers().get(server);
		if(se==null) throw new IllegalArgumentException("Unable to find Server: "+server);
		return se;
	}

	private ServerFarm getServerFarm(String name) throws IllegalArgumentException, SQLException, IOException {
		ServerFarm sf=connector.getInfrastructure().getServerFarms().get(name);
		if(sf==null) throw new IllegalArgumentException("Unable to find ServerFarm: "+name);
		return sf;
	}

	private Certificate getSslCertificate(String aoServer, String keyFileOrCertbotName) throws IllegalArgumentException, SQLException, IOException {
		for(Certificate cert : getAOServer(aoServer).getSslCertificates()) {
			if(
				cert.getKeyFile().toString().equals(keyFileOrCertbotName)
				|| keyFileOrCertbotName.equals(cert.getCertbotName())
			) {
				return cert;
			}
		}
		throw new IllegalArgumentException("Unable to find SslCertificate: " + keyFileOrCertbotName + " on " + aoServer);
	}

	/**
	 * Gets the ticket category in "/ path" form.
	 */
	private Category getTicketCategory(String path) throws IllegalArgumentException, IOException, SQLException {
		Category tc = null;
		for(String name : StringUtility.splitString(path, '/')) {
			Category newTc = connector.getReseller().getTicketCategories().getTicketCategory(tc, name);
			if(newTc==null) {
				if(tc==null) throw new IllegalArgumentException("Unable to find top-level TicketCategory: "+name);
				else throw new IllegalArgumentException("Unable to TicketCategory: "+name+" in "+tc);
			}
			tc = newTc;
		}
		if(tc==null) throw new IllegalArgumentException("Unable to find TicketCategory: "+path);
		return tc;
	}

	private Priority getTicketPriority(String priority) throws IllegalArgumentException, IOException, SQLException {
		Priority tp = connector.getTicket().getTicketPriorities().get(priority);
		if(tp==null) throw new IllegalArgumentException("Unable to find TicketPriority: "+priority);
		return tp;
	}

	private TicketType getTicketType(String type) throws IllegalArgumentException, IOException, SQLException {
		TicketType tt = connector.getTicket().getTicketTypes().get(type);
		if(tt==null) throw new IllegalArgumentException("Unable to find TicketType: "+type);
		return tt;
	}

	private Username getUsername(UserId username) throws IllegalArgumentException, IOException, SQLException {
		Username un=connector.getAccount().getUsernames().get(username);
		if(un==null) throw new IllegalArgumentException("Unable to find Username: "+username);
		return un;
	}

	private VirtualServer getVirtualServer(String virtualServer) throws IllegalArgumentException, SQLException, IOException {
		Host se = getServer(virtualServer);
		VirtualServer vs = se.getVirtualServer();
		if(vs==null) throw new IllegalArgumentException("Unable to find VirtualServer: "+virtualServer);
		return vs;
	}

	private VirtualDisk getVirtualDisk(String virtualServer, String device) throws IllegalArgumentException, SQLException, IOException {
		VirtualServer vs = getVirtualServer(virtualServer);
		VirtualDisk vd = vs.getVirtualDisk(device);
		if(vd==null) throw new IllegalArgumentException("Unable to find VirtualDisk: "+virtualServer+":/dev/"+device);
		return vd;
	}

	private com.aoindustries.aoserv.client.net.reputation.Set getIpReputationSet(String identifier) throws IllegalArgumentException, SQLException, IOException {
		com.aoindustries.aoserv.client.net.reputation.Set set = connector.getNet_reputation().getIpReputationSets().get(identifier);
		if(set==null) throw new IllegalArgumentException("Unable to find IpReputationSet: "+identifier);
		return set;
	}

	/**
	 * Adds a new backup <code>Server</code>.
	 *
	 * @param  hostname  the desired hostname for the server
	 * @param  farm  the farm the server is part of
	 * @param  owner  the package the server belongs to
	 * @param  description  a description of the server
	 * @param  backup_hour  the hour the backup will be run if used in daemon mode,
	 *                      expressed in server-local time
	 * @param  os_type  the type of operating system on the server
	 * @param  os_version  the version of operating system on the server
	 * @param  architecture  the type of CPU(s) on the server
	 * @param  username  the desired backup account username
	 * @param  password  the desired backup account password
	 * @param  contact_phone  the phone number to call for anything related to this server
	 * @param  contact_email  the email address to contact for anything related to this server
	 *
	 * @exception  IOException  if unable to communicate with the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>ServerFarm</code>, <code>Business</code>, <code>Architecture</code>,
	 *                                       <code>OperatingSystem</code>, or <code>OperatingSystemVersion<code>
	 *
	 * @see  Host
	 * @see  ServerTable#addBackupServer
	 */
	public int addBackupServer(
		String hostname,
		String farm,
		AccountingCode owner,
		String description,
		int backup_hour,
		String os_type,
		String os_version,
		String architecture,
		UserId username,
		String password,
		String contact_phone,
		String contact_email
	) throws IllegalArgumentException, IOException, SQLException {
		return connector.getNet().getServers().addBackupServer(
			hostname,
			getServerFarm(farm),
			getPackage(owner),
			description,
			backup_hour,
			getOperatingSystemVersion(os_type, os_version, getArchitecture(architecture)),
			username,
			password,
			contact_phone,
			contact_email
		);
	}

	/**
	 * Adds a new <code>Business</code> to the system.
	 *
	 * @param  accounting  the accounting code of the new business
	 * @param  contractVersion  the version number of the digitally signed contract
	 * @param  defaultServer  the hostname of the default server
	 * @param  parent  the parent business of the new business
	 * @param  can_add_backup_servers  allows backup servers to be added to the system
	 * @param  can_add_businesses  if <code>true</code>, the new business
	 *					is allowed to add additional businesses
	 * @param  billParent  if <code>true</code> the parent account will be billed instead
	 *                                  of this account
	 *
	 * @exception  IOException  if unable to communicate with the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the server or parent business
	 *
	 * @see  Account
	 * @see  #checkAccounting
	 * @see  Server#addBusiness
	 */
	public void addBusiness(
		AccountingCode accounting,
		String contractVersion,
		String defaultServer,
		AccountingCode parent,
		boolean can_add_backup_servers,
		boolean can_add_businesses,
		boolean can_see_prices,
		boolean billParent
	) throws IllegalArgumentException, SQLException, IOException {
		if(contractVersion!=null && contractVersion.length()==0) contractVersion=null;
		getServer(defaultServer).addBusiness(
			accounting,
			contractVersion,
			getBusiness(parent),
			can_add_backup_servers,
			can_add_businesses,
			can_see_prices,
			billParent
		);
	}

	/**
	 * Adds a new <code>BusinessAdministrator</code> to a
	 * <code>Business</code>.
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if unable to find the <code>Username</code>
	 *
	 * @see  Administrator
	 * @see  Account
	 * @see  Username#addBusinessAdministrator
	 */
	public void addBusinessAdministrator(
		UserId username,
		String name,
		String title,
		Date birthday,
		boolean isPrivate,
		String workPhone,
		String homePhone,
		String cellPhone,
		String fax,
		String email,
		String address1,
		String address2,
		String city,
		String state,
		String country,
		String zip,
		boolean enableEmailSupport
	) throws IllegalArgumentException, IOException, SQLException {
		Username usernameObj=getUsername(username);
		usernameObj.addBusinessAdministrator(
			name,
			title,
			birthday,
			isPrivate,
			workPhone,
			homePhone,
			cellPhone,
			fax,
			email,
			address1,
			address2,
			city,
			state,
			country,
			zip,
			enableEmailSupport
		);
	}

	/**
	 * Adds a new <code>BusinessProfile</code> to a <code>Business</code>.  The
	 * profile is a complete set of contact information about a business.  New
	 * profiles can be added, and they are used as the contact information, but
	 * old profiles are still available.
	 *
	 * @exception  IllegalArgumentException  if unable to find the <code>Business</code>
	 *
	 * @see  BusinessProfile
	 * @see  Account
	 * @see  Business#addBusinessProfile
	 */
	public int addBusinessProfile(
		AccountingCode business,
		String name,
		boolean isPrivate,
		String phone,
		String fax,
		String address1,
		String address2,
		String city,
		String state,
		String country,
		String zip,
		boolean sendInvoice,
		String billingContact,
		String billingEmail,
		String billingEmailFormat,
		String technicalContact,
		String technicalEmail,
		String technicalEmailFormat
	) throws IllegalArgumentException, IOException, SQLException {
		return getBusiness(business).addBusinessProfile(
			name,
			isPrivate,
			phone,
			fax,
			address1,
			address2,
			city,
			state,
			country,
			zip,
			sendInvoice,
			billingContact,
			billingEmail,
			Profile.EmailFormat.valueOf(billingEmailFormat.toUpperCase(Locale.ROOT)),
			technicalContact,
			technicalEmail,
			Profile.EmailFormat.valueOf(technicalEmailFormat.toUpperCase(Locale.ROOT))
		);
	}

	/**
	 * Grants a <code>Business</code> access to a <code>Server</code>.
	 *
	 * @param  accounting  the accounting code of the business
	 * @param  server  the hostname of the server
	 *
	 * @return  the pkey of the new <code>BusinessServer</code>
	 *
	 * @exception  IOException  if unable to communicate with the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the business or server
	 *
	 * @see  AccountHost
	 * @see  #checkAccounting
	 * @see  Business#addBusinessServer
	 */
	public int addBusinessServer(
		AccountingCode accounting,
		String server
	) throws IllegalArgumentException, SQLException, IOException {
		return getBusiness(accounting).addBusinessServer(getServer(server));
	}

	/**
	 * Adds a new <code>CvsRepository</code> to a <code>Server</code>.
	 *
	 * @param  aoServer  the hostname of the server
	 * @param  path    the full path of the repository
	 * @param  username  the name of the shell account that owns the directory
	 * @param  group     the group that owns the directory
	 * @param  mode      the permissions of the directory
	 *
	 * @return  the <code>pkey</code> of the new <code>CvsRepository</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code>, <code>LinuxServerAccount</code>,
	 *					or <code>LinuxServerGroup</code>
	 *
	 * @see  AOServer#addCvsRepository
	 */
	public int addCvsRepository(
		String aoServer,
		UnixPath path,
		UserId username,
		GroupId group,
		long mode
	) throws IllegalArgumentException, IOException, SQLException {
		Server ao=getAOServer(aoServer);
		return ao.addCvsRepository(
			path,
			getLinuxServerAccount(aoServer, username),
			getLinuxServerGroup(aoServer, group),
			mode
		);
	}

	/**
	 * Adds a new <code>DNSRecord</code> to a <code>DNSZone</code>.  Each <code>DNSZone</code>
	 * can have multiple DNS records in it, each being a <code>DNSRecord</code>.
	 *
	 * @param  zone      the zone, in the <code>name.<i>topleveldomain</i>.</code> format.  Please note the
	 *                   trailing period (<code>.</code>)
	 * @param  domain    the part of the name before the zone or <code>@</code> for the zone itself.  For example,
	 *                   the domain for the hostname of <code>www.aoindustries.com.</code> in the
	 *                   <code>aoindustries.com.</code> zone is <code>www</code>.
	 * @param  type      the <code>DNSType</code>
	 * @param  priority  if a <code>MX</code> or <code>SRV</code> type, then the value is the priority of the record, otherwise
	 *                   it is <code>DNSRecord.NO_PRIORITY</code>.
	 * @param  weight    if a <code>SRV</code> type, then the value is the weight of the record, otherwise
	 *                   it is <code>DNSRecord.NO_WEIGHT</code>.
	 * @param  port      if a <code>SRV</code> type, then the value is the port of the record, otherwise
	 *                   it is <code>DNSRecord.NO_PORT</code>.
	 *
	 * @return  the <code>pkey</code> of the new <code>DNSRecord</code>
	 *
	 * @exception  IOException   if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *                           violation occurs
	 * @exception  IllegalArgumentException  if the priority is provided for a non-<code>MX</code> and non-<code>SRV</code> record,
	 *                                       the priority is not provided for a <code>MX</code> or <code>SRV</code> record,
	 *                                       if the weight is provided for a non-<code>SRV</code> record,
	 *                                       the weight is not provided for a <code>SRV</code> record,
	 *                                       if the port is provided for a non-<code>SRV</code> record,
	 *                                       the port is not provided for a <code>SRV</code> record,
	 *                                       the destination is not the correct format for the <code>DNSType</code>,
	 *                                       or  unable to find the <code>DNSZone</code> or <code>DNSType</code>
	 *
	 * @see  DNSZone#addDNSRecord
	 * @see  Record
	 * @see  #addDNSZone
	 * @see  DNSType#checkDestination
	 */
	public int addDNSRecord(
		String zone,
		String domain,
		String type,
		int priority,
		int weight,
		int port,
		String destination,
		int ttl
	) throws IllegalArgumentException, IOException, SQLException {
		Zone nz=getDNSZone(zone);

		// Must be a valid type
		RecordType nt=connector.getDns().getDnsTypes().get(type);
		if(nt==null) throw new IllegalArgumentException("Unable to find DNSType: "+type);

		// Must have appropriate priority
		if(nt.hasPriority()) {
			if(priority==Record.NO_PRIORITY) throw new IllegalArgumentException("priority required for type="+type);
			else if(priority<=0) throw new IllegalArgumentException("Invalid priority: "+priority);
		} else {
			if(priority!=Record.NO_PRIORITY) throw new IllegalArgumentException("No priority allowed for type="+type);
		}

		// Must have appropriate weight
		if(nt.hasWeight()) {
			if(weight==Record.NO_WEIGHT) throw new IllegalArgumentException("weight required for type="+type);
			else if(weight<=0) throw new IllegalArgumentException("Invalid weight: "+weight);
		} else {
			if(weight!=Record.NO_WEIGHT) throw new IllegalArgumentException("No weight allowed for type="+type);
		}

		// Must have appropriate port
		if(nt.hasPort()) {
			if(port==Record.NO_PORT) throw new IllegalArgumentException("port required for type="+type);
			else if(port<1 || port>65535) throw new IllegalArgumentException("Invalid port: "+port);
		} else {
			if(port!=Record.NO_PORT) throw new IllegalArgumentException("No port allowed for type="+type);
		}

		// Must have a valid destination type
		nt.checkDestination(destination);

		return nz.addDNSRecord(
			domain,
			nt,
			priority,
			weight,
			port,
			destination,
			ttl
		);
	}

	/**
	 * Adds a new <code>DNSZone</code> to a system.  A <code>DNSZone</code> is one unique domain in
	 * the name servers.  It is always one host up from a top level domain.  In <code><i>mydomain</i>.com.</code>
	 * <code>com</code> is the top level domain, which are defined by <code>DNSTLD</code>s.
	 *
	 * @param  packageName  the name of the <code>Package</code> that owns this domain
	 * @param  zone  the complete domain of the new <code>DNSZone</code>
	 * @param  ip  the IP address that will be used for the default <code>DNSRecord</code>s
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Package</code> or either parameter is not in
	 *                                  the proper format.
	 *
	 * @see  Package#addDNSZone
	 * @see  Zone
	 * @see  #addDNSRecord
	 * @see  IpAddress
	 * @see  DNSTLD
	 */
	public void addDNSZone(
		AccountingCode packageName,
		String zone,
		InetAddress ip,
		int ttl
	) throws IllegalArgumentException, IOException, SQLException {
		if(!connector.getDns().getDnsZones().checkDNSZone(zone)) throw new IllegalArgumentException("Invalid zone: "+zone);
		getPackage(packageName).addDNSZone(zone, ip, ttl);
	}

	/**
	 * Forwards email addressed to an address at a <code>EmailDomain</code> to
	 * a different email address.  The destination email address may be any email
	 * address, not just those in a <code>EmailDomain</code>.
	 *
	 * @param  address  the part of the email address before the <code>@</code>
	 * @param  domain  the part of the email address after the <code>@</code>
	 * @param  aoServer  the hostname of the server hosting the domain
	 * @param  destination  the completed email address of the final delivery address
	 *
	 * @exception  IOException  if unable to communicate with the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable find the <code>EmailDomain</code>
	 *
	 * @see  #checkEmailForwarding
	 * @see  EmailAddress#addEmailForwarding
	 * @see  Domain
	 */
	public int addEmailForwarding(
		String address,
		DomainName domain,
		String aoServer,
		Email destination
	) throws IllegalArgumentException, IOException, SQLException {
		Domain sd=getEmailDomain(aoServer, domain);
		Address eaddress=sd.getEmailAddress(address);
		boolean added=false;
		if(eaddress==null) {
			eaddress=connector.getEmail().getEmailAddresses().get(sd.addEmailAddress(address));
			added=true;
		}
		try {
			return eaddress.addEmailForwarding(destination);
		} catch(RuntimeException err) {
			if(added && !eaddress.isUsed()) eaddress.remove();
			throw err;
		}
	}

	/**
	 * Adds a new <code>EmailList</code> to the system.  When an email is sent
	 * to an <code>EmailList</code>, it is immediately forwarded to all addresses
	 * contained in the list.  The list may accept mail on any number of addresses
	 * and forward to any number of recipients.
	 * <p>
	 * Even though the <code>EmailList</code> may receive email on any number of
	 * addresses, each address must be part of a <code>EmailDomain</code> that
	 * is hosted on the same <code>Server</code> as the <code>EmailList</code>.
	 * If email in a domain on another <code>Server</code> is required to be sent
	 * to this list, it must be forwarded from the other <code>Server</code> via
	 * a <code>EmailForwarding</code>.
	 * <p>
	 * The list of destinations for the <code>EmailList</code> is stored on the
	 * <code>Server</code> in a flat file of one address per line.  This file
	 * may be either manipulated through the API or used directly on the
	 * filesystem.
	 *
	 * @param  aoServer  the hostname of the server the list is hosted on
	 * @param  path  the name of the file that stores the list
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data
	 *					integrity violation occurs
	 * @exception  IllegalArgumentException  if unable to find find the <code>AOServer</code>,
	 *					<code>LinuxServerAccount</code>, or <code>LinuxServerGroup</code>
	 *
	 * @see  #checkEmailListPath
	 * @see  #addEmailListAddress
	 * @see  ListAddress
	 * @see  Domain
	 * @see  Forwarding
	 * @see  Host
	 * @see  UserServer
	 * @see  GroupServer
	 */
	public int addEmailList(
		String aoServer,
		UnixPath path,
		UserId username,
		GroupId group
	) throws IllegalArgumentException, IOException, SQLException {
		return connector.getEmail().getEmailLists().addEmailList(
			path,
			getLinuxServerAccount(aoServer, username),
			getLinuxServerGroup(aoServer, group)
		);
	}

	/**
	 * Adds to the list of <code>EmailAddresses</code> to which the <code>EmailList</code>
	 * will accept mail.
	 *
	 * @param  address  the part of the email address before the <code>@</code>
	 * @param  domain  the part of the email address after the <code>@</code>
	 * @param  path  the path of the list
	 * @param  aoServer  the hostname of the server
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data
	 *					integrity violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>EmailDomain</code>
	 *					or <code>EmailList</code>
	 *
	 * @see  #addEmailList
	 * @see  List
	 * @see  Address
	 * @see  Domain
	 */
	public int addEmailListAddress(
		String address,
		DomainName domain,
		UnixPath path,
		String aoServer
	) throws IllegalArgumentException, IOException, SQLException {
		Domain sd=getEmailDomain(aoServer, domain);
		com.aoindustries.aoserv.client.email.List el=getEmailList(aoServer, path);
		Address ea=sd.getEmailAddress(address);
		boolean added=false;
		if(ea==null) {
			ea=connector.getEmail().getEmailAddresses().get(connector.getEmail().getEmailAddresses().addEmailAddress(address, sd));
			added=true;
		}
		try {
			return el.addEmailAddress(ea);
		} catch(RuntimeException err) {
			if(added && !ea.isUsed()) ea.remove();
			throw err;
		}
	}

	/**
	 * Adds a new <code>EmailPipe</code> to the system.  When an email is sent
	 * to an <code>EmailPipe</code>, a process is invoked with the email pipes into
	 * the process' standard input.
	 *
	 * @param  aoServer  the hostname of the server that the process exists on
	 * @param  command  the full command line of the program to launch
	 * @param  packageName  the package that this <code>EmailPipe</code> belongs to
	 *
	 * @return  the pkey of the new pipe
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data
	 *					integrity violation occurs
	 * @exception  IllegalArgumentException  if unable to find find the <code>Server</code> or
	 *					<code>Package</code>
	 *
	 * @see  #addEmailPipeAddress
	 * @see  AOServer#addEmailPipe
	 */
	public int addEmailPipe(
		String aoServer,
		String command,
		AccountingCode packageName
	) throws IllegalArgumentException, IOException, SQLException {
		return connector.getEmail().getEmailPipes().addEmailPipe(
			getAOServer(aoServer),
			command,
			getPackage(packageName)
		);
	}

	/**
	 * Adds an address to the list of email addresses that will be piped to
	 * an <code>EmailPipe</code>.
	 *
	 * @param  address  the part of the email address before the <code>@</code>
	 * @param  domain  the part of the email address after the <code>@</code>
	 * @param  pkey  the pkey of the <code>EmailList</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data
	 *					integrity violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>EmailDomain</code>
	 *					or <code>EmailPipe</code>
	 *
	 * @see  #addEmailPipe
	 * @see  Pipe
	 * @see  Address
	 * @see  Domain
	 */
	public int addEmailPipeAddress(
		String address,
		DomainName domain,
		int pkey
	) throws IllegalArgumentException, IOException, SQLException {
		Pipe ep=connector.getEmail().getEmailPipes().get(pkey);
		if(ep==null) throw new IllegalArgumentException("Unable to find EmailPipe: "+ep);
		Server ao=ep.getAOServer();
		Domain sd=ao.getEmailDomain(domain);
		if(sd==null) throw new IllegalArgumentException("Unable to find EmailDomain: "+domain+" on "+ao.getHostname());
		Address ea=sd.getEmailAddress(address);
		boolean added=false;
		if(ea==null) {
			ea = connector.getEmail().getEmailAddresses().get(sd.addEmailAddress(address));
			added=true;
		}
		try {
			return ep.addEmailAddress(ea);
		} catch(RuntimeException err) {
			if(added && !ea.isUsed()) ea.remove();
			throw err;
		}
	}

	/**
	 * Adds a <code>FileBackupSetting</code> to a <code>FailoverFileReplication</code>.
	 *
	 * @param  replication  the pkey of the FailoverFileReplication
	 * @param  path  the path that is being configured
	 * @param  backupEnabled  the enabled flag for the prefix
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data
	 *					integrity violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>FailoverFileReplication</code>, or <code>Package</code>
	 *
	 * @return  the pkey of the newly created <code>FileBackupSetting</code>
	 *
	 * @see  FailoverFileReplication#addFileBackupSetting
	 * @see  FileReplicationSetting
	 */
	public int addFileBackupSetting(
		int replication,
		String path,
		boolean backupEnabled,
		boolean required
	) throws IllegalArgumentException, IOException, SQLException {
		FileReplication ffr = getConnector().getBackup().getFailoverFileReplications().get(replication);
		if(ffr==null) throw new IllegalArgumentException("Unable to find FailoverFileReplication: "+replication);
		return ffr.addFileBackupSetting(
			path,
			backupEnabled,
			required
		);
	}

	/**
	 * Flags a <code>LinuxAccount</code> as being a <code>FTPGuestUser</code>.  Once
	 * flagged, FTP connections as that user will be limited to transfers in their
	 * home directory.
	 *
	 * @param  username  the username of the <code>LinuxAccount</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data
	 *					integrity violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>LinuxAccount</code>
	 *
	 * @see  #addLinuxAccount
	 * @see  LinuxAccount#addFTPGuestUser
	 */
	public void addFTPGuestUser(
		UserId username
	) throws IllegalArgumentException, IOException, SQLException {
		getLinuxAccount(username).addFTPGuestUser();
	}

	/**
	 * Finds a PHP version given its version, allowing prefix matches.
	 */
	private SoftwareVersion findPhpVersion(Server aoServer, String phpVersion) throws IllegalArgumentException, IOException, SQLException {
		if(phpVersion == null || phpVersion.isEmpty()) return null;
		String prefix = phpVersion;
		if(!prefix.endsWith(".")) prefix += '.';
		int osvId = aoServer.getServer().getOperatingSystemVersion_id();
		List<SoftwareVersion> matches = new ArrayList<>();
		for(SoftwareVersion tv : connector.getDistribution().getTechnologyVersions()) {
			if(
				tv.getOperatingSystemVersion_id() == osvId
				&& tv.getTechnologyName_name().equals(Software.PHP)
				&& (
					tv.getVersion().equals(phpVersion)
					|| tv.getVersion().startsWith(prefix)
				)
			) {
				matches.add(tv);
			}
		}
		if(matches.isEmpty()) {
			throw new IllegalArgumentException("Unable to find PHP version: " + phpVersion);
		} else if(matches.size() > 1) {
			StringBuilder sb = new StringBuilder();
			sb.append("Found more than one matching PHP version, please be more specific: ");
			boolean didOne = false;
			for(SoftwareVersion match : matches) {
				if(didOne) sb.append(", ");
				else didOne = true;
				sb.append(match.getVersion());
			}
			throw new IllegalArgumentException(sb.toString());
		} else {
			return matches.get(0);
		}
	}

	/**
	 * Finds a Tomcat version given its version, allowing prefix matches.
	 */
	private com.aoindustries.aoserv.client.web.tomcat.Version findTomcatVersion(Server aoServer, String version) throws IllegalArgumentException, IOException, SQLException {
		String prefix = version;
		if(!prefix.endsWith(".")) prefix += '.';
		int osvId = aoServer.getServer().getOperatingSystemVersion_id();
		List<com.aoindustries.aoserv.client.web.tomcat.Version> matches = new ArrayList<>();
		for(com.aoindustries.aoserv.client.web.tomcat.Version htv : connector.getWeb_tomcat().getHttpdTomcatVersions()) {
			SoftwareVersion tv = htv.getTechnologyVersion(connector);
			if(
				tv.getOperatingSystemVersion_id() == osvId
				&& (
					tv.getVersion().equals(version)
					|| tv.getVersion().startsWith(prefix)
				)
			) {
				matches.add(htv);
			}
		}
		if(matches.isEmpty()) {
			throw new IllegalArgumentException("Unable to find Tomcat version: " + version);
		} else if(matches.size() > 1) {
			StringBuilder sb = new StringBuilder();
			sb.append("Found more than one matching Tomcat version, please be more specific: ");
			boolean didOne = false;
			for(com.aoindustries.aoserv.client.web.tomcat.Version match : matches) {
				if(didOne) sb.append(", ");
				else didOne = true;
				sb.append(match.getTechnologyVersion(connector).getVersion());
			}
			throw new IllegalArgumentException(sb.toString());
		} else {
			return matches.get(0);
		}
	}

	/**
	 * Adds a new <code>HttpdJBossSite</code> to the system.  An <code>HttpdJBossSite</code> is
	 * an <code>HttpdSite</code> that uses the Tomcat servlet engine and JBoss as an EJB container.
	 *
	 * @param  aoServer  the hostname of the <code>AOServer</code>
	 * @param  siteName  the name of the <code>HttpdTomcatStdSite</code>
	 * @param  packageName  the name of the <code>Package</code>
	 * @param  jvmUsername  the username of the <code>LinuxAccount</code> that the Java VM
	 *					will run as
	 * @param  groupName  the name of the <code>LinuxGroup</code> that the web site will
	 *					be owned by
	 * @param  serverAdmin  the email address of the person who is responsible for the site
	 *					content and reliability
	 * @param  useApache  instructs the system to host static content, shtml, CGI, and PHP using Apache,
	 *					comes at the price of less request control through Tomcat
	 * @param  ipAddress  the <code>IPAddress</code> that the web site will bind to.  In
	 *					order for HTTP requests to succeed, <code>DNSRecord</code> entries
	 *					must point the hostnames of this <code>HttpdTomcatStdSite</code> to this
	 *					<code>IPAddress</code>.  If <code>null</code>, the system will assign a
	 *                                      shared IP address.
	 * @param  primaryHttpHostname  the primary hostname of the <code>HttpdTomcatStdSite</code> for the
	 *					HTTP protocol
	 * @param  altHttpHostnames  any number of alternate hostnames for the HTTP protocol or
	 *					<code>null</code> for none
	 * @param  jBossVersion  the version number of <code>JBoss</code> to install in the site
	 *
	 * @return  the <code>pkey</code> of the new <code>HttpdTomcatStdSite</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find a referenced object or a
	 *					parameter is not in the right format
	 *
	 * @see  Site
	 */
	public int addHttpdJBossSite(
		String aoServer,
		String siteName,
		AccountingCode packageName,
		UserId jvmUsername,
		GroupId groupName,
		Email serverAdmin,
		boolean useApache,
		InetAddress ipAddress,
		String netDevice,
		DomainName primaryHttpHostname,
		DomainName[] altHttpHostnames,
		String jBossVersion
	) throws IllegalArgumentException, SQLException, IOException {
		Server ao=getAOServer(aoServer);
		checkSiteName(siteName);

		IpAddress ip;
		if (netDevice!=null && (netDevice=netDevice.trim()).length()==0) netDevice=null;
		if (ipAddress!=null && netDevice!=null) {
			ip=getIPAddress(aoServer, netDevice, ipAddress);
		} else if(ipAddress==null && netDevice==null) {
			ip=null;
		} else {
			throw new IllegalArgumentException("ip_address and net_device must both be null or both be not null");
		}
		com.aoindustries.aoserv.client.web.jboss.Version hjv=connector.getWeb_jboss().getHttpdJBossVersions().getHttpdJBossVersion(jBossVersion, ao.getServer().getOperatingSystemVersion());
		if(hjv==null) throw new IllegalArgumentException("Unable to find HttpdJBossVersion: "+jBossVersion);
		return ao.addHttpdJBossSite(
			siteName,
			getPackage(packageName),
			getLinuxServerAccount(aoServer, jvmUsername).getLinuxAccount(),
			getLinuxServerGroup(aoServer, groupName).getLinuxGroup(),
			serverAdmin,
			useApache,
			ip,
			primaryHttpHostname,
			altHttpHostnames,
			hjv
		);
	}

	/**
	 * Adds a new <code>HttpdSharedTomcat</code> to a server.
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if unable to find the <code>LinuxServerAccount</code>,
	 *					the <code>LinuxServerGroup</code>, or the <code>Server</code>
	 *
	 * @see  SharedTomcat
	 * @see  UserServer
	 * @see  GroupServer
	 * @see  Host
	 */
	public int addHttpdSharedTomcat(
		String name,
		String aoServer,
		String version,
		UserId linuxServerAccount,
		GroupId linuxServerGroup
	) throws IllegalArgumentException, SQLException, IOException {
		Server ao=getAOServer(aoServer);
		return ao.addHttpdSharedTomcat(
			name,
			findTomcatVersion(ao, version),
			getLinuxServerAccount(aoServer, linuxServerAccount),
			getLinuxServerGroup(aoServer, linuxServerGroup)
		);
	}

	/**
	 * Adds a new <code>HttpdSiteURL</code> to a <code>HttpdSiteBind</code>.
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if unable to find the <code>HttpdSiteBind</code>
	 */
	public int addHttpdSiteURL(
		int hsbPKey,
		DomainName hostname
	) throws IllegalArgumentException, IOException, SQLException {
		VirtualHost hsb=connector.getWeb().getHttpdSiteBinds().get(hsbPKey);
		if(hsb==null) throw new IllegalArgumentException("Unable to find HttpdSiteBind: "+hsbPKey);
		return hsb.addHttpdSiteURL(hostname);
	}

	/**
	 * Adds a new {@link Location} to a {@link Site}.
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @throws IllegalArgumentException if unable to find the {@link Site}
	 */
	public int addHttpdSiteAuthenticatedLocation(
		String siteName,
		String aoServer,
		String path,
		boolean isRegularExpression,
		String authName,
		UnixPath authGroupFile,
		UnixPath authUserFile,
		String require,
		String handler
	) throws IllegalArgumentException, IOException, SQLException {
		Site hs = getAOServer(aoServer).getHttpdSite(siteName);
		if(hs == null) throw new IllegalArgumentException("Unable to find HttpdSite: " + siteName + " on " + aoServer);
		return hs.addHttpdSiteAuthenticatedLocation(path, isRegularExpression, authName, authGroupFile, authUserFile, require, handler);
	}

	/**
	 * Updates a {@link Location}.
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @throws IllegalArgumentException if unable to find the {@link Site}
	 */
	public void setHttpdSiteAuthenticatedLocationAttributes(
		String siteName,
		String aoServer,
		String path,
		boolean isRegularExpression,
		String authName,
		UnixPath authGroupFile,
		UnixPath authUserFile,
		String require,
		String handler
	) throws IllegalArgumentException, IOException, SQLException {
		Site hs = getAOServer(aoServer).getHttpdSite(siteName);
		if(hs == null) throw new IllegalArgumentException("Unable to find HttpdSite: " + siteName + " on " + aoServer);
		Location hsal = null;
		for(Location location : hs.getHttpdSiteAuthenticatedLocations()) {
			if(path.equals(location.getPath())) {
				hsal = location;
				break;
			}
		}
		if(hsal == null) throw new IllegalArgumentException("Unable to find HttpdSiteAuthenticatedLocation: " + siteName + " on " + aoServer + " at " + path);
		hsal.setAttributes(path, isRegularExpression, authName, authGroupFile, authUserFile, require, handler);
	}

	/**
	 * Adds a new <code>HttpdTomcatContext</code> to a <code>HttpdTomcatSite</code>.
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code>, <code>HttpdSite</code>,
	 *                                  or <code>HttpdTomcatSite</code>
	 */
	public int addHttpdTomcatContext(
		String siteName,
		String aoServer,
		String className,
		boolean cookies,
		boolean crossContext,
		UnixPath docBase,
		boolean override,
		String path,
		boolean privileged,
		boolean reloadable,
		boolean useNaming,
		String wrapperClass,
		int debug,
		UnixPath workDir,
		boolean serverXmlConfigured
	) throws IllegalArgumentException, IOException, SQLException {
		Site hs=getHttpdSite(aoServer, siteName);
		com.aoindustries.aoserv.client.web.tomcat.Site hts=hs.getHttpdTomcatSite();
		if(hts==null) throw new IllegalArgumentException("Unable to find HttpdTomcatSite: "+siteName+" on "+aoServer);
		return hts.addHttpdTomcatContext(
			className==null||(className=className.trim()).length()==0?null:className,
			cookies,
			crossContext,
			docBase,
			override,
			path,
			privileged,
			reloadable,
			useNaming,
			wrapperClass==null || (wrapperClass=wrapperClass.trim()).length()==0?null:wrapperClass,
			debug,
			workDir,
			serverXmlConfigured
		);
	}

	/**
	 * Adds a new data source to a <code>HttpdTomcatContext</code>.
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code>, <code>HttpdSite</code>,
	 *                                  <code>HttpdTomcatSite</code> or <code>HttpdTomcatContext</code>.
	 */
	public int addHttpdTomcatDataSource(
		String siteName,
		String aoServer,
		String path,
		String name,
		String driverClassName,
		String url,
		String username,
		String password,
		int maxActive,
		int maxIdle,
		int maxWait,
		String validationQuery
	) throws IllegalArgumentException, IOException, SQLException {
		Site hs=getHttpdSite(aoServer, siteName);
		com.aoindustries.aoserv.client.web.tomcat.Site hts=hs.getHttpdTomcatSite();
		if(hts==null) throw new IllegalArgumentException("Unable to find HttpdTomcatSite: "+siteName+" on "+aoServer);
		Context htc=hts.getHttpdTomcatContext(path);
		if(htc==null) throw new IllegalArgumentException("Unable to find HttpdTomcatContext: "+siteName+" on "+aoServer+" path='"+path+'\'');
		return htc.addHttpdTomcatDataSource(
			name,
			driverClassName,
			url,
			username,
			password,
			maxActive,
			maxIdle,
			maxWait,
			validationQuery
		);
	}

	/**
	 * Adds a new parameter to a <code>HttpdTomcatContext</code>.
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code>, <code>HttpdSite</code>,
	 *                                  <code>HttpdTomcatSite</code> or <code>HttpdTomcatContext</code>.
	 */
	public int addHttpdTomcatParameter(
		String siteName,
		String aoServer,
		String path,
		String name,
		String value,
		boolean override,
		String description
	) throws IllegalArgumentException, IOException, SQLException {
		Site hs=getHttpdSite(aoServer, siteName);
		com.aoindustries.aoserv.client.web.tomcat.Site hts=hs.getHttpdTomcatSite();
		if(hts==null) throw new IllegalArgumentException("Unable to find HttpdTomcatSite: "+siteName+" on "+aoServer);
		Context htc=hts.getHttpdTomcatContext(path);
		if(htc==null) throw new IllegalArgumentException("Unable to find HttpdTomcatContext: "+siteName+" on "+aoServer+" path='"+path+'\'');
		return htc.addHttpdTomcatParameter(
			name,
			value,
			override,
			description
		);
	}

	/**
	 * Adds a new {@link JkMount} to a {@link Site}.
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code>, <code>HttpdSite</code>,
	 *                                  or <code>HttpdTomcatSite</code>
	 *
	 * @see  HttpdTomcatSite#addHttpdTomcatSiteJkMount(java.lang.String, boolean)
	 */
	public int addHttpdTomcatSiteJkMount(
		String siteName,
		String aoServer,
		String path,
		boolean mount
	) throws IllegalArgumentException, IOException, SQLException {
		Site hs = getHttpdSite(aoServer, siteName);
		com.aoindustries.aoserv.client.web.tomcat.Site hts = hs.getHttpdTomcatSite();
		if(hts == null) throw new IllegalArgumentException("Unable to find HttpdTomcatSite: " + siteName + " on " + aoServer);
		return hts.addJkMount(path, mount);
	}

	/**
	 * Removes a {@link JkMount} from a {@link Site}.
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code>, <code>HttpdSite</code>,
	 *                                  or <code>HttpdTomcatSite</code>
	 *
	 * @see  HttpdTomcatSiteJkMount#remove()
	 */
	public void removeHttpdTomcatSiteJkMount(
		String siteName,
		String aoServer,
		String path
	) throws IllegalArgumentException, IOException, SQLException {
		Site hs = getHttpdSite(aoServer, siteName);
		com.aoindustries.aoserv.client.web.tomcat.Site hts = hs.getHttpdTomcatSite();
		if(hts == null) throw new IllegalArgumentException("Unable to find HttpdTomcatSite: " + siteName + " on " + aoServer);
		JkMount match = null;
		for(JkMount htsjm : hts.getJkMounts()) {
			if(htsjm.getPath().equals(path)) {
				match = htsjm;
				break;
			}
		}
		if(match == null) throw new IllegalArgumentException("Unable to find HttpdTomcatSiteJkMount: " + siteName + " on " + aoServer + " at " + path);
		match.remove();
	}

	/**
	 * Adds a new <code>HttpdTomcatSharedSite</code> to the system.  An <code>HttpdTomcatSharedSite</code> is
	 * an <code>HttpdSite</code> that uses a shared Tomcat servlet engine in a virtual-hosting configuration.  It
	 * hosts multiple sites per Java VM.
	 *
	 * @param  aoServer  the hostname of the <code>AOServer</code>
	 * @param  siteName  the name of the <code>HttpdTomcatSharedSite</code>
	 * @param  packageName  the name of the <code>Package</code>
	 * @param  jvmUsername  the username of the <code>LinuxAccount</code> that the Java VM
	 *					will run as
	 * @param  groupName  the name of the <code>LinuxGroup</code> that the web site will
	 *					be owned by
	 * @param  serverAdmin  the email address of the person who is responsible for the site
	 *					content and reliability
	 * @param  useApache  instructs the system to host static content, shtml, CGI, and PHP using Apache,
	 *					comes at the price of less request control through Tomcat
	 * @param  ipAddress  the <code>IPAddress</code> that the web site will bind to.  In
	 *					order for HTTP requests to succeed, <code>DNSRecord</code> entries
	 *					must point the hostnames of this <code>HttpdTomcatSharedSite</code> to this
	 *					<code>IPAddress</code>.  If <code>null</code>, the system will assign a
	 *                                      shared IP address.
	 * @param  primaryHttpHostname  the primary hostname of the <code>HttpdTomcatSharedSite</code> for the
	 *					HTTP protocol
	 * @param  altHttpHostnames  any number of alternate hostnames for the HTTP protocol or
	 *					<code>null</code> for none
	 * @param  sharedTomcatName   the shared Tomcat JVM under which this site runs
	 *
	 * @return  the <code>pkey</code> of the new <code>HttpdTomcatSharedSite</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find a referenced object or a
	 *					parameter is not in the right format
	 *
	 * @see  AOServer#addHttpdTomcatSharedSite
	 * @see  HttpdTomcatSharedSite
	 * @see  Site
	 * @see  com.aoindustries.aoserv.client.web.tomcat.Site
	 */
	public int addHttpdTomcatSharedSite(
		String aoServer,
		String siteName,
		AccountingCode packageName,
		UserId jvmUsername,
		GroupId groupName,
		Email serverAdmin,
		boolean useApache,
		InetAddress ipAddress,
		String netDevice,
		DomainName primaryHttpHostname,
		DomainName[] altHttpHostnames,
		String sharedTomcatName
	) throws IllegalArgumentException, SQLException, IOException {
		Server ao=getAOServer(aoServer);
		checkSiteName(siteName);

		IpAddress ip;
		if (netDevice!=null && (netDevice=netDevice.trim()).length()==0) netDevice=null;
		if (ipAddress!=null && netDevice!=null) {
			ip=getIPAddress(aoServer, netDevice, ipAddress);
		} else if(ipAddress==null && netDevice==null) {
			ip=null;
		} else {
			throw new IllegalArgumentException("ip_address and net_device must both be null or both be not null");
		}
		SharedTomcat sht = ao.getHttpdSharedTomcat(sharedTomcatName);
		if(sht == null) throw new IllegalArgumentException("Unable to find HttpdSharedTomcat: " + sharedTomcatName + " on " + aoServer);

		return ao.addHttpdTomcatSharedSite(
			siteName,
			getPackage(packageName),
			getLinuxServerAccount(aoServer, jvmUsername).getLinuxAccount(),
			getLinuxServerGroup(aoServer, groupName).getLinuxGroup(),
			serverAdmin,
			useApache,
			ip,
			primaryHttpHostname,
			altHttpHostnames,
			sharedTomcatName
		);
	}

	/**
	 * Adds a new <code>HttpdTomcatStdSite</code> to the system.  An <code>HttpdTomcatStdSite</code> is
	 * an <code>HttpdSite</code> that contains a Tomcat servlet engine in the standard configuration.  It
	 * only hosts one site per Java VM, but is arranged in the stock Tomcat structure and uses no
	 * special code.
	 *
	 * @param  aoServer  the hostname of the <code>AOServer</code>
	 * @param  siteName  the name of the <code>HttpdTomcatStdSite</code>
	 * @param  packageName  the name of the <code>Package</code>
	 * @param  jvmUsername  the username of the <code>LinuxAccount</code> that the Java VM
	 *					will run as
	 * @param  groupName  the name of the <code>LinuxGroup</code> that the web site will
	 *					be owned by
	 * @param  serverAdmin  the email address of the person who is responsible for the site
	 *					content and reliability
	 * @param  useApache  instructs the system to host static content, shtml, CGI, and PHP using Apache,
	 *					comes at the price of less request control through Tomcat
	 * @param  ipAddress  the <code>IPAddress</code> that the web site will bind to.  In
	 *					order for HTTP requests to succeed, <code>DNSRecord</code> entries
	 *					must point the hostnames of this <code>HttpdTomcatStdSite</code> to this
	 *					<code>IPAddress</code>.  If <code>null</code>, the system will assign a
	 *                                      shared IP address.
	 * @param  primaryHttpHostname  the primary hostname of the <code>HttpdTomcatStdSite</code> for the
	 *					HTTP protocol
	 * @param  altHttpHostnames  any number of alternate hostnames for the HTTP protocol or
	 *					<code>null</code> for none
	 * @param  tomcatVersion  the version number of <code>Tomcat</code> to install in the site
	 *
	 * @return  the <code>pkey</code> of the new <code>HttpdTomcatStdSite</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find a referenced object or a
	 *					parameter is not in the right format
	 *
	 * @see  AOServer#addHttpdTomcatStdSite
	 * @see  PrivateTomcatSite
	 * @see  Site
	 * @see  Site
	 */
	public int addHttpdTomcatStdSite(
		String aoServer,
		String siteName,
		AccountingCode packageName,
		UserId jvmUsername,
		GroupId groupName,
		Email serverAdmin,
		boolean useApache,
		InetAddress ipAddress,
		String netDevice,
		DomainName primaryHttpHostname,
		DomainName[] altHttpHostnames,
		String tomcatVersion
	) throws IllegalArgumentException, SQLException, IOException {
		Server ao=getAOServer(aoServer);
		checkSiteName(siteName);

		IpAddress ip;
		if (netDevice!=null && (netDevice=netDevice.trim()).length()==0) netDevice=null;
		if (ipAddress!=null && netDevice!=null) {
			ip=getIPAddress(aoServer, netDevice, ipAddress);
		} else if(ipAddress==null && netDevice==null) {
			ip=null;
		} else {
			throw new IllegalArgumentException("ip_address and net_device must both be null or both be not null");
		}
		return ao.addHttpdTomcatStdSite(
			siteName,
			getPackage(packageName),
			getLinuxServerAccount(aoServer, jvmUsername).getLinuxAccount(),
			getLinuxServerGroup(aoServer, groupName).getLinuxGroup(),
			serverAdmin,
			useApache,
			ip,
			primaryHttpHostname,
			altHttpHostnames,
			findTomcatVersion(ao, tomcatVersion)
		);
	}

	/**
	 * Adds an <code>EmailAddress</code> to a <code>LinuxAccount</code>.  Not all
	 * <code>LinuxAccount</code>s may be used as an email inbox.  The <code>LinuxAccountType</code>
	 * of the account determines which accounts may store email.  When email is allowed for the account,
	 * an <code>EmailAddress</code> is associated with the account as a <code>LinuxAccAddress</code>.
	 *
	 * @param  address  the part of the email address before the <code>@</code>
	 * @param  domain  the part of the email address after the <code>@</code>
	 * @param  aoServer  the hostname of the server storing the email account
	 * @param  username  the uesrname of the <code>LinuxAccount</code> to route the emails to
	 *
	 * @return  the pkey of the new LinuxAccAddress
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data
	 *					integrity violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>EmailDomain</code> or
	 *                                       <code>LinuxAccount</code>
	 *
	 * @see  LinuxServerAccount#addEmailAddress
	 * @see  InboxAddress
	 * @see  #addLinuxAccount
	 * @see  #addEmailDomain
	 * @see  Address
	 */
	public int addLinuxAccAddress(
		String address,
		DomainName domain,
		String aoServer,
		UserId username
	) throws IllegalArgumentException, IOException, SQLException {
		Domain sd=getEmailDomain(aoServer, domain);
		UserServer lsa=getLinuxServerAccount(aoServer, username);
		Address ea=sd.getEmailAddress(address);
		boolean added;
		if(ea==null) {
			ea=connector.getEmail().getEmailAddresses().get(sd.addEmailAddress(address));
			added=true;
		} else added=false;
		try {
			return lsa.addEmailAddress(ea);
		} catch(RuntimeException err) {
			if(added && !ea.isUsed()) ea.remove();
			throw err;
		}
	}

	/**
	 * Adds a new <code>LinuxAccount</code> the system.  A <code>LinuxAccount</code> does not
	 * grant access to any <code>Server</code>s, <code>addLinuxServerAccount</code> must be used
	 * after the <code>LinuxAccount</code> has been created.
	 *
	 * @param  username  the username of the new <code>LinuxAccount</code>
	 * @param  primary_group  the primary group of the new account
	 * @param  name  the account's full name
	 * @param  office_location  optional office location available via the Unix <code>finger</code> command
	 * @param  office_phone  optional phone number available via the Unix <code>finger</code> command
	 * @param  home_phone  optional home phone number available vie the Unix <code>finger</code> command
	 * @param  type  the <code>LinuxAccountType</code>
	 * @param  shell  the login <code>Shell</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if the name is not a valid format or unable to find
	 *					the <code>Username</code>, <code>LinuxAccountType</code>,
	 *					or <code>Shell</code>
	 *
	 * @see  Username#addLinuxAccount
	 * @see  #checkLinuxAccountName
	 * @see  #addUsername
	 * @see  #addLinuxServerAccount
	 * @see  User
	 * @see  UserType
	 * @see  UserServer
	 */
	public void addLinuxAccount(
		UserId username,
		GroupId primary_group,
		Gecos name,
		Gecos office_location,
		Gecos office_phone,
		Gecos home_phone,
		String type,
		UnixPath shell
	) throws IllegalArgumentException, IOException, SQLException {
		Username un=getUsername(username);
		Group lg=getLinuxGroup(primary_group);
		UserType lat=connector.getLinux().getLinuxAccountTypes().get(type);
		if(lat==null) throw new IllegalArgumentException("Unable to find LinuxAccountType: "+type);
		Shell sh=connector.getLinux().getShells().get(shell);
		if(sh==null) throw new IllegalArgumentException("Unable to find Shell: "+shell);
		un.addLinuxAccount(
			primary_group,
			name,
			office_location,
			office_phone,
			home_phone,
			type,
			shell
		);
	}

	/**
	 * Adds a <code>LinuxGroup</code> to the system.  After adding the <code>LinuxGroup</code>, the group
	 * may be added to a <code>Server</code> via a <code>LinuxServerGroup</code>.  Also, <code>LinuxAccount</code>s
	 * may be granted access to the group using <code>LinuxGroupAccount</code>.
	 *
	 * @param  name  the name of the new <code>LinuxGroup</code>
	 * @param  packageName  the name of the <code>Package</code> that the group belongs to
	 * @param  type  the <code>LinuxGroupType</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data
	 *					integrity violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Package</code> or
	 *					<code>LinuxGroupType</code>
	 *
	 * @see  Package#addLinuxGroup
	 * @see  Group
	 * @see  GroupType
	 * @see  Package
	 * @see  #addLinuxServerGroup
	 * @see  #addLinuxGroupAccount
	 */
	public void addLinuxGroup(
		GroupId name,
		AccountingCode packageName,
		String type
	) throws IllegalArgumentException, IOException, SQLException {
		GroupType lgt=connector.getLinux().getLinuxGroupTypes().get(type);
		if(lgt==null) throw new IllegalArgumentException("Unable to find LinuxGroupType: "+type);
		connector.getLinux().getLinuxGroups().addLinuxGroup(
			name,
			getPackage(packageName),
			type
		);
	}

	/**
	 * Once a <code>LinuxAccount</code> and a <code>LinuxGroup</code> have been established,
	 * permission for the <code>LinuxAccount</code> to access the <code>LinuxGroup</code> may
	 * be granted using a <code>LinuxGroupAccount</code>.
	 *
	 * @param  group  the name of the <code>LinuxGroup</code>
	 * @param  username  the username of the <code>LinuxAccount</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data
	 *					integrity violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>LinuxGroup</code> or
	 *					<code>LinuxAccount</code>
	 *
	 * @see  LinuxGroup#addLinuxAccount
	 * @see  GroupUser
	 * @see  Group
	 * @see  User
	 * @see  UserServer
	 * @see  GroupServer
	 */
	public int addLinuxGroupAccount(
		GroupId group,
		UserId username
	) throws IllegalArgumentException, IOException, SQLException {
		return getLinuxGroup(group).addLinuxAccount(getLinuxAccount(username));
	}

	/**
	 * Grants a <code>LinuxAccount</code> access to a <code>Server</code>.  The primary
	 * <code>LinuxGroup</code> for this account must already have a <code>LinuxServerGroup</code>
	 * for the <code>Server</code>.
	 *
	 * @param  username  the username of the <code>LinuxAccount</code>
	 * @param  aoServer  the hostname of the <code>AOServer</code>
	 * @param  home  the home directory of the user, typically <code>/home/<i>username</i></code>.
	 *                  If {@code null}, the {@link LinuxServerAccount#getDefaultHomeDirectory(com.aoindustries.aoserv.client.validator.UserId) default home directory} for <code>username</code>
	 *                  is used.
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data
	 *					integrity violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>LinuxAccount</code>, <code>Server</code>
	 *					or <code>AOServer</code>
	 *
	 * @see  LinuxAccount#addLinuxServerAccount
	 * @see  #addLinuxAccount
	 * @see  #addLinuxGroupAccount
	 * @see  #addLinuxServerGroup
	 * @see  Server
	 */
	public int addLinuxServerAccount(
		UserId username,
		String aoServer,
		UnixPath home
	) throws IllegalArgumentException, IOException, SQLException {
		User la=getLinuxAccount(username);
		Server ao=getAOServer(aoServer);
		if(home == null) home = UserServer.getDefaultHomeDirectory(username);
		return la.addLinuxServerAccount(ao, home);
	}

	/**
	 * Grants a <code>LinuxGroup</code> access to a <code>Server</code>.  If the group is
	 * the primary <code>LinuxGroup</code> for any <code>LinuxAccount</code> that will be
	 * added to the <code>Server</code>, the <code>LinuxGroup</code> must be added to the
	 * <code>Server</code> first via a <code>LinuxServerGroup</code>.
	 *
	 * @param  group  the name of the <code>LinuxGroup</code>
	 * @param  aoServer  the hostname of the <code>AOServer</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data
	 *					integrity violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>LinuxGroup</code> or
	 *					<code>Server</code>
	 *
	 * @see  LinuxGroup#addLinuxServerGroup
	 * @see  #addLinuxGroup
	 * @see  #addLinuxGroupAccount
	 * @see  Host
	 */
	public int addLinuxServerGroup(
		GroupId group,
		String aoServer
	) throws IllegalArgumentException, IOException, SQLException {
		return getLinuxGroup(group).addLinuxServerGroup(getAOServer(aoServer));
	}

	/**
	 * Adds a new <code>MajordomoList</code> to a <code>MajordomoServer</code>.
	 *
	 * @param  domain  the domain of the <code>MajordomoServer</code>
	 * @param  aoServer  the hostname of the <code>AOServer</code>
	 * @param  listName  the name of the new list
	 *
	 * @return  the pkey of the new <code>EmailList</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data
	 *					integrity violation occurs
	 * @exception  IllegalArgumentException  if the name is not valid or unable to find the
	 *                                  <code>Server</code>, code>EmailDomain</code>, or
	 *                                  <code>MajordomoServer</code>
	 *
	 * @see  MajordomoServer#addMajordomoList
	 * @see  #removeEmailList
	 */
	public int addMajordomoList(
		DomainName domain,
		String aoServer,
		String listName
	) throws IllegalArgumentException, IOException, SQLException {
		Domain ed=getEmailDomain(aoServer, domain);
		MajordomoServer ms=ed.getMajordomoServer();
		if(ms==null) throw new IllegalArgumentException("Unable to find MajordomoServer: "+domain+" on "+aoServer);
		checkMajordomoListName(listName);
		return ms.addMajordomoList(listName);
	}

	/**
	 * Adds a new <code>MajordomoServer</code> to an <code>EmailDomain</code>.
	 *
	 * @param  domain  the domain of the <code>EmailDomain</code>
	 * @param  aoServer  the hostname of the <code>AOServer</code>
	 * @param  linux_account  the username of the <code>LinuxAccount</code>
	 * @param  linux_group  the naem of the <code>LinuxGroup</code>
	 * @param  version  the version of the <code>MajordomoVersion</code>
	 *
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code>,
	 *                                  <code>EmailDomain</code>, <code>LinuxServerAccount</code>,
	 *                                  <code>LinuxServerGroup</code>, or <code>MajordomoVersion</code>
	 *
	 * @see  EmailDomain#addMajordomoServer
	 * @see  #removeMajordomoServer
	 */
	public void addMajordomoServer(
		DomainName domain,
		String aoServer,
		UserId linux_account,
		GroupId linux_group,
		String version
	) throws IllegalArgumentException, IOException, SQLException {
		Domain ed=getEmailDomain(aoServer, domain);
		MajordomoVersion mv=connector.getEmail().getMajordomoVersions().get(version);
		if(mv==null) throw new IllegalArgumentException("Unable to find MajordomoVersion: "+version);
		ed.addMajordomoServer(
			getLinuxServerAccount(aoServer, linux_account),
			getLinuxServerGroup(aoServer, linux_group),
			mv
		);
	}

	/**
	 * Adds a new <code>MySQLDatabase</code> to the system.  Once added, <code>MySQLUser</code>s may
	 * be granted access to the <code>MySQLDatabase</code> using a <code>MySQLDBUser</code>.
	 * <p>
	 * Because updates the the MySQL configurations are batched, the database may not be immediately
	 * created in the MySQL system.  To ensure the database is ready for use, call <code>waitForMySQLDatabaseRebuild</code>.
	 *
	 * @param  name  the name of the new database
	 * @param  aoServer  the hostname of the <code>AOServer</code>
	 * @param  packageName  the name of the <code>Package</code> that owns the database
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data
	 *					integrity violation occurs
	 * @exception  IllegalArgumentException  if the database name is not valid or unable to
	 *					find the <code>Server</code> or <code>Package</code>
	 *
	 * @see  MySQLServer#addMySQLDatabase
	 * @see  #checkMySQLDatabaseName
	 * @see  #addMySQLUser
	 * @see  #addMySQLServerUser
	 * @see  #addMySQLDBUser
	 * @see  #removeMySQLDatabase
	 * @see  #waitForMySQLDatabaseRebuild
	 */
	public int addMySQLDatabase(
		MySQLDatabaseName name,
		MySQLServerName mysqlServer,
		String aoServer,
		AccountingCode packageName
	) throws IllegalArgumentException, IOException, SQLException {
		return connector.getMysql().getMysqlDatabases().addMySQLDatabase(
			name,
			getMySQLServer(aoServer, mysqlServer),
			getPackage(packageName)
		);
	}

	/**
	 * Grants a <code>MySQLServerUser</code> permission to access a <code>MySQLDatabase</code>.
	 *
	 * @param  name  the name of the <code>MySQLDatabase</code>
	 * @param  aoServer  the hostname of the <code>AOServer</code>
	 * @param  username  the username of the <code>MySQLUser</code>
	 * @param  canSelect  grants the user <code>SELECT</code> privileges
	 * @param  canInsert  grants the user <code>INSERT</code> privileges
	 * @param  canUpdate  grants the user <code>UPDATE</code> privileges
	 * @param  canDelete  grants the user <code>DELETE</code> privileges
	 * @param  canCreate  grants the user <code>CREATE</code> privileges
	 * @param  canDrop  grants the user <code>DROP</code> privileges
	 * @param  canIndex  grants the user <code>INDEX</code> privileges
	 * @param  canAlter  grants the user <code>ALTER</code> privileges
	 * @param  canCreateTempTable  grants the user <code>CREATE TEMPORARY TABLE</code> privileges
	 * @param  canLockTables  grants the user <code>LOCK TABLE</code> privileges
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data
	 *					integrity violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code>,
	 *					<code>MySQLDatabase</code>, or <code>MySQLServerUser</code>
	 *
	 * @see  MySQLDatabase#addMySQLServerUser
	 * @see  #addMySQLUser
	 * @see  #addMySQLServerUser
	 * @see  #addMySQLDatabase
	 */
	public int addMySQLDBUser(
		MySQLDatabaseName name,
		MySQLServerName mysqlServer,
		String aoServer,
		MySQLUserId username,
		boolean canSelect,
		boolean canInsert,
		boolean canUpdate,
		boolean canDelete,
		boolean canCreate,
		boolean canDrop,
		boolean canReference,
		boolean canIndex,
		boolean canAlter,
		boolean canCreateTempTable,
		boolean canLockTables,
		boolean canCreateView,
		boolean canShowView,
		boolean canCreateRoutine,
		boolean canAlterRoutine,
		boolean canExecute,
		boolean canEvent,
		boolean canTrigger
	) throws IllegalArgumentException, IOException, SQLException {
		Database md=getMySQLDatabase(aoServer, mysqlServer, name);
		return md.addMySQLServerUser(
			getMySQLServerUser(aoServer, mysqlServer, username),
			canSelect,
			canInsert,
			canUpdate,
			canDelete,
			canCreate,
			canDrop,
			canReference,
			canIndex,
			canAlter,
			canCreateTempTable,
			canLockTables,
			canCreateView,
			canShowView,
			canCreateRoutine,
			canAlterRoutine,
			canExecute,
			canEvent,
			canTrigger
		);
	}

	/**
	 * Grants a <code>MySQLUser</code> access to a <code>Server</code> by adding a
	 * <code>MySQLServerUser</code>.
	 *
	 * @param  username  the username of the <code>MySQLUser</code>
	 * @param  aoServer  the hostname of the <code>AOServer</code>
	 * @param  host  the host the user is allowed to connect from, almost always
	 *					<code>MySQLHost.ANY_LOCAL_HOST</code> because the host limitation
	 *					is provided on a per-database level by <code>MySQLDBUser</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data
	 *					integrity violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>MySQLUser</code> or
	 *					<code>Server</code>
	 *
	 * @see  MySQLUser#addMySQLServerUser
	 * @see  MySQLServerUser#ANY_LOCAL_HOST
	 * @see  #addMySQLUser
	 * @see  #addMySQLDBUser
	 */
	public int addMySQLServerUser(
		MySQLUserId username,
		MySQLServerName mysqlServer,
		String aoServer,
		String host
	) throws IllegalArgumentException, IOException, SQLException {
		return getMySQLUser(username).addMySQLServerUser(getMySQLServer(aoServer, mysqlServer), host==null || host.length()==0?null:host);
	}

	/**
	 * Adds a <code>MySQLUser</code> to the system.  A <code>MySQLUser</code> does not
	 * exist on any <code>Server</code>, it merely indicates that a <code>Username</code>
	 * will be used for accessing a <code>MySQLDatabase</code>.  In order to grant
	 * the new <code>MySQLUser</code> access to a <code>MySQLDatabase</code>, first
	 * add a <code>MySQLServerUser</code> on the same <code>Server</code> as the
	 * <code>MySQLDatabase</code>, then add a <code>MySQLDBUser</code> granting
	 * permission to the <code>MySQLDatabase</code>.
	 *
	 * @param  username  the <code>Username</code> that will be used for accessing MySQL
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data
	 *					integrity violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Username</code>
	 *
	 * @see  Username#addMySQLUser
	 * @see  #addUsername
	 * @see  #addMySQLServerUser
	 * @see  #addMySQLDatabase
	 * @see  #addMySQLDBUser
	 * @see  User
	 */
	public void addMySQLUser(
		MySQLUserId username
	) throws IllegalArgumentException, IOException, SQLException {
		getUsername(username).addMySQLUser();
	}

	/**
	 * Adds a network bind to the system.
	 *
	 * @exception  IOException  if unable to access the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if unable to find a referenced object.
	 *
	 * @see  Server#addNetBind
	 */
	public int addNetBind(
		String server,
		AccountingCode packageName,
		InetAddress ipAddress,
		String net_device,
		Port port,
		String appProtocol,
		boolean monitoringEnabled,
		Set<FirewalldZoneName> firewalldZoneNames
	) throws IllegalArgumentException, SQLException, IOException {
		IpAddress ia=getIPAddress(server, net_device, ipAddress);
		AppProtocol appProt=connector.getNet().getProtocols().get(appProtocol);
		if(appProt==null) throw new IllegalArgumentException("Unable to find Protocol: "+appProtocol);
		return getServer(server).addNetBind(
			getPackage(packageName),
			ia,
			port,
			appProt,
			monitoringEnabled,
			firewalldZoneNames
		);
	}

	/**
	 * Whenever a credit card transaction fails, or when an account has not been paid for
	 * over month, the billing contact for the <code>Business</code> is notified.  The details
	 * of this notification are logged as a <code>NoticeLog</code>.
	 *
	 * @param  accounting  the accounting code of the <code>Business</code>
	 * @param  billingContact  the name of the person who was contacted
	 * @param  emailAddress  the email address that the email was sent to
	 * @param  balance  their account balance at the time the notification was sent
	 * @param  type  the <code>NoticeType</code>
	 * @param  transid  the transaction ID associated with this notification or
	 *					<code>NoticeLog.NO_TRANSACTION</code> for none
	 *
	 * @exception  IOException  if unable to access the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if unable to find the <code>Business</code>,
	 *					<code>NoticeType</code>, or <code>Transaction</code>.
	 *
	 * @see  Business#addNoticeLog
	 * @see  NoticeType
	 * @see  Account
	 * @see  Transaction
	 */
	public void addNoticeLog(
		AccountingCode accounting,
		String billingContact,
		String emailAddress,
		BigDecimal balance,
		String type,
		int transid
	) throws IllegalArgumentException, IOException, SQLException {
		Account bu=getBusiness(accounting);
		NoticeType nt=connector.getBilling().getNoticeTypes().get(type);
		if(nt==null) throw new IllegalArgumentException("Unable to find NoticeType: "+type);
		if(transid!=NoticeLog.NO_TRANSACTION) {
			Transaction trans=connector.getBilling().getTransactions().get(transid);
			if(trans==null) throw new IllegalArgumentException("Unable to find Transaction: "+transid);
		}
		connector.getBilling().getNoticeLogs().addNoticeLog(
			accounting,
			billingContact,
			emailAddress,
			balance,
			type,
			transid
		);
	}

	/**
	 * Each <code>Business</code> can have multiple <code>Package</code>s associated with it.
	 * Each <code>Package</code> is an allotment of resources with a monthly charge.
	 * <p>
	 * To determine if this connection can set prices:
	 * <pre>
	 * SimpleAOClient client=new SimpleAOClient();
	 *
	 * boolean canSetPrices=client
	 *     .getConnector()
	 *     .getThisBusinessAdministrator()
	 *     .getUsername()
	 *     .getPackage()
	 *     .getBusiness()
	 *     .canSetPrices();
	 * </pre>
	 *
	 * @param  packageName  the name for the new package
	 * @param  accounting  the accounting code of the <code>Business</code>
	 * @param  packageDefinition  the unique identifier of the <code>PackageDefinition</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if unable to find 
	 *
	 * @see  #checkPackageName
	 * @see  #addBusiness
	 * @see  PackageDefinition
	 */
	public int addPackage(
		AccountingCode packageName,
		AccountingCode accounting,
		int packageDefinition
	) throws IllegalArgumentException, IOException, SQLException {
		Account business=getBusiness(accounting);
		PackageDefinition pd=getPackageDefinition(packageDefinition);

		return business.addPackage(packageName, pd);
	}

	/**
	 * Adds a new <code>PostgresDatabase</code> to the system.
	 *
	 * Because updates the the PostgreSQL configurations are batched, the database may not be immediately
	 * created in the PostgreSQL system.  To ensure the database is ready for use, call
	 * <code>waitForPostgresDatabaseRebuild</code>.
	 *
	 * @param  name  the name of the new database
	 * @param  aoServer  the hostname of the <code>Server</code>
	 * @param  datdba  the username of the <code>PostgresServerUser</code> who owns the database
	 * @param  encoding  the encoding of the database
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data
	 *					integrity violation occurs
	 * @exception  IllegalArgumentException  if the database name is not valid or unable to
	 *					find the <code>Server</code>, <code>PostgresUser</code>,
	 *					<code>PostgresServerUser</code>, or <code>PostgresEncoding</code>
	 *
	 * @see  PostgresServer#addPostgresDatabase
	 * @see  #checkPostgresDatabaseName
	 * @see  #addPostgresUser
	 * @see  #addPostgresServerUser
	 * @see  #removePostgresDatabase
	 * @see  #waitForPostgresDatabaseRebuild
	 * @see  Encoding
	 */
	public int addPostgresDatabase(
		PostgresDatabaseName name,
		PostgresServerName postgres_server,
		String aoServer,
		PostgresUserId datdba,
		String encoding,
		boolean enablePostgis
	) throws IllegalArgumentException, SQLException, IOException {
		com.aoindustries.aoserv.client.postgresql.UserServer psu=getPostgresServerUser(aoServer, postgres_server, datdba);
		com.aoindustries.aoserv.client.postgresql.Server ps=psu.getPostgresServer();
		Version pv=ps.getVersion();
		Encoding pe=pv.getPostgresEncoding(connector, encoding);
		if(pe==null) throw new IllegalArgumentException("Unable to find PostgresEncoding for PostgresVersion "+pv.getTechnologyVersion(connector).getVersion()+": "+encoding);
		if(enablePostgis && pv.getPostgisVersion(connector)==null) throw new IllegalArgumentException("Unable to enable PostGIS, PostgresVersion "+pv.getTechnologyVersion(connector).getVersion()+" doesn't support PostGIS");
		return ps.addPostgresDatabase(
			name,
			psu,
			pe,
			enablePostgis
		);
	}

	/**
	 * Grants a <code>PostgresUser</code> access to a <code>Server</code> by adding a
	 * <code>PostgresServerUser</code>.
	 *
	 * @param  username  the username of the <code>PostgresUser</code>
	 * @param  postgresServer  the name of the PostgreSQL server
	 * @param  aoServer  the hostname of the <code>Server</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data
	 *					integrity violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>PostgresUser</code> or
	 *					<code>Server</code>
	 *
	 * @see  PostgresUser#addPostgresServerUser
	 * @see  #addPostgresUser
	 */
	public int addPostgresServerUser(
		PostgresUserId username,
		PostgresServerName postgresServer,
		String aoServer
	) throws IllegalArgumentException, IOException, SQLException {
		return getPostgresUser(username).addPostgresServerUser(getPostgresServer(aoServer, postgresServer));
	}

	/**
	 * Adds a <code>PostgresUser</code> to the system.  A <code>PostgresUser</code> does not
	 * exist on any <code>Server</code>, it merely indicates that a <code>Username</code>
	 * will be used for accessing a <code>PostgresDatabase</code>.  In order to grant
	 * the new <code>PostgresUser</code> access to a <code>PostgresDatabase</code>, first
	 * add a <code>PostgresServerUser</code> on the same <code>Server</code> as the
	 * <code>PostgresDatabase</code>, then use the PostgreSQL <code>grant</code> and
	 * <code>revoke</code> commands.
	 *
	 * @param  username  the <code>Username</code> that will be used for accessing PostgreSQL
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data
	 *					integrity violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Username</code>
	 *
	 * @see  Username#addPostgresUser
	 * @see  #addUsername
	 * @see  #addPostgresServerUser
	 * @see  #addPostgresDatabase
	 * @see  User
	 */
	public void addPostgresUser(
		PostgresUserId username
	) throws IllegalArgumentException, IOException, SQLException {
		getUsername(username).addPostgresUser();
	}

	/**
	 * Adds a new <code>EmailDomain</code> to a <code>Server</code>.  Once added, the <code>Server</code>
	 * will accept email for the provided domain.  In order for the email to function, however, a DNS
	 * <code>MX</code> entry for the domain must point to a hostname that resolves to an
	 * <code>IPAddress</code> on the <code>Server</code>.
	 *
	 * @param  domain  the email domain that will be hosted
	 * @param  aoServer  the hostname of the <code>Server</code> that is being added
	 * @param  packageName  the name of the <code>Package</code> that owns the email domain
	 *
	 * @exception  IOException  if unable to access the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if the domain is not in the correct format or
	 *					unable to find the <code>Package</code>
	 *
	 * @see  AOServer#addEmailDomain
	 * @see  #addDNSRecord
	 * @see  #addEmailForwarding
	 * @see  #addEmailListAddress
	 * @see  #addEmailPipeAddress
	 * @see  #addLinuxAccAddress
	 */
	public int addEmailDomain(
		DomainName domain,
		String aoServer,
		AccountingCode packageName
	) throws IllegalArgumentException, IOException, SQLException {
		return getAOServer(aoServer).addEmailDomain(domain, getPackage(packageName));
	}

	/**
	 * Grants access to the SMTP server.  Access to the SMTP server is granted when an
	 * email client successfully logs into either the IMAP or POP3 servers.  If desired,
	 * access to the SMTP server may also be granted from the API.  In either case,
	 * the SMTP access will be revoked after 24 hours unless refresh.
	 *
	 * @param  packageName  the name of the <code>Package</code> that is granted access
	 * @param  aoServer  the hostname of the <code>AOServer</code>
	 * @param  host  the hostname or IP address that is being configured
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if the IP address is for valid or unable to
	 *					find the <code>Package</code> or <code>Server</code>
	 *
	 * @see  Package#addEmailSmtpRelay
	 */
	public int addEmailSmtpRelay(
		AccountingCode packageName,
		String aoServer,
		HostAddress host,
		String type,
		long duration
	) throws IllegalArgumentException, SQLException, IOException {
		Server ao;
		if(aoServer!=null && (aoServer=aoServer.trim()).length()==0) aoServer=null;
		if(aoServer==null) ao=null;
		else ao=getAOServer(aoServer);
		SmtpRelayType esrt=connector.getEmail().getEmailSmtpRelayTypes().get(type);
		if(esrt==null) throw new SQLException("Unable to find EmailSmtpRelayType: "+type);

		return getPackage(packageName).addEmailSmtpRelay(ao, host, esrt, duration);
	}

	/**
	 * Adds a <code>SpamEmailMessage</code>.
	 *
	 * @return  the pkey of the <code>SpamEmailMessage</code> that was created
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to the <code>EmailSmtpRelay</code>
	 *
	 * @see  EmailSmtpRelay#addSpamEmailMessage
	 */
	public int addSpamEmailMessage(
		int email_relay,
		String message
	) throws IllegalArgumentException, IOException, SQLException {
		SmtpRelay esr=connector.getEmail().getEmailSmtpRelays().get(email_relay);
		if(esr==null) throw new IllegalArgumentException("Unable to find EmailSmtpRelay: "+email_relay);
		return esr.addSpamEmailMessage(message);
	}

	/**
	 * Adds a new support request <code>Ticket</code> to the system.
	 *
	 * @param  accounting  the name of the <code>Business</code> that the support
	 *                      request relates to
	 * @param  business_administrator  the person to contact regarding the ticket
	 * @param  ticket_type  the <code>TicketType</code>
	 * @param  details  the content of the <code>Ticket</code>
	 * @param  deadline  the requested deadline for ticket completion or
	 *					<code>Ticket.NO_DEADLINE</code> for none
	 * @param  client_priority  the priority assigned by the client
	 * @param  admin_priority  the priority assigned by the ticket administrator
	 * @param  technology  the <code>TechnologyName</code> that this <code>Ticket</code>
	 *					relates to or <code>null</code> for none
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Package</code>,
	 *					<code>BusinessAdministrator</code>, <code>TicketType</code>,
	 *					client <code>TicketPriority</code>, admin <code>TicketPriority</code>,
	 *					or <code>TechnologyName</code>
	 *
	 * @see  BusinessAdministrator#addTicket(Business,TicketType,String,long,TicketPriority,TicketPriority,TechnologyName,BusinessAdministrator,String,String)
	 * @see  BusinessAdministrator#isActiveTicketAdmin
	 * @see  Action
	 * @see  Package
	 * @see  TechnologyName
	 * @see  Ticket
	 * @see  TicketPriority
	 * @see  TicketType
	 */
	/*
	public int addTicket(
		String accounting,
		String language,
		String category,
		String ticketType,
		String summary,
		String details,
		String clientPriority,
		String contactEmails,
		String contactPhoneNumbers
	) throws IllegalArgumentException, IOException, SQLException {
		return connector.getTickets().addTicket(
			(accounting==null || accounting.length()==0) ? null : getBusiness(accounting),
			getLanguage(language),
			(category==null || category.length()==0) ? null : getTicketCategory(category),
			getTicketType(ticketType),
			summary,
			(details==null || details.length()==0) ? null : details,
			getTicketPriority(clientPriority),
			contactEmails,
			contactPhoneNumbers
		);
	}*/

	/**
	 * Adds a work entry to a <code>Ticket</code> when a <code>Ticket</code> is worked on,
	 * but not completed.
	 *
	 * @param  ticket_id  the pkey of the <code>Ticket</code>
	 * @param  business_administrator  the username of the <code>BusinessAdministrator</code>
	 *					making the change
	 * @param  comments  the details of their work
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Ticket</code> or
	 *					<code>BusinessAdministrator</code>
	 *
	 * @see  Ticket#actWorkEntry
	 * @see  #addTicket
	 * @see  Action
	 */
	/*
	public void addTicketWork(
		int ticket_id,
		String business_administrator,
		String comments
	) throws IllegalArgumentException, IOException, SQLException {
		Ticket ti=connector.getTickets().get(ticket_id);
		if(ti==null) throw new IllegalArgumentException("Unable to find Ticket: "+ticket_id);
		BusinessAdministrator pe=connector.getBusinessAdministrators().get(business_administrator);
		if(pe==null) throw new IllegalArgumentException("Unable to find BusinessAdministrator: "+business_administrator);
		ti.actWorkEntry(pe, comments);
	}*/

	/**
	 * Adds a new <code>Transaction</code> to a <code>Business</code>.
	 *
	 * @param  business  the accounting code of the <code>Business</code>
	 * @param  source_business  the accounting code of the originating <code>Business</code>
	 * @param  business_administrator  the username of the <code>BusinessAdministrator</code> making
	 *					this <code>Transaction</code>
	 * @param  type  the type as found in <code>Rate</code>
	 * @param  description  the description
	 * @param  quantity  the quantity in thousandths
	 * @param  rate  the rate in hundredths
	 * @param  paymentType
	 * @param  paymentInfo
	 * @param  processor
	 * @param  payment_confirmed  the confirmation status of the transaction
	 *
	 * @return  the transid of the new <code>Transaction</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code>,
	 *					<code>Business</code>, <code>BusinessAdministrator</code>, <code>Rate</code>,
	 *					<code>PaymentType</code>, or <code>payment_confirmed</code>
	 *
	 * @see  Business#addTransaction
	 * @see  Transaction
	 * @see  #addBusiness
	 * @see  Account
	 * @see  #addBusinessAdministrator
	 * @see  Administrator
	 * @see  TransactionType
	 */
	public int addTransaction(
		AccountingCode business,
		AccountingCode source_business,
		UserId business_administrator,
		String type,
		String description,
		int quantity,
		int rate,
		String paymentType,
		String paymentInfo,
		String processor,
		byte payment_confirmed
	) throws IllegalArgumentException, IOException, SQLException {
		Account bu=getBusiness(business);
		Account sourceBU=getBusiness(source_business);
		Administrator pe=connector.getAccount().getBusinessAdministrators().get(business_administrator);
		if(pe==null) throw new IllegalArgumentException("Unable to find BusinessAdministrator: "+business_administrator);
		TransactionType tt=connector.getBilling().getTransactionTypes().get(type);
		if(tt==null) throw new IllegalArgumentException("Unable to find TransactionType: "+type);
		PaymentType pt;
		if(paymentType==null || paymentType.length()==0) pt=null;
		else {
			pt=connector.getPayment().getPaymentTypes().get(paymentType);
			if(pt==null) throw new IllegalArgumentException("Unable to find PaymentType: "+paymentType);
		}
		if(paymentInfo!=null && paymentInfo.length()==0) paymentInfo=null;
		Processor ccProcessor;
		if(processor==null || processor.length()==0) ccProcessor=null;
		else {
			ccProcessor = connector.getPayment().getCreditCardProcessors().get(processor);
			if(ccProcessor==null) throw new IllegalArgumentException("Unable to find CreditCardProcessor: "+processor);
		}
		return bu.addTransaction(
			sourceBU,
			pe,
			tt,
			description,
			quantity,
			rate,
			pt,
			paymentInfo,
			ccProcessor,
			payment_confirmed
		);
	}

	/**
	 * Adds a new <code>Username</code> to a <code>Package</code>.  A username is unique to the
	 * system, regardless of which service(s) it is used for.  For example, if a username is
	 * allocated for use as a MySQL user for business A, business B may not use the username as
	 * a PostgreSQL user.
	 *
	 * @param  packageName  the name of the <code>Package</code> that owns the <code>Username</code>
	 * @param  username  the username to add
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if the username is not a valid username or
	 *					unable to find the <code>Package</code>
	 *
	 * @see  Package#addUsername
	 * @see  Username
	 * @see  #addPackage
	 * @see  Package
	 */
	public void addUsername(
		AccountingCode packageName,
		UserId username
	) throws IllegalArgumentException, IOException, SQLException {
		getPackage(packageName).addUsername(username);
	}

	/**
	 * Determines if a <code>LinuxAccount</code> currently has passwords set.
	 *
	 * @param  username  the username of the account
	 *
	 * @return  an <code>int</code> containing <code>PasswordProtected.NONE</code>,
	 *          <code>PasswordProtected.SOME</code>, or <code>PasswordProtected.ALL</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if the <code>LinuxAccount</code> is not found
	 *
	 * @see  LinuxAccount#arePasswordsSet
	 * @see  #setLinuxAccountPassword
	 * @see  User
	 * @see  PasswordProtected
	 */
	public int areLinuxAccountPasswordsSet(
		UserId username
	) throws IllegalArgumentException, IOException, SQLException {
		return getLinuxAccount(username).arePasswordsSet();
	}

	/**
	 * Determines if a <code>MySQLUser</code> currently has passwords set.
	 *
	 * @param  username  the username of the user
	 *
	 * @return  an <code>int</code> containing <code>PasswordProtected.NONE</code>,
	 *          <code>PasswordProtected.SOME</code>, or <code>PasswordProtected.ALL</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if the <code>MySQLUser</code> is not found
	 *
	 * @see  MySQLUser#arePasswordsSet
	 * @see  #setMySQLUserPassword
	 * @see  User
	 * @see  PasswordProtected
	 */
	public int areMySQLUserPasswordsSet(
		MySQLUserId username
	) throws IllegalArgumentException, IOException, SQLException {
		return getMySQLUser(username).arePasswordsSet();
	}

	/**
	 * Determines if a <code>PostgresUser</code> currently has passwords set.
	 *
	 * @param  username  the username of the user
	 *
	 * @return  an <code>int</code> containing <code>PasswordProtected.NONE</code>,
	 *          <code>PasswordProtected.SOME</code>, or <code>PasswordProtected.ALL</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if the <code>PostgresUser</code> is not found
	 *
	 * @see  PostgresUser#arePasswordsSet
	 * @see  #setPostgresUserPassword
	 * @see  User
	 * @see  PasswordProtected
	 */
	public int arePostgresUserPasswordsSet(
		PostgresUserId username
	) throws IllegalArgumentException, IOException, SQLException {
		return getPostgresUser(username).arePasswordsSet();
	}

	/**
	 * Determines if a <code>Username</code> currently has passwords set.
	 *
	 * @param  username  the username
	 *
	 * @return  an <code>int</code> containing <code>PasswordProtected.NONE</code>,
	 *          <code>PasswordProtected.SOME</code>, or <code>PasswordProtected.ALL</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if the <code>Username</code> is not found
	 *
	 * @see  Username#arePasswordsSet
	 * @see  #setUsernamePassword
	 * @see  Username
	 * @see  PasswordProtected
	 */
	public int areUsernamePasswordsSet(
		UserId username
	) throws IllegalArgumentException, IOException, SQLException {
		return getUsername(username).arePasswordsSet();
	}

	/**
	 * Bounces a <code>Ticket</code>.
	 *
	 * @param  ticket_id  the pkey of the <code>Ticket</code>
	 * @param  business_administrator  the username of the <code>BusinessAdministrator</code>
	 *					making the change
	 * @param  comments  the details of the bounce
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Ticket</code> or
	 *					<code>BusinessAdministrator</code>
	 *
	 * @see  Ticket#actBounceTicket
	 * @see  #addTicket
	 * @see  Action
	 */
	/*
	public void bounceTicket(
		int ticket_id,
		String business_administrator,
		String comments
	) throws IllegalArgumentException, IOException, SQLException {
		Ticket ti=connector.getTickets().get(ticket_id);
		if(ti==null) throw new IllegalArgumentException("Unable to find Ticket: "+ticket_id);
		BusinessAdministrator pe=connector.getBusinessAdministrators().get(business_administrator);
		if(pe==null) throw new IllegalArgumentException("Unable to find BusinessAdministrator: "+business_administrator);
		ti.actBounceTicket(pe, comments);
	}*/

	/**
	 * Cancels a <code>Business</code>.  The <code>Business</code> must already be disabled.
	 *
	 * @param  accounting  the accounting code of the business
	 * @param  reason  the reason the account is being canceled
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Business</code>
	 *
	 * @see  Business#cancel
	 */
	public void cancelBusiness(
		AccountingCode accounting,
		String reason
	) throws IllegalArgumentException, IOException, SQLException {
		getBusiness(accounting).cancel(reason);
	}

	/**
	 * Changes the administrative priority of a <code>Ticket</code>.
	 *
	 * @param  ticket_id  the pkey of the <code>Ticket</code>
	 * @param  priority  the new <code>TicketPriority</code>
	 * @param  business_administrator  the username of the <code>BusinessAdministrator</code>
	 *					making the change
	 * @param  comments  the details of the change
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Ticket</code>,
	 *					<code>BusinessAdministrator</code>, or <code>TicketPriority</code>
	 *
	 * @see  Ticket#actChangeAdminPriority
	 * @see  #addTicket
	 * @see  TicketPriority
	 * @see  Action
	 */
	/*
	public void changeTicketAdminPriority(
		int ticket_id,
		String priority,
		String business_administrator,
		String comments
	) throws IllegalArgumentException, IOException, SQLException {
		Ticket ti=connector.getTickets().get(ticket_id);
		if(ti==null) throw new IllegalArgumentException("Unable to find Ticket: "+ticket_id);
		TicketPriority pr;
		if(priority==null || priority.length()==0) {
			pr=null;
		} else {
			pr=connector.getTicketPriorities().get(priority);
			if(pr==null) throw new IllegalArgumentException("Unable to find TicketPriority: "+priority);
		}
		BusinessAdministrator pe=connector.getBusinessAdministrators().get(business_administrator);
		if(pe==null) throw new IllegalArgumentException("Unable to find BusinessAdministrator: "+business_administrator);
		ti.actChangeAdminPriority(pr, pe, comments);
	}*/

	/**
	 * Changes the client's priority of a <code>Ticket</code>.
	 *
	 * @param  ticket_id  the pkey of the <code>Ticket</code>
	 * @param  priority  the new <code>TicketPriority</code>
	 * @param  business_administrator  the username of the <code>BusinessAdministrator</code>
	 *					making the change
	 * @param  comments  the details of the change
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Ticket</code>,
	 *					<code>BusinessAdministrator</code>, or <code>TicketPriority</code>
	 *
	 * @see  Ticket#actChangeClientPriority
	 * @see  #addTicket
	 * @see  TicketPriority
	 * @see  Action
	 */
	/*
	public void changeTicketClientPriority(
		int ticket_id,
		String priority,
		String business_administrator,
		String comments
	) throws IllegalArgumentException, IOException, SQLException {
		Ticket ti=connector.getTickets().get(ticket_id);
		if(ti==null) throw new IllegalArgumentException("Unable to find Ticket: "+ticket_id);
		TicketPriority pr=connector.getTicketPriorities().get(priority);
		if(pr==null) throw new IllegalArgumentException("Unable to find TicketPriority: "+priority);
		BusinessAdministrator pe=connector.getBusinessAdministrators().get(business_administrator);
		if(pe==null) throw new IllegalArgumentException("Unable to find BusinessAdministrator: "+business_administrator);
		ti.actChangeClientPriority(pr, pe, comments);
	}*/

	/**
	 * Changes the <code>TicketType</code> of a <code>Ticket</code>.
	 *
	 * @param  ticket_id  the pkey of the <code>Ticket</code>
	 * @param  type  the name of the new <code>TicketType</code>
	 * @param  business_administrator  the username of the <code>BusinessAdministrator</code>
	 *					making the change
	 * @param  comments  the details of the change
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Ticket</code>,
	 *					<code>TicketType</code>, or <code>BusinessAdministrator</code>
	 *
	 * @see  Ticket#actChangeTicketType
	 * @see  TicketType
	 * @see  #addTicket
	 * @see  TicketPriority
	 * @see  Action
	 */
	/*
	public void changeTicketType(
		int ticket_id,
		String type,
		String business_administrator,
		String comments
	) throws IllegalArgumentException, IOException, SQLException {
		Ticket ti=connector.getTickets().get(ticket_id);
		if(ti==null) throw new IllegalArgumentException("Unable to find Ticket: "+ticket_id);
		TicketType tt=connector.getTicketTypes().get(type);
		if(tt==null) throw new IllegalArgumentException("Unable to find TicketType: "+type);
		BusinessAdministrator pe=connector.getBusinessAdministrators().get(business_administrator);
		if(pe==null) throw new IllegalArgumentException("Unable to find BusinessAdministrator: "+business_administrator);
		ti.actChangeTicketType(tt, pe, comments);
	}*/

	/**
	 * Checks the strength of a password that will be used for
	 * a <code>BusinessAdministrator</code>.
	 *
	 * @param  username  the username of the <code>BusinessAdministrator</code> whos
	 *					password will be set
	 * @param  password  the new password
	 *
	 * @return  a description of why the password is weak or <code>null</code>
	 *          if all checks succeed
	 *
	 * @see  #setBusinessAdministratorPassword
	 * @see  BusinessAdministrator#checkPassword
	 */
	public static List<PasswordChecker.Result> checkBusinessAdministratorPassword(
		UserId username,
		String password
	) throws IOException, SQLException {
		return Administrator.checkPassword(username, password);
	}

	/**
	 * Checks the format of a <code>DNSZone</code>.
	 *
	 * @param  zone  the new DNS zone name, some examples include <code>aoindustries.com.</code>
	 *					and <code>netspade.co.uk.</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if the format is not valid
	 *
	 * @see  DNSZoneTable#checkDNSZone
	 * @see  Zone
	 */
	public void checkDNSZone(
		String zone
	) throws IllegalArgumentException, IOException, SQLException {
		if(!connector.getDns().getDnsZones().checkDNSZone(zone)) throw new IllegalArgumentException("Invalid DNS zone: "+zone);
	}

	/**
	 * Checks the format of an email list path.
	 *
	 * @param  aoServer  the hostname of the server the list would be hosted on
	 * @param  path  the path of the list
	 *
	 * @exception  IllegalArgumentException  if the name is not in a valid format
	 *
	 * @see  EmailList#isValidRegularPath
	 */
	public void checkEmailListPath(
		String aoServer,
		UnixPath path
	) throws IllegalArgumentException, IOException, SQLException {
		Server ao = getAOServer(aoServer);
		if(
			!com.aoindustries.aoserv.client.email.List.isValidRegularPath(
				path,
				ao.getServer().getOperatingSystemVersion_id()
			)
		) throw new IllegalArgumentException("Invalid EmailList path: " + path + " on " + ao);
	}

	/**
	 * Checks the strength of a password that will be used for a <code>LinuxAccount</code> or
	 * <code>LinuxServerAccount</code>.
	 *
	 * @param  username  the username of the account that will have its password set
	 * @param  password  the new password for the account
	 *
	 * @return  a <code>String</code> describing why the password is not secure or <code>null</code>
	 *					if the password is strong
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>LinuxAccount</code>
	 *
	 * @see  LinuxAccount#checkPassword
	 * @see  #setLinuxAccountPassword
	 * @see  #setLinuxServerAccountPassword
	 * @see  PasswordChecker
	 */
	public List<PasswordChecker.Result> checkLinuxAccountPassword(
		UserId username,
		String password
	) throws IllegalArgumentException, IOException, SQLException {
		return getLinuxAccount(username).checkPassword(password);
	}

	/**
	 * Checks the strength of a password that will be used for
	 * a <code>MySQLServerUser</code>.
	 *
	 * @param  username  the username of the <code>MySQLServerUser</code> whos
	 *					password will be set
	 * @param  password  the new password
	 *
	 * @return  a description of why the password is weak or <code>null</code>
	 *          if all checks succeed
	 *
	 * @exception  IOException  if unable to load the dictionary resource
	 *
	 * @see  #setMySQLUserPassword
	 * @see  #setMySQLServerUserPassword
	 * @see  MySQLUser#checkPassword
	 */
	public static List<PasswordChecker.Result> checkMySQLPassword(
		MySQLUserId username,
		String password
	) throws IOException {
		return com.aoindustries.aoserv.client.mysql.User.checkPassword(username, password);
	}

	/**
	 * Checks the strength of a password that will be used for
	 * a <code>PostgresServerUser</code>.
	 *
	 * @param  username  the username of the <code>PostgresServerUser</code> whos
	 *					password will be set
	 * @param  password  the new password
	 *
	 * @return  a description of why the password is weak or <code>null</code>
	 *          if all checks succeed
	 *
	 * @exception  IOException  if unable to load the dictionary resource
	 *
	 * @see  #setPostgresUserPassword
	 * @see  #setPostgresServerUserPassword
	 * @see  PostgresUser#checkPassword
	 */
	public static List<PasswordChecker.Result> checkPostgresPassword(
		PostgresUserId username,
		String password
	) throws IOException {
		return com.aoindustries.aoserv.client.postgresql.User.checkPassword(username, password);
	}

	/**
	 * Checks the format of a Majordomo list name.
	 *
	 * @exception  IllegalArgumentException  if the domain is not in a valid format
	 *
	 * @see  MajordomoList#isValidListName
	 * @see  #addMajordomoList
	 */
	public static void checkMajordomoListName(
		String listName
	) throws IllegalArgumentException {
		if(!MajordomoList.isValidListName(listName)) throw new IllegalArgumentException("Invalid Majordomo list name: "+listName);
	}

	/**
	 * Checks the format of an <code>HttpdSharedTomcat</code> name.
	 *
	 * @param  tomcatName  the name of the <code>HttpdSharedTomcat</code>
	 *
	 * @exception  IllegalArgumentException  if the name is not in a valid format
	 *
	 * @see  HttpdSharedTomcat#isValidSharedTomcatName
	 * @see  #addHttpdSharedTomcat
	 * @see  #addHttpdTomcatSharedSite
	 */
	public static void checkSharedTomcatName(
		String tomcatName
	) throws IllegalArgumentException {
		if(!SharedTomcat.isValidSharedTomcatName(tomcatName)) throw new IllegalArgumentException("Invalid shared Tomcat name: "+tomcatName);
	}

	/**
	 * Checks the format of an <code>HttpdSite</code> name.
	 *
	 * @param  siteName  the name of the <code>HttpdSite</code>
	 *
	 * @exception  IllegalArgumentException  if the name is not in a valid format
	 *
	 * @see  HttpdSite#isValidSiteName
	 * @see  #addHttpdTomcatStdSite
	 */
	public static void checkSiteName(
		String siteName
	) throws IllegalArgumentException {
		if(!Site.isValidSiteName(siteName)) throw new IllegalArgumentException("Invalid site name: "+siteName);
	}

	/**
	 * Checks the strength of a password that will be used for
	 * a <code>Username</code>.  The strength requirement is based on
	 * which services use the <code>Username</code>.
	 *
	 * @param  username  the username whos password will be set
	 * @param  password  the new password
	 *
	 * @return  a description of why the password is weak or <code>null</code>
	 *          if all checks succeed
	 *
	 * @exception  IOException  if unable to load the dictionary resource or unable to access the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if unable to find the <code>Username</code>
	 *
	 * @see  #setUsernamePassword
	 * @see  Username#checkPassword
	 */
	public List<PasswordChecker.Result> checkUsernamePassword(
		UserId username,
		String password
	) throws IllegalArgumentException, IOException, SQLException {
		return getUsername(username).checkPassword(password);
	}

	/**
	 * Checks if a password matches a <code>LinuxServerAccount</code>.
	 *
	 * @param  username  the username of the account
	 * @param  aoServer  the hostname of the server to check
	 * @param  password  the password to compare against
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if the <code>LinuxAccount</code>, <code>Server</code>,
	 *                                  <code>AOServer</code>, or <code>LinuxServerAccount</code> is not found
	 *
	 * @see  LinuxServerAccount#passwordMatches
	 * @see  #addLinuxServerAccount
	 */
	public boolean compareLinuxServerAccountPassword(
		UserId username,
		String aoServer,
		String password
	) throws IllegalArgumentException, IOException, SQLException {
		return getLinuxServerAccount(aoServer, username).passwordMatches(password);
	}

	/**
	 * Completes a <code>Ticket</code>.  Once a <code>Ticket</code> is completed, no more
	 * modifications or actions may be applied to the <code>Ticket</code>.
	 *
	 * @param  ticket_id  the pkey of the <code>Ticket</code>
	 * @param  business_administrator  the username of the <code>BusinessAdministrator</code>
	 *					making the change
	 * @param  comments  the details of the change
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Ticket</code>,
	 *					<code>TicketType</code>, or <code>BusinessAdministrator</code>
	 *
	 * @see  Ticket#actCompleteTicket
	 * @see  #addTicket
	 * @see  Action
	 */
	/*
	public void completeTicket(
		int ticket_id,
		String business_administrator,
		String comments
	) throws IllegalArgumentException, IOException, SQLException {
		Ticket ti=connector.getTickets().get(ticket_id);
		if(ti==null) throw new IllegalArgumentException("Unable to find Ticket: "+ticket_id);
		BusinessAdministrator pe=connector.getBusinessAdministrators().get(business_administrator);
		if(pe==null) throw new IllegalArgumentException("Unable to find BusinessAdministrator: "+business_administrator);
		ti.actCompleteTicket(pe, comments);
	}*/

	/**
	 * Copies the contents of user's home directory from one server to another.
	 *
	 * @param  username  the username of the <code>LinuxAccount</code>
	 * @param  from_ao_server  the server to get the data from
	 * @param  to_ao_server  the server to put the data on
	 *
	 * @return  the number of bytes transferred
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if unable to find the source <code>LinuxServerAccount</code>
	 *					or destination <code>AOServer</code>
	 *
	 * @see  LinuxServerAccount#copyHomeDirectory
	 * @see  #addLinuxServerAccount
	 * @see  #removeLinuxServerAccount
	 */
	public long copyHomeDirectory(
		UserId username,
		String from_ao_server,
		String to_ao_server
	) throws IllegalArgumentException, IOException, SQLException {
		return getLinuxServerAccount(from_ao_server, username).copyHomeDirectory(getAOServer(to_ao_server));
	}

	/**
	 * Copies the password from one <code>LinuxServerAccount</code> to another.
	 *
	 * @param  from_username  the username to copy from
	 * @param  from_ao_server  the server to get the data from
	 * @param  to_username  the username to copy to
	 * @param  to_ao_server  the server to put the data on
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if unable to find a <code>LinuxServerAccount</code>
	 *
	 * @see  LinuxServerAccount#copyPassword
	 * @see  #addLinuxServerAccount
	 * @see  #removeLinuxServerAccount
	 */
	public void copyLinuxServerAccountPassword(
		UserId from_username,
		String from_ao_server,
		UserId to_username,
		String to_ao_server
	) throws IllegalArgumentException, IOException, SQLException {
		getLinuxServerAccount(from_ao_server, from_username).copyPassword(getLinuxServerAccount(to_ao_server, to_username));
	}

	/**
	 * Encrypts a password using a pure Java implementation of the standard Unix <code>crypt</code>
	 * function.
	 *
	 * @param  password  the password that is to be encrypted
	 * @param  salt  the two character salt for the encryption process, if <code>null</code>,
	 *					a random salt will be used
	 * 
	 * @deprecated  Please use hash instead.
	 * @see  #hash(String)
	 */
	@Deprecated
	public static String crypt(String password, String salt) {
		if(password==null || password.length()==0) return "*";
		return salt==null || salt.length()==0?com.aoindustries.util.UnixCrypt.crypt(password):com.aoindustries.util.UnixCrypt.crypt(password, salt);
	}

	/**
	 * Hashes a password using SHA-1.
	 */
	public static String hash(String password) {
		return HashedPassword.hash(password);
	}

	/**
	 * Disables a <code>CreditCard</code>.  When a <code>Transaction</code> using a
	 * <code>CreditCard</code> fails, the <code>CreditCard</code> is disabled.
	 *
	 * @param  pkey  the unique identifier of the <code>CreditCard</code>
	 * @param  reason  the reason the card is being disabled
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>CreditCard</code>
	 *
	 * @see  CreditCard#declined
	 * @see  Transaction
	 * @see  CreditCard
	 */
	public void declineCreditCard(
		int pkey,
		String reason
	) throws IllegalArgumentException, IOException, SQLException {
		CreditCard card=connector.getPayment().getCreditCards().get(pkey);
		if(card==null) throw new IllegalArgumentException("Unable to find CreditCard: "+pkey);
		card.declined(reason);
	}

	/**
	 * Disables a business, recursively disabling all of its enabled child components.
	 *
	 * @param  accounting  the accounting code to disable
	 * @param  disableReason  the reason the account is being disabled
	 *
	 * @return  the pkey of the new <code>DisableLog</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the necessary <code>AOServObject</code>s
	 */
	public int disableBusiness(AccountingCode accounting, String disableReason) throws IllegalArgumentException, IOException, SQLException {
		Account bu=getBusiness(accounting);
		DisableLog dl=connector.getAccount().getDisableLogs().get(bu.addDisableLog(disableReason));
		for(Package pk : bu.getPackages()) if(!pk.isDisabled()) disablePackage(dl, pk);
		bu.disable(dl);
		return dl.getPkey();
	}

	/**
	 * Disables a package, recursively disabling all of its enabled child components.
	 *
	 * @param  name  the name of the package
	 * @param  disableReason  the reason the account is being disabled
	 *
	 * @return  the pkey of the new <code>DisableLog</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the necessary <code>AOServObject</code>s
	 */
	public int disablePackage(AccountingCode name, String disableReason) throws IllegalArgumentException, SQLException, IOException {
		Package pk=getPackage(name);
		DisableLog dl=connector.getAccount().getDisableLogs().get(pk.getBusiness().addDisableLog(disableReason));
		disablePackage(dl, pk);
		return dl.getPkey();
	}
	private void disablePackage(DisableLog dl, Package pk) throws IOException, SQLException {
		/*
		 * Email stuff
		 */
		for(com.aoindustries.aoserv.client.email.List el : pk.getEmailLists()) if(!el.isDisabled()) el.disable(dl);
		for(Pipe ep : pk.getEmailPipes()) if(!ep.isDisabled()) ep.disable(dl);
		for(SmtpRelay ssr : pk.getEmailSmtpRelays()) if(!ssr.isDisabled()) ssr.disable(dl);

		/*
		 * HTTP stuff
		 */
		List<Server> httpdServers=new SortedArrayList<>();
		for(SharedTomcat hst : pk.getHttpdSharedTomcats()) {
			if(!hst.isDisabled()) {
				hst.disable(dl);
				Server ao=hst.getAOServer();
				if(!httpdServers.contains(ao)) httpdServers.add(ao);
			}
		}
		for(Site hs : pk.getHttpdSites()) {
			if(!hs.isDisabled()) {
				disableHttpdSite(dl, hs);
				Server ao=hs.getAoServer();
				if(!httpdServers.contains(ao)) httpdServers.add(ao);
			}
		}

		// Wait for httpd site rebuilds to complete, which shuts down all the appropriate processes
		for(Server httpdServer : httpdServers) httpdServer.waitForHttpdSiteRebuild();

		// Disable the user accounts once the JVMs have been shut down
		for(Username un : pk.getUsernames()) if(!un.isDisabled()) disableUsername(dl, un);

		pk.disable(dl);
	}

	/**
	 * Disables a <code>HttpdSharedTomcat</code>.
	 *
	 * @param  name  the name of the tomcat JVM
	 * @param  aoServer  the server that hosts the JVM
	 * @param  disableReason  the reason the JVM is being disabled
	 *
	 * @return  the pkey of the new <code>DisableLog</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>AOServer</code>, or <code>HttpdSharedTomcat</code>
	 */
	public int disableHttpdSharedTomcat(
		String name,
		String aoServer,
		String disableReason
	) throws IllegalArgumentException, IOException, SQLException {
		SharedTomcat hst=getHttpdSharedTomcat(aoServer, name);
		DisableLog dl=connector.getAccount().getDisableLogs().get(hst.getLinuxServerGroup().getLinuxGroup().getPackage().getBusiness().addDisableLog(disableReason));
		hst.disable(dl);
		return dl.getPkey();
	}

	/**
	 * Disables an <code>EmailPipe</code>.
	 *
	 * @param  pkey  the pkey of the pipe
	 * @param  disableReason  the reason the pipe is being disabled
	 *
	 * @return  the pkey of the new <code>DisableLog</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>EmailPipe</code>
	 */
	public int disableEmailPipe(
		int pkey,
		String disableReason
	) throws IllegalArgumentException, SQLException, IOException {
		Pipe ep=connector.getEmail().getEmailPipes().get(pkey);
		if(ep==null) throw new IllegalArgumentException("Unable to find EmailPipe: "+pkey);
		DisableLog dl=connector.getAccount().getDisableLogs().get(ep.getPackage().getBusiness().addDisableLog(disableReason));
		ep.disable(dl);
		return dl.getPkey();
	}

	/**
	 * Disables a <code>HttpdSite</code>.
	 *
	 * @param  name  the name of the site
	 * @param  aoServer  the server that hosts the site
	 * @param  disableReason  the reason the site is being disabled
	 *
	 * @return  the pkey of the new <code>DisableLog</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code>, or <code>HttpdSite</code>
	 */
	public int disableHttpdSite(
		String name,
		String aoServer,
		String disableReason
	) throws IllegalArgumentException, SQLException, IOException {
		Site hs=getHttpdSite(aoServer, name);
		DisableLog dl=connector.getAccount().getDisableLogs().get(hs.getPackage().getBusiness().addDisableLog(disableReason));
		disableHttpdSite(dl, hs);
		return dl.getPkey();
	}
	private void disableHttpdSite(DisableLog dl, Site hs) throws IOException, SQLException {
		for(VirtualHost hsb : hs.getHttpdSiteBinds()) if(!hsb.isDisabled()) hsb.disable(dl);
		hs.disable(dl);
	}

	/**
	 * Disables a <code>HttpdSiteBind</code>.
	 *
	 * @param  pkey  the pkey of the bind
	 * @param  disableReason  the reason the bind is being disabled
	 *
	 * @return  the pkey of the new <code>DisableLog</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>HttpdSiteBind</code>
	 */
	public int disableHttpdSiteBind(
		int pkey,
		String disableReason
	) throws IllegalArgumentException, SQLException, IOException {
		VirtualHost hsb=connector.getWeb().getHttpdSiteBinds().get(pkey);
		if(hsb==null) throw new IllegalArgumentException("Unable to find HttpdSiteBind: "+pkey);
		DisableLog dl=connector.getAccount().getDisableLogs().get(hsb.getHttpdSite().getPackage().getBusiness().addDisableLog(disableReason));
		hsb.disable(dl);
		return dl.getPkey();
	}

	/**
	 * Disables an <code>EmailList</code>.
	 *
	 * @param  path  the path of the list
	 * @param  aoServer  the server the list is part of
	 * @param  disableReason  the reason the bind is being disabled
	 *
	 * @return  the pkey of the new <code>DisableLog</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>EmailList</code>
	 */
	public int disableEmailList(
		UnixPath path,
		String aoServer,
		String disableReason
	) throws IllegalArgumentException, SQLException, IOException {
		com.aoindustries.aoserv.client.email.List el=getEmailList(aoServer, path);
		DisableLog dl=connector.getAccount().getDisableLogs().get(el.getLinuxServerGroup().getLinuxGroup().getPackage().getBusiness().addDisableLog(disableReason));
		el.disable(dl);
		return dl.getPkey();
	}

	/**
	 * Disables a <code>EmailSmtpRelay</code>.
	 *
	 * @param  pkey  the pkey of the relay
	 * @param  disableReason  the reason the bind is being disabled
	 *
	 * @return  the pkey of the new <code>DisableLog</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>EmailSmtpRelay</code>
	 */
	public int disableEmailSmtpRelay(
		int pkey,
		String disableReason
	) throws IllegalArgumentException, SQLException, IOException {
		SmtpRelay ssr=connector.getEmail().getEmailSmtpRelays().get(pkey);
		if(ssr==null) throw new IllegalArgumentException("Unable to find EmailSmtpRelay: "+pkey);
		DisableLog dl=connector.getAccount().getDisableLogs().get(ssr.getPackage().getBusiness().addDisableLog(disableReason));
		ssr.disable(dl);
		return dl.getPkey();
	}

	/**
	 * Disables a <code>Username</code> and all uses of the username.
	 *
	 * @param  username  the username to disable
	 * @param  disableReason  the reason the bind is being disabled
	 *
	 * @return  the pkey of the new <code>DisableLog</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Username</code>
	 */
	public int disableUsername(
		UserId username,
		String disableReason
	) throws IllegalArgumentException, SQLException, IOException {
		Username un=getUsername(username);
		DisableLog dl=connector.getAccount().getDisableLogs().get(un.getPackage().getBusiness().addDisableLog(disableReason));
		disableUsername(dl, un);
		return dl.getPkey();
	}
	private void disableUsername(DisableLog dl, Username un) throws IOException, SQLException {
		User la=un.getLinuxAccount();
		if(la!=null && !la.isDisabled()) disableLinuxAccount(dl, la);

		com.aoindustries.aoserv.client.mysql.User mu=un.getMySQLUser();
		if(mu!=null && !mu.isDisabled()) disableMySQLUser(dl, mu);

		com.aoindustries.aoserv.client.postgresql.User pu=un.getPostgresUser();
		if(pu!=null && !pu.isDisabled()) disablePostgresUser(dl, pu);

		un.disable(dl);
	}

	/**
	 * Disables a <code>LinuxAccount</code>.
	 *
	 * @param  username  the username to disable
	 * @param  disableReason  the reason the account is being disabled
	 *
	 * @return  the pkey of the new <code>DisableLog</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Username</code> or <code>LinuxAccount</code>
	 */
	public int disableLinuxAccount(
		UserId username,
		String disableReason
	) throws IllegalArgumentException, SQLException, IOException {
		User la=getLinuxAccount(username);
		DisableLog dl=connector.getAccount().getDisableLogs().get(la.getUsername().getPackage().getBusiness().addDisableLog(disableReason));
		disableLinuxAccount(dl, la);
		return dl.getPkey();
	}
	private void disableLinuxAccount(DisableLog dl, User la) throws IOException, SQLException {
		for(UserServer lsa : la.getLinuxServerAccounts()) {
			if(!lsa.isDisabled()) disableLinuxServerAccount(dl, lsa);
		}

		la.disable(dl);
	}

	/**
	 * Disables a <code>LinuxServerAccount</code>.
	 *
	 * @param  username  the username to disable
	 * @param  aoServer  the server the account is on
	 * @param  disableReason  the reason the account is being disabled
	 *
	 * @return  the pkey of the new <code>DisableLog</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Username</code>,
	 *                                  <code>LinuxAccount</code>, or <code>LinuxServerAccount</code>
	 */
	public int disableLinuxServerAccount(
		UserId username,
		String aoServer,
		String disableReason
	) throws IllegalArgumentException, SQLException, IOException {
		UserServer lsa=getLinuxServerAccount(aoServer, username);
		DisableLog dl=connector.getAccount().getDisableLogs().get(lsa.getLinuxAccount().getUsername().getPackage().getBusiness().addDisableLog(disableReason));
		disableLinuxServerAccount(dl, lsa);
		return dl.getPkey();
	}
	private void disableLinuxServerAccount(DisableLog dl, UserServer lsa) throws IOException, SQLException {
		for(CvsRepository cr : lsa.getCvsRepositories()) if(!cr.isDisabled()) cr.disable(dl);
		lsa.disable(dl);
	}

	/**
	 * Disables a <code>CvsRepository</code>.
	 *
	 * @param  pkey  the pkey of the repository to disable
	 * @param  disableReason  the reason the account is being disabled
	 *
	 * @return  the pkey of the new <code>DisableLog</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>CvsRepository</code>
	 */
	public int disableCvsRepository(
		int pkey,
		String disableReason
	) throws IllegalArgumentException, SQLException, IOException {
		CvsRepository cr=connector.getScm().getCvsRepositories().get(pkey);
		if(cr==null) throw new IllegalArgumentException("Unable to find CvsRepository: "+pkey);
		DisableLog dl=connector.getAccount().getDisableLogs()
			.get(
				cr
				.getLinuxServerAccount()
				.getLinuxAccount()
				.getUsername()
				.getPackage()
				.getBusiness()
				.addDisableLog(disableReason)
			)
		;
		cr.disable(dl);
		return dl.getPkey();
	}

	/**
	 * Disables a <code>MySQLUser</code>.
	 *
	 * @param  username  the username to disable
	 * @param  disableReason  the reason the account is being disabled
	 *
	 * @return  the pkey of the new <code>DisableLog</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Username</code> or <code>MySQLUser</code>
	 */
	public int disableMySQLUser(
		MySQLUserId username,
		String disableReason
	) throws IllegalArgumentException, SQLException, IOException {
		com.aoindustries.aoserv.client.mysql.User mu=getMySQLUser(username);
		DisableLog dl=connector.getAccount().getDisableLogs().get(mu.getUsername().getPackage().getBusiness().addDisableLog(disableReason));
		disableMySQLUser(dl, mu);
		return dl.getPkey();
	}
	private void disableMySQLUser(DisableLog dl, com.aoindustries.aoserv.client.mysql.User mu) throws IOException, SQLException {
		for(com.aoindustries.aoserv.client.mysql.UserServer msu : mu.getMySQLServerUsers()) if(!msu.isDisabled()) msu.disable(dl);
		mu.disable(dl);
	}

	/**
	 * Disables a <code>MySQLServerUser</code>.
	 *
	 * @param  username  the username to disable
	 * @param  aoServer  the server the account is on
	 * @param  disableReason  the reason the account is being disabled
	 *
	 * @return  the pkey of the new <code>DisableLog</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>MySQLUser</code>
	 */
	public int disableMySQLServerUser(
		MySQLUserId username,
		MySQLServerName mysqlServer,
		String aoServer,
		String disableReason
	) throws IllegalArgumentException, SQLException, IOException {
		com.aoindustries.aoserv.client.mysql.UserServer msu=getMySQLServerUser(aoServer, mysqlServer, username);
		DisableLog dl=connector.getAccount().getDisableLogs().get(msu.getMySQLUser().getUsername().getPackage().getBusiness().addDisableLog(disableReason));
		msu.disable(dl);
		return dl.getPkey();
	}

	/**
	 * Disables a <code>PostgresUser</code>.
	 *
	 * @param  username  the username to disable
	 * @param  disableReason  the reason the account is being disabled
	 *
	 * @return  the pkey of the new <code>DisableLog</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Username</code> or <code>PostgresUser</code>
	 */
	public int disablePostgresUser(
		PostgresUserId username,
		String disableReason
	) throws IllegalArgumentException, SQLException, IOException {
		Username un=getUsername(username);
		com.aoindustries.aoserv.client.postgresql.User pu=un.getPostgresUser();
		if(pu==null) throw new IllegalArgumentException("Unable to find PostgresUser: "+username);
		DisableLog dl=connector.getAccount().getDisableLogs().get(un.getPackage().getBusiness().addDisableLog(disableReason));
		disablePostgresUser(dl, pu);
		return dl.getPkey();
	}
	private void disablePostgresUser(DisableLog dl, com.aoindustries.aoserv.client.postgresql.User pu) throws IOException, SQLException{
		for(com.aoindustries.aoserv.client.postgresql.UserServer psu : pu.getPostgresServerUsers()) if(!psu.isDisabled()) psu.disable(dl);
		pu.disable(dl);
	}

	/**
	 * Disables a <code>PostgresServerUser</code>.
	 *
	 * @param  username  the username to disable
	 * @param  postgresServer  the name of the PostgresServer
	 * @param  aoServer  the server the account is on
	 * @param  disableReason  the reason the account is being disabled
	 *
	 * @return  the pkey of the new <code>DisableLog</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>PostgresUser</code>
	 */
	public int disablePostgresServerUser(
		PostgresUserId username,
		PostgresServerName postgresServer,
		String aoServer,
		String disableReason
	) throws IllegalArgumentException, SQLException, IOException {
		com.aoindustries.aoserv.client.postgresql.UserServer psu=getPostgresServerUser(aoServer, postgresServer, username);
		DisableLog dl=connector.getAccount().getDisableLogs()
			.get(
				psu
				.getPostgresUser()
				.getUsername()
				.getPackage()
				.getBusiness()
				.addDisableLog(disableReason)
			)
		;
		psu.disable(dl);
		return dl.getPkey();
	}

	/**
	 * Disables a <code>BusinessAdministrator</code>.
	 *
	 * @param  username  the username to disable
	 * @param  disableReason  the reason the account is being disabled
	 *
	 * @return  the pkey of the new <code>DisableLog</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Username</code> or <code>BusinessAdministrator</code>
	 */
	public int disableBusinessAdministrator(
		UserId username,
		String disableReason
	) throws IllegalArgumentException, SQLException, IOException {
		Username un=getUsername(username);
		Administrator ba=un.getBusinessAdministrator();
		if(ba==null) throw new IllegalArgumentException("Unable to find BusinessAdministrator: "+username);
		DisableLog dl=connector.getAccount().getDisableLogs().get(un.getPackage().getBusiness().addDisableLog(disableReason));
		ba.disable(dl);
		return dl.getPkey();
	}

	/**
	 * Enables a business, recursively enabling all of its disabled child components.
	 *
	 * @param  accounting  the accounting code to enable
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the necessary <code>Business</code>s
	 */
	public void enableBusiness(AccountingCode accounting) throws IllegalArgumentException, IOException, SQLException {
		Account bu=getBusiness(accounting);
		DisableLog dl=bu.getDisableLog();
		if(dl==null) throw new IllegalArgumentException("Business not disabled: "+accounting);
		bu.enable();
		for(Package pk : bu.getPackages()) if(dl.equals(pk.getDisableLog())) enablePackage(dl, pk);
	}

	/**
	 * Enables a package, recursively enabling all of its disabled child components.
	 *
	 * @param  name  the name of the package
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the necessary <code>AOServObject</code>s
	 */
	public void enablePackage(AccountingCode name) throws IllegalArgumentException, SQLException, IOException {
		Package pk=getPackage(name);
		DisableLog dl=pk.getDisableLog();
		if(dl==null) throw new IllegalArgumentException("Package not disabled: "+name);
		enablePackage(dl, pk);
	}
	private void enablePackage(DisableLog dl, Package pk) throws IOException, SQLException {
		pk.enable();

		/*
		 * Email stuff
		 */
		for(com.aoindustries.aoserv.client.email.List el : pk.getEmailLists()) if(dl.equals(el.getDisableLog())) el.enable();
		for(Pipe ep : pk.getEmailPipes()) if(dl.equals(ep.getDisableLog())) ep.enable();
		for(SmtpRelay ssr : pk.getEmailSmtpRelays()) if(dl.equals(ssr.getDisableLog())) ssr.enable();

		// Various accounts
		List<Server> linuxAccountServers=new SortedArrayList<>();
		List<Server> mysqlServers=new SortedArrayList<>();
		List<Server> postgresServers=new SortedArrayList<>();
		for(Username un : pk.getUsernames()) {
			if(dl.equals(un.getDisableLog())) enableUsername(
				dl,
				un,
				linuxAccountServers,
				mysqlServers,
				postgresServers
			);
		}

		// Wait for rebuilds
		for (Server linuxAccountServer : linuxAccountServers) {
			linuxAccountServer.waitForLinuxAccountRebuild();
		}
		for (Server mysqlServer : mysqlServers) {
			mysqlServer.waitForMySQLUserRebuild();
		}
		for (Server postgresServer : postgresServers) {
			postgresServer.waitForPostgresUserRebuild();
		}

		// Start up the web sites
		for(SharedTomcat hst : pk.getHttpdSharedTomcats()) if(dl.equals(hst.getDisableLog())) hst.enable();

		for(Site hs : pk.getHttpdSites()) if(hs.getDisableLog_pkey() != null && hs.getDisableLog_pkey() == dl.getPkey()) enableHttpdSite(dl, hs);
	}

	/**
	 * Enables a <code>HttpdSharedTomcat</code>.
	 *
	 * @param  name  the name of the tomcat JVM
	 * @param  aoServer  the server that hosts the JVM
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>AOServer</code>, or <code>HttpdSharedTomcat</code>
	 */
	public void enableHttpdSharedTomcat(
		String name,
		String aoServer
	) throws IllegalArgumentException, SQLException, IOException {
		SharedTomcat hst=getHttpdSharedTomcat(aoServer, name);
		DisableLog dl=hst.getDisableLog();
		if(dl==null) throw new IllegalArgumentException("HttpdSharedTomcat not disabled: "+name+" on "+aoServer);
		hst.enable();
	}

	/**
	 * Enables an <code>EmailPipe</code>.
	 *
	 * @param  pkey  the pkey of the pipe
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>EmailPipe</code>
	 */
	public void enableEmailPipe(
		int pkey
	) throws IllegalArgumentException, SQLException, IOException {
		Pipe ep=connector.getEmail().getEmailPipes().get(pkey);
		if(ep==null) throw new IllegalArgumentException("Unable to find EmailPipe: "+pkey);
		DisableLog dl=ep.getDisableLog();
		if(dl==null) throw new IllegalArgumentException("EmailPipe not disabled: "+pkey);
		ep.enable();
	}

	/**
	 * Enables a <code>HttpdSite</code>.
	 *
	 * @param  name  the name of the site
	 * @param  aoServer  the server that hosts the site
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>AOServer</code>, or <code>HttpdSite</code>
	 */
	public void enableHttpdSite(
		String name,
		String aoServer
	) throws IllegalArgumentException, SQLException, IOException {
		Site hs=getHttpdSite(aoServer, name);
		DisableLog dl=hs.getDisableLog();
		if(dl==null) throw new IllegalArgumentException("HttpdSite not disabled: "+name+" on "+aoServer);
		enableHttpdSite(dl, hs);
	}
	private void enableHttpdSite(DisableLog dl, Site hs) throws IOException, SQLException {
		hs.enable();
		for(VirtualHost hsb : hs.getHttpdSiteBinds()) if(dl.equals(hsb.getDisableLog())) hsb.enable();
	}

	/**
	 * Enables a <code>HttpdSiteBind</code>.
	 *
	 * @param  pkey  the pkey of the bind
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>HttpdSiteBind</code>
	 */
	public void enableHttpdSiteBind(
		int pkey
	) throws IllegalArgumentException, SQLException, IOException {
		VirtualHost hsb=connector.getWeb().getHttpdSiteBinds().get(pkey);
		if(hsb==null) throw new IllegalArgumentException("Unable to find HttpdSiteBind: "+pkey);
		DisableLog dl=hsb.getDisableLog();
		if(dl==null) throw new IllegalArgumentException("HttpdSiteBind not disabled: "+pkey);
		hsb.enable();
	}

	/**
	 * Enables an <code>EmailList</code>.
	 *
	 * @param  path  the path of the list
	 * @param  aoServer  the server the list is part of
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>EmailList</code>
	 */
	public void enableEmailList(
		UnixPath path,
		String aoServer
	) throws IllegalArgumentException, SQLException, IOException {
		com.aoindustries.aoserv.client.email.List el=getEmailList(aoServer, path);
		DisableLog dl=el.getDisableLog();
		if(dl==null) throw new IllegalArgumentException("EmailList not disabled: "+path+" on "+aoServer);
		el.enable();
	}

	/**
	 * Enables a <code>EmailSmtpRelay</code>.
	 *
	 * @param  pkey  the pkey of the relay
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>EmailSmtpRelay</code>
	 */
	public void enableEmailSmtpRelay(
		int pkey
	) throws IllegalArgumentException, IOException, SQLException {
		SmtpRelay ssr=connector.getEmail().getEmailSmtpRelays().get(pkey);
		if(ssr==null) throw new IllegalArgumentException("Unable to find EmailSmtpRelay: "+pkey);
		DisableLog dl=ssr.getDisableLog();
		if(dl==null) throw new IllegalArgumentException("EmailSmtpRelay not disabled: "+pkey);
		ssr.enable();
	}

	/**
	 * Enables a <code>Username</code> and all uses of the username.
	 *
	 * @param  username  the username to enable
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Username</code>
	 */
	public void enableUsername(
		UserId username
	) throws IllegalArgumentException, SQLException, IOException {
		Username un=getUsername(username);
		DisableLog dl=un.getDisableLog();
		if(dl==null) throw new IllegalArgumentException("Username not disabled: "+username);
		enableUsername(dl, un, null, null, null);
	}
	private void enableUsername(
		DisableLog dl,
		Username un,
		List<Server> linuxAccountServers,
		List<Server> mysqlServers,
		List<Server> postgresServers
	) throws IOException, SQLException {
		un.enable();

		Administrator ba=un.getBusinessAdministrator();
		if(ba!=null && dl.equals(ba.getDisableLog())) ba.enable();

		User la=un.getLinuxAccount();
		if(la!=null && dl.equals(la.getDisableLog())) enableLinuxAccount(dl, la, linuxAccountServers);

		com.aoindustries.aoserv.client.mysql.User mu=un.getMySQLUser();
		if(mu!=null && dl.equals(mu.getDisableLog())) enableMySQLUser(dl, mu, mysqlServers);

		com.aoindustries.aoserv.client.postgresql.User pu=un.getPostgresUser();
		if(pu!=null && dl.equals(pu.getDisableLog())) enablePostgresUser(dl, pu, postgresServers);
	}

	/**
	 * Enables a <code>LinuxAccount</code>.
	 *
	 * @param  username  the username to enable
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Username</code> or <code>LinuxAccount</code>
	 */
	public void enableLinuxAccount(
		UserId username
	) throws IllegalArgumentException, SQLException, IOException {
		User la=getLinuxAccount(username);
		DisableLog dl=la.getDisableLog();
		if(dl==null) throw new IllegalArgumentException("LinuxAccount not disabled: "+username);
		enableLinuxAccount(dl, la, null);
	}
	private void enableLinuxAccount(DisableLog dl, User la, List<Server> linuxAccountServers) throws SQLException, IOException {
		la.enable();

		for(UserServer lsa : la.getLinuxServerAccounts()) {
			if(dl.equals(lsa.getDisableLog())) {
				enableLinuxServerAccount(dl, lsa);
				if(linuxAccountServers!=null) {
					Server ao=lsa.getAOServer();
					if(!linuxAccountServers.contains(ao)) linuxAccountServers.add(ao);
				}
			}
		}
	}

	/**
	 * Enables a <code>LinuxServerAccount</code>.
	 *
	 * @param  username  the username to enable
	 * @param  aoServer  the server the account is on
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Username</code>,
	 *                                  <code>LinuxAccount</code>, or <code>LinuxServerAccount</code>
	 */
	public void enableLinuxServerAccount(
		UserId username,
		String aoServer
	) throws IllegalArgumentException, SQLException, IOException {
		UserServer lsa=getLinuxServerAccount(aoServer, username);
		DisableLog dl=lsa.getDisableLog();
		if(dl==null) throw new IllegalArgumentException("LinuxServerAccount not disabled: "+username+" on "+aoServer);
		enableLinuxServerAccount(dl, lsa);
	}
	private void enableLinuxServerAccount(DisableLog dl, UserServer lsa) throws IOException, SQLException {
		lsa.enable();
		for(CvsRepository cr : lsa.getCvsRepositories()) if(dl.equals(cr.getDisableLog())) cr.enable();
	}

	/**
	 * Enables a <code>CvsRepository</code>.
	 *
	 * @param  pkey  the pkey of the repository to enable
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>CvsRepository</code>
	 */
	public void enableCvsRepository(
		int pkey
	) throws IllegalArgumentException, SQLException, IOException {
		CvsRepository cr=connector.getScm().getCvsRepositories().get(pkey);
		if(cr==null) throw new IllegalArgumentException("Unable to find CvsRepository: "+pkey);
		DisableLog dl=cr.getDisableLog();
		if(dl==null) throw new IllegalArgumentException("CvsRepository not disabled: "+pkey);
		cr.enable();
	}

	/**
	 * Enables a <code>MySQLUser</code>.
	 *
	 * @param  username  the username to enable
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Username</code> or <code>MySQLUser</code>
	 */
	public void enableMySQLUser(
		MySQLUserId username
	) throws IllegalArgumentException, SQLException, IOException {
		com.aoindustries.aoserv.client.mysql.User mu=getMySQLUser(username);
		DisableLog dl=mu.getDisableLog();
		if(dl==null) throw new IllegalArgumentException("MySQLUser not disabled: "+username);
		enableMySQLUser(dl, mu, null);
	}
	private void enableMySQLUser(DisableLog dl, com.aoindustries.aoserv.client.mysql.User mu, List<Server> mysqlServers) throws IOException, SQLException {
		mu.enable();
		for(com.aoindustries.aoserv.client.mysql.UserServer msu : mu.getMySQLServerUsers()) {
			if(dl.equals(msu.getDisableLog())) {
				msu.enable();
				if(mysqlServers!=null) {
					Server ao=msu.getMySQLServer().getAoServer();
					if(!mysqlServers.contains(ao)) mysqlServers.add(ao);
				}
			}
		}
	}

	/**
	 * Enables a <code>MySQLServerUser</code>.
	 *
	 * @param  username  the username to enable
	 * @param  aoServer  the server the account is on
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>MySQLUser</code>
	 */
	public void enableMySQLServerUser(
		MySQLUserId username,
		MySQLServerName mysqlServer,
		String aoServer
	) throws IllegalArgumentException, SQLException, IOException {
		com.aoindustries.aoserv.client.mysql.UserServer msu=getMySQLServerUser(aoServer, mysqlServer, username);
		DisableLog dl=msu.getDisableLog();
		if(dl==null) throw new IllegalArgumentException("MySQLServerUser not disabled: "+username+" on "+mysqlServer+" on "+aoServer);
		msu.enable();
	}

	/**
	 * Enables a <code>PostgresUser</code>.
	 *
	 * @param  username  the username to enable
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Username</code> or <code>PostgresUser</code>
	 */
	public void enablePostgresUser(
		PostgresUserId username
	) throws IllegalArgumentException, SQLException, IOException {
		Username un=getUsername(username);
		com.aoindustries.aoserv.client.postgresql.User pu=un.getPostgresUser();
		if(pu==null) throw new IllegalArgumentException("Unable to find PostgresUser: "+username);
		DisableLog dl=pu.getDisableLog();
		if(dl==null) throw new IllegalArgumentException("PostgresUser not disabled: "+username);
		enablePostgresUser(dl, pu, null);
	}
	private void enablePostgresUser(DisableLog dl, com.aoindustries.aoserv.client.postgresql.User pu, List<Server> postgresServers) throws IOException, SQLException {
		pu.enable();

		for(com.aoindustries.aoserv.client.postgresql.UserServer psu : pu.getPostgresServerUsers()) {
			if(dl.equals(psu.getDisableLog())) {
				psu.enable();
				if(postgresServers!=null) {
					Server ao=psu.getPostgresServer().getAoServer();
					if(!postgresServers.contains(ao)) postgresServers.add(ao);
				}
			}
		}
	}

	/**
	 * Enables a <code>PostgresServerUser</code>.
	 *
	 * @param  username  the username to enable
	 * @param  postgresServer  the name of the PostgreSQL server
	 * @param  aoServer  the server the account is on
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>PostgresUser</code>
	 */
	public void enablePostgresServerUser(
		PostgresUserId username,
		PostgresServerName postgresServer,
		String aoServer
	) throws IllegalArgumentException, IOException, SQLException {
		com.aoindustries.aoserv.client.postgresql.UserServer psu=getPostgresServerUser(aoServer, postgresServer, username);
		DisableLog dl=psu.getDisableLog();
		if(dl==null) throw new IllegalArgumentException("PostgresServerUser not disabled: "+username+" on "+aoServer);
		psu.enable();
	}

	/**
	 * Enables a <code>BusinessAdministrator</code>.
	 *
	 * @param  username  the username to enable
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Username</code> or <code>BusinessAdministrator</code>
	 */
	public void enableBusinessAdministrator(
		UserId username
	) throws IllegalArgumentException, SQLException, IOException {
		Username un=getUsername(username);
		Administrator ba=un.getBusinessAdministrator();
		if(ba==null) throw new IllegalArgumentException("Unable to find BusinessAdministrator: "+username);
		DisableLog dl=ba.getDisableLog();
		if(dl==null) throw new IllegalArgumentException("BusinessAdministrator not disabled: "+username);
		ba.enable();
	}

	/**
	 * Dumps the contents of a <code>MySQLDatabase</code> to a <code>Writer</code>.
	 *
	 * @param  name  the name of the <code>MySQLDatabase</code>
	 * @param  aoServer  the hostname of the <code>AOServer</code>
	 * @param  out  the <code>Writer</code> to dump to
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or
	 *					<code>MySQLDatabase</code>
	 *
	 * @see  MySQLDatabase#dump
	 * @see  Database
	 */
	public void dumpMySQLDatabase(
		MySQLDatabaseName name,
		MySQLServerName mysqlServer,
		String aoServer,
		Writer out
	) throws IllegalArgumentException, IOException, SQLException {
		getMySQLDatabase(aoServer, mysqlServer, name).dump(out);
	}

	/**
	 * Dumps the contents of a <code>MySQLDatabase</code> to an {@link OutputStream}, optionally gzipped.
	 *
	 * @param  name  the name of the <code>MySQLDatabase</code>
	 * @param  aoServer  the hostname of the <code>AOServer</code>
	 * @param  gzip  the gzip flag
	 * @param  out  the <code>OutputStream</code> to dump to
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or
	 *					<code>MySQLDatabase</code>
	 *
	 * @see  MySQLDatabase#dump
	 * @see  Database
	 */
	public void dumpMySQLDatabase(
		MySQLDatabaseName name,
		MySQLServerName mysqlServer,
		String aoServer,
		boolean gzip,
		StreamHandler streamHandler
	) throws IllegalArgumentException, IOException, SQLException {
		getMySQLDatabase(aoServer, mysqlServer, name).dump(gzip, streamHandler);
	}

	/**
	 * Dumps the contents of a <code>PostgresDatabase</code> to a <code>Writer</code>.
	 *
	 * @param  name  the name of the <code>PostgresDatabase</code>
	 * @param  postgresServer  the name of the PostgreSQL server
	 * @param  aoServer  the hostname of the <code>Server</code>
	 * @param  out  the <code>Writer</code> to dump to
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or
	 *					<code>PostgresDatabase</code>
	 *
	 * @see  PostgresDatabase#dump
	 * @see  Database
	 */
	public void dumpPostgresDatabase(
		PostgresDatabaseName name,
		PostgresServerName postgresServer,
		String aoServer,
		Writer out
	) throws IllegalArgumentException, IOException, SQLException {
		getPostgresDatabase(aoServer, postgresServer, name).dump(out);
	}

	/**
	 * Dumps the contents of a <code>PostgresDatabase</code> to an {@link OutputStream}, optionally gzipped.
	 *
	 * @param  name  the name of the <code>PostgresDatabase</code>
	 * @param  postgresServer  the name of the PostgreSQL server
	 * @param  aoServer  the hostname of the <code>Server</code>
	 * @param  gzip  the gzip flag
	 * @param  out  the <code>OutputStream</code> to dump to
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or
	 *					<code>PostgresDatabase</code>
	 *
	 * @see  PostgresDatabase#dump
	 * @see  Database
	 */
	public void dumpPostgresDatabase(
		PostgresDatabaseName name,
		PostgresServerName postgresServer,
		String aoServer,
		boolean gzip,
		StreamHandler streamHandler
	) throws IllegalArgumentException, IOException, SQLException {
		getPostgresDatabase(aoServer, postgresServer, name).dump(gzip, streamHandler);
	}

	/**
	 * Generates a unique accounting code that may be used to create a new <code>Business</code>.
	 *
	 * @param  accountingTemplate  the beginning part of the accounting code, such as <code>"AO_"</code>
	 *
	 * @return  the available accounting code
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 *
	 * @see  BusinessTable#generateAccountingCode
	 * @see  #addBusiness
	 * @see  Account
	 */
	public AccountingCode generateAccountingCode(
		AccountingCode accountingTemplate
	) throws IOException, SQLException {
		return connector.getAccount().getBusinesses().generateAccountingCode(accountingTemplate);
	}

	/**
	 * Generates a unique MySQL database name.
	 *
	 * @param  template_base  the beginning part of the template, such as <code>"AO"</code>
	 * @param  template_added  the part of the template added between the <code>template_base</code> and
	 *					the generated number, such as <code>"_"</code>
	 *
	 * @return  the available database name
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 *
	 * @see  MySQLDatabaseTable#generateMySQLDatabaseName
	 * @see  #addMySQLDatabase
	 * @see  Database
	 */
	public MySQLDatabaseName generateMySQLDatabaseName(
		String template_base,
		String template_added
	) throws IOException, SQLException {
		return connector.getMysql().getMysqlDatabases().generateMySQLDatabaseName(template_base, template_added);
	}

	/**
	 * Generates a unique <code>Package</code> name.
	 *
	 * @param  template  the beginning part of the template, such as <code>"AO_"</code>
	 *
	 * @return  the available <code>Package</code> name
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 *
	 * @see  PackageTable#generatePackageName
	 * @see  #addPackage
	 * @see  Package
	 */
	public AccountingCode generatePackageName(
		AccountingCode template
	) throws IOException, SQLException {
		return connector.getBilling().getPackages().generatePackageName(template);
	}

	/**
	 * Generates a random, valid password.
	 *
	 * @return  the password
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 *
	 */
	public String generatePassword() throws IOException {
		return PasswordGenerator.generatePassword();
	}

	/**
	 * Generates a unique PostgreSQL database name.
	 *
	 * @param  template_base  the beginning part of the template, such as <code>"AO"</code>
	 * @param  template_added  the part of the template added between the <code>template_base</code> and
	 *					the generated number, such as <code>"_"</code>
	 *
	 * @return  the available database name
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 *
	 * @see  PostgresDatabaseTable#generatePostgresDatabaseName
	 * @see  #addPostgresDatabase
	 * @see  Database
	 */
	public PostgresDatabaseName generatePostgresDatabaseName(
		String template_base,
		String template_added
	) throws IOException, SQLException {
		return connector.getPostgresql().getPostgresDatabases().generatePostgresDatabaseName(template_base, template_added);
	}

	/**
	 * Generates a unique <code>HttpdSharedTomcat</code> name.
	 *
	 * @param  template  the beginning part of the template, such as <code>"ao"</code>
	 *
	 * @return  the available <code>HttpdSharedTomcat</code> name
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 *
	 * @see  HttpdSharedTomcatTable#generateSharedTomcatName
	 * @see  #addHttpdSharedTomcat
	 * @see  #addHttpdTomcatSharedSite
	 * @see  Site
	 */
	public String generateSharedTomcatName(
		String template
	) throws IOException, SQLException {
		return connector.getWeb_tomcat().getHttpdSharedTomcats().generateSharedTomcatName(template);
	}

	/**
	 * Generates a unique <code>HttpdSite</code> name.
	 *
	 * @param  template  the beginning part of the template, such as <code>"ao"</code>
	 *
	 * @return  the available <code>HttpdSite</code> name
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 *
	 * @see  HttpdSiteTable#generateSiteName
	 * @see  #addHttpdTomcatStdSite
	 * @see  Site
	 */
	public String generateSiteName(
		String template
	) throws IOException, SQLException {
		return connector.getWeb().getHttpdSites().generateSiteName(template);
	}

	/**
	 * Gets the autoresponder content.
	 *
	 * @param  username  the username of the <code>LinuxServerAccount</code>
	 * @param  aoServer  the server to get the data from
	 *
	 * @return  the autoresponder content
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if unable to find the source <code>LinuxServerAccount</code>
	 *
	 * @see  LinuxServerAccount#getAutoresponderContent
	 * @see  #setAutoresponder
	 */
	public String getAutoresponderContent(
		UserId username,
		String aoServer
	) throws IllegalArgumentException, IOException, SQLException {
		return getLinuxServerAccount(aoServer, username).getAutoresponderContent();
	}

	/**
	 * Gets the <code>AOServConnector</code> used for communication with the server.
	 */
	public AOServConnector getConnector() {
		return connector;
	}

	/**
	 * Gets a user's cron table on one server.
	 *
	 * @param  username  the username of the <code>LinuxAccount</code>
	 * @param  aoServer  the server to get the data from
	 *
	 * @return  the cron table
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if unable to find the source <code>LinuxServerAccount</code>
	 *
	 * @see  LinuxServerAccount#getCronTable
	 * @see  #setCronTable
	 * @see  #addLinuxServerAccount
	 * @see  #removeLinuxServerAccount
	 */
	public String getCronTable(
		UserId username,
		String aoServer
	) throws IllegalArgumentException, IOException, SQLException {
		return getLinuxServerAccount(aoServer, username).getCronTable();
	}

	/**
	 * Gets the list of email addresses that an <code>EmailList</code> will be forwarded to.
	 *
	 * @param  path  the path of the list
	 * @param  aoServer  the server this list is part of
	 *
	 * @return  the list of addresses, one address per line separated by <code>'\n'</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if unable to find the <code>EmailList</code>
	 *
	 * @see  EmailList#getAddressList
	 * @see  #addEmailList
	 * @see  #setEmailListAddressList
	 * @see  List
	 */
	public String getEmailListAddressList(
		UnixPath path,
		String aoServer
	) throws IllegalArgumentException, IOException, SQLException {
		return getEmailList(aoServer, path).getAddressList();
	}

	/**
	 * Gets the total size of a <code>BackupPartition</code>.
	 *
	 * @param  aoServer  the hostname of the server
	 * @param  path  the path of the <code>BackupPartition</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if unable to find the <code>AOServer</code> or <cdoe>BackupPartition</code>
	 *
	 * @see  BackupPartition#getDiskTotalSize
	 */
	public long getBackupPartitionTotalSize(
		String aoServer,
		String path
	) throws IllegalArgumentException, IOException, SQLException {
		BackupPartition bp=getAOServer(aoServer).getBackupPartitionForPath(path);
		if(bp==null) throw new IllegalArgumentException("Unable to find BackupPartition: "+path+" on "+aoServer);
		return bp.getDiskTotalSize();
	}

	/**
	 * Gets the used size of a <code>BackupPartition</code>.
	 *
	 * @param  aoServer  the hostname of the server
	 * @param  path  the path of the <code>BackupPartition</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if unable to find the <code>AOServer</code> or <cdoe>BackupPartition</code>
	 *
	 * @see  BackupPartition#getDiskUsedSize
	 */
	public long getBackupPartitionUsedSize(
		String aoServer,
		String path
	) throws IllegalArgumentException, IOException, SQLException {
		BackupPartition bp=getAOServer(aoServer).getBackupPartitionForPath(path);
		if(bp==null) throw new IllegalArgumentException("Unable to find BackupPartition: "+path+" on "+aoServer);
		return bp.getDiskUsedSize();
	}

	/**
	 * Gets the last reported activity for a <code>FailoverFileReplication</code>.
	 *
	 * @param  fromServer  the server that is being backed-up
	 * @param  toServer  the hostname of the server the stores the backups
	 * @param  path  the path of the <code>BackupPartition</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code>, <code>AOServer</code>, <cdoe>BackupPartition</code>, or <code>FailoverFileReplication</code>
	 *
	 * @see  FailoverFileReplication#getActivity()
	 */
	public FileReplication.Activity getFailoverFileReplicationActivity(
		String fromServer,
		String toServer,
		String path
	) throws IllegalArgumentException, IOException, SQLException {
		return getFailoverFileReplication(fromServer, toServer, path).getActivity();
	}

	/**
	 * @see  HttpdServer#getConcurrency()
	 *
	 * @param  aoServer  the server hosting the account
	 * @param  name      the name of the instance of {@code null} for the default instance
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @throws IllegalArgumentException if unable to find the {@link Host}, {@link Server}, or {@link HttpdServer}
	 */
	public int getHttpdServerConcurrency(
		String aoServer,
		String name
	) throws IllegalArgumentException, IOException, SQLException {
		return getHttpdServer(aoServer, name).getConcurrency();
	}

	/**
	 * Gets the attributes of an inbox.
	 *
	 * @param  username  the username of the <code>LinuxServerAccount</code>
	 * @param  aoServer    the server hosting the account
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if unable to find the <cdoe>LinuxServerAccount</code>
	 *
	 * @see  LinuxServerAccount#getInboxAttributes
	 */
	public InboxAttributes getInboxAttributes(
		UserId username,
		String aoServer
	) throws IllegalArgumentException, IOException, SQLException {
		return getLinuxServerAccount(aoServer, username).getInboxAttributes();
	}

	/**
	 * Gets the IMAP folder sizes for an  inbox.
	 *
	 * @param  username  the username of the <code>LinuxServerAccount</code>
	 * @param  aoServer    the server hosting the account
	 * @param  folderNames  the folder names
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if unable to find the <cdoe>LinuxServerAccount</code>
	 *
	 * @see  LinuxServerAccount#getImapFolderSizes
	 */
	public long[] getImapFolderSizes(
		UserId username,
		String aoServer,
		String[] folderNames
	) throws IllegalArgumentException, IOException, SQLException {
		return getLinuxServerAccount(aoServer, username).getImapFolderSizes(folderNames);
	}

	/**
	 * Gets the info file for a <code>MajordomoList</code>.
	 *
	 * @param  domain  the domain of the <code>MajordomoServer</code>
	 * @param  aoServer  the hostname of the <code>AOServer</code>
	 * @param  listName  the name of the new list
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data
	 *					integrity violation occurs
	 * @exception  IllegalArgumentException  if the name is not valid or unable to find the
	 *                                  <code>Server</code>, code>EmailDomain</code>,
	 *                                  <code>MajordomoServer</code>, or <code>MajordomoList</code>
	 *
	 * @see  MajordomoList#getInfoFile
	 * @see  #addMajordomoList
	 * @see  #removeEmailList
	 */
	public String getMajordomoInfoFile(
		DomainName domain,
		String aoServer,
		String listName
	) throws IllegalArgumentException, IOException, SQLException {
		Domain ed=getEmailDomain(aoServer, domain);
		MajordomoServer ms=ed.getMajordomoServer();
		if(ms==null) throw new IllegalArgumentException("Unable to find MajordomoServer: "+domain+" on "+aoServer);
		MajordomoList ml=ms.getMajordomoList(listName);
		if(ml==null) throw new IllegalArgumentException("Unable to find MajordomoList: "+listName+'@'+domain+" on "+aoServer);
		return ml.getInfoFile();
	}

	/**
	 * Gets the intro file for a <code>MajordomoList</code>.
	 *
	 * @param  domain  the domain of the <code>MajordomoServer</code>
	 * @param  aoServer  the hostname of the <code>AOServer</code>
	 * @param  listName  the name of the new list
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data
	 *					integrity violation occurs
	 * @exception  IllegalArgumentException  if the name is not valid or unable to find the
	 *                                  <code>Server</code>, code>EmailDomain</code>,
	 *                                  <code>MajordomoServer</code>, or <code>MajordomoList</code>
	 *
	 * @see  MajordomoList#getIntroFile
	 * @see  #addMajordomoList
	 * @see  #removeEmailList
	 */
	public String getMajordomoIntroFile(
		DomainName domain,
		String aoServer,
		String listName
	) throws IllegalArgumentException, IOException, SQLException {
		Domain ed=getEmailDomain(aoServer, domain);
		MajordomoServer ms=ed.getMajordomoServer();
		if(ms==null) throw new IllegalArgumentException("Unable to find MajordomoServer: "+domain+" on "+aoServer);
		MajordomoList ml=ms.getMajordomoList(listName);
		if(ml==null) throw new IllegalArgumentException("Unable to find MajordomoList: "+listName+'@'+domain+" on "+aoServer);
		return ml.getIntroFile();
	}

	/**
	 * Gets the contents of a MRTG file.
	 *
	 * @param  aoServer  the hostname of the server to get the file from
	 * @param  filename  the filename on the server
	 * @param  out  the <code>OutputStream</code> to write the file to
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if unable to find the <cdoe>Server</code> or <code>AOServer</code>
	 *
	 * @see  AOServer#getMrtgFile
	 */
	public void getMrtgFile(
		String aoServer,
		String filename,
		OutputStream out
	) throws IllegalArgumentException, IOException, SQLException {
		getAOServer(aoServer).getMrtgFile(filename, out);
	}

	/**
	 * Gets the current status of the UPS.
	 *
	 * @param  aoServer  the hostname of the server to get the file from
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if unable to find the <cdoe>Server</code> or <code>AOServer</code>
	 *
	 * @see  AOServer#getMrtgFile
	 */
	public String getUpsStatus(
		String aoServer
	) throws IllegalArgumentException, IOException, SQLException {
		return getAOServer(aoServer).getUpsStatus();
	}

	/**
	 * Gets the contents of an AWStats file.
	 *
	 * @param  siteName  the site name
	 * @param  aoServer  the hostname of the server to get the file from
	 * @param  path  the filename on the server
	 * @param  queryString  the query string for the request
	 * @param  out  the <code>OutputStream</code> to write the file to
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if unable to find the <cdoe>Server</code>, <code>AOServer</code>, or <code>HttpdSite</code>
	 *
	 * @see  HttpdSite#getAWStatsFile
	 */
	public void getAWStatsFile(
		String siteName,
		String aoServer,
		String path,
		String queryString,
		OutputStream out
	) throws IllegalArgumentException, IOException, SQLException {
		getHttpdSite(aoServer, siteName).getAWStatsFile(path, queryString, out);
	}

	/**
	 * Gets the name of the root <code>Business</code> in the tree of <code>Business</code>es.
	 *
	 * @return  the accounting code of the root <code>Business</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 *
	 * @see  BusinessTable#getRootAccounting
	 */
	public AccountingCode getRootBusiness() throws IOException, SQLException {
		return connector.getAccount().getBusinesses().getRootAccounting();
	}

	/**
	 * Places a <code>Ticket</code> in the hold state.  When in a hold state, a <code>Ticket</code>
	 * is not being worked on because the support personnel are waiting for something out of their
	 * immediate control.
	 *
	 * @param  ticket_id  the pkey of the <code>Ticket</code>
	 * @param  comments  the details of the change
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Ticket</code>,
	 *					<code>TicketType</code>, or <code>BusinessAdministrator</code>
	 *
	 * @see  Ticket#actHoldTicket
	 * @see  #addTicket
	 * @see  Action
	 */
	/*
	public void holdTicket(
		int ticket_id,
		String comments
	) throws IllegalArgumentException, IOException, SQLException {
		Ticket ti=connector.getTickets().get(ticket_id);
		if(ti==null) throw new IllegalArgumentException("Unable to find Ticket: "+ticket_id);
		ti.actHoldTicket(comments);
	}*/

	/**
	 * Initializes the password files for an <code>HttpdSite</code>.  These files are
	 * typically contained in <code>/www/<i>sitename</i>/conf/passwd</code> and
	 * <code>/www/<i>sitename</i>/conf/group</code>.
	 *
	 * @param  siteName  the name of the site to initialize
	 * @param  aoServer  the hostname of the <code>AOServer</code>
	 * @param  username  the username granted access to the site
	 * @param  password  the password for that username
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>AOServer</code> or
	 *					<code>HttpdSite</code>
	 *
	 * @see  HttpdSite#initializePasswdFile
	 * @see  #addHttpdTomcatStdSite
	 */
	/*
	public void initializeHttpdSitePasswdFile(
		String siteName,
		String aoServer,
		String username,
		String password
	) {
		getHttpdSite(aoServer, siteName).initializePasswdFile(username, password);
	}
	 */

	private static final int numTables = Table.TableID.values().length;

	/**
	 * Invalidates a table, causing all caches of the table to be removed and all configurations
	 * based on the table to be reevaluated.
	 *
	 * @param  tableID  the ID of the <code>AOServTable</code> to invalidate
	 * @param  server  the server that should be invalidated or <code>null or ""</code> for none, accepts ao_servers.hostname, servers.package||'/'||servers.name, or servers.pkey
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if the table ID is invalid
	 *
	 * @see  AOServConnector#invalidateTable
	 * @see  BusinessAdministrator#isActiveTableInvalidator
	 */
	public void invalidate(
		int tableID,
		String server
	) throws IllegalArgumentException, SQLException, IOException {
		if(tableID<0 || tableID>=numTables) throw new IllegalArgumentException("Invalid table ID: "+tableID);
		Host se;
		if(server!=null && server.length()==0) server=null;
		if(server==null) se=null;
		else {
			se = connector.getNet().getServers().get(server);
			if(se==null) throw new IllegalArgumentException("Unable to find Server: "+server);
		}
		connector.invalidateTable(tableID, se==null ? -1 : se.pkey);
	}

	/**
	 * Determines if an accounting code is available.
	 *
	 * @param  accounting  the accounting code
	 *
	 * @return  <code>true</code> if the accounting code is available
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 *
	 * @see  BusinessTable#isAccountingAvailable
	 * @see  #checkAccounting
	 * @see  #addBusiness
	 * @see  #generateAccountingCode
	 * @see  Account
	 */
	public boolean isAccountingAvailable(
		AccountingCode accounting
	) throws SQLException, IOException {
		return connector.getAccount().getBusinesses().isAccountingAvailable(accounting);
	}

	/**
	 * Determines if a <code>BusinessAdministrator</code> currently has a password set.
	 *
	 * @param  username  the username of the administrator
	 *
	 * @return  if the <code>BusinessAdministrator</code> has a password set
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if the <code>BusinessAdministrator</code> is not found
	 *
	 * @see  BusinessAdministrator#arePasswordsSet
	 * @see  #setBusinessAdministratorPassword
	 * @see  Administrator
	 */
	public boolean isBusinessAdministratorPasswordSet(
		UserId username
	) throws IllegalArgumentException, IOException, SQLException {
		Administrator ba=connector.getAccount().getBusinessAdministrators().get(username);
		if(ba==null) throw new IllegalArgumentException("Unable to find BusinessAdministrator: "+username);
		return ba.arePasswordsSet()==PasswordProtected.ALL;
	}

	/**
	 * Determines if a <code>DNSZone</code> is available.
	 *
	 * @param  zone  the zone in <code>domain.tld.</code> format
	 *
	 * @return  <code>true</code> if the <code>DNSZone</code> is available
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 *
	 * @see  DNSZoneTable#isDNSZoneAvailable
	 * @see  #addDNSZone
	 * @see  Zone
	 */
	public boolean isDNSZoneAvailable(
		String zone
	) throws IOException, SQLException {
		return connector.getDns().getDnsZones().isDNSZoneAvailable(zone);
	}

	/**
	 * Determines if an <code>IPAddress</code> is currently being used.
	 *
	 * @param  ipAddress  the IP address
	 *
	 * @return  <code>true</code> if the <code>IPAddress</code> is in use
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if unable to find the <code>IPAddress</code>
	 *
	 * @see  IPAddress#isUsed
	 * @see  #setIPAddressPackage
	 */
	public boolean isIPAddressUsed(
		InetAddress ipAddress,
		String server,
		String net_device
	) throws IllegalArgumentException, IOException, SQLException {
		return getIPAddress(server, net_device, ipAddress).isUsed();
	}

	/**
	 * Determines if a groupname is available.
	 *
	 * @param  groupname  the groupname
	 *
	 * @return  <code>true</code> if the groupname is available
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 *
	 * @see  LinuxGroupTable#isLinuxGroupNameAvailable
	 * @see  #addLinuxGroup
	 * @see  Group
	 */
	public boolean isLinuxGroupNameAvailable(
		GroupId groupname
	) throws IOException, SQLException {
		return connector.getLinux().getLinuxGroups().isLinuxGroupNameAvailable(groupname);
	}

	/**
	 * Determines if a <code>LinuxServerAccount</code> currently has a password set.
	 *
	 * @param  username  the username of the account
	 * @param  aoServer  the server the account is hosted on
	 *
	 * @return  if the <code>LinuxServerAccount/code> has a password set
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if the <code>LinuxServerAccount</code> is not found
	 *
	 * @see  LinuxServerAccount#arePasswordsSet
	 * @see  #setLinuxServerAccountPassword
	 * @see  UserServer
	 */
	public boolean isLinuxServerAccountPasswordSet(
		UserId username,
		String aoServer
	) throws IllegalArgumentException, IOException, SQLException {
		return getLinuxServerAccount(aoServer, username).arePasswordsSet()==PasswordProtected.ALL;
	}

	/**
	 * Determines if a <code>LinuxServerAccount</code> is currently in manual procmail mode.  Manual
	 * procmail mode is initiated when the header comment in the .procmailrc file is altered or removed.
	 *
	 * @param  username  the username of the account
	 * @param  aoServer  the server the account is hosted on
	 *
	 * @return  if the <code>LinuxServerAccount/code> is in manual mode
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if the <code>LinuxServerAccount</code> is not found
	 *
	 * @see  LinuxServerAccount#isProcmailManual
	 * @see  UserServer
	 */
	public int isLinuxServerAccountProcmailManual(
		UserId username,
		String aoServer
	) throws IllegalArgumentException, IOException, SQLException {
		return getLinuxServerAccount(aoServer, username).isProcmailManual();
	}

	/**
	 * Determines if a <code>MySQLDatabase</code> name is available on the specified
	 * <code>Server</code>.
	 *
	 * @param  name  the name of the database
	 * @param  aoServer  the hostname of the <code>Server</code>
	 *
	 * @return  <code>true</code> if the <code>MySQLDatabase</code> is available
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if the database name is invalid or unable
	 *					to find the <code>Server</code>
	 *
	 * @see  MySQLServer#isMySQLDatabaseNameAvailable
	 * @see  #checkMySQLDatabaseName
	 */
	public boolean isMySQLDatabaseNameAvailable(
		MySQLDatabaseName name,
		MySQLServerName mysqlServer,
		String aoServer
	) throws IllegalArgumentException, IOException, SQLException {
		return getMySQLServer(aoServer, mysqlServer).isMySQLDatabaseNameAvailable(name);
	}

	/**
	 * Determines if a <code>MySQLServer</code> name is available on the specified
	 * <code>Server</code>.
	 *
	 * @param  name  the name of the MySQL server
	 * @param  aoServer  the hostname of the <code>Server</code>
	 *
	 * @return  <code>true</code> if the <code>MySQLServer</code> is available
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if the server name is invalid or unable
	 *					to find the <code>Server</code>
	 *
	 * @see  AOServer#isMySQLServerNameAvailable
	 * @see  #checkMySQLServerName
	 */
	public boolean isMySQLServerNameAvailable(
		MySQLServerName name,
		String aoServer
	) throws IllegalArgumentException, IOException, SQLException {
		return getAOServer(aoServer).isMySQLServerNameAvailable(name);
	}

	/**
	 * Determines if a <code>MySQLServerUser</code> currently has a password set.
	 *
	 * @param  username  the username of the account
	 * @param  aoServer  the server the account is hosted on
	 *
	 * @return  if the <code>MySQLServerUser</code> has a password set
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if the <code>MySQLServerUser</code> is not found
	 *
	 * @see  MySQLServerUser#arePasswordsSet
	 * @see  #setMySQLServerUserPassword
	 * @see  UserServer
	 */
	public boolean isMySQLServerUserPasswordSet(
		MySQLUserId username,
		MySQLServerName mysqlServer,
		String aoServer
	) throws IllegalArgumentException, IOException, SQLException {
		return getMySQLServerUser(aoServer, mysqlServer, username).arePasswordsSet()==PasswordProtected.ALL;
	}

	/**
	 * Determines if a <code>Package</code> name is available.
	 *
	 * @param  packageName  the name of the <code>Package</code>
	 *
	 * @return  <code>true</code> if the <code>Package</code> name is available
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 *
	 * @see  PackageTable#isPackageNameAvailable
	 * @see  #generatePackageName
	 * @see  #addPackage
	 * @see  Package
	 */
	public boolean isPackageNameAvailable(
		AccountingCode packageName
	) throws IOException, SQLException {
		return connector.getBilling().getPackages().isPackageNameAvailable(packageName);
	}

	/**
	 * Determines if a <code>PostgresDatabase</code> name is available on the specified
	 * <code>Server</code>.
	 *
	 * @param  name  the name of the database
	 * @param  postgresServer  the name of the PostgreSQL server
	 * @param  aoServer  the hostname of the <code>Server</code>
	 *
	 * @return  <code>true</code> if the <code>PostgresDatabase</code> is available
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if the database name is invalid or unable
	 *					to find the <code>Server</code>
	 *
	 * @see  PostgresServer#isPostgresDatabaseNameAvailable
	 * @see  #checkPostgresDatabaseName
	 */
	public boolean isPostgresDatabaseNameAvailable(
		PostgresDatabaseName name,
		PostgresServerName postgresServer,
		String aoServer
	) throws IllegalArgumentException, IOException, SQLException {
		return getPostgresServer(aoServer, postgresServer).isPostgresDatabaseNameAvailable(name);
	}

	/**
	 * Determines if a <code>PostgresServer</code> name is available on the specified
	 * <code>Server</code>.
	 *
	 * @param  name  the name of the PostgreSQL server
	 * @param  aoServer  the hostname of the <code>Server</code>
	 *
	 * @return  <code>true</code> if the <code>PostgresServer</code> is available
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if the server name is invalid or unable
	 *					to find the <code>Server</code>
	 *
	 * @see  AOServer#isPostgresServerNameAvailable
	 * @see  #checkPostgresServerName
	 */
	public boolean isPostgresServerNameAvailable(
		PostgresServerName name,
		String aoServer
	) throws IllegalArgumentException, IOException, SQLException {
		return getAOServer(aoServer).isPostgresServerNameAvailable(name);
	}

	/**
	 * Determines if a <code>PostgresServerUser</code> currently has a password set.
	 *
	 * @param  username  the username of the account
	 * @param  postgresServer  the name of the PostgreSQL server
	 * @param  aoServer  the server the account is hosted on
	 *
	 * @return  if the <code>PostgresServerUser</code> has a password set
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if the <code>PostgresServerUser</code> is not found
	 *
	 * @see  PostgresServerUser#arePasswordsSet
	 * @see  #setPostgresServerUserPassword
	 * @see  UserServer
	 */
	public boolean isPostgresServerUserPasswordSet(
		PostgresUserId username,
		PostgresServerName postgresServer,
		String aoServer
	) throws IllegalArgumentException, IOException, SQLException {
		return getPostgresServerUser(aoServer, postgresServer, username).arePasswordsSet()==PasswordProtected.ALL;
	}

	/**
	 * Determines if a <code>EmailDomain</code> is available.
	 *
	 * @param  domain  the domain
	 * @param  aoServer  the hostname of the server
	 *
	 * @return  <code>true</code> if the <code>EmailDomain</code> is available
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if the <code>EmailDomain</code> is invalid
	 *
	 * @see  AOServer#isEmailDomainAvailable
	 * @see  #addEmailDomain
	 * @see  Domain
	 */
	public boolean isEmailDomainAvailable(
		DomainName domain,
		String aoServer
	) throws IllegalArgumentException, IOException, SQLException {
		return getAOServer(aoServer).isEmailDomainAvailable(domain);
	}

	/**
	 * Determines if a name is available for use as a <code>HttpdSharedTomcat</code>.
	 *
	 * @param  name  the name
	 *
	 * @return  <code>true</code> if the name is available
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 *
	 * @see  HttpdSharedTomcatTable#isSharedTomcatNameAvailable
	 * @see  #generateSharedTomcatName
	 * @see  SharedTomcat
	 */
	public boolean isSharedTomcatNameAvailable(
		String name
	) throws IOException, SQLException{
		return connector.getWeb_tomcat().getHttpdSharedTomcats().isSharedTomcatNameAvailable(name);
	}

	/**
	 * Determines if a site name is available.
	 *
	 * @param  siteName  the site name
	 *
	 * @return  <code>true</code> if the site name is available
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if the site name is invalid
	 *
	 * @see  HttpdSiteTable#isSiteNameAvailable
	 */
	public boolean isSiteNameAvailable(
		String siteName
	) throws IllegalArgumentException, IOException, SQLException {
		checkSiteName(siteName);
		return connector.getWeb().getHttpdSites().isSiteNameAvailable(siteName);
	}

	/**
	 * Determines if a <code>Username</code> is available.
	 *
	 * @param  username  the username
	 *
	 * @return  <code>true</code> if the <code>Username</code> is available
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 *
	 * @see  UsernameTable#isUsernameAvailable
	 * @see  #addUsername
	 * @see  Username
	 */
	public boolean isUsernameAvailable(
		UserId username
	) throws IOException, SQLException {
		return connector.getAccount().getUsernames().isUsernameAvailable(username);
	}

	/**
	 * Kills a <code>Ticket</code>.  Once killed, a <code>Ticket</code> may not be modified in
	 * any way.
	 *
	 * @param  ticket_id  the pkey of the <code>Ticket</code>
	 * @param  business_administrator  the username of the <code>BusinessAdministrator</code>
	 *					making the change
	 * @param  comments  the details of the change
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Ticket</code>,
	 *					<code>TicketType</code>, or <code>BusinessAdministrator</code>
	 *
	 * @see  Ticket#actKillTicket
	 * @see  #addTicket
	 * @see  Action
	 */
	/*
	public void killTicket(
		int ticket_id,
		String business_administrator,
		String comments
	) throws IllegalArgumentException, IOException, SQLException {
		Ticket ti=connector.getTickets().get(ticket_id);
		if(ti==null) throw new IllegalArgumentException("Unable to find Ticket: "+ticket_id);
		BusinessAdministrator pe=connector.getBusinessAdministrators().get(business_administrator);
		if(pe==null) throw new IllegalArgumentException("Unable to find BusinessAdministrator: "+business_administrator);
		ti.actKillTicket(pe, comments);
	}*/

	/**
	 * Moves all resources for one <code>Business</code> from one <code>Server</code>
	 * to another <code>Server</code>.
	 *
	 * @param  business  the accounting code of the <code>Business</code>
	 * @param  from  the hostname of the <code>Server</code> to get all the resources from
	 * @param  to  the hostname of the <code>Server</code> to place all the resources on
	 * @param  out  an optional <code>PrintWriter</code> to send diagnostic output to
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if unable to find the <code>Business</code> or either
	 *					of the <code>Server</code>s
	 *
	 * @see  Business#move
	 */
	public void moveBusiness(
		AccountingCode business,
		String from,
		String to,
		TerminalWriter out
	) throws IllegalArgumentException, IOException, SQLException {
		getBusiness(business).move(getAOServer(from), getAOServer(to), out);
	}

	/**
	 * Moves an <code>IPAddress</code> from one <code>Server</code> to another.
	 *
	 * @param  ip_address  the IP address to move
	 * @param  to_server  the destination server
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if unable to find the <code>IPAddress</code> or
	 *					the <code>Server</code>
	 *
	 * @see  IPAddress#moveTo
	 */
	public void moveIPAddress(
		InetAddress ip_address,
		String from_server,
		String from_net_device,
		String to_server
	) throws IllegalArgumentException, IOException, SQLException {
		getIPAddress(from_server, from_net_device, ip_address).moveTo(getServer(to_server));
	}

	/**
	 * Times the latency of the communication with the server.
	 *
	 * @return  the latency of the communication in milliseconds
	 *
	 * @see  AOServConnector#ping
	 */
	public int ping() throws IOException, SQLException {
		return connector.ping();
	}

	/**
	 * Prints the contents of a <code>DNSZone</code> as used by the <code>named</code> process.
	 *
	 * @param  zone  the name of the <code>DNSZone</code>
	 * @param  out  the <code>PrintWriter</code> to write to
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>DNSZone</code>
	 *
	 * @see  DNSZone#printZoneFile
	 * @see  #addDNSZone
	 */
	public void printZoneFile(
		String zone,
		PrintWriter out
	) throws IllegalArgumentException, SQLException, IOException {
		getDNSZone(zone).printZoneFile(out);
	}

	/**
	 * Reactivates a <code>Ticket</code> that is in the hold state.
	 *
	 * @param  ticket_id  the pkey of the <code>Ticket</code>
	 * @param  business_administrator  the username of the <code>BusinessAdministrator</code>
	 *					making the change
	 * @param  comments  the details of the change
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Ticket</code>,
	 *					<code>TicketType</code>, or <code>BusinessAdministrator</code>
	 *
	 * @see  Ticket#actReactivateTicket
	 * @see  #addTicket
	 * @see  Action
	 */
	/*
	public void reactivateTicket(
		int ticket_id,
		String business_administrator,
		String comments
	) throws IllegalArgumentException, IOException, SQLException {
		Ticket ti=connector.getTickets().get(ticket_id);
		if(ti==null) throw new IllegalArgumentException("Unable to find Ticket: "+ticket_id);
		BusinessAdministrator pe=connector.getBusinessAdministrators().get(business_administrator);
		if(pe==null) throw new IllegalArgumentException("Unable to find BusinessAdministrator: "+business_administrator);
		ti.actReactivateTicket(pe, comments);
	}*/

	/**
	 * Refreshes the time window for SMTP server access by resetting the expiration to 24 hours from the current time.
	 *
	 * @param  pkey  the <code>pkey</code> of the <code>EmailSmtpRelay</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>EmailSmtpRelay</code>
	 *
	 * @see  EmailSmtpRelay#refresh
	 * @see  #addEmailSmtpRelay
	 * @see  SmtpRelay
	 */
	public void refreshEmailSmtpRelay(
		int pkey,
		long minDuration
	) throws IllegalArgumentException, IOException, SQLException {
		SmtpRelay sr=connector.getEmail().getEmailSmtpRelays().get(pkey);
		if(sr==null) throw new IllegalArgumentException("Unable to find EmailSmtpRelay: "+pkey);
		sr.refresh(minDuration);
	}

	/**
	 * Removes a <code>BlackholeEmailAddress</code> from the system.
	 *
	 * @param  address  the part of the email address before the <code>@</code>
	 * @param  domain  the part of the email address after the <code>@</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>EmailDomain</code>,
	 *					<code>EmailAddress</code>, or <code>BlackholeEmailAddress</code>
	 *
	 * @see  BlackholeEmailAddress#remove
	 */
	public void removeBlackholeEmailAddress(
		String address,
		DomainName domain,
		String aoServer
	) throws IllegalArgumentException, IOException, SQLException {
		Address addr=getEmailAddress(aoServer, domain, address);
		BlackholeAddress bea=addr.getBlackholeEmailAddress();
		if(bea==null) throw new IllegalArgumentException("Unable to find BlackholeEmailAddress: "+address+'@'+domain+" on "+aoServer);
		bea.remove();
		if(addr.getCannotRemoveReasons().isEmpty() && !addr.isUsed()) addr.remove();
	}

	/**
	 * Removes a <code>BusinessAdministrator</code> from the system.
	 *
	 * @param  username  the <code>username</code> of the <code>BusinessAdministrator</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Username</code> or
	 *                                  <code>BusinessAdministrator</code>
	 *
	 * @see  BusinessAdministrator#remove
	 * @see  #addBusinessAdministrator
	 */
	public void removeBusinessAdministrator(
		UserId username
	) throws IllegalArgumentException, IOException, SQLException {
		Username un=getUsername(username);
		Administrator ba=un.getBusinessAdministrator();
		if(ba==null) throw new IllegalArgumentException("Unable to find BusinessAdministrator: "+username);
		ba.remove();
	}

	/**
	 * Revokes a <code>Business</code>es access to a <code>Server</code>.  The server
	 * must not have any resources allocated for the business, and the server must not
	 * be the default server for the business.
	 *
	 * @param  accounting  the accounting code of the business
	 * @param  server  the hostname of the server
	 *
	 * @exception  IOException  if unable to communicate with the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the business or server
	 *
	 * @see  AccountHost
	 * @see  BusinessServer#remove
	 * @see  #addBusinessServer
	 * @see  #setDefaultBusinessServer
	 */
	public void removeBusinessServer(
		AccountingCode accounting,
		String server
	) throws IllegalArgumentException, IOException, SQLException {
		Account bu=getBusiness(accounting);
		Host se=getServer(server);
		AccountHost bs=bu.getBusinessServer(se);
		if(bs==null) throw new IllegalArgumentException("Unable to find BusinessServer: accounting="+accounting+" and server="+server);
		bs.remove();
	}

	/**
	 * Removes a <code>CreditCard</code>.
	 *
	 * @param  pkey  the <code>pkey</code> of the <code>CreditCard</code> to remove
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>CreditCard</code>
	 *
	 * @see  CreditCard#remove
	 */
	public void removeCreditCard(
		int pkey
	) throws IllegalArgumentException, SQLException, IOException {
		CreditCard cc=connector.getPayment().getCreditCards().get(pkey);
		if(cc==null) throw new IllegalArgumentException("Unable to find CreditCard: "+pkey);
		cc.remove();
	}

	/**
	 * Removes a <code>CvsRepository</code>.
	 *
	 * @param  aoServer  the hostname of the <code>Server</code>
	 * @param  path  the path of the repository
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or
	 *                                  <code>CvsRepository</code>
	 *
	 * @see  CvsRepository#remove
	 * @see  #addCvsRepository
	 * @see  CvsRepository
	 */
	public void removeCvsRepository(
		String aoServer,
		UnixPath path
	) throws IllegalArgumentException, IOException, SQLException {
		Server ao=getAOServer(aoServer);
		CvsRepository cr=ao.getCvsRepository(path);
		if(cr==null) throw new IllegalArgumentException("Unable to find CvsRepository: "+path+" on "+aoServer);
		cr.remove();
	}

	/**
	 * Removes one record from a <code>DNSZone</code>.
	 *
	 * @param  pkey  the <code>pkey</code> of the <code>DNSRecord</code> to remove
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>DNSRecord</code>
	 *
	 * @see  DNSRecord#remove
	 * @see  #addDNSRecord
	 * @see  Record
	 */
	public void removeDNSRecord(
		int pkey
	) throws IllegalArgumentException, IOException, SQLException {
		Record nr=connector.getDns().getDnsRecords().get(pkey);
		if(nr==null) throw new IllegalArgumentException("Unable to find DNSRecord: "+pkey);
		nr.remove();
	}

	public void removeDNSRecord(
		String zone,
		String domain,
		String type,
		String destination
	) throws IllegalArgumentException, IOException, SQLException {
		Zone nz=getDNSZone(zone);

		// Must be a valid type
		RecordType nt=connector.getDns().getDnsTypes().get(type);
		if(nt==null) throw new IllegalArgumentException("Unable to find DNSType: "+type);
		// Must have a valid destination type
		nt.checkDestination(destination);

		// Find the record matching all four fields, should be one and *only* one
		Record found = null;
		for(Record record : nz.getDNSRecords(domain, nt)) {
			if(record.getDestination().equals(destination)) {
				if(found != null) throw new AssertionError("Duplicate DNSRecord: (" + zone + ", " + domain + ", " + type + ", " + destination + ")");
				found = record;
			}
		}
		if(found == null) throw new AssertionError("Unable to find DNSRecord: (" + zone + ", " + domain + ", " + type + ", " + destination + ")");
		found.remove();
	}

	/**
	 * Completely removes a <code>DNSZone</code> from the servers.
	 *
	 * @param  zone  the name of the <code>DNSZone</code> to remove
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>DNSZone</code>
	 *
	 * @see  DNSZone#remove
	 * @see  #addDNSZone
	 * @see  Zone
	 */
	public void removeDNSZone(
		String zone
	) throws IllegalArgumentException, IOException, SQLException {
		getDNSZone(zone).remove();
	}

	/**
	 * Completely removes a <code>DNSZone</code> from the servers.
	 *
	 * @param  zone  the name of the <code>DNSZone</code> to remove
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>DNSZone</code>
	 *
	 * @see  DNSZone#remove
	 * @see  #addDNSZone
	 * @see  Zone
	 */
	public void setDNSZoneTTL(
		String zone,
		int ttl
	) throws IllegalArgumentException, IOException, SQLException {
		getDNSZone(zone).setTTL(ttl);
	}

	/**
	 * Removes an <code>EmailAddress</code> from the system.  If the <code>EmailAddress</code> is used
	 * by other resources, such as <code>EmailListAddress</code>es, those resources are also removed.
	 *
	 * @param  address  the part of the email address before the <code>@</code>
	 * @param  domain  the part of the email address after the <code>@</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>EmailDomain</code> or
	 *					<code>EmailAddress</code>
	 *
	 * @see  EmailAddress#remove
	 * @see  #addEmailForwarding
	 * @see  #addEmailListAddress
	 * @see  #addEmailPipeAddress
	 * @see  #addLinuxAccAddress
	 */
	public void removeEmailAddress(
		String address,
		DomainName domain,
		String aoServer
	) throws IllegalArgumentException, IOException, SQLException {
		getEmailAddress(aoServer, domain, address).remove();
	}

	/**
	 * Removes an <code>EmailForwarding</code> from the system.
	 *
	 * @param  address  the part of the email address before the <code>@</code>
	 * @param  domain  the part of the email address after the <code>@</code>
	 * @param  aoServer  the hostname of the server that hosts this domain
	 * @param  destination  the destination of the email forwarding
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>EmailDomain</code>,
	 *					<code>EmailAddress</code>, or <code>EmailForwarding</code>
	 *
	 * @see  EmailForwarding#remove
	 * @see  #addEmailForwarding
	 */
	public void removeEmailForwarding(
		String address,
		DomainName domain,
		String aoServer,
		Email destination
	) throws IllegalArgumentException, IOException, SQLException {
		Address addr=getEmailAddress(aoServer, domain, address);
		Forwarding ef=addr.getEmailForwarding(destination);
		if(ef==null) throw new IllegalArgumentException("Unable to find EmailForwarding: "+address+'@'+domain+"->"+destination+" on "+aoServer);
		ef.remove();
		if(addr.getCannotRemoveReasons().isEmpty() && !addr.isUsed()) addr.remove();
	}

	/**
	 * Removes an <code>EmailList</code> from the system.  All <code>EmailAddress</code>es that are directed
	 * to the list are also removed.  The file that stores the list contents is removed from the file system.
	 *
	 * @param  path  the path of the <code>EmailList</code> to remove
	 * @param  aoServer  the server that hosts this list
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>EmailList</code>
	 *
	 * @see  EmailList#remove
	 * @see  #addEmailList
	 */
	public void removeEmailList(
		UnixPath path,
		String aoServer
	) throws IllegalArgumentException, IOException, SQLException {
		getEmailList(aoServer, path).remove();
	}

	/**
	 * Removes an <code>EmailListAddress</code> from the system.
	 *
	 * @param  address  the part of the email address before the <code>@</code>
	 * @param  domain  the part of the email address after the <code>@</code>
	 * @param  path  the list the emails are sent to
	 * @param  aoServer  the hostname of the server hosting the list
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>EmailDomain</code>,
	 *					<code>EmailAddress</code>, <code>EmailList</code>, or
	 *                                  <code>EmailListAddress</code>
	 *
	 * @see  EmailListAddress#remove
	 * @see  #addEmailListAddress
	 */
	public void removeEmailListAddress(
		String address,
		DomainName domain,
		UnixPath path,
		String aoServer
	) throws IllegalArgumentException, IOException, SQLException {
		Address addr=getEmailAddress(aoServer, domain, address);
		com.aoindustries.aoserv.client.email.List el=getEmailList(aoServer, path);
		ListAddress ela=addr.getEmailListAddress(el);
		if(ela==null) throw new IllegalArgumentException("Unable to find EmailListAddress: "+address+'@'+domain+"->"+path+" on "+aoServer);
		ela.remove();
		if(addr.getCannotRemoveReasons().isEmpty() && !addr.isUsed()) addr.remove();
	}

	/**
	 * Removes an <code>EmailPipe</code> from the system.  All <code>EmailAddress</code>es that are directed
	 * to the pipe are also removed.
	 *
	 * @param  pkey  the <code>pkey</code> of the <code>EmailPipe</code> to remove
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>EmailPipe</code>
	 *
	 * @see  EmailPipe#remove
	 * @see  #addEmailPipe
	 */
	public void removeEmailPipe(
		int pkey
	) throws IllegalArgumentException, IOException, SQLException {
		Pipe ep=connector.getEmail().getEmailPipes().get(pkey);
		if(ep==null) throw new IllegalArgumentException("Unable to find EmailPipe: "+pkey);
		ep.remove();
	}

	/**
	 * Removes an <code>EmailPipeAddress</code> from the system.
	 *
	 * @param  address  the part of the email address before the <code>@</code>
	 * @param  domain  the part of the email address after the <code>@</code>
	 * @param  pipe  the pkey of the email pipe
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>EmailDomain</code>,
	 *					<code>EmailAddress</code>, <code>EmailPipe</code>, or
	 *                                  <code>EmailPipeAddress</code>
	 *
	 * @see  EmailPipeAddress#remove
	 * @see  #addEmailPipeAddress
	 */
	public void removeEmailPipeAddress(
		String address,
		DomainName domain,
		int pipe
	) throws IllegalArgumentException, IOException, SQLException {
		Pipe ep=connector.getEmail().getEmailPipes().get(pipe);
		if(ep==null) throw new IllegalArgumentException("Unable to find EmailPipe: "+pipe);
		Server ao=ep.getAOServer();
		Domain sd=ao.getEmailDomain(domain);
		if(sd==null) throw new IllegalArgumentException("Unable to find EmailDomain: "+domain+" on "+ao.getHostname());
		Address addr=connector.getEmail().getEmailAddresses().getEmailAddress(address, sd);
		if(addr==null) throw new IllegalArgumentException("Unable to find EmailAddress: "+address+"@"+domain+" on "+ao.getHostname());
		PipeAddress epa=addr.getEmailPipeAddress(ep);
		if(epa==null) throw new IllegalArgumentException("Unable to find EmailPipeAddress: "+address+"@"+domain+"->"+ep);
		epa.remove();
		if(addr.getCannotRemoveReasons().isEmpty() && !addr.isUsed()) addr.remove();
	}

	/**
	 * Removes the <code>FTPGuestUser</code> flag from a <code>LinuxAccount</code>, allowing access
	 * to the server root directory.
	 *
	 * @param  username  the username of the <code>FTPGuestUser</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>FTPGuestUser</code>
	 *
	 * @see  FTPGuestUser#remove
	 * @see  #addFTPGuestUser
	 */
	public void removeFTPGuestUser(
		UserId username
	) throws IllegalArgumentException, IOException, SQLException {
		GuestUser ftpUser=connector.getFtp().getFtpGuestUsers().get(username);
		if(ftpUser==null) throw new IllegalArgumentException("Unable to find FTPGuestUser: "+username);
		ftpUser.remove();
	}

	/**
	 * Completely removes a <code>HttpdSharedTomcat</code> from the servers.
	 *
	 * @param  name  the name of the site
	 * @param  aoServer  the server the site runs on
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>HttpdSharedTomcat</code>
	 *
	 * @see  HttpdSharedTomcat#remove
	 * @see  SharedTomcat
	 */
	public void removeHttpdSharedTomcat(
		String name,
		String aoServer
	) throws IllegalArgumentException, IOException, SQLException {
		getHttpdSharedTomcat(aoServer, name).remove();
	}

	/**
	 * Completely removes a <code>HttpdSite</code> from the servers.
	 *
	 * @param  name  the name of the site
	 * @param  aoServer  the server the site runs on
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>HttpdSite</code>
	 *
	 * @see  HttpdSite#remove
	 * @see  Site
	 */
	public void removeHttpdSite(
		String name,
		String aoServer
	) throws IllegalArgumentException, IOException, SQLException {
		getHttpdSite(aoServer, name).remove();
	}

	/**
	 * Removes a <code>HttpdSiteURL</code> from the servers.
	 *
	 * @param  pkey  the pkey of the site URL
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>HttpdSiteURL</code>
	 *
	 * @see  HttpdSiteURL#remove
	 */
	public void removeHttpdSiteURL(
		int pkey
	) throws IllegalArgumentException, IOException, SQLException {
		VirtualHostName hsu=connector.getWeb().getHttpdSiteURLs().get(pkey);
		if(hsu==null) throw new IllegalArgumentException("Unable to find HttpdSiteURL: "+pkey);
		hsu.remove();
	}

	/**
	 * Removes a <code>HttpdTomcatContext</code> from the servers.
	 *
	 * @param  pkey  the pkey of the context
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>HttpdTomcatContext</code>
	 *
	 * @see  HttpdTomcatContext#remove
	 */
	public void removeHttpdTomcatContext(
		int pkey
	) throws IllegalArgumentException, IOException, SQLException {
		Context htc=connector.getWeb_tomcat().getHttpdTomcatContexts().get(pkey);
		if(htc==null) throw new IllegalArgumentException("Unable to find HttpdTomcatContext: "+pkey);
		htc.remove();
	}

	/**
	 * Removes a <code>HttpdTomcatDataSource</code> from a <code>HttpdTomcatContext</code>.
	 *
	 * @param  pkey  the pkey of the data source
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>HttpdTomcatDataSource</code>
	 *
	 * @see  HttpdTomcatContext#remove
	 */
	public void removeHttpdTomcatDataSource(
		int pkey
	) throws IllegalArgumentException, IOException, SQLException {
		ContextDataSource htds=connector.getWeb_tomcat().getHttpdTomcatDataSources().get(pkey);
		if(htds==null) throw new IllegalArgumentException("Unable to find HttpdTomcatDataSource: "+pkey);
		htds.remove();
	}

	/**
	 * Removes a <code>HttpdTomcatParameter</code> from a <code>HttpdTomcatContext</code>.
	 *
	 * @param  pkey  the pkey of the parameter
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>HttpdTomcatParameter</code>
	 *
	 * @see  HttpdTomcatContext#remove
	 */
	public void removeHttpdTomcatParameter(
		int pkey
	) throws IllegalArgumentException, IOException, SQLException {
		ContextParameter htp=connector.getWeb_tomcat().getHttpdTomcatParameters().get(pkey);
		if(htp==null) throw new IllegalArgumentException("Unable to find HttpdTomcatParameter: "+pkey);
		htp.remove();
	}

	/**
	 * Removes a <code>LinuxAccAddress</code> from the system.
	 *
	 * @param  address  the part of the email address before the <code>@</code>
	 * @param  domain  the part of the email address after the <code>@</code>
	 * @param  username  the account the emails are sent to
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>EmailDomain</code>,
	 *					<code>EmailAddress</code>, <code>Username</code>,
	 *                                  <code>LinuxAccount</code>, or <code>LinuxAccAddress</code>
	 *
	 * @see  LinuxAccAddress#remove
	 * @see  #addLinuxAccAddress
	 */
	public void removeLinuxAccAddress(
		String address,
		DomainName domain,
		String aoServer,
		UserId username
	) throws IllegalArgumentException, IOException, SQLException {
		Address addr=getEmailAddress(aoServer, domain, address);
		UserServer lsa=getLinuxServerAccount(aoServer, username);
		InboxAddress laa=addr.getLinuxAccAddress(lsa);
		if(laa==null) throw new IllegalArgumentException("Unable to find LinuxAccAddress: "+address+'@'+domain+"->"+username+" on "+aoServer);
		laa.remove();
		if(addr.getCannotRemoveReasons().isEmpty() && !addr.isUsed()) addr.remove();
	}

	/**
	 * Removes a <code>LinuxAccount</code> and all related data from the system.
	 *
	 * @param  username  the username of the <code>LinuxAccount</code> to remove
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>LinuxAccount</code>
	 *
	 * @see  LinuxAccount#remove
	 * @see  #addLinuxAccount
	 */
	public void removeLinuxAccount(
		UserId username
	) throws IllegalArgumentException, IOException, SQLException {
		getLinuxAccount(username).remove();
	}

	/**
	 * Removes a <code>LinuxGroup</code> and all related data from the system.
	 *
	 * @param  name  the name of the <code>LinuxGroup</code> to remove
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>LinuxGroup</code>
	 *
	 * @see  LinuxGroup#remove
	 * @see  #addLinuxGroup
	 */
	public void removeLinuxGroup(
		GroupId name
	) throws IllegalArgumentException, IOException, SQLException {
		getLinuxGroup(name).remove();
	}

	/**
	 * Removes a <code>LinuxAccount</code>'s access to a <code>LinuxGroup</code>.
	 *
	 * @param  group  the name of the <code>LinuxGroup</code> to remove access to
	 * @param  username  the username of the <code>LinuxAccount</code> to remove access from
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>LinuxGroup</code>,
	 *					<code>LinuxAccount</code>, or <code>LinuxGroupAccount</code>
	 *
	 * @see  LinuxGroupAccount#remove
	 * @see  #addLinuxGroupAccount
	 * @see  #addLinuxGroup
	 * @see  #addLinuxAccount
	 */
	public void removeLinuxGroupAccount(
		GroupId group,
		UserId username
	) throws IllegalArgumentException, IOException, SQLException {
		Group lg=getLinuxGroup(group);
		User la=getLinuxAccount(username);
		List<GroupUser> lgas = connector.getLinux().getLinuxGroupAccounts().getLinuxGroupAccounts(group, username);
		if(lgas.isEmpty()) throw new IllegalArgumentException(username+" is not part of the "+group+" group");
		for(GroupUser lga : lgas) lga.remove();
	}

	/**
	 * Removes a <code>LinuxServerAccount</code> from a <code>Server</code>.
	 *
	 * @param  username  the username of the <code>LinuxServerAccount</code> to remove
	 * @param  aoServer  the hostname of the <code>Server</code> to remove the account from
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>LinuxAccount</code>,
	 *					<code>Server</code>, or <code>LinuxServerAccount</code>
	 *
	 * @see  LinuxServerAccount#remove
	 * @see  #addLinuxServerAccount
	 */
	public void removeLinuxServerAccount(
		UserId username,
		String aoServer
	) throws IllegalArgumentException, IOException, SQLException {
		getLinuxServerAccount(aoServer, username).remove();
	}

	/**
	 * Removes a <code>LinuxServerGroup</code> from a <code>Server</code>.
	 *
	 * @param  group  the name of the <code>LinuxServerGroup</code> to remove
	 * @param  aoServer  the hostname of the <code>AOServer</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>LinuxGroup</code>,
	 *					<code>Server</code>, or <code>LinuxServerGroup</code>
	 *
	 * @see  LinuxServerGroup#remove
	 * @see  #addLinuxServerGroup
	 */
	public void removeLinuxServerGroup(
		GroupId group,
		String aoServer
	) throws IllegalArgumentException, IOException, SQLException {
		getLinuxServerGroup(aoServer, group).remove();
	}

	/**
	 * Removes a <code>MySQLDatabase</code> from the system.  All related
	 * <code>MySQLDBUser</code>s are also removed, and all data is removed
	 * from the MySQL server.  The data is not dumped or backed-up during
	 * the removal, if a backup is desired, use <code>dumpMySQLDatabase</code>.
	 *
	 * @param  name  the name of the database
	 * @param  aoServer  the server the database is hosted on
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or
	 *					<code>MySQLDatabase</code>
	 *
	 * @see  MySQLDatabase#remove
	 * @see  #addMySQLDatabase
	 * @see  #dumpMySQLDatabase
	 */
	public void removeMySQLDatabase(
		MySQLDatabaseName name,
		MySQLServerName mysqlServer,
		String aoServer
	) throws IllegalArgumentException, IOException, SQLException {
		getMySQLDatabase(aoServer, mysqlServer, name).remove();
	}

	/**
	 * Removes a <code>MySQLDBUser</code> from the system.  The <code>MySQLUser</code> is
	 * no longer allowed to access the <code>MySQLDatabase</code>.
	 *
	 * @param  name  the name of the <code>MySQLDatabase</code>
	 * @param  aoServer  the hostname of the <code>AOServer</code>
	 * @param  username  the username of the <code>MySQLUser</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code>,
	 *					<code>MySQLDatabase</code>, <code>MySQLServerUser</code>, or
	 *					<code>MySQLDBUser</code>
	 *
	 * @see  MySQLDBUser#remove
	 * @see  #addMySQLDBUser
	 */
	public void removeMySQLDBUser(
		MySQLDatabaseName name,
		MySQLServerName mysqlServer,
		String aoServer,
		MySQLUserId username
	) throws IllegalArgumentException, IOException, SQLException {
		Database md=getMySQLDatabase(aoServer, mysqlServer, name);
		com.aoindustries.aoserv.client.mysql.UserServer msu=getMySQLServerUser(aoServer, mysqlServer, username);
		DatabaseUser mdu=md.getMySQLDBUser(msu);
		if(mdu==null) throw new IllegalArgumentException("Unable to find MySQLDBUser on MySQLServer "+mysqlServer+" on AOServer "+aoServer+" for MySQLDatabase named "+name+" and MySQLServerUser named "+username);
		mdu.remove();
	}

	/**
	 * Removes a <code>MySQLServerUser</code> from a the system..  The <code>MySQLUser</code> is
	 * no longer allowed to access the <code>Server</code>.
	 *
	 * @param  username  the username of the <code>MySQLServerUser</code>
	 * @param  aoServer  the hostname of the <code>Server</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or
	 *					<code>MySQLServerUser</code>
	 *
	 * @see  MySQLServerUser#remove
	 * @see  #addMySQLServerUser
	 */
	public void removeMySQLServerUser(
		MySQLUserId username,
		MySQLServerName mysqlServer,
		String aoServer
	) throws IllegalArgumentException, IOException, SQLException {
		getMySQLServerUser(aoServer, mysqlServer, username).remove();
	}

	/**
	 * Removes a <code>MySQLUser</code> from a the system.  All of the associated
	 * <code>MySQLServerUser</code>s and <code>MySQLDBUser</code>s are also removed.
	 *
	 * @param  username  the username of the <code>MySQLUser</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>MySQLUser</code>
	 *
	 * @see  MySQLUser#remove
	 * @see  #addMySQLUser
	 * @see  #removeMySQLServerUser
	 */
	public void removeMySQLUser(
		MySQLUserId username
	) throws IllegalArgumentException, IOException, SQLException {
		getMySQLUser(username).remove();
	}

	/**
	 * Removes a <code>NetBind</code> from a the system.
	 *
	 * @param  pkey  the primary key of the <code>NetBind</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>NetBind</code>
	 *
	 * @see  NetBind#remove
	 */
	public void removeNetBind(
		int pkey
	) throws IllegalArgumentException, IOException, SQLException {
		getNetBind(pkey).remove();
	}

	/**
	 * Removes a <code>PostgresDatabase</code> from the system.  All data is removed
	 * from the PostgreSQL server.  The data is not dumped or backed-up during
	 * the removal, if a backup is desired, use <code>dumpPostgresDatabase</code>.
	 *
	 * @param  name  the name of the database
	 * @param  postgresServer  the name of the PostgreSQL server
	 * @param  aoServer  the server the database is hosted on
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or
	 *					<code>PostgresDatabase</code>
	 *
	 * @see  PostgresDatabase#remove
	 * @see  #addPostgresDatabase
	 * @see  #dumpPostgresDatabase
	 */
	public void removePostgresDatabase(
		PostgresDatabaseName name,
		PostgresServerName postgresServer,
		String aoServer
	) throws IllegalArgumentException, IOException, SQLException {
		getPostgresDatabase(aoServer, postgresServer, name).remove();
	}

	/**
	 * Removes a <code>PostgresServerUser</code> from a the system..  The <code>PostgresUser</code> is
	 * no longer allowed to access the <code>Server</code>.
	 *
	 * @param  username  the username of the <code>PostgresServerUser</code>
	 * @param  postgresServer  the name of the PostgreSQL server
	 * @param  aoServer  the hostname of the <code>Server</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or
	 *					<code>PostgresServerUser</code>
	 *
	 * @see  PostgresServerUser#remove
	 */
	public void removePostgresServerUser(
		PostgresUserId username,
		PostgresServerName postgresServer,
		String aoServer
	) throws IllegalArgumentException, IOException, SQLException {
		getPostgresServerUser(aoServer, postgresServer, username).remove();
	}

	/**
	 * Removes a <code>PostgresUser</code> from a the system..  All of the associated
	 * <code>PostgresServerUser</code>s are also removed.
	 *
	 * @param  username  the username of the <code>PostgresUser</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>PostgresUser</code>
	 *
	 * @see  PostgresUser#remove
	 * @see  #addPostgresUser
	 * @see  #removePostgresServerUser
	 */
	public void removePostgresUser(
		PostgresUserId username
	) throws IllegalArgumentException, IOException, SQLException {
		getPostgresUser(username).remove();
	}

	/**
	 * Removes an <code>EmailDomain</code> and all of its <code>EmailAddress</code>es.
	 *
	 * @param  domain  the name of the <code>EmailDomain</code>
	 * @param  aoServer  the server hosting this domain
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>EmailDomain</code>
	 *
	 * @see  EmailDomain#remove
	 * @see  #addEmailDomain
	 * @see  #removeEmailAddress
	 */
	public void removeEmailDomain(
		DomainName domain,
		String aoServer
	) throws IllegalArgumentException, IOException, SQLException {
		getEmailDomain(aoServer, domain).remove();
	}

	/**
	 * Removes an <code>EmailSMTPRelay</code> from the system, revoking access to the SMTP
	 * server from one IP address.
	 *
	 * @param  pkey  the <code>pkey</code> of the <code>EmailDomain</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>EmailSmtpRelay</code>
	 *
	 * @see  EmailSmtpRelay#remove
	 * @see  #addEmailSmtpRelay
	 * @see  #refreshEmailSmtpRelay
	 */
	public void removeEmailSmtpRelay(
		int pkey
	) throws IllegalArgumentException, IOException, SQLException {
		SmtpRelay sr=connector.getEmail().getEmailSmtpRelays().get(pkey);
		if(sr==null) throw new IllegalArgumentException("Unable to find EmailSmtpRelay: "+pkey);
		sr.remove();
	}

	/**
	 * Removes a <code>FileBackupSetting</code> from the system.
	 *
	 * @param  replication  the pkey of the <code>FailoverFileReplication</code>
	 * @param  path  the path of the setting
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>FailoverFileReplication</code> or <code>FileBackupSetting</code>
	 *
	 * @see  FileBackupSetting#remove
	 * @see  #addFileBackupSetting
	 */
	public void removeFileBackupSetting(
		int replication,
		String path
	) throws IllegalArgumentException, IOException, SQLException {
		FileReplication ffr = getConnector().getBackup().getFailoverFileReplications().get(replication);
		if(ffr==null) throw new IllegalArgumentException("Unable to find FailoverFileReplication: "+replication);
		FileReplicationSetting fbs=ffr.getFileBackupSetting(path);
		if(fbs==null) throw new IllegalArgumentException("Unable to find FileBackupSetting: "+path+" on "+replication);
		fbs.remove();
	}

	/**
	 * Removes a <code>MajordomoServer</code> and all of its <code>MajordomoList</code>s.
	 *
	 * @param  domain  the name of the <code>MajordomoServer</code>
	 * @param  aoServer  the server hosting the list
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code>,
	 *                                  <code>EmailDomain</code> or <code>MajordomoServer</code>
	 *
	 * @see  MajordomoServer#remove
	 * @see  #addMajordomoServer
	 */
	public void removeMajordomoServer(
		DomainName domain,
		String aoServer
	) throws IllegalArgumentException, IOException, SQLException {
		Domain sd=getEmailDomain(aoServer, domain);
		MajordomoServer ms=sd.getMajordomoServer();
		if(ms==null) throw new IllegalArgumentException("Unable to find MajordomoServer: "+domain+" on "+aoServer);
		ms.remove();
	}

	/**
	 * Removes a <code>Username</code> from the system.
	 *
	 * @param  username  the <code>username</code> of the <code>Username</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Username</code>
	 *
	 * @see  Username#remove
	 * @see  #addUsername
	 */
	public void removeUsername(
		UserId username
	) throws IllegalArgumentException, IOException, SQLException {
		getUsername(username).remove();
	}

	/**
	 * Restarts the Apache web server.
	 *
	 * @param  aoServer       the public hostname of the <code>AOServer</code>
	 *
	 * @exception  IOException  if not able to communicate with the server
	 * @exception  SQLException  if not able to access the database
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
	 *
	 * @see  AOServer#restartApache
	 */
	public void restartApache(String aoServer) throws IllegalArgumentException, IOException, SQLException {
		getAOServer(aoServer).restartApache();
	}

	/**
	 * Restarts the cron doggie.
	 *
	 * @param  aoServer       the public hostname of the <code>AOServer</code>
	 *
	 * @exception  IOException  if not able to communicate with the server
	 * @exception  SQLException  if not able to access the database
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
	 *
	 * @see  AOServer#restartCron
	 */
	public void restartCron(String aoServer) throws IllegalArgumentException, IOException, SQLException {
		getAOServer(aoServer).restartCron();
	}

	/**
	 * Restarts the MySQL database server.
	 *
	 * @param  aoServer       the public hostname of the <code>AOServer</code>
	 *
	 * @exception  IOException  if not able to communicate with the server
	 * @exception  SQLException  if not able to access the database
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
	 *
	 * @see  MySQLServer#restartMySQL
	 */
	public void restartMySQL(MySQLServerName mysqlServer, String aoServer) throws IllegalArgumentException, IOException, SQLException {
		getMySQLServer(aoServer, mysqlServer).restartMySQL();
	}

	/**
	 * Restarts the PostgreSQL database server.
	 *
	 * @param  postgresServer  the name of the PostgreSQL server
	 * @param  aoServer  the public hostname of the <code>AOServer</code>
	 *
	 * @exception  IOException  if not able to communicate with the server
	 * @exception  SQLException  if not able to access the database
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
	 *
	 * @see  PostgresServer#restartPostgreSQL
	 */
	public void restartPostgreSQL(PostgresServerName postgresServer, String aoServer) throws IllegalArgumentException, IOException, SQLException {
		getPostgresServer(aoServer, postgresServer).restartPostgreSQL();
	}

	/**
	 * Restarts the X Font Server.
	 *
	 * @param  aoServer       the public hostname of the <code>AOServer</code>
	 *
	 * @exception  IOException  if not able to communicate with the server
	 * @exception  SQLException  if not able to access the database
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
	 *
	 * @see  AOServer#restartXfs
	 */
	public void restartXfs(String aoServer) throws IllegalArgumentException, IOException, SQLException {
		getAOServer(aoServer).restartXfs();
	}

	/**
	 * Restarts the X Virtual Frame Buffer.
	 *
	 * @param  aoServer       the public hostname of the <code>AOServer</code>
	 *
	 * @exception  IOException  if not able to communicate with the server
	 * @exception  SQLException  if not able to access the database
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
	 *
	 * @see  AOServer#restartXvfb
	 */
	public void restartXvfb(String aoServer) throws IllegalArgumentException, IOException, SQLException {
		getAOServer(aoServer).restartXvfb();
	}

	/**
	 * Sets the autoresponder behavior for a Linux server account.
	 *
	 * @param  username  the username of the account
	 * @param  aoServer  the server the account is on
	 * @param  address  the address part of the email address
	 * @param  domain  the domain of the email address
	 * @param  subject  the subject of the email
	 * @param  content  the content of the email
	 * @param  enabled  if the autoresponder is enabled or not
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>EmailAddress</code> or
	 *                                  the <code>LinuxServerAccount</code>
	 *
	 * @see  LinuxServerAccount#setAutoresponder
	 */
	public void setAutoresponder(
		UserId username,
		String aoServer,
		String address,
		DomainName domain,
		String subject,
		String content,
		boolean enabled
	) throws IllegalArgumentException, IOException, SQLException {
		UserServer lsa=getLinuxServerAccount(aoServer, username);
		if(address==null) address="";
		Address ea;
		if(domain == null) {
			if(address.length() > 0) throw new IllegalArgumentException("Cannot have an address without a domain: " + address);
			ea = null;
		} else {
			Domain sd = getEmailDomain(aoServer, domain);
			ea = sd.getEmailAddress(address);
			if(ea == null) throw new IllegalArgumentException("Unable to find EmailAddress: " + address + '@' + domain + " on " + aoServer);
		}
		if(subject != null && subject.length() == 0) subject = null;
		if(content != null && content.length() == 0) content = null;
		InboxAddress laa = ea == null ? null : ea.getLinuxAccAddress(lsa);
		if(laa == null) throw new IllegalArgumentException("Unable to find LinuxAccAddress: " + address + " on " + aoServer);
		lsa.setAutoresponder(laa, subject, content, enabled);
	}

	/**
	 * Sets the accounting code for the business.  The accounting code is the value that uniquely
	 * identifies an account within the system.
	 *
	 * @param  oldAccounting  the old accounting code
	 * @param  newAccounting  the new accounting code
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Business</code> or
	 *                                  the requested accounting code is not valid
	 *
	 * @see  Business#setAccounting
	 */
	public void setBusinessAccounting(
		AccountingCode oldAccounting,
		AccountingCode newAccounting
	) throws IllegalArgumentException, IOException, SQLException {
		getBusiness(oldAccounting).setAccounting(newAccounting);
	}

	/**
	 * Sets the password for a <code>BusinessAdministrator</code>.  This password must pass the security
	 * checks provided by <code>checkBusinessAdministratorPassword</code>.
	 *
	 * @param  username  the username of the <code>BusinessAdministrator</code>
	 * @param  password  the new password
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>BusinessAdministrator</code>
	 *
	 * @see  BusinessAdministrator#setPassword
	 * @see  #addBusinessAdministrator
	 */
	public void setBusinessAdministratorPassword(
		UserId username,
		String password
	) throws IllegalArgumentException, IOException, SQLException {
		Administrator pe=connector.getAccount().getBusinessAdministrators().get(username);
		if(pe==null) throw new IllegalArgumentException("Unable to find BusinessAdministrator: "+username);
		pe.setPassword(password);
	}

	/**
	 * Sets the profile of a <code>BusinessAdministrator</code>, which is all of their contact
	 * information and other details.
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>BusinessAdministrator</code>
	 *
	 * @see  BusinessAdministrator#setProfile
	 * @see  #addBusinessAdministrator
	 */
	public void setBusinessAdministratorProfile(
		UserId username,
		String name,
		String title,
		Date birthday,
		boolean isPrivate,
		String workPhone,
		String homePhone,
		String cellPhone,
		String fax,
		String email,
		String address1,
		String address2,
		String city,
		String state,
		String country,
		String zip
	) throws IllegalArgumentException, IOException, SQLException {
		Administrator business_administrator=connector.getAccount().getBusinessAdministrators().get(username);
		if(business_administrator==null) throw new IllegalArgumentException("Unable to find BusinessAdministrator: "+username);
		business_administrator.setProfile(
			name,
			title,
			birthday,
			isPrivate,
			workPhone,
			homePhone,
			cellPhone,
			fax,
			email,
			address1,
			address2,
			city,
			state,
			country,
			zip
		);
	}

	/**
	 * Sets a user's cron table on one server.
	 *
	 * @param  username  the username of the <code>LinuxAccount</code>
	 * @param  aoServer  the server to get the data from
	 * @param  cronTable  the new cron table
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if unable to find the source <code>LinuxServerAccount</code>
	 *
	 * @see  LinuxServerAccount#setCronTable
	 * @see  #getCronTable
	 * @see  #addLinuxServerAccount
	 * @see  #removeLinuxServerAccount
	 */
	public void setCronTable(
		UserId username,
		String aoServer,
		String cronTable
	) throws IllegalArgumentException, IOException, SQLException {
		getLinuxServerAccount(aoServer, username).setCronTable(cronTable);
	}

	/**
	 * Sets the permissions for a CVS repository directory.
	 *
	 * @param  aoServer  the server the repository exists on
	 * @param  path  the path of the server
	 * @param  mode  the permission bits
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if unable to find the source <code>CvsRepository</code>
	 *
	 * @see  CvsRepository#setMode
	 * @see  #addCvsRepository
	 * @see  #removeCvsRepository
	 */
	public void setCvsRepositoryMode(
		String aoServer,
		UnixPath path,
		long mode
	) throws IllegalArgumentException, IOException, SQLException {
		Server ao=getAOServer(aoServer);
		CvsRepository cr=ao.getCvsRepository(path);
		if(cr==null) throw new IllegalArgumentException("Unable to find CvsRepository: "+path+" on "+aoServer);
		cr.setMode(mode);
	}

	/**
	 * Sets the default <code>Server</code> for a <code>Business</code>.
	 *
	 * @param  accounting  the accounting code of the business
	 * @param  server  the hostname of the server
	 *
	 * @exception  IOException  if unable to communicate with the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the business or server
	 *
	 * @see  AccountHost
	 * @see  BusinessServer#setAsDefault
	 * @see  #addBusinessServer
	 * @see  #removeBusinessServer
	 */
	public void setDefaultBusinessServer(
		AccountingCode accounting,
		String server
	) throws IllegalArgumentException, SQLException, IOException {
		Account bu=getBusiness(accounting);
		Host se=getServer(server);
		AccountHost bs=bu.getBusinessServer(se);
		if(bs==null) throw new IllegalArgumentException("Unable to find BusinessServer: accounting="+accounting+" and server="+server);
		bs.setAsDefault();
	}

	/**
	 * Sets the list of addresses that an <code>EmailList</code> will forward messages
	 * to.
	 *
	 * @param  path  the path of the <code>EmailList</code>
	 * @param  aoServer  the server hosting the list
	 * @param  addresses  the list of addresses, one address per line
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>EmailList</code>
	 *
	 * @see  EmailList#setAddressList
	 * @see  #getEmailListAddressList
	 * @see  #addEmailList
	 */
	public void setEmailListAddressList(
		UnixPath path,
		String aoServer,
		String addresses
	) throws IllegalArgumentException, IOException, SQLException {
		getEmailList(aoServer, path).setAddressList(addresses);
	}

	/**
	 * Sets the settings contained by one <code>FileBackupSetting</code>
	 *
	 * @param  replication  the hostname of the <code>FailoverFileReplication</code>
	 * @param  path  the path of the setting
	 * @param  backupEnabled  the enabled flag for the prefix
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>FailoverFileReplication</code> or <code>FileBackupSetting</code>
	 *
	 * @see  FileBackupSetting#setSettings
	 * @see  #addFileBackupSetting
	 */
	public void setFileBackupSetting(
		int replication,
		String path,
		boolean backupEnabled,
		boolean required
	) throws IllegalArgumentException, IOException, SQLException {
		FileReplication ffr = getConnector().getBackup().getFailoverFileReplications().get(replication);
		if(ffr==null) throw new IllegalArgumentException("Unable to find FailoverFileReplication: "+replication);
		FileReplicationSetting fbs=ffr.getFileBackupSetting(path);
		if(fbs==null) throw new IllegalArgumentException("Unable to find FileBackupSetting: "+path+" on "+replication);
		fbs.setSettings(
			path,
			backupEnabled,
			required
		);
	}

	/**
	 * Sets the <code>is_manual</code> flag for a <code>HttpdSharedTomcat</code>
	 *
	 * @param  name  the name of the JVM
	 * @param  aoServer  the hostname of the <code>AOServer</code>
	 * @param  isManual  the new flag
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>AOServer</code> or <code>HttpdSharedTomcat</code>
	 *
	 * @see  HttpdSharedTomcat#setIsManual
	 */
	public void setHttpdSharedTomcatIsManual(
		String name,
		String aoServer,
		boolean isManual
	) throws IllegalArgumentException, IOException, SQLException {
		getHttpdSharedTomcat(aoServer, name).setIsManual(isManual);
	}

	/**
	 * Sets the <code>maxPostSize</code> for a <code>HttpdSharedTomcat</code>
	 *
	 * @param  name  the name of the JVM
	 * @param  aoServer  the hostname of the <code>AOServer</code>
	 * @param  maxPostSize  the new maximum POST size, in bytes, {@code -1} for none.
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>AOServer</code> or <code>HttpdSharedTomcat</code>
	 *
	 * @see  HttpdSharedTomcat#setMaxPostSize(int)
	 */
	public void setHttpdSharedTomcatMaxPostSize(
		String name,
		String aoServer,
		int maxPostSize
	) throws IllegalArgumentException, IOException, SQLException {
		getHttpdSharedTomcat(aoServer, name).setMaxPostSize(maxPostSize);
	}

	/**
	 * Sets the <code>unpackWARs</code> setting for a <code>HttpdSharedTomcat</code>
	 *
	 * @param  name  the name of the JVM
	 * @param  aoServer  the hostname of the <code>AOServer</code>
	 * @param  unpackWARs  the new setting
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>AOServer</code> or <code>HttpdSharedTomcat</code>
	 *
	 * @see  HttpdSharedTomcat#setUnpackWARs(boolean)
	 */
	public void setHttpdSharedTomcatUnpackWARs(
		String name,
		String aoServer,
		boolean unpackWARs
	) throws IllegalArgumentException, IOException, SQLException {
		getHttpdSharedTomcat(aoServer, name).setUnpackWARs(unpackWARs);
	}

	/**
	 * Sets the <code>autoDeploy</code> setting for a <code>HttpdSharedTomcat</code>
	 *
	 * @param  name  the name of the JVM
	 * @param  aoServer  the hostname of the <code>AOServer</code>
	 * @param  autoDeploy  the new setting
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>AOServer</code> or <code>HttpdSharedTomcat</code>
	 *
	 * @see  HttpdSharedTomcat#setAutoDeploy(boolean)
	 */
	public void setHttpdSharedTomcatAutoDeploy(
		String name,
		String aoServer,
		boolean autoDeploy
	) throws IllegalArgumentException, IOException, SQLException {
		getHttpdSharedTomcat(aoServer, name).setAutoDeploy(autoDeploy);
	}

	/**
	 * Sets the Tomcat version for a {@link SharedTomcat}
	 *
	 * @param  name  the name of the JVM
	 * @param  aoServer  the hostname of the {@link Server}
	 * @param  version  the new version
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity violation occurs
	 * @throws IllegalArgumentException if unable to find the {@link Server}, {@link SharedTomcat}, or {@link Version}.
	 *
	 * @see  HttpdSharedTomcat#setHttpdTomcatVersion(com.aoindustries.aoserv.client.HttpdTomcatVersion)
	 */
	public void setHttpdSharedTomcatVersion(
		String name,
		String aoServer,
		String version
	) throws IllegalArgumentException, IOException, SQLException {
		SharedTomcat hst = getHttpdSharedTomcat(aoServer, name);
		hst.setHttpdTomcatVersion(
			findTomcatVersion(hst.getAOServer(), version)
		);
	}

	/**
	 * Sets the <code>is_manual</code> flag for a <code>HttpdSiteBind</code>
	 *
	 * @param  pkey  the primary key of the <code>HttpdSiteBind</code>
	 * @param  isManual  the new flag
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>HttpdSiteBind</code>
	 *
	 * @see  HttpdSiteBind#setIsManual
	 */
	public void setHttpdSiteBindIsManual(
		int pkey,
		boolean isManual
	) throws IllegalArgumentException, IOException, SQLException {
		VirtualHost hsb=connector.getWeb().getHttpdSiteBinds().get(pkey);
		if(hsb==null) throw new IllegalArgumentException("Unable to find HttpdSiteBind: "+pkey);
		hsb.setIsManual(isManual);
	}

	/**
	 * Sets the <code>redirect_to_primary_hostname</code> flag for a <code>HttpdSiteBind</code>
	 *
	 * @param  pkey  the primary key of the <code>HttpdSiteBind</code>
	 * @param  redirectToPrimaryHostname  the new flag
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>HttpdSiteBind</code>
	 *
	 * @see  HttpdSiteBind#setRedirectToPrimaryHostname
	 */
	public void setHttpdSiteBindRedirectToPrimaryHostname(
		int pkey,
		boolean redirectToPrimaryHostname
	) throws IllegalArgumentException, IOException, SQLException {
		VirtualHost hsb=connector.getWeb().getHttpdSiteBinds().get(pkey);
		if(hsb==null) throw new IllegalArgumentException("Unable to find HttpdSiteBind: "+pkey);
		hsb.setRedirectToPrimaryHostname(redirectToPrimaryHostname);
	}

	/**
	 * Sets the <code>is_manual</code> flag for a <code>HttpdSite</code>
	 *
	 * @param  siteName  the name of the site
	 * @param  aoServer  the hostname of the <code>AOServer</code>
	 * @param  isManual  the new flag
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>AOServer</code> or <code>HttpdSite</code>
	 *
	 * @see  HttpdSite#setIsManual
	 */
	public void setHttpdSiteIsManual(
		String siteName,
		String aoServer,
		boolean isManual
	) throws IllegalArgumentException, IOException, SQLException {
		getHttpdSite(aoServer, siteName).setIsManual(isManual);
	}

	/**
	 * Sets the administrative email address for a <code>HttpdSite</code>.
	 *
	 * @param  siteName  the name of the <code>HttpdSite</code>
	 * @param  aoServer  the hostname of the server that hosts the site
	 * @param  emailAddress  the new adminstrative email address
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>AOServer</code> or <code>HttpdSite</code>,
	 *                                  or the email address is not in a valid format
	 *
	 * @see  HttpdSite#setServerAdmin
	 */
	public void setHttpdSiteServerAdmin(
		String siteName,
		String aoServer,
		Email emailAddress
	) throws IllegalArgumentException, IOException, SQLException {
		getHttpdSite(aoServer, siteName).setServerAdmin(emailAddress);
	}

	/**
	 * Sets the PHP version for a <code>HttpdSite</code>
	 *
	 * @param  siteName  the name of the site
	 * @param  aoServer  the hostname of the <code>AOServer</code>
	 * @param  phpVersion  the new version
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>AOServer</code>, <code>HttpdSite</code>, or PHP version.
	 *
	 * @see  HttpdSite#setPhpVersion(com.aoindustries.aoserv.client.TechnologyVersion)
	 */
	public void setHttpdSitePhpVersion(
		String siteName,
		String aoServer,
		String phpVersion
	) throws IllegalArgumentException, IOException, SQLException {
		Site hs = getHttpdSite(aoServer, siteName);
		hs.setPhpVersion(
			findPhpVersion(hs.getAoServer(), phpVersion)
		);
	}

	/**
	 * Sets the <code>enable_cgi</code> flag for a <code>HttpdSite</code>
	 *
	 * @param  siteName  the name of the site
	 * @param  aoServer  the hostname of the <code>AOServer</code>
	 * @param  enableCgi  the new flag
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>AOServer</code> or <code>HttpdSite</code>
	 *
	 * @see  HttpdSite#setEnableCgi(boolean)
	 */
	public void setHttpdSiteEnableCgi(
		String siteName,
		String aoServer,
		boolean enableCgi
	) throws IllegalArgumentException, IOException, SQLException {
		getHttpdSite(aoServer, siteName).setEnableCgi(enableCgi);
	}

	/**
	 * Sets the <code>enable_ssi</code> flag for a <code>HttpdSite</code>
	 *
	 * @param  siteName  the name of the site
	 * @param  aoServer  the hostname of the <code>AOServer</code>
	 * @param  enableSsi  the new flag
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>AOServer</code> or <code>HttpdSite</code>
	 *
	 * @see  HttpdSite#setEnableSsi(boolean)
	 */
	public void setHttpdSiteEnableSsi(
		String siteName,
		String aoServer,
		boolean enableSsi
	) throws IllegalArgumentException, IOException, SQLException {
		getHttpdSite(aoServer, siteName).setEnableSsi(enableSsi);
	}

	/**
	 * Sets the <code>enable_htaccess</code> flag for a <code>HttpdSite</code>
	 *
	 * @param  siteName  the name of the site
	 * @param  aoServer  the hostname of the <code>AOServer</code>
	 * @param  enableHtaccess  the new flag
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>AOServer</code> or <code>HttpdSite</code>
	 *
	 * @see  HttpdSite#setEnableHtaccess(boolean)
	 */
	public void setHttpdSiteEnableHtaccess(
		String siteName,
		String aoServer,
		boolean enableHtaccess
	) throws IllegalArgumentException, IOException, SQLException {
		getHttpdSite(aoServer, siteName).setEnableHtaccess(enableHtaccess);
	}

	/**
	 * Sets the <code>enable_indexes</code> flag for a <code>HttpdSite</code>
	 *
	 * @param  siteName  the name of the site
	 * @param  aoServer  the hostname of the <code>AOServer</code>
	 * @param  enableIndexes  the new flag
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>AOServer</code> or <code>HttpdSite</code>
	 *
	 * @see  HttpdSite#setEnableIndexes(boolean)
	 */
	public void setHttpdSiteEnableIndexes(
		String siteName,
		String aoServer,
		boolean enableIndexes
	) throws IllegalArgumentException, IOException, SQLException {
		getHttpdSite(aoServer, siteName).setEnableIndexes(enableIndexes);
	}

	/**
	 * Sets the <code>enable_follow_symlinks</code> flag for a <code>HttpdSite</code>
	 *
	 * @param  siteName  the name of the site
	 * @param  aoServer  the hostname of the <code>AOServer</code>
	 * @param  enableFollowSymlinks  the new flag
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>AOServer</code> or <code>HttpdSite</code>
	 *
	 * @see  HttpdSite#setEnableFollowSymlinks(boolean)
	 */
	public void setHttpdSiteEnableFollowSymlinks(
		String siteName,
		String aoServer,
		boolean enableFollowSymlinks
	) throws IllegalArgumentException, IOException, SQLException {
		getHttpdSite(aoServer, siteName).setEnableFollowSymlinks(enableFollowSymlinks);
	}

	/**
	 * Sets the <code>enable_anonymous_ftp</code> flag for a <code>HttpdSite</code>
	 *
	 * @param  siteName  the name of the site
	 * @param  aoServer  the hostname of the <code>AOServer</code>
	 * @param  enableAnonymousFtp  the new flag
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>AOServer</code> or <code>HttpdSite</code>
	 *
	 * @see  HttpdSite#setEnableAnonymousFtp(boolean)
	 */
	public void setHttpdSiteEnableAnonymousFtp(
		String siteName,
		String aoServer,
		boolean enableAnonymousFtp
	) throws IllegalArgumentException, IOException, SQLException {
		getHttpdSite(aoServer, siteName).setEnableAnonymousFtp(enableAnonymousFtp);
	}

	/**
	 * Sets the <code>block_trace_track</code> flag for a <code>HttpdSite</code>
	 *
	 * @param  siteName  the name of the site
	 * @param  aoServer  the hostname of the <code>AOServer</code>
	 * @param  blockTraceTrack  the new flag
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>AOServer</code> or <code>HttpdSite</code>
	 *
	 * @see  HttpdSite#setBlockTraceTrack(boolean)
	 */
	public void setHttpdSiteBlockTraceTrack(
		String siteName,
		String aoServer,
		boolean blockTraceTrack
	) throws IllegalArgumentException, IOException, SQLException {
		getHttpdSite(aoServer, siteName).setBlockTraceTrack(blockTraceTrack);
	}

	/**
	 * Sets the <code>block_scm</code> flag for a <code>HttpdSite</code>
	 *
	 * @param  siteName  the name of the site
	 * @param  aoServer  the hostname of the <code>AOServer</code>
	 * @param  blockScm  the new flag
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>AOServer</code> or <code>HttpdSite</code>
	 *
	 * @see  HttpdSite#setBlockScm(boolean)
	 */
	public void setHttpdSiteBlockScm(
		String siteName,
		String aoServer,
		boolean blockScm
	) throws IllegalArgumentException, IOException, SQLException {
		getHttpdSite(aoServer, siteName).setBlockScm(blockScm);
	}

	/**
	 * Sets the <code>block_core_dumps</code> flag for a <code>HttpdSite</code>
	 *
	 * @param  siteName  the name of the site
	 * @param  aoServer  the hostname of the <code>AOServer</code>
	 * @param  blockCoreDumps  the new flag
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>AOServer</code> or <code>HttpdSite</code>
	 *
	 * @see  HttpdSite#setBlockCoreDumps(boolean)
	 */
	public void setHttpdSiteBlockCoreDumps(
		String siteName,
		String aoServer,
		boolean blockCoreDumps
	) throws IllegalArgumentException, IOException, SQLException {
		getHttpdSite(aoServer, siteName).setBlockCoreDumps(blockCoreDumps);
	}

	/**
	 * Sets the <code>block_editor_backups</code> flag for a <code>HttpdSite</code>
	 *
	 * @param  siteName  the name of the site
	 * @param  aoServer  the hostname of the <code>AOServer</code>
	 * @param  blockEditorBackups  the new flag
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>AOServer</code> or <code>HttpdSite</code>
	 *
	 * @see  HttpdSite#setBlockEditorBackups(boolean)
	 */
	public void setHttpdSiteBlockEditorBackups(
		String siteName,
		String aoServer,
		boolean blockEditorBackups
	) throws IllegalArgumentException, IOException, SQLException {
		getHttpdSite(aoServer, siteName).setBlockEditorBackups(blockEditorBackups);
	}

	/**
	 * Sets the attributes for a <code>HttpdTomcatContext</code>.
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if unable to find the <code>AOServer</code>, <code>HttpdSite</code>,
	 *                                  or <code>HttpdTomcatSite</code>
	 */
	public void setHttpdTomcatContextAttributes(
		String siteName,
		String aoServer,
		String oldPath,
		String className,
		boolean cookies,
		boolean crossContext,
		UnixPath docBase,
		boolean override,
		String newPath,
		boolean privileged,
		boolean reloadable,
		boolean useNaming,
		String wrapperClass,
		int debug,
		UnixPath workDir,
		boolean serverXmlConfigured
	) throws IllegalArgumentException, IOException, SQLException {
		Site hs=getHttpdSite(aoServer, siteName);
		com.aoindustries.aoserv.client.web.tomcat.Site hts=hs.getHttpdTomcatSite();
		if(hts==null) throw new IllegalArgumentException("Unable to find HttpdTomcatSite: "+siteName+" on "+aoServer);
		Context htc=hts.getHttpdTomcatContext(oldPath);
		if(htc==null) throw new IllegalArgumentException("Unable to find HttpdTomcatContext: "+siteName+" on "+aoServer+" path='"+oldPath+'\'');
		htc.setAttributes(
			className,
			cookies,
			crossContext,
			docBase,
			override,
			newPath,
			privileged,
			reloadable,
			useNaming,
			wrapperClass,
			debug,
			workDir,
			serverXmlConfigured
		);
	}

	/**
	 * Sets the <code>block_webinf</code> flag for a <code>HttpdTomcatSite</code>
	 *
	 * @param  siteName  the name of the site
	 * @param  aoServer  the hostname of the <code>AOServer</code>
	 * @param  blockWebinf  the new flag
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>AOServer</code>, <code>HttpdSite</code>, or <code>HttpdTomcatSite</code>.
	 *
	 * @see  HttpdTomcatSite#setBlockWebinf(boolean)
	 */
	public void setHttpdTomcatSiteBlockWebinf(
		String siteName,
		String aoServer,
		boolean blockWebinf
	) throws IllegalArgumentException, IOException, SQLException {
		Site hs = getHttpdSite(aoServer, siteName);
		com.aoindustries.aoserv.client.web.tomcat.Site hts = hs.getHttpdTomcatSite();
		if(hts == null) throw new IllegalArgumentException("Unable to find HttpdTomcatSite: " + siteName + " on " + aoServer);
		hts.setBlockWebinf(blockWebinf);
	}

	/**
	 * Sets the <code>maxPostSize</code> for a <code>HttpdTomcatStdSite</code>
	 *
	 * @param  siteName  the name of the site
	 * @param  aoServer  the hostname of the <code>AOServer</code>
	 * @param  maxPostSize  the new maximum POST size, in bytes, {@code -1} for none.
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>AOServer</code>, <code>HttpdSite</code>, or <code>HttpdTomcatStdSite</code>
	 *
	 * @see  HttpdTomcatStdSite#setMaxPostSize(int)
	 */
	public void setHttpdTomcatStdSiteMaxPostSize(
		String siteName,
		String aoServer,
		int maxPostSize
	) throws IllegalArgumentException, IOException, SQLException {
		Site hs = getHttpdSite(aoServer, siteName);
		com.aoindustries.aoserv.client.web.tomcat.Site hts = hs.getHttpdTomcatSite();
		if(hts == null) throw new IllegalArgumentException("Unable to find HttpdTomcatSite: " + siteName + " on " + aoServer);
		PrivateTomcatSite htss = hts.getHttpdTomcatStdSite();
		if(htss == null) throw new IllegalArgumentException("Unable to find HttpdTomcatStdSite: " + siteName + " on " + aoServer);
		htss.setMaxPostSize(maxPostSize);
	}

	/**
	 * Sets the <code>unpackWARs</code> setting for a <code>HttpdTomcatStdSite</code>
	 *
	 * @param  siteName  the name of the site
	 * @param  aoServer  the hostname of the <code>AOServer</code>
	 * @param  unpackWARs  the new setting
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>AOServer</code>, <code>HttpdSite</code>, or <code>HttpdTomcatStdSite</code>
	 *
	 * @see  HttpdTomcatStdSite#setUnpackWARs(boolean)
	 */
	public void setHttpdTomcatStdSiteUnpackWARs(
		String siteName,
		String aoServer,
		boolean unpackWARs
	) throws IllegalArgumentException, IOException, SQLException {
		Site hs = getHttpdSite(aoServer, siteName);
		com.aoindustries.aoserv.client.web.tomcat.Site hts = hs.getHttpdTomcatSite();
		if(hts == null) throw new IllegalArgumentException("Unable to find HttpdTomcatSite: " + siteName + " on " + aoServer);
		PrivateTomcatSite htss = hts.getHttpdTomcatStdSite();
		if(htss == null) throw new IllegalArgumentException("Unable to find HttpdTomcatStdSite: " + siteName + " on " + aoServer);
		htss.setUnpackWARs(unpackWARs);
	}

	/**
	 * Sets the <code>autoDeploy</code> setting for a <code>HttpdTomcatStdSite</code>
	 *
	 * @param  siteName  the name of the site
	 * @param  aoServer  the hostname of the <code>AOServer</code>
	 * @param  autoDeploy  the new setting
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>AOServer</code>, <code>HttpdSite</code>, or <code>HttpdTomcatStdSite</code>
	 *
	 * @see  HttpdTomcatStdSite#setAutoDeploy(boolean)
	 */
	public void setHttpdTomcatStdSiteAutoDeploy(
		String siteName,
		String aoServer,
		boolean autoDeploy
	) throws IllegalArgumentException, IOException, SQLException {
		Site hs = getHttpdSite(aoServer, siteName);
		com.aoindustries.aoserv.client.web.tomcat.Site hts = hs.getHttpdTomcatSite();
		if(hts == null) throw new IllegalArgumentException("Unable to find HttpdTomcatSite: " + siteName + " on " + aoServer);
		PrivateTomcatSite htss = hts.getHttpdTomcatStdSite();
		if(htss == null) throw new IllegalArgumentException("Unable to find HttpdTomcatStdSite: " + siteName + " on " + aoServer);
		htss.setAutoDeploy(autoDeploy);
	}

	/**
	 * Sets the Tomcat version for a {@link PrivateTomcatSite}
	 *
	 * @param  siteName  the name of the site
	 * @param  aoServer  the hostname of the {@link Server}
	 * @param  version  the new version
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity violation occurs
	 * @throws IllegalArgumentException if unable to find the {@link Server}, {@link Site}, {@link PrivateTomcatSite}, or {@link Version}.
	 *
	 * @see  HttpdTomcatStdSite#setHttpdTomcatVersion(com.aoindustries.aoserv.client.HttpdTomcatVersion)
	 */
	public void setHttpdTomcatStdSiteVersion(
		String siteName,
		String aoServer,
		String version
	) throws IllegalArgumentException, IOException, SQLException {
		Site hs = getHttpdSite(aoServer, siteName);
		com.aoindustries.aoserv.client.web.tomcat.Site hts = hs.getHttpdTomcatSite();
		if(hts == null) throw new IllegalArgumentException("Unable to find HttpdTomcatSite: " + siteName + " on " + aoServer);
		PrivateTomcatSite htss = hts.getHttpdTomcatStdSite();
		if(htss == null) throw new IllegalArgumentException("Unable to find HttpdTomcatStdSite: " + siteName + " on " + aoServer);
		htss.setHttpdTomcatVersion(
			findTomcatVersion(hs.getAoServer(), version)
		);
	}

	/**
	 * Sets the IP address of a DHCP-enabled <code>IPAddress</code>.
	 *
	 * @param  ipAddress  the pkey of the <code>IPAddress</code>
	 * @param  dhcpAddress  the new IP address
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>IPAddress</code> or
	 *					DHCP address is not valid format
	 *
	 * @see  IPAddress#setDHCPAddress
	 */
	public void setIPAddressDHCPAddress(
		int ipAddress,
		InetAddress dhcpAddress
	) throws IllegalArgumentException, IOException, SQLException {
		IpAddress ia=connector.getNet().getIpAddresses().get(ipAddress);
		if(ia==null) throw new IllegalArgumentException("Unable to find IPAddress: "+ipAddress);
		ia.setDHCPAddress(dhcpAddress);
	}

	/**
	 * Sets the hostname of an <code>IPAddress</code>.
	 *
	 * @param  ipAddress  the <code>IPAddress</code> being modified
	 * @param  hostname  the new hostname
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>IPAddress</code> or
	 *					hostname is not valid format
	 *
	 * @see  IPAddress#setHostname
	 */
	public void setIPAddressHostname(
		InetAddress ipAddress,
		String server,
		String net_device,
		DomainName hostname
	) throws IllegalArgumentException, IOException, SQLException {
		getIPAddress(server, net_device, ipAddress).setHostname(hostname);
	}

	/**
	 * Sets the monitoring status of an {@link IpAddress}.
	 *
	 * @param  ipAddress  the {@link IpAddress} being modified
	 * @param  enabled  the new monitoring state
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @throws IllegalArgumentException if unable to find the {@link IpAddress} or
					{@link Package}
	 *
	 * @see  IPAddress#setPackage
	 * @see  #addPackage
	 */
	public void setIPAddressMonitoringEnabled(
		InetAddress ipAddress,
		String server,
		String net_device,
		boolean enabled
	) throws IllegalArgumentException, IOException, SQLException {
		getIPAddress(server, net_device, ipAddress).getMonitoring().setEnabled(enabled);
	}

	/**
	 * Sets the ownership of an {@link IpAddress}.  The {@link Package} may only be set
	 * if the {@link IpAddress} is not being used by any resources.
	 *
	 * @param  ipAddress  the {@link IpAddress} being modified
	 * @param  newPackage  the name of the {@link Package}
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @throws IllegalArgumentException if unable to find the {@link IpAddress} or
					{@link Package}
	 *
	 * @see  IPAddress#setPackage
	 * @see  #addPackage
	 */
	public void setIPAddressPackage(
		InetAddress ipAddress,
		String server,
		String net_device,
		AccountingCode newPackage
	) throws IllegalArgumentException, IOException, SQLException {
		getIPAddress(server, net_device, ipAddress).setPackage(getPackage(newPackage));
	}

	/**
	 * Sets the home phone number associated with a <code>LinuxAccount</code>.
	 *
	 * @param  username  the username of the <code>LinuxAccount</code>
	 * @param  phone  the new office phone
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>LinuxAccount</code>
	 *
	 * @see  LinuxAccount#setHomePhone
	 * @see  #addLinuxAccount
	 */
	public void setLinuxAccountHomePhone(
		UserId username,
		Gecos phone
	) throws IllegalArgumentException, IOException, SQLException {
		getLinuxAccount(username).setHomePhone(phone);
	}

	/**
	 * Sets the full name associated with a <code>LinuxAccount</code>.
	 *
	 * @param  username  the username of the <code>LinuxAccount</code>
	 * @param  name  the new full name for the account
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if the name is not in a valid format or unable to
	 *					find the <code>LinuxAccount</code>
	 *
	 * @see  LinuxAccount#setName
	 * @see  LinuxAccount#checkGECOS
	 * @see  #addLinuxAccount
	 */
	public void setLinuxAccountName(
		UserId username,
		Gecos name
	) throws IllegalArgumentException, IOException, SQLException {
		getLinuxAccount(username).setName(name);
	}

	/**
	 * Sets the office location associated with a <code>LinuxAccount</code>.
	 *
	 * @param  username  the username of the <code>LinuxAccount</code>
	 * @param  location  the new office location
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>LinuxAccount</code>
	 *
	 * @see  LinuxAccount#setOfficeLocation
	 * @see  #addLinuxAccount
	 */
	public void setLinuxAccountOfficeLocation(
		UserId username,
		Gecos location
	) throws IllegalArgumentException, IOException, SQLException {
		getLinuxAccount(username).setOfficeLocation(location);
	}

	/**
	 * Sets the office phone number associated with a <code>LinuxAccount</code>.
	 *
	 * @param  username  the username of the <code>LinuxAccount</code>
	 * @param  phone  the new office phone
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>LinuxAccount</code>
	 *
	 * @see  LinuxAccount#setOfficePhone
	 * @see  #addLinuxAccount
	 */
	public void setLinuxAccountOfficePhone(
		UserId username,
		Gecos phone
	) throws IllegalArgumentException, IOException, SQLException {
		getLinuxAccount(username).setOfficePhone(phone);
	}

	/**
	 * Sets the password for a <code>LinuxAccount</code> by setting the password
	 * for each one of its <code>LinuxServerAccount</code>s.
	 *
	 * @param  username  the username of the <code>LinuxAccount</code>
	 * @param  password  the new password
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>LinuxAccount</code>
	 *
	 * @see  LinuxAccount#setPassword
	 * @see  #addLinuxAccount
	 */
	public void setLinuxAccountPassword(
		UserId username,
		String password
	) throws IllegalArgumentException, IOException, SQLException {
		getLinuxAccount(username).setPassword(password);
	}

	/**
	 * Sets the shell used by a <code>LinuxAccount</code>.
	 *
	 * @param  username  the username of the <code>LinuxAccount</code>
	 * @param  path  the full path of the shell
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>LinuxAccount</code> or <code>Shell</code>
	 *
	 * @see  LinuxAccount#setShell
	 * @see  #addLinuxAccount
	 */
	public void setLinuxAccountShell(
		UserId username,
		UnixPath path
	) throws IllegalArgumentException, IOException, SQLException {
		User la=getLinuxAccount(username);
		Shell sh=connector.getLinux().getShells().get(path);
		if(sh==null) throw new IllegalArgumentException("Unable to find Shell: "+path);
		la.setShell(sh);
	}

	/**
	 * Sets the password for a <code>LinuxServerAccount</code>.
	 *
	 * @param  username  the username of the <code>LinuxServerAccount</code>
	 * @param  aoServer  the hostname of the <code>AOServer</code>
	 * @param  password  the new password
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code>, <code>AOServer</code> or
	 *					<code>LinuxServerAccount</code>
	 *
	 * @see  LinuxServerAccount#setPassword
	 * @see  #addLinuxServerAccount
	 */
	public void setLinuxServerAccountPassword(
		UserId username,
		String aoServer,
		String password
	) throws IllegalArgumentException, IOException, SQLException {
		getLinuxServerAccount(aoServer, username).setPassword(password);
	}

	/**
	 * Sets the number of days junk email is kept.
	 *
	 * @param  username  the username of the <code>LinuxServerAccount</code>
	 * @param  aoServer  the hostname of the <code>Server</code>
	 * @param  days  the new number of days, <code>-1</code> causes the junk to not be automatically removed
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>LinuxServerAccount</code>
	 *
	 * @see  LinuxServerAccount#setJunkEmailRetention
	 * @see  #addLinuxServerAccount
	 */
	public void setLinuxServerAccountJunkEmailRetention(
		UserId username,
		String aoServer,
		int days
	) throws IllegalArgumentException, IOException, SQLException {
		getLinuxServerAccount(aoServer, username).setJunkEmailRetention(days);
	}

	/**
	 * Sets the SpamAssassin integration mode for an email account.
	 *
	 * @param  username  the username of the <code>LinuxServerAccount</code>
	 * @param  aoServer  the hostname of the <code>Server</code>
	 * @param  mode      the new mode
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code>, <code>LinuxServerAccount</code>, or <code>EmailSpamAssassinIntegrationMode</code>
	 *
	 * @see  LinuxServerAccount#setEmailSpamAssassinIntegrationMode
	 * @see  #addLinuxServerAccount
	 * @see  SpamAssassinMode
	 */
	public void setLinuxServerAccountSpamAssassinIntegrationMode(
		UserId username,
		String aoServer,
		String mode
	) throws IllegalArgumentException, IOException, SQLException {
		getLinuxServerAccount(aoServer, username).setEmailSpamAssassinIntegrationMode(getEmailSpamAssassinIntegrationMode(mode));
	}

	/**
	 * Sets the SpamAssassin required score for an email account.
	 *
	 * @param  username        the username of the <code>LinuxServerAccount</code>
	 * @param  aoServer        the hostname of the <code>Server</code>
	 * @param  required_score  the new required score
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>LinuxServerAccount</code>
	 *
	 * @see  LinuxServerAccount#setSpamAssassinRequiredScore
	 * @see  #addLinuxServerAccount
	 */
	public void setLinuxServerAccountSpamAssassinRequiredScore(
		UserId username,
		String aoServer,
		float required_score
	) throws IllegalArgumentException, IOException, SQLException {
		getLinuxServerAccount(aoServer, username).setSpamAssassinRequiredScore(required_score);
	}

	/**
	 * Sets the number of days trash email is kept.
	 *
	 * @param  username  the username of the <code>LinuxServerAccount</code>
	 * @param  aoServer  the hostname of the <code>Server</code>
	 * @param  days  the new number of days, <code>-1</code> causes the trash to not be automatically removed
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>LinuxServerAccount</code>
	 *
	 * @see  LinuxServerAccount#setTrashEmailRetention
	 * @see  #addLinuxServerAccount
	 */
	public void setLinuxServerAccountTrashEmailRetention(
		UserId username,
		String aoServer,
		int days
	) throws IllegalArgumentException, IOException, SQLException {
		getLinuxServerAccount(aoServer, username).setTrashEmailRetention(days);
	}

	/**
	 * Sets the <code>use_inbox</code> flag on a <code>LinuxServerAccount</code>.
	 *
	 * @param  username  the username of the <code>LinuxServerAccount</code>
	 * @param  aoServer  the hostname of the <code>Server</code>
	 * @param  useInbox  the new flag
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>LinuxServerAccount</code>
	 *
	 * @see  LinuxServerAccount#setUseInbox
	 * @see  LinuxServerAccount#useInbox
	 * @see  #addLinuxServerAccount
	 */
	public void setLinuxServerAccountUseInbox(
		UserId username,
		String aoServer,
		boolean useInbox
	) throws IllegalArgumentException, IOException, SQLException {
		getLinuxServerAccount(aoServer, username).setUseInbox(useInbox);
	}

	/**
	 * Sets the info file for a <code>MajordomoList</code>.
	 *
	 * @param  domain  the domain of the <code>MajordomoServer</code>
	 * @param  aoServer  the hostname of the <code>Server</code>
	 * @param  listName  the name of the new list
	 * @param  file  the new file contents
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data
	 *					integrity violation occurs
	 * @exception  IllegalArgumentException  if the name is not valid or unable to find the
	 *                                  <code>Server</code>, code>EmailDomain</code>,
	 *                                  <code>MajordomoServer</code>, or <code>MajordomoList</code>
	 *
	 * @see  MajordomoList#setInfoFile
	 * @see  #addMajordomoList
	 * @see  #removeEmailList
	 */
	public void setMajordomoInfoFile(
		DomainName domain,
		String aoServer,
		String listName,
		String file
	) throws IllegalArgumentException, IOException, SQLException {
		Domain ed=getEmailDomain(aoServer, domain);
		MajordomoServer ms=ed.getMajordomoServer();
		if(ms==null) throw new IllegalArgumentException("Unable to find MajordomoServer: "+domain+" on "+aoServer);
		MajordomoList ml=ms.getMajordomoList(listName);
		if(ml==null) throw new IllegalArgumentException("Unable to find MajordomoList: "+listName+'@'+domain+" on "+aoServer);
		ml.setInfoFile(file);
	}

	/**
	 * Sets the intro file for a <code>MajordomoList</code>.
	 *
	 * @param  domain  the domain of the <code>MajordomoServer</code>
	 * @param  aoServer  the hostname of the <code>AOServer</code>
	 * @param  listName  the name of the new list
	 * @param  file  the new file contents
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data
	 *					integrity violation occurs
	 * @exception  IllegalArgumentException  if the name is not valid or unable to find the
	 *                                  <code>Server</code>, code>EmailDomain</code>,
	 *                                  <code>MajordomoServer</code>, or <code>MajordomoList</code>
	 *
	 * @see  MajordomoList#setIntroFile
	 * @see  #addMajordomoList
	 * @see  #removeEmailList
	 */
	public void setMajordomoIntroFile(
		DomainName domain,
		String aoServer,
		String listName,
		String file
	) throws IllegalArgumentException, IOException, SQLException {
		Domain ed=getEmailDomain(aoServer, domain);
		MajordomoServer ms=ed.getMajordomoServer();
		if(ms==null) throw new IllegalArgumentException("Unable to find MajordomoServer: "+domain+" on "+aoServer);
		MajordomoList ml=ms.getMajordomoList(listName);
		if(ml==null) throw new IllegalArgumentException("Unable to find MajordomoList: "+listName+'@'+domain+" on "+aoServer);
		ml.setIntroFile(file);
	}

	/**
	 * Sets the password for a <code>MySQLServerUser</code>.
	 *
	 * @param  username  the username of the <code>MySQLServerUser</code>
	 * @param  aoServer  the hostname of the <code>AOServer</code>
	 * @param  password  the new password
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>MySQLUser</code>,
	 *					<code>Server</code>, or <code>MySQLServerUser</code>
	 *
	 * @see  MySQLServerUser#setPassword
	 */
	public void setMySQLServerUserPassword(
		MySQLUserId username,
		MySQLServerName mysqlServer,
		String aoServer,
		String password
	) throws IllegalArgumentException, IOException, SQLException {
		getMySQLServerUser(aoServer, mysqlServer, username).setPassword(password==null || password.length()==0?null:password);
	}

	/**
	 * Sets the password for a <code>MySQLUser</code> by settings the password for
	 * all of its <code>MySQLServerUser</code>s.
	 *
	 * @param  username  the username of the <code>MySQLUser</code>
	 * @param  password  the new password
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>MySQLUser</code>
	 *
	 * @see  MySQLUser#setPassword
	 */
	public void setMySQLUserPassword(
		MySQLUserId username,
		String password
	) throws IllegalArgumentException, IOException, SQLException {
		getMySQLUser(username).setPassword(password==null || password.length()==0?null:password);
	}

	/**
	 * Sets the firewalld zones enable for a <code>NetBind</code>.
	 *
	 * @param  pkey  the pkey of the <code>NetBind</code>
	 * @param  firewalldZones  the set of enabled zones
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>NetBind</code>
	 *
	 * @see  NetBind#setFirewalldZones(java.util.Set)
	 */
	public void setNetBindFirewalldZones(
		int pkey,
		Set<FirewalldZoneName> firewalldZones
	) throws IllegalArgumentException, IOException, SQLException {
		getNetBind(pkey).setFirewalldZones(firewalldZones);
	}

	/**
	 * Sets the monitoring status for a <code>NetBind</code>
	 *
	 * @param  pkey  the pkey of the <code>NetBind</code>
	 * @param  enabled  the new monitoring state
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>NetBind</code>
	 *
	 * @see  NetBind#setMonitoringEnabled
	 */
	public void setNetBindMonitoringEnabled(
		int pkey,
		boolean enabled
	) throws IllegalArgumentException, IOException, SQLException {
		getNetBind(pkey).setMonitoringEnabled(enabled);
	}

	/**
	 * Sets the password for a <code>PostgresServerUser</code>.
	 *
	 * @param  username  the username of the <code>PostgresServerUser</code>
	 * @param  postgresServer  the name of the PostgreSQL server
	 * @param  aoServer  the hostname of the <code>Server</code>
	 * @param  password  the new password
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>PostgresUser</code>,
	 *					<code>Server</code>, or <code>PostgresServerUser</code>
	 *
	 * @see  PostgresServerUser#setPassword
	 */
	public void setPostgresServerUserPassword(
		PostgresUserId username,
		PostgresServerName postgresServer,
		String aoServer,
		String password
	) throws IllegalArgumentException, IOException, SQLException {
		getPostgresServerUser(aoServer, postgresServer, username).setPassword(password==null || password.length()==0?null:password);
	}

	/**
	 * Sets the password for a <code>PostgresUser</code> by settings the password for
	 * all of its <code>PostgresServerUser</code>s.
	 *
	 * @param  username  the username of the <code>PostgresUser</code>
	 * @param  password  the new password
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>PostgresUser</code>
	 *
	 * @see  PostgresUser#setPassword
	 */
	public void setPostgresUserPassword(
		PostgresUserId username,
		String password
	) throws IllegalArgumentException, IOException, SQLException {
		getPostgresUser(username).setPassword(password==null || password.length()==0?null:password);
	}

	/**
	 * Sets the primary URL for a <code>HttpdSiteBind</code>.
	 *
	 * @param  pkey  the pkey of the <code>HttpdSiteURL</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>HttpdSiteURL</code>
	 *
	 * @see  HttpdSiteURL#setAsPrimary()
	 */
	public void setPrimaryHttpdSiteURL(
		int pkey
	) throws IllegalArgumentException, IOException, SQLException {
		VirtualHostName hsu=connector.getWeb().getHttpdSiteURLs().get(pkey);
		if(hsu==null) throw new IllegalArgumentException("Unable to find HttpdSiteURL: "+pkey);
		hsu.setAsPrimary();
	}

	/**
	 * Sets the primary group for a <code>LinuxAccount</code>.
	 *
	 * @param  group_name  the name of the <code>LinuxGroup</code>
	 * @param  username  the username of the <code>LinuxAccount</code>
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if the name is not in a valid format or unable to
	 *					find the <code>LinuxAccount</code>
	 *
	 * @see  LinuxAccount#setPrimaryLinuxGroup
	 */
	public void setPrimaryLinuxGroupAccount(
		GroupId group_name,
		UserId username
	) throws IllegalArgumentException, IOException, SQLException {
		getLinuxAccount(username).setPrimaryLinuxGroup(getLinuxGroup(group_name));
	}

	/**
	 * Sets the password for a <code>Username</code>.  This password must pass the security
	 * checks provided by <code>checkUsernamePassword</code>.
	 *
	 * @param  username  the username
	 * @param  password  the new password
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>Username</code>
	 *
	 * @see  Username#setPassword
	 * @see  #checkUsernamePassword
	 * @see  #addUsername
	 */
	public void setUsernamePassword(
		UserId username,
		String password
	) throws IllegalArgumentException, IOException, SQLException {
		getUsername(username).setPassword(password);
	}

	/**
	 * Starts the Apache web server if it is not already running.
	 *
	 * @param  aoServer       the public hostname of the <code>AOServer</code>
	 *
	 * @exception  IOException  if not able to communicate with the server
	 * @exception  SQLException  if not able to access the database
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
	 *
	 * @see  AOServer#startApache
	 */
	public void startApache(String aoServer) throws IllegalArgumentException, IOException, SQLException {
		getAOServer(aoServer).startApache();
	}

	/**
	 * Starts the cron process if it is not already running.
	 *
	 * @param  aoServer       the public hostname of the <code>AOServer</code>
	 *
	 * @exception  IOException  if not able to communicate with the server
	 * @exception  SQLException  if not able to access the database
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
	 *
	 * @see  AOServer#startCron
	 */
	public void startCron(String aoServer) throws IllegalArgumentException, IOException, SQLException {
		getAOServer(aoServer).startCron();
	}

	/**
	 * Starts the distribution on a server and/or changes the setting of the user file scanning.
	 *
	 * @param  aoServer     the public hostname of the <code>AOServer</code> to start the scan on
	 * @param  includeUser  the flag indicating whether to include user files
	 *
	 * @exception  IOException  if not able to communicate with the server
	 * @exception  SQLException  if not able to access the database
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code>
	 *
	 * @see  AOServer#startDistro
	 */
	public void startDistro(String aoServer, boolean includeUser) throws IllegalArgumentException, IOException, SQLException {
		getAOServer(aoServer).startDistro(includeUser);
	}

	/**
	 * Starts and/or restarts the Tomcat or JBoss Java VM for the provided site.
	 *
	 * @param  siteName  the name of the site, which is the directory name under <code>/www/</code>
	 * @param  aoServer    the public hostname of the <code>AOServer</code> the site is hosted on
	 *
	 * @return  an error message if the Java VM cannot currently be restarted or
	 *          <code>null</code> on success
	 *
	 * @exception  IOException  if not able to communicate with the server
	 * @exception  SQLException  if not able to access the database
	 * @exception  IllegalArgumentException  if unable to find the <code>AOServer</code>,
	 *					<code>HttpdSite</code>, or <code>HttpdTomcatSite</code>
	 *
	 * @see  HttpdTomcatSite#startJVM
	 * @see  #addHttpdTomcatStdSite
	 */
	public String startJVM(String siteName, String aoServer) throws IllegalArgumentException, IOException, SQLException {
		Site site=getHttpdSite(aoServer, siteName);
		com.aoindustries.aoserv.client.web.tomcat.Site tomcatSite=site.getHttpdTomcatSite();
		if(tomcatSite==null) throw new IllegalArgumentException("HttpdSite "+siteName+" on "+aoServer+" is not a HttpdTomcatSite");
		return tomcatSite.startJVM();
	}

	/**
	 * Starts the MySQL database server if it is not already running.
	 *
	 * @param  aoServer       the public hostname of the <code>AOServer</code>
	 *
	 * @exception  IOException  if not able to communicate with the server
	 * @exception  SQLException  if not able to access the database
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
	 *
	 * @see  MySQLServer#startMySQL
	 */
	public void startMySQL(MySQLServerName mysqlServer, String aoServer) throws IllegalArgumentException, IOException, SQLException {
		getMySQLServer(aoServer, mysqlServer).startMySQL();
	}

	/**
	 * Starts the PostgreSQL database server if it is not already running.
	 *
	 * @param  postgresServer  the name of the PostgreSQL server
	 * @param  aoServer  the public hostname of the <code>AOServer</code>
	 *
	 * @exception  IOException  if not able to communicate with the server
	 * @exception  SQLException  if not able to access the database
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
	 *
	 * @see  PostgresServer#startPostgreSQL
	 */
	public void startPostgreSQL(PostgresServerName postgresServer, String aoServer) throws IllegalArgumentException, IOException, SQLException {
		getPostgresServer(aoServer, postgresServer).startPostgreSQL();
	}

	/**
	 * Starts the X Font Server if it is not already running.
	 *
	 * @param  aoServer       the public hostname of the <code>AOServer</code>
	 *
	 * @exception  IOException  if not able to communicate with the server
	 * @exception  SQLException  if not able to access the database
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
	 *
	 * @see  AOServer#startXfs
	 */
	public void startXfs(String aoServer) throws IllegalArgumentException, IOException, SQLException {
		getAOServer(aoServer).startXfs();
	}

	/**
	 * Starts the X Virtual Frame Buffer if it is not already running.
	 *
	 * @param  aoServer       the public hostname of the <code>AOServer</code>
	 *
	 * @exception  IOException  if not able to communicate with the server
	 * @exception  SQLException  if not able to access the database
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
	 *
	 * @see  AOServer#startXvfb
	 */
	public void startXvfb(String aoServer) throws IllegalArgumentException, IOException, SQLException {
		getAOServer(aoServer).startXvfb();
	}

	/**
	 * Stops the Apache web server if it is running.
	 *
	 * @param  aoServer       the public hostname of the <code>AOServer</code>
	 *
	 * @exception  IOException  if not able to communicate with the server
	 * @exception  SQLException  if not able to access the database
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
	 *
	 * @see  AOServer#stopApache
	 */
	public void stopApache(String aoServer) throws IllegalArgumentException, IOException, SQLException {
		getAOServer(aoServer).stopApache();
	}

	/**
	 * Stops the cron daemon if it is running.
	 *
	 * @param  aoServer       the public hostname of the <code>AOServer</code>
	 *
	 * @exception  IOException  if not able to communicate with the server
	 * @exception  SQLException  if not able to access the database
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
	 *
	 * @see  AOServer#stopCron
	 */
	public void stopCron(String aoServer) throws IllegalArgumentException, IOException, SQLException {
		getAOServer(aoServer).stopCron();
	}

	/**
	 * Stops the Tomcat or JBoss Java VM for the provided site.
	 *
	 * @param  siteName  the name of the site, which is the directory name under <code>/www/</code>
	 * @param  aoServer    the public hostname of the <code>AOServer</code> the site is hosted on
	 *
	 * @return  an error message if the Java VM cannot currently be stopped
	 *          <code>null</code> on success
	 *
	 * @exception  IOException  if not able to communicate with the server
	 * @exception  SQLException  if not able to access the database
	 * @exception  IllegalArgumentException  if unable to find the <code>AOServer</code>,
	 *					<code>HttpdSite</code>, or <code>HttpdTomcatSite</code>
	 *
	 * @see  HttpdTomcatSite#stopJVM
	 * @see  #addHttpdTomcatStdSite
	 */
	public String stopJVM(String siteName, String aoServer) throws IllegalArgumentException, IOException, SQLException {
		Site site=getHttpdSite(aoServer, siteName);
		com.aoindustries.aoserv.client.web.tomcat.Site tomcatSite=site.getHttpdTomcatSite();
		if(tomcatSite==null) throw new IllegalArgumentException("HttpdSite "+siteName+" on "+aoServer+" is not a HttpdTomcatSite");
		return tomcatSite.stopJVM();
	}

	/**
	 * Stops the MySQL database server if it is running.
	 *
	 * @param  aoServer       the public hostname of the <code>AOServer</code>
	 *
	 * @exception  IOException  if not able to communicate with the server
	 * @exception  SQLException  if not able to access the database
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
	 *
	 * @see  MySQLServer#stopMySQL
	 */
	public void stopMySQL(MySQLServerName mysqlServer, String aoServer) throws IllegalArgumentException, IOException, SQLException {
		getMySQLServer(aoServer, mysqlServer).stopMySQL();
	}

	/**
	 * Stops the PostgreSQL database server if it is running.
	 *
	 * @param  postgresServer  the name of the PostgreSQL server
	 * @param  aoServer  the public hostname of the <code>AOServer</code>
	 *
	 * @exception  IOException  if not able to communicate with the server
	 * @exception  SQLException  if not able to access the database
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
	 *
	 * @see  PostgresServer#stopPostgreSQL
	 */
	public void stopPostgreSQL(PostgresServerName postgresServer, String aoServer) throws IllegalArgumentException, IOException, SQLException {
		getPostgresServer(aoServer, postgresServer).stopPostgreSQL();
	}

	/**
	 * Stops the X Font Server if it is running.
	 *
	 * @param  aoServer       the public hostname of the <code>AOServer</code>
	 *
	 * @exception  IOException  if not able to communicate with the server
	 * @exception  SQLException  if not able to access the database
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
	 *
	 * @see  AOServer#stopXfs
	 */
	public void stopXfs(String aoServer) throws IllegalArgumentException, IOException, SQLException {
		getAOServer(aoServer).stopXfs();
	}

	/**
	 * Stops the X Virtual Frame Buffer if it is running.
	 *
	 * @param  aoServer       the public hostname of the <code>AOServer</code>
	 *
	 * @exception  IOException  if not able to communicate with the server
	 * @exception  SQLException  if not able to access the database
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
	 *
	 * @see  AOServer#stopXvfb
	 */
	public void stopXvfb(String aoServer) throws IllegalArgumentException, IOException, SQLException {
		getAOServer(aoServer).stopXvfb();
	}

	/**
	 * Updates a <code>HttpdTomcatContext</code> data source.
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code>, <code>HttpdSite</code>,
	 *                                  <code>HttpdTomcatSite</code> or <code>HttpdTomcatContext</code>.
	 */
	public void updateHttpdTomcatDataSource(
		String siteName,
		String aoServer,
		String path,
		String oldName,
		String newName,
		String driverClassName,
		String url,
		String username,
		String password,
		int maxActive,
		int maxIdle,
		int maxWait,
		String validationQuery
	) throws IllegalArgumentException, IOException, SQLException {
		Site hs=getHttpdSite(aoServer, siteName);
		com.aoindustries.aoserv.client.web.tomcat.Site hts=hs.getHttpdTomcatSite();
		if(hts==null) throw new IllegalArgumentException("Unable to find HttpdTomcatSite: "+siteName+" on "+aoServer);
		Context htc=hts.getHttpdTomcatContext(path);
		if(htc==null) throw new IllegalArgumentException("Unable to find HttpdTomcatContext: "+siteName+" on "+aoServer+" path='"+path+'\'');
		ContextDataSource htds=htc.getHttpdTomcatDataSource(oldName);
		if(htds==null) throw new IllegalArgumentException("Unable to find HttpdTomcatDataSource: "+siteName+" on "+aoServer+" path='"+path+"' name='"+oldName+'\'');
		htds.update(
			newName,
			driverClassName,
			url,
			username,
			password,
			maxActive,
			maxIdle,
			maxWait,
			validationQuery
		);
	}

	/**
	 * Updates a <code>HttpdTomcatContext</code> parameter.
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code>, <code>HttpdSite</code>,
	 *                                  <code>HttpdTomcatSite</code> or <code>HttpdTomcatContext</code>.
	 */
	public void updateHttpdTomcatParameter(
		String siteName,
		String aoServer,
		String path,
		String oldName,
		String newName,
		String value,
		boolean override,
		String description
	) throws IllegalArgumentException, IOException, SQLException {
		Site hs=getHttpdSite(aoServer, siteName);
		com.aoindustries.aoserv.client.web.tomcat.Site hts=hs.getHttpdTomcatSite();
		if(hts==null) throw new IllegalArgumentException("Unable to find HttpdTomcatSite: "+siteName+" on "+aoServer);
		Context htc=hts.getHttpdTomcatContext(path);
		if(htc==null) throw new IllegalArgumentException("Unable to find HttpdTomcatContext: "+siteName+" on "+aoServer+" path='"+path+'\'');
		ContextParameter htp=htc.getHttpdTomcatParameter(oldName);
		if(htp==null) throw new IllegalArgumentException("Unable to find HttpdTomcatParameter: "+siteName+" on "+aoServer+" path='"+path+"' name='"+oldName+'\'');
		htp.update(
			newName,
			value,
			override,
			description
		);
	}

	/**
	 * Waits for any processing or pending updates of the Apache configurations to complete.
	 *
	 * @param  aoServer  the hostname of the <code>AOServer</code> to wait for
	 *
	 * @exception  IOException  if not able to communicate with the server
	 * @exception  SQLException  if not able to access the database
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
	 *
	 * @see  AOServer#waitForHttpdSiteRebuild
	 * @see  #addHttpdTomcatStdSite
	 */
	public void waitForHttpdSiteRebuild(String aoServer) throws IllegalArgumentException, IOException, SQLException {
		getAOServer(aoServer).waitForHttpdSiteRebuild();
	}

	/**
	 * Waits for any processing or pending updates of the Linux account configurations to complete.
	 *
	 * @param  aoServer  the hostname of the <code>AOServer</code> to wait for
	 *
	 * @exception  IOException  if not able to communicate with the server
	 * @exception  SQLException  if not able to access the database
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
	 *
	 * @see  AOServer#waitForLinuxAccountRebuild
	 */
	public void waitForLinuxAccountRebuild(String aoServer) throws IllegalArgumentException, IOException, SQLException {
		getAOServer(aoServer).waitForLinuxAccountRebuild();
	}

	/**
	 * Waits for any processing or pending updates of the MySQL configurations to complete.
	 *
	 * @param  aoServer  the hostname of the <code>AOServer</code> to wait for
	 *
	 * @exception  IOException  if not able to communicate with the server
	 * @exception  SQLException  if not able to access the database
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
	 *
	 * @see  AOServer#waitForMySQLDatabaseRebuild
	 */
	public void waitForMySQLDatabaseRebuild(String aoServer) throws IllegalArgumentException, IOException, SQLException {
		getAOServer(aoServer).waitForMySQLDatabaseRebuild();
	}

	/**
	 * Waits for any processing or pending updates of the MySQL configurations to complete.
	 *
	 * @param  aoServer  the hostname of the <code>AOServer</code> to wait for
	 *
	 * @exception  IOException  if not able to communicate with the server
	 * @exception  SQLException  if not able to access the database
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
	 *
	 * @see  AOServer#waitForMySQLDBUserRebuild
	 */
	public void waitForMySQLDBUserRebuild(String aoServer) throws IllegalArgumentException, IOException, SQLException {
		getAOServer(aoServer).waitForMySQLDBUserRebuild();
	}

	/**
	 * Waits for any processing or pending updates of the MySQL server configurations to complete.
	 *
	 * @param  aoServer  the hostname of the <code>AOServer</code> to wait for
	 *
	 * @exception  IOException  if not able to communicate with the server
	 * @exception  SQLException  if not able to access the database
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
	 *
	 * @see  AOServer#waitForMySQLServerRebuild
	 */
	public void waitForMySQLServerRebuild(String aoServer) throws IllegalArgumentException, IOException, SQLException {
		getAOServer(aoServer).waitForMySQLServerRebuild();
	}

	/**
	 * Waits for any processing or pending updates of the MySQL configurations to complete.
	 *
	 * @param  aoServer  the hostname of the <code>AOServer</code> to wait for
	 *
	 * @exception  IOException  if not able to communicate with the server
	 * @exception  SQLException  if not able to access the database
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
	 *
	 * @see  AOServer#waitForMySQLUserRebuild
	 */
	public void waitForMySQLUserRebuild(String aoServer) throws IllegalArgumentException, IOException, SQLException {
		getAOServer(aoServer).waitForMySQLUserRebuild();
	}

	/**
	 * Waits for any processing or pending updates of the PostgreSQL configurations to complete.
	 *
	 * @param  aoServer  the hostname of the <code>AOServer</code> to wait for
	 *
	 * @exception  IOException  if not able to communicate with the server
	 * @exception  SQLException  if not able to access the database
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
	 *
	 * @see  AOServer#waitForPostgresDatabaseRebuild
	 */
	public void waitForPostgresDatabaseRebuild(String aoServer) throws IllegalArgumentException, IOException, SQLException {
		getAOServer(aoServer).waitForPostgresDatabaseRebuild();
	}

	/**
	 * Waits for any processing or pending updates of the PostgreSQL server configurations to complete.
	 *
	 * @param  aoServer  the hostname of the <code>AOServer</code> to wait for
	 *
	 * @exception  IOException  if not able to communicate with the server
	 * @exception  SQLException  if not able to access the database
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
	 *
	 * @see  AOServer#waitForPostgresServerRebuild
	 */
	public void waitForPostgresServerRebuild(String aoServer) throws IllegalArgumentException, IOException, SQLException {
		getAOServer(aoServer).waitForPostgresServerRebuild();
	}

	/**
	 * Waits for any processing or pending updates of the PostgreSQL configurations to complete.
	 *
	 * @param  aoServer  the hostname of the <code>AOServer</code> to wait for
	 *
	 * @exception  IOException  if not able to communicate with the server
	 * @exception  SQLException  if not able to access the database
	 * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
	 *
	 * @see  AOServer#waitForPostgresUserRebuild
	 */
	public void waitForPostgresUserRebuild(String aoServer) throws IllegalArgumentException, IOException, SQLException {
		getAOServer(aoServer).waitForPostgresUserRebuild();
	}

	/**
	 * @see  SslCertificate#check()
	 *
	 * @param  aoServer  the hostname of the server
	 * @param  keyFileOrCertbotName  Either the full path for keyFile or the per-server unique certbot name
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @throws IllegalArgumentException if unable to find the {@link Server} or {@link Certificate}
	 */
	public List<Certificate.Check> checkSslCertificate(
		String aoServer,
		String keyFileOrCertbotName
	) throws IllegalArgumentException, IOException, SQLException {
		return getSslCertificate(aoServer, keyFileOrCertbotName).check();
	}

	/**
	 * @see  VirtualServer#create()
	 *
	 * @param  virtualServer  the pkey, package/name, or hostname of the virtual server
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if unable to find the <cdoe>Server</code> or <code>VirtualServer</code>
	 */
	public String createVirtualServer(
		String virtualServer
	) throws IllegalArgumentException, IOException, SQLException {
		return getVirtualServer(virtualServer).create();
	}

	/**
	 * @see  VirtualServer#reboot()
	 *
	 * @param  virtualServer  the pkey, package/name, or hostname of the virtual server
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if unable to find the <cdoe>Server</code> or <code>VirtualServer</code>
	 */
	public String rebootVirtualServer(
		String virtualServer
	) throws IllegalArgumentException, IOException, SQLException {
		return getVirtualServer(virtualServer).reboot();
	}

	/**
	 * @see  VirtualServer#shutdown()
	 *
	 * @param  virtualServer  the pkey, package/name, or hostname of the virtual server
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if unable to find the <cdoe>Server</code> or <code>VirtualServer</code>
	 */
	public String shutdownVirtualServer(
		String virtualServer
	) throws IllegalArgumentException, IOException, SQLException {
		return getVirtualServer(virtualServer).shutdown();
	}

	/**
	 * @see  VirtualServer#destroy()
	 *
	 * @param  virtualServer  the pkey, package/name, or hostname of the virtual server
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if unable to find the <cdoe>Server</code> or <code>VirtualServer</code>
	 */
	public String destroyVirtualServer(
		String virtualServer
	) throws IllegalArgumentException, IOException, SQLException {
		return getVirtualServer(virtualServer).destroy();
	}

	/**
	 * @see  VirtualServer#pause()
	 *
	 * @param  virtualServer  the pkey, package/name, or hostname of the virtual server
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if unable to find the <cdoe>Server</code> or <code>VirtualServer</code>
	 */
	public String pauseVirtualServer(
		String virtualServer
	) throws IllegalArgumentException, IOException, SQLException {
		return getVirtualServer(virtualServer).pause();
	}

	/**
	 * @see  VirtualServer#unpause()
	 *
	 * @param  virtualServer  the pkey, package/name, or hostname of the virtual server
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if unable to find the <cdoe>Server</code> or <code>VirtualServer</code>
	 */
	public String unpauseVirtualServer(
		String virtualServer
	) throws IllegalArgumentException, IOException, SQLException {
		return getVirtualServer(virtualServer).unpause();
	}

	/**
	 * @see  VirtualServer#getStatus()
	 *
	 * @param  virtualServer  the pkey, package/name, or hostname of the virtual server
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if unable to find the <cdoe>Server</code> or <code>VirtualServer</code>
	 */
	public int getVirtualServerStatus(
		String virtualServer
	) throws IllegalArgumentException, IOException, SQLException {
		return getVirtualServer(virtualServer).getStatus();
	}

	/**
	 * @see  IpReputationSet#addReputation(int, com.aoindustries.aoserv.client.IpReputationSet.ConfidenceType, com.aoindustries.aoserv.client.IpReputationSet.ReputationType, short)
	 *
	 * @param  identifier      the unique identifier of the set
	 * @param  host            the dotted-quad (A.B.C.D) format IPv4 address
	 * @param  confidence      either "uncertain" or "definite"
	 * @param  reputationType  either "good" or "bad"
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if unable to find the <cdoe>IpReputationSet</code> or unable to parse parameters
	 */
	public void addIpReputation(
		String identifier,
		String host,
		String confidence,
		String reputationType,
		short score
	) throws IllegalArgumentException, IOException, SQLException {
		com.aoindustries.aoserv.client.net.reputation.Set set = getIpReputationSet(identifier);
		int hostIp = IpAddress.getIntForIPAddress(host);
		set.addReputation(hostIp,
			com.aoindustries.aoserv.client.net.reputation.Set.ConfidenceType.valueOf(confidence.toUpperCase(Locale.ROOT)),
			com.aoindustries.aoserv.client.net.reputation.Set.ReputationType.valueOf(reputationType.toUpperCase(Locale.ROOT)),
			score
		);
	}

	/**
	 * Begins a verification of the redundancy of the virtual disk.
	 *
	 * @param  virtualServer  the pkey, package/name, or hostname of the virtual server
	 * @param  device  the device identifier (xvda, xvdb, ...)
	 *
	 * @return  The time the verification began, which may be in the past if a verification was already in progress
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database or a data integrity
	 *					violation occurs
	 * @exception  IllegalArgumentException  if unable to find the <code>VirtualServer</code> or
	 *                                  <code>VirtualDisk</code>
	 *
	 * @see  VirtualDisk#verify()
	 */
	public long verifyVirtualDisk(
		String virtualServer,
		String device
	) throws IllegalArgumentException, IOException, SQLException {
		return getVirtualDisk(virtualServer, device).verify();
	}

	/**
	 * @see  VirtualServer#getPrimaryPhysicalServer()
	 *
	 * @param  virtualServer  the pkey, package/name, or hostname of the virtual server
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if unable to find the <cdoe>Server</code> or <code>VirtualServer</code>
	 */
	public String getPrimaryVirtualServer(
		String virtualServer
	) throws IllegalArgumentException, IOException, SQLException {
		return
			getVirtualServer(virtualServer)
			.getPrimaryPhysicalServer()
			.toString()
		;
	}

	/**
	 * @see  VirtualServer#getSecondaryPhysicalServer()
	 *
	 * @param  virtualServer  the pkey, package/name, or hostname of the virtual server
	 *
	 * @exception  IOException  if unable to contact the server
	 * @exception  SQLException  if unable to access the database
	 * @exception  IllegalArgumentException  if unable to find the <cdoe>Server</code> or <code>VirtualServer</code>
	 */
	public String getSecondaryVirtualServer(
		String virtualServer
	) throws IllegalArgumentException, IOException, SQLException {
		return
			getVirtualServer(virtualServer)
			.getSecondaryPhysicalServer()
			.toString()
		;
	}
}
