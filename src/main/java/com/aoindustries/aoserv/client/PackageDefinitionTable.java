/*
 * Copyright 2005-2013, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

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

	PackageDefinitionTable(AOServConnector connector) {
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
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	int addPackageDefinition(
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
			new AOServConnector.ResultRequest<Integer>() {
				int pkey;
				IntList invalidateList;

				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(AOServProtocol.CommandID.ADD.ordinal());
					out.writeCompressedInt(SchemaTable.TableID.PACKAGE_DEFINITIONS.ordinal());
					out.writeUTF(business.pkey.toString());
					out.writeUTF(category.pkey);
					out.writeUTF(name);
					out.writeUTF(version);
					out.writeUTF(display);
					out.writeUTF(description);
					out.writeCompressedInt(setupFee);
					out.writeBoolean(setupFeeTransactionType!=null);
					if(setupFeeTransactionType!=null) out.writeUTF(setupFeeTransactionType.pkey);
					out.writeCompressedInt(monthlyRate);
					out.writeUTF(monthlyRateTransactionType.pkey);
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

	PackageDefinition getPackageDefinition(Business business, PackageCategory category, String name, String version) throws IOException, SQLException {
		AccountingCode accounting=business.pkey;
		String categoryName=category.pkey;
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

	List<PackageDefinition> getPackageDefinitions(Business business, PackageCategory category) throws IOException, SQLException {
		AccountingCode accounting=business.pkey;
		String categoryName=category.pkey;

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
