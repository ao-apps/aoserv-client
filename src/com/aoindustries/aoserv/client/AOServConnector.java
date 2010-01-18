package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.command.AOServCommand;
import com.aoindustries.aoserv.client.validator.UserId;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.Map;

/**
 * <p>
 * An <code>AOServConnector</code> is the main interface for all data-related accesses and updates.  This
 * is RMI-compatible to support client/server decoupling of connectors.  Any
 * single connector-implementation-specific errors, like SQLException or IOException
 * will be wrapped in RemoteException.
 * </p>
 * <p>
 * Each user will have their own instance of a connector, and each connector will have its own
 * unique connector id.
 * </p>
 * @see  AOServConnectorFactory
 *
 * @author  AO Industries, Inc.
 */
public interface AOServConnector<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends Remote {

    /**
     * Gets the factory that was used to create this connector.
     */
    F getFactory() throws RemoteException;

    /**
     * Gets the user's locale for this connector.  Defaults to the locale
     * provided to the factory getConnector method.
     *
     * @see  #setLocale
     */
    Locale getLocale() throws RemoteException;

    /**
     * Sets the user's locale for this connector.
     */
    void setLocale(Locale locale) throws RemoteException;

    /**
     * Gets the username they are behaving as.
     */
    // TODO: Make be a UserId
    UserId getConnectAs() throws RemoteException;

    /**
     * Gets the <code>BusinessAdministrator</code> who is logged in using
     * this <code>AOServConnector</code>.  Each username and password pair
     * resolves to an always-accessible <code>BusinessAdministrator</code>.
     * Details about permissions and capabilities may be obtained from the
     * <code>BusinessAdministrator</code>.
     *
     * @return  the <code>BusinessAdministrator</code> who is logged in
     */
    BusinessAdministrator getThisBusinessAdministrator() throws RemoteException;

    /**
     * Gets the username they logged-in as.
     */
    UserId getAuthenticateAs() throws RemoteException;

    /**
     * Gets the password used to login.
     */
    String getPassword() throws RemoteException;

    /**
     * Executes an aosh command and returns its result.
     */
    <R> R executeCommand(AOServCommand<R> command, boolean isInteractive) throws RemoteException;

    /**
     * Gets an unmodifiable map of all of the services in the system.
     *
     * @return  a <code>Map</code> containing all the services.  Each
     *          service is at an index corresponding to its unique ID.
     */
    Map<ServiceName,AOServService<C,F,?,?>> getServices() throws RemoteException;

    AOServerDaemonHostService<C,F> getAoServerDaemonHosts() throws RemoteException;
    AOServerResourceService<C,F> getAoServerResources() throws RemoteException;
    AOServerService<C,F> getAoServers() throws RemoteException;
    AOServPermissionService<C,F> getAoservPermissions() throws RemoteException;
    /* TODO
    AOSHCommandService<C,F> getAoshCommands() throws RemoteException;
     */
    ArchitectureService<C,F> getArchitectures() throws RemoteException;
    BackupPartitionService<C,F> getBackupPartitions() throws RemoteException;
    BackupRetentionService<C,F> getBackupRetentions() throws RemoteException;
    /*
    BankAccountService<C,F> getBankAccounts() throws RemoteException;
    BankTransactionTypeService<C,F> getBankTransactionTypes() throws RemoteException;
    BankTransactionService<C,F> getBankTransactions() throws RemoteException;
    BankService<C,F> getBanks() throws RemoteException;
    BlackholeEmailAddressService<C,F> getBlackholeEmailAddresses() throws RemoteException;
     */
    BrandService<C,F> getBrands() throws RemoteException;
    BusinessAdministratorService<C,F> getBusinessAdministrators() throws RemoteException;
    /*
    BusinessAdministratorPermissionService<C,F> getBusinessAdministratorPermissions() throws RemoteException;
    BusinessProfileService<C,F> getBusinessProfiles() throws RemoteException;
     */
    BusinessService<C,F> getBusinesses() throws RemoteException;
    BusinessServerService<C,F> getBusinessServers() throws RemoteException;
    CountryCodeService<C,F> getCountryCodes() throws RemoteException;
    /*
    CreditCardProcessorService<C,F> getCreditCardProcessors() throws RemoteException;
    CreditCardTransactionService<C,F> getCreditCardTransactions() throws RemoteException;
    CreditCardService<C,F> getCreditCards() throws RemoteException;
     */
    CvsRepositoryService<C,F> getCvsRepositories() throws RemoteException;
    DisableLogService<C,F> getDisableLogs() throws RemoteException;
    /*
    DistroFileTypeService<C,F> getDistroFileTypes() throws RemoteException;
    DistroFileService<C,F> getDistroFiles() throws RemoteException;
     */
    DnsRecordService<C,F> getDnsRecords() throws RemoteException;
    DnsTldService<C,F> getDnsTlds() throws RemoteException;
    DnsTypeService<C,F> getDnsTypes() throws RemoteException;
    DnsZoneService<C,F> getDnsZones() throws RemoteException;
    /* TODO
    EmailAddressService<C,F> getEmailAddresses() throws RemoteException;
    EmailAttachmentBlockService<C,F> getEmailAttachmentBlocks() throws RemoteException;
    EmailAttachmentTypeService<C,F> getEmailAttachmentTypes() throws RemoteException;
    EmailDomainService<C,F> getEmailDomains() throws RemoteException;
    EmailForwardingService<C,F> getEmailForwardings() throws RemoteException;
     */
    EmailInboxService<C,F> getEmailInboxes() throws RemoteException;
    /* TODO
    EmailListAddressService<C,F> getEmailListAddresses() throws RemoteException;
    EmailListService<C,F> getEmailLists() throws RemoteException;
    EmailPipeAddressService<C,F> getEmailPipeAddresses() throws RemoteException;
    EmailPipeService<C,F> getEmailPipes() throws RemoteException;
    EmailSmtpRelayTypeService<C,F> getEmailSmtpRelayTypes() throws RemoteException;
    EmailSmtpRelayService<C,F> getEmailSmtpRelays() throws RemoteException;
    EmailSmtpSmartHostDomainService<C,F> getEmailSmtpSmartHostDomains() throws RemoteException;
    EmailSmtpSmartHostService<C,F> getEmailSmtpSmartHosts() throws RemoteException;
    EmailSpamAssassinIntegrationModeService<C,F> getEmailSpamAssassinIntegrationModes() throws RemoteException;
    EncryptionKeyService<C,F> getEncryptionKeys() throws RemoteException;
    ExpenseCategoryService<C,F> getExpenseCategories() throws RemoteException;
     */
    FailoverFileLogService<C,F> getFailoverFileLogs() throws RemoteException;
    FailoverFileReplicationService<C,F> getFailoverFileReplications() throws RemoteException;
    FailoverFileScheduleService<C,F> getFailoverFileSchedules() throws RemoteException;
    FailoverMySQLReplicationService<C,F> getFailoverMySQLReplications() throws RemoteException;
    FileBackupSettingService<C,F> getFileBackupSettings() throws RemoteException;
    FtpGuestUserService<C,F> getFtpGuestUsers() throws RemoteException;
    GroupNameService<C,F> getGroupNames() throws RemoteException;
    /* TODO
    HttpdBindService<C,F> getHttpdBinds() throws RemoteException;
    HttpdJBossSiteService<C,F> getHttpdJBossSites() throws RemoteException;
    HttpdJBossVersionService<C,F> getHttpdJBossVersions() throws RemoteException;
    HttpdJKCodeService<C,F> getHttpdJKCodes() throws RemoteException;
    HttpdJKProtocolService<C,F> getHttpdJKProtocols() throws RemoteException;
     */
    HttpdServerService<C,F> getHttpdServers() throws RemoteException;
    /* TODO
    HttpdSharedTomcatService<C,F> getHttpdSharedTomcats() throws RemoteException;
    HttpdSiteAuthenticatedLocationService<C,F> getHttpdSiteAuthenticatedLocations() throws RemoteException;
    HttpdSiteBindService<C,F> getHttpdSiteBinds() throws RemoteException;
    HttpdSiteURLService<C,F> getHttpdSiteURLs() throws RemoteException;
     */
    HttpdSiteService<C,F> getHttpdSites() throws RemoteException;
    // TODO: HttpdStaticSiteService<C,F> getHttpdStaticSites() throws RemoteException;
    // TODO: HttpdTomcatContextService<C,F> getHttpdTomcatContexts() throws RemoteException;
    // TODO: HttpdTomcatDataSourceService<C,F> getHttpdTomcatDataSources() throws RemoteException;
    // TODO: HttpdTomcatParameterService<C,F> getHttpdTomcatParameters() throws RemoteException;
    // TODO: HttpdTomcatSiteService<C,F> getHttpdTomcatSites() throws RemoteException;
    // TODO: HttpdTomcatSharedSiteService<C,F> getHttpdTomcatSharedSites() throws RemoteException;
    // TODO: HttpdTomcatStdSiteService<C,F> getHttpdTomcatStdSites() throws RemoteException;
    // TODO: HttpdTomcatVersionService<C,F> getHttpdTomcatVersions() throws RemoteException;
    // TODO: HttpdWorkerService<C,F> getHttpdWorkers() throws RemoteException;
    IPAddressService<C,F> getIpAddresses() throws RemoteException;
    LanguageService<C,F> getLanguages() throws RemoteException;
    // TODO: LinuxAccAddressService<C,F> getLinuxAccAddresses() throws RemoteException;
    LinuxAccountGroupService<C,F> getLinuxAccountGroups() throws RemoteException;
    LinuxAccountTypeService<C,F> getLinuxAccountTypes() throws RemoteException;
    LinuxAccountService<C,F> getLinuxAccounts() throws RemoteException;
    LinuxGroupTypeService<C,F> getLinuxGroupTypes() throws RemoteException;
    LinuxGroupService<C,F> getLinuxGroups() throws RemoteException;
    /* TODO
    LinuxServerAccountService<C,F> getLinuxServerAccounts() throws RemoteException;
    LinuxServerGroupService<C,F> getLinuxServerGroups() throws RemoteException;
    MajordomoListService<C,F> getMajordomoLists() throws RemoteException;
    MajordomoServerService<C,F> getMajordomoServers() throws RemoteException;
    MajordomoVersionService<C,F> getMajordomoVersions() throws RemoteException;
     */
    MasterHostService<C,F> getMasterHosts() throws RemoteException;
    MasterServerService<C,F> getMasterServers() throws RemoteException;
    MasterUserService<C,F> getMasterUsers() throws RemoteException;
    // TODO: MonthlyChargeService<C,F> getMonthlyCharges() throws RemoteException;
    MySQLDatabaseService<C,F> getMysqlDatabases() throws RemoteException;
    MySQLDBUserService<C,F> getMysqlDBUsers() throws RemoteException;
    MySQLServerService<C,F> getMysqlServers() throws RemoteException;
    MySQLUserService<C,F> getMysqlUsers() throws RemoteException;
    NetBindService<C,F> getNetBinds() throws RemoteException;
    NetDeviceIDService<C,F> getNetDeviceIDs() throws RemoteException;
    NetDeviceService<C,F> getNetDevices() throws RemoteException;
    NetProtocolService<C,F> getNetProtocols() throws RemoteException;
    NetTcpRedirectService<C,F> getNetTcpRedirects() throws RemoteException;
    /* TODO
    NoticeLogService<C,F> getNoticeLogs() throws RemoteException;
    NoticeTypeService<C,F> getNoticeTypes() throws RemoteException;
    */
    OperatingSystemVersionService<C,F> getOperatingSystemVersions() throws RemoteException;
    OperatingSystemService<C,F> getOperatingSystems() throws RemoteException;
    PackageCategoryService<C,F> getPackageCategories() throws RemoteException;
    /*
    PackageDefinitionLimitService<C,F> getPackageDefinitionLimits() throws RemoteException;
    PackageDefinitionService<C,F> getPackageDefinitions() throws RemoteException;
    PaymentTypeService<C,F> getPaymentTypes() throws RemoteException;
    PhysicalServerService<C,F> getPhysicalServers() throws RemoteException;
     */
    PostgresDatabaseService<C,F> getPostgresDatabases() throws RemoteException;
    PostgresEncodingService<C,F> getPostgresEncodings() throws RemoteException;
    PostgresServerService<C,F> getPostgresServers() throws RemoteException;
    PostgresUserService<C,F> getPostgresUsers() throws RemoteException;
    PostgresVersionService<C,F> getPostgresVersions() throws RemoteException;
    // TODO: PrivateFTPServerService<C,F> getPrivateFTPServers() throws RemoteException;
    // TODO: ProcessorTypeService<C,F> getProcessorTypes() throws RemoteException;
    ProtocolService<C,F> getProtocols() throws RemoteException;
    /* TODO
    RackService<C,F> getRacks() throws RemoteException;
    */
    ResellerService<C,F> getResellers() throws RemoteException;
    ResourceTypeService<C,F> getResourceTypes() throws RemoteException;
    ResourceService<C,F> getResources() throws RemoteException;
    ServerFarmService<C,F> getServerFarms() throws RemoteException;
    ServerResourceService<C,F> getServerResources() throws RemoteException;
    ServerService<C,F> getServers() throws RemoteException;
    ShellService<C,F> getShells() throws RemoteException;
    /* TODO
    SignupRequestOptionService<C,F> getSignupRequestOptions() throws RemoteException;
    SignupRequestService<C,F> getSignupRequests() throws RemoteException;
    SpamEmailMessageService<C,F> getSpamEmailMessages() throws RemoteException;
    SystemEmailAliasService<C,F> getSystemEmailAliases() throws RemoteException;
     */
    TechnologyService<C,F> getTechnologies() throws RemoteException;
    TechnologyClassService<C,F> getTechnologyClasses() throws RemoteException;
    TechnologyNameService<C,F> getTechnologyNames() throws RemoteException;
    TechnologyVersionService<C,F> getTechnologyVersions() throws RemoteException;
    /* TODO
    TicketActionTypeService<C,F> getTicketActionTypes() throws RemoteException;
    TicketActionService<C,F> getTicketActions() throws RemoteException;
    */
    TicketAssignmentService<C,F> getTicketAssignments() throws RemoteException;
    // TODO: TicketBrandCategoryService<C,F> getTicketBrandCategories() throws RemoteException;
    TicketCategoryService<C,F> getTicketCategories() throws RemoteException;
    TicketPriorityService<C,F> getTicketPriorities() throws RemoteException;
    TicketStatusService<C,F> getTicketStatuses() throws RemoteException;
    TicketTypeService<C,F> getTicketTypes() throws RemoteException;
    TicketService<C,F> getTickets() throws RemoteException;
    TimeZoneService<C,F> getTimeZones() throws RemoteException;
    /* TODO
    TransactionTypeService<C,F> getTransactionTypes() throws RemoteException;
    TransactionService<C,F> getTransactions() throws RemoteException;
    USStateService<C,F> getUsStates() throws RemoteException;
    */
    UsernameService<C,F> getUsernames() throws RemoteException;
    /* TODO
    VirtualDiskService<C,F> getVirtualDisks() throws RemoteException;
    VirtualServerService<C,F> getVirtualServers() throws RemoteException;
    WhoisHistoryService<C,F> getWhoisHistory() throws RemoteException;
     */

    /**
     * Clears all caches used by this connector.
     */
    /* TODO
    public void clearCaches() {
        for(AOServTable table : tables) table.clearCache();
    }

    private static final Random random = new SecureRandom();
    public static Random getRandom() {
        return random;
    }
    */
    /**
     * Manually invalidates the system caches.
     *
     * @param tableID the table ID
     * @param server the pkey of the server or <code>-1</code> for all servers
     */
    /* TODO
    public void invalidateTable(final int tableID, final int server) throws IOException, SQLException {
        requestUpdate(
            true,
            new UpdateRequest() {
                IntList tableList;
                public void writeRequest(CompressedDataOutputStream out) throws IOException {
                    out.writeCompressedInt(AOServProtocol.CommandID.INVALIDATE_TABLE.ordinal());
                    out.writeCompressedInt(tableID);
                    out.writeCompressedInt(server);
                }
                public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
                    int code=in.readByte();
                    if(code==AOServProtocol.DONE) tableList=readInvalidateList(in);
                    else {
                        AOServProtocol.checkResult(code, in);
                        throw new IOException("Unknown response code: "+code);
                    }
                }
                public void afterRelease() {
                    tablesUpdated(tableList);
                }
            }
        );
    }
    */
    /**
     * Times how long it takes to make one request with the server.
     * This will not retry and will return the first error encountered.
     *
     * @return  the connection latency in milliseconds
     */
    /* TODO
    final public int ping() throws IOException, SQLException {
        long startTime=System.currentTimeMillis();
        requestUpdate(false, AOServProtocol.CommandID.PING);
        long timeSpan=System.currentTimeMillis()-startTime;
        if(timeSpan>Integer.MAX_VALUE) return Integer.MAX_VALUE;
        return (int)timeSpan;
    }

    final public void removeFromAllTables(TableListener listener) {
        for(AOServTable table : tables) table.removeTableListener(listener);
    }

    protected final void tablesUpdated(IntList invalidateList) {
        if(invalidateList!=null) {
            int size=invalidateList.size();

            // Clear the caches
            for(int c=0;c<size;c++) {
                int tableID=invalidateList.getInt(c);
                tables.get(tableID).clearCache();
            }

            // Then send the events
            for(int c=0;c<size;c++) {
                int tableID=invalidateList.getInt(c);
                tables.get(tableID).tableUpdated();
            }
        }
    }

    @Override
    final public String toString() {
        return getClass().getName()+"?protocol="+getProtocol()+"&hostname="+hostname+"&local_ip="+local_ip+"&port="+port+"&connectAs="+connectAs+"&authenticateAs="+authenticateAs;
    }
    */
    /**
     * Is notified when a table listener is being added.
     */
    /* TODO
    void addingTableListener() {
    }
     */
}
