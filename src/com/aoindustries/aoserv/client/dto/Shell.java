/*
 * Copyright 2009-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
public class Shell extends AOServObject {

    private UnixPath path;

    public Shell() {
    }

    public Shell(UnixPath path) {
        this.path = path;
    }

    public UnixPath getPath() {
        return path;
    }

    public void setPath(UnixPath path) {
        this.path = path;
    }
}
