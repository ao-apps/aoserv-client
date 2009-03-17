package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import java.io.*;
import java.sql.*;

/**
 * <code>InboxAttributes</code> stores all the details of a mail inbox.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class InboxAttributes implements Streamable {

    final private AOServConnector connector;
    final private int linuxServerAccount;
    private long systemTime;
    private long fileSize;
    private long lastModified;

    public InboxAttributes(AOServConnector connector, LinuxServerAccount lsa) {
        this.connector=connector;
        this.linuxServerAccount=lsa.getPkey();
    }

    public InboxAttributes(
        long fileSize,
        long lastModified
    ) {
        this.connector=null;
        this.linuxServerAccount=-1;
        this.systemTime=System.currentTimeMillis();
        this.fileSize=fileSize;
        this.lastModified=lastModified;
    }

    public AOServConnector getAOServConnector() {
        return connector;
    }

    public LinuxServerAccount getLinuxServerAccount() throws IOException, SQLException {
        return connector.linuxServerAccounts.get(linuxServerAccount);
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

    /**
     * @deprecated  This is maintained only for compatibility with the <code>Streamable</code> interface.
     * 
     * @see  #write(CompressedDataOutputStream,AOServProtocol.Version)
     */
    final public void write(CompressedDataOutputStream out, String protocolVersion) throws IOException {
        write(out, AOServProtocol.Version.getVersion(protocolVersion));
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version protocolVersion) throws IOException {
        out.writeLong(systemTime);
        out.writeLong(fileSize);
        out.writeLong(lastModified);
    }
}