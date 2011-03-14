/*
 * Copyright 2004-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.WrappedException;
import java.rmi.RemoteException;

/**
 * Each server may perform TCP redirects via xinetd.
 *
 * @author  AO Industries, Inc.
 */
final public class NetTcpRedirect extends AOServObjectIntegerKey implements Comparable<NetTcpRedirect>, DtoFactory<com.aoindustries.aoserv.client.dto.NetTcpRedirect> {

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private static final long serialVersionUID = 3103910184331373575L;

    final private int cps;
    final private int cpsOverloadSleepTime;
    private Hostname destinationHost;
    final private NetPort destinationPort;

    public NetTcpRedirect(
        AOServConnector connector,
        int netBind,
        int cps,
        int cpsOverloadSleepTime,
        Hostname destinationHost,
        NetPort destinationPort
    ) {
        super(connector, netBind);
        this.cps = cps;
        this.cpsOverloadSleepTime = cpsOverloadSleepTime;
        this.destinationHost = destinationHost;
        this.destinationPort = destinationPort;
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        destinationHost = intern(destinationHost);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(NetTcpRedirect other) {
        try {
            return key==other.key ? 0 : getNetBind().compareTo(other.getNetBind());
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    public static final MethodColumn COLUMN_NET_BIND = getMethodColumn(NetTcpRedirect.class, "netBind");
    @DependencySingleton
    @SchemaColumn(order=0, index=IndexType.PRIMARY_KEY, description="the pkey as found in net_binds")
    public NetBind getNetBind() throws RemoteException {
        return getConnector().getNetBinds().get(key);
    }

    @SchemaColumn(order=1, description="the maximum number of connections per second before the redirect is temporarily disabled")
    public int getConnectionsPerSecond() {
        return cps;
    }

    @SchemaColumn(order=2, description="the number of seconds the service will be disabled")
    public int getConnectionsPerSecondOverloadSleepTime() {
        return cpsOverloadSleepTime;
    }

    @SchemaColumn(order=3, description="the destination IP address or hostname, please note that hostnames are only resolved once on xinetd startup")
    public Hostname getDestinationHost() {
        return destinationHost;
    }

    @SchemaColumn(order=4, description="the remote port to connect to")
    public NetPort getDestinationPort() {
        return destinationPort;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public NetTcpRedirect(AOServConnector connector, com.aoindustries.aoserv.client.dto.NetTcpRedirect dto) throws ValidationException {
        this(
            connector,
            dto.getNetBind(),
            dto.getCps(),
            dto.getCpsOverloadSleepTime(),
            getHostname(dto.getDestinationHost()),
            getNetPort(dto.getDestinationPort())
        );
    }

    @Override
    public com.aoindustries.aoserv.client.dto.NetTcpRedirect getDto() {
        return new com.aoindustries.aoserv.client.dto.NetTcpRedirect(key, cps, cpsOverloadSleepTime, getDto(destinationHost), getDto(destinationPort));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() throws RemoteException {
        String address = destinationHost.toString();
        if(address.indexOf(':')==-1) return getNetBind().toStringImpl()+"->"+destinationHost+':'+destinationPort;
        else return getNetBind().toStringImpl()+"->["+destinationHost+"]:"+destinationPort;
    }
    // </editor-fold>
}
