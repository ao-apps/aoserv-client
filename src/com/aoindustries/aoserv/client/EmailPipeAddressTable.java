package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  EmailPipeAddress
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class EmailPipeAddressTable extends CachedTableIntegerKey<EmailPipeAddress> {

    EmailPipeAddressTable(AOServConnector connector) {
	super(connector, EmailPipeAddress.class);
    }

    int addEmailPipeAddress(EmailAddress emailAddressObject, EmailPipe emailPipeObject) {
	return connector.requestIntQueryIL(
            AOServProtocol.CommandID.ADD,
            SchemaTable.TableID.EMAIL_PIPE_ADDRESSES,
            emailAddressObject.pkey,
            emailPipeObject.pkey
	);
    }

    public EmailPipeAddress get(Object pkey) {
	return getUniqueRow(EmailPipeAddress.COLUMN_PKEY, pkey);
    }

    public EmailPipeAddress get(int pkey) {
	return getUniqueRow(EmailPipeAddress.COLUMN_PKEY, pkey);
    }

    List<EmailPipe> getEmailPipes(EmailAddress address) {
        int pkey=address.pkey;
	List<EmailPipeAddress> cached=getRows();
	int len = cached.size();
        List<EmailPipe> matches=new ArrayList<EmailPipe>(len);
	for (int c = 0; c < len; c++) {
            EmailPipeAddress pipe=cached.get(c);
            if (pipe.email_address==pkey) {
                // The pipe might be filtered
                EmailPipe ep=pipe.getEmailPipe();
                if(ep!=null) matches.add(pipe.getEmailPipe());
            }
	}
	return matches;
    }

    List<EmailPipeAddress> getEmailPipeAddresses(EmailAddress address) {
        return getIndexedRows(EmailPipeAddress.COLUMN_EMAIL_ADDRESS, address.pkey);
    }

    List<EmailPipeAddress> getEnabledEmailPipeAddresses(EmailAddress address) {
        // Use the index first
	List<EmailPipeAddress> cached = getEmailPipeAddresses(address);
	int len = cached.size();
        List<EmailPipeAddress> matches=new ArrayList<EmailPipeAddress>(len);
	for (int c = 0; c < len; c++) {
            EmailPipeAddress pipe=cached.get(c);
            if(pipe.getEmailPipe().disable_log==-1) matches.add(pipe);
	}
	return matches;
    }

    EmailPipeAddress getEmailPipeAddress(EmailAddress address, EmailPipe pipe) {
        int pkey=address.pkey;
        int pipePKey=pipe.pkey;
	List<EmailPipeAddress> cached = getRows();
	int len = cached.size();
	for (int c = 0; c < len; c++) {
            EmailPipeAddress epa = cached.get(c);
            if (epa.email_address==pkey && epa.email_pipe==pipePKey) return epa;
	}
        return null;
    }

    List<EmailPipeAddress> getEmailPipeAddresses(AOServer ao) {
        int aoPKey=ao.pkey;
	List<EmailPipeAddress> cached = getRows();
        int len = cached.size();
        List<EmailPipeAddress> matches=new ArrayList<EmailPipeAddress>(len);
	for (int c = 0; c < len; c++) {
            EmailPipeAddress pipe = cached.get(c);
            if(pipe.getEmailAddress().getDomain().ao_server==aoPKey) matches.add(pipe);
	}
	return matches;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.EMAIL_PIPE_ADDRESSES;
    }

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.ADD_EMAIL_PIPE_ADDRESS)) {
            if(AOSH.checkMinParamCount(AOSHCommand.ADD_EMAIL_PIPE_ADDRESS, args, 2, err)) {
                if((args.length&1)==0) {
                    err.println("aosh: "+AOSHCommand.ADD_EMAIL_PIPE_ADDRESS+": must have even number of parameters");
                    err.flush();
                } else {
                    for(int c=1;c<args.length;c+=2) {
                        String addr=args[c];
                        int pos=addr.indexOf('@');
                        if(pos==-1) {
                            err.print("aosh: "+AOSHCommand.ADD_EMAIL_PIPE_ADDRESS+": invalid email address: ");
                            err.println(addr);
                            err.flush();
                        } else {
                            out.println(
                                connector.simpleAOClient.addEmailPipeAddress(
                                    addr.substring(0, pos),
                                    addr.substring(pos+1),
                                    AOSH.parseInt(args[c+1], "pkey")
                                )
                            );
                            out.flush();
                        }
                    }
                }
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_EMAIL_PIPE_ADDRESS)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_EMAIL_PIPE_ADDRESS, args, 2, err)) {
                String addr=args[1];
                int pos=addr.indexOf('@');
                if(pos==-1) {
                    err.print("aosh: "+AOSHCommand.REMOVE_EMAIL_PIPE_ADDRESS+": invalid email address: ");
                    err.println(addr);
                    err.flush();
                } else {
                    connector.simpleAOClient.removeEmailPipeAddress(
                        addr.substring(0, pos),
                        addr.substring(pos+1),
                        AOSH.parseInt(args[2], "pkey")
                    );
                }
            }
            return true;
	}
	return false;
    }
}