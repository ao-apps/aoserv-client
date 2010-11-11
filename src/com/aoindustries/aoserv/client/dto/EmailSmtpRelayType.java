/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
public class EmailSmtpRelayType {

    private String name;
    private String sendmailConfig;

    public EmailSmtpRelayType() {
    }

    public EmailSmtpRelayType(String name, String sendmailConfig) {
        this.name = name;
        this.sendmailConfig = sendmailConfig;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSendmailConfig() {
        return sendmailConfig;
    }

    public void setSendmailConfig(String sendmailConfig) {
        this.sendmailConfig = sendmailConfig;
    }
}
