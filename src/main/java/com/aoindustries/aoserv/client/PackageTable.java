/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017  AO Industries, Inc.
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
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  Package
 *
 * @author  AO Industries, Inc.
 */
final public class PackageTable extends CachedTableIntegerKey<Package> {

	PackageTable(AOServConnector connector) {
		super(connector, Package.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(Package.COLUMN_NAME_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	int addPackage(
		AccountingCode name,
		Business business,
		PackageDefinition packageDefinition
	) throws IOException, SQLException {
		return connector.requestIntQueryIL(
			true,
			AOServProtocol.CommandID.ADD,
			SchemaTable.TableID.PACKAGES,
			name,
			business.pkey,
			packageDefinition.pkey
		);
	}

	/**
	 * Supports both {@link Integer} (pkey) and {@link AccountingCode} (name) keys.
	 *
	 * @deprecated  Always try to lookup by specific keys; the compiler will help you more when types change.
	 */
	@Deprecated
	@Override
	public Package get(Object pkey) throws IOException, SQLException {
		if(pkey instanceof Integer) return get(((Integer)pkey).intValue());
		if(pkey instanceof AccountingCode) return get((AccountingCode)pkey);
		throw new IllegalArgumentException("pkey must be either an Integer or an AccountingCode");
	}

	@Override
	public Package get(int pkey) throws IOException, SQLException {
		return getUniqueRow(Package.COLUMN_PKEY, pkey);
	}

	public Package get(AccountingCode name) throws IOException, SQLException {
		return getUniqueRow(Package.COLUMN_NAME, name);
	}

	public AccountingCode generatePackageName(AccountingCode template) throws IOException, SQLException {
		try {
			return AccountingCode.valueOf(connector.requestStringQuery(true, AOServProtocol.CommandID.GENERATE_PACKAGE_NAME, template));
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	List<Package> getPackages(Business business) throws IOException, SQLException {
		return getIndexedRows(Package.COLUMN_ACCOUNTING, business.pkey);
	}

	List<Package> getPackages(PackageDefinition pd) throws IOException, SQLException {
		return getIndexedRows(Package.COLUMN_PACKAGE_DEFINITION, pd.pkey);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.PACKAGES;
	}

	@Override
	boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, SQLException, IOException {
		String command=args[0];
		if(command.equalsIgnoreCase(AOSHCommand.ADD_PACKAGE)) {
			if(AOSH.checkParamCount(AOSHCommand.ADD_PACKAGE, args, 3, err)) {
				try {
					out.println(
						connector.getSimpleAOClient().addPackage(
							AOSH.parseAccountingCode(args[1], "package"),
							AOSH.parseAccountingCode(args[2], "business"),
							AOSH.parseInt(args[3], "package_definition")
						)
					);
					out.flush();
				} catch(IllegalArgumentException iae) {
					err.print("aosh: "+AOSHCommand.ADD_PACKAGE+": ");
					err.println(iae.getMessage());
					err.flush();
				}
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.DISABLE_PACKAGE)) {
			if(AOSH.checkParamCount(AOSHCommand.DISABLE_PACKAGE, args, 2, err)) {
				out.println(
					connector.getSimpleAOClient().disablePackage(
						AOSH.parseAccountingCode(args[1], "name"),
						args[2]
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.ENABLE_PACKAGE)) {
			if(AOSH.checkParamCount(AOSHCommand.ENABLE_PACKAGE, args, 1, err)) {
				connector.getSimpleAOClient().enablePackage(
					AOSH.parseAccountingCode(args[1], "name")
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.GENERATE_PACKAGE_NAME)) {
			if(AOSH.checkParamCount(AOSHCommand.GENERATE_PACKAGE_NAME, args, 1, err)) {
				out.println(
					connector.getSimpleAOClient().generatePackageName(
						AOSH.parseAccountingCode(args[1], "template")
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.IS_PACKAGE_NAME_AVAILABLE)) {
			if(AOSH.checkParamCount(AOSHCommand.IS_PACKAGE_NAME_AVAILABLE, args, 1, err)) {
				try {
					out.println(
						connector.getSimpleAOClient().isPackageNameAvailable(
							AOSH.parseAccountingCode(args[1], "package")
						)
					);
					out.flush();
				} catch(IllegalArgumentException iae) {
					err.print("aosh: "+AOSHCommand.IS_PACKAGE_NAME_AVAILABLE+": ");
					err.println(iae.getMessage());
					err.flush();
				}
			}
			return true;
		} else return false;
	}

	public boolean isPackageNameAvailable(AccountingCode packageName) throws SQLException, IOException {
		return connector.requestBooleanQuery(true, AOServProtocol.CommandID.IS_PACKAGE_NAME_AVAILABLE, packageName);
	}
}
