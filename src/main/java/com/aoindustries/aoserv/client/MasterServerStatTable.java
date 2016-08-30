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

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see  MasterServerStat
 *
 * @author  AO Industries, Inc.
 */
final public class MasterServerStatTable extends AOServTable<String,MasterServerStat> {

	MasterServerStatTable(AOServConnector connector) {
		super(connector, MasterServerStat.class);
	}

	@Override
	OrderBy[] getDefaultOrderBy() {
		return null;
	}

	@Override
	public MasterServerStat get(Object name) throws IOException, SQLException {
		List<MasterServerStat> table=getRows();
		int size=table.size();
		for(int c=0;c<size;c++) {
			MasterServerStat mss=table.get(c);
			if(mss.name.equals(name)) return mss;
		}
		return null;
	}

	@Override
	public List<MasterServerStat> getRows() throws IOException, SQLException {
		List<MasterServerStat> list=new ArrayList<>();
		getObjects(true, list, AOServProtocol.CommandID.GET_TABLE, SchemaTable.TableID.MASTER_SERVER_STATS);
		return list;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.MASTER_SERVER_STATS;
	}

	@Override
	protected MasterServerStat getUniqueRowImpl(int col, Object value) throws IOException, SQLException {
		if(col!=0) throw new IllegalArgumentException("Not a unique column: "+col);
		return get(value);
	}
}
