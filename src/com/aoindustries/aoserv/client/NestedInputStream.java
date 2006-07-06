package com.aoindustries.aoserv.client;

/*
 * Copyright 2002-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;

/**
 * A <code>NestedInputStream</code> reads data from
 * within a <code>CompressedDataInputStream</code> as if it were
 * a separate stream.  The underlying <code>CompressedDataInputStream</code>
 * is never closed, allowing it to be used for multiple
 * nested streams.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
public final class NestedInputStream extends InputStream {

    final private CompressedDataInputStream in;
    private boolean isDone=false;
    private byte[] buffer=BufferManager.getBytes();
    private int bufferFilled=0;
    private int bufferRead=0;

    public NestedInputStream(CompressedDataInputStream in) {
        this.in=in;
    }

    synchronized public int available() {
        return bufferRead-bufferFilled;
    }

    private void loadNextBlock() throws IOException {
        // Load the next block, if needed
        while(!isDone && bufferRead>=bufferFilled) {
            int code=in.read();
            if(code==AOServProtocol.NEXT) {
                bufferFilled=in.readShort();
                in.readFully(buffer, 0, bufferFilled);
                bufferRead=0;
            } else {
                isDone=true;
                bufferFilled=bufferRead=0;
                try {
                    AOServProtocol.checkResult(code, in);
                } catch(SQLException err) {
                    throw new IOException(err.toString());
                }
            }
        }
    }

    synchronized public void close() throws IOException {
        if(!isDone) {
            // Read the rest of the underlying stream
            int code;
            while((code=in.read())==AOServProtocol.NEXT) {
                int len=in.readShort();
                while(len>0) {
                    int skipped=(int)in.skip(len);
                    len-=skipped;
                }
            }
            isDone=true;
            bufferFilled=bufferRead=0;
            if(buffer!=null) {
                BufferManager.release(buffer);
                buffer=null;
            }
            try {
                AOServProtocol.checkResult(code, in);
            } catch(SQLException err) {
                throw new IOException(err.toString());
            }
        }
    }
    
    synchronized public int read() throws IOException {
        if(isDone) return -1;
        loadNextBlock();
        if(isDone) return -1;
        return ((int)buffer[bufferRead++])&0xff;
    }
    
    synchronized public int read(byte[] b, int off, int len) throws IOException {
        if(isDone) return -1;
        loadNextBlock();
        if(isDone) return -1;
        int bufferLeft=bufferFilled-bufferRead;
        if(bufferLeft>len) bufferLeft=len;
        System.arraycopy(buffer, bufferRead, b, off, bufferLeft);
        bufferRead+=bufferLeft;
        return bufferLeft;
    }

    synchronized public long skip(long n) throws IOException {
        if(isDone) return -1;
        loadNextBlock();
        if(isDone) return -1;
        int bufferLeft=bufferFilled-bufferRead;
        if(bufferLeft>n) bufferLeft=(int)n;
        bufferRead+=bufferLeft;
        return bufferLeft;
    }
    
    public void finalize() throws Throwable {
        if(buffer!=null) {
            BufferManager.release(buffer);
            buffer=null;
        }
        super.finalize();
    }
}