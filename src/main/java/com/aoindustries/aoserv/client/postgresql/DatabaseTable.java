/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2012, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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
 * along with aoserv-client.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.aoindustries.aoserv.client.postgresql;

import com.aoapps.hodgepodge.io.TerminalWriter;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.lang.validation.ValidationResult;
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.StreamHandler;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see  Database
 *
 * @author  AO Industries, Inc.
 */
public final class DatabaseTable extends CachedTableIntegerKey<Database> {

	DatabaseTable(AOServConnector connector) {
		super(connector, Database.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(Database.COLUMN_NAME_name, ASCENDING),
		new OrderBy(Database.COLUMN_POSTGRES_SERVER_name+'.'+Server.COLUMN_NAME_name, ASCENDING),
		new OrderBy(Database.COLUMN_POSTGRES_SERVER_name+'.'+Server.COLUMN_AO_SERVER_name+'.'+com.aoindustries.aoserv.client.linux.Server.COLUMN_HOSTNAME_name, ASCENDING)
	};
	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	int addPostgresDatabase(
		Database.Name name,
		Server postgresServer,
		UserServer datdba,
		Encoding encoding,
		boolean enablePostgis
	) throws IOException, SQLException {
		if(Database.isSpecial(name)) throw new SQLException("Refusing to add special PostgreSQL database: " + name + " on " + postgresServer);
		return connector.requestIntQueryIL(
			true,
			AoservProtocol.CommandID.ADD,
			Table.TableID.POSTGRES_DATABASES,
			name,
			postgresServer.getBind_id(),
			datdba.getPkey(),
			encoding.getPkey(),
			enablePostgis
		);
	}

	public Database.Name generatePostgresDatabaseName(String template_base, String template_added) throws IOException, SQLException {
		try {
			return Database.Name.valueOf(connector.requestStringQuery(true, AoservProtocol.CommandID.GENERATE_POSTGRES_DATABASE_NAME, template_base, template_added));
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public Database get(int pkey) throws IOException, SQLException {
		return getUniqueRow(Database.COLUMN_PKEY, pkey);
	}

	Database getPostgresDatabase(Database.Name name, Server postgresServer) throws IOException, SQLException {
		// Use the index first
		for(Database pd : getPostgresDatabases(postgresServer)) {
			if(pd.getName().equals(name)) return pd;
		}
		return null;
	}

	public List<Database> getPostgresDatabases(Package pack) throws IOException, SQLException {
		Account.Name name = pack.getName();

		List<Database> cached=getRows();
		int size=cached.size();
		List<Database> matches=new ArrayList<>(size);
		for(int c=0;c<size;c++) {
			Database pd=cached.get(c);
			if(pd.getDatDBA().getPostgresUser().getUsername().getPackage_name().equals(name)) matches.add(pd);
		}
		return matches;
	}

	List<Database> getPostgresDatabases(UserServer psu) throws IOException, SQLException {
		return getIndexedRows(Database.COLUMN_DATDBA, psu.getPkey());
	}

	List<Database> getPostgresDatabases(Server postgresServer) throws IOException, SQLException {
		return getIndexedRows(Database.COLUMN_POSTGRES_SERVER, postgresServer.getBind_id());
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.POSTGRES_DATABASES;
	}

	@Override
	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, SQLException, IOException {
		String command=args[0];
		if(command.equalsIgnoreCase(Command.ADD_POSTGRES_DATABASE)) {
			if(AOSH.checkParamCount(Command.ADD_POSTGRES_DATABASE, args, 6, err)) {
				out.println(
					connector.getSimpleAOClient().addPostgresDatabase(
						AOSH.parsePostgresDatabaseName(args[1], "database_name"),
						AOSH.parsePostgresServerName(args[2], "postgres_server"),
						args[3],
						AOSH.parsePostgresUserName(args[4], "datdba"),
						args[5],
						AOSH.parseBoolean(args[6], "enable_postgis")
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.CHECK_POSTGRES_DATABASE_NAME)) {
			if(AOSH.checkParamCount(Command.CHECK_POSTGRES_DATABASE_NAME, args, 1, err)) {
				ValidationResult validationResult = Database.Name.validate(args[1]);
				out.println(validationResult.isValid());
				out.flush();
				if(!validationResult.isValid()) {
					err.print("aosh: "+Command.CHECK_POSTGRES_DATABASE_NAME+": ");
					err.println(validationResult.toString());
					err.flush();
				}
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.DUMP_POSTGRES_DATABASE)) {
			if(AOSH.checkParamCount(Command.DUMP_POSTGRES_DATABASE, args, 4, err)) {
				try {
					Database.Name dbName = AOSH.parsePostgresDatabaseName(args[1], "database_name");
					Server.Name serverName = AOSH.parsePostgresServerName(args[2], "postgres_server");
					String aoServer = args[3];
					if(AOSH.parseBoolean(args[4], "gzip")) {
						connector.getSimpleAOClient().dumpPostgresDatabase(
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
						connector.getSimpleAOClient().dumpPostgresDatabase(
							dbName,
							serverName,
							aoServer,
							out
						);
						out.flush();
					}
				} catch(IllegalArgumentException iae) {
					err.print("aosh: "+Command.DUMP_POSTGRES_DATABASE+": ");
					err.println(iae.getMessage());
					err.flush();
				}
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.GENERATE_POSTGRES_DATABASE_NAME)) {
			if(AOSH.checkParamCount(Command.GENERATE_POSTGRES_DATABASE_NAME, args, 2, err)) {
				out.println(connector.getSimpleAOClient().generatePostgresDatabaseName(args[1], args[2]));
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.IS_POSTGRES_DATABASE_NAME_AVAILABLE)) {
			if(AOSH.checkParamCount(Command.IS_POSTGRES_DATABASE_NAME_AVAILABLE, args, 3, err)) {
				try {
					out.println(
						connector.getSimpleAOClient().isPostgresDatabaseNameAvailable(
							AOSH.parsePostgresDatabaseName(args[1], "database_name"),
							AOSH.parsePostgresServerName(args[2], "postgres_server"),
							args[3]
						)
					);
					out.flush();
				} catch(IllegalArgumentException iae) {
					err.print("aosh: "+Command.IS_POSTGRES_DATABASE_NAME_AVAILABLE+": ");
					err.println(iae.getMessage());
					err.flush();
				}
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.REMOVE_POSTGRES_DATABASE)) {
			if(AOSH.checkParamCount(Command.REMOVE_POSTGRES_DATABASE, args, 3, err)) {
				connector.getSimpleAOClient().removePostgresDatabase(
					AOSH.parsePostgresDatabaseName(args[1], "database_name"),
					AOSH.parsePostgresServerName(args[2], "postgres_server"),
					args[3]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.WAIT_FOR_POSTGRES_DATABASE_REBUILD)) {
			if(AOSH.checkParamCount(Command.WAIT_FOR_POSTGRES_DATABASE_REBUILD, args, 1, err)) {
				connector.getSimpleAOClient().waitForPostgresDatabaseRebuild(args[1]);
			}
			return true;
		}
		return false;
	}

	boolean isPostgresDatabaseNameAvailable(Database.Name name, Server postgresServer) throws IOException, SQLException {
		return connector.requestBooleanQuery(
			true,
			AoservProtocol.CommandID.IS_POSTGRES_DATABASE_NAME_AVAILABLE,
			name,
			postgresServer.getBind_id()
		);
	}

	public void waitForRebuild(com.aoindustries.aoserv.client.linux.Server aoServer) throws IOException, SQLException {
		connector.requestUpdate(
			true,
			AoservProtocol.CommandID.WAIT_FOR_REBUILD,
			Table.TableID.POSTGRES_DATABASES,
			aoServer.getPkey()
		);
	}
}
