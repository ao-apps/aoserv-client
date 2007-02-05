package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.profiler.*;
import com.aoindustries.sql.*;
import com.aoindustries.table.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.security.*;
import java.util.*;

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

    private static final String[] profileTitles={
        "Method",
        "Parameter",
        "Use Count",
        "Total Time",
        "Min Time",
        "Avg Time",
        "Max Time"
    };

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

    protected final String password;

    private final Object testConnectLock=new Object();

    public final ActionTypeTable actionTypes;
    public ActionTypeTable getActionTypes() {return actionTypes;}

    public final ActionTable actions;
    public ActionTable getActions() {return actions;}

    public final AOServerDaemonHostTable aoServerDaemonHosts;
    public final AOServerTable aoServers;
    public final AOServProtocolTable aoservProtocols;
    public final AOSHCommandTable aoshCommands;
    public final ArchitectureTable architectures;
    public final BackupDataTable backupDatas;
    public final BackupLevelTable backupLevels;
    public final BackupPartitionTable backupPartitions;
    public final BackupReportTable backupReports;
    public final BackupRetentionTable backupRetentions;
    public final BankAccountTable bankAccounts;
    public final BankTransactionTypeTable bankTransactionTypes;
    public final BankTransactionTable bankTransactions;
    public final BankTable banks;
    public final BlackholeEmailAddressTable blackholeEmailAddresses;
    public final BusinessAdministratorTable businessAdministrators;
    public final BusinessProfileTable businessProfiles;
    public final BusinessTable businesses;
    public final BusinessServerTable businessServers;
    public final ClientJvmProfileTable clientJvmProfiles;
    public final CountryCodeTable countryCodes;
    public final CreditCardTable creditCards;
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
    public final ExpenseCategoryTable expenseCategories;
    public final FailoverFileLogTable failoverFileLogs;
    public final FailoverFileReplicationTable failoverFileReplications;
    public final FailoverFileScheduleTable failoverFileSchedules;
    public final FileBackupTable fileBackups;
    public final FileBackupDeviceTable fileBackupDevices;
    public final FileBackupRootTable fileBackupRoots;
    public final FileBackupSettingTable fileBackupSettings;
    public final FileBackupStatTable fileBackupStats;
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
    public final IncomingPaymentTable incomingPayments;
    public final InterBaseBackupTable interBaseBackups;
    public final InterBaseDatabaseTable interBaseDatabases;
    public final InterBaseDBGroupTable interBaseDBGroups;
    public final InterBaseReservedWordTable interBaseReservedWords;
    public final InterBaseServerUserTable interBaseServerUsers;
    public final InterBaseUserTable interBaseUsers;
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
    public final MerchantAccountTable merchantAccounts;
    public final MonthlyChargeTable monthlyCharges;
    public final MySQLBackupTable mysqlBackups;
    public final MySQLDatabaseTable mysqlDatabases;
    public final MySQLDBUserTable mysqlDBUsers;
    public final MySQLReservedWordTable mysqlReservedWords;
    public final MySQLServerUserTable mysqlServerUsers;
    public final MySQLServerTable mysqlServers;
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
    public final PaymentTypeTable paymentTypes;
    public final PhoneNumberTable phoneNumbers;
    public final PostgresBackupTable postgresBackups;
    public final PostgresDatabaseTable postgresDatabases;
    public final PostgresEncodingTable postgresEncodings;
    public final PostgresReservedWordTable postgresReservedWords;
    public final PostgresServerUserTable postgresServerUsers;
    public final PostgresServerTable postgresServers;
    public final PostgresUserTable postgresUsers;
    public final PostgresVersionTable postgresVersions;
    public final PrivateFTPServerTable privateFTPServers;
    public final ProtocolTable protocols;
    public final ResourceTable resources;
    public final SchemaColumnTable schemaColumns;
    public final SchemaForeignKeyTable schemaForeignKeys;
    public final SchemaTableTable schemaTables;
    public final SchemaTypeTable schemaTypes;
    public final SendmailSmtpStatTable sendmailSmtpStats;
    public final ServerFarmTable serverFarms;
    public final ServerReportTable serverReports;
    public final ServerTable servers;
    public final ShellTable shells;
    public final SpamEmailMessageTable spamEmailMessages;
    public final SRCpuTable srCpu;
    public final SRDbMySQLTable srDbMySQL;
    public final SRDbPostgresTable srDbPostgres;
    public final SRDiskAccessTable srDiskAccess;
    public final SRDiskMDStatTable srDiskMDStat;
    public final SRDiskSpaceTable srDiskSpace;
    public final SRKernelTable srKernel;
    public final SRLoadTable srLoad;
    public final SRMemoryTable srMemory;
    public final SRNetDeviceTable srNetDevice;
    public final SRNetICMPTable srNetICMP;
    public final SRNetIPTable srNetIP;
    public final SRNetTCPTable srNetTCP;
    public final SRNetUDPTable srNetUDP;
    public final SRNumUsersTable srNumUsers;
    public final SRPagingTable srPaging;
    public final SRProcessesTable srProcesses;
    public final SRSwapRateTable srSwapRate;
    public final SRSwapSizeTable srSwapSize;
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

    public final WhoisHistoryTable whoisHistory;
    public WhoisHistoryTable getWhoisHistory() {return whoisHistory;}

    public final SimpleAOClient simpleAOClient;
    public SimpleAOClient getSimpleAOClient() {return simpleAOClient;}

    /**
     * The tables are placed in this array as they are created.
     * This array is aligned with the table identifiers in
     * <code>SchemaTable</code>.
     *
     * @see  SchemaTable
     */
    final AOServTable[] tables;

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
        Profiler.startProfile(Profiler.FAST, AOServConnector.class, "<init>(String,String,int,String,String,String,String,ErrorHandler)", null);
        try {
            this.hostname=hostname;
            this.local_ip=local_ip;
            this.port=port;
            this.connectAs=connectAs;
            this.authenticateAs=authenticateAs;
            this.password=password;
            this.daemonServer=daemonServer;
            this.errorHandler=errorHandler;

            // These must match the table IDs in SchemaTable
            tables=new AOServTable[] {
                actionTypes=new ActionTypeTable(this),
                actions=new ActionTable(this),
                aoServerDaemonHosts=new AOServerDaemonHostTable(this),
                aoServers=new AOServerTable(this),
                aoservProtocols=new AOServProtocolTable(this),
                aoshCommands=new AOSHCommandTable(this),
                architectures=new ArchitectureTable(this),
                backupDatas=new BackupDataTable(this),
                backupLevels=new BackupLevelTable(this),
                backupPartitions=new BackupPartitionTable(this),
                backupReports=new BackupReportTable(this),
                backupRetentions=new BackupRetentionTable(this),
                bankAccounts=new BankAccountTable(this),
                bankTransactionTypes=new BankTransactionTypeTable(this),
                bankTransactions=new BankTransactionTable(this),
                banks=new BankTable(this),
                blackholeEmailAddresses=new BlackholeEmailAddressTable(this),
                businessAdministrators=new BusinessAdministratorTable(this),
                businessProfiles=new BusinessProfileTable(this),
                businesses=new BusinessTable(this),
                businessServers=new BusinessServerTable(this),
                clientJvmProfiles=new ClientJvmProfileTable(this),
                countryCodes=new CountryCodeTable(this),
                creditCards=new CreditCardTable(this),
                cvsRepositories=new CvsRepositoryTable(this),
                daemonProfiles=new DaemonProfileTable(this),
                disableLogs=new DisableLogTable(this),
                distroFileTypes=new DistroFileTypeTable(this),
                distroFiles=new DistroFileTable(this),
                dnsForbiddenZones=new DNSForbiddenZoneTable(this),
                dnsRecords=new DNSRecordTable(this),
                dnsTLDs=new DNSTLDTable(this),
                dnsTypes=new DNSTypeTable(this),
                dnsZones=new DNSZoneTable(this),
                emailAddresses=new EmailAddressTable(this),
                emailAttachmentBlocks=new EmailAttachmentBlockTable(this),
                emailAttachmentTypes=new EmailAttachmentTypeTable(this),
                emailDomains=new EmailDomainTable(this),
                emailForwardings=new EmailForwardingTable(this),
                emailListAddresses=new EmailListAddressTable(this),
                emailLists=new EmailListTable(this),
                emailPipeAddresses=new EmailPipeAddressTable(this),
                emailPipes=new EmailPipeTable(this),
                emailSmtpRelayTypes=new EmailSmtpRelayTypeTable(this),
                emailSmtpRelays=new EmailSmtpRelayTable(this),
                emailSpamAssassinIntegrationModes=new EmailSpamAssassinIntegrationModeTable(this),
                expenseCategories=new ExpenseCategoryTable(this),
                failoverFileLogs=new FailoverFileLogTable(this),
                failoverFileReplications=new FailoverFileReplicationTable(this),
                failoverFileSchedules=new FailoverFileScheduleTable(this),
                fileBackups=new FileBackupTable(this),
                fileBackupDevices=new FileBackupDeviceTable(this),
                fileBackupRoots=new FileBackupRootTable(this),
                fileBackupSettings=new FileBackupSettingTable(this),
                fileBackupStats=new FileBackupStatTable(this),
                ftpGuestUsers=new FTPGuestUserTable(this),
                httpdBinds=new HttpdBindTable(this),
                httpdJBossSites=new HttpdJBossSiteTable(this),
                httpdJBossVersions=new HttpdJBossVersionTable(this),
                httpdJKCodes=new HttpdJKCodeTable(this),
                httpdJKProtocols=new HttpdJKProtocolTable(this),
                httpdServers=new HttpdServerTable(this),
                httpdSharedTomcats=new HttpdSharedTomcatTable(this),
                httpdSiteAuthenticatedLocationTable=new HttpdSiteAuthenticatedLocationTable(this),
                httpdSiteBinds=new HttpdSiteBindTable(this),
                httpdSiteURLs=new HttpdSiteURLTable(this),
                httpdSites=new HttpdSiteTable(this),
                httpdStaticSites=new HttpdStaticSiteTable(this),
                httpdTomcatContexts=new HttpdTomcatContextTable(this),
                httpdTomcatDataSources=new HttpdTomcatDataSourceTable(this),
                httpdTomcatParameters=new HttpdTomcatParameterTable(this),
                httpdTomcatSites=new HttpdTomcatSiteTable(this),
                httpdTomcatSharedSites=new HttpdTomcatSharedSiteTable(this),
                httpdTomcatStdSites=new HttpdTomcatStdSiteTable(this),
                httpdTomcatVersions=new HttpdTomcatVersionTable(this),
                httpdWorkers=new HttpdWorkerTable(this),
                incomingPayments=new IncomingPaymentTable(this),
                interBaseBackups=new InterBaseBackupTable(this),
                interBaseDatabases=new InterBaseDatabaseTable(this),
                interBaseDBGroups=new InterBaseDBGroupTable(this),
                interBaseReservedWords=new InterBaseReservedWordTable(this),
                interBaseServerUsers=new InterBaseServerUserTable(this),
                interBaseUsers=new InterBaseUserTable(this),
                ipAddresses=new IPAddressTable(this),
                linuxAccAddresses=new LinuxAccAddressTable(this),
                linuxAccountTypes=new LinuxAccountTypeTable(this),
                linuxAccounts=new LinuxAccountTable(this),
                linuxGroupAccounts=new LinuxGroupAccountTable(this),
                linuxGroupTypes=new LinuxGroupTypeTable(this),
                linuxGroups=new LinuxGroupTable(this),
                linuxIDs=new LinuxIDTable(this),
                linuxServerAccounts=new LinuxServerAccountTable(this),
                linuxServerGroups=new LinuxServerGroupTable(this),
                majordomoLists=new MajordomoListTable(this),
                majordomoServers=new MajordomoServerTable(this),
                majordomoVersions=new MajordomoVersionTable(this),
                masterHistory=new MasterHistoryTable(this),
                masterHosts=new MasterHostTable(this),
                masterProcesses=new MasterProcessTable(this),
                masterServerProfiles=new MasterServerProfileTable(this),
                masterServerStats=new MasterServerStatTable(this),
                masterServers=new MasterServerTable(this),
                masterUsers=new MasterUserTable(this),
                merchantAccounts=new MerchantAccountTable(this),
                monthlyCharges=new MonthlyChargeTable(this),
                mysqlBackups=new MySQLBackupTable(this),
                mysqlDatabases=new MySQLDatabaseTable(this),
                mysqlDBUsers=new MySQLDBUserTable(this),
                mysqlReservedWords=new MySQLReservedWordTable(this),
                mysqlServerUsers=new MySQLServerUserTable(this),
                mysqlServers=new MySQLServerTable(this),
                mysqlUsers=new MySQLUserTable(this),
                netBinds=new NetBindTable(this),
                netDeviceIDs=new NetDeviceIDTable(this),
                netDevices=new NetDeviceTable(this),
                netPorts=new NetPortTable(this),
                netProtocols=new NetProtocolTable(this),
                netTcpRedirects=new NetTcpRedirectTable(this),
                noticeLogs=new NoticeLogTable(this),
                noticeTypes=new NoticeTypeTable(this),
                operatingSystemVersions=new OperatingSystemVersionTable(this),
                operatingSystems=new OperatingSystemTable(this),
                packageCategories=new PackageCategoryTable(this),
                packageDefinitionLimits=new PackageDefinitionLimitTable(this),
                packageDefinitions=new PackageDefinitionTable(this),
                packages=new PackageTable(this),
                paymentTypes=new PaymentTypeTable(this),
                phoneNumbers=new PhoneNumberTable(this),
                postgresBackups=new PostgresBackupTable(this),
                postgresDatabases=new PostgresDatabaseTable(this),
                postgresEncodings=new PostgresEncodingTable(this),
                postgresReservedWords=new PostgresReservedWordTable(this),
                postgresServerUsers=new PostgresServerUserTable(this),
                postgresServers=new PostgresServerTable(this),
                postgresUsers=new PostgresUserTable(this),
                postgresVersions=new PostgresVersionTable(this),
                privateFTPServers=new PrivateFTPServerTable(this),
                protocols=new ProtocolTable(this),
                resources=new ResourceTable(this),
                schemaColumns=new SchemaColumnTable(this),
                schemaForeignKeys=new SchemaForeignKeyTable(this),
                schemaTables=new SchemaTableTable(this),
                schemaTypes=new SchemaTypeTable(this),
                sendmailSmtpStats=new SendmailSmtpStatTable(this),
                serverFarms=new ServerFarmTable(this),
                serverReports=new ServerReportTable(this),
                servers=new ServerTable(this),
                shells=new ShellTable(this),
                spamEmailMessages=new SpamEmailMessageTable(this),
                srCpu=new SRCpuTable(this),
                srDbMySQL=new SRDbMySQLTable(this),
                srDbPostgres=new SRDbPostgresTable(this),
                srDiskAccess=new SRDiskAccessTable(this),
                srDiskMDStat=new SRDiskMDStatTable(this),
                srDiskSpace=new SRDiskSpaceTable(this),
                srKernel=new SRKernelTable(this),
                srLoad=new SRLoadTable(this),
                srMemory=new SRMemoryTable(this),
                srNetDevice=new SRNetDeviceTable(this),
                srNetICMP=new SRNetICMPTable(this),
                srNetIP=new SRNetIPTable(this),
                srNetTCP=new SRNetTCPTable(this),
                srNetUDP=new SRNetUDPTable(this),
                srNumUsers=new SRNumUsersTable(this),
                srPaging=new SRPagingTable(this),
                srProcesses=new SRProcessesTable(this),
                srSwapRate=new SRSwapRateTable(this),
                srSwapSize=new SRSwapSizeTable(this),
                systemEmailAliases=new SystemEmailAliasTable(this),
                technologies=new TechnologyTable(this),
                technologyClasses=new TechnologyClassTable(this),
                technologyNames=new TechnologyNameTable(this),
                technologyVersions=new TechnologyVersionTable(this),
                ticketPriorities=new TicketPriorityTable(this),
                ticketStatuses=new TicketStatusTable(this),
                ticketTypes=new TicketTypeTable(this),
                tickets=new TicketTable(this),
                timeZones=new TimeZoneTable(this),
                transactionTypes=new TransactionTypeTable(this),
                transactions=new TransactionTable(this),
                usStates=new USStateTable(this),
                usernames=new UsernameTable(this),
                whoisHistory=new WhoisHistoryTable(this)
            };

            simpleAOClient=new SimpleAOClient(this);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Clears all caches used by this connector.
     */
    public void clearCaches() {
        Profiler.startProfile(Profiler.FAST, AOServConnector.class, "clearCaches()", null);
        try {
            for(int c=0;c<SchemaTable.NUM_TABLES;c++) {
                tables[c].clearCache();
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
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
    public String executeCommand(String[] args) {
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
        Profiler.startProfile(Profiler.FAST, AOServConnector.class, "getConnector(ErrorHandler)", null);
        try {
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
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
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
        Profiler.startProfile(Profiler.UNKNOWN, AOServConnector.class, "getConnector(String,String,String,String,ErrorHandler)", null);
        try {
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
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
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

    private static Random random;
    public static Random getRandom() {
        Profiler.startProfile(Profiler.UNKNOWN, AOServConnector.class, "getRandom()", null);
        try {
	    synchronized(AOServConnector.class) {
                String algorithm="SHA1PRNG";
		try {
		    if(random==null) random=SecureRandom.getInstance(algorithm);
		    return random;
		} catch(NoSuchAlgorithmException err) {
		    throw new WrappedException(err, new Object[] {"algorithm="+algorithm});
		}
	    }
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
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
    final public AOServTable<?,? extends AOServObject> getTable(int tableID) throws IllegalArgumentException {
        if(tableID>=0 && tableID<SchemaTable.NUM_TABLES) return tables[tableID];
        throw new IllegalArgumentException("Table not found for ID="+tableID);
    }

    /**
     * Gets all of the tables in the system.
     *
     * @return  an <code>AOServTable[]</code> containing all the tables.  Each
     *          table is at an index corresponding to its unique ID.
     *
     * @see  #getTable(int)
     * @see  SchemaTable
     */
    final public AOServTable[] getTables() {
        AOServTable[] tables=new AOServTable[SchemaTable.NUM_TABLES];
        System.arraycopy(this.tables, 0, tables, 0, SchemaTable.NUM_TABLES);
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
    final public BusinessAdministrator getThisBusinessAdministrator() {
        Profiler.startProfile(Profiler.FAST, AOServConnector.class, "getThisBusinessAdministrator()", null);
        try {
            BusinessAdministrator obj=businessAdministrators.get(connectAs);
            if(obj==null) throw new WrappedException(new SQLException("Unable to find BusinessAdministrator: "+connectAs));
            return obj;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public void invalidateTable(int tableID, String server) {
        Profiler.startProfile(Profiler.IO, AOServConnector.class, "invalidateTable(int,String)", null);
        try {
            try {
                IntList tableList;
                AOServConnection connection=getConnection();
                try {
                    CompressedDataOutputStream out=connection.getOutputStream();
                    out.writeCompressedInt(AOServProtocol.INVALIDATE_TABLE);
                    out.writeCompressedInt(tableID);
                    out.writeBoolean(server!=null);
                    if(server!=null) out.writeUTF(server);
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
            } catch(IOException err) {
                throw new WrappedException(err);
            } catch(SQLException err) {
                throw new WrappedException(err);
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    static IntList readInvalidateList(CompressedDataInputStream in) throws IOException {
        Profiler.startProfile(Profiler.IO, AOServConnector.class, "readInvalidateList(CompressedDataInputStream)", null);
        try {
            IntArrayList tableList=null;
            int tableID;
            while((tableID=in.readCompressedInt())!=-1) {
                if(tableList==null) tableList=new IntArrayList();
                tableList.add(tableID);
            }
            return tableList;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
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
    abstract public boolean isSecure();

    /**
     * Times how long it takes to make one request with the server.
     *
     * @return  the connection latency in milliseconds
     */
    final public int ping() {
        Profiler.startProfile(Profiler.FAST, AOServConnector.class, "ping()", null);
        try {
            long startTime=System.currentTimeMillis();
            requestUpdate(AOServProtocol.PING);
            long timeSpan=System.currentTimeMillis()-startTime;
            if(timeSpan>Integer.MAX_VALUE) return Integer.MAX_VALUE;
            return (int)timeSpan;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
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
        Profiler.startProfile(Profiler.FAST, AOServConnector.class, "removeFromAllTables(TableListener)", null);
        try {
            for(int c=0;c<SchemaTable.NUM_TABLES;c++) tables[c].removeTableListener(listener);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    static void writeParams(Object[] params, CompressedDataOutputStream out) throws IOException {
        for(Object param : params) {
            if(param==null) throw new NullPointerException("param is null");
            if(param instanceof Integer) out.writeCompressedInt((Integer)param);
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
            else if(param instanceof Streamable) ((Streamable)param).write(out, AOServProtocol.CURRENT_VERSION);
            else throw new IOException("Unknown class for param: "+param.getClass().getName());
        }
    }

    final boolean requestBooleanQuery(int commID, Object ... params) {
        Profiler.startProfile(Profiler.IO, AOServConnector.class, "requestBooleanQuery(int,...)", null);
        try {
            try {
                AOServConnection connection=getConnection();
                try {
                    CompressedDataOutputStream out=connection.getOutputStream();
                    out.writeCompressedInt(commID);
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
            } catch(IOException err) {
                throw new WrappedException(err);
            } catch(SQLException err) {
                throw new WrappedException(err);
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    final int requestIntQuery(int commID, Object ... params) {
        Profiler.startProfile(Profiler.IO, AOServConnector.class, "requestIntQuery(int,...)", null);
        try {
            try {
                AOServConnection connection=getConnection();
                try {
                    CompressedDataOutputStream out=connection.getOutputStream();
                    out.writeCompressedInt(commID);
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
            } catch(IOException err) {
                throw new WrappedException(err);
            } catch(SQLException err) {
                throw new WrappedException(err);
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    final int requestIntQueryIL(int commID, Object ... params) {
        Profiler.startProfile(Profiler.IO, AOServConnector.class, "requestIntQueryIL(int,...)", null);
        try {
            try {
                int result;
                IntList invalidateList;
                AOServConnection connection=getConnection();
                try {
                    CompressedDataOutputStream out=connection.getOutputStream();
                    out.writeCompressedInt(commID);
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
            } catch(IOException err) {
                throw new WrappedException(err);
            } catch(SQLException err) {
                throw new WrappedException(err);
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    final long requestLongQuery(int commID, Object ... params) {
        Profiler.startProfile(Profiler.IO, AOServConnector.class, "requestLongQuery(int,...)", null);
        try {
            try {
                AOServConnection connection=getConnection();
                try {
                    CompressedDataOutputStream out=connection.getOutputStream();
                    out.writeCompressedInt(commID);
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
            } catch(IOException err) {
                throw new WrappedException(err);
            } catch(SQLException err) {
                throw new WrappedException(err);
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    final short requestShortQuery(int commID, Object ... params) {
        Profiler.startProfile(Profiler.IO, AOServConnector.class, "requestShortQuery(int,...)", null);
        try {
            try {
                AOServConnection connection=getConnection();
                try {
                    CompressedDataOutputStream out=connection.getOutputStream();
                    out.writeCompressedInt(commID);
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
            } catch(IOException err) {
                throw new WrappedException(err);
            } catch(SQLException err) {
                throw new WrappedException(err);
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    final short requestShortQueryIL(int commID, Object ... params) {
        Profiler.startProfile(Profiler.IO, AOServConnector.class, "requestShortQueryIL(int,...)", null);
        try {
            try {
                short result;
                IntList invalidateList;
                AOServConnection connection=getConnection();
                try {
                    CompressedDataOutputStream out=connection.getOutputStream();
                    out.writeCompressedInt(commID);
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
            } catch(IOException err) {
                throw new WrappedException(err);
            } catch(SQLException err) {
                throw new WrappedException(err);
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    final String requestStringQuery(int commID, Object ... params) {
        Profiler.startProfile(Profiler.IO, AOServConnector.class, "requestStringQuery(int,...)", null);
        try {
            try {
                AOServConnection connection=getConnection();
                try {
                    CompressedDataOutputStream out=connection.getOutputStream();
                    out.writeCompressedInt(commID);
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
            } catch(IOException err) {
                throw new WrappedException(err);
            } catch(SQLException err) {
                throw new WrappedException(err);
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    final void requestUpdate(int commID, Object ... params) {
        Profiler.startProfile(Profiler.IO, AOServConnector.class, "requestUpdate(int,...)", null);
        try {
            try {
                AOServConnection connection=getConnection();
                try {
                    CompressedDataOutputStream out=connection.getOutputStream();
                    out.writeCompressedInt(commID);
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
            } catch(IOException err) {
                throw new WrappedException(err);
            } catch(SQLException err) {
                throw new WrappedException(err);
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    final void requestUpdateIL(int commID, Object ... params) {
        Profiler.startProfile(Profiler.IO, AOServConnector.class, "requestUpdateIL(int,Object...)", null);
        try {
            try {
                IntList invalidateList;
                AOServConnection connection=getConnection();
                try {
                    CompressedDataOutputStream out=connection.getOutputStream();
                    out.writeCompressedInt(commID);
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
            } catch(IOException err) {
                throw new WrappedException(err);
            } catch(SQLException err) {
                throw new WrappedException(err);
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }
    
    public abstract AOServConnector switchUsers(String username) throws IOException;

    protected final void tablesUpdated(IntList invalidateList) {
        Profiler.startProfile(Profiler.FAST, AOServConnector.class, "tablesUpdated(IntList)", null);
        try {
            if(invalidateList!=null) {
                int size=invalidateList.size();

                // Clear the caches
                for(int c=0;c<size;c++) {
                    int tableID=invalidateList.getInt(c);
                    tables[tableID].clearCache();
                }

                // Then send the events
                for(int c=0;c<size;c++) {
                    int tableID=invalidateList.getInt(c);
                    tables[tableID].tableUpdated();
                }
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Tests the connectivity to the server.  This test is only
     * performed once per server per protocol.  Following that,
     * the cached results are used.
     *
     * @exception  IOException  if unable to contact the server
     */
    public final void testConnect() {
        Profiler.startProfile(Profiler.IO, AOServConnector.class, "testConnection()", null);
        try {
            try {
                synchronized(testConnectLock) {
                    AOServConnection conn=getConnection();
                    try {
                        CompressedDataOutputStream out=conn.getOutputStream();
                        out.writeCompressedInt(AOServProtocol.TEST_CONNECTION);
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
            } catch(IOException err) {
                throw new WrappedException(err);
            } catch(SQLException err) {
                throw new WrappedException(err);
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    final public String toString() {
        Profiler.startProfile(Profiler.FAST, AOServConnector.class, "toString()", null);
        try {
            return getClass().getName()+"?protocol="+getProtocol()+"&hostname="+hostname+"&local_ip="+local_ip+"&port="+port+"&connectAs="+connectAs+"&authenticateAs="+authenticateAs;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
    
    /**
     * Is notified when a table listener is being added.
     */
    void addingTableListener() {
    }
    
    /**
     * Gets some entropy from the master server, returns the number of bytes actually obtained.
     */
    public int getMasterEntropy(byte[] buff, int numBytes) {
        Profiler.startProfile(Profiler.IO, AOServConnector.class, "getMasterEntropy(byte[],int)", null);
        try {
            try {
                AOServConnection conn=getConnection();
                try {
                    CompressedDataOutputStream out=conn.getOutputStream();
                    out.writeCompressedInt(AOServProtocol.GET_MASTER_ENTROPY);
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
            } catch(IOException err) {
                throw new WrappedException(err);
            } catch(SQLException err) {
                throw new WrappedException(err);
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * Gets the amount of entropy needed by the master server in bytes.
     */
    public long getMasterEntropyNeeded() {
        Profiler.startProfile(Profiler.FAST, AOServConnector.class, "getMasterEntropyNeeded()", null);
        try {
            return requestLongQuery(AOServProtocol.GET_MASTER_ENTROPY_NEEDED);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Adds some entropy to the master server.
     */
    public void addMasterEntropy(byte[] buff, int numBytes) {
        Profiler.startProfile(Profiler.IO, AOServConnector.class, "addMasterEntropy(byte[],int)", null);
        try {
            try {
                AOServConnection conn=getConnection();
                try {
                    CompressedDataOutputStream out=conn.getOutputStream();
                    out.writeCompressedInt(AOServProtocol.ADD_MASTER_ENTROPY);
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
            } catch(IOException err) {
                throw new WrappedException(err);
            } catch(SQLException err) {
                throw new WrappedException(err);
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }
}
