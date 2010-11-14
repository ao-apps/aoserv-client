/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
public class PackageDefinitionLimit extends AOServObject {

    private int pkey;
    private int packageDefinition;
    private String resourceType;
    private Integer softLimit;
    private Integer hardLimit;
    private Money additionalRate;
    private String additionalTransactionType;

    public PackageDefinitionLimit() {
    }

    public PackageDefinitionLimit(int pkey, int packageDefinition, String resourceType, Integer softLimit, Integer hardLimit, Money additionalRate, String additionalTransactionType) {
        this.pkey = pkey;
        this.packageDefinition = packageDefinition;
        this.resourceType = resourceType;
        this.softLimit = softLimit;
        this.hardLimit = hardLimit;
        this.additionalRate = additionalRate;
        this.additionalTransactionType = additionalTransactionType;
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

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public Integer getSoftLimit() {
        return softLimit;
    }

    public void setSoftLimit(Integer softLimit) {
        this.softLimit = softLimit;
    }

    public Integer getHardLimit() {
        return hardLimit;
    }

    public void setHardLimit(Integer hardLimit) {
        this.hardLimit = hardLimit;
    }

    public Money getAdditionalRate() {
        return additionalRate;
    }

    public void setAdditionalRate(Money additionalRate) {
        this.additionalRate = additionalRate;
    }

    public String getAdditionalTransactionType() {
        return additionalTransactionType;
    }

    public void setAdditionalTransactionType(String additionalTransactionType) {
        this.additionalTransactionType = additionalTransactionType;
    }
}
