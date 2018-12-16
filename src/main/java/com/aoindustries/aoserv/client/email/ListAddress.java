/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2013, 2016, 2017, 2018  AO Industries, Inc.
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

import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.CannotRemoveReason;
import com.aoindustries.aoserv.client.Removable;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * An {@link List} may receive email on multiple addresses, and
 * then forward those emails to the list of destinations.  An
 * <code>EmailListAddress</code> directs incoming emails to the email list.
 *
 * @see  List
 * @see  Address
 *
 * @author  AO Industries, Inc.
 */
final public class ListAddress extends CachedObjectIntegerKey<ListAddress> implements Removable {

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
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_EMAIL_ADDRESS: return email_address;
			case COLUMN_EMAIL_LIST: return email_list;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public int getEmailAddress_pkey() {
		return email_address;
	}

	public Address getEmailAddress() throws SQLException, IOException {
		Address emailAddressObject = table.getConnector().getEmail().getEmailAddresses().get(email_address);
		if (emailAddressObject == null) throw new SQLException("Unable to find EmailAddress: " + email_address);
		return emailAddressObject;
	}

	public int getEmailList_pkey() {
		return email_list;
	}

	public List getEmailList() throws SQLException, IOException {
		List emailListObject = table.getConnector().getEmail().getEmailLists().get(email_list);
		if (emailListObject == null) throw new SQLException("Unable to find EmailList: " + email_list);
		return emailListObject;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.EMAIL_LIST_ADDRESSES;
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
	public java.util.List<CannotRemoveReason<MajordomoList>> getCannotRemoveReasons() throws SQLException, IOException {
		java.util.List<CannotRemoveReason<MajordomoList>> reasons=new ArrayList<>();

		// Cannot be used as the list for a majordomo list
		for(MajordomoList ml : table.getConnector().getEmail().getMajordomoLists().getRows()) {
			if(ml.getListListAddress().getPkey()==pkey) {
				Domain ed=ml.getMajordomoServer().getDomain();
				reasons.add(new CannotRemoveReason<>("Used by Majordomo list "+ml.getName()+'@'+ed.getDomain()+" on "+ed.getAOServer().getHostname(), ml));
			}
		}

		return reasons;
	}

	@Override
	public void remove() throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true,
			AoservProtocol.CommandID.REMOVE,
			Table.TableID.EMAIL_LIST_ADDRESSES,
			pkey
		);
	}

	@Override
	public String toStringImpl() throws SQLException, IOException {
		return getEmailAddress().toStringImpl()+"->"+getEmailList().getPath();
	}

	@Override
	public void write(CompressedDataOutputStream out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(email_address);
		out.writeCompressedInt(email_list);
	}
}
