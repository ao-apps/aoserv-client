package com.aoindustries.aoserv.client.noswing;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.Architecture;
import com.aoindustries.aoserv.client.ArchitectureService;

/**
 * @author  AO Industries, Inc.
 */
final class NoSwingArchitectureService extends NoSwingServiceStringKey<Architecture> implements ArchitectureService<NoSwingConnector,NoSwingConnectorFactory> {

    NoSwingArchitectureService(NoSwingConnector connector, ArchitectureService<?,?> wrapped) {
        super(connector, Architecture.class, wrapped);
    }
}
