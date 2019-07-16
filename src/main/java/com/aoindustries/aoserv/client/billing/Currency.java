/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2019  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.billing;

import com.aoindustries.aoserv.client.GlobalObjectStringKey;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.util.i18n.ThreadLocale;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A {@link Currency} represents one type supported {@link java.util.Currency}.
 *
 * @author  AO Industries, Inc.
 */
public final class Currency extends GlobalObjectStringKey<Currency> {

	static final int COLUMN_currencyCode = 0;
	static final String COLUMN_currencyCode_name = "currencyCode";

	/** Looked-up when pkey is set, effectively final so no synchronization */
	private java.util.Currency currency;

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_currencyCode: return pkey;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	public String getCurrencyCode() {
		return pkey;
	}

	public java.util.Currency getCurrency() {
		java.util.Currency c = currency;
		if(c == null) throw new IllegalStateException("currency not set");
		return c;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.Currency;
	}

	@Override
	public void init(ResultSet results) throws SQLException {
		pkey = results.getString(1);
		currency = java.util.Currency.getInstance(pkey);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey = in.readUTF().intern();
		currency = java.util.Currency.getInstance(pkey);
	}

	@Override
	public String toStringImpl() {
		return getCurrency().getDisplayName(ThreadLocale.get());
	}

	@Override
	public void write(CompressedDataOutputStream out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeUTF(pkey);
	}
}
