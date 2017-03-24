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

import com.aoindustries.aoserv.client.validator.MySQLDatabaseName;
import com.aoindustries.aoserv.client.validator.MySQLServerName;
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.validation.ValidationException;
import com.aoindustries.validation.ValidationResult;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  MySQLDatabase
 *
 * @author  AO Industries, Inc.
 */
final public class MySQLDatabaseTable extends CachedTableIntegerKey<MySQLDatabase> {

	MySQLDatabaseTable(AOServConnector connector) {
		super(connector, MySQLDatabase.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(MySQLDatabase.COLUMN_NAME_name, ASCENDING),
		new OrderBy(MySQLDatabase.COLUMN_MYSQL_SERVER_name+'.'+MySQLServer.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING),
		new OrderBy(MySQLDatabase.COLUMN_MYSQL_SERVER_name+'.'+MySQLServer.COLUMN_NAME_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	int addMySQLDatabase(
		MySQLDatabaseName name,
		MySQLServer mysqlServer,
		Package packageObj
	) throws IOException, SQLException {
		int pkey=connector.requestIntQueryIL(
			true,
			AOServProtocol.CommandID.ADD,
			SchemaTable.TableID.MYSQL_DATABASES,
			name,
			mysqlServer.pkey,
			packageObj.name
		);
		return pkey;
	}

	public MySQLDatabaseName generateMySQLDatabaseName(String template_base, String template_added) throws IOException, SQLException {
		try {
			return MySQLDatabaseName.valueOf(connector.requestStringQuery(true, AOServProtocol.CommandID.GENERATE_MYSQL_DATABASE_NAME, template_base, template_added));
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public MySQLDatabase get(int pkey) throws IOException, SQLException {
		return getUniqueRow(MySQLDatabase.COLUMN_PKEY, pkey);
	}

	MySQLDatabase getMySQLDatabase(MySQLDatabaseName name, MySQLServer ms) throws IOException, SQLException {
		// Use index first
		for(MySQLDatabase md : getMySQLDatabases(ms)) if(md.name.equals(name)) return md;
		return null;
	}

	List<MySQLDatabase> getMySQLDatabases(Package pack) throws IOException, SQLException {
		return getIndexedRows(MySQLDatabase.COLUMN_PACKAGE, pack.name);
	}

	List<MySQLDatabase> getMySQLDatabases(MySQLServer ms) throws IOException, SQLException {
		return getIndexedRows(MySQLDatabase.COLUMN_MYSQL_SERVER, ms.pkey);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.MYSQL_DATABASES;
	}

	@Override
	boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(AOSHCommand.ADD_MYSQL_DATABASE)) {
			if(AOSH.checkParamCount(AOSHCommand.ADD_MYSQL_DATABASE, args, 4, err)) {
				int pkey=connector.getSimpleAOClient().addMySQLDatabase(
					AOSH.parseMySQLDatabaseName(args[1], "database_name"),
					AOSH.parseMySQLServerName(args[2], "mysql_server"),
					args[3],
					AOSH.parseAccountingCode(args[4], "package")
				);
				out.println(pkey);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.CHECK_MYSQL_DATABASE_NAME)) {
			if(AOSH.checkParamCount(AOSHCommand.CHECK_MYSQL_DATABASE_NAME, args, 1, err)) {
				ValidationResult validationResult = MySQLDatabaseName.validate(args[1]);
				out.println(validationResult.isValid());
				out.flush();
				if(!validationResult.isValid()) {
					err.print("aosh: "+AOSHCommand.CHECK_MYSQL_DATABASE_NAME+": ");
					err.println(validationResult.toString());
					err.flush();
				}
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.DUMP_MYSQL_DATABASE)) {
			if(AOSH.checkParamCount(AOSHCommand.DUMP_MYSQL_DATABASE, args, 4, err)) {
				try {
					MySQLDatabaseName dbName = AOSH.parseMySQLDatabaseName(args[1], "database_name");
					MySQLServerName serverName = AOSH.parseMySQLServerName(args[2], "mysql_server");
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
					err.print("aosh: "+AOSHCommand.DUMP_MYSQL_DATABASE+": ");
					err.println(iae.getMessage());
					err.flush();
				}
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.GENERATE_MYSQL_DATABASE_NAME)) {
			if(AOSH.checkParamCount(AOSHCommand.GENERATE_MYSQL_DATABASE_NAME, args, 2, err)) {
				out.println(connector.getSimpleAOClient().generateMySQLDatabaseName(args[1], args[2]));
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.IS_MYSQL_DATABASE_NAME_AVAILABLE)) {
			if(AOSH.checkParamCount(AOSHCommand.IS_MYSQL_DATABASE_NAME_AVAILABLE, args, 3, err)) {
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
					err.print("aosh: "+AOSHCommand.IS_MYSQL_DATABASE_NAME_AVAILABLE+": ");
					err.println(iae.getMessage());
					err.flush();
				}
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_MYSQL_DATABASE)) {
			if(AOSH.checkParamCount(AOSHCommand.REMOVE_MYSQL_DATABASE, args, 3, err)) {
				connector.getSimpleAOClient().removeMySQLDatabase(
					AOSH.parseMySQLDatabaseName(args[1], "database_name"),
					AOSH.parseMySQLServerName(args[2], "mysql_server"),
					args[3]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.WAIT_FOR_MYSQL_DATABASE_REBUILD)) {
			if(AOSH.checkParamCount(AOSHCommand.WAIT_FOR_MYSQL_DATABASE_REBUILD, args, 1, err)) {
				connector.getSimpleAOClient().waitForMySQLDatabaseRebuild(args[1]);
			}
			return true;
		}
		return false;
	}

	boolean isMySQLDatabaseNameAvailable(MySQLDatabaseName name, MySQLServer mysqlServer) throws IOException, SQLException {
		return connector.requestBooleanQuery(true, AOServProtocol.CommandID.IS_MYSQL_DATABASE_NAME_AVAILABLE, name, mysqlServer.pkey);
	}

	void waitForRebuild(AOServer aoServer) throws IOException, SQLException {
		connector.requestUpdate(
			true,
			AOServProtocol.CommandID.WAIT_FOR_REBUILD,
			SchemaTable.TableID.MYSQL_DATABASES,
			aoServer.pkey
		);
	}
}
