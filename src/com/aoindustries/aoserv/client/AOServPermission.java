package com.aoindustries.aoserv.client;

/*
 * Copyright 2007-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.util.i18n.LocalizedToString;
import java.util.Locale;

/**
 * All of the permissions within the system.
 *
 * @author  AO Industries, Inc.
 */
final public class AOServPermission extends AOServObjectStringKey<AOServPermission> {

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
        // linux_server_accounts
        set_linux_server_account_password,
        // mysql_databases
        check_mysql_tables,
        get_mysql_table_status,
        // mysql_servers
        get_mysql_master_status,
        get_mysql_slave_status,
        // mysql_users
        set_mysql_user_password,
        // postgres_server_users
        set_postgres_server_user_password,
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
    final private short sort_order;

    public AOServPermission(AOServPermissionService<?,?> service, String name, short sort_order) {
        super(service, name);
        this.sort_order = sort_order;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(AOServPermission other) {
        return compare(sort_order, other.sort_order);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="name", unique=true, description="the unique name of the permission")
    public String getName() {
        return key;
    }

    @SchemaColumn(order=1, name="sort_order", unique=true, description="the sort order for the permission")
    public short getSortOrder() {
        return sort_order;
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
