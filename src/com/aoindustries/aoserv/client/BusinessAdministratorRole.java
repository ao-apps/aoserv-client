/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.UnionSet;
import java.rmi.RemoteException;

/**
 * Associates a role with a business administrator.
 *
 * @author  AO Industries, Inc.
 */
final public class BusinessAdministratorRole extends AOServObjectIntegerKey<BusinessAdministratorRole> implements BeanFactory<com.aoindustries.aoserv.client.beans.BusinessAdministratorRole> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private UserId username;
    final private int role;

    public BusinessAdministratorRole(BusinessAdministratorRoleService<?,?> service, int pkey, UserId username, int role) {
        super(service, pkey);
        this.username = username;
        this.role = role;
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        username = intern(username);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(BusinessAdministratorRole other) throws RemoteException {
        if(key==other.key) return 0;
        int diff = username==other.username ? 0 : getBusinessAdministrator().compareToImpl(other.getBusinessAdministrator());
        if(diff!=0) return diff;
        return role==other.role ? 0 : getRole().compareToImpl(other.getRole());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="pkey", index=IndexType.PRIMARY_KEY, description="a generated unique id")
    public int getPkey() {
        return key;
    }

    static final String COLUMN_USERNAME = "username";
    @SchemaColumn(order=1, name=COLUMN_USERNAME, index=IndexType.INDEXED, description="the business administrator who has the role")
    public BusinessAdministrator getBusinessAdministrator() throws RemoteException {
        return getService().getConnector().getBusinessAdministrators().get(username);
    }
    
    static final String COLUMN_ROLE = "role";
    @SchemaColumn(order=2, name=COLUMN_ROLE, index=IndexType.INDEXED, description="the role the business administrator has")
    public AOServRole getRole() throws RemoteException {
        return getService().getConnector().getAoservRoles().get(role);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    @Override
    public com.aoindustries.aoserv.client.beans.BusinessAdministratorRole getBean() {
        return new com.aoindustries.aoserv.client.beans.BusinessAdministratorRole(key, getBean(username), role);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionSet<AOServObject> addDependencies(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getBusinessAdministrator());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getRole());
        return unionSet;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() throws RemoteException {
        return ApplicationResources.accessor.getMessage("BusinessAdministratorRole.toString", username, getRole().getName());
    }
    // </editor-fold>
}
