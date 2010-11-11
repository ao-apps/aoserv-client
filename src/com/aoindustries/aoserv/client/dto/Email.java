/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
public class Email {

    private String localPart;
    private DomainName domain;

    public Email() {
    }

    public Email(String localPart, DomainName domain) {
        this.localPart = localPart;
        this.domain = domain;;
    }

    public String getLocalPart() {
        return localPart;
    }

    public void setLocalPart(String localPart) {
        this.localPart = localPart;
    }

    public DomainName getDomain() {
        return domain;
    }

    public void setDomain(DomainName domain) {
        this.domain = domain;
    }
}
