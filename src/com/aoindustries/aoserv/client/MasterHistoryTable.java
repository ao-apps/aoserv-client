package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.util.ArrayList;
import java.util.List;

/**
 * @see  MasterHistory
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class MasterHistoryTable extends AOServTable<Long,MasterHistory> {

    MasterHistoryTable(AOServConnector connector) {
	super(connector, MasterHistory.class);
    }

    @Override
    OrderBy[] getDefaultOrderBy() {
        return null;
    }

    public MasterHistory get(Object commandID) {
        return get(((Long)commandID).longValue());
    }

    public MasterHistory get(long commandID) {
        for(MasterHistory mh : getRows()) if(mh.command_id==commandID) return mh;
        return null;
    }

    public List<MasterHistory> getRows() {
        List<MasterHistory> list=new ArrayList<MasterHistory>();
        getObjects(list, AOServProtocol.CommandID.GET_TABLE, SchemaTable.TableID.MASTER_HISTORY);
        return list;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.MASTER_HISTORY;
    }

    protected MasterHistory getUniqueRowImpl(int col, Object value) {
        if(col!=0) throw new IllegalArgumentException("Not a unique column: "+col);
        return get(value);
    }
}
