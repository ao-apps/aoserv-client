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
 * @see  InterBaseDBGroup
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class InterBaseDBGroupTable extends CachedTableIntegerKey<InterBaseDBGroup> {

    /**
     * The maximum name for a group.
     */
    public static final int MAX_LENGTH=255;

    InterBaseDBGroupTable(AOServConnector connector) {
	super(connector, InterBaseDBGroup.class);
    }

    public int addInterBaseDBGroup(String name, LinuxServerGroup lsg) {
	int pkey=connector.requestIntQueryIL(
            AOServProtocol.ADD,
            SchemaTable.TableID.INTERBASE_DB_GROUPS,
            name,
            lsg.pkey
	);
	return pkey;
    }

    String generateInterBaseDBGroupName(AOServer aoServer, String template_base, String template_added) {
	return connector.requestStringQuery(
            AOServProtocol.GENERATE_INTERBASE_DB_GROUP_NAME,
            aoServer.pkey,
            template_base,
            template_added
        );
    }

    public InterBaseDBGroup get(Object pkey) {
	return getUniqueRow(InterBaseDBGroup.COLUMN_PKEY, pkey);
    }

    public InterBaseDBGroup get(int pkey) {
	return getUniqueRow(InterBaseDBGroup.COLUMN_PKEY, pkey);
    }

    InterBaseDBGroup getInterBaseDBGroup(AOServer aoServer, String name) {
        int aoPKey=aoServer.pkey;
	List<InterBaseDBGroup> cached=getRows();
	int size=cached.size();
        for(int c=0;c<size;c++) {
            InterBaseDBGroup dbGroup=cached.get(c);
            if(
                dbGroup.name.equals(name)
                && dbGroup.getLinuxServerGroup().ao_server==aoPKey
            ) return dbGroup;
        }
        return null;
    }

    List<InterBaseDBGroup> getInterBaseDBGroups(AOServer aoServer) {
	int pkey=aoServer.pkey;

	List<InterBaseDBGroup> cached=getRows();
	int size=cached.size();
        List<InterBaseDBGroup> matches=new ArrayList<InterBaseDBGroup>(size);
	for(int c=0;c<size;c++) {
            InterBaseDBGroup dbg=cached.get(c);
            if(dbg.getLinuxServerGroup().ao_server==pkey) matches.add(dbg);
	}
	return matches;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.INTERBASE_DB_GROUPS;
    }

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.ADD_INTERBASE_DB_GROUP)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_INTERBASE_DB_GROUP, args, 3, err)) {
                int pkey=connector.simpleAOClient.addInterBaseDBGroup(
                    args[1],
                    args[2],
                    args[3]
                );
                out.println(pkey);
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.CHECK_INTERBASE_DB_GROUP_NAME)) {
            if(AOSH.checkParamCount(AOSHCommand.CHECK_INTERBASE_DB_GROUP_NAME, args, 1, err)) {
                try {
                    connector.simpleAOClient.checkInterBaseDBGroupName(args[1]);
                    out.println("true");
                    out.flush();
                } catch(IllegalArgumentException iae) {
                    err.print("aosh: "+AOSHCommand.CHECK_INTERBASE_DB_GROUP_NAME+": ");
                    err.println(iae.getMessage());
                    err.flush();
                }
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.GENERATE_INTERBASE_DB_GROUP_NAME)) {
            if(AOSH.checkParamCount(AOSHCommand.GENERATE_INTERBASE_DB_GROUP_NAME, args, 3, err)) {
                out.println(connector.simpleAOClient.generateInterBaseDBGroupName(args[1], args[2], args[3]));
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.IS_INTERBASE_DB_GROUP_NAME_AVAILABLE)) {
            if(AOSH.checkParamCount(AOSHCommand.IS_INTERBASE_DB_GROUP_NAME_AVAILABLE, args, 2, err)) {
                try {
                    out.println(connector.simpleAOClient.isInterBaseDBGroupNameAvailable(args[1], args[2]));
                    out.flush();
                } catch(IllegalArgumentException iae) {
                    err.print("aosh: "+AOSHCommand.IS_INTERBASE_DB_GROUP_NAME_AVAILABLE+": ");
                    err.println(iae.getMessage());
                    err.flush();
                }
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_INTERBASE_DB_GROUP)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_INTERBASE_DB_GROUP, args, 2, err)) {
                connector.simpleAOClient.removeInterBaseDBGroup(args[1], args[2]);
            }
            return true;
	}
	return false;
    }

    boolean isInterBaseDBGroupNameAvailable(AOServer aoServer, String name) {
        return connector.requestBooleanQuery(AOServProtocol.IS_INTERBASE_DB_GROUP_NAME_AVAILABLE, aoServer.pkey, name);
    }

    public boolean isValidDBGroupName(String name) {
	return isValidDBGroupName(name, connector.interBaseReservedWords.getRows());
    }

    /**
     * Determines if a name can be used as an InterBase group name.  A name is valid if
     * it is between 1 and 255 characters in length and uses only ASCII 0x21
     * through 0x7f, excluding the following characters:
     * <code>space , : ( ) [ ] ' " | & ;</code>
     */
    public static boolean isValidDBGroupName(String name, List<?> reservedWords) {
	int len = name.length();
	if (len == 0 || len > MAX_LENGTH) return false;
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

        // Must not be disallowed
        if(name.equals(InterBaseDBGroup.LOST_AND_FOUND)) return false;

        // Also must not be a reserved word
	int size=reservedWords.size();
	for(int c=0;c<size;c++) {
            if(name.equalsIgnoreCase(reservedWords.get(c).toString())) return false;
	}
        return true;
    }
}