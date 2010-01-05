package com.aoindustries.aoserv.client.noswing;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.LinuxAccount;
import com.aoindustries.aoserv.client.LinuxAccountService;

/**
 * @author  AO Industries, Inc.
 */
final class NoSwingLinuxAccountService extends NoSwingServiceIntegerKey<LinuxAccount> implements LinuxAccountService<NoSwingConnector,NoSwingConnectorFactory> {

    NoSwingLinuxAccountService(NoSwingConnector connector, LinuxAccountService<?,?> wrapped) {
        super(connector, LinuxAccount.class, wrapped);
    }
}
