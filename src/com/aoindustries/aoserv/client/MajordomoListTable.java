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
 * @see  MajordomoList
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class MajordomoListTable extends CachedTableIntegerKey<MajordomoList> {

    MajordomoListTable(AOServConnector connector) {
	super(connector, MajordomoList.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(MajordomoList.COLUMN_MAJORDOMO_SERVER_name+'.'+MajordomoServer.COLUMN_DOMAIN_name+'.'+EmailDomain.COLUMN_DOMAIN_name, ASCENDING),
        new OrderBy(MajordomoList.COLUMN_MAJORDOMO_SERVER_name+'.'+MajordomoServer.COLUMN_DOMAIN_name+'.'+EmailDomain.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING),
        new OrderBy(MajordomoList.COLUMN_NAME_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    int addMajordomoList(
        MajordomoServer majordomoServer,
        String listName
    ) throws IOException, SQLException {
        return connector.requestIntQueryIL(
            AOServProtocol.CommandID.ADD,
            SchemaTable.TableID.MAJORDOMO_LISTS,
            majordomoServer.pkey,
            listName
        );
    }

    public MajordomoList get(Object pkey) {
        try {
            return getUniqueRow(MajordomoList.COLUMN_EMAIL_LIST, pkey);
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public MajordomoList get(int pkey) throws IOException, SQLException {
	return getUniqueRow(MajordomoList.COLUMN_EMAIL_LIST, pkey);
    }

    MajordomoList getMajordomoList(MajordomoServer ms, String listName) throws IOException, SQLException {
        int majordomo_server=ms.pkey;
        List<MajordomoList> mls=getRows();
        int len=mls.size();
        for(int c=0;c<len;c++) {
            MajordomoList ml=mls.get(c);
            if(
                ml.majordomo_server==majordomo_server
                && ml.name.equals(listName)
            ) return ml;
        }
        return null;
    }

    List<MajordomoList> getMajordomoLists(MajordomoServer server) throws IOException, SQLException {
        return getIndexedRows(MajordomoList.COLUMN_MAJORDOMO_SERVER, server.pkey);
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.MAJORDOMO_LISTS;
    }

    @Override
    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
        String command=args[0];
        if(command.equalsIgnoreCase(AOSHCommand.ADD_MAJORDOMO_LIST)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_MAJORDOMO_LIST, args, 3, err)) {
                int pkey=connector.simpleAOClient.addMajordomoList(
                    args[1],
                    args[2],
                    args[3]
                );
                out.println(pkey);
                out.flush();
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.CHECK_MAJORDOMO_LIST_NAME)) {
            if(AOSH.checkParamCount(AOSHCommand.CHECK_MAJORDOMO_LIST_NAME, args, 1, err)) {
                try {
                    SimpleAOClient.checkMajordomoListName(args[1]);
                    out.println("true");
                } catch(IllegalArgumentException iae) {
                    out.print("aosh: "+AOSHCommand.CHECK_MAJORDOMO_LIST_NAME+": ");
                    out.println(iae.getMessage());
                }
                out.flush();
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.GET_MAJORDOMO_INFO_FILE)) {
            if(AOSH.checkParamCount(AOSHCommand.GET_MAJORDOMO_INFO_FILE, args, 3, err)) {
                out.println(connector.simpleAOClient.getMajordomoInfoFile(args[1], args[2], args[3]));
                out.flush();
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.GET_MAJORDOMO_INTRO_FILE)) {
            if(AOSH.checkParamCount(AOSHCommand.GET_MAJORDOMO_INTRO_FILE, args, 3, err)) {
                out.println(connector.simpleAOClient.getMajordomoIntroFile(args[1], args[2], args[3]));
                out.flush();
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.SET_MAJORDOMO_INFO_FILE)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_MAJORDOMO_INFO_FILE, args, 4, err)) {
                connector.simpleAOClient.setMajordomoInfoFile(args[1], args[2], args[3], args[4]);
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.SET_MAJORDOMO_INTRO_FILE)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_MAJORDOMO_INTRO_FILE, args, 4, err)) {
                connector.simpleAOClient.setMajordomoIntroFile(args[1], args[2], args[3], args[4]);
            }
            return true;
        }
        return false;
    }
}