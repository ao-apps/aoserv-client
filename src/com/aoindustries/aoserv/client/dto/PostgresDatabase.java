/*
 * Copyright 2009-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
public class PostgresDatabase extends AOServerResource {

    private PostgresDatabaseName name;
    private int postgresServer;
    private int datdba;
    private int encoding;
    private boolean template;
    private boolean allowConn;
    private boolean enablePostgis;

    public PostgresDatabase() {
    }

    public PostgresDatabase(
        int pkey,
        String resourceType,
        AccountingCode accounting,
        long created,
        UserId createdBy,
        Integer disableLog,
        long lastEnabled,
        int aoServer,
        int businessServer,
        PostgresDatabaseName name,
        int postgresServer,
        int datdba,
        int encoding,
        boolean template,
        boolean allowConn,
        boolean enablePostgis
    ) {
        super(pkey, resourceType, accounting, created, createdBy, disableLog, lastEnabled, aoServer, businessServer);
        this.name = name;
        this.postgresServer = postgresServer;
        this.datdba = datdba;
        this.encoding = encoding;
        this.template = template;
        this.allowConn = allowConn;
        this.enablePostgis = enablePostgis;
    }

    public PostgresDatabaseName getName() {
        return name;
    }

    public void setName(PostgresDatabaseName name) {
        this.name = name;
    }

    public int getPostgresServer() {
        return postgresServer;
    }

    public void setPostgresServer(int postgresServer) {
        this.postgresServer = postgresServer;
    }

    public int getDatdba() {
        return datdba;
    }

    public void setDatdba(int datdba) {
        this.datdba = datdba;
    }

    public int getEncoding() {
        return encoding;
    }

    public void setEncoding(int encoding) {
        this.encoding = encoding;
    }

    public boolean isTemplate() {
        return template;
    }

    public void setTemplate(boolean template) {
        this.template = template;
    }

    public boolean isAllowConn() {
        return allowConn;
    }

    public void setAllowConn(boolean allowConn) {
        this.allowConn = allowConn;
    }

    public boolean isEnablePostgis() {
        return enablePostgis;
    }

    public void setEnablePostgis(boolean enablePostgis) {
        this.enablePostgis = enablePostgis;
    }
}
