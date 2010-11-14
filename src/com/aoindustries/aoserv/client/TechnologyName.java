/*
 * Copyright 2000-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import com.aoindustries.util.UnionSet;
import java.rmi.RemoteException;

/**
 * A <code>TechnologyName</code> represents one piece of software installed in
 * the system.
 *
 * @author  AO Industries, Inc.
 */
final public class TechnologyName extends AOServObjectStringKey implements Comparable<TechnologyName>, DtoFactory<com.aoindustries.aoserv.client.dto.TechnologyName> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    public static final String MYSQL = "MySQL";
    public static final String POSTGRESQL = "postgresql";
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    public TechnologyName(AOServConnector connector, String name) {
        super(connector, name);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(TechnologyName other) {
        return AOServObjectUtils.compareIgnoreCaseConsistentWithEquals(getKey(), other.getKey());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="name", index=IndexType.PRIMARY_KEY, description="the name of the package")
    public String getName() {
        return getKey();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    @Override
    public com.aoindustries.aoserv.client.dto.TechnologyName getDto() {
        return new com.aoindustries.aoserv.client.dto.TechnologyName(getKey());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionSet<AOServObject> addDependentObjects(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = super.addDependentObjects(unionSet);
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getTechnologyVersions());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getTechnologies());
        return unionSet;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public IndexedSet<TechnologyVersion> getTechnologyVersions() throws RemoteException {
        return getConnector().getTechnologyVersions().filterIndexed(TechnologyVersion.COLUMN_NAME, this);
    }

    public IndexedSet<Technology> getTechnologies() throws RemoteException {
        return getConnector().getTechnologies().filterIndexed(Technology.COLUMN_NAME, this);
    }

    /* TODO
    public List<Technology> getTechnologies() throws RemoteException {
    	return connector.getTechnologies().getTechnologies(this);
    }*/
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
    /* TODO
    public TechnologyVersion getTechnologyVersion(AOServConnector connector, String version, OperatingSystemVersion osv) throws IOException, SQLException {
        return connector.getTechnologyVersions().getTechnologyVersion(this, version, osv);
    } */
    // </editor-fold>
}
