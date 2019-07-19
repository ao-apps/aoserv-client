/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018, 2019  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.account;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.net.Email;
import com.aoindustries.util.IntList;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

/**
 * @see  Profile
 *
 * @author  AO Industries, Inc.
 */
final public class ProfileTable extends CachedTableIntegerKey<Profile> {

	ProfileTable(AOServConnector connector) {
		super(connector, Profile.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(Profile.COLUMN_ACCOUNTING_name, ASCENDING),
		new OrderBy(Profile.COLUMN_PRIORITY_name, DESCENDING)
	};
	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	int addProfile(
		final Account business,
		final String name,
		final boolean isPrivate,
		final String phone,
		String fax,
		final String address1,
		String address2,
		final String city,
		String state,
		final String country,
		String zip,
		final boolean sendInvoice,
		final String billingContact,
		final Set<Email> billingEmail,
		final Profile.EmailFormat billingEmailFormat,
		final String technicalContact,
		final Set<Email> technicalEmail,
		final Profile.EmailFormat technicalEmailFormat
	) throws IOException, SQLException {
		if(fax!=null && fax.length()==0) fax=null;
		final String finalFax = fax;
		if(address2!=null && address2.length()==0) address2=null;
		final String finalAddress2 = address2;
		if(state!=null && state.length()==0) state=null;
		final String finalState = state;
		if(zip!=null && zip.length()==0) zip=null;
		final String finalZip = zip;
		// Create the new profile
		return connector.requestResult(
			true,
			AoservProtocol.CommandID.ADD,
			new AOServConnector.ResultRequest<Integer>() {
				int pkey;
				IntList invalidateList;

				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(Table.TableID.BUSINESS_PROFILES.ordinal());
					out.writeUTF(business.getName().toString());
					out.writeUTF(name);
					out.writeBoolean(isPrivate);
					out.writeUTF(phone);
					out.writeBoolean(finalFax!=null);
					if(finalFax!=null) out.writeUTF(finalFax);
					out.writeUTF(address1);
					out.writeBoolean(finalAddress2!=null);
					if(finalAddress2!=null) out.writeUTF(finalAddress2);
					out.writeUTF(city);
					out.writeBoolean(finalState!=null);
					if(finalState!=null) out.writeUTF(finalState);
					out.writeUTF(country);
					out.writeBoolean(finalZip!=null);
					if(finalZip!=null) out.writeUTF(finalZip);
					out.writeBoolean(sendInvoice);
					out.writeUTF(billingContact);
					out.writeCompressedInt(billingEmail.size());
					for(Email email : billingEmail) out.writeUTF(email.toString());
					out.writeEnum(billingEmailFormat);
					out.writeUTF(technicalContact);
					out.writeCompressedInt(technicalEmail.size());
					for(Email email : technicalEmail) out.writeUTF(email.toString());
					out.writeEnum(technicalEmailFormat);
				}

				@Override
				public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AoservProtocol.DONE) {
						pkey=in.readCompressedInt();
						invalidateList=AOServConnector.readInvalidateList(in);
					} else {
						AoservProtocol.checkResult(code, in);
						throw new IOException("Unexpected response code: "+code);
					}
				}

				@Override
				public Integer afterRelease() {
					connector.tablesUpdated(invalidateList);
					return pkey;
				}
			}
		);
	}

	@Override
	public Profile get(int pkey) throws IOException, SQLException {
		return getUniqueRow(Profile.COLUMN_PKEY, pkey);
	}

	/**
	 * Gets the highest priority {@link Profile} for
	 * the provided {@link Account}.
	 */
	Profile getProfile(Account account) throws IOException, SQLException {
		Account.Name account_name = account.getName();
		List<Profile> cached=getRows();
		int size=cached.size();
		for(int c=0;c<size;c++) {
			Profile profile=cached.get(c);
			// Return first found because sorted highest priority first
			if(profile.accounting.equals(account_name)) return profile;
		}
		return null;
	}

	List<Profile> getProfiles(Account business) throws IOException, SQLException {
		return getIndexedRows(Profile.COLUMN_ACCOUNTING, business.getName());
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.BUSINESS_PROFILES;
	}

	@Override
	public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
		String command=args[0];
		if(command.equalsIgnoreCase(Command.ADD_BUSINESS_PROFILE)) {
			if(AOSH.checkParamCount(Command.ADD_BUSINESS_PROFILE, args, 18, err)) {
				try {
					out.println(
						connector.getSimpleAOClient().addProfile(
							AOSH.parseAccountingCode(args[1], "business"),
							args[2],
							AOSH.parseBoolean(args[3], "is_secure"),
							args[4],
							args[5],
							args[6],
							args[7],
							args[8],
							args[9],
							args[10],
							args[11],
							AOSH.parseBoolean(args[12], "send_invoice"),
							args[13],
							Profile.splitEmails(args[14]),
							args[15],
							args[16],
							Profile.splitEmails(args[17]),
							args[18]
						)
					);
					out.flush();
				} catch(IllegalArgumentException | ValidationException | IOException | SQLException iae) {
					err.print("aosh: "+Command.ADD_BUSINESS_PROFILE+": ");
					err.println(iae.getMessage());
					err.flush();
				}
			}
			return true;
		} else return false;
	}
}
