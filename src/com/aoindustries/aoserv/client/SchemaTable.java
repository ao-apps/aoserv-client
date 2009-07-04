package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.sql.SQLUtility;
import com.aoindustries.util.StringUtility;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

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
    public enum TableID {
        AO_SERVER_DAEMON_HOSTS,
        AO_SERVERS,
        AOSERV_PERMISSIONS,
        AOSERV_PROTOCOLS,
        AOSH_COMMANDS,
        ARCHITECTURES,
        BACKUP_PARTITIONS,
        BACKUP_REPORTS,
        BACKUP_RETENTIONS,
        BANK_ACCOUNTS,
        BANK_TRANSACTION_TYPES,
        BANK_TRANSACTIONS,
        BANKS,
        BLACKHOLE_EMAIL_ADDRESSES,
        BRANDS,
        BUSINESS_ADMINISTRATORS,
        BUSINESS_ADMINISTRATOR_PERMISSIONS,
        BUSINESS_PROFILES,
        BUSINESSES,
        BUSINESS_SERVERS,
        CLIENT_JVM_PROFILE,
        COUNTRY_CODES,
        CREDIT_CARD_PROCESSORS,
        CREDIT_CARD_TRANSACTIONS,
        CREDIT_CARDS,
        CVS_REPOSITORIES,
        DAEMON_PROFILE,
        DISABLE_LOG,
        DISTRO_FILE_TYPES,
        DISTRO_FILES,
        DNS_FORBIDDEN_ZONES,
        DNS_RECORDS,
        DNS_TLDS,
        DNS_TYPES,
        DNS_ZONES,
        EMAIL_ADDRESSES,
        EMAIL_ATTACHMENT_BLOCKS,
        EMAIL_ATTACHMENT_TYPES,
        EMAIL_DOMAINS,
        EMAIL_FORWARDING,
        EMAIL_LIST_ADDRESSES,
        EMAIL_LISTS,
        EMAIL_PIPE_ADDRESSES,
        EMAIL_PIPES,
        EMAIL_SMTP_RELAY_TYPES,
        EMAIL_SMTP_RELAYS,
        EMAIL_SPAMASSASSIN_INTEGRATION_MODES,
        ENCRYPTION_KEYS,
        EXPENSE_CATEGORIES,
        FAILOVER_FILE_LOG,
        FAILOVER_FILE_REPLICATIONS,
        FAILOVER_FILE_SCHEDULE,
        FAILOVER_MYSQL_REPLICATIONS,
        FILE_BACKUP_SETTINGS,
        FTP_GUEST_USERS,
        HTTPD_BINDS,
        HTTPD_JBOSS_SITES,
        HTTPD_JBOSS_VERSIONS,
        HTTPD_JK_CODES,
        HTTPD_JK_PROTOCOLS,
        HTTPD_SERVERS,
        HTTPD_SHARED_TOMCATS,
        HTTPD_SITE_AUTHENTICATED_LOCATIONS,
        HTTPD_SITE_BINDS,
        HTTPD_SITE_URLS,
        HTTPD_SITES,
        HTTPD_STATIC_SITES,
        HTTPD_TOMCAT_CONTEXTS,
        HTTPD_TOMCAT_DATA_SOURCES,
        HTTPD_TOMCAT_PARAMETERS,
        HTTPD_TOMCAT_SITES,
        HTTPD_TOMCAT_SHARED_SITES,
        HTTPD_TOMCAT_STD_SITES,
        HTTPD_TOMCAT_VERSIONS,
        HTTPD_WORKERS,
        IP_ADDRESSES,
        LANGUAGES,
        LINUX_ACC_ADDRESSES,
        LINUX_ACCOUNT_TYPES,
        LINUX_ACCOUNTS,
        LINUX_GROUP_ACCOUNTS,
        LINUX_GROUP_TYPES,
        LINUX_GROUPS,
        LINUX_IDS,
        LINUX_SERVER_ACCOUNTS,
        LINUX_SERVER_GROUPS,
        MAJORDOMO_LISTS,
        MAJORDOMO_SERVERS,
        MAJORDOMO_VERSIONS,
        MASTER_HISTORY,
        MASTER_HOSTS,
        MASTER_PROCESSES,
        MASTER_SERVER_PROFILE,
        MASTER_SERVER_STATS,
        MASTER_SERVERS,
        MASTER_USERS,
        MONTHLY_CHARGES,
        MYSQL_DATABASES,
        MYSQL_DB_USERS,
        MYSQL_RESERVED_WORDS,
        MYSQL_SERVER_USERS,
        MYSQL_SERVERS,
        MYSQL_USERS,
        NET_BINDS,
        NET_DEVICE_IDS,
        NET_DEVICES,
        NET_PORTS,
        NET_PROTOCOLS,
        NET_TCP_REDIRECTS,
        NOTICE_LOG,
        NOTICE_TYPES,
        OPERATING_SYSTEM_VERSIONS,
        OPERATING_SYSTEMS,
        PACKAGE_CATEGORIES,
        PACKAGE_DEFINITION_LIMITS,
        PACKAGE_DEFINITIONS,
        PACKAGES,
        PAYMENT_TYPES,
        PHYSICAL_SERVERS,
        POSTGRES_DATABASES,
        POSTGRES_ENCODINGS,
        POSTGRES_RESERVED_WORDS,
        POSTGRES_SERVER_USERS,
        POSTGRES_SERVERS,
        POSTGRES_USERS,
        POSTGRES_VERSIONS,
        PRIVATE_FTP_SERVERS,
        PROCESSOR_TYPES,
        PROTOCOLS,
        RACKS,
        RESELLERS,
        RESOURCES,
        SCHEMA_COLUMNS,
        SCHEMA_FOREIGN_KEYS,
        SCHEMA_TABLES,
        SCHEMA_TYPES,
        SERVER_FARMS,
        SERVERS,
        SHELLS,
        SIGNUP_REQUEST_OPTIONS,
        SIGNUP_REQUESTS,
        SPAM_EMAIL_MESSAGES,
        SYSTEM_EMAIL_ALIASES,
        TECHNOLOGIES,
        TECHNOLOGY_CLASSES,
        TECHNOLOGY_NAMES,
        TECHNOLOGY_VERSIONS,
        TICKET_ACTION_TYPES,
        TICKET_ACTIONS,
        TICKET_ASSIGNMENTS,
        TICKET_BRAND_CATEGORIES,
        TICKET_CATEGORIES,
        TICKET_PRIORITIES,
        TICKET_STATI,
        TICKET_TYPES,
        TICKETS,
        TIME_ZONES,
        TRANSACTION_TYPES,
        TRANSACTIONS,
        US_STATES,
        USERNAMES,
        VIRTUAL_DISKS,
        VIRTUAL_SERVERS,
        WHOIS_HISTORY
    }

    String name;
    String display;
    private boolean is_public;
    private String description;
    private String since_version;
    private String last_version;

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
        String since_version,
        String last_version
    ) {
        this.name=name;
        this.pkey=table_id;
        this.display=display;
        this.is_public=is_public;
        this.description=description;
        this.since_version=since_version;
        this.last_version=last_version;
    }

    public AOServTable<?,? extends AOServObject> getAOServTable(AOServConnector connector) {
        return connector.getTable(pkey);
    }

    public List<AOSHCommand> getAOSHCommands(AOServConnector connector) throws IOException, SQLException {
        return connector.getAoshCommands().getAOSHCommands(this);
    }

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_NAME: return name;
            case 1: return Integer.valueOf(pkey);
            case 2: return display;
            case 3: return is_public?Boolean.TRUE:Boolean.FALSE;
            case 4: return description;
            case 5: return since_version;
            case 6: return last_version;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public String getSinceVersion() {
        return since_version;
    }

    public String getLastVersion() {
        return last_version;
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

    @Override
    String toStringImpl(Locale userLocale) {
        return name;
    }

    public SchemaColumn getSchemaColumn(AOServConnector connector, String name) throws IOException, SQLException {
        return connector.getSchemaColumns().getSchemaColumn(this, name);
    }

    public SchemaColumn getSchemaColumn(AOServConnector connector, int index) throws IOException, SQLException {
        return connector.getSchemaColumns().getSchemaColumn(this, index);
    }

    public List<SchemaColumn> getSchemaColumns(AOServConnector connector) throws IOException, SQLException {
        return connector.getSchemaColumns().getSchemaColumns(this);
    }

    public List<SchemaForeignKey> getSchemaForeignKeys(AOServConnector connector) throws IOException, SQLException {
        return connector.getSchemaForeignKeys().getSchemaForeignKeys(this);
    }

    public TableID getTableID() {
        return TableID.SCHEMA_TABLES;
    }

    public int getTableUniqueID() {
        return pkey;
    }

    public void init(ResultSet result) throws SQLException {
        name=result.getString(1);
        pkey=result.getInt(2);
        display=result.getString(3);
        is_public=result.getBoolean(4);
        description=result.getString(5);
        since_version=result.getString(6);
        last_version=result.getString(7);
    }

    public boolean isPublic() {
        return is_public;
    }

    public void printDescription(AOServConnector connector, TerminalWriter out, boolean isInteractive) throws IOException, SQLException {
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
    }

    public void read(CompressedDataInputStream in) throws IOException {
        name=in.readUTF().intern();
        pkey=in.readCompressedInt();
        display=in.readUTF();
        is_public=in.readBoolean();
        description=in.readUTF();
        since_version=in.readUTF().intern();
        last_version=StringUtility.intern(in.readNullUTF());
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeUTF(name);
        out.writeCompressedInt(pkey);
        out.writeUTF(display);
        out.writeBoolean(is_public);
        out.writeUTF(description);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_30)<=0) out.writeNullUTF(null); // dataverse_editor
        if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_101)>=0) out.writeUTF(since_version);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_104)>=0) out.writeNullUTF(last_version);
        if(
            version.compareTo(AOServProtocol.Version.VERSION_1_4)>=0
            && version.compareTo(AOServProtocol.Version.VERSION_1_30)<=0
        ) out.writeNullUTF(null); // default_order_by
    }
}