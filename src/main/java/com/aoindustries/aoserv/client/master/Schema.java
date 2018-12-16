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
package com.aoindustries.aoserv.client.master;

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

	private final AdministratorPermissionTable administratorPermissionTable;
	public AdministratorPermissionTable getBusinessAdministratorPermissions() {return administratorPermissionTable;}

	private final ProcessTable processTable;
	public ProcessTable getMasterProcesses() {return processTable;}

	private final ServerStatTable serverStatTable;
	public ServerStatTable getMasterServerStats() {return serverStatTable;}

	private final PermissionTable permissionTable;
	public PermissionTable getAoservPermissions() {return permissionTable;}

	private final UserTable userTable;
	public UserTable getMasterUsers() {return userTable;}

	private final UserAclTable userAclTable;
	public UserAclTable getMasterHosts() {return userAclTable;}

	private final UserHostTable userHostTable;
	public UserHostTable getMasterServers() {return userHostTable;}

	final List<? extends AOServTable<?,?>> tables;

	public Schema(AOServConnector connector) throws IOException {
		super(connector);

		ArrayList<AOServTable<?,?>> newTables = new ArrayList<>();
		newTables.add(administratorPermissionTable = new AdministratorPermissionTable(connector));
		newTables.add(processTable = new ProcessTable(connector));
		newTables.add(serverStatTable = new ServerStatTable(connector));
		newTables.add(permissionTable = new PermissionTable(connector));
		newTables.add(userTable = new com.aoindustries.aoserv.client.master.UserTable(connector));
		newTables.add(userAclTable = new UserAclTable(connector));
		newTables.add(userHostTable = new UserHostTable(connector));
		newTables.trimToSize();
		tables = Collections.unmodifiableList(newTables);
	}

	@Override
	public List<? extends AOServTable<?,?>> getTables() {
		return tables;
	}

	@Override
	public String getName() {
		return "master";
	}
}
