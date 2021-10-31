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
package com.aoindustries.aoserv.client.accounting;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.AOServTable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author  AO Industries, Inc.
 */
public class Schema extends com.aoindustries.aoserv.client.Schema {

	private final BankTable Bank;
	public BankTable getBank() {return Bank;}

	private final BankAccountTable BankAccount;
	public BankAccountTable getBankAccount() {return BankAccount;}

	private final BankTransactionTable BankTransaction;
	public BankTransactionTable getBankTransaction() {return BankTransaction;}

	private final BankTransactionTypeTable BankTransactionType;
	public BankTransactionTypeTable getBankTransactionType() {return BankTransactionType;}

	private final ExpenseCategoryTable ExpenseCategory;
	public ExpenseCategoryTable getExpenseCategory() {return ExpenseCategory;}

	private final List<? extends AOServTable<?, ?>> tables;

	public Schema(AOServConnector connector) {
		super(connector);

		ArrayList<AOServTable<?, ?>> newTables = new ArrayList<>();
		newTables.add(BankAccount = new BankAccountTable(connector));
		newTables.add(BankTransactionType = new BankTransactionTypeTable(connector));
		newTables.add(BankTransaction = new BankTransactionTable(connector));
		newTables.add(Bank = new BankTable(connector));
		newTables.add(ExpenseCategory = new ExpenseCategoryTable(connector));
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
		return "accounting";
	}
}
