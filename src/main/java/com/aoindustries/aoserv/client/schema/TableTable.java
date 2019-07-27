/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018, 2019  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.sql.Parser;
import com.aoindustries.aoserv.client.sql.SQLExpression;
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.sql.SQLUtility;
import com.aoindustries.util.sort.JavaSort;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
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
				String tableName = Parser.unquote(args[1]);
				Table table = connector.getSchema().getTable().get(tableName);
				if(table != null) {
					table.printDescription(connector, out, isInteractive);
					out.flush();
				} else {
					err.print("aosh: "+Command.DESCRIBE+": table not found: ");
					err.println(Parser.quote(tableName));
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
						String tableName = Parser.unquote(args[3]);
						Table table = connector.getSchema().getTable().get(tableName);
						if (table != null) {
							Object[] titles = { "count" };
							Object[] values = { table.getAOServTable(connector).size()};
							SQLUtility.printTable(titles, values, out, isInteractive, new boolean[] {true});
							out.flush();
						} else {
							err.println("aosh: " + Command.SELECT + ": table not found: " + Parser.quote(tableName));
							err.flush();
						}
					} else {
						err.println("aosh: " + Command.SELECT + ": unknown parameter: " + args[2]);
						err.flush();
					}
				} else {
					List<String> expressionArgs = new ArrayList<>();

					// Parse the list of expressions until "from" is found
					int c = 1;
					for (; c < argCount; c++) {
						String arg = args[c];
						if ("from".equalsIgnoreCase(arg)) break;
						expressionArgs.add(arg);
					}
					if (c >= argCount - 1) {
						// The from was not found
						err.println("aosh: " + Command.SELECT + ": parameter not found: from");
						err.flush();
						return true;
					}

					// Get the table name
					String tableName = Parser.unquote(args[++c]);
					c++;

					Table schemaTable = connector.getSchema().getTable().get(tableName);
					if(schemaTable != null) {
						List<String> expressions = new ArrayList<>(expressionArgs.size());
						// Substitute any * columnName and ,
						for(String expressionArg : expressionArgs) {
							if(expressionArg.equals("*")) {
								for(Column column : schemaTable.getSchemaColumns(connector)) {
									expressions.add(Parser.quote(column.getName()));
								}
							} else {
								do {
									String current;
									int commaPos = Parser.indexOfNotQuoted(expressionArg, ',');
									if(commaPos == -1) {
										current = expressionArg;
										expressionArg = "";
									} else {
										current = expressionArg.substring(0, commaPos);
										expressionArg = expressionArg.substring(commaPos + 1);
									}
									if(current.equals("*")) {
										for(Column column : schemaTable.getSchemaColumns(connector)) {
											expressions.add(Parser.quote(column.getName()));
										}
									} else {
										expressions.add(current);
									}
								} while(!expressionArg.isEmpty());
							}
						}

						AOServTable<?,?> aoServTable = schemaTable.getAOServTable(connector);

						// Parse any order by clause
						List<SQLExpression> orderExpressions = new ArrayList<>();
						List<Boolean> sortOrders = new ArrayList<>();
						if(c < argCount) {
							String arg = args[c++];
							if(arg.equalsIgnoreCase("order")) {
								if(c < argCount) {
									if(args[c++].equalsIgnoreCase("by")) {
										while(c < argCount) {
											String orderBy = args[c++];
											do {
												String expr;
												int commaPos = Parser.indexOfNotQuoted(orderBy, ',');
												if(commaPos == -1) {
													expr = orderBy;
													orderBy = "";
												} else {
													expr = orderBy.substring(0, commaPos);
													orderBy = orderBy.substring(commaPos + 1);
												}
												if(
													orderExpressions.size() > 0
													&& (
														expr.equalsIgnoreCase("asc")
														|| expr.equalsIgnoreCase("ascending")
													)
												) {
													sortOrders.set(sortOrders.size() - 1, AOServTable.ASCENDING);
												} else if(
													orderExpressions.size() > 0
													&& (
														expr.equalsIgnoreCase("desc")
														|| expr.equalsIgnoreCase("descending")
													)
												) {
													sortOrders.set(sortOrders.size() - 1, AOServTable.DESCENDING);
												} else { // if(!expr.isEmpty()) {
													orderExpressions.add(Parser.parseSQLExpression(aoServTable, expr));
													sortOrders.add(AOServTable.ASCENDING);
												}
											} while(!orderBy.isEmpty());
										}
										if(orderExpressions.isEmpty()) throw new SQLException("Parse error: no expressions listed after 'order by'");
									} else throw new SQLException("Parse error: 'by' expected");
								} else throw new SQLException("Parse error: 'by' expected");
							} else throw new SQLException("Parse error: 'order' expected, found '" + arg + '\'');
						}

						// Figure out the expressions for each columns
						SQLExpression[] valueExpressions = new SQLExpression[expressions.size()];
						Type[] valueTypes = new Type[expressions.size()];
						boolean[] rightAligns = new boolean[expressions.size()];
						for(int d = 0; d < expressions.size(); d++) {
							SQLExpression sql = Parser.parseSQLExpression(aoServTable, expressions.get(d));
							Type type = sql.getType();
							valueExpressions[d] = sql;
							valueTypes[d] = type;
							rightAligns[d] = type.alignRight();
						}

						// Get the data
						List<? extends AOServObject> rows = aoServTable.getRows();
						int numRows = rows.size();

						// Sort if needed
						if(orderExpressions.size() > 0) {
							SQLExpression[] exprs = orderExpressions.toArray(new SQLExpression[orderExpressions.size()]);
							boolean[] orders = new boolean[exprs.length];
							for(int d = 0; d < orders.length;d++) orders[d] = sortOrders.get(d);
							rows = new ArrayList<>(rows);
							connector.getSchema().getType().sort(JavaSort.getInstance(), rows, exprs, orders);
						}

						// Evaluate the expressions while finding the maximum precisions per column.
						// The precisions allow uniform formatting within a column to depend on the overall contents of the column.
						Object[] values = new Object[valueExpressions.length * numRows];
						int[] precisions = new int[valueExpressions.length];
						Arrays.fill(precisions, -1);
						int index = 0;
						for(AOServObject<?,?> row : rows) {
							for(int col = 0; col < valueExpressions.length; col++) {
								SQLExpression sql = valueExpressions[col];
								Type type = valueTypes[col];
								Object value = sql.getValue(connector, row);
								int precision = type.getPrecision(value);
								if(precision != -1) {
									int current = precisions[col];
									if(current == -1 || precision > current) {
										precisions[col] = precision;
									}
								}
								values[index++] = value;
							}
						}

						// Convert the results to strings
						String[] strings = new String[valueExpressions.length * numRows];
						index = 0;
						for(int row = 0; row < numRows; row++) {
							for(int col = 0; col < valueExpressions.length; col++) {
								strings[index] = valueTypes[col].getString(values[index], precisions[col]);
								index++;
							}
						}

						// Print the results
						String[] cnames = new String[valueExpressions.length];
						for(int d = 0; d < cnames.length; d++) cnames[d] = valueExpressions[d].getColumnName();
						SQLUtility.printTable(cnames, strings, out, isInteractive, rightAligns);
						out.flush();
					} else {
						err.println("aosh: " + Command.SELECT + ": table not found: " + Parser.quote(tableName));
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
