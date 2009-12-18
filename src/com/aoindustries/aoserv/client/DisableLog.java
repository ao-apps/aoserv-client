package com.aoindustries.aoserv.client;

/*
 * Copyright 2002-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * When a resource or resources are disabled, the reason and time is logged.
 *
 * @author  AO Industries, Inc.
 */
final public class DisableLog extends CachedObjectIntegerKey<DisableLog> {

    static final int
        COLUMN_PKEY = 0,
        COLUMN_ACCOUNTING = 2,
        COLUMN_DISABLED_BY = 3
    ;
    static final String COLUMN_TIME_name = "time";
    static final String COLUMN_ACCOUNTING_name = "accounting";
    static final String COLUMN_PKEY_name = "pkey";

    private long time;
    private String accounting;
    private String disabled_by;
    private String disable_reason;
    
    /**
     * Determines if the current <code>AOServConnector</code> can enable
     * things disabled by this <code>DisableLog</code>.
     */
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

    Object getColumnImpl(int i) {
        if(i==COLUMN_PKEY) return Integer.valueOf(pkey);
        if(i==1) return new java.sql.Date(time);
        if(i==COLUMN_ACCOUNTING) return accounting;
        if(i==COLUMN_DISABLED_BY) return disabled_by;
        if(i==4) return disable_reason;
    	throw new IllegalArgumentException("Invalid index: "+i);
    }

    public Business getBusiness() throws SQLException, IOException {
        Business bu=table.connector.getBusinesses().get(accounting);
        if(bu==null) throw new SQLException("Unable to find Business: "+accounting);
        return bu;
    }

    public long getTime() {
        return time;
    }

    public String getDisabledByUsername() {
        return disabled_by;
    }

    public BusinessAdministrator getDisabledBy() throws IOException, SQLException {
        // May be filtered
        return table.connector.getBusinessAdministrators().get(disabled_by);
    }

    public String getDisableReason() {
        return disable_reason;
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.DISABLE_LOG;
    }

    public void init(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
        time=result.getTimestamp(2).getTime();
        accounting=result.getString(3);
        disabled_by=result.getString(4);
        disable_reason=result.getString(5);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        time=in.readLong();
        accounting=in.readUTF().intern();
        disabled_by=in.readUTF().intern();
        disable_reason=in.readNullUTF();
    }

    public List<? extends AOServObject> getDependencies() throws IOException, SQLException {
        return createDependencyList(
            getBusiness(),
            getDisabledBy()
        );
    }

    @SuppressWarnings("unchecked")
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

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeLong(time);
        out.writeUTF(accounting);
        out.writeUTF(disabled_by);
        out.writeNullUTF(disable_reason);
    }

    public List<Resource> getResources() throws IOException, SQLException {
        return table.connector.getResources().getResources(this);
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
}