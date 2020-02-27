/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2018, 2019, 2020  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.billing;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.AOServTable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author  AO Industries, Inc.
 */
public class Schema extends com.aoindustries.aoserv.client.Schema {

	private final CurrencyTable Currency;
	public CurrencyTable getCurrency() {return Currency;}

	private final MonthlyChargeTable MonthlyCharge;
	public MonthlyChargeTable getMonthlyCharge() {return MonthlyCharge;}

	private final NoticeLogTable NoticeLog;
	public NoticeLogTable getNoticeLog() {return NoticeLog;}

	private final NoticeLogBalanceTable NoticeLogBalance;
	public NoticeLogBalanceTable getNoticeLogBalance() {return NoticeLogBalance;}

	private final NoticeTypeTable NoticeType;
	public NoticeTypeTable getNoticeType() {return NoticeType;}

	private final PackageTable Package;
	public PackageTable getPackage() {return Package;}

	private final PackageCategoryTable PackageCategory;
	public PackageCategoryTable getPackageCategory() {return PackageCategory;}

	private final PackageDefinitionTable PackageDefinition;
	public PackageDefinitionTable getPackageDefinition() {return PackageDefinition;}

	private final PackageDefinitionLimitTable PackageDefinitionLimit;
	public PackageDefinitionLimitTable getPackageDefinitionLimit() {return PackageDefinitionLimit;}

	private final ResourceTable Resource;
	public ResourceTable getResource() {return Resource;}

	private final TransactionTable Transaction;
	public TransactionTable getTransaction() {return Transaction;}

	private final TransactionTypeTable TransactionType;
	public TransactionTypeTable getTransactionType() {return TransactionType;}

	private final WhoisHistoryTable WhoisHistory;
	public WhoisHistoryTable getWhoisHistory() {return WhoisHistory;}

	private final WhoisHistoryAccountTable WhoisHistoryAccount;
	public WhoisHistoryAccountTable getWhoisHistoryAccount() {return WhoisHistoryAccount;}

	private final List<? extends AOServTable<?,?>> tables;

	public Schema(AOServConnector connector) {
		super(connector);

		ArrayList<AOServTable<?,?>> newTables = new ArrayList<>();
		newTables.add(Currency = new CurrencyTable(connector));
		newTables.add(MonthlyCharge = new MonthlyChargeTable(connector));
		newTables.add(NoticeLog = new NoticeLogTable(connector));
		newTables.add(NoticeLogBalance = new NoticeLogBalanceTable(connector));
		newTables.add(NoticeType = new NoticeTypeTable(connector));
		newTables.add(Package = new PackageTable(connector));
		newTables.add(PackageCategory = new PackageCategoryTable(connector));
		newTables.add(PackageDefinition = new PackageDefinitionTable(connector));
		newTables.add(PackageDefinitionLimit = new PackageDefinitionLimitTable(connector));
		newTables.add(Resource = new ResourceTable(connector));
		newTables.add(Transaction = new TransactionTable(connector));
		newTables.add(TransactionType = new TransactionTypeTable(connector));
		newTables.add(WhoisHistory = new WhoisHistoryTable(connector));
		newTables.add(WhoisHistoryAccount = new WhoisHistoryAccountTable(connector));
		newTables.trimToSize();
		tables = Collections.unmodifiableList(newTables);
	}

	@Override
	public List<? extends AOServTable<?,?>> getTables() {
		return tables;
	}

	@Override
	public String getName() {
		return "billing";
	}
}
