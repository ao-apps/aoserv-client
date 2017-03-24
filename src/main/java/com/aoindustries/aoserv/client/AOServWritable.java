/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2017  AO Industries, Inc.
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

import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.io.CompressedWritable;
import java.io.IOException;

/**
 * Something that can be written with a given version represented by {@link AOServProtocol.Version}.
 *
 * @author  AO Industries, Inc.
 */
public interface AOServWritable extends CompressedWritable {

	/**
	 *
	 * @deprecated  This is maintained only for compatibility with the {@link CompressedWritable} interface.
	 * 
	 * @see  #write(CompressedDataOutputStream,AOServProtocol.Version)
	 */
	@Deprecated
	@Override
	void write(CompressedDataOutputStream out, String version) throws IOException;
	// TODO: Java 1.8: default method
	// write(out, AOServProtocol.Version.getVersion(version));

	void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException;
}
