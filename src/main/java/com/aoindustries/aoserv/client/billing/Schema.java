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
package com.aoindustries.aoserv.client.billing;

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

	private final MonthlyChargeTable monthlyChargeTable;
	public MonthlyChargeTable getMonthlyCharges() {return monthlyChargeTable;}

	private final NoticeLogTable noticeLogTable;
	public NoticeLogTable getNoticeLogs() {return noticeLogTable;}

	private final NoticeTypeTable noticeTypeTable;
	public NoticeTypeTable getNoticeTypes() {return noticeTypeTable;}

	private final PackageTable packageTable;
	public PackageTable getPackages() {return packageTable;}

	private final PackageCategoryTable packageCategoryTable;
	public PackageCategoryTable getPackageCategories() {return packageCategoryTable;}

	private final PackageDefinitionTable packageDefinitionTable;
	public PackageDefinitionTable getPackageDefinitions() {return packageDefinitionTable;}

	private final PackageDefinitionLimitTable packageDefinitionLimitTable;
	public PackageDefinitionLimitTable getPackageDefinitionLimits() {return packageDefinitionLimitTable;}

	private final ResourceTable resourceTable;
	public ResourceTable getResources() {return resourceTable;}

	private final TransactionTable transactionTable;
	public TransactionTable getTransactions() {return transactionTable;}

	private final TransactionTypeTable transactionTypeTable;
	public TransactionTypeTable getTransactionTypes() {return transactionTypeTable;}

	private final WhoisHistoryTable whoisHistoryTable;
	public WhoisHistoryTable getWhoisHistory() {return whoisHistoryTable;}

	private final WhoisHistoryAccountTable whoisHistoryAccountTable;
	public WhoisHistoryAccountTable getWhoisHistoryAccount() {return whoisHistoryAccountTable;}

	final List<? extends AOServTable<?,?>> tables;

	public Schema(AOServConnector connector) throws IOException {
		super(connector);

		ArrayList<AOServTable<?,?>> newTables = new ArrayList<>();
		newTables.add(monthlyChargeTable = new MonthlyChargeTable(connector));
		newTables.add(noticeLogTable = new NoticeLogTable(connector));
		newTables.add(noticeTypeTable = new NoticeTypeTable(connector));
		newTables.add(packageTable = new PackageTable(connector));
		newTables.add(packageCategoryTable = new PackageCategoryTable(connector));
		newTables.add(packageDefinitionTable = new PackageDefinitionTable(connector));
		newTables.add(packageDefinitionLimitTable = new PackageDefinitionLimitTable(connector));
		newTables.add(resourceTable = new ResourceTable(connector));
		newTables.add(transactionTable = new TransactionTable(connector));
		newTables.add(transactionTypeTable = new TransactionTypeTable(connector));
		newTables.add(whoisHistoryTable = new WhoisHistoryTable(connector));
		newTables.add(whoisHistoryAccountTable = new WhoisHistoryAccountTable(connector));
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
