/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.billing;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.io.TerminalWriter;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  NoticeLog
 *
 * @author  AO Industries, Inc.
 */
final public class NoticeLogTable extends CachedTableIntegerKey<NoticeLog> {

	NoticeLogTable(AOServConnector connector) {
		super(connector, NoticeLog.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(NoticeLog.COLUMN_CREATE_TIME_name, ASCENDING),
		new OrderBy(NoticeLog.COLUMN_PKEY_name, ASCENDING)
	};
	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	public void addNoticeLog(
		AccountingCode accounting,
		String billingContact,
		String emailAddress,
		BigDecimal balance,
		String type,
		int transid
	) throws IOException, SQLException {
		connector.requestUpdateIL(true,
			AoservProtocol.CommandID.ADD,
			Table.TableID.NOTICE_LOG,
			accounting.toString(),
			billingContact,
			emailAddress,
			balance.scaleByPowerOfTen(2).intValueExact(),
			type,
			transid
		);
	}

	@Override
	public NoticeLog get(int pkey) throws IOException, SQLException {
		return getUniqueRow(NoticeLog.COLUMN_PKEY, pkey);
	}

	public List<NoticeLog> getNoticeLogs(Account bu) throws IOException, SQLException {
		return getIndexedRows(NoticeLog.COLUMN_ACCOUNTING, bu.getAccounting());
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.NOTICE_LOG;
	}

	@Override
	public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(Command.ADD_NOTICE_LOG)) {
			if(AOSH.checkParamCount(Command.ADD_NOTICE_LOG, args, 6, err)) {
				connector.getSimpleAOClient().addNoticeLog(
					AOSH.parseAccountingCode(args[1], "business"),
					args[2],
					args[3],
					BigDecimal.valueOf(AOSH.parsePennies(args[4], "balance"), 2),
					args[5],
					AOSH.parseInt(args[6], "transid")
				);
			}
			return true;
		}
		return false;
	}
}
