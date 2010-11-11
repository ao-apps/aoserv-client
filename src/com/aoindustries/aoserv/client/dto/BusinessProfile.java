/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

import java.util.Date;

/**
 * @author  AO Industries, Inc.
 */
public class BusinessProfile {

    private int pkey;
    private AccountingCode accounting;
    private int priority;
    private String name;
    private boolean isPrivate;
    private String phone;
    private String fax;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String country;
    private String zip;
    private boolean sendInvoice;
    private Date created;
    private String billingContact;
    private String billingEmail;
    private String technicalContact;
    private String technicalEmail;

    public BusinessProfile() {
    }

    public BusinessProfile(
        int pkey,
        AccountingCode accounting,
        int priority,
        String name,
        boolean isPrivate,
        String phone,
        String fax,
        String address1,
        String address2,
        String city,
        String state,
        String country,
        String zip,
        boolean sendInvoice,
        Date created,
        String billingContact,
        String billingEmail,
        String technicalContact,
        String technicalEmail
    ) {
        this.pkey = pkey;
        this.accounting = accounting;
        this.priority = priority;
        this.name = name;
        this.isPrivate = isPrivate;
        this.phone = phone;
        this.fax = fax;
        this.address1 = address1;
        this.address2 = address2;
        this.city = city;
        this.state = state;
        this.country = country;
        this.zip = zip;
        this.sendInvoice = sendInvoice;
        this.created = created;
        this.billingContact = billingContact;
        this.billingEmail = billingEmail;
        this.technicalContact = technicalContact;
        this.technicalEmail = technicalEmail;
    }

    public int getPkey() {
        return pkey;
    }

    public void setPkey(int pkey) {
        this.pkey = pkey;
    }

    public AccountingCode getAccounting() {
        return accounting;
    }

    public void setAccounting(AccountingCode accounting) {
        this.accounting = accounting;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public boolean isSendInvoice() {
        return sendInvoice;
    }

    public void setSendInvoice(boolean sendInvoice) {
        this.sendInvoice = sendInvoice;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getBillingContact() {
        return billingContact;
    }

    public void setBillingContact(String billingContact) {
        this.billingContact = billingContact;
    }

    public String getBillingEmail() {
        return billingEmail;
    }

    public void setBillingEmail(String billingEmail) {
        this.billingEmail = billingEmail;
    }

    public String getTechnicalContact() {
        return technicalContact;
    }

    public void setTechnicalContact(String technicalContact) {
        this.technicalContact = technicalContact;
    }

    public String getTechnicalEmail() {
        return technicalEmail;
    }

    public void setTechnicalEmail(String technicalEmail) {
        this.technicalEmail = technicalEmail;
    }
}
