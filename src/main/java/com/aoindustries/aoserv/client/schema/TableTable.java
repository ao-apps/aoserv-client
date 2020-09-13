/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018, 2019, 2020  AO Industries, Inc.
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
import com.aoindustries.exception.WrappedException;
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.sql.SQLUtility;
import com.aoindustries.util.sort.JavaSort;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
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
	@SuppressWarnings("ReturnOfCollectionOrArrayField")
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

	/** Avoid repeated array copies. */
	private static final int numTables = Table.TableID.values().length;

	@Override
	public List<Table> getRows() throws IOException, SQLException {
		List<Table> rows = super.getRows();
		int size = rows.size();
		if(size != numTables) {
			throw new SQLException("Unexpected number of rows: expected " + numTables + ", got " + size);
		}
		return rows;
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
					selectCount(args, out, err, isInteractive);
				} else {
					selectRows(args, out, err, isInteractive);
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

	private void selectCount(String[] args, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IOException, SQLException {
		// Is a select count(*)
		if ("from".equalsIgnoreCase(args[2])) {
			String tableName = Parser.unquote(args[3]);
			Table table = connector.getSchema().getTable().get(tableName);
			if (table != null) {
				SQLUtility.printTable(
					new String[] {"count"},
					(Iterable<Object[]>)Collections.singleton(new Object[] {table.getAOServTable(connector).size()}),
					out,
					isInteractive,
					new boolean[] {true}
				);
				out.flush();
			} else {
				err.println("aosh: " + Command.SELECT + ": table not found: " + Parser.quote(tableName));
				err.flush();
			}
		} else {
			err.println("aosh: " + Command.SELECT + ": unknown parameter: " + args[2]);
			err.flush();
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes", "AssignmentToForLoopParameter", "UseSpecificCatch", "TooBroadCatch"})
	private void selectRows(String[] args, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IOException, SQLException {
		int argCount = args.length;
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
			return;
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
			final int numExpressions = expressions.size();
			final SQLExpression[] valueExpressions = new SQLExpression[numExpressions];
			final Type[] valueTypes = new Type[numExpressions];
			int supportsAnyPrecisionCount = 0;
			boolean[] rightAligns = new boolean[numExpressions];
			for(int d = 0; d < numExpressions; d++) {
				SQLExpression sql = Parser.parseSQLExpression(aoServTable, expressions.get(d));
				Type type = sql.getType();
				valueExpressions[d] = sql;
				valueTypes[d] = type;
				if(type.supportsPrecision()) supportsAnyPrecisionCount++;
				rightAligns[d] = type.alignRight();
			}

			// Get the data
			List<AOServObject> rows = null;
			boolean rowsCopied = false;
			try {
				// Sort if needed
				if(orderExpressions.size() > 0) {
					SQLExpression[] exprs = orderExpressions.toArray(new SQLExpression[orderExpressions.size()]);
					boolean[] orders = new boolean[exprs.length];
					for(int d = 0; d < orders.length; d++) {
						orders[d] = sortOrders.get(d);
					}
					rows = (List<AOServObject>)aoServTable.getRowsCopy();
					rowsCopied = true;
					connector.sort(JavaSort.getInstance(), rows, exprs, orders);
				} else {
					rows = (List<AOServObject>)aoServTable.getRows();
				}
				final List<AOServObject> finalRows = rows;
				final int numRows = rows.size();

				// Evaluate the expressions while finding the maximum precisions per column.
				// The precisions allow uniform formatting within a column to depend on the overall contents of the column.
				final int[] precisions = new int[numExpressions];
				Arrays.fill(precisions, -1);
				// Only iterate through all rows here when needing to process precisions
				if(supportsAnyPrecisionCount > 0) {
					// Stop searching if all max precisions have been found
					int precisionsNotMaxedCount = supportsAnyPrecisionCount;
					ROWS :
					for(AOServObject<?,?> row : rows) {
						for(int col = 0; col < numExpressions; col++) {
							Type type = valueTypes[col];
							// Skip evaluation when precision not supported
							if(type.supportsPrecision()) {
								int maxPrecision = type.getMaxPrecision();
								int current = precisions[col];
								if(
									maxPrecision == -1
									|| current == -1
									|| current < maxPrecision
								) {
									int precision = type.getPrecision(valueExpressions[col].evaluate(connector, row));
									if(
										precision != -1
										&& (current == -1 || precision > current)
									) {
										precisions[col] = precision;
										if(maxPrecision != -1 && precision >= maxPrecision) {
											precisionsNotMaxedCount--;
											// Stop searching when all precision-based columns are maxed
											if(precisionsNotMaxedCount <= 0) break ROWS;
										}
									}
								}
							}
						}
					}
				}

				// Print the results
				String[] cnames = new String[numExpressions];
				for(int d = 0; d < numExpressions; d++) {
					cnames[d] = valueExpressions[d].getColumnName();
				}
				try {
					SQLUtility.printTable(
						cnames,
						(Iterable<String[]>)() -> new Iterator<String[]>() {
							private int index = 0;

							@Override
							public boolean hasNext() {
								return index < numRows;
							}

							@Override
							public String[] next() {
								try {
									// Convert the results to strings
									AOServObject<?,?> row = finalRows.get(index++);
									String[] strings = new String[numExpressions];
									for(int col = 0; col < numExpressions; col++) {
										strings[col] = valueTypes[col].getString(
											valueExpressions[col].evaluate(connector, row),
											precisions[col]
										);
									}
									return strings;
								} catch(IOException | SQLException e) {
									throw new WrappedException(e);
								}
							}

							@Override
							public void remove() {
								throw new UnsupportedOperationException();
							}
						},
						out,
						isInteractive,
						rightAligns
					);
				} catch(WrappedException e) {
					Throwable cause = e.getCause();
					if(cause instanceof IOException) throw (IOException)cause;
					if(cause instanceof SQLException) throw (SQLException)cause;
					throw e;
				}
			} finally {
				if(rowsCopied && rows instanceof AutoCloseable) {
					try {
						((AutoCloseable)rows).close();
					} catch(Error | RuntimeException | IOException | SQLException e) {
						throw e;
					} catch(Throwable t) {
						throw new WrappedException(t);
					}
				}
			}
			out.flush();
		} else {
			err.println("aosh: " + Command.SELECT + ": table not found: " + Parser.quote(tableName));
			err.flush();
		}
	}
}
