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

    private int pkey;
    private String name;
    private int mysqlServer;
    private String accounting;

    public MySQLDatabase() {
    }

    public MySQLDatabase(int pkey, String name, int mysqlServer, String accounting) {
        this.pkey = pkey;
        this.name = name;
        this.mysqlServer = mysqlServer;
        this.accounting = accounting;
    }

    public int getPkey() {
        return pkey;
    }

    public void setPkey(int pkey) {
        this.pkey = pkey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMysqlServer() {
        return mysqlServer;
    }

    public void setMysqlServer(int mysqlServer) {
        this.mysqlServer = mysqlServer;
    }

    public String getAccounting() {
        return accounting;
    }

    public void setAccounting(String accounting) {
        this.accounting = accounting;
    }
}
