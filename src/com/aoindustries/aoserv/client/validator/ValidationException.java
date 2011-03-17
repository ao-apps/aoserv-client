/*
 * Copyright 2010-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.validator;

import com.aoindustries.util.AoArrays;
import com.aoindustries.util.i18n.ApplicationResourcesAccessor;
import java.io.Serializable;

/**
 * Thrown when internal object validation fails.
 *
 * @author  AO Industries, Inc.
 */
public class ValidationException extends Exception {

    private static final long serialVersionUID = 9113659436631669565L;

    private final ApplicationResourcesAccessor accessor;
    private final String key;
    private final Serializable[] args;

    public ValidationException(ApplicationResourcesAccessor accessor, String key) {
        super(key);
        this.accessor = accessor;
        this.key = key;
        this.args = AoArrays.EMPTY_SERIALIZABLE_ARRAY;
    }

    public ValidationException(ApplicationResourcesAccessor accessor, String key, Serializable... args) {
        super(key);
        this.accessor = accessor;
        this.key = key;
        this.args = args;
    }

    public ValidationException(Throwable cause, ApplicationResourcesAccessor accessor, String key) {
        super(key, cause);
        this.accessor = accessor;
        this.key = key;
        this.args = AoArrays.EMPTY_SERIALIZABLE_ARRAY;
    }

    public ValidationException(Throwable cause, ApplicationResourcesAccessor accessor, String key, Serializable... args) {
        super(key, cause);
        this.accessor = accessor;
        this.key = key;
        this.args = args;
    }

    @Override
    public String getMessage() {
        return getLocalizedMessage();
    }

    @Override
    public String getLocalizedMessage() {
        return accessor.getMessage(key, (Object[])args);
    }
}
