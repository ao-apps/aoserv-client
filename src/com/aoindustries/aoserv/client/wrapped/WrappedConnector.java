/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.wrapped;

import com.aoindustries.aoserv.client.*;
import com.aoindustries.aoserv.client.command.*;
import com.aoindustries.aoserv.client.validator.*;
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

    protected WrappedConnector(F factory, Locale locale, UserId connectAs, UserId authenticateAs, String password, DomainName daemonServer) throws RemoteException, LoginException {
        this.factory = factory;
        this.locale = locale;
        this.connectAs = connectAs;
        this.authenticateAs = authenticateAs;
        this.password = password;
        this.daemonServer = daemonServer;
        aoserverDaemonHosts = new WrappedAOServerDaemonHostService<C,F>(this);
        aoserverResources = new AOServerResourceService<C,F>(this);
        aoservers = new WrappedAOServerService<C,F>(this);
        aoservPermissions = new WrappedAOServPermissionService<C,F>(this);
        aoservRoles = new WrappedAOServRoleService<C,F>(this);
        aoservRolePermissions = new WrappedAOServRolePermissionService<C,F>(this);
        architectures = new WrappedArchitectureService<C,F>(this);
        backupPartitions = new WrappedBackupPartitionService<C,F>(this);
        backupRetentions = new WrappedBackupRetentionService<C,F>(this);
        // TODO: bankAccounts = new WrappedBankAccountService<C,F>(this);
        bankTransactionTypes = new WrappedBankTransactionTypeService<C,F>(this);
        // TODO: bankTransactions = new WrappedBankTransactionService<C,F>(this);
        // TODO: banks = new WrappedBankService<C,F>(this);
        brands = new WrappedBrandService<C,F>(this);
        businessAdministrators = new WrappedBusinessAdministratorService<C,F>(this);
        businessAdministratorRoles = new WrappedBusinessAdministratorRoleService<C,F>(this);
        businessProfiles = new WrappedBusinessProfileService<C,F>(this);
        businesses = new WrappedBusinessService<C,F>(this);
        businessServers = new WrappedBusinessServerService<C,F>(this);
        countryCodes = new WrappedCountryCodeService<C,F>(this);
        creditCardProcessors = new WrappedCreditCardProcessorService<C,F>(this);
        creditCardTransactions = new WrappedCreditCardTransactionService<C,F>(this);
        creditCards = new WrappedCreditCardService<C,F>(this);
        cvsRepositories = new WrappedCvsRepositoryService<C,F>(this);
        disableLogs = new WrappedDisableLogService<C,F>(this);
        /* TODO
        distroFileTypes = new WrappedDistroFileTypeService<C,F>(this);
        distroFiles = new WrappedDistroFileService<C,F>(this);
         */
        dnsRecords = new WrappedDnsRecordService<C,F>(this);
        dnsTlds = new WrappedDnsTldService<C,F>(this);
        dnsTypes = new WrappedDnsTypeService<C,F>(this);
        dnsZones = new WrappedDnsZoneService<C,F>(this);
        // TODO: emailAddresss = new WrappedEmailAddressService<C,F>(this);
        // TODO: emailAttachmentBlocks = new WrappedEmailAttachmentBlockService<C,F>(this);
        emailAttachmentTypes = new WrappedEmailAttachmentTypeService<C,F>(this);
        // TODO: emailDomains = new WrappedEmailDomainService<C,F>(this);
        // TODO: emailForwardings = new WrappedEmailForwardingService<C,F>(this);
        emailInboxes = new WrappedEmailInboxService<C,F>(this);
        // TODO: emailListAddresss = new WrappedEmailListAddressService<C,F>(this);
        // TODO: emailLists = new WrappedEmailListService<C,F>(this);
        // TODO: emailPipeAddresss = new WrappedEmailPipeAddressService<C,F>(this);
        // TODO: emailPipes = new WrappedEmailPipeService<C,F>(this);
        emailSmtpRelayTypes = new WrappedEmailSmtpRelayTypeService<C,F>(this);
        // TODO: emailSmtpRelays = new WrappedEmailSmtpRelayService<C,F>(this);
        // TODO: emailSmtpSmartHostDomains = new WrappedEmailSmtpSmartHostDomainService<C,F>(this);
        // TODO: emailSmtpSmartHosts = new WrappedEmailSmtpSmartHostService<C,F>(this);
        emailSpamAssassinIntegrationModes = new WrappedEmailSpamAssassinIntegrationModeService<C,F>(this);
        // TODO: encryptionKeys = new WrappedEncryptionKeyService<C,F>(this);
        expenseCategories = new WrappedExpenseCategoryService<C,F>(this);
        failoverFileLogs = new WrappedFailoverFileLogService<C,F>(this);
        failoverFileReplications = new WrappedFailoverFileReplicationService<C,F>(this);
        failoverFileSchedules = new WrappedFailoverFileScheduleService<C,F>(this);
        failoverMySQLReplications = new WrappedFailoverMySQLReplicationService<C,F>(this);
        fileBackupSettings = new WrappedFileBackupSettingService<C,F>(this);
        groupNames = new WrappedGroupNameService<C,F>(this);
        ftpGuestUsers = new WrappedFtpGuestUserService<C,F>(this);
        /* TODO
        httpdBinds = new WrappedHttpdBindService<C,F>(this);
        httpdJBossSites = new WrappedHttpdJBossSiteService<C,F>(this);
         */
        httpdJBossVersions = new WrappedHttpdJBossVersionService<C,F>(this);
        httpdJKCodes = new WrappedHttpdJKCodeService<C,F>(this);
        httpdJKProtocols = new WrappedHttpdJKProtocolService<C,F>(this);
        httpdServers = new WrappedHttpdServerService<C,F>(this);
        /* TODO
        httpdSharedTomcats = new WrappedHttpdSharedTomcatService<C,F>(this);
        httpdSiteAuthenticatedLocations = new WrappedHttpdSiteAuthenticatedLocationService<C,F>(this);
        httpdSiteBinds = new WrappedHttpdSiteBindService<C,F>(this);
        httpdSiteURLs = new WrappedHttpdSiteURLService<C,F>(this);
         */
        httpdSites = new WrappedHttpdSiteService<C,F>(this);
        // TODO: httpdStaticSites = new WrappedHttpdStaticSiteService<C,F>(this);
        // TODO: httpdTomcatContexts = new WrappedHttpdTomcatContextService<C,F>(this);
        // TODO: httpdTomcatDataSources = new WrappedHttpdTomcatDataSourceService<C,F>(this);
        // TODO: httpdTomcatParameters = new WrappedHttpdTomcatParameterService<C,F>(this);
        // TODO: httpdTomcatSites = new WrappedHttpdTomcatSiteService<C,F>(this);
        // TODO: httpdTomcatSharedSites = new WrappedHttpdTomcatSharedSiteService<C,F>(this);
        // TODO: httpdTomcatStdSites = new WrappedHttpdTomcatStdSiteService<C,F>(this);
        httpdTomcatVersions = new WrappedHttpdTomcatVersionService<C,F>(this);
        // TODO: httpdWorkers = new WrappedHttpdWorkerService<C,F>(this);
        ipAddresses = new WrappedIPAddressService<C,F>(this);
        languages = new WrappedLanguageService<C,F>(this);
        /* TODO
        linuxAccAddresss = new WrappedLinuxAccAddressService<C,F>(this);
         */
        linuxAccountGroups = new WrappedLinuxAccountGroupService<C,F>(this);
        linuxAccountTypes = new WrappedLinuxAccountTypeService<C,F>(this);
        linuxAccounts = new WrappedLinuxAccountService<C,F>(this);
        linuxGroupTypes = new WrappedLinuxGroupTypeService<C,F>(this);
        linuxGroups = new WrappedLinuxGroupService<C,F>(this);
        /* TODO
        majordomoLists = new WrappedMajordomoListService<C,F>(this);
        majordomoServers = new WrappedMajordomoServerService<C,F>(this);
         */
        majordomoVersions = new WrappedMajordomoVersionService<C,F>(this);
        masterHosts = new WrappedMasterHostService<C,F>(this);
        masterServers = new WrappedMasterServerService<C,F>(this);
        masterUsers = new WrappedMasterUserService<C,F>(this);
        // TODO: monthlyCharges = new WrappedMonthlyChargeService<C,F>(this);
        mysqlDatabases = new WrappedMySQLDatabaseService<C,F>(this);
        mysqlDBUsers = new WrappedMySQLDBUserService<C,F>(this);
        mysqlServers = new WrappedMySQLServerService<C,F>(this);
        mysqlUsers = new WrappedMySQLUserService<C,F>(this);
        netBinds = new WrappedNetBindService<C,F>(this);
        netDeviceIDs = new WrappedNetDeviceIDService<C,F>(this);
        netDevices = new WrappedNetDeviceService<C,F>(this);
        netProtocols = new WrappedNetProtocolService<C,F>(this);
        netTcpRedirects = new WrappedNetTcpRedirectService<C,F>(this);
        // TODO: noticeLogs = new WrappedNoticeLogService<C,F>(this);
        noticeTypes = new WrappedNoticeTypeService<C,F>(this);
        operatingSystemVersions = new WrappedOperatingSystemVersionService<C,F>(this);
        operatingSystems = new WrappedOperatingSystemService<C,F>(this);
        packageCategories = new WrappedPackageCategoryService<C,F>(this);
        packageDefinitionBusinesses = new WrappedPackageDefinitionBusinessService<C,F>(this);
        packageDefinitionLimits = new WrappedPackageDefinitionLimitService<C,F>(this);
        packageDefinitions = new WrappedPackageDefinitionService<C,F>(this);
        paymentTypes = new WrappedPaymentTypeService<C,F>(this);
        // TODO: physicalServers = new WrappedPhysicalServerService<C,F>(this);
        postgresDatabases = new WrappedPostgresDatabaseService<C,F>(this);
        postgresEncodings = new WrappedPostgresEncodingService<C,F>(this);
        postgresServers = new WrappedPostgresServerService<C,F>(this);
        postgresUsers = new WrappedPostgresUserService<C,F>(this);
        postgresVersions = new WrappedPostgresVersionService<C,F>(this);
        privateFtpServers = new WrappedPrivateFtpServerService<C,F>(this);
        processorTypes = new WrappedProcessorTypeService<C,F>(this);
        protocols = new WrappedProtocolService<C,F>(this);
        // TODO: racks = new WrappedRackService<C,F>(this);
        resellers = new WrappedResellerService<C,F>(this);
        resourceTypes = new WrappedResourceTypeService<C,F>(this);
        resources = new ResourceService<C,F>(this);
        serverFarms = new WrappedServerFarmService<C,F>(this);
        serverResources = new ServerResourceService<C,F>(this);
        servers = new WrappedServerService<C,F>(this);
        shells = new WrappedShellService<C,F>(this);
        /* TODO
        signupRequestOptions = new WrappedSignupRequestOptionService<C,F>(this);
        signupRequests = new WrappedSignupRequestService<C,F>(this);
        spamEmailMessages = new WrappedSpamEmailMessageService<C,F>(this);
        systemEmailAliass = new WrappedSystemEmailAliasService<C,F>(this);
         */
        technologies = new WrappedTechnologyService<C,F>(this);
        technologyClasses = new WrappedTechnologyClassService<C,F>(this);
        technologyNames = new WrappedTechnologyNameService<C,F>(this);
        technologyVersions = new WrappedTechnologyVersionService<C,F>(this);
        ticketActionTypes = new WrappedTicketActionTypeService<C,F>(this);
        ticketActions = new WrappedTicketActionService<C,F>(this);
        ticketAssignments = new WrappedTicketAssignmentService<C,F>(this);
        // TODO: ticketBrandCategories = new WrappedTicketBrandCategoryService<C,F>(this);
        ticketCategories = new WrappedTicketCategoryService<C,F>(this);
        ticketPriorities = new WrappedTicketPriorityService<C,F>(this);
        ticketStatuses = new WrappedTicketStatusService<C,F>(this);
        ticketTypes = new WrappedTicketTypeService<C,F>(this);
        tickets = new WrappedTicketService<C,F>(this);
        timeZones = new WrappedTimeZoneService<C,F>(this);
        transactionTypes = new WrappedTransactionTypeService<C,F>(this);
        transactions = new WrappedTransactionService<C,F>(this);
        // TODO: usStates = new WrappedUSStateService<C,F>(this);
        usernames = new WrappedUsernameService<C,F>(this);
        // TODO: virtualDisks = new WrappedVirtualDiskService<C,F>(this);
        virtualServers = new WrappedVirtualServerService<C,F>(this);
        // TODO: whoisHistories = new WrappedWhoisHistoryService<C,F>(this);
        // Connect immediately in order to have the chance to throw exceptions that will occur during connection
        getWrapped();
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
    final protected AOServConnector<?,?> getWrapped() throws RemoteException, LoginException {
        synchronized(connectionLock) {
            // (Re)connects to the wrapped factory
            if(wrapped==null) wrapped = factory.wrapped.newConnector(locale, connectAs, authenticateAs, password, daemonServer);
            return wrapped;
        }
    }

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

    @Override
    final public F getFactory() {
        return factory;
    }

    /**
     * Defaults to <code>true</code>.
     */
    @Override
    public boolean isAoServObjectConnectorSettable() throws RemoteException {
        return true;
    }

    @Override
    final public Locale getLocale() {
        return locale;
    }

    @Override
    final public void setLocale(final Locale locale) throws RemoteException {
        if(!this.locale.equals(locale)) {
            this.locale = locale;
            call(
                new Callable<Void>() {
                    @Override
                    public Void call() throws RemoteException {
                        try {
                            getWrapped().setLocale(locale);
                            return null;
                        } catch(LoginException err) {
                            throw new RemoteException(err.getMessage(), err);
                        }
                    }
                }
            );
        }
    }

    @Override
    final public UserId getConnectAs() {
        return connectAs;
    }

    @Override
    final public BusinessAdministrator getThisBusinessAdministrator() throws RemoteException {
        return getBusinessAdministrators().get(connectAs);
    }

    @Override
    final public UserId getAuthenticateAs() {
        return authenticateAs;
    }

    @Override
    final public String getPassword() {
        return password;
    }

    @Override
    final public <R> CommandResult<R> executeCommand(final RemoteCommand<R> command, final boolean isInteractive) throws RemoteException {
        return call(
            new Callable<CommandResult<R>>() {
                @Override
                public CommandResult<R> call() throws RemoteException {
                    try {
                        return getWrapped().executeCommand(command, isInteractive);
                    } catch(LoginException err) {
                        throw new RemoteException(err.getMessage(), err);
                    }
                }
            },
            command.isIdempotent()
        );
    }

    private final AtomicReference<Map<ServiceName,AOServService<C,F,?,?>>> tables = new AtomicReference<Map<ServiceName,AOServService<C,F,?,?>>>();
    @Override
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
        WrappedAOServerDaemonHostService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, AOServerDaemonHost.class);
        }
    }
    final WrappedAOServerDaemonHostService<C,F> aoserverDaemonHosts;
    @Override
    final public AOServerDaemonHostService<C,F> getAoServerDaemonHosts() {
        return aoserverDaemonHosts;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="AOServerResourceService">
    final AOServerResourceService<C,F> aoserverResources;
    @Override
    final public AOServerResourceService<C,F> getAoServerResources() {
        return aoserverResources;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="AOServerService">
    static class WrappedAOServerService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,AOServer> implements AOServerService<C,F> {
        WrappedAOServerService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, AOServer.class);
        }
    }
    final WrappedAOServerService<C,F> aoservers;
    @Override
    final public AOServerService<C,F> getAoServers() {
        return aoservers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="AOServPermissionService">
    static class WrappedAOServPermissionService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,AOServPermission> implements AOServPermissionService<C,F> {
        WrappedAOServPermissionService(WrappedConnector<C,F> connector) {
            super(connector, String.class, AOServPermission.class);
        }
    }
    final WrappedAOServPermissionService<C,F> aoservPermissions;
    @Override
    final public AOServPermissionService<C,F> getAoservPermissions() {
        return aoservPermissions;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="AOServRoleService">
    static class WrappedAOServRoleService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,AOServRole> implements AOServRoleService<C,F> {
        WrappedAOServRoleService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, AOServRole.class);
        }
    }
    final WrappedAOServRoleService<C,F> aoservRoles;
    @Override
    final public AOServRoleService<C,F> getAoservRoles() {
        return aoservRoles;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="AOServRolePermissionService">
    static class WrappedAOServRolePermissionService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,AOServRolePermission> implements AOServRolePermissionService<C,F> {
        WrappedAOServRolePermissionService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, AOServRolePermission.class);
        }
    }
    final WrappedAOServRolePermissionService<C,F> aoservRolePermissions;
    @Override
    final public AOServRolePermissionService<C,F> getAoservRolePermissions() {
        return aoservRolePermissions;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ArchitectureService">
    static class WrappedArchitectureService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,Architecture> implements ArchitectureService<C,F> {
        WrappedArchitectureService(WrappedConnector<C,F> connector) {
            super(connector, String.class, Architecture.class);
        }
    }
    final WrappedArchitectureService<C,F> architectures;
    @Override
    final public ArchitectureService<C,F> getArchitectures() {
        return architectures;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BackupPartitionService">
    static class WrappedBackupPartitionService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,BackupPartition> implements BackupPartitionService<C,F> {
        WrappedBackupPartitionService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, BackupPartition.class);
        }
    }
    final WrappedBackupPartitionService<C,F> backupPartitions;
    @Override
    final public BackupPartitionService<C,F> getBackupPartitions() {
        return backupPartitions;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BackupRetentionService">
    static class WrappedBackupRetentionService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Short,BackupRetention> implements BackupRetentionService<C,F> {
        WrappedBackupRetentionService(WrappedConnector<C,F> connector) {
            super(connector, Short.class, BackupRetention.class);
        }
    }
    final WrappedBackupRetentionService<C,F> backupRetentions;
    @Override
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
        WrappedBankTransactionTypeService(WrappedConnector<C,F> connector) {
            super(connector, String.class, BankTransactionType.class);
        }
    }
    final WrappedBankTransactionTypeService<C,F> bankTransactionTypes;
    @Override
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
    // <editor-fold defaultstate="collapsed" desc="BrandService">
    static class WrappedBrandService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,AccountingCode,Brand> implements BrandService<C,F> {
        WrappedBrandService(WrappedConnector<C,F> connector) {
            super(connector, AccountingCode.class, Brand.class);
        }
    }
    final WrappedBrandService<C,F> brands;
    @Override
    final public BrandService<C,F> getBrands() {
        return brands;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BusinessAdministratorService">
    static class WrappedBusinessAdministratorService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,UserId,BusinessAdministrator> implements BusinessAdministratorService<C,F> {
        WrappedBusinessAdministratorService(WrappedConnector<C,F> connector) {
            super(connector, UserId.class, BusinessAdministrator.class);
        }
    }
    final WrappedBusinessAdministratorService<C,F> businessAdministrators;
    @Override
    final public BusinessAdministratorService<C,F> getBusinessAdministrators() {
        return businessAdministrators;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BusinessAdministratorRoleService">
    static class WrappedBusinessAdministratorRoleService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,BusinessAdministratorRole> implements BusinessAdministratorRoleService<C,F> {
        WrappedBusinessAdministratorRoleService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, BusinessAdministratorRole.class);
        }
    }
    final WrappedBusinessAdministratorRoleService<C,F> businessAdministratorRoles;
    @Override
    final public BusinessAdministratorRoleService<C,F> getBusinessAdministratorRoles() {
        return businessAdministratorRoles;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BusinessProfileService">
    static class WrappedBusinessProfileService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,BusinessProfile> implements BusinessProfileService<C,F> {
        WrappedBusinessProfileService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, BusinessProfile.class);
        }
    }
    final WrappedBusinessProfileService<C,F> businessProfiles;
    @Override
    final public BusinessProfileService<C,F> getBusinessProfiles() {
        return businessProfiles;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BusinessService">
    static class WrappedBusinessService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,AccountingCode,Business> implements BusinessService<C,F> {
        WrappedBusinessService(WrappedConnector<C,F> connector) {
            super(connector, AccountingCode.class, Business.class);
        }
    }
    final WrappedBusinessService<C,F> businesses;
    @Override
    final public BusinessService<C,F> getBusinesses() {
        return businesses;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BusinessServerService">
    static class WrappedBusinessServerService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,BusinessServer> implements BusinessServerService<C,F> {
        WrappedBusinessServerService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, BusinessServer.class);
        }
    }
    final WrappedBusinessServerService<C,F> businessServers;
    @Override
    final public BusinessServerService<C,F> getBusinessServers() {
        return businessServers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="CountryCodeService">
    static class WrappedCountryCodeService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,CountryCode> implements CountryCodeService<C,F> {
        WrappedCountryCodeService(WrappedConnector<C,F> connector) {
            super(connector, String.class, CountryCode.class);
        }
    }
    final WrappedCountryCodeService<C,F> countryCodes;
    @Override
    final public CountryCodeService<C,F> getCountryCodes() {
        return countryCodes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="CreditCardProcessorService">
    static class WrappedCreditCardProcessorService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,CreditCardProcessor> implements CreditCardProcessorService<C,F> {
        WrappedCreditCardProcessorService(WrappedConnector<C,F> connector) {
            super(connector, String.class, CreditCardProcessor.class);
        }
    }
    final WrappedCreditCardProcessorService<C,F> creditCardProcessors;
    @Override
    final public CreditCardProcessorService<C,F> getCreditCardProcessors() {
        return creditCardProcessors;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="CreditCardTransactionService">
    static class WrappedCreditCardTransactionService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,CreditCardTransaction> implements CreditCardTransactionService<C,F> {
        WrappedCreditCardTransactionService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, CreditCardTransaction.class);
        }
    }
    final WrappedCreditCardTransactionService<C,F> creditCardTransactions;
    @Override
    final public CreditCardTransactionService<C,F> getCreditCardTransactions() {
        return creditCardTransactions;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="CreditCardService">
    static class WrappedCreditCardService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,CreditCard> implements CreditCardService<C,F> {
        WrappedCreditCardService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, CreditCard.class);
        }
    }
    final WrappedCreditCardService<C,F> creditCards;
    @Override
    final public CreditCardService<C,F> getCreditCards() {
        return creditCards;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="CvsRepositoryService">
    static class WrappedCvsRepositoryService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,CvsRepository> implements CvsRepositoryService<C,F> {
        WrappedCvsRepositoryService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, CvsRepository.class);
        }
    }
    final WrappedCvsRepositoryService<C,F> cvsRepositories;
    @Override
    final public CvsRepositoryService<C,F> getCvsRepositories() {
        return cvsRepositories;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="DisableLogService">
    static class WrappedDisableLogService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,DisableLog> implements DisableLogService<C,F> {
        WrappedDisableLogService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, DisableLog.class);
        }
    }
    final WrappedDisableLogService<C,F> disableLogs;
    @Override
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
        WrappedDnsRecordService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, DnsRecord.class);
        }
    }
    final WrappedDnsRecordService<C,F> dnsRecords;
    @Override
    final public DnsRecordService<C,F> getDnsRecords() {
        return dnsRecords;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="DnsTldService">
    static class WrappedDnsTldService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,DomainName,DnsTld> implements DnsTldService<C,F> {
        WrappedDnsTldService(WrappedConnector<C,F> connector) {
            super(connector, DomainName.class, DnsTld.class);
        }
    }
    final WrappedDnsTldService<C,F> dnsTlds;
    @Override
    final public DnsTldService<C,F> getDnsTlds() {
        return dnsTlds;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="DnsTypeService">
    static class WrappedDnsTypeService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,DnsType> implements DnsTypeService<C,F> {
        WrappedDnsTypeService(WrappedConnector<C,F> connector) {
            super(connector, String.class, DnsType.class);
        }
    }
    final WrappedDnsTypeService<C,F> dnsTypes;
    @Override
    final public DnsTypeService<C,F> getDnsTypes() {
        return dnsTypes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="DnsZoneService">
    static class WrappedDnsZoneService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,DnsZone> implements DnsZoneService<C,F> {
        WrappedDnsZoneService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, DnsZone.class);
        }
    }
    final WrappedDnsZoneService<C,F> dnsZones;
    @Override
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
        WrappedEmailAttachmentTypeService(WrappedConnector<C,F> connector) {
            super(connector, String.class, EmailAttachmentType.class);
        }
    }
    final WrappedEmailAttachmentTypeService<C,F> emailAttachmentTypes;
    @Override
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
        WrappedEmailInboxService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, EmailInbox.class);
        }
    }
    final WrappedEmailInboxService<C,F> emailInboxes;
    @Override
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
        WrappedEmailSmtpRelayTypeService(WrappedConnector<C,F> connector) {
            super(connector, String.class, EmailSmtpRelayType.class);
        }
    }
    final WrappedEmailSmtpRelayTypeService<C,F> emailSmtpRelayTypes;
    @Override
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
        WrappedEmailSpamAssassinIntegrationModeService(WrappedConnector<C,F> connector) {
            super(connector, String.class, EmailSpamAssassinIntegrationMode.class);
        }
    }
    final WrappedEmailSpamAssassinIntegrationModeService<C,F> emailSpamAssassinIntegrationModes;
    @Override
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
        WrappedExpenseCategoryService(WrappedConnector<C,F> connector) {
            super(connector, String.class, ExpenseCategory.class);
        }
    }
    final WrappedExpenseCategoryService<C,F> expenseCategories;
    @Override
    final public ExpenseCategoryService<C,F> getExpenseCategories() {
        return expenseCategories;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="FailoverFileLogService">
    static class WrappedFailoverFileLogService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,FailoverFileLog> implements FailoverFileLogService<C,F> {
        WrappedFailoverFileLogService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, FailoverFileLog.class);
        }
    }
    final WrappedFailoverFileLogService<C,F> failoverFileLogs;
    @Override
    final public FailoverFileLogService<C,F> getFailoverFileLogs() {
        return failoverFileLogs;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="FailoverFileReplicationService">
    static class WrappedFailoverFileReplicationService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,FailoverFileReplication> implements FailoverFileReplicationService<C,F> {
        WrappedFailoverFileReplicationService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, FailoverFileReplication.class);
        }
    }
    final WrappedFailoverFileReplicationService<C,F> failoverFileReplications;
    @Override
    final public FailoverFileReplicationService<C,F> getFailoverFileReplications() {
        return failoverFileReplications;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="FailoverFileScheduleService">
    static class WrappedFailoverFileScheduleService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,FailoverFileSchedule> implements FailoverFileScheduleService<C,F> {
        WrappedFailoverFileScheduleService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, FailoverFileSchedule.class);
        }
    }
    final WrappedFailoverFileScheduleService<C,F> failoverFileSchedules;
    @Override
    final public FailoverFileScheduleService<C,F> getFailoverFileSchedules() {
        return failoverFileSchedules;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="FailoverMySQLReplicationService">
    static class WrappedFailoverMySQLReplicationService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,FailoverMySQLReplication> implements FailoverMySQLReplicationService<C,F> {
        WrappedFailoverMySQLReplicationService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, FailoverMySQLReplication.class);
        }
    }
    final WrappedFailoverMySQLReplicationService<C,F> failoverMySQLReplications;
    @Override
    final public FailoverMySQLReplicationService<C,F> getFailoverMySQLReplications() {
        return failoverMySQLReplications;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="FileBackupSettingService">
    static class WrappedFileBackupSettingService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,FileBackupSetting> implements FileBackupSettingService<C,F> {
        WrappedFileBackupSettingService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, FileBackupSetting.class);
        }
    }
    final WrappedFileBackupSettingService<C,F> fileBackupSettings;
    @Override
    final public FileBackupSettingService<C,F> getFileBackupSettings() {
        return fileBackupSettings;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="FtpGuestUserService">
    static class WrappedFtpGuestUserService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,FtpGuestUser> implements FtpGuestUserService<C,F> {
        WrappedFtpGuestUserService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, FtpGuestUser.class);
        }
    }
    final WrappedFtpGuestUserService<C,F> ftpGuestUsers;
    @Override
    final public FtpGuestUserService<C,F> getFtpGuestUsers() {
        return ftpGuestUsers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="GroupNameService">
    static class WrappedGroupNameService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,GroupId,GroupName> implements GroupNameService<C,F> {
        WrappedGroupNameService(WrappedConnector<C,F> connector) {
            super(connector, GroupId.class, GroupName.class);
        }
    }
    final WrappedGroupNameService<C,F> groupNames;
    @Override
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
        WrappedHttpdJBossVersionService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, HttpdJBossVersion.class);
        }
    }
    final WrappedHttpdJBossVersionService<C,F> httpdJBossVersions;
    @Override
    final public HttpdJBossVersionService<C,F> getHttpdJBossVersions() {
        return httpdJBossVersions;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdJKCodeService">
    static class WrappedHttpdJKCodeService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,HttpdJKCode> implements HttpdJKCodeService<C,F> {
        WrappedHttpdJKCodeService(WrappedConnector<C,F> connector) {
            super(connector, String.class, HttpdJKCode.class);
        }
    }
    final WrappedHttpdJKCodeService<C,F> httpdJKCodes;
    @Override
    final public HttpdJKCodeService<C,F> getHttpdJKCodes() {
        return httpdJKCodes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdJKProtocolService">
    static class WrappedHttpdJKProtocolService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,HttpdJKProtocol> implements HttpdJKProtocolService<C,F> {
        WrappedHttpdJKProtocolService(WrappedConnector<C,F> connector) {
            super(connector, String.class, HttpdJKProtocol.class);
        }
    }
    final WrappedHttpdJKProtocolService<C,F> httpdJKProtocols;
    @Override
    final public HttpdJKProtocolService<C,F> getHttpdJKProtocols() {
        return httpdJKProtocols;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdServerService">
    static class WrappedHttpdServerService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,HttpdServer> implements HttpdServerService<C,F> {
        WrappedHttpdServerService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, HttpdServer.class);
        }
    }
    final WrappedHttpdServerService<C,F> httpdServers;
    @Override
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
        WrappedHttpdSiteService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, HttpdSite.class);
        }
    }
    final WrappedHttpdSiteService<C,F> httpdSites;
    @Override
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
        WrappedHttpdTomcatVersionService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, HttpdTomcatVersion.class);
        }
    }
    final WrappedHttpdTomcatVersionService<C,F> httpdTomcatVersions;
    @Override
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
        WrappedIPAddressService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, IPAddress.class);
        }
    }
    final WrappedIPAddressService<C,F> ipAddresses;
    @Override
    final public IPAddressService<C,F> getIpAddresses() {
        return ipAddresses;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="LanguageService">
    static class WrappedLanguageService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,Language> implements LanguageService<C,F> {
        WrappedLanguageService(WrappedConnector<C,F> connector) {
            super(connector, String.class, Language.class);
        }
    }
    final WrappedLanguageService<C,F> languages;
    @Override
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
        WrappedLinuxAccountGroupService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, LinuxAccountGroup.class);
        }
    }
    final WrappedLinuxAccountGroupService<C,F> linuxAccountGroups;
    @Override
    final public LinuxAccountGroupService<C,F> getLinuxAccountGroups() {
        return linuxAccountGroups;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="LinuxAccountTypeService">
    static class WrappedLinuxAccountTypeService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,LinuxAccountType> implements LinuxAccountTypeService<C,F> {
        WrappedLinuxAccountTypeService(WrappedConnector<C,F> connector) {
            super(connector, String.class, LinuxAccountType.class);
        }
    }
    final WrappedLinuxAccountTypeService<C,F> linuxAccountTypes;
    @Override
    final public LinuxAccountTypeService<C,F> getLinuxAccountTypes() {
        return linuxAccountTypes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="LinuxAccountService">
    static class WrappedLinuxAccountService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,LinuxAccount> implements LinuxAccountService<C,F> {
        WrappedLinuxAccountService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, LinuxAccount.class);
        }
    }
    final WrappedLinuxAccountService<C,F> linuxAccounts;
    @Override
    final public LinuxAccountService<C,F> getLinuxAccounts() {
        return linuxAccounts;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="LinuxGroupTypeService">
    static class WrappedLinuxGroupTypeService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,LinuxGroupType> implements LinuxGroupTypeService<C,F> {
        WrappedLinuxGroupTypeService(WrappedConnector<C,F> connector) {
            super(connector, String.class, LinuxGroupType.class);
        }
    }
    final WrappedLinuxGroupTypeService<C,F> linuxGroupTypes;
    @Override
    final public LinuxGroupTypeService<C,F> getLinuxGroupTypes() {
        return linuxGroupTypes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="LinuxGroupService">
    static class WrappedLinuxGroupService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,LinuxGroup> implements LinuxGroupService<C,F> {
        WrappedLinuxGroupService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, LinuxGroup.class);
        }
    }
    final WrappedLinuxGroupService<C,F> linuxGroups;
    @Override
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
        WrappedMajordomoVersionService(WrappedConnector<C,F> connector) {
            super(connector, String.class, MajordomoVersion.class);
        }
    }
    final WrappedMajordomoVersionService<C,F> majordomoVersions;
    @Override
    final public MajordomoVersionService<C,F> getMajordomoVersions() {
        return majordomoVersions;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MasterHostService">
    static class WrappedMasterHostService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,MasterHost> implements MasterHostService<C,F> {
        WrappedMasterHostService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, MasterHost.class);
        }
    }
    final WrappedMasterHostService<C,F> masterHosts;
    @Override
    final public MasterHostService<C,F> getMasterHosts() {
        return masterHosts;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MasterServerService">
    static class WrappedMasterServerService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,MasterServer> implements MasterServerService<C,F> {
        WrappedMasterServerService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, MasterServer.class);
        }
    }
    final WrappedMasterServerService<C,F> masterServers;
    @Override
    final public MasterServerService<C,F> getMasterServers() {
        return masterServers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MasterUserService">
    static class WrappedMasterUserService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,UserId,MasterUser> implements MasterUserService<C,F> {
        WrappedMasterUserService(WrappedConnector<C,F> connector) {
            super(connector, UserId.class, MasterUser.class);
        }
    }
    final WrappedMasterUserService<C,F> masterUsers;
    @Override
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
        WrappedMySQLDatabaseService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, MySQLDatabase.class);
        }
    }
    final WrappedMySQLDatabaseService<C,F> mysqlDatabases;
    @Override
    final public MySQLDatabaseService<C,F> getMysqlDatabases() {
        return mysqlDatabases;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MySQLDBUserService">
    static class WrappedMySQLDBUserService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,MySQLDBUser> implements MySQLDBUserService<C,F> {
        WrappedMySQLDBUserService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, MySQLDBUser.class);
        }
    }
    final WrappedMySQLDBUserService<C,F> mysqlDBUsers;
    @Override
    final public MySQLDBUserService<C,F> getMysqlDBUsers() {
        return mysqlDBUsers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MySQLServerService">
    static class WrappedMySQLServerService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,MySQLServer> implements MySQLServerService<C,F> {
        WrappedMySQLServerService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, MySQLServer.class);
        }
    }
    final WrappedMySQLServerService<C,F> mysqlServers;
    @Override
    final public MySQLServerService<C,F> getMysqlServers() {
        return mysqlServers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MySQLUserService">
    static class WrappedMySQLUserService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,MySQLUser> implements MySQLUserService<C,F> {
        WrappedMySQLUserService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, MySQLUser.class);
        }
    }
    final WrappedMySQLUserService<C,F> mysqlUsers;
    @Override
    final public MySQLUserService<C,F> getMysqlUsers() {
        return mysqlUsers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="NetBindService">
    static class WrappedNetBindService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,NetBind> implements NetBindService<C,F> {
        WrappedNetBindService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, NetBind.class);
        }
    }
    final WrappedNetBindService<C,F> netBinds;
    @Override
    final public NetBindService<C,F> getNetBinds() {
        return netBinds;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="NetDeviceIDService">
    static class WrappedNetDeviceIDService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,NetDeviceID> implements NetDeviceIDService<C,F> {
        WrappedNetDeviceIDService(WrappedConnector<C,F> connector) {
            super(connector, String.class, NetDeviceID.class);
        }
    }
    final WrappedNetDeviceIDService<C,F> netDeviceIDs;
    @Override
    final public NetDeviceIDService<C,F> getNetDeviceIDs() {
        return netDeviceIDs;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="NetDeviceService">
    static class WrappedNetDeviceService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,NetDevice> implements NetDeviceService<C,F> {
        WrappedNetDeviceService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, NetDevice.class);
        }
    }
    final WrappedNetDeviceService<C,F> netDevices;
    @Override
    final public NetDeviceService<C,F> getNetDevices() {
        return netDevices;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="NetProtocolService">
    static class WrappedNetProtocolService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,NetProtocol> implements NetProtocolService<C,F> {
        WrappedNetProtocolService(WrappedConnector<C,F> connector) {
            super(connector, String.class, NetProtocol.class);
        }
    }
    final WrappedNetProtocolService<C,F> netProtocols;
    @Override
    final public NetProtocolService<C,F> getNetProtocols() {
        return netProtocols;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="NetTcpRedirectService">
    static class WrappedNetTcpRedirectService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,NetTcpRedirect> implements NetTcpRedirectService<C,F> {
        WrappedNetTcpRedirectService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, NetTcpRedirect.class);
        }
    }
    final WrappedNetTcpRedirectService<C,F> netTcpRedirects;
    @Override
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
        WrappedNoticeTypeService(WrappedConnector<C,F> connector) {
            super(connector, String.class, NoticeType.class);
        }
    }
    final WrappedNoticeTypeService<C,F> noticeTypes;
    @Override
    final public NoticeTypeService<C,F> getNoticeTypes() {
        return noticeTypes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="OperatingSystemVersionService">
    static class WrappedOperatingSystemVersionService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,OperatingSystemVersion> implements OperatingSystemVersionService<C,F> {
        WrappedOperatingSystemVersionService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, OperatingSystemVersion.class);
        }
    }
    final WrappedOperatingSystemVersionService<C,F> operatingSystemVersions;
    @Override
    final public OperatingSystemVersionService<C,F> getOperatingSystemVersions() {
        return operatingSystemVersions;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="OperatingSystemService">
    static class WrappedOperatingSystemService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,OperatingSystem> implements OperatingSystemService<C,F> {
        WrappedOperatingSystemService(WrappedConnector<C,F> connector) {
            super(connector, String.class, OperatingSystem.class);
        }
    }
    final WrappedOperatingSystemService<C,F> operatingSystems;
    @Override
    final public OperatingSystemService<C,F> getOperatingSystems() {
        return operatingSystems;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PackageCategoryService">
    static class WrappedPackageCategoryService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,PackageCategory> implements PackageCategoryService<C,F> {
        WrappedPackageCategoryService(WrappedConnector<C,F> connector) {
            super(connector, String.class, PackageCategory.class);
        }
    }
    final WrappedPackageCategoryService<C,F> packageCategories;
    @Override
    final public PackageCategoryService<C,F> getPackageCategories() {
        return packageCategories;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PackageDefinitionBusinessService">
    static class WrappedPackageDefinitionBusinessService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,PackageDefinitionBusiness> implements PackageDefinitionBusinessService<C,F> {
        WrappedPackageDefinitionBusinessService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, PackageDefinitionBusiness.class);
        }
    }
    final WrappedPackageDefinitionBusinessService<C,F> packageDefinitionBusinesses;
    @Override
    final public PackageDefinitionBusinessService<C,F> getPackageDefinitionBusinesses() {
        return packageDefinitionBusinesses;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PackageDefinitionLimitService">
    static class WrappedPackageDefinitionLimitService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,PackageDefinitionLimit> implements PackageDefinitionLimitService<C,F> {
        WrappedPackageDefinitionLimitService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, PackageDefinitionLimit.class);
        }
    }
    final WrappedPackageDefinitionLimitService<C,F> packageDefinitionLimits;
    @Override
    final public PackageDefinitionLimitService<C,F> getPackageDefinitionLimits() {
        return packageDefinitionLimits;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PackageDefinitionService">
    static class WrappedPackageDefinitionService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,PackageDefinition> implements PackageDefinitionService<C,F> {
        WrappedPackageDefinitionService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, PackageDefinition.class);
        }
    }
    final WrappedPackageDefinitionService<C,F> packageDefinitions;
    @Override
    final public PackageDefinitionService<C,F> getPackageDefinitions() {
        return packageDefinitions;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PaymentTypeService">
    static class WrappedPaymentTypeService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,PaymentType> implements PaymentTypeService<C,F> {
        WrappedPaymentTypeService(WrappedConnector<C,F> connector) {
            super(connector, String.class, PaymentType.class);
        }
    }
    final WrappedPaymentTypeService<C,F> paymentTypes;
    @Override
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
        WrappedPostgresDatabaseService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, PostgresDatabase.class);
        }
    }
    final WrappedPostgresDatabaseService<C,F> postgresDatabases;
    @Override
    final public PostgresDatabaseService<C,F> getPostgresDatabases() {
        return postgresDatabases;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PostgresEncodingService">
    static class WrappedPostgresEncodingService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,PostgresEncoding> implements PostgresEncodingService<C,F> {
        WrappedPostgresEncodingService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, PostgresEncoding.class);
        }
    }
    final WrappedPostgresEncodingService<C,F> postgresEncodings;
    @Override
    final public PostgresEncodingService<C,F> getPostgresEncodings() {
        return postgresEncodings;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PostgresServerService">
    static class WrappedPostgresServerService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,PostgresServer> implements PostgresServerService<C,F> {
        WrappedPostgresServerService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, PostgresServer.class);
        }
    }
    final WrappedPostgresServerService<C,F> postgresServers;
    @Override
    final public PostgresServerService<C,F> getPostgresServers() {
        return postgresServers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PostgresUserService">
    static class WrappedPostgresUserService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,PostgresUser> implements PostgresUserService<C,F> {
        WrappedPostgresUserService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, PostgresUser.class);
        }
    }
    final WrappedPostgresUserService<C,F> postgresUsers;
    @Override
    final public PostgresUserService<C,F> getPostgresUsers() {
        return postgresUsers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PostgresVersionService">
    static class WrappedPostgresVersionService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,PostgresVersion> implements PostgresVersionService<C,F> {
        WrappedPostgresVersionService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, PostgresVersion.class);
        }
    }
    final WrappedPostgresVersionService<C,F> postgresVersions;
    @Override
    final public PostgresVersionService<C,F> getPostgresVersions() {
        return postgresVersions;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PrivateFtpServerService">
    static class WrappedPrivateFtpServerService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,PrivateFtpServer> implements PrivateFtpServerService<C,F> {
        WrappedPrivateFtpServerService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, PrivateFtpServer.class);
        }
    }
    final WrappedPrivateFtpServerService<C,F> privateFtpServers;
    @Override
    final public PrivateFtpServerService<C,F> getPrivateFtpServers() {
        return privateFtpServers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ProcessorTypeService">
    static class WrappedProcessorTypeService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,ProcessorType> implements ProcessorTypeService<C,F> {
        WrappedProcessorTypeService(WrappedConnector<C,F> connector) {
            super(connector, String.class, ProcessorType.class);
        }
    }
    final WrappedProcessorTypeService<C,F> processorTypes;
    @Override
    final public ProcessorTypeService<C,F> getProcessorTypes() {
        return processorTypes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ProtocolService">
    static class WrappedProtocolService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,Protocol> implements ProtocolService<C,F> {
        WrappedProtocolService(WrappedConnector<C,F> connector) {
            super(connector, String.class, Protocol.class);
        }
    }
    final WrappedProtocolService<C,F> protocols;
    @Override
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
        WrappedResellerService(WrappedConnector<C,F> connector) {
            super(connector, AccountingCode.class, Reseller.class);
        }
    }
    final WrappedResellerService<C,F> resellers;
    @Override
    final public ResellerService<C,F> getResellers() {
        return resellers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ResourceTypeService">
    static class WrappedResourceTypeService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,ResourceType> implements ResourceTypeService<C,F> {
        WrappedResourceTypeService(WrappedConnector<C,F> connector) {
            super(connector, String.class, ResourceType.class);
        }
    }
    final WrappedResourceTypeService<C,F> resourceTypes;
    @Override
    final public ResourceTypeService<C,F> getResourceTypes() {
        return resourceTypes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ResourceService">
    final ResourceService<C,F> resources;
    @Override
    final public ResourceService<C,F> getResources() {
        return resources;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ServerFarmService">
    static class WrappedServerFarmService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,DomainLabel,ServerFarm> implements ServerFarmService<C,F> {
        WrappedServerFarmService(WrappedConnector<C,F> connector) {
            super(connector, DomainLabel.class, ServerFarm.class);
        }
    }
    final WrappedServerFarmService<C,F> serverFarms;
    @Override
    final public ServerFarmService<C,F> getServerFarms() {
        return serverFarms;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ServerResourceService">
    final ServerResourceService<C,F> serverResources;
    @Override
    final public ServerResourceService<C,F> getServerResources() {
        return serverResources;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ServerService">
    static class WrappedServerService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,Server> implements ServerService<C,F> {
        WrappedServerService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, Server.class);
        }
    }
    final WrappedServerService<C,F> servers;
    @Override
    final public ServerService<C,F> getServers() {
        return servers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ShellService">
    static class WrappedShellService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,UnixPath,Shell> implements ShellService<C,F> {
        WrappedShellService(WrappedConnector<C,F> connector) {
            super(connector, UnixPath.class, Shell.class);
        }
    }
    final WrappedShellService<C,F> shells;
    @Override
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
        WrappedTechnologyService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, Technology.class);
        }
    }
    final WrappedTechnologyService<C,F> technologies;
    @Override
    final public TechnologyService<C,F> getTechnologies() {
        return technologies;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TechnologyClassService">
    static class WrappedTechnologyClassService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,TechnologyClass> implements TechnologyClassService<C,F> {
        WrappedTechnologyClassService(WrappedConnector<C,F> connector) {
            super(connector, String.class, TechnologyClass.class);
        }
    }
    final WrappedTechnologyClassService<C,F> technologyClasses;
    @Override
    final public TechnologyClassService<C,F> getTechnologyClasses() {
        return technologyClasses;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TechnologyNameService">
    static class WrappedTechnologyNameService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,TechnologyName> implements TechnologyNameService<C,F> {
        WrappedTechnologyNameService(WrappedConnector<C,F> connector) {
            super(connector, String.class, TechnologyName.class);
        }
    }
    final WrappedTechnologyNameService<C,F> technologyNames;
    @Override
    final public TechnologyNameService<C,F> getTechnologyNames() {
        return technologyNames;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TechnologyVersionService">
    static class WrappedTechnologyVersionService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,TechnologyVersion> implements TechnologyVersionService<C,F> {
        WrappedTechnologyVersionService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, TechnologyVersion.class);
        }
    }
    final WrappedTechnologyVersionService<C,F> technologyVersions;
    @Override
    final public TechnologyVersionService<C,F> getTechnologyVersions() {
        return technologyVersions;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TicketActionTypeService">
    static class WrappedTicketActionTypeService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,TicketActionType> implements TicketActionTypeService<C,F> {
        WrappedTicketActionTypeService(WrappedConnector<C,F> connector) {
            super(connector, String.class, TicketActionType.class);
        }
    }
    final WrappedTicketActionTypeService<C,F> ticketActionTypes;
    @Override
    final public TicketActionTypeService<C,F> getTicketActionTypes() {
        return ticketActionTypes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TicketActionService">
    static class WrappedTicketActionService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,TicketAction> implements TicketActionService<C,F> {
        WrappedTicketActionService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, TicketAction.class);
        }
    }
    final WrappedTicketActionService<C,F> ticketActions;
    @Override
    final public TicketActionService<C,F> getTicketActions() {
        return ticketActions;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TicketAssignmentService">
    static class WrappedTicketAssignmentService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,TicketAssignment> implements TicketAssignmentService<C,F> {
        WrappedTicketAssignmentService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, TicketAssignment.class);
        }
    }
    final WrappedTicketAssignmentService<C,F> ticketAssignments;
    @Override
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
        WrappedTicketCategoryService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, TicketCategory.class);
        }
    }
    final WrappedTicketCategoryService<C,F> ticketCategories;
    @Override
    final public TicketCategoryService<C,F> getTicketCategories() {
        return ticketCategories;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TicketPriorityService">
    static class WrappedTicketPriorityService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,TicketPriority> implements TicketPriorityService<C,F> {
        WrappedTicketPriorityService(WrappedConnector<C,F> connector) {
            super(connector, String.class, TicketPriority.class);
        }
    }
    final WrappedTicketPriorityService<C,F> ticketPriorities;
    @Override
    final public TicketPriorityService<C,F> getTicketPriorities() {
        return ticketPriorities;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TicketStatusService">
    static class WrappedTicketStatusService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,TicketStatus> implements TicketStatusService<C,F> {
        WrappedTicketStatusService(WrappedConnector<C,F> connector) {
            super(connector, String.class, TicketStatus.class);
        }
    }
    final WrappedTicketStatusService<C,F> ticketStatuses;
    @Override
    final public TicketStatusService<C,F> getTicketStatuses() {
        return ticketStatuses;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TicketTypeService">
    static class WrappedTicketTypeService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,TicketType> implements TicketTypeService<C,F> {
        WrappedTicketTypeService(WrappedConnector<C,F> connector) {
            super(connector, String.class, TicketType.class);
        }
    }
    final WrappedTicketTypeService<C,F> ticketTypes;
    @Override
    final public TicketTypeService<C,F> getTicketTypes() {
        return ticketTypes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TicketService">
    static class WrappedTicketService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,Ticket> implements TicketService<C,F> {
        WrappedTicketService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, Ticket.class);
        }
    }
    final WrappedTicketService<C,F> tickets;
    @Override
    final public TicketService<C,F> getTickets() {
        return tickets;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TimeZoneService">
    static class WrappedTimeZoneService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,TimeZone> implements TimeZoneService<C,F> {
        WrappedTimeZoneService(WrappedConnector<C,F> connector) {
            super(connector, String.class, TimeZone.class);
        }
    }
    final WrappedTimeZoneService<C,F> timeZones;
    @Override
    final public TimeZoneService<C,F> getTimeZones() {
        return timeZones;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TransactionTypeService">
    static class WrappedTransactionTypeService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,String,TransactionType> implements TransactionTypeService<C,F> {
        WrappedTransactionTypeService(WrappedConnector<C,F> connector) {
            super(connector, String.class, TransactionType.class);
        }
    }
    final WrappedTransactionTypeService<C,F> transactionTypes;
    @Override
    final public TransactionTypeService<C,F> getTransactionTypes() {
        return transactionTypes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TransactionService">
    static class WrappedTransactionService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,Transaction> implements TransactionService<C,F> {
        WrappedTransactionService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, Transaction.class);
        }
    }
    final WrappedTransactionService<C,F> transactions;
    @Override
    final public TransactionService<C,F> getTransactions() {
        return transactions;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="UsernameService">
    static class WrappedUsernameService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,UserId,Username> implements UsernameService<C,F> {
        WrappedUsernameService(WrappedConnector<C,F> connector) {
            super(connector, UserId.class, Username.class);
        }
    }
    final WrappedUsernameService<C,F> usernames;
    @Override
    final public UsernameService<C,F> getUsernames() {
        return usernames;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="VirtualDiskService">
    // TODO: final WrappedVirtualDiskService<C,F> virtualDisks;
    // TODO: final public VirtualDiskService<C,F> getVirtualDisks();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="VirtualServerService">
    static class WrappedVirtualServerService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> extends WrappedService<C,F,Integer,VirtualServer> implements VirtualServerService<C,F> {
        WrappedVirtualServerService(WrappedConnector<C,F> connector) {
            super(connector, Integer.class, VirtualServer.class);
        }
    }
    final WrappedVirtualServerService<C,F> virtualServers;
    @Override
    final public VirtualServerService<C,F> getVirtualServers() {
        return virtualServers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="WhoisHistoryService">
    // TODO: final WrappedWhoisHistoryService<C,F> whoisHistories;
    // TODO: final public WhoisHistoryService<C,F> getWhoisHistory();
    // </editor-fold>
}
