/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import com.aoindustries.util.StringUtility;
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.Set;

/**
 * A <code>MySQLUser</code> stores the details of a MySQL account
 * that are common to all servers.
 *
 * @see  MySQLDBUser
 *
 * @author  AO Industries, Inc.
 */
final public class MySQLUser extends AOServObjectIntegerKey<MySQLUser> implements BeanFactory<com.aoindustries.aoserv.client.beans.MySQLUser> /* PasswordProtected, Removable, Disablable*/ {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    public static final int
        UNLIMITED_QUESTIONS=0,
        DEFAULT_MAX_QUESTIONS=UNLIMITED_QUESTIONS
    ;

    public static final int
        UNLIMITED_UPDATES=0,
        DEFAULT_MAX_UPDATES=UNLIMITED_UPDATES
    ;

    public static final int
        UNLIMITED_CONNECTIONS=0,
        DEFAULT_MAX_CONNECTIONS=UNLIMITED_CONNECTIONS
    ;

    public static final int
        UNLIMITED_USER_CONNECTIONS=0,
        DEFAULT_MAX_USER_CONNECTIONS=UNLIMITED_USER_CONNECTIONS
    ;

    public static final int MAX_HOST_LENGTH=60;

    /**
     * Convenience constants for the most commonly used host values.
     */
    public static final String
        ANY_HOST="%",
        ANY_LOCAL_HOST=null
    ;

    /**
     * The maximum length of a MySQL username.
     */
    public static final int MAX_USERNAME_LENGTH=16;

    /**
     * The username of the MySQL super user.
     */
    public static final String ROOT="root";

    /**
     * A password may be set to null, which means that the account will
     * be disabled.
     */
    public static final String NO_PASSWORD=null;

    public static final String NO_PASSWORD_DB_VALUE="*";
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private String username;
    final private int mysqlServer;
    final private String host;
    final private boolean selectPriv;
    final private boolean insertPriv;
    final private boolean updatePriv;
    final private boolean deletePriv;
    final private boolean createPriv;
    final private boolean dropPriv;
    final private boolean reloadPriv;
    final private boolean shutdownPriv;
    final private boolean processPriv;
    final private boolean filePriv;
    final private boolean grantPriv;
    final private boolean referencesPriv;
    final private boolean indexPriv;
    final private boolean alterPriv;
    final private boolean showDbPriv;
    final private boolean superPriv;
    final private boolean createTmpTablePriv;
    final private boolean lockTablesPriv;
    final private boolean executePriv;
    final private boolean replSlavePriv;
    final private boolean replClientPriv;
    final private boolean createViewPriv;
    final private boolean showViewPriv;
    final private boolean createRoutinePriv;
    final private boolean alterRoutinePriv;
    final private boolean createUserPriv;
    final private boolean eventPriv;
    final private boolean triggerPriv;
    final private String predisablePassword;
    final private int maxQuestions;
    final private int maxUpdates;
    final private int maxConnections;
    final private int maxUserConnections;

    public MySQLUser(
        MySQLUserService<?,?> service,
        int aoServerResource,
        String username,
        int mysqlServer,
        String host,
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
        super(service, aoServerResource);
        this.username = username.intern();
        this.mysqlServer = mysqlServer;
        this.host = StringUtility.intern(host);
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
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(MySQLUser other) throws RemoteException {
        int diff = username.equals(other.username) ? 0 : getUsername().compareTo(other.getUsername());
        if(diff!=0) return diff;
        return mysqlServer==other.mysqlServer ? 0 : getMysqlServer().compareTo(other.getMysqlServer());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="ao_server_resource", index=IndexType.PRIMARY_KEY, description="the unique resource id")
    public AOServerResource getAoServerResource() throws RemoteException {
        return getService().getConnector().getAoServerResources().get(key);
    }

    static final String COLUMN_USERNAME = "username";
    @SchemaColumn(order=1, name=COLUMN_USERNAME, index=IndexType.INDEXED, description="the username of the MySQL user")
    public Username getUsername() throws RemoteException {
        return getService().getConnector().getUsernames().get(username);
    }

    static final String COLUMN_MYSQL_SERVER = "mysql_server";
    @SchemaColumn(order=2, name=COLUMN_MYSQL_SERVER, index=IndexType.INDEXED, description="the resource ID of the MySQL server")
    public MySQLServer getMysqlServer() throws RemoteException {
    	return getService().getConnector().getMysqlServers().get(mysqlServer);
    }

    @SchemaColumn(order=3, name="host", description="the host this user is allowed to connect from, if this is not null, all access is restricted to these hosts, otherwise the entries in mysql_db_users and mysql_hosts are used.")
    public String getHost() {
    	return host;
    }

    @SchemaColumn(order=4, name="select_priv", description="the SELECT privilege to all databases")
    public boolean canSelect() {
        return selectPriv;
    }

    @SchemaColumn(order=5, name="insert_priv", description="the INSERT privilege to all databases")
    public boolean canInsert() {
        return insertPriv;
    }

    @SchemaColumn(order=6, name="update_priv", description="the UPDATE privilege to all databases")
    public boolean canUpdate() {
        return updatePriv;
    }

    @SchemaColumn(order=7, name="delete_priv", description="the DELETE privilege to all databases")
    public boolean canDelete() {
        return deletePriv;
    }

    @SchemaColumn(order=8, name="create_priv", description="the CREATE privilege to all databases")
    public boolean canCreate() {
        return createPriv;
    }

    @SchemaColumn(order=9, name="drop_priv", description="the DROP privilege to all databases")
    public boolean canDrop() {
        return dropPriv;
    }

    @SchemaColumn(order=10, name="reload_priv", description="the RELOAD privilege to all databases")
    public boolean canReload() {
        return reloadPriv;
    }

    @SchemaColumn(order=11, name="shutdown_priv", description="the SHUTDOWN privilege to all databases")
    public boolean canShutdown() {
        return shutdownPriv;
    }

    @SchemaColumn(order=12, name="process_priv", description="the PROCESS privilege to all databases")
    public boolean canProcess() {
        return processPriv;
    }

    @SchemaColumn(order=13, name="file_priv", description="the FILE privilege to all databases")
    public boolean canFile() {
        return filePriv;
    }

    @SchemaColumn(order=14, name="grant_priv", description="the GRANT privilege to all databases")
    public boolean canGrant() {
        return grantPriv;
    }

    @SchemaColumn(order=15, name="references_priv", description="the REFERENCES privilege to all databases")
    public boolean canReference() {
        return referencesPriv;
    }

    @SchemaColumn(order=16, name="index_priv", description="the INDEX privilege to all databases")
    public boolean canIndex() {
        return indexPriv;
    }

    @SchemaColumn(order=17, name="alter_priv", description="the ALTER privilege to all databases")
    public boolean canAlter() {
        return alterPriv;
    }

    @SchemaColumn(order=18, name="show_db_priv", description="the SHOW_DB privilege to all databases")
    public boolean canShowDB() {
        return showDbPriv;
    }

    @SchemaColumn(order=19, name="super_priv", description="the SUPER privilege to all databases")
    public boolean isSuper() {
        return superPriv;
    }

    @SchemaColumn(order=20, name="create_tmp_table_priv", description="the CREATE_TMP_TABLE privilege to all databases")
    public boolean canCreateTempTable() {
        return createTmpTablePriv;
    }

    @SchemaColumn(order=21, name="lock_tables_priv", description="the LOCK_TABLES privilege to all databases")
    public boolean canLockTables() {
        return lockTablesPriv;
    }

    @SchemaColumn(order=22, name="execute_priv", description="the EXECUTE privilege to all databases")
    public boolean canExecute() {
        return executePriv;
    }
    @SchemaColumn(order=23, name="repl_slave_priv", description="the REPL_SLAVE privilege to all databases")
    public boolean isReplicationSlave() {
        return replSlavePriv;
    }

    @SchemaColumn(order=24, name="repl_client_priv", description="the REPL_CLIENT privilege to all databases")
    public boolean isReplicationClient() {
        return replClientPriv;
    }

    @SchemaColumn(order=25, name="create_view_priv", description="the CREATE_VIEW privilege to all databases")
    public boolean canCreateView() {
        return createViewPriv;
    }

    @SchemaColumn(order=26, name="show_view_priv", description="the SHOW_VIEW privilege to all databases")
    public boolean canShowView() {
        return showViewPriv;
    }

    @SchemaColumn(order=27, name="create_routine_priv", description="the CREATE_ROUTINE privilege to all databases")
    public boolean canCreateRoutine() {
        return createRoutinePriv;
    }

    @SchemaColumn(order=28, name="alter_routine_priv", description="the ALTER_ROUTINE privilege to all databases")
    public boolean canAlterRoutine() {
        return alterRoutinePriv;
    }

    @SchemaColumn(order=29, name="create_user_priv", description="the  CREATE_USER privilege to all databases")
    public boolean canCreateUser() {
        return createUserPriv;
    }

    @SchemaColumn(order=30, name="event_priv", description="the EVENT_PRIV privilege to all databases")
    public boolean canEvent() {
        return eventPriv;
    }

    @SchemaColumn(order=31, name="trigger_priv", description="the TRIGGER_PRIV privilege to all databases")
    public boolean canTrigger() {
        return triggerPriv;
    }

    @SchemaColumn(order=32, name="predisable_password", description="the password used before the account was disabled")
    public String getPredisablePassword() {
        return predisablePassword;
    }

    @SchemaColumn(order=33, name="max_questions", description="the maximum number of questions to this database server, 0 means unlimited")
    public int getMaxQuestions() {
        return maxQuestions;
    }

    @SchemaColumn(order=34, name="max_updates", description="the maximum number of updates to this database server, 0 means unlimited")
    public int getMaxUpdates() {
        return maxUpdates;
    }

    @SchemaColumn(order=35, name="max_connections", description="the maximum number of connections to this database server, 0 means unlimited")
    public int getMaxConnections() {
        return maxConnections;
    }

    @SchemaColumn(order=36, name="max_user_connections", description="the maximum number of user connections to this database server, 0 means unlimited")
    public int getMaxUserConnections() {
        return maxUserConnections;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.MySQLUser getBean() {
        return new com.aoindustries.aoserv.client.beans.MySQLUser(key, username, mysqlServer, host, selectPriv, insertPriv, updatePriv, deletePriv, createPriv, dropPriv, reloadPriv, shutdownPriv, processPriv, filePriv, grantPriv, referencesPriv, indexPriv, alterPriv, showDbPriv, superPriv, createTmpTablePriv, lockTablesPriv, executePriv, replSlavePriv, replClientPriv, createViewPriv, showViewPriv, createRoutinePriv, alterRoutinePriv, createUserPriv, eventPriv, triggerPriv, predisablePassword, maxQuestions, maxUpdates, maxConnections, maxUserConnections);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependencies() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getAoServerResource(),
            getUsername(),
            getMysqlServer()
        );
    }

    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getMysqlDBUsers()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl(Locale userLocale) throws RemoteException {
        return username+" on "+getMysqlServer().toStringImpl(userLocale);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public Set<MySQLDBUser> getMysqlDBUsers() throws RemoteException {
        return getService().getConnector().getMysqlDBUsers().getIndexed(MySQLDBUser.COLUMN_MYSQL_USER, this);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
    /* TODO
    public boolean canDisable() throws IOException, SQLException {
        if(disable_log!=-1) return false;
        return true;
    }

    public boolean canEnable() throws SQLException, IOException {
        DisableLog dl=getDisableLog();
        if(dl==null) return false;
        else return dl.canEnable() && getUsername().disable_log==-1;
    }

    public PasswordChecker.Result[] checkPassword(Locale userLocale, String password) throws IOException {
        return checkPassword(userLocale, username, password);
    }

    public static PasswordChecker.Result[] checkPassword(Locale userLocale, String username, String password) throws IOException {
        return PasswordChecker.checkPassword(userLocale, username, password, true, false);
    }

    public String checkPasswordDescribe(String password) {
        return checkPasswordDescribe(pkey, password);
    }

    public static String checkPasswordDescribe(String username, String password) {
        return PasswordChecker.checkPasswordDescribe(username, password, true, false);
    }

    public void disable(DisableLog dl) throws IOException, SQLException {
        getService().getConnector().requestUpdateIL(true, AOServProtocol.CommandID.DISABLE, SchemaTable.TableID.MYSQL_USERS, dl.pkey, pkey);
    }
    
    public void enable() throws IOException, SQLException {
        getService().getConnector().requestUpdateIL(true, AOServProtocol.CommandID.ENABLE, SchemaTable.TableID.MYSQL_USERS, pkey);
    }

    public boolean isDisabled() {
        return disable_log!=-1;
    }

    public List<CannotRemoveReason> getCannotRemoveReasons(Locale userLocale) {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();
        if(username.equals(ROOT)) reasons.add(new CannotRemoveReason<MySQLUser>("Not allowed to remove the "+ROOT+" MySQL user", this));
        return reasons;
    }

    public void remove() throws IOException, SQLException {
        getService().getConnector().requestUpdateIL(
            true,
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.MYSQL_USERS,
            pkey
        );
    }

    public void setPassword(final String password) throws IOException, SQLException {
        AOServConnector connector=getService().getConnector();
        if(!connector.isSecure()) throw new IOException("Passwords for MySQL users may only be set when using secure protocols.  Currently using the "+connector.getProtocol()+" protocol, which is not secure.");

        connector.requestUpdate(
            true,
            new AOServConnector.UpdateRequest() {
                public void writeRequest(CompressedDataOutputStream out) throws IOException {
                    out.writeCompressedInt(AOServProtocol.CommandID.SET_MYSQL_USER_PASSWORD.ordinal());
                    out.writeCompressedInt(pkey);
                    out.writeNullUTF(password);
                }

                public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
                    int code=in.readByte();
                    if(code!=AOServProtocol.DONE) {
                        AOServProtocol.checkResult(code, in);
                        throw new IOException("Unexpected response code: "+code);
                    }
                }

                public void afterRelease() {
                }
            }
        );
    }

    public void setPredisablePassword(final String password) throws IOException, SQLException {
        getService().getConnector().requestUpdate(
            true,
            new AOServConnector.UpdateRequest() {
                IntList invalidateList;

                public void writeRequest(CompressedDataOutputStream out) throws IOException {
                    out.writeCompressedInt(AOServProtocol.CommandID.SET_MYSQL_USER_PREDISABLE_PASSWORD.ordinal());
                    out.writeCompressedInt(pkey);
                    out.writeNullUTF(password);
                }

                public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
                    int code=in.readByte();
                    if(code==AOServProtocol.DONE) invalidateList=AOServConnector.readInvalidateList(in);
                    else {
                        AOServProtocol.checkResult(code, in);
                        throw new IOException("Unexpected response code: "+code);
                    }
                }

                public void afterRelease() {
                    getService().getConnector().tablesUpdated(invalidateList);
                }
            }
        );
    }
    */

    /**
     * Determines if a name can be used as a username.  A name is valid if
     * it is between 1 and 16 characters in length and uses only [a-z], [0-9], or _
     */
    /* TODO
    public static boolean isValidUsername(String name) {
        int len = name.length();
        if (len == 0 || len > MAX_USERNAME_LENGTH) return false;
        // The first character must be [a-z]
        char ch = name.charAt(0);
        if (ch < 'a' || ch > 'z') return false;
        // The rest may have additional characters
        for (int c = 1; c < len; c++) {
            ch = name.charAt(c);
            if(
                (ch<'a' || ch>'z')
                && (ch<'0' || ch>'9')
                && ch!='_'
            ) return false;
        }
        return true;
    }
    
    public boolean canSetPassword() {
        return disable_log==-1 && !username.equals(ROOT);
    }

    public int arePasswordsSet() throws IOException, SQLException {
        return getService().getConnector().requestBooleanQuery(true, AOServProtocol.CommandID.IS_MYSQL_USER_PASSWORD_SET, pkey)?PasswordProtected.ALL:PasswordProtected.NONE;
    }
     */
    // </editor-fold>
}