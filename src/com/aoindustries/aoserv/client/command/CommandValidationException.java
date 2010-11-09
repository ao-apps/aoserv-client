/*
 * Copyright 2009-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.command;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

/**
 * @author  AO Industries, Inc.
 */
public class CommandValidationException extends RemoteException {

    private static final long serialVersionUID = 1L;

    private static final String eol = System.getProperty("line.separator");

    private static String getMessage(AOServCommand<?> command, Map<String,List<String>> errors) {
        StringBuilder SB = new StringBuilder();
        SB.append(ApplicationResources.accessor.getMessage("CommandValidationException", command)).append(eol);
        for(Map.Entry<String,List<String>> entry : errors.entrySet()) {
            String paramName = entry.getKey();
            for(String message : entry.getValue()) SB.append(ApplicationResources.accessor.getMessage("CommandValidationExceptionError", paramName, message)).append(eol);
        }
        return SB.toString();
    }

    private final AOServCommand<?> command;
    private final Map<String,List<String>> errors;

    public CommandValidationException(AOServCommand<?> command, Map<String,List<String>> errors) {
        super(getMessage(command, errors));
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
