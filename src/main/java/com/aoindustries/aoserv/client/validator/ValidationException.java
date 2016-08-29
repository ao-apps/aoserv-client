/*
 * Copyright 2010-2013 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.validator;

import com.aoindustries.lang.LocalizedIllegalArgumentException;

/**
 * Thrown when internal object validation fails.
 *
 * @author  AO Industries, Inc.
 */
public class ValidationException extends Exception {

    private static final long serialVersionUID = -1153407618428602416L;

    final ValidationResult result;

    public ValidationException(ValidationResult result) {
        super(result.toString()); // Conversion done in server
        if(result.isValid()) throw new LocalizedIllegalArgumentException(ApplicationResources.accessor, "ValidationException.init.validResult");
        this.result = result;
    }

    public ValidationException(Throwable cause, ValidationResult result) {
        super(result.toString(), cause); // Conversion done in server
        if(result.isValid()) throw new LocalizedIllegalArgumentException(ApplicationResources.accessor, "ValidationException.init.validResult");
        this.result = result;
    }

    @Override
    public String getLocalizedMessage() {
        return result.toString(); // Conversion done in client
    }
}
