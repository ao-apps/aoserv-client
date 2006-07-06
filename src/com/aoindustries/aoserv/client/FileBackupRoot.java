package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.sql.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;

/**
 * <code>FileBackupRoot</code>s keep track of the starting points for browsing
 * <code>FileBackup</code>s on a per-package and per-server basis.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class FileBackupRoot extends CachedObjectIntegerKey<FileBackupRoot> {

    static final int
        COLUMN_PKEY=0,
        COLUMN_SERVER=2
    ;

    private String path;
    int server;
    private int packageNum;

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case 1: return path;
            case COLUMN_SERVER: return Integer.valueOf(server);
            case 3: return Integer.valueOf(packageNum);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public String getPath() {
        return path;
    }

    public Server getServer() {
        Server se=table.connector.servers.get(server);
        if(se==null) throw new WrappedException(new SQLException("Unable to find Server: "+server));
        return se;
    }

    public Package getPackage() {
        Package pk=table.connector.packages.get(packageNum);
        if(pk==null) throw new WrappedException(new SQLException("Unable to find Package: "+packageNum));
        return pk;
    }

    protected int getTableIDImpl() {
	return SchemaTable.FILE_BACKUP_ROOTS;
    }

    void initImpl(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
        path=result.getString(2);
        server=result.getInt(3);
        packageNum=result.getInt(4);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        path=in.readUTF();
        server=in.readCompressedInt();
        packageNum=in.readCompressedInt();
    }

    String toStringImpl() {
        return getServer().getHostname()+":"+path;
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeUTF(path);
	out.writeCompressedInt(server);
	out.writeCompressedInt(packageNum);
    }
}