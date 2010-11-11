/*
 * Copyright 2007-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import com.aoindustries.util.UnionSet;
import java.rmi.RemoteException;

/**
 * All of the permissions within the system.
 *
 * @author  AO Industries, Inc.
 */
final public class AOServPermission extends AOServObjectStringKey<AOServPermission> implements DtoFactory<com.aoindustries.aoserv.client.dto.AOServPermission>, java.security.acl.Permission {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    /**
     * The possible permissions.
     */
    public enum Permission {
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
        // ticket_actions
        add_ticket_annotation,
        // tickets
        add_ticket,
        edit_ticket,
        get_ticket_details,
        // transactions
        add_transaction,
        get_transaction_description,
        // virtual_servers
        vnc_console
        ;

        @Override
        public String toString() {
            return ApplicationResources.accessor.getMessage("AOServPermission."+name()+".toString");
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
        return getKey();
    }

    @SchemaColumn(order=1, name="sort_order", index=IndexType.UNIQUE, description="the sort order for the permission")
    public short getSortOrder() {
        return sortOrder;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    @Override
    public com.aoindustries.aoserv.client.dto.AOServPermission getDto() {
        return new com.aoindustries.aoserv.client.dto.AOServPermission(getKey(), sortOrder);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionSet<AOServObject> addDependentObjects(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getAoservRolePermissions());
        return unionSet;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() {
        return ApplicationResources.accessor.getMessage("AOServPermission."+getKey()+".toString");
    }

    /**
     * Gets the locale-specific description of this permission.
     */
    public String getDescription() {
        return ApplicationResources.accessor.getMessage("AOServPermission."+getKey()+".description");
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public IndexedSet<AOServRolePermission> getAoservRolePermissions() throws RemoteException {
        return getService().getConnector().getAoservRolePermissions().filterIndexed(AOServRolePermission.COLUMN_PERMISSION, this);
    }
    // </editor-fold>
}
