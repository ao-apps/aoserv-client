/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2008-2012, 2014, 2016, 2017  AO Industries, Inc.
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

	@Override
	public VirtualServer get(int server) throws IOException, SQLException {
		return getUniqueRow(VirtualServer.COLUMN_SERVER, server);
	}

	@Override
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
		if(command.equalsIgnoreCase(AOSHCommand.GET_PRIMARY_PHYSICAL_SERVER)) {
			if(AOSH.checkParamCount(AOSHCommand.GET_PRIMARY_PHYSICAL_SERVER, args, 1, err)) {
				out.println(
					connector.getSimpleAOClient().getPrimaryVirtualServer(args[1])
				);
				out.flush();
			}
			return true;
		}
		if(command.equalsIgnoreCase(AOSHCommand.GET_SECONDARY_PHYSICAL_SERVER)) {
			if(AOSH.checkParamCount(AOSHCommand.GET_SECONDARY_PHYSICAL_SERVER, args, 1, err)) {
				out.println(
					connector.getSimpleAOClient().getSecondaryVirtualServer(args[1])
				);
				out.flush();
			}
			return true;
		}
		return false;
	}
}
