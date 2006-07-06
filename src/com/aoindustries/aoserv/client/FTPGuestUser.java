package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.StringUtility;
import com.aoindustries.util.WrappedException;
import java.io.*;
import java.sql.*;
import java.util.Collections;
import java.util.List;

/**
 * If a <code>LinuxAccount</code> has a <code>FTPGuestUser</code> attached to it,
 * FTP connections will be limited with their home directory as the root
 * directory.
 *
 * @see  LinuxAccount
 * @see  LinuxServerAccount
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class FTPGuestUser extends CachedObjectStringKey<FTPGuestUser> implements Removable {

    static final int COLUMN_USERNAME=0;

    public Object getColumn(int i) {
        if(i==COLUMN_USERNAME) return pkey;
        throw new IllegalArgumentException("Invalid index: "+i);
    }

    public LinuxAccount getLinuxAccount() {
	LinuxAccount obj = table.connector.linuxAccounts.get(pkey);
	if (obj == null) throw new WrappedException(new SQLException("Unable to find LinuxAccount: " + pkey));
	return obj;
    }

    protected int getTableIDImpl() {
	return SchemaTable.FTP_GUEST_USERS;
    }

    void initImpl(ResultSet result) throws SQLException {
	pkey = result.getString(1);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readUTF();
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        return Collections.emptyList();
    }

    public void remove() {
	table.connector.requestUpdateIL(
            AOServProtocol.REMOVE,
            SchemaTable.FTP_GUEST_USERS,
            pkey
	);
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeUTF(pkey);
    }
}