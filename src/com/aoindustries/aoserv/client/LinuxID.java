/*
 * Copyright 2000-2013 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.io.*;
import java.io.*;
import java.sql.*;

/**
 * Several resources on a <code>Server</code> require a server-wide
 * unique identifier.  All of the possible identifiers are represented
 * by <code>LinuxID</code>s.
 *
 * @see  LinuxServerAccount
 * @see  LinuxServerGroup
 * @see  PostgresServerUser
 *
 * @author  AO Industries, Inc.
 */
final public class LinuxID extends AOServObject<Integer,LinuxID> {

    int id;

    LinuxID(int id) {
        this.id=id;
    }

    boolean equalsImpl(Object O) {
	return
            O instanceof LinuxID
            && ((LinuxID)O).id==id
	;
    }

    Object getColumnImpl(int i) {
	if(i==0) return Integer.valueOf(id);
	if(i==1) return isSystem()?Boolean.TRUE:Boolean.FALSE;
	throw new IllegalArgumentException("Invalid index: "+i);
    }

    public int getID() {
	return id;
    }

    public Integer getKey() {
	return id;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.LINUX_IDS;
    }

    int hashCodeImpl() {
	return id;
    }

    public void init(ResultSet result) throws SQLException {
	throw new SQLException("Should not be read from the database, should be generated.");
    }

    public boolean isSystem() {
        return id<500 || id==65534 || id==65535;
    }

    public void read(CompressedDataInputStream in) throws IOException {
	throw new IOException("Should not be read from a stream, should be generated.");
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
	throw new IOException("Should not be written to a stream, should be generated.");
    }
}