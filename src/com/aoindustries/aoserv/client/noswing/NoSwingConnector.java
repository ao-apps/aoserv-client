package com.aoindustries.aoserv.client.noswing;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.AOServConnectorUtils;
import com.aoindustries.aoserv.client.AOServService;
import com.aoindustries.aoserv.client.BusinessAdministrator;
import com.aoindustries.aoserv.client.BusinessAdministratorService;
import com.aoindustries.aoserv.client.BusinessService;
import com.aoindustries.aoserv.client.DisableLogService;
import com.aoindustries.aoserv.client.LanguageService;
import com.aoindustries.aoserv.client.PackageCategoryService;
import com.aoindustries.aoserv.client.ResourceTypeService;
import com.aoindustries.aoserv.client.ServiceName;
import com.aoindustries.aoserv.client.TicketCategoryService;
import com.aoindustries.aoserv.client.TicketPriorityService;
import com.aoindustries.aoserv.client.TicketStatusService;
import com.aoindustries.aoserv.client.TicketTypeService;
import com.aoindustries.aoserv.client.TimeZoneService;
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
    final NoSwingAOServerResourceService aoserverResources;
    final NoSwingAOServerService aoservers;
    final NoSwingAOServPermissionService aoservPermissions;
    final NoSwingAOServProtocolService aoservProtocols;
    final NoSwingAOSHCommandService aoshCommands;
    final NoSwingArchitectureService architectures;
    final NoSwingBackupPartitionService backupPartitions;
    final NoSwingBackupRetentionService backupRetentions;
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
    final NoSwingCountryCodeService countryCodes;
    final NoSwingCreditCardProcessorService creditCardProcessors;
    final NoSwingCreditCardTransactionService creditCardTransactions;
    final NoSwingCreditCardService creditCards;
    final NoSwingCvsRepositoryService cvsRepositories;
     */
    final NoSwingDisableLogService disabledLogs;
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
    final NoSwingFailoverFileReplicationService failoverFileReplications;
    final NoSwingFailoverFileScheduleService failoverFileSchedules;
    final NoSwingFailoverMySQLReplicationService failoverMySQLReplications;
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
    final NoSwingLinuxAccountTypeService linuxAccountTypes;
    final NoSwingLinuxAccountService linuxAccounts;
    final NoSwingLinuxGroupAccountService linuxGroupAccounts;
    final NoSwingLinuxGroupTypeService linuxGroupTypes;
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
    final NoSwingMySQLDatabaseService mysqlDatabases;
    final NoSwingMySQLDBUserService mysqlDBUsers;
    final NoSwingMySQLReservedWordService mysqlReservedWords;
    final NoSwingMySQLServerService mysqlServers;
    final NoSwingMySQLUserService mysqlUsers;
    final NoSwingNetBindService netBinds;
    final NoSwingNetDeviceIDService netDeviceIDs;
    final NoSwingNetDeviceService netDevices;
    final NoSwingNetPortService netPorts;
    final NoSwingNetProtocolService netProtocols;
    final NoSwingNetTcpRedirectService netTcpRedirects;
    final NoSwingNoticeLogService noticeLogs;
    final NoSwingNoticeTypeService noticeTypes;
    final NoSwingOperatingSystemVersionService operatingSystemVersions;
    final NoSwingOperatingSystemService operatingSystems;
    */
    final NoSwingPackageCategoryService packageCategories;
    /* TODO
    final NoSwingPackageDefinitionLimitService packageDefinitionLimits;
    final NoSwingPackageDefinitionService packageDefinitions;
    final NoSwingPaymentTypeService paymentTypes;
    final NoSwingPhysicalServerService physicalServers;
    final NoSwingPostgresDatabaseService postgresDatabases;
    final NoSwingPostgresEncodingService postgresEncodings;
    final NoSwingPostgresReservedWordService postgresReservedWords;
    final NoSwingPostgresServerUserService postgresServerUsers;
    final NoSwingPostgresServerService postgresServers;
    final NoSwingPostgresUserService postgresUsers;
    final NoSwingPostgresVersionService postgresVersions;
    final NoSwingPrivateFTPServerService privateFTPServers;
    final NoSwingProcessorTypeService processorTypes;
    final NoSwingProtocolService protocols;
    final NoSwingRackService racks;
    final NoSwingResellerService resellers;
     */
    final NoSwingResourceTypeService resourceTypes;
    /* TODO
    final NoSwingResourceService resources;
    final NoSwingServerFarmService serverFarms;
    final NoSwingServerService servers;
    final NoSwingShellService shells;
    final NoSwingSignupRequestOptionService signupRequestOptions;
    final NoSwingSignupRequestService signupRequests;
    final NoSwingSpamEmailMessageService spamEmailMessages;
    final NoSwingSystemEmailAliasService systemEmailAliass;
    final NoSwingTechnologyService technologies;
    final NoSwingTechnologyClassService technologyClasss;
    final NoSwingTechnologyNameService technologyNames;
    final NoSwingTechnologyVersionService technologyVersions;
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
    final NoSwingUsernameService usernames;
    final NoSwingVirtualDiskService virtualDisks;
    final NoSwingVirtualServerService virtualServers;
    final NoSwingWhoisHistoryService whoisHistories;
     */

    NoSwingConnector(NoSwingConnectorFactory factory, AOServConnector<?,?> wrapped) throws RemoteException, LoginException {
        this.factory = factory;
        this.wrapped = wrapped;
        /* TODO
        aoserverDaemonHosts = new NoSwingAOServerDaemonHostService(this, wrapped.getAOServerDaemonHosts());
        aoserverResources = new NoSwingAOServerResourceService(this, wrapped.getAOServerResources());
        aoservers = new NoSwingAOServerService(this, wrapped.getAOServers());
        aoservPermissions = new NoSwingAOServPermissionService(this, wrapped.getAOServPermissions());
        aoservProtocols = new NoSwingAOServProtocolService(this, wrapped.getAOServProtocols());
        aoshCommands = new NoSwingAOSHCommandService(this, wrapped.getAOSHCommands());
        architectures = new NoSwingArchitectureService(this, wrapped.getArchitectures());
        backupPartitions = new NoSwingBackupPartitionService(this, wrapped.getBackupPartitions());
        backupRetentions = new NoSwingBackupRetentionService(this, wrapped.getBackupRetentions());
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
        countryCodes = new NoSwingCountryCodeService(this, wrapped.getCountryCodes());
        creditCardProcessors = new NoSwingCreditCardProcessorService(this, wrapped.getCreditCardProcessors());
        creditCardTransactions = new NoSwingCreditCardTransactionService(this, wrapped.getCreditCardTransactions());
        creditCards = new NoSwingCreditCardService(this, wrapped.getCreditCards());
        cvsRepositories = new NoSwingCvsRepositoryService(this, wrapped.getCvsRepositorys());
         */
        disabledLogs = new NoSwingDisableLogService(this, wrapped.getDisableLogs());
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
        failoverFileReplications = new NoSwingFailoverFileReplicationService(this, wrapped.getFailoverFileReplications());
        failoverFileSchedules = new NoSwingFailoverFileScheduleService(this, wrapped.getFailoverFileSchedules());
        failoverMySQLReplications = new NoSwingFailoverMySQLReplicationService(this, wrapped.getFailoverMySQLReplications());
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
        /* TODO
        linuxAccAddresss = new NoSwingLinuxAccAddressService(this, wrapped.getLinuxAccAddresss());
        linuxAccountTypes = new NoSwingLinuxAccountTypeService(this, wrapped.getLinuxAccountTypes());
        linuxAccounts = new NoSwingLinuxAccountService(this, wrapped.getLinuxAccounts());
        linuxGroupAccounts = new NoSwingLinuxGroupAccountService(this, wrapped.getLinuxGroupAccounts());
        linuxGroupTypes = new NoSwingLinuxGroupTypeService(this, wrapped.getLinuxGroupTypes());
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
        mysqlDatabases = new NoSwingMySQLDatabaseService(this, wrapped.getMySQLDatabases());
        mysqlDBUsers = new NoSwingMySQLDBUserService(this, wrapped.getMySQLDBUsers());
        mysqlReservedWords = new NoSwingMySQLReservedWordService(this, wrapped.getMySQLReservedWords());
        mysqlServers = new NoSwingMySQLServerService(this, wrapped.getMySQLServers());
        mysqlUsers = new NoSwingMySQLUserService(this, wrapped.getMySQLUsers());
        netBinds = new NoSwingNetBindService(this, wrapped.getNetBinds());
        netDeviceIDs = new NoSwingNetDeviceIDService(this, wrapped.getNetDeviceIDs());
        netDevices = new NoSwingNetDeviceService(this, wrapped.getNetDevices());
        netPorts = new NoSwingNetPortService(this, wrapped.getNetPorts());
        netProtocols = new NoSwingNetProtocolService(this, wrapped.getNetProtocols());
        netTcpRedirects = new NoSwingNetTcpRedirectService(this, wrapped.getNetTcpRedirects());
        noticeLogs = new NoSwingNoticeLogService(this, wrapped.getNoticeLogs());
        noticeTypes = new NoSwingNoticeTypeService(this, wrapped.getNoticeTypes());
        operatingSystemVersions = new NoSwingOperatingSystemVersionService(this, wrapped.getOperatingSystemVersions());
        operatingSystems = new NoSwingOperatingSystemService(this, wrapped.getOperatingSystems());
        */
        packageCategories = new NoSwingPackageCategoryService(this, wrapped.getPackageCategories());
        /* TODO
        packageDefinitionLimits = new NoSwingPackageDefinitionLimitService(this, wrapped.getPackageDefinitionLimits());
        packageDefinitions = new NoSwingPackageDefinitionService(this, wrapped.getPackageDefinitions());
        paymentTypes = new NoSwingPaymentTypeService(this, wrapped.getPaymentTypes());
        physicalServers = new NoSwingPhysicalServerService(this, wrapped.getPhysicalServers());
        postgresDatabases = new NoSwingPostgresDatabaseService(this, wrapped.getPostgresDatabases());
        postgresEncodings = new NoSwingPostgresEncodingService(this, wrapped.getPostgresEncodings());
        postgresReservedWords = new NoSwingPostgresReservedWordService(this, wrapped.getPostgresReservedWords());
        postgresServerUsers = new NoSwingPostgresServerUserService(this, wrapped.getPostgresServerUsers());
        postgresServers = new NoSwingPostgresServerService(this, wrapped.getPostgresServers());
        postgresUsers = new NoSwingPostgresUserService(this, wrapped.getPostgresUsers());
        postgresVersions = new NoSwingPostgresVersionService(this, wrapped.getPostgresVersions());
        privateFTPServers = new NoSwingPrivateFTPServerService(this, wrapped.getPrivateFTPServers());
        processorTypes = new NoSwingProcessorTypeService(this, wrapped.getProcessorTypes());
        protocols = new NoSwingProtocolService(this, wrapped.getProtocols());
        racks = new NoSwingRackService(this, wrapped.getRacks());
        resellers = new NoSwingResellerService(this, wrapped.getResellers());
         */
        resourceTypes = new NoSwingResourceTypeService(this, wrapped.getResourceTypes());
        /* TODO
        resources = new NoSwingResourceService(this, wrapped.getResources());
        serverFarms = new NoSwingServerFarmService(this, wrapped.getServerFarms());
        servers = new NoSwingServerService(this, wrapped.getServers());
        shells = new NoSwingShellService(this, wrapped.getShells());
        signupRequestOptions = new NoSwingSignupRequestOptionService(this, wrapped.getSignupRequestOptions());
        signupRequests = new NoSwingSignupRequestService(this, wrapped.getSignupRequests());
        spamEmailMessages = new NoSwingSpamEmailMessageService(this, wrapped.getSpamEmailMessages());
        systemEmailAliass = new NoSwingSystemEmailAliasService(this, wrapped.getSystemEmailAliass());
        technologies = new NoSwingTechnologyService(this, wrapped.getTechnologys());
        technologyClasss = new NoSwingTechnologyClassService(this, wrapped.getTechnologyClasss());
        technologyNames = new NoSwingTechnologyNameService(this, wrapped.getTechnologyNames());
        technologyVersions = new NoSwingTechnologyVersionService(this, wrapped.getTechnologyVersions());
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
        usernames = new NoSwingUsernameService(this, wrapped.getUsernames());
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
    AOServerDaemonHostService<NoSwingConnector,NoSwingConnectorFactory> getAoServerDaemonHosts() throws RemoteException;

    AOServerResourceService<NoSwingConnector,NoSwingConnectorFactory> getAoServerResources() throws RemoteException;

    AOServerService<NoSwingConnector,NoSwingConnectorFactory> getAoServers() throws RemoteException;

    AOServPermissionService<NoSwingConnector,NoSwingConnectorFactory> getAoservPermissions() throws RemoteException;

    AOServProtocolService<NoSwingConnector,NoSwingConnectorFactory> getAoservProtocols() throws RemoteException;

    AOSHCommandService<NoSwingConnector,NoSwingConnectorFactory> getAoshCommands() throws RemoteException;

    ArchitectureService<NoSwingConnector,NoSwingConnectorFactory> getArchitectures() throws RemoteException;

    BackupPartitionService<NoSwingConnector,NoSwingConnectorFactory> getBackupPartitions() throws RemoteException;

    BackupRetentionService<NoSwingConnector,NoSwingConnectorFactory> getBackupRetentions() throws RemoteException;

    BankAccountService<NoSwingConnector,NoSwingConnectorFactory> getBankAccounts() throws RemoteException;

    BankTransactionTypeService<NoSwingConnector,NoSwingConnectorFactory> getBankTransactionTypes() throws RemoteException;

    BankTransactionService<NoSwingConnector,NoSwingConnectorFactory> getBankTransactions() throws RemoteException;

    BankService<NoSwingConnector,NoSwingConnectorFactory> getBanks() throws RemoteException;

    BlackholeEmailAddressService<NoSwingConnector,NoSwingConnectorFactory> getBlackholeEmailAddresses() throws RemoteException;

    BrandService<NoSwingConnector,NoSwingConnectorFactory> getBrands() throws RemoteException;
     */
    public BusinessAdministratorService<NoSwingConnector,NoSwingConnectorFactory> getBusinessAdministrators() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return businessAdministrators;
    }
    /*
    BusinessAdministratorPermissionService<NoSwingConnector,NoSwingConnectorFactory> getBusinessAdministratorPermissions() throws RemoteException;

    BusinessProfileService<NoSwingConnector,NoSwingConnectorFactory> getBusinessProfiles() throws RemoteException;
     */
    public BusinessService<NoSwingConnector,NoSwingConnectorFactory> getBusinesses() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return businesses;
    }

    /*
    BusinessServerService<NoSwingConnector,NoSwingConnectorFactory> getBusinessServers() throws RemoteException;

    ClientJvmProfileService<NoSwingConnector,NoSwingConnectorFactory> getClientJvmProfiles() throws RemoteException;

    CountryCodeService<NoSwingConnector,NoSwingConnectorFactory> getCountryCodes() throws RemoteException;

    CreditCardProcessorService<NoSwingConnector,NoSwingConnectorFactory> getCreditCardProcessors() throws RemoteException;

    CreditCardTransactionService<NoSwingConnector,NoSwingConnectorFactory> getCreditCardTransactions() throws RemoteException;

    CreditCardService<NoSwingConnector,NoSwingConnectorFactory> getCreditCards() throws RemoteException;

    CvsRepositoryService<NoSwingConnector,NoSwingConnectorFactory> getCvsRepositories() throws RemoteException;
     */
    public DisableLogService<NoSwingConnector,NoSwingConnectorFactory> getDisableLogs() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return disabledLogs;
    }
    /*
    DistroFileTypeService<NoSwingConnector,NoSwingConnectorFactory> getDistroFileTypes() throws RemoteException;

    DistroFileService<NoSwingConnector,NoSwingConnectorFactory> getDistroFiles() throws RemoteException;

    DNSForbiddenZoneService<NoSwingConnector,NoSwingConnectorFactory> getDnsForbiddenZones() throws RemoteException;

    DNSRecordService<NoSwingConnector,NoSwingConnectorFactory> getDnsRecords() throws RemoteException;

    DNSTLDService<NoSwingConnector,NoSwingConnectorFactory> getDnsTLDs() throws RemoteException;

    DNSTypeService<NoSwingConnector,NoSwingConnectorFactory> getDnsTypes() throws RemoteException;

    DNSZoneService<NoSwingConnector,NoSwingConnectorFactory> getDnsZones() throws RemoteException;

    EmailAddressService<NoSwingConnector,NoSwingConnectorFactory> getEmailAddresses() throws RemoteException;

    EmailAttachmentBlockService<NoSwingConnector,NoSwingConnectorFactory> getEmailAttachmentBlocks() throws RemoteException;

    EmailAttachmentTypeService<NoSwingConnector,NoSwingConnectorFactory> getEmailAttachmentTypes() throws RemoteException;

    EmailDomainService<NoSwingConnector,NoSwingConnectorFactory> getEmailDomains() throws RemoteException;

    EmailForwardingService<NoSwingConnector,NoSwingConnectorFactory> getEmailForwardings() throws RemoteException;

    EmailListAddressService<NoSwingConnector,NoSwingConnectorFactory> getEmailListAddresses() throws RemoteException;

    EmailListService<NoSwingConnector,NoSwingConnectorFactory> getEmailLists() throws RemoteException;

    EmailPipeAddressService<NoSwingConnector,NoSwingConnectorFactory> getEmailPipeAddresses() throws RemoteException;

    EmailPipeService<NoSwingConnector,NoSwingConnectorFactory> getEmailPipes() throws RemoteException;

    EmailSmtpRelayTypeService<NoSwingConnector,NoSwingConnectorFactory> getEmailSmtpRelayTypes() throws RemoteException;

    EmailSmtpRelayService<NoSwingConnector,NoSwingConnectorFactory> getEmailSmtpRelays() throws RemoteException;

    EmailSmtpSmartHostDomainService<NoSwingConnector,NoSwingConnectorFactory> getEmailSmtpSmartHostDomains() throws RemoteException;

    EmailSmtpSmartHostService<NoSwingConnector,NoSwingConnectorFactory> getEmailSmtpSmartHosts() throws RemoteException;

    EmailSpamAssassinIntegrationModeService<NoSwingConnector,NoSwingConnectorFactory> getEmailSpamAssassinIntegrationModes() throws RemoteException;

    EncryptionKeyService<NoSwingConnector,NoSwingConnectorFactory> getEncryptionKeys() throws RemoteException;

    ExpenseCategoryService<NoSwingConnector,NoSwingConnectorFactory> getExpenseCategories() throws RemoteException;

    FailoverFileLogService<NoSwingConnector,NoSwingConnectorFactory> getFailoverFileLogs() throws RemoteException;

    FailoverFileReplicationService<NoSwingConnector,NoSwingConnectorFactory> getFailoverFileReplications() throws RemoteException;

    FailoverFileScheduleService<NoSwingConnector,NoSwingConnectorFactory> getFailoverFileSchedules() throws RemoteException;

    FailoverMySQLReplicationService<NoSwingConnector,NoSwingConnectorFactory> getFailoverMySQLReplications() throws RemoteException;

    FileBackupSettingService<NoSwingConnector,NoSwingConnectorFactory> getFileBackupSettings() throws RemoteException;

    FTPGuestUserService<NoSwingConnector,NoSwingConnectorFactory> getFtpGuestUsers() throws RemoteException;

    HttpdBindService<NoSwingConnector,NoSwingConnectorFactory> getHttpdBinds() throws RemoteException;

    HttpdJBossSiteService<NoSwingConnector,NoSwingConnectorFactory> getHttpdJBossSites() throws RemoteException;

    HttpdJBossVersionService<NoSwingConnector,NoSwingConnectorFactory> getHttpdJBossVersions() throws RemoteException;

    HttpdJKCodeService<NoSwingConnector,NoSwingConnectorFactory> getHttpdJKCodes() throws RemoteException;

    HttpdJKProtocolService<NoSwingConnector,NoSwingConnectorFactory> getHttpdJKProtocols() throws RemoteException;

    HttpdServerService<NoSwingConnector,NoSwingConnectorFactory> getHttpdServers() throws RemoteException;

    HttpdSharedTomcatService<NoSwingConnector,NoSwingConnectorFactory> getHttpdSharedTomcats() throws RemoteException;

    HttpdSiteAuthenticatedLocationService<NoSwingConnector,NoSwingConnectorFactory> getHttpdSiteAuthenticatedLocations() throws RemoteException;

    HttpdSiteBindService<NoSwingConnector,NoSwingConnectorFactory> getHttpdSiteBinds() throws RemoteException;

    HttpdSiteURLService<NoSwingConnector,NoSwingConnectorFactory> getHttpdSiteURLs() throws RemoteException;

    HttpdSiteService<NoSwingConnector,NoSwingConnectorFactory> getHttpdSites() throws RemoteException;

    HttpdStaticSiteService<NoSwingConnector,NoSwingConnectorFactory> getHttpdStaticSites() throws RemoteException;

    HttpdTomcatContextService<NoSwingConnector,NoSwingConnectorFactory> getHttpdTomcatContexts() throws RemoteException;

    HttpdTomcatDataSourceService<NoSwingConnector,NoSwingConnectorFactory> getHttpdTomcatDataSources() throws RemoteException;

    HttpdTomcatParameterService<NoSwingConnector,NoSwingConnectorFactory> getHttpdTomcatParameters() throws RemoteException;

    HttpdTomcatSiteService<NoSwingConnector,NoSwingConnectorFactory> getHttpdTomcatSites() throws RemoteException;

    HttpdTomcatSharedSiteService<NoSwingConnector,NoSwingConnectorFactory> getHttpdTomcatSharedSites() throws RemoteException;

    HttpdTomcatStdSiteService<NoSwingConnector,NoSwingConnectorFactory> getHttpdTomcatStdSites() throws RemoteException;

    HttpdTomcatVersionService<NoSwingConnector,NoSwingConnectorFactory> getHttpdTomcatVersions() throws RemoteException;

    HttpdWorkerService<NoSwingConnector,NoSwingConnectorFactory> getHttpdWorkers() throws RemoteException;

    IPAddressService<NoSwingConnector,NoSwingConnectorFactory> getIpAddresses() throws RemoteException;
    */
    public LanguageService<NoSwingConnector,NoSwingConnectorFactory> getLanguages() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return languages;
    }
    /* TODO
    LinuxAccAddressService<NoSwingConnector,NoSwingConnectorFactory> getLinuxAccAddresses() throws RemoteException;

    LinuxAccountTypeService<NoSwingConnector,NoSwingConnectorFactory> getLinuxAccountTypes() throws RemoteException;

    LinuxAccountService<NoSwingConnector,NoSwingConnectorFactory> getLinuxAccounts() throws RemoteException;

    LinuxGroupAccountService<NoSwingConnector,NoSwingConnectorFactory> getLinuxGroupAccounts() throws RemoteException;

    LinuxGroupTypeService<NoSwingConnector,NoSwingConnectorFactory> getLinuxGroupTypes() throws RemoteException;

    LinuxGroupService<NoSwingConnector,NoSwingConnectorFactory> getLinuxGroups() throws RemoteException;

    LinuxIDService<NoSwingConnector,NoSwingConnectorFactory> getLinuxIDs() throws RemoteException;

    LinuxServerAccountService<NoSwingConnector,NoSwingConnectorFactory> getLinuxServerAccounts() throws RemoteException;

    LinuxServerGroupService<NoSwingConnector,NoSwingConnectorFactory> getLinuxServerGroups() throws RemoteException;

    MajordomoListService<NoSwingConnector,NoSwingConnectorFactory> getMajordomoLists() throws RemoteException;

    MajordomoServerService<NoSwingConnector,NoSwingConnectorFactory> getMajordomoServers() throws RemoteException;

    MajordomoVersionService<NoSwingConnector,NoSwingConnectorFactory> getMajordomoVersions() throws RemoteException;

    MasterHistoryService<NoSwingConnector,NoSwingConnectorFactory> getMasterHistory() throws RemoteException;

    MasterHostService<NoSwingConnector,NoSwingConnectorFactory> getMasterHosts() throws RemoteException;

    MasterServerService<NoSwingConnector,NoSwingConnectorFactory> getMasterServers() throws RemoteException;

    MasterUserService<NoSwingConnector,NoSwingConnectorFactory> getMasterUsers() throws RemoteException;

    MonthlyChargeService<NoSwingConnector,NoSwingConnectorFactory> getMonthlyCharges() throws RemoteException;

    MySQLDatabaseService<NoSwingConnector,NoSwingConnectorFactory> getMysqlDatabases() throws RemoteException;

    MySQLDBUserService<NoSwingConnector,NoSwingConnectorFactory> getMysqlDBUsers() throws RemoteException;

    MySQLReservedWordService<NoSwingConnector,NoSwingConnectorFactory> getMysqlReservedWords() throws RemoteException;

    MySQLServerService<NoSwingConnector,NoSwingConnectorFactory> getMysqlServers() throws RemoteException;

    MySQLUserService<NoSwingConnector,NoSwingConnectorFactory> getMysqlUsers() throws RemoteException;

    NetBindService<NoSwingConnector,NoSwingConnectorFactory> getNetBinds() throws RemoteException;

    NetDeviceIDService<NoSwingConnector,NoSwingConnectorFactory> getNetDeviceIDs() throws RemoteException;

    NetDeviceService<NoSwingConnector,NoSwingConnectorFactory> getNetDevices() throws RemoteException;

    NetPortService<NoSwingConnector,NoSwingConnectorFactory> getNetPorts() throws RemoteException;

    NetProtocolService<NoSwingConnector,NoSwingConnectorFactory> getNetProtocols() throws RemoteException;

    NetTcpRedirectService<NoSwingConnector,NoSwingConnectorFactory> getNetTcpRedirects() throws RemoteException;

    NoticeLogService<NoSwingConnector,NoSwingConnectorFactory> getNoticeLogs() throws RemoteException;

    NoticeTypeService<NoSwingConnector,NoSwingConnectorFactory> getNoticeTypes() throws RemoteException;

    OperatingSystemVersionService<NoSwingConnector,NoSwingConnectorFactory> getOperatingSystemVersions() throws RemoteException;

    OperatingSystemService<NoSwingConnector,NoSwingConnectorFactory> getOperatingSystems() throws RemoteException;
    */
    public PackageCategoryService<NoSwingConnector,NoSwingConnectorFactory> getPackageCategories() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return packageCategories;
    }
    /*
    PackageDefinitionLimitService<NoSwingConnector,NoSwingConnectorFactory> getPackageDefinitionLimits() throws RemoteException;

    PackageDefinitionService<NoSwingConnector,NoSwingConnectorFactory> getPackageDefinitions() throws RemoteException;

    PaymentTypeService<NoSwingConnector,NoSwingConnectorFactory> getPaymentTypes() throws RemoteException;

    PhysicalServerService<NoSwingConnector,NoSwingConnectorFactory> getPhysicalServers() throws RemoteException;

    PostgresDatabaseService<NoSwingConnector,NoSwingConnectorFactory> getPostgresDatabases() throws RemoteException;

    PostgresEncodingService<NoSwingConnector,NoSwingConnectorFactory> getPostgresEncodings() throws RemoteException;

    PostgresReservedWordService<NoSwingConnector,NoSwingConnectorFactory> getPostgresReservedWords() throws RemoteException;

    PostgresServerUserService<NoSwingConnector,NoSwingConnectorFactory> getPostgresServerUsers() throws RemoteException;

    PostgresServerService<NoSwingConnector,NoSwingConnectorFactory> getPostgresServers() throws RemoteException;

    PostgresUserService<NoSwingConnector,NoSwingConnectorFactory> getPostgresUsers() throws RemoteException;

    PostgresVersionService<NoSwingConnector,NoSwingConnectorFactory> getPostgresVersions() throws RemoteException;

    PrivateFTPServerService<NoSwingConnector,NoSwingConnectorFactory> getPrivateFTPServers() throws RemoteException;

    ProcessorTypeService<NoSwingConnector,NoSwingConnectorFactory> getProcessorTypes() throws RemoteException;

    ProtocolService<NoSwingConnector,NoSwingConnectorFactory> getProtocols() throws RemoteException;

    RackService<NoSwingConnector,NoSwingConnectorFactory> getRacks() throws RemoteException;

    ResellerService<NoSwingConnector,NoSwingConnectorFactory> getResellers() throws RemoteException;
*/
    public ResourceTypeService<NoSwingConnector,NoSwingConnectorFactory> getResourceTypes() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return resourceTypes;
    }
/* TODO
    ResourceService<NoSwingConnector,NoSwingConnectorFactory> getResources() throws RemoteException;

    ServerFarmService<NoSwingConnector,NoSwingConnectorFactory> getServerFarms() throws RemoteException;

    ServerTable getServers() throws RemoteException;

    ShellService<NoSwingConnector,NoSwingConnectorFactory> getShells() throws RemoteException;

    SignupRequestOptionService<NoSwingConnector,NoSwingConnectorFactory> getSignupRequestOptions() throws RemoteException;

    SignupRequestService<NoSwingConnector,NoSwingConnectorFactory> getSignupRequests() throws RemoteException;

    SpamEmailMessageService<NoSwingConnector,NoSwingConnectorFactory> getSpamEmailMessages() throws RemoteException;

    SystemEmailAliasService<NoSwingConnector,NoSwingConnectorFactory> getSystemEmailAliases() throws RemoteException;

    TechnologyService<NoSwingConnector,NoSwingConnectorFactory> getTechnologies() throws RemoteException;

    TechnologyClassService<NoSwingConnector,NoSwingConnectorFactory> getTechnologyClasses() throws RemoteException;

    TechnologyNameService<NoSwingConnector,NoSwingConnectorFactory> getTechnologyNames() throws RemoteException;

    TechnologyVersionService<NoSwingConnector,NoSwingConnectorFactory> getTechnologyVersions() throws RemoteException;

    TicketActionTypeService<NoSwingConnector,NoSwingConnectorFactory> getTicketActionTypes() throws RemoteException;

    TicketActionService<NoSwingConnector,NoSwingConnectorFactory> getTicketActions() throws RemoteException;

    TicketAssignmentService<NoSwingConnector,NoSwingConnectorFactory> getTicketAssignments() throws RemoteException;

    TicketBrandCategoryService<NoSwingConnector,NoSwingConnectorFactory> getTicketBrandCategories() throws RemoteException;
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
    TicketService<NoSwingConnector,NoSwingConnectorFactory> getTickets() throws RemoteException;
    */
    public TimeZoneService<NoSwingConnector,NoSwingConnectorFactory> getTimeZones() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return timeZones;
    }
    /* TODO
    TransactionTypeService<NoSwingConnector,NoSwingConnectorFactory> getTransactionTypes() throws RemoteException;

    TransactionService<NoSwingConnector,NoSwingConnectorFactory> getTransactions() throws RemoteException;

    USStateService<NoSwingConnector,NoSwingConnectorFactory> getUsStates() throws RemoteException;

    UsernameService<NoSwingConnector,NoSwingConnectorFactory> getUsernames() throws RemoteException;

    VirtualDiskService<NoSwingConnector,NoSwingConnectorFactory> getVirtualDisks() throws RemoteException;

    VirtualServerService<NoSwingConnector,NoSwingConnectorFactory> getVirtualServers() throws RemoteException;

    WhoisHistoryService<NoSwingConnector,NoSwingConnectorFactory> getWhoisHistory() throws RemoteException;
 */
}
