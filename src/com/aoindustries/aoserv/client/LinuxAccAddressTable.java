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

    int addLinuxAccAddress(EmailAddress emailAddressObject, LinuxAccount linuxAccountObject) {
	return connector.requestIntQueryIL(
            AOServProtocol.CommandID.ADD,
            SchemaTable.TableID.LINUX_ACC_ADDRESSES,
            emailAddressObject.pkey,
            linuxAccountObject.pkey
	);
    }

    public LinuxAccAddress get(Object pkey) {
	return getUniqueRow(LinuxAccAddress.COLUMN_PKEY, pkey);
    }

    public LinuxAccAddress get(int pkey) {
	return getUniqueRow(LinuxAccAddress.COLUMN_PKEY, pkey);
    }

    List<EmailAddress> getEmailAddresses(LinuxAccount linuxAccount) {
	List<LinuxAccAddress> cached = getRows();
	int len = cached.size();
        List<EmailAddress> matches=new ArrayList<EmailAddress>(len);
	for (int c = 0; c < len; c++) {
            LinuxAccAddress acc = cached.get(c);
            LinuxAccount la=acc.getLinuxAccount();
            // la may be null when data filtered
            if(la!=null && la.equals(linuxAccount)) matches.add(acc.getEmailAddress());
	}
	return matches;
    }

    List<EmailAddress> getEmailAddresses(LinuxServerAccount lsa) {
        LinuxAccount la=lsa.getLinuxAccount();
        int aoPKey=lsa.ao_server;

	List<LinuxAccAddress> cached = getRows();
	int len = cached.size();
        List<EmailAddress> matches=new ArrayList<EmailAddress>(len);
	for (int c = 0; c < len; c++) {
            LinuxAccAddress acc=cached.get(c);
            LinuxAccount accLA=acc.getLinuxAccount();
            // la may be null when data filtered
            if(accLA!=null && accLA.equals(la)) {
                EmailAddress accEA=acc.getEmailAddress();
                if(accEA.getDomain().ao_server==aoPKey) matches.add(accEA);
            }
	}
	return matches;
    }

    List<LinuxAccAddress> getLinuxAccAddresses(LinuxServerAccount lsa) {
        LinuxAccount la=lsa.getLinuxAccount();
        int aoPKey=lsa.ao_server;

	List<LinuxAccAddress> cached = getRows();
	int len = cached.size();
        List<LinuxAccAddress> matches=new ArrayList<LinuxAccAddress>(len);
	for (int c = 0; c < len; c++) {
            LinuxAccAddress acc=cached.get(c);
            LinuxAccount accLA=acc.getLinuxAccount();
            // la may be null when data filtered
            if(accLA!=null && accLA.equals(la)) {
                EmailAddress accEA=acc.getEmailAddress();
                if(accEA.getDomain().ao_server==aoPKey) matches.add(acc);
            }
	}
	return matches;
    }

    public LinuxAccAddress getLinuxAccAddress(EmailAddress ea, LinuxAccount la) {
        int pkey=ea.pkey;
        String username=la.pkey;
        List<LinuxAccAddress> cached=getRows();
        int size=cached.size();
        for(int c=0;c<size;c++) {
            LinuxAccAddress laa=cached.get(c);
            if(laa.email_address==pkey && laa.linux_account.equals(username)) return laa;
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

    List<LinuxAccount> getLinuxAccounts(EmailAddress address) {
        int pkey=address.pkey;
	List<LinuxAccAddress> cached = getRows();
	int len = cached.size();
        List<LinuxAccount> matches=new ArrayList<LinuxAccount>(len);
	for (int c = 0; c < len; c++) {
            LinuxAccAddress acc = cached.get(c);
            if (acc.email_address==pkey) {
                LinuxAccount la=acc.getLinuxAccount();
                if(la!=null) matches.add(la);
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