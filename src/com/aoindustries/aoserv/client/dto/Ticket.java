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
public class Ticket {

    private int ticketId;
    private AccountingCode brand;
    private AccountingCode reseller;
    private AccountingCode accounting;
    private String language;
    private UserId createdBy;
    private Integer category;
    private String ticketType;
    private Email fromAddress;
    private String summary;
    private long openDate;
    private String clientPriority;
    private String adminPriority;
    private String status;
    private Long statusTimeout;
    private String contactEmails;
    private String contactPhoneNumbers;

    public Ticket() {
    }

    public Ticket(
        int ticketId,
        AccountingCode brand,
        AccountingCode reseller,
        AccountingCode accounting,
        String language,
        UserId createdBy,
        Integer category,
        String ticketType,
        Email fromAddress,
        String summary,
        long openDate,
        String clientPriority,
        String adminPriority,
        String status,
        Long statusTimeout,
        String contactEmails,
        String contactPhoneNumbers
    ) {
        this.ticketId = ticketId;
        this.brand = brand;
        this.reseller = reseller;
        this.accounting = accounting;
        this.language = language;
        this.createdBy = createdBy;
        this.category = category;
        this.ticketType = ticketType;
        this.fromAddress = fromAddress;
        this.summary = summary;
        this.openDate = openDate;
        this.clientPriority = clientPriority;
        this.adminPriority = adminPriority;
        this.status = status;
        this.statusTimeout = statusTimeout;
        this.contactEmails = contactEmails;
        this.contactPhoneNumbers = contactPhoneNumbers;
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public AccountingCode getBrand() {
        return brand;
    }

    public void setBrand(AccountingCode brand) {
        this.brand = brand;
    }

    public AccountingCode getReseller() {
        return reseller;
    }

    public void setReseller(AccountingCode reseller) {
        this.reseller = reseller;
    }

    public AccountingCode getAccounting() {
        return accounting;
    }

    public void setAccounting(AccountingCode accounting) {
        this.accounting = accounting;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public UserId getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserId createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public Email getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(Email fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Date getOpenDate() {
        return new Date(openDate);
    }

    public void setOpenDate(Date openDate) {
        this.openDate = openDate.getTime();
    }

    public String getClientPriority() {
        return clientPriority;
    }

    public void setClientPriority(String clientPriority) {
        this.clientPriority = clientPriority;
    }

    public String getAdminPriority() {
        return adminPriority;
    }

    public void setAdminPriority(String adminPriority) {
        this.adminPriority = adminPriority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getStatusTimeout() {
        return statusTimeout==null ? null : new Date(statusTimeout);
    }

    public void setStatusTimeout(Date statusTimeout) {
        this.statusTimeout = statusTimeout==null ? null : statusTimeout.getTime();
    }

    public String getContactEmails() {
        return contactEmails;
    }

    public void setContactEmails(String contactEmails) {
        this.contactEmails = contactEmails;
    }

    public String getContactPhoneNumbers() {
        return contactPhoneNumbers;
    }

    public void setContactPhoneNumbers(String contactPhoneNumbers) {
        this.contactPhoneNumbers = contactPhoneNumbers;
    }
}
