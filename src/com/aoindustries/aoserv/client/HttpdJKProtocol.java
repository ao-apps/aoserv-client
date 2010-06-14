/*
 * Copyright 2000-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;
import java.util.Set;

/**
 * Apache's <code>mod_jk</code> supports multiple versions of the
 * Apache JServ Protocol.  Both Apache and Tomcat must be using
 * the same protocol for communication.  The protocol is represented
 * by an <code>HttpdJKProtocol</code>.
 *
 * @see  HttpdWorker
 * @see  Protocol
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdJKProtocol extends AOServObjectStringKey<HttpdJKProtocol> implements BeanFactory<com.aoindustries.aoserv.client.beans.HttpdJKProtocol> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    public static final String
        AJP12="ajp12",
        AJP13="ajp13"
    ;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    public HttpdJKProtocol(HttpdJKProtocolService<?,?> service, String protocol) {
        super(service, protocol);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(HttpdJKProtocol other) throws RemoteException {
        return getKey()==other.getKey() ? 0 : getProtocol().compareTo(other.getProtocol());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    static final String COLUMN_PROTOCOL = "protocol";
    @SchemaColumn(order=0, name=COLUMN_PROTOCOL, index=IndexType.PRIMARY_KEY, description="the protocol used, as found in protocols.protocol")
    public Protocol getProtocol() throws RemoteException {
        return getService().getConnector().getProtocols().get(getKey());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.HttpdJKProtocol getBean() {
        return new com.aoindustries.aoserv.client.beans.HttpdJKProtocol(getKey());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependencies() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getProtocol()
        );
    }
    // </editor-fold>
}