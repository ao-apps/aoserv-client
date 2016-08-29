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

	Object getColumnImpl(int i) {
		if(i==COLUMN_EMAIL_ADDRESS) return Integer.valueOf(pkey);
		throw new IllegalArgumentException("Invalid index: "+i);
	}

	public EmailAddress getEmailAddress() throws SQLException, IOException {
		EmailAddress emailAddressObject = table.connector.getEmailAddresses().get(pkey);
		if (emailAddressObject == null) throw new SQLException("Unable to find EmailAddress: " + pkey);
		return emailAddressObject;
	}

	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.BLACKHOLE_EMAIL_ADDRESSES;
	}

	public void init(ResultSet result) throws SQLException {
		pkey = result.getInt(1);
	}

	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readCompressedInt();
	}

	public List<CannotRemoveReason> getCannotRemoveReasons() {
		return Collections.emptyList();
	}

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

	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeCompressedInt(pkey);
	}
}