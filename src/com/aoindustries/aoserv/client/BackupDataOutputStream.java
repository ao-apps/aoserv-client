package com.aoindustries.aoserv.client;

/*
 * Copyright 2002-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  BackupDataTable#sendData
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class BackupDataOutputStream extends OutputStream {

    private AOServConnector connector;
    private AOServConnection conn;
    private boolean closed=false;
    private CompressedDataOutputStream out;
    private boolean commitOnClose=true;

    public BackupDataOutputStream(
        AOServConnector connector,
        int backupData,
        String filename,
        boolean isCompressed,
        long md5_hi,
        long md5_lo
    ) throws IOException {
        this.connector=connector;
        conn=connector.getConnection();
        boolean initDone=false;
        try {
            out=conn.getOutputStream();
            out.writeCompressedInt(AOServProtocol.CommandID.SEND_BACKUP_DATA.ordinal());
            out.writeCompressedInt(backupData);
            out.writeUTF(filename);
            out.writeBoolean(isCompressed);
            out.writeLong(md5_hi);
            out.writeLong(md5_lo);
            initDone=true;
        } catch(IOException err) {
            conn.close();
            throw err;
        } finally {
            if(!initDone) {
                connector.releaseConnection(conn);
                closed=true;
            }
        }
    }
    
    synchronized public void close() throws IOException {
        if(!closed) {
            IntList invalidateList=null;
            try {
                out.write(AOServProtocol.DONE);
                out.writeBoolean(commitOnClose);
                out.flush();

                CompressedDataInputStream in=conn.getInputStream();
                int code=in.read();
                if(code!=AOServProtocol.DONE) {
                    AOServProtocol.checkResult(code, in);
                    throw new IOException("Unexpected response code: "+code);
                }
                invalidateList=AOServConnector.readInvalidateList(in);
            } catch(IOException err) {
                conn.close();
                throw err;
            } catch(SQLException err) {
                conn.close();
                IOException ioErr=new IOException("Error closing BackupDataOutputStream");
                ioErr.initCause(err);
                throw ioErr;
            } finally {
                connector.releaseConnection(conn);
                closed=true;
            }
            connector.tablesUpdated(invalidateList);
        }
    }

    synchronized public void flush() throws IOException {
        try {
            out.flush();
        } catch(IOException err) {
            commitOnClose=false;
            throw err;
        }
    }

    public void setCommitOnClose(boolean commit) {
        commitOnClose=commit;
    }

    synchronized public void write(byte[] buff, int off, int len) throws IOException {
        try {
            while(len>0) {
                int blockLen=len;
                if(blockLen>BufferManager.BUFFER_SIZE) blockLen=BufferManager.BUFFER_SIZE;
                out.write(AOServProtocol.NEXT);
                out.writeShort(blockLen);
                out.write(buff, off, blockLen);
                off+=blockLen;
                len-=blockLen;
            }
        } catch(IOException err) {
            commitOnClose=false;
            throw err;
        }
    }
    
    synchronized public void write(int b) throws IOException {
        try {
            out.write(AOServProtocol.NEXT);
            out.writeShort(1);
            out.write(b);
        } catch(IOException err) {
            commitOnClose=false;
            throw err;
        }
    }
}
