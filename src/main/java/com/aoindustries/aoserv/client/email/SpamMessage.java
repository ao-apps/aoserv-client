/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2003-2013, 2016, 2017, 2018, 2019  AO Industries, Inc.
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

import com.aoindustries.aoserv.client.AOServObject;
import com.aoindustries.aoserv.client.AOServTable;
import com.aoindustries.aoserv.client.SingleTableObject;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.sql.UnmodifiableTimestamp;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Every <code>SpamEmailMessage</code> that causes an IP address
 * to be blocked via a <code>EmailSmtpRelay</code> is logged in this
 * table.
 *
 * @see  SmtpRelay
 *
 * @author  AO Industries, Inc.
 */
final public class SpamMessage extends AOServObject<Integer,SpamMessage> implements SingleTableObject<Integer,SpamMessage> {

	static final int
		COLUMN_PKEY=0,
		COLUMN_EMAIL_RELAY=1
	;
	static final String COLUMN_PKEY_name = "pkey";

	AOServTable<Integer,SpamMessage> table;

	private int pkey;
	private int email_relay;
	private UnmodifiableTimestamp time;
	private String message;

	@Override
	public boolean equals(Object O) {
		return
			O instanceof SpamMessage
			&& ((SpamMessage)O).getPkey()==pkey
		;
	}

	public int getPkey() {
		return pkey;
	}

	public SmtpRelay getEmailSmtpRelay() throws SQLException, IOException {
		SmtpRelay er=table.getConnector().getEmail().getSmtpRelay().get(email_relay);
		if(er==null) throw new SQLException("Unable to find EmailSmtpRelay: "+email_relay);
		return er;
	}

	public UnmodifiableTimestamp getTime() {
		return time;
	}

	public String getMessage() {
		return message;
	}

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case 1: return email_relay;
			case 2: return time;
			case 3: return message;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	@Override
	public Integer getKey() {
		return pkey;
	}

	@Override
	public AOServTable<Integer,SpamMessage> getTable() {
		return table;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.SPAM_EMAIL_MESSAGES;
	}

	@Override
	public int hashCode() {
		return pkey;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey=result.getInt(1);
		email_relay=result.getInt(2);
		time = UnmodifiableTimestamp.valueOf(result.getTimestamp(3));
		message=result.getString(4);
	}

	@Override
	public void read(CompressedDataInputStream in, AoservProtocol.Version protocolVersion) throws IOException {
		pkey=in.readCompressedInt();
		email_relay=in.readCompressedInt();
		time = in.readUnmodifiableTimestamp();
		message=in.readUTF();
	}

	@Override
	public void setTable(AOServTable<Integer,SpamMessage> table) {
		if(this.table!=null) throw new IllegalStateException("table already set");
		this.table=table;
	}

	@Override
	public void write(CompressedDataOutputStream out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(email_relay);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
			out.writeLong(time.getTime());
		} else {
			out.writeTimestamp(time);
		}
		out.writeUTF(message);
	}
}
