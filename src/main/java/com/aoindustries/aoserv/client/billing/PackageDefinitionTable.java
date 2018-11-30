/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2005-2013, 2016, 2017, 2018  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.account.Business;
import com.aoindustries.aoserv.client.schema.AOServProtocol;
import com.aoindustries.aoserv.client.schema.SchemaTable;
import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.util.IntList;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see  PackageDefinition
 *
 * @author  AO Industries, Inc.
 */
public final class PackageDefinitionTable extends CachedTableIntegerKey<PackageDefinition> {

	public PackageDefinitionTable(AOServConnector connector) {
		super(connector, PackageDefinition.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(PackageDefinition.COLUMN_ACCOUNTING_name, ASCENDING),
		new OrderBy(PackageDefinition.COLUMN_CATEGORY_name, ASCENDING),
		new OrderBy(PackageDefinition.COLUMN_MONTHLY_RATE_name, ASCENDING),
		new OrderBy(PackageDefinition.COLUMN_NAME_name, ASCENDING),
		new OrderBy(PackageDefinition.COLUMN_VERSION_name, ASCENDING)
	};
	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	public int addPackageDefinition(
		final Business business,
		final PackageCategory category,
		final String name,
		final String version,
		final String display,
		final String description,
		final int setupFee,
		final TransactionType setupFeeTransactionType,
		final int monthlyRate,
		final TransactionType monthlyRateTransactionType
	) throws IOException, SQLException {
		return connector.requestResult(
			true,
			AOServProtocol.CommandID.ADD,
			new AOServConnector.ResultRequest<Integer>() {
				int pkey;
				IntList invalidateList;

				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(SchemaTable.TableID.PACKAGE_DEFINITIONS.ordinal());
					out.writeUTF(business.getAccounting().toString());
					out.writeUTF(category.getName());
					out.writeUTF(name);
					out.writeUTF(version);
					out.writeUTF(display);
					out.writeUTF(description);
					out.writeCompressedInt(setupFee);
					out.writeBoolean(setupFeeTransactionType!=null);
					if(setupFeeTransactionType!=null) out.writeUTF(setupFeeTransactionType.getName());
					out.writeCompressedInt(monthlyRate);
					out.writeUTF(monthlyRateTransactionType.getName());
				}

				@Override
				public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AOServProtocol.DONE) {
						pkey=in.readCompressedInt();
						invalidateList=AOServConnector.readInvalidateList(in);
					} else {
						AOServProtocol.checkResult(code, in);
						throw new IOException("Unknown response code: "+code);
					}
				}

				@Override
				public Integer afterRelease() {
					connector.tablesUpdated(invalidateList);
					return pkey;
				}
			}
		);
	}

	@Override
	public PackageDefinition get(int pkey) throws IOException, SQLException {
		return getUniqueRow(PackageDefinition.COLUMN_PKEY, pkey);
	}

	public PackageDefinition getPackageDefinition(Business business, PackageCategory category, String name, String version) throws IOException, SQLException {
		AccountingCode accounting=business.getAccounting();
		String categoryName=category.getName();
		List<PackageDefinition> pds=getRows();
		int size=pds.size();
		for(int c=0;c<size;c++) {
			PackageDefinition pd=pds.get(c);
			if(
				pd.accounting.equals(accounting)
				&& pd.category.equals(categoryName)
				&& pd.name.equals(name)
				&& pd.version.equals(version)
			) return pd;
		}
		return null;
	}

	public List<PackageDefinition> getPackageDefinitions(Business business, PackageCategory category) throws IOException, SQLException {
		AccountingCode accounting=business.getAccounting();
		String categoryName=category.getName();

		List<PackageDefinition> cached = getRows();
		List<PackageDefinition> matches = new ArrayList<>(cached.size());
		int size=cached.size();
		for(int c=0;c<size;c++) {
			PackageDefinition pd=cached.get(c);
			if(
				pd.accounting.equals(accounting)
				&& pd.category.equals(categoryName)
			) matches.add(pd);
		}
		return matches;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.PACKAGE_DEFINITIONS;
	}
}
