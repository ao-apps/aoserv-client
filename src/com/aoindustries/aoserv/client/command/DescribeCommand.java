package com.aoindustries.aoserv.client.command;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.AOServPermission;
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
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author  AO Industries, Inc.
 */
final public class DescribeCommand extends AOServCommand<String> {

    private static final long serialVersionUID = 1L;

    private static final String eol = System.getProperty("line.separator");

    public static final String PARAM_TABLE_NAME = "table_name";

    private final String table_name;

    public DescribeCommand(
        @Param(name=PARAM_TABLE_NAME, nullable=false, syntax="<i>"+PARAM_TABLE_NAME+"</i>") String table_name
    ) {
        this.table_name = table_name;
    }

    public Map<String, List<String>> validate(Locale locale, BusinessAdministrator connectedUser) throws RemoteException {
        // Must be able to find the command name
        if(table_name==null) {
            return Collections.singletonMap(
                PARAM_TABLE_NAME,
                Collections.singletonList(
                    ApplicationResources.accessor.getMessage(locale, "AOServCommand.validate.paramRequired", PARAM_TABLE_NAME)
                )
            );
        }
        try {
            ServiceName.valueOf(table_name);
            return Collections.emptyMap();
        } catch(IllegalArgumentException err) {
            return Collections.singletonMap(
                PARAM_TABLE_NAME,
                Collections.singletonList(
                    ApplicationResources.accessor.getMessage(locale, "DescribeCommand.validate.tableNotFound", table_name)
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
        AOServService<?,?,?,?> service = connector.getServices().get(ServiceName.valueOf(table_name));
        Locale locale = connector.getLocale();
        StringBuilder SB = new StringBuilder();
        SB.append("<b>").append(ApplicationResources.accessor.getMessage(locale, "DescribeCommand.header.tableName")).append("</b>").append(eol);
        SB.append("       ").append(table_name).append(eol);
        String description = service.getServiceName().getDescription(locale);
        if(description!=null && description.length()>0) {
            SB.append(eol);
            SB.append("<b>").append(ApplicationResources.accessor.getMessage(locale, "DescribeCommand.header.description")).append("</b>").append(eol);
            SB.append("       ").append(description).append(eol);
        }
        SB.append(eol);
        SB.append("<b>").append(ApplicationResources.accessor.getMessage(locale, "DescribeCommand.header.columns")).append("</b>").append(eol);
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
                    ApplicationResources.accessor.getMessage(locale, "DescribeCommand.header.columns.column"),
                    ApplicationResources.accessor.getMessage(locale, "DescribeCommand.header.columns.type"),
                    ApplicationResources.accessor.getMessage(locale, "DescribeCommand.header.columns.index"),
                    ApplicationResources.accessor.getMessage(locale, "DescribeCommand.header.columns.description")
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
