package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * @see FailoverMySQLReplication
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.failover_mysql_replications)
public interface FailoverMySQLReplicationService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends AOServService<C,F,Integer,FailoverMySQLReplication> {

    /* TODO
    List<FailoverMySQLReplication> getFailoverMySQLReplications(Business bu) throws IOException, SQLException {
        List<FailoverMySQLReplication> matches = new ArrayList<FailoverMySQLReplication>();
        List<MySQLServer> mss = bu.getMysqlServers();
        for(MySQLServer ms : mss) {
            matches.addAll(ms.getFailoverMySQLReplications());
        }
        return matches;
    }

    List<FailoverMySQLReplication> getFailoverMySQLReplications(MySQLServer mysqlServer) throws IOException, SQLException {
        return getIndexedRows(FailoverMySQLReplication.COLUMN_MYSQL_SERVER, mysqlServer.pkey);
    }

    List<FailoverMySQLReplication> getFailoverMySQLReplications(AOServer aoServer) throws IOException, SQLException {
        return getIndexedRows(FailoverMySQLReplication.COLUMN_AO_SERVER, aoServer.pkey);
    }

    List<FailoverMySQLReplication> getFailoverMySQLReplications(FailoverFileReplication replication) throws IOException, SQLException {
        return getIndexedRows(FailoverMySQLReplication.COLUMN_REPLICATION, replication.pkey);
    }
    */
}
