package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.validator.GroupId;
import com.aoindustries.aoserv.client.validator.LinuxID;
import com.aoindustries.aoserv.client.validator.ValidationException;
import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.Set;

/**
 * A <code>LinuxGroup</code> may exist on multiple <code>Server</code>s.
 * The information common across all servers is stored is a <code>LinuxGroup</code>.
 *
 * @see  LinuxServerGroup
 *
 * @author  AO Industries, Inc.
 */
final public class LinuxGroup extends AOServObjectIntegerKey<LinuxGroup> implements BeanFactory<com.aoindustries.aoserv.client.beans.LinuxGroup> /* Removable*/ {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    /**
     * Some commonly used system and application groups.
     */
    public static final GroupId
        ADM,
        APACHE,
        AWSTATS,
        BIN,
        DAEMON,
        FTP,
        MAIL,
        MAILONLY,
        NAMED,
        NOGROUP,
        POSTGRES,
        ROOT,
        SYS,
        TTY
    ;

    static {
        try {
            ADM = GroupId.valueOf("adm").intern();
            APACHE = GroupId.valueOf("apache").intern();
            AWSTATS = GroupId.valueOf("awstats").intern();
            BIN = GroupId.valueOf("bin").intern();
            DAEMON = GroupId.valueOf("").intern();
            FTP = GroupId.valueOf("ftp").intern();
            MAIL = GroupId.valueOf("mail").intern();
            MAILONLY = GroupId.valueOf("mailonly").intern();
            NAMED = GroupId.valueOf("named").intern();
            NOGROUP = GroupId.valueOf("nogroup").intern();
            POSTGRES = GroupId.valueOf("postgres").intern();
            ROOT = GroupId.valueOf("root").intern();
            SYS = GroupId.valueOf("sys").intern();
            TTY = GroupId.valueOf("tty").intern();
        } catch(ValidationException err) {
            throw new AssertionError(err.getMessage());
        }
    }

    /**
     * @deprecated  Group httpd no longer used.
     */
    @Deprecated
    public static final String HTTPD="httpd";
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private String linuxGroupType;
    final private GroupId groupName;
    final private LinuxID gid;

    public LinuxGroup(
        LinuxGroupService<?,?> service,
        int aoServerResource,
        String linuxGroupType,
        GroupId groupName,
        LinuxID gid
    ) {
        super(service, aoServerResource);
        this.linuxGroupType = linuxGroupType.intern();
        this.groupName = groupName.intern();
        this.gid = gid;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(LinuxGroup other) throws RemoteException {
        if(key==other.key) return 0;
        int diff = groupName.equals(other.groupName) ? 0 : getGroupName().compareTo(other.getGroupName());
        if(diff!=0) return diff;
        AOServerResource aor1 = getAoServerResource();
        AOServerResource aor2 = other.getAoServerResource();
        return aor1.aoServer==aor2.aoServer ? 0 : aor1.getAoServer().compareTo(aor2.getAoServer());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="ao_server_resource", index=IndexType.PRIMARY_KEY, description="the unique resource id")
    public AOServerResource getAoServerResource() throws RemoteException {
        return getService().getConnector().getAoServerResources().get(key);
    }

    static final String COLUMN_LINUX_GROUP_TYPE = "linux_group_type";
    @SchemaColumn(order=1, name=COLUMN_LINUX_GROUP_TYPE, index=IndexType.INDEXED, description="the type of group")
    public LinuxGroupType getLinuxGroupType() throws RemoteException {
        return getService().getConnector().getLinuxGroupTypes().get(linuxGroupType);
    }

    static final String COLUMN_GROUP_NAME = "group_name";
    @SchemaColumn(order=2, name=COLUMN_GROUP_NAME, index=IndexType.INDEXED, description="the name of the group")
    public GroupName getGroupName() throws RemoteException {
        return getService().getConnector().getGroupNames().get(groupName);
    }

    @SchemaColumn(order=3, name="gid", description="the gid of the group on the machine")
    public LinuxID getGid() {
        return gid;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.LinuxGroup getBean() {
        return new com.aoindustries.aoserv.client.beans.LinuxGroup(
            key,
            linuxGroupType,
            groupName.getBean(),
            gid.getBean()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependencies() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getAoServerResource(),
            getLinuxGroupType(),
            getGroupName()
        );
    }

    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getLinuxAccountGroups()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl(Locale userLocale) throws RemoteException {
        return ApplicationResources.accessor.getMessage(userLocale, "LinuxGroup.toString", groupName, getAoServerResource().getAoServer().getHostname());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public IndexedSet<LinuxAccountGroup> getLinuxAccountGroups() throws RemoteException {
        return getService().getConnector().getLinuxAccountGroups().filterIndexed(LinuxAccountGroup.COLUMN_LINUX_GROUP, this);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
    /* TODO
    public int addLinuxAccount(LinuxAccount account) throws IOException, SQLException {
        return getService().getConnector().getLinuxGroupAccounts().addLinuxGroupAccount(this, account);
    }

    public int addLinuxServerGroup(AOServer aoServer) throws IOException, SQLException {
        return getService().getConnector().getLinuxServerGroups().addLinuxServerGroup(this, aoServer);
    }

    public LinuxServerGroup getLinuxServerGroup(AOServer aoServer) throws IOException, SQLException {
        return getService().getConnector().getLinuxServerGroups().getLinuxServerGroup(aoServer, pkey);
    }

    public List<CannotRemoveReason> getCannotRemoveReasons(Locale userLocale) throws IOException, SQLException {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();

        // Cannot be the primary group for any linux accounts
        for(LinuxGroupAccount lga : getService().getConnector().getLinuxGroupAccounts().getRows()) {
            if(lga.isPrimary() && equals(lga.getLinuxGroup())) {
                reasons.add(new CannotRemoveReason<LinuxGroupAccount>("Used as primary group for Linux account "+lga.getLinuxAccount().getUsername().getUsername(), lga));
            }
        }

        // All LinuxServerGroups must be removable
        for(LinuxServerGroup lsg : getLinuxServerGroups()) reasons.addAll(lsg.getCannotRemoveReasons(userLocale));

        return reasons;
    }

    public void remove() throws IOException, SQLException {
        getService().getConnector().requestUpdateIL(
            true,
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.LINUX_GROUPS,
            pkey
        );
    }
    */
    // </editor-fold>
}