/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.beans;

/**
 * @author  AO Industries, Inc.
 */
public class MySQLDatabase {

    private int aoServerResource;
    private MySQLDatabaseName name;
    private int mysqlServer;

    public MySQLDatabase() {
    }

    public MySQLDatabase(int aoServerResource, MySQLDatabaseName name, int mysqlServer) {
        this.aoServerResource = aoServerResource;
        this.name = name;
        this.mysqlServer = mysqlServer;
    }

    public int getAoServerResource() {
        return aoServerResource;
    }

    public void setAoServerResource(int aoServerResource) {
        this.aoServerResource = aoServerResource;
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
