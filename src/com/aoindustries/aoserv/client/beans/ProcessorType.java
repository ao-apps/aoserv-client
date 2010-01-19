/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.beans;

/**
 * @author  AO Industries, Inc.
 */
public class ProcessorType {

    private String type;
    private short sortOrder;

    public ProcessorType() {
    }

    public ProcessorType(String type, short sortOrder) {
        this.type = type;
        this.sortOrder = sortOrder;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public short getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(short sortOrder) {
        this.sortOrder = sortOrder;
    }
}
