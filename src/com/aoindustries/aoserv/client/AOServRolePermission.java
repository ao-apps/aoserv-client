/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import com.aoindustries.util.UnionClassSet;
import com.aoindustries.util.WrappedException;
import java.rmi.RemoteException;

/**
 * All of the permissions granted for each role.
 *
 * @author  AO Industries, Inc.
 */
final public class AOServRolePermission
extends AOServObjectIntegerKey
implements
    Comparable<AOServRolePermission>,
    DtoFactory<com.aoindustries.aoserv.client.dto.AOServRolePermission> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private int role;
    private String permission;

    public AOServRolePermission(AOServConnector connector, int pkey, int role, String permission) {
        super(connector, pkey);
        this.role = role;
        this.permission = permission;
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        permission = intern(permission);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(AOServRolePermission other) {
        try {
            int diff = role==other.role ? 0 : getRole().compareTo(other.getRole());
            if(diff!=0) return diff;
            return permission==other.permission ? 0 : getPermission().compareTo(other.getPermission());
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

    static final String COLUMN_ROLE = "role";
    @SchemaColumn(order=1, name=COLUMN_ROLE, index=IndexType.INDEXED, description="the role")
    public AOServRole getRole() throws RemoteException {
        return getConnector().getAoservRoles().get(role);
    }

    static final String COLUMN_PERMISSION = "permission";
    @SchemaColumn(order=2, name=COLUMN_PERMISSION, index=IndexType.INDEXED, description="the permission that is granted by this role")
    public AOServPermission getPermission() throws RemoteException {
        return getConnector().getAoservPermissions().get(permission);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public AOServRolePermission(AOServConnector connector, com.aoindustries.aoserv.client.dto.AOServRolePermission dto) {
        this(connector, dto.getPkey(), dto.getRole(), dto.getPermission());
    }

    @Override
    public com.aoindustries.aoserv.client.dto.AOServRolePermission getDto() {
        return new com.aoindustries.aoserv.client.dto.AOServRolePermission(key, role, permission);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionClassSet<AOServObject<?>> addDependencies(UnionClassSet<AOServObject<?>> unionSet) throws RemoteException {
        unionSet = super.addDependencies(unionSet);
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getRole());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getPermission());
        return unionSet;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() throws RemoteException {
        return ApplicationResources.accessor.getMessage("AOServRolePermission.toString", getRole().getName(), getPermission().toStringImpl());
    }
    // </editor-fold>
}
