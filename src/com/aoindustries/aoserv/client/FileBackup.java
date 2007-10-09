package com.aoindustries.aoserv.client;

/*
 * Copyright 2002-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.io.unix.*;
import com.aoindustries.sql.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.Collections;
import java.util.List;

/**
 * <code>FileBackup</code> stores the owner, permissions, and time range for a file that
 * has been backed-up from the filesystem.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
public final class FileBackup extends AOServObject<Integer,FileBackup> implements Dumpable, Removable, SingleTableObject<Integer,FileBackup> {

    static final int COLUMN_PKEY=0;

    /**
     * The list of extensions that that should not be compressed during the backup.
     */
    private static final String[] noCompressExtensions={
        "ace",
        "aiff",
        "arc",
        "arj",
        "avi",
        "bin",
        "bz",
        "bz2",
        "bzip",
        "bzip2",
        "cab",
        "ear",
        "gif",
        "gz",
        "gzip",
        "jfif",
        "jpeg",
        "jpg",
        "lzh",
        "mpeg",
        "mpg",
        "mp3",
        "moov",
        "mov",
        "movie",
        "png",
        "qt",
        "ram",
        "rar",
        "rm",
        "rpm",
        "sea",
        "sit",
        "taz",
        "tgz",
        "tif",
        "tiff",
        "war",
        "xxe",
        "z",
        "zip"
    };
    public static boolean isCompressedExtension(String extension) {
        if(extension.length()>0 && extension.charAt(0)=='.') extension=extension.substring(1);
        for(int c=0;c<noCompressExtensions.length;c++) {
            if(extension.equalsIgnoreCase(noCompressExtensions[c])) return true;
        }
        return false;
    }

    protected AOServTable<Integer,FileBackup> table;

    private int pkey;
    private int server;
    private String path;
    private short device;
    private long inode;
    private int package_num;
    private long mode;
    private int uid;
    private int gid;
    int backup_data;
    private long create_time;
    private long modify_time;
    private long remove_time;
    private short backup_level;
    private short backup_retention;
    private String symlink_target;
    private long device_id;

    public void dump(PrintWriter out) {
        BackupData data=getBackupData();
        if(data==null) {
            out.write(symlink_target);
            out.write('\n');
        } else data.getData(out, true, 0, null);
    }

    boolean equalsImpl(Object O) {
	return
            O instanceof FileBackup
            && ((FileBackup)O).pkey==pkey
	;
    }

    /**
     * Gets the device this file is stored on.
     */
    public FileBackupDevice getDevice() {
        if(device==-1) return null;
        FileBackupDevice fbd=table.connector.fileBackupDevices.get(device);
        if(fbd==null) throw new WrappedException(new SQLException("Unable to find FileBackupDevice: "+device));
        return fbd;
    }

    /**
     * Gets the device identifier for device files, such as those in <code>/dev/</code>.
     */
    public long getDeviceID() {
        return device_id;
    }

    /**
     * The inode number, -1 indicates not present.
     */
    public long getInode() {
        return inode;
    }

    public long getModifyTime() {
        return modify_time;
    }

    public int getPkey() {
        return pkey;
    }

    public Server getServer() {
        Server se=table.connector.servers.get(server);
        if(se==null) throw new WrappedException(new SQLException("Unable to find Server: "+server));
        return se;
    }

    public String getPath() {
        return path;
    }

    public Package getPackage() {
        Package obj = table.connector.packages.get(package_num);
        if (obj == null) throw new WrappedException(new SQLException("Unable to find Package: "+package_num));
        return obj;
    }

    public long getMode() {
        return mode;
    }

    public LinuxID getUID() {
        if(uid==-1) return null;
        LinuxID lid=table.connector.linuxIDs.get(uid);
        if(lid==null) throw new WrappedException(new SQLException("Unable to find LinuxID: "+uid));
        return lid;
    }

    public LinuxID getGID() {
        if(gid==-1) return null;
        LinuxID lid=table.connector.linuxIDs.get(gid);
        if(lid==null) throw new WrappedException(new SQLException("Unable to find LinuxID: "+gid));
        return lid;
    }

    public BackupData getBackupData() {
        if(backup_data==-1) return null;
        BackupData obj=table.connector.backupDatas.get(backup_data);
        if(obj==null) throw new WrappedException(new SQLException("Unable to find BackupData: "+backup_data));
        return obj;
    }

    public String getSymLinkTarget() {
        return symlink_target;
    }

    public BackupLevel getBackupLevel() {
        BackupLevel bl=table.connector.backupLevels.get(backup_level);
        if(bl==null) throw new WrappedException(new SQLException("Unable to find BackupLevel: "+backup_level));
        return bl;
    }

    public BackupRetention getBackupRetention() {
        BackupRetention br=table.connector.backupRetentions.get(backup_retention);
        if(br==null) throw new WrappedException(new SQLException("Unable to find BackupRetention: "+backup_retention));
        return br;
    }

    public long getCreateTime() {
        return create_time;
    }
    
    public long getRemoveTime() {
        return remove_time;
    }
    
    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case 1: return Integer.valueOf(server);
            case 2: return path;
            case 3: return device==-1?null:Short.valueOf(device);
            case 4: return inode==-1?null:Long.valueOf(inode);
            case 5: return Integer.valueOf(package_num);
            case 6: return Long.valueOf(mode);
            case 7: return uid==-1?null:Integer.valueOf(uid);
            case 8: return gid==-1?null:Integer.valueOf(gid);
            case 9: return backup_data==-1?null:Integer.valueOf(backup_data);
            case 10: return new java.sql.Date(create_time);
            case 11: return modify_time==-1?null:new java.sql.Date(modify_time);
            case 12: return remove_time==-1?null:new java.sql.Date(remove_time);
            case 13: return Short.valueOf(backup_level);
            case 14: return Short.valueOf(backup_retention);
            case 15: return symlink_target;
            case 16: return device_id==-1?null:Long.valueOf(device_id);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public Integer getKey() {
	return pkey;
    }

    final public AOServTable<Integer,FileBackup> getTable() {
        return table;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.FILE_BACKUPS;
    }

    int hashCodeImpl() {
	return pkey;
    }

    void initImpl(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
        server=result.getInt(2);
        path=SQLUtility.decodeString(result.getString(3));
        device=result.getShort(4);
        if(result.wasNull()) device=-1;
        inode=result.getLong(5);
        if(result.wasNull()) inode=-1;
        else if(inode==-1) throw new SQLException("inode of -1 conflicts with internal use of -1 as null");
        package_num=result.getInt(6);
        mode=result.getLong(7);
        uid=result.getInt(8);
        if(result.wasNull()) uid=-1;
        gid=result.getInt(9);
        if(result.wasNull()) gid=-1;
        backup_data=result.getInt(10);
        if(result.wasNull()) backup_data=-1;
        create_time=result.getTimestamp(11).getTime();
        Timestamp T=result.getTimestamp(12);
        modify_time=T==null?-1:T.getTime();
        T=result.getTimestamp(13);
        remove_time=T==null?-1:T.getTime();
        backup_level=result.getShort(14);
        backup_retention=result.getShort(15);
        symlink_target=SQLUtility.decodeString(result.getString(16));
        device_id=result.getLong(17);
        if(result.wasNull()) device_id=-1;
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readInt();
        server=in.readCompressedInt();
        path=in.readCompressedUTF();
        device=in.readShort();
        inode=in.readLong();
        package_num=in.readCompressedInt();
        mode=in.readLong();
        uid=in.readCompressedInt();
        gid=in.readCompressedInt();
        backup_data=in.readInt();
        create_time=in.readLong();
        modify_time=UnixFile.isRegularFile(mode)?in.readLong():-1;
        remove_time=in.readLong();
        backup_level=in.readShort();
        backup_retention=in.readShort();
        if(UnixFile.isSymLink(mode)) {
            symlink_target=in.readCompressedUTF();
            device_id=-1;
        } else {
            symlink_target=null;
            device_id=UnixFile.isBlockDevice(mode) || UnixFile.isCharacterDevice(mode) ? in.readLong() : -1;
        }
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        return Collections.emptyList();
    }

    public void remove() {
        ((FileBackupTable)table).removeFileBackup(pkey);
    }

    public void setTable(AOServTable<Integer,FileBackup> table) {
	if(this.table!=null) throw new IllegalStateException("table already set");
	this.table=table;
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        out.writeInt(pkey);
        out.writeCompressedInt(server);
        out.writeCompressedUTF(path, 0);
        out.writeShort(device);
        out.writeLong(inode);
        out.writeCompressedInt(package_num);
        out.writeLong(mode);
        out.writeCompressedInt(uid);
        out.writeCompressedInt(gid);
        out.writeInt(backup_data);
        out.writeLong(create_time);
        if(UnixFile.isRegularFile(mode)) out.writeLong(modify_time);
        out.writeLong(remove_time);
        out.writeShort(backup_level);
        out.writeShort(backup_retention);
        if(UnixFile.isSymLink(mode)) out.writeCompressedUTF(symlink_target, 1);
        else if(UnixFile.isBlockDevice(mode) || UnixFile.isCharacterDevice(mode)) out.writeLong(device_id);
    }
}