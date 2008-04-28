package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * <code>LinuxID</code>s are not transferred over the network.  Instead,
 * they are generated on the client upon first use.
 *
 * @see  LinuxID
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class LinuxIDTable extends AOServTable<Integer,LinuxID> {

    private static final List<LinuxID> ids=new AbstractList<LinuxID>() {
        public LinuxID get(int index) {
            if(index<0) throw new IndexOutOfBoundsException("Index below zero: "+index);
            if(index>65535) throw new IndexOutOfBoundsException("Index above 65535: "+index);
            return new LinuxID(index);
        }
        
        public int size() {
            return 65536;
        }
        
        public int indexOf(Object o) {
            if(o!=null && (o instanceof LinuxID)) {
                return ((LinuxID)o).getID();
            }
            return -1;
        }

        public int lastIndexOf(Object o) {
            return indexOf(o);
        }
    };

    LinuxIDTable(AOServConnector connector) {
	super(connector, LinuxID.class);
    }

    @Override
    OrderBy[] getDefaultOrderBy() {
        return null;
    }

    @Override
    public LinuxID get(Object id) {
        return get(((Integer)id).intValue());
    }

    public LinuxID get(int id) {
	if(id>=0 && id<=65535) return new LinuxID(id);
        return null;
    }

    @Override
    public List<LinuxID> getRows() {
        return ids;
    }

    @Override
    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.LINUX_IDS;
    }

    @Override
    protected LinuxID getUniqueRowImpl(int col, Object value) {
        if(col!=0) throw new IllegalArgumentException("Not a unique column: "+col);
        return get(value);
    }

    @Override
    public boolean isLoaded() {
	return true;
    }
}
