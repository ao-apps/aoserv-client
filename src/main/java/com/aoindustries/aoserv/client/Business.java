/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2000-2013, 2016  AO Industries, Inc.
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
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.aoserv.client.validator.InetAddress;
import com.aoindustries.aoserv.client.validator.ValidationException;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.lang.ObjectUtils;
import com.aoindustries.sql.SQLUtility;
import com.aoindustries.util.IntList;
import com.aoindustries.util.InternUtils;
import com.aoindustries.util.SortedArrayList;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A <code>Business</code> is one distinct set of packages, resources, and permissions.
* Some businesses may have child businesses associated with them.  When that is the
 * case, the top level business is ultimately responsible for all actions taken and
 * resources used by itself and all child businesses.
 *
 * @author  AO Industries, Inc.
 */
final public class Business extends CachedObjectAccountingCodeKey<Business> implements Disablable, Comparable<Business> {

	static final int COLUMN_ACCOUNTING=0;
	static final String COLUMN_ACCOUNTING_name = "accounting";

	/**
	 * The maximum depth of the business tree.
	 */
	public static final int MAXIMUM_BUSINESS_TREE_DEPTH=7;

	/**
	 * The minimum payment for auto-enabling accounts, in pennies.
	 */
	public static final BigDecimal MINIMUM_PAYMENT = BigDecimal.valueOf(3000, 2);

	String contractVersion;
	private long created;

	private long canceled;

	private String cancelReason;

	AccountingCode parent;

	private boolean can_add_backup_server;
	private boolean can_add_businesses;
	private boolean can_see_prices;

	int disable_log;
	private String do_not_disable_reason;
	private boolean auto_enable;
	private boolean bill_parent;

	public int addBusinessProfile(
		String name,
		boolean isPrivate,
		String phone,
		String fax,
		String address1,
		String address2,
		String city,
		String state,
		String country,
		String zip,
		boolean sendInvoice,
		String billingContact,
		String billingEmail,
		String technicalContact,
		String technicalEmail
	) throws IOException, SQLException {
		return table.connector.getBusinessProfiles().addBusinessProfile(
			this,
			name,
			isPrivate,
			phone,
			fax,
			address1,
			address2,
			city,
			state,
			country,
			zip,
			sendInvoice,
			billingContact,
			billingEmail,
			technicalContact,
			technicalEmail
		);
	}

	public int addBusinessServer(
		Server server
	) throws IOException, SQLException {
		return table.connector.getBusinessServers().addBusinessServer(this, server);
	}

	public int addCreditCard(
		CreditCardProcessor processor,
		String groupName,
		String cardInfo,
		String providerUniqueId,
		String firstName,
		String lastName,
		String companyName,
		String email,
		String phone,
		String fax,
		String customerTaxId,
		String streetAddress1,
		String streetAddress2,
		String city,
		String state,
		String postalCode,
		CountryCode countryCode,
		String principalName,
		String description,
		String cardNumber,
		byte expirationMonth,
		short expirationYear
	) throws IOException, SQLException {
		return table.connector.getCreditCards().addCreditCard(
			processor,
			this,
			groupName,
			cardInfo,
			providerUniqueId,
			firstName,
			lastName,
			companyName,
			email,
			phone,
			fax,
			customerTaxId,
			streetAddress1,
			streetAddress2,
			city,
			state,
			postalCode,
			countryCode,
			principalName,
			description,
			cardNumber,
			expirationMonth,
			expirationYear
		);
	}

	/**
	 * Adds a transaction in the pending state.
	 */
	public int addCreditCardTransaction(
		CreditCardProcessor processor,
		String groupName,
		boolean testMode,
		int duplicateWindow,
		String orderNumber,
		String currencyCode,
		BigDecimal amount,
		BigDecimal taxAmount,
		boolean taxExempt,
		BigDecimal shippingAmount,
		BigDecimal dutyAmount,
		String shippingFirstName,
		String shippingLastName,
		String shippingCompanyName,
		String shippingStreetAddress1,
		String shippingStreetAddress2,
		String shippingCity,
		String shippingState,
		String shippingPostalCode,
		String shippingCountryCode,
		boolean emailCustomer,
		String merchantEmail,
		String invoiceNumber,
		String purchaseOrderNumber,
		String description,
		BusinessAdministrator creditCardCreatedBy,
		String creditCardPrincipalName,
		Business creditCardAccounting,
		String creditCardGroupName,
		String creditCardProviderUniqueId,
		String creditCardMaskedCardNumber,
		String creditCardFirstName,
		String creditCardLastName,
		String creditCardCompanyName,
		String creditCardEmail,
		String creditCardPhone,
		String creditCardFax,
		String creditCardCustomerTaxId,
		String creditCardStreetAddress1,
		String creditCardStreetAddress2,
		String creditCardCity,
		String creditCardState,
		String creditCardPostalCode,
		String creditCardCountryCode,
		String creditCardComments,
		long authorizationTime,
		String authorizationPrincipalName
	) throws IOException, SQLException {
		return table.connector.getCreditCardTransactions().addCreditCardTransaction(
			processor,
			this,
			groupName,
			testMode,
			duplicateWindow,
			orderNumber,
			currencyCode,
			amount,
			taxAmount,
			taxExempt,
			shippingAmount,
			dutyAmount,
			shippingFirstName,
			shippingLastName,
			shippingCompanyName,
			shippingStreetAddress1,
			shippingStreetAddress2,
			shippingCity,
			shippingState,
			shippingPostalCode,
			shippingCountryCode,
			emailCustomer,
			merchantEmail,
			invoiceNumber,
			purchaseOrderNumber,
			description,
			creditCardCreatedBy,
			creditCardPrincipalName,
			creditCardAccounting,
			creditCardGroupName,
			creditCardProviderUniqueId,
			creditCardMaskedCardNumber,
			creditCardFirstName,
			creditCardLastName,
			creditCardCompanyName,
			creditCardEmail,
			creditCardPhone,
			creditCardFax,
			creditCardCustomerTaxId,
			creditCardStreetAddress1,
			creditCardStreetAddress2,
			creditCardCity,
			creditCardState,
			creditCardPostalCode,
			creditCardCountryCode,
			creditCardComments,
			authorizationTime,
			authorizationPrincipalName
		);
	}

	public int addDisableLog(
		String disableReason
	) throws IOException, SQLException {
		return table.connector.getDisableLogs().addDisableLog(this, disableReason);
	}

	public void addNoticeLog(
		String billingContact,
		String emailAddress,
		BigDecimal balance,
		String type,
		int transid
	) throws IOException, SQLException {
		table.connector.getNoticeLogs().addNoticeLog(
			pkey,
			billingContact,
			emailAddress,
			balance,
			type,
			transid
		);
	}

	public int addPackage(
		String name,
		PackageDefinition packageDefinition
	) throws IOException, SQLException {
		return table.connector.getPackages().addPackage(
			name,
			this,
			packageDefinition
		);
	}

	public int addTransaction(
		Business sourceBusiness,
		BusinessAdministrator business_administrator,
		TransactionType type,
		String description,
		int quantity,
		int rate,
		PaymentType paymentType,
		String paymentInfo,
		CreditCardProcessor processor,
		byte payment_confirmed
	) throws IOException, SQLException {
		return table.connector.getTransactions().addTransaction(
			this,
			sourceBusiness,
			business_administrator,
			type.pkey,
			description,
			quantity,
			rate,
			paymentType,
			paymentInfo,
			processor,
			payment_confirmed
		);
	}

	public boolean canAddBackupServer() {
		return can_add_backup_server;
	}

	public boolean canAddBusinesses() {
		return can_add_businesses;
	}

	public void cancel(String cancelReason) throws IllegalArgumentException, IOException, SQLException {
		// Automatically disable if not already disabled
		if(disable_log==-1) {
			new SimpleAOClient(table.connector).disableBusiness(pkey, "Account canceled");
		}

		// Now cancel the account
		if(cancelReason!=null && (cancelReason=cancelReason.trim()).length()==0) cancelReason=null;
		final String finalCancelReason = cancelReason;
		table.connector.requestUpdate(
			true,
			new AOServConnector.UpdateRequest() {
				IntList invalidateList;

				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(AOServProtocol.CommandID.CANCEL_BUSINESS.ordinal());
					out.writeUTF(pkey.toString());
					out.writeNullUTF(finalCancelReason);
				}

				@Override
				public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AOServProtocol.DONE) invalidateList=AOServConnector.readInvalidateList(in);
					else {
						AOServProtocol.checkResult(code, in);
						throw new IOException("Unexpected response code: "+code);
					}
				}

				@Override
				public void afterRelease() {
					table.connector.tablesUpdated(invalidateList);
				}
			}
		);
	}

	public boolean canCancel() throws IOException, SQLException {
		return canceled==-1 && !isRootBusiness();
	}

	public boolean isRootBusiness() throws IOException, SQLException {
		return pkey.equals(table.connector.getBusinesses().getRootAccounting());
	}

	@Override
	public boolean canDisable() throws IOException, SQLException {
		// already disabled
		if(disable_log!=-1) return false;

		if(isRootBusiness()) return false;

		// packages
		for(Package pk : getPackages()) if(pk.disable_log==-1) return false;

		return true;
	}

	@Override
	public boolean canEnable() throws SQLException, IOException {
		// Cannot enable a canceled business
		if(canceled!=-1) return false;

		// Can only enable if it is disabled
		DisableLog dl=getDisableLog();
		if(dl==null) return false;
		else return dl.canEnable();
	}

	public boolean canSeePrices() {
		return can_see_prices;
	}

	@Override
	public void disable(DisableLog dl) throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.DISABLE, SchemaTable.TableID.BUSINESSES, dl.pkey, pkey.toString());
	}

	@Override
	public void enable() throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.ENABLE, SchemaTable.TableID.BUSINESSES, pkey.toString());
	}

	public BigDecimal getAccountBalance() throws IOException, SQLException {
		return table.connector.getTransactions().getAccountBalance(pkey);
	}

	public BigDecimal getAccountBalance(long before) throws IOException, SQLException {
		return table.connector.getTransactions().getAccountBalance(pkey, before);
	}

	/**
	 * @see  #getAccountBalance()
	 */
	public String getAccountBalanceString() throws IOException, SQLException {
		return "$"+getAccountBalance();
	}

	/**
	 * @see  #getAccountBalance(long)
	 */
	public String getAccountBalanceString(long before) throws IOException, SQLException {
		return "$"+getAccountBalance(before);
	}

	public AccountingCode getAccounting() {
		return pkey;
	}

	public boolean getAutoEnable() {
		return auto_enable;
	}

	public boolean billParent() {
		return bill_parent;
	}

	public BigDecimal getAutoEnableMinimumPayment() throws IOException, SQLException {
		BigDecimal balance=getAccountBalance();
		if(balance.signum()<0) return BigDecimal.valueOf(0, 2);
		BigDecimal minimum = balance.divide(BigDecimal.valueOf(2), RoundingMode.DOWN);
		if(minimum.compareTo(MINIMUM_PAYMENT)<0) minimum=MINIMUM_PAYMENT;
		if(minimum.compareTo(balance)>0) minimum=balance;
		return minimum;
	}

	public String getDoNotDisableReason() {
		return do_not_disable_reason;
	}

	/**
	 * Gets the <code>Business</code> in the business tree that is one level down
	 * from the top level business.
	 */
	public Business getTopLevelBusiness() throws IOException, SQLException {
		AccountingCode rootAccounting=table.connector.getBusinesses().getRootAccounting();
		Business bu=this;
		Business tempParent;
		while((tempParent=bu.getParentBusiness())!=null && !tempParent.getAccounting().equals(rootAccounting)) bu=tempParent;
		return bu;
	}

	/**
	 * Gets the <code>Business</code> the is responsible for paying the bills created by this business.
	 */
	public Business getAccountingBusiness() throws SQLException, IOException {
		Business bu=this;
		while(bu.bill_parent) {
			bu=bu.getParentBusiness();
			if(bu==null) throw new SQLException("Unable to find the accounting business for '"+pkey+'\'');
		}
		return bu;
	}

	/**
	 * Gets the <code>BusinessProfile</code> with the highest priority.
	 */
	public BusinessProfile getBusinessProfile() throws IOException, SQLException {
		return table.connector.getBusinessProfiles().getBusinessProfile(this);
	}

	/**
	 * Gets a list of all <code>BusinessProfiles</code> for this <code>Business</code>
	 * sorted with the highest priority profile at the zero index.
	 */
	public List<BusinessProfile> getBusinessProfiles() throws IOException, SQLException {
		return table.connector.getBusinessProfiles().getBusinessProfiles(this);
	}

	public BusinessServer getBusinessServer(
		Server server
	) throws IOException, SQLException {
		return table.connector.getBusinessServers().getBusinessServer(this, server);
	}

	public List<BusinessServer> getBusinessServers() throws IOException, SQLException {
		return table.connector.getBusinessServers().getBusinessServers(this);
	}

	public Timestamp getCanceled() {
		return canceled==-1 ? null : new Timestamp(canceled);
	}

	public String getCancelReason() {
		return cancelReason;
	}

	public List<Business> getChildBusinesses() throws IOException, SQLException {
		return table.connector.getBusinesses().getChildBusinesses(this);
	}

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_ACCOUNTING: return pkey;
			case 1: return contractVersion;
			case 2: return getCreated();
			case 3: return getCanceled();
			case 4: return cancelReason;
			case 5: return parent;
			case 6: return can_add_backup_server;
			case 7: return can_add_businesses;
			case 8: return can_see_prices;
			case 9: return disable_log== -1 ? null : disable_log;
			case 10: return do_not_disable_reason;
			case 11: return auto_enable;
			case 12: return bill_parent;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public BigDecimal getConfirmedAccountBalance() throws IOException, SQLException {
		return table.connector.getTransactions().getConfirmedAccountBalance(pkey);
	}

	public BigDecimal getConfirmedAccountBalance(long before) throws IOException, SQLException {
		return table.connector.getTransactions().getConfirmedAccountBalance(pkey, before);
	}

	public String getContractVersion() {
		return contractVersion;
	}

	public Timestamp getCreated() {
		return new Timestamp(created);
	}

	public List<CreditCardProcessor> getCreditCardProcessors() throws IOException, SQLException {
		return table.connector.getCreditCardProcessors().getCreditCardProcessors(this);
	}

	public List<CreditCard> getCreditCards() throws IOException, SQLException {
		return table.connector.getCreditCards().getCreditCards(this);
	}

	public Server getDefaultServer() throws IOException, SQLException {
		// May be null when the account is canceled or not using servers
		return table.connector.getBusinessServers().getDefaultServer(this);
	}

	@Override
	public boolean isDisabled() {
		return disable_log!=-1;
	}

	@Override
	public DisableLog getDisableLog() throws SQLException, IOException {
		if(disable_log==-1) return null;
		DisableLog obj=table.connector.getDisableLogs().get(disable_log);
		if(obj==null) throw new SQLException("Unable to find DisableLog: "+disable_log);
		return obj;
	}

	public List<EmailForwarding> getEmailForwarding() throws SQLException, IOException {
		return table.connector.getEmailForwardings().getEmailForwarding(this);
	}

	public List<EmailList> getEmailLists() throws IOException, SQLException {
		return table.connector.getEmailLists().getEmailLists(this);
	}

	public LinuxServerGroup getLinuxServerGroup(AOServer aoServer) throws IOException, SQLException {
		return table.connector.getLinuxServerGroups().getLinuxServerGroup(aoServer, this);
	}

	public List<LinuxAccount> getMailAccounts() throws IOException, SQLException {
		return table.connector.getLinuxAccounts().getMailAccounts(this);
	}

	public CreditCard getMonthlyCreditCard() throws IOException, SQLException {
		return table.connector.getCreditCards().getMonthlyCreditCard(this);
	}

	public List<MonthlyCharge> getMonthlyCharges() throws SQLException, IOException {
		return table.connector.getMonthlyCharges().getMonthlyCharges(this);
	}

	/**
	 * Gets an approximation of the monthly rate paid by this account.  This is not guaranteed to
	 * be exactly the same as the underlying accounting database processes.
	 */
	public BigDecimal getMonthlyRate() throws SQLException, IOException {
		BigDecimal total = BigDecimal.valueOf(0, 2);
		for(MonthlyCharge mc : getMonthlyCharges()) if(mc.isActive()) total = total.add(new BigDecimal(SQLUtility.getDecimal(mc.getPennies())));
		return total;
	}

	/**
	 * @see  #getMonthlyRate()
	 */
	public String getMonthlyRateString() throws SQLException, IOException {
		return "$"+getMonthlyRate();
	}

	public List<NoticeLog> getNoticeLogs() throws IOException, SQLException {
		return table.connector.getNoticeLogs().getNoticeLogs(this);
	}

	public List<Package> getPackages() throws IOException, SQLException {
		return table.connector.getPackages().getPackages(this);
	}

	public Business getParentBusiness() throws IOException, SQLException {
		if(parent==null) return null;
		// The parent business might not be found, even when the value is set.  This is normal due
		// to filtering.
		return table.connector.getBusinesses().get(parent);
	}

	public List<EmailDomain> getEmailDomains() throws SQLException, IOException {
		return table.connector.getEmailDomains().getEmailDomains(this);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.BUSINESSES;
	}

	/**
	 * Gets the total monthly rate or <code>null</code> if unavailable.
	 */
	public BigDecimal getTotalMonthlyRate() throws SQLException, IOException {
		BigDecimal sum = BigDecimal.valueOf(0, 2);
		for (Package pack : getPackages()) {
			BigDecimal monthlyRate = pack.getPackageDefinition().getMonthlyRate();
			if(monthlyRate==null) return null;
			sum = sum.add(monthlyRate);
		}
		return sum;
	}

	public List<Transaction> getTransactions() throws IOException, SQLException {
		return table.connector.getTransactions().getTransactions(pkey);
	}

	public List<WhoisHistory> getWhoisHistory() throws IOException, SQLException {
		return table.connector.getWhoisHistory().getWhoisHistory(this);
	}

	/**
	 * @deprecated  Please use <code>isBusinessOrParentOf</code> instead.
	 */
	public boolean isBusinessOrParent(Business other) throws IOException, SQLException {
		return isBusinessOrParentOf(other);
	}

	/**
	 * Determines if this <code>Business</code> is the other business
	 * or a parent of it.  This is often used for access control between
	 * accounts.
	 */
	public boolean isBusinessOrParentOf(Business other) throws IOException, SQLException {
		while(other!=null) {
			if(equals(other)) return true;
			other=other.getParentBusiness();
		}
		return false;
	}

	/**
	 * Determines if this <code>Business</code> is a parent of the other business.
	 * This is often used for access control between accounts.
	 */
	public boolean isParentOf(Business other) throws IOException, SQLException {
		if(other!=null) {
			other=other.getParentBusiness();
			while(other!=null) {
				if(equals(other)) return true;
				other=other.getParentBusiness();
			}
		}
		return false;
	}

	public void move(AOServer from, AOServer to, TerminalWriter out) throws IOException, SQLException {
		if(from.equals(to)) throw new SQLException("Cannot move from AOServer "+from.getHostname()+" to AOServer "+to.getHostname()+": same AOServer");

		BusinessServer fromBusinessServer=getBusinessServer(from.getServer());
		if(fromBusinessServer==null) throw new SQLException("Unable to find BusinessServer for Business="+pkey+" and Server="+from.getHostname());

		// Grant the Business access to the other server if it does not already have access
		if(out!=null) {
			out.boldOn();
			out.println("Adding Business Privileges");
			out.attributesOff();
			out.flush();
		}
		BusinessServer toBusinessServer=getBusinessServer(to.getServer());
		if(toBusinessServer==null) {
			if(out!=null) {
				out.print("    ");
				out.println(to.getHostname());
				out.flush();
			}
			addBusinessServer(to.getServer());
		}

		// Add the LinuxServerGroups
		if(out!=null) {
			out.boldOn();
			out.println("Adding Linux Groups");
			out.attributesOff();
			out.flush();
		}
		List<LinuxServerGroup> fromLinuxServerGroups=new ArrayList<>();
		List<LinuxServerGroup> toLinuxServerGroups=new SortedArrayList<>();
		{
			for(LinuxServerGroup lsg : table.connector.getLinuxServerGroups().getRows()) {
				Package pk=lsg.getLinuxGroup().getPackage();
				if(pk!=null && pk.getBusiness().equals(this)) {
					AOServer ao=lsg.getAOServer();
					if(ao.equals(from)) fromLinuxServerGroups.add(lsg);
					else if(ao.equals(to)) toLinuxServerGroups.add(lsg);
				}
			}
		}
		for (LinuxServerGroup lsg : fromLinuxServerGroups) {
			if(!toLinuxServerGroups.contains(lsg)) {
				if(out!=null) {
					out.print("    ");
					out.print(lsg.name);
					out.print(" to ");
					out.println(to.getHostname());
					out.flush();
				}
				lsg.getLinuxGroup().addLinuxServerGroup(to);
			}
		}

		// Add the LinuxServerAccounts
		if(out!=null) {
			out.boldOn();
			out.println("Adding Linux Accounts");
			out.attributesOff();
			out.flush();
		}
		List<LinuxServerAccount> fromLinuxServerAccounts=new ArrayList<>();
		List<LinuxServerAccount> toLinuxServerAccounts=new SortedArrayList<>();
		{
			List<LinuxServerAccount> lsas=table.connector.getLinuxServerAccounts().getRows();
			for (LinuxServerAccount lsa : lsas) {
				Package pk=lsa.getLinuxAccount().getUsername().getPackage();
				if(pk!=null && pk.getBusiness().equals(this)) {
					AOServer ao=lsa.getAOServer();
					if(ao.equals(from)) fromLinuxServerAccounts.add(lsa);
					else if(ao.equals(to)) toLinuxServerAccounts.add(lsa);
				}
			}
		}
		for (LinuxServerAccount lsa : fromLinuxServerAccounts) {
			if(!toLinuxServerAccounts.contains(lsa)) {
				if(out!=null) {
					out.print("    ");
					out.print(lsa.username);
					out.print(" to ");
					out.println(to.getHostname());
					out.flush();
				}
				lsa.getLinuxAccount().addLinuxServerAccount(to, lsa.getHome());
			}
		}

		// Wait for Linux Account rebuild
		if(out!=null) {
			out.boldOn();
			out.println("Waiting for Linux Account rebuild");
			out.attributesOff();
			out.print("    ");
			out.println(to.getHostname());
			out.flush();
		}
		to.waitForLinuxAccountRebuild();

		// Copy the home directory contents
		if(out!=null) {
			out.boldOn();
			out.println("Copying Home Directories");
			out.attributesOff();
			out.flush();
		}
		for (LinuxServerAccount lsa : fromLinuxServerAccounts) {
			if(!toLinuxServerAccounts.contains(lsa)) {
				if(out!=null) {
					out.print("    ");
					out.print(lsa.username);
					out.print(" to ");
					out.print(to.getHostname());
					out.print(": ");
					out.flush();
				}
				long byteCount=lsa.copyHomeDirectory(to);
				if(out!=null) {
					out.print(byteCount);
					out.println(byteCount==1?" byte":" bytes");
					out.flush();
				}
			}
		}

		// Copy the cron tables
		if(out!=null) {
			out.boldOn();
			out.println("Copying Cron Tables");
			out.attributesOff();
			out.flush();
		}
		for (LinuxServerAccount lsa : fromLinuxServerAccounts) {
			if(!toLinuxServerAccounts.contains(lsa)) {
				if(out!=null) {
					out.print("    ");
					out.print(lsa.username);
					out.print(" to ");
					out.print(to.getHostname());
					out.print(": ");
					out.flush();
				}
				String cronTable=lsa.getCronTable();
				lsa.getLinuxAccount().getLinuxServerAccount(to).setCronTable(cronTable);
				if(out!=null) {
					out.print(cronTable.length());
					out.println(cronTable.length()==1?" byte":" bytes");
					out.flush();
				}
			}
		}

		// Copy the passwords
		if(out!=null) {
			out.boldOn();
			out.println("Copying Passwords");
			out.attributesOff();
			out.flush();
		}
		for (LinuxServerAccount lsa : fromLinuxServerAccounts) {
			if(!toLinuxServerAccounts.contains(lsa)) {
				if(out!=null) {
					out.print("    ");
					out.print(lsa.username);
					out.print(" to ");
					out.println(to.getHostname());
					out.flush();
				}

				lsa.copyPassword(lsa.getLinuxAccount().getLinuxServerAccount(to));
			}
		}

		// Move IP Addresses
		if(out!=null) {
			out.boldOn();
			out.println("Moving IP Addresses");
			out.attributesOff();
			out.flush();
		}
		List<IPAddress> ips=table.connector.getIpAddresses().getRows();
		for (IPAddress ip : ips) {
			InetAddress inetAddress = ip.getInetAddress();
			if(
				ip.isAlias()
				&& !inetAddress.isUnspecified()
				&& !ip.getNetDevice().getNetDeviceID().isLoopback()
				&& ip.getPackage().accounting.equals(pkey)
			) {
				if(out!=null) {
					out.print("    ");
					out.println(ip);
				}
				ip.moveTo(to.getServer());
			}
		} // TODO: Continue development here



		// Remove the LinuxServerAccounts
		if(out!=null) {
			out.boldOn();
			out.println("Removing Linux Accounts");
			out.attributesOff();
			out.flush();
		}
		for (LinuxServerAccount lsa : fromLinuxServerAccounts) {
			if(out!=null) {
				out.print("    ");
				out.print(lsa.username);
				out.print(" on ");
				out.println(from.getHostname());
				out.flush();
			}
			lsa.remove();
		}

		// Remove the LinuxServerGroups
		if(out!=null) {
			out.boldOn();
			out.println("Removing Linux Groups");
			out.attributesOff();
			out.flush();
		}
		for (LinuxServerGroup lsg : fromLinuxServerGroups) {
			if(out!=null) {
				out.print("    ");
				out.print(lsg.name);
				out.print(" on ");
				out.println(from.getHostname());
				out.flush();
			}
			lsg.remove();
		}

		// Remove access to the old server
		if(out!=null) {
			out.boldOn();
			out.println("Removing Business Privileges");
			out.attributesOff();
			out.print("    ");
			out.println(from.getHostname());
			out.flush();
		}
		fromBusinessServer.remove();
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			pkey = AccountingCode.valueOf(result.getString(1));
			contractVersion = result.getString(2);
			created = result.getTimestamp(3).getTime();
			Timestamp T = result.getTimestamp(4);
			canceled = T==null ? -1 : T.getTime();
			cancelReason = result.getString(5);
			parent = AccountingCode.valueOf(result.getString(6));
			can_add_backup_server=result.getBoolean(7);
			can_add_businesses=result.getBoolean(8);
			can_see_prices=result.getBoolean(9);
			disable_log=result.getInt(10);
			if(result.wasNull()) disable_log=-1;
			do_not_disable_reason=result.getString(11);
			auto_enable=result.getBoolean(12);
			bill_parent=result.getBoolean(13);
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			pkey=AccountingCode.valueOf(in.readUTF()).intern();
			contractVersion=InternUtils.intern(in.readNullUTF());
			created=in.readLong();
			canceled=in.readLong();
			cancelReason=in.readNullUTF();
			parent=InternUtils.intern(AccountingCode.valueOf(in.readNullUTF()));
			can_add_backup_server=in.readBoolean();
			can_add_businesses=in.readBoolean();
			can_see_prices=in.readBoolean();
			disable_log=in.readCompressedInt();
			do_not_disable_reason=in.readNullUTF();
			auto_enable=in.readBoolean();
			bill_parent=in.readBoolean();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	public void setAccounting(AccountingCode accounting) throws SQLException, IOException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.SET_BUSINESS_ACCOUNTING, this.pkey.toString(), accounting.toString());
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeUTF(pkey.toString());
		out.writeBoolean(contractVersion!=null); if(contractVersion!=null) out.writeUTF(contractVersion);
		out.writeLong(created);
		out.writeLong(canceled);
		out.writeNullUTF(cancelReason);
		out.writeNullUTF(ObjectUtils.toString(parent));
		if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_102)>=0) out.writeBoolean(can_add_backup_server);
		out.writeBoolean(can_add_businesses);
		if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_122)<=0) out.writeBoolean(false);
		if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_103)>=0) out.writeBoolean(can_see_prices);
		out.writeCompressedInt(disable_log);
		out.writeNullUTF(do_not_disable_reason);
		out.writeBoolean(auto_enable);
		out.writeBoolean(bill_parent);
	}

	public List<Ticket> getTickets() throws SQLException, IOException {
		return table.connector.getTickets().getTickets(this);
	}

	/**
	 * Gets all of the encryption keys for this business.
	 */
	public List<EncryptionKey> getEncryptionKeys() throws IOException, SQLException {
		return table.connector.getEncryptionKeys().getEncryptionKeys(this);
	}

	/**
	 * Sets the credit card that will be used monthly.  Any other selected card will
	 * be deselected.  If <code>creditCard</code> is null, none will be used automatically.
	 */
	public void setUseMonthlyCreditCard(CreditCard creditCard) throws IOException, SQLException {
		table.connector.requestUpdateIL(
			true,
			AOServProtocol.CommandID.SET_CREDIT_CARD_USE_MONTHLY,
			pkey.toString(),
			creditCard==null ? -1 : creditCard.getPkey()
		);
	}

	/**
	 * Gets the most recent credit card transaction.
	 */
	public CreditCardTransaction getLastCreditCardTransaction() throws IOException, SQLException {
		return table.connector.getCreditCardTransactions().getLastCreditCardTransaction(this);
	}

	/**
	 * Gets the Brand for this business or <code>null</code> if not a brand.
	 */
	public Brand getBrand() throws IOException, SQLException {
		return table.connector.getBrands().getBrand(this);
	}

	public int addPackageDefinition(
		PackageCategory category,
		String name,
		String version,
		String display,
		String description,
		int setupFee,
		TransactionType setupFeeTransactionType,
		int monthlyRate,
		TransactionType monthlyRateTransactionType
	) throws IOException, SQLException {
		return table.connector.getPackageDefinitions().addPackageDefinition(
			this,
			category,
			name,
			version,
			display,
			description,
			setupFee,
			setupFeeTransactionType,
			monthlyRate,
			monthlyRateTransactionType
		);
	}

	public PackageDefinition getPackageDefinition(PackageCategory category, String name, String version) throws IOException, SQLException {
		return table.connector.getPackageDefinitions().getPackageDefinition(this, category, name, version);
	}

	public List<PackageDefinition> getPackageDefinitions(PackageCategory category) throws IOException, SQLException {
		return table.connector.getPackageDefinitions().getPackageDefinitions(this, category);
	}

	/**
	 * Gets all active package definitions for this business.
	 */
	public Map<PackageCategory,List<PackageDefinition>> getActivePackageDefinitions() throws IOException, SQLException {
		// Determine the active packages per category
		List<PackageCategory> allCategories = table.connector.getPackageCategories().getRows();
		Map<PackageCategory,List<PackageDefinition>> categories = new LinkedHashMap<>(allCategories.size()*4/3+1);
		for(PackageCategory category : allCategories) {
			List<PackageDefinition> allDefinitions = getPackageDefinitions(category);
			List<PackageDefinition> definitions = new ArrayList<>(allDefinitions.size());
			for(PackageDefinition definition : allDefinitions) {
				if(definition.isActive()) definitions.add(definition);
			}
			if(!definitions.isEmpty()) categories.put(category, Collections.unmodifiableList(definitions));
		}
		return Collections.unmodifiableMap(categories);
	}

	@Override
	public int compareTo(Business o) {
		return pkey.compareTo(o.pkey);
	}
}
