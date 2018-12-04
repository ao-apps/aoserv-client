/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2015, 2016, 2017, 2018  AO Industries, Inc.
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

import com.aoindustries.aoserv.client.account.AccountHostTable;
import com.aoindustries.aoserv.client.account.AccountTable;
import com.aoindustries.aoserv.client.account.Administrator;
import com.aoindustries.aoserv.client.account.AdministratorTable;
import com.aoindustries.aoserv.client.account.DisableLogTable;
import com.aoindustries.aoserv.client.account.ProfileTable;
import com.aoindustries.aoserv.client.account.UsStateTable;
import com.aoindustries.aoserv.client.account.UsernameTable;
import com.aoindustries.aoserv.client.accounting.BankAccountTable;
import com.aoindustries.aoserv.client.accounting.BankTable;
import com.aoindustries.aoserv.client.accounting.BankTransactionTable;
import com.aoindustries.aoserv.client.accounting.BankTransactionTypeTable;
import com.aoindustries.aoserv.client.accounting.ExpenseCategoryTable;
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.aosh.CommandTable;
import com.aoindustries.aoserv.client.backup.BackupPartitionTable;
import com.aoindustries.aoserv.client.backup.BackupReportTable;
import com.aoindustries.aoserv.client.backup.BackupRetentionTable;
import com.aoindustries.aoserv.client.backup.FileReplicationLogTable;
import com.aoindustries.aoserv.client.backup.FileReplicationScheduleTable;
import com.aoindustries.aoserv.client.backup.FileReplicationSettingTable;
import com.aoindustries.aoserv.client.backup.FileReplicationTable;
import com.aoindustries.aoserv.client.backup.MysqlReplicationTable;
import com.aoindustries.aoserv.client.billing.MonthlyChargeTable;
import com.aoindustries.aoserv.client.billing.NoticeLogTable;
import com.aoindustries.aoserv.client.billing.NoticeTypeTable;
import com.aoindustries.aoserv.client.billing.PackageCategoryTable;
import com.aoindustries.aoserv.client.billing.PackageDefinitionLimitTable;
import com.aoindustries.aoserv.client.billing.PackageDefinitionTable;
import com.aoindustries.aoserv.client.billing.PackageTable;
import com.aoindustries.aoserv.client.billing.ResourceTable;
import com.aoindustries.aoserv.client.billing.TransactionTable;
import com.aoindustries.aoserv.client.billing.TransactionTypeTable;
import com.aoindustries.aoserv.client.billing.WhoisHistoryAccountTable;
import com.aoindustries.aoserv.client.billing.WhoisHistoryTable;
import com.aoindustries.aoserv.client.distribution.ArchitectureTable;
import com.aoindustries.aoserv.client.distribution.OperatingSystemTable;
import com.aoindustries.aoserv.client.distribution.OperatingSystemVersionTable;
import com.aoindustries.aoserv.client.distribution.SoftwareCategorizationTable;
import com.aoindustries.aoserv.client.distribution.SoftwareCategoryTable;
import com.aoindustries.aoserv.client.distribution.SoftwareTable;
import com.aoindustries.aoserv.client.distribution.SoftwareVersionTable;
import com.aoindustries.aoserv.client.distribution.management.DistroFileTable;
import com.aoindustries.aoserv.client.distribution.management.DistroFileTypeTable;
import com.aoindustries.aoserv.client.distribution.management.DistroReportTypeTable;
import com.aoindustries.aoserv.client.dns.ForbiddenZoneTable;
import com.aoindustries.aoserv.client.dns.RecordTable;
import com.aoindustries.aoserv.client.dns.RecordTypeTable;
import com.aoindustries.aoserv.client.dns.TopLevelDomainTable;
import com.aoindustries.aoserv.client.dns.ZoneTable;
import com.aoindustries.aoserv.client.email.AddressTable;
import com.aoindustries.aoserv.client.email.AttachmentBlockTable;
import com.aoindustries.aoserv.client.email.AttachmentTypeTable;
import com.aoindustries.aoserv.client.email.BlackholeAddressTable;
import com.aoindustries.aoserv.client.email.CyrusImapdBindTable;
import com.aoindustries.aoserv.client.email.CyrusImapdServerTable;
import com.aoindustries.aoserv.client.email.DomainTable;
import com.aoindustries.aoserv.client.email.ForwardingTable;
import com.aoindustries.aoserv.client.email.InboxAddressTable;
import com.aoindustries.aoserv.client.email.ListAddressTable;
import com.aoindustries.aoserv.client.email.ListTable;
import com.aoindustries.aoserv.client.email.MajordomoListTable;
import com.aoindustries.aoserv.client.email.MajordomoServerTable;
import com.aoindustries.aoserv.client.email.MajordomoVersionTable;
import com.aoindustries.aoserv.client.email.PipeAddressTable;
import com.aoindustries.aoserv.client.email.PipeTable;
import com.aoindustries.aoserv.client.email.SendmailBindTable;
import com.aoindustries.aoserv.client.email.SendmailServerTable;
import com.aoindustries.aoserv.client.email.SmtpRelayTable;
import com.aoindustries.aoserv.client.email.SmtpRelayTypeTable;
import com.aoindustries.aoserv.client.email.SmtpSmartHostDomainTable;
import com.aoindustries.aoserv.client.email.SmtpSmartHostTable;
import com.aoindustries.aoserv.client.email.SpamAssassinModeTable;
import com.aoindustries.aoserv.client.email.SpamMessageTable;
import com.aoindustries.aoserv.client.email.SystemAliasTable;
import com.aoindustries.aoserv.client.ftp.GuestUserTable;
import com.aoindustries.aoserv.client.ftp.PrivateServerTable;
import com.aoindustries.aoserv.client.infrastructure.PhysicalServerTable;
import com.aoindustries.aoserv.client.infrastructure.ProcessorTypeTable;
import com.aoindustries.aoserv.client.infrastructure.RackTable;
import com.aoindustries.aoserv.client.infrastructure.ServerFarmTable;
import com.aoindustries.aoserv.client.infrastructure.VirtualDiskTable;
import com.aoindustries.aoserv.client.infrastructure.VirtualServerTable;
import com.aoindustries.aoserv.client.linux.DaemonAclTable;
import com.aoindustries.aoserv.client.linux.GroupServerTable;
import com.aoindustries.aoserv.client.linux.GroupTable;
import com.aoindustries.aoserv.client.linux.GroupTypeTable;
import com.aoindustries.aoserv.client.linux.GroupUserTable;
import com.aoindustries.aoserv.client.linux.ServerTable;
import com.aoindustries.aoserv.client.linux.ShellTable;
import com.aoindustries.aoserv.client.linux.TimeZoneTable;
import com.aoindustries.aoserv.client.linux.UserServerTable;
import com.aoindustries.aoserv.client.linux.UserTable;
import com.aoindustries.aoserv.client.linux.UserTypeTable;
import com.aoindustries.aoserv.client.master.AdministratorPermissionTable;
import com.aoindustries.aoserv.client.master.PermissionTable;
import com.aoindustries.aoserv.client.master.ProcessTable;
import com.aoindustries.aoserv.client.master.ServerStatTable;
import com.aoindustries.aoserv.client.master.UserAclTable;
import com.aoindustries.aoserv.client.master.UserHostTable;
import com.aoindustries.aoserv.client.mysql.DatabaseTable;
import com.aoindustries.aoserv.client.mysql.DatabaseUserTable;
import com.aoindustries.aoserv.client.net.AppProtocolTable;
import com.aoindustries.aoserv.client.net.BindFirewallZoneTable;
import com.aoindustries.aoserv.client.net.BindTable;
import com.aoindustries.aoserv.client.net.DeviceIdTable;
import com.aoindustries.aoserv.client.net.DeviceTable;
import com.aoindustries.aoserv.client.net.FirewallZoneTable;
import com.aoindustries.aoserv.client.net.HostTable;
import com.aoindustries.aoserv.client.net.IpAddressTable;
import com.aoindustries.aoserv.client.net.TcpRedirectTable;
import com.aoindustries.aoserv.client.net.monitoring.IpAddressMonitoringTable;
import com.aoindustries.aoserv.client.net.reputation.LimiterClassTable;
import com.aoindustries.aoserv.client.net.reputation.LimiterSetTable;
import com.aoindustries.aoserv.client.net.reputation.LimiterTable;
import com.aoindustries.aoserv.client.net.reputation.NetworkTable;
import com.aoindustries.aoserv.client.net.reputation.SetTable;
import com.aoindustries.aoserv.client.payment.CountryCodeTable;
import com.aoindustries.aoserv.client.payment.CreditCardTable;
import com.aoindustries.aoserv.client.payment.PaymentTable;
import com.aoindustries.aoserv.client.payment.PaymentTypeTable;
import com.aoindustries.aoserv.client.payment.ProcessorTable;
import com.aoindustries.aoserv.client.pki.CertificateNameTable;
import com.aoindustries.aoserv.client.pki.CertificateOtherUseTable;
import com.aoindustries.aoserv.client.pki.CertificateTable;
import com.aoindustries.aoserv.client.pki.EncryptionKeyTable;
import com.aoindustries.aoserv.client.postgresql.EncodingTable;
import com.aoindustries.aoserv.client.postgresql.VersionTable;
import com.aoindustries.aoserv.client.reseller.BrandCategoryTable;
import com.aoindustries.aoserv.client.reseller.BrandTable;
import com.aoindustries.aoserv.client.reseller.CategoryTable;
import com.aoindustries.aoserv.client.reseller.ResellerTable;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.AoservProtocolTable;
import com.aoindustries.aoserv.client.schema.ColumnTable;
import com.aoindustries.aoserv.client.schema.ForeignKeyTable;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.schema.TableTable;
import com.aoindustries.aoserv.client.schema.TypeTable;
import com.aoindustries.aoserv.client.scm.CvsRepositoryTable;
import com.aoindustries.aoserv.client.signup.OptionTable;
import com.aoindustries.aoserv.client.signup.RequestTable;
import com.aoindustries.aoserv.client.ticket.ActionTable;
import com.aoindustries.aoserv.client.ticket.ActionTypeTable;
import com.aoindustries.aoserv.client.ticket.AssignmentTable;
import com.aoindustries.aoserv.client.ticket.LanguageTable;
import com.aoindustries.aoserv.client.ticket.PriorityTable;
import com.aoindustries.aoserv.client.ticket.StatusTable;
import com.aoindustries.aoserv.client.ticket.TicketTable;
import com.aoindustries.aoserv.client.ticket.TicketTypeTable;
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
import com.aoindustries.aoserv.client.web.HeaderTable;
import com.aoindustries.aoserv.client.web.HttpdBindTable;
import com.aoindustries.aoserv.client.web.HttpdServerTable;
import com.aoindustries.aoserv.client.web.LocationTable;
import com.aoindustries.aoserv.client.web.RedirectTable;
import com.aoindustries.aoserv.client.web.SiteTable;
import com.aoindustries.aoserv.client.web.StaticSiteTable;
import com.aoindustries.aoserv.client.web.VirtualHostNameTable;
import com.aoindustries.aoserv.client.web.VirtualHostTable;
import com.aoindustries.aoserv.client.web.tomcat.ContextDataSourceTable;
import com.aoindustries.aoserv.client.web.tomcat.ContextParameterTable;
import com.aoindustries.aoserv.client.web.tomcat.ContextTable;
import com.aoindustries.aoserv.client.web.tomcat.JkMountTable;
import com.aoindustries.aoserv.client.web.tomcat.JkProtocolTable;
import com.aoindustries.aoserv.client.web.tomcat.PrivateTomcatSiteTable;
import com.aoindustries.aoserv.client.web.tomcat.SharedTomcatSiteTable;
import com.aoindustries.aoserv.client.web.tomcat.SharedTomcatTable;
import com.aoindustries.aoserv.client.web.tomcat.WorkerNameTable;
import com.aoindustries.aoserv.client.web.tomcat.WorkerTable;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.io.CompressedWritable;
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
	public Logger getLogger() {
		return logger;
	}

	/**
	 * Gets the logger for this connector.
	 */
	//public Logger getLogger() {
	//    return logger;
	//}

	protected final String password;

	private static class TestConnectLock {}
	private final TestConnectLock testConnectLock=new TestConnectLock();

	private final DaemonAclTable aoServerDaemonHosts;
	public DaemonAclTable getAoServerDaemonHosts() {return aoServerDaemonHosts;}

	private final ServerTable aoServers;
	public ServerTable getAoServers() {return aoServers;}

	private final PermissionTable aoservPermissions;
	public PermissionTable getAoservPermissions() {return aoservPermissions;}

	private final AoservProtocolTable aoservProtocols;
	public AoservProtocolTable getAoservProtocols() {
		return aoservProtocols;
	}

	private final CommandTable aoshCommands;
	public CommandTable getAoshCommands() {
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

	private final BlackholeAddressTable blackholeEmailAddresses;
	public BlackholeAddressTable getBlackholeEmailAddresses() {
		return blackholeEmailAddresses;
	}

	private final BrandTable brands;
	public BrandTable getBrands() {
		return brands;
	}

	private final AdministratorTable businessAdministrators;
	public AdministratorTable getBusinessAdministrators() {
		return businessAdministrators;
	}

	private final AdministratorPermissionTable businessAdministratorPermissions;
	public AdministratorPermissionTable getBusinessAdministratorPermissions() {return businessAdministratorPermissions;}

	private final ProfileTable businessProfiles;
	public ProfileTable getBusinessProfiles() {
		return businessProfiles;
	}

	private final AccountTable businesses;
	public AccountTable getBusinesses() {
		return businesses;
	}

	private final AccountHostTable businessServers;
	public AccountHostTable getBusinessServers() {
		return businessServers;
	}

	private final CountryCodeTable countryCodes;
	public CountryCodeTable getCountryCodes() {
		return countryCodes;
	}

	private final ProcessorTable creditCardProcessors;
	public ProcessorTable getCreditCardProcessors() {return creditCardProcessors;}

	private final PaymentTable creditCardTransactions;
	public PaymentTable getCreditCardTransactions() {return creditCardTransactions;}

	private final CreditCardTable creditCards;
	public CreditCardTable getCreditCards() {return creditCards;}

	private final CvsRepositoryTable cvsRepositories;
	public CvsRepositoryTable getCvsRepositories() {
		return cvsRepositories;
	}

	private final CyrusImapdBindTable cyrusImapdBinds;
	public CyrusImapdBindTable getCyrusImapdBinds() {
		return cyrusImapdBinds;
	}

	private final CyrusImapdServerTable cyrusImapdServers;
	public CyrusImapdServerTable getCyrusImapdServers() {
		return cyrusImapdServers;
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

	private final ForbiddenZoneTable dnsForbiddenZones;
	public ForbiddenZoneTable getDnsForbiddenZones() {
		return dnsForbiddenZones;
	}

	private final RecordTable dnsRecords;
	public RecordTable getDnsRecords() {
		return dnsRecords;
	}

	private final TopLevelDomainTable dnsTLDs;
	public TopLevelDomainTable getDnsTLDs() {
		return dnsTLDs;
	}

	private final RecordTypeTable dnsTypes;
	public RecordTypeTable getDnsTypes() {
		return dnsTypes;
	}

	private final ZoneTable dnsZones;
	public ZoneTable getDnsZones() {
		return dnsZones;
	}

	private final AddressTable emailAddresses;
	public AddressTable getEmailAddresses() {
		return emailAddresses;
	}

	private final AttachmentBlockTable emailAttachmentBlocks;
	public AttachmentBlockTable getEmailAttachmentBlocks() {
		return emailAttachmentBlocks;
	}

	private final AttachmentTypeTable emailAttachmentTypes;
	public AttachmentTypeTable getEmailAttachmentTypes() {
		return emailAttachmentTypes;
	}

	private final DomainTable emailDomains;
	public DomainTable getEmailDomains() {
		return emailDomains;
	}

	private final ForwardingTable emailForwardings;
	public ForwardingTable getEmailForwardings() {
		return emailForwardings;
	}

	private final ListAddressTable emailListAddresses;
	public ListAddressTable getEmailListAddresses() {
		return emailListAddresses;
	}

	private final ListTable emailLists;
	public ListTable getEmailLists() {
		return emailLists;
	}

	private final PipeAddressTable emailPipeAddresses;
	public PipeAddressTable getEmailPipeAddresses() {
		return emailPipeAddresses;
	}

	private final PipeTable emailPipes;
	public PipeTable getEmailPipes() {
		return emailPipes;
	}

	private final SmtpRelayTypeTable emailSmtpRelayTypes;
	public SmtpRelayTypeTable getEmailSmtpRelayTypes() {
		return emailSmtpRelayTypes;
	}

	private final SmtpRelayTable emailSmtpRelays;
	public SmtpRelayTable getEmailSmtpRelays() {
		return emailSmtpRelays;
	}

	private final SmtpSmartHostDomainTable emailSmtpSmartHostDomainTable;
	public SmtpSmartHostDomainTable getEmailSmtpSmartHostDomains() {
		return emailSmtpSmartHostDomainTable;
	}

	private final SmtpSmartHostTable emailSmtpSmartHostTable;
	public SmtpSmartHostTable getEmailSmtpSmartHosts() {
		return emailSmtpSmartHostTable;
	}

	private final SpamAssassinModeTable emailSpamAssassinIntegrationModes;
	public SpamAssassinModeTable getEmailSpamAssassinIntegrationModes() {
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

	private final FileReplicationLogTable failoverFileLogs;
	public FileReplicationLogTable getFailoverFileLogs() {
		return failoverFileLogs;
	}

	private final FileReplicationTable failoverFileReplications;
	public FileReplicationTable getFailoverFileReplications() {
		return failoverFileReplications;
	}

	private final FileReplicationScheduleTable failoverFileSchedules;
	public FileReplicationScheduleTable getFailoverFileSchedules() {
		return failoverFileSchedules;
	}

	private final MysqlReplicationTable failoverMySQLReplications;
	public MysqlReplicationTable getFailoverMySQLReplications() {return failoverMySQLReplications;}

	private final FileReplicationSettingTable fileBackupSettings;
	public FileReplicationSettingTable getFileBackupSettings() {
		return fileBackupSettings;
	}

	private final FirewallZoneTable firewalldZones;
	public FirewallZoneTable getFirewalldZones() {
		return firewalldZones;
	}

	private final GuestUserTable ftpGuestUsers;
	public GuestUserTable getFtpGuestUsers() {
		return ftpGuestUsers;
	}

	private final HttpdBindTable httpdBinds;
	public HttpdBindTable getHttpdBinds() {
		return httpdBinds;
	}

	private final com.aoindustries.aoserv.client.web.jboss.SiteTable httpdJBossSites;
	public com.aoindustries.aoserv.client.web.jboss.SiteTable getHttpdJBossSites() {
		return httpdJBossSites;
	}

	private final com.aoindustries.aoserv.client.web.jboss.VersionTable httpdJBossVersions;
	public com.aoindustries.aoserv.client.web.jboss.VersionTable getHttpdJBossVersions() {
		return httpdJBossVersions;
	}

	private final WorkerNameTable httpdJKCodes;
	public WorkerNameTable getHttpdJKCodes() {
		return httpdJKCodes;
	}

	private final JkProtocolTable httpdJKProtocols;
	public JkProtocolTable getHttpdJKProtocols() {
		return httpdJKProtocols;
	}

	private final HttpdServerTable httpdServers;
	public HttpdServerTable getHttpdServers() {
		return httpdServers;
	}

	private final SharedTomcatTable httpdSharedTomcats;
	public SharedTomcatTable getHttpdSharedTomcats() {
		return httpdSharedTomcats;
	}

	private final LocationTable httpdSiteAuthenticatedLocationTable;
	public LocationTable getHttpdSiteAuthenticatedLocationTable() {
		return httpdSiteAuthenticatedLocationTable;
	}

	private final HeaderTable httpdSiteBindHeaders;
	public HeaderTable getHttpdSiteBindHeaders() {
		return httpdSiteBindHeaders;
	}

	private final RedirectTable httpdSiteBindRedirects;
	public RedirectTable getHttpdSiteBindRedirects() {
		return httpdSiteBindRedirects;
	}

	private final VirtualHostTable httpdSiteBinds;
	public VirtualHostTable getHttpdSiteBinds() {
		return httpdSiteBinds;
	}

	private final VirtualHostNameTable httpdSiteURLs;
	public VirtualHostNameTable getHttpdSiteURLs() {
		return httpdSiteURLs;
	}

	private final SiteTable httpdSites;
	public SiteTable getHttpdSites() {return httpdSites;}

	private final StaticSiteTable httpdStaticSites;
	public StaticSiteTable getHttpdStaticSites() {
		return httpdStaticSites;
	}

	private final ContextTable httpdTomcatContexts;
	public ContextTable getHttpdTomcatContexts() {
		return httpdTomcatContexts;
	}

	private final ContextDataSourceTable httpdTomcatDataSources;
	public ContextDataSourceTable getHttpdTomcatDataSources() {
		return httpdTomcatDataSources;
	}

	private final ContextParameterTable httpdTomcatParameters;
	public ContextParameterTable getHttpdTomcatParameters() {
		return httpdTomcatParameters;
	}

	private final JkMountTable httpdTomcatSiteJkMounts;
	public JkMountTable getHttpdTomcatSiteJkMounts() {
		return httpdTomcatSiteJkMounts;
	}

	private final com.aoindustries.aoserv.client.web.tomcat.SiteTable httpdTomcatSites;
	public com.aoindustries.aoserv.client.web.tomcat.SiteTable getHttpdTomcatSites() {
		return httpdTomcatSites;
	}

	private final SharedTomcatSiteTable httpdTomcatSharedSites;
	public SharedTomcatSiteTable getHttpdTomcatSharedSites() {
		return httpdTomcatSharedSites;
	}

	private final PrivateTomcatSiteTable httpdTomcatStdSites;
	public PrivateTomcatSiteTable getHttpdTomcatStdSites() {
		return httpdTomcatStdSites;
	}

	private final com.aoindustries.aoserv.client.web.tomcat.VersionTable httpdTomcatVersions;
	public com.aoindustries.aoserv.client.web.tomcat.VersionTable getHttpdTomcatVersions() {
		return httpdTomcatVersions;
	}

	private final WorkerTable httpdWorkers;
	public WorkerTable getHttpdWorkers() {
		return httpdWorkers;
	}

	private final IpAddressTable ipAddresses;
	public IpAddressTable getIpAddresses() {
		return ipAddresses;
	}

	private final IpAddressMonitoringTable ipAddressMonitoring;
	public IpAddressMonitoringTable getIpAddressMonitoring() {
		return ipAddressMonitoring;
	}

	private final LimiterClassTable ipReputationLimiterLimits;
	public LimiterClassTable getIpReputationLimiterLimits() {
		return ipReputationLimiterLimits;
	}

	private final LimiterSetTable ipReputationLimiterSets;
	public LimiterSetTable getIpReputationLimiterSets() {
		return ipReputationLimiterSets;
	}

	private final LimiterTable ipReputationLimiters;
	public LimiterTable getIpReputationLimiters() {
		return ipReputationLimiters;
	}

	private final com.aoindustries.aoserv.client.net.reputation.HostTable ipReputationSetHosts;
	public com.aoindustries.aoserv.client.net.reputation.HostTable getIpReputationSetHosts() {
		return ipReputationSetHosts;
	}

	private final NetworkTable ipReputationSetNetworks;
	public NetworkTable getIpReputationSetNetworks() {
		return ipReputationSetNetworks;
	}

	private final SetTable ipReputationSets;
	public SetTable getIpReputationSets() {
		return ipReputationSets;
	}

	private final LanguageTable languages;
	public LanguageTable getLanguages() {
		return languages;
	}

	private final InboxAddressTable linuxAccAddresses;
	public InboxAddressTable getLinuxAccAddresses() {
		return linuxAccAddresses;
	}

	private final UserTypeTable linuxAccountTypes;
	public UserTypeTable getLinuxAccountTypes() {
		return linuxAccountTypes;
	}

	private final UserTable linuxAccounts;
	public UserTable getLinuxAccounts() {
		return linuxAccounts;
	}

	private final GroupUserTable linuxGroupAccounts;
	public GroupUserTable getLinuxGroupAccounts() {
		return linuxGroupAccounts;
	}

	private final GroupTypeTable linuxGroupTypes;
	public GroupTypeTable getLinuxGroupTypes() {
		return linuxGroupTypes;
	}

	private final GroupTable linuxGroups;
	public GroupTable getLinuxGroups() {
		return linuxGroups;
	}

	private final UserServerTable linuxServerAccounts;
	public UserServerTable getLinuxServerAccounts() {
		return linuxServerAccounts;
	}

	private final GroupServerTable linuxServerGroups;
	public GroupServerTable getLinuxServerGroups() {
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

	private final UserAclTable masterHosts;
	public UserAclTable getMasterHosts() {
		return masterHosts;
	}

	private final ProcessTable masterProcesses;
	public ProcessTable getMasterProcesses() {
		return masterProcesses;
	}

	private final ServerStatTable masterServerStats;
	public ServerStatTable getMasterServerStats() {
		return masterServerStats;
	}

	private final UserHostTable masterServers;
	public UserHostTable getMasterServers() {
		return masterServers;
	}

	private final com.aoindustries.aoserv.client.master.UserTable masterUsers;
	public com.aoindustries.aoserv.client.master.UserTable getMasterUsers() {
		return masterUsers;
	}

	private final MonthlyChargeTable monthlyCharges;
	public MonthlyChargeTable getMonthlyCharges() {
		return monthlyCharges;
	}

	private final DatabaseTable mysqlDatabases;
	public DatabaseTable getMysqlDatabases() {
		return mysqlDatabases;
	}

	private final DatabaseUserTable mysqlDBUsers;
	public DatabaseUserTable getMysqlDBUsers() {
		return mysqlDBUsers;
	}

	private final com.aoindustries.aoserv.client.mysql.UserServerTable mysqlServerUsers;
	public com.aoindustries.aoserv.client.mysql.UserServerTable getMysqlServerUsers() {
		return mysqlServerUsers;
	}

	private final com.aoindustries.aoserv.client.mysql.ServerTable mysqlServers;
	public com.aoindustries.aoserv.client.mysql.ServerTable getMysqlServers() {return mysqlServers;}

	private final com.aoindustries.aoserv.client.mysql.UserTable mysqlUsers;
	public com.aoindustries.aoserv.client.mysql.UserTable getMysqlUsers() {
		return mysqlUsers;
	}

	private final BindFirewallZoneTable netBindFirewalldZones;
	public BindFirewallZoneTable getNetBindFirewalldZones() {
		return netBindFirewalldZones;
	}

	private final BindTable netBinds;
	public BindTable getNetBinds() {
		return netBinds;
	}

	private final DeviceIdTable netDeviceIDs;
	public DeviceIdTable getNetDeviceIDs() {
		return netDeviceIDs;
	}

	private final DeviceTable netDevices;
	public DeviceTable getNetDevices() {
		return netDevices;
	}

	private final TcpRedirectTable netTcpRedirects;
	public TcpRedirectTable getNetTcpRedirects() {
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

	private final com.aoindustries.aoserv.client.postgresql.DatabaseTable postgresDatabases;
	public com.aoindustries.aoserv.client.postgresql.DatabaseTable getPostgresDatabases() {
		return postgresDatabases;
	}

	private final EncodingTable postgresEncodings;
	public EncodingTable getPostgresEncodings() {
		return postgresEncodings;
	}

	private final com.aoindustries.aoserv.client.postgresql.UserServerTable postgresServerUsers;
	public com.aoindustries.aoserv.client.postgresql.UserServerTable getPostgresServerUsers() {
		return postgresServerUsers;
	}

	private final com.aoindustries.aoserv.client.postgresql.ServerTable postgresServers;
	public com.aoindustries.aoserv.client.postgresql.ServerTable getPostgresServers() {return postgresServers;}

	private final com.aoindustries.aoserv.client.postgresql.UserTable postgresUsers;
	public com.aoindustries.aoserv.client.postgresql.UserTable getPostgresUsers() {
		return postgresUsers;
	}

	private final VersionTable postgresVersions;
	public VersionTable getPostgresVersions() {
		return postgresVersions;
	}

	private final PrivateServerTable privateFTPServers;
	public PrivateServerTable getPrivateFTPServers() {
		return privateFTPServers;
	}

	private final ProcessorTypeTable processorTypes;
	public ProcessorTypeTable getProcessorTypes() {return processorTypes;}

	private final AppProtocolTable protocols;
	public AppProtocolTable getProtocols() {
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

	private final ColumnTable schemaColumns;
	public ColumnTable getSchemaColumns() {
		return schemaColumns;
	}

	private final ForeignKeyTable schemaForeignKeys;
	public ForeignKeyTable getSchemaForeignKeys() {
		return schemaForeignKeys;
	}

	private final TableTable schemaTables;
	public TableTable getSchemaTables() {
		return schemaTables;
	}

	private final TypeTable schemaTypes;
	public TypeTable getSchemaTypes() {
		return schemaTypes;
	}

	private final SendmailBindTable sendmailBinds;
	public SendmailBindTable getSendmailBinds() {
		return sendmailBinds;
	}

	private final SendmailServerTable sendmailServers;
	public SendmailServerTable getSendmailServers() {
		return sendmailServers;
	}

	private final ServerFarmTable serverFarms;
	public ServerFarmTable getServerFarms() {
		return serverFarms;
	}

	private final HostTable servers;
	public HostTable getServers() {
		return servers;
	}

	private final ShellTable shells;
	public ShellTable getShells() {
		return shells;
	}

	private final OptionTable signupRequestOptions;
	public OptionTable getSignupRequestOptions() {
		return signupRequestOptions;
	}

	private final RequestTable signupRequests;
	public RequestTable getSignupRequests() {
		return signupRequests;
	}

	private final SpamMessageTable spamEmailMessages;
	public SpamMessageTable getSpamEmailMessages() {
		return spamEmailMessages;
	}

	private final CertificateNameTable sslCertificateNames;
	public CertificateNameTable getSslCertificateNames() {
		return sslCertificateNames;
	}

	private final CertificateOtherUseTable sslCertificateOtherUses;
	public CertificateOtherUseTable getSslCertificateOtherUses() {
		return sslCertificateOtherUses;
	}

	private final CertificateTable sslCertificates;
	public CertificateTable getSslCertificates() {
		return sslCertificates;
	}

	private final SystemAliasTable systemEmailAliases;
	public SystemAliasTable getSystemEmailAliases() {
		return systemEmailAliases;
	}

	private final SoftwareCategorizationTable technologies;
	public SoftwareCategorizationTable getTechnologies() {
		return technologies;
	}

	private final SoftwareCategoryTable technologyClasses;
	public SoftwareCategoryTable getTechnologyClasses() {
		return technologyClasses;
	}

	private final SoftwareTable technologyNames;
	public SoftwareTable getTechnologyNames() {
		return technologyNames;
	}

	private final SoftwareVersionTable technologyVersions;
	public SoftwareVersionTable getTechnologyVersions() {
		return technologyVersions;
	}

	private final ActionTypeTable ticketActionTypes;
	public ActionTypeTable getTicketActionTypes() {
		return ticketActionTypes;
	}

	private final ActionTable ticketActions;
	public ActionTable getTicketActions() {
		return ticketActions;
	}

	private final AssignmentTable ticketAssignments;
	public AssignmentTable getTicketAssignments() {
		return ticketAssignments;
	}

	private final BrandCategoryTable ticketBrandCategories;
	public BrandCategoryTable getTicketBrandCategories() {
		return ticketBrandCategories;
	}

	private final CategoryTable ticketCategories;
	public CategoryTable getTicketCategories() {
		return ticketCategories;
	}

	private final PriorityTable ticketPriorities;
	public PriorityTable getTicketPriorities() {
		return ticketPriorities;
	}

	private final StatusTable ticketStatuses;
	public StatusTable getTicketStatuses() {
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

	private final UsStateTable usStates;
	public UsStateTable getUsStates() {
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

	private final WhoisHistoryAccountTable whoisHistoryAccount;
	public WhoisHistoryAccountTable getWhoisHistoryAccount() {return whoisHistoryAccount;}

	private final SimpleAOClient simpleAOClient;
	public SimpleAOClient getSimpleAOClient() {return simpleAOClient;}

	/**
	 * The tables are placed in this list in the constructor.
	 * This list is aligned with the table identifiers in
	 * <code>SchemaTable</code>.
	 *
	 * @see  Table
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
		newTables.add(aoServerDaemonHosts=new DaemonAclTable(this));
		newTables.add(aoServers=new ServerTable(this));
		newTables.add(aoservPermissions=new PermissionTable(this));
		newTables.add(aoservProtocols=new AoservProtocolTable(this));
		newTables.add(aoshCommands=new CommandTable(this));
		newTables.add(architectures=new ArchitectureTable(this));
		newTables.add(backupPartitions=new BackupPartitionTable(this));
		newTables.add(backupReports=new BackupReportTable(this));
		newTables.add(backupRetentions=new BackupRetentionTable(this));
		newTables.add(bankAccounts=new BankAccountTable(this));
		newTables.add(bankTransactionTypes=new BankTransactionTypeTable(this));
		newTables.add(bankTransactions=new BankTransactionTable(this));
		newTables.add(banks=new BankTable(this));
		newTables.add(blackholeEmailAddresses=new BlackholeAddressTable(this));
		newTables.add(brands=new BrandTable(this));
		newTables.add(businessAdministrators=new AdministratorTable(this));
		newTables.add(businessAdministratorPermissions=new AdministratorPermissionTable(this));
		newTables.add(businessProfiles=new ProfileTable(this));
		newTables.add(businesses=new AccountTable(this));
		newTables.add(businessServers=new AccountHostTable(this));
		newTables.add(countryCodes=new CountryCodeTable(this));
		newTables.add(creditCardProcessors=new ProcessorTable(this));
		newTables.add(creditCardTransactions=new PaymentTable(this));
		newTables.add(creditCards=new CreditCardTable(this));
		newTables.add(cvsRepositories=new CvsRepositoryTable(this));
		newTables.add(cyrusImapdBinds=new CyrusImapdBindTable(this));
		newTables.add(cyrusImapdServers=new CyrusImapdServerTable(this));
		newTables.add(disableLogs=new DisableLogTable(this));
		newTables.add(distroFileTypes=new DistroFileTypeTable(this));
		newTables.add(distroFiles=new DistroFileTable(this));
		newTables.add(distroReportTypes=new DistroReportTypeTable(this));
		newTables.add(dnsForbiddenZones=new ForbiddenZoneTable(this));
		newTables.add(dnsRecords=new RecordTable(this));
		newTables.add(dnsTLDs=new TopLevelDomainTable(this));
		newTables.add(dnsTypes=new RecordTypeTable(this));
		newTables.add(dnsZones=new ZoneTable(this));
		newTables.add(emailAddresses=new AddressTable(this));
		newTables.add(emailAttachmentBlocks=new AttachmentBlockTable(this));
		newTables.add(emailAttachmentTypes=new AttachmentTypeTable(this));
		newTables.add(emailDomains=new DomainTable(this));
		newTables.add(emailForwardings=new ForwardingTable(this));
		newTables.add(emailListAddresses=new ListAddressTable(this));
		newTables.add(emailLists=new ListTable(this));
		newTables.add(emailPipeAddresses=new PipeAddressTable(this));
		newTables.add(emailPipes=new PipeTable(this));
		newTables.add(emailSmtpRelayTypes=new SmtpRelayTypeTable(this));
		newTables.add(emailSmtpRelays=new SmtpRelayTable(this));
		newTables.add(emailSmtpSmartHostDomainTable=new SmtpSmartHostDomainTable(this));
		newTables.add(emailSmtpSmartHostTable=new SmtpSmartHostTable(this));
		newTables.add(emailSpamAssassinIntegrationModes=new SpamAssassinModeTable(this));
		newTables.add(encryptionKeys=new EncryptionKeyTable(this));
		newTables.add(expenseCategories=new ExpenseCategoryTable(this));
		newTables.add(failoverFileLogs=new FileReplicationLogTable(this));
		newTables.add(failoverFileReplications=new FileReplicationTable(this));
		newTables.add(failoverFileSchedules=new FileReplicationScheduleTable(this));
		newTables.add(failoverMySQLReplications=new MysqlReplicationTable(this));
		newTables.add(fileBackupSettings=new FileReplicationSettingTable(this));
		newTables.add(firewalldZones=new FirewallZoneTable(this));
		newTables.add(ftpGuestUsers=new GuestUserTable(this));
		newTables.add(httpdBinds=new HttpdBindTable(this));
		newTables.add(httpdJBossSites=new com.aoindustries.aoserv.client.web.jboss.SiteTable(this));
		newTables.add(httpdJBossVersions=new com.aoindustries.aoserv.client.web.jboss.VersionTable(this));
		newTables.add(httpdJKCodes=new WorkerNameTable(this));
		newTables.add(httpdJKProtocols=new JkProtocolTable(this));
		newTables.add(httpdServers=new HttpdServerTable(this));
		newTables.add(httpdSharedTomcats=new SharedTomcatTable(this));
		newTables.add(httpdSiteAuthenticatedLocationTable=new LocationTable(this));
		newTables.add(httpdSiteBindHeaders=new HeaderTable(this));
		newTables.add(httpdSiteBindRedirects=new RedirectTable(this));
		newTables.add(httpdSiteBinds=new VirtualHostTable(this));
		newTables.add(httpdSiteURLs=new VirtualHostNameTable(this));
		newTables.add(httpdSites=new SiteTable(this));
		newTables.add(httpdStaticSites=new StaticSiteTable(this));
		newTables.add(httpdTomcatContexts=new ContextTable(this));
		newTables.add(httpdTomcatDataSources=new ContextDataSourceTable(this));
		newTables.add(httpdTomcatParameters=new ContextParameterTable(this));
		newTables.add(httpdTomcatSiteJkMounts=new JkMountTable(this));
		newTables.add(httpdTomcatSites=new com.aoindustries.aoserv.client.web.tomcat.SiteTable(this));
		newTables.add(httpdTomcatSharedSites=new SharedTomcatSiteTable(this));
		newTables.add(httpdTomcatStdSites=new PrivateTomcatSiteTable(this));
		newTables.add(httpdTomcatVersions=new com.aoindustries.aoserv.client.web.tomcat.VersionTable(this));
		newTables.add(httpdWorkers=new WorkerTable(this));
		newTables.add(ipAddresses=new IpAddressTable(this));
		newTables.add(ipAddressMonitoring=new IpAddressMonitoringTable(this));
		newTables.add(ipReputationLimiterLimits=new LimiterClassTable(this));
		newTables.add(ipReputationLimiterSets=new LimiterSetTable(this));
		newTables.add(ipReputationLimiters=new LimiterTable(this));
		newTables.add(ipReputationSetHosts=new com.aoindustries.aoserv.client.net.reputation.HostTable(this));
		newTables.add(ipReputationSetNetworks=new NetworkTable(this));
		newTables.add(ipReputationSets=new SetTable(this));
		newTables.add(languages=new LanguageTable(this));
		newTables.add(linuxAccAddresses=new InboxAddressTable(this));
		newTables.add(linuxAccountTypes=new UserTypeTable(this));
		newTables.add(linuxAccounts=new UserTable(this));
		newTables.add(linuxGroupAccounts=new GroupUserTable(this));
		newTables.add(linuxGroupTypes=new GroupTypeTable(this));
		newTables.add(linuxGroups=new GroupTable(this));
		newTables.add(linuxServerAccounts=new UserServerTable(this));
		newTables.add(linuxServerGroups=new GroupServerTable(this));
		newTables.add(majordomoLists=new MajordomoListTable(this));
		newTables.add(majordomoServers=new MajordomoServerTable(this));
		newTables.add(majordomoVersions=new MajordomoVersionTable(this));
		newTables.add(masterHosts=new UserAclTable(this));
		newTables.add(masterProcesses=new ProcessTable(this));
		newTables.add(masterServerStats=new ServerStatTable(this));
		newTables.add(masterServers=new UserHostTable(this));
		newTables.add(masterUsers=new com.aoindustries.aoserv.client.master.UserTable(this));
		newTables.add(monthlyCharges=new MonthlyChargeTable(this));
		newTables.add(mysqlDatabases=new DatabaseTable(this));
		newTables.add(mysqlDBUsers=new DatabaseUserTable(this));
		newTables.add(mysqlServerUsers=new com.aoindustries.aoserv.client.mysql.UserServerTable(this));
		newTables.add(mysqlServers=new com.aoindustries.aoserv.client.mysql.ServerTable(this));
		newTables.add(mysqlUsers=new com.aoindustries.aoserv.client.mysql.UserTable(this));
		newTables.add(netBindFirewalldZones=new BindFirewallZoneTable(this));
		newTables.add(netBinds=new BindTable(this));
		newTables.add(netDeviceIDs=new DeviceIdTable(this));
		newTables.add(netDevices=new DeviceTable(this));
		newTables.add(netTcpRedirects=new TcpRedirectTable(this));
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
		newTables.add(postgresDatabases=new com.aoindustries.aoserv.client.postgresql.DatabaseTable(this));
		newTables.add(postgresEncodings=new EncodingTable(this));
		newTables.add(postgresServerUsers=new com.aoindustries.aoserv.client.postgresql.UserServerTable(this));
		newTables.add(postgresServers=new com.aoindustries.aoserv.client.postgresql.ServerTable(this));
		newTables.add(postgresUsers=new com.aoindustries.aoserv.client.postgresql.UserTable(this));
		newTables.add(postgresVersions=new VersionTable(this));
		newTables.add(privateFTPServers=new PrivateServerTable(this));
		newTables.add(processorTypes=new ProcessorTypeTable(this));
		newTables.add(protocols=new AppProtocolTable(this));
		newTables.add(racks=new RackTable(this));
		newTables.add(resellers=new ResellerTable(this));
		newTables.add(resources=new ResourceTable(this));
		newTables.add(schemaColumns=new ColumnTable(this));
		newTables.add(schemaForeignKeys=new ForeignKeyTable(this));
		newTables.add(schemaTables=new TableTable(this));
		newTables.add(schemaTypes=new TypeTable(this));
		newTables.add(sendmailBinds=new SendmailBindTable(this));
		newTables.add(sendmailServers=new SendmailServerTable(this));
		newTables.add(serverFarms=new ServerFarmTable(this));
		newTables.add(servers=new HostTable(this));
		newTables.add(shells=new ShellTable(this));
		newTables.add(signupRequestOptions=new OptionTable(this));
		newTables.add(signupRequests=new RequestTable(this));
		newTables.add(spamEmailMessages=new SpamMessageTable(this));
		newTables.add(sslCertificateNames=new CertificateNameTable(this));
		newTables.add(sslCertificateOtherUses=new CertificateOtherUseTable(this));
		newTables.add(sslCertificates=new CertificateTable(this));
		newTables.add(systemEmailAliases=new SystemAliasTable(this));
		newTables.add(technologies=new SoftwareCategorizationTable(this));
		newTables.add(technologyClasses=new SoftwareCategoryTable(this));
		newTables.add(technologyNames=new SoftwareTable(this));
		newTables.add(technologyVersions=new SoftwareVersionTable(this));
		newTables.add(ticketActionTypes=new ActionTypeTable(this));
		newTables.add(ticketActions=new ActionTable(this));
		newTables.add(ticketAssignments=new AssignmentTable(this));
		newTables.add(ticketBrandCategories=new BrandCategoryTable(this));
		newTables.add(ticketCategories=new CategoryTable(this));
		newTables.add(ticketPriorities=new PriorityTable(this));
		newTables.add(ticketStatuses=new StatusTable(this));
		newTables.add(ticketTypes=new TicketTypeTable(this));
		newTables.add(tickets=new TicketTable(this));
		newTables.add(timeZones=new TimeZoneTable(this));
		newTables.add(transactionTypes=new TransactionTypeTable(this));
		newTables.add(transactions=new TransactionTable(this));
		newTables.add(usStates=new UsStateTable(this));
		newTables.add(usernames=new UsernameTable(this));
		newTables.add(virtualDisks=new VirtualDiskTable(this));
		newTables.add(virtualServers=new VirtualServerTable(this));
		newTables.add(whoisHistory=new WhoisHistoryTable(this));
		newTables.add(whoisHistoryAccount = new WhoisHistoryAccountTable(this));
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
		for(AOServTable<?,?> table : tables) table.clearCache();
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
	 * @see  Table
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
	 * @see  Table
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
	final public Administrator getThisBusinessAdministrator() throws SQLException, IOException {
		Administrator obj=businessAdministrators.get(connectAs);
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
		requestUpdate(true,
			AoservProtocol.CommandID.INVALIDATE_TABLE,
			new UpdateRequest() {
				IntList tableList;
				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(tableID);
					out.writeCompressedInt(server);
				}
				@Override
				public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
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

	public static IntList readInvalidateList(CompressedDataInputStream in) throws IOException {
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
		for(AOServTable<?,?> table : tables) table.removeTableListener(listener);
	}

	static void writeParams(Object[] params, CompressedDataOutputStream out) throws IOException {
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
			else if(param instanceof Enum) out.writeEnum((Enum)param);
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
				out.writeEnum(port.getProtocol());
			}
			else if(param instanceof PostgresDatabaseName) out.writeUTF(param.toString());
			else if(param instanceof PostgresServerName) out.writeUTF(param.toString());
			else if(param instanceof PostgresUserId) out.writeUTF(param.toString());
			// Any other Writable
			else if(param instanceof AOServWritable) ((AOServWritable)param).write(out, AoservProtocol.Version.CURRENT_VERSION);
			else if(param instanceof CompressedWritable) ((CompressedWritable)param).write(out, AoservProtocol.Version.CURRENT_VERSION.getVersion());
			else throw new IOException("Unknown class for param: "+param.getClass().getName());
		}
	}

	/**
	 * This is the preferred mechanism for providing custom requests that have a return value.
	 *
	 * @see  #requestResult(boolean,ResultRequest)
	 */
	public interface ResultRequest<T> {
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
					CompressedDataOutputStream out = connection.getRequestOut(commID);
					resultRequest.writeRequest(out);
					out.flush();

					CompressedDataInputStream in=connection.getResponseIn();
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
				InterruptedIOException ioErr = new InterruptedIOException();
				ioErr.initCause(err);
				throw ioErr;
			}
			attempt++;
		}
		throw new InterruptedIOException();
	}

	final public boolean requestBooleanQuery(boolean allowRetry, AoservProtocol.CommandID commID, Object ... params) throws IOException, SQLException {
		int attempt = 1;
		int attempts = allowRetry ? RETRY_ATTEMPTS : 1;
		while(!Thread.interrupted()) {
			try {
				AOServConnection connection=getConnection(1);
				try {
					CompressedDataOutputStream out = connection.getRequestOut(commID);
					writeParams(params, out);
					out.flush();

					CompressedDataInputStream in=connection.getResponseIn();
					int code=in.readByte();
					if(code==AoservProtocol.DONE) return in.readBoolean();
					AoservProtocol.checkResult(code, in);
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
				InterruptedIOException ioErr = new InterruptedIOException();
				ioErr.initCause(err);
				throw ioErr;
			}
			attempt++;
		}
		throw new InterruptedIOException();
	}

	final public boolean requestBooleanQueryIL(boolean allowRetry, AoservProtocol.CommandID commID, Object ... params) throws IOException, SQLException {
		int attempt = 1;
		int attempts = allowRetry ? RETRY_ATTEMPTS : 1;
		while(!Thread.interrupted()) {
			try {
				boolean result;
				IntList invalidateList;
				AOServConnection connection=getConnection(1);
				try {
					CompressedDataOutputStream out = connection.getRequestOut(commID);
					writeParams(params, out);
					out.flush();

					CompressedDataInputStream in=connection.getResponseIn();
					int code=in.readByte();
					if(code==AoservProtocol.DONE) {
						result = in.readBoolean();
						invalidateList=readInvalidateList(in);
					} else {
						AoservProtocol.checkResult(code, in);
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
				InterruptedIOException ioErr = new InterruptedIOException();
				ioErr.initCause(err);
				throw ioErr;
			}
			attempt++;
		}
		throw new InterruptedIOException();
	}

	final public int requestIntQuery(boolean allowRetry, AoservProtocol.CommandID commID, Object ... params) throws IOException, SQLException {
		int attempt = 1;
		int attempts = allowRetry ? RETRY_ATTEMPTS : 1;
		while(!Thread.interrupted()) {
			try {
				AOServConnection connection=getConnection(1);
				try {
					CompressedDataOutputStream out = connection.getRequestOut(commID);
					writeParams(params, out);
					out.flush();

					CompressedDataInputStream in=connection.getResponseIn();
					int code=in.readByte();
					if(code==AoservProtocol.DONE) return in.readCompressedInt();
					AoservProtocol.checkResult(code, in);
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
				InterruptedIOException ioErr = new InterruptedIOException();
				ioErr.initCause(err);
				throw ioErr;
			}
			attempt++;
		}
		throw new InterruptedIOException();
	}

	final public int requestIntQueryIL(boolean allowRetry, AoservProtocol.CommandID commID, Object ... params) throws IOException, SQLException {
		int attempt = 1;
		int attempts = allowRetry ? RETRY_ATTEMPTS : 1;
		while(!Thread.interrupted()) {
			try {
				int result;
				IntList invalidateList;
				AOServConnection connection=getConnection(1);
				try {
					CompressedDataOutputStream out = connection.getRequestOut(commID);
					writeParams(params, out);
					out.flush();

					CompressedDataInputStream in=connection.getResponseIn();
					int code=in.readByte();
					if(code==AoservProtocol.DONE) {
						result=in.readCompressedInt();
						invalidateList=readInvalidateList(in);
					} else {
						AoservProtocol.checkResult(code, in);
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
				InterruptedIOException ioErr = new InterruptedIOException();
				ioErr.initCause(err);
				throw ioErr;
			}
			attempt++;
		}
		throw new InterruptedIOException();
	}

	final public long requestLongQuery(boolean allowRetry, AoservProtocol.CommandID commID, Object ... params) throws IOException, SQLException {
		int attempt = 1;
		int attempts = allowRetry ? RETRY_ATTEMPTS : 1;
		while(!Thread.interrupted()) {
			try {
				AOServConnection connection=getConnection(1);
				try {
					CompressedDataOutputStream out = connection.getRequestOut(commID);
					writeParams(params, out);
					out.flush();

					CompressedDataInputStream in=connection.getResponseIn();
					int code=in.readByte();
					if(code==AoservProtocol.DONE) return in.readLong();
					AoservProtocol.checkResult(code, in);
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
				InterruptedIOException ioErr = new InterruptedIOException();
				ioErr.initCause(err);
				throw ioErr;
			}
			attempt++;
		}
		throw new InterruptedIOException();
	}

	final public short requestShortQuery(boolean allowRetry, AoservProtocol.CommandID commID, Object ... params) throws IOException, SQLException {
		int attempt = 1;
		int attempts = allowRetry ? RETRY_ATTEMPTS : 1;
		while(!Thread.interrupted()) {
			try {
				AOServConnection connection=getConnection(1);
				try {
					CompressedDataOutputStream out = connection.getRequestOut(commID);
					writeParams(params, out);
					out.flush();

					CompressedDataInputStream in=connection.getResponseIn();
					int code=in.readByte();
					if(code==AoservProtocol.DONE) return in.readShort();
					AoservProtocol.checkResult(code, in);
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
				InterruptedIOException ioErr = new InterruptedIOException();
				ioErr.initCause(err);
				throw ioErr;
			}
			attempt++;
		}
		throw new InterruptedIOException();
	}

	final public short requestShortQueryIL(boolean allowRetry, AoservProtocol.CommandID commID, Object ... params) throws IOException, SQLException {
		int attempt = 1;
		int attempts = allowRetry ? RETRY_ATTEMPTS : 1;
		while(!Thread.interrupted()) {
			try {
				short result;
				IntList invalidateList;
				AOServConnection connection=getConnection(1);
				try {
					CompressedDataOutputStream out = connection.getRequestOut(commID);
					writeParams(params, out);
					out.flush();

					CompressedDataInputStream in=connection.getResponseIn();
					int code=in.readByte();
					if(code==AoservProtocol.DONE) {
						result=in.readShort();
						invalidateList=readInvalidateList(in);
					} else {
						AoservProtocol.checkResult(code, in);
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
				InterruptedIOException ioErr = new InterruptedIOException();
				ioErr.initCause(err);
				throw ioErr;
			}
			attempt++;
		}
		throw new InterruptedIOException();
	}

	final public String requestStringQuery(boolean allowRetry, AoservProtocol.CommandID commID, Object ... params) throws IOException, SQLException {
		int attempt = 1;
		int attempts = allowRetry ? RETRY_ATTEMPTS : 1;
		while(!Thread.interrupted()) {
			try {
				AOServConnection connection=getConnection(1);
				try {
					CompressedDataOutputStream out = connection.getRequestOut(commID);
					writeParams(params, out);
					out.flush();

					CompressedDataInputStream in=connection.getResponseIn();
					int code=in.readByte();
					if(code==AoservProtocol.DONE) return in.readUTF();
					AoservProtocol.checkResult(code, in);
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
	final public String requestLongStringQuery(boolean allowRetry, AoservProtocol.CommandID commID, Object ... params) throws IOException, SQLException {
		int attempt = 1;
		int attempts = allowRetry ? RETRY_ATTEMPTS : 1;
		while(!Thread.interrupted()) {
			try {
				AOServConnection connection=getConnection(1);
				try {
					CompressedDataOutputStream out = connection.getRequestOut(commID);
					writeParams(params, out);
					out.flush();

					CompressedDataInputStream in=connection.getResponseIn();
					int code=in.readByte();
					if(code==AoservProtocol.DONE) return in.readLongUTF();
					AoservProtocol.checkResult(code, in);
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
	final public String requestNullLongStringQuery(boolean allowRetry, AoservProtocol.CommandID commID, Object ... params) throws IOException, SQLException {
		int attempt = 1;
		int attempts = allowRetry ? RETRY_ATTEMPTS : 1;
		while(!Thread.interrupted()) {
			try {
				AOServConnection connection=getConnection(1);
				try {
					CompressedDataOutputStream out = connection.getRequestOut(commID);
					writeParams(params, out);
					out.flush();

					CompressedDataInputStream in=connection.getResponseIn();
					int code=in.readByte();
					if(code==AoservProtocol.DONE) return in.readNullLongUTF();
					AoservProtocol.checkResult(code, in);
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
	public interface UpdateRequest {
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
					CompressedDataOutputStream out = connection.getRequestOut(commID);
					updateRequest.writeRequest(out);
					out.flush();

					CompressedDataInputStream in=connection.getResponseIn();
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
				InterruptedIOException ioErr = new InterruptedIOException();
				ioErr.initCause(err);
				throw ioErr;
			}
			attempt++;
		}
		throw new InterruptedIOException();
	}

	final public void requestUpdate(boolean allowRetry, AoservProtocol.CommandID commID, Object ... params) throws IOException, SQLException {
		int attempt = 1;
		int attempts = allowRetry ? RETRY_ATTEMPTS : 1;
		while(!Thread.interrupted()) {
			try {
				AOServConnection connection=getConnection(1);
				try {
					CompressedDataOutputStream out = connection.getRequestOut(commID);
					writeParams(params, out);
					out.flush();

					CompressedDataInputStream in=connection.getResponseIn();
					int code=in.readByte();
					if(code!=AoservProtocol.DONE) AoservProtocol.checkResult(code, in);
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
				InterruptedIOException ioErr = new InterruptedIOException();
				ioErr.initCause(err);
				throw ioErr;
			}
			attempt++;
		}
		throw new InterruptedIOException();
	}

	final public void requestUpdateIL(boolean allowRetry, AoservProtocol.CommandID commID, Object ... params) throws IOException, SQLException {
		int attempt = 1;
		int attempts = allowRetry ? RETRY_ATTEMPTS : 1;
		while(!Thread.interrupted()) {
			try {
				IntList invalidateList;
				AOServConnection connection=getConnection(1);
				try {
					CompressedDataOutputStream out = connection.getRequestOut(commID);
					writeParams(params, out);
					out.flush();

					CompressedDataInputStream in=connection.getResponseIn();
					int code=in.readByte();
					if(code==AoservProtocol.DONE) invalidateList=readInvalidateList(in);
					else {
						AoservProtocol.checkResult(code, in);
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
				InterruptedIOException ioErr = new InterruptedIOException();
				ioErr.initCause(err);
				throw ioErr;
			}
			attempt++;
		}
		throw new InterruptedIOException();
	}

	public abstract AOServConnector switchUsers(UserId username) throws IOException;

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
			requestUpdate(true,
				AoservProtocol.CommandID.TEST_CONNECTION,
				new UpdateRequest() {
					@Override
					public void writeRequest(CompressedDataOutputStream out) {
					}
					@Override
					public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
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
	void addingTableListener() {
	}

	/**
	 * Gets some entropy from the master server, returns the number of bytes actually obtained.
	 */
	public int getMasterEntropy(final byte[] buff, final int numBytes) throws IOException, SQLException {
		return requestResult(true,
			AoservProtocol.CommandID.GET_MASTER_ENTROPY,
			new ResultRequest<Integer>() {
				int numObtained;

				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(numBytes);
				}

				@Override
				public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AoservProtocol.DONE) {
						numObtained=in.readCompressedInt();
						for(int c=0;c<numObtained;c++) buff[c]=in.readByte();
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
	public void addMasterEntropy(final byte[] buff, final int numBytes) throws IOException, SQLException {
		requestUpdate(true,
			AoservProtocol.CommandID.ADD_MASTER_ENTROPY,
			new UpdateRequest() {
				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(numBytes);
					for(int c=0;c<numBytes;c++) out.writeByte(buff[c]);
				}
				@Override
				public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
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
