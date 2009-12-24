package com.aoindustries.aoserv.client.noswing;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.Username;
import com.aoindustries.aoserv.client.UsernameService;

/**
 * @author  AO Industries, Inc.
 */
final class NoSwingUsernameService extends NoSwingServiceStringKey<Username> implements UsernameService<NoSwingConnector,NoSwingConnectorFactory> {

    NoSwingUsernameService(NoSwingConnector connector, UsernameService<?,?> wrapped) {
        super(connector, Username.class, wrapped);
    }
}
