/*
 * Copyright 2009-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
public class MySQLUser extends AOServerResource {

    private MySQLUserId username;
    private int mysqlServer;
    private InetAddress host;
    private boolean selectPriv;
    private boolean insertPriv;
    private boolean updatePriv;
    private boolean deletePriv;
    private boolean createPriv;
    private boolean dropPriv;
    private boolean reloadPriv;
    private boolean shutdownPriv;
    private boolean processPriv;
    private boolean filePriv;
    private boolean grantPriv;
    private boolean referencesPriv;
    private boolean indexPriv;
    private boolean alterPriv;
    private boolean showDbPriv;
    private boolean superPriv;
    private boolean createTmpTablePriv;
    private boolean lockTablesPriv;
    private boolean executePriv;
    private boolean replSlavePriv;
    private boolean replClientPriv;
    private boolean createViewPriv;
    private boolean showViewPriv;
    private boolean createRoutinePriv;
    private boolean alterRoutinePriv;
    private boolean createUserPriv;
    private boolean eventPriv;
    private boolean triggerPriv;
    private String predisablePassword;
    private int maxQuestions;
    private int maxUpdates;
    private int maxConnections;
    private int maxUserConnections;

    public MySQLUser() {
    }

    public MySQLUser(
        int pkey,
        String resourceType,
        AccountingCode accounting,
        long created,
        UserId createdBy,
        Integer disableLog,
        long lastEnabled,
        int aoServer,
        int businessServer,
        MySQLUserId username,
        int mysqlServer,
        InetAddress host,
        boolean selectPriv,
        boolean insertPriv,
        boolean updatePriv,
        boolean deletePriv,
        boolean createPriv,
        boolean dropPriv,
        boolean reloadPriv,
        boolean shutdownPriv,
        boolean processPriv,
        boolean filePriv,
        boolean grantPriv,
        boolean referencesPriv,
        boolean indexPriv,
        boolean alterPriv,
        boolean showDbPriv,
        boolean superPriv,
        boolean createTmpTablePriv,
        boolean lockTablesPriv,
        boolean executePriv,
        boolean replSlavePriv,
        boolean replClientPriv,
        boolean createViewPriv,
        boolean showViewPriv,
        boolean createRoutinePriv,
        boolean alterRoutinePriv,
        boolean createUserPriv,
        boolean eventPriv,
        boolean triggerPriv,
        String predisablePassword,
        int maxQuestions,
        int maxUpdates,
        int maxConnections,
        int maxUserConnections
    ) {
        super(pkey, resourceType, accounting, created, createdBy, disableLog, lastEnabled, aoServer, businessServer);
        this.username = username;
        this.mysqlServer = mysqlServer;
        this.host = host;
        this.selectPriv = selectPriv;
        this.insertPriv = insertPriv;
        this.updatePriv = updatePriv;
        this.deletePriv = deletePriv;
        this.createPriv = createPriv;
        this.dropPriv = dropPriv;
        this.reloadPriv = reloadPriv;
        this.shutdownPriv = shutdownPriv;
        this.processPriv = processPriv;
        this.filePriv = filePriv;
        this.grantPriv = grantPriv;
        this.referencesPriv = referencesPriv;
        this.indexPriv = indexPriv;
        this.alterPriv = alterPriv;
        this.showDbPriv = showDbPriv;
        this.superPriv = superPriv;
        this.createTmpTablePriv = createTmpTablePriv;
        this.lockTablesPriv = lockTablesPriv;
        this.executePriv = executePriv;
        this.replSlavePriv = replSlavePriv;
        this.replClientPriv = replClientPriv;
        this.createViewPriv = createViewPriv;
        this.showViewPriv = showViewPriv;
        this.createRoutinePriv = createRoutinePriv;
        this.alterRoutinePriv = alterRoutinePriv;
        this.createUserPriv = createUserPriv;
        this.eventPriv = eventPriv;
        this.triggerPriv = triggerPriv;
        this.predisablePassword = predisablePassword;
        this.maxQuestions = maxQuestions;
        this.maxUpdates = maxUpdates;
        this.maxConnections = maxConnections;
        this.maxUserConnections = maxUserConnections;
    }

    public MySQLUserId getUsername() {
        return username;
    }

    public void setUsername(MySQLUserId username) {
        this.username = username;
    }

    public int getMysqlServer() {
        return mysqlServer;
    }

    public void setMysqlServer(int mysqlServer) {
        this.mysqlServer = mysqlServer;
    }

    public InetAddress getHost() {
        return host;
    }

    public void setHost(InetAddress host) {
        this.host = host;
    }

    public boolean getSelectPriv() {
        return selectPriv;
    }

    public void setSelectPriv(boolean selectPriv) {
        this.selectPriv = selectPriv;
    }

    public boolean getInsertPriv() {
        return insertPriv;
    }

    public void setInsertPriv(boolean insertPriv) {
        this.insertPriv = insertPriv;
    }

    public boolean getUpdatePriv() {
        return updatePriv;
    }

    public void setUpdatePriv(boolean updatePriv) {
        this.updatePriv = updatePriv;
    }

    public boolean getDeletePriv() {
        return deletePriv;
    }

    public void setDeletePriv(boolean deletePriv) {
        this.deletePriv = deletePriv;
    }

    public boolean getCreatePriv() {
        return createPriv;
    }

    public void setCreatePriv(boolean createPriv) {
        this.createPriv = createPriv;
    }

    public boolean getDropPriv() {
        return dropPriv;
    }

    public void setDropPriv(boolean dropPriv) {
        this.dropPriv = dropPriv;
    }

    public boolean getReloadPriv() {
        return reloadPriv;
    }

    public void setReloadPriv(boolean reloadPriv) {
        this.reloadPriv = reloadPriv;
    }

    public boolean getShutdownPriv() {
        return shutdownPriv;
    }

    public void setShutdownPriv(boolean shutdownPriv) {
        this.shutdownPriv = shutdownPriv;
    }

    public boolean getProcessPriv() {
        return processPriv;
    }

    public void setProcessPriv(boolean processPriv) {
        this.processPriv = processPriv;
    }

    public boolean getFilePriv() {
        return filePriv;
    }

    public void setFilePriv(boolean filePriv) {
        this.filePriv = filePriv;
    }

    public boolean getGrantPriv() {
        return grantPriv;
    }

    public void setGrantPriv(boolean grantPriv) {
        this.grantPriv = grantPriv;
    }

    public boolean getReferencesPriv() {
        return referencesPriv;
    }

    public void setReferencesPriv(boolean referencesPriv) {
        this.referencesPriv = referencesPriv;
    }

    public boolean getIndexPriv() {
        return indexPriv;
    }

    public void setIndexPriv(boolean indexPriv) {
        this.indexPriv = indexPriv;
    }

    public boolean getAlterPriv() {
        return alterPriv;
    }

    public void setAlterPriv(boolean alterPriv) {
        this.alterPriv = alterPriv;
    }

    public boolean getShowDbPriv() {
        return showDbPriv;
    }

    public void setShowDbPriv(boolean showDbPriv) {
        this.showDbPriv = showDbPriv;
    }

    public boolean getSuperPriv() {
        return superPriv;
    }

    public void setSuperPriv(boolean superPriv) {
        this.superPriv = superPriv;
    }

    public boolean getCreateTmpTablePriv() {
        return createTmpTablePriv;
    }

    public void setCreateTmpTablePriv(boolean createTmpTablePriv) {
        this.createTmpTablePriv = createTmpTablePriv;
    }

    public boolean getLockTablesPriv() {
        return lockTablesPriv;
    }

    public void setLockTablesPriv(boolean lockTablesPriv) {
        this.lockTablesPriv = lockTablesPriv;
    }

    public boolean getExecutePriv() {
        return executePriv;
    }

    public void setExecutePriv(boolean executePriv) {
        this.executePriv = executePriv;
    }

    public boolean getReplSlavePriv() {
        return replSlavePriv;
    }

    public void setReplSlavePriv(boolean replSlavePriv) {
        this.replSlavePriv = replSlavePriv;
    }

    public boolean getReplClientPriv() {
        return replClientPriv;
    }

    public void setReplClientPriv(boolean replClientPriv) {
        this.replClientPriv = replClientPriv;
    }

    public boolean getCreateViewPriv() {
        return createViewPriv;
    }

    public void setCreateViewPriv(boolean createViewPriv) {
        this.createViewPriv = createViewPriv;
    }

    public boolean getShowViewPriv() {
        return showViewPriv;
    }

    public void setShowViewPriv(boolean showViewPriv) {
        this.showViewPriv = showViewPriv;
    }

    public boolean getCreateRoutinePriv() {
        return createRoutinePriv;
    }

    public void setCreateRoutinePriv(boolean createRoutinePriv) {
        this.createRoutinePriv = createRoutinePriv;
    }

    public boolean getAlterRoutinePriv() {
        return alterRoutinePriv;
    }

    public void setAlterRoutinePriv(boolean alterRoutinePriv) {
        this.alterRoutinePriv = alterRoutinePriv;
    }

    public boolean getCreateUserPriv() {
        return createUserPriv;
    }

    public void setCreateUserPriv(boolean createUserPriv) {
        this.createUserPriv = createUserPriv;
    }

    public boolean getEventPriv() {
        return eventPriv;
    }

    public void setEventPriv(boolean eventPriv) {
        this.eventPriv = eventPriv;
    }

    public boolean getTriggerPriv() {
        return triggerPriv;
    }

    public void setTriggerPriv(boolean triggerPriv) {
        this.triggerPriv = triggerPriv;
    }

    public String getPredisablePassword() {
        return predisablePassword;
    }

    public void setPredisablePassword(String predisablePassword) {
        this.predisablePassword = predisablePassword;
    }

    public int getMaxQuestions() {
        return maxQuestions;
    }

    public void setMaxQuestions(int maxQuestions) {
        this.maxQuestions = maxQuestions;
    }

    public int getMaxUpdates() {
        return maxUpdates;
    }

    public void setMaxUpdates(int maxUpdates) {
        this.maxUpdates = maxUpdates;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    public int getMaxUserConnections() {
        return maxUserConnections;
    }

    public void setMaxUserConnections(int maxUserConnections) {
        this.maxUserConnections = maxUserConnections;
    }
}
