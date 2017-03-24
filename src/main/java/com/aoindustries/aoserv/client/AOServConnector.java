/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2001-2013, 2015, 2016, 2017  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.validator.Gecos;
import com.aoindustries.aoserv.client.validator.GroupId;
import com.aoindustries.aoserv.client.validator.HashedPassword;
import com.aoindustries.aoserv.client.validator.LinuxId;
import com.aoindustries.aoserv.client.validator.MySQLDatabaseName;
import com.aoindustries.aoserv.client.validator.MySQLServerName;
import com.aoindustries.aoserv.client.validator.MySQLTableName;
import com.aoindustries.aoserv.client.validator.MySQLUserId;
import com.aoindustries.aoserv.client.validator.PostgresDatabaseName;
import com.aoindustries.aoserv.client.validator.PostgresServerName;
import com.aoindustries.aoserv.client.validator.PostgresUserId;
import com.aoindustries.aoserv.client.validator.UnixPath;
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.io.Streamable;
import com.aoindustries.net.DomainLabel;
import com.aoindustries.net.DomainLabels;
import com.aoindustries.net.DomainName;
import com.aoindustries.net.Email;
import com.aoindustries.net.HostAddress;
import com.aoindustries.net.InetAddress;
import com.aoindustries.net.MacAddress;
import com.aoindustries.net.Port;
import com.aoindustries.table.TableListener;
import com.aoindustries.util.IntArrayList;
import com.aoindustries.util.IntList;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
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
abstract public class AOServConnector {

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
		23,
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
		String message = T.getMessage();
		return
			(
				(T instanceof IOException)
				&& message!=null
				&& (
					message.equals("Connection attempted with invalid password")
					|| message.equals("Connection attempted with empty password")
					|| message.equals("Connection attempted with empty connect username")
					|| message.startsWith("Unable to find BusinessAdministrator: ")
					|| message.startsWith("Not allowed to switch users from ")
				)
			)
		;
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
	 * @see  #getConnectorID()
	 */
	private static class IdLock {}
	final IdLock idLock = new IdLock();
	long id=-1; // Rename back to id

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
	final UserId connectAs;

	/**
	 * @see  #getAuthenticatedAs()
	 */
	final UserId authenticateAs;

	final DomainName daemonServer;

	final Logger logger;

	/**
	 * Gets the logger for this connector.
	 */
	//public Logger getLogger() {
	//    return logger;
	//}

	protected final String password;

	private static class TestConnectLock {}
	private final TestConnectLock testConnectLock=new TestConnectLock();

	private final AOServerDaemonHostTable aoServerDaemonHosts;
	public AOServerDaemonHostTable getAoServerDaemonHosts() {return aoServerDaemonHosts;}

	private final AOServerTable aoServers;
	public AOServerTable getAoServers() {return aoServers;}

	private final AOServPermissionTable aoservPermissions;
	public AOServPermissionTable getAoservPermissions() {return aoservPermissions;}

	private final AOServProtocolTable aoservProtocols;
	public AOServProtocolTable getAoservProtocols() {
		return aoservProtocols;
	}

	private final AOSHCommandTable aoshCommands;
	public AOSHCommandTable getAoshCommands() {
		return aoshCommands;
	}

	private final ArchitectureTable architectures;
	public ArchitectureTable getArchitectures() {
		return architectures;
	}

	private final BackupPartitionTable backupPartitions;
	public BackupPartitionTable getBackupPartitions() {
		return backupPartitions;
	}

	private final BackupReportTable backupReports;
	public BackupReportTable getBackupReports() {
		return backupReports;
	}

	private final BackupRetentionTable backupRetentions;
	public BackupRetentionTable getBackupRetentions() {
		return backupRetentions;
	}

	private final BankAccountTable bankAccounts;
	public BankAccountTable getBankAccounts() {
		return bankAccounts;
	}

	private final BankTransactionTypeTable bankTransactionTypes;
	public BankTransactionTypeTable getBankTransactionTypes() {
		return bankTransactionTypes;
	}

	private final BankTransactionTable bankTransactions;
	public BankTransactionTable getBankTransactions() {
		return bankTransactions;
	}

	private final BankTable banks;
	public BankTable getBanks() {
		return banks;
	}

	private final BlackholeEmailAddressTable blackholeEmailAddresses;
	public BlackholeEmailAddressTable getBlackholeEmailAddresses() {
		return blackholeEmailAddresses;
	}

	private final BrandTable brands;
	public BrandTable getBrands() {
		return brands;
	}

	private final BusinessAdministratorTable businessAdministrators;
	public BusinessAdministratorTable getBusinessAdministrators() {
		return businessAdministrators;
	}

	private final BusinessAdministratorPermissionTable businessAdministratorPermissions;
	public BusinessAdministratorPermissionTable getBusinessAdministratorPermissions() {return businessAdministratorPermissions;}

	private final BusinessProfileTable businessProfiles;
	public BusinessProfileTable getBusinessProfiles() {
		return businessProfiles;
	}

	private final BusinessTable businesses;
	public BusinessTable getBusinesses() {
		return businesses;
	}

	private final BusinessServerTable businessServers;
	public BusinessServerTable getBusinessServers() {
		return businessServers;
	}

	private final CountryCodeTable countryCodes;
	public CountryCodeTable getCountryCodes() {
		return countryCodes;
	}

	private final CreditCardProcessorTable creditCardProcessors;
	public CreditCardProcessorTable getCreditCardProcessors() {return creditCardProcessors;}

	private final CreditCardTransactionTable creditCardTransactions;
	public CreditCardTransactionTable getCreditCardTransactions() {return creditCardTransactions;}

	private final CreditCardTable creditCards;
	public CreditCardTable getCreditCards() {return creditCards;}

	private final CvsRepositoryTable cvsRepositories;
	public CvsRepositoryTable getCvsRepositories() {
		return cvsRepositories;
	}

	private final DisableLogTable disableLogs;
	public DisableLogTable getDisableLogs() {
		return disableLogs;
	}

	private final DistroFileTypeTable distroFileTypes;
	public DistroFileTypeTable getDistroFileTypes() {
		return distroFileTypes;
	}

	private final DistroFileTable distroFiles;
	public DistroFileTable getDistroFiles() {
		return distroFiles;
	}

	private final DistroReportTypeTable distroReportTypes;
	public DistroReportTypeTable getDistroReportTypes() {
		return distroReportTypes;
	}

	private final DNSForbiddenZoneTable dnsForbiddenZones;
	public DNSForbiddenZoneTable getDnsForbiddenZones() {
		return dnsForbiddenZones;
	}

	private final DNSRecordTable dnsRecords;
	public DNSRecordTable getDnsRecords() {
		return dnsRecords;
	}

	private final DNSTLDTable dnsTLDs;
	public DNSTLDTable getDnsTLDs() {
		return dnsTLDs;
	}

	private final DNSTypeTable dnsTypes;
	public DNSTypeTable getDnsTypes() {
		return dnsTypes;
	}

	private final DNSZoneTable dnsZones;
	public DNSZoneTable getDnsZones() {
		return dnsZones;
	}

	private final EmailAddressTable emailAddresses;
	public EmailAddressTable getEmailAddresses() {
		return emailAddresses;
	}

	private final EmailAttachmentBlockTable emailAttachmentBlocks;
	public EmailAttachmentBlockTable getEmailAttachmentBlocks() {
		return emailAttachmentBlocks;
	}

	private final EmailAttachmentTypeTable emailAttachmentTypes;
	public EmailAttachmentTypeTable getEmailAttachmentTypes() {
		return emailAttachmentTypes;
	}

	private final EmailDomainTable emailDomains;
	public EmailDomainTable getEmailDomains() {
		return emailDomains;
	}

	private final EmailForwardingTable emailForwardings;
	public EmailForwardingTable getEmailForwardings() {
		return emailForwardings;
	}

	private final EmailListAddressTable emailListAddresses;
	public EmailListAddressTable getEmailListAddresses() {
		return emailListAddresses;
	}

	private final EmailListTable emailLists;
	public EmailListTable getEmailLists() {
		return emailLists;
	}

	private final EmailPipeAddressTable emailPipeAddresses;
	public EmailPipeAddressTable getEmailPipeAddresses() {
		return emailPipeAddresses;
	}

	private final EmailPipeTable emailPipes;
	public EmailPipeTable getEmailPipes() {
		return emailPipes;
	}

	private final EmailSmtpRelayTypeTable emailSmtpRelayTypes;
	public EmailSmtpRelayTypeTable getEmailSmtpRelayTypes() {
		return emailSmtpRelayTypes;
	}

	private final EmailSmtpRelayTable emailSmtpRelays;
	public EmailSmtpRelayTable getEmailSmtpRelays() {
		return emailSmtpRelays;
	}

	private final EmailSmtpSmartHostDomainTable emailSmtpSmartHostDomainTable;
	public EmailSmtpSmartHostDomainTable getEmailSmtpSmartHostDomains() {
		return emailSmtpSmartHostDomainTable;
	}

	private final EmailSmtpSmartHostTable emailSmtpSmartHostTable;
	public EmailSmtpSmartHostTable getEmailSmtpSmartHosts() {
		return emailSmtpSmartHostTable;
	}

	private final EmailSpamAssassinIntegrationModeTable emailSpamAssassinIntegrationModes;
	public EmailSpamAssassinIntegrationModeTable getEmailSpamAssassinIntegrationModes() {
		return emailSpamAssassinIntegrationModes;
	}

	private final EncryptionKeyTable encryptionKeys;
	public EncryptionKeyTable getEncryptionKeys() {
		return encryptionKeys;
	}

	private final ExpenseCategoryTable expenseCategories;
	public ExpenseCategoryTable getExpenseCategories() {
		return expenseCategories;
	}

	private final FailoverFileLogTable failoverFileLogs;
	public FailoverFileLogTable getFailoverFileLogs() {
		return failoverFileLogs;
	}

	private final FailoverFileReplicationTable failoverFileReplications;
	public FailoverFileReplicationTable getFailoverFileReplications() {
		return failoverFileReplications;
	}

	private final FailoverFileScheduleTable failoverFileSchedules;
	public FailoverFileScheduleTable getFailoverFileSchedules() {
		return failoverFileSchedules;
	}

	private final FailoverMySQLReplicationTable failoverMySQLReplications;
	public FailoverMySQLReplicationTable getFailoverMySQLReplications() {return failoverMySQLReplications;}

	private final FileBackupSettingTable fileBackupSettings;
	public FileBackupSettingTable getFileBackupSettings() {
		return fileBackupSettings;
	}

	private final FTPGuestUserTable ftpGuestUsers;
	public FTPGuestUserTable getFtpGuestUsers() {
		return ftpGuestUsers;
	}

	private final HttpdBindTable httpdBinds;
	public HttpdBindTable getHttpdBinds() {
		return httpdBinds;
	}

	private final HttpdJBossSiteTable httpdJBossSites;
	public HttpdJBossSiteTable getHttpdJBossSites() {
		return httpdJBossSites;
	}

	private final HttpdJBossVersionTable httpdJBossVersions;
	public HttpdJBossVersionTable getHttpdJBossVersions() {
		return httpdJBossVersions;
	}

	private final HttpdJKCodeTable httpdJKCodes;
	public HttpdJKCodeTable getHttpdJKCodes() {
		return httpdJKCodes;
	}

	private final HttpdJKProtocolTable httpdJKProtocols;
	public HttpdJKProtocolTable getHttpdJKProtocols() {
		return httpdJKProtocols;
	}

	private final HttpdServerTable httpdServers;
	public HttpdServerTable getHttpdServers() {
		return httpdServers;
	}

	private final HttpdSharedTomcatTable httpdSharedTomcats;
	public HttpdSharedTomcatTable getHttpdSharedTomcats() {
		return httpdSharedTomcats;
	}

	private final HttpdSiteAuthenticatedLocationTable httpdSiteAuthenticatedLocationTable;
	public HttpdSiteAuthenticatedLocationTable getHttpdSiteAuthenticatedLocationTable() {
		return httpdSiteAuthenticatedLocationTable;
	}

	private final HttpdSiteBindTable httpdSiteBinds;
	public HttpdSiteBindTable getHttpdSiteBinds() {
		return httpdSiteBinds;
	}

	private final HttpdSiteURLTable httpdSiteURLs;
	public HttpdSiteURLTable getHttpdSiteURLs() {
		return httpdSiteURLs;
	}

	private final HttpdSiteTable httpdSites;
	public HttpdSiteTable getHttpdSites() {return httpdSites;}

	private final HttpdStaticSiteTable httpdStaticSites;
	public HttpdStaticSiteTable getHttpdStaticSites() {
		return httpdStaticSites;
	}

	private final HttpdTomcatContextTable httpdTomcatContexts;
	public HttpdTomcatContextTable getHttpdTomcatContexts() {
		return httpdTomcatContexts;
	}

	private final HttpdTomcatDataSourceTable httpdTomcatDataSources;
	public HttpdTomcatDataSourceTable getHttpdTomcatDataSources() {
		return httpdTomcatDataSources;
	}

	private final HttpdTomcatParameterTable httpdTomcatParameters;
	public HttpdTomcatParameterTable getHttpdTomcatParameters() {
		return httpdTomcatParameters;
	}

	private final HttpdTomcatSiteTable httpdTomcatSites;
	public HttpdTomcatSiteTable getHttpdTomcatSites() {
		return httpdTomcatSites;
	}

	private final HttpdTomcatSharedSiteTable httpdTomcatSharedSites;
	public HttpdTomcatSharedSiteTable getHttpdTomcatSharedSites() {
		return httpdTomcatSharedSites;
	}

	private final HttpdTomcatStdSiteTable httpdTomcatStdSites;
	public HttpdTomcatStdSiteTable getHttpdTomcatStdSites() {
		return httpdTomcatStdSites;
	}

	private final HttpdTomcatVersionTable httpdTomcatVersions;
	public HttpdTomcatVersionTable getHttpdTomcatVersions() {
		return httpdTomcatVersions;
	}

	private final HttpdWorkerTable httpdWorkers;
	public HttpdWorkerTable getHttpdWorkers() {
		return httpdWorkers;
	}

	private final IPAddressTable ipAddresses;
	public IPAddressTable getIpAddresses() {
		return ipAddresses;
	}

	private final IpReputationLimiterLimitTable ipReputationLimiterLimits;
	public IpReputationLimiterLimitTable getIpReputationLimiterLimits() {
		return ipReputationLimiterLimits;
	}

	private final IpReputationLimiterSetTable ipReputationLimiterSets;
	public IpReputationLimiterSetTable getIpReputationLimiterSets() {
		return ipReputationLimiterSets;
	}

	private final IpReputationLimiterTable ipReputationLimiters;
	public IpReputationLimiterTable getIpReputationLimiters() {
		return ipReputationLimiters;
	}

	private final IpReputationSetHostTable ipReputationSetHosts;
	public IpReputationSetHostTable getIpReputationSetHosts() {
		return ipReputationSetHosts;
	}

	private final IpReputationSetNetworkTable ipReputationSetNetworks;
	public IpReputationSetNetworkTable getIpReputationSetNetworks() {
		return ipReputationSetNetworks;
	}

	private final IpReputationSetTable ipReputationSets;
	public IpReputationSetTable getIpReputationSets() {
		return ipReputationSets;
	}

	private final LanguageTable languages;
	public LanguageTable getLanguages() {
		return languages;
	}

	private final LinuxAccAddressTable linuxAccAddresses;
	public LinuxAccAddressTable getLinuxAccAddresses() {
		return linuxAccAddresses;
	}

	private final LinuxAccountTypeTable linuxAccountTypes;
	public LinuxAccountTypeTable getLinuxAccountTypes() {
		return linuxAccountTypes;
	}

	private final LinuxAccountTable linuxAccounts;
	public LinuxAccountTable getLinuxAccounts() {
		return linuxAccounts;
	}

	private final LinuxGroupAccountTable linuxGroupAccounts;
	public LinuxGroupAccountTable getLinuxGroupAccounts() {
		return linuxGroupAccounts;
	}

	private final LinuxGroupTypeTable linuxGroupTypes;
	public LinuxGroupTypeTable getLinuxGroupTypes() {
		return linuxGroupTypes;
	}

	private final LinuxGroupTable linuxGroups;
	public LinuxGroupTable getLinuxGroups() {
		return linuxGroups;
	}

	private final LinuxServerAccountTable linuxServerAccounts;
	public LinuxServerAccountTable getLinuxServerAccounts() {
		return linuxServerAccounts;
	}

	private final LinuxServerGroupTable linuxServerGroups;
	public LinuxServerGroupTable getLinuxServerGroups() {
		return linuxServerGroups;
	}

	private final MajordomoListTable majordomoLists;
	public MajordomoListTable getMajordomoLists() {
		return majordomoLists;
	}

	private final MajordomoServerTable majordomoServers;
	public MajordomoServerTable getMajordomoServers() {
		return majordomoServers;
	}

	private final MajordomoVersionTable majordomoVersions;
	public MajordomoVersionTable getMajordomoVersions() {
		return majordomoVersions;
	}

	private final MasterHostTable masterHosts;
	public MasterHostTable getMasterHosts() {
		return masterHosts;
	}

	private final MasterProcessTable masterProcesses;
	public MasterProcessTable getMasterProcesses() {
		return masterProcesses;
	}

	private final MasterServerStatTable masterServerStats;
	public MasterServerStatTable getMasterServerStats() {
		return masterServerStats;
	}

	private final MasterServerTable masterServers;
	public MasterServerTable getMasterServers() {
		return masterServers;
	}

	private final MasterUserTable masterUsers;
	public MasterUserTable getMasterUsers() {
		return masterUsers;
	}

	private final MonthlyChargeTable monthlyCharges;
	public MonthlyChargeTable getMonthlyCharges() {
		return monthlyCharges;
	}

	private final MySQLDatabaseTable mysqlDatabases;
	public MySQLDatabaseTable getMysqlDatabases() {
		return mysqlDatabases;
	}

	private final MySQLDBUserTable mysqlDBUsers;
	public MySQLDBUserTable getMysqlDBUsers() {
		return mysqlDBUsers;
	}

	private final MySQLServerUserTable mysqlServerUsers;
	public MySQLServerUserTable getMysqlServerUsers() {
		return mysqlServerUsers;
	}

	private final MySQLServerTable mysqlServers;
	public MySQLServerTable getMysqlServers() {return mysqlServers;}

	private final MySQLUserTable mysqlUsers;
	public MySQLUserTable getMysqlUsers() {
		return mysqlUsers;
	}

	private final NetBindTable netBinds;
	public NetBindTable getNetBinds() {
		return netBinds;
	}

	private final NetDeviceIDTable netDeviceIDs;
	public NetDeviceIDTable getNetDeviceIDs() {
		return netDeviceIDs;
	}

	private final NetDeviceTable netDevices;
	public NetDeviceTable getNetDevices() {
		return netDevices;
	}

	private final NetTcpRedirectTable netTcpRedirects;
	public NetTcpRedirectTable getNetTcpRedirects() {
		return netTcpRedirects;
	}

	private final NoticeLogTable noticeLogs;
	public NoticeLogTable getNoticeLogs() {
		return noticeLogs;
	}

	private final NoticeTypeTable noticeTypes;
	public NoticeTypeTable getNoticeTypes() {
		return noticeTypes;
	}

	private final OperatingSystemVersionTable operatingSystemVersions;
	public OperatingSystemVersionTable getOperatingSystemVersions() {
		return operatingSystemVersions;
	}

	private final OperatingSystemTable operatingSystems;
	public OperatingSystemTable getOperatingSystems() {
		return operatingSystems;
	}

	private final PackageCategoryTable packageCategories;
	public PackageCategoryTable getPackageCategories() {
		return packageCategories;
	}

	private final PackageDefinitionLimitTable packageDefinitionLimits;
	public PackageDefinitionLimitTable getPackageDefinitionLimits() {
		return packageDefinitionLimits;
	}

	private final PackageDefinitionTable packageDefinitions;
	public PackageDefinitionTable getPackageDefinitions() {
		return packageDefinitions;
	}

	private final PackageTable packages;
	public PackageTable getPackages() {return packages;}

	private final PaymentTypeTable paymentTypes;
	public PaymentTypeTable getPaymentTypes() {
		return paymentTypes;
	}

	private final PhysicalServerTable physicalServers;
	public PhysicalServerTable getPhysicalServers() {return physicalServers;}

	private final PostgresDatabaseTable postgresDatabases;
	public PostgresDatabaseTable getPostgresDatabases() {
		return postgresDatabases;
	}

	private final PostgresEncodingTable postgresEncodings;
	public PostgresEncodingTable getPostgresEncodings() {
		return postgresEncodings;
	}

	private final PostgresServerUserTable postgresServerUsers;
	public PostgresServerUserTable getPostgresServerUsers() {
		return postgresServerUsers;
	}

	private final PostgresServerTable postgresServers;
	public PostgresServerTable getPostgresServers() {return postgresServers;}

	private final PostgresUserTable postgresUsers;
	public PostgresUserTable getPostgresUsers() {
		return postgresUsers;
	}

	private final PostgresVersionTable postgresVersions;
	public PostgresVersionTable getPostgresVersions() {
		return postgresVersions;
	}

	private final PrivateFTPServerTable privateFTPServers;
	public PrivateFTPServerTable getPrivateFTPServers() {
		return privateFTPServers;
	}

	private final ProcessorTypeTable processorTypes;
	public ProcessorTypeTable getProcessorTypes() {return processorTypes;}

	private final ProtocolTable protocols;
	public ProtocolTable getProtocols() {
		return protocols;
	}

	private final RackTable racks;
	public RackTable getRacks() {return racks;}

	private final ResellerTable resellers;
	public ResellerTable getResellers() {
		return resellers;
	}

	private final ResourceTable resources;
	public ResourceTable getResources() {
		return resources;
	}

	private final SchemaColumnTable schemaColumns;
	public SchemaColumnTable getSchemaColumns() {
		return schemaColumns;
	}

	private final SchemaForeignKeyTable schemaForeignKeys;
	public SchemaForeignKeyTable getSchemaForeignKeys() {
		return schemaForeignKeys;
	}

	private final SchemaTableTable schemaTables;
	public SchemaTableTable getSchemaTables() {
		return schemaTables;
	}

	private final SchemaTypeTable schemaTypes;
	public SchemaTypeTable getSchemaTypes() {
		return schemaTypes;
	}

	private final ServerFarmTable serverFarms;
	public ServerFarmTable getServerFarms() {
		return serverFarms;
	}

	private final ServerTable servers;
	public ServerTable getServers() {
		return servers;
	}

	private final ShellTable shells;
	public ShellTable getShells() {
		return shells;
	}

	private final SignupRequestOptionTable signupRequestOptions;
	public SignupRequestOptionTable getSignupRequestOptions() {
		return signupRequestOptions;
	}

	private final SignupRequestTable signupRequests;
	public SignupRequestTable getSignupRequests() {
		return signupRequests;
	}

	private final SpamEmailMessageTable spamEmailMessages;
	public SpamEmailMessageTable getSpamEmailMessages() {
		return spamEmailMessages;
	}

	private final SystemEmailAliasTable systemEmailAliases;
	public SystemEmailAliasTable getSystemEmailAliases() {
		return systemEmailAliases;
	}

	private final TechnologyTable technologies;
	public TechnologyTable getTechnologies() {
		return technologies;
	}

	private final TechnologyClassTable technologyClasses;
	public TechnologyClassTable getTechnologyClasses() {
		return technologyClasses;
	}

	private final TechnologyNameTable technologyNames;
	public TechnologyNameTable getTechnologyNames() {
		return technologyNames;
	}

	private final TechnologyVersionTable technologyVersions;
	public TechnologyVersionTable getTechnologyVersions() {
		return technologyVersions;
	}

	private final TicketActionTypeTable ticketActionTypes;
	public TicketActionTypeTable getTicketActionTypes() {
		return ticketActionTypes;
	}

	private final TicketActionTable ticketActions;
	public TicketActionTable getTicketActions() {
		return ticketActions;
	}

	private final TicketAssignmentTable ticketAssignments;
	public TicketAssignmentTable getTicketAssignments() {
		return ticketAssignments;
	}

	private final TicketBrandCategoryTable ticketBrandCategories;
	public TicketBrandCategoryTable getTicketBrandCategories() {
		return ticketBrandCategories;
	}

	private final TicketCategoryTable ticketCategories;
	public TicketCategoryTable getTicketCategories() {
		return ticketCategories;
	}

	private final TicketPriorityTable ticketPriorities;
	public TicketPriorityTable getTicketPriorities() {
		return ticketPriorities;
	}

	private final TicketStatusTable ticketStatuses;
	public TicketStatusTable getTicketStatuses() {
		return ticketStatuses;
	}

	private final TicketTypeTable ticketTypes;
	public TicketTypeTable getTicketTypes() {
		return ticketTypes;
	}

	private final TicketTable tickets;
	public TicketTable getTickets() {
		return tickets;
	}

	private final TimeZoneTable timeZones;
	public TimeZoneTable getTimeZones() {
		return timeZones;
	}

	private final TransactionTypeTable transactionTypes;
	public TransactionTypeTable getTransactionTypes() {
		return transactionTypes;
	}

	private final TransactionTable transactions;
	public TransactionTable getTransactions() {
		return transactions;
	}

	private final USStateTable usStates;
	public USStateTable getUsStates() {
		return usStates;
	}

	private final UsernameTable usernames;
	public UsernameTable getUsernames() {
		return usernames;
	}

	private final VirtualDiskTable virtualDisks;
	public VirtualDiskTable getVirtualDisks() {return virtualDisks;}

	private final VirtualServerTable virtualServers;
	public VirtualServerTable getVirtualServers() {return virtualServers;}

	private final WhoisHistoryTable whoisHistory;
	public WhoisHistoryTable getWhoisHistory() {return whoisHistory;}

	private final SimpleAOClient simpleAOClient;
	public SimpleAOClient getSimpleAOClient() {return simpleAOClient;}

	/**
	 * The tables are placed in this list in the constructor.
	 * This list is aligned with the table identifiers in
	 * <code>SchemaTable</code>.
	 *
	 * @see  SchemaTable
	 */
	final List<AOServTable> tables;

	protected AOServConnector(
		HostAddress hostname,
		InetAddress local_ip,
		Port port,
		UserId connectAs,
		UserId authenticateAs,
		String password,
		DomainName daemonServer,
		Logger logger
	) throws IOException {
		this.hostname = hostname;
		this.local_ip = local_ip;
		this.port = port;
		this.connectAs = connectAs;
		this.authenticateAs = authenticateAs;
		this.password = password;
		this.daemonServer = daemonServer;
		this.logger = logger;

		// These must match the table IDs in SchemaTable
		ArrayList<AOServTable> newTables = new ArrayList<>();
		newTables.add(aoServerDaemonHosts=new AOServerDaemonHostTable(this));
		newTables.add(aoServers=new AOServerTable(this));
		newTables.add(aoservPermissions=new AOServPermissionTable(this));
		newTables.add(aoservProtocols=new AOServProtocolTable(this));
		newTables.add(aoshCommands=new AOSHCommandTable(this));
		newTables.add(architectures=new ArchitectureTable(this));
		newTables.add(backupPartitions=new BackupPartitionTable(this));
		newTables.add(backupReports=new BackupReportTable(this));
		newTables.add(backupRetentions=new BackupRetentionTable(this));
		newTables.add(bankAccounts=new BankAccountTable(this));
		newTables.add(bankTransactionTypes=new BankTransactionTypeTable(this));
		newTables.add(bankTransactions=new BankTransactionTable(this));
		newTables.add(banks=new BankTable(this));
		newTables.add(blackholeEmailAddresses=new BlackholeEmailAddressTable(this));
		newTables.add(brands=new BrandTable(this));
		newTables.add(businessAdministrators=new BusinessAdministratorTable(this));
		newTables.add(businessAdministratorPermissions=new BusinessAdministratorPermissionTable(this));
		newTables.add(businessProfiles=new BusinessProfileTable(this));
		newTables.add(businesses=new BusinessTable(this));
		newTables.add(businessServers=new BusinessServerTable(this));
		newTables.add(countryCodes=new CountryCodeTable(this));
		newTables.add(creditCardProcessors=new CreditCardProcessorTable(this));
		newTables.add(creditCardTransactions=new CreditCardTransactionTable(this));
		newTables.add(creditCards=new CreditCardTable(this));
		newTables.add(cvsRepositories=new CvsRepositoryTable(this));
		newTables.add(disableLogs=new DisableLogTable(this));
		newTables.add(distroFileTypes=new DistroFileTypeTable(this));
		newTables.add(distroFiles=new DistroFileTable(this));
		newTables.add(distroReportTypes=new DistroReportTypeTable(this));
		newTables.add(dnsForbiddenZones=new DNSForbiddenZoneTable(this));
		newTables.add(dnsRecords=new DNSRecordTable(this));
		newTables.add(dnsTLDs=new DNSTLDTable(this));
		newTables.add(dnsTypes=new DNSTypeTable(this));
		newTables.add(dnsZones=new DNSZoneTable(this));
		newTables.add(emailAddresses=new EmailAddressTable(this));
		newTables.add(emailAttachmentBlocks=new EmailAttachmentBlockTable(this));
		newTables.add(emailAttachmentTypes=new EmailAttachmentTypeTable(this));
		newTables.add(emailDomains=new EmailDomainTable(this));
		newTables.add(emailForwardings=new EmailForwardingTable(this));
		newTables.add(emailListAddresses=new EmailListAddressTable(this));
		newTables.add(emailLists=new EmailListTable(this));
		newTables.add(emailPipeAddresses=new EmailPipeAddressTable(this));
		newTables.add(emailPipes=new EmailPipeTable(this));
		newTables.add(emailSmtpRelayTypes=new EmailSmtpRelayTypeTable(this));
		newTables.add(emailSmtpRelays=new EmailSmtpRelayTable(this));
		newTables.add(emailSmtpSmartHostDomainTable=new EmailSmtpSmartHostDomainTable(this));
		newTables.add(emailSmtpSmartHostTable=new EmailSmtpSmartHostTable(this));
		newTables.add(emailSpamAssassinIntegrationModes=new EmailSpamAssassinIntegrationModeTable(this));
		newTables.add(encryptionKeys=new EncryptionKeyTable(this));
		newTables.add(expenseCategories=new ExpenseCategoryTable(this));
		newTables.add(failoverFileLogs=new FailoverFileLogTable(this));
		newTables.add(failoverFileReplications=new FailoverFileReplicationTable(this));
		newTables.add(failoverFileSchedules=new FailoverFileScheduleTable(this));
		newTables.add(failoverMySQLReplications=new FailoverMySQLReplicationTable(this));
		newTables.add(fileBackupSettings=new FileBackupSettingTable(this));
		newTables.add(ftpGuestUsers=new FTPGuestUserTable(this));
		newTables.add(httpdBinds=new HttpdBindTable(this));
		newTables.add(httpdJBossSites=new HttpdJBossSiteTable(this));
		newTables.add(httpdJBossVersions=new HttpdJBossVersionTable(this));
		newTables.add(httpdJKCodes=new HttpdJKCodeTable(this));
		newTables.add(httpdJKProtocols=new HttpdJKProtocolTable(this));
		newTables.add(httpdServers=new HttpdServerTable(this));
		newTables.add(httpdSharedTomcats=new HttpdSharedTomcatTable(this));
		newTables.add(httpdSiteAuthenticatedLocationTable=new HttpdSiteAuthenticatedLocationTable(this));
		newTables.add(httpdSiteBinds=new HttpdSiteBindTable(this));
		newTables.add(httpdSiteURLs=new HttpdSiteURLTable(this));
		newTables.add(httpdSites=new HttpdSiteTable(this));
		newTables.add(httpdStaticSites=new HttpdStaticSiteTable(this));
		newTables.add(httpdTomcatContexts=new HttpdTomcatContextTable(this));
		newTables.add(httpdTomcatDataSources=new HttpdTomcatDataSourceTable(this));
		newTables.add(httpdTomcatParameters=new HttpdTomcatParameterTable(this));
		newTables.add(httpdTomcatSites=new HttpdTomcatSiteTable(this));
		newTables.add(httpdTomcatSharedSites=new HttpdTomcatSharedSiteTable(this));
		newTables.add(httpdTomcatStdSites=new HttpdTomcatStdSiteTable(this));
		newTables.add(httpdTomcatVersions=new HttpdTomcatVersionTable(this));
		newTables.add(httpdWorkers=new HttpdWorkerTable(this));
		newTables.add(ipAddresses=new IPAddressTable(this));
		newTables.add(ipReputationLimiterLimits=new IpReputationLimiterLimitTable(this));
		newTables.add(ipReputationLimiterSets=new IpReputationLimiterSetTable(this));
		newTables.add(ipReputationLimiters=new IpReputationLimiterTable(this));
		newTables.add(ipReputationSetHosts=new IpReputationSetHostTable(this));
		newTables.add(ipReputationSetNetworks=new IpReputationSetNetworkTable(this));
		newTables.add(ipReputationSets=new IpReputationSetTable(this));
		newTables.add(languages=new LanguageTable(this));
		newTables.add(linuxAccAddresses=new LinuxAccAddressTable(this));
		newTables.add(linuxAccountTypes=new LinuxAccountTypeTable(this));
		newTables.add(linuxAccounts=new LinuxAccountTable(this));
		newTables.add(linuxGroupAccounts=new LinuxGroupAccountTable(this));
		newTables.add(linuxGroupTypes=new LinuxGroupTypeTable(this));
		newTables.add(linuxGroups=new LinuxGroupTable(this));
		newTables.add(linuxServerAccounts=new LinuxServerAccountTable(this));
		newTables.add(linuxServerGroups=new LinuxServerGroupTable(this));
		newTables.add(majordomoLists=new MajordomoListTable(this));
		newTables.add(majordomoServers=new MajordomoServerTable(this));
		newTables.add(majordomoVersions=new MajordomoVersionTable(this));
		newTables.add(masterHosts=new MasterHostTable(this));
		newTables.add(masterProcesses=new MasterProcessTable(this));
		newTables.add(masterServerStats=new MasterServerStatTable(this));
		newTables.add(masterServers=new MasterServerTable(this));
		newTables.add(masterUsers=new MasterUserTable(this));
		newTables.add(monthlyCharges=new MonthlyChargeTable(this));
		newTables.add(mysqlDatabases=new MySQLDatabaseTable(this));
		newTables.add(mysqlDBUsers=new MySQLDBUserTable(this));
		newTables.add(mysqlServerUsers=new MySQLServerUserTable(this));
		newTables.add(mysqlServers=new MySQLServerTable(this));
		newTables.add(mysqlUsers=new MySQLUserTable(this));
		newTables.add(netBinds=new NetBindTable(this));
		newTables.add(netDeviceIDs=new NetDeviceIDTable(this));
		newTables.add(netDevices=new NetDeviceTable(this));
		newTables.add(netTcpRedirects=new NetTcpRedirectTable(this));
		newTables.add(noticeLogs=new NoticeLogTable(this));
		newTables.add(noticeTypes=new NoticeTypeTable(this));
		newTables.add(operatingSystemVersions=new OperatingSystemVersionTable(this));
		newTables.add(operatingSystems=new OperatingSystemTable(this));
		newTables.add(packageCategories=new PackageCategoryTable(this));
		newTables.add(packageDefinitionLimits=new PackageDefinitionLimitTable(this));
		newTables.add(packageDefinitions=new PackageDefinitionTable(this));
		newTables.add(packages=new PackageTable(this));
		newTables.add(paymentTypes=new PaymentTypeTable(this));
		newTables.add(physicalServers=new PhysicalServerTable(this));
		newTables.add(postgresDatabases=new PostgresDatabaseTable(this));
		newTables.add(postgresEncodings=new PostgresEncodingTable(this));
		newTables.add(postgresServerUsers=new PostgresServerUserTable(this));
		newTables.add(postgresServers=new PostgresServerTable(this));
		newTables.add(postgresUsers=new PostgresUserTable(this));
		newTables.add(postgresVersions=new PostgresVersionTable(this));
		newTables.add(privateFTPServers=new PrivateFTPServerTable(this));
		newTables.add(processorTypes=new ProcessorTypeTable(this));
		newTables.add(protocols=new ProtocolTable(this));
		newTables.add(racks=new RackTable(this));
		newTables.add(resellers=new ResellerTable(this));
		newTables.add(resources=new ResourceTable(this));
		newTables.add(schemaColumns=new SchemaColumnTable(this));
		newTables.add(schemaForeignKeys=new SchemaForeignKeyTable(this));
		newTables.add(schemaTables=new SchemaTableTable(this));
		newTables.add(schemaTypes=new SchemaTypeTable(this));
		newTables.add(serverFarms=new ServerFarmTable(this));
		newTables.add(servers=new ServerTable(this));
		newTables.add(shells=new ShellTable(this));
		newTables.add(signupRequestOptions=new SignupRequestOptionTable(this));
		newTables.add(signupRequests=new SignupRequestTable(this));
		newTables.add(spamEmailMessages=new SpamEmailMessageTable(this));
		newTables.add(systemEmailAliases=new SystemEmailAliasTable(this));
		newTables.add(technologies=new TechnologyTable(this));
		newTables.add(technologyClasses=new TechnologyClassTable(this));
		newTables.add(technologyNames=new TechnologyNameTable(this));
		newTables.add(technologyVersions=new TechnologyVersionTable(this));
		newTables.add(ticketActionTypes=new TicketActionTypeTable(this));
		newTables.add(ticketActions=new TicketActionTable(this));
		newTables.add(ticketAssignments=new TicketAssignmentTable(this));
		newTables.add(ticketBrandCategories=new TicketBrandCategoryTable(this));
		newTables.add(ticketCategories=new TicketCategoryTable(this));
		newTables.add(ticketPriorities=new TicketPriorityTable(this));
		newTables.add(ticketStatuses=new TicketStatusTable(this));
		newTables.add(ticketTypes=new TicketTypeTable(this));
		newTables.add(tickets=new TicketTable(this));
		newTables.add(timeZones=new TimeZoneTable(this));
		newTables.add(transactionTypes=new TransactionTypeTable(this));
		newTables.add(transactions=new TransactionTable(this));
		newTables.add(usStates=new USStateTable(this));
		newTables.add(usernames=new UsernameTable(this));
		newTables.add(virtualDisks=new VirtualDiskTable(this));
		newTables.add(virtualServers=new VirtualServerTable(this));
		newTables.add(whoisHistory=new WhoisHistoryTable(this));
		newTables.trimToSize();
		tables = Collections.unmodifiableList(newTables);

		simpleAOClient=new SimpleAOClient(this);
	}

	/**
	 * Uses equivilence equality like <code>Object.equals</code>.  Two
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
	 * Uses equivilence hashCode like <code>Object.hashCode()</code>.
	 */
	@Override
	final public int hashCode() {
		return super.hashCode();
	}

	/**
	 * Clears all caches used by this connector.
	 */
	public void clearCaches() {
		for(AOServTable table : tables) table.clearCache();
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
	 * @exception  IOException  if no connection can be established
	 */
	public static AOServConnector getConnector(Logger logger) throws IOException {
		UserId username = AOServClientConfiguration.getUsername();
		DomainName daemonServer = AOServClientConfiguration.getDaemonServer();
		return getConnector(
			username,
			username,
			AOServClientConfiguration.getPassword(),
			daemonServer,
			logger
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
	 * @exception  IOException  if no connection can be established
	 */
	public static AOServConnector getConnector(UserId username, String password, Logger logger) throws IOException {
		return getConnector(username, username, password, null, logger);
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
	 * @exception  IOException  if no connection can be established
	 */
	public static AOServConnector getConnector(
		UserId connectAs,
		UserId authenticateAs,
		String password,
		DomainName daemonServer,
		Logger logger
	) throws IOException {
		List<String> protocols=AOServClientConfiguration.getProtocols();
		int size=protocols.size();
		for(int c=0;c<size;c++) {
			String protocol=protocols.get(c);
			try {
				AOServConnector connector;
				if(TCPConnector.PROTOCOL.equals(protocol)) {
					connector=TCPConnector.getTCPConnector(
						AOServClientConfiguration.getTcpHostname(),
						AOServClientConfiguration.getTcpLocalIp(),
						AOServClientConfiguration.getTcpPort(),
						connectAs,
						authenticateAs,
						password,
						daemonServer,
						AOServClientConfiguration.getTcpConnectionPoolSize(),
						AOServClientConfiguration.getTcpConnectionMaxAge(),
						logger
					);
				} else if(SSLConnector.PROTOCOL.equals(protocol)) {
					connector=SSLConnector.getSSLConnector(
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
						AOServClientConfiguration.getSslTruststorePassword(),
						logger
					);
				/*
				} else if("http".equals(protocol)) {
					connector=new HTTPConnector();
				} else if("https".equals(protocol)) {
					connector=new HTTPSConnector();
				*/
				} else throw new IOException("Unknown protocol in aoserv.client.protocols: "+protocol);

				return connector;
			} catch(IOException err) {
				logger.log(Level.SEVERE, null, err);
			}
		}
		throw new IOException("Unable to connect using any of the available protocols.");
	}

	/**
	 * Each connector is assigned a unique identifier, which the
	 * server uses to not send events originating from
	 * this connector back to connections of this
	 * connector.
	 *
	 * @return  the globally unique identifier or <code>-1</code> if
	 *          the identifier has not yet been assigned
	 */
	final public long getConnectorID() {
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

	private static final SecureRandom secureRandom = new SecureRandom();

	public static SecureRandom getRandom() {
		return secureRandom;
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
	 * @see  SchemaTable
	 */
	@SuppressWarnings({"unchecked"})
	final public AOServTable<?,? extends AOServObject> getTable(int tableID) throws IllegalArgumentException {
		if(tableID>=0 && tableID<tables.size()) return tables.get(tableID);
		throw new IllegalArgumentException("Table not found for ID="+tableID);
	}

	/**
	 * Gets an unmodifiable list of all of the tables in the system.
	 *
	 * @return  a <code>List<AOServTable></code> containing all the tables.  Each
	 *          table is at an index corresponding to its unique ID.
	 *
	 * @see  #getTable(int)
	 * @see  SchemaTable
	 */
	final public List<AOServTable> getTables() {
		return tables;
	}

	/**
	 * Gets the <code>BusinessAdministrator</code> who is logged in using
	 * this <code>AOServConnector</code>.  Each username and password pair
	 * resolves to an always-accessible <code>BusinessAdministrator</code>.
	 * Details about permissions and capabilities may be obtained from the
	 * <code>BusinessAdministrator</code>.
	 *
	 * @return  the <code>BusinessAdministrator</code> who is logged in
	 *
	 * @exception  IOException  if unable to communicate with the server
	 * @exception  SQLException  if unable to access the database or the
	 *                           <code>BusinessAdministrator</code> was not
	 *                           found
	 */
	final public BusinessAdministrator getThisBusinessAdministrator() throws SQLException, IOException {
		BusinessAdministrator obj=businessAdministrators.get(connectAs);
		if(obj==null) throw new SQLException("Unable to find BusinessAdministrator: "+connectAs);
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
			new UpdateRequest() {
				IntList tableList;
				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(AOServProtocol.CommandID.INVALIDATE_TABLE.ordinal());
					out.writeCompressedInt(tableID);
					out.writeCompressedInt(server);
				}
				@Override
				public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AOServProtocol.DONE) tableList=readInvalidateList(in);
					else {
						AOServProtocol.checkResult(code, in);
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

	static IntList readInvalidateList(CompressedDataInputStream in) throws IOException {
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
		requestUpdate(false, AOServProtocol.CommandID.PING);
		long timeSpan=System.currentTimeMillis()-startTime;
		if(timeSpan>Integer.MAX_VALUE) return Integer.MAX_VALUE;
		return (int)timeSpan;
	}

	abstract public void printConnectionStatsHTML(Appendable out) throws IOException;

	/**
	 * Releases a connection to the server.  This will either close the
	 * connection or allow another thread to use the connection.
	 * Connections may be of any protocol, so each connection must be
	 * released after every transaction.
	 *
	 * @param  connection  the connection to release
	 *
	 * @exception  if an error occurred while closing or releasing the conection
	 *
	 * @see  #getConnection
	 */
	protected abstract void releaseConnection(AOServConnection connection) throws IOException;

	final public void removeFromAllTables(TableListener listener) {
		for(AOServTable table : tables) table.removeTableListener(listener);
	}

	static void writeParams(Object[] params, CompressedDataOutputStream out) throws IOException {
		for(Object param : params) {
			if(param==null) throw new NullPointerException("param is null");
			else if(param instanceof Integer) out.writeCompressedInt(((Integer)param));
			else if(param instanceof SchemaTable.TableID) out.writeCompressedInt(((SchemaTable.TableID)param).ordinal());
			else if(param instanceof AOServProtocol.CommandID) out.writeCompressedInt(((AOServProtocol.CommandID)param).ordinal());
			else if(param instanceof String) out.writeUTF((String)param);
			else if(param instanceof Float) out.writeFloat((Float)param);
			else if(param instanceof Long) out.writeLong((Long)param);
			else if(param instanceof Boolean) out.writeBoolean((Boolean)param);
			else if(param instanceof Short) out.writeShort((Short)param);
			else if(param instanceof byte[]) {
				byte[] bytes=(byte[])param;
				out.writeCompressedInt(bytes.length);
				out.write(bytes, 0, bytes.length);
			}
			// Self-validating types
			else if(param instanceof AccountingCode) out.writeUTF(param.toString());
			else if(param instanceof Email) out.writeUTF(param.toString());
			else if(param instanceof HostAddress) out.writeUTF(param.toString());
			else if(param instanceof InetAddress) out.writeUTF(param.toString());
			else if(param instanceof UnixPath) out.writeUTF(param.toString());
			else if(param instanceof UserId) out.writeUTF(param.toString());
			else if(param instanceof DomainLabel) out.writeUTF(param.toString());
			else if(param instanceof DomainLabels) out.writeUTF(param.toString());
			else if(param instanceof DomainName) out.writeUTF(param.toString());
			else if(param instanceof Gecos) out.writeUTF(param.toString());
			else if(param instanceof GroupId) out.writeUTF(param.toString());
			else if(param instanceof HashedPassword) out.writeUTF(param.toString());
			else if(param instanceof LinuxId) out.writeCompressedInt(((LinuxId)param).getId());
			else if(param instanceof MacAddress) out.writeUTF(param.toString());
			else if(param instanceof MySQLDatabaseName) out.writeUTF(param.toString());
			else if(param instanceof MySQLServerName) out.writeUTF(param.toString());
			else if(param instanceof MySQLTableName) out.writeUTF(param.toString());
			else if(param instanceof MySQLUserId) out.writeUTF(param.toString());
			else if(param instanceof Port) {
				Port port = (Port)param;
				out.writeCompressedInt(port.getPort());
				out.writeUTF(port.getProtocol().name().toLowerCase(Locale.ROOT));
			}
			else if(param instanceof PostgresDatabaseName) out.writeUTF(param.toString());
			else if(param instanceof PostgresServerName) out.writeUTF(param.toString());
			else if(param instanceof PostgresUserId) out.writeUTF(param.toString());
			// Any other Streamable
			else if(param instanceof AOServStreamable) ((AOServStreamable)param).write(out, AOServProtocol.Version.CURRENT_VERSION);
			else if(param instanceof Streamable) ((Streamable)param).write(out, AOServProtocol.Version.CURRENT_VERSION.getVersion());
			else throw new IOException("Unknown class for param: "+param.getClass().getName());
		}
	}

	/**
	 * This is the preferred mechanism for providing custom requests that have a return value.
	 *
	 * @see  #requestResult(boolean,ResultRequest)
	 */
	interface ResultRequest<T> {
		/**
		 * Writes the request to the server.
		 * This does not need to flush the output stream.
		 */
		void writeRequest(CompressedDataOutputStream out) throws IOException;

		/**
		 * Reads the response from the server if the request was successfully sent.
		 */
		void readResponse(CompressedDataInputStream in) throws IOException, SQLException;

		/**
		 * If both the request and response were successful, this is called after the
		 * connection to the server is released.  The result is returned here so
		 * any additional processing in packaging the result may be performed
		 * after the connection is released.
		 */
		T afterRelease();
	}

	final <T> T requestResult(boolean allowRetry, ResultRequest<T> resultRequest) throws IOException, SQLException {
		int attempt = 1;
		int attempts = allowRetry ? RETRY_ATTEMPTS : 1;
		while(!Thread.interrupted()) {
			try {
				AOServConnection connection=getConnection(1);
				try {
					CompressedDataOutputStream out=connection.getOutputStream();
					resultRequest.writeRequest(out);
					out.flush();

					CompressedDataInputStream in=connection.getInputStream();
					resultRequest.readResponse(in);
				} catch(RuntimeException | IOException err) {
					connection.close();
					throw err;
				} finally {
					releaseConnection(connection);
				}
				return resultRequest.afterRelease();
			} catch(InterruptedIOException err) {
				throw err;
			} catch(RuntimeException | IOException | SQLException err) {
				if(Thread.interrupted() || attempt>=attempts || isImmediateFail(err)) throw err;
			}
			try {
				Thread.sleep(retryAttemptDelays[attempt-1]);
			} catch(InterruptedException err) {
				// Restore the interrupted status
				Thread.currentThread().interrupt();
				InterruptedIOException ioErr = new InterruptedIOException();
				ioErr.initCause(err);
				throw ioErr;
			}
			attempt++;
		}
		throw new InterruptedIOException();
	}

	final boolean requestBooleanQuery(boolean allowRetry, AOServProtocol.CommandID commID, Object ... params) throws IOException, SQLException {
		int attempt = 1;
		int attempts = allowRetry ? RETRY_ATTEMPTS : 1;
		while(!Thread.interrupted()) {
			try {
				AOServConnection connection=getConnection(1);
				try {
					CompressedDataOutputStream out=connection.getOutputStream();
					out.writeCompressedInt(commID.ordinal());
					writeParams(params, out);
					out.flush();

					CompressedDataInputStream in=connection.getInputStream();
					int code=in.readByte();
					if(code==AOServProtocol.DONE) return in.readBoolean();
					AOServProtocol.checkResult(code, in);
					throw new IOException("Unexpected response code: "+code);
				} catch(RuntimeException | IOException err) {
					connection.close();
					throw err;
				} finally {
					releaseConnection(connection);
				}
			} catch(InterruptedIOException err) {
				throw err;
			} catch(RuntimeException | IOException | SQLException err) {
				if(Thread.interrupted() || attempt>=attempts || isImmediateFail(err)) throw err;
			}
			try {
				Thread.sleep(retryAttemptDelays[attempt-1]);
			} catch(InterruptedException err) {
				// Restore the interrupted status
				Thread.currentThread().interrupt();
				InterruptedIOException ioErr = new InterruptedIOException();
				ioErr.initCause(err);
				throw ioErr;
			}
			attempt++;
		}
		throw new InterruptedIOException();
	}

	final boolean requestBooleanQueryIL(boolean allowRetry, AOServProtocol.CommandID commID, Object ... params) throws IOException, SQLException {
		int attempt = 1;
		int attempts = allowRetry ? RETRY_ATTEMPTS : 1;
		while(!Thread.interrupted()) {
			try {
				boolean result;
				IntList invalidateList;
				AOServConnection connection=getConnection(1);
				try {
					CompressedDataOutputStream out=connection.getOutputStream();
					out.writeCompressedInt(commID.ordinal());
					writeParams(params, out);
					out.flush();

					CompressedDataInputStream in=connection.getInputStream();
					int code=in.readByte();
					if(code==AOServProtocol.DONE) {
						result = in.readBoolean();
						invalidateList=readInvalidateList(in);
					} else {
						AOServProtocol.checkResult(code, in);
						throw new IOException("Unexpected response code: "+code);
					}
				} catch(RuntimeException | IOException err) {
					connection.close();
					throw err;
				} finally {
					releaseConnection(connection);
				}
				tablesUpdated(invalidateList);
				return result;
			} catch(InterruptedIOException err) {
				throw err;
			} catch(RuntimeException | IOException | SQLException err) {
				if(Thread.interrupted() || attempt>=attempts || isImmediateFail(err)) throw err;
			}
			try {
				Thread.sleep(retryAttemptDelays[attempt-1]);
			} catch(InterruptedException err) {
				// Restore the interrupted status
				Thread.currentThread().interrupt();
				InterruptedIOException ioErr = new InterruptedIOException();
				ioErr.initCause(err);
				throw ioErr;
			}
			attempt++;
		}
		throw new InterruptedIOException();
	}

	final int requestIntQuery(boolean allowRetry, AOServProtocol.CommandID commID, Object ... params) throws IOException, SQLException {
		int attempt = 1;
		int attempts = allowRetry ? RETRY_ATTEMPTS : 1;
		while(!Thread.interrupted()) {
			try {
				AOServConnection connection=getConnection(1);
				try {
					CompressedDataOutputStream out=connection.getOutputStream();
					out.writeCompressedInt(commID.ordinal());
					writeParams(params, out);
					out.flush();

					CompressedDataInputStream in=connection.getInputStream();
					int code=in.readByte();
					if(code==AOServProtocol.DONE) return in.readCompressedInt();
					AOServProtocol.checkResult(code, in);
					throw new IOException("Unexpected response code: "+code);
				} catch(RuntimeException | IOException err) {
					connection.close();
					throw err;
				} finally {
					releaseConnection(connection);
				}
			} catch(InterruptedIOException err) {
				throw err;
			} catch(RuntimeException | IOException | SQLException err) {
				if(Thread.interrupted() || attempt>=attempts || isImmediateFail(err)) throw err;
			}
			try {
				Thread.sleep(retryAttemptDelays[attempt-1]);
			} catch(InterruptedException err) {
				// Restore the interrupted status
				Thread.currentThread().interrupt();
				InterruptedIOException ioErr = new InterruptedIOException();
				ioErr.initCause(err);
				throw ioErr;
			}
			attempt++;
		}
		throw new InterruptedIOException();
	}

	final int requestIntQueryIL(boolean allowRetry, AOServProtocol.CommandID commID, Object ... params) throws IOException, SQLException {
		int attempt = 1;
		int attempts = allowRetry ? RETRY_ATTEMPTS : 1;
		while(!Thread.interrupted()) {
			try {
				int result;
				IntList invalidateList;
				AOServConnection connection=getConnection(1);
				try {
					CompressedDataOutputStream out=connection.getOutputStream();
					out.writeCompressedInt(commID.ordinal());
					writeParams(params, out);
					out.flush();

					CompressedDataInputStream in=connection.getInputStream();
					int code=in.readByte();
					if(code==AOServProtocol.DONE) {
						result=in.readCompressedInt();
						invalidateList=readInvalidateList(in);
					} else {
						AOServProtocol.checkResult(code, in);
						throw new IOException("Unexpected response code: "+code);
					}
				} catch(RuntimeException | IOException err) {
					connection.close();
					throw err;
				} finally {
					releaseConnection(connection);
				}
				tablesUpdated(invalidateList);
				return result;
			} catch(InterruptedIOException err) {
				throw err;
			} catch(RuntimeException | IOException | SQLException err) {
				if(Thread.interrupted() || attempt>=attempts || isImmediateFail(err)) throw err;
			}
			try {
				Thread.sleep(retryAttemptDelays[attempt-1]);
			} catch(InterruptedException err) {
				// Restore the interrupted status
				Thread.currentThread().interrupt();
				InterruptedIOException ioErr = new InterruptedIOException();
				ioErr.initCause(err);
				throw ioErr;
			}
			attempt++;
		}
		throw new InterruptedIOException();
	}

	final long requestLongQuery(boolean allowRetry, AOServProtocol.CommandID commID, Object ... params) throws IOException, SQLException {
		int attempt = 1;
		int attempts = allowRetry ? RETRY_ATTEMPTS : 1;
		while(!Thread.interrupted()) {
			try {
				AOServConnection connection=getConnection(1);
				try {
					CompressedDataOutputStream out=connection.getOutputStream();
					out.writeCompressedInt(commID.ordinal());
					writeParams(params, out);
					out.flush();

					CompressedDataInputStream in=connection.getInputStream();
					int code=in.readByte();
					if(code==AOServProtocol.DONE) return in.readLong();
					AOServProtocol.checkResult(code, in);
					throw new IOException("Unexpected response code: "+code);
				} catch(RuntimeException | IOException err) {
					connection.close();
					throw err;
				} finally {
					releaseConnection(connection);
				}
			} catch(InterruptedIOException err) {
				throw err;
			} catch(RuntimeException | IOException | SQLException err) {
				if(Thread.interrupted() || attempt>=attempts || isImmediateFail(err)) throw err;
			}
			try {
				Thread.sleep(retryAttemptDelays[attempt-1]);
			} catch(InterruptedException err) {
				// Restore the interrupted status
				Thread.currentThread().interrupt();
				InterruptedIOException ioErr = new InterruptedIOException();
				ioErr.initCause(err);
				throw ioErr;
			}
			attempt++;
		}
		throw new InterruptedIOException();
	}

	final short requestShortQuery(boolean allowRetry, AOServProtocol.CommandID commID, Object ... params) throws IOException, SQLException {
		int attempt = 1;
		int attempts = allowRetry ? RETRY_ATTEMPTS : 1;
		while(!Thread.interrupted()) {
			try {
				AOServConnection connection=getConnection(1);
				try {
					CompressedDataOutputStream out=connection.getOutputStream();
					out.writeCompressedInt(commID.ordinal());
					writeParams(params, out);
					out.flush();

					CompressedDataInputStream in=connection.getInputStream();
					int code=in.readByte();
					if(code==AOServProtocol.DONE) return in.readShort();
					AOServProtocol.checkResult(code, in);
					throw new IOException("Unexpected response code: "+code);
				} catch(RuntimeException | IOException err) {
					connection.close();
					throw err;
				} finally {
					releaseConnection(connection);
				}
			} catch(InterruptedIOException err) {
				throw err;
			} catch(RuntimeException | IOException | SQLException err) {
				if(Thread.interrupted() || attempt>=attempts || isImmediateFail(err)) throw err;
			}
			try {
				Thread.sleep(retryAttemptDelays[attempt-1]);
			} catch(InterruptedException err) {
				// Restore the interrupted status
				Thread.currentThread().interrupt();
				InterruptedIOException ioErr = new InterruptedIOException();
				ioErr.initCause(err);
				throw ioErr;
			}
			attempt++;
		}
		throw new InterruptedIOException();
	}

	final short requestShortQueryIL(boolean allowRetry, AOServProtocol.CommandID commID, Object ... params) throws IOException, SQLException {
		int attempt = 1;
		int attempts = allowRetry ? RETRY_ATTEMPTS : 1;
		while(!Thread.interrupted()) {
			try {
				short result;
				IntList invalidateList;
				AOServConnection connection=getConnection(1);
				try {
					CompressedDataOutputStream out=connection.getOutputStream();
					out.writeCompressedInt(commID.ordinal());
					writeParams(params, out);
					out.flush();

					CompressedDataInputStream in=connection.getInputStream();
					int code=in.readByte();
					if(code==AOServProtocol.DONE) {
						result=in.readShort();
						invalidateList=readInvalidateList(in);
					} else {
						AOServProtocol.checkResult(code, in);
						throw new IOException("Unexpected response code: "+code);
					}
				} catch(RuntimeException | IOException err) {
					connection.close();
					throw err;
				} finally {
					releaseConnection(connection);
				}
				tablesUpdated(invalidateList);
				return result;
			} catch(InterruptedIOException err) {
				throw err;
			} catch(RuntimeException | IOException | SQLException err) {
				if(Thread.interrupted() || attempt>=attempts || isImmediateFail(err)) throw err;
			}
			try {
				Thread.sleep(retryAttemptDelays[attempt-1]);
			} catch(InterruptedException err) {
				// Restore the interrupted status
				Thread.currentThread().interrupt();
				InterruptedIOException ioErr = new InterruptedIOException();
				ioErr.initCause(err);
				throw ioErr;
			}
			attempt++;
		}
		throw new InterruptedIOException();
	}

	final String requestStringQuery(boolean allowRetry, AOServProtocol.CommandID commID, Object ... params) throws IOException, SQLException {
		int attempt = 1;
		int attempts = allowRetry ? RETRY_ATTEMPTS : 1;
		while(!Thread.interrupted()) {
			try {
				AOServConnection connection=getConnection(1);
				try {
					CompressedDataOutputStream out=connection.getOutputStream();
					out.writeCompressedInt(commID.ordinal());
					writeParams(params, out);
					out.flush();

					CompressedDataInputStream in=connection.getInputStream();
					int code=in.readByte();
					if(code==AOServProtocol.DONE) return in.readUTF();
					AOServProtocol.checkResult(code, in);
					throw new IOException("Unexpected response code: "+code);
				} catch(RuntimeException | IOException err) {
					connection.close();
					throw err;
				} finally {
					releaseConnection(connection);
				}
			} catch(InterruptedIOException err) {
				throw err;
			} catch(RuntimeException | IOException | SQLException err) {
				if(Thread.interrupted() || attempt>=attempts || isImmediateFail(err)) throw err;
			}
			try {
				Thread.sleep(retryAttemptDelays[attempt-1]);
			} catch(InterruptedException err) {
				// Restore the interrupted status
				Thread.currentThread().interrupt();
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
	final String requestLongStringQuery(boolean allowRetry, AOServProtocol.CommandID commID, Object ... params) throws IOException, SQLException {
		int attempt = 1;
		int attempts = allowRetry ? RETRY_ATTEMPTS : 1;
		while(!Thread.interrupted()) {
			try {
				AOServConnection connection=getConnection(1);
				try {
					CompressedDataOutputStream out=connection.getOutputStream();
					out.writeCompressedInt(commID.ordinal());
					writeParams(params, out);
					out.flush();

					CompressedDataInputStream in=connection.getInputStream();
					int code=in.readByte();
					if(code==AOServProtocol.DONE) return in.readLongUTF();
					AOServProtocol.checkResult(code, in);
					throw new IOException("Unexpected response code: "+code);
				} catch(RuntimeException | IOException err) {
					connection.close();
					throw err;
				} finally {
					releaseConnection(connection);
				}
			} catch(InterruptedIOException err) {
				throw err;
			} catch(RuntimeException | IOException | SQLException err) {
				if(Thread.interrupted() || attempt>=attempts || isImmediateFail(err)) throw err;
			}
			try {
				Thread.sleep(retryAttemptDelays[attempt-1]);
			} catch(InterruptedException err) {
				// Restore the interrupted status
				Thread.currentThread().interrupt();
				InterruptedIOException ioErr = new InterruptedIOException();
				ioErr.initCause(err);
				throw ioErr;
			}
			attempt++;
		}
		throw new InterruptedIOException();
	}

	/**
	 * Performs a query returning a String of any length (not limited to size &lt;= 64k like requestStringQuery) or <code>null</code>.
	 * Supports nulls.
	 */
	final String requestNullLongStringQuery(boolean allowRetry, AOServProtocol.CommandID commID, Object ... params) throws IOException, SQLException {
		int attempt = 1;
		int attempts = allowRetry ? RETRY_ATTEMPTS : 1;
		while(!Thread.interrupted()) {
			try {
				AOServConnection connection=getConnection(1);
				try {
					CompressedDataOutputStream out=connection.getOutputStream();
					out.writeCompressedInt(commID.ordinal());
					writeParams(params, out);
					out.flush();

					CompressedDataInputStream in=connection.getInputStream();
					int code=in.readByte();
					if(code==AOServProtocol.DONE) return in.readNullLongUTF();
					AOServProtocol.checkResult(code, in);
					throw new IOException("Unexpected response code: "+code);
				} catch(RuntimeException | IOException err) {
					connection.close();
					throw err;
				} finally {
					releaseConnection(connection);
				}
			} catch(InterruptedIOException err) {
				throw err;
			} catch(RuntimeException | IOException | SQLException err) {
				if(Thread.interrupted() || attempt>=attempts || isImmediateFail(err)) throw err;
			}
			try {
				Thread.sleep(retryAttemptDelays[attempt-1]);
			} catch(InterruptedException err) {
				// Restore the interrupted status
				Thread.currentThread().interrupt();
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
	 * @see  #requestUpdate(boolean,UpdateRequest)
	 */
	interface UpdateRequest {
		/**
		 * Writes the request to the server.
		 * This does not need to flush the output stream.
		 */
		void writeRequest(CompressedDataOutputStream out) throws IOException;

		/**
		 * Reads the response from the server if the request was successfully sent.
		 */
		void readResponse(CompressedDataInputStream in) throws IOException, SQLException;

		/**
		 * If both the request and response were successful, this is called after the
		 * connection to the server is released.
		 */
		void afterRelease();
	}

	final void requestUpdate(boolean allowRetry, UpdateRequest updateRequest) throws IOException, SQLException {
		int attempt = 1;
		int attempts = allowRetry ? RETRY_ATTEMPTS : 1;
		while(!Thread.interrupted()) {
			try {
				AOServConnection connection=getConnection(1);
				try {
					CompressedDataOutputStream out=connection.getOutputStream();
					updateRequest.writeRequest(out);
					out.flush();

					CompressedDataInputStream in=connection.getInputStream();
					updateRequest.readResponse(in);
				} catch(RuntimeException | IOException err) {
					connection.close();
					throw err;
				} finally {
					releaseConnection(connection);
				}
				updateRequest.afterRelease();
				return;
			} catch(InterruptedIOException err) {
				throw err;
			} catch(RuntimeException | IOException | SQLException err) {
				if(Thread.interrupted() || attempt>=attempts || isImmediateFail(err)) throw err;
			}
			try {
				Thread.sleep(retryAttemptDelays[attempt-1]);
			} catch(InterruptedException err) {
				// Restore the interrupted status
				Thread.currentThread().interrupt();
				InterruptedIOException ioErr = new InterruptedIOException();
				ioErr.initCause(err);
				throw ioErr;
			}
			attempt++;
		}
		throw new InterruptedIOException();
	}

	final void requestUpdate(boolean allowRetry, AOServProtocol.CommandID commID, Object ... params) throws IOException, SQLException {
		int attempt = 1;
		int attempts = allowRetry ? RETRY_ATTEMPTS : 1;
		while(!Thread.interrupted()) {
			try {
				AOServConnection connection=getConnection(1);
				try {
					CompressedDataOutputStream out=connection.getOutputStream();
					out.writeCompressedInt(commID.ordinal());
					writeParams(params, out);
					out.flush();

					CompressedDataInputStream in=connection.getInputStream();
					int code=in.readByte();
					if(code!=AOServProtocol.DONE) AOServProtocol.checkResult(code, in);
				} catch(RuntimeException | IOException err) {
					connection.close();
					throw err;
				} finally {
					releaseConnection(connection);
				}
				return;
			} catch(InterruptedIOException err) {
				throw err;
			} catch(RuntimeException | IOException | SQLException err) {
				if(Thread.interrupted() || attempt>=attempts || isImmediateFail(err)) throw err;
			}
			try {
				Thread.sleep(retryAttemptDelays[attempt-1]);
			} catch(InterruptedException err) {
				// Restore the interrupted status
				Thread.currentThread().interrupt();
				InterruptedIOException ioErr = new InterruptedIOException();
				ioErr.initCause(err);
				throw ioErr;
			}
			attempt++;
		}
		throw new InterruptedIOException();
	}

	final void requestUpdateIL(boolean allowRetry, AOServProtocol.CommandID commID, Object ... params) throws IOException, SQLException {
		int attempt = 1;
		int attempts = allowRetry ? RETRY_ATTEMPTS : 1;
		while(!Thread.interrupted()) {
			try {
				IntList invalidateList;
				AOServConnection connection=getConnection(1);
				try {
					CompressedDataOutputStream out=connection.getOutputStream();
					out.writeCompressedInt(commID.ordinal());
					writeParams(params, out);
					out.flush();

					CompressedDataInputStream in=connection.getInputStream();
					int code=in.readByte();
					if(code==AOServProtocol.DONE) invalidateList=readInvalidateList(in);
					else {
						AOServProtocol.checkResult(code, in);
						throw new IOException("Unexpected response code: "+code);
					}
				} catch(RuntimeException | IOException err) {
					connection.close();
					throw err;
				} finally {
					releaseConnection(connection);
				}
				tablesUpdated(invalidateList);
				return;
			} catch(InterruptedIOException err) {
				throw err;
			} catch(RuntimeException | IOException | SQLException err) {
				if(Thread.interrupted() || attempt>=attempts || isImmediateFail(err)) throw err;
			}
			try {
				Thread.sleep(retryAttemptDelays[attempt-1]);
			} catch(InterruptedException err) {
				// Restore the interrupted status
				Thread.currentThread().interrupt();
				InterruptedIOException ioErr = new InterruptedIOException();
				ioErr.initCause(err);
				throw ioErr;
			}
			attempt++;
		}
		throw new InterruptedIOException();
	}

	public abstract AOServConnector switchUsers(UserId username) throws IOException;

	protected final void tablesUpdated(IntList invalidateList) {
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
	public final void testConnect() throws IOException, SQLException {
		synchronized(testConnectLock) {
			requestUpdate(
				true,
				new UpdateRequest() {
					@Override
					public void writeRequest(CompressedDataOutputStream out) throws IOException {
						out.writeCompressedInt(AOServProtocol.CommandID.TEST_CONNECTION.ordinal());
					}
					@Override
					public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
						int code=in.readByte();
						if(code!=AOServProtocol.DONE) {
							AOServProtocol.checkResult(code, in);
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
	void addingTableListener() {
	}

	/**
	 * Gets some entropy from the master server, returns the number of bytes actually obtained.
	 */
	public int getMasterEntropy(final byte[] buff, final int numBytes) throws IOException, SQLException {
		return requestResult(
			true,
			new ResultRequest<Integer>() {
				int numObtained;

				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(AOServProtocol.CommandID.GET_MASTER_ENTROPY.ordinal());
					out.writeCompressedInt(numBytes);
				}

				@Override
				public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AOServProtocol.DONE) {
						numObtained=in.readCompressedInt();
						for(int c=0;c<numObtained;c++) buff[c]=in.readByte();
					} else {
						AOServProtocol.checkResult(code, in);
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
		return requestLongQuery(true, AOServProtocol.CommandID.GET_MASTER_ENTROPY_NEEDED);
	}

	/**
	 * Adds some entropy to the master server.
	 */
	public void addMasterEntropy(final byte[] buff, final int numBytes) throws IOException, SQLException {
		requestUpdate(
			true,
			new UpdateRequest() {
				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(AOServProtocol.CommandID.ADD_MASTER_ENTROPY.ordinal());
					out.writeCompressedInt(numBytes);
					for(int c=0;c<numBytes;c++) out.writeByte(buff[c]);
				}
				@Override
				public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
					int code=in.readByte();
					if(code!=AOServProtocol.DONE) {
						AOServProtocol.checkResult(code, in);
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
