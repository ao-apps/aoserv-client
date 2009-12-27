package com.aoindustries.aoserv.client.noswing;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.OperatingSystemVersion;
import com.aoindustries.aoserv.client.OperatingSystemVersionService;

/**
 * @author  AO Industries, Inc.
 */
final class NoSwingOperatingSystemVersionService extends NoSwingServiceIntegerKey<OperatingSystemVersion> implements OperatingSystemVersionService<NoSwingConnector,NoSwingConnectorFactory> {

    NoSwingOperatingSystemVersionService(NoSwingConnector connector, OperatingSystemVersionService<?,?> wrapped) {
        super(connector, OperatingSystemVersion.class, wrapped);
    }
}
