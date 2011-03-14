/*
 * Copyright 2007-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.i18n.Money;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.rmi.RemoteException;
import java.sql.Timestamp;

/**
 * A <code>CreditCardTransaction</code> stores the complete history of credit card transactions.
 *
 * @author  AO Industries, Inc.
 */
final public class CreditCardTransaction extends AOServObjectIntegerKey implements Comparable<CreditCardTransaction>, DtoFactory<com.aoindustries.aoserv.client.dto.CreditCardTransaction> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    // TODO: private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private String processorId;
    private AccountingCode accounting;
    final private String groupName;
    final private boolean testMode;
    final private int duplicateWindow;
    final private String orderNumber;
    final private Money amount;
    final private Money taxAmount;
    final private boolean taxExempt;
    final private Money shippingAmount;
    final private Money dutyAmount;
    final private String shippingFirstName;
    final private String shippingLastName;
    final private String shippingCompanyName;
    final private String shippingStreetAddress1;
    final private String shippingStreetAddress2;
    final private String shippingCity;
    final private String shippingState;
    final private String shippingPostalCode;
    private String shippingCountryCode;
    final private boolean emailCustomer;
    final private Email merchantEmail;
    final private String invoiceNumber;
    final private String purchaseOrderNumber;
    final private String description;
    private UserId creditCardCreatedBy;
    final private String creditCardPrincipalName;
    private AccountingCode creditCardAccounting;
    final private String creditCardGroupName;
    final private String creditCardProviderUniqueId;
    final private String creditCardMaskedCardNumber;
    final private String creditCardFirstName;
    final private String creditCardLastName;
    final private String creditCardCompanyName;
    final private Email creditCardEmail;
    final private String creditCardPhone;
    final private String creditCardFax;
    final private String creditCardCustomerTaxId;
    final private String creditCardStreetAddress1;
    final private String creditCardStreetAddress2;
    final private String creditCardCity;
    final private String creditCardState;
    final private String creditCardPostalCode;
    private String creditCardCountryCode;
    final private String creditCardComments;
    final private long authorizationTime;
    private UserId authorizationUsername;
    final private String authorizationPrincipalName;
    private String authorizationCommunicationResult;
    final private String authorizationProviderErrorCode;
    private String authorizationErrorCode;
    final private String authorizationProviderErrorMessage;
    final private String authorizationProviderUniqueId;
    final private String authorizationProviderApprovalResult;
    private String authorizationApprovalResult;
    final private String authorizationProviderDeclineReason;
    private String authorizationDeclineReason;
    final private String authorizationProviderReviewReason;
    private String authorizationReviewReason;
    final private String authorizationProviderCvvResult;
    private String authorizationCvvResult;
    final private String authorizationProviderAvsResult;
    private String authorizationAvsResult;
    final private String authorizationApprovalCode;
    final private Long captureTime;
    private UserId captureUsername;
    final private String capturePrincipalName;
    private String captureCommunicationResult;
    final private String captureProviderErrorCode;
    private String captureErrorCode;
    final private String captureProviderErrorMessage;
    final private String captureProviderUniqueId;
    final private Long voidTime;
    private UserId voidUsername;
    final private String voidPrincipalName;
    private String voidCommunicationResult;
    final private String voidProviderErrorCode;
    private String voidErrorCode;
    final private String voidProviderErrorMessage;
    final private String voidProviderUniqueId;
    private String status;

    public CreditCardTransaction(
        AOServConnector connector,
        int pkey,
        String processorId,
        AccountingCode accounting,
        String groupName,
        boolean testMode,
        int duplicateWindow,
        String orderNumber,
        Money amount,
        Money taxAmount,
        boolean taxExempt,
        Money shippingAmount,
        Money dutyAmount,
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
        Email merchantEmail,
        String invoiceNumber,
        String purchaseOrderNumber,
        String description,
        UserId creditCardCreatedBy,
        String creditCardPrincipalName,
        AccountingCode creditCardAccounting,
        String creditCardGroupName,
        String creditCardProviderUniqueId,
        String creditCardMaskedCardNumber,
        String creditCardFirstName,
        String creditCardLastName,
        String creditCardCompanyName,
        Email creditCardEmail,
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
        UserId authorizationUsername,
        String authorizationPrincipalName,
        String authorizationCommunicationResult,
        String authorizationProviderErrorCode,
        String authorizationErrorCode,
        String authorizationProviderErrorMessage,
        String authorizationProviderUniqueId,
        String authorizationProviderApprovalResult,
        String authorizationApprovalResult,
        String authorizationProviderDeclineReason,
        String authorizationDeclineReason,
        String authorizationProviderReviewReason,
        String authorizationReviewReason,
        String authorizationProviderCvvResult,
        String authorizationCvvResult,
        String authorizationProviderAvsResult,
        String authorizationAvsResult,
        String authorizationApprovalCode,
        Long captureTime,
        UserId captureUsername,
        String capturePrincipalName,
        String captureCommunicationResult,
        String captureProviderErrorCode,
        String captureErrorCode,
        String captureProviderErrorMessage,
        String captureProviderUniqueId,
        Long voidTime,
        UserId voidUsername,
        String voidPrincipalName,
        String voidCommunicationResult,
        String voidProviderErrorCode,
        String voidErrorCode,
        String voidProviderErrorMessage,
        String voidProviderUniqueId,
        String status
    ) {
        super(connector, pkey);
        this.processorId = processorId;
        this.accounting = accounting;
        this.groupName = groupName;
        this.testMode = testMode;
        this.duplicateWindow = duplicateWindow;
        this.orderNumber = orderNumber;
        this.amount = amount;
        this.taxAmount = taxAmount;
        this.taxExempt = taxExempt;
        this.shippingAmount = shippingAmount;
        this.dutyAmount = dutyAmount;
        this.shippingFirstName = shippingFirstName;
        this.shippingLastName = shippingLastName;
        this.shippingCompanyName = shippingCompanyName;
        this.shippingStreetAddress1 = shippingStreetAddress1;
        this.shippingStreetAddress2 = shippingStreetAddress2;
        this.shippingCity = shippingCity;
        this.shippingState = shippingState;
        this.shippingPostalCode = shippingPostalCode;
        this.shippingCountryCode = shippingCountryCode;
        this.emailCustomer = emailCustomer;
        this.merchantEmail = merchantEmail;
        this.invoiceNumber = invoiceNumber;
        this.purchaseOrderNumber = purchaseOrderNumber;
        this.description = description;
        this.creditCardCreatedBy = creditCardCreatedBy;
        this.creditCardPrincipalName = creditCardPrincipalName;
        this.creditCardAccounting = creditCardAccounting;
        this.creditCardGroupName = creditCardGroupName;
        this.creditCardProviderUniqueId = creditCardProviderUniqueId;
        this.creditCardMaskedCardNumber = creditCardMaskedCardNumber;
        this.creditCardFirstName = creditCardFirstName;
        this.creditCardLastName = creditCardLastName;
        this.creditCardCompanyName = creditCardCompanyName;
        this.creditCardEmail = creditCardEmail;
        this.creditCardPhone = creditCardPhone;
        this.creditCardFax = creditCardFax;
        this.creditCardCustomerTaxId = creditCardCustomerTaxId;
        this.creditCardStreetAddress1 = creditCardStreetAddress1;
        this.creditCardStreetAddress2 = creditCardStreetAddress2;
        this.creditCardCity = creditCardCity;
        this.creditCardState = creditCardState;
        this.creditCardPostalCode = creditCardPostalCode;
        this.creditCardCountryCode = creditCardCountryCode;
        this.creditCardComments = creditCardComments;
        this.authorizationTime = authorizationTime;
        this.authorizationUsername = authorizationUsername;
        this.authorizationPrincipalName = authorizationPrincipalName;
        this.authorizationCommunicationResult = authorizationCommunicationResult;
        this.authorizationProviderErrorCode = authorizationProviderErrorCode;
        this.authorizationErrorCode = authorizationErrorCode;
        this.authorizationProviderErrorMessage = authorizationProviderErrorMessage;
        this.authorizationProviderUniqueId = authorizationProviderUniqueId;
        this.authorizationProviderApprovalResult = authorizationProviderApprovalResult;
        this.authorizationApprovalResult = authorizationApprovalResult;
        this.authorizationProviderDeclineReason = authorizationProviderDeclineReason;
        this.authorizationDeclineReason = authorizationDeclineReason;
        this.authorizationProviderReviewReason = authorizationProviderReviewReason;
        this.authorizationReviewReason = authorizationReviewReason;
        this.authorizationProviderCvvResult = authorizationProviderCvvResult;
        this.authorizationCvvResult = authorizationCvvResult;
        this.authorizationProviderAvsResult = authorizationProviderAvsResult;
        this.authorizationAvsResult = authorizationAvsResult;
        this.authorizationApprovalCode = authorizationApprovalCode;
        this.captureTime = captureTime;
        this.captureUsername = captureUsername;
        this.capturePrincipalName = capturePrincipalName;
        this.captureCommunicationResult = captureCommunicationResult;
        this.captureProviderErrorCode = captureProviderErrorCode;
        this.captureErrorCode = captureErrorCode;
        this.captureProviderErrorMessage = captureProviderErrorMessage;
        this.captureProviderUniqueId = captureProviderUniqueId;
        this.voidTime = voidTime;
        this.voidUsername = voidUsername;
        this.voidPrincipalName = voidPrincipalName;
        this.voidCommunicationResult = voidCommunicationResult;
        this.voidProviderErrorCode = voidProviderErrorCode;
        this.voidErrorCode = voidErrorCode;
        this.voidProviderErrorMessage = voidProviderErrorMessage;
        this.voidProviderUniqueId = voidProviderUniqueId;
        this.status = status;
        intern();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        processorId = intern(processorId);
        accounting = intern(accounting);
        shippingCountryCode = intern(shippingCountryCode);
        creditCardCreatedBy = intern(creditCardCreatedBy);
        creditCardAccounting = intern(creditCardAccounting);
        creditCardCountryCode = intern(creditCardCountryCode);
        authorizationUsername = intern(authorizationUsername);
        authorizationCommunicationResult = intern(authorizationCommunicationResult);
        authorizationErrorCode = intern(authorizationErrorCode);
        authorizationApprovalResult = intern(authorizationApprovalResult);
        authorizationDeclineReason = intern(authorizationDeclineReason);
        authorizationReviewReason = intern(authorizationReviewReason);
        authorizationCvvResult = intern(authorizationCvvResult);
        authorizationAvsResult = intern(authorizationAvsResult);
        captureUsername = intern(captureUsername);
        captureCommunicationResult = intern(captureCommunicationResult);
        captureErrorCode = intern(captureErrorCode);
        voidUsername = intern(voidUsername);
        voidCommunicationResult = intern(voidCommunicationResult);
        voidErrorCode = intern(voidErrorCode);
        status = intern(status);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(CreditCardTransaction other) {
        int diff = accounting.compareTo(other.accounting);
        if(diff!=0) return diff;
        diff = compare(authorizationTime, other.authorizationTime);
        if(diff!=0) return diff;
        return compare(key, other.key);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    public static final MethodColumn COLUMN_PKEY = getMethodColumn(CreditCardTransaction.class, "pkey");
    @SchemaColumn(order=0, index=IndexType.PRIMARY_KEY, description="the unique ID of this transaction")
    public int getPkey() {
        return key;
    }

    public static final MethodColumn COLUMN_PROCESSOR = getMethodColumn(CreditCardTransaction.class, "processor");
    /**
     * Gets the credit card processor used for this transaction.
     */
    @DependencySingleton
    @SchemaColumn(order=1, index=IndexType.INDEXED, description="the name of the processor used for this transaction")
    public CreditCardProcessor getProcessor() throws RemoteException {
        return getConnector().getCreditCardProcessors().get(processorId);
    }

    public static final MethodColumn COLUMN_BUSINESS = getMethodColumn(CreditCardTransaction.class, "business");
    /**
     * For AOServ sub-account support, this is the business that is making the payment.
     * For application-only use (not a sub-account to parent-account payment), use the same business
     * as the owner of the credit card processor.
     */
    @DependencySingleton
    @SchemaColumn(order=2, index=IndexType.INDEXED, description="the accounting code for the source of this transaction")
    public Business getBusiness() throws RemoteException {
        return getConnector().getBusinesses().get(accounting);
    }

    /**
     * Gets the group name for this transaction.  This is an arbitrary accounting name or grouping level.
     */
    @SchemaColumn(order=3, description="the application-provided grouping for this transaction")
    public String getGroupName() {
        return groupName;
    }

    /**
     * Gets the test mode flag.
     */
    @SchemaColumn(order=4, description="indicates this is a test transaction")
    public boolean isTestMode() {
        return testMode;
    }

    /**
     * Gets the duplicate detection window in seconds.
     */
    @SchemaColumn(order=5, description="the number of seconds for duplicate transaction detection")
    public int getDuplicateWindow() {
        return duplicateWindow;
    }

    /**
     * Gets the order number.
     */
    @SchemaColumn(order=6, description="the merchant-provided order number for this transaction")
    public String getOrderNumber() {
        return orderNumber;
    }

    /**
     * Gets the amount of the transaction.  This amount should not include any tax, shipping charges, or duty.
     * Thus the total amount of the transaction is the amount + taxAmount + shippingAmount + dutyAmount.
     */
    @SchemaColumn(order=7, description="the amount of the transaction")
    public Money getAmount() {
        return amount;
    }
    
    /**
     * Gets the tax amount of the transaction.
     */
    @SchemaColumn(order=8, description="the tax amount of the transaction")
    public Money getTaxAmount() {
        return taxAmount;
    }

    /**
     * Gets the taxExempt flag for this transaction.
     */
    @SchemaColumn(order=9, description="the taxExempt flag for this transaction")
    public boolean getTaxExempt() {
        return taxExempt;
    }

    /**
     * Gets the shipping amount for this transaction.
     */
    @SchemaColumn(order=10, description="the shipping amount for this transaction")
    public Money getShippingAmount() {
        return shippingAmount;
    }

    /**
     * Gets the duty amount for this transaction.
     */
    @SchemaColumn(order=11, description="the duty amount for this transaction")
    public Money getDutyAmount() {
        return dutyAmount;
    }

    @SchemaColumn(order=12, description="the shipping first name")
    public String getShippingFirstName() {
        return shippingFirstName;
    }

    @SchemaColumn(order=13, description="the shipping last name")
    public String getShippingLastName() {
        return shippingLastName;
    }

    @SchemaColumn(order=14, description="the shipping company name")
    public String getShippingCompanyName() {
        return shippingCompanyName;
    }

    @SchemaColumn(order=15, description="the shipping address line 1")
    public String getShippingStreetAddress1() {
        return shippingStreetAddress1;
    }

    @SchemaColumn(order=16, description="the shipping address line 2")
    public String getShippingStreetAddress2() {
        return shippingStreetAddress2;
    }

    @SchemaColumn(order=17, description="the shipping city")
    public String getShippingCity() {
        return shippingCity;
    }

    @SchemaColumn(order=18, description="the shipping state")
    public String getShippingState() {
        return shippingState;
    }

    @SchemaColumn(order=19, description="the shipping postal code")
    public String getShippingPostalCode() {
        return shippingPostalCode;
    }

    public static final MethodColumn COLUMN_SHIPPING_COUNTRY_CODE = getMethodColumn(CreditCardTransaction.class, "shippingCountryCode");
    /**
     * Gets the shipping two-digit ISO 3166-1 alpha-2 country code.
     *
     * See <a href="http://en.wikipedia.org/wiki/ISO_3166-1_alpha-2">http://en.wikipedia.org/wiki/ISO_3166-1_alpha-2</a>
     */
    @DependencySingleton
    @SchemaColumn(order=20, index=IndexType.INDEXED, description="the shipping two-digit ISO 3166-1 alpha-2 country code")
    public CountryCode getShippingCountryCode() throws RemoteException {
        if(shippingCountryCode==null) return null;
        return getConnector().getCountryCodes().get(shippingCountryCode);
    }

    @SchemaColumn(order=21, description="the flag indicating the API should generate an email to the customer")
    public boolean getEmailCustomer() {
        return emailCustomer;
    }

    @SchemaColumn(order=22, description="the email address of the merchant")
    public Email getMerchantEmail() {
        return merchantEmail;
    }

    @SchemaColumn(order=23, description="the merchant-provided invoice number")
    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    @SchemaColumn(order=24, description="the merchant-provided purchase order number")
    public String getPurchaseOrderNumber() {
        return purchaseOrderNumber;
    }

    @SchemaColumn(order=25, description="the merchant-provided description of this transaction")
    public String getDescription() {
        return description;
    }

    public static final MethodColumn COLUMN_CREDIT_CARD_CREATED_BY = getMethodColumn(CreditCardTransaction.class, "creditCardCreatedBy");
    @DependencySingleton
    @SchemaColumn(order=26, index=IndexType.INDEXED, description="the business administrator account that provided this credit card")
    public BusinessAdministrator getCreditCardCreatedBy() throws RemoteException {
        return getConnector().getBusinessAdministrators().get(creditCardCreatedBy);
    }

    /**
     * Gets the application-provided principal who added this credit card.
     */
    @SchemaColumn(order=27, description="the application-provided principal who provided this credit card")
    public String getCreditCardPrincipalName() {
        return creditCardPrincipalName;
    }

    public static final MethodColumn COLUMN_CREDIT_CARD_BUSINESS = getMethodColumn(CreditCardTransaction.class, "creditCardBusiness");
    @DependencySingleton
    @SchemaColumn(order=28, index=IndexType.INDEXED, description="the accounting code of the business that provided this credit card")
    public Business getCreditCardBusiness() throws RemoteException {
        return getConnector().getBusinesses().get(creditCardAccounting);
    }

    @SchemaColumn(order=29, description="any application-specific grouping")
    public String getCreditCardGroupName() {
        return creditCardGroupName;
    }

    @SchemaColumn(order=30, description="the unique ID provided by the merchant services provider storage mechanism")
    public String getCreditCardProviderUniqueId() {
        return creditCardProviderUniqueId;
    }

    @SchemaColumn(order=31, description="the masked card number")
    public String getCreditCardMaskedCardNumber() {
        return creditCardMaskedCardNumber;
    }

    @SchemaColumn(order=32, description="the first name of the card holder")
    public String getCreditCardFirstName() {
        return creditCardFirstName;
    }

    @SchemaColumn(order=33, description="the last name of the card holder")
    public String getCreditCardLastName() {
        return creditCardLastName;
    }

    @SchemaColumn(order=34, description="the company name for the credit card")
    public String getCreditCardCompanyName() {
        return creditCardCompanyName;
    }

    @SchemaColumn(order=35, description="the email address of the card holder")
    public Email getCreditCardEmail() {
        return creditCardEmail;
    }

    @SchemaColumn(order=36, description="the phone number of the card holder")
    public String getCreditCardPhone() {
        return creditCardPhone;
    }

    @SchemaColumn(order=37, description="the fax number of the card holder")
    public String getCreditCardFax() {
        return creditCardFax;
    }

    @SchemaColumn(order=38, description="the tax ID of the card holder")
    public String getCreditCardCustomerTaxId() {
        return creditCardCustomerTaxId;
    }

    @SchemaColumn(order=39, description="the street address of the card holder (line 1)")
    public String getCreditCardStreetAddress1() {
        return creditCardStreetAddress1;
    }

    @SchemaColumn(order=40, description="the street address of the card holder (line 2)")
    public String getCreditCardStreetAddress2() {
        return creditCardStreetAddress2;
    }

    @SchemaColumn(order=41, description="the city of the card holder")
    public String getCreditCardCity() {
        return creditCardCity;
    }

    @SchemaColumn(order=42, description="the state/province/prefecture of the card holder")
    public String getCreditCardState() {
        return creditCardState;
    }

    @SchemaColumn(order=43, description="the postal code of the card holder")
    public String getCreditCardPostalCode() {
        return creditCardPostalCode;
    }

    public static final MethodColumn COLUMN_CREDIT_CARD_COUNTRY_CODE = getMethodColumn(CreditCardTransaction.class, "creditCardCountryCode");
    /**
     * Gets the credit card two-digit ISO 3166-1 alpha-2 country code.
     *
     * See <a href="http://en.wikipedia.org/wiki/ISO_3166-1_alpha-2">http://en.wikipedia.org/wiki/ISO_3166-1_alpha-2</a>
     */
    @DependencySingleton
    @SchemaColumn(order=44, index=IndexType.INDEXED, description="the two-digit ISO 3166-1 alpha-2 country code of the card holder")
    public CountryCode getCreditCardCountryCode() throws RemoteException {
        return getConnector().getCountryCodes().get(creditCardCountryCode);
    }

    @SchemaColumn(order=45, description="any comments associated with the credit card")
    public String getCreditCardComments() {
        return creditCardComments;
    }

    /**
     * Gets the time of the authorization.
     */
    @SchemaColumn(order=46, description="the time the authorization was attempted")
    public Timestamp getAuthorizationTime() {
        return new Timestamp(authorizationTime);
    }

    public static final MethodColumn COLUMN_AUTHORIZATION_ADMINISTRATOR = getMethodColumn(CreditCardTransaction.class, "authorizationAdministrator");
    /**
     * Gets the <code>BusinessAdministrator</code> who authorized this transactions.  This is the
     * username of the account that has access to control credit card transactions.
     */
    @DependencySingleton
    @SchemaColumn(order=47, index=IndexType.INDEXED, description="the username of the business_administrator account that processed the transaction")
    public BusinessAdministrator getAuthorizationAdministrator() throws RemoteException {
        if(authorizationUsername==null) return null;
        return getConnector().getBusinessAdministrators().get(authorizationUsername);
    }

    /**
     * Gets the application-specific username who authorized this transaction.  For pure-AOServ data, this
     * will contain the username of the <code>BusinessAdministrator</code> who was logged in and made the payment.
     */
    @SchemaColumn(order=48, description="an application-provided identity of the user who initiated the transaction")
    public String getAuthorizationPrincipalName() {
        return authorizationPrincipalName;
    }

    @SchemaColumn(order=49, description="the authorization communication result")
    public String getAuthorizationCommunicationResult() {
        return authorizationCommunicationResult;
    }

    @SchemaColumn(order=50, description="the provider-specific error code")
    public String getAuthorizationProviderErrorCode() {
        return authorizationProviderErrorCode;
    }

    @SchemaColumn(order=51, description="the provider-neutral error code")
    public String getAuthorizationErrorCode() {
        return authorizationErrorCode;
    }

    @SchemaColumn(order=52, description="the provider-specific error message")
    public String getAuthorizationProviderErrorMessage() {
        return authorizationProviderErrorMessage;
    }

    @SchemaColumn(order=53, description="the per-provider unique ID")
    public String getAuthorizationProviderUniqueId() {
        return authorizationProviderUniqueId;
    }

    @SchemaColumn(order=54, description="the provider-specific approval result")
    public String getAuthorizationProviderApprovalResult() {
        return authorizationProviderApprovalResult;
    }

    @SchemaColumn(order=55, description="the provider-neutral approval result")
    public String getAuthorizationApprovalResult() {
        return authorizationApprovalResult;
    }

    @SchemaColumn(order=56, description="the provider-specific decline reason")
    public String getAuthorizationProviderDeclineReason() {
        return authorizationProviderDeclineReason;
    }

    @SchemaColumn(order=57, description="the provider-neutral decline reason")
    public String getAuthorizationDeclineReason() {
        return authorizationDeclineReason;
    }

    @SchemaColumn(order=58, description="the provider-specific review reason")
    public String getAuthorizationProviderReviewReason() {
        return authorizationProviderReviewReason;
    }

    @SchemaColumn(order=59, description="the provider-neutral review reason")
    public String getAuthorizationReviewReason() {
        return authorizationReviewReason;
    }

    @SchemaColumn(order=60, description="the provider-specific CVV result")
    public String getAuthorizationProviderCvvResult() {
        return authorizationProviderCvvResult;
    }

    @SchemaColumn(order=61, description="the provider-neutral CVV result")
    public String getAuthorizationCvvResult() {
        return authorizationCvvResult;
    }

    @SchemaColumn(order=62, description="the provider-specific AVS result")
    public String getAuthorizationProviderAvsResult() {
        return authorizationProviderAvsResult;
    }

    @SchemaColumn(order=63, description="the provider-neutral AVS result")
    public String getAuthorizationAvsResult() {
        return authorizationAvsResult;
    }

    @SchemaColumn(order=64, description="the approval code")
    public String getAuthorizationApprovalCode() {
        return authorizationApprovalCode;
    }

    /**
     * Gets the time of the capture.
     */
    @SchemaColumn(order=65, description="the time the capture was attempted")
    public Timestamp getCaptureTime() {
        return captureTime==null ? null : new Timestamp(captureTime);
    }

    public static final MethodColumn COLUMN_CAPTURE_ADMINISTRATOR = getMethodColumn(CreditCardTransaction.class, "captureAdministrator");
    /**
     * Gets the <code>BusinessAdministrator</code> who captured this transactions.  This is the
     * username of the account that has access to control credit card transactions.
     */
    @DependencySingleton
    @SchemaColumn(order=66, index=IndexType.INDEXED, description="the username of the business_administrator account that processed the capture")
    public BusinessAdministrator getCaptureAdministrator() throws RemoteException {
        if(captureUsername==null) return null;
        return getConnector().getBusinessAdministrators().get(captureUsername);
    }

    /**
     * Gets the application-specific username who captured this transaction.  For pure-AOServ data, this
     * will contain the username of the <code>BusinessAdministrator</code> who was logged in and initiated the capture.
     */
    @SchemaColumn(order=67, description="an application-provided identity of the user who initiated the capture")
    public String getCapturePrincipalName() {
        return capturePrincipalName;
    }

    @SchemaColumn(order=68, description="the capture communication result")
    public String getCaptureCommunicationResult() {
        return captureCommunicationResult;
    }

    @SchemaColumn(order=69, description="the provider-specific error code")
    public String getCaptureProviderErrorCode() {
        return captureProviderErrorCode;
    }

    @SchemaColumn(order=70, description="the provider-neutral error code")
    public String getCaptureErrorCode() {
        return captureErrorCode;
    }

    @SchemaColumn(order=71, description="the provider-specific error message")
    public String getCaptureProviderErrorMessage() {
        return captureProviderErrorMessage;
    }

    @SchemaColumn(order=72, description="the per-provider unique ID")
    public String getCaptureProviderUniqueId() {
        return captureProviderUniqueId;
    }

    /**
     * Gets the time of the void.
     */
    @SchemaColumn(order=73, description="the time the void was attempted")
    public Timestamp getVoidTime() {
        return voidTime==null ? null : new Timestamp(voidTime);
    }

    public static final MethodColumn COLUMN_VOID_ADMINISTRATOR = getMethodColumn(CreditCardTransaction.class, "voidAdministrator");
    /**
     * Gets the <code>BusinessAdministrator</code> who voided this transactions.  This is the
     * username of the account that has access to control credit card transactions.
     */
    @DependencySingleton
    @SchemaColumn(order=74, index=IndexType.INDEXED, description="the username of the business_administrator account that processed the void")
    public BusinessAdministrator getVoidAdministrator() throws RemoteException {
        if(voidUsername==null) return null;
        return getConnector().getBusinessAdministrators().get(voidUsername);
    }

    /**
     * Gets the application-specific username who voided this transaction.  For pure-AOServ data, this
     * will contain the username of the <code>BusinessAdministrator</code> who was logged in and caused the void.
     */
    @SchemaColumn(order=75, description="an application-provided identity of the user who initiated the void")
    public String getVoidPrincipalName() {
        return voidPrincipalName;
    }

    @SchemaColumn(order=76, description="the void communication result")
    public String getVoidCommunicationResult() {
        return voidCommunicationResult;
    }

    @SchemaColumn(order=77, description="the provider-specific error code")
    public String getVoidProviderErrorCode() {
        return voidProviderErrorCode;
    }

    @SchemaColumn(order=78, description="the provider-neutral error code")
    public String getVoidErrorCode() {
        return voidErrorCode;
    }

    @SchemaColumn(order=79, description="the provider-specific error message")
    public String getVoidProviderErrorMessage() {
        return voidProviderErrorMessage;
    }

    @SchemaColumn(order=80, description="the per-provider unique ID")
    public String getVoidProviderUniqueId() {
        return voidProviderUniqueId;
    }

    @SchemaColumn(order=81, description="the status of the transaction")
    public String getStatus() {
        return status;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public CreditCardTransaction(AOServConnector connector, com.aoindustries.aoserv.client.dto.CreditCardTransaction dto) throws ValidationException {
        this(
            connector,
            dto.getPkey(),
            dto.getProcessorId(),
            getAccountingCode(dto.getAccounting()),
            dto.getGroupName(),
            dto.isTestMode(),
            dto.getDuplicateWindow(),
            dto.getOrderNumber(),
            getMoney(dto.getAmount()),
            getMoney(dto.getTaxAmount()),
            dto.isTaxExempt(),
            getMoney(dto.getShippingAmount()),
            getMoney(dto.getDutyAmount()),
            dto.getShippingFirstName(),
            dto.getShippingLastName(),
            dto.getShippingCompanyName(),
            dto.getShippingStreetAddress1(),
            dto.getShippingStreetAddress2(),
            dto.getShippingCity(),
            dto.getShippingState(),
            dto.getShippingPostalCode(),
            dto.getShippingCountryCode(),
            dto.isEmailCustomer(),
            getEmail(dto.getMerchantEmail()),
            dto.getInvoiceNumber(),
            dto.getPurchaseOrderNumber(),
            dto.getDescription(),
            getUserId(dto.getCreditCardCreatedBy()),
            dto.getCreditCardPrincipalName(),
            getAccountingCode(dto.getCreditCardAccounting()),
            dto.getCreditCardGroupName(),
            dto.getCreditCardProviderUniqueId(),
            dto.getCreditCardMaskedCardNumber(),
            dto.getCreditCardFirstName(),
            dto.getCreditCardLastName(),
            dto.getCreditCardCompanyName(),
            getEmail(dto.getCreditCardEmail()),
            dto.getCreditCardPhone(),
            dto.getCreditCardFax(),
            dto.getCreditCardCustomerTaxId(),
            dto.getCreditCardStreetAddress1(),
            dto.getCreditCardStreetAddress2(),
            dto.getCreditCardCity(),
            dto.getCreditCardState(),
            dto.getCreditCardPostalCode(),
            dto.getCreditCardCountryCode(),
            dto.getCreditCardComments(),
            getTimeMillis(dto.getAuthorizationTime()),
            getUserId(dto.getAuthorizationUsername()),
            dto.getAuthorizationPrincipalName(),
            dto.getAuthorizationCommunicationResult(),
            dto.getAuthorizationProviderErrorCode(),
            dto.getAuthorizationErrorCode(),
            dto.getAuthorizationProviderErrorMessage(),
            dto.getAuthorizationProviderUniqueId(),
            dto.getAuthorizationProviderApprovalResult(),
            dto.getAuthorizationApprovalResult(),
            dto.getAuthorizationProviderDeclineReason(),
            dto.getAuthorizationDeclineReason(),
            dto.getAuthorizationProviderReviewReason(),
            dto.getAuthorizationReviewReason(),
            dto.getAuthorizationProviderCvvResult(),
            dto.getAuthorizationCvvResult(),
            dto.getAuthorizationProviderAvsResult(),
            dto.getAuthorizationAvsResult(),
            dto.getAuthorizationApprovalCode(),
            getTimeMillis(dto.getCaptureTime()),
            getUserId(dto.getCaptureUsername()),
            dto.getCapturePrincipalName(),
            dto.getCaptureCommunicationResult(),
            dto.getCaptureProviderErrorCode(),
            dto.getCaptureErrorCode(),
            dto.getCaptureProviderErrorMessage(),
            dto.getCaptureProviderUniqueId(),
            getTimeMillis(dto.getVoidTime()),
            getUserId(dto.getVoidUsername()),
            dto.getVoidPrincipalName(),
            dto.getVoidCommunicationResult(),
            dto.getVoidProviderErrorCode(),
            dto.getVoidErrorCode(),
            dto.getVoidProviderErrorMessage(),
            dto.getVoidProviderUniqueId(),
            dto.getStatus()
        );
    }

    @Override
    public com.aoindustries.aoserv.client.dto.CreditCardTransaction getDto() {
        return new com.aoindustries.aoserv.client.dto.CreditCardTransaction(
            key,
            processorId,
            getDto(accounting),
            groupName,
            testMode,
            duplicateWindow,
            orderNumber,
            getDto(amount),
            getDto(taxAmount),
            taxExempt,
            getDto(shippingAmount),
            getDto(dutyAmount),
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
            getDto(merchantEmail),
            invoiceNumber,
            purchaseOrderNumber,
            description,
            getDto(creditCardCreatedBy),
            creditCardPrincipalName,
            getDto(creditCardAccounting),
            creditCardGroupName,
            creditCardProviderUniqueId,
            creditCardMaskedCardNumber,
            creditCardFirstName,
            creditCardLastName,
            creditCardCompanyName,
            getDto(creditCardEmail),
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
            getDto(authorizationUsername),
            authorizationPrincipalName,
            authorizationCommunicationResult,
            authorizationProviderErrorCode,
            authorizationErrorCode,
            authorizationProviderErrorMessage,
            authorizationProviderUniqueId,
            authorizationProviderApprovalResult,
            authorizationApprovalResult,
            authorizationProviderDeclineReason,
            authorizationDeclineReason,
            authorizationProviderReviewReason,
            authorizationReviewReason,
            authorizationProviderCvvResult,
            authorizationCvvResult,
            authorizationProviderAvsResult,
            authorizationAvsResult,
            authorizationApprovalCode,
            captureTime,
            getDto(captureUsername),
            capturePrincipalName,
            captureCommunicationResult,
            captureProviderErrorCode,
            captureErrorCode,
            captureProviderErrorMessage,
            captureProviderUniqueId,
            voidTime,
            getDto(voidUsername),
            voidPrincipalName,
            voidCommunicationResult,
            voidProviderErrorCode,
            voidErrorCode,
            voidProviderErrorMessage,
            voidProviderUniqueId,
            status
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    @DependentObjectSingleton
    public Transaction getTransaction() throws RemoteException {
        return getConnector().getTransactions().filterUnique(Transaction.COLUMN_CREDIT_CARD_TRANSACTION, this);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
    /**
     * Called when a sale (combined authorization and capture) has been completed.
     */
    /* TODO
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
    ) throws RemoteException {
        if(!getConnector().isSecure()) throw new IOException("Credit card transactions may only be updated when using secure protocols.  Currently using the "+getConnector().getProtocol()+" protocol, which is not secure.");

        getConnector().requestUpdate(
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

                public void readResponse(CompressedDataInputStream in) throws RemoteException {
                    int code=in.readByte();
                    if(code==AOServProtocol.DONE) {
                        invalidateList=AOServConnector.readInvalidateList(in);
                    } else {
                        AOServProtocol.checkResult(code, in);
                        throw new IOException("Unknown response code: "+code);
                    }
                }

                public void afterRelease() {
                    getConnector().tablesUpdated(invalidateList);
                }
            }
        );
    }
     */
    /**
     * Called when an authorization has been completed.
     */
    /* TODO
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
    ) throws RemoteException {
        if(!getConnector().isSecure()) throw new IOException("Credit card transactions may only be updated when using secure protocols.  Currently using the "+getConnector().getProtocol()+" protocol, which is not secure.");

        getConnector().requestUpdate(
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

                public void readResponse(CompressedDataInputStream in) throws RemoteException {
                    int code=in.readByte();
                    if(code==AOServProtocol.DONE) {
                        invalidateList=AOServConnector.readInvalidateList(in);
                    } else {
                        AOServProtocol.checkResult(code, in);
                        throw new IOException("Unknown response code: "+code);
                    }
                }

                public void afterRelease() {
                    getConnector().tablesUpdated(invalidateList);
                }
            }
        );
    }
     */
    // </editor-fold>
}
