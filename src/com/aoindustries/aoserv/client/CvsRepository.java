/*
 * Copyright 2002-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.UnixPath;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.UnionSet;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A <code>CvsRepository</code> represents one repository directory for the CVS pserver.
 *
 * @author  AO Industries, Inc.
 */
final public class CvsRepository extends AOServObjectIntegerKey<CvsRepository> implements DtoFactory<com.aoindustries.aoserv.client.dto.CvsRepository> /*, Removable, Disablable */ {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    /**
     * The default permissions for a CVS repository.
     */
    public static final long DEFAULT_MODE=0770;

    public static final List<Long> VALID_MODES = Collections.unmodifiableList(
        Arrays.asList(
            Long.valueOf(0700),
            Long.valueOf(0750),
            Long.valueOf(DEFAULT_MODE),
            Long.valueOf(0755),
            Long.valueOf(0775),
            Long.valueOf(02770),
            Long.valueOf(03770)
        )
    );
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private UnixPath path;
    final private int linuxAccountGroup;
    final private long mode;

    public CvsRepository(
        CvsRepositoryService<?,?> service,
        int aoServerResource,
        UnixPath path,
        int linuxAccountGroup,
        long mode
    ) {
        super(service, aoServerResource);
        this.path = path;
        this.linuxAccountGroup = linuxAccountGroup;
        this.mode = mode;
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        path = intern(path);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(CvsRepository other) throws RemoteException {
        if(key==other.key) return 0;
        AOServerResource aoResource1 = getAoServerResource();
        AOServerResource aoResource2 = other.getAoServerResource();
        int diff = aoResource1.aoServer==aoResource2.aoServer ? 0 : aoResource1.getAoServer().compareToImpl(aoResource2.getAoServer());
        if(diff!=0) return 0;
        return path.compareTo(other.path);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    static final String COLUMN_AO_SERVER_RESOURCE = "ao_server_resource";
    @SchemaColumn(order=0, name=COLUMN_AO_SERVER_RESOURCE, index=IndexType.PRIMARY_KEY, description="the unique resource id")
    public AOServerResource getAoServerResource() throws RemoteException {
        return getService().getConnector().getAoServerResources().get(key);
    }

    @SchemaColumn(order=1, name="path", description="the full path to the repository")
    public UnixPath getPath() {
        return path;
    }

    static final String COLUMN_LINUX_ACCOUNT_GROUP = "linux_account_group";
    @SchemaColumn(order=2, name=COLUMN_LINUX_ACCOUNT_GROUP, index=IndexType.INDEXED, description="the directory owner")
    public LinuxAccountGroup getLinuxAccountGroup() throws RemoteException {
        return getService().getConnector().getLinuxAccountGroups().get(linuxAccountGroup);
    }

    @SchemaColumn(order=3, name="mode", description="the directory permissions")
    public long getMode() {
        return mode;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    @Override
    public com.aoindustries.aoserv.client.dto.CvsRepository getDto() {
        return new com.aoindustries.aoserv.client.dto.CvsRepository(key, getDto(path), linuxAccountGroup, mode);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionSet<AOServObject> addDependencies(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getAoServerResource());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getLinuxAccountGroup());
        return unionSet;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() throws RemoteException {
        return getAoServerResource().getAoServer().getHostname()+":"+path.toString();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
    /* TODO
    public boolean canDisable() {
        return disable_log==-1;
    }

    public boolean canEnable() throws SQLException, IOException {
        DisableLog dl=getDisableLog();
        if(dl==null) return false;
        else return dl.canEnable() && getLinuxServerAccount().disable_log==-1;
    }

    public void disable(DisableLog dl) throws IOException, SQLException {
        getService().getConnector().requestUpdateIL(true, AOServProtocol.CommandID.DISABLE, SchemaTable.TableID.CVS_REPOSITORIES, dl.pkey, pkey);
    }
    
    public void enable() throws IOException, SQLException {
        getService().getConnector().requestUpdateIL(true, AOServProtocol.CommandID.ENABLE, SchemaTable.TableID.CVS_REPOSITORIES, pkey);
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        return Collections.emptyList();
    }

    public void remove() throws IOException, SQLException {
        getService().getConnector().requestUpdateIL(true, AOServProtocol.CommandID.REMOVE, SchemaTable.TableID.CVS_REPOSITORIES, pkey);
    }

    public void setMode(long mode) throws IOException, SQLException {
        getService().getConnector().requestUpdateIL(true, AOServProtocol.CommandID.SET_CVS_REPOSITORY_MODE, pkey, mode);
    }
     */
    // </editor-fold>
}