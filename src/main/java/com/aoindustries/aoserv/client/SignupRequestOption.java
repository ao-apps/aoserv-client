/*
 * Copyright 2007-2009, 2016 by AO Industries, Inc.,
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
 * Stores an option for a sign-up request, each option has a unique name per sign-up request.
 *
 * @author  AO Industries, Inc.
 */
final public class SignupRequestOption extends CachedObjectIntegerKey<SignupRequestOption> {

	static final int
		COLUMN_PKEY=0,
		COLUMN_REQUEST=1
	;
	static final String COLUMN_REQUEST_name = "request";
	static final String COLUMN_NAME_name = "name";

	int request;
	private String name;
	private String value;

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_REQUEST: return request;
			case 2: return name;
			case 3: return value;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.SIGNUP_REQUEST_OPTIONS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		int pos = 1;
		pkey = result.getInt(pos++);
		request = result.getInt(pos++);
		name = result.getString(pos++);
		value = result.getString(pos++);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readCompressedInt();
		request=in.readCompressedInt();
		name = in.readUTF().intern();
		value = in.readNullUTF();
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(request);
		out.writeUTF(name);
		out.writeNullUTF(value);
	}

	public SignupRequest getSignupRequest() throws SQLException, IOException {
		SignupRequest sr = table.connector.getSignupRequests().get(request);
		if (sr == null) throw new SQLException("Unable to find SignupRequest: " + request);
		return sr;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}
}
