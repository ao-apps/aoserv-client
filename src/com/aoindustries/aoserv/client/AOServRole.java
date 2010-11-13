/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.UnionSet;
import com.aoindustries.util.WrappedException;
import java.rmi.RemoteException;

/**
 * All of the roles within the system.
 *
 * @author  AO Industries, Inc.
 */
final public class AOServRole extends AOServObjectIntegerKey<AOServRole> implements Comparable<AOServRole>, DtoFactory<com.aoindustries.aoserv.client.dto.AOServRole> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private AccountingCode accounting;
    private String name;

    public AOServRole(AOServRoleService<?,?> service, int pkey, AccountingCode accounting, String name) {
        super(service, pkey);
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
            int diff = accounting==other.accounting ? 0 : AOServObjectUtils.compare(getBusiness(), other.getBusiness());
            if(diff!=0) return diff;
            return AOServObjectUtils.compareIgnoreCaseConsistentWithEquals(name, other.name);
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="pkey", index=IndexType.PRIMARY_KEY, description="a generated unique id")
    public int getPkey() {
        return key;
    }

    static final String COLUMN_ACCOUNTING = "accounting";
    /**
     * May be filtered.
     */
    @SchemaColumn(order=1, name=COLUMN_ACCOUNTING, index=IndexType.INDEXED, description="the business that owns the role")
    public Business getBusiness() throws RemoteException {
        return getService().getConnector().getBusinesses().filterUnique(Business.COLUMN_ACCOUNTING, accounting);
    }

    @SchemaColumn(order=2, name="name", description="the per-business unique role name")
    public String getName() {
        return name;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    @Override
    public com.aoindustries.aoserv.client.dto.AOServRole getDto() {
        return new com.aoindustries.aoserv.client.dto.AOServRole(key, getDto(accounting), name);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionSet<AOServObject> addDependencies(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getBusiness());
        return unionSet;
    }

    @Override
    protected UnionSet<AOServObject> addDependentObjects(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getAoservRolePermissions());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getBusinessAdministratorRoles());
        return unionSet;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() {
        return name;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public IndexedSet<AOServRolePermission> getAoservRolePermissions() throws RemoteException {
        return getService().getConnector().getAoservRolePermissions().filterIndexed(AOServRolePermission.COLUMN_ROLE, this);
    }

    public IndexedSet<BusinessAdministratorRole> getBusinessAdministratorRoles() throws RemoteException {
        return getService().getConnector().getBusinessAdministratorRoles().filterIndexed(BusinessAdministratorRole.COLUMN_ROLE, this);
    }
    // </editor-fold>
}
