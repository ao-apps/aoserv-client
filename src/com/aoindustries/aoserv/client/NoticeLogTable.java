package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.WrappedException;
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
	int balance,
	String type,
	int transid
    ) throws IOException, SQLException {
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
        try {
            return getUniqueRow(NoticeLog.COLUMN_PKEY, pkey);
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
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
    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
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
