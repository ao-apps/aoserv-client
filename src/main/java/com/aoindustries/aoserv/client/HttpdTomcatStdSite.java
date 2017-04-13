/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2001-2013, 2016, 2017  AO Industries, Inc.
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
import com.aoindustries.util.IntList;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * An <code>HttpdStdTomcatSite</code> indicates that a
 * <code>HttpdTomcatSite</code> is configured in the standard layout
 * of one Tomcat instance per Java virtual machine.
 *
 * @see  HttpdTomcatSite
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdTomcatStdSite extends CachedObjectIntegerKey<HttpdTomcatStdSite> {

	static final int COLUMN_TOMCAT_SITE=0;
	static final String COLUMN_TOMCAT_SITE_name = "tomcat_site";

	/**
	 * The default setting of maxPostSize on the &lt;Connector /&gt; in server.xml.
	 * This raises the value from the Tomcat default of 2 MiB to a more real-world
	 * value, such as allowing uploads of pictures from modern digital cameras.
	 *
	 * @see  #getMaxPostSize()
	 */
	public static final int DEFAULT_MAX_POST_SIZE = 16 * 1024 * 1024; // 16 MiB

	public static final String DEFAULT_TOMCAT_VERSION_PREFIX = HttpdTomcatVersion.VERSION_8_0_PREFIX;

	int tomcat4_shutdown_port;
	private String tomcat4_shutdown_key;
	private int maxPostSize;

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_TOMCAT_SITE: return pkey;
			case 1: return tomcat4_shutdown_port==-1?null:tomcat4_shutdown_port;
			case 2: return tomcat4_shutdown_key;
			case 3: return maxPostSize==-1 ? null: maxPostSize;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public HttpdTomcatSite getHttpdTomcatSite() throws SQLException, IOException {
		HttpdTomcatSite obj=table.connector.getHttpdTomcatSites().get(pkey);
		if(obj==null) throw new SQLException("Unable to find HttpdTomcatSite: "+pkey);
		return obj;
	}

	public String getTomcat4ShutdownKey() {
		return tomcat4_shutdown_key;
	}

	/**
	 * Gets the max post size or {@code -1} of not limited.
	 */
	public int getMaxPostSize() {
		return maxPostSize;
	}

	public void setMaxPostSize(final int maxPostSize) throws IOException, SQLException {
		table.connector.requestUpdate(
			true,
			AOServProtocol.CommandID.SET_HTTPD_TOMCAT_STD_SITE_MAX_POST_SIZE,
			new AOServConnector.UpdateRequest() {
				IntList invalidateList;

				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(pkey);
					out.writeInt(maxPostSize);
				}

				@Override
				public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AOServProtocol.DONE) invalidateList=AOServConnector.readInvalidateList(in);
					else {
						AOServProtocol.checkResult(code, in);
						throw new IOException("Unexpected response code: "+code);
					}
				}

				@Override
				public void afterRelease() {
					table.connector.tablesUpdated(invalidateList);
				}
			}
		);
	}

	public NetBind getTomcat4ShutdownPort() throws IOException, SQLException {
		if(tomcat4_shutdown_port==-1) return null;
		NetBind nb=table.connector.getNetBinds().get(tomcat4_shutdown_port);
		if(nb==null) throw new SQLException("Unable to find NetBind: "+tomcat4_shutdown_port);
		return nb;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.HTTPD_TOMCAT_STD_SITES;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey=result.getInt(1);
		tomcat4_shutdown_port=result.getInt(2);
		if(result.wasNull()) tomcat4_shutdown_port=-1;
		tomcat4_shutdown_key=result.getString(3);
		maxPostSize = result.getInt(4);
		if(result.wasNull()) maxPostSize = -1;
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readCompressedInt();
		tomcat4_shutdown_port=in.readCompressedInt();
		tomcat4_shutdown_key=in.readNullUTF();
		maxPostSize = in.readInt();
	}

	@Override
	String toStringImpl() throws SQLException, IOException {
		return getHttpdTomcatSite().toStringImpl();
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(tomcat4_shutdown_port);
		out.writeNullUTF(tomcat4_shutdown_key);
		if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_80_1_SNAPSHOT) >= 0) {
			out.writeInt(maxPostSize);
		}
	}
}
