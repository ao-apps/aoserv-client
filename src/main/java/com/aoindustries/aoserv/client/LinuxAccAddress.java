/*
 * Copyright 2000-2013 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
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
 * @author  AO Industries, Inc.
 */
final public class LinuxAccAddress extends CachedObjectIntegerKey<LinuxAccAddress> implements Removable {

	static final int
		COLUMN_PKEY=0,
		COLUMN_EMAIL_ADDRESS=1,
		COLUMN_LINUX_SERVER_ACCOUNT=2
	;
	static final String COLUMN_EMAIL_ADDRESS_name = "email_address";
	static final String COLUMN_LINUX_SERVER_ACCOUNT_name = "linux_server_account";

	int email_address;
	int linux_server_account;

	Object getColumnImpl(int i) {
		if(i==COLUMN_PKEY) return Integer.valueOf(pkey);
		if(i==COLUMN_EMAIL_ADDRESS) return Integer.valueOf(email_address);
		if(i==COLUMN_LINUX_SERVER_ACCOUNT) return Integer.valueOf(linux_server_account);
		throw new IllegalArgumentException("Invalid index: "+i);
	}

	public EmailAddress getEmailAddress() throws SQLException, IOException {
		EmailAddress emailAddressObject = table.connector.getEmailAddresses().get(email_address);
		if (emailAddressObject == null) throw new SQLException("Unable to find EmailAddress: " + email_address);
		return emailAddressObject;
	}

	public LinuxServerAccount getLinuxServerAccount() throws SQLException, IOException {
		LinuxServerAccount lsa = table.connector.getLinuxServerAccounts().get(linux_server_account);
		if(lsa == null) throw new SQLException("Unable to find LinuxServerAccount: " + linux_server_account);
		return lsa;
	}

	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.LINUX_ACC_ADDRESSES;
	}

	public void init(ResultSet result) throws SQLException {
		pkey=result.getInt(1);
		email_address=result.getInt(2);
		linux_server_account=result.getInt(3);
	}

	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readCompressedInt();
		email_address=in.readCompressedInt();
		linux_server_account=in.readCompressedInt();
	}

	public List<CannotRemoveReason> getCannotRemoveReasons() {
		return Collections.emptyList();
	}

	public void remove() throws IOException, SQLException {
		table.connector.requestUpdateIL(
			true,
			AOServProtocol.CommandID.REMOVE,
			SchemaTable.TableID.LINUX_ACC_ADDRESSES,
			pkey
		);
	}

	@Override
	String toStringImpl() throws SQLException, IOException {
		return getEmailAddress().toStringImpl()+"->"+getLinuxServerAccount().toStringImpl();
	}

	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(email_address);
		if(version.compareTo(AOServProtocol.Version.VERSION_1_30)<=0) {
			out.writeUTF("TODO: Convert somehow"); // linux_account
		} else {
			out.writeCompressedInt(linux_server_account);
		}
	}
}
