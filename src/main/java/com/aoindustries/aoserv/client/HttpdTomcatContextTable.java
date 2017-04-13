/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2002-2012, 2016, 2017  AO Industries, Inc.
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

import com.aoindustries.aoserv.client.validator.UnixPath;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.lang.ObjectUtils;
import com.aoindustries.util.IntList;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  HttpdTomcatContext
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdTomcatContextTable extends CachedTableIntegerKey<HttpdTomcatContext> {

	HttpdTomcatContextTable(AOServConnector connector) {
		super(connector, HttpdTomcatContext.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(HttpdTomcatContext.COLUMN_TOMCAT_SITE_name+'.'+HttpdTomcatSite.COLUMN_HTTPD_SITE_name+'.'+HttpdSite.COLUMN_SITE_NAME_name, ASCENDING),
		new OrderBy(HttpdTomcatContext.COLUMN_TOMCAT_SITE_name+'.'+HttpdTomcatSite.COLUMN_HTTPD_SITE_name+'.'+HttpdSite.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING),
		new OrderBy(HttpdTomcatContext.COLUMN_PATH_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	int addHttpdTomcatContext(
		final HttpdTomcatSite hts,
		final String className,
		final boolean cookies,
		final boolean crossContext,
		final UnixPath docBase,
		final boolean override,
		final String path,
		final boolean privileged,
		final boolean reloadable,
		final boolean useNaming,
		final String wrapperClass,
		final int debug,
		final UnixPath workDir
	) throws IOException, SQLException {
		return connector.requestResult(
			true,
			AOServProtocol.CommandID.ADD,
			new AOServConnector.ResultRequest<Integer>() {
				int pkey;
				IntList invalidateList;

				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(SchemaTable.TableID.HTTPD_TOMCAT_CONTEXTS.ordinal());
					out.writeCompressedInt(hts.pkey);
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
					out.writeNullUTF(ObjectUtils.toString(workDir));
				}

				@Override
				public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AOServProtocol.DONE) {
						pkey=in.readCompressedInt();
						invalidateList=AOServConnector.readInvalidateList(in);
					} else {
						AOServProtocol.checkResult(code, in);
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
	public HttpdTomcatContext get(int pkey) throws IOException, SQLException {
		return getUniqueRow(HttpdTomcatContext.COLUMN_PKEY, pkey);
	}

	HttpdTomcatContext getHttpdTomcatContext(HttpdTomcatSite hts, String path) throws IOException, SQLException {
		int hts_pkey=hts.pkey;
		List<HttpdTomcatContext> cached=getRows();
		int size=cached.size();
		for(int c=0;c<size;c++) {
			HttpdTomcatContext htc=cached.get(c);
			if(htc.tomcat_site==hts_pkey && htc.path.equals(path)) return htc;
		}
		return null;
	}

	List<HttpdTomcatContext> getHttpdTomcatContexts(HttpdTomcatSite hts) throws IOException, SQLException {
		return getIndexedRows(HttpdTomcatContext.COLUMN_TOMCAT_SITE, hts.pkey);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.HTTPD_TOMCAT_CONTEXTS;
	}

	@Override
	boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(AOSHCommand.ADD_HTTPD_TOMCAT_CONTEXT)) {
			if(AOSH.checkParamCount(AOSHCommand.ADD_HTTPD_TOMCAT_CONTEXT, args, 14, err)) {
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
						args[14].isEmpty() ? null : AOSH.parseUnixPath(args[14], "work_dir")
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_HTTPD_TOMCAT_CONTEXT)) {
			if(AOSH.checkParamCount(AOSHCommand.REMOVE_HTTPD_TOMCAT_CONTEXT, args, 1, err)) {
				connector.getSimpleAOClient().removeHttpdTomcatContext(AOSH.parseInt(args[1], "pkey"));
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.SET_HTTPD_TOMCAT_CONTEXT_ATTRIBUTES)) {
			if(AOSH.checkParamCount(AOSHCommand.SET_HTTPD_TOMCAT_CONTEXT_ATTRIBUTES, args, 15, err)) {
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
					args[15].isEmpty() ? null : AOSH.parseUnixPath(args[15], "work_dir")
				);
			}
			return true;
		} else return false;
	}
}
