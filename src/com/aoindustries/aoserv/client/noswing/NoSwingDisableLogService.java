package com.aoindustries.aoserv.client.noswing;

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
final class NoSwingDisableLogService extends NoSwingServiceIntegerKey<DisableLog> implements DisableLogService<NoSwingConnector,NoSwingConnectorFactory> {

    NoSwingDisableLogService(NoSwingConnector connector, DisableLogService<?,?> wrapped) {
        super(connector, DisableLog.class, wrapped);
    }
}
