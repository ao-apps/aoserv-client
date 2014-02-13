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

    @Override
    OrderBy[] getDefaultOrderBy() {
        return null;
    }

    public MasterServerStat get(Object name) throws IOException, SQLException {
        List<MasterServerStat> table=getRows();
        int size=table.size();
        for(int c=0;c<size;c++) {
            MasterServerStat mss=table.get(c);
            if(mss.name.equals(name)) return mss;
        }
        return null;
    }

    public List<MasterServerStat> getRows() throws IOException, SQLException {
        List<MasterServerStat> list=new ArrayList<MasterServerStat>();
        getObjects(true, list, AOServProtocol.CommandID.GET_TABLE, SchemaTable.TableID.MASTER_SERVER_STATS);
        return list;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.MASTER_SERVER_STATS;
    }

    protected MasterServerStat getUniqueRowImpl(int col, Object value) throws IOException, SQLException {
        if(col!=0) throw new IllegalArgumentException("Not a unique column: "+col);
        return get(value);
    }
}