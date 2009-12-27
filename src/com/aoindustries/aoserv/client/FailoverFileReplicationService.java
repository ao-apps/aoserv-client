package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * @see FailoverFileReplication
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.failover_file_replications)
public interface FailoverFileReplicationService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends AOServServiceIntegerKey<C,F,FailoverFileReplication> {

    /* TODO
    List<FailoverFileReplication> getFailoverFileReplications(Server server) throws IOException, SQLException {
        return getIndexedRows(FailoverFileReplication.COLUMN_SERVER, server.pkey);
    }
    */
}