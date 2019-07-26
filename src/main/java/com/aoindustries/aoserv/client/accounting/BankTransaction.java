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
package com.aoindustries.aoserv.client.accounting;

import com.aoindustries.aoserv.client.AOServObject;
import com.aoindustries.aoserv.client.AOServTable;
import com.aoindustries.aoserv.client.SingleTableObject;
import com.aoindustries.aoserv.client.account.User;
import com.aoindustries.aoserv.client.payment.Processor;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.sql.SQLUtility;
import com.aoindustries.util.InternUtils;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * For AO Industries use only.
 *
 * @author  AO Industries, Inc.
 */
final public class BankTransaction extends AOServObject<Integer,BankTransaction> implements SingleTableObject<Integer,BankTransaction> {

	static final int COLUMN_ID = 0;
	static final String COLUMN_ID_name = "id";
	static final String COLUMN_TIME_name = "time";

	private AOServTable<Integer,BankTransaction> table;
	private int id;
	private long time;
	private String
		account,
		processor;
	private User.Name administrator;
	private String
		type,
		expenseCategory,
		description,
		checkNo
	;
	private int amount;
	private boolean confirmed;

	@Override
	public boolean equals(Object O) {
		return
			O instanceof BankTransaction
			&& ((BankTransaction)O).id == id
		;
	}

	public com.aoindustries.aoserv.client.master.User getAdministrator() throws SQLException, IOException {
		com.aoindustries.aoserv.client.master.User obj = table.getConnector().getMaster().getUser().get(administrator);
		if (obj == null) throw new SQLException("Unable to find MasterUser: " + administrator);
		return obj;
	}

	public int getAmount() {
		return amount;
	}

	public BankAccount getBankAccount() throws SQLException, IOException {
		BankAccount bankAccountObject = table.getConnector().getAccounting().getBankAccount().get(account);
		if (bankAccountObject == null) throw new SQLException("BankAccount not found: " + account);
		return bankAccountObject;
	}

	public BankTransactionType getBankTransactionType() throws SQLException, IOException {
		BankTransactionType typeObject = table.getConnector().getAccounting().getBankTransactionType().get(type);
		if (typeObject == null) throw new SQLException("BankTransactionType not found: " + type);
		return typeObject;
	}

	public String getCheckNo() {
		return checkNo;
	}

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_ID: return id;
			case 1: return getTime();
			case 2: return account;
			case 3: return processor;
			case 4: return administrator;
			case 5: return type;
			case 6: return expenseCategory;
			case 7: return description;
			case 8: return checkNo;
			case 9: return amount;
			case 10: return confirmed;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	public String getDescription() {
		return description;
	}

	public ExpenseCategory getExpenseCategory() throws SQLException, IOException {
		if(expenseCategory==null) return null;
		ExpenseCategory cat=table.getConnector().getAccounting().getExpenseCategory().get(expenseCategory);
		if (cat == null) throw new SQLException("ExpenseCategory not found: " + expenseCategory);
		return cat;
	}

	public Processor getCreditCardProcessor() throws SQLException, IOException {
		if (processor == null) return null;
		Processor ccProcessor = table.getConnector().getPayment().getProcessor().get(processor);
		if (ccProcessor == null) throw new SQLException("CreditCardProcessor not found: " + processor);
		return ccProcessor;
	}

	@Override
	public Integer getKey() {
		return id;
	}

	/**
	 * Gets the <code>AOServTable</code> that contains this <code>AOServObject</code>.
	 *
	 * @return  the <code>AOServTable</code>.
	 */
	@Override
	public AOServTable<Integer,BankTransaction> getTable() {
		return table;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.BANK_TRANSACTIONS;
	}

	public int getId() {
		return id;
	}

	public long getTime_millis() {
		return time;
	}

	public Timestamp getTime() {
		return new Timestamp(time);
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			int pos = 1;
			id = result.getInt(pos++);
			time = result.getTimestamp(pos++).getTime();
			account = result.getString(pos++);
			processor = result.getString(pos++);
			administrator = User.Name.valueOf(result.getString(pos++));
			type = result.getString(pos++);
			expenseCategory = result.getString(pos++);
			description = result.getString(pos++);
			checkNo = result.getString(pos++);
			amount = SQLUtility.parseDecimal2(result.getString(pos++));
			confirmed = result.getBoolean(pos++);
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	public boolean isConfirmed() {
		return confirmed;
	}

	@Override
	public void read(CompressedDataInputStream in, AoservProtocol.Version protocolVersion) throws IOException {
		try {
			id = in.readCompressedInt();
			time = in.readLong();
			account = in.readUTF().intern();
			processor = InternUtils.intern(in.readNullUTF());
			administrator = User.Name.valueOf(in.readUTF()).intern();
			type = in.readUTF().intern();
			expenseCategory = InternUtils.intern(in.readNullUTF());
			description = in.readUTF();
			checkNo = in.readNullUTF();
			amount = in.readCompressedInt();
			confirmed = in.readBoolean();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void setTable(AOServTable<Integer,BankTransaction> table) {
		if(this.table!=null) throw new IllegalStateException("table already set");
		this.table=table;
	}

	@Override
	public String toStringImpl() {
		return id+"|"+administrator+'|'+type+'|'+SQLUtility.formatDecimal2(amount);
	}

	@Override
	public void write(CompressedDataOutputStream out, AoservProtocol.Version protocolVersion) throws IOException {
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_81_17) <= 0) {
			out.writeLong(time);
			out.writeCompressedInt(id);
		} else {
			out.writeCompressedInt(id);
			out.writeLong(time);
		}
		out.writeUTF(account);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_29)<0) {
			out.writeNullUTF(null);
		} else {
			out.writeNullUTF(processor);
		}
		out.writeUTF(administrator.toString());
		out.writeUTF(type);
		out.writeNullUTF(expenseCategory);
		out.writeUTF(description);
		out.writeNullUTF(checkNo);
		out.writeCompressedInt(amount);
		out.writeBoolean(confirmed);
	}
}
