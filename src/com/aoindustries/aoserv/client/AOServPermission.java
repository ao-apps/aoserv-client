package com.aoindustries.aoserv.client;

/*
 * Copyright 2007-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.table.IndexType;
import com.aoindustries.util.i18n.LocalizedToString;
import java.util.Locale;

/**
 * All of the permissions within the system.
 *
 * @author  AO Industries, Inc.
 */
final public class AOServPermission extends AOServObjectStringKey<AOServPermission> implements BeanFactory<com.aoindustries.aoserv.client.beans.AOServPermission> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    /**
     * The possible permissions.
     */
    public enum Permission implements LocalizedToString {
        // business_administrators
        set_business_administrator_password,
        // businesses
        cancel_business,
        // credit_card_processors
        get_credit_card_processors,
        // credit_card_transactions
        add_credit_card_transaction,
        credit_card_transaction_authorize_completed,
        credit_card_transaction_sale_completed,
        get_credit_card_transactions,
        // credit_cards
        get_credit_cards,
        add_credit_card,
        delete_credit_card,
        edit_credit_card,
        // failover_file_logs
        add_failover_file_log,
        // failover_file_replications
        request_replication_daemon_access,
        // ip_addresses
        set_ip_address_dhcp_address,
        // linux_accounts
        set_linux_account_password,
        set_linux_account_predisable_password,
        // mysql_databases
        check_mysql_tables,
        get_mysql_table_status,
        // mysql_servers
        get_mysql_master_status,
        get_mysql_slave_status,
        // mysql_users
        set_mysql_user_password,
        set_mysql_user_predisable_password,
        // postgres_server_users
        set_postgres_user_password,
        set_postgres_user_predisable_password,
        // tickets
        add_ticket,
        edit_ticket,
        // virtual_servers
        vnc_console
        ;

        /**
         * Gets the permission display value in the JVM-default locale.
         */
        @Override
        public String toString() {
            return toString(Locale.getDefault());
        }
        
        /**
         * Gets the permission display value in the provided locale.
         */
        public String toString(Locale userLocale) {
            return ApplicationResources.accessor.getMessage(userLocale, "AOServPermission."+name()+".toString");
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private short sortOrder;

    public AOServPermission(AOServPermissionService<?,?> service, String name, short sortOrder) {
        super(service, name);
        this.sortOrder = sortOrder;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(AOServPermission other) {
        return AOServObjectUtils.compare(sortOrder, other.sortOrder);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="name", index=IndexType.PRIMARY_KEY, description="the unique name of the permission")
    public String getName() {
        return key;
    }

    @SchemaColumn(order=1, name="sort_order", index=IndexType.UNIQUE, description="the sort order for the permission")
    public short getSortOrder() {
        return sortOrder;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.AOServPermission getBean() {
        return new com.aoindustries.aoserv.client.beans.AOServPermission(key, sortOrder);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl(Locale userLocale) {
        return ApplicationResources.accessor.getMessage(userLocale, "AOServPermission."+key+".toString");
    }

    /**
     * Gets the locale-specific description of this permission.
     */
    public String getDescription(Locale userLocale) {
        return ApplicationResources.accessor.getMessage(userLocale, "AOServPermission."+key+".description");
    }
    // </editor-fold>
}
