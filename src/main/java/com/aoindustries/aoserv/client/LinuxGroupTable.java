/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2001-2012, 2016  AO Industries, Inc.
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
import java.util.List;

/**
 * @see  LinuxGroup
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

	void addLinuxGroup(String name, Package packageObject, String type) throws IOException, SQLException {
		connector.requestUpdateIL(
			true,
			AOServProtocol.CommandID.ADD,
			SchemaTable.TableID.LINUX_GROUPS,
			name,
			packageObject.name,
			type
		);
	}

	@Override
	public LinuxGroup get(String name) throws IOException, SQLException {
		return getUniqueRow(LinuxGroup.COLUMN_NAME, name);
	}

	List<LinuxGroup> getLinuxGroups(Package pack) throws IOException, SQLException {
		return getIndexedRows(LinuxGroup.COLUMN_PACKAGE, pack.name);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.LINUX_GROUPS;
	}

	@Override
	boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(AOSHCommand.ADD_LINUX_GROUP)) {
			if(AOSH.checkParamCount(AOSHCommand.ADD_LINUX_GROUP, args, 3, err)) {
				connector.getSimpleAOClient().addLinuxGroup(
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
					out.println(connector.getSimpleAOClient().isLinuxGroupNameAvailable(args[1]));
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
				connector.getSimpleAOClient().removeLinuxGroup(
					args[1]
				);
			}
			return true;
		}
		return false;
	}

	public boolean isLinuxGroupNameAvailable(String groupname) throws SQLException, IOException {
		if(!LinuxGroup.isValidGroupname(groupname)) throw new SQLException("Invalid groupname: "+groupname);
		return connector.requestBooleanQuery(true, AOServProtocol.CommandID.IS_LINUX_GROUP_NAME_AVAILABLE, groupname);
	}
}
