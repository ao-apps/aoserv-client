package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.sql.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;

/**
 * Connections made by <code>TCPConnector</code> or any
 * of its derivatives are pooled and reused.
 *
 * @see  TCPConnector
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class SocketConnectionPool extends AOPool {

    public static final int DELAY_TIME=3*60*1000;
    public static final int MAX_IDLE_TIME=15*60*1000;

    private final TCPConnector connector;

    SocketConnectionPool(TCPConnector connector, ErrorHandler errorHandler) {
	super(DELAY_TIME, MAX_IDLE_TIME, SocketConnectionPool.class.getName()+"?hostname=" + connector.hostname+"&port="+connector.port+"&connectAs="+connector.connectAs+"&authenticateAs="+connector.authenticateAs, connector.poolSize, connector.maxConnectionAge, errorHandler);
	this.connector=connector;
    }

    void close() throws IOException {
	try {
            closeImp();
	} catch(Exception err) {
            if(err instanceof IOException) throw (IOException)err;
            IOException ioErr=new IOException();
            ioErr.initCause(err);
            throw ioErr;
	}
    }

    protected void close(Object O) {
	((SocketConnection)O).close();
    }

    SocketConnection getConnection() throws IOException {
        return getConnection(1);
    }

    SocketConnection getConnection(int maxConnections) throws IOException {
	try {
            return (SocketConnection)getConnectionImp(maxConnections);
	} catch(Exception err) {
            if(err instanceof IOException) throw (IOException)err;
            IOException ioErr=new IOException();
            ioErr.initCause(err);
            throw ioErr;
	}
    }

    protected Object getConnectionObject() throws IOException {
	return new SocketConnection(connector);
    }

    protected boolean isClosed(Object O) {
	return ((SocketConnection)O).isClosed();
    }

    /**
     * Avoid repeated copies.
     */
    private static final int numTables = SchemaTable.TableID.values().length;

    protected void printConnectionStats(ChainWriter out) throws IOException {
        // Create statistics on the caches
        int totalLoaded=0;
        int totalCaches=0;
        int totalActive=0;
        int totalHashed=0;
        int totalIndexed=0;
        int totalRows=0;
        for(AOServTable table : connector.tables) {
            totalLoaded++;
            if(table instanceof CachedTable) {
                totalCaches++;
                int columnCount=table.getTableSchema().getSchemaColumns(connector).size();
                CachedTable cached=(CachedTable)table;
                if(cached.isLoaded()) {
                    totalActive++;
                    for(int d=0;d<columnCount;d++) {
                        if(cached.isHashed(d)) totalHashed++;
                        if(cached.isIndexed(d)) totalIndexed++;
                    }
                    totalRows+=cached.size();
                }
            } else if(table instanceof GlobalTable) {
                totalCaches++;
                int columnCount=table.getTableSchema().getSchemaColumns(connector).size();
                GlobalTable global=(GlobalTable)table;
                if(global.isLoaded()) {
                    totalActive++;
                    for(int d=0;d<columnCount;d++) {
                        if(global.isHashed(d)) totalHashed++;
                        if(global.isIndexed(d)) totalIndexed++;
                    }
                    totalRows+=global.size();
                }
            }
        }

        // Show the table statistics
        out.print("  <TR><TH colspan=2><FONT size=+1>AOServ Tables</FONT></TH></TR>\n"
                + "  <TR><TD>Total Tables:</TD><TD>").print(numTables).print("</TD></TR>\n"
                + "  <TR><TD>Loaded:</TD><TD>").print(totalLoaded).print("</TD></TR>\n"
                + "  <TR><TD>Caches:</TD><TD>").print(totalCaches).print("</TD></TR>\n"
                + "  <TR><TD>Active:</TD><TD>").print(totalActive).print("</TD></TR>\n"
                + "  <TR><TD>Hashed:</TD><TD>").print(totalHashed).print("</TD></TR>\n"
                + "  <TR><TD>Indexes:</TD><TD>").print(totalIndexed).print("</TD></TR>\n"
                + "  <TR><TD>Total Rows:</TD><TD>").print(totalRows).print("</TD></TR>\n"
                + "</TABLE>\n"
                + "<BR><BR>\n"
                + "<TABLE>\n"
                + "  <TR><TH colspan=2><FONT size=+1>TCP Connection Pool</FONT></TH></TR>\n"
                + "  <TR><TD>Host:</TD><TD>").print(connector.hostname).print("</TD></TR>\n"
                + "  <TR><TD>Port:</TD><TD>").print(connector.port).print("</TD></TR>\n"
                + "  <TR><TD>Connected As:</TD><TD>").print(connector.connectAs).print("</TD></TR>\n"
                + "  <TR><TD>Authenticated As:</TD><TD>").print(connector.authenticateAs).print("</TD></TR>\n"
                + "  <TR><TD>Password:</TD><TD>");
        String password=connector.password;
        int len=password.length();
        for(int c=0;c<len;c++) out.print('*');
        out.print("</TD></TR>\n");
    }

    void printStatisticsHTML(ChainWriter out) throws IOException {
	try {
            printStatisticsHTMLImp(out);
	} catch(Exception err) {
            if(err instanceof IOException) throw (IOException)err;
            IOException ioErr=new IOException();
            ioErr.initCause(err);
            throw ioErr;
	}
    }

    void releaseConnection(SocketConnection connection) throws IOException {
	try {
            releaseConnectionImp(connection);
	} catch(Exception err) {
            if(err instanceof IOException) throw (IOException)err;
            IOException ioErr=new IOException();
            ioErr.initCause(err);
            throw ioErr;
	}
    }

    protected void resetConnection(Object O) {
    }

    protected void throwException(String message, Throwable allocateStackTrace) throws IOException {
	IOException err=new IOException(message);
        err.initCause(allocateStackTrace);
        throw err;
    }
}