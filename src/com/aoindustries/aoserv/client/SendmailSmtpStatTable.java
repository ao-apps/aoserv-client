package com.aoindustries.aoserv.client;

/*
 * Copyright 2002-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.profiler.*;
import com.aoindustries.util.WrappedException;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see SendmailSmtpStat
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class SendmailSmtpStatTable extends AOServTable<Integer,SendmailSmtpStat> {

    SendmailSmtpStatTable(AOServConnector connector) {
	super(connector, SendmailSmtpStat.class);
    }

    int addSendmailSmtpStat(
        Package pack,
        long date,
        AOServer ao,
        int in_count,
        long in_bandwidth,
        int out_count,
        long out_bandwidth
    ) {
        return connector.requestIntQueryIL(
            AOServProtocol.CommandID.ADD,
            SchemaTable.TableID.SENDMAIL_SMTP_STATS,
            pack.name,
            date,
            ao.pkey,
            in_count,
            in_bandwidth,
            out_count,
            out_bandwidth
        );
    }

    public SendmailSmtpStat get(Object pkey) {
        return get(((Integer)pkey).intValue());
    }

    public SendmailSmtpStat get(int pkey) {
        return getObject(AOServProtocol.CommandID.GET_OBJECT, SchemaTable.TableID.SENDMAIL_SMTP_STATS, pkey);
    }

    List<SendmailSmtpStat> getSendmailSmtpStats(Package pk) {
	String name=pk.name;

        List<SendmailSmtpStat> matches=new ArrayList<SendmailSmtpStat>();
        for(SendmailSmtpStat stat : getRows()) {
            if (name.equals(stat.packageName)) matches.add(stat);
	}
        return matches;
    }

    public List<SendmailSmtpStat> getRows() {
        List<SendmailSmtpStat> list=new ArrayList<SendmailSmtpStat>();
        getObjects(list, AOServProtocol.CommandID.GET_TABLE, SchemaTable.TableID.SENDMAIL_SMTP_STATS);
        return list;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.SENDMAIL_SMTP_STATS;
    }

    protected SendmailSmtpStat getUniqueRowImpl(int col, Object value) {
        if(col!=SendmailSmtpStat.COLUMN_PKEY) throw new IllegalArgumentException("Not a unique column: "+col);
        return get(value);
    }

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.ADD_SENDMAIL_SMTP_STAT)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_SENDMAIL_SMTP_STAT, args, 7, err)) {
                int pkey=connector.simpleAOClient.addSendmailSmtpStat(
                    args[1],
                    AOSH.parseDate(args[2], "date"),
                    args[3],
                    AOSH.parseInt(args[4], "in_count"),
                    AOSH.parseLong(args[5], "in_bandwidth"),
                    AOSH.parseInt(args[6], "out_count"),
                    AOSH.parseLong(args[7], "out_bandwidth")
                );
                out.println(pkey);
                out.flush();
            }
            return true;
	}
	return false;
    }
}