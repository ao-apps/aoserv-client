/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2007-2013, 2016, 2017, 2018, 2019  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.net.Email;
import com.aoindustries.util.IntList;
import com.aoindustries.util.InternUtils;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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
	Account.Name accounting;
	private String groupName;
	private boolean testMode;
	private int duplicateWindow;
	private String orderNumber;
	private String currencyCode;
	private BigDecimal amount;
	private BigDecimal taxAmount;
	private boolean taxExempt;
	private BigDecimal shippingAmount;
	private BigDecimal dutyAmount;
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
	private String creditCardFirstName;
	private String creditCardLastName;
	private String creditCardCompanyName;
	private Email creditCardEmail;
	private String creditCardPhone;
	private String creditCardFax;
	private String creditCardCustomerTaxId;
	private String creditCardStreetAddress1;
	private String creditCardStreetAddress2;
	private String creditCardCity;
	private String creditCardState;
	private String creditCardPostalCode;
	private String creditCardCountryCode;
	private String creditCardComments;
	private long authorizationTime;
	private User.Name authorizationUsername;
	private String authorizationPrincipalName;
	// TODO: enum com.aoindustries.creditcards.TransactionResult.CommunicationResult
	private String authorizationCommunicationResult;
	private String authorizationProviderErrorCode;
	// TODO: enum com.aoindustries.creditcards.TransactionResult.ErrorCode
	private String authorizationErrorCode;
	private String authorizationProviderErrorMessage;
	private String authorizationProviderUniqueId;
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
	private long captureTime;
	private User.Name captureUsername;
	private String capturePrincipalName;
	// TODO: enum com.aoindustries.creditcards.TransactionResult.CommunicationResult
	private String captureCommunicationResult;
	private String captureProviderErrorCode;
	// TODO: enum com.aoindustries.creditcards.TransactionResult.ErrorCode
	private String captureErrorCode;
	private String captureProviderErrorMessage;
	private String captureProviderUniqueId;
	private long voidTime;
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

	/**
	 * For AOServ sub-account support, this is the business that is making the payment.
	 * For application-only use (not a sub-account to parent-account payment), use the same business
	 * as the owner of the credit card processor.
	 */
	public Account getBusiness() throws SQLException, IOException {
		Account business = table.getConnector().getAccount().getAccount().get(accounting);
		if(business==null) throw new SQLException("Unable to find Business: "+accounting);
		return business;
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
	 * Gets the 3-character ISO 4217 country code for the currency used in this transaction.
	 *
	 * These are obtained from <a href="http://en.wikipedia.org/wiki/ISO_4217">http://en.wikipedia.org/wiki/ISO_4217</a>
	 */
	public String getCurrencyCode() {
		return currencyCode;
	}

	/**
	 * Gets the amount of the transaction.  This amount should not include any tax, shipping charges, or duty.
	 * Thus the total amount of the transaction is the amount + taxAmount + shippingAmount + dutyAmount.
	 */
	public BigDecimal getAmount() {
		return amount;
	}

	/**
	 * Gets the tax amount of the transaction.
	 */
	public BigDecimal getTaxAmount() {
		return taxAmount;
	}

	/**
	 * Gets the taxExempt flag for this transaction.
	 */
	public boolean getTaxExempt() {
		return taxExempt;
	}

	/**
	 * Gets the shipping amount for this transaction.
	 */
	public BigDecimal getShippingAmount() {
		return shippingAmount;
	}

	/**
	 * Gets the duty amount for this transaction.
	 */
	public BigDecimal getDutyAmount() {
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
	 *
	 * See <a href="http://en.wikipedia.org/wiki/ISO_3166-1_alpha-2">http://en.wikipedia.org/wiki/ISO_3166-1_alpha-2</a>
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
		Administrator business_administrator = table.getConnector().getAccount().getAdministrator().get(creditCardCreatedBy);
		if (business_administrator == null) throw new SQLException("Unable to find BusinessAdministrator: " + creditCardCreatedBy);
		return business_administrator;
	}

	/**
	 * Gets the application-provided principal who added this credit card.
	 */
	public String getCreditCardPrincipalName() {
		return creditCardPrincipalName;
	}

	public Account getCreditCardBusiness() throws SQLException, IOException {
		Account business = table.getConnector().getAccount().getAccount().get(creditCardAccounting);
		if (business == null) throw new SQLException("Unable to find Business: " + creditCardAccounting);
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
	 *
	 * See <a href="http://en.wikipedia.org/wiki/ISO_3166-1_alpha-2">http://en.wikipedia.org/wiki/ISO_3166-1_alpha-2</a>
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
	public Timestamp getAuthorizationTime() {
		return new Timestamp(authorizationTime);
	}

	/**
	 * Gets the <code>BusinessAdministrator</code> who authorized this transactions.  This is the
	 * username of the account that has access to control credit card transactions.
	 */
	public Administrator getAuthorizationAdministrator() throws SQLException, IOException {
		if(authorizationUsername==null) return null;
		Administrator business_administrator = table.getConnector().getAccount().getAdministrator().get(authorizationUsername);
		if (business_administrator == null) throw new SQLException("Unable to find BusinessAdministrator: " + authorizationUsername);
		return business_administrator;
	}

	/**
	 * Gets the application-specific username who authorized this transaction.  For pure-AOServ data, this
	 * will contain the username of the <code>BusinessAdministrator</code> who was logged in and made the payment.
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
	public Timestamp getCaptureTime() {
		return captureTime==-1 ? null : new Timestamp(captureTime);
	}

	/**
	 * Gets the <code>BusinessAdministrator</code> who captured this transactions.  This is the
	 * username of the account that has access to control credit card transactions.
	 */
	public Administrator getCaptureAdministrator() throws SQLException, IOException {
		if(captureUsername==null) return null;
		Administrator business_administrator = table.getConnector().getAccount().getAdministrator().get(captureUsername);
		if (business_administrator == null) throw new SQLException("Unable to find BusinessAdministrator: " + captureUsername);
		return business_administrator;
	}

	/**
	 * Gets the application-specific username who captured this transaction.  For pure-AOServ data, this
	 * will contain the username of the <code>BusinessAdministrator</code> who was logged in and initiated the capture.
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
	public Timestamp getVoidTime() {
		return voidTime==-1 ? null : new Timestamp(voidTime);
	}

	/**
	 * Gets the <code>BusinessAdministrator</code> who voided this transactions.  This is the
	 * username of the account that has access to control credit card transactions.
	 */
	public Administrator getVoidAdministrator() throws SQLException, IOException {
		if(voidUsername==null) return null;
		Administrator business_administrator = table.getConnector().getAccount().getAdministrator().get(voidUsername);
		if (business_administrator == null) throw new SQLException("Unable to find BusinessAdministrator: " + voidUsername);
		return business_administrator;
	}

	/**
	 * Gets the application-specific username who voided this transaction.  For pure-AOServ data, this
	 * will contain the username of the <code>BusinessAdministrator</code> who was logged in and caused the void.
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
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_PROCESSOR_ID: return processorId;
			case 2: return accounting;
			case 3: return groupName;
			case 4: return testMode;
			case 5: return duplicateWindow;
			case 6: return orderNumber;
			case 7: return currencyCode;
			case 8: return amount;
			case 9: return taxAmount;
			case 10: return taxExempt;
			case 11: return shippingAmount;
			case 12: return dutyAmount;
			case 13: return shippingFirstName;
			case 14: return shippingLastName;
			case 15: return shippingCompanyName;
			case 16: return shippingStreetAddress1;
			case 17: return shippingStreetAddress2;
			case 18: return shippingCity;
			case 19: return shippingState;
			case 20: return shippingPostalCode;
			case 21: return shippingCountryCode;
			case 22: return emailCustomer;
			case 23: return merchantEmail;
			case 24: return invoiceNumber;
			case 25: return purchaseOrderNumber;
			case 26: return description;
			case 27: return creditCardCreatedBy;
			case 28: return creditCardPrincipalName;
			case 29: return creditCardAccounting;
			case 30: return creditCardGroupName;
			case 31: return creditCardProviderUniqueId;
			case 32: return creditCardMaskedCardNumber;
			case 33: return creditCardFirstName;
			case 34: return creditCardLastName;
			case 35: return creditCardCompanyName;
			case 36: return creditCardEmail;
			case 37: return creditCardPhone;
			case 38: return creditCardFax;
			case 39: return creditCardCustomerTaxId;
			case 40: return creditCardStreetAddress1;
			case 41: return creditCardStreetAddress2;
			case 42: return creditCardCity;
			case 43: return creditCardState;
			case 44: return creditCardPostalCode;
			case 45: return creditCardCountryCode;
			case 46: return creditCardComments;
			case 47: return getAuthorizationTime();
			case 48: return authorizationUsername;
			case 49: return authorizationPrincipalName;
			case 50: return authorizationCommunicationResult;
			case 51: return authorizationProviderErrorCode;
			case 52: return authorizationErrorCode;
			case 53: return authorizationProviderErrorMessage;
			case 54: return authorizationProviderUniqueId;
			case 55: return authorizationProviderApprovalResult;
			case 56: return authorizationApprovalResult;
			case 57: return authorizationProviderDeclineReason;
			case 58: return authorizationDeclineReason;
			case 59: return authorizationProviderReviewReason;
			case 60: return authorizationReviewReason;
			case 61: return authorizationProviderCvvResult;
			case 62: return authorizationCvvResult;
			case 63: return authorizationProviderAvsResult;
			case 64: return authorizationAvsResult;
			case 65: return authorizationApprovalCode;
			case 66: return getCaptureTime();
			case 67: return captureUsername;
			case 68: return capturePrincipalName;
			case 69: return captureCommunicationResult;
			case 70: return captureProviderErrorCode;
			case 71: return captureErrorCode;
			case 72: return captureProviderErrorMessage;
			case 73: return captureProviderUniqueId;
			case 74: return getVoidTime();
			case 75: return voidUsername;
			case 76: return voidPrincipalName;
			case 77: return voidCommunicationResult;
			case 78: return voidProviderErrorCode;
			case 79: return voidErrorCode;
			case 80: return voidProviderErrorMessage;
			case 81: return voidProviderUniqueId;
			case 82: return status;
			default: throw new IllegalArgumentException("Invalid index: "+i);
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
			currencyCode = result.getString(pos++);
			amount = result.getBigDecimal(pos++);
			taxAmount = result.getBigDecimal(pos++);
			taxExempt = result.getBoolean(pos++);
			shippingAmount = result.getBigDecimal(pos++);
			dutyAmount = result.getBigDecimal(pos++);
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
			creditCardFirstName = result.getString(pos++);
			creditCardLastName = result.getString(pos++);
			creditCardCompanyName = result.getString(pos++);
			creditCardEmail = Email.valueOf(result.getString(pos++));
			creditCardPhone = result.getString(pos++);
			creditCardFax = result.getString(pos++);
			creditCardCustomerTaxId = result.getString(pos++);
			creditCardStreetAddress1 = result.getString(pos++);
			creditCardStreetAddress2 = result.getString(pos++);
			creditCardCity = result.getString(pos++);
			creditCardState = result.getString(pos++);
			creditCardPostalCode = result.getString(pos++);
			creditCardCountryCode = result.getString(pos++);
			creditCardComments = result.getString(pos++);
			authorizationTime = result.getTimestamp(pos++).getTime();
			authorizationUsername = User.Name.valueOf(result.getString(pos++));
			authorizationPrincipalName = result.getString(pos++);
			authorizationCommunicationResult = result.getString(pos++);
			authorizationProviderErrorCode = result.getString(pos++);
			authorizationErrorCode = result.getString(pos++);
			authorizationProviderErrorMessage = result.getString(pos++);
			authorizationProviderUniqueId = result.getString(pos++);
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
			Timestamp T = result.getTimestamp(pos++);
			captureTime = T==null ? -1 : T.getTime();
			captureUsername = User.Name.valueOf(result.getString(pos++));
			capturePrincipalName = result.getString(pos++);
			captureCommunicationResult = result.getString(pos++);
			captureProviderErrorCode = result.getString(pos++);
			captureErrorCode = result.getString(pos++);
			captureProviderErrorMessage = result.getString(pos++);
			captureProviderUniqueId = result.getString(pos++);
			T = result.getTimestamp(pos++);
			voidTime = T==null ? -1 : T.getTime();
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
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			pkey = in.readCompressedInt();
			processorId = in.readUTF().intern();
			accounting = Account.Name.valueOf(in.readUTF()).intern();
			groupName = in.readNullUTF();
			testMode = in.readBoolean();
			duplicateWindow = in.readCompressedInt();
			orderNumber = in.readNullUTF();
			currencyCode = in.readUTF().intern();
			amount = new BigDecimal(in.readUTF());
			String S = in.readNullUTF();
			taxAmount = S==null ? null : new BigDecimal(S);
			taxExempt = in.readBoolean();
			S = in.readNullUTF();
			shippingAmount = S==null ? null : new BigDecimal(S);
			S = in.readNullUTF();
			dutyAmount = S==null ? null : new BigDecimal(S);
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
			creditCardFirstName = in.readUTF();
			creditCardLastName = in.readUTF();
			creditCardCompanyName = in.readNullUTF();
			creditCardEmail = Email.valueOf(in.readNullUTF());
			creditCardPhone = in.readNullUTF();
			creditCardFax = in.readNullUTF();
			creditCardCustomerTaxId = in.readNullUTF();
			creditCardStreetAddress1 = in.readUTF();
			creditCardStreetAddress2 = in.readNullUTF();
			creditCardCity = in.readUTF();
			creditCardState = InternUtils.intern(in.readNullUTF());
			creditCardPostalCode = in.readNullUTF();
			creditCardCountryCode = in.readUTF().intern();
			creditCardComments = in.readNullUTF();
			authorizationTime = in.readLong();
			authorizationUsername = InternUtils.intern(User.Name.valueOf(in.readNullUTF()));
			authorizationPrincipalName = InternUtils.intern(in.readNullUTF());
			authorizationCommunicationResult = InternUtils.intern(in.readNullUTF());
			authorizationProviderErrorCode = InternUtils.intern(in.readNullUTF());
			authorizationErrorCode = InternUtils.intern(in.readNullUTF());
			authorizationProviderErrorMessage = in.readNullUTF();
			authorizationProviderUniqueId = in.readNullUTF();
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
			captureTime = in.readLong();
			captureUsername = InternUtils.intern(User.Name.valueOf(in.readNullUTF()));
			capturePrincipalName = InternUtils.intern(in.readNullUTF());
			captureCommunicationResult = InternUtils.intern(in.readNullUTF());
			captureProviderErrorCode = InternUtils.intern(in.readNullUTF());
			captureErrorCode = InternUtils.intern(in.readNullUTF());
			captureProviderErrorMessage = in.readNullUTF();
			captureProviderUniqueId = in.readNullUTF();
			voidTime = in.readLong();
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
	public void write(CompressedDataOutputStream out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeUTF(processorId);
		out.writeUTF(accounting.toString());
		out.writeNullUTF(groupName);
		out.writeBoolean(testMode);
		out.writeCompressedInt(duplicateWindow);
		out.writeNullUTF(orderNumber);
		out.writeUTF(currencyCode);
		out.writeUTF(amount.toString());
		out.writeNullUTF(Objects.toString(taxAmount, null));
		out.writeBoolean(taxExempt);
		out.writeNullUTF(Objects.toString(shippingAmount, null));
		out.writeNullUTF(Objects.toString(dutyAmount, null));
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
		out.writeUTF(creditCardFirstName);
		out.writeUTF(creditCardLastName);
		out.writeNullUTF(creditCardCompanyName);
		out.writeNullUTF(Objects.toString(creditCardEmail, null));
		out.writeNullUTF(creditCardPhone);
		out.writeNullUTF(creditCardFax);
		out.writeNullUTF(creditCardCustomerTaxId);
		out.writeUTF(creditCardStreetAddress1);
		out.writeNullUTF(creditCardStreetAddress2);
		out.writeUTF(creditCardCity);
		out.writeNullUTF(creditCardState);
		out.writeNullUTF(creditCardPostalCode);
		out.writeUTF(creditCardCountryCode);
		out.writeNullUTF(creditCardComments);
		out.writeLong(authorizationTime);
		out.writeNullUTF(Objects.toString(authorizationUsername, null));
		out.writeNullUTF(authorizationPrincipalName);
		out.writeNullUTF(authorizationCommunicationResult);
		out.writeNullUTF(authorizationProviderErrorCode);
		out.writeNullUTF(authorizationErrorCode);
		out.writeNullUTF(authorizationProviderErrorMessage);
		out.writeNullUTF(authorizationProviderUniqueId);
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
		out.writeLong(captureTime);
		out.writeNullUTF(Objects.toString(captureUsername, null));
		out.writeNullUTF(capturePrincipalName);
		out.writeNullUTF(captureCommunicationResult);
		out.writeNullUTF(captureProviderErrorCode);
		out.writeNullUTF(captureErrorCode);
		out.writeNullUTF(captureProviderErrorMessage);
		out.writeNullUTF(captureProviderUniqueId);
		out.writeLong(voidTime);
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

		table.getConnector().requestUpdate(true,
			AoservProtocol.CommandID.CREDIT_CARD_TRANSACTION_SALE_COMPLETED,
			new AOServConnector.UpdateRequest() {
				IntList invalidateList;

				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(pkey);
					out.writeNullUTF(authorizationCommunicationResult);
					out.writeNullUTF(authorizationProviderErrorCode);
					out.writeNullUTF(authorizationErrorCode);
					out.writeNullUTF(authorizationProviderErrorMessage);
					out.writeNullUTF(authorizationProviderUniqueId);
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
					out.writeLong(captureTime==null ? -1 : captureTime.getTime());
					out.writeNullUTF(capturePrincipalName);
					out.writeNullUTF(captureCommunicationResult);
					out.writeNullUTF(captureProviderErrorCode);
					out.writeNullUTF(captureErrorCode);
					out.writeNullUTF(captureProviderErrorMessage);
					out.writeNullUTF(captureProviderUniqueId);
					out.writeNullUTF(status);
				}

				@Override
				public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
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

		table.getConnector().requestUpdate(true,
			AoservProtocol.CommandID.CREDIT_CARD_TRANSACTION_AUTHORIZE_COMPLETED,
			new AOServConnector.UpdateRequest() {
				IntList invalidateList;

				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(pkey);
					out.writeNullUTF(authorizationCommunicationResult);
					out.writeNullUTF(authorizationProviderErrorCode);
					out.writeNullUTF(authorizationErrorCode);
					out.writeNullUTF(authorizationProviderErrorMessage);
					out.writeNullUTF(authorizationProviderUniqueId);
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
				public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
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
