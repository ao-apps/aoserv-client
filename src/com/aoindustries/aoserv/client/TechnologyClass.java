/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.Set;

/**
 * A <code>TechnologyClass</code> is one type of software package
 * installed on the servers.
 *
 * @see  Technology
 *
 * @author  AO Industries, Inc.
 */
final public class TechnologyClass extends AOServObjectStringKey<TechnologyClass> implements BeanFactory<com.aoindustries.aoserv.client.beans.TechnologyClass> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

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
    public TechnologyClass(TechnologyClassService<?,?> service, String name) {
        super(service, name);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="name", index=IndexType.PRIMARY_KEY, description="the name of the class")
    public String getName() {
    	return getKey();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.TechnologyClass getBean() {
        return new com.aoindustries.aoserv.client.beans.TechnologyClass(getKey());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getTechnologies()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    public String getDescription(Locale userLocale) {
        return ApplicationResources.accessor.getMessage(userLocale, "TechnologyClass."+getKey()+".description");
    }

    @Override
    String toStringImpl(Locale userLocale) throws RemoteException {
        return ApplicationResources.accessor.getMessage(userLocale, "TechnologyClass."+getKey()+".toString");
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public IndexedSet<Technology> getTechnologies() throws RemoteException {
        return getService().getConnector().getTechnologies().filterIndexed(Technology.COLUMN_CLASS, this);
    }
    // </editor-fold>
}
