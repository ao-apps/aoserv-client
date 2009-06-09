package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.MethodProfile;
import com.aoindustries.profiler.Profiler;
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

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(ClientJvmProfile.COLUMN_CLASSNAME_name, ASCENDING),
        new OrderBy(ClientJvmProfile.COLUMN_METHOD_NAME_name, ASCENDING),
        new OrderBy(ClientJvmProfile.COLUMN_PARAMETER_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public ClientJvmProfile get(Object pkey) {
        throw new UnsupportedOperationException("There is no unique key on the ClientJvmProfile table");
    }

    public List<ClientJvmProfile> getRows() throws SQLException, IOException {
        List<MethodProfile> mps=Profiler.getMethodProfiles();
        int len=mps.size();
        List<ClientJvmProfile> cjps=new ArrayList<ClientJvmProfile>(len);
        for(int c=0;c<len;c++) {
            cjps.add(ClientJvmProfile.getClientJvmProfile(mps.get(c)));
        }
        sortIfNeeded(cjps);
        return cjps;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.CLIENT_JVM_PROFILE;
    }

    protected ClientJvmProfile getUniqueRowImpl(int col, Object value) {
        throw new IllegalArgumentException("Not a unique column: "+col);
    }
}