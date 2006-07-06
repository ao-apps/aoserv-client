package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  ClientJvmProfile
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class ClientJvmProfileTable extends AOServTable<String,ClientJvmProfile> {

    ClientJvmProfileTable(AOServConnector connector) {
	super(connector, ClientJvmProfile.class);
    }

    public ClientJvmProfile get(Object pkey) {
        return get((String)pkey);
    }

    public List<ClientJvmProfile> getRows() {
        MethodProfile[] mps=Profiler.getMethodProfiles();
        int len=mps.length;
        List<ClientJvmProfile> cjps=new ArrayList<ClientJvmProfile>(len);
        for(int c=0;c<len;c++) {
            cjps.add(ClientJvmProfile.getClientJvmProfile(mps[c]));
        }
        sortIfNeeded(cjps);
        return cjps;
    }

    int getTableID() {
	return SchemaTable.CLIENT_JVM_PROFILE;
    }

    protected ClientJvmProfile getUniqueRowImpl(int col, Object value) {
        throw new IllegalArgumentException("Not a unique column: "+col);
    }
}