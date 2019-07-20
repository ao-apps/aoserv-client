/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2013, 2016, 2017, 2018, 2019  AO Industries, Inc.
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
import com.aoindustries.math.SafeMath;
import com.aoindustries.sql.SQLUtility;
import com.aoindustries.util.i18n.Money;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Miscellaneous monthly charges may be applied to an
 * {@link Account}.  These currently include
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
	private Money rate;
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
		Money rate,
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
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	public long getCreated_millis() {
		return created;
	}

	public Timestamp getCreated() {
		return new Timestamp(created);
	}

	public Administrator getCreatedBy() throws SQLException, IOException {
		Administrator obj = table.getConnector().getAccount().getAdministrator().get(created_by);
		if (obj == null) throw new SQLException("Unable to find Administrator: " + created_by);
		return obj;
	}

	public Account.Name getAccount_name() {
		return accounting;
	}

	public Account getAccount() throws SQLException, IOException {
		Account obj = table.getConnector().getAccount().getAccount().get(accounting);
		if(obj == null) throw new SQLException("Unable to find Account: " + accounting);
		return obj;
	}

	public String getDescription() throws SQLException, IOException {
		return description == null ? getType().getDescription() : description;
	}

	public Package getPackage() throws SQLException, IOException {
		Package packageObject = table.getConnector().getBilling().getPackage().get(packageName);
		if (packageObject == null) throw new SQLException("Unable to find Package: " + packageName);
		return packageObject;
	}

	/**
	 * Gets the effective amount of quantity * rate.
	 */
	public Money getAmount() {
		return rate.multiply(BigDecimal.valueOf(quantity, 3), RoundingMode.HALF_UP);
	}

	public int getQuantity() {
		return quantity;
	}

	public Money getRate() {
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
			pkey = result.getInt("id");
			accounting = Account.Name.valueOf(result.getString("accounting"));
			packageName = Account.Name.valueOf(result.getString("package"));
			type = result.getString("type");
			description = result.getString("description");
			quantity = SQLUtility.getMillis(result.getString("quantity"));
			rate = MoneyUtil.getMoney(result, "rate.currency", "rate.value");
			created = result.getTimestamp("created").getTime();
			created_by = User.Name.valueOf(result.getString("created_by"));
			active = result.getBoolean("active");
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
			pkey = in.readCompressedInt();
			accounting = Account.Name.valueOf(in.readUTF()).intern();
			packageName = Account.Name.valueOf(in.readUTF()).intern();
			type = in.readUTF().intern();
			description = in.readNullUTF();
			quantity = in.readCompressedInt();
			rate = MoneyUtil.readMoney(in);
			created = in.readLong();
			created_by = User.Name.valueOf(in.readUTF()).intern();
			active = in.readBoolean();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public String toStringImpl() {
		return packageName.toString() + '|' + type + '|' + SQLUtility.getMilliDecimal(quantity) + "×" + rate;
	}

	@Override
	public void write(CompressedDataOutputStream out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeUTF(accounting.toString());
		out.writeUTF(packageName.toString());
		out.writeUTF(type);
		out.writeNullUTF(description);
		out.writeCompressedInt(quantity);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
			if(rate != null && rate.getCurrency() == Currency.USD && rate.getScale() == 2) {
				out.writeCompressedInt(SafeMath.castInt(rate.getUnscaledValue()));
			} else {
				out.writeCompressedInt(-1);
			}
		} else {
			MoneyUtil.writeNullMoney(rate, out);
		}
		out.writeLong(created);
		out.writeUTF(created_by.toString());
		out.writeBoolean(active);
	}
}
