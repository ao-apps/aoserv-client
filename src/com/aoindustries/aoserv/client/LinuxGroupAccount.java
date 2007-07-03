package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.profiler.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * Each <code>LinuxGroup</code> may be accessed by any number
 * of <code>LinuxAccount</code>s.  The accounts are granted access
 * to a group via a <code>LinuxGroupAccount</code>.  One account
 * may access a maximum of 31 different groups.  Also, a
 * <code>LinuxAccount</code> must have one and only one primary
 * <code>LinuxGroupAccount</code>.
 *
 * @see  LinuxAccount
 * @see  LinuxGroup
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class LinuxGroupAccount extends CachedObjectIntegerKey<LinuxGroupAccount> implements Removable {

    static final int COLUMN_PKEY=0;

    /**
     * The maximum number of groups allowed for one account.
     */
    public static final int MAX_GROUPS=31;

    String group_name;
    String username;
    boolean is_primary;

    public Object getColumn(int i) {
        Profiler.startProfile(Profiler.FAST, LinuxGroupAccount.class, "getColValueImpl(int)", null);
        try {
            switch(i) {
                case COLUMN_PKEY: return Integer.valueOf(pkey);
                case 1: return group_name;
                case 2: return username;
                case 3: return is_primary?Boolean.TRUE:Boolean.FALSE;
                default: throw new IllegalArgumentException("Invalid index: "+i);
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public LinuxAccount getLinuxAccount() {
        Profiler.startProfile(Profiler.FAST, LinuxGroupAccount.class, "getLinuxAccount()", null);
        try {
            LinuxAccount usernameObject = table.connector.usernames.get(username).getLinuxAccount();
            if (usernameObject == null) throw new WrappedException(new SQLException("Unable to find LinuxAccount: " + username));
            return usernameObject;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public LinuxGroup getLinuxGroup() {
        Profiler.startProfile(Profiler.FAST, LinuxGroupAccount.class, "getLinuxGroup()", null);
        try {
            LinuxGroup groupNameObject = table.connector.linuxGroups.get(group_name);
            if (groupNameObject == null) throw new WrappedException(new SQLException("Unable to find LinuxGroup: " + group_name));
            return groupNameObject;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.LINUX_GROUP_ACCOUNTS;
    }

    void initImpl(ResultSet result) throws SQLException {
        Profiler.startProfile(Profiler.FAST, LinuxGroupAccount.class, "initImpl(ResultSet)", null);
        try {
            pkey = result.getInt(1);
            group_name = result.getString(2);
            username = result.getString(3);
            is_primary = result.getBoolean(4);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public boolean isPrimary() {
        return is_primary;
    }

    public void read(CompressedDataInputStream in) throws IOException {
        Profiler.startProfile(Profiler.IO, LinuxGroupAccount.class, "read(CompressedDataInputStream)", null);
        try {
            pkey=in.readCompressedInt();
            group_name=in.readUTF().intern();
            username=in.readUTF().intern();
            is_primary=in.readBoolean();
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();
        if(is_primary) reasons.add(new CannotRemoveReason<LinuxGroupAccount>("Not allowed to drop a primary group", this));
        return reasons;
    }

    public void remove() {
        Profiler.startProfile(Profiler.UNKNOWN, LinuxGroupAccount.class, "remove()", null);
        try {
            table.connector.requestUpdateIL(
                AOServProtocol.CommandID.REMOVE,
                SchemaTable.TableID.LINUX_GROUP_ACCOUNTS,
                pkey
            );
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    void setAsPrimary() {
        Profiler.startProfile(Profiler.UNKNOWN, LinuxGroupAccount.class, "setAsPrimary()", null);
        try {
            table.connector.requestUpdateIL(
                AOServProtocol.CommandID.SET_PRIMARY_LINUX_GROUP_ACCOUNT,
                pkey
            );
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    String toStringImpl() {
        Profiler.startProfile(Profiler.FAST, LinuxGroupAccount.class, "toStringImpl()", null);
        try {
            return group_name+'|'+username+(is_primary?"|p":"|a");
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        Profiler.startProfile(Profiler.IO, LinuxGroupAccount.class, "write(CompressedDataOutputStream,String)", null);
        try {
            out.writeCompressedInt(pkey);
            out.writeUTF(group_name);
            out.writeUTF(username);
            out.writeBoolean(is_primary);
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }
}
