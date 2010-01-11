package com.aoindustries.aoserv.client.noswing;

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
final class NoSwingFailoverFileScheduleService extends NoSwingServiceIntegerKey<FailoverFileSchedule> implements FailoverFileScheduleService<NoSwingConnector,NoSwingConnectorFactory> {

    NoSwingFailoverFileScheduleService(NoSwingConnector connector, FailoverFileScheduleService<?,?> wrapped) {
        super(connector, FailoverFileSchedule.class, wrapped);
    }
}
