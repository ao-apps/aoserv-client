/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import java.sql.Timestamp;

/**
 * Multiple versions of Majordomo are supported by the system.
 * Each <code>MajordomoServer</code> is of a specific version,
 * and all its <code>MajordomoList</code>s inherit that
 * <code>MajordomoVersion</code>.
 *
 * @see  MajordomoServer
 *
 * @author  AO Industries, Inc.
 */
final public class MajordomoVersion extends AOServObjectStringKey<MajordomoVersion> implements BeanFactory<com.aoindustries.aoserv.client.beans.MajordomoVersion> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    /**
     * The default Majordomo version.
     */
    public static final String DEFAULT_VERSION="1.94.5";
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private Timestamp created;

    public MajordomoVersion(MajordomoVersionService<?,?> service, String version, Timestamp created) {
        super(service, version);
        this.created = created;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="version", index=IndexType.PRIMARY_KEY, description="the version number")
    public String getVersion() {
        return getKey();
    }

    @SchemaColumn(order=1, name="created", description="the time the version was added")
    public Timestamp getCreated() {
        return created;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.MajordomoVersion getBean() {
        return new com.aoindustries.aoserv.client.beans.MajordomoVersion(getKey(), created);
    }
    // </editor-fold>
}