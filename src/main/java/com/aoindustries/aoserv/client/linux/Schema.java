/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2018  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.AOServTable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author  AO Industries, Inc.
 */
public class Schema extends com.aoindustries.aoserv.client.Schema {

	private final DaemonAclTable DaemonAcl;
	public DaemonAclTable getDaemonAcl() {return DaemonAcl;}

	private final GroupTable Group;
	public GroupTable getGroup() {return Group;}

	private final GroupServerTable GroupServer;
	public GroupServerTable getGroupServer() {return GroupServer;}

	private final GroupTypeTable GroupType;
	public GroupTypeTable getGroupType() {return GroupType;}

	private final GroupUserTable GroupUser;
	public GroupUserTable getGroupUser() {return GroupUser;}

	private final ServerTable Server;
	public ServerTable getServer() {return Server;}

	private final ShellTable Shell;
	public ShellTable getShell() {return Shell;}

	private final TimeZoneTable TimeZone;
	public TimeZoneTable getTimeZone() {return TimeZone;}

	private final UserTable User;
	public UserTable getUser() {return User;}

	private final UserServerTable UserServer;
	public UserServerTable getUserServer() {return UserServer;}

	private final UserTypeTable UserType;
	public UserTypeTable getUserType() {return UserType;}

	final List<? extends AOServTable<?,?>> tables;

	public Schema(AOServConnector connector) throws IOException {
		super(connector);

		ArrayList<AOServTable<?,?>> newTables = new ArrayList<>();
		newTables.add(DaemonAcl = new DaemonAclTable(connector));
		newTables.add(Group = new GroupTable(connector));
		newTables.add(GroupServer = new GroupServerTable(connector));
		newTables.add(GroupType = new GroupTypeTable(connector));
		newTables.add(GroupUser = new GroupUserTable(connector));
		newTables.add(Server = new ServerTable(connector));
		newTables.add(Shell = new ShellTable(connector));
		newTables.add(TimeZone = new TimeZoneTable(connector));
		newTables.add(User = new UserTable(connector));
		newTables.add(UserServer = new UserServerTable(connector));
		newTables.add(UserType = new UserTypeTable(connector));
		newTables.trimToSize();
		tables = Collections.unmodifiableList(newTables);
	}

	@Override
	public List<? extends AOServTable<?,?>> getTables() {
		return tables;
	}

	@Override
	public String getName() {
		return "linux";
	}
}
