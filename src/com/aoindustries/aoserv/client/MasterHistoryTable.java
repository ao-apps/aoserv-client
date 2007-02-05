package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.*;
import com.aoindustries.util.WrappedException;
import java.io.*;
import java.sql.*;
import java.util.*;

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

    public MasterHistory get(Object commandID) {
        return get((Long)commandID);
    }

    public MasterHistory get(long commandID) {
        for(MasterHistory mh : getRows()) if(mh.command_id==commandID) return mh;
        return null;
    }

    public List<MasterHistory> getRows() {
        List<MasterHistory> list=new ArrayList<MasterHistory>();
        getObjects(list, AOServProtocol.GET_TABLE, SchemaTable.MASTER_HISTORY);
        return list;
    }

    int getTableID() {
	return SchemaTable.MASTER_HISTORY;
    }

    protected MasterHistory getUniqueRowImpl(int col, Object value) {
        if(col!=0) throw new IllegalArgumentException("Not a unique column: "+col);
        return get(value);
    }
}
