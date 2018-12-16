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

	private final DaemonAclTable daemonAclTable;
	public DaemonAclTable getAoServerDaemonHosts() {return daemonAclTable;}

	private final GroupTable groupTable;
	public GroupTable getLinuxGroups() {return groupTable;}

	private final GroupServerTable groupServerTable;
	public GroupServerTable getLinuxServerGroups() {return groupServerTable;}

	private final GroupTypeTable groupTypeTable;
	public GroupTypeTable getLinuxGroupTypes() {return groupTypeTable;}

	private final GroupUserTable groupUserTable;
	public GroupUserTable getLinuxGroupAccounts() {return groupUserTable;}

	private final ServerTable serverTable;
	public ServerTable getAoServers() {return serverTable;}

	private final ShellTable shellTable;
	public ShellTable getShells() {return shellTable;}

	private final TimeZoneTable timeZoneTable;
	public TimeZoneTable getTimeZones() {return timeZoneTable;}

	private final UserTable userTable;
	public UserTable getLinuxAccounts() {return userTable;}

	private final UserServerTable userServerTable;
	public UserServerTable getLinuxServerAccounts() {return userServerTable;}

	private final UserTypeTable userTypeTable;
	public UserTypeTable getLinuxAccountTypes() {return userTypeTable;}

	final List<? extends AOServTable<?,?>> tables;

	public Schema(AOServConnector connector) throws IOException {
		super(connector);

		ArrayList<AOServTable<?,?>> newTables = new ArrayList<>();
		newTables.add(daemonAclTable = new DaemonAclTable(connector));
		newTables.add(groupTable = new GroupTable(connector));
		newTables.add(groupServerTable = new GroupServerTable(connector));
		newTables.add(groupTypeTable = new GroupTypeTable(connector));
		newTables.add(groupUserTable = new GroupUserTable(connector));
		newTables.add(serverTable = new ServerTable(connector));
		newTables.add(shellTable = new ShellTable(connector));
		newTables.add(timeZoneTable = new TimeZoneTable(connector));
		newTables.add(userTable = new UserTable(connector));
		newTables.add(userServerTable = new UserServerTable(connector));
		newTables.add(userTypeTable = new UserTypeTable(connector));
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
