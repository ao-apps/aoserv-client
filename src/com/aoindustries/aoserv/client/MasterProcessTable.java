package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.*;
import com.aoindustries.util.WrappedException;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  MasterProcess
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class MasterProcessTable extends AOServTable<Long,MasterProcess> {

    MasterProcessTable(AOServConnector connector) {
	super(connector, MasterProcess.class);
    }

    public MasterProcess get(Object pid) {
        return get((Long)pid);
    }

    public MasterProcess get(long pid) {
        for(MasterProcess mp : getRows()) if(mp.process_id==pid) return mp;
        return null;
    }

    public List<MasterProcess> getRows() {
        List<MasterProcess> list=new ArrayList<MasterProcess>();
        getObjects(list, AOServProtocol.GET_TABLE, SchemaTable.MASTER_PROCESSES);
        return list;
    }

    int getTableID() {
	return SchemaTable.MASTER_PROCESSES;
    }

    protected MasterProcess getUniqueRowImpl(int col, Object value) {
        if(col!=0) throw new IllegalArgumentException("Not a unique column: "+col);
        return get(value);
    }
}
