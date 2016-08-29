/*
 * Copyright 2000-2009, 2016 by AO Industries, Inc.,
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
 * Each <code>AOServer</code> has several entries in <code>/etc/aliases</code>
 * that do not belong to any particular <code>EmailDomain</code> or
 * <code>Package</code>.  These are a standard part of the email
 * configuration and are contained in <code>SystemEmailAlias</code>es.
 *
 * @author  AO Industries, Inc.
 */
final public class SystemEmailAlias extends CachedObjectIntegerKey<SystemEmailAlias> {

	static final int
		COLUMN_PKEY=0,
		COLUMN_AO_SERVER=1
	;
	static final String COLUMN_AO_SERVER_name = "ao_server";
	static final String COLUMN_ADDRESS_name = "address";

	int ao_server;
	private String address;
	private String destination;

	public String getAddress() {
		return address;
	}

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_AO_SERVER: return ao_server;
			case 2: return address;
			case 3: return destination;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public String getDestination() {
		return destination;
	}

	public AOServer getAOServer() throws SQLException, IOException {
		AOServer ao=table.connector.getAoServers().get(ao_server);
		if(ao==null) throw new SQLException("Unable to find AOServer: "+ao_server);
		return ao;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.SYSTEM_EMAIL_ALIASES;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey = result.getInt(1);
		ao_server = result.getInt(2);
		address = result.getString(3);
		destination = result.getString(4);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readCompressedInt();
		ao_server=in.readCompressedInt();
		address=in.readUTF().intern();
		destination=in.readUTF().intern();
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(ao_server);
		out.writeUTF(address);
		out.writeUTF(destination);
	}
}
