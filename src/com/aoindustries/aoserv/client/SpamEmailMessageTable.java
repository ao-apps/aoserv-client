/*
 * Copyright 2003-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.io.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  SpamEmailMessage
 *
 * @author  AO Industries, Inc.
 */
final public class SpamEmailMessageTable extends AOServTable<Integer,SpamEmailMessage> {

    SpamEmailMessageTable(AOServConnector connector) {
	super(connector, SpamEmailMessage.class);
    }
    
    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(SpamEmailMessage.COLUMN_PKEY_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    int addSpamEmailMessage(EmailSmtpRelay esr, String message) throws IOException, SQLException {
        return connector.requestIntQueryIL(
            true,
            AOServProtocol.CommandID.ADD,
            SchemaTable.TableID.SPAM_EMAIL_MESSAGES,
            esr.pkey,
            message
        );
    }

    public List<SpamEmailMessage> getRows() throws IOException, SQLException {
        List<SpamEmailMessage> list=new ArrayList<SpamEmailMessage>();
        getObjects(true, list, AOServProtocol.CommandID.GET_TABLE, SchemaTable.TableID.SPAM_EMAIL_MESSAGES);
        return list;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.SPAM_EMAIL_MESSAGES;
    }

    public SpamEmailMessage get(Object pkey) throws IOException, SQLException {
        return get(((Integer)pkey).intValue());
    }

    public SpamEmailMessage get(int pkey) throws IOException, SQLException {
        return getObject(true, AOServProtocol.CommandID.GET_OBJECT, SchemaTable.TableID.SPAM_EMAIL_MESSAGES, pkey);
    }

    List<SpamEmailMessage> getSpamEmailMessages(EmailSmtpRelay esr) throws IOException, SQLException {
        return getSpamEmailMessages(esr.pkey);
    }

    List<SpamEmailMessage> getSpamEmailMessages(int esr) throws IOException, SQLException {
        return getObjects(true, AOServProtocol.CommandID.GET_SPAM_EMAIL_MESSAGES_FOR_EMAIL_SMTP_RELAY, esr);
    }

    @Override
    final public List<SpamEmailMessage> getIndexedRows(int col, Object value) throws IOException, SQLException {
        if(col==SpamEmailMessage.COLUMN_PKEY) {
            SpamEmailMessage sem=get(value);
            if(sem==null) return Collections.emptyList();
            else return Collections.singletonList(sem);
        }
        if(col==SpamEmailMessage.COLUMN_EMAIL_RELAY) return getSpamEmailMessages(((Integer)value).intValue());
        throw new UnsupportedOperationException("Not an indexed column: "+col);
    }

    protected SpamEmailMessage getUniqueRowImpl(int col, Object value) throws IOException, SQLException {
        if(col!=SpamEmailMessage.COLUMN_PKEY) throw new IllegalArgumentException("Not a unique column: "+col);
        return get(value);
    }

    @Override
    boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.ADD_SPAM_EMAIL_MESSAGE)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_SPAM_EMAIL_MESSAGE, args, 2, err)) {
                int pkey=connector.getSimpleAOClient().addSpamEmailMessage(
                    AOSH.parseInt(args[1], "email_relay"),
                    args[2]
                );
                out.println(pkey);
                out.flush();
            }
            return true;
	}
	return false;
    }
}