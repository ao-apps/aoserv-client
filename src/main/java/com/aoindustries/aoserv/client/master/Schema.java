/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2018, 2020, 2021  AO Industries, Inc.
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
 * along with aoserv-client.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.aoindustries.aoserv.client.master;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.AOServTable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author  AO Industries, Inc.
 */
public class Schema extends com.aoindustries.aoserv.client.Schema {

	private final AdministratorPermissionTable AdministratorPermission;
	public AdministratorPermissionTable getAdministratorPermission() {return AdministratorPermission;}

	private final ProcessTable Process;
	public ProcessTable getProcess() {return Process;}

	private final ServerStatTable ServerStat;
	public ServerStatTable getServerStat() {return ServerStat;}

	private final PermissionTable Permission;
	public PermissionTable getPermission() {return Permission;}

	private final UserTable User;
	public UserTable getUser() {return User;}

	private final UserAclTable UserAcl;
	public UserAclTable getUserAcl() {return UserAcl;}

	private final UserHostTable UserHost;
	public UserHostTable getUserHost() {return UserHost;}

	private final List<? extends AOServTable<?, ?>> tables;

	public Schema(AOServConnector connector) {
		super(connector);

		ArrayList<AOServTable<?, ?>> newTables = new ArrayList<>();
		newTables.add(AdministratorPermission = new AdministratorPermissionTable(connector));
		newTables.add(Process = new ProcessTable(connector));
		newTables.add(ServerStat = new ServerStatTable(connector));
		newTables.add(Permission = new PermissionTable(connector));
		newTables.add(User = new com.aoindustries.aoserv.client.master.UserTable(connector));
		newTables.add(UserAcl = new UserAclTable(connector));
		newTables.add(UserHost = new UserHostTable(connector));
		newTables.trimToSize();
		tables = Collections.unmodifiableList(newTables);
	}

	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField") // Returning unmodifiable
	public List<? extends AOServTable<?, ?>> getTables() {
		return tables;
	}

	@Override
	public String getName() {
		return "master";
	}
}
