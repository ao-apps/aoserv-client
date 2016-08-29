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
 * An <code>HttpdStaticSite</code> indicates that an <code>HttpdSite</code>
 * serves static content only.
 *
 * @see  HttpdSite
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdStaticSite extends CachedObjectIntegerKey<HttpdStaticSite> {

	static final int COLUMN_HTTPD_SITE=0;
	static final String COLUMN_HTTPD_SITE_name = "httpd_site";

	@Override
	Object getColumnImpl(int i) {
		if(i==COLUMN_HTTPD_SITE) return pkey;
		throw new IllegalArgumentException("Invalid index: "+i);
	}

	public HttpdSite getHttpdSite() throws SQLException, IOException {
		HttpdSite obj=table.connector.getHttpdSites().get(pkey);
		if(obj==null) throw new SQLException("Unable to find HttpdSite: "+pkey);
		return obj;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.HTTPD_STATIC_SITES;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey=result.getInt(1);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readCompressedInt();
	}

	@Override
	String toStringImpl() throws SQLException, IOException {
		return getHttpdSite().toStringImpl();
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeCompressedInt(pkey);
	}
}
