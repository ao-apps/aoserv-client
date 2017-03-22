/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2000-2013, 2016, 2017  AO Industries, Inc.
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
import java.util.ArrayList;
import java.util.List;

/**
 * An <code>EmailList</code> may receive email on multiple addresses, and
 * then forward those emails to the list of destinations.  An
 * <code>EmailListAddress</code> directs incoming emails to the email list.
 *
 * @see  EmailList
 * @see  EmailAddress
 *
 * @author  AO Industries, Inc.
 */
final public class EmailListAddress extends CachedObjectIntegerKey<EmailListAddress> implements Removable {

	static final int
		COLUMN_PKEY=0,
		COLUMN_EMAIL_ADDRESS=1,
		COLUMN_EMAIL_LIST=2
	;
	static final String COLUMN_EMAIL_ADDRESS_name = "email_address";
	static final String COLUMN_EMAIL_LIST_name = "email_list";

	int email_address;
	int email_list;

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_EMAIL_ADDRESS: return email_address;
			case COLUMN_EMAIL_LIST: return email_list;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public EmailAddress getEmailAddress() throws SQLException, IOException {
		EmailAddress emailAddressObject = table.connector.getEmailAddresses().get(email_address);
		if (emailAddressObject == null) throw new SQLException("Unable to find EmailAddress: " + email_address);
		return emailAddressObject;
	}

	public EmailList getEmailList() throws SQLException, IOException {
		EmailList emailListObject = table.connector.getEmailLists().get(email_list);
		if (emailListObject == null) throw new SQLException("Unable to find EmailList: " + email_list);
		return emailListObject;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.EMAIL_LIST_ADDRESSES;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey=result.getInt(1);
		email_address=result.getInt(2);
		email_list=result.getInt(3);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readCompressedInt();
		email_address=in.readCompressedInt();
		email_list=in.readCompressedInt();
	}

	@Override
	public List<CannotRemoveReason<MajordomoList>> getCannotRemoveReasons() throws SQLException, IOException {
		List<CannotRemoveReason<MajordomoList>> reasons=new ArrayList<>();

		// Cannot be used as the list for a majordomo list
		for(MajordomoList ml : table.connector.getMajordomoLists().getRows()) {
			if(ml.getListListAddress().pkey==pkey) {
				EmailDomain ed=ml.getMajordomoServer().getDomain();
				reasons.add(new CannotRemoveReason<>("Used by Majordomo list "+ml.getName()+'@'+ed.getDomain()+" on "+ed.getAOServer().getHostname(), ml));
			}
		}

		return reasons;
	}

	@Override
	public void remove() throws IOException, SQLException {
		table.connector.requestUpdateIL(
			true,
			AOServProtocol.CommandID.REMOVE,
			SchemaTable.TableID.EMAIL_LIST_ADDRESSES,
			pkey
		);
	}

	@Override
	String toStringImpl() throws SQLException, IOException {
		return getEmailAddress().toStringImpl()+"->"+getEmailList().getPath();
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(email_address);
		out.writeCompressedInt(email_list);
	}
}
