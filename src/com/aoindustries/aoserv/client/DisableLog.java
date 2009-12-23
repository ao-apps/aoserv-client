package com.aoindustries.aoserv.client;

/*
 * Copyright 2002-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * When a resource or resources are disabled, the reason and time is logged.
 *
 * @author  AO Industries, Inc.
 */
final public class DisableLog extends AOServObjectIntegerKey<DisableLog> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private Timestamp time;
    final private String accounting;
    final private String disabled_by;
    final private String disable_reason;

    public DisableLog(
        DisableLogService<?,?> service,
        int pkey,
        Timestamp time,
        String accounting,
        String disabled_by,
        String disable_reason
    ) {
        super(service, pkey);
        this.time = time;
        this.accounting = accounting;
        this.disabled_by = disabled_by;
        this.disable_reason = disable_reason;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(DisableLog other) {
        int diff = time.compareTo(other.time);
        if(diff!=0) return diff;
        diff = compareIgnoreCaseConsistentWithEquals(accounting, other.accounting);
        if(diff!=0) return diff;
        return compare(key, other.key);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(name="pkey", unique=true, description="a generated primary key")
    public int getPkey() {
        return key;
    }

    @SchemaColumn(name="time", description="the time the stuff was disabled")
    public Timestamp getTime() {
        return time;
    }

    @SchemaColumn(name="accounting", description="the business whos resources are being disabled")
    public Business getBusiness() throws SQLException, IOException {
        Business bu=getService().getConnector().getBusinesses().get(accounting);
        if(bu==null) throw new SQLException("Unable to find Business: "+accounting);
        return bu;
    }

    /**
     * May be filtered.
     */
    @SchemaColumn(name="disabled_by", description="the person who disabled the accounts")
    public BusinessAdministrator getDisabledBy() throws IOException, SQLException {
        return getService().getConnector().getBusinessAdministrators().get(disabled_by);
    }

    @SchemaColumn(name="disable_reason", description="the optional reason the accounts were disabled")
    public String getDisableReason() {
        return disable_reason;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
    /* TODO
    public String getDisabledByUsername() {
        return disabled_by;
    }*/

    /**
     * Determines if the current <code>AOServConnector</code> can enable
     * things disabled by this <code>DisableLog</code>.
     */
    /* TODO
    public boolean canEnable() throws SQLException, IOException {
        BusinessAdministrator disabledBy=getDisabledBy();
        return disabledBy!=null && table
            .connector
            .getThisBusinessAdministrator()
            .getUsername()
            .getBusiness()
            .isBusinessOrParentOf(
                disabledBy
                .getUsername()
                .getBusiness()
            )
        ;
    }

    public List<? extends AOServObject> getDependencies() throws IOException, SQLException {
        return createDependencyList(
            getBusiness(),
            getDisabledBy()
        );
    }

    public List<? extends AOServObject> getDependentObjects() throws IOException, SQLException {
        return createDependencyList(
            getLinuxServerAccounts(),
            getUsernames(),
            getEmailLists(),
            getEmailPipes(),
            getEmailSmtpRelays(),
            getHttpdSites(),
            getHttpdSharedTomcats(),
            getMySQLUsers(),
            getHttpdSiteBinds(),
            getPostgresServerUsers(),
            getPostgresUsers()
        );
    }

    public List<Resource> getResources() throws IOException, SQLException {
        return table.connector.getResources().getIndexedRows(Resource.COLUMN_DISABLE_LOG, pkey);
    }

    public List<LinuxServerAccount> getLinuxServerAccounts() throws IOException, SQLException {
        return table.connector.getLinuxServerAccounts().getIndexedRows(LinuxServerAccount.COLUMN_DISABLE_LOG, pkey);
    }

    public List<Username> getUsernames() throws IOException, SQLException {
        return table.connector.getUsernames().getIndexedRows(Username.COLUMN_DISABLE_LOG, pkey);
    }

    public List<EmailList> getEmailLists() throws IOException, SQLException {
        return table.connector.getEmailLists().getIndexedRows(EmailList.COLUMN_DISABLE_LOG, pkey);
    }

    public List<EmailPipe> getEmailPipes() throws IOException, SQLException {
        return table.connector.getEmailPipes().getIndexedRows(EmailPipe.COLUMN_DISABLE_LOG, pkey);
    }

    public List<EmailSmtpRelay> getEmailSmtpRelays() throws IOException, SQLException {
        return table.connector.getEmailSmtpRelays().getIndexedRows(EmailSmtpRelay.COLUMN_DISABLE_LOG, pkey);
    }

    public List<HttpdSite> getHttpdSites() throws IOException, SQLException {
        return table.connector.getHttpdSites().getIndexedRows(HttpdSite.COLUMN_DISABLE_LOG, pkey);
    }

    public List<HttpdSiteBind> getHttpdSiteBinds() throws IOException, SQLException {
        return table.connector.getHttpdSiteBinds().getIndexedRows(HttpdSiteBind.COLUMN_DISABLE_LOG, pkey);
    }

    public List<HttpdSharedTomcat> getHttpdSharedTomcats() throws IOException, SQLException {
        return table.connector.getHttpdSharedTomcats().getIndexedRows(HttpdSharedTomcat.COLUMN_DISABLE_LOG, pkey);
    }

    public List<MySQLUser> getMySQLUsers() throws IOException, SQLException {
        return table.connector.getMysqlUsers().getIndexedRows(MySQLUser.COLUMN_DISABLE_LOG, pkey);
    }

    public List<PostgresServerUser> getPostgresServerUsers() throws IOException, SQLException {
        return table.connector.getPostgresServerUsers().getIndexedRows(PostgresServerUser.COLUMN_DISABLE_LOG, pkey);
    }

    public List<PostgresUser> getPostgresUsers() throws IOException, SQLException {
        return table.connector.getPostgresUsers().getIndexedRows(PostgresUser.COLUMN_DISABLE_LOG, pkey);
    }
     */
    // </editor-fold>
}
