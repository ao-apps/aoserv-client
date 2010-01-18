/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.InetAddress;
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;
import java.util.Set;

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
    final private UserId username;
    final private InetAddress host;

    public MasterHost(
        MasterHostService<?,?> service,
        int pkey,
        UserId username,
        InetAddress host
    ) {
        super(service, pkey);
        this.username = username.intern();
        this.host = host.intern();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(MasterHost other) throws RemoteException {
        int diff = username.equals(other.username) ? 0 : getMasterUser().compareTo(other.getMasterUser());
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
    public com.aoindustries.aoserv.client.beans.MasterHost getBean() {
        return new com.aoindustries.aoserv.client.beans.MasterHost(key, username.getBean(), host.getBean());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependencies() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getMasterUser()
        );
    }
    // </editor-fold>
}