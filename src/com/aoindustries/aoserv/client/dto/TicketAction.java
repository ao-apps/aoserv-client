/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

import java.util.Calendar;

/**
 * @author  AO Industries, Inc.
 */
public class TicketAction {

    private int pkey;
    private int ticket;
    private UserId administrator;
    private long time;
    private String actionType;
    private AccountingCode oldAccounting;
    private AccountingCode newAccounting;
    private String oldPriority;
    private String newPriority;
    private String oldType;
    private String newType;
    private String oldStatus;
    private String newStatus;
    private UserId oldAssignedTo;
    private UserId newAssignedTo;
    private Integer oldCategory;
    private Integer newCategory;
    private Email fromAddress;
    private String summary;

    public TicketAction() {
    }

    public TicketAction(
        int pkey,
        int ticket,
        UserId administrator,
        long time,
        String actionType,
        AccountingCode oldAccounting,
        AccountingCode newAccounting,
        String oldPriority,
        String newPriority,
        String oldType,
        String newType,
        String oldStatus,
        String newStatus,
        UserId oldAssignedTo,
        UserId newAssignedTo,
        Integer oldCategory,
        Integer newCategory,
        Email fromAddress,
        String summary
    ) {
        this.pkey = pkey;
        this.ticket = ticket;
        this.administrator = administrator;
        this.time = time;
        this.actionType = actionType;
        this.oldAccounting = oldAccounting;
        this.newAccounting = newAccounting;
        this.oldPriority = oldPriority;
        this.newPriority = newPriority;
        this.oldType = oldType;
        this.newType = newType;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.oldAssignedTo = oldAssignedTo;
        this.newAssignedTo = newAssignedTo;
        this.oldCategory = oldCategory;
        this.newCategory = newCategory;
        this.fromAddress = fromAddress;
        this.summary = summary;
    }

    public int getPkey() {
        return pkey;
    }

    public void setPkey(int pkey) {
        this.pkey = pkey;
    }

    public int getTicket() {
        return ticket;
    }

    public void setTicket(int ticket) {
        this.ticket = ticket;
    }

    public UserId getAdministrator() {
        return administrator;
    }

    public void setAdministrator(UserId administrator) {
        this.administrator = administrator;
    }

    public Calendar getTime() {
        return DtoUtils.getCalendar(time);
    }

    public void setTime(Calendar time) {
        this.time = time.getTimeInMillis();
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public AccountingCode getOldAccounting() {
        return oldAccounting;
    }

    public void setOldAccounting(AccountingCode oldAccounting) {
        this.oldAccounting = oldAccounting;
    }

    public AccountingCode getNewAccounting() {
        return newAccounting;
    }

    public void setNewAccounting(AccountingCode newAccounting) {
        this.newAccounting = newAccounting;
    }

    public String getOldPriority() {
        return oldPriority;
    }

    public void setOldPriority(String oldPriority) {
        this.oldPriority = oldPriority;
    }

    public String getNewPriority() {
        return newPriority;
    }

    public void setNewPriority(String newPriority) {
        this.newPriority = newPriority;
    }

    public String getOldType() {
        return oldType;
    }

    public void setOldType(String oldType) {
        this.oldType = oldType;
    }

    public String getNewType() {
        return newType;
    }

    public void setNewType(String newType) {
        this.newType = newType;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(String oldStatus) {
        this.oldStatus = oldStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public UserId getOldAssignedTo() {
        return oldAssignedTo;
    }

    public void setOldAssignedTo(UserId oldAssignedTo) {
        this.oldAssignedTo = oldAssignedTo;
    }

    public UserId getNewAssignedTo() {
        return newAssignedTo;
    }

    public void setNewAssignedTo(UserId newAssignedTo) {
        this.newAssignedTo = newAssignedTo;
    }

    public Integer getOldCategory() {
        return oldCategory;
    }

    public void setOldCategory(Integer oldCategory) {
        this.oldCategory = oldCategory;
    }

    public Integer getNewCategory() {
        return newCategory;
    }

    public void setNewCategory(Integer newCategory) {
        this.newCategory = newCategory;
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
}
