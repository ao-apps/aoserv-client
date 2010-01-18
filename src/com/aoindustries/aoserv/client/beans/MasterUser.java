/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.beans;

/**
 * @author  AO Industries, Inc.
 */
public class MasterUser {

    private UserId username;
    private boolean isActive;
    private boolean canAccessAccounting;
    private boolean canAccessBankAccount;
    private boolean canInvalidateTables;
    private boolean canAccessAdminWeb;
    private boolean isDnsAdmin;

    public MasterUser() {
    }

    public MasterUser(
        UserId username,
        boolean isActive,
        boolean canAccessAccounting,
        boolean canAccessBankAccount,
        boolean canInvalidateTables,
        boolean canAccessAdminWeb,
        boolean isDnsAdmin
    ) {
        this.username = username;
        this.isActive = isActive;
        this.canAccessAccounting = canAccessAccounting;
        this.canAccessBankAccount = canAccessBankAccount;
        this.canInvalidateTables = canInvalidateTables;
        this.canAccessAdminWeb = canAccessAdminWeb;
        this.isDnsAdmin = isDnsAdmin;
    }

    public UserId getUsername() {
        return username;
    }

    public void setUsername(UserId username) {
        this.username = username;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public boolean isCanAccessAccounting() {
        return canAccessAccounting;
    }

    public void setCanAccessAccounting(boolean canAccessAccounting) {
        this.canAccessAccounting = canAccessAccounting;
    }

    public boolean isCanAccessBankAccount() {
        return canAccessBankAccount;
    }

    public void setCanAccessBankAccount(boolean canAccessBankAccount) {
        this.canAccessBankAccount = canAccessBankAccount;
    }

    public boolean isCanInvalidateTables() {
        return canInvalidateTables;
    }

    public void setCanInvalidateTables(boolean canInvalidateTables) {
        this.canInvalidateTables = canInvalidateTables;
    }

    public boolean isCanAccessAdminWeb() {
        return canAccessAdminWeb;
    }

    public void setCanAccessAdminWeb(boolean canAccessAdminWeb) {
        this.canAccessAdminWeb = canAccessAdminWeb;
    }

    public boolean isIsDnsAdmin() {
        return isDnsAdmin;
    }

    public void setIsDnsAdmin(boolean isDnsAdmin) {
        this.isDnsAdmin = isDnsAdmin;
    }
}
