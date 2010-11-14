/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
public class CreditCardProcessor extends AOServObject {

    private String providerId;
    private AccountingCode accounting;
    private String className;
    private String param1;
    private String param2;
    private String param3;
    private String param4;
    private boolean enabled;
    private int weight;
    private String description;
    private Integer encryptionFrom;
    private Integer encryptionRecipient;

    public CreditCardProcessor() {
    }

    public CreditCardProcessor(
        String providerId,
        AccountingCode accounting,
        String className,
        String param1,
        String param2,
        String param3,
        String param4,
        boolean enabled,
        int weight,
        String description,
        Integer encryptionFrom,
        Integer encryptionRecipient
    ) {
        this.providerId = providerId;
        this.accounting = accounting;
        this.className = className;
        this.param1 = param1;
        this.param2 = param2;
        this.param3 = param3;
        this.param4 = param4;
        this.enabled = enabled;
        this.weight = weight;
        this.description = description;
        this.encryptionFrom = encryptionFrom;
        this.encryptionRecipient = encryptionRecipient;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public AccountingCode getAccounting() {
        return accounting;
    }

    public void setAccounting(AccountingCode accounting) {
        this.accounting = accounting;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getParam1() {
        return param1;
    }

    public void setParam1(String param1) {
        this.param1 = param1;
    }

    public String getParam2() {
        return param2;
    }

    public void setParam2(String param2) {
        this.param2 = param2;
    }

    public String getParam3() {
        return param3;
    }

    public void setParam3(String param3) {
        this.param3 = param3;
    }

    public String getParam4() {
        return param4;
    }

    public void setParam4(String param4) {
        this.param4 = param4;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getEncryptionFrom() {
        return encryptionFrom;
    }

    public void setEncryptionFrom(Integer encryptionFrom) {
        this.encryptionFrom = encryptionFrom;
    }

    public Integer getEncryptionRecipient() {
        return encryptionRecipient;
    }

    public void setEncryptionRecipient(Integer encryptionRecipient) {
        this.encryptionRecipient = encryptionRecipient;
    }
}
