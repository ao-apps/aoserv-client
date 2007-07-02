package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
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
    
    int addSpamEmailMessage(EmailSmtpRelay esr, String message) {
        return connector.requestIntQueryIL(
            AOServProtocol.ADD,
            SchemaTable.TableID.SPAM_EMAIL_MESSAGES,
            esr.pkey,
            message
        );
    }

    public List<SpamEmailMessage> getRows() {
        List<SpamEmailMessage> list=new ArrayList<SpamEmailMessage>();
        getObjects(list, AOServProtocol.GET_TABLE, SchemaTable.TableID.SPAM_EMAIL_MESSAGES);
        return list;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.SPAM_EMAIL_MESSAGES;
    }

    public SpamEmailMessage get(Object pkey) {
        return get(((Integer)pkey).intValue());
    }

    public SpamEmailMessage get(int pkey) {
        return getObject(AOServProtocol.GET_OBJECT, SchemaTable.TableID.SPAM_EMAIL_MESSAGES, pkey);
    }

    List<SpamEmailMessage> getSpamEmailMessages(EmailSmtpRelay esr) {
        return getSpamEmailMessages(esr.pkey);
    }

    List<SpamEmailMessage> getSpamEmailMessages(int esr) {
        return getObjects(AOServProtocol.GET_SPAM_EMAIL_MESSAGES_FOR_EMAIL_SMTP_RELAY, esr);
    }

    final public List<SpamEmailMessage> getIndexedRows(int col, Object value) {
        if(col==SpamEmailMessage.COLUMN_PKEY) {
            SpamEmailMessage sem=get(value);
            if(sem==null) return Collections.emptyList();
            else return Collections.singletonList(sem);
        }
        if(col==SpamEmailMessage.COLUMN_EMAIL_RELAY) return getSpamEmailMessages(((Integer)value).intValue());
        throw new IllegalArgumentException("Not an indexed column: "+col);
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