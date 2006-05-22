package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.profiler.*;
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

    void addMajordomoServer(
        EmailDomain emailDomain,
        LinuxServerAccount linuxServerAccount,
        LinuxServerGroup linuxServerGroup,
        MajordomoVersion majordomoVersion
    ) {
        Profiler.startProfile(Profiler.UNKNOWN, MajordomoServerTable.class, "addMajordomoServer(EmailDomain,LinuxServerAccount,LinuxServerGroup,MajordomoVersion)", null);
        try {
            connector.requestUpdateIL(
                AOServProtocol.ADD,
                SchemaTable.MAJORDOMO_SERVERS,
                emailDomain.pkey,
                linuxServerAccount.pkey,
                linuxServerGroup.pkey,
                majordomoVersion.pkey
            );
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public MajordomoServer get(Object pkey) {
	return getUniqueRow(MajordomoServer.COLUMN_DOMAIN, pkey);
    }

    public MajordomoServer get(int pkey) {
	return getUniqueRow(MajordomoServer.COLUMN_DOMAIN, pkey);
    }

    List<MajordomoServer> getMajordomoServers(AOServer ao) {
        Profiler.startProfile(Profiler.UNKNOWN, MajordomoServerTable.class, "getMajordomoServers(AOServer)", null);
        try {
            int aoPKey=ao.pkey;
            List<MajordomoServer> cached=getRows();
            int size=cached.size();
            List<MajordomoServer> matches=new ArrayList<MajordomoServer>(size);
            for(int c=0;c<size;c++) {
                MajordomoServer ms=cached.get(c);
                if(ms.getDomain().ao_server==aoPKey) matches.add(ms);
            }
            return matches;
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    int getTableID() {
        return SchemaTable.MAJORDOMO_SERVERS;
    }

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
        Profiler.startProfile(Profiler.UNKNOWN, MajordomoServerTable.class, "handleCommand(String[],InputStream,TerminalWriter,TerminalWriter,boolean)", null);
        try {
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
            } else if(command.equalsIgnoreCase(AOSHCommand.SET_MAJORDOMO_SERVER_BACKUP_RETENTION)) {
                if(AOSH.checkParamCount(AOSHCommand.SET_MAJORDOMO_SERVER_BACKUP_RETENTION, args, 3, err)) {
                    connector.simpleAOClient.setMajordomoServerBackupRetention(
                        args[1],
                        args[2],
                        AOSH.parseShort(args[3], "backup_retention")
                    );
                }
                return true;
            }
            return false;
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }
}