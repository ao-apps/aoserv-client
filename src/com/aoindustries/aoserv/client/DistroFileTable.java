/*
 * Copyright 2001-2012 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.io.*;
import java.io.*;
import java.sql.*;

/**
 * For AO Industries use only.
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

    public DistroFile get(Object pkey) throws IOException, SQLException {
        return getUniqueRow(DistroFile.COLUMN_PKEY, pkey);
    }

    public DistroFile get(int pkey) throws IOException, SQLException {
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

    @Override
    public int getCachedRowCount() throws IOException, SQLException {
        if(isLoaded()) return super.getCachedRowCount();
        else return connector.requestIntQuery(true, AOServProtocol.CommandID.GET_CACHED_ROW_COUNT, SchemaTable.TableID.DISTRO_FILES);
    }

    @Override
    public int size() throws IOException, SQLException {
        if(isLoaded()) return super.size();
        else return connector.requestIntQuery(true, AOServProtocol.CommandID.GET_ROW_COUNT, SchemaTable.TableID.DISTRO_FILES);
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.DISTRO_FILES;
    }

    @Override
    boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
        String command=args[0];
        if(command.equalsIgnoreCase(AOSHCommand.START_DISTRO)) {
            if(AOSH.checkParamCount(AOSHCommand.START_DISTRO, args, 2, err)) {
                connector.getSimpleAOClient().startDistro(
                    args[1],
                    AOSH.parseBoolean(args[2], "include_user")
                );
            }
            return true;
        }
        return false;
    }

    void startDistro(AOServer server, boolean includeUser) throws IOException, SQLException {
        connector.requestUpdate(
            true,
            AOServProtocol.CommandID.START_DISTRO,
            server.pkey,
            includeUser
        );
    }
}