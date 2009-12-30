package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.rmi.RemoteException;

/**
 * A <code>Technology</code> associates a <code>TechnologyClass</code>
 * with a <code>TechnologyName</code>.
 *
 * @see  TechnologyClass
 * @see  TechnologyName
 *
 * @author  AO Industries, Inc.
 */
final public class Technology extends AOServObjectIntegerKey<Technology> implements BeanFactory<com.aoindustries.aoserv.client.beans.Technology> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final String name;
    final String technologyClass;

    public Technology(TechnologyService<?,?> service, int pkey, String name, String technologyClass) {
        super(service, pkey);
        this.name = name.intern();
        this.technologyClass = technologyClass.intern();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(Technology other) throws RemoteException {
        int diff = name.equals(other.name) ? 0 : getTechnologyName().compareTo(other.getTechnologyName());
        if(diff!=0) return diff;
        return technologyClass.equals(other.technologyClass) ? 0 : getTechnologyClass().compareTo(other.getTechnologyClass());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="pkey", unique=true, description="the unique identifier")
    public int getPkey() {
        return key;
    }

    @SchemaColumn(order=1, name="name", description="the name of the package")
    public TechnologyName getTechnologyName() throws RemoteException {
        return getService().getConnector().getTechnologyNames().get(name);
    }

    @SchemaColumn(order=2, name="class", description="the name of the group this package belongs to")
    public TechnologyClass getTechnologyClass() throws RemoteException {
        return getService().getConnector().getTechnologyClasses().get(technologyClass);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.Technology getBean() {
        return new com.aoindustries.aoserv.client.beans.Technology(key, name, technologyClass);
    }
    // </editor-fold>
}
