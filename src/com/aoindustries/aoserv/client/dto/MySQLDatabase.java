/*
 * Copyright 2009-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
public class MySQLDatabase extends AOServerResource {

    private MySQLDatabaseName name;
    private int mysqlServer;

    public MySQLDatabase() {
    }

    public MySQLDatabase(
        int pkey,
        String resourceType,
        AccountingCode accounting,
        long created,
        UserId createdBy,
        Integer disableLog,
        long lastEnabled,
        int aoServer,
        int businessServer,
        MySQLDatabaseName name,
        int mysqlServer
    ) {
        super(pkey, resourceType, accounting, created, createdBy, disableLog, lastEnabled, aoServer, businessServer);
        this.name = name;
        this.mysqlServer = mysqlServer;
    }

    public MySQLDatabaseName getName() {
        return name;
    }

    public void setName(MySQLDatabaseName name) {
        this.name = name;
    }

    public int getMysqlServer() {
        return mysqlServer;
    }

    public void setMysqlServer(int mysqlServer) {
        this.mysqlServer = mysqlServer;
    }
}
