/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2013, 2016, 2017, 2018, 2019  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.accounting;

import com.aoindustries.aoserv.client.CachedObjectStringKey;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * For AO Industries use only.
 *
 * @author  AO Industries, Inc.
 */
final public class BankTransactionType extends CachedObjectStringKey<BankTransactionType> {

	static final int COLUMN_NAME=0;
	static final String COLUMN_DISPLAY_name = "display";

	private String
		display,
		description
	;

	private boolean isNegative;

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_NAME: return pkey;
			case 1: return display;
			case 2: return description;
			case 3: return isNegative;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	public String getDescription() {
		return description;
	}

	public String getDisplay() {
		return display;
	}

	public String getName() {
		return pkey;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.BANK_TRANSACTION_TYPES;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey = result.getString(1);
		display = result.getString(2);
		description = result.getString(3);
		isNegative = result.getBoolean(4);
	}

	public boolean isNegative() {
		return isNegative;
	}

	@Override
	public void read(CompressedDataInputStream in, AoservProtocol.Version protocolVersion) throws IOException {
		pkey=in.readUTF().intern();
		display=in.readUTF();
		description=in.readUTF();
		isNegative=in.readBoolean();
	}

	@Override
	public String toStringImpl() {
		return display;
	}

	@Override
	public void write(CompressedDataOutputStream out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeUTF(pkey);
		out.writeUTF(display);
		out.writeUTF(description);
		out.writeBoolean(isNegative);
	}
}
