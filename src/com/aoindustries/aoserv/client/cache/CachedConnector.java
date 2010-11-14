/*
 * Copyright 2009-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.cache;

import com.aoindustries.aoserv.client.*;
import com.aoindustries.aoserv.client.command.*;
import com.aoindustries.aoserv.client.validator.*;
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
    final UserId connectAs;
    private final UserId authenticateAs;
    private final String password;

    CachedConnector(CachedConnectorFactory factory, AOServConnector<?,?> wrapped) throws RemoteException, LoginException {
        this.factory = factory;
        this.wrapped = wrapped;
        locale = wrapped.getLocale();
        connectAs = wrapped.getConnectAs();
        authenticateAs = wrapped.getAuthenticateAs();
        password = wrapped.getPassword();
        aoserverDaemonHosts = new CachedAOServerDaemonHostService(this, wrapped.getAoServerDaemonHosts());
        aoserverResources = new AOServerResourceService<CachedConnector,CachedConnectorFactory>(this);
        aoservers = new CachedAOServerService(this, wrapped.getAoServers());
        aoservPermissions = new CachedAOServPermissionService(this, wrapped.getAoservPermissions());
        aoservRoles = new CachedAOServRoleService(this, wrapped.getAoservRoles());
        aoservRolePermissions = new CachedAOServRolePermissionService(this, wrapped.getAoservRolePermissions());
        architectures = new CachedArchitectureService(this, wrapped.getArchitectures());
        backupPartitions = new CachedBackupPartitionService(this, wrapped.getBackupPartitions());
        backupRetentions = new CachedBackupRetentionService(this, wrapped.getBackupRetentions());
        // TODO: bankAccounts = new CachedBankAccountService(this, wrapped.getBankAccounts());
        bankTransactionTypes = new CachedBankTransactionTypeService(this, wrapped.getBankTransactionTypes());
        // TODO: bankTransactions = new CachedBankTransactionService(this, wrapped.getBankTransactions());
        // TODO: banks = new CachedBankService(this, wrapped.getBanks());
        brands = new CachedBrandService(this, wrapped.getBrands());
        businessAdministrators = new CachedBusinessAdministratorService(this, wrapped.getBusinessAdministrators());
        businessAdministratorRoles = new CachedBusinessAdministratorRoleService(this, wrapped.getBusinessAdministratorRoles());
        businessProfiles = new CachedBusinessProfileService(this, wrapped.getBusinessProfiles());
        businesses = new CachedBusinessService(this, wrapped.getBusinesses());
        businessServers = new CachedBusinessServerService(this, wrapped.getBusinessServers());
        countryCodes = new CachedCountryCodeService(this, wrapped.getCountryCodes());
        creditCardProcessors = new CachedCreditCardProcessorService(this, wrapped.getCreditCardProcessors());
        creditCardTransactions = new CachedCreditCardTransactionService(this, wrapped.getCreditCardTransactions());
        creditCards = new CachedCreditCardService(this, wrapped.getCreditCards());
        cvsRepositories = new CachedCvsRepositoryService(this, wrapped.getCvsRepositories());
        disableLogs = new CachedDisableLogService(this, wrapped.getDisableLogs());
        // TODO: distroFileTypes = new CachedDistroFileTypeService(this, wrapped.getDistroFileTypes());
        // TODO: distroFiles = new CachedDistroFileService(this, wrapped.getDistroFiles());
        dnsRecords = new CachedDnsRecordService(this, wrapped.getDnsRecords());
        dnsTlds = new CachedDnsTldService(this, wrapped.getDnsTlds());
        dnsTypes = new CachedDnsTypeService(this, wrapped.getDnsTypes());
        dnsZones = new CachedDnsZoneService(this, wrapped.getDnsZones());
        // TODO: emailAddresss = new CachedEmailAddressService(this, wrapped.getEmailAddresss());
        // TODO: emailAttachmentBlocks = new CachedEmailAttachmentBlockService(this, wrapped.getEmailAttachmentBlocks());
        emailAttachmentTypes = new CachedEmailAttachmentTypeService(this, wrapped.getEmailAttachmentTypes());
        // TODO: emailDomains = new CachedEmailDomainService(this, wrapped.getEmailDomains());
        // TODO: emailForwardings = new CachedEmailForwardingService(this, wrapped.getEmailForwardings());
        emailInboxes = new CachedEmailInboxService(this, wrapped.getEmailInboxes());
        /* TODO
        emailListAddresss = new CachedEmailListAddressService(this, wrapped.getEmailListAddresss());
        emailLists = new CachedEmailListService(this, wrapped.getEmailLists());
        emailPipeAddresss = new CachedEmailPipeAddressService(this, wrapped.getEmailPipeAddresss());
        emailPipes = new CachedEmailPipeService(this, wrapped.getEmailPipes());
         */
        emailSmtpRelayTypes = new CachedEmailSmtpRelayTypeService(this, wrapped.getEmailSmtpRelayTypes());
        // TODO: emailSmtpRelays = new CachedEmailSmtpRelayService(this, wrapped.getEmailSmtpRelays());
        // TODO: emailSmtpSmartHostDomains = new CachedEmailSmtpSmartHostDomainService(this, wrapped.getEmailSmtpSmartHostDomains());
        // TODO: emailSmtpSmartHosts = new CachedEmailSmtpSmartHostService(this, wrapped.getEmailSmtpSmartHosts());
        emailSpamAssassinIntegrationModes = new CachedEmailSpamAssassinIntegrationModeService(this, wrapped.getEmailSpamAssassinIntegrationModes());
        // TODO: encryptionKeys = new CachedEncryptionKeyService(this, wrapped.getEncryptionKeys());
        expenseCategories = new CachedExpenseCategoryService(this, wrapped.getExpenseCategories());
        failoverFileLogs = new CachedFailoverFileLogService(this, wrapped.getFailoverFileLogs());
        failoverFileReplications = new CachedFailoverFileReplicationService(this, wrapped.getFailoverFileReplications());
        failoverFileSchedules = new CachedFailoverFileScheduleService(this, wrapped.getFailoverFileSchedules());
        failoverMySQLReplications = new CachedFailoverMySQLReplicationService(this, wrapped.getFailoverMySQLReplications());
        fileBackupSettings = new CachedFileBackupSettingService(this, wrapped.getFileBackupSettings());
        groupNames = new CachedGroupNameService(this, wrapped.getGroupNames());
        ftpGuestUsers = new CachedFtpGuestUserService(this, wrapped.getFtpGuestUsers());
        /* TODO
        httpdBinds = new CachedHttpdBindService(this, wrapped.getHttpdBinds());
        httpdJBossSites = new CachedHttpdJBossSiteService(this, wrapped.getHttpdJBossSites());
         */
        httpdJBossVersions = new CachedHttpdJBossVersionService(this, wrapped.getHttpdJBossVersions());
        httpdJKCodes = new CachedHttpdJKCodeService(this, wrapped.getHttpdJKCodes());
        httpdJKProtocols = new CachedHttpdJKProtocolService(this, wrapped.getHttpdJKProtocols());
        httpdServers = new CachedHttpdServerService(this, wrapped.getHttpdServers());
        /* TODO
        httpdSharedTomcats = new CachedHttpdSharedTomcatService(this, wrapped.getHttpdSharedTomcats());
        httpdSiteAuthenticatedLocations = new CachedHttpdSiteAuthenticatedLocationService(this, wrapped.getHttpdSiteAuthenticatedLocations());
        httpdSiteBinds = new CachedHttpdSiteBindService(this, wrapped.getHttpdSiteBinds());
        httpdSiteURLs = new CachedHttpdSiteURLService(this, wrapped.getHttpdSiteURLs());
         */
        httpdSites = new CachedHttpdSiteService(this, wrapped.getHttpdSites());
        // TODO: httpdStaticSites = new CachedHttpdStaticSiteService(this, wrapped.getHttpdStaticSites());
        // TODO: httpdTomcatContexts = new CachedHttpdTomcatContextService(this, wrapped.getHttpdTomcatContexts());
        // TODO: httpdTomcatDataSources = new CachedHttpdTomcatDataSourceService(this, wrapped.getHttpdTomcatDataSources());
        // TODO: httpdTomcatParameters = new CachedHttpdTomcatParameterService(this, wrapped.getHttpdTomcatParameters());
        // TODO: httpdTomcatSites = new CachedHttpdTomcatSiteService(this, wrapped.getHttpdTomcatSites());
        // TODO: httpdTomcatSharedSites = new CachedHttpdTomcatSharedSiteService(this, wrapped.getHttpdTomcatSharedSites());
        // TODO: httpdTomcatStdSites = new CachedHttpdTomcatStdSiteService(this, wrapped.getHttpdTomcatStdSites());
        httpdTomcatVersions = new CachedHttpdTomcatVersionService(this, wrapped.getHttpdTomcatVersions());
        // TODO: httpdWorkers = new CachedHttpdWorkerService(this, wrapped.getHttpdWorkers());
        ipAddresses = new CachedIPAddressService(this, wrapped.getIpAddresses());
        languages = new CachedLanguageService(this, wrapped.getLanguages());
        /* TODO
        linuxAccAddresss = new CachedLinuxAccAddressService(this, wrapped.getLinuxAccAddresss());
         */
        linuxAccountGroups = new CachedLinuxAccountGroupService(this, wrapped.getLinuxAccountGroups());
        linuxAccountTypes = new CachedLinuxAccountTypeService(this, wrapped.getLinuxAccountTypes());
        linuxAccounts = new CachedLinuxAccountService(this, wrapped.getLinuxAccounts());
        linuxGroupTypes = new CachedLinuxGroupTypeService(this, wrapped.getLinuxGroupTypes());
        linuxGroups = new CachedLinuxGroupService(this, wrapped.getLinuxGroups());
        /* TODO
        majordomoLists = new CachedMajordomoListService(this, wrapped.getMajordomoLists());
        majordomoServers = new CachedMajordomoServerService(this, wrapped.getMajordomoServers());
         */
        majordomoVersions = new CachedMajordomoVersionService(this, wrapped.getMajordomoVersions());
        masterHosts = new CachedMasterHostService(this, wrapped.getMasterHosts());
        masterServers = new CachedMasterServerService(this, wrapped.getMasterServers());
        masterUsers = new CachedMasterUserService(this, wrapped.getMasterUsers());
        // TODO: monthlyCharges = new CachedMonthlyChargeService(this, wrapped.getMonthlyCharges());
        mysqlDatabases = new CachedMySQLDatabaseService(this, wrapped.getMysqlDatabases());
        mysqlDBUsers = new CachedMySQLDBUserService(this, wrapped.getMysqlDBUsers());
        mysqlServers = new CachedMySQLServerService(this, wrapped.getMysqlServers());
        mysqlUsers = new CachedMySQLUserService(this, wrapped.getMysqlUsers());
        netBinds = new CachedNetBindService(this, wrapped.getNetBinds());
        netDeviceIDs = new CachedNetDeviceIDService(this, wrapped.getNetDeviceIDs());
        netDevices = new CachedNetDeviceService(this, wrapped.getNetDevices());
        netProtocols = new CachedNetProtocolService(this, wrapped.getNetProtocols());
        netTcpRedirects = new CachedNetTcpRedirectService(this, wrapped.getNetTcpRedirects());
        // TODO: noticeLogs = new CachedNoticeLogService(this, wrapped.getNoticeLogs());
        noticeTypes = new CachedNoticeTypeService(this, wrapped.getNoticeTypes());
        operatingSystemVersions = new CachedOperatingSystemVersionService(this, wrapped.getOperatingSystemVersions());
        operatingSystems = new CachedOperatingSystemService(this, wrapped.getOperatingSystems());
        packageCategories = new CachedPackageCategoryService(this, wrapped.getPackageCategories());
        packageDefinitionBusinesses = new CachedPackageDefinitionBusinessService(this, wrapped.getPackageDefinitionBusinesses());
        packageDefinitionLimits = new CachedPackageDefinitionLimitService(this, wrapped.getPackageDefinitionLimits());
        packageDefinitions = new CachedPackageDefinitionService(this, wrapped.getPackageDefinitions());
        paymentTypes = new CachedPaymentTypeService(this, wrapped.getPaymentTypes());
        // TODO: physicalServers = new CachedPhysicalServerService(this, wrapped.getPhysicalServers());
        postgresDatabases = new CachedPostgresDatabaseService(this, wrapped.getPostgresDatabases());
        postgresEncodings = new CachedPostgresEncodingService(this, wrapped.getPostgresEncodings());
        postgresServers = new CachedPostgresServerService(this, wrapped.getPostgresServers());
        postgresUsers = new CachedPostgresUserService(this, wrapped.getPostgresUsers());
        postgresVersions = new CachedPostgresVersionService(this, wrapped.getPostgresVersions());
        privateFtpServers = new CachedPrivateFtpServerService(this, wrapped.getPrivateFtpServers());
        processorTypes = new CachedProcessorTypeService(this, wrapped.getProcessorTypes());
        protocols = new CachedProtocolService(this, wrapped.getProtocols());
        // TODO: racks = new CachedRackService(this, wrapped.getRacks());
        resellers = new CachedResellerService(this, wrapped.getResellers());
        resourceTypes = new CachedResourceTypeService(this, wrapped.getResourceTypes());
        resources = new ResourceService<CachedConnector,CachedConnectorFactory>(this);
        serverFarms = new CachedServerFarmService(this, wrapped.getServerFarms());
        serverResources = new ServerResourceService<CachedConnector,CachedConnectorFactory>(this);
        servers = new CachedServerService(this, wrapped.getServers());
        shells = new CachedShellService(this, wrapped.getShells());
        /* TODO
        signupRequestOptions = new CachedSignupRequestOptionService(this, wrapped.getSignupRequestOptions());
        signupRequests = new CachedSignupRequestService(this, wrapped.getSignupRequests());
        spamEmailMessages = new CachedSpamEmailMessageService(this, wrapped.getSpamEmailMessages());
        systemEmailAliass = new CachedSystemEmailAliasService(this, wrapped.getSystemEmailAliass());
         */
        technologies = new CachedTechnologyService(this, wrapped.getTechnologies());
        technologyClasses = new CachedTechnologyClassService(this, wrapped.getTechnologyClasses());
        technologyNames = new CachedTechnologyNameService(this, wrapped.getTechnologyNames());
        technologyVersions = new CachedTechnologyVersionService(this, wrapped.getTechnologyVersions());
        ticketActionTypes = new CachedTicketActionTypeService(this, wrapped.getTicketActionTypes());
        ticketActions = new CachedTicketActionService(this, wrapped.getTicketActions());
        ticketAssignments = new CachedTicketAssignmentService(this, wrapped.getTicketAssignments());
        // TODO: ticketBrandCategories = new CachedTicketBrandCategoryService(this, wrapped.getTicketBrandCategorys());
        ticketCategories = new CachedTicketCategoryService(this, wrapped.getTicketCategories());
        ticketPriorities = new CachedTicketPriorityService(this, wrapped.getTicketPriorities());
        ticketStatuses = new CachedTicketStatusService(this, wrapped.getTicketStatuses());
        ticketTypes = new CachedTicketTypeService(this, wrapped.getTicketTypes());
        tickets = new CachedTicketService(this, wrapped.getTickets());
        timeZones = new CachedTimeZoneService(this, wrapped.getTimeZones());
        transactionTypes = new CachedTransactionTypeService(this, wrapped.getTransactionTypes());
        transactions = new CachedTransactionService(this, wrapped.getTransactions());
        usernames = new CachedUsernameService(this, wrapped.getUsernames());
        // TODO: virtualDisks = new CachedVirtualDiskService(this, wrapped.getVirtualDisks());
        virtualServers = new CachedVirtualServerService(this, wrapped.getVirtualServers());
        // TODO: whoisHistories = new CachedWhoisHistoryService(this, wrapped.getWhoisHistorys());
    }

    @Override
    public CachedConnectorFactory getFactory() {
        return factory;
    }

    @Override
    public boolean isAoServObjectConnectorSettable() {
        return false;
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public void setLocale(Locale locale) throws RemoteException {
        if(!this.locale.equals(locale)) {
            wrapped.setLocale(locale);
            this.locale = locale;
        }
    }

    @Override
    public UserId getConnectAs() {
        return connectAs;
    }

    @Override
    public BusinessAdministrator getThisBusinessAdministrator() throws RemoteException {
        return getBusinessAdministrators().get(connectAs);
    }

    @Override
    public UserId getAuthenticateAs() {
        return authenticateAs;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public <R> CommandResult<R> executeCommand(RemoteCommand<R> command, boolean isInteractive) throws RemoteException {
        CommandResult<R> result = wrapped.executeCommand(command, isInteractive);
        Map<ServiceName,AOServService<CachedConnector,CachedConnectorFactory,?,?>> services = getServices();
        for(ServiceName service : result.getModifiedServiceNames()) ((CachedService)services.get(service)).clearCache();
        return result;
    }

    private final AtomicReference<Map<ServiceName,AOServService<CachedConnector,CachedConnectorFactory,?,?>>> tables = new AtomicReference<Map<ServiceName,AOServService<CachedConnector,CachedConnectorFactory,?,?>>>();
    @Override
    final public Map<ServiceName,AOServService<CachedConnector,CachedConnectorFactory,?,?>> getServices() throws RemoteException {
        Map<ServiceName,AOServService<CachedConnector,CachedConnectorFactory,?,?>> ts = tables.get();
        if(ts==null) {
            ts = AOServConnectorUtils.createServiceMap(this);
            if(!tables.compareAndSet(null, ts)) ts = tables.get();
        }
        return ts;
    }

    // <editor-fold defaultstate="collapsed" desc="AOServerDaemonHostService">
    static class CachedAOServerDaemonHostService extends CachedService<Integer,AOServerDaemonHost> implements AOServerDaemonHostService<CachedConnector,CachedConnectorFactory> {
        CachedAOServerDaemonHostService(CachedConnector connector, AOServerDaemonHostService<?,?> wrapped) {
            super(connector, Integer.class, AOServerDaemonHost.class, wrapped);
        }
    }
    final CachedAOServerDaemonHostService aoserverDaemonHosts;
    @Override
    public AOServerDaemonHostService<CachedConnector,CachedConnectorFactory> getAoServerDaemonHosts() {
        return aoserverDaemonHosts;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="AOServerResourceService">
    final AOServerResourceService<CachedConnector,CachedConnectorFactory> aoserverResources;
    @Override
    public AOServerResourceService<CachedConnector,CachedConnectorFactory> getAoServerResources() {
        return aoserverResources;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="AOServerService">
    static class CachedAOServerService extends CachedService<Integer,AOServer> implements AOServerService<CachedConnector,CachedConnectorFactory> {
        CachedAOServerService(CachedConnector connector, AOServerService<?,?> wrapped) {
            super(connector, Integer.class, AOServer.class, wrapped);
        }
    }
    final CachedAOServerService aoservers;
    @Override
    public AOServerService<CachedConnector,CachedConnectorFactory> getAoServers() {
        return aoservers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="AOServPermissionService">
    static class CachedAOServPermissionService extends CachedService<String,AOServPermission> implements AOServPermissionService<CachedConnector,CachedConnectorFactory> {
        CachedAOServPermissionService(CachedConnector connector, AOServPermissionService<?,?> wrapped) {
            super(connector, String.class, AOServPermission.class, wrapped);
        }
    }
    final CachedAOServPermissionService aoservPermissions;
    @Override
    public AOServPermissionService<CachedConnector,CachedConnectorFactory> getAoservPermissions() {
        return aoservPermissions;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="AOServRoleService">
    static class CachedAOServRoleService extends CachedService<Integer,AOServRole> implements AOServRoleService<CachedConnector,CachedConnectorFactory> {
        CachedAOServRoleService(CachedConnector connector, AOServRoleService<?,?> wrapped) {
            super(connector, Integer.class, AOServRole.class, wrapped);
        }
    }
    final CachedAOServRoleService aoservRoles;
    @Override
    public AOServRoleService<CachedConnector,CachedConnectorFactory> getAoservRoles() {
        return aoservRoles;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="AOServRolePermissionService">
    static class CachedAOServRolePermissionService extends CachedService<Integer,AOServRolePermission> implements AOServRolePermissionService<CachedConnector,CachedConnectorFactory> {
        CachedAOServRolePermissionService(CachedConnector connector, AOServRolePermissionService<?,?> wrapped) {
            super(connector, Integer.class, AOServRolePermission.class, wrapped);
        }
    }
    final CachedAOServRolePermissionService aoservRolePermissions;
    @Override
    public AOServRolePermissionService<CachedConnector,CachedConnectorFactory> getAoservRolePermissions() {
        return aoservRolePermissions;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ArchitectureService">
    static class CachedArchitectureService extends CachedService<String,Architecture> implements ArchitectureService<CachedConnector,CachedConnectorFactory> {
        CachedArchitectureService(CachedConnector connector, ArchitectureService<?,?> wrapped) {
            super(connector, String.class, Architecture.class, wrapped);
        }
    }
    final CachedArchitectureService architectures;
    @Override
    public ArchitectureService<CachedConnector,CachedConnectorFactory> getArchitectures() {
        return architectures;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BackupPartitionService">
    static class CachedBackupPartitionService extends CachedService<Integer,BackupPartition> implements BackupPartitionService<CachedConnector,CachedConnectorFactory> {
        CachedBackupPartitionService(CachedConnector connector, BackupPartitionService<?,?> wrapped) {
            super(connector, Integer.class, BackupPartition.class, wrapped);
        }
    }
    final CachedBackupPartitionService backupPartitions;
    @Override
    public BackupPartitionService<CachedConnector,CachedConnectorFactory> getBackupPartitions() {
        return backupPartitions;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BackupRetentionService">
    static class CachedBackupRetentionService extends CachedService<Short,BackupRetention> implements BackupRetentionService<CachedConnector,CachedConnectorFactory> {
        CachedBackupRetentionService(CachedConnector connector, BackupRetentionService<?,?> wrapped) {
            super(connector, Short.class, BackupRetention.class, wrapped);
        }
    }
    final CachedBackupRetentionService backupRetentions;
    @Override
    public BackupRetentionService<CachedConnector,CachedConnectorFactory> getBackupRetentions() {
        return backupRetentions;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BankAccountService">
    // TODO: final CachedBankAccountService bankAccounts;
    // TODO: public BankAccountService<CachedConnector,CachedConnectorFactory> getBankAccounts();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BankTransactionTypeService">
    static class CachedBankTransactionTypeService extends CachedService<String,BankTransactionType> implements BankTransactionTypeService<CachedConnector,CachedConnectorFactory> {
        CachedBankTransactionTypeService(CachedConnector connector, BankTransactionTypeService<?,?> wrapped) {
            super(connector, String.class, BankTransactionType.class, wrapped);
        }
    }
    final CachedBankTransactionTypeService bankTransactionTypes;
    @Override
    public BankTransactionTypeService<CachedConnector,CachedConnectorFactory> getBankTransactionTypes() {
        return bankTransactionTypes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BankTransactionService">
    // TODO: final CachedBankTransactionService bankTransactions;
    // TODO: public BankTransactionService<CachedConnector,CachedConnectorFactory> getBankTransactions();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BankService">
    // TODO: final CachedBankService banks;
    // TODO: public BankService<CachedConnector,CachedConnectorFactory> getBanks();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BrandService">
    static class CachedBrandService extends CachedService<AccountingCode,Brand> implements BrandService<CachedConnector,CachedConnectorFactory> {
        CachedBrandService(CachedConnector connector, BrandService<?,?> wrapped) {
            super(connector, AccountingCode.class, Brand.class, wrapped);
        }
    }
    final CachedBrandService brands;
    @Override
    public BrandService<CachedConnector,CachedConnectorFactory> getBrands() {
        return brands;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BusinessAdministratorService">
    static class CachedBusinessAdministratorService extends CachedService<UserId,BusinessAdministrator> implements BusinessAdministratorService<CachedConnector,CachedConnectorFactory> {
        CachedBusinessAdministratorService(CachedConnector connector, BusinessAdministratorService<?,?> wrapped) {
            super(connector, UserId.class, BusinessAdministrator.class, wrapped);
        }
    }
    final CachedBusinessAdministratorService businessAdministrators;
    @Override
    public BusinessAdministratorService<CachedConnector,CachedConnectorFactory> getBusinessAdministrators() {
        return businessAdministrators;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BusinessAdministratorRoleService">
    static class CachedBusinessAdministratorRoleService extends CachedService<Integer,BusinessAdministratorRole> implements BusinessAdministratorRoleService<CachedConnector,CachedConnectorFactory> {
        CachedBusinessAdministratorRoleService(CachedConnector connector, BusinessAdministratorRoleService<?,?> wrapped) {
            super(connector, Integer.class, BusinessAdministratorRole.class, wrapped);
        }
    }
    final CachedBusinessAdministratorRoleService businessAdministratorRoles;
    @Override
    public BusinessAdministratorRoleService<CachedConnector,CachedConnectorFactory> getBusinessAdministratorRoles() {
        return businessAdministratorRoles;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BusinessProfileService">
    static class CachedBusinessProfileService extends CachedService<Integer,BusinessProfile> implements BusinessProfileService<CachedConnector,CachedConnectorFactory> {
        CachedBusinessProfileService(CachedConnector connector, BusinessProfileService<?,?> wrapped) {
            super(connector, Integer.class, BusinessProfile.class, wrapped);
        }
    }
    final CachedBusinessProfileService businessProfiles;
    @Override
    public BusinessProfileService<CachedConnector,CachedConnectorFactory> getBusinessProfiles() {
        return businessProfiles;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BusinessService">
    static class CachedBusinessService extends CachedService<AccountingCode,Business> implements BusinessService<CachedConnector,CachedConnectorFactory> {
        CachedBusinessService(CachedConnector connector, BusinessService<?,?> wrapped) {
            super(connector, AccountingCode.class, Business.class, wrapped);
        }
    }
    final CachedBusinessService businesses;
    @Override
    public BusinessService<CachedConnector,CachedConnectorFactory> getBusinesses() {
        return businesses;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="BusinessServerService">
    static class CachedBusinessServerService extends CachedService<Integer,BusinessServer> implements BusinessServerService<CachedConnector,CachedConnectorFactory> {
        CachedBusinessServerService(CachedConnector connector, BusinessServerService<?,?> wrapped) {
            super(connector, Integer.class, BusinessServer.class, wrapped);
        }
    }
    final CachedBusinessServerService businessServers;
    @Override
    public BusinessServerService<CachedConnector,CachedConnectorFactory> getBusinessServers() {
        return businessServers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="CountryCodeService">
    static class CachedCountryCodeService extends CachedService<String,CountryCode> implements CountryCodeService<CachedConnector,CachedConnectorFactory> {
        CachedCountryCodeService(CachedConnector connector, CountryCodeService<?,?> wrapped) {
            super(connector, String.class, CountryCode.class, wrapped);
        }
    }
    final CachedCountryCodeService countryCodes;
    @Override
    public CountryCodeService<CachedConnector,CachedConnectorFactory> getCountryCodes() {
        return countryCodes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="CreditCardProcessorService">
    static class CachedCreditCardProcessorService extends CachedService<String,CreditCardProcessor> implements CreditCardProcessorService<CachedConnector,CachedConnectorFactory> {
        CachedCreditCardProcessorService(CachedConnector connector, CreditCardProcessorService<?,?> wrapped) {
            super(connector, String.class, CreditCardProcessor.class, wrapped);
        }
    }
    final CachedCreditCardProcessorService creditCardProcessors;
    @Override
    public CreditCardProcessorService<CachedConnector,CachedConnectorFactory> getCreditCardProcessors() {
        return creditCardProcessors;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="CreditCardTransactionService">
    static class CachedCreditCardTransactionService extends CachedService<Integer,CreditCardTransaction> implements CreditCardTransactionService<CachedConnector,CachedConnectorFactory> {
        CachedCreditCardTransactionService(CachedConnector connector, CreditCardTransactionService<?,?> wrapped) {
            super(connector, Integer.class, CreditCardTransaction.class, wrapped);
        }
    }
    final CachedCreditCardTransactionService creditCardTransactions;
    @Override
    public CreditCardTransactionService<CachedConnector,CachedConnectorFactory> getCreditCardTransactions() {
        return creditCardTransactions;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="CreditCardService">
    static class CachedCreditCardService extends CachedService<Integer,CreditCard> implements CreditCardService<CachedConnector,CachedConnectorFactory> {
        CachedCreditCardService(CachedConnector connector, CreditCardService<?,?> wrapped) {
            super(connector, Integer.class, CreditCard.class, wrapped);
        }
    }
    final CachedCreditCardService creditCards;
    @Override
    public CreditCardService<CachedConnector,CachedConnectorFactory> getCreditCards() {
        return creditCards;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="CvsRepositoryService">
    static class CachedCvsRepositoryService extends CachedService<Integer,CvsRepository> implements CvsRepositoryService<CachedConnector,CachedConnectorFactory> {
        CachedCvsRepositoryService(CachedConnector connector, CvsRepositoryService<?,?> wrapped) {
            super(connector, Integer.class, CvsRepository.class, wrapped);
        }
    }
    final CachedCvsRepositoryService cvsRepositories;
    @Override
    public CvsRepositoryService<CachedConnector,CachedConnectorFactory> getCvsRepositories() {
        return cvsRepositories;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="DisableLogService">
    static class CachedDisableLogService extends CachedService<Integer,DisableLog> implements DisableLogService<CachedConnector,CachedConnectorFactory> {
        CachedDisableLogService(CachedConnector connector, DisableLogService<?,?> wrapped) {
            super(connector, Integer.class, DisableLog.class, wrapped);
        }
    }
    final CachedDisableLogService disableLogs;
    @Override
    public DisableLogService<CachedConnector,CachedConnectorFactory> getDisableLogs() {
        return disableLogs;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="DistroFileTypeService">
    // TODO: final CachedDistroFileTypeService distroFileTypes;
    // TODO: public DistroFileTypeService<CachedConnector,CachedConnectorFactory> getDistroFileTypes();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="DistroFileService">
    // TODO: final CachedDistroFileService distroFiles;
    // TODO: public DistroFileService<CachedConnector,CachedConnectorFactory> getDistroFiles();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="DnsRecordService">
    static class CachedDnsRecordService extends CachedService<Integer,DnsRecord> implements DnsRecordService<CachedConnector,CachedConnectorFactory> {
        CachedDnsRecordService(CachedConnector connector, DnsRecordService<?,?> wrapped) {
            super(connector, Integer.class, DnsRecord.class, wrapped);
        }
    }
    final CachedDnsRecordService dnsRecords;
    @Override
    public DnsRecordService<CachedConnector,CachedConnectorFactory> getDnsRecords() {
        return dnsRecords;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="DnsTldService">
    static class CachedDnsTldService extends CachedService<DomainName,DnsTld> implements DnsTldService<CachedConnector,CachedConnectorFactory> {
        CachedDnsTldService(CachedConnector connector, DnsTldService<?,?> wrapped) {
            super(connector, DomainName.class, DnsTld.class, wrapped);
        }
    }
    final CachedDnsTldService dnsTlds;
    @Override
    public DnsTldService<CachedConnector,CachedConnectorFactory> getDnsTlds() {
        return dnsTlds;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="DnsTypeService">
    static class CachedDnsTypeService extends CachedService<String,DnsType> implements DnsTypeService<CachedConnector,CachedConnectorFactory> {
        CachedDnsTypeService(CachedConnector connector, DnsTypeService<?,?> wrapped) {
            super(connector, String.class, DnsType.class, wrapped);
        }
    }
    final CachedDnsTypeService dnsTypes;
    @Override
    public DnsTypeService<CachedConnector,CachedConnectorFactory> getDnsTypes() {
        return dnsTypes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="DnsZoneService">
    static class CachedDnsZoneService extends CachedService<Integer,DnsZone> implements DnsZoneService<CachedConnector,CachedConnectorFactory> {
        CachedDnsZoneService(CachedConnector connector, DnsZoneService<?,?> wrapped) {
            super(connector, Integer.class, DnsZone.class, wrapped);
        }
    }
    final CachedDnsZoneService dnsZones;
    @Override
    public DnsZoneService<CachedConnector,CachedConnectorFactory> getDnsZones() {
        return dnsZones;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailAddressService">
    // TODO: final CachedEmailAddressService emailAddresss;
    // TODO: public EmailAddressService<CachedConnector,CachedConnectorFactory> getEmailAddresses();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailAttachmentBlockService">
    // TODO: final CachedEmailAttachmentBlockService emailAttachmentBlocks;
    // TODO: public EmailAttachmentBlockService<CachedConnector,CachedConnectorFactory> getEmailAttachmentBlocks();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailAttachmentTypeService">
    static class CachedEmailAttachmentTypeService extends CachedService<String,EmailAttachmentType> implements EmailAttachmentTypeService<CachedConnector,CachedConnectorFactory> {
        CachedEmailAttachmentTypeService(CachedConnector connector, EmailAttachmentTypeService<?,?> wrapped) {
            super(connector, String.class, EmailAttachmentType.class, wrapped);
        }
    }
    final CachedEmailAttachmentTypeService emailAttachmentTypes;
    @Override
    public EmailAttachmentTypeService<CachedConnector,CachedConnectorFactory> getEmailAttachmentTypes() {
        return emailAttachmentTypes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailDomainService">
    // TODO: final CachedEmailDomainService emailDomains;
    // TODO: public EmailDomainService<CachedConnector,CachedConnectorFactory> getEmailDomains();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailForwardingService">
    // TODO: final CachedEmailForwardingService emailForwardings;
    // TODO: public EmailForwardingService<CachedConnector,CachedConnectorFactory> getEmailForwardings();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailInboxService">
    static class CachedEmailInboxService extends CachedService<Integer,EmailInbox> implements EmailInboxService<CachedConnector,CachedConnectorFactory> {
        CachedEmailInboxService(CachedConnector connector, EmailInboxService<?,?> wrapped) {
            super(connector, Integer.class, EmailInbox.class, wrapped);
        }
    }
    final CachedEmailInboxService emailInboxes;
    @Override
    public EmailInboxService<CachedConnector,CachedConnectorFactory> getEmailInboxes() {
        return emailInboxes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailListAddressService">
    // TODO: final CachedEmailListAddressService emailListAddresss;
    // TODO: public EmailListAddressService<CachedConnector,CachedConnectorFactory> getEmailListAddresses();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailListService">
    // TODO: final CachedEmailListService emailLists;
    // TODO: public EmailListService<CachedConnector,CachedConnectorFactory> getEmailLists();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailPipeAddressService">
    // TODO: final CachedEmailPipeAddressService emailPipeAddresss;
    // TODO: public EmailPipeAddressService<CachedConnector,CachedConnectorFactory> getEmailPipeAddresses();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailPipeService">
    // TODO: final CachedEmailPipeService emailPipes;
    // TODO: public EmailPipeService<CachedConnector,CachedConnectorFactory> getEmailPipes();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailSmtpRelayTypeService">
    static class CachedEmailSmtpRelayTypeService extends CachedService<String,EmailSmtpRelayType> implements EmailSmtpRelayTypeService<CachedConnector,CachedConnectorFactory> {
        CachedEmailSmtpRelayTypeService(CachedConnector connector, EmailSmtpRelayTypeService<?,?> wrapped) {
            super(connector, String.class, EmailSmtpRelayType.class, wrapped);
        }
    }
    final CachedEmailSmtpRelayTypeService emailSmtpRelayTypes;
    @Override
    public EmailSmtpRelayTypeService<CachedConnector,CachedConnectorFactory> getEmailSmtpRelayTypes() {
        return emailSmtpRelayTypes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailSmtpRelayService">
    // TODO: final CachedEmailSmtpRelayService emailSmtpRelays;
    // TODO: public EmailSmtpRelayService<CachedConnector,CachedConnectorFactory> getEmailSmtpRelays();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailSmtpSmartHostDomainService">
    // TODO: final CachedEmailSmtpSmartHostDomainService emailSmtpSmartHostDomains;
    // TODO: public EmailSmtpSmartHostDomainService<CachedConnector,CachedConnectorFactory> getEmailSmtpSmartHostDomains();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailSmtpSmartHostService">
    // TODO: final CachedEmailSmtpSmartHostService emailSmtpSmartHosts;
    // TODO: public EmailSmtpSmartHostService<CachedConnector,CachedConnectorFactory> getEmailSmtpSmartHosts();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EmailSpamAssassinIntegrationModeService">
    static class CachedEmailSpamAssassinIntegrationModeService extends CachedService<String,EmailSpamAssassinIntegrationMode> implements EmailSpamAssassinIntegrationModeService<CachedConnector,CachedConnectorFactory> {
        CachedEmailSpamAssassinIntegrationModeService(CachedConnector connector, EmailSpamAssassinIntegrationModeService<?,?> wrapped) {
            super(connector, String.class, EmailSpamAssassinIntegrationMode.class, wrapped);
        }
    }
    final CachedEmailSpamAssassinIntegrationModeService emailSpamAssassinIntegrationModes;
    @Override
    public EmailSpamAssassinIntegrationModeService<CachedConnector,CachedConnectorFactory> getEmailSpamAssassinIntegrationModes() {
        return emailSpamAssassinIntegrationModes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EncryptionKeyService">
    // TODO: final CachedEncryptionKeyService encryptionKeys;
    // TODO: public EncryptionKeyService<CachedConnector,CachedConnectorFactory> getEncryptionKeys();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ExpenseCategoryService">
    static class CachedExpenseCategoryService extends CachedService<String,ExpenseCategory> implements ExpenseCategoryService<CachedConnector,CachedConnectorFactory> {
        CachedExpenseCategoryService(CachedConnector connector, ExpenseCategoryService<?,?> wrapped) {
            super(connector, String.class, ExpenseCategory.class, wrapped);
        }
    }
    final CachedExpenseCategoryService expenseCategories;
    @Override
    public ExpenseCategoryService<CachedConnector,CachedConnectorFactory> getExpenseCategories() {
        return expenseCategories;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="FailoverFileLogService">
    static class CachedFailoverFileLogService extends CachedService<Integer,FailoverFileLog> implements FailoverFileLogService<CachedConnector,CachedConnectorFactory> {
        CachedFailoverFileLogService(CachedConnector connector, FailoverFileLogService<?,?> wrapped) {
            super(connector, Integer.class, FailoverFileLog.class, wrapped);
        }
    }
    final CachedFailoverFileLogService failoverFileLogs;
    @Override
    public FailoverFileLogService<CachedConnector,CachedConnectorFactory> getFailoverFileLogs() {
        return failoverFileLogs;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="FailoverFileReplicationService">
    static class CachedFailoverFileReplicationService extends CachedService<Integer,FailoverFileReplication> implements FailoverFileReplicationService<CachedConnector,CachedConnectorFactory> {
        CachedFailoverFileReplicationService(CachedConnector connector, FailoverFileReplicationService<?,?> wrapped) {
            super(connector, Integer.class, FailoverFileReplication.class, wrapped);
        }
    }
    final CachedFailoverFileReplicationService failoverFileReplications;
    @Override
    public FailoverFileReplicationService<CachedConnector,CachedConnectorFactory> getFailoverFileReplications() {
        return failoverFileReplications;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="FailoverFileScheduleService">
    static class CachedFailoverFileScheduleService extends CachedService<Integer,FailoverFileSchedule> implements FailoverFileScheduleService<CachedConnector,CachedConnectorFactory> {
        CachedFailoverFileScheduleService(CachedConnector connector, FailoverFileScheduleService<?,?> wrapped) {
            super(connector, Integer.class, FailoverFileSchedule.class, wrapped);
        }
    }
    final CachedFailoverFileScheduleService failoverFileSchedules;
    @Override
    public FailoverFileScheduleService<CachedConnector,CachedConnectorFactory> getFailoverFileSchedules() {
        return failoverFileSchedules;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="FailoverMySQLReplicationService">
    static class CachedFailoverMySQLReplicationService extends CachedService<Integer,FailoverMySQLReplication> implements FailoverMySQLReplicationService<CachedConnector,CachedConnectorFactory> {
        CachedFailoverMySQLReplicationService(CachedConnector connector, FailoverMySQLReplicationService<?,?> wrapped) {
            super(connector, Integer.class, FailoverMySQLReplication.class, wrapped);
        }
    }
    final CachedFailoverMySQLReplicationService failoverMySQLReplications;
    @Override
    public FailoverMySQLReplicationService<CachedConnector,CachedConnectorFactory> getFailoverMySQLReplications() {
        return failoverMySQLReplications;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="FileBackupSettingService">
    static class CachedFileBackupSettingService extends CachedService<Integer,FileBackupSetting> implements FileBackupSettingService<CachedConnector,CachedConnectorFactory> {
        CachedFileBackupSettingService(CachedConnector connector, FileBackupSettingService<?,?> wrapped) {
            super(connector, Integer.class, FileBackupSetting.class, wrapped);
        }
    }
    final CachedFileBackupSettingService fileBackupSettings;
    @Override
    public FileBackupSettingService<CachedConnector,CachedConnectorFactory> getFileBackupSettings() {
        return fileBackupSettings;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="FtpGuestUserService">
    static class CachedFtpGuestUserService extends CachedService<Integer,FtpGuestUser> implements FtpGuestUserService<CachedConnector,CachedConnectorFactory> {
        CachedFtpGuestUserService(CachedConnector connector, FtpGuestUserService<?,?> wrapped) {
            super(connector, Integer.class, FtpGuestUser.class, wrapped);
        }
    }
    final CachedFtpGuestUserService ftpGuestUsers;
    @Override
    public FtpGuestUserService<CachedConnector,CachedConnectorFactory> getFtpGuestUsers() {
        return ftpGuestUsers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="GroupNameService">
    static class CachedGroupNameService extends CachedService<GroupId,GroupName> implements GroupNameService<CachedConnector,CachedConnectorFactory> {
        CachedGroupNameService(CachedConnector connector, GroupNameService<?,?> wrapped) {
            super(connector, GroupId.class, GroupName.class, wrapped);
        }
    }
    final CachedGroupNameService groupNames;
    @Override
    public GroupNameService<CachedConnector,CachedConnectorFactory> getGroupNames() {
        return groupNames;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdBindService">
    // TODO: final CachedHttpdBindService httpdBinds;
    // TODO: public HttpdBindService<CachedConnector,CachedConnectorFactory> getHttpdBinds();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdJBossSiteService">
    // TODO: final CachedHttpdJBossSiteService httpdJBossSites;
    // TODO: public HttpdJBossSiteService<CachedConnector,CachedConnectorFactory> getHttpdJBossSites();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdJBossVersionService">
    static class CachedHttpdJBossVersionService extends CachedService<Integer,HttpdJBossVersion> implements HttpdJBossVersionService<CachedConnector,CachedConnectorFactory> {
        CachedHttpdJBossVersionService(CachedConnector connector, HttpdJBossVersionService<?,?> wrapped) {
            super(connector, Integer.class, HttpdJBossVersion.class, wrapped);
        }
    }
    final CachedHttpdJBossVersionService httpdJBossVersions;
    @Override
    public HttpdJBossVersionService<CachedConnector,CachedConnectorFactory> getHttpdJBossVersions() {
        return httpdJBossVersions;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdJKCodeService">
    static class CachedHttpdJKCodeService extends CachedService<String,HttpdJKCode> implements HttpdJKCodeService<CachedConnector,CachedConnectorFactory> {
        CachedHttpdJKCodeService(CachedConnector connector, HttpdJKCodeService<?,?> wrapped) {
            super(connector, String.class, HttpdJKCode.class, wrapped);
        }
    }
    final CachedHttpdJKCodeService httpdJKCodes;
    @Override
    public HttpdJKCodeService<CachedConnector,CachedConnectorFactory> getHttpdJKCodes() {
        return httpdJKCodes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdJKProtocolService">
    static class CachedHttpdJKProtocolService extends CachedService<String,HttpdJKProtocol> implements HttpdJKProtocolService<CachedConnector,CachedConnectorFactory> {
        CachedHttpdJKProtocolService(CachedConnector connector, HttpdJKProtocolService<?,?> wrapped) {
            super(connector, String.class, HttpdJKProtocol.class, wrapped);
        }
    }
    final CachedHttpdJKProtocolService httpdJKProtocols;
    @Override
    public HttpdJKProtocolService<CachedConnector,CachedConnectorFactory> getHttpdJKProtocols() {
        return httpdJKProtocols;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdServerService">
    static class CachedHttpdServerService extends CachedService<Integer,HttpdServer> implements HttpdServerService<CachedConnector,CachedConnectorFactory> {
        CachedHttpdServerService(CachedConnector connector, HttpdServerService<?,?> wrapped) {
            super(connector, Integer.class, HttpdServer.class, wrapped);
        }
    }
    final CachedHttpdServerService httpdServers;
    @Override
    public HttpdServerService<CachedConnector,CachedConnectorFactory> getHttpdServers() {
        return httpdServers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdSharedTomcatService">
    // TODO: final CachedHttpdSharedTomcatService httpdSharedTomcats;
    // TODO: public HttpdSharedTomcatService<CachedConnector,CachedConnectorFactory> getHttpdSharedTomcats();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdSiteAuthenticatedLocationService">
    // TODO: final CachedHttpdSiteAuthenticatedLocationService httpdSiteAuthenticatedLocations;
    // TODO: public HttpdSiteAuthenticatedLocationService<CachedConnector,CachedConnectorFactory> getHttpdSiteAuthenticatedLocations();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdSiteBindService">
    // TODO: final CachedHttpdSiteBindService httpdSiteBinds;
    // TODO: public HttpdSiteBindService<CachedConnector,CachedConnectorFactory> getHttpdSiteBinds();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdSiteURLService">
    // TODO: final CachedHttpdSiteURLService httpdSiteURLs;
    // TODO: public HttpdSiteURLService<CachedConnector,CachedConnectorFactory> getHttpdSiteURLs();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdSiteService">
    static class CachedHttpdSiteService extends CachedService<Integer,HttpdSite> implements HttpdSiteService<CachedConnector,CachedConnectorFactory> {
        CachedHttpdSiteService(CachedConnector connector, HttpdSiteService<?,?> wrapped) {
            super(connector, Integer.class, HttpdSite.class, wrapped);
        }
    }
    final CachedHttpdSiteService httpdSites;
    @Override
    public HttpdSiteService<CachedConnector,CachedConnectorFactory> getHttpdSites() {
        return httpdSites;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdStaticSiteService">
    // TODO: final CachedHttpdStaticSiteService httpdStaticSites;
    // TODO: public HttpdStaticSiteService<CachedConnector,CachedConnectorFactory> getHttpdStaticSites();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdTomcatContextService">
    // TODO: final CachedHttpdTomcatContextService httpdTomcatContexts;
    // TODO: public HttpdTomcatContextService<CachedConnector,CachedConnectorFactory> getHttpdTomcatContexts();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdTomcatDataSourceService">
    // TODO: final CachedHttpdTomcatDataSourceService httpdTomcatDataSources;
    // TODO: public HttpdTomcatDataSourceService<CachedConnector,CachedConnectorFactory> getHttpdTomcatDataSources();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdTomcatParameterService">
    // TODO: final CachedHttpdTomcatParameterService httpdTomcatParameters;
    // TODO: public HttpdTomcatParameterService<CachedConnector,CachedConnectorFactory> getHttpdTomcatParameters();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdTomcatSiteService">
    // TODO: final CachedHttpdTomcatSiteService httpdTomcatSites;
    // TODO: public HttpdTomcatSiteService<CachedConnector,CachedConnectorFactory> getHttpdTomcatSites();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdTomcatSharedSiteService">
    // TODO: final CachedHttpdTomcatSharedSiteService httpdTomcatSharedSites;
    // TODO: public HttpdTomcatSharedSiteService<CachedConnector,CachedConnectorFactory> getHttpdTomcatSharedSites();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdTomcatStdSiteService">
    // TODO: final CachedHttpdTomcatStdSiteService httpdTomcatStdSites;
    // TODO: public HttpdTomcatStdSiteService<CachedConnector,CachedConnectorFactory> getHttpdTomcatStdSites();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdTomcatVersionService">
    static class CachedHttpdTomcatVersionService extends CachedService<Integer,HttpdTomcatVersion> implements HttpdTomcatVersionService<CachedConnector,CachedConnectorFactory> {
        CachedHttpdTomcatVersionService(CachedConnector connector, HttpdTomcatVersionService<?,?> wrapped) {
            super(connector, Integer.class, HttpdTomcatVersion.class, wrapped);
        }
    }
    final CachedHttpdTomcatVersionService httpdTomcatVersions;
    @Override
    public HttpdTomcatVersionService<CachedConnector,CachedConnectorFactory> getHttpdTomcatVersions() {
        return httpdTomcatVersions;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HttpdWorkerService">
    // TODO: final CachedHttpdWorkerService httpdWorkers;
    // TODO: public HttpdWorkerService<CachedConnector,CachedConnectorFactory> getHttpdWorkers();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="IPAddressService">
    static class CachedIPAddressService extends CachedService<Integer,IPAddress> implements IPAddressService<CachedConnector,CachedConnectorFactory> {
        CachedIPAddressService(CachedConnector connector, IPAddressService<?,?> wrapped) {
            super(connector, Integer.class, IPAddress.class, wrapped);
        }
    }
    final CachedIPAddressService ipAddresses;
    @Override
    public IPAddressService<CachedConnector,CachedConnectorFactory> getIpAddresses() {
        return ipAddresses;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="LanguageService">
    static class CachedLanguageService extends CachedService<String,Language> implements LanguageService<CachedConnector,CachedConnectorFactory> {
        CachedLanguageService(CachedConnector connector, LanguageService<?,?> wrapped) {
            super(connector, String.class, Language.class, wrapped);
        }
    }
    final CachedLanguageService languages;
    @Override
    public LanguageService<CachedConnector,CachedConnectorFactory> getLanguages() {
        return languages;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="LinuxAccAddressService">
    // TODO: final CachedLinuxAccAddressService linuxAccAddresss;
    // TODO: public LinuxAccAddressService<CachedConnector,CachedConnectorFactory> getLinuxAccAddresses();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="LinuxAccountGroupService">
    static class CachedLinuxAccountGroupService extends CachedService<Integer,LinuxAccountGroup> implements LinuxAccountGroupService<CachedConnector,CachedConnectorFactory> {
        CachedLinuxAccountGroupService(CachedConnector connector, LinuxAccountGroupService<?,?> wrapped) {
            super(connector, Integer.class, LinuxAccountGroup.class, wrapped);
        }
    }
    final CachedLinuxAccountGroupService linuxAccountGroups;
    @Override
    public LinuxAccountGroupService<CachedConnector,CachedConnectorFactory> getLinuxAccountGroups() {
        return linuxAccountGroups;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="LinuxAccountTypeService">
    static class CachedLinuxAccountTypeService extends CachedService<String,LinuxAccountType> implements LinuxAccountTypeService<CachedConnector,CachedConnectorFactory> {
        CachedLinuxAccountTypeService(CachedConnector connector, LinuxAccountTypeService<?,?> wrapped) {
            super(connector, String.class, LinuxAccountType.class, wrapped);
        }
    }
    final CachedLinuxAccountTypeService linuxAccountTypes;
    @Override
    public LinuxAccountTypeService<CachedConnector,CachedConnectorFactory> getLinuxAccountTypes() {
        return linuxAccountTypes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="LinuxAccountService">
    static class CachedLinuxAccountService extends CachedService<Integer,LinuxAccount> implements LinuxAccountService<CachedConnector,CachedConnectorFactory> {
        CachedLinuxAccountService(CachedConnector connector, LinuxAccountService<?,?> wrapped) {
            super(connector, Integer.class, LinuxAccount.class, wrapped);
        }
    }
    final CachedLinuxAccountService linuxAccounts;
    @Override
    public LinuxAccountService<CachedConnector,CachedConnectorFactory> getLinuxAccounts() {
        return linuxAccounts;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="LinuxGroupTypeService">
    static class CachedLinuxGroupTypeService extends CachedService<String,LinuxGroupType> implements LinuxGroupTypeService<CachedConnector,CachedConnectorFactory> {
        CachedLinuxGroupTypeService(CachedConnector connector, LinuxGroupTypeService<?,?> wrapped) {
            super(connector, String.class, LinuxGroupType.class, wrapped);
        }
    }
    final CachedLinuxGroupTypeService linuxGroupTypes;
    @Override
    public LinuxGroupTypeService<CachedConnector,CachedConnectorFactory> getLinuxGroupTypes() {
        return linuxGroupTypes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="LinuxGroupService">
    static class CachedLinuxGroupService extends CachedService<Integer,LinuxGroup> implements LinuxGroupService<CachedConnector,CachedConnectorFactory> {
        CachedLinuxGroupService(CachedConnector connector, LinuxGroupService<?,?> wrapped) {
            super(connector, Integer.class, LinuxGroup.class, wrapped);
        }
    }
    final CachedLinuxGroupService linuxGroups;
    @Override
    public LinuxGroupService<CachedConnector,CachedConnectorFactory> getLinuxGroups() {
        return linuxGroups;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MajordomoListService">
    // TODO: final CachedMajordomoListService majordomoLists;
    // TODO: public MajordomoListService<CachedConnector,CachedConnectorFactory> getMajordomoLists();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MajordomoServerService">
    // TODO: final CachedMajordomoServerService majordomoServers;
    // TODO: public MajordomoServerService<CachedConnector,CachedConnectorFactory> getMajordomoServers();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MajordomoVersionService">
    static class CachedMajordomoVersionService extends CachedService<String,MajordomoVersion> implements MajordomoVersionService<CachedConnector,CachedConnectorFactory> {
        CachedMajordomoVersionService(CachedConnector connector, MajordomoVersionService<?,?> wrapped) {
            super(connector, String.class, MajordomoVersion.class, wrapped);
        }
    }
    final CachedMajordomoVersionService majordomoVersions;
    @Override
    public MajordomoVersionService<CachedConnector,CachedConnectorFactory> getMajordomoVersions() {
        return majordomoVersions;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MasterHostService">
    static class CachedMasterHostService extends CachedService<Integer,MasterHost> implements MasterHostService<CachedConnector,CachedConnectorFactory> {
        CachedMasterHostService(CachedConnector connector, MasterHostService<?,?> wrapped) {
            super(connector, Integer.class, MasterHost.class, wrapped);
        }
    }
    final CachedMasterHostService masterHosts;
    @Override
    public MasterHostService<CachedConnector,CachedConnectorFactory> getMasterHosts() {
        return masterHosts;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MasterServerService">
    static class CachedMasterServerService extends CachedService<Integer,MasterServer> implements MasterServerService<CachedConnector,CachedConnectorFactory> {
        CachedMasterServerService(CachedConnector connector, MasterServerService<?,?> wrapped) {
            super(connector, Integer.class, MasterServer.class, wrapped);
        }
    }
    final CachedMasterServerService masterServers;
    @Override
    public MasterServerService<CachedConnector,CachedConnectorFactory> getMasterServers() {
        return masterServers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MasterUserService">
    static class CachedMasterUserService extends CachedService<UserId,MasterUser> implements MasterUserService<CachedConnector,CachedConnectorFactory> {
        CachedMasterUserService(CachedConnector connector, MasterUserService<?,?> wrapped) {
            super(connector, UserId.class, MasterUser.class, wrapped);
        }
    }
    final CachedMasterUserService masterUsers;
    @Override
    public MasterUserService<CachedConnector,CachedConnectorFactory> getMasterUsers() {
        return masterUsers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MonthlyChargeService">
    // TODO: final CachedMonthlyChargeService monthlyCharges;
    // TODO: public MonthlyChargeService<CachedConnector,CachedConnectorFactory> getMonthlyCharges();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MySQLDatabaseService">
    static class CachedMySQLDatabaseService extends CachedService<Integer,MySQLDatabase> implements MySQLDatabaseService<CachedConnector,CachedConnectorFactory> {
        CachedMySQLDatabaseService(CachedConnector connector, MySQLDatabaseService<?,?> wrapped) {
            super(connector, Integer.class, MySQLDatabase.class, wrapped);
        }
    }
    final CachedMySQLDatabaseService mysqlDatabases;
    @Override
    public MySQLDatabaseService<CachedConnector,CachedConnectorFactory> getMysqlDatabases() {
        return mysqlDatabases;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MySQLDBUserService">
    static class CachedMySQLDBUserService extends CachedService<Integer,MySQLDBUser> implements MySQLDBUserService<CachedConnector,CachedConnectorFactory> {
        CachedMySQLDBUserService(CachedConnector connector, MySQLDBUserService<?,?> wrapped) {
            super(connector, Integer.class, MySQLDBUser.class, wrapped);
        }
    }
    final CachedMySQLDBUserService mysqlDBUsers;
    @Override
    public MySQLDBUserService<CachedConnector,CachedConnectorFactory> getMysqlDBUsers() {
        return mysqlDBUsers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MySQLServerService">
    static class CachedMySQLServerService extends CachedService<Integer,MySQLServer> implements MySQLServerService<CachedConnector,CachedConnectorFactory> {
        CachedMySQLServerService(CachedConnector connector, MySQLServerService<?,?> wrapped) {
            super(connector, Integer.class, MySQLServer.class, wrapped);
        }
    }
    final CachedMySQLServerService mysqlServers;
    @Override
    public MySQLServerService<CachedConnector,CachedConnectorFactory> getMysqlServers() {
        return mysqlServers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MySQLUserService">
    static class CachedMySQLUserService extends CachedService<Integer,MySQLUser> implements MySQLUserService<CachedConnector,CachedConnectorFactory> {
        CachedMySQLUserService(CachedConnector connector, MySQLUserService<?,?> wrapped) {
            super(connector, Integer.class, MySQLUser.class, wrapped);
        }
    }
    final CachedMySQLUserService mysqlUsers;
    @Override
    public MySQLUserService<CachedConnector,CachedConnectorFactory> getMysqlUsers() {
        return mysqlUsers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="NetBindService">
    static class CachedNetBindService extends CachedService<Integer,NetBind> implements NetBindService<CachedConnector,CachedConnectorFactory> {
        CachedNetBindService(CachedConnector connector, NetBindService<?,?> wrapped) {
            super(connector, Integer.class, NetBind.class, wrapped);
        }
    }
    final CachedNetBindService netBinds;
    @Override
    public NetBindService<CachedConnector,CachedConnectorFactory> getNetBinds() {
        return netBinds;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="NetDeviceIDService">
    static class CachedNetDeviceIDService extends CachedService<String,NetDeviceID> implements NetDeviceIDService<CachedConnector,CachedConnectorFactory> {
        CachedNetDeviceIDService(CachedConnector connector, NetDeviceIDService<?,?> wrapped) {
            super(connector, String.class, NetDeviceID.class, wrapped);
        }
    }
    final CachedNetDeviceIDService netDeviceIDs;
    @Override
    public NetDeviceIDService<CachedConnector,CachedConnectorFactory> getNetDeviceIDs() {
        return netDeviceIDs;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="NetDeviceService">
    static class CachedNetDeviceService extends CachedService<Integer,NetDevice> implements NetDeviceService<CachedConnector,CachedConnectorFactory> {
        CachedNetDeviceService(CachedConnector connector, NetDeviceService<?,?> wrapped) {
            super(connector, Integer.class, NetDevice.class, wrapped);
        }
    }
    final CachedNetDeviceService netDevices;
    @Override
    public NetDeviceService<CachedConnector,CachedConnectorFactory> getNetDevices() {
        return netDevices;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="NetProtocolService">
    static class CachedNetProtocolService extends CachedService<String,NetProtocol> implements NetProtocolService<CachedConnector,CachedConnectorFactory> {
        CachedNetProtocolService(CachedConnector connector, NetProtocolService<?,?> wrapped) {
            super(connector, String.class, NetProtocol.class, wrapped);
        }
    }
    final CachedNetProtocolService netProtocols;
    @Override
    public NetProtocolService<CachedConnector,CachedConnectorFactory> getNetProtocols() {
        return netProtocols;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="NetTcpRedirectService">
    static class CachedNetTcpRedirectService extends CachedService<Integer,NetTcpRedirect> implements NetTcpRedirectService<CachedConnector,CachedConnectorFactory> {
        CachedNetTcpRedirectService(CachedConnector connector, NetTcpRedirectService<?,?> wrapped) {
            super(connector, Integer.class, NetTcpRedirect.class, wrapped);
        }
    }
    final CachedNetTcpRedirectService netTcpRedirects;
    @Override
    public NetTcpRedirectService<CachedConnector,CachedConnectorFactory> getNetTcpRedirects() {
        return netTcpRedirects;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="NoticeLogService">
    // TODO: final CachedNoticeLogService noticeLogs;
    // TODO: public NoticeLogService<CachedConnector,CachedConnectorFactory> getNoticeLogs();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="NoticeTypeService">
    static class CachedNoticeTypeService extends CachedService<String,NoticeType> implements NoticeTypeService<CachedConnector,CachedConnectorFactory> {
        CachedNoticeTypeService(CachedConnector connector, NoticeTypeService<?,?> wrapped) {
            super(connector, String.class, NoticeType.class, wrapped);
        }
    }
    final CachedNoticeTypeService noticeTypes;
    @Override
    public NoticeTypeService<CachedConnector,CachedConnectorFactory> getNoticeTypes() {
        return noticeTypes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="OperatingSystemVersionService">
    static class CachedOperatingSystemVersionService extends CachedService<Integer,OperatingSystemVersion> implements OperatingSystemVersionService<CachedConnector,CachedConnectorFactory> {
        CachedOperatingSystemVersionService(CachedConnector connector, OperatingSystemVersionService<?,?> wrapped) {
            super(connector, Integer.class, OperatingSystemVersion.class, wrapped);
        }
    }
    final CachedOperatingSystemVersionService operatingSystemVersions;
    @Override
    public OperatingSystemVersionService<CachedConnector,CachedConnectorFactory> getOperatingSystemVersions() {
        return operatingSystemVersions;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="OperatingSystemService">
    static class CachedOperatingSystemService extends CachedService<String,OperatingSystem> implements OperatingSystemService<CachedConnector,CachedConnectorFactory> {
        CachedOperatingSystemService(CachedConnector connector, OperatingSystemService<?,?> wrapped) {
            super(connector, String.class, OperatingSystem.class, wrapped);
        }
    }
    final CachedOperatingSystemService operatingSystems;
    @Override
    public OperatingSystemService<CachedConnector,CachedConnectorFactory> getOperatingSystems() {
        return operatingSystems;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PackageCategoryService">
    static class CachedPackageCategoryService extends CachedService<String,PackageCategory> implements PackageCategoryService<CachedConnector,CachedConnectorFactory> {
        CachedPackageCategoryService(CachedConnector connector, PackageCategoryService<?,?> wrapped) {
            super(connector, String.class, PackageCategory.class, wrapped);
        }
    }
    final CachedPackageCategoryService packageCategories;
    @Override
    public PackageCategoryService<CachedConnector,CachedConnectorFactory> getPackageCategories() {
        return packageCategories;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PackageDefinitionBusinessService">
    static class CachedPackageDefinitionBusinessService extends CachedService<Integer,PackageDefinitionBusiness> implements PackageDefinitionBusinessService<CachedConnector,CachedConnectorFactory> {
        CachedPackageDefinitionBusinessService(CachedConnector connector, PackageDefinitionBusinessService<?,?> wrapped) {
            super(connector, Integer.class, PackageDefinitionBusiness.class, wrapped);
        }
    }
    final CachedPackageDefinitionBusinessService packageDefinitionBusinesses;
    @Override
    public PackageDefinitionBusinessService<CachedConnector,CachedConnectorFactory> getPackageDefinitionBusinesses() {
        return packageDefinitionBusinesses;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PackageDefinitionLimitService">
    static class CachedPackageDefinitionLimitService extends CachedService<Integer,PackageDefinitionLimit> implements PackageDefinitionLimitService<CachedConnector,CachedConnectorFactory> {
        CachedPackageDefinitionLimitService(CachedConnector connector, PackageDefinitionLimitService<?,?> wrapped) {
            super(connector, Integer.class, PackageDefinitionLimit.class, wrapped);
        }
    }
    final CachedPackageDefinitionLimitService packageDefinitionLimits;
    @Override
    public PackageDefinitionLimitService<CachedConnector,CachedConnectorFactory> getPackageDefinitionLimits() {
        return packageDefinitionLimits;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PackageDefinitionService">
    static class CachedPackageDefinitionService extends CachedService<Integer,PackageDefinition> implements PackageDefinitionService<CachedConnector,CachedConnectorFactory> {
        CachedPackageDefinitionService(CachedConnector connector, PackageDefinitionService<?,?> wrapped) {
            super(connector, Integer.class, PackageDefinition.class, wrapped);
        }
    }
    final CachedPackageDefinitionService packageDefinitions;
    @Override
    public PackageDefinitionService<CachedConnector,CachedConnectorFactory> getPackageDefinitions() {
        return packageDefinitions;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PaymentTypeService">
    static class CachedPaymentTypeService extends CachedService<String,PaymentType> implements PaymentTypeService<CachedConnector,CachedConnectorFactory> {
        CachedPaymentTypeService(CachedConnector connector, PaymentTypeService<?,?> wrapped) {
            super(connector, String.class, PaymentType.class, wrapped);
        }
    }
    final CachedPaymentTypeService paymentTypes;
    @Override
    public PaymentTypeService<CachedConnector,CachedConnectorFactory> getPaymentTypes() {
        return paymentTypes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PhysicalServerService">
    // TODO: final CachedPhysicalServerService physicalServers;
    // TODO: public PhysicalServerService<CachedConnector,CachedConnectorFactory> getPhysicalServers();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PostgresDatabaseService">
    static class CachedPostgresDatabaseService extends CachedService<Integer,PostgresDatabase> implements PostgresDatabaseService<CachedConnector,CachedConnectorFactory> {
        CachedPostgresDatabaseService(CachedConnector connector, PostgresDatabaseService<?,?> wrapped) {
            super(connector, Integer.class, PostgresDatabase.class, wrapped);
        }
    }
    final CachedPostgresDatabaseService postgresDatabases;
    @Override
    public PostgresDatabaseService<CachedConnector,CachedConnectorFactory> getPostgresDatabases() {
        return postgresDatabases;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PostgresEncodingService">
    static class CachedPostgresEncodingService extends CachedService<Integer,PostgresEncoding> implements PostgresEncodingService<CachedConnector,CachedConnectorFactory> {
        CachedPostgresEncodingService(CachedConnector connector, PostgresEncodingService<?,?> wrapped) {
            super(connector, Integer.class, PostgresEncoding.class, wrapped);
        }
    }
    final CachedPostgresEncodingService postgresEncodings;
    @Override
    public PostgresEncodingService<CachedConnector,CachedConnectorFactory> getPostgresEncodings() {
        return postgresEncodings;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PostgresServerService">
    static class CachedPostgresServerService extends CachedService<Integer,PostgresServer> implements PostgresServerService<CachedConnector,CachedConnectorFactory> {
        CachedPostgresServerService(CachedConnector connector, PostgresServerService<?,?> wrapped) {
            super(connector, Integer.class, PostgresServer.class, wrapped);
        }
    }
    final CachedPostgresServerService postgresServers;
    @Override
    public PostgresServerService<CachedConnector,CachedConnectorFactory> getPostgresServers() {
        return postgresServers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PostgresUserService">
    static class CachedPostgresUserService extends CachedService<Integer,PostgresUser> implements PostgresUserService<CachedConnector,CachedConnectorFactory> {
        CachedPostgresUserService(CachedConnector connector, PostgresUserService<?,?> wrapped) {
            super(connector, Integer.class, PostgresUser.class, wrapped);
        }
    }
    final CachedPostgresUserService postgresUsers;
    @Override
    public PostgresUserService<CachedConnector,CachedConnectorFactory> getPostgresUsers() {
        return postgresUsers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PostgresVersionService">
    static class CachedPostgresVersionService extends CachedService<Integer,PostgresVersion> implements PostgresVersionService<CachedConnector,CachedConnectorFactory> {
        CachedPostgresVersionService(CachedConnector connector, PostgresVersionService<?,?> wrapped) {
            super(connector, Integer.class, PostgresVersion.class, wrapped);
        }
    }
    final CachedPostgresVersionService postgresVersions;
    @Override
    public PostgresVersionService<CachedConnector,CachedConnectorFactory> getPostgresVersions() {
        return postgresVersions;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PrivateFtpServerService">
    static class CachedPrivateFtpServerService extends CachedService<Integer,PrivateFtpServer> implements PrivateFtpServerService<CachedConnector,CachedConnectorFactory> {
        CachedPrivateFtpServerService(CachedConnector connector, PrivateFtpServerService<?,?> wrapped) {
            super(connector, Integer.class, PrivateFtpServer.class, wrapped);
        }
    }
    final CachedPrivateFtpServerService privateFtpServers;
    @Override
    public PrivateFtpServerService<CachedConnector,CachedConnectorFactory> getPrivateFtpServers() {
        return privateFtpServers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ProcessorTypeService">
    static class CachedProcessorTypeService extends CachedService<String,ProcessorType> implements ProcessorTypeService<CachedConnector,CachedConnectorFactory> {
        CachedProcessorTypeService(CachedConnector connector, ProcessorTypeService<?,?> wrapped) {
            super(connector, String.class, ProcessorType.class, wrapped);
        }
    }
    final CachedProcessorTypeService processorTypes;
    @Override
    public ProcessorTypeService<CachedConnector,CachedConnectorFactory> getProcessorTypes() {
        return processorTypes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ProtocolService">
    static class CachedProtocolService extends CachedService<String,Protocol> implements ProtocolService<CachedConnector,CachedConnectorFactory> {
        CachedProtocolService(CachedConnector connector, ProtocolService<?,?> wrapped) {
            super(connector, String.class, Protocol.class, wrapped);
        }
    }
    final CachedProtocolService protocols;
    @Override
    public ProtocolService<CachedConnector,CachedConnectorFactory> getProtocols() {
        return protocols;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="RackService">
    // TODO: final CachedRackService racks;
    // TODO: public RackService<CachedConnector,CachedConnectorFactory> getRacks();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ResellerService">
    static class CachedResellerService extends CachedService<AccountingCode,Reseller> implements ResellerService<CachedConnector,CachedConnectorFactory> {
        CachedResellerService(CachedConnector connector, ResellerService<?,?> wrapped) {
            super(connector, AccountingCode.class, Reseller.class, wrapped);
        }
    }
    final CachedResellerService resellers;
    @Override
    public ResellerService<CachedConnector,CachedConnectorFactory> getResellers() {
        return resellers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ResourceTypeService">
    static class CachedResourceTypeService extends CachedService<String,ResourceType> implements ResourceTypeService<CachedConnector,CachedConnectorFactory> {
        CachedResourceTypeService(CachedConnector connector, ResourceTypeService<?,?> wrapped) {
            super(connector, String.class, ResourceType.class, wrapped);
        }
    }
    final CachedResourceTypeService resourceTypes;
    @Override
    public ResourceTypeService<CachedConnector,CachedConnectorFactory> getResourceTypes() {
        return resourceTypes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ResourceService">
    final ResourceService<CachedConnector,CachedConnectorFactory> resources;
    @Override
    public ResourceService<CachedConnector,CachedConnectorFactory> getResources() {
        return resources;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ServerFarmService">
    static class CachedServerFarmService extends CachedService<DomainLabel,ServerFarm> implements ServerFarmService<CachedConnector,CachedConnectorFactory> {
        CachedServerFarmService(CachedConnector connector, ServerFarmService<?,?> wrapped) {
            super(connector, DomainLabel.class, ServerFarm.class, wrapped);
        }
    }
    final CachedServerFarmService serverFarms;
    @Override
    public ServerFarmService<CachedConnector,CachedConnectorFactory> getServerFarms() {
        return serverFarms;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ServerResourceService">
    final ServerResourceService<CachedConnector,CachedConnectorFactory> serverResources;
    @Override
    public ServerResourceService<CachedConnector,CachedConnectorFactory> getServerResources() {
        return serverResources;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ServerService">
    static class CachedServerService extends CachedService<Integer,Server> implements ServerService<CachedConnector,CachedConnectorFactory> {
        CachedServerService(CachedConnector connector, ServerService<?,?> wrapped) {
            super(connector, Integer.class, Server.class, wrapped);
        }
    }
    final CachedServerService servers;
    @Override
    public ServerService<CachedConnector,CachedConnectorFactory> getServers() {
        return servers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ShellService">
    static class CachedShellService extends CachedService<UnixPath,Shell> implements ShellService<CachedConnector,CachedConnectorFactory> {
        CachedShellService(CachedConnector connector, ShellService<?,?> wrapped) {
            super(connector, UnixPath.class, Shell.class, wrapped);
        }
    }
    final CachedShellService shells;
    @Override
    public ShellService<CachedConnector,CachedConnectorFactory> getShells() {
        return shells;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="SignupRequestOptionService">
    // TODO: final CachedSignupRequestOptionService signupRequestOptions;
    // TODO: public SignupRequestOptionService<CachedConnector,CachedConnectorFactory> getSignupRequestOptions();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="SignupRequestService">
    // TODO: final CachedSignupRequestService signupRequests;
    // TODO: public SignupRequestService<CachedConnector,CachedConnectorFactory> getSignupRequests();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="SpamEmailMessageService">
    // TODO: final CachedSpamEmailMessageService spamEmailMessages;
    // TODO: public SpamEmailMessageService<CachedConnector,CachedConnectorFactory> getSpamEmailMessages();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="SystemEmailAliasService">
    // TODO: final CachedSystemEmailAliasService systemEmailAliass;
    // TODO: public SystemEmailAliasService<CachedConnector,CachedConnectorFactory> getSystemEmailAliases();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TechnologyService">
    final class CachedTechnologyService extends CachedService<Integer,Technology> implements TechnologyService<CachedConnector,CachedConnectorFactory> {
        CachedTechnologyService(CachedConnector connector, TechnologyService<?,?> wrapped) {
            super(connector, Integer.class, Technology.class, wrapped);
        }
    }
    final CachedTechnologyService technologies;
    @Override
    public TechnologyService<CachedConnector,CachedConnectorFactory> getTechnologies() {
        return technologies;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TechnologyClassService">
    static class CachedTechnologyClassService extends CachedService<String,TechnologyClass> implements TechnologyClassService<CachedConnector,CachedConnectorFactory> {
        CachedTechnologyClassService(CachedConnector connector, TechnologyClassService<?,?> wrapped) {
            super(connector, String.class, TechnologyClass.class, wrapped);
        }
    }
    final CachedTechnologyClassService technologyClasses;
    @Override
    public TechnologyClassService<CachedConnector,CachedConnectorFactory> getTechnologyClasses() {
        return technologyClasses;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TechnologyNameService">
    static class CachedTechnologyNameService extends CachedService<String,TechnologyName> implements TechnologyNameService<CachedConnector,CachedConnectorFactory> {
        CachedTechnologyNameService(CachedConnector connector, TechnologyNameService<?,?> wrapped) {
            super(connector, String.class, TechnologyName.class, wrapped);
        }
    }
    final CachedTechnologyNameService technologyNames;
    @Override
    public TechnologyNameService<CachedConnector,CachedConnectorFactory> getTechnologyNames() {
        return technologyNames;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TechnologyVersionService">
    static class CachedTechnologyVersionService extends CachedService<Integer,TechnologyVersion> implements TechnologyVersionService<CachedConnector,CachedConnectorFactory> {
        CachedTechnologyVersionService(CachedConnector connector, TechnologyVersionService<?,?> wrapped) {
            super(connector, Integer.class, TechnologyVersion.class, wrapped);
        }
    }
    final CachedTechnologyVersionService technologyVersions;
    @Override
    public TechnologyVersionService<CachedConnector,CachedConnectorFactory> getTechnologyVersions() {
        return technologyVersions;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TicketActionTypeService">
    static class CachedTicketActionTypeService extends CachedService<String,TicketActionType> implements TicketActionTypeService<CachedConnector,CachedConnectorFactory> {
        CachedTicketActionTypeService(CachedConnector connector, TicketActionTypeService<?,?> wrapped) {
            super(connector, String.class, TicketActionType.class, wrapped);
        }
    }
    final CachedTicketActionTypeService ticketActionTypes;
    @Override
    public TicketActionTypeService<CachedConnector,CachedConnectorFactory> getTicketActionTypes() {
        return ticketActionTypes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TicketActionService">
    static class CachedTicketActionService extends CachedService<Integer,TicketAction> implements TicketActionService<CachedConnector,CachedConnectorFactory> {
        CachedTicketActionService(CachedConnector connector, TicketActionService<?,?> wrapped) {
            super(connector, Integer.class, TicketAction.class, wrapped);
        }
    }
    final CachedTicketActionService ticketActions;
    @Override
    public TicketActionService<CachedConnector,CachedConnectorFactory> getTicketActions() {
        return ticketActions;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TicketAssignmentService">
    static class CachedTicketAssignmentService extends CachedService<Integer,TicketAssignment> implements TicketAssignmentService<CachedConnector,CachedConnectorFactory> {
        CachedTicketAssignmentService(CachedConnector connector, TicketAssignmentService<?,?> wrapped) {
            super(connector, Integer.class, TicketAssignment.class, wrapped);
        }
    }
    final CachedTicketAssignmentService ticketAssignments;
    @Override
    public TicketAssignmentService<CachedConnector,CachedConnectorFactory> getTicketAssignments() {
        return ticketAssignments;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TicketBrandCategoryService">
    // TODO: final CachedTicketBrandCategoryService ticketBrandCategories;
    // TODO: public TicketBrandCategoryService<CachedConnector,CachedConnectorFactory> getTicketBrandCategories();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TicketCategoryService">
    static class CachedTicketCategoryService extends CachedService<Integer,TicketCategory> implements TicketCategoryService<CachedConnector,CachedConnectorFactory> {
        CachedTicketCategoryService(CachedConnector connector, TicketCategoryService<?,?> wrapped) {
            super(connector, Integer.class, TicketCategory.class, wrapped);
        }
    }
    final CachedTicketCategoryService ticketCategories;
    @Override
    public TicketCategoryService<CachedConnector,CachedConnectorFactory> getTicketCategories() {
        return ticketCategories;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TicketPriorityService">
    static class CachedTicketPriorityService extends CachedService<String,TicketPriority> implements TicketPriorityService<CachedConnector,CachedConnectorFactory> {
        CachedTicketPriorityService(CachedConnector connector, TicketPriorityService<?,?> wrapped) {
            super(connector, String.class, TicketPriority.class, wrapped);
        }
    }
    final CachedTicketPriorityService ticketPriorities;
    @Override
    public TicketPriorityService<CachedConnector,CachedConnectorFactory> getTicketPriorities() {
        return ticketPriorities;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TicketStatusService">
    static class CachedTicketStatusService extends CachedService<String,TicketStatus> implements TicketStatusService<CachedConnector,CachedConnectorFactory> {
        CachedTicketStatusService(CachedConnector connector, TicketStatusService<?,?> wrapped) {
            super(connector, String.class, TicketStatus.class, wrapped);
        }
    }
    final CachedTicketStatusService ticketStatuses;
    @Override
    public TicketStatusService<CachedConnector,CachedConnectorFactory> getTicketStatuses() {
        return ticketStatuses;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TicketTypeService">
    static class CachedTicketTypeService extends CachedService<String,TicketType> implements TicketTypeService<CachedConnector,CachedConnectorFactory> {
        CachedTicketTypeService(CachedConnector connector, TicketTypeService<?,?> wrapped) {
            super(connector, String.class, TicketType.class, wrapped);
        }
    }
    final CachedTicketTypeService ticketTypes;
    @Override
    public TicketTypeService<CachedConnector,CachedConnectorFactory> getTicketTypes() {
        return ticketTypes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TicketService">
    static class CachedTicketService extends CachedService<Integer,Ticket> implements TicketService<CachedConnector,CachedConnectorFactory> {
        CachedTicketService(CachedConnector connector, TicketService<?,?> wrapped) {
            super(connector, Integer.class, Ticket.class, wrapped);
        }
    }
    final CachedTicketService tickets;
    @Override
    public TicketService<CachedConnector,CachedConnectorFactory> getTickets() {
        return tickets;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TimeZoneService">
    static class CachedTimeZoneService extends CachedService<String,TimeZone> implements TimeZoneService<CachedConnector,CachedConnectorFactory> {
        CachedTimeZoneService(CachedConnector connector, TimeZoneService<?,?> wrapped) {
            super(connector, String.class, TimeZone.class, wrapped);
        }
    }
    final CachedTimeZoneService timeZones;
    @Override
    public TimeZoneService<CachedConnector,CachedConnectorFactory> getTimeZones() {
        return timeZones;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TransactionTypeService">
    static class CachedTransactionTypeService extends CachedService<String,TransactionType> implements TransactionTypeService<CachedConnector,CachedConnectorFactory> {
        CachedTransactionTypeService(CachedConnector connector, TransactionTypeService<?,?> wrapped) {
            super(connector, String.class, TransactionType.class, wrapped);
        }
    }
    final CachedTransactionTypeService transactionTypes;
    @Override
    public TransactionTypeService<CachedConnector,CachedConnectorFactory> getTransactionTypes() {
        return transactionTypes;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TransactionService">
    static class CachedTransactionService extends CachedService<Integer,Transaction> implements TransactionService<CachedConnector,CachedConnectorFactory> {
        CachedTransactionService(CachedConnector connector, TransactionService<?,?> wrapped) {
            super(connector, Integer.class, Transaction.class, wrapped);
        }
    }
    final CachedTransactionService transactions;
    @Override
    public TransactionService<CachedConnector,CachedConnectorFactory> getTransactions() {
        return transactions;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="UsernameService">
    static class CachedUsernameService extends CachedService<UserId,Username> implements UsernameService<CachedConnector,CachedConnectorFactory> {
        CachedUsernameService(CachedConnector connector, UsernameService<?,?> wrapped) {
            super(connector, UserId.class, Username.class, wrapped);
        }
    }
    final CachedUsernameService usernames;
    @Override
    public UsernameService<CachedConnector,CachedConnectorFactory> getUsernames() {
        return usernames;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="VirtualDiskService">
    // TODO: final CachedVirtualDiskService virtualDisks;
    // TODO: public VirtualDiskService<CachedConnector,CachedConnectorFactory> getVirtualDisks();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="VirtualServerService">
    static class CachedVirtualServerService extends CachedService<Integer,VirtualServer> implements VirtualServerService<CachedConnector,CachedConnectorFactory> {
        CachedVirtualServerService(CachedConnector connector, VirtualServerService<?,?> wrapped) {
            super(connector, Integer.class, VirtualServer.class, wrapped);
        }
    }
    final CachedVirtualServerService virtualServers;
    @Override
    public VirtualServerService<CachedConnector,CachedConnectorFactory> getVirtualServers() {
        return virtualServers;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="WhoisHistoryService">
    // TODO: final CachedWhoisHistoryService whoisHistories;
    // TODO: public WhoisHistoryService<CachedConnector,CachedConnectorFactory> getWhoisHistory();
    // </editor-fold>
}
