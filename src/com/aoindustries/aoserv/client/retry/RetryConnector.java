package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.AOServConnectorUtils;
import com.aoindustries.aoserv.client.AOServPermissionService;
import com.aoindustries.aoserv.client.AOServService;
import com.aoindustries.aoserv.client.AOServerDaemonHostService;
import com.aoindustries.aoserv.client.AOServerResourceService;
import com.aoindustries.aoserv.client.AOServerService;
import com.aoindustries.aoserv.client.ArchitectureService;
import com.aoindustries.aoserv.client.BackupPartitionService;
import com.aoindustries.aoserv.client.BackupRetentionService;
import com.aoindustries.aoserv.client.BusinessAdministrator;
import com.aoindustries.aoserv.client.BusinessAdministratorService;
import com.aoindustries.aoserv.client.BusinessServerService;
import com.aoindustries.aoserv.client.BusinessService;
import com.aoindustries.aoserv.client.CountryCodeService;
import com.aoindustries.aoserv.client.CvsRepositoryService;
import com.aoindustries.aoserv.client.DisableLogService;
import com.aoindustries.aoserv.client.FailoverFileLogService;
import com.aoindustries.aoserv.client.FailoverFileReplicationService;
import com.aoindustries.aoserv.client.FailoverFileScheduleService;
import com.aoindustries.aoserv.client.FailoverMySQLReplicationService;
import com.aoindustries.aoserv.client.FileBackupSettingService;
import com.aoindustries.aoserv.client.GroupNameService;
import com.aoindustries.aoserv.client.HttpdSiteService;
import com.aoindustries.aoserv.client.IPAddressService;
import com.aoindustries.aoserv.client.LanguageService;
import com.aoindustries.aoserv.client.LinuxAccountGroupService;
import com.aoindustries.aoserv.client.LinuxAccountService;
import com.aoindustries.aoserv.client.LinuxAccountTypeService;
import com.aoindustries.aoserv.client.LinuxGroupService;
import com.aoindustries.aoserv.client.LinuxGroupTypeService;
import com.aoindustries.aoserv.client.MySQLDBUserService;
import com.aoindustries.aoserv.client.MySQLDatabaseService;
import com.aoindustries.aoserv.client.MySQLServerService;
import com.aoindustries.aoserv.client.MySQLUserService;
import com.aoindustries.aoserv.client.NetBindService;
import com.aoindustries.aoserv.client.NetDeviceIDService;
import com.aoindustries.aoserv.client.NetDeviceService;
import com.aoindustries.aoserv.client.NetProtocolService;
import com.aoindustries.aoserv.client.NetTcpRedirectService;
import com.aoindustries.aoserv.client.OperatingSystemService;
import com.aoindustries.aoserv.client.OperatingSystemVersionService;
import com.aoindustries.aoserv.client.PackageCategoryService;
import com.aoindustries.aoserv.client.PostgresDatabaseService;
import com.aoindustries.aoserv.client.PostgresEncodingService;
import com.aoindustries.aoserv.client.PostgresServerService;
import com.aoindustries.aoserv.client.PostgresUserService;
import com.aoindustries.aoserv.client.PostgresVersionService;
import com.aoindustries.aoserv.client.ProtocolService;
import com.aoindustries.aoserv.client.ResourceService;
import com.aoindustries.aoserv.client.ResourceTypeService;
import com.aoindustries.aoserv.client.ServerFarmService;
import com.aoindustries.aoserv.client.ServerResourceService;
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
import com.aoindustries.aoserv.client.validator.DomainName;
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.security.LoginException;
import java.rmi.ConnectException;
import java.rmi.MarshalException;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @see  RetryConnectorFactory
 *
 * @author  AO Industries, Inc.
 */
final public class RetryConnector implements AOServConnector<RetryConnector,RetryConnectorFactory> {

    private static final Logger logger = Logger.getLogger(RetryConnector.class.getName());

    final RetryConnectorFactory factory;
    Locale locale;
    final UserId connectAs;
    private final UserId authenticateAs;
    private final String password;
    private final DomainName daemonServer;
    final RetryAOServerDaemonHostService aoserverDaemonHosts;
    final RetryAOServerResourceService aoserverResources;
    final RetryAOServerService aoservers;
    final RetryAOServPermissionService aoservPermissions;
    /* TODO
    final RetryAOServProtocolService aoservProtocols;
    final RetryAOSHCommandService aoshCommands;
     */
    final RetryArchitectureService architectures;
    final RetryBackupPartitionService backupPartitions;
    final RetryBackupRetentionService backupRetentions;
    /* TODO
    final RetryBankAccountService bankAccounts;
    final RetryBankTransactionTypeService bankTransactionTypes;
    final RetryBankTransactionService bankTransactions;
    final RetryBankService banks;
    final RetryBlackholeEmailAddressService blackholeEmailAddresss;
    final RetryBrandService brands;
     */
    final RetryBusinessAdministratorService businessAdministrators;
    /* TODO
    final RetryBusinessAdministratorPermissionService businessAdministratorPermissions;
    final RetryBusinessProfileService businessProfiles;
     */
    final RetryBusinessService businesses;
    final RetryBusinessServerService businessServers;
    final RetryCountryCodeService countryCodes;
    /* TODO
    final RetryCreditCardProcessorService creditCardProcessors;
    final RetryCreditCardTransactionService creditCardTransactions;
    final RetryCreditCardService creditCards;
     */
    final RetryCvsRepositoryService cvsRepositories;
    final RetryDisableLogService disableLogs;
    /*
    final RetryDistroFileTypeService distroFileTypes;
    final RetryDistroFileService distroFiles;
    final RetryDNSForbiddenZoneService dnsForbiddenZones;
    final RetryDNSRecordService dnsRecords;
    final RetryDNSTLDService dnsTLDs;
    final RetryDNSTypeService dnsTypes;
    final RetryDNSZoneService dnsZones;
    final RetryEmailAddressService emailAddresss;
    final RetryEmailAttachmentBlockService emailAttachmentBlocks;
    final RetryEmailAttachmentTypeService emailAttachmentTypes;
    final RetryEmailDomainService emailDomains;
    final RetryEmailForwardingService emailForwardings;
    final RetryEmailListAddressService emailListAddresss;
    final RetryEmailListService emailLists;
    final RetryEmailPipeAddressService emailPipeAddresss;
    final RetryEmailPipeService emailPipes;
    final RetryEmailSmtpRelayTypeService emailSmtpRelayTypes;
    final RetryEmailSmtpRelayService emailSmtpRelays;
    final RetryEmailSmtpSmartHostDomainService emailSmtpSmartHostDomains;
    final RetryEmailSmtpSmartHostService emailSmtpSmartHosts;
    final RetryEmailSpamAssassinIntegrationModeService emailSpamAssassinIntegrationModes;
    final RetryEncryptionKeyService encryptionKeys;
    final RetryExpenseCategoryService expenseCategories;
     */
    final RetryFailoverFileLogService failoverFileLogs;
    final RetryFailoverFileReplicationService failoverFileReplications;
    final RetryFailoverFileScheduleService failoverFileSchedules;
    final RetryFailoverMySQLReplicationService failoverMySQLReplications;
    final RetryFileBackupSettingService fileBackupSettings;
    /* TODO
    final RetryFTPGuestUserService ftpGuestUsers;
     */
    final RetryGroupNameService groupNames;
    /* TODO
    final RetryHttpdBindService httpdBinds;
    final RetryHttpdJBossSiteService httpdJBossSites;
    final RetryHttpdJBossVersionService httpdJBossVersions;
    final RetryHttpdJKCodeService httpdJKCodes;
    final RetryHttpdJKProtocolService httpdJKProtocols;
    final RetryHttpdServerService httpdServers;
    final RetryHttpdSharedTomcatService httpdSharedTomcats;
    final RetryHttpdSiteAuthenticatedLocationService httpdSiteAuthenticatedLocations;
    final RetryHttpdSiteBindService httpdSiteBinds;
    final RetryHttpdSiteURLService httpdSiteURLs;
     */
    final RetryHttpdSiteService httpdSites;
    /* TODO
    final RetryHttpdStaticSiteService httpdStaticSites;
    final RetryHttpdTomcatContextService httpdTomcatContexts;
    final RetryHttpdTomcatDataSourceService httpdTomcatDataSources;
    final RetryHttpdTomcatParameterService httpdTomcatParameters;
    final RetryHttpdTomcatSiteService httpdTomcatSites;
    final RetryHttpdTomcatSharedSiteService httpdTomcatSharedSites;
    final RetryHttpdTomcatStdSiteService httpdTomcatStdSites;
    final RetryHttpdTomcatVersionService httpdTomcatVersions;
    final RetryHttpdWorkerService httpdWorkers;
    */
    final RetryIPAddressService ipAddresses;
    final RetryLanguageService languages;
    /* TODO
    final RetryLinuxAccAddressService linuxAccAddresss;
     */
    final RetryLinuxAccountGroupService linuxAccountGroups;
    final RetryLinuxAccountTypeService linuxAccountTypes;
    final RetryLinuxAccountService linuxAccounts;
    final RetryLinuxGroupTypeService linuxGroupTypes;
    final RetryLinuxGroupService linuxGroups;
    // TODO: final RetryMajordomoListService majordomoLists;
    // TODO: final RetryMajordomoServerService majordomoServers;
    // TODO: final RetryMajordomoVersionService majordomoVersions;
    // TODO: final RetryMasterHistoryService masterHistories;
    // TODO: final RetryMasterHostService masterHosts;
    // TODO: final RetryMasterServerService masterServers;
    // TODO: final RetryMasterUserService masterUsers;
    // TODO: final RetryMonthlyChargeService monthlyCharges;
    final RetryMySQLDatabaseService mysqlDatabases;
    final RetryMySQLDBUserService mysqlDBUsers;
    final RetryMySQLServerService mysqlServers;
    final RetryMySQLUserService mysqlUsers;
    final RetryNetBindService netBinds;
    final RetryNetDeviceIDService netDeviceIDs;
    final RetryNetDeviceService netDevices;
    final RetryNetProtocolService netProtocols;
    final RetryNetTcpRedirectService netTcpRedirects;
    /* TODO
    final RetryNoticeLogService noticeLogs;
    final RetryNoticeTypeService noticeTypes;
    */
    final RetryOperatingSystemVersionService operatingSystemVersions;
    final RetryOperatingSystemService operatingSystems;
    final RetryPackageCategoryService packageCategories;
    /* TODO
    final RetryPackageDefinitionLimitService packageDefinitionLimits;
    final RetryPackageDefinitionService packageDefinitions;
    final RetryPaymentTypeService paymentTypes;
    final RetryPhysicalServerService physicalServers;
     */
    final RetryPostgresDatabaseService postgresDatabases;
    final RetryPostgresEncodingService postgresEncodings;
    final RetryPostgresServerService postgresServers;
    final RetryPostgresUserService postgresUsers;
    final RetryPostgresVersionService postgresVersions;
    // TODO: final RetryPrivateFTPServerService privateFTPServers;
    // TODO: final RetryProcessorTypeService processorTypes;
    final RetryProtocolService protocols;
    // TODO: final RetryRackService racks;
    // TODO: final RetryResellerService resellers;
    final RetryResourceTypeService resourceTypes;
    final RetryResourceService resources;
    final RetryServerFarmService serverFarms;
    final RetryServerResourceService serverResources;
    final RetryServerService servers;
    final RetryShellService shells;
    /* TODO
    final RetrySignupRequestOptionService signupRequestOptions;
    final RetrySignupRequestService signupRequests;
    final RetrySpamEmailMessageService spamEmailMessages;
    final RetrySystemEmailAliasService systemEmailAliass;
     */
    final RetryTechnologyService technologies;
    final RetryTechnologyClassService technologyClasses;
    final RetryTechnologyNameService technologyNames;
    final RetryTechnologyVersionService technologyVersions;
    /* TODO
    final RetryTicketActionTypeService ticketActionTypes;
    final RetryTicketActionService ticketActions;
    final RetryTicketAssignmentService ticketAssignments;
    final RetryTicketBrandCategoryService ticketBrandCategories;
    */
    final RetryTicketCategoryService ticketCategories;
    final RetryTicketPriorityService ticketPriorities;
    final RetryTicketStatusService ticketStatuses;
    final RetryTicketTypeService ticketTypes;
    /* TODO
    final RetryTicketService tickets;
    */
    final RetryTimeZoneService timeZones;
    /* TODO
    final RetryTransactionTypeService transactionTypes;
    final RetryTransactionService transactions;
    final RetryUSStateService usStates;
     */
    final RetryUsernameService usernames;
    /* TODO
    final RetryVirtualDiskService virtualDisks;
    final RetryVirtualServerService virtualServers;
    final RetryWhoisHistoryService whoisHistories;
     */

    final Object connectionLock = new Object();
    private AOServConnector<?,?> wrapped;

    RetryConnector(RetryConnectorFactory factory, Locale locale, UserId connectAs, UserId authenticateAs, String password, DomainName daemonServer) throws RemoteException, LoginException {
        this.factory = factory;
        this.locale = locale;
        this.connectAs = connectAs;
        this.authenticateAs = authenticateAs;
        this.password = password;
        this.daemonServer = daemonServer;
        aoserverDaemonHosts = new RetryAOServerDaemonHostService(this);
        aoserverResources = new RetryAOServerResourceService(this);
        aoservers = new RetryAOServerService(this);
        aoservPermissions = new RetryAOServPermissionService(this);
        /* TODO
        aoservProtocols = new RetryAOServProtocolService(this);
        aoshCommands = new RetryAOSHCommandService(this);
         */
        architectures = new RetryArchitectureService(this);
        backupPartitions = new RetryBackupPartitionService(this);
        backupRetentions = new RetryBackupRetentionService(this);
        /* TODO
        bankAccounts = new RetryBankAccountService(this);
        bankTransactionTypes = new RetryBankTransactionTypeService(this);
        bankTransactions = new RetryBankTransactionService(this);
        banks = new RetryBankService(this);
        blackholeEmailAddresss = new RetryBlackholeEmailAddressService(this);
        brands = new RetryBrandService(this);
         */
        businessAdministrators = new RetryBusinessAdministratorService(this);
        /* TODO
        businessAdministratorPermissions = new RetryBusinessAdministratorPermissionService(this);
        businessProfiles = new RetryBusinessProfileService(this);
         */
        businesses = new RetryBusinessService(this);
        businessServers = new RetryBusinessServerService(this);
        countryCodes = new RetryCountryCodeService(this);
        /* TODO
        creditCardProcessors = new RetryCreditCardProcessorService(this);
        creditCardTransactions = new RetryCreditCardTransactionService(this);
        creditCards = new RetryCreditCardService(this);
         */
        cvsRepositories = new RetryCvsRepositoryService(this);
        disableLogs = new RetryDisableLogService(this);
        /*
        distroFileTypes = new RetryDistroFileTypeService(this);
        distroFiles = new RetryDistroFileService(this);
        dnsForbiddenZones = new RetryDNSForbiddenZoneService(this);
        dnsRecords = new RetryDNSRecordService(this);
        dnsTLDs = new RetryDNSTLDService(this);
        dnsTypes = new RetryDNSTypeService(this);
        dnsZones = new RetryDNSZoneService(this);
        emailAddresss = new RetryEmailAddressService(this);
        emailAttachmentBlocks = new RetryEmailAttachmentBlockService(this);
        emailAttachmentTypes = new RetryEmailAttachmentTypeService(this);
        emailDomains = new RetryEmailDomainService(this);
        emailForwardings = new RetryEmailForwardingService(this);
        emailListAddresss = new RetryEmailListAddressService(this);
        emailLists = new RetryEmailListService(this);
        emailPipeAddresss = new RetryEmailPipeAddressService(this);
        emailPipes = new RetryEmailPipeService(this);
        emailSmtpRelayTypes = new RetryEmailSmtpRelayTypeService(this);
        emailSmtpRelays = new RetryEmailSmtpRelayService(this);
        emailSmtpSmartHostDomains = new RetryEmailSmtpSmartHostDomainService(this);
        emailSmtpSmartHosts = new RetryEmailSmtpSmartHostService(this);
        emailSpamAssassinIntegrationModes = new RetryEmailSpamAssassinIntegrationModeService(this);
        encryptionKeys = new RetryEncryptionKeyService(this);
        expenseCategories = new RetryExpenseCategoryService(this);
         */
        failoverFileLogs = new RetryFailoverFileLogService(this);
        failoverFileReplications = new RetryFailoverFileReplicationService(this);
        failoverFileSchedules = new RetryFailoverFileScheduleService(this);
        failoverMySQLReplications = new RetryFailoverMySQLReplicationService(this);
        fileBackupSettings = new RetryFileBackupSettingService(this);
        /* TODO
        ftpGuestUsers = new RetryFTPGuestUserService(this);
         */
        groupNames = new RetryGroupNameService(this);
        /* TODO
        httpdBinds = new RetryHttpdBindService(this);
        httpdJBossSites = new RetryHttpdJBossSiteService(this);
        httpdJBossVersions = new RetryHttpdJBossVersionService(this);
        httpdJKCodes = new RetryHttpdJKCodeService(this);
        httpdJKProtocols = new RetryHttpdJKProtocolService(this);
        httpdServers = new RetryHttpdServerService(this);
        httpdSharedTomcats = new RetryHttpdSharedTomcatService(this);
        httpdSiteAuthenticatedLocations = new RetryHttpdSiteAuthenticatedLocationService(this);
        httpdSiteBinds = new RetryHttpdSiteBindService(this);
        httpdSiteURLs = new RetryHttpdSiteURLService(this);
         */
        httpdSites = new RetryHttpdSiteService(this);
        /* TODO
        httpdStaticSites = new RetryHttpdStaticSiteService(this);
        httpdTomcatContexts = new RetryHttpdTomcatContextService(this);
        httpdTomcatDataSources = new RetryHttpdTomcatDataSourceService(this);
        httpdTomcatParameters = new RetryHttpdTomcatParameterService(this);
        httpdTomcatSites = new RetryHttpdTomcatSiteService(this);
        httpdTomcatSharedSites = new RetryHttpdTomcatSharedSiteService(this);
        httpdTomcatStdSites = new RetryHttpdTomcatStdSiteService(this);
        httpdTomcatVersions = new RetryHttpdTomcatVersionService(this);
        httpdWorkers = new RetryHttpdWorkerService(this);
        */
        ipAddresses = new RetryIPAddressService(this);
        languages = new RetryLanguageService(this);
        // TODO: linuxAccAddresss = new RetryLinuxAccAddressService(this);
        linuxAccountGroups = new RetryLinuxAccountGroupService(this);
        linuxAccountTypes = new RetryLinuxAccountTypeService(this);
        linuxAccounts = new RetryLinuxAccountService(this);
        linuxGroupTypes = new RetryLinuxGroupTypeService(this);
        linuxGroups = new RetryLinuxGroupService(this);
        // TODO: majordomoLists = new RetryMajordomoListService(this);
        // TODO: majordomoServers = new RetryMajordomoServerService(this);
        // TODO: majordomoVersions = new RetryMajordomoVersionService(this);
        // TODO: masterHistories = new RetryMasterHistoryService(this);
        // TODO: masterHosts = new RetryMasterHostService(this);
        // TODO: masterServers = new RetryMasterServerService(this);
        // TODO: masterUsers = new RetryMasterUserService(this);
        // TODO: monthlyCharges = new RetryMonthlyChargeService(this);
        mysqlDatabases = new RetryMySQLDatabaseService(this);
        mysqlDBUsers = new RetryMySQLDBUserService(this);
        mysqlServers = new RetryMySQLServerService(this);
        mysqlUsers = new RetryMySQLUserService(this);
        netBinds = new RetryNetBindService(this);
        netDeviceIDs = new RetryNetDeviceIDService(this);
        netDevices = new RetryNetDeviceService(this);
        netProtocols = new RetryNetProtocolService(this);
        netTcpRedirects = new RetryNetTcpRedirectService(this);
        /* TODO
        noticeLogs = new RetryNoticeLogService(this);
        noticeTypes = new RetryNoticeTypeService(this);
        */
        operatingSystemVersions = new RetryOperatingSystemVersionService(this);
        operatingSystems = new RetryOperatingSystemService(this);
        packageCategories = new RetryPackageCategoryService(this);
        /* TODO
        packageDefinitionLimits = new RetryPackageDefinitionLimitService(this);
        packageDefinitions = new RetryPackageDefinitionService(this);
        paymentTypes = new RetryPaymentTypeService(this);
        physicalServers = new RetryPhysicalServerService(this);
         */
        postgresDatabases = new RetryPostgresDatabaseService(this);
        postgresEncodings = new RetryPostgresEncodingService(this);
        postgresServers = new RetryPostgresServerService(this);
        postgresUsers = new RetryPostgresUserService(this);
        postgresVersions = new RetryPostgresVersionService(this);
        // TODO: privateFTPServers = new RetryPrivateFTPServerService(this);
        // TODO: processorTypes = new RetryProcessorTypeService(this);
        protocols = new RetryProtocolService(this);
        /* TODO
        racks = new RetryRackService(this);
        resellers = new RetryResellerService(this);
         */
        resourceTypes = new RetryResourceTypeService(this);
        resources = new RetryResourceService(this);
        serverFarms = new RetryServerFarmService(this);
        serverResources = new RetryServerResourceService(this);
        servers = new RetryServerService(this);
        shells = new RetryShellService(this);
        /* TODO
        signupRequestOptions = new RetrySignupRequestOptionService(this);
        signupRequests = new RetrySignupRequestService(this);
        spamEmailMessages = new RetrySpamEmailMessageService(this);
        systemEmailAliass = new RetrySystemEmailAliasService(this);
         */
        technologies = new RetryTechnologyService(this);
        technologyClasses = new RetryTechnologyClassService(this);
        technologyNames = new RetryTechnologyNameService(this);
        technologyVersions = new RetryTechnologyVersionService(this);
        /* TODO
        ticketActionTypes = new RetryTicketActionTypeService(this);
        ticketActions = new RetryTicketActionService(this);
        ticketAssignments = new RetryTicketAssignmentService(this);
        ticketBrandCategories = new RetryTicketBrandCategoryService(this);
        */
        ticketCategories = new RetryTicketCategoryService(this);
        ticketPriorities = new RetryTicketPriorityService(this);
        ticketStatuses = new RetryTicketStatusService(this);
        ticketTypes = new RetryTicketTypeService(this);
        /* TODO
        tickets = new RetryTicketService(this);
        */
        timeZones = new RetryTimeZoneService(this);
        /* TODO
        transactionTypes = new RetryTransactionTypeService(this);
        transactions = new RetryTransactionService(this);
        usStates = new RetryUSStateService(this);
         */
        usernames = new RetryUsernameService(this);
        /* TODO
        virtualDisks = new RetryVirtualDiskService(this);
        virtualServers = new RetryVirtualServerService(this);
        whoisHistories = new RetryWhoisHistoryService(this);
         */
        // Connect immediately in order to have the chance to throw exceptions that will occur during connection
        synchronized(connectionLock) {
            connect();
        }
    }

    @SuppressWarnings("unchecked")
    void connect() throws RemoteException, LoginException {
        assert Thread.holdsLock(connectionLock);

        // Connect to the remote registry and get each of the stubs
        AOServConnector<?,?> newWrapped = factory.wrapped.newConnector(locale, connectAs, authenticateAs, password, daemonServer);

        // Now that each stub has been successfully received, store as the current connection
        this.wrapped = newWrapped;
        for(ServiceName serviceName : ServiceName.values) {
            ((RetryService)getServices().get(serviceName)).wrapped = newWrapped.getServices().get(serviceName);
        }
    }

    /**
     * Disconnects if appropriate for the provided type of RemoteException.
     * TODO: Clear all caches on disconnect, how to signal outer cache layers?
     */
    void disconnectIfNeeded(Throwable err) {
        while(err!=null) {
            if(
                (err instanceof NoSuchObjectException)
                || (err instanceof ConnectException)
                || (err instanceof MarshalException)
            ) {
                try {
                    disconnect();
                } catch(RemoteException err2) {
                    logger.log(Level.SEVERE, null, err2);
                }
                break;
            }
            err = err.getCause();
        }
    }

    /**
     * Disconnects this client.  The client will automatically reconnect on the next use.
     */
    void disconnect() throws RemoteException {
        synchronized(connectionLock) {
            wrapped = null;
            for(AOServService<RetryConnector,RetryConnectorFactory,?,?> service : getServices().values()) {
                ((RetryService<?,?>)service).wrapped = null;
            }
        }
    }

    AOServConnector<?,?> getWrapped() throws RemoteException {
        synchronized(connectionLock) {
            if(wrapped==null) {
                try {
                    connect();
                } catch(Exception err) {
                    throw new RemoteException(err.getMessage(), err);
                }
            }
            return wrapped;
        }
    }

    <T> T retry(Callable<T> callable) throws RemoteException, NoSuchElementException {
        int attempt = 1;
        while(!Thread.interrupted()) {
            if(factory.timeout>0) {
                Future<T> future = RetryUtils.executorService.submit(callable);
                try {
                    return future.get(factory.timeout, factory.unit);
                } catch(RuntimeException err) {
                    disconnectIfNeeded(err);
                    if(Thread.interrupted() || attempt>=RetryUtils.RETRY_ATTEMPTS || RetryUtils.isImmediateFail(err)) throw err;
                } catch(ExecutionException err) {
                    disconnectIfNeeded(err);
                    if(Thread.interrupted() || attempt>=RetryUtils.RETRY_ATTEMPTS || RetryUtils.isImmediateFail(err)) {
                        Throwable cause = err.getCause();
                        if(cause instanceof RemoteException) throw (RemoteException)cause;
                        if(cause instanceof NoSuchElementException) throw (NoSuchElementException)cause;
                        throw new RemoteException(err.getMessage(), err);
                    }
                } catch(TimeoutException err) {
                    future.cancel(true);
                    disconnectIfNeeded(err);
                    if(Thread.interrupted() || attempt>=RetryUtils.RETRY_ATTEMPTS || RetryUtils.isImmediateFail(err)) throw new RemoteException(err.getMessage(), err);
                } catch(Exception err) {
                    disconnectIfNeeded(err);
                    if(Thread.interrupted() || attempt>=RetryUtils.RETRY_ATTEMPTS || RetryUtils.isImmediateFail(err)) throw new RemoteException(err.getMessage(), err);
                }
            } else {
                try {
                    return callable.call();
                } catch(RuntimeException err) {
                    disconnectIfNeeded(err);
                    if(Thread.interrupted() || attempt>=RetryUtils.RETRY_ATTEMPTS || RetryUtils.isImmediateFail(err)) throw err;
                } catch(RemoteException err) {
                    disconnectIfNeeded(err);
                    if(Thread.interrupted() || attempt>=RetryUtils.RETRY_ATTEMPTS || RetryUtils.isImmediateFail(err)) throw err;
                } catch(Exception err) {
                    disconnectIfNeeded(err);
                    if(Thread.interrupted() || attempt>=RetryUtils.RETRY_ATTEMPTS || RetryUtils.isImmediateFail(err)) throw new RemoteException(err.getMessage(), err);
                }
            }
            try {
                Thread.sleep(RetryUtils.retryAttemptDelays[attempt-1]);
            } catch(InterruptedException err) {
                throw new RemoteException(err.getMessage(), err);
            }
            attempt++;
        }
        throw new RemoteException("interrupted", new InterruptedException("interrupted"));
    }

    public RetryConnectorFactory getFactory() {
        return factory;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(final Locale locale) throws RemoteException {
        if(!this.locale.equals(locale)) {
            this.locale = locale;
            retry(
                new Callable<Object>() {
                    public Object call() throws RemoteException {
                        getWrapped().setLocale(locale);
                        return null;
                    }
                }
            );
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

    private final AtomicReference<Map<ServiceName,AOServService<RetryConnector,RetryConnectorFactory,?,?>>> tables = new AtomicReference<Map<ServiceName,AOServService<RetryConnector,RetryConnectorFactory,?,?>>>();
    public Map<ServiceName,AOServService<RetryConnector,RetryConnectorFactory,?,?>> getServices() throws RemoteException {
        Map<ServiceName,AOServService<RetryConnector,RetryConnectorFactory,?,?>> ts = tables.get();
        if(ts==null) {
            ts = AOServConnectorUtils.createServiceMap(this);
            if(!tables.compareAndSet(null, ts)) ts = tables.get();
        }
        return ts;
    }

    public AOServerDaemonHostService<RetryConnector,RetryConnectorFactory> getAoServerDaemonHosts() {
        return aoserverDaemonHosts;
    }

    public AOServerResourceService<RetryConnector,RetryConnectorFactory> getAoServerResources() {
        return aoserverResources;
    }

    public AOServerService<RetryConnector,RetryConnectorFactory> getAoServers() {
        return aoservers;
    }

    public AOServPermissionService<RetryConnector,RetryConnectorFactory> getAoservPermissions() {
        return aoservPermissions;
    }
    /* TODO
    public AOServProtocolService<RetryConnector,RetryConnectorFactory> getAoservProtocols();

    public AOSHCommandService<RetryConnector,RetryConnectorFactory> getAoshCommands();
    */
    public ArchitectureService<RetryConnector,RetryConnectorFactory> getArchitectures() {
        return architectures;
    }

    public BackupPartitionService<RetryConnector,RetryConnectorFactory> getBackupPartitions() {
        return backupPartitions;
    }

    public BackupRetentionService<RetryConnector,RetryConnectorFactory> getBackupRetentions() {
        return backupRetentions;
    }
    /* TODO
    public BankAccountService<RetryConnector,RetryConnectorFactory> getBankAccounts();

    public BankTransactionTypeService<RetryConnector,RetryConnectorFactory> getBankTransactionTypes();

    public BankTransactionService<RetryConnector,RetryConnectorFactory> getBankTransactions();

    public BankService<RetryConnector,RetryConnectorFactory> getBanks();

    public BlackholeEmailAddressService<RetryConnector,RetryConnectorFactory> getBlackholeEmailAddresses();

    public BrandService<RetryConnector,RetryConnectorFactory> getBrands();
     */
    public BusinessAdministratorService<RetryConnector,RetryConnectorFactory> getBusinessAdministrators() {
        return businessAdministrators;
    }
    /*
    public BusinessAdministratorPermissionService<RetryConnector,RetryConnectorFactory> getBusinessAdministratorPermissions();

    public BusinessProfileService<RetryConnector,RetryConnectorFactory> getBusinessProfiles();
     */
    public BusinessService<RetryConnector,RetryConnectorFactory> getBusinesses() {
        return businesses;
    }

    public BusinessServerService<RetryConnector,RetryConnectorFactory> getBusinessServers() {
        return businessServers;
    }

    public CountryCodeService<RetryConnector,RetryConnectorFactory> getCountryCodes() {
        return countryCodes;
    }
    /* TODO
    public CreditCardProcessorService<RetryConnector,RetryConnectorFactory> getCreditCardProcessors();

    public CreditCardTransactionService<RetryConnector,RetryConnectorFactory> getCreditCardTransactions();

    public CreditCardService<RetryConnector,RetryConnectorFactory> getCreditCards();
     */
    public CvsRepositoryService<RetryConnector,RetryConnectorFactory> getCvsRepositories() {
        return cvsRepositories;
    }

    public DisableLogService<RetryConnector,RetryConnectorFactory> getDisableLogs() {
        return disableLogs;
    }
    /*
    public DistroFileTypeService<RetryConnector,RetryConnectorFactory> getDistroFileTypes();

    public DistroFileService<RetryConnector,RetryConnectorFactory> getDistroFiles();

    public DNSForbiddenZoneService<RetryConnector,RetryConnectorFactory> getDnsForbiddenZones();

    public DNSRecordService<RetryConnector,RetryConnectorFactory> getDnsRecords();

    public DNSTLDService<RetryConnector,RetryConnectorFactory> getDnsTLDs();

    public DNSTypeService<RetryConnector,RetryConnectorFactory> getDnsTypes();

    public DNSZoneService<RetryConnector,RetryConnectorFactory> getDnsZones();

    public EmailAddressService<RetryConnector,RetryConnectorFactory> getEmailAddresses();

    public EmailAttachmentBlockService<RetryConnector,RetryConnectorFactory> getEmailAttachmentBlocks();

    public EmailAttachmentTypeService<RetryConnector,RetryConnectorFactory> getEmailAttachmentTypes();

    public EmailDomainService<RetryConnector,RetryConnectorFactory> getEmailDomains();

    public EmailForwardingService<RetryConnector,RetryConnectorFactory> getEmailForwardings();

    public EmailListAddressService<RetryConnector,RetryConnectorFactory> getEmailListAddresses();

    public EmailListService<RetryConnector,RetryConnectorFactory> getEmailLists();

    public EmailPipeAddressService<RetryConnector,RetryConnectorFactory> getEmailPipeAddresses();

    public EmailPipeService<RetryConnector,RetryConnectorFactory> getEmailPipes();

    public EmailSmtpRelayTypeService<RetryConnector,RetryConnectorFactory> getEmailSmtpRelayTypes();

    public EmailSmtpRelayService<RetryConnector,RetryConnectorFactory> getEmailSmtpRelays();

    public EmailSmtpSmartHostDomainService<RetryConnector,RetryConnectorFactory> getEmailSmtpSmartHostDomains();

    public EmailSmtpSmartHostService<RetryConnector,RetryConnectorFactory> getEmailSmtpSmartHosts();

    public EmailSpamAssassinIntegrationModeService<RetryConnector,RetryConnectorFactory> getEmailSpamAssassinIntegrationModes();

    public EncryptionKeyService<RetryConnector,RetryConnectorFactory> getEncryptionKeys();

    public ExpenseCategoryService<RetryConnector,RetryConnectorFactory> getExpenseCategories();
    */
    public FailoverFileLogService<RetryConnector,RetryConnectorFactory> getFailoverFileLogs() {
        return failoverFileLogs;
    }

    public FailoverFileReplicationService<RetryConnector,RetryConnectorFactory> getFailoverFileReplications() {
        return failoverFileReplications;
    }

    public FailoverFileScheduleService<RetryConnector,RetryConnectorFactory> getFailoverFileSchedules() {
        return failoverFileSchedules;
    }

    public FailoverMySQLReplicationService<RetryConnector,RetryConnectorFactory> getFailoverMySQLReplications() {
        return failoverMySQLReplications;
    }

    public FileBackupSettingService<RetryConnector,RetryConnectorFactory> getFileBackupSettings() {
        return fileBackupSettings;
    }
    /* TODO
    public FTPGuestUserService<RetryConnector,RetryConnectorFactory> getFtpGuestUsers();
     */
    public GroupNameService<RetryConnector,RetryConnectorFactory> getGroupNames() {
        return groupNames;
    }
    /* TODO
    public HttpdBindService<RetryConnector,RetryConnectorFactory> getHttpdBinds();

    public HttpdJBossSiteService<RetryConnector,RetryConnectorFactory> getHttpdJBossSites();

    public HttpdJBossVersionService<RetryConnector,RetryConnectorFactory> getHttpdJBossVersions();

    public HttpdJKCodeService<RetryConnector,RetryConnectorFactory> getHttpdJKCodes();

    public HttpdJKProtocolService<RetryConnector,RetryConnectorFactory> getHttpdJKProtocols();

    public HttpdServerService<RetryConnector,RetryConnectorFactory> getHttpdServers();

    public HttpdSharedTomcatService<RetryConnector,RetryConnectorFactory> getHttpdSharedTomcats();

    public HttpdSiteAuthenticatedLocationService<RetryConnector,RetryConnectorFactory> getHttpdSiteAuthenticatedLocations();

    public HttpdSiteBindService<RetryConnector,RetryConnectorFactory> getHttpdSiteBinds();

    public HttpdSiteURLService<RetryConnector,RetryConnectorFactory> getHttpdSiteURLs();
    */
    public HttpdSiteService<RetryConnector,RetryConnectorFactory> getHttpdSites() {
        return httpdSites;
    }
    /* TODO
    public HttpdStaticSiteService<RetryConnector,RetryConnectorFactory> getHttpdStaticSites();

    public HttpdTomcatContextService<RetryConnector,RetryConnectorFactory> getHttpdTomcatContexts();

    public HttpdTomcatDataSourceService<RetryConnector,RetryConnectorFactory> getHttpdTomcatDataSources();

    public HttpdTomcatParameterService<RetryConnector,RetryConnectorFactory> getHttpdTomcatParameters();

    public HttpdTomcatSiteService<RetryConnector,RetryConnectorFactory> getHttpdTomcatSites();

    public HttpdTomcatSharedSiteService<RetryConnector,RetryConnectorFactory> getHttpdTomcatSharedSites();

    public HttpdTomcatStdSiteService<RetryConnector,RetryConnectorFactory> getHttpdTomcatStdSites();

    public HttpdTomcatVersionService<RetryConnector,RetryConnectorFactory> getHttpdTomcatVersions();

    public HttpdWorkerService<RetryConnector,RetryConnectorFactory> getHttpdWorkers();
    */
    public IPAddressService<RetryConnector,RetryConnectorFactory> getIpAddresses() {
        return ipAddresses;
    }

    public LanguageService<RetryConnector,RetryConnectorFactory> getLanguages() {
        return languages;
    }

    // TODO: public LinuxAccAddressService<RetryConnector,RetryConnectorFactory> getLinuxAccAddresses();

    public LinuxAccountGroupService<RetryConnector,RetryConnectorFactory> getLinuxAccountGroups() {
        return linuxAccountGroups;
    }

    public LinuxAccountTypeService<RetryConnector,RetryConnectorFactory> getLinuxAccountTypes() {
        return linuxAccountTypes;
    }

    public LinuxAccountService<RetryConnector,RetryConnectorFactory> getLinuxAccounts() {
        return linuxAccounts;
    }

    public LinuxGroupTypeService<RetryConnector,RetryConnectorFactory> getLinuxGroupTypes() {
        return linuxGroupTypes;
    }

    public LinuxGroupService<RetryConnector,RetryConnectorFactory> getLinuxGroups() {
        return linuxGroups;
    }

    // TODO: public MajordomoListService<RetryConnector,RetryConnectorFactory> getMajordomoLists();

    // TODO: public MajordomoServerService<RetryConnector,RetryConnectorFactory> getMajordomoServers();

    // TODO: public MajordomoVersionService<RetryConnector,RetryConnectorFactory> getMajordomoVersions();

    // TODO: public MasterHistoryService<RetryConnector,RetryConnectorFactory> getMasterHistory();

    // TODO: public MasterHostService<RetryConnector,RetryConnectorFactory> getMasterHosts();

    // TODO: public MasterServerService<RetryConnector,RetryConnectorFactory> getMasterServers();

    // TODO: public MasterUserService<RetryConnector,RetryConnectorFactory> getMasterUsers();

    // TODO: public MonthlyChargeService<RetryConnector,RetryConnectorFactory> getMonthlyCharges();

    public MySQLDatabaseService<RetryConnector,RetryConnectorFactory> getMysqlDatabases() {
        return mysqlDatabases;
    }

    public MySQLDBUserService<RetryConnector,RetryConnectorFactory> getMysqlDBUsers() {
        return mysqlDBUsers;
    }

    public MySQLServerService<RetryConnector,RetryConnectorFactory> getMysqlServers() {
        return mysqlServers;
    }

    public MySQLUserService<RetryConnector,RetryConnectorFactory> getMysqlUsers() {
        return mysqlUsers;
    }

    public NetBindService<RetryConnector,RetryConnectorFactory> getNetBinds() {
        return netBinds;
    }

    public NetDeviceIDService<RetryConnector,RetryConnectorFactory> getNetDeviceIDs() {
        return netDeviceIDs;
    }

    public NetDeviceService<RetryConnector,RetryConnectorFactory> getNetDevices() {
        return netDevices;
    }

    public NetProtocolService<RetryConnector,RetryConnectorFactory> getNetProtocols() {
        return netProtocols;
    }

    public NetTcpRedirectService<RetryConnector,RetryConnectorFactory> getNetTcpRedirects() {
        return netTcpRedirects;
    }
    /* TODO
    public NoticeLogService<RetryConnector,RetryConnectorFactory> getNoticeLogs();

    public NoticeTypeService<RetryConnector,RetryConnectorFactory> getNoticeTypes();
    */
    public OperatingSystemVersionService<RetryConnector,RetryConnectorFactory> getOperatingSystemVersions() {
        return operatingSystemVersions;
    }

    public OperatingSystemService<RetryConnector,RetryConnectorFactory> getOperatingSystems() {
        return operatingSystems;
    }

    public PackageCategoryService<RetryConnector,RetryConnectorFactory> getPackageCategories() {
        return packageCategories;
    }
    /*
    public PackageDefinitionLimitService<RetryConnector,RetryConnectorFactory> getPackageDefinitionLimits();

    public PackageDefinitionService<RetryConnector,RetryConnectorFactory> getPackageDefinitions();

    public PaymentTypeService<RetryConnector,RetryConnectorFactory> getPaymentTypes();

    public PhysicalServerService<RetryConnector,RetryConnectorFactory> getPhysicalServers();
    */
    public PostgresDatabaseService<RetryConnector,RetryConnectorFactory> getPostgresDatabases() {
        return postgresDatabases;
    }

    public PostgresEncodingService<RetryConnector,RetryConnectorFactory> getPostgresEncodings() {
        return postgresEncodings;
    }

    public PostgresServerService<RetryConnector,RetryConnectorFactory> getPostgresServers() {
        return postgresServers;
    }

    public PostgresUserService<RetryConnector,RetryConnectorFactory> getPostgresUsers() {
        return postgresUsers;
    }

    public PostgresVersionService<RetryConnector,RetryConnectorFactory> getPostgresVersions() {
        return postgresVersions;
    }

    // TODO: public PrivateFTPServerService<RetryConnector,RetryConnectorFactory> getPrivateFTPServers();

    // TODO: public ProcessorTypeService<RetryConnector,RetryConnectorFactory> getProcessorTypes();

    public ProtocolService<RetryConnector,RetryConnectorFactory> getProtocols() {
        return protocols;
    }
    /* TODO
    public RackService<RetryConnector,RetryConnectorFactory> getRacks();

    public ResellerService<RetryConnector,RetryConnectorFactory> getResellers();
    */
    public ResourceTypeService<RetryConnector,RetryConnectorFactory> getResourceTypes() {
        return resourceTypes;
    }

    public ResourceService<RetryConnector,RetryConnectorFactory> getResources() {
        return resources;
    }

    public ServerFarmService<RetryConnector,RetryConnectorFactory> getServerFarms() {
        return serverFarms;
    }

    public ServerResourceService<RetryConnector,RetryConnectorFactory> getServerResources() {
        return serverResources;
    }

    public ServerService<RetryConnector,RetryConnectorFactory> getServers() {
        return servers;
    }

    public ShellService<RetryConnector,RetryConnectorFactory> getShells() {
        return shells;
    }
    /* TODO
    public SignupRequestOptionService<RetryConnector,RetryConnectorFactory> getSignupRequestOptions();

    public SignupRequestService<RetryConnector,RetryConnectorFactory> getSignupRequests();

    public SpamEmailMessageService<RetryConnector,RetryConnectorFactory> getSpamEmailMessages();

    public SystemEmailAliasService<RetryConnector,RetryConnectorFactory> getSystemEmailAliases();
    */
    public TechnologyService<RetryConnector,RetryConnectorFactory> getTechnologies() {
        return technologies;
    }

    public TechnologyClassService<RetryConnector,RetryConnectorFactory> getTechnologyClasses() {
        return technologyClasses;
    }

    public TechnologyNameService<RetryConnector,RetryConnectorFactory> getTechnologyNames() {
        return technologyNames;
    }

    public TechnologyVersionService<RetryConnector,RetryConnectorFactory> getTechnologyVersions() {
        return technologyVersions;
    }
    /* TODO
    public TicketActionTypeService<RetryConnector,RetryConnectorFactory> getTicketActionTypes();

    public TicketActionService<RetryConnector,RetryConnectorFactory> getTicketActions();

    public TicketAssignmentService<RetryConnector,RetryConnectorFactory> getTicketAssignments();

    public TicketBrandCategoryService<RetryConnector,RetryConnectorFactory> getTicketBrandCategories();
    */
    public TicketCategoryService<RetryConnector,RetryConnectorFactory> getTicketCategories() {
        return ticketCategories;
    }

    public TicketPriorityService<RetryConnector,RetryConnectorFactory> getTicketPriorities() {
        return ticketPriorities;
    }

    public TicketStatusService<RetryConnector,RetryConnectorFactory> getTicketStatuses() {
        return ticketStatuses;
    }

    public TicketTypeService<RetryConnector,RetryConnectorFactory> getTicketTypes() {
        return ticketTypes;
    }
    /* TODO
    public TicketService<RetryConnector,RetryConnectorFactory> getTickets();
    */
    public TimeZoneService<RetryConnector,RetryConnectorFactory> getTimeZones() {
        return timeZones;
    }
    /* TODO
    public TransactionTypeService<RetryConnector,RetryConnectorFactory> getTransactionTypes();

    public TransactionService<RetryConnector,RetryConnectorFactory> getTransactions();

    public USStateService<RetryConnector,RetryConnectorFactory> getUsStates();
    */
    public UsernameService<RetryConnector,RetryConnectorFactory> getUsernames() {
        return usernames;
    }
    /* TODO
    public VirtualDiskService<RetryConnector,RetryConnectorFactory> getVirtualDisks();

    public VirtualServerService<RetryConnector,RetryConnectorFactory> getVirtualServers();

    public WhoisHistoryService<RetryConnector,RetryConnectorFactory> getWhoisHistory();
 */
}
