/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2014, 2015, 2016, 2017, 2018  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aoserv-client.
 *
 * aoserv-client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aoserv-client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aoserv-client.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Constants used in communication between the client and server.
 *
 * @author  AO Industries, Inc.
 */
public final class AOServProtocol extends GlobalObjectStringKey<AOServProtocol> {

	static final int COLUMN_VERSION = 0;
	static final String COLUMN_CREATED_name = "created";

	/**
	 * The current version of the client/server protocol.
	 */
	public enum Version {
		VERSION_1_0_A_100("1.0a100"),
		VERSION_1_0_A_101("1.0a101"),
		VERSION_1_0_A_102("1.0a102"),
		VERSION_1_0_A_103("1.0a103"),
		VERSION_1_0_A_104("1.0a104"),
		VERSION_1_0_A_105("1.0a105"),
		VERSION_1_0_A_106("1.0a106"),
		VERSION_1_0_A_107("1.0a107"),
		VERSION_1_0_A_108("1.0a108"),
		VERSION_1_0_A_109("1.0a109"),
		VERSION_1_0_A_110("1.0a110"),
		VERSION_1_0_A_111("1.0a111"),
		VERSION_1_0_A_112("1.0a112"),
		VERSION_1_0_A_113("1.0a113"),
		VERSION_1_0_A_114("1.0a114"),
		VERSION_1_0_A_115("1.0a115"),
		VERSION_1_0_A_116("1.0a116"),
		VERSION_1_0_A_117("1.0a117"),
		VERSION_1_0_A_118("1.0a118"),
		VERSION_1_0_A_119("1.0a119"),
		VERSION_1_0_A_120("1.0a120"),
		VERSION_1_0_A_121("1.0a121"),
		VERSION_1_0_A_122("1.0a122"),
		VERSION_1_0_A_123("1.0a123"),
		VERSION_1_0_A_124("1.0a124"),
		VERSION_1_0_A_125("1.0a125"),
		VERSION_1_0_A_126("1.0a126"),
		VERSION_1_0_A_127("1.0a127"),
		VERSION_1_0_A_128("1.0a128"),
		VERSION_1_0_A_129("1.0a129"),
		VERSION_1_0_A_130("1.0a130"),
		VERSION_1_1("1.1"),
		VERSION_1_2("1.2"),
		VERSION_1_3("1.3"),
		VERSION_1_4("1.4"),
		VERSION_1_5("1.5"),
		VERSION_1_6("1.6"),
		VERSION_1_7("1.7"),
		VERSION_1_8("1.8"),
		VERSION_1_9("1.9"),
		VERSION_1_10("1.10"),
		VERSION_1_11("1.11"),
		VERSION_1_12("1.12"),
		VERSION_1_13("1.13"),
		VERSION_1_14("1.14"),
		VERSION_1_15("1.15"),
		VERSION_1_16("1.16"),
		VERSION_1_17("1.17"),
		VERSION_1_18("1.18"),
		VERSION_1_19("1.19"),
		VERSION_1_20("1.20"),
		VERSION_1_21("1.21"),
		VERSION_1_22("1.22"),
		VERSION_1_23("1.23"),
		VERSION_1_24("1.24"),
		VERSION_1_25("1.25"),
		VERSION_1_26("1.26"),
		VERSION_1_27("1.27"),
		VERSION_1_28("1.28"),
		VERSION_1_29("1.29"),
		VERSION_1_30("1.30"),
		VERSION_1_31("1.31"),
		VERSION_1_32("1.32"),
		VERSION_1_33("1.33"),
		VERSION_1_34("1.34"),
		VERSION_1_35("1.35"),
		VERSION_1_36("1.36"),
		VERSION_1_37("1.37"),
		VERSION_1_38("1.38"),
		VERSION_1_39("1.39"),
		VERSION_1_40("1.40"),
		VERSION_1_41("1.41"),
		VERSION_1_42("1.42"),
		VERSION_1_43("1.43"),
		VERSION_1_44("1.44"),
		VERSION_1_45("1.45"),
		VERSION_1_46("1.46"),
		VERSION_1_47("1.47"),
		VERSION_1_48("1.48"),
		VERSION_1_49("1.49"),
		VERSION_1_50("1.50"),
		VERSION_1_51("1.51"),
		VERSION_1_52("1.52"),
		VERSION_1_53("1.53"),
		VERSION_1_54("1.54"),
		VERSION_1_55("1.55"),
		VERSION_1_56("1.56"),
		VERSION_1_57("1.57"),
		VERSION_1_58("1.58"),
		VERSION_1_59("1.59"),
		VERSION_1_60("1.60"),
		VERSION_1_61("1.61"),
		VERSION_1_62("1.62"),
		VERSION_1_63("1.63"),
		VERSION_1_64("1.64"),
		VERSION_1_65("1.65"),
		VERSION_1_66("1.66"),
		VERSION_1_67("1.67"),
		VERSION_1_68("1.68"),
		VERSION_1_69("1.69"),
		VERSION_1_70("1.70"),
		VERSION_1_71("1.71"),
		VERSION_1_72("1.72"),
		VERSION_1_73("1.73"),
		VERSION_1_74("1.74"),
		VERSION_1_75("1.75"),
		VERSION_1_76("1.76"),
		VERSION_1_77("1.77"),
		VERSION_1_78("1.78"),
		VERSION_1_79("1.79"),
		VERSION_1_80("1.80"),
		VERSION_1_80_0("1.80.0"),
		VERSION_1_80_1("1.80.1"),
		VERSION_1_80_2("1.80.2"),
		VERSION_1_81_0("1.81.0"),
		VERSION_1_81_1("1.81.1"),
		VERSION_1_81_2("1.81.2"),
		VERSION_1_81_3("1.81.3"),
		VERSION_1_81_4("1.81.4"),
		VERSION_1_81_5("1.81.5"),
		VERSION_1_81_6("1.81.6"),
		VERSION_1_81_7("1.81.7"),
		VERSION_1_81_8("1.81.8");

		public static final Version CURRENT_VERSION = VERSION_1_81_8;

		private static final Map<String,Version> versionMap = new HashMap<>();
		static {
			for(Version version : values()) versionMap.put(version.getVersion(), version);
		}

		/**
		 * Gets a specific version given its unique version string.
		 *
		 * @see  #getVersion()
		 *
		 * @throws  IllegalArgumentException if version not found
		 */
		public static Version getVersion(String version) {
			Version versionEnum = versionMap.get(version);
			if(versionEnum == null) throw new IllegalArgumentException("Version not found: " + version);
			return versionEnum;
		}

		private final String version;

		private Version(String version) {
			this.version = version;
		}

		public String getVersion() {
			return version;
		}

		@Override
		public String toString() {
			return version;
		}
	}

	public static final int
		NEXT = 0,
		DONE = 1,
		IO_EXCEPTION = 2,
		SQL_EXCEPTION = 3
	;

	public static final int
		FALSE = 0,
		TRUE = 1,
		SERVER_DOWN = 2
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
		@Deprecated
		UNUSED_BACKUP_INTERBASE_DATABASE, // No longer used
		@Deprecated
		UNUSED_BACKUP_MYSQL_DATABASE, // No longer used
		@Deprecated
		UNUSED_BACKUP_POSTGRES_DATABASE, // No longer used
		@Deprecated
		UNUSED_BOUNCE_TICKET, // No longer used
		CANCEL_BUSINESS,
		CHANGE_TICKET_ADMIN_PRIORITY,
		CHANGE_TICKET_CLIENT_PRIORITY,
		@Deprecated
		UNUSED_CHANGE_TICKET_DEADLINE, // No longer used
		@Deprecated
		UNUSED_CHANGE_TICKET_TECHNOLOGY, // No longer used
		CHANGE_TICKET_TYPE,
		COMPARE_LINUX_SERVER_ACCOUNT_PASSWORD,
		@Deprecated
		UNUSED_COMPLETE_TICKET, // No longer used
		COPY_HOME_DIRECTORY,
		COPY_LINUX_SERVER_ACCOUNT_PASSWORD,
		CREDIT_CARD_DECLINED,
		DISABLE,
		@Deprecated
		UNUSED_DUMP_INTERBASE_DATABASE, // No longer used
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
		@Deprecated
		UNUSED_GENERATE_INTERBASE_DATABASE_NAME, // No longer used
		@Deprecated
		UNUSED_GENERATE_INTERBASE_DB_GROUP_NAME, // No longer used
		GENERATE_MYSQL_DATABASE_NAME,
		GENERATE_PACKAGE_NAME,
		GENERATE_POSTGRES_DATABASE_NAME,
		GENERATE_SHARED_TOMCAT_NAME,
		GENERATE_SITE_NAME,
		GET_ACCOUNT_BALANCE_BEFORE,
		GET_ACCOUNT_BALANCE,
		@Deprecated
		UNUSED_GET_ACTIONS_TICKET, // No longer used
		GET_AUTORESPONDER_CONTENT,
		@Deprecated
		UNUSED_GET_BACKUP_DATA, // No longer used
		@Deprecated
		UNUSED_GET_BACKUP_DATA_PKEYS, // No longer used
		@Deprecated
		UNUSED_GET_BACKUP_DATAS_PKEYS, // No longer used
		GET_BACKUP_PARTITION_DISK_TOTAL_SIZE,
		GET_BACKUP_PARTITION_DISK_USED_SIZE,
		GET_BANK_TRANSACTIONS_ACCOUNT,
		GET_CACHED_ROW_COUNT,
		GET_CONFIRMED_ACCOUNT_BALANCE_BEFORE,
		GET_CONFIRMED_ACCOUNT_BALANCE,
		GET_CRON_TABLE,
		GET_EMAIL_LIST_ADDRESS_LIST,
		@Deprecated
		UNUSED_GET_FILE_BACKUPS_PKEYS, // No longer used
		@Deprecated
		UNUSED_GET_FILE_BACKUPS_SERVER, // No longer used
		@Deprecated
		UNUSED_GET_FILE_BACKUP_CHILDREN, // No longer used
		@Deprecated
		UNUSED_GET_FILE_BACKUP_SET_SERVER, // No longer used
		@Deprecated
		UNUSED_GET_FILE_BACKUP_VERSIONS, // No longer used
		@Deprecated
		UNUSED_GET_FILENAME_FOR_BACKUP_DATA, // No longer used
		GET_INBOX_ATTRIBUTES,
		GET_LATEST_FILE_BACKUP_SET,
		GET_MAJORDOMO_INFO_FILE,
		GET_MAJORDOMO_INTRO_FILE,
		GET_MRTG_FILE,
		GET_PENDING_PAYMENTS,
		GET_ROOT_BUSINESS,
		GET_SPAM_EMAIL_MESSAGES_FOR_EMAIL_SMTP_RELAY,
		@Deprecated
		UNUSED_GET_TICKETS_BUSINESS_ADMINISTRATOR, // No longer used
		@Deprecated
		UNUSED_GET_TICKETS_BUSINESS, // No longer used
		GET_TRANSACTIONS_BUSINESS,
		GET_TRANSACTIONS_SEARCH,
		@Deprecated
		UNUSED_HOLD_TICKET, // No longer used
		@Deprecated
		UNUSED_INITIALIZE_HTTPD_SITE_PASSWD_FILE, // No longer used
		IS_ACCOUNTING_AVAILABLE,
		IS_BUSINESS_ADMINISTRATOR_PASSWORD_SET,
		IS_DNS_ZONE_AVAILABLE,
		IS_EMAIL_DOMAIN_AVAILABLE,
		@Deprecated
		UNUSED_IS_INTERBASE_DATABASE_NAME_AVAILABLE, // No longer used
		@Deprecated
		UNUSED_IS_INTERBASE_DB_GROUP_NAME_AVAILABLE, // No longer used
		@Deprecated
		UNUSED_IS_INTERBASE_SERVER_USER_PASSWORD_SET, // No longer used
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
		@Deprecated
		UNUSED_KILL_TICKET, // No longer used
		MOVE_IP_ADDRESS,
		@Deprecated
		UNUSED_REACTIVATE_TICKET, // No longer used
		REFRESH_EMAIL_SMTP_RELAY,
		@Deprecated
		UNUSED_REMOVE_EXPIRED_FILE_BACKUPS, // No longer used
		@Deprecated
		UNUSED_REMOVE_EXPIRED_MYSQL_BACKUPS, // No longer used
		@Deprecated
		UNUSED_REMOVE_EXPIRED_POSTGRES_BACKUPS, // No longer used
		@Deprecated
		UNUSED_REMOVE_UNUSED_BACKUP_DATAS, // No longer used
		@Deprecated
		UNUSED_REQUEST_DAEMON_ACCESS, // No longer used
		RESTART_APACHE,
		RESTART_CRON,
		@Deprecated
		UNUSED_RESTART_INTERBASE, // No longer used
		RESTART_MYSQL,
		RESTART_POSTGRESQL,
		RESTART_XFS,
		RESTART_XVFB,
		@Deprecated
		UNUSED_SEND_BACKUP_DATA, // No longer used
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
		@Deprecated
		UNUSED_SET_INTERBASE_SERVER_USER_PASSWORD, // No longer used
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
		@Deprecated
		UNUSED_START_INTERBASE, // No longer used
		START_JVM,
		START_MYSQL,
		START_POSTGRESQL,
		START_XFS,
		START_XVFB,
		STOP_APACHE,
		STOP_CRON,
		@Deprecated
		UNUSED_STOP_INTERBASE, // No longer used
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
		@Deprecated
		UNUSED_SET_NET_BIND_OPEN_FIREWALL, // No longer used
		SET_NET_BIND_MONITORING,
		@Deprecated
		UNUSED_GET_BACKUP_DATAS_FOR_BACKUP_PARTITION, // No longer used
		@Deprecated
		UNUSED_REMOVE_EXPIRED_INTERBASE_BACKUPS, // No longer used
		@Deprecated
		UNUSED_SET_INTERBASE_SERVER_USER_PREDISABLE_PASSWORD, // No longer used
		GET_TRANSACTIONS_BUSINESS_ADMINISTRATOR,
		@Deprecated
		UNUSED_GET_ACTIONS_BUSINESS_ADMINISTRATOR, // No longer used
		@Deprecated
		UNUSED_GET_TICKETS_CREATED_BUSINESS_ADMINISTRATOR, // No longer used
		@Deprecated
		UNUSED_GET_TICKETS_CLOSED_BUSINESS_ADMINISTRATOR, // No longer used
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
		GET_AO_SERVER_MD_STAT_REPORT,
		GET_AO_SERVER_DRBD_REPORT,
		UPDATE_CREDIT_CARD_EXPIRATION,
		SET_FAILOVER_FILE_REPLICATION_BIT_RATE,
		SET_FAILOVER_FILE_SCHEDULES,
		SET_FILE_BACKUP_SETTINGS_ALL_AT_ONCE,
		CREDIT_CARD_TRANSACTION_AUTHORIZE_COMPLETED,
		GET_AO_SERVER_HDD_TEMP_REPORT,
		GET_AO_SERVER_FILESYSTEMS_CSV_REPORT,
		GET_AO_SERVER_LOADAVG_REPORT,
		GET_AO_SERVER_MEMINFO_REPORT,
		GET_NET_DEVICE_STATISTICS_REPORT,
		SET_LINUX_SERVER_ACCOUNT_SPAMASSASSIN_DISCARD_SCORE,
		GET_AO_SERVER_SYSTEM_TIME_MILLIS,
		GET_AO_SERVER_LVM_REPORT,
		GET_AO_SERVER_HDD_MODEL_REPORT,
		GET_TICKET_DETAILS,
		GET_TICKET_RAW_EMAIL,
		GET_TICKET_INTERNAL_NOTES,
		GET_TICKET_ACTION_OLD_VALUE,
		GET_TICKET_ACTION_DETAILS,
		GET_TICKET_ACTION_RAW_EMAIL,
		GET_TICKET_ACTION_NEW_VALUE,
		SET_TICKET_SUMMARY,
		ADD_TICKET_ANNOTATION,
		SET_TICKET_STATUS,
		SET_TICKET_INTERNAL_NOTES,
		REQUEST_REPLICATION_DAEMON_ACCESS,
		REQUEST_VNC_CONSOLE_DAEMON_ACCESS,
		GET_MYSQL_TABLE_STATUS,
		CHECK_MYSQL_TABLES,
		AO_SERVER_CHECK_PORT,
		AO_SERVER_CHECK_SMTP_BLACKLIST,
		GET_UPS_STATUS,
		CREATE_VIRTUAL_SERVER,
		REBOOT_VIRTUAL_SERVER,
		SHUTDOWN_VIRTUAL_SERVER,
		DESTROY_VIRTUAL_SERVER,
		PAUSE_VIRTUAL_SERVER,
		UNPAUSE_VIRTUAL_SERVER,
		GET_VIRTUAL_SERVER_STATUS,
		ADD_IP_REPUTATION,
		GET_AO_SERVER_MD_MISMATCH_REPORT,
		VERIFY_VIRTUAL_DISK,
		GET_PRIMARY_PHYSICAL_SERVER,
		GET_SECONDARY_PHYSICAL_SERVER,
		GET_FAILOVER_FILE_REPLICATION_ACTIVITY,
		ADD_SYSTEM_GROUP,
		ADD_SYSTEM_USER,
		SET_HTTPD_SITE_PHP_VERSION,
		SET_HTTPD_SITE_ENABLE_CGI,
		SET_HTTPD_SITE_ENABLE_SSI,
		SET_HTTPD_SITE_ENABLE_HTACCESS,
		SET_HTTPD_SITE_ENABLE_INDEXES,
		SET_HTTPD_SITE_ENABLE_FOLLOW_SYMLINKS,
		SET_HTTPD_SITE_ENABLE_ANONYMOUS_FTP,
		@Deprecated
		UNUSED_SET_HTTPD_TOMCAT_SITE_USE_APACHE, // No longer used
		SET_HTTPD_SHARED_TOMCAT_MAX_POST_SIZE,
		SET_HTTPD_SHARED_TOMCAT_UNPACK_WARS,
		SET_HTTPD_SHARED_TOMCAT_AUTO_DEPLOY,
		SET_HTTPD_TOMCAT_STD_SITE_MAX_POST_SIZE,
		SET_HTTPD_TOMCAT_STD_SITE_UNPACK_WARS,
		SET_HTTPD_TOMCAT_STD_SITE_AUTO_DEPLOY,
		SET_NET_BIND_FIREWALLD_ZONES,
		SET_HTTPD_SITE_BLOCK_TRACE_TRACK,
		SET_HTTPD_SITE_BLOCK_SCM,
		SET_HTTPD_SITE_BLOCK_CORE_DUMPS,
		SET_HTTPD_SITE_BLOCK_EDITOR_BACKUPS,
		SET_HTTPD_TOMCAT_SITE_BLOCK_WEBINF
	}

	/**
	 * Indicates that a field was filtered by the server.
	 */
	public static final String FILTERED="*";

	static void checkResult(int code, CompressedDataInputStream in) throws IOException, SQLException {
		if(in == null) throw new IllegalArgumentException("in is null");
		if(code == AOServProtocol.IO_EXCEPTION) throw new IOException(in.readUTF());
		if(code == AOServProtocol.SQL_EXCEPTION) throw new SQLException(in.readUTF());
		if(code == -1) throw new EOFException("End of file while reading response code");
		if(code != AOServProtocol.DONE) throw new IOException("Unknown status code: "+code);
	}

	private long created;
	private String comments;
	private long last_used;

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_VERSION: return pkey;
			case 1: return getCreated();
			case 2: return comments;
			case 3: return getLastUsed();
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public String getVersion() {
		return pkey;
	}

	public Date getCreated() {
		return new Date(created);
	}

	public String getComments() {
		return comments;
	}

	public Date getLastUsed() {
		return last_used==-1 ? null : new Date(last_used);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.AOSERV_PROTOCOLS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey = result.getString(1);
		created = result.getDate(2).getTime();
		comments = result.getString(3);
		Date D = result.getDate(4);
		last_used = D == null ? -1 : D.getTime();
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey = in.readUTF().intern();
		created = in.readLong();
		comments = in.readUTF();
		last_used = in.readLong();
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version protocolVersion) throws IOException {
		out.writeUTF(pkey);
		out.writeLong(created);
		out.writeUTF(comments);
		if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_9) >= 0) {
			out.writeLong(last_used);
		}
	}
}
