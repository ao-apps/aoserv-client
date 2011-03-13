/*
 * Copyright 2001-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.command.AOServCommand;
import com.aoindustries.aoserv.client.command.CheckPostgresUserPasswordCommand;
import com.aoindustries.aoserv.client.command.SetPostgresUserPasswordCommand;
import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.UnionClassSet;
import com.aoindustries.util.WrappedException;
import java.rmi.RemoteException;
import java.util.List;

/**
 * A <code>PostgresUser</code> has access to one PostgreSQL server.
 *
 * @author  AO Industries, Inc.
 */
final public class PostgresUser extends AOServerResource implements Comparable<PostgresUser>, DtoFactory<com.aoindustries.aoserv.client.dto.PostgresUser>, PasswordProtected /* TODO: Removable, Disablable*/ {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    /**
     * The username of the PostgreSQL special users.
     */
    public static final PostgresUserId
        POSTGRES,
        AOADMIN,
        AOSERV_APP,
        AOWEB_APP
    ;
    static {
        try {
            POSTGRES = PostgresUserId.valueOf("postgres").intern();
            AOADMIN = PostgresUserId.valueOf("aoadmin").intern();
            AOSERV_APP = PostgresUserId.valueOf("aoserv_app").intern();
            AOWEB_APP = PostgresUserId.valueOf("aoweb_app").intern();
        } catch(ValidationException err) {
            throw new AssertionError(err.getMessage());
        }
    }

    /**
     * A password may be set to null, which means that the account will
     * be disabled.  When this is the case, an empty string is stored in the
     * password field of the database.
     */
    public static final String NO_PASSWORD_DB_VALUE="";
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private PostgresUserId username;
    final private int postgresServer;
    final private boolean createdb;
    final private boolean trace;
    final private boolean superPriv;
    final private boolean catupd;
    private String predisablePassword;

    public PostgresUser(
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
        PostgresUserId username,
        int postgresServer,
        boolean createdb,
        boolean trace,
        boolean superPriv,
        boolean catupd,
        String predisablePassword
    ) {
        super(connector, pkey, resourceType, accounting, created, createdBy, disableLog, lastEnabled, aoServer, businessServer);
        this.username = username;
        this.postgresServer = postgresServer;
        this.createdb = createdb;
        this.trace = trace;
        this.superPriv = superPriv;
        this.catupd = catupd;
        this.predisablePassword = predisablePassword;
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        username = intern(username);
        predisablePassword = intern(predisablePassword);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(PostgresUser other) {
        try {
            int diff = username==other.username ? 0 : getUsername().compareTo(other.getUsername());
            if(diff!=0) return diff;
            return postgresServer==other.postgresServer ? 0 : getPostgresServer().compareTo(other.getPostgresServer());
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    static final String COLUMN_USERNAME = "username";
    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+1, name=COLUMN_USERNAME, index=IndexType.INDEXED, description="the username of the PostgreSQL user")
    public Username getUsername() throws RemoteException {
        return getConnector().getUsernames().get(username.getUserId());
    }
    public PostgresUserId getUserId() {
        return username;
    }

    static final String COLUMN_POSTGRES_SERVER = "postgres_server";
    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+2, name=COLUMN_POSTGRES_SERVER, index=IndexType.INDEXED, description="the pkey of the PostgreSQL server")
    public PostgresServer getPostgresServer() throws RemoteException {
        return getConnector().getPostgresServers().get(postgresServer);
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+3, name="createdb", description="usecreatedb flag")
    public boolean canCreateDB() {
        return createdb;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+4, name="trace", description="usetrace flag")
    public boolean canTrace() {
        return trace;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+5, name="super", description="usesuper flag")
    public boolean isDatabaseAdmin() {
        return superPriv;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+6, name="catupd", description="usecatupd flag")
    public boolean canCatUPD() {
        return catupd;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+7, name="predisable_password", description="the password that was on the account before it was disabled")
    public String getPredisablePassword() {
        return predisablePassword;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public PostgresUser(AOServConnector connector, com.aoindustries.aoserv.client.dto.PostgresUser dto) throws ValidationException {
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
            getPostgresUserId(dto.getUsername()),
            dto.getPostgresServer(),
            dto.isCreatedb(),
            dto.isTrace(),
            dto.isSuperPriv(),
            dto.isCatupd(),
            dto.getPredisablePassword()
        );
    }

    @Override
    public com.aoindustries.aoserv.client.dto.PostgresUser getDto() {
        return new com.aoindustries.aoserv.client.dto.PostgresUser(
            key,
            getResourceTypeName(),
            getDto(getAccounting()),
            created,
            getDto(getCreatedByUsername()),
            disableLog,
            lastEnabled,
            aoServer,
            businessServer,
            getDto(username),
            postgresServer,
            createdb,
            trace,
            superPriv,
            catupd,
            predisablePassword
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionClassSet<AOServObject<?>> addDependencies(UnionClassSet<AOServObject<?>> unionSet) throws RemoteException {
        unionSet = super.addDependencies(unionSet);
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getUsername());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getPostgresServer());
        return unionSet;
    }

    @Override
    protected UnionClassSet<AOServObject<?>> addDependentObjects(UnionClassSet<AOServObject<?>> unionSet) throws RemoteException {
        unionSet = super.addDependentObjects(null);
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getPostgresDatabases());
        return unionSet;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() throws RemoteException {
        return username+"@"+getPostgresServer().toStringImpl();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public IndexedSet<PostgresDatabase> getPostgresDatabases() throws RemoteException {
        return getConnector().getPostgresDatabases().filterIndexed(PostgresDatabase.COLUMN_DATDBA, this);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Password Protected">
    @Override
    public AOServCommand<List<PasswordChecker.Result>> getCheckPasswordCommand(String password) {
        return new CheckPostgresUserPasswordCommand(this, password);
    }

    @Override
    public AOServCommand<Void> getSetPasswordCommand(String plaintext) {
        return new SetPostgresUserPasswordCommand(this, plaintext);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
    /* TODO
    public int arePasswordsSet() throws IOException, SQLException {
        return getConnector().requestBooleanQuery(true, AOServProtocol.CommandID.IS_POSTGRES_SERVER_USER_PASSWORD_SET, pkey)?PasswordProtected.ALL:PasswordProtected.NONE;
    }

    public boolean canDisable() throws IOException, SQLException {
        return disable_log==-1;
    }

    public boolean canEnable() throws SQLException, IOException {
        DisableLog dl=getDisableLog();
        if(dl==null) return false;
        else return dl.canEnable() && getUsername().disable_log==-1;
    }

    public void disable(DisableLog dl) throws IOException, SQLException {
        getConnector().requestUpdateIL(true, AOServProtocol.CommandID.DISABLE, SchemaTable.TableID.POSTGRES_USERS, dl.pkey, pkey);
    }
    
    public void enable() throws IOException, SQLException {
        getConnector().requestUpdateIL(true, AOServProtocol.CommandID.ENABLE, SchemaTable.TableID.POSTGRES_USERS, pkey);
    }

    public boolean isDisabled() {
        return disable_log!=-1;
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() throws SQLException, IOException {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();

        if(
            username==PostgresUser.POSTGRES // OK - interned
        ) reasons.add(new CannotRemoveReason<PostgresServerUser>("Not allowed to remove the "+PostgresUser.POSTGRES+" PostgreSQL user", this));

        for(PostgresDatabase pd : getPostgresDatabases()) {
            PostgresServer ps=pd.getPostgresServer();
            reasons.add(new CannotRemoveReason<PostgresDatabase>("Used by PostgreSQL database "+pd.getName()+" on "+ps.getName()+" on "+ps.getAOServer().getHostname(), pd));
        }

        return reasons;
    }

    public void remove() throws IOException, SQLException {
        getConnector().requestUpdateIL(
            true,
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.POSTGRES_USERS,
            pkey
        );
    }
    */
    // </editor-fold>
}