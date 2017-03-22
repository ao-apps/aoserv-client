/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2000-2013, 2016, 2017  AO Industries, Inc.
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
package com.aoindustries.aoserv.client;

import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * Any email sent to a <code>BlackholeEmailAddress</code> is piped
 * directly to <code>/dev/null</code> - the bit bucket - the email
 * appears to have been delivered but is simply discarded.
 *
 * @see  EmailAddress#getBlackholeEmailAddress
 *
 * @author  AO Industries, Inc.
 */
final public class BlackholeEmailAddress extends CachedObjectIntegerKey<BlackholeEmailAddress> implements Removable {

	static final int COLUMN_EMAIL_ADDRESS=0;
	static final String COLUMN_EMAIL_ADDRESS_name = "email_address";

	@Override
	Object getColumnImpl(int i) {
		if(i==COLUMN_EMAIL_ADDRESS) return pkey;
		throw new IllegalArgumentException("Invalid index: "+i);
	}

	public EmailAddress getEmailAddress() throws SQLException, IOException {
		EmailAddress emailAddressObject = table.connector.getEmailAddresses().get(pkey);
		if (emailAddressObject == null) throw new SQLException("Unable to find EmailAddress: " + pkey);
		return emailAddressObject;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.BLACKHOLE_EMAIL_ADDRESSES;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey = result.getInt(1);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readCompressedInt();
	}

	@Override
	public List<CannotRemoveReason<?>> getCannotRemoveReasons() {
		return Collections.emptyList();
	}

	@Override
	public void remove() throws IOException, SQLException {
		table.connector.requestUpdateIL(
			true,
			AOServProtocol.CommandID.REMOVE,
			SchemaTable.TableID.BLACKHOLE_EMAIL_ADDRESSES,
			pkey
		);
	}

	@Override
	String toStringImpl() throws SQLException, IOException {
		return getEmailAddress().toStringImpl();
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeCompressedInt(pkey);
	}
}
