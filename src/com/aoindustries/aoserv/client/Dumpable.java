package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

/**
 * Flags an <code>AOServObject</code>s as being able to dump its contents into
 * a <code>Writer</code>.
 *
 * @see  AOServObject
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
public interface Dumpable {
    /**
     * Dumps the contents of this object into a <code>Writer</code>
     */
    void dump(PrintWriter out) throws IOException, SQLException;
}
