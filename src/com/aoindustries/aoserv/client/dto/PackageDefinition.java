/*
 * Copyright 2010-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
public class PackageDefinition extends AOServObject {

    private int pkey;
    private String category;
    private String name;
    private String version;
    private Money setupFee;
    private String setupFeeTransactionType;
    private Money monthlyRate;
    private String monthlyRateTransactionType;
    private boolean approved;

    public PackageDefinition() {
    }

    public PackageDefinition(
        int pkey,
        String category,
        String name,
        String version,
        Money setupFee,
        String setupFeeTransactionType,
        Money monthlyRate,
        String monthlyRateTransactionType,
        boolean approved
    ) {
        this.pkey = pkey;
        this.category = category;
        this.name = name;
        this.version = version;
        this.setupFee = setupFee;
        this.setupFeeTransactionType = setupFeeTransactionType;
        this.monthlyRate = monthlyRate;
        this.monthlyRateTransactionType = monthlyRateTransactionType;
        this.approved = approved;
    }

    public int getPkey() {
        return pkey;
    }

    public void setPkey(int pkey) {
        this.pkey = pkey;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Money getSetupFee() {
        return setupFee;
    }

    public void setSetupFee(Money setupFee) {
        this.setupFee = setupFee;
    }

    public String getSetupFeeTransactionType() {
        return setupFeeTransactionType;
    }

    public void setSetupFeeTransactionType(String setupFeeTransactionType) {
        this.setupFeeTransactionType = setupFeeTransactionType;
    }

    public Money getMonthlyRate() {
        return monthlyRate;
    }

    public void setMonthlyRate(Money monthlyRate) {
        this.monthlyRate = monthlyRate;
    }

    public String getMonthlyRateTransactionType() {
        return monthlyRateTransactionType;
    }

    public void setMonthlyRateTransactionType(String monthlyRateTransactionType) {
        this.monthlyRateTransactionType = monthlyRateTransactionType;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }
}
