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
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.util.InternUtils;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Information about every command in the <code>AOSH</code> is
 * available through the use of <code>AOSHCommand<code>s.
 *
 * @see AOSH
 *
 * @author  AO Industries, Inc.
 */
final public class AOSHCommand extends GlobalObjectStringKey<AOSHCommand> {

	static final int COLUMN_COMMAND = 0;
	static final String COLUMN_COMMAND_name = "command";

	/**
	 * All of the commands for the <code>AOSH</code>.
	 *
	 * @see  AOSH
	 */
	public static final String
		ADD_BACKUP_SERVER = "add_backup_server",
		ADD_BUSINESS = "add_business",
		ADD_BUSINESS_ADMINISTRATOR = "add_business_administrator",
		ADD_BUSINESS_PROFILE = "add_business_profile",
		ADD_BUSINESS_SERVER = "add_business_server",
		ADD_CVS_REPOSITORY = "add_cvs_repository",
		ADD_DNS_RECORD = "add_dns_record",
		ADD_DNS_ZONE = "add_dns_zone",
		ADD_EMAIL_ADDRESS = "add_email_address",
		ADD_EMAIL_DOMAIN = "add_email_domain",
		ADD_EMAIL_FORWARDING = "add_email_forwarding",
		ADD_EMAIL_LIST = "add_email_list",
		ADD_EMAIL_LIST_ADDRESS = "add_email_list_address",
		ADD_EMAIL_PIPE_ADDRESS = "add_email_pipe_address",
		ADD_EMAIL_PIPE = "add_email_pipe",
		ADD_EMAIL_SMTP_RELAY = "add_email_smtp_relay",
		ADD_FILE_BACKUP_SETTING = "add_file_backup_setting",
		ADD_FTP_GUEST_USER = "add_ftp_guest_user",
		ADD_HTTPD_JBOSS_SITE = "add_httpd_jboss_site",
		ADD_HTTPD_SHARED_TOMCAT = "add_httpd_shared_tomcat",
		ADD_HTTPD_SITE_AUTHENTICATED_LOCATION = "add_httpd_site_authenticated_location",
		ADD_HTTPD_SITE_URL = "add_httpd_site_url",
		ADD_HTTPD_TOMCAT_CONTEXT = "add_httpd_tomcat_context",
		ADD_HTTPD_TOMCAT_DATA_SOURCE = "add_httpd_tomcat_data_source",
		ADD_HTTPD_TOMCAT_PARAMETER = "add_httpd_tomcat_parameter",
		ADD_HTTPD_TOMCAT_SITE_JK_MOUNT = "add_httpd_tomcat_site_jk_mount",
		ADD_HTTPD_TOMCAT_SHARED_SITE = "add_httpd_tomcat_shared_site",
		ADD_HTTPD_TOMCAT_STD_SITE = "add_httpd_tomcat_std_site",
		ADD_INCOMING_PAYMENT = "add_incoming_payment",
		ADD_IP_REPUTATION = "add_ip_reputation",
		ADD_LINUX_ACC_ADDRESS = "add_linux_acc_address",
		ADD_LINUX_ACCOUNT = "add_linux_account",
		ADD_LINUX_GROUP = "add_linux_group",
		ADD_LINUX_GROUP_ACCOUNT = "add_linux_group_account",
		ADD_LINUX_SERVER_ACCOUNT = "add_linux_server_account",
		ADD_LINUX_SERVER_GROUP = "add_linux_server_group",
		ADD_MAJORDOMO_LIST = "add_majordomo_list",
		ADD_MAJORDOMO_SERVER = "add_majordomo_server",
		ADD_MYSQL_DATABASE = "add_mysql_database",
		ADD_MYSQL_DB_USER = "add_mysql_db_user",
		ADD_MYSQL_SERVER_USER = "add_mysql_server_user",
		ADD_MYSQL_USER = "add_mysql_user",
		ADD_NET_BIND = "add_net_bind",
		ADD_NOTICE_LOG = "add_notice_log",
		ADD_PACKAGE = "add_package",
		ADD_POSTGRES_DATABASE = "add_postgres_database",
		ADD_POSTGRES_SERVER_USER = "add_postgres_server_user",
		ADD_POSTGRES_USER = "add_postgres_user",
		ADD_SPAM_EMAIL_MESSAGE = "add_spam_email_message",
		//ADD_TICKET = "add_ticket",
		//ADD_TICKET_WORK = "add_ticket_work",
		ADD_TRANSACTION = "add_transaction",
		ADD_USERNAME = "add_username",
		ARE_LINUX_ACCOUNT_PASSWORDS_SET = "are_linux_account_passwords_set",
		ARE_MYSQL_USER_PASSWORDS_SET = "are_mysql_user_passwords_set",
		ARE_POSTGRES_USER_PASSWORDS_SET = "are_postgres_user_passwords_set",
		ARE_USERNAME_PASSWORDS_SET = "are_username_passwords_set",
		//BOUNCE_TICKET = "bounce_ticket",
		CANCEL_BUSINESS = "cancel_business",
		//CHANGE_TICKET_ADMIN_PRIORITY = "change_ticket_admin_priority",
		//CHANGE_TICKET_CLIENT_PRIORITY = "change_ticket_client_priority",
		//CHANGE_TICKET_DEADLINE = "change_ticket_deadline",
		//CHANGE_TICKET_TECHNOLOGY = "change_ticket_technology",
		//CHANGE_TICKET_TYPE = "change_ticket_type",
		CHECK_ACCOUNTING = "check_accounting",
		CHECK_BUSINESS_ADMINISTRATOR_PASSWORD = "check_business_administrator_password",
		//CHECK_BUSINESS_ADMINISTRATOR_USERNAME = "check_business_administrator_username",
		CHECK_DNS_ZONE = "check_dns_zone",
		CHECK_EMAIL_ADDRESS = "check_email_address",
		CHECK_EMAIL_DOMAIN = "check_email_domain",
		//CHECK_EMAIL_FORWARDING = "check_email_forwarding",
		CHECK_EMAIL_LIST_PATH = "check_email_list_path",
		//CHECK_IP_ADDRESS = "check_ip_address",
		//CHECK_LINUX_ACCOUNT_USERNAME = "check_linux_account_username",
		CHECK_LINUX_ACCOUNT_NAME = "check_linux_account_name",
		CHECK_LINUX_ACCOUNT_PASSWORD = "check_linux_account_password",
		CHECK_LINUX_GROUP_NAME = "check_linux_group_name",
		CHECK_MAJORDOMO_LIST_NAME = "check_majordomo_list_name",
		CHECK_MYSQL_DATABASE_NAME = "check_mysql_database_name",
		CHECK_MYSQL_PASSWORD = "check_mysql_user_password",
		CHECK_MYSQL_SERVER_NAME = "check_mysql_server_name",
		CHECK_MYSQL_USERNAME = "check_mysql_username",
		//CHECK_PACKAGE_NAME = "check_package_name",
		CHECK_POSTGRES_DATABASE_NAME = "check_postgres_database_name",
		CHECK_POSTGRES_PASSWORD = "check_postgres_password",
		CHECK_POSTGRES_SERVER_NAME = "check_postgres_server_name",
		CHECK_POSTGRES_USERNAME = "check_postgres_username",
		CHECK_SHARED_TOMCAT_NAME = "check_shared_tomcat_name",
		CHECK_SITE_NAME = "check_site_name",
		CHECK_SSL_CERTIFICATE = "check_ssl_certificate",
		CHECK_USERNAME = "check_username",
		CHECK_USERNAME_PASSWORD = "check_username_password",
		CLEAR = "clear",
		COMPARE_LINUX_SERVER_ACCOUNT_PASSWORD = "compare_linux_server_account_password",
		//COMPLETE_TICKET = "complete_ticket",
		COPY_HOME_DIRECTORY = "copy_home_directory",
		COPY_LINUX_SERVER_ACCOUNT_PASSWORD = "copy_linux_server_account_password",
		CREATE_VIRTUAL_SERVER = "create_virtual_server",
		CRYPT = "crypt",
		DECLINE_CREDIT_CARD = "decline_credit_card",
		DESC = "desc",
		DESCRIBE = "describe",
		DESTROY_VIRTUAL_SERVER = "destroy_virtual_server",
		DISABLE_BUSINESS = "disable_business",
		DISABLE_BUSINESS_ADMINISTRATOR = "disable_business_administrator",
		DISABLE_CVS_REPOSITORY = "disable_cvs_repository",
		DISABLE_EMAIL_LIST = "disable_email_list",
		DISABLE_EMAIL_PIPE = "disable_email_pipe",
		DISABLE_EMAIL_SMTP_RELAY = "disable_email_smtp_relay",
		DISABLE_HTTPD_SHARED_TOMCAT = "disable_httpd_shared_tomcat",
		DISABLE_HTTPD_SITE = "disable_httpd_site",
		DISABLE_HTTPD_SITE_BIND = "disable_httpd_site_bind",
		DISABLE_LINUX_ACCOUNT = "disable_linux_account",
		DISABLE_LINUX_SERVER_ACCOUNT = "disable_linux_server_account",
		DISABLE_MYSQL_SERVER_USER = "disable_mysql_server_user",
		DISABLE_MYSQL_USER = "disable_mysql_user",
		DISABLE_PACKAGE = "disable_package",
		DISABLE_POSTGRES_SERVER_USER = "disable_postgres_server_user",
		DISABLE_POSTGRES_USER = "disable_postgres_user",
		DISABLE_USERNAME = "disable_username",
		DUMP_MYSQL_DATABASE = "dump_mysql_database",
		DUMP_POSTGRES_DATABASE = "dump_postgres_database",
		ECHO = "echo",
		ENABLE_BUSINESS = "enable_business",
		ENABLE_BUSINESS_ADMINISTRATOR = "enable_business_administrator",
		ENABLE_CVS_REPOSITORY = "enable_cvs_repository",
		ENABLE_EMAIL_LIST = "enable_email_list",
		ENABLE_EMAIL_PIPE = "enable_email_pipe",
		ENABLE_EMAIL_SMTP_RELAY = "enable_email_smtp_relay",
		ENABLE_HTTPD_SHARED_TOMCAT = "enable_httpd_shared_tomcat",
		ENABLE_HTTPD_SITE = "enable_httpd_site",
		ENABLE_HTTPD_SITE_BIND = "enable_httpd_site_bind",
		ENABLE_LINUX_ACCOUNT = "enable_linux_account",
		ENABLE_LINUX_SERVER_ACCOUNT = "enable_linux_server_account",
		ENABLE_MYSQL_SERVER_USER = "enable_mysql_server_user",
		ENABLE_MYSQL_USER = "enable_mysql_user",
		ENABLE_PACKAGE = "enable_package",
		ENABLE_POSTGRES_SERVER_USER = "enable_postgres_server_user",
		ENABLE_POSTGRES_USER = "enable_postgres_user",
		ENABLE_USERNAME = "enable_username",
		EXIT = "exit",
		GENERATE_ACCOUNTING = "generate_accounting",
		GENERATE_MYSQL_DATABASE_NAME = "generate_mysql_database_name",
		GENERATE_PACKAGE_NAME = "generate_package_name",
		GENERATE_PASSWORD = "generate_password",
		GENERATE_POSTGRES_DATABASE_NAME = "generate_postgres_database_name",
		GENERATE_SHARED_TOMCAT_NAME = "generate_shared_tomcat_name",
		GENERATE_SITE_NAME = "generate_site_name",
		GET_AUTORESPONDER_CONTENT = "get_autoresponder_content",
		GET_AWSTATS_FILE = "get_awstats_file",
		GET_BACKUP_PARTITION_TOTAL_SIZE = "get_backup_partition_total_size",
		GET_BACKUP_PARTITION_USED_SIZE = "get_backup_partition_used_size",
		GET_CRON_TABLE = "get_cron_table",
		GET_EMAIL_LIST = "get_email_list",
		GET_FAILOVER_FILE_REPLICATION_ACTIVITY = "get_failover_file_replication_activity",
		GET_HTTPD_SERVER_CONCURRENCY = "get_httpd_server_concurrency",
		GET_IMAP_FOLDER_SIZES = "get_imap_folder_sizes",
		GET_INBOX_ATTRIBUTES = "get_inbox_attributes",
		GET_MAJORDOMO_INFO_FILE = "get_majordomo_info_file",
		GET_MAJORDOMO_INTRO_FILE = "get_majordomo_intro_file",
		GET_MRTG_FILE = "get_mrtg_file",
		GET_PRIMARY_PHYSICAL_SERVER = "get_primary_physical_server",
		GET_SECONDARY_PHYSICAL_SERVER = "get_secondary_physical_server",
		GET_ROOT_BUSINESS = "get_root_business",
		GET_UPS_STATUS = "get_ups_status",
		GET_VIRTUAL_SERVER_STATUS = "get_virtual_server_status",
		HELP = "help",
		//HOLD_TICKET = "hold_ticket",
		//INITIALIZE_HTTPD_SITE_PASSWD_FILE = "initialize_httpd_site_passwd_file",
		INVALIDATE = "invalidate",
		IS_ACCOUNTING_AVAILABLE = "is_accounting_available",
		IS_BUSINESS_ADMINISTRATOR_PASSWORD_SET = "is_business_administrator_password_set",
		IS_DNS_ZONE_AVAILABLE = "is_dns_zone_available",
		IS_EMAIL_DOMAIN_AVAILABLE = "is_email_domain_available",
		IS_IP_ADDRESS_USED = "is_ip_address_used",
		IS_LINUX_GROUP_NAME_AVAILABLE = "is_linux_group_name_available",
		IS_LINUX_SERVER_ACCOUNT_PROCMAIL_MANUAL = "is_linux_server_account_procmail_manual",
		IS_LINUX_SERVER_ACCOUNT_PASSWORD_SET = "is_linux_server_account_password_set",
		IS_MYSQL_DATABASE_NAME_AVAILABLE = "is_mysql_database_name_available",
		IS_MYSQL_SERVER_NAME_AVAILABLE = "is_mysql_server_name_available",
		IS_MYSQL_SERVER_USER_PASSWORD_SET = "is_mysql_server_user_password_set",
		IS_PACKAGE_NAME_AVAILABLE = "is_package_name_available",
		IS_POSTGRES_DATABASE_NAME_AVAILABLE = "is_postgres_database_name_available",
		IS_POSTGRES_SERVER_NAME_AVAILABLE = "is_postgres_server_name_available",
		IS_POSTGRES_SERVER_USER_PASSWORD_SET = "is_postgres_server_user_password_set",
		IS_SHARED_TOMCAT_NAME_AVAILABLE = "is_shared_tomcat_name_available",
		IS_SITE_NAME_AVAILABLE = "is_site_name_available",
		IS_USERNAME_AVAILABLE = "is_username_available",
		//KILL_TICKET = "kill_ticket",
		JOBS = "jobs",
		MOVE_BUSINESS = "move_business",
		MOVE_IP_ADDRESS = "move_ip_address",
		PAUSE_VIRTUAL_SERVER = "pause_virtual_server",
		PING = "ping",
		PRINT_ZONE_FILE = "print_zone_file",
		//REACTIVATE_TICKET = "reactivate_ticket",
		REBOOT_VIRTUAL_SERVER = "reboot_virtual_server",
		REFRESH_EMAIL_SMTP_RELAY = "refresh_email_smtp_relay",
		REMOVE_BLACKHOLE_EMAIL_ADDRESS = "remove_blackhole_email_address",
		REMOVE_BUSINESS_ADMINISTRATOR = "remove_business_administrator",
		REMOVE_BUSINESS_SERVER = "remove_business_server",
		REMOVE_CREDIT_CARD = "remove_credit_card",
		REMOVE_CVS_REPOSITORY = "remove_cvs_repository",
		REMOVE_DNS_RECORD = "remove_dns_record",
		REMOVE_DNS_ZONE = "remove_dns_zone",
		REMOVE_EMAIL_ADDRESS = "remove_email_address",
		REMOVE_EMAIL_DOMAIN = "remove_email_domain",
		REMOVE_EMAIL_FORWARDING = "remove_email_forwarding",
		REMOVE_EMAIL_LIST = "remove_email_list",
		REMOVE_EMAIL_LIST_ADDRESS = "remove_email_list_address",
		REMOVE_EMAIL_PIPE = "remove_email_pipe",
		REMOVE_EMAIL_PIPE_ADDRESS = "remove_email_pipe_address",
		REMOVE_EMAIL_SMTP_RELAY = "remove_email_smtp_relay",
		REMOVE_FILE_BACKUP_SETTING = "remove_file_backup_setting",
		REMOVE_FTP_GUEST_USER = "remove_ftp_guest_user",
		REMOVE_HTTPD_SHARED_TOMCAT = "remove_httpd_shared_tomcat",
		REMOVE_HTTPD_SITE = "remove_httpd_site",
		REMOVE_HTTPD_SITE_URL = "remove_httpd_site_url",
		REMOVE_HTTPD_TOMCAT_CONTEXT = "remove_httpd_tomcat_context",
		REMOVE_HTTPD_TOMCAT_DATA_SOURCE = "remove_httpd_tomcat_data_source",
		REMOVE_HTTPD_TOMCAT_PARAMETER = "remove_httpd_tomcat_parameter",
		REMOVE_HTTPD_TOMCAT_SITE_JK_MOUNT = "remove_httpd_tomcat_site_jk_mount",
		REMOVE_INCOMING_PAYMENT = "remove_incoming_payment",
		REMOVE_LINUX_ACC_ADDRESS = "remove_linux_acc_address",
		REMOVE_LINUX_ACCOUNT = "remove_linux_account",
		REMOVE_LINUX_GROUP = "remove_linux_group",
		REMOVE_LINUX_GROUP_ACCOUNT = "remove_linux_group_account",
		REMOVE_LINUX_SERVER_ACCOUNT = "remove_linux_server_account",
		REMOVE_LINUX_SERVER_GROUP = "remove_linux_server_group",
		REMOVE_MAJORDOMO_SERVER = "remove_majordomo_server",
		REMOVE_MYSQL_DATABASE = "remove_mysql_database",
		REMOVE_MYSQL_DB_USER = "remove_mysql_db_user",
		REMOVE_MYSQL_SERVER_USER = "remove_mysql_server_user",
		REMOVE_MYSQL_USER = "remove_mysql_user",
		REMOVE_NET_BIND = "remove_net_bind",
		REMOVE_POSTGRES_DATABASE = "remove_postgres_database",
		REMOVE_POSTGRES_SERVER_USER = "remove_postgres_server_user",
		REMOVE_POSTGRES_USER = "remove_postgres_user",
		REMOVE_USERNAME = "remove_username",
		REPEAT = "repeat",
		RESTART_APACHE = "restart_apache",
		RESTART_CRON = "restart_cron",
		RESTART_MYSQL = "restart_mysql",
		RESTART_POSTGRESQL = "restart_postgresql",
		RESTART_XFS = "restart_xfs",
		RESTART_XVFB = "restart_xvfb",
		SELECT = "select",
		SET_AUTORESPONDER = "set_autoresponder",
		SET_BUSINESS_ACCOUNTING = "set_business_accounting",
		SET_BUSINESS_ADMINISTRATOR_PASSWORD = "set_business_administrator_password",
		SET_BUSINESS_ADMINISTRATOR_PROFILE = "set_business_administrator_profile",
		SET_CRON_TABLE = "set_cron_table",
		SET_CVS_REPOSITORY_MODE = "set_cvs_repository_mode",
		SET_DEFAULT_BUSINESS_SERVER = "set_default_business_server",
		SET_DNS_ZONE_TTL = "set_dns_zone_ttl",
		SET_EMAIL_LIST = "set_email_list",
		SET_FILE_BACKUP_SETTING = "set_file_backup_setting",
		SET_HTTPD_SHARED_TOMCAT_IS_MANUAL = "set_httpd_shared_tomcat_is_manual",
		SET_HTTPD_SHARED_TOMCAT_MAX_POST_SIZE = "set_httpd_shared_tomcat_max_post_size",
		SET_HTTPD_SHARED_TOMCAT_UNPACK_WARS = "set_httpd_shared_tomcat_unpack_wars",
		SET_HTTPD_SHARED_TOMCAT_AUTO_DEPLOY = "set_httpd_shared_tomcat_auto_deploy",
		SET_HTTPD_SHARED_TOMCAT_VERSION = "set_httpd_shared_tomcat_version",
		SET_HTTPD_SITE_AUTHENTICATED_LOCATION_ATTRIBUTES = "set_httpd_site_authenticated_location_attributes",
		SET_HTTPD_SITE_BIND_IS_MANUAL = "set_httpd_site_bind_is_manual",
		SET_HTTPD_SITE_BIND_REDIRECT_TO_PRIMARY_HOSTNAME = "set_httpd_site_bind_redirect_to_primary_hostname",
		SET_HTTPD_SITE_IS_MANUAL = "set_httpd_site_is_manual",
		SET_HTTPD_SITE_SERVER_ADMIN = "set_httpd_site_server_admin",
		SET_HTTPD_SITE_PHP_VERSION = "set_httpd_site_php_version",
		SET_HTTPD_SITE_ENABLE_CGI = "set_httpd_site_enable_cgi",
		SET_HTTPD_SITE_ENABLE_SSI = "set_httpd_site_enable_ssi",
		SET_HTTPD_SITE_ENABLE_HTACCESS = "set_httpd_site_enable_htaccess",
		SET_HTTPD_SITE_ENABLE_INDEXES = "set_httpd_site_enable_indexes",
		SET_HTTPD_SITE_ENABLE_FOLLOW_SYMLINKS = "set_httpd_site_enable_follow_symlinks",
		SET_HTTPD_SITE_ENABLE_ANONYMOUS_FTP = "set_httpd_site_enable_anonymous_ftp",
		SET_HTTPD_SITE_BLOCK_TRACE_TRACK = "set_httpd_site_block_trace_track",
		SET_HTTPD_SITE_BLOCK_SCM = "set_httpd_site_block_scm",
		SET_HTTPD_SITE_BLOCK_CORE_DUMPS = "set_httpd_site_block_core_dumps",
		SET_HTTPD_SITE_BLOCK_EDITOR_BACKUPS = "set_httpd_site_block_editor_backups",
		SET_HTTPD_TOMCAT_CONTEXT_ATTRIBUTES = "set_httpd_tomcat_context_attributes",
		SET_HTTPD_TOMCAT_STD_SITE_MAX_POST_SIZE = "set_httpd_tomcat_std_site_max_post_size",
		SET_HTTPD_TOMCAT_STD_SITE_UNPACK_WARS = "set_httpd_tomcat_std_site_unpack_wars",
		SET_HTTPD_TOMCAT_STD_SITE_AUTO_DEPLOY = "set_httpd_tomcat_std_site_auto_deploy",
		SET_HTTPD_TOMCAT_STD_SITE_VERSION = "set_httpd_tomcat_std_site_version",
		SET_HTTPD_TOMCAT_SITE_BLOCK_WEBINF = "set_httpd_tomcat_site_block_webinf",
		SET_IP_ADDRESS_DHCP_ADDRESS = "set_ip_address_dhcp_address",
		SET_IP_ADDRESS_HOSTNAME = "set_ip_address_hostname",
		SET_IP_ADDRESS_MONITORING_ENABLED = "set_ip_address_monitoring_enabled",
		SET_IP_ADDRESS_PACKAGE = "set_ip_address_package",
		SET_LINUX_ACCOUNT_HOME_PHONE = "set_linux_account_home_phone",
		SET_LINUX_ACCOUNT_NAME = "set_linux_account_name",
		SET_LINUX_ACCOUNT_OFFICE_LOCATION = "set_linux_account_office_location",
		SET_LINUX_ACCOUNT_OFFICE_PHONE = "set_linux_account_office_phone",
		SET_LINUX_ACCOUNT_PASSWORD = "set_linux_account_password",
		SET_LINUX_ACCOUNT_SHELL = "set_linux_account_shell",
		SET_LINUX_SERVER_ACCOUNT_JUNK_EMAIL_RETENTION = "set_linux_server_account_junk_email_retention",
		SET_LINUX_SERVER_ACCOUNT_PASSWORD = "set_linux_server_account_password",
		SET_LINUX_SERVER_ACCOUNT_SPAMASSASSIN_INTEGRATION_MODE = "set_linux_server_account_spamassassin_integration_mode",
		SET_LINUX_SERVER_ACCOUNT_SPAMASSASSIN_REQUIRED_SCORE = "set_linux_server_account_spamassassin_required_score",
		SET_LINUX_SERVER_ACCOUNT_TRASH_EMAIL_RETENTION = "set_linux_server_account_trash_email_retention",
		SET_LINUX_SERVER_ACCOUNT_USE_INBOX = "set_linux_server_account_use_inbox",
		SET_MAJORDOMO_INFO_FILE = "set_majordomo_info_file",
		SET_MAJORDOMO_INTRO_FILE = "set_majordomo_intro_file",
		SET_MYSQL_SERVER_USER_PASSWORD = "set_mysql_server_user_password",
		SET_MYSQL_USER_PASSWORD = "set_mysql_user_password",
		SET_NET_BIND_FIREWALLD_ZONES = "set_net_bind_firewalld_zones",
		SET_NET_BIND_MONITORING_ENABLED = "set_net_bind_monitoring_enabled",
		SET_POSTGRES_SERVER_USER_PASSWORD = "set_postgres_server_user_password",
		SET_POSTGRES_USER_PASSWORD = "set_postgres_user_password",
		SET_PRIMARY_HTTPD_SITE_URL = "set_primary_httpd_site_url",
		SET_PRIMARY_LINUX_GROUP_ACCOUNT = "set_primary_linux_group_account",
		SET_USERNAME_PASSWORD = "set_username_password",
		SHUTDOWN_VIRTUAL_SERVER = "shutdown_virtual_server",
		SHOW = "show",
		SLEEP = "sleep",
		START_APACHE = "start_apache",
		START_CRON = "start_cron",
		START_DISTRO = "start_distro",
		START_JVM = "start_jvm",
		START_MYSQL = "start_mysql",
		START_POSTGRESQL = "start_postgresql",
		START_XFS = "start_xfs",
		START_XVFB = "start_xvfb",
		STOP_APACHE = "stop_apache",
		STOP_CRON = "stop_cron",
		STOP_JVM = "stop_jvm",
		STOP_MYSQL = "stop_mysql",
		STOP_POSTGRESQL = "stop_postgresql",
		STOP_XFS = "stop_xfs",
		STOP_XVFB = "stop_xvfb",
		SU = "su",
		TIME = "time",
		UNPAUSE_VIRTUAL_SERVER = "unpause_virtual_server",
		UPDATE_HTTPD_TOMCAT_DATA_SOURCE = "update_httpd_tomcat_data_source",
		UPDATE_HTTPD_TOMCAT_PARAMETER = "update_httpd_tomcat_parameter",
		VERIFY_VIRTUAL_DISK = "verify_virtual_disk",
		WAIT_FOR_HTTPD_SITE_REBUILD = "wait_for_httpd_site_rebuild",
		WAIT_FOR_LINUX_ACCOUNT_REBUILD = "wait_for_linux_account_rebuild",
		WAIT_FOR_MYSQL_DATABASE_REBUILD = "wait_for_mysql_database_rebuild",
		WAIT_FOR_MYSQL_DB_USER_REBUILD = "wait_for_mysql_db_user_rebuild",
		WAIT_FOR_MYSQL_HOST_REBUILD = "wait_for_mysql_host_rebuild",
		WAIT_FOR_MYSQL_SERVER_REBUILD = "wait_for_mysql_server_rebuild",
		WAIT_FOR_MYSQL_USER_REBUILD = "wait_for_mysql_user_rebuild",
		WAIT_FOR_POSTGRES_DATABASE_REBUILD = "wait_for_postgres_database_rebuild",
		WAIT_FOR_POSTGRES_SERVER_REBUILD = "wait_for_postgres_server_rebuild",
		WAIT_FOR_POSTGRES_USER_REBUILD = "wait_for_postgres_user_rebuild",
		WHOAMI = "whoami"
	;

	private String sinceVersion;
	private String lastVersion;
	private String table;
	private String description;
	private String syntax;

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_COMMAND: return pkey;
			case 1: return sinceVersion;
			case 2: return lastVersion;
			case 3: return table;
			case 4: return description;
			case 5: return syntax;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	public String getCommand() {
		return pkey;
	}

	public String getSinceVersion_version() {
		return sinceVersion;
	}

	public AOServProtocol getSinceVersion(AOServConnector connector) throws SQLException, IOException {
		AOServProtocol obj = connector.getAoservProtocols().get(sinceVersion);
		if(obj == null) throw new SQLException("Unable to find AOServProtocol: " + sinceVersion);
		return obj;
	}

	public String getLastVersion_version() {
		return lastVersion;
	}

	public AOServProtocol getLastVersion(AOServConnector connector) throws SQLException, IOException {
		if(lastVersion == null) return null;
		AOServProtocol obj = connector.getAoservProtocols().get(lastVersion);
		if(obj == null) throw new SQLException("Unable to find AOServProtocol: " + lastVersion);
		return obj;
	}

	public String getTable_name() {
		return table;
	}

	public SchemaTable getTable(AOServConnector connector) throws SQLException, IOException {
		if(table == null) return null;
		SchemaTable obj = connector.getSchemaTables().get(table);
		if(obj == null) throw new SQLException("Unable to find SchemaTable: " + table);
		return obj;
	}

	public String getDescription() {
		return description;
	}

	public String getSyntax() {
		return syntax;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.AOSH_COMMANDS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		int pos = 1;
		pkey = result.getString(pos++);
		sinceVersion = result.getString(pos++);
		lastVersion = result.getString(pos++);
		table = result.getString(pos++);
		description = result.getString(pos++);
		syntax = result.getString(pos++);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey = in.readUTF().intern();
		sinceVersion = in.readUTF().intern();
		lastVersion = InternUtils.intern(in.readNullUTF());
		table = InternUtils.intern(in.readNullUTF());
		description = in.readUTF();
		syntax = in.readUTF();
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version protocolVersion) throws IOException {
		out.writeUTF(pkey);
		if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_81_17) <= 0) {
			out.writeNullUTF(table);
			out.writeUTF(description);
			out.writeUTF(syntax);
			if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_0_A_101) >= 0) out.writeUTF(sinceVersion);
			if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_0_A_102) >= 0) out.writeNullUTF(lastVersion);
		} else {
			out.writeUTF(sinceVersion);
			out.writeNullUTF(lastVersion);
			out.writeNullUTF(table);
			out.writeUTF(description);
			out.writeUTF(syntax);
		}
	}

	public void printCommandHelp(TerminalWriter out) throws IOException {
		out.println();
		out.boldOn();
		out.println("NAME");
		out.attributesOff();
		out.print("       "); out.print(pkey); out.print(" - "); printNoHTML(out, description); out.println();
		out.println();
		out.boldOn();
		out.println("SYNOPSIS");
		out.attributesOff();
		out.print("       "); out.print(pkey); if(syntax.length() > 0) out.print(' '); printNoHTML(out, syntax); out.println();
		out.println();
	}

	public static void printNoHTML(TerminalWriter out, String S) {
		if(S == null) out.print("null");
		else {
			int len = S.length();
			int pos = 0;
			while(pos < len) {
				char ch;
				if((ch = S.charAt(pos++)) == '<') {
					if((ch = S.charAt(pos++)) == '/') {
						if(
							(ch = S.charAt(pos++))=='b'
							|| ch == 'B'
						) out.print('"');
						else if(
							ch == 'i'
							|| ch == 'I'
						) out.print('>');
						pos++;
					} else {
						if(
							ch == 'b'
							|| ch == 'B'
						) out.print('"');
						else if(
							ch == 'i'
							|| ch == 'I'
						) out.print('<');
						pos++;
					}
				} else out.print(ch);
			}
		}
	}
}
