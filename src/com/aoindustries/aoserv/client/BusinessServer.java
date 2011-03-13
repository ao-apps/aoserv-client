/*
 * Copyright 2000-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.WrappedException;
import java.rmi.RemoteException;

/**
 * A <code>BusinessServer</code> grants a <code>Business</code> permission to
 * access resources on a <code>Server</code>.
 *
 * @see  Business
 * @see  Server
 * @see  AOServerResource
 *
 * @author  AO Industries, Inc.
 */
final public class BusinessServer extends AOServObjectIntegerKey implements Comparable<BusinessServer>, DtoFactory<com.aoindustries.aoserv.client.dto.BusinessServer> /*, Removable*/ {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private AccountingCode accounting;
    final private int server;
    final private boolean isDefault;
    final private boolean canVncConsole;

    public BusinessServer(
        AOServConnector connector,
        int pkey,
        AccountingCode accounting,
        int server,
        boolean isDefault,
        boolean canVncConsole
    ) {
        super(connector, pkey);
        this.accounting = accounting;
        this.server = server;
        this.isDefault = isDefault;
        this.canVncConsole = canVncConsole;
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        accounting = intern(accounting);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(BusinessServer other) {
        try {
            int diff = accounting.compareTo(other.accounting);
            if(diff!=0) return diff;
            return server==other.server ? 0 : getServer().compareTo(other.getServer());
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

    /**
     * May be filtered.
     */
    static final String COLUMN_ACCOUNTING = "accounting";
    @DependencySingleton
    @SchemaColumn(order=1, name=COLUMN_ACCOUNTING, index=IndexType.INDEXED, description="the business")
    public Business getBusiness() throws RemoteException {
        return getConnector().getBusinesses().filterUnique(Business.COLUMN_ACCOUNTING, accounting);
    }

    static final String COLUMN_SERVER = "server";
    @DependencySingleton
    @SchemaColumn(order=2, name=COLUMN_SERVER, index=IndexType.INDEXED, description="the server")
    public Server getServer() throws RemoteException {
        return getConnector().getServers().get(server);
    }

    @SchemaColumn(order=3, name="is_default", description="if <code>true</code>, this is the default server.")
    public boolean isDefault() {
        return isDefault;
    }

    @SchemaColumn(order=4, name="can_vnc_console", description="grants VNC console access")
    public boolean getCanVncConsole() {
        return canVncConsole;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public BusinessServer(AOServConnector connector, com.aoindustries.aoserv.client.dto.BusinessServer dto) throws ValidationException {
        this(
            connector,
            dto.getPkey(),
            getAccountingCode(dto.getAccounting()),
            dto.getServer(),
            dto.isIsDefault(),
            dto.isCanVncConsole()
        );
    }
    @Override
    public com.aoindustries.aoserv.client.dto.BusinessServer getDto() {
        return new com.aoindustries.aoserv.client.dto.BusinessServer(key, getDto(accounting), server, isDefault, canVncConsole);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() throws RemoteException {
        Business bu = getBusiness();
    	return (bu==null ? accounting : bu.toString())+"->"+getServer().toStringImpl();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    @DependentObjectSet
    public IndexedSet<AOServerResource> getAoServerResources() throws RemoteException {
        return getConnector().getAoServerResources().filterIndexed(AOServerResource.COLUMN_BUSINESS_SERVER, this);
    }

    @DependentObjectSet
    public IndexedSet<NetBind> getNetBinds() throws RemoteException {
        return getConnector().getNetBinds().filterIndexed(NetBind.COLUMN_BUSINESS_SERVER, this);
    }

    @DependentObjectSet
    public IndexedSet<ServerResource> getServerResources() throws RemoteException {
        return getConnector().getServerResources().filterIndexed(ServerResource.COLUMN_BUSINESS_SERVER, this);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
    /* TODO
    public List<CannotRemoveReason> getCannotRemoveReasons() throws SQLException, IOException {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();

        Business bu=getBusiness();

        // Do not remove the default unless it is the only one left
        if(
            isDefault
            && bu.getBusinessServers().size()>1
        ) reasons.add(new CannotRemoveReason<Business>("Not allowed to remove access to the default server while access to other servers remains", bu));

        Server se=getServer();
        AOServer ao=se.getAOServer();

        // No children should be able to access the server
        List<Business> bus=getConnector().getBusinesses().getRows();
        for(int c=0;c<bus.size();c++) {
            if(bu.isBusinessOrParentOf(bus.get(c))) {
                Business bu2=bus.get(c);
                if(!bu.equals(bu2) && bu2.getBusinessServer(se)!=null) reasons.add(new CannotRemoveReason<Business>("Child business "+bu2.getAccounting()+" still has access to "+se, bu2));

                // net_binds
                for(NetBind nb : getNetBinds()) {
                    String details=nb.getDetails();
                    if(details!=null) reasons.add(new CannotRemoveReason<NetBind>("Used for "+details+" on "+se.toStringImpl(), nb));
                    else {
                        IPAddress ia=nb.getIPAddress();
                        NetDevice nd=ia.getNetDevice();
                        if(nd!=null) reasons.add(new CannotRemoveReason<NetBind>("Used for port "+nb.getPort().getPort()+"/"+nb.getNetProtocol()+" on "+ia.getIPAddress()+" on "+nd.getNetDeviceID().getName()+" on "+se.toStringImpl(), nb));
                        else reasons.add(new CannotRemoveReason<NetBind>("Used for port "+nb.getPort().getPort()+"/"+nb.getNetProtocol()+" on "+ia.getIPAddress()+" on "+se.toStringImpl(), nb));
                    }
                }

                // ip_addresses
                for(IPAddress ia : bu2.getIPAddresses()) {
                    NetDevice nd=ia.getNetDevice();
                    if(
                        nd!=null
                        && se.equals(nd.getServer())
                    ) reasons.add(new CannotRemoveReason<IPAddress>("Used by IP address "+ia.getIPAddress()+" on "+nd.getNetDeviceID().getName()+" on "+se.toStringImpl(), ia));
                }

                if(ao!=null) {
                    // email_pipes
                    for(EmailPipe ep : bu2.getEmailPipes()) {
                        if(ep.getAOServer().equals(ao)) reasons.add(new CannotRemoveReason<EmailPipe>("Used by email pipe '"+ep.getPath()+"' on "+ao.getHostname(), ep));
                    }

                    // httpd_sites
                    for(HttpdSite hs : bu2.getHttpdSites()) {
                        if(hs.getAOServer().equals(ao)) reasons.add(new CannotRemoveReason<HttpdSite>("Used by website "+hs.getInstallDirectory()+" on "+ao.getHostname(), hs));
                    }

                    for(Username un : bu2.getUsernames()) {
                        // linux_server_accounts
                        LinuxAccount la=un.getLinuxAccount();
                        if(la!=null) {
                            LinuxServerAccount lsa=la.getLinuxServerAccount(ao);
                            if(lsa!=null) reasons.add(new CannotRemoveReason<LinuxServerAccount>("Used by Linux account "+un.getUsername()+" on "+ao.getHostname(), lsa));
                        }

                        // mysql_users
                        for(MySQLUser mu : un.getMySQLUsers()) {
                            MySQLServer ms = mu.getMySQLServer();
                            if(ms.getAoServer().equals(ao)) {
                                reasons.add(new CannotRemoveReason<MySQLUser>("Used by MySQL user "+mu.username+" on "+ms.getName()+" on "+ao.getHostname(), mu));
                            }
                        }

                        // postgres_server_users
                        PostgresUser pu=un.getPostgresUser();
                        if(pu!=null) {
                            for(PostgresServer ps : ao.getPostgresServers()) {
                                PostgresServerUser psu=pu.getPostgresServerUser(ps);
                                if(psu!=null) reasons.add(new CannotRemoveReason<PostgresServerUser>("Used by PostgreSQL user "+un.getUsername()+" on "+ps.getName()+" on "+ao.getHostname(), psu));
                            }
                        }
                    }

                    for(LinuxGroup lg : bu2.getLinuxGroups()) {
                        // linux_server_groups
                        LinuxServerGroup lsg=lg.getLinuxServerGroup(ao);
                        if(lsg!=null) reasons.add(new CannotRemoveReason<LinuxServerGroup>("Used by Linux group "+lg.getName()+" on "+ao.getHostname(), lsg));
                    }

                    // mysql_databases
                    for(MySQLDatabase md : bu2.getMysqlDatabases()) {
                        MySQLServer ms=md.getMySQLServer();
                        if(ms.getAoServer().equals(ao)) reasons.add(new CannotRemoveReason<MySQLDatabase>("Used by MySQL database "+md.getName()+" on "+ms.getName()+" on "+ao.getHostname(), md));
                    }

                    // postgres_databases
                    for(PostgresDatabase pd : bu2.getPostgresDatabases()) {
                        PostgresServer ps=pd.getPostgresServer();
                        if(ps.getAOServer().equals(ao)) reasons.add(new CannotRemoveReason<PostgresDatabase>("Used by PostgreSQL database "+pd.getName()+" on "+ps.getName()+" on "+ao.getHostname(), pd));
                    }

                    // email_domains
                    for(EmailDomain ed : bu2.getEmailDomains()) {
                        if(ed.getAOServer().equals(ao)) reasons.add(new CannotRemoveReason<EmailDomain>("Used by email domain "+ed.getDomain()+" on "+ao.getHostname(), ed));
                    }

                    // email_smtp_relays
                    for(EmailSmtpRelay esr : bu2.getEmailSmtpRelays()) {
                        if(esr.getAOServer().equals(ao)) reasons.add(new CannotRemoveReason<EmailSmtpRelay>("Used by email SMTP rule "+esr, esr));
                    }
                }
            }
        }
        return reasons;
    }

    public void remove() throws IOException, SQLException {
    	getConnector().requestUpdateIL(true, AOServProtocol.CommandID.REMOVE, SchemaTable.TableID.BUSINESS_SERVERS, pkey);
    }

    public void setAsDefault() throws IOException, SQLException {
    	getConnector().requestUpdateIL(true, AOServProtocol.CommandID.SET_DEFAULT_BUSINESS_SERVER, pkey);
    }
    */
    // </editor-fold>
}
