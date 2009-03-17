package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.ChainWriter;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.io.Streamable;
import com.aoindustries.table.TableListener;
import com.aoindustries.util.ErrorHandler;
import com.aoindustries.util.IntArrayList;
import com.aoindustries.util.IntList;
import com.aoindustries.util.StandardErrorHandler;
import java.io.IOException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * An <code>AOServConnector</code> provides the connection
 * between the object layer and the data.  This connection
 * may be persistant over TCP sockets, or it may be request
 * based like HTTP.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
abstract public class AOServConnector {

    /**
     * The maximum size of the master entropy pool in bytes.
     */
    public static final long MASTER_ENTROPY_POOL_SIZE=(long)64*1024*1024;

    /*private static final String[] profileTitles={
        "Method",
        "Parameter",
        "Use Count",
        "Total Time",
        "Min Time",
        "Avg Time",
        "Max Time"
    };*/

    /**
     * @see  #getConnectorID()
     */
    long id=-1;

    /**
     * @see  #getHostname()
     */
    final String hostname;

    /**
     * @see  #getLocalIp()
     */
    final String local_ip;

    /**
     * @see  #getPort()
     */
    final int port;

    /**
     * @see  #getConnectedAs()
     */
    final String connectAs;

    /**
     * @see  #getAuthenticatedAs()
     */
    final String authenticateAs;
    
    final String daemonServer;
    
    final ErrorHandler errorHandler;
    
    /**
     * Gets the error handler for this connector.
     */
    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    protected final String password;

    private final Object testConnectLock=new Object();

    public final ActionTypeTable actionTypes;
    public ActionTypeTable getActionTypes() {return actionTypes;}

    public final ActionTable actions;
    public ActionTable getActions() {return actions;}

    public final AOServerDaemonHostTable aoServerDaemonHosts;

    public final AOServerTable aoServers;
    public AOServerTable getAoServers() {return aoServers;}

    public final AOServPermissionTable aoservPermissions;
    public AOServPermissionTable getAoservPermissions() {return aoservPermissions;}

    public final AOServProtocolTable aoservProtocols;

    public final AOSHCommandTable aoshCommands;
    public final ArchitectureTable architectures;
    public final BackupPartitionTable backupPartitions;
    public final BackupReportTable backupReports;
    public final BackupRetentionTable backupRetentions;
    public final BankAccountTable bankAccounts;
    public final BankTransactionTypeTable bankTransactionTypes;
    public final BankTransactionTable bankTransactions;
    public final BankTable banks;
    public final BlackholeEmailAddressTable blackholeEmailAddresses;
    public final BusinessAdministratorTable businessAdministrators;

    public final BusinessAdministratorPermissionTable businessAdministratorPermissions;
    public BusinessAdministratorPermissionTable getBusinessAdministratorPermissions() {return businessAdministratorPermissions;}

    public final BusinessProfileTable businessProfiles;
    public final BusinessTable businesses;
    public final BusinessServerTable businessServers;
    public final ClientJvmProfileTable clientJvmProfiles;
    public final CountryCodeTable countryCodes;

    public final CreditCardProcessorTable creditCardProcessors;
    public CreditCardProcessorTable getCreditCardProcessors() {return creditCardProcessors;}

    public final CreditCardTransactionTable creditCardTransactions;
    public CreditCardTransactionTable getCreditCardTransactions() {return creditCardTransactions;}

    public final CreditCardTable creditCards;
    public CreditCardTable getCreditCards() {return creditCards;}

    public final CvsRepositoryTable cvsRepositories;
    public final DaemonProfileTable daemonProfiles;
    public final DisableLogTable disableLogs;
    
    public final DistroFileTypeTable distroFileTypes;
    public final DistroFileTable distroFiles;
    public final DNSForbiddenZoneTable dnsForbiddenZones;
    public final DNSRecordTable dnsRecords;
    public final DNSTLDTable dnsTLDs;
    public final DNSTypeTable dnsTypes;
    public final DNSZoneTable dnsZones;
    public final EmailAddressTable emailAddresses;
    public final EmailAttachmentBlockTable emailAttachmentBlocks;
    public final EmailAttachmentTypeTable emailAttachmentTypes;
    public final EmailDomainTable emailDomains;
    public final EmailForwardingTable emailForwardings;
    public final EmailListAddressTable emailListAddresses;
    public final EmailListTable emailLists;
    public final EmailPipeAddressTable emailPipeAddresses;
    public final EmailPipeTable emailPipes;
    public final EmailSmtpRelayTypeTable emailSmtpRelayTypes;
    public final EmailSmtpRelayTable emailSmtpRelays;
    public final EmailSpamAssassinIntegrationModeTable emailSpamAssassinIntegrationModes;

    public final EncryptionKeyTable encryptionKeys;
    public EncryptionKeyTable getEncryptionKeys() {return encryptionKeys;}

    public final ExpenseCategoryTable expenseCategories;
    public final FailoverFileLogTable failoverFileLogs;
    public final FailoverFileReplicationTable failoverFileReplications;
    public final FailoverFileScheduleTable failoverFileSchedules;

    public final FailoverMySQLReplicationTable failoverMySQLReplications;
    public FailoverMySQLReplicationTable getFailoverMySQLReplications() {return failoverMySQLReplications;}

    public final FileBackupSettingTable fileBackupSettings;
    public final FTPGuestUserTable ftpGuestUsers;
    public final HttpdBindTable httpdBinds;
    public final HttpdJBossSiteTable httpdJBossSites;
    public final HttpdJBossVersionTable httpdJBossVersions;
    public final HttpdJKCodeTable httpdJKCodes;
    public final HttpdJKProtocolTable httpdJKProtocols;
    public final HttpdServerTable httpdServers;
    public final HttpdSharedTomcatTable httpdSharedTomcats;
    public final HttpdSiteAuthenticatedLocationTable httpdSiteAuthenticatedLocationTable;
    public final HttpdSiteBindTable httpdSiteBinds;
    public final HttpdSiteURLTable httpdSiteURLs;

    public final HttpdSiteTable httpdSites;
    public HttpdSiteTable getHttpdSites() {return httpdSites;}

    public final HttpdStaticSiteTable httpdStaticSites;
    public final HttpdTomcatContextTable httpdTomcatContexts;
    public final HttpdTomcatDataSourceTable httpdTomcatDataSources;
    public final HttpdTomcatParameterTable httpdTomcatParameters;
    public final HttpdTomcatSiteTable httpdTomcatSites;
    public final HttpdTomcatSharedSiteTable httpdTomcatSharedSites;
    public final HttpdTomcatStdSiteTable httpdTomcatStdSites;
    public final HttpdTomcatVersionTable httpdTomcatVersions;
    public final HttpdWorkerTable httpdWorkers;
    public final IPAddressTable ipAddresses;
    public final LinuxAccAddressTable linuxAccAddresses;
    public final LinuxAccountTypeTable linuxAccountTypes;
    public final LinuxAccountTable linuxAccounts;
    public final LinuxGroupAccountTable linuxGroupAccounts;
    public final LinuxGroupTypeTable linuxGroupTypes;
    public final LinuxGroupTable linuxGroups;
    public final LinuxIDTable linuxIDs;
    public final LinuxServerAccountTable linuxServerAccounts;
    public final LinuxServerGroupTable linuxServerGroups;
    public final MajordomoListTable majordomoLists;
    public final MajordomoServerTable majordomoServers;
    public final MajordomoVersionTable majordomoVersions;
    public final MasterHistoryTable masterHistory;
    public final MasterHostTable masterHosts;
    public final MasterProcessTable masterProcesses;
    public final MasterServerProfileTable masterServerProfiles;
    public final MasterServerStatTable masterServerStats;
    public final MasterServerTable masterServers;
    public final MasterUserTable masterUsers;
    public final MonthlyChargeTable monthlyCharges;
    public final MySQLDatabaseTable mysqlDatabases;
    public final MySQLDBUserTable mysqlDBUsers;
    public final MySQLReservedWordTable mysqlReservedWords;
    public final MySQLServerUserTable mysqlServerUsers;

    public final MySQLServerTable mysqlServers;
    public MySQLServerTable getMysqlServers() {return mysqlServers;}

    public final MySQLUserTable mysqlUsers;
    public final NetBindTable netBinds;
    public final NetDeviceIDTable netDeviceIDs;
    public final NetDeviceTable netDevices;
    public final NetPortTable netPorts;
    public final NetProtocolTable netProtocols;
    public final NetTcpRedirectTable netTcpRedirects;
    public final NoticeLogTable noticeLogs;
    public final NoticeTypeTable noticeTypes;
    public final OperatingSystemVersionTable operatingSystemVersions;
    public final OperatingSystemTable operatingSystems;
    public final PackageCategoryTable packageCategories;
    public final PackageDefinitionLimitTable packageDefinitionLimits;
    public final PackageDefinitionTable packageDefinitions;

    public final PackageTable packages;
    public PackageTable getPackages() {return packages;}

    public final PaymentTypeTable paymentTypes;
    
    public final PhysicalServerTable physicalServers;
    public PhysicalServerTable getPhysicalServers() {return physicalServers;}

    public final PostgresDatabaseTable postgresDatabases;
    public final PostgresEncodingTable postgresEncodings;
    public final PostgresReservedWordTable postgresReservedWords;
    public final PostgresServerUserTable postgresServerUsers;

    public final PostgresServerTable postgresServers;
    public PostgresServerTable getPostgresServers() {return postgresServers;}

    public final PostgresUserTable postgresUsers;
    public final PostgresVersionTable postgresVersions;
    public final PrivateFTPServerTable privateFTPServers;

    public final ProcessorTypeTable processorTypes;
    public ProcessorTypeTable getProcessorTypes() {return processorTypes;}
    
    public final ProtocolTable protocols;
    
    public final RackTable racks;
    public RackTable getRacks() {return racks;}

    public final ResourceTable resources;
    public final SchemaColumnTable schemaColumns;
    public final SchemaForeignKeyTable schemaForeignKeys;
    public final SchemaTableTable schemaTables;
    public final SchemaTypeTable schemaTypes;
    public final ServerFarmTable serverFarms;
    public final ServerTable servers;
    public final ShellTable shells;

    public final SignupRequestOptionTable signupRequestOptions;
    public SignupRequestOptionTable getSignupRequestOptions() {return signupRequestOptions;}

    public final SignupRequestTable signupRequests;
    public SignupRequestTable getSignupRequests() {return signupRequests;}

    public final SpamEmailMessageTable spamEmailMessages;
    public final SystemEmailAliasTable systemEmailAliases;
    public final TechnologyTable technologies;
    public final TechnologyClassTable technologyClasses;
    public final TechnologyNameTable technologyNames;
    public final TechnologyVersionTable technologyVersions;
    public final TicketPriorityTable ticketPriorities;
    public final TicketStatusTable ticketStatuses;
    public final TicketTypeTable ticketTypes;
    public final TicketTable tickets;
    public final TimeZoneTable timeZones;
    public final TransactionTypeTable transactionTypes;
    public final TransactionTable transactions;
    public final USStateTable usStates;
    public final UsernameTable usernames;

    public final VirtualDiskTable virtualDisks;
    public VirtualDiskTable getVirtualDisks() {return virtualDisks;}

    public final VirtualServerTable virtualServers;
    public VirtualServerTable getVirtualServers() {return virtualServers;}

    public final WhoisHistoryTable whoisHistory;
    public WhoisHistoryTable getWhoisHistory() {return whoisHistory;}

    public final SimpleAOClient simpleAOClient;
    public SimpleAOClient getSimpleAOClient() {return simpleAOClient;}

    /**
     * The tables are placed in this list in the constructor.
     * This list is aligned with the table identifiers in
     * <code>SchemaTable</code>.
     *
     * @see  SchemaTable
     */
    final List<AOServTable> tables;

    protected AOServConnector(
        String hostname,
        String local_ip,
        int port,
        String connectAs,
        String authenticateAs,
        String password,
        String daemonServer,
        ErrorHandler errorHandler
    ) throws IOException {
        this.hostname=hostname;
        this.local_ip=local_ip;
        this.port=port;
        this.connectAs=connectAs;
        this.authenticateAs=authenticateAs;
        this.password=password;
        this.daemonServer=daemonServer;
        this.errorHandler=errorHandler;

        // These must match the table IDs in SchemaTable
        ArrayList<AOServTable> newTables = new ArrayList<AOServTable>();
        newTables.add(actionTypes=new ActionTypeTable(this));
        newTables.add(actions=new ActionTable(this));
        newTables.add(aoServerDaemonHosts=new AOServerDaemonHostTable(this));
        newTables.add(aoServers=new AOServerTable(this));
        newTables.add(aoservPermissions=new AOServPermissionTable(this));
        newTables.add(aoservProtocols=new AOServProtocolTable(this));
        newTables.add(aoshCommands=new AOSHCommandTable(this));
        newTables.add(architectures=new ArchitectureTable(this));
        newTables.add(backupPartitions=new BackupPartitionTable(this));
        newTables.add(backupReports=new BackupReportTable(this));
        newTables.add(backupRetentions=new BackupRetentionTable(this));
        newTables.add(bankAccounts=new BankAccountTable(this));
        newTables.add(bankTransactionTypes=new BankTransactionTypeTable(this));
        newTables.add(bankTransactions=new BankTransactionTable(this));
        newTables.add(banks=new BankTable(this));
        newTables.add(blackholeEmailAddresses=new BlackholeEmailAddressTable(this));
        newTables.add(businessAdministrators=new BusinessAdministratorTable(this));
        newTables.add(businessAdministratorPermissions=new BusinessAdministratorPermissionTable(this));
        newTables.add(businessProfiles=new BusinessProfileTable(this));
        newTables.add(businesses=new BusinessTable(this));
        newTables.add(businessServers=new BusinessServerTable(this));
        newTables.add(clientJvmProfiles=new ClientJvmProfileTable(this));
        newTables.add(countryCodes=new CountryCodeTable(this));
        newTables.add(creditCardProcessors=new CreditCardProcessorTable(this));
        newTables.add(creditCardTransactions=new CreditCardTransactionTable(this));
        newTables.add(creditCards=new CreditCardTable(this));
        newTables.add(cvsRepositories=new CvsRepositoryTable(this));
        newTables.add(daemonProfiles=new DaemonProfileTable(this));
        newTables.add(disableLogs=new DisableLogTable(this));
        newTables.add(distroFileTypes=new DistroFileTypeTable(this));
        newTables.add(distroFiles=new DistroFileTable(this));
        newTables.add(dnsForbiddenZones=new DNSForbiddenZoneTable(this));
        newTables.add(dnsRecords=new DNSRecordTable(this));
        newTables.add(dnsTLDs=new DNSTLDTable(this));
        newTables.add(dnsTypes=new DNSTypeTable(this));
        newTables.add(dnsZones=new DNSZoneTable(this));
        newTables.add(emailAddresses=new EmailAddressTable(this));
        newTables.add(emailAttachmentBlocks=new EmailAttachmentBlockTable(this));
        newTables.add(emailAttachmentTypes=new EmailAttachmentTypeTable(this));
        newTables.add(emailDomains=new EmailDomainTable(this));
        newTables.add(emailForwardings=new EmailForwardingTable(this));
        newTables.add(emailListAddresses=new EmailListAddressTable(this));
        newTables.add(emailLists=new EmailListTable(this));
        newTables.add(emailPipeAddresses=new EmailPipeAddressTable(this));
        newTables.add(emailPipes=new EmailPipeTable(this));
        newTables.add(emailSmtpRelayTypes=new EmailSmtpRelayTypeTable(this));
        newTables.add(emailSmtpRelays=new EmailSmtpRelayTable(this));
        newTables.add(emailSpamAssassinIntegrationModes=new EmailSpamAssassinIntegrationModeTable(this));
        newTables.add(encryptionKeys=new EncryptionKeyTable(this));
        newTables.add(expenseCategories=new ExpenseCategoryTable(this));
        newTables.add(failoverFileLogs=new FailoverFileLogTable(this));
        newTables.add(failoverFileReplications=new FailoverFileReplicationTable(this));
        newTables.add(failoverFileSchedules=new FailoverFileScheduleTable(this));
        newTables.add(failoverMySQLReplications=new FailoverMySQLReplicationTable(this));
        newTables.add(fileBackupSettings=new FileBackupSettingTable(this));
        newTables.add(ftpGuestUsers=new FTPGuestUserTable(this));
        newTables.add(httpdBinds=new HttpdBindTable(this));
        newTables.add(httpdJBossSites=new HttpdJBossSiteTable(this));
        newTables.add(httpdJBossVersions=new HttpdJBossVersionTable(this));
        newTables.add(httpdJKCodes=new HttpdJKCodeTable(this));
        newTables.add(httpdJKProtocols=new HttpdJKProtocolTable(this));
        newTables.add(httpdServers=new HttpdServerTable(this));
        newTables.add(httpdSharedTomcats=new HttpdSharedTomcatTable(this));
        newTables.add(httpdSiteAuthenticatedLocationTable=new HttpdSiteAuthenticatedLocationTable(this));
        newTables.add(httpdSiteBinds=new HttpdSiteBindTable(this));
        newTables.add(httpdSiteURLs=new HttpdSiteURLTable(this));
        newTables.add(httpdSites=new HttpdSiteTable(this));
        newTables.add(httpdStaticSites=new HttpdStaticSiteTable(this));
        newTables.add(httpdTomcatContexts=new HttpdTomcatContextTable(this));
        newTables.add(httpdTomcatDataSources=new HttpdTomcatDataSourceTable(this));
        newTables.add(httpdTomcatParameters=new HttpdTomcatParameterTable(this));
        newTables.add(httpdTomcatSites=new HttpdTomcatSiteTable(this));
        newTables.add(httpdTomcatSharedSites=new HttpdTomcatSharedSiteTable(this));
        newTables.add(httpdTomcatStdSites=new HttpdTomcatStdSiteTable(this));
        newTables.add(httpdTomcatVersions=new HttpdTomcatVersionTable(this));
        newTables.add(httpdWorkers=new HttpdWorkerTable(this));
        newTables.add(ipAddresses=new IPAddressTable(this));
        newTables.add(linuxAccAddresses=new LinuxAccAddressTable(this));
        newTables.add(linuxAccountTypes=new LinuxAccountTypeTable(this));
        newTables.add(linuxAccounts=new LinuxAccountTable(this));
        newTables.add(linuxGroupAccounts=new LinuxGroupAccountTable(this));
        newTables.add(linuxGroupTypes=new LinuxGroupTypeTable(this));
        newTables.add(linuxGroups=new LinuxGroupTable(this));
        newTables.add(linuxIDs=new LinuxIDTable(this));
        newTables.add(linuxServerAccounts=new LinuxServerAccountTable(this));
        newTables.add(linuxServerGroups=new LinuxServerGroupTable(this));
        newTables.add(majordomoLists=new MajordomoListTable(this));
        newTables.add(majordomoServers=new MajordomoServerTable(this));
        newTables.add(majordomoVersions=new MajordomoVersionTable(this));
        newTables.add(masterHistory=new MasterHistoryTable(this));
        newTables.add(masterHosts=new MasterHostTable(this));
        newTables.add(masterProcesses=new MasterProcessTable(this));
        newTables.add(masterServerProfiles=new MasterServerProfileTable(this));
        newTables.add(masterServerStats=new MasterServerStatTable(this));
        newTables.add(masterServers=new MasterServerTable(this));
        newTables.add(masterUsers=new MasterUserTable(this));
        newTables.add(monthlyCharges=new MonthlyChargeTable(this));
        newTables.add(mysqlDatabases=new MySQLDatabaseTable(this));
        newTables.add(mysqlDBUsers=new MySQLDBUserTable(this));
        newTables.add(mysqlReservedWords=new MySQLReservedWordTable(this));
        newTables.add(mysqlServerUsers=new MySQLServerUserTable(this));
        newTables.add(mysqlServers=new MySQLServerTable(this));
        newTables.add(mysqlUsers=new MySQLUserTable(this));
        newTables.add(netBinds=new NetBindTable(this));
        newTables.add(netDeviceIDs=new NetDeviceIDTable(this));
        newTables.add(netDevices=new NetDeviceTable(this));
        newTables.add(netPorts=new NetPortTable(this));
        newTables.add(netProtocols=new NetProtocolTable(this));
        newTables.add(netTcpRedirects=new NetTcpRedirectTable(this));
        newTables.add(noticeLogs=new NoticeLogTable(this));
        newTables.add(noticeTypes=new NoticeTypeTable(this));
        newTables.add(operatingSystemVersions=new OperatingSystemVersionTable(this));
        newTables.add(operatingSystems=new OperatingSystemTable(this));
        newTables.add(packageCategories=new PackageCategoryTable(this));
        newTables.add(packageDefinitionLimits=new PackageDefinitionLimitTable(this));
        newTables.add(packageDefinitions=new PackageDefinitionTable(this));
        newTables.add(packages=new PackageTable(this));
        newTables.add(paymentTypes=new PaymentTypeTable(this));
        newTables.add(physicalServers=new PhysicalServerTable(this));
        newTables.add(postgresDatabases=new PostgresDatabaseTable(this));
        newTables.add(postgresEncodings=new PostgresEncodingTable(this));
        newTables.add(postgresReservedWords=new PostgresReservedWordTable(this));
        newTables.add(postgresServerUsers=new PostgresServerUserTable(this));
        newTables.add(postgresServers=new PostgresServerTable(this));
        newTables.add(postgresUsers=new PostgresUserTable(this));
        newTables.add(postgresVersions=new PostgresVersionTable(this));
        newTables.add(privateFTPServers=new PrivateFTPServerTable(this));
        newTables.add(processorTypes=new ProcessorTypeTable(this));
        newTables.add(protocols=new ProtocolTable(this));
        newTables.add(racks=new RackTable(this));
        newTables.add(resources=new ResourceTable(this));
        newTables.add(schemaColumns=new SchemaColumnTable(this));
        newTables.add(schemaForeignKeys=new SchemaForeignKeyTable(this));
        newTables.add(schemaTables=new SchemaTableTable(this));
        newTables.add(schemaTypes=new SchemaTypeTable(this));
        newTables.add(serverFarms=new ServerFarmTable(this));
        newTables.add(servers=new ServerTable(this));
        newTables.add(shells=new ShellTable(this));
        newTables.add(signupRequestOptions=new SignupRequestOptionTable(this));
        newTables.add(signupRequests=new SignupRequestTable(this));
        newTables.add(spamEmailMessages=new SpamEmailMessageTable(this));
        newTables.add(systemEmailAliases=new SystemEmailAliasTable(this));
        newTables.add(technologies=new TechnologyTable(this));
        newTables.add(technologyClasses=new TechnologyClassTable(this));
        newTables.add(technologyNames=new TechnologyNameTable(this));
        newTables.add(technologyVersions=new TechnologyVersionTable(this));
        newTables.add(ticketPriorities=new TicketPriorityTable(this));
        newTables.add(ticketStatuses=new TicketStatusTable(this));
        newTables.add(ticketTypes=new TicketTypeTable(this));
        newTables.add(tickets=new TicketTable(this));
        newTables.add(timeZones=new TimeZoneTable(this));
        newTables.add(transactionTypes=new TransactionTypeTable(this));
        newTables.add(transactions=new TransactionTable(this));
        newTables.add(usStates=new USStateTable(this));
        newTables.add(usernames=new UsernameTable(this));
        newTables.add(virtualDisks=new VirtualDiskTable(this));
        newTables.add(virtualServers=new VirtualServerTable(this));
        newTables.add(whoisHistory=new WhoisHistoryTable(this));
        newTables.trimToSize();
        tables = Collections.unmodifiableList(newTables);

        simpleAOClient=new SimpleAOClient(this);
    }

    /**
     * Uses equivilence equality like <code>Object.equals</code>.  Two
     * connectors are considered equal only if they refer to the same
     * object.
     */
    @Override
    final public boolean equals(Object O) {
        return super.equals(O);
    }
    
    /**
     * Uses equivilence hashCode like <code>Object.hashCode()</code>.
     */
    @Override
    final public int hashCode() {
        return super.hashCode();
    }

    /**
     * Clears all caches used by this connector.
     */
    public void clearCaches() {
        for(AOServTable table : tables) table.clearCache();
    }

    /**
     * Executes an aosh command and captures its output into a <code>String</code>.
     *
     * @param  args  the command and arguments to be processed
     *
     * @return  the results of the command wrapped into a <code>String</code>
     *
     * @exception  IOException  if unable to access the server
     * @exception  SQLException  if unable to access the database or data integrity
     *                           checks fail
     */
    public String executeCommand(String[] args) throws IOException, SQLException {
        return AOSH.executeCommand(this, args);
    }

    /**
     * Allocates a connection to the server.  These connections must later be
     * released with the <code>releaseConnection</code> method.  Connection
     * pooling is obtained this way.  These connections may be over any protocol,
     * so they may only safely be used for one client/server exchange per
     * allocation.  Also, if connections are not <i>always</i> released, deadlock
     * will quickly occur.  Please use a try/finally block immediately after
     * allocating the connection to make sure it is always released.
     *
     * @return  the connection to the server
     *
     * @exception  IOException  if unable to connect to the server
     *
     * @see  #releaseConnection
     */
    final AOServConnection getConnection() throws IOException {
        return getConnection(1);
    }

    /**
     * Allocates a connection to the server.  These connections must later be
     * released with the <code>releaseConnection</code> method.  Connection
     * pooling is obtained this way.  These connections may be over any protocol,
     * so they may only safely be used for one client/server exchange per
     * allocation.  Also, if connections are not <i>always</i> released, deadlock
     * will quickly occur.  Please use a try/finally block immediately after
     * allocating the connection to make sure it is always released.
     *
     * @return  the connection to the server
     *
     * @exception  IOException  if unable to connect to the server
     *
     * @see  #releaseConnection
     */
    abstract AOServConnection getConnection(int maxConnections) throws IOException;

    /**
     * @deprecated  Please use version with <code>ErrorHandler</code>.
     *
     * @see  #getConnector(ErrorHandler)
     */
    public static AOServConnector getConnector() throws IOException {
        return getConnector(new StandardErrorHandler());
    }

    /**
     * Gets the default <code>AOServConnector</code> as defined in the
     * <code>com/aoindustries/aoserv/client/aoserv-client.properties</code>
     * resource.  Each possible protocol is tried, in order, until a
     * successful connection is made.
     *
     * @return  the first <code>AOServConnector</code> to successfully connect
     *          to the server
     *
     * @exception  IOException  if no connection can be established
     */
    public static AOServConnector getConnector(ErrorHandler errorHandler) throws IOException {
        String username=AOServClientConfiguration.getUsername();
        String daemonServer=AOServClientConfiguration.getDaemonServer();
        if(daemonServer==null || daemonServer.length()==0) daemonServer=null;
        return getConnector(
            username,
            username,
            AOServClientConfiguration.getPassword(),
            daemonServer,
            errorHandler
        );
    }

    /**
     * @deprecated  Please use version with <code>ErrorHandler</code>.
     *
     * @see  #getConnector(String,String,ErrorHandler)
     */
    public static AOServConnector getConnector(String username, String password) throws IOException {
        return getConnector(username, password, new StandardErrorHandler());
    }

    /**
     * Gets the <code>AOServConnector</code> with the provided authentication
     * information.  The <code>com/aoindustries/aoserv/client/aoserv-client.properties</code>
     * resource determines which protocols will be used.  Each possible protocol is
     * tried, in order, until a successful connection is made.
     *
     * @param  username  the username to connect as
     * @param  password  the password to connect with
     *
     * @return  the first <code>AOServConnector</code> to successfully connect
     *          to the server
     *
     * @exception  IOException  if no connection can be established
     */
    public static AOServConnector getConnector(String username, String password, ErrorHandler errorHandler) throws IOException {
        return getConnector(username, username, password, null, errorHandler);
    }

    /**
     * @deprecated  Please use version with <code>ErrorHandler</code>.
     *
     * @see  #getConnector(String,String,String,String,ErrorHandler)
     */
    public static AOServConnector getConnector(String connectAs, String authenticateAs, String password, String daemonServer) throws IOException {
        return getConnector(connectAs, authenticateAs, password, daemonServer, new StandardErrorHandler());
    }

    /**
     * Gets the <code>AOServConnector</code> with the provided authentication
     * information.  The <code>com/aoindustries/aoserv/client/aoserv-client.properties</code>
     * resource determines which protocols will be used.  Each possible protocol is
     * tried, in order, until a successful connection is made.
     *
     * @param  connectAs  the username to connect as
     * @param  authenticateAs  the username used for authentication, if different than
     *					<code>connectAs</code>, this username must have super user
     *					privileges
     * @param  password  the password to connect with
     *
     * @return  the first <code>AOServConnector</code> to successfully connect
     *          to the server
     *
     * @exception  IOException  if no connection can be established
     */
    public static AOServConnector getConnector(String connectAs, String authenticateAs, String password, String daemonServer, ErrorHandler errorHandler) throws IOException {
        List<String> protocols=AOServClientConfiguration.getProtocols();
        int size=protocols.size();
        for(int c=0;c<size;c++) {
            String protocol=protocols.get(c);
            try {
                AOServConnector connector;
                if(TCPConnector.PROTOCOL.equals(protocol)) {
                    connector=TCPConnector.getTCPConnector(
                        AOServClientConfiguration.getTcpHostname(),
                        AOServClientConfiguration.getTcpLocalIp(),
                        AOServClientConfiguration.getTcpPort(),
                        connectAs,
                        authenticateAs,
                        password,
                        daemonServer,
                        AOServClientConfiguration.getTcpConnectionPoolSize(),
                        AOServClientConfiguration.getTcpConnectionMaxAge(),
                        errorHandler
                    );
                } else if(SSLConnector.PROTOCOL.equals(protocol)) {
                    connector=SSLConnector.getSSLConnector(
                        AOServClientConfiguration.getSslHostname(),
                        AOServClientConfiguration.getSslLocalIp(),
                        AOServClientConfiguration.getSslPort(),
                        connectAs,
                        authenticateAs,
                        password,
                        daemonServer,
                        AOServClientConfiguration.getSslConnectionPoolSize(),
                        AOServClientConfiguration.getSslConnectionMaxAge(),
                        AOServClientConfiguration.getSslTruststorePath(),
                        AOServClientConfiguration.getSslTruststorePassword(),
                        errorHandler
                    );
                /*
                } else if("http".equals(protocol)) {
                    connector=new HTTPConnector();
                } else if("https".equals(protocol)) {
                    connector=new HTTPSConnector();
                */
                } else throw new IOException("Unknown protocol in aoserv.client.protocols: "+protocol);

                return connector;
            } catch(IOException err) {
                errorHandler.reportError(err, null);
            }
        }
        throw new IOException("Unable to connect using any of the available protocols.");
    }

    /**
     * Each connector is assigned a unique identifier, which the
     * server uses to not send events originating from
     * this connector back to connections of this
     * connector.
     *
     * @return  the globally unique identifier or <code>-1</code> if
     *          the identifier has not yet been assigned
     */
    final public long getConnectorID() {
        return id;
    }

    /**
     * Gets the hostname of the server that is connected to.
     */
    final public String getHostname() {
        return hostname;
    }

    /**
     * Gets the optional local IP address that connections are made from.
     */
    final public String getLocalIp() {
        return local_ip;
    }

    /**
     * Gets the server port that is connected to.
     */
    final public int getPort() {
        return port;
    }

    /**
     * Gets the communication protocol being used.
     */
    abstract public String getProtocol();

    private static final Random random = new SecureRandom();
    public static Random getRandom() {
        return random;
    }

    /**
     * Each table has a unique ID, as found in <code>SchemaTable</code>.  The actual
     * <code>AOServTable</code> may be obtained given its identifier.
     *
     * @param  tableID  the unique ID of the table
     *
     * @return  the appropriate subclass of <code>AOServTable</code>
     *
     * @exception  IllegalArgumentException  if unable to find the table
     *
     * @see  SchemaTable
     */
    @SuppressWarnings({"unchecked"})
    final public AOServTable<?,? extends AOServObject> getTable(int tableID) throws IllegalArgumentException {
        if(tableID>=0 && tableID<tables.size()) return tables.get(tableID);
        throw new IllegalArgumentException("Table not found for ID="+tableID);
    }

    /**
     * Gets an unmodifiable list of all of the tables in the system.
     *
     * @return  a <code>List<AOServTable></code> containing all the tables.  Each
     *          table is at an index corresponding to its unique ID.
     *
     * @see  #getTable(int)
     * @see  SchemaTable
     */
    final public List<AOServTable> getTables() {
        return tables;
    }

    /**
     * Gets the <code>BusinessAdministrator</code> who is logged in using
     * this <code>AOServConnector</code>.  Each username and password pair
     * resolves to an always-accessible <code>BusinessAdministrator</code>.
     * Details about permissions and capabilities may be obtained from the
     * <code>BusinessAdministrator</code>.
     *
     * @return  the <code>BusinessAdministrator</code> who is logged in
     *
     * @exception  IOException  if unable to communicate with the server
     * @exception  SQLException  if unable to access the database or the
     *                           <code>BusinessAdministrator</code> was not
     *                           found
     */
    final public BusinessAdministrator getThisBusinessAdministrator() throws SQLException {
        BusinessAdministrator obj=businessAdministrators.get(connectAs);
        if(obj==null) throw new SQLException("Unable to find BusinessAdministrator: "+connectAs);
        return obj;
    }

    /**
     * Manually invalidates the system caches.
     * 
     * @param tableID the table ID
     * @param server the pkey of the server or <code>-1</code> for all servers
     */
    public void invalidateTable(int tableID, int server) throws IOException, SQLException {
        IntList tableList;
        AOServConnection connection=getConnection();
        try {
            CompressedDataOutputStream out=connection.getOutputStream();
            out.writeCompressedInt(AOServProtocol.CommandID.INVALIDATE_TABLE.ordinal());
            out.writeCompressedInt(tableID);
            out.writeCompressedInt(server);
            out.flush();

            CompressedDataInputStream in=connection.getInputStream();
            int code=in.readByte();
            if(code==AOServProtocol.DONE) tableList=readInvalidateList(in);
            else {
                AOServProtocol.checkResult(code, in);
                throw new IOException("Unknown response code: "+code);
            }
        } catch(IOException err) {
            connection.close();
            throw err;
        } finally {
            releaseConnection(connection);
        }
        tablesUpdated(tableList);
    }

    static IntList readInvalidateList(CompressedDataInputStream in) throws IOException {
        IntArrayList tableList=null;
        int tableID;
        while((tableID=in.readCompressedInt())!=-1) {
            if(tableList==null) tableList=new IntArrayList();
            tableList.add(tableID);
        }
        return tableList;
    }

    /**
     * Determines if the connections made by this protocol
     * are secure.  A connection is considered secure if
     * it uses end-point to end-point encryption or goes
     * over private lines.
     *
     * @return  <code>true</code> if the connection is secure
     *
     * @exception  IOException  if unable to determine if the connection
     *                          is secure
     */
    abstract public boolean isSecure() throws IOException;

    /**
     * Times how long it takes to make one request with the server.
     *
     * @return  the connection latency in milliseconds
     */
    final public int ping() throws IOException, SQLException {
        long startTime=System.currentTimeMillis();
        requestUpdate(AOServProtocol.CommandID.PING);
        long timeSpan=System.currentTimeMillis()-startTime;
        if(timeSpan>Integer.MAX_VALUE) return Integer.MAX_VALUE;
        return (int)timeSpan;
    }

    abstract public void printConnectionStatsHTML(ChainWriter out) throws IOException;

    /**
     * Releases a connection to the server.  This will either close the
     * connection or allow another thread to use the connection.
     * Connections may be of any protocol, so each connection must be
     * released after every transaction.
     *
     * @param  connection  the connection to release
     *
     * @exception  if an error occurred while closing or releasing the conection
     *
     * @see  #getConnection
     */
    abstract void releaseConnection(AOServConnection connection) throws IOException;

    final public void removeFromAllTables(TableListener listener) {
        for(AOServTable table : tables) table.removeTableListener(listener);
    }

    static void writeParams(Object[] params, CompressedDataOutputStream out) throws IOException {
        for(Object param : params) {
            if(param==null) throw new NullPointerException("param is null");
            else if(param instanceof Integer) out.writeCompressedInt(((Integer)param).intValue());
            else if(param instanceof SchemaTable.TableID) out.writeCompressedInt(((SchemaTable.TableID)param).ordinal());
            else if(param instanceof AOServProtocol.CommandID) out.writeCompressedInt(((AOServProtocol.CommandID)param).ordinal());
            else if(param instanceof String) out.writeUTF((String)param);
            else if(param instanceof Float) out.writeFloat((Float)param);
            else if(param instanceof Long) out.writeLong((Long)param);
            else if(param instanceof Boolean) out.writeBoolean((Boolean)param);
            else if(param instanceof Short) out.writeShort((Short)param);
            else if(param instanceof byte[]) {
                byte[] bytes=(byte[])param;
                out.writeCompressedInt(bytes.length);
                out.write(bytes, 0, bytes.length);
            }
            else if(param instanceof Streamable) ((Streamable)param).write(out, AOServProtocol.Version.CURRENT_VERSION.getVersion());
            else throw new IOException("Unknown class for param: "+param.getClass().getName());
        }
    }

    final boolean requestBooleanQuery(AOServProtocol.CommandID commID, Object ... params) throws IOException, SQLException {
        AOServConnection connection=getConnection();
        try {
            CompressedDataOutputStream out=connection.getOutputStream();
            out.writeCompressedInt(commID.ordinal());
            writeParams(params, out);
            out.flush();

            CompressedDataInputStream in=connection.getInputStream();
            int code=in.readByte();
            if(code==AOServProtocol.DONE) return in.readBoolean();
            AOServProtocol.checkResult(code, in);
            throw new IOException("Unexpected response code: "+code);
        } catch(IOException err) {
            connection.close();
            throw err;
        } finally {
            releaseConnection(connection);
        }
    }

    final int requestIntQuery(AOServProtocol.CommandID commID, Object ... params) throws IOException, SQLException {
        AOServConnection connection=getConnection();
        try {
            CompressedDataOutputStream out=connection.getOutputStream();
            out.writeCompressedInt(commID.ordinal());
            writeParams(params, out);
            out.flush();

            CompressedDataInputStream in=connection.getInputStream();
            int code=in.readByte();
            if(code==AOServProtocol.DONE) return in.readCompressedInt();
            AOServProtocol.checkResult(code, in);
            throw new IOException("Unexpected response code: "+code);
        } catch(IOException err) {
            connection.close();
            throw err;
        } finally {
            releaseConnection(connection);
        }
    }

    final int requestIntQueryIL(AOServProtocol.CommandID commID, Object ... params) throws IOException, SQLException {
        int result;
        IntList invalidateList;
        AOServConnection connection=getConnection();
        try {
            CompressedDataOutputStream out=connection.getOutputStream();
            out.writeCompressedInt(commID.ordinal());
            writeParams(params, out);
            out.flush();

            CompressedDataInputStream in=connection.getInputStream();
            int code=in.readByte();
            if(code==AOServProtocol.DONE) {
                result=in.readCompressedInt();
                invalidateList=readInvalidateList(in);
            } else {
                AOServProtocol.checkResult(code, in);
                throw new IOException("Unexpected response code: "+code);
            }
        } catch(IOException err) {
            connection.close();
            throw err;
        } finally {
            releaseConnection(connection);
        }
        tablesUpdated(invalidateList);
        return result;
    }

    final long requestLongQuery(AOServProtocol.CommandID commID, Object ... params) throws IOException, SQLException {
        AOServConnection connection=getConnection();
        try {
            CompressedDataOutputStream out=connection.getOutputStream();
            out.writeCompressedInt(commID.ordinal());
            writeParams(params, out);
            out.flush();

            CompressedDataInputStream in=connection.getInputStream();
            int code=in.readByte();
            if(code==AOServProtocol.DONE) return in.readLong();
            AOServProtocol.checkResult(code, in);
            throw new IOException("Unexpected response code: "+code);
        } catch(IOException err) {
            connection.close();
            throw err;
        } finally {
            releaseConnection(connection);
        }
    }

    final short requestShortQuery(AOServProtocol.CommandID commID, Object ... params) throws IOException, SQLException {
        AOServConnection connection=getConnection();
        try {
            CompressedDataOutputStream out=connection.getOutputStream();
            out.writeCompressedInt(commID.ordinal());
            writeParams(params, out);
            out.flush();

            CompressedDataInputStream in=connection.getInputStream();
            int code=in.readByte();
            if(code==AOServProtocol.DONE) return in.readShort();
            AOServProtocol.checkResult(code, in);
            throw new IOException("Unexpected response code: "+code);
        } catch(IOException err) {
            connection.close();
            throw err;
        } finally {
            releaseConnection(connection);
        }
    }

    final short requestShortQueryIL(AOServProtocol.CommandID commID, Object ... params) throws IOException, SQLException {
        short result;
        IntList invalidateList;
        AOServConnection connection=getConnection();
        try {
            CompressedDataOutputStream out=connection.getOutputStream();
            out.writeCompressedInt(commID.ordinal());
            writeParams(params, out);
            out.flush();

            CompressedDataInputStream in=connection.getInputStream();
            int code=in.readByte();
            if(code==AOServProtocol.DONE) {
                result=in.readShort();
                invalidateList=readInvalidateList(in);
            } else {
                AOServProtocol.checkResult(code, in);
                throw new IOException("Unexpected response code: "+code);
            }
        } catch(IOException err) {
            connection.close();
            throw err;
        } finally {
            releaseConnection(connection);
        }
        tablesUpdated(invalidateList);
        return result;
    }

    final String requestStringQuery(AOServProtocol.CommandID commID, Object ... params) throws IOException, SQLException {
        AOServConnection connection=getConnection();
        try {
            CompressedDataOutputStream out=connection.getOutputStream();
            out.writeCompressedInt(commID.ordinal());
            writeParams(params, out);
            out.flush();

            CompressedDataInputStream in=connection.getInputStream();
            int code=in.readByte();
            if(code==AOServProtocol.DONE) return in.readUTF();
            AOServProtocol.checkResult(code, in);
            throw new IOException("Unexpected response code: "+code);
        } catch(IOException err) {
            connection.close();
            throw err;
        } finally {
            releaseConnection(connection);
        }
    }

    final void requestUpdate(AOServProtocol.CommandID commID, Object ... params) throws IOException, SQLException {
        AOServConnection connection=getConnection();
        try {
            CompressedDataOutputStream out=connection.getOutputStream();
            out.writeCompressedInt(commID.ordinal());
            writeParams(params, out);
            out.flush();

            CompressedDataInputStream in=connection.getInputStream();
            int code=in.readByte();
            if(code!=AOServProtocol.DONE) AOServProtocol.checkResult(code, in);
        } catch(IOException err) {
            connection.close();
            throw err;
        } finally {
            releaseConnection(connection);
        }
    }

    final void requestUpdateIL(AOServProtocol.CommandID commID, Object ... params) throws IOException, SQLException {
        IntList invalidateList;
        AOServConnection connection=getConnection();
        try {
            CompressedDataOutputStream out=connection.getOutputStream();
            out.writeCompressedInt(commID.ordinal());
            writeParams(params, out);
            out.flush();

            CompressedDataInputStream in=connection.getInputStream();
            int code=in.readByte();
            if(code==AOServProtocol.DONE) invalidateList=readInvalidateList(in);
            else {
                AOServProtocol.checkResult(code, in);
                throw new IOException("Unexpected response code: "+code);
            }
        } catch(IOException err) {
            connection.close();
            throw err;
        } finally {
            releaseConnection(connection);
        }
        tablesUpdated(invalidateList);
    }
    
    public abstract AOServConnector switchUsers(String username) throws IOException;

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

    /**
     * Tests the connectivity to the server.  This test is only
     * performed once per server per protocol.  Following that,
     * the cached results are used.
     *
     * @exception  IOException  if unable to contact the server
     */
    public final void testConnect() throws IOException, SQLException {
        synchronized(testConnectLock) {
            AOServConnection conn=getConnection();
            try {
                CompressedDataOutputStream out=conn.getOutputStream();
                out.writeCompressedInt(AOServProtocol.CommandID.TEST_CONNECTION.ordinal());
                out.flush();

                CompressedDataInputStream in=conn.getInputStream();
                int code=in.readByte();
                if(code!=AOServProtocol.DONE) {
                    AOServProtocol.checkResult(code, in);
                    throw new IOException("Unexpected response code: "+code);
                }
            } catch(IOException err) {
                conn.close();
                throw err;
            } finally {
                releaseConnection(conn);
            }
        }
    }

    @Override
    final public String toString() {
        return getClass().getName()+"?protocol="+getProtocol()+"&hostname="+hostname+"&local_ip="+local_ip+"&port="+port+"&connectAs="+connectAs+"&authenticateAs="+authenticateAs;
    }
    
    /**
     * Is notified when a table listener is being added.
     */
    void addingTableListener() {
    }
    
    /**
     * Gets some entropy from the master server, returns the number of bytes actually obtained.
     */
    public int getMasterEntropy(byte[] buff, int numBytes) throws IOException, SQLException {
        AOServConnection conn=getConnection();
        try {
            CompressedDataOutputStream out=conn.getOutputStream();
            out.writeCompressedInt(AOServProtocol.CommandID.GET_MASTER_ENTROPY.ordinal());
            out.writeCompressedInt(numBytes);
            out.flush();

            CompressedDataInputStream in=conn.getInputStream();
            int code=in.readByte();
            if(code==AOServProtocol.DONE) {
                int numObtained=in.readCompressedInt();
                for(int c=0;c<numObtained;c++) buff[c]=in.readByte();
                return numObtained;
            } else {
                AOServProtocol.checkResult(code, in);
                throw new IOException("Unexpected response code: "+code);
            }
        } catch(IOException err) {
            conn.close();
            throw err;
        } finally {
            releaseConnection(conn);
        }
    }

    /**
     * Gets the amount of entropy needed by the master server in bytes.
     */
    public long getMasterEntropyNeeded() throws IOException, SQLException {
        return requestLongQuery(AOServProtocol.CommandID.GET_MASTER_ENTROPY_NEEDED);
    }

    /**
     * Adds some entropy to the master server.
     */
    public void addMasterEntropy(byte[] buff, int numBytes) throws IOException, SQLException {
        AOServConnection conn=getConnection();
        try {
            CompressedDataOutputStream out=conn.getOutputStream();
            out.writeCompressedInt(AOServProtocol.CommandID.ADD_MASTER_ENTROPY.ordinal());
            out.writeCompressedInt(numBytes);
            for(int c=0;c<numBytes;c++) out.writeByte(buff[c]);
            out.flush();

            CompressedDataInputStream in=conn.getInputStream();
            int code=in.readByte();
            if(code!=AOServProtocol.DONE) {
                AOServProtocol.checkResult(code, in);
                throw new IOException("Unexpected response code: "+code);
            }
        } catch(IOException err) {
            conn.close();
            throw err;
        } finally {
            releaseConnection(conn);
        }
    }
}
