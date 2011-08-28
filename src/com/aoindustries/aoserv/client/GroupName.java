/*
 * Copyright 2010-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.table.IndexType;
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
	
    // <editor-fold defaultstate="collapsed" desc="Fields">
    private static final long serialVersionUID = -5216804200826962L;

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
    @SchemaColumn(order=0, index=IndexType.PRIMARY_KEY, description="the unique group name")
    public GroupId getGroupName() {
        return getKey();
    }

    /**
     * May be filtered.
     */
    public static final MethodColumn COLUMN_BUSINESS = getMethodColumn(GroupName.class, "business");
    @DependencySingleton
    @SchemaColumn(order=1, index=IndexType.INDEXED, description="the business that this group is part of")
    public Business getBusiness() throws RemoteException {
    	return getConnector().getBusinesses().filterUnique(Business.COLUMN_ACCOUNTING, accounting);
    }

    public static final MethodColumn COLUMN_DISABLE_LOG = getMethodColumn(GroupName.class, "disableLog");
    @DependencySingleton
    @SchemaColumn(order=2, index=IndexType.INDEXED, description="indicates that the group name is disabled")
    public DisableLog getDisableLog() throws RemoteException {
        if(disableLog==null) return null;
        return getConnector().getDisableLogs().get(disableLog);
    }
    public boolean isDisabled() {
        return disableLog!=null;
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

    // <editor-fold defaultstate="collapsed" desc="Relations">
    @DependentObjectSet
    public IndexedSet<LinuxGroup> getLinuxGroups() throws RemoteException {
        return getConnector().getLinuxGroups().filterIndexed(LinuxGroup.COLUMN_GROUP_NAME, this);
    }
    // </editor-fold>
}