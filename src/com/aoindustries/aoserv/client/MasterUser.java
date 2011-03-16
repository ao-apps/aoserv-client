/*
 * Copyright 2001-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;

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
final public class MasterUser extends AOServObjectUserIdKey implements Comparable<MasterUser>, DtoFactory<com.aoindustries.aoserv.client.dto.MasterUser> {

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private static final long serialVersionUID = -4273739122344650408L;

    final private boolean active;
    final private boolean canAccessAccounting;
    final private boolean canAccessBankAccount;
    final private boolean canInvalidateTables;
    final private boolean canAccessAdminWeb;
    final private boolean dnsAdmin;

    public MasterUser(
        AOServConnector connector,
        UserId username,
        boolean active,
        boolean canAccessAccounting,
        boolean canAccessBankAccount,
        boolean canInvalidateTables,
        boolean canAccessAdminWeb,
        boolean dnsAdmin
    ) {
        super(connector, username);
        this.active = active;
        this.canAccessAccounting = canAccessAccounting;
        this.canAccessBankAccount = canAccessBankAccount;
        this.canInvalidateTables = canInvalidateTables;
        this.canAccessAdminWeb = canAccessAdminWeb;
        this.dnsAdmin = dnsAdmin;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(MasterUser other) {
        return getKey().compareTo(other.getKey());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    public static final MethodColumn COLUMN_BUSINESS_ADMINISTRATOR = getMethodColumn(MasterUser.class, "businessAdministrator");
    @DependencySingleton
    @SchemaColumn(order=0, index=IndexType.PRIMARY_KEY, description="the unique username of this master user")
    public BusinessAdministrator getBusinessAdministrator() throws RemoteException {
        return getConnector().getBusinessAdministrators().get(getKey());
    }

    @SchemaColumn(order=1, description="this level of access may be disabled")
    public boolean isActive() {
        return active;
    }

    @SchemaColumn(order=2, description="if they can access accounting resources")
    public boolean getCanAccessAccounting() {
        return canAccessAccounting;
    }

    @SchemaColumn(order=3, description="if they can access bank account info")
    public boolean getCanAccessBankAccount() {
        return canAccessBankAccount;
    }

    @SchemaColumn(order=4, description="if they can invalidate master tables")
    public boolean getCanInvalidateTables() {
        return canInvalidateTables;
    }

    @SchemaColumn(order=5, description="if they can access administrative web pages")
    public boolean getCanAccessAdminWeb() {
        return canAccessAdminWeb;
    }

    @SchemaColumn(order=6, description="if they can access all DNS zones and records")
    public boolean isDnsAdmin() {
        return dnsAdmin;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public MasterUser(AOServConnector connector, com.aoindustries.aoserv.client.dto.MasterUser dto) throws ValidationException {
        this(
            connector,
            getUserId(dto.getUsername()),
            dto.isActive(),
            dto.isCanAccessAccounting(),
            dto.isCanAccessBankAccount(),
            dto.isCanInvalidateTables(),
            dto.isCanAccessAdminWeb(),
            dto.isDnsAdmin()
        );
    }

    @Override
    public com.aoindustries.aoserv.client.dto.MasterUser getDto() {
        return new com.aoindustries.aoserv.client.dto.MasterUser(getDto(getKey()), active, canAccessAccounting, canAccessBankAccount, canInvalidateTables, canAccessAdminWeb, dnsAdmin);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    @DependentObjectSet
    public IndexedSet<BankTransaction> getBankTransactions() throws RemoteException {
        return getConnector().getBankTransactions().filterIndexed(BankTransaction.COLUMN_ADMINISTRATOR, this);
    }

    @DependentObjectSet
    public IndexedSet<MasterHost> getMasterHosts() throws RemoteException {
        return getConnector().getMasterHosts().filterIndexed(MasterHost.COLUMN_MASTER_USER, this);
    }

    @DependentObjectSet
    public IndexedSet<MasterServer> getMasterServers() throws RemoteException {
        return getConnector().getMasterServers().filterIndexed(MasterServer.COLUMN_MASTER_USER, this);
    }

    @DependentObjectSet
    public IndexedSet<TechnologyVersion> getTechnologyVersions() throws RemoteException {
        return getConnector().getTechnologyVersions().filterIndexed(TechnologyVersion.COLUMN_OWNER, this);
    }
    // </editor-fold>
}