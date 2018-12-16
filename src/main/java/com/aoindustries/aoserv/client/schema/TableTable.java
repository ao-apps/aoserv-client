/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aoserv-client.
 *
 * aoserv-client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aoserv-client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aoserv-client.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.aoserv.client.schema;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.AOServObject;
import com.aoindustries.aoserv.client.AOServTable;
import com.aoindustries.aoserv.client.GlobalTableIntegerKey;
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.sql.SQLExpression;
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.sql.SQLUtility;
import com.aoindustries.util.StringUtility;
import com.aoindustries.util.sort.JavaSort;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see  Table
 *
 * @author  AO Industries, Inc.
 */
final public class TableTable extends GlobalTableIntegerKey<Table> {

	TableTable(AOServConnector connector) {
		super(connector, Table.class);
	}

	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return null;
	}

	/**
	 * Supports Integer (table_id), String(name), and SchemaTable.TableID (table_id) keys.
	 *
	 * @deprecated  Always try to lookup by specific keys; the compiler will help you more when types change.
	 */
	@Deprecated
	@Override
	public Table get(Object pkey) throws IOException, SQLException {
		if(pkey == null) return null;
		if(pkey instanceof Integer) return get(((Number)pkey).intValue());
		else if(pkey instanceof String) return get((String)pkey);
		else if(pkey instanceof Table.TableID) return get((Table.TableID)pkey);
		else throw new IllegalArgumentException("Must be an Integer, a String, or a SchemaTable.TableID");
	}

	/**
	 * @see  #get(java.lang.Object)
	 */
	@Override
	public Table get(int table_id) throws IOException, SQLException {
		return getRows().get(table_id);
	}

	/**
	 * @see  #get(java.lang.Object)
	 */
	public Table get(String name) throws IOException, SQLException {
		return getUniqueRow(Table.COLUMN_NAME, name);
	}

	/**
	 * @see  #get(java.lang.Object)
	 */
	public Table get(Table.TableID tableID) throws IOException, SQLException {
		return get(tableID.ordinal());
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.SCHEMA_TABLES;
	}

	@Override
	public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws SQLException, IOException {
		String command = args[0];
		if (command.equalsIgnoreCase(Command.DESC) || command.equalsIgnoreCase(Command.DESCRIBE)) {
			if(AOSH.checkParamCount(Command.DESCRIBE, args, 1, err)) {
				String tableName=args[1];
				Table table=connector.getSchema().getSchemaTables().get(tableName);
				if(table!=null) {
					table.printDescription(connector, out, isInteractive);
					out.flush();
				} else {
					err.print("aosh: "+Command.DESCRIBE+": table not found: ");
					err.println(tableName);
					err.flush();
				}
			}
			return true;
		} else if (command.equalsIgnoreCase(Command.SELECT)) {
			int argCount = args.length;
			if (argCount >= 4) {
				if (argCount == 4 && args[1].equalsIgnoreCase("count(*)")) {
					// Is a select count(*)
					if ("from".equalsIgnoreCase(args[2])) {
						String tableName = args[3];
						Table table = connector.getSchema().getSchemaTables().get(tableName);
						if (table != null) {
							Object[] titles = { "count" };
							Object[] values = { table.getAOServTable(connector).size()};
							SQLUtility.printTable(titles, values, out, isInteractive, new boolean[] {true});
							out.flush();
						} else {
							err.println("aosh: " + Command.SELECT + ": table not found: " + tableName);
							err.flush();
						}
					} else {
						err.println("aosh: " + Command.SELECT + ": unknown parameter: " + args[2]);
						err.flush();
					}
				} else {
					List<String> columnNames=new ArrayList<>();
					List<SQLExpression> orderExpressions=new ArrayList<>();
					List<Boolean> sortOrders=new ArrayList<>();
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
						err.println("aosh: " + Command.SELECT + ": parameter not found: from");
						err.flush();
						return true;
					}

					// Get the table name
					tableName = args[++c];
					c++;

					Table schemaTable=connector.getSchema().getSchemaTables().get(tableName);
					if(schemaTable!=null) {
						// Substitute any * columnName and ,
						for(int d=0;d<columnNames.size();d++) {
							String columnName=columnNames.get(d);
							if(columnName.equals("*")) {
								List<Column> tcolumns = schemaTable.getSchemaColumns(connector);
								for (int e = 0; e < tcolumns.size(); e++) {
									columnName = tcolumns.get(e).getName();
									if(e==0) columnNames.set(d, columnName);
									else columnNames.add(++d, columnName);
								}
							} else {
								if(columnName.indexOf(',')!=-1) {
									List<String> tcolumns=StringUtility.splitString(columnName, ',');
									int addPos=d--;
									int numAdded=0;
									for (int e = 0; e < tcolumns.size(); e++) {
										columnName=tcolumns.get(e).trim();
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

						AOServTable<?,?> aoServTable=schemaTable.getAOServTable(connector);

						// Parse any order by clause
						if(c<argCount) {
							String arg=args[c++];
							if(arg.equalsIgnoreCase("order")) {
								if(c<argCount) {
									if(args[c++].equalsIgnoreCase("by")) {
										while(c<argCount) {
											String columnName=args[c++];
											List<String> exprs=StringUtility.splitString(columnName, ',');
											for(int d=0;d<exprs.size();d++) {
												String expr=exprs.get(d).trim();
												if(
													orderExpressions.size()>0
													&& (
														expr.equalsIgnoreCase("asc")
														|| expr.equalsIgnoreCase("ascending")
													)
												) sortOrders.set(sortOrders.size()-1, AOServTable.ASCENDING);
												else if(
													orderExpressions.size()>0
													&& (
														expr.equalsIgnoreCase("desc")
														|| expr.equalsIgnoreCase("descending")
													)
												) sortOrders.set(sortOrders.size()-1, AOServTable.DESCENDING);
												else if(expr.length()>0) {
													orderExpressions.add(aoServTable.getSQLExpression(expr));
													sortOrders.add(AOServTable.ASCENDING);
												}
											}
										}
										if(orderExpressions.isEmpty()) throw new SQLException("Parse error: no columns listed after 'order by'");
									} else throw new SQLException("Parse error: 'by' expected");
								} else throw new SQLException("Parse error: 'by' expected");
							} else throw new SQLException("Parse error: 'order' expected, found '"+arg+'\'');
						}

						// Figure out the expressions for each columns
						SQLExpression[] valueExpressions=new SQLExpression[columnNames.size()];
						Type[] valueTypes=new Type[columnNames.size()];
						boolean[] rightAligns=new boolean[columnNames.size()];
						for(int d=0;d<columnNames.size();d++) {
							SQLExpression sql=valueExpressions[d]=aoServTable.getSQLExpression(columnNames.get(d));
							Type type=valueTypes[d]=sql.getType();
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
							for(int d=0;d<orders.length;d++) orders[d]=sortOrders.get(d);
							rows = new ArrayList<>(rows);
							connector.getSchema().getSchemaTypes().sort(JavaSort.getInstance(), rows, exprs, orders);
						}

						// Convert the results
						int numRows=rows.size();
						values=new Object[columnNames.size()*numRows];
						int valuePos=0;
						for(int d=0;d<numRows;d++) {
							AOServObject<?,?> row=rows.get(d);
							for(int e=0;e<valueExpressions.length;e++) {
								SQLExpression sql=valueExpressions[e];
								Type type=valueTypes[e];
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
						err.println("aosh: " + Command.SELECT + ": table not found: " + tableName);
						err.flush();
					}
				}
			} else if (argCount < 4) {
				err.println("aosh: "+Command.SELECT+": not enough parameters");
				err.flush();
			}
			return true;
		} else if (command.equalsIgnoreCase(Command.SHOW)) {
			int argCount = args.length;
			if (argCount >= 2) {
				if ("tables".equalsIgnoreCase(args[1])) {
					handleCommand(new String[] { "select", "name,", "description", "from", "schema_tables" }, in, out, err, isInteractive);
				} else {
					err.println("aosh: "+Command.SHOW+": unknown parameter: " + args[1]);
					err.flush();
				}
			} else {
				err.println("aosh: "+Command.SHOW+": not enough parameters");
				err.flush();
			}
			return true;
		}
		return false;
	}
}
