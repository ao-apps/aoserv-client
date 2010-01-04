package com.aoindustries.aoserv.client.noswing;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.LinuxAccountType;
import com.aoindustries.aoserv.client.LinuxAccountTypeService;

/**
 * @author  AO Industries, Inc.
 */
final class NoSwingLinuxAccountTypeService extends NoSwingServiceStringKey<LinuxAccountType> implements LinuxAccountTypeService<NoSwingConnector,NoSwingConnectorFactory> {

    NoSwingLinuxAccountTypeService(NoSwingConnector connector, LinuxAccountTypeService<?,?> wrapped) {
        super(connector, LinuxAccountType.class, wrapped);
    }
}
