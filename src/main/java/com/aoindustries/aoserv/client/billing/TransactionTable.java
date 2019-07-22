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
package com.aoindustries.aoserv.client.billing;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.account.Administrator;
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.payment.PaymentType;
import com.aoindustries.aoserv.client.payment.Processor;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.schema.Type;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.util.IntList;
import com.aoindustries.util.MinimalList;
import com.aoindustries.util.StringUtility;
import com.aoindustries.util.i18n.CurrencyComparator;
import com.aoindustries.util.i18n.Money;
import com.aoindustries.util.i18n.Monies;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @see  Transaction
 *
 * @author  AO Industries, Inc.
 */
final public class TransactionTable extends CachedTableIntegerKey<Transaction> {

	final private Map<Account.Name,Monies> accountBalances = new HashMap<>();
	final private Map<Account.Name,Monies> confirmedAccountBalances = new HashMap<>();
	final private Map<Transaction,Monies> transactionBalances = new HashMap<>();

	TransactionTable(AOServConnector connector) {
		super(connector, Transaction.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(Transaction.COLUMN_TIME_name+"::"+Type.DATE_name, ASCENDING),
		new OrderBy(Transaction.COLUMN_SOURCE_ACCOUNTING_name, ASCENDING),
		new OrderBy(Transaction.COLUMN_TIME_name, ASCENDING),
		new OrderBy(Transaction.COLUMN_TRANSID_name, ASCENDING)
	};
	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	public int addTransaction(
		final Account account,
		final Account sourceAccount,
		final Administrator administrator,
		final TransactionType type,
		final String description,
		final int quantity,
		final Money rate,
		final PaymentType paymentType,
		final String paymentInfo,
		final Processor processor,
		final byte paymentConfirmed
	) throws IOException, SQLException {
		return connector.requestResult(
			false,
			AoservProtocol.CommandID.ADD,
			new AOServConnector.ResultRequest<Integer>() {
				int transid;
				IntList invalidateList;

				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(Table.TableID.TRANSACTIONS.ordinal());
					out.writeUTF(account.getName().toString());
					out.writeUTF(sourceAccount.getName().toString());
					out.writeUTF(administrator.getUsername_userId().toString());
					out.writeUTF(type.getName());
					out.writeUTF(description);
					out.writeCompressedInt(quantity);
					MoneyUtil.writeMoney(rate, out);
					out.writeBoolean(paymentType!=null); if(paymentType!=null) out.writeUTF(paymentType.getName());
					out.writeNullUTF(paymentInfo);
					out.writeNullUTF(processor==null ? null : processor.getProviderId());
					out.writeByte(paymentConfirmed);
				}

				@Override
				public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AoservProtocol.DONE) {
						transid=in.readCompressedInt();
						invalidateList=AOServConnector.readInvalidateList(in);
					} else {
						AoservProtocol.checkResult(code, in);
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
	public Transaction get(int transid) throws IOException, SQLException {
		return getUniqueRow(Transaction.COLUMN_TRANSID, transid);
	}

	@Override
	public void clearCache() {
		// System.err.println("DEBUG: TransactionTable: clearCache() called");
		super.clearCache();
		synchronized(accountBalances) {
			accountBalances.clear();
		}
		synchronized(confirmedAccountBalances) {
			confirmedAccountBalances.clear();
		}
		synchronized(transactionBalances) {
			transactionBalances.clear();
		}
	}

	private static void addBalance(SortedMap<java.util.Currency,BigDecimal> accountBalances, Money amount) {
		java.util.Currency currency = amount.getCurrency();
		BigDecimal total = accountBalances.get(currency);
		total = (total == null) ? amount.getValue() : total.add(amount.getValue());
		accountBalances.put(currency, total);
	}

	private static void addAccountBalance(Map<Account.Name,SortedMap<java.util.Currency,BigDecimal>> balances, Account.Name account, Money amount) {
		SortedMap<java.util.Currency,BigDecimal> accountBalances = balances.get(account);
		if(accountBalances == null) {
			accountBalances = new TreeMap<>(CurrencyComparator.getInstance());
			balances.put(account, accountBalances);
		}
		addBalance(accountBalances, amount);
	}

	private static Monies toMonies(SortedMap<java.util.Currency,BigDecimal> balances) {
		List<Money> monies = MinimalList.emptyList();
		for(Map.Entry<java.util.Currency,BigDecimal> moneyEntry : balances.entrySet()) {
			monies = MinimalList.add(
				monies,
				new Money(moneyEntry.getKey(), moneyEntry.getValue())
			);
		}
		return Monies.of(monies);
	}

	public Monies getAccountBalance(Account.Name accounting) throws IOException, SQLException {
		synchronized(accountBalances) {
			if(accountBalances.isEmpty()) {
				// Compute all balances now
				Map<Account.Name,SortedMap<java.util.Currency,BigDecimal>> balances = new HashMap<>();
				for(Transaction transaction : getRows()) {
					if(transaction.getPaymentConfirmed() != Transaction.NOT_CONFIRMED) {
						addAccountBalance(balances, transaction.getAccount_name(), transaction.getAmount());
					}
				}
				// Wrap totals into unmodified lists
				for(Map.Entry<Account.Name,SortedMap<java.util.Currency,BigDecimal>> entry : balances.entrySet()) {
					accountBalances.put(entry.getKey(), toMonies(entry.getValue()));
				}
			}
			Monies balance = accountBalances.get(accounting);
			return balance == null ? Monies.of() : balance;
		}
	}

	public Monies getAccountBalance(Account.Name accounting, long before) throws IOException, SQLException {
		SortedMap<java.util.Currency,BigDecimal> balances = new TreeMap<>(CurrencyComparator.getInstance());
		for(Transaction transaction : getTransactions(accounting)) {
			if(
				transaction.getPaymentConfirmed() != Transaction.NOT_CONFIRMED
				&& transaction.getTime_millis() < before
			) {
				addBalance(balances, transaction.getAmount());
			}
		}
		return toMonies(balances);
	}

	public Monies getConfirmedAccountBalance(Account.Name accounting) throws IOException, SQLException {
		synchronized(confirmedAccountBalances) {
			if(confirmedAccountBalances.isEmpty()) {
				// Compute all balances now
				Map<Account.Name,SortedMap<java.util.Currency,BigDecimal>> balances = new HashMap<>();
				for(Transaction transaction : getRows()) {
					if(transaction.getPaymentConfirmed() == Transaction.CONFIRMED) {
						addAccountBalance(balances, transaction.getAccount_name(), transaction.getAmount());
					}
				}
				// Wrap totals into unmodified lists
				for(Map.Entry<Account.Name,SortedMap<java.util.Currency,BigDecimal>> entry : balances.entrySet()) {
					confirmedAccountBalances.put(entry.getKey(), toMonies(entry.getValue()));
				}
			}
			Monies balance = confirmedAccountBalances.get(accounting);
			return balance == null ? Monies.of() : balance;
		}
	}

	public Monies getConfirmedAccountBalance(Account.Name accounting, long before) throws IOException, SQLException {
		SortedMap<java.util.Currency,BigDecimal> balances = new TreeMap<>(CurrencyComparator.getInstance());
		for(Transaction transaction : getTransactions(accounting)) {
			if(
				transaction.getPaymentConfirmed() == Transaction.CONFIRMED
				&& transaction.getTime_millis() < before
			) {
				addBalance(balances, transaction.getAmount());
			}
		}
		return toMonies(balances);
	}

	public Monies getTransactionBalance(Transaction transaction) throws IOException, SQLException {
		synchronized(transactionBalances) {
			if(transactionBalances.isEmpty()) {
				// Compute all balances now
				Map<Account,Monies> accountBalance = new HashMap<>();
				for(Transaction trans : getRows()) {
					Account account = trans.getAccount();
					Monies balance = accountBalance.get(account);
					if(balance == null) balance = Monies.of();
					if(trans.getPaymentConfirmed() != Transaction.NOT_CONFIRMED) {
						balance.add(trans.getAmount());
					}
					accountBalance.put(account, balance);
					transactionBalances.put(trans, balance);
				}
			}
			Monies balance = transactionBalances.get(transaction);
			if(balance == null) {
				throw new SQLException("Unable to find transaction in transactionBalances: " + transaction);
			}
			return balance;
		}
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.TRANSACTIONS;
	}

	private static boolean matchesWords(String value, String words) {
		String lower = value == null ? null : value.toLowerCase(Locale.ROOT);
		for(String word : StringUtility.splitString(words)) {
			if(lower == null || !lower.contains(word.toLowerCase(Locale.ROOT))) {
				return false;
			}
		}
		return true;
	}

	public List<Transaction> get(TransactionSearchCriteria criteria) throws IOException, SQLException {
		List<Transaction> matches = new ArrayList<>();
		List<Transaction> rows;
		if(criteria.getTransid() == TransactionSearchCriteria.ANY) {
			rows = getRows();
		} else {
			Transaction row = get(criteria.getTransid());
			if(row == null) return Collections.emptyList();
			rows = Collections.singletonList(row);
		}
		for(Transaction tr : rows) {
			if(
				(
					criteria.getAfter() == TransactionSearchCriteria.ANY
					|| criteria.getAfter() <= tr.getTime_millis()
				) && (
					criteria.getBefore() == TransactionSearchCriteria.ANY
					|| criteria.getBefore() > tr.getTime_millis()
				) && (
					criteria.getPaymentConfirmed() == TransactionSearchCriteria.ANY
					|| criteria.getPaymentConfirmed() == tr.getPaymentConfirmed()
				) && (
					criteria.getAccount() == null
					|| criteria.getAccount().equals(tr.getAccount_name())
				) && (
					criteria.getSourceAccount() == null
					|| criteria.getSourceAccount().equals(tr.getSourceAccount_name())
				) && (
					criteria.getAdministrator() == null
					|| criteria.getAdministrator().equals(tr.getAdministrator_username())
				) && (
					criteria.getType() == null
					|| criteria.getType().equals(tr.getType_name())
				) && (
					criteria.getDescription() == null || criteria.getDescription().isEmpty()
					|| matchesWords(tr.getDescription(), criteria.getDescription())
				) && (
					criteria.getPaymentType() == null
					|| criteria.getPaymentType().equals(tr.getPaymentType_name())
				) && (
					criteria.getPaymentInfo() == null || criteria.getPaymentInfo().isEmpty()
					|| matchesWords(tr.getPaymentInfo(), criteria.getPaymentInfo())
				)
			) {
				matches.add(tr);
			}
		}
		return Collections.unmodifiableList(matches);
	}

	public List<Transaction> getTransactions(Account.Name accounting) throws IOException, SQLException {
		return getIndexedRows(Transaction.COLUMN_ACCOUNTING, accounting);
	}

	public List<Transaction> getTransactions(Administrator ba) throws IOException, SQLException {
		return getIndexedRows(Transaction.COLUMN_ADMINISTRATOR, ba.getUsername_userId());
	}

	@Override
	public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command = args[0];
		if(command.equalsIgnoreCase(Command.ADD_TRANSACTION)) {
			if(AOSH.checkParamCount(Command.ADD_TRANSACTION, args, 12, err)) {
				byte pc;
				if(args[12].equals("Y")) pc=Transaction.CONFIRMED;
				else if(args[12].equals("W")) pc=Transaction.WAITING_CONFIRMATION;
				else if(args[12].equals("N")) pc=Transaction.NOT_CONFIRMED;
				else throw new IllegalArgumentException("Unknown value for payment_confirmed, should be one of Y, W, or N: "+args[12]);
				out.println(
					connector.getSimpleAOClient().addTransaction(
						AOSH.parseAccountingCode(args[1], "business"),
						AOSH.parseAccountingCode(args[2], "source_business"),
						AOSH.parseUserName(args[3], "business_administrator"),
						args[4],
						args[5],
						AOSH.parseMillis(args[6], "quantity"),
						new Money(
							java.util.Currency.getInstance(args[7]),
							AOSH.parseBigDecimal(args[8], "rate")
						),
						args[9],
						args[10],
						args[11],
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
