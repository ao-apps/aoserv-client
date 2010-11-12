/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

import java.util.Calendar;

/**
 * @author  AO Industries, Inc.
 */
public class CreditCardTransaction {

    private int pkey;
    private String processorId;
    private AccountingCode accounting;
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
    private UserId creditCardCreatedBy;
    private String creditCardPrincipalName;
    private AccountingCode creditCardAccounting;
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
    private UserId authorizationUsername;
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
    private Long captureTime;
    private UserId captureUsername;
    private String capturePrincipalName;
    private String captureCommunicationResult;
    private String captureProviderErrorCode;
    private String captureErrorCode;
    private String captureProviderErrorMessage;
    private String captureProviderUniqueId;
    private Long voidTime;
    private UserId voidUsername;
    private String voidPrincipalName;
    private String voidCommunicationResult;
    private String voidProviderErrorCode;
    private String voidErrorCode;
    private String voidProviderErrorMessage;
    private String voidProviderUniqueId;
    private String status;

    public CreditCardTransaction() {
    }

    public CreditCardTransaction(int pkey, String processorId, AccountingCode accounting, String groupName, boolean testMode, int duplicateWindow, String orderNumber, Money amount, Money taxAmount, boolean taxExempt, Money shippingAmount, Money dutyAmount, String shippingFirstName, String shippingLastName, String shippingCompanyName, String shippingStreetAddress1, String shippingStreetAddress2, String shippingCity, String shippingState, String shippingPostalCode, String shippingCountryCode, boolean emailCustomer, Email merchantEmail, String invoiceNumber, String purchaseOrderNumber, String description, UserId creditCardCreatedBy, String creditCardPrincipalName, AccountingCode creditCardAccounting, String creditCardGroupName, String creditCardProviderUniqueId, String creditCardMaskedCardNumber, String creditCardFirstName, String creditCardLastName, String creditCardCompanyName, Email creditCardEmail, String creditCardPhone, String creditCardFax, String creditCardCustomerTaxId, String creditCardStreetAddress1, String creditCardStreetAddress2, String creditCardCity, String creditCardState, String creditCardPostalCode, String creditCardCountryCode, String creditCardComments, long authorizationTime, UserId authorizationUsername, String authorizationPrincipalName, String authorizationCommunicationResult, String authorizationProviderErrorCode, String authorizationErrorCode, String authorizationProviderErrorMessage, String authorizationProviderUniqueId, String authorizationProviderApprovalResult, String authorizationApprovalResult, String authorizationProviderDeclineReason, String authorizationDeclineReason, String authorizationProviderReviewReason, String authorizationReviewReason, String authorizationProviderCvvResult, String authorizationCvvResult, String authorizationProviderAvsResult, String authorizationAvsResult, String authorizationApprovalCode, Long captureTime, UserId captureUsername, String capturePrincipalName, String captureCommunicationResult, String captureProviderErrorCode, String captureErrorCode, String captureProviderErrorMessage, String captureProviderUniqueId, Long voidTime, UserId voidUsername, String voidPrincipalName, String voidCommunicationResult, String voidProviderErrorCode, String voidErrorCode, String voidProviderErrorMessage, String voidProviderUniqueId, String status) {
        this.pkey = pkey;
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
    }

    public int getPkey() {
        return pkey;
    }

    public void setPkey(int pkey) {
        this.pkey = pkey;
    }

    public String getProcessorId() {
        return processorId;
    }

    public void setProcessorId(String processorId) {
        this.processorId = processorId;
    }

    public AccountingCode getAccounting() {
        return accounting;
    }

    public void setAccounting(AccountingCode accounting) {
        this.accounting = accounting;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public boolean isTestMode() {
        return testMode;
    }

    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }

    public int getDuplicateWindow() {
        return duplicateWindow;
    }

    public void setDuplicateWindow(int duplicateWindow) {
        this.duplicateWindow = duplicateWindow;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Money getAmount() {
        return amount;
    }

    public void setAmount(Money amount) {
        this.amount = amount;
    }

    public Money getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(Money taxAmount) {
        this.taxAmount = taxAmount;
    }

    public boolean isTaxExempt() {
        return taxExempt;
    }

    public void setTaxExempt(boolean taxExempt) {
        this.taxExempt = taxExempt;
    }

    public Money getShippingAmount() {
        return shippingAmount;
    }

    public void setShippingAmount(Money shippingAmount) {
        this.shippingAmount = shippingAmount;
    }

    public Money getDutyAmount() {
        return dutyAmount;
    }

    public void setDutyAmount(Money dutyAmount) {
        this.dutyAmount = dutyAmount;
    }

    public String getShippingFirstName() {
        return shippingFirstName;
    }

    public void setShippingFirstName(String shippingFirstName) {
        this.shippingFirstName = shippingFirstName;
    }

    public String getShippingLastName() {
        return shippingLastName;
    }

    public void setShippingLastName(String shippingLastName) {
        this.shippingLastName = shippingLastName;
    }

    public String getShippingCompanyName() {
        return shippingCompanyName;
    }

    public void setShippingCompanyName(String shippingCompanyName) {
        this.shippingCompanyName = shippingCompanyName;
    }

    public String getShippingStreetAddress1() {
        return shippingStreetAddress1;
    }

    public void setShippingStreetAddress1(String shippingStreetAddress1) {
        this.shippingStreetAddress1 = shippingStreetAddress1;
    }

    public String getShippingStreetAddress2() {
        return shippingStreetAddress2;
    }

    public void setShippingStreetAddress2(String shippingStreetAddress2) {
        this.shippingStreetAddress2 = shippingStreetAddress2;
    }

    public String getShippingCity() {
        return shippingCity;
    }

    public void setShippingCity(String shippingCity) {
        this.shippingCity = shippingCity;
    }

    public String getShippingState() {
        return shippingState;
    }

    public void setShippingState(String shippingState) {
        this.shippingState = shippingState;
    }

    public String getShippingPostalCode() {
        return shippingPostalCode;
    }

    public void setShippingPostalCode(String shippingPostalCode) {
        this.shippingPostalCode = shippingPostalCode;
    }

    public String getShippingCountryCode() {
        return shippingCountryCode;
    }

    public void setShippingCountryCode(String shippingCountryCode) {
        this.shippingCountryCode = shippingCountryCode;
    }

    public boolean isEmailCustomer() {
        return emailCustomer;
    }

    public void setEmailCustomer(boolean emailCustomer) {
        this.emailCustomer = emailCustomer;
    }

    public Email getMerchantEmail() {
        return merchantEmail;
    }

    public void setMerchantEmail(Email merchantEmail) {
        this.merchantEmail = merchantEmail;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getPurchaseOrderNumber() {
        return purchaseOrderNumber;
    }

    public void setPurchaseOrderNumber(String purchaseOrderNumber) {
        this.purchaseOrderNumber = purchaseOrderNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UserId getCreditCardCreatedBy() {
        return creditCardCreatedBy;
    }

    public void setCreditCardCreatedBy(UserId creditCardCreatedBy) {
        this.creditCardCreatedBy = creditCardCreatedBy;
    }

    public String getCreditCardPrincipalName() {
        return creditCardPrincipalName;
    }

    public void setCreditCardPrincipalName(String creditCardPrincipalName) {
        this.creditCardPrincipalName = creditCardPrincipalName;
    }

    public AccountingCode getCreditCardAccounting() {
        return creditCardAccounting;
    }

    public void setCreditCardAccounting(AccountingCode creditCardAccounting) {
        this.creditCardAccounting = creditCardAccounting;
    }

    public String getCreditCardGroupName() {
        return creditCardGroupName;
    }

    public void setCreditCardGroupName(String creditCardGroupName) {
        this.creditCardGroupName = creditCardGroupName;
    }

    public String getCreditCardProviderUniqueId() {
        return creditCardProviderUniqueId;
    }

    public void setCreditCardProviderUniqueId(String creditCardProviderUniqueId) {
        this.creditCardProviderUniqueId = creditCardProviderUniqueId;
    }

    public String getCreditCardMaskedCardNumber() {
        return creditCardMaskedCardNumber;
    }

    public void setCreditCardMaskedCardNumber(String creditCardMaskedCardNumber) {
        this.creditCardMaskedCardNumber = creditCardMaskedCardNumber;
    }

    public String getCreditCardFirstName() {
        return creditCardFirstName;
    }

    public void setCreditCardFirstName(String creditCardFirstName) {
        this.creditCardFirstName = creditCardFirstName;
    }

    public String getCreditCardLastName() {
        return creditCardLastName;
    }

    public void setCreditCardLastName(String creditCardLastName) {
        this.creditCardLastName = creditCardLastName;
    }

    public String getCreditCardCompanyName() {
        return creditCardCompanyName;
    }

    public void setCreditCardCompanyName(String creditCardCompanyName) {
        this.creditCardCompanyName = creditCardCompanyName;
    }

    public Email getCreditCardEmail() {
        return creditCardEmail;
    }

    public void setCreditCardEmail(Email creditCardEmail) {
        this.creditCardEmail = creditCardEmail;
    }

    public String getCreditCardPhone() {
        return creditCardPhone;
    }

    public void setCreditCardPhone(String creditCardPhone) {
        this.creditCardPhone = creditCardPhone;
    }

    public String getCreditCardFax() {
        return creditCardFax;
    }

    public void setCreditCardFax(String creditCardFax) {
        this.creditCardFax = creditCardFax;
    }

    public String getCreditCardCustomerTaxId() {
        return creditCardCustomerTaxId;
    }

    public void setCreditCardCustomerTaxId(String creditCardCustomerTaxId) {
        this.creditCardCustomerTaxId = creditCardCustomerTaxId;
    }

    public String getCreditCardStreetAddress1() {
        return creditCardStreetAddress1;
    }

    public void setCreditCardStreetAddress1(String creditCardStreetAddress1) {
        this.creditCardStreetAddress1 = creditCardStreetAddress1;
    }

    public String getCreditCardStreetAddress2() {
        return creditCardStreetAddress2;
    }

    public void setCreditCardStreetAddress2(String creditCardStreetAddress2) {
        this.creditCardStreetAddress2 = creditCardStreetAddress2;
    }

    public String getCreditCardCity() {
        return creditCardCity;
    }

    public void setCreditCardCity(String creditCardCity) {
        this.creditCardCity = creditCardCity;
    }

    public String getCreditCardState() {
        return creditCardState;
    }

    public void setCreditCardState(String creditCardState) {
        this.creditCardState = creditCardState;
    }

    public String getCreditCardPostalCode() {
        return creditCardPostalCode;
    }

    public void setCreditCardPostalCode(String creditCardPostalCode) {
        this.creditCardPostalCode = creditCardPostalCode;
    }

    public String getCreditCardCountryCode() {
        return creditCardCountryCode;
    }

    public void setCreditCardCountryCode(String creditCardCountryCode) {
        this.creditCardCountryCode = creditCardCountryCode;
    }

    public String getCreditCardComments() {
        return creditCardComments;
    }

    public void setCreditCardComments(String creditCardComments) {
        this.creditCardComments = creditCardComments;
    }

    public Calendar getAuthorizationTime() {
        return DtoUtils.getCalendar(authorizationTime);
    }

    public void setAuthorizationTime(Calendar authorizationTime) {
        this.authorizationTime = authorizationTime.getTimeInMillis();
    }

    public UserId getAuthorizationUsername() {
        return authorizationUsername;
    }

    public void setAuthorizationUsername(UserId authorizationUsername) {
        this.authorizationUsername = authorizationUsername;
    }

    public String getAuthorizationPrincipalName() {
        return authorizationPrincipalName;
    }

    public void setAuthorizationPrincipalName(String authorizationPrincipalName) {
        this.authorizationPrincipalName = authorizationPrincipalName;
    }

    public String getAuthorizationCommunicationResult() {
        return authorizationCommunicationResult;
    }

    public void setAuthorizationCommunicationResult(String authorizationCommunicationResult) {
        this.authorizationCommunicationResult = authorizationCommunicationResult;
    }

    public String getAuthorizationProviderErrorCode() {
        return authorizationProviderErrorCode;
    }

    public void setAuthorizationProviderErrorCode(String authorizationProviderErrorCode) {
        this.authorizationProviderErrorCode = authorizationProviderErrorCode;
    }

    public String getAuthorizationErrorCode() {
        return authorizationErrorCode;
    }

    public void setAuthorizationErrorCode(String authorizationErrorCode) {
        this.authorizationErrorCode = authorizationErrorCode;
    }

    public String getAuthorizationProviderErrorMessage() {
        return authorizationProviderErrorMessage;
    }

    public void setAuthorizationProviderErrorMessage(String authorizationProviderErrorMessage) {
        this.authorizationProviderErrorMessage = authorizationProviderErrorMessage;
    }

    public String getAuthorizationProviderUniqueId() {
        return authorizationProviderUniqueId;
    }

    public void setAuthorizationProviderUniqueId(String authorizationProviderUniqueId) {
        this.authorizationProviderUniqueId = authorizationProviderUniqueId;
    }

    public String getAuthorizationProviderApprovalResult() {
        return authorizationProviderApprovalResult;
    }

    public void setAuthorizationProviderApprovalResult(String authorizationProviderApprovalResult) {
        this.authorizationProviderApprovalResult = authorizationProviderApprovalResult;
    }

    public String getAuthorizationApprovalResult() {
        return authorizationApprovalResult;
    }

    public void setAuthorizationApprovalResult(String authorizationApprovalResult) {
        this.authorizationApprovalResult = authorizationApprovalResult;
    }

    public String getAuthorizationProviderDeclineReason() {
        return authorizationProviderDeclineReason;
    }

    public void setAuthorizationProviderDeclineReason(String authorizationProviderDeclineReason) {
        this.authorizationProviderDeclineReason = authorizationProviderDeclineReason;
    }

    public String getAuthorizationDeclineReason() {
        return authorizationDeclineReason;
    }

    public void setAuthorizationDeclineReason(String authorizationDeclineReason) {
        this.authorizationDeclineReason = authorizationDeclineReason;
    }

    public String getAuthorizationProviderReviewReason() {
        return authorizationProviderReviewReason;
    }

    public void setAuthorizationProviderReviewReason(String authorizationProviderReviewReason) {
        this.authorizationProviderReviewReason = authorizationProviderReviewReason;
    }

    public String getAuthorizationReviewReason() {
        return authorizationReviewReason;
    }

    public void setAuthorizationReviewReason(String authorizationReviewReason) {
        this.authorizationReviewReason = authorizationReviewReason;
    }

    public String getAuthorizationProviderCvvResult() {
        return authorizationProviderCvvResult;
    }

    public void setAuthorizationProviderCvvResult(String authorizationProviderCvvResult) {
        this.authorizationProviderCvvResult = authorizationProviderCvvResult;
    }

    public String getAuthorizationCvvResult() {
        return authorizationCvvResult;
    }

    public void setAuthorizationCvvResult(String authorizationCvvResult) {
        this.authorizationCvvResult = authorizationCvvResult;
    }

    public String getAuthorizationProviderAvsResult() {
        return authorizationProviderAvsResult;
    }

    public void setAuthorizationProviderAvsResult(String authorizationProviderAvsResult) {
        this.authorizationProviderAvsResult = authorizationProviderAvsResult;
    }

    public String getAuthorizationAvsResult() {
        return authorizationAvsResult;
    }

    public void setAuthorizationAvsResult(String authorizationAvsResult) {
        this.authorizationAvsResult = authorizationAvsResult;
    }

    public String getAuthorizationApprovalCode() {
        return authorizationApprovalCode;
    }

    public void setAuthorizationApprovalCode(String authorizationApprovalCode) {
        this.authorizationApprovalCode = authorizationApprovalCode;
    }

    public Calendar getCaptureTime() {
        return DtoUtils.getCalendar(captureTime);
    }

    public void setCaptureTime(Calendar captureTime) {
        this.captureTime = captureTime==null ? null : captureTime.getTimeInMillis();
    }

    public UserId getCaptureUsername() {
        return captureUsername;
    }

    public void setCaptureUsername(UserId captureUsername) {
        this.captureUsername = captureUsername;
    }

    public String getCapturePrincipalName() {
        return capturePrincipalName;
    }

    public void setCapturePrincipalName(String capturePrincipalName) {
        this.capturePrincipalName = capturePrincipalName;
    }

    public String getCaptureCommunicationResult() {
        return captureCommunicationResult;
    }

    public void setCaptureCommunicationResult(String captureCommunicationResult) {
        this.captureCommunicationResult = captureCommunicationResult;
    }

    public String getCaptureProviderErrorCode() {
        return captureProviderErrorCode;
    }

    public void setCaptureProviderErrorCode(String captureProviderErrorCode) {
        this.captureProviderErrorCode = captureProviderErrorCode;
    }

    public String getCaptureErrorCode() {
        return captureErrorCode;
    }

    public void setCaptureErrorCode(String captureErrorCode) {
        this.captureErrorCode = captureErrorCode;
    }

    public String getCaptureProviderErrorMessage() {
        return captureProviderErrorMessage;
    }

    public void setCaptureProviderErrorMessage(String captureProviderErrorMessage) {
        this.captureProviderErrorMessage = captureProviderErrorMessage;
    }

    public String getCaptureProviderUniqueId() {
        return captureProviderUniqueId;
    }

    public void setCaptureProviderUniqueId(String captureProviderUniqueId) {
        this.captureProviderUniqueId = captureProviderUniqueId;
    }

    public Calendar getVoidTime() {
        return DtoUtils.getCalendar(voidTime);
    }

    public void setVoidTime(Calendar voidTime) {
        this.voidTime = voidTime==null ? null : voidTime.getTimeInMillis();
    }

    public UserId getVoidUsername() {
        return voidUsername;
    }

    public void setVoidUsername(UserId voidUsername) {
        this.voidUsername = voidUsername;
    }

    public String getVoidPrincipalName() {
        return voidPrincipalName;
    }

    public void setVoidPrincipalName(String voidPrincipalName) {
        this.voidPrincipalName = voidPrincipalName;
    }

    public String getVoidCommunicationResult() {
        return voidCommunicationResult;
    }

    public void setVoidCommunicationResult(String voidCommunicationResult) {
        this.voidCommunicationResult = voidCommunicationResult;
    }

    public String getVoidProviderErrorCode() {
        return voidProviderErrorCode;
    }

    public void setVoidProviderErrorCode(String voidProviderErrorCode) {
        this.voidProviderErrorCode = voidProviderErrorCode;
    }

    public String getVoidErrorCode() {
        return voidErrorCode;
    }

    public void setVoidErrorCode(String voidErrorCode) {
        this.voidErrorCode = voidErrorCode;
    }

    public String getVoidProviderErrorMessage() {
        return voidProviderErrorMessage;
    }

    public void setVoidProviderErrorMessage(String voidProviderErrorMessage) {
        this.voidProviderErrorMessage = voidProviderErrorMessage;
    }

    public String getVoidProviderUniqueId() {
        return voidProviderUniqueId;
    }

    public void setVoidProviderUniqueId(String voidProviderUniqueId) {
        this.voidProviderUniqueId = voidProviderUniqueId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
