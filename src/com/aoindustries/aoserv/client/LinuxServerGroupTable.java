/*
 * Copyright 2001-2013 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.io.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  LinuxServerGroup
 *
 * @author  AO Industries, Inc.
 */
final public class LinuxServerGroupTable extends CachedTableIntegerKey<LinuxServerGroup> {

    LinuxServerGroupTable(AOServConnector connector) {
	super(connector, LinuxServerGroup.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(LinuxServerGroup.COLUMN_NAME_name, ASCENDING),
        new OrderBy(LinuxServerGroup.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    int addLinuxServerGroup(LinuxGroup linuxGroup, AOServer aoServer) throws IOException, SQLException {
        int pkey=connector.requestIntQueryIL(
            true,
            AOServProtocol.CommandID.ADD,
            SchemaTable.TableID.LINUX_SERVER_GROUPS,
            linuxGroup.pkey,
            aoServer.pkey
        );
        return pkey;
    }

    @Override
    public void clearCache() {
        super.clearCache();
        synchronized(gidHash) {
            gidHashBuilt=false;
        }
        synchronized(nameHash) {
            nameHashBuilt=false;
        }
    }

    public LinuxServerGroup get(int pkey) throws IOException, SQLException {
    	return getUniqueRow(LinuxServerGroup.COLUMN_PKEY, pkey);
    }

    LinuxServerGroup getLinuxServerGroup(AOServer aoServer, Business business) throws IOException, SQLException {
        AccountingCode accounting=business.pkey;
        int aoPKey=aoServer.pkey;

        List<LinuxServerGroup> list = getRows();
        int len = list.size();
        for (int c = 0; c < len; c++) {
            // Must be for the correct server
            LinuxServerGroup group = list.get(c);
            if (aoPKey==group.ao_server) {
                // Must be for the correct business
                LinuxGroup linuxGroup = group.getLinuxGroup();
                Package pk=linuxGroup.getPackage();
                if (pk!=null && pk.accounting.equals(accounting)) {
                    // Must be a user group
                    if (linuxGroup.getLinuxGroupType().pkey.equals(LinuxGroupType.USER)) return group;
                }
            }
        }
        return null;
    }

    private boolean nameHashBuilt=false;
    private final Map<Integer,Map<String,LinuxServerGroup>> nameHash=new HashMap<Integer,Map<String,LinuxServerGroup>>();

    LinuxServerGroup getLinuxServerGroup(AOServer aoServer, String group_name) throws IOException, SQLException {
        synchronized(nameHash) {
            if(!nameHashBuilt) {
                nameHash.clear();

                List<LinuxServerGroup> list=getRows();
                int len=list.size();
                for(int c=0; c<len; c++) {
                    LinuxServerGroup lsg=list.get(c);
                    Integer I=Integer.valueOf(lsg.ao_server);
                    Map<String,LinuxServerGroup> serverHash=nameHash.get(I);
                    if(serverHash==null) nameHash.put(I, serverHash=new HashMap<String,LinuxServerGroup>());
                    if(serverHash.put(lsg.name, lsg)!=null) throw new SQLException("LinuxServerGroup name exists more than once on server: "+lsg.name+" on "+I.intValue());

                }
                nameHashBuilt=true;
            }
            Map<String,LinuxServerGroup> serverHash=nameHash.get(Integer.valueOf(aoServer.pkey));
            if(serverHash==null) return null;
            return serverHash.get(group_name);
        }
    }

    private boolean gidHashBuilt=false;
    private final Map<Integer,Map<Integer,LinuxServerGroup>> gidHash=new HashMap<Integer,Map<Integer,LinuxServerGroup>>();

    public LinuxServerGroup getLinuxServerGroup(AOServer aoServer, int gid) throws IOException, SQLException {
        synchronized(gidHash) {
            if(!gidHashBuilt) {
                gidHash.clear();

                List<LinuxServerGroup> list=getRows();
                int len=list.size();
                for(int c=0; c<len; c++) {
                    LinuxServerGroup lsg=list.get(c);
                    Integer serverI=Integer.valueOf(lsg.ao_server);
                    Map<Integer,LinuxServerGroup> serverHash=gidHash.get(serverI);
                    if(serverHash==null) gidHash.put(serverI, serverHash=new HashMap<Integer,LinuxServerGroup>());
                    Integer gidI=Integer.valueOf(lsg.getGid().getID());
                    if(serverHash.put(gidI, lsg)!=null) throw new SQLException("GID exists more than once on server: "+gidI.intValue()+" on "+serverI.intValue());
                }
                gidHashBuilt=true;
            }
            Map<Integer,LinuxServerGroup> serverHash=gidHash.get(Integer.valueOf(aoServer.pkey));
            if(serverHash==null) return null;
            return serverHash.get(Integer.valueOf(gid));
        }
    }

    List<LinuxServerGroup> getLinuxServerGroups(AOServer aoServer) throws IOException, SQLException {
        return getIndexedRows(LinuxServerGroup.COLUMN_AO_SERVER, aoServer.pkey);
    }

    List<LinuxServerGroup> getLinuxServerGroups(LinuxGroup lg) throws IOException, SQLException {
        return getIndexedRows(LinuxServerGroup.COLUMN_NAME, lg.pkey);
    }

    /**
     * Gets the primary <code>LinuxServerGroup</code> for this <code>LinuxServerAccount</code>
     *
     * @exception  SQLException  if the primary group is not found
     *                           or two or more groups are marked as primary
     *                           or the primary group does not exist on the same server
     */
    LinuxServerGroup getPrimaryLinuxServerGroup(LinuxServerAccount account) throws SQLException, IOException {
        if(account==null) throw new IllegalArgumentException("account=null");

        // Find the primary group for the account
        LinuxAccount linuxAccount=account.getLinuxAccount();
        LinuxGroup linuxGroup=connector.getLinuxGroupAccounts().getPrimaryGroup(linuxAccount);
        if(linuxGroup==null) throw new SQLException("Unable to find primary LinuxGroup for username="+linuxAccount.pkey);
        LinuxServerGroup lsg=getLinuxServerGroup(account.getAOServer(), linuxGroup.pkey);
        if(lsg==null) throw new SQLException("Unable to find LinuxServerGroup: "+linuxGroup.pkey+" on "+account.ao_server);
        return lsg;
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.LINUX_SERVER_GROUPS;
    }

    @Override
    boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
        String command=args[0];
        if(command.equalsIgnoreCase(AOSHCommand.ADD_LINUX_SERVER_GROUP)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_LINUX_SERVER_GROUP, args, 2, err)) {
                int pkey=connector.getSimpleAOClient().addLinuxServerGroup(
                    args[1],
                    args[2]
                );
                out.println(pkey);
                out.flush();
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_LINUX_SERVER_GROUP)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_LINUX_SERVER_GROUP, args, 2, err)) {
                connector.getSimpleAOClient().removeLinuxServerGroup(
                    args[1],
                    args[2]
                );
            }
            return true;
        }
        return false;
    }
}