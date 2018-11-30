/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2013, 2016, 2017, 2018  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aoserv-client.
 *
 * aoserv-client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aoserv-client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aoserv-client.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.aoserv.client.email;

import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.CannotRemoveReason;
import com.aoindustries.aoserv.client.Removable;
import com.aoindustries.aoserv.client.linux.LinuxServerAccount;
import com.aoindustries.aoserv.client.schema.AOServProtocol;
import com.aoindustries.aoserv.client.schema.SchemaTable;
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

	@Override
	protected Object getColumnImpl(int i) {
		if(i==COLUMN_PKEY) return pkey;
		if(i==COLUMN_EMAIL_ADDRESS) return email_address;
		if(i==COLUMN_LINUX_SERVER_ACCOUNT) return linux_server_account;
		throw new IllegalArgumentException("Invalid index: "+i);
	}

	public EmailAddress getEmailAddress() throws SQLException, IOException {
		EmailAddress emailAddressObject = table.getConnector().getEmailAddresses().get(email_address);
		if (emailAddressObject == null) throw new SQLException("Unable to find EmailAddress: " + email_address);
		return emailAddressObject;
	}

	public LinuxServerAccount getLinuxServerAccount() throws SQLException, IOException {
		LinuxServerAccount lsa = table.getConnector().getLinuxServerAccounts().get(linux_server_account);
		if(lsa == null) throw new SQLException("Unable to find LinuxServerAccount: " + linux_server_account);
		return lsa;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.LINUX_ACC_ADDRESSES;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey=result.getInt(1);
		email_address=result.getInt(2);
		linux_server_account=result.getInt(3);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readCompressedInt();
		email_address=in.readCompressedInt();
		linux_server_account=in.readCompressedInt();
	}

	@Override
	public List<CannotRemoveReason<?>> getCannotRemoveReasons() {
		return Collections.emptyList();
	}

	@Override
	public void remove() throws IOException, SQLException {
		table.getConnector().requestUpdateIL(
			true,
			AOServProtocol.CommandID.REMOVE,
			SchemaTable.TableID.LINUX_ACC_ADDRESSES,
			pkey
		);
	}

	@Override
	public String toStringImpl() throws SQLException, IOException {
		return getEmailAddress().toStringImpl()+"->"+getLinuxServerAccount().toStringImpl();
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(email_address);
		if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_30)<=0) {
			out.writeUTF("TODO: Convert somehow"); // linux_account
		} else {
			out.writeCompressedInt(linux_server_account);
		}
	}
}
