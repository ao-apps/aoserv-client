package com.aoindustries.aoserv.client.command;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.AOServPermission;
import com.aoindustries.aoserv.client.ServiceName;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Each command has a unique name.  These IDs may change over time, but
 * are constant for one release.
 *
 * @author  AO Industries, Inc.
 */
public enum CommandName {

    // Global Commands
    describe(DescribeCommand.class, null),
    // TODO: select(SelectCommand.class, null),
    show(ShowCommand.class, null),
    // failover_file_logs
    add_failover_file_log(AddFailoverFileLog.class, ServiceName.failover_file_log, AOServPermission.Permission.add_failover_file_log),
    // failover_file_replications
    request_replication_daemon_access(RequestReplicationDaemonAccess.class, ServiceName.failover_file_replications, AOServPermission.Permission.request_replication_daemon_access),
    // TODO: add_backup_server(TODO.class, TODO),
    // TODO: add_business(TODO.class, TODO),
    // TODO: add_business_administrator(TODO.class, TODO),
    // TODO: add_business_profile(TODO.class, TODO),
    // TODO: add_business_server(TODO.class, TODO),
    // TODO: add_cvs_repository(TODO.class, TODO),
    // TODO: add_dns_record(TODO.class, TODO),
    // TODO: add_dns_zone(TODO.class, TODO),
    // TODO: add_email_address(TODO.class, TODO),
    // TODO: add_email_domain(TODO.class, TODO),
    // TODO: add_email_forwarding(TODO.class, TODO),
    // TODO: add_email_list(TODO.class, TODO),
    // TODO: add_email_list_address(TODO.class, TODO),
    // TODO: add_email_pipe_address(TODO.class, TODO),
    // TODO: add_email_pipe(TODO.class, TODO),
    // TODO: add_email_smtp_relay(TODO.class, TODO),
    // TODO: add_file_backup_setting(TODO.class, TODO),
    // TODO: add_ftp_guest_user(TODO.class, TODO),
    // TODO: add_httpd_jboss_site(TODO.class, TODO),
    // TODO: add_httpd_shared_tomcat(TODO.class, TODO),
    // TODO: add_httpd_site_url(TODO.class, TODO),
    // TODO: add_httpd_tomcat_context(TODO.class, TODO),
    // TODO: add_httpd_tomcat_data_source(TODO.class, TODO),
    // TODO: add_httpd_tomcat_parameter(TODO.class, TODO),
    // TODO: add_httpd_tomcat_shared_site(TODO.class, TODO),
    // TODO: add_httpd_tomcat_std_site(TODO.class, TODO),
    // TODO: add_incoming_payment(TODO.class, TODO),
    // TODO: add_linux_acc_address(TODO.class, TODO),
    // TODO: add_linux_account(TODO.class, TODO),
    // TODO: add_linux_group(TODO.class, TODO),
    // TODO: add_linux_group_account(TODO.class, TODO),
    // TODO: add_linux_server_account(TODO.class, TODO),
    // TODO: add_linux_server_group(TODO.class, TODO),
    // TODO: add_majordomo_list(TODO.class, TODO),
    // TODO: add_majordomo_server(TODO.class, TODO),
    // TODO: add_mysql_database(TODO.class, TODO),
    // TODO: add_mysql_db_user(TODO.class, TODO),
    // TODO: add_mysql_user(TODO.class, TODO),
    // TODO: add_net_bind(TODO.class, TODO),
    // TODO: add_notice_log(TODO.class, TODO),
    // TODO: add_postgres_database(TODO.class, TODO),
    // TODO: add_postgres_server_user(TODO.class, TODO),
    // TODO: add_postgres_user(TODO.class, TODO),
    // TODO: add_spam_email_message(TODO.class, TODO),
    // TODO: add_ticket(TODO.class, TODO),
    // TODO: add_ticket_work(TODO.class, TODO),
    // TODO: add_transaction(TODO.class, TODO),
    // TODO: add_username(TODO.class, TODO),
    // TODO: are_linux_account_passwords_set(TODO.class, TODO),
    // TODO: are_postgres_user_passwords_set(TODO.class, TODO),
    // TODO: are_username_passwords_set(TODO.class, TODO),
    // TODO: bounce_ticket(TODO.class, TODO),
    // TODO: cancel_business(TODO.class, TODO),
    // TODO: change_ticket_admin_priority(TODO.class, TODO),
    // TODO: change_ticket_client_priority(TODO.class, TODO),
    // TODO: change_ticket_deadline(TODO.class, TODO),
    // TODO: change_ticket_technology(TODO.class, TODO),
    // TODO: change_ticket_type(TODO.class, TODO),
    // TODO: check_accounting(TODO.class, TODO),
    // TODO: check_business_administrator_password(TODO.class, TODO),
    // TODO: check_business_administrator_username(TODO.class, TODO),
    // TODO: check_dns_zone(TODO.class, TODO),
    // TODO: check_email_address(TODO.class, TODO),
    // TODO: check_email_domain(TODO.class, TODO),
    // TODO: check_email_forwarding(TODO.class, TODO),
    // TODO: check_email_list_path(TODO.class, TODO),
    // TODO: check_ip_address(TODO.class, TODO),
    // TODO: check_linux_account_username(TODO.class, TODO),
    // TODO: check_linux_account_name(TODO.class, TODO),
    // TODO: check_linux_account_password(TODO.class, TODO),
    // TODO: check_linux_group_name(TODO.class, TODO),
    // TODO: check_majordomo_list_name(TODO.class, TODO),
    // TODO: check_mysql_database_name(TODO.class, TODO),
    // TODO: check_mysql_user_password(TODO.class, TODO),
    // TODO: check_mysql_server_name(TODO.class, TODO),
    // TODO: check_mysql_username(TODO.class, TODO),
    // TODO: check_postgres_database_name(TODO.class, TODO),
    // TODO: check_postgres_password(TODO.class, TODO),
    // TODO: check_postgres_server_name(TODO.class, TODO),
    // TODO: check_postgres_username(TODO.class, TODO),
    // TODO: check_shared_tomcat_name(TODO.class, TODO),
    // TODO: check_site_name(TODO.class, TODO),
    // TODO: check_username(TODO.class, TODO),
    // TODO: check_username_password(TODO.class, TODO),
    // TODO: clear(TODO.class, TODO),
    // TODO: compare_linux_server_account_password(TODO.class, TODO),
    // TODO: complete_ticket(TODO.class, TODO),
    // TODO: copy_home_directory(TODO.class, TODO),
    // TODO: copy_linux_server_account_password(TODO.class, TODO),
    // TODO: crypt(TODO.class, TODO),
    // TODO: decline_credit_card(TODO.class, TODO),
    // TODO: disable_business(TODO.class, TODO),
    // TODO: disable_business_administrator(TODO.class, TODO),
    // TODO: disable_cvs_repository(TODO.class, TODO),
    // TODO: disable_email_list(TODO.class, TODO),
    // TODO: disable_email_pipe(TODO.class, TODO),
    // TODO: disable_email_smtp_relay(TODO.class, TODO),
    // TODO: disable_httpd_shared_tomcat(TODO.class, TODO),
    // TODO: disable_httpd_site(TODO.class, TODO),
    // TODO: disable_httpd_site_bind(TODO.class, TODO),
    // TODO: disable_linux_account(TODO.class, TODO),
    // TODO: disable_linux_server_account(TODO.class, TODO),
    // TODO: disable_mysql_user(TODO.class, TODO),
    // TODO: disable_postgres_server_user(TODO.class, TODO),
    // TODO: disable_postgres_user(TODO.class, TODO),
    // TODO: disable_username(TODO.class, TODO),
    // TODO: dump_mysql_database(TODO.class, TODO),
    // TODO: dump_postgres_database(TODO.class, TODO),
    // TODO: echo(TODO.class, TODO),
    // TODO: enable_business(TODO.class, TODO),
    // TODO: enable_business_administrator(TODO.class, TODO),
    // TODO: enable_cvs_repository(TODO.class, TODO),
    // TODO: enable_email_list(TODO.class, TODO),
    // TODO: enable_email_pipe(TODO.class, TODO),
    // TODO: enable_email_smtp_relay(TODO.class, TODO),
    // TODO: enable_httpd_shared_tomcat(TODO.class, TODO),
    // TODO: enable_httpd_site(TODO.class, TODO),
    // TODO: enable_httpd_site_bind(TODO.class, TODO),
    // TODO: enable_linux_account(TODO.class, TODO),
    // TODO: enable_linux_server_account(TODO.class, TODO),
    // TODO: enable_mysql_user(TODO.class, TODO),
    // TODO: enable_postgres_server_user(TODO.class, TODO),
    // TODO: enable_postgres_user(TODO.class, TODO),
    // TODO: enable_username(TODO.class, TODO),
    // TODO: exit(TODO.class, TODO),
    // TODO: generate_accounting(TODO.class, TODO),
    // TODO: generate_mysql_database_name(TODO.class, TODO),
    // TODO: generate_password(TODO.class, TODO),
    // TODO: generate_postgres_database_name(TODO.class, TODO),
    // TODO: generate_shared_tomcat_name(TODO.class, TODO),
    // TODO: generate_site_name(TODO.class, TODO),
    // TODO: get_autoresponder_content(TODO.class, TODO),
    // TODO: get_awstats_file(TODO.class, TODO),
    // TODO: get_backup_partition_total_size(TODO.class, TODO),
    // TODO: get_backup_partition_used_size(TODO.class, TODO),
    // TODO: get_cron_table(TODO.class, TODO),
    // TODO: get_email_list(TODO.class, TODO),
    // TODO: get_imap_folder_sizes(TODO.class, TODO),
    // TODO: get_inbox_attributes(TODO.class, TODO),
    // TODO: get_majordomo_info_file(TODO.class, TODO),
    // TODO: get_majordomo_intro_file(TODO.class, TODO),
    // TODO: get_mrtg_file(TODO.class, TODO),
    // TODO: get_root_business(TODO.class, TODO),
    // TODO: hold_ticket(TODO.class, TODO),
    // TODO: initialize_httpd_site_passwd_file(TODO.class, TODO),
    // TODO: invalidate(TODO.class, TODO),
    // TODO: is_accounting_available(TODO.class, TODO),
    // TODO: is_business_administrator_password_set(TODO.class, TODO),
    // TODO: is_dns_zone_available(TODO.class, TODO),
    // TODO: is_email_domain_available(TODO.class, TODO),
    // TODO: is_ip_address_used(TODO.class, TODO),
    // TODO: is_linux_group_name_available(TODO.class, TODO),
    // TODO: is_linux_server_account_procmail_manual(TODO.class, TODO),
    // TODO: is_linux_server_account_password_set(TODO.class, TODO),
    // TODO: is_mysql_database_name_available(TODO.class, TODO),
    // TODO: is_mysql_server_name_available(TODO.class, TODO),
    // TODO: is_mysql_user_password_set(TODO.class, TODO),
    // TODO: is_postgres_database_name_available(TODO.class, TODO),
    // TODO: is_postgres_server_name_available(TODO.class, TODO),
    // TODO: is_postgres_server_user_password_set(TODO.class, TODO),
    // TODO: is_shared_tomcat_name_available(TODO.class, TODO),
    // TODO: is_site_name_available(TODO.class, TODO),
    // TODO: is_username_available(TODO.class, TODO),
    // TODO: kill_ticket(TODO.class, TODO),
    // TODO: jobs(TODO.class, TODO),
    // TODO: move_business(TODO.class, TODO),
    // TODO: move_ip_address(TODO.class, TODO),
    // TODO: ping(TODO.class, TODO),
    // TODO: print_zone_file(TODO.class, TODO),
    // TODO: reactivate_ticket(TODO.class, TODO),
    // TODO: refresh_email_smtp_relay(TODO.class, TODO),
    // TODO: remove_blackhole_email_address(TODO.class, TODO),
    // TODO: remove_business_administrator(TODO.class, TODO),
    // TODO: remove_business_server(TODO.class, TODO),
    // TODO: remove_credit_card(TODO.class, TODO),
    // TODO: remove_cvs_repository(TODO.class, TODO),
    // TODO: remove_dns_record(TODO.class, TODO),
    // TODO: remove_dns_zone(TODO.class, TODO),
    // TODO: remove_email_address(TODO.class, TODO),
    // TODO: remove_email_domain(TODO.class, TODO),
    // TODO: remove_email_forwarding(TODO.class, TODO),
    // TODO: remove_email_list(TODO.class, TODO),
    // TODO: remove_email_list_address(TODO.class, TODO),
    // TODO: remove_email_pipe(TODO.class, TODO),
    // TODO: remove_email_pipe_address(TODO.class, TODO),
    // TODO: remove_email_smtp_relay(TODO.class, TODO),
    // TODO: remove_file_backup_setting(TODO.class, TODO),
    // TODO: remove_ftp_guest_user(TODO.class, TODO),
    // TODO: remove_httpd_shared_tomcat(TODO.class, TODO),
    // TODO: remove_httpd_site(TODO.class, TODO),
    // TODO: remove_httpd_site_url(TODO.class, TODO),
    // TODO: remove_httpd_tomcat_context(TODO.class, TODO),
    // TODO: remove_httpd_tomcat_data_source(TODO.class, TODO),
    // TODO: remove_httpd_tomcat_parameter(TODO.class, TODO),
    // TODO: remove_incoming_payment(TODO.class, TODO),
    // TODO: remove_linux_acc_address(TODO.class, TODO),
    // TODO: remove_linux_account(TODO.class, TODO),
    // TODO: remove_linux_group(TODO.class, TODO),
    // TODO: remove_linux_group_account(TODO.class, TODO),
    // TODO: remove_linux_server_account(TODO.class, TODO),
    // TODO: remove_linux_server_group(TODO.class, TODO),
    // TODO: remove_majordomo_server(TODO.class, TODO),
    // TODO: remove_mysql_database(TODO.class, TODO),
    // TODO: remove_mysql_db_user(TODO.class, TODO),
    // TODO: remove_mysql_user(TODO.class, TODO),
    // TODO: remove_net_bind(TODO.class, TODO),
    // TODO: remove_postgres_database(TODO.class, TODO),
    // TODO: remove_postgres_server_user(TODO.class, TODO),
    // TODO: remove_postgres_user(TODO.class, TODO),
    // TODO: remove_username(TODO.class, TODO),
    // TODO: repeat(TODO.class, TODO),
    // TODO: restart_apache(TODO.class, TODO),
    // TODO: restart_cron(TODO.class, TODO),
    // TODO: restart_mysql(TODO.class, TODO),
    // TODO: restart_postgresql(TODO.class, TODO),
    // TODO: restart_xfs(TODO.class, TODO),
    // TODO: restart_xvfb(TODO.class, TODO),
    // TODO: set_autoresponder(TODO.class, TODO),
    // TODO: set_business_accounting(TODO.class, TODO),
    // TODO: set_business_administrator_password(TODO.class, TODO),
    // TODO: set_business_administrator_profile(TODO.class, TODO),
    // TODO: set_cron_table(TODO.class, TODO),
    // TODO: set_cvs_repository_mode(TODO.class, TODO),
    // TODO: set_default_business_server(TODO.class, TODO),
    // TODO: set_dns_zone_ttl(TODO.class, TODO),
    // TODO: set_email_list(TODO.class, TODO),
    // TODO: set_file_backup_setting(TODO.class, TODO),
    // TODO: set_httpd_shared_tomcat_is_manual(TODO.class, TODO),
    // TODO: set_httpd_site_bind_is_manual(TODO.class, TODO),
    // TODO: set_httpd_site_bind_redirect_to_primary_hostname(TODO.class, TODO),
    // TODO: set_httpd_site_is_manual(TODO.class, TODO),
    // TODO: set_httpd_site_server_admin(TODO.class, TODO),
    // TODO: set_httpd_tomcat_context_attributes(TODO.class, TODO),
    // TODO: set_ip_address_business(TODO.class, TODO),
    // TODO: set_ip_address_dhcp_address(TODO.class, TODO),
    // TODO: set_ip_address_hostname(TODO.class, TODO),
    // TODO: set_linux_account_home_phone(TODO.class, TODO),
    // TODO: set_linux_account_name(TODO.class, TODO),
    // TODO: set_linux_account_office_location(TODO.class, TODO),
    // TODO: set_linux_account_office_phone(TODO.class, TODO),
    // TODO: set_linux_account_password(TODO.class, TODO),
    // TODO: set_linux_account_shell(TODO.class, TODO),
    // TODO: set_linux_server_account_junk_email_retention(TODO.class, TODO),
    // TODO: set_linux_server_account_password(TODO.class, TODO),
    // TODO: set_linux_server_account_spamassassin_integration_mode(TODO.class, TODO),
    // TODO: set_linux_server_account_spamassassin_required_score(TODO.class, TODO),
    // TODO: set_linux_server_account_trash_email_retention(TODO.class, TODO),
    // TODO: set_linux_server_account_use_inbox(TODO.class, TODO),
    // TODO: set_majordomo_info_file(TODO.class, TODO),
    // TODO: set_majordomo_intro_file(TODO.class, TODO),
    // TODO: set_mysql_user_password(TODO.class, TODO),
    // TODO: set_net_bind_monitoring_enabled(TODO.class, TODO),
    // TODO: set_net_bind_open_firewall(TODO.class, TODO),
    // TODO: set_postgres_server_user_password(TODO.class, TODO),
    // TODO: set_postgres_user_password(TODO.class, TODO),
    // TODO: set_primary_httpd_site_url(TODO.class, TODO),
    // TODO: set_primary_linux_group_account(TODO.class, TODO),
    // TODO: set_username_password(TODO.class, TODO),
    // TODO: sleep(TODO.class, TODO),
    // TODO: start_apache(TODO.class, TODO),
    // TODO: start_cron(TODO.class, TODO),
    // TODO: start_distro(TODO.class, TODO),
    // TODO: start_jvm(TODO.class, TODO),
    // TODO: start_mysql(TODO.class, TODO),
    // TODO: start_postgresql(TODO.class, TODO),
    // TODO: start_xfs(TODO.class, TODO),
    // TODO: start_xvfb(TODO.class, TODO),
    // TODO: stop_apache(TODO.class, TODO),
    // TODO: stop_cron(TODO.class, TODO),
    // TODO: stop_jvm(TODO.class, TODO),
    // TODO: stop_mysql(TODO.class, TODO),
    // TODO: stop_postgresql(TODO.class, TODO),
    // TODO: stop_xfs(TODO.class, TODO),
    // TODO: stop_xvfb(TODO.class, TODO),
    // TODO: su(TODO.class, TODO),
    // TODO: time(TODO.class, TODO),
    // TODO: update_httpd_tomcat_data_source(TODO.class, TODO),
    // TODO: update_httpd_tomcat_parameter(TODO.class, TODO),
    // TODO: wait_for_httpd_site_rebuild(TODO.class, TODO),
    // TODO: wait_for_linux_account_rebuild(TODO.class, TODO),
    // TODO: wait_for_mysql_database_rebuild(TODO.class, TODO),
    // TODO: wait_for_mysql_db_user_rebuild(TODO.class, TODO),
    // TODO: wait_for_mysql_host_rebuild(TODO.class, TODO),
    // TODO: wait_for_mysql_server_rebuild(TODO.class, TODO),
    // TODO: wait_for_mysql_user_rebuild(TODO.class, TODO),
    // TODO: wait_for_postgres_database_rebuild(TODO.class, TODO),
    // TODO: wait_for_postgres_server_rebuild(TODO.class, TODO),
    // TODO: wait_for_postgres_user_rebuild(TODO.class, TODO),
    // TODO: whoami(TODO.class, TODO)
    ;

    /**
     * An unmodifiable list of all command names.
     */
    public static final List<CommandName> values = Collections.unmodifiableList(
        Arrays.asList(
            values()
        )
    );

    private final Class<? extends AOServCommand> commandClass;
    private final ServiceName serviceName;
    private final Set<AOServPermission.Permission> permissions;

    private CommandName(Class<? extends AOServCommand> commandClass, ServiceName serviceName, AOServPermission.Permission... permissions) {
        this.commandClass = commandClass;
        this.serviceName = serviceName;
        if(permissions.length==0) this.permissions = Collections.emptySet();
        else if(permissions.length==1) this.permissions = Collections.singleton(permissions[0]);
        else this.permissions = Collections.unmodifiableSet(EnumSet.of(permissions[0], permissions));
    }

    public Class<? extends AOServCommand> getCommandClass() {
        return commandClass;
    }

    /**
     * Gets the service name this command is best associated with, or <code>null</code>
     * if there is no association.
     */
    public ServiceName getServiceName() {
        return serviceName;
    }

    /**
     * Gets a short description of the command.
     */
    public String getShortDesc(Locale userLocale) {
        return ApplicationResources.accessor.getMessage(userLocale, "CommandName."+name()+".shortDesc");
    }

    /**
     * Gets the unmodifiable set of permissions that are required to be allowed
     * to execute this command.  An empty set indicates no specific permissions are
     * required.
     */
    public Set<AOServPermission.Permission> getPermissions() {
        return permissions;
    }
}
