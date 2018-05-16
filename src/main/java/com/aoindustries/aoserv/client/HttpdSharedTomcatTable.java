/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2012, 2016, 2017, 2018  AO Industries, Inc.
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

import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.util.IntList;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see  HttpdSharedTomcat
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdSharedTomcatTable extends CachedTableIntegerKey<HttpdSharedTomcat> {

	protected HttpdSharedTomcatTable(AOServConnector connector) {
		super(connector, HttpdSharedTomcat.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(HttpdSharedTomcat.COLUMN_NAME_name, ASCENDING),
		new OrderBy(HttpdSharedTomcat.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	int addHttpdSharedTomcat(
		final String name,
		final AOServer aoServer,
		HttpdTomcatVersion version,
		final LinuxServerAccount lsa,
		final LinuxServerGroup lsg
	) throws IOException, SQLException {
		final int tvPkey = version.getTechnologyVersion(connector).getPkey();
		return connector.requestResult(
			true,
			AOServProtocol.CommandID.ADD,
			new AOServConnector.ResultRequest<Integer>() {
				int pkey;
				IntList invalidateList;

				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(SchemaTable.TableID.HTTPD_SHARED_TOMCATS.ordinal());
					out.writeUTF(name);
					out.writeCompressedInt(aoServer.pkey);
					out.writeCompressedInt(tvPkey);
					out.writeUTF(lsa.username.toString());
					out.writeUTF(lsg.name.toString());
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

	public String generateSharedTomcatName(String template) throws IOException, SQLException {
		return connector.requestStringQuery(true, AOServProtocol.CommandID.GENERATE_SHARED_TOMCAT_NAME, template);
	}

	@Override
	public HttpdSharedTomcat get(int pkey) throws SQLException, IOException {
		return getUniqueRow(HttpdSharedTomcat.COLUMN_PKEY, pkey);
	}

	HttpdSharedTomcat getHttpdSharedTomcat(HttpdWorker hw) throws SQLException, IOException {
		return getUniqueRow(HttpdSharedTomcat.COLUMN_TOMCAT4_WORKER, hw.pkey);
	}

	HttpdSharedTomcat getHttpdSharedTomcatByShutdownPort(NetBind nb) throws SQLException, IOException {
		return getUniqueRow(HttpdSharedTomcat.COLUMN_TOMCAT4_SHUTDOWN_PORT, nb.pkey);
	}

	List<HttpdSharedTomcat> getHttpdSharedTomcats(LinuxServerAccount lsa) throws IOException, SQLException {
		return getIndexedRows(HttpdSharedTomcat.COLUMN_LINUX_SERVER_ACCOUNT, lsa.pkey);
	}

	List<HttpdSharedTomcat> getHttpdSharedTomcats(Package pk) throws IOException, SQLException {
		AccountingCode pkname=pk.name;

		List<HttpdSharedTomcat> cached=getRows();
		int size=cached.size();
		List<HttpdSharedTomcat> matches=new ArrayList<>(size);
		for(int c=0;c<size;c++) {
			HttpdSharedTomcat hst=cached.get(c);
			if(hst.getLinuxServerGroup().getLinuxGroup().packageName.equals(pkname)) matches.add(hst);
		}
		return matches;
	}

	List<HttpdSharedTomcat> getHttpdSharedTomcats(AOServer ao) throws IOException, SQLException {
		return getIndexedRows(HttpdSharedTomcat.COLUMN_AO_SERVER, ao.pkey);
	}

	HttpdSharedTomcat getHttpdSharedTomcat(String name, AOServer ao) throws IOException, SQLException {
		// Use the index first
		List<HttpdSharedTomcat> cached=getHttpdSharedTomcats(ao);
		int size=cached.size();
		for(int c=0;c<size;c++) {
			HttpdSharedTomcat tomcat=cached.get(c);
			if(tomcat.getName().equals(name)) return tomcat;
		}
		return null;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.HTTPD_SHARED_TOMCATS;
	}

	@Override
	boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, SQLException, IOException {
		String command=args[0];
		if(command.equalsIgnoreCase(AOSHCommand.ADD_HTTPD_SHARED_TOMCAT)) {
			if(AOSH.checkMinParamCount(AOSHCommand.ADD_HTTPD_SHARED_TOMCAT, args, 5, err)) {
				// Create an array of all the alternate hostnames
				out.println(
					connector.getSimpleAOClient().addHttpdSharedTomcat(
						args[1],
						args[2],
						args[3],
						AOSH.parseUserId(args[4], "linux_server_account"),
						AOSH.parseGroupId(args[5], "linux_server_group")
					)
				);
				out.flush();
			}
			return true;
		} if(command.equalsIgnoreCase(AOSHCommand.CHECK_SHARED_TOMCAT_NAME)) {
			if(AOSH.checkParamCount(AOSHCommand.CHECK_SHARED_TOMCAT_NAME, args, 1, err)) {
				try {
					SimpleAOClient.checkSharedTomcatName(args[1]);
					out.println("true");
				} catch(IllegalArgumentException iae) {
					out.print("aosh: "+AOSHCommand.CHECK_SHARED_TOMCAT_NAME+": ");
					out.println(iae.getMessage());
				}
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.DISABLE_HTTPD_SHARED_TOMCAT)) {
			if(AOSH.checkParamCount(AOSHCommand.DISABLE_HTTPD_SHARED_TOMCAT, args, 3, err)) {
				out.println(
					connector.getSimpleAOClient().disableHttpdSharedTomcat(
						args[1],
						args[2],
						args[3]
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.ENABLE_HTTPD_SHARED_TOMCAT)) {
			if(AOSH.checkParamCount(AOSHCommand.ENABLE_HTTPD_SHARED_TOMCAT, args, 2, err)) {
				connector.getSimpleAOClient().enableHttpdSharedTomcat(args[1], args[2]);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.GENERATE_SHARED_TOMCAT_NAME)) {
			if(AOSH.checkParamCount(AOSHCommand.GENERATE_SHARED_TOMCAT_NAME, args, 1, err)) {
				out.println(connector.getSimpleAOClient().generateSharedTomcatName(args[1]));
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.IS_SHARED_TOMCAT_NAME_AVAILABLE)) {
			if(AOSH.checkParamCount(AOSHCommand.IS_SHARED_TOMCAT_NAME_AVAILABLE, args, 1, err)) {
				out.println(connector.getSimpleAOClient().isSharedTomcatNameAvailable(args[1]));
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_HTTPD_SHARED_TOMCAT)) {
			if(AOSH.checkParamCount(AOSHCommand.REMOVE_HTTPD_SHARED_TOMCAT, args, 2, err)) {
				connector.getSimpleAOClient().removeHttpdSharedTomcat(args[1], args[2]);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.SET_HTTPD_SHARED_TOMCAT_IS_MANUAL)) {
			if(AOSH.checkParamCount(AOSHCommand.SET_HTTPD_SHARED_TOMCAT_IS_MANUAL, args, 3, err)) {
				connector.getSimpleAOClient().setHttpdSharedTomcatIsManual(
					args[1],
					args[2],
					AOSH.parseBoolean(args[3], "is_manual")
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.SET_HTTPD_SHARED_TOMCAT_MAX_POST_SIZE)) {
			if(AOSH.checkParamCount(AOSHCommand.SET_HTTPD_SHARED_TOMCAT_MAX_POST_SIZE, args, 3, err)) {
				connector.getSimpleAOClient().setHttpdSharedTomcatMaxPostSize(
					args[1],
					args[2],
					args[3].isEmpty() ? -1 : AOSH.parseInt(args[3], "max_post_size")
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.SET_HTTPD_SHARED_TOMCAT_UNPACK_WARS)) {
			if(AOSH.checkParamCount(AOSHCommand.SET_HTTPD_SHARED_TOMCAT_UNPACK_WARS, args, 3, err)) {
				connector.getSimpleAOClient().setHttpdSharedTomcatIsManual(
					args[1],
					args[2],
					AOSH.parseBoolean(args[3], "unpack_wars")
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.SET_HTTPD_SHARED_TOMCAT_AUTO_DEPLOY)) {
			if(AOSH.checkParamCount(AOSHCommand.SET_HTTPD_SHARED_TOMCAT_AUTO_DEPLOY, args, 3, err)) {
				connector.getSimpleAOClient().setHttpdSharedTomcatAutoDeploy(
					args[1],
					args[2],
					AOSH.parseBoolean(args[3], "auto_deploy")
				);
			}
			return true;
		}
		return false;
	}

	public boolean isSharedTomcatNameAvailable(String name) throws IOException, SQLException {
		return connector.requestBooleanQuery(
			true,
			AOServProtocol.CommandID.IS_SHARED_TOMCAT_NAME_AVAILABLE,
			name
		);
	}
}
