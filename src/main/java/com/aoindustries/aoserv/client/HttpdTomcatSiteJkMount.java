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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Each {@link HttpdTomcatSite} has independently configured <a href="https://tomcat.apache.org/connectors-doc/reference/apache.html">JkMount and JkUnMount directives</a>.
 *
 * @see  HttpdTomcatSite
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdTomcatSiteJkMount extends CachedObjectIntegerKey<HttpdTomcatSiteJkMount> implements Removable {

	static final int
		COLUMN_PKEY = 0,
		COLUMN_HTTPD_TOMCAT_SITE = 1
	;
	static final String COLUMN_HTTPD_TOMCAT_SITE_name = "httpd_tomcat_site";
	static final String COLUMN_SORT_ORDER_name = "sort_order";

	int httpd_tomcat_site;
	private short sortOrder;
	private String path;
	private String comment;
	private boolean mount;

	@Override
	public List<CannotRemoveReason<?>> getCannotRemoveReasons() {
		return Collections.emptyList();
	}

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_HTTPD_TOMCAT_SITE: return httpd_tomcat_site;
			case 2: return sortOrder;
			case 3: return path;
			case 4: return comment;
			case 5: return mount;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	public HttpdTomcatSite getHttpdTomcatSite() throws SQLException, IOException {
		HttpdTomcatSite obj = table.connector.getHttpdTomcatSites().get(httpd_tomcat_site);
		if(obj == null) throw new SQLException("Unable to find HttpdTomcatSite: " + httpd_tomcat_site);
		return obj;
	}

	public short getSortOrder() {
		return sortOrder;
	}

	public String getPath() {
		return path;
	}

	public String getComment() {
		return comment;
	}

	/**
	 * When {@code true} is a <code>JkMount</code> directive.
	 * When {@code false} is a <code>JkUnMount</code> directive.
	 */
	public boolean isMount() {
		return mount;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.HTTPD_TOMCAT_SITE_JK_MOUNTS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey = result.getInt(1);
		httpd_tomcat_site = result.getInt(2);
		sortOrder = result.getShort(3);
		path = result.getString(4);
		comment = result.getString(5);
		mount = result.getBoolean(6);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey = in.readCompressedInt();
		httpd_tomcat_site = in.readCompressedInt();
		sortOrder = in.readShort();
		path = in.readUTF();
		comment = in.readNullUTF();
		mount = in.readBoolean();
	}

	@Override
	String toStringImpl() {
		return (mount ? "JkMount " : "JkUnMount ") + path;
	}

	@Override
	public void remove() throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.REMOVE, SchemaTable.TableID.HTTPD_TOMCAT_SITE_JK_MOUNTS, pkey);
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(httpd_tomcat_site);
		out.writeShort(sortOrder);
		out.writeUTF(path);
		out.writeNullUTF(comment);
		out.writeBoolean(mount);
	}
}
