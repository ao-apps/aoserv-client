/*
 * Copyright 2001-2012 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.io.*;
import java.io.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

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
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    void addNoticeLog(
	String accounting,
	String billingContact,
	String emailAddress,
	BigDecimal balance,
	String type,
	int transid
    ) throws IOException, SQLException {
    	connector.requestUpdateIL(
            true,
            AOServProtocol.CommandID.ADD,
            SchemaTable.TableID.NOTICE_LOG,
            accounting,
            billingContact,
            emailAddress,
            balance.scaleByPowerOfTen(2).intValueExact(),
            type,
            transid
    	);
    }

    public NoticeLog get(int pkey) throws IOException, SQLException {
    	return getUniqueRow(NoticeLog.COLUMN_PKEY, pkey);
    }

    List<NoticeLog> getNoticeLogs(Business bu) throws IOException, SQLException {
        return getIndexedRows(NoticeLog.COLUMN_ACCOUNTING, bu.pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.NOTICE_LOG;
    }

    @Override
    boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.ADD_NOTICE_LOG)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_NOTICE_LOG, args, 6, err)) {
                connector.getSimpleAOClient().addNoticeLog(
                    args[1],
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
