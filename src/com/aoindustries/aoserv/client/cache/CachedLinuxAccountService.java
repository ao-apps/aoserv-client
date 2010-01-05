package com.aoindustries.aoserv.client.cache;

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
final class CachedLinuxAccountService extends CachedServiceIntegerKey<LinuxAccount> implements LinuxAccountService<CachedConnector,CachedConnectorFactory> {

    CachedLinuxAccountService(CachedConnector connector, LinuxAccountService<?,?> wrapped) {
        super(connector, LinuxAccount.class, wrapped);
    }
}
