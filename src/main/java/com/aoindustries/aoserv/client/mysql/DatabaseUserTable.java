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
package com.aoindustries.aoserv.client.mysql;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
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
 * @see  DatabaseUser
 *
 * @author  AO Industries, Inc.
 */
final public class DatabaseUserTable extends CachedTableIntegerKey<DatabaseUser> {

	DatabaseUserTable(AOServConnector connector) {
		super(connector, DatabaseUser.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(DatabaseUser.COLUMN_MYSQL_DATABASE_name+'.'+Database.COLUMN_NAME_name, ASCENDING),
		new OrderBy(DatabaseUser.COLUMN_MYSQL_DATABASE_name+'.'+Database.COLUMN_MYSQL_SERVER_name+'.'+Server.COLUMN_AO_SERVER_name+'.'+com.aoindustries.aoserv.client.linux.Server.COLUMN_HOSTNAME_name, ASCENDING),
		new OrderBy(DatabaseUser.COLUMN_MYSQL_DATABASE_name+'.'+Database.COLUMN_MYSQL_SERVER_name+'.'+Server.COLUMN_NAME_name, ASCENDING),
		new OrderBy(DatabaseUser.COLUMN_MYSQL_SERVER_USER_name+'.'+UserServer.COLUMN_USERNAME_name, ASCENDING)
	};
	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	int addMySQLDBUser(
		final Database md,
		final UserServer msu,
		final boolean canSelect,
		final boolean canInsert,
		final boolean canUpdate,
		final boolean canDelete,
		final boolean canCreate,
		final boolean canDrop,
		final boolean canReference,
		final boolean canIndex,
		final boolean canAlter,
		final boolean canCreateTempTable,
		final boolean canLockTables,
		final boolean canCreateView,
		final boolean canShowView,
		final boolean canCreateRoutine,
		final boolean canAlterRoutine,
		final boolean canExecute,
		final boolean canEvent,
		final boolean canTrigger
	) throws IOException, SQLException {
		return connector.requestResult(true,
			AoservProtocol.CommandID.ADD,
			new AOServConnector.ResultRequest<Integer>() {
				int pkey;
				IntList invalidateList;

				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(Table.TableID.MYSQL_DB_USERS.ordinal());
					out.writeCompressedInt(md.getPkey());
					out.writeCompressedInt(msu.getPkey());
					out.writeBoolean(canSelect);
					out.writeBoolean(canInsert);
					out.writeBoolean(canUpdate);
					out.writeBoolean(canDelete);
					out.writeBoolean(canCreate);
					out.writeBoolean(canDrop);
					out.writeBoolean(canReference);
					out.writeBoolean(canIndex);
					out.writeBoolean(canAlter);
					out.writeBoolean(canCreateTempTable);
					out.writeBoolean(canLockTables);
					out.writeBoolean(canCreateView);
					out.writeBoolean(canShowView);
					out.writeBoolean(canCreateRoutine);
					out.writeBoolean(canAlterRoutine);
					out.writeBoolean(canExecute);
					out.writeBoolean(canEvent);
					out.writeBoolean(canTrigger);
				}

				@Override
				public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
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
	public DatabaseUser get(int pkey) throws IOException, SQLException {
		return getUniqueRow(DatabaseUser.COLUMN_PKEY, pkey);
	}

	DatabaseUser getMySQLDBUser(Database db, UserServer msu) throws IOException, SQLException {
		int msuPKey=msu.getPkey();

		// Use index first on database
		List<DatabaseUser> cached=getMySQLDBUsers(db);
		int size=cached.size();
		for(int c=0;c<size;c++) {
			DatabaseUser mdu=cached.get(c);
			if(mdu.mysql_server_user==msuPKey) return mdu;
		}
		return null;
	}

	List<DatabaseUser> getMySQLDBUsers(Server ms) throws IOException, SQLException {
		int msPKey=ms.getBind_id();

		List<DatabaseUser> cached=getRows();
		int size=cached.size();
		List<DatabaseUser> matches=new ArrayList<>(size);
		for(int c=0;c<size;c++) {
			DatabaseUser mdu=cached.get(c);
			Database md=mdu.getMySQLDatabase();
			// The database might be null if filtered or recently removed
			if(md!=null && md.mysql_server==msPKey) matches.add(mdu);
		}
		return matches;
	}

	List<DatabaseUser> getMySQLDBUsers(UserServer msu) throws IOException, SQLException {
		return getIndexedRows(DatabaseUser.COLUMN_MYSQL_SERVER_USER, msu.getPkey());
	}

	List<DatabaseUser> getMySQLDBUsers(Database md) throws IOException, SQLException {
		return getIndexedRows(DatabaseUser.COLUMN_MYSQL_DATABASE, md.getPkey());
	}

	List<UserServer> getMySQLServerUsers(Database md) throws IOException, SQLException {
		// Use index first
		List<DatabaseUser> cached=getMySQLDBUsers(md);
		int len=cached.size();
		List<UserServer> array=new ArrayList<>(len);
		for(int c=0;c<len;c++) array.add(cached.get(c).getMySQLServerUser());
		return array;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.MYSQL_DB_USERS;
	}

	@Override
	public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(Command.ADD_MYSQL_DB_USER)) {
			if(AOSH.checkParamCount(Command.ADD_MYSQL_DB_USER, args, 22, err)) {
				out.println(
					connector.getSimpleAOClient().addMySQLDBUser(
						AOSH.parseMySQLDatabaseName(args[1], "database_name"),
						AOSH.parseMySQLServerName(args[2], "mysql_server"),
						args[3],
						AOSH.parseMySQLUserId(args[4], "username"),
						AOSH.parseBoolean(args[5], "can_select"),
						AOSH.parseBoolean(args[6], "can_insert"),
						AOSH.parseBoolean(args[7], "can_update"),
						AOSH.parseBoolean(args[8], "can_delete"),
						AOSH.parseBoolean(args[9], "can_create"),
						AOSH.parseBoolean(args[10], "can_drop"),
						AOSH.parseBoolean(args[11], "can_reference"),
						AOSH.parseBoolean(args[12], "can_index"),
						AOSH.parseBoolean(args[13], "can_alter"),
						AOSH.parseBoolean(args[14], "can_create_temp_table"),
						AOSH.parseBoolean(args[15], "can_lock_tables"),
						AOSH.parseBoolean(args[16], "can_create_view"),
						AOSH.parseBoolean(args[17], "can_show_view"),
						AOSH.parseBoolean(args[18], "can_create_routine"),
						AOSH.parseBoolean(args[19], "can_alter_routine"),
						AOSH.parseBoolean(args[20], "can_execute"),
						AOSH.parseBoolean(args[21], "can_event"),
						AOSH.parseBoolean(args[22], "can_trigger")
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.REMOVE_MYSQL_DB_USER)) {
			if(AOSH.checkParamCount(Command.REMOVE_MYSQL_DB_USER, args, 4, err)) {
				connector.getSimpleAOClient().removeMySQLDBUser(
					AOSH.parseMySQLDatabaseName(args[1], "database_name"),
					AOSH.parseMySQLServerName(args[2], "mysql_server"),
					args[3],
					AOSH.parseMySQLUserId(args[4], "username")
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.WAIT_FOR_MYSQL_DB_USER_REBUILD)) {
			if(AOSH.checkParamCount(Command.WAIT_FOR_MYSQL_DB_USER_REBUILD, args, 1, err)) {
				connector.getSimpleAOClient().waitForMySQLDBUserRebuild(args[1]);
			}
			return true;
		}
		return false;
	}

	public void waitForRebuild(com.aoindustries.aoserv.client.linux.Server aoServer) throws IOException, SQLException {
		connector.requestUpdate(true,
			AoservProtocol.CommandID.WAIT_FOR_REBUILD,
			Table.TableID.MYSQL_DB_USERS,
			aoServer.getPkey()
		);
	}
}
