/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2018, 2020  AO Industries, Inc.
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

import java.util.Collections;
import java.util.List;

/**
 * @author  AO Industries, Inc.
 */
abstract public class Schema implements SchemaParent {

	protected final AOServConnector connector;

	protected Schema(AOServConnector connector) {
		this.connector = connector;
	}

	final public AOServConnector getConnector() {
		return connector;
	}

	/**
	 * Gets an unmodifiable list of all of the direct sub-schemas of this schema.
	 *
	 * @implSpec  This default implementation returns {@link Collections#emptyList()}.
	 */
	@Override
	public List<? extends Schema> getSchemas() {
		return Collections.emptyList();
	}

	/**
	 * Gets an unmodifiable list of all of the tables in the schema.
	 */
	abstract public List<? extends AOServTable<?,?>> getTables();

	/**
	 * Gets the unique name of the schema.
	 */
	abstract public String getName();

	/**
	 * @see  #getName()
	 */
	@Override
	final public String toString() {
		return getName();
	}
}
