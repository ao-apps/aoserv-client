package com.aoindustries.aoserv.client.noswing;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.TechnologyVersion;
import com.aoindustries.aoserv.client.TechnologyVersionService;

/**
 * @author  AO Industries, Inc.
 */
final class NoSwingTechnologyVersionService extends NoSwingServiceIntegerKey<TechnologyVersion> implements TechnologyVersionService<NoSwingConnector,NoSwingConnectorFactory> {

    NoSwingTechnologyVersionService(NoSwingConnector connector, TechnologyVersionService<?,?> wrapped) {
        super(connector, TechnologyVersion.class, wrapped);
    }
}
