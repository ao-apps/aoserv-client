/*
 * Copyright 2000-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import com.aoindustries.util.WrappedException;
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
final public class HttpdJKProtocol extends AOServObjectStringKey implements Comparable<HttpdJKProtocol>, DtoFactory<com.aoindustries.aoserv.client.dto.HttpdJKProtocol> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    public static final String
        AJP12="ajp12",
        AJP13="ajp13"
    ;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    public HttpdJKProtocol(AOServConnector connector, String protocol) {
        super(connector, protocol);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(HttpdJKProtocol other) {
        try {
            return getKey()==other.getKey() ? 0 : getProtocol().compareTo(other.getProtocol());
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    static final String COLUMN_PROTOCOL = "protocol";
    @DependencySingleton
    @SchemaColumn(order=0, name=COLUMN_PROTOCOL, index=IndexType.PRIMARY_KEY, description="the protocol used, as found in protocols.protocol")
    public Protocol getProtocol() throws RemoteException {
        return getConnector().getProtocols().get(getKey());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public HttpdJKProtocol(AOServConnector connector, com.aoindustries.aoserv.client.dto.HttpdJKProtocol dto) {
        this(connector, dto.getProtocol());
    }

    @Override
    public com.aoindustries.aoserv.client.dto.HttpdJKProtocol getDto() {
        return new com.aoindustries.aoserv.client.dto.HttpdJKProtocol(getKey());
    }
    // </editor-fold>
}