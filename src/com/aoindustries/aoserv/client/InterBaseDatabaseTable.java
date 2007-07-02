package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  InterBaseDatabase
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class InterBaseDatabaseTable extends CachedTableIntegerKey<InterBaseDatabase> {

    /**
     * The maximum name length for a database.
     */
    public static final int MAX_DATABASE_NAME_LENGTH=251;

    InterBaseDatabaseTable(AOServConnector connector) {
	super(connector, InterBaseDatabase.class);
    }

    int addInterBaseDatabase(InterBaseDBGroup dbGroup, String name, InterBaseServerUser datdba) {
	int pkey=connector.requestIntQueryIL(
            AOServProtocol.ADD,
            SchemaTable.TableID.INTERBASE_DATABASES,
            name,
            dbGroup.pkey,
            datdba.pkey
	);
	return pkey;
    }

    String generateInterBaseDatabaseName(InterBaseDBGroup dbGroup, String template_base, String template_added) {
	return connector.requestStringQuery(
            AOServProtocol.GENERATE_INTERBASE_DATABASE_NAME,
            dbGroup.pkey,
            template_base,
            template_added
        );
    }

    public InterBaseDatabase get(Object pkey) {
	return getUniqueRow(InterBaseDatabase.COLUMN_PKEY, pkey);
    }

    public InterBaseDatabase get(int pkey) {
	return getUniqueRow(InterBaseDatabase.COLUMN_PKEY, pkey);
    }

    InterBaseDatabase getInterBaseDatabase(InterBaseDBGroup dbGroup, String name) {
	int pkey=dbGroup.pkey;

	List<InterBaseDatabase> table=getRows();
	int size=table.size();
	for(int c=0;c<size;c++) {
            InterBaseDatabase id=table.get(c);
            if(id.db_group==pkey && id.name.equals(name)) return id;
	}
	return null;
    }

    List<InterBaseDatabase> getInterBaseDatabases(InterBaseDBGroup dbGroup) {
        return getIndexedRows(InterBaseDatabase.COLUMN_DB_GROUP, dbGroup.pkey);
    }

    List<InterBaseDatabase> getInterBaseDatabases(AOServer aoServer) {
	int pkey=aoServer.pkey;

	List<InterBaseDatabase> cached=getRows();
	int size=cached.size();
        List<InterBaseDatabase> matches=new ArrayList<InterBaseDatabase>(size);
	for(int c=0;c<size;c++) {
            InterBaseDatabase dat=cached.get(c);
            if(dat.getInterBaseDBGroup().getLinuxServerGroup().ao_server==pkey) matches.add(dat);
	}
	return matches;
    }

    List<InterBaseDatabase> getInterBaseDatabases(InterBaseServerUser isu) {
        return getIndexedRows(InterBaseDatabase.COLUMN_DATDBA, isu.pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.INTERBASE_DATABASES;
    }

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.ADD_INTERBASE_DATABASE)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_INTERBASE_DATABASE, args, 4, err)) {
                int pkey=connector.simpleAOClient.addInterBaseDatabase(
                    args[1],
                    args[2],
                    args[3],
                    args[4]
                );
                out.println(pkey);
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.BACKUP_INTERBASE_DATABASE)) {
            if(AOSH.checkParamCount(AOSHCommand.BACKUP_INTERBASE_DATABASE, args, 3, err)) {
                try {
                    int pkey=connector.simpleAOClient.backupInterBaseDatabase(args[1], args[2], args[3]);
                    out.println(pkey);
                    out.flush();
                } catch(IllegalArgumentException iae) {
                    err.print("aosh: "+AOSHCommand.BACKUP_INTERBASE_DATABASE+": ");
                    err.println(iae.getMessage());
                    err.flush();
                }
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.CHECK_INTERBASE_DATABASE_NAME)) {
            if(AOSH.checkParamCount(AOSHCommand.CHECK_INTERBASE_DATABASE_NAME, args, 1, err)) {
                try {
                    connector.simpleAOClient.checkInterBaseDatabaseName(args[1]);
                    out.println("true");
                    out.flush();
                } catch(IllegalArgumentException iae) {
                    err.print("aosh: "+AOSHCommand.CHECK_INTERBASE_DATABASE_NAME+": ");
                    err.println(iae.getMessage());
                    err.flush();
                }
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.DUMP_INTERBASE_DATABASE)) {
            if(AOSH.checkParamCount(AOSHCommand.DUMP_INTERBASE_DATABASE, args, 3, err)) {
                try {
                    connector.simpleAOClient.dumpInterBaseDatabase(args[1], args[2], args[3], out);
                    out.flush();
                } catch(IllegalArgumentException iae) {
                    err.print("aosh: "+AOSHCommand.DUMP_INTERBASE_DATABASE+": ");
                    err.println(iae.getMessage());
                    err.flush();
                }
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.GENERATE_INTERBASE_DATABASE_NAME)) {
            if(AOSH.checkParamCount(AOSHCommand.GENERATE_INTERBASE_DATABASE_NAME, args, 4, err)) {
                out.println(connector.simpleAOClient.generateInterBaseDatabaseName(args[1], args[2], args[3], args[4]));
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.IS_INTERBASE_DATABASE_NAME_AVAILABLE)) {
            if(AOSH.checkParamCount(AOSHCommand.IS_INTERBASE_DATABASE_NAME_AVAILABLE, args, 3, err)) {
                try {
                    out.println(connector.simpleAOClient.isInterBaseDatabaseNameAvailable(args[1], args[2], args[3]));
                    out.flush();
                } catch(IllegalArgumentException iae) {
                    err.print("aosh: "+AOSHCommand.IS_INTERBASE_DATABASE_NAME_AVAILABLE+": ");
                    err.println(iae.getMessage());
                    err.flush();
                }
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_INTERBASE_DATABASE)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_INTERBASE_DATABASE, args, 3, err)) {
                connector.simpleAOClient.removeInterBaseDatabase(args[1], args[2], args[3]);
            }
            return true;
	}
	return false;
    }

    boolean isInterBaseDatabaseNameAvailable(InterBaseDBGroup dbGroup, String name) {
        return connector.requestBooleanQuery(AOServProtocol.IS_INTERBASE_DATABASE_NAME_AVAILABLE, dbGroup.pkey, name);
    }

    public boolean isValidDatabaseName(String name) {
	return isValidDatabaseName(name, connector.interBaseReservedWords.getRows());
    }

    /**
     * Determines if a name can be used as an InterBase database.  A name is valid if
     * it is between 1 and 251 characters in length and uses only ASCII 0x21
     * through 0x7f, excluding the following characters:
     * <code>space , : ( ) [ ] ' " | & ;</code>
     */
    public static boolean isValidDatabaseName(String name, List<?> reservedWords) {
	int len = name.length();
	if (len == 0 || len > MAX_DATABASE_NAME_LENGTH)
		return false;
	// The first character must be [a-z]
	char ch = name.charAt(0);
	if (ch < 'a' || ch > 'z')
		return false;
	// The rest may have additional characters
	for (int c = 1; c < len; c++) {
            ch = name.charAt(c);
            if(
                ch<0x21
                || ch>0x7f
                || ch==','
                || ch==':'
                || ch=='('
                || ch==')'
                || ch=='['
                || ch==']'
                || ch=='\''
                || ch=='"'
                || ch=='|'
                || ch=='&'
                || ch==';'
            ) return false;
	}
        // Must not end with .gdb
        if(name.length()>=4 && name.substring(0,4).equals(".gdb")) return false;

        // Also must not be a reserved word
	int size=reservedWords.size();
	for(int c=0;c<size;c++) {
            if(name.equalsIgnoreCase(reservedWords.get(c).toString())) return false;
	}
	return true;
    }
}