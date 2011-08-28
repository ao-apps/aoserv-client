/*
 * Copyright 2009-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.command;

import com.aoindustries.aoserv.client.*;
import com.aoindustries.sql.SQLUtility;
import com.aoindustries.table.Table;
import java.io.IOException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author  AO Industries, Inc.
 */
final public class DescribeCommand extends AOServCommand<String> {

    private static final String eol = System.getProperty("line.separator");

    private static final String PARAM_TABLE_NAME = "tableName";

    private final String tableName;

    public DescribeCommand(
        @Param(name=PARAM_TABLE_NAME) String tableName
    ) {
        this.tableName = tableName;
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    public String getTableName() {
        return tableName;
    }

    @Override
    protected Map<String,List<String>> checkCommand(AOServConnector userConn, AOServConnector rootConn, BusinessAdministrator rootUser) throws RemoteException {
        Map<String,List<String>> errors = Collections.emptyMap();
        // Must be able to find the command name
        if(tableName==null) {
            errors = addValidationError(
                errors,
                PARAM_TABLE_NAME,
                "AOServCommand.validate.paramRequired",
                PARAM_TABLE_NAME
            );
        } else {
            try {
                ServiceName.valueOf(tableName);
                // No problem - fall-through to return
            } catch(IllegalArgumentException err) {
                errors = addValidationError(
                    errors,
                    PARAM_TABLE_NAME,
                    "DescribeCommand.validate.tableNotFound",
                    tableName
                );
            }
        }
        return errors;
    }

    private static String getReturnType(Method method) {
        String generic = method.getGenericReturnType().toString();
        return generic.indexOf('<')!=-1 ? generic : method.getReturnType().getName();
    }

    @Override
    public String execute(AOServConnector connector, boolean isInteractive) throws RemoteException {
        // Find the table given its name
        AOServService<?,?> service = connector.getServices().get(ServiceName.valueOf(tableName));
        StringBuilder SB = new StringBuilder();
        SB.append("<b>").append(ApplicationResources.accessor.getMessage("DescribeCommand.header.tableName")).append("</b>").append(eol);
        SB.append("       ").append(tableName).append(eol);
        String description = service.getServiceName().getDescription();
        if(description!=null && description.length()>0) {
            SB.append(eol);
            SB.append("<b>").append(ApplicationResources.accessor.getMessage("DescribeCommand.header.description")).append("</b>").append(eol);
            SB.append("       ").append(description).append(eol);
        }
        SB.append(eol);
        SB.append("<b>").append(ApplicationResources.accessor.getMessage("DescribeCommand.header.columns")).append("</b>").append(eol);
        SB.append(eol);

        // Get the list of columns
        Table<MethodColumn,?> table = service.getTable();
        List<? extends MethodColumn> columns = table.getColumns();
        int len=columns.size();

        // Build the Object[] of values
        Object[] values=new Object[len*4];
        int pos=0;
        for(int c=0;c<len;c++) {
            MethodColumn methodColumn = columns.get(c);
            SchemaColumn schemaColumn = methodColumn.getSchemaColumn();
            values[pos++]=methodColumn.getName();
            values[pos++]=getReturnType(methodColumn.getMethod());
            values[pos++]=methodColumn.getIndexType();
            values[pos++]=schemaColumn.description();
        }

        // Display the results
        try {
            SQLUtility.printTable(
                new String[] {
                    ApplicationResources.accessor.getMessage("DescribeCommand.header.columns.column"),
                    ApplicationResources.accessor.getMessage("DescribeCommand.header.columns.type"),
                    ApplicationResources.accessor.getMessage("DescribeCommand.header.columns.index"),
                    ApplicationResources.accessor.getMessage("DescribeCommand.header.columns.description")
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
