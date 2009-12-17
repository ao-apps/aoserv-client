package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  PostgresDatabase
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class PostgresDatabaseTable extends CachedTableIntegerKey<PostgresDatabase> {

    PostgresDatabaseTable(AOServConnector connector) {
	super(connector, PostgresDatabase.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(PostgresDatabase.COLUMN_NAME_name, ASCENDING),
        new OrderBy(PostgresDatabase.COLUMN_POSTGRES_SERVER_name+'.'+PostgresServer.COLUMN_NAME_name, ASCENDING),
        new OrderBy(PostgresDatabase.COLUMN_POSTGRES_SERVER_name+'.'+PostgresServer.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    int addPostgresDatabase(
        String name,
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
            postgresServer.pkey,
            datdba.pkey,
            encoding.pkey,
            enablePostgis
        );
    	return pkey;
    }

    public String generatePostgresDatabaseName(String template_base, String template_added) throws IOException, SQLException {
    	return connector.requestStringQuery(true, AOServProtocol.CommandID.GENERATE_POSTGRES_DATABASE_NAME, template_base, template_added);
    }

    public PostgresDatabase get(int pkey) throws IOException, SQLException {
    	return getUniqueRow(PostgresDatabase.COLUMN_PKEY, pkey);
    }

    PostgresDatabase getPostgresDatabase(String name, PostgresServer postgresServer) throws IOException, SQLException {
        // Use the index first
	for(PostgresDatabase pd : getPostgresDatabases(postgresServer)) if(pd.name.equals(name)) return pd;
	return null;
    }

    List<PostgresDatabase> getPostgresDatabases(Business business) throws IOException, SQLException {
        String accounting=business.pkey;

        List<PostgresDatabase> cached=getRows();
    	int size=cached.size();
        List<PostgresDatabase> matches=new ArrayList<PostgresDatabase>(size);
    	for(int c=0;c<size;c++) {
            PostgresDatabase pd=cached.get(c);
            if(pd.getDatDBA().getPostgresUser().getUsername().accounting.equals(accounting)) matches.add(pd);
        }
        return matches;
    }

    List<PostgresDatabase> getPostgresDatabases(PostgresServer postgresServer) throws IOException, SQLException {
        return getIndexedRows(PostgresDatabase.COLUMN_POSTGRES_SERVER, postgresServer.pkey);
    }

    public SchemaTable.TableID getTableID() {
    	return SchemaTable.TableID.POSTGRES_DATABASES;
    }

    @Override
    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, SQLException, IOException {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.ADD_POSTGRES_DATABASE)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_POSTGRES_DATABASE, args, 5, err)) {
                int pkey=connector.getSimpleAOClient().addPostgresDatabase(
                    args[1],
                    args[2],
                    args[3],
                    args[4],
                    args[5],
                    AOSH.parseBoolean(args[6], "enable_postgis")
                );
                out.println(pkey);
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.CHECK_POSTGRES_DATABASE_NAME)) {
            if(AOSH.checkParamCount(AOSHCommand.CHECK_POSTGRES_DATABASE_NAME, args, 1, err)) {
                try {
                    connector.getSimpleAOClient().checkPostgresDatabaseName(args[1]);
                    out.println("true");
                    out.flush();
                } catch(IllegalArgumentException iae) {
                    err.print("aosh: "+AOSHCommand.CHECK_POSTGRES_DATABASE_NAME+": ");
                    err.println(iae.getMessage());
                    err.flush();
                }
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.DUMP_POSTGRES_DATABASE)) {
            if(AOSH.checkParamCount(AOSHCommand.DUMP_POSTGRES_DATABASE, args, 3, err)) {
                try {
                    connector.getSimpleAOClient().dumpPostgresDatabase(args[1], args[2], args[3], out);
                    out.flush();
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
                            args[1],
                            args[2],
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
                connector.getSimpleAOClient().removePostgresDatabase(args[1], args[2], args[3]);
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

    boolean isPostgresDatabaseNameAvailable(String name, PostgresServer postgresServer) throws IOException, SQLException {
    	return connector.requestBooleanQuery(
            true,
            AOServProtocol.CommandID.IS_POSTGRES_DATABASE_NAME_AVAILABLE,
            name,
            postgresServer.pkey
        );
    }

    public boolean isValidDatabaseName(String name) throws IOException, SQLException {
	return isValidDatabaseName(name, connector.getPostgresReservedWords().getRows());
    }

    public static boolean isValidDatabaseName(String name, List<?> reservedWords) {
	// Must be a-z first, then a-z or 0-9 or _
	int len = name.length();
	if (len == 0 || len > PostgresDatabase.MAX_DATABASE_NAME_LENGTH) return false;
	// The first character must be [a-z]
	char ch = name.charAt(0);
	if (ch < 'a' || ch > 'z') return false;
	// The rest may have additional characters
	for (int c = 1; c < len; c++) {
            ch = name.charAt(c);
            if ((ch < 'a' || ch > 'z') && (ch < '0' || ch > '9') && ch != '_') return false;
	}

	// Also must not be a reserved word
	int size=reservedWords.size();
	for(int c=0;c<size;c++) {
            if(name.equalsIgnoreCase(reservedWords.get(c).toString())) return false;
	}
	return true;
    }

    void waitForRebuild(AOServer aoServer) throws IOException, SQLException {
        connector.requestUpdate(
            true,
            AOServProtocol.CommandID.WAIT_FOR_REBUILD,
            SchemaTable.TableID.POSTGRES_DATABASES,
            aoServer.pkey
        );
    }
}