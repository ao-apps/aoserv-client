/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
public class LinuxAccount {

    private int aoServerResource;
    private String linuxAccountType;
    private UserId username;
    private LinuxID uid;
    private UnixPath home;
    private Gecos name;
    private Gecos officeLocation;
    private Gecos officePhone;
    private Gecos homePhone;
    private UnixPath shell;
    private String predisablePassword;

    public LinuxAccount() {
    }

    public LinuxAccount(
        int aoServerResource,
        String linuxAccountType,
        UserId username,
        LinuxID uid,
        UnixPath home,
        Gecos name,
        Gecos officeLocation,
        Gecos officePhone,
        Gecos homePhone,
        UnixPath shell,
        String predisablePassword
    ) {
        this.aoServerResource = aoServerResource;
        this.linuxAccountType = linuxAccountType;
        this.username = username;
        this.uid = uid;
        this.home = home;
        this.name = name;
        this.officeLocation = officeLocation;
        this.officePhone = officePhone;
        this.homePhone = homePhone;
        this.shell = shell;
        this.predisablePassword = predisablePassword;
    }

    public int getAoServerResource() {
        return aoServerResource;
    }

    public void setAoServerResource(int aoServerResource) {
        this.aoServerResource = aoServerResource;
    }

    public String getLinuxAccountType() {
        return linuxAccountType;
    }

    public void setLinuxAccountType(String linuxAccountType) {
        this.linuxAccountType = linuxAccountType;
    }

    public UserId getUsername() {
        return username;
    }

    public void setUsername(UserId username) {
        this.username = username;
    }

    public LinuxID getUid() {
        return uid;
    }

    public void setUid(LinuxID uid) {
        this.uid = uid;
    }

    public UnixPath getHome() {
        return home;
    }

    public void setHome(UnixPath home) {
        this.home = home;
    }

    public Gecos getName() {
        return name;
    }

    public void setName(Gecos name) {
        this.name = name;
    }

    public Gecos getOfficeLocation() {
        return officeLocation;
    }

    public void setOfficeLocation(Gecos officeLocation) {
        this.officeLocation = officeLocation;
    }

    public Gecos getOfficePhone() {
        return officePhone;
    }

    public void setOfficePhone(Gecos officePhone) {
        this.officePhone = officePhone;
    }

    public Gecos getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(Gecos homePhone) {
        this.homePhone = homePhone;
    }

    public UnixPath getShell() {
        return shell;
    }

    public void setShell(UnixPath shell) {
        this.shell = shell;
    }

    public String getPredisablePassword() {
        return predisablePassword;
    }

    public void setPredisablePassword(String predisablePassword) {
        this.predisablePassword = predisablePassword;
    }
}
