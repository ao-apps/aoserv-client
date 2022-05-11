/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2019, 2021, 2022  AO Industries, Inc.
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

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.i18n.Money;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A {@link NoticeLog} has the account balances at the time the notice was sent.
 *
 * @see  NoticeLog
 *
 * @author  AO Industries, Inc.
 */
public final class NoticeLogBalance extends CachedObjectIntegerKey<NoticeLogBalance> {

  static final int COLUMN_id = 0;
  static final int COLUMN_noticeLog = 1;
  static final String COLUMN_noticeLog_name = "noticeLog";
  static final String COLUMN_balance_name = "balance";

  private int noticeLog;
  private Money balance;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated // Java 9: (forRemoval = true)
  public NoticeLogBalance() {
    // Do nothing
  }

  public int getId() {
    return pkey;
  }

  public int getNoticeLog_id() {
    return noticeLog;
  }

  public NoticeLog getNoticeLog() throws SQLException, IOException {
    NoticeLog obj = table.getConnector().getBilling().getNoticeLog().get(noticeLog);
    if (obj == null) {
      throw new SQLException("Unable to find NoticeLog: " + noticeLog);
    }
    return obj;
  }

  public Money getBalance() {
    return balance;
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_id:
        return pkey;
      case COLUMN_noticeLog:
        return noticeLog;
      case 2:
        return balance;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.NoticeLogBalance;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    pkey = result.getInt("id");
    noticeLog = result.getInt("noticeLog");
    balance = MoneyUtil.getMoney(result, "balance.currency", "balance.value");
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    pkey = in.readCompressedInt();
    noticeLog = in.readCompressedInt();
    balance = MoneyUtil.readMoney(in);
  }

  @Override
  public String toStringImpl() throws SQLException, IOException {
    return getNoticeLog().toStringImpl() + "->" + balance;
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeCompressedInt(noticeLog);
    MoneyUtil.writeMoney(balance, out);
  }
}
