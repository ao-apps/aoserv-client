/*
 * Copyright 2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.util.WrappedException;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterates through all objects in a connector.
 *
 * @author  AO Industries, Inc.
 */
public class AOServConnectorIterator implements Iterator<AOServObject<?>> {

    private final AOServConnector conn;

    private int serviceIndex;
    private Iterator<? extends AOServObject<?>> serviceIterator;

    public AOServConnectorIterator(AOServConnector conn) throws RemoteException {
        this.conn = conn;
        while(serviceIndex<ServiceName.values.size()) {
            Iterator<? extends AOServObject<?>> newServiceIterator = conn.getServices().get(ServiceName.values.get(serviceIndex)).getSet().iterator();
            if(newServiceIterator.hasNext()) {
                serviceIterator = newServiceIterator;
                break;
            }
            serviceIndex++;
        }
    }

    @Override
    public boolean hasNext() {
        return serviceIndex < ServiceName.values.size();
    }

    @Override
    public AOServObject<?> next() {
        try {
            if(serviceIterator==null) throw new NoSuchElementException();
            AOServObject<?> obj = serviceIterator.next();
            if(!serviceIterator.hasNext()) {
                serviceIndex++;
                serviceIterator = null;
                while(serviceIndex<ServiceName.values.size()) {
                    Iterator<? extends AOServObject<?>> newServiceIterator = conn.getServices().get(ServiceName.values.get(serviceIndex)).getSet().iterator();
                    if(newServiceIterator.hasNext()) {
                        serviceIterator = newServiceIterator;
                        break;
                    }
                    serviceIndex++;
                }
            }
            return obj;
        } catch(RemoteException exc) {
            throw new WrappedException(exc);
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
