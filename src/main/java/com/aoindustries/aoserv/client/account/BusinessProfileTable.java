/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.aosh.AOSHCommand;
import com.aoindustries.aoserv.client.schema.AOServProtocol;
import com.aoindustries.aoserv.client.schema.SchemaTable;
import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.util.IntList;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  BusinessProfile
 *
 * @author  AO Industries, Inc.
 */
final public class BusinessProfileTable extends CachedTableIntegerKey<BusinessProfile> {

	public BusinessProfileTable(AOServConnector connector) {
		super(connector, BusinessProfile.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(BusinessProfile.COLUMN_ACCOUNTING_name, ASCENDING),
		new OrderBy(BusinessProfile.COLUMN_PRIORITY_name, DESCENDING)
	};
	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	int addBusinessProfile(
		final Business business,
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
		final String billingEmail,
		final String technicalContact,
		final String technicalEmail
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
			AOServProtocol.CommandID.ADD,
			new AOServConnector.ResultRequest<Integer>() {
				int pkey;
				IntList invalidateList;

				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(SchemaTable.TableID.BUSINESS_PROFILES.ordinal());
					out.writeUTF(business.getAccounting().toString());
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
					out.writeUTF(billingEmail);
					out.writeUTF(technicalContact);
					out.writeUTF(technicalEmail);
				}

				@Override
				public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AOServProtocol.DONE) {
						pkey=in.readCompressedInt();
						invalidateList=AOServConnector.readInvalidateList(in);
					} else {
						AOServProtocol.checkResult(code, in);
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
	public BusinessProfile get(int pkey) throws IOException, SQLException {
		return getUniqueRow(BusinessProfile.COLUMN_PKEY, pkey);
	}

	/**
	 * Gets the highest priority  <code>BusinessProfile</code> for
	 * the provided <code>Business</code>.
	 */
	BusinessProfile getBusinessProfile(Business business) throws IOException, SQLException {
		AccountingCode accounting=business.getAccounting();
		List<BusinessProfile> cached=getRows();
		int size=cached.size();
		for(int c=0;c<size;c++) {
			BusinessProfile profile=cached.get(c);
			// Return first found because sorted highest priority first
			if(profile.accounting.equals(accounting)) return profile;
		}
		return null;
	}

	List<BusinessProfile> getBusinessProfiles(Business business) throws IOException, SQLException {
		return getIndexedRows(BusinessProfile.COLUMN_ACCOUNTING, business.getAccounting());
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.BUSINESS_PROFILES;
	}

	@Override
	public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
		String command=args[0];
		if(command.equalsIgnoreCase(AOSHCommand.ADD_BUSINESS_PROFILE)) {
			if(AOSH.checkParamCount(AOSHCommand.ADD_BUSINESS_PROFILE, args, 16, err)) {
				try {
					out.println(
						connector.getSimpleAOClient().addBusinessProfile(
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
							args[14],
							args[15],
							args[16]
						)
					);
					out.flush();
				} catch(IllegalArgumentException | IOException | SQLException iae) {
					err.print("aosh: "+AOSHCommand.ADD_BUSINESS_PROFILE+": ");
					err.println(iae.getMessage());
					err.flush();
				}
			}
			return true;
		} else return false;
	}
}
