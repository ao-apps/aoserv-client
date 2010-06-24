/*
 * Copyright 2000-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import com.aoindustries.util.UnionSet;
import java.rmi.RemoteException;

/**
 * Each <code>LinuxGroup</code> may be accessed by any number
 * of <code>LinuxAccount</code>s.  The accounts are granted access
 * to a group via a <code>LinuxGroupAccount</code>.  One account
 * may access a maximum of 31 different groups.  Also, a
 * <code>LinuxAccount</code> must have one and only one primary
 * <code>LinuxAccountGroup</code>.
 *
 * @see  LinuxAccount
 * @see  LinuxGroup
 *
 * @author  AO Industries, Inc.
 */
final public class LinuxAccountGroup extends AOServObjectIntegerKey<LinuxAccountGroup> implements BeanFactory<com.aoindustries.aoserv.client.beans.LinuxAccountGroup> /*, Removable */ {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    /**
     * The maximum number of groups allowed for one account.
     */
    public static final int MAX_GROUPS=31;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private final int linuxAccount;
    private final int linuxGroup;
    private final boolean isPrimary;

    public LinuxAccountGroup(LinuxAccountGroupService<?,?> service, int pkey, int linuxAccount, int linuxGroup, boolean isPrimary) {
        super(service, pkey);
        this.linuxAccount = linuxAccount;
        this.linuxGroup = linuxGroup;
        this.isPrimary = isPrimary;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(LinuxAccountGroup other) throws RemoteException {
        int diff = linuxAccount==other.linuxAccount ? 0 : getLinuxAccount().compareToImpl(other.getLinuxAccount());
        if(diff!=0) return diff;
        return linuxGroup==other.linuxGroup ? 0 : getLinuxGroup().compareToImpl(other.getLinuxGroup());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    static final String COLUMN_PKEY = "pkey";
    @SchemaColumn(order=0, name=COLUMN_PKEY, index=IndexType.PRIMARY_KEY, description="a generated unique id")
    public int getPkey() {
        return key;
    }

    static final String COLUMN_LINUX_ACCOUNT = "linux_account";
    @SchemaColumn(order=1, name=COLUMN_LINUX_ACCOUNT, index=IndexType.INDEXED, description="the linux account that belongs to the group")
    public LinuxAccount getLinuxAccount() throws RemoteException {
        return getService().getConnector().getLinuxAccounts().get(linuxAccount);
    }

    static final String COLUMN_LINUX_GROUP = "linux_group";
    @SchemaColumn(order=2, name=COLUMN_LINUX_GROUP, index=IndexType.INDEXED, description="the linux group that the account belongs to")
    public LinuxGroup getLinuxGroup() throws RemoteException {
        return getService().getConnector().getLinuxGroups().get(linuxGroup);
    }

    static final String COLUMN_IS_PRIMARY = "is_primary";
    @SchemaColumn(order=3, name=COLUMN_IS_PRIMARY, index=IndexType.INDEXED, description="flag showing that this group is the user's primary group")
    public boolean isPrimary() {
        return isPrimary;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    @Override
    public com.aoindustries.aoserv.client.beans.LinuxAccountGroup getBean() {
        return new com.aoindustries.aoserv.client.beans.LinuxAccountGroup(key, linuxAccount, linuxGroup, isPrimary);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionSet<AOServObject> addDependencies(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getLinuxGroup());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getLinuxAccount());
        return unionSet;
    }

    @Override
    protected UnionSet<AOServObject> addDependentObjects(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getCvsRepositories());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getHttpdSites());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getHttpdServers());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getPrivateFtpServers());
        return unionSet;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() throws RemoteException {
        LinuxAccount la = getLinuxAccount();
        return ApplicationResources.accessor.getMessage(
            "LinuxAccountGroup.toString",
            la.getAoServerResource().getAoServer().getHostname(),
            la.getUsername().getUsername(),
            getLinuxGroup().getGroupName().getGroupName()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public IndexedSet<CvsRepository> getCvsRepositories() throws RemoteException {
        return getService().getConnector().getCvsRepositories().filterIndexed(CvsRepository.COLUMN_LINUX_ACCOUNT_GROUP, this);
    }

    public IndexedSet<HttpdSite> getHttpdSites() throws RemoteException {
        return getService().getConnector().getHttpdSites().filterIndexed(HttpdSite.COLUMN_LINUX_ACCOUNT_GROUP, this);
    }

    public IndexedSet<HttpdServer> getHttpdServers() throws RemoteException {
        return getService().getConnector().getHttpdServers().filterIndexed(HttpdServer.COLUMN_LINUX_ACCOUNT_GROUP, this);
    }

    public IndexedSet<PrivateFtpServer> getPrivateFtpServers() throws RemoteException {
        return getService().getConnector().getPrivateFtpServers().filterIndexed(PrivateFtpServer.COLUMN_LINUX_ACCOUNT_GROUP, this);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
    /* TODO
    public List<CannotRemoveReason> getCannotRemoveReasons() {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();
        if(is_primary) reasons.add(new CannotRemoveReason<LinuxAccountGroup>("Not allowed to drop a primary group", this));
        return reasons;
    }

    public void remove() throws IOException, SQLException {
        getService().getConnector().requestUpdateIL(
            true,
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.LINUX_GROUP_ACCOUNTS,
            pkey
        );
    }

    void setAsPrimary() throws IOException, SQLException {
        getService().getConnector().requestUpdateIL(
            true,
            AOServProtocol.CommandID.SET_PRIMARY_LINUX_GROUP_ACCOUNT,
            pkey
        );
    }
     */
    // </editor-fold>
}
