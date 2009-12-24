package com.aoindustries.aoserv.client.command;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.AOServService;
import java.rmi.RemoteException;
import java.util.Locale;

/**
 * @author  AO Industries, Inc.
 */
final public class DescribeCommand implements AOServCommand<String> {

    private static final String eol = System.getProperty("line.separator");

    private final String table_name;

    @Syntax("<i>table_name</i>")
    public DescribeCommand(String table_name) {
        this.table_name = table_name;
    }

    public String execute(AOServConnector<?,?> connector) throws RemoteException {
        // Find the table given its name
        AOServService<?,?,?,?> service = null;
        for(AOServService<?,?,?,?> temp : connector.getServices().values()) {
            if(temp.getServiceName().name().equals(table_name)) {
                service = temp;
                break;
            }
        }
        if(service==null) throw new RemoteException(ApplicationResources.accessor.getMessage(connector.getLocale(), "DescribeCommand.tableNotFound", table_name));
        Locale locale = connector.getLocale();
        StringBuilder SB = new StringBuilder();
        SB.append(eol);
        SB.append("<b>").append(ApplicationResources.accessor.getMessage(locale, "DescribeCommand.header.tableName")).append("</b>").append(eol);
        SB.append(eol);
        SB.append("       ").append(CommandName.desc).append(eol);
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
