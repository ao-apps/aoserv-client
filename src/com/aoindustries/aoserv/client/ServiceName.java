/*
 * Copyright 2001-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Each service has a unique identifier.  These IDs may change over time, but
 * are constant for one release.
 *
 * @author  AO Industries, Inc.
 */
public enum ServiceName {
    ao_server_daemon_hosts,
    ao_server_resources,
    ao_servers,
    aoserv_permissions,
    aoserv_roles,
    aoserv_role_permissions,
    architectures,
    backup_partitions,
    backup_retentions,
    /* TODO
    bank_accounts,
     */
    bank_transaction_types,
    /* TODO
    bank_transactions,
    banks,
    blackhole_email_addresses,
     */
    brands,
    business_administrators,
    business_administrator_roles,
    business_profiles,
    businesses,
    business_servers,
    country_codes,
    credit_card_processors,
    credit_card_transactions,
    credit_cards,
    cvs_repositories,
    disable_log,
    /* TODO
    distro_file_types,
    distro_files,
     */
    dns_records,
    dns_tlds,
    dns_types,
    dns_zones,
    /* TODO
    email_addresses,
    email_attachment_blocks,
     */
    email_attachment_types,
    /* TODO
    email_domains,
    email_forwarding,
     */
    email_inboxes,
    /* TODO
    email_list_addresses,
    email_lists,
    email_pipe_addresses,
    email_pipes,
     */
    email_smtp_relay_types,
    /* TODO
    email_smtp_relays,
    email_smtp_smart_host_domains,
    email_smtp_smart_hosts,
     */
    email_sa_integration_modes,
    /* TODO
    encryption_keys,
     */
    expense_categories,
    failover_file_log,
    failover_file_replications,
    failover_file_schedule,
    failover_mysql_replications,
    file_backup_settings,
    ftp_guest_users,
    group_names,
    /* TODO
    httpd_binds,
    httpd_jboss_sites,
     */
    httpd_jboss_versions,
    httpd_jk_codes,
    httpd_jk_protocols,
    httpd_servers,
    /* TODO
    httpd_shared_tomcats,
    httpd_site_authenticated_locations,
    httpd_site_binds,
    httpd_site_urls,
     */
    httpd_sites,
    // TODO: httpd_static_sites,
    // TODO: httpd_tomcat_contexts,
    // TODO: httpd_tomcat_data_sources,
    // TODO: httpd_tomcat_parameters,
    // TODO: httpd_tomcat_sites,
    // TODO: httpd_tomcat_shared_sites,
    // TODO: httpd_tomcat_std_sites,
    httpd_tomcat_versions,
    // TODO: httpd_workers,
    ip_addresses,
    languages,
    /* TODO
    linux_acc_addresses,
     */
    linux_account_groups,
    linux_account_types,
    linux_accounts,
    linux_group_types,
    linux_groups,
    /* TODO
    majordomo_lists,
    majordomo_servers,
     */
    majordomo_versions,
    master_hosts,
    master_servers,
    master_users,
    /* TODO
    monthly_charges,
     */
    mysql_databases,
    mysql_db_users,
    mysql_servers,
    mysql_users,
    net_binds,
    net_device_ids,
    net_devices,
    net_protocols,
    net_tcp_redirects,
    /* TODO
    notice_log,
     */
    notice_types,
    operating_system_versions,
    operating_systems,
    package_categories,
    package_definition_businesses,
    package_definition_limits,
    package_definitions,
    payment_types,
    // TODO: physical_servers,
    postgres_databases,
    postgres_encodings,
    postgres_servers,
    postgres_users,
    postgres_versions,
    private_ftp_servers,
    processor_types,
    protocols,
    /* TODO
    racks,
     */
    resellers,
    resource_types,
    resources,
    /* TODO
    schema_columns,
    schema_foreign_keys,
    schema_tables,
    schema_types,
     */
    server_farms,
    server_resources,
    servers,
    shells,
    /* TODO
    signup_request_options,
    signup_requests,
    spam_email_messages,
    system_email_aliases,
     */
    technologies,
    technology_classes,
    technology_names,
    technology_versions,
    ticket_action_types,
    ticket_actions,
    ticket_assignments,
    // TODO: ticket_brand_categories,
    ticket_categories,
    ticket_priorities,
    ticket_statuses,
    ticket_types,
    tickets,
    time_zones,
    transaction_types,
    transactions,
    usernames,
    // TODO: virtual_disks,
    virtual_servers,
    // TODO: whois_history
    ;

    /**
     * An unmodifiable list of all service names.
     */
    public static final List<ServiceName> values = Collections.unmodifiableList(
        Arrays.asList(
            values()
        )
    );

    @Override
    public String toString() {
        return ApplicationResources.accessor.getMessage("ServiceName."+name()+".toString");
    }

    public String getDescription() {
        return ApplicationResources.accessor.getMessage("ServiceName."+name()+".description");
    }
}