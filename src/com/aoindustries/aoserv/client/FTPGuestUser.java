package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
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
    static final String COLUMN_USERNAME_name = "username";

    Object getColumnImpl(int i) {
        if(i==COLUMN_USERNAME) return pkey;
        throw new IllegalArgumentException("Invalid index: "+i);
    }

    public LinuxAccount getLinuxAccount() throws SQLException, IOException {
	LinuxAccount obj = table.connector.getLinuxAccounts().get(pkey);
	if (obj == null) throw new SQLException("Unable to find LinuxAccount: " + pkey);
	return obj;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.FTP_GUEST_USERS;
    }

    public void init(ResultSet result) throws SQLException {
	pkey = result.getString(1);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readUTF().intern();
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        return Collections.emptyList();
    }

    public void remove() throws IOException, SQLException {
    	table.connector.requestUpdateIL(
            true,
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.FTP_GUEST_USERS,
            pkey
    	);
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
	out.writeUTF(pkey);
    }
}