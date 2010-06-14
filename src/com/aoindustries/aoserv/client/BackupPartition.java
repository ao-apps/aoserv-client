package com.aoindustries.aoserv.client;

/*
 * Copyright 2002-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.validator.UnixPath;
import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.Set;

/**
 * <code>BackupPartition</code> stores backup data.
 *
 * @author  AO Industries, Inc.
 */
final public class BackupPartition extends AOServObjectIntegerKey<BackupPartition> implements BeanFactory<com.aoindustries.aoserv.client.beans.BackupPartition> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private int aoServer;
    private UnixPath path;
    final private boolean enabled;
    final private boolean quotaEnabled;

    public BackupPartition(
        BackupPartitionService<?,?> service,
        int pkey,
        int aoServer,
        UnixPath path,
        boolean enabled,
        boolean quotaEnabled
    ) {
        super(service, pkey);
        this.aoServer = aoServer;
        this.path = path;
        this.enabled = enabled;
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
    protected int compareToImpl(BackupPartition other) throws RemoteException {
        int diff = aoServer==other.aoServer ? 0 : getAOServer().compareTo(other.getAOServer());
        if(diff!=0) return diff;
        return path.compareTo(other.path);
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
        return getService().getConnector().getAoServers().get(aoServer);
    }

    @SchemaColumn(order=2, name="path", description="the full path to the root of the backup data")
    public UnixPath getPath() {
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
        return quotaEnabled;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.BackupPartition getBean() {
        return new com.aoindustries.aoserv.client.beans.BackupPartition(key, aoServer, getBean(path), enabled, quotaEnabled);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependencies() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getAOServer()
        );
    }

    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
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
