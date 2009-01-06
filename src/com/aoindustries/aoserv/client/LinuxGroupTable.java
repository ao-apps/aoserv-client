package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.WrappedException;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  LinuxGroup
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class LinuxGroupTable extends CachedTableStringKey<LinuxGroup> {

    LinuxGroupTable(AOServConnector connector) {
	super(connector, LinuxGroup.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(LinuxGroup.COLUMN_NAME_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    void addLinuxGroup(String name, Package packageObject, String type) {
        connector.requestUpdateIL(
            AOServProtocol.CommandID.ADD,
            SchemaTable.TableID.LINUX_GROUPS,
            name,
            packageObject.name,
            type
        );
    }

    public LinuxGroup get(Object pkey) {
	return getUniqueRow(LinuxGroup.COLUMN_NAME, pkey);
    }

    List<LinuxGroup> getLinuxGroups(Package pack) {
        return getIndexedRows(LinuxGroup.COLUMN_PACKAGE, pack.name);
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.LINUX_GROUPS;
    }

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
        String command=args[0];
        if(command.equalsIgnoreCase(AOSHCommand.ADD_LINUX_GROUP)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_LINUX_GROUP, args, 3, err)) {
                connector.simpleAOClient.addLinuxGroup(
                    args[1],
                    args[2],
                    args[3]
                );
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.CHECK_LINUX_GROUP_NAME)) {
            if(AOSH.checkParamCount(AOSHCommand.CHECK_LINUX_GROUP_NAME, args, 1, err)) {
                try {
                    SimpleAOClient.checkLinuxGroupname(args[1]);
                    out.println("true");
                } catch(IllegalArgumentException iae) {
                    out.print("aosh: "+AOSHCommand.CHECK_LINUX_GROUP_NAME+": ");
                    out.println(iae.getMessage());
                }
                out.flush();
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.IS_LINUX_GROUP_NAME_AVAILABLE)) {
            if(AOSH.checkParamCount(AOSHCommand.IS_LINUX_GROUP_NAME_AVAILABLE, args, 1, err)) {
                try {
                    out.println(connector.simpleAOClient.isLinuxGroupNameAvailable(args[1]));
                    out.flush();
                } catch(IllegalArgumentException iae) {
                    err.print("aosh: "+AOSHCommand.IS_LINUX_GROUP_NAME_AVAILABLE+": ");
                    err.println(iae.getMessage());
                    err.flush();
                }
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_LINUX_GROUP)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_LINUX_GROUP, args, 1, err)) {
                connector.simpleAOClient.removeLinuxGroup(
                    args[1]
                );
            }
            return true;
        }
        return false;
    }

    public boolean isLinuxGroupNameAvailable(String groupname) {
        if(!LinuxGroup.isValidGroupname(groupname)) throw new WrappedException(new SQLException("Invalid groupname: "+groupname));
        return connector.requestBooleanQuery(AOServProtocol.CommandID.IS_LINUX_GROUP_NAME_AVAILABLE, groupname);
    }
}