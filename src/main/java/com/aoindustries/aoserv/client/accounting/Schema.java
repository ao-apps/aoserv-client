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
package com.aoindustries.aoserv.client.accounting;

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

	private final BankTable bankTable;
	public BankTable getBanks() {
		return bankTable;
	}

	private final BankAccountTable bankAccountTable;
	public BankAccountTable getBankAccounts() {
		return bankAccountTable;
	}

	private final BankTransactionTable bankTransactionTable;
	public BankTransactionTable getBankTransactions() {
		return bankTransactionTable;
	}

	private final BankTransactionTypeTable bankTransactionTypeTable;
	public BankTransactionTypeTable getBankTransactionTypes() {
		return bankTransactionTypeTable;
	}

	private final ExpenseCategoryTable expenseCategoryTable;
	public ExpenseCategoryTable getExpenseCategories() {
		return expenseCategoryTable;
	}

	final List<? extends AOServTable<?,?>> tables;

	public Schema(AOServConnector connector) throws IOException {
		super(connector);

		ArrayList<AOServTable<?,?>> newTables = new ArrayList<>();
		newTables.add(bankAccountTable = new BankAccountTable(connector));
		newTables.add(bankTransactionTypeTable = new BankTransactionTypeTable(connector));
		newTables.add(bankTransactionTable = new BankTransactionTable(connector));
		newTables.add(bankTable = new BankTable(connector));
		newTables.add(expenseCategoryTable = new ExpenseCategoryTable(connector));
		newTables.trimToSize();
		tables = Collections.unmodifiableList(newTables);
	}

	@Override
	public List<? extends AOServTable<?,?>> getTables() {
		return tables;
	}

	@Override
	public String getName() {
		return "accounting";
	}
}
