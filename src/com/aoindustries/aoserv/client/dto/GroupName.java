/*
 * Copyright 2009-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
public class GroupName extends AOServObject {

    private GroupId groupName;
    private AccountingCode accounting;
    private Integer disableLog;

    public GroupName() {
    }

    public GroupName(GroupId groupName, AccountingCode accounting, Integer disableLog) {
        this.groupName = groupName;
        this.accounting = accounting;
        this.disableLog = disableLog;
    }

    public GroupId getGroupName() {
        return groupName;
    }

    public void setGroupName(GroupId groupName) {
        this.groupName = groupName;
    }

    public AccountingCode getAccounting() {
        return accounting;
    }

    public void setAccounting(AccountingCode accounting) {
        this.accounting = accounting;
    }

    public Integer getDisableLog() {
        return disableLog;
    }

    public void setDisableLog(Integer disableLog) {
        this.disableLog = disableLog;
    }
}
