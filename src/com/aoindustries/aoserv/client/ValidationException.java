/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.util.Arrays;
import com.aoindustries.util.i18n.ApplicationResourcesAccessor;
import com.aoindustries.util.i18n.LocalizedMessage;
import java.io.Serializable;
import java.util.Locale;

/**
 * Thrown when internal object validation fails.
 *
 * @author  AO Industries, Inc.
 */
public class ValidationException extends Exception implements LocalizedMessage {

    private static final long serialVersionUID = 1L;

    private final ApplicationResourcesAccessor accessor;
    private final String key;
    private final Serializable[] args;

    public ValidationException(ApplicationResourcesAccessor accessor, String key) {
        super(accessor.getMessage(Locale.getDefault(), key));
        this.accessor = accessor;
        this.key = key;
        this.args = Arrays.EMPTY_SERIALIZABLE_ARRAY;
    }

    public ValidationException(ApplicationResourcesAccessor accessor, String key, Serializable... args) {
        super(accessor.getMessage(Locale.getDefault(), key, (Object[])args));
        this.accessor = accessor;
        this.key = key;
        this.args = args;
    }

    public ValidationException(Throwable cause, ApplicationResourcesAccessor accessor, String key) {
        super(accessor.getMessage(Locale.getDefault(), key), cause);
        this.accessor = accessor;
        this.key = key;
        this.args = Arrays.EMPTY_SERIALIZABLE_ARRAY;
    }

    public ValidationException(Throwable cause, ApplicationResourcesAccessor accessor, String key, Serializable... args) {
        super(accessor.getMessage(Locale.getDefault(), key, (Object[])args), cause);
        this.accessor = accessor;
        this.key = key;
        this.args = args;
    }

    @Override
    public String getLocalizedMessage() {
        return getLocalizedMessage(Locale.getDefault());
    }

    public String getLocalizedMessage(Locale userLocale) {
        return accessor.getMessage(userLocale, key, (Object[])args);
    }
}
