package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.AOServConnectorUtils;
import com.aoindustries.aoserv.client.AOServPermissionService;
import com.aoindustries.aoserv.client.AOServService;
import com.aoindustries.aoserv.client.BusinessAdministrator;
import com.aoindustries.aoserv.client.BusinessAdministratorService;
import com.aoindustries.aoserv.client.BusinessService;
import com.aoindustries.aoserv.client.CountryCodeService;
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
import com.aoindustries.aoserv.client.UsernameService;
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
     */
    final RetryAOServPermissionService aoservPermissions;
    /* TODO
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
     */
    final RetryCountryCodeService countryCodes;
    /* TODO
    final RetryCreditCardProcessorService creditCardProcessors;
    final RetryCreditCardTransactionService creditCardTransactions;
    final RetryCreditCardService creditCards;
    final RetryCvsRepositoryService cvsRepositories;
     */
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
     */
    final RetryUsernameService usernames;
    /* TODO
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
         */
        aoservPermissions = new RetryAOServPermissionService(this);
        /* TODO
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
         */
        countryCodes = new RetryCountryCodeService(this);
        /* TODO
        creditCardProcessors = new RetryCreditCardProcessorService(this);
        creditCardTransactions = new RetryCreditCardTransactionService(this);
        creditCards = new RetryCreditCardService(this);
        cvsRepositories = new RetryCvsRepositoryService(this);
         */
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

    public String getAuthenticateAs() {
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

    /*
     * TODO
    public AOServerDaemonHostService<RetryConnector,RetryConnectorFactory> getAoServerDaemonHosts();

    public AOServerResourceService<RetryConnector,RetryConnectorFactory> getAoServerResources();

    public AOServerService<RetryConnector,RetryConnectorFactory> getAoServers();
    */
    public AOServPermissionService<RetryConnector,RetryConnectorFactory> getAoservPermissions() {
        return aoservPermissions;
    }
    /* TODO
    public AOServProtocolService<RetryConnector,RetryConnectorFactory> getAoservProtocols();

    public AOSHCommandService<RetryConnector,RetryConnectorFactory> getAoshCommands();

    public ArchitectureService<RetryConnector,RetryConnectorFactory> getArchitectures();

    public BackupPartitionService<RetryConnector,RetryConnectorFactory> getBackupPartitions();

    public BackupRetentionService<RetryConnector,RetryConnectorFactory> getBackupRetentions();

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

    /* TODO
    public BusinessServerService<RetryConnector,RetryConnectorFactory> getBusinessServers();

    public ClientJvmProfileService<RetryConnector,RetryConnectorFactory> getClientJvmProfiles();
    */
    public CountryCodeService<RetryConnector,RetryConnectorFactory> getCountryCodes() {
        return countryCodes;
    }
    /* TODO
    public CreditCardProcessorService<RetryConnector,RetryConnectorFactory> getCreditCardProcessors();

    public CreditCardTransactionService<RetryConnector,RetryConnectorFactory> getCreditCardTransactions();

    public CreditCardService<RetryConnector,RetryConnectorFactory> getCreditCards();

    public CvsRepositoryService<RetryConnector,RetryConnectorFactory> getCvsRepositories();
     */
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

    public FailoverFileLogService<RetryConnector,RetryConnectorFactory> getFailoverFileLogs();

    public FailoverFileReplicationService<RetryConnector,RetryConnectorFactory> getFailoverFileReplications();

    public FailoverFileScheduleService<RetryConnector,RetryConnectorFactory> getFailoverFileSchedules();

    public FailoverMySQLReplicationService<RetryConnector,RetryConnectorFactory> getFailoverMySQLReplications();

    public FileBackupSettingService<RetryConnector,RetryConnectorFactory> getFileBackupSettings();

    public FTPGuestUserService<RetryConnector,RetryConnectorFactory> getFtpGuestUsers();

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

    public HttpdSiteService<RetryConnector,RetryConnectorFactory> getHttpdSites();

    public HttpdStaticSiteService<RetryConnector,RetryConnectorFactory> getHttpdStaticSites();

    public HttpdTomcatContextService<RetryConnector,RetryConnectorFactory> getHttpdTomcatContexts();

    public HttpdTomcatDataSourceService<RetryConnector,RetryConnectorFactory> getHttpdTomcatDataSources();

    public HttpdTomcatParameterService<RetryConnector,RetryConnectorFactory> getHttpdTomcatParameters();

    public HttpdTomcatSiteService<RetryConnector,RetryConnectorFactory> getHttpdTomcatSites();

    public HttpdTomcatSharedSiteService<RetryConnector,RetryConnectorFactory> getHttpdTomcatSharedSites();

    public HttpdTomcatStdSiteService<RetryConnector,RetryConnectorFactory> getHttpdTomcatStdSites();

    public HttpdTomcatVersionService<RetryConnector,RetryConnectorFactory> getHttpdTomcatVersions();

    public HttpdWorkerService<RetryConnector,RetryConnectorFactory> getHttpdWorkers();

    public IPAddressService<RetryConnector,RetryConnectorFactory> getIpAddresses();
    */
    public LanguageService<RetryConnector,RetryConnectorFactory> getLanguages() {
        return languages;
    }
    /* TODO
    public LinuxAccAddressService<RetryConnector,RetryConnectorFactory> getLinuxAccAddresses();

    public LinuxAccountTypeService<RetryConnector,RetryConnectorFactory> getLinuxAccountTypes();

    public LinuxAccountService<RetryConnector,RetryConnectorFactory> getLinuxAccounts();

    public LinuxGroupAccountService<RetryConnector,RetryConnectorFactory> getLinuxGroupAccounts();

    public LinuxGroupTypeService<RetryConnector,RetryConnectorFactory> getLinuxGroupTypes();

    public LinuxGroupService<RetryConnector,RetryConnectorFactory> getLinuxGroups();

    public LinuxIDService<RetryConnector,RetryConnectorFactory> getLinuxIDs();

    public LinuxServerAccountService<RetryConnector,RetryConnectorFactory> getLinuxServerAccounts();

    public LinuxServerGroupService<RetryConnector,RetryConnectorFactory> getLinuxServerGroups();

    public MajordomoListService<RetryConnector,RetryConnectorFactory> getMajordomoLists();

    public MajordomoServerService<RetryConnector,RetryConnectorFactory> getMajordomoServers();

    public MajordomoVersionService<RetryConnector,RetryConnectorFactory> getMajordomoVersions();

    public MasterHistoryService<RetryConnector,RetryConnectorFactory> getMasterHistory();

    public MasterHostService<RetryConnector,RetryConnectorFactory> getMasterHosts();

    public MasterServerService<RetryConnector,RetryConnectorFactory> getMasterServers();

    public MasterUserService<RetryConnector,RetryConnectorFactory> getMasterUsers();

    public MonthlyChargeService<RetryConnector,RetryConnectorFactory> getMonthlyCharges();

    public MySQLDatabaseService<RetryConnector,RetryConnectorFactory> getMysqlDatabases();

    public MySQLDBUserService<RetryConnector,RetryConnectorFactory> getMysqlDBUsers();

    public MySQLReservedWordService<RetryConnector,RetryConnectorFactory> getMysqlReservedWords();

    public MySQLServerService<RetryConnector,RetryConnectorFactory> getMysqlServers();

    public MySQLUserService<RetryConnector,RetryConnectorFactory> getMysqlUsers();

    public NetBindService<RetryConnector,RetryConnectorFactory> getNetBinds();

    public NetDeviceIDService<RetryConnector,RetryConnectorFactory> getNetDeviceIDs();

    public NetDeviceService<RetryConnector,RetryConnectorFactory> getNetDevices();

    public NetPortService<RetryConnector,RetryConnectorFactory> getNetPorts();

    public NetProtocolService<RetryConnector,RetryConnectorFactory> getNetProtocols();

    public NetTcpRedirectService<RetryConnector,RetryConnectorFactory> getNetTcpRedirects();

    public NoticeLogService<RetryConnector,RetryConnectorFactory> getNoticeLogs();

    public NoticeTypeService<RetryConnector,RetryConnectorFactory> getNoticeTypes();

    public OperatingSystemVersionService<RetryConnector,RetryConnectorFactory> getOperatingSystemVersions();

    public OperatingSystemService<RetryConnector,RetryConnectorFactory> getOperatingSystems();
    */
    public PackageCategoryService<RetryConnector,RetryConnectorFactory> getPackageCategories() {
        return packageCategories;
    }
    /*
    public PackageDefinitionLimitService<RetryConnector,RetryConnectorFactory> getPackageDefinitionLimits();

    public PackageDefinitionService<RetryConnector,RetryConnectorFactory> getPackageDefinitions();

    public PaymentTypeService<RetryConnector,RetryConnectorFactory> getPaymentTypes();

    public PhysicalServerService<RetryConnector,RetryConnectorFactory> getPhysicalServers();

    public PostgresDatabaseService<RetryConnector,RetryConnectorFactory> getPostgresDatabases();

    public PostgresEncodingService<RetryConnector,RetryConnectorFactory> getPostgresEncodings();

    public PostgresReservedWordService<RetryConnector,RetryConnectorFactory> getPostgresReservedWords();

    public PostgresServerUserService<RetryConnector,RetryConnectorFactory> getPostgresServerUsers();

    public PostgresServerService<RetryConnector,RetryConnectorFactory> getPostgresServers();

    public PostgresUserService<RetryConnector,RetryConnectorFactory> getPostgresUsers();

    public PostgresVersionService<RetryConnector,RetryConnectorFactory> getPostgresVersions();

    public PrivateFTPServerService<RetryConnector,RetryConnectorFactory> getPrivateFTPServers();

    public ProcessorTypeService<RetryConnector,RetryConnectorFactory> getProcessorTypes();

    public ProtocolService<RetryConnector,RetryConnectorFactory> getProtocols();

    public RackService<RetryConnector,RetryConnectorFactory> getRacks();

    public ResellerService<RetryConnector,RetryConnectorFactory> getResellers();
*/
    public ResourceTypeService<RetryConnector,RetryConnectorFactory> getResourceTypes() {
        return resourceTypes;
    }
/* TODO
    public ResourceService<RetryConnector,RetryConnectorFactory> getResources();

    public ServerFarmService<RetryConnector,RetryConnectorFactory> getServerFarms();

    public ServerTable getServers();

    public ShellService<RetryConnector,RetryConnectorFactory> getShells();

    public SignupRequestOptionService<RetryConnector,RetryConnectorFactory> getSignupRequestOptions();

    public SignupRequestService<RetryConnector,RetryConnectorFactory> getSignupRequests();

    public SpamEmailMessageService<RetryConnector,RetryConnectorFactory> getSpamEmailMessages();

    public SystemEmailAliasService<RetryConnector,RetryConnectorFactory> getSystemEmailAliases();

    public TechnologyService<RetryConnector,RetryConnectorFactory> getTechnologies();

    public TechnologyClassService<RetryConnector,RetryConnectorFactory> getTechnologyClasses();

    public TechnologyNameService<RetryConnector,RetryConnectorFactory> getTechnologyNames();

    public TechnologyVersionService<RetryConnector,RetryConnectorFactory> getTechnologyVersions();

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
