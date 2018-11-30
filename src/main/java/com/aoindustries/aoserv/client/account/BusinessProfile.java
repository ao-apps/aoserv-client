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
package com.aoindustries.aoserv.client.account;

import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.payment.CountryCode;
import com.aoindustries.aoserv.client.schema.AOServProtocol;
import com.aoindustries.aoserv.client.schema.SchemaTable;
import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.util.InternUtils;
import com.aoindustries.util.StringUtility;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

/**
 * Contact information associated with a <code>Business</code>.
 *
 * @author  AO Industries, Inc.
 */
final public class BusinessProfile extends CachedObjectIntegerKey<BusinessProfile> {

	static final int
		COLUMN_PKEY=0,
		COLUMN_ACCOUNTING=1
	;
	static final String COLUMN_ACCOUNTING_name = "accounting";
	static final String COLUMN_PRIORITY_name = "priority";

	AccountingCode accounting;
	private int priority;

	private String name;

	private boolean isPrivate;
	private String phone, fax, address1, address2, city, state;
	String country;
	private String zip;

	private boolean sendInvoice;

	private long created;

	private String billingContact, billingEmail, technicalContact, technicalEmail;

	public String getAddress1() {
		return address1;
	}

	public String getAddress2() {
		return address2;
	}

	public String getBillingContact() {
		return billingContact;
	}

	public List<String> getBillingEmail() {
		return StringUtility.splitStringCommaSpace(billingEmail);
	}

	public AccountingCode getBusiness_accounting() {
		return accounting;
	}

	public Business getBusiness() throws SQLException, IOException {
		Business business=table.getConnector().getBusinesses().get(accounting);
		if (business == null) throw new SQLException("Unable to find Business: " + accounting);
		return business;
	}

	public String getCity() {
		return city;
	}

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_ACCOUNTING: return accounting;
			case 2: return priority;
			case 3: return name;
			case 4: return isPrivate;
			case 5: return phone;
			case 6: return fax;
			case 7: return address1;
			case 8: return address2;
			case 9: return city;
			case 10: return state;
			case 11: return country;
			case 12: return zip;
			case 13: return sendInvoice;
			case 14: return getCreated();
			case 15: return billingContact;
			case 16: return billingEmail;
			case 17: return technicalContact;
			case 18: return technicalEmail;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public String getCountry_code() {
		return country;
	}

	public CountryCode getCountry() throws SQLException, IOException {
		CountryCode countryCode = table.getConnector().getCountryCodes().get(country);
		if (countryCode == null) throw new SQLException("CountryCode not found: " + country);
		return countryCode;
	}

	public Timestamp getCreated() {
		return new Timestamp(created);
	}

	public String getFax() {
		return fax;
	}

	public String getName() {
		return name;
	}

	public String getPhone() {
		return phone;
	}

	public int getPriority() {
		return priority;
	}

	public String getState() {
		return state;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.BUSINESS_PROFILES;
	}

	public String getTechnicalContact() {
		return technicalContact;
	}

	public List<String> getTechnicalEmail() {
		return StringUtility.splitStringCommaSpace(technicalEmail);
	}

	public String getZIP() {
		return zip;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			pkey = result.getInt(1);
			accounting = AccountingCode.valueOf(result.getString(2));
			priority = result.getInt(3);
			name = result.getString(4);
			isPrivate = result.getBoolean(5);
			phone = result.getString(6);
			fax = result.getString(7);
			address1 = result.getString(8);
			address2 = result.getString(9);
			city = result.getString(10);
			state = result.getString(11);
			country = result.getString(12);
			zip = result.getString(13);
			sendInvoice = result.getBoolean(14);
			created = result.getTimestamp(15).getTime();
			billingContact = result.getString(16);
			billingEmail = result.getString(17);
			technicalContact = result.getString(18);
			technicalEmail = result.getString(19);
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	public boolean isPrivate() {
		return isPrivate;
	}

	/*
	private static String makeEmailList(String[] addresses) {
	StringBuilder SB=new StringBuilder();
	int len=addresses.length;
	for(int c=0;c<len;c++) {
			if(c>0) SB.append(' ');
			SB.append(addresses[c]);
	}
	return SB.toString();
	}*/

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			pkey=in.readCompressedInt();
			accounting=AccountingCode.valueOf(in.readUTF()).intern();
			priority=in.readCompressedInt();
			name=in.readUTF();
			isPrivate=in.readBoolean();
			phone=in.readUTF();
			fax=in.readNullUTF();
			address1=in.readUTF();
			address2=in.readNullUTF();
			city=in.readUTF();
			state=InternUtils.intern(in.readNullUTF());
			country=in.readUTF().intern();
			zip=in.readNullUTF();
			sendInvoice=in.readBoolean();
			created=in.readLong();
			billingContact=in.readUTF();
			billingEmail=in.readUTF();
			technicalContact=in.readUTF();
			technicalEmail=in.readUTF();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	public boolean sendInvoice() {
		return sendInvoice;
	}

	@Override
	public String toStringImpl() {
		return name + " ("+priority+')';
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeUTF(accounting.toString());
		out.writeCompressedInt(priority);
		out.writeUTF(name);
		out.writeBoolean(isPrivate);
		out.writeUTF(phone);
		out.writeNullUTF(fax);
		out.writeUTF(address1);
		out.writeNullUTF(address2);
		out.writeUTF(city);
		out.writeNullUTF(state);
		out.writeUTF(country);
		out.writeNullUTF(zip);
		out.writeBoolean(sendInvoice);
		out.writeLong(created);
		out.writeUTF(billingContact);
		out.writeUTF(billingEmail);
		out.writeUTF(technicalContact);
		out.writeUTF(technicalEmail);
	}
}
