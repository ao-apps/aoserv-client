/*
 * Copyright 2010-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.io.FastObjectInput;
import com.aoindustries.io.FastObjectOutput;
import java.io.IOException;
import java.rmi.RemoteException;

/**
 * An object that uses a int as its key value.
 *
 * @see  AOServService
 *
 * @author  AO Industries, Inc.
 */
abstract public class AOServObjectIntegerKey extends AOServObject<Integer> {

    private int key;

    protected AOServObjectIntegerKey(AOServConnector connector, int key) {
        super(connector);
        this.key = key;
    }

    // <editor-fold defaultstate="collapsed" desc="FastExternalizable">
    private static final long serialVersionUID = -1856816259667938811L;

    protected AOServObjectIntegerKey() {
    }

    @Override
    protected long getSerialVersionUID() {
        return super.getSerialVersionUID() ^ serialVersionUID;
    }

    @Override
    protected void writeExternal(FastObjectOutput fastOut) throws IOException {
        super.writeExternal(fastOut);
        fastOut.writeInt(key);
    }

    @Override
    protected void readExternal(FastObjectInput fastIn) throws IOException, ClassNotFoundException {
        super.readExternal(fastIn);
        key = fastIn.readInt();
    }
    // </editor-fold>

    /**
     * Gets the key value for this object.
     */
    @Override
    final public Integer getKey() {
        return key;
    }
    final protected int getKeyInt() {
        return key;
    }

    @Override
    final public boolean equals(Object o) {
        if(o==null || getClass()!=o.getClass()) return false;
        AOServObjectIntegerKey other = (AOServObjectIntegerKey)o;
        return key==other.key;
    }

    @Override
    final public int hashCode() {
        return key;
    }

    /**
     * The default string representation is that of the key value.
     */
    @Override
    String toStringImpl() throws RemoteException {
        return Integer.toString(key);
    }
}
