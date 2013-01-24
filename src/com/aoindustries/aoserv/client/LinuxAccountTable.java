/*
 * Copyright 2001-2012 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.io.*;
import com.aoindustries.util.IntList;
import com.aoindustries.util.StringUtility;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  LinuxAccount
 *
 * @author  AO Industries, Inc.
 */
final public class LinuxAccountTable extends CachedTableStringKey<LinuxAccount> {

    LinuxAccountTable(AOServConnector connector) {
        super(connector, LinuxAccount.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(LinuxAccount.COLUMN_USERNAME_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    void addLinuxAccount(
    	final Username usernameObject,
        final String primaryGroup,
        final String name,
        String office_location,
        String office_phone,
        String home_phone,
        final String type,
        final String shell
    ) throws IOException, SQLException {
        String validity=LinuxAccount.checkGECOS(name, "full name");
        if(validity!=null) throw new SQLException(validity);

        if(office_location!=null && office_location.length()==0) office_location=null;
        final String finalOfficeLocation = office_location;
        if(office_phone!=null && office_phone.length()==0) office_phone=null;
        final String finalOfficePhone = office_phone;
        if(home_phone!=null && home_phone.length()==0) home_phone=null;
        final String finalHomePhone = home_phone;
        connector.requestUpdate(
            true,
            new AOServConnector.UpdateRequest() {
                IntList invalidateList;

                public void writeRequest(CompressedDataOutputStream out) throws IOException {
                    out.writeCompressedInt(AOServProtocol.CommandID.ADD.ordinal());
                    out.writeCompressedInt(SchemaTable.TableID.LINUX_ACCOUNTS.ordinal());
                    out.writeUTF(usernameObject.pkey);
                    out.writeUTF(primaryGroup);
                    out.writeUTF(name);
                    out.writeBoolean(finalOfficeLocation!=null); if(finalOfficeLocation!=null) out.writeUTF(finalOfficeLocation);
                    out.writeBoolean(finalOfficePhone!=null); if(finalOfficePhone!=null) out.writeUTF(finalOfficePhone);
                    out.writeBoolean(finalHomePhone!=null); if(finalHomePhone!=null) out.writeUTF(finalHomePhone);
                    out.writeUTF(type);
                    out.writeUTF(shell);
                }

                public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
                    int code=in.readByte();
                    if(code==AOServProtocol.DONE) invalidateList=AOServConnector.readInvalidateList(in);
                    else {
                        AOServProtocol.checkResult(code, in);
                        throw new IOException("Unexpected response code: "+code);
                    }
                }

                public void afterRelease() {
                    connector.tablesUpdated(invalidateList);
                }
            }
        );
    }
    
    public LinuxAccount get(String username) throws IOException, SQLException {
        return getUniqueRow(LinuxAccount.COLUMN_USERNAME, username);
    }

    public List<LinuxAccount> getMailAccounts() throws IOException, SQLException {
        List<LinuxAccount> cached = getRows();
        int len = cached.size();
        List<LinuxAccount> matches=new ArrayList<LinuxAccount>(len);
        for (int c = 0; c < len; c++) {
            LinuxAccount linuxAccount = cached.get(c);
            if (linuxAccount.getType().isEmail()) matches.add(linuxAccount);
        }
        return matches;
    }

    List<LinuxAccount> getMailAccounts(Business business) throws IOException, SQLException {
        String accounting=business.pkey;
        List<LinuxAccount> cached = getRows();
        int len = cached.size();
        List<LinuxAccount> matches=new ArrayList<LinuxAccount>(len);
        for (int c = 0; c < len; c++) {
            LinuxAccount linuxAccount = cached.get(c);
            if (
                linuxAccount.getType().isEmail()
                && linuxAccount.getUsername().getPackage().accounting.equals(accounting)
            ) matches.add(linuxAccount);
        }
        return matches;
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.LINUX_ACCOUNTS;
    }

    @Override
    boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
        String command=args[0];
        if(command.equalsIgnoreCase(AOSHCommand.ADD_LINUX_ACCOUNT)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_LINUX_ACCOUNT, args, 8, err)) {
                connector.getSimpleAOClient().addLinuxAccount(
                    args[1],
                    args[2],
                    args[3],
                    args[4],
                    args[5],
                    args[6],
                    args[7],
                    args[8]
                );
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.ARE_LINUX_ACCOUNT_PASSWORDS_SET)) {
            if(AOSH.checkParamCount(AOSHCommand.ARE_LINUX_ACCOUNT_PASSWORDS_SET, args, 1, err)) {
                int result=connector.getSimpleAOClient().areLinuxAccountPasswordsSet(args[1]);
                if(result==PasswordProtected.NONE) out.println("none");
                else if(result==PasswordProtected.SOME) out.println("some");
                else if(result==PasswordProtected.ALL) out.println("all");
                else throw new RuntimeException("Unexpected value for result: "+result);
                out.flush();
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.CHECK_LINUX_ACCOUNT_NAME)) {
            if(AOSH.checkParamCount(AOSHCommand.CHECK_LINUX_ACCOUNT_NAME, args, 1, err)) {
                try {
                    SimpleAOClient.checkLinuxAccountName(args[1]);
                    out.println("true");
                } catch(IllegalArgumentException ia) {
                    out.println("false");
                }
                out.flush();
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.CHECK_LINUX_ACCOUNT_PASSWORD)) {
            if(AOSH.checkParamCount(AOSHCommand.CHECK_LINUX_ACCOUNT_PASSWORD, args, 2, err)) {
                List<PasswordChecker.Result> results = connector.getSimpleAOClient().checkLinuxAccountPassword(args[1], args[2]);
                if(PasswordChecker.hasResults(results)) {
                    PasswordChecker.printResults(results, out);
                    out.flush();
                }
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.CHECK_LINUX_ACCOUNT_USERNAME)) {
            if(AOSH.checkParamCount(AOSHCommand.CHECK_LINUX_ACCOUNT_USERNAME, args, 1, err)) {
                SimpleAOClient.checkLinuxAccountUsername(args[1]);
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.DISABLE_LINUX_ACCOUNT)) {
            if(AOSH.checkParamCount(AOSHCommand.DISABLE_LINUX_ACCOUNT, args, 2, err)) {
                out.println(
                    connector.getSimpleAOClient().disableLinuxAccount(
                        args[1],
                        args[2]
                    )
                );
                out.flush();
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.ENABLE_LINUX_ACCOUNT)) {
            if(AOSH.checkParamCount(AOSHCommand.ENABLE_LINUX_ACCOUNT, args, 1, err)) {
                connector.getSimpleAOClient().enableLinuxAccount(args[1]);
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.GENERATE_PASSWORD)) {
            if(AOSH.checkParamCount(AOSHCommand.GENERATE_PASSWORD, args, 0, err)) {
                out.println(connector.getSimpleAOClient().generatePassword());
                out.flush();
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_LINUX_ACCOUNT)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_LINUX_ACCOUNT, args, 1, err)) {
                connector.getSimpleAOClient().removeLinuxAccount(args[1]);
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.SET_LINUX_ACCOUNT_HOME_PHONE)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_LINUX_ACCOUNT_HOME_PHONE, args, 2, err)) {
                connector.getSimpleAOClient().setLinuxAccountHomePhone(args[1], args[2]);
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.SET_LINUX_ACCOUNT_NAME)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_LINUX_ACCOUNT_NAME, args, 2, err)) {
                connector.getSimpleAOClient().setLinuxAccountName(args[1], args[2]);
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.SET_LINUX_ACCOUNT_OFFICE_LOCATION)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_LINUX_ACCOUNT_OFFICE_LOCATION, args, 2, err)) {
                connector.getSimpleAOClient().setLinuxAccountOfficeLocation(args[1], args[2]);
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.SET_LINUX_ACCOUNT_OFFICE_PHONE)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_LINUX_ACCOUNT_OFFICE_PHONE, args, 2, err)) {
                connector.getSimpleAOClient().setLinuxAccountOfficePhone(args[1], args[2]);
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.SET_LINUX_ACCOUNT_PASSWORD)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_LINUX_ACCOUNT_PASSWORD, args, 2, err)) {
                connector.getSimpleAOClient().setLinuxAccountPassword(args[1], args[2]);
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.SET_LINUX_ACCOUNT_SHELL)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_LINUX_ACCOUNT_SHELL, args, 2, err)) {
                connector.getSimpleAOClient().setLinuxAccountShell(args[1], args[2]);
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.WAIT_FOR_LINUX_ACCOUNT_REBUILD)) {
            if(AOSH.checkParamCount(AOSHCommand.WAIT_FOR_LINUX_ACCOUNT_REBUILD, args, 1, err)) {
                connector.getSimpleAOClient().waitForLinuxAccountRebuild(args[1]);
            }
            return true;
        }
        return false;
    }

    void waitForRebuild(AOServer aoServer) throws IOException, SQLException {
        connector.requestUpdate(
            true,
            AOServProtocol.CommandID.WAIT_FOR_REBUILD,
            SchemaTable.TableID.LINUX_ACCOUNTS,
            aoServer.pkey
        );
    }
}