/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2004-2009, 2016, 2017, 2018, 2019, 2020, 2021  AO Industries, Inc.
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

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.CannotRemoveReason;
import com.aoindustries.aoserv.client.Removable;
import com.aoindustries.aoserv.client.linux.UserServer;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * An <code>EmailAttachmentBlock</code> restricts one attachment type on one email inbox.
 *
 * @see  AttachmentType
 * @see  UserServer
 *
 * @author  AO Industries, Inc.
 */
public final class AttachmentBlock extends CachedObjectIntegerKey<AttachmentBlock> implements Removable {

	static final int
		COLUMN_PKEY=0,
		COLUMN_LINUX_SERVER_ACCOUNT=1
	;
	static final String COLUMN_LINUX_SERVER_ACCOUNT_name = "linux_server_account";
	static final String COLUMN_EXTENSION_name = "extension";

	private int linux_server_account;
	private String extension;

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_LINUX_SERVER_ACCOUNT: return linux_server_account;
			case 2: return extension;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	public UserServer getLinuxServerAccount() throws SQLException, IOException {
		UserServer lsa=table.getConnector().getLinux().getUserServer().get(linux_server_account);
		if(lsa==null) throw new SQLException("Unable to find LinuxServerAccount: " + linux_server_account);
		return lsa;
	}

	public AttachmentType getEmailAttachmentType() throws SQLException, IOException {
		AttachmentType eat=table.getConnector().getEmail().getAttachmentType().get(extension);
		if(eat==null) throw new SQLException("Unable to find EmailAttachmentType: " + extension);
		return eat;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.EMAIL_ATTACHMENT_BLOCKS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey=result.getInt(1);
		linux_server_account=result.getInt(2);
		extension=result.getString(3);
	}

	@Override
	public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
		pkey=in.readCompressedInt();
		linux_server_account=in.readCompressedInt();
		extension=in.readUTF().intern();
	}

	@Override
	public List<CannotRemoveReason<?>> getCannotRemoveReasons() {
		return Collections.emptyList();
	}

	@Override
	public void remove() throws SQLException, IOException {
		table.getConnector().requestUpdateIL(
			true,
			AoservProtocol.CommandID.REMOVE,
			Table.TableID.EMAIL_ATTACHMENT_BLOCKS,
			pkey
		);
	}

	@Override
	public String toStringImpl() throws SQLException, IOException {
		return getLinuxServerAccount().toStringImpl()+"->"+extension;
	}

	@Override
	public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(linux_server_account);
		out.writeUTF(extension);
	}
}
