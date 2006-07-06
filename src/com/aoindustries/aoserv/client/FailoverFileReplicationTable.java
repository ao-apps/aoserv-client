package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see FailoverFileReplication
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class FailoverFileReplicationTable extends CachedTableIntegerKey<FailoverFileReplication> {

    FailoverFileReplicationTable(AOServConnector connector) {
	super(connector, FailoverFileReplication.class);
    }

    List<FailoverFileReplication> getFailoverFileReplications(AOServer from_ao_server) {
        int aoPKey=from_ao_server.pkey;

        List<FailoverFileReplication> cached=getRows();
	int len=cached.size();
        List<FailoverFileReplication> matches=new ArrayList<FailoverFileReplication>(len);
	for (int c = 0; c < len; c++) {
            FailoverFileReplication ffr=cached.get(c);
            if(ffr.from_server==aoPKey) matches.add(ffr);
	}
        return matches;
    }

    public FailoverFileReplication get(Object pkey) {
	return getUniqueRow(FailoverFileReplication.COLUMN_PKEY, pkey);
    }

    public FailoverFileReplication get(int pkey) {
	return getUniqueRow(FailoverFileReplication.COLUMN_PKEY, pkey);
    }

    int getTableID() {
	return SchemaTable.FAILOVER_FILE_REPLICATIONS;
    }
}