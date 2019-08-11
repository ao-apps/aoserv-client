/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2005-2013, 2016, 2017, 2018, 2019  AO Industries, Inc.
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
import static com.aoindustries.aoserv.client.billing.ApplicationResources.accessor;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.io.stream.StreamableInput;
import com.aoindustries.io.stream.StreamableOutput;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A <code>TransactionType</code> is one type that may be used
 * in a <code>Transaction</code>.  Each <code>PackageDefinition</code>
 * and <code>PackageDefinitionLimit</code> defines which type will be
 * used for billing.
 *
 * @see  PackageDefinition
 * @see  PackageDefinitionLimit
 * @see  Transaction
 *
 * @author  AO Industries, Inc.
 */
public final class TransactionType extends GlobalObjectStringKey<TransactionType> {

	static final int COLUMN_NAME=0;
	static final String COLUMN_NAME_name = "name";

	public static final String
		HTTPD="httpd",
		PAYMENT="payment",
		VIRTUAL="virtual"
	;

	/**
	 * If <code>true</code> this <code>TransactionType</code> represents a credit to
	 * an account and will be listed in payments received reports.
	 */
	private boolean isCredit;

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_NAME: return pkey;
			case 1: return isCredit;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	public String getDescription() {
		return accessor.getMessage("TransactionType."+pkey+".description");
	}

	/**
	 * Gets the unique name of this transaction type.
	 */
	public String getName() {
		return pkey;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.TRANSACTION_TYPES;
	}

	public String getUnit() {
		return accessor.getMessage("TransactionType."+pkey+".unit");
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey = result.getString(1);
		isCredit = result.getBoolean(2);
	}

	public boolean isCredit() {
		return isCredit;
	}

	@Override
	public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
		pkey = in.readUTF().intern();
		isCredit = in.readBoolean();
	}

	@Override
	public String toStringImpl() {
		return accessor.getMessage("TransactionType."+pkey+".toString");
	}

	@Override
	public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeUTF(pkey);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_60)<=0) {
			out.writeUTF(toStringImpl()); // display
			out.writeUTF(getDescription()); // description
			out.writeUTF(getUnit()); // unit
		}
		out.writeBoolean(isCredit);
	}
}
