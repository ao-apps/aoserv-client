package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.IOException;

/**
 * Flags an <code>AOServObject</code>s as being able to dump its contents into
 * a <code>Writer</code>.
 *
 * @see  AOServObject
 *
 * @author  AO Industries, Inc.
 */
public interface Dumpable {

    /**
     * Dumps the contents of this object into an <code>Appendable</code>
     */
    void dump(Appendable out) throws IOException;
}
