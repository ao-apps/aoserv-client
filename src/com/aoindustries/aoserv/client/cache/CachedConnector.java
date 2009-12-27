package com.aoindustries.aoserv.client.cache;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.AOServConnectorUtils;
import com.aoindustries.aoserv.client.AOServPermissionService;
import com.aoindustries.aoserv.client.AOServService;
import com.aoindustries.aoserv.client.AOServerResourceService;
import com.aoindustries.aoserv.client.AOServerService;
import com.aoindustries.aoserv.client.ArchitectureService;
import com.aoindustries.aoserv.client.BackupPartitionService;
import com.aoindustries.aoserv.client.BackupRetentionService;
import com.aoindustries.aoserv.client.BusinessAdministrator;
import com.aoindustries.aoserv.client.BusinessAdministratorService;
import com.aoindustries.aoserv.client.BusinessService;
import com.aoindustries.aoserv.client.CountryCodeService;
import com.aoindustries.aoserv.client.DisableLogService;
import com.aoindustries.aoserv.client.FailoverFileReplicationService;
import com.aoindustries.aoserv.client.FailoverMySQLReplicationService;
import com.aoindustries.aoserv.client.LanguageService;
import com.aoindustries.aoserv.client.MySQLServerService;
import com.aoindustries.aoserv.client.NetBindService;
import com.aoindustries.aoserv.client.NetDeviceIDService;
import com.aoindustries.aoserv.client.NetProtocolService;
import com.aoindustries.aoserv.client.OperatingSystemService;
import com.aoindustries.aoserv.client.OperatingSystemVersionService;
import com.aoindustries.aoserv.client.PackageCategoryService;
import com.aoindustries.aoserv.client.ProtocolService;
import com.aoindustries.aoserv.client.ResourceService;
import com.aoindustries.aoserv.client.ResourceTypeService;
import com.aoindustries.aoserv.client.ServerFarmService;
import com.aoindustries.aoserv.client.ServerService;
import com.aoindustries.aoserv.client.ServiceName;
import com.aoindustries.aoserv.client.TechnologyClassService;
import com.aoindustries.aoserv.client.TechnologyNameService;
import com.aoindustries.aoserv.client.TechnologyService;
import com.aoindustries.aoserv.client.TechnologyVersionService;
import com.aoindustries.aoserv.client.TicketCategoryService;
import com.aoindustries.aoserv.client.TicketPriorityService;
import com.aoindustries.aoserv.client.TicketStatusService;
import com.aoindustries.aoserv.client.TicketTypeService;
import com.aoindustries.aoserv.client.TimeZoneService;
import com.aoindustries.aoserv.client.UsernameService;
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
    final String connectAs;
    private final String authenticateAs;
    private final String password;
    /* TODO
    final CachedAOServerDaemonHostService aoserverDaemonHosts;
     */
    final CachedAOServerResourceService aoserverResources;
    final CachedAOServerService aoservers;
    final CachedAOServPermissionService aoservPermissions;
    /* TODO
    final CachedAOServProtocolService aoservProtocols;
    final CachedAOSHCommandService aoshCommands;
     */
    final CachedArchitectureService architectures;
    final CachedBackupPartitionService backupPartitions;
    final CachedBackupRetentionService backupRetentions;
    /* TODO
    final CachedBankAccountService bankAccounts;
    final CachedBankTransactionTypeService bankTransactionTypes;
    final CachedBankTransactionService bankTransactions;
    final CachedBankService banks;
    final CachedBlackholeEmailAddressService blackholeEmailAddresss;
    final CachedBrandService brands;
     */
    final CachedBusinessAdministratorService businessAdministrators;
    /* TODO
    final CachedBusinessAdministratorPermissionService businessAdministratorPermissions;
    final CachedBusinessProfileService businessProfiles;
     */
    final CachedBusinessService businesses;
    /* TODO
    final CachedBusinessServerService businessServers;
    final CachedClientJvmProfileService clientJvmProfiles;
     */
    final CachedCountryCodeService countryCodes;
    /*
    final CachedCreditCardProcessorService creditCardProcessors;
    final CachedCreditCardTransactionService creditCardTransactions;
    final CachedCreditCardService creditCards;
    final CachedCvsRepositoryService cvsRepositories;
     */
    final CachedDisableLogService disableLogs;
    /*
    final CachedDistroFileTypeService distroFileTypes;
    final CachedDistroFileService distroFiles;
    final CachedDNSForbiddenZoneService dnsForbiddenZones;
    final CachedDNSRecordService dnsRecords;
    final CachedDNSTLDService dnsTLDs;
    final CachedDNSTypeService dnsTypes;
    final CachedDNSZoneService dnsZones;
    final CachedEmailAddressService emailAddresss;
    final CachedEmailAttachmentBlockService emailAttachmentBlocks;
    final CachedEmailAttachmentTypeService emailAttachmentTypes;
    final CachedEmailDomainService emailDomains;
    final CachedEmailForwardingService emailForwardings;
    final CachedEmailListAddressService emailListAddresss;
    final CachedEmailListService emailLists;
    final CachedEmailPipeAddressService emailPipeAddresss;
    final CachedEmailPipeService emailPipes;
    final CachedEmailSmtpRelayTypeService emailSmtpRelayTypes;
    final CachedEmailSmtpRelayService emailSmtpRelays;
    final CachedEmailSmtpSmartHostDomainService emailSmtpSmartHostDomains;
    final CachedEmailSmtpSmartHostService emailSmtpSmartHosts;
    final CachedEmailSpamAssassinIntegrationModeService emailSpamAssassinIntegrationModes;
    final CachedEncryptionKeyService encryptionKeys;
    final CachedExpenseCategoryService expenseCategories;
    final CachedFailoverFileLogService failoverFileLogs;
     */
    final CachedFailoverFileReplicationService failoverFileReplications;
    /* TODO
    final CachedFailoverFileScheduleService failoverFileSchedules;
     */
    final CachedFailoverMySQLReplicationService failoverMySQLReplications;
    /* TODO
    final CachedFileBackupSettingService fileBackupSettings;
    final CachedFTPGuestUserService ftpGuestUsers;
    final CachedHttpdBindService httpdBinds;
    final CachedHttpdJBossSiteService httpdJBossSites;
    final CachedHttpdJBossVersionService httpdJBossVersions;
    final CachedHttpdJKCodeService httpdJKCodes;
    final CachedHttpdJKProtocolService httpdJKProtocols;
    final CachedHttpdServerService httpdServers;
    final CachedHttpdSharedTomcatService httpdSharedTomcats;
    final CachedHttpdSiteAuthenticatedLocationService httpdSiteAuthenticatedLocations;
    final CachedHttpdSiteBindService httpdSiteBinds;
    final CachedHttpdSiteURLService httpdSiteURLs;
    final CachedHttpdSiteService httpdSites;
    final CachedHttpdStaticSiteService httpdStaticSites;
    final CachedHttpdTomcatContextService httpdTomcatContexts;
    final CachedHttpdTomcatDataSourceService httpdTomcatDataSources;
    final CachedHttpdTomcatParameterService httpdTomcatParameters;
    final CachedHttpdTomcatSiteService httpdTomcatSites;
    final CachedHttpdTomcatSharedSiteService httpdTomcatSharedSites;
    final CachedHttpdTomcatStdSiteService httpdTomcatStdSites;
    final CachedHttpdTomcatVersionService httpdTomcatVersions;
    final CachedHttpdWorkerService httpdWorkers;
    final CachedIPAddressService ipAddresss;
    */
    final CachedLanguageService languages;
    /* TODO
    final CachedLinuxAccAddressService linuxAccAddresss;
    final CachedLinuxAccountTypeService linuxAccountTypes;
    final CachedLinuxAccountService linuxAccounts;
    final CachedLinuxGroupAccountService linuxGroupAccounts;
    final CachedLinuxGroupTypeService linuxGroupTypes;
    final CachedLinuxGroupService linuxGroups;
    final CachedLinuxIDService linuxIDs;
    final CachedLinuxServerAccountService linuxServerAccounts;
    final CachedLinuxServerGroupService linuxServerGroups;
    final CachedMajordomoListService majordomoLists;
    final CachedMajordomoServerService majordomoServers;
    final CachedMajordomoVersionService majordomoVersions;
    final CachedMasterHistoryService masterHistories;
    final CachedMasterHostService masterHosts;
    final CachedMasterServerService masterServers;
    final CachedMasterUserService masterUsers;
    final CachedMonthlyChargeService monthlyCharges;
    final CachedMySQLDatabaseService mysqlDatabases;
    final CachedMySQLDBUserService mysqlDBUsers;
    final CachedMySQLReservedWordService mysqlReservedWords;
     */
    final CachedMySQLServerService mysqlServers;
    /* TODO
    final CachedMySQLUserService mysqlUsers;
     */
    final CachedNetBindService netBinds;
    final CachedNetDeviceIDService netDeviceIDs;
    /* TODO
    final CachedNetDeviceService netDevices;
    final CachedNetPortService netPorts;
     */
    final CachedNetProtocolService netProtocols;
    /* TODO
    final CachedNetTcpRedirectService netTcpRedirects;
    final CachedNoticeLogService noticeLogs;
    final CachedNoticeTypeService noticeTypes;
    */
    final CachedOperatingSystemVersionService operatingSystemVersions;
    final CachedOperatingSystemService operatingSystems;
    final CachedPackageCategoryService packageCategories;
    /* TODO
    final CachedPackageDefinitionLimitService packageDefinitionLimits;
    final CachedPackageDefinitionService packageDefinitions;
    final CachedPaymentTypeService paymentTypes;
    final CachedPhysicalServerService physicalServers;
    final CachedPostgresDatabaseService postgresDatabases;
    final CachedPostgresEncodingService postgresEncodings;
    final CachedPostgresReservedWordService postgresReservedWords;
    final CachedPostgresServerUserService postgresServerUsers;
    final CachedPostgresServerService postgresServers;
    final CachedPostgresUserService postgresUsers;
    final CachedPostgresVersionService postgresVersions;
    final CachedPrivateFTPServerService privateFTPServers;
    final CachedProcessorTypeService processorTypes;
     */
    final CachedProtocolService protocols;
    /* TODO
    final CachedRackService racks;
    final CachedResellerService resellers;
     */
    final CachedResourceTypeService resourceTypes;
    final CachedResourceService resources;
    final CachedServerFarmService serverFarms;
    final CachedServerService servers;
    /* TODO
    final CachedShellService shells;
    final CachedSignupRequestOptionService signupRequestOptions;
    final CachedSignupRequestService signupRequests;
    final CachedSpamEmailMessageService spamEmailMessages;
    final CachedSystemEmailAliasService systemEmailAliass;
     */
    final CachedTechnologyService technologies;
    final CachedTechnologyClassService technologyClasses;
    final CachedTechnologyNameService technologyNames;
    final CachedTechnologyVersionService technologyVersions;
    /* TODO
    final CachedTicketActionTypeService ticketActionTypes;
    final CachedTicketActionService ticketActions;
    final CachedTicketAssignmentService ticketAssignments;
    final CachedTicketBrandCategoryService ticketBrandCategories;
    */
    final CachedTicketCategoryService ticketCategories;
    final CachedTicketPriorityService ticketPriorities;
    final CachedTicketStatusService ticketStatuses;
    final CachedTicketTypeService ticketTypes;
    /* TODO
    final CachedTicketService tickets;
    */
    final CachedTimeZoneService timeZones;
    /* TODO
    final CachedTransactionTypeService transactionTypes;
    final CachedTransactionService transactions;
    final CachedUSStateService usStates;
     */
    final CachedUsernameService usernames;
    /* TODO
    final CachedVirtualDiskService virtualDisks;
    final CachedVirtualServerService virtualServers;
    final CachedWhoisHistoryService whoisHistories;
     */

    CachedConnector(CachedConnectorFactory factory, AOServConnector<?,?> wrapped) throws RemoteException, LoginException {
        this.factory = factory;
        this.wrapped = wrapped;
        locale = wrapped.getLocale();
        connectAs = wrapped.getConnectAs();
        authenticateAs = wrapped.getAuthenticateAs();
        password = wrapped.getPassword();
        /* TODO
        aoserverDaemonHosts = new CachedAOServerDaemonHostService(this, wrapped.getAOServerDaemonHosts());
         */
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
        /* TODO
        businessServers = new CachedBusinessServerService(this, wrapped.getBusinessServers());
        clientJvmProfiles = new CachedClientJvmProfileService(this, wrapped.getClientJvmProfiles());
         */
        countryCodes = new CachedCountryCodeService(this, wrapped.getCountryCodes());
        /* TODO
        creditCardProcessors = new CachedCreditCardProcessorService(this, wrapped.getCreditCardProcessors());
        creditCardTransactions = new CachedCreditCardTransactionService(this, wrapped.getCreditCardTransactions());
        creditCards = new CachedCreditCardService(this, wrapped.getCreditCards());
        cvsRepositories = new CachedCvsRepositoryService(this, wrapped.getCvsRepositorys());
         */
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
        failoverFileLogs = new CachedFailoverFileLogService(this, wrapped.getFailoverFileLogs());
         */
        failoverFileReplications = new CachedFailoverFileReplicationService(this, wrapped.getFailoverFileReplications());
        /* TODO
        failoverFileSchedules = new CachedFailoverFileScheduleService(this, wrapped.getFailoverFileSchedules());
         */
        failoverMySQLReplications = new CachedFailoverMySQLReplicationService(this, wrapped.getFailoverMySQLReplications());
        /* TODO
        fileBackupSettings = new CachedFileBackupSettingService(this, wrapped.getFileBackupSettings());
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
        httpdSites = new CachedHttpdSiteService(this, wrapped.getHttpdSites());
        httpdStaticSites = new CachedHttpdStaticSiteService(this, wrapped.getHttpdStaticSites());
        httpdTomcatContexts = new CachedHttpdTomcatContextService(this, wrapped.getHttpdTomcatContexts());
        httpdTomcatDataSources = new CachedHttpdTomcatDataSourceService(this, wrapped.getHttpdTomcatDataSources());
        httpdTomcatParameters = new CachedHttpdTomcatParameterService(this, wrapped.getHttpdTomcatParameters());
        httpdTomcatSites = new CachedHttpdTomcatSiteService(this, wrapped.getHttpdTomcatSites());
        httpdTomcatSharedSites = new CachedHttpdTomcatSharedSiteService(this, wrapped.getHttpdTomcatSharedSites());
        httpdTomcatStdSites = new CachedHttpdTomcatStdSiteService(this, wrapped.getHttpdTomcatStdSites());
        httpdTomcatVersions = new CachedHttpdTomcatVersionService(this, wrapped.getHttpdTomcatVersions());
        httpdWorkers = new CachedHttpdWorkerService(this, wrapped.getHttpdWorkers());
        ipAddresss = new CachedIPAddressService(this, wrapped.getIPAddresss());
        */
        languages = new CachedLanguageService(this, wrapped.getLanguages());
        /* TODO
        linuxAccAddresss = new CachedLinuxAccAddressService(this, wrapped.getLinuxAccAddresss());
        linuxAccountTypes = new CachedLinuxAccountTypeService(this, wrapped.getLinuxAccountTypes());
        linuxAccounts = new CachedLinuxAccountService(this, wrapped.getLinuxAccounts());
        linuxGroupAccounts = new CachedLinuxGroupAccountService(this, wrapped.getLinuxGroupAccounts());
        linuxGroupTypes = new CachedLinuxGroupTypeService(this, wrapped.getLinuxGroupTypes());
        linuxGroups = new CachedLinuxGroupService(this, wrapped.getLinuxGroups());
        linuxIDs = new CachedLinuxIDService(this, wrapped.getLinuxIDs());
        linuxServerAccounts = new CachedLinuxServerAccountService(this, wrapped.getLinuxServerAccounts());
        linuxServerGroups = new CachedLinuxServerGroupService(this, wrapped.getLinuxServerGroups());
        majordomoLists = new CachedMajordomoListService(this, wrapped.getMajordomoLists());
        majordomoServers = new CachedMajordomoServerService(this, wrapped.getMajordomoServers());
        majordomoVersions = new CachedMajordomoVersionService(this, wrapped.getMajordomoVersions());
        masterHistories = new CachedMasterHistoryService(this, wrapped.getMasterHistorys());
        masterHosts = new CachedMasterHostService(this, wrapped.getMasterHosts());
        masterServers = new CachedMasterServerService(this, wrapped.getMasterServers());
        masterUsers = new CachedMasterUserService(this, wrapped.getMasterUsers());
        monthlyCharges = new CachedMonthlyChargeService(this, wrapped.getMonthlyCharges());
        mysqlDatabases = new CachedMySQLDatabaseService(this, wrapped.getMySQLDatabases());
        mysqlDBUsers = new CachedMySQLDBUserService(this, wrapped.getMySQLDBUsers());
        mysqlReservedWords = new CachedMySQLReservedWordService(this, wrapped.getMySQLReservedWords());
         */
        mysqlServers = new CachedMySQLServerService(this, wrapped.getMysqlServers());
        /* TODO
        mysqlUsers = new CachedMySQLUserService(this, wrapped.getMySQLUsers());
         */
        netBinds = new CachedNetBindService(this, wrapped.getNetBinds());
        netDeviceIDs = new CachedNetDeviceIDService(this, wrapped.getNetDeviceIDs());
        /* TODO
        netDevices = new CachedNetDeviceService(this, wrapped.getNetDevices());
        netPorts = new CachedNetPortService(this, wrapped.getNetPorts());
         */
        netProtocols = new CachedNetProtocolService(this, wrapped.getNetProtocols());
        /* TODO
        netTcpRedirects = new CachedNetTcpRedirectService(this, wrapped.getNetTcpRedirects());
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
        postgresDatabases = new CachedPostgresDatabaseService(this, wrapped.getPostgresDatabases());
        postgresEncodings = new CachedPostgresEncodingService(this, wrapped.getPostgresEncodings());
        postgresReservedWords = new CachedPostgresReservedWordService(this, wrapped.getPostgresReservedWords());
        postgresServerUsers = new CachedPostgresServerUserService(this, wrapped.getPostgresServerUsers());
        postgresServers = new CachedPostgresServerService(this, wrapped.getPostgresServers());
        postgresUsers = new CachedPostgresUserService(this, wrapped.getPostgresUsers());
        postgresVersions = new CachedPostgresVersionService(this, wrapped.getPostgresVersions());
        privateFTPServers = new CachedPrivateFTPServerService(this, wrapped.getPrivateFTPServers());
        processorTypes = new CachedProcessorTypeService(this, wrapped.getProcessorTypes());
         */
        protocols = new CachedProtocolService(this, wrapped.getProtocols());
        /* TODO
        racks = new CachedRackService(this, wrapped.getRacks());
        resellers = new CachedResellerService(this, wrapped.getResellers());
         */
        resourceTypes = new CachedResourceTypeService(this, wrapped.getResourceTypes());
        resources = new CachedResourceService(this, wrapped.getResources());
        serverFarms = new CachedServerFarmService(this, wrapped.getServerFarms());
        servers = new CachedServerService(this, wrapped.getServers());
        /* TODO
        shells = new CachedShellService(this, wrapped.getShells());
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

    public String getConnectAs() {
        return connectAs;
    }

    public BusinessAdministrator getThisBusinessAdministrator() throws RemoteException {
        BusinessAdministrator obj = getBusinessAdministrators().get(connectAs);
        if(obj==null) throw new RemoteException("Unable to find BusinessAdministrator: "+connectAs);
        return obj;
    }

    public String getAuthenticateAs() {
        return authenticateAs;
    }

    public String getPassword() {
        return password;
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

    /*
     * TODO
    public AOServerDaemonHostService<CachedConnector,CachedConnectorFactory> getAoServerDaemonHosts();
    */
    public AOServerResourceService<CachedConnector,CachedConnectorFactory> getAoServerResources() {
        return aoserverResources;
    }

    public AOServerService<CachedConnector,CachedConnectorFactory> getAoServers() {
        return aoservers;
    }

    public AOServPermissionService<CachedConnector,CachedConnectorFactory> getAoservPermissions() {
        return aoservPermissions;
    }
    /* TODO
    public AOServProtocolService<CachedConnector,CachedConnectorFactory> getAoservProtocols();

    public AOSHCommandService<CachedConnector,CachedConnectorFactory> getAoshCommands();
    */
    public ArchitectureService<CachedConnector,CachedConnectorFactory> getArchitectures() {
        return architectures;
    }

    public BackupPartitionService<CachedConnector,CachedConnectorFactory> getBackupPartitions() {
        return backupPartitions;
    }

    public BackupRetentionService<CachedConnector,CachedConnectorFactory> getBackupRetentions() {
        return backupRetentions;
    }
    /* TODO
    public BankAccountService<CachedConnector,CachedConnectorFactory> getBankAccounts();

    public BankTransactionTypeService<CachedConnector,CachedConnectorFactory> getBankTransactionTypes();

    public BankTransactionService<CachedConnector,CachedConnectorFactory> getBankTransactions();

    public BankService<CachedConnector,CachedConnectorFactory> getBanks();

    public BlackholeEmailAddressService<CachedConnector,CachedConnectorFactory> getBlackholeEmailAddresses();

    public BrandService<CachedConnector,CachedConnectorFactory> getBrands();
     */
    public BusinessAdministratorService<CachedConnector,CachedConnectorFactory> getBusinessAdministrators() {
        return businessAdministrators;
    }
    /*
    public BusinessAdministratorPermissionService<CachedConnector,CachedConnectorFactory> getBusinessAdministratorPermissions();

    public BusinessProfileService<CachedConnector,CachedConnectorFactory> getBusinessProfiles();
     */
    public BusinessService<CachedConnector,CachedConnectorFactory> getBusinesses() {
        return businesses;
    }

    /* TODO
    public BusinessServerService<CachedConnector,CachedConnectorFactory> getBusinessServers();

    public ClientJvmProfileService<CachedConnector,CachedConnectorFactory> getClientJvmProfiles();
    */
    public CountryCodeService<CachedConnector,CachedConnectorFactory> getCountryCodes() {
        return countryCodes;
    }
    /* TODO

    public CreditCardProcessorService<CachedConnector,CachedConnectorFactory> getCreditCardProcessors();

    public CreditCardTransactionService<CachedConnector,CachedConnectorFactory> getCreditCardTransactions();

    public CreditCardService<CachedConnector,CachedConnectorFactory> getCreditCards();

    public CvsRepositoryService<CachedConnector,CachedConnectorFactory> getCvsRepositories();
     */
    public DisableLogService<CachedConnector,CachedConnectorFactory> getDisableLogs() {
        return disableLogs;
    }
    /*
    public DistroFileTypeService<CachedConnector,CachedConnectorFactory> getDistroFileTypes();

    public DistroFileService<CachedConnector,CachedConnectorFactory> getDistroFiles();

    public DNSForbiddenZoneService<CachedConnector,CachedConnectorFactory> getDnsForbiddenZones();

    public DNSRecordService<CachedConnector,CachedConnectorFactory> getDnsRecords();

    public DNSTLDService<CachedConnector,CachedConnectorFactory> getDnsTLDs();

    public DNSTypeService<CachedConnector,CachedConnectorFactory> getDnsTypes();

    public DNSZoneService<CachedConnector,CachedConnectorFactory> getDnsZones();

    public EmailAddressService<CachedConnector,CachedConnectorFactory> getEmailAddresses();

    public EmailAttachmentBlockService<CachedConnector,CachedConnectorFactory> getEmailAttachmentBlocks();

    public EmailAttachmentTypeService<CachedConnector,CachedConnectorFactory> getEmailAttachmentTypes();

    public EmailDomainService<CachedConnector,CachedConnectorFactory> getEmailDomains();

    public EmailForwardingService<CachedConnector,CachedConnectorFactory> getEmailForwardings();

    public EmailListAddressService<CachedConnector,CachedConnectorFactory> getEmailListAddresses();

    public EmailListService<CachedConnector,CachedConnectorFactory> getEmailLists();

    public EmailPipeAddressService<CachedConnector,CachedConnectorFactory> getEmailPipeAddresses();

    public EmailPipeService<CachedConnector,CachedConnectorFactory> getEmailPipes();

    public EmailSmtpRelayTypeService<CachedConnector,CachedConnectorFactory> getEmailSmtpRelayTypes();

    public EmailSmtpRelayService<CachedConnector,CachedConnectorFactory> getEmailSmtpRelays();

    public EmailSmtpSmartHostDomainService<CachedConnector,CachedConnectorFactory> getEmailSmtpSmartHostDomains();

    public EmailSmtpSmartHostService<CachedConnector,CachedConnectorFactory> getEmailSmtpSmartHosts();

    public EmailSpamAssassinIntegrationModeService<CachedConnector,CachedConnectorFactory> getEmailSpamAssassinIntegrationModes();

    public EncryptionKeyService<CachedConnector,CachedConnectorFactory> getEncryptionKeys();

    public ExpenseCategoryService<CachedConnector,CachedConnectorFactory> getExpenseCategories();

    public FailoverFileLogService<CachedConnector,CachedConnectorFactory> getFailoverFileLogs();
    */
    public FailoverFileReplicationService<CachedConnector,CachedConnectorFactory> getFailoverFileReplications() {
        return failoverFileReplications;
    }
    /* TODO
    public FailoverFileScheduleService<CachedConnector,CachedConnectorFactory> getFailoverFileSchedules();
    */
    public FailoverMySQLReplicationService<CachedConnector,CachedConnectorFactory> getFailoverMySQLReplications() {
        return failoverMySQLReplications;
    }
    /* TODO
    public FileBackupSettingService<CachedConnector,CachedConnectorFactory> getFileBackupSettings();

    public FTPGuestUserService<CachedConnector,CachedConnectorFactory> getFtpGuestUsers();

    public HttpdBindService<CachedConnector,CachedConnectorFactory> getHttpdBinds();

    public HttpdJBossSiteService<CachedConnector,CachedConnectorFactory> getHttpdJBossSites();

    public HttpdJBossVersionService<CachedConnector,CachedConnectorFactory> getHttpdJBossVersions();

    public HttpdJKCodeService<CachedConnector,CachedConnectorFactory> getHttpdJKCodes();

    public HttpdJKProtocolService<CachedConnector,CachedConnectorFactory> getHttpdJKProtocols();

    public HttpdServerService<CachedConnector,CachedConnectorFactory> getHttpdServers();

    public HttpdSharedTomcatService<CachedConnector,CachedConnectorFactory> getHttpdSharedTomcats();

    public HttpdSiteAuthenticatedLocationService<CachedConnector,CachedConnectorFactory> getHttpdSiteAuthenticatedLocations();

    public HttpdSiteBindService<CachedConnector,CachedConnectorFactory> getHttpdSiteBinds();

    public HttpdSiteURLService<CachedConnector,CachedConnectorFactory> getHttpdSiteURLs();

    public HttpdSiteService<CachedConnector,CachedConnectorFactory> getHttpdSites();

    public HttpdStaticSiteService<CachedConnector,CachedConnectorFactory> getHttpdStaticSites();

    public HttpdTomcatContextService<CachedConnector,CachedConnectorFactory> getHttpdTomcatContexts();

    public HttpdTomcatDataSourceService<CachedConnector,CachedConnectorFactory> getHttpdTomcatDataSources();

    public HttpdTomcatParameterService<CachedConnector,CachedConnectorFactory> getHttpdTomcatParameters();

    public HttpdTomcatSiteService<CachedConnector,CachedConnectorFactory> getHttpdTomcatSites();

    public HttpdTomcatSharedSiteService<CachedConnector,CachedConnectorFactory> getHttpdTomcatSharedSites();

    public HttpdTomcatStdSiteService<CachedConnector,CachedConnectorFactory> getHttpdTomcatStdSites();

    public HttpdTomcatVersionService<CachedConnector,CachedConnectorFactory> getHttpdTomcatVersions();

    public HttpdWorkerService<CachedConnector,CachedConnectorFactory> getHttpdWorkers();

    public IPAddressService<CachedConnector,CachedConnectorFactory> getIpAddresses();
    */
    public LanguageService<CachedConnector,CachedConnectorFactory> getLanguages() {
        return languages;
    }
    /* TODO
    public LinuxAccAddressService<CachedConnector,CachedConnectorFactory> getLinuxAccAddresses();

    public LinuxAccountTypeService<CachedConnector,CachedConnectorFactory> getLinuxAccountTypes();

    public LinuxAccountService<CachedConnector,CachedConnectorFactory> getLinuxAccounts();

    public LinuxGroupAccountService<CachedConnector,CachedConnectorFactory> getLinuxGroupAccounts();

    public LinuxGroupTypeService<CachedConnector,CachedConnectorFactory> getLinuxGroupTypes();

    public LinuxGroupService<CachedConnector,CachedConnectorFactory> getLinuxGroups();

    public LinuxIDService<CachedConnector,CachedConnectorFactory> getLinuxIDs();

    public LinuxServerAccountService<CachedConnector,CachedConnectorFactory> getLinuxServerAccounts();

    public LinuxServerGroupService<CachedConnector,CachedConnectorFactory> getLinuxServerGroups();

    public MajordomoListService<CachedConnector,CachedConnectorFactory> getMajordomoLists();

    public MajordomoServerService<CachedConnector,CachedConnectorFactory> getMajordomoServers();

    public MajordomoVersionService<CachedConnector,CachedConnectorFactory> getMajordomoVersions();

    public MasterHistoryService<CachedConnector,CachedConnectorFactory> getMasterHistory();

    public MasterHostService<CachedConnector,CachedConnectorFactory> getMasterHosts();

    public MasterServerService<CachedConnector,CachedConnectorFactory> getMasterServers();

    public MasterUserService<CachedConnector,CachedConnectorFactory> getMasterUsers();

    public MonthlyChargeService<CachedConnector,CachedConnectorFactory> getMonthlyCharges();

    public MySQLDatabaseService<CachedConnector,CachedConnectorFactory> getMysqlDatabases();

    public MySQLDBUserService<CachedConnector,CachedConnectorFactory> getMysqlDBUsers();

    public MySQLReservedWordService<CachedConnector,CachedConnectorFactory> getMysqlReservedWords();
    */
    public MySQLServerService<CachedConnector,CachedConnectorFactory> getMysqlServers() {
        return mysqlServers;
    }
    /* TODO
    public MySQLUserService<CachedConnector,CachedConnectorFactory> getMysqlUsers();
    */
    public NetBindService<CachedConnector,CachedConnectorFactory> getNetBinds() {
        return netBinds;
    }

    public NetDeviceIDService<CachedConnector,CachedConnectorFactory> getNetDeviceIDs() {
        return netDeviceIDs;
    }
    /* TODO
    public NetDeviceService<CachedConnector,CachedConnectorFactory> getNetDevices();

    public NetPortService<CachedConnector,CachedConnectorFactory> getNetPorts();
    */
    public NetProtocolService<CachedConnector,CachedConnectorFactory> getNetProtocols() {
        return netProtocols;
    }
    /* TODO
    public NetTcpRedirectService<CachedConnector,CachedConnectorFactory> getNetTcpRedirects();

    public NoticeLogService<CachedConnector,CachedConnectorFactory> getNoticeLogs();

    public NoticeTypeService<CachedConnector,CachedConnectorFactory> getNoticeTypes();
    */
    public OperatingSystemVersionService<CachedConnector,CachedConnectorFactory> getOperatingSystemVersions() {
        return operatingSystemVersions;
    }

    public OperatingSystemService<CachedConnector,CachedConnectorFactory> getOperatingSystems() {
        return operatingSystems;
    }

    public PackageCategoryService<CachedConnector,CachedConnectorFactory> getPackageCategories() {
        return packageCategories;
    }
    /*
    public PackageDefinitionLimitService<CachedConnector,CachedConnectorFactory> getPackageDefinitionLimits();

    public PackageDefinitionService<CachedConnector,CachedConnectorFactory> getPackageDefinitions();

    public PaymentTypeService<CachedConnector,CachedConnectorFactory> getPaymentTypes();

    public PhysicalServerService<CachedConnector,CachedConnectorFactory> getPhysicalServers();

    public PostgresDatabaseService<CachedConnector,CachedConnectorFactory> getPostgresDatabases();

    public PostgresEncodingService<CachedConnector,CachedConnectorFactory> getPostgresEncodings();

    public PostgresReservedWordService<CachedConnector,CachedConnectorFactory> getPostgresReservedWords();

    public PostgresServerUserService<CachedConnector,CachedConnectorFactory> getPostgresServerUsers();

    public PostgresServerService<CachedConnector,CachedConnectorFactory> getPostgresServers();

    public PostgresUserService<CachedConnector,CachedConnectorFactory> getPostgresUsers();

    public PostgresVersionService<CachedConnector,CachedConnectorFactory> getPostgresVersions();

    public PrivateFTPServerService<CachedConnector,CachedConnectorFactory> getPrivateFTPServers();

    public ProcessorTypeService<CachedConnector,CachedConnectorFactory> getProcessorTypes();
    */
    public ProtocolService<CachedConnector,CachedConnectorFactory> getProtocols() {
        return protocols;
    }
    /* TODO
    public RackService<CachedConnector,CachedConnectorFactory> getRacks();

    public ResellerService<CachedConnector,CachedConnectorFactory> getResellers();
    */
    public ResourceTypeService<CachedConnector,CachedConnectorFactory> getResourceTypes() {
        return resourceTypes;
    }

    public ResourceService<CachedConnector,CachedConnectorFactory> getResources() {
        return resources;
    }

    public ServerFarmService<CachedConnector,CachedConnectorFactory> getServerFarms() {
        return serverFarms;
    }

    public ServerService<CachedConnector,CachedConnectorFactory> getServers() {
        return servers;
    }
    /* TODO
    public ShellService<CachedConnector,CachedConnectorFactory> getShells();

    public SignupRequestOptionService<CachedConnector,CachedConnectorFactory> getSignupRequestOptions();

    public SignupRequestService<CachedConnector,CachedConnectorFactory> getSignupRequests();

    public SpamEmailMessageService<CachedConnector,CachedConnectorFactory> getSpamEmailMessages();

    public SystemEmailAliasService<CachedConnector,CachedConnectorFactory> getSystemEmailAliases();
    */
    public TechnologyService<CachedConnector,CachedConnectorFactory> getTechnologies() {
        return technologies;
    }

    public TechnologyClassService<CachedConnector,CachedConnectorFactory> getTechnologyClasses() {
        return technologyClasses;
    }

    public TechnologyNameService<CachedConnector,CachedConnectorFactory> getTechnologyNames() {
        return technologyNames;
    }

    public TechnologyVersionService<CachedConnector,CachedConnectorFactory> getTechnologyVersions() {
        return technologyVersions;
    }
    /* TODO
    public TicketActionTypeService<CachedConnector,CachedConnectorFactory> getTicketActionTypes();

    public TicketActionService<CachedConnector,CachedConnectorFactory> getTicketActions();

    public TicketAssignmentService<CachedConnector,CachedConnectorFactory> getTicketAssignments();

    public TicketBrandCategoryService<CachedConnector,CachedConnectorFactory> getTicketBrandCategories();
    */
    public TicketCategoryService<CachedConnector,CachedConnectorFactory> getTicketCategories() {
        return ticketCategories;
    }

    public TicketPriorityService<CachedConnector,CachedConnectorFactory> getTicketPriorities() {
        return ticketPriorities;
    }

    public TicketStatusService<CachedConnector,CachedConnectorFactory> getTicketStatuses() {
        return ticketStatuses;
    }

    public TicketTypeService<CachedConnector,CachedConnectorFactory> getTicketTypes() {
        return ticketTypes;
    }
    /* TODO
    public TicketService<CachedConnector,CachedConnectorFactory> getTickets();
    */
    public TimeZoneService<CachedConnector,CachedConnectorFactory> getTimeZones() {
        return timeZones;
    }
    /* TODO
    public TransactionTypeService<CachedConnector,CachedConnectorFactory> getTransactionTypes();

    public TransactionService<CachedConnector,CachedConnectorFactory> getTransactions();

    public USStateService<CachedConnector,CachedConnectorFactory> getUsStates();
    */
    public UsernameService<CachedConnector,CachedConnectorFactory> getUsernames() {
        return usernames;
    }
    /* TODO
    public VirtualDiskService<CachedConnector,CachedConnectorFactory> getVirtualDisks();

    public VirtualServerService<CachedConnector,CachedConnectorFactory> getVirtualServers();

    public WhoisHistoryService<CachedConnector,CachedConnectorFactory> getWhoisHistory();
 */
}
