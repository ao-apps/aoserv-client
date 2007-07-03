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

    public int addEmailList(
	String path,
	LinuxServerAccount linuxAccountObject,
	LinuxServerGroup linuxGroupObject
    ) throws IllegalArgumentException {
	if (!EmailList.isValidRegularPath(path)) throw new IllegalArgumentException("Invalid list path: " + path);

	return connector.requestIntQueryIL(
            AOServProtocol.CommandID.ADD,
            SchemaTable.TableID.EMAIL_LISTS,
            path,
            linuxAccountObject.pkey,
            linuxGroupObject.pkey
	);
    }

    public EmailList get(Object pkey) {
	return getUniqueRow(EmailList.COLUMN_PKEY, pkey);
    }

    public EmailList get(int pkey) {
	return getUniqueRow(EmailList.COLUMN_PKEY, pkey);
    }

    List<EmailList> getEmailLists(Business business) {
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

    List<EmailList> getEmailLists(Package pack) {
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

    List<EmailList> getEmailLists(LinuxServerAccount lsa) {
        return getIndexedRows(EmailList.COLUMN_LINUX_ACCOUNT, lsa.pkey);
    }

    EmailList getEmailList(AOServer ao, String path) {
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

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
        String command=args[0];
        if(command.equalsIgnoreCase(AOSHCommand.ADD_EMAIL_LIST)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_EMAIL_LIST, args, 4, err)) {
                out.println(
                    connector.simpleAOClient.addEmailList(
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
                    connector.simpleAOClient.disableEmailList(
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
                connector.simpleAOClient.enableEmailList(args[1], args[2]);
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.GET_EMAIL_LIST)) {
            if(AOSH.checkParamCount(AOSHCommand.GET_EMAIL_LIST, args, 2, err)) {
                out.println(connector.simpleAOClient.getEmailListAddressList(args[1], args[2]));
                out.flush();
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_EMAIL_LIST)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_EMAIL_LIST, args, 2, err)) {
                connector.simpleAOClient.removeEmailList(args[1], args[2]);
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.SET_EMAIL_LIST)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_EMAIL_LIST, args, 3, err)) {
                connector.simpleAOClient.setEmailListAddressList(args[1], args[2], args[3]);
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.SET_EMAIL_LIST_BACKUP_RETENTION)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_EMAIL_LIST_BACKUP_RETENTION, args, 3, err)) {
                connector.simpleAOClient.setEmailListBackupRetention(
                    args[1],
                    args[2],
                    AOSH.parseShort(args[3], "backup_retention")
                );
            }
            return true;
        }
        return false;
    }
}