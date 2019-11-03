/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2017, 2018, 2019  AO Industries, Inc.
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

import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.io.stream.Streamable;
import com.aoindustries.io.stream.StreamableInput;
import com.aoindustries.io.stream.StreamableOutput;
import java.io.IOException;

/**
 * {@link Streamable} with a given version represented by {@link AOServProtocol.Version}.
 *
 * @author  AO Industries, Inc.
 */
public interface AOServStreamable extends Streamable, AOServReadable, AOServWritable {

	/**
	 *
	 * @deprecated  This is maintained only for compatibility with the {@link Streamable} interface.
	 * 
	 * @see  #read(StreamableInput,AOServProtocol.Version)
	 */
	@Deprecated
	@Override
	void read(StreamableInput in, String protocolVersion) throws IOException;
	// Java 1.8: default method (or inherit from AOServReadable)
	// read(in, AOServProtocol.Version.getVersion(version));

	@Override
	void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException;

	/**
	 *
	 * @deprecated  This is maintained only for compatibility with the {@link Streamable} interface.
	 * 
	 * @see  #write(StreamableOutput,AOServProtocol.Version)
	 */
	@Deprecated
	@Override
	void write(StreamableOutput out, String protocolVersion) throws IOException;
	// Java 1.8: default method (or inherit from AOServWritable)
	// write(out, AOServProtocol.Version.getVersion(version));

	@Override
	void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException;
}
