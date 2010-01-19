package com.aoindustries.aoserv.client.wrapped;

/*
 * Copyright 2010 by AO Industries, Inc.,
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
import com.aoindustries.aoserv.client.BankTransactionType;
import com.aoindustries.aoserv.client.BankTransactionTypeService;
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
import com.aoindustries.aoserv.client.EmailAttachmentType;
import com.aoindustries.aoserv.client.EmailAttachmentTypeService;
import com.aoindustries.aoserv.client.EmailInbox;
import com.aoindustries.aoserv.client.EmailInboxService;
import com.aoindustries.aoserv.client.EmailSmtpRelayType;
import com.aoindustries.aoserv.client.EmailSmtpRelayTypeService;
import com.aoindustries.aoserv.client.EmailSpamAssassinIntegrationMode;
import com.aoindustries.aoserv.client.EmailSpamAssassinIntegrationModeService;
import com.aoindustries.aoserv.client.ExpenseCategory;
import com.aoindustries.aoserv.client.ExpenseCategoryService;
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
import com.aoindustries.aoserv.client.HttpdJBossVersion;
import com.aoindustries.aoserv.client.HttpdJBossVersionService;
import com.aoindustries.aoserv.client.HttpdJKCode;
import com.aoindustries.aoserv.client.HttpdJKCodeService;
import com.aoindustries.aoserv.client.HttpdJKProtocol;
import com.aoindustries.aoserv.client.HttpdJKProtocolService;
import com.aoindustries.aoserv.client.HttpdServer;
import com.aoindustries.aoserv.client.HttpdServerService;
import com.aoindustries.aoserv.client.HttpdSite;
import com.aoindustries.aoserv.client.HttpdSiteService;
import com.aoindustries.aoserv.client.HttpdTomcatVersion;
import com.aoindustries.aoserv.client.HttpdTomcatVersionService;
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
import com.aoindustries.aoserv.client.MajordomoVersion;
import com.aoindustries.aoserv.client.MajordomoVersionService;
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
import com.aoindustries.aoserv.client.NoticeType;
import com.aoindustries.aoserv.client.NoticeTypeService;
import com.aoindustries.aoserv.client.OperatingSystem;
import com.aoindustries.aoserv.client.OperatingSystemService;
import com.aoindustries.aoserv.client.OperatingSystemVersion;
import com.aoindustries.aoserv.client.OperatingSystemVersionService;
import com.aoindustries.aoserv.client.PackageCategory;
import com.aoindustries.aoserv.client.PackageCategoryService;
import com.aoindustries.aoserv.client.PaymentType;
import com.aoindustries.aoserv.client.PaymentTypeService;
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
import com.aoindustries.aoserv.client.PrivateFtpServer;
import com.aoindustries.aoserv.client.PrivateFtpServerService;
import com.aoindustries.aoserv.client.ProcessorType;
import com.aoindustries.aoserv.client.ProcessorTypeService;
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
import com.aoindustries.aoserv.client.TicketActionType;
import com.aoindustries.aoserv.client.TicketActionTypeService;
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
import com.aoindustries.aoserv.client.TransactionType;
import com.aoindustries.aoserv.client.TransactionTypeService;
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
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @see  WrappedConnectorFactory
 *
 * @author  AO Industries, Inc.
 */
abstract public class WrappedConnector<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> implements AOServConnector<C,F> {

    protected final F factory;
    Locale locale;
    final UserId connectAs;
    private final UserId authenticateAs;
    private final String password;
    private final DomainName daemonServer;

    final Object connectionLock = new Object();
    private AOServConnector<?,?> wrapped;

    @SuppressWarnings("unchecked")
    protected WrappedConnector(F factory, Locale locale, UserId connectAs, UserId authenticateAs, String password, DomainName daemonServer) throws RemoteException, LoginException {
        this.factory = factory;
        this.locale = locale;
        this.connectAs = connectAs;
        this.authenticateAs = authenticateAs;
        this.password = password;
        this.daemonServer = daemonServer;
        aoserverDaemonHosts = new WrappedAOServerDaemonHostService(this);
        aoserverResources = new WrappedAOServerResourceService(this);
        aoservers = new WrappedAOServerService(this);
        aoservPermissions = new WrappedAOServPermissionService(this);
        /* TODO
        aoservProtocols = new WrappedAOServProtocolService(this);
        aoshCommands = new WrappedAOSHCommandService(this);
         */
        architectures = new WrappedArchitectureService(this);
        backupPartitions = new WrappedBackupPartitionService(this);
        backupRetentions = new WrappedBackupRetentionService(this);
        // TODO: bankAccounts = new WrappedBankAccountService(this);
        bankTransactionTypes = new WrappedBankTransactionTypeService(this);
        // TODO: bankTransactions = new WrappedBankTransactionService(this);
        // TODO: banks = new WrappedBankService(this);
        // TODO: blackholeEmailAddresss = new WrappedBlackholeEmailAddressService(this);
        brands = new WrappedBrandService(this);
        businessAdministrators = new WrappedBusinessAdministratorService(this);
        /* TODO
        businessAdministratorPermissions = new WrappedBusinessAdministratorPermissionService(this);
        businessProfiles = new WrappedBusinessProfileService(this);
         */
        businesses = new WrappedBusinessService(this);
        businessServers = new WrappedBusinessServerService(this);
        countryCodes = new WrappedCountryCodeService(this);
        /* TODO
        creditCardProcessors = new WrappedCreditCardProcessorService(this);
        creditCardTransactions = new WrappedCreditCardTransactionService(this);
        creditCards = new WrappedCreditCardService(this);
         */
        cvsRepositories = new WrappedCvsRepositoryService(this);
        disableLogs = new WrappedDisableLogService(this);
        /* TODO
        distroFileTypes = new WrappedDistroFileTypeService(this);
        distroFiles = new WrappedDistroFileService(this);
         */
        dnsRecords = new WrappedDnsRecordService(this);
        dnsTlds = new WrappedDnsTldService(this);
        dnsTypes = new WrappedDnsTypeService(this);
        dnsZones = new WrappedDnsZoneService(this);
        // TODO: emailAddresss = new WrappedEmailAddressService(this);
        // TODO: emailAttachmentBlocks = new WrappedEmailAttachmentBlockService(this);
        emailAttachmentTypes = new WrappedEmailAttachmentTypeService(this);
        // TODO: emailDomains = new WrappedEmailDomainService(this);
        // TODO: emailForwardings = new WrappedEmailForwardingService(this);
        emailInboxes = new WrappedEmailInboxService(this);
        // TODO: emailListAddresss = new WrappedEmailListAddressService(this);
        // TODO: emailLists = new WrappedEmailListService(this);
        // TODO: emailPipeAddresss = new WrappedEmailPipeAddressService(this);
        // TODO: emailPipes = new WrappedEmailPipeService(this);
        emailSmtpRelayTypes = new WrappedEmailSmtpRelayTypeService(this);
        // TODO: emailSmtpRelays = new WrappedEmailSmtpRelayService(this);
        // TODO: emailSmtpSmartHostDomains = new WrappedEmailSmtpSmartHostDomainService(this);
        // TODO: emailSmtpSmartHosts = new WrappedEmailSmtpSmartHostService(this);
        emailSpamAssassinIntegrationModes = new WrappedEmailSpamAssassinIntegrationModeService(this);
        // TODO: encryptionKeys = new WrappedEncryptionKeyService(this);
        expenseCategories = new WrappedExpenseCategoryService(this);
        failoverFileLogs = new WrappedFailoverFileLogService(this);
        failoverFileReplications = new WrappedFailoverFileReplicationService(this);
        failoverFileSchedules = new WrappedFailoverFileScheduleService(this);
        failoverMySQLReplications = new WrappedFailoverMySQLReplicationService(this);
        fileBackupSettings = new WrappedFileBackupSettingService(this);
        groupNames = new WrappedGroupNameService(this);
        ftpGuestUsers = new WrappedFtpGuestUserService(this);
        /* TODO
        httpdBinds = new WrappedHttpdBindService(this);
        httpdJBossSites = new WrappedHttpdJBossSiteService(this);
         */
        httpdJBossVersions = new WrappedHttpdJBossVersionService(this);
        httpdJKCodes = new WrappedHttpdJKCodeService(this);
        httpdJKProtocols = new WrappedHttpdJKProtocolService(this);
        httpdServers = new WrappedHttpdServerService(this);
        /* TODO
        httpdSharedTomcats = new WrappedHttpdSharedTomcatService(this);
        httpdSiteAuthenticatedLocations = new WrappedHttpdSiteAuthenticatedLocationService(this);
        httpdSiteBinds = new WrappedHttpdSiteBindService(this);
        httpdSiteURLs = new WrappedHttpdSiteURLService(this);
         */
        httpdSites = new WrappedHttpdSiteService(this);
        // TODO: httpdStaticSites = new WrappedHttpdStaticSiteService(this);
        // TODO: httpdTomcatContexts = new WrappedHttpdTomcatContextService(this);
        // TODO: httpdTomcatDataSources = new WrappedHttpdTomcatDataSourceService(this);
        // TODO: httpdTomcatParameters = new WrappedHttpdTomcatParameterService(this);
        // TODO: httpdTomcatSites = new WrappedHttpdTomcatSiteService(this);
        // TODO: httpdTomcatSharedSites = new WrappedHttpdTomcatSharedSiteService(this);
        // TODO: httpdTomcatStdSites = new WrappedHttpdTomcatStdSiteService(this);
        httpdTomcatVersions = new WrappedHttpdTomcatVersionService(this);
        // TODO: httpdWorkers = new WrappedHttpdWorkerService(this);
        ipAddresses = new WrappedIPAddressService(this);
        languages = new WrappedLanguageService(this);
        /* TODO
        linuxAccAddresss = new WrappedLinuxAccAddressService(this);
         */
        linuxAccountGroups = new WrappedLinuxAccountGroupService(this);
        linuxAccountTypes = new WrappedLinuxAccountTypeService(this);
        linuxAccounts = new WrappedLinuxAccountService(this);
        linuxGroupTypes = new WrappedLinuxGroupTypeService(this);
        linuxGroups = new WrappedLinuxGroupService(this);
        /* TODO
        majordomoLists = new WrappedMajordomoListService(this);
        majordomoServers = new WrappedMajordomoServerService(this);
         */
        majordomoVersions = new WrappedMajordomoVersionService(this);
        masterHosts = new WrappedMasterHostService(this);
        masterServers = new WrappedMasterServerService(this);
        masterUsers = new WrappedMasterUserService(this);
        // TODO: monthlyCharges = new WrappedMonthlyChargeService(this);
        mysqlDatabases = new WrappedMySQLDatabaseService(this);
        mysqlDBUsers = new WrappedMySQLDBUserService(this);
        mysqlServers = new WrappedMySQLServerService(this);
        mysqlUsers = new WrappedMySQLUserService(this);
        netBinds = new WrappedNetBindService(this);
        netDeviceIDs = new WrappedNetDeviceIDService(this);
        netDevices = new WrappedNetDeviceService(this);
        netProtocols = new WrappedNetProtocolService(this);
        netTcpRedirects = new WrappedNetTcpRedirectService(this);
        // TODO: noticeLogs = new WrappedNoticeLogService(this);
        noticeTypes = new WrappedNoticeTypeService(this);
        operatingSystemVersions = new WrappedOperatingSystemVersionService(this);
        operatingSystems = new WrappedOperatingSystemService(this);
        packageCategories = new WrappedPackageCategoryService(this);
        // TODO: packageDefinitionLimits = new WrappedPackageDefinitionLimitService(this);
        // TODO: packageDefinitions = new WrappedPackageDefinitionService(this);
        paymentTypes = new WrappedPaymentTypeService(this);
        // TODO: physicalServers = new WrappedPhysicalServerService(this);
        postgresDatabases = new WrappedPostgresDatabaseService(this);
        postgresEncodings = new WrappedPostgresEncodingService(this);
        postgresServers = new WrappedPostgresServerService(this);
        postgresUsers = new WrappedPostgresUserService(this);
        postgresVersions = new WrappedPostgresVersionService(this);
        privateFtpServers = new WrappedPrivateFtpServerService(this);
        processorTypes = new WrappedProcessorTypeService(this);
        protocols = new WrappedProtocolService(this);
        // TODO: racks = new WrappedRackService(this);
        resellers = new WrappedResellerService(this);
        resourceTypes = new WrappedResourceTypeService(this);
        resources = new WrappedResourceService(this);
        serverFarms = new WrappedServerFarmService(this);
        serverResources = new WrappedServerResourceService(this);
        servers = new WrappedServerService(this);
        shells = new WrappedShellService(this);
        /* TODO
        signupRequestOptions = new WrappedSignupRequestOptionService(this);
        signupRequests = new WrappedSignupRequestService(this);
        spamEmailMessages = new WrappedSpamEmailMessageService(this);
        systemEmailAliass = new WrappedSystemEmailAliasService(this);
         */
        technologies = new WrappedTechnologyService(this);
        technologyClasses = new WrappedTechnologyClassService(this);
        technologyNames = new WrappedTechnologyNameService(this);
        technologyVersions = new WrappedTechnologyVersionService(this);
        ticketActionTypes = new WrappedTicketActionTypeService(this);
        // TODO: ticketActions = new WrappedTicketActionService(this);
        ticketAssignments = new WrappedTicketAssignmentService(this);
        // TODO: ticketBrandCategories = new WrappedTicketBrandCategoryService(this);
        ticketCategories = new WrappedTicketCategoryService(this);
        ticketPriorities = new WrappedTicketPriorityService(this);
        ticketStatuses = new WrappedTicketStatusService(this);
        ticketTypes = new WrappedTicketTypeService(this);
        tickets = new WrappedTicketService(this);
        timeZones = new WrappedTimeZoneService(this);
        transactionTypes = new WrappedTransactionTypeService(this);
        /* TODO
        transactions = new WrappedTransactionService(this);
        usStates = new WrappedUSStateService(this);
         */
        usernames = new WrappedUsernameService(this);
        /* TODO
        virtualDisks = new WrappedVirtualDiskService(this);
        virtualServers = new WrappedVirtualServerService(this);
        whoisHistories = new WrappedWhoisHistoryService(this);
         */
        // Connect immediately in order to have the chance to throw exceptions that will occur during connection
        synchronized(connectionLock) {
            connect();
        }
    }

    @SuppressWarnings("unchecked")
    final void connect() throws RemoteException, LoginException {
        assert Thread.holdsLock(connectionLock);

        // Connect to the remote registry and get each of the stubs
        AOServConnector<?,?> newWrapped = factory.wrapped.newConnector(locale, connectAs, authenticateAs, password, daemonServer);

        // Now that each stub has been successfully received, store as the current connection
        this.wrapped = newWrapped;
        for(ServiceName serviceName : ServiceName.values) {
            ((WrappedService)getServices().get(serviceName)).wrapped = newWrapped.getServices().get(serviceName);
        }
    }

    /**
     * Disconnects this client.  The client will automatically reconnect on the next use.
     * TODO: Clear all caches on disconnect, how to signal outer cache layers?
     */
    final protected void disconnect() throws RemoteException {
        synchronized(connectionLock) {
            wrapped = null;
            for(AOServService<C,F,?,?> service : getServices().values()) {
                ((WrappedService<C,F,?,?>)service).wrapped = null;
            }
        }
    }

    /**
     * Gets the wrapped connector, reconnecting if needed.
     */
    final protected AOServConnector<?,?> getWrapped() throws RemoteException {
        synchronized(connectionLock) {
            if(wrapped==null) {
                try {
                    connect();
                } catch(RemoteException err) {
                    throw err;
                } catch(Exception err) {
                    throw new RemoteException(err.getMessage(), err);
                }
            }
            return wrapped;
        }
    }

    /**
     * All services call this to determine if the service is settable on objects returned by this connector.
     */
    abstract protected boolean isAoServObjectServiceSettable();

    /**
     * Performs the call on the wrapped object, allowing retry.
     */
    final protected <T> T call(Callable<T> callable) throws RemoteException, NoSuchElementException {
        return call(callable, true);
    }

    /**
     * Performs the call on the wrapped object.  This is the main hook to intercept requests
     * for features like auto-reconnects, timeouts, and retries.
     */
    protected <T> T call(Callable<T> callable, boolean allowRetry) throws RemoteException, NoSuchElementException {
        try {
            return callable.call();
        } catch(RemoteException err) {
            throw err;
        } catch(NoSuchElementException err) {
            throw err;
        } catch(Exception err) {
            throw new RuntimeException(err.getMessage(), err);
        }
    }

    final public F getFactory() {
        return factory;
    }

    final public Locale getLocale() {
        return locale;
    }

    final public void setLocale(final Locale locale) throws RemoteException {
        if(!this.locale.equals(locale)) {
            this.locale = locale;
            call(
                new Callable<Void>() {
                    public Void call() throws RemoteException {
                        getWrapped().setLocale(locale);
                        return null;
                    }
                }
            );
        }
    }

    final public UserId getConnectAs() {
        return connectAs;
    }

    final public BusinessAdministrator getThisBusinessAdministrator() throws RemoteException {
        return getBusinessAdministrators().get(connectAs);
    }

    final public UserId getAuthenticateAs() {
        return authenticateAs;
    }

    final public String getPassword() {
        return password;
    }

    final public <R> R executeCommand(final AOServCommand<R> command, final boolean isInteractive) throws RemoteException {
        return call(
            new Callable<R>() {
                public R call() throws RemoteException {
                    return getWrapped().executeCommand(command, isInteractive);
                }
            },
            command.isRetryable()
        );
    }

    private final AtomicReference<Map<ServiceName,AOServService<C,F,?,?>>> tables = new AtomicReference<Map<ServiceName,AOServService<C,F,?,?>>>();
    final public Map<ServiceName,AOServService<C,F,?,?>> getServices() throws RemoteException {
        Map<ServiceName,AOServService<C,F,?,?>> ts = tables.get();
        if(ts==null) {
            ts = AOServConnectorUtils.createServiceMap(this);
            if(!tables.compareAndSet(null, ts)) ts = tables.get();
        }
        return ts;
    }

    // <editor-fold defaultstate="collapsed" desc="AOServerDaemonHostService">
    static class WrappedAOServerDaemonHostService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,AOServerDaemonHost> implements AOServerDaemonHostService<C,F> {
        WrappedAOServerDaemonHostService(C connector) {
            super(connector, Integer.class, AOServerDaemonHost.class);
        }
    }
    final WrappedAOServerDaemonHostService<C,F> aoserverDaemonHosts;
    final public AOServerDaemonHostService<C,F> getAoServerDaemonHosts() {
        return aoserverDaemonHosts;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="AOServerResourceService">
    static class WrappedAOServerResourceService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,AOServerResource> implements AOServerResourceService<C,F> {
        WrappedAOServerResourceService(C connector) {
            super(connector, Integer.class, AOServerResource.class);
        }
    }
    final WrappedAOServerResourceService<C,F> aoserverResources;
    final public AOServerResourceService<C,F> getAoServerResources() {
        return aoserverResources;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="AOServerService">
    static class WrappedAOServerService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,AOServer> implements AOServerService<C,F> {
        WrappedAOServerService(C connector) {
            super(connector, Integer.class, AOServer.class);
        }
    }
    final WrappedAOServerService<C,F> aoservers;
    final public AOServerService<C,F> getAoServers() {
        return aoservers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="AOServPermissionService">
    static class WrappedAOServPermissionService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,AOServPermission> implements AOServPermissionService<C,F> {
        WrappedAOServPermissionService(C connector) {
            super(connector, String.class, AOServPermission.class);
        }
    }
    final WrappedAOServPermissionService<C,F> aoservPermissions;
    final public AOServPermissionService<C,F> getAoservPermissions() {
        return aoservPermissions;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ArchitectureService">
    static class WrappedArchitectureService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,Architecture> implements ArchitectureService<C,F> {
        WrappedArchitectureService(C connector) {
            super(connector, String.class, Architecture.class);
        }
    }
    final WrappedArchitectureService<C,F> architectures;
    final public ArchitectureService<C,F> getArchitectures() {
        return architectures;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BackupPartitionService">
    static class WrappedBackupPartitionService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,BackupPartition> implements BackupPartitionService<C,F> {
        WrappedBackupPartitionService(C connector) {
            super(connector, Integer.class, BackupPartition.class);
        }
    }
    final WrappedBackupPartitionService<C,F> backupPartitions;
    final public BackupPartitionService<C,F> getBackupPartitions() {
        return backupPartitions;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BackupRetentionService">
    static class WrappedBackupRetentionService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Short,BackupRetention> implements BackupRetentionService<C,F> {
        WrappedBackupRetentionService(C connector) {
            super(connector, Short.class, BackupRetention.class);
        }
    }
    final WrappedBackupRetentionService<C,F> backupRetentions;
    final public BackupRetentionService<C,F> getBackupRetentions() {
        return backupRetentions;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BankAccountService">
    // TODO: final WrappedBankAccountService<C,F> bankAccounts;
    // TODO: final public BankAccountService<C,F> getBankAccounts();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BankTransactionTypeService">
    static class WrappedBankTransactionTypeService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,BankTransactionType> implements BankTransactionTypeService<C,F> {
        WrappedBankTransactionTypeService(C connector) {
            super(connector, String.class, BankTransactionType.class);
        }
    }
    final WrappedBankTransactionTypeService<C,F> bankTransactionTypes;
    final public BankTransactionTypeService<C,F> getBankTransactionTypes() {
        return bankTransactionTypes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BankTransactionService">
    // TODO: final WrappedBankTransactionService<C,F> bankTransactions;
    // TODO: final public BankTransactionService<C,F> getBankTransactions();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BankService">
    // TODO: final WrappedBankService<C,F> banks;
    // TODO: final public BankService<C,F> getBanks();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BlackholeEmailAddressService">
    // TODO: final WrappedBlackholeEmailAddressService<C,F> blackholeEmailAddresss;
    // TODO: final public BlackholeEmailAddressService<C,F> getBlackholeEmailAddresses();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BrandService">
    static class WrappedBrandService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,AccountingCode,Brand> implements BrandService<C,F> {
        WrappedBrandService(C connector) {
            super(connector, AccountingCode.class, Brand.class);
        }
    }
    final WrappedBrandService<C,F> brands;
    final public BrandService<C,F> getBrands() {
        return brands;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BusinessAdministratorService">
    static class WrappedBusinessAdministratorService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,UserId,BusinessAdministrator> implements BusinessAdministratorService<C,F> {
        WrappedBusinessAdministratorService(C connector) {
            super(connector, UserId.class, BusinessAdministrator.class);
        }
    }
    final WrappedBusinessAdministratorService<C,F> businessAdministrators;
    final public BusinessAdministratorService<C,F> getBusinessAdministrators() {
        return businessAdministrators;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BusinessAdministratorPermissionService">
    // TODO: final WrappedBusinessAdministratorPermissionService<C,F> businessAdministratorPermissions;
    // TODO: final public BusinessAdministratorPermissionService<C,F> getBusinessAdministratorPermissions();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BusinessProfileService">
    // TODO: final WrappedBusinessProfileService<C,F> businessProfiles;
    // TODO: final public BusinessProfileService<C,F> getBusinessProfiles();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BusinessService">
    static class WrappedBusinessService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,AccountingCode,Business> implements BusinessService<C,F> {
        WrappedBusinessService(C connector) {
            super(connector, AccountingCode.class, Business.class);
        }
    }
    final WrappedBusinessService<C,F> businesses;
    final public BusinessService<C,F> getBusinesses() {
        return businesses;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BusinessServerService">
    static class WrappedBusinessServerService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,BusinessServer> implements BusinessServerService<C,F> {
        WrappedBusinessServerService(C connector) {
            super(connector, Integer.class, BusinessServer.class);
        }
    }
    final WrappedBusinessServerService<C,F> businessServers;
    final public BusinessServerService<C,F> getBusinessServers() {
        return businessServers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="CountryCodeService">
    static class WrappedCountryCodeService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,CountryCode> implements CountryCodeService<C,F> {
        WrappedCountryCodeService(C connector) {
            super(connector, String.class, CountryCode.class);
        }
    }
    final WrappedCountryCodeService<C,F> countryCodes;
    final public CountryCodeService<C,F> getCountryCodes() {
        return countryCodes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="CreditCardProcessorService">
    // TODO: final WrappedCreditCardProcessorService<C,F> creditCardProcessors;
    // TODO: final public CreditCardProcessorService<C,F> getCreditCardProcessors();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="CreditCardTransactionService">
    // TODO: final WrappedCreditCardTransactionService<C,F> creditCardTransactions;
    // TODO: final public CreditCardTransactionService<C,F> getCreditCardTransactions();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="CreditCardService">
    // TODO: final WrappedCreditCardService<C,F> creditCards;
    // TODO: final public CreditCardService<C,F> getCreditCards();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="CvsRepositoryService">
    static class WrappedCvsRepositoryService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,CvsRepository> implements CvsRepositoryService<C,F> {
        WrappedCvsRepositoryService(C connector) {
            super(connector, Integer.class, CvsRepository.class);
        }
    }
    final WrappedCvsRepositoryService<C,F> cvsRepositories;
    final public CvsRepositoryService<C,F> getCvsRepositories() {
        return cvsRepositories;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="DisableLogService">
    static class WrappedDisableLogService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,DisableLog> implements DisableLogService<C,F> {
        WrappedDisableLogService(C connector) {
            super(connector, Integer.class, DisableLog.class);
        }
    }
    final WrappedDisableLogService<C,F> disableLogs;
    final public DisableLogService<C,F> getDisableLogs() {
        return disableLogs;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="DistroFileTypeService">
    // TODO: final WrappedDistroFileTypeService<C,F> distroFileTypes;
    // TODO: final public DistroFileTypeService<C,F> getDistroFileTypes();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="DistroFileService">
    // TODO: final WrappedDistroFileService<C,F> distroFiles;
    // TODO: final public DistroFileService<C,F> getDistroFiles();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="DnsRecordService">
    static class WrappedDnsRecordService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,DnsRecord> implements DnsRecordService<C,F> {
        WrappedDnsRecordService(C connector) {
            super(connector, Integer.class, DnsRecord.class);
        }
    }
    final WrappedDnsRecordService<C,F> dnsRecords;
    final public DnsRecordService<C,F> getDnsRecords() {
        return dnsRecords;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="DnsTldService">
    static class WrappedDnsTldService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,DomainName,DnsTld> implements DnsTldService<C,F> {
        WrappedDnsTldService(C connector) {
            super(connector, DomainName.class, DnsTld.class);
        }
    }
    final WrappedDnsTldService<C,F> dnsTlds;
    final public DnsTldService<C,F> getDnsTlds() {
        return dnsTlds;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="DnsTypeService">
    static class WrappedDnsTypeService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,DnsType> implements DnsTypeService<C,F> {
        WrappedDnsTypeService(C connector) {
            super(connector, String.class, DnsType.class);
        }
    }
    final WrappedDnsTypeService<C,F> dnsTypes;
    final public DnsTypeService<C,F> getDnsTypes() {
        return dnsTypes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="DnsZoneService">
    static class WrappedDnsZoneService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,DnsZone> implements DnsZoneService<C,F> {
        WrappedDnsZoneService(C connector) {
            super(connector, Integer.class, DnsZone.class);
        }
    }
    final WrappedDnsZoneService<C,F> dnsZones;
    final public DnsZoneService<C,F> getDnsZones() {
        return dnsZones;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailAddressService">
    // TODO: final WrappedEmailAddressService<C,F> emailAddresss;
    // TODO: final public EmailAddressService<C,F> getEmailAddresses();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailAttachmentBlockService">
    // TODO: final WrappedEmailAttachmentBlockService<C,F> emailAttachmentBlocks;
    // TODO: final public EmailAttachmentBlockService<C,F> getEmailAttachmentBlocks();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailAttachmentTypeService">
    static class WrappedEmailAttachmentTypeService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,EmailAttachmentType> implements EmailAttachmentTypeService<C,F> {
        WrappedEmailAttachmentTypeService(C connector) {
            super(connector, String.class, EmailAttachmentType.class);
        }
    }
    final WrappedEmailAttachmentTypeService<C,F> emailAttachmentTypes;
    final public EmailAttachmentTypeService<C,F> getEmailAttachmentTypes() {
        return emailAttachmentTypes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailDomainService">
    // TODO: final WrappedEmailDomainService<C,F> emailDomains;
    // TODO: final public EmailDomainService<C,F> getEmailDomains();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailForwardingService">
    // TODO: final WrappedEmailForwardingService<C,F> emailForwardings;
    // TODO: final public EmailForwardingService<C,F> getEmailForwardings();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailInboxService">
    static class WrappedEmailInboxService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,EmailInbox> implements EmailInboxService<C,F> {
        WrappedEmailInboxService(C connector) {
            super(connector, Integer.class, EmailInbox.class);
        }
    }
    final WrappedEmailInboxService<C,F> emailInboxes;
    final public EmailInboxService<C,F> getEmailInboxes() {
        return emailInboxes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailListAddressService">
    // TODO: final WrappedEmailListAddressService<C,F> emailListAddresss;
    // TODO: final public EmailListAddressService<C,F> getEmailListAddresses();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailListService">
    // TODO: final WrappedEmailListService<C,F> emailLists;
    // TODO: final public EmailListService<C,F> getEmailLists();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailPipeAddressService">
    // TODO: final WrappedEmailPipeAddressService<C,F> emailPipeAddresss;
    // TODO: final public EmailPipeAddressService<C,F> getEmailPipeAddresses();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailPipeService">
    // TODO: final WrappedEmailPipeService<C,F> emailPipes;
    // TODO: final public EmailPipeService<C,F> getEmailPipes();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailSmtpRelayTypeService">
    static class WrappedEmailSmtpRelayTypeService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,EmailSmtpRelayType> implements EmailSmtpRelayTypeService<C,F> {
        WrappedEmailSmtpRelayTypeService(C connector) {
            super(connector, String.class, EmailSmtpRelayType.class);
        }
    }
    final WrappedEmailSmtpRelayTypeService<C,F> emailSmtpRelayTypes;
    final public EmailSmtpRelayTypeService<C,F> getEmailSmtpRelayTypes() {
        return emailSmtpRelayTypes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailSmtpRelayService">
    // TODO: final WrappedEmailSmtpRelayService<C,F> emailSmtpRelays;
    // TODO: final public EmailSmtpRelayService<C,F> getEmailSmtpRelays();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailSmtpSmartHostDomainService">
    // TODO: final WrappedEmailSmtpSmartHostDomainService<C,F> emailSmtpSmartHostDomains;
    // TODO: final public EmailSmtpSmartHostDomainService<C,F> getEmailSmtpSmartHostDomains();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailSmtpSmartHostService">
    // TODO: final WrappedEmailSmtpSmartHostService<C,F> emailSmtpSmartHosts;
    // TODO: final public EmailSmtpSmartHostService<C,F> getEmailSmtpSmartHosts();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailSpamAssassinIntegrationModeService">
    static class WrappedEmailSpamAssassinIntegrationModeService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,EmailSpamAssassinIntegrationMode> implements EmailSpamAssassinIntegrationModeService<C,F> {
        WrappedEmailSpamAssassinIntegrationModeService(C connector) {
            super(connector, String.class, EmailSpamAssassinIntegrationMode.class);
        }
    }
    final WrappedEmailSpamAssassinIntegrationModeService<C,F> emailSpamAssassinIntegrationModes;
    final public EmailSpamAssassinIntegrationModeService<C,F> getEmailSpamAssassinIntegrationModes() {
        return emailSpamAssassinIntegrationModes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EncryptionKeyService">
    // TODO: final WrappedEncryptionKeyService<C,F> encryptionKeys;
    // TODO: final public EncryptionKeyService<C,F> getEncryptionKeys();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ExpenseCategoryService">
    static class WrappedExpenseCategoryService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,ExpenseCategory> implements ExpenseCategoryService<C,F> {
        WrappedExpenseCategoryService(C connector) {
            super(connector, String.class, ExpenseCategory.class);
        }
    }
    final WrappedExpenseCategoryService<C,F> expenseCategories;
    final public ExpenseCategoryService<C,F> getExpenseCategories() {
        return expenseCategories;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="FailoverFileLogService">
    static class WrappedFailoverFileLogService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,FailoverFileLog> implements FailoverFileLogService<C,F> {
        WrappedFailoverFileLogService(C connector) {
            super(connector, Integer.class, FailoverFileLog.class);
        }
    }
    final WrappedFailoverFileLogService<C,F> failoverFileLogs;
    final public FailoverFileLogService<C,F> getFailoverFileLogs() {
        return failoverFileLogs;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="FailoverFileReplicationService">
    static class WrappedFailoverFileReplicationService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,FailoverFileReplication> implements FailoverFileReplicationService<C,F> {
        WrappedFailoverFileReplicationService(C connector) {
            super(connector, Integer.class, FailoverFileReplication.class);
        }
    }
    final WrappedFailoverFileReplicationService<C,F> failoverFileReplications;
    final public FailoverFileReplicationService<C,F> getFailoverFileReplications() {
        return failoverFileReplications;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="FailoverFileScheduleService">
    static class WrappedFailoverFileScheduleService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,FailoverFileSchedule> implements FailoverFileScheduleService<C,F> {
        WrappedFailoverFileScheduleService(C connector) {
            super(connector, Integer.class, FailoverFileSchedule.class);
        }
    }
    final WrappedFailoverFileScheduleService<C,F> failoverFileSchedules;
    final public FailoverFileScheduleService<C,F> getFailoverFileSchedules() {
        return failoverFileSchedules;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="FailoverMySQLReplicationService">
    static class WrappedFailoverMySQLReplicationService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,FailoverMySQLReplication> implements FailoverMySQLReplicationService<C,F> {
        WrappedFailoverMySQLReplicationService(C connector) {
            super(connector, Integer.class, FailoverMySQLReplication.class);
        }
    }
    final WrappedFailoverMySQLReplicationService<C,F> failoverMySQLReplications;
    final public FailoverMySQLReplicationService<C,F> getFailoverMySQLReplications() {
        return failoverMySQLReplications;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="FileBackupSettingService">
    static class WrappedFileBackupSettingService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,FileBackupSetting> implements FileBackupSettingService<C,F> {
        WrappedFileBackupSettingService(C connector) {
            super(connector, Integer.class, FileBackupSetting.class);
        }
    }
    final WrappedFileBackupSettingService<C,F> fileBackupSettings;
    final public FileBackupSettingService<C,F> getFileBackupSettings() {
        return fileBackupSettings;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="FtpGuestUserService">
    static class WrappedFtpGuestUserService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,FtpGuestUser> implements FtpGuestUserService<C,F> {
        WrappedFtpGuestUserService(C connector) {
            super(connector, Integer.class, FtpGuestUser.class);
        }
    }
    final WrappedFtpGuestUserService<C,F> ftpGuestUsers;
    final public FtpGuestUserService<C,F> getFtpGuestUsers() {
        return ftpGuestUsers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="GroupNameService">
    static class WrappedGroupNameService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,GroupId,GroupName> implements GroupNameService<C,F> {
        WrappedGroupNameService(C connector) {
            super(connector, GroupId.class, GroupName.class);
        }
    }
    final WrappedGroupNameService<C,F> groupNames;
    final public GroupNameService<C,F> getGroupNames() {
        return groupNames;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdBindService">
    // TODO: final WrappedHttpdBindService<C,F> httpdBinds;
    // TODO: final public HttpdBindService<C,F> getHttpdBinds();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdJBossSiteService">
    // TODO: final WrappedHttpdJBossSiteService<C,F> httpdJBossSites;
    // TODO: final public HttpdJBossSiteService<C,F> getHttpdJBossSites();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdJBossVersionService">
    static class WrappedHttpdJBossVersionService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,HttpdJBossVersion> implements HttpdJBossVersionService<C,F> {
        WrappedHttpdJBossVersionService(C connector) {
            super(connector, Integer.class, HttpdJBossVersion.class);
        }
    }
    final WrappedHttpdJBossVersionService<C,F> httpdJBossVersions;
    final public HttpdJBossVersionService<C,F> getHttpdJBossVersions() {
        return httpdJBossVersions;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdJKCodeService">
    static class WrappedHttpdJKCodeService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,HttpdJKCode> implements HttpdJKCodeService<C,F> {
        WrappedHttpdJKCodeService(C connector) {
            super(connector, String.class, HttpdJKCode.class);
        }
    }
    final WrappedHttpdJKCodeService<C,F> httpdJKCodes;
    final public HttpdJKCodeService<C,F> getHttpdJKCodes() {
        return httpdJKCodes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdJKProtocolService">
    static class WrappedHttpdJKProtocolService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,HttpdJKProtocol> implements HttpdJKProtocolService<C,F> {
        WrappedHttpdJKProtocolService(C connector) {
            super(connector, String.class, HttpdJKProtocol.class);
        }
    }
    final WrappedHttpdJKProtocolService<C,F> httpdJKProtocols;
    final public HttpdJKProtocolService<C,F> getHttpdJKProtocols() {
        return httpdJKProtocols;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdServerService">
    static class WrappedHttpdServerService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,HttpdServer> implements HttpdServerService<C,F> {
        WrappedHttpdServerService(C connector) {
            super(connector, Integer.class, HttpdServer.class);
        }
    }
    final WrappedHttpdServerService<C,F> httpdServers;
    final public HttpdServerService<C,F> getHttpdServers() {
        return httpdServers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdSharedTomcatService">
    // TODO: final WrappedHttpdSharedTomcatService<C,F> httpdSharedTomcats;
    // TODO: final public HttpdSharedTomcatService<C,F> getHttpdSharedTomcats();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdSiteAuthenticatedLocationService">
    // TODO: final WrappedHttpdSiteAuthenticatedLocationService<C,F> httpdSiteAuthenticatedLocations;
    // TODO: final public HttpdSiteAuthenticatedLocationService<C,F> getHttpdSiteAuthenticatedLocations();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdSiteBindService">
    // TODO: final WrappedHttpdSiteBindService<C,F> httpdSiteBinds;
    // TODO: final public HttpdSiteBindService<C,F> getHttpdSiteBinds();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdSiteURLService">
    // TODO: final WrappedHttpdSiteURLService<C,F> httpdSiteURLs;
    // TODO: final public HttpdSiteURLService<C,F> getHttpdSiteURLs();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdSiteService">
    static class WrappedHttpdSiteService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,HttpdSite> implements HttpdSiteService<C,F> {
        WrappedHttpdSiteService(C connector) {
            super(connector, Integer.class, HttpdSite.class);
        }
    }
    final WrappedHttpdSiteService<C,F> httpdSites;
    final public HttpdSiteService<C,F> getHttpdSites() {
        return httpdSites;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdStaticSiteService">
    // TODO: final WrappedHttpdStaticSiteService<C,F> httpdStaticSites;
    // TODO: final public HttpdStaticSiteService<C,F> getHttpdStaticSites();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdTomcatContextService">
    // TODO: final WrappedHttpdTomcatContextService<C,F> httpdTomcatContexts;
    // TODO: final public HttpdTomcatContextService<C,F> getHttpdTomcatContexts();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdTomcatDataSourceService">
    // TODO: final WrappedHttpdTomcatDataSourceService<C,F> httpdTomcatDataSources;
    // TODO: final public HttpdTomcatDataSourceService<C,F> getHttpdTomcatDataSources();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdTomcatParameterService">
    // TODO: final WrappedHttpdTomcatParameterService<C,F> httpdTomcatParameters;
    // TODO: final public HttpdTomcatParameterService<C,F> getHttpdTomcatParameters();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdTomcatSiteService">
    // TODO: final WrappedHttpdTomcatSiteService<C,F> httpdTomcatSites;
    // TODO: final public HttpdTomcatSiteService<C,F> getHttpdTomcatSites();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdTomcatSharedSiteService">
    // TODO: final WrappedHttpdTomcatSharedSiteService<C,F> httpdTomcatSharedSites;
    // TODO: final public HttpdTomcatSharedSiteService<C,F> getHttpdTomcatSharedSites();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdTomcatStdSiteService">
    // TODO: final WrappedHttpdTomcatStdSiteService<C,F> httpdTomcatStdSites;
    // TODO: final public HttpdTomcatStdSiteService<C,F> getHttpdTomcatStdSites();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdTomcatVersionService">
    static class WrappedHttpdTomcatVersionService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,HttpdTomcatVersion> implements HttpdTomcatVersionService<C,F> {
        WrappedHttpdTomcatVersionService(C connector) {
            super(connector, Integer.class, HttpdTomcatVersion.class);
        }
    }
    final WrappedHttpdTomcatVersionService<C,F> httpdTomcatVersions;
    final public HttpdTomcatVersionService<C,F> getHttpdTomcatVersions() {
        return httpdTomcatVersions;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdWorkerService">
    // TODO: final WrappedHttpdWorkerService<C,F> httpdWorkers;
    // TODO: final public HttpdWorkerService<C,F> getHttpdWorkers();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="IPAddressService">
    static class WrappedIPAddressService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,IPAddress> implements IPAddressService<C,F> {
        WrappedIPAddressService(C connector) {
            super(connector, Integer.class, IPAddress.class);
        }
    }
    final WrappedIPAddressService<C,F> ipAddresses;
    final public IPAddressService<C,F> getIpAddresses() {
        return ipAddresses;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="LanguageService">
    static class WrappedLanguageService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,Language> implements LanguageService<C,F> {
        WrappedLanguageService(C connector) {
            super(connector, String.class, Language.class);
        }
    }
    final WrappedLanguageService<C,F> languages;
    final public LanguageService<C,F> getLanguages() {
        return languages;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="LinuxAccAddressService">
    // TODO: final WrappedLinuxAccAddressService<C,F> linuxAccAddresss;
    // TODO: final public LinuxAccAddressService<C,F> getLinuxAccAddresses();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="LinuxAccountGroupService">
    static class WrappedLinuxAccountGroupService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,LinuxAccountGroup> implements LinuxAccountGroupService<C,F> {
        WrappedLinuxAccountGroupService(C connector) {
            super(connector, Integer.class, LinuxAccountGroup.class);
        }
    }
    final WrappedLinuxAccountGroupService<C,F> linuxAccountGroups;
    final public LinuxAccountGroupService<C,F> getLinuxAccountGroups() {
        return linuxAccountGroups;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="LinuxAccountTypeService">
    static class WrappedLinuxAccountTypeService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,LinuxAccountType> implements LinuxAccountTypeService<C,F> {
        WrappedLinuxAccountTypeService(C connector) {
            super(connector, String.class, LinuxAccountType.class);
        }
    }
    final WrappedLinuxAccountTypeService<C,F> linuxAccountTypes;
    final public LinuxAccountTypeService<C,F> getLinuxAccountTypes() {
        return linuxAccountTypes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="LinuxAccountService">
    static class WrappedLinuxAccountService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,LinuxAccount> implements LinuxAccountService<C,F> {
        WrappedLinuxAccountService(C connector) {
            super(connector, Integer.class, LinuxAccount.class);
        }
    }
    final WrappedLinuxAccountService<C,F> linuxAccounts;
    final public LinuxAccountService<C,F> getLinuxAccounts() {
        return linuxAccounts;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="LinuxGroupTypeService">
    static class WrappedLinuxGroupTypeService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,LinuxGroupType> implements LinuxGroupTypeService<C,F> {
        WrappedLinuxGroupTypeService(C connector) {
            super(connector, String.class, LinuxGroupType.class);
        }
    }
    final WrappedLinuxGroupTypeService<C,F> linuxGroupTypes;
    final public LinuxGroupTypeService<C,F> getLinuxGroupTypes() {
        return linuxGroupTypes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="LinuxGroupService">
    static class WrappedLinuxGroupService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,LinuxGroup> implements LinuxGroupService<C,F> {
        WrappedLinuxGroupService(C connector) {
            super(connector, Integer.class, LinuxGroup.class);
        }
    }
    final WrappedLinuxGroupService<C,F> linuxGroups;
    final public LinuxGroupService<C,F> getLinuxGroups() {
        return linuxGroups;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MajordomoListService">
    // TODO: final WrappedMajordomoListService<C,F> majordomoLists;
    // TODO: final public MajordomoListService<C,F> getMajordomoLists();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MajordomoServerService">
    // TODO: final WrappedMajordomoServerService<C,F> majordomoServers;
    // TODO: final public MajordomoServerService<C,F> getMajordomoServers();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MajordomoVersionService">
    static class WrappedMajordomoVersionService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,MajordomoVersion> implements MajordomoVersionService<C,F> {
        WrappedMajordomoVersionService(C connector) {
            super(connector, String.class, MajordomoVersion.class);
        }
    }
    final WrappedMajordomoVersionService<C,F> majordomoVersions;
    final public MajordomoVersionService<C,F> getMajordomoVersions() {
        return majordomoVersions;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MasterHostService">
    static class WrappedMasterHostService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,MasterHost> implements MasterHostService<C,F> {
        WrappedMasterHostService(C connector) {
            super(connector, Integer.class, MasterHost.class);
        }
    }
    final WrappedMasterHostService<C,F> masterHosts;
    final public MasterHostService<C,F> getMasterHosts() {
        return masterHosts;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MasterServerService">
    static class WrappedMasterServerService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,MasterServer> implements MasterServerService<C,F> {
        WrappedMasterServerService(C connector) {
            super(connector, Integer.class, MasterServer.class);
        }
    }
    final WrappedMasterServerService<C,F> masterServers;
    final public MasterServerService<C,F> getMasterServers() {
        return masterServers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MasterUserService">
    static class WrappedMasterUserService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,UserId,MasterUser> implements MasterUserService<C,F> {
        WrappedMasterUserService(C connector) {
            super(connector, UserId.class, MasterUser.class);
        }
    }
    final WrappedMasterUserService<C,F> masterUsers;
    final public MasterUserService<C,F> getMasterUsers() {
        return masterUsers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MonthlyChargeService">
    // TODO: final WrappedMonthlyChargeService<C,F> monthlyCharges;
    // TODO: final public MonthlyChargeService<C,F> getMonthlyCharges();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MySQLDatabaseService">
    static class WrappedMySQLDatabaseService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,MySQLDatabase> implements MySQLDatabaseService<C,F> {
        WrappedMySQLDatabaseService(C connector) {
            super(connector, Integer.class, MySQLDatabase.class);
        }
    }
    final WrappedMySQLDatabaseService<C,F> mysqlDatabases;
    final public MySQLDatabaseService<C,F> getMysqlDatabases() {
        return mysqlDatabases;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MySQLDBUserService">
    static class WrappedMySQLDBUserService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,MySQLDBUser> implements MySQLDBUserService<C,F> {
        WrappedMySQLDBUserService(C connector) {
            super(connector, Integer.class, MySQLDBUser.class);
        }
    }
    final WrappedMySQLDBUserService<C,F> mysqlDBUsers;
    final public MySQLDBUserService<C,F> getMysqlDBUsers() {
        return mysqlDBUsers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MySQLServerService">
    static class WrappedMySQLServerService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,MySQLServer> implements MySQLServerService<C,F> {
        WrappedMySQLServerService(C connector) {
            super(connector, Integer.class, MySQLServer.class);
        }
    }
    final WrappedMySQLServerService<C,F> mysqlServers;
    final public MySQLServerService<C,F> getMysqlServers() {
        return mysqlServers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MySQLUserService">
    static class WrappedMySQLUserService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,MySQLUser> implements MySQLUserService<C,F> {
        WrappedMySQLUserService(C connector) {
            super(connector, Integer.class, MySQLUser.class);
        }
    }
    final WrappedMySQLUserService<C,F> mysqlUsers;
    final public MySQLUserService<C,F> getMysqlUsers() {
        return mysqlUsers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="NetBindService">
    static class WrappedNetBindService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,NetBind> implements NetBindService<C,F> {
        WrappedNetBindService(C connector) {
            super(connector, Integer.class, NetBind.class);
        }
    }
    final WrappedNetBindService<C,F> netBinds;
    final public NetBindService<C,F> getNetBinds() {
        return netBinds;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="NetDeviceIDService">
    static class WrappedNetDeviceIDService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,NetDeviceID> implements NetDeviceIDService<C,F> {
        WrappedNetDeviceIDService(C connector) {
            super(connector, String.class, NetDeviceID.class);
        }
    }
    final WrappedNetDeviceIDService<C,F> netDeviceIDs;
    final public NetDeviceIDService<C,F> getNetDeviceIDs() {
        return netDeviceIDs;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="NetDeviceService">
    static class WrappedNetDeviceService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,NetDevice> implements NetDeviceService<C,F> {
        WrappedNetDeviceService(C connector) {
            super(connector, Integer.class, NetDevice.class);
        }
    }
    final WrappedNetDeviceService<C,F> netDevices;
    final public NetDeviceService<C,F> getNetDevices() {
        return netDevices;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="NetProtocolService">
    static class WrappedNetProtocolService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,NetProtocol> implements NetProtocolService<C,F> {
        WrappedNetProtocolService(C connector) {
            super(connector, String.class, NetProtocol.class);
        }
    }
    final WrappedNetProtocolService<C,F> netProtocols;
    final public NetProtocolService<C,F> getNetProtocols() {
        return netProtocols;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="NetTcpRedirectService">
    static class WrappedNetTcpRedirectService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,NetTcpRedirect> implements NetTcpRedirectService<C,F> {
        WrappedNetTcpRedirectService(C connector) {
            super(connector, Integer.class, NetTcpRedirect.class);
        }
    }
    final WrappedNetTcpRedirectService<C,F> netTcpRedirects;
    final public NetTcpRedirectService<C,F> getNetTcpRedirects() {
        return netTcpRedirects;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="NoticeLogService">
    // TODO: final WrappedNoticeLogService<C,F> noticeLogs;
    // TODO: final public NoticeLogService<C,F> getNoticeLogs();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="NoticeTypeService">
    static class WrappedNoticeTypeService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,NoticeType> implements NoticeTypeService<C,F> {
        WrappedNoticeTypeService(C connector) {
            super(connector, String.class, NoticeType.class);
        }
    }
    final WrappedNoticeTypeService<C,F> noticeTypes;
    final public NoticeTypeService<C,F> getNoticeTypes() {
        return noticeTypes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="OperatingSystemVersionService">
    static class WrappedOperatingSystemVersionService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,OperatingSystemVersion> implements OperatingSystemVersionService<C,F> {
        WrappedOperatingSystemVersionService(C connector) {
            super(connector, Integer.class, OperatingSystemVersion.class);
        }
    }
    final WrappedOperatingSystemVersionService<C,F> operatingSystemVersions;
    final public OperatingSystemVersionService<C,F> getOperatingSystemVersions() {
        return operatingSystemVersions;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="OperatingSystemService">
    static class WrappedOperatingSystemService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,OperatingSystem> implements OperatingSystemService<C,F> {
        WrappedOperatingSystemService(C connector) {
            super(connector, String.class, OperatingSystem.class);
        }
    }
    final WrappedOperatingSystemService<C,F> operatingSystems;
    final public OperatingSystemService<C,F> getOperatingSystems() {
        return operatingSystems;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PackageCategoryService">
    static class WrappedPackageCategoryService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,PackageCategory> implements PackageCategoryService<C,F> {
        WrappedPackageCategoryService(C connector) {
            super(connector, String.class, PackageCategory.class);
        }
    }
    final WrappedPackageCategoryService<C,F> packageCategories;
    final public PackageCategoryService<C,F> getPackageCategories() {
        return packageCategories;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PackageDefinitionLimitService">
    // TODO: final WrappedPackageDefinitionLimitService<C,F> packageDefinitionLimits;
    // TODO: final public PackageDefinitionLimitService<C,F> getPackageDefinitionLimits();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PackageDefinitionService">
    // TODO: final WrappedPackageDefinitionService<C,F> packageDefinitions;
    // TODO: final public PackageDefinitionService<C,F> getPackageDefinitions();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PaymentTypeService">
    static class WrappedPaymentTypeService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,PaymentType> implements PaymentTypeService<C,F> {
        WrappedPaymentTypeService(C connector) {
            super(connector, String.class, PaymentType.class);
        }
    }
    final WrappedPaymentTypeService<C,F> paymentTypes;
    final public PaymentTypeService<C,F> getPaymentTypes() {
        return paymentTypes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PhysicalServerService">
    // TODO: final WrappedPhysicalServerService<C,F> physicalServers;
    // TODO: final public PhysicalServerService<C,F> getPhysicalServers();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PostgresDatabaseService">
    static class WrappedPostgresDatabaseService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,PostgresDatabase> implements PostgresDatabaseService<C,F> {
        WrappedPostgresDatabaseService(C connector) {
            super(connector, Integer.class, PostgresDatabase.class);
        }
    }
    final WrappedPostgresDatabaseService<C,F> postgresDatabases;
    final public PostgresDatabaseService<C,F> getPostgresDatabases() {
        return postgresDatabases;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PostgresEncodingService">
    static class WrappedPostgresEncodingService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,PostgresEncoding> implements PostgresEncodingService<C,F> {
        WrappedPostgresEncodingService(C connector) {
            super(connector, Integer.class, PostgresEncoding.class);
        }
    }
    final WrappedPostgresEncodingService<C,F> postgresEncodings;
    final public PostgresEncodingService<C,F> getPostgresEncodings() {
        return postgresEncodings;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PostgresServerService">
    static class WrappedPostgresServerService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,PostgresServer> implements PostgresServerService<C,F> {
        WrappedPostgresServerService(C connector) {
            super(connector, Integer.class, PostgresServer.class);
        }
    }
    final WrappedPostgresServerService<C,F> postgresServers;
    final public PostgresServerService<C,F> getPostgresServers() {
        return postgresServers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PostgresUserService">
    static class WrappedPostgresUserService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,PostgresUser> implements PostgresUserService<C,F> {
        WrappedPostgresUserService(C connector) {
            super(connector, Integer.class, PostgresUser.class);
        }
    }
    final WrappedPostgresUserService<C,F> postgresUsers;
    final public PostgresUserService<C,F> getPostgresUsers() {
        return postgresUsers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PostgresVersionService">
    static class WrappedPostgresVersionService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,PostgresVersion> implements PostgresVersionService<C,F> {
        WrappedPostgresVersionService(C connector) {
            super(connector, Integer.class, PostgresVersion.class);
        }
    }
    final WrappedPostgresVersionService<C,F> postgresVersions;
    final public PostgresVersionService<C,F> getPostgresVersions() {
        return postgresVersions;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PrivateFtpServerService">
    static class WrappedPrivateFtpServerService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,PrivateFtpServer> implements PrivateFtpServerService<C,F> {
        WrappedPrivateFtpServerService(C connector) {
            super(connector, Integer.class, PrivateFtpServer.class);
        }
    }
    final WrappedPrivateFtpServerService<C,F> privateFtpServers;
    final public PrivateFtpServerService<C,F> getPrivateFtpServers() {
        return privateFtpServers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ProcessorTypeService">
    static class WrappedProcessorTypeService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,ProcessorType> implements ProcessorTypeService<C,F> {
        WrappedProcessorTypeService(C connector) {
            super(connector, String.class, ProcessorType.class);
        }
    }
    final WrappedProcessorTypeService<C,F> processorTypes;
    final public ProcessorTypeService<C,F> getProcessorTypes() {
        return processorTypes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ProtocolService">
    static class WrappedProtocolService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,Protocol> implements ProtocolService<C,F> {
        WrappedProtocolService(C connector) {
            super(connector, String.class, Protocol.class);
        }
    }
    final WrappedProtocolService<C,F> protocols;
    final public ProtocolService<C,F> getProtocols() {
        return protocols;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="RackService">
    // TODO: final WrappedRackService<C,F> racks;
    // TODO: final public RackService<C,F> getRacks();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ResellerService">
    static class WrappedResellerService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,AccountingCode,Reseller> implements ResellerService<C,F> {
        WrappedResellerService(C connector) {
            super(connector, AccountingCode.class, Reseller.class);
        }
    }
    final WrappedResellerService<C,F> resellers;
    final public ResellerService<C,F> getResellers() {
        return resellers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ResourceTypeService">
    static class WrappedResourceTypeService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,ResourceType> implements ResourceTypeService<C,F> {
        WrappedResourceTypeService(C connector) {
            super(connector, String.class, ResourceType.class);
        }
    }
    final WrappedResourceTypeService<C,F> resourceTypes;
    final public ResourceTypeService<C,F> getResourceTypes() {
        return resourceTypes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ResourceService">
    static class WrappedResourceService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,Resource> implements ResourceService<C,F> {
        WrappedResourceService(C connector) {
            super(connector, Integer.class, Resource.class);
        }
    }
    final WrappedResourceService<C,F> resources;
    final public ResourceService<C,F> getResources() {
        return resources;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ServerFarmService">
    static class WrappedServerFarmService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,DomainLabel,ServerFarm> implements ServerFarmService<C,F> {
        WrappedServerFarmService(C connector) {
            super(connector, DomainLabel.class, ServerFarm.class);
        }
    }
    final WrappedServerFarmService<C,F> serverFarms;
    final public ServerFarmService<C,F> getServerFarms() {
        return serverFarms;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ServerResourceService">
    static class WrappedServerResourceService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,ServerResource> implements ServerResourceService<C,F> {
        WrappedServerResourceService(C connector) {
            super(connector, Integer.class, ServerResource.class);
        }
    }
    final WrappedServerResourceService<C,F> serverResources;
    final public ServerResourceService<C,F> getServerResources() {
        return serverResources;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ServerService">
    static class WrappedServerService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,Server> implements ServerService<C,F> {
        WrappedServerService(C connector) {
            super(connector, Integer.class, Server.class);
        }
    }
    final WrappedServerService<C,F> servers;
    final public ServerService<C,F> getServers() {
        return servers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ShellService">
    static class WrappedShellService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,UnixPath,Shell> implements ShellService<C,F> {
        WrappedShellService(C connector) {
            super(connector, UnixPath.class, Shell.class);
        }
    }
    final WrappedShellService<C,F> shells;
    final public ShellService<C,F> getShells() {
        return shells;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="SignupRequestOptionService">
    // TODO: final WrappedSignupRequestOptionService<C,F> signupRequestOptions;
    // TODO: final public SignupRequestOptionService<C,F> getSignupRequestOptions();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="SignupRequestService">
    // TODO: final WrappedSignupRequestService<C,F> signupRequests;
    // TODO: final public SignupRequestService<C,F> getSignupRequests();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="SpamEmailMessageService">
    // TODO: final WrappedSpamEmailMessageService<C,F> spamEmailMessages;
    // TODO: final public SpamEmailMessageService<C,F> getSpamEmailMessages();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="SystemEmailAliasService">
    // TODO: final WrappedSystemEmailAliasService<C,F> systemEmailAliass;
    // TODO: final public SystemEmailAliasService<C,F> getSystemEmailAliases();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TechnologyService">
    final class WrappedTechnologyService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,Technology> implements TechnologyService<C,F> {
        WrappedTechnologyService(C connector) {
            super(connector, Integer.class, Technology.class);
        }
    }
    final WrappedTechnologyService<C,F> technologies;
    final public TechnologyService<C,F> getTechnologies() {
        return technologies;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TechnologyClassService">
    static class WrappedTechnologyClassService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,TechnologyClass> implements TechnologyClassService<C,F> {
        WrappedTechnologyClassService(C connector) {
            super(connector, String.class, TechnologyClass.class);
        }
    }
    final WrappedTechnologyClassService<C,F> technologyClasses;
    final public TechnologyClassService<C,F> getTechnologyClasses() {
        return technologyClasses;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TechnologyNameService">
    static class WrappedTechnologyNameService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,TechnologyName> implements TechnologyNameService<C,F> {
        WrappedTechnologyNameService(C connector) {
            super(connector, String.class, TechnologyName.class);
        }
    }
    final WrappedTechnologyNameService<C,F> technologyNames;
    final public TechnologyNameService<C,F> getTechnologyNames() {
        return technologyNames;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TechnologyVersionService">
    static class WrappedTechnologyVersionService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,TechnologyVersion> implements TechnologyVersionService<C,F> {
        WrappedTechnologyVersionService(C connector) {
            super(connector, Integer.class, TechnologyVersion.class);
        }
    }
    final WrappedTechnologyVersionService<C,F> technologyVersions;
    final public TechnologyVersionService<C,F> getTechnologyVersions() {
        return technologyVersions;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TicketActionTypeService">
    static class WrappedTicketActionTypeService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,TicketActionType> implements TicketActionTypeService<C,F> {
        WrappedTicketActionTypeService(C connector) {
            super(connector, String.class, TicketActionType.class);
        }
    }
    final WrappedTicketActionTypeService<C,F> ticketActionTypes;
    final public TicketActionTypeService<C,F> getTicketActionTypes() {
        return ticketActionTypes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TicketActionService">
    // TODO: final WrappedTicketActionService<C,F> ticketActions;
    // TODO: final public TicketActionService<C,F> getTicketActions();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TicketAssignmentService">
    static class WrappedTicketAssignmentService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,TicketAssignment> implements TicketAssignmentService<C,F> {
        WrappedTicketAssignmentService(C connector) {
            super(connector, Integer.class, TicketAssignment.class);
        }
    }
    final WrappedTicketAssignmentService<C,F> ticketAssignments;
    final public TicketAssignmentService<C,F> getTicketAssignments() {
        return ticketAssignments;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TicketBrandCategoryService">
    // TODO: final WrappedTicketBrandCategoryService<C,F> ticketBrandCategories;
    // TODO: final public TicketBrandCategoryService<C,F> getTicketBrandCategories();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TicketCategoryService">
    static class WrappedTicketCategoryService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,TicketCategory> implements TicketCategoryService<C,F> {
        WrappedTicketCategoryService(C connector) {
            super(connector, Integer.class, TicketCategory.class);
        }
    }
    final WrappedTicketCategoryService<C,F> ticketCategories;
    final public TicketCategoryService<C,F> getTicketCategories() {
        return ticketCategories;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TicketPriorityService">
    static class WrappedTicketPriorityService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,TicketPriority> implements TicketPriorityService<C,F> {
        WrappedTicketPriorityService(C connector) {
            super(connector, String.class, TicketPriority.class);
        }
    }
    final WrappedTicketPriorityService<C,F> ticketPriorities;
    final public TicketPriorityService<C,F> getTicketPriorities() {
        return ticketPriorities;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TicketStatusService">
    static class WrappedTicketStatusService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,TicketStatus> implements TicketStatusService<C,F> {
        WrappedTicketStatusService(C connector) {
            super(connector, String.class, TicketStatus.class);
        }
    }
    final WrappedTicketStatusService<C,F> ticketStatuses;
    final public TicketStatusService<C,F> getTicketStatuses() {
        return ticketStatuses;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TicketTypeService">
    static class WrappedTicketTypeService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,TicketType> implements TicketTypeService<C,F> {
        WrappedTicketTypeService(C connector) {
            super(connector, String.class, TicketType.class);
        }
    }
    final WrappedTicketTypeService<C,F> ticketTypes;
    final public TicketTypeService<C,F> getTicketTypes() {
        return ticketTypes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TicketService">
    static class WrappedTicketService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,Ticket> implements TicketService<C,F> {
        WrappedTicketService(C connector) {
            super(connector, Integer.class, Ticket.class);
        }
    }
    final WrappedTicketService<C,F> tickets;
    final public TicketService<C,F> getTickets() {
        return tickets;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TimeZoneService">
    static class WrappedTimeZoneService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,TimeZone> implements TimeZoneService<C,F> {
        WrappedTimeZoneService(C connector) {
            super(connector, String.class, TimeZone.class);
        }
    }
    final WrappedTimeZoneService<C,F> timeZones;
    final public TimeZoneService<C,F> getTimeZones() {
        return timeZones;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TransactionTypeService">
    static class WrappedTransactionTypeService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,TransactionType> implements TransactionTypeService<C,F> {
        WrappedTransactionTypeService(C connector) {
            super(connector, String.class, TransactionType.class);
        }
    }
    final WrappedTransactionTypeService<C,F> transactionTypes;
    final public TransactionTypeService<C,F> getTransactionTypes() {
        return transactionTypes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TransactionService">
    // TODO: final WrappedTransactionService<C,F> transactions;
    // TODO: final public TransactionService<C,F> getTransactions();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="UsernameService">
    static class WrappedUsernameService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,UserId,Username> implements UsernameService<C,F> {
        WrappedUsernameService(C connector) {
            super(connector, UserId.class, Username.class);
        }
    }
    final WrappedUsernameService<C,F> usernames;
    final public UsernameService<C,F> getUsernames() {
        return usernames;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="VirtualDiskService">
    // TODO: final WrappedVirtualDiskService<C,F> virtualDisks;
    // TODO: final public VirtualDiskService<C,F> getVirtualDisks();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="VirtualServerService">
    // TODO: final WrappedVirtualServerService<C,F> virtualServers;
    // TODO: final public VirtualServerService<C,F> getVirtualServers();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="WhoisHistoryService">
    // TODO: final WrappedWhoisHistoryService<C,F> whoisHistories;
    // TODO: final public WhoisHistoryService<C,F> getWhoisHistory();
    // </editor-fold>
}
