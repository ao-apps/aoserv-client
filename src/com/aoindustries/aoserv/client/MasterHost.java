/*
 * Copyright 2001-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.InetAddress;
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.UnionSet;
import java.rmi.RemoteException;

/**
 * A <code>MasterHost</code> controls which hosts a <code>MasterUser</code>
 * is allowed to connect from.  Because <code>MasterUser</code>s have more
 * control over the system, this extra security measure is taken.
 *
 * @see  MasterUser
 *
 * @author  AO Industries, Inc.
 */
final public class MasterHost extends AOServObjectIntegerKey<MasterHost> implements BeanFactory<com.aoindustries.aoserv.client.beans.MasterHost> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private UserId username;
    private InetAddress host;

    public MasterHost(
        MasterHostService<?,?> service,
        int pkey,
        UserId username,
        InetAddress host
    ) {
        super(service, pkey);
        this.username = username;
        this.host = host;
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        username = intern(username);
        host = intern(host);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(MasterHost other) throws RemoteException {
        int diff = username==other.username ? 0 : getMasterUser().compareToImpl(other.getMasterUser()); // OK - interned
        if(diff!=0) return diff;
        return host.compareTo(other.host);
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

    @SchemaColumn(order=2, name="host", description="the IP address they are allowed to connect from")
    public InetAddress getHost() {
        return host;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    @Override
    public com.aoindustries.aoserv.client.beans.MasterHost getBean() {
        return new com.aoindustries.aoserv.client.beans.MasterHost(key, getBean(username), getBean(host));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionSet<AOServObject> addDependencies(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getMasterUser());
        return unionSet;
    }
    // </editor-fold>
}