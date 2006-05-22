package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.*;

/**
 * A <code>GlobalObject</code> is stored in
 * a <code>GlobalTable</code> and shared by all users
 * for greater performance.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
abstract public class GlobalObject<K,T extends GlobalObject<K,T>> extends AOServObject<K,T> {

    protected GlobalObject() {
    }
}
