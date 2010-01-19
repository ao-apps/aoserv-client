package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;
import java.util.Set;

/**
 * <code>MasterUser</code>s are restricted to data based on a list
 * of <code>Server</code>s they may access.  A <code>MasterServer</code>
 * grants a <code>MasterUser</code> permission to data associated with
 * a <code>Server</code>.  If a <code>MasterUser</code> does not have
 * any <code>MasterServer</code>s associated with it, it is granted
 * permissions to all servers.
 *
 * @see  MasterUser
 * @see  Server
 *
 * @author  AO Industries, Inc.
 */
final public class MasterServer extends AOServObjectIntegerKey<MasterServer> implements BeanFactory<com.aoindustries.aoserv.client.beans.MasterServer> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private UserId username;
    final private int server;

    public MasterServer(
        MasterServerService<?,?> service,
        int pkey,
        UserId username,
        int server
    ) {
        super(service, pkey);
        this.username = username;
        this.server = server;
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        username = intern(username);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(MasterServer other) throws RemoteException {
        int diff = username.equals(other.username) ? 0 : getMasterUser().compareTo(other.getMasterUser());
        if(diff!=0) return diff;
        return server==other.server ? 0 : getServer().compareTo(other.getServer());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="pkey", index=IndexType.PRIMARY_KEY, description="a generated unique primary key")
    public int getPkey() {
        return key;
    }

    static final String COLUMN_USERNAME = "username";
    @SchemaColumn(order=1, name=COLUMN_USERNAME, index=IndexType.INDEXED, description="the unique username of the user")
    public MasterUser getMasterUser() throws RemoteException {
        return getService().getConnector().getMasterUsers().get(username);
    }

    static final String COLUMN_SERVER = "server";
    @SchemaColumn(order=2, name=COLUMN_SERVER, index=IndexType.INDEXED, description="the pkey of the server they may control")
    public Server getServer() throws RemoteException {
        return getService().getConnector().getServers().get(server);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.MasterServer getBean() {
        return new com.aoindustries.aoserv.client.beans.MasterServer(key, username.getBean(), server);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependencies() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getMasterUser(),
            getServer()
        );
    }
    // </editor-fold>
}