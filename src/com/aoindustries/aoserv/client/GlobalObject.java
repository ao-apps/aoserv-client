package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * A <code>GlobalObject</code> is stored in
 * a <code>GlobalTable</code> and shared by all users
 * for greater performance.
 *
 * @author  AO Industries, Inc.
 */
abstract public class GlobalObject<K,T extends GlobalObject<K,T>> extends AOServObject<K,T> {

    protected GlobalObject() {
    }

    /**
     * Global objects are shared between all connections and therefore have no dependencies.
     */
    final public List<? extends AOServObject> getDependencies() throws IOException, SQLException {
        return createDependencyList();
    }

    /**
     * Global objects are shared between all connections and therefore have no dependent objects.
     */
    final public List<? extends AOServObject> getDependentObjects() throws IOException, SQLException {
        return createDependencyList();
    }
}
