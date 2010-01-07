/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.beans;

import java.util.Date;

/**
 * @author  AO Industries, Inc.
 */
public class Business {

    private String accounting;
    private String contractVersion;
    private Date created;
    private Date canceled;
    private String cancelReason;
    private String parent;
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
        String accounting,
        String contractVersion,
        Date created,
        Date canceled,
        String cancelReason,
        String parent,
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

    public String getAccounting() {
        return accounting;
    }

    public void setAccounting(String accounting) {
        this.accounting = accounting;
    }

    public String getContractVersion() {
        return contractVersion;
    }

    public void setContractVersion(String contractVersion) {
        this.contractVersion = contractVersion;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getCanceled() {
        return canceled;
    }

    public void setCanceled(Date canceled) {
        this.canceled = canceled;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
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
