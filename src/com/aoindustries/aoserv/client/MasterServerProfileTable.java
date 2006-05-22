package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.*;
import com.aoindustries.util.WrappedException;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  MasterServerProfile
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class MasterServerProfileTable extends AOServTable<String,MasterServerProfile> {

    MasterServerProfileTable(AOServConnector connector) {
	super(connector, MasterServerProfile.class);
    }

    public MasterServerProfile get(Object key) {
        for(MasterServerProfile msp : getRows()) if(key.equals(msp.getKey())) return msp;
        return null;
    }

    public List<MasterServerProfile> getRows() {
        List<MasterServerProfile> list=new ArrayList<MasterServerProfile>();
        getObjects(list, AOServProtocol.GET_TABLE, SchemaTable.MASTER_SERVER_PROFILE);
        return list;
    }

    int getTableID() {
	return SchemaTable.MASTER_SERVER_PROFILE;
    }

    protected MasterServerProfile getUniqueRowImpl(int col, Object value) {
        throw new IllegalArgumentException("Not a unique column: "+col);
    }
}
