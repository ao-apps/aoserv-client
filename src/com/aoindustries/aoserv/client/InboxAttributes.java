package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
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
        this.linuxServerAccount=lsa.getPKey();
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

    public LinuxServerAccount getLinuxServerAccount() {
        return connector.linuxServerAccounts.get(linuxServerAccount);
    }
    
    public long getSystemTime() {
        return systemTime;
    }
    
    public long getFileSize() {
        return fileSize;
    }
    
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