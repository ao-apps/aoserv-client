/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2019, 2020, 2021  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.sql;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.AOServTable;
import com.aoindustries.aoserv.client.schema.Column;
import com.aoindustries.aoserv.client.schema.ForeignKey;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.schema.Type;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Static utilities for use in SQL parsing.
 *
 * @author  AO Industries, Inc.
 */
public class Parser {

	private Parser() {}

	/**
	 * Find a match only outside quoted section of the expressions.
	 * Quoted by ", with "" being the escape for a " within the quoted section.
	 */
	// TODO: Unit tests
	public static int indexOfNotQuoted(String expr, char ch, int fromIndex) {
		boolean quoted = false;
		for(int i = fromIndex, end = expr.length(); i < end; i++) {
			char c = expr.charAt(i);
			if(!quoted && c == ch) return i;
			if(c == '"') {
				// If followed by another quote while quoted, do not unquote
				if(quoted) {
					if(
						i < (end - 1)
						&& expr.charAt(i + 1) == '"'
					) {
						// Skip
						i++;
					} else {
						// Unquote
						quoted = false;
					}
				} else {
					quoted = true;
				}
			}
		}
		return -1;
	}

	/**
	 * Find a match only outside quoted section of the expressions.
	 * Quoted by ", with "" being the escape for a " within the quoted section.
	 */
	// TODO: Unit tests
	public static int indexOfNotQuoted(String expr, char ch) {
		return indexOfNotQuoted(expr, ch, 0);
	}

	/**
	 * Find a match only outside quoted section of the expressions.
	 * Quoted by ", with "" being the escape for a " within the quoted section.
	 */
	// TODO: Unit tests
	public static int indexOfNotQuoted(String expr, String str, int fromIndex) {
		boolean quoted = false;
		int strLen = str.length();
		for(int i = fromIndex, end = expr.length() - strLen; i < end; i++) {
			char c = expr.charAt(i);
			if(!quoted && expr.regionMatches(i, str, 0, strLen)) return i;
			if(c == '"') {
				// If followed by another quote while quoted, do not unquote
				if(quoted) {
					if(
						i < (end - 1)
						&& expr.charAt(i + 1) == '"'
					) {
						// Skip
						i++;
					} else {
						// Unquote
						quoted = false;
					}
				} else {
					quoted = true;
				}
			}
		}
		return -1;
	}

	/**
	 * Find a match only outside quoted section of the expressions.
	 * Quoted by ", with "" being the escape for a " within the quoted section.
	 */
	// TODO: Unit tests
	public static int indexOfNotQuoted(String expr, String str) {
		return indexOfNotQuoted(expr, str, 0);
	}

	/**
	 * Unquotes a string, removing " characters, except "" being the escape for a " within a quoted section.
	 */
	// TODO: Have AOSH only support ' quotes in command line parsing?  This would help with "" quoting for table/columns not being swallowed by bash-style double quotes
	//       This should not hurt since we don't support any variable substitution inside double quotes anyway

	// TODO: Unit tests
	@SuppressWarnings("AssignmentToForLoopParameter")
	public static String unquote(String str) {
		int strLen = str.length();
		StringBuilder unquoted = new StringBuilder(strLen);
		boolean quoted = false;
		for(int i = 0; i < strLen; i++) {
			char c = str.charAt(i);
			if(c == '"') {
				// If followed by another quote while quoted, do not unquote
				if(quoted) {
					if(
						i < (strLen - 1)
						&& str.charAt(i + 1) == '"'
					) {
						// Is escaped quote
						unquoted.append('"');
						i++;
					} else {
						// Unquote
						quoted = false;
					}
				} else {
					quoted = true;
				}
			} else {
				unquoted.append(c);
			}
		}
		return unquoted.length() == strLen ? str : unquoted.toString();
	}

	/**
	 * Quotes a string if needed.  Currently this only when is empty, contains " or .
	 * or a character outside normal ASCII range.
	 * Unicode is not considered for use without quoting, since this is only used to quote AOServ table/column names, which are all ASCII.
	 */
	// TODO: Unit tests
	public static String quote(String str) {
		int strLen = str.length();
		if(strLen == 0) return "\"\"";
		int quotedLen = strLen + 2;
		boolean needsQuote = false;
		for(int i = 0; i < strLen; i++) {
			char c = str.charAt(i);
			if(c <= ' ' || c > '~' || c == '.') {
				needsQuote = true;
			} else if(c == '"') {
				needsQuote = true;
				quotedLen++;
			}
		}
		if(needsQuote) {
			char[] quoted = new char[quotedLen];
			int quotedPos = 0;
			quoted[quotedPos++] = '"';
			for(int i = 0; i < strLen; i++) {
				char c = str.charAt(i);
				quoted[quotedPos++] = c;
				if(c == '"') quoted[quotedPos++] = '"';
			}
			quoted[quotedPos++] = '"';
			assert quotedPos == quotedLen;
			return new String(quoted);
		} else {
			return str;
		}
	}

	// TODO: Unit tests
	public static SQLExpression parseSQLExpression(AOServTable<?, ?> table, String expr) throws SQLException, IOException {
		AOServConnector connector = table.getConnector();
		int joinPos = indexOfNotQuoted(expr, '.');
		if(joinPos == -1) joinPos = expr.length();
		int castPos = indexOfNotQuoted(expr, "::");
		if(castPos == -1) castPos = expr.length();
		int columnNameEnd = Math.min(joinPos, castPos);
		String columnName = unquote(expr.substring(0, columnNameEnd));
		Table tableSchema = table.getTableSchema();
		Column lastColumn = tableSchema.getSchemaColumn(connector, columnName);
		if(lastColumn == null) throw new IllegalArgumentException("Unable to find column: " + quote(tableSchema.getName()) + '.' + quote(columnName));

		SQLExpression sql = new SQLColumnValue(connector, lastColumn);
		expr = expr.substring(columnNameEnd);

		while(!expr.isEmpty()) {
			if(expr.charAt(0) == '.') {
				List<ForeignKey> keys = lastColumn.getReferences(connector);
				if(keys.size() != 1) throw new IllegalArgumentException("Column " + quote(lastColumn.getTable(connector).getName()) + '.' + quote(lastColumn.getName()) + " should reference precisely one column, references " + keys.size());

				joinPos = indexOfNotQuoted(expr, '.', 1);
				if(joinPos == -1) joinPos = expr.length();
				castPos = indexOfNotQuoted(expr, "::", 1);
				if(castPos == -1) castPos = expr.length();
				int joinNameEnd = Math.min(joinPos, castPos);
				columnName = unquote(expr.substring(1, joinNameEnd));
				Column keyColumn = keys.get(0).getForeignColumn(connector);
				Table valueTable = keyColumn.getTable(connector);
				Column valueColumn = valueTable.getSchemaColumn(connector, columnName);
				if(valueColumn == null) throw new IllegalArgumentException("Unable to find column: " + quote(valueTable.getName()) + '.' + quote(columnName) + " referenced from " + quote(tableSchema.getName()));

				sql = new SQLColumnJoin(connector, sql, keyColumn, valueColumn);
				expr = expr.substring(joinNameEnd);

				lastColumn = valueColumn;
			} else if(expr.charAt(0)==':' && expr.length() >= 2 && expr.charAt(1) == ':') {
				joinPos = indexOfNotQuoted(expr, '.', 2);
				if(joinPos == -1) joinPos = expr.length();
				castPos = indexOfNotQuoted(expr, "::", 2);
				if(castPos == -1) castPos = expr.length();
				int typeNameEnd = Math.min(joinPos, castPos);
				String typeName = unquote(expr.substring(2, typeNameEnd));
				Type type = connector.getSchema().getType().get(typeName);
				if(type == null) throw new IllegalArgumentException("Unable to find SchemaType: " + quote(typeName));

				sql = new SQLCast(sql, type);
				expr = expr.substring(typeNameEnd);
			} else {
				throw new IllegalArgumentException("Unable to parse: " + expr);
			}
		}
		return sql;
	}
}
