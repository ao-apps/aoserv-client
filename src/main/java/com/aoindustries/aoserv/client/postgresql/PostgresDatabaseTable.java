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
package com.aoindustries.aoserv.client.postgresql;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.StreamHandler;
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.aosh.AOSHCommand;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.linux.AOServer;
import com.aoindustries.aoserv.client.schema.AOServProtocol;
import com.aoindustries.aoserv.client.schema.SchemaTable;
import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.aoserv.client.validator.PostgresDatabaseName;
import com.aoindustries.aoserv.client.validator.PostgresServerName;
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.validation.ValidationException;
import com.aoindustries.validation.ValidationResult;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see  PostgresDatabase
 *
 * @author  AO Industries, Inc.
 */
final public class PostgresDatabaseTable extends CachedTableIntegerKey<PostgresDatabase> {

	public PostgresDatabaseTable(AOServConnector connector) {
		super(connector, PostgresDatabase.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(PostgresDatabase.COLUMN_NAME_name, ASCENDING),
		new OrderBy(PostgresDatabase.COLUMN_POSTGRES_SERVER_name+'.'+PostgresServer.COLUMN_NAME_name, ASCENDING),
		new OrderBy(PostgresDatabase.COLUMN_POSTGRES_SERVER_name+'.'+PostgresServer.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING)
	};
	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	int addPostgresDatabase(
		PostgresDatabaseName name,
		PostgresServer postgresServer,
		PostgresServerUser datdba,
		PostgresEncoding encoding,
		boolean enablePostgis
	) throws IOException, SQLException {
		int pkey=connector.requestIntQueryIL(
			true,
			AOServProtocol.CommandID.ADD,
			SchemaTable.TableID.POSTGRES_DATABASES,
			name,
			postgresServer.getBind_id(),
			datdba.getPkey(),
			encoding.getPkey(),
			enablePostgis
		);
		return pkey;
	}

	public PostgresDatabaseName generatePostgresDatabaseName(String template_base, String template_added) throws IOException, SQLException {
		try {
			return PostgresDatabaseName.valueOf(connector.requestStringQuery(true, AOServProtocol.CommandID.GENERATE_POSTGRES_DATABASE_NAME, template_base, template_added));
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public PostgresDatabase get(int pkey) throws IOException, SQLException {
		return getUniqueRow(PostgresDatabase.COLUMN_PKEY, pkey);
	}

	PostgresDatabase getPostgresDatabase(PostgresDatabaseName name, PostgresServer postgresServer) throws IOException, SQLException {
		// Use the index first
		for(PostgresDatabase pd : getPostgresDatabases(postgresServer)) if(pd.name.equals(name)) return pd;
		return null;
	}

	public List<PostgresDatabase> getPostgresDatabases(Package pack) throws IOException, SQLException {
		AccountingCode name = pack.getName();

		List<PostgresDatabase> cached=getRows();
		int size=cached.size();
		List<PostgresDatabase> matches=new ArrayList<>(size);
		for(int c=0;c<size;c++) {
			PostgresDatabase pd=cached.get(c);
			if(pd.getDatDBA().getPostgresUser().getUsername().getPackage_name().equals(name)) matches.add(pd);
		}
		return matches;
	}

	List<PostgresDatabase> getPostgresDatabases(PostgresServerUser psu) throws IOException, SQLException {
		return getIndexedRows(PostgresDatabase.COLUMN_DATDBA, psu.getPkey());
	}

	List<PostgresDatabase> getPostgresDatabases(PostgresServer postgresServer) throws IOException, SQLException {
		return getIndexedRows(PostgresDatabase.COLUMN_POSTGRES_SERVER, postgresServer.getBind_id());
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.POSTGRES_DATABASES;
	}

	@Override
	public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, SQLException, IOException {
		String command=args[0];
		if(command.equalsIgnoreCase(AOSHCommand.ADD_POSTGRES_DATABASE)) {
			if(AOSH.checkParamCount(AOSHCommand.ADD_POSTGRES_DATABASE, args, 5, err)) {
				out.println(
					connector.getSimpleAOClient().addPostgresDatabase(
						AOSH.parsePostgresDatabaseName(args[1], "database_name"),
						AOSH.parsePostgresServerName(args[2], "postgres_server"),
						args[3],
						AOSH.parsePostgresUserId(args[4], "datdba"),
						args[5],
						AOSH.parseBoolean(args[6], "enable_postgis")
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.CHECK_POSTGRES_DATABASE_NAME)) {
			if(AOSH.checkParamCount(AOSHCommand.CHECK_POSTGRES_DATABASE_NAME, args, 1, err)) {
				ValidationResult validationResult = PostgresDatabaseName.validate(args[1]);
				out.println(validationResult.isValid());
				out.flush();
				if(!validationResult.isValid()) {
					err.print("aosh: "+AOSHCommand.CHECK_POSTGRES_DATABASE_NAME+": ");
					err.println(validationResult.toString());
					err.flush();
				}
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.DUMP_POSTGRES_DATABASE)) {
			if(AOSH.checkParamCount(AOSHCommand.DUMP_POSTGRES_DATABASE, args, 4, err)) {
				try {
					PostgresDatabaseName dbName = AOSH.parsePostgresDatabaseName(args[1], "database_name");
					PostgresServerName serverName = AOSH.parsePostgresServerName(args[2], "postgres_server");
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
					err.print("aosh: "+AOSHCommand.DUMP_POSTGRES_DATABASE+": ");
					err.println(iae.getMessage());
					err.flush();
				}
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.GENERATE_POSTGRES_DATABASE_NAME)) {
			if(AOSH.checkParamCount(AOSHCommand.GENERATE_POSTGRES_DATABASE_NAME, args, 2, err)) {
				out.println(connector.getSimpleAOClient().generatePostgresDatabaseName(args[1], args[2]));
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.IS_POSTGRES_DATABASE_NAME_AVAILABLE)) {
			if(AOSH.checkParamCount(AOSHCommand.IS_POSTGRES_DATABASE_NAME_AVAILABLE, args, 3, err)) {
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
					err.print("aosh: "+AOSHCommand.IS_POSTGRES_DATABASE_NAME_AVAILABLE+": ");
					err.println(iae.getMessage());
					err.flush();
				}
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_POSTGRES_DATABASE)) {
			if(AOSH.checkParamCount(AOSHCommand.REMOVE_POSTGRES_DATABASE, args, 3, err)) {
				connector.getSimpleAOClient().removePostgresDatabase(
					AOSH.parsePostgresDatabaseName(args[1], "database_name"),
					AOSH.parsePostgresServerName(args[2], "postgres_server"),
					args[3]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.WAIT_FOR_POSTGRES_DATABASE_REBUILD)) {
			if(AOSH.checkParamCount(AOSHCommand.WAIT_FOR_POSTGRES_DATABASE_REBUILD, args, 1, err)) {
				connector.getSimpleAOClient().waitForPostgresDatabaseRebuild(args[1]);
			}
			return true;
		}
		return false;
	}

	boolean isPostgresDatabaseNameAvailable(PostgresDatabaseName name, PostgresServer postgresServer) throws IOException, SQLException {
		return connector.requestBooleanQuery(
			true,
			AOServProtocol.CommandID.IS_POSTGRES_DATABASE_NAME_AVAILABLE,
			name,
			postgresServer.getBind_id()
		);
	}

	public void waitForRebuild(AOServer aoServer) throws IOException, SQLException {
		connector.requestUpdate(
			true,
			AOServProtocol.CommandID.WAIT_FOR_REBUILD,
			SchemaTable.TableID.POSTGRES_DATABASES,
			aoServer.getPkey()
		);
	}
}
