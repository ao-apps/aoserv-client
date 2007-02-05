package com.aoindustries.aoserv.client;

/*
 * Copyright 2002-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.io.unix.*;
import com.aoindustries.profiler.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  FileBackup
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class FileBackupTable extends AOServTable<Integer,FileBackup> {

    FileBackupTable(AOServConnector connector) {
	super(connector, FileBackup.class);
    }

    int addFileBackup(
        Server server,
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
        try {
            IntList invalidateList;
            AOServConnection connection=connector.getConnection();
            int pkey;
            try {
                CompressedDataOutputStream out=connection.getOutputStream();
                out.writeCompressedInt(AOServProtocol.ADD);
                out.writeCompressedInt(SchemaTable.FILE_BACKUPS);
                out.writeCompressedInt(server.pkey);
                out.writeUTF(path);
                out.writeShort(device.pkey);
                out.writeLong(inode);
                out.writeCompressedInt(packageNum);
                out.writeLong(mode);
                out.writeCompressedInt(uid);
                out.writeCompressedInt(gid);
                out.writeCompressedInt(backupData);
                if(UnixFile.isRegularFile(mode)) {
                    out.writeLong(md5_hi);
                    out.writeLong(md5_lo);
                    out.writeLong(modifyTime);
                }
                out.writeShort(backupLevel);
                out.writeShort(backupRetention);
                if(UnixFile.isSymLink(mode)) out.writeUTF(symlinkTarget);
                else if(UnixFile.isBlockDevice(mode) || UnixFile.isCharacterDevice(mode)) out.writeLong(deviceID);
                out.flush();

                CompressedDataInputStream in=connection.getInputStream();
                int code=in.readByte();
                if(code==AOServProtocol.DONE) {
                    pkey=in.readCompressedInt();
                    invalidateList=AOServConnector.readInvalidateList(in);
                } else {
                    AOServProtocol.checkResult(code, in);
                    throw new IOException("Unexpected response code: "+code);
                }
            } catch(IOException err) {
                connection.close();
                throw err;
            } finally {
                connector.releaseConnection(connection);
            }
            connector.tablesUpdated(invalidateList);
            return pkey;
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    int[] addFileBackups(
        Server server,
        int batchSize,
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
        try {
            int[] pkeys=new int[batchSize];

            // Shortcut when possible to avoid master server latency
            int count=0;
            for(int c=0;c<batchSize;c++) if(paths[c]!=null) count++;
            if(count==0) {
                for(int c=0;c<batchSize;c++) pkeys[c]=-1;
                return pkeys;
            }

            IntList invalidateList;
            AOServConnection connection=connector.getConnection();
            try {
                CompressedDataOutputStream out=connection.getOutputStream();
                out.writeCompressedInt(AOServProtocol.ADD_FILE_BACKUPS);
                out.writeCompressedInt(server.pkey);
                out.writeCompressedInt(count);
                for(int c=0;c<batchSize;c++) {
                    String path=paths[c];
                    if(path!=null) {
                        long mode=modes[c];
                        out.writeCompressedUTF(path, 0);
                        out.writeShort(devices[c]==null?-1:devices[c].pkey);
                        out.writeLong(inodes[c]);
                        out.writeCompressedInt(packageNums[c]);
                        out.writeLong(mode);
                        out.writeCompressedInt(uids[c]);
                        out.writeCompressedInt(gids[c]);
                        out.writeCompressedInt(backupDatas[c]);
                        if(UnixFile.isRegularFile(mode)) {
                            out.writeLong(md5_his[c]);
                            out.writeLong(md5_los[c]);
                            out.writeLong(modifyTimes[c]);
                        }
                        out.writeShort(backupLevels[c]);
                        out.writeShort(backupRetentions[c]);
                        if(UnixFile.isSymLink(mode)) out.writeCompressedUTF(symlinkTargets[c], 1);
                        else if(UnixFile.isBlockDevice(mode) || UnixFile.isCharacterDevice(mode)) out.writeLong(deviceIDs[c]);
                    }
                }
                out.flush();

                CompressedDataInputStream in=connection.getInputStream();
                int code=in.readByte();
                if(code==AOServProtocol.DONE) {
                    int startPKey=in.readCompressedInt();
                    for(int c=0;c<batchSize;c++) {
                        if(paths[c]!=null) pkeys[c]=startPKey++;
                        else pkeys[c]=-1;
                    }
                    invalidateList=AOServConnector.readInvalidateList(in);
                } else {
                    AOServProtocol.checkResult(code, in);
                    throw new IOException("Unexpected response code: "+code);
                }
            } catch(IOException err) {
                connection.close();
                throw err;
            } finally {
                connector.releaseConnection(connection);
            }
            connector.tablesUpdated(invalidateList);
            return pkeys;
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }
    
    Object[] findLatestFileBackupSetAttributeMatches(
        Server server,
        int batchSize,
        String[] paths,
        FileBackupDevice[] devices,
        long[] inodes,
        int[] packages,
        long[] modes,
        int[] uids,
        int[] gids,
        long[] modify_times,
        short[] backup_levels,
        short[] backup_retentions,
        long[] lengths,
        String[] symlink_targets,
        long[] deviceIDs
    ) {
        try {
            int[] fileBackups=new int[batchSize];
            int[] backupDatas=new int[batchSize];
            long[] md5_his=new long[batchSize];
            long[] md5_los=new long[batchSize];
            boolean[] hasDatas=new boolean[batchSize];

            // Shortcut when possible to avoid master server latency
            if(batchSize>0) {
                AOServConnection connection=connector.getConnection();
                try {
                    CompressedDataOutputStream out=connection.getOutputStream();
                    out.writeCompressedInt(AOServProtocol.FIND_LATEST_FILE_BACKUP_SET_ATTRIBUTE_MATCHES);
                    out.writeCompressedInt(server.pkey);
                    out.writeCompressedInt(batchSize);
                    for(int c=0;c<batchSize;c++) {
                        out.writeCompressedUTF(paths[c], 0);
                        out.writeShort(devices[c]==null?-1:devices[c].pkey);
                        out.writeLong(inodes[c]);
                        out.writeCompressedInt(packages[c]);
                        long mode=modes[c];
                        out.writeLong(mode);
                        out.writeCompressedInt(uids[c]);
                        out.writeCompressedInt(gids[c]);
                        out.writeShort(backup_levels[c]);
                        out.writeShort(backup_retentions[c]);
                        if(UnixFile.isRegularFile(mode)) {
                            out.writeLong(modify_times[c]);
                            out.writeLong(lengths[c]);
                        } else if(UnixFile.isSymLink(mode)) {
                            out.writeCompressedUTF(symlink_targets[c], 3);
                        } else if(UnixFile.isBlockDevice(mode) || UnixFile.isCharacterDevice(mode)) {
                            out.writeLong(deviceIDs[c]);
                        }
                    }
                    out.flush();

                    CompressedDataInputStream in=connection.getInputStream();
                    int code=in.readByte();
                    if(code==AOServProtocol.DONE) {
                        for(int c=0;c<batchSize;c++) {
                            int fileBackup=fileBackups[c]=in.readCompressedInt();
                            long mode;
                            if(
                                fileBackup!=-1
                                && UnixFile.isRegularFile(modes[c])
                            ) {
                                backupDatas[c]=in.readCompressedInt();
                                md5_his[c]=in.readLong();
                                md5_los[c]=in.readLong();
                                hasDatas[c]=in.readBoolean();
                            } else {
                                backupDatas[c]=-1;
                                md5_his[c]=md5_los[c]=-1;
                                hasDatas[c]=false;
                            }
                        }
                    } else {
                        AOServProtocol.checkResult(code, in);
                        throw new IOException("Unexpected response code: "+code);
                    }
                } catch(IOException err) {
                    connection.close();
                    throw err;
                } finally {
                    connector.releaseConnection(connection);
                }
            }
            Object[] OA={
                fileBackups,
                backupDatas,
                md5_his,
                md5_los,
                hasDatas
            };
            return OA;
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public FileBackup get(Object pkey) {
        return get(((Integer)pkey).intValue());
    }

    public FileBackup get(int pkey) {
        return getObject(AOServProtocol.GET_OBJECT, SchemaTable.FILE_BACKUPS, pkey);
    }

    public void getFileBackups(
        int batchSize,
        int[] pkeys,
        List<FileBackup> fileBackups
    ) {
        try {
            if(batchSize>0) {
                AOServConnection connection=connector.getConnection();
                try {
                    CompressedDataOutputStream out=connection.getOutputStream();
                    out.writeCompressedInt(AOServProtocol.GET_FILE_BACKUPS_PKEYS);
                    out.writeCompressedInt(batchSize);
                    for(int c=0;c<batchSize;c++) out.writeCompressedInt(pkeys[c]);
                    out.flush();

                    CompressedDataInputStream in=connection.getInputStream();
                    for(int c=0;c<batchSize;c++) {
                        int code=in.readByte();
                        if(code==AOServProtocol.NEXT) {
                            FileBackup fb=new FileBackup();
                            fb.read(in);
                            fb.setTable(this);
                            fileBackups.add(fb);
                        } else if(code==AOServProtocol.DONE) {
                            fileBackups.add(null);
                        } else {
                            AOServProtocol.checkResult(code, in);
                            throw new IOException("Unexpected response code: "+code);
                        }
                    }
                } catch(IOException err) {
                    connection.close();
                    throw err;
                } finally {
                    connector.releaseConnection(connection);
                }
            }
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    List<FileBackup> getFileBackups(Server server) {
	return getObjectsNoProgress(AOServProtocol.GET_FILE_BACKUPS_SERVER, server.pkey);
    }

    List<FileBackup> getFileBackupChildren(Server server, String path) {
	return getObjectsNoProgress(AOServProtocol.GET_FILE_BACKUP_CHILDREN, server.pkey, path);
    }

    List<FileBackup> findHardLinks(Server server, FileBackupDevice device, long inode) {
	return getObjectsNoProgress(AOServProtocol.FIND_HARD_LINKS, server.pkey, device.getPKey(), inode);
    }

    List<FileBackup> getFileBackupVersions(Server server, String path) {
	return getObjectsNoProgress(AOServProtocol.GET_FILE_BACKUP_VERSIONS, server.pkey, path);
    }

    public List<FileBackup> findFileBackupsByMD5(long md5_hi, long md5_lo, Server server) {
	return getObjectsNoProgress(AOServProtocol.FIND_FILE_BACKUPS_BY_MD5, md5_hi, md5_lo, server==null?-1:server.pkey);
    }

    /**
     * Gets a set of backup files for a server.
     *
     * @return  <code>Object[]</code> wrapping <code>IntList</code> of pkeys along with <code>List<Boolean></code>
     *          of <code>Boolean</code> indicating if each item is a directory.
     */
    IntsAndBooleans getFileBackupSet(Server server, String path, long time) {
        try {
            AOServConnection connection=connector.getConnection();
            try {
                CompressedDataOutputStream out=connection.getOutputStream();
                out.writeCompressedInt(AOServProtocol.GET_FILE_BACKUP_SET_SERVER);
                out.writeCompressedInt(server.pkey);
                out.writeBoolean(path!=null); if(path!=null) out.writeUTF(path);
                out.writeLong(time);
                out.flush();

                CompressedDataInputStream in=connection.getInputStream();
                int code=in.readByte();
                if(code==AOServProtocol.DONE) {
                    IntList pkeys=new IntArrayList(200000);
                    BitSet isDirectories=new BitSet(200000);

                    int pkey;
                    int index=0;
                    while((pkey=in.readInt())!=-1) {
                        pkeys.add(pkey);
                        isDirectories.set(index++, in.readBoolean());
                    }
                    return new IntsAndBooleans(pkeys, isDirectories);
                } else {
                    AOServProtocol.checkResult(code, in);
                    throw new IOException("Unexpected response code: "+code);
                }
            } catch(IOException err) {
                connection.close();
                throw err;
            } finally {
                connector.releaseConnection(connection);
            }
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public int getCachedRowCount() {
        return connector.requestIntQuery(AOServProtocol.GET_CACHED_ROW_COUNT, SchemaTable.FILE_BACKUPS);
    }

    public int size() {
        return connector.requestIntQuery(AOServProtocol.GET_ROW_COUNT, SchemaTable.FILE_BACKUPS);
    }

    public void flagFileBackupsAsDeleted(
        int batchSize,
        int[] pkeys
    ) {
        try {
            IntList invalidateList;
            AOServConnection connection=connector.getConnection();
            try {
                CompressedDataOutputStream out=connection.getOutputStream();
                out.writeCompressedInt(AOServProtocol.FLAG_FILE_BACKUPS_AS_DELETED);
                out.writeCompressedInt(batchSize);
                for(int c=0;c<batchSize;c++) out.writeCompressedInt(pkeys[c]);
                out.flush();

                CompressedDataInputStream in=connection.getInputStream();
                int code=in.readByte();
                if(code!=AOServProtocol.DONE) {
                    AOServProtocol.checkResult(code, in);
                    throw new IOException("Unexpected response code: "+code);
                }
                invalidateList=AOServConnector.readInvalidateList(in);
            } catch(IOException err) {
                connection.close();
                throw err;
            } finally {
                connector.releaseConnection(connection);
            }
            connector.tablesUpdated(invalidateList);
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    SortedIntArrayList getLatestFileBackupSet(Server server) {
        try {
            SortedIntArrayList pkeys=new SortedIntArrayList();
            AOServConnection connection=connector.getConnection();
            try {
                CompressedDataOutputStream out=connection.getOutputStream();
                out.writeCompressedInt(AOServProtocol.GET_LATEST_FILE_BACKUP_SET);
                out.writeCompressedInt(server.pkey);
                out.flush();

                CompressedDataInputStream in=connection.getInputStream();
                int code=in.readByte();
                if(code==AOServProtocol.DONE) {
                    int pkey;
                    while((pkey=in.readCompressedInt())!=-1) pkeys.add(pkey);
                } else {
                    AOServProtocol.checkResult(code, in);
                    throw new IOException("Unexpected response code: "+code);
                }
            } catch(IOException err) {
                connection.close();
                throw err;
            } finally {
                connector.releaseConnection(connection);
            }
            return pkeys;
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public List<FileBackup> getRows() {
        List<FileBackup> list=new ArrayList<FileBackup>();
        getObjects(list, AOServProtocol.GET_TABLE, SchemaTable.FILE_BACKUPS);
        return list;
    }

    int getTableID() {
	return SchemaTable.FILE_BACKUPS;
    }

    protected FileBackup getUniqueRowImpl(int col, Object value) {
        if(col!=FileBackup.COLUMN_PKEY) throw new UnsupportedOperationException("Not a unique column: "+col);
        return get(value);
    }

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.REMOVE_FILE_BACKUP)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_FILE_BACKUP, args, 1, err)) {
                connector.simpleAOClient.removeFileBackup(
                    AOSH.parseInt(args[1], "pkey")
                );
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.GET_FILE_BACKUP)) {
            if(AOSH.checkParamCount(AOSHCommand.GET_FILE_BACKUP, args, 1, err)) {
                connector.simpleAOClient.getFileBackup(AOSH.parseInt(args[1], "pkey"), out);
                out.flush();
            }
            return true;
	}
	return false;
    }

    void removeExpiredFileBackups(Server server) {
	connector.requestUpdateIL(
            AOServProtocol.REMOVE_EXPIRED_FILE_BACKUPS,
            server.pkey
	);
    }

    public void removeFileBackup(int pkey) {
	connector.requestUpdateIL(
            AOServProtocol.REMOVE,
            SchemaTable.FILE_BACKUPS,
            pkey
	);
    }
}