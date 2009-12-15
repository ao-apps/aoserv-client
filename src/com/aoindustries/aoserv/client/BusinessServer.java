package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A <code>BusinessServer</code> grants a <code>Business</code> permission to
 * access resources on a <code>Server</code>.
 *
 * @see  Business
 * @see  Server
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class BusinessServer extends CachedObjectIntegerKey<BusinessServer> implements Removable {

    static final int
        COLUMN_PKEY=0,
        COLUMN_ACCOUNTING=1,
        COLUMN_SERVER=2
    ;
    static final String COLUMN_ACCOUNTING_name = "accounting";
    static final String COLUMN_SERVER_name = "server";

    String accounting;
    int server;
    boolean is_default;
    private boolean
        can_control_apache,
        can_control_cron,
        can_control_mysql,
        can_control_postgresql,
        can_control_xfs,
        can_control_xvfb,
        can_vnc_console
    ;
    
    public boolean canControlApache() {
        return can_control_apache;
    }

    public boolean canControlCron() {
        return can_control_cron;
    }

    public boolean canControlMySQL() {
        return can_control_mysql;
    }
    
    public boolean canControlPostgreSQL() {
        return can_control_postgresql;
    }
    
    public boolean canControlXfs() {
        return can_control_xfs;
    }
    
    public boolean canControlXvfb() {
        return can_control_xvfb;
    }

    public boolean canVncConsole() {
        return can_vnc_console;
    }

    public Business getBusiness() throws IOException, SQLException {
        Business obj=table.connector.getBusinesses().get(accounting);
        if(obj==null) throw new SQLException("Unable to find Business: "+accounting);
        return obj;
    }

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_PKEY: return pkey;
            case COLUMN_ACCOUNTING: return accounting;
            case COLUMN_SERVER: return server;
            case 3: return is_default;
            case 4: return can_control_apache;
            case 5: return can_control_cron;
            case 6: return can_control_mysql;
            case 7: return can_control_postgresql;
            case 8: return can_control_xfs;
            case 9: return can_control_xvfb;
            case 10: return can_vnc_console;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public Server getServer() throws IOException, SQLException {
        Server obj=table.connector.getServers().get(server);
        if(obj==null) throw new SQLException("Unable to find Server: "+server);
        return obj;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.BUSINESS_SERVERS;
    }

    public void init(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
        accounting=result.getString(2);
        server=result.getInt(3);
        is_default=result.getBoolean(4);
        can_control_apache=result.getBoolean(5);
        can_control_cron=result.getBoolean(6);
        can_control_mysql=result.getBoolean(7);
        can_control_postgresql=result.getBoolean(8);
        can_control_xfs=result.getBoolean(9);
        can_control_xvfb=result.getBoolean(10);
        can_vnc_console = result.getBoolean(11);
    }

    public boolean isDefault() {
        return is_default;
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        accounting=in.readUTF().intern();
        server=in.readCompressedInt();
        is_default=in.readBoolean();
        can_control_apache=in.readBoolean();
        can_control_cron=in.readBoolean();
        can_control_mysql=in.readBoolean();
        can_control_postgresql=in.readBoolean();
        can_control_xfs=in.readBoolean();
        can_control_xvfb=in.readBoolean();
        can_vnc_console = in.readBoolean();
    }

    public List<CannotRemoveReason> getCannotRemoveReasons(Locale userLocale) throws SQLException, IOException {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();

        Business bu=getBusiness();

        // Do not remove the default unless it is the only one left
        if(
            is_default
            && bu.getBusinessServers().size()>1
        ) reasons.add(new CannotRemoveReason<Business>("Not allowed to remove access to the default server while access to other servers remains", bu));

        Server se=getServer();
        AOServer ao=se.getAOServer();

        // No children should be able to access the server
        List<Business> bus=table.connector.getBusinesses().getRows();
        for(int c=0;c<bus.size();c++) {
            if(bu.isBusinessOrParentOf(bus.get(c))) {
                Business bu2=bus.get(c);
                if(!bu.equals(bu2) && bu2.getBusinessServer(se)!=null) reasons.add(new CannotRemoveReason<Business>("Child business "+bu2.getAccounting()+" still has access to "+se, bu2));

                // net_binds
                for(NetBind nb : bu2.getNetBinds()) {
                    if(nb.getServer().equals(se)) {
                        String details=nb.getDetails();
                        if(details!=null) reasons.add(new CannotRemoveReason<NetBind>("Used for "+details+" on "+se.toStringImpl(userLocale), nb));
                        else {
                            IPAddress ia=nb.getIPAddress();
                            NetDevice nd=ia.getNetDevice();
                            if(nd!=null) reasons.add(new CannotRemoveReason<NetBind>("Used for port "+nb.getPort().getPort()+"/"+nb.getNetProtocol()+" on "+ia.getIPAddress()+" on "+nd.getNetDeviceID().getName()+" on "+se.toStringImpl(userLocale), nb));
                            else reasons.add(new CannotRemoveReason<NetBind>("Used for port "+nb.getPort().getPort()+"/"+nb.getNetProtocol()+" on "+ia.getIPAddress()+" on "+se.toStringImpl(userLocale), nb));
                        }
                    }
                }

                // ip_addresses
                for(IPAddress ia : bu2.getIPAddresses()) {
                    NetDevice nd=ia.getNetDevice();
                    if(
                        nd!=null
                        && se.equals(nd.getServer())
                    ) reasons.add(new CannotRemoveReason<IPAddress>("Used by IP address "+ia.getIPAddress()+" on "+nd.getNetDeviceID().getName()+" on "+se.toStringImpl(userLocale), ia));
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

                        // mysql_server_users
                        MySQLUser mu=un.getMySQLUser();
                        if(mu!=null) {
                            for(MySQLServer ms : ao.getMySQLServers()) {
                                MySQLServerUser msu=mu.getMySQLServerUser(ms);
                                if(msu!=null) reasons.add(new CannotRemoveReason<MySQLServerUser>("Used by MySQL user "+un.getUsername()+" on "+ms.getName()+" on "+ao.getHostname(), msu));
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
                        if(ms.getAOServer().equals(ao)) reasons.add(new CannotRemoveReason<MySQLDatabase>("Used by MySQL database "+md.getName()+" on "+ms.getName()+" on "+ao.getHostname(), md));
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
    	table.connector.requestUpdateIL(true, AOServProtocol.CommandID.REMOVE, SchemaTable.TableID.BUSINESS_SERVERS, pkey);
    }

    public void setAsDefault() throws IOException, SQLException {
    	table.connector.requestUpdateIL(true, AOServProtocol.CommandID.SET_DEFAULT_BUSINESS_SERVER, pkey);
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeUTF(accounting);
        out.writeCompressedInt(server);
        out.writeBoolean(is_default);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_30)<=0) out.writeBoolean(false); // can_configure_backup
        out.writeBoolean(can_control_apache);
        out.writeBoolean(can_control_cron);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_30)<=0) out.writeBoolean(false); // can_control_interbase
        out.writeBoolean(can_control_mysql);
        out.writeBoolean(can_control_postgresql);
        out.writeBoolean(can_control_xfs);
        out.writeBoolean(can_control_xvfb);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_51)>=0) out.writeBoolean(can_vnc_console);
    }
}