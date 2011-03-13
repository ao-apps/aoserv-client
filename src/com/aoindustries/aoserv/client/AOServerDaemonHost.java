/*
 * Copyright 2001-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.UnionClassSet;
import com.aoindustries.util.WrappedException;
import java.rmi.RemoteException;

/**
 * A limited number of hosts may connect to a <code>AOServer</code>'s daemon,
 * each is configured as an <code>AOServerDaemonHost</code>.
 *
 * @see  Server
 *
 * @author  AO Industries, Inc.
 */
final public class AOServerDaemonHost extends AOServObjectIntegerKey implements Comparable<AOServerDaemonHost>, DtoFactory<com.aoindustries.aoserv.client.dto.AOServerDaemonHost> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private int aoServer;
    private Hostname host;

    public AOServerDaemonHost(
        AOServConnector connector,
        int pkey,
        int aoServer,
        Hostname host
    ) {
        super(connector, pkey);
        this.aoServer = aoServer;
        this.host = host;
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        host = intern(host);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(AOServerDaemonHost other) {
        try {
            int diff = aoServer==other.aoServer ? 0 : getAoServer().compareTo(other.getAoServer());
            if(diff!=0) return diff;
            return host.compareTo(other.host);
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="pkey", index=IndexType.PRIMARY_KEY, description="a generated unique primary key")
    public int getPkey() {
        return key;
    }

    static final String COLUMN_AO_SERVER = "ao_server";
    @SchemaColumn(order=1, name=COLUMN_AO_SERVER, index=IndexType.INDEXED, description="the pkey of the ao_server")
    public AOServer getAoServer() throws RemoteException {
        return getConnector().getAoServers().get(aoServer);
    }

    @SchemaColumn(order=2, name="host", description="the hostname or IP address that is allowed to connect")
    public Hostname getHost() {
        return host;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public AOServerDaemonHost(AOServConnector connector, com.aoindustries.aoserv.client.dto.AOServerDaemonHost dto) throws ValidationException {
        this(
            connector,
            dto.getPkey(),
            dto.getAoServer(),
            getHostname(dto.getHost())
        );
    }
    @Override
    public com.aoindustries.aoserv.client.dto.AOServerDaemonHost getDto() {
        return new com.aoindustries.aoserv.client.dto.AOServerDaemonHost(key, aoServer, getDto(host));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionClassSet<AOServObject<?>> addDependencies(UnionClassSet<AOServObject<?>> unionSet) throws RemoteException {
        unionSet = super.addDependencies(unionSet);
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getAoServer());
        return unionSet;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() throws RemoteException {
    	return host+"->"+getAoServer().toStringImpl();
    }
    // </editor-fold>
}
