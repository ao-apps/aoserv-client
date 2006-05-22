package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.profiler.*;
import java.io.*;

/**
 * An <code>AOServConnection</code> is one stream of communication
 * between the client and the server.
 *
 * @see  AOServConnector
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
abstract public class AOServConnection {

    /**
     * The connector that this connection is part of.
     */
    protected final AOServConnector connector;

    /**
     * Creates a new <code>AOServConnection</code>.
     */
    protected AOServConnection(AOServConnector connector) {
        this.connector=connector;
    }

    /**
     * Closes this connection to the server
     * so that a reconnect is forced in the
     * future.
     */
    abstract void close();

    /**
     * Gets the stream to read from the server.
     */
    abstract CompressedDataInputStream getInputStream();

    /**
     * Gets the stream to write to the server.
     */
    abstract CompressedDataOutputStream getOutputStream();
}