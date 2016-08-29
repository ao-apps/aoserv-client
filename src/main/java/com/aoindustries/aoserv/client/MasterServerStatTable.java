/*
 * Copyright 2001-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
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
