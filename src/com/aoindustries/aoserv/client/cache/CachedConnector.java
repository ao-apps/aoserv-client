package com.aoindustries.aoserv.client.cache;

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
    /* TODO
    final CachedAOServerDaemonHostService aoserverDaemonHosts;
    final CachedAOServerResourceService aoserverResources;
    final CachedAOServerService aoservers;
    final CachedAOServPermissionService aoservPermissions;
    final CachedAOServProtocolService aoservProtocols;
    final CachedAOSHCommandService aoshCommands;
    final CachedArchitectureService architectures;
    final CachedBackupPartitionService backupPartitions;
    final CachedBackupRetentionService backupRetentions;
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
    final CachedCountryCodeService countryCodes;
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
    final CachedFailoverFileReplicationService failoverFileReplications;
    final CachedFailoverFileScheduleService failoverFileSchedules;
    final CachedFailoverMySQLReplicationService failoverMySQLReplications;
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
    final CachedMySQLServerService mysqlServers;
    final CachedMySQLUserService mysqlUsers;
    final CachedNetBindService netBinds;
    final CachedNetDeviceIDService netDeviceIDs;
    final CachedNetDeviceService netDevices;
    final CachedNetPortService netPorts;
    final CachedNetProtocolService netProtocols;
    final CachedNetTcpRedirectService netTcpRedirects;
    final CachedNoticeLogService noticeLogs;
    final CachedNoticeTypeService noticeTypes;
    final CachedOperatingSystemVersionService operatingSystemVersions;
    final CachedOperatingSystemService operatingSystems;
    */
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
    final CachedProtocolService protocols;
    final CachedRackService racks;
    final CachedResellerService resellers;
     */
    final CachedResourceTypeService resourceTypes;
    /* TODO
    final CachedResourceService resources;
    final CachedServerFarmService serverFarms;
    final CachedServerService servers;
    final CachedShellService shells;
    final CachedSignupRequestOptionService signupRequestOptions;
    final CachedSignupRequestService signupRequests;
    final CachedSpamEmailMessageService spamEmailMessages;
    final CachedSystemEmailAliasService systemEmailAliass;
    final CachedTechnologyService technologies;
    final CachedTechnologyClassService technologyClasss;
    final CachedTechnologyNameService technologyNames;
    final CachedTechnologyVersionService technologyVersions;
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
    final CachedUsernameService usernames;
    final CachedVirtualDiskService virtualDisks;
    final CachedVirtualServerService virtualServers;
    final CachedWhoisHistoryService whoisHistories;
     */

    CachedConnector(CachedConnectorFactory factory, AOServConnector<?,?> wrapped) throws RemoteException, LoginException {
        this.factory = factory;
        this.wrapped = wrapped;
        locale = wrapped.getLocale();
        connectAs = wrapped.getConnectAs();
        /* TODO
        aoserverDaemonHosts = new CachedAOServerDaemonHostService(this, wrapped.getAOServerDaemonHosts());
        aoserverResources = new CachedAOServerResourceService(this, wrapped.getAOServerResources());
        aoservers = new CachedAOServerService(this, wrapped.getAOServers());
        aoservPermissions = new CachedAOServPermissionService(this, wrapped.getAOServPermissions());
        aoservProtocols = new CachedAOServProtocolService(this, wrapped.getAOServProtocols());
        aoshCommands = new CachedAOSHCommandService(this, wrapped.getAOSHCommands());
        architectures = new CachedArchitectureService(this, wrapped.getArchitectures());
        backupPartitions = new CachedBackupPartitionService(this, wrapped.getBackupPartitions());
        backupRetentions = new CachedBackupRetentionService(this, wrapped.getBackupRetentions());
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
        countryCodes = new CachedCountryCodeService(this, wrapped.getCountryCodes());
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
        failoverFileReplications = new CachedFailoverFileReplicationService(this, wrapped.getFailoverFileReplications());
        failoverFileSchedules = new CachedFailoverFileScheduleService(this, wrapped.getFailoverFileSchedules());
        failoverMySQLReplications = new CachedFailoverMySQLReplicationService(this, wrapped.getFailoverMySQLReplications());
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
        mysqlServers = new CachedMySQLServerService(this, wrapped.getMySQLServers());
        mysqlUsers = new CachedMySQLUserService(this, wrapped.getMySQLUsers());
        netBinds = new CachedNetBindService(this, wrapped.getNetBinds());
        netDeviceIDs = new CachedNetDeviceIDService(this, wrapped.getNetDeviceIDs());
        netDevices = new CachedNetDeviceService(this, wrapped.getNetDevices());
        netPorts = new CachedNetPortService(this, wrapped.getNetPorts());
        netProtocols = new CachedNetProtocolService(this, wrapped.getNetProtocols());
        netTcpRedirects = new CachedNetTcpRedirectService(this, wrapped.getNetTcpRedirects());
        noticeLogs = new CachedNoticeLogService(this, wrapped.getNoticeLogs());
        noticeTypes = new CachedNoticeTypeService(this, wrapped.getNoticeTypes());
        operatingSystemVersions = new CachedOperatingSystemVersionService(this, wrapped.getOperatingSystemVersions());
        operatingSystems = new CachedOperatingSystemService(this, wrapped.getOperatingSystems());
        */
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
        protocols = new CachedProtocolService(this, wrapped.getProtocols());
        racks = new CachedRackService(this, wrapped.getRacks());
        resellers = new CachedResellerService(this, wrapped.getResellers());
         */
        resourceTypes = new CachedResourceTypeService(this, wrapped.getResourceTypes());
        /* TODO
        resources = new CachedResourceService(this, wrapped.getResources());
        serverFarms = new CachedServerFarmService(this, wrapped.getServerFarms());
        servers = new CachedServerService(this, wrapped.getServers());
        shells = new CachedShellService(this, wrapped.getShells());
        signupRequestOptions = new CachedSignupRequestOptionService(this, wrapped.getSignupRequestOptions());
        signupRequests = new CachedSignupRequestService(this, wrapped.getSignupRequests());
        spamEmailMessages = new CachedSpamEmailMessageService(this, wrapped.getSpamEmailMessages());
        systemEmailAliass = new CachedSystemEmailAliasService(this, wrapped.getSystemEmailAliass());
        technologies = new CachedTechnologyService(this, wrapped.getTechnologys());
        technologyClasss = new CachedTechnologyClassService(this, wrapped.getTechnologyClasss());
        technologyNames = new CachedTechnologyNameService(this, wrapped.getTechnologyNames());
        technologyVersions = new CachedTechnologyVersionService(this, wrapped.getTechnologyVersions());
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
        usernames = new CachedUsernameService(this, wrapped.getUsernames());
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
    AOServerDaemonHostService<CachedConnector,CachedConnectorFactory> getAoServerDaemonHosts();

    AOServerResourceService<CachedConnector,CachedConnectorFactory> getAoServerResources();

    AOServerService<CachedConnector,CachedConnectorFactory> getAoServers();

    AOServPermissionService<CachedConnector,CachedConnectorFactory> getAoservPermissions();

    AOServProtocolService<CachedConnector,CachedConnectorFactory> getAoservProtocols();

    AOSHCommandService<CachedConnector,CachedConnectorFactory> getAoshCommands();

    ArchitectureService<CachedConnector,CachedConnectorFactory> getArchitectures();

    BackupPartitionService<CachedConnector,CachedConnectorFactory> getBackupPartitions();

    BackupRetentionService<CachedConnector,CachedConnectorFactory> getBackupRetentions();

    BankAccountService<CachedConnector,CachedConnectorFactory> getBankAccounts();

    BankTransactionTypeService<CachedConnector,CachedConnectorFactory> getBankTransactionTypes();

    BankTransactionService<CachedConnector,CachedConnectorFactory> getBankTransactions();

    BankService<CachedConnector,CachedConnectorFactory> getBanks();

    BlackholeEmailAddressService<CachedConnector,CachedConnectorFactory> getBlackholeEmailAddresses();

    BrandService<CachedConnector,CachedConnectorFactory> getBrands();
     */
    public BusinessAdministratorService<CachedConnector,CachedConnectorFactory> getBusinessAdministrators() {
        return businessAdministrators;
    }
    /*
    BusinessAdministratorPermissionService<CachedConnector,CachedConnectorFactory> getBusinessAdministratorPermissions();

    BusinessProfileService<CachedConnector,CachedConnectorFactory> getBusinessProfiles();
     */
    public BusinessService<CachedConnector,CachedConnectorFactory> getBusinesses() {
        return businesses;
    }

    /*
    BusinessServerService<CachedConnector,CachedConnectorFactory> getBusinessServers();

    ClientJvmProfileService<CachedConnector,CachedConnectorFactory> getClientJvmProfiles();

    CountryCodeService<CachedConnector,CachedConnectorFactory> getCountryCodes();

    CreditCardProcessorService<CachedConnector,CachedConnectorFactory> getCreditCardProcessors();

    CreditCardTransactionService<CachedConnector,CachedConnectorFactory> getCreditCardTransactions();

    CreditCardService<CachedConnector,CachedConnectorFactory> getCreditCards();

    CvsRepositoryService<CachedConnector,CachedConnectorFactory> getCvsRepositories();
     */
    public DisableLogService<CachedConnector,CachedConnectorFactory> getDisableLogs() {
        return disableLogs;
    }
    /*
    DistroFileTypeService<CachedConnector,CachedConnectorFactory> getDistroFileTypes();

    DistroFileService<CachedConnector,CachedConnectorFactory> getDistroFiles();

    DNSForbiddenZoneService<CachedConnector,CachedConnectorFactory> getDnsForbiddenZones();

    DNSRecordService<CachedConnector,CachedConnectorFactory> getDnsRecords();

    DNSTLDService<CachedConnector,CachedConnectorFactory> getDnsTLDs();

    DNSTypeService<CachedConnector,CachedConnectorFactory> getDnsTypes();

    DNSZoneService<CachedConnector,CachedConnectorFactory> getDnsZones();

    EmailAddressService<CachedConnector,CachedConnectorFactory> getEmailAddresses();

    EmailAttachmentBlockService<CachedConnector,CachedConnectorFactory> getEmailAttachmentBlocks();

    EmailAttachmentTypeService<CachedConnector,CachedConnectorFactory> getEmailAttachmentTypes();

    EmailDomainService<CachedConnector,CachedConnectorFactory> getEmailDomains();

    EmailForwardingService<CachedConnector,CachedConnectorFactory> getEmailForwardings();

    EmailListAddressService<CachedConnector,CachedConnectorFactory> getEmailListAddresses();

    EmailListService<CachedConnector,CachedConnectorFactory> getEmailLists();

    EmailPipeAddressService<CachedConnector,CachedConnectorFactory> getEmailPipeAddresses();

    EmailPipeService<CachedConnector,CachedConnectorFactory> getEmailPipes();

    EmailSmtpRelayTypeService<CachedConnector,CachedConnectorFactory> getEmailSmtpRelayTypes();

    EmailSmtpRelayService<CachedConnector,CachedConnectorFactory> getEmailSmtpRelays();

    EmailSmtpSmartHostDomainService<CachedConnector,CachedConnectorFactory> getEmailSmtpSmartHostDomains();

    EmailSmtpSmartHostService<CachedConnector,CachedConnectorFactory> getEmailSmtpSmartHosts();

    EmailSpamAssassinIntegrationModeService<CachedConnector,CachedConnectorFactory> getEmailSpamAssassinIntegrationModes();

    EncryptionKeyService<CachedConnector,CachedConnectorFactory> getEncryptionKeys();

    ExpenseCategoryService<CachedConnector,CachedConnectorFactory> getExpenseCategories();

    FailoverFileLogService<CachedConnector,CachedConnectorFactory> getFailoverFileLogs();

    FailoverFileReplicationService<CachedConnector,CachedConnectorFactory> getFailoverFileReplications();

    FailoverFileScheduleService<CachedConnector,CachedConnectorFactory> getFailoverFileSchedules();

    FailoverMySQLReplicationService<CachedConnector,CachedConnectorFactory> getFailoverMySQLReplications();

    FileBackupSettingService<CachedConnector,CachedConnectorFactory> getFileBackupSettings();

    FTPGuestUserService<CachedConnector,CachedConnectorFactory> getFtpGuestUsers();

    HttpdBindService<CachedConnector,CachedConnectorFactory> getHttpdBinds();

    HttpdJBossSiteService<CachedConnector,CachedConnectorFactory> getHttpdJBossSites();

    HttpdJBossVersionService<CachedConnector,CachedConnectorFactory> getHttpdJBossVersions();

    HttpdJKCodeService<CachedConnector,CachedConnectorFactory> getHttpdJKCodes();

    HttpdJKProtocolService<CachedConnector,CachedConnectorFactory> getHttpdJKProtocols();

    HttpdServerService<CachedConnector,CachedConnectorFactory> getHttpdServers();

    HttpdSharedTomcatService<CachedConnector,CachedConnectorFactory> getHttpdSharedTomcats();

    HttpdSiteAuthenticatedLocationService<CachedConnector,CachedConnectorFactory> getHttpdSiteAuthenticatedLocations();

    HttpdSiteBindService<CachedConnector,CachedConnectorFactory> getHttpdSiteBinds();

    HttpdSiteURLService<CachedConnector,CachedConnectorFactory> getHttpdSiteURLs();

    HttpdSiteService<CachedConnector,CachedConnectorFactory> getHttpdSites();

    HttpdStaticSiteService<CachedConnector,CachedConnectorFactory> getHttpdStaticSites();

    HttpdTomcatContextService<CachedConnector,CachedConnectorFactory> getHttpdTomcatContexts();

    HttpdTomcatDataSourceService<CachedConnector,CachedConnectorFactory> getHttpdTomcatDataSources();

    HttpdTomcatParameterService<CachedConnector,CachedConnectorFactory> getHttpdTomcatParameters();

    HttpdTomcatSiteService<CachedConnector,CachedConnectorFactory> getHttpdTomcatSites();

    HttpdTomcatSharedSiteService<CachedConnector,CachedConnectorFactory> getHttpdTomcatSharedSites();

    HttpdTomcatStdSiteService<CachedConnector,CachedConnectorFactory> getHttpdTomcatStdSites();

    HttpdTomcatVersionService<CachedConnector,CachedConnectorFactory> getHttpdTomcatVersions();

    HttpdWorkerService<CachedConnector,CachedConnectorFactory> getHttpdWorkers();

    IPAddressService<CachedConnector,CachedConnectorFactory> getIpAddresses();
    */
    public LanguageService<CachedConnector,CachedConnectorFactory> getLanguages() {
        return languages;
    }
    /* TODO
    LinuxAccAddressService<CachedConnector,CachedConnectorFactory> getLinuxAccAddresses();

    LinuxAccountTypeService<CachedConnector,CachedConnectorFactory> getLinuxAccountTypes();

    LinuxAccountService<CachedConnector,CachedConnectorFactory> getLinuxAccounts();

    LinuxGroupAccountService<CachedConnector,CachedConnectorFactory> getLinuxGroupAccounts();

    LinuxGroupTypeService<CachedConnector,CachedConnectorFactory> getLinuxGroupTypes();

    LinuxGroupService<CachedConnector,CachedConnectorFactory> getLinuxGroups();

    LinuxIDService<CachedConnector,CachedConnectorFactory> getLinuxIDs();

    LinuxServerAccountService<CachedConnector,CachedConnectorFactory> getLinuxServerAccounts();

    LinuxServerGroupService<CachedConnector,CachedConnectorFactory> getLinuxServerGroups();

    MajordomoListService<CachedConnector,CachedConnectorFactory> getMajordomoLists();

    MajordomoServerService<CachedConnector,CachedConnectorFactory> getMajordomoServers();

    MajordomoVersionService<CachedConnector,CachedConnectorFactory> getMajordomoVersions();

    MasterHistoryService<CachedConnector,CachedConnectorFactory> getMasterHistory();

    MasterHostService<CachedConnector,CachedConnectorFactory> getMasterHosts();

    MasterServerService<CachedConnector,CachedConnectorFactory> getMasterServers();

    MasterUserService<CachedConnector,CachedConnectorFactory> getMasterUsers();

    MonthlyChargeService<CachedConnector,CachedConnectorFactory> getMonthlyCharges();

    MySQLDatabaseService<CachedConnector,CachedConnectorFactory> getMysqlDatabases();

    MySQLDBUserService<CachedConnector,CachedConnectorFactory> getMysqlDBUsers();

    MySQLReservedWordService<CachedConnector,CachedConnectorFactory> getMysqlReservedWords();

    MySQLServerService<CachedConnector,CachedConnectorFactory> getMysqlServers();

    MySQLUserService<CachedConnector,CachedConnectorFactory> getMysqlUsers();

    NetBindService<CachedConnector,CachedConnectorFactory> getNetBinds();

    NetDeviceIDService<CachedConnector,CachedConnectorFactory> getNetDeviceIDs();

    NetDeviceService<CachedConnector,CachedConnectorFactory> getNetDevices();

    NetPortService<CachedConnector,CachedConnectorFactory> getNetPorts();

    NetProtocolService<CachedConnector,CachedConnectorFactory> getNetProtocols();

    NetTcpRedirectService<CachedConnector,CachedConnectorFactory> getNetTcpRedirects();

    NoticeLogService<CachedConnector,CachedConnectorFactory> getNoticeLogs();

    NoticeTypeService<CachedConnector,CachedConnectorFactory> getNoticeTypes();

    OperatingSystemVersionService<CachedConnector,CachedConnectorFactory> getOperatingSystemVersions();

    OperatingSystemService<CachedConnector,CachedConnectorFactory> getOperatingSystems();
    */
    public PackageCategoryService<CachedConnector,CachedConnectorFactory> getPackageCategories() {
        return packageCategories;
    }
    /*
    PackageDefinitionLimitService<CachedConnector,CachedConnectorFactory> getPackageDefinitionLimits();

    PackageDefinitionService<CachedConnector,CachedConnectorFactory> getPackageDefinitions();

    PaymentTypeService<CachedConnector,CachedConnectorFactory> getPaymentTypes();

    PhysicalServerService<CachedConnector,CachedConnectorFactory> getPhysicalServers();

    PostgresDatabaseService<CachedConnector,CachedConnectorFactory> getPostgresDatabases();

    PostgresEncodingService<CachedConnector,CachedConnectorFactory> getPostgresEncodings();

    PostgresReservedWordService<CachedConnector,CachedConnectorFactory> getPostgresReservedWords();

    PostgresServerUserService<CachedConnector,CachedConnectorFactory> getPostgresServerUsers();

    PostgresServerService<CachedConnector,CachedConnectorFactory> getPostgresServers();

    PostgresUserService<CachedConnector,CachedConnectorFactory> getPostgresUsers();

    PostgresVersionService<CachedConnector,CachedConnectorFactory> getPostgresVersions();

    PrivateFTPServerService<CachedConnector,CachedConnectorFactory> getPrivateFTPServers();

    ProcessorTypeService<CachedConnector,CachedConnectorFactory> getProcessorTypes();

    ProtocolService<CachedConnector,CachedConnectorFactory> getProtocols();

    RackService<CachedConnector,CachedConnectorFactory> getRacks();

    ResellerService<CachedConnector,CachedConnectorFactory> getResellers();
*/
    public ResourceTypeService<CachedConnector,CachedConnectorFactory> getResourceTypes() {
        return resourceTypes;
    }
/* TODO
    ResourceService<CachedConnector,CachedConnectorFactory> getResources();

    ServerFarmService<CachedConnector,CachedConnectorFactory> getServerFarms();

    ServerTable getServers();

    ShellService<CachedConnector,CachedConnectorFactory> getShells();

    SignupRequestOptionService<CachedConnector,CachedConnectorFactory> getSignupRequestOptions();

    SignupRequestService<CachedConnector,CachedConnectorFactory> getSignupRequests();

    SpamEmailMessageService<CachedConnector,CachedConnectorFactory> getSpamEmailMessages();

    SystemEmailAliasService<CachedConnector,CachedConnectorFactory> getSystemEmailAliases();

    TechnologyService<CachedConnector,CachedConnectorFactory> getTechnologies();

    TechnologyClassService<CachedConnector,CachedConnectorFactory> getTechnologyClasses();

    TechnologyNameService<CachedConnector,CachedConnectorFactory> getTechnologyNames();

    TechnologyVersionService<CachedConnector,CachedConnectorFactory> getTechnologyVersions();

    TicketActionTypeService<CachedConnector,CachedConnectorFactory> getTicketActionTypes();

    TicketActionService<CachedConnector,CachedConnectorFactory> getTicketActions();

    TicketAssignmentService<CachedConnector,CachedConnectorFactory> getTicketAssignments();

    TicketBrandCategoryService<CachedConnector,CachedConnectorFactory> getTicketBrandCategories();
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
    TicketService<CachedConnector,CachedConnectorFactory> getTickets();
    */
    public TimeZoneService<CachedConnector,CachedConnectorFactory> getTimeZones() {
        return timeZones;
    }
    /* TODO
    TransactionTypeService<CachedConnector,CachedConnectorFactory> getTransactionTypes();

    TransactionService<CachedConnector,CachedConnectorFactory> getTransactions();

    USStateService<CachedConnector,CachedConnectorFactory> getUsStates();

    UsernameService<CachedConnector,CachedConnectorFactory> getUsernames();

    VirtualDiskService<CachedConnector,CachedConnectorFactory> getVirtualDisks();

    VirtualServerService<CachedConnector,CachedConnectorFactory> getVirtualServers();

    WhoisHistoryService<CachedConnector,CachedConnectorFactory> getWhoisHistory();
 */
}
