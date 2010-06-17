package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;
import java.util.Set;

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
    private String name;
    private String technologyClass;

    public Technology(TechnologyService<?,?> service, int pkey, String name, String technologyClass) {
        super(service, pkey);
        this.name = name;
        this.technologyClass = technologyClass;
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        name = intern(name);
        technologyClass = intern(technologyClass);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(Technology other) throws RemoteException {
        int diff = name==other.name ? 0 : getTechnologyName().compareToImpl(other.getTechnologyName()); // OK - interned
        if(diff!=0) return diff;
        return technologyClass==other.technologyClass ? 0 : getTechnologyClass().compareToImpl(other.getTechnologyClass()); // OK - interned
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="pkey", index=IndexType.PRIMARY_KEY, description="the unique identifier")
    public int getPkey() {
        return key;
    }

    static final String COLUMN_NAME = "name";
    @SchemaColumn(order=1, name=COLUMN_NAME, index=IndexType.INDEXED, description="the name of the package")
    public TechnologyName getTechnologyName() throws RemoteException {
        return getService().getConnector().getTechnologyNames().get(name);
    }

    static final String COLUMN_CLASS = "class";
    @SchemaColumn(order=2, name=COLUMN_CLASS, index=IndexType.INDEXED, description="the name of the group this package belongs to")
    public TechnologyClass getTechnologyClass() throws RemoteException {
        return getService().getConnector().getTechnologyClasses().get(technologyClass);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    @Override
    public com.aoindustries.aoserv.client.beans.Technology getBean() {
        return new com.aoindustries.aoserv.client.beans.Technology(key, name, technologyClass);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependencies() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getTechnologyName(),
            getTechnologyClass()
        );
    }
    // </editor-fold>
}
