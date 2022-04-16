/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.CannotRemoveReason;
import com.aoindustries.aoserv.client.Removable;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Any number of <code>EmailAddress</code>es may be directed to
 * an <code>EmailPipe</code>.  An <code>EmailPipeAddress</code>
 * makes this connection.
 *
 * @see  Pipe
 * @see  Address
 *
 * @author  AO Industries, Inc.
 */
public final class PipeAddress extends CachedObjectIntegerKey<PipeAddress> implements Removable {

	static final int
		COLUMN_PKEY=0,
		COLUMN_EMAIL_ADDRESS=1
	;
	static final String COLUMN_EMAIL_ADDRESS_name = "email_address";
	static final String COLUMN_EMAIL_PIPE_name = "email_pipe";

	private int email_address;
	private int email_pipe;

	/**
	 * @deprecated  Only required for implementation, do not use directly.
	 *
	 * @see  #init(java.sql.ResultSet)
	 * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
	 */
	@Deprecated/* Java 9: (forRemoval = true) */
	public PipeAddress() {
		// Do nothing
	}

	@Override
	protected Object getColumnImpl(int i) {
		if(i==COLUMN_PKEY) return pkey;
		if(i==COLUMN_EMAIL_ADDRESS) return email_address;
		if(i==2) return email_pipe;
		throw new IllegalArgumentException("Invalid index: " + i);
	}

	public int getEmailAddress_id() {
		return email_address;
	}

	public Address getEmailAddress() throws SQLException, IOException {
		Address emailAddressObject = table.getConnector().getEmail().getAddress().get(email_address);
		if (emailAddressObject == null) throw new SQLException("Unable to find EmailAddress: " + email_address);
		return emailAddressObject;
	}

	public int getEmailPipe_id() {
		return email_pipe;
	}

	public Pipe getEmailPipe() throws SQLException, IOException {
		Pipe emailPipeObject = table.getConnector().getEmail().getPipe().get(email_pipe);
		if (emailPipeObject == null) throw new SQLException("Unable to find EmailPipe: " + email_pipe);
		return emailPipeObject;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.EMAIL_PIPE_ADDRESSES;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey=result.getInt(1);
		email_address=result.getInt(2);
		email_pipe=result.getInt(3);
	}

	@Override
	public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
		pkey=in.readCompressedInt();
		email_address=in.readCompressedInt();
		email_pipe=in.readCompressedInt();
	}

	@Override
	public List<CannotRemoveReason<?>> getCannotRemoveReasons() throws SQLException, IOException {
		List<CannotRemoveReason<?>> reasons = new ArrayList<>();

		// Cannot be used as any part of a majordomo list
		for(MajordomoList ml : table.getConnector().getEmail().getMajordomoList().getRows()) {
			if(
				ml.getListPipeAddress().getPkey()==pkey
				|| ml.getListRequestPipeAddress().getPkey()==pkey
			) {
				Domain ed=ml.getMajordomoServer().getDomain();
				reasons.add(new CannotRemoveReason<>("Used by Majordomo list "+ml.getName()+'@'+ed.getDomain()+" on "+ed.getLinuxServer().getHostname(), ml));
			}
		}

		// Cannot be used as any part of a majordomo server
		for(MajordomoServer ms : table.getConnector().getEmail().getMajordomoServer().getRows()) {
			if(ms.getMajordomoPipeAddress().getPkey()==pkey) {
				Domain ed = ms.getDomain();
				reasons.add(new CannotRemoveReason<>("Used by Majordomo server "+ed.getDomain()+" on "+ed.getLinuxServer().getHostname(), ms));
			}
		}

		return reasons;
	}

	@Override
	public void remove() throws IOException, SQLException {
		table.getConnector().requestUpdateIL(
			true,
			AoservProtocol.CommandID.REMOVE,
			Table.TableID.EMAIL_PIPE_ADDRESSES,
			pkey
		);
	}

	@Override
	public String toStringImpl() throws SQLException, IOException {
		return getEmailAddress().toStringImpl()+"->"+getEmailPipe().getCommand();
	}

	@Override
	public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(email_address);
		out.writeCompressedInt(email_pipe);
	}
}
