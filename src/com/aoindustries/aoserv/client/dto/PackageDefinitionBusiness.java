/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
public class PackageDefinitionBusiness extends AOServObject {

    private int pkey;
    private int packageDefinition;
    private AccountingCode accounting;
    private String display;
    private String description;
    private boolean active;

    public PackageDefinitionBusiness() {
    }

    public PackageDefinitionBusiness(int pkey, int packageDefinition, AccountingCode accounting, String display, String description, boolean active) {
        this.pkey = pkey;
        this.packageDefinition = packageDefinition;
        this.accounting = accounting;
        this.display = display;
        this.description = description;
        this.active = active;
    }

    public int getPkey() {
        return pkey;
    }

    public void setPkey(int pkey) {
        this.pkey = pkey;
    }

    public int getPackageDefinition() {
        return packageDefinition;
    }

    public void setPackageDefinition(int packageDefinition) {
        this.packageDefinition = packageDefinition;
    }

    public AccountingCode getAccounting() {
        return accounting;
    }

    public void setAccounting(AccountingCode accounting) {
        this.accounting = accounting;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
