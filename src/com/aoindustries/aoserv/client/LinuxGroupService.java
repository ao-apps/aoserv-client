/*
 * Copyright 2001-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * @see  LinuxGroup
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.linux_groups)
public interface LinuxGroupService extends AOServService<Integer,LinuxGroup> {

    /* TODO
    void addLinuxGroup(String name, Business business, String type) throws IOException, SQLException {
        connector.requestUpdateIL(
            true,
            AOServProtocol.CommandID.ADD,
            SchemaTable.TableID.LINUX_GROUPS,
            name,
            business.pkey,
            type
        );
    }

    List<LinuxGroup> getLinuxGroups(Business business) throws IOException, SQLException {
        return getIndexedRows(LinuxGroup.COLUMN_ACCOUNTING, business.pkey);
    }

    @Override
    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
        String command=args[0];
        if(command.equalsIgnoreCase(AOSHCommand.ADD_LINUX_GROUP)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_LINUX_GROUP, args, 3, err)) {
                connector.getSimpleAOClient().addLinuxGroup(
                    args[1],
                    args[2],
                    args[3]
                );
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.CHECK_LINUX_GROUP_NAME)) {
            if(AOSH.checkParamCount(AOSHCommand.CHECK_LINUX_GROUP_NAME, args, 1, err)) {
                try {
                    SimpleAOClient.checkLinuxGroupname(args[1]);
                    out.println("true");
                } catch(IllegalArgumentException iae) {
                    out.print("aosh: "+AOSHCommand.CHECK_LINUX_GROUP_NAME+": ");
                    out.println(iae.getMessage());
                }
                out.flush();
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.IS_LINUX_GROUP_NAME_AVAILABLE)) {
            if(AOSH.checkParamCount(AOSHCommand.IS_LINUX_GROUP_NAME_AVAILABLE, args, 1, err)) {
                try {
                    out.println(connector.getSimpleAOClient().isLinuxGroupNameAvailable(args[1]));
                    out.flush();
                } catch(IllegalArgumentException iae) {
                    err.print("aosh: "+AOSHCommand.IS_LINUX_GROUP_NAME_AVAILABLE+": ");
                    err.println(iae.getMessage());
                    err.flush();
                }
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_LINUX_GROUP)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_LINUX_GROUP, args, 1, err)) {
                connector.getSimpleAOClient().removeLinuxGroup(
                    args[1]
                );
            }
            return true;
        }
        return false;
    }

    public boolean isLinuxGroupNameAvailable(String groupname) throws SQLException, IOException {
        if(!LinuxGroup.isValidGroupname(groupname)) throw new SQLException("Invalid groupname: "+groupname);
        return connector.requestBooleanQuery(true, AOServProtocol.CommandID.IS_LINUX_GROUP_NAME_AVAILABLE, groupname);
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

    LinuxServerGroup getLinuxServerGroup(AOServer aoServer, Business business) throws IOException, SQLException {
        String accounting=business.pkey;
        int aoPKey=aoServer.pkey;

        List<LinuxServerGroup> list = getRows();
        int len = list.size();
        for (int c = 0; c < len; c++) {
            // Must be for the correct server
            LinuxServerGroup group = list.get(c);
            if (aoPKey==group.ao_server) {
                // Must be for the correct business
                LinuxGroup linuxGroup = group.getLinuxGroup();
                if(linuxGroup.accounting.equals(accounting)) {
                    // Must be a user group
                    if (linuxGroup.getLinuxGroupType().pkey.equals(LinuxGroupType.USER)) return group;
                }
            }
        }
        return null;
    }
    */
    /**
     * Gets the primary <code>LinuxServerGroup</code> for this <code>LinuxServerAccount</code>
     *
     * @exception  SQLException  if the primary group is not found
     *                           or two or more groups are marked as primary
     *                           or the primary group does not exist on the same server
     */
    /* TODO
    LinuxServerGroup getPrimaryLinuxServerGroup(LinuxServerAccount account) throws SQLException, IOException {
        if(account==null) throw new IllegalArgumentException("account=null");

        // Find the primary group for the account
        LinuxAccount linuxAccount=account.getLinuxAccount();
        LinuxGroup linuxGroup=connector.getLinuxGroupAccounts().getPrimaryGroup(linuxAccount);
        if(linuxGroup==null) throw new NoSuchElementException("Unable to find primary LinuxGroup for username="+linuxAccount.pkey);
        LinuxServerGroup lsg=getLinuxServerGroup(account.getAOServer(), linuxGroup.pkey);
        if(lsg==null) throw new NoSuchElementException("Unable to find LinuxServerGroup: "+linuxGroup.pkey+" on "+account.ao_server);
        return lsg;
    }

    @Override
    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
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
     */
}