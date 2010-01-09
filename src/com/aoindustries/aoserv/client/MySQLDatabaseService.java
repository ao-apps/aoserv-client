package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * @see  MySQLDatabase
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.mysql_databases)
public interface MySQLDatabaseService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends AOServServiceIntegerKey<C,F,MySQLDatabase> {

    /* TODO
    int addMySQLDatabase(
        String name,
        MySQLServer mysqlServer,
        Business bu
    ) throws IOException, SQLException {
    	int pkey=connector.requestIntQueryIL(
            true,
            AOServProtocol.CommandID.ADD,
            SchemaTable.TableID.MYSQL_DATABASES,
            name,
            mysqlServer.pkey,
            bu.pkey
        );
        return pkey;
    }

    public String generateMySQLDatabaseName(String template_base, String template_added) throws IOException, SQLException {
    	return connector.requestStringQuery(true, AOServProtocol.CommandID.GENERATE_MYSQL_DATABASE_NAME, template_base, template_added);
    }

    MySQLDatabase getMySQLDatabase(String name, MySQLServer ms) throws IOException, SQLException {
        // Use index first
        for(MySQLDatabase md : getMySQLDatabases(ms)) if(md.name.equals(name)) return md;
        return null;
    }

    List<MySQLDatabase> getMySQLDatabases(Business bu) throws IOException, SQLException {
        return getIndexedRows(MySQLDatabase.COLUMN_ACCOUNTING, bu.pkey);
    }

    List<MySQLDatabase> getMySQLDatabases(MySQLServer ms) throws IOException, SQLException {
        return getIndexedRows(MySQLDatabase.COLUMN_MYSQL_SERVER, ms.pkey);
    }

    @Override
    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
        String command=args[0];
        if(command.equalsIgnoreCase(AOSHCommand.ADD_MYSQL_DATABASE)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_MYSQL_DATABASE, args, 4, err)) {
                int pkey=connector.getSimpleAOClient().addMySQLDatabase(
                    Locale.getDefault(),
                    args[1],
                    args[2],
                    args[3],
                    args[4]
                );
                out.println(pkey);
                out.flush();
            }
            return true;
    	} else if(command.equalsIgnoreCase(AOSHCommand.CHECK_MYSQL_DATABASE_NAME)) {
            if(AOSH.checkParamCount(AOSHCommand.CHECK_MYSQL_DATABASE_NAME, args, 1, err)) {
                try {
                    connector.getSimpleAOClient().checkMySQLDatabaseName(Locale.getDefault(), args[1]);
                    out.println("true");
                    out.flush();
                } catch(IllegalArgumentException iae) {
                    err.print("aosh: "+AOSHCommand.CHECK_MYSQL_DATABASE_NAME+": ");
                    err.println(iae.getMessage());
                    err.flush();
                }
            }
            return true;
    	} else if(command.equalsIgnoreCase(AOSHCommand.DUMP_MYSQL_DATABASE)) {
            if(AOSH.checkParamCount(AOSHCommand.DUMP_MYSQL_DATABASE, args, 3, err)) {
                try {
                    connector.getSimpleAOClient().dumpMySQLDatabase(args[1], args[2], args[3], out);
                    out.flush();
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
                    out.println(connector.getSimpleAOClient().isMySQLDatabaseNameAvailable(Locale.getDefault(), args[1], args[2], args[3]));
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
                connector.getSimpleAOClient().removeMySQLDatabase(args[1], args[2], args[3]);
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

    boolean isMySQLDatabaseNameAvailable(String name, MySQLServer mysqlServer) throws IOException, SQLException {
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
     */
}
