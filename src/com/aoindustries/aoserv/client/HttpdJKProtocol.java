/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.rmi.RemoteException;

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
        return key.equals(other.key) ? 0 : getProtocol().compareTo(other.getProtocol());
    }
    // </editor-fold>

    public Protocol getProtocol() throws RemoteException {
        return getService().getConnector().getProtocols().get(key);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(HttpdJKProtocol.COLUMN_PROTOCOL_name, ASCENDING)
    };
}