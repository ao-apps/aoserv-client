package com.aoindustries.aoserv.client.retry;

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
import java.rmi.ConnectException;
import java.rmi.MarshalException;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.Map;
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
    final String connectAs;
    private final String authenticateAs;
    private final String password;
    private final String daemonServer;
    /* TODO
    final RetryAOServerDaemonHostService aoserverDaemonHosts;
    final RetryAOServerResourceService aoserverResources;
    final RetryAOServerService aoservers;
    final RetryAOServPermissionService aoservPermissions;
    final RetryAOServProtocolService aoservProtocols;
    final RetryAOSHCommandService aoshCommands;
    final RetryArchitectureService architectures;
    final RetryBackupPartitionService backupPartitions;
    final RetryBackupRetentionService backupRetentions;
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
    /* TODO
    final RetryBusinessServerService businessServers;
    final RetryClientJvmProfileService clientJvmProfiles;
    final RetryCountryCodeService countryCodes;
    final RetryCreditCardProcessorService creditCardProcessors;
    final RetryCreditCardTransactionService creditCardTransactions;
    final RetryCreditCardService creditCards;
    final RetryCvsRepositoryService cvsRepositories;
     */
    final RetryDisableLogService disabledLogs;
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
    final RetryFailoverFileLogService failoverFileLogs;
    final RetryFailoverFileReplicationService failoverFileReplications;
    final RetryFailoverFileScheduleService failoverFileSchedules;
    final RetryFailoverMySQLReplicationService failoverMySQLReplications;
    final RetryFileBackupSettingService fileBackupSettings;
    final RetryFTPGuestUserService ftpGuestUsers;
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
    final RetryHttpdSiteService httpdSites;
    final RetryHttpdStaticSiteService httpdStaticSites;
    final RetryHttpdTomcatContextService httpdTomcatContexts;
    final RetryHttpdTomcatDataSourceService httpdTomcatDataSources;
    final RetryHttpdTomcatParameterService httpdTomcatParameters;
    final RetryHttpdTomcatSiteService httpdTomcatSites;
    final RetryHttpdTomcatSharedSiteService httpdTomcatSharedSites;
    final RetryHttpdTomcatStdSiteService httpdTomcatStdSites;
    final RetryHttpdTomcatVersionService httpdTomcatVersions;
    final RetryHttpdWorkerService httpdWorkers;
    final RetryIPAddressService ipAddresss;
    */
    final RetryLanguageService languages;
    /* TODO
    final RetryLinuxAccAddressService linuxAccAddresss;
    final RetryLinuxAccountTypeService linuxAccountTypes;
    final RetryLinuxAccountService linuxAccounts;
    final RetryLinuxGroupAccountService linuxGroupAccounts;
    final RetryLinuxGroupTypeService linuxGroupTypes;
    final RetryLinuxGroupService linuxGroups;
    final RetryLinuxIDService linuxIDs;
    final RetryLinuxServerAccountService linuxServerAccounts;
    final RetryLinuxServerGroupService linuxServerGroups;
    final RetryMajordomoListService majordomoLists;
    final RetryMajordomoServerService majordomoServers;
    final RetryMajordomoVersionService majordomoVersions;
    final RetryMasterHistoryService masterHistories;
    final RetryMasterHostService masterHosts;
    final RetryMasterServerService masterServers;
    final RetryMasterUserService masterUsers;
    final RetryMonthlyChargeService monthlyCharges;
    final RetryMySQLDatabaseService mysqlDatabases;
    final RetryMySQLDBUserService mysqlDBUsers;
    final RetryMySQLReservedWordService mysqlReservedWords;
    final RetryMySQLServerService mysqlServers;
    final RetryMySQLUserService mysqlUsers;
    final RetryNetBindService netBinds;
    final RetryNetDeviceIDService netDeviceIDs;
    final RetryNetDeviceService netDevices;
    final RetryNetPortService netPorts;
    final RetryNetProtocolService netProtocols;
    final RetryNetTcpRedirectService netTcpRedirects;
    final RetryNoticeLogService noticeLogs;
    final RetryNoticeTypeService noticeTypes;
    final RetryOperatingSystemVersionService operatingSystemVersions;
    final RetryOperatingSystemService operatingSystems;
    */
    final RetryPackageCategoryService packageCategories;
    /* TODO
    final RetryPackageDefinitionLimitService packageDefinitionLimits;
    final RetryPackageDefinitionService packageDefinitions;
    final RetryPaymentTypeService paymentTypes;
    final RetryPhysicalServerService physicalServers;
    final RetryPostgresDatabaseService postgresDatabases;
    final RetryPostgresEncodingService postgresEncodings;
    final RetryPostgresReservedWordService postgresReservedWords;
    final RetryPostgresServerUserService postgresServerUsers;
    final RetryPostgresServerService postgresServers;
    final RetryPostgresUserService postgresUsers;
    final RetryPostgresVersionService postgresVersions;
    final RetryPrivateFTPServerService privateFTPServers;
    final RetryProcessorTypeService processorTypes;
    final RetryProtocolService protocols;
    final RetryRackService racks;
    final RetryResellerService resellers;
     */
    final RetryResourceTypeService resourceTypes;
    /* TODO
    final RetryResourceService resources;
    final RetryServerFarmService serverFarms;
    final RetryServerService servers;
    final RetryShellService shells;
    final RetrySignupRequestOptionService signupRequestOptions;
    final RetrySignupRequestService signupRequests;
    final RetrySpamEmailMessageService spamEmailMessages;
    final RetrySystemEmailAliasService systemEmailAliass;
    final RetryTechnologyService technologies;
    final RetryTechnologyClassService technologyClasss;
    final RetryTechnologyNameService technologyNames;
    final RetryTechnologyVersionService technologyVersions;
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
    final RetryUsernameService usernames;
    final RetryVirtualDiskService virtualDisks;
    final RetryVirtualServerService virtualServers;
    final RetryWhoisHistoryService whoisHistories;
     */

    final Object connectionLock = new Object();
    private AOServConnector<?,?> wrapped;

    RetryConnector(RetryConnectorFactory factory, Locale locale, String connectAs, String authenticateAs, String password, String daemonServer) throws RemoteException, LoginException {
        this.factory = factory;
        this.locale = locale;
        this.connectAs = connectAs;
        this.authenticateAs = authenticateAs;
        this.password = password;
        this.daemonServer = daemonServer;
        /* TODO
        aoserverDaemonHosts = new RetryAOServerDaemonHostService(this);
        aoserverResources = new RetryAOServerResourceService(this);
        aoservers = new RetryAOServerService(this);
        aoservPermissions = new RetryAOServPermissionService(this);
        aoservProtocols = new RetryAOServProtocolService(this);
        aoshCommands = new RetryAOSHCommandService(this);
        architectures = new RetryArchitectureService(this);
        backupPartitions = new RetryBackupPartitionService(this);
        backupRetentions = new RetryBackupRetentionService(this);
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
        /* TODO
        businessServers = new RetryBusinessServerService(this);
        clientJvmProfiles = new RetryClientJvmProfileService(this);
        countryCodes = new RetryCountryCodeService(this);
        creditCardProcessors = new RetryCreditCardProcessorService(this);
        creditCardTransactions = new RetryCreditCardTransactionService(this);
        creditCards = new RetryCreditCardService(this);
        cvsRepositories = new RetryCvsRepositoryService(this);
         */
        disabledLogs = new RetryDisableLogService(this);
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
        failoverFileLogs = new RetryFailoverFileLogService(this);
        failoverFileReplications = new RetryFailoverFileReplicationService(this);
        failoverFileSchedules = new RetryFailoverFileScheduleService(this);
        failoverMySQLReplications = new RetryFailoverMySQLReplicationService(this);
        fileBackupSettings = new RetryFileBackupSettingService(this);
        ftpGuestUsers = new RetryFTPGuestUserService(this);
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
        httpdSites = new RetryHttpdSiteService(this);
        httpdStaticSites = new RetryHttpdStaticSiteService(this);
        httpdTomcatContexts = new RetryHttpdTomcatContextService(this);
        httpdTomcatDataSources = new RetryHttpdTomcatDataSourceService(this);
        httpdTomcatParameters = new RetryHttpdTomcatParameterService(this);
        httpdTomcatSites = new RetryHttpdTomcatSiteService(this);
        httpdTomcatSharedSites = new RetryHttpdTomcatSharedSiteService(this);
        httpdTomcatStdSites = new RetryHttpdTomcatStdSiteService(this);
        httpdTomcatVersions = new RetryHttpdTomcatVersionService(this);
        httpdWorkers = new RetryHttpdWorkerService(this);
        ipAddresss = new RetryIPAddressService(this);
        */
        languages = new RetryLanguageService(this);
        /* TODO
        linuxAccAddresss = new RetryLinuxAccAddressService(this);
        linuxAccountTypes = new RetryLinuxAccountTypeService(this);
        linuxAccounts = new RetryLinuxAccountService(this);
        linuxGroupAccounts = new RetryLinuxGroupAccountService(this);
        linuxGroupTypes = new RetryLinuxGroupTypeService(this);
        linuxGroups = new RetryLinuxGroupService(this);
        linuxIDs = new RetryLinuxIDService(this);
        linuxServerAccounts = new RetryLinuxServerAccountService(this);
        linuxServerGroups = new RetryLinuxServerGroupService(this);
        majordomoLists = new RetryMajordomoListService(this);
        majordomoServers = new RetryMajordomoServerService(this);
        majordomoVersions = new RetryMajordomoVersionService(this);
        masterHistories = new RetryMasterHistoryService(this);
        masterHosts = new RetryMasterHostService(this);
        masterServers = new RetryMasterServerService(this);
        masterUsers = new RetryMasterUserService(this);
        monthlyCharges = new RetryMonthlyChargeService(this);
        mysqlDatabases = new RetryMySQLDatabaseService(this);
        mysqlDBUsers = new RetryMySQLDBUserService(this);
        mysqlReservedWords = new RetryMySQLReservedWordService(this);
        mysqlServers = new RetryMySQLServerService(this);
        mysqlUsers = new RetryMySQLUserService(this);
        netBinds = new RetryNetBindService(this);
        netDeviceIDs = new RetryNetDeviceIDService(this);
        netDevices = new RetryNetDeviceService(this);
        netPorts = new RetryNetPortService(this);
        netProtocols = new RetryNetProtocolService(this);
        netTcpRedirects = new RetryNetTcpRedirectService(this);
        noticeLogs = new RetryNoticeLogService(this);
        noticeTypes = new RetryNoticeTypeService(this);
        operatingSystemVersions = new RetryOperatingSystemVersionService(this);
        operatingSystems = new RetryOperatingSystemService(this);
        */
        packageCategories = new RetryPackageCategoryService(this);
        /* TODO
        packageDefinitionLimits = new RetryPackageDefinitionLimitService(this);
        packageDefinitions = new RetryPackageDefinitionService(this);
        paymentTypes = new RetryPaymentTypeService(this);
        physicalServers = new RetryPhysicalServerService(this);
        postgresDatabases = new RetryPostgresDatabaseService(this);
        postgresEncodings = new RetryPostgresEncodingService(this);
        postgresReservedWords = new RetryPostgresReservedWordService(this);
        postgresServerUsers = new RetryPostgresServerUserService(this);
        postgresServers = new RetryPostgresServerService(this);
        postgresUsers = new RetryPostgresUserService(this);
        postgresVersions = new RetryPostgresVersionService(this);
        privateFTPServers = new RetryPrivateFTPServerService(this);
        processorTypes = new RetryProcessorTypeService(this);
        protocols = new RetryProtocolService(this);
        racks = new RetryRackService(this);
        resellers = new RetryResellerService(this);
         */
        resourceTypes = new RetryResourceTypeService(this);
        /* TODO
        resources = new RetryResourceService(this);
        serverFarms = new RetryServerFarmService(this);
        servers = new RetryServerService(this);
        shells = new RetryShellService(this);
        signupRequestOptions = new RetrySignupRequestOptionService(this);
        signupRequests = new RetrySignupRequestService(this);
        spamEmailMessages = new RetrySpamEmailMessageService(this);
        systemEmailAliass = new RetrySystemEmailAliasService(this);
        technologies = new RetryTechnologyService(this);
        technologyClasss = new RetryTechnologyClassService(this);
        technologyNames = new RetryTechnologyNameService(this);
        technologyVersions = new RetryTechnologyVersionService(this);
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
        usernames = new RetryUsernameService(this);
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

    <T> T retry(Callable<T> callable) throws RemoteException {
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

    public String getConnectAs() {
        return connectAs;
    }

    public BusinessAdministrator getThisBusinessAdministrator() throws RemoteException {
        BusinessAdministrator obj = getBusinessAdministrators().get(connectAs);
        if(obj==null) throw new RemoteException("Unable to find BusinessAdministrator: "+connectAs);
        return obj;
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

    /*
     * TODO
    AOServerDaemonHostService<RetryConnector,RetryConnectorFactory> getAoServerDaemonHosts();

    AOServerResourceService<RetryConnector,RetryConnectorFactory> getAoServerResources();

    AOServerService<RetryConnector,RetryConnectorFactory> getAoServers();

    AOServPermissionService<RetryConnector,RetryConnectorFactory> getAoservPermissions();

    AOServProtocolService<RetryConnector,RetryConnectorFactory> getAoservProtocols();

    AOSHCommandService<RetryConnector,RetryConnectorFactory> getAoshCommands();

    ArchitectureService<RetryConnector,RetryConnectorFactory> getArchitectures();

    BackupPartitionService<RetryConnector,RetryConnectorFactory> getBackupPartitions();

    BackupRetentionService<RetryConnector,RetryConnectorFactory> getBackupRetentions();

    BankAccountService<RetryConnector,RetryConnectorFactory> getBankAccounts();

    BankTransactionTypeService<RetryConnector,RetryConnectorFactory> getBankTransactionTypes();

    BankTransactionService<RetryConnector,RetryConnectorFactory> getBankTransactions();

    BankService<RetryConnector,RetryConnectorFactory> getBanks();

    BlackholeEmailAddressService<RetryConnector,RetryConnectorFactory> getBlackholeEmailAddresses();

    BrandService<RetryConnector,RetryConnectorFactory> getBrands();
     */
    public BusinessAdministratorService<RetryConnector,RetryConnectorFactory> getBusinessAdministrators() {
        return businessAdministrators;
    }
    /*
    BusinessAdministratorPermissionService<RetryConnector,RetryConnectorFactory> getBusinessAdministratorPermissions();

    BusinessProfileService<RetryConnector,RetryConnectorFactory> getBusinessProfiles();
     */
    public BusinessService<RetryConnector,RetryConnectorFactory> getBusinesses() {
        return businesses;
    }

    /*
    BusinessServerService<RetryConnector,RetryConnectorFactory> getBusinessServers();

    ClientJvmProfileService<RetryConnector,RetryConnectorFactory> getClientJvmProfiles();

    CountryCodeService<RetryConnector,RetryConnectorFactory> getCountryCodes();

    CreditCardProcessorService<RetryConnector,RetryConnectorFactory> getCreditCardProcessors();

    CreditCardTransactionService<RetryConnector,RetryConnectorFactory> getCreditCardTransactions();

    CreditCardService<RetryConnector,RetryConnectorFactory> getCreditCards();

    CvsRepositoryService<RetryConnector,RetryConnectorFactory> getCvsRepositories();
     */
    public DisableLogService<RetryConnector,RetryConnectorFactory> getDisableLogs() {
        return disabledLogs;
    }
    /*
    DistroFileTypeService<RetryConnector,RetryConnectorFactory> getDistroFileTypes();

    DistroFileService<RetryConnector,RetryConnectorFactory> getDistroFiles();

    DNSForbiddenZoneService<RetryConnector,RetryConnectorFactory> getDnsForbiddenZones();

    DNSRecordService<RetryConnector,RetryConnectorFactory> getDnsRecords();

    DNSTLDService<RetryConnector,RetryConnectorFactory> getDnsTLDs();

    DNSTypeService<RetryConnector,RetryConnectorFactory> getDnsTypes();

    DNSZoneService<RetryConnector,RetryConnectorFactory> getDnsZones();

    EmailAddressService<RetryConnector,RetryConnectorFactory> getEmailAddresses();

    EmailAttachmentBlockService<RetryConnector,RetryConnectorFactory> getEmailAttachmentBlocks();

    EmailAttachmentTypeService<RetryConnector,RetryConnectorFactory> getEmailAttachmentTypes();

    EmailDomainService<RetryConnector,RetryConnectorFactory> getEmailDomains();

    EmailForwardingService<RetryConnector,RetryConnectorFactory> getEmailForwardings();

    EmailListAddressService<RetryConnector,RetryConnectorFactory> getEmailListAddresses();

    EmailListService<RetryConnector,RetryConnectorFactory> getEmailLists();

    EmailPipeAddressService<RetryConnector,RetryConnectorFactory> getEmailPipeAddresses();

    EmailPipeService<RetryConnector,RetryConnectorFactory> getEmailPipes();

    EmailSmtpRelayTypeService<RetryConnector,RetryConnectorFactory> getEmailSmtpRelayTypes();

    EmailSmtpRelayService<RetryConnector,RetryConnectorFactory> getEmailSmtpRelays();

    EmailSmtpSmartHostDomainService<RetryConnector,RetryConnectorFactory> getEmailSmtpSmartHostDomains();

    EmailSmtpSmartHostService<RetryConnector,RetryConnectorFactory> getEmailSmtpSmartHosts();

    EmailSpamAssassinIntegrationModeService<RetryConnector,RetryConnectorFactory> getEmailSpamAssassinIntegrationModes();

    EncryptionKeyService<RetryConnector,RetryConnectorFactory> getEncryptionKeys();

    ExpenseCategoryService<RetryConnector,RetryConnectorFactory> getExpenseCategories();

    FailoverFileLogService<RetryConnector,RetryConnectorFactory> getFailoverFileLogs();

    FailoverFileReplicationService<RetryConnector,RetryConnectorFactory> getFailoverFileReplications();

    FailoverFileScheduleService<RetryConnector,RetryConnectorFactory> getFailoverFileSchedules();

    FailoverMySQLReplicationService<RetryConnector,RetryConnectorFactory> getFailoverMySQLReplications();

    FileBackupSettingService<RetryConnector,RetryConnectorFactory> getFileBackupSettings();

    FTPGuestUserService<RetryConnector,RetryConnectorFactory> getFtpGuestUsers();

    HttpdBindService<RetryConnector,RetryConnectorFactory> getHttpdBinds();

    HttpdJBossSiteService<RetryConnector,RetryConnectorFactory> getHttpdJBossSites();

    HttpdJBossVersionService<RetryConnector,RetryConnectorFactory> getHttpdJBossVersions();

    HttpdJKCodeService<RetryConnector,RetryConnectorFactory> getHttpdJKCodes();

    HttpdJKProtocolService<RetryConnector,RetryConnectorFactory> getHttpdJKProtocols();

    HttpdServerService<RetryConnector,RetryConnectorFactory> getHttpdServers();

    HttpdSharedTomcatService<RetryConnector,RetryConnectorFactory> getHttpdSharedTomcats();

    HttpdSiteAuthenticatedLocationService<RetryConnector,RetryConnectorFactory> getHttpdSiteAuthenticatedLocations();

    HttpdSiteBindService<RetryConnector,RetryConnectorFactory> getHttpdSiteBinds();

    HttpdSiteURLService<RetryConnector,RetryConnectorFactory> getHttpdSiteURLs();

    HttpdSiteService<RetryConnector,RetryConnectorFactory> getHttpdSites();

    HttpdStaticSiteService<RetryConnector,RetryConnectorFactory> getHttpdStaticSites();

    HttpdTomcatContextService<RetryConnector,RetryConnectorFactory> getHttpdTomcatContexts();

    HttpdTomcatDataSourceService<RetryConnector,RetryConnectorFactory> getHttpdTomcatDataSources();

    HttpdTomcatParameterService<RetryConnector,RetryConnectorFactory> getHttpdTomcatParameters();

    HttpdTomcatSiteService<RetryConnector,RetryConnectorFactory> getHttpdTomcatSites();

    HttpdTomcatSharedSiteService<RetryConnector,RetryConnectorFactory> getHttpdTomcatSharedSites();

    HttpdTomcatStdSiteService<RetryConnector,RetryConnectorFactory> getHttpdTomcatStdSites();

    HttpdTomcatVersionService<RetryConnector,RetryConnectorFactory> getHttpdTomcatVersions();

    HttpdWorkerService<RetryConnector,RetryConnectorFactory> getHttpdWorkers();

    IPAddressService<RetryConnector,RetryConnectorFactory> getIpAddresses();
    */
    public LanguageService<RetryConnector,RetryConnectorFactory> getLanguages() {
        return languages;
    }
    /* TODO
    LinuxAccAddressService<RetryConnector,RetryConnectorFactory> getLinuxAccAddresses();

    LinuxAccountTypeService<RetryConnector,RetryConnectorFactory> getLinuxAccountTypes();

    LinuxAccountService<RetryConnector,RetryConnectorFactory> getLinuxAccounts();

    LinuxGroupAccountService<RetryConnector,RetryConnectorFactory> getLinuxGroupAccounts();

    LinuxGroupTypeService<RetryConnector,RetryConnectorFactory> getLinuxGroupTypes();

    LinuxGroupService<RetryConnector,RetryConnectorFactory> getLinuxGroups();

    LinuxIDService<RetryConnector,RetryConnectorFactory> getLinuxIDs();

    LinuxServerAccountService<RetryConnector,RetryConnectorFactory> getLinuxServerAccounts();

    LinuxServerGroupService<RetryConnector,RetryConnectorFactory> getLinuxServerGroups();

    MajordomoListService<RetryConnector,RetryConnectorFactory> getMajordomoLists();

    MajordomoServerService<RetryConnector,RetryConnectorFactory> getMajordomoServers();

    MajordomoVersionService<RetryConnector,RetryConnectorFactory> getMajordomoVersions();

    MasterHistoryService<RetryConnector,RetryConnectorFactory> getMasterHistory();

    MasterHostService<RetryConnector,RetryConnectorFactory> getMasterHosts();

    MasterServerService<RetryConnector,RetryConnectorFactory> getMasterServers();

    MasterUserService<RetryConnector,RetryConnectorFactory> getMasterUsers();

    MonthlyChargeService<RetryConnector,RetryConnectorFactory> getMonthlyCharges();

    MySQLDatabaseService<RetryConnector,RetryConnectorFactory> getMysqlDatabases();

    MySQLDBUserService<RetryConnector,RetryConnectorFactory> getMysqlDBUsers();

    MySQLReservedWordService<RetryConnector,RetryConnectorFactory> getMysqlReservedWords();

    MySQLServerService<RetryConnector,RetryConnectorFactory> getMysqlServers();

    MySQLUserService<RetryConnector,RetryConnectorFactory> getMysqlUsers();

    NetBindService<RetryConnector,RetryConnectorFactory> getNetBinds();

    NetDeviceIDService<RetryConnector,RetryConnectorFactory> getNetDeviceIDs();

    NetDeviceService<RetryConnector,RetryConnectorFactory> getNetDevices();

    NetPortService<RetryConnector,RetryConnectorFactory> getNetPorts();

    NetProtocolService<RetryConnector,RetryConnectorFactory> getNetProtocols();

    NetTcpRedirectService<RetryConnector,RetryConnectorFactory> getNetTcpRedirects();

    NoticeLogService<RetryConnector,RetryConnectorFactory> getNoticeLogs();

    NoticeTypeService<RetryConnector,RetryConnectorFactory> getNoticeTypes();

    OperatingSystemVersionService<RetryConnector,RetryConnectorFactory> getOperatingSystemVersions();

    OperatingSystemService<RetryConnector,RetryConnectorFactory> getOperatingSystems();
    */
    public PackageCategoryService<RetryConnector,RetryConnectorFactory> getPackageCategories() {
        return packageCategories;
    }
    /*
    PackageDefinitionLimitService<RetryConnector,RetryConnectorFactory> getPackageDefinitionLimits();

    PackageDefinitionService<RetryConnector,RetryConnectorFactory> getPackageDefinitions();

    PaymentTypeService<RetryConnector,RetryConnectorFactory> getPaymentTypes();

    PhysicalServerService<RetryConnector,RetryConnectorFactory> getPhysicalServers();

    PostgresDatabaseService<RetryConnector,RetryConnectorFactory> getPostgresDatabases();

    PostgresEncodingService<RetryConnector,RetryConnectorFactory> getPostgresEncodings();

    PostgresReservedWordService<RetryConnector,RetryConnectorFactory> getPostgresReservedWords();

    PostgresServerUserService<RetryConnector,RetryConnectorFactory> getPostgresServerUsers();

    PostgresServerService<RetryConnector,RetryConnectorFactory> getPostgresServers();

    PostgresUserService<RetryConnector,RetryConnectorFactory> getPostgresUsers();

    PostgresVersionService<RetryConnector,RetryConnectorFactory> getPostgresVersions();

    PrivateFTPServerService<RetryConnector,RetryConnectorFactory> getPrivateFTPServers();

    ProcessorTypeService<RetryConnector,RetryConnectorFactory> getProcessorTypes();

    ProtocolService<RetryConnector,RetryConnectorFactory> getProtocols();

    RackService<RetryConnector,RetryConnectorFactory> getRacks();

    ResellerService<RetryConnector,RetryConnectorFactory> getResellers();
*/
    public ResourceTypeService<RetryConnector,RetryConnectorFactory> getResourceTypes() {
        return resourceTypes;
    }
/* TODO
    ResourceService<RetryConnector,RetryConnectorFactory> getResources();

    ServerFarmService<RetryConnector,RetryConnectorFactory> getServerFarms();

    ServerTable getServers();

    ShellService<RetryConnector,RetryConnectorFactory> getShells();

    SignupRequestOptionService<RetryConnector,RetryConnectorFactory> getSignupRequestOptions();

    SignupRequestService<RetryConnector,RetryConnectorFactory> getSignupRequests();

    SpamEmailMessageService<RetryConnector,RetryConnectorFactory> getSpamEmailMessages();

    SystemEmailAliasService<RetryConnector,RetryConnectorFactory> getSystemEmailAliases();

    TechnologyService<RetryConnector,RetryConnectorFactory> getTechnologies();

    TechnologyClassService<RetryConnector,RetryConnectorFactory> getTechnologyClasses();

    TechnologyNameService<RetryConnector,RetryConnectorFactory> getTechnologyNames();

    TechnologyVersionService<RetryConnector,RetryConnectorFactory> getTechnologyVersions();

    TicketActionTypeService<RetryConnector,RetryConnectorFactory> getTicketActionTypes();

    TicketActionService<RetryConnector,RetryConnectorFactory> getTicketActions();

    TicketAssignmentService<RetryConnector,RetryConnectorFactory> getTicketAssignments();

    TicketBrandCategoryService<RetryConnector,RetryConnectorFactory> getTicketBrandCategories();
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
    TicketService<RetryConnector,RetryConnectorFactory> getTickets();
    */
    public TimeZoneService<RetryConnector,RetryConnectorFactory> getTimeZones() {
        return timeZones;
    }
    /* TODO
    TransactionTypeService<RetryConnector,RetryConnectorFactory> getTransactionTypes();

    TransactionService<RetryConnector,RetryConnectorFactory> getTransactions();

    USStateService<RetryConnector,RetryConnectorFactory> getUsStates();

    UsernameService<RetryConnector,RetryConnectorFactory> getUsernames();

    VirtualDiskService<RetryConnector,RetryConnectorFactory> getVirtualDisks();

    VirtualServerService<RetryConnector,RetryConnectorFactory> getVirtualServers();

    WhoisHistoryService<RetryConnector,RetryConnectorFactory> getWhoisHistory();
 */
}
