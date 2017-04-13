/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2012, 2016, 2017  AO Industries, Inc.
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

import com.aoindustries.aoserv.client.validator.MySQLUserId;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.util.IntList;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  MySQLServerUser
 *
 * @author  AO Industries, Inc.
 */
final public class MySQLServerUserTable extends CachedTableIntegerKey<MySQLServerUser> {

	MySQLServerUserTable(AOServConnector connector) {
		super(connector, MySQLServerUser.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(MySQLServerUser.COLUMN_USERNAME_name, ASCENDING),
		new OrderBy(MySQLServerUser.COLUMN_MYSQL_SERVER_name+'.'+MySQLServer.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING),
		new OrderBy(MySQLServerUser.COLUMN_MYSQL_SERVER_name+'.'+MySQLServer.COLUMN_NAME_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	int addMySQLServerUser(final MySQLUserId username, final MySQLServer mysqlServer, final String host) throws IOException, SQLException {
		return connector.requestResult(
			true,
			AOServProtocol.CommandID.ADD,
			new AOServConnector.ResultRequest<Integer>() {
				int pkey;
				IntList invalidateList;

				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(SchemaTable.TableID.MYSQL_SERVER_USERS.ordinal());
					out.writeUTF(username.toString());
					out.writeCompressedInt(mysqlServer.pkey);
					out.writeBoolean(host!=null); if(host!=null) out.writeUTF(host);
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
	public MySQLServerUser get(int pkey) throws IOException, SQLException {
		return getUniqueRow(MySQLServerUser.COLUMN_PKEY, pkey);
	}

	MySQLServerUser getMySQLServerUser(MySQLUserId username, MySQLServer ms) throws IOException, SQLException {
		int msPKey=ms.pkey;

		List<MySQLServerUser> table=getRows();
		int size=table.size();
		for(int c=0;c<size;c++) {
			MySQLServerUser msu=table.get(c);
			if(msu.mysql_server==msPKey && msu.username.equals(username)) return msu;
		}
		return null;
	}

	List<MySQLServerUser> getMySQLServerUsers(MySQLUser mu) throws IOException, SQLException {
		return getIndexedRows(MySQLServerUser.COLUMN_USERNAME, mu.pkey);
	}

	List<MySQLServerUser> getMySQLServerUsers(MySQLServer ms) throws IOException, SQLException {
		return getIndexedRows(MySQLServerUser.COLUMN_MYSQL_SERVER, ms.pkey);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.MYSQL_SERVER_USERS;
	}

	@Override
	boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, SQLException, IOException {
		String command=args[0];
		if(command.equalsIgnoreCase(AOSHCommand.ADD_MYSQL_SERVER_USER)) {
			if(AOSH.checkParamCount(AOSHCommand.ADD_MYSQL_SERVER_USER, args, 4, err)) {
				int pkey=connector.getSimpleAOClient().addMySQLServerUser(
					AOSH.parseMySQLUserId(args[1], "username"),
					AOSH.parseMySQLServerName(args[2], "mysql_server"),
					args[3],
					args[4]
				);
				out.println(pkey);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.DISABLE_MYSQL_SERVER_USER)) {
			if(AOSH.checkParamCount(AOSHCommand.DISABLE_MYSQL_SERVER_USER, args, 4, err)) {
				out.println(
					connector.getSimpleAOClient().disableMySQLServerUser(
						AOSH.parseMySQLUserId(args[1], "username"),
						AOSH.parseMySQLServerName(args[2], "mysql_server"),
						args[3],
						args[4]
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.ENABLE_MYSQL_SERVER_USER)) {
			if(AOSH.checkParamCount(AOSHCommand.ENABLE_MYSQL_SERVER_USER, args, 3, err)) {
				connector.getSimpleAOClient().enableMySQLServerUser(
					AOSH.parseMySQLUserId(args[1], "username"),
					AOSH.parseMySQLServerName(args[2], "mysql_server"),
					args[3]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.IS_MYSQL_SERVER_USER_PASSWORD_SET)) {
			if(AOSH.checkParamCount(AOSHCommand.IS_MYSQL_SERVER_USER_PASSWORD_SET, args, 3, err)) {
				out.println(
					connector.getSimpleAOClient().isMySQLServerUserPasswordSet(
						AOSH.parseMySQLUserId(args[1], "username"),
						AOSH.parseMySQLServerName(args[2], "mysql_server"),
						args[3]
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_MYSQL_SERVER_USER)) {
			if(AOSH.checkParamCount(AOSHCommand.REMOVE_MYSQL_SERVER_USER, args, 3, err)) {
				connector.getSimpleAOClient().removeMySQLServerUser(
					AOSH.parseMySQLUserId(args[1], "username"),
					AOSH.parseMySQLServerName(args[2], "mysql_server"),
					args[3]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.SET_MYSQL_SERVER_USER_PASSWORD)) {
			if(AOSH.checkParamCount(AOSHCommand.SET_MYSQL_SERVER_USER_PASSWORD, args, 4, err)) {
				connector.getSimpleAOClient().setMySQLServerUserPassword(
					AOSH.parseMySQLUserId(args[1], "username"),
					AOSH.parseMySQLServerName(args[2], "mysql_server"),
					args[3],
					args[4]
				);
			}
			return true;
		}
		return false;
	}
}
