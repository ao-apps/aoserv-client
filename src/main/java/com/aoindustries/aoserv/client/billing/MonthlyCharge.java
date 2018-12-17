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
package com.aoindustries.aoserv.client.billing;

import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.account.Administrator;
import com.aoindustries.aoserv.client.account.User;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.sql.SQLUtility;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Miscellaneous monthly charges may be applied to a
 * <code>Business</code>.  These currently include
 * the recurring charges that are not fully automated.
 * Once all of the accounting data is available in other
 * places of the system, the use of this table will
 * decrease and possibly disappear.
 *
 * @see  Account
 * @see  Transaction
 *
 * @author  AO Industries, Inc.
 */
final public class MonthlyCharge extends CachedObjectIntegerKey<MonthlyCharge> {

	static final int COLUMN_PKEY=0;
	static final String COLUMN_ACCOUNTING_name = "accounting";
	static final String COLUMN_PACKAGE_name = "package";
	static final String COLUMN_TYPE_name = "type";
	static final String COLUMN_CREATED_name = "created";

	private Account.Name accounting;
	Account.Name packageName;
	private String type;
	private String description;
	private int quantity;
	private int rate;
	private long created;
	private User.Name created_by;
	private boolean active;

	public MonthlyCharge() {
	}

	MonthlyCharge(
		MonthlyChargeTable table,
		Account business,
		Package packageObject,
		TransactionType typeObject,
		String description,
		int quantity,
		int rate,
		Administrator createdByObject
	) {
		setTable(table);
		this.pkey=-1;
		this.accounting = business.getName();
		this.packageName = packageObject.getName();
		this.type = typeObject.getName();
		this.description=description;
		this.quantity = quantity;
		this.rate = rate;
		this.created = System.currentTimeMillis();
		this.created_by = createdByObject.getUsername_userId();
		this.active=true;
	}

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey == -1 ? null : pkey;
			case 1: return accounting;
			case 2: return packageName;
			case 3: return type;
			case 4: return description;
			case 5: return quantity;
			case 6: return rate;
			case 7: return getCreated();
			case 8: return created_by;
			case 9: return active;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public Timestamp getCreated() {
		return new Timestamp(created);
	}

	public Administrator getCreatedBy() throws SQLException, IOException {
		Administrator createdByObject = table.getConnector().getAccount().getAdministrator().get(created_by);
		if (createdByObject == null) throw new SQLException("Unable to find BusinessAdministrator: " + created_by);
		return createdByObject;
	}

	public Account getBusiness() throws SQLException, IOException {
		Account bu=table.getConnector().getAccount().getAccount().get(accounting);
		if(bu==null) throw new SQLException("Unable to find Business: "+accounting);
		return bu;
	}

	public String getDescription() throws SQLException, IOException {
		return description == null ? getType().getDescription() : description;
	}

	public Package getPackage() throws SQLException, IOException {
		Package packageObject = table.getConnector().getBilling().getPackage().get(packageName);
		if (packageObject == null) throw new SQLException("Unable to find Package: " + packageName);
		return packageObject;
	}

	public int getPennies() {
		int pennies=quantity*rate/100;
		int fraction=pennies%10;
		pennies/=10;
		if(fraction>=5) pennies++;
		else if(fraction<=-5) pennies--;
		return pennies;
	}

	public int getQuantity() {
		return quantity;
	}

	public int getRate() {
		return rate;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.MONTHLY_CHARGES;
	}

	public TransactionType getType() throws SQLException, IOException {
		TransactionType typeObject = table.getConnector().getBilling().getTransactionType().get(type);
		if (typeObject == null) throw new SQLException("Unable to find TransactionType: " + type);
		return typeObject;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			pkey = result.getInt(1);
			accounting = Account.Name.valueOf(result.getString(2));
			packageName = Account.Name.valueOf(result.getString(3));
			type = result.getString(4);
			description = result.getString(5);
			quantity = SQLUtility.getMillis(result.getString(6));
			rate = SQLUtility.getPennies(result.getString(7));
			created = result.getTimestamp(8).getTime();
			created_by = User.Name.valueOf(result.getString(9));
			active = result.getBoolean(10);
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	public boolean isActive() {
		return active;
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			pkey=in.readCompressedInt();
			accounting=Account.Name.valueOf(in.readUTF()).intern();
			packageName = Account.Name.valueOf(in.readUTF()).intern();
			type=in.readUTF().intern();
			description=in.readNullUTF();
			quantity=in.readCompressedInt();
			rate=in.readCompressedInt();
			created=in.readLong();
			created_by = User.Name.valueOf(in.readUTF()).intern();
			active=in.readBoolean();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public String toStringImpl() {
		return packageName.toString()+'|'+type+'|'+SQLUtility.getMilliDecimal(quantity)+"x$"+SQLUtility.getDecimal(rate);
	}

	@Override
	public void write(CompressedDataOutputStream out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeUTF(accounting.toString());
		out.writeUTF(packageName.toString());
		out.writeUTF(type);
		out.writeNullUTF(description);
		out.writeCompressedInt(quantity);
		out.writeCompressedInt(rate);
		out.writeLong(created);
		out.writeUTF(created_by.toString());
		out.writeBoolean(active);
	}
}