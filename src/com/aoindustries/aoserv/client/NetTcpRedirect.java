/*
 * Copyright 2004-2013 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.HostAddress;
import com.aoindustries.aoserv.client.validator.ValidationException;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Each server may perform TCP redirects via xinetd.
 *
 * @author  AO Industries, Inc.
 */
public final class NetTcpRedirect extends CachedObjectIntegerKey<NetTcpRedirect> {

	static final int COLUMN_NET_BIND=0;
	static final String COLUMN_NET_BIND_name = "net_bind";

	private int cps;
	private int cps_overload_sleep_time;
	private HostAddress destination_host;
	private int destination_port;

	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_NET_BIND: return Integer.valueOf(pkey);
			case 1: return Integer.valueOf(cps);
			case 2: return Integer.valueOf(cps_overload_sleep_time);
			case 3: return destination_host;
			case 4: return Integer.valueOf(destination_port);
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public NetBind getNetBind() throws IOException, SQLException {
		NetBind nb=table.connector.getNetBinds().get(pkey);
		if(nb==null) throw new SQLException("Unable to find NetBind: "+pkey);
		return nb;
	}

	public int getConnectionsPerSecond() {
		return cps;
	}

	public int getConnectionsPerSecondOverloadSleepTime() {
		return cps_overload_sleep_time;
	}

	public HostAddress getDestinationHost() {
		return destination_host;
	}

	public NetPort getDestinationPort() throws SQLException {
		NetPort np=table.connector.getNetPorts().get(destination_port);
		if(np==null) throw new SQLException("Unable to find NetPort: "+destination_port);
		return np;
	}

	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.NET_TCP_REDIRECTS;
	}

	public void init(ResultSet result) throws SQLException {
		try {
			pkey=result.getInt(1);
			cps=result.getInt(2);
			cps_overload_sleep_time=result.getInt(3);
			destination_host=HostAddress.valueOf(result.getString(4));
			destination_port=result.getInt(5);
		} catch(ValidationException e) {
			SQLException exc = new SQLException(e.getLocalizedMessage());
			exc.initCause(e);
			throw exc;
		}
	}

	public void read(CompressedDataInputStream in) throws IOException {
		try {
			pkey=in.readCompressedInt();
			cps=in.readCompressedInt();
			cps_overload_sleep_time=in.readCompressedInt();
			destination_host=HostAddress.valueOf(in.readUTF()).intern();
			destination_port=in.readCompressedInt();
		} catch(ValidationException e) {
			IOException exc = new IOException(e.getLocalizedMessage());
			exc.initCause(e);
			throw exc;
		}
	}

	@Override
	String toStringImpl() throws SQLException, IOException {
		return getNetBind().toStringImpl()+"->"+destination_host.toBracketedString()+':'+destination_port;
	}

	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(cps);
		out.writeCompressedInt(cps_overload_sleep_time);
		out.writeUTF(destination_host.toString());
		out.writeCompressedInt(destination_port);
	}
}