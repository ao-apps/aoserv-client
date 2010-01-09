package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.validator.PostgresUserId;
import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.Set;

/**
 * A <code>PostgresUser</code> has access to one PostgreSQL server.
 *
 * @author  AO Industries, Inc.
 */
final public class PostgresUser extends AOServObjectIntegerKey<PostgresUser> implements BeanFactory<com.aoindustries.aoserv.client.beans.PostgresUser> /* PasswordProtected, Removable, Disablable*/ {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    /**
     * The username of the PostgreSQL special users.
     */
    public static final String
        POSTGRES="postgres",
        AOADMIN="aoadmin",
        AOSERV_APP="aoserv_app",
        AOWEB_APP="aoweb_app"
    ;

    /**
     * A password may be set to null, which means that the account will
     * be disabled.
     */
    public static final String NO_PASSWORD=null;

    public static final String NO_PASSWORD_DB_VALUE="";
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private PostgresUserId username;
    final private int postgresServer;
    final private boolean createdb;
    final private boolean trace;
    final private boolean superPriv;
    final private boolean catupd;
    final private String predisablePassword;

    public PostgresUser(
        PostgresUserService<?,?> service,
        int aoServerResource,
        PostgresUserId username,
        int postgresServer,
        boolean createdb,
        boolean trace,
        boolean superPriv,
        boolean catupd,
        String predisablePassword
    ) {
        super(service, aoServerResource);
        this.username = username.intern();
        this.postgresServer = postgresServer;
        this.createdb = createdb;
        this.trace = trace;
        this.superPriv = superPriv;
        this.catupd = catupd;
        this.predisablePassword = predisablePassword;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(PostgresUser other) throws RemoteException {
        int diff = username.equals(other.username) ? 0 : getUsername().compareTo(other.getUsername());
        if(diff!=0) return diff;
        return postgresServer==other.postgresServer ? 0 : getPostgresServer().compareTo(other.getPostgresServer());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="ao_server_resource", index=IndexType.PRIMARY_KEY, description="the unique resource id")
    public AOServerResource getAoServerResource() throws RemoteException {
        return getService().getConnector().getAoServerResources().get(key);
    }

    static final String COLUMN_USERNAME = "username";
    @SchemaColumn(order=1, name=COLUMN_USERNAME, index=IndexType.INDEXED, description="the username of the PostgreSQL user")
    public Username getUsername() throws RemoteException {
        return getService().getConnector().getUsernames().get(username.getUserId());
    }

    static final String COLUMN_POSTGRES_SERVER = "postgres_server";
    @SchemaColumn(order=2, name=COLUMN_POSTGRES_SERVER, index=IndexType.INDEXED, description="the pkey of the PostgreSQL server")
    public PostgresServer getPostgresServer() throws RemoteException {
        return getService().getConnector().getPostgresServers().get(postgresServer);
    }

    @SchemaColumn(order=3, name="createdb", description="usecreatedb flag")
    public boolean canCreateDB() {
        return createdb;
    }

    @SchemaColumn(order=4, name="trace", description="usetrace flag")
    public boolean canTrace() {
        return trace;
    }

    @SchemaColumn(order=5, name="super", description="usesuper flag")
    public boolean isDatabaseAdmin() {
        return superPriv;
    }

    @SchemaColumn(order=6, name="catupd", description="usecatupd flag")
    public boolean canCatUPD() {
        return catupd;
    }

    @SchemaColumn(order=7, name="predisable_password", description="the password that was on the account before it was disabled")
    public String getPredisablePassword() {
        return predisablePassword;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.PostgresUser getBean() {
        return new com.aoindustries.aoserv.client.beans.PostgresUser(key, username.getBean(), postgresServer, createdb, trace, superPriv, catupd, predisablePassword);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependencies() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getAoServerResource(),
            getUsername(),
            getPostgresServer()
        );
    }

    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getPostgresDatabases()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl(Locale userLocale) throws RemoteException {
        return ApplicationResources.accessor.getMessage(userLocale, "PostgresUser.toString", username, getPostgresServer().toStringImpl(userLocale));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public Set<PostgresDatabase> getPostgresDatabases() throws RemoteException {
        return getService().getConnector().getPostgresDatabases().getIndexed(PostgresDatabase.COLUMN_DATDBA, this);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
    /* TODO
    public int arePasswordsSet() throws IOException, SQLException {
        return getService().getConnector().requestBooleanQuery(true, AOServProtocol.CommandID.IS_POSTGRES_SERVER_USER_PASSWORD_SET, pkey)?PasswordProtected.ALL:PasswordProtected.NONE;
    }

    public boolean canDisable() throws IOException, SQLException {
        return disable_log==-1;
    }

    public boolean canEnable() throws SQLException, IOException {
        DisableLog dl=getDisableLog();
        if(dl==null) return false;
        else return dl.canEnable() && getUsername().disable_log==-1;
    }

    public PasswordChecker.Result[] checkPassword(Locale userLocale, String password) throws IOException {
        return checkPassword(userLocale, pkey, password);
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
        getService().getConnector().requestUpdateIL(true, AOServProtocol.CommandID.DISABLE, SchemaTable.TableID.POSTGRES_USERS, dl.pkey, pkey);
    }
    
    public void enable() throws IOException, SQLException {
        getService().getConnector().requestUpdateIL(true, AOServProtocol.CommandID.ENABLE, SchemaTable.TableID.POSTGRES_USERS, pkey);
    }

    public boolean isDisabled() {
        return disable_log!=-1;
    }

    public List<CannotRemoveReason> getCannotRemoveReasons(Locale userLocale) throws SQLException, IOException {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();

        if(username.equals(PostgresUser.POSTGRES)) reasons.add(new CannotRemoveReason<PostgresServerUser>("Not allowed to remove the "+PostgresUser.POSTGRES+" PostgreSQL user", this));

        for(PostgresDatabase pd : getPostgresDatabases()) {
            PostgresServer ps=pd.getPostgresServer();
            reasons.add(new CannotRemoveReason<PostgresDatabase>("Used by PostgreSQL database "+pd.getName()+" on "+ps.getName()+" on "+ps.getAOServer().getHostname(), pd));
        }

        return reasons;
    }

    public void remove() throws IOException, SQLException {
        getService().getConnector().requestUpdateIL(
            true,
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.POSTGRES_USERS,
            pkey
        );
    }

    public void setPassword(String password) throws IOException, SQLException {
        AOServConnector connector=getService().getConnector();
        if(!connector.isSecure()) throw new IOException("Passwords for PostgreSQL users may only be set when using secure protocols.  Currently using the "+connector.getProtocol()+" protocol, which is not secure.");

        connector.requestUpdate(
            true,
            new AOServConnector.UpdateRequest() {
                public void writeRequest(CompressedDataOutputStream out) throws IOException {
                    out.writeCompressedInt(AOServProtocol.CommandID.SET_POSTGRES_SERVER_USER_PASSWORD.ordinal());
                    out.writeCompressedInt(pkey);
                    out.writeBoolean(password!=null); if(password!=null) out.writeUTF(password);
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
                    out.writeCompressedInt(AOServProtocol.CommandID.SET_POSTGRES_SERVER_USER_PREDISABLE_PASSWORD.ordinal());
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

    public boolean canSetPassword() {
        return disable_log==-1 && !POSTGRES.equals(pkey);
    }
    */
    // </editor-fold>
}