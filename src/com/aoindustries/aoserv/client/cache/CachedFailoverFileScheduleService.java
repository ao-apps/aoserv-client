package com.aoindustries.aoserv.client.cache;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.FailoverFileSchedule;
import com.aoindustries.aoserv.client.FailoverFileScheduleService;

/**
 * @author  AO Industries, Inc.
 */
final class CachedFailoverFileScheduleService extends CachedServiceIntegerKey<FailoverFileSchedule> implements FailoverFileScheduleService<CachedConnector,CachedConnectorFactory> {

    CachedFailoverFileScheduleService(CachedConnector connector, FailoverFileScheduleService<?,?> wrapped) {
        super(connector, FailoverFileSchedule.class, wrapped);
    }
}
