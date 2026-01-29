/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2017, 2018, 2019, 2020, 2021, 2022, 2025, 2026  AO Industries, Inc.
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

import com.aoapps.hodgepodge.io.stream.Streamable;
import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import java.io.IOException;

/**
 * {@link Streamable} with a given version represented by {@link com.aoindustries.aoserv.client.schema.AoservProtocol.Version}.
 *
 * @author  AO Industries, Inc.
 */
public interface AoservStreamable extends Streamable, AoservReadable, AoservWritable {

  /**
   * @deprecated  This is maintained only for compatibility with the {@link Streamable} interface.
   *
   * @see  AoservStreamable#read(StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated
  @Override
  void read(StreamableInput in, String protocolVersion) throws IOException;

  // TODO: Java 1.8: default method (or inherit from AoservReadable)
  // read(in, com.aoindustries.aoserv.client.schema.AoservProtocol.Version.getVersion(version));

  @Override
  void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException;

  /**
   * @deprecated  This is maintained only for compatibility with the {@link Streamable} interface.
   *
   * @see  AoservStreamable#write(StreamableOutput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated
  @Override
  void write(StreamableOutput out, String protocolVersion) throws IOException;

  // TODO: Java 1.8: default method (or inherit from AoservWritable)
  // write(out, com.aoindustries.aoserv.client.schema.AoservProtocol.Version.getVersion(version));

  @Override
  void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException;
}
