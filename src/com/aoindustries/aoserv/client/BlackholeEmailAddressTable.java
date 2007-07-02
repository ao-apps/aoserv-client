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
 * @see BlackholeEmailAddress
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class BlackholeEmailAddressTable extends CachedTableIntegerKey<BlackholeEmailAddress> {

    BlackholeEmailAddressTable(AOServConnector connector) {
	super(connector, BlackholeEmailAddress.class);
    }

    public BlackholeEmailAddress get(Object address) {
        return get(((Integer)address).intValue());
    }

    public BlackholeEmailAddress get(int address) {
	return getUniqueRow(BlackholeEmailAddress.COLUMN_EMAIL_ADDRESS, address);
    }

    List<BlackholeEmailAddress> getBlackholeEmailAddresses(AOServer ao) {
        int aoPKey=ao.pkey;
	List<BlackholeEmailAddress> cached = getRows();
	int len = cached.size();
        List<BlackholeEmailAddress> matches=new ArrayList<BlackholeEmailAddress>(len);
	for (int c = 0; c < len; c++) {
            BlackholeEmailAddress blackhole=cached.get(c);
            if(blackhole.getEmailAddress().getDomain().ao_server==aoPKey) matches.add(blackhole);
	}
	return matches;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.BLACKHOLE_EMAIL_ADDRESSES;
    }

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
	String command=args[0];
        if(command.equalsIgnoreCase(AOSHCommand.REMOVE_BLACKHOLE_EMAIL_ADDRESS)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_BLACKHOLE_EMAIL_ADDRESS, args, 2, err)) {
                String addr=args[1];
                int pos=addr.indexOf('@');
                if(pos==-1) {
                    err.print("aosh: "+AOSHCommand.REMOVE_BLACKHOLE_EMAIL_ADDRESS+": invalid email address: ");
                    err.println(addr);
                    err.flush();
                } else {
                    connector.simpleAOClient.removeBlackholeEmailAddress(
                        addr.substring(0, pos),
                        addr.substring(pos+1),
                        args[2]
                    );
                }
            }
            return true;
	}
	return false;
    }
}
