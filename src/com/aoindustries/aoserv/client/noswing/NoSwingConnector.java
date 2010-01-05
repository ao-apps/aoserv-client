package com.aoindustries.aoserv.client.noswing;

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
import com.aoindustries.aoserv.client.LinuxAccountService;
import com.aoindustries.aoserv.client.LinuxAccountTypeService;
import com.aoindustries.aoserv.client.LinuxGroupTypeService;
import com.aoindustries.aoserv.client.MySQLDBUserService;
import com.aoindustries.aoserv.client.MySQLDatabaseService;
import com.aoindustries.aoserv.client.MySQLReservedWordService;
import com.aoindustries.aoserv.client.MySQLServerService;
import com.aoindustries.aoserv.client.MySQLUserService;
import com.aoindustries.aoserv.client.NetBindService;
import com.aoindustries.aoserv.client.NetDeviceIDService;
import com.aoindustries.aoserv.client.NetProtocolService;
import com.aoindustries.aoserv.client.OperatingSystemService;
import com.aoindustries.aoserv.client.OperatingSystemVersionService;
import com.aoindustries.aoserv.client.PackageCategoryService;
import com.aoindustries.aoserv.client.PostgresDatabaseService;
import com.aoindustries.aoserv.client.PostgresEncodingService;
import com.aoindustries.aoserv.client.PostgresReservedWordService;
import com.aoindustries.aoserv.client.PostgresServerService;
import com.aoindustries.aoserv.client.PostgresUserService;
import com.aoindustries.aoserv.client.PostgresVersionService;
import com.aoindustries.aoserv.client.ProtocolService;
import com.aoindustries.aoserv.client.ResourceService;
import com.aoindustries.aoserv.client.ResourceTypeService;
import com.aoindustries.aoserv.client.ServerFarmService;
import com.aoindustries.aoserv.client.ServerService;
import com.aoindustries.aoserv.client.ServiceName;
import com.aoindustries.aoserv.client.ShellService;
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
 * @see NoSwingConnectorFactory
 *
 * @author  AO Industries, Inc.
 */
final public class NoSwingConnector implements AOServConnector<NoSwingConnector,NoSwingConnectorFactory> {

    final NoSwingConnectorFactory factory;
    final AOServConnector<?,?> wrapped;
    /* TODO
    final NoSwingAOServerDaemonHostService aoserverDaemonHosts;
     */
    final NoSwingAOServerResourceService aoserverResources;
    final NoSwingAOServerService aoservers;
    final NoSwingAOServPermissionService aoservPermissions;
    /* TODO
    final NoSwingAOServProtocolService aoservProtocols;
    final NoSwingAOSHCommandService aoshCommands;
     */
    final NoSwingArchitectureService architectures;
    final NoSwingBackupPartitionService backupPartitions;
    final NoSwingBackupRetentionService backupRetentions;
    /* TODO
    final NoSwingBankAccountService bankAccounts;
    final NoSwingBankTransactionTypeService bankTransactionTypes;
    final NoSwingBankTransactionService bankTransactions;
    final NoSwingBankService banks;
    final NoSwingBlackholeEmailAddressService blackholeEmailAddresss;
    final NoSwingBrandService brands;
     */
    final NoSwingBusinessAdministratorService businessAdministrators;
    /* TODO
    final NoSwingBusinessAdministratorPermissionService businessAdministratorPermissions;
    final NoSwingBusinessProfileService businessProfiles;
     */
    final NoSwingBusinessService businesses;
    /* TODO
    final NoSwingBusinessServerService businessServers;
    final NoSwingClientJvmProfileService clientJvmProfiles;
     */
    final NoSwingCountryCodeService countryCodes;
    /* TODO
    final NoSwingCreditCardProcessorService creditCardProcessors;
    final NoSwingCreditCardTransactionService creditCardTransactions;
    final NoSwingCreditCardService creditCards;
    final NoSwingCvsRepositoryService cvsRepositories;
     */
    final NoSwingDisableLogService disableLogs;
    /*
    final NoSwingDistroFileTypeService distroFileTypes;
    final NoSwingDistroFileService distroFiles;
    final NoSwingDNSForbiddenZoneService dnsForbiddenZones;
    final NoSwingDNSRecordService dnsRecords;
    final NoSwingDNSTLDService dnsTLDs;
    final NoSwingDNSTypeService dnsTypes;
    final NoSwingDNSZoneService dnsZones;
    final NoSwingEmailAddressService emailAddresss;
    final NoSwingEmailAttachmentBlockService emailAttachmentBlocks;
    final NoSwingEmailAttachmentTypeService emailAttachmentTypes;
    final NoSwingEmailDomainService emailDomains;
    final NoSwingEmailForwardingService emailForwardings;
    final NoSwingEmailListAddressService emailListAddresss;
    final NoSwingEmailListService emailLists;
    final NoSwingEmailPipeAddressService emailPipeAddresss;
    final NoSwingEmailPipeService emailPipes;
    final NoSwingEmailSmtpRelayTypeService emailSmtpRelayTypes;
    final NoSwingEmailSmtpRelayService emailSmtpRelays;
    final NoSwingEmailSmtpSmartHostDomainService emailSmtpSmartHostDomains;
    final NoSwingEmailSmtpSmartHostService emailSmtpSmartHosts;
    final NoSwingEmailSpamAssassinIntegrationModeService emailSpamAssassinIntegrationModes;
    final NoSwingEncryptionKeyService encryptionKeys;
    final NoSwingExpenseCategoryService expenseCategories;
    final NoSwingFailoverFileLogService failoverFileLogs;
     */
    final NoSwingFailoverFileReplicationService failoverFileReplications;
    /* TODO
    final NoSwingFailoverFileScheduleService failoverFileSchedules;
     */
    final NoSwingFailoverMySQLReplicationService failoverMySQLReplications;
    /* TODO
    final NoSwingFileBackupSettingService fileBackupSettings;
    final NoSwingFTPGuestUserService ftpGuestUsers;
    final NoSwingHttpdBindService httpdBinds;
    final NoSwingHttpdJBossSiteService httpdJBossSites;
    final NoSwingHttpdJBossVersionService httpdJBossVersions;
    final NoSwingHttpdJKCodeService httpdJKCodes;
    final NoSwingHttpdJKProtocolService httpdJKProtocols;
    final NoSwingHttpdServerService httpdServers;
    final NoSwingHttpdSharedTomcatService httpdSharedTomcats;
    final NoSwingHttpdSiteAuthenticatedLocationService httpdSiteAuthenticatedLocations;
    final NoSwingHttpdSiteBindService httpdSiteBinds;
    final NoSwingHttpdSiteURLService httpdSiteURLs;
    final NoSwingHttpdSiteService httpdSites;
    final NoSwingHttpdStaticSiteService httpdStaticSites;
    final NoSwingHttpdTomcatContextService httpdTomcatContexts;
    final NoSwingHttpdTomcatDataSourceService httpdTomcatDataSources;
    final NoSwingHttpdTomcatParameterService httpdTomcatParameters;
    final NoSwingHttpdTomcatSiteService httpdTomcatSites;
    final NoSwingHttpdTomcatSharedSiteService httpdTomcatSharedSites;
    final NoSwingHttpdTomcatStdSiteService httpdTomcatStdSites;
    final NoSwingHttpdTomcatVersionService httpdTomcatVersions;
    final NoSwingHttpdWorkerService httpdWorkers;
    final NoSwingIPAddressService ipAddresss;
    */
    final NoSwingLanguageService languages;
    /* TODO
    final NoSwingLinuxAccAddressService linuxAccAddresss;
     */
    final NoSwingLinuxAccountTypeService linuxAccountTypes;
    final NoSwingLinuxAccountService linuxAccounts;
    // TODO: final NoSwingLinuxGroupAccountService linuxGroupAccounts;
    final NoSwingLinuxGroupTypeService linuxGroupTypes;
    /* TODO
    final NoSwingLinuxGroupService linuxGroups;
    final NoSwingLinuxIDService linuxIDs;
    final NoSwingLinuxServerAccountService linuxServerAccounts;
    final NoSwingLinuxServerGroupService linuxServerGroups;
    final NoSwingMajordomoListService majordomoLists;
    final NoSwingMajordomoServerService majordomoServers;
    final NoSwingMajordomoVersionService majordomoVersions;
    final NoSwingMasterHistoryService masterHistories;
    final NoSwingMasterHostService masterHosts;
    final NoSwingMasterServerService masterServers;
    final NoSwingMasterUserService masterUsers;
    final NoSwingMonthlyChargeService monthlyCharges;
     */
    final NoSwingMySQLDatabaseService mysqlDatabases;
    final NoSwingMySQLDBUserService mysqlDBUsers;
    final NoSwingMySQLReservedWordService mysqlReservedWords;
    final NoSwingMySQLServerService mysqlServers;
    final NoSwingMySQLUserService mysqlUsers;
    final NoSwingNetBindService netBinds;
    final NoSwingNetDeviceIDService netDeviceIDs;
    /* TODO
    final NoSwingNetDeviceService netDevices;
     */
    final NoSwingNetProtocolService netProtocols;
    /* TODO
    final NoSwingNetTcpRedirectService netTcpRedirects;
    final NoSwingNoticeLogService noticeLogs;
    final NoSwingNoticeTypeService noticeTypes;
    */
    final NoSwingOperatingSystemVersionService operatingSystemVersions;
    final NoSwingOperatingSystemService operatingSystems;
    final NoSwingPackageCategoryService packageCategories;
    /* TODO
    final NoSwingPackageDefinitionLimitService packageDefinitionLimits;
    final NoSwingPackageDefinitionService packageDefinitions;
    final NoSwingPaymentTypeService paymentTypes;
    final NoSwingPhysicalServerService physicalServers;
     */
    final NoSwingPostgresDatabaseService postgresDatabases;
    final NoSwingPostgresEncodingService postgresEncodings;
    final NoSwingPostgresReservedWordService postgresReservedWords;
    final NoSwingPostgresServerService postgresServers;
    final NoSwingPostgresUserService postgresUsers;
    final NoSwingPostgresVersionService postgresVersions;
    // TODO: final NoSwingPrivateFTPServerService privateFTPServers;
    // TODO: final NoSwingProcessorTypeService processorTypes;
    final NoSwingProtocolService protocols;
    // TODO: final NoSwingRackService racks;
    // TODO: final NoSwingResellerService resellers;
    final NoSwingResourceTypeService resourceTypes;
    final NoSwingResourceService resources;
    final NoSwingServerFarmService serverFarms;
    final NoSwingServerService servers;
    final NoSwingShellService shells;
    /* TODO
    final NoSwingSignupRequestOptionService signupRequestOptions;
    final NoSwingSignupRequestService signupRequests;
    final NoSwingSpamEmailMessageService spamEmailMessages;
    final NoSwingSystemEmailAliasService systemEmailAliass;
     */
    final NoSwingTechnologyService technologies;
    final NoSwingTechnologyClassService technologyClasses;
    final NoSwingTechnologyNameService technologyNames;
    final NoSwingTechnologyVersionService technologyVersions;
    /* TODO
    final NoSwingTicketActionTypeService ticketActionTypes;
    final NoSwingTicketActionService ticketActions;
    final NoSwingTicketAssignmentService ticketAssignments;
    final NoSwingTicketBrandCategoryService ticketBrandCategories;
    */
    final NoSwingTicketCategoryService ticketCategories;
    final NoSwingTicketPriorityService ticketPriorities;
    final NoSwingTicketStatusService ticketStatuses;
    final NoSwingTicketTypeService ticketTypes;
    /* TODO
    final NoSwingTicketService tickets;
    */
    final NoSwingTimeZoneService timeZones;
    /* TODO
    final NoSwingTransactionTypeService transactionTypes;
    final NoSwingTransactionService transactions;
    final NoSwingUSStateService usStates;
     */
    final NoSwingUsernameService usernames;
    /* TODO
    final NoSwingVirtualDiskService virtualDisks;
    final NoSwingVirtualServerService virtualServers;
    final NoSwingWhoisHistoryService whoisHistories;
     */

    NoSwingConnector(NoSwingConnectorFactory factory, AOServConnector<?,?> wrapped) throws RemoteException, LoginException {
        this.factory = factory;
        this.wrapped = wrapped;
        /* TODO
        aoserverDaemonHosts = new NoSwingAOServerDaemonHostService(this, wrapped.getAOServerDaemonHosts());
         */
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
        brands = new NoSwingBrandService(this, wrapped.getBrands());
         */
        businessAdministrators = new NoSwingBusinessAdministratorService(this, wrapped.getBusinessAdministrators());
        /* TODO
        businessAdministratorPermissions = new NoSwingBusinessAdministratorPermissionService(this, wrapped.getBusinessAdministratorPermissions());
        businessProfiles = new NoSwingBusinessProfileService(this, wrapped.getBusinessProfiles());
         */
        businesses = new NoSwingBusinessService(this, wrapped.getBusinesses());
        /* TODO
        businessServers = new NoSwingBusinessServerService(this, wrapped.getBusinessServers());
        clientJvmProfiles = new NoSwingClientJvmProfileService(this, wrapped.getClientJvmProfiles());
         */
        countryCodes = new NoSwingCountryCodeService(this, wrapped.getCountryCodes());
        /* TODO
        creditCardProcessors = new NoSwingCreditCardProcessorService(this, wrapped.getCreditCardProcessors());
        creditCardTransactions = new NoSwingCreditCardTransactionService(this, wrapped.getCreditCardTransactions());
        creditCards = new NoSwingCreditCardService(this, wrapped.getCreditCards());
        cvsRepositories = new NoSwingCvsRepositoryService(this, wrapped.getCvsRepositorys());
         */
        disableLogs = new NoSwingDisableLogService(this, wrapped.getDisableLogs());
        /*
        distroFileTypes = new NoSwingDistroFileTypeService(this, wrapped.getDistroFileTypes());
        distroFiles = new NoSwingDistroFileService(this, wrapped.getDistroFiles());
        dnsForbiddenZones = new NoSwingDNSForbiddenZoneService(this, wrapped.getDNSForbiddenZones());
        dnsRecords = new NoSwingDNSRecordService(this, wrapped.getDNSRecords());
        dnsTLDs = new NoSwingDNSTLDService(this, wrapped.getDNSTLDs());
        dnsTypes = new NoSwingDNSTypeService(this, wrapped.getDNSTypes());
        dnsZones = new NoSwingDNSZoneService(this, wrapped.getDNSZones());
        emailAddresss = new NoSwingEmailAddressService(this, wrapped.getEmailAddresss());
        emailAttachmentBlocks = new NoSwingEmailAttachmentBlockService(this, wrapped.getEmailAttachmentBlocks());
        emailAttachmentTypes = new NoSwingEmailAttachmentTypeService(this, wrapped.getEmailAttachmentTypes());
        emailDomains = new NoSwingEmailDomainService(this, wrapped.getEmailDomains());
        emailForwardings = new NoSwingEmailForwardingService(this, wrapped.getEmailForwardings());
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
        failoverFileLogs = new NoSwingFailoverFileLogService(this, wrapped.getFailoverFileLogs());
         */
        failoverFileReplications = new NoSwingFailoverFileReplicationService(this, wrapped.getFailoverFileReplications());
        /* TODO
        failoverFileSchedules = new NoSwingFailoverFileScheduleService(this, wrapped.getFailoverFileSchedules());
         */
        failoverMySQLReplications = new NoSwingFailoverMySQLReplicationService(this, wrapped.getFailoverMySQLReplications());
        /* TODO
        fileBackupSettings = new NoSwingFileBackupSettingService(this, wrapped.getFileBackupSettings());
        ftpGuestUsers = new NoSwingFTPGuestUserService(this, wrapped.getFTPGuestUsers());
        httpdBinds = new NoSwingHttpdBindService(this, wrapped.getHttpdBinds());
        httpdJBossSites = new NoSwingHttpdJBossSiteService(this, wrapped.getHttpdJBossSites());
        httpdJBossVersions = new NoSwingHttpdJBossVersionService(this, wrapped.getHttpdJBossVersions());
        httpdJKCodes = new NoSwingHttpdJKCodeService(this, wrapped.getHttpdJKCodes());
        httpdJKProtocols = new NoSwingHttpdJKProtocolService(this, wrapped.getHttpdJKProtocols());
        httpdServers = new NoSwingHttpdServerService(this, wrapped.getHttpdServers());
        httpdSharedTomcats = new NoSwingHttpdSharedTomcatService(this, wrapped.getHttpdSharedTomcats());
        httpdSiteAuthenticatedLocations = new NoSwingHttpdSiteAuthenticatedLocationService(this, wrapped.getHttpdSiteAuthenticatedLocations());
        httpdSiteBinds = new NoSwingHttpdSiteBindService(this, wrapped.getHttpdSiteBinds());
        httpdSiteURLs = new NoSwingHttpdSiteURLService(this, wrapped.getHttpdSiteURLs());
        httpdSites = new NoSwingHttpdSiteService(this, wrapped.getHttpdSites());
        httpdStaticSites = new NoSwingHttpdStaticSiteService(this, wrapped.getHttpdStaticSites());
        httpdTomcatContexts = new NoSwingHttpdTomcatContextService(this, wrapped.getHttpdTomcatContexts());
        httpdTomcatDataSources = new NoSwingHttpdTomcatDataSourceService(this, wrapped.getHttpdTomcatDataSources());
        httpdTomcatParameters = new NoSwingHttpdTomcatParameterService(this, wrapped.getHttpdTomcatParameters());
        httpdTomcatSites = new NoSwingHttpdTomcatSiteService(this, wrapped.getHttpdTomcatSites());
        httpdTomcatSharedSites = new NoSwingHttpdTomcatSharedSiteService(this, wrapped.getHttpdTomcatSharedSites());
        httpdTomcatStdSites = new NoSwingHttpdTomcatStdSiteService(this, wrapped.getHttpdTomcatStdSites());
        httpdTomcatVersions = new NoSwingHttpdTomcatVersionService(this, wrapped.getHttpdTomcatVersions());
        httpdWorkers = new NoSwingHttpdWorkerService(this, wrapped.getHttpdWorkers());
        ipAddresss = new NoSwingIPAddressService(this, wrapped.getIPAddresss());
        */
        languages = new NoSwingLanguageService(this, wrapped.getLanguages());
        // TODO: linuxAccAddresss = new NoSwingLinuxAccAddressService(this, wrapped.getLinuxAccAddresss());
        linuxAccountTypes = new NoSwingLinuxAccountTypeService(this, wrapped.getLinuxAccountTypes());
        linuxAccounts = new NoSwingLinuxAccountService(this, wrapped.getLinuxAccounts());
        // TODO: linuxGroupAccounts = new NoSwingLinuxGroupAccountService(this, wrapped.getLinuxGroupAccounts());
        linuxGroupTypes = new NoSwingLinuxGroupTypeService(this, wrapped.getLinuxGroupTypes());
        /* TODO
        linuxGroups = new NoSwingLinuxGroupService(this, wrapped.getLinuxGroups());
        linuxIDs = new NoSwingLinuxIDService(this, wrapped.getLinuxIDs());
        linuxServerAccounts = new NoSwingLinuxServerAccountService(this, wrapped.getLinuxServerAccounts());
        linuxServerGroups = new NoSwingLinuxServerGroupService(this, wrapped.getLinuxServerGroups());
        majordomoLists = new NoSwingMajordomoListService(this, wrapped.getMajordomoLists());
        majordomoServers = new NoSwingMajordomoServerService(this, wrapped.getMajordomoServers());
        majordomoVersions = new NoSwingMajordomoVersionService(this, wrapped.getMajordomoVersions());
        masterHistories = new NoSwingMasterHistoryService(this, wrapped.getMasterHistorys());
        masterHosts = new NoSwingMasterHostService(this, wrapped.getMasterHosts());
        masterServers = new NoSwingMasterServerService(this, wrapped.getMasterServers());
        masterUsers = new NoSwingMasterUserService(this, wrapped.getMasterUsers());
        monthlyCharges = new NoSwingMonthlyChargeService(this, wrapped.getMonthlyCharges());
         */
        mysqlDatabases = new NoSwingMySQLDatabaseService(this, wrapped.getMysqlDatabases());
        mysqlDBUsers = new NoSwingMySQLDBUserService(this, wrapped.getMysqlDBUsers());
        mysqlReservedWords = new NoSwingMySQLReservedWordService(this, wrapped.getMysqlReservedWords());
        mysqlServers = new NoSwingMySQLServerService(this, wrapped.getMysqlServers());
        mysqlUsers = new NoSwingMySQLUserService(this, wrapped.getMysqlUsers());
        netBinds = new NoSwingNetBindService(this, wrapped.getNetBinds());
        netDeviceIDs = new NoSwingNetDeviceIDService(this, wrapped.getNetDeviceIDs());
        /* TODO
        netDevices = new NoSwingNetDeviceService(this, wrapped.getNetDevices());
         */
        netProtocols = new NoSwingNetProtocolService(this, wrapped.getNetProtocols());
        /* TODO
        netTcpRedirects = new NoSwingNetTcpRedirectService(this, wrapped.getNetTcpRedirects());
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
        postgresReservedWords = new NoSwingPostgresReservedWordService(this, wrapped.getPostgresReservedWords());
        postgresServers = new NoSwingPostgresServerService(this, wrapped.getPostgresServers());
        postgresUsers = new NoSwingPostgresUserService(this, wrapped.getPostgresUsers());
        postgresVersions = new NoSwingPostgresVersionService(this, wrapped.getPostgresVersions());
        // TODO: privateFTPServers = new NoSwingPrivateFTPServerService(this, wrapped.getPrivateFTPServers());
        // TODO: processorTypes = new NoSwingProcessorTypeService(this, wrapped.getProcessorTypes());
        protocols = new NoSwingProtocolService(this, wrapped.getProtocols());
        // TODO: racks = new NoSwingRackService(this, wrapped.getRacks());
        // TODO: resellers = new NoSwingResellerService(this, wrapped.getResellers());
        resourceTypes = new NoSwingResourceTypeService(this, wrapped.getResourceTypes());
        resources = new NoSwingResourceService(this, wrapped.getResources());
        serverFarms = new NoSwingServerFarmService(this, wrapped.getServerFarms());
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
        /* TODO
        ticketActionTypes = new NoSwingTicketActionTypeService(this, wrapped.getTicketActionTypes());
        ticketActions = new NoSwingTicketActionService(this, wrapped.getTicketActions());
        ticketAssignments = new NoSwingTicketAssignmentService(this, wrapped.getTicketAssignments());
        ticketBrandCategories = new NoSwingTicketBrandCategoryService(this, wrapped.getTicketBrandCategorys());
        */
        ticketCategories = new NoSwingTicketCategoryService(this, wrapped.getTicketCategories());
        ticketPriorities = new NoSwingTicketPriorityService(this, wrapped.getTicketPriorities());
        ticketStatuses = new NoSwingTicketStatusService(this, wrapped.getTicketStatuses());
        ticketTypes = new NoSwingTicketTypeService(this, wrapped.getTicketTypes());
        /* TODO
        tickets = new NoSwingTicketService(this, wrapped.getTickets());
        */
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

    public String getConnectAs() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return wrapped.getConnectAs();
    }

    public BusinessAdministrator getThisBusinessAdministrator() throws RemoteException {
        String connectAs = getConnectAs();
        BusinessAdministrator obj = getBusinessAdministrators().get(connectAs);
        if(obj==null) throw new RemoteException("Unable to find BusinessAdministrator: "+connectAs);
        return obj;
    }

    public String getAuthenticateAs() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return wrapped.getAuthenticateAs();
    }

    public String getPassword() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return wrapped.getPassword();
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

    /*
     * TODO
    public AOServerDaemonHostService<NoSwingConnector,NoSwingConnectorFactory> getAoServerDaemonHosts() throws RemoteException;
    */
    public AOServerResourceService<NoSwingConnector,NoSwingConnectorFactory> getAoServerResources() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return aoserverResources;
    }

    public AOServerService<NoSwingConnector,NoSwingConnectorFactory> getAoServers() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return aoservers;
    }

    public AOServPermissionService<NoSwingConnector,NoSwingConnectorFactory> getAoservPermissions() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return aoservPermissions;
    }
    /* TODO
    public AOServProtocolService<NoSwingConnector,NoSwingConnectorFactory> getAoservProtocols() throws RemoteException;

    public AOSHCommandService<NoSwingConnector,NoSwingConnectorFactory> getAoshCommands() throws RemoteException;
    */
    public ArchitectureService<NoSwingConnector,NoSwingConnectorFactory> getArchitectures() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return architectures;
    }

    public BackupPartitionService<NoSwingConnector,NoSwingConnectorFactory> getBackupPartitions() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return backupPartitions;
    }

    public BackupRetentionService<NoSwingConnector,NoSwingConnectorFactory> getBackupRetentions() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return backupRetentions;
    }
    /* TODO
    public BankAccountService<NoSwingConnector,NoSwingConnectorFactory> getBankAccounts() throws RemoteException;

    public BankTransactionTypeService<NoSwingConnector,NoSwingConnectorFactory> getBankTransactionTypes() throws RemoteException;

    public BankTransactionService<NoSwingConnector,NoSwingConnectorFactory> getBankTransactions() throws RemoteException;

    public BankService<NoSwingConnector,NoSwingConnectorFactory> getBanks() throws RemoteException;

    public BlackholeEmailAddressService<NoSwingConnector,NoSwingConnectorFactory> getBlackholeEmailAddresses() throws RemoteException;

    public BrandService<NoSwingConnector,NoSwingConnectorFactory> getBrands() throws RemoteException;
     */
    public BusinessAdministratorService<NoSwingConnector,NoSwingConnectorFactory> getBusinessAdministrators() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return businessAdministrators;
    }
    /*
    public BusinessAdministratorPermissionService<NoSwingConnector,NoSwingConnectorFactory> getBusinessAdministratorPermissions() throws RemoteException;

    public BusinessProfileService<NoSwingConnector,NoSwingConnectorFactory> getBusinessProfiles() throws RemoteException;
     */
    public BusinessService<NoSwingConnector,NoSwingConnectorFactory> getBusinesses() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return businesses;
    }

    /* TODO
    public BusinessServerService<NoSwingConnector,NoSwingConnectorFactory> getBusinessServers() throws RemoteException;

    public ClientJvmProfileService<NoSwingConnector,NoSwingConnectorFactory> getClientJvmProfiles() throws RemoteException;
    */
    public CountryCodeService<NoSwingConnector,NoSwingConnectorFactory> getCountryCodes() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return countryCodes;
    }
    /* TODO
    public CreditCardProcessorService<NoSwingConnector,NoSwingConnectorFactory> getCreditCardProcessors() throws RemoteException;

    public CreditCardTransactionService<NoSwingConnector,NoSwingConnectorFactory> getCreditCardTransactions() throws RemoteException;

    public CreditCardService<NoSwingConnector,NoSwingConnectorFactory> getCreditCards() throws RemoteException;

    public CvsRepositoryService<NoSwingConnector,NoSwingConnectorFactory> getCvsRepositories() throws RemoteException;
     */
    public DisableLogService<NoSwingConnector,NoSwingConnectorFactory> getDisableLogs() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return disableLogs;
    }
    /*
    public DistroFileTypeService<NoSwingConnector,NoSwingConnectorFactory> getDistroFileTypes() throws RemoteException;

    public DistroFileService<NoSwingConnector,NoSwingConnectorFactory> getDistroFiles() throws RemoteException;

    public DNSForbiddenZoneService<NoSwingConnector,NoSwingConnectorFactory> getDnsForbiddenZones() throws RemoteException;

    public DNSRecordService<NoSwingConnector,NoSwingConnectorFactory> getDnsRecords() throws RemoteException;

    public DNSTLDService<NoSwingConnector,NoSwingConnectorFactory> getDnsTLDs() throws RemoteException;

    public DNSTypeService<NoSwingConnector,NoSwingConnectorFactory> getDnsTypes() throws RemoteException;

    public DNSZoneService<NoSwingConnector,NoSwingConnectorFactory> getDnsZones() throws RemoteException;

    public EmailAddressService<NoSwingConnector,NoSwingConnectorFactory> getEmailAddresses() throws RemoteException;

    public EmailAttachmentBlockService<NoSwingConnector,NoSwingConnectorFactory> getEmailAttachmentBlocks() throws RemoteException;

    public EmailAttachmentTypeService<NoSwingConnector,NoSwingConnectorFactory> getEmailAttachmentTypes() throws RemoteException;

    public EmailDomainService<NoSwingConnector,NoSwingConnectorFactory> getEmailDomains() throws RemoteException;

    public EmailForwardingService<NoSwingConnector,NoSwingConnectorFactory> getEmailForwardings() throws RemoteException;

    public EmailListAddressService<NoSwingConnector,NoSwingConnectorFactory> getEmailListAddresses() throws RemoteException;

    public EmailListService<NoSwingConnector,NoSwingConnectorFactory> getEmailLists() throws RemoteException;

    public EmailPipeAddressService<NoSwingConnector,NoSwingConnectorFactory> getEmailPipeAddresses() throws RemoteException;

    public EmailPipeService<NoSwingConnector,NoSwingConnectorFactory> getEmailPipes() throws RemoteException;

    public EmailSmtpRelayTypeService<NoSwingConnector,NoSwingConnectorFactory> getEmailSmtpRelayTypes() throws RemoteException;

    public EmailSmtpRelayService<NoSwingConnector,NoSwingConnectorFactory> getEmailSmtpRelays() throws RemoteException;

    public EmailSmtpSmartHostDomainService<NoSwingConnector,NoSwingConnectorFactory> getEmailSmtpSmartHostDomains() throws RemoteException;

    public EmailSmtpSmartHostService<NoSwingConnector,NoSwingConnectorFactory> getEmailSmtpSmartHosts() throws RemoteException;

    public EmailSpamAssassinIntegrationModeService<NoSwingConnector,NoSwingConnectorFactory> getEmailSpamAssassinIntegrationModes() throws RemoteException;

    public EncryptionKeyService<NoSwingConnector,NoSwingConnectorFactory> getEncryptionKeys() throws RemoteException;

    public ExpenseCategoryService<NoSwingConnector,NoSwingConnectorFactory> getExpenseCategories() throws RemoteException;

    public FailoverFileLogService<NoSwingConnector,NoSwingConnectorFactory> getFailoverFileLogs() throws RemoteException;
    */
    public FailoverFileReplicationService<NoSwingConnector,NoSwingConnectorFactory> getFailoverFileReplications() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return failoverFileReplications;
    }
    /* TODO
    public FailoverFileScheduleService<NoSwingConnector,NoSwingConnectorFactory> getFailoverFileSchedules() throws RemoteException;
    */
    public FailoverMySQLReplicationService<NoSwingConnector,NoSwingConnectorFactory> getFailoverMySQLReplications() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return failoverMySQLReplications;
    }
    /* TODO
    public FileBackupSettingService<NoSwingConnector,NoSwingConnectorFactory> getFileBackupSettings() throws RemoteException;

    public FTPGuestUserService<NoSwingConnector,NoSwingConnectorFactory> getFtpGuestUsers() throws RemoteException;

    public HttpdBindService<NoSwingConnector,NoSwingConnectorFactory> getHttpdBinds() throws RemoteException;

    public HttpdJBossSiteService<NoSwingConnector,NoSwingConnectorFactory> getHttpdJBossSites() throws RemoteException;

    public HttpdJBossVersionService<NoSwingConnector,NoSwingConnectorFactory> getHttpdJBossVersions() throws RemoteException;

    public HttpdJKCodeService<NoSwingConnector,NoSwingConnectorFactory> getHttpdJKCodes() throws RemoteException;

    public HttpdJKProtocolService<NoSwingConnector,NoSwingConnectorFactory> getHttpdJKProtocols() throws RemoteException;

    public HttpdServerService<NoSwingConnector,NoSwingConnectorFactory> getHttpdServers() throws RemoteException;

    public HttpdSharedTomcatService<NoSwingConnector,NoSwingConnectorFactory> getHttpdSharedTomcats() throws RemoteException;

    public HttpdSiteAuthenticatedLocationService<NoSwingConnector,NoSwingConnectorFactory> getHttpdSiteAuthenticatedLocations() throws RemoteException;

    public HttpdSiteBindService<NoSwingConnector,NoSwingConnectorFactory> getHttpdSiteBinds() throws RemoteException;

    public HttpdSiteURLService<NoSwingConnector,NoSwingConnectorFactory> getHttpdSiteURLs() throws RemoteException;

    public HttpdSiteService<NoSwingConnector,NoSwingConnectorFactory> getHttpdSites() throws RemoteException;

    public HttpdStaticSiteService<NoSwingConnector,NoSwingConnectorFactory> getHttpdStaticSites() throws RemoteException;

    public HttpdTomcatContextService<NoSwingConnector,NoSwingConnectorFactory> getHttpdTomcatContexts() throws RemoteException;

    public HttpdTomcatDataSourceService<NoSwingConnector,NoSwingConnectorFactory> getHttpdTomcatDataSources() throws RemoteException;

    public HttpdTomcatParameterService<NoSwingConnector,NoSwingConnectorFactory> getHttpdTomcatParameters() throws RemoteException;

    public HttpdTomcatSiteService<NoSwingConnector,NoSwingConnectorFactory> getHttpdTomcatSites() throws RemoteException;

    public HttpdTomcatSharedSiteService<NoSwingConnector,NoSwingConnectorFactory> getHttpdTomcatSharedSites() throws RemoteException;

    public HttpdTomcatStdSiteService<NoSwingConnector,NoSwingConnectorFactory> getHttpdTomcatStdSites() throws RemoteException;

    public HttpdTomcatVersionService<NoSwingConnector,NoSwingConnectorFactory> getHttpdTomcatVersions() throws RemoteException;

    public HttpdWorkerService<NoSwingConnector,NoSwingConnectorFactory> getHttpdWorkers() throws RemoteException;

    public IPAddressService<NoSwingConnector,NoSwingConnectorFactory> getIpAddresses() throws RemoteException;
    */
    public LanguageService<NoSwingConnector,NoSwingConnectorFactory> getLanguages() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return languages;
    }

    // TODO: public LinuxAccAddressService<NoSwingConnector,NoSwingConnectorFactory> getLinuxAccAddresses() throws RemoteException;

    public LinuxAccountTypeService<NoSwingConnector,NoSwingConnectorFactory> getLinuxAccountTypes() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return linuxAccountTypes;
    }

    public LinuxAccountService<NoSwingConnector,NoSwingConnectorFactory> getLinuxAccounts() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return linuxAccounts;
    }

    // TODO: public LinuxGroupAccountService<NoSwingConnector,NoSwingConnectorFactory> getLinuxGroupAccounts() throws RemoteException;

    public LinuxGroupTypeService<NoSwingConnector,NoSwingConnectorFactory> getLinuxGroupTypes() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return linuxGroupTypes;
    }
    /* TODO
    public LinuxGroupService<NoSwingConnector,NoSwingConnectorFactory> getLinuxGroups() throws RemoteException;

    public LinuxIDService<NoSwingConnector,NoSwingConnectorFactory> getLinuxIDs() throws RemoteException;

    public LinuxServerAccountService<NoSwingConnector,NoSwingConnectorFactory> getLinuxServerAccounts() throws RemoteException;

    public LinuxServerGroupService<NoSwingConnector,NoSwingConnectorFactory> getLinuxServerGroups() throws RemoteException;

    public MajordomoListService<NoSwingConnector,NoSwingConnectorFactory> getMajordomoLists() throws RemoteException;

    public MajordomoServerService<NoSwingConnector,NoSwingConnectorFactory> getMajordomoServers() throws RemoteException;

    public MajordomoVersionService<NoSwingConnector,NoSwingConnectorFactory> getMajordomoVersions() throws RemoteException;

    public MasterHistoryService<NoSwingConnector,NoSwingConnectorFactory> getMasterHistory() throws RemoteException;

    public MasterHostService<NoSwingConnector,NoSwingConnectorFactory> getMasterHosts() throws RemoteException;

    public MasterServerService<NoSwingConnector,NoSwingConnectorFactory> getMasterServers() throws RemoteException;

    public MasterUserService<NoSwingConnector,NoSwingConnectorFactory> getMasterUsers() throws RemoteException;

    public MonthlyChargeService<NoSwingConnector,NoSwingConnectorFactory> getMonthlyCharges() throws RemoteException;
    */
    public MySQLDatabaseService<NoSwingConnector,NoSwingConnectorFactory> getMysqlDatabases() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return mysqlDatabases;
    }

    public MySQLDBUserService<NoSwingConnector,NoSwingConnectorFactory> getMysqlDBUsers() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return mysqlDBUsers;
    }

    public MySQLReservedWordService<NoSwingConnector,NoSwingConnectorFactory> getMysqlReservedWords() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return mysqlReservedWords;
    }

    public MySQLServerService<NoSwingConnector,NoSwingConnectorFactory> getMysqlServers() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return mysqlServers;
    }

    public MySQLUserService<NoSwingConnector,NoSwingConnectorFactory> getMysqlUsers() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return mysqlUsers;
    }

    public NetBindService<NoSwingConnector,NoSwingConnectorFactory> getNetBinds() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return netBinds;
    }

    public NetDeviceIDService<NoSwingConnector,NoSwingConnectorFactory> getNetDeviceIDs() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return netDeviceIDs;
    }
    /* TODO
    public NetDeviceService<NoSwingConnector,NoSwingConnectorFactory> getNetDevices() throws RemoteException;
    */
    public NetProtocolService<NoSwingConnector,NoSwingConnectorFactory> getNetProtocols() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return netProtocols;
    }
    /* TODO
    public NetTcpRedirectService<NoSwingConnector,NoSwingConnectorFactory> getNetTcpRedirects() throws RemoteException;

    public NoticeLogService<NoSwingConnector,NoSwingConnectorFactory> getNoticeLogs() throws RemoteException;

    public NoticeTypeService<NoSwingConnector,NoSwingConnectorFactory> getNoticeTypes() throws RemoteException;
    */
    public OperatingSystemVersionService<NoSwingConnector,NoSwingConnectorFactory> getOperatingSystemVersions() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return operatingSystemVersions;
    }

    public OperatingSystemService<NoSwingConnector,NoSwingConnectorFactory> getOperatingSystems() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return operatingSystems;
    }

    public PackageCategoryService<NoSwingConnector,NoSwingConnectorFactory> getPackageCategories() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return packageCategories;
    }
    /*
    public PackageDefinitionLimitService<NoSwingConnector,NoSwingConnectorFactory> getPackageDefinitionLimits() throws RemoteException;

    public PackageDefinitionService<NoSwingConnector,NoSwingConnectorFactory> getPackageDefinitions() throws RemoteException;

    public PaymentTypeService<NoSwingConnector,NoSwingConnectorFactory> getPaymentTypes() throws RemoteException;

    public PhysicalServerService<NoSwingConnector,NoSwingConnectorFactory> getPhysicalServers() throws RemoteException;
    */
    public PostgresDatabaseService<NoSwingConnector,NoSwingConnectorFactory> getPostgresDatabases() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return postgresDatabases;
    }

    public PostgresEncodingService<NoSwingConnector,NoSwingConnectorFactory> getPostgresEncodings() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return postgresEncodings;
    }

    public PostgresReservedWordService<NoSwingConnector,NoSwingConnectorFactory> getPostgresReservedWords() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return postgresReservedWords;
    }

    public PostgresServerService<NoSwingConnector,NoSwingConnectorFactory> getPostgresServers() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return postgresServers;
    }

    public PostgresUserService<NoSwingConnector,NoSwingConnectorFactory> getPostgresUsers() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return postgresUsers;
    }

    public PostgresVersionService<NoSwingConnector,NoSwingConnectorFactory> getPostgresVersions() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return postgresVersions;
    }

    // TODO: public PrivateFTPServerService<NoSwingConnector,NoSwingConnectorFactory> getPrivateFTPServers() throws RemoteException;

    // TODO: public ProcessorTypeService<NoSwingConnector,NoSwingConnectorFactory> getProcessorTypes() throws RemoteException;

    public ProtocolService<NoSwingConnector,NoSwingConnectorFactory> getProtocols() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return protocols;
    }
    /* TODO
    public RackService<NoSwingConnector,NoSwingConnectorFactory> getRacks() throws RemoteException;

    public ResellerService<NoSwingConnector,NoSwingConnectorFactory> getResellers() throws RemoteException;
    */
    public ResourceTypeService<NoSwingConnector,NoSwingConnectorFactory> getResourceTypes() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return resourceTypes;
    }

    public ResourceService<NoSwingConnector,NoSwingConnectorFactory> getResources() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return resources;
    }

    public ServerFarmService<NoSwingConnector,NoSwingConnectorFactory> getServerFarms() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return serverFarms;
    }

    public ServerService<NoSwingConnector,NoSwingConnectorFactory> getServers() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return servers;
    }

    public ShellService<NoSwingConnector,NoSwingConnectorFactory> getShells() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return shells;
    }
    /* TODO
    public SignupRequestOptionService<NoSwingConnector,NoSwingConnectorFactory> getSignupRequestOptions() throws RemoteException;

    public SignupRequestService<NoSwingConnector,NoSwingConnectorFactory> getSignupRequests() throws RemoteException;

    public SpamEmailMessageService<NoSwingConnector,NoSwingConnectorFactory> getSpamEmailMessages() throws RemoteException;

    public SystemEmailAliasService<NoSwingConnector,NoSwingConnectorFactory> getSystemEmailAliases() throws RemoteException;
    */
    public TechnologyService<NoSwingConnector,NoSwingConnectorFactory> getTechnologies() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return technologies;
    }

    public TechnologyClassService<NoSwingConnector,NoSwingConnectorFactory> getTechnologyClasses() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return technologyClasses;
    }

    public TechnologyNameService<NoSwingConnector,NoSwingConnectorFactory> getTechnologyNames() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return technologyNames;
    }

    public TechnologyVersionService<NoSwingConnector,NoSwingConnectorFactory> getTechnologyVersions() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return technologyVersions;
    }
    /* TODO
    public TicketActionTypeService<NoSwingConnector,NoSwingConnectorFactory> getTicketActionTypes() throws RemoteException;

    public TicketActionService<NoSwingConnector,NoSwingConnectorFactory> getTicketActions() throws RemoteException;

    public TicketAssignmentService<NoSwingConnector,NoSwingConnectorFactory> getTicketAssignments() throws RemoteException;

    public TicketBrandCategoryService<NoSwingConnector,NoSwingConnectorFactory> getTicketBrandCategories() throws RemoteException;
    */
    public TicketCategoryService<NoSwingConnector,NoSwingConnectorFactory> getTicketCategories() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return ticketCategories;
    }

    public TicketPriorityService<NoSwingConnector,NoSwingConnectorFactory> getTicketPriorities() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return ticketPriorities;
    }

    public TicketStatusService<NoSwingConnector,NoSwingConnectorFactory> getTicketStatuses() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return ticketStatuses;
    }

    public TicketTypeService<NoSwingConnector,NoSwingConnectorFactory> getTicketTypes() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return ticketTypes;
    }
    /* TODO
    public TicketService<NoSwingConnector,NoSwingConnectorFactory> getTickets() throws RemoteException;
    */
    public TimeZoneService<NoSwingConnector,NoSwingConnectorFactory> getTimeZones() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return timeZones;
    }
    /* TODO
    public TransactionTypeService<NoSwingConnector,NoSwingConnectorFactory> getTransactionTypes() throws RemoteException;

    public TransactionService<NoSwingConnector,NoSwingConnectorFactory> getTransactions() throws RemoteException;

    public USStateService<NoSwingConnector,NoSwingConnectorFactory> getUsStates() throws RemoteException;
    */
    public UsernameService<NoSwingConnector,NoSwingConnectorFactory> getUsernames() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return usernames;
    }
    /* TODO
    public VirtualDiskService<NoSwingConnector,NoSwingConnectorFactory> getVirtualDisks() throws RemoteException;

    public VirtualServerService<NoSwingConnector,NoSwingConnectorFactory> getVirtualServers() throws RemoteException;

    public WhoisHistoryService<NoSwingConnector,NoSwingConnectorFactory> getWhoisHistory() throws RemoteException;
 */
}
