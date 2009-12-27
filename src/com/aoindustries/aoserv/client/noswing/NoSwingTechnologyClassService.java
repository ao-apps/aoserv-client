package com.aoindustries.aoserv.client.noswing;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.TechnologyClass;
import com.aoindustries.aoserv.client.TechnologyClassService;

/**
 * @author  AO Industries, Inc.
 */
final class NoSwingTechnologyClassService extends NoSwingServiceStringKey<TechnologyClass> implements TechnologyClassService<NoSwingConnector,NoSwingConnectorFactory> {

    NoSwingTechnologyClassService(NoSwingConnector connector, TechnologyClassService<?,?> wrapped) {
        super(connector, TechnologyClass.class, wrapped);
    }
}
