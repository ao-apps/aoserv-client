/*
 * Copyright 2001-2013 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Flags an <code>AOServObject</code>s as being able to be removed
 * with a call to the <code>remove()</code> method.
 *
 * @see  AOServObject
 *
 * @author  AO Industries, Inc.
 */
public interface Removable {
    
    /**
     * Lists the reasons an object may not be removed.
     *
     * @return  an empty <code>List<CannotRemoveReason></code> if this object may be removed, or a list of descriptions
     */
    List<CannotRemoveReason> getCannotRemoveReasons() throws IOException, SQLException;

    /**
     * Removes this object, and all dependant objects, from the system.
     */
    void remove() throws IOException, SQLException;
}