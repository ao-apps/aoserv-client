/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2007-2009, 2016, 2017, 2018, 2020, 2021, 2022  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.pki;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  EncryptionKey
 *
 * @author  AO Industries, Inc.
 */
public final class EncryptionKeyTable extends CachedTableIntegerKey<EncryptionKey> {

  EncryptionKeyTable(AOServConnector connector) {
    super(connector, EncryptionKey.class);
  }

  private static final OrderBy[] defaultOrderBy = {
    new OrderBy(EncryptionKey.COLUMN_ACCOUNTING_name, ASCENDING),
    new OrderBy(EncryptionKey.COLUMN_ID_name, ASCENDING)
  };
  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  @Override
  public EncryptionKey get(int pkey) throws IOException, SQLException {
    return getUniqueRow(EncryptionKey.COLUMN_PKEY, pkey);
  }

  public List<EncryptionKey> getEncryptionKeys(Account business) throws IOException, SQLException {
    return getIndexedRows(EncryptionKey.COLUMN_ACCOUNTING, business.getName());
  }

  @Override
  public Table.TableID getTableID() {
    return Table.TableID.ENCRYPTION_KEYS;
  }
}
