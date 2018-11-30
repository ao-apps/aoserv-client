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
package com.aoindustries.aoserv.client.billing;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.AOServTable;
import com.aoindustries.aoserv.client.account.Business;
import com.aoindustries.aoserv.client.account.BusinessAdministrator;
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.aosh.AOSHCommand;
import com.aoindustries.aoserv.client.payment.CreditCardProcessor;
import com.aoindustries.aoserv.client.payment.PaymentType;
import com.aoindustries.aoserv.client.schema.AOServProtocol;
import com.aoindustries.aoserv.client.schema.SchemaTable;
import com.aoindustries.aoserv.client.schema.SchemaType;
import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.util.IntList;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @see  Transaction
 *
 * @author  AO Industries, Inc.
 */
final public class TransactionTable extends AOServTable<Integer,Transaction> {

	private long accountBalancesClearCounter = 0;
	final private Map<AccountingCode,BigDecimal> accountBalances=new HashMap<>();
	private long confirmedAccountBalancesClearCounter = 0;
	final private Map<AccountingCode,BigDecimal> confirmedAccountBalances=new HashMap<>();

	public TransactionTable(AOServConnector connector) {
		super(connector, Transaction.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(Transaction.COLUMN_TIME_name+"::"+SchemaType.DATE_name, ASCENDING),
		new OrderBy(Transaction.COLUMN_TRANSID_name, ASCENDING)
	};
	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	public int addTransaction(
		final Business business,
		final Business sourceBusiness,
		final BusinessAdministrator business_administrator,
		final String type,
		final String description,
		final int quantity,
		final int rate,
		final PaymentType paymentType,
		final String paymentInfo,
		final CreditCardProcessor processor,
		final byte payment_confirmed
	) throws IOException, SQLException {
		return connector.requestResult(
			false,
			AOServProtocol.CommandID.ADD,
			new AOServConnector.ResultRequest<Integer>() {
				int transid;
				IntList invalidateList;

				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(SchemaTable.TableID.TRANSACTIONS.ordinal());
					out.writeUTF(business.getAccounting().toString());
					out.writeUTF(sourceBusiness.getAccounting().toString());
					out.writeUTF(business_administrator.getUsername_userId().toString());
					out.writeUTF(type);
					out.writeUTF(description);
					out.writeCompressedInt(quantity);
					out.writeCompressedInt(rate);
					out.writeBoolean(paymentType!=null); if(paymentType!=null) out.writeUTF(paymentType.getName());
					out.writeNullUTF(paymentInfo);
					out.writeNullUTF(processor==null ? null : processor.getProviderId());
					out.writeByte(payment_confirmed);
				}

				@Override
				public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AOServProtocol.DONE) {
						transid=in.readCompressedInt();
						invalidateList=AOServConnector.readInvalidateList(in);
					} else {
						AOServProtocol.checkResult(code, in);
						throw new IOException("Unexpected response code: "+code);
					}
				}

				@Override
				public Integer afterRelease() {
					connector.tablesUpdated(invalidateList);
					return transid;
				}
			}
		);
	}

	@Override
	public void clearCache() {
		// System.err.println("DEBUG: TransactionTable: clearCache() called");
		super.clearCache();
		synchronized(accountBalances) {
			accountBalancesClearCounter++;
			accountBalances.clear();
		}
		synchronized(confirmedAccountBalances) {
			confirmedAccountBalancesClearCounter++;
			confirmedAccountBalances.clear();
		}
	}

	public BigDecimal getAccountBalance(AccountingCode accounting) throws IOException, SQLException {
		long clearCounter;
		synchronized(accountBalances) {
			BigDecimal balance=accountBalances.get(accounting);
			if(balance!=null) return balance;
			clearCounter = accountBalancesClearCounter;
		}
		BigDecimal balance=BigDecimal.valueOf(connector.requestIntQuery(true, AOServProtocol.CommandID.GET_ACCOUNT_BALANCE, accounting.toString()), 2);
		synchronized(accountBalances) {
			// Only put in cache when not cleared while performing query
			if(clearCounter==accountBalancesClearCounter) accountBalances.put(accounting, balance);
		}
		return balance;
	}

	public BigDecimal getAccountBalance(AccountingCode accounting, long before) throws IOException, SQLException {
		return BigDecimal.valueOf(connector.requestIntQuery(true, AOServProtocol.CommandID.GET_ACCOUNT_BALANCE_BEFORE, accounting.toString(), before), 2);
	}

	public BigDecimal getConfirmedAccountBalance(AccountingCode accounting) throws IOException, SQLException {
		long clearCounter;
		synchronized(confirmedAccountBalances) {
			BigDecimal balance=confirmedAccountBalances.get(accounting);
			if(balance!=null) return balance;
			clearCounter = confirmedAccountBalancesClearCounter;
		}
		BigDecimal balance=BigDecimal.valueOf(connector.requestIntQuery(true, AOServProtocol.CommandID.GET_CONFIRMED_ACCOUNT_BALANCE, accounting.toString()), 2);
		synchronized(confirmedAccountBalances) {
			// Only put in cache when not cleared while performing query
			if(clearCounter==confirmedAccountBalancesClearCounter) confirmedAccountBalances.put(accounting, balance);
		}
		return balance;
	}

	public BigDecimal getConfirmedAccountBalance(AccountingCode accounting, long before) throws IOException, SQLException {
		return BigDecimal.valueOf(connector.requestIntQuery(true, AOServProtocol.CommandID.GET_CONFIRMED_ACCOUNT_BALANCE_BEFORE, accounting.toString(), before), 2);
	}

	public List<Transaction> getPendingPayments() throws IOException, SQLException {
		return getObjects(true, AOServProtocol.CommandID.GET_PENDING_PAYMENTS);
	}

	@Override
	public List<Transaction> getRows() throws IOException, SQLException {
		List<Transaction> list=new ArrayList<>();
		getObjects(true, list, AOServProtocol.CommandID.GET_TABLE, SchemaTable.TableID.TRANSACTIONS);
		return list;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.TRANSACTIONS;
	}

	/**
	 * @deprecated  Always try to lookup by specific keys; the compiler will help you more when types change.
	 */
	@Deprecated
	@Override
	public Transaction get(Object transid) throws IOException, SQLException {
		return get(((Integer)transid).intValue());
	}

	public Transaction get(int transid) throws IOException, SQLException {
		return getObject(true, AOServProtocol.CommandID.GET_OBJECT, SchemaTable.TableID.TRANSACTIONS, transid);
	}

	List<Transaction> getTransactions(TransactionSearchCriteria search) throws IOException, SQLException {
		return getObjects(true, AOServProtocol.CommandID.GET_TRANSACTIONS_SEARCH, search);
	}

	public List<Transaction> getTransactions(AccountingCode accounting) throws IOException, SQLException {
		return getObjects(true, AOServProtocol.CommandID.GET_TRANSACTIONS_BUSINESS, accounting.toString());
	}

	public List<Transaction> getTransactions(BusinessAdministrator ba) throws IOException, SQLException {
		return getObjects(true, AOServProtocol.CommandID.GET_TRANSACTIONS_BUSINESS_ADMINISTRATOR, ba.getUsername_userId());
	}

	@Override
	public List<Transaction> getIndexedRows(int col, Object value) throws IOException, SQLException {
		if(col==Transaction.COLUMN_TRANSID) {
			Transaction tr=get(value);
			if(tr==null) return Collections.emptyList();
			else return Collections.singletonList(tr);
		}
		if(col==Transaction.COLUMN_ACCOUNTING) return getTransactions((AccountingCode)value);
		throw new UnsupportedOperationException("Not an indexed column: "+col);
	}

	@Override
	protected Transaction getUniqueRowImpl(int col, Object value) throws IOException, SQLException {
		if(col!=Transaction.COLUMN_TRANSID) throw new IllegalArgumentException("Not a unique column: "+col);
		return get(value);
	}

	@Override
	public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(AOSHCommand.ADD_TRANSACTION)) {
			if(AOSH.checkParamCount(AOSHCommand.ADD_TRANSACTION, args, 11, err)) {
				byte pc;
				if(args[11].equals("Y")) pc=Transaction.CONFIRMED;
				else if(args[11].equals("W")) pc=Transaction.WAITING_CONFIRMATION;
				else if(args[11].equals("N")) pc=Transaction.NOT_CONFIRMED;
				else throw new IllegalArgumentException("Unknown value for payment_confirmed, should be one of Y, W, or N: "+args[11]);
				out.println(
					connector.getSimpleAOClient().addTransaction(
						AOSH.parseAccountingCode(args[1], "business"),
						AOSH.parseAccountingCode(args[2], "source_business"),
						AOSH.parseUserId(args[3], "business_administrator"),
						args[4],
						args[5],
						AOSH.parseMillis(args[6], "quantity"),
						AOSH.parsePennies(args[7], "rate"),
						args[8],
						args[9],
						args[10],
						pc
					)
				);
				out.flush();
			}
			return true;
		}
		return false;
	}
}
