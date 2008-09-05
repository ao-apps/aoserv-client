package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.profiler.*;
import java.io.*;
import java.sql.*;

/**
 * Constants used in communication between the client and server.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
public final class AOServProtocol extends GlobalObjectStringKey<AOServProtocol> {

    static final int COLUMN_VERSION=0;
    static final String COLUMN_CREATED_name="created";

    /**
     * The current version of the client/server protocol.
     */
    public static final String
        VERSION_1_0_A_100="1.0a100",
        VERSION_1_0_A_101="1.0a101",
        VERSION_1_0_A_102="1.0a102",
        VERSION_1_0_A_103="1.0a103",
        VERSION_1_0_A_104="1.0a104",
        VERSION_1_0_A_105="1.0a105",
        VERSION_1_0_A_106="1.0a106",
        VERSION_1_0_A_107="1.0a107",
        VERSION_1_0_A_108="1.0a108",
        VERSION_1_0_A_109="1.0a109",
        VERSION_1_0_A_110="1.0a110",
        VERSION_1_0_A_111="1.0a111",
        VERSION_1_0_A_112="1.0a112",
        VERSION_1_0_A_113="1.0a113",
        VERSION_1_0_A_114="1.0a114",
        VERSION_1_0_A_115="1.0a115",
        VERSION_1_0_A_116="1.0a116",
        VERSION_1_0_A_117="1.0a117",
        VERSION_1_0_A_118="1.0a118",
        VERSION_1_0_A_119="1.0a119",
        VERSION_1_0_A_120="1.0a120",
        VERSION_1_0_A_121="1.0a121",
        VERSION_1_0_A_122="1.0a122",
        VERSION_1_0_A_123="1.0a123",
        VERSION_1_0_A_124="1.0a124",
        VERSION_1_0_A_125="1.0a125",
        VERSION_1_0_A_126="1.0a126",
        VERSION_1_0_A_127="1.0a127",
        VERSION_1_0_A_128="1.0a128",
        VERSION_1_0_A_129="1.0a129",
        VERSION_1_0_A_130="1.0a130",
        VERSION_1_1="1.1",
        VERSION_1_2="1.2",
        VERSION_1_3="1.3",
        VERSION_1_4="1.4",
        VERSION_1_5="1.5",
        VERSION_1_6="1.6",
        VERSION_1_7="1.7",
        VERSION_1_8="1.8",
        VERSION_1_9="1.9",
        VERSION_1_10="1.10",
        VERSION_1_11="1.11",
        VERSION_1_12="1.12",
        VERSION_1_13="1.13",
        VERSION_1_14="1.14",
        VERSION_1_15="1.15",
        VERSION_1_16="1.16",
        VERSION_1_17="1.17",
        VERSION_1_18="1.18",
        VERSION_1_19="1.19",
        VERSION_1_20="1.20",
        VERSION_1_21="1.21",
        VERSION_1_22="1.22",
        VERSION_1_23="1.23",
        VERSION_1_24="1.24",
        VERSION_1_25="1.25",
        VERSION_1_26="1.26",
        VERSION_1_27="1.27",
        VERSION_1_28="1.28",
        VERSION_1_29="1.29",
        VERSION_1_30="1.30",
        VERSION_1_31="1.31",
        VERSION_1_32="1.32",
        VERSION_1_33="1.33",
        VERSION_1_34="1.34",
        CURRENT_VERSION=VERSION_1_34
    ;

    /**
     * Gets all of the supported API versions.
     *
     * @return  a <code>String[]</code> of the versions, sorted with the oldest version of array index <code>0</code>.
     */
    public static String[] getVersions() {
        Profiler.startProfile(Profiler.FAST, AOServProtocol.class, "getVersions()", null);
        try {
            return new String[] {
                VERSION_1_0_A_100,
                VERSION_1_0_A_101,
                VERSION_1_0_A_102,
                VERSION_1_0_A_103,
                VERSION_1_0_A_104,
                VERSION_1_0_A_105,
                VERSION_1_0_A_106,
                VERSION_1_0_A_107,
                VERSION_1_0_A_108,
                VERSION_1_0_A_109,
                VERSION_1_0_A_110,
                VERSION_1_0_A_111,
                VERSION_1_0_A_112,
                VERSION_1_0_A_113,
                VERSION_1_0_A_114,
                VERSION_1_0_A_115,
                VERSION_1_0_A_116,
                VERSION_1_0_A_117,
                VERSION_1_0_A_118,
                VERSION_1_0_A_119,
                VERSION_1_0_A_120,
                VERSION_1_0_A_121,
                VERSION_1_0_A_122,
                VERSION_1_0_A_123,
                VERSION_1_0_A_124,
                VERSION_1_0_A_125,
                VERSION_1_0_A_126,
                VERSION_1_0_A_127,
                VERSION_1_0_A_128,
                VERSION_1_0_A_129,
                VERSION_1_0_A_130,
                VERSION_1_1,
                VERSION_1_2,
                VERSION_1_3,
                VERSION_1_4,
                VERSION_1_5,
                VERSION_1_6,
                VERSION_1_7,
                VERSION_1_8,
                VERSION_1_9,
                VERSION_1_10,
                VERSION_1_11,
                VERSION_1_12,
                VERSION_1_13,
                VERSION_1_14,
                VERSION_1_15,
                VERSION_1_16,
                VERSION_1_17,
                VERSION_1_18,
                VERSION_1_19,
                VERSION_1_20,
                VERSION_1_21,
                VERSION_1_22,
                VERSION_1_23,
                VERSION_1_24,
                VERSION_1_25,
                VERSION_1_26,
                VERSION_1_27,
                VERSION_1_28,
                VERSION_1_29,
                VERSION_1_30,
                VERSION_1_31,
                VERSION_1_32,
                VERSION_1_33,
                VERSION_1_34
            };
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Compares two version numbers.
     *
     * @return  <code>&lt;0</code> if version1 was released before version2, <code>0</code> if they are the same,
     *          or <code>&gt;0</code> if version1 was released after version2.
     */
    public static int compareVersions(String version1, String version2) {
        Profiler.startProfile(Profiler.FAST, AOServProtocol.class, "compareVersions(String,String)", null);
        try {
            String[] versions=getVersions();
            int numVersions=versions.length;

            // Find index of version1 in the array
            int index1=-1;
            for(int c=0;c<numVersions;c++) {
                if(version1.equals(versions[c])) {
                    index1=c;
                    break;
                }
            }
            if(index1==-1) throw new IllegalArgumentException("Unknown version1: "+version1);
            
            // Find index of version2 in the array
            int index2=-1;
            for(int c=0;c<numVersions;c++) {
                if(version2.equals(versions[c])) {
                    index2=c;
                    break;
                }
            }
            if(index2==-1) throw new IllegalArgumentException("Unknown version2: "+version2);
            
            // Return the difference in versions
            return index1-index2;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public static final int
        NEXT=0,
        DONE=1,
        IO_EXCEPTION=2,
        SQL_EXCEPTION=3
    ;

    public static final int
        FALSE=0,
        TRUE=1,
        SERVER_DOWN=2
    ;

    /**
     * Since the ordinals are used in the protocol (for compatibility with older implementations), values
     * must be added to the end of this enum.  Values may not be removed (since this would change the
     * ordinals).
     */
    public enum CommandID {
        ADD,
        GET_OBJECT,
        GET_TABLE,
        GET_ROW_COUNT,
        INVALIDATE_TABLE,
        LISTEN_CACHES,
        PING,
        QUIT,
        REMOVE,
        TEST_CONNECTION,
        // Other commands
        ADD_FILE_BACKUPS,
        BACKUP_INTERBASE_DATABASE,
        BACKUP_MYSQL_DATABASE,
        BACKUP_POSTGRES_DATABASE,
        BOUNCE_TICKET,
        CANCEL_BUSINESS,
        CHANGE_TICKET_ADMIN_PRIORITY,
        CHANGE_TICKET_CLIENT_PRIORITY,
        CHANGE_TICKET_DEADLINE,
        CHANGE_TICKET_TECHNOLOGY,
        CHANGE_TICKET_TYPE,
        COMPARE_LINUX_SERVER_ACCOUNT_PASSWORD,
        COMPLETE_TICKET,
        COPY_HOME_DIRECTORY,
        COPY_LINUX_SERVER_ACCOUNT_PASSWORD,
        CREDIT_CARD_DECLINED,
        DISABLE,
        DUMP_INTERBASE_DATABASE,
        DUMP_MYSQL_DATABASE,
        DUMP_POSTGRES_DATABASE,
        ENABLE,
        FIND_FILE_BACKUPS_BY_MD5,
        FIND_HARD_LINKS,
        FIND_LATEST_FILE_BACKUP_SET_ATTRIBUTE_MATCHES,
        FIND_OR_ADD_BACKUP_DATA,
        FIND_OR_ADD_BACKUP_DATAS,
        FLAG_FILE_BACKUPS_AS_DELETED,
        GENERATE_ACCOUNTING_CODE,
        GENERATE_INTERBASE_DATABASE_NAME,
        GENERATE_INTERBASE_DB_GROUP_NAME,
        GENERATE_MYSQL_DATABASE_NAME,
        GENERATE_PACKAGE_NAME,
        GENERATE_POSTGRES_DATABASE_NAME,
        GENERATE_SHARED_TOMCAT_NAME,
        GENERATE_SITE_NAME,
        GET_ACCOUNT_BALANCE_BEFORE,
        GET_ACCOUNT_BALANCE,
        GET_ACTIONS_TICKET,
        GET_AUTORESPONDER_CONTENT,
        GET_BACKUP_DATA,
        GET_BACKUP_DATA_PKEYS,
        GET_BACKUP_DATAS_PKEYS,
        GET_BACKUP_PARTITION_DISK_TOTAL_SIZE,
        GET_BACKUP_PARTITION_DISK_USED_SIZE,
        GET_BANK_TRANSACTIONS_ACCOUNT,
        GET_CACHED_ROW_COUNT,
        GET_CONFIRMED_ACCOUNT_BALANCE_BEFORE,
        GET_CONFIRMED_ACCOUNT_BALANCE,
        GET_CRON_TABLE,
        GET_EMAIL_LIST_ADDRESS_LIST,
        GET_FILE_BACKUPS_PKEYS,
        GET_FILE_BACKUPS_SERVER,
        GET_FILE_BACKUP_CHILDREN,
        GET_FILE_BACKUP_SET_SERVER,
        GET_FILE_BACKUP_VERSIONS,
        GET_FILENAME_FOR_BACKUP_DATA,
        GET_INBOX_ATTRIBUTES,
        GET_LATEST_FILE_BACKUP_SET,
        GET_MAJORDOMO_INFO_FILE,
        GET_MAJORDOMO_INTRO_FILE,
        GET_MRTG_FILE,
        GET_PENDING_PAYMENTS,
        GET_ROOT_BUSINESS,
        GET_SPAM_EMAIL_MESSAGES_FOR_EMAIL_SMTP_RELAY,
        GET_TICKETS_BUSINESS_ADMINISTRATOR,
        GET_TICKETS_BUSINESS,
        GET_TRANSACTIONS_BUSINESS,
        GET_TRANSACTIONS_SEARCH,
        HOLD_TICKET,
        UNUSED_INITIALIZE_HTTPD_SITE_PASSWD_FILE, // No longer used
        IS_ACCOUNTING_AVAILABLE,
        IS_BUSINESS_ADMINISTRATOR_PASSWORD_SET,
        IS_DNS_ZONE_AVAILABLE,
        IS_EMAIL_DOMAIN_AVAILABLE,
        IS_INTERBASE_DATABASE_NAME_AVAILABLE,
        IS_INTERBASE_DB_GROUP_NAME_AVAILABLE,
        IS_INTERBASE_SERVER_USER_PASSWORD_SET,
        IS_LINUX_GROUP_NAME_AVAILABLE,
        IS_LINUX_SERVER_ACCOUNT_PASSWORD_SET,
        IS_MYSQL_DATABASE_NAME_AVAILABLE,
        IS_MYSQL_SERVER_USER_PASSWORD_SET,
        IS_PACKAGE_NAME_AVAILABLE,
        IS_POSTGRES_DATABASE_NAME_AVAILABLE,
        IS_POSTGRES_SERVER_NAME_AVAILABLE,
        IS_POSTGRES_SERVER_USER_PASSWORD_SET,
        IS_SITE_NAME_AVAILABLE,
        IS_SHARED_TOMCAT_NAME_AVAILABLE,
        IS_USERNAME_AVAILABLE,
        KILL_TICKET,
        MOVE_IP_ADDRESS,
        REACTIVATE_TICKET,
        REFRESH_EMAIL_SMTP_RELAY,
        REMOVE_EXPIRED_FILE_BACKUPS,
        REMOVE_EXPIRED_MYSQL_BACKUPS,
        REMOVE_EXPIRED_POSTGRES_BACKUPS,
        REMOVE_UNUSED_BACKUP_DATAS,
        REQUEST_DAEMON_ACCESS,
        RESTART_APACHE,
        RESTART_CRON,
        RESTART_INTERBASE,
        RESTART_MYSQL,
        RESTART_POSTGRESQL,
        RESTART_XFS,
        RESTART_XVFB,
        SEND_BACKUP_DATA,
        SET_AUTORESPONDER,
        SET_BACKUP_RETENTION,
        SET_BUSINESS_ACCOUNTING,
        SET_BUSINESS_ADMINISTRATOR_PASSWORD,
        SET_BUSINESS_ADMINISTRATOR_PROFILE,
        SET_CRON_TABLE,
        SET_CVS_REPOSITORY_MODE,
        SET_DEFAULT_BUSINESS_SERVER,
        SET_EMAIL_LIST_ADDRESS_LIST,
        SET_FILE_BACKUP_SETTINGS,
        SET_HTTPD_SHARED_TOMCAT_IS_MANUAL,
        SET_HTTPD_SITE_BIND_IS_MANUAL,
        SET_HTTPD_SITE_BIND_PREDISABLE_CONFIG,
        SET_HTTPD_SITE_IS_MANUAL,
        SET_HTTPD_SITE_SERVER_ADMIN,
        SET_HTTPD_TOMCAT_CONTEXT_ATTRIBUTES,
        SET_INTERBASE_SERVER_USER_PASSWORD,
        SET_IP_ADDRESS_DHCP_ADDRESS,
        SET_IP_ADDRESS_HOSTNAME,
        SET_IP_ADDRESS_PACKAGE,
        SET_LAST_BACKUP_TIME,
        SET_LAST_DISTRO_TIME,
        SET_LAST_FAILOVER_REPLICATION_TIME,
        SET_LINUX_ACCOUNT_HOME_PHONE,
        SET_LINUX_ACCOUNT_NAME,
        SET_LINUX_ACCOUNT_OFFICE_LOCATION,
        SET_LINUX_ACCOUNT_OFFICE_PHONE,
        SET_LINUX_ACCOUNT_SHELL,
        SET_LINUX_SERVER_ACCOUNT_PASSWORD,
        SET_LINUX_SERVER_ACCOUNT_PREDISABLE_PASSWORD,
        SET_LINUX_SERVER_ACCOUNT_TRASH_EMAIL_RETENTION,
        SET_LINUX_SERVER_ACCOUNT_USE_INBOX,
        SET_MAJORDOMO_INFO_FILE,
        SET_MAJORDOMO_INTRO_FILE,
        SET_MYSQL_SERVER_USER_PASSWORD,
        SET_MYSQL_SERVER_USER_PREDISABLE_PASSWORD,
        SET_POSTGRES_SERVER_USER_PASSWORD,
        SET_POSTGRES_SERVER_USER_PREDISABLE_PASSWORD,
        SET_PRIMARY_HTTPD_SITE_URL,
        SET_PRIMARY_LINUX_GROUP_ACCOUNT,
        START_APACHE,
        START_CRON,
        START_DISTRO,
        START_INTERBASE,
        START_JVM,
        START_MYSQL,
        START_POSTGRESQL,
        START_XFS,
        START_XVFB,
        STOP_APACHE,
        STOP_CRON,
        STOP_INTERBASE,
        STOP_JVM,
        STOP_MYSQL,
        STOP_POSTGRESQL,
        STOP_XFS,
        STOP_XVFB,
        TICKET_WORK,
        TRANSACTION_APPROVED,
        TRANSACTION_DECLINED,
        WAIT_FOR_REBUILD,
        ADD_BACKUP_SERVER,
        SET_NET_BIND_OPEN_FIREWALL,
        SET_NET_BIND_MONITORING,
        GET_BACKUP_DATAS_FOR_BACKUP_PARTITION,
        REMOVE_EXPIRED_INTERBASE_BACKUPS,
        SET_INTERBASE_SERVER_USER_PREDISABLE_PASSWORD,
        GET_TRANSACTIONS_BUSINESS_ADMINISTRATOR,
        GET_ACTIONS_BUSINESS_ADMINISTRATOR,
        GET_TICKETS_CREATED_BUSINESS_ADMINISTRATOR,
        GET_TICKETS_CLOSED_BUSINESS_ADMINISTRATOR,
        GET_MASTER_ENTROPY,
        GET_MASTER_ENTROPY_NEEDED,
        ADD_MASTER_ENTROPY,
        IS_LINUX_SERVER_ACCOUNT_PROCMAIL_MANUAL,
        SET_LINUX_SERVER_ACCOUNT_JUNK_EMAIL_RETENTION,
        SET_LINUX_SERVER_ACCOUNT_EMAIL_SPAMASSASSIN_INTEGRATION_MODE,
        GET_IMAP_FOLDER_SIZES,
        SET_IMAP_FOLDER_SUBSCRIBED,
        SET_LINUX_SERVER_ACCOUNT_SPAMASSASSIN_REQUIRED_SCORE,
        SET_TICKET_ASSIGNED_TO,
        SET_TICKET_CONTACT_EMAILS,
        SET_TICKET_CONTACT_PHONE_NUMBERS,
        SET_TICKET_BUSINESS,
        SET_PACKAGE_DEFINITION_ACTIVE,
        UPDATE_PACKAGE_DEFINITION,
        SET_PACKAGE_DEFINITION_LIMITS,
        COPY_PACKAGE_DEFINITION,
        SET_DNS_ZONE_TTL,
        GET_AWSTATS_FILE,
        REQUEST_SEND_BACKUP_DATA_TO_DAEMON,
        FLAG_BACKUP_DATA_AS_STORED,
        IS_MYSQL_SERVER_NAME_AVAILABLE,
        UPDATE_HTTPD_TOMCAT_DATA_SOURCE,
        UPDATE_HTTPD_TOMCAT_PARAMETER,
        GET_FAILOVER_FILE_LOGS_FOR_REPLICATION,
        SET_HTTPD_SITE_AUTHENTICATED_LOCATION_ATTRIBUTES,
        SET_HTTPD_SITE_BIND_REDIRECT_TO_PRIMARY_HOSTNAME,
        GET_WHOIS_HISTORY_WHOIS_OUTPUT,
        GET_MYSQL_SLAVE_STATUS,
        GET_MYSQL_MASTER_STATUS,
        UPDATE_CREDIT_CARD,
        UPDATE_CREDIT_CARD_NUMBER_AND_EXPIRATION,
        REACTIVATE_CREDIT_CARD,
        SET_CREDIT_CARD_USE_MONTHLY,
        CREDIT_CARD_TRANSACTION_SALE_COMPLETED,
        TRANSACTION_HELD,
        GET_NET_DEVICE_BONDING_REPORT,
        GET_AO_SERVER_3WARE_RAID_REPORT,
        GET_AO_SERVER_MD_RAID_REPORT,
        GET_AO_SERVER_DRBD_REPORT,
        UPDATE_CREDIT_CARD_EXPIRATION,
        SET_FAILOVER_FILE_REPLICATION_BIT_RATE,
        SET_FAILOVER_FILE_SCHEDULES,
        SET_FILE_BACKUP_SETTINGS_ALL_AT_ONCE,
        CREDIT_CARD_TRANSACTION_AUTHORIZE_COMPLETED
    }

    /**
     * Indicates that a field was filtered by the server.
     */
    public static final String FILTERED="*";

    static void checkResult(int code, CompressedDataInputStream in) throws IOException, SQLException {
        Profiler.startProfile(Profiler.IO, AOServProtocol.class, "checkResult(int,CompressedDataInputStream)", null);
        try {
            if(in==null) throw new IllegalArgumentException("in is null");
            if(code==AOServProtocol.IO_EXCEPTION) throw new IOException(in.readUTF());
            if(code==AOServProtocol.SQL_EXCEPTION) throw new SQLException(in.readUTF());
            if(code==-1) throw new EOFException("End of file while reading response code");
            if(code!=AOServProtocol.DONE) throw new IOException("Unknown status code: "+code);
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    private long created;
    private String comments;
    private long last_used;

    public Object getColumn(int i) {
        Profiler.startProfile(Profiler.FAST, AOServProtocol.class, "getColValueImpl(int)", null);
        try {
            switch(i) {
                case COLUMN_VERSION: return pkey;
                case 1: return new java.sql.Date(created);
                case 2: return comments;
                case 3: return last_used == -1 ? null : new java.sql.Date(last_used);
                default: throw new IllegalArgumentException("Invalid index: "+i);
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public String getVersion() {
        return pkey;
    }

    public long getCreated() {
        return created;
    }

    public String getComments() {
        return comments;
    }
    
    public long getLastUsed() {
        return last_used;
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.AOSERV_PROTOCOLS;
    }

    void initImpl(ResultSet result) throws SQLException {
        Profiler.startProfile(Profiler.FAST, AOServProtocol.class, "initImpl(ResultSet)", null);
        try {
            pkey=result.getString(1);
            created=result.getDate(2).getTime();
            comments=result.getString(3);
            java.sql.Date D = result.getDate(4);
            last_used = D==null ? -1 : D.getTime();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public void read(CompressedDataInputStream in) throws IOException {
        Profiler.startProfile(Profiler.IO, AOServProtocol.class, "read(CompressedDataInputStream)", null);
        try {
            pkey=in.readUTF().intern();
            created=in.readLong();
            comments=in.readUTF();
            last_used=in.readLong();
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public void write(CompressedDataOutputStream out, String protocolVersion) throws IOException {
        Profiler.startProfile(Profiler.IO, AOServProtocol.class, "write(CompressedDataOutputStream,String)", null);
        try {
            out.writeUTF(pkey);
            out.writeLong(created);
            out.writeUTF(comments);
            if(AOServProtocol.compareVersions(protocolVersion, AOServProtocol.VERSION_1_9)>=0) out.writeLong(last_used);
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }
}
