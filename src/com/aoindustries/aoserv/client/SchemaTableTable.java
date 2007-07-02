package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.profiler.*;
import com.aoindustries.sql.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  SchemaTable
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class SchemaTableTable extends GlobalTableIntegerKey<SchemaTable> {

    SchemaTableTable(AOServConnector connector) {
	super(connector, SchemaTable.class);
    }

    protected String[] getDefaultSortColumnsImpl() {
        return null;
    }
    
    protected boolean[] getDefaultSortOrdersImpl() {
        return null;
    }

    public SchemaTable get(Object pkey) {
        if(pkey instanceof Integer) return get(((Integer)pkey).intValue());
        else if(pkey instanceof String) return get((String)pkey);
        else if(pkey instanceof SchemaTable.TableID) return get((SchemaTable.TableID)pkey);
        else throw new IllegalArgumentException("Must be an Integer or a String");
    }

    public SchemaTable get(int table_id) {
        return getRows().get(table_id);
    }

    public SchemaTable get(String name) {
        return getUniqueRow(SchemaTable.COLUMN_NAME, name);
    }

    public SchemaTable get(SchemaTable.TableID tableID) {
        return get(tableID.ordinal());
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.SCHEMA_TABLES;
    }

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
        Profiler.startProfile(Profiler.UNKNOWN, SchemaTableTable.class, "handleCommand(String[],InputStream,TerminalWriter,TerminalWriter,boolean)", null);
        try {
            String command = args[0];
            if (command.equalsIgnoreCase(AOSHCommand.DESC) || command.equalsIgnoreCase(AOSHCommand.DESCRIBE)) {
                if(AOSH.checkParamCount(AOSHCommand.DESCRIBE, args, 1, err)) {
                    String tableName=args[1];
                    SchemaTable table=connector.schemaTables.get(tableName);
                    if(table!=null) {
                        try {
                            table.printDescription(connector, out, isInteractive);
                            out.flush();
                        } catch(IOException err2) {
                            throw new WrappedException(err2);
                        }
                    } else {
                        err.print("aosh: "+AOSHCommand.DESCRIBE+": table not found: ");
                        err.println(tableName);
                        err.flush();
                    }
                }
                return true;
            } else if (command.equalsIgnoreCase(AOSHCommand.SELECT)) {
                int argCount = args.length;
                if (argCount >= 4) {
                    if (argCount == 4 && args[1].equalsIgnoreCase("count(*)")) {
                        // Is a select count(*)
                        if ("from".equalsIgnoreCase(args[2])) {
                            String tableName = args[3];
                            SchemaTable table = connector.schemaTables.get(tableName);
                            if (table != null) {
                                Object[] titles = { "count" };
                                Object[] values = { Integer.valueOf(table.getAOServTable(connector).size())};
                                SQLUtility.printTable(titles, values, out, isInteractive, new boolean[] {true});
                                out.flush();
                            } else {
                                err.println("aosh: " + AOSHCommand.SELECT + ": table not found: " + tableName);
                                err.flush();
                            }
                        } else {
                            err.println("aosh: " + AOSHCommand.SELECT + ": unknown parameter: " + args[2]);
                            err.flush();
                        }
                    } else {
                        List<String> columnNames=new ArrayList<String>();
                        List<SQLExpression> orderExpressions=new ArrayList<SQLExpression>();
                        List<Boolean> sortOrders=new ArrayList<Boolean>();
                        String arg1 = args[1];
                        String tableName;

                        // Parse the list of columns until "from" is found
                        int c = 1;
                        for (; c < argCount; c++) {
                            String arg = args[c];
                            if ("from".equalsIgnoreCase(arg)) break;
                            columnNames.add(arg);
                        }
                        if (c >= argCount - 1) {
                            // The from was not found
                            err.println("aosh: " + AOSHCommand.SELECT + ": parameter not found: from");
                            err.flush();
                            return true;
                        }

                        // Get the table name
                        tableName = args[++c];
                        c++;

                        SchemaTable schemaTable=connector.schemaTables.get(tableName);
                        if(schemaTable!=null) {
                            // Substitute any * columnName and ,
                            for(int d=0;d<columnNames.size();d++) {
                                String columnName=columnNames.get(d);
                                if(columnName.equals("*")) {
                                    List<SchemaColumn> tcolumns = schemaTable.getSchemaColumns(connector);
                                    for (int e = 0; e < tcolumns.size(); e++) {
                                        columnName=tcolumns.get(e).column_name;
                                        if(e==0) columnNames.set(d, columnName);
                                        else columnNames.add(++d, columnName);
                                    }
                                } else {
                                    if(columnName.indexOf(',')!=-1) {
                                        String[] tcolumns=StringUtility.splitString(columnName.toString(), ',');
                                        int addPos=d--;
                                        int numAdded=0;
                                        for (int e = 0; e < tcolumns.length; e++) {
                                            columnName=tcolumns[e].trim();
                                            if(columnName.length()>0) {
                                                if(numAdded==0) columnNames.set(addPos++, columnName);
                                                else columnNames.add(addPos++, columnName);
                                                numAdded++;
                                            }
                                        }
                                        if(numAdded==0) columnNames.remove(addPos);
                                    }
                                }
                            }

                            AOServTable<?,? extends AOServObject> aoServTable=schemaTable.getAOServTable(connector);

                            // Parse any order by clause
                            if(c<argCount) {
                                String arg=args[c++];
                                if(arg.equalsIgnoreCase("order")) {
                                    if(c<argCount) {
                                        if(args[c++].equalsIgnoreCase("by")) {
                                            while(c<argCount) {
                                                String columnName=args[c++];
                                                String[] exprs=StringUtility.splitString(columnName, ',');
                                                for(int d=0;d<exprs.length;d++) {
                                                    String expr=exprs[d].trim();
                                                    if(
                                                        orderExpressions.size()>0
                                                        && (
                                                            expr.equalsIgnoreCase("asc")
                                                            || expr.equalsIgnoreCase("ascending")
                                                        )
                                                    ) sortOrders.set(sortOrders.size()-1, AOServTable.ASCENDING?Boolean.TRUE:Boolean.FALSE);
                                                    else if(
                                                        orderExpressions.size()>0
                                                        && (
                                                            expr.equalsIgnoreCase("desc")
                                                            || expr.equalsIgnoreCase("descending")
                                                        )
                                                    ) sortOrders.set(sortOrders.size()-1, AOServTable.DESCENDING?Boolean.TRUE:Boolean.FALSE);
                                                    else if(expr.length()>0) {
                                                        orderExpressions.add(aoServTable.getSQLExpression(expr));
                                                        sortOrders.add(AOServTable.ASCENDING?Boolean.TRUE:Boolean.FALSE);
                                                    }
                                                }
                                            }
                                            if(orderExpressions.isEmpty()) throw new WrappedException(new SQLException("Parse error: no columns listed after 'order by'"));
                                        } else throw new WrappedException(new SQLException("Parse error: 'by' expected"));
                                    } else throw new WrappedException(new SQLException("Parse error: 'by' expected"));
                                } else throw new WrappedException(new SQLException("Parse error: 'order' expected, found '"+arg+'\''));
                            }

                            // Figure out the expressions for each columns
                            SQLExpression[] valueExpressions=new SQLExpression[columnNames.size()];
                            SchemaType[] valueTypes=new SchemaType[columnNames.size()];
                            boolean[] rightAligns=new boolean[columnNames.size()];
                            for(int d=0;d<columnNames.size();d++) {
                                SQLExpression sql=valueExpressions[d]=aoServTable.getSQLExpression(columnNames.get(d));
                                SchemaType type=valueTypes[d]=sql.getType();
                                rightAligns[d]=type.alignRight();
                            }

                            // Get the data
                            Object[] values;
                            boolean copy=orderExpressions.size()>0;
                            List<? extends AOServObject> rows=aoServTable.getRows();
                            // Sort if needed
                            if(orderExpressions.size()>0) {
                                SQLExpression[] exprs=orderExpressions.toArray(new SQLExpression[orderExpressions.size()]);
                                boolean[] orders=new boolean[exprs.length];
                                for(int d=0;d<orders.length;d++) orders[d]=sortOrders.get(d).booleanValue();
                                rows = new ArrayList<AOServObject>(rows);
                                connector.schemaTypes.sort(rows, exprs, orders);
                            }

                            // Convert the results
                            int numRows=rows.size();
                            values=new Object[columnNames.size()*numRows];
                            int valuePos=0;
                            for(int d=0;d<numRows;d++) {
                                AOServObject row=(AOServObject)rows.get(d);
                                for(int e=0;e<valueExpressions.length;e++) {
                                    SQLExpression sql=valueExpressions[e];
                                    SchemaType type=valueTypes[e];
                                    Object val=sql.getValue(connector, row);
                                    values[valuePos++]=type.getString(val);
                                }
                            }

                            // Print the results
                            String[] cnames=new String[valueExpressions.length];
                            for(int d=0;d<cnames.length;d++) cnames[d]=valueExpressions[d].getColumnName();
                            SQLUtility.printTable(cnames, values, out, isInteractive, rightAligns);
                            out.flush();
                        } else {
                            err.println("aosh: " + AOSHCommand.SELECT + ": table not found: " + tableName);
                            err.flush();
                        }
                    }
                } else if (argCount < 4) {
                    err.println("aosh: "+AOSHCommand.SELECT+": not enough parameters");
                    err.flush();
                }
                return true;
            } else if (command.equalsIgnoreCase(AOSHCommand.SHOW)) {
                int argCount = args.length;
                if (argCount >= 2) {
                    if ("tables".equalsIgnoreCase(args[1])) {
                        handleCommand(new String[] { "select", "name,", "description", "from", "schema_tables" }, in, out, err, isInteractive);
                    } else {
                        err.println("aosh: "+AOSHCommand.SHOW+": unknown parameter: " + args[1]);
                        err.flush();
                    }
                } else {
                    err.println("aosh: "+AOSHCommand.SHOW+": not enough parameters");
                    err.flush();
                }
                return true;
            }
            return false;
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }
}