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
 * An <code>EmailForwarding</code> directs incoming mail to a
 * different destination.  Any mail sent to the email address
 * is immediately sent on to the configured destination.
 *
 * @see  EmailAddress
 *
 * @author  AO Industries, Inc.
 */
final public class EmailForwarding extends CachedObjectIntegerKey<EmailForwarding> implements Removable {

	static final int
		COLUMN_PKEY=0,
		COLUMN_EMAIL_ADDRESS=1
	;
	static final String COLUMN_EMAIL_ADDRESS_name = "email_address";
	static final String COLUMN_DESTINATION_name = "destination";

	int email_address;
	String destination;

	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return Integer.valueOf(pkey);
			case COLUMN_EMAIL_ADDRESS: return Integer.valueOf(email_address);
			case 2: return destination;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	/**
	 * Gets the <code>destination</code>
	 */
	public String getDestination() {
		return destination;
	}

	/**
	 * Gets the <code>email_address</code>
	 */
	public EmailAddress getEmailAddress() throws SQLException, IOException {
		EmailAddress emailAddressObject = table.connector.getEmailAddresses().get(email_address);
		if (emailAddressObject == null) throw new SQLException("Unable to find EmailAddress: " + email_address);
		return emailAddressObject;
	}

	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.EMAIL_FORWARDING;
	}

	public void init(ResultSet result) throws SQLException {
		pkey=result.getInt(1);
		email_address=result.getInt(2);
		destination=result.getString(3);
	}

	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readCompressedInt();
		email_address=in.readCompressedInt();
		destination=in.readUTF();
	}

	public List<CannotRemoveReason> getCannotRemoveReasons() {
		return Collections.emptyList();
	}

	public void remove() throws IOException, SQLException {
		table.connector.requestUpdateIL(
			true,
			AOServProtocol.CommandID.REMOVE,
			SchemaTable.TableID.EMAIL_FORWARDING,
			pkey
		);
	}

	@Override
	String toStringImpl() throws SQLException, IOException {
		return getEmailAddress().toStringImpl()+" -> "+destination;
	}

	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(email_address);
		out.writeUTF(destination);
	}
}