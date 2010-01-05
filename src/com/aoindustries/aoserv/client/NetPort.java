/*
 * Copyright 2001-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectInputValidation;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * Several network resources on a <code>Server</code> require a unique
 * port.  All of the possible network ports are represented by
 * <code>NetPort</code>s.
 *
 * @author  AO Industries, Inc.
 */
final public class NetPort implements Comparable<NetPort>, Serializable, ObjectInputValidation, BeanFactory<com.aoindustries.aoserv.client.beans.NetPort> {

    private static final long serialVersionUID = 1L;

    private static final AtomicReferenceArray<NetPort> cache = new AtomicReferenceArray<NetPort>(65536);

    public static NetPort valueOf(int port) {
        if(port<1) throw new IllegalArgumentException("port<1: "+port);
        if(port>65535) throw new IllegalArgumentException("port>65535: "+port);
        while(true) {
            NetPort existing = cache.get(port);
            if(existing!=null) return existing;
            NetPort newObj = new NetPort(port);
            if(cache.compareAndSet(port, null, newObj)) return newObj;
        }
    }

    final private int port;

    private NetPort(int port) {
        this.port=port;
        validate();
    }

    private void validate() {
        if(port<1) throw new IllegalArgumentException("port<1: "+port);
        if(port>65535) throw new IllegalArgumentException("port>65535: "+port);
    }

    /**
     * Perform same validation as constructor on readObject.
     */
    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.registerValidation(this, 0);
        ois.defaultReadObject();
    }

    public void validateObject() throws InvalidObjectException {
        try {
            validate();
        } catch(IllegalArgumentException err) {
            InvalidObjectException newErr = new InvalidObjectException(err.getMessage());
            newErr.initCause(err);
            throw newErr;
        }
    }

    private Object readResolve() {
        return valueOf(port);
    }

    @Override
    public boolean equals(Object O) {
    	return
            O!=null
            && O instanceof NetPort
            && ((NetPort)O).port==port
    	;
    }

    @Override
    public int hashCode() {
        return port;
    }

    public int compareTo(NetPort other) {
        return AOServObject.compare(port, other.port);
    }

    @Override
    public String toString() {
        return Integer.toString(port);
    }

    public int getPort() {
        return port;
    }

    public boolean isUser() {
        return port>=1024;
    }

    public com.aoindustries.aoserv.client.beans.NetPort getBean() {
        return new com.aoindustries.aoserv.client.beans.NetPort(port);
    }
}