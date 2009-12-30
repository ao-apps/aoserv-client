package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

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
    final private String description;

    public TechnologyClass(TechnologyClassService<?,?> service, String name, String description) {
        super(service, name);
        this.description = description;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="name", unique=true, description="the name of the class")
    public String getName() {
    	return key;
    }

    @SchemaColumn(order=1, name="description", description="a description of the class")
    public String getDescription() {
        return description;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.TechnologyClass getBean() {
        return new com.aoindustries.aoserv.client.beans.TechnologyClass(key, description);
    }
    // </editor-fold>
}
