package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.sql.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.Collections;
import java.util.List;

/**
 * A <code>LinuxAccount</code> may have any number of email
 * addresses routed to it to become an email inbox.
 * <code>LinuxAccAddress</code>es make this connection.  Once
 * email arrives in the inbox, it may be retrieved via one of
 * several mail protocols, including the common POP3 and IMAP.
 *
 * @see  LinuxAccount
 * @see  EmailAddress
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class LinuxAccAddress extends CachedObjectIntegerKey<LinuxAccAddress> implements Removable {

    static final int
        COLUMN_PKEY=0,
        COLUMN_EMAIL_ADDRESS=1
    ;

    int email_address;
    String linux_account;

    public Object getColumn(int i) {
        if(i==COLUMN_PKEY) return Integer.valueOf(pkey);
	if(i==COLUMN_EMAIL_ADDRESS) return Integer.valueOf(email_address);
	if(i==2) return linux_account;
	throw new IllegalArgumentException("Invalid index: "+i);
    }

    public EmailAddress getEmailAddress() {
	EmailAddress emailAddressObject = table.connector.emailAddresses.get(email_address);
	if (emailAddressObject == null) throw new WrappedException(new SQLException("Unable to find EmailAddress: " + email_address));
	return emailAddressObject;
    }

    public LinuxAccount getLinuxAccount() {
	Username username=table.connector.usernames.get(linux_account);
        // Username might have been filtered
        if(username==null) return null;
        // But if the username is available, so should be the LinuxAccount
	LinuxAccount linuxAccountObject = username.getLinuxAccount();
	if (linuxAccountObject == null) throw new WrappedException(new SQLException("Unable to find LinuxAccount: " + linux_account));
	return linuxAccountObject;
    }

    protected int getTableIDImpl() {
	return SchemaTable.LINUX_ACC_ADDRESSES;
    }

    void initImpl(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
	email_address=result.getInt(2);
	linux_account=result.getString(3);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
	email_address=in.readCompressedInt();
	linux_account=in.readUTF();
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        return Collections.emptyList();
    }

    public void remove() {
	table.connector.requestUpdateIL(
            AOServProtocol.REMOVE,
            SchemaTable.LINUX_ACC_ADDRESSES,
            pkey
	);
    }

    String toStringImpl() {
        return getEmailAddress().toString()+"->"+linux_account;
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        out.writeCompressedInt(pkey);
	out.writeCompressedInt(email_address);
	out.writeUTF(linux_account);
    }
}