/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2001-2009, 2016  AO Industries, Inc.
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

import java.util.AbstractList;
import java.util.List;

/**
 * <code>LinuxID</code>s are not transferred over the network.  Instead,
 * they are generated on the client upon first use.
 *
 * @see  LinuxID
 *
 * @author  AO Industries, Inc.
 */
final public class LinuxIDTable extends AOServTable<Integer,LinuxID> {

	private static final List<LinuxID> ids = new AbstractList<LinuxID>() {

		@Override
		public LinuxID get(int index) {
			if(index<0) throw new IndexOutOfBoundsException("Index below zero: "+index);
			if(index>65535) throw new IndexOutOfBoundsException("Index above 65535: "+index);
			return new LinuxID(index);
		}

		@Override
		public int size() {
			return 65536;
		}

		@Override
		public int indexOf(Object o) {
			if(o!=null && (o instanceof LinuxID)) {
				return ((LinuxID)o).getID();
			}
			return -1;
		}

		@Override
		public int lastIndexOf(Object o) {
			return indexOf(o);
		}
	};

	LinuxIDTable(AOServConnector connector) {
		super(connector, LinuxID.class);
	}

	@Override
	OrderBy[] getDefaultOrderBy() {
		return null;
	}

	@Override
	public LinuxID get(Object id) {
		return get(((Integer)id).intValue());
	}

	public LinuxID get(int id) {
		if(id>=0 && id<=65535) return new LinuxID(id);
		return null;
	}

	@Override
	public List<LinuxID> getRows() {
		return ids;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.LINUX_IDS;
	}

	@Override
	protected LinuxID getUniqueRowImpl(int col, Object value) {
		if(col!=0) throw new IllegalArgumentException("Not a unique column: "+col);
		return get(value);
	}

	@Override
	public boolean isLoaded() {
		return true;
	}
}
