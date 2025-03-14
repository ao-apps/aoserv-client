/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2005-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2025  AO Industries, Inc.
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

import com.aoapps.collections.IntList;
import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.i18n.Money;
import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
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

  PackageDefinitionTable(AoservConnector connector) {
    super(connector, PackageDefinition.class);
  }

  private static final OrderBy[] defaultOrderBy = {
      new OrderBy(PackageDefinition.COLUMN_ACCOUNTING_name, ASCENDING),
      new OrderBy(PackageDefinition.COLUMN_CATEGORY_name, ASCENDING),
      new OrderBy(PackageDefinition.COLUMN_monthlyRate_name, ASCENDING),
      new OrderBy(PackageDefinition.COLUMN_NAME_name, ASCENDING),
      new OrderBy(PackageDefinition.COLUMN_VERSION_name, ASCENDING)
  };

  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  public int addPackageDefinition(
      final Account business,
      final PackageCategory category,
      final String name,
      final String version,
      final String display,
      final String description,
      final Money setupFee,
      final TransactionType setupFeeTransactionType,
      final Money monthlyRate,
      final TransactionType monthlyRateTransactionType
  ) throws IOException, SQLException {
    return connector.requestResult(
        true,
        AoservProtocol.CommandId.ADD,
        new AoservConnector.ResultRequest<>() {
          private int pkey;
          private IntList invalidateList;

          @Override
          public void writeRequest(StreamableOutput out) throws IOException {
            out.writeCompressedInt(Table.TableId.PACKAGE_DEFINITIONS.ordinal());
            out.writeUTF(business.getName().toString());
            out.writeUTF(category.getName());
            out.writeUTF(name);
            out.writeUTF(version);
            out.writeUTF(display);
            out.writeUTF(description);
            MoneyUtil.writeNullMoney(setupFee, out);
            out.writeNullUTF(setupFeeTransactionType == null ? null : setupFeeTransactionType.getName());
            MoneyUtil.writeMoney(monthlyRate, out);
            out.writeUTF(monthlyRateTransactionType.getName());
          }

          @Override
          public void readResponse(StreamableInput in) throws IOException, SQLException {
            int code = in.readByte();
            if (code == AoservProtocol.DONE) {
              pkey = in.readCompressedInt();
              invalidateList = AoservConnector.readInvalidateList(in);
            } else {
              AoservProtocol.checkResult(code, in);
              throw new IOException("Unknown response code: " + code);
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

  public PackageDefinition getPackageDefinition(Account business, PackageCategory category, String name, String version) throws IOException, SQLException {
    Account.Name accounting = business.getName();
    String categoryName = category.getName();
    List<PackageDefinition> pds = getRows();
    int size = pds.size();
    for (int c = 0; c < size; c++) {
      PackageDefinition pd = pds.get(c);
      if (
          pd.getAccount_name().equals(accounting)
              && pd.getPackageCategory_name().equals(categoryName)
              && pd.getName().equals(name)
              && pd.getVersion().equals(version)
      ) {
        return pd;
      }
    }
    return null;
  }

  public List<PackageDefinition> getPackageDefinitions(Account business, PackageCategory category) throws IOException, SQLException {
    Account.Name accounting = business.getName();
    String categoryName = category.getName();

    List<PackageDefinition> cached = getRows();
    List<PackageDefinition> matches = new ArrayList<>(cached.size());
    int size = cached.size();
    for (int c = 0; c < size; c++) {
      PackageDefinition pd = cached.get(c);
      if (
          pd.getAccount_name().equals(accounting)
              && pd.getPackageCategory_name().equals(categoryName)
      ) {
        matches.add(pd);
      }
    }
    return matches;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.PACKAGE_DEFINITIONS;
  }
}
