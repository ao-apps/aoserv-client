/*
 * Copyright 2000-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import com.aoindustries.util.WrappedException;
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
final public class LinuxAccountGroup extends AOServObjectIntegerKey implements Comparable<LinuxAccountGroup>, DtoFactory<com.aoindustries.aoserv.client.dto.LinuxAccountGroup> /*, Removable */ {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    /**
     * The maximum number of groups allowed for one account.
     */
    public static final int MAX_GROUPS=31;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private static final long serialVersionUID = -3048390113837358515L;

    private final int linuxAccount;
    private final int linuxGroup;
    private final boolean primary;

    public LinuxAccountGroup(AOServConnector connector, int pkey, int linuxAccount, int linuxGroup, boolean isPrimary) {
        super(connector, pkey);
        this.linuxAccount = linuxAccount;
        this.linuxGroup = linuxGroup;
        this.primary = isPrimary;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(LinuxAccountGroup other) {
        try {
            int diff = linuxAccount==other.linuxAccount ? 0 : getLinuxAccount().compareTo(other.getLinuxAccount());
            if(diff!=0) return diff;
            return linuxGroup==other.linuxGroup ? 0 : getLinuxGroup().compareTo(other.getLinuxGroup());
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    public static final MethodColumn COLUMN_PKEY = getMethodColumn(LinuxAccountGroup.class, "pkey");
    @SchemaColumn(order=0, index=IndexType.PRIMARY_KEY, description="a generated unique id")
    public int getPkey() {
        return getKeyInt();
    }

    public static final MethodColumn COLUMN_LINUX_ACCOUNT = getMethodColumn(LinuxAccountGroup.class, "linuxAccount");
    @DependencySingleton
    @SchemaColumn(order=1, index=IndexType.INDEXED, description="the linux account that belongs to the group")
    public LinuxAccount getLinuxAccount() throws RemoteException {
        return getConnector().getLinuxAccounts().get(linuxAccount);
    }

    public static final MethodColumn COLUMN_LINUX_GROUP = getMethodColumn(LinuxAccountGroup.class, "linuxGroup");
    @DependencySingleton
    @SchemaColumn(order=2, index=IndexType.INDEXED, description="the linux group that the account belongs to")
    public LinuxGroup getLinuxGroup() throws RemoteException {
        return getConnector().getLinuxGroups().get(linuxGroup);
    }

    public static final MethodColumn COLUMN_IS_PRIMARY = getMethodColumn(LinuxAccountGroup.class, "primary");
    @SchemaColumn(order=3, index=IndexType.INDEXED, description="flag showing that this group is the user's primary group")
    public boolean isPrimary() {
        return primary;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public LinuxAccountGroup(AOServConnector connector, com.aoindustries.aoserv.client.dto.LinuxAccountGroup dto) {
        this(
            connector,
            dto.getPkey(),
            dto.getLinuxAccount(),
            dto.getLinuxGroup(),
            dto.isPrimary()
        );
    }

    @Override
    public com.aoindustries.aoserv.client.dto.LinuxAccountGroup getDto() {
        return new com.aoindustries.aoserv.client.dto.LinuxAccountGroup(getKeyInt(), linuxAccount, linuxGroup, primary);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() throws RemoteException {
        LinuxAccount la = getLinuxAccount();
        return ApplicationResources.accessor.getMessage(
            "LinuxAccountGroup.toString",
            la.getAoServer().getHostname(),
            la.getUserId(),
            getLinuxGroup().getGroupId()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    @DependentObjectSet
    public IndexedSet<CvsRepository> getCvsRepositories() throws RemoteException {
        return getConnector().getCvsRepositories().filterIndexed(CvsRepository.COLUMN_LINUX_ACCOUNT_GROUP, this);
    }

    @DependentObjectSet
    public IndexedSet<HttpdSite> getHttpdSites() throws RemoteException {
        return getConnector().getHttpdSites().filterIndexed(HttpdSite.COLUMN_LINUX_ACCOUNT_GROUP, this);
    }

    @DependentObjectSet
    public IndexedSet<HttpdServer> getHttpdServers() throws RemoteException {
        return getConnector().getHttpdServers().filterIndexed(HttpdServer.COLUMN_LINUX_ACCOUNT_GROUP, this);
    }

    @DependentObjectSet
    public IndexedSet<PrivateFtpServer> getPrivateFtpServers() throws RemoteException {
        return getConnector().getPrivateFtpServers().filterIndexed(PrivateFtpServer.COLUMN_LINUX_ACCOUNT_GROUP, this);
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
        getConnector().requestUpdateIL(
            true,
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.LINUX_GROUP_ACCOUNTS,
            pkey
        );
    }

    void setAsPrimary() throws IOException, SQLException {
        getConnector().requestUpdateIL(
            true,
            AOServProtocol.CommandID.SET_PRIMARY_LINUX_GROUP_ACCOUNT,
            pkey
        );
    }
     */
    // </editor-fold>
}
