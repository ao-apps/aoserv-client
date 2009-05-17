package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
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
    static final String COLUMN_GROUP_NAME_name = "group_name";
    static final String COLUMN_USERNAME_name = "username";

    /**
     * The maximum number of groups allowed for one account.
     */
    public static final int MAX_GROUPS=31;

    String group_name;
    String username;
    boolean is_primary;

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case 1: return group_name;
            case 2: return username;
            case 3: return is_primary?Boolean.TRUE:Boolean.FALSE;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public LinuxAccount getLinuxAccount() throws SQLException, IOException {
        LinuxAccount usernameObject = table.connector.getUsernames().get(username).getLinuxAccount();
        if (usernameObject == null) throw new SQLException("Unable to find LinuxAccount: " + username);
        return usernameObject;
    }

    public LinuxGroup getLinuxGroup() throws SQLException {
        LinuxGroup groupNameObject = table.connector.getLinuxGroups().get(group_name);
        if (groupNameObject == null) throw new SQLException("Unable to find LinuxGroup: " + group_name);
        return groupNameObject;
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.LINUX_GROUP_ACCOUNTS;
    }

    public void init(ResultSet result) throws SQLException {
        pkey = result.getInt(1);
        group_name = result.getString(2);
        username = result.getString(3);
        is_primary = result.getBoolean(4);
    }

    public boolean isPrimary() {
        return is_primary;
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        group_name=in.readUTF().intern();
        username=in.readUTF().intern();
        is_primary=in.readBoolean();
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();
        if(is_primary) reasons.add(new CannotRemoveReason<LinuxGroupAccount>("Not allowed to drop a primary group", this));
        return reasons;
    }

    public void remove() throws IOException, SQLException {
        table.connector.requestUpdateIL(
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.LINUX_GROUP_ACCOUNTS,
            pkey
        );
    }

    void setAsPrimary() throws IOException, SQLException {
        table.connector.requestUpdateIL(
            AOServProtocol.CommandID.SET_PRIMARY_LINUX_GROUP_ACCOUNT,
            pkey
        );
    }

    @Override
    String toStringImpl() {
        return group_name+'|'+username+(is_primary?"|p":"|a");
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeUTF(group_name);
        out.writeUTF(username);
        out.writeBoolean(is_primary);
    }
}
