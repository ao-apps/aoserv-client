/*
 * Copyright 2009-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.beans;

/**
 * @author  AO Industries, Inc.
 */
public class PostgresUser {

    private int aoServerResource;
    private PostgresUserId username;
    private int postgresServer;
    private boolean createdb;
    private boolean trace;
    private boolean superPriv;
    private boolean catupd;
    private String predisablePassword;

    public PostgresUser() {
    }

    public PostgresUser(
        int aoServerResource,
        PostgresUserId username,
        int postgresServer,
        boolean createdb,
        boolean trace,
        boolean superPriv,
        boolean catupd,
        String predisablePassword
    ) {
        this.aoServerResource = aoServerResource;
        this.username = username;
        this.postgresServer = postgresServer;
        this.createdb = createdb;
        this.trace = trace;
        this.superPriv = superPriv;
        this.catupd = catupd;
        this.predisablePassword = predisablePassword;
    }

    public int getAoServerResource() {
        return aoServerResource;
    }

    public void setAoServerResource(int aoServerResource) {
        this.aoServerResource = aoServerResource;
    }

    public PostgresUserId getUsername() {
        return username;
    }

    public void setUsername(PostgresUserId username) {
        this.username = username;
    }

    public int getPostgresServer() {
        return postgresServer;
    }

    public void setPostgresServer(int postgresServer) {
        this.postgresServer = postgresServer;
    }

    public boolean isCreatedb() {
        return createdb;
    }

    public void setCreatedb(boolean createdb) {
        this.createdb = createdb;
    }

    public boolean isTrace() {
        return trace;
    }

    public void setTrace(boolean trace) {
        this.trace = trace;
    }

    public boolean isSuperPriv() {
        return superPriv;
    }

    public void setSuperPriv(boolean superPriv) {
        this.superPriv = superPriv;
    }

    public boolean isCatupd() {
        return catupd;
    }

    public void setCatupd(boolean catupd) {
        this.catupd = catupd;
    }

    public String getPredisablePassword() {
        return predisablePassword;
    }

    public void setPredisablePassword(String predisablePassword) {
        this.predisablePassword = predisablePassword;
    }
}
