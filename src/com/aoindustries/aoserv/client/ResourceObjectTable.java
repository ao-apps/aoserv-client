package com.aoindustries.aoserv.client;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * @see  ResourceObject
 *
 * @author  AO Industries, Inc.
 */
public abstract class ResourceObjectTable<V extends ResourceObject<V>> extends CachedTableIntegerKey<V> {

    ResourceObjectTable(AOServConnector connector, Class<V> clazz) {
        super(connector, clazz);
    }
}
