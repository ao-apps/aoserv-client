/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018, 2019, 2020  AO Industries, Inc.
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
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.io.stream.StreamableInput;
import com.aoindustries.io.stream.StreamableOutput;
import com.aoindustries.sql.SQLUtility;
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
import java.sql.Timestamp;
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

	/**
	 * @param timeType  Either {@link Type#DATE} (rounded to the date in {@link Type#DATE_TIME_ZONE} time zone)
	 *                  or {@link Type#TIME}, which maintains up to microsecond accuracy and is time zone agnostic.
	 *
	 * @param time  The time of the transaction or {@code null} to use the current date / time per the master database clock.
	 *              The master database server is both NTP-synchronized and actively monitored, so it is best to let the master database
	 *              choose the time when "now" or "today" is desired for the transaction.
	 */
	public int add(
		final int timeType,
		final Timestamp time,
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
		if(
			timeType != Type.DATE
			&& timeType != Type.TIME
		) {
			throw new IllegalArgumentException("timeType must be either Type.DATE or Type.TIME: " + timeType);
		}
		return connector.requestResult(
			false,
			AoservProtocol.CommandID.ADD,
			new AOServConnector.ResultRequest<Integer>() {
				int transid;
				IntList invalidateList;

				@Override
				public void writeRequest(StreamableOutput out) throws IOException {
					out.writeCompressedInt(Table.TableID.TRANSACTIONS.ordinal());
					if(timeType == Type.DATE) {
						out.writeByte('D');
						// No need to send full precision, since the server will round to the date anyway
						out.writeNullLong(time == null ? null : time.getTime());
					} else if(timeType == Type.TIME) {
						out.writeByte('T');
						out.writeNullTimestamp(time);
					} else {
						throw new AssertionError("Unexpected value for timeType: " + timeType);
					}
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
				public void readResponse(StreamableInput in) throws IOException, SQLException {
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

	public Monies getAccountBalance(Account account) throws IOException, SQLException {
		if(account == null) return Monies.of();
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
			Monies balance = accountBalances.get(account.getName());
			return balance == null ? Monies.of() : balance;
		}
	}

	/**
	 * The number of milliseconds to still show a canceled account when it has a zero balance.
	 */
	private static final long SHOW_CANCELED_DURATION = 366L * 24 * 60 * 60 * 1000;

	/**
	 * Gets the active account balances for an account, including zero balances
	 * for any currency that has no transactions and is currently active in billing.
	 * A currency is active when any of the following is true:
	 * <ol>
	 * <li>Is part of the current {@link MonthlyCharge monthly charges} applied to the account (see {@link Account#getCanceled()} and {@link Account#getBillingMonthlyRate()}).</li>
	 * <li>Has any existing {@link Transaction transaction} in the currency and any of the following:
	 *   <ol type="a">
	 *   <li>Account is active (see {@link Account#getCanceled()})</li>
	 *   <li>Account canceled less than a year ago</li>
	 *   <li>Account has a non-zero balance in the currency</li>
	 *   <li>Account has a {@link Transaction transaction} in the currency within the last year</li>
	 *   </ol>
	 * </li>
	 * </ol>
	 */
	public Monies getActiveAccountBalance(Account account, long currentTime) throws IOException, SQLException {
		if(account == null) return Monies.of();

		Timestamp canceled = account.getCanceled();

		// Find all non-zero balances
		Monies accountBalance = getAccountBalance(account); // Has any existing transaction in the currency
		Monies monthlyRate;
		if(canceled == null) {
			// Account active
			monthlyRate = account.getBillingMonthlyRate();
			// Add any zero-balances for any active billing
			for(java.util.Currency currency : monthlyRate.getCurrencies()) accountBalance = accountBalance.add(new Money(currency, 0, 0));
		} else {
			// Account canceled
			monthlyRate = Monies.of();
		}

		// Find transactions when first needed
		List<Transaction> transactions = null;
		int numTransactions = 0;

		Monies activeAccountBalance = Monies.of();
		for(Money money : accountBalance) {
			boolean active;
			// Is part of the current monthly charges
			if(monthlyRate.getCurrencies().contains(money.getCurrency())) {
				active = true;
			} else {
				// and any of the following
				if(
					// Account is active
					canceled == null
					// Account canceled less than a year ago
					|| (currentTime - canceled.getTime()) <= SHOW_CANCELED_DURATION
					// Account has a non-zero balance in the currency
					|| money.getUnscaledValue() != 0
				) {
					active = true;
				} else {
					// Find transactions if not yet done
					if(transactions == null) {
						transactions = getTransactions(account);
						numTransactions = transactions.size();
					}
					// Find most recent transaction in the currency
					Transaction lastTransaction = null;
					for(int i = numTransactions - 1; i >= 0; i--) {
						Transaction transaction = transactions.get(i);
						if(
							transaction.getPaymentConfirmed() != Transaction.NOT_CONFIRMED
							&& transaction.getRate().getCurrency() == money.getCurrency()
						) {
							lastTransaction = transaction;
							break;
						}
					}
					// Account has a transaction in the currency within the last year
					active = lastTransaction != null && (currentTime - lastTransaction.getTime().getTime()) <= SHOW_CANCELED_DURATION;
				}
			}
			if(active) activeAccountBalance = activeAccountBalance.add(money);
		}
		return activeAccountBalance;
	}

	/**
	 * @see #getActiveAccountBalance(com.aoindustries.aoserv.client.account.Account, long)
	 * @see System#currentTimeMillis()
	 */
	public Monies getActiveAccountBalance(Account account) throws IOException, SQLException {
		return getActiveAccountBalance(account, System.currentTimeMillis());
	}

	public Monies getAccountBalance(Account account, Timestamp before) throws IOException, SQLException {
		if(account == null) return Monies.of();
		SortedMap<java.util.Currency,BigDecimal> balances = new TreeMap<>(CurrencyComparator.getInstance());
		for(Transaction transaction : getTransactions(account)) {
			if(
				transaction.getPaymentConfirmed() != Transaction.NOT_CONFIRMED
				&& transaction.getTime().compareTo(before) < 0
			) {
				addBalance(balances, transaction.getAmount());
			}
		}
		return toMonies(balances);
	}

	public Monies getConfirmedAccountBalance(Account account) throws IOException, SQLException {
		if(account == null) return Monies.of();
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
			Monies balance = confirmedAccountBalances.get(account.getName());
			return balance == null ? Monies.of() : balance;
		}
	}

	public Monies getConfirmedAccountBalance(Account account, Timestamp before) throws IOException, SQLException {
		if(account == null) return Monies.of();
		SortedMap<java.util.Currency,BigDecimal> balances = new TreeMap<>(CurrencyComparator.getInstance());
		for(Transaction transaction : getTransactions(account)) {
			if(
				transaction.getPaymentConfirmed() == Transaction.CONFIRMED
				&& transaction.getTime().compareTo(before) < 0
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
				Map<Account.Name,Monies> accountBalance = new HashMap<>();
				for(Transaction trans : getRows()) {
					Account.Name account = trans.getAccount_name();
					Monies balance = accountBalance.get(account);
					boolean updated = false;
					if(balance == null) {
						balance = Monies.of();
						updated = true;
					}
					if(trans.getPaymentConfirmed() != Transaction.NOT_CONFIRMED) {
						balance = balance.add(trans.getAmount());
						updated = true;
					}
					if(updated) accountBalance.put(account, balance);
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
					criteria.getAfter() == null
					|| tr.getTime().compareTo(criteria.getAfter()) >= 0
				) && (
					criteria.getBefore() == null
					|| tr.getTime().compareTo(criteria.getBefore()) < 0
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

	/**
	 * Gets transactions that have this account as their applied-to.
	 */
	public List<Transaction> getTransactions(Account account) throws IOException, SQLException {
		return getIndexedRows(Transaction.COLUMN_ACCOUNTING, account == null ? null : account.getName());
	}

	/**
	 * Gets transactions that have this account as their source.
	 */
	public List<Transaction> getTransactionsFrom(Account account) throws IOException, SQLException {
		return getIndexedRows(Transaction.COLUMN_SOURCE_ACCOUNTING, account == null ? null : account.getName());
	}

	public List<Transaction> getTransactions(Administrator ba) throws IOException, SQLException {
		return getIndexedRows(Transaction.COLUMN_ADMINISTRATOR, ba == null ? null : ba.getUsername_userId());
	}

	@Override
	public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command = args[0];
		if(command.equalsIgnoreCase(Command.BILLING_TRANSACTION_ADD)) {
			if(AOSH.checkParamCount(Command.BILLING_TRANSACTION_ADD, args, 13, err)) {
				byte pc;
				{
					String paymentConfirmed = args[13];
					if(
						paymentConfirmed.equals("Confirmed")
						// Backwards compatibility
						|| paymentConfirmed.equals("Y")
					) {
						pc = Transaction.CONFIRMED;
					} else if(
						paymentConfirmed.equals("Pending")
						// Backwards compatibility
						|| paymentConfirmed.equals("W")
					) {
						pc = Transaction.WAITING_CONFIRMATION;
					} else if(
						paymentConfirmed.equals("Failed")
						// Backwards compatibility
						|| paymentConfirmed.equals("N")
					) {
						pc = Transaction.NOT_CONFIRMED;
					} else {
						throw new IllegalArgumentException("Unknown value for payment_confirmed, should be one of \"Pending\", \"Confirmed\", or \"Failed\": " + paymentConfirmed);
					}
				}
				int timeType;
				Timestamp time;
				{
					String timeStr = args[1];
					if("now".equalsIgnoreCase(timeStr)) {
						timeType = Type.TIME;
						time = null;
					} else if("today".equalsIgnoreCase(timeStr)) {
						timeType = Type.DATE;
						time = null;
					} else if(timeStr.length() <= "YYYY-MM-DD".length()) {
						timeType = Type.DATE;
						time = SQLUtility.parseDateTime(timeStr, Type.DATE_TIME_ZONE);
					} else {
						timeType = Type.TIME;
						time = SQLUtility.parseDateTime(timeStr);
					}
				}
				out.println(
					connector.getSimpleAOClient().addTransaction(
						timeType,
						time,
						AOSH.parseAccountingCode(args[2], "business"),
						AOSH.parseAccountingCode(args[3], "source_business"),
						AOSH.parseUserName(args[4], "business_administrator"),
						args[5],
						args[6],
						AOSH.parseDecimal3(args[7], "quantity"),
						new Money(
							java.util.Currency.getInstance(args[8]),
							AOSH.parseBigDecimal(args[9], "rate")
						),
						args[10],
						args[11],
						args[12],
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
