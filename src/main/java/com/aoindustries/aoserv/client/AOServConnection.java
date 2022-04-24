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

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import java.io.Closeable;
import java.io.IOException;

/**
 * An <code>AOServConnection</code> is one stream of communication
 * between the client and the server.
 *
 * @see  AOServConnector
 *
 * @author  AO Industries, Inc.
 */
public abstract class AOServConnection implements Closeable {

  /**
   * The connector that this connection is part of.
   */
  protected final AOServConnector connector;

  /**
   * Creates a new <code>AOServConnection</code>.
   */
  protected AOServConnection(AOServConnector connector) {
    this.connector = connector;
  }

  /**
   * Releases this connection back to the pool.
   *
   * @see  AOServConnector#release(com.aoindustries.aoserv.client.AOServConnection)
   */
  @Override
  public void close() throws IOException {
    connector.release(this);
  }

  /**
   * Closes this connection to the server so that a reconnect is forced in the future.
   * Adds any new throwables to {@code t0} via {@link Throwables#addSuppressed(java.lang.Throwable, java.lang.Throwable)}.
   */
  abstract Throwable abort(Throwable t0);

  /**
   * Gets the stream to write to the server.
   */
  abstract StreamableOutput getRequestOut(AoservProtocol.CommandID commID) throws IOException;

  /**
   * Gets the stream to read from the server.
   */
  abstract StreamableInput getResponseIn() throws IOException;
}
