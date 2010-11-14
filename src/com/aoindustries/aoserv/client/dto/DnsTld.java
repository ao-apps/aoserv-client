/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
public class DnsTld extends AOServObject {

    private DomainName domain;

    public DnsTld() {
    }

    public DnsTld(DomainName domain) {
        this.domain = domain;
    }

    public DomainName getDomain() {
        return domain;
    }

    public void setDomain(DomainName domain) {
        this.domain = domain;
    }
}
