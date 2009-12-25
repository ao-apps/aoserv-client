package com.aoindustries.aoserv.client.command;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.util.List;
import java.util.Map;

/**
 * @author  AO Industries, Inc.
 */
public class ValidationException extends Exception {

    private static final long serialVersionUID = 1L;

    private final AOServCommand<?> command;
    private final Map<String,List<String>> errors;

    public ValidationException(AOServCommand<?> command, Map<String,List<String>> errors) {
        this.command = command;
        this.errors = errors;
    }

    public AOServCommand<?> getCommand() {
        return command;
    }

    public Map<String,List<String>> getErrors() {
        return errors;
    }
}
