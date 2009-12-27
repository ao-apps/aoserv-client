package com.aoindustries.aoserv.client.noswing;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.OperatingSystem;
import com.aoindustries.aoserv.client.OperatingSystemService;

/**
 * @author  AO Industries, Inc.
 */
final class NoSwingOperatingSystemService extends NoSwingServiceStringKey<OperatingSystem> implements OperatingSystemService<NoSwingConnector,NoSwingConnectorFactory> {

    NoSwingOperatingSystemService(NoSwingConnector connector, OperatingSystemService<?,?> wrapped) {
        super(connector, OperatingSystem.class, wrapped);
    }
}
