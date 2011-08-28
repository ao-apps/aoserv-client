/*
 * Copyright 2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.command;

import com.aoindustries.aoserv.client.*;
import com.aoindustries.aoserv.client.validator.*;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Adds a transaction in the pending state.
 *
 * @author  AO Industries, Inc.
 */
final public class AddCreditCardTransactionCommand extends RemoteCommand<Integer> {

    private static final long serialVersionUID = 1L;

    final private String processor;
    final private AccountingCode business;
    final private String groupName;
    final private boolean testMode;
    final private int duplicateWindow;
    final private String orderNumber;
    final private String currencyCode;
    final private BigDecimal amount;
    final private BigDecimal taxAmount;
    final private boolean taxExempt;
    final private BigDecimal shippingAmount;
    final private BigDecimal dutyAmount;
    final private String shippingFirstName;
    final private String shippingLastName;
    final private String shippingCompanyName;
    final private String shippingStreetAddress1;
    final private String shippingStreetAddress2;
    final private String shippingCity;
    final private String shippingState;
    final private String shippingPostalCode;
    final private String shippingCountryCode;
    final private boolean emailCustomer;
    final private String merchantEmail;
    final private String invoiceNumber;
    final private String purchaseOrderNumber;
    final private String description;
    final private UserId creditCardCreatedBy;
    final private String creditCardPrincipalName;
    final private AccountingCode creditCardAccounting;
    final private String creditCardGroupName;
    final private String creditCardProviderUniqueId;
    final private String creditCardMaskedCardNumber;
    final private String creditCardFirstName;
    final private String creditCardLastName;
    final private String creditCardCompanyName;
    final private String creditCardEmail;
    final private String creditCardPhone;
    final private String creditCardFax;
    final private String creditCardCustomerTaxId;
    final private String creditCardStreetAddress1;
    final private String creditCardStreetAddress2;
    final private String creditCardCity;
    final private String creditCardState;
    final private String creditCardPostalCode;
    final private String creditCardCountryCode;
    final private String creditCardComments;
    final private Timestamp authorizationTime;
    final private String authorizationPrincipalName;

    public AddCreditCardTransactionCommand(
        @Param(name="processor") CreditCardProcessor processor,
        @Param(name="business") Business business,
        @Param(name="groupName", nullable=true) String groupName,
        @Param(name="testMode") boolean testMode,
        @Param(name="duplicateWindow") int duplicateWindow,
        @Param(name="orderNumber", nullable=true) String orderNumber,
        @Param(name="currencyCode") String currencyCode,
        @Param(name="amount") BigDecimal amount,
        @Param(name="taxAmount", nullable=true) BigDecimal taxAmount,
        @Param(name="taxExempt") boolean taxExempt,
        @Param(name="shippingAmount", nullable=true) BigDecimal shippingAmount,
        @Param(name="dutyAmount", nullable=true) BigDecimal dutyAmount,
        @Param(name="shippingFirstName", nullable=true) String shippingFirstName,
        @Param(name="shippingLastName", nullable=true) String shippingLastName,
        @Param(name="shippingCompanyName", nullable=true) String shippingCompanyName,
        @Param(name="shippingStreetAddress1", nullable=true) String shippingStreetAddress1,
        @Param(name="shippingStreetAddress2", nullable=true) String shippingStreetAddress2,
        @Param(name="shippingCity", nullable=true) String shippingCity,
        @Param(name="shippingState", nullable=true) String shippingState,
        @Param(name="shippingPostalCode", nullable=true) String shippingPostalCode,
        @Param(name="shippingCountryCode", nullable=true) String shippingCountryCode,
        @Param(name="emailCustomer") boolean emailCustomer,
        @Param(name="merchantEmail", nullable=true) String merchantEmail,
        @Param(name="invoiceNumber", nullable=true) String invoiceNumber,
        @Param(name="purchaseOrderNumber", nullable=true) String purchaseOrderNumber,
        @Param(name="description", nullable=true) String description,
        @Param(name="creditCardCreatedBy") BusinessAdministrator creditCardCreatedBy,
        @Param(name="creditCardPrincipalName", nullable=true) String creditCardPrincipalName,
        @Param(name="creditCardAccounting") Business creditCardAccounting,
        @Param(name="creditCardGroupName", nullable=true) String creditCardGroupName,
        @Param(name="creditCardProviderUniqueId", nullable=true) String creditCardProviderUniqueId,
        @Param(name="creditCardMaskedCardNumber") String creditCardMaskedCardNumber,
        @Param(name="creditCardFirstName") String creditCardFirstName,
        @Param(name="creditCardLastName") String creditCardLastName,
        @Param(name="creditCardCompanyName", nullable=true) String creditCardCompanyName,
        @Param(name="creditCardEmail", nullable=true) String creditCardEmail,
        @Param(name="creditCardPhone", nullable=true) String creditCardPhone,
        @Param(name="creditCardFax", nullable=true) String creditCardFax,
        @Param(name="creditCardCustomerTaxId", nullable=true) String creditCardCustomerTaxId,
        @Param(name="creditCardStreetAddress1") String creditCardStreetAddress1,
        @Param(name="creditCardStreetAddress2", nullable=true) String creditCardStreetAddress2,
        @Param(name="creditCardCity") String creditCardCity,
        @Param(name="creditCardState", nullable=true) String creditCardState,
        @Param(name="creditCardPostalCode", nullable=true) String creditCardPostalCode,
        @Param(name="creditCardCountryCode") String creditCardCountryCode,
        @Param(name="creditCardComments", nullable=true) String creditCardComments,
        @Param(name="authorizationTime") Timestamp authorizationTime,
        @Param(name="authorizationPrincipalName", nullable=true) String authorizationPrincipalName
    ) {
        this.processor = processor.getProviderId();
        this.business = business.getAccounting();
        this.groupName = nullIfEmpty(groupName);
        this.testMode = testMode;
        this.duplicateWindow = duplicateWindow;
        this.orderNumber = orderNumber;
        this.currencyCode = currencyCode;
        this.amount = amount;
        this.taxAmount = taxAmount;
        this.taxExempt = taxExempt;
        this.shippingAmount = shippingAmount;
        this.dutyAmount = dutyAmount;
        this.shippingFirstName = nullIfEmpty(shippingFirstName);
        this.shippingLastName = nullIfEmpty(shippingLastName);
        this.shippingCompanyName = nullIfEmpty(shippingCompanyName);
        this.shippingStreetAddress1 = nullIfEmpty(shippingStreetAddress1);
        this.shippingStreetAddress2 = nullIfEmpty(shippingStreetAddress2);
        this.shippingCity = nullIfEmpty(shippingCity);
        this.shippingState = nullIfEmpty(shippingState);
        this.shippingPostalCode = nullIfEmpty(shippingPostalCode);
        this.shippingCountryCode = nullIfEmpty(shippingCountryCode);
        this.emailCustomer = emailCustomer;
        this.merchantEmail = nullIfEmpty(merchantEmail);
        this.invoiceNumber = nullIfEmpty(invoiceNumber);
        this.purchaseOrderNumber = nullIfEmpty(purchaseOrderNumber);
        this.description = nullIfEmpty(description);
        this.creditCardCreatedBy = creditCardCreatedBy.getUserId();
        this.creditCardPrincipalName = nullIfEmpty(creditCardPrincipalName);
        this.creditCardAccounting = creditCardAccounting.getAccounting();
        this.creditCardGroupName = nullIfEmpty(creditCardGroupName);
        this.creditCardProviderUniqueId = nullIfEmpty(creditCardProviderUniqueId);
        this.creditCardMaskedCardNumber = creditCardMaskedCardNumber;
        this.creditCardFirstName = creditCardFirstName;
        this.creditCardLastName = creditCardLastName;
        this.creditCardCompanyName = nullIfEmpty(creditCardCompanyName);
        this.creditCardEmail = nullIfEmpty(creditCardEmail);
        this.creditCardPhone = nullIfEmpty(creditCardPhone);
        this.creditCardFax = nullIfEmpty(creditCardFax);
        this.creditCardCustomerTaxId = nullIfEmpty(creditCardCustomerTaxId);
        this.creditCardStreetAddress1 = creditCardStreetAddress1;
        this.creditCardStreetAddress2 = nullIfEmpty(creditCardStreetAddress2);
        this.creditCardCity = creditCardCity;
        this.creditCardState = creditCardState;
        this.creditCardPostalCode = creditCardPostalCode;
        this.creditCardCountryCode = creditCardCountryCode;
        this.creditCardComments = nullIfEmpty(creditCardComments);
        this.authorizationTime = authorizationTime;
        this.authorizationPrincipalName = nullIfEmpty(authorizationPrincipalName);
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    public String getProcessor() {
        return processor;
    }

    public AccountingCode getBusiness() {
        return business;
    }

    public String getGroupName() {
        return groupName;
    }

    public boolean isTestMode() {
        return testMode;
    }

    public int getDuplicateWindow() {
        return duplicateWindow;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public boolean isTaxExempt() {
        return taxExempt;
    }

    public BigDecimal getShippingAmount() {
        return shippingAmount;
    }

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

    public String getShippingCountryCode() {
        return shippingCountryCode;
    }

    public boolean isEmailCustomer() {
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

    public String getDescription() {
        return description;
    }

    public UserId getCreditCardCreatedBy() {
        return creditCardCreatedBy;
    }

    public String getCreditCardPrincipalName() {
        return creditCardPrincipalName;
    }

    public AccountingCode getCreditCardAccounting() {
        return creditCardAccounting;
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

    public String getCreditCardCountryCode() {
        return creditCardCountryCode;
    }

    public String getCreditCardComments() {
        return creditCardComments;
    }

    public Timestamp getAuthorizationTime() {
        return authorizationTime;
    }

    public String getAuthorizationPrincipalName() {
        return authorizationPrincipalName;
    }

    @Override
    protected Map<String,List<String>> checkCommand(AOServConnector userConn, AOServConnector rootConn, BusinessAdministrator rootUser) throws RemoteException {
        Map<String,List<String>> errors = Collections.emptyMap();
        // TODO
        // if(!connector.isSecure()) throw new IOException("Credit card transactions may only be added when using secure protocols.  Currently using the "+connector.getProtocol()+" protocol, which is not secure.");
        return errors;
    }
}
