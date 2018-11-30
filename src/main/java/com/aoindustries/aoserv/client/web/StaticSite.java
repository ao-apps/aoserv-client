/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aoserv-client.
 *
 * aoserv-client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aoserv-client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aoserv-client.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.aoserv.client.web;

import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * An <code>HttpdStaticSite</code> indicates that an <code>HttpdSite</code>
 * serves static content only.
 *
 * @see  Site
 *
 * @author  AO Industries, Inc.
 */
final public class StaticSite extends CachedObjectIntegerKey<StaticSite> {

	static final int COLUMN_HTTPD_SITE=0;
	static final String COLUMN_HTTPD_SITE_name = "httpd_site";

	@Override
	protected Object getColumnImpl(int i) {
		if(i==COLUMN_HTTPD_SITE) return pkey;
		throw new IllegalArgumentException("Invalid index: "+i);
	}

	public Site getHttpdSite() throws SQLException, IOException {
		Site obj=table.getConnector().getHttpdSites().get(pkey);
		if(obj==null) throw new SQLException("Unable to find HttpdSite: "+pkey);
		return obj;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.HTTPD_STATIC_SITES;
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
	public String toStringImpl() throws SQLException, IOException {
		return getHttpdSite().toStringImpl();
	}

	@Override
	public void write(CompressedDataOutputStream out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
	}
}
