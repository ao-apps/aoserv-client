/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2017  AO Industries, Inc.
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
package com.aoindustries.aoserv.client;

import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Each {@link HttpdSiteBind} may have redirect configurations attached to it.
 *
 * @see  HttpdSiteBind
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdSiteBindRedirect extends CachedObjectIntegerKey<HttpdSiteBindRedirect> {

	static final int
		COLUMN_PKEY = 0,
		COLUMN_HTTPD_SITE_BIND = 1
	;
	static final String COLUMN_HTTPD_SITE_BIND_name = "httpd_site_bind";
	static final String COLUMN_SORT_ORDER_name = "sort_order";

	int httpd_site_bind;
	private short sortOrder;
	private String pattern;
	private String substitution;
	private String comment;

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_HTTPD_SITE_BIND: return httpd_site_bind;
			case 2: return sortOrder;
			case 3: return pattern;
			case 4: return substitution;
			case 5: return comment;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	public HttpdSiteBind getHttpdSiteBind() throws SQLException, IOException {
		HttpdSiteBind obj = table.connector.getHttpdSiteBinds().get(httpd_site_bind);
		if(obj == null) throw new SQLException("Unable to find HttpdSiteBind: " + httpd_site_bind);
		return obj;
	}

	public short getSortOrder() {
		return sortOrder;
	}

	public String getPattern() {
		return pattern;
	}

	public String getSubstitution() {
		return substitution;
	}

	public String getComment() {
		return comment;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.HTTPD_SITE_BIND_REDIRECTS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey = result.getInt(1);
		httpd_site_bind = result.getInt(2);
		sortOrder = result.getShort(3);
		pattern = result.getString(4);
		substitution = result.getString(5);
		comment = result.getString(6);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey = in.readCompressedInt();
		httpd_site_bind = in.readCompressedInt();
		sortOrder = in.readShort();
		pattern = in.readUTF();
		substitution = in.readUTF();
		comment = in.readNullUTF();
	}

	@Override
	String toStringImpl() {
		return pattern + " -> " + substitution;
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(httpd_site_bind);
		out.writeShort(sortOrder);
		out.writeUTF(pattern);
		out.writeUTF(substitution);
		out.writeNullUTF(comment);
	}
}
