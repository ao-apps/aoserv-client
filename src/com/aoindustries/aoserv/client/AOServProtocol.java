package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
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
        CURRENT_VERSION=VERSION_1_7
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
                VERSION_1_7
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

    public static final int
        ADD=0,
        GET_OBJECT=ADD+1,
        GET_TABLE=GET_OBJECT+1,
        GET_ROW_COUNT=GET_TABLE+1,
        INVALIDATE_TABLE=GET_ROW_COUNT+1,
        LISTEN_CACHES=INVALIDATE_TABLE+1,
        PING=LISTEN_CACHES+1,
        QUIT=PING+1,
        REMOVE=QUIT+1,
        TEST_CONNECTION=REMOVE+1
    ;
    
    public static final int
        ADD_FILE_BACKUPS=TEST_CONNECTION+1,
        BACKUP_INTERBASE_DATABASE=ADD_FILE_BACKUPS+1,
        BACKUP_MYSQL_DATABASE=BACKUP_INTERBASE_DATABASE+1,
        BACKUP_POSTGRES_DATABASE=BACKUP_MYSQL_DATABASE+1,
        BOUNCE_TICKET=BACKUP_POSTGRES_DATABASE+1,
        CANCEL_BUSINESS=BOUNCE_TICKET+1,
        CHANGE_TICKET_ADMIN_PRIORITY=CANCEL_BUSINESS+1,
        CHANGE_TICKET_CLIENT_PRIORITY=CHANGE_TICKET_ADMIN_PRIORITY+1,
        CHANGE_TICKET_DEADLINE=CHANGE_TICKET_CLIENT_PRIORITY+1,
        CHANGE_TICKET_TECHNOLOGY=CHANGE_TICKET_DEADLINE+1,
        CHANGE_TICKET_TYPE=CHANGE_TICKET_TECHNOLOGY+1,
        COMPARE_LINUX_SERVER_ACCOUNT_PASSWORD=CHANGE_TICKET_TYPE+1,
        COMPLETE_TICKET=COMPARE_LINUX_SERVER_ACCOUNT_PASSWORD+1,
        COPY_HOME_DIRECTORY=COMPLETE_TICKET+1,
        COPY_LINUX_SERVER_ACCOUNT_PASSWORD=COPY_HOME_DIRECTORY+1,
        CREDIT_CARD_DECLINED=COPY_LINUX_SERVER_ACCOUNT_PASSWORD+1,
        DISABLE=CREDIT_CARD_DECLINED+1,
        DUMP_INTERBASE_DATABASE=DISABLE+1,
        DUMP_MYSQL_DATABASE=DUMP_INTERBASE_DATABASE+1,
        DUMP_POSTGRES_DATABASE=DUMP_MYSQL_DATABASE+1,
        ENABLE=DUMP_POSTGRES_DATABASE+1,
        FIND_FILE_BACKUPS_BY_MD5=ENABLE+1,
        FIND_HARD_LINKS=FIND_FILE_BACKUPS_BY_MD5+1,
        FIND_LATEST_FILE_BACKUP_SET_ATTRIBUTE_MATCHES=FIND_HARD_LINKS+1,
        FIND_OR_ADD_BACKUP_DATA=FIND_LATEST_FILE_BACKUP_SET_ATTRIBUTE_MATCHES+1,
        FIND_OR_ADD_BACKUP_DATAS=FIND_OR_ADD_BACKUP_DATA+1,
        FLAG_FILE_BACKUPS_AS_DELETED=FIND_OR_ADD_BACKUP_DATAS+1,
        GENERATE_ACCOUNTING_CODE=FLAG_FILE_BACKUPS_AS_DELETED+1,
        GENERATE_INTERBASE_DATABASE_NAME=GENERATE_ACCOUNTING_CODE+1,
        GENERATE_INTERBASE_DB_GROUP_NAME=GENERATE_INTERBASE_DATABASE_NAME+1,
        GENERATE_MYSQL_DATABASE_NAME=GENERATE_INTERBASE_DB_GROUP_NAME+1,
        GENERATE_PACKAGE_NAME=GENERATE_MYSQL_DATABASE_NAME+1,
        GENERATE_POSTGRES_DATABASE_NAME=GENERATE_PACKAGE_NAME+1,
        GENERATE_SHARED_TOMCAT_NAME=GENERATE_POSTGRES_DATABASE_NAME+1,
        GENERATE_SITE_NAME=GENERATE_SHARED_TOMCAT_NAME+1,
        GET_ACCOUNT_BALANCE_BEFORE=GENERATE_SITE_NAME+1,
        GET_ACCOUNT_BALANCE=GET_ACCOUNT_BALANCE_BEFORE+1,
        GET_ACTIONS_TICKET=GET_ACCOUNT_BALANCE+1,
        GET_AUTORESPONDER_CONTENT=GET_ACTIONS_TICKET+1,
        GET_BACKUP_DATA=GET_AUTORESPONDER_CONTENT+1,
        GET_BACKUP_DATA_PKEYS=GET_BACKUP_DATA+1,
        GET_BACKUP_DATAS_PKEYS=GET_BACKUP_DATA_PKEYS+1,
        GET_BACKUP_PARTITION_DISK_TOTAL_SIZE=GET_BACKUP_DATAS_PKEYS+1,
        GET_BACKUP_PARTITION_DISK_USED_SIZE=GET_BACKUP_PARTITION_DISK_TOTAL_SIZE+1,
        GET_BANK_TRANSACTIONS_ACCOUNT=GET_BACKUP_PARTITION_DISK_USED_SIZE+1,
        GET_CACHED_ROW_COUNT=GET_BANK_TRANSACTIONS_ACCOUNT+1,
        GET_CONFIRMED_ACCOUNT_BALANCE_BEFORE=GET_CACHED_ROW_COUNT+1,
        GET_CONFIRMED_ACCOUNT_BALANCE=GET_CONFIRMED_ACCOUNT_BALANCE_BEFORE+1,
        GET_CRON_TABLE=GET_CONFIRMED_ACCOUNT_BALANCE+1,
        GET_EMAIL_LIST_ADDRESS_LIST=GET_CRON_TABLE+1,
        GET_FILE_BACKUPS_PKEYS=GET_EMAIL_LIST_ADDRESS_LIST+1,
        GET_FILE_BACKUPS_SERVER=GET_FILE_BACKUPS_PKEYS+1,
        GET_FILE_BACKUP_CHILDREN=GET_FILE_BACKUPS_SERVER+1,
        GET_FILE_BACKUP_SET_SERVER=GET_FILE_BACKUP_CHILDREN+1,
        GET_FILE_BACKUP_VERSIONS=GET_FILE_BACKUP_SET_SERVER+1,
        GET_FILENAME_FOR_BACKUP_DATA=GET_FILE_BACKUP_VERSIONS+1,
        GET_INBOX_ATTRIBUTES=GET_FILENAME_FOR_BACKUP_DATA+1,
        GET_LATEST_FILE_BACKUP_SET=GET_INBOX_ATTRIBUTES+1,
        GET_MAJORDOMO_INFO_FILE=GET_LATEST_FILE_BACKUP_SET+1,
        GET_MAJORDOMO_INTRO_FILE=GET_MAJORDOMO_INFO_FILE+1,
        GET_MRTG_FILE=GET_MAJORDOMO_INTRO_FILE+1,
        GET_PENDING_PAYMENTS=GET_MRTG_FILE+1,
        GET_ROOT_BUSINESS=GET_PENDING_PAYMENTS+1,
        GET_SPAM_EMAIL_MESSAGES_FOR_EMAIL_SMTP_RELAY=GET_ROOT_BUSINESS+1,
        GET_TICKETS_BUSINESS_ADMINISTRATOR=GET_SPAM_EMAIL_MESSAGES_FOR_EMAIL_SMTP_RELAY+1,
        GET_TICKETS_BUSINESS=GET_TICKETS_BUSINESS_ADMINISTRATOR+1,
        GET_TRANSACTIONS_BUSINESS=GET_TICKETS_BUSINESS+1,
        GET_TRANSACTIONS_SEARCH=GET_TRANSACTIONS_BUSINESS+1,
        HOLD_TICKET=GET_TRANSACTIONS_SEARCH+1,
        //INITIALIZE_HTTPD_SITE_PASSWD_FILE=HOLD_TICKET+1,
        IS_ACCOUNTING_AVAILABLE=HOLD_TICKET+2,
        IS_BUSINESS_ADMINISTRATOR_PASSWORD_SET=IS_ACCOUNTING_AVAILABLE+1,
        IS_DNS_ZONE_AVAILABLE=IS_BUSINESS_ADMINISTRATOR_PASSWORD_SET+1,
        IS_EMAIL_DOMAIN_AVAILABLE=IS_DNS_ZONE_AVAILABLE+1,
        IS_INTERBASE_DATABASE_NAME_AVAILABLE=IS_EMAIL_DOMAIN_AVAILABLE+1,
        IS_INTERBASE_DB_GROUP_NAME_AVAILABLE=IS_INTERBASE_DATABASE_NAME_AVAILABLE+1,
        IS_INTERBASE_SERVER_USER_PASSWORD_SET=IS_INTERBASE_DB_GROUP_NAME_AVAILABLE+1,
        IS_LINUX_GROUP_NAME_AVAILABLE=IS_INTERBASE_SERVER_USER_PASSWORD_SET+1,
        IS_LINUX_SERVER_ACCOUNT_PASSWORD_SET=IS_LINUX_GROUP_NAME_AVAILABLE+1,
        IS_MYSQL_DATABASE_NAME_AVAILABLE=IS_LINUX_SERVER_ACCOUNT_PASSWORD_SET+1,
        IS_MYSQL_SERVER_USER_PASSWORD_SET=IS_MYSQL_DATABASE_NAME_AVAILABLE+1,
        IS_PACKAGE_NAME_AVAILABLE=IS_MYSQL_SERVER_USER_PASSWORD_SET+1,
        IS_POSTGRES_DATABASE_NAME_AVAILABLE=IS_PACKAGE_NAME_AVAILABLE+1,
        IS_POSTGRES_SERVER_NAME_AVAILABLE=IS_POSTGRES_DATABASE_NAME_AVAILABLE+1,
        IS_POSTGRES_SERVER_USER_PASSWORD_SET=IS_POSTGRES_SERVER_NAME_AVAILABLE+1,
        IS_SITE_NAME_AVAILABLE=IS_POSTGRES_SERVER_USER_PASSWORD_SET+1,
        IS_SHARED_TOMCAT_NAME_AVAILABLE=IS_SITE_NAME_AVAILABLE+1,
        IS_USERNAME_AVAILABLE=IS_SHARED_TOMCAT_NAME_AVAILABLE+1,
        KILL_TICKET=IS_USERNAME_AVAILABLE+1,
        MOVE_IP_ADDRESS=KILL_TICKET+1,
        REACTIVATE_TICKET=MOVE_IP_ADDRESS+1,
        REFRESH_EMAIL_SMTP_RELAY=REACTIVATE_TICKET+1,
        REMOVE_EXPIRED_FILE_BACKUPS=REFRESH_EMAIL_SMTP_RELAY+1,
        REMOVE_EXPIRED_MYSQL_BACKUPS=REMOVE_EXPIRED_FILE_BACKUPS+1,
        REMOVE_EXPIRED_POSTGRES_BACKUPS=REMOVE_EXPIRED_MYSQL_BACKUPS+1,
        REMOVE_UNUSED_BACKUP_DATAS=REMOVE_EXPIRED_POSTGRES_BACKUPS+1,
        REQUEST_DAEMON_ACCESS=REMOVE_UNUSED_BACKUP_DATAS+1,
        RESTART_APACHE=REQUEST_DAEMON_ACCESS+1,
        RESTART_CRON=RESTART_APACHE+1,
        RESTART_INTERBASE=RESTART_CRON+1,
        RESTART_MYSQL=RESTART_INTERBASE+1,
        RESTART_POSTGRESQL=RESTART_MYSQL+1,
        RESTART_XFS=RESTART_POSTGRESQL+1,
        RESTART_XVFB=RESTART_XFS+1,
        SEND_BACKUP_DATA=RESTART_XVFB+1,
        SET_AUTORESPONDER=SEND_BACKUP_DATA+1,
        SET_BACKUP_RETENTION=SET_AUTORESPONDER+1,
        SET_BUSINESS_ACCOUNTING=SET_BACKUP_RETENTION+1,
        SET_BUSINESS_ADMINISTRATOR_PASSWORD=SET_BUSINESS_ACCOUNTING+1,
        SET_BUSINESS_ADMINISTRATOR_PROFILE=SET_BUSINESS_ADMINISTRATOR_PASSWORD+1,
        SET_CRON_TABLE=SET_BUSINESS_ADMINISTRATOR_PROFILE+1,
        SET_CVS_REPOSITORY_MODE=SET_CRON_TABLE+1,
        SET_DEFAULT_BUSINESS_SERVER=SET_CVS_REPOSITORY_MODE+1,
        SET_EMAIL_LIST_ADDRESS_LIST=SET_DEFAULT_BUSINESS_SERVER+1,
        SET_FILE_BACKUP_SETTINGS=SET_EMAIL_LIST_ADDRESS_LIST+1,
        SET_HTTPD_SHARED_TOMCAT_IS_MANUAL=SET_FILE_BACKUP_SETTINGS+1,
        SET_HTTPD_SITE_BIND_IS_MANUAL=SET_HTTPD_SHARED_TOMCAT_IS_MANUAL+1,
        SET_HTTPD_SITE_BIND_PREDISABLE_CONFIG=SET_HTTPD_SITE_BIND_IS_MANUAL+1,
        SET_HTTPD_SITE_IS_MANUAL=SET_HTTPD_SITE_BIND_PREDISABLE_CONFIG+1,
        SET_HTTPD_SITE_SERVER_ADMIN=SET_HTTPD_SITE_IS_MANUAL+1,
        SET_HTTPD_TOMCAT_CONTEXT_ATTRIBUTES=SET_HTTPD_SITE_SERVER_ADMIN+1,
        SET_INTERBASE_SERVER_USER_PASSWORD=SET_HTTPD_TOMCAT_CONTEXT_ATTRIBUTES+1,
        SET_IP_ADDRESS_DHCP_ADDRESS=SET_INTERBASE_SERVER_USER_PASSWORD+1,
        SET_IP_ADDRESS_HOSTNAME=SET_IP_ADDRESS_DHCP_ADDRESS+1,
        SET_IP_ADDRESS_PACKAGE=SET_IP_ADDRESS_HOSTNAME+1,
        SET_LAST_BACKUP_TIME=SET_IP_ADDRESS_PACKAGE+1,
        SET_LAST_DISTRO_TIME=SET_LAST_BACKUP_TIME+1,
        SET_LAST_FAILOVER_REPLICATION_TIME=SET_LAST_DISTRO_TIME+1,
        SET_LINUX_ACCOUNT_HOME_PHONE=SET_LAST_FAILOVER_REPLICATION_TIME+1,
        SET_LINUX_ACCOUNT_NAME=SET_LINUX_ACCOUNT_HOME_PHONE+1,
        SET_LINUX_ACCOUNT_OFFICE_LOCATION=SET_LINUX_ACCOUNT_NAME+1,
        SET_LINUX_ACCOUNT_OFFICE_PHONE=SET_LINUX_ACCOUNT_OFFICE_LOCATION+1,
        SET_LINUX_ACCOUNT_SHELL=SET_LINUX_ACCOUNT_OFFICE_PHONE+1,
        SET_LINUX_SERVER_ACCOUNT_PASSWORD=SET_LINUX_ACCOUNT_SHELL+1,
        SET_LINUX_SERVER_ACCOUNT_PREDISABLE_PASSWORD=SET_LINUX_SERVER_ACCOUNT_PASSWORD+1,
        SET_LINUX_SERVER_ACCOUNT_TRASH_EMAIL_RETENTION=SET_LINUX_SERVER_ACCOUNT_PREDISABLE_PASSWORD+1,
        SET_LINUX_SERVER_ACCOUNT_USE_INBOX=SET_LINUX_SERVER_ACCOUNT_TRASH_EMAIL_RETENTION+1,
        SET_MAJORDOMO_INFO_FILE=SET_LINUX_SERVER_ACCOUNT_USE_INBOX+1,
        SET_MAJORDOMO_INTRO_FILE=SET_MAJORDOMO_INFO_FILE+1,
        SET_MYSQL_SERVER_USER_PASSWORD=SET_MAJORDOMO_INTRO_FILE+1,
        SET_MYSQL_SERVER_USER_PREDISABLE_PASSWORD=SET_MYSQL_SERVER_USER_PASSWORD+1,
        SET_POSTGRES_SERVER_USER_PASSWORD=SET_MYSQL_SERVER_USER_PREDISABLE_PASSWORD+1,
        SET_POSTGRES_SERVER_USER_PREDISABLE_PASSWORD=SET_POSTGRES_SERVER_USER_PASSWORD+1,
        SET_PRIMARY_HTTPD_SITE_URL=SET_POSTGRES_SERVER_USER_PREDISABLE_PASSWORD+1,
        SET_PRIMARY_LINUX_GROUP_ACCOUNT=SET_PRIMARY_HTTPD_SITE_URL+1,
        START_APACHE=SET_PRIMARY_LINUX_GROUP_ACCOUNT+1,
        START_CRON=START_APACHE+1,
        START_DISTRO=START_CRON+1,
        START_INTERBASE=START_DISTRO+1,
        START_JVM=START_INTERBASE+1,
        START_MYSQL=START_JVM+1,
        START_POSTGRESQL=START_MYSQL+1,
        START_XFS=START_POSTGRESQL+1,
        START_XVFB=START_XFS+1,
        STOP_APACHE=START_XVFB+1,
        STOP_CRON=STOP_APACHE+1,
        STOP_INTERBASE=STOP_CRON+1,
        STOP_JVM=STOP_INTERBASE+1,
        STOP_MYSQL=STOP_JVM+1,
        STOP_POSTGRESQL=STOP_MYSQL+1,
        STOP_XFS=STOP_POSTGRESQL+1,
        STOP_XVFB=STOP_XFS+1,
        TICKET_WORK=STOP_XVFB+1,
        TRANSACTION_APPROVED=TICKET_WORK+1,
        TRANSACTION_DECLINED=TRANSACTION_APPROVED+1,
        WAIT_FOR_REBUILD=TRANSACTION_DECLINED+1,
        ADD_BACKUP_SERVER=WAIT_FOR_REBUILD+1,
        SET_NET_BIND_OPEN_FIREWALL=ADD_BACKUP_SERVER+1,
        SET_NET_BIND_MONITORING=SET_NET_BIND_OPEN_FIREWALL+1,
        GET_BACKUP_DATAS_FOR_BACKUP_PARTITION=SET_NET_BIND_MONITORING+1,
        REMOVE_EXPIRED_INTERBASE_BACKUPS=GET_BACKUP_DATAS_FOR_BACKUP_PARTITION+1,
        SET_INTERBASE_SERVER_USER_PREDISABLE_PASSWORD=REMOVE_EXPIRED_INTERBASE_BACKUPS+1,
        GET_TRANSACTIONS_BUSINESS_ADMINISTRATOR=SET_INTERBASE_SERVER_USER_PREDISABLE_PASSWORD+1,
        GET_ACTIONS_BUSINESS_ADMINISTRATOR=GET_TRANSACTIONS_BUSINESS_ADMINISTRATOR+1,
        GET_TICKETS_CREATED_BUSINESS_ADMINISTRATOR=GET_ACTIONS_BUSINESS_ADMINISTRATOR+1,
        GET_TICKETS_CLOSED_BUSINESS_ADMINISTRATOR=GET_TICKETS_CREATED_BUSINESS_ADMINISTRATOR+1,
        GET_MASTER_ENTROPY=GET_TICKETS_CLOSED_BUSINESS_ADMINISTRATOR+1,
        GET_MASTER_ENTROPY_NEEDED=GET_MASTER_ENTROPY+1,
        ADD_MASTER_ENTROPY=GET_MASTER_ENTROPY_NEEDED+1,
        IS_LINUX_SERVER_ACCOUNT_PROCMAIL_MANUAL=ADD_MASTER_ENTROPY+1,
        SET_LINUX_SERVER_ACCOUNT_JUNK_EMAIL_RETENTION=IS_LINUX_SERVER_ACCOUNT_PROCMAIL_MANUAL+1,
        SET_LINUX_SERVER_ACCOUNT_EMAIL_SPAMASSASSIN_INTEGRATION_MODE=SET_LINUX_SERVER_ACCOUNT_JUNK_EMAIL_RETENTION+1,
        GET_IMAP_FOLDER_SIZES=SET_LINUX_SERVER_ACCOUNT_EMAIL_SPAMASSASSIN_INTEGRATION_MODE+1,
        SET_IMAP_FOLDER_SUBSCRIBED=GET_IMAP_FOLDER_SIZES+1,
        SET_LINUX_SERVER_ACCOUNT_SPAMASSASSIN_REQUIRED_SCORE=SET_IMAP_FOLDER_SUBSCRIBED+1,
        SET_TICKET_ASSIGNED_TO=SET_LINUX_SERVER_ACCOUNT_SPAMASSASSIN_REQUIRED_SCORE+1,
        SET_TICKET_CONTACT_EMAILS=SET_TICKET_ASSIGNED_TO+1,
        SET_TICKET_CONTACT_PHONE_NUMBERS=SET_TICKET_CONTACT_EMAILS+1,
        SET_TICKET_BUSINESS=SET_TICKET_CONTACT_PHONE_NUMBERS+1,
        SET_PACKAGE_DEFINITION_ACTIVE=SET_TICKET_BUSINESS+1,
        UPDATE_PACKAGE_DEFINITION=SET_PACKAGE_DEFINITION_ACTIVE+1,
        SET_PACKAGE_DEFINITION_LIMITS=UPDATE_PACKAGE_DEFINITION+1,
        COPY_PACKAGE_DEFINITION=SET_PACKAGE_DEFINITION_LIMITS+1,
        SET_DNS_ZONE_TTL=COPY_PACKAGE_DEFINITION+1,
        GET_AWSTATS_FILE=SET_DNS_ZONE_TTL+1,
        REQUEST_SEND_BACKUP_DATA_TO_DAEMON=GET_AWSTATS_FILE+1,
        FLAG_BACKUP_DATA_AS_STORED=REQUEST_SEND_BACKUP_DATA_TO_DAEMON+1,
        IS_MYSQL_SERVER_NAME_AVAILABLE=FLAG_BACKUP_DATA_AS_STORED+1,
        UPDATE_HTTPD_TOMCAT_DATA_SOURCE=IS_MYSQL_SERVER_NAME_AVAILABLE+1,
        UPDATE_HTTPD_TOMCAT_PARAMETER=UPDATE_HTTPD_TOMCAT_DATA_SOURCE+1,
        GET_FAILOVER_FILE_LOGS_FOR_REPLICATION=UPDATE_HTTPD_TOMCAT_PARAMETER+1
    ;

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

    public Object getColumn(int i) {
        Profiler.startProfile(Profiler.FAST, AOServProtocol.class, "getColValueImpl(int)", null);
        try {
            switch(i) {
                case COLUMN_VERSION: return pkey;
                case 1: return new java.sql.Date(created);
                case 2: return comments;
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

    protected int getTableIDImpl() {
        return SchemaTable.AOSERV_PROTOCOLS;
    }

    void initImpl(ResultSet result) throws SQLException {
        Profiler.startProfile(Profiler.FAST, AOServProtocol.class, "initImpl(ResultSet)", null);
        try {
            pkey=result.getString(1);
            created=result.getTimestamp(2).getTime();
            comments=result.getString(3);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public void read(CompressedDataInputStream in) throws IOException {
        Profiler.startProfile(Profiler.IO, AOServProtocol.class, "read(CompressedDataInputStream)", null);
        try {
            pkey=in.readUTF();
            created=in.readLong();
            comments=in.readUTF();
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
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }
}
