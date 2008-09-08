package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.profiler.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * For AO Industries use only.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class DistroFileTable extends FilesystemCachedTable<Integer,DistroFile> {

    DistroFileTable(AOServConnector connector) {
	super(connector, DistroFile.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(DistroFile.COLUMN_PATH_name, ASCENDING),
        new OrderBy(DistroFile.COLUMN_OPERATING_SYSTEM_VERSION_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public DistroFile get(Object pkey) {
        return getUniqueRow(DistroFile.COLUMN_PKEY, pkey);
    }

    public DistroFile get(int pkey) {
        return getUniqueRow(DistroFile.COLUMN_PKEY, pkey);
    }

    public int getRecordLength() {
        return
              4                                             // pkey
            + 4                                             // operating_system_version
            + 4+DistroFile.MAX_PATH_LENGTH*2                // path
            + 1                                             // optional
            + 4+DistroFile.MAX_TYPE_LENGTH*2                // type
            + 8                                             // mode
            + 4+DistroFile.MAX_LINUX_ACCOUNT_LENGTH*2       // linux_account
            + 4+DistroFile.MAX_LINUX_GROUP_LENGTH*2         // linux_group
            + 8                                             // size
            + 1+8+8                                         // file_md5
            + 1+4+DistroFile.MAX_SYMLINK_TARGET_LENGTH*2    // symlink_target
        ;
    }

    public int getCachedRowCount() {
        Profiler.startProfile(Profiler.UNKNOWN, DistroFileTable.class, "getCachedRowCount()", null);
        try {
            if(isLoaded()) return super.getCachedRowCount();
            else return connector.requestIntQuery(AOServProtocol.CommandID.GET_CACHED_ROW_COUNT, SchemaTable.TableID.DISTRO_FILES);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public int size() {
        Profiler.startProfile(Profiler.UNKNOWN, DistroFileTable.class, "getRowCount()", null);
        try {
            if(isLoaded()) return super.size();
            else return connector.requestIntQuery(AOServProtocol.CommandID.GET_ROW_COUNT, SchemaTable.TableID.DISTRO_FILES);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.DISTRO_FILES;
    }

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
        Profiler.startProfile(Profiler.UNKNOWN, DistroFileTable.class, "handleCommand(String[],InputStream,TerminalWriter,TerminalWriter,boolean)", null);
        try {
            String command=args[0];
            if(command.equalsIgnoreCase(AOSHCommand.START_DISTRO)) {
                if(AOSH.checkParamCount(AOSHCommand.START_DISTRO, args, 2, err)) {
                    connector.simpleAOClient.startDistro(
                        args[1],
                        AOSH.parseBoolean(args[2], "include_user")
                    );
                }
                return true;
            }
            return false;
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    void startDistro(AOServer server, boolean includeUser) {
        Profiler.startProfile(Profiler.UNKNOWN, DistroFileTable.class, "startDistro(AOServer,boolean)", null);
        try {
            connector.requestUpdate(
                AOServProtocol.CommandID.START_DISTRO,
                server.pkey,
                includeUser
            );
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }
}