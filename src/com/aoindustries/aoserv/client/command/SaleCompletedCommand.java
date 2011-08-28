/*
 * Copyright 2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.command;

import com.aoindustries.aoserv.client.*;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Called when a sale (combined authorization and capture) has been completed.
 *
 * @author  AO Industries, Inc.
 */
final public class SaleCompletedCommand extends RemoteCommand<Void> {

    private static final long serialVersionUID = 1L;

    final private int creditCardTransaction;
    final private String authorizationCommunicationResult;
    final private String authorizationProviderErrorCode;
    final private String authorizationErrorCode;
    final private String authorizationProviderErrorMessage;
    final private String authorizationProviderUniqueId;
    final private String providerApprovalResult;
    final private String approvalResult;
    final private String providerDeclineReason;
    final private String declineReason;
    final private String providerReviewReason;
    final private String reviewReason;
    final private String providerCvvResult;
    final private String cvvResult;
    final private String providerAvsResult;
    final private String avsResult;
    final private String approvalCode;
    final private Timestamp captureTime;
    final private String capturePrincipalName;
    final private String captureCommunicationResult;
    final private String captureProviderErrorCode;
    final private String captureErrorCode;
    final private String captureProviderErrorMessage;
    final private String captureProviderUniqueId;
    final private String status;

    public SaleCompletedCommand(
        @Param(name="creditCardTransaction") CreditCardTransaction creditCardTransaction,
        @Param(name="authorizationCommunicationResult", nullable=true) String authorizationCommunicationResult,
        @Param(name="authorizationProviderErrorCode", nullable=true) String authorizationProviderErrorCode,
        @Param(name="authorizationErrorCode", nullable=true) String authorizationErrorCode,
        @Param(name="authorizationProviderErrorMessage", nullable=true) String authorizationProviderErrorMessage,
        @Param(name="authorizationProviderUniqueId", nullable=true) String authorizationProviderUniqueId,
        @Param(name="providerApprovalResult", nullable=true) String providerApprovalResult,
        @Param(name="approvalResult", nullable=true) String approvalResult,
        @Param(name="providerDeclineReason", nullable=true) String providerDeclineReason,
        @Param(name="declineReason", nullable=true) String declineReason,
        @Param(name="providerReviewReason", nullable=true) String providerReviewReason,
        @Param(name="reviewReason", nullable=true) String reviewReason,
        @Param(name="providerCvvResult", nullable=true) String providerCvvResult,
        @Param(name="cvvResult", nullable=true) String cvvResult,
        @Param(name="providerAvsResult", nullable=true) String providerAvsResult,
        @Param(name="avsResult", nullable=true) String avsResult,
        @Param(name="approvalCode", nullable=true) String approvalCode,
        @Param(name="captureTime") Timestamp captureTime,
        @Param(name="capturePrincipalName", nullable=true) String capturePrincipalName,
        @Param(name="captureCommunicationResult", nullable=true) String captureCommunicationResult,
        @Param(name="captureProviderErrorCode", nullable=true) String captureProviderErrorCode,
        @Param(name="captureErrorCode", nullable=true) String captureErrorCode,
        @Param(name="captureProviderErrorMessage", nullable=true) String captureProviderErrorMessage,
        @Param(name="captureProviderUniqueId", nullable=true) String captureProviderUniqueId,
        @Param(name="status") String status
    ) {
        this.creditCardTransaction = creditCardTransaction.getPkey();
        this.authorizationCommunicationResult = nullIfEmpty(authorizationCommunicationResult);
        this.authorizationProviderErrorCode = nullIfEmpty(authorizationProviderErrorCode);
        this.authorizationErrorCode = nullIfEmpty(authorizationErrorCode);
        this.authorizationProviderErrorMessage = nullIfEmpty(authorizationProviderErrorMessage);
        this.authorizationProviderUniqueId = nullIfEmpty(authorizationProviderUniqueId);
        this.providerApprovalResult = nullIfEmpty(providerApprovalResult);
        this.approvalResult = nullIfEmpty(approvalResult);
        this.providerDeclineReason = nullIfEmpty(providerDeclineReason);
        this.declineReason = nullIfEmpty(declineReason);
        this.providerReviewReason = nullIfEmpty(providerReviewReason);
        this.reviewReason = nullIfEmpty(reviewReason);
        this.providerCvvResult = nullIfEmpty(providerCvvResult);
        this.cvvResult = nullIfEmpty(cvvResult);
        this.providerAvsResult = nullIfEmpty(providerAvsResult);
        this.avsResult = nullIfEmpty(avsResult);
        this.approvalCode = nullIfEmpty(approvalCode);
        this.captureTime = captureTime;
        this.capturePrincipalName = nullIfEmpty(capturePrincipalName);
        this.captureCommunicationResult = nullIfEmpty(captureCommunicationResult);
        this.captureProviderErrorCode = nullIfEmpty(captureProviderErrorCode);
        this.captureErrorCode = nullIfEmpty(captureErrorCode);
        this.captureProviderErrorMessage = nullIfEmpty(captureProviderErrorMessage);
        this.captureProviderUniqueId = nullIfEmpty(captureProviderUniqueId);
        this.status = status;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    public int getCreditCardTransaction() {
        return creditCardTransaction;
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

    public String getProviderApprovalResult() {
        return providerApprovalResult;
    }

    public String getApprovalResult() {
        return approvalResult;
    }

    public String getProviderDeclineReason() {
        return providerDeclineReason;
    }

    public String getDeclineReason() {
        return declineReason;
    }

    public String getProviderReviewReason() {
        return providerReviewReason;
    }

    public String getReviewReason() {
        return reviewReason;
    }

    public String getProviderCvvResult() {
        return providerCvvResult;
    }

    public String getCvvResult() {
        return cvvResult;
    }

    public String getProviderAvsResult() {
        return providerAvsResult;
    }

    public String getAvsResult() {
        return avsResult;
    }

    public String getApprovalCode() {
        return approvalCode;
    }

    public Timestamp getCaptureTime() {
        return captureTime;
    }

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

    public String getStatus() {
        return status;
    }

    @Override
    protected Map<String,List<String>> checkCommand(AOServConnector userConn, AOServConnector rootConn, BusinessAdministrator rootUser) throws RemoteException {
        Map<String,List<String>> errors = Collections.emptyMap();
        // TODO
        return errors;
    }
}
