/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018, 2019, 2021  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.billing;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoindustries.aoserv.client.GlobalObjectStringKey;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Each reason for notifying clients is represented by a
 * <code>NoticeType</code>.
 *
 * @author  AO Industries, Inc.
 */
public final class NoticeType extends GlobalObjectStringKey<NoticeType> {

	static final int COLUMN_TYPE=0;
	static final String COLUMN_TYPE_name = "type";

	private String description;

	public static final String
		NONPAY="nonpay",
		BADCARD="badcard",
		DISABLE_WARNING="disable_warning",
		DISABLED="disabled",
		ENABLED="enabled"
	;

	@Override
	protected Object getColumnImpl(int i) {
		if(i==COLUMN_TYPE) return pkey;
		if(i==1) return description;
		throw new IllegalArgumentException("Invalid index: " + i);
	}

	public String getDescription() {
		return description;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.NOTICE_TYPES;
	}

	public String getType() {
		return pkey;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey = result.getString(1);
		description = result.getString(2);
	}

	@Override
	public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
		pkey=in.readUTF().intern();
		description=in.readUTF();
	}

	@Override
	public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeUTF(pkey);
		out.writeUTF(description);
	}
}
