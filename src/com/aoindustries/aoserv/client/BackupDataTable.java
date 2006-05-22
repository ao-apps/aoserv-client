package com.aoindustries.aoserv.client;

/*
 * Copyright 2002-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.md5.*;
import com.aoindustries.profiler.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.zip.*;

/**
 * @see  BackupData
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class BackupDataTable extends AOServTable<Integer,BackupData> {

    BackupDataTable(AOServConnector connector) {
	super(connector, BackupData.class);
    }

    Object[] findOrAddBackupData(
        Server server,
        long length,
        long md5_hi,
        long md5_lo
    ) {
        try {
            int pkey;
            boolean hasData;

            IntList invalidateList;
            AOServConnection connection=connector.getConnection();
            try {
                CompressedDataOutputStream out=connection.getOutputStream();
                out.writeCompressedInt(AOServProtocol.FIND_OR_ADD_BACKUP_DATA);
                out.writeCompressedInt(server.pkey);
                out.writeLong(length);
                out.writeLong(md5_hi);
                out.writeLong(md5_lo);
                out.flush();

                CompressedDataInputStream in=connection.getInputStream();
                int code=in.readByte();
                if(code==AOServProtocol.DONE) {
                    pkey=in.readCompressedInt();
                    hasData=in.readBoolean();
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
            return new Object[] {
                Integer.valueOf(pkey),
                hasData?Boolean.TRUE:Boolean.FALSE
            };
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    Object[] findOrAddBackupDatas(
        Server server,
        int batchSize,
        long[] lengths,
        long[] md5_his,
        long[] md5_los
    ) {
        try {
            int[] pkeys=new int[batchSize];
            boolean[] hasDatas=new boolean[batchSize];

            // Shortcut when possible to avoid master server latency
            if(batchSize==0) {
                return new Object[] {
                    pkeys,
                    hasDatas
                };
            }

            // Resolve the unique data to avoid sending duplicate requests to the master
            int uniqueBatchSize=0;
            long[] uniqueLengths=new long[batchSize];
            long[] uniqueMD5His=new long[batchSize];
            long[] uniqueMD5Los=new long[batchSize];
            for(int c=0;c<batchSize;c++) {
                // Look for existing entry
                long length=lengths[c];
                long md5_hi=md5_his[c];
                long md5_lo=md5_los[c];
                boolean found=false;
                for(int d=0;d<c;d++) {
                    if(
                        uniqueLengths[d]==length
                        && uniqueMD5His[d]==md5_hi
                        && uniqueMD5Los[d]==md5_lo
                    ) {
                        found=true;
                        break;
                    }
                }
                if(!found) {
                    // Not found, add to the list sent to the server
                    uniqueLengths[uniqueBatchSize]=length;
                    uniqueMD5His[uniqueBatchSize]=md5_hi;
                    uniqueMD5Los[uniqueBatchSize]=md5_lo;
                    uniqueBatchSize++;
                }
            }

            IntList invalidateList;
            AOServConnection connection=connector.getConnection();
            try {
                CompressedDataOutputStream out=connection.getOutputStream();
                out.writeCompressedInt(AOServProtocol.FIND_OR_ADD_BACKUP_DATAS);
                out.writeCompressedInt(server.pkey);
                out.writeCompressedInt(uniqueBatchSize);
                for(int c=0;c<uniqueBatchSize;c++) {
                    out.writeLong(uniqueLengths[c]);
                    out.writeLong(uniqueMD5His[c]);
                    out.writeLong(uniqueMD5Los[c]);
                }
                out.flush();

                CompressedDataInputStream in=connection.getInputStream();
                int code=in.readByte();
                if(code==AOServProtocol.DONE) {
                    for(int c=0;c<uniqueBatchSize;c++) {
                        int pkey=in.readCompressedInt();
                        boolean hasData=in.readBoolean();
                        // Apply to all matching this unique lookup
                        long length=uniqueLengths[c];
                        long md5_hi=uniqueMD5His[c];
                        long md5_lo=uniqueMD5Los[c];
                        for(int d=0;d<batchSize;d++) {
                            if(
                                lengths[d]==length
                                && md5_his[d]==md5_hi
                                && md5_los[d]==md5_lo
                            ) {
                                pkeys[d]=pkey;
                                hasDatas[d]=hasData;
                            }
                        }
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
            return new Object[] {
                pkeys,
                hasDatas
            };
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public BackupData get(Object pkey) {
        return get(((Integer)pkey).intValue());
    }

    public BackupData get(int pkey) {
        return getObject(AOServProtocol.GET_OBJECT, SchemaTable.BACKUP_DATA, pkey);
    }

    public void getBackupData(
        int batchSize,
        List<FileBackup> fileBackups,
        BackupData[] backupDatas
    ) {
        try {
            // Count the number of entries that will be looked up
            int count=0;
            for(int c=0;c<batchSize;c++) {
                FileBackup fb=fileBackups.get(c);
                if(
                    fb!=null
                    && fb.backup_data!=-1
                ) count++;
            }
            if(count>0) {
                AOServConnection connection=connector.getConnection();
                try {
                    CompressedDataOutputStream out=connection.getOutputStream();
                    out.writeCompressedInt(AOServProtocol.GET_BACKUP_DATAS_PKEYS);
                    out.writeCompressedInt(count);
                    for(int c=0;c<batchSize;c++) {
                        FileBackup fb=fileBackups.get(c);
                        if(
                            fb!=null
                            && fb.backup_data!=-1
                        ) out.writeCompressedInt(fb.backup_data);
                    }
                    out.flush();

                    CompressedDataInputStream in=connection.getInputStream();
                    for(int c=0;c<batchSize;c++) {
                        FileBackup fb=fileBackups.get(c);
                        if(
                            fb!=null
                            && fb.backup_data!=-1
                        ) {
                            int code=in.readByte();
                            if(code==AOServProtocol.NEXT) {
                                BackupData bd=new BackupData();
                                bd.read(in);
                                bd.setTable(this);
                                backupDatas[c]=bd;
                            } else if(code==AOServProtocol.DONE) {
                                backupDatas[c]=null;
                            } else {
                                AOServProtocol.checkResult(code, in);
                                throw new IOException("Unexpected response code: "+code);
                            }
                        } else backupDatas[c]=null;
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

    public int getCachedRowCount() {
        return connector.requestIntQuery(AOServProtocol.GET_CACHED_ROW_COUNT, SchemaTable.BACKUP_DATA);
    }

    public int size() {
        return connector.requestIntQuery(AOServProtocol.GET_ROW_COUNT, SchemaTable.BACKUP_DATA);
    }

    public IntsAndLongs getBackupDataPKeys(boolean hasDataOnly, BackupLevel minBackupLevel) {
        try {
            IntList pkeys=new SortedIntArrayList();
            LongList sizes=new LongArrayList();
            AOServConnection connection=connector.getConnection();
            try {
                CompressedDataOutputStream out=connection.getOutputStream();
                out.writeCompressedInt(AOServProtocol.GET_BACKUP_DATA_PKEYS);
                out.writeBoolean(hasDataOnly);
                out.writeShort(minBackupLevel.getLevel());
                out.flush();

                CompressedDataInputStream in=connection.getInputStream();
                int code=in.readByte();
                if(code==AOServProtocol.DONE) {
                    int lastpkey=0;
                    int pkey;
                    while((pkey=in.readCompressedInt())!=-1) {
                        lastpkey+=pkey;
                        pkeys.add(lastpkey);
                        sizes.add(in.readLong());
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
            return new IntsAndLongs(pkeys, sizes);
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public List<BackupData> getRows() {
        List<BackupData> list=new ArrayList<BackupData>();
        getObjects(list, AOServProtocol.GET_TABLE, SchemaTable.BACKUP_DATA);
        return list;
    }

    int getTableID() {
	return SchemaTable.BACKUP_DATA;
    }

    protected BackupData getUniqueRowImpl(int col, Object value) {
        if(col!=0) throw new IllegalArgumentException("Not a unique column: "+col);
        return get(value);
    }

    public boolean sendData(
        int backupData,
        InputStream in,
        String filename,
        long length,
        long md5_hi,
        long md5_lo,
        boolean fileIsCompressed,
        boolean shouldBeCompressed,
        BitRateProvider bitRateProvider
    ) {
        try {
            MD5InputStream md5In=fileIsCompressed?null:new MD5InputStream(new BufferedInputStream(in));
            InputStream fileIn=fileIsCompressed?(InputStream)in:md5In;
            long bytesRead=0;
            boolean commit;
            OutputStream sendOut=null;
            try {
                BackupDataOutputStream backupOut=new BackupDataOutputStream(
                                                                            connector,
                                                                            backupData,
                                                                            filename,
                                                                            fileIsCompressed || shouldBeCompressed,
                                                                            md5_hi,
                                                                            md5_lo
                                                                            );
                if(bitRateProvider==null) {
                    sendOut=
                        !fileIsCompressed && shouldBeCompressed
                        ?(OutputStream)new GZIPOutputStream(new BufferedOutputStream(backupOut))
                        :backupOut
                        ;
                } else {
                    sendOut=
                        !fileIsCompressed && shouldBeCompressed
                        ?(OutputStream)new GZIPOutputStream(new BufferedOutputStream(new BitRateOutputStream(backupOut, bitRateProvider)))
                        :new BitRateOutputStream(backupOut, bitRateProvider)
                        ;
                }
                try {
                    // Should only read up to a maximum length of length, because log files grow between initial processing
                    // and here.
                    byte[] buff=BufferManager.getBytes();
                    try {
                        while(bytesRead<length) {
                            long bytesLeft=length-bytesRead;
                            if(bytesLeft>BufferManager.BUFFER_SIZE) bytesLeft=BufferManager.BUFFER_SIZE;
                            int ret=fileIn.read(buff, 0, (int)bytesLeft);

                            // Unexpected end of file
                            if(ret==-1) break;

                            sendOut.write(buff, 0, ret);
                            bytesRead+=ret;
                        }
                    } finally {
                        BufferManager.release(buff);
                    }
                } finally {
                    fileIn.close();
                }

                // Only verify length and md5 when not already compressed
                if(!fileIsCompressed) {
                    if(length!=bytesRead) commit=false;
                    else {
                        byte[] newMD5=md5In.hash();
                        commit=
                            MD5.getMD5Hi(newMD5)==md5_hi
                            && MD5.getMD5Lo(newMD5)==md5_lo
                            ;
                    }
                } else commit=true;

                backupOut.setCommitOnClose(commit);
            } finally {
                if(sendOut!=null) {
                    sendOut.flush();
                    sendOut.close();
                }
            }
            return commit;
        } catch(IOException err) {
            throw new WrappedException(err);
        }
    }

    public class SendDataToDaemonAccess {

        private int aoServerPKey;
        private String host;
        private int port;
        private String protocol;
        private long key;
        
        private SendDataToDaemonAccess(
            int aoServerPKey,
            String host,
            int port,
            String protocol,
            long key
        ) {
            this.aoServerPKey=aoServerPKey;
            this.host=host;
            this.port=port;
            this.protocol=protocol;
            this.key=key;
        }

        public int getAOServerPKey() {
            return aoServerPKey;
        }

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }

        public Protocol getProtocol() {
            Protocol pr = connector.protocols.get(protocol);
            if(pr==null) throw new WrappedException(new SQLException("Unable to find Protocol: "+protocol));
            return pr;
        }

        public long getKey() {
            return key;
        }
    }

    /**
     * Requests access in order to send the data for the given file.
     *
     * @return  <code>null</code> if the data does not need to be sent
     *          or the <code>SendDataToDaemonAccess</code> providing the
     *          connection details.
     */
    public SendDataToDaemonAccess requestSendBackupDataToDaemon(
        int backupData,
        long md5_hi,
        long md5_lo
    ) {
        try {
            Profiler.startProfile(Profiler.UNKNOWN, BackupDataTable.class, "requestSendBackupDataToDaemon(int,long,long,long)", null);
            try {
                AOServConnection connection=connector.getConnection();
                try {
                    CompressedDataOutputStream out=connection.getOutputStream();
                    out.writeCompressedInt(AOServProtocol.REQUEST_SEND_BACKUP_DATA_TO_DAEMON);
                    out.writeCompressedInt(backupData);
                    out.writeLong(md5_hi);
                    out.writeLong(md5_lo);
                    out.flush();

                    CompressedDataInputStream in=connection.getInputStream();
                    int code=in.readByte();
                    if(code==AOServProtocol.DONE) {
                        return null;
                    } else if(code==AOServProtocol.NEXT) {
                        int aoServerPKey=in.readCompressedInt();
                        String host=in.readUTF();
                        int port=in.readCompressedInt();
                        String protocol=in.readUTF();
                        long key=in.readLong();
                        return new SendDataToDaemonAccess(
                            aoServerPKey,
                            host,
                            port,
                            protocol,
                            key
                        );
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
            } finally {
                Profiler.endProfile(Profiler.UNKNOWN);
            }
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
	String command=args[0];
        if(command.equalsIgnoreCase(AOSHCommand.GET_BACKUP_DATA)) {
            if(AOSH.checkParamCount(AOSHCommand.GET_BACKUP_DATA, args, 1, err)) {
                connector.simpleAOClient.getBackupData(AOSH.parseInt(args[1], "pkey"), out);
                out.flush();
            }
            return true;
	}
	return false;
    }

    public void flagAsStored(int backupData, boolean isCompressed, long compressedSize) {
        Profiler.startProfile(Profiler.UNKNOWN, BackupDataTable.class, "flagAsStored(int,boolean,long)", null);
        try {
            if(isCompressed) {
                connector.requestUpdate(AOServProtocol.FLAG_BACKUP_DATA_AS_STORED, backupData, true, compressedSize);
            } else {
                connector.requestUpdate(AOServProtocol.FLAG_BACKUP_DATA_AS_STORED, backupData, false);
            }
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }
}
