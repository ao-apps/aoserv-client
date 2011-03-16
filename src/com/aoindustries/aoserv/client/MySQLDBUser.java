/*
 * Copyright 2001-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import com.aoindustries.util.WrappedException;
import java.rmi.RemoteException;

/**
 * A <code>MySQLDBUser</code> grants a <code>MySQLUser</code>
 * access to a <code>MySQLDatabase</code>.  The database and
 * user must be on the same server.
 *
 * @see  MySQLDatabase
 * @see  MySQLUser
 *
 * @author  AO Industries, Inc.
 */
final public class MySQLDBUser extends AOServObjectIntegerKey implements Comparable<MySQLDBUser>, DtoFactory<com.aoindustries.aoserv.client.dto.MySQLDBUser> /*, Removable*/ {

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private static final long serialVersionUID = 7261009759252544752L;

    final private int mysqlDatabase;
    final private int mysqlUser;
    final private boolean selectPriv;
    final private boolean insertPriv;
    final private boolean updatePriv;
    final private boolean deletePriv;
    final private boolean createPriv;
    final private boolean dropPriv;
    final private boolean grantPriv;
    final private boolean referencesPriv;
    final private boolean indexPriv;
    final private boolean alterPriv;
    final private boolean createTmpTablePriv;
    final private boolean lockTablesPriv;
    final private boolean createViewPriv;
    final private boolean showViewPriv;
    final private boolean createRoutinePriv;
    final private boolean alterRoutinePriv;
    final private boolean executePriv;
    final private boolean eventPriv;
    final private boolean triggerPriv;

    public MySQLDBUser(
        AOServConnector connector,
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
        super(connector, pkey);
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
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(MySQLDBUser other) {
        try {
            int diff = mysqlDatabase==other.mysqlDatabase ? 0 : getMysqlDatabase().compareTo(other.getMysqlDatabase());
            if(diff!=0) return diff;
            return mysqlUser==other.mysqlUser ? 0 : getMysqlUser().compareTo(other.getMysqlUser());
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, index=IndexType.PRIMARY_KEY, description="a generated primary key")
    public int getPkey() {
        return getKeyInt();
    }

    public static final MethodColumn COLUMN_MYSQL_DATABASE = getMethodColumn(MySQLDBUser.class, "mysqlDatabase");
    @DependencySingleton
    @SchemaColumn(order=1, index=IndexType.INDEXED, description="the pkey in mysql_databases")
    public MySQLDatabase getMysqlDatabase() throws RemoteException {
    	return getConnector().getMysqlDatabases().get(mysqlDatabase);
    }

    public static final MethodColumn COLUMN_MYSQL_USER = getMethodColumn(MySQLDBUser.class, "mysqlUser");
    @DependencySingleton
    @SchemaColumn(order=2, index=IndexType.INDEXED, description="the pkey in mysql_users")
    public MySQLUser getMysqlUser() throws RemoteException {
    	return getConnector().getMysqlUsers().get(mysqlUser);
    }

    @SchemaColumn(order=3, description="the SELECT privilege")
    public boolean getSelectPriv() {
        return selectPriv;
    }

    @SchemaColumn(order=4, description="the INSERT privilege")
    public boolean getInsertPriv() {
        return insertPriv;
    }

    @SchemaColumn(order=5, description="the UPDATE privilege")
    public boolean getUpdatePriv() {
        return updatePriv;
    }

    @SchemaColumn(order=6, description="the DELETE privilege")
    public boolean getDeletePriv() {
        return deletePriv;
    }

    @SchemaColumn(order=7, description="the CREATE privilege")
    public boolean getCreatePriv() {
        return createPriv;
    }

    @SchemaColumn(order=8, description="the DROP privilege")
    public boolean getDropPriv() {
        return dropPriv;
    }

    @SchemaColumn(order=9, description="the GRANT privilege")
    public boolean getGrantPriv() {
        return grantPriv;
    }

    @SchemaColumn(order=10, description="the REFERENCES privilege")
    public boolean getReferencesPriv() {
        return referencesPriv;
    }

    @SchemaColumn(order=11, description="the INDEX privilege")
    public boolean getIndexPriv() {
        return indexPriv;
    }

    @SchemaColumn(order=12, description="the ALTER privilete")
    public boolean getAlterPriv() {
    	return alterPriv;
    }

    @SchemaColumn(order=13, description="the Create_tmp_table_priv")
    public boolean getCreateTmpTablePriv() {
        return createTmpTablePriv;
    }

    @SchemaColumn(order=14, description="the Lock_tables_priv")
    public boolean getLockTablesPriv() {
        return lockTablesPriv;
    }

    @SchemaColumn(order=15, description="the Create_view_priv")
    public boolean getCreateViewPriv() {
        return createViewPriv;
    }

    @SchemaColumn(order=16, description="the Show_view_priv")
    public boolean getShowViewPriv() {
        return showViewPriv;
    }

    @SchemaColumn(order=17, description="the Create_routine_priv")
    public boolean getCreateRoutinePriv() {
        return createRoutinePriv;
    }

    @SchemaColumn(order=18, description="the Alter_routine_priv")
    public boolean getAlterRoutinePriv() {
        return alterRoutinePriv;
    }

    @SchemaColumn(order=19, description="the Execute_priv")
    public boolean getExecutePriv() {
        return executePriv;
    }

    @SchemaColumn(order=20, description="the Event_priv")
    public boolean getEventPriv() {
        return eventPriv;
    }

    @SchemaColumn(order=21, description="the Trigger_priv")
    public boolean getTriggerPriv() {
        return triggerPriv;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public MySQLDBUser(AOServConnector connector, com.aoindustries.aoserv.client.dto.MySQLDBUser dto) {
        this(
            connector,
            dto.getPkey(),
            dto.getMysqlDatabase(),
            dto.getMysqlUser(),
            dto.getSelectPriv(),
            dto.getInsertPriv(),
            dto.getUpdatePriv(),
            dto.getDeletePriv(),
            dto.getCreatePriv(),
            dto.getDropPriv(),
            dto.getGrantPriv(),
            dto.getReferencesPriv(),
            dto.getIndexPriv(),
            dto.getAlterPriv(),
            dto.getCreateTmpTablePriv(),
            dto.getLockTablesPriv(),
            dto.getCreateViewPriv(),
            dto.getShowViewPriv(),
            dto.getCreateRoutinePriv(),
            dto.getAlterRoutinePriv(),
            dto.getExecutePriv(),
            dto.getEventPriv(),
            dto.getTriggerPriv()
        );
    }

    @Override
    public com.aoindustries.aoserv.client.dto.MySQLDBUser getDto() {
        return new com.aoindustries.aoserv.client.dto.MySQLDBUser(getKeyInt(), mysqlDatabase, mysqlUser, selectPriv, insertPriv, updatePriv, deletePriv, createPriv, dropPriv, grantPriv, referencesPriv, indexPriv, alterPriv, createTmpTablePriv, lockTablesPriv, createViewPriv, showViewPriv, createRoutinePriv, alterRoutinePriv, executePriv, eventPriv, triggerPriv);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
    /* TODO
    public List<CannotRemoveReason> getCannotRemoveReasons() throws IOException, SQLException {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();
        reasons.addAll(getMySQLUser().getCannotRemoveReasons());
        reasons.addAll(getMySQLDatabase().getCannotRemoveReasons());
        return reasons;
    }

    public void remove() throws IOException, SQLException {
    	getConnector().requestUpdateIL(
            true,
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.MYSQL_DB_USERS,
            pkey
    	);
    }
     */
    // </editor-fold>
}
