/*
 * Copyright 2000-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;

/**
 * A <code>TechnologyClass</code> is one type of software package
 * installed on the servers.
 *
 * @see  Technology
 *
 * @author  AO Industries, Inc.
 */
final public class TechnologyClass extends AOServObjectStringKey implements Comparable<TechnologyClass>, DtoFactory<com.aoindustries.aoserv.client.dto.TechnologyClass> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    /**
     * The possible <code>TechnologyClass</code>es.
     */
    public static final String
        APACHE="Apache",
        EMAIL="E-Mail",
        ENCRYPTION="Encryption",
        INTERBASE="InterBase",
        JAVA="Java",
        LINUX="Linux",
        MYSQL="MySQL",
        PERL="PERL",
        PHP="PHP",
        POSTGRESQL="PostgreSQL",
        X11="X11",
        XML="XML"
    ;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private static final long serialVersionUID = 8924692879673552444L;

    public TechnologyClass(AOServConnector connector, String name) {
        super(connector, name);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(TechnologyClass other) {
        return compareIgnoreCaseConsistentWithEquals(getKey(), other.getKey());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, index=IndexType.PRIMARY_KEY, description="the name of the class")
    public String getName() {
    	return getKey();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public TechnologyClass(AOServConnector connector, com.aoindustries.aoserv.client.dto.TechnologyClass dto) {
        this(connector,dto.getName());
    }

    @Override
    public com.aoindustries.aoserv.client.dto.TechnologyClass getDto() {
        return new com.aoindustries.aoserv.client.dto.TechnologyClass(getKey());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    public String getDescription() {
        return ApplicationResources.accessor.getMessage("TechnologyClass."+getKey()+".description");
    }

    @Override
    String toStringImpl() throws RemoteException {
        return ApplicationResources.accessor.getMessage("TechnologyClass."+getKey()+".toString");
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    @DependentObjectSet
    public IndexedSet<Technology> getTechnologies() throws RemoteException {
        return getConnector().getTechnologies().filterIndexed(Technology.COLUMN_TECHNOLOGY_CLASS, this);
    }
    // </editor-fold>
}
