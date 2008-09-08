package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  SpamEmailMessage
 *
 * @version  1.0a
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

    int addSpamEmailMessage(EmailSmtpRelay esr, String message) {
        return connector.requestIntQueryIL(
            AOServProtocol.CommandID.ADD,
            SchemaTable.TableID.SPAM_EMAIL_MESSAGES,
            esr.pkey,
            message
        );
    }

    public List<SpamEmailMessage> getRows() {
        List<SpamEmailMessage> list=new ArrayList<SpamEmailMessage>();
        getObjects(list, AOServProtocol.CommandID.GET_TABLE, SchemaTable.TableID.SPAM_EMAIL_MESSAGES);
        return list;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.SPAM_EMAIL_MESSAGES;
    }

    public SpamEmailMessage get(Object pkey) {
        return get(((Integer)pkey).intValue());
    }

    public SpamEmailMessage get(int pkey) {
        return getObject(AOServProtocol.CommandID.GET_OBJECT, SchemaTable.TableID.SPAM_EMAIL_MESSAGES, pkey);
    }

    List<SpamEmailMessage> getSpamEmailMessages(EmailSmtpRelay esr) {
        return getSpamEmailMessages(esr.pkey);
    }

    List<SpamEmailMessage> getSpamEmailMessages(int esr) {
        return getObjects(AOServProtocol.CommandID.GET_SPAM_EMAIL_MESSAGES_FOR_EMAIL_SMTP_RELAY, esr);
    }

    final public List<SpamEmailMessage> getIndexedRows(int col, Object value) {
        if(col==SpamEmailMessage.COLUMN_PKEY) {
            SpamEmailMessage sem=get(value);
            if(sem==null) return Collections.emptyList();
            else return Collections.singletonList(sem);
        }
        if(col==SpamEmailMessage.COLUMN_EMAIL_RELAY) return getSpamEmailMessages(((Integer)value).intValue());
        throw new UnsupportedOperationException("Not an indexed column: "+col);
    }

    protected SpamEmailMessage getUniqueRowImpl(int col, Object value) {
        if(col!=SpamEmailMessage.COLUMN_PKEY) throw new IllegalArgumentException("Not a unique column: "+col);
        return get(value);
    }

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.ADD_SPAM_EMAIL_MESSAGE)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_SPAM_EMAIL_MESSAGE, args, 2, err)) {
                int pkey=connector.simpleAOClient.addSpamEmailMessage(
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