/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2002-2012, 2016, 2017, 2018, 2019, 2020, 2021  AO Industries, Inc.
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

import com.aoapps.collections.IntList;
import com.aoapps.hodgepodge.io.TerminalWriter;
import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.linux.PosixPath;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/**
 * @see  Context
 *
 * @author  AO Industries, Inc.
 */
public final class ContextTable extends CachedTableIntegerKey<Context> {

	ContextTable(AOServConnector connector) {
		super(connector, Context.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(Context.COLUMN_TOMCAT_SITE_name+'.'+Site.COLUMN_HTTPD_SITE_name+'.'+com.aoindustries.aoserv.client.web.Site.COLUMN_NAME_name, ASCENDING),
		new OrderBy(Context.COLUMN_TOMCAT_SITE_name+'.'+Site.COLUMN_HTTPD_SITE_name+'.'+com.aoindustries.aoserv.client.web.Site.COLUMN_AO_SERVER_name+'.'+Server.COLUMN_HOSTNAME_name, ASCENDING),
		new OrderBy(Context.COLUMN_PATH_name, ASCENDING)
	};
	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	int addHttpdTomcatContext(
		final Site hts,
		final String className,
		final boolean cookies,
		final boolean crossContext,
		final PosixPath docBase,
		final boolean override,
		final String path,
		final boolean privileged,
		final boolean reloadable,
		final boolean useNaming,
		final String wrapperClass,
		final int debug,
		final PosixPath workDir,
		final boolean serverXmlConfigured
	) throws IOException, SQLException {
		return connector.requestResult(
			true,
			AoservProtocol.CommandID.ADD,
			new AOServConnector.ResultRequest<Integer>() {
				private int pkey;
				private IntList invalidateList;

				@Override
				public void writeRequest(StreamableOutput out) throws IOException {
					out.writeCompressedInt(Table.TableID.HTTPD_TOMCAT_CONTEXTS.ordinal());
					out.writeCompressedInt(hts.getPkey());
					out.writeNullUTF(className);
					out.writeBoolean(cookies);
					out.writeBoolean(crossContext);
					out.writeUTF(docBase.toString());
					out.writeBoolean(override);
					out.writeUTF(path);
					out.writeBoolean(privileged);
					out.writeBoolean(reloadable);
					out.writeBoolean(useNaming);
					out.writeNullUTF(wrapperClass);
					out.writeCompressedInt(debug);
					out.writeNullUTF(Objects.toString(workDir, null));
					out.writeBoolean(serverXmlConfigured);
				}

				@Override
				public void readResponse(StreamableInput in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AoservProtocol.DONE) {
						pkey=in.readCompressedInt();
						invalidateList=AOServConnector.readInvalidateList(in);
					} else {
						AoservProtocol.checkResult(code, in);
						throw new IOException("Unexpected response code: "+code);
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
	public Context get(int pkey) throws IOException, SQLException {
		return getUniqueRow(Context.COLUMN_PKEY, pkey);
	}

	Context getHttpdTomcatContext(Site hts, String path) throws IOException, SQLException {
		int hts_pkey=hts.getPkey();
		List<Context> cached=getRows();
		int size=cached.size();
		for(int c=0;c<size;c++) {
			Context htc=cached.get(c);
			if(htc.getHttpdTomcatSite_httpdSite_id()==hts_pkey && htc.getPath().equals(path)) return htc;
		}
		return null;
	}

	List<Context> getHttpdTomcatContexts(Site hts) throws IOException, SQLException {
		return getIndexedRows(Context.COLUMN_TOMCAT_SITE, hts.getPkey());
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.HTTPD_TOMCAT_CONTEXTS;
	}

	@Override
	public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(Command.ADD_HTTPD_TOMCAT_CONTEXT)) {
			if(AOSH.checkParamCount(Command.ADD_HTTPD_TOMCAT_CONTEXT, args, 15, err)) {
				out.println(
					connector.getSimpleAOClient().addHttpdTomcatContext(
						args[1],
						args[2],
						args[3],
						AOSH.parseBoolean(args[4], "use_cookies"),
						AOSH.parseBoolean(args[5], "cross_context"),
						AOSH.parseUnixPath(args[6], "doc_base"),
						AOSH.parseBoolean(args[7], "allow_override"),
						args[8],
						AOSH.parseBoolean(args[9], "is_privileged"),
						AOSH.parseBoolean(args[10], "is_reloadable"),
						AOSH.parseBoolean(args[11], "use_naming"),
						args[12],
						AOSH.parseInt(args[13], "debug_level"),
						args[14].isEmpty() ? null : AOSH.parseUnixPath(args[14], "work_dir"),
						AOSH.parseBoolean(args[15], "server_xml_configured")
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.REMOVE_HTTPD_TOMCAT_CONTEXT)) {
			if(AOSH.checkParamCount(Command.REMOVE_HTTPD_TOMCAT_CONTEXT, args, 1, err)) {
				connector.getSimpleAOClient().removeHttpdTomcatContext(AOSH.parseInt(args[1], "pkey"));
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.SET_HTTPD_TOMCAT_CONTEXT_ATTRIBUTES)) {
			if(AOSH.checkParamCount(Command.SET_HTTPD_TOMCAT_CONTEXT_ATTRIBUTES, args, 16, err)) {
				connector.getSimpleAOClient().setHttpdTomcatContextAttributes(
					args[1],
					args[2],
					args[3],
					args[4],
					AOSH.parseBoolean(args[5], "use_cookies"),
					AOSH.parseBoolean(args[6], "cross_context"),
					AOSH.parseUnixPath(args[7], "doc_base"),
					AOSH.parseBoolean(args[8], "allow_override"),
					args[9],
					AOSH.parseBoolean(args[10], "is_privileged"),
					AOSH.parseBoolean(args[11], "is_reloadable"),
					AOSH.parseBoolean(args[12], "use_naming"),
					args[13],
					AOSH.parseInt(args[14], "debug_level"),
					args[15].isEmpty() ? null : AOSH.parseUnixPath(args[15], "work_dir"),
					AOSH.parseBoolean(args[16], "server_xml_configured")
				);
			}
			return true;
		} else return false;
	}
}
