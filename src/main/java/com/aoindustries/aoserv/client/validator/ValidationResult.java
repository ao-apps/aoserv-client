/*
 * Copyright 2011-2013 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.validator;

import java.io.Serializable;

/**
 * For higher performance when validating objects fails, a validator result
 * is returned from the core validation routines instead of immediately
 * throwing ValidationException.  Methods that automatically perform validation,
 * including constructors, will throw ValidationException when needed.
 *
 * @author  AO Industries, Inc.
 */
public interface ValidationResult extends Serializable {

    /**
     * Gets the validation result.
     */
    boolean isValid();

    /**
     * Gets a description of why invalid in the current thread's locale.
     * Should be simply "Valid" (or translation) for valid.
     */
    @Override
    String toString();
}
