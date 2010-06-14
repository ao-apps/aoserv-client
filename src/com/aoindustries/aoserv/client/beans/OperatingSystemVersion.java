/*
 * Copyright 2009-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.beans;

/**
 * @author  AO Industries, Inc.
 */
public class OperatingSystemVersion {

    private int pkey;
    private String operatingSystem;
    private String versionNumber;
    private String versionName;
    private String architecture;
    private String display;
    private boolean isAoservDaemonSupported;
    private short sortOrder;

    public OperatingSystemVersion() {
    }

    public OperatingSystemVersion(
        int pkey,
        String operatingSystem,
        String versionNumber,
        String versionName,
        String architecture,
        String display,
        boolean isAoservDaemonSupported,
        short sortOrder
    ) {
        this.pkey = pkey;
        this.operatingSystem = operatingSystem;
        this.versionNumber = versionNumber;
        this.versionName = versionName;
        this.architecture = architecture;
        this.display = display;
        this.isAoservDaemonSupported = isAoservDaemonSupported;
        this.sortOrder = sortOrder;
    }

    public int getPkey() {
        return pkey;
    }

    public void setPkey(int pkey) {
        this.pkey = pkey;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getArchitecture() {
        return architecture;
    }

    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public boolean isIsAoservDaemonSupported() {
        return isAoservDaemonSupported;
    }

    public void setIsAoservDaemonSupported(boolean isAoservDaemonSupported) {
        this.isAoservDaemonSupported = isAoservDaemonSupported;
    }

    public short getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(short sortOrder) {
        this.sortOrder = sortOrder;
    }
}
