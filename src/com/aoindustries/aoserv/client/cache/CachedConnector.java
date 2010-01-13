package com.aoindustries.aoserv.client.cache;

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
import com.aoindustries.aoserv.client.GroupName;
import com.aoindustries.aoserv.client.GroupNameService;
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
import com.aoindustries.aoserv.client.TicketCategory;
import com.aoindustries.aoserv.client.TicketCategoryService;
import com.aoindustries.aoserv.client.TicketPriority;
import com.aoindustries.aoserv.client.TicketPriorityService;
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
import com.aoindustries.aoserv.client.validator.GroupId;
import com.aoindustries.aoserv.client.validator.UnixPath;
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.security.LoginException;
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * An implementation of <code>AOServConnector</code> that transfers entire
 * tables as a set and performs local lookups.
 *
 * @author  AO Industries, Inc.
 */
final public class CachedConnector implements AOServConnector<CachedConnector,CachedConnectorFactory> {

    final CachedConnectorFactory factory;
    final AOServConnector<?,?> wrapped;
    Locale locale;
    final UserId connectAs;
    private final UserId authenticateAs;
    private final String password;

    CachedConnector(CachedConnectorFactory factory, AOServConnector<?,?> wrapped) throws RemoteException, LoginException {
        this.factory = factory;
        this.wrapped = wrapped;
        locale = wrapped.getLocale();
        connectAs = wrapped.getConnectAs();
        authenticateAs = wrapped.getAuthenticateAs();
        password = wrapped.getPassword();
        aoserverDaemonHosts = new CachedAOServerDaemonHostService(this, wrapped.getAoServerDaemonHosts());
        aoserverResources = new CachedAOServerResourceService(this, wrapped.getAoServerResources());
        aoservers = new CachedAOServerService(this, wrapped.getAoServers());
        aoservPermissions = new CachedAOServPermissionService(this, wrapped.getAoservPermissions());
        /* TODO
        aoservProtocols = new CachedAOServProtocolService(this, wrapped.getAOServProtocols());
        aoshCommands = new CachedAOSHCommandService(this, wrapped.getAOSHCommands());
         */
        architectures = new CachedArchitectureService(this, wrapped.getArchitectures());
        backupPartitions = new CachedBackupPartitionService(this, wrapped.getBackupPartitions());
        backupRetentions = new CachedBackupRetentionService(this, wrapped.getBackupRetentions());
        /* TODO
        bankAccounts = new CachedBankAccountService(this, wrapped.getBankAccounts());
        bankTransactionTypes = new CachedBankTransactionTypeService(this, wrapped.getBankTransactionTypes());
        bankTransactions = new CachedBankTransactionService(this, wrapped.getBankTransactions());
        banks = new CachedBankService(this, wrapped.getBanks());
        blackholeEmailAddresss = new CachedBlackholeEmailAddressService(this, wrapped.getBlackholeEmailAddresss());
        brands = new CachedBrandService(this, wrapped.getBrands());
         */
        businessAdministrators = new CachedBusinessAdministratorService(this, wrapped.getBusinessAdministrators());
        /* TODO
        businessAdministratorPermissions = new CachedBusinessAdministratorPermissionService(this, wrapped.getBusinessAdministratorPermissions());
        businessProfiles = new CachedBusinessProfileService(this, wrapped.getBusinessProfiles());
         */
        businesses = new CachedBusinessService(this, wrapped.getBusinesses());
        businessServers = new CachedBusinessServerService(this, wrapped.getBusinessServers());
        countryCodes = new CachedCountryCodeService(this, wrapped.getCountryCodes());
        /* TODO
        creditCardProcessors = new CachedCreditCardProcessorService(this, wrapped.getCreditCardProcessors());
        creditCardTransactions = new CachedCreditCardTransactionService(this, wrapped.getCreditCardTransactions());
        creditCards = new CachedCreditCardService(this, wrapped.getCreditCards());
         */
        cvsRepositories = new CachedCvsRepositoryService(this, wrapped.getCvsRepositories());
        disableLogs = new CachedDisableLogService(this, wrapped.getDisableLogs());
        /*
        distroFileTypes = new CachedDistroFileTypeService(this, wrapped.getDistroFileTypes());
        distroFiles = new CachedDistroFileService(this, wrapped.getDistroFiles());
        dnsForbiddenZones = new CachedDNSForbiddenZoneService(this, wrapped.getDNSForbiddenZones());
        dnsRecords = new CachedDNSRecordService(this, wrapped.getDNSRecords());
        dnsTLDs = new CachedDNSTLDService(this, wrapped.getDNSTLDs());
        dnsTypes = new CachedDNSTypeService(this, wrapped.getDNSTypes());
        dnsZones = new CachedDNSZoneService(this, wrapped.getDNSZones());
        emailAddresss = new CachedEmailAddressService(this, wrapped.getEmailAddresss());
        emailAttachmentBlocks = new CachedEmailAttachmentBlockService(this, wrapped.getEmailAttachmentBlocks());
        emailAttachmentTypes = new CachedEmailAttachmentTypeService(this, wrapped.getEmailAttachmentTypes());
        emailDomains = new CachedEmailDomainService(this, wrapped.getEmailDomains());
        emailForwardings = new CachedEmailForwardingService(this, wrapped.getEmailForwardings());
        emailListAddresss = new CachedEmailListAddressService(this, wrapped.getEmailListAddresss());
        emailLists = new CachedEmailListService(this, wrapped.getEmailLists());
        emailPipeAddresss = new CachedEmailPipeAddressService(this, wrapped.getEmailPipeAddresss());
        emailPipes = new CachedEmailPipeService(this, wrapped.getEmailPipes());
        emailSmtpRelayTypes = new CachedEmailSmtpRelayTypeService(this, wrapped.getEmailSmtpRelayTypes());
        emailSmtpRelays = new CachedEmailSmtpRelayService(this, wrapped.getEmailSmtpRelays());
        emailSmtpSmartHostDomains = new CachedEmailSmtpSmartHostDomainService(this, wrapped.getEmailSmtpSmartHostDomains());
        emailSmtpSmartHosts = new CachedEmailSmtpSmartHostService(this, wrapped.getEmailSmtpSmartHosts());
        emailSpamAssassinIntegrationModes = new CachedEmailSpamAssassinIntegrationModeService(this, wrapped.getEmailSpamAssassinIntegrationModes());
        encryptionKeys = new CachedEncryptionKeyService(this, wrapped.getEncryptionKeys());
        expenseCategories = new CachedExpenseCategoryService(this, wrapped.getExpenseCategorys());
         */
        failoverFileLogs = new CachedFailoverFileLogService(this, wrapped.getFailoverFileLogs());
        failoverFileReplications = new CachedFailoverFileReplicationService(this, wrapped.getFailoverFileReplications());
        failoverFileSchedules = new CachedFailoverFileScheduleService(this, wrapped.getFailoverFileSchedules());
        failoverMySQLReplications = new CachedFailoverMySQLReplicationService(this, wrapped.getFailoverMySQLReplications());
        fileBackupSettings = new CachedFileBackupSettingService(this, wrapped.getFileBackupSettings());
        groupNames = new CachedGroupNameService(this, wrapped.getGroupNames());
        /* TODO
        ftpGuestUsers = new CachedFTPGuestUserService(this, wrapped.getFTPGuestUsers());
        httpdBinds = new CachedHttpdBindService(this, wrapped.getHttpdBinds());
        httpdJBossSites = new CachedHttpdJBossSiteService(this, wrapped.getHttpdJBossSites());
        httpdJBossVersions = new CachedHttpdJBossVersionService(this, wrapped.getHttpdJBossVersions());
        httpdJKCodes = new CachedHttpdJKCodeService(this, wrapped.getHttpdJKCodes());
        httpdJKProtocols = new CachedHttpdJKProtocolService(this, wrapped.getHttpdJKProtocols());
        httpdServers = new CachedHttpdServerService(this, wrapped.getHttpdServers());
        httpdSharedTomcats = new CachedHttpdSharedTomcatService(this, wrapped.getHttpdSharedTomcats());
        httpdSiteAuthenticatedLocations = new CachedHttpdSiteAuthenticatedLocationService(this, wrapped.getHttpdSiteAuthenticatedLocations());
        httpdSiteBinds = new CachedHttpdSiteBindService(this, wrapped.getHttpdSiteBinds());
        httpdSiteURLs = new CachedHttpdSiteURLService(this, wrapped.getHttpdSiteURLs());
         */
        httpdSites = new CachedHttpdSiteService(this, wrapped.getHttpdSites());
        // TODO: httpdStaticSites = new CachedHttpdStaticSiteService(this, wrapped.getHttpdStaticSites());
        // TODO: httpdTomcatContexts = new CachedHttpdTomcatContextService(this, wrapped.getHttpdTomcatContexts());
        // TODO: httpdTomcatDataSources = new CachedHttpdTomcatDataSourceService(this, wrapped.getHttpdTomcatDataSources());
        // TODO: httpdTomcatParameters = new CachedHttpdTomcatParameterService(this, wrapped.getHttpdTomcatParameters());
        // TODO: httpdTomcatSites = new CachedHttpdTomcatSiteService(this, wrapped.getHttpdTomcatSites());
        // TODO: httpdTomcatSharedSites = new CachedHttpdTomcatSharedSiteService(this, wrapped.getHttpdTomcatSharedSites());
        // TODO: httpdTomcatStdSites = new CachedHttpdTomcatStdSiteService(this, wrapped.getHttpdTomcatStdSites());
        // TODO: httpdTomcatVersions = new CachedHttpdTomcatVersionService(this, wrapped.getHttpdTomcatVersions());
        // TODO: httpdWorkers = new CachedHttpdWorkerService(this, wrapped.getHttpdWorkers());
        ipAddresses = new CachedIPAddressService(this, wrapped.getIpAddresses());
        languages = new CachedLanguageService(this, wrapped.getLanguages());
        /* TODO
        linuxAccAddresss = new CachedLinuxAccAddressService(this, wrapped.getLinuxAccAddresss());
         */
        linuxAccountGroups = new CachedLinuxAccountGroupService(this, wrapped.getLinuxAccountGroups());
        linuxAccountTypes = new CachedLinuxAccountTypeService(this, wrapped.getLinuxAccountTypes());
        linuxAccounts = new CachedLinuxAccountService(this, wrapped.getLinuxAccounts());
        linuxGroupTypes = new CachedLinuxGroupTypeService(this, wrapped.getLinuxGroupTypes());
        linuxGroups = new CachedLinuxGroupService(this, wrapped.getLinuxGroups());
        /* TODO
        majordomoLists = new CachedMajordomoListService(this, wrapped.getMajordomoLists());
        majordomoServers = new CachedMajordomoServerService(this, wrapped.getMajordomoServers());
        majordomoVersions = new CachedMajordomoVersionService(this, wrapped.getMajordomoVersions());
        masterHistories = new CachedMasterHistoryService(this, wrapped.getMasterHistorys());
        masterHosts = new CachedMasterHostService(this, wrapped.getMasterHosts());
        masterServers = new CachedMasterServerService(this, wrapped.getMasterServers());
        masterUsers = new CachedMasterUserService(this, wrapped.getMasterUsers());
        monthlyCharges = new CachedMonthlyChargeService(this, wrapped.getMonthlyCharges());
         */
        mysqlDatabases = new CachedMySQLDatabaseService(this, wrapped.getMysqlDatabases());
        mysqlDBUsers = new CachedMySQLDBUserService(this, wrapped.getMysqlDBUsers());
        mysqlServers = new CachedMySQLServerService(this, wrapped.getMysqlServers());
        mysqlUsers = new CachedMySQLUserService(this, wrapped.getMysqlUsers());
        netBinds = new CachedNetBindService(this, wrapped.getNetBinds());
        netDeviceIDs = new CachedNetDeviceIDService(this, wrapped.getNetDeviceIDs());
        netDevices = new CachedNetDeviceService(this, wrapped.getNetDevices());
        netProtocols = new CachedNetProtocolService(this, wrapped.getNetProtocols());
        netTcpRedirects = new CachedNetTcpRedirectService(this, wrapped.getNetTcpRedirects());
        /* TODO
        noticeLogs = new CachedNoticeLogService(this, wrapped.getNoticeLogs());
        noticeTypes = new CachedNoticeTypeService(this, wrapped.getNoticeTypes());
        */
        operatingSystemVersions = new CachedOperatingSystemVersionService(this, wrapped.getOperatingSystemVersions());
        operatingSystems = new CachedOperatingSystemService(this, wrapped.getOperatingSystems());
        packageCategories = new CachedPackageCategoryService(this, wrapped.getPackageCategories());
        /* TODO
        packageDefinitionLimits = new CachedPackageDefinitionLimitService(this, wrapped.getPackageDefinitionLimits());
        packageDefinitions = new CachedPackageDefinitionService(this, wrapped.getPackageDefinitions());
        paymentTypes = new CachedPaymentTypeService(this, wrapped.getPaymentTypes());
        physicalServers = new CachedPhysicalServerService(this, wrapped.getPhysicalServers());
         */
        postgresDatabases = new CachedPostgresDatabaseService(this, wrapped.getPostgresDatabases());
        postgresEncodings = new CachedPostgresEncodingService(this, wrapped.getPostgresEncodings());
        postgresServers = new CachedPostgresServerService(this, wrapped.getPostgresServers());
        postgresUsers = new CachedPostgresUserService(this, wrapped.getPostgresUsers());
        postgresVersions = new CachedPostgresVersionService(this, wrapped.getPostgresVersions());
        // TODO: privateFTPServers = new CachedPrivateFTPServerService(this, wrapped.getPrivateFTPServers());
        // TODO: processorTypes = new CachedProcessorTypeService(this, wrapped.getProcessorTypes());
        protocols = new CachedProtocolService(this, wrapped.getProtocols());
        /* TODO
        racks = new CachedRackService(this, wrapped.getRacks());
        resellers = new CachedResellerService(this, wrapped.getResellers());
         */
        resourceTypes = new CachedResourceTypeService(this, wrapped.getResourceTypes());
        resources = new CachedResourceService(this, wrapped.getResources());
        serverFarms = new CachedServerFarmService(this, wrapped.getServerFarms());
        serverResources = new CachedServerResourceService(this, wrapped.getServerResources());
        servers = new CachedServerService(this, wrapped.getServers());
        shells = new CachedShellService(this, wrapped.getShells());
        /* TODO
        signupRequestOptions = new CachedSignupRequestOptionService(this, wrapped.getSignupRequestOptions());
        signupRequests = new CachedSignupRequestService(this, wrapped.getSignupRequests());
        spamEmailMessages = new CachedSpamEmailMessageService(this, wrapped.getSpamEmailMessages());
        systemEmailAliass = new CachedSystemEmailAliasService(this, wrapped.getSystemEmailAliass());
         */
        technologies = new CachedTechnologyService(this, wrapped.getTechnologies());
        technologyClasses = new CachedTechnologyClassService(this, wrapped.getTechnologyClasses());
        technologyNames = new CachedTechnologyNameService(this, wrapped.getTechnologyNames());
        technologyVersions = new CachedTechnologyVersionService(this, wrapped.getTechnologyVersions());
        /* TODO
        ticketActionTypes = new CachedTicketActionTypeService(this, wrapped.getTicketActionTypes());
        ticketActions = new CachedTicketActionService(this, wrapped.getTicketActions());
        ticketAssignments = new CachedTicketAssignmentService(this, wrapped.getTicketAssignments());
        ticketBrandCategories = new CachedTicketBrandCategoryService(this, wrapped.getTicketBrandCategorys());
        */
        ticketCategories = new CachedTicketCategoryService(this, wrapped.getTicketCategories());
        ticketPriorities = new CachedTicketPriorityService(this, wrapped.getTicketPriorities());
        ticketStatuses = new CachedTicketStatusService(this, wrapped.getTicketStatuses());
        ticketTypes = new CachedTicketTypeService(this, wrapped.getTicketTypes());
        /* TODO
        tickets = new CachedTicketService(this, wrapped.getTickets());
        */
        timeZones = new CachedTimeZoneService(this, wrapped.getTimeZones());
        /* TODO
        transactionTypes = new CachedTransactionTypeService(this, wrapped.getTransactionTypes());
        transactions = new CachedTransactionService(this, wrapped.getTransactions());
        usStates = new CachedUSStateService(this, wrapped.getUSStates());
         */
        usernames = new CachedUsernameService(this, wrapped.getUsernames());
        /* TODO
        virtualDisks = new CachedVirtualDiskService(this, wrapped.getVirtualDisks());
        virtualServers = new CachedVirtualServerService(this, wrapped.getVirtualServers());
        whoisHistories = new CachedWhoisHistoryService(this, wrapped.getWhoisHistorys());
         */
    }

    public CachedConnectorFactory getFactory() {
        return factory;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) throws RemoteException {
        if(!this.locale.equals(locale)) {
            wrapped.setLocale(locale);
            this.locale = locale;
        }
    }

    public UserId getConnectAs() {
        return connectAs;
    }

    public BusinessAdministrator getThisBusinessAdministrator() throws RemoteException {
        return getBusinessAdministrators().get(connectAs);
    }

    public UserId getAuthenticateAs() {
        return authenticateAs;
    }

    public String getPassword() {
        return password;
    }

    public <R> R executeCommand(AOServCommand<R> command, boolean isInteractive) throws RemoteException {
        return wrapped.executeCommand(command, isInteractive);
    }

    private final AtomicReference<Map<ServiceName,AOServService<CachedConnector,CachedConnectorFactory,?,?>>> tables = new AtomicReference<Map<ServiceName,AOServService<CachedConnector,CachedConnectorFactory,?,?>>>();
    final public Map<ServiceName,AOServService<CachedConnector,CachedConnectorFactory,?,?>> getServices() throws RemoteException {
        Map<ServiceName,AOServService<CachedConnector,CachedConnectorFactory,?,?>> ts = tables.get();
        if(ts==null) {
            ts = AOServConnectorUtils.createServiceMap(this);
            if(!tables.compareAndSet(null, ts)) ts = tables.get();
        }
        return ts;
    }

    // <editor-fold defaultstate="collapsed" desc="AOServerDaemonHostService">
    static class CachedAOServerDaemonHostService extends CachedService<Integer,AOServerDaemonHost> implements AOServerDaemonHostService<CachedConnector,CachedConnectorFactory> {
        CachedAOServerDaemonHostService(CachedConnector connector, AOServerDaemonHostService<?,?> wrapped) {
            super(connector, Integer.class, AOServerDaemonHost.class, wrapped);
        }
    }
    final CachedAOServerDaemonHostService aoserverDaemonHosts;
    public AOServerDaemonHostService<CachedConnector,CachedConnectorFactory> getAoServerDaemonHosts() {
        return aoserverDaemonHosts;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="AOServerResourceService">
    static class CachedAOServerResourceService extends CachedService<Integer,AOServerResource> implements AOServerResourceService<CachedConnector,CachedConnectorFactory> {
        CachedAOServerResourceService(CachedConnector connector, AOServerResourceService<?,?> wrapped) {
            super(connector, Integer.class, AOServerResource.class, wrapped);
        }
    }
    final CachedAOServerResourceService aoserverResources;
    public AOServerResourceService<CachedConnector,CachedConnectorFactory> getAoServerResources() {
        return aoserverResources;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="AOServerService">
    static class CachedAOServerService extends CachedService<Integer,AOServer> implements AOServerService<CachedConnector,CachedConnectorFactory> {
        CachedAOServerService(CachedConnector connector, AOServerService<?,?> wrapped) {
            super(connector, Integer.class, AOServer.class, wrapped);
        }
    }
    final CachedAOServerService aoservers;
    public AOServerService<CachedConnector,CachedConnectorFactory> getAoServers() {
        return aoservers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="AOServPermissionService">
    static class CachedAOServPermissionService extends CachedService<String,AOServPermission> implements AOServPermissionService<CachedConnector,CachedConnectorFactory> {
        CachedAOServPermissionService(CachedConnector connector, AOServPermissionService<?,?> wrapped) {
            super(connector, String.class, AOServPermission.class, wrapped);
        }
    }
    final CachedAOServPermissionService aoservPermissions;
    public AOServPermissionService<CachedConnector,CachedConnectorFactory> getAoservPermissions() {
        return aoservPermissions;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ArchitectureService">
    static class CachedArchitectureService extends CachedService<String,Architecture> implements ArchitectureService<CachedConnector,CachedConnectorFactory> {
        CachedArchitectureService(CachedConnector connector, ArchitectureService<?,?> wrapped) {
            super(connector, String.class, Architecture.class, wrapped);
        }
    }
    final CachedArchitectureService architectures;
    public ArchitectureService<CachedConnector,CachedConnectorFactory> getArchitectures() {
        return architectures;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BackupPartitionService">
    static class CachedBackupPartitionService extends CachedService<Integer,BackupPartition> implements BackupPartitionService<CachedConnector,CachedConnectorFactory> {
        CachedBackupPartitionService(CachedConnector connector, BackupPartitionService<?,?> wrapped) {
            super(connector, Integer.class, BackupPartition.class, wrapped);
        }
    }
    final CachedBackupPartitionService backupPartitions;
    public BackupPartitionService<CachedConnector,CachedConnectorFactory> getBackupPartitions() {
        return backupPartitions;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BackupRetentionService">
    static class CachedBackupRetentionService extends CachedService<Short,BackupRetention> implements BackupRetentionService<CachedConnector,CachedConnectorFactory> {
        CachedBackupRetentionService(CachedConnector connector, BackupRetentionService<?,?> wrapped) {
            super(connector, Short.class, BackupRetention.class, wrapped);
        }
    }
    final CachedBackupRetentionService backupRetentions;
    public BackupRetentionService<CachedConnector,CachedConnectorFactory> getBackupRetentions() {
        return backupRetentions;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BankAccountService">
    // TODO: final CachedBankAccountService bankAccounts;
    // TODO: public BankAccountService<CachedConnector,CachedConnectorFactory> getBankAccounts();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BankTransactionTypeService">
    // TODO: final CachedBankTransactionTypeService bankTransactionTypes;
    // TODO: public BankTransactionTypeService<CachedConnector,CachedConnectorFactory> getBankTransactionTypes();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BankTransactionService">
    // TODO: final CachedBankTransactionService bankTransactions;
    // TODO: public BankTransactionService<CachedConnector,CachedConnectorFactory> getBankTransactions();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BankService">
    // TODO: final CachedBankService banks;
    // TODO: public BankService<CachedConnector,CachedConnectorFactory> getBanks();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BlackholeEmailAddressService">
    // TODO: final CachedBlackholeEmailAddressService blackholeEmailAddresss;
    // TODO: public BlackholeEmailAddressService<CachedConnector,CachedConnectorFactory> getBlackholeEmailAddresses();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BrandService">
    // TODO: final CachedBrandService brands;
    // TODO: public BrandService<CachedConnector,CachedConnectorFactory> getBrands();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BusinessAdministratorService">
    static class CachedBusinessAdministratorService extends CachedService<UserId,BusinessAdministrator> implements BusinessAdministratorService<CachedConnector,CachedConnectorFactory> {
        CachedBusinessAdministratorService(CachedConnector connector, BusinessAdministratorService<?,?> wrapped) {
            super(connector, UserId.class, BusinessAdministrator.class, wrapped);
        }
    }
    final CachedBusinessAdministratorService businessAdministrators;
    public BusinessAdministratorService<CachedConnector,CachedConnectorFactory> getBusinessAdministrators() {
        return businessAdministrators;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BusinessAdministratorPermissionService">
    // TODO: final CachedBusinessAdministratorPermissionService businessAdministratorPermissions;
    // TODO: public BusinessAdministratorPermissionService<CachedConnector,CachedConnectorFactory> getBusinessAdministratorPermissions();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BusinessProfileService">
    // TODO: final CachedBusinessProfileService businessProfiles;
    // TODO: public BusinessProfileService<CachedConnector,CachedConnectorFactory> getBusinessProfiles();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BusinessService">
    static class CachedBusinessService extends CachedService<AccountingCode,Business> implements BusinessService<CachedConnector,CachedConnectorFactory> {
        CachedBusinessService(CachedConnector connector, BusinessService<?,?> wrapped) {
            super(connector, AccountingCode.class, Business.class, wrapped);
        }
    }
    final CachedBusinessService businesses;
    public BusinessService<CachedConnector,CachedConnectorFactory> getBusinesses() {
        return businesses;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BusinessServerService">
    static class CachedBusinessServerService extends CachedService<Integer,BusinessServer> implements BusinessServerService<CachedConnector,CachedConnectorFactory> {
        CachedBusinessServerService(CachedConnector connector, BusinessServerService<?,?> wrapped) {
            super(connector, Integer.class, BusinessServer.class, wrapped);
        }
    }
    final CachedBusinessServerService businessServers;
    public BusinessServerService<CachedConnector,CachedConnectorFactory> getBusinessServers() {
        return businessServers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="CountryCodeService">
    static class CachedCountryCodeService extends CachedService<String,CountryCode> implements CountryCodeService<CachedConnector,CachedConnectorFactory> {
        CachedCountryCodeService(CachedConnector connector, CountryCodeService<?,?> wrapped) {
            super(connector, String.class, CountryCode.class, wrapped);
        }
    }
    final CachedCountryCodeService countryCodes;
    public CountryCodeService<CachedConnector,CachedConnectorFactory> getCountryCodes() {
        return countryCodes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="CreditCardProcessorService">
    // TODO: final CachedCreditCardProcessorService creditCardProcessors;
    // TODO: public CreditCardProcessorService<CachedConnector,CachedConnectorFactory> getCreditCardProcessors();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="CreditCardTransactionService">
    // TODO: final CachedCreditCardTransactionService creditCardTransactions;
    // TODO: public CreditCardTransactionService<CachedConnector,CachedConnectorFactory> getCreditCardTransactions();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="CreditCardService">
    // TODO: final CachedCreditCardService creditCards;
    // TODO: public CreditCardService<CachedConnector,CachedConnectorFactory> getCreditCards();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="CvsRepositoryService">
    static class CachedCvsRepositoryService extends CachedService<Integer,CvsRepository> implements CvsRepositoryService<CachedConnector,CachedConnectorFactory> {
        CachedCvsRepositoryService(CachedConnector connector, CvsRepositoryService<?,?> wrapped) {
            super(connector, Integer.class, CvsRepository.class, wrapped);
        }
    }
    final CachedCvsRepositoryService cvsRepositories;
    public CvsRepositoryService<CachedConnector,CachedConnectorFactory> getCvsRepositories() {
        return cvsRepositories;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="DisableLogService">
    static class CachedDisableLogService extends CachedService<Integer,DisableLog> implements DisableLogService<CachedConnector,CachedConnectorFactory> {
        CachedDisableLogService(CachedConnector connector, DisableLogService<?,?> wrapped) {
            super(connector, Integer.class, DisableLog.class, wrapped);
        }
    }
    final CachedDisableLogService disableLogs;
    public DisableLogService<CachedConnector,CachedConnectorFactory> getDisableLogs() {
        return disableLogs;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="DistroFileTypeService">
    // TODO: final CachedDistroFileTypeService distroFileTypes;
    // TODO: public DistroFileTypeService<CachedConnector,CachedConnectorFactory> getDistroFileTypes();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="DistroFileService">
    // TODO: final CachedDistroFileService distroFiles;
    // TODO: public DistroFileService<CachedConnector,CachedConnectorFactory> getDistroFiles();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="DNSForbiddenZoneService">
    // TODO: final CachedDNSForbiddenZoneService dnsForbiddenZones;
    // TODO: public DNSForbiddenZoneService<CachedConnector,CachedConnectorFactory> getDnsForbiddenZones();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="DNSRecordService">
    // TODO: final CachedDNSRecordService dnsRecords;
    // TODO: public DNSRecordService<CachedConnector,CachedConnectorFactory> getDnsRecords();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="DNSTLDService">
    // TODO: final CachedDNSTLDService dnsTLDs;
    // TODO: public DNSTLDService<CachedConnector,CachedConnectorFactory> getDnsTLDs();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="DNSTypeService">
    // TODO: final CachedDNSTypeService dnsTypes;
    // TODO: public DNSTypeService<CachedConnector,CachedConnectorFactory> getDnsTypes();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="DNSZoneService">
    // TODO: final CachedDNSZoneService dnsZones;
    // TODO: public DNSZoneService<CachedConnector,CachedConnectorFactory> getDnsZones();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailAddressService">
    // TODO: final CachedEmailAddressService emailAddresss;
    // TODO: public EmailAddressService<CachedConnector,CachedConnectorFactory> getEmailAddresses();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailAttachmentBlockService">
    // TODO: final CachedEmailAttachmentBlockService emailAttachmentBlocks;
    // TODO: public EmailAttachmentBlockService<CachedConnector,CachedConnectorFactory> getEmailAttachmentBlocks();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailAttachmentTypeService">
    // TODO: final CachedEmailAttachmentTypeService emailAttachmentTypes;
    // TODO: public EmailAttachmentTypeService<CachedConnector,CachedConnectorFactory> getEmailAttachmentTypes();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailDomainService">
    // TODO: final CachedEmailDomainService emailDomains;
    // TODO: public EmailDomainService<CachedConnector,CachedConnectorFactory> getEmailDomains();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailForwardingService">
    // TODO: final CachedEmailForwardingService emailForwardings;
    // TODO: public EmailForwardingService<CachedConnector,CachedConnectorFactory> getEmailForwardings();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailListAddressService">
    // TODO: final CachedEmailListAddressService emailListAddresss;
    // TODO: public EmailListAddressService<CachedConnector,CachedConnectorFactory> getEmailListAddresses();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailListService">
    // TODO: final CachedEmailListService emailLists;
    // TODO: public EmailListService<CachedConnector,CachedConnectorFactory> getEmailLists();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailPipeAddressService">
    // TODO: final CachedEmailPipeAddressService emailPipeAddresss;
    // TODO: public EmailPipeAddressService<CachedConnector,CachedConnectorFactory> getEmailPipeAddresses();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailPipeService">
    // TODO: final CachedEmailPipeService emailPipes;
    // TODO: public EmailPipeService<CachedConnector,CachedConnectorFactory> getEmailPipes();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailSmtpRelayTypeService">
    // TODO: final CachedEmailSmtpRelayTypeService emailSmtpRelayTypes;
    // TODO: public EmailSmtpRelayTypeService<CachedConnector,CachedConnectorFactory> getEmailSmtpRelayTypes();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailSmtpRelayService">
    // TODO: final CachedEmailSmtpRelayService emailSmtpRelays;
    // TODO: public EmailSmtpRelayService<CachedConnector,CachedConnectorFactory> getEmailSmtpRelays();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailSmtpSmartHostDomainService">
    // TODO: final CachedEmailSmtpSmartHostDomainService emailSmtpSmartHostDomains;
    // TODO: public EmailSmtpSmartHostDomainService<CachedConnector,CachedConnectorFactory> getEmailSmtpSmartHostDomains();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailSmtpSmartHostService">
    // TODO: final CachedEmailSmtpSmartHostService emailSmtpSmartHosts;
    // TODO: public EmailSmtpSmartHostService<CachedConnector,CachedConnectorFactory> getEmailSmtpSmartHosts();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailSpamAssassinIntegrationModeService">
    // TODO: final CachedEmailSpamAssassinIntegrationModeService emailSpamAssassinIntegrationModes;
    // TODO: public EmailSpamAssassinIntegrationModeService<CachedConnector,CachedConnectorFactory> getEmailSpamAssassinIntegrationModes();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EncryptionKeyService">
    // TODO: final CachedEncryptionKeyService encryptionKeys;
    // TODO: public EncryptionKeyService<CachedConnector,CachedConnectorFactory> getEncryptionKeys();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ExpenseCategoryService">
    // TODO: final CachedExpenseCategoryService expenseCategories;
    // TODO: public ExpenseCategoryService<CachedConnector,CachedConnectorFactory> getExpenseCategories();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="FailoverFileLogService">
    static class CachedFailoverFileLogService extends CachedService<Integer,FailoverFileLog> implements FailoverFileLogService<CachedConnector,CachedConnectorFactory> {
        CachedFailoverFileLogService(CachedConnector connector, FailoverFileLogService<?,?> wrapped) {
            super(connector, Integer.class, FailoverFileLog.class, wrapped);
        }
    }
    final CachedFailoverFileLogService failoverFileLogs;
    public FailoverFileLogService<CachedConnector,CachedConnectorFactory> getFailoverFileLogs() {
        return failoverFileLogs;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="FailoverFileReplicationService">
    static class CachedFailoverFileReplicationService extends CachedService<Integer,FailoverFileReplication> implements FailoverFileReplicationService<CachedConnector,CachedConnectorFactory> {
        CachedFailoverFileReplicationService(CachedConnector connector, FailoverFileReplicationService<?,?> wrapped) {
            super(connector, Integer.class, FailoverFileReplication.class, wrapped);
        }
    }
    final CachedFailoverFileReplicationService failoverFileReplications;
    public FailoverFileReplicationService<CachedConnector,CachedConnectorFactory> getFailoverFileReplications() {
        return failoverFileReplications;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="FailoverFileScheduleService">
    static class CachedFailoverFileScheduleService extends CachedService<Integer,FailoverFileSchedule> implements FailoverFileScheduleService<CachedConnector,CachedConnectorFactory> {
        CachedFailoverFileScheduleService(CachedConnector connector, FailoverFileScheduleService<?,?> wrapped) {
            super(connector, Integer.class, FailoverFileSchedule.class, wrapped);
        }
    }
    final CachedFailoverFileScheduleService failoverFileSchedules;
    public FailoverFileScheduleService<CachedConnector,CachedConnectorFactory> getFailoverFileSchedules() {
        return failoverFileSchedules;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="FailoverMySQLReplicationService">
    static class CachedFailoverMySQLReplicationService extends CachedService<Integer,FailoverMySQLReplication> implements FailoverMySQLReplicationService<CachedConnector,CachedConnectorFactory> {
        CachedFailoverMySQLReplicationService(CachedConnector connector, FailoverMySQLReplicationService<?,?> wrapped) {
            super(connector, Integer.class, FailoverMySQLReplication.class, wrapped);
        }
    }
    final CachedFailoverMySQLReplicationService failoverMySQLReplications;
    public FailoverMySQLReplicationService<CachedConnector,CachedConnectorFactory> getFailoverMySQLReplications() {
        return failoverMySQLReplications;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="FileBackupSettingService">
    static class CachedFileBackupSettingService extends CachedService<Integer,FileBackupSetting> implements FileBackupSettingService<CachedConnector,CachedConnectorFactory> {
        CachedFileBackupSettingService(CachedConnector connector, FileBackupSettingService<?,?> wrapped) {
            super(connector, Integer.class, FileBackupSetting.class, wrapped);
        }
    }
    final CachedFileBackupSettingService fileBackupSettings;
    public FileBackupSettingService<CachedConnector,CachedConnectorFactory> getFileBackupSettings() {
        return fileBackupSettings;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="FTPGuestUserService">
    // TODO: final CachedFTPGuestUserService ftpGuestUsers;
    // TODO: public FTPGuestUserService<CachedConnector,CachedConnectorFactory> getFtpGuestUsers();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="GroupNameService">
    static class CachedGroupNameService extends CachedService<GroupId,GroupName> implements GroupNameService<CachedConnector,CachedConnectorFactory> {
        CachedGroupNameService(CachedConnector connector, GroupNameService<?,?> wrapped) {
            super(connector, GroupId.class, GroupName.class, wrapped);
        }
    }
    final CachedGroupNameService groupNames;
    public GroupNameService<CachedConnector,CachedConnectorFactory> getGroupNames() {
        return groupNames;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdBindService">
    // TODO: final CachedHttpdBindService httpdBinds;
    // TODO: public HttpdBindService<CachedConnector,CachedConnectorFactory> getHttpdBinds();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdJBossSiteService">
    // TODO: final CachedHttpdJBossSiteService httpdJBossSites;
    // TODO: public HttpdJBossSiteService<CachedConnector,CachedConnectorFactory> getHttpdJBossSites();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdJBossVersionService">
    // TODO: final CachedHttpdJBossVersionService httpdJBossVersions;
    // TODO: public HttpdJBossVersionService<CachedConnector,CachedConnectorFactory> getHttpdJBossVersions();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdJKCodeService">
    // TODO: final CachedHttpdJKCodeService httpdJKCodes;
    // TODO: public HttpdJKCodeService<CachedConnector,CachedConnectorFactory> getHttpdJKCodes();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdJKProtocolService">
    // TODO: final CachedHttpdJKProtocolService httpdJKProtocols;
    // TODO: public HttpdJKProtocolService<CachedConnector,CachedConnectorFactory> getHttpdJKProtocols();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdServerService">
    // TODO: final CachedHttpdServerService httpdServers;
    // TODO: public HttpdServerService<CachedConnector,CachedConnectorFactory> getHttpdServers();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdSharedTomcatService">
    // TODO: final CachedHttpdSharedTomcatService httpdSharedTomcats;
    // TODO: public HttpdSharedTomcatService<CachedConnector,CachedConnectorFactory> getHttpdSharedTomcats();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdSiteAuthenticatedLocationService">
    // TODO: final CachedHttpdSiteAuthenticatedLocationService httpdSiteAuthenticatedLocations;
    // TODO: public HttpdSiteAuthenticatedLocationService<CachedConnector,CachedConnectorFactory> getHttpdSiteAuthenticatedLocations();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdSiteBindService">
    // TODO: final CachedHttpdSiteBindService httpdSiteBinds;
    // TODO: public HttpdSiteBindService<CachedConnector,CachedConnectorFactory> getHttpdSiteBinds();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdSiteURLService">
    // TODO: final CachedHttpdSiteURLService httpdSiteURLs;
    // TODO: public HttpdSiteURLService<CachedConnector,CachedConnectorFactory> getHttpdSiteURLs();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdSiteService">
    static class CachedHttpdSiteService extends CachedService<Integer,HttpdSite> implements HttpdSiteService<CachedConnector,CachedConnectorFactory> {
        CachedHttpdSiteService(CachedConnector connector, HttpdSiteService<?,?> wrapped) {
            super(connector, Integer.class, HttpdSite.class, wrapped);
        }
    }
    final CachedHttpdSiteService httpdSites;
    public HttpdSiteService<CachedConnector,CachedConnectorFactory> getHttpdSites() {
        return httpdSites;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdStaticSiteService">
    // TODO: final CachedHttpdStaticSiteService httpdStaticSites;
    // TODO: public HttpdStaticSiteService<CachedConnector,CachedConnectorFactory> getHttpdStaticSites();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdTomcatContextService">
    // TODO: final CachedHttpdTomcatContextService httpdTomcatContexts;
    // TODO: public HttpdTomcatContextService<CachedConnector,CachedConnectorFactory> getHttpdTomcatContexts();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdTomcatDataSourceService">
    // TODO: final CachedHttpdTomcatDataSourceService httpdTomcatDataSources;
    // TODO: public HttpdTomcatDataSourceService<CachedConnector,CachedConnectorFactory> getHttpdTomcatDataSources();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdTomcatParameterService">
    // TODO: final CachedHttpdTomcatParameterService httpdTomcatParameters;
    // TODO: public HttpdTomcatParameterService<CachedConnector,CachedConnectorFactory> getHttpdTomcatParameters();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdTomcatSiteService">
    // TODO: final CachedHttpdTomcatSiteService httpdTomcatSites;
    // TODO: public HttpdTomcatSiteService<CachedConnector,CachedConnectorFactory> getHttpdTomcatSites();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdTomcatSharedSiteService">
    // TODO: final CachedHttpdTomcatSharedSiteService httpdTomcatSharedSites;
    // TODO: public HttpdTomcatSharedSiteService<CachedConnector,CachedConnectorFactory> getHttpdTomcatSharedSites();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdTomcatStdSiteService">
    // TODO: final CachedHttpdTomcatStdSiteService httpdTomcatStdSites;
    // TODO: public HttpdTomcatStdSiteService<CachedConnector,CachedConnectorFactory> getHttpdTomcatStdSites();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdTomcatVersionService">
    // TODO: final CachedHttpdTomcatVersionService httpdTomcatVersions;
    // TODO: public HttpdTomcatVersionService<CachedConnector,CachedConnectorFactory> getHttpdTomcatVersions();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdWorkerService">
    // TODO: final CachedHttpdWorkerService httpdWorkers;
    // TODO: public HttpdWorkerService<CachedConnector,CachedConnectorFactory> getHttpdWorkers();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="IPAddressService">
    static class CachedIPAddressService extends CachedService<Integer,IPAddress> implements IPAddressService<CachedConnector,CachedConnectorFactory> {
        CachedIPAddressService(CachedConnector connector, IPAddressService<?,?> wrapped) {
            super(connector, Integer.class, IPAddress.class, wrapped);
        }
    }
    final CachedIPAddressService ipAddresses;
    public IPAddressService<CachedConnector,CachedConnectorFactory> getIpAddresses() {
        return ipAddresses;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="LanguageService">
    static class CachedLanguageService extends CachedService<String,Language> implements LanguageService<CachedConnector,CachedConnectorFactory> {
        CachedLanguageService(CachedConnector connector, LanguageService<?,?> wrapped) {
            super(connector, String.class, Language.class, wrapped);
        }
    }
    final CachedLanguageService languages;
    public LanguageService<CachedConnector,CachedConnectorFactory> getLanguages() {
        return languages;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="LinuxAccAddressService">
    // TODO: final CachedLinuxAccAddressService linuxAccAddresss;
    // TODO: public LinuxAccAddressService<CachedConnector,CachedConnectorFactory> getLinuxAccAddresses();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="LinuxAccountGroupService">
    static class CachedLinuxAccountGroupService extends CachedService<Integer,LinuxAccountGroup> implements LinuxAccountGroupService<CachedConnector,CachedConnectorFactory> {
        CachedLinuxAccountGroupService(CachedConnector connector, LinuxAccountGroupService<?,?> wrapped) {
            super(connector, Integer.class, LinuxAccountGroup.class, wrapped);
        }
    }
    final CachedLinuxAccountGroupService linuxAccountGroups;
    public LinuxAccountGroupService<CachedConnector,CachedConnectorFactory> getLinuxAccountGroups() {
        return linuxAccountGroups;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="LinuxAccountTypeService">
    static class CachedLinuxAccountTypeService extends CachedService<String,LinuxAccountType> implements LinuxAccountTypeService<CachedConnector,CachedConnectorFactory> {
        CachedLinuxAccountTypeService(CachedConnector connector, LinuxAccountTypeService<?,?> wrapped) {
            super(connector, String.class, LinuxAccountType.class, wrapped);
        }
    }
    final CachedLinuxAccountTypeService linuxAccountTypes;
    public LinuxAccountTypeService<CachedConnector,CachedConnectorFactory> getLinuxAccountTypes() {
        return linuxAccountTypes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="LinuxAccountService">
    static class CachedLinuxAccountService extends CachedService<Integer,LinuxAccount> implements LinuxAccountService<CachedConnector,CachedConnectorFactory> {
        CachedLinuxAccountService(CachedConnector connector, LinuxAccountService<?,?> wrapped) {
            super(connector, Integer.class, LinuxAccount.class, wrapped);
        }
    }
    final CachedLinuxAccountService linuxAccounts;
    public LinuxAccountService<CachedConnector,CachedConnectorFactory> getLinuxAccounts() {
        return linuxAccounts;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="LinuxGroupTypeService">
    static class CachedLinuxGroupTypeService extends CachedService<String,LinuxGroupType> implements LinuxGroupTypeService<CachedConnector,CachedConnectorFactory> {
        CachedLinuxGroupTypeService(CachedConnector connector, LinuxGroupTypeService<?,?> wrapped) {
            super(connector, String.class, LinuxGroupType.class, wrapped);
        }
    }
    final CachedLinuxGroupTypeService linuxGroupTypes;
    public LinuxGroupTypeService<CachedConnector,CachedConnectorFactory> getLinuxGroupTypes() {
        return linuxGroupTypes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="LinuxGroupService">
    static class CachedLinuxGroupService extends CachedService<Integer,LinuxGroup> implements LinuxGroupService<CachedConnector,CachedConnectorFactory> {
        CachedLinuxGroupService(CachedConnector connector, LinuxGroupService<?,?> wrapped) {
            super(connector, Integer.class, LinuxGroup.class, wrapped);
        }
    }
    final CachedLinuxGroupService linuxGroups;
    public LinuxGroupService<CachedConnector,CachedConnectorFactory> getLinuxGroups() {
        return linuxGroups;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MajordomoListService">
    // TODO: final CachedMajordomoListService majordomoLists;
    // TODO: public MajordomoListService<CachedConnector,CachedConnectorFactory> getMajordomoLists();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MajordomoServerService">
    // TODO: final CachedMajordomoServerService majordomoServers;
    // TODO: public MajordomoServerService<CachedConnector,CachedConnectorFactory> getMajordomoServers();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MajordomoVersionService">
    // TODO: final CachedMajordomoVersionService majordomoVersions;
    // TODO: public MajordomoVersionService<CachedConnector,CachedConnectorFactory> getMajordomoVersions();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MasterHistoryService">
    // TODO: final CachedMasterHistoryService masterHistories;
    // TODO: public MasterHistoryService<CachedConnector,CachedConnectorFactory> getMasterHistory();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MasterHostService">
    // TODO: final CachedMasterHostService masterHosts;
    // TODO: public MasterHostService<CachedConnector,CachedConnectorFactory> getMasterHosts();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MasterServerService">
    // TODO: final CachedMasterServerService masterServers;
    // TODO: public MasterServerService<CachedConnector,CachedConnectorFactory> getMasterServers();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MasterUserService">
    // TODO: final CachedMasterUserService masterUsers;
    // TODO: public MasterUserService<CachedConnector,CachedConnectorFactory> getMasterUsers();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MonthlyChargeService">
    // TODO: final CachedMonthlyChargeService monthlyCharges;
    // TODO: public MonthlyChargeService<CachedConnector,CachedConnectorFactory> getMonthlyCharges();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MySQLDatabaseService">
    static class CachedMySQLDatabaseService extends CachedService<Integer,MySQLDatabase> implements MySQLDatabaseService<CachedConnector,CachedConnectorFactory> {
        CachedMySQLDatabaseService(CachedConnector connector, MySQLDatabaseService<?,?> wrapped) {
            super(connector, Integer.class, MySQLDatabase.class, wrapped);
        }
    }
    final CachedMySQLDatabaseService mysqlDatabases;
    public MySQLDatabaseService<CachedConnector,CachedConnectorFactory> getMysqlDatabases() {
        return mysqlDatabases;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MySQLDBUserService">
    static class CachedMySQLDBUserService extends CachedService<Integer,MySQLDBUser> implements MySQLDBUserService<CachedConnector,CachedConnectorFactory> {
        CachedMySQLDBUserService(CachedConnector connector, MySQLDBUserService<?,?> wrapped) {
            super(connector, Integer.class, MySQLDBUser.class, wrapped);
        }
    }
    final CachedMySQLDBUserService mysqlDBUsers;
    public MySQLDBUserService<CachedConnector,CachedConnectorFactory> getMysqlDBUsers() {
        return mysqlDBUsers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MySQLServerService">
    static class CachedMySQLServerService extends CachedService<Integer,MySQLServer> implements MySQLServerService<CachedConnector,CachedConnectorFactory> {
        CachedMySQLServerService(CachedConnector connector, MySQLServerService<?,?> wrapped) {
            super(connector, Integer.class, MySQLServer.class, wrapped);
        }
    }
    final CachedMySQLServerService mysqlServers;
    public MySQLServerService<CachedConnector,CachedConnectorFactory> getMysqlServers() {
        return mysqlServers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MySQLUserService">
    static class CachedMySQLUserService extends CachedService<Integer,MySQLUser> implements MySQLUserService<CachedConnector,CachedConnectorFactory> {
        CachedMySQLUserService(CachedConnector connector, MySQLUserService<?,?> wrapped) {
            super(connector, Integer.class, MySQLUser.class, wrapped);
        }
    }
    final CachedMySQLUserService mysqlUsers;
    public MySQLUserService<CachedConnector,CachedConnectorFactory> getMysqlUsers() {
        return mysqlUsers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="NetBindService">
    static class CachedNetBindService extends CachedService<Integer,NetBind> implements NetBindService<CachedConnector,CachedConnectorFactory> {
        CachedNetBindService(CachedConnector connector, NetBindService<?,?> wrapped) {
            super(connector, Integer.class, NetBind.class, wrapped);
        }
    }
    final CachedNetBindService netBinds;
    public NetBindService<CachedConnector,CachedConnectorFactory> getNetBinds() {
        return netBinds;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="NetDeviceIDService">
    static class CachedNetDeviceIDService extends CachedService<String,NetDeviceID> implements NetDeviceIDService<CachedConnector,CachedConnectorFactory> {
        CachedNetDeviceIDService(CachedConnector connector, NetDeviceIDService<?,?> wrapped) {
            super(connector, String.class, NetDeviceID.class, wrapped);
        }
    }
    final CachedNetDeviceIDService netDeviceIDs;
    public NetDeviceIDService<CachedConnector,CachedConnectorFactory> getNetDeviceIDs() {
        return netDeviceIDs;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="NetDeviceService">
    static class CachedNetDeviceService extends CachedService<Integer,NetDevice> implements NetDeviceService<CachedConnector,CachedConnectorFactory> {
        CachedNetDeviceService(CachedConnector connector, NetDeviceService<?,?> wrapped) {
            super(connector, Integer.class, NetDevice.class, wrapped);
        }
    }
    final CachedNetDeviceService netDevices;
    public NetDeviceService<CachedConnector,CachedConnectorFactory> getNetDevices() {
        return netDevices;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="NetProtocolService">
    static class CachedNetProtocolService extends CachedService<String,NetProtocol> implements NetProtocolService<CachedConnector,CachedConnectorFactory> {
        CachedNetProtocolService(CachedConnector connector, NetProtocolService<?,?> wrapped) {
            super(connector, String.class, NetProtocol.class, wrapped);
        }
    }
    final CachedNetProtocolService netProtocols;
    public NetProtocolService<CachedConnector,CachedConnectorFactory> getNetProtocols() {
        return netProtocols;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="NetTcpRedirectService">
    static class CachedNetTcpRedirectService extends CachedService<Integer,NetTcpRedirect> implements NetTcpRedirectService<CachedConnector,CachedConnectorFactory> {
        CachedNetTcpRedirectService(CachedConnector connector, NetTcpRedirectService<?,?> wrapped) {
            super(connector, Integer.class, NetTcpRedirect.class, wrapped);
        }
    }
    final CachedNetTcpRedirectService netTcpRedirects;
    public NetTcpRedirectService<CachedConnector,CachedConnectorFactory> getNetTcpRedirects() {
        return netTcpRedirects;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="NoticeLogService">
    // TODO: final CachedNoticeLogService noticeLogs;
    // TODO: public NoticeLogService<CachedConnector,CachedConnectorFactory> getNoticeLogs();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="NoticeTypeService">
    // TODO: final CachedNoticeTypeService noticeTypes;
    // TODO: public NoticeTypeService<CachedConnector,CachedConnectorFactory> getNoticeTypes();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="OperatingSystemVersionService">
    static class CachedOperatingSystemVersionService extends CachedService<Integer,OperatingSystemVersion> implements OperatingSystemVersionService<CachedConnector,CachedConnectorFactory> {
        CachedOperatingSystemVersionService(CachedConnector connector, OperatingSystemVersionService<?,?> wrapped) {
            super(connector, Integer.class, OperatingSystemVersion.class, wrapped);
        }
    }
    final CachedOperatingSystemVersionService operatingSystemVersions;
    public OperatingSystemVersionService<CachedConnector,CachedConnectorFactory> getOperatingSystemVersions() {
        return operatingSystemVersions;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="OperatingSystemService">
    static class CachedOperatingSystemService extends CachedService<String,OperatingSystem> implements OperatingSystemService<CachedConnector,CachedConnectorFactory> {
        CachedOperatingSystemService(CachedConnector connector, OperatingSystemService<?,?> wrapped) {
            super(connector, String.class, OperatingSystem.class, wrapped);
        }
    }
    final CachedOperatingSystemService operatingSystems;
    public OperatingSystemService<CachedConnector,CachedConnectorFactory> getOperatingSystems() {
        return operatingSystems;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PackageCategoryService">
    static class CachedPackageCategoryService extends CachedService<String,PackageCategory> implements PackageCategoryService<CachedConnector,CachedConnectorFactory> {
        CachedPackageCategoryService(CachedConnector connector, PackageCategoryService<?,?> wrapped) {
            super(connector, String.class, PackageCategory.class, wrapped);
        }
    }
    final CachedPackageCategoryService packageCategories;
    public PackageCategoryService<CachedConnector,CachedConnectorFactory> getPackageCategories() {
        return packageCategories;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PackageDefinitionLimitService">
    // TODO: final CachedPackageDefinitionLimitService packageDefinitionLimits;
    // TODO: public PackageDefinitionLimitService<CachedConnector,CachedConnectorFactory> getPackageDefinitionLimits();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PackageDefinitionService">
    // TODO: final CachedPackageDefinitionService packageDefinitions;
    // TODO: public PackageDefinitionService<CachedConnector,CachedConnectorFactory> getPackageDefinitions();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PaymentTypeService">
    // TODO: final CachedPaymentTypeService paymentTypes;
    // TODO: public PaymentTypeService<CachedConnector,CachedConnectorFactory> getPaymentTypes();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PhysicalServerService">
    // TODO: final CachedPhysicalServerService physicalServers;
    // TODO: public PhysicalServerService<CachedConnector,CachedConnectorFactory> getPhysicalServers();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PostgresDatabaseService">
    static class CachedPostgresDatabaseService extends CachedService<Integer,PostgresDatabase> implements PostgresDatabaseService<CachedConnector,CachedConnectorFactory> {
        CachedPostgresDatabaseService(CachedConnector connector, PostgresDatabaseService<?,?> wrapped) {
            super(connector, Integer.class, PostgresDatabase.class, wrapped);
        }
    }
    final CachedPostgresDatabaseService postgresDatabases;
    public PostgresDatabaseService<CachedConnector,CachedConnectorFactory> getPostgresDatabases() {
        return postgresDatabases;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PostgresEncodingService">
    static class CachedPostgresEncodingService extends CachedService<Integer,PostgresEncoding> implements PostgresEncodingService<CachedConnector,CachedConnectorFactory> {
        CachedPostgresEncodingService(CachedConnector connector, PostgresEncodingService<?,?> wrapped) {
            super(connector, Integer.class, PostgresEncoding.class, wrapped);
        }
    }
    final CachedPostgresEncodingService postgresEncodings;
    public PostgresEncodingService<CachedConnector,CachedConnectorFactory> getPostgresEncodings() {
        return postgresEncodings;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PostgresServerService">
    static class CachedPostgresServerService extends CachedService<Integer,PostgresServer> implements PostgresServerService<CachedConnector,CachedConnectorFactory> {
        CachedPostgresServerService(CachedConnector connector, PostgresServerService<?,?> wrapped) {
            super(connector, Integer.class, PostgresServer.class, wrapped);
        }
    }
    final CachedPostgresServerService postgresServers;
    public PostgresServerService<CachedConnector,CachedConnectorFactory> getPostgresServers() {
        return postgresServers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PostgresUserService">
    static class CachedPostgresUserService extends CachedService<Integer,PostgresUser> implements PostgresUserService<CachedConnector,CachedConnectorFactory> {
        CachedPostgresUserService(CachedConnector connector, PostgresUserService<?,?> wrapped) {
            super(connector, Integer.class, PostgresUser.class, wrapped);
        }
    }
    final CachedPostgresUserService postgresUsers;
    public PostgresUserService<CachedConnector,CachedConnectorFactory> getPostgresUsers() {
        return postgresUsers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PostgresVersionService">
    static class CachedPostgresVersionService extends CachedService<Integer,PostgresVersion> implements PostgresVersionService<CachedConnector,CachedConnectorFactory> {
        CachedPostgresVersionService(CachedConnector connector, PostgresVersionService<?,?> wrapped) {
            super(connector, Integer.class, PostgresVersion.class, wrapped);
        }
    }
    final CachedPostgresVersionService postgresVersions;
    public PostgresVersionService<CachedConnector,CachedConnectorFactory> getPostgresVersions() {
        return postgresVersions;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PrivateFTPServerService">
    // TODO: final CachedPrivateFTPServerService privateFTPServers;
    // TODO: public PrivateFTPServerService<CachedConnector,CachedConnectorFactory> getPrivateFTPServers();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ProcessorTypeService">
    // TODO: final CachedProcessorTypeService processorTypes;
    // TODO: public ProcessorTypeService<CachedConnector,CachedConnectorFactory> getProcessorTypes();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ProtocolService">
    static class CachedProtocolService extends CachedService<String,Protocol> implements ProtocolService<CachedConnector,CachedConnectorFactory> {
        CachedProtocolService(CachedConnector connector, ProtocolService<?,?> wrapped) {
            super(connector, String.class, Protocol.class, wrapped);
        }
    }
    final CachedProtocolService protocols;
    public ProtocolService<CachedConnector,CachedConnectorFactory> getProtocols() {
        return protocols;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="RackService">
    // TODO: final CachedRackService racks;
    // TODO: public RackService<CachedConnector,CachedConnectorFactory> getRacks();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ResellerService">
    // TODO: final CachedResellerService resellers;
    // TODO: public ResellerService<CachedConnector,CachedConnectorFactory> getResellers();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ResourceTypeService">
    static class CachedResourceTypeService extends CachedService<String,ResourceType> implements ResourceTypeService<CachedConnector,CachedConnectorFactory> {
        CachedResourceTypeService(CachedConnector connector, ResourceTypeService<?,?> wrapped) {
            super(connector, String.class, ResourceType.class, wrapped);
        }
    }
    final CachedResourceTypeService resourceTypes;
    public ResourceTypeService<CachedConnector,CachedConnectorFactory> getResourceTypes() {
        return resourceTypes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ResourceService">
    static class CachedResourceService extends CachedService<Integer,Resource> implements ResourceService<CachedConnector,CachedConnectorFactory> {
        CachedResourceService(CachedConnector connector, ResourceService<?,?> wrapped) {
            super(connector, Integer.class, Resource.class, wrapped);
        }
    }
    final CachedResourceService resources;
    public ResourceService<CachedConnector,CachedConnectorFactory> getResources() {
        return resources;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ServerFarmService">
    static class CachedServerFarmService extends CachedService<DomainLabel,ServerFarm> implements ServerFarmService<CachedConnector,CachedConnectorFactory> {
        CachedServerFarmService(CachedConnector connector, ServerFarmService<?,?> wrapped) {
            super(connector, DomainLabel.class, ServerFarm.class, wrapped);
        }
    }
    final CachedServerFarmService serverFarms;
    public ServerFarmService<CachedConnector,CachedConnectorFactory> getServerFarms() {
        return serverFarms;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ServerResourceService">
    static class CachedServerResourceService extends CachedService<Integer,ServerResource> implements ServerResourceService<CachedConnector,CachedConnectorFactory> {
        CachedServerResourceService(CachedConnector connector, ServerResourceService<?,?> wrapped) {
            super(connector, Integer.class, ServerResource.class, wrapped);
        }
    }
    final CachedServerResourceService serverResources;
    public ServerResourceService<CachedConnector,CachedConnectorFactory> getServerResources() {
        return serverResources;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ServerService">
    static class CachedServerService extends CachedService<Integer,Server> implements ServerService<CachedConnector,CachedConnectorFactory> {
        CachedServerService(CachedConnector connector, ServerService<?,?> wrapped) {
            super(connector, Integer.class, Server.class, wrapped);
        }
    }
    final CachedServerService servers;
    public ServerService<CachedConnector,CachedConnectorFactory> getServers() {
        return servers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ShellService">
    static class CachedShellService extends CachedService<UnixPath,Shell> implements ShellService<CachedConnector,CachedConnectorFactory> {
        CachedShellService(CachedConnector connector, ShellService<?,?> wrapped) {
            super(connector, UnixPath.class, Shell.class, wrapped);
        }
    }
    final CachedShellService shells;
    public ShellService<CachedConnector,CachedConnectorFactory> getShells() {
        return shells;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="SignupRequestOptionService">
    // TODO: final CachedSignupRequestOptionService signupRequestOptions;
    // TODO: public SignupRequestOptionService<CachedConnector,CachedConnectorFactory> getSignupRequestOptions();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="SignupRequestService">
    // TODO: final CachedSignupRequestService signupRequests;
    // TODO: public SignupRequestService<CachedConnector,CachedConnectorFactory> getSignupRequests();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="SpamEmailMessageService">
    // TODO: final CachedSpamEmailMessageService spamEmailMessages;
    // TODO: public SpamEmailMessageService<CachedConnector,CachedConnectorFactory> getSpamEmailMessages();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="SystemEmailAliasService">
    // TODO: final CachedSystemEmailAliasService systemEmailAliass;
    // TODO: public SystemEmailAliasService<CachedConnector,CachedConnectorFactory> getSystemEmailAliases();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TechnologyService">
    final class CachedTechnologyService extends CachedService<Integer,Technology> implements TechnologyService<CachedConnector,CachedConnectorFactory> {
        CachedTechnologyService(CachedConnector connector, TechnologyService<?,?> wrapped) {
            super(connector, Integer.class, Technology.class, wrapped);
        }
    }
    final CachedTechnologyService technologies;
    public TechnologyService<CachedConnector,CachedConnectorFactory> getTechnologies() {
        return technologies;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TechnologyClassService">
    static class CachedTechnologyClassService extends CachedService<String,TechnologyClass> implements TechnologyClassService<CachedConnector,CachedConnectorFactory> {
        CachedTechnologyClassService(CachedConnector connector, TechnologyClassService<?,?> wrapped) {
            super(connector, String.class, TechnologyClass.class, wrapped);
        }
    }
    final CachedTechnologyClassService technologyClasses;
    public TechnologyClassService<CachedConnector,CachedConnectorFactory> getTechnologyClasses() {
        return technologyClasses;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TechnologyNameService">
    static class CachedTechnologyNameService extends CachedService<String,TechnologyName> implements TechnologyNameService<CachedConnector,CachedConnectorFactory> {
        CachedTechnologyNameService(CachedConnector connector, TechnologyNameService<?,?> wrapped) {
            super(connector, String.class, TechnologyName.class, wrapped);
        }
    }
    final CachedTechnologyNameService technologyNames;
    public TechnologyNameService<CachedConnector,CachedConnectorFactory> getTechnologyNames() {
        return technologyNames;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TechnologyVersionService">
    static class CachedTechnologyVersionService extends CachedService<Integer,TechnologyVersion> implements TechnologyVersionService<CachedConnector,CachedConnectorFactory> {
        CachedTechnologyVersionService(CachedConnector connector, TechnologyVersionService<?,?> wrapped) {
            super(connector, Integer.class, TechnologyVersion.class, wrapped);
        }
    }
    final CachedTechnologyVersionService technologyVersions;
    public TechnologyVersionService<CachedConnector,CachedConnectorFactory> getTechnologyVersions() {
        return technologyVersions;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TicketActionTypeService">
    // TODO: final CachedTicketActionTypeService ticketActionTypes;
    // TODO: public TicketActionTypeService<CachedConnector,CachedConnectorFactory> getTicketActionTypes();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TicketActionService">
    // TODO: final CachedTicketActionService ticketActions;
    // TODO: public TicketActionService<CachedConnector,CachedConnectorFactory> getTicketActions();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TicketAssignmentService">
    // TODO: final CachedTicketAssignmentService ticketAssignments;
    // TODO: public TicketAssignmentService<CachedConnector,CachedConnectorFactory> getTicketAssignments();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TicketBrandCategoryService">
    // TODO: final CachedTicketBrandCategoryService ticketBrandCategories;
    // TODO: public TicketBrandCategoryService<CachedConnector,CachedConnectorFactory> getTicketBrandCategories();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TicketCategoryService">
    static class CachedTicketCategoryService extends CachedService<Integer,TicketCategory> implements TicketCategoryService<CachedConnector,CachedConnectorFactory> {
        CachedTicketCategoryService(CachedConnector connector, TicketCategoryService<?,?> wrapped) {
            super(connector, Integer.class, TicketCategory.class, wrapped);
        }
    }
    final CachedTicketCategoryService ticketCategories;
    public TicketCategoryService<CachedConnector,CachedConnectorFactory> getTicketCategories() {
        return ticketCategories;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TicketPriorityService">
    static class CachedTicketPriorityService extends CachedService<String,TicketPriority> implements TicketPriorityService<CachedConnector,CachedConnectorFactory> {
        CachedTicketPriorityService(CachedConnector connector, TicketPriorityService<?,?> wrapped) {
            super(connector, String.class, TicketPriority.class, wrapped);
        }
    }
    final CachedTicketPriorityService ticketPriorities;
    public TicketPriorityService<CachedConnector,CachedConnectorFactory> getTicketPriorities() {
        return ticketPriorities;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TicketStatusService">
    static class CachedTicketStatusService extends CachedService<String,TicketStatus> implements TicketStatusService<CachedConnector,CachedConnectorFactory> {
        CachedTicketStatusService(CachedConnector connector, TicketStatusService<?,?> wrapped) {
            super(connector, String.class, TicketStatus.class, wrapped);
        }
    }
    final CachedTicketStatusService ticketStatuses;
    public TicketStatusService<CachedConnector,CachedConnectorFactory> getTicketStatuses() {
        return ticketStatuses;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TicketTypeService">
    static class CachedTicketTypeService extends CachedService<String,TicketType> implements TicketTypeService<CachedConnector,CachedConnectorFactory> {
        CachedTicketTypeService(CachedConnector connector, TicketTypeService<?,?> wrapped) {
            super(connector, String.class, TicketType.class, wrapped);
        }
    }
    final CachedTicketTypeService ticketTypes;
    public TicketTypeService<CachedConnector,CachedConnectorFactory> getTicketTypes() {
        return ticketTypes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TicketService">
    // TODO: final CachedTicketService tickets;
    // TODO: public TicketService<CachedConnector,CachedConnectorFactory> getTickets();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TimeZoneService">
    static class CachedTimeZoneService extends CachedService<String,TimeZone> implements TimeZoneService<CachedConnector,CachedConnectorFactory> {
        CachedTimeZoneService(CachedConnector connector, TimeZoneService<?,?> wrapped) {
            super(connector, String.class, TimeZone.class, wrapped);
        }
    }
    final CachedTimeZoneService timeZones;
    public TimeZoneService<CachedConnector,CachedConnectorFactory> getTimeZones() {
        return timeZones;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TransactionTypeService">
    // TODO: final CachedTransactionTypeService transactionTypes;
    // TODO: public TransactionTypeService<CachedConnector,CachedConnectorFactory> getTransactionTypes();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TransactionService">
    // TODO: final CachedTransactionService transactions;
    // TODO: public TransactionService<CachedConnector,CachedConnectorFactory> getTransactions();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="UsernameService">
    static class CachedUsernameService extends CachedService<UserId,Username> implements UsernameService<CachedConnector,CachedConnectorFactory> {
        CachedUsernameService(CachedConnector connector, UsernameService<?,?> wrapped) {
            super(connector, UserId.class, Username.class, wrapped);
        }
    }
    final CachedUsernameService usernames;
    public UsernameService<CachedConnector,CachedConnectorFactory> getUsernames() {
        return usernames;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="VirtualDiskService">
    // TODO: final CachedVirtualDiskService virtualDisks;
    // TODO: public VirtualDiskService<CachedConnector,CachedConnectorFactory> getVirtualDisks();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="VirtualServerService">
    // TODO: final CachedVirtualServerService virtualServers;
    // TODO: public VirtualServerService<CachedConnector,CachedConnectorFactory> getVirtualServers();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="WhoisHistoryService">
    // TODO: final CachedWhoisHistoryService whoisHistories;
    // TODO: public WhoisHistoryService<CachedConnector,CachedConnectorFactory> getWhoisHistory();
    // </editor-fold>
}
