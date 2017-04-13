/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2007-2013, 2016, 2017  AO Industries, Inc.
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

import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Logs the whois history for each account and domain combination.
 *
 * @author  AO Industries, Inc.
 */
final public class WhoisHistory extends CachedObjectIntegerKey<WhoisHistory> {

	static final int
		COLUMN_PKEY=0,
		COLUMN_ACCOUNTING=2,
		COLUMN_WHOIS_OUTPUT=4
	;
	static final String COLUMN_ACCOUNTING_name = "accounting";
	static final String COLUMN_ZONE_name = "zone";
	static final String COLUMN_TIME_name = "time";

	private long time;
	private AccountingCode accounting;
	private String zone;

	/**
	 * Note: this is loaded in a separate call to the master as needed to conserve heap space, and it is null to begin with.
	 */
	private String whois_output;

	@Override
	Object getColumnImpl(int i) throws IOException, SQLException {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case 1: return getTime();
			case COLUMN_ACCOUNTING: return accounting;
			case 3: return zone;
			case COLUMN_WHOIS_OUTPUT: return getWhoisOutput();
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	@Override
	public int getPkey() {
		return pkey;
	}

	public Timestamp getTime() {
		return new Timestamp(time);
	}

	public Business getBusiness() throws SQLException, IOException {
		Business business = table.connector.getBusinesses().get(accounting);
		if (business == null) throw new SQLException("Unable to find Business: " + accounting);
		return business;
	}

	/**
	 * Gets the top level domain that was queried in the whois system.
	 */
	public String getZone() {
		return zone;
	}

	/**
	 * Gets the whois output from the database.  The first access to this for a specific object instance
	 * will query the master server for the information and then cache the results.  This is done
	 * to conserve heap space while still yielding high performance through the caching of the rest of the fields.
	 *
	 * From an outside point of view, the object is still immutable and will yield constant return
	 * values per instance.
	 */
	public String getWhoisOutput() throws IOException, SQLException {
		if(whois_output==null) whois_output = table.connector.requestStringQuery(true, AOServProtocol.CommandID.GET_WHOIS_HISTORY_WHOIS_OUTPUT, pkey);
		return whois_output;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.WHOIS_HISTORY;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			pkey = result.getInt(1);
			time = result.getTimestamp(2).getTime();
			accounting = AccountingCode.valueOf(result.getString(3));
			zone = result.getString(4);
			// Note: this is loaded in a separate call to the master as needed to conserve heap space: whois_output = result.getString(5);
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			pkey = in.readCompressedInt();
			time = in.readLong();
			accounting = AccountingCode.valueOf(in.readUTF()).intern();
			zone = in.readUTF().intern();
			// Note: this is loaded in a separate call to the master as needed to conserve heap space: whois_output = in.readUTF();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	String toStringImpl() {
		return pkey+"|"+accounting+'|'+zone+'|'+getTime();
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeLong(time);
		out.writeUTF(accounting.toString());
		out.writeUTF(zone);
		// Note: this is loaded in a separate call to the master as needed to conserve heap space: out.writeUTF(whois_output);
	}
}
