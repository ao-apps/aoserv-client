/*
 * Copyright 2000-2013, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Multiple versions of Majordomo are supported by the system.
 * Each <code>MajordomoServer</code> is of a specific version,
 * and all its <code>MajordomoList</code>s inherit that
 * <code>MajordomoVersion</code>.
 *
 * @see  MajordomoServer
 *
 * @author  AO Industries, Inc.
 */
final public class MajordomoVersion extends GlobalObjectStringKey<MajordomoVersion> {

	static final int COLUMN_VERSION=0;
	static final String COLUMN_VERSION_name = "version";

	/**
	 * The default Majordomo version.
	 */
	public static final String DEFAULT_VERSION="1.94.5";

	private long created;

	@Override
	Object getColumnImpl(int i) {
		if(i==COLUMN_VERSION) return pkey;
		if(i==1) return getCreated();
		throw new IllegalArgumentException("Invalid index: "+i);
	}

	public Timestamp getCreated() {
		return new Timestamp(created);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.MAJORDOMO_VERSIONS;
	}

	public String getVersion() {
		return pkey;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey=result.getString(1);
		created=result.getTimestamp(2).getTime();
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readUTF().intern();
		created=in.readLong();
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version protocolVersion) throws IOException {
		out.writeUTF(pkey);
		out.writeLong(created);
	}
}
