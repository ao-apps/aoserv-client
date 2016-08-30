/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2003-2013, 2016  AO Industries, Inc.
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

import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Every <code>SpamEmailMessage</code> that causes an IP address
 * to be blocked via a <code>EmailSmtpRelay</code> is logged in this
 * table.
 *
 * @see  EmailSmtpRelay
 *
 * @author  AO Industries, Inc.
 */
final public class SpamEmailMessage extends AOServObject<Integer,SpamEmailMessage> implements SingleTableObject<Integer,SpamEmailMessage> {

	static final int
		COLUMN_PKEY=0,
		COLUMN_EMAIL_RELAY=1
	;
	static final String COLUMN_PKEY_name = "pkey";

	AOServTable<Integer,SpamEmailMessage> table;

	private int pkey;
	private int email_relay;
	private long time;
	private String message;

	@Override
	boolean equalsImpl(Object O) {
		return
			O instanceof SpamEmailMessage
			&& ((SpamEmailMessage)O).pkey==pkey
		;
	}

	public int getPkey() {
		return pkey;
	}

	public EmailSmtpRelay getEmailSmtpRelay() throws SQLException, IOException {
		EmailSmtpRelay er=table.connector.getEmailSmtpRelays().get(email_relay);
		if(er==null) throw new SQLException("Unable to find EmailSmtpRelay: "+email_relay);
		return er;
	}

	public Timestamp getTime() {
		return new Timestamp(time);
	}

	public String getMessage() {
		return message;
	}

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case 1: return email_relay;
			case 2: return getTime();
			case 3: return message;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	@Override
	public Integer getKey() {
		return pkey;
	}

	@Override
	public AOServTable<Integer,SpamEmailMessage> getTable() {
		return table;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.SPAM_EMAIL_MESSAGES;
	}

	@Override
	int hashCodeImpl() {
		return pkey;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey=result.getInt(1);
		email_relay=result.getInt(2);
		time=result.getTimestamp(3).getTime();
		message=result.getString(4);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readCompressedInt();
		email_relay=in.readCompressedInt();
		time=in.readLong();
		message=in.readUTF();
	}

	@Override
	public void setTable(AOServTable<Integer,SpamEmailMessage> table) {
		if(this.table!=null) throw new IllegalStateException("table already set");
		this.table=table;
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(email_relay);
		out.writeLong(time);
		out.writeUTF(message);
	}
}
