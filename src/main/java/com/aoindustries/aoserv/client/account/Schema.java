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
package com.aoindustries.aoserv.client.account;

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

	private final AccountTable accountTable;
	public AccountTable getBusinesses() {
		return accountTable;
	}

	private final AccountHostTable accountHostTable;
	public AccountHostTable getBusinessServers() {
		return accountHostTable;
	}

	private final AdministratorTable administratorTable;
	public AdministratorTable getBusinessAdministrators() {
		return administratorTable;
	}

	private final DisableLogTable disableLogTable;
	public DisableLogTable getDisableLogs() {
		return disableLogTable;
	}

	private final ProfileTable profileTable;
	public ProfileTable getBusinessProfiles() {
		return profileTable;
	}

	private final UsStateTable usStateTable;
	public UsStateTable getUsStates() {
		return usStateTable;
	}

	private final UsernameTable usernameTable;
	public UsernameTable getUsernames() {
		return usernameTable;
	}

	final List<? extends AOServTable<?,?>> tables;

	public Schema(AOServConnector connector) throws IOException {
		super(connector);

		ArrayList<AOServTable<?,?>> newTables = new ArrayList<>();
		newTables.add(accountTable = new AccountTable(connector));
		newTables.add(accountHostTable = new AccountHostTable(connector));
		newTables.add(administratorTable = new AdministratorTable(connector));
		newTables.add(disableLogTable = new DisableLogTable(connector));
		newTables.add(profileTable = new ProfileTable(connector));
		newTables.add(usStateTable = new UsStateTable(connector));
		newTables.add(usernameTable = new UsernameTable(connector));
		newTables.trimToSize();
		tables = Collections.unmodifiableList(newTables);
	}

	@Override
	public List<? extends AOServTable<?,?>> getTables() {
		return tables;
	}

	@Override
	public String getName() {
		return "account";
	}
}
