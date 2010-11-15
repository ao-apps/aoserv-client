/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.UnionSet;
import java.rmi.RemoteException;

/**
 * Each <code>GroupName</code> is unique across all systems and must
 * be allocated to a <code>Business</code> before use in any of the
 * account types.
 *
 * @see  LinuxGroup
 *
 * @author  AO Industries, Inc.
 */
final public class GroupName extends AOServObjectGroupIdKey implements Comparable<GroupName>, DtoFactory<com.aoindustries.aoserv.client.dto.GroupName> {
	
    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private AccountingCode accounting;
    final Integer disableLog;

    public GroupName(AOServConnector connector, GroupId groupName, AccountingCode accounting, Integer disableLog) {
        super(connector, groupName);
        this.accounting = accounting;
        this.disableLog = disableLog;
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        accounting = intern(accounting);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(GroupName other) {
        return getKey().compareTo(other.getKey());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="group_name", index=IndexType.PRIMARY_KEY, description="the unique group name")
    public GroupId getGroupName() {
        return getKey();
    }

    /**
     * May be filtered.
     */
    static final String COLUMN_ACCOUNTING = "accounting";
    @SchemaColumn(order=1, name=COLUMN_ACCOUNTING, index=IndexType.INDEXED, description="the business that this group is part of")
    public Business getBusiness() throws RemoteException {
    	return getConnector().getBusinesses().filterUnique(Business.COLUMN_ACCOUNTING, accounting);
    }

    static final String COLUMN_DISABLE_LOG = "disable_log";
    @SchemaColumn(order=2, name=COLUMN_DISABLE_LOG, index=IndexType.INDEXED, description="indicates that the group name is disabled")
    public DisableLog getDisableLog() throws RemoteException {
        if(disableLog==null) return null;
        return getConnector().getDisableLogs().get(disableLog);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public GroupName(AOServConnector connector, com.aoindustries.aoserv.client.dto.GroupName dto) throws ValidationException {
        this(
            connector,
            getGroupId(dto.getGroupName()),
            getAccountingCode(dto.getAccounting()),
            dto.getDisableLog()
        );
    }

    @Override
    public com.aoindustries.aoserv.client.dto.GroupName getDto() {
        return new com.aoindustries.aoserv.client.dto.GroupName(getDto(getKey()), getDto(accounting), disableLog);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionSet<AOServObject> addDependencies(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = super.addDependencies(unionSet);
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getBusiness());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getDisableLog());
        return unionSet;
    }

    @Override
    protected UnionSet<AOServObject> addDependentObjects(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = super.addDependentObjects(unionSet);
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getLinuxGroups());
        return unionSet;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public IndexedSet<LinuxGroup> getLinuxGroups() throws RemoteException {
        return getConnector().getLinuxGroups().filterIndexed(LinuxGroup.COLUMN_GROUP_NAME, this);
    }
    // </editor-fold>
}