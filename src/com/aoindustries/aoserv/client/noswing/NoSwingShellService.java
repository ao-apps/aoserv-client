package com.aoindustries.aoserv.client.noswing;

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
final class NoSwingShellService extends NoSwingServiceUnixPathKey<Shell> implements ShellService<NoSwingConnector,NoSwingConnectorFactory> {

    NoSwingShellService(NoSwingConnector connector, ShellService<?,?> wrapped) {
        super(connector, Shell.class, wrapped);
    }
}
