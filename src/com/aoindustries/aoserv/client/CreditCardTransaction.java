package com.aoindustries.aoserv.client;

/*
 * Copyright 2007-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.util.IntList;
import com.aoindustries.util.StringUtility;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

/**
 * A <code>CreditCardTransaction</code> stores the complete history of credit card transactions.
 *
 * @author  AO Industries, Inc.
 */
final public class CreditCardTransaction extends CachedObjectIntegerKey<CreditCardTransaction> {

    static final int
        COLUMN_PKEY = 0,
        COLUMN_PROCESSOR_ID = 1
    ;
    static final String COLUMN_ACCOUNTING_name = "accounting";
    static final String COLUMN_AUTHORIZATION_TIME_name = "authorization_time";
    static final String COLUMN_PKEY_name = "pkey";

    private String processorId;
    String accounting;
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
    private String merchantEmail;
    private String invoiceNumber;
    private String purchaseOrderNumber;
    private String description;
    private String creditCardCreatedBy;
    private String creditCardPrincipalName;
    private String creditCardAccounting;
    private String creditCardGroupName;
    private String creditCardProviderUniqueId;
    private String creditCardMaskedCardNumber;
    private String creditCardFirstName;
    private String creditCardLastName;
    private String creditCardCompanyName;
    private String creditCardEmail;
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
    private String authorizationUsername;
    private String authorizationPrincipalName;
    private String authorizationCommunicationResult;
    private String authorizationProviderErrorCode;
    private String authorizationErrorCode;
    private String authorizationProviderErrorMessage;
    private String authorizationProviderUniqueId;
    private String authorizationProviderApprovalResult;
    private String authorizationApprovalResult;
    private String authorizationProviderDeclineReason;
    private String authorizationDeclineReason;
    private String authorizationProviderReviewReason;
    private String authorizationReviewReason;
    private String authorizationProviderCvvResult;
    private String authorizationCvvResult;
    private String authorizationProviderAvsResult;
    private String authorizationAvsResult;
    private String authorizationApprovalCode;
    private long captureTime;
    private String captureUsername;
    private String capturePrincipalName;
    private String captureCommunicationResult;
    private String captureProviderErrorCode;
    private String captureErrorCode;
    private String captureProviderErrorMessage;
    private String captureProviderUniqueId;
    private long voidTime;
    private String voidUsername;
    private String voidPrincipalName;
    private String voidCommunicationResult;
    private String voidProviderErrorCode;
    private String voidErrorCode;
    private String voidProviderErrorMessage;
    private String voidProviderUniqueId;
    private String status;

    /**
     * Gets the credit card processor used for this transaction.
     */
    public CreditCardProcessor getCreditCardProcessor() throws SQLException, IOException {
        CreditCardProcessor ccp = table.connector.getCreditCardProcessors().get(processorId);
        if(ccp==null) throw new SQLException("Unable to find CreditCardProcessor: "+processorId);
        return ccp;
    }

    /**
     * For AOServ sub-account support, this is the business that is making the payment.
     * For application-only use (not a sub-account to parent-account payment), use the same business
     * as the owner of the credit card processor.
     */
    public Business getBusiness() throws SQLException, IOException {
        Business business = table.connector.getBusinesses().get(accounting);
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
        CountryCode cc = table.connector.getCountryCodes().get(shippingCountryCode);
        if(cc==null) throw new SQLException("Unable to find CountryCode: "+shippingCountryCode);
        return cc;
    }

    public boolean getEmailCustomer() {
        return emailCustomer;
    }

    public String getMerchantEmail() {
        return merchantEmail;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public String getPurchaseOrderNumber() {
        return purchaseOrderNumber;
    }

    public BusinessAdministrator getCreditCardCreatedBy() throws SQLException, IOException {
        BusinessAdministrator business_administrator = table.connector.getBusinessAdministrators().get(creditCardCreatedBy);
        if (business_administrator == null) throw new SQLException("Unable to find BusinessAdministrator: " + creditCardCreatedBy);
        return business_administrator;
    }

    /**
     * Gets the application-provided principal who added this credit card.
     */
    public String getCreditCardPrincipalName() {
        return creditCardPrincipalName;
    }

    public Business getCreditCardBusiness() throws SQLException, IOException {
        Business business = table.connector.getBusinesses().get(creditCardAccounting);
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

    public String getCreditCardEmail() {
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
        CountryCode cc = table.connector.getCountryCodes().get(creditCardCountryCode);
        if(cc==null) throw new SQLException("Unable to find CountryCode: "+creditCardCountryCode);
        return cc;
    }

    public String getCreditCardComments() {
        return creditCardComments;
    }

    /**
     * Gets the time of the authorization or <code>-1</code> if not available.
     */
    public long getAuthorizationTime() {
        return authorizationTime;
    }
    
    /**
     * Gets the <code>BusinessAdministrator</code> who authorized this transactions.  This is the
     * username of the account that has access to control credit card transactions.
     */
    public BusinessAdministrator getAuthorizationAdministrator() throws SQLException, IOException {
        if(authorizationUsername==null) return null;
        BusinessAdministrator business_administrator = table.connector.getBusinessAdministrators().get(authorizationUsername);
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
    public long getCaptureTime() {
        return captureTime;
    }
    
    /**
     * Gets the <code>BusinessAdministrator</code> who captured this transactions.  This is the
     * username of the account that has access to control credit card transactions.
     */
    public BusinessAdministrator getCaptureAdministrator() throws SQLException, IOException {
        if(captureUsername==null) return null;
        BusinessAdministrator business_administrator = table.connector.getBusinessAdministrators().get(captureUsername);
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
    public long getVoidTime() {
        return voidTime;
    }
    
    /**
     * Gets the <code>BusinessAdministrator</code> who voided this transactions.  This is the
     * username of the account that has access to control credit card transactions.
     */
    public BusinessAdministrator getVoidAdministrator() throws SQLException, IOException {
        if(voidUsername==null) return null;
        BusinessAdministrator business_administrator = table.connector.getBusinessAdministrators().get(voidUsername);
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

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_PROCESSOR_ID: return processorId;
            case 2: return accounting;
            case 3: return groupName;
            case 4: return Boolean.valueOf(testMode);
            case 5: return Integer.valueOf(duplicateWindow);
            case 6: return orderNumber;
            case 7: return currencyCode;
            case 8: return amount;
            case 9: return taxAmount;
            case 10: return Boolean.valueOf(taxExempt);
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
            case 22: return Boolean.valueOf(emailCustomer);
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
            case 47: return authorizationTime==-1 ? null : new java.sql.Date(authorizationTime);
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
            case 66: return captureTime==-1 ? null : new java.sql.Date(captureTime);
            case 67: return captureUsername;
            case 68: return capturePrincipalName;
            case 69: return captureCommunicationResult;
            case 70: return captureProviderErrorCode;
            case 71: return captureErrorCode;
            case 72: return captureProviderErrorMessage;
            case 73: return captureProviderUniqueId;
            case 74: return voidTime==-1 ? null : new java.sql.Date(voidTime);
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

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.CREDIT_CARD_TRANSACTIONS;
    }

    public void init(ResultSet result) throws SQLException {
        int pos = 1;
	pkey = result.getInt(pos++);
        processorId = result.getString(pos++);
        accounting = result.getString(pos++);
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
        merchantEmail = result.getString(pos++);
        invoiceNumber = result.getString(pos++);
        purchaseOrderNumber = result.getString(pos++);
        description = result.getString(pos++);
        creditCardCreatedBy = result.getString(pos++);
        creditCardPrincipalName = result.getString(pos++);
        creditCardAccounting = result.getString(pos++);
        creditCardGroupName = result.getString(pos++);
        creditCardProviderUniqueId = result.getString(pos++);
        creditCardMaskedCardNumber = result.getString(pos++);
        creditCardFirstName = result.getString(pos++);
        creditCardLastName = result.getString(pos++);
        creditCardCompanyName = result.getString(pos++);
        creditCardEmail = result.getString(pos++);
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
        Timestamp T = result.getTimestamp(pos++);
        authorizationTime = T==null ? -1 : T.getTime();
        authorizationUsername = result.getString(pos++);
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
        T = result.getTimestamp(pos++);
        captureTime = T==null ? -1 : T.getTime();
        captureUsername = result.getString(pos++);
        capturePrincipalName = result.getString(pos++);
        captureCommunicationResult = result.getString(pos++);
        captureProviderErrorCode = result.getString(pos++);
        captureErrorCode = result.getString(pos++);
        captureProviderErrorMessage = result.getString(pos++);
        captureProviderUniqueId = result.getString(pos++);
        T = result.getTimestamp(pos++);
        voidTime = T==null ? -1 : T.getTime();
        voidUsername = result.getString(pos++);
        voidPrincipalName = result.getString(pos++);
        voidCommunicationResult = result.getString(pos++);
        voidProviderErrorCode = result.getString(pos++);
        voidErrorCode = result.getString(pos++);
        voidProviderErrorMessage = result.getString(pos++);
        voidProviderUniqueId = result.getString(pos++);
        status = result.getString(pos++);
    }

    public void read(CompressedDataInputStream in) throws IOException {
    	pkey = in.readCompressedInt();
        processorId = in.readUTF().intern();
        accounting = in.readUTF().intern();
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
        shippingState = StringUtility.intern(in.readNullUTF());
        shippingPostalCode = in.readNullUTF();
        shippingCountryCode = StringUtility.intern(in.readNullUTF());
        emailCustomer = in.readBoolean();
        merchantEmail = in.readNullUTF();
        invoiceNumber = in.readNullUTF();
        purchaseOrderNumber = in.readNullUTF();
        description = in.readNullUTF();
        creditCardCreatedBy = in.readUTF().intern();
        creditCardPrincipalName = in.readNullUTF();
        creditCardAccounting = in.readUTF().intern();
        creditCardGroupName = in.readNullUTF();
        creditCardProviderUniqueId = in.readNullUTF();
        creditCardMaskedCardNumber = in.readUTF();
        creditCardFirstName = in.readUTF();
        creditCardLastName = in.readUTF();
        creditCardCompanyName = in.readNullUTF();
        creditCardEmail = in.readNullUTF();
        creditCardPhone = in.readNullUTF();
        creditCardFax = in.readNullUTF();
        creditCardCustomerTaxId = in.readNullUTF();
        creditCardStreetAddress1 = in.readUTF();
        creditCardStreetAddress2 = in.readNullUTF();
        creditCardCity = in.readUTF();
        creditCardState = StringUtility.intern(in.readNullUTF());
        creditCardPostalCode = in.readNullUTF();
        creditCardCountryCode = in.readUTF().intern();
        creditCardComments = in.readNullUTF();
        authorizationTime = in.readLong();
        authorizationUsername = StringUtility.intern(in.readNullUTF());
        authorizationPrincipalName = StringUtility.intern(in.readNullUTF());
        authorizationCommunicationResult = StringUtility.intern(in.readNullUTF());
        authorizationProviderErrorCode = StringUtility.intern(in.readNullUTF());
        authorizationErrorCode = StringUtility.intern(in.readNullUTF());
        authorizationProviderErrorMessage = in.readNullUTF();
        authorizationProviderUniqueId = in.readNullUTF();
        authorizationProviderApprovalResult = StringUtility.intern(in.readNullUTF());
        authorizationApprovalResult = StringUtility.intern(in.readNullUTF());
        authorizationProviderDeclineReason = StringUtility.intern(in.readNullUTF());
        authorizationDeclineReason = StringUtility.intern(in.readNullUTF());
        authorizationProviderReviewReason = StringUtility.intern(in.readNullUTF());
        authorizationReviewReason = StringUtility.intern(in.readNullUTF());
        authorizationProviderCvvResult = StringUtility.intern(in.readNullUTF());
        authorizationCvvResult = StringUtility.intern(in.readNullUTF());
        authorizationProviderAvsResult = StringUtility.intern(in.readNullUTF());
        authorizationAvsResult = StringUtility.intern(in.readNullUTF());
        authorizationApprovalCode = in.readNullUTF();
        captureTime = in.readLong();
        captureUsername = StringUtility.intern(in.readNullUTF());
        capturePrincipalName = StringUtility.intern(in.readNullUTF());
        captureCommunicationResult = StringUtility.intern(in.readNullUTF());
        captureProviderErrorCode = StringUtility.intern(in.readNullUTF());
        captureErrorCode = StringUtility.intern(in.readNullUTF());
        captureProviderErrorMessage = in.readNullUTF();
        captureProviderUniqueId = in.readNullUTF();
        voidTime = in.readLong();
        voidUsername = StringUtility.intern(in.readNullUTF());
        voidPrincipalName = StringUtility.intern(in.readNullUTF());
        voidCommunicationResult = StringUtility.intern(in.readNullUTF());
        voidProviderErrorCode = StringUtility.intern(in.readNullUTF());
        voidErrorCode = StringUtility.intern(in.readNullUTF());
        voidProviderErrorMessage = in.readNullUTF();
        voidProviderUniqueId = in.readNullUTF();
        status = in.readUTF().intern();
    }

    public List<? extends AOServObject> getDependencies() throws IOException, SQLException {
        return createDependencyList(
            getCreditCardProcessor(),
            getBusiness(),
            getCreditCardCreatedBy(),
            getCreditCardBusiness(),
            getAuthorizationAdministrator(),
            getCaptureAdministrator(),
            getVoidAdministrator()
        );
    }

    public List<? extends AOServObject> getDependentObjects() throws IOException, SQLException {
        return createDependencyList(
        );
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
    	out.writeCompressedInt(pkey);
        out.writeUTF(processorId);
        out.writeUTF(accounting);
    	out.writeNullUTF(groupName);
        out.writeBoolean(testMode);
        out.writeCompressedInt(duplicateWindow);
        out.writeNullUTF(orderNumber);
        out.writeUTF(currencyCode);
        out.writeUTF(amount.toString());
        out.writeNullUTF(taxAmount==null ? null : taxAmount.toString());
        out.writeBoolean(taxExempt);
        out.writeNullUTF(shippingAmount==null ? null : shippingAmount.toString());
        out.writeNullUTF(dutyAmount==null ? null : dutyAmount.toString());
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
        out.writeNullUTF(merchantEmail);
        out.writeNullUTF(invoiceNumber);
        out.writeNullUTF(purchaseOrderNumber);
        out.writeNullUTF(description);
        out.writeUTF(creditCardCreatedBy);
        out.writeNullUTF(creditCardPrincipalName);
        out.writeUTF(creditCardAccounting);
        out.writeNullUTF(creditCardGroupName);
        out.writeNullUTF(creditCardProviderUniqueId);
        out.writeUTF(creditCardMaskedCardNumber);
        out.writeUTF(creditCardFirstName);
        out.writeUTF(creditCardLastName);
        out.writeNullUTF(creditCardCompanyName);
        out.writeNullUTF(creditCardEmail);
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
        out.writeNullUTF(authorizationUsername);
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
        out.writeNullUTF(captureUsername);
        out.writeNullUTF(capturePrincipalName);
        out.writeNullUTF(captureCommunicationResult);
        out.writeNullUTF(captureProviderErrorCode);
        out.writeNullUTF(captureErrorCode);
        out.writeNullUTF(captureProviderErrorMessage);
        out.writeNullUTF(captureProviderUniqueId);
        out.writeLong(voidTime);
        out.writeNullUTF(voidUsername);
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
        final long captureTime,
        final String capturePrincipalName,
        final String captureCommunicationResult,
        final String captureProviderErrorCode,
        final String captureErrorCode,
        final String captureProviderErrorMessage,
        final String captureProviderUniqueId,
        final String status
    ) throws IOException, SQLException {
        if(!table.connector.isSecure()) throw new IOException("Credit card transactions may only be updated when using secure protocols.  Currently using the "+table.connector.getProtocol()+" protocol, which is not secure.");

        table.connector.requestUpdate(
            true,
            new AOServConnector.UpdateRequest() {
                IntList invalidateList;

                public void writeRequest(CompressedDataOutputStream out) throws IOException {
                    out.writeCompressedInt(AOServProtocol.CommandID.CREDIT_CARD_TRANSACTION_SALE_COMPLETED.ordinal());
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
                    out.writeLong(captureTime);
                    out.writeNullUTF(capturePrincipalName);
                    out.writeNullUTF(captureCommunicationResult);
                    out.writeNullUTF(captureProviderErrorCode);
                    out.writeNullUTF(captureErrorCode);
                    out.writeNullUTF(captureProviderErrorMessage);
                    out.writeNullUTF(captureProviderUniqueId);
                    out.writeNullUTF(status);
                }

                public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
                    int code=in.readByte();
                    if(code==AOServProtocol.DONE) {
                        invalidateList=AOServConnector.readInvalidateList(in);
                    } else {
                        AOServProtocol.checkResult(code, in);
                        throw new IOException("Unknown response code: "+code);
                    }
                }

                public void afterRelease() {
                    table.connector.tablesUpdated(invalidateList);
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
        if(!table.connector.isSecure()) throw new IOException("Credit card transactions may only be updated when using secure protocols.  Currently using the "+table.connector.getProtocol()+" protocol, which is not secure.");

        table.connector.requestUpdate(
            true,
            new AOServConnector.UpdateRequest() {
                IntList invalidateList;

                public void writeRequest(CompressedDataOutputStream out) throws IOException {
                    out.writeCompressedInt(AOServProtocol.CommandID.CREDIT_CARD_TRANSACTION_AUTHORIZE_COMPLETED.ordinal());
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

                public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
                    int code=in.readByte();
                    if(code==AOServProtocol.DONE) {
                        invalidateList=AOServConnector.readInvalidateList(in);
                    } else {
                        AOServProtocol.checkResult(code, in);
                        throw new IOException("Unknown response code: "+code);
                    }
                }

                public void afterRelease() {
                    table.connector.tablesUpdated(invalidateList);
                }
            }
        );
    }
}
