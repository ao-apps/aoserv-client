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
 * @see  EmailList
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class EmailListTable extends CachedTableIntegerKey<EmailList> {

    EmailListTable(AOServConnector connector) {
	super(connector, EmailList.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(EmailList.COLUMN_PATH_name, ASCENDING),
        new OrderBy(EmailList.COLUMN_LINUX_SERVER_ACCOUNT_name+'.'+LinuxServerAccount.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public int addEmailList(
	String path,
	LinuxServerAccount linuxAccountObject,
	LinuxServerGroup linuxGroupObject
    ) throws IllegalArgumentException, IOException, SQLException {
	if (!EmailList.isValidRegularPath(path)) throw new IllegalArgumentException("Invalid list path: " + path);

	return connector.requestIntQueryIL(
            AOServProtocol.CommandID.ADD,
            SchemaTable.TableID.EMAIL_LISTS,
            path,
            linuxAccountObject.pkey,
            linuxGroupObject.pkey
	);
    }

    public EmailList get(int pkey) throws IOException, SQLException {
    	return getUniqueRow(EmailList.COLUMN_PKEY, pkey);
    }

    List<EmailList> getEmailLists(Business business) throws IOException, SQLException {
        String accounting=business.pkey;
        List<EmailList> cached = getRows();
        int len = cached.size();
        List<EmailList> matches=new ArrayList<EmailList>(len);
        for (int c = 0; c < len; c++) {
            EmailList list = cached.get(c);
            if (
                list
                .getLinuxServerGroup()
                .getLinuxGroup()
                .getPackage()
                .accounting
                .equals(accounting)
            ) matches.add(list);
        }
        return matches;
    }

    List<EmailList> getEmailLists(Package pack) throws IOException, SQLException {
        String packName=pack.name;

        List<EmailList> cached=getRows();
        int size=cached.size();
        List<EmailList> matches=new ArrayList<EmailList>(size);
        for(int c=0;c<size;c++) {
            EmailList list=cached.get(c);
            if(list.getLinuxServerGroup().getLinuxGroup().packageName.equals(packName)) matches.add(list);
        }
        return matches;
    }

    List<EmailList> getEmailLists(LinuxServerAccount lsa) throws IOException, SQLException {
        return getIndexedRows(EmailList.COLUMN_LINUX_SERVER_ACCOUNT, lsa.pkey);
    }

    EmailList getEmailList(AOServer ao, String path) throws IOException, SQLException {
        int aoPKey=ao.pkey;
        List<EmailList> cached=getRows();
        int size=cached.size();
        for(int c=0;c<size;c++) {
            EmailList list=cached.get(c);
            if(list.getLinuxServerGroup().ao_server==aoPKey && list.path.equals(path)) return list;
        }
        return null;
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.EMAIL_LISTS;
    }

    @Override
    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
        String command=args[0];
        if(command.equalsIgnoreCase(AOSHCommand.ADD_EMAIL_LIST)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_EMAIL_LIST, args, 4, err)) {
                out.println(
                    connector.getSimpleAOClient().addEmailList(
                        args[1],
                        args[2],
                        args[3],
                        args[4]
                    )
                );
                out.flush();
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.CHECK_EMAIL_LIST_PATH)) {
            if(AOSH.checkMinParamCount(AOSHCommand.CHECK_EMAIL_LIST_PATH, args, 1, err)) {
                for(int c=1;c<args.length;c++) {
                    try {
                        SimpleAOClient.checkEmailListPath(args[c]);
                        if(args.length>2) {
                            out.print(args[c]);
                            out.print(": ");
                        }
                        out.println("true");
                    } catch(IllegalArgumentException ia) {
                        if(args.length>2) {
                            out.print(args[c]);
                            out.print(": ");
                        }
                        out.println(ia.getMessage());
                    }
                    out.flush();
                }
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.DISABLE_EMAIL_LIST)) {
            if(AOSH.checkParamCount(AOSHCommand.DISABLE_EMAIL_LIST, args, 3, err)) {
                out.println(
                    connector.getSimpleAOClient().disableEmailList(
                        args[1],
                        args[2],
                        args[3]
                    )
                );
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.ENABLE_EMAIL_LIST)) {
            if(AOSH.checkParamCount(AOSHCommand.ENABLE_EMAIL_LIST, args, 2, err)) {
                connector.getSimpleAOClient().enableEmailList(args[1], args[2]);
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.GET_EMAIL_LIST)) {
            if(AOSH.checkParamCount(AOSHCommand.GET_EMAIL_LIST, args, 2, err)) {
                out.println(connector.getSimpleAOClient().getEmailListAddressList(args[1], args[2]));
                out.flush();
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_EMAIL_LIST)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_EMAIL_LIST, args, 2, err)) {
                connector.getSimpleAOClient().removeEmailList(args[1], args[2]);
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.SET_EMAIL_LIST)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_EMAIL_LIST, args, 3, err)) {
                connector.getSimpleAOClient().setEmailListAddressList(args[1], args[2], args[3]);
            }
            return true;
        }
        return false;
    }
}