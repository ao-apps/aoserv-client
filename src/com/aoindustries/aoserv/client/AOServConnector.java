/*
 * Copyright 2001-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.command.*;
import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.util.graph.Edge;
import com.aoindustries.util.graph.SymmetricGraph;
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
public interface AOServConnector extends Remote {

    /**
     * Gets the factory that was used to create this connector.
     */
    AOServConnectorFactory getFactory() throws RemoteException;

    /**
     * Checks if this connector returns objects that may safely have their connector reset without copying.
     * This implies that this connector performs no caching of the objects.
     */
    boolean isAoServObjectConnectorSettable() throws RemoteException;

    /**
     * Gets the user's locale for this connector.  The remains unchanged for the
     * lifetime of the connector.  If a different locale is requested, it will
     * result in the creation of a separate connector.
     */
    Locale getLocale() throws RemoteException;

    /**
     * Gets the username they logged-in as.
     */
    UserId getUsername() throws RemoteException;

    /**
     * Gets the password used to login.
     */
    String getPassword() throws RemoteException;

    /**
     * Gets the username they are behaving as.
     */
    UserId getSwitchUser() throws RemoteException;

    /**
     * Gets the <code>BusinessAdministrator</code> who is logged-in using
     * this <code>AOServConnector</code>.  This represents the switched-to
     * user when switchUser is different than username.  Each username and password
     * pair resolves to an always-accessible <code>BusinessAdministrator</code>.
     * Details about permissions and capabilities may be obtained from the
     * <code>BusinessAdministrator</code>.
     *
     * @return  the <code>BusinessAdministrator</code> who is logged in
     */
    BusinessAdministrator getThisBusinessAdministrator() throws RemoteException;

    /**
     * Gets the name of the server this connection represents.
     */
    DomainName getDaemonServer() throws RemoteException;

    /**
     * Gets the read-only flag of this connector.  A read-only connector may only execute read-only
     * commands.
     */
    boolean isReadOnly() throws RemoteException;

    /**
     * Executes the command and retrieves the result.  If the command return
     * value is void, returns a CommandResult containing <code>null</code>.
     */
    <R> CommandResult<R> execute(RemoteCommand<R> command, boolean isInteractive) throws RemoteException;

    /**
     * Gets an unmodifiable map of all of the services in the system.
     *
     * @return  a <code>Map</code> containing all the services.  Each
     *          service is at an index corresponding to its unique ID.
     */
    Map<ServiceName,AOServService<?,?>> getServices() throws RemoteException;

    /**
     * Gets a symmetric directed acyclic graph representing the depedencies between
     * objects.
     */
    SymmetricGraph<AOServObject<?>,Edge<AOServObject<?>>,RemoteException> getDependencyGraph() throws RemoteException;

    AOServerDaemonHostService getAoServerDaemonHosts() throws RemoteException;
    AOServerResourceService getAoServerResources() throws RemoteException;
    AOServerService getAoServers() throws RemoteException;
    AOServPermissionService getAoservPermissions() throws RemoteException;
    AOServRoleService getAoservRoles() throws RemoteException;
    AOServRolePermissionService getAoservRolePermissions() throws RemoteException;
    ArchitectureService getArchitectures() throws RemoteException;
    BackupPartitionService getBackupPartitions() throws RemoteException;
    BackupRetentionService getBackupRetentions() throws RemoteException;
    BackupServerService getBackupServers() throws RemoteException;
    BankAccountService getBankAccounts() throws RemoteException;
    BankTransactionTypeService getBankTransactionTypes() throws RemoteException;
    BankTransactionService getBankTransactions() throws RemoteException;
    BankService getBanks() throws RemoteException;
    BrandService getBrands() throws RemoteException;
    BusinessAdministratorService getBusinessAdministrators() throws RemoteException;
    BusinessAdministratorRoleService getBusinessAdministratorRoles() throws RemoteException;
    BusinessProfileService getBusinessProfiles() throws RemoteException;
    BusinessService getBusinesses() throws RemoteException;
    BusinessServerService getBusinessServers() throws RemoteException;
    CountryCodeService getCountryCodes() throws RemoteException;
    CreditCardProcessorService getCreditCardProcessors() throws RemoteException;
    CreditCardTransactionService getCreditCardTransactions() throws RemoteException;
    CreditCardService getCreditCards() throws RemoteException;
    CvsRepositoryService getCvsRepositories() throws RemoteException;
    DisableLogService getDisableLogs() throws RemoteException;
    // TODO: DistroFileTypeService getDistroFileTypes() throws RemoteException;
    // TODO: DistroFileService getDistroFiles() throws RemoteException;
    DnsRecordService getDnsRecords() throws RemoteException;
    DnsTldService getDnsTlds() throws RemoteException;
    DnsTypeService getDnsTypes() throws RemoteException;
    DnsZoneService getDnsZones() throws RemoteException;
    // TODO: EmailAddressService getEmailAddresses() throws RemoteException;
    // TODO: EmailAttachmentBlockService getEmailAttachmentBlocks() throws RemoteException;
    EmailAttachmentTypeService getEmailAttachmentTypes() throws RemoteException;
    // TODO: EmailDomainService getEmailDomains() throws RemoteException;
    // TODO: EmailForwardingService getEmailForwardings() throws RemoteException;
    EmailInboxService getEmailInboxes() throws RemoteException;
    // TODO: EmailListAddressService getEmailListAddresses() throws RemoteException;
    // TODO: EmailListService getEmailLists() throws RemoteException;
    // TODO: EmailPipeAddressService getEmailPipeAddresses() throws RemoteException;
    // TODO: EmailPipeService getEmailPipes() throws RemoteException;
    EmailSmtpRelayTypeService getEmailSmtpRelayTypes() throws RemoteException;
    // TODO: EmailSmtpRelayService getEmailSmtpRelays() throws RemoteException;
    // TODO: EmailSmtpSmartHostDomainService getEmailSmtpSmartHostDomains() throws RemoteException;
    // TODO: EmailSmtpSmartHostService getEmailSmtpSmartHosts() throws RemoteException;
    EmailSpamAssassinIntegrationModeService getEmailSpamAssassinIntegrationModes() throws RemoteException;
    // TODO: EncryptionKeyService getEncryptionKeys() throws RemoteException;
    ExpenseCategoryService getExpenseCategories() throws RemoteException;
    FailoverFileLogService getFailoverFileLogs() throws RemoteException;
    FailoverFileReplicationService getFailoverFileReplications() throws RemoteException;
    FailoverFileScheduleService getFailoverFileSchedules() throws RemoteException;
    FailoverMySQLReplicationService getFailoverMySQLReplications() throws RemoteException;
    FileBackupSettingService getFileBackupSettings() throws RemoteException;
    FtpGuestUserService getFtpGuestUsers() throws RemoteException;
    GroupNameService getGroupNames() throws RemoteException;
    // TODO: HttpdBindService getHttpdBinds() throws RemoteException;
    // TODO: HttpdJBossSiteService getHttpdJBossSites() throws RemoteException;
    HttpdJBossVersionService getHttpdJBossVersions() throws RemoteException;
    HttpdJKCodeService getHttpdJKCodes() throws RemoteException;
    HttpdJKProtocolService getHttpdJKProtocols() throws RemoteException;
    HttpdServerService getHttpdServers() throws RemoteException;
    /* TODO
    HttpdSharedTomcatService getHttpdSharedTomcats() throws RemoteException;
    HttpdSiteAuthenticatedLocationService getHttpdSiteAuthenticatedLocations() throws RemoteException;
    HttpdSiteBindService getHttpdSiteBinds() throws RemoteException;
    HttpdSiteURLService getHttpdSiteURLs() throws RemoteException;
     */
    HttpdSiteService getHttpdSites() throws RemoteException;
    // TODO: HttpdStaticSiteService getHttpdStaticSites() throws RemoteException;
    // TODO: HttpdTomcatContextService getHttpdTomcatContexts() throws RemoteException;
    // TODO: HttpdTomcatDataSourceService getHttpdTomcatDataSources() throws RemoteException;
    // TODO: HttpdTomcatParameterService getHttpdTomcatParameters() throws RemoteException;
    // TODO: HttpdTomcatSiteService getHttpdTomcatSites() throws RemoteException;
    // TODO: HttpdTomcatSharedSiteService getHttpdTomcatSharedSites() throws RemoteException;
    // TODO: HttpdTomcatStdSiteService getHttpdTomcatStdSites() throws RemoteException;
    HttpdTomcatVersionService getHttpdTomcatVersions() throws RemoteException;
    // TODO: HttpdWorkerService getHttpdWorkers() throws RemoteException;
    IPAddressService getIpAddresses() throws RemoteException;
    LanguageService getLanguages() throws RemoteException;
    // TODO: LinuxAccAddressService getLinuxAccAddresses() throws RemoteException;
    LinuxAccountGroupService getLinuxAccountGroups() throws RemoteException;
    LinuxAccountTypeService getLinuxAccountTypes() throws RemoteException;
    LinuxAccountService getLinuxAccounts() throws RemoteException;
    LinuxGroupTypeService getLinuxGroupTypes() throws RemoteException;
    LinuxGroupService getLinuxGroups() throws RemoteException;
    // TODO: MajordomoListService getMajordomoLists() throws RemoteException;
    // TODO: MajordomoServerService getMajordomoServers() throws RemoteException;
    MajordomoVersionService getMajordomoVersions() throws RemoteException;
    MasterHostService getMasterHosts() throws RemoteException;
    MasterServerService getMasterServers() throws RemoteException;
    MasterUserService getMasterUsers() throws RemoteException;
    // TODO: MonthlyChargeService getMonthlyCharges() throws RemoteException;
    MySQLDatabaseService getMysqlDatabases() throws RemoteException;
    MySQLDBUserService getMysqlDBUsers() throws RemoteException;
    MySQLServerService getMysqlServers() throws RemoteException;
    MySQLUserService getMysqlUsers() throws RemoteException;
    NetBindService getNetBinds() throws RemoteException;
    NetDeviceIDService getNetDeviceIDs() throws RemoteException;
    NetDeviceService getNetDevices() throws RemoteException;
    NetProtocolService getNetProtocols() throws RemoteException;
    NetTcpRedirectService getNetTcpRedirects() throws RemoteException;
    // TODO: NoticeLogService getNoticeLogs() throws RemoteException;
    NoticeTypeService getNoticeTypes() throws RemoteException;
    OperatingSystemVersionService getOperatingSystemVersions() throws RemoteException;
    OperatingSystemService getOperatingSystems() throws RemoteException;
    PackageCategoryService getPackageCategories() throws RemoteException;
    PackageDefinitionBusinessService getPackageDefinitionBusinesses() throws RemoteException;
    PackageDefinitionLimitService getPackageDefinitionLimits() throws RemoteException;
    PackageDefinitionService getPackageDefinitions() throws RemoteException;
    PaymentTypeService getPaymentTypes() throws RemoteException;
    PhysicalServerService getPhysicalServers() throws RemoteException;
    PostgresDatabaseService getPostgresDatabases() throws RemoteException;
    PostgresEncodingService getPostgresEncodings() throws RemoteException;
    PostgresServerService getPostgresServers() throws RemoteException;
    PostgresUserService getPostgresUsers() throws RemoteException;
    PostgresVersionService getPostgresVersions() throws RemoteException;
    PrivateFtpServerService getPrivateFtpServers() throws RemoteException;
    ProcessorTypeService getProcessorTypes() throws RemoteException;
    ProtocolService getProtocols() throws RemoteException;
    RackService getRacks() throws RemoteException;
    ResellerService getResellers() throws RemoteException;
    ResourceTypeService getResourceTypes() throws RemoteException;
    ResourceService getResources() throws RemoteException;
    ServerFarmService getServerFarms() throws RemoteException;
    ServerResourceService getServerResources() throws RemoteException;
    ServerService getServers() throws RemoteException;
    ShellService getShells() throws RemoteException;
    /* TODO
    SignupRequestOptionService getSignupRequestOptions() throws RemoteException;
    SignupRequestService getSignupRequests() throws RemoteException;
    SpamEmailMessageService getSpamEmailMessages() throws RemoteException;
    SystemEmailAliasService getSystemEmailAliases() throws RemoteException;
     */
    TechnologyService getTechnologies() throws RemoteException;
    TechnologyClassService getTechnologyClasses() throws RemoteException;
    TechnologyNameService getTechnologyNames() throws RemoteException;
    TechnologyVersionService getTechnologyVersions() throws RemoteException;
    TicketActionTypeService getTicketActionTypes() throws RemoteException;
    TicketActionService getTicketActions() throws RemoteException;
    TicketAssignmentService getTicketAssignments() throws RemoteException;
    // TODO: TicketBrandCategoryService getTicketBrandCategories() throws RemoteException;
    TicketCategoryService getTicketCategories() throws RemoteException;
    TicketPriorityService getTicketPriorities() throws RemoteException;
    TicketStatusService getTicketStatuses() throws RemoteException;
    TicketTypeService getTicketTypes() throws RemoteException;
    TicketService getTickets() throws RemoteException;
    TimeZoneService getTimeZones() throws RemoteException;
    TransactionTypeService getTransactionTypes() throws RemoteException;
    TransactionService getTransactions() throws RemoteException;
    UsernameService getUsernames() throws RemoteException;
    // TODO: VirtualDiskService getVirtualDisks() throws RemoteException;
    VirtualServerService getVirtualServers() throws RemoteException;
    // TODO: WhoisHistoryService getWhoisHistory() throws RemoteException;

    /**
     * Clears all caches used by this connector.
     */
    /* TODO
    public void clearCaches() {
        for(AOServTable table : tables) table.clearCache();
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
        return getClass().getName()+"?protocol="+getProtocol()+"&hostname="+hostname+"&local_ip="+local_ip+"&port="+port+"&username="+username+"&switchUser="+switchUser;
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
