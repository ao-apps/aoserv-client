/*
 * Copyright 2009-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
public class BusinessAdministrator {

    private UserId username;
    private HashedPassword password;
    private String fullName;
    private String title;
    private java.util.Date birthday;
    private boolean isPreferred;
    private boolean isPrivate;
    private java.util.Date created;
    private String workPhone;
    private String homePhone;
    private String cellPhone;
    private String fax;
    private Email email;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String country;
    private String zip;
    private Integer disableLog;
    private boolean canSwitchUsers;
    private String supportCode;

    public BusinessAdministrator() {
    }

    public BusinessAdministrator(
        UserId username,
        HashedPassword password,
        String fullName,
        String title,
        java.util.Date birthday,
        boolean isPreferred,
        boolean isPrivate,
        java.util.Date created,
        String workPhone,
        String homePhone,
        String cellPhone,
        String fax,
        Email email,
        String address1,
        String address2,
        String city,
        String state,
        String country,
        String zip,
        Integer disableLog,
        boolean canSwitchUsers,
        String supportCode
    ) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.title = title;
        this.birthday = birthday;
        this.isPreferred = isPreferred;
        this.isPrivate = isPrivate;
        this.created = created;
        this.workPhone = workPhone;
        this.homePhone = homePhone;
        this.cellPhone = cellPhone;
        this.fax = fax;
        this.email = email;
        this.address1 = address1;
        this.address2 = address2;
        this.city = city;
        this.state = state;
        this.country = country;
        this.zip = zip;
        this.disableLog = disableLog;
        this.canSwitchUsers = canSwitchUsers;
        this.supportCode = supportCode;
    }

    public UserId getUsername() {
        return username;
    }

    public void setUsername(UserId username) {
        this.username = username;
    }

    public HashedPassword getPassword() {
        return password;
    }

    public void setPassword(HashedPassword password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public java.util.Date getBirthday() {
        return birthday;
    }

    public void setBirthday(java.util.Date birthday) {
        this.birthday = birthday;
    }

    public boolean isIsPreferred() {
        return isPreferred;
    }

    public void setIsPreferred(boolean isPreferred) {
        this.isPreferred = isPreferred;
    }

    public boolean isIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public java.util.Date getCreated() {
        return created;
    }

    public void setCreated(java.util.Date created) {
        this.created = created;
    }

    public String getWorkPhone() {
        return workPhone;
    }

    public void setWorkPhone(String workPhone) {
        this.workPhone = workPhone;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
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

    public Integer getDisableLog() {
        return disableLog;
    }

    public void setDisableLog(Integer disableLog) {
        this.disableLog = disableLog;
    }

    public boolean isCanSwitchUsers() {
        return canSwitchUsers;
    }

    public void setCanSwitchUsers(boolean canSwitchUsers) {
        this.canSwitchUsers = canSwitchUsers;
    }

    public String getSupportCode() {
        return supportCode;
    }

    public void setSupportCode(String supportCode) {
        this.supportCode = supportCode;
    }
}
