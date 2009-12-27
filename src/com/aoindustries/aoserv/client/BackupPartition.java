package com.aoindustries.aoserv.client;

/*
 * Copyright 2002-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.Set;

/**
 * <code>BackupPartition</code> stores backup data.
 *
 * @author  AO Industries, Inc.
 */
final public class BackupPartition extends AOServObjectIntegerKey<BackupPartition> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private int ao_server;
    final private String path;
    final private boolean enabled;
    final private boolean quota_enabled;

    public BackupPartition(
        BackupPartitionService<?,?> service,
        int pkey,
        int ao_server,
        String path,
        boolean enabled,
        boolean quota_enabled
    ) {
        super(service, pkey);
        this.ao_server = ao_server;
        this.path = path.intern();
        this.enabled = enabled;
        this.quota_enabled = quota_enabled;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(BackupPartition other) throws RemoteException {
        int diff = getAOServer().compareTo(other.getAOServer());
        if(diff!=0) return diff;
        return compareIgnoreCaseConsistentWithEquals(path, other.path);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="pkey", unique=true, description="the unique category id")
    public int getPkey() {
        return key;
    }

    @SchemaColumn(order=1, name="ao_server", description="the pkey of the server that stores the backup data")
    public AOServer getAOServer() throws RemoteException {
        return getService().getConnector().getAoServers().get(ao_server);
    }

    @SchemaColumn(order=2, name="path", description="the full path to the root of the backup data")
    public String getPath() {
        return path;
    }

    @SchemaColumn(order=3, name="enabled", description="flags the partition as currently accepting new data")
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * When quota is enabled, all replications/backups into the partition must have quota_gid set.
     * When quota is disabled, all replications/backups into the partition must have quota_gid not set.
     * This generally means that ao_servers, which backup full Unix permissions, will be backed-up to non-quota partitions,
     * while other backups (such as from Windows) will go to quota-enabled partitions for billing purposes.
     *
     * @return the enabled flag
     */
    @SchemaColumn(order=4, name="quota_enabled", description="When quota is enabled, all replications/backups into the partition must have quota_gid set.")
    public boolean isQuotaEnabled() {
        return quota_enabled;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependencies() throws RemoteException {
        return createDependencySet(
            getAOServer()
        );
    }

    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return createDependencySet(
            // TODO: getFailoverFileReplications()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl(Locale userLocale) throws RemoteException {
        return getAOServer().getHostname()+":"+path;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    /* TODO
    public List<FailoverFileReplication> getFailoverFileReplications() throws IOException, SQLException {
        return getService().getConnector().getFailoverFileReplications().getIndexedRows(FailoverFileReplication.COLUMN_BACKUP_PARTITION, pkey);
    }
     */
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
    /* TODO
    public long getDiskTotalSize() throws IOException, SQLException {
        return getService().getConnector().requestLongQuery(true, AOServProtocol.CommandID.GET_BACKUP_PARTITION_DISK_TOTAL_SIZE, pkey);
    }

    public long getDiskUsedSize() throws IOException, SQLException {
        return getService().getConnector().requestLongQuery(true, AOServProtocol.CommandID.GET_BACKUP_PARTITION_DISK_USED_SIZE, pkey);
    }
     */
    // </editor-fold>
}
