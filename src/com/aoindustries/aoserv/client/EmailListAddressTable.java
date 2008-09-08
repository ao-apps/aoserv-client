package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  EmailListAddress
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class EmailListAddressTable extends CachedTableIntegerKey<EmailListAddress> {

    EmailListAddressTable(AOServConnector connector) {
	super(connector, EmailListAddress.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(EmailListAddress.COLUMN_EMAIL_ADDRESS_name+'.'+EmailAddress.COLUMN_DOMAIN_name+'.'+EmailDomain.COLUMN_DOMAIN_name, ASCENDING),
        new OrderBy(EmailListAddress.COLUMN_EMAIL_ADDRESS_name+'.'+EmailAddress.COLUMN_DOMAIN_name+'.'+EmailDomain.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING),
        new OrderBy(EmailListAddress.COLUMN_EMAIL_ADDRESS_name+'.'+EmailAddress.COLUMN_ADDRESS_name, ASCENDING),
        new OrderBy(EmailListAddress.COLUMN_EMAIL_LIST_name+'.'+EmailList.COLUMN_PATH_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    int addEmailListAddress(EmailAddress emailAddressObject, EmailList emailListObject) {
	return connector.requestIntQueryIL(
            AOServProtocol.CommandID.ADD,
            SchemaTable.TableID.EMAIL_LIST_ADDRESSES,
            emailAddressObject.pkey,
            emailListObject.pkey
	);
    }

    public EmailListAddress get(Object pkey) {
	return getUniqueRow(EmailListAddress.COLUMN_PKEY, pkey);
    }

    public EmailListAddress get(int pkey) {
	return getUniqueRow(EmailListAddress.COLUMN_PKEY, pkey);
    }

    List<EmailListAddress> getEmailListAddresses(EmailList list) {
        return getIndexedRows(EmailListAddress.COLUMN_EMAIL_LIST, list.pkey);
    }

    List<EmailAddress> getEmailAddresses(EmailList list) {
        // Use the index first
        List<EmailListAddress> cached=getEmailListAddresses(list);
        int len=cached.size();
        List<EmailAddress> eas=new ArrayList<EmailAddress>(len);
        for(int c=0;c<len;c++) eas.add(cached.get(c).getEmailAddress());
	return eas;
    }

    EmailListAddress getEmailListAddress(EmailAddress ea, EmailList list) {
        int pkey=ea.pkey;
        // Use the index first
	List<EmailListAddress> cached=getEmailListAddresses(list);
	int size=cached.size();
	for (int c = 0; c < size; c++) {
            EmailListAddress ela=cached.get(c);
            if(ela.email_address==pkey) return ela;
	}
        return null;
    }

    List<EmailListAddress> getEmailListAddresses(EmailAddress ea) {
        return getIndexedRows(EmailListAddress.COLUMN_EMAIL_ADDRESS, ea.pkey);
    }

    List<EmailList> getEmailLists(EmailAddress ea) {
        // Use the cache first
        List<EmailListAddress> cached=getEmailListAddresses(ea);
        int len=cached.size();
        List<EmailList> els=new ArrayList<EmailList>(len);
        for(int c=0;c<len;c++) els.add(cached.get(c).getEmailList());
        return els;
    }

    List<EmailListAddress> getEnabledEmailListAddresses(EmailAddress ea) {
        // Use the cache first
	List<EmailListAddress> cached=getEmailListAddresses(ea);
	int size=cached.size();
        List<EmailListAddress> matches=new ArrayList<EmailListAddress>(size);
	for (int c = 0; c < size; c++) {
            EmailListAddress ela=cached.get(c);
            if(ela.getEmailList().disable_log==-1) matches.add(ela);
	}
        return matches;
    }

    List<EmailListAddress> getEmailListAddresses(AOServer ao) {
        int aoPKey=ao.pkey;
	List<EmailListAddress> cached = getRows();
	int len = cached.size();
        List<EmailListAddress> matches=new ArrayList<EmailListAddress>(len);
	for (int c = 0; c < len; c++) {
            EmailListAddress list=cached.get(c);
            if(list.getEmailAddress().getDomain().ao_server==aoPKey) matches.add(list);
	}
	return matches;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.EMAIL_LIST_ADDRESSES;
    }

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.ADD_EMAIL_LIST_ADDRESS)) {
            if(AOSH.checkMinParamCount(AOSHCommand.ADD_EMAIL_LIST_ADDRESS, args, 3, err)) {
                if((args.length%3)!=0) {
                    err.println("aosh: "+AOSHCommand.ADD_EMAIL_LIST_ADDRESS+": must have multiples of three number of parameters");
                    err.flush();
                } else {
                    for(int c=1;c<args.length;c+=3) {
                        String addr=args[c];
                        int pos=addr.indexOf('@');
                        if(pos==-1) {
                            err.print("aosh: "+AOSHCommand.ADD_EMAIL_LIST_ADDRESS+": invalid email address: ");
                            err.println(addr);
                            err.flush();
                        } else {
                            out.println(
                                connector.simpleAOClient.addEmailListAddress(
                                    addr.substring(0, pos),
                                    addr.substring(pos+1),
                                    args[c+1],
                                    args[c+2]
                                )
                            );
                            out.flush();
                        }
                    }
                }
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_EMAIL_LIST_ADDRESS)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_EMAIL_LIST_ADDRESS, args, 3, err)) {
                String addr=args[1];
                int pos=addr.indexOf('@');
                if(pos==-1) {
                    err.print("aosh: "+AOSHCommand.REMOVE_EMAIL_LIST_ADDRESS+": invalid email address: ");
                    err.println(addr);
                    err.flush();
                } else {
                    connector.simpleAOClient.removeEmailListAddress(
                        addr.substring(0, pos),
                        addr.substring(pos+1),
                        args[2],
                        args[3]
                    );
                }
            }
            return true;
	}
	return false;
    }
}