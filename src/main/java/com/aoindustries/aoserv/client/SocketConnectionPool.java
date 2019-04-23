/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2009, 2016, 2017, 2018, 2019  AO Industries, Inc.
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
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.io.AOPool;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Connections made by <code>TCPConnector</code> or any
 * of its derivatives are pooled and reused.
 *
 * @see  TCPConnector
 *
 * @author  AO Industries, Inc.
 */
final class SocketConnectionPool extends AOPool<SocketConnection,IOException,InterruptedIOException> {

	public static final int DELAY_TIME=3*60*1000;
	public static final int MAX_IDLE_TIME=15*60*1000;

	private final TCPConnector connector;

	SocketConnectionPool(TCPConnector connector, Logger logger) {
		super(DELAY_TIME, MAX_IDLE_TIME, SocketConnectionPool.class.getName()+"?hostname=" + connector.hostname+"&port="+connector.port+"&connectAs="+connector.connectAs+"&authenticateAs="+connector.authenticateAs, connector.poolSize, connector.maxConnectionAge, logger);
		this.connector=connector;
	}

	@Override
	protected void close(SocketConnection conn) {
		conn.close();
	}

	@Override
	protected SocketConnection getConnectionObject() throws InterruptedIOException, IOException {
		return new SocketConnection(connector);
	}

	@Override
	protected boolean isClosed(SocketConnection conn) {
		return conn.isClosed();
	}

	/**
	 * Avoid repeated copies.
	 */
	private static final int numTables = Table.TableID.values().length;

	@Override
	@SuppressWarnings("deprecation")
	protected void printConnectionStats(Appendable out) throws IOException {
		try {
			// Create statistics on the caches
			int totalLoaded=0;
			int totalCaches=0;
			int totalActive=0;
			int totalHashed=0;
			int totalIndexed=0;
			int totalRows=0;
			for(AOServTable<?,?> table : connector.getTables()) {
				totalLoaded++;
				if(table instanceof CachedTable<?,?>) {
					totalCaches++;
					int columnCount=table.getTableSchema().getSchemaColumns(connector).size();
					CachedTable<?,?> cached=(CachedTable<?,?>)table;
					if(cached.isLoaded()) {
						totalActive++;
						for(int d=0;d<columnCount;d++) {
							if(cached.isHashed(d)) totalHashed++;
							if(cached.isIndexed(d)) totalIndexed++;
						}
						totalRows+=cached.size();
					}
				} else if(table instanceof GlobalTable<?,?>) {
					totalCaches++;
					int columnCount=table.getTableSchema().getSchemaColumns(connector).size();
					GlobalTable<?,?> global=(GlobalTable<?,?>)table;
					if(global.isLoaded()) {
						totalActive++;
						for(int d=0;d<columnCount;d++) {
							if(global.isHashed(d)) totalHashed++;
							if(global.isIndexed(d)) totalIndexed++;
						}
						totalRows+=global.size();
					}
				}
			}

			// Show the table statistics
			out.append("  <tr><th colspan='2'><span style='font-size:large;'>AOServ Tables</span></th></tr>\n"
					+ "  <tr><td>Total Tables:</td><td>").append(Integer.toString(numTables)).append("</td></tr>\n"
					+ "  <tr><td>Loaded:</td><td>").append(Integer.toString(totalLoaded)).append("</td></tr>\n"
					+ "  <tr><td>Caches:</td><td>").append(Integer.toString(totalCaches)).append("</td></tr>\n"
					+ "  <tr><td>Active:</td><td>").append(Integer.toString(totalActive)).append("</td></tr>\n"
					+ "  <tr><td>Hashed:</td><td>").append(Integer.toString(totalHashed)).append("</td></tr>\n"
					+ "  <tr><td>Indexes:</td><td>").append(Integer.toString(totalIndexed)).append("</td></tr>\n"
					+ "  <tr><td>Total Rows:</td><td>").append(Integer.toString(totalRows)).append("</td></tr>\n"
					+ "</table>\n"
					+ "<br /><br />\n"
					+ "<table>\n"
					+ "  <tr><th colspan='2'><span style='font-size:large;'>TCP Connection Pool</span></th></tr>\n"
					+ "  <tr><td>Host:</td><td>");
			com.aoindustries.util.EncodingUtils.encodeHtml(connector.hostname, out);
			out.append("</td></tr>\n"
					+ "  <tr><td>Port:</td><td>").append(Integer.toString(connector.port.getPort())).append("</td></tr>\n"
					+ "  <tr><td>Connected As:</td><td>");
			com.aoindustries.util.EncodingUtils.encodeHtml(connector.connectAs, out);
			out.append("</td></tr>\n"
					+ "  <tr><td>Authenticated As:</td><td>");
			com.aoindustries.util.EncodingUtils.encodeHtml(connector.authenticateAs, out);
			out.append("</td></tr>\n"
					+ "  <tr><td>Password:</td><td>");
			String password=connector.password;
			int len=password.length();
			for(int c=0;c<len;c++) {
				out.append('*');
			}
			out.append("</td></tr>\n");
		} catch(SQLException err) {
			throw new IOException(err);
		}
	}

	@Override
	protected void resetConnection(SocketConnection conn) {
	}

	@Override
	protected IOException newException(String message, Throwable cause) {
		IOException err=new IOException(message);
		if(cause!=null) err.initCause(cause);
		return err;
	}

	@Override
	protected InterruptedIOException newInterruptedException(String message, Throwable cause) {
		InterruptedIOException err = new InterruptedIOException(message);
		if(cause!=null) err.initCause(cause);
		return err;
	}
}
