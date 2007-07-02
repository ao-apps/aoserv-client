package com.aoindustries.aoserv.client;

/*
 * Copyright 2002-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.md5.*;
import com.aoindustries.sql.*;
import com.aoindustries.util.*;
import com.aoindustries.util.zip.*;
import java.io.*;
import java.sql.*;

/**
 * <code>BackupData</code> stores one unique set of data on a backup partition.  This data
 * may be shared by any number of <code>BackupFile</code>s, <code>MySQLBackup</code>s,
 * <code>PostgresBackup</code>s, or <code>InterBaseBackup</code>s.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class BackupData extends AOServObject<Integer,BackupData> implements SingleTableObject<Integer,BackupData>, Dumpable {

    protected AOServTable<Integer,BackupData> table;

    private int pkey;
    private long created;
    private int backup_partition;
    private long data_size;
    private long compressed_size;
    private long md5_hi;
    private long md5_lo;
    private boolean is_stored;

    public void dump(PrintWriter out) {
        getData(out, getCompressedSize()!=-1, 0, null);
    }

    boolean equalsImpl(Object O) {
	return
            O instanceof BackupData
            && ((BackupData)O).pkey==pkey
	;
    }

    /**
     * Gets the full path that will be used for the backup data with the provided attributes.
     */
    public static String getPathPrefix(
        String backupPartitionPath,
        int pkey
    ) {
        StringBuilder path=new StringBuilder();
        path.append(backupPartitionPath).append('/');
        int firstLevel=(pkey/1000000)%100;
        if(firstLevel!=0) {
            if(firstLevel<10) path.append('0');
            path.append(firstLevel).append('/');
        }
        int secondLevel=(pkey/10000)%100;
        if(firstLevel!=0 || secondLevel!=0) {
            if(secondLevel<10) path.append('0');
            path.append(secondLevel).append('/');
        }
        int thirdLevel=(pkey/100)%100;
        if(thirdLevel<10) path.append('0');
        path.append(thirdLevel).append('/').append(pkey).append('_');
        return path.toString();
    }

    /**
     * Gets the relative path from the BackupPartition directory that will be used for the backup data with the provided attributes.
     */
    public static String getRelativePathPrefix(int pkey) {
        StringBuilder relativePath=new StringBuilder();
        int firstLevel=(pkey/1000000)%100;
        if(firstLevel!=0) {
            if(firstLevel<10) relativePath.append('0');
            relativePath.append(firstLevel).append('/');
        }
        int secondLevel=(pkey/10000)%100;
        if(firstLevel!=0 || secondLevel!=0) {
            if(secondLevel<10) relativePath.append('0');
            relativePath.append(secondLevel).append('/');
        }
        int thirdLevel=(pkey/100)%100;
        if(thirdLevel<10) relativePath.append('0');
        relativePath.append(thirdLevel).append('/').append(pkey).append('_');
        return relativePath.toString();
    }

    /**
     * Files are stored in a directory tree up to three levels deep.  Each of the levels
     * contains one hundred files and one hundred sub directories.  This allows for the
     * efficient location of files when millions of files exist in one backup partition.
     */
    public String getPathPrefix() {
        return getPathPrefix(
            getBackupPartition().getPath(),
            pkey
        );
    }

    public int getPKey() {
        return pkey;
    }

    public long getCreated() {
        return created;
    }

    public void getData(Writer out, boolean decompress, long skipBytes, BitRateProvider bitRateProvider) {
        getData(new WriterOutputStream(out), decompress, skipBytes, bitRateProvider);
    }

    public void getData(OutputStream out, boolean decompress, long skipBytes, BitRateProvider bitRateProvider) {
        try {
            if(!is_stored) throw new SQLException("The backup data is not yet available for BackupData: "+pkey);
            if(decompress) {
                if(compressed_size==-1) throw new SQLException("The backup data is not compressed: "+pkey);
                if(skipBytes>0) throw new SQLException("Unable to skip bytes while decompressing.");
            }
            AOServConnection connection=table.connector.getConnection();
            try {
                CompressedDataOutputStream masterOut=connection.getOutputStream();
                masterOut.writeCompressedInt(AOServProtocol.GET_BACKUP_DATA);
                masterOut.writeCompressedInt(pkey);
                masterOut.writeLong(skipBytes);
                masterOut.flush();

                CompressedDataInputStream masterIn=connection.getInputStream();
                NestedInputStream nestedIn=new NestedInputStream(masterIn);
                try {
                    InputStream in=decompress?(InputStream)new CorrectedGZIPInputStream(nestedIn):nestedIn;
                    try {
                        byte[] buff=BufferManager.getBytes();
                        try {
                            long lastBlockStartTime=bitRateProvider==null?0:System.currentTimeMillis();
                            int bitsInBlock=0;

                            int ret;
                            while((ret=in.read(buff, 0, BufferManager.BUFFER_SIZE))!=-1) {
                                out.write(buff, 0, ret);
                                if(bitRateProvider!=null) {
                                    bitsInBlock+=ret*8;
                                    int blockSize=bitRateProvider.getBlockSize();
                                    if(bitsInBlock>blockSize) {
                                        int bitRate=bitRateProvider.getBitRate();
                                        // Calculate the number of milliseconds that should have passed for this block
                                        int blockTime=bitsInBlock*1000/bitRate;
                                        int blockRemaining=blockTime-((int)(System.currentTimeMillis()-lastBlockStartTime));
                                        if(blockRemaining>0) {
                                            try {
                                                Thread.sleep(blockRemaining);
                                            } catch(InterruptedException err) {
                                                InterruptedIOException ioErr=new InterruptedIOException();
                                                ioErr.initCause(err);
                                                throw ioErr;
                                            }
                                        }
                                        lastBlockStartTime=System.currentTimeMillis();
                                        bitsInBlock=0;
                                    }
                                }
                            }
                            if(bitRateProvider!=null && bitsInBlock>0) {
                                // Sleep for the bits in the last block
                                int bitRate=bitRateProvider.getBitRate();
                                // Calculate the number of milliseconds that should have passed for this block
                                int blockTime=bitsInBlock*1000/bitRate;
                                int blockRemaining=blockTime-((int)(System.currentTimeMillis()-lastBlockStartTime));
                                if(blockRemaining>0) {
                                    try {
                                        Thread.sleep(blockRemaining);
                                    } catch(InterruptedException err) {
                                        InterruptedIOException ioErr=new InterruptedIOException();
                                        ioErr.initCause(err);
                                        throw ioErr;
                                    }
                                }
                            }
                        } finally {
                            BufferManager.release(buff);
                        }
                    } finally {
                        if(decompress) in.close();
                    }
                } finally {
                    nestedIn.close();
                }
            } catch(IOException err) {
                connection.close();
                throw err;
            } finally {
                table.connector.releaseConnection(connection);
            }
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public void getData(RandomAccessFile out, boolean decompress, long skipBytes, BitRateProvider bitRateProvider) {
        try {
            if(!is_stored) throw new SQLException("The backup data is not yet available for BackupData: "+pkey);
            if(decompress) {
                if(compressed_size==-1) throw new SQLException("The backup data is not compressed: "+pkey);
                if(skipBytes>0) throw new SQLException("Unable to skip bytes while decompressing.");
            }
            AOServConnection connection=table.connector.getConnection();
            try {
                CompressedDataOutputStream masterOut=connection.getOutputStream();
                masterOut.writeCompressedInt(AOServProtocol.GET_BACKUP_DATA);
                masterOut.writeCompressedInt(pkey);
                masterOut.writeLong(skipBytes);
                masterOut.flush();

                CompressedDataInputStream masterIn=connection.getInputStream();
                NestedInputStream nestedIn=new NestedInputStream(masterIn);
                try {
                    InputStream in=decompress?(InputStream)new CorrectedGZIPInputStream(nestedIn):nestedIn;
                    try {
                        byte[] buff=BufferManager.getBytes();
                        try {
                            long lastBlockStartTime=bitRateProvider==null?0:System.currentTimeMillis();
                            int bitsInBlock=0;

                            int ret;
                            while((ret=in.read(buff, 0, BufferManager.BUFFER_SIZE))!=-1) {
                                out.write(buff, 0, ret);
                                if(bitRateProvider!=null) {
                                    bitsInBlock+=ret*8;
                                    int blockSize=bitRateProvider.getBlockSize();
                                    if(bitsInBlock>blockSize) {
                                        int bitRate=bitRateProvider.getBitRate();
                                        // Calculate the number of milliseconds that should have passed for this block
                                        int blockTime=bitsInBlock*1000/bitRate;
                                        int blockRemaining=blockTime-((int)(System.currentTimeMillis()-lastBlockStartTime));
                                        if(blockRemaining>0) {
                                            try {
                                                Thread.sleep(blockRemaining);
                                            } catch(InterruptedException err) {
                                                InterruptedIOException ioErr=new InterruptedIOException();
                                                ioErr.initCause(err);
                                                throw ioErr;
                                            }
                                        }
                                        lastBlockStartTime=System.currentTimeMillis();
                                        bitsInBlock=0;
                                    }
                                }
                            }
                            if(bitRateProvider!=null && bitsInBlock>0) {
                                // Sleep for the bits in the last block
                                int bitRate=bitRateProvider.getBitRate();
                                // Calculate the number of milliseconds that should have passed for this block
                                int blockTime=bitsInBlock*1000/bitRate;
                                int blockRemaining=blockTime-((int)(System.currentTimeMillis()-lastBlockStartTime));
                                if(blockRemaining>0) {
                                    try {
                                        Thread.sleep(blockRemaining);
                                    } catch(InterruptedException err) {
                                        InterruptedIOException ioErr=new InterruptedIOException();
                                        ioErr.initCause(err);
                                        throw ioErr;
                                    }
                                }
                            }
                        } finally {
                            BufferManager.release(buff);
                        }
                    } finally {
                        if(decompress) in.close();
                    }
                } finally {
                    nestedIn.close();
                }
            } catch(IOException err) {
                connection.close();
                throw err;
            } finally {
                table.connector.releaseConnection(connection);
            }
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public BackupPartition getBackupPartition() {
        BackupPartition obj = table.connector.backupPartitions.get(backup_partition);
        if (obj == null) throw new WrappedException(new SQLException("Unable to find BackupPartition: "+backup_partition));
        return obj;
    }

    public String getFilename() {
        return table.connector.requestStringQuery(AOServProtocol.GET_FILENAME_FOR_BACKUP_DATA, pkey);
    }

    public boolean isStored() {
        return is_stored;
    }
    
    public long getDataSize() {
        return data_size;
    }
    
    public long getCompressedSize() {
        return compressed_size;
    }

    public String getMD5() {
        return MD5.getMD5String(md5_hi, md5_lo);
    }

    public long getMD5Hi() {
        return md5_hi;
    }
    
    public long getMD5Lo() {
        return md5_lo;
    }

    public Object getColumn(int i) {
        switch(i) {
            case 0: return Integer.valueOf(pkey);
            case 1: return new java.sql.Date(created);
            case 2: return Integer.valueOf(backup_partition);
            case 3: return Long.valueOf(data_size);
            case 4: return compressed_size==-1?null:Long.valueOf(compressed_size);
            case 5: return Long.valueOf(md5_hi);
            case 6: return Long.valueOf(md5_lo);
            case 7: return is_stored?Boolean.TRUE:Boolean.FALSE;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public Integer getKey() {
	return pkey;
    }

    final public AOServTable<Integer,BackupData> getTable() {
        return table;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.BACKUP_DATA;
    }

    int hashCodeImpl() {
	return pkey;
    }

    void initImpl(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
        created=result.getTimestamp(2).getTime();
        backup_partition=result.getInt(3);
        data_size=result.getLong(4);
        compressed_size=result.getLong(5);
        if(result.wasNull()) compressed_size=-1;
        md5_hi=result.getLong(6);
        md5_lo=result.getLong(7);
        is_stored=result.getBoolean(8);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readInt();
        created=in.readLong();
        backup_partition=in.readCompressedInt();
        data_size=in.readLong();
        compressed_size=in.readLong();
        md5_hi=in.readLong();
        md5_lo=in.readLong();
        is_stored=in.readBoolean();
    }

    public void setTable(AOServTable<Integer,BackupData> table) {
	if(this.table!=null) throw new IllegalStateException("table already set");
	this.table=table;
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        out.writeInt(pkey);
        out.writeLong(created);
        out.writeCompressedInt(backup_partition);
        out.writeLong(data_size);
        out.writeLong(compressed_size);
        out.writeLong(md5_hi);
        out.writeLong(md5_lo);
        out.writeBoolean(is_stored);
    }
}