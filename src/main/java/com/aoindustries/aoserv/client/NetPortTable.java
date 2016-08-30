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
 * @see  NetPort
 *
 * @author  AO Industries, Inc.
 */
final public class NetPortTable extends AOServTable<Integer,NetPort> {

	private static final List<NetPort> netPorts = new AbstractList<NetPort>() {

		@Override
		public NetPort get(int index) {
			if(index<0) throw new IndexOutOfBoundsException("Index below zero: "+index);
			if(index>65534) throw new IndexOutOfBoundsException("Index above 65534: "+index);
			return new NetPort(index+1);
		}

		@Override
		public int size() {
			return 65535;
		}

		@Override
		public int indexOf(Object o) {
			if(o!=null && (o instanceof NetPort)) {
				return ((NetPort)o).getPort()-1;
			}
			return -1;
		}

		@Override
		public int lastIndexOf(Object o) {
			return indexOf(o);
		}
	};

	NetPortTable(AOServConnector connector) {
		super(connector, NetPort.class);
	}

	@Override
	OrderBy[] getDefaultOrderBy() {
		return null;
	}

	@Override
	public NetPort get(Object port) {
		return get(((Integer)port).intValue());
	}

	public NetPort get(int port) {
		if(port>=1 && port<=65535) return new NetPort(port);
		return null;
	}

	@Override
	public List<NetPort> getRows() {
		return netPorts;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.NET_PORTS;
	}

	@Override
	protected NetPort getUniqueRowImpl(int col, Object value) {
		if(col!=0) throw new IllegalArgumentException("Not a unique column: "+col);
		return get(value);
	}

	@Override
	public boolean isLoaded() {
		return true;
	}
}
