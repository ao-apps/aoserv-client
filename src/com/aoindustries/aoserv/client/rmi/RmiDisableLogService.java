package com.aoindustries.aoserv.client.rmi;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.DisableLog;
import com.aoindustries.aoserv.client.DisableLogService;

/**
 * @author  AO Industries, Inc.
 */
final class RmiDisableLogService extends RmiServiceIntegerKey<DisableLog> implements DisableLogService<RmiConnector,RmiConnectorFactory> {

    RmiDisableLogService(RmiConnector connector) {
        super(connector, DisableLog.class);
    }
}
