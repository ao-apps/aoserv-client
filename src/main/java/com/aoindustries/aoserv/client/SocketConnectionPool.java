/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2009, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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
 * along with aoserv-client.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.aoindustries.aoserv.client;

import com.aoapps.hodgepodge.io.AOPool;
import com.aoapps.lang.Throwables;
import com.aoindustries.aoserv.client.schema.Table;
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
final class SocketConnectionPool extends AOPool<SocketConnection, IOException, InterruptedIOException> {

  public static final int DELAY_TIME = 3 * 60 * 1000;
  public static final int MAX_IDLE_TIME = 15 * 60 * 1000;

  private final TCPConnector connector;

  SocketConnectionPool(TCPConnector connector, Logger logger) {
    super(
        DELAY_TIME,
        MAX_IDLE_TIME,
        // TODO: EncodeURLComponent this and AOServConnector?
        SocketConnectionPool.class.getName() + "?hostname=" + connector.hostname + "&port=" + connector.port + "&connectAs=" + connector.connectAs + "&authenticateAs=" + connector.authenticateAs,
        connector.poolSize,
        connector.maxConnectionAge,
        logger
    );
    this.connector = connector;
  }

  @Override
  protected void close(SocketConnection conn) throws IOException {
    Throwable t0 = conn.abort(null);
    if (t0 != null) {
      throw Throwables.wrap(t0, IOException.class, IOException::new);
    }
  }

  // Expose to package
  @Override
  protected void release(SocketConnection connection) throws IOException {
    super.release(connection);
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
  protected void printConnectionStats(Appendable out, boolean isXhtml) throws IOException {
    try {
      // Create statistics on the caches
      int totalLoaded = 0;
      int totalCaches = 0;
      int totalActive = 0;
      int totalHashed = 0;
      int totalIndexed = 0;
      int totalRows = 0;
      for (AOServTable<?, ?> table : connector.getTables()) {
        totalLoaded++;
        if (table instanceof CachedTable<?, ?>) {
          totalCaches++;
          int columnCount = table.getTableSchema().getSchemaColumns(connector).size();
          CachedTable<?, ?> cached = (CachedTable<?, ?>) table;
          if (cached.isLoaded()) {
            totalActive++;
            for (int d = 0; d < columnCount; d++) {
              if (cached.isHashed(d)) {
                totalHashed++;
              }
              if (cached.isIndexed(d)) {
                totalIndexed++;
              }
            }
            totalRows += cached.size();
          }
        } else if (table instanceof GlobalTable<?, ?>) {
          totalCaches++;
          int columnCount = table.getTableSchema().getSchemaColumns(connector).size();
          GlobalTable<?, ?> global = (GlobalTable<?, ?>) table;
          if (global.isLoaded()) {
            totalActive++;
            for (int d = 0; d < columnCount; d++) {
              if (global.isHashed(d)) {
                totalHashed++;
              }
              if (global.isIndexed(d)) {
                totalIndexed++;
              }
            }
            totalRows += global.size();
          }
        }
      }

      // Show the table statistics
      out.append("  <thead>\n"
          + "    <tr><th colspan=\"2\"><span style=\"font-size:large\">AOServ Tables</span></th></tr>\n"
          + "  </thead>\n");
      super.printConnectionStats(out, isXhtml);
      out.append("    <tr><td>Total Tables:</td><td>").append(Integer.toString(numTables)).append("</td></tr>\n"
          + "    <tr><td>Loaded:</td><td>").append(Integer.toString(totalLoaded)).append("</td></tr>\n"
          + "    <tr><td>Caches:</td><td>").append(Integer.toString(totalCaches)).append("</td></tr>\n"
          + "    <tr><td>Active:</td><td>").append(Integer.toString(totalActive)).append("</td></tr>\n"
          + "    <tr><td>Hashed:</td><td>").append(Integer.toString(totalHashed)).append("</td></tr>\n"
          + "    <tr><td>Indexes:</td><td>").append(Integer.toString(totalIndexed)).append("</td></tr>\n"
          + "    <tr><td>Total Rows:</td><td>").append(Integer.toString(totalRows)).append("</td></tr>\n"
          + "  </tbody>\n"
          + "</table>\n");
      if (isXhtml) {
        out.append("<br /><br />\n");
      } else {
        out.append("<br><br>\n");
      }
      out.append("<table class=\"ao-grid\">\n"
          + "  <thead>\n"
          + "    <tr><th colspan=\"2\"><span style=\"font-size:large\">TCP Connection Pool</span></th></tr>\n"
          + "  </thead>\n");
      super.printConnectionStats(out, isXhtml);
      out.append("    <tr><td>Host:</td><td>");
      com.aoapps.hodgepodge.util.EncodingUtils.encodeHtml(connector.hostname, out, isXhtml);
      out.append("</td></tr>\n"
          + "    <tr><td>Port:</td><td>").append(Integer.toString(connector.port.getPort())).append("</td></tr>\n"
          + "    <tr><td>Connected As:</td><td>");
      com.aoapps.hodgepodge.util.EncodingUtils.encodeHtml(connector.connectAs, out, isXhtml);
      out.append("</td></tr>\n"
          + "    <tr><td>Authenticated As:</td><td>");
      com.aoapps.hodgepodge.util.EncodingUtils.encodeHtml(connector.authenticateAs, out, isXhtml);
      out.append("</td></tr>\n"
          + "    <tr><td>Password:</td><td>");
      String password = connector.password;
      int len = Math.max(password.length(), 8);
      for (int c = 0; c < len; c++) {
        out.append('*');
      }
      out.append("</td></tr>\n");
    } catch (SQLException err) {
      throw new IOException(err);
    }
  }

  @Override
  protected void resetConnection(SocketConnection conn) {
    // Do nothing
  }

  @Override
  protected IOException newException(String message, Throwable cause) {
    if (cause instanceof IOException) {
      return (IOException) cause;
    }
    if (cause instanceof InterruptedException) {
      return newInterruptedException(message, cause);
    }
    if (message == null) {
      if (cause == null) {
        return new IOException();
      } else {
        return new IOException(cause);
      }
    } else {
      if (cause == null) {
        return new IOException(message);
      } else {
        return new IOException(message, cause);
      }
    }
  }

  @Override
  protected InterruptedIOException newInterruptedException(String message, Throwable cause) {
    // Restore the interrupted status
    Thread.currentThread().interrupt();
    if (cause instanceof InterruptedIOException) {
      return (InterruptedIOException) cause;
    }
    if (message == null) {
      if (cause == null) {
        return new InterruptedIOException();
      } else {
        InterruptedIOException err = new InterruptedIOException(cause.toString());
        err.initCause(cause);
        return err;
      }
    } else {
      if (cause == null) {
        return new InterruptedIOException(message);
      } else {
        InterruptedIOException err = new InterruptedIOException(message);
        err.initCause(cause);
        return err;
      }
    }
  }
}
