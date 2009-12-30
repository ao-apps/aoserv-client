package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * Each <code>NetBind</code> is listening on a <code>NetProtocol</code>.  The
 * protocols include <code>TCP</code>, <code>UDP</code>, and <code>RAW</code>.
 *
 * @see  NetBind
 *
 * @author  AO Industries, Inc.
 */
final public class NetProtocol extends AOServObjectStringKey<NetProtocol> implements BeanFactory<com.aoindustries.aoserv.client.beans.NetProtocol> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    public static final String
        RAW="raw",
        UDP="udp",
        TCP="tcp"
    ;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    public NetProtocol(NetProtocolService<?,?> service, String protocol) {
        super(service, protocol);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="protocol", unique=true, description="the network protocol")
    public String getProtocol() {
        return key;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.NetProtocol getBean() {
        return new com.aoindustries.aoserv.client.beans.NetProtocol(key);
    }
    // </editor-fold>
}
