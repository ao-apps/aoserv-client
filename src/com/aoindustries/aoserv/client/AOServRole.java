/*
 * Copyright 2010-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.WrappedException;
import java.rmi.RemoteException;

/**
 * All of the roles within the system.
 *
 * @author  AO Industries, Inc.
 */
final public class AOServRole extends AOServObjectIntegerKey implements Comparable<AOServRole>, DtoFactory<com.aoindustries.aoserv.client.dto.AOServRole> {

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private static final long serialVersionUID = 6726180835761119511L;

    private AccountingCode accounting;
    private String name;

    public AOServRole(AOServConnector connector, int pkey, AccountingCode accounting, String name) {
        super(connector, pkey);
        this.accounting = accounting;
        this.name = name;
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        accounting = intern(accounting);
        name = intern(name);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(AOServRole other) {
        try {
            int diff = accounting==other.accounting ? 0 : compare(getBusiness(), other.getBusiness());
            if(diff!=0) return diff;
            return compareIgnoreCaseConsistentWithEquals(name, other.name);
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, index=IndexType.PRIMARY_KEY, description="a generated unique id")
    public int getPkey() {
        return getKeyInt();
    }

    public static final MethodColumn COLUMN_BUSINESS = getMethodColumn(AOServRole.class, "business");
    /**
     * May be filtered.
     */
    @DependencySingleton
    @SchemaColumn(order=1, index=IndexType.INDEXED, description="the business that owns the role")
    public Business getBusiness() throws RemoteException {
        return getConnector().getBusinesses().filterUnique(Business.COLUMN_ACCOUNTING, accounting);
    }

    @SchemaColumn(order=2, description="the per-business unique role name")
    public String getName() {
        return name;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public AOServRole(AOServConnector connector, com.aoindustries.aoserv.client.dto.AOServRole dto) throws ValidationException {
        this(
            connector,
            dto.getPkey(),
            getAccountingCode(dto.getAccounting()),
            dto.getName()
        );
    }

    @Override
    public com.aoindustries.aoserv.client.dto.AOServRole getDto() {
        return new com.aoindustries.aoserv.client.dto.AOServRole(getKeyInt(), getDto(accounting), name);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() {
        return name;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    @DependentObjectSet
    public IndexedSet<AOServRolePermission> getAoservRolePermissions() throws RemoteException {
        return getConnector().getAoservRolePermissions().filterIndexed(AOServRolePermission.COLUMN_ROLE, this);
    }

    @DependentObjectSet
    public IndexedSet<BusinessAdministratorRole> getBusinessAdministratorRoles() throws RemoteException {
        return getConnector().getBusinessAdministratorRoles().filterIndexed(BusinessAdministratorRole.COLUMN_ROLE, this);
    }
    // </editor-fold>
}
