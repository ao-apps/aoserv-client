/*
 * Copyright 2003-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;

/**
 * A <code>FileBackupSetting</code> overrides the default backup behavior.
 *
 * @author  AO Industries, Inc.
 */
final public class FileBackupSetting extends AOServObjectIntegerKey<FileBackupSetting> implements BeanFactory<com.aoindustries.aoserv.client.beans.FileBackupSetting> /*, Removable */ {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private int replication;
    private String path;
    final private boolean backupEnabled;

    public FileBackupSetting(
        FileBackupSettingService<?,?> service,
        int pkey,
        int replication,
        String path,
        boolean backupEnabled
    ) {
        super(service, pkey);
        this.replication = replication;
        this.path = path;
        this.backupEnabled = backupEnabled;
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
    protected int compareToImpl(FileBackupSetting other) throws RemoteException {
        int diff = replication==other.replication ? 0 : getReplication().compareTo(other.getReplication());
        if(diff!=0) return diff;
        return AOServObjectUtils.compareIgnoreCaseConsistentWithEquals(path, other.path);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="pkey", index=IndexType.PRIMARY_KEY, description="a generated primary key")
    public int getPkey() {
        return key;
    }

    static final String COLUMN_REPLICATION = "replication";
    @SchemaColumn(order=1, name=COLUMN_REPLICATION, index=IndexType.INDEXED, description="the pkey of the failover_file_replication configured")
    public FailoverFileReplication getReplication() throws RemoteException {
        return getService().getConnector().getFailoverFileReplications().get(replication);
    }

    @SchemaColumn(order=2, name="path", description="the path to control")
    public String getPath() {
        return path;
    }

    @SchemaColumn(order=3, name="backup_enabled", description="the enabled flag for this prefix")
    public boolean getBackupEnabled() {
        return backupEnabled;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.FileBackupSetting getBean() {
        return new com.aoindustries.aoserv.client.beans.FileBackupSetting(key, replication, path, backupEnabled);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
    /* TODO
    public List<CannotRemoveReason> getCannotRemoveReasons() {
        return Collections.emptyList();
    }

    public void remove() throws IOException, SQLException {
        getService().getConnector().requestUpdateIL(
            true,
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.FILE_BACKUP_SETTINGS,
            pkey
    	);
    }

    public void setSettings(
        String path,
        boolean backupEnabled
    ) throws IOException, SQLException {
        getService().getConnector().requestUpdateIL(
            true,
            AOServProtocol.CommandID.SET_FILE_BACKUP_SETTINGS,
            pkey,
            path,
            backupEnabled
        );
    }
     */
    // </editor-fold>
}
