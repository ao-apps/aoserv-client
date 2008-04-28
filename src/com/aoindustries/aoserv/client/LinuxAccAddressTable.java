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
 * @see  LinuxAccAddress
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class LinuxAccAddressTable extends CachedTableIntegerKey<LinuxAccAddress> {

    LinuxAccAddressTable(AOServConnector connector) {
	super(connector, LinuxAccAddress.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(LinuxAccAddress.COLUMN_EMAIL_ADDRESS_name+'.'+EmailAddress.COLUMN_DOMAIN_name+'.'+EmailDomain.COLUMN_DOMAIN_name, ASCENDING),
        new OrderBy(LinuxAccAddress.COLUMN_EMAIL_ADDRESS_name+'.'+EmailAddress.COLUMN_DOMAIN_name+'.'+EmailDomain.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING),
        new OrderBy(LinuxAccAddress.COLUMN_EMAIL_ADDRESS_name+'.'+EmailAddress.COLUMN_ADDRESS_name, ASCENDING),
        new OrderBy(LinuxAccAddress.COLUMN_LINUX_SERVER_ACCOUNT_name+'.'+LinuxServerAccount.COLUMN_USERNAME_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    int addLinuxAccAddress(EmailAddress emailAddressObject, LinuxServerAccount lsa) {
	return connector.requestIntQueryIL(
            AOServProtocol.CommandID.ADD,
            SchemaTable.TableID.LINUX_ACC_ADDRESSES,
            emailAddressObject.pkey,
            lsa.pkey
	);
    }

    public LinuxAccAddress get(Object pkey) {
	return getUniqueRow(LinuxAccAddress.COLUMN_PKEY, pkey);
    }

    public LinuxAccAddress get(int pkey) {
	return getUniqueRow(LinuxAccAddress.COLUMN_PKEY, pkey);
    }

    List<EmailAddress> getEmailAddresses(LinuxServerAccount lsa) {
        // Start with the index
	List<LinuxAccAddress> cached = getLinuxAccAddresses(lsa);
	int len = cached.size();
        List<EmailAddress> matches=new ArrayList<EmailAddress>(len);
	for (int c = 0; c < len; c++) {
            LinuxAccAddress acc=cached.get(c);
            matches.add(acc.getEmailAddress());
	}
	return matches;
    }

    List<LinuxAccAddress> getLinuxAccAddresses(LinuxServerAccount lsa) {
        return getIndexedRows(LinuxAccAddress.COLUMN_LINUX_SERVER_ACCOUNT, lsa.pkey);
    }

    public LinuxAccAddress getLinuxAccAddress(EmailAddress ea, LinuxServerAccount lsa) {
        int pkey=ea.pkey;
        int lsaPKey=lsa.pkey;
        List<LinuxAccAddress> cached=getRows();
        int size=cached.size();
        for(int c=0;c<size;c++) {
            LinuxAccAddress laa=cached.get(c);
            if(laa.email_address==pkey && laa.linux_server_account==lsaPKey) return laa;
        }
        return null;
    }

    List<LinuxAccAddress> getLinuxAccAddresses(AOServer ao) {
        int aoPKey=ao.pkey;
	List<LinuxAccAddress> cached = getRows();
	int len = cached.size();
        List<LinuxAccAddress> matches=new ArrayList<LinuxAccAddress>(len);
	for (int c = 0; c < len; c++) {
            LinuxAccAddress acc = cached.get(c);
            if(acc.getEmailAddress().getDomain().ao_server==aoPKey) matches.add(acc);
	}
	return matches;
    }

    List<LinuxServerAccount> getLinuxServerAccounts(EmailAddress address) {
        int pkey=address.pkey;
	List<LinuxAccAddress> cached = getRows();
	int len = cached.size();
        List<LinuxServerAccount> matches=new ArrayList<LinuxServerAccount>(len);
	for (int c = 0; c < len; c++) {
            LinuxAccAddress acc = cached.get(c);
            if (acc.email_address==pkey) {
                LinuxServerAccount lsa=acc.getLinuxServerAccount();
                if(lsa!=null) matches.add(lsa);
            }
	}
	return matches;
    }

    List<LinuxAccAddress> getLinuxAccAddresses(EmailAddress address) {
        return getIndexedRows(LinuxAccAddress.COLUMN_EMAIL_ADDRESS, address.pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.LINUX_ACC_ADDRESSES;
    }

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.ADD_LINUX_ACC_ADDRESS)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_LINUX_ACC_ADDRESS, args, 3, err)) {
                String addr=args[1];
                int pos=addr.indexOf('@');
                if(pos==-1) {
                    err.print("aosh: "+AOSHCommand.ADD_LINUX_ACC_ADDRESS+": invalid email address: ");
                    err.println(addr);
                    err.flush();
                } else {
                    int pkey=connector.simpleAOClient.addLinuxAccAddress(
                        addr.substring(0, pos),
                        addr.substring(pos+1),
                        args[2],
                        args[3]
                    );
                    out.println(pkey);
                    out.flush();
                }
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_LINUX_ACC_ADDRESS)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_LINUX_ACC_ADDRESS, args, 3, err)) {
                String addr=args[1];
                int pos=addr.indexOf('@');
                if(pos==-1) {
                    err.print("aosh: "+AOSHCommand.REMOVE_LINUX_ACC_ADDRESS+": invalid email address: ");
                    err.println(addr);
                    err.flush();
                } else {
                    connector.simpleAOClient.removeLinuxAccAddress(
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