/*
 * Copyright 2002-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.AoCollections;
import com.aoindustries.util.WrappedException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

/**
 * A <code>CvsRepository</code> represents one repository directory for the CVS pserver.
 *
 * @author  AO Industries, Inc.
 */
final public class CvsRepository extends AOServerResource implements Comparable<CvsRepository>, DtoFactory<com.aoindustries.aoserv.client.dto.CvsRepository> /*, Removable, Disablable */ {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    /**
     * The default permissions for a CVS repository.
     */
    public static final long DEFAULT_MODE=0770;

    public static final List<Long> VALID_MODES = AoCollections.optimalUnmodifiableList(
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
        UnixPath path,
        int linuxAccountGroup,
        long mode
    ) {
        super(connector, pkey, resourceType, accounting, created, createdBy, disableLog, lastEnabled, aoServer, businessServer);
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
    public int compareTo(CvsRepository other) {
        try {
            if(key==other.key) return 0;
            int diff = aoServer==other.aoServer ? 0 : getAoServer().compareTo(other.getAoServer());
            if(diff!=0) return 0;
            return path.compareTo(other.path);
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+1, name="path", description="the full path to the repository")
    public UnixPath getPath() {
        return path;
    }

    static final String COLUMN_LINUX_ACCOUNT_GROUP = "linux_account_group";
    @DependencySingleton
    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+2, name=COLUMN_LINUX_ACCOUNT_GROUP, index=IndexType.INDEXED, description="the directory owner")
    public LinuxAccountGroup getLinuxAccountGroup() throws RemoteException {
        return getConnector().getLinuxAccountGroups().get(linuxAccountGroup);
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+3, name="mode", description="the directory permissions")
    public long getMode() {
        return mode;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public CvsRepository(AOServConnector connector, com.aoindustries.aoserv.client.dto.CvsRepository dto) throws ValidationException {
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
            getUnixPath(dto.getPath()),
            dto.getLinuxAccountGroup(),
            dto.getMode()
        );
    }

    @Override
    public com.aoindustries.aoserv.client.dto.CvsRepository getDto() {
        return new com.aoindustries.aoserv.client.dto.CvsRepository(
            key,
            getResourceTypeName(),
            getDto(getAccounting()),
            created,
            getDto(getCreatedByUsername()),
            disableLog,
            lastEnabled,
            aoServer,
            businessServer,
            getDto(path),
            linuxAccountGroup,
            mode
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() throws RemoteException {
        return getAoServer().getHostname()+":"+path.toString();
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
        getConnector().requestUpdateIL(true, AOServProtocol.CommandID.DISABLE, SchemaTable.TableID.CVS_REPOSITORIES, dl.pkey, pkey);
    }
    
    public void enable() throws IOException, SQLException {
        getConnector().requestUpdateIL(true, AOServProtocol.CommandID.ENABLE, SchemaTable.TableID.CVS_REPOSITORIES, pkey);
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        return Collections.emptyList();
    }

    public void remove() throws IOException, SQLException {
        getConnector().requestUpdateIL(true, AOServProtocol.CommandID.REMOVE, SchemaTable.TableID.CVS_REPOSITORIES, pkey);
    }

    public void setMode(long mode) throws IOException, SQLException {
        getConnector().requestUpdateIL(true, AOServProtocol.CommandID.SET_CVS_REPOSITORY_MODE, pkey, mode);
    }
     */
    // </editor-fold>
}