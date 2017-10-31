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
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.util.IntList;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  HttpdTomcatSiteJkMount
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdTomcatSiteJkMountTable extends CachedTableIntegerKey<HttpdTomcatSiteJkMount> {

	HttpdTomcatSiteJkMountTable(AOServConnector connector) {
		super(connector, HttpdTomcatSiteJkMount.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(HttpdTomcatSiteJkMount.COLUMN_HTTPD_TOMCAT_SITE_name + '.' + HttpdTomcatSite.COLUMN_HTTPD_SITE_name + '.' + HttpdSite.COLUMN_SITE_NAME_name, ASCENDING),
		new OrderBy(HttpdTomcatSiteJkMount.COLUMN_HTTPD_TOMCAT_SITE_name + '.' + HttpdTomcatSite.COLUMN_HTTPD_SITE_name + '.' + HttpdSite.COLUMN_AO_SERVER_name + '.' + AOServer.COLUMN_HOSTNAME_name, ASCENDING),
		new OrderBy(HttpdTomcatSiteJkMount.COLUMN_MOUNT_name, DESCENDING), // JkMount before JkUnMount
		new OrderBy(HttpdTomcatSiteJkMount.COLUMN_PATH_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	int addHttpdTomcatSiteJkMount(
		final HttpdTomcatSite hts,
		final String path,
		final boolean mount
	) throws IOException, SQLException {
		if(!HttpdTomcatSiteJkMount.isValidPath(path)) throw new IllegalArgumentException("Invalid path: " + path);
		return connector.requestResult(
			true,
			AOServProtocol.CommandID.ADD,
			new AOServConnector.ResultRequest<Integer>() {
				int pkey;
				IntList invalidateList;

				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(SchemaTable.TableID.HTTPD_TOMCAT_SITE_JK_MOUNTS.ordinal());
					out.writeCompressedInt(hts.pkey);
					out.writeUTF(path);
					out.writeBoolean(mount);
				}

				@Override
				public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
					int code = in.readByte();
					if(code == AOServProtocol.DONE) {
						pkey = in.readCompressedInt();
						invalidateList = AOServConnector.readInvalidateList(in);
					} else {
						AOServProtocol.checkResult(code, in);
						throw new IOException("Unexpected response code: " + code);
					}
				}

				@Override
				public Integer afterRelease() {
					connector.tablesUpdated(invalidateList);
					return pkey;
				}
			}
		);
	}

	@Override
	public HttpdTomcatSiteJkMount get(int pkey) throws IOException, SQLException {
		return getUniqueRow(HttpdTomcatSiteJkMount.COLUMN_PKEY, pkey);
	}

	List<HttpdTomcatSiteJkMount> getHttpdTomcatSiteJkMounts(HttpdTomcatSite tomcatSite) throws IOException, SQLException {
		return getIndexedRows(HttpdTomcatSiteJkMount.COLUMN_HTTPD_TOMCAT_SITE, tomcatSite.pkey);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.HTTPD_TOMCAT_SITE_JK_MOUNTS;
	}

	@Override
	boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command = args[0];
		if(command.equalsIgnoreCase(AOSHCommand.ADD_HTTPD_TOMCAT_SITE_JK_MOUNT)) {
			if(AOSH.checkParamCount(AOSHCommand.ADD_HTTPD_TOMCAT_SITE_JK_MOUNT, args, 4, err)) {
				out.println(
					connector.getSimpleAOClient().addHttpdTomcatSiteJkMount(
						args[1],
						args[2],
						args[3],
						AOSH.parseBoolean(args[4], "mount")
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_HTTPD_TOMCAT_SITE_JK_MOUNT)) {
			if(AOSH.checkParamCount(AOSHCommand.REMOVE_HTTPD_TOMCAT_SITE_JK_MOUNT, args, 3, err)) {
				connector.getSimpleAOClient().removeHttpdTomcatSiteJkMount(
					args[1],
					args[2],
					args[3]
				);
			}
			return true;
		} else return false;
	}
}
