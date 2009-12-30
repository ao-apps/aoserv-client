/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.beans;

/**
 * @author  AO Industries, Inc.
 */
public class ServerFarm {

    private String name;
    private String description;
    private String owner;
    private boolean useRestrictedSmtpPort;

    public ServerFarm() {
    }

    public ServerFarm(String name, String description, String owner, boolean useRestrictedSmtpPort) {
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.useRestrictedSmtpPort = useRestrictedSmtpPort;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public boolean isUseRestrictedSmtpPort() {
        return useRestrictedSmtpPort;
    }

    public void setUseRestrictedSmtpPort(boolean useRestrictedSmtpPort) {
        this.useRestrictedSmtpPort = useRestrictedSmtpPort;
    }
}
