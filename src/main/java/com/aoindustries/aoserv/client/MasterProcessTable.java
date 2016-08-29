package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
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

    @Override
    OrderBy[] getDefaultOrderBy() {
        return null;
    }

    public MasterProcess get(Object pid) throws IOException, SQLException {
        return get(((Long)pid).longValue());
    }

    public MasterProcess get(long pid) throws IOException, SQLException {
        for(MasterProcess mp : getRows()) if(mp.process_id==pid) return mp;
        return null;
    }

    public List<MasterProcess> getRows() throws IOException, SQLException {
        List<MasterProcess> list=new ArrayList<MasterProcess>();
        getObjects(true, list, AOServProtocol.CommandID.GET_TABLE, SchemaTable.TableID.MASTER_PROCESSES);
        return list;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.MASTER_PROCESSES;
    }

    protected MasterProcess getUniqueRowImpl(int col, Object value) throws IOException, SQLException {
        if(col!=0) throw new IllegalArgumentException("Not a unique column: "+col);
        return get(value);
    }
}
