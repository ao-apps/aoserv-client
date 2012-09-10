/*
 * Copyright 2008-2012 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.io.TerminalWriter;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;

/**
 * @see  VirtualServer
 *
 * @author  AO Industries, Inc.
 */
final public class VirtualServerTable extends CachedTableIntegerKey<VirtualServer> {

    VirtualServerTable(AOServConnector connector) {
        super(connector, VirtualServer.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(VirtualServer.COLUMN_SERVER_name+'.'+Server.COLUMN_PACKAGE_name+'.'+Package.COLUMN_NAME_name, ASCENDING),
        new OrderBy(VirtualServer.COLUMN_SERVER_name+'.'+Server.COLUMN_NAME_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public VirtualServer get(int server) throws IOException, SQLException {
        return getUniqueRow(VirtualServer.COLUMN_SERVER, server);
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.VIRTUAL_SERVERS;
    }

    @Override
    boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.CREATE_VIRTUAL_SERVER)) {
            if(AOSH.checkParamCount(AOSHCommand.CREATE_VIRTUAL_SERVER, args, 1, err)) {
                out.print(connector.getSimpleAOClient().createVirtualServer(args[1]));
                out.flush();
            }
            return true;
	}
	if(command.equalsIgnoreCase(AOSHCommand.REBOOT_VIRTUAL_SERVER)) {
            if(AOSH.checkParamCount(AOSHCommand.REBOOT_VIRTUAL_SERVER, args, 1, err)) {
                out.print(connector.getSimpleAOClient().rebootVirtualServer(args[1]));
                out.flush();
            }
            return true;
	}
	if(command.equalsIgnoreCase(AOSHCommand.SHUTDOWN_VIRTUAL_SERVER)) {
            if(AOSH.checkParamCount(AOSHCommand.SHUTDOWN_VIRTUAL_SERVER, args, 1, err)) {
                out.print(connector.getSimpleAOClient().shutdownVirtualServer(args[1]));
                out.flush();
            }
            return true;
	}
	if(command.equalsIgnoreCase(AOSHCommand.DESTROY_VIRTUAL_SERVER)) {
            if(AOSH.checkParamCount(AOSHCommand.DESTROY_VIRTUAL_SERVER, args, 1, err)) {
                out.print(connector.getSimpleAOClient().destroyVirtualServer(args[1]));
                out.flush();
            }
            return true;
	}
	if(command.equalsIgnoreCase(AOSHCommand.PAUSE_VIRTUAL_SERVER)) {
            if(AOSH.checkParamCount(AOSHCommand.PAUSE_VIRTUAL_SERVER, args, 1, err)) {
                out.print(connector.getSimpleAOClient().pauseVirtualServer(args[1]));
                out.flush();
            }
            return true;
	}
	if(command.equalsIgnoreCase(AOSHCommand.UNPAUSE_VIRTUAL_SERVER)) {
            if(AOSH.checkParamCount(AOSHCommand.UNPAUSE_VIRTUAL_SERVER, args, 1, err)) {
                out.print(connector.getSimpleAOClient().unpauseVirtualServer(args[1]));
                out.flush();
            }
            return true;
	}
	if(command.equalsIgnoreCase(AOSHCommand.GET_VIRTUAL_SERVER_STATUS)) {
            if(AOSH.checkParamCount(AOSHCommand.GET_VIRTUAL_SERVER_STATUS, args, 1, err)) {
                out.println(
                    VirtualServer.getStatusList(
                        connector.getSimpleAOClient().getVirtualServerStatus(args[1])
                    )
                );
                out.flush();
            }
            return true;
	}
	return false;
    }
}
