/*
 * Copyright 2001-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import com.aoindustries.util.UnionSet;
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

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
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
    @SchemaColumn(order=0, name="pkey", index=IndexType.PRIMARY_KEY, description="a generated primary key")
    public int getPkey() {
        return key;
    }

    static final String COLUMN_MYSQL_DATABASE = "mysql_database";
    @SchemaColumn(order=1, name=COLUMN_MYSQL_DATABASE, index=IndexType.INDEXED, description="the pkey in mysql_databases")
    public MySQLDatabase getMysqlDatabase() throws RemoteException {
    	return getConnector().getMysqlDatabases().get(mysqlDatabase);
    }

    static final String COLUMN_MYSQL_USER = "mysql_user";
    @SchemaColumn(order=2, name=COLUMN_MYSQL_USER, index=IndexType.INDEXED, description="the pkey in mysql_users")
    public MySQLUser getMysqlUser() throws RemoteException {
    	return getConnector().getMysqlUsers().get(mysqlUser);
    }

    @SchemaColumn(order=3, name="select_priv", description="the SELECT privilege")
    public boolean canSelect() {
        return selectPriv;
    }

    @SchemaColumn(order=4, name="insert_priv", description="the INSERT privilege")
    public boolean canInsert() {
        return insertPriv;
    }

    @SchemaColumn(order=5, name="update_priv", description="the UPDATE privilege")
    public boolean canUpdate() {
        return updatePriv;
    }

    @SchemaColumn(order=6, name="delete_priv", description="the DELETE privilege")
    public boolean canDelete() {
        return deletePriv;
    }

    @SchemaColumn(order=7, name="create_priv", description="the CREATE privilege")
    public boolean canCreate() {
        return createPriv;
    }

    @SchemaColumn(order=8, name="drop_priv", description="the DROP privilege")
    public boolean canDrop() {
        return dropPriv;
    }

    @SchemaColumn(order=9, name="grant_priv", description="the GRANT privilege")
    public boolean canGrant() {
        return grantPriv;
    }

    @SchemaColumn(order=10, name="references_priv", description="the REFERENCES privilege")
    public boolean canReference() {
        return referencesPriv;
    }

    @SchemaColumn(order=11, name="index_priv", description="the INDEX privilege")
    public boolean canIndex() {
        return indexPriv;
    }

    @SchemaColumn(order=12, name="alter_priv", description="the ALTER privilete")
    public boolean canAlter() {
    	return alterPriv;
    }

    @SchemaColumn(order=13, name="create_tmp_table_priv", description="the Create_tmp_table_priv")
    public boolean canCreateTempTable() {
        return createTmpTablePriv;
    }

    @SchemaColumn(order=14, name="lock_tables_priv", description="the Lock_tables_priv")
    public boolean canLockTables() {
        return lockTablesPriv;
    }

    @SchemaColumn(order=15, name="create_view_priv", description="the Create_view_priv")
    public boolean canCreateView() {
        return createViewPriv;
    }

    @SchemaColumn(order=16, name="show_view_priv", description="the Show_view_priv")
    public boolean canShowView() {
        return showViewPriv;
    }

    @SchemaColumn(order=17, name="create_routine_priv", description="the Create_routine_priv")
    public boolean canCreateRoutine() {
        return createRoutinePriv;
    }

    @SchemaColumn(order=18, name="alter_routine_priv", description="the Alter_routine_priv")
    public boolean canAlterRoutine() {
        return alterRoutinePriv;
    }

    @SchemaColumn(order=19, name="execute_priv", description="the Execute_priv")
    public boolean canExecute() {
        return executePriv;
    }

    @SchemaColumn(order=20, name="event_priv", description="the Event_priv")
    public boolean canEvent() {
        return eventPriv;
    }

    @SchemaColumn(order=21, name="trigger_priv", description="the Trigger_priv")
    public boolean canTrigger() {
        return triggerPriv;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    @Override
    public com.aoindustries.aoserv.client.dto.MySQLDBUser getDto() {
        return new com.aoindustries.aoserv.client.dto.MySQLDBUser(key, mysqlDatabase, mysqlUser, selectPriv, insertPriv, updatePriv, deletePriv, createPriv, dropPriv, grantPriv, referencesPriv, indexPriv, alterPriv, createTmpTablePriv, lockTablesPriv, createViewPriv, showViewPriv, createRoutinePriv, alterRoutinePriv, executePriv, eventPriv, triggerPriv);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionSet<AOServObject> addDependencies(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = super.addDependencies(unionSet);
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getMysqlDatabase());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getMysqlUser());
        return unionSet;
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
