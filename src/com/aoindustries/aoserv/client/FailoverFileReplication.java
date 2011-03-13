/*
 * Copyright 2003-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.io.BitRateProvider;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.BufferManager;
import com.aoindustries.util.WrappedException;
import java.rmi.RemoteException;
import java.util.NoSuchElementException;

/**
 * Causes a server to replicate itself to another machine on a regular basis.
 *
 * @author  AO Industries, Inc.
 */
final public class FailoverFileReplication
extends AOServObjectIntegerKey
implements
    Comparable<FailoverFileReplication>,
    DtoFactory<com.aoindustries.aoserv.client.dto.FailoverFileReplication>,
    BitRateProvider {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private int server;
    final private int backupPartition;
    final private Long maxBitRate;
    final private boolean useCompression;
    final private short retention;
    private InetAddress connectAddress;
    private InetAddress connectFrom;
    final private boolean enabled;
    final private LinuxID quotaGid;

    public FailoverFileReplication(
        AOServConnector connector,
        int pkey,
        int server,
        int backupPartition,
        Long maxBitRate,
        boolean useCompression,
        short retention,
        InetAddress connectAddress,
        InetAddress connectFrom,
        boolean enabled,
        LinuxID quotaGid
    ) {
        super(connector, pkey);
        this.server = server;
        this.backupPartition = backupPartition;
        this.maxBitRate = maxBitRate;
        this.useCompression = useCompression;
        this.retention = retention;
        this.connectAddress = connectAddress;
        this.connectFrom = connectFrom;
        this.enabled = enabled;
        this.quotaGid = quotaGid;
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        connectAddress = intern(connectAddress);
        connectFrom = intern(connectFrom);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(FailoverFileReplication other) {
        try {
            int diff = server==other.server ? 0 : getServer().compareTo(other.getServer());
            if(diff!=0) return diff;
            return AOServObjectUtils.compare(backupPartition, other.backupPartition); // Sorting by pkey only because BackupPartition objects may be filtered
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="pkey", index=IndexType.PRIMARY_KEY, description="a generated, unique id")
    public int getPkey() {
        return key;
    }

    static final String COLUMN_SERVER = "server";
    @DependencySingleton
    @SchemaColumn(order=1, name=COLUMN_SERVER, index=IndexType.INDEXED, description="the pkey of the server that the files are coming from")
    public Server getServer() throws RemoteException {
        return getConnector().getServers().get(server);
    }

    static final String COLUMN_BACKUP_PARTITION = "backup_partition";
    /**
     * May be filtered.
     */
    @DependencySingleton
    @SchemaColumn(order=2, name=COLUMN_BACKUP_PARTITION, index=IndexType.INDEXED, description="the pkey of the backup partition that the files are going to")
    public BackupPartition getBackupPartition() throws RemoteException {
        try {
            return getConnector().getBackupPartitions().get(backupPartition);
        } catch(NoSuchElementException err) {
            return null;
        }
    }

    @SchemaColumn(order=3, name="max_bit_rate", description="the maximum bit rate for files being replicated")
    @Override
    public Long getBitRate() {
        return maxBitRate;
    }

    @SchemaColumn(order=4, name="use_compression", description="when compression is enabled, chunk mode is used on mirroring, resulting in more CPU and disk, but less bandwidth")
    public boolean getUseCompression() {
        return useCompression;
    }

    static final String COLUMN_RETENTION = "retention";
    @DependencySingleton
    @SchemaColumn(order=5, name=COLUMN_RETENTION, index=IndexType.INDEXED, description="the number of days backups will be kept")
    public BackupRetention getRetention() throws RemoteException {
        return getConnector().getBackupRetentions().get(retention);
    }

    /**
     * Gets a connect address that should override the normal address resolution mechanisms.  This allows
     * a replication to be specifically sent through a gigabit connection or alternate route.
     */
    @SchemaColumn(order=6, name="connect_address", description="an address that overrides regular AOServ connections for failovers")
    public InetAddress getConnectAddress() {
        return connectAddress;
    }

    /**
     * Gets the address connections should be made from that overrides the normal address resolution mechanism.  This
     * allows a replication to be specifically sent through a gigabit connection or alternate route.
     */
    @SchemaColumn(order=7, name="connect_from", description="an address that overrides regular AOServ connection source addresses for failovers")
    public InetAddress getConnectFrom() {
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
    @SchemaColumn(order=9, name="quota_gid", description="the gid used on the backup_partition for quota reports, required if backup_partitions quotas are enabled, not allowed otherwise")
    public LinuxID getQuotaGID() {
        return quotaGid;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public FailoverFileReplication(AOServConnector connector, com.aoindustries.aoserv.client.dto.FailoverFileReplication dto) throws ValidationException {
        this(
            connector,
            dto.getPkey(),
            dto.getServer(),
            dto.getBackupPartition(),
            dto.getMaxBitRate(),
            dto.isUseCompression(),
            dto.getRetention(),
            getInetAddress(dto.getConnectAddress()),
            getInetAddress(dto.getConnectFrom()),
            dto.isEnabled(),
            getLinuxID(dto.getQuotaGid())
        );
    }

    @Override
    public com.aoindustries.aoserv.client.dto.FailoverFileReplication getDto() {
        return new com.aoindustries.aoserv.client.dto.FailoverFileReplication(key, server, backupPartition, maxBitRate, useCompression, retention, getDto(connectAddress), getDto(connectFrom), enabled, getDto(quotaGid));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() throws RemoteException {
        BackupPartition bp = getBackupPartition();
        return getServer().toStringImpl()+"->"+(bp==null ? Integer.toString(backupPartition) : bp.toStringImpl());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    @DependentObjectSet
    public IndexedSet<FailoverMySQLReplication> getFailoverMySQLReplications() throws RemoteException {
        return getConnector().getFailoverMySQLReplications().filterIndexed(FailoverMySQLReplication.COLUMN_REPLICATION, this);
    }

    @DependentObjectSet
    public IndexedSet<FailoverFileSchedule> getFailoverFileSchedules() throws RemoteException {
        return getConnector().getFailoverFileSchedules().filterIndexed(FailoverFileSchedule.COLUMN_REPLICATION, this);
    }

    @DependentObjectSet
    public IndexedSet<FileBackupSetting> getFileBackupSettings() throws RemoteException {
        return getConnector().getFileBackupSettings().filterIndexed(FileBackupSetting.COLUMN_REPLICATION, this);
    }

    @DependentObjectSet
    public IndexedSet<FailoverFileLog> getFailoverFileLogs() throws RemoteException {
        return getConnector().getFailoverFileLogs().filterIndexed(FailoverFileLog.COLUMN_REPLICATION, this);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="BitRateProvider">
    @Override
    public int getBlockSize() {
        return BufferManager.BUFFER_SIZE;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
    /*
    public void setBitRate(int bitRate) throws IOException, SQLException {
        getConnector().requestUpdateIL(true, AOServProtocol.CommandID.SET_FAILOVER_FILE_REPLICATION_BIT_RATE, pkey, bitRate);
    }

    public int addFileBackupSetting(String path, boolean backupEnabled) throws IOException, SQLException {
        return getConnector().getFileBackupSettings().addFileBackupSetting(this, path, backupEnabled);
    }

    public FileBackupSetting getFileBackupSetting(String path) throws IOException, SQLException {
        return getConnector().getFileBackupSettings().getFileBackupSetting(this, path);
    }

    public void setFailoverFileSchedules(List<Short> hours, List<Short> minutes) throws IOException, SQLException {
        getConnector().getFailoverFileSchedules().setFailoverFileSchedules(this, hours, minutes);
    }

    public void setFileBackupSettings(List<String> paths, List<Boolean> backupEnableds) throws IOException, SQLException {
        getConnector().getFileBackupSettings().setFileBackupSettings(this, paths, backupEnableds);
    }
     */
    // </editor-fold>
}