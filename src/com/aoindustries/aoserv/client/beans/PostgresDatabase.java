/*
 * Copyright 2009-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.beans;

/**
 * @author  AO Industries, Inc.
 */
public class PostgresDatabase {

    private int aoServerResource;
    private PostgresDatabaseName name;
    private int postgresServer;
    private int datdba;
    private int encoding;
    private boolean isTemplate;
    private boolean allowConn;
    private boolean enablePostgis;

    public PostgresDatabase() {
    }

    public PostgresDatabase(
        int aoServerResource,
        PostgresDatabaseName name,
        int postgresServer,
        int datdba,
        int encoding,
        boolean isTemplate,
        boolean allowConn,
        boolean enablePostgis
    ) {
        this.aoServerResource = aoServerResource;
        this.name = name;
        this.postgresServer = postgresServer;
        this.datdba = datdba;
        this.encoding = encoding;
        this.isTemplate = isTemplate;
        this.allowConn = allowConn;
        this.enablePostgis = enablePostgis;
    }

    public int getAoServerResource() {
        return aoServerResource;
    }

    public void setAoServerResource(int aoServerResource) {
        this.aoServerResource = aoServerResource;
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

    public boolean isIsTemplate() {
        return isTemplate;
    }

    public void setIsTemplate(boolean isTemplate) {
        this.isTemplate = isTemplate;
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
