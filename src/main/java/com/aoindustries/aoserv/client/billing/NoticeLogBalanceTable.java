/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2019, 2020, 2021, 2022  AO Industries, Inc.
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

import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  NoticeLogBalance
 *
 * @author  AO Industries, Inc.
 */
public final class NoticeLogBalanceTable extends CachedTableIntegerKey<NoticeLogBalance> {

  NoticeLogBalanceTable(AoservConnector connector) {
    super(connector, NoticeLogBalance.class);
  }

  private static final OrderBy[] defaultOrderBy = {
      new OrderBy(NoticeLogBalance.COLUMN_noticeLog_name + '.' + NoticeLog.COLUMN_CREATE_TIME_name, ASCENDING),
      new OrderBy(NoticeLogBalance.COLUMN_noticeLog_name + '.' + NoticeLog.COLUMN_PKEY_name, ASCENDING),
      new OrderBy(NoticeLogBalance.COLUMN_balance_name, ASCENDING),
  };

  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  @Override
  public NoticeLogBalance get(int id) throws IOException, SQLException {
    return getUniqueRow(NoticeLogBalance.COLUMN_id, id);
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.NoticeLogBalance;
  }

  List<NoticeLogBalance> getNoticeLogBalances(NoticeLog noticeLog) throws IOException, SQLException {
    return getIndexedRows(NoticeLogBalance.COLUMN_noticeLog, noticeLog.getPkey());
  }
}
