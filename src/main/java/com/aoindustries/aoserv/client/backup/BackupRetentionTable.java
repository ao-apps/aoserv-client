/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2003-2009, 2016, 2017, 2018, 2020, 2021, 2022  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.backup;

import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.GlobalTable;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  BackupRetention
 *
 * @author  AO Industries, Inc.
 */
public final class BackupRetentionTable extends GlobalTable<Short, BackupRetention> {

  BackupRetentionTable(AoservConnector connector) {
    super(connector, BackupRetention.class);
  }

  private static final OrderBy[] defaultOrderBy = {
      new OrderBy(BackupRetention.COLUMN_DAYS_name, ASCENDING)
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
  public BackupRetention get(Object days) throws IOException, SQLException {
    if (days == null) {
      return null;
    }
    return get(((Short) days).shortValue());
  }

  /**
   * @see  #get(java.lang.Object)
   */
  public BackupRetention get(short days) throws IOException, SQLException {
    return getUniqueRow(BackupRetention.COLUMN_DAYS, days);
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.BACKUP_RETENTIONS;
  }
}
