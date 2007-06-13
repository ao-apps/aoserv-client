package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.profiler.*;
import com.aoindustries.sql.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * A <code>Resource</code> wraps all the data for an entry in the resource table.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class SchemaTable extends GlobalObjectIntegerKey<SchemaTable> {

    static final int COLUMN_NAME=0;

    /**
     * Each table has a unique identifier.  These IDs may change over time, but
     * are constant for one release.
     */
    public static final int
        ACTION_TYPES=0,
        ACTIONS=ACTION_TYPES+1,
        AO_SERVER_DAEMON_HOSTS=ACTIONS+1,
        AO_SERVERS=AO_SERVER_DAEMON_HOSTS+1,
        AOSERV_PERMISSIONS=AO_SERVERS+1,
        AOSERV_PROTOCOLS=AOSERV_PERMISSIONS+1,
        AOSH_COMMANDS=AOSERV_PROTOCOLS+1,
        ARCHITECTURES=AOSH_COMMANDS+1,
        BACKUP_DATA=ARCHITECTURES+1,
        BACKUP_LEVELS=BACKUP_DATA+1,
        BACKUP_PARTITIONS=BACKUP_LEVELS+1,
        BACKUP_REPORTS=BACKUP_PARTITIONS+1,
        BACKUP_RETENTIONS=BACKUP_REPORTS+1,
        BANK_ACCOUNTS=BACKUP_RETENTIONS+1,
        BANK_TRANSACTION_TYPES=BANK_ACCOUNTS+1,
        BANK_TRANSACTIONS=BANK_TRANSACTION_TYPES+1,
        BANKS=BANK_TRANSACTIONS+1,
        BLACKHOLE_EMAIL_ADDRESSES=BANKS+1,
        BUSINESS_ADMINISTRATORS=BLACKHOLE_EMAIL_ADDRESSES+1,
        BUSINESS_ADMINISTRATOR_PERMISSIONS=BUSINESS_ADMINISTRATORS+1,
        BUSINESS_PROFILES=BUSINESS_ADMINISTRATOR_PERMISSIONS+1,
        BUSINESSES=BUSINESS_PROFILES+1,
        BUSINESS_SERVERS=BUSINESSES+1,
        CLIENT_JVM_PROFILE=BUSINESS_SERVERS+1,
        COUNTRY_CODES=CLIENT_JVM_PROFILE+1,
        CREDIT_CARDS=COUNTRY_CODES+1,
        CVS_REPOSITORIES=CREDIT_CARDS+1,
        DAEMON_PROFILE=CVS_REPOSITORIES+1,
        DISABLE_LOG=DAEMON_PROFILE+1,
        DISTRO_FILE_TYPES=DISABLE_LOG+1,
        DISTRO_FILES=DISTRO_FILE_TYPES+1,
        DNS_FORBIDDEN_ZONES=DISTRO_FILES+1,
        DNS_RECORDS=DNS_FORBIDDEN_ZONES+1,
        DNS_TLDS=DNS_RECORDS+1,
        DNS_TYPES=DNS_TLDS+1,
        DNS_ZONES=DNS_TYPES+1,
        EMAIL_ADDRESSES=DNS_ZONES+1,
        EMAIL_ATTACHMENT_BLOCKS=EMAIL_ADDRESSES+1,
        EMAIL_ATTACHMENT_TYPES=EMAIL_ATTACHMENT_BLOCKS+1,
        EMAIL_DOMAINS=EMAIL_ATTACHMENT_TYPES+1,
        EMAIL_FORWARDING=EMAIL_DOMAINS+1,
        EMAIL_LIST_ADDRESSES=EMAIL_FORWARDING+1,
        EMAIL_LISTS=EMAIL_LIST_ADDRESSES+1,
        EMAIL_PIPE_ADDRESSES=EMAIL_LISTS+1,
        EMAIL_PIPES=EMAIL_PIPE_ADDRESSES+1,
        EMAIL_SMTP_RELAY_TYPES=EMAIL_PIPES+1,
        EMAIL_SMTP_RELAYS=EMAIL_SMTP_RELAY_TYPES+1,
        EMAIL_SPAMASSASSIN_INTEGRATION_MODES=EMAIL_SMTP_RELAYS+1,
        ENCRYPTION_KEYS=EMAIL_SPAMASSASSIN_INTEGRATION_MODES+1,
        EXPENSE_CATEGORIES=ENCRYPTION_KEYS+1,
        FAILOVER_FILE_LOG=EXPENSE_CATEGORIES+1,
        FAILOVER_FILE_REPLICATIONS=FAILOVER_FILE_LOG+1,
        FAILOVER_FILE_SCHEDULE=FAILOVER_FILE_REPLICATIONS+1,
        FAILOVER_MYSQL_REPLICATIONS=FAILOVER_FILE_SCHEDULE+1,
        FILE_BACKUPS=FAILOVER_MYSQL_REPLICATIONS+1,
        FILE_BACKUP_DEVICES=FILE_BACKUPS+1,
        FILE_BACKUP_ROOTS=FILE_BACKUP_DEVICES+1,
        FILE_BACKUP_SETTINGS=FILE_BACKUP_ROOTS+1,
        FILE_BACKUP_STATS=FILE_BACKUP_SETTINGS+1,
        FTP_GUEST_USERS=FILE_BACKUP_STATS+1,
        HTTPD_BINDS=FTP_GUEST_USERS+1,
        HTTPD_JBOSS_SITES=HTTPD_BINDS+1,
        HTTPD_JBOSS_VERSIONS=HTTPD_JBOSS_SITES+1,
        HTTPD_JK_CODES=HTTPD_JBOSS_VERSIONS+1,
        HTTPD_JK_PROTOCOLS=HTTPD_JK_CODES+1,
        HTTPD_SERVERS=HTTPD_JK_PROTOCOLS+1,
        HTTPD_SHARED_TOMCATS=HTTPD_SERVERS+1,
        HTTPD_SITE_AUTHENTICATED_LOCATIONS=HTTPD_SHARED_TOMCATS+1,
        HTTPD_SITE_BINDS=HTTPD_SITE_AUTHENTICATED_LOCATIONS+1,
        HTTPD_SITE_URLS=HTTPD_SITE_BINDS+1,
        HTTPD_SITES=HTTPD_SITE_URLS+1,
        HTTPD_STATIC_SITES=HTTPD_SITES+1,
        HTTPD_TOMCAT_CONTEXTS=HTTPD_STATIC_SITES+1,
        HTTPD_TOMCAT_DATA_SOURCES=HTTPD_TOMCAT_CONTEXTS+1,
        HTTPD_TOMCAT_PARAMETERS=HTTPD_TOMCAT_DATA_SOURCES+1,
        HTTPD_TOMCAT_SITES=HTTPD_TOMCAT_PARAMETERS+1,
        HTTPD_TOMCAT_SHARED_SITES=HTTPD_TOMCAT_SITES+1,
        HTTPD_TOMCAT_STD_SITES=HTTPD_TOMCAT_SHARED_SITES+1,
        HTTPD_TOMCAT_VERSIONS=HTTPD_TOMCAT_STD_SITES+1,
        HTTPD_WORKERS=HTTPD_TOMCAT_VERSIONS+1,
        INCOMING_PAYMENTS=HTTPD_WORKERS+1,
        INTERBASE_BACKUPS=INCOMING_PAYMENTS+1,
        INTERBASE_DATABASES=INTERBASE_BACKUPS+1,
        INTERBASE_DB_GROUPS=INTERBASE_DATABASES+1,
        INTERBASE_RESERVED_WORDS=INTERBASE_DB_GROUPS+1,
        INTERBASE_SERVER_USERS=INTERBASE_RESERVED_WORDS+1,
        INTERBASE_USERS=INTERBASE_SERVER_USERS+1,
        IP_ADDRESSES=INTERBASE_USERS+1,
        LINUX_ACC_ADDRESSES=IP_ADDRESSES+1,
        LINUX_ACCOUNT_TYPES=LINUX_ACC_ADDRESSES+1,
        LINUX_ACCOUNTS=LINUX_ACCOUNT_TYPES+1,
        LINUX_GROUP_ACCOUNTS=LINUX_ACCOUNTS+1,
        LINUX_GROUP_TYPES=LINUX_GROUP_ACCOUNTS+1,
        LINUX_GROUPS=LINUX_GROUP_TYPES+1,
        LINUX_IDS=LINUX_GROUPS+1,
        LINUX_SERVER_ACCOUNTS=LINUX_IDS+1,
        LINUX_SERVER_GROUPS=LINUX_SERVER_ACCOUNTS+1,
        MAJORDOMO_LISTS=LINUX_SERVER_GROUPS+1,
        MAJORDOMO_SERVERS=MAJORDOMO_LISTS+1,
        MAJORDOMO_VERSIONS=MAJORDOMO_SERVERS+1,
        MASTER_HISTORY=MAJORDOMO_VERSIONS+1,
        MASTER_HOSTS=MASTER_HISTORY+1,
        MASTER_PROCESSES=MASTER_HOSTS+1,
        MASTER_SERVER_PROFILE=MASTER_PROCESSES+1,
        MASTER_SERVER_STATS=MASTER_SERVER_PROFILE+1,
        MASTER_SERVERS=MASTER_SERVER_STATS+1,
        MASTER_USERS=MASTER_SERVERS+1,
        MERCHANT_ACCOUNTS=MASTER_USERS+1,
        MONTHLY_CHARGES=MERCHANT_ACCOUNTS+1,
        MYSQL_BACKUPS=MONTHLY_CHARGES+1,
        MYSQL_DATABASES=MYSQL_BACKUPS+1,
        MYSQL_DB_USERS=MYSQL_DATABASES+1,
        MYSQL_RESERVED_WORDS=MYSQL_DB_USERS+1,
        MYSQL_SERVER_USERS=MYSQL_RESERVED_WORDS+1,
        MYSQL_SERVERS=MYSQL_SERVER_USERS+1,
        MYSQL_USERS=MYSQL_SERVERS+1,
        NET_BINDS=MYSQL_USERS+1,
        NET_DEVICE_IDS=NET_BINDS+1,
        NET_DEVICES=NET_DEVICE_IDS+1,
        NET_PORTS=NET_DEVICES+1,
        NET_PROTOCOLS=NET_PORTS+1,
        NET_TCP_REDIRECTS=NET_PROTOCOLS+1,
        NOTICE_LOG=NET_TCP_REDIRECTS+1,
        NOTICE_TYPES=NOTICE_LOG+1,
        OPERATING_SYSTEM_VERSIONS=NOTICE_TYPES+1,
        OPERATING_SYSTEMS=OPERATING_SYSTEM_VERSIONS+1,
        PACKAGE_CATEGORIES=OPERATING_SYSTEMS+1,
        PACKAGE_DEFINITION_LIMITS=PACKAGE_CATEGORIES+1,
        PACKAGE_DEFINITIONS=PACKAGE_DEFINITION_LIMITS+1,
        PACKAGES=PACKAGE_DEFINITIONS+1,
        PAYMENT_TYPES=PACKAGES+1,
        PHONE_NUMBERS=PAYMENT_TYPES+1,
        POSTGRES_BACKUPS=PHONE_NUMBERS+1,
        POSTGRES_DATABASES=POSTGRES_BACKUPS+1,
        POSTGRES_ENCODINGS=POSTGRES_DATABASES+1,
        POSTGRES_RESERVED_WORDS=POSTGRES_ENCODINGS+1,
        POSTGRES_SERVER_USERS=POSTGRES_RESERVED_WORDS+1,
        POSTGRES_SERVERS=POSTGRES_SERVER_USERS+1,
        POSTGRES_USERS=POSTGRES_SERVERS+1,
        POSTGRES_VERSIONS=POSTGRES_USERS+1,
        PRIVATE_FTP_SERVERS=POSTGRES_VERSIONS+1,
        PROTOCOLS=PRIVATE_FTP_SERVERS+1,
        RESOURCES=PROTOCOLS+1,
        SCHEMA_COLUMNS=RESOURCES+1,
        SCHEMA_FOREIGN_KEYS=SCHEMA_COLUMNS+1,
        SCHEMA_TABLES=SCHEMA_FOREIGN_KEYS+1,
        SCHEMA_TYPES=SCHEMA_TABLES+1,
        SENDMAIL_SMTP_STATS=SCHEMA_TYPES+1,
        SERVER_FARMS=SENDMAIL_SMTP_STATS+1,
        SERVER_REPORTS=SERVER_FARMS+1,
        SERVERS=SERVER_REPORTS+1,
        SHELLS=SERVERS+1,
        SIGNUP_REQUEST_OPTIONS=SHELLS+1,
        SIGNUP_REQUESTS=SIGNUP_REQUEST_OPTIONS+1,
        SPAM_EMAIL_MESSAGES=SIGNUP_REQUESTS+1,
        SR_CPU=SPAM_EMAIL_MESSAGES+1,
        SR_DB_MYSQL=SR_CPU+1,
        SR_DB_POSTGRES=SR_DB_MYSQL+1,
        SR_DISK_ACCESS=SR_DB_POSTGRES+1,
        SR_DISK_MDSTAT=SR_DISK_ACCESS+1,
        SR_DISK_SPACE=SR_DISK_MDSTAT+1,
        SR_KERNEL=SR_DISK_SPACE+1,
        SR_LOAD=SR_KERNEL+1,
        SR_MEMORY=SR_LOAD+1,
        SR_NET_DEVICES=SR_MEMORY+1,
        SR_NET_ICMP=SR_NET_DEVICES+1,
        SR_NET_IP=SR_NET_ICMP+1,
        SR_NET_TCP=SR_NET_IP+1,
        SR_NET_UDP=SR_NET_TCP+1,
        SR_NUM_USERS=SR_NET_UDP+1,
        SR_PAGING=SR_NUM_USERS+1,
        SR_PROCESSES=SR_PAGING+1,
        SR_SWAP_RATE=SR_PROCESSES+1,
        SR_SWAP_SIZE=SR_SWAP_RATE+1,
        SYSTEM_EMAIL_ALIASES=SR_SWAP_SIZE+1,
        TECHNOLOGIES=SYSTEM_EMAIL_ALIASES+1,
        TECHNOLOGY_CLASSES=TECHNOLOGIES+1,
        TECHNOLOGY_NAMES=TECHNOLOGY_CLASSES+1,
        TECHNOLOGY_VERSIONS=TECHNOLOGY_NAMES+1,
        TICKET_PRIORITIES=TECHNOLOGY_VERSIONS+1,
        TICKET_STATI=TICKET_PRIORITIES+1,
        TICKET_TYPES=TICKET_STATI+1,
        TICKETS=TICKET_TYPES+1,
        TIME_ZONES=TICKETS+1,
        TRANSACTION_TYPES=TIME_ZONES+1,
        TRANSACTIONS=TRANSACTION_TYPES+1,
        US_STATES=TRANSACTIONS+1,
        USERNAMES=US_STATES+1,
        WHOIS_HISTORY=USERNAMES+1
    ;

    public static final int NUM_TABLES=WHOIS_HISTORY+1;

    String name;
    String display;
    private boolean is_public;
    private String description;
    private String dataverse_editor;
    private String since_version;
    private String last_version;
    private String default_order_by;

    private static final String[] descColumns={
        "column", "type", "null", "unique", "references", "referenced_by", "description"
    };

    private static final boolean[] descRightAligns={
        false, false, false, false, false, false, false
    };

    public SchemaTable() {
    }

    public SchemaTable(
        String name,
        int table_id,
        String display,
        boolean is_public,
        String description,
        String dataverse_editor,
        String since_version,
        String last_version,
        String default_order_by
    ) {
        this.name=name;
        this.pkey=table_id;
        this.display=display;
        this.is_public=is_public;
        this.description=description;
        this.dataverse_editor=dataverse_editor;
        this.since_version=since_version;
        this.last_version=last_version;
        this.default_order_by=default_order_by;
    }

    public AOServTable<?,? extends AOServObject> getAOServTable(AOServConnector connector) {
        return connector.getTable(pkey);
    }

    public List<AOSHCommand> getAOSHCommands(AOServConnector connector) {
        Profiler.startProfile(Profiler.FAST, SchemaTable.class, "getAOSHCommands(AOServConnector)", null);
        try {
            return connector.aoshCommands.getAOSHCommands(this);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public Object getColumn(int i) {
        Profiler.startProfile(Profiler.FAST, SchemaTable.class, "getColValueImpl(int)", null);
        try {
            switch(i) {
                case COLUMN_NAME: return name;
                case 1: return Integer.valueOf(pkey);
                case 2: return display;
                case 3: return is_public?Boolean.TRUE:Boolean.FALSE;
                case 4: return description;
                case 5: return dataverse_editor;
                case 6: return since_version;
                case 7: return last_version;
                case 8: return default_order_by;
                default: throw new IllegalArgumentException("Invalid index: "+i);
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public List<SchemaForeignKey> getDataverseBridges(AOServConnector connector, boolean notDraconian) {
        Profiler.startProfile(Profiler.FAST, SchemaTable.class, "getDataverseBridges(AOServConnector,boolean)", null);
        try {
            return connector.schemaForeignKeys.getDataverseBridges(this, notDraconian);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public String getDataverseEditor() {
        return dataverse_editor;
    }

    public String getSinceVersion() {
        return since_version;
    }

    public String getLastVersion() {
        return last_version;
    }
    
    public String getDefaultOrderBy() {
        return default_order_by;
    }

    public String getDescription() {
        return description;
    }

    public String getDisplay() {
        return display;
    }

    public String getName() {
        return name;
    }

    String toStringImpl() {
        return name;
    }

    public SchemaColumn getSchemaColumn(AOServConnector connector, String name) {
        Profiler.startProfile(Profiler.FAST, SchemaTable.class, "getSchemaColumn(AOServConnector,String)", null);
        try {
            return connector.schemaColumns.getSchemaColumn(this, name);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public SchemaColumn getSchemaColumn(AOServConnector connector, int index) {
        Profiler.startProfile(Profiler.FAST, SchemaTable.class, "getSchemaColumn(AOServConnector,int)", null);
        try {
            return connector.schemaColumns.getSchemaColumn(this, index);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public List<SchemaColumn> getSchemaColumns(AOServConnector connector) {
        Profiler.startProfile(Profiler.FAST, SchemaTable.class, "getSchemaColumns(AOServConnector)", null);
        try {
            return connector.schemaColumns.getSchemaColumns(this);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public List<SchemaForeignKey> getSchemaForeignKeys(AOServConnector connector, boolean matchBothWays) {
        Profiler.startProfile(Profiler.FAST, SchemaTable.class, "getSchemaForeignKeys(AOServConnector,boolean)", null);
        try {
            return connector.schemaForeignKeys.getSchemaForeignKeys(this, matchBothWays);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    protected int getTableIDImpl() {
        return SCHEMA_TABLES;
    }

    public int getTableUniqueID() {
        return pkey;
    }

    void initImpl(ResultSet result) throws SQLException {
        Profiler.startProfile(Profiler.FAST, SchemaTable.class, "initImpl(ResultSet)", null);
        try {
            name=result.getString(1);
            pkey=result.getInt(2);
            display=result.getString(3);
            is_public=result.getBoolean(4);
            description=result.getString(5);
            dataverse_editor=result.getString(6);
            since_version=result.getString(7);
            last_version=result.getString(8);
            default_order_by=result.getString(9);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public boolean isPublic() {
        return is_public;
    }

    public void printDescription(AOServConnector connector, TerminalWriter out, boolean isInteractive) throws IOException {
        Profiler.startProfile(Profiler.IO, SchemaTable.class, "printDescription(AOServConnector,TerminalWriter,boolean)", null);
        try {
            out.println();
            out.boldOn();
            out.print("TABLE NAME");
            out.attributesOff();
            out.println();
            out.print("       ");
            out.println(name);
            if(description!=null && description.length()>0) {
                out.println();
                out.boldOn();
                out.print("DESCRIPTION");
                out.attributesOff();
                out.println();
                out.print("       ");
                out.println(description);
            }
            out.println();
            out.boldOn();
            out.print("COLUMNS");
            out.attributesOff();
            out.println();
            out.println();

            // Get the list of columns
            List<SchemaColumn> columns=getSchemaColumns(connector);
            int len=columns.size();

            // Build the Object[] of values
            Object[] values=new Object[len*7];
            int pos=0;
            for(int c=0;c<len;c++) {
                SchemaColumn column=columns.get(c);
                values[pos++]=column.column_name;
                values[pos++]=column.getSchemaType(connector).getType();
                values[pos++]=column.isNullable()?"true":"false";
                values[pos++]=column.isUnique()?"true":"false";
                List<SchemaForeignKey> fkeys=column.getReferences(connector);
                if(!fkeys.isEmpty()) {
                    StringBuilder SB=new StringBuilder();
                    for(int d=0;d<fkeys.size();d++) {
                        SchemaForeignKey key=fkeys.get(d);
                        if(d>0) SB.append('\n');
                        SchemaColumn other=key.getForeignColumn(connector);
                        SB
                            .append(other.getSchemaTable(connector).getName())
                            .append('.')
                            .append(other.column_name)
                        ;
                    }
                    values[pos++]=SB.toString();
                } else values[pos++]=null;

                fkeys=column.getReferencedBy(connector);
                if(!fkeys.isEmpty()) {
                    StringBuilder SB=new StringBuilder();
                    for(int d=0;d<fkeys.size();d++) {
                        SchemaForeignKey key=fkeys.get(d);
                        if(d>0) SB.append('\n');
                        SchemaColumn other=key.getKeyColumn(connector);
                        SB
                            .append(other.getSchemaTable(connector).getName())
                            .append('.')
                            .append(other.column_name)
                        ;
                    }
                    values[pos++]=SB.toString();
                } else values[pos++]=null;
                values[pos++]=column.getDescription();
            }

            // Display the results
            SQLUtility.printTable(descColumns, values, out, isInteractive, descRightAligns);
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public void read(CompressedDataInputStream in) throws IOException {
        Profiler.startProfile(Profiler.IO, SchemaTable.class, "read(CompressedDataInputStream)", null);
        try {
            name=in.readUTF();
            pkey=in.readCompressedInt();
            display=in.readUTF();
            is_public=in.readBoolean();
            description=in.readUTF();
            dataverse_editor=readNullUTF(in);
            since_version=in.readUTF();
            last_version=readNullUTF(in);
            default_order_by=readNullUTF(in);
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        Profiler.startProfile(Profiler.IO, SchemaTable.class, "write(CompressedDataOutputStream,String)", null);
        try {
            out.writeUTF(name);
            out.writeCompressedInt(pkey);
            out.writeUTF(display);
            out.writeBoolean(is_public);
            out.writeUTF(description);
            writeNullUTF(out, dataverse_editor);
            if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_0_A_101)>=0) out.writeUTF(since_version);
            if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_0_A_104)>=0) writeNullUTF(out, last_version);
            if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_4)>=0) writeNullUTF(out, default_order_by);
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }
}