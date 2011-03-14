/*
 * Copyright 2000-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.WrappedException;
import java.rmi.RemoteException;

/**
 * A <code>LinuxGroup</code> may exist on multiple <code>Server</code>s.
 * The information common across all servers is stored is a <code>LinuxGroup</code>.
 *
 * @see  LinuxServerGroup
 *
 * @author  AO Industries, Inc.
 */
final public class LinuxGroup extends AOServerResource implements Comparable<LinuxGroup>, DtoFactory<com.aoindustries.aoserv.client.dto.LinuxGroup> /* Removable*/ {

    // <editor-fold defaultstate="collapsed" desc="Constants">
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
            DAEMON = GroupId.valueOf("daemon").intern();
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
    private static final long serialVersionUID = -6664240122878684478L;

    private String linuxGroupType;
    private GroupId groupName;
    final private LinuxID gid;

    public LinuxGroup(
        AOServConnector connector,
        int pkey,
        String resourceType,
        AccountingCode accounting,
        long created,
        UserId createdBy,
        Integer disableLog,
        long lastEnabled,
        int aoServer,
        int businessServer,
        String linuxGroupType,
        GroupId groupName,
        LinuxID gid
    ) {
        super(connector, pkey, resourceType, accounting, created, createdBy, disableLog, lastEnabled, aoServer, businessServer);
        this.linuxGroupType = linuxGroupType;
        this.groupName = groupName;
        this.gid = gid;
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        linuxGroupType = intern(linuxGroupType);
        groupName = intern(groupName);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(LinuxGroup other) {
        try {
            if(key==other.key) return 0;
            int diff = groupName==other.groupName ? 0 : getGroupName().compareTo(other.getGroupName());
            if(diff!=0) return diff;
            return aoServer==other.aoServer ? 0 : getAoServer().compareTo(other.getAoServer());
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    public static final MethodColumn COLUMN_LINUX_GROUP_TYPE = getMethodColumn(LinuxGroup.class, "linuxGroupType");
    @DependencySingleton
    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+1, index=IndexType.INDEXED, description="the type of group")
    public LinuxGroupType getLinuxGroupType() throws RemoteException {
        return getConnector().getLinuxGroupTypes().get(linuxGroupType);
    }

    public static final MethodColumn COLUMN_GROUP_NAME = getMethodColumn(LinuxGroup.class, "groupName");
    @DependencySingleton
    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+2, index=IndexType.INDEXED, description="the name of the group")
    public GroupName getGroupName() throws RemoteException {
        return getConnector().getGroupNames().get(groupName);
    }
    public GroupId getGroupId() {
        return groupName;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+3, description="the gid of the group on the machine")
    public LinuxID getGid() {
        return gid;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public LinuxGroup(AOServConnector connector, com.aoindustries.aoserv.client.dto.LinuxGroup dto) throws ValidationException {
        this(
            connector,
            dto.getPkey(),
            dto.getResourceType(),
            getAccountingCode(dto.getAccounting()),
            getTimeMillis(dto.getCreated()),
            getUserId(dto.getCreatedBy()),
            dto.getDisableLog(),
            getTimeMillis(dto.getLastEnabled()),
            dto.getAoServer(),
            dto.getBusinessServer(),
            dto.getLinuxGroupType(),
            getGroupId(dto.getGroupName()),
            getLinuxID(dto.getGid())
        );
    }

    @Override
    public com.aoindustries.aoserv.client.dto.LinuxGroup getDto() {
        return new com.aoindustries.aoserv.client.dto.LinuxGroup(
            key,
            getResourceTypeName(),
            getDto(getAccounting()),
            created,
            getDto(getCreatedByUsername()),
            disableLog,
            lastEnabled,
            aoServer,
            businessServer,
            linuxGroupType,
            getDto(groupName),
            getDto(gid)
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() throws RemoteException {
        return ApplicationResources.accessor.getMessage("LinuxGroup.toString", groupName, getAoServer().getHostname());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    @DependentObjectSet
    public IndexedSet<LinuxAccountGroup> getLinuxAccountGroups() throws RemoteException {
        return getConnector().getLinuxAccountGroups().filterIndexed(LinuxAccountGroup.COLUMN_LINUX_GROUP, this);
    }

    public IndexedSet<LinuxAccountGroup> getAlternateLinuxAccountGroups() throws RemoteException {
        return getLinuxAccountGroups().filterIndexed(LinuxAccountGroup.COLUMN_IS_PRIMARY, false);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
    /* TODO
    public int addLinuxAccount(LinuxAccount account) throws IOException, SQLException {
        return getConnector().getLinuxGroupAccounts().addLinuxGroupAccount(this, account);
    }

    public int addLinuxServerGroup(AOServer aoServer) throws IOException, SQLException {
        return getConnector().getLinuxServerGroups().addLinuxServerGroup(this, aoServer);
    }

    public LinuxServerGroup getLinuxServerGroup(AOServer aoServer) throws IOException, SQLException {
        return getConnector().getLinuxServerGroups().getLinuxServerGroup(aoServer, pkey);
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() throws IOException, SQLException {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();

        // Cannot be the primary group for any linux accounts
        for(LinuxGroupAccount lga : getConnector().getLinuxGroupAccounts().getRows()) {
            if(lga.isPrimary() && equals(lga.getLinuxGroup())) {
                reasons.add(new CannotRemoveReason<LinuxGroupAccount>("Used as primary group for Linux account "+lga.getLinuxAccount().getUsername().getUsername(), lga));
            }
        }

        // All LinuxServerGroups must be removable
        for(LinuxServerGroup lsg : getLinuxServerGroups()) reasons.addAll(lsg.getCannotRemoveReasons());

        return reasons;
    }

    public void remove() throws IOException, SQLException {
        getConnector().requestUpdateIL(
            true,
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.LINUX_GROUPS,
            pkey
        );
    }
    public List<CannotRemoveReason> getCannotRemoveReasons() throws SQLException, IOException {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();

        AOServer ao=getAOServer();

        for(CvsRepository cr : ao.getCvsRepositories()) {
            if(cr.linux_server_group==pkey) reasons.add(new CannotRemoveReason<CvsRepository>("Used by CVS repository "+cr.getPath()+" on "+cr.getLinuxServerGroup().getAOServer().getHostname(), cr));
        }

        for(EmailList el : getConnector().getEmailLists().getRows()) {
            if(el.linux_server_group==pkey) reasons.add(new CannotRemoveReason<EmailList>("Used by email list "+el.getPath()+" on "+el.getLinuxServerGroup().getAOServer().getHostname(), el));
        }

        for(HttpdServer hs : ao.getHttpdServers()) {
            if(hs.linux_server_group==pkey) reasons.add(new CannotRemoveReason<HttpdServer>("Used by Apache server #"+hs.getNumber()+" on "+hs.getAOServer().getHostname(), hs));
        }

        for(HttpdSharedTomcat hst : ao.getHttpdSharedTomcats()) {
            if(hst.linux_server_group==pkey) reasons.add(new CannotRemoveReason<HttpdSharedTomcat>("Used by Multi-Site Tomcat JVM "+hst.getInstallDirectory()+" on "+hst.getAOServer().getHostname(), hst));
        }

        // httpd_sites
        for(HttpdSite site : ao.getHttpdSites()) {
            if(site.linux_server_group==pkey) reasons.add(new CannotRemoveReason<HttpdSite>("Used by website "+site.getInstallDirectory()+" on "+site.getAOServer().getHostname(), site));
        }

        for(MajordomoServer ms : ao.getMajordomoServers()) {
            if(ms.linux_server_group==pkey) {
                EmailDomain ed=ms.getDomain();
                reasons.add(new CannotRemoveReason<MajordomoServer>("Used by Majordomo server "+ed.getDomain()+" on "+ed.getAOServer().getHostname(), ms));
            }
        }

        //for(PrivateFtpServer pfs : ao.getPrivateFtpServers()) {
        //    if(pfs.pub_linux_server_group==pkey) reasons.add(new CannotRemoveReason<PrivateFtpServer>("Used by private FTP server "+pfs.getRoot()+" on "+pfs.getLinuxServerGroup().getAOServer().getHostname(), pfs));
        //}

        return reasons;
    }

    public void remove() throws IOException, SQLException {
        getConnector().requestUpdateIL(
            true,
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.LINUX_SERVER_GROUPS,
            pkey
        );
    }

    public List<CvsRepository> getCvsRepositories() throws IOException, SQLException {
        return getConnector().getCvsRepositories().getIndexedRows(CvsRepository.COLUMN_LINUX_SERVER_GROUP, pkey);
    }

    public List<EmailList> getEmailLists() throws IOException, SQLException {
        return getConnector().getEmailLists().getIndexedRows(EmailList.COLUMN_LINUX_SERVER_GROUP, pkey);
    }

    public List<HttpdServer> getHttpdServers() throws IOException, SQLException {
        return getConnector().getHttpdServers().getIndexedRows(HttpdServer.COLUMN_LINUX_SERVER_GROUP, pkey);
    }

    public List<HttpdSite> getHttpdSites() throws IOException, SQLException {
        return getConnector().getHttpdSites().getIndexedRows(HttpdSite.COLUMN_LINUX_SERVER_GROUP, pkey);
    }

    public List<HttpdSharedTomcat> getHttpdSharedTomcats() throws IOException, SQLException {
        return getConnector().getHttpdSharedTomcats().getIndexedRows(HttpdSharedTomcat.COLUMN_LINUX_SERVER_GROUP, pkey);
    }

    public List<MajordomoServer> getMajordomoServers() throws IOException, SQLException {
        return getConnector().getMajordomoServers().getIndexedRows(MajordomoServer.COLUMN_LINUX_SERVER_GROUP, pkey);
    }
    */
    // </editor-fold>
}