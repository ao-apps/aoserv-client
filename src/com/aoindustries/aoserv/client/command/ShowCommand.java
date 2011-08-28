/*
 * Copyright 2009-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.command;

import com.aoindustries.aoserv.client.*;
import com.aoindustries.sql.SQLUtility;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author  AO Industries, Inc.
 */
final public class ShowCommand extends AOServCommand<String> {

    private static final String PARAM_OBJECT = "object";

    private final String object;

    public ShowCommand(
        @Param(name=PARAM_OBJECT, syntax="<b>tables</b>") String object
    ) {
        this.object = object;
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    public String getObject() {
        return object;
    }

    @Override
    protected Map<String,List<String>> checkCommand(AOServConnector userConn, AOServConnector rootConn, BusinessAdministrator rootUser) throws RemoteException {
        Map<String,List<String>> errors = Collections.emptyMap();
        // Must be able to find the command name
        if(object==null) {
            errors = addValidationError(
                errors,
                PARAM_OBJECT,
                "AOServCommand.validate.paramRequired",
                PARAM_OBJECT
            );
        } else if(!"tables".equals(object)) {
            errors = addValidationError(
                errors,
                PARAM_OBJECT,
                "AOServCommand.validate.unknownObject",
                object
            );
        }
        return errors;
    }

    @Override
    public String execute(AOServConnector connector, boolean isInteractive) throws RemoteException {
        // Build the Object[] of values
        Object[] values=new Object[ServiceName.values.size()*2];
        int pos=0;
        for(ServiceName serviceName : ServiceName.values) {
            values[pos++]=serviceName.name();
            values[pos++]=serviceName.getDescription();
        }

        // Display the results
        try {
            StringBuilder SB = new StringBuilder();
            SQLUtility.printTable(
                new String[] {
                    ApplicationResources.accessor.getMessage("ShowCommand.header.name"),
                    ApplicationResources.accessor.getMessage("ShowCommand.header.description")
                },
                values,
                SB,
                isInteractive,
                new boolean[] {
                    false,
                    false,
                    false,
                    false
                }
            );
            return SB.toString();
        } catch(IOException err) {
            throw new RemoteException(err.getMessage(), err);
        }
    }
}
