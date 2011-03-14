/*
 * Copyright 2010-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
public class MasterUser extends AOServObject {

    private UserId username;
    private boolean active;
    private boolean canAccessAccounting;
    private boolean canAccessBankAccount;
    private boolean canInvalidateTables;
    private boolean canAccessAdminWeb;
    private boolean dnsAdmin;

    public MasterUser() {
    }

    public MasterUser(
        UserId username,
        boolean active,
        boolean canAccessAccounting,
        boolean canAccessBankAccount,
        boolean canInvalidateTables,
        boolean canAccessAdminWeb,
        boolean dnsAdmin
    ) {
        this.username = username;
        this.active = active;
        this.canAccessAccounting = canAccessAccounting;
        this.canAccessBankAccount = canAccessBankAccount;
        this.canInvalidateTables = canInvalidateTables;
        this.canAccessAdminWeb = canAccessAdminWeb;
        this.dnsAdmin = dnsAdmin;
    }

    public UserId getUsername() {
        return username;
    }

    public void setUsername(UserId username) {
        this.username = username;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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

    public boolean isDnsAdmin() {
        return dnsAdmin;
    }

    public void setDnsAdmin(boolean dnsAdmin) {
        this.dnsAdmin = dnsAdmin;
    }
}
