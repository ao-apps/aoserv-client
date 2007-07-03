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
 * @see  MasterServerStat
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class MasterServerStatTable extends AOServTable<String,MasterServerStat> {

    MasterServerStatTable(AOServConnector connector) {
	super(connector, MasterServerStat.class);
    }

    public MasterServerStat get(Object name) {
	List<MasterServerStat> table=getRows();
	int size=table.size();
        for(int c=0;c<size;c++) {
            MasterServerStat mss=table.get(c);
            if(mss.name.equals(name)) return mss;
        }
        return null;
    }

    public List<MasterServerStat> getRows() {
        List<MasterServerStat> list=new ArrayList<MasterServerStat>();
        getObjects(list, AOServProtocol.CommandID.GET_TABLE, SchemaTable.TableID.MASTER_SERVER_STATS);
        return list;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.MASTER_SERVER_STATS;
    }

    protected MasterServerStat getUniqueRowImpl(int col, Object value) {
        if(col!=0) throw new IllegalArgumentException("Not a unique column: "+col);
        return get(value);
    }
}
