package com.aoindustries.aoserv.client;

/*
 * Copyright 2002-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * When a resource or resources are disabled, the reason and time is logged.
 *
 * @author  AO Industries, Inc.
 */
final public class DisableLog extends AOServObjectIntegerKey<DisableLog> implements BeanFactory<com.aoindustries.aoserv.client.beans.DisableLog> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private Timestamp time;
    final private AccountingCode accounting;
    final private UserId disabledBy;
    final private String disableReason;

    public DisableLog(
        DisableLogService<?,?> service,
        int pkey,
        Timestamp time,
        AccountingCode accounting,
        UserId disabledBy,
        String disableReason
    ) {
        super(service, pkey);
        this.time = time;
        this.accounting = accounting.intern();
        this.disabledBy = disabledBy.intern();
        this.disableReason = disableReason;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(DisableLog other) throws RemoteException {
        int diff = time.compareTo(other.time);
        if(diff!=0) return diff;
        diff = accounting.equals(other.accounting) ? 0 : getBusiness().compareTo(other.getBusiness());
        if(diff!=0) return diff;
        return AOServObjectUtils.compare(key, other.key);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="pkey", index=IndexType.PRIMARY_KEY, description="a generated primary key")
    public int getPkey() {
        return key;
    }

    @SchemaColumn(order=1, name="time", description="the time the stuff was disabled")
    public Timestamp getTime() {
        return time;
    }

    static final String COLUMN_ACCOUNTING = "accounting";
    @SchemaColumn(order=2, name=COLUMN_ACCOUNTING, index=IndexType.INDEXED, description="the business whos resources are being disabled")
    public Business getBusiness() throws RemoteException {
        return getService().getConnector().getBusinesses().get(accounting);
    }

    /**
     * May be filtered.
     */
    static final String COLUMN_DISABLED_BY = "disabled_by";
    @SchemaColumn(order=3, name=COLUMN_DISABLED_BY, index=IndexType.INDEXED, description="the person who disabled the accounts")
    public BusinessAdministrator getDisabledBy() throws RemoteException {
        try {
            return getService().getConnector().getBusinessAdministrators().get(disabledBy);
        } catch(NoSuchElementException err) {
            // Filtered
            return null;
        }
    }

    @SchemaColumn(order=4, name="disable_reason", description="the optional reason the accounts were disabled")
    public String getDisableReason() {
        return disableReason;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.DisableLog getBean() {
        return new com.aoindustries.aoserv.client.beans.DisableLog(key, time, accounting.getBean(), disabledBy.getBean(), disableReason);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependencies() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getBusiness(),
            getDisabledBy()
        );
    }

    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getBusinesses(),
            getBusinessAdministrators(),
            getGroupNames(),
            getUsernames()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public IndexedSet<Business> getBusinesses() throws RemoteException {
        return getService().getConnector().getBusinesses().filterIndexed(Business.COLUMN_DISABLE_LOG, this);
    }

    public IndexedSet<BusinessAdministrator> getBusinessAdministrators() throws RemoteException {
        return getService().getConnector().getBusinessAdministrators().filterIndexed(BusinessAdministrator.COLUMN_DISABLE_LOG, this);
    }

    public IndexedSet<GroupName> getGroupNames() throws RemoteException {
        return getService().getConnector().getGroupNames().filterIndexed(GroupName.COLUMN_DISABLE_LOG, this);
    }

    public IndexedSet<Username> getUsernames() throws RemoteException {
        return getService().getConnector().getUsernames().filterIndexed(Username.COLUMN_DISABLE_LOG, this);
    }
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
            getHttpdSiteBinds(),
            getPostgresServerUsers(),
            getPostgresUsers(),
            getResources()
        );
    }

    public List<Resource> getResources() throws IOException, SQLException {
        return getService().getConnector().getResources().getIndexedRows(Resource.COLUMN_DISABLE_LOG, pkey);
    }

    public List<LinuxServerAccount> getLinuxServerAccounts() throws IOException, SQLException {
        return getService().getConnector().getLinuxServerAccounts().getIndexedRows(LinuxServerAccount.COLUMN_DISABLE_LOG, pkey);
    }

    public List<Username> getUsernames() throws IOException, SQLException {
        return getService().getConnector().getUsernames().getIndexedRows(Username.COLUMN_DISABLE_LOG, pkey);
    }

    public List<EmailList> getEmailLists() throws IOException, SQLException {
        return getService().getConnector().getEmailLists().getIndexedRows(EmailList.COLUMN_DISABLE_LOG, pkey);
    }

    public List<EmailPipe> getEmailPipes() throws IOException, SQLException {
        return getService().getConnector().getEmailPipes().getIndexedRows(EmailPipe.COLUMN_DISABLE_LOG, pkey);
    }

    public List<EmailSmtpRelay> getEmailSmtpRelays() throws IOException, SQLException {
        return getService().getConnector().getEmailSmtpRelays().getIndexedRows(EmailSmtpRelay.COLUMN_DISABLE_LOG, pkey);
    }

    public List<HttpdSite> getHttpdSites() throws IOException, SQLException {
        return getService().getConnector().getHttpdSites().getIndexedRows(HttpdSite.COLUMN_DISABLE_LOG, pkey);
    }

    public List<HttpdSiteBind> getHttpdSiteBinds() throws IOException, SQLException {
        return getService().getConnector().getHttpdSiteBinds().getIndexedRows(HttpdSiteBind.COLUMN_DISABLE_LOG, pkey);
    }

    public List<HttpdSharedTomcat> getHttpdSharedTomcats() throws IOException, SQLException {
        return getService().getConnector().getHttpdSharedTomcats().getIndexedRows(HttpdSharedTomcat.COLUMN_DISABLE_LOG, pkey);
    }

    public List<PostgresServerUser> getPostgresServerUsers() throws IOException, SQLException {
        return getService().getConnector().getPostgresServerUsers().getIndexedRows(PostgresServerUser.COLUMN_DISABLE_LOG, pkey);
    }

    public List<PostgresUser> getPostgresUsers() throws IOException, SQLException {
        return getService().getConnector().getPostgresUsers().getIndexedRows(PostgresUser.COLUMN_DISABLE_LOG, pkey);
    }
     */
    // </editor-fold>
}
