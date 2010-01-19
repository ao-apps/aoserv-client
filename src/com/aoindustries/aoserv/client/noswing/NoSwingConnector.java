package com.aoindustries.aoserv.client.noswing;

/*
 * Copyright 2009-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.AOServConnectorUtils;
import com.aoindustries.aoserv.client.AOServPermission;
import com.aoindustries.aoserv.client.AOServPermissionService;
import com.aoindustries.aoserv.client.AOServService;
import com.aoindustries.aoserv.client.AOServer;
import com.aoindustries.aoserv.client.AOServerDaemonHost;
import com.aoindustries.aoserv.client.AOServerDaemonHostService;
import com.aoindustries.aoserv.client.AOServerResource;
import com.aoindustries.aoserv.client.AOServerResourceService;
import com.aoindustries.aoserv.client.AOServerService;
import com.aoindustries.aoserv.client.Architecture;
import com.aoindustries.aoserv.client.ArchitectureService;
import com.aoindustries.aoserv.client.BackupPartition;
import com.aoindustries.aoserv.client.BackupPartitionService;
import com.aoindustries.aoserv.client.BackupRetention;
import com.aoindustries.aoserv.client.BackupRetentionService;
import com.aoindustries.aoserv.client.Brand;
import com.aoindustries.aoserv.client.BrandService;
import com.aoindustries.aoserv.client.Business;
import com.aoindustries.aoserv.client.BusinessAdministrator;
import com.aoindustries.aoserv.client.BusinessAdministratorService;
import com.aoindustries.aoserv.client.BusinessServer;
import com.aoindustries.aoserv.client.BusinessServerService;
import com.aoindustries.aoserv.client.BusinessService;
import com.aoindustries.aoserv.client.CountryCode;
import com.aoindustries.aoserv.client.CountryCodeService;
import com.aoindustries.aoserv.client.CvsRepository;
import com.aoindustries.aoserv.client.CvsRepositoryService;
import com.aoindustries.aoserv.client.DisableLog;
import com.aoindustries.aoserv.client.DisableLogService;
import com.aoindustries.aoserv.client.DnsRecord;
import com.aoindustries.aoserv.client.DnsRecordService;
import com.aoindustries.aoserv.client.DnsTld;
import com.aoindustries.aoserv.client.DnsTldService;
import com.aoindustries.aoserv.client.DnsType;
import com.aoindustries.aoserv.client.DnsTypeService;
import com.aoindustries.aoserv.client.DnsZone;
import com.aoindustries.aoserv.client.DnsZoneService;
import com.aoindustries.aoserv.client.EmailInbox;
import com.aoindustries.aoserv.client.EmailInboxService;
import com.aoindustries.aoserv.client.FailoverFileLog;
import com.aoindustries.aoserv.client.FailoverFileLogService;
import com.aoindustries.aoserv.client.FailoverFileReplication;
import com.aoindustries.aoserv.client.FailoverFileReplicationService;
import com.aoindustries.aoserv.client.FailoverFileSchedule;
import com.aoindustries.aoserv.client.FailoverFileScheduleService;
import com.aoindustries.aoserv.client.FailoverMySQLReplication;
import com.aoindustries.aoserv.client.FailoverMySQLReplicationService;
import com.aoindustries.aoserv.client.FileBackupSetting;
import com.aoindustries.aoserv.client.FileBackupSettingService;
import com.aoindustries.aoserv.client.FtpGuestUser;
import com.aoindustries.aoserv.client.FtpGuestUserService;
import com.aoindustries.aoserv.client.GroupName;
import com.aoindustries.aoserv.client.GroupNameService;
import com.aoindustries.aoserv.client.HttpdServer;
import com.aoindustries.aoserv.client.HttpdServerService;
import com.aoindustries.aoserv.client.HttpdSite;
import com.aoindustries.aoserv.client.HttpdSiteService;
import com.aoindustries.aoserv.client.IPAddress;
import com.aoindustries.aoserv.client.IPAddressService;
import com.aoindustries.aoserv.client.Language;
import com.aoindustries.aoserv.client.LanguageService;
import com.aoindustries.aoserv.client.LinuxAccount;
import com.aoindustries.aoserv.client.LinuxAccountGroup;
import com.aoindustries.aoserv.client.LinuxAccountGroupService;
import com.aoindustries.aoserv.client.LinuxAccountService;
import com.aoindustries.aoserv.client.LinuxAccountType;
import com.aoindustries.aoserv.client.LinuxAccountTypeService;
import com.aoindustries.aoserv.client.LinuxGroup;
import com.aoindustries.aoserv.client.LinuxGroupService;
import com.aoindustries.aoserv.client.LinuxGroupType;
import com.aoindustries.aoserv.client.LinuxGroupTypeService;
import com.aoindustries.aoserv.client.MasterHost;
import com.aoindustries.aoserv.client.MasterHostService;
import com.aoindustries.aoserv.client.MasterServer;
import com.aoindustries.aoserv.client.MasterServerService;
import com.aoindustries.aoserv.client.MasterUser;
import com.aoindustries.aoserv.client.MasterUserService;
import com.aoindustries.aoserv.client.MySQLDBUser;
import com.aoindustries.aoserv.client.MySQLDBUserService;
import com.aoindustries.aoserv.client.MySQLDatabase;
import com.aoindustries.aoserv.client.MySQLDatabaseService;
import com.aoindustries.aoserv.client.MySQLServer;
import com.aoindustries.aoserv.client.MySQLServerService;
import com.aoindustries.aoserv.client.MySQLUser;
import com.aoindustries.aoserv.client.MySQLUserService;
import com.aoindustries.aoserv.client.NetBind;
import com.aoindustries.aoserv.client.NetBindService;
import com.aoindustries.aoserv.client.NetDevice;
import com.aoindustries.aoserv.client.NetDeviceID;
import com.aoindustries.aoserv.client.NetDeviceIDService;
import com.aoindustries.aoserv.client.NetDeviceService;
import com.aoindustries.aoserv.client.NetProtocol;
import com.aoindustries.aoserv.client.NetProtocolService;
import com.aoindustries.aoserv.client.NetTcpRedirect;
import com.aoindustries.aoserv.client.NetTcpRedirectService;
import com.aoindustries.aoserv.client.OperatingSystem;
import com.aoindustries.aoserv.client.OperatingSystemService;
import com.aoindustries.aoserv.client.OperatingSystemVersion;
import com.aoindustries.aoserv.client.OperatingSystemVersionService;
import com.aoindustries.aoserv.client.PackageCategory;
import com.aoindustries.aoserv.client.PackageCategoryService;
import com.aoindustries.aoserv.client.PostgresDatabase;
import com.aoindustries.aoserv.client.PostgresDatabaseService;
import com.aoindustries.aoserv.client.PostgresEncoding;
import com.aoindustries.aoserv.client.PostgresEncodingService;
import com.aoindustries.aoserv.client.PostgresServer;
import com.aoindustries.aoserv.client.PostgresServerService;
import com.aoindustries.aoserv.client.PostgresUser;
import com.aoindustries.aoserv.client.PostgresUserService;
import com.aoindustries.aoserv.client.PostgresVersion;
import com.aoindustries.aoserv.client.PostgresVersionService;
import com.aoindustries.aoserv.client.Protocol;
import com.aoindustries.aoserv.client.ProtocolService;
import com.aoindustries.aoserv.client.Reseller;
import com.aoindustries.aoserv.client.ResellerService;
import com.aoindustries.aoserv.client.Resource;
import com.aoindustries.aoserv.client.ResourceService;
import com.aoindustries.aoserv.client.ResourceType;
import com.aoindustries.aoserv.client.ResourceTypeService;
import com.aoindustries.aoserv.client.Server;
import com.aoindustries.aoserv.client.ServerFarm;
import com.aoindustries.aoserv.client.ServerFarmService;
import com.aoindustries.aoserv.client.ServerResource;
import com.aoindustries.aoserv.client.ServerResourceService;
import com.aoindustries.aoserv.client.ServerService;
import com.aoindustries.aoserv.client.ServiceName;
import com.aoindustries.aoserv.client.Shell;
import com.aoindustries.aoserv.client.ShellService;
import com.aoindustries.aoserv.client.Technology;
import com.aoindustries.aoserv.client.TechnologyClass;
import com.aoindustries.aoserv.client.TechnologyClassService;
import com.aoindustries.aoserv.client.TechnologyName;
import com.aoindustries.aoserv.client.TechnologyNameService;
import com.aoindustries.aoserv.client.TechnologyService;
import com.aoindustries.aoserv.client.TechnologyVersion;
import com.aoindustries.aoserv.client.TechnologyVersionService;
import com.aoindustries.aoserv.client.Ticket;
import com.aoindustries.aoserv.client.TicketAssignment;
import com.aoindustries.aoserv.client.TicketAssignmentService;
import com.aoindustries.aoserv.client.TicketCategory;
import com.aoindustries.aoserv.client.TicketCategoryService;
import com.aoindustries.aoserv.client.TicketPriority;
import com.aoindustries.aoserv.client.TicketPriorityService;
import com.aoindustries.aoserv.client.TicketService;
import com.aoindustries.aoserv.client.TicketStatus;
import com.aoindustries.aoserv.client.TicketStatusService;
import com.aoindustries.aoserv.client.TicketType;
import com.aoindustries.aoserv.client.TicketTypeService;
import com.aoindustries.aoserv.client.TimeZone;
import com.aoindustries.aoserv.client.TimeZoneService;
import com.aoindustries.aoserv.client.Username;
import com.aoindustries.aoserv.client.UsernameService;
import com.aoindustries.aoserv.client.command.AOServCommand;
import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.aoserv.client.validator.DomainLabel;
import com.aoindustries.aoserv.client.validator.DomainName;
import com.aoindustries.aoserv.client.validator.GroupId;
import com.aoindustries.aoserv.client.validator.UnixPath;
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.security.LoginException;
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @see NoSwingConnectorFactory
 *
 * @author  AO Industries, Inc.
 */
final public class NoSwingConnector implements AOServConnector<NoSwingConnector,NoSwingConnectorFactory> {

    final NoSwingConnectorFactory factory;
    final AOServConnector<?,?> wrapped;

    NoSwingConnector(NoSwingConnectorFactory factory, AOServConnector<?,?> wrapped) throws RemoteException, LoginException {
        this.factory = factory;
        this.wrapped = wrapped;
        aoserverDaemonHosts = new NoSwingAOServerDaemonHostService(this, wrapped.getAoServerDaemonHosts());
        aoserverResources = new NoSwingAOServerResourceService(this, wrapped.getAoServerResources());
        aoservers = new NoSwingAOServerService(this, wrapped.getAoServers());
        aoservPermissions = new NoSwingAOServPermissionService(this, wrapped.getAoservPermissions());
        /* TODO
        aoservProtocols = new NoSwingAOServProtocolService(this, wrapped.getAOServProtocols());
        aoshCommands = new NoSwingAOSHCommandService(this, wrapped.getAOSHCommands());
         */
        architectures = new NoSwingArchitectureService(this, wrapped.getArchitectures());
        backupPartitions = new NoSwingBackupPartitionService(this, wrapped.getBackupPartitions());
        backupRetentions = new NoSwingBackupRetentionService(this, wrapped.getBackupRetentions());
        /* TODO
        bankAccounts = new NoSwingBankAccountService(this, wrapped.getBankAccounts());
        bankTransactionTypes = new NoSwingBankTransactionTypeService(this, wrapped.getBankTransactionTypes());
        bankTransactions = new NoSwingBankTransactionService(this, wrapped.getBankTransactions());
        banks = new NoSwingBankService(this, wrapped.getBanks());
        blackholeEmailAddresss = new NoSwingBlackholeEmailAddressService(this, wrapped.getBlackholeEmailAddresss());
         */
        brands = new NoSwingBrandService(this, wrapped.getBrands());
        businessAdministrators = new NoSwingBusinessAdministratorService(this, wrapped.getBusinessAdministrators());
        /* TODO
        businessAdministratorPermissions = new NoSwingBusinessAdministratorPermissionService(this, wrapped.getBusinessAdministratorPermissions());
        businessProfiles = new NoSwingBusinessProfileService(this, wrapped.getBusinessProfiles());
         */
        businesses = new NoSwingBusinessService(this, wrapped.getBusinesses());
        businessServers = new NoSwingBusinessServerService(this, wrapped.getBusinessServers());
        countryCodes = new NoSwingCountryCodeService(this, wrapped.getCountryCodes());
        /* TODO
        creditCardProcessors = new NoSwingCreditCardProcessorService(this, wrapped.getCreditCardProcessors());
        creditCardTransactions = new NoSwingCreditCardTransactionService(this, wrapped.getCreditCardTransactions());
        creditCards = new NoSwingCreditCardService(this, wrapped.getCreditCards());
         */
        cvsRepositories = new NoSwingCvsRepositoryService(this, wrapped.getCvsRepositories());
        disableLogs = new NoSwingDisableLogService(this, wrapped.getDisableLogs());
        /* TODO
        distroFileTypes = new NoSwingDistroFileTypeService(this, wrapped.getDistroFileTypes());
        distroFiles = new NoSwingDistroFileService(this, wrapped.getDistroFiles());
         */
        dnsRecords = new NoSwingDnsRecordService(this, wrapped.getDnsRecords());
        dnsTlds = new NoSwingDnsTldService(this, wrapped.getDnsTlds());
        dnsTypes = new NoSwingDnsTypeService(this, wrapped.getDnsTypes());
        dnsZones = new NoSwingDnsZoneService(this, wrapped.getDnsZones());
        /* TODO
        emailAddresss = new NoSwingEmailAddressService(this, wrapped.getEmailAddresss());
        emailAttachmentBlocks = new NoSwingEmailAttachmentBlockService(this, wrapped.getEmailAttachmentBlocks());
        emailAttachmentTypes = new NoSwingEmailAttachmentTypeService(this, wrapped.getEmailAttachmentTypes());
        emailDomains = new NoSwingEmailDomainService(this, wrapped.getEmailDomains());
        emailForwardings = new NoSwingEmailForwardingService(this, wrapped.getEmailForwardings());
         */
        emailInboxes = new NoSwingEmailInboxService(this, wrapped.getEmailInboxes());
        /* TODO
        emailListAddresss = new NoSwingEmailListAddressService(this, wrapped.getEmailListAddresss());
        emailLists = new NoSwingEmailListService(this, wrapped.getEmailLists());
        emailPipeAddresss = new NoSwingEmailPipeAddressService(this, wrapped.getEmailPipeAddresss());
        emailPipes = new NoSwingEmailPipeService(this, wrapped.getEmailPipes());
        emailSmtpRelayTypes = new NoSwingEmailSmtpRelayTypeService(this, wrapped.getEmailSmtpRelayTypes());
        emailSmtpRelays = new NoSwingEmailSmtpRelayService(this, wrapped.getEmailSmtpRelays());
        emailSmtpSmartHostDomains = new NoSwingEmailSmtpSmartHostDomainService(this, wrapped.getEmailSmtpSmartHostDomains());
        emailSmtpSmartHosts = new NoSwingEmailSmtpSmartHostService(this, wrapped.getEmailSmtpSmartHosts());
        emailSpamAssassinIntegrationModes = new NoSwingEmailSpamAssassinIntegrationModeService(this, wrapped.getEmailSpamAssassinIntegrationModes());
        encryptionKeys = new NoSwingEncryptionKeyService(this, wrapped.getEncryptionKeys());
        expenseCategories = new NoSwingExpenseCategoryService(this, wrapped.getExpenseCategorys());
         */
        failoverFileLogs = new NoSwingFailoverFileLogService(this, wrapped.getFailoverFileLogs());
        failoverFileReplications = new NoSwingFailoverFileReplicationService(this, wrapped.getFailoverFileReplications());
        failoverFileSchedules = new NoSwingFailoverFileScheduleService(this, wrapped.getFailoverFileSchedules());
        failoverMySQLReplications = new NoSwingFailoverMySQLReplicationService(this, wrapped.getFailoverMySQLReplications());
        fileBackupSettings = new NoSwingFileBackupSettingService(this, wrapped.getFileBackupSettings());
        groupNames = new NoSwingGroupNameService(this, wrapped.getGroupNames());
        ftpGuestUsers = new NoSwingFtpGuestUserService(this, wrapped.getFtpGuestUsers());
        /* TODO
        httpdBinds = new NoSwingHttpdBindService(this, wrapped.getHttpdBinds());
        httpdJBossSites = new NoSwingHttpdJBossSiteService(this, wrapped.getHttpdJBossSites());
        httpdJBossVersions = new NoSwingHttpdJBossVersionService(this, wrapped.getHttpdJBossVersions());
        httpdJKCodes = new NoSwingHttpdJKCodeService(this, wrapped.getHttpdJKCodes());
        httpdJKProtocols = new NoSwingHttpdJKProtocolService(this, wrapped.getHttpdJKProtocols());
         */
        httpdServers = new NoSwingHttpdServerService(this, wrapped.getHttpdServers());
        /* TODO
        httpdSharedTomcats = new NoSwingHttpdSharedTomcatService(this, wrapped.getHttpdSharedTomcats());
        httpdSiteAuthenticatedLocations = new NoSwingHttpdSiteAuthenticatedLocationService(this, wrapped.getHttpdSiteAuthenticatedLocations());
        httpdSiteBinds = new NoSwingHttpdSiteBindService(this, wrapped.getHttpdSiteBinds());
        httpdSiteURLs = new NoSwingHttpdSiteURLService(this, wrapped.getHttpdSiteURLs());
         */
        httpdSites = new NoSwingHttpdSiteService(this, wrapped.getHttpdSites());
        // TODO: httpdStaticSites = new NoSwingHttpdStaticSiteService(this, wrapped.getHttpdStaticSites());
        // TODO: httpdTomcatContexts = new NoSwingHttpdTomcatContextService(this, wrapped.getHttpdTomcatContexts());
        // TODO: httpdTomcatDataSources = new NoSwingHttpdTomcatDataSourceService(this, wrapped.getHttpdTomcatDataSources());
        // TODO: httpdTomcatParameters = new NoSwingHttpdTomcatParameterService(this, wrapped.getHttpdTomcatParameters());
        // TODO: httpdTomcatSites = new NoSwingHttpdTomcatSiteService(this, wrapped.getHttpdTomcatSites());
        // TODO: httpdTomcatSharedSites = new NoSwingHttpdTomcatSharedSiteService(this, wrapped.getHttpdTomcatSharedSites());
        // TODO: httpdTomcatStdSites = new NoSwingHttpdTomcatStdSiteService(this, wrapped.getHttpdTomcatStdSites());
        // TODO: httpdTomcatVersions = new NoSwingHttpdTomcatVersionService(this, wrapped.getHttpdTomcatVersions());
        // TODO: httpdWorkers = new NoSwingHttpdWorkerService(this, wrapped.getHttpdWorkers());
        ipAddresses = new NoSwingIPAddressService(this, wrapped.getIpAddresses());
        languages = new NoSwingLanguageService(this, wrapped.getLanguages());
        /* TODO
        linuxAccAddresss = new NoSwingLinuxAccAddressService(this, wrapped.getLinuxAccAddresss());
         */
        linuxAccountGroups = new NoSwingLinuxAccountGroupService(this, wrapped.getLinuxAccountGroups());
        linuxAccountTypes = new NoSwingLinuxAccountTypeService(this, wrapped.getLinuxAccountTypes());
        linuxAccounts = new NoSwingLinuxAccountService(this, wrapped.getLinuxAccounts());
        linuxGroupTypes = new NoSwingLinuxGroupTypeService(this, wrapped.getLinuxGroupTypes());
        linuxGroups = new NoSwingLinuxGroupService(this, wrapped.getLinuxGroups());
        /* TODO
        majordomoLists = new NoSwingMajordomoListService(this, wrapped.getMajordomoLists());
        majordomoServers = new NoSwingMajordomoServerService(this, wrapped.getMajordomoServers());
        majordomoVersions = new NoSwingMajordomoVersionService(this, wrapped.getMajordomoVersions());
         */
        masterHosts = new NoSwingMasterHostService(this, wrapped.getMasterHosts());
        masterServers = new NoSwingMasterServerService(this, wrapped.getMasterServers());
        masterUsers = new NoSwingMasterUserService(this, wrapped.getMasterUsers());
        // TODO: monthlyCharges = new NoSwingMonthlyChargeService(this, wrapped.getMonthlyCharges());
        mysqlDatabases = new NoSwingMySQLDatabaseService(this, wrapped.getMysqlDatabases());
        mysqlDBUsers = new NoSwingMySQLDBUserService(this, wrapped.getMysqlDBUsers());
        mysqlServers = new NoSwingMySQLServerService(this, wrapped.getMysqlServers());
        mysqlUsers = new NoSwingMySQLUserService(this, wrapped.getMysqlUsers());
        netBinds = new NoSwingNetBindService(this, wrapped.getNetBinds());
        netDeviceIDs = new NoSwingNetDeviceIDService(this, wrapped.getNetDeviceIDs());
        netDevices = new NoSwingNetDeviceService(this, wrapped.getNetDevices());
        netProtocols = new NoSwingNetProtocolService(this, wrapped.getNetProtocols());
        netTcpRedirects = new NoSwingNetTcpRedirectService(this, wrapped.getNetTcpRedirects());
        /* TODO
        noticeLogs = new NoSwingNoticeLogService(this, wrapped.getNoticeLogs());
        noticeTypes = new NoSwingNoticeTypeService(this, wrapped.getNoticeTypes());
        */
        operatingSystemVersions = new NoSwingOperatingSystemVersionService(this, wrapped.getOperatingSystemVersions());
        operatingSystems = new NoSwingOperatingSystemService(this, wrapped.getOperatingSystems());
        packageCategories = new NoSwingPackageCategoryService(this, wrapped.getPackageCategories());
        /* TODO
        packageDefinitionLimits = new NoSwingPackageDefinitionLimitService(this, wrapped.getPackageDefinitionLimits());
        packageDefinitions = new NoSwingPackageDefinitionService(this, wrapped.getPackageDefinitions());
        paymentTypes = new NoSwingPaymentTypeService(this, wrapped.getPaymentTypes());
        physicalServers = new NoSwingPhysicalServerService(this, wrapped.getPhysicalServers());
         */
        postgresDatabases = new NoSwingPostgresDatabaseService(this, wrapped.getPostgresDatabases());
        postgresEncodings = new NoSwingPostgresEncodingService(this, wrapped.getPostgresEncodings());
        postgresServers = new NoSwingPostgresServerService(this, wrapped.getPostgresServers());
        postgresUsers = new NoSwingPostgresUserService(this, wrapped.getPostgresUsers());
        postgresVersions = new NoSwingPostgresVersionService(this, wrapped.getPostgresVersions());
        // TODO: privateFtpServers = new NoSwingPrivateFtpServerService(this, wrapped.getPrivateFtpServers());
        // TODO: processorTypes = new NoSwingProcessorTypeService(this, wrapped.getProcessorTypes());
        protocols = new NoSwingProtocolService(this, wrapped.getProtocols());
        // TODO: racks = new NoSwingRackService(this, wrapped.getRacks());
        resellers = new NoSwingResellerService(this, wrapped.getResellers());
        resourceTypes = new NoSwingResourceTypeService(this, wrapped.getResourceTypes());
        resources = new NoSwingResourceService(this, wrapped.getResources());
        serverFarms = new NoSwingServerFarmService(this, wrapped.getServerFarms());
        serverResources = new NoSwingServerResourceService(this, wrapped.getServerResources());
        servers = new NoSwingServerService(this, wrapped.getServers());
        shells = new NoSwingShellService(this, wrapped.getShells());
        /* TODO
        signupRequestOptions = new NoSwingSignupRequestOptionService(this, wrapped.getSignupRequestOptions());
        signupRequests = new NoSwingSignupRequestService(this, wrapped.getSignupRequests());
        spamEmailMessages = new NoSwingSpamEmailMessageService(this, wrapped.getSpamEmailMessages());
        systemEmailAliass = new NoSwingSystemEmailAliasService(this, wrapped.getSystemEmailAliass());
         */
        technologies = new NoSwingTechnologyService(this, wrapped.getTechnologies());
        technologyClasses = new NoSwingTechnologyClassService(this, wrapped.getTechnologyClasses());
        technologyNames = new NoSwingTechnologyNameService(this, wrapped.getTechnologyNames());
        technologyVersions = new NoSwingTechnologyVersionService(this, wrapped.getTechnologyVersions());
        // TODO: ticketActionTypes = new NoSwingTicketActionTypeService(this, wrapped.getTicketActionTypes());
        // TODO: ticketActions = new NoSwingTicketActionService(this, wrapped.getTicketActions());
        ticketAssignments = new NoSwingTicketAssignmentService(this, wrapped.getTicketAssignments());
        // TODO: ticketBrandCategories = new NoSwingTicketBrandCategoryService(this, wrapped.getTicketBrandCategorys());
        ticketCategories = new NoSwingTicketCategoryService(this, wrapped.getTicketCategories());
        ticketPriorities = new NoSwingTicketPriorityService(this, wrapped.getTicketPriorities());
        ticketStatuses = new NoSwingTicketStatusService(this, wrapped.getTicketStatuses());
        ticketTypes = new NoSwingTicketTypeService(this, wrapped.getTicketTypes());
        tickets = new NoSwingTicketService(this, wrapped.getTickets());
        timeZones = new NoSwingTimeZoneService(this, wrapped.getTimeZones());
        /* TODO
        transactionTypes = new NoSwingTransactionTypeService(this, wrapped.getTransactionTypes());
        transactions = new NoSwingTransactionService(this, wrapped.getTransactions());
        usStates = new NoSwingUSStateService(this, wrapped.getUSStates());
         */
        usernames = new NoSwingUsernameService(this, wrapped.getUsernames());
        /* TODO
        virtualDisks = new NoSwingVirtualDiskService(this, wrapped.getVirtualDisks());
        virtualServers = new NoSwingVirtualServerService(this, wrapped.getVirtualServers());
        whoisHistories = new NoSwingWhoisHistoryService(this, wrapped.getWhoisHistorys());
         */
    }

    public NoSwingConnectorFactory getFactory() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return factory;
    }

    public Locale getLocale() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return wrapped.getLocale();
    }

    public void setLocale(Locale locale) throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        wrapped.setLocale(locale);
    }

    public UserId getConnectAs() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return wrapped.getConnectAs();
    }

    public BusinessAdministrator getThisBusinessAdministrator() throws RemoteException {
        return getBusinessAdministrators().get(getConnectAs());
    }

    public UserId getAuthenticateAs() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return wrapped.getAuthenticateAs();
    }

    public String getPassword() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return wrapped.getPassword();
    }

    public <R> R executeCommand(AOServCommand<R> command, boolean isInteractive) throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return wrapped.executeCommand(command, isInteractive);
    }

    private final AtomicReference<Map<ServiceName,AOServService<NoSwingConnector,NoSwingConnectorFactory,?,?>>> tables = new AtomicReference<Map<ServiceName,AOServService<NoSwingConnector,NoSwingConnectorFactory,?,?>>>();
    public Map<ServiceName,AOServService<NoSwingConnector,NoSwingConnectorFactory,?,?>> getServices() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        Map<ServiceName,AOServService<NoSwingConnector,NoSwingConnectorFactory,?,?>> ts = tables.get();
        if(ts==null) {
            ts = AOServConnectorUtils.createServiceMap(this);
            if(!tables.compareAndSet(null, ts)) ts = tables.get();
        }
        return ts;
    }

    // <editor-fold defaultstate="collapsed" desc="AOServerDaemonHostService">
    static class NoSwingAOServerDaemonHostService extends NoSwingService<Integer,AOServerDaemonHost> implements AOServerDaemonHostService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingAOServerDaemonHostService(NoSwingConnector connector, AOServerDaemonHostService<?,?> wrapped) {
            super(connector, Integer.class, AOServerDaemonHost.class, wrapped);
        }
    }
    final NoSwingAOServerDaemonHostService aoserverDaemonHosts;
    public AOServerDaemonHostService<NoSwingConnector,NoSwingConnectorFactory> getAoServerDaemonHosts() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return aoserverDaemonHosts;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="AOServerResourceService">
    static class NoSwingAOServerResourceService extends NoSwingService<Integer,AOServerResource> implements AOServerResourceService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingAOServerResourceService(NoSwingConnector connector, AOServerResourceService<?,?> wrapped) {
            super(connector, Integer.class, AOServerResource.class, wrapped);
        }
    }
    final NoSwingAOServerResourceService aoserverResources;
    public AOServerResourceService<NoSwingConnector,NoSwingConnectorFactory> getAoServerResources() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return aoserverResources;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="AOServerService">
    static class NoSwingAOServerService extends NoSwingService<Integer,AOServer> implements AOServerService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingAOServerService(NoSwingConnector connector, AOServerService<?,?> wrapped) {
            super(connector, Integer.class, AOServer.class, wrapped);
        }
    }
    final NoSwingAOServerService aoservers;
    public AOServerService<NoSwingConnector,NoSwingConnectorFactory> getAoServers() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return aoservers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="AOServPermissionService">
    static class NoSwingAOServPermissionService extends NoSwingService<String,AOServPermission> implements AOServPermissionService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingAOServPermissionService(NoSwingConnector connector, AOServPermissionService<?,?> wrapped) {
            super(connector, String.class, AOServPermission.class, wrapped);
        }
    }
    final NoSwingAOServPermissionService aoservPermissions;
    public AOServPermissionService<NoSwingConnector,NoSwingConnectorFactory> getAoservPermissions() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return aoservPermissions;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ArchitectureService">
    static class NoSwingArchitectureService extends NoSwingService<String,Architecture> implements ArchitectureService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingArchitectureService(NoSwingConnector connector, ArchitectureService<?,?> wrapped) {
            super(connector, String.class, Architecture.class, wrapped);
        }
    }
    final NoSwingArchitectureService architectures;
    public ArchitectureService<NoSwingConnector,NoSwingConnectorFactory> getArchitectures() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return architectures;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BackupPartitionService">
    static class NoSwingBackupPartitionService extends NoSwingService<Integer,BackupPartition> implements BackupPartitionService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingBackupPartitionService(NoSwingConnector connector, BackupPartitionService<?,?> wrapped) {
            super(connector, Integer.class, BackupPartition.class, wrapped);
        }
    }
    final NoSwingBackupPartitionService backupPartitions;
    public BackupPartitionService<NoSwingConnector,NoSwingConnectorFactory> getBackupPartitions() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return backupPartitions;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BackupRetentionService">
    static class NoSwingBackupRetentionService extends NoSwingService<Short,BackupRetention> implements BackupRetentionService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingBackupRetentionService(NoSwingConnector connector, BackupRetentionService<?,?> wrapped) {
            super(connector, Short.class, BackupRetention.class, wrapped);
        }
    }
    final NoSwingBackupRetentionService backupRetentions;
    public BackupRetentionService<NoSwingConnector,NoSwingConnectorFactory> getBackupRetentions() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return backupRetentions;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BankAccountService">
    // TODO: final NoSwingBankAccountService bankAccounts;
    // TODO: public BankAccountService<NoSwingConnector,NoSwingConnectorFactory> getBankAccounts() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BankTransactionTypeService">
    // TODO: final NoSwingBankTransactionTypeService bankTransactionTypes;
    // TODO: public BankTransactionTypeService<NoSwingConnector,NoSwingConnectorFactory> getBankTransactionTypes() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BankTransactionService">
    // TODO: final NoSwingBankTransactionService bankTransactions;
    // TODO: public BankTransactionService<NoSwingConnector,NoSwingConnectorFactory> getBankTransactions() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BankService">
    // TODO: final NoSwingBankService banks;
    // TODO: public BankService<NoSwingConnector,NoSwingConnectorFactory> getBanks() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BlackholeEmailAddressService">
    // TODO: final NoSwingBlackholeEmailAddressService blackholeEmailAddresss;
    // TODO: public BlackholeEmailAddressService<NoSwingConnector,NoSwingConnectorFactory> getBlackholeEmailAddresses() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BrandService">
    static class NoSwingBrandService extends NoSwingService<AccountingCode,Brand> implements BrandService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingBrandService(NoSwingConnector connector, BrandService<?,?> wrapped) {
            super(connector, AccountingCode.class, Brand.class, wrapped);
        }
    }
    final NoSwingBrandService brands;
    public BrandService<NoSwingConnector,NoSwingConnectorFactory> getBrands() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return brands;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BusinessAdministratorService">
    static class NoSwingBusinessAdministratorService extends NoSwingService<UserId,BusinessAdministrator> implements BusinessAdministratorService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingBusinessAdministratorService(NoSwingConnector connector, BusinessAdministratorService<?,?> wrapped) {
            super(connector, UserId.class, BusinessAdministrator.class, wrapped);
        }
    }
    final NoSwingBusinessAdministratorService businessAdministrators;
    public BusinessAdministratorService<NoSwingConnector,NoSwingConnectorFactory> getBusinessAdministrators() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return businessAdministrators;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BusinessAdministratorPermissionService">
    // TODO: final NoSwingBusinessAdministratorPermissionService businessAdministratorPermissions;
    // TODO: public BusinessAdministratorPermissionService<NoSwingConnector,NoSwingConnectorFactory> getBusinessAdministratorPermissions() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BusinessProfileService">
    // TODO: final NoSwingBusinessProfileService businessProfiles;
    // TODO: public BusinessProfileService<NoSwingConnector,NoSwingConnectorFactory> getBusinessProfiles() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BusinessService">
    static class NoSwingBusinessService extends NoSwingService<AccountingCode,Business> implements BusinessService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingBusinessService(NoSwingConnector connector, BusinessService<?,?> wrapped) {
            super(connector, AccountingCode.class, Business.class, wrapped);
        }
    }
    final NoSwingBusinessService businesses;
    public BusinessService<NoSwingConnector,NoSwingConnectorFactory> getBusinesses() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return businesses;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BusinessServerService">
    static class NoSwingBusinessServerService extends NoSwingService<Integer,BusinessServer> implements BusinessServerService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingBusinessServerService(NoSwingConnector connector, BusinessServerService<?,?> wrapped) {
            super(connector, Integer.class, BusinessServer.class, wrapped);
        }
    }
    final NoSwingBusinessServerService businessServers;
    public BusinessServerService<NoSwingConnector,NoSwingConnectorFactory> getBusinessServers() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return businessServers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="CountryCodeService">
    static class NoSwingCountryCodeService extends NoSwingService<String,CountryCode> implements CountryCodeService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingCountryCodeService(NoSwingConnector connector, CountryCodeService<?,?> wrapped) {
            super(connector, String.class, CountryCode.class, wrapped);
        }
    }
    final NoSwingCountryCodeService countryCodes;
    public CountryCodeService<NoSwingConnector,NoSwingConnectorFactory> getCountryCodes() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return countryCodes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="CreditCardProcessorService">
    // TODO: final NoSwingCreditCardProcessorService creditCardProcessors;
    // TODO: public CreditCardProcessorService<NoSwingConnector,NoSwingConnectorFactory> getCreditCardProcessors() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="CreditCardTransactionService">
    // TODO: final NoSwingCreditCardTransactionService creditCardTransactions;
    // TODO: public CreditCardTransactionService<NoSwingConnector,NoSwingConnectorFactory> getCreditCardTransactions() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="CreditCardService">
    // TODO: final NoSwingCreditCardService creditCards;
    // TODO: public CreditCardService<NoSwingConnector,NoSwingConnectorFactory> getCreditCards() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="CvsRepositoryService">
    static class NoSwingCvsRepositoryService extends NoSwingService<Integer,CvsRepository> implements CvsRepositoryService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingCvsRepositoryService(NoSwingConnector connector, CvsRepositoryService<?,?> wrapped) {
            super(connector, Integer.class, CvsRepository.class, wrapped);
        }
    }
    final NoSwingCvsRepositoryService cvsRepositories;
    public CvsRepositoryService<NoSwingConnector,NoSwingConnectorFactory> getCvsRepositories() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return cvsRepositories;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="DisableLogService">
    static class NoSwingDisableLogService extends NoSwingService<Integer,DisableLog> implements DisableLogService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingDisableLogService(NoSwingConnector connector, DisableLogService<?,?> wrapped) {
            super(connector, Integer.class, DisableLog.class, wrapped);
        }
    }
    final NoSwingDisableLogService disableLogs;
    public DisableLogService<NoSwingConnector,NoSwingConnectorFactory> getDisableLogs() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return disableLogs;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="DistroFileTypeService">
    // TODO: final NoSwingDistroFileTypeService distroFileTypes;
    // TODO: public DistroFileTypeService<NoSwingConnector,NoSwingConnectorFactory> getDistroFileTypes() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="DistroFileService">
    // TODO: final NoSwingDistroFileService distroFiles;
    // TODO: public DistroFileService<NoSwingConnector,NoSwingConnectorFactory> getDistroFiles() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="DnsRecordService">
    static class NoSwingDnsRecordService extends NoSwingService<Integer,DnsRecord> implements DnsRecordService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingDnsRecordService(NoSwingConnector connector, DnsRecordService<?,?> wrapped) {
            super(connector, Integer.class, DnsRecord.class, wrapped);
        }
    }
    final NoSwingDnsRecordService dnsRecords;
    public DnsRecordService<NoSwingConnector,NoSwingConnectorFactory> getDnsRecords() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return dnsRecords;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="DnsTldService">
    static class NoSwingDnsTldService extends NoSwingService<DomainName,DnsTld> implements DnsTldService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingDnsTldService(NoSwingConnector connector, DnsTldService<?,?> wrapped) {
            super(connector, DomainName.class, DnsTld.class, wrapped);
        }
    }
    final NoSwingDnsTldService dnsTlds;
    public DnsTldService<NoSwingConnector,NoSwingConnectorFactory> getDnsTlds() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return dnsTlds;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="DnsTypeService">
    static class NoSwingDnsTypeService extends NoSwingService<String,DnsType> implements DnsTypeService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingDnsTypeService(NoSwingConnector connector, DnsTypeService<?,?> wrapped) {
            super(connector, String.class, DnsType.class, wrapped);
        }
    }
    final NoSwingDnsTypeService dnsTypes;
    public DnsTypeService<NoSwingConnector,NoSwingConnectorFactory> getDnsTypes() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return dnsTypes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="DnsZoneService">
    static class NoSwingDnsZoneService extends NoSwingService<Integer,DnsZone> implements DnsZoneService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingDnsZoneService(NoSwingConnector connector, DnsZoneService<?,?> wrapped) {
            super(connector, Integer.class, DnsZone.class, wrapped);
        }
    }
    final NoSwingDnsZoneService dnsZones;
    public DnsZoneService<NoSwingConnector,NoSwingConnectorFactory> getDnsZones() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return dnsZones;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailAddressService">
    // TODO: final NoSwingEmailAddressService emailAddresss;
    // TODO: public EmailAddressService<NoSwingConnector,NoSwingConnectorFactory> getEmailAddresses() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailAttachmentBlockService">
    // TODO: final NoSwingEmailAttachmentBlockService emailAttachmentBlocks;
    // TODO: public EmailAttachmentBlockService<NoSwingConnector,NoSwingConnectorFactory> getEmailAttachmentBlocks() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailAttachmentTypeService">
    // TODO: final NoSwingEmailAttachmentTypeService emailAttachmentTypes;
    // TODO: public EmailAttachmentTypeService<NoSwingConnector,NoSwingConnectorFactory> getEmailAttachmentTypes() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailDomainService">
    // TODO: final NoSwingEmailDomainService emailDomains;
    // TODO: public EmailDomainService<NoSwingConnector,NoSwingConnectorFactory> getEmailDomains() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailForwardingService">
    // TODO: final NoSwingEmailForwardingService emailForwardings;
    // TODO: public EmailForwardingService<NoSwingConnector,NoSwingConnectorFactory> getEmailForwardings() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailInboxService">
    static class NoSwingEmailInboxService extends NoSwingService<Integer,EmailInbox> implements EmailInboxService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingEmailInboxService(NoSwingConnector connector, EmailInboxService<?,?> wrapped) {
            super(connector, Integer.class, EmailInbox.class, wrapped);
        }
    }
    final NoSwingEmailInboxService emailInboxes;
    public EmailInboxService<NoSwingConnector,NoSwingConnectorFactory> getEmailInboxes() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return emailInboxes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailListAddressService">
    // TODO: final NoSwingEmailListAddressService emailListAddresss;
    // TODO: public EmailListAddressService<NoSwingConnector,NoSwingConnectorFactory> getEmailListAddresses() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailListService">
    // TODO: final NoSwingEmailListService emailLists;
    // TODO: public EmailListService<NoSwingConnector,NoSwingConnectorFactory> getEmailLists() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailPipeAddressService">
    // TODO: final NoSwingEmailPipeAddressService emailPipeAddresss;
    // TODO: public EmailPipeAddressService<NoSwingConnector,NoSwingConnectorFactory> getEmailPipeAddresses() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailPipeService">
    // TODO: final NoSwingEmailPipeService emailPipes;
    // TODO: public EmailPipeService<NoSwingConnector,NoSwingConnectorFactory> getEmailPipes() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailSmtpRelayTypeService">
    // TODO: final NoSwingEmailSmtpRelayTypeService emailSmtpRelayTypes;
    // TODO: public EmailSmtpRelayTypeService<NoSwingConnector,NoSwingConnectorFactory> getEmailSmtpRelayTypes() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailSmtpRelayService">
    // TODO: final NoSwingEmailSmtpRelayService emailSmtpRelays;
    // TODO: public EmailSmtpRelayService<NoSwingConnector,NoSwingConnectorFactory> getEmailSmtpRelays() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailSmtpSmartHostDomainService">
    // TODO: final NoSwingEmailSmtpSmartHostDomainService emailSmtpSmartHostDomains;
    // TODO: public EmailSmtpSmartHostDomainService<NoSwingConnector,NoSwingConnectorFactory> getEmailSmtpSmartHostDomains() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailSmtpSmartHostService">
    // TODO: final NoSwingEmailSmtpSmartHostService emailSmtpSmartHosts;
    // TODO: public EmailSmtpSmartHostService<NoSwingConnector,NoSwingConnectorFactory> getEmailSmtpSmartHosts() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailSpamAssassinIntegrationModeService">
    // TODO: final NoSwingEmailSpamAssassinIntegrationModeService emailSpamAssassinIntegrationModes;
    // TODO: public EmailSpamAssassinIntegrationModeService<NoSwingConnector,NoSwingConnectorFactory> getEmailSpamAssassinIntegrationModes() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EncryptionKeyService">
    // TODO: final NoSwingEncryptionKeyService encryptionKeys;
    // TODO: public EncryptionKeyService<NoSwingConnector,NoSwingConnectorFactory> getEncryptionKeys() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ExpenseCategoryService">
    // TODO: final NoSwingExpenseCategoryService expenseCategories;
    // TODO: public ExpenseCategoryService<NoSwingConnector,NoSwingConnectorFactory> getExpenseCategories() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="FailoverFileLogService">
    static class NoSwingFailoverFileLogService extends NoSwingService<Integer,FailoverFileLog> implements FailoverFileLogService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingFailoverFileLogService(NoSwingConnector connector, FailoverFileLogService<?,?> wrapped) {
            super(connector, Integer.class, FailoverFileLog.class, wrapped);
        }
    }
    final NoSwingFailoverFileLogService failoverFileLogs;
    public FailoverFileLogService<NoSwingConnector,NoSwingConnectorFactory> getFailoverFileLogs() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return failoverFileLogs;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="FailoverFileReplicationService">
    static class NoSwingFailoverFileReplicationService extends NoSwingService<Integer,FailoverFileReplication> implements FailoverFileReplicationService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingFailoverFileReplicationService(NoSwingConnector connector, FailoverFileReplicationService<?,?> wrapped) {
            super(connector, Integer.class, FailoverFileReplication.class, wrapped);
        }
    }
    final NoSwingFailoverFileReplicationService failoverFileReplications;
    public FailoverFileReplicationService<NoSwingConnector,NoSwingConnectorFactory> getFailoverFileReplications() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return failoverFileReplications;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="FailoverFileScheduleService">
    static class NoSwingFailoverFileScheduleService extends NoSwingService<Integer,FailoverFileSchedule> implements FailoverFileScheduleService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingFailoverFileScheduleService(NoSwingConnector connector, FailoverFileScheduleService<?,?> wrapped) {
            super(connector, Integer.class, FailoverFileSchedule.class, wrapped);
        }
    }
    final NoSwingFailoverFileScheduleService failoverFileSchedules;
    public FailoverFileScheduleService<NoSwingConnector,NoSwingConnectorFactory> getFailoverFileSchedules() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return failoverFileSchedules;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="FailoverMySQLReplicationService">
    static class NoSwingFailoverMySQLReplicationService extends NoSwingService<Integer,FailoverMySQLReplication> implements FailoverMySQLReplicationService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingFailoverMySQLReplicationService(NoSwingConnector connector, FailoverMySQLReplicationService<?,?> wrapped) {
            super(connector, Integer.class, FailoverMySQLReplication.class, wrapped);
        }
    }
    final NoSwingFailoverMySQLReplicationService failoverMySQLReplications;
    public FailoverMySQLReplicationService<NoSwingConnector,NoSwingConnectorFactory> getFailoverMySQLReplications() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return failoverMySQLReplications;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="FileBackupSettingService">
    static class NoSwingFileBackupSettingService extends NoSwingService<Integer,FileBackupSetting> implements FileBackupSettingService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingFileBackupSettingService(NoSwingConnector connector, FileBackupSettingService<?,?> wrapped) {
            super(connector, Integer.class, FileBackupSetting.class, wrapped);
        }
    }
    final NoSwingFileBackupSettingService fileBackupSettings;
    public FileBackupSettingService<NoSwingConnector,NoSwingConnectorFactory> getFileBackupSettings() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return fileBackupSettings;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="FtpGuestUserService">
    static class NoSwingFtpGuestUserService extends NoSwingService<Integer,FtpGuestUser> implements FtpGuestUserService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingFtpGuestUserService(NoSwingConnector connector, FtpGuestUserService<?,?> wrapped) {
            super(connector, Integer.class, FtpGuestUser.class, wrapped);
        }
    }
    final NoSwingFtpGuestUserService ftpGuestUsers;
    public FtpGuestUserService<NoSwingConnector,NoSwingConnectorFactory> getFtpGuestUsers() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return ftpGuestUsers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="GroupNameService">
    static class NoSwingGroupNameService extends NoSwingService<GroupId,GroupName> implements GroupNameService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingGroupNameService(NoSwingConnector connector, GroupNameService<?,?> wrapped) {
            super(connector, GroupId.class, GroupName.class, wrapped);
        }
    }
    final NoSwingGroupNameService groupNames;
    public GroupNameService<NoSwingConnector,NoSwingConnectorFactory> getGroupNames() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return groupNames;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdBindService">
    // TODO: final NoSwingHttpdBindService httpdBinds;
    // TODO: public HttpdBindService<NoSwingConnector,NoSwingConnectorFactory> getHttpdBinds() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdJBossSiteService">
    // TODO: final NoSwingHttpdJBossSiteService httpdJBossSites;
    // TODO: public HttpdJBossSiteService<NoSwingConnector,NoSwingConnectorFactory> getHttpdJBossSites() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdJBossVersionService">
    // TODO: final NoSwingHttpdJBossVersionService httpdJBossVersions;
    // TODO: public HttpdJBossVersionService<NoSwingConnector,NoSwingConnectorFactory> getHttpdJBossVersions() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdJKCodeService">
    // TODO: final NoSwingHttpdJKCodeService httpdJKCodes;
    // TODO: public HttpdJKCodeService<NoSwingConnector,NoSwingConnectorFactory> getHttpdJKCodes() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdJKProtocolService">
    // TODO: final NoSwingHttpdJKProtocolService httpdJKProtocols;
    // TODO: public HttpdJKProtocolService<NoSwingConnector,NoSwingConnectorFactory> getHttpdJKProtocols() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdServerService">
    static class NoSwingHttpdServerService extends NoSwingService<Integer,HttpdServer> implements HttpdServerService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingHttpdServerService(NoSwingConnector connector, HttpdServerService<?,?> wrapped) {
            super(connector, Integer.class, HttpdServer.class, wrapped);
        }
    }
    final NoSwingHttpdServerService httpdServers;
    public HttpdServerService<NoSwingConnector,NoSwingConnectorFactory> getHttpdServers() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return httpdServers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdSharedTomcatService">
    // TODO: final NoSwingHttpdSharedTomcatService httpdSharedTomcats;
    // TODO: public HttpdSharedTomcatService<NoSwingConnector,NoSwingConnectorFactory> getHttpdSharedTomcats() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdSiteAuthenticatedLocationService">
    // TODO: final NoSwingHttpdSiteAuthenticatedLocationService httpdSiteAuthenticatedLocations;
    // TODO: public HttpdSiteAuthenticatedLocationService<NoSwingConnector,NoSwingConnectorFactory> getHttpdSiteAuthenticatedLocations() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdSiteBindService">
    // TODO: final NoSwingHttpdSiteBindService httpdSiteBinds;
    // TODO: public HttpdSiteBindService<NoSwingConnector,NoSwingConnectorFactory> getHttpdSiteBinds() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdSiteURLService">
    // TODO: final NoSwingHttpdSiteURLService httpdSiteURLs;
    // TODO: public HttpdSiteURLService<NoSwingConnector,NoSwingConnectorFactory> getHttpdSiteURLs() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdSiteService">
    static class NoSwingHttpdSiteService extends NoSwingService<Integer,HttpdSite> implements HttpdSiteService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingHttpdSiteService(NoSwingConnector connector, HttpdSiteService<?,?> wrapped) {
            super(connector, Integer.class, HttpdSite.class, wrapped);
        }
    }
    final NoSwingHttpdSiteService httpdSites;
    public HttpdSiteService<NoSwingConnector,NoSwingConnectorFactory> getHttpdSites() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return httpdSites;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdStaticSiteService">
    // TODO: final NoSwingHttpdStaticSiteService httpdStaticSites;
    // TODO: public HttpdStaticSiteService<NoSwingConnector,NoSwingConnectorFactory> getHttpdStaticSites() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdTomcatContextService">
    // TODO: final NoSwingHttpdTomcatContextService httpdTomcatContexts;
    // TODO: public HttpdTomcatContextService<NoSwingConnector,NoSwingConnectorFactory> getHttpdTomcatContexts() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdTomcatDataSourceService">
    // TODO: final NoSwingHttpdTomcatDataSourceService httpdTomcatDataSources;
    // TODO: public HttpdTomcatDataSourceService<NoSwingConnector,NoSwingConnectorFactory> getHttpdTomcatDataSources() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdTomcatParameterService">
    // TODO: final NoSwingHttpdTomcatParameterService httpdTomcatParameters;
    // TODO: public HttpdTomcatParameterService<NoSwingConnector,NoSwingConnectorFactory> getHttpdTomcatParameters() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdTomcatSiteService">
    // TODO: final NoSwingHttpdTomcatSiteService httpdTomcatSites;
    // TODO: public HttpdTomcatSiteService<NoSwingConnector,NoSwingConnectorFactory> getHttpdTomcatSites() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdTomcatSharedSiteService">
    // TODO: final NoSwingHttpdTomcatSharedSiteService httpdTomcatSharedSites;
    // TODO: public HttpdTomcatSharedSiteService<NoSwingConnector,NoSwingConnectorFactory> getHttpdTomcatSharedSites() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdTomcatStdSiteService">
    // TODO: final NoSwingHttpdTomcatStdSiteService httpdTomcatStdSites;
    // TODO: public HttpdTomcatStdSiteService<NoSwingConnector,NoSwingConnectorFactory> getHttpdTomcatStdSites() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdTomcatVersionService">
    // TODO: final NoSwingHttpdTomcatVersionService httpdTomcatVersions;
    // TODO: public HttpdTomcatVersionService<NoSwingConnector,NoSwingConnectorFactory> getHttpdTomcatVersions() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdWorkerService">
    // TODO: final NoSwingHttpdWorkerService httpdWorkers;
    // TODO: public HttpdWorkerService<NoSwingConnector,NoSwingConnectorFactory> getHttpdWorkers() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="IPAddressService">
    static class NoSwingIPAddressService extends NoSwingService<Integer,IPAddress> implements IPAddressService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingIPAddressService(NoSwingConnector connector, IPAddressService<?,?> wrapped) {
            super(connector, Integer.class, IPAddress.class, wrapped);
        }
    }
    final NoSwingIPAddressService ipAddresses;
    public IPAddressService<NoSwingConnector,NoSwingConnectorFactory> getIpAddresses() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return ipAddresses;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="LanguageService">
    static class NoSwingLanguageService extends NoSwingService<String,Language> implements LanguageService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingLanguageService(NoSwingConnector connector, LanguageService<?,?> wrapped) {
            super(connector, String.class, Language.class, wrapped);
        }
    }
    final NoSwingLanguageService languages;
    public LanguageService<NoSwingConnector,NoSwingConnectorFactory> getLanguages() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return languages;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="LinuxAccAddressService">
    // TODO: final NoSwingLinuxAccAddressService linuxAccAddresss;
    // TODO: public LinuxAccAddressService<NoSwingConnector,NoSwingConnectorFactory> getLinuxAccAddresses() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="LinuxAccountGroupService">
    static class NoSwingLinuxAccountGroupService extends NoSwingService<Integer,LinuxAccountGroup> implements LinuxAccountGroupService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingLinuxAccountGroupService(NoSwingConnector connector, LinuxAccountGroupService<?,?> wrapped) {
            super(connector, Integer.class, LinuxAccountGroup.class, wrapped);
        }
    }
    final NoSwingLinuxAccountGroupService linuxAccountGroups;
    public LinuxAccountGroupService<NoSwingConnector,NoSwingConnectorFactory> getLinuxAccountGroups() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return linuxAccountGroups;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="LinuxAccountTypeService">
    static class NoSwingLinuxAccountTypeService extends NoSwingService<String,LinuxAccountType> implements LinuxAccountTypeService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingLinuxAccountTypeService(NoSwingConnector connector, LinuxAccountTypeService<?,?> wrapped) {
            super(connector, String.class, LinuxAccountType.class, wrapped);
        }
    }
    final NoSwingLinuxAccountTypeService linuxAccountTypes;
    public LinuxAccountTypeService<NoSwingConnector,NoSwingConnectorFactory> getLinuxAccountTypes() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return linuxAccountTypes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="LinuxAccountService">
    static class NoSwingLinuxAccountService extends NoSwingService<Integer,LinuxAccount> implements LinuxAccountService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingLinuxAccountService(NoSwingConnector connector, LinuxAccountService<?,?> wrapped) {
            super(connector, Integer.class, LinuxAccount.class, wrapped);
        }
    }
    final NoSwingLinuxAccountService linuxAccounts;
    public LinuxAccountService<NoSwingConnector,NoSwingConnectorFactory> getLinuxAccounts() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return linuxAccounts;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="LinuxGroupTypeService">
    static class NoSwingLinuxGroupTypeService extends NoSwingService<String,LinuxGroupType> implements LinuxGroupTypeService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingLinuxGroupTypeService(NoSwingConnector connector, LinuxGroupTypeService<?,?> wrapped) {
            super(connector, String.class, LinuxGroupType.class, wrapped);
        }
    }
    final NoSwingLinuxGroupTypeService linuxGroupTypes;
    public LinuxGroupTypeService<NoSwingConnector,NoSwingConnectorFactory> getLinuxGroupTypes() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return linuxGroupTypes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="LinuxGroupService">
    static class NoSwingLinuxGroupService extends NoSwingService<Integer,LinuxGroup> implements LinuxGroupService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingLinuxGroupService(NoSwingConnector connector, LinuxGroupService<?,?> wrapped) {
            super(connector, Integer.class, LinuxGroup.class, wrapped);
        }
    }
    final NoSwingLinuxGroupService linuxGroups;
    public LinuxGroupService<NoSwingConnector,NoSwingConnectorFactory> getLinuxGroups() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return linuxGroups;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MajordomoListService">
    // TODO: final NoSwingMajordomoListService majordomoLists;
    // TODO: public MajordomoListService<NoSwingConnector,NoSwingConnectorFactory> getMajordomoLists() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MajordomoServerService">
    // TODO: final NoSwingMajordomoServerService majordomoServers;
    // TODO: public MajordomoServerService<NoSwingConnector,NoSwingConnectorFactory> getMajordomoServers() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MajordomoVersionService">
    // TODO: final NoSwingMajordomoVersionService majordomoVersions;
    // TODO: public MajordomoVersionService<NoSwingConnector,NoSwingConnectorFactory> getMajordomoVersions() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MasterHostService">
    static class NoSwingMasterHostService extends NoSwingService<Integer,MasterHost> implements MasterHostService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingMasterHostService(NoSwingConnector connector, MasterHostService<?,?> wrapped) {
            super(connector, Integer.class, MasterHost.class, wrapped);
        }
    }
    final NoSwingMasterHostService masterHosts;
    public MasterHostService<NoSwingConnector,NoSwingConnectorFactory> getMasterHosts() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return masterHosts;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MasterServerService">
    static class NoSwingMasterServerService extends NoSwingService<Integer,MasterServer> implements MasterServerService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingMasterServerService(NoSwingConnector connector, MasterServerService<?,?> wrapped) {
            super(connector, Integer.class, MasterServer.class, wrapped);
        }
    }
    final NoSwingMasterServerService masterServers;
    public MasterServerService<NoSwingConnector,NoSwingConnectorFactory> getMasterServers() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return masterServers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MasterUserService">
    static class NoSwingMasterUserService extends NoSwingService<UserId,MasterUser> implements MasterUserService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingMasterUserService(NoSwingConnector connector, MasterUserService<?,?> wrapped) {
            super(connector, UserId.class, MasterUser.class, wrapped);
        }
    }
    final NoSwingMasterUserService masterUsers;
    public MasterUserService<NoSwingConnector,NoSwingConnectorFactory> getMasterUsers() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return masterUsers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MonthlyChargeService">
    // TODO: final NoSwingMonthlyChargeService monthlyCharges;
    // TODO: public MonthlyChargeService<NoSwingConnector,NoSwingConnectorFactory> getMonthlyCharges() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MySQLDatabaseService">
    static class NoSwingMySQLDatabaseService extends NoSwingService<Integer,MySQLDatabase> implements MySQLDatabaseService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingMySQLDatabaseService(NoSwingConnector connector, MySQLDatabaseService<?,?> wrapped) {
            super(connector, Integer.class, MySQLDatabase.class, wrapped);
        }
    }
    final NoSwingMySQLDatabaseService mysqlDatabases;
    public MySQLDatabaseService<NoSwingConnector,NoSwingConnectorFactory> getMysqlDatabases() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return mysqlDatabases;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MySQLDBUserService">
    static class NoSwingMySQLDBUserService extends NoSwingService<Integer,MySQLDBUser> implements MySQLDBUserService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingMySQLDBUserService(NoSwingConnector connector, MySQLDBUserService<?,?> wrapped) {
            super(connector, Integer.class, MySQLDBUser.class, wrapped);
        }
    }
    final NoSwingMySQLDBUserService mysqlDBUsers;
    public MySQLDBUserService<NoSwingConnector,NoSwingConnectorFactory> getMysqlDBUsers() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return mysqlDBUsers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MySQLServerService">
    static class NoSwingMySQLServerService extends NoSwingService<Integer,MySQLServer> implements MySQLServerService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingMySQLServerService(NoSwingConnector connector, MySQLServerService<?,?> wrapped) {
            super(connector, Integer.class, MySQLServer.class, wrapped);
        }
    }
    final NoSwingMySQLServerService mysqlServers;
    public MySQLServerService<NoSwingConnector,NoSwingConnectorFactory> getMysqlServers() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return mysqlServers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MySQLUserService">
    static class NoSwingMySQLUserService extends NoSwingService<Integer,MySQLUser> implements MySQLUserService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingMySQLUserService(NoSwingConnector connector, MySQLUserService<?,?> wrapped) {
            super(connector, Integer.class, MySQLUser.class, wrapped);
        }
    }
    final NoSwingMySQLUserService mysqlUsers;
    public MySQLUserService<NoSwingConnector,NoSwingConnectorFactory> getMysqlUsers() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return mysqlUsers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="NetBindService">
    static class NoSwingNetBindService extends NoSwingService<Integer,NetBind> implements NetBindService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingNetBindService(NoSwingConnector connector, NetBindService<?,?> wrapped) {
            super(connector, Integer.class, NetBind.class, wrapped);
        }
    }
    final NoSwingNetBindService netBinds;
    public NetBindService<NoSwingConnector,NoSwingConnectorFactory> getNetBinds() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return netBinds;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="NetDeviceIDService">
    static class NoSwingNetDeviceIDService extends NoSwingService<String,NetDeviceID> implements NetDeviceIDService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingNetDeviceIDService(NoSwingConnector connector, NetDeviceIDService<?,?> wrapped) {
            super(connector, String.class, NetDeviceID.class, wrapped);
        }
    }
    final NoSwingNetDeviceIDService netDeviceIDs;
    public NetDeviceIDService<NoSwingConnector,NoSwingConnectorFactory> getNetDeviceIDs() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return netDeviceIDs;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="NetDeviceService">
    static class NoSwingNetDeviceService extends NoSwingService<Integer,NetDevice> implements NetDeviceService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingNetDeviceService(NoSwingConnector connector, NetDeviceService<?,?> wrapped) {
            super(connector, Integer.class, NetDevice.class, wrapped);
        }
    }
    final NoSwingNetDeviceService netDevices;
    public NetDeviceService<NoSwingConnector,NoSwingConnectorFactory> getNetDevices() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return netDevices;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="NetProtocolService">
    static class NoSwingNetProtocolService extends NoSwingService<String,NetProtocol> implements NetProtocolService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingNetProtocolService(NoSwingConnector connector, NetProtocolService<?,?> wrapped) {
            super(connector, String.class, NetProtocol.class, wrapped);
        }
    }
    final NoSwingNetProtocolService netProtocols;
    public NetProtocolService<NoSwingConnector,NoSwingConnectorFactory> getNetProtocols() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return netProtocols;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="NetTcpRedirectService">
    static class NoSwingNetTcpRedirectService extends NoSwingService<Integer,NetTcpRedirect> implements NetTcpRedirectService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingNetTcpRedirectService(NoSwingConnector connector, NetTcpRedirectService<?,?> wrapped) {
            super(connector, Integer.class, NetTcpRedirect.class, wrapped);
        }
    }
    final NoSwingNetTcpRedirectService netTcpRedirects;
    public NetTcpRedirectService<NoSwingConnector,NoSwingConnectorFactory> getNetTcpRedirects() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return netTcpRedirects;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="NoticeLogService">
    // TODO: final NoSwingNoticeLogService noticeLogs;
    // TODO: public NoticeLogService<NoSwingConnector,NoSwingConnectorFactory> getNoticeLogs() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="NoticeTypeService">
    // TODO: final NoSwingNoticeTypeService noticeTypes;
    // TODO: public NoticeTypeService<NoSwingConnector,NoSwingConnectorFactory> getNoticeTypes() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="OperatingSystemVersionService">
    static class NoSwingOperatingSystemVersionService extends NoSwingService<Integer,OperatingSystemVersion> implements OperatingSystemVersionService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingOperatingSystemVersionService(NoSwingConnector connector, OperatingSystemVersionService<?,?> wrapped) {
            super(connector, Integer.class, OperatingSystemVersion.class, wrapped);
        }
    }
    final NoSwingOperatingSystemVersionService operatingSystemVersions;
    public OperatingSystemVersionService<NoSwingConnector,NoSwingConnectorFactory> getOperatingSystemVersions() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return operatingSystemVersions;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="OperatingSystemService">
    static class NoSwingOperatingSystemService extends NoSwingService<String,OperatingSystem> implements OperatingSystemService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingOperatingSystemService(NoSwingConnector connector, OperatingSystemService<?,?> wrapped) {
            super(connector, String.class, OperatingSystem.class, wrapped);
        }
    }
    final NoSwingOperatingSystemService operatingSystems;
    public OperatingSystemService<NoSwingConnector,NoSwingConnectorFactory> getOperatingSystems() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return operatingSystems;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PackageCategoryService">
    static class NoSwingPackageCategoryService extends NoSwingService<String,PackageCategory> implements PackageCategoryService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingPackageCategoryService(NoSwingConnector connector, PackageCategoryService<?,?> wrapped) {
            super(connector, String.class, PackageCategory.class, wrapped);
        }
    }
    final NoSwingPackageCategoryService packageCategories;
    public PackageCategoryService<NoSwingConnector,NoSwingConnectorFactory> getPackageCategories() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return packageCategories;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PackageDefinitionLimitService">
    // TODO: final NoSwingPackageDefinitionLimitService packageDefinitionLimits;
    // TODO: public PackageDefinitionLimitService<NoSwingConnector,NoSwingConnectorFactory> getPackageDefinitionLimits() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PackageDefinitionService">
    // TODO: final NoSwingPackageDefinitionService packageDefinitions;
    // TODO: public PackageDefinitionService<NoSwingConnector,NoSwingConnectorFactory> getPackageDefinitions() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PaymentTypeService">
    // TODO: final NoSwingPaymentTypeService paymentTypes;
    // TODO: public PaymentTypeService<NoSwingConnector,NoSwingConnectorFactory> getPaymentTypes() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PhysicalServerService">
    // TODO: final NoSwingPhysicalServerService physicalServers;
    // TODO: public PhysicalServerService<NoSwingConnector,NoSwingConnectorFactory> getPhysicalServers() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PostgresDatabaseService">
    static class NoSwingPostgresDatabaseService extends NoSwingService<Integer,PostgresDatabase> implements PostgresDatabaseService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingPostgresDatabaseService(NoSwingConnector connector, PostgresDatabaseService<?,?> wrapped) {
            super(connector, Integer.class, PostgresDatabase.class, wrapped);
        }
    }
    final NoSwingPostgresDatabaseService postgresDatabases;
    public PostgresDatabaseService<NoSwingConnector,NoSwingConnectorFactory> getPostgresDatabases() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return postgresDatabases;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PostgresEncodingService">
    static class NoSwingPostgresEncodingService extends NoSwingService<Integer,PostgresEncoding> implements PostgresEncodingService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingPostgresEncodingService(NoSwingConnector connector, PostgresEncodingService<?,?> wrapped) {
            super(connector, Integer.class, PostgresEncoding.class, wrapped);
        }
    }
    final NoSwingPostgresEncodingService postgresEncodings;
    public PostgresEncodingService<NoSwingConnector,NoSwingConnectorFactory> getPostgresEncodings() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return postgresEncodings;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PostgresServerService">
    static class NoSwingPostgresServerService extends NoSwingService<Integer,PostgresServer> implements PostgresServerService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingPostgresServerService(NoSwingConnector connector, PostgresServerService<?,?> wrapped) {
            super(connector, Integer.class, PostgresServer.class, wrapped);
        }
    }
    final NoSwingPostgresServerService postgresServers;
    public PostgresServerService<NoSwingConnector,NoSwingConnectorFactory> getPostgresServers() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return postgresServers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PostgresUserService">
    static class NoSwingPostgresUserService extends NoSwingService<Integer,PostgresUser> implements PostgresUserService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingPostgresUserService(NoSwingConnector connector, PostgresUserService<?,?> wrapped) {
            super(connector, Integer.class, PostgresUser.class, wrapped);
        }
    }
    final NoSwingPostgresUserService postgresUsers;
    public PostgresUserService<NoSwingConnector,NoSwingConnectorFactory> getPostgresUsers() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return postgresUsers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PostgresVersionService">
    static class NoSwingPostgresVersionService extends NoSwingService<Integer,PostgresVersion> implements PostgresVersionService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingPostgresVersionService(NoSwingConnector connector, PostgresVersionService<?,?> wrapped) {
            super(connector, Integer.class, PostgresVersion.class, wrapped);
        }
    }
    final NoSwingPostgresVersionService postgresVersions;
    public PostgresVersionService<NoSwingConnector,NoSwingConnectorFactory> getPostgresVersions() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return postgresVersions;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PrivateFtpServerService">
    // TODO: final NoSwingPrivateFtpServerService privateFtpServers;
    // TODO: public PrivateFtpServerService<NoSwingConnector,NoSwingConnectorFactory> getPrivateFtpServers() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ProcessorTypeService">
    // TODO: final NoSwingProcessorTypeService processorTypes;
    // TODO: public ProcessorTypeService<NoSwingConnector,NoSwingConnectorFactory> getProcessorTypes() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ProtocolService">
    static class NoSwingProtocolService extends NoSwingService<String,Protocol> implements ProtocolService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingProtocolService(NoSwingConnector connector, ProtocolService<?,?> wrapped) {
            super(connector, String.class, Protocol.class, wrapped);
        }
    }
    final NoSwingProtocolService protocols;
    public ProtocolService<NoSwingConnector,NoSwingConnectorFactory> getProtocols() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return protocols;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="RackService">
    // TODO: final NoSwingRackService racks;
    // TODO: public RackService<NoSwingConnector,NoSwingConnectorFactory> getRacks() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ResellerService">
    static class NoSwingResellerService extends NoSwingService<AccountingCode,Reseller> implements ResellerService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingResellerService(NoSwingConnector connector, ResellerService<?,?> wrapped) {
            super(connector, AccountingCode.class, Reseller.class, wrapped);
        }
    }
    final NoSwingResellerService resellers;
    public ResellerService<NoSwingConnector,NoSwingConnectorFactory> getResellers() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return resellers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ResourceTypeService">
    static class NoSwingResourceTypeService extends NoSwingService<String,ResourceType> implements ResourceTypeService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingResourceTypeService(NoSwingConnector connector, ResourceTypeService<?,?> wrapped) {
            super(connector, String.class, ResourceType.class, wrapped);
        }
    }
    final NoSwingResourceTypeService resourceTypes;
    public ResourceTypeService<NoSwingConnector,NoSwingConnectorFactory> getResourceTypes() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return resourceTypes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ResourceService">
    static class NoSwingResourceService extends NoSwingService<Integer,Resource> implements ResourceService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingResourceService(NoSwingConnector connector, ResourceService<?,?> wrapped) {
            super(connector, Integer.class, Resource.class, wrapped);
        }
    }
    final NoSwingResourceService resources;
    public ResourceService<NoSwingConnector,NoSwingConnectorFactory> getResources() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return resources;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ServerFarmService">
    static class NoSwingServerFarmService extends NoSwingService<DomainLabel,ServerFarm> implements ServerFarmService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingServerFarmService(NoSwingConnector connector, ServerFarmService<?,?> wrapped) {
            super(connector, DomainLabel.class, ServerFarm.class, wrapped);
        }
    }
    final NoSwingServerFarmService serverFarms;
    public ServerFarmService<NoSwingConnector,NoSwingConnectorFactory> getServerFarms() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return serverFarms;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ServerResourceService">
    static class NoSwingServerResourceService extends NoSwingService<Integer,ServerResource> implements ServerResourceService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingServerResourceService(NoSwingConnector connector, ServerResourceService<?,?> wrapped) {
            super(connector, Integer.class, ServerResource.class, wrapped);
        }
    }
    final NoSwingServerResourceService serverResources;
    public ServerResourceService<NoSwingConnector,NoSwingConnectorFactory> getServerResources() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return serverResources;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ServerService">
    static class NoSwingServerService extends NoSwingService<Integer,Server> implements ServerService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingServerService(NoSwingConnector connector, ServerService<?,?> wrapped) {
            super(connector, Integer.class, Server.class, wrapped);
        }
    }
    final NoSwingServerService servers;
    public ServerService<NoSwingConnector,NoSwingConnectorFactory> getServers() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return servers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ShellService">
    static class NoSwingShellService extends NoSwingService<UnixPath,Shell> implements ShellService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingShellService(NoSwingConnector connector, ShellService<?,?> wrapped) {
            super(connector, UnixPath.class, Shell.class, wrapped);
        }
    }
    final NoSwingShellService shells;
    public ShellService<NoSwingConnector,NoSwingConnectorFactory> getShells() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return shells;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="SignupRequestOptionService">
    // TODO: final NoSwingSignupRequestOptionService signupRequestOptions;
    // TODO: public SignupRequestOptionService<NoSwingConnector,NoSwingConnectorFactory> getSignupRequestOptions() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="SignupRequestService">
    // TODO: final NoSwingSignupRequestService signupRequests;
    // TODO: public SignupRequestService<NoSwingConnector,NoSwingConnectorFactory> getSignupRequests() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="SpamEmailMessageService">
    // TODO: final NoSwingSpamEmailMessageService spamEmailMessages;
    // TODO: public SpamEmailMessageService<NoSwingConnector,NoSwingConnectorFactory> getSpamEmailMessages() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="SystemEmailAliasService">
    // TODO: final NoSwingSystemEmailAliasService systemEmailAliass;
    // TODO: public SystemEmailAliasService<NoSwingConnector,NoSwingConnectorFactory> getSystemEmailAliases() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TechnologyService">
    final class NoSwingTechnologyService extends NoSwingService<Integer,Technology> implements TechnologyService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingTechnologyService(NoSwingConnector connector, TechnologyService<?,?> wrapped) {
            super(connector, Integer.class, Technology.class, wrapped);
        }
    }
    final NoSwingTechnologyService technologies;
    public TechnologyService<NoSwingConnector,NoSwingConnectorFactory> getTechnologies() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return technologies;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TechnologyClassService">
    static class NoSwingTechnologyClassService extends NoSwingService<String,TechnologyClass> implements TechnologyClassService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingTechnologyClassService(NoSwingConnector connector, TechnologyClassService<?,?> wrapped) {
            super(connector, String.class, TechnologyClass.class, wrapped);
        }
    }
    final NoSwingTechnologyClassService technologyClasses;
    public TechnologyClassService<NoSwingConnector,NoSwingConnectorFactory> getTechnologyClasses() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return technologyClasses;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TechnologyNameService">
    static class NoSwingTechnologyNameService extends NoSwingService<String,TechnologyName> implements TechnologyNameService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingTechnologyNameService(NoSwingConnector connector, TechnologyNameService<?,?> wrapped) {
            super(connector, String.class, TechnologyName.class, wrapped);
        }
    }
    final NoSwingTechnologyNameService technologyNames;
    public TechnologyNameService<NoSwingConnector,NoSwingConnectorFactory> getTechnologyNames() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return technologyNames;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TechnologyVersionService">
    static class NoSwingTechnologyVersionService extends NoSwingService<Integer,TechnologyVersion> implements TechnologyVersionService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingTechnologyVersionService(NoSwingConnector connector, TechnologyVersionService<?,?> wrapped) {
            super(connector, Integer.class, TechnologyVersion.class, wrapped);
        }
    }
    final NoSwingTechnologyVersionService technologyVersions;
    public TechnologyVersionService<NoSwingConnector,NoSwingConnectorFactory> getTechnologyVersions() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return technologyVersions;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TicketActionTypeService">
    // TODO: final NoSwingTicketActionTypeService ticketActionTypes;
    // TODO: public TicketActionTypeService<NoSwingConnector,NoSwingConnectorFactory> getTicketActionTypes() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TicketActionService">
    // TODO: final NoSwingTicketActionService ticketActions;
    // TODO: public TicketActionService<NoSwingConnector,NoSwingConnectorFactory> getTicketActions() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TicketAssignmentService">
    static class NoSwingTicketAssignmentService extends NoSwingService<Integer,TicketAssignment> implements TicketAssignmentService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingTicketAssignmentService(NoSwingConnector connector, TicketAssignmentService<?,?> wrapped) {
            super(connector, Integer.class, TicketAssignment.class, wrapped);
        }
    }
    final NoSwingTicketAssignmentService ticketAssignments;
    public TicketAssignmentService<NoSwingConnector,NoSwingConnectorFactory> getTicketAssignments() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return ticketAssignments;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TicketBrandCategoryService">
    // TODO: final NoSwingTicketBrandCategoryService ticketBrandCategories;
    // TODO: public TicketBrandCategoryService<NoSwingConnector,NoSwingConnectorFactory> getTicketBrandCategories() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TicketCategoryService">
    static class NoSwingTicketCategoryService extends NoSwingService<Integer,TicketCategory> implements TicketCategoryService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingTicketCategoryService(NoSwingConnector connector, TicketCategoryService<?,?> wrapped) {
            super(connector, Integer.class, TicketCategory.class, wrapped);
        }
    }
    final NoSwingTicketCategoryService ticketCategories;
    public TicketCategoryService<NoSwingConnector,NoSwingConnectorFactory> getTicketCategories() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return ticketCategories;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TicketPriorityService">
    static class NoSwingTicketPriorityService extends NoSwingService<String,TicketPriority> implements TicketPriorityService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingTicketPriorityService(NoSwingConnector connector, TicketPriorityService<?,?> wrapped) {
            super(connector, String.class, TicketPriority.class, wrapped);
        }
    }
    final NoSwingTicketPriorityService ticketPriorities;
    public TicketPriorityService<NoSwingConnector,NoSwingConnectorFactory> getTicketPriorities() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return ticketPriorities;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TicketStatusService">
    static class NoSwingTicketStatusService extends NoSwingService<String,TicketStatus> implements TicketStatusService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingTicketStatusService(NoSwingConnector connector, TicketStatusService<?,?> wrapped) {
            super(connector, String.class, TicketStatus.class, wrapped);
        }
    }
    final NoSwingTicketStatusService ticketStatuses;
    public TicketStatusService<NoSwingConnector,NoSwingConnectorFactory> getTicketStatuses() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return ticketStatuses;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TicketTypeService">
    static class NoSwingTicketTypeService extends NoSwingService<String,TicketType> implements TicketTypeService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingTicketTypeService(NoSwingConnector connector, TicketTypeService<?,?> wrapped) {
            super(connector, String.class, TicketType.class, wrapped);
        }
    }
    final NoSwingTicketTypeService ticketTypes;
    public TicketTypeService<NoSwingConnector,NoSwingConnectorFactory> getTicketTypes() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return ticketTypes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TicketService">
    static class NoSwingTicketService extends NoSwingService<Integer,Ticket> implements TicketService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingTicketService(NoSwingConnector connector, TicketService<?,?> wrapped) {
            super(connector, Integer.class, Ticket.class, wrapped);
        }
    }
    final NoSwingTicketService tickets;
    public TicketService<NoSwingConnector,NoSwingConnectorFactory> getTickets() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return tickets;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TimeZoneService">
    static class NoSwingTimeZoneService extends NoSwingService<String,TimeZone> implements TimeZoneService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingTimeZoneService(NoSwingConnector connector, TimeZoneService<?,?> wrapped) {
            super(connector, String.class, TimeZone.class, wrapped);
        }
    }
    final NoSwingTimeZoneService timeZones;
    public TimeZoneService<NoSwingConnector,NoSwingConnectorFactory> getTimeZones() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return timeZones;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TransactionTypeService">
    // TODO: final NoSwingTransactionTypeService transactionTypes;
    // TODO: public TransactionTypeService<NoSwingConnector,NoSwingConnectorFactory> getTransactionTypes() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TransactionService">
    // TODO: final NoSwingTransactionService transactions;
    // TODO: public TransactionService<NoSwingConnector,NoSwingConnectorFactory> getTransactions() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="UsernameService">
    static class NoSwingUsernameService extends NoSwingService<UserId,Username> implements UsernameService<NoSwingConnector,NoSwingConnectorFactory> {
        NoSwingUsernameService(NoSwingConnector connector, UsernameService<?,?> wrapped) {
            super(connector, UserId.class, Username.class, wrapped);
        }
    }
    final NoSwingUsernameService usernames;
    public UsernameService<NoSwingConnector,NoSwingConnectorFactory> getUsernames() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return usernames;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="VirtualDiskService">
    // TODO: final NoSwingVirtualDiskService virtualDisks;
    // TODO: public VirtualDiskService<NoSwingConnector,NoSwingConnectorFactory> getVirtualDisks() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="VirtualServerService">
    // TODO: final NoSwingVirtualServerService virtualServers;
    // TODO: public VirtualServerService<NoSwingConnector,NoSwingConnectorFactory> getVirtualServers() throws RemoteException;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="WhoisHistoryService">
    // TODO: final NoSwingWhoisHistoryService whoisHistories;
    // TODO: public WhoisHistoryService<NoSwingConnector,NoSwingConnectorFactory> getWhoisHistory() throws RemoteException;
    // </editor-fold>
}
