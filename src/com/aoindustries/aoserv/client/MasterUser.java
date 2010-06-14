/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;
import java.util.Set;

/**
 * A <code>MasterUser</code> is a <code>BusinessAdministrator</code> who
 * has greater permissions.  Their access is secure on a per-<code>Server</code>
 * basis, and may also include full access to DNS, backups, and other
 * systems.
 *
 * @see  BusinessAdministrator
 * @see  MasterHost
 * @see  MasterServer
 *
 * @author  AO Industries, Inc.
 */
final public class MasterUser extends AOServObjectUserIdKey<MasterUser> implements BeanFactory<com.aoindustries.aoserv.client.beans.MasterUser> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private boolean isActive;
    final private boolean canAccessAccounting;
    final private boolean canAccessBankAccount;
    final private boolean canInvalidateTables;
    final private boolean canAccessAdminWeb;
    final private boolean isDnsAdmin;

    public MasterUser(
        MasterUserService<?,?> service,
        UserId username,
        boolean isActive,
        boolean canAccessAccounting,
        boolean canAccessBankAccount,
        boolean canInvalidateTables,
        boolean canAccessAdminWeb,
        boolean isDnsAdmin
    ) {
        super(service, username);
        this.isActive = isActive;
        this.canAccessAccounting = canAccessAccounting;
        this.canAccessBankAccount = canAccessBankAccount;
        this.canInvalidateTables = canInvalidateTables;
        this.canAccessAdminWeb = canAccessAdminWeb;
        this.isDnsAdmin = isDnsAdmin;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    static final String COLUMN_USERNAME = "username";
    @SchemaColumn(order=0, name=COLUMN_USERNAME, index=IndexType.PRIMARY_KEY, description="the unique username of this master user")
    public BusinessAdministrator getBusinessAdministrator() throws RemoteException {
        return getService().getConnector().getBusinessAdministrators().get(getKey());
    }

    @SchemaColumn(order=1, name="is_active", description="this level of access may be disabled")
    public boolean isActive() {
        return isActive;
    }

    @SchemaColumn(order=2, name="can_access_accounting", description="if they can access accounting resources")
    public boolean getCanAccessAccounting() {
        return canAccessAccounting;
    }

    @SchemaColumn(order=3, name="can_access_bank_account", description="if they can access bank account info")
    public boolean getCanAccessBankAccount() {
        return canAccessBankAccount;
    }

    @SchemaColumn(order=4, name="can_invalidate_tables", description="if they can invalidate master tables")
    public boolean getCanInvalidateTables() {
        return canInvalidateTables;
    }

    @SchemaColumn(order=5, name="can_access_admin_web", description="if they can access administrative web pages")
    public boolean isWebAdmin() {
        return canAccessAdminWeb;
    }

    @SchemaColumn(order=6, name="is_dns_admin", description="if they can access all DNS zones and records")
    public boolean isDnsAdmin() {
        return isDnsAdmin;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.MasterUser getBean() {
        return new com.aoindustries.aoserv.client.beans.MasterUser(getBean(getKey()), isActive, canAccessAccounting, canAccessBankAccount, canInvalidateTables, canAccessAdminWeb, isDnsAdmin);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependencies() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getBusinessAdministrator()
        );
    }

    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            // TODO: getBankTransactions(),
            getMasterHosts(),
            getMasterServers(),
            getTechnologyVersions()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    /* TODO
    public List<BankTransaction> getBankTransactions() throws IOException, SQLException {
        return getService().getConnector().getBankTransactions().getIndexedRows(BankTransaction.COLUMN_ADMINISTRATOR, pkey);
    }
     */
    public IndexedSet<MasterHost> getMasterHosts() throws RemoteException {
        return getService().getConnector().getMasterHosts().filterIndexed(MasterHost.COLUMN_USERNAME, this);
    }

    public IndexedSet<MasterServer> getMasterServers() throws RemoteException {
        return getService().getConnector().getMasterServers().filterIndexed(MasterServer.COLUMN_USERNAME, this);
    }

    public IndexedSet<TechnologyVersion> getTechnologyVersions() throws RemoteException {
        return getService().getConnector().getTechnologyVersions().filterIndexed(TechnologyVersion.COLUMN_OWNER, this);
    }
    // </editor-fold>
}