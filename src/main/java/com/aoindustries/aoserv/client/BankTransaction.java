/*
 * Copyright 2000-2013, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.sql.SQLUtility;
import com.aoindustries.util.InternUtils;
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

	static final String COLUMN_TIME_name = "time";
	static final String COLUMN_TRANSID_name = "transid";

	private AOServTable<Integer,BankTransaction> table;
	private long time;
	private int transID;
	private String
		bankAccount,
		processor,
		administrator,
		type,
		expenseCode,
		description,
		checkNo
	;
	private int amount;
	private boolean confirmed;

	@Override
	boolean equalsImpl(Object O) {
		return
			O instanceof BankTransaction
			&& ((BankTransaction)O).transID==transID
		;
	}

	public MasterUser getAdministrator() throws SQLException, IOException {
		MasterUser obj = table.connector.getMasterUsers().get(administrator);
		if (obj == null) throw new SQLException("Unable to find MasterUser: " + administrator);
		return obj;
	}

	public int getAmount() {
		return amount;
	}

	public BankAccount getBankAccount() throws SQLException, IOException {
		BankAccount bankAccountObject = table.connector.getBankAccounts().get(bankAccount);
		if (bankAccountObject == null) throw new SQLException("BankAccount not found: " + bankAccount);
		return bankAccountObject;
	}

	public BankTransactionType getBankTransactionType() throws SQLException, IOException {
		BankTransactionType typeObject = table.connector.getBankTransactionTypes().get(type);
		if (typeObject == null) throw new SQLException("BankTransactionType not found: " + type);
		return typeObject;
	}

	public String getCheckNo() {
		return checkNo;
	}

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case 0: return getTime();
			case 1: return transID;
			case 2: return bankAccount;
			case 3: return processor;
			case 4: return administrator;
			case 5: return type;
			case 6: return expenseCode;
			case 7: return description;
			case 8: return checkNo;
			case 9: return amount;
			case 10: return confirmed;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public String getDescription() {
		return description;
	}

	public ExpenseCategory getExpenseCategory() throws SQLException, IOException {
		if(expenseCode==null) return null;
		ExpenseCategory cat=table.connector.getExpenseCategories().get(expenseCode);
		if (cat == null) throw new SQLException("ExpenseCategory not found: " + expenseCode);
		return cat;
	}

	public CreditCardProcessor getCreditCardProcessor() throws SQLException, IOException {
		if (processor == null) return null;
		CreditCardProcessor ccProcessor = table.connector.getCreditCardProcessors().get(processor);
		if (ccProcessor == null) throw new SQLException("CreditCardProcessor not found: " + processor);
		return ccProcessor;
	}

	@Override
	public Integer getKey() {
		return transID;
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
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.BANK_TRANSACTIONS;
	}

	public Timestamp getTime() {
		return new Timestamp(time);
	}

	public int getTransID() {
		return transID;
	}

	@Override
	int hashCodeImpl() {
		return transID;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		time = result.getTimestamp(1).getTime();
		transID = result.getInt(2);
		bankAccount = result.getString(3);
		processor = result.getString(4);
		administrator = result.getString(5);
		type = result.getString(6);
		expenseCode = result.getString(7);
		description = result.getString(8);
		checkNo = result.getString(9);
		amount = SQLUtility.getPennies(result.getString(10));
		confirmed = result.getBoolean(11);
	}

	public boolean isConfirmed() {
		return confirmed;
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		time = in.readLong();
		transID = in.readCompressedInt();
		bankAccount = in.readUTF().intern();
		processor = InternUtils.intern(in.readNullUTF());
		administrator = in.readUTF().intern();
		type = in.readUTF().intern();
		expenseCode = InternUtils.intern(in.readNullUTF());
		description = in.readUTF();
		checkNo = in.readNullUTF();
		amount = in.readCompressedInt();
		confirmed = in.readBoolean();
	}

	@Override
	public void setTable(AOServTable<Integer,BankTransaction> table) {
		if(this.table!=null) throw new IllegalStateException("table already set");
		this.table=table;
	}

	@Override
	String toStringImpl() {
		return transID+"|"+administrator+'|'+type+'|'+SQLUtility.getDecimal(amount);
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeLong(time);
		out.writeCompressedInt(transID);
		out.writeUTF(bankAccount);
		if(version.compareTo(AOServProtocol.Version.VERSION_1_29)<0) {
			out.writeNullUTF(null);
		} else {
			out.writeNullUTF(processor);
		}
		out.writeUTF(administrator);
		out.writeUTF(type);
		out.writeNullUTF(expenseCode);
		out.writeUTF(description);
		out.writeNullUTF(checkNo);
		out.writeCompressedInt(amount);
		out.writeBoolean(confirmed);
	}
}