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

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(EmailPipeAddress.COLUMN_EMAIL_ADDRESS_name+'.'+EmailAddress.COLUMN_DOMAIN_name+'.'+EmailDomain.COLUMN_DOMAIN_name, ASCENDING),
        new OrderBy(EmailPipeAddress.COLUMN_EMAIL_ADDRESS_name+'.'+EmailAddress.COLUMN_DOMAIN_name+'.'+EmailDomain.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING),
        new OrderBy(EmailPipeAddress.COLUMN_EMAIL_ADDRESS_name+'.'+EmailAddress.COLUMN_ADDRESS_name, ASCENDING),
        new OrderBy(EmailPipeAddress.COLUMN_EMAIL_PIPE_name+'.'+EmailPipe.COLUMN_PATH_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    int addEmailPipeAddress(EmailAddress emailAddressObject, EmailPipe emailPipeObject) throws IOException, SQLException {
	return connector.requestIntQueryIL(
            AOServProtocol.CommandID.ADD,
            SchemaTable.TableID.EMAIL_PIPE_ADDRESSES,
            emailAddressObject.pkey,
            emailPipeObject.pkey
	);
    }

    public EmailPipeAddress get(Object pkey) {
        try {
            return getUniqueRow(EmailPipeAddress.COLUMN_PKEY, pkey);
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public EmailPipeAddress get(int pkey) throws IOException, SQLException {
	return getUniqueRow(EmailPipeAddress.COLUMN_PKEY, pkey);
    }

    List<EmailPipe> getEmailPipes(EmailAddress address) throws IOException, SQLException {
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

    List<EmailPipeAddress> getEmailPipeAddresses(EmailAddress address) throws IOException, SQLException {
        return getIndexedRows(EmailPipeAddress.COLUMN_EMAIL_ADDRESS, address.pkey);
    }

    List<EmailPipeAddress> getEnabledEmailPipeAddresses(EmailAddress address) throws IOException, SQLException {
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

    EmailPipeAddress getEmailPipeAddress(EmailAddress address, EmailPipe pipe) throws IOException, SQLException {
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

    List<EmailPipeAddress> getEmailPipeAddresses(AOServer ao) throws IOException, SQLException {
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

    @Override
    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
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
                                connector.getSimpleAOClient().addEmailPipeAddress(
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
                    connector.getSimpleAOClient().removeEmailPipeAddress(
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