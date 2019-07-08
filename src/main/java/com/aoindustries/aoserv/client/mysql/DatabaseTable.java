/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018, 2019  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.StreamHandler;
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.validation.ValidationException;
import com.aoindustries.validation.ValidationResult;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  Database
 *
 * @author  AO Industries, Inc.
 */
final public class DatabaseTable extends CachedTableIntegerKey<Database> {

	DatabaseTable(AOServConnector connector) {
		super(connector, Database.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(Database.COLUMN_NAME_name, ASCENDING),
		new OrderBy(Database.COLUMN_MYSQL_SERVER_name+'.'+Server.COLUMN_AO_SERVER_name+'.'+com.aoindustries.aoserv.client.linux.Server.COLUMN_HOSTNAME_name, ASCENDING),
		new OrderBy(Database.COLUMN_MYSQL_SERVER_name+'.'+Server.COLUMN_NAME_name, ASCENDING)
	};
	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	public int addMySQLDatabase(
		Database.Name name,
		Server mysqlServer,
		Package packageObj
	) throws IOException, SQLException {
		int pkey=connector.requestIntQueryIL(
			true,
			AoservProtocol.CommandID.ADD,
			Table.TableID.MYSQL_DATABASES,
			name,
			mysqlServer.getBind_id(),
			packageObj.getName()
		);
		return pkey;
	}

	public Database.Name generateMySQLDatabaseName(String template_base, String template_added) throws IOException, SQLException {
		try {
			return Database.Name.valueOf(connector.requestStringQuery(true, AoservProtocol.CommandID.GENERATE_MYSQL_DATABASE_NAME, template_base, template_added));
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public Database get(int pkey) throws IOException, SQLException {
		return getUniqueRow(Database.COLUMN_PKEY, pkey);
	}

	Database getMySQLDatabase(Database.Name name, Server ms) throws IOException, SQLException {
		// Use index first
		for(Database md : getMySQLDatabases(ms)) if(md.getName().equals(name)) return md;
		return null;
	}

	public List<Database> getMySQLDatabases(Package pack) throws IOException, SQLException {
		return getIndexedRows(Database.COLUMN_PACKAGE, pack.getName());
	}

	List<Database> getMySQLDatabases(Server ms) throws IOException, SQLException {
		return getIndexedRows(Database.COLUMN_MYSQL_SERVER, ms.getBind_id());
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.MYSQL_DATABASES;
	}

	@Override
	public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(Command.ADD_MYSQL_DATABASE)) {
			if(AOSH.checkParamCount(Command.ADD_MYSQL_DATABASE, args, 4, err)) {
				out.println(
					connector.getSimpleAOClient().addMySQLDatabase(
						AOSH.parseMySQLDatabaseName(args[1], "database_name"),
						AOSH.parseMySQLServerName(args[2], "mysql_server"),
						args[3],
						AOSH.parseAccountingCode(args[4], "package")
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.CHECK_MYSQL_DATABASE_NAME)) {
			if(AOSH.checkParamCount(Command.CHECK_MYSQL_DATABASE_NAME, args, 1, err)) {
				ValidationResult validationResult = Database.Name.validate(args[1]);
				out.println(validationResult.isValid());
				out.flush();
				if(!validationResult.isValid()) {
					err.print("aosh: "+Command.CHECK_MYSQL_DATABASE_NAME+": ");
					err.println(validationResult.toString());
					err.flush();
				}
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.DUMP_MYSQL_DATABASE)) {
			if(AOSH.checkParamCount(Command.DUMP_MYSQL_DATABASE, args, 4, err)) {
				try {
					Database.Name dbName = AOSH.parseMySQLDatabaseName(args[1], "database_name");
					Server.Name serverName = AOSH.parseMySQLServerName(args[2], "mysql_server");
					String aoServer = args[3];
					if(AOSH.parseBoolean(args[4], "gzip")) {
						connector.getSimpleAOClient().dumpMySQLDatabase(
							dbName,
							serverName,
							aoServer,
							true,
							new StreamHandler() {
								@Override
								public void onDumpSize(long dumpSize) {
									// Do nothing
								}
								@Override
								public OutputStream getOut() {
									return System.out; // By-pass TerminalWriter stuff to avoid possible encoding issues.
								}
							}
						);
						System.out.flush();
					} else {
						connector.getSimpleAOClient().dumpMySQLDatabase(
							dbName,
							serverName,
							aoServer,
							out
						);
						out.flush();
					}
				} catch(IllegalArgumentException iae) {
					err.print("aosh: "+Command.DUMP_MYSQL_DATABASE+": ");
					err.println(iae.getMessage());
					err.flush();
				}
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.GENERATE_MYSQL_DATABASE_NAME)) {
			if(AOSH.checkParamCount(Command.GENERATE_MYSQL_DATABASE_NAME, args, 2, err)) {
				out.println(connector.getSimpleAOClient().generateMySQLDatabaseName(args[1], args[2]));
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.IS_MYSQL_DATABASE_NAME_AVAILABLE)) {
			if(AOSH.checkParamCount(Command.IS_MYSQL_DATABASE_NAME_AVAILABLE, args, 3, err)) {
				try {
					out.println(
						connector.getSimpleAOClient().isMySQLDatabaseNameAvailable(
							AOSH.parseMySQLDatabaseName(args[1], "database_name"),
							AOSH.parseMySQLServerName(args[2], "mysql_server"),
							args[3]
						)
					);
					out.flush();
				} catch(IllegalArgumentException iae) {
					err.print("aosh: "+Command.IS_MYSQL_DATABASE_NAME_AVAILABLE+": ");
					err.println(iae.getMessage());
					err.flush();
				}
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.REMOVE_MYSQL_DATABASE)) {
			if(AOSH.checkParamCount(Command.REMOVE_MYSQL_DATABASE, args, 3, err)) {
				connector.getSimpleAOClient().removeMySQLDatabase(
					AOSH.parseMySQLDatabaseName(args[1], "database_name"),
					AOSH.parseMySQLServerName(args[2], "mysql_server"),
					args[3]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.WAIT_FOR_MYSQL_DATABASE_REBUILD)) {
			if(AOSH.checkParamCount(Command.WAIT_FOR_MYSQL_DATABASE_REBUILD, args, 1, err)) {
				connector.getSimpleAOClient().waitForMySQLDatabaseRebuild(args[1]);
			}
			return true;
		}
		return false;
	}

	boolean isMySQLDatabaseNameAvailable(Database.Name name, Server mysqlServer) throws IOException, SQLException {
		return connector.requestBooleanQuery(true, AoservProtocol.CommandID.IS_MYSQL_DATABASE_NAME_AVAILABLE, name, mysqlServer.getPkey());
	}

	public void waitForRebuild(com.aoindustries.aoserv.client.linux.Server aoServer) throws IOException, SQLException {
		connector.requestUpdate(
			true,
			AoservProtocol.CommandID.WAIT_FOR_REBUILD,
			Table.TableID.MYSQL_DATABASES,
			aoServer.getPkey()
		);
	}
}
