package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.util.i18n.LocalizedToString;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Each service has a unique identifier.  These IDs may change over time, but
 * are constant for one release.
 *
 * @author  AO Industries, Inc.
 */
public enum ServiceName implements LocalizedToString {
    /*TODO
    ao_server_daemon_hosts,
     */
    ao_server_resources,
    ao_servers,
    aoserv_permissions,
    /*
    aoserv_protocols,
    aosh_commands,
     */
    architectures,
    backup_partitions,
    backup_retentions,
    /* TODO
    bank_accounts,
    bank_transaction_types,
    bank_transactions,
    banks,
    blackhole_email_addresses,
    brands,
     */
    business_administrators,
    /* TODO
    business_administrator_permissions,
    business_profiles,
     */
    businesses,
    /* TODO
    business_servers,
    client_jvm_profile,
     */
    country_codes,
    /* TODO
    credit_card_processors,
    credit_card_transactions,
    credit_cards,
    cvs_repositories,
    daemon_profile,
     */
    disable_log,
    /* TODO
    distro_file_types,
    distro_files,
    dns_forbidden_zones,
    dns_records,
    dns_tlds,
    dns_types,
    dns_zones,
    email_addresses,
    email_attachment_blocks,
    email_attachment_types,
    email_domains,
    email_forwarding,
    email_list_addresses,
    email_lists,
    email_pipe_addresses,
    email_pipes,
    email_smtp_relay_types,
    email_smtp_relays,
    email_smtp_smart_host_domains,
    email_smtp_smart_hosts,
    email_sa_integration_modes,
    encryption_keys,
    expense_categories,
    failover_file_log,
     */
    failover_file_replications,
    /* TODO
    failover_file_schedule,
     */
    failover_mysql_replications,
    /* TODO
    file_backup_settings,
    ftp_guest_users,
    httpd_binds,
    httpd_jboss_sites,
    httpd_jboss_versions,
    httpd_jk_codes,
    httpd_jk_protocols,
    httpd_servers,
    httpd_shared_tomcats,
    httpd_site_authenticated_locations,
    httpd_site_binds,
    httpd_site_urls,
    httpd_sites,
    httpd_static_sites,
    httpd_tomcat_contexts,
    httpd_tomcat_data_sources,
    httpd_tomcat_parameters,
    httpd_tomcat_sites,
    httpd_tomcat_shared_sites,
    httpd_tomcat_std_sites,
    httpd_tomcat_versions,
    httpd_workers,
    ip_addresses,
     */
    languages,
    /* TODO
    linux_acc_addresses,
    linux_account_types,
    linux_accounts,
    linux_group_accounts,
    linux_group_types,
    linux_groups,
    linux_ids,
    linux_server_accounts,
    linux_server_groups,
    majordomo_lists,
    majordomo_servers,
    majordomo_versions,
    master_history,
    master_hosts,
    master_processes,
    master_server_profile,
    master_server_stats,
    master_servers,
    master_users,
    monthly_charges,
     */
    mysql_databases,
    mysql_db_users,
    mysql_reserved_words,
    mysql_servers,
    mysql_users,
    net_binds,
    net_device_ids,
    // TODO: net_devices,
    // TODO: net_ports,
    net_protocols,
    /* TODO
    net_tcp_redirects,
    notice_log,
    notice_types,
     */
    operating_system_versions,
    operating_systems,
    package_categories,
    /* TODO
    package_definition_limits,
    package_definitions,
    payment_types,
    physical_servers,
    postgres_databases,
    postgres_encodings,
    postgres_reserved_words,
    postgres_server_users,
     */
    postgres_servers,
    // TODO: postgres_users,
    postgres_versions,
    // TODO: private_ftp_servers,
    // TODO: processor_types,
    protocols,
    /* TODO
    racks,
    resellers,
     */
    resource_types,
    resources,
    /* TODO
    schema_columns,
    schema_foreign_keys,
    schema_tables,
    schema_types,
     */
    server_farms,
    servers,
    /* TODO
    shells,
    signup_request_options,
    signup_requests,
    spam_email_messages,
    system_email_aliases,
     */
    technologies,
    technology_classes,
    technology_names,
    technology_versions,
    /* TODO
    ticket_action_types,
    ticket_actions,
    ticket_assignments,
    ticket_brand_categories,
     */
    ticket_categories,
    ticket_priorities,
    ticket_stati,
    ticket_types,
    /* TODO
    tickets,
     */
    time_zones,
    /* TODO
    transaction_types,
    transactions,
    us_states,
     */
    usernames,
    /* TODO
    virtual_disks,
    virtual_servers,
    whois_history,
     */
    ;

    /**
     * An unmodifiable list of all service names.
     */
    public static final List<ServiceName> values = Collections.unmodifiableList(
        Arrays.asList(
            values()
        )
    );

    /**
     * Gets the service name in the default locale.
     */
    @Override
    public String toString() {
        return toString(Locale.getDefault());
    }

    /**
     * Gets the service name in the provided locale.
     */
    public String toString(Locale userLocale) {
        return ApplicationResources.accessor.getMessage(userLocale, "ServiceName."+name()+".toString");
    }

    public String getDescription(Locale userLocale) {
        return ApplicationResources.accessor.getMessage(userLocale, "ServiceName."+name()+".description");
    }

}