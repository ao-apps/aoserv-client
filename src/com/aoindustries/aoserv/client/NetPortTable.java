package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  NetPort
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class NetPortTable extends AOServTable<Integer,NetPort> {

    private static final List<NetPort> netPorts=new AbstractList<NetPort>() {
        public NetPort get(int index) {
            if(index<0) throw new IndexOutOfBoundsException("Index below zero: "+index);
            if(index>65534) throw new IndexOutOfBoundsException("Index above 65534: "+index);
            return new NetPort(index+1);
        }
        
        public int size() {
            return 65535;
        }
        
        public int indexOf(Object o) {
            if(o!=null && (o instanceof NetPort)) {
                return ((NetPort)o).getPort()-1;
            }
            return -1;
        }

        public int lastIndexOf(Object o) {
            return indexOf(o);
        }
    };

    NetPortTable(AOServConnector connector) {
	super(connector, NetPort.class);
    }

    public NetPort get(Object port) {
        return get(((Integer)port).intValue());
    }

    public NetPort get(int port) {
	if(port>=1 && port<=65535) return new NetPort(port);
	return null;
    }

    public List<NetPort> getRows() {
        return netPorts;
    }

    int getTableID() {
	return SchemaTable.NET_PORTS;
    }

    protected NetPort getUniqueRowImpl(int col, Object value) {
        if(col!=0) throw new IllegalArgumentException("Not a unique column: "+col);
        return get(value);
    }

    public boolean isLoaded() {
	return true;
    }
}
