/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2009, 2016, 2017, 2018  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.master;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.AOServTable;
import com.aoindustries.aoserv.client.schema.AOServProtocol;
import com.aoindustries.aoserv.client.schema.SchemaTable;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see  MasterProcess
 *
 * @author  AO Industries, Inc.
 */
final public class MasterProcessTable extends AOServTable<Long,MasterProcess> {

	public MasterProcessTable(AOServConnector connector) {
		super(connector, MasterProcess.class);
	}

	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return null;
	}

	/**
	 * @deprecated  Always try to lookup by specific keys; the compiler will help you more when types change.
	 */
	@Deprecated
	@Override
	public MasterProcess get(Object pid) throws IOException, SQLException {
		return get(((Long)pid).longValue());
	}

	public MasterProcess get(long pid) throws IOException, SQLException {
		for(MasterProcess mp : getRows()) if(mp.process_id==pid) return mp;
		return null;
	}

	@Override
	public List<MasterProcess> getRows() throws IOException, SQLException {
		List<MasterProcess> list=new ArrayList<>();
		getObjects(true, list, AOServProtocol.CommandID.GET_TABLE, SchemaTable.TableID.MASTER_PROCESSES);
		return list;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.MASTER_PROCESSES;
	}

	@Override
	protected MasterProcess getUniqueRowImpl(int col, Object value) throws IOException, SQLException {
		if(col!=0) throw new IllegalArgumentException("Not a unique column: "+col);
		return get(value);
	}
}
