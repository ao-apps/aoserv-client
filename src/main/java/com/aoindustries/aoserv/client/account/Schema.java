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
package com.aoindustries.aoserv.client.account;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.AOServTable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author  AO Industries, Inc.
 */
public class Schema extends com.aoindustries.aoserv.client.Schema {

	private final AccountTable Account;
	public AccountTable getAccount() {return Account;}

	private final AccountHostTable AccountHost;
	public AccountHostTable getAccountHost() {return AccountHost;}

	private final AdministratorTable Administrator;
	public AdministratorTable getAdministrator() {return Administrator;}

	private final DisableLogTable DisableLog;
	public DisableLogTable getDisableLog() {return DisableLog;}

	private final ProfileTable Profile;
	public ProfileTable getProfile() {return Profile;}

	private final UsStateTable UsState;
	public UsStateTable getUsState() {return UsState;}

	private final UserTable User;
	public UserTable getUser() {return User;}

	private final List<? extends AOServTable<?, ?>> tables;

	public Schema(AOServConnector connector) {
		super(connector);

		ArrayList<AOServTable<?, ?>> newTables = new ArrayList<>();
		newTables.add(Account = new AccountTable(connector));
		newTables.add(AccountHost = new AccountHostTable(connector));
		newTables.add(Administrator = new AdministratorTable(connector));
		newTables.add(DisableLog = new DisableLogTable(connector));
		newTables.add(Profile = new ProfileTable(connector));
		newTables.add(UsState = new UsStateTable(connector));
		newTables.add(User = new UserTable(connector));
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
		return "account";
	}
}
