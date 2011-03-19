/*
 * Copyright 2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.validator;

import com.aoindustries.util.AoArrays;
import com.aoindustries.util.i18n.ApplicationResourcesAccessor;
import java.io.Serializable;

/**
 * An invalid result with a user-friendly message.
 *
 * @author  AO Industries, Inc.
 */
final class InvalidResult implements ValidationResult {

    private static final long serialVersionUID = -105878200149461063L;

    private final ApplicationResourcesAccessor accessor;
    private final String key;
    private final Serializable[] args;

    InvalidResult(ApplicationResourcesAccessor accessor, String key) {
        this.accessor = accessor;
        this.key = key;
        this.args = AoArrays.EMPTY_SERIALIZABLE_ARRAY;
    }

    InvalidResult(ApplicationResourcesAccessor accessor, String key, Serializable... args) {
        this.accessor = accessor;
        this.key = key;
        this.args = args;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public String toString() {
        return accessor.getMessage(key, (Object[])args);
    }
}
