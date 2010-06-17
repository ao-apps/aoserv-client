package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.validator.Hostname;
import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;
import java.util.Set;

/**
 * A limited number of hosts may connect to a <code>AOServer</code>'s daemon,
 * each is configured as an <code>AOServerDaemonHost</code>.
 *
 * @see  Server
 *
 * @author  AO Industries, Inc.
 */
final public class AOServerDaemonHost extends AOServObjectIntegerKey<AOServerDaemonHost> implements BeanFactory<com.aoindustries.aoserv.client.beans.AOServerDaemonHost> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private int aoServer;
    private Hostname host;

    public AOServerDaemonHost(
        AOServerDaemonHostService<?,?> service,
        int pkey,
        int aoServer,
        Hostname host
    ) {
        super(service, pkey);
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
    protected int compareToImpl(AOServerDaemonHost other) throws RemoteException {
        int diff = aoServer==other.aoServer ? 0 : getAoServer().compareToImpl(other.getAoServer());
        if(diff!=0) return diff;
        return host.compareTo(other.host);
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
        return getService().getConnector().getAoServers().get(aoServer);
    }

    @SchemaColumn(order=2, name="host", description="the hostname or IP address that is allowed to connect")
    public Hostname getHost() {
        return host;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.AOServerDaemonHost getBean() {
        return new com.aoindustries.aoserv.client.beans.AOServerDaemonHost(key, aoServer, getBean(host));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependencies() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getAoServer()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() throws RemoteException {
    	return host+"->"+getAoServer().toStringImpl();
    }
    // </editor-fold>
}
