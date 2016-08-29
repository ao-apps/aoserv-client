package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;

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