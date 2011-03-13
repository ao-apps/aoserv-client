/*
 * Copyright 2009-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

import java.util.Calendar;

/**
 * @author  AO Industries, Inc.
 */
public class Business extends AOServObject {

    private AccountingCode accounting;
    private String contractVersion;
    private long created;
    private Long canceled;
    private String cancelReason;
    private AccountingCode parent;
    private boolean canAddBackupServer;
    private boolean canAddBusinesses;
    private boolean canSeePrices;
    private Integer disableLog;
    private String doNotDisableReason;
    private boolean autoEnable;
    private boolean billParent;
    private int packageDefinition;
    private UserId createdBy;
    private Integer emailInBurst;
    private Float emailInRate;
    private Integer emailOutBurst;
    private Float emailOutRate;
    private Integer emailRelayBurst;
    private Float emailRelayRate;

    public Business() {
    }

    public Business(
        AccountingCode accounting,
        String contractVersion,
        long created,
        Long canceled,
        String cancelReason,
        AccountingCode parent,
        boolean canAddBackupServer,
        boolean canAddBusinesses,
        boolean canSeePrices,
        Integer disableLog,
        String doNotDisableReason,
        boolean autoEnable,
        boolean billParent,
        int packageDefinition,
        UserId createdBy,
        Integer emailInBurst,
        Float emailInRate,
        Integer emailOutBurst,
        Float emailOutRate,
        Integer emailRelayBurst,
        Float emailRelayRate
    ) {
        this.accounting = accounting;
        this.contractVersion = contractVersion;
        this.created = created;
        this.canceled = canceled;
        this.cancelReason = cancelReason;
        this.parent = parent;
        this.canAddBackupServer = canAddBackupServer;
        this.canAddBusinesses = canAddBusinesses;
        this.canSeePrices = canSeePrices;
        this.disableLog = disableLog;
        this.doNotDisableReason = doNotDisableReason;
        this.autoEnable = autoEnable;
        this.billParent = billParent;
        this.packageDefinition = packageDefinition;
        this.createdBy = createdBy;
        this.emailInBurst = emailInBurst;
        this.emailInRate = emailInRate;
        this.emailOutBurst = emailOutBurst;
        this.emailOutRate = emailOutRate;
        this.emailRelayBurst = emailRelayBurst;
        this.emailRelayRate = emailRelayRate;
    }

    public AccountingCode getAccounting() {
        return accounting;
    }

    public void setAccounting(AccountingCode accounting) {
        this.accounting = accounting;
    }

    public String getContractVersion() {
        return contractVersion;
    }

    public void setContractVersion(String contractVersion) {
        this.contractVersion = contractVersion;
    }

    public Calendar getCreated() {
        return DtoUtils.getCalendar(created);
    }

    public void setCreated(Calendar created) {
        this.created = created.getTimeInMillis();
    }

    public Calendar getCanceled() {
        return DtoUtils.getCalendar(canceled);
    }

    public void setCanceled(Calendar canceled) {
        this.canceled = canceled==null ? null : canceled.getTimeInMillis();
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public AccountingCode getParent() {
        return parent;
    }

    public void setParent(AccountingCode parent) {
        this.parent = parent;
    }

    public boolean isCanAddBackupServer() {
        return canAddBackupServer;
    }

    public void setCanAddBackupServer(boolean canAddBackupServer) {
        this.canAddBackupServer = canAddBackupServer;
    }

    public boolean isCanAddBusinesses() {
        return canAddBusinesses;
    }

    public void setCanAddBusinesses(boolean canAddBusinesses) {
        this.canAddBusinesses = canAddBusinesses;
    }

    public boolean isCanSeePrices() {
        return canSeePrices;
    }

    public void setCanSeePrices(boolean canSeePrices) {
        this.canSeePrices = canSeePrices;
    }

    public Integer getDisableLog() {
        return disableLog;
    }

    public void setDisableLog(Integer disableLog) {
        this.disableLog = disableLog;
    }

    public String getDoNotDisableReason() {
        return doNotDisableReason;
    }

    public void setDoNotDisableReason(String doNotDisableReason) {
        this.doNotDisableReason = doNotDisableReason;
    }

    public boolean isAutoEnable() {
        return autoEnable;
    }

    public void setAutoEnable(boolean autoEnable) {
        this.autoEnable = autoEnable;
    }

    public boolean isBillParent() {
        return billParent;
    }

    public void setBillParent(boolean billParent) {
        this.billParent = billParent;
    }

    public int getPackageDefinition() {
        return packageDefinition;
    }

    public void setPackageDefinition(int packageDefinition) {
        this.packageDefinition = packageDefinition;
    }

    public UserId getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserId createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getEmailInBurst() {
        return emailInBurst;
    }

    public void setEmailInBurst(Integer emailInBurst) {
        this.emailInBurst = emailInBurst;
    }

    public Float getEmailInRate() {
        return emailInRate;
    }

    public void setEmailInRate(Float emailInRate) {
        this.emailInRate = emailInRate;
    }

    public Integer getEmailOutBurst() {
        return emailOutBurst;
    }

    public void setEmailOutBurst(Integer emailOutBurst) {
        this.emailOutBurst = emailOutBurst;
    }

    public Float getEmailOutRate() {
        return emailOutRate;
    }

    public void setEmailOutRate(Float emailOutRate) {
        this.emailOutRate = emailOutRate;
    }

    public Integer getEmailRelayBurst() {
        return emailRelayBurst;
    }

    public void setEmailRelayBurst(Integer emailRelayBurst) {
        this.emailRelayBurst = emailRelayBurst;
    }

    public Float getEmailRelayRate() {
        return emailRelayRate;
    }

    public void setEmailRelayRate(Float emailRelayRate) {
        this.emailRelayRate = emailRelayRate;
    }
}
