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
import com.aoapps.net.Email;
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
 * @see  NoticeLog
 *
 * @author  AO Industries, Inc.
 */
public final class NoticeLogTable extends CachedTableIntegerKey<NoticeLog> {

  NoticeLogTable(AoservConnector connector) {
    super(connector, NoticeLog.class);
  }

  private static final OrderBy[] defaultOrderBy = {
      new OrderBy(NoticeLog.COLUMN_CREATE_TIME_name, ASCENDING),
      new OrderBy(NoticeLog.COLUMN_PKEY_name, ASCENDING)
  };

  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  public int addNoticeLog(
      final Account account,
      final String billingContact,
      final Email emailAddress,
      final NoticeType type,
      final Transaction trans
  ) throws IOException, SQLException {
    return connector.requestIntQueryInvalidating(
        true,
        AoservProtocol.CommandId.ADD,
        Table.TableId.NOTICE_LOG,
        account.getName().toString(),
        billingContact,
        emailAddress,
        type.getType(),
        trans == null ? NoticeLog.NO_TRANSACTION : trans.getTransid()
    );
  }

  @Override
  public NoticeLog get(int pkey) throws IOException, SQLException {
    return getUniqueRow(NoticeLog.COLUMN_PKEY, pkey);
  }

  public List<NoticeLog> getNoticeLogs(Account bu) throws IOException, SQLException {
    return getIndexedRows(NoticeLog.COLUMN_ACCOUNTING, bu.getName());
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.NOTICE_LOG;
  }

  @Override
  public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
    String command = args[0];
    if (command.equalsIgnoreCase(Command.ADD_NOTICE_LOG)) {
      if (Aosh.checkParamCount(Command.ADD_NOTICE_LOG, args, 5, err)) {
        out.println(
            connector.getSimpleClient().addNoticeLog(
                Aosh.parseAccountingCode(args[1], "business"),
                args[2],
                Aosh.parseEmail(args[3], "email_address"),
                args[4],
                args[5].isEmpty() ? NoticeLog.NO_TRANSACTION : Aosh.parseInt(args[5], "transid")
            )
        );
        out.flush();
      }
      return true;
    }
    return false;
  }
}
