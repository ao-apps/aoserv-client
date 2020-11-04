/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2007-2013, 2016, 2017, 2018, 2019, 2020  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.payment;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.account.Administrator;
import com.aoindustries.aoserv.client.account.User;
import com.aoindustries.aoserv.client.billing.MoneyUtil;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.collections.IntList;
import com.aoindustries.io.stream.StreamableInput;
import com.aoindustries.io.stream.StreamableOutput;
import com.aoindustries.math.SafeMath;
import com.aoindustries.net.Email;
import com.aoindustries.sql.SQLStreamables;
import com.aoindustries.sql.UnmodifiableTimestamp;
import com.aoindustries.util.InternUtils;
import com.aoindustries.util.i18n.Money;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Currency;
import java.util.Objects;

/**
 * A <code>CreditCardTransaction</code> stores the complete history of credit card transactions.
 *
 * @author  AO Industries, Inc.
 */
final public class Payment extends CachedObjectIntegerKey<Payment> {

	static final int
		COLUMN_PKEY = 0,
		COLUMN_PROCESSOR_ID = 1
	;
	static final String COLUMN_ACCOUNTING_name = "accounting";
	static final String COLUMN_AUTHORIZATION_TIME_name = "authorization_time";
	static final String COLUMN_PKEY_name = "pkey";

	private String processorId;
	private Account.Name accounting;
	private String groupName;
	private boolean testMode;
	private int duplicateWindow;
	private String orderNumber;
	private Money amount;
	private Money taxAmount;
	private boolean taxExempt;
	private Money shippingAmount;
	private Money dutyAmount;
	private String shippingFirstName;
	private String shippingLastName;
	private String shippingCompanyName;
	private String shippingStreetAddress1;
	private String shippingStreetAddress2;
	private String shippingCity;
	private String shippingState;
	private String shippingPostalCode;
	private String shippingCountryCode;
	private boolean emailCustomer;
	private Email merchantEmail;
	private String invoiceNumber;
	private String purchaseOrderNumber;
	private String description;
	private User.Name creditCardCreatedBy;
	private String creditCardPrincipalName;
	private Account.Name creditCardAccounting;
	private String creditCardGroupName;
	private String creditCardProviderUniqueId;
	private String creditCardMaskedCardNumber;
	private Byte creditCard_expirationMonth;
	private Short creditCard_expirationYear;
	private String creditCardFirstName;
	private String creditCardLastName;
	private String creditCardCompanyName;
	private Email creditCardEmail;
	private String creditCardPhone;
	private String creditCardFax;
	private String creditCardCustomerId;
	private String creditCardCustomerTaxId;
	private String creditCardStreetAddress1;
	private String creditCardStreetAddress2;
	private String creditCardCity;
	private String creditCardState;
	private String creditCardPostalCode;
	private String creditCardCountryCode;
	private String creditCardComments;
	private UnmodifiableTimestamp authorizationTime;
	private User.Name authorizationUsername;
	private String authorizationPrincipalName;
	// TODO: enum com.aoindustries.creditcards.TransactionResult.CommunicationResult
	private String authorizationCommunicationResult;
	private String authorizationProviderErrorCode;
	// TODO: enum com.aoindustries.creditcards.TransactionResult.ErrorCode
	private String authorizationErrorCode;
	private String authorizationProviderErrorMessage;
	private String authorizationProviderUniqueId;
	private String authorizationResult_providerReplacementMaskedCardNumber;
	private String authorizationResult_replacementMaskedCardNumber;
	private String authorizationResult_providerReplacementExpiration;
	private Byte authorizationResult_replacementExpirationMonth;
	private Short authorizationResult_replacementExpirationYear;
	private String authorizationProviderApprovalResult;
	// TODO: enum com.aoindustries.creditcards.AuthorizationResult.ApprovalResult
	private String authorizationApprovalResult;
	private String authorizationProviderDeclineReason;
	// TODO: enum com.aoindustries.creditcards.AuthorizationResult.DeclineReason
	private String authorizationDeclineReason;
	private String authorizationProviderReviewReason;
	// TODO: enum com.aoindustries.creditcards.AuthorizationResult.ReviewReason
	private String authorizationReviewReason;
	private String authorizationProviderCvvResult;
	// TODO: enum com.aoindustries.creditcards.AuthorizationResult.CvvResult
	private String authorizationCvvResult;
	private String authorizationProviderAvsResult;
	// TODO: enum com.aoindustries.creditcards.AuthorizationResult.AvsResult
	private String authorizationAvsResult;
	private String authorizationApprovalCode;
	private UnmodifiableTimestamp captureTime;
	private User.Name captureUsername;
	private String capturePrincipalName;
	// TODO: enum com.aoindustries.creditcards.TransactionResult.CommunicationResult
	private String captureCommunicationResult;
	private String captureProviderErrorCode;
	// TODO: enum com.aoindustries.creditcards.TransactionResult.ErrorCode
	private String captureErrorCode;
	private String captureProviderErrorMessage;
	private String captureProviderUniqueId;
	private UnmodifiableTimestamp voidTime;
	private User.Name voidUsername;
	private String voidPrincipalName;
	// TODO: enum com.aoindustries.creditcards.TransactionResult.CommunicationResult
	private String voidCommunicationResult;
	private String voidProviderErrorCode;
	// TODO: enum com.aoindustries.creditcards.TransactionResult.ErrorCode
	private String voidErrorCode;
	private String voidProviderErrorMessage;
	private String voidProviderUniqueId;
	// TODO: enum com.aoindustries.creditcards.Transaction.Status
	// TODO: Use enum directly?  Wait until we have modular aoserv-client by schema, then pick it up as a dependency of aoserv-client-payment only?
	private String status;

	/**
	 * Gets the credit card processor used for this transaction.
	 */
	public Processor getCreditCardProcessor() throws SQLException, IOException {
		Processor ccp = table.getConnector().getPayment().getProcessor().get(processorId);
		if(ccp==null) throw new SQLException("Unable to find CreditCardProcessor: "+processorId);
		return ccp;
	}

	public Account.Name getAccount_name() {
		return accounting;
	}

	/**
	 * For AOServ sub-account support, this is the account that is making the payment.
	 * For application-only use (not a sub-account to parent-account payment), use the same account
	 * as the owner of the credit card processor.
	 */
	public Account getAccount() throws SQLException, IOException {
		Account account = table.getConnector().getAccount().getAccount().get(accounting);
		if(account == null) throw new SQLException("Unable to find Account: " + accounting);
		return account;
	}

	/**
	 * Gets the group name for this transaction.  This is an arbitrary accounting name or grouping level.
	 */
	public String getGroupName() {
		return groupName;
	}

	/**
	 * Gets the test mode flag.
	 */
	public boolean getTestMode() {
		return testMode;
	}

	/**
	 * Gets the duplicate detection window in seconds.
	 */
	public int getDuplicateWindow() {
		return duplicateWindow;
	}

	/**
	 * Gets the order number.
	 */
	public String getOrderNumber() {
		return orderNumber;
	}

	/**
	 * Gets the amount of the transaction.  This amount should not include any tax, shipping charges, or duty.
	 * Thus the total amount of the transaction is the amount + taxAmount + shippingAmount + dutyAmount.
	 */
	public Money getAmount() {
		return amount;
	}

	/**
	 * Gets the optional tax amount of the transaction.
	 */
	public Money getTaxAmount() {
		return taxAmount;
	}

	/**
	 * Gets the taxExempt flag for this transaction.
	 */
	public boolean getTaxExempt() {
		return taxExempt;
	}

	/**
	 * Gets the optional shipping amount for this transaction.
	 */
	public Money getShippingAmount() {
		return shippingAmount;
	}

	/**
	 * Gets the optional duty amount for this transaction.
	 */
	public Money getDutyAmount() {
		return dutyAmount;
	}

	public String getShippingFirstName() {
		return shippingFirstName;
	}

	public String getShippingLastName() {
		return shippingLastName;
	}

	public String getShippingCompanyName() {
		return shippingCompanyName;
	}

	public String getShippingStreetAddress1() {
		return shippingStreetAddress1;
	}

	public String getShippingStreetAddress2() {
		return shippingStreetAddress2;
	}

	public String getShippingCity() {
		return shippingCity;
	}

	public String getShippingState() {
		return shippingState;
	}

	public String getShippingPostalCode() {
		return shippingPostalCode;
	}

	/**
	 * Gets the shipping two-digit ISO 3166-1 alpha-2 country code.
	 * <p>
	 * See <a href="https://wikipedia.org/wiki/ISO_3166-1_alpha-2">https://wikipedia.org/wiki/ISO_3166-1_alpha-2</a>
	 * </p>
	 */
	public CountryCode getShippingCountryCode() throws SQLException, IOException {
		if(shippingCountryCode==null) return null;
		CountryCode cc = table.getConnector().getPayment().getCountryCode().get(shippingCountryCode);
		if(cc==null) throw new SQLException("Unable to find CountryCode: "+shippingCountryCode);
		return cc;
	}

	public boolean getEmailCustomer() {
		return emailCustomer;
	}

	public Email getMerchantEmail() {
		return merchantEmail;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public String getPurchaseOrderNumber() {
		return purchaseOrderNumber;
	}

	public Administrator getCreditCardCreatedBy() throws SQLException, IOException {
		Administrator administrator = table.getConnector().getAccount().getAdministrator().get(creditCardCreatedBy);
		if (administrator == null) throw new SQLException("Unable to find Administrator: " + creditCardCreatedBy);
		return administrator;
	}

	/**
	 * Gets the application-provided principal who added this credit card.
	 */
	public String getCreditCardPrincipalName() {
		return creditCardPrincipalName;
	}

	public Account getCreditCardAccount() throws SQLException, IOException {
		Account business = table.getConnector().getAccount().getAccount().get(creditCardAccounting);
		if (business == null) throw new SQLException("Unable to find Account: " + creditCardAccounting);
		return business;
	}

	public String getCreditCardGroupName() {
		return creditCardGroupName;
	}

	public String getCreditCardProviderUniqueId() {
		return creditCardProviderUniqueId;
	}

	public String getCreditCardMaskedCardNumber() {
		return creditCardMaskedCardNumber;
	}

	public Byte getCreditCard_expirationMonth() {
		return creditCard_expirationMonth;
	}

	public Short getCreditCard_expirationYear() {
		return creditCard_expirationYear;
	}

	public String getCreditCardFirstName() {
		return creditCardFirstName;
	}

	public String getCreditCardLastName() {
		return creditCardLastName;
	}

	public String getCreditCardCompanyName() {
		return creditCardCompanyName;
	}

	public Email getCreditCardEmail() {
		return creditCardEmail;
	}

	public String getCreditCardPhone() {
		return creditCardPhone;
	}

	public String getCreditCardFax() {
		return creditCardFax;
	}

	public String getCreditCardCustomerId() {
		return creditCardCustomerId;
	}

	public String getCreditCardCustomerTaxId() {
		return creditCardCustomerTaxId;
	}

	public String getCreditCardStreetAddress1() {
		return creditCardStreetAddress1;
	}

	public String getCreditCardStreetAddress2() {
		return creditCardStreetAddress2;
	}

	public String getCreditCardCity() {
		return creditCardCity;
	}

	public String getCreditCardState() {
		return creditCardState;
	}

	public String getCreditCardPostalCode() {
		return creditCardPostalCode;
	}

	/**
	 * Gets the credit card two-digit ISO 3166-1 alpha-2 country code.
	 * <p>
	 * See <a href="https://wikipedia.org/wiki/ISO_3166-1_alpha-2">https://wikipedia.org/wiki/ISO_3166-1_alpha-2</a>
	 * </p>
	 */
	public CountryCode getCreditCardCountryCode() throws SQLException, IOException {
		CountryCode cc = table.getConnector().getPayment().getCountryCode().get(creditCardCountryCode);
		if(cc==null) throw new SQLException("Unable to find CountryCode: "+creditCardCountryCode);
		return cc;
	}

	public String getCreditCardComments() {
		return creditCardComments;
	}

	/**
	 * Gets the time of the authorization if not available.
	 */
	@SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
	public UnmodifiableTimestamp getAuthorizationTime() {
		return authorizationTime;
	}

	/**
	 * Gets the {@link Administrator} who authorized this transactions.  This is the
	 * username of the account that has access to control credit card transactions.
	 */
	public Administrator getAuthorizationAdministrator() throws SQLException, IOException {
		if(authorizationUsername==null) return null;
		Administrator administrator = table.getConnector().getAccount().getAdministrator().get(authorizationUsername);
		if (administrator == null) throw new SQLException("Unable to find Administrator: " + authorizationUsername);
		return administrator;
	}

	/**
	 * Gets the application-specific username who authorized this transaction.  For pure-AOServ data, this
	 * will contain the username of the {@link Administrator} who was logged in and made the payment.
	 */
	public String getAuthorizationPrincipalName() {
		return authorizationPrincipalName;
	}

	public String getAuthorizationCommunicationResult() {
		return authorizationCommunicationResult;
	}

	public String getAuthorizationProviderErrorCode() {
		return authorizationProviderErrorCode;
	}

	public String getAuthorizationErrorCode() {
		return authorizationErrorCode;
	}

	public String getAuthorizationProviderErrorMessage() {
		return authorizationProviderErrorMessage;
	}

	public String getAuthorizationProviderUniqueId() {
		return authorizationProviderUniqueId;
	}

	public String getAuthorizationProviderReplacementMaskedCardNumber() {
		return authorizationResult_providerReplacementMaskedCardNumber;
	}

	public String getAuthorizationReplacementMaskedCardNumber() {
		return authorizationResult_replacementMaskedCardNumber;
	}

	public String getAuthorizationProviderReplacementExpiration() {
		return authorizationResult_providerReplacementExpiration;
	}

	public Byte getAuthorizationReplacementExpirationMonth() {
		return authorizationResult_replacementExpirationMonth;
	}

	public Short getAuthorizationReplacementExpirationYear() {
		return authorizationResult_replacementExpirationYear;
	}

	public String getAuthorizationProviderApprovalResult() {
		return authorizationProviderApprovalResult;
	}

	public String getAuthorizationApprovalResult() {
		return authorizationApprovalResult;
	}

	public String getAuthorizationProviderDeclineReason() {
		return authorizationProviderDeclineReason;
	}

	public String getAuthorizationDeclineReason() {
		return authorizationDeclineReason;
	}

	public String getAuthorizationProviderReviewReason() {
		return authorizationProviderReviewReason;
	}

	public String getAuthorizationReviewReason() {
		return authorizationReviewReason;
	}

	public String getAuthorizationProviderCvvResult() {
		return authorizationProviderCvvResult;
	}

	public String getAuthorizationCvvResult() {
		return authorizationCvvResult;
	}

	public String getAuthorizationProviderAvsResult() {
		return authorizationProviderAvsResult;
	}

	public String getAuthorizationAvsResult() {
		return authorizationAvsResult;
	}

	public String getAuthorizationApprovalCode() {
		return authorizationApprovalCode;
	}

	/**
	 * Gets the time of the capture.
	 */
	@SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
	public UnmodifiableTimestamp getCaptureTime() {
		return captureTime;
	}

	/**
	 * Gets the {@link Administrator} who captured this transactions.  This is the
	 * username of the account that has access to control credit card transactions.
	 */
	public Administrator getCaptureAdministrator() throws SQLException, IOException {
		if(captureUsername==null) return null;
		Administrator administrator = table.getConnector().getAccount().getAdministrator().get(captureUsername);
		if (administrator == null) throw new SQLException("Unable to find Administrator: " + captureUsername);
		return administrator;
	}

	/**
	 * Gets the application-specific username who captured this transaction.  For pure-AOServ data, this
	 * will contain the username of the {@link Administrator} who was logged in and initiated the capture.
	 */
	public String getCapturePrincipalName() {
		return capturePrincipalName;
	}

	public String getCaptureCommunicationResult() {
		return captureCommunicationResult;
	}

	public String getCaptureProviderErrorCode() {
		return captureProviderErrorCode;
	}

	public String getCaptureErrorCode() {
		return captureErrorCode;
	}

	public String getCaptureProviderErrorMessage() {
		return captureProviderErrorMessage;
	}

	public String getCaptureProviderUniqueId() {
		return captureProviderUniqueId;
	}

	/**
	 * Gets the time of the void.
	 */
	@SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
	public UnmodifiableTimestamp getVoidTime() {
		return voidTime;
	}

	/**
	 * Gets the {@link Administrator} who voided this transactions.  This is the
	 * username of the account that has access to control credit card transactions.
	 */
	public Administrator getVoidAdministrator() throws SQLException, IOException {
		if(voidUsername==null) return null;
		Administrator administrator = table.getConnector().getAccount().getAdministrator().get(voidUsername);
		if (administrator == null) throw new SQLException("Unable to find Administrator: " + voidUsername);
		return administrator;
	}

	/**
	 * Gets the application-specific username who voided this transaction.  For pure-AOServ data, this
	 * will contain the username of the {@link Administrator} who was logged in and caused the void.
	 */
	public String getVoidPrincipalName() {
		return voidPrincipalName;
	}

	public String getVoidCommunicationResult() {
		return voidCommunicationResult;
	}

	public String getVoidProviderErrorCode() {
		return voidProviderErrorCode;
	}

	public String getVoidErrorCode() {
		return voidErrorCode;
	}

	public String getVoidProviderErrorMessage() {
		return voidProviderErrorMessage;
	}

	public String getVoidProviderUniqueId() {
		return voidProviderUniqueId;
	}

	public String getStatus() {
		return status;
	}

	public String getDescription() {
	return description;
	}

	@Override
	@SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_PROCESSOR_ID: return processorId;
			case 2: return accounting;
			case 3: return groupName;
			case 4: return testMode;
			case 5: return duplicateWindow;
			case 6: return orderNumber;
			case 7: return amount;
			case 8: return taxAmount;
			case 9: return taxExempt;
			case 10: return shippingAmount;
			case 11: return dutyAmount;
			case 12: return shippingFirstName;
			case 13: return shippingLastName;
			case 14: return shippingCompanyName;
			case 15: return shippingStreetAddress1;
			case 16: return shippingStreetAddress2;
			case 17: return shippingCity;
			case 18: return shippingState;
			case 19: return shippingPostalCode;
			case 20: return shippingCountryCode;
			case 21: return emailCustomer;
			case 22: return merchantEmail;
			case 23: return invoiceNumber;
			case 24: return purchaseOrderNumber;
			case 25: return description;
			case 26: return creditCardCreatedBy;
			case 27: return creditCardPrincipalName;
			case 28: return creditCardAccounting;
			case 29: return creditCardGroupName;
			case 30: return creditCardProviderUniqueId;
			case 31: return creditCardMaskedCardNumber;
			case 32: return creditCard_expirationMonth == null ? null : creditCard_expirationMonth.shortValue(); // TODO: Add "byte" type back to AOServ?
			case 33: return creditCard_expirationYear;
			case 34: return creditCardFirstName;
			case 35: return creditCardLastName;
			case 36: return creditCardCompanyName;
			case 37: return creditCardEmail;
			case 38: return creditCardPhone;
			case 39: return creditCardFax;
			case 40: return creditCardCustomerId;
			case 41: return creditCardCustomerTaxId;
			case 42: return creditCardStreetAddress1;
			case 43: return creditCardStreetAddress2;
			case 44: return creditCardCity;
			case 45: return creditCardState;
			case 46: return creditCardPostalCode;
			case 47: return creditCardCountryCode;
			case 48: return creditCardComments;
			case 49: return authorizationTime;
			case 50: return authorizationUsername;
			case 51: return authorizationPrincipalName;
			case 52: return authorizationCommunicationResult;
			case 53: return authorizationProviderErrorCode;
			case 54: return authorizationErrorCode;
			case 55: return authorizationProviderErrorMessage;
			case 56: return authorizationProviderUniqueId;
			case 57: return authorizationResult_providerReplacementMaskedCardNumber;
			case 58: return authorizationResult_replacementMaskedCardNumber;
			case 59: return authorizationResult_providerReplacementExpiration;
			case 60: return authorizationResult_replacementExpirationMonth == null ? null : authorizationResult_replacementExpirationMonth.shortValue(); // TODO: Add "byte" type back to AOServ?
			case 61: return authorizationResult_replacementExpirationYear;
			case 62: return authorizationProviderApprovalResult;
			case 63: return authorizationApprovalResult;
			case 64: return authorizationProviderDeclineReason;
			case 65: return authorizationDeclineReason;
			case 66: return authorizationProviderReviewReason;
			case 67: return authorizationReviewReason;
			case 68: return authorizationProviderCvvResult;
			case 69: return authorizationCvvResult;
			case 70: return authorizationProviderAvsResult;
			case 71: return authorizationAvsResult;
			case 72: return authorizationApprovalCode;
			case 73: return captureTime;
			case 74: return captureUsername;
			case 75: return capturePrincipalName;
			case 76: return captureCommunicationResult;
			case 77: return captureProviderErrorCode;
			case 78: return captureErrorCode;
			case 79: return captureProviderErrorMessage;
			case 80: return captureProviderUniqueId;
			case 81: return voidTime;
			case 82: return voidUsername;
			case 83: return voidPrincipalName;
			case 84: return voidCommunicationResult;
			case 85: return voidProviderErrorCode;
			case 86: return voidErrorCode;
			case 87: return voidProviderErrorMessage;
			case 88: return voidProviderUniqueId;
			case 89: return status;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.CREDIT_CARD_TRANSACTIONS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			int pos = 1;
			pkey = result.getInt(pos++);
			processorId = result.getString(pos++);
			accounting = Account.Name.valueOf(result.getString(pos++));
			groupName = result.getString(pos++);
			testMode = result.getBoolean(pos++);
			duplicateWindow = result.getInt(pos++);
			orderNumber = result.getString(pos++);
			Currency currency = Currency.getInstance(result.getString(pos++));
			amount = new Money(currency, result.getBigDecimal(pos++));
			taxAmount = MoneyUtil.getMoney(currency, result.getBigDecimal(pos++));
			taxExempt = result.getBoolean(pos++);
			shippingAmount = MoneyUtil.getMoney(currency, result.getBigDecimal(pos++));
			dutyAmount = MoneyUtil.getMoney(currency, result.getBigDecimal(pos++));
			shippingFirstName = result.getString(pos++);
			shippingLastName = result.getString(pos++);
			shippingCompanyName = result.getString(pos++);
			shippingStreetAddress1 = result.getString(pos++);
			shippingStreetAddress2 = result.getString(pos++);
			shippingCity = result.getString(pos++);
			shippingState = result.getString(pos++);
			shippingPostalCode = result.getString(pos++);
			shippingCountryCode = result.getString(pos++);
			emailCustomer = result.getBoolean(pos++);
			merchantEmail = Email.valueOf(result.getString(pos++));
			invoiceNumber = result.getString(pos++);
			purchaseOrderNumber = result.getString(pos++);
			description = result.getString(pos++);
			creditCardCreatedBy = User.Name.valueOf(result.getString(pos++));
			creditCardPrincipalName = result.getString(pos++);
			creditCardAccounting = Account.Name.valueOf(result.getString(pos++));
			creditCardGroupName = result.getString(pos++);
			creditCardProviderUniqueId = result.getString(pos++);
			creditCardMaskedCardNumber = result.getString(pos++);
			creditCard_expirationMonth = SafeMath.castByte(result.getShort(pos++));
			if(result.wasNull()) creditCard_expirationMonth = null;
			creditCard_expirationYear = result.getShort(pos++);
			if(result.wasNull()) creditCard_expirationYear = null;
			creditCardFirstName = result.getString(pos++);
			creditCardLastName = result.getString(pos++);
			creditCardCompanyName = result.getString(pos++);
			creditCardEmail = Email.valueOf(result.getString(pos++));
			creditCardPhone = result.getString(pos++);
			creditCardFax = result.getString(pos++);
			creditCardCustomerId = result.getString(pos++);
			creditCardCustomerTaxId = result.getString(pos++);
			creditCardStreetAddress1 = result.getString(pos++);
			creditCardStreetAddress2 = result.getString(pos++);
			creditCardCity = result.getString(pos++);
			creditCardState = result.getString(pos++);
			creditCardPostalCode = result.getString(pos++);
			creditCardCountryCode = result.getString(pos++);
			creditCardComments = result.getString(pos++);
			authorizationTime = UnmodifiableTimestamp.valueOf(result.getTimestamp(pos++));
			authorizationUsername = User.Name.valueOf(result.getString(pos++));
			authorizationPrincipalName = result.getString(pos++);
			authorizationCommunicationResult = result.getString(pos++);
			authorizationProviderErrorCode = result.getString(pos++);
			authorizationErrorCode = result.getString(pos++);
			authorizationProviderErrorMessage = result.getString(pos++);
			authorizationProviderUniqueId = result.getString(pos++);
			authorizationResult_providerReplacementMaskedCardNumber = result.getString(pos++);
			authorizationResult_replacementMaskedCardNumber = result.getString(pos++);
			authorizationResult_providerReplacementExpiration = result.getString(pos++);
			authorizationResult_replacementExpirationMonth = SafeMath.castByte(result.getShort(pos++));
			if(result.wasNull()) authorizationResult_replacementExpirationMonth = null;
			authorizationResult_replacementExpirationYear = result.getShort(pos++);
			if(result.wasNull()) authorizationResult_replacementExpirationYear = null;
			authorizationProviderApprovalResult = result.getString(pos++);
			authorizationApprovalResult = result.getString(pos++);
			authorizationProviderDeclineReason = result.getString(pos++);
			authorizationDeclineReason = result.getString(pos++);
			authorizationProviderReviewReason = result.getString(pos++);
			authorizationReviewReason = result.getString(pos++);
			authorizationProviderCvvResult = result.getString(pos++);
			authorizationCvvResult = result.getString(pos++);
			authorizationProviderAvsResult = result.getString(pos++);
			authorizationAvsResult = result.getString(pos++);
			authorizationApprovalCode = result.getString(pos++);
			captureTime = UnmodifiableTimestamp.valueOf(result.getTimestamp(pos++));
			captureUsername = User.Name.valueOf(result.getString(pos++));
			capturePrincipalName = result.getString(pos++);
			captureCommunicationResult = result.getString(pos++);
			captureProviderErrorCode = result.getString(pos++);
			captureErrorCode = result.getString(pos++);
			captureProviderErrorMessage = result.getString(pos++);
			captureProviderUniqueId = result.getString(pos++);
			voidTime = UnmodifiableTimestamp.valueOf(result.getTimestamp(pos++));
			voidUsername = User.Name.valueOf(result.getString(pos++));
			voidPrincipalName = result.getString(pos++);
			voidCommunicationResult = result.getString(pos++);
			voidProviderErrorCode = result.getString(pos++);
			voidErrorCode = result.getString(pos++);
			voidProviderErrorMessage = result.getString(pos++);
			voidProviderUniqueId = result.getString(pos++);
			status = result.getString(pos++);
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
		try {
			pkey = in.readCompressedInt();
			processorId = in.readUTF().intern();
			accounting = Account.Name.valueOf(in.readUTF()).intern();
			groupName = in.readNullUTF();
			testMode = in.readBoolean();
			duplicateWindow = in.readCompressedInt();
			orderNumber = in.readNullUTF();
			Currency currency = Currency.getInstance(in.readUTF());
			amount = new Money(currency, in.readLong(), in.readCompressedInt());
			if(in.readBoolean()) {
				taxAmount = new Money(currency, in.readLong(), in.readCompressedInt());
			} else {
				taxAmount = null;
			}
			taxExempt = in.readBoolean();
			if(in.readBoolean()) {
				shippingAmount = new Money(currency, in.readLong(), in.readCompressedInt());
			} else {
				shippingAmount = null;
			}
			if(in.readBoolean()) {
				dutyAmount = new Money(currency, in.readLong(), in.readCompressedInt());
			} else {
				dutyAmount = null;
			}
			shippingFirstName = in.readNullUTF();
			shippingLastName = in.readNullUTF();
			shippingCompanyName = in.readNullUTF();
			shippingStreetAddress1 = in.readNullUTF();
			shippingStreetAddress2 = in.readNullUTF();
			shippingCity = in.readNullUTF();
			shippingState = InternUtils.intern(in.readNullUTF());
			shippingPostalCode = in.readNullUTF();
			shippingCountryCode = InternUtils.intern(in.readNullUTF());
			emailCustomer = in.readBoolean();
			merchantEmail = Email.valueOf(in.readNullUTF());
			invoiceNumber = in.readNullUTF();
			purchaseOrderNumber = in.readNullUTF();
			description = in.readNullUTF();
			creditCardCreatedBy = User.Name.valueOf(in.readUTF()).intern();
			creditCardPrincipalName = in.readNullUTF();
			creditCardAccounting = Account.Name.valueOf(in.readUTF()).intern();
			creditCardGroupName = in.readNullUTF();
			creditCardProviderUniqueId = in.readNullUTF();
			creditCardMaskedCardNumber = in.readUTF();
			creditCard_expirationMonth = in.readNullByte();
			creditCard_expirationYear = in.readNullShort();
			creditCardFirstName = in.readUTF();
			creditCardLastName = in.readUTF();
			creditCardCompanyName = in.readNullUTF();
			creditCardEmail = Email.valueOf(in.readNullUTF());
			creditCardPhone = in.readNullUTF();
			creditCardFax = in.readNullUTF();
			creditCardCustomerId = in.readNullUTF();
			creditCardCustomerTaxId = in.readNullUTF();
			creditCardStreetAddress1 = in.readUTF();
			creditCardStreetAddress2 = in.readNullUTF();
			creditCardCity = in.readUTF();
			creditCardState = InternUtils.intern(in.readNullUTF());
			creditCardPostalCode = in.readNullUTF();
			creditCardCountryCode = in.readUTF().intern();
			creditCardComments = in.readNullUTF();
			authorizationTime = SQLStreamables.readUnmodifiableTimestamp(in);
			authorizationUsername = InternUtils.intern(User.Name.valueOf(in.readNullUTF()));
			authorizationPrincipalName = InternUtils.intern(in.readNullUTF());
			authorizationCommunicationResult = InternUtils.intern(in.readNullUTF());
			authorizationProviderErrorCode = InternUtils.intern(in.readNullUTF());
			authorizationErrorCode = InternUtils.intern(in.readNullUTF());
			authorizationProviderErrorMessage = in.readNullUTF();
			authorizationProviderUniqueId = in.readNullUTF();
			authorizationResult_providerReplacementMaskedCardNumber = in.readNullUTF();
			authorizationResult_replacementMaskedCardNumber = in.readNullUTF();
			authorizationResult_providerReplacementExpiration = in.readNullUTF();
			authorizationResult_replacementExpirationMonth = in.readNullByte();
			authorizationResult_replacementExpirationYear = in.readNullShort();
			authorizationProviderApprovalResult = InternUtils.intern(in.readNullUTF());
			authorizationApprovalResult = InternUtils.intern(in.readNullUTF());
			authorizationProviderDeclineReason = InternUtils.intern(in.readNullUTF());
			authorizationDeclineReason = InternUtils.intern(in.readNullUTF());
			authorizationProviderReviewReason = InternUtils.intern(in.readNullUTF());
			authorizationReviewReason = InternUtils.intern(in.readNullUTF());
			authorizationProviderCvvResult = InternUtils.intern(in.readNullUTF());
			authorizationCvvResult = InternUtils.intern(in.readNullUTF());
			authorizationProviderAvsResult = InternUtils.intern(in.readNullUTF());
			authorizationAvsResult = InternUtils.intern(in.readNullUTF());
			authorizationApprovalCode = in.readNullUTF();
			captureTime = SQLStreamables.readNullUnmodifiableTimestamp(in);
			captureUsername = InternUtils.intern(User.Name.valueOf(in.readNullUTF()));
			capturePrincipalName = InternUtils.intern(in.readNullUTF());
			captureCommunicationResult = InternUtils.intern(in.readNullUTF());
			captureProviderErrorCode = InternUtils.intern(in.readNullUTF());
			captureErrorCode = InternUtils.intern(in.readNullUTF());
			captureProviderErrorMessage = in.readNullUTF();
			captureProviderUniqueId = in.readNullUTF();
			voidTime = SQLStreamables.readNullUnmodifiableTimestamp(in);
			voidUsername = InternUtils.intern(User.Name.valueOf(in.readNullUTF()));
			voidPrincipalName = InternUtils.intern(in.readNullUTF());
			voidCommunicationResult = InternUtils.intern(in.readNullUTF());
			voidProviderErrorCode = InternUtils.intern(in.readNullUTF());
			voidErrorCode = InternUtils.intern(in.readNullUTF());
			voidProviderErrorMessage = in.readNullUTF();
			voidProviderUniqueId = in.readNullUTF();
			status = in.readUTF().intern();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeUTF(processorId);
		out.writeUTF(accounting.toString());
		out.writeNullUTF(groupName);
		out.writeBoolean(testMode);
		out.writeCompressedInt(duplicateWindow);
		out.writeNullUTF(orderNumber);
		out.writeUTF(amount.getCurrency().getCurrencyCode());
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
			out.writeUTF(amount.getValue().toString());
		} else {
			out.writeLong(amount.getUnscaledValue());
			out.writeCompressedInt(amount.getScale());
		}
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
			out.writeNullUTF(taxAmount == null ? null : taxAmount.getValue().toString());
		} else {
			if(taxAmount != null) {
				out.writeBoolean(true);
				out.writeLong(taxAmount.getUnscaledValue());
				out.writeCompressedInt(taxAmount.getScale());
			} else {
				out.writeBoolean(false);
			}
		}
		out.writeBoolean(taxExempt);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
			out.writeNullUTF(shippingAmount == null ? null : shippingAmount.getValue().toString());
		} else {
			if(shippingAmount != null) {
				out.writeBoolean(true);
				out.writeLong(shippingAmount.getUnscaledValue());
				out.writeCompressedInt(shippingAmount.getScale());
			} else {
				out.writeBoolean(false);
			}
		}
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
			out.writeNullUTF(dutyAmount == null ? null : dutyAmount.getValue().toString());
		} else {
			if(dutyAmount != null) {
				out.writeBoolean(true);
				out.writeLong(dutyAmount.getUnscaledValue());
				out.writeCompressedInt(dutyAmount.getScale());
			} else {
				out.writeBoolean(false);
			}
		}
		out.writeNullUTF(shippingFirstName);
		out.writeNullUTF(shippingLastName);
		out.writeNullUTF(shippingCompanyName);
		out.writeNullUTF(shippingStreetAddress1);
		out.writeNullUTF(shippingStreetAddress2);
		out.writeNullUTF(shippingCity);
		out.writeNullUTF(shippingState);
		out.writeNullUTF(shippingPostalCode);
		out.writeNullUTF(shippingCountryCode);
		out.writeBoolean(emailCustomer);
		out.writeNullUTF(Objects.toString(merchantEmail, null));
		out.writeNullUTF(invoiceNumber);
		out.writeNullUTF(purchaseOrderNumber);
		out.writeNullUTF(description);
		out.writeUTF(creditCardCreatedBy.toString());
		out.writeNullUTF(creditCardPrincipalName);
		out.writeUTF(creditCardAccounting.toString());
		out.writeNullUTF(creditCardGroupName);
		out.writeNullUTF(creditCardProviderUniqueId);
		out.writeUTF(creditCardMaskedCardNumber);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_82_0) >= 0) {
			out.writeNullByte(creditCard_expirationMonth);
			out.writeNullShort(creditCard_expirationYear);
		}
		out.writeUTF(creditCardFirstName);
		out.writeUTF(creditCardLastName);
		out.writeNullUTF(creditCardCompanyName);
		out.writeNullUTF(Objects.toString(creditCardEmail, null));
		out.writeNullUTF(creditCardPhone);
		out.writeNullUTF(creditCardFax);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_82_1) >= 0) {
			out.writeNullUTF(creditCardCustomerId);
		}
		out.writeNullUTF(creditCardCustomerTaxId);
		out.writeUTF(creditCardStreetAddress1);
		out.writeNullUTF(creditCardStreetAddress2);
		out.writeUTF(creditCardCity);
		out.writeNullUTF(creditCardState);
		out.writeNullUTF(creditCardPostalCode);
		out.writeUTF(creditCardCountryCode);
		out.writeNullUTF(creditCardComments);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
			out.writeLong(authorizationTime.getTime());
		} else {
			SQLStreamables.writeTimestamp(authorizationTime, out);
		}
		out.writeNullUTF(Objects.toString(authorizationUsername, null));
		out.writeNullUTF(authorizationPrincipalName);
		out.writeNullUTF(authorizationCommunicationResult);
		out.writeNullUTF(authorizationProviderErrorCode);
		out.writeNullUTF(authorizationErrorCode);
		out.writeNullUTF(authorizationProviderErrorMessage);
		out.writeNullUTF(authorizationProviderUniqueId);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_82_0) >= 0) {
			out.writeNullUTF(authorizationResult_providerReplacementMaskedCardNumber);
			out.writeNullUTF(authorizationResult_replacementMaskedCardNumber);
			out.writeNullUTF(authorizationResult_providerReplacementExpiration);
			out.writeNullByte(authorizationResult_replacementExpirationMonth);
			out.writeNullShort(authorizationResult_replacementExpirationYear);
		}
		out.writeNullUTF(authorizationProviderApprovalResult);
		out.writeNullUTF(authorizationApprovalResult);
		out.writeNullUTF(authorizationProviderDeclineReason);
		out.writeNullUTF(authorizationDeclineReason);
		out.writeNullUTF(authorizationProviderReviewReason);
		out.writeNullUTF(authorizationReviewReason);
		out.writeNullUTF(authorizationProviderCvvResult);
		out.writeNullUTF(authorizationCvvResult);
		out.writeNullUTF(authorizationProviderAvsResult);
		out.writeNullUTF(authorizationAvsResult);
		out.writeNullUTF(authorizationApprovalCode);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
			out.writeLong(captureTime == null ? -1 : captureTime.getTime());
		} else {
			SQLStreamables.writeNullTimestamp(captureTime, out);
		}
		out.writeNullUTF(Objects.toString(captureUsername, null));
		out.writeNullUTF(capturePrincipalName);
		out.writeNullUTF(captureCommunicationResult);
		out.writeNullUTF(captureProviderErrorCode);
		out.writeNullUTF(captureErrorCode);
		out.writeNullUTF(captureProviderErrorMessage);
		out.writeNullUTF(captureProviderUniqueId);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
			out.writeLong(voidTime == null ? -1 : voidTime.getTime());
		} else {
			SQLStreamables.writeNullTimestamp(voidTime, out);
		}
		out.writeNullUTF(Objects.toString(voidUsername, null));
		out.writeNullUTF(voidPrincipalName);
		out.writeNullUTF(voidCommunicationResult);
		out.writeNullUTF(voidProviderErrorCode);
		out.writeNullUTF(voidErrorCode);
		out.writeNullUTF(voidProviderErrorMessage);
		out.writeNullUTF(voidProviderUniqueId);
		out.writeUTF(status);
	}

	/**
	 * Called when a sale (combined authorization and capture) has been completed.
	 */
	public void saleCompleted(
		final String authorizationCommunicationResult,
		final String authorizationProviderErrorCode,
		final String authorizationErrorCode,
		final String authorizationProviderErrorMessage,
		final String authorizationProviderUniqueId,
		final String authorizationResult_providerReplacementMaskedCardNumber,
		final String authorizationResult_replacementMaskedCardNumber,
		final String authorizationResult_providerReplacementExpiration,
		final Byte authorizationResult_replacementExpirationMonth,
		final Short authorizationResult_replacementExpirationYear,
		final String providerApprovalResult,
		final String approvalResult,
		final String providerDeclineReason,
		final String declineReason,
		final String providerReviewReason,
		final String reviewReason,
		final String providerCvvResult,
		final String cvvResult,
		final String providerAvsResult,
		final String avsResult,
		final String approvalCode,
		final Timestamp captureTime,
		final String capturePrincipalName,
		final String captureCommunicationResult,
		final String captureProviderErrorCode,
		final String captureErrorCode,
		final String captureProviderErrorMessage,
		final String captureProviderUniqueId,
		final String status
	) throws IOException, SQLException {
		if(!table.getConnector().isSecure()) throw new IOException("Credit card transactions may only be updated when using secure protocols.  Currently using the "+table.getConnector().getProtocol()+" protocol, which is not secure.");

		table.getConnector().requestUpdate(
			true,
			AoservProtocol.CommandID.CREDIT_CARD_TRANSACTION_SALE_COMPLETED,
			new AOServConnector.UpdateRequest() {
				private IntList invalidateList;

				@Override
				public void writeRequest(StreamableOutput out) throws IOException {
					out.writeCompressedInt(pkey);
					out.writeNullUTF(authorizationCommunicationResult);
					out.writeNullUTF(authorizationProviderErrorCode);
					out.writeNullUTF(authorizationErrorCode);
					out.writeNullUTF(authorizationProviderErrorMessage);
					out.writeNullUTF(authorizationProviderUniqueId);
					out.writeNullUTF(authorizationResult_providerReplacementMaskedCardNumber);
					out.writeNullUTF(authorizationResult_replacementMaskedCardNumber);
					out.writeNullUTF(authorizationResult_providerReplacementExpiration);
					out.writeNullByte(authorizationResult_replacementExpirationMonth);
					out.writeNullShort(authorizationResult_replacementExpirationYear);
					out.writeNullUTF(providerApprovalResult);
					out.writeNullUTF(approvalResult);
					out.writeNullUTF(providerDeclineReason);
					out.writeNullUTF(declineReason);
					out.writeNullUTF(providerReviewReason);
					out.writeNullUTF(reviewReason);
					out.writeNullUTF(providerCvvResult);
					out.writeNullUTF(cvvResult);
					out.writeNullUTF(providerAvsResult);
					out.writeNullUTF(avsResult);
					out.writeNullUTF(approvalCode);
					SQLStreamables.writeNullTimestamp(captureTime, out);
					out.writeNullUTF(capturePrincipalName);
					out.writeNullUTF(captureCommunicationResult);
					out.writeNullUTF(captureProviderErrorCode);
					out.writeNullUTF(captureErrorCode);
					out.writeNullUTF(captureProviderErrorMessage);
					out.writeNullUTF(captureProviderUniqueId);
					out.writeNullUTF(status);
				}

				@Override
				public void readResponse(StreamableInput in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AoservProtocol.DONE) {
						invalidateList=AOServConnector.readInvalidateList(in);
					} else {
						AoservProtocol.checkResult(code, in);
						throw new IOException("Unknown response code: "+code);
					}
				}

				@Override
				public void afterRelease() {
					table.getConnector().tablesUpdated(invalidateList);
				}
			}
		);
	}

	/**
	 * Called when an authorization has been completed.
	 */
	public void authorizeCompleted(
		final String authorizationCommunicationResult,
		final String authorizationProviderErrorCode,
		final String authorizationErrorCode,
		final String authorizationProviderErrorMessage,
		final String authorizationProviderUniqueId,
		final String authorizationResult_providerReplacementMaskedCardNumber,
		final String authorizationResult_replacementMaskedCardNumber,
		final String authorizationResult_providerReplacementExpiration,
		final Byte authorizationResult_replacementExpirationMonth,
		final Short authorizationResult_replacementExpirationYear,
		final String providerApprovalResult,
		final String approvalResult,
		final String providerDeclineReason,
		final String declineReason,
		final String providerReviewReason,
		final String reviewReason,
		final String providerCvvResult,
		final String cvvResult,
		final String providerAvsResult,
		final String avsResult,
		final String approvalCode,
		final String status
	) throws IOException, SQLException {
		if(!table.getConnector().isSecure()) throw new IOException("Credit card transactions may only be updated when using secure protocols.  Currently using the "+table.getConnector().getProtocol()+" protocol, which is not secure.");

		table.getConnector().requestUpdate(
			true,
			AoservProtocol.CommandID.CREDIT_CARD_TRANSACTION_AUTHORIZE_COMPLETED,
			new AOServConnector.UpdateRequest() {
				private IntList invalidateList;

				@Override
				public void writeRequest(StreamableOutput out) throws IOException {
					out.writeCompressedInt(pkey);
					out.writeNullUTF(authorizationCommunicationResult);
					out.writeNullUTF(authorizationProviderErrorCode);
					out.writeNullUTF(authorizationErrorCode);
					out.writeNullUTF(authorizationProviderErrorMessage);
					out.writeNullUTF(authorizationProviderUniqueId);
					out.writeNullUTF(authorizationResult_providerReplacementMaskedCardNumber);
					out.writeNullUTF(authorizationResult_replacementMaskedCardNumber);
					out.writeNullUTF(authorizationResult_providerReplacementExpiration);
					out.writeNullByte(authorizationResult_replacementExpirationMonth);
					out.writeNullShort(authorizationResult_replacementExpirationYear);
					out.writeNullUTF(providerApprovalResult);
					out.writeNullUTF(approvalResult);
					out.writeNullUTF(providerDeclineReason);
					out.writeNullUTF(declineReason);
					out.writeNullUTF(providerReviewReason);
					out.writeNullUTF(reviewReason);
					out.writeNullUTF(providerCvvResult);
					out.writeNullUTF(cvvResult);
					out.writeNullUTF(providerAvsResult);
					out.writeNullUTF(avsResult);
					out.writeNullUTF(approvalCode);
					out.writeNullUTF(status);
				}

				@Override
				public void readResponse(StreamableInput in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AoservProtocol.DONE) {
						invalidateList=AOServConnector.readInvalidateList(in);
					} else {
						AoservProtocol.checkResult(code, in);
						throw new IOException("Unknown response code: "+code);
					}
				}

				@Override
				public void afterRelease() {
					table.getConnector().tablesUpdated(invalidateList);
				}
			}
		);
	}
}
