/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2017, 2022  AO Industries, Inc.
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

import java.io.IOException;
import java.io.OutputStream;

/**
 * Handles a response stream.
 *
 * @author  AO Industries, Inc.
 */
public interface StreamHandler {

  /**
   * Called once the dump size is known and before
   * the stream is obtained.
   *
   * @param  dumpSize  The number of bytes that will be transferred or {@code -1} if unknown
   */
  void onDumpSize(long dumpSize) throws IOException;

  /**
   * Gets the output to write to.  This output stream will neither be flushed nor closed.
   */
  OutputStream getOut() throws IOException;
}
