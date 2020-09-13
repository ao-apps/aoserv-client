/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2015, 2016, 2017, 2018, 2019, 2020  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.account.Administrator;
import com.aoindustries.aoserv.client.account.User;
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.linux.Group;
import com.aoindustries.aoserv.client.linux.LinuxId;
import com.aoindustries.aoserv.client.linux.PosixPath;
import com.aoindustries.aoserv.client.linux.User.Gecos;
import com.aoindustries.aoserv.client.pki.HashedPassword;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.sql.SQLComparator;
import com.aoindustries.aoserv.client.sql.SQLExpression;
import com.aoindustries.collections.IntArrayList;
import com.aoindustries.collections.IntList;
import com.aoindustries.exception.ConfigurationException;
import com.aoindustries.io.IoUtils;
import com.aoindustries.io.stream.StreamWritable;
import com.aoindustries.io.stream.StreamableInput;
import com.aoindustries.io.stream.StreamableOutput;
import com.aoindustries.net.DomainLabel;
import com.aoindustries.net.DomainLabels;
import com.aoindustries.net.DomainName;
import com.aoindustries.net.Email;
import com.aoindustries.net.HostAddress;
import com.aoindustries.net.InetAddress;
import com.aoindustries.net.MacAddress;
import com.aoindustries.net.Port;
import com.aoindustries.security.Identifier;
import com.aoindustries.table.TableListener;
import com.aoindustries.util.sort.ComparisonSortAlgorithm;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An <code>AOServConnector</code> provides the connection between the object
 * layer and the data.  This connection may be persistent over TCP sockets, or
 * it may be request-based like HTTP.
 *
 * @author  AO Industries, Inc.
 */
abstract public class AOServConnector implements SchemaParent {

	/**
	 * The maximum size of the master entropy pool in bytes.
	 */
	public static final long MASTER_ENTROPY_POOL_SIZE=(long)64*1024*1024;

	/**
	 * The delay for each retry attempt.
	 */
	private static final long[] retryAttemptDelays = {
		0,
		1,
		2,
		3,
		4,
		6,
		8,
		12,
		16,
		24,
		32,
		48,
		64,
		96,
		128,
		192,
		256,
		384,
		512,
		768,
		1024,
		1536,
		2048,
		3072
	};

	/**
	 * The number of attempts that will be made when request retry is allowed.
	 */
	private static final int RETRY_ATTEMPTS = retryAttemptDelays.length + 1;

	/**
	 * Certain errors will not be retried.
	 */
	static boolean isImmediateFail(Throwable T) {
		if(T instanceof ThreadDeath) {
			return true;
		} else {
			String message = T.getMessage();
			return
				(T instanceof ConfigurationException)
				|| (
					(T instanceof IOException)
					&& message != null
					&& (
						message.equals("Connection attempted with invalid password")
						|| message.equals("Connection attempted with empty password")
						|| message.equals("Connection attempted with empty connect username")
						|| message.startsWith("Unable to find Administrator: ")
						|| message.startsWith("Not allowed to switch users from ")
					)
				)
			;
		}
	}

	/**
	 * One thread pool is shared by all instances.
	 */
	final static ExecutorService executorService = Executors.newCachedThreadPool();

	/*private static final String[] profileTitles={
		"Method",
		"Parameter",
		"Use Count",
		"Total Time",
		"Min Time",
		"Avg Time",
		"Max Time"
	};*/

	/**
	 * @see  #getConnectorId()
	 */
	// TODO: AtomicReference
	protected static class IdLock {}
	protected final IdLock idLock = new IdLock();
	protected Identifier id = null;

	@SuppressWarnings("NonConstantLogger")
	private final Logger logger;

	/**
	 * @see  #getHostname()
	 */
	final HostAddress hostname;

	/**
	 * @see  #getLocalIp()
	 */
	final InetAddress local_ip;

	/**
	 * @see  #getPort()
	 */
	final Port port;

	/**
	 * @see  #getConnectedAs()
	 */
	final User.Name connectAs;

	/**
	 * @see  #getAuthenticatedAs()
	 */
	final User.Name authenticateAs;

	final DomainName daemonServer;

	protected final String password;

	private static class TestConnectLock {}
	private final TestConnectLock testConnectLock=new TestConnectLock();

	private final com.aoindustries.aoserv.client.account.Schema account;
	public com.aoindustries.aoserv.client.account.Schema getAccount() {return account;}

	private final com.aoindustries.aoserv.client.accounting.Schema accounting;
	public com.aoindustries.aoserv.client.accounting.Schema getAccounting() {return accounting;}

	private final com.aoindustries.aoserv.client.aosh.Schema aosh;
	public com.aoindustries.aoserv.client.aosh.Schema getAosh() {return aosh;}

	private final com.aoindustries.aoserv.client.backup.Schema backup;
	public com.aoindustries.aoserv.client.backup.Schema getBackup() {return backup;}

	private final com.aoindustries.aoserv.client.billing.Schema billing;
	public com.aoindustries.aoserv.client.billing.Schema getBilling() {return billing;}

	private final com.aoindustries.aoserv.client.distribution.Schema distribution;
	public com.aoindustries.aoserv.client.distribution.Schema getDistribution() {return distribution;}

	private final com.aoindustries.aoserv.client.distribution.management.Schema distribution_management;
	public com.aoindustries.aoserv.client.distribution.management.Schema getDistribution_management() {return distribution_management;}

	private final com.aoindustries.aoserv.client.dns.Schema dns;
	public com.aoindustries.aoserv.client.dns.Schema getDns() {return dns;}

	private final com.aoindustries.aoserv.client.email.Schema email;
	public com.aoindustries.aoserv.client.email.Schema getEmail() {return email;}

	private final com.aoindustries.aoserv.client.ftp.Schema ftp;
	public com.aoindustries.aoserv.client.ftp.Schema getFtp() {return ftp;}

	private final com.aoindustries.aoserv.client.infrastructure.Schema infrastructure;
	public com.aoindustries.aoserv.client.infrastructure.Schema getInfrastructure() {return infrastructure;}

	private final com.aoindustries.aoserv.client.linux.Schema linux;
	public com.aoindustries.aoserv.client.linux.Schema getLinux() {return linux;}

	private final com.aoindustries.aoserv.client.master.Schema master;
	public com.aoindustries.aoserv.client.master.Schema getMaster() {return master;}

	private final com.aoindustries.aoserv.client.mysql.Schema mysql;
	public com.aoindustries.aoserv.client.mysql.Schema getMysql() {return mysql;}

	private final com.aoindustries.aoserv.client.net.Schema net;
	public com.aoindustries.aoserv.client.net.Schema getNet() {return net;}

	private final com.aoindustries.aoserv.client.payment.Schema payment;
	public com.aoindustries.aoserv.client.payment.Schema getPayment() {return payment;}

	private final com.aoindustries.aoserv.client.pki.Schema pki;
	public com.aoindustries.aoserv.client.pki.Schema getPki() {return pki;}

	private final com.aoindustries.aoserv.client.postgresql.Schema postgresql;
	public com.aoindustries.aoserv.client.postgresql.Schema getPostgresql() {return postgresql;}

	private final com.aoindustries.aoserv.client.reseller.Schema reseller;
	public com.aoindustries.aoserv.client.reseller.Schema getReseller() {return reseller;}

	private final com.aoindustries.aoserv.client.schema.Schema schema;
	public com.aoindustries.aoserv.client.schema.Schema getSchema() {return schema;}

	private final com.aoindustries.aoserv.client.scm.Schema scm;
	public com.aoindustries.aoserv.client.scm.Schema getScm() {return scm;}

	private final com.aoindustries.aoserv.client.signup.Schema signup;
	public com.aoindustries.aoserv.client.signup.Schema getSignup() {return signup;}

	private final com.aoindustries.aoserv.client.ticket.Schema ticket;
	public com.aoindustries.aoserv.client.ticket.Schema getTicket() {return ticket;}

	private final com.aoindustries.aoserv.client.web.Schema web;
	public com.aoindustries.aoserv.client.web.Schema getWeb() {return web;}

	private final com.aoindustries.aoserv.client.web.jboss.Schema web_jboss;
	public com.aoindustries.aoserv.client.web.jboss.Schema getWeb_jboss() {return web_jboss;}

	private final com.aoindustries.aoserv.client.web.tomcat.Schema web_tomcat;
	public com.aoindustries.aoserv.client.web.tomcat.Schema getWeb_tomcat() {return web_tomcat;}

	private final List<? extends Schema> schemas;

	/**
	 * The tables are placed in this list in the constructor.
	 * This list is aligned with the table identifiers in
	 * {@link Table.TableID}}.
	 *
	 * @see  Table.TableID#ordinal()
	 */
	private final List<? extends AOServTable<?,?>> tables;

	private final SimpleAOClient simpleAOClient;
	public SimpleAOClient getSimpleAOClient() {return simpleAOClient;}

	protected AOServConnector(
		HostAddress hostname,
		InetAddress local_ip,
		Port port,
		User.Name connectAs,
		User.Name authenticateAs,
		String password,
		DomainName daemonServer
	) {
		this.logger = Logger.getLogger(getClass().getName());

		this.hostname = hostname;
		this.local_ip = local_ip;
		this.port = port;
		this.connectAs = connectAs;
		this.authenticateAs = authenticateAs;
		this.password = password;
		this.daemonServer = daemonServer;

		// TODO: Load schemas with ServiceLoader
		ArrayList<Schema> newSchemas = new ArrayList<>();
		newSchemas.add(account = new com.aoindustries.aoserv.client.account.Schema(this));
		newSchemas.add(accounting = new com.aoindustries.aoserv.client.accounting.Schema(this));
		newSchemas.add(aosh = new com.aoindustries.aoserv.client.aosh.Schema(this));
		newSchemas.add(backup = new com.aoindustries.aoserv.client.backup.Schema(this));
		newSchemas.add(billing = new com.aoindustries.aoserv.client.billing.Schema(this));
		newSchemas.add(distribution = new com.aoindustries.aoserv.client.distribution.Schema(this));
		newSchemas.add(distribution_management = new com.aoindustries.aoserv.client.distribution.management.Schema(this));
		newSchemas.add(dns = new com.aoindustries.aoserv.client.dns.Schema(this));
		newSchemas.add(email = new com.aoindustries.aoserv.client.email.Schema(this));
		newSchemas.add(ftp = new com.aoindustries.aoserv.client.ftp.Schema(this));
		newSchemas.add(infrastructure = new com.aoindustries.aoserv.client.infrastructure.Schema(this));
		newSchemas.add(linux = new com.aoindustries.aoserv.client.linux.Schema(this));
		newSchemas.add(master = new com.aoindustries.aoserv.client.master.Schema(this));
		newSchemas.add(mysql = new com.aoindustries.aoserv.client.mysql.Schema(this));
		newSchemas.add(net = new com.aoindustries.aoserv.client.net.Schema(this));
		newSchemas.add(payment = new com.aoindustries.aoserv.client.payment.Schema(this));
		newSchemas.add(pki = new com.aoindustries.aoserv.client.pki.Schema(this));
		newSchemas.add(postgresql = new com.aoindustries.aoserv.client.postgresql.Schema(this));
		newSchemas.add(reseller = new com.aoindustries.aoserv.client.reseller.Schema(this));
		newSchemas.add(schema = new com.aoindustries.aoserv.client.schema.Schema(this));
		newSchemas.add(scm = new com.aoindustries.aoserv.client.scm.Schema(this));
		newSchemas.add(signup = new com.aoindustries.aoserv.client.signup.Schema(this));
		newSchemas.add(ticket = new com.aoindustries.aoserv.client.ticket.Schema(this));
		newSchemas.add(web = new com.aoindustries.aoserv.client.web.Schema(this));
		newSchemas.add(web_jboss = new com.aoindustries.aoserv.client.web.jboss.Schema(this));
		newSchemas.add(web_tomcat = new com.aoindustries.aoserv.client.web.tomcat.Schema(this));
		newSchemas.trimToSize();
		schemas = Collections.unmodifiableList(newSchemas);

		// These must match the table IDs in SchemaTable
		ArrayList<AOServTable<?,?>> newTables = new ArrayList<>();
		newTables.add(linux.getDaemonAcl());
		newTables.add(linux.getServer());
		newTables.add(master.getPermission());
		newTables.add(schema.getAoservProtocol());
		newTables.add(aosh.getCommand());
		newTables.add(distribution.getArchitecture());
		newTables.add(backup.getBackupPartition());
		newTables.add(backup.getBackupReport());
		newTables.add(backup.getBackupRetention());
		newTables.add(accounting.getBankAccount());
		newTables.add(accounting.getBankTransactionType());
		newTables.add(accounting.getBankTransaction());
		newTables.add(accounting.getBank());
		newTables.add(email.getBlackholeAddress());
		newTables.add(reseller.getBrand());
		newTables.add(account.getAdministrator());
		newTables.add(master.getAdministratorPermission());
		newTables.add(account.getProfile());
		newTables.add(account.getAccount());
		newTables.add(account.getAccountHost());
		newTables.add(payment.getCountryCode());
		newTables.add(payment.getProcessor());
		newTables.add(payment.getPayment());
		newTables.add(payment.getCreditCard());
		newTables.add(billing.getCurrency());
		newTables.add(scm.getCvsRepository());
		newTables.add(email.getCyrusImapdBind());
		newTables.add(email.getCyrusImapdServer());
		newTables.add(account.getDisableLog());
		newTables.add(distribution_management.getDistroFileType());
		newTables.add(distribution_management.getDistroFile());
		newTables.add(distribution_management.getDistroReportType());
		newTables.add(dns.getForbiddenZone());
		newTables.add(dns.getRecord());
		newTables.add(dns.getTopLevelDomain());
		newTables.add(dns.getRecordType());
		newTables.add(dns.getZone());
		newTables.add(email.getAddress());
		newTables.add(email.getAttachmentBlock());
		newTables.add(email.getAttachmentType());
		newTables.add(email.getDomain());
		newTables.add(email.getForwarding());
		newTables.add(email.getListAddress());
		newTables.add(email.getList());
		newTables.add(email.getPipeAddress());
		newTables.add(email.getPipe());
		newTables.add(email.getSmtpRelayType());
		newTables.add(email.getSmtpRelay());
		newTables.add(email.getSmtpSmartHostDomain());
		newTables.add(email.getSmtpSmartHost());
		newTables.add(email.getSpamAssassinMode());
		newTables.add(pki.getEncryptionKey());
		newTables.add(accounting.getExpenseCategory());
		newTables.add(backup.getFileReplicationLog());
		newTables.add(backup.getFileReplication());
		newTables.add(backup.getFileReplicationSchedule());
		newTables.add(backup.getMysqlReplication());
		newTables.add(backup.getFileReplicationSetting());
		newTables.add(net.getFirewallZone());
		newTables.add(ftp.getGuestUser());
		newTables.add(web.getHttpdBind());
		newTables.add(web_jboss.getSite());
		newTables.add(web_jboss.getVersion());
		newTables.add(web_tomcat.getWorkerName());
		newTables.add(web_tomcat.getJkProtocol());
		newTables.add(web.getHttpdServer());
		newTables.add(web_tomcat.getSharedTomcat());
		newTables.add(web.getLocation());
		newTables.add(web.getHeader());
		newTables.add(web.getRewriteRule());
		newTables.add(web.getVirtualHost());
		newTables.add(web.getVirtualHostName());
		newTables.add(web.getSite());
		newTables.add(web.getStaticSite());
		newTables.add(web_tomcat.getContext());
		newTables.add(web_tomcat.getContextDataSource());
		newTables.add(web_tomcat.getContextParameter());
		newTables.add(web_tomcat.getJkMount());
		newTables.add(web_tomcat.getSite());
		newTables.add(web_tomcat.getSharedTomcatSite());
		newTables.add(web_tomcat.getPrivateTomcatSite());
		newTables.add(web_tomcat.getVersion());
		newTables.add(web_tomcat.getWorker());
		newTables.add(net.getIpAddress());
		newTables.add(net.getMonitoring().getIpAddressMonitoring());
		newTables.add(net.getReputation().getLimiterClass());
		newTables.add(net.getReputation().getLimiterSet());
		newTables.add(net.getReputation().getLimiter());
		newTables.add(net.getReputation().getHost());
		newTables.add(net.getReputation().getNetwork());
		newTables.add(net.getReputation().getSet());
		newTables.add(ticket.getLanguage());
		newTables.add(email.getInboxAddress());
		newTables.add(linux.getUserType());
		newTables.add(linux.getUser());
		newTables.add(linux.getGroupUser());
		newTables.add(linux.getGroupType());
		newTables.add(linux.getGroup());
		newTables.add(linux.getUserServer());
		newTables.add(linux.getGroupServer());
		newTables.add(email.getMajordomoList());
		newTables.add(email.getMajordomoServer());
		newTables.add(email.getMajordomoVersion());
		newTables.add(master.getUserAcl());
		newTables.add(master.getProcess());
		newTables.add(master.getServerStat());
		newTables.add(master.getUserHost());
		newTables.add(master.getUser());
		newTables.add(billing.getMonthlyCharge());
		newTables.add(mysql.getDatabase());
		newTables.add(mysql.getDatabaseUser());
		newTables.add(mysql.getUserServer());
		newTables.add(mysql.getServer());
		newTables.add(mysql.getUser());
		newTables.add(net.getBindFirewallZone());
		newTables.add(net.getBind());
		newTables.add(net.getDeviceId());
		newTables.add(net.getDevice());
		newTables.add(net.getTcpRedirect());
		newTables.add(billing.getNoticeLog());
		newTables.add(billing.getNoticeLogBalance());
		newTables.add(billing.getNoticeType());
		newTables.add(distribution.getOperatingSystemVersion());
		newTables.add(distribution.getOperatingSystem());
		newTables.add(billing.getPackageCategory());
		newTables.add(billing.getPackageDefinitionLimit());
		newTables.add(billing.getPackageDefinition());
		newTables.add(billing.getPackage());
		newTables.add(payment.getPaymentType());
		newTables.add(infrastructure.getPhysicalServer());
		newTables.add(postgresql.getDatabase());
		newTables.add(postgresql.getEncoding());
		newTables.add(postgresql.getUserServer());
		newTables.add(postgresql.getServer());
		newTables.add(postgresql.getUser());
		newTables.add(postgresql.getVersion());
		newTables.add(ftp.getPrivateServer());
		newTables.add(infrastructure.getProcessorType());
		newTables.add(net.getAppProtocol());
		newTables.add(infrastructure.getRack());
		newTables.add(reseller.getReseller());
		newTables.add(billing.getResource());
		newTables.add(schema.getColumn());
		newTables.add(schema.getForeignKey());
		newTables.add(schema.getTable());
		newTables.add(schema.getType());
		newTables.add(email.getSendmailBind());
		newTables.add(email.getSendmailServer());
		newTables.add(infrastructure.getServerFarm());
		newTables.add(net.getHost());
		newTables.add(linux.getShell());
		newTables.add(signup.getOption());
		newTables.add(signup.getRequest());
		newTables.add(email.getSpamMessage());
		newTables.add(pki.getCertificateName());
		newTables.add(pki.getCertificateOtherUse());
		newTables.add(pki.getCertificate());
		newTables.add(email.getSystemAlias());
		newTables.add(distribution.getSoftwareCategorization());
		newTables.add(distribution.getSoftwareCategory());
		newTables.add(distribution.getSoftware());
		newTables.add(distribution.getSoftwareVersion());
		newTables.add(ticket.getActionType());
		newTables.add(ticket.getAction());
		newTables.add(ticket.getAssignment());
		newTables.add(reseller.getBrandCategory());
		newTables.add(reseller.getCategory());
		newTables.add(ticket.getPriority());
		newTables.add(ticket.getStatus());
		newTables.add(ticket.getTicketType());
		newTables.add(ticket.getTicket());
		newTables.add(linux.getTimeZone());
		newTables.add(billing.getTransactionType());
		newTables.add(billing.getTransaction());
		newTables.add(account.getUsState());
		newTables.add(account.getUser());
		newTables.add(infrastructure.getVirtualDisk());
		newTables.add(infrastructure.getVirtualServer());
		newTables.add(billing.getWhoisHistory());
		newTables.add(billing.getWhoisHistoryAccount());
		newTables.trimToSize();
		tables = Collections.unmodifiableList(newTables);

		simpleAOClient = new SimpleAOClient(this);
	}

	/**
	 * Uses equivalence equality like {@link Object#equals(java.lang.Object)}.  Two
	 * connectors are considered equal only if they refer to the same
	 * object.
	 */
	@Override
	final public boolean equals(Object O) {
		if(O==null) return false;
		return
			(O instanceof AOServConnector) && // This is just to get rid of the NetBeans 6.5 warning about not checking type of parameter
			this==O
		;
	}

	/**
	 * Uses equivalence hashCode like {@link Object#hashCode()}.
	 */
	@Override
	final public int hashCode() {
		return super.hashCode();
	}

	/**
	 * Clears all caches used by this connector.
	 */
	public void clearCaches() {
		for(AOServTable<?,?> table : tables) {
			table.clearCache();
		}
	}

	/**
	 * Executes an aosh command and captures its output into a <code>String</code>.
	 *
	 * @param  args  the command and arguments to be processed
	 *
	 * @return  the results of the command wrapped into a <code>String</code>
	 *
	 * @exception  IOException  if unable to access the server
	 * @exception  SQLException  if unable to access the database or data integrity
	 *                           checks fail
	 */
	public String executeCommand(String[] args) throws IOException, SQLException {
		return AOSH.executeCommand(this, args);
	}

	/**
	 * Gets the logger for this connector.
	 */
	final protected Logger getLogger() {
		return logger;
	}

	/**
	 * Allocates a connection to the server.  These connections must later be
	 * released with the <code>releaseConnection</code> method.  Connection
	 * pooling is obtained this way.  These connections may be over any protocol,
	 * so they may only safely be used for one client/server exchange per
	 * allocation.  Also, if connections are not <i>always</i> released, deadlock
	 * will quickly occur.  Please use a try/finally block immediately after
	 * allocating the connection to make sure it is always released.
	 *
	 * @return  the connection to the server
	 *
	 * @exception  InterruptedIOException  if interrupted while connecting
	 * @exception  IOException  if unable to connect to the server
	 *
	 * @see  #releaseConnection
	 */
	protected abstract AOServConnection getConnection(int maxConnections) throws InterruptedIOException, IOException;

	/**
	 * Gets the default <code>AOServConnector</code> as defined in the
	 * <code>com/aoindustries/aoserv/client/aoserv-client.properties</code>
	 * resource.  Each possible protocol is tried, in order, until a
	 * successful connection is made.
	 *
	 * @return  the first <code>AOServConnector</code> to successfully connect
	 *          to the server
	 *
	 * @exception  ConfigurationException  if no connection can be established
	 */
	public static AOServConnector getConnector() throws ConfigurationException {
		User.Name username = AOServClientConfiguration.getUsername();
		DomainName daemonServer = AOServClientConfiguration.getDaemonServer();
		return getConnector(
			username,
			username,
			AOServClientConfiguration.getPassword(),
			daemonServer
		);
	}

	/**
	 * Gets the <code>AOServConnector</code> with the provided authentication
	 * information.  The <code>com/aoindustries/aoserv/client/aoserv-client.properties</code>
	 * resource determines which protocols will be used.  Each possible protocol is
	 * tried, in order, until a successful connection is made.
	 *
	 * @param  username  the username to connect as
	 * @param  password  the password to connect with
	 *
	 * @return  the first <code>AOServConnector</code> to successfully connect
	 *          to the server
	 *
	 * @exception  ConfigurationException  if no connection can be established
	 */
	public static AOServConnector getConnector(User.Name username, String password) throws ConfigurationException {
		return getConnector(username, username, password, null);
	}

	/**
	 * Gets the <code>AOServConnector</code> with the provided authentication
	 * information.  The <code>com/aoindustries/aoserv/client/aoserv-client.properties</code>
	 * resource determines which protocols will be used.  Each possible protocol is
	 * tried, in order, until a successful connection is made.
	 *
	 * @param  connectAs  the username to connect as
	 * @param  authenticateAs  the username used for authentication, if different than
	 *                                        <code>connectAs</code>, this username must have super user
	 *                                        privileges
	 * @param  password  the password to connect with
	 *
	 * @return  the first <code>AOServConnector</code> to successfully connect
	 *          to the server
	 *
	 * @exception  ConfigurationException  if no connection can be established
	 */
	public static AOServConnector getConnector(
		User.Name connectAs,
		User.Name authenticateAs,
		String password,
		DomainName daemonServer
	) throws ConfigurationException {
		List<String> protocols = AOServClientConfiguration.getProtocols();
		int size = protocols.size();
		for(int c = 0; c < size; c++) {
			String protocol = protocols.get(c);
			try {
				AOServConnector connector;
				if(TCPConnector.TCP_PROTOCOL.equals(protocol)) {
					connector = TCPConnector.getTCPConnector(
						AOServClientConfiguration.getTcpHostname(),
						AOServClientConfiguration.getTcpLocalIp(),
						AOServClientConfiguration.getTcpPort(),
						connectAs,
						authenticateAs,
						password,
						daemonServer,
						AOServClientConfiguration.getTcpConnectionPoolSize(),
						AOServClientConfiguration.getTcpConnectionMaxAge()
					);
				} else if(SSLConnector.SSL_PROTOCOL.equals(protocol)) {
					connector = SSLConnector.getSSLConnector(
						AOServClientConfiguration.getSslHostname(),
						AOServClientConfiguration.getSslLocalIp(),
						AOServClientConfiguration.getSslPort(),
						connectAs,
						authenticateAs,
						password,
						daemonServer,
						AOServClientConfiguration.getSslConnectionPoolSize(),
						AOServClientConfiguration.getSslConnectionMaxAge(),
						AOServClientConfiguration.getSslTruststorePath(),
						AOServClientConfiguration.getSslTruststorePassword()
					);
				/*
				} else if("http".equals(protocol)) {
					connector=new HTTPConnector();
				} else if("https".equals(protocol)) {
					connector=new HTTPSConnector();
				*/
				} else throw new ConfigurationException("Unknown protocol in aoserv.client.protocols: "+protocol);

				return connector;
			} catch(ConfigurationException err) {
				Logger.getLogger(AOServConnector.class.getName()).log(Level.SEVERE, null, err);
			}
		}
		throw new ConfigurationException("Unable to connect using any of the available protocols.");
	}

	/**
	 * Each connector is assigned a unique identifier, which the
	 * server uses to not send events originating from
	 * this connector back to connections of this
	 * connector.
	 *
	 * @return  the globally unique identifier or {@code null} if
	 *          the identifier has not yet been assigned
	 */
	final public Identifier getConnectorId() {
		synchronized(idLock) {
			return id;
		}
	}

	/**
	 * Gets the hostname of the server that is connected to.
	 */
	final public HostAddress getHostname() {
		return hostname;
	}

	/**
	 * Gets the optional local IP address that connections are made from.
	 */
	final public InetAddress getLocalIp() {
		return local_ip;
	}

	/**
	 * Gets the server port that is connected to.
	 */
	final public Port getPort() {
		return port;
	}

	/**
	 * Gets the communication protocol being used.
	 */
	abstract public String getProtocol();

	/**
	 * A single random number generator is shared by all connector resources to provide better randomness.
	 */
	private static final SecureRandom secureRandom = new SecureRandom();
	public static SecureRandom getSecureRandom() {
		return secureRandom;
	}

	/**
	 * A fast pseudo-random number generated seeded by secure random.
	 */
	private static final Random fastRandom = new Random(IoUtils.bufferToLong(secureRandom.generateSeed(8)));
	public static Random getFastRandom() {
		return fastRandom;
	}

	/**
	 * Gets an unmodifiable list of all of the top-level schemas in the system.
	 */
	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField") // Returning unmodifiable
	final public List<? extends Schema> getSchemas() {
		return schemas;
	}

	/**
	 * Each table has a unique ID, as found in <code>SchemaTable</code>.  The actual
	 * <code>AOServTable</code> may be obtained given its identifier.
	 *
	 * @param  tableID  the unique ID of the table
	 *
	 * @return  the appropriate subclass of <code>AOServTable</code>
	 *
	 * @exception  IllegalArgumentException  if unable to find the table
	 *
	 * @see  Table
	 */
	final public AOServTable<?,?> getTable(int tableID) throws IllegalArgumentException {
		if(tableID>=0 && tableID<tables.size()) return tables.get(tableID);
		throw new IllegalArgumentException("Table not found for ID="+tableID);
	}

	/**
	 * Gets an unmodifiable list of all of the tables in the system.
	 *
	 * @return  a {@code List<AOServTable>} containing all the tables.  Each
	 *          table is at an index corresponding to its unique ID.
	 *
	 * @see  #getTable(int)
	 * @see  Table
	 */
	@SuppressWarnings("ReturnOfCollectionOrArrayField") // Returning unmodifiable
	final public List<? extends AOServTable<?,?>> getTables() {
		return tables;
	}

	/**
	 * Gets the {@link Administrator} who is logged in using
	 * this <code>AOServConnector</code>.  Each username and password pair
	 * resolves to an always-accessible {@link Administrator}.
	 * Details about permissions and capabilities may be obtained from the
	 * {@link Administrator}.
	 *
	 * @return  the {@link Administrator} who is logged in
	 *
	 * @exception  IOException  if unable to communicate with the server
	 * @exception  SQLException  if unable to access the database or the
	 *                           {@link Administrator} was not
	 *                           found
	 */
	final public Administrator getCurrentAdministrator() throws SQLException, IOException {
		Administrator obj = account.getAdministrator().get(connectAs);
		if(obj == null) throw new SQLException("Unable to find Administrator: " + connectAs);
		return obj;
	}

	/**
	 * Manually invalidates the system caches.
	 *
	 * @param tableID the table ID
	 * @param server the pkey of the server or <code>-1</code> for all servers
	 */
	public void invalidateTable(final int tableID, final int server) throws IOException, SQLException {
		requestUpdate(
			true,
			AoservProtocol.CommandID.INVALIDATE_TABLE,
			new UpdateRequest() {
				private IntList tableList;
				@Override
				public void writeRequest(StreamableOutput out) throws IOException {
					out.writeCompressedInt(tableID);
					out.writeCompressedInt(server);
				}
				@Override
				public void readResponse(StreamableInput in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AoservProtocol.DONE) tableList=readInvalidateList(in);
					else {
						AoservProtocol.checkResult(code, in);
						throw new IOException("Unknown response code: "+code);
					}
				}
				@Override
				public void afterRelease() {
					tablesUpdated(tableList);
				}
			}
		);
	}

	public static IntList readInvalidateList(StreamableInput in) throws IOException {
		IntArrayList tableList=null;
		int tableID;
		while((tableID=in.readCompressedInt())!=-1) {
			if(tableList==null) tableList=new IntArrayList();
			tableList.add(tableID);
		}
		return tableList;
	}

	/**
	 * Determines if the connections made by this protocol
	 * are secure.  A connection is considered secure if
	 * it uses end-point to end-point encryption or goes
	 * over private lines.
	 *
	 * @return  <code>true</code> if the connection is secure
	 *
	 * @exception  IOException  if unable to determine if the connection
	 *                          is secure
	 */
	abstract public boolean isSecure() throws IOException;

	/**
	 * Times how long it takes to make one request with the server.
	 * This will not retry and will return the first error encountered.
	 *
	 * @return  the connection latency in milliseconds
	 */
	final public int ping() throws IOException, SQLException {
		long startTime=System.currentTimeMillis();
		requestUpdate(false, AoservProtocol.CommandID.PING);
		long timeSpan=System.currentTimeMillis()-startTime;
		if(timeSpan>Integer.MAX_VALUE) return Integer.MAX_VALUE;
		return (int)timeSpan;
	}

	abstract public void printConnectionStatsHTML(Appendable out, boolean isXhtml) throws IOException;

	/**
	 * Releases a connection to the server.  This will either close the
	 * connection or allow another thread to use the connection.
	 * Connections may be of any protocol, so each connection must be
	 * released after every transaction.
	 *
	 * @param  connection  the connection to release
	 *
	 * @throws  IOException  if an error occurred while closing or releasing the connection
	 *
	 * @see  #getConnection
	 */
	protected abstract void releaseConnection(AOServConnection connection) throws IOException;

	final public void removeFromAllTables(TableListener listener) {
		for(AOServTable<?,?> table : tables) {
			table.removeTableListener(listener);
		}
	}

	static void writeParams(Object[] params, StreamableOutput out) throws IOException {
		for(Object param : params) {
			if(param==null) throw new NullPointerException("param is null");
			else if(param instanceof Integer) out.writeCompressedInt(((Integer)param));
			else if(param instanceof Table.TableID) out.writeCompressedInt(((Table.TableID)param).ordinal());
			// Now passed while getting output stream: else if(param instanceof AOServProtocol.CommandID) out.writeCompressedInt(((AOServProtocol.CommandID)param).ordinal());
			else if(param instanceof String) out.writeUTF((String)param);
			else if(param instanceof Float) out.writeFloat((Float)param);
			else if(param instanceof Long) out.writeLong((Long)param);
			else if(param instanceof Boolean) out.writeBoolean((Boolean)param);
			else if(param instanceof Short) out.writeShort((Short)param);
			else if(param instanceof Byte) out.writeByte((Byte)param);
			else if(param instanceof Timestamp) out.writeTimestamp((Timestamp)param);
			else if(param instanceof Enum) out.writeEnum((Enum)param);
			else if(param instanceof byte[]) {
				byte[] bytes=(byte[])param;
				out.writeCompressedInt(bytes.length);
				out.write(bytes, 0, bytes.length);
			}
			// Self-validating types
			else if(param instanceof Account.Name) out.writeUTF(param.toString());
			else if(param instanceof Email) out.writeUTF(param.toString());
			else if(param instanceof HostAddress) out.writeUTF(param.toString());
			else if(param instanceof InetAddress) out.writeUTF(param.toString());
			else if(param instanceof PosixPath) out.writeUTF(param.toString());
			else if(param instanceof User.Name) out.writeUTF(param.toString());
			else if(param instanceof DomainLabel) out.writeUTF(param.toString());
			else if(param instanceof DomainLabels) out.writeUTF(param.toString());
			else if(param instanceof DomainName) out.writeUTF(param.toString());
			else if(param instanceof Gecos) out.writeUTF(param.toString());
			else if(param instanceof Group.Name) out.writeUTF(param.toString());
			else if(param instanceof HashedPassword) out.writeUTF(param.toString());
			else if(param instanceof LinuxId) out.writeCompressedInt(((LinuxId)param).getId());
			else if(param instanceof com.aoindustries.aoserv.client.linux.User.Name) out.writeUTF(param.toString());
			else if(param instanceof MacAddress) out.writeUTF(param.toString());
			else if(param instanceof com.aoindustries.aoserv.client.mysql.Database.Name) out.writeUTF(param.toString());
			else if(param instanceof com.aoindustries.aoserv.client.mysql.Server.Name) out.writeUTF(param.toString());
			else if(param instanceof com.aoindustries.aoserv.client.mysql.Table_Name) out.writeUTF(param.toString());
			else if(param instanceof com.aoindustries.aoserv.client.mysql.User.Name) out.writeUTF(param.toString());
			else if(param instanceof Port) {
				Port port = (Port)param;
				out.writeCompressedInt(port.getPort());
				out.writeEnum(port.getProtocol());
			}
			else if(param instanceof com.aoindustries.aoserv.client.postgresql.Database.Name) out.writeUTF(param.toString());
			else if(param instanceof com.aoindustries.aoserv.client.postgresql.Server.Name) out.writeUTF(param.toString());
			else if(param instanceof com.aoindustries.aoserv.client.postgresql.User.Name) out.writeUTF(param.toString());
			// Any other Writable
			else if(param instanceof AOServWritable) ((AOServWritable)param).write(out, AoservProtocol.Version.CURRENT_VERSION);
			else if(param instanceof StreamWritable) ((StreamWritable)param).write(out, AoservProtocol.Version.CURRENT_VERSION.getVersion());
			else throw new IOException("Unknown class for param: "+param.getClass().getName());
		}
	}

	/**
	 * This is the preferred mechanism for providing custom requests that have a return value.
	 *
	 * @see  #requestResult(boolean, com.aoindustries.aoserv.client.schema.AoservProtocol.CommandID, com.aoindustries.aoserv.client.AOServConnector.ResultRequest)
	 */
	public interface ResultRequest<T> {
		/**
		 * Writes the request to the server.
		 * This does not need to flush the output stream.
		 */
		void writeRequest(StreamableOutput out) throws IOException;

		/**
		 * Reads the response from the server if the request was successfully sent.
		 */
		void readResponse(StreamableInput in) throws IOException, SQLException;

		/**
		 * If both the request and response were successful, this is called after the
		 * connection to the server is released.  The result is returned here so
		 * any additional processing in packaging the result may be performed
		 * after the connection is released.
		 */
		T afterRelease();
	}

	@SuppressWarnings("SleepWhileInLoop")
	final public <T> T requestResult(
		boolean allowRetry,
		AoservProtocol.CommandID commID,
		ResultRequest<T> resultRequest
	) throws IOException, SQLException {
		int attempt = 1;
		int attempts = allowRetry ? RETRY_ATTEMPTS : 1;
		while(!Thread.interrupted()) {
			try {
				AOServConnection connection=getConnection(1);
				try {
					StreamableOutput out = connection.getRequestOut(commID);
					resultRequest.writeRequest(out);
					out.flush();

					StreamableInput in=connection.getResponseIn();
					resultRequest.readResponse(in);
				} catch(Error | RuntimeException | IOException err) {
					connection.close();
					throw err;
				} finally {
					releaseConnection(connection);
				}
				return resultRequest.afterRelease();
			} catch(InterruptedIOException err) {
				throw err;
			} catch(Error | RuntimeException | IOException | SQLException err) {
				if(Thread.interrupted() || attempt>=attempts || isImmediateFail(err)) throw err;
			}
			try {
				Thread.sleep(retryAttemptDelays[attempt-1]);
			} catch(InterruptedException err) {
				InterruptedIOException ioErr = new InterruptedIOException();
				ioErr.initCause(err);
				throw ioErr;
			}
			attempt++;
		}
		throw new InterruptedIOException();
	}

	@SuppressWarnings("SleepWhileInLoop")
	final public boolean requestBooleanQuery(boolean allowRetry, AoservProtocol.CommandID commID, Object ... params) throws IOException, SQLException {
		int attempt = 1;
		int attempts = allowRetry ? RETRY_ATTEMPTS : 1;
		while(!Thread.interrupted()) {
			try {
				AOServConnection connection=getConnection(1);
				try {
					StreamableOutput out = connection.getRequestOut(commID);
					writeParams(params, out);
					out.flush();

					StreamableInput in=connection.getResponseIn();
					int code=in.readByte();
					if(code==AoservProtocol.DONE) return in.readBoolean();
					AoservProtocol.checkResult(code, in);
					throw new IOException("Unexpected response code: "+code);
				} catch(Error | RuntimeException | IOException err) {
					connection.close();
					throw err;
				} finally {
					releaseConnection(connection);
				}
			} catch(InterruptedIOException err) {
				throw err;
			} catch(Error | RuntimeException | IOException | SQLException err) {
				if(Thread.interrupted() || attempt>=attempts || isImmediateFail(err)) throw err;
			}
			try {
				Thread.sleep(retryAttemptDelays[attempt-1]);
			} catch(InterruptedException err) {
				InterruptedIOException ioErr = new InterruptedIOException();
				ioErr.initCause(err);
				throw ioErr;
			}
			attempt++;
		}
		throw new InterruptedIOException();
	}

	@SuppressWarnings("SleepWhileInLoop")
	final public boolean requestBooleanQueryIL(boolean allowRetry, AoservProtocol.CommandID commID, Object ... params) throws IOException, SQLException {
		int attempt = 1;
		int attempts = allowRetry ? RETRY_ATTEMPTS : 1;
		while(!Thread.interrupted()) {
			try {
				boolean result;
				IntList invalidateList;
				AOServConnection connection=getConnection(1);
				try {
					StreamableOutput out = connection.getRequestOut(commID);
					writeParams(params, out);
					out.flush();

					StreamableInput in=connection.getResponseIn();
					int code=in.readByte();
					if(code==AoservProtocol.DONE) {
						result = in.readBoolean();
						invalidateList=readInvalidateList(in);
					} else {
						AoservProtocol.checkResult(code, in);
						throw new IOException("Unexpected response code: "+code);
					}
				} catch(Error | RuntimeException | IOException err) {
					connection.close();
					throw err;
				} finally {
					releaseConnection(connection);
				}
				tablesUpdated(invalidateList);
				return result;
			} catch(InterruptedIOException err) {
				throw err;
			} catch(Error | RuntimeException | IOException | SQLException err) {
				if(Thread.interrupted() || attempt>=attempts || isImmediateFail(err)) throw err;
			}
			try {
				Thread.sleep(retryAttemptDelays[attempt-1]);
			} catch(InterruptedException err) {
				InterruptedIOException ioErr = new InterruptedIOException();
				ioErr.initCause(err);
				throw ioErr;
			}
			attempt++;
		}
		throw new InterruptedIOException();
	}

	@SuppressWarnings("SleepWhileInLoop")
	final public int requestIntQuery(boolean allowRetry, AoservProtocol.CommandID commID, Object ... params) throws IOException, SQLException {
		int attempt = 1;
		int attempts = allowRetry ? RETRY_ATTEMPTS : 1;
		while(!Thread.interrupted()) {
			try {
				AOServConnection connection=getConnection(1);
				try {
					StreamableOutput out = connection.getRequestOut(commID);
					writeParams(params, out);
					out.flush();

					StreamableInput in=connection.getResponseIn();
					int code=in.readByte();
					if(code==AoservProtocol.DONE) return in.readCompressedInt();
					AoservProtocol.checkResult(code, in);
					throw new IOException("Unexpected response code: "+code);
				} catch(Error | RuntimeException | IOException err) {
					connection.close();
					throw err;
				} finally {
					releaseConnection(connection);
				}
			} catch(InterruptedIOException err) {
				throw err;
			} catch(Error | RuntimeException | IOException | SQLException err) {
				if(Thread.interrupted() || attempt>=attempts || isImmediateFail(err)) throw err;
			}
			try {
				Thread.sleep(retryAttemptDelays[attempt-1]);
			} catch(InterruptedException err) {
				InterruptedIOException ioErr = new InterruptedIOException();
				ioErr.initCause(err);
				throw ioErr;
			}
			attempt++;
		}
		throw new InterruptedIOException();
	}

	@SuppressWarnings("SleepWhileInLoop")
	final public int requestIntQueryIL(boolean allowRetry, AoservProtocol.CommandID commID, Object ... params) throws IOException, SQLException {
		int attempt = 1;
		int attempts = allowRetry ? RETRY_ATTEMPTS : 1;
		while(!Thread.interrupted()) {
			try {
				int result;
				IntList invalidateList;
				AOServConnection connection=getConnection(1);
				try {
					StreamableOutput out = connection.getRequestOut(commID);
					writeParams(params, out);
					out.flush();

					StreamableInput in=connection.getResponseIn();
					int code=in.readByte();
					if(code==AoservProtocol.DONE) {
						result=in.readCompressedInt();
						invalidateList=readInvalidateList(in);
					} else {
						AoservProtocol.checkResult(code, in);
						throw new IOException("Unexpected response code: "+code);
					}
				} catch(Error | RuntimeException | IOException err) {
					connection.close();
					throw err;
				} finally {
					releaseConnection(connection);
				}
				tablesUpdated(invalidateList);
				return result;
			} catch(InterruptedIOException err) {
				throw err;
			} catch(Error | RuntimeException | IOException | SQLException err) {
				if(Thread.interrupted() || attempt>=attempts || isImmediateFail(err)) throw err;
			}
			try {
				Thread.sleep(retryAttemptDelays[attempt-1]);
			} catch(InterruptedException err) {
				InterruptedIOException ioErr = new InterruptedIOException();
				ioErr.initCause(err);
				throw ioErr;
			}
			attempt++;
		}
		throw new InterruptedIOException();
	}

	@SuppressWarnings("SleepWhileInLoop")
	final public long requestLongQuery(boolean allowRetry, AoservProtocol.CommandID commID, Object ... params) throws IOException, SQLException {
		int attempt = 1;
		int attempts = allowRetry ? RETRY_ATTEMPTS : 1;
		while(!Thread.interrupted()) {
			try {
				AOServConnection connection=getConnection(1);
				try {
					StreamableOutput out = connection.getRequestOut(commID);
					writeParams(params, out);
					out.flush();

					StreamableInput in=connection.getResponseIn();
					int code=in.readByte();
					if(code==AoservProtocol.DONE) return in.readLong();
					AoservProtocol.checkResult(code, in);
					throw new IOException("Unexpected response code: "+code);
				} catch(Error | RuntimeException | IOException err) {
					connection.close();
					throw err;
				} finally {
					releaseConnection(connection);
				}
			} catch(InterruptedIOException err) {
				throw err;
			} catch(Error | RuntimeException | IOException | SQLException err) {
				if(Thread.interrupted() || attempt>=attempts || isImmediateFail(err)) throw err;
			}
			try {
				Thread.sleep(retryAttemptDelays[attempt-1]);
			} catch(InterruptedException err) {
				InterruptedIOException ioErr = new InterruptedIOException();
				ioErr.initCause(err);
				throw ioErr;
			}
			attempt++;
		}
		throw new InterruptedIOException();
	}

	@SuppressWarnings("SleepWhileInLoop")
	final public short requestShortQuery(boolean allowRetry, AoservProtocol.CommandID commID, Object ... params) throws IOException, SQLException {
		int attempt = 1;
		int attempts = allowRetry ? RETRY_ATTEMPTS : 1;
		while(!Thread.interrupted()) {
			try {
				AOServConnection connection=getConnection(1);
				try {
					StreamableOutput out = connection.getRequestOut(commID);
					writeParams(params, out);
					out.flush();

					StreamableInput in=connection.getResponseIn();
					int code=in.readByte();
					if(code==AoservProtocol.DONE) return in.readShort();
					AoservProtocol.checkResult(code, in);
					throw new IOException("Unexpected response code: "+code);
				} catch(Error | RuntimeException | IOException err) {
					connection.close();
					throw err;
				} finally {
					releaseConnection(connection);
				}
			} catch(InterruptedIOException err) {
				throw err;
			} catch(Error | RuntimeException | IOException | SQLException err) {
				if(Thread.interrupted() || attempt>=attempts || isImmediateFail(err)) throw err;
			}
			try {
				Thread.sleep(retryAttemptDelays[attempt-1]);
			} catch(InterruptedException err) {
				InterruptedIOException ioErr = new InterruptedIOException();
				ioErr.initCause(err);
				throw ioErr;
			}
			attempt++;
		}
		throw new InterruptedIOException();
	}

	@SuppressWarnings("SleepWhileInLoop")
	final public short requestShortQueryIL(boolean allowRetry, AoservProtocol.CommandID commID, Object ... params) throws IOException, SQLException {
		int attempt = 1;
		int attempts = allowRetry ? RETRY_ATTEMPTS : 1;
		while(!Thread.interrupted()) {
			try {
				short result;
				IntList invalidateList;
				AOServConnection connection=getConnection(1);
				try {
					StreamableOutput out = connection.getRequestOut(commID);
					writeParams(params, out);
					out.flush();

					StreamableInput in=connection.getResponseIn();
					int code=in.readByte();
					if(code==AoservProtocol.DONE) {
						result=in.readShort();
						invalidateList=readInvalidateList(in);
					} else {
						AoservProtocol.checkResult(code, in);
						throw new IOException("Unexpected response code: "+code);
					}
				} catch(Error | RuntimeException | IOException err) {
					connection.close();
					throw err;
				} finally {
					releaseConnection(connection);
				}
				tablesUpdated(invalidateList);
				return result;
			} catch(InterruptedIOException err) {
				throw err;
			} catch(Error | RuntimeException | IOException | SQLException err) {
				if(Thread.interrupted() || attempt>=attempts || isImmediateFail(err)) throw err;
			}
			try {
				Thread.sleep(retryAttemptDelays[attempt-1]);
			} catch(InterruptedException err) {
				InterruptedIOException ioErr = new InterruptedIOException();
				ioErr.initCause(err);
				throw ioErr;
			}
			attempt++;
		}
		throw new InterruptedIOException();
	}

	@SuppressWarnings("SleepWhileInLoop")
	final public String requestStringQuery(boolean allowRetry, AoservProtocol.CommandID commID, Object ... params) throws IOException, SQLException {
		int attempt = 1;
		int attempts = allowRetry ? RETRY_ATTEMPTS : 1;
		while(!Thread.interrupted()) {
			try {
				AOServConnection connection=getConnection(1);
				try {
					StreamableOutput out = connection.getRequestOut(commID);
					writeParams(params, out);
					out.flush();

					StreamableInput in=connection.getResponseIn();
					int code=in.readByte();
					if(code==AoservProtocol.DONE) return in.readUTF();
					AoservProtocol.checkResult(code, in);
					throw new IOException("Unexpected response code: "+code);
				} catch(Error | RuntimeException | IOException err) {
					connection.close();
					throw err;
				} finally {
					releaseConnection(connection);
				}
			} catch(InterruptedIOException err) {
				throw err;
			} catch(Error | RuntimeException | IOException | SQLException err) {
				if(Thread.interrupted() || attempt>=attempts || isImmediateFail(err)) throw err;
			}
			try {
				Thread.sleep(retryAttemptDelays[attempt-1]);
			} catch(InterruptedException err) {
				InterruptedIOException ioErr = new InterruptedIOException();
				ioErr.initCause(err);
				throw ioErr;
			}
			attempt++;
		}
		throw new InterruptedIOException();
	}

	/**
	 * Performs a query returning a String of any length (not limited to size &lt;= 64k like requestStringQuery).
	 */
	@SuppressWarnings("SleepWhileInLoop")
	final public String requestLongStringQuery(boolean allowRetry, AoservProtocol.CommandID commID, Object ... params) throws IOException, SQLException {
		int attempt = 1;
		int attempts = allowRetry ? RETRY_ATTEMPTS : 1;
		while(!Thread.interrupted()) {
			try {
				AOServConnection connection=getConnection(1);
				try {
					StreamableOutput out = connection.getRequestOut(commID);
					writeParams(params, out);
					out.flush();

					StreamableInput in=connection.getResponseIn();
					int code=in.readByte();
					if(code==AoservProtocol.DONE) return in.readLongUTF();
					AoservProtocol.checkResult(code, in);
					throw new IOException("Unexpected response code: "+code);
				} catch(Error | RuntimeException | IOException err) {
					connection.close();
					throw err;
				} finally {
					releaseConnection(connection);
				}
			} catch(InterruptedIOException err) {
				throw err;
			} catch(Error | RuntimeException | IOException | SQLException err) {
				if(Thread.interrupted() || attempt>=attempts || isImmediateFail(err)) throw err;
			}
			try {
				Thread.sleep(retryAttemptDelays[attempt-1]);
			} catch(InterruptedException err) {
				InterruptedIOException ioErr = new InterruptedIOException();
				ioErr.initCause(err);
				throw ioErr;
			}
			attempt++;
		}
		throw new InterruptedIOException();
	}

	/**
	 * Performs a query returning a String of any length (not limited to size &lt;= 64k like requestStringQuery) or {@code null}.
	 * Supports nulls.
	 */
	@SuppressWarnings("SleepWhileInLoop")
	final public String requestNullLongStringQuery(boolean allowRetry, AoservProtocol.CommandID commID, Object ... params) throws IOException, SQLException {
		int attempt = 1;
		int attempts = allowRetry ? RETRY_ATTEMPTS : 1;
		while(!Thread.interrupted()) {
			try {
				AOServConnection connection=getConnection(1);
				try {
					StreamableOutput out = connection.getRequestOut(commID);
					writeParams(params, out);
					out.flush();

					StreamableInput in=connection.getResponseIn();
					int code=in.readByte();
					if(code==AoservProtocol.DONE) return in.readNullLongUTF();
					AoservProtocol.checkResult(code, in);
					throw new IOException("Unexpected response code: "+code);
				} catch(Error | RuntimeException | IOException err) {
					connection.close();
					throw err;
				} finally {
					releaseConnection(connection);
				}
			} catch(InterruptedIOException err) {
				throw err;
			} catch(Error | RuntimeException | IOException | SQLException err) {
				if(Thread.interrupted() || attempt>=attempts || isImmediateFail(err)) throw err;
			}
			try {
				Thread.sleep(retryAttemptDelays[attempt-1]);
			} catch(InterruptedException err) {
				InterruptedIOException ioErr = new InterruptedIOException();
				ioErr.initCause(err);
				throw ioErr;
			}
			attempt++;
		}
		throw new InterruptedIOException();
	}

	/**
	 * This is the preferred mechanism for providing custom requests.
	 *
	 * @see  #requestUpdate(boolean, com.aoindustries.aoserv.client.schema.AoservProtocol.CommandID, com.aoindustries.aoserv.client.AOServConnector.UpdateRequest)
	 */
	public interface UpdateRequest {
		/**
		 * Writes the request to the server.
		 * This does not need to flush the output stream.
		 */
		void writeRequest(StreamableOutput out) throws IOException;

		/**
		 * Reads the response from the server if the request was successfully sent.
		 */
		void readResponse(StreamableInput in) throws IOException, SQLException;

		/**
		 * If both the request and response were successful, this is called after the
		 * connection to the server is released.
		 */
		void afterRelease();
	}

	@SuppressWarnings("SleepWhileInLoop")
	final public void requestUpdate(
		boolean allowRetry,
		AoservProtocol.CommandID commID,
		UpdateRequest updateRequest
	) throws IOException, SQLException {
		int attempt = 1;
		int attempts = allowRetry ? RETRY_ATTEMPTS : 1;
		while(!Thread.interrupted()) {
			try {
				AOServConnection connection=getConnection(1);
				try {
					StreamableOutput out = connection.getRequestOut(commID);
					updateRequest.writeRequest(out);
					out.flush();

					StreamableInput in=connection.getResponseIn();
					updateRequest.readResponse(in);
				} catch(Error | RuntimeException | IOException err) {
					connection.close();
					throw err;
				} finally {
					releaseConnection(connection);
				}
				updateRequest.afterRelease();
				return;
			} catch(InterruptedIOException err) {
				throw err;
			} catch(Error | RuntimeException | IOException | SQLException err) {
				if(Thread.interrupted() || attempt>=attempts || isImmediateFail(err)) throw err;
			}
			try {
				Thread.sleep(retryAttemptDelays[attempt-1]);
			} catch(InterruptedException err) {
				InterruptedIOException ioErr = new InterruptedIOException();
				ioErr.initCause(err);
				throw ioErr;
			}
			attempt++;
		}
		throw new InterruptedIOException();
	}

	@SuppressWarnings("SleepWhileInLoop")
	final public void requestUpdate(boolean allowRetry, AoservProtocol.CommandID commID, Object ... params) throws IOException, SQLException {
		int attempt = 1;
		int attempts = allowRetry ? RETRY_ATTEMPTS : 1;
		while(!Thread.interrupted()) {
			try {
				AOServConnection connection=getConnection(1);
				try {
					StreamableOutput out = connection.getRequestOut(commID);
					writeParams(params, out);
					out.flush();

					StreamableInput in=connection.getResponseIn();
					int code=in.readByte();
					if(code!=AoservProtocol.DONE) AoservProtocol.checkResult(code, in);
				} catch(Error | RuntimeException | IOException err) {
					connection.close();
					throw err;
				} finally {
					releaseConnection(connection);
				}
				return;
			} catch(InterruptedIOException err) {
				throw err;
			} catch(Error | RuntimeException | IOException | SQLException err) {
				if(Thread.interrupted() || attempt>=attempts || isImmediateFail(err)) throw err;
			}
			try {
				Thread.sleep(retryAttemptDelays[attempt-1]);
			} catch(InterruptedException err) {
				InterruptedIOException ioErr = new InterruptedIOException();
				ioErr.initCause(err);
				throw ioErr;
			}
			attempt++;
		}
		throw new InterruptedIOException();
	}

	@SuppressWarnings("SleepWhileInLoop")
	final public void requestUpdateIL(boolean allowRetry, AoservProtocol.CommandID commID, Object ... params) throws IOException, SQLException {
		int attempt = 1;
		int attempts = allowRetry ? RETRY_ATTEMPTS : 1;
		while(!Thread.interrupted()) {
			try {
				IntList invalidateList;
				AOServConnection connection=getConnection(1);
				try {
					StreamableOutput out = connection.getRequestOut(commID);
					writeParams(params, out);
					out.flush();

					StreamableInput in=connection.getResponseIn();
					int code=in.readByte();
					if(code==AoservProtocol.DONE) invalidateList=readInvalidateList(in);
					else {
						AoservProtocol.checkResult(code, in);
						throw new IOException("Unexpected response code: "+code);
					}
				} catch(Error | RuntimeException | IOException err) {
					connection.close();
					throw err;
				} finally {
					releaseConnection(connection);
				}
				tablesUpdated(invalidateList);
				return;
			} catch(InterruptedIOException err) {
				throw err;
			} catch(Error | RuntimeException | IOException | SQLException err) {
				if(Thread.interrupted() || attempt>=attempts || isImmediateFail(err)) throw err;
			}
			try {
				Thread.sleep(retryAttemptDelays[attempt-1]);
			} catch(InterruptedException err) {
				InterruptedIOException ioErr = new InterruptedIOException();
				ioErr.initCause(err);
				throw ioErr;
			}
			attempt++;
		}
		throw new InterruptedIOException();
	}

	public abstract AOServConnector switchUsers(User.Name username) throws IOException;

	final public void tablesUpdated(IntList invalidateList) {
		if(invalidateList!=null) {
			int size=invalidateList.size();

			// Clear the caches
			for(int c=0;c<size;c++) {
				int tableID=invalidateList.getInt(c);
				tables.get(tableID).clearCache();
			}

			// Then send the events
			for(int c=0;c<size;c++) {
				int tableID=invalidateList.getInt(c);
				//System.err.println("DEBUG: AOServConnector: tablesUpdated: "+tableID+": "+SchemaTable.TableID.values()[tableID]);
				tables.get(tableID).tableUpdated();
			}
		}
	}

	/**
	 * Tests the connectivity to the server.  This test is only
	 * performed once per server per protocol.  Following that,
	 * the cached results are used.
	 *
	 * @exception  IOException  if unable to contact the server
	 */
	final public void testConnect() throws IOException, SQLException {
		synchronized(testConnectLock) {
			requestUpdate(
				true,
				AoservProtocol.CommandID.TEST_CONNECTION,
				new UpdateRequest() {
					@Override
					public void writeRequest(StreamableOutput out) {
					}
					@Override
					public void readResponse(StreamableInput in) throws IOException, SQLException {
						int code=in.readByte();
						if(code!=AoservProtocol.DONE) {
							AoservProtocol.checkResult(code, in);
							throw new IOException("Unexpected response code: "+code);
						}
					}
					@Override
					public void afterRelease() {
					}
				}
			);
		}
	}

	@Override
	final public String toString() {
		return getClass().getName()+"?protocol="+getProtocol()+"&hostname="+hostname+"&local_ip="+local_ip+"&port="+port+"&connectAs="+connectAs+"&authenticateAs="+authenticateAs;
	}

	/**
	 * Is notified when a table listener is being added.
	 */
	@SuppressWarnings("NoopMethodInAbstractClass")
	void addingTableListener() {
	}

	/**
	 * Gets some entropy from the master server, returns the number of bytes actually obtained.
	 */
	public int getMasterEntropy(final byte[] buff, final int numBytes) throws IOException, SQLException {
		return requestResult(
			true,
			AoservProtocol.CommandID.GET_MASTER_ENTROPY,
			new ResultRequest<Integer>() {
				private int numObtained;

				@Override
				public void writeRequest(StreamableOutput out) throws IOException {
					out.writeCompressedInt(numBytes);
				}

				@Override
				public void readResponse(StreamableInput in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AoservProtocol.DONE) {
						numObtained=in.readCompressedInt();
						for(int c = 0; c < numObtained; c++) {
							buff[c] = in.readByte();
						}
					} else {
						AoservProtocol.checkResult(code, in);
						throw new IOException("Unexpected response code: "+code);
					}
				}

				@Override
				public Integer afterRelease() {
					return numObtained;
				}
			}
		);
	}

	/**
	 * Gets the amount of entropy needed by the master server in bytes.
	 */
	public long getMasterEntropyNeeded() throws IOException, SQLException {
		return requestLongQuery(true, AoservProtocol.CommandID.GET_MASTER_ENTROPY_NEEDED);
	}

	/**
	 * Adds some entropy to the master server.
	 */
	public long addMasterEntropy(final byte[] buff, final int numBytes) throws IOException, SQLException {
		return requestResult(
			true,
			AoservProtocol.CommandID.ADD_MASTER_ENTROPY,
			new ResultRequest<Long>() {
				private long entropyNeeded;
				@Override
				public void writeRequest(StreamableOutput out) throws IOException {
					out.writeCompressedInt(numBytes);
					out.write(buff, 0, numBytes);
				}
				@Override
				public void readResponse(StreamableInput in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AoservProtocol.DONE) {
						entropyNeeded = in.readLong();
					} else {
						AoservProtocol.checkResult(code, in);
						throw new IOException("Unexpected response code: "+code);
					}
				}
				@Override
				public Long afterRelease() {
					return entropyNeeded;
				}
			}
		);
	}

	public <K,T extends AOServObject<K,T>> void sort(
		ComparisonSortAlgorithm<? super T> sortAlgorithm,
		T[] list,
		SQLExpression[] sortExpressions,
		boolean[] sortOrders
	) {
		sortAlgorithm.sort(
			list,
			new SQLComparator<>(
				this,
				sortExpressions,
				sortOrders
			)
		);
	}

	public <K,T extends AOServObject<K,T>> void sort(
		ComparisonSortAlgorithm<? super T> sortAlgorithm,
		List<T> list,
		SQLExpression[] sortExpressions,
		boolean[] sortOrders
	) {
		sortAlgorithm.sort(
			list,
			new SQLComparator<>(
				this,
				sortExpressions,
				sortOrders
			)
		);
	}
}
