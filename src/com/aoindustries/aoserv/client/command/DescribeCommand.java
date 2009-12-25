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
import com.aoindustries.aoserv.client.ServiceName;
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

    private final String table_name;

    public static final String PARAM_TABLE_NAME = "table_name";

    public DescribeCommand(ServiceName table_name) {
        this(table_name.name());
    }

    public DescribeCommand(
        @Param(name=PARAM_TABLE_NAME, required=true, syntax="<i>"+PARAM_TABLE_NAME+"</i>") String table_name
    ) {
        this.table_name = table_name;
    }

    public Set<AOServPermission.Permission> getPermissions() throws RemoteException {
        return Collections.emptySet();
    }

    public Map<String, List<String>> validate(Locale locale, BusinessAdministrator connectedUser) throws RemoteException {
        // Must be able to find the command name
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

    protected String doExecute(AOServConnector<?,?> connector) throws RemoteException {
        // Find the table given its name
        AOServService<?,?,?,?> service = connector.getServices().get(ServiceName.valueOf(table_name));
        Locale locale = connector.getLocale();
        StringBuilder SB = new StringBuilder();
        SB.append(eol);
        SB.append("<b>").append(ApplicationResources.accessor.getMessage(locale, "DescribeCommand.header.tableName")).append("</b>").append(eol);
        SB.append(eol);
        SB.append("       ").append(CommandName.describe).append(eol);
        String description = service.getServiceName().getDescription(locale);
        if(description!=null && description.length()>0) {
            SB.append(eol);
            SB.append("<b>").append(ApplicationResources.accessor.getMessage(locale, "DescribeCommand.header.description")).append("</b>").append(eol);
            SB.append("       ").append(description).append(eol);
        }
        SB.append(eol);
        SB.append("<b>").append(ApplicationResources.accessor.getMessage(locale, "DescribeCommand.header.columns")).append("</b>").append(eol);
        SB.append(eol);

        SB.append("TODO: Show columns");
        /* TODO
        // Get the list of columns
        List<SchemaColumn> columns=getSchemaColumns(connector);
        int len=columns.size();

        // Build the Object[] of values
        Object[] values=new Object[len*7];
        int pos=0;
        for(int c=0;c<len;c++) {
            SchemaColumn column=columns.get(c);
            values[pos++]=column.column_name;
            values[pos++]=column.getSchemaType(connector).getType();
            values[pos++]=column.isNullable()?"true":"false";
            values[pos++]=column.isUnique()?"true":"false";
            List<SchemaForeignKey> fkeys=column.getReferences(connector);
            if(!fkeys.isEmpty()) {
                StringBuilder SB=new StringBuilder();
                for(int d=0;d<fkeys.size();d++) {
                    SchemaForeignKey key=fkeys.get(d);
                    if(d>0) SB.append('\n');
                    SchemaColumn other=key.getForeignColumn(connector);
                    SB
                        .append(other.getSchemaTable(connector).getName())
                        .append('.')
                        .append(other.column_name)
                    ;
                }
                values[pos++]=SB.toString();
            } else values[pos++]=null;

            fkeys=column.getReferencedBy(connector);
            if(!fkeys.isEmpty()) {
                StringBuilder SB=new StringBuilder();
                for(int d=0;d<fkeys.size();d++) {
                    SchemaForeignKey key=fkeys.get(d);
                    if(d>0) SB.append('\n');
                    SchemaColumn other=key.getKeyColumn(connector);
                    SB
                        .append(other.getSchemaTable(connector).getName())
                        .append('.')
                        .append(other.column_name)
                    ;
                }
                values[pos++]=SB.toString();
            } else values[pos++]=null;
            values[pos++]=column.getDescription();
        }

        // Display the results
        SQLUtility.printTable(descColumns, values, out, isInteractive, descRightAligns);
         */
        return SB.toString();
    }
}
