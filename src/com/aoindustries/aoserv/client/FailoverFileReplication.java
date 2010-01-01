package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.BitRateProvider;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.BufferManager;
import com.aoindustries.util.StringUtility;
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.Set;

/**
 * Causes a server to replicate itself to another machine on a regular basis.
 *
 * @author  AO Industries, Inc.
 */
final public class FailoverFileReplication extends AOServObjectIntegerKey<FailoverFileReplication> implements BeanFactory<com.aoindustries.aoserv.client.beans.FailoverFileReplication>, BitRateProvider {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private int server;
    final private int backupPartition;
    final private int maxBitRate;
    final private boolean useCompression;
    final private short retention;
    final private String connectAddress;
    final private String connectFrom;
    final private boolean enabled;
    final private Integer quotaGid;

    public FailoverFileReplication(
        FailoverFileReplicationService<?,?> service,
        int pkey,
        int server,
        int backupPartition,
        int maxBitRate,
        boolean useCompression,
        short retention,
        String connectAddress,
        String connectFrom,
        boolean enabled,
        Integer quotaGid
    ) {
        super(service, pkey);
        this.server = server;
        this.backupPartition = backupPartition;
        this.maxBitRate = maxBitRate;
        this.useCompression = useCompression;
        this.retention = retention;
        this.connectAddress = StringUtility.intern(connectAddress);
        this.connectFrom = StringUtility.intern(connectFrom);
        this.enabled = enabled;
        this.quotaGid = quotaGid;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(FailoverFileReplication other) throws RemoteException {
        int diff = server==other.server ? 0 : getServer().compareTo(other.getServer());
        if(diff!=0) return diff;
        return backupPartition==other.backupPartition ? 0 : getBackupPartition().compareTo(other.getBackupPartition());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="pkey", index=IndexType.PRIMARY_KEY, description="a generated, unique id")
    public int getPkey() {
        return key;
    }

    @SchemaColumn(order=1, name="server", description="the pkey of the server that the files are coming from")
    public Server getServer() throws RemoteException {
        return getService().getConnector().getServers().get(server);
    }

    /**
     * May be filtered.
     */
    @SchemaColumn(order=2, name="backup_partition", description="the pkey of the backup partition that the files are going to")
    public BackupPartition getBackupPartition() throws RemoteException {
        return getService().getConnector().getBackupPartitions().get(backupPartition);
    }

    @SchemaColumn(order=3, name="max_bit_rate", description="the maximum bit rate for files being replicated")
    public int getBitRate() {
        return maxBitRate;
    }

    @SchemaColumn(order=4, name="use_compression", description="when compression is enabled, chunk mode is used on mirroring, resulting in more CPU and disk, but less bandwidth")
    public boolean getUseCompression() {
        return useCompression;
    }

    @SchemaColumn(order=5, name="retention", description="the number of days backups will be kept")
    public BackupRetention getRetention() throws RemoteException {
        return getService().getConnector().getBackupRetentions().get(retention);
    }

    /**
     * Gets a connect address that should override the normal address resolution mechanisms.  This allows
     * a replication to be specifically sent through a gigabit connection or alternate route.
     */
    @SchemaColumn(order=6, name="connect_address", description="an address that overrides regular AOServ connections for failovers")
    public String getConnectAddress() {
        return connectAddress;
    }

    /**
     * Gets the address connections should be made from that overrides the normal address resolution mechanism.  This
     * allows a replication to be specifically sent through a gigabit connection or alternate route.
     */
    @SchemaColumn(order=7, name="connect_from", description="an address that overrides regular AOServ connection source addresses for failovers")
    public String getConnectFrom() {
        return connectFrom;
    }

    /**
     * Gets the enabled flag for this replication.
     */
    @SchemaColumn(order=8, name="enabled", description="the enabled flag for failovers")
    public boolean getEnabled() {
        return enabled;
    }

    /**
     * When set, the group ID will always be set to this value, regardless what the client sends.
     * This gid is only unique per backup_partition, thus on a single host the same gid
     * may be used for different accounts.  Also, the gid will not exist in /etc/groups and has
     * nothing to do with the shell accounts on the server.  This is to track quotas per backup
     * partition by group ID.  This may only be set (and must be set) when stored on a
     * backup_partition with quota_enabled.
     */
    /* TODO
    @SchemaColumn(order=9, name="quota_gid", description="the gid used on the backup_partition for quota reports, required if backup_partitions quotas are enabled, not allowed otherwise")
    public LinuxID getQuotaGID() throws SQLException {
        if(quotaGid==null) return null;
        return getService().getConnector().getLinuxIDs().get(quotaGid);
    }*/
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.FailoverFileReplication getBean() {
        return new com.aoindustries.aoserv.client.beans.FailoverFileReplication(key, server, backupPartition, maxBitRate, useCompression, retention, connectAddress, connectFrom, enabled, quotaGid);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependencies() throws RemoteException {
        return createDependencySet(
            getServer(),
            getBackupPartition(),
            getRetention()
        );
    }

    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return createDependencySet(
            // TODO: getFailoverFileSchedules(),
            // TODO: getFailoverMySQLReplications(),
            // TODO: getFileBackupSettings()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl(Locale userLocale) throws RemoteException {
        return getServer().toStringImpl(userLocale)+"->"+getBackupPartition().toStringImpl(userLocale);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    /* TODO
    public List<FailoverFileSchedule> getFailoverFileSchedules() throws IOException, SQLException {
        return getService().getConnector().getFailoverFileSchedules().getFailoverFileSchedules(this);
    }

    public List<FailoverMySQLReplication> getFailoverMySQLReplications() throws IOException, SQLException {
        return getService().getConnector().getFailoverMySQLReplications().getFailoverMySQLReplications(this);
    }

    public List<FileBackupSetting> getFileBackupSettings() throws IOException, SQLException {
        return getService().getConnector().getFileBackupSettings().getFileBackupSettings(this);
    }
     */
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
    /*
    public int addFailoverFileLog(long startTime, long endTime, int scanned, int updated, long bytes, boolean isSuccessful) throws IOException, SQLException {
    	return getService().getConnector().getFailoverFileLogs().addFailoverFileLog(this, startTime, endTime, scanned, updated, bytes, isSuccessful);
    }

    public void setBitRate(int bitRate) throws IOException, SQLException {
        getService().getConnector().requestUpdateIL(true, AOServProtocol.CommandID.SET_FAILOVER_FILE_REPLICATION_BIT_RATE, pkey, bitRate);
    }
    */
    public int getBlockSize() {
        return BufferManager.BUFFER_SIZE;
    }

    /**
     * Gets the most recent (by start time) log entries for failover file replications, up to the
     * maximum number of rows.  May return less than this number of rows.  The results
     * are sorted by start_time descending (most recent at index zero).
     */
    /*
    public List<FailoverFileLog> getFailoverFileLogs(int maxRows) throws IOException, SQLException {
        return getService().getConnector().getFailoverFileLogs().getFailoverFileLogs(this, maxRows);
    }

    public int addFileBackupSetting(String path, boolean backupEnabled) throws IOException, SQLException {
        return getService().getConnector().getFileBackupSettings().addFileBackupSetting(this, path, backupEnabled);
    }

    public FileBackupSetting getFileBackupSetting(String path) throws IOException, SQLException {
        return getService().getConnector().getFileBackupSettings().getFileBackupSetting(this, path);
    }

    public void setFailoverFileSchedules(List<Short> hours, List<Short> minutes) throws IOException, SQLException {
        getService().getConnector().getFailoverFileSchedules().setFailoverFileSchedules(this, hours, minutes);
    }

    public void setFileBackupSettings(List<String> paths, List<Boolean> backupEnableds) throws IOException, SQLException {
        getService().getConnector().getFileBackupSettings().setFileBackupSettings(this, paths, backupEnableds);
    }

    public AOServer.DaemonAccess requestReplicationDaemonAccess() throws IOException, SQLException {
        return getService().getConnector().requestResult(
            true,
            new AOServConnector.ResultRequest<AOServer.DaemonAccess>() {
                private AOServer.DaemonAccess daemonAccess;
                public void writeRequest(CompressedDataOutputStream out) throws IOException {
                    out.writeCompressedInt(AOServProtocol.CommandID.REQUEST_REPLICATION_DAEMON_ACCESS.ordinal());
                    out.writeCompressedInt(pkey);
                }
                public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
                    int code=in.readByte();
                    if(code==AOServProtocol.DONE) {
                        daemonAccess = new AOServer.DaemonAccess(
                            in.readUTF(),
                            in.readUTF(),
                            in.readCompressedInt(),
                            in.readLong()
                        );
                    } else {
                        AOServProtocol.checkResult(code, in);
                        throw new IOException("Unexpected response code: "+code);
                    }
                }
                public AOServer.DaemonAccess afterRelease() {
                    return daemonAccess;
                }
            }
        );
    }
     */
    // </editor-fold>
}