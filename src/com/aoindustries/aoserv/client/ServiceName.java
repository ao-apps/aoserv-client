package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
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
    /*TODO
    ao_server_daemon_hosts("AOServ Server Daemon Hosts", "<code>ao_server_daemon_hosts</code> stores which hosts may connect to the AOServ Daemon on a server."),
    ao_server_resources("AOServ Server Resources", "The ao_server_resources service keeps track of all the resources within a server running the AOServ distribution."),
    ao_servers("AOServ Servers", "The ao_servers service keeps track of all the servers that are running the AOServ distribution."),
    aoserv_permissions("AOServ Permissions", "<code>aoserv_permissions</code> lists all of the possible permissions in the AOServ system."),
    aoserv_protocols("AOServ Protocols", "<code>aoserv_protocols</code> keeps track of the different versions of the protocol and what is available in that protocol."),
    aosh_commands("AOSH Commands", "<code>aosh_commands</code> stores the details about each command that may be used in the AO shell."),
    architectures("Architectures", "The architectures service provides all the possible computer architectures for the servers service."),
    backup_partitions("Backup Partitions", "The <code>backup_partitions</code> service stores the details of each backup location."),
    backup_retentions("Backup Retentions", "<code>backup_retentions</code> stores all of the possible backup retention times."),
    bank_accounts("Bank Accounts", "The bank_accounts service stores all the bank accounts we operate."),
    bank_transaction_types("Bank Transaction Types", "The bank_transaction_types service stores all the possible types of transactions for bank_transactions."),
    bank_transactions("Bank Transactions", "The bank_transactions service stores all the the transactions that have occured on a bank account."),
    banks("Banks", "The banks service stores information about each bank we deal with"),
    blackhole_email_addresses("Blackhole Email Addresses", "The blackhole_email_addresses is a list of email addresses in which all email sent to them is immediately discarded via /dev/null."),
    brands("Brands", "Each brand has separate website, packages, nameservers, and support"),
     */
    business_administrators("Business Administrators", "The business_administrators service stores details about the people who are allowed to access the aoserv and aoweb utilities."),
    /* TODO
    business_administrator_permissions("Business Administrator Permissions", "Grants administrative permissions."),
    business_profiles("Business Profiles", "The business_profiles stores all the old profile data for a business.  This is done so that old contacts may be used in case of an emergency."),
     */
    businesses("Businesses", "The businesses service stores details about businesses"),
    /* TODO
    business_servers("Business Servers", "The business_servers service stores which servers may be accessed by which business.  A child business can only access a subset of the parent business' servers.  Only one entry per business is flagged as default."),
    client_jvm_profile("Client JVM Profile", ""),
     */
    country_codes("Country Codes", "The country_codes service contains all of the valid country codes.  This data was obtained from http://digitalid.verisign.com/ccodes.html"),
    /* TODO
    credit_card_processors("Credit Card Processors", "The credit_card_processors service stores all of the different credit card processing merchant accounts."),
    credit_card_transactions("Credit Card Transactions", "The credit_card_transactions service stores the complete history of credit card processor transactions."),
    credit_cards("Credit Cards", "The credit_cards service stores all of the client credit card information in an encrypted format."),
    cvs_repositories("CVS Repositories", "The <code>cvs_repositories</code> service stores the details of each CVSROOT"),
    daemon_profile("Daemon Profile", ""),
     */
    disable_log("Disable Log", "The <code>disable_log</code> logs all the times things are disabled."),
    /* TODO
    distro_file_types("Distro File Types", "The distro_file_types stores all of the possible file types."),
    distro_files("Distro Files", "The distro_files stores all of the file attributes in the templates."),
    dns_forbidden_zones("DNS Forbidden Zones", "<code>dns_forbidden_zones</code> stores all of the DNS zones that may not be hosted by the name servers"),
    dns_records("DNS Records", "<code>dns_records</code> stores all of individual records of the name server."),
    dns_tlds("DNS TLDs", "<code>dns_tlds</code> stores all of the domains that are considered to be \"top level domains\"."),
    dns_types("DNS Types", "<code>dns_types</code> stores all of the types of entries that may be placed in a line of a zone file."),
    dns_zones("DNS Zones", "<code>dns_zones</code> stores all of the DNS zones."),
    email_addresses("Email Addresses", "The email_addresses service stores a central pool of all email addresses."),
    email_attachment_blocks("Email Attachment Blocks", "The email_attachment_blocks service stores all attachment types that are currently blocked."),
    email_attachment_types("Email Attachment Types", "<code>email_attachment_types</code> stores all of the possible attachment extensions that may be blocked."),
    email_domains("Email Domains", "The email_domains service stores all the domains that the MTA responds to."),
    email_forwarding("Email Forwarding", "The email_forwarding sends email addresssed to email_addresses to another address."),
    email_list_addresses("Email List Addresses", "The email_list_addresses attaches the email_addresses to the email lists."),
    email_lists("Email Lists", "The email_lists service stores pointers to all the list files that are used for email."),
    email_pipe_addresses("Email Pipe Addresses", "The email_pipe_addresses attaches the email_addresses to the email pipes."),
    email_pipes("Email Pipes", "The email_pipes service stores pointers to all the processes that are used for direct delivery of email."),
    email_smtp_relay_types("Email SMTP Relay Types", "The email_smtp_relay_types service stores all the different types of SMTP rules."),
    email_smtp_relays("Email SMTP Relays", "The email_smtp_relays service stores the SMTP server access rules."),
    email_smtp_smart_host_domains("Email SMTP Smart Host Domains", "The email_smtp_smart_host_domains service stores optional per-domain settings for email_smtp_smart_hosts."),
    email_smtp_smart_hosts("Email SMTP Smart Hosts", "The email_smtp_smart_hosts service indicates a SMTP port is configured as a SmartHost for one or more servers."),
    email_sa_integration_modes("Email SpamAssassin Integration Modes", "The email_sa_integration_modes services provides all the possible spam assassin itegration modes supported."),
    encryption_keys("Encryption Keys", "The encryption_keys service stores the list of GPG key ids on a per-business basis."),
    expense_categories("Expense Categories", "The expense_categories service is a lookup for bank_transactions, and is used to organize expenditures for accounting purposes."),
    failover_file_log("Failover File Log", "The failover_file_log records statistics about each finished failover file replication."),
    failover_file_replications("Failover File Replications", "The failover_file_replications configures the replication of files from servers to failover servers."),
    failover_file_schedule("Failover File Schedule", "The failover_file_schedule controls when replications occur."),
    failover_mysql_replications("Failover MySQL Replications", "The failover_mysql_replications indicates a failover replication is using MySQL replication for /var/lib/mysql/... instead of file-based replication."),
    file_backup_settings("File Backup Settings", "The <code>file_backup_settings</code> service overrides everything in the backup system except <code>file_backup_devices</code>."),
    ftp_guest_users("FTP Guest Users", "Each <code>linux_account</code> may optionally be a guest FTP user.  A guest FTP user is restricted to FTP access in their home directory.  The passwd and group files are created such that any username or group in the same business resolve to the correct text names."),
    httpd_binds("Httpd Binds", "The <code>httpd_binds</code> service represents all the ip_address port binding combinations.  The app_protocol of the bind should always be HTTP or HTTPS."),
    httpd_jboss_sites("Httpd JBoss Sites", "<code>httpd_jboss_sites</code> ties httpd_sites to a jboss version to its httpd_shared_tomcat JVM."),
    httpd_jboss_versions("Httpd JBoss Versions", "<code>httpd_jboss_versions</code> is a lookup service containing information about different jboss versions."),
    httpd_jk_codes("Httpd JK Codes", "The httpd_jk_codes service contains all the possible mod_jk codes.  The codes must be unique for one httpd_sites when the site is used in multiple httpd_servers."),
    httpd_jk_protocols("Httpd JK Protocols", "The httpd_jk_protocols service contains all of the possible protocols used by the mod_jk Apache module."),
    httpd_servers("Httpd Servers", "The httpd_servers service contains a list of all the Apache servers."),
    httpd_shared_tomcats("Httpd Shared Tomcats", "<code>httpd_shared_tomcats</code> stores information about the JVM shared by httpd_tomcat_shared_sites."),
    httpd_site_authenticated_locations("Httpd Site Authenticated Locations", "The <code>httpd_site_authenticated_locations</code> configured Apache basic authentication using Location directives."),
    httpd_site_binds("Httpd Site Binds", "The <code>httpd_site_binds</code> attaches httpd_sites to httpd_binds."),
    httpd_site_urls("Httpd Site URLs", "The <code>httpd_site_urls</code> stores the hostnames that a site will respond to."),
    httpd_sites("Httpd Sites", "The httpd_sites service stores all of the site names used in the /www directory on servers.  Each site name must be unique per server.  Also, for site portability, it is preferred that sites remain unique between one set of servers.  Such as AO Industries will use unique names across all of its virtual servers, while resellers may also choose to use unique names across their sets of dedicated machines."),
    httpd_static_sites("Httpd Static Sites", "The <code>httpd_static_sites</code> stores the details of all sites that simply host static content only."),
    httpd_tomcat_contexts("Httpd Tomcat Contexts", "The <code>httpd_tomcat_contexts</code> stores the details of all contexts for all Tomcat sites."),
    httpd_tomcat_data_sources("Httpd Tomcat Data Sources", "The <code>httpd_tomcat_data_sources</code> stores the data sources on a per-context basis."),
    httpd_tomcat_parameters("Httpd Tomcat Parameters", "The <code>httpd_tomcat_parameters</code> stores the parameters on a per-context basis."),
    httpd_tomcat_sites("Httpd Tomcat Sites", "The <code>httpd_tomcat_sites</code> stores the details of all sites based on the Tomcat servlet engine.  This includes JBoss, single instance Tomcats, and a Tomcat that runs in a shared Java VM."),
    httpd_tomcat_shared_sites("Httpd Tomcat Shared Sites", "<code>httpd_tomcat_shared_sites</code> ties the httpd_tomcat_site to its httpd_shared_tomcat JVM."),
    httpd_tomcat_std_sites("Httpd Tomcat Std Sites", "The <code>httpd_tomcat_std_sites</code> service stores the details for each site that is self contained in its /www directory."),
    httpd_tomcat_versions("Httpd Tomcat Versions", "The <code>httpd_tomcat_versions</code> is a lookup of all the available versions of Tomcat."),
    httpd_workers("Httpd Workers", "The httpd_workers service contains all of the workers that are registered on each server."),
    ip_addresses("IP Addresses", "The ip_addresses service has an entry for every IP address hosted on our machines."),
     */
    languages("Languages", "The languages service provides all the possible languages for brands."),
    /* TODO
    linux_acc_addresses("Linux Account Addresses", "The linux_acc_addresses service links email addresses to linux accounts."),
    linux_account_types("Linux Account Types", "The linux_account_types service stores all the different types of linux accounts."),
    linux_accounts("Linux Accounts", "The linux_accounts service stores all of the linux account info that is common to all machines."),
    linux_group_accounts("Linux Group Accounts", "The linux_group_accounts stores the alternate users for linux groups."),
    linux_group_types("Linux Group Types", "The linux_group_types service stores all the different types of linux groups."),
    linux_groups("Linux Groups", "The linux_groups service stores the details of each linux group that are common to every server."),
    linux_ids("Linux IDs", "The linux_ids is a service of all the GID and UID values that are accepservice for a linux_server_group or linux_server_account.  0-499 are flagged as system and 500-65533 are user."),
    linux_server_accounts("Linux Server Accounts", "The linux_server_accounts service contains the user configuration that is unique to each server."),
    linux_server_groups("Linux Server Groups", "The linux_server_groups service stores the details about the groups that exist on the servers."),
    majordomo_lists("Majordomo Lists", "The majordomo_lists service stores the details of each list in a majordomo_server."),
    majordomo_servers("Majordomo Servers", "The majordomo_servers service stores all the details for one domain of Majordomo hosting."),
    majordomo_versions("Majordomo Versions", "The majordomo_versions service contains a list of all the supported versions of the Majordomo manages email list server."),
    master_history("Master History", ""),
    master_hosts("Master Hosts", "<code>master_hosts</code> stores which hosts each user may connect from."),
    master_processes("Master Processes", ""),
    master_server_profile("Master Server Profile", ""),
    master_server_stats("Master Server Stats", ""),
    master_servers("Master Servers", "<code>master_servers</code> stores which servers each user may control."),
    master_users("Master Users", "<code>master_users</code> stores the authorization information for people that may access the master controller."),
    monthly_charges("Monthly Charges", "The monthly_charges service contains an entry for each transaction that is automatically charged each month."),
    mysql_databases("MySQL Databases", "The mysql_databases service stores the information about each MySQL database in the system."),
    mysql_db_users("MySQL DB Users", "The <code>mysql_db_users</code> service stores which <code>mysql_users</code> can access which <code>mysql_databases</code>.  The user permissions are also contained in each row."),
    mysql_reserved_words("MySQL Reserved Words", "<code>mysql_reserved_words</code> may not be used for database or service names in MySQL."),
    mysql_servers("MySQL Servers", "The mysql_servers service stores the information about each MySQL instance in the system."),
    mysql_users("MySQL Users", "The mysql_users service stores all of the user info for every MySQL user."),
    net_binds(
        "Net Binds",
        "<code>net_binds</code> stores all of ports that should be bound to\n"
        + "by processes running on the servers.  If any ports is bound to that\n"
        + "is not listed here, it may indicate a security violation.  This\n"
        + "data is also used to open firewall ports.  Each port may also be\n"
        + "periodically monitored for process reliability.\n"
        + "<p>\n"
        + "Four types of IP addresses exist:\n"
        + "<ol>\n"
        + "  <li>wildcard - Processes bind to all available IP addresses on\n"
        + "                 the server.  Monitoring may be performed from\n"
        + "                 anywhere, depending on firewall flags.\n"
        + "  <li>public -   Processes bind to a public IP address.  Monitoring\n"
        + "                 may be performed from the localhost or public\n"
        + "                 network, depending on firewall rules.\n"
        + "  <li>private -  Processes bind to a private IP address.  Monitoring\n"
        + "                 may be performed from the localhost or private\n"
        + "                 network, depending on firewall rules.\n"
        + "  <li>loopback - Processes bind internally in the server.  Monitoring\n"
        + "                 may only be performed from localhost.\n"
        + "</ol>"
    ),
    net_device_ids("Net Device IDs", "The net_device_ids contains all the network devices used on Linux servers."),
    net_devices("Net Devices", "The net_devices service contains each device used by Linux servers."),
    net_ports("Net Ports", "The net_ports service contains all of the possible network ports."),
    net_protocols("Net Protocols", "<code>net_protocols</code> lists the possible network protocols used in <code>net_binds</code>."),
    net_tcp_redirects("Net TCP Redirects", "The net_tcp_redirects service stores all of the xinetd redirect configurations."),
    notice_log("Notice Log", "The service notice_log contains information about any late or non-payment notices."),
    notice_types("Notice Types", "The service notice_types provides lookup information for the notice_log service"),
    operating_system_versions("Operating System Versions", "The operating_system_versions service provides all the supported versions of operating_systems."),
    operating_systems("Operating Systems", "The operating_systems service stores represents each type of operating system."),
     */
    package_categories("Package Categories", "The various categories for package_definitions."),
    /* TODO
    package_definition_limits("Package Definition Limits", "The package_definition_limits service defines the limits for each package_definition and resource combination."),
    package_definitions("Package Definitions", "The package_definitions service stores a list of supported services."),
    payment_types("Payment Types", "The payment_types service stores all the accepservice payment forms"),
    physical_servers("Physical Servers", "The physical servers consume rack space and electricity and optionally provide cluster resources."),
    postgres_databases("PostgreSQL Databases", "The postgres_databases service stores the information about each PostgreSQL database in the system."),
    postgres_encodings("PostgreSQL Encodings", "The postgres_encodings service stores the information about each PostgreSQL database encoding supported."),
    postgres_reserved_words("PostgreSQL Reserved Words", "<code>postgres_reserved_words</code> may not be used for database or service named in PostgreSQL."),
    postgres_server_users("PostgreSQL Server Users", "The postgres_server_users service stores the specific attributes of postgres_users for one server."),
    postgres_servers("PostgreSQL Servers", "The postgres_servers service stores the information about each PostgreSQL instance in the system."),
    postgres_users("PostgreSQL Users", "The postgres_users service stores all the users for PostgreSQL.  Because a single person may have access to multiple PostgreSQL servers, this service represents what is common to them across all machines.  The machine specific details are in the postgres_server_users service."),
    postgres_versions("PostgreSQL Versions", "The <code>postgres_versions</code> is a lookup of all the available versions of PostgreSQL."),
    private_ftp_servers("Private FTP Servers", "The private_ftp_servers service contains an entry for each private FTP server hosted."),
    processor_types("Processor Types", "The possible types of processors for cluster QoS management."),
    protocols("Protocols", "The protocols service has an entry for each service protocol along with the default port numbers."),
    racks("Racks", "Each server farm may manage cluster resources on a per-rack basis."),
    resellers("Resellers", "A reseller may provide ticket-based support"),
     */
    resource_types("Resource Types", "The types of billable resources"),
    /* TODO
    resources("Resources", "All billable resources"),
    schema_columns("Schema Columns", "<code>schema_columns</code> stores the details about the data contained in each column."),
    schema_foreign_keys("Schema Foreign Keys", "<code>schema_foreign_keys</code> services stores all the foreign key info for the database."),
    schema_tables("Schema Tables", "<code>schema_tables</code> stores table names, display names, and descriptions for each table."),
    schema_types("Schema Types", "<code>schema_types</code> stores names and unique numbers for each type."),
    server_farms("Server Farms", "The server_farms service stores the details about each unique server farm (or location) that exists."),
    servers("Servers", "The servers service keeps track of all the servers that are referenced in the database"),
    shells("Shells", "The shells service stores all the shells that may be used in a linux account."),
    signup_request_options("Sign-Up Request Options", "The options for each specific sign-up type."),
    signup_requests("Sign-Up Requests", "One sign-up request for any time of service."),
    spam_email_messages("Spam Email Messages", "The spam_email_messages service stores all of the reported spam."),
    system_email_aliases("System Email Aliases", "The system_email_aliases is a list of all entries that are placed in /etc/aliases but are not part of the other services."),
    technologies("Technologies", "The technologies service contains the list of all software packages and the classes they belong to."),
    technology_classes("Technology Classes", "The technology_classes service defines the classes that may be used in the technologies service"),
    technology_names("Technology Names", "The technology_names service contains all of the unique names of software packages that are installed in the servers."),
    technology_versions("Technology Versions", "The technology_versions service contains the list of all software packages, owners, and versions."),
    ticket_action_types("Ticket Action Types", "Service ticket_action_types is a lookup for all of the possible actions that may be made to a ticket"),
    ticket_actions("Ticket Actions", "The ticket_actions service represent a complete history of the changes that have been made to a ticket.  When a ticket is initially created it has no actions.  Any change from its initial state will cause an action to be logged."),
    ticket_assignments("Ticket Assignments", "Tickets may be assigned to specific administrators at each reseller level."),
    ticket_brand_categories("Ticket Brand Categories", "Each brand may select which categories will be visible for ticket support."),
     */
    ticket_categories("Ticket Categories", "Each ticket may be associated with one of these hierichical categories."),
    ticket_priorities("Ticket Priorities", "Service ticket_priorities is a lookup service for tickets"),
    ticket_stati("Ticket Statuses", "Service ticket_stati is a lookup service for tickets"),
    ticket_types("Ticket Types", "Service ticket_types is a lookup for tickets showing types of service requests"),
    /* TODO
    tickets("Tickets", "Service tickets contains information on service requests"),
     */
    time_zones("Time Zones", "The allowed time zones for a server"),
    /* TODO
    transaction_types("Transaction Types", "The transaction_types service stores all the types of transactions."),
    transactions("Transactions", "The transactions service keeps track of all transactions that have occured with the businesses"),
    us_states("US States", "The us_states service contains an entry for each state in the United States."),
     */
    usernames("Usernames", "The usernames service stores information about each username.  Every username is unique across the system, and may be used for multiple services.  Regardless of the system, the username always belongs to one business, and preferrably one actual person or system."),
    /* TODO
    virtual_disks("Virtual Disks", "The virtual disk configurations for cluster QoS management."),
    virtual_servers("Virtual Servers", "A virtual server is any server that is a guest in the virtualized clustering."),
    whois_history("Whois History", "The output from whois lookups are logged weekly to keep track of when customers begin and end using the systems.")
     */
    ;

    private final String display;
    private final String description;

    /**
     * An unmodifiable list of all service names.
     */
    public static final List<ServiceName> values = Collections.unmodifiableList(
        Arrays.asList(
            values()
        )
    );

    private ServiceName(String display, String description) {
        this.display = display;
        this.description = description;
    }

    public String getDisplay() {
        return display;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return display;
    }
}