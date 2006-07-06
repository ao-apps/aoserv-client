package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.util.List;

/**
 * Flags an <code>AOServObject</code>s as being able to be removed
 * with a call to the <code>remove()</code> method.
 *
 * @see  AOServObject
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
public interface Removable {
    
    /**
     * Lists the reasons an object may not be removed.
     *
     * @return  an empty <code>List<CannotRemoveReason></code> if this object may be removed, or a list of descriptions
     */
    List<CannotRemoveReason> getCannotRemoveReasons();

    /**
     * Removes this object, and all dependant objects, from the system.
     */
    void remove();
}