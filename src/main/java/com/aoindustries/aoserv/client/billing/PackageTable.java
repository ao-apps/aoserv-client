/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.billing;

import com.aoapps.hodgepodge.io.TerminalWriter;
import com.aoapps.lang.validation.ValidationException;
import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.aosh.Aosh;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  Package
 *
 * @author  AO Industries, Inc.
 */
public final class PackageTable extends CachedTableIntegerKey<Package> {

  PackageTable(AoservConnector connector) {
    super(connector, Package.class);
  }

  private static final OrderBy[] defaultOrderBy = {
      new OrderBy(Package.COLUMN_NAME_name, ASCENDING)
  };

  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  public int addPackage(
      Account.Name name,
      Account business,
      PackageDefinition packageDefinition
  ) throws IOException, SQLException {
    return connector.requestIntQueryInvalidating(
        true,
        AoservProtocol.CommandId.ADD,
        Table.TableId.PACKAGES,
        name,
        business.getName(),
        packageDefinition.getPkey()
    );
  }

  /**
   * Supports both {@link Integer} (id) and {@link Account.Name} (name) keys.
   *
   * @deprecated  Always try to lookup by specific keys; the compiler will help you more when types change.
   */
  @Deprecated
  @Override
  public Package get(Object pkey) throws IOException, SQLException {
    if (pkey == null) {
      return null;
    }
    if (pkey instanceof Integer) {
      return get(((Integer) pkey).intValue());
    }
    if (pkey instanceof Account.Name) {
      return get((Account.Name) pkey);
    }
    throw new IllegalArgumentException("pkey must be either an Integer or an AccountingCode");
  }

  /**
   * {@inheritDoc}
   *
   * @see  #get(java.lang.Object)
   */
  @Override
  public Package get(int pkey) throws IOException, SQLException {
    return getUniqueRow(Package.COLUMN_PKEY, pkey);
  }

  /**
   * @see  #get(java.lang.Object)
   */
  public Package get(Account.Name name) throws IOException, SQLException {
    return getUniqueRow(Package.COLUMN_NAME, name);
  }

  public Account.Name generatePackageName(Account.Name template) throws IOException, SQLException {
    try {
      return Account.Name.valueOf(connector.requestStringQuery(true, AoservProtocol.CommandId.GENERATE_PACKAGE_NAME, template));
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  public List<Package> getPackages(Account business) throws IOException, SQLException {
    return getIndexedRows(Package.COLUMN_ACCOUNTING, business.getName());
  }

  List<Package> getPackages(PackageDefinition pd) throws IOException, SQLException {
    return getIndexedRows(Package.COLUMN_PACKAGE_DEFINITION, pd.getPkey());
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.PACKAGES;
  }

  @Override
  public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, SQLException, IOException {
    String command = args[0];
    if (command.equalsIgnoreCase(Command.ADD_PACKAGE)) {
      if (Aosh.checkParamCount(Command.ADD_PACKAGE, args, 3, err)) {
        try {
          out.println(
              connector.getSimpleClient().addPackage(
                  Aosh.parseAccountingCode(args[1], "package"),
                  Aosh.parseAccountingCode(args[2], "business"),
                  Aosh.parseInt(args[3], "package_definition")
              )
          );
          out.flush();
        } catch (IllegalArgumentException iae) {
          err.print("aosh: " + Command.ADD_PACKAGE + ": ");
          err.println(iae.getMessage());
          err.flush();
        }
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.DISABLE_PACKAGE)) {
      if (Aosh.checkParamCount(Command.DISABLE_PACKAGE, args, 2, err)) {
        out.println(
            connector.getSimpleClient().disablePackage(
                Aosh.parseAccountingCode(args[1], "name"),
                args[2]
            )
        );
        out.flush();
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.ENABLE_PACKAGE)) {
      if (Aosh.checkParamCount(Command.ENABLE_PACKAGE, args, 1, err)) {
        connector.getSimpleClient().enablePackage(
            Aosh.parseAccountingCode(args[1], "name")
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.GENERATE_PACKAGE_NAME)) {
      if (Aosh.checkParamCount(Command.GENERATE_PACKAGE_NAME, args, 1, err)) {
        out.println(
            connector.getSimpleClient().generatePackageName(
                Aosh.parseAccountingCode(args[1], "template")
            )
        );
        out.flush();
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.IS_PACKAGE_NAME_AVAILABLE)) {
      if (Aosh.checkParamCount(Command.IS_PACKAGE_NAME_AVAILABLE, args, 1, err)) {
        try {
          out.println(
              connector.getSimpleClient().isPackageNameAvailable(
                  Aosh.parseAccountingCode(args[1], "package")
              )
          );
          out.flush();
        } catch (IllegalArgumentException iae) {
          err.print("aosh: " + Command.IS_PACKAGE_NAME_AVAILABLE + ": ");
          err.println(iae.getMessage());
          err.flush();
        }
      }
      return true;
    } else {
      return false;
    }
  }

  public boolean isPackageNameAvailable(Account.Name packageName) throws SQLException, IOException {
    return connector.requestBooleanQuery(true, AoservProtocol.CommandId.IS_PACKAGE_NAME_AVAILABLE, packageName);
  }
}
