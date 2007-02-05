package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.io.*;
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
void dump(PrintWriter out);
}
