/*
 * Copyright 2001-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * @see  LinuxGroupAccount
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.linux_account_groups)
public interface LinuxAccountGroupService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends AOServService<C,F,Integer,LinuxAccountGroup> {

    /* TODO
    private boolean hashBuilt=false;
    private final Map<String,LinuxAccountGroup> hash=new HashMap<String,LinuxAccountGroup>();
    */
    /**
     * The group name of the primary group is hashed on first use for fast
     * lookups.
     */
    /* TODO
    private boolean primaryHashBuilt=false;
    private final Map<String,LinuxAccountGroup> primaryHash=new HashMap<String,LinuxAccountGroup>();

    int addLinuxGroupAccount(
        LinuxGroup groupNameObject,
        LinuxAccount usernameObject
    ) throws IOException, SQLException {
        int pkey=connector.requestIntQueryIL(
            true,
            AOServProtocol.CommandID.ADD,
            SchemaTable.TableID.LINUX_GROUP_ACCOUNTS,
            groupNameObject.pkey,
            usernameObject.pkey
        );
        return pkey;
    }

    LinuxAccountGroup getLinuxGroupAccount(
        String groupName,
        String username
    ) throws IOException, SQLException {
        synchronized(hash) {
            if(!hashBuilt) {
                hash.clear();
                List<LinuxAccountGroup> list=getRows();
                int len=list.size();
                for(int c=0;c<len;c++) {
                    LinuxAccountGroup lga=list.get(c);
                    hash.put(lga.group_name+':'+lga.username, lga);
                }
                hashBuilt=true;
            }
            return hash.get(groupName+':'+username);
        }
    }

    List<LinuxGroup> getLinuxGroups(LinuxAccount linuxAccount) throws IOException, SQLException {
        String username = linuxAccount.pkey;
        List<LinuxAccountGroup> cached = getRows();
        int len = cached.size();
        List<LinuxGroup> matches=new ArrayList<LinuxGroup>(LinuxAccountGroup.MAX_GROUPS);
        for (int c = 0; c < len; c++) {
            LinuxAccountGroup lga = cached.get(c);
            if (lga.username.equals(username)) matches.add(lga.getLinuxGroup());
        }
        return matches;
    }

    LinuxGroup getPrimaryGroup(LinuxAccount account) throws IOException, SQLException {
        synchronized(primaryHash) {
            if(account==null) throw new IllegalArgumentException("param account is null");
            // Rebuild the hash if needed
            if(!primaryHashBuilt) {
                List<LinuxAccountGroup> cache=getRows();
                primaryHash.clear();
                int len=cache.size();
                for(int c=0;c<len;c++) {
                    LinuxAccountGroup lga=cache.get(c);
                    if(lga.isPrimary()) primaryHash.put(lga.username, lga);
                }
                primaryHashBuilt=true;
            }
            LinuxAccountGroup lga=primaryHash.get(account.pkey);
            // May be filtered
            if(lga==null) return null;
            return lga.getLinuxGroup();
        }
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
     */
}
