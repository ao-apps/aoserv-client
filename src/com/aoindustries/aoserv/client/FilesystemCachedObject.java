package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.profiler.*;
import java.io.*;

/**
 * A <code>FilesystemCachedObject</code> is stored in
 * a temporary file on disk for local-speed performance while using
 * minimal heap space.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
public abstract class FilesystemCachedObject<K,T extends FilesystemCachedObject<K,T>> extends AOServObject<K,T> implements SingleTableObject<K,T>, FileListObject {

    protected AOServTable<K,T> table;

    protected FilesystemCachedObject() {
    }
    /*
    public FileListObject createInstance() throws IOException {
        Profiler.startProfile(Profiler.FAST, FilesystemCachedObject.class, "createInstance()", null);
        try {
            T fco=table.getNewObject();
            if(table!=null) fco.setTable(table);
            return fco;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
     */

    /**
     * Gets the <code>AOServTable</code> that contains this <code>AOServObject</code>.
     *
     * @return  the <code>AOServTable</code>.
     */
    final public AOServTable<K,T> getTable() {
        return table;
    }

    final public void setTable(AOServTable<K,T> table) {
        Profiler.startProfile(Profiler.FAST, FilesystemCachedObject.class, "setTable(AOServTable<K,T>)", null);
        try {
            if(this.table!=null) throw new IllegalStateException("table already set");
            this.table=table;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
    
    public abstract void writeRecord(DataOutputStream out) throws IOException;
    
    public abstract void readRecord(DataInputStream in) throws IOException;
}