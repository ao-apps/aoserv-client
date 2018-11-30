/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2012, 2016, 2017, 2018  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.linux;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableGroupIdKey;
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.aosh.AOSHCommand;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.schema.AOServProtocol;
import com.aoindustries.aoserv.client.schema.SchemaTable;
import com.aoindustries.aoserv.client.validator.GroupId;
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.validation.ValidationResult;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  LinuxGroup
 *
 * @author  AO Industries, Inc.
 */
final public class LinuxGroupTable extends CachedTableGroupIdKey<LinuxGroup> {

	public LinuxGroupTable(AOServConnector connector) {
		super(connector, LinuxGroup.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(LinuxGroup.COLUMN_NAME_name, ASCENDING)
	};
	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	public void addLinuxGroup(GroupId name, Package packageObject, String type) throws IOException, SQLException {
		connector.requestUpdateIL(
			true,
			AOServProtocol.CommandID.ADD,
			SchemaTable.TableID.LINUX_GROUPS,
			name,
			packageObject.getName(),
			type
		);
	}

	@Override
	public LinuxGroup get(GroupId name) throws IOException, SQLException {
		return getUniqueRow(LinuxGroup.COLUMN_NAME, name);
	}

	public List<LinuxGroup> getLinuxGroups(Package pack) throws IOException, SQLException {
		return getIndexedRows(LinuxGroup.COLUMN_PACKAGE, pack.getName());
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.LINUX_GROUPS;
	}

	@Override
	public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(AOSHCommand.ADD_LINUX_GROUP)) {
			if(AOSH.checkParamCount(AOSHCommand.ADD_LINUX_GROUP, args, 3, err)) {
				connector.getSimpleAOClient().addLinuxGroup(
					AOSH.parseGroupId(args[1], "group"),
					AOSH.parseAccountingCode(args[2], "package"),
					args[3]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.CHECK_LINUX_GROUP_NAME)) {
			if(AOSH.checkParamCount(AOSHCommand.CHECK_LINUX_GROUP_NAME, args, 1, err)) {
				ValidationResult validationResult = GroupId.validate(args[1]);
				out.println(validationResult.isValid());
				out.flush();
				if(!validationResult.isValid()) {
					err.print("aosh: "+AOSHCommand.CHECK_LINUX_GROUP_NAME+": ");
					err.println(validationResult.toString());
					err.flush();
				}
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.IS_LINUX_GROUP_NAME_AVAILABLE)) {
			if(AOSH.checkParamCount(AOSHCommand.IS_LINUX_GROUP_NAME_AVAILABLE, args, 1, err)) {
				try {
					out.println(
						connector.getSimpleAOClient().isLinuxGroupNameAvailable(
							AOSH.parseGroupId(args[1], "groupname")
						)
					);
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
					AOSH.parseGroupId(args[1], "group")
				);
			}
			return true;
		}
		return false;
	}

	public boolean isLinuxGroupNameAvailable(GroupId groupname) throws SQLException, IOException {
		return connector.requestBooleanQuery(true, AOServProtocol.CommandID.IS_LINUX_GROUP_NAME_AVAILABLE, groupname);
	}
}
