/*
 * Copyright 2001-2013, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Several network resources on a <code>Server</code> require a unique
 * port.  All of the possible network ports are represented by
 * <code>NetPort</code>s.
 *
 * @author  AO Industries, Inc.
 */
final public class NetPort extends AOServObject<Integer,NetPort> {

	int port;

	NetPort(int port) {
		this.port=port;
	}

	@Override
	boolean equalsImpl(Object O) {
		return
			O instanceof NetPort
			&& ((NetPort)O).port==port
		;
	}

	@Override
	Object getColumnImpl(int i) {
		if(i==0) return port;
		if(i==1) return port>=1024;
		throw new IllegalArgumentException("Invalid index: "+i);
	}

	public int getPort() {
		return port;
	}

	@Override
	public Integer getKey() {
		return port;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.NET_PORTS;
	}

	@Override
	int hashCodeImpl() {
		return port;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		throw new SQLException("Should not be read from the database, should be generated.");
	}

	public boolean isUser() {
		return port>=1024;
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		throw new IOException("Should not be read from a stream, should be generated.");
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		throw new IOException("Should not be written to a stream, should be generated.");
	}
}
