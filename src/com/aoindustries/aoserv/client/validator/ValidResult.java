/*
 * Copyright 2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.validator;

/**
 * A valid result singleton.
 *
 * @author  AO Industries, Inc.
 */
final class ValidResult implements ValidationResult {

    private static final long serialVersionUID = -5742207860354792003L;

    private static final ValidResult singleton = new ValidResult();

    static ValidResult getInstance() {
        return singleton;
    }

    private ValidResult() {
    }

    private Object readResolve() {
        return singleton;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public String toString() {
        return ApplicationResources.accessor.getMessage("ValidResult.toString");
    }
}
