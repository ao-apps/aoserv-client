package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.io.*;
import java.sql.*;

/**
 * Notified with each object as the table is being loaded.  This
 * is useful so that tasks may be completed during the transfer,
 * which may yield more efficient and interactive environment.
 *
 * @see  AOServTable#addTableLoadListener
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
public interface TableLoadListener {

    /**
     * Called when the table is completely loaded.
     */
    Object tableLoadCompleted(AOServTable table, Object param);

    /**
     * Whenever an <code>AOServTable</code> is starting to be loaded,
     * this is called with the parameter that was provided in
     * the <code>addTableLoadListener</code> call.  The object
     * returned is stored and will be the parameter provided in
     * the next call.
     */
    Object tableLoadStarted(AOServTable table, Object param);

    /**
     * Called as each row is loaded.
     */
    Object tableRowLoaded(AOServTable table, AOServObject object, int rowNumber, Object param);
}