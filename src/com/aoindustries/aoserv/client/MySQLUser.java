/*
 * Copyright 2000-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.command.AOServCommand;
import com.aoindustries.aoserv.client.command.CheckMySQLUserPasswordCommand;
import com.aoindustries.aoserv.client.command.SetMySQLUserPasswordCommand;
import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.WrappedException;
import java.rmi.RemoteException;
import java.util.List;

/**
 * A <code>MySQLUser</code> stores the details of a MySQL account
 * that are common to all servers.
 *
 * @see  MySQLDBUser
 *
 * @author  AO Industries, Inc.
 */
final public class MySQLUser extends AOServerResource implements Comparable<MySQLUser>, DtoFactory<com.aoindustries.aoserv.client.dto.MySQLUser>, PasswordProtected /* TODO, Removable, Disablable*/ {

    // <editor-fold defaultstate="collapsed" desc="Constants">
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

    /**
     * Convenience constants for the most commonly used host values.
     */
    public static final String
        ANY_HOST="%",
        ANY_LOCAL_HOST=null
    ;

    /**
     * The username of the MySQL super user.
     */
    public static final MySQLUserId ROOT;
    static {
        try {
            ROOT = MySQLUserId.valueOf("root").intern();
        } catch(ValidationException err) {
            throw new AssertionError(err.getMessage());
        }
    }

    /**
     * A password may be set to null, which means that the account will
     * be disabled.
     */
    //public static final String NO_PASSWORD=null;

    public static final String NO_PASSWORD_DB_VALUE="*";
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private static final long serialVersionUID = -3410157014290683392L;

    private MySQLUserId username;
    final private int mysqlServer;
    private InetAddress host;
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
    private String predisablePassword;
    final private int maxQuestions;
    final private int maxUpdates;
    final private int maxConnections;
    final private int maxUserConnections;

    public MySQLUser(
        AOServConnector connector,
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
        super(connector, pkey, resourceType, accounting, created, createdBy, disableLog, lastEnabled, aoServer, businessServer);
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
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        username = intern(username);
        host = intern(host);
        predisablePassword = intern(predisablePassword);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(MySQLUser other) {
        try {
            int diff = username==other.username ? 0 : getUsername().compareTo(other.getUsername()); // OK - interned
            if(diff!=0) return diff;
            return mysqlServer==other.mysqlServer ? 0 : getMysqlServer().compareTo(other.getMysqlServer());
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    public static final MethodColumn COLUMN_USERNAME = getMethodColumn(MySQLUser.class, "username");
    @DependencySingleton
    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+1, index=IndexType.INDEXED, description="the username of the MySQL user")
    public Username getUsername() throws RemoteException {
        return getConnector().getUsernames().get(username.getUserId());
    }
    public MySQLUserId getUserId() {
        return username;
    }

    public static final MethodColumn COLUMN_MYSQL_SERVER = getMethodColumn(MySQLUser.class, "mysqlServer");
    @DependencySingleton
    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+2, index=IndexType.INDEXED, description="the resource ID of the MySQL server")
    public MySQLServer getMysqlServer() throws RemoteException {
    	return getConnector().getMysqlServers().get(mysqlServer);
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+3, description="the host this user is allowed to connect from, if this is not null, all access is restricted to these hosts, otherwise the entries in mysql_db_users and mysql_hosts are used.")
    public InetAddress getHost() {
    	return host;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+4, description="the SELECT privilege to all databases")
    public boolean getSelectPriv() {
        return selectPriv;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+5, description="the INSERT privilege to all databases")
    public boolean getInsertPriv() {
        return insertPriv;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+6, description="the UPDATE privilege to all databases")
    public boolean getUpdatePriv() {
        return updatePriv;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+7, description="the DELETE privilege to all databases")
    public boolean getDeletePriv() {
        return deletePriv;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+8, description="the CREATE privilege to all databases")
    public boolean getCreatePriv() {
        return createPriv;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+9, description="the DROP privilege to all databases")
    public boolean getDropPriv() {
        return dropPriv;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+10, description="the RELOAD privilege to all databases")
    public boolean getReloadPriv() {
        return reloadPriv;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+11, description="the SHUTDOWN privilege to all databases")
    public boolean getShutdownPriv() {
        return shutdownPriv;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+12, description="the PROCESS privilege to all databases")
    public boolean getProcessPriv() {
        return processPriv;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+13, description="the FILE privilege to all databases")
    public boolean getFilePriv() {
        return filePriv;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+14, description="the GRANT privilege to all databases")
    public boolean getGrantPriv() {
        return grantPriv;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+15, description="the REFERENCES privilege to all databases")
    public boolean getReferencePriv() {
        return referencesPriv;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+16, description="the INDEX privilege to all databases")
    public boolean getIndexPriv() {
        return indexPriv;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+17, description="the ALTER privilege to all databases")
    public boolean getAlterPriv() {
        return alterPriv;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+18, description="the SHOW_DB privilege to all databases")
    public boolean getShowDbPriv() {
        return showDbPriv;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+19, description="the SUPER privilege to all databases")
    public boolean getSuperPriv() {
        return superPriv;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+20, description="the CREATE_TMP_TABLE privilege to all databases")
    public boolean getCreateTmpTablePriv() {
        return createTmpTablePriv;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+21, description="the LOCK_TABLES privilege to all databases")
    public boolean getLockTablesPriv() {
        return lockTablesPriv;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+22, description="the EXECUTE privilege to all databases")
    public boolean getExecutePriv() {
        return executePriv;
    }
    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+23, description="the REPL_SLAVE privilege to all databases")
    public boolean getReplSlavePriv() {
        return replSlavePriv;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+24, description="the REPL_CLIENT privilege to all databases")
    public boolean getReplClientPriv() {
        return replClientPriv;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+25, description="the CREATE_VIEW privilege to all databases")
    public boolean getCreateViewPriv() {
        return createViewPriv;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+26, description="the SHOW_VIEW privilege to all databases")
    public boolean getShowViewPriv() {
        return showViewPriv;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+27, description="the CREATE_ROUTINE privilege to all databases")
    public boolean getCreateRoutinePriv() {
        return createRoutinePriv;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+28, description="the ALTER_ROUTINE privilege to all databases")
    public boolean getAlterRoutinePriv() {
        return alterRoutinePriv;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+29, description="the  CREATE_USER privilege to all databases")
    public boolean getCreateUserPriv() {
        return createUserPriv;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+30, description="the EVENT_PRIV privilege to all databases")
    public boolean getEventPriv() {
        return eventPriv;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+31, description="the TRIGGER_PRIV privilege to all databases")
    public boolean getTriggerPriv() {
        return triggerPriv;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+32, description="the password used before the account was disabled")
    public String getPredisablePassword() {
        return predisablePassword;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+33, description="the maximum number of questions to this database server, 0 means unlimited")
    public int getMaxQuestions() {
        return maxQuestions;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+34, description="the maximum number of updates to this database server, 0 means unlimited")
    public int getMaxUpdates() {
        return maxUpdates;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+35, description="the maximum number of connections to this database server, 0 means unlimited")
    public int getMaxConnections() {
        return maxConnections;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+36, description="the maximum number of user connections to this database server, 0 means unlimited")
    public int getMaxUserConnections() {
        return maxUserConnections;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public MySQLUser(AOServConnector connector, com.aoindustries.aoserv.client.dto.MySQLUser dto) throws ValidationException {
        this(
            connector,
            dto.getPkey(),
            dto.getResourceType(),
            getAccountingCode(dto.getAccounting()),
            getTimeMillis(dto.getCreated()),
            getUserId(dto.getCreatedBy()),
            dto.getDisableLog(),
            getTimeMillis(dto.getLastEnabled()),
            dto.getAoServer(),
            dto.getBusinessServer(),
            getMySQLUserId(dto.getUsername()),
            dto.getMysqlServer(),
            getInetAddress(dto.getHost()),
            dto.getSelectPriv(),
            dto.getInsertPriv(),
            dto.getUpdatePriv(),
            dto.getDeletePriv(),
            dto.getCreatePriv(),
            dto.getDropPriv(),
            dto.getReloadPriv(),
            dto.getShutdownPriv(),
            dto.getProcessPriv(),
            dto.getFilePriv(),
            dto.getGrantPriv(),
            dto.getReferencesPriv(),
            dto.getIndexPriv(),
            dto.getAlterPriv(),
            dto.getShowDbPriv(),
            dto.getSuperPriv(),
            dto.getCreateTmpTablePriv(),
            dto.getLockTablesPriv(),
            dto.getExecutePriv(),
            dto.getReplSlavePriv(),
            dto.getReplClientPriv(),
            dto.getCreateViewPriv(),
            dto.getShowViewPriv(),
            dto.getCreateRoutinePriv(),
            dto.getAlterRoutinePriv(),
            dto.getCreateUserPriv(),
            dto.getEventPriv(),
            dto.getTriggerPriv(),
            dto.getPredisablePassword(),
            dto.getMaxQuestions(),
            dto.getMaxUpdates(),
            dto.getMaxConnections(),
            dto.getMaxUserConnections()
        );
    }

    @Override
    public com.aoindustries.aoserv.client.dto.MySQLUser getDto() {
        return new com.aoindustries.aoserv.client.dto.MySQLUser(
            getKeyInt(),
            getResourceTypeName(),
            getDto(getAccounting()),
            created,
            getDto(getCreatedByUsername()),
            disableLog,
            lastEnabled,
            aoServer,
            businessServer,
            getDto(username),
            mysqlServer,
            getDto(host),
            selectPriv,
            insertPriv,
            updatePriv,
            deletePriv,
            createPriv,
            dropPriv,
            reloadPriv,
            shutdownPriv,
            processPriv,
            filePriv,
            grantPriv,
            referencesPriv,
            indexPriv,
            alterPriv,
            showDbPriv,
            superPriv,
            createTmpTablePriv,
            lockTablesPriv,
            executePriv,
            replSlavePriv,
            replClientPriv,
            createViewPriv,
            showViewPriv,
            createRoutinePriv,
            alterRoutinePriv,
            createUserPriv,
            eventPriv,
            triggerPriv,
            predisablePassword,
            maxQuestions,
            maxUpdates,
            maxConnections,
            maxUserConnections
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() throws RemoteException {
        return username+"@"+getMysqlServer().toStringImpl();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    @DependentObjectSet
    public IndexedSet<MySQLDBUser> getMysqlDBUsers() throws RemoteException {
        return getConnector().getMysqlDBUsers().filterIndexed(MySQLDBUser.COLUMN_MYSQL_USER, this);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Password Protected">
    @Override
    public AOServCommand<List<PasswordChecker.Result>> getCheckPasswordCommand(String password) {
        return new CheckMySQLUserPasswordCommand(this, password);
    }

    @Override
    public AOServCommand<Void> getSetPasswordCommand(String plaintext) {
        return new SetMySQLUserPasswordCommand(this, plaintext);
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

    public void disable(DisableLog dl) throws IOException, SQLException {
        getConnector().requestUpdateIL(true, AOServProtocol.CommandID.DISABLE, SchemaTable.TableID.MYSQL_USERS, dl.pkey, pkey);
    }
    
    public void enable() throws IOException, SQLException {
        getConnector().requestUpdateIL(true, AOServProtocol.CommandID.ENABLE, SchemaTable.TableID.MYSQL_USERS, pkey);
    }

    public boolean isDisabled() {
        return disable_log!=-1;
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();
        if(
            username==ROOT // OK - interned
        ) reasons.add(new CannotRemoveReason<MySQLUser>("Not allowed to remove the "+ROOT+" MySQL user", this));
        return reasons;
    }

    public void remove() throws IOException, SQLException {
        getConnector().requestUpdateIL(
            true,
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.MYSQL_USERS,
            pkey
        );
    }

    public int arePasswordsSet() throws IOException, SQLException {
        return getConnector().requestBooleanQuery(true, AOServProtocol.CommandID.IS_MYSQL_USER_PASSWORD_SET, pkey)?PasswordProtected.ALL:PasswordProtected.NONE;
    }
     */
    // </editor-fold>
}