/*
 * Copyright 2000-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import com.aoindustries.util.UnionSet;
import com.aoindustries.util.WrappedException;
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
final public class Technology extends AOServObjectIntegerKey implements Comparable<Technology>, DtoFactory<com.aoindustries.aoserv.client.dto.Technology> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private String name;
    private String technologyClass;

    public Technology(AOServConnector<?,?> connector, int pkey, String name, String technologyClass) {
        super(connector, pkey);
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
    public int compareTo(Technology other) {
        try {
            int diff = name==other.name ? 0 : getTechnologyName().compareTo(other.getTechnologyName()); // OK - interned
            if(diff!=0) return diff;
            return technologyClass==other.technologyClass ? 0 : getTechnologyClass().compareTo(other.getTechnologyClass()); // OK - interned
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
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
        return getConnector().getTechnologyNames().get(name);
    }

    static final String COLUMN_CLASS = "class";
    @SchemaColumn(order=2, name=COLUMN_CLASS, index=IndexType.INDEXED, description="the name of the group this package belongs to")
    public TechnologyClass getTechnologyClass() throws RemoteException {
        return getConnector().getTechnologyClasses().get(technologyClass);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    @Override
    public com.aoindustries.aoserv.client.dto.Technology getDto() {
        return new com.aoindustries.aoserv.client.dto.Technology(key, name, technologyClass);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionSet<AOServObject> addDependencies(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = super.addDependencies(unionSet);
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getTechnologyName());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getTechnologyClass());
        return unionSet;
    }
    // </editor-fold>
}
