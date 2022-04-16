/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2013, 2016, 2017, 2018, 2019, 2021, 2022  AO Industries, Inc.
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
 * along with aoserv-client.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.aoindustries.aoserv.client.email;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.net.Email;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.CannotRemoveReason;
import com.aoindustries.aoserv.client.Removable;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
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
 * @see  Address
 *
 * @author  AO Industries, Inc.
 */
public final class Forwarding extends CachedObjectIntegerKey<Forwarding> implements Removable {

	static final int
		COLUMN_PKEY=0,
		COLUMN_EMAIL_ADDRESS=1
	;
	static final String COLUMN_EMAIL_ADDRESS_name = "email_address";
	static final String COLUMN_DESTINATION_name = "destination";

	private int email_address;
	private Email destination;

	/**
	 * @deprecated  Only required for implementation, do not use directly.
	 *
	 * @see  #init(java.sql.ResultSet)
	 * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
	 */
	@Deprecated/* Java 9: (forRemoval = true) */
	public Forwarding() {
		// Do nothing
	}

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_EMAIL_ADDRESS: return email_address;
			case 2: return destination;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	/**
	 * Gets the <code>destination</code>
	 */
	public Email getDestination() {
		return destination;
	}

	/**
	 * Gets the <code>email_address</code>
	 */
	public Address getEmailAddress() throws SQLException, IOException {
		Address emailAddressObject = table.getConnector().getEmail().getAddress().get(email_address);
		if (emailAddressObject == null) throw new SQLException("Unable to find EmailAddress: " + email_address);
		return emailAddressObject;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.EMAIL_FORWARDING;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			pkey=result.getInt(1);
			email_address=result.getInt(2);
			destination=Email.valueOf(result.getString(3));
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
		try {
			pkey=in.readCompressedInt();
			email_address=in.readCompressedInt();
			destination=Email.valueOf(in.readUTF());
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public List<CannotRemoveReason<?>> getCannotRemoveReasons() {
		return Collections.emptyList();
	}

	@Override
	public void remove() throws IOException, SQLException {
		table.getConnector().requestUpdateIL(
			true,
			AoservProtocol.CommandID.REMOVE,
			Table.TableID.EMAIL_FORWARDING,
			pkey
		);
	}

	@Override
	public String toStringImpl() throws SQLException, IOException {
		return getEmailAddress().toStringImpl()+" -> "+destination;
	}

	@Override
	public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(email_address);
		out.writeUTF(destination.toString());
	}
}
