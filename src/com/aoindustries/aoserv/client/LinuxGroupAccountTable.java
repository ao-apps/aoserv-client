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
 * @see  LinuxGroupAccount
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class LinuxGroupAccountTable extends CachedTableIntegerKey<LinuxGroupAccount> {

    private boolean hashBuilt=false;
    private final Map<String,LinuxGroupAccount> hash=new HashMap<String,LinuxGroupAccount>();

    /**
     * The group name of the primary group is hashed on first use for fast
     * lookups.
     */
    private boolean primaryHashBuilt=false;
    private final Map<String,LinuxGroupAccount> primaryHash=new HashMap<String,LinuxGroupAccount>();

    LinuxGroupAccountTable(AOServConnector connector) {
	super(connector, LinuxGroupAccount.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(LinuxGroupAccount.COLUMN_GROUP_NAME_name, ASCENDING),
        new OrderBy(LinuxGroupAccount.COLUMN_USERNAME_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    int addLinuxGroupAccount(
	LinuxGroup groupNameObject,
	LinuxAccount usernameObject
    ) throws IOException, SQLException {
        int pkey=connector.requestIntQueryIL(
            AOServProtocol.CommandID.ADD,
            SchemaTable.TableID.LINUX_GROUP_ACCOUNTS,
            groupNameObject.pkey,
            usernameObject.pkey
        );
        return pkey;
    }

    public LinuxGroupAccount get(int pkey) throws IOException, SQLException {
    	return getUniqueRow(LinuxGroupAccount.COLUMN_PKEY, pkey);
    }

    LinuxGroupAccount getLinuxGroupAccount(
	String groupName,
	String username
    ) throws IOException, SQLException {
        synchronized(hash) {
            if(!hashBuilt) {
                hash.clear();
                List<LinuxGroupAccount> list=getRows();
                int len=list.size();
                for(int c=0;c<len;c++) {
                    LinuxGroupAccount lga=list.get(c);
                    hash.put(lga.group_name+':'+lga.username, lga);
                }
                hashBuilt=true;
            }
            return hash.get(groupName+':'+username);
        }
    }

    List<LinuxGroup> getLinuxGroups(LinuxAccount linuxAccount) throws IOException, SQLException {
        String username = linuxAccount.pkey;
        List<LinuxGroupAccount> cached = getRows();
        int len = cached.size();
        List<LinuxGroup> matches=new ArrayList<LinuxGroup>(LinuxGroupAccount.MAX_GROUPS);
        for (int c = 0; c < len; c++) {
            LinuxGroupAccount lga = cached.get(c);
            if (lga.username.equals(username)) matches.add(lga.getLinuxGroup());
        }
        return matches;
    }

    LinuxGroup getPrimaryGroup(LinuxAccount account) throws IOException, SQLException {
        synchronized(primaryHash) {
            if(account==null) throw new IllegalArgumentException("param account is null");
            // Rebuild the hash if needed
            if(!primaryHashBuilt) {
                List<LinuxGroupAccount> cache=getRows();
                primaryHash.clear();
                int len=cache.size();
                for(int c=0;c<len;c++) {
                    LinuxGroupAccount lga=cache.get(c);
                    if(lga.isPrimary()) primaryHash.put(lga.username, lga);
                }
                primaryHashBuilt=true;
            }
            LinuxGroupAccount lga=primaryHash.get(account.pkey);
            // May be filtered
            if(lga==null) return null;
            return lga.getLinuxGroup();
        }
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.LINUX_GROUP_ACCOUNTS;
    }

    @Override
    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
        String command=args[0];
        if(command.equalsIgnoreCase(AOSHCommand.ADD_LINUX_GROUP_ACCOUNT)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_LINUX_GROUP_ACCOUNT, args, 2, err)) {
                connector.getSimpleAOClient().addLinuxGroupAccount(
                    args[1],
                    args[2]
                );
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_LINUX_GROUP_ACCOUNT)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_LINUX_GROUP_ACCOUNT, args, 2, err)) {
                connector.getSimpleAOClient().removeLinuxGroupAccount(
                    args[1],
                    args[2]
                );
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.SET_PRIMARY_LINUX_GROUP_ACCOUNT)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_PRIMARY_LINUX_GROUP_ACCOUNT, args, 2, err)) {
                connector.getSimpleAOClient().setPrimaryLinuxGroupAccount(
                    args[1],
                    args[2]
                );
            }
            return true;
        }
        return false;
    }

    @Override
    public void clearCache() {
        super.clearCache();
        synchronized(hash) {
            hashBuilt=false;
        }
        synchronized(primaryHash) {
            primaryHashBuilt=false;
        }
    }
}
