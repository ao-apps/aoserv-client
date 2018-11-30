/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2017, 2018  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.web.tomcat;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.util.IntList;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  JkMount
 *
 * @author  AO Industries, Inc.
 */
final public class JkMountTable extends CachedTableIntegerKey<JkMount> {

	public JkMountTable(AOServConnector connector) {
		super(connector, JkMount.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(JkMount.COLUMN_HTTPD_TOMCAT_SITE_name + '.' + Site.COLUMN_HTTPD_SITE_name + '.' + com.aoindustries.aoserv.client.web.Site.COLUMN_NAME_name, ASCENDING),
		new OrderBy(JkMount.COLUMN_HTTPD_TOMCAT_SITE_name + '.' + Site.COLUMN_HTTPD_SITE_name + '.' + com.aoindustries.aoserv.client.web.Site.COLUMN_AO_SERVER_name + '.' + Server.COLUMN_HOSTNAME_name, ASCENDING),
		new OrderBy(JkMount.COLUMN_MOUNT_name, DESCENDING), // JkMount before JkUnMount
		new OrderBy(JkMount.COLUMN_PATH_name, ASCENDING)
	};
	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	int addHttpdTomcatSiteJkMount(
		final Site hts,
		final String path,
		final boolean mount
	) throws IOException, SQLException {
		if(!JkMount.isValidPath(path)) throw new IllegalArgumentException("Invalid path: " + path);
		return connector.requestResult(true,
			AoservProtocol.CommandID.ADD,
			new AOServConnector.ResultRequest<Integer>() {
				int pkey;
				IntList invalidateList;

				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(Table.TableID.HTTPD_TOMCAT_SITE_JK_MOUNTS.ordinal());
					out.writeCompressedInt(hts.getPkey());
					out.writeUTF(path);
					out.writeBoolean(mount);
				}

				@Override
				public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
					int code = in.readByte();
					if(code == AoservProtocol.DONE) {
						pkey = in.readCompressedInt();
						invalidateList = AOServConnector.readInvalidateList(in);
					} else {
						AoservProtocol.checkResult(code, in);
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
	public JkMount get(int pkey) throws IOException, SQLException {
		return getUniqueRow(JkMount.COLUMN_PKEY, pkey);
	}

	List<JkMount> getHttpdTomcatSiteJkMounts(Site tomcatSite) throws IOException, SQLException {
		return getIndexedRows(JkMount.COLUMN_HTTPD_TOMCAT_SITE, tomcatSite.getPkey());
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.HTTPD_TOMCAT_SITE_JK_MOUNTS;
	}

	@Override
	public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command = args[0];
		if(command.equalsIgnoreCase(Command.ADD_HTTPD_TOMCAT_SITE_JK_MOUNT)) {
			if(AOSH.checkParamCount(Command.ADD_HTTPD_TOMCAT_SITE_JK_MOUNT, args, 4, err)) {
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
		} else if(command.equalsIgnoreCase(Command.REMOVE_HTTPD_TOMCAT_SITE_JK_MOUNT)) {
			if(AOSH.checkParamCount(Command.REMOVE_HTTPD_TOMCAT_SITE_JK_MOUNT, args, 3, err)) {
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
