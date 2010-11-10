/*
 * Copyright 2009-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.command;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.AOServService;
import com.aoindustries.aoserv.client.BusinessAdministrator;
import com.aoindustries.aoserv.client.MethodColumn;
import com.aoindustries.aoserv.client.SchemaColumn;
import com.aoindustries.aoserv.client.ServiceName;
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

    public String getTableName() {
        return tableName;
    }

    @Override
    public Map<String, List<String>> validate(BusinessAdministrator connectedUser) throws RemoteException {
        // Must be able to find the command name
        if(tableName==null) {
            return Collections.singletonMap(
                PARAM_TABLE_NAME,
                Collections.singletonList(
                    ApplicationResources.accessor.getMessage("AOServCommand.validate.paramRequired", PARAM_TABLE_NAME)
                )
            );
        }
        try {
            ServiceName.valueOf(tableName);
            return Collections.emptyMap();
        } catch(IllegalArgumentException err) {
            return Collections.singletonMap(
                PARAM_TABLE_NAME,
                Collections.singletonList(
                    ApplicationResources.accessor.getMessage("DescribeCommand.validate.tableNotFound", tableName)
                )
            );
        }
    }

    private static String getReturnType(Method method) {
        String generic = method.getGenericReturnType().toString();
        return generic.indexOf('<')!=-1 ? generic : method.getReturnType().getName();
    }

    @Override
    public String execute(AOServConnector<?,?> connector, boolean isInteractive) throws RemoteException {
        // Find the table given its name
        AOServService<?,?,?,?> service = connector.getServices().get(ServiceName.valueOf(tableName));
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
            values[pos++]=methodColumn.getColumnName();
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
