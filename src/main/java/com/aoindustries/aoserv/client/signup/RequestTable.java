/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2007-2013, 2016, 2017, 2018  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.signup;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.account.User;
import com.aoindustries.aoserv.client.billing.PackageDefinition;
import com.aoindustries.aoserv.client.payment.CountryCode;
import com.aoindustries.aoserv.client.pki.EncryptionKey;
import com.aoindustries.aoserv.client.reseller.Brand;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.net.Email;
import com.aoindustries.net.InetAddress;
import com.aoindustries.util.IntList;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

/**
 * @see  Request
 *
 * @author  AO Industries, Inc.
 */
final public class RequestTable extends CachedTableIntegerKey<Request> {

	RequestTable(AOServConnector connector) {
		super(connector, Request.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(Request.COLUMN_BRAND_name, ASCENDING),
		new OrderBy(Request.COLUMN_TIME_name, ASCENDING)
	};
	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public Request get(int pkey) throws IOException, SQLException {
		return getUniqueRow(Request.COLUMN_PKEY, pkey);
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.SIGNUP_REQUESTS;
	}

	/**
	 * Encrypts the signup request details and adds to the master database.  The first encryption key flagged to use for signup_from is used as the from
	 * and the first key flagged to use as signup_recipient as the recipient.
	 */
	public int addSignupRequest(
		final Brand brand,
		final InetAddress ip_address,
		final PackageDefinition package_definition,
		final String business_name,
		final String business_phone,
		final String business_fax,
		final String business_address1,
		final String business_address2,
		final String business_city,
		final String business_state,
		final CountryCode business_country,
		final String business_zip,
		final String ba_name,
		final String ba_title,
		final String ba_work_phone,
		final String ba_cell_phone,
		final String ba_home_phone,
		final String ba_fax,
		final Email ba_email,
		final String ba_address1,
		final String ba_address2,
		final String ba_city,
		final String ba_state,
		final CountryCode ba_country,
		final String ba_zip,
		final User.Name ba_username,
		final String billing_contact,
		final Email billing_email,
		final boolean billing_use_monthly,
		final boolean billing_pay_one_year,
		// Encrypted values
		String ba_password,
		String billing_cardholder_name,
		String billing_card_number,
		String billing_expiration_month,
		String billing_expiration_year,
		String billing_street_address,
		String billing_city,
		String billing_state,
		String billing_zip,
		// options
		final Map<String,String> options
	) throws IOException, SQLException {
		// Validate the encrypted parameters
		if(ba_password==null) throw new NullPointerException("ba_password is null");
		if(ba_password.indexOf('\n')!=-1) throw new IllegalArgumentException("ba_password may not contain '\n'");
		if(billing_cardholder_name==null) throw new NullPointerException("billing_cardholder_name is null");
		if(billing_cardholder_name.indexOf('\n')!=-1) throw new IllegalArgumentException("billing_cardholder_name may not contain '\n'");
		if(billing_card_number==null) throw new NullPointerException("billing_card_number is null");
		if(billing_card_number.indexOf('\n')!=-1) throw new IllegalArgumentException("billing_card_number may not contain '\n'");
		if(billing_expiration_month==null) throw new NullPointerException("billing_expiration_month is null");
		if(billing_expiration_month.indexOf('\n')!=-1) throw new IllegalArgumentException("billing_expiration_month may not contain '\n'");
		if(billing_expiration_year==null) throw new NullPointerException("billing_expiration_year is null");
		if(billing_expiration_year.indexOf('\n')!=-1) throw new IllegalArgumentException("billing_expiration_year may not contain '\n'");
		if(billing_street_address==null) throw new NullPointerException("billing_street_address is null");
		if(billing_street_address.indexOf('\n')!=-1) throw new IllegalArgumentException("billing_street_address may not contain '\n'");
		if(billing_city==null) throw new NullPointerException("billing_city is null");
		if(billing_city.indexOf('\n')!=-1) throw new IllegalArgumentException("billing_city may not contain '\n'");
		if(billing_state==null) throw new NullPointerException("billing_state is null");
		if(billing_state.indexOf('\n')!=-1) throw new IllegalArgumentException("billing_state may not contain '\n'");
		if(billing_zip==null) throw new NullPointerException("billing_zip is null");
		if(billing_zip.indexOf('\n')!=-1) throw new IllegalArgumentException("billing_zip may not contain '\n'");

		// Find the from and recipient keys
		final EncryptionKey from = brand.getSignupEncryptionFrom();
		final EncryptionKey recipient = brand.getSignupEncryptionRecipient();

		// Encrypt the message
		String plaintext =
			ba_password + "\n"
			+ billing_cardholder_name + "\n"
			+ billing_card_number + "\n"
			+ billing_expiration_month + "\n"
			+ billing_expiration_year + "\n"
			+ billing_street_address + "\n"
			+ billing_city + "\n"
			+ billing_state + "\n"
			+ billing_zip + "\n"
		;
		final String ciphertext = from.encrypt(recipient, plaintext);

		// Send the request to the master server
		return connector.requestResult(true,
			AoservProtocol.CommandID.ADD,
			new AOServConnector.ResultRequest<Integer>() {
				int pkey;
				IntList invalidateList;

				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(Table.TableID.SIGNUP_REQUESTS.ordinal());
					out.writeUTF(brand.getBusiness_accounting().toString());
					out.writeUTF(ip_address.toString());
					out.writeCompressedInt(package_definition.getPkey());
					out.writeUTF(business_name);
					out.writeUTF(business_phone);
					out.writeBoolean(business_fax!=null); if(business_fax!=null) out.writeUTF(business_fax);
					out.writeUTF(business_address1);
					out.writeBoolean(business_address2!=null); if(business_address2!=null) out.writeUTF(business_address2);
					out.writeUTF(business_city);
					out.writeBoolean(business_state!=null); if(business_state!=null) out.writeUTF(business_state);
					out.writeUTF(business_country.getCode());
					out.writeBoolean(business_zip!=null); if(business_zip!=null) out.writeUTF(business_zip);
					out.writeUTF(ba_name);
					out.writeBoolean(ba_title!=null); if(ba_title!=null) out.writeUTF(ba_title);
					out.writeUTF(ba_work_phone);
					out.writeBoolean(ba_cell_phone!=null); if(ba_cell_phone!=null) out.writeUTF(ba_cell_phone);
					out.writeBoolean(ba_home_phone!=null); if(ba_home_phone!=null) out.writeUTF(ba_home_phone);
					out.writeBoolean(ba_fax!=null); if(ba_fax!=null) out.writeUTF(ba_fax);
					out.writeUTF(ba_email.toString());
					out.writeBoolean(ba_address1!=null); if(ba_address1!=null) out.writeUTF(ba_address1);
					out.writeBoolean(ba_address2!=null); if(ba_address2!=null) out.writeUTF(ba_address2);
					out.writeBoolean(ba_city!=null); if(ba_city!=null) out.writeUTF(ba_city);
					out.writeBoolean(ba_state!=null); if(ba_state!=null) out.writeUTF(ba_state);
					out.writeBoolean(ba_country!=null); if(ba_country!=null) out.writeUTF(ba_country.getCode());
					out.writeBoolean(ba_zip!=null); if(ba_zip!=null) out.writeUTF(ba_zip);
					out.writeUTF(ba_username.toString());
					out.writeUTF(billing_contact);
					out.writeUTF(billing_email.toString());
					out.writeBoolean(billing_use_monthly);
					out.writeBoolean(billing_pay_one_year);
					// Encrypted values
					out.writeCompressedInt(from.getPkey());
					out.writeCompressedInt(recipient.getPkey());
					out.writeUTF(ciphertext);
					// options
					int numOptions = options.size();
					out.writeCompressedInt(numOptions);
					int optionCount = 0;
					for(String name : options.keySet()) {
						out.writeUTF(name);
						String value = options.get(name);
						out.writeBoolean(value!=null); if(value!=null) out.writeUTF(value);
						optionCount++;
					}
					if(optionCount!=numOptions) throw new IOException("options modified while writing to master");
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
}
