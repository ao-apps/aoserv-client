package com.aoindustries.aoserv.client.retry;

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
         */
        brands = new RetryBrandService(this);
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
        groupNames = new RetryGroupNameService(this);
        /* TODO
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
         */
        httpdSites = new RetryHttpdSiteService(this);
        // TODO: httpdStaticSites = new RetryHttpdStaticSiteService(this);
        // TODO: httpdTomcatContexts = new RetryHttpdTomcatContextService(this);
        // TODO: httpdTomcatDataSources = new RetryHttpdTomcatDataSourceService(this);
        // TODO: httpdTomcatParameters = new RetryHttpdTomcatParameterService(this);
        // TODO: httpdTomcatSites = new RetryHttpdTomcatSiteService(this);
        // TODO: httpdTomcatSharedSites = new RetryHttpdTomcatSharedSiteService(this);
        // TODO: httpdTomcatStdSites = new RetryHttpdTomcatStdSiteService(this);
        // TODO: httpdTomcatVersions = new RetryHttpdTomcatVersionService(this);
        // TODO: httpdWorkers = new RetryHttpdWorkerService(this);
        ipAddresses = new RetryIPAddressService(this);
        languages = new RetryLanguageService(this);
        /* TODO
        linuxAccAddresss = new RetryLinuxAccAddressService(this);
         */
        linuxAccountGroups = new RetryLinuxAccountGroupService(this);
        linuxAccountTypes = new RetryLinuxAccountTypeService(this);
        linuxAccounts = new RetryLinuxAccountService(this);
        linuxGroupTypes = new RetryLinuxGroupTypeService(this);
        linuxGroups = new RetryLinuxGroupService(this);
        /* TODO
        majordomoLists = new RetryMajordomoListService(this);
        majordomoServers = new RetryMajordomoServerService(this);
        majordomoVersions = new RetryMajordomoVersionService(this);
        masterHistories = new RetryMasterHistoryService(this);
        masterHosts = new RetryMasterHostService(this);
        masterServers = new RetryMasterServerService(this);
        masterUsers = new RetryMasterUserService(this);
        monthlyCharges = new RetryMonthlyChargeService(this);
         */
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
        // TODO: racks = new RetryRackService(this);
        resellers = new RetryResellerService(this);
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
        // TODO: ticketActionTypes = new RetryTicketActionTypeService(this);
        // TODO: ticketActions = new RetryTicketActionService(this);
        ticketAssignments = new RetryTicketAssignmentService(this);
        // TODO: ticketBrandCategories = new RetryTicketBrandCategoryService(this);
        ticketCategories = new RetryTicketCategoryService(this);
        ticketPriorities = new RetryTicketPriorityService(this);
        ticketStatuses = new RetryTicketStatusService(this);
        ticketTypes = new RetryTicketTypeService(this);
        tickets = new RetryTicketService(this);
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
        return call(callable, true);
    }

    <T> T call(Callable<T> callable, boolean allowRetry) throws RemoteException, NoSuchElementException {
        int attempt = 1;
        while(!Thread.interrupted()) {
            if(factory.timeout>0) {
                Future<T> future = RetryUtils.executorService.submit(callable);
                try {
                    return future.get(factory.timeout, factory.unit);
                } catch(RuntimeException err) {
                    disconnectIfNeeded(err);
                    if(!allowRetry || Thread.interrupted() || attempt>=RetryUtils.RETRY_ATTEMPTS || RetryUtils.isImmediateFail(err)) throw err;
                } catch(ExecutionException err) {
                    disconnectIfNeeded(err);
                    if(!allowRetry || Thread.interrupted() || attempt>=RetryUtils.RETRY_ATTEMPTS || RetryUtils.isImmediateFail(err)) {
                        Throwable cause = err.getCause();
                        if(cause instanceof RemoteException) throw (RemoteException)cause;
                        if(cause instanceof NoSuchElementException) throw (NoSuchElementException)cause;
                        throw new RemoteException(err.getMessage(), err);
                    }
                } catch(TimeoutException err) {
                    future.cancel(true);
                    disconnectIfNeeded(err);
                    if(!allowRetry || Thread.interrupted() || attempt>=RetryUtils.RETRY_ATTEMPTS || RetryUtils.isImmediateFail(err)) throw new RemoteException(err.getMessage(), err);
                } catch(Exception err) {
                    disconnectIfNeeded(err);
                    if(!allowRetry || Thread.interrupted() || attempt>=RetryUtils.RETRY_ATTEMPTS || RetryUtils.isImmediateFail(err)) throw new RemoteException(err.getMessage(), err);
                }
            } else {
                try {
                    return callable.call();
                } catch(RuntimeException err) {
                    disconnectIfNeeded(err);
                    if(!allowRetry || Thread.interrupted() || attempt>=RetryUtils.RETRY_ATTEMPTS || RetryUtils.isImmediateFail(err)) throw err;
                } catch(RemoteException err) {
                    disconnectIfNeeded(err);
                    if(!allowRetry || Thread.interrupted() || attempt>=RetryUtils.RETRY_ATTEMPTS || RetryUtils.isImmediateFail(err)) throw err;
                } catch(Exception err) {
                    disconnectIfNeeded(err);
                    if(!allowRetry || Thread.interrupted() || attempt>=RetryUtils.RETRY_ATTEMPTS || RetryUtils.isImmediateFail(err)) throw new RemoteException(err.getMessage(), err);
                }
            }
            if(!allowRetry) throw new AssertionError("allowRetry==false");
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
                new Callable<Void>() {
                    public Void call() throws RemoteException {
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

    public <R> R executeCommand(final AOServCommand<R> command, final boolean isInteractive) throws RemoteException {
        return call(
            new Callable<R>() {
                public R call() throws RemoteException {
                    return getWrapped().executeCommand(command, isInteractive);
                }
            },
            command.isRetryable()
        );
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

    // <editor-fold defaultstate="collapsed" desc="AOServerDaemonHostService">
    static class RetryAOServerDaemonHostService extends RetryService<Integer,AOServerDaemonHost> implements AOServerDaemonHostService<RetryConnector,RetryConnectorFactory> {
        RetryAOServerDaemonHostService(RetryConnector connector) {
            super(connector, Integer.class, AOServerDaemonHost.class);
        }
    }
    final RetryAOServerDaemonHostService aoserverDaemonHosts;
    public AOServerDaemonHostService<RetryConnector,RetryConnectorFactory> getAoServerDaemonHosts() {
        return aoserverDaemonHosts;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="AOServerResourceService">
    static class RetryAOServerResourceService extends RetryService<Integer,AOServerResource> implements AOServerResourceService<RetryConnector,RetryConnectorFactory> {
        RetryAOServerResourceService(RetryConnector connector) {
            super(connector, Integer.class, AOServerResource.class);
        }
    }
    final RetryAOServerResourceService aoserverResources;
    public AOServerResourceService<RetryConnector,RetryConnectorFactory> getAoServerResources() {
        return aoserverResources;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="AOServerService">
    static class RetryAOServerService extends RetryService<Integer,AOServer> implements AOServerService<RetryConnector,RetryConnectorFactory> {
        RetryAOServerService(RetryConnector connector) {
            super(connector, Integer.class, AOServer.class);
        }
    }
    final RetryAOServerService aoservers;
    public AOServerService<RetryConnector,RetryConnectorFactory> getAoServers() {
        return aoservers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="AOServPermissionService">
    static class RetryAOServPermissionService extends RetryService<String,AOServPermission> implements AOServPermissionService<RetryConnector,RetryConnectorFactory> {
        RetryAOServPermissionService(RetryConnector connector) {
            super(connector, String.class, AOServPermission.class);
        }
    }
    final RetryAOServPermissionService aoservPermissions;
    public AOServPermissionService<RetryConnector,RetryConnectorFactory> getAoservPermissions() {
        return aoservPermissions;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ArchitectureService">
    static class RetryArchitectureService extends RetryService<String,Architecture> implements ArchitectureService<RetryConnector,RetryConnectorFactory> {
        RetryArchitectureService(RetryConnector connector) {
            super(connector, String.class, Architecture.class);
        }
    }
    final RetryArchitectureService architectures;
    public ArchitectureService<RetryConnector,RetryConnectorFactory> getArchitectures() {
        return architectures;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BackupPartitionService">
    static class RetryBackupPartitionService extends RetryService<Integer,BackupPartition> implements BackupPartitionService<RetryConnector,RetryConnectorFactory> {
        RetryBackupPartitionService(RetryConnector connector) {
            super(connector, Integer.class, BackupPartition.class);
        }
    }
    final RetryBackupPartitionService backupPartitions;
    public BackupPartitionService<RetryConnector,RetryConnectorFactory> getBackupPartitions() {
        return backupPartitions;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BackupRetentionService">
    static class RetryBackupRetentionService extends RetryService<Short,BackupRetention> implements BackupRetentionService<RetryConnector,RetryConnectorFactory> {
        RetryBackupRetentionService(RetryConnector connector) {
            super(connector, Short.class, BackupRetention.class);
        }
    }
    final RetryBackupRetentionService backupRetentions;
    public BackupRetentionService<RetryConnector,RetryConnectorFactory> getBackupRetentions() {
        return backupRetentions;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BankAccountService">
    // TODO: final RetryBankAccountService bankAccounts;
    // TODO: public BankAccountService<RetryConnector,RetryConnectorFactory> getBankAccounts();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BankTransactionTypeService">
    // TODO: final RetryBankTransactionTypeService bankTransactionTypes;
    // TODO: public BankTransactionTypeService<RetryConnector,RetryConnectorFactory> getBankTransactionTypes();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BankTransactionService">
    // TODO: final RetryBankTransactionService bankTransactions;
    // TODO: public BankTransactionService<RetryConnector,RetryConnectorFactory> getBankTransactions();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BankService">
    // TODO: final RetryBankService banks;
    // TODO: public BankService<RetryConnector,RetryConnectorFactory> getBanks();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BlackholeEmailAddressService">
    // TODO: final RetryBlackholeEmailAddressService blackholeEmailAddresss;
    // TODO: public BlackholeEmailAddressService<RetryConnector,RetryConnectorFactory> getBlackholeEmailAddresses();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BrandService">
    static class RetryBrandService extends RetryService<AccountingCode,Brand> implements BrandService<RetryConnector,RetryConnectorFactory> {
        RetryBrandService(RetryConnector connector) {
            super(connector, AccountingCode.class, Brand.class);
        }
    }
    final RetryBrandService brands;
    public BrandService<RetryConnector,RetryConnectorFactory> getBrands() {
        return brands;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BusinessAdministratorService">
    static class RetryBusinessAdministratorService extends RetryService<UserId,BusinessAdministrator> implements BusinessAdministratorService<RetryConnector,RetryConnectorFactory> {
        RetryBusinessAdministratorService(RetryConnector connector) {
            super(connector, UserId.class, BusinessAdministrator.class);
        }
    }
    final RetryBusinessAdministratorService businessAdministrators;
    public BusinessAdministratorService<RetryConnector,RetryConnectorFactory> getBusinessAdministrators() {
        return businessAdministrators;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BusinessAdministratorPermissionService">
    // TODO: final RetryBusinessAdministratorPermissionService businessAdministratorPermissions;
    // TODO: public BusinessAdministratorPermissionService<RetryConnector,RetryConnectorFactory> getBusinessAdministratorPermissions();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BusinessProfileService">
    // TODO: final RetryBusinessProfileService businessProfiles;
    // TODO: public BusinessProfileService<RetryConnector,RetryConnectorFactory> getBusinessProfiles();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BusinessService">
    static class RetryBusinessService extends RetryService<AccountingCode,Business> implements BusinessService<RetryConnector,RetryConnectorFactory> {
        RetryBusinessService(RetryConnector connector) {
            super(connector, AccountingCode.class, Business.class);
        }
    }
    final RetryBusinessService businesses;
    public BusinessService<RetryConnector,RetryConnectorFactory> getBusinesses() {
        return businesses;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BusinessServerService">
    static class RetryBusinessServerService extends RetryService<Integer,BusinessServer> implements BusinessServerService<RetryConnector,RetryConnectorFactory> {
        RetryBusinessServerService(RetryConnector connector) {
            super(connector, Integer.class, BusinessServer.class);
        }
    }
    final RetryBusinessServerService businessServers;
    public BusinessServerService<RetryConnector,RetryConnectorFactory> getBusinessServers() {
        return businessServers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="CountryCodeService">
    static class RetryCountryCodeService extends RetryService<String,CountryCode> implements CountryCodeService<RetryConnector,RetryConnectorFactory> {
        RetryCountryCodeService(RetryConnector connector) {
            super(connector, String.class, CountryCode.class);
        }
    }
    final RetryCountryCodeService countryCodes;
    public CountryCodeService<RetryConnector,RetryConnectorFactory> getCountryCodes() {
        return countryCodes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="CreditCardProcessorService">
    // TODO: final RetryCreditCardProcessorService creditCardProcessors;
    // TODO: public CreditCardProcessorService<RetryConnector,RetryConnectorFactory> getCreditCardProcessors();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="CreditCardTransactionService">
    // TODO: final RetryCreditCardTransactionService creditCardTransactions;
    // TODO: public CreditCardTransactionService<RetryConnector,RetryConnectorFactory> getCreditCardTransactions();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="CreditCardService">
    // TODO: final RetryCreditCardService creditCards;
    // TODO: public CreditCardService<RetryConnector,RetryConnectorFactory> getCreditCards();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="CvsRepositoryService">
    static class RetryCvsRepositoryService extends RetryService<Integer,CvsRepository> implements CvsRepositoryService<RetryConnector,RetryConnectorFactory> {
        RetryCvsRepositoryService(RetryConnector connector) {
            super(connector, Integer.class, CvsRepository.class);
        }
    }
    final RetryCvsRepositoryService cvsRepositories;
    public CvsRepositoryService<RetryConnector,RetryConnectorFactory> getCvsRepositories() {
        return cvsRepositories;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="DisableLogService">
    static class RetryDisableLogService extends RetryService<Integer,DisableLog> implements DisableLogService<RetryConnector,RetryConnectorFactory> {
        RetryDisableLogService(RetryConnector connector) {
            super(connector, Integer.class, DisableLog.class);
        }
    }
    final RetryDisableLogService disableLogs;
    public DisableLogService<RetryConnector,RetryConnectorFactory> getDisableLogs() {
        return disableLogs;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="DistroFileTypeService">
    // TODO: final RetryDistroFileTypeService distroFileTypes;
    // TODO: public DistroFileTypeService<RetryConnector,RetryConnectorFactory> getDistroFileTypes();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="DistroFileService">
    // TODO: final RetryDistroFileService distroFiles;
    // TODO: public DistroFileService<RetryConnector,RetryConnectorFactory> getDistroFiles();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="DNSForbiddenZoneService">
    // TODO: final RetryDNSForbiddenZoneService dnsForbiddenZones;
    // TODO: public DNSForbiddenZoneService<RetryConnector,RetryConnectorFactory> getDnsForbiddenZones();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="DNSRecordService">
    // TODO: final RetryDNSRecordService dnsRecords;
    // TODO: public DNSRecordService<RetryConnector,RetryConnectorFactory> getDnsRecords();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="DNSTLDService">
    // TODO: final RetryDNSTLDService dnsTLDs;
    // TODO: public DNSTLDService<RetryConnector,RetryConnectorFactory> getDnsTLDs();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="DNSTypeService">
    // TODO: final RetryDNSTypeService dnsTypes;
    // TODO: public DNSTypeService<RetryConnector,RetryConnectorFactory> getDnsTypes();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="DNSZoneService">
    // TODO: final RetryDNSZoneService dnsZones;
    // TODO: public DNSZoneService<RetryConnector,RetryConnectorFactory> getDnsZones();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailAddressService">
    // TODO: final RetryEmailAddressService emailAddresss;
    // TODO: public EmailAddressService<RetryConnector,RetryConnectorFactory> getEmailAddresses();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailAttachmentBlockService">
    // TODO: final RetryEmailAttachmentBlockService emailAttachmentBlocks;
    // TODO: public EmailAttachmentBlockService<RetryConnector,RetryConnectorFactory> getEmailAttachmentBlocks();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailAttachmentTypeService">
    // TODO: final RetryEmailAttachmentTypeService emailAttachmentTypes;
    // TODO: public EmailAttachmentTypeService<RetryConnector,RetryConnectorFactory> getEmailAttachmentTypes();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailDomainService">
    // TODO: final RetryEmailDomainService emailDomains;
    // TODO: public EmailDomainService<RetryConnector,RetryConnectorFactory> getEmailDomains();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailForwardingService">
    // TODO: final RetryEmailForwardingService emailForwardings;
    // TODO: public EmailForwardingService<RetryConnector,RetryConnectorFactory> getEmailForwardings();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailListAddressService">
    // TODO: final RetryEmailListAddressService emailListAddresss;
    // TODO: public EmailListAddressService<RetryConnector,RetryConnectorFactory> getEmailListAddresses();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailListService">
    // TODO: final RetryEmailListService emailLists;
    // TODO: public EmailListService<RetryConnector,RetryConnectorFactory> getEmailLists();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailPipeAddressService">
    // TODO: final RetryEmailPipeAddressService emailPipeAddresss;
    // TODO: public EmailPipeAddressService<RetryConnector,RetryConnectorFactory> getEmailPipeAddresses();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailPipeService">
    // TODO: final RetryEmailPipeService emailPipes;
    // TODO: public EmailPipeService<RetryConnector,RetryConnectorFactory> getEmailPipes();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailSmtpRelayTypeService">
    // TODO: final RetryEmailSmtpRelayTypeService emailSmtpRelayTypes;
    // TODO: public EmailSmtpRelayTypeService<RetryConnector,RetryConnectorFactory> getEmailSmtpRelayTypes();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailSmtpRelayService">
    // TODO: final RetryEmailSmtpRelayService emailSmtpRelays;
    // TODO: public EmailSmtpRelayService<RetryConnector,RetryConnectorFactory> getEmailSmtpRelays();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailSmtpSmartHostDomainService">
    // TODO: final RetryEmailSmtpSmartHostDomainService emailSmtpSmartHostDomains;
    // TODO: public EmailSmtpSmartHostDomainService<RetryConnector,RetryConnectorFactory> getEmailSmtpSmartHostDomains();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailSmtpSmartHostService">
    // TODO: final RetryEmailSmtpSmartHostService emailSmtpSmartHosts;
    // TODO: public EmailSmtpSmartHostService<RetryConnector,RetryConnectorFactory> getEmailSmtpSmartHosts();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailSpamAssassinIntegrationModeService">
    // TODO: final RetryEmailSpamAssassinIntegrationModeService emailSpamAssassinIntegrationModes;
    // TODO: public EmailSpamAssassinIntegrationModeService<RetryConnector,RetryConnectorFactory> getEmailSpamAssassinIntegrationModes();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EncryptionKeyService">
    // TODO: final RetryEncryptionKeyService encryptionKeys;
    // TODO: public EncryptionKeyService<RetryConnector,RetryConnectorFactory> getEncryptionKeys();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ExpenseCategoryService">
    // TODO: final RetryExpenseCategoryService expenseCategories;
    // TODO: public ExpenseCategoryService<RetryConnector,RetryConnectorFactory> getExpenseCategories();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="FailoverFileLogService">
    static class RetryFailoverFileLogService extends RetryService<Integer,FailoverFileLog> implements FailoverFileLogService<RetryConnector,RetryConnectorFactory> {
        RetryFailoverFileLogService(RetryConnector connector) {
            super(connector, Integer.class, FailoverFileLog.class);
        }
    }
    final RetryFailoverFileLogService failoverFileLogs;
    public FailoverFileLogService<RetryConnector,RetryConnectorFactory> getFailoverFileLogs() {
        return failoverFileLogs;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="FailoverFileReplicationService">
    static class RetryFailoverFileReplicationService extends RetryService<Integer,FailoverFileReplication> implements FailoverFileReplicationService<RetryConnector,RetryConnectorFactory> {
        RetryFailoverFileReplicationService(RetryConnector connector) {
            super(connector, Integer.class, FailoverFileReplication.class);
        }
    }
    final RetryFailoverFileReplicationService failoverFileReplications;
    public FailoverFileReplicationService<RetryConnector,RetryConnectorFactory> getFailoverFileReplications() {
        return failoverFileReplications;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="FailoverFileScheduleService">
    static class RetryFailoverFileScheduleService extends RetryService<Integer,FailoverFileSchedule> implements FailoverFileScheduleService<RetryConnector,RetryConnectorFactory> {
        RetryFailoverFileScheduleService(RetryConnector connector) {
            super(connector, Integer.class, FailoverFileSchedule.class);
        }
    }
    final RetryFailoverFileScheduleService failoverFileSchedules;
    public FailoverFileScheduleService<RetryConnector,RetryConnectorFactory> getFailoverFileSchedules() {
        return failoverFileSchedules;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="FailoverMySQLReplicationService">
    static class RetryFailoverMySQLReplicationService extends RetryService<Integer,FailoverMySQLReplication> implements FailoverMySQLReplicationService<RetryConnector,RetryConnectorFactory> {
        RetryFailoverMySQLReplicationService(RetryConnector connector) {
            super(connector, Integer.class, FailoverMySQLReplication.class);
        }
    }
    final RetryFailoverMySQLReplicationService failoverMySQLReplications;
    public FailoverMySQLReplicationService<RetryConnector,RetryConnectorFactory> getFailoverMySQLReplications() {
        return failoverMySQLReplications;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="FileBackupSettingService">
    static class RetryFileBackupSettingService extends RetryService<Integer,FileBackupSetting> implements FileBackupSettingService<RetryConnector,RetryConnectorFactory> {
        RetryFileBackupSettingService(RetryConnector connector) {
            super(connector, Integer.class, FileBackupSetting.class);
        }
    }
    final RetryFileBackupSettingService fileBackupSettings;
    public FileBackupSettingService<RetryConnector,RetryConnectorFactory> getFileBackupSettings() {
        return fileBackupSettings;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="FTPGuestUserService">
    // TODO: final RetryFTPGuestUserService ftpGuestUsers;
    // TODO: public FTPGuestUserService<RetryConnector,RetryConnectorFactory> getFtpGuestUsers();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="GroupNameService">
    static class RetryGroupNameService extends RetryService<GroupId,GroupName> implements GroupNameService<RetryConnector,RetryConnectorFactory> {
        RetryGroupNameService(RetryConnector connector) {
            super(connector, GroupId.class, GroupName.class);
        }
    }
    final RetryGroupNameService groupNames;
    public GroupNameService<RetryConnector,RetryConnectorFactory> getGroupNames() {
        return groupNames;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdBindService">
    // TODO: final RetryHttpdBindService httpdBinds;
    // TODO: public HttpdBindService<RetryConnector,RetryConnectorFactory> getHttpdBinds();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdJBossSiteService">
    // TODO: final RetryHttpdJBossSiteService httpdJBossSites;
    // TODO: public HttpdJBossSiteService<RetryConnector,RetryConnectorFactory> getHttpdJBossSites();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdJBossVersionService">
    // TODO: final RetryHttpdJBossVersionService httpdJBossVersions;
    // TODO: public HttpdJBossVersionService<RetryConnector,RetryConnectorFactory> getHttpdJBossVersions();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdJKCodeService">
    // TODO: final RetryHttpdJKCodeService httpdJKCodes;
    // TODO: public HttpdJKCodeService<RetryConnector,RetryConnectorFactory> getHttpdJKCodes();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdJKProtocolService">
    // TODO: final RetryHttpdJKProtocolService httpdJKProtocols;
    // TODO: public HttpdJKProtocolService<RetryConnector,RetryConnectorFactory> getHttpdJKProtocols();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdServerService">
    // TODO: final RetryHttpdServerService httpdServers;
    // TODO: public HttpdServerService<RetryConnector,RetryConnectorFactory> getHttpdServers();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdSharedTomcatService">
    // TODO: final RetryHttpdSharedTomcatService httpdSharedTomcats;
    // TODO: public HttpdSharedTomcatService<RetryConnector,RetryConnectorFactory> getHttpdSharedTomcats();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdSiteAuthenticatedLocationService">
    // TODO: final RetryHttpdSiteAuthenticatedLocationService httpdSiteAuthenticatedLocations;
    // TODO: public HttpdSiteAuthenticatedLocationService<RetryConnector,RetryConnectorFactory> getHttpdSiteAuthenticatedLocations();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdSiteBindService">
    // TODO: final RetryHttpdSiteBindService httpdSiteBinds;
    // TODO: public HttpdSiteBindService<RetryConnector,RetryConnectorFactory> getHttpdSiteBinds();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdSiteURLService">
    // TODO: final RetryHttpdSiteURLService httpdSiteURLs;
    // TODO: public HttpdSiteURLService<RetryConnector,RetryConnectorFactory> getHttpdSiteURLs();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdSiteService">
    static class RetryHttpdSiteService extends RetryService<Integer,HttpdSite> implements HttpdSiteService<RetryConnector,RetryConnectorFactory> {
        RetryHttpdSiteService(RetryConnector connector) {
            super(connector, Integer.class, HttpdSite.class);
        }
    }
    final RetryHttpdSiteService httpdSites;
    public HttpdSiteService<RetryConnector,RetryConnectorFactory> getHttpdSites() {
        return httpdSites;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdStaticSiteService">
    // TODO: final RetryHttpdStaticSiteService httpdStaticSites;
    // TODO: public HttpdStaticSiteService<RetryConnector,RetryConnectorFactory> getHttpdStaticSites();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdTomcatContextService">
    // TODO: final RetryHttpdTomcatContextService httpdTomcatContexts;
    // TODO: public HttpdTomcatContextService<RetryConnector,RetryConnectorFactory> getHttpdTomcatContexts();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdTomcatDataSourceService">
    // TODO: final RetryHttpdTomcatDataSourceService httpdTomcatDataSources;
    // TODO: public HttpdTomcatDataSourceService<RetryConnector,RetryConnectorFactory> getHttpdTomcatDataSources();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdTomcatParameterService">
    // TODO: final RetryHttpdTomcatParameterService httpdTomcatParameters;
    // TODO: public HttpdTomcatParameterService<RetryConnector,RetryConnectorFactory> getHttpdTomcatParameters();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdTomcatSiteService">
    // TODO: final RetryHttpdTomcatSiteService httpdTomcatSites;
    // TODO: public HttpdTomcatSiteService<RetryConnector,RetryConnectorFactory> getHttpdTomcatSites();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdTomcatSharedSiteService">
    // TODO: final RetryHttpdTomcatSharedSiteService httpdTomcatSharedSites;
    // TODO: public HttpdTomcatSharedSiteService<RetryConnector,RetryConnectorFactory> getHttpdTomcatSharedSites();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdTomcatStdSiteService">
    // TODO: final RetryHttpdTomcatStdSiteService httpdTomcatStdSites;
    // TODO: public HttpdTomcatStdSiteService<RetryConnector,RetryConnectorFactory> getHttpdTomcatStdSites();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdTomcatVersionService">
    // TODO: final RetryHttpdTomcatVersionService httpdTomcatVersions;
    // TODO: public HttpdTomcatVersionService<RetryConnector,RetryConnectorFactory> getHttpdTomcatVersions();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdWorkerService">
    // TODO: final RetryHttpdWorkerService httpdWorkers;
    // TODO: public HttpdWorkerService<RetryConnector,RetryConnectorFactory> getHttpdWorkers();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="IPAddressService">
    static class RetryIPAddressService extends RetryService<Integer,IPAddress> implements IPAddressService<RetryConnector,RetryConnectorFactory> {
        RetryIPAddressService(RetryConnector connector) {
            super(connector, Integer.class, IPAddress.class);
        }
    }
    final RetryIPAddressService ipAddresses;
    public IPAddressService<RetryConnector,RetryConnectorFactory> getIpAddresses() {
        return ipAddresses;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="LanguageService">
    static class RetryLanguageService extends RetryService<String,Language> implements LanguageService<RetryConnector,RetryConnectorFactory> {
        RetryLanguageService(RetryConnector connector) {
            super(connector, String.class, Language.class);
        }
    }
    final RetryLanguageService languages;
    public LanguageService<RetryConnector,RetryConnectorFactory> getLanguages() {
        return languages;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="LinuxAccAddressService">
    // TODO: final RetryLinuxAccAddressService linuxAccAddresss;
    // TODO: public LinuxAccAddressService<RetryConnector,RetryConnectorFactory> getLinuxAccAddresses();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="LinuxAccountGroupService">
    static class RetryLinuxAccountGroupService extends RetryService<Integer,LinuxAccountGroup> implements LinuxAccountGroupService<RetryConnector,RetryConnectorFactory> {
        RetryLinuxAccountGroupService(RetryConnector connector) {
            super(connector, Integer.class, LinuxAccountGroup.class);
        }
    }
    final RetryLinuxAccountGroupService linuxAccountGroups;
    public LinuxAccountGroupService<RetryConnector,RetryConnectorFactory> getLinuxAccountGroups() {
        return linuxAccountGroups;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="LinuxAccountTypeService">
    static class RetryLinuxAccountTypeService extends RetryService<String,LinuxAccountType> implements LinuxAccountTypeService<RetryConnector,RetryConnectorFactory> {
        RetryLinuxAccountTypeService(RetryConnector connector) {
            super(connector, String.class, LinuxAccountType.class);
        }
    }
    final RetryLinuxAccountTypeService linuxAccountTypes;
    public LinuxAccountTypeService<RetryConnector,RetryConnectorFactory> getLinuxAccountTypes() {
        return linuxAccountTypes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="LinuxAccountService">
    static class RetryLinuxAccountService extends RetryService<Integer,LinuxAccount> implements LinuxAccountService<RetryConnector,RetryConnectorFactory> {
        RetryLinuxAccountService(RetryConnector connector) {
            super(connector, Integer.class, LinuxAccount.class);
        }
    }
    final RetryLinuxAccountService linuxAccounts;
    public LinuxAccountService<RetryConnector,RetryConnectorFactory> getLinuxAccounts() {
        return linuxAccounts;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="LinuxGroupTypeService">
    static class RetryLinuxGroupTypeService extends RetryService<String,LinuxGroupType> implements LinuxGroupTypeService<RetryConnector,RetryConnectorFactory> {
        RetryLinuxGroupTypeService(RetryConnector connector) {
            super(connector, String.class, LinuxGroupType.class);
        }
    }
    final RetryLinuxGroupTypeService linuxGroupTypes;
    public LinuxGroupTypeService<RetryConnector,RetryConnectorFactory> getLinuxGroupTypes() {
        return linuxGroupTypes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="LinuxGroupService">
    static class RetryLinuxGroupService extends RetryService<Integer,LinuxGroup> implements LinuxGroupService<RetryConnector,RetryConnectorFactory> {
        RetryLinuxGroupService(RetryConnector connector) {
            super(connector, Integer.class, LinuxGroup.class);
        }
    }
    final RetryLinuxGroupService linuxGroups;
    public LinuxGroupService<RetryConnector,RetryConnectorFactory> getLinuxGroups() {
        return linuxGroups;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MajordomoListService">
    // TODO: final RetryMajordomoListService majordomoLists;
    // TODO: public MajordomoListService<RetryConnector,RetryConnectorFactory> getMajordomoLists();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MajordomoServerService">
    // TODO: final RetryMajordomoServerService majordomoServers;
    // TODO: public MajordomoServerService<RetryConnector,RetryConnectorFactory> getMajordomoServers();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MajordomoVersionService">
    // TODO: final RetryMajordomoVersionService majordomoVersions;
    // TODO: public MajordomoVersionService<RetryConnector,RetryConnectorFactory> getMajordomoVersions();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MasterHistoryService">
    // TODO: final RetryMasterHistoryService masterHistories;
    // TODO: public MasterHistoryService<RetryConnector,RetryConnectorFactory> getMasterHistory();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MasterHostService">
    // TODO: final RetryMasterHostService masterHosts;
    // TODO: public MasterHostService<RetryConnector,RetryConnectorFactory> getMasterHosts();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MasterServerService">
    // TODO: final RetryMasterServerService masterServers;
    // TODO: public MasterServerService<RetryConnector,RetryConnectorFactory> getMasterServers();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MasterUserService">
    // TODO: final RetryMasterUserService masterUsers;
    // TODO: public MasterUserService<RetryConnector,RetryConnectorFactory> getMasterUsers();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MonthlyChargeService">
    // TODO: final RetryMonthlyChargeService monthlyCharges;
    // TODO: public MonthlyChargeService<RetryConnector,RetryConnectorFactory> getMonthlyCharges();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MySQLDatabaseService">
    static class RetryMySQLDatabaseService extends RetryService<Integer,MySQLDatabase> implements MySQLDatabaseService<RetryConnector,RetryConnectorFactory> {
        RetryMySQLDatabaseService(RetryConnector connector) {
            super(connector, Integer.class, MySQLDatabase.class);
        }
    }
    final RetryMySQLDatabaseService mysqlDatabases;
    public MySQLDatabaseService<RetryConnector,RetryConnectorFactory> getMysqlDatabases() {
        return mysqlDatabases;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MySQLDBUserService">
    static class RetryMySQLDBUserService extends RetryService<Integer,MySQLDBUser> implements MySQLDBUserService<RetryConnector,RetryConnectorFactory> {
        RetryMySQLDBUserService(RetryConnector connector) {
            super(connector, Integer.class, MySQLDBUser.class);
        }
    }
    final RetryMySQLDBUserService mysqlDBUsers;
    public MySQLDBUserService<RetryConnector,RetryConnectorFactory> getMysqlDBUsers() {
        return mysqlDBUsers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MySQLServerService">
    static class RetryMySQLServerService extends RetryService<Integer,MySQLServer> implements MySQLServerService<RetryConnector,RetryConnectorFactory> {
        RetryMySQLServerService(RetryConnector connector) {
            super(connector, Integer.class, MySQLServer.class);
        }
    }
    final RetryMySQLServerService mysqlServers;
    public MySQLServerService<RetryConnector,RetryConnectorFactory> getMysqlServers() {
        return mysqlServers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MySQLUserService">
    static class RetryMySQLUserService extends RetryService<Integer,MySQLUser> implements MySQLUserService<RetryConnector,RetryConnectorFactory> {
        RetryMySQLUserService(RetryConnector connector) {
            super(connector, Integer.class, MySQLUser.class);
        }
    }
    final RetryMySQLUserService mysqlUsers;
    public MySQLUserService<RetryConnector,RetryConnectorFactory> getMysqlUsers() {
        return mysqlUsers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="NetBindService">
    static class RetryNetBindService extends RetryService<Integer,NetBind> implements NetBindService<RetryConnector,RetryConnectorFactory> {
        RetryNetBindService(RetryConnector connector) {
            super(connector, Integer.class, NetBind.class);
        }
    }
    final RetryNetBindService netBinds;
    public NetBindService<RetryConnector,RetryConnectorFactory> getNetBinds() {
        return netBinds;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="NetDeviceIDService">
    static class RetryNetDeviceIDService extends RetryService<String,NetDeviceID> implements NetDeviceIDService<RetryConnector,RetryConnectorFactory> {
        RetryNetDeviceIDService(RetryConnector connector) {
            super(connector, String.class, NetDeviceID.class);
        }
    }
    final RetryNetDeviceIDService netDeviceIDs;
    public NetDeviceIDService<RetryConnector,RetryConnectorFactory> getNetDeviceIDs() {
        return netDeviceIDs;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="NetDeviceService">
    static class RetryNetDeviceService extends RetryService<Integer,NetDevice> implements NetDeviceService<RetryConnector,RetryConnectorFactory> {
        RetryNetDeviceService(RetryConnector connector) {
            super(connector, Integer.class, NetDevice.class);
        }
    }
    final RetryNetDeviceService netDevices;
    public NetDeviceService<RetryConnector,RetryConnectorFactory> getNetDevices() {
        return netDevices;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="NetProtocolService">
    static class RetryNetProtocolService extends RetryService<String,NetProtocol> implements NetProtocolService<RetryConnector,RetryConnectorFactory> {
        RetryNetProtocolService(RetryConnector connector) {
            super(connector, String.class, NetProtocol.class);
        }
    }
    final RetryNetProtocolService netProtocols;
    public NetProtocolService<RetryConnector,RetryConnectorFactory> getNetProtocols() {
        return netProtocols;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="NetTcpRedirectService">
    static class RetryNetTcpRedirectService extends RetryService<Integer,NetTcpRedirect> implements NetTcpRedirectService<RetryConnector,RetryConnectorFactory> {
        RetryNetTcpRedirectService(RetryConnector connector) {
            super(connector, Integer.class, NetTcpRedirect.class);
        }
    }
    final RetryNetTcpRedirectService netTcpRedirects;
    public NetTcpRedirectService<RetryConnector,RetryConnectorFactory> getNetTcpRedirects() {
        return netTcpRedirects;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="NoticeLogService">
    // TODO: final RetryNoticeLogService noticeLogs;
    // TODO: public NoticeLogService<RetryConnector,RetryConnectorFactory> getNoticeLogs();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="NoticeTypeService">
    // TODO: final RetryNoticeTypeService noticeTypes;
    // TODO: public NoticeTypeService<RetryConnector,RetryConnectorFactory> getNoticeTypes();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="OperatingSystemVersionService">
    static class RetryOperatingSystemVersionService extends RetryService<Integer,OperatingSystemVersion> implements OperatingSystemVersionService<RetryConnector,RetryConnectorFactory> {
        RetryOperatingSystemVersionService(RetryConnector connector) {
            super(connector, Integer.class, OperatingSystemVersion.class);
        }
    }
    final RetryOperatingSystemVersionService operatingSystemVersions;
    public OperatingSystemVersionService<RetryConnector,RetryConnectorFactory> getOperatingSystemVersions() {
        return operatingSystemVersions;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="OperatingSystemService">
    static class RetryOperatingSystemService extends RetryService<String,OperatingSystem> implements OperatingSystemService<RetryConnector,RetryConnectorFactory> {
        RetryOperatingSystemService(RetryConnector connector) {
            super(connector, String.class, OperatingSystem.class);
        }
    }
    final RetryOperatingSystemService operatingSystems;
    public OperatingSystemService<RetryConnector,RetryConnectorFactory> getOperatingSystems() {
        return operatingSystems;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PackageCategoryService">
    static class RetryPackageCategoryService extends RetryService<String,PackageCategory> implements PackageCategoryService<RetryConnector,RetryConnectorFactory> {
        RetryPackageCategoryService(RetryConnector connector) {
            super(connector, String.class, PackageCategory.class);
        }
    }
    final RetryPackageCategoryService packageCategories;
    public PackageCategoryService<RetryConnector,RetryConnectorFactory> getPackageCategories() {
        return packageCategories;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PackageDefinitionLimitService">
    // TODO: final RetryPackageDefinitionLimitService packageDefinitionLimits;
    // TODO: public PackageDefinitionLimitService<RetryConnector,RetryConnectorFactory> getPackageDefinitionLimits();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PackageDefinitionService">
    // TODO: final RetryPackageDefinitionService packageDefinitions;
    // TODO: public PackageDefinitionService<RetryConnector,RetryConnectorFactory> getPackageDefinitions();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PaymentTypeService">
    // TODO: final RetryPaymentTypeService paymentTypes;
    // TODO: public PaymentTypeService<RetryConnector,RetryConnectorFactory> getPaymentTypes();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PhysicalServerService">
    // TODO: final RetryPhysicalServerService physicalServers;
    // TODO: public PhysicalServerService<RetryConnector,RetryConnectorFactory> getPhysicalServers();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PostgresDatabaseService">
    static class RetryPostgresDatabaseService extends RetryService<Integer,PostgresDatabase> implements PostgresDatabaseService<RetryConnector,RetryConnectorFactory> {
        RetryPostgresDatabaseService(RetryConnector connector) {
            super(connector, Integer.class, PostgresDatabase.class);
        }
    }
    final RetryPostgresDatabaseService postgresDatabases;
    public PostgresDatabaseService<RetryConnector,RetryConnectorFactory> getPostgresDatabases() {
        return postgresDatabases;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PostgresEncodingService">
    static class RetryPostgresEncodingService extends RetryService<Integer,PostgresEncoding> implements PostgresEncodingService<RetryConnector,RetryConnectorFactory> {
        RetryPostgresEncodingService(RetryConnector connector) {
            super(connector, Integer.class, PostgresEncoding.class);
        }
    }
    final RetryPostgresEncodingService postgresEncodings;
    public PostgresEncodingService<RetryConnector,RetryConnectorFactory> getPostgresEncodings() {
        return postgresEncodings;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PostgresServerService">
    static class RetryPostgresServerService extends RetryService<Integer,PostgresServer> implements PostgresServerService<RetryConnector,RetryConnectorFactory> {
        RetryPostgresServerService(RetryConnector connector) {
            super(connector, Integer.class, PostgresServer.class);
        }
    }
    final RetryPostgresServerService postgresServers;
    public PostgresServerService<RetryConnector,RetryConnectorFactory> getPostgresServers() {
        return postgresServers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PostgresUserService">
    static class RetryPostgresUserService extends RetryService<Integer,PostgresUser> implements PostgresUserService<RetryConnector,RetryConnectorFactory> {
        RetryPostgresUserService(RetryConnector connector) {
            super(connector, Integer.class, PostgresUser.class);
        }
    }
    final RetryPostgresUserService postgresUsers;
    public PostgresUserService<RetryConnector,RetryConnectorFactory> getPostgresUsers() {
        return postgresUsers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PostgresVersionService">
    static class RetryPostgresVersionService extends RetryService<Integer,PostgresVersion> implements PostgresVersionService<RetryConnector,RetryConnectorFactory> {
        RetryPostgresVersionService(RetryConnector connector) {
            super(connector, Integer.class, PostgresVersion.class);
        }
    }
    final RetryPostgresVersionService postgresVersions;
    public PostgresVersionService<RetryConnector,RetryConnectorFactory> getPostgresVersions() {
        return postgresVersions;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PrivateFTPServerService">
    // TODO: final RetryPrivateFTPServerService privateFTPServers;
    // TODO: public PrivateFTPServerService<RetryConnector,RetryConnectorFactory> getPrivateFTPServers();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ProcessorTypeService">
    // TODO: final RetryProcessorTypeService processorTypes;
    // TODO: public ProcessorTypeService<RetryConnector,RetryConnectorFactory> getProcessorTypes();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ProtocolService">
    static class RetryProtocolService extends RetryService<String,Protocol> implements ProtocolService<RetryConnector,RetryConnectorFactory> {
        RetryProtocolService(RetryConnector connector) {
            super(connector, String.class, Protocol.class);
        }
    }
    final RetryProtocolService protocols;
    public ProtocolService<RetryConnector,RetryConnectorFactory> getProtocols() {
        return protocols;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="RackService">
    // TODO: final RetryRackService racks;
    // TODO: public RackService<RetryConnector,RetryConnectorFactory> getRacks();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ResellerService">
    static class RetryResellerService extends RetryService<AccountingCode,Reseller> implements ResellerService<RetryConnector,RetryConnectorFactory> {
        RetryResellerService(RetryConnector connector) {
            super(connector, AccountingCode.class, Reseller.class);
        }
    }
    final RetryResellerService resellers;
    public ResellerService<RetryConnector,RetryConnectorFactory> getResellers() {
        return resellers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ResourceTypeService">
    static class RetryResourceTypeService extends RetryService<String,ResourceType> implements ResourceTypeService<RetryConnector,RetryConnectorFactory> {
        RetryResourceTypeService(RetryConnector connector) {
            super(connector, String.class, ResourceType.class);
        }
    }
    final RetryResourceTypeService resourceTypes;
    public ResourceTypeService<RetryConnector,RetryConnectorFactory> getResourceTypes() {
        return resourceTypes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ResourceService">
    static class RetryResourceService extends RetryService<Integer,Resource> implements ResourceService<RetryConnector,RetryConnectorFactory> {
        RetryResourceService(RetryConnector connector) {
            super(connector, Integer.class, Resource.class);
        }
    }
    final RetryResourceService resources;
    public ResourceService<RetryConnector,RetryConnectorFactory> getResources() {
        return resources;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ServerFarmService">
    static class RetryServerFarmService extends RetryService<DomainLabel,ServerFarm> implements ServerFarmService<RetryConnector,RetryConnectorFactory> {
        RetryServerFarmService(RetryConnector connector) {
            super(connector, DomainLabel.class, ServerFarm.class);
        }
    }
    final RetryServerFarmService serverFarms;
    public ServerFarmService<RetryConnector,RetryConnectorFactory> getServerFarms() {
        return serverFarms;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ServerResourceService">
    static class RetryServerResourceService extends RetryService<Integer,ServerResource> implements ServerResourceService<RetryConnector,RetryConnectorFactory> {
        RetryServerResourceService(RetryConnector connector) {
            super(connector, Integer.class, ServerResource.class);
        }
    }
    final RetryServerResourceService serverResources;
    public ServerResourceService<RetryConnector,RetryConnectorFactory> getServerResources() {
        return serverResources;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ServerService">
    static class RetryServerService extends RetryService<Integer,Server> implements ServerService<RetryConnector,RetryConnectorFactory> {
        RetryServerService(RetryConnector connector) {
            super(connector, Integer.class, Server.class);
        }
    }
    final RetryServerService servers;
    public ServerService<RetryConnector,RetryConnectorFactory> getServers() {
        return servers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ShellService">
    static class RetryShellService extends RetryService<UnixPath,Shell> implements ShellService<RetryConnector,RetryConnectorFactory> {
        RetryShellService(RetryConnector connector) {
            super(connector, UnixPath.class, Shell.class);
        }
    }
    final RetryShellService shells;
    public ShellService<RetryConnector,RetryConnectorFactory> getShells() {
        return shells;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="SignupRequestOptionService">
    // TODO: final RetrySignupRequestOptionService signupRequestOptions;
    // TODO: public SignupRequestOptionService<RetryConnector,RetryConnectorFactory> getSignupRequestOptions();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="SignupRequestService">
    // TODO: final RetrySignupRequestService signupRequests;
    // TODO: public SignupRequestService<RetryConnector,RetryConnectorFactory> getSignupRequests();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="SpamEmailMessageService">
    // TODO: final RetrySpamEmailMessageService spamEmailMessages;
    // TODO: public SpamEmailMessageService<RetryConnector,RetryConnectorFactory> getSpamEmailMessages();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="SystemEmailAliasService">
    // TODO: final RetrySystemEmailAliasService systemEmailAliass;
    // TODO: public SystemEmailAliasService<RetryConnector,RetryConnectorFactory> getSystemEmailAliases();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TechnologyService">
    final class RetryTechnologyService extends RetryService<Integer,Technology> implements TechnologyService<RetryConnector,RetryConnectorFactory> {
        RetryTechnologyService(RetryConnector connector) {
            super(connector, Integer.class, Technology.class);
        }
    }
    final RetryTechnologyService technologies;
    public TechnologyService<RetryConnector,RetryConnectorFactory> getTechnologies() {
        return technologies;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TechnologyClassService">
    static class RetryTechnologyClassService extends RetryService<String,TechnologyClass> implements TechnologyClassService<RetryConnector,RetryConnectorFactory> {
        RetryTechnologyClassService(RetryConnector connector) {
            super(connector, String.class, TechnologyClass.class);
        }
    }
    final RetryTechnologyClassService technologyClasses;
    public TechnologyClassService<RetryConnector,RetryConnectorFactory> getTechnologyClasses() {
        return technologyClasses;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TechnologyNameService">
    static class RetryTechnologyNameService extends RetryService<String,TechnologyName> implements TechnologyNameService<RetryConnector,RetryConnectorFactory> {
        RetryTechnologyNameService(RetryConnector connector) {
            super(connector, String.class, TechnologyName.class);
        }
    }
    final RetryTechnologyNameService technologyNames;
    public TechnologyNameService<RetryConnector,RetryConnectorFactory> getTechnologyNames() {
        return technologyNames;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TechnologyVersionService">
    static class RetryTechnologyVersionService extends RetryService<Integer,TechnologyVersion> implements TechnologyVersionService<RetryConnector,RetryConnectorFactory> {
        RetryTechnologyVersionService(RetryConnector connector) {
            super(connector, Integer.class, TechnologyVersion.class);
        }
    }
    final RetryTechnologyVersionService technologyVersions;
    public TechnologyVersionService<RetryConnector,RetryConnectorFactory> getTechnologyVersions() {
        return technologyVersions;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TicketActionTypeService">
    // TODO: final RetryTicketActionTypeService ticketActionTypes;
    // TODO: public TicketActionTypeService<RetryConnector,RetryConnectorFactory> getTicketActionTypes();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TicketActionService">
    // TODO: final RetryTicketActionService ticketActions;
    // TODO: public TicketActionService<RetryConnector,RetryConnectorFactory> getTicketActions();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TicketAssignmentService">
    static class RetryTicketAssignmentService extends RetryService<Integer,TicketAssignment> implements TicketAssignmentService<RetryConnector,RetryConnectorFactory> {
        RetryTicketAssignmentService(RetryConnector connector) {
            super(connector, Integer.class, TicketAssignment.class);
        }
    }
    final RetryTicketAssignmentService ticketAssignments;
    public TicketAssignmentService<RetryConnector,RetryConnectorFactory> getTicketAssignments() {
        return ticketAssignments;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TicketBrandCategoryService">
    // TODO: final RetryTicketBrandCategoryService ticketBrandCategories;
    // TODO: public TicketBrandCategoryService<RetryConnector,RetryConnectorFactory> getTicketBrandCategories();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TicketCategoryService">
    static class RetryTicketCategoryService extends RetryService<Integer,TicketCategory> implements TicketCategoryService<RetryConnector,RetryConnectorFactory> {
        RetryTicketCategoryService(RetryConnector connector) {
            super(connector, Integer.class, TicketCategory.class);
        }
    }
    final RetryTicketCategoryService ticketCategories;
    public TicketCategoryService<RetryConnector,RetryConnectorFactory> getTicketCategories() {
        return ticketCategories;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TicketPriorityService">
    static class RetryTicketPriorityService extends RetryService<String,TicketPriority> implements TicketPriorityService<RetryConnector,RetryConnectorFactory> {
        RetryTicketPriorityService(RetryConnector connector) {
            super(connector, String.class, TicketPriority.class);
        }
    }
    final RetryTicketPriorityService ticketPriorities;
    public TicketPriorityService<RetryConnector,RetryConnectorFactory> getTicketPriorities() {
        return ticketPriorities;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TicketStatusService">
    static class RetryTicketStatusService extends RetryService<String,TicketStatus> implements TicketStatusService<RetryConnector,RetryConnectorFactory> {
        RetryTicketStatusService(RetryConnector connector) {
            super(connector, String.class, TicketStatus.class);
        }
    }
    final RetryTicketStatusService ticketStatuses;
    public TicketStatusService<RetryConnector,RetryConnectorFactory> getTicketStatuses() {
        return ticketStatuses;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TicketTypeService">
    static class RetryTicketTypeService extends RetryService<String,TicketType> implements TicketTypeService<RetryConnector,RetryConnectorFactory> {
        RetryTicketTypeService(RetryConnector connector) {
            super(connector, String.class, TicketType.class);
        }
    }
    final RetryTicketTypeService ticketTypes;
    public TicketTypeService<RetryConnector,RetryConnectorFactory> getTicketTypes() {
        return ticketTypes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TicketService">
    static class RetryTicketService extends RetryService<Integer,Ticket> implements TicketService<RetryConnector,RetryConnectorFactory> {
        RetryTicketService(RetryConnector connector) {
            super(connector, Integer.class, Ticket.class);
        }
    }
    final RetryTicketService tickets;
    public TicketService<RetryConnector,RetryConnectorFactory> getTickets() {
        return tickets;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TimeZoneService">
    static class RetryTimeZoneService extends RetryService<String,TimeZone> implements TimeZoneService<RetryConnector,RetryConnectorFactory> {
        RetryTimeZoneService(RetryConnector connector) {
            super(connector, String.class, TimeZone.class);
        }
    }
    final RetryTimeZoneService timeZones;
    public TimeZoneService<RetryConnector,RetryConnectorFactory> getTimeZones() {
        return timeZones;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TransactionTypeService">
    // TODO: final RetryTransactionTypeService transactionTypes;
    // TODO: public TransactionTypeService<RetryConnector,RetryConnectorFactory> getTransactionTypes();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TransactionService">
    // TODO: final RetryTransactionService transactions;
    // TODO: public TransactionService<RetryConnector,RetryConnectorFactory> getTransactions();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="UsernameService">
    static class RetryUsernameService extends RetryService<UserId,Username> implements UsernameService<RetryConnector,RetryConnectorFactory> {
        RetryUsernameService(RetryConnector connector) {
            super(connector, UserId.class, Username.class);
        }
    }
    final RetryUsernameService usernames;
    public UsernameService<RetryConnector,RetryConnectorFactory> getUsernames() {
        return usernames;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="VirtualDiskService">
    // TODO: final RetryVirtualDiskService virtualDisks;
    // TODO: public VirtualDiskService<RetryConnector,RetryConnectorFactory> getVirtualDisks();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="VirtualServerService">
    // TODO: final RetryVirtualServerService virtualServers;
    // TODO: public VirtualServerService<RetryConnector,RetryConnectorFactory> getVirtualServers();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="WhoisHistoryService">
    // TODO: final RetryWhoisHistoryService whoisHistories;
    // TODO: public WhoisHistoryService<RetryConnector,RetryConnectorFactory> getWhoisHistory();
    // </editor-fold>
}
