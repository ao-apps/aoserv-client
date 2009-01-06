package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  MajordomoServer
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class MajordomoServerTable extends CachedTableIntegerKey<MajordomoServer> {

    MajordomoServerTable(AOServConnector connector) {
	super(connector, MajordomoServer.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(MajordomoServer.COLUMN_DOMAIN_name+'.'+EmailDomain.COLUMN_DOMAIN_name, ASCENDING),
        new OrderBy(MajordomoServer.COLUMN_DOMAIN_name+'.'+EmailDomain.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING),
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    void addMajordomoServer(
        EmailDomain emailDomain,
        LinuxServerAccount linuxServerAccount,
        LinuxServerGroup linuxServerGroup,
        MajordomoVersion majordomoVersion
    ) {
        connector.requestUpdateIL(
            AOServProtocol.CommandID.ADD,
            SchemaTable.TableID.MAJORDOMO_SERVERS,
            emailDomain.pkey,
            linuxServerAccount.pkey,
            linuxServerGroup.pkey,
            majordomoVersion.pkey
        );
    }

    public MajordomoServer get(Object pkey) {
	return getUniqueRow(MajordomoServer.COLUMN_DOMAIN, pkey);
    }

    public MajordomoServer get(int pkey) {
	return getUniqueRow(MajordomoServer.COLUMN_DOMAIN, pkey);
    }

    List<MajordomoServer> getMajordomoServers(AOServer ao) {
        int aoPKey=ao.pkey;
        List<MajordomoServer> cached=getRows();
        int size=cached.size();
        List<MajordomoServer> matches=new ArrayList<MajordomoServer>(size);
        for(int c=0;c<size;c++) {
            MajordomoServer ms=cached.get(c);
            if(ms.getDomain().ao_server==aoPKey) matches.add(ms);
        }
        return matches;
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.MAJORDOMO_SERVERS;
    }

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
        String command=args[0];
        if(command.equalsIgnoreCase(AOSHCommand.ADD_MAJORDOMO_SERVER)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_MAJORDOMO_SERVER, args, 5, err)) {
                connector.simpleAOClient.addMajordomoServer(
                    args[1],
                    args[2],
                    args[3],
                    args[4],
                    args[5]
                );
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_MAJORDOMO_SERVER)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_MAJORDOMO_SERVER, args, 2, err)) {
                connector.simpleAOClient.removeMajordomoServer(
                    args[1],
                    args[2]
                );
            }
            return true;
        }
        return false;
    }
}