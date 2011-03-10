/*
 * Copyright 2002-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.UnionSet;
import com.aoindustries.util.WrappedException;
import java.rmi.RemoteException;

/**
 * <code>BackupPartition</code> stores backup data.
 *
 * @author  AO Industries, Inc.
 */
final public class BackupPartition extends AOServObjectIntegerKey implements Comparable<BackupPartition>, DtoFactory<com.aoindustries.aoserv.client.dto.BackupPartition> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private int aoServer;
    private UnixPath path;
    final private boolean quotaEnabled;

    public BackupPartition(
        AOServConnector connector,
        int pkey,
        int aoServer,
        UnixPath path,
        boolean quotaEnabled
    ) {
        super(connector, pkey);
        this.aoServer = aoServer;
        this.path = path;
        this.quotaEnabled = quotaEnabled;
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
    public int compareTo(BackupPartition other) {
        try {
            int diff = aoServer==other.aoServer ? 0 : getAOServer().compareTo(other.getAOServer());
            if(diff!=0) return diff;
            return path.compareTo(other.path);
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="pkey", index=IndexType.PRIMARY_KEY, description="the unique category id")
    public int getPkey() {
        return key;
    }

    static final String COLUMN_AO_SERVER = "ao_server";
    @SchemaColumn(order=1, name=COLUMN_AO_SERVER, index=IndexType.INDEXED, description="the pkey of the server that stores the backup data")
    public AOServer getAOServer() throws RemoteException {
        return getConnector().getAoServers().get(aoServer);
    }

    @SchemaColumn(order=2, name="path", description="the full path to the root of the backup data")
    public UnixPath getPath() {
        return path;
    }

    /**
     * When quota is enabled, all replications/backups into the partition must have quota_gid set.
     * When quota is disabled, all replications/backups into the partition must have quota_gid not set.
     * This generally means that ao_servers, which backup full Unix permissions, will be backed-up to non-quota partitions,
     * while other backups (such as from Windows) will go to quota-enabled partitions for billing purposes.
     *
     * @return the enabled flag
     */
    @SchemaColumn(order=3, name="quota_enabled", description="When quota is enabled, all replications/backups into the partition must have quota_gid set.")
    public boolean isQuotaEnabled() {
        return quotaEnabled;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public BackupPartition(AOServConnector connector, com.aoindustries.aoserv.client.dto.BackupPartition dto) throws ValidationException {
        this(
            connector,
            dto.getPkey(),
            dto.getAoServer(),
            getUnixPath(dto.getPath()),
            dto.isQuotaEnabled()
        );
    }
    @Override
    public com.aoindustries.aoserv.client.dto.BackupPartition getDto() {
        return new com.aoindustries.aoserv.client.dto.BackupPartition(key, aoServer, getDto(path), quotaEnabled);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionSet<AOServObject<?>> addDependencies(UnionSet<AOServObject<?>> unionSet) throws RemoteException {
        unionSet = super.addDependencies(unionSet);
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getAOServer());
        return unionSet;
    }

    @Override
    protected UnionSet<AOServObject<?>> addDependentObjects(UnionSet<AOServObject<?>> unionSet) throws RemoteException {
        unionSet = super.addDependentObjects(unionSet);
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getFailoverFileReplications());
        return unionSet;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() throws RemoteException {
        return getAOServer().getHostname()+":"+path;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public IndexedSet<FailoverFileReplication> getFailoverFileReplications() throws RemoteException {
        return getConnector().getFailoverFileReplications().filterIndexed(FailoverFileReplication.COLUMN_BACKUP_PARTITION, this);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
    /* TODO
    public long getDiskTotalSize() throws IOException, SQLException {
        return getConnector().requestLongQuery(true, AOServProtocol.CommandID.GET_BACKUP_PARTITION_DISK_TOTAL_SIZE, pkey);
    }

    public long getDiskUsedSize() throws IOException, SQLException {
        return getConnector().requestLongQuery(true, AOServProtocol.CommandID.GET_BACKUP_PARTITION_DISK_USED_SIZE, pkey);
    }
     */
    // </editor-fold>
}
