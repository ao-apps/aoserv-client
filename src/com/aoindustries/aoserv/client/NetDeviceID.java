package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * An <code>NetDeviceID</code> is a simple wrapper for the
 * different names of network devices used in Linux servers.
 *
 * @see  NetDevice
 *
 * @author  AO Industries, Inc.
 */
final public class NetDeviceID extends AOServObjectStringKey<NetDeviceID> implements BeanFactory<com.aoindustries.aoserv.client.beans.NetDeviceID> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    public static final String
        BOND0="bond0",
        LO="lo",
        ETH0="eth0",
        ETH1="eth1",
        ETH2="eth2",
        ETH3="eth3",
        ETH4="eth4",
        ETH5="eth5",
        ETH6="eth6"
    ;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private boolean isLoopback;

    public NetDeviceID(NetDeviceIDService<?,?> service, String name, boolean isLoopback) {
        super(service, name);
        this.isLoopback = isLoopback;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="name", index=IndexType.PRIMARY_KEY, description="the unique name of the device")
    public String getName() {
    	return key;
    }

    @SchemaColumn(order=1, name="is_loopback", description="if the device is the loopback device")
    public boolean isLoopback() {
        return isLoopback;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.NetDeviceID getBean() {
        return new com.aoindustries.aoserv.client.beans.NetDeviceID(key, isLoopback);
    }
    // </editor-fold>
}
