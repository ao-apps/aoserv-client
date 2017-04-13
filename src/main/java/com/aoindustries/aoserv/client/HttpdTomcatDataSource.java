/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2006-2009, 2016, 2017  AO Industries, Inc.
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
import java.util.Collections;
import java.util.List;

/**
 * Represents one data source within a <code>HttpdTomcatContext</code>.
 *
 * @see  HttpdTomcatContext
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdTomcatDataSource extends CachedObjectIntegerKey<HttpdTomcatDataSource> implements Removable {

	static final int
		COLUMN_PKEY=0,
		COLUMN_TOMCAT_CONTEXT=1
	;
	static final String COLUMN_TOMCAT_CONTEXT_name = "tomcat_context";
	static final String COLUMN_NAME_name = "name";

	int tomcat_context;
	String name;
	private String driverClassName;
	private String url;
	private String username;
	private String password;
	private int maxActive;
	private int maxIdle;
	private int maxWait;
	private String validationQuery;

	@Override
	public List<CannotRemoveReason<?>> getCannotRemoveReasons() {
		return Collections.emptyList();
	}

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_TOMCAT_CONTEXT: return tomcat_context;
			case 2: return name;
			case 3: return driverClassName;
			case 4: return url;
			case 5: return username;
			case 6: return password;
			case 7: return maxActive;
			case 8: return maxIdle;
			case 9: return maxWait;
			case 10: return validationQuery;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public HttpdTomcatContext getHttpdTomcatContext() throws SQLException, IOException {
		HttpdTomcatContext obj=table.connector.getHttpdTomcatContexts().get(tomcat_context);
		if(obj==null) throw new SQLException("Unable to find HttpdTomcatContext: "+tomcat_context);
		return obj;
	}

	public String getName() {
		return name;
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public String getUrl() {
		return url;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public int getMaxActive() {
		return maxActive;
	}

	public int getMaxIdle() {
		return maxIdle;
	}

	public int getMaxWait() {
		return maxWait;
	}

	public String getValidationQuery() {
		return validationQuery;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.HTTPD_TOMCAT_DATA_SOURCES;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey=result.getInt(1);
		tomcat_context=result.getInt(2);
		name=result.getString(3);
		driverClassName=result.getString(4);
		url=result.getString(5);
		username=result.getString(6);
		password=result.getString(7);
		maxActive=result.getInt(8);
		maxIdle=result.getInt(9);
		maxWait=result.getInt(10);
		validationQuery=result.getString(11);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readCompressedInt();
		tomcat_context=in.readCompressedInt();
		name=in.readUTF();
		driverClassName=in.readUTF();
		url=in.readUTF();
		username=in.readUTF().intern();
		password=in.readUTF();
		maxActive=in.readCompressedInt();
		maxIdle=in.readCompressedInt();
		maxWait=in.readCompressedInt();
		validationQuery=in.readNullUTF();
	}

	@Override
	public void remove() throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.REMOVE, SchemaTable.TableID.HTTPD_TOMCAT_DATA_SOURCES, pkey);
	}

	public void update(
		String name,
		String driverClassName,
		String url,
		String username,
		String password,
		int maxActive,
		int maxIdle,
		int maxWait,
		String validationQuery
	) throws IOException, SQLException {
		table.connector.requestUpdateIL(
			true,
			AOServProtocol.CommandID.UPDATE_HTTPD_TOMCAT_DATA_SOURCE,
			pkey,
			name,
			driverClassName,
			url,
			username,
			password,
			maxActive,
			maxIdle,
			maxWait,
			validationQuery==null ? "" : validationQuery
		);
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(tomcat_context);
		out.writeUTF(name);
		out.writeUTF(driverClassName);
		out.writeUTF(url);
		out.writeUTF(username);
		out.writeUTF(password);
		out.writeCompressedInt(maxActive);
		out.writeCompressedInt(maxIdle);
		out.writeCompressedInt(maxWait);
		out.writeNullUTF(validationQuery);
	}
}
