package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.profiler.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  NoticeLog
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class NoticeLogTable extends CachedTableIntegerKey<NoticeLog> {

    NoticeLogTable(AOServConnector connector) {
	super(connector, NoticeLog.class);
    }

    void addNoticeLog(
	String accounting,
	String billingContact,
	String emailAddress,
	int balance,
	String type,
	int transid
    ) {
	connector.requestUpdateIL(
            AOServProtocol.CommandID.ADD,
            SchemaTable.TableID.NOTICE_LOG,
            accounting,
            billingContact,
            emailAddress,
            balance,
            type,
            transid
	);
    }

    public NoticeLog get(Object pkey) {
	return getUniqueRow(NoticeLog.COLUMN_PKEY, pkey);
    }

    public NoticeLog get(int pkey) {
	return getUniqueRow(NoticeLog.COLUMN_PKEY, pkey);
    }

    List<NoticeLog> getNoticeLogs(Business bu) {
        return getIndexedRows(NoticeLog.COLUMN_ACCOUNTING, bu.pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.NOTICE_LOG;
    }

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.ADD_NOTICE_LOG)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_NOTICE_LOG, args, 6, err)) {
                connector.simpleAOClient.addNoticeLog(
                    args[1],
                    args[2],
                    args[3],
                    AOSH.parsePennies(args[4], "balance"),
                    args[5],
                    AOSH.parseInt(args[6], "transid")
                );
            }
            return true;
	}
	return false;
    }
}
