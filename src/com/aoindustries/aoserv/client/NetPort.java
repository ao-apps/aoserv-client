package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;

/**
 * Several network resources on a <code>Server</code> require a unique
 * port.  All of the possible network ports are represented by
 * <code>NetPort</code>s.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class NetPort extends AOServObject<Integer,NetPort> {

    int port;

    NetPort(int port) {
        this.port=port;
    }

    boolean equalsImpl(Object O) {
	return
            O instanceof NetPort
            && ((NetPort)O).port==port
	;
    }

    public Object getColumn(int i) {
	if(i==0) return Integer.valueOf(port);
	if(i==1) return port>=1024 ? Boolean.TRUE : Boolean.FALSE;
	throw new IllegalArgumentException("Invalid index: "+i);
    }

    public int getPort() {
	return port;
    }

    public Integer getKey() {
	return port;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.NET_PORTS;
    }

    int hashCodeImpl() {
	return port;
    }

    void initImpl(ResultSet result) throws SQLException {
	throw new SQLException("Should not be read from the database, should be generated.");
    }

    public boolean isUser() {
	return port>=1024;
    }

    public void read(CompressedDataInputStream in) throws IOException {
	throw new IOException("Should not be read from a stream, should be generated.");
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	throw new IOException("Should not be written to a stream, should be generated.");
    }
}