package com.aoindustries.aoserv.client.cache;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.Shell;
import com.aoindustries.aoserv.client.ShellService;

/**
 * @author  AO Industries, Inc.
 */
final class CachedShellService extends CachedServiceUnixPathKey<Shell> implements ShellService<CachedConnector,CachedConnectorFactory> {

    CachedShellService(CachedConnector connector, ShellService<?,?> wrapped) {
        super(connector, Shell.class, wrapped);
    }
}
