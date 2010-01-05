/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;
import java.util.Set;

/**
 * Each <code>GroupName</code> is unique across all systems and must
 * be allocated to a <code>Business</code> before use in any of the
 * account types.
 *
 * @see  LinuxGroup
 *
 * @author  AO Industries, Inc.
 */
final public class GroupName extends AOServObjectStringKey<GroupName> implements BeanFactory<com.aoindustries.aoserv.client.beans.GroupName> {
	
    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    public static final int MAX_LENGTH=255;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final String accounting;
    final Integer disableLog;

    public GroupName(GroupNameService<?,?> table, String groupName, String accounting, Integer disableLog) {
        super(table, groupName);
        this.accounting = accounting.intern();
        this.disableLog = disableLog;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="group_name", index=IndexType.PRIMARY_KEY, description="the unique group name")
    public String getGroupName() {
        return key;
    }

    /**
     * May be filtered.
     */
    static final String COLUMN_ACCOUNTING = "accounting";
    @SchemaColumn(order=1, name=COLUMN_ACCOUNTING, index=IndexType.INDEXED, description="the business that this group is part of")
    public Business getBusiness() throws RemoteException {
    	return getService().getConnector().getBusinesses().get(accounting);
    }

    static final String COLUMN_DISABLE_LOG = "disable_log";
    @SchemaColumn(order=2, name=COLUMN_DISABLE_LOG, index=IndexType.INDEXED, description="indicates that the group name is disabled")
    public DisableLog getDisableLog() throws RemoteException {
        if(disableLog==null) return null;
        DisableLog obj=getService().getConnector().getDisableLogs().get(disableLog);
        if(obj==null) throw new RemoteException("Unable to find DisableLog: "+disableLog);
        return obj;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.GroupName getBean() {
        return new com.aoindustries.aoserv.client.beans.GroupName(key, accounting, disableLog);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependencies() throws RemoteException {
        return createDependencySet(
            getBusiness(),
            getDisableLog()
        );
    }

    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return createDependencySet(
            getLinuxGroups()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public Set<LinuxGroup> getLinuxGroups() throws RemoteException {
        return getService().getConnector().getLinuxGroups().getIndexed(LinuxGroup.COLUMN_GROUP_NAME, this);
    }
    // </editor-fold>
}