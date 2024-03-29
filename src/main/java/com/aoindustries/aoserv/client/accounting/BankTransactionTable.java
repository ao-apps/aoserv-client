/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2009, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.AoservTable;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.schema.Type;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * For AO Industries use only.
 *
 * @author  AO Industries, Inc.
 */
public final class BankTransactionTable extends AoservTable<Integer, BankTransaction> {

  BankTransactionTable(AoservConnector connector) {
    super(connector, BankTransaction.class);
  }

  private static final OrderBy[] defaultOrderBy = {
      new OrderBy(BankTransaction.COLUMN_TIME_name + "::" + Type.DATE_name, ASCENDING),
      new OrderBy(BankTransaction.COLUMN_ID_name, ASCENDING)
  };

  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Always try to lookup by specific keys; the compiler will help you more when types change.
   */
  @Deprecated
  @Override
  public BankTransaction get(Object transid) throws IOException, SQLException {
    if (transid == null) {
      return null;
    }
    return get(((Integer) transid).intValue());
  }

  /**
   * @see  #get(java.lang.Object)
   */
  public BankTransaction get(int transid) throws IOException, SQLException {
    return getObject(true, AoservProtocol.CommandId.GET_OBJECT, Table.TableId.BANK_TRANSACTIONS, transid);
  }

  List<BankTransaction> getBankTransactions(BankAccount account) throws IOException, SQLException {
    return getObjects(true, AoservProtocol.CommandId.GET_BANK_TRANSACTIONS_ACCOUNT, account.getName());
  }

  @Override
  public List<BankTransaction> getRowsCopy() throws IOException, SQLException {
    List<BankTransaction> list = new ArrayList<>();
    getObjects(true, list, AoservProtocol.CommandId.GET_TABLE, Table.TableId.BANK_TRANSACTIONS);
    return list;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.BANK_TRANSACTIONS;
  }

  @Override
  protected BankTransaction getUniqueRowImpl(int col, Object value) throws IOException, SQLException {
    if (col == BankTransaction.COLUMN_ID) {
      return get(value);
    }
    throw new IllegalArgumentException("Not a unique column: " + col);
  }
}
