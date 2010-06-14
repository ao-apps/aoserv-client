/*
 * Copyright 2009-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.beans;

/**
 * @author  AO Industries, Inc.
 */
public class MySQLDBUser {

    private int pkey;
    private int mysqlDatabase;
    private int mysqlUser;
    private boolean selectPriv;
    private boolean insertPriv;
    private boolean updatePriv;
    private boolean deletePriv;
    private boolean createPriv;
    private boolean dropPriv;
    private boolean grantPriv;
    private boolean referencesPriv;
    private boolean indexPriv;
    private boolean alterPriv;
    private boolean createTmpTablePriv;
    private boolean lockTablesPriv;
    private boolean createViewPriv;
    private boolean showViewPriv;
    private boolean createRoutinePriv;
    private boolean alterRoutinePriv;
    private boolean executePriv;
    private boolean eventPriv;
    private boolean triggerPriv;

    public MySQLDBUser() {
    }

    public MySQLDBUser(
        int pkey,
        int mysqlDatabase,
        int mysqlUser,
        boolean selectPriv,
        boolean insertPriv,
        boolean updatePriv,
        boolean deletePriv,
        boolean createPriv,
        boolean dropPriv,
        boolean grantPriv,
        boolean referencesPriv,
        boolean indexPriv,
        boolean alterPriv,
        boolean createTmpTablePriv,
        boolean lockTablesPriv,
        boolean createViewPriv,
        boolean showViewPriv,
        boolean createRoutinePriv,
        boolean alterRoutinePriv,
        boolean executePriv,
        boolean eventPriv,
        boolean triggerPriv
    ) {
        this.pkey = pkey;
        this.mysqlDatabase = mysqlDatabase;
        this.mysqlUser = mysqlUser;
        this.selectPriv = selectPriv;
        this.insertPriv = insertPriv;
        this.updatePriv = updatePriv;
        this.deletePriv = deletePriv;
        this.createPriv = createPriv;
        this.dropPriv = dropPriv;
        this.grantPriv = grantPriv;
        this.referencesPriv = referencesPriv;
        this.indexPriv = indexPriv;
        this.alterPriv = alterPriv;
        this.createTmpTablePriv = createTmpTablePriv;
        this.lockTablesPriv = lockTablesPriv;
        this.createViewPriv = createViewPriv;
        this.showViewPriv = showViewPriv;
        this.createRoutinePriv = createRoutinePriv;
        this.alterRoutinePriv = alterRoutinePriv;
        this.executePriv = executePriv;
        this.eventPriv = eventPriv;
        this.triggerPriv = triggerPriv;
    }

    public int getPkey() {
        return pkey;
    }

    public void setPkey(int pkey) {
        this.pkey = pkey;
    }

    public int getMysqlDatabase() {
        return mysqlDatabase;
    }

    public void setMysqlDatabase(int mysqlDatabase) {
        this.mysqlDatabase = mysqlDatabase;
    }

    public int getMysqlUser() {
        return mysqlUser;
    }

    public void setMysqlUser(int mysqlUser) {
        this.mysqlUser = mysqlUser;
    }

    public boolean isSelectPriv() {
        return selectPriv;
    }

    public void setSelectPriv(boolean selectPriv) {
        this.selectPriv = selectPriv;
    }

    public boolean isInsertPriv() {
        return insertPriv;
    }

    public void setInsertPriv(boolean insertPriv) {
        this.insertPriv = insertPriv;
    }

    public boolean isUpdatePriv() {
        return updatePriv;
    }

    public void setUpdatePriv(boolean updatePriv) {
        this.updatePriv = updatePriv;
    }

    public boolean isDeletePriv() {
        return deletePriv;
    }

    public void setDeletePriv(boolean deletePriv) {
        this.deletePriv = deletePriv;
    }

    public boolean isCreatePriv() {
        return createPriv;
    }

    public void setCreatePriv(boolean createPriv) {
        this.createPriv = createPriv;
    }

    public boolean isDropPriv() {
        return dropPriv;
    }

    public void setDropPriv(boolean dropPriv) {
        this.dropPriv = dropPriv;
    }

    public boolean isGrantPriv() {
        return grantPriv;
    }

    public void setGrantPriv(boolean grantPriv) {
        this.grantPriv = grantPriv;
    }

    public boolean isReferencesPriv() {
        return referencesPriv;
    }

    public void setReferencesPriv(boolean referencesPriv) {
        this.referencesPriv = referencesPriv;
    }

    public boolean isIndexPriv() {
        return indexPriv;
    }

    public void setIndexPriv(boolean indexPriv) {
        this.indexPriv = indexPriv;
    }

    public boolean isAlterPriv() {
        return alterPriv;
    }

    public void setAlterPriv(boolean alterPriv) {
        this.alterPriv = alterPriv;
    }

    public boolean isCreateTmpTablePriv() {
        return createTmpTablePriv;
    }

    public void setCreateTmpTablePriv(boolean createTmpTablePriv) {
        this.createTmpTablePriv = createTmpTablePriv;
    }

    public boolean isLockTablesPriv() {
        return lockTablesPriv;
    }

    public void setLockTablesPriv(boolean lockTablesPriv) {
        this.lockTablesPriv = lockTablesPriv;
    }

    public boolean isCreateViewPriv() {
        return createViewPriv;
    }

    public void setCreateViewPriv(boolean createViewPriv) {
        this.createViewPriv = createViewPriv;
    }

    public boolean isShowViewPriv() {
        return showViewPriv;
    }

    public void setShowViewPriv(boolean showViewPriv) {
        this.showViewPriv = showViewPriv;
    }

    public boolean isCreateRoutinePriv() {
        return createRoutinePriv;
    }

    public void setCreateRoutinePriv(boolean createRoutinePriv) {
        this.createRoutinePriv = createRoutinePriv;
    }

    public boolean isAlterRoutinePriv() {
        return alterRoutinePriv;
    }

    public void setAlterRoutinePriv(boolean alterRoutinePriv) {
        this.alterRoutinePriv = alterRoutinePriv;
    }

    public boolean isExecutePriv() {
        return executePriv;
    }

    public void setExecutePriv(boolean executePriv) {
        this.executePriv = executePriv;
    }

    public boolean isEventPriv() {
        return eventPriv;
    }

    public void setEventPriv(boolean eventPriv) {
        this.eventPriv = eventPriv;
    }

    public boolean isTriggerPriv() {
        return triggerPriv;
    }

    public void setTriggerPriv(boolean triggerPriv) {
        this.triggerPriv = triggerPriv;
    }
}
