package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.sql.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.List;

/**
 * A <code>Server</code> stores the details about a single, physical server.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class Server extends CachedObjectIntegerKey<Server> {

    static final int
        COLUMN_PKEY=0,
        COLUMN_HOSTNAME=1
    ;

    /**
     * The daemon key is only available to <code>MasterUser</code>s.  This value is used
     * in place of the key when not accessible.
     */
    public static final String HIDDEN_PASSWORD="*";

    String
        hostname,
        farm,
        owner,
        administrator,
        description
    ;

    private int backup_hour;
    private long last_backup_time;
    private int operating_system_version;
    private String asset_label;

    public void addBusiness(
        String accounting,
        String contractVersion,
        Business parent,
        boolean can_add_backup_servers,
        boolean can_add_businesses,
        boolean can_see_prices,
        boolean billParent
    ) {
	table.connector.businesses.addBusiness(
            accounting,
            contractVersion,
            this,
            parent.pkey,
            can_add_backup_servers,
            can_add_businesses,
            can_see_prices,
            billParent
        );
    }

    public int addFileBackup(
        String path,
        FileBackupDevice device,
        long inode,
        int packageNum,
        long mode,
        int uid,
        int gid,
        int backupData,
        long md5_hi,
        long md5_lo,
        long modifyTime,
        short backupLevel,
        short backupRetention,
        String symlinkTarget,
        long deviceID
    ) {
        return table.connector.fileBackups.addFileBackup(
            this,
            path,
            device,
            inode,
            packageNum,
            mode,
            uid,
            gid,
            backupData,
            md5_hi,
            md5_lo,
            modifyTime,
            backupLevel,
            backupRetention,
            symlinkTarget,
            deviceID
        );
    }

    public int[] addFileBackups(
        int count,
        String[] paths,
        FileBackupDevice[] devices,
        long[] inodes,
        int[] packageNums,
        long[] modes,
        int[] uids,
        int[] gids,
        int[] backupDatas,
        long[] md5_his,
        long[] md5_los,
        long[] modifyTimes,
        short[] backupLevels,
        short[] backupRetentions,
        String[] symlinkTargets,
        long[] deviceIDs
    ) {
        return table.connector.fileBackups.addFileBackups(
            this,
            count,
            paths,
            devices,
            inodes,
            packageNums,
            modes,
            uids,
            gids,
            backupDatas,
            md5_his,
            md5_los,
            modifyTimes,
            backupLevels,
            backupRetentions,
            symlinkTargets,
            deviceIDs
        );
    }

    public int addFileBackupSetting(String path, Package packageObj, BackupLevel backupLevel, BackupRetention backupRetention, boolean recurse) {
        return table.connector.fileBackupSettings.addFileBackupSetting(this, path, packageObj, backupLevel, backupRetention, recurse);
    }

    public int addFileBackupStat(
        long startTime,
        long endTime,
        int scanned,
        int file_backup_attribute_matches,
        int not_matched_md5_files,
        int not_matched_md5_failures,
        int send_missing_backup_data_files,
        int send_missing_backup_data_failures,
        int temp_files,
        int temp_send_backup_data_files,
        int temp_failures,
        int added,
        int deleted,
        boolean is_successful
    ) {
        return table.connector.fileBackupStats.addFileBackupStat(
            this,
            startTime,
            endTime,
            scanned,
            file_backup_attribute_matches,
            not_matched_md5_files,
            not_matched_md5_failures,
            send_missing_backup_data_files,
            send_missing_backup_data_failures,
            temp_files,
            temp_send_backup_data_files,
            temp_failures,
            added,
            deleted,
            is_successful
        );
    }

    public Object[] findOrAddBackupDatas(
        int batchSize,
        long[] lengths,
        long[] md5_his,
        long[] md5_los
    ) {
        return table.connector.backupDatas.findOrAddBackupDatas(
            this,
            batchSize,
            lengths,
            md5_his,
            md5_los
        );
    }

    public Object[] findOrAddBackupData(
        long length,
        long md5_hi,
        long md5_lo
    ) {
        return table.connector.backupDatas.findOrAddBackupData(
            this,
            length,
            md5_hi,
            md5_lo
        );
    }

    public Object[] findLatestFileBackupSetAttributeMatches(
        int batchSize,
        String[] paths,
        FileBackupDevice[] devices,
        long[] inodes,
        int[] packageNums,
        long[] modes,
        int[] uids,
        int[] gids,
        long[] modify_times,
        short[] backup_levels,
        short[] backup_retentions,
        long[] lengths,
        String[] symlink_targets,
        long[] device_ids
    ) {
        return table.connector.fileBackups.findLatestFileBackupSetAttributeMatches(
            this,
            batchSize,
            paths,
            devices,
            inodes,
            packageNums,
            modes,
            uids,
            gids,
            modify_times,
            backup_levels,
            backup_retentions,
            lengths,
            symlink_targets,
            device_ids
        );
    }

    public Business getOwner() {
        Business bu=table.connector.businesses.get(owner);
        if(bu==null) throw new WrappedException(new SQLException("Unable to find Business: "+owner));
        return bu;
    }

    public BusinessAdministrator getAdministrator() {
	BusinessAdministrator obj=table.connector.businessAdministrators.get(administrator);
	if(obj==null) throw new WrappedException(new SQLException("Unable to find BusinessAdministrator: "+administrator));
	return obj;
    }

    public AOServer getAOServer() {
        return table.connector.aoServers.get(pkey);
    }

    public List<BackupReport> getBackupReports() {
	return table.connector.backupReports.getBackupReports(this);
    }

    public List<Business> getBusinesses() {
	return table.connector.businessServers.getBusinesses(this);
    }

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_HOSTNAME: return hostname;
            case 2: return farm;
            case 3: return owner;
            case 4: return administrator;
            case 5: return description;
            case 6: return Integer.valueOf(backup_hour);
            case 7: return last_backup_time==-1?null:new java.sql.Date(last_backup_time);
            case 8: return Integer.valueOf(operating_system_version);
            case 9: return asset_label;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public String getDescription() {
	return description;
    }

    public List<FileBackup> getFileBackups() {
	return table.connector.fileBackups.getFileBackups(this);
    }

    public List<FileBackup> getFileBackupChildren(String path) {
        return table.connector.fileBackups.getFileBackupChildren(this, path);
    }

    public List<FileBackupRoot> getFileBackupRoots() {
        return table.connector.fileBackupRoots.getFileBackupRoots(this);
    }

    public List<FileBackupStat> getFileBackupStats() {
        return table.connector.fileBackupStats.getFileBackupStats(this);
    }

    public List<FileBackup> getFileBackupVersions(String path) {
        return table.connector.fileBackups.getFileBackupVersions(this, path);
    }

    public List<FileBackup> findHardLinks(FileBackupDevice device, long inode) {
        return table.connector.fileBackups.findHardLinks(this, device, inode);
    }

    /**
     * Gets a set of backup files for a server.
     *
     * @return  <code>IntsAndBooleans</code> wrapping <code>IntVector</code> of pkeys along with <code>ArrayList</code>
     *          of <code>Boolean</code> indicating if each item is a directory.
     */
    public IntsAndBooleans getFileBackupSet(String path, long time) {
        return table.connector.fileBackups.getFileBackupSet(this, path, time);
    }

    public FileBackupSetting getFileBackupSetting(String path) {
        return table.connector.fileBackupSettings.getFileBackupSetting(this, path);
    }

    public List<FileBackupSetting> getFileBackupSettings() {
        return table.connector.fileBackupSettings.getFileBackupSettings(this);
    }

    public List<String> getFileBackupSettingWarnings() {
        return table.connector.fileBackupSettings.getFileBackupSettingWarnings(this);
    }

    public String getHostname() {
	return hostname;
    }
    
    public int getBackupHour() {
        return backup_hour;
    }

    public long getLastBackupTime() {
        return last_backup_time;
    }
    
    /**
     * Gets the most recent successful file backup stats or <code>null</code> if not successfully backed-up.
     */
    public FileBackupStat getLastSuccessfulFileBackupStat() {
        for(FileBackupStat fbs : getFileBackupStats()) if(fbs.isSuccessful()) return fbs;
        return null;
    }

    public OperatingSystemVersion getOperatingSystemVersion() {
        OperatingSystemVersion osv=table.connector.operatingSystemVersions.get(operating_system_version);
        if(osv==null) throw new WrappedException(new SQLException("Unable to find OperatingSystemVersion: "+operating_system_version));
        return osv;
    }
    
    public String getAssetLabel() {
        return asset_label;
    }

    public SortedIntArrayList getLatestFileBackupSet() {
        return table.connector.fileBackups.getLatestFileBackupSet(this);
    }

    public ServerFarm getServerFarm() {
	ServerFarm sf=table.connector.serverFarms.get(farm);
	if(sf==null) throw new WrappedException(new SQLException("Unable to find ServerFarm: "+farm));
	return sf;
    }

    protected int getTableIDImpl() {
	return SchemaTable.SERVERS;
    }

    void initImpl(ResultSet result) throws SQLException {
        pkey = result.getInt(1);
	hostname = result.getString(2);
	farm = result.getString(3);
        owner = result.getString(4);
	administrator = result.getString(5);
	description = result.getString(6);
        backup_hour=result.getInt(7);
        Timestamp T=result.getTimestamp(8);
        last_backup_time=T==null?-1:T.getTime();
        operating_system_version=result.getInt(9);
        asset_label=result.getString(10);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
	hostname=in.readUTF();
	farm=in.readUTF();
        owner=in.readUTF();
	administrator=in.readUTF();
	description=in.readUTF();
        backup_hour=in.readCompressedInt();
        last_backup_time=in.readLong();
        operating_system_version=in.readCompressedInt();
        asset_label=readNullUTF(in);
    }

    public void removeExpiredFileBackups() {
        table.connector.fileBackups.removeExpiredFileBackups(this);
    }

    public void setLastBackupTime(long backupTime) {
        table.connector.requestUpdateIL(AOServProtocol.SET_LAST_BACKUP_TIME, pkey, backupTime);
    }

    protected String toStringImpl() {
        return hostname;
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        out.writeCompressedInt(pkey);
	out.writeUTF(hostname);
	out.writeUTF(farm);
        out.writeUTF(owner);
	out.writeUTF(administrator);
	out.writeUTF(description);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_0_A_107)<=0) out.writeUTF(Architecture.I686);
        out.writeCompressedInt(backup_hour);
        out.writeLong(last_backup_time);
        out.writeCompressedInt(operating_system_version);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_0_A_108)>=0) writeNullUTF(out, asset_label);
    }
}