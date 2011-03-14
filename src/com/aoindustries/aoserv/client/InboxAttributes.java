package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.io.Streamable;
import java.io.IOException;
import java.io.Serializable;

/**
 * <code>InboxAttributes</code> stores all the details of a mail inbox.
 *
 * @author  AO Industries, Inc.
 */
final public class InboxAttributes implements Streamable, Serializable {

    // TODO: private static final long serialVersionUID = 1L;

    private long systemTime;
    private long fileSize;
    private long lastModified;

    public InboxAttributes(
        long fileSize,
        long lastModified
    ) {
        this.systemTime=System.currentTimeMillis();
        this.fileSize=fileSize;
        this.lastModified=lastModified;
    }

    public long getSystemTime() {
        return systemTime;
    }
    
    public long getFileSize() {
        return fileSize;
    }
    
    /**
     * Gets the last modified time or <code>0L</code> if unknown.
     */
    public long getLastModified() {
        return lastModified;
    }

    public void read(CompressedDataInputStream in) throws IOException {
        systemTime=in.readLong();
        fileSize=in.readLong();
        lastModified=in.readLong();
    }

    public void write(CompressedDataOutputStream out, String protocolVersion) throws IOException {
        out.writeLong(systemTime);
        out.writeLong(fileSize);
        out.writeLong(lastModified);
    }
}