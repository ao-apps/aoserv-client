/*
 * Copyright 2001-2013, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.InetAddress;
import com.aoindustries.aoserv.client.validator.ValidationException;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A <code>MasterHost</code> controls which hosts a <code>MasterUser</code>
 * is allowed to connect from.  Because <code>MasterUser</code>s have more
 * control over the system, this extra security measure is taken.
 *
 * @see  MasterUser
 *
 * @author  AO Industries, Inc.
 */
final public class MasterHost extends CachedObjectIntegerKey<MasterHost> {

	static final int COLUMN_PKEY=0;
	static final String COLUMN_USERNAME_name = "username";
	static final String COLUMN_HOST_name = "host";

	private String username;
	private InetAddress host;

	@Override
	Object getColumnImpl(int i) {
		if(i==COLUMN_PKEY) return pkey;
		if(i==1) return username;
		if(i==2) return host;
		throw new IllegalArgumentException("Invalid index: "+i);
	}

	public InetAddress getHost() {
		return host;
	}

	public MasterUser getMasterUser() throws SQLException, IOException {
		MasterUser obj=table.connector.getMasterUsers().get(username);
		if(obj==null) throw new SQLException("Unable to find MasterUser: "+username);
		return obj;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.MASTER_HOSTS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			pkey=result.getInt(1);
			username=result.getString(2);
			host=InetAddress.valueOf(result.getString(3));
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			pkey=in.readCompressedInt();
			username=in.readUTF().intern();
			host=InetAddress.valueOf(in.readUTF()).intern();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeUTF(username);
		out.writeUTF(host.toString());
	}
}
