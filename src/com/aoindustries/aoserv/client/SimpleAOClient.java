package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.profiler.Profiler;
import com.aoindustries.util.ErrorHandler;
import com.aoindustries.util.SortedArrayList;
import com.aoindustries.util.StandardErrorHandler;
import com.aoindustries.util.WrappedException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

/**
 * <code>SimpleAOClient</code> is a simplified interface into the client
 * code.  Not all information is available, but less knowledge is required
 * to accomplish some common tasks.  All methods are invoked using standard
 * data types.  The underlying implementation changes over time, but
 * this access point does not change as frequently.
 * <p>
 * Most of the <code>AOSH</code> commands resolve to these method calls.
 *
 * @see  AOSH
 * @see  AOServConnector
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class SimpleAOClient {

    final AOServConnector connector;

    /**
     * @deprecated  Please use method with <code>ErrorHandler</code>
     *
     * @see  #SimpleAOClient(ErrorHandler)
     */
    public SimpleAOClient() throws IOException {
        this(new StandardErrorHandler());
    }

    /**
     * Creates a new <code>AOServClient</code> using the default
     * <code>AOServConnector</code>.
     *
     * @exception  IOException  if unable to establish a connection
     *
     * @see  AOServConnector#getConnector(ErrorHandler)
     */
    public SimpleAOClient(ErrorHandler errorHandler) throws IOException {
        this(AOServConnector.getConnector(errorHandler));
    }

    /**
     * Creates a new <code>AOServClient</code> using the provided
     * <code>AOServConnector</code>.
     *
     * @param  connector  the <code>AOServConnector</code> that will be
     *					used for communication with the server.
     *
     * @see  AOServConnector#getConnector()
     * @see  TCPConnector#getTCPConnector
     * @see  SSLConnector#getSSLConnector
     */
    public SimpleAOClient(AOServConnector connector) {
        this.connector=connector;
    }

    /**
     * @deprecated  Please use method with <code>ErrorHandler</code>
     *
     * @see  #SimpleAOClient(String,String,ErrorHandler)
     */
    public SimpleAOClient(String username, String password) throws IOException {
        this(username, password, new StandardErrorHandler());
    }

    /**
     * Creates a new <code>AOServClient</code> using the provided
     * username and password.
     *
     * @param  username  the username to connect with
     * @param  password  the password to connect with
     *
     * @exception  IOException  if unable to establish a connection
     *
     * @see  AOServConnector#getConnector(String,String,ErrorHandler)
     */
    public SimpleAOClient(String username, String password, ErrorHandler errorHandler) throws IOException {
        this(AOServConnector.getConnector(username, password, errorHandler));
    }

    private Architecture getArchitecture(String architecture) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "getArchitecture(String)", null);
        try {
            Architecture ar=connector.architectures.get(architecture);
            if(ar==null) throw new IllegalArgumentException("Unable to find Architecture: "+architecture);
            return ar;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    private AOServer getAOServer(String hostname) throws IllegalArgumentException {
        AOServer ao=connector.aoServers.get(hostname);
        if(ao==null) throw new IllegalArgumentException("Server is not an AOServer: "+hostname);
        return ao;
    }

    private Business getBusiness(String accounting) throws IllegalArgumentException {
        Business bu=connector.businesses.get(accounting);
        if(bu==null) throw new IllegalArgumentException("Unable to find Business: "+accounting);
        return bu;
    }

    private DNSZone getDNSZone(String zone) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "getDNSZone(String)", null);
        try {
            DNSZone dz=connector.dnsZones.get(zone);
            if(dz==null) throw new IllegalArgumentException("Unable to find DNSZone: "+zone);
            return dz;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    private EmailAddress getEmailAddress(String aoServer, String domain, String address) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "getEmailAddress(String,String,String)", null);
        try {
            EmailAddress ea=getEmailDomain(aoServer, domain).getEmailAddress(address);
            if(ea==null) throw new IllegalArgumentException("Unable to find EmailAddress: "+address+'@'+domain+" on "+aoServer);
            return ea;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    private EmailDomain getEmailDomain(String aoServer, String domain) throws IllegalArgumentException {
        EmailDomain ed=getAOServer(aoServer).getEmailDomain(domain);
        if(ed==null) throw new IllegalArgumentException("Unable to find EmailDomain: "+domain+" on "+aoServer);
        return ed;
    }

    private EmailList getEmailList(String aoServer, String path) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "getEmailList(String,String)", null);
        try {
            EmailList el=getAOServer(aoServer).getEmailList(path);
            if(el==null) throw new IllegalArgumentException("Unable to find EmailList: "+path+" on "+aoServer);
            return el;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    private EmailSpamAssassinIntegrationMode getEmailSpamAssassinIntegrationMode(String mode) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "getEmailSpamAssassinIntegrationMode(String)", null);
        try {
            EmailSpamAssassinIntegrationMode esaim=connector.emailSpamAssassinIntegrationModes.get(mode);
            if(esaim==null) throw new IllegalArgumentException("Unable to find EmailSpamAssassinIntegrationMode: "+mode);
            return esaim;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    private HttpdSharedTomcat getHttpdSharedTomcat(String aoServer, String name) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "getHttpdSharedTomcat(String,String)", null);
        try {
            HttpdSharedTomcat hst=getAOServer(aoServer).getHttpdSharedTomcat(name);
            if(hst==null) throw new IllegalArgumentException("Unable to find HttpdSharedTomcat: "+name+" on "+aoServer);
            return hst;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    private HttpdSite getHttpdSite(String aoServer, String siteName) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "getHttpdSite(String,String)", null);
        try {
            HttpdSite hs=getAOServer(aoServer).getHttpdSite(siteName);
            if(hs==null) throw new IllegalArgumentException("Unable to find HttpdSite: "+siteName+" on "+aoServer);
            return hs;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
            
    private IPAddress getIPAddress(String aoServer, String netDevice, String ipAddress) throws IllegalArgumentException {
        IPAddress ia=getNetDevice(aoServer, netDevice).getIPAddress(ipAddress);
        if(ia==null) throw new IllegalArgumentException("Unable to find IPAddress: "+ipAddress+" on "+netDevice+" on "+aoServer);
        return ia;
    }

    private LinuxAccount getLinuxAccount(String username) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "getLinuxAccount(String)", null);
        try {
            LinuxAccount la=getUsername(username).getLinuxAccount();
            if(la==null) throw new IllegalArgumentException("Unable to find LinuxAccount: "+username);
            return la;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    private LinuxGroup getLinuxGroup(String name) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "getLinuxGroup(String)", null);
        try {
            LinuxGroup lg=connector.linuxGroups.get(name);
            if(lg==null) throw new IllegalArgumentException("Unable to find LinuxGroup: "+name);
            return lg;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    private LinuxServerAccount getLinuxServerAccount(String aoServer, String username) throws IllegalArgumentException {
        LinuxServerAccount lsa=getAOServer(aoServer).getLinuxServerAccount(username);
        if(lsa==null) throw new IllegalArgumentException("Unable to find LinuxServerAccount: "+username+" on "+aoServer);
        return lsa;
    }

    private LinuxServerGroup getLinuxServerGroup(String server, String name) throws IllegalArgumentException {
        LinuxServerGroup lsg=getAOServer(server).getLinuxServerGroup(name);
        if(lsg==null) throw new IllegalArgumentException("Unable to find LinuxServerGroup: "+name+" on "+server);
        return lsg;
    }

    private MySQLServer getMySQLServer(String aoServer, String name) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "getMySQLServer(String,String)", null);
        try {
            MySQLServer ms=getAOServer(aoServer).getMySQLServer(name);
            if(ms==null) throw new IllegalArgumentException("Unable to find MySQLServer: "+name+" on "+aoServer);
            return ms;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    private MySQLDatabase getMySQLDatabase(String aoServer, String mysqlServer, String name) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "getMySQLDatabase(String,String,String)", null);
        try {
            MySQLServer ms=getMySQLServer(aoServer, mysqlServer);
            MySQLDatabase md=ms.getMySQLDatabase(name);
            if(md==null) throw new IllegalArgumentException("Unable to find MySQLDatabase: "+name+" on "+mysqlServer+" on "+aoServer);
            return md;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    private MySQLServerUser getMySQLServerUser(String aoServer, String mysqlServer, String username) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "getMySQLServerUser(String,String,String)", null);
        try {
            MySQLServerUser msu=getMySQLServer(aoServer, mysqlServer).getMySQLServerUser(username);
            if(msu==null) throw new IllegalArgumentException("Unable to find MySQLServerUser: "+username+" on "+aoServer);
            return msu;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    private MySQLUser getMySQLUser(String username) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "getMySQLUser(String)", null);
        try {
            MySQLUser mu=getUsername(username).getMySQLUser();
            if(mu==null) throw new IllegalArgumentException("Unable to find MySQLUser: "+username);
            return mu;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    private NetBind getNetBind(int pkey) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "getNetBind(int)", null);
        try {
            NetBind nb=connector.netBinds.get(pkey);
            if(nb==null) throw new IllegalArgumentException("Unable to find NetBind: "+pkey);
            return nb;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    private NetDevice getNetDevice(String aoServer, String netDevice) throws IllegalArgumentException {
        NetDevice nd=getAOServer(aoServer).getNetDevice(netDevice);
        if(nd==null) throw new IllegalArgumentException("Unable to find NetDevice: "+netDevice+" on "+aoServer);
        return nd;
    }

    private OperatingSystem getOperatingSystem(String name) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "getOperatingSystem(String)", null);
        try {
            OperatingSystem os=connector.operatingSystems.get(name);
            if(os==null) throw new IllegalArgumentException("Unable to find OperatingSystem: "+name);
            return os;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    private OperatingSystemVersion getOperatingSystemVersion(String name, String version, Architecture architecture) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "getOperatingSystemVersion(String,String,Architecture)", null);
        try {
            OperatingSystemVersion ov=getOperatingSystem(name).getOperatingSystemVersion(connector, version, architecture);
            if(ov==null) throw new IllegalArgumentException("Unable to find OperatingSystemVersion: "+name+" version "+version+" for architecture of "+architecture);
            return ov;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    private PackageDefinition getPackageDefinition(int packageDefinition) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "getPackageDefinition(int)", null);
        try {
            PackageDefinition pd=connector.packageDefinitions.get(packageDefinition);
            if(pd==null) throw new IllegalArgumentException("Unable to find PackageDefinition: "+packageDefinition);
            return pd;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    private Package getPackage(String name) throws IllegalArgumentException {
        Package pk=connector.packages.get(name);
        if(pk==null) throw new IllegalArgumentException("Unable to find Package: "+name);
        return pk;
    }

    private PostgresDatabase getPostgresDatabase(String aoServer, String postgres_server, String name) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "getPostgresDatabase(String,String,String)", null);
        try {
            PostgresServer ps=getPostgresServer(aoServer, postgres_server);
            PostgresDatabase pd=ps.getPostgresDatabase(name);
            if(pd==null) throw new IllegalArgumentException("Unable to find PostgresDatabase: "+name+" on "+postgres_server+" on "+aoServer);
            return pd;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
            
    private PostgresServer getPostgresServer(String aoServer, String name) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "getPostgresServer(String,String)", null);
        try {
            PostgresServer ps=getAOServer(aoServer).getPostgresServer(name);
            if(ps==null) throw new IllegalArgumentException("Unable to find PostgresServer: "+name+" on "+aoServer);
            return ps;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    private PostgresServerUser getPostgresServerUser(String aoServer, String postgres_server, String username) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "getPostgresServerUser(String,String,String)", null);
        try {
            PostgresServerUser psu=getPostgresServer(aoServer, postgres_server).getPostgresServerUser(username);
            if(psu==null) throw new IllegalArgumentException("Unable to find PostgresServerUser: "+username+" on "+postgres_server+" on "+aoServer);
            return psu;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    private PostgresUser getPostgresUser(String username) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "getPostgresUser(String)", null);
        try {
            PostgresUser pu=getUsername(username).getPostgresUser();
            if(pu==null) throw new IllegalArgumentException("Unable to find PostgresUser: "+username);
            return pu;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    private Server getServer(String server) throws IllegalArgumentException {
        Server se=connector.servers.get(server);
        if(se==null) throw new IllegalArgumentException("Unable to find Server: "+server);
        return se;
    }

    private ServerFarm getServerFarm(String name) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "getServerFarm(String)", null);
        try {
            ServerFarm sf=connector.serverFarms.get(name);
            if(sf==null) throw new IllegalArgumentException("Unable to find ServerFarm: "+name);
            return sf;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    private Username getUsername(String username) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "getUsername(String)", null);
        try {
            Username un=connector.usernames.get(username);
            if(un==null) throw new IllegalArgumentException("Unable to find Username: "+username);
            return un;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Adds a new backup <code>Server</code>.
     *
     * @param  hostname  the desired hostname for the server
     * @param  farm  the farm the server is part of
     * @param  owner  the package the server belongs to
     * @param  description  a description of the server
     * @param  backup_hour  the hour the backup will be run if used in daemon mode,
     *                      expressed in server-local time
     * @param  os_type  the type of operating system on the server
     * @param  os_version  the version of operating system on the server
     * @param  architecture  the type of CPU(s) on the server
     * @param  username  the desired backup account username
     * @param  password  the desired backup account password
     * @param  contact_phone  the phone number to call for anything related to this server
     * @param  contact_email  the email address to contact for anything related to this server
     *
     * @exception  IOException  if unable to communicate with the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>ServerFarm</code>, <code>Business</code>, <code>Architecture</code>,
     *                                       <code>OperatingSystem</code>, or <code>OperatingSystemVersion<code>
     *
     * @see  Server
     * @see  ServerTable#addBackupServer
     */
    public int addBackupServer(
        String hostname,
        String farm,
        String owner,
        String description,
        int backup_hour,
        String os_type,
        String os_version,
        String architecture,
        String username,
        String password,
        String contact_phone,
        String contact_email
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "addBackupServer(String,String,String,String,int,String,String,String,String,String,String,String)", null);
        try {
            return connector.servers.addBackupServer(
                hostname,
                getServerFarm(farm),
                getPackage(owner),
                description,
                backup_hour,
                getOperatingSystemVersion(os_type, os_version, getArchitecture(architecture)),
                username,
                password,
                contact_phone,
                contact_email
            );
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Adds a new <code>Business</code> to the system.
     *
     * @param  accounting  the accounting code of the new business
     * @param  contractVersion  the version number of the digitally signed contract
     * @param  defaultServer  the hostname of the default server
     * @param  parent  the parent business of the new business
     * @param  can_add_backup_servers  allows backup servers to be added to the system
     * @param  can_add_businesses  if <code>true</code>, the new business
     *					is allowed to add additional businesses
     * @param  billParent  if <code>true</code> the parent account will be billed instead
     *                                  of this account
     *
     * @exception  IOException  if unable to communicate with the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the server or parent business
     *
     * @see  Business
     * @see  #checkAccounting
     * @see  Server#addBusiness
     */
    public void addBusiness(
        String accounting,
        String contractVersion,
        String defaultServer,
        String parent,
        boolean can_add_backup_servers,
        boolean can_add_businesses,
        boolean can_see_prices,
        boolean billParent
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "addBusiness(String,String,String,boolean,boolean,boolean,boolean)", null);
        try {
            checkAccounting(accounting);
            if(contractVersion!=null && contractVersion.length()==0) contractVersion=null;
            getServer(defaultServer).addBusiness(
                accounting,
                contractVersion,
                getBusiness(parent),
                can_add_backup_servers,
                can_add_businesses,
                can_see_prices,
                billParent
            );
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Adds a new <code>BusinessAdministrator</code> to a
     * <code>Business</code>.
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     * @exception  IllegalArgumentException  if unable to find the <code>Username</code>
     *
     * @see  BusinessAdministrator
     * @see  Business
     * @see  Username#addBusinessAdministrator
     */
    public void addBusinessAdministrator(
        String username,
        String name,
        String title,
        long birthday,
        boolean isPrivate,
        String workPhone,
        String homePhone,
        String cellPhone,
        String fax,
        String email,
        String address1,
        String address2,
        String city,
        String state,
        String country,
        String zip
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "addBusinessAdministrator(String,String,String,long,boolean,String,String,String,String,String,String,String,String,String,String,String)", null);
        try {
            Username usernameObj=getUsername(username);
            checkBusinessAdministratorUsername(username);
            usernameObj.addBusinessAdministrator(
                name,
                title,
                birthday,
                isPrivate,
                workPhone,
                homePhone,
                cellPhone,
                fax,
                email,
                address1,
                address2,
                city,
                state,
                country,
                zip
            );
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Adds a new <code>BusinessProfile</code> to a <code>Business</code>.  The
     * profile is a complete set of contact information about a business.  New
     * profiles can be added, and they are used as the contact information, but
     * old profiles are still available.
     *
     * @exception  IllegalArgumentException  if unable to find the <code>Business</code>
     *
     * @see  BusinessProfile
     * @see  Business
     * @see  Business#addBusinessProfile
     */
    public int addBusinessProfile(
        String business,
        String name,
        boolean isPrivate,
        String phone,
        String fax,
        String address1,
        String address2,
        String city,
        String state,
        String country,
        String zip,
        boolean sendInvoice,
        String billingContact,
        String billingEmail,
        String technicalContact,
        String technicalEmail
    ) throws IllegalArgumentException, IOException, SQLException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "addBusinessProfile(String,String,boolean,String,String,String,String,String,String,String,String,boolean,String,String,String,String)", null);
        try {
            return getBusiness(business).addBusinessProfile(
                name,
                isPrivate,
                phone,
                fax,
                address1,
                address2,
                city,
                state,
                country,
                zip,
                sendInvoice,
                billingContact,
                billingEmail,
                technicalContact,
                technicalEmail
            );
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Grants a <code>Business</code> access to a <code>Server</code>.
     *
     * @param  accounting  the accounting code of the business
     * @param  server  the hostname of the server
     *
     * @return  the pkey of the new <code>BusinessServer</code>
     *
     * @exception  IOException  if unable to communicate with the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the business or server
     *
     * @see  BusinessServer
     * @see  #checkAccounting
     * @see  Business#addBusinessServer
     */
    public int addBusinessServer(
        String accounting,
        String server
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "addBusinessServer(String,String,boolean)", null);
        try {
            return getBusiness(accounting).addBusinessServer(getServer(server));
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Adds a new <code>CvsRepository</code> to a <code>Server</code>.
     *
     * @param  aoServer  the hostname of the server
     * @param  path    the full path of the repository
     * @param  username  the name of the shell account that owns the directory
     * @param  group     the group that owns the directory
     * @param  mode      the permissions of the directory
     *
     * @return  the <code>pkey</code> of the new <code>CvsRepository</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code>, <code>LinuxServerAccount</code>,
     *					or <code>LinuxServerGroup</code>
     *
     * @see  AOServer#addCvsRepository
     */
    public int addCvsRepository(
        String aoServer,
        String path,
        String username,
        String group,
        long mode
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "addCvsRepository(String,String,String,String,long)", null);
        try {
            AOServer ao=getAOServer(aoServer);
            return ao.addCvsRepository(
                path,
                getLinuxServerAccount(aoServer, username),
                getLinuxServerGroup(aoServer, group),
                mode
            );
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Adds a new <code>DNSRecord</code> to a <code>DNSZone</code>.  Each <code>DNSZone</code>
     * can have multiple DNS records in it, each being a <code>DNSRecord</code>.
     *
     * @param  zone  the zone, in the <code>name.<i>topleveldomain</i>.</code> format.  Please note the
     *					trailing period (<code>.</code>)
     * @param  domain  the part of the name before the zone or <code>@</code> for the zone itself.  For example,
     *					the domain for the hostname of <code>www.aoindustries.com.</code> in the
     *					<code>aoindustries.com.</code> zone is <code>www</code>.
     * @param  type  the <code>DNSType</code>
     * @param  mx_priority  if a <code>MX</code> type, then the value is the priority of the MX record, otherwise
     *					it is <code>DNSRecord.NO_MX_PRIORITY</code>.
     *
     * @return  the <code>pkey</code> of the new <code>DNSRecord</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if the mx_priority is provided for a non-<code>MX</code> record,
     *					the mx_priority is not provided for a <code>MX</code> record,
     *					the destination is not the correct format for the <code>DNSType</code>,
     *					or  unable to find the <code>DNSZone</code> or <code>DNSType</code>
     *
     * @see  DNSZone#addDNSRecord
     * @see  DNSRecord
     * @see  #addDNSZone
     * @see  DNSType#checkDestination
     */
    public int addDNSRecord(
        String zone,
        String domain,
        String type,
        int mx_priority,
        String destination,
        int ttl
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "addDNSRecord(String,String,String,int,String,int)", null);
        try {
            DNSZone nz=getDNSZone(zone);

            // Must be a valid type
            DNSType nt=connector.dnsTypes.get(type);
            if(nt==null) throw new IllegalArgumentException("Unable to find DNSType: "+type);

            // Must have appropriate MX priority
            if(nt.isMX()) {
                if(mx_priority==DNSRecord.NO_MX_PRIORITY) throw new IllegalArgumentException("mx_priority required for type="+type);
                else if(mx_priority<=0) throw new IllegalArgumentException("Invalid mx_priority: "+mx_priority);
            } else {
                if(mx_priority!=DNSRecord.NO_MX_PRIORITY) throw new IllegalArgumentException("No mx_priority allowed for type="+type);
            }

            // Must have a valid destination type
            nt.checkDestination(destination);

            return nz.addDNSRecord(
                domain,
                nt,
                mx_priority,
                destination,
                ttl
            );
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Adds a new <code>DNSZone</code> to a system.  A <code>DNSZone</code> is one unique domain in
     * the name servers.  It is always one host up from a top level domain.  In <code><i>mydomain</i>.com.</code>
     * <code>com</code> is the top level domain, which are defined by <code>DNSTLD</code>s.
     *
     * @param  packageName  the name of the <code>Package</code> that owns this domain
     * @param  zone  the complete domain of the new <code>DNSZone</code>
     * @param  ip  the IP address that will be used for the default <code>DNSRecord</code>s
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Package</code> or either parameter is not in
     *                                  the proper format.
     *
     * @see  Package#addDNSZone
     * @see  DNSZone
     * @see  #addDNSRecord
     * @see  IPAddress
     * @see  DNSTLD
     */
    public void addDNSZone(
        String packageName,
        String zone,
        String ip,
        int ttl
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "addDNSZone(String,String,String,int)", null);
        try {
            if(!connector.dnsZones.checkDNSZone(zone)) throw new IllegalArgumentException("Invalid zone: "+zone);
            checkIPAddress(ip);
            getPackage(packageName).addDNSZone(zone, ip, ttl);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Forwards email addressed to an address at a <code>EmailDomain</code> to
     * a different email address.  The destination email address may be any email
     * address, not just those in a <code>EmailDomain</code>.
     *
     * @param  address  the part of the email address before the <code>@</code>
     * @param  domain  the part of the email address after the <code>@</code>
     * @param  aoServer  the hostname of the server hosting the domain
     * @param  destination  the completed email address of the final delivery address
     *
     * @exception  IOException  if unable to communicate with the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable find the <code>EmailDomain</code>
     *
     * @see  #checkEmailForwarding
     * @see  EmailAddress#addEmailForwarding
     * @see  EmailDomain
     */
    public int addEmailForwarding(
        String address,
        String domain,
        String aoServer,
        String destination
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "addEmailForwarding(String,String,String,String)", null);
        try {
            EmailDomain sd=getEmailDomain(aoServer, domain);
            EmailAddress eaddress=sd.getEmailAddress(address);
            boolean added=false;
            if(eaddress==null) {
                eaddress=connector.emailAddresses.get(sd.addEmailAddress(address));
                added=true;
            }
            try {
                return eaddress.addEmailForwarding(destination);
            } catch(RuntimeException err) {
                if(added && !eaddress.isUsed()) eaddress.remove();
                throw err;
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Adds a new <code>EmailList</code> to the system.  When an email is sent
     * to an <code>EmailList</code>, it is immediately forwarded to all addresses
     * contained in the list.  The list may accept mail on any number of addresses
     * and forward to any number of recipients.
     * <p>
     * Even though the <code>EmailList</code> may receive email on any number of
     * addresses, each address must be part of a <code>EmailDomain</code> that
     * is hosted on the same <code>Server</code> as the <code>EmailList</code>.
     * If email in a domain on another <code>Server</code> is required to be sent
     * to this list, it must be forwarded from the other <code>Server</code> via
     * a <code>EmailForwarding</code>.
     * <p>
     * The list of destinations for the <code>EmailList</code> is stored on the
     * <code>Server</code> in a flat file of one address per line.  This file
     * may be either manipulated through the API or used directly on the
     * filesystem.
     *
     * @param  aoServer  the hostname of the server the list is hosted on
     * @param  path  the name of the file that stores the list
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data
     *					integrity violation occurs
     * @exception  IllegalArgumentException  if unable to find find the <code>AOServer</code>,
     *					<code>LinuxServerAccount</code>, or <code>LinuxServerGroup</code>
     *
     * @see  #checkEmailListPath
     * @see  #addEmailListAddress
     * @see  EmailListAddress
     * @see  EmailDomain
     * @see  EmailForwarding
     * @see  Server
     * @see  LinuxServerAccount
     * @see  LinuxServerGroup
     */
    public int addEmailList(
        String aoServer,
        String path,
        String username,
        String group
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "addEmailList(String,String,String,String)", null);
        try {
            return connector.emailLists.addEmailList(
                path,
                getLinuxServerAccount(aoServer, username),
                getLinuxServerGroup(aoServer, group)
            );
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Adds to the list of <code>EmailAddresses</code> to which the <code>EmailList</code>
     * will accept mail.
     *
     * @param  address  the part of the email address before the <code>@</code>
     * @param  domain  the part of the email address after the <code>@</code>
     * @param  path  the path of the list
     * @param  aoServer  the hostname of the server
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data
     *					integrity violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>EmailDomain</code>
     *					or <code>EmailList</code>
     *
     * @see  #addEmailList
     * @see  EmailList
     * @see  EmailAddress
     * @see  EmailDomain
     */
    public int addEmailListAddress(
        String address,
        String domain,
        String path,
        String aoServer
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "addEmailListAddress(String,String,String,String)", null);
        try {
            EmailDomain sd=getEmailDomain(aoServer, domain);
            EmailList el=getEmailList(aoServer, path);
            EmailAddress ea=sd.getEmailAddress(address);
            boolean added=false;
            if(ea==null) {
                ea=connector.emailAddresses.get(connector.emailAddresses.addEmailAddress(address, sd));
                added=true;
            }
            try {
                return el.addEmailAddress(ea);
            } catch(RuntimeException err) {
                if(added && !ea.isUsed()) ea.remove();
                throw err;
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Adds a new <code>EmailPipe</code> to the system.  When an email is sent
     * to an <code>EmailPipe</code>, a process is invoked with the email pipes into
     * the process' standard input.
     *
     * @param  aoServer  the hostname of the server that the process exists on
     * @param  path  the name of the executable to launch
     * @param  packageName  the package that this <code>EmailPipe</code> belongs to
     *
     * @return  the pkey of the new pipe
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data
     *					integrity violation occurs
     * @exception  IllegalArgumentException  if unable to find find the <code>Server</code> or
     *					<code>Package</code>
     *
     * @see  #addEmailPipeAddress
     * @see  AOServer#addEmailPipe
     */
    public int addEmailPipe(
        String aoServer,
        String path,
        String packageName
    ) throws IllegalArgumentException {
        return connector.emailPipes.addEmailPipe(
            getAOServer(aoServer),
            path,
            getPackage(packageName)
        );
    }

    /**
     * Adds an address to the list of email addresses that will be piped to
     * an <code>EmailPipe</code>.
     *
     * @param  address  the part of the email address before the <code>@</code>
     * @param  domain  the part of the email address after the <code>@</code>
     * @param  pkey  the pkey of the <code>EmailList</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data
     *					integrity violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>EmailDomain</code>
     *					or <code>EmailPipe</code>
     *
     * @see  #addEmailPipe
     * @see  EmailPipe
     * @see  EmailAddress
     * @see  EmailDomain
     */
    public int addEmailPipeAddress(
        String address,
        String domain,
        int pkey
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "addEmailPipeAddress(String,String,int)", null);
        try {
            EmailPipe ep=connector.emailPipes.get(pkey);
            if(ep==null) throw new IllegalArgumentException("Unable to find EmailPipe: "+ep);
            AOServer ao=ep.getAOServer();
            EmailDomain sd=ao.getEmailDomain(domain);
            if(sd==null) throw new IllegalArgumentException("Unable to find EmailDomain: "+domain+" on "+ao.getHostname());
            EmailAddress ea=sd.getEmailAddress(address);
            boolean added=false;
            if(ea==null) {
                ea=connector.emailAddresses.get(connector.emailAddresses.addEmailAddress(address, sd));
                added=true;
            }
            try {
                return ep.addEmailAddress(ea);
            } catch(RuntimeException err) {
                if(added && !ea.isUsed()) ea.remove();
                throw err;
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Adds a <code>FileBackupSetting</code> to a <code>FailoverFileReplication</code>.
     *
     * @param  replication  the pkey of the FailoverFileReplication
     * @param  path  the path that is being configured
     * @param  backupEnabled  the enabled flag for the prefix
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data
     *					integrity violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>FailoverFileReplication</code>, or <code>Package</code>
     *
     * @return  the pkey of the newly created <code>FileBackupSetting</code>
     *
     * @see  FailoverFileReplication#addFileBackupSetting
     * @see  FileBackupSetting
     */
    public int addFileBackupSetting(
        int replication,
        String path,
        boolean backupEnabled
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "addFileBackupSetting(int,String,boolean)", null);
        try {
            FailoverFileReplication ffr = getConnector().failoverFileReplications.get(replication);
            if(ffr==null) throw new IllegalArgumentException("Unable to find FailoverFileReplication: "+replication);
            return ffr.addFileBackupSetting(
                path,
                backupEnabled
            );
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Flags a <code>LinuxAccount</code> as being a <code>FTPGuestUser</code>.  Once
     * flagged, FTP connections as that user will be limited to transfers in their
     * home directory.
     *
     * @param  username  the username of the <code>LinuxAccount</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data
     *					integrity violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>LinuxAccount</code>
     *
     * @see  #addLinuxAccount
     * @see  LinuxAccount#addFTPGuestUser
     */
    public void addFTPGuestUser(
        String username
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "addFTPGuestUser(String)", null);
        try {
            getLinuxAccount(username).addFTPGuestUser();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Adds a new <code>HttpdJBossSite</code> to the system.  An <code>HttpdJBossSite</code> is
     * an <code>HttpdSite</code> that uses the Tomcat servlet engine and JBoss as an EJB container.
     *
     * @param  aoServer  the hostname of the <code>AOServer</code>
     * @param  siteName  the name of the <code>HttpdTomcatStdSite</code>
     * @param  packageName  the name of the <code>Package</code>
     * @param  jvmUsername  the username of the <code>LinuxAccount</code> that the Java VM
     *					will run as
     * @param  groupName  the name of the <code>LinuxGroup</code> that the web site will
     *					be owned by
     * @param  serverAdmin  the email address of the person who is responsible for the site
     *					content and reliability
     * @param  useApache  instructs the system to host static content, shtml, CGI, and PHP using Apache,
     *					comes at the price of less request control through Tomcat
     * @param  ipAddress  the <code>IPAddress</code> that the web site will bind to.  In
     *					order for HTTP requests to succeed, <code>DNSRecord</code> entries
     *					must point the hostnames of this <code>HttpdTomcatStdSite</code> to this
     *					<code>IPAddress</code>.  If <code>null</code>, the system will assign a
     *                                      shared IP address.
     * @param  primaryHttpHostname  the primary hostname of the <code>HttpdTomcatStdSite</code> for the
     *					HTTP protocol
     * @param  altHttpHostnames  any number of alternate hostnames for the HTTP protocol or
     *					<code>null</code> for none
     * @param  jBossVersion  the version number of <code>JBoss</code> to install in the site
     *
     * @param  contentSrc  initial content installed to the site directory upon creation
     *
     * @return  the <code>pkey</code> of the new <code>HttpdTomcatStdSite</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find a referenced object or a
     *					parameter is not in the right format
     *
     * @see  HttpdSite
     */
    public int addHttpdJBossSite(
        String aoServer,
        String siteName,
        String packageName,
        String jvmUsername,
        String groupName,
        String serverAdmin,
        boolean useApache,
        String ipAddress,
        String netDevice,
        String primaryHttpHostname,
        String[] altHttpHostnames,
        String jBossVersion,
        String contentSrc
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "addHttpdJBossSite(String,String,String,String,String,String,boolean,String,String,String,String[],String,String)", null);
        try {
            AOServer ao=getAOServer(aoServer);
            checkSiteName(siteName);
            if(!EmailAddress.isValidEmailAddress(serverAdmin)) throw new IllegalArgumentException("Invalid serverAdmin email address: "+serverAdmin);

            IPAddress ip;
            if (netDevice!=null && (netDevice=netDevice.trim()).length()==0) netDevice=null;
            if (ipAddress!=null && (ipAddress=ipAddress.trim()).length()==0) ipAddress=null;
            if (ipAddress!=null && netDevice!=null) {
                ip=getIPAddress(aoServer, netDevice, ipAddress);
            } else if(ipAddress==null && netDevice==null) {
                ip=null;
            } else {
                throw new IllegalArgumentException("ip_address and net_device must both be null or both be not null");
            }

            if(!EmailDomain.isValidFormat(primaryHttpHostname)) throw new IllegalArgumentException("Invalid hostname: "+primaryHttpHostname);
            for(int c=0;c<altHttpHostnames.length;c++) {
                String hostname=altHttpHostnames[c];
                if(!EmailDomain.isValidFormat(hostname)) throw new IllegalArgumentException("Invalid hostname: "+hostname);
            }
            HttpdJBossVersion hjv=connector.httpdJBossVersions.getHttpdJBossVersion(jBossVersion, ao.getServer().getOperatingSystemVersion());
            if(hjv==null) throw new IllegalArgumentException("Unable to find HttpdJBossVersion: "+jBossVersion);
            return ao.addHttpdJBossSite(
                siteName,
                getPackage(packageName),
                getLinuxServerAccount(aoServer, jvmUsername).getLinuxAccount(),
                getLinuxServerGroup(aoServer, groupName).getLinuxGroup(),
                serverAdmin,
                useApache,
                ip,
                primaryHttpHostname,
                altHttpHostnames,
                hjv.getTechnologyVersion(connector).getPkey(),
                (contentSrc==null || contentSrc.length()==0)?null:contentSrc
            );
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Adds a new <code>HttpdSharedTomcat</code> to a server.
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     * @exception  IllegalArgumentException  if unable to find the <code>LinuxServerAccount</code>,
     *					the <code>LinuxServerGroup</code>, or the <code>Server</code>
     *
     * @see  HttpdSharedTomcat
     * @see  LinuxServerAccount
     * @see  LinuxServerGroup
     * @see  Server
     */
    public int addHttpdSharedTomcat(
        String name,
        String aoServer,
        String version,
        String linuxServerAccount,
        String linuxServerGroup,
        boolean isSecure,
        boolean isOverflow
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "addHttpdSharedTomcat(String,String,String,String,String,boolean,boolean)", null);
        try {
            AOServer ao=getAOServer(aoServer);
            HttpdTomcatVersion ve = connector.httpdTomcatVersions.getHttpdTomcatVersion(version, ao.getServer().getOperatingSystemVersion());
            if (ve==null) throw new IllegalArgumentException("Unable to find HttpdTomcatVersion: "+version);
            return ao.addHttpdSharedTomcat(
                name,
                ve,
                getLinuxServerAccount(aoServer, linuxServerAccount),
                getLinuxServerGroup(aoServer, linuxServerGroup),
                isSecure,
                isOverflow
            );
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Adds a new <code>HttpdSiteURL</code> to a <code>HttpdSiteBind</code>.
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     * @exception  IllegalArgumentException  if unable to find the <code>HttpdSiteBind</code>
     */
    public int addHttpdSiteURL(
        int hsbPKey,
        String hostname
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "addHttpdSiteURL(int,String)", null);
        try {
            HttpdSiteBind hsb=connector.httpdSiteBinds.get(hsbPKey);
            if(hsb==null) throw new IllegalArgumentException("Unable to find HttpdSiteBind: "+hsbPKey);
            return hsb.addHttpdSiteURL(hostname);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Adds a new <code>HttpdTomcatContext</code> to a <code>HttpdTomcatSite</code>.
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code>, <code>HttpdSite</code>,
     *                                  or <code>HttpdTomcatSite</code>
     */
    public int addHttpdTomcatContext(
        String siteName,
        String aoServer,
        String className,
        boolean cookies,
        boolean crossContext,
        String docBase,
        boolean override,
        String path,
        boolean privileged,
        boolean reloadable,
        boolean useNaming,
        String wrapperClass,
        int debug,
        String workDir
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "addHttpdTomcatContext(String,String,String,boolean,boolean,String,boolean,String,boolean,boolean,boolean,String,int,String)", null);
        try {
            HttpdSite hs=getHttpdSite(aoServer, siteName);
            HttpdTomcatSite hts=hs.getHttpdTomcatSite();
            if(hts==null) throw new IllegalArgumentException("Unable to find HttpdTomcatSite: "+siteName+" on "+aoServer);
            return hts.addHttpdTomcatContext(
                className==null||(className=className.trim()).length()==0?null:className,
                cookies,
                crossContext,
                docBase,
                override,
                path,
                privileged,
                reloadable,
                useNaming,
                wrapperClass==null || (wrapperClass=wrapperClass.trim()).length()==0?null:wrapperClass,
                debug,
                workDir==null || (workDir=workDir.trim()).length()==0?null:workDir
            );
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Adds a new data source to a <code>HttpdTomcatContext</code>.
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code>, <code>HttpdSite</code>,
     *                                  <code>HttpdTomcatSite</code> or <code>HttpdTomcatContext</code>.
     */
    public int addHttpdTomcatDataSource(
        String siteName,
        String aoServer,
        String path,
        String name,
        String driverClassName,
        String url,
        String username,
        String password,
        int maxActive,
        int maxIdle,
        int maxWait,
        String validationQuery
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "addHttpdTomcatDataSource(String,String,String,String,String,String,String,String,int,int,int,String)", null);
        try {
            HttpdSite hs=getHttpdSite(aoServer, siteName);
            HttpdTomcatSite hts=hs.getHttpdTomcatSite();
            if(hts==null) throw new IllegalArgumentException("Unable to find HttpdTomcatSite: "+siteName+" on "+aoServer);
            HttpdTomcatContext htc=hts.getHttpdTomcatContext(path);
            if(htc==null) throw new IllegalArgumentException("Unable to find HttpdTomcatContext: "+siteName+" on "+aoServer+" path='"+path+'\'');
            return htc.addHttpdTomcatDataSource(
                name,
                driverClassName,
                url,
                username,
                password,
                maxActive,
                maxIdle,
                maxWait,
                validationQuery
            );
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Adds a new parameter to a <code>HttpdTomcatContext</code>.
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code>, <code>HttpdSite</code>,
     *                                  <code>HttpdTomcatSite</code> or <code>HttpdTomcatContext</code>.
     */
    public int addHttpdTomcatParameter(
        String siteName,
        String aoServer,
        String path,
        String name,
        String value,
        boolean override,
        String description
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "addHttpdTomcatParameter(String,String,String,String,String,boolean,String)", null);
        try {
            HttpdSite hs=getHttpdSite(aoServer, siteName);
            HttpdTomcatSite hts=hs.getHttpdTomcatSite();
            if(hts==null) throw new IllegalArgumentException("Unable to find HttpdTomcatSite: "+siteName+" on "+aoServer);
            HttpdTomcatContext htc=hts.getHttpdTomcatContext(path);
            if(htc==null) throw new IllegalArgumentException("Unable to find HttpdTomcatContext: "+siteName+" on "+aoServer+" path='"+path+'\'');
            return htc.addHttpdTomcatParameter(
                name,
                value,
                override,
                description
            );
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Adds a new <code>HttpdTomcatSharedSite</code> to the system.  An <code>HttpdTomcatSharedSite</code> is
     * an <code>HttpdSite</code> that contains a Tomcat servlet engine in the standard configuration.  It
     * only hosts one site per Java VM, but is arranged in the stock Tomcat structure and uses no
     * special code.
     *
     * @param  aoServer  the hostname of the <code>AOServer</code>
     * @param  siteName  the name of the <code>HttpdTomcatSharedSite</code>
     * @param  packageName  the name of the <code>Package</code>
     * @param  jvmUsername  the username of the <code>LinuxAccount</code> that the Java VM
     *					will run as
     * @param  groupName  the name of the <code>LinuxGroup</code> that the web site will
     *					be owned by
     * @param  serverAdmin  the email address of the person who is responsible for the site
     *					content and reliability
     * @param  useApache  instructs the system to host static content, shtml, CGI, and PHP using Apache,
     *					comes at the price of less request control through Tomcat
     * @param  ipAddress  the <code>IPAddress</code> that the web site will bind to.  In
     *					order for HTTP requests to succeed, <code>DNSRecord</code> entries
     *					must point the hostnames of this <code>HttpdTomcatSharedSite</code> to this
     *					<code>IPAddress</code>.  If <code>null</code>, the system will assign a
     *                                      shared IP address.
     * @param  primaryHttpHostname  the primary hostname of the <code>HttpdTomcatSharedSite</code> for the
     *					HTTP protocol
     * @param  altHttpHostnames  any number of alternate hostnames for the HTTP protocol or
     *					<code>null</code> for none
     * @param  sharedTomcatName   the shared Tomcat JVM under which this site runs or <code>null</code>
     *					to use an overflow JVM
     * @param  version                  the version of Tomcat to support
     *
     * @param  contentSrc  initial content installed to the site directories upon creation
     *
     * @return  the <code>pkey</code> of the new <code>HttpdTomcatSharedSite</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find a referenced object or a
     *					parameter is not in the right format
     *
     * @see  AOServer#addHttpdTomcatSharedSite
     * @see  HttpdTomcatSharedSite
     * @see  HttpdTomcatSite
     * @see  HttpdSite
     */
    public int addHttpdTomcatSharedSite(
        String aoServer,
        String siteName,
        String packageName,
        String jvmUsername,
        String groupName,
        String serverAdmin,
        boolean useApache,
        String ipAddress,
        String netDevice,
        String primaryHttpHostname,
        String[] altHttpHostnames,
        String sharedTomcatName,
        String version,
        String contentSrc
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "addHttpdTomcatSharedSite(String,String,String,String,String,String,boolean,String,String,String,String[],String,String,String)", null);
        try {
            AOServer ao=getAOServer(aoServer);
            checkSiteName(siteName);
            if(!EmailAddress.isValidEmailAddress(serverAdmin)) throw new IllegalArgumentException("Invalid serverAdmin email address: "+serverAdmin);

            IPAddress ip;
            if (netDevice!=null && (netDevice=netDevice.trim()).length()==0) netDevice=null;
            if (ipAddress!=null && (ipAddress=ipAddress.trim()).length()==0) ipAddress=null;
            if (ipAddress!=null && netDevice!=null) {
                ip=getIPAddress(aoServer, netDevice, ipAddress);
            } else if(ipAddress==null && netDevice==null) {
                ip=null;
            } else {
                throw new IllegalArgumentException("ip_address and net_device must both be null or both be not null");
            }

            if(!EmailDomain.isValidFormat(primaryHttpHostname)) throw new IllegalArgumentException("Invalid hostname: "+primaryHttpHostname);
            for(int c=0;c<altHttpHostnames.length;c++) {
                String hostname=altHttpHostnames[c];
                if(!EmailDomain.isValidFormat(hostname)) throw new IllegalArgumentException("Invalid hostname: "+hostname);
            }
            HttpdSharedTomcat sht;
            if(sharedTomcatName==null || sharedTomcatName.length()==0) {
                sht=null;
                sharedTomcatName=null;
            } else {
                sht = ao.getHttpdSharedTomcat(sharedTomcatName);
                if (sht==null) throw new IllegalArgumentException("Unable to find HttpdSharedTomcat: "+sharedTomcatName+" on "+aoServer);
            }
            HttpdTomcatVersion htv;
            if(version!=null && version.length()>0) {
                TechnologyName tn=connector.technologyNames.get(HttpdTomcatVersion.TECHNOLOGY_NAME);
                if(tn==null) throw new WrappedException(new SQLException("Unable to find TechnologyName: "+HttpdTomcatVersion.TECHNOLOGY_NAME));
                TechnologyVersion tv=tn.getTechnologyVersion(connector, version, ao.getServer().getOperatingSystemVersion());
                if(tv==null) throw new IllegalArgumentException("Unable to find TechnologyVersion: "+HttpdTomcatVersion.TECHNOLOGY_NAME+" version "+version);
                htv=tv.getHttpdTomcatVersion(connector);
                if(htv==null) throw new IllegalArgumentException("Unable to find HttpdTomcatVersion: "+HttpdTomcatVersion.TECHNOLOGY_NAME+" version "+version);
            } else htv=null;

            return ao.addHttpdTomcatSharedSite(
                siteName,
                getPackage(packageName),
                getLinuxServerAccount(aoServer, jvmUsername).getLinuxAccount(),
                getLinuxServerGroup(aoServer, groupName).getLinuxGroup(),
                serverAdmin,
                useApache,
                ip,
                primaryHttpHostname,
                altHttpHostnames,
                sharedTomcatName,
                htv,
                (contentSrc==null || contentSrc.length()==0)?null:contentSrc
            );
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Adds a new <code>HttpdTomcatStdSite</code> to the system.  An <code>HttpdTomcatStdSite</code> is
     * an <code>HttpdSite</code> that contains a Tomcat servlet engine in the standard configuration.  It
     * only hosts one site per Java VM, but is arranged in the stock Tomcat structure and uses no
     * special code.
     *
     * @param  aoServer  the hostname of the <code>AOServer</code>
     * @param  siteName  the name of the <code>HttpdTomcatStdSite</code>
     * @param  packageName  the name of the <code>Package</code>
     * @param  jvmUsername  the username of the <code>LinuxAccount</code> that the Java VM
     *					will run as
     * @param  groupName  the name of the <code>LinuxGroup</code> that the web site will
     *					be owned by
     * @param  serverAdmin  the email address of the person who is responsible for the site
     *					content and reliability
     * @param  useApache  instructs the system to host static content, shtml, CGI, and PHP using Apache,
     *					comes at the price of less request control through Tomcat
     * @param  ipAddress  the <code>IPAddress</code> that the web site will bind to.  In
     *					order for HTTP requests to succeed, <code>DNSRecord</code> entries
     *					must point the hostnames of this <code>HttpdTomcatStdSite</code> to this
     *					<code>IPAddress</code>.  If <code>null</code>, the system will assign a
     *                                      shared IP address.
     * @param  primaryHttpHostname  the primary hostname of the <code>HttpdTomcatStdSite</code> for the
     *					HTTP protocol
     * @param  altHttpHostnames  any number of alternate hostnames for the HTTP protocol or
     *					<code>null</code> for none
     * @param  tomcatVersion  the version number of <code>Tomcat</code> to install in the site
     *
     * @param  contentSrc  initial content installed to the site directory upon creation
     *
     * @return  the <code>pkey</code> of the new <code>HttpdTomcatStdSite</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find a referenced object or a
     *					parameter is not in the right format
     *
     * @see  AOServer#addHttpdTomcatStdSite
     * @see  HttpdTomcatStdSite
     * @see  HttpdTomcatSite
     * @see  HttpdSite
     */
    public int addHttpdTomcatStdSite(
        String aoServer,
        String siteName,
        String packageName,
        String jvmUsername,
        String groupName,
        String serverAdmin,
        boolean useApache,
        String ipAddress,
        String netDevice,
        String primaryHttpHostname,
        String[] altHttpHostnames,
        String tomcatVersion,
        String contentSrc
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "addHttpdTomcatStdSite(String,String,String,String,String,boolean,String,String,String,String[],String,String)", null);
        try {
            AOServer ao=getAOServer(aoServer);
            checkSiteName(siteName);
            if(!EmailAddress.isValidEmailAddress(serverAdmin)) throw new IllegalArgumentException("Invalid serverAdmin email address: "+serverAdmin);

            IPAddress ip;
            if (netDevice!=null && (netDevice=netDevice.trim()).length()==0) netDevice=null;
            if (ipAddress!=null && (ipAddress=ipAddress.trim()).length()==0) ipAddress=null;
            if (ipAddress!=null && netDevice!=null) {
                ip=getIPAddress(aoServer, netDevice, ipAddress);
            } else if(ipAddress==null && netDevice==null) {
                ip=null;
            } else {
                throw new IllegalArgumentException("ip_address and net_device must both be null or both be not null");
            }

            if(!EmailDomain.isValidFormat(primaryHttpHostname)) throw new IllegalArgumentException("Invalid hostname: "+primaryHttpHostname);
            for(int c=0;c<altHttpHostnames.length;c++) {
                String hostname=altHttpHostnames[c];
                if(!EmailDomain.isValidFormat(hostname)) throw new IllegalArgumentException("Invalid hostname: "+hostname);
            }
            HttpdTomcatVersion htv=connector.httpdTomcatVersions.getHttpdTomcatVersion(tomcatVersion, ao.getServer().getOperatingSystemVersion());
            if(htv==null) throw new IllegalArgumentException("Unable to find HttpdTomcatVersion: "+tomcatVersion);
            return ao.addHttpdTomcatStdSite(
                siteName,
                getPackage(packageName),
                getLinuxServerAccount(aoServer, jvmUsername).getLinuxAccount(),
                getLinuxServerGroup(aoServer, groupName).getLinuxGroup(),
                serverAdmin,
                useApache,
                ip,
                primaryHttpHostname,
                altHttpHostnames,
                htv,
                (contentSrc==null || contentSrc.length()==0)?null:contentSrc
            );
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Adds an <code>EmailAddress</code> to a <code>LinuxAccount</code>.  Not all
     * <code>LinuxAccount</code>s may be used as an email inbox.  The <code>LinuxAccountType</code>
     * of the account determines which accounts may store email.  When email is allowed for the account,
     * an <code>EmailAddress</code> is associated with the account as a <code>LinuxAccAddress</code>.
     *
     * @param  address  the part of the email address before the <code>@</code>
     * @param  domain  the part of the email address after the <code>@</code>
     * @param  aoServer  the hostname of the server storing the email account
     * @param  username  the uesrname of the <code>LinuxAccount</code> to route the emails to
     *
     * @return  the pkey of the new LinuxAccAddress
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data
     *					integrity violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>EmailDomain</code> or
     *                                       <code>LinuxAccount</code>
     *
     * @see  LinuxAccount#addEmailAddress
     * @see  LinuxAccAddress
     * @see  #addLinuxAccount
     * @see  #addEmailDomain
     * @see  EmailAddress
     */
    public int addLinuxAccAddress(
        String address,
        String domain,
        String aoServer,
        String username
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "addLinuxAccAddress(String,String,String,String)", null);
        try {
            EmailDomain sd=getEmailDomain(aoServer, domain);
            LinuxServerAccount lsa=getLinuxServerAccount(aoServer, username);
            EmailAddress ea=sd.getEmailAddress(address);
            boolean added;
            if(ea==null) {
                ea=connector.emailAddresses.get(sd.addEmailAddress(address));
                added=true;
            } else added=false;
            try {
                return lsa.addEmailAddress(ea);
            } catch(RuntimeException err) {
                if(added && !ea.isUsed()) ea.remove();
                throw err;
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Adds a new <code>LinuxAccount</code> the system.  A <code>LinuxAccount</code> does not
     * grant access to any <code>Server</code>s, <code>addLinuxServerAccount</code> must be used
     * after the <code>LinuxAccount</code> has been created.
     *
     * @param  username  the username of the new <code>LinuxAccount</code>
     * @param  primary_group  the primary group of the new account
     * @param  name  the account's full name
     * @param  office_location  optional office location available via the Unix <code>finger</code> command
     * @param  office_phone  optional phone number available via the Unix <code>finger</code> command
     * @param  home_phone  optional home phone number available vie the Unix <code>finger</code> command
     * @param  type  the <code>LinuxAccountType</code>
     * @param  shell  the login <code>Shell</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if the name is not a valid format or unable to find
     *					the <code>Username</code>, <code>LinuxAccountType</code>,
     *					or <code>Shell</code>
     *
     * @see  Username#addLinuxAccount
     * @see  #checkLinuxAccountName
     * @see  #addUsername
     * @see  #addLinuxServerAccount
     * @see  LinuxAccount
     * @see  LinuxAccountType
     * @see  LinuxServerAccount
     */
    public void addLinuxAccount(
        String username,
        String primary_group,
        String name,
        String office_location,
        String office_phone,
        String home_phone,
        String type,
        String shell
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "addLinuxAccount(String,String,String,String,String,String,String,String)", null);
        try {
            Username un=getUsername(username);
            checkLinuxAccountUsername(username);
            LinuxGroup lg=getLinuxGroup(primary_group);
            String validity=LinuxAccount.checkGECOS(name, "full name");
            if(validity!=null) throw new IllegalArgumentException(validity);
            LinuxAccountType lat=connector.linuxAccountTypes.get(type);
            if(lat==null) throw new IllegalArgumentException("Unable to find LinuxAccountType: "+type);
            Shell sh=connector.shells.get(shell);
            if(sh==null) throw new IllegalArgumentException("Unable to find Shell: "+shell);
            un.addLinuxAccount(
                primary_group,
                name,
                office_location,
                office_phone,
                home_phone,
                type,
                shell
            );
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Adds a <code>LinuxGroup</code> to the system.  After adding the <code>LinuxGroup</code>, the group
     * may be added to a <code>Server</code> via a <code>LinuxServerGroup</code>.  Also, <code>LinuxAccount</code>s
     * may be granted access to the group using <code>LinuxGroupAccount</code>.
     *
     * @param  name  the name of the new <code>LinuxGroup</code>
     * @param  packageName  the name of the <code>Package</code> that the group belongs to
     * @param  type  the <code>LinuxGroupType</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data
     *					integrity violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Package</code> or
     *					<code>LinuxGroupType</code>
     *
     * @see  Package#addLinuxGroup
     * @see  LinuxGroup
     * @see  LinuxGroupType
     * @see  Package
     * @see  #addLinuxServerGroup
     * @see  #addLinuxGroupAccount
     */
    public void addLinuxGroup(
        String name,
        String packageName,
        String type
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "addLinuxGroup(String,String,String)", null);
        try {
            LinuxGroupType lgt=connector.linuxGroupTypes.get(type);
            if(lgt==null) throw new IllegalArgumentException("Unable to find LinuxGroupType: "+type);
            connector.linuxGroups.addLinuxGroup(
                name,
                getPackage(packageName),
                type
            );
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Once a <code>LinuxAccount</code> and a <code>LinuxGroup</code> have been established,
     * permission for the <code>LinuxAccount</code> to access the <code>LinuxGroup</code> may
     * be granted using a <code>LinuxGroupAccount</code>.
     *
     * @param  group  the name of the <code>LinuxGroup</code>
     * @param  username  the username of the <code>LinuxAccount</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data
     *					integrity violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>LinuxGroup</code> or
     *					<code>LinuxAccount</code>
     *
     * @see  LinuxGroup#addLinuxAccount
     * @see  LinuxGroupAccount
     * @see  LinuxGroup
     * @see  LinuxAccount
     * @see  LinuxServerAccount
     * @see  LinuxServerGroup
     */
    public int addLinuxGroupAccount(
        String group,
        String username
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "addLinuxGroupAccount(String,String)", null);
        try {
            return getLinuxGroup(group).addLinuxAccount(getLinuxAccount(username));
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Grants a <code>LinuxAccount</code> access to a <code>Server</code>.  The primary
     * <code>LinuxGroup</code> for this account must already have a <code>LinuxServerGroup</code>
     * for the <code>Server</code>.
     *
     * @param  username  the username of the <code>LinuxAccount</code>
     * @param  aoServer  the hostname of the <code>AOServer</code>
     * @param  home  the home directory of the user, typically <code>/home/<i>first_letter_of_username</i>/<i>username</i></code>.
     *                  If <code>null</code>, <code>""</code>, or <code>"~"</code>, the default home directory for <code>username</code>
     *                  is used.
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data
     *					integrity violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>LinuxAccount</code>, <code>Server</code>
     *					or <code>AOServer</code>
     *
     * @see  LinuxAccount#addLinuxServerAccount
     * @see  #addLinuxAccount
     * @see  #addLinuxGroupAccount
     * @see  #addLinuxServerGroup
     * @see  AOServer
     */
    public int addLinuxServerAccount(
        String username,
        String aoServer,
        String home
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "addLinuxServerAccount(String,String,String)", null);
        try {
            LinuxAccount la=getLinuxAccount(username);
            AOServer ao=getAOServer(aoServer);
            if(
                home==null
                || home.length()==0
                || home.equals("~")
            ) home=LinuxServerAccount.getDefaultHomeDirectory(username, Locale.getDefault());
            return la.addLinuxServerAccount(ao, home);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Grants a <code>LinuxGroup</code> access to a <code>Server</code>.  If the group is
     * the primary <code>LinuxGroup</code> for any <code>LinuxAccount</code> that will be
     * added to the <code>Server</code>, the <code>LinuxGroup</code> must be added to the
     * <code>Server</code> first via a <code>LinuxServerGroup</code>.
     *
     * @param  group  the name of the <code>LinuxGroup</code>
     * @param  aoServer  the hostname of the <code>AOServer</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data
     *					integrity violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>LinuxGroup</code> or
     *					<code>Server</code>
     *
     * @see  LinuxGroup#addLinuxServerGroup
     * @see  #addLinuxGroup
     * @see  #addLinuxGroupAccount
     * @see  Server
     */
    public int addLinuxServerGroup(
        String group,
        String aoServer
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "addLinuxServerGroup(String,String)", null);
        try {
            return getLinuxGroup(group).addLinuxServerGroup(getAOServer(aoServer));
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Adds a new <code>MajordomoList</code> to a <code>MajordomoServer</code>.
     *
     * @param  domain  the domain of the <code>MajordomoServer</code>
     * @param  aoServer  the hostname of the <code>AOServer</code>
     * @param  listName  the name of the new list
     *
     * @return  the pkey of the new <code>EmailList</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data
     *					integrity violation occurs
     * @exception  IllegalArgumentException  if the name is not valid or unable to find the
     *                                  <code>Server</code>, code>EmailDomain</code>, or
     *                                  <code>MajordomoServer</code>
     *
     * @see  MajordomoServer#addMajordomoList
     * @see  #removeEmailList
     */
    public int addMajordomoList(
        String domain,
        String aoServer,
        String listName
    ) throws IllegalArgumentException {
        EmailDomain ed=getEmailDomain(aoServer, domain);
        MajordomoServer ms=ed.getMajordomoServer();
        if(ms==null) throw new IllegalArgumentException("Unable to find MajordomoServer: "+domain+" on "+aoServer);
        checkMajordomoListName(listName);
        return ms.addMajordomoList(listName);
    }

    /**
     * Adds a new <code>MajordomoServer</code> to an <code>EmailDomain</code>.
     *
     * @param  domain  the domain of the <code>EmailDomain</code>
     * @param  aoServer  the hostname of the <code>AOServer</code>
     * @param  linux_account  the username of the <code>LinuxAccount</code>
     * @param  linux_group  the naem of the <code>LinuxGroup</code>
     * @param  version  the version of the <code>MajordomoVersion</code>
     *
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code>,
     *                                  <code>EmailDomain</code>, <code>LinuxServerAccount</code>,
     *                                  <code>LinuxServerGroup</code>, or <code>MajordomoVersion</code>
     *
     * @see  EmailDomain#addMajordomoServer
     * @see  #removeMajordomoServer
     */
    public void addMajordomoServer(
        String domain,
        String aoServer,
        String linux_account,
        String linux_group,
        String version
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "addMajordomoServer(String,String,String,String,String)", null);
        try {
            EmailDomain ed=getEmailDomain(aoServer, domain);
            MajordomoVersion mv=connector.majordomoVersions.get(version);
            if(mv==null) throw new IllegalArgumentException("Unable to find MajordomoVersion: "+version);
            ed.addMajordomoServer(
                getLinuxServerAccount(aoServer, linux_account),
                getLinuxServerGroup(aoServer, linux_group),
                mv
            );
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Adds a new <code>MySQLDatabase</code> to the system.  Once added, <code>MySQLUser</code>s may
     * be granted access to the <code>MySQLDatabase</code> using a <code>MySQLDBUser</code>.
     * <p>
     * Because updates the the MySQL configurations are batched, the database may not be immediately
     * created in the MySQL system.  To ensure the database is ready for use, call <code>waitForMySQLDatabaseRebuild</code>.
     *
     * @param  name  the name of the new database
     * @param  aoServer  the hostname of the <code>AOServer</code>
     * @param  packageName  the name of the <code>Package</code> that owns the database
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data
     *					integrity violation occurs
     * @exception  IllegalArgumentException  if the database name is not valid or unable to
     *					find the <code>Server</code> or <code>Package</code>
     *
     * @see  MySQLServer#addMySQLDatabase
     * @see  #checkMySQLDatabaseName
     * @see  #addMySQLUser
     * @see  #addMySQLServerUser
     * @see  #addMySQLDBUser
     * @see  #removeMySQLDatabase
     * @see  #waitForMySQLDatabaseRebuild
     */
    public int addMySQLDatabase(
        String name,
        String mysqlServer,
        String aoServer,
        String packageName
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "addMySQLDatabase(String,String,String)", null);
        try {
            checkMySQLDatabaseName(name);
            return connector.mysqlDatabases.addMySQLDatabase(
                name,
                getMySQLServer(aoServer, mysqlServer),
                getPackage(packageName)
            );
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Grants a <code>MySQLServerUser</code> permission to access a <code>MySQLDatabase</code>.
     *
     * @param  name  the name of the <code>MySQLDatabase</code>
     * @param  aoServer  the hostname of the <code>AOServer</code>
     * @param  username  the username of the <code>MySQLUser</code>
     * @param  canSelect  grants the user <code>SELECT</code> privileges
     * @param  canInsert  grants the user <code>INSERT</code> privileges
     * @param  canUpdate  grants the user <code>UPDATE</code> privileges
     * @param  canDelete  grants the user <code>DELETE</code> privileges
     * @param  canCreate  grants the user <code>CREATE</code> privileges
     * @param  canDrop  grants the user <code>DROP</code> privileges
     * @param  canIndex  grants the user <code>INDEX</code> privileges
     * @param  canAlter  grants the user <code>ALTER</code> privileges
     * @param  canCreateTempTable  grants the user <code>CREATE TEMPORARY TABLE</code> privileges
     * @param  canLockTables  grants the user <code>LOCK TABLE</code> privileges
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data
     *					integrity violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code>,
     *					<code>MySQLDatabase</code>, or <code>MySQLServerUser</code>
     *
     * @see  MySQLDatabase#addMySQLServerUser
     * @see  #addMySQLUser
     * @see  #addMySQLServerUser
     * @see  #addMySQLDatabase
     */
    public int addMySQLDBUser(
        String name,
        String mysqlServer,
        String aoServer,
        String username,
        boolean canSelect,
        boolean canInsert,
        boolean canUpdate,
        boolean canDelete,
        boolean canCreate,
        boolean canDrop,
        boolean canIndex,
        boolean canAlter,
        boolean canCreateTempTable,
        boolean canLockTables,
        boolean canCreateView,
        boolean canShowView,
        boolean canCreateRoutine,
        boolean canAlterRoutine,
        boolean canExecute
    ) throws IllegalArgumentException {
        MySQLDatabase md=getMySQLDatabase(aoServer, mysqlServer, name);
        return md.addMySQLServerUser(
            getMySQLServerUser(aoServer, mysqlServer, username),
            canSelect,
            canInsert,
            canUpdate,
            canDelete,
            canCreate,
            canDrop,
            canIndex,
            canAlter,
            canCreateTempTable,
            canLockTables,
            canCreateView,
            canShowView,
            canCreateRoutine,
            canAlterRoutine,
            canExecute
        );
    }

    /**
     * Grants a <code>MySQLUser</code> access to a <code>Server</code> by adding a
     * <code>MySQLServerUser</code>.
     *
     * @param  username  the username of the <code>MySQLUser</code>
     * @param  aoServer  the hostname of the <code>AOServer</code>
     * @param  host  the host the user is allowed to connect from, almost always
     *					<code>MySQLHost.ANY_LOCAL_HOST</code> because the host limitation
     *					is provided on a per-database level by <code>MySQLDBUser</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data
     *					integrity violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>MySQLUser</code> or
     *					<code>Server</code>
     *
     * @see  MySQLUser#addMySQLServerUser
     * @see  MySQLServerUser#ANY_LOCAL_HOST
     * @see  #addMySQLUser
     * @see  #addMySQLDBUser
     */
    public int addMySQLServerUser(
        String username,
        String mysqlServer,
        String aoServer,
        String host
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "addMySQLServerUser(String,String,String,String)", null);
        try {
            return getMySQLUser(username).addMySQLServerUser(getMySQLServer(aoServer, mysqlServer), host==null || host.length()==0?null:host);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Adds a <code>MySQLUser</code> to the system.  A <code>MySQLUser</code> does not
     * exist on any <code>Server</code>, it merely indicates that a <code>Username</code>
     * will be used for accessing a <code>MySQLDatabase</code>.  In order to grant
     * the new <code>MySQLUser</code> access to a <code>MySQLDatabase</code>, first
     * add a <code>MySQLServerUser</code> on the same <code>Server</code> as the
     * <code>MySQLDatabase</code>, then add a <code>MySQLDBUser</code> granting
     * permission to the <code>MySQLDatabase</code>.
     *
     * @param  username  the <code>Username</code> that will be used for accessing MySQL
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data
     *					integrity violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Username</code>
     *
     * @see  Username#addMySQLUser
     * @see  #addUsername
     * @see  #addMySQLServerUser
     * @see  #addMySQLDatabase
     * @see  #addMySQLDBUser
     * @see  MySQLUser
     */
    public void addMySQLUser(
        String username
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "addMySQLUser(String)", null);
        try {
            Username un=getUsername(username);
            checkMySQLUsername(username);
            un.addMySQLUser();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Adds a network bind to the system.
     *
     * @exception  IOException  if unable to access the server
     * @exception  SQLException  if unable to access the database
     * @exception  IllegalArgumentException  if unable to find a referenced object.
     *
     * @see  AOServer#addNetBind
     */
    public int addNetBind(
        String aoServer,
        String packageName,
        String ipAddress,
        String net_device,
        int netPort,
        String netProtocol,
        String appProtocol,
        boolean openFirewall,
        boolean monitoringEnabled
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "addNetBind(String,String,String,String,int,String,String,boolean,boolean)", null);
        try {
            IPAddress ia=getIPAddress(aoServer, net_device, ipAddress);
            NetPort netPortObj=connector.netPorts.get(netPort);
            if(netPortObj==null) throw new IllegalArgumentException("Unable to find NetPort: "+netPort);
            NetProtocol netProt=connector.netProtocols.get(netProtocol);
            if(netProt==null) throw new IllegalArgumentException("Unable to find NetProtocol: "+netProtocol);
            Protocol appProt=connector.protocols.get(appProtocol);
            if(appProt==null) throw new IllegalArgumentException("Unable to find Protocol: "+appProtocol);
            return getAOServer(aoServer).addNetBind(
                getPackage(packageName),
                ia,
                netPortObj,
                netProt,
                appProt,
                openFirewall,
                monitoringEnabled
            );
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Whenever a credit card transaction fails, or when an account has not been paid for
     * over month, the billing contact for the <code>Business</code> is notified.  The details
     * of this notification are logged as a <code>NoticeLog</code>.
     *
     * @param  accounting  the accounting code of the <code>Business</code>
     * @param  billingContact  the name of the person who was contacted
     * @param  emailAddress  the email address that the email was sent to
     * @param  balance  their account balance at the time the notification was sent
     * @param  type  the <code>NoticeType</code>
     * @param  transid  the transaction ID associated with this notification or
     *					<code>NoticeLog.NO_TRANSACTION</code> for none
     *
     * @exception  IOException  if unable to access the server
     * @exception  SQLException  if unable to access the database
     * @exception  IllegalArgumentException  if unable to find the <code>Business</code>,
     *					<code>NoticeType</code>, or <code>Transaction</code>.
     *
     * @see  Business#addNoticeLog
     * @see  NoticeType
     * @see  Business
     * @see  Transaction
     */
    public void addNoticeLog(
        String accounting,
        String billingContact,
        String emailAddress,
        int balance,
        String type,
        int transid
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "addNoticeLog(String,String,String,int,String,int)", null);
        try {
            Business bu=getBusiness(accounting);
            NoticeType nt=connector.noticeTypes.get(type);
            if(nt==null) throw new IllegalArgumentException("Unable to find NoticeType: "+type);
            if(transid!=NoticeLog.NO_TRANSACTION) {
                Transaction trans=connector.transactions.get(transid);
                if(trans==null) throw new IllegalArgumentException("Unable to find Transaction: "+transid);
            }
            connector.noticeLogs.addNoticeLog(
                accounting,
                billingContact,
                emailAddress,
                balance,
                type,
                transid
            );
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Each <code>Business</code> can have multiple <code>Package</code>s associated with it.
     * Each <code>Package</code> is an allotment of resources with a monthly charge.
     * <p>
     * To determine if this connection can set prices:
     * <pre>
     * SimpleAOClient client=new SimpleAOClient();
     *
     * boolean canSetPrices=client
     *     .getConnector()
     *     .getThisBusinessAdministrator()
     *     .getUsername()
     *     .getPackage()
     *     .getBusiness()
     *     .canSetPrices();
     * </pre>
     *
     * @param  packageName  the name for the new package
     * @param  accounting  the accounting code of the <code>Business</code>
     * @param  packageDefinition  the unique identifier of the <code>PackageDefinition</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     * @exception  IllegalArgumentException  if unable to find 
     *
     * @see  #checkPackageName
     * @see  #addBusiness
     * @see  PackageDefinition
     */
    public int addPackage(
        String packageName,
        String accounting,
        int packageDefinition
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "addPackage(String,String,int)", null);
        try {
            checkPackageName(packageName);
            Business business=getBusiness(accounting);
            PackageDefinition pd=getPackageDefinition(packageDefinition);

            return business.addPackage(
                packageName,
                pd
            );
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Adds a new <code>PostgresDatabase</code> to the system.
     *
     * Because updates the the PostgreSQL configurations are batched, the database may not be immediately
     * created in the PostgreSQL system.  To ensure the database is ready for use, call
     * <code>waitForPostgresDatabaseRebuild</code>.
     *
     * @param  name  the name of the new database
     * @param  aoServer  the hostname of the <code>Server</code>
     * @param  datdba  the username of the <code>PostgresServerUser</code> who owns the database
     * @param  encoding  the encoding of the database
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data
     *					integrity violation occurs
     * @exception  IllegalArgumentException  if the database name is not valid or unable to
     *					find the <code>Server</code>, <code>PostgresUser</code>,
     *					<code>PostgresServerUser</code>, or <code>PostgresEncoding</code>
     *
     * @see  PostgresServer#addPostgresDatabase
     * @see  #checkPostgresDatabaseName
     * @see  #addPostgresUser
     * @see  #addPostgresServerUser
     * @see  #removePostgresDatabase
     * @see  #waitForPostgresDatabaseRebuild
     * @see  PostgresEncoding
     */
    public int addPostgresDatabase(
        String name,
        String postgres_server,
        String aoServer,
        String datdba,
        String encoding,
        boolean enablePostgis
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "addPostgresDatabase(String,String,String,String,String,boolean)", null);
        try {
            checkPostgresDatabaseName(name);
            PostgresServerUser psu=getPostgresServerUser(aoServer, postgres_server, datdba);
            PostgresServer ps=psu.getPostgresServer();
            PostgresVersion pv=ps.getPostgresVersion();
            PostgresEncoding pe=pv.getPostgresEncoding(connector, encoding);
            if(pe==null) throw new IllegalArgumentException("Unable to find PostgresEncoding for PostgresVersion "+pv.getTechnologyVersion(connector).getVersion()+": "+encoding);
            if(enablePostgis && pv.getPostgisVersion(connector)==null) throw new IllegalArgumentException("Unable to enable PostGIS, PostgresVersion "+pv.getTechnologyVersion(connector).getVersion()+" doesn't support PostGIS");
            return connector.postgresDatabases.addPostgresDatabase(
                name,
                ps,
                psu,
                pe,
                enablePostgis
            );
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Grants a <code>PostgresUser</code> access to a <code>Server</code> by adding a
     * <code>PostgresServerUser</code>.
     *
     * @param  username  the username of the <code>PostgresUser</code>
     * @param  postgresServer  the name of the PostgreSQL server
     * @param  aoServer  the hostname of the <code>Server</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data
     *					integrity violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>PostgresUser</code> or
     *					<code>Server</code>
     *
     * @see  PostgresUser#addPostgresServerUser
     * @see  #addPostgresUser
     */
    public int addPostgresServerUser(
        String username,
        String postgresServer,
        String aoServer
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "addPostgresServerUser(String,String,String)", null);
        try {
            return getPostgresUser(username).addPostgresServerUser(getPostgresServer(aoServer, postgresServer));
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Adds a <code>PostgresUser</code> to the system.  A <code>PostgresUser</code> does not
     * exist on any <code>Server</code>, it merely indicates that a <code>Username</code>
     * will be used for accessing a <code>PostgresDatabase</code>.  In order to grant
     * the new <code>PostgresUser</code> access to a <code>PostgresDatabase</code>, first
     * add a <code>PostgresServerUser</code> on the same <code>Server</code> as the
     * <code>PostgresDatabase</code>, then use the PostgreSQL <code>grant</code> and
     * <code>revoke</code> commands.
     *
     * @param  username  the <code>Username</code> that will be used for accessing PostgreSQL
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data
     *					integrity violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Username</code>
     *
     * @see  Username#addPostgresUser
     * @see  #addUsername
     * @see  #addPostgresServerUser
     * @see  #addPostgresDatabase
     * @see  PostgresUser
     */
    public void addPostgresUser(
        String username
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "addPostgresUser(String)", null);
        try {
            Username un=getUsername(username);
            checkPostgresUsername(username);
            un.addPostgresUser();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Adds a new <code>EmailDomain</code> to a <code>Server</code>.  Once added, the <code>Server</code>
     * will accept email for the provided domain.  In order for the email to function, however, a DNS
     * <code>MX</code> entry for the domain must point to a hostname that resolves to an
     * <code>IPAddress</code> on the <code>Server</code>.
     *
     * @param  domain  the email domain that will be hosted
     * @param  aoServer  the hostname of the <code>Server</code> that is being added
     * @param  packageName  the name of the <code>Package</code> that owns the email domain
     *
     * @exception  IOException  if unable to access the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if the domain is not in the correct format or
     *					unable to find the <code>Package</code>
     *
     * @see  EmailDomain#isValidFormat
     * @see  AOServer#addEmailDomain
     * @see  #addDNSRecord
     * @see  #addEmailForwarding
     * @see  #addEmailListAddress
     * @see  #addEmailPipeAddress
     * @see  #addLinuxAccAddress
     */
    public int addEmailDomain(
        String domain,
        String aoServer,
        String packageName
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "addEmailDomain(String,String,String)", null);
        try {
            if(!EmailDomain.isValidFormat(domain)) throw new IllegalArgumentException("Invalid domain name: "+domain);
            return getAOServer(aoServer).addEmailDomain(domain, getPackage(packageName));
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Grants access to the SMTP server.  Access to the SMTP server is granted when an
     * email client successfully logs into either the IMAP or POP3 servers.  If desired,
     * access to the SMTP server may also be granted from the API.  In either case,
     * the SMTP access will be revoked after 24 hours unless refresh.
     *
     * @param  packageName  the name of the <code>Package</code> that is granted access
     * @param  aoServer  the hostname of the <code>AOServer</code>
     * @param  host  the hostname or IP address that is being configured
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if the IP address is for valid or unable to
     *					find the <code>Package</code> or <code>Server</code>
     *
     * @see  Package#addEmailSmtpRelay
     */
    public int addEmailSmtpRelay(
        String packageName,
        String aoServer,
        String host,
        String type,
        long duration
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "addEmailSmtpRelay(String,String,String,String,long)", null);
        try {
            AOServer ao;
            if(aoServer!=null && (aoServer=aoServer.trim()).length()==0) aoServer=null;
            if(aoServer==null) ao=null;
            else ao=getAOServer(aoServer);
            EmailSmtpRelayType esrt=connector.emailSmtpRelayTypes.get(type);
            if(esrt==null) throw new WrappedException(new SQLException("Unable to find EmailSmtpRelayType: "+type));

            return getPackage(packageName).addEmailSmtpRelay(ao, host, esrt, duration);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Adds a <code>SpamEmailMessage</code>.
     *
     * @return  the pkey of the <code>SpamEmailMessage</code> that was created
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to the <code>EmailSmtpRelay</code>
     *
     * @see  EmailSmtpRelay#addSpamEmailMessage
     */
    public int addSpamEmailMessage(
        int email_relay,
        String message
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "addSpamEmailMessage(int,String)", null);
        try {
            EmailSmtpRelay esr=connector.emailSmtpRelays.get(email_relay);
            if(esr==null) throw new IllegalArgumentException("Unable to find EmailSmtpRelay: "+email_relay);
            return esr.addSpamEmailMessage(message);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Adds a new support request <code>Ticket</code> to the system.
     *
     * @param  accounting  the name of the <code>Business</code> that the support
     *                      request relates to
     * @param  business_administrator  the person to contact regarding the ticket
     * @param  ticket_type  the <code>TicketType</code>
     * @param  details  the content of the <code>Ticket</code>
     * @param  deadline  the requested deadline for ticket completion or
     *					<code>Ticket.NO_DEADLINE</code> for none
     * @param  client_priority  the priority assigned by the client
     * @param  admin_priority  the priority assigned by the ticket administrator
     * @param  technology  the <code>TechnologyName</code> that this <code>Ticket</code>
     *					relates to or <code>null</code> for none
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Package</code>,
     *					<code>BusinessAdministrator</code>, <code>TicketType</code>,
     *					client <code>TicketPriority</code>, admin <code>TicketPriority</code>,
     *					or <code>TechnologyName</code>
     *
     * @see  BusinessAdministrator#addTicket(Business,TicketType,String,long,TicketPriority,TicketPriority,TechnologyName,BusinessAdministrator,String,String)
     * @see  BusinessAdministrator#isActiveTicketAdmin
     * @see  Action
     * @see  Package
     * @see  TechnologyName
     * @see  Ticket
     * @see  TicketPriority
     * @see  TicketType
     */
    public int addTicket(
        String accounting,
        String business_administrator,
        String ticket_type,
        String details,
        long deadline,
        String client_priority,
        String admin_priority,
        String technology,
        String assigned_to,
        String contact_emails,
        String contact_phone_numbers
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "addTicket(String,String,String,String,long,String,String,String,String,String,String)", null);
        try {
            Business business;
            if(accounting==null || accounting.length()==0) business=null;
            else business=getBusiness(accounting);
            BusinessAdministrator pe=connector.businessAdministrators.get(business_administrator);
            if(pe==null) throw new IllegalArgumentException("Unable to find BusinessAdministrator: "+business_administrator);
            TicketType tt=connector.ticketTypes.get(ticket_type);
            if(tt==null) throw new IllegalArgumentException("Unable to find TicketType: "+ticket_type);
            TicketPriority clp=connector.ticketPriorities.get(client_priority);
            if(clp==null) throw new IllegalArgumentException("Unable to find TicketPriority: "+client_priority);
            TicketPriority adp;
            if(admin_priority==null || admin_priority.length()==0) adp=null;
            else {
                adp=connector.ticketPriorities.get(admin_priority);
                if(adp==null) throw new IllegalArgumentException("Unable to find TicketPriority: "+admin_priority);
            }
            TechnologyName tn;
            if(technology!=null && technology.length()>0) {
                tn=connector.technologyNames.get(technology);
                if(tn==null) throw new IllegalArgumentException("Unable to find TechnologyName: "+technology);
            } else tn=null;
            BusinessAdministrator assignedBA;
            if(assigned_to==null) assignedBA=null;
            else {
                assignedBA=connector.businessAdministrators.get(assigned_to);
                if(assignedBA==null) throw new IllegalArgumentException("Unable to find BusinessAdministrator: "+assigned_to);
            }

            return pe.addTicket(
                business,
                ticket_type,
                details,
                deadline,
                client_priority,
                admin_priority,
                tn==null?null:tn.pkey,
                assignedBA,
                contact_emails,
                contact_phone_numbers
            );
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Adds a work entry to a <code>Ticket</code> when a <code>Ticket</code> is worked on,
     * but not completed.
     *
     * @param  ticket_id  the pkey of the <code>Ticket</code>
     * @param  business_administrator  the username of the <code>BusinessAdministrator</code>
     *					making the change
     * @param  comments  the details of their work
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Ticket</code> or
     *					<code>BusinessAdministrator</code>
     *
     * @see  Ticket#actWorkEntry
     * @see  #addTicket
     * @see  Action
     */
    public void addTicketWork(
        int ticket_id,
        String business_administrator,
        String comments
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "addTicketWork(int,String,String)", null);
        try {
            Ticket ti=connector.tickets.get(ticket_id);
            if(ti==null) throw new IllegalArgumentException("Unable to find Ticket: "+ticket_id);
            BusinessAdministrator pe=connector.businessAdministrators.get(business_administrator);
            if(pe==null) throw new IllegalArgumentException("Unable to find BusinessAdministrator: "+business_administrator);
            ti.actWorkEntry(pe, comments);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Adds a new <code>Transaction</code> to a <code>Business</code>.
     *
     * @param  business  the accounting code of the <code>Business</code>
     * @param  source_business  the accounting code of the originating <code>Business</code>
     * @param  business_administrator  the username of the <code>BusinessAdministrator</code> making
     *					this <code>Transaction</code>
     * @param  type  the type as found in <code>Rate</code>
     * @param  description  the description
     * @param  quantity  the quantity in thousandths
     * @param  rate  the rate in hundredths
     * @param  paymentType
     * @param  paymentInfo
     * @param  processor
     * @param  payment_confirmed  the confirmation status of the transaction
     *
     * @return  the transid of the new <code>Transaction</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code>,
     *					<code>Business</code>, <code>BusinessAdministrator</code>, <code>Rate</code>,
     *					<code>PaymentType</code>, or <code>payment_confirmed</code>
     *
     * @see  Business#addTransaction
     * @see  Transaction
     * @see  #addBusiness
     * @see  Business
     * @see  #addBusinessAdministrator
     * @see  BusinessAdministrator
     * @see  TransactionType
     */
    public int addTransaction(
        String business,
        String source_business,
        String business_administrator,
        String type,
        String description,
        int quantity,
        int rate,
        String paymentType,
        String paymentInfo,
        String processor,
        byte payment_confirmed
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "addTransaction(String,String,String,String,int,int,String,String,String,byte)", null);
        try {
            Business bu=getBusiness(business);
            Business sourceBU=getBusiness(source_business);
            BusinessAdministrator pe=connector.businessAdministrators.get(business_administrator);
            if(pe==null) throw new IllegalArgumentException("Unable to find BusinessAdministrator: "+business_administrator);
            TransactionType tt=connector.transactionTypes.get(type);
            if(tt==null) throw new IllegalArgumentException("Unable to find TransactionType: "+type);
            PaymentType pt;
            if(paymentType==null || paymentType.length()==0) pt=null;
            else {
                pt=connector.paymentTypes.get(paymentType);
                if(pt==null) throw new IllegalArgumentException("Unable to find PaymentType: "+paymentType);
            }
            if(paymentInfo!=null && paymentInfo.length()==0) paymentInfo=null;
            CreditCardProcessor ccProcessor;
            if(processor==null || processor.length()==0) ccProcessor=null;
            else {
                ccProcessor = connector.creditCardProcessors.get(processor);
                if(ccProcessor==null) throw new IllegalArgumentException("Unable to find CreditCardProcessor: "+processor);
            }
            return bu.addTransaction(
                sourceBU,
                pe,
                tt,
                description,
                quantity,
                rate,
                pt,
                paymentInfo,
                ccProcessor,
                payment_confirmed
            );
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Adds a new <code>Username</code> to a <code>Package</code>.  A username is unique to the
     * system, regardless of which service(s) it is used for.  For example, if a username is
     * allocated for use as a MySQL user for business A, business B may not use the username as
     * a PostgreSQL user.
     *
     * @param  packageName  the name of the <code>Package</code> that owns the <code>Username</code>
     * @param  username  the username to add
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if the username is not a valid username or
     *					unable to find the <code>Package</code>
     *
     * @see  Package#addUsername
     * @see  Username
     * @see  #addPackage
     * @see  Package
     */
    public void addUsername(
        String packageName,
        String username
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "addUsername(String,String)", null);
            try {
            checkUsername(username);
            getPackage(packageName).addUsername(username);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Determines if a <code>LinuxAccount</code> currently has passwords set.
     *
     * @param  username  the username of the account
     *
     * @return  an <code>int</code> containing <code>PasswordProtected.NONE</code>,
     *          <code>PasswordProtected.SOME</code>, or <code>PasswordProtected.ALL</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     * @exception  IllegalArgumentException  if the <code>LinuxAccount</code> is not found
     *
     * @see  LinuxAccount#arePasswordsSet
     * @see  #setLinuxAccountPassword
     * @see  LinuxAccount
     * @see  PasswordProtected
     */
    public int areLinuxAccountPasswordsSet(
        String username
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "areLinuxAccountPasswordsSet(String)", null);
        try {
            return getLinuxAccount(username).arePasswordsSet();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Determines if a <code>MySQLUser</code> currently has passwords set.
     *
     * @param  username  the username of the user
     *
     * @return  an <code>int</code> containing <code>PasswordProtected.NONE</code>,
     *          <code>PasswordProtected.SOME</code>, or <code>PasswordProtected.ALL</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     * @exception  IllegalArgumentException  if the <code>MySQLUser</code> is not found
     *
     * @see  MySQLUser#arePasswordsSet
     * @see  #setMySQLUserPassword
     * @see  MySQLUser
     * @see  PasswordProtected
     */
    public int areMySQLUserPasswordsSet(
        String username
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "areMySQLUserPasswordsSet(String)", null);
        try {
            return getMySQLUser(username).arePasswordsSet();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Determines if a <code>PostgresUser</code> currently has passwords set.
     *
     * @param  username  the username of the user
     *
     * @return  an <code>int</code> containing <code>PasswordProtected.NONE</code>,
     *          <code>PasswordProtected.SOME</code>, or <code>PasswordProtected.ALL</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     * @exception  IllegalArgumentException  if the <code>PostgresUser</code> is not found
     *
     * @see  PostgresUser#arePasswordsSet
     * @see  #setPostgresUserPassword
     * @see  PostgresUser
     * @see  PasswordProtected
     */
    public int arePostgresUserPasswordsSet(
        String username
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "arePostgresUserPasswordsSet(String)", null);
        try {
            return getPostgresUser(username).arePasswordsSet();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Determines if a <code>Username</code> currently has passwords set.
     *
     * @param  username  the username
     *
     * @return  an <code>int</code> containing <code>PasswordProtected.NONE</code>,
     *          <code>PasswordProtected.SOME</code>, or <code>PasswordProtected.ALL</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     * @exception  IllegalArgumentException  if the <code>Username</code> is not found
     *
     * @see  Username#arePasswordsSet
     * @see  #setUsernamePassword
     * @see  Username
     * @see  PasswordProtected
     */
    public int areUsernamePasswordsSet(
        String username
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "areUsernamePasswordsSet(String)", null);
        try {
            return getUsername(username).arePasswordsSet();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Bounces a <code>Ticket</code>.
     *
     * @param  ticket_id  the pkey of the <code>Ticket</code>
     * @param  business_administrator  the username of the <code>BusinessAdministrator</code>
     *					making the change
     * @param  comments  the details of the bounce
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Ticket</code> or
     *					<code>BusinessAdministrator</code>
     *
     * @see  Ticket#actBounceTicket
     * @see  #addTicket
     * @see  Action
     */
    public void bounceTicket(
        int ticket_id,
        String business_administrator,
        String comments
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "bounceTicket(int,String,String)", null);
        try {
            Ticket ti=connector.tickets.get(ticket_id);
            if(ti==null) throw new IllegalArgumentException("Unable to find Ticket: "+ticket_id);
            BusinessAdministrator pe=connector.businessAdministrators.get(business_administrator);
            if(pe==null) throw new IllegalArgumentException("Unable to find BusinessAdministrator: "+business_administrator);
            ti.actBounceTicket(pe, comments);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Cancels a <code>Business</code>.  The <code>Business</code> must already be disabled.
     *
     * @param  accounting  the accounting code of the business
     * @param  reason  the reason the account is being canceled
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Business</code>
     *
     * @see  Business#cancel
     */
    public void cancelBusiness(
        String accounting,
        String reason
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "cancelBusiness(String,String)", null);
        try {
            getBusiness(accounting).cancel(reason);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Changes the administrative priority of a <code>Ticket</code>.
     *
     * @param  ticket_id  the pkey of the <code>Ticket</code>
     * @param  priority  the new <code>TicketPriority</code>
     * @param  business_administrator  the username of the <code>BusinessAdministrator</code>
     *					making the change
     * @param  comments  the details of the change
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Ticket</code>,
     *					<code>BusinessAdministrator</code>, or <code>TicketPriority</code>
     *
     * @see  Ticket#actChangeAdminPriority
     * @see  #addTicket
     * @see  TicketPriority
     * @see  Action
     */
    public void changeTicketAdminPriority(
        int ticket_id,
        String priority,
        String business_administrator,
        String comments
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "changeTicketAdminPriority(int,String,String,String)", null);
        try {
            Ticket ti=connector.tickets.get(ticket_id);
            if(ti==null) throw new IllegalArgumentException("Unable to find Ticket: "+ticket_id);
            TicketPriority pr;
            if(priority==null || priority.length()==0) {
                pr=null;
            } else {
                pr=connector.ticketPriorities.get(priority);
                if(pr==null) throw new IllegalArgumentException("Unable to find TicketPriority: "+priority);
            }
            BusinessAdministrator pe=connector.businessAdministrators.get(business_administrator);
            if(pe==null) throw new IllegalArgumentException("Unable to find BusinessAdministrator: "+business_administrator);
            ti.actChangeAdminPriority(pr, pe, comments);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Changes the client's priority of a <code>Ticket</code>.
     *
     * @param  ticket_id  the pkey of the <code>Ticket</code>
     * @param  priority  the new <code>TicketPriority</code>
     * @param  business_administrator  the username of the <code>BusinessAdministrator</code>
     *					making the change
     * @param  comments  the details of the change
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Ticket</code>,
     *					<code>BusinessAdministrator</code>, or <code>TicketPriority</code>
     *
     * @see  Ticket#actChangeClientPriority
     * @see  #addTicket
     * @see  TicketPriority
     * @see  Action
     */
    public void changeTicketClientPriority(
        int ticket_id,
        String priority,
        String business_administrator,
        String comments
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "changeTicketClientPriority(int,String,String,String)", null);
        try {
            Ticket ti=connector.tickets.get(ticket_id);
            if(ti==null) throw new IllegalArgumentException("Unable to find Ticket: "+ticket_id);
            TicketPriority pr=connector.ticketPriorities.get(priority);
            if(pr==null) throw new IllegalArgumentException("Unable to find TicketPriority: "+priority);
            BusinessAdministrator pe=connector.businessAdministrators.get(business_administrator);
            if(pe==null) throw new IllegalArgumentException("Unable to find BusinessAdministrator: "+business_administrator);
            ti.actChangeClientPriority(pr, pe, comments);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Changes the deadline of a <code>Ticket</code>.
     *
     * @param  ticket_id  the pkey of the <code>Ticket</code>
     * @param  deadline  the new deadline or <code>Ticket.NO_DEADLINE</code> for none
     * @param  business_administrator  the username of the <code>BusinessAdministrator</code>
     *					making the change
     * @param  comments  the details of the change
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Ticket</code> or
     *					<code>BusinessAdministrator</code>
     *
     * @see  Ticket#actChangeDeadline
     * @see  Ticket#NO_DEADLINE
     * @see  #addTicket
     * @see  TicketPriority
     * @see  Action
     */
    public void changeTicketDeadline(
        int ticket_id,
        long deadline,
        String business_administrator,
        String comments
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "changeTicketDeadline(int,long,String,String)", null);
        try {
            Ticket ti=connector.tickets.get(ticket_id);
            if(ti==null) throw new IllegalArgumentException("Unable to find Ticket: "+ticket_id);
            BusinessAdministrator pe=connector.businessAdministrators.get(business_administrator);
            if(pe==null) throw new IllegalArgumentException("Unable to find BusinessAdministrator: "+business_administrator);
            ti.actChangeDeadline(deadline, pe, comments);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Changes the technology associated with a <code>Ticket</code>.
     *
     * @param  ticket_id  the pkey of the <code>Ticket</code>
     * @param  technology  the name of the new <code>TechnologyName</code> or <code>null</code> for none
     * @param  business_administrator  the username of the <code>BusinessAdministrator</code>
     *					making the change
     * @param  comments  the details of the change
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Ticket</code>,
     *					<code>TechnologyName</code>, or <code>BusinessAdministrator</code>
     *
     * @see  Ticket#actChangeTechnology
     * @see  TechnologyName
     * @see  #addTicket
     * @see  TicketPriority
     * @see  Action
     */
    public void changeTicketTechnology(
        int ticket_id,
        String technology,
        String business_administrator,
        String comments
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "changeTicketTechnology(int,String,String,String)", null);
        try {
            Ticket ti=connector.tickets.get(ticket_id);
            if(ti==null) throw new IllegalArgumentException("Unable to find Ticket: "+ticket_id);
            TechnologyName tn;
            if(technology!=null && technology.length()>0) {
                tn=connector.technologyNames.get(technology);
                if(tn==null) throw new IllegalArgumentException("Unable to find TechnologyName: "+technology);
            } else tn=null;
            BusinessAdministrator pe=connector.businessAdministrators.get(business_administrator);
            if(pe==null) throw new IllegalArgumentException("Unable to find BusinessAdministrator: "+business_administrator);
            ti.actChangeTechnology(tn, pe, comments);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Changes the <code>TicketType</code> of a <code>Ticket</code>.
     *
     * @param  ticket_id  the pkey of the <code>Ticket</code>
     * @param  type  the name of the new <code>TicketType</code>
     * @param  business_administrator  the username of the <code>BusinessAdministrator</code>
     *					making the change
     * @param  comments  the details of the change
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Ticket</code>,
     *					<code>TicketType</code>, or <code>BusinessAdministrator</code>
     *
     * @see  Ticket#actChangeTicketType
     * @see  TicketType
     * @see  #addTicket
     * @see  TicketPriority
     * @see  Action
     */
    public void changeTicketType(
        int ticket_id,
        String type,
        String business_administrator,
        String comments
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "changeTicketType(int,String,String,String)", null);
        try {
            Ticket ti=connector.tickets.get(ticket_id);
            if(ti==null) throw new IllegalArgumentException("Unable to find Ticket: "+ticket_id);
            TicketType tt=connector.ticketTypes.get(type);
            if(tt==null) throw new IllegalArgumentException("Unable to find TicketType: "+type);
            BusinessAdministrator pe=connector.businessAdministrators.get(business_administrator);
            if(pe==null) throw new IllegalArgumentException("Unable to find BusinessAdministrator: "+business_administrator);
            ti.actChangeTicketType(tt, pe, comments);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Checks the format of an accounting code, which is the unique
     * identifier for a <code>Business</code>.
     *
     * @param  accounting  the accounting code to check
     *
     * @exception  IllegalArgumentException  if the accounting code is not a valid format
     *
     * @see  Business#isValidAccounting
     */
    public static void checkAccounting(
        String accounting
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "checkAccounting(String)", null);
        try {
            if(!Business.isValidAccounting(accounting)) throw new IllegalArgumentException("Invalid accounting code: "+accounting);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Checks the strength of a password that will be used for
     * a <code>BusinessAdministrator</code>.
     *
     * @param  username  the username of the <code>BusinessAdministrator</code> whos
     *					password will be set
     * @param  password  the new password
     *
     * @return  a description of why the password is weak or <code>null</code>
     *          if all checks succeed
     *
     * @exception  IllegalArgumentException  if username is not a valid <code>Username</code>
     *
     * @see  #setBusinessAdministratorPassword
     * @see  Username#isValidUsername
     * @see  BusinessAdministrator#checkPassword
     */
    public static PasswordChecker.Result[] checkBusinessAdministratorPassword(
        String username,
        String password
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "checkBusinessAdministratorPassword(String,String)", null);
        try {
            String check = Username.checkUsername(username, Locale.getDefault());
            if(check!=null) throw new IllegalArgumentException(check);
            return BusinessAdministrator.checkPassword(Locale.getDefault(), username, password);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Checks the format of a username that will be used for a <code>BusinessAdministrator</code>.
     *
     * @param  username  the username
     *
     * @exception  IllegalArgumentException  if the username is not in a valid format
     *
     * @see  BusinessAdministrator#isValidUsername
     * @see  #addBusinessAdministrator
     */
    public static void checkBusinessAdministratorUsername(
        String username
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "checkBusinessAdministratorUsername(String)", null);
        try {
            String check = BusinessAdministrator.checkUsername(username, Locale.getDefault());
            if(check!=null) throw new IllegalArgumentException(check);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Checks the format of a <code>DNSZone</code>.
     *
     * @param  zone  the new DNS zone name, some examples include <code>aoindustries.com.</code>
     *					and <code>netspade.co.uk.</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if the format is not valid
     *
     * @see  DNSZoneTable#checkDNSZone
     * @see  DNSZone
     */
    public void checkDNSZone(
        String zone
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "checkDNSZone(String)", null);
        try {
            if(!connector.dnsZones.checkDNSZone(zone)) throw new IllegalArgumentException("Invalid DNS zone: "+zone);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Checks the format of an email address.
     *
     * @param  address  the part of the email address before the <code>@</code>
     * @param  domain  the part of the email address after the <code>@</code>
     *
     * @exception  IllegalArgumentException  if the address is not in a valid format
     *
     * @see  EmailAddress#isValidFormat
     * @see  EmailDomain#isValidFormat
     */
    public static void checkEmailAddress(
        String address,
        String domain
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "checkEmailAddress(String,String)", null);
        try {
            if(!EmailAddress.isValidFormat(address)) throw new IllegalArgumentException("Invalid EmailAddress: "+address);
            if(!EmailDomain.isValidFormat(domain)) throw new IllegalArgumentException("Invalid EmailDomain: "+domain);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Checks the format of an email address that will be forwarded to another destination.
     *
     * @param  address      the part of the email address before the <code>@</code>
     * @param  domain       the part of the email address after the <code>@</code>
     * @param  destination  the final destination of emails sent to <code>address@domain</code>
     *
     * @exception  IllegalArgumentException  if the address or destination is not in a valid format
     *
     * @see  EmailAddress#isValidFormat
     * @see  EmailDomain#isValidFormat
     * @see  EmailAddress#isValidEmailAddress
     */
    public static void checkEmailForwarding(
        String address,
        String domain,
        String destination
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "checkEmailForwarding(String,String,String)", null);
        try {
            if(!EmailAddress.isValidFormat(address)) throw new IllegalArgumentException("Invalid EmailAddress: "+address);
            if(!EmailDomain.isValidFormat(domain)) throw new IllegalArgumentException("Invalid EmailDomain: "+domain);
            if(!EmailAddress.isValidEmailAddress(destination)) throw new IllegalArgumentException("Invalid destination: "+destination);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Checks the format of an email list path.
     *
     * @param  path  the path of the list
     *
     * @exception  IllegalArgumentException  if the name is not in a valid format
     *
     * @see  EmailList#isValidRegularPath
     */
    public static void checkEmailListPath(
        String path
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "checkEmailListPath(String)", null);
        try {
            if(!EmailList.isValidRegularPath(path)) throw new IllegalArgumentException("Invalid EmailList path: "+path);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Checks the format of an IP address.
     *
     * @param  ip  the IP address to check
     *
     * @exception  IllegalArgumentException  if the IP address is not in a valid format
     *
     * @see  IPAddress#isValidIPAddress
     */
    public static void checkIPAddress(
        String ip
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "checkIPAddress(String)", null);
        try {
            if(!IPAddress.isValidIPAddress(ip)) throw new IllegalArgumentException("Invalid IP address: "+ip);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Checks the format of a full name that will be used for
     * a <code>LinuxAccount</code>.
     *
     * @param  name  the full name
     *
     * @exception  IllegalArgumentException  if the name is not in a valid format
     *
     * @see  LinuxAccount#checkGECOS
     * @see  #setLinuxAccountName
     */
    public static void checkLinuxAccountName(
        String name
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "checkLinuxAccountName(String)", null);
        try {
            String validity=LinuxAccount.checkGECOS(name, "full name");
            if(validity!=null) throw new IllegalArgumentException(validity);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Checks the strength of a password that will be used for a <code>LinuxAccount</code> or
     * <code>LinuxServerAccount</code>.
     *
     * @param  username  the username of the account that will have its password set
     * @param  password  the new password for the account
     *
     * @return  a <code>String</code> describing why the password is not secure or <code>null</code>
     *					if the password is strong
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>LinuxAccount</code>
     *
     * @see  LinuxAccount#checkPassword
     * @see  #setLinuxAccountPassword
     * @see  #setLinuxServerAccountPassword
     * @see  PasswordChecker
     */
    public PasswordChecker.Result[] checkLinuxAccountPassword(
        String username,
        String password
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "checkLinuxAccountPassword(String,String)", null);
        try {
            return getLinuxAccount(username).checkPassword(Locale.getDefault(), password);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Checks the format of a username that will be used for a <code>LinuxAccount</code>.
     *
     * @param  username  the username
     *
     * @exception  IllegalArgumentException  if the username is not in a valid format
     *
     * @see  LinuxAccount#isValidUsername
     * @see  #addLinuxAccount
     */
    public static void checkLinuxAccountUsername(
        String username
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "checkLinuxAccountUsername(String)", null);
        try {
            if(!LinuxAccount.isValidUsername(username)) throw new IllegalArgumentException("Invalid LinuxAccount username: "+username);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Checks the format of a groupname.
     *
     * @param  groupname  the groupname that will be used to name a <code>LinuxGroup</code>
     *
     * @exception  IllegalArgumentException  if the groupname is not in a valid format
     *
     * @see  LinuxGroup#isValidGroupname
     * @see  #addLinuxGroup
     */
    public static void checkLinuxGroupname(
        String groupname
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "checkLinuxGroupname(String)", null);
        try {
            if(!LinuxGroup.isValidGroupname(groupname)) throw new IllegalArgumentException("Invalid groupname: "+groupname);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Checks the format of a MySQL database name.
     *
     * @param  name  the name that will be used to create a new database
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if the database name is not allowed
     *
     * @see  MySQLDatabaseTable#isValidDatabaseName
     * @see  #addMySQLDatabase
     */
    public void checkMySQLDatabaseName(
        String name
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "checkMySQLDatabaseName(String)", null);
        try {
            if(!connector.mysqlDatabases.isValidDatabaseName(name)) throw new IllegalArgumentException("Invalid MySQL database name: "+name);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Checks the strength of a password that will be used for
     * a <code>MySQLServerUser</code>.
     *
     * @param  username  the username of the <code>MySQLServerUser</code> whos
     *					password will be set
     * @param  password  the new password
     *
     * @return  a description of why the password is weak or <code>null</code>
     *          if all checks succeed
     *
     * @exception  IOException  if unable to load the dictionary resource
     *
     * @see  #setMySQLUserPassword
     * @see  #setMySQLServerUserPassword
     * @see  MySQLUser#checkPassword
     */
    public static PasswordChecker.Result[] checkMySQLPassword(
        String username,
        String password
    ) {
        return MySQLUser.checkPassword(Locale.getDefault(), username, password);
    }

    /**
     * Checks the format of a MySQL server name.
     *
     * @param  name  the name that will be used to create a new server
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if the database name is not allowed
     *
     * @see  MySQLServer#checkServerName
     */
    public static void checkMySQLServerName(
        String name
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "checkMySQLServerName(String)", null);
        try {
            MySQLServer.checkServerName(name);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Checks the format of a username that will be used for a <code>MySQLUser</code>.
     *
     * @param  username  the username
     *
     * @exception  IllegalArgumentException  if the username is not in a valid format
     *
     * @see  MySQLUser#isValidUsername
     * @see  #addMySQLUser
     */
    public static void checkMySQLUsername(
        String username
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "checkMySQLUsername(String)", null);
        try {
            if(!MySQLUser.isValidUsername(username)) throw new IllegalArgumentException("Invalid MySQLUser username: "+username);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Checks the format of a <code>Package</code> name.
     *
     * @param  packageName  the name that will be used for a <code>Package</code>
     *
     * @exception  IllegalArgumentException  if the name is not valid
     *
     * @see  Package#isValidPackageName
     * @see  #addPackage
     */
    public static void checkPackageName(
        String packageName
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "checkPackageName(String)", null);
        try {
            if(!Package.isValidPackageName(packageName)) throw new IllegalArgumentException("Invalid package name: "+packageName);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Checks the format of a PostgreSQL database name.
     *
     * @param  name  the name that will be used to create a new database
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if the database name is not allowed
     *
     * @see  PostgresDatabaseTable#isValidDatabaseName
     * @see  #addPostgresDatabase
     */
    public void checkPostgresDatabaseName(
        String name
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "checkPostgresDatabaseName(String)", null);
        try {
            if(!connector.postgresDatabases.isValidDatabaseName(name)) throw new IllegalArgumentException("Invalid PostgreSQL database name: "+name);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Checks the strength of a password that will be used for
     * a <code>PostgresServerUser</code>.
     *
     * @param  username  the username of the <code>PostgresServerUser</code> whos
     *					password will be set
     * @param  password  the new password
     *
     * @return  a description of why the password is weak or <code>null</code>
     *          if all checks succeed
     *
     * @exception  IOException  if unable to load the dictionary resource
     *
     * @see  #setPostgresUserPassword
     * @see  #setPostgresServerUserPassword
     * @see  PostgresUser#checkPassword
     */
    public static PasswordChecker.Result[] checkPostgresPassword(
        String username,
        String password
    ) throws IOException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "checkPostgresPassword(String,String)", null);
        try {
            return PostgresUser.checkPassword(Locale.getDefault(), username, password);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Checks the format of a PostgreSQL server name.
     *
     * @param  name  the name that will be used to create a new server
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if the database name is not allowed
     *
     * @see  PostgresServer#checkServerName
     */
    public static void checkPostgresServerName(
        String name
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "checkPostgresServerName(String)", null);
        try {
            PostgresServer.checkServerName(name);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Checks the format of a username that will be used for a <code>PostgresUser</code>.
     *
     * @param  username  the username
     *
     * @exception  IllegalArgumentException  if the username is not in a valid format
     *
     * @see  PostgresUser#isValidUsername
     * @see  #addPostgresUser
     */
    public static void checkPostgresUsername(
        String username
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "checkPostgresUsername(String)", null);
        try {
            if(!PostgresUser.isValidUsername(username)) throw new IllegalArgumentException("Invalid PostgresUser username: "+username);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Checks the format of an email domain.
     *
     * @param  domain  the domain that will be used as a <code>EmailDomain</code>
     *
     * @exception  IllegalArgumentException  if the domain is not in a valid format
     *
     * @see  EmailDomain#isValidFormat
     * @see  #addEmailDomain
     */
    public static void checkEmailDomain(
        String domain
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "checkEmailDomain(String)", null);
        try {
            if(!EmailDomain.isValidFormat(domain)) throw new IllegalArgumentException("Invalid EmailDomain: "+domain);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Checks the format of a Majordomo list name.
     *
     * @exception  IllegalArgumentException  if the domain is not in a valid format
     *
     * @see  MajordomoList#isValidListName
     * @see  #addMajordomoList
     */
    public static void checkMajordomoListName(
        String listName
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "checkMajordomoListName(String)", null);
        try {
            if(!MajordomoList.isValidListName(listName)) throw new IllegalArgumentException("Invalid Majordomo list name: "+listName);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Checks the format of an <code>HttpdSharedTomcat</code> name.
     *
     * @param  tomcatName  the name of the <code>HttpdSharedTomcat</code>
     *
     * @exception  IllegalArgumentException  if the name is not in a valid format
     *
     * @see  HttpdSharedTomcat#isValidSharedTomcatName
     * @see  #addHttpdSharedTomcat
     * @see  #addHttpdTomcatSharedSite
     */
    public static void checkSharedTomcatName(
        String tomcatName
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "checkSharedTomcatName(String)", null);
        try {
            if(!HttpdSharedTomcat.isValidSharedTomcatName(tomcatName)) throw new IllegalArgumentException("Invalid shared Tomcat name: "+tomcatName);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Checks the format of an <code>HttpdSite</code> name.
     *
     * @param  siteName  the name of the <code>HttpdSite</code>
     *
     * @exception  IllegalArgumentException  if the name is not in a valid format
     *
     * @see  HttpdSite#isValidSiteName
     * @see  #addHttpdTomcatStdSite
     */
    public static void checkSiteName(
        String siteName
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "checkSiteName(String)", null);
        try {
            if(!HttpdSite.isValidSiteName(siteName)) throw new IllegalArgumentException("Invalid site name: "+siteName);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Checks the format of a username.
     *
     * @param  username  the username that will be used as a <code>Username</code>
     *
     * @exception  IllegalArgumentException  if the username is not in a valid format
     *
     * @see  Username#isValidUsername
     * @see  #addUsername
     */
    public static void checkUsername(
        String username
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "checkUsername(String)", null);
        try {
            String check = Username.checkUsername(username, Locale.getDefault());
            if(check!=null) throw new IllegalArgumentException(check);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Checks the strength of a password that will be used for
     * a <code>Username</code>.  The strength requirement is based on
     * which services use the <code>Username</code>.
     *
     * @param  username  the username whos password will be set
     * @param  password  the new password
     *
     * @return  a description of why the password is weak or <code>null</code>
     *          if all checks succeed
     *
     * @exception  IOException  if unable to load the dictionary resource or unable to access the server
     * @exception  SQLException  if unable to access the database
     * @exception  IllegalArgumentException  if unable to find the <code>Username</code>
     *
     * @see  #setUsernamePassword
     * @see  Username#checkPassword
     */
    public PasswordChecker.Result[] checkUsernamePassword(
        String username,
        String password
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "checkUsernamePassword(String,String)", null);
        try {
            return getUsername(username).checkPassword(Locale.getDefault(), password);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Checks if a password matches a <code>LinuxServerAccount</code>.
     *
     * @param  username  the username of the account
     * @param  aoServer  the hostname of the server to check
     * @param  password  the password to compare against
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if the <code>LinuxAccount</code>, <code>Server</code>,
     *                                  <code>AOServer</code>, or <code>LinuxServerAccount</code> is not found
     *
     * @see  LinuxServerAccount#passwordMatches
     * @see  #addLinuxServerAccount
     */
    public boolean compareLinuxServerAccountPassword(
        String username,
        String aoServer,
        String password
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "compareLinuxServerAccountPassword(String,String,String)", null);
        try {
            return getLinuxServerAccount(aoServer, username).passwordMatches(password);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Completes a <code>Ticket</code>.  Once a <code>Ticket</code> is completed, no more
     * modifications or actions may be applied to the <code>Ticket</code>.
     *
     * @param  ticket_id  the pkey of the <code>Ticket</code>
     * @param  business_administrator  the username of the <code>BusinessAdministrator</code>
     *					making the change
     * @param  comments  the details of the change
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Ticket</code>,
     *					<code>TicketType</code>, or <code>BusinessAdministrator</code>
     *
     * @see  Ticket#actCompleteTicket
     * @see  #addTicket
     * @see  Action
     */
    public void completeTicket(
        int ticket_id,
        String business_administrator,
        String comments
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "completeTicket(int,String,String)", null);
        try {
            Ticket ti=connector.tickets.get(ticket_id);
            if(ti==null) throw new IllegalArgumentException("Unable to find Ticket: "+ticket_id);
            BusinessAdministrator pe=connector.businessAdministrators.get(business_administrator);
            if(pe==null) throw new IllegalArgumentException("Unable to find BusinessAdministrator: "+business_administrator);
            ti.actCompleteTicket(pe, comments);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Copies the contents of user's home directory from one server to another.
     *
     * @param  username  the username of the <code>LinuxAccount</code>
     * @param  from_ao_server  the server to get the data from
     * @param  to_ao_server  the server to put the data on
     *
     * @return  the number of bytes transferred
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     * @exception  IllegalArgumentException  if unable to find the source <code>LinuxServerAccount</code>
     *					or destination <code>AOServer</code>
     *
     * @see  LinuxServerAccount#copyHomeDirectory
     * @see  #addLinuxServerAccount
     * @see  #removeLinuxServerAccount
     */
    public long copyHomeDirectory(
        String username,
        String from_ao_server,
        String to_ao_server
    ) {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "copyHomeDirectory(String,String,String)", null);
        try {
            return getLinuxServerAccount(from_ao_server, username).copyHomeDirectory(getAOServer(to_ao_server));
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Copies the password from one <code>LinuxServerAccount</code> to another.
     *
     * @param  from_username  the username to copy from
     * @param  from_ao_server  the server to get the data from
     * @param  to_username  the username to copy to
     * @param  to_ao_server  the server to put the data on
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     * @exception  IllegalArgumentException  if unable to find a <code>LinuxServerAccount</code>
     *
     * @see  LinuxServerAccount#copyPassword
     * @see  #addLinuxServerAccount
     * @see  #removeLinuxServerAccount
     */
    public void copyLinuxServerAccountPassword(
        String from_username,
        String from_ao_server,
        String to_username,
        String to_ao_server
    ) {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "copyLinuxServerAccountPassword(String,String,String,String)", null);
        try {
            getLinuxServerAccount(from_ao_server, from_username).copyPassword(getLinuxServerAccount(to_ao_server, to_username));
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Encrypts a password using a pure Java implementation of the standard Unix <code>crypt</code>
     * function.
     *
     * @param  password  the password that is to be encrypted
     * @param  salt  the two character salt for the encryption process, if <code>null</code>,
     *					a random salt will be used
     *
     * @see  BusinessAdministrator#crypt(String,String)
     */
    public static String crypt(String password, String salt) {
        return BusinessAdministrator.crypt(password, salt);
    }

    /**
     * Disables a <code>CreditCard</code>.  When a <code>Transaction</code> using a
     * <code>CreditCard</code> fails, the <code>CreditCard</code> is disabled.
     *
     * @param  pkey  the unique identifier of the <code>CreditCard</code>
     * @param  reason  the reason the card is being disabled
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>CreditCard</code>
     *
     * @see  CreditCard#declined
     * @see  Transaction
     * @see  CreditCard
     */
    public void declineCreditCard(
        int pkey,
        String reason
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "declineCreditCard(int,String)", null);
        try {
            CreditCard card=connector.creditCards.get(pkey);
            if(card==null) throw new IllegalArgumentException("Unable to find CreditCard: "+pkey);
            card.declined(reason);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Disables a business, recursively disabling all of its enabled child components.
     *
     * @param  accounting  the accounting code to disable
     * @param  disableReason  the reason the account is being disabled
     *
     * @return  the pkey of the new <code>DisableLog</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the necessary <code>AOServObject</code>s
     */
    public int disableBusiness(String accounting, String disableReason) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.UNKNOWN, SimpleAOClient.class, "disableBusiness(String,String)", null);
        try {
            Business bu=getBusiness(accounting);
            DisableLog dl=connector.disableLogs.get(bu.addDisableLog(disableReason));
            for(Package pk : bu.getPackages()) if(pk.disable_log==-1) disablePackage(dl, pk);
            bu.disable(dl);
            return dl.getPkey();
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    /**
     * Disables a package, recursively disabling all of its enabled child components.
     *
     * @param  name  the name of the package
     * @param  disableReason  the reason the account is being disabled
     *
     * @return  the pkey of the new <code>DisableLog</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the necessary <code>AOServObject</code>s
     */
    public int disablePackage(String name, String disableReason) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "disablePackage(String,String)", null);
        try {
            Package pk=getPackage(name);
            DisableLog dl=connector.disableLogs.get(pk.getBusiness().addDisableLog(disableReason));
            disablePackage(dl, pk);
            return dl.getPkey();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
    private void disablePackage(DisableLog dl, Package pk) {
        Profiler.startProfile(Profiler.UNKNOWN, SimpleAOClient.class, "disablePackage(DisableLog,Package)", null);
        try {
            /*
             * Email stuff
             */
            for(EmailList el : pk.getEmailLists()) if(el.disable_log==-1) el.disable(dl);
            for(EmailPipe ep : pk.getEmailPipes()) if(ep.disable_log==-1) ep.disable(dl);
            for(EmailSmtpRelay ssr : pk.getEmailSmtpRelays()) if(ssr.disable_log==-1) ssr.disable(dl);

            /*
             * HTTP stuff
             */
            List<AOServer> httpdServers=new SortedArrayList<AOServer>();
            for(HttpdSharedTomcat hst : pk.getHttpdSharedTomcats()) {
                if(hst.disable_log==-1) {
                    hst.disable(dl);
                    AOServer ao=hst.getAOServer();
                    if(!httpdServers.contains(ao)) httpdServers.add(ao);
                }
            }
            for(HttpdSite hs : pk.getHttpdSites()) {
                if(hs.disable_log==-1) {
                    disableHttpdSite(dl, hs);
                    AOServer ao=hs.getAOServer();
                    if(!httpdServers.contains(ao)) httpdServers.add(ao);
                }
            }

            // Wait for httpd site rebuilds to complete, which shuts down all the appropriate processes
            for(AOServer httpdServer : httpdServers) httpdServer.waitForHttpdSiteRebuild();

            // Disable the user accounts once the JVMs have been shut down
            for(Username un : pk.getUsernames()) if(un.disable_log==-1) disableUsername(dl, un);

            pk.disable(dl);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    /**
     * Disables a <code>HttpdSharedTomcat</code>.
     *
     * @param  name  the name of the tomcat JVM
     * @param  aoServer  the server that hosts the JVM
     * @param  disableReason  the reason the JVM is being disabled
     *
     * @return  the pkey of the new <code>DisableLog</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>AOServer</code>, or <code>HttpdSharedTomcat</code>
     */
    public int disableHttpdSharedTomcat(
        String name,
        String aoServer,
        String disableReason
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "disableHttpdSharedTomcat(String,String,String)", null);
        try {
            HttpdSharedTomcat hst=getHttpdSharedTomcat(aoServer, name);
            DisableLog dl=connector.disableLogs.get(hst.getLinuxServerGroup().getLinuxGroup().getPackage().getBusiness().addDisableLog(disableReason));
            hst.disable(dl);
            return dl.getPkey();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Disables an <code>EmailPipe</code>.
     *
     * @param  pkey  the pkey of the pipe
     * @param  disableReason  the reason the pipe is being disabled
     *
     * @return  the pkey of the new <code>DisableLog</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>EmailPipe</code>
     */
    public int disableEmailPipe(
        int pkey,
        String disableReason
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "disableEmailPipe(int,String)", null);
        try {
            EmailPipe ep=connector.emailPipes.get(pkey);
            if(ep==null) throw new IllegalArgumentException("Unable to find EmailPipe: "+pkey);
            DisableLog dl=connector.disableLogs.get(ep.getPackage().getBusiness().addDisableLog(disableReason));
            ep.disable(dl);
            return dl.getPkey();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Disables a <code>HttpdSite</code>.
     *
     * @param  name  the name of the site
     * @param  aoServer  the server that hosts the site
     * @param  disableReason  the reason the site is being disabled
     *
     * @return  the pkey of the new <code>DisableLog</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code>, or <code>HttpdSite</code>
     */
    public int disableHttpdSite(
        String name,
        String aoServer,
        String disableReason
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "disableHttpdSite(String,String,String)", null);
        try {
            HttpdSite hs=getHttpdSite(aoServer, name);
            DisableLog dl=connector.disableLogs.get(hs.getPackage().getBusiness().addDisableLog(disableReason));
            disableHttpdSite(dl, hs);
            return dl.getPkey();
        } finally {
            Profiler.endProfile(Profiler.FAST);        }
    }
    private void disableHttpdSite(DisableLog dl, HttpdSite hs) {
        Profiler.startProfile(Profiler.UNKNOWN, SimpleAOClient.class, "disableHttpdSite(DisableLog,HttpdSite)", null);
        try {
            for(HttpdSiteBind hsb : hs.getHttpdSiteBinds()) if(hsb.disable_log==-1) hsb.disable(dl);
            hs.disable(dl);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    /**
     * Disables a <code>HttpdSiteBind</code>.
     *
     * @param  pkey  the pkey of the bind
     * @param  disableReason  the reason the bind is being disabled
     *
     * @return  the pkey of the new <code>DisableLog</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>HttpdSiteBind</code>
     */
    public int disableHttpdSiteBind(
        int pkey,
        String disableReason
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "disableHttpdSiteBind(int,String)", null);
        try {
            HttpdSiteBind hsb=connector.httpdSiteBinds.get(pkey);
            if(hsb==null) throw new IllegalArgumentException("Unable to find HttpdSiteBind: "+pkey);
            DisableLog dl=connector.disableLogs.get(hsb.getHttpdSite().getPackage().getBusiness().addDisableLog(disableReason));
            hsb.disable(dl);
            return dl.getPkey();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Disables an <code>EmailList</code>.
     *
     * @param  path  the path of the list
     * @param  aoServer  the server the list is part of
     * @param  disableReason  the reason the bind is being disabled
     *
     * @return  the pkey of the new <code>DisableLog</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>EmailList</code>
     */
    public int disableEmailList(
        String path,
        String aoServer,
        String disableReason
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "disableEmailList(String,String,String)", null);
        try {
            EmailList el=getEmailList(aoServer, path);
            DisableLog dl=connector.disableLogs.get(el.getLinuxServerGroup().getLinuxGroup().getPackage().getBusiness().addDisableLog(disableReason));
            el.disable(dl);
            return dl.getPkey();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Disables a <code>EmailSmtpRelay</code>.
     *
     * @param  pkey  the pkey of the relay
     * @param  disableReason  the reason the bind is being disabled
     *
     * @return  the pkey of the new <code>DisableLog</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>EmailSmtpRelay</code>
     */
    public int disableEmailSmtpRelay(
        int pkey,
        String disableReason
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "disableEmailSmtpRelay(int,String)", null);
        try {
            EmailSmtpRelay ssr=connector.emailSmtpRelays.get(pkey);
            if(ssr==null) throw new IllegalArgumentException("Unable to find EmailSmtpRelay: "+pkey);
            DisableLog dl=connector.disableLogs.get(ssr.getPackage().getBusiness().addDisableLog(disableReason));
            ssr.disable(dl);
            return dl.getPkey();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Disables a <code>Username</code> and all uses of the username.
     *
     * @param  username  the username to disable
     * @param  disableReason  the reason the bind is being disabled
     *
     * @return  the pkey of the new <code>DisableLog</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Username</code>
     */
    public int disableUsername(
        String username,
        String disableReason
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "disableUsername(String,String)", null);
        try {
            Username un=getUsername(username);
            DisableLog dl=connector.disableLogs.get(un.getPackage().getBusiness().addDisableLog(disableReason));
            disableUsername(dl, un);
            return dl.getPkey();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
    private void disableUsername(DisableLog dl, Username un) {
        Profiler.startProfile(Profiler.UNKNOWN, SimpleAOClient.class, "disableUsername(DisableLog,Username)", null);
        try {
            LinuxAccount la=un.getLinuxAccount();
            if(la!=null && la.disable_log==-1) disableLinuxAccount(dl, la);

            MySQLUser mu=un.getMySQLUser();
            if(mu!=null && mu.disable_log==-1) disableMySQLUser(dl, mu);

            PostgresUser pu=un.getPostgresUser();
            if(pu!=null && pu.disable_log==-1) disablePostgresUser(dl, pu);

            un.disable(dl);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    /**
     * Disables a <code>LinuxAccount</code>.
     *
     * @param  username  the username to disable
     * @param  disableReason  the reason the account is being disabled
     *
     * @return  the pkey of the new <code>DisableLog</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Username</code> or <code>LinuxAccount</code>
     */
    public int disableLinuxAccount(
        String username,
        String disableReason
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "disableLinuxAccount(String,String)", null);
        try {
            LinuxAccount la=getLinuxAccount(username);
            DisableLog dl=connector.disableLogs.get(la.getUsername().getPackage().getBusiness().addDisableLog(disableReason));
            disableLinuxAccount(dl, la);
            return dl.getPkey();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
    private void disableLinuxAccount(DisableLog dl, LinuxAccount la) {
        Profiler.startProfile(Profiler.UNKNOWN, SimpleAOClient.class, "disableLinuxAccount(DisableLog,LinuxAccount)", null);
        try {
            for(LinuxServerAccount lsa : la.getLinuxServerAccounts()) {
                if(lsa.disable_log==-1) disableLinuxServerAccount(dl, lsa);
            }

            la.disable(dl);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    /**
     * Disables a <code>LinuxServerAccount</code>.
     *
     * @param  username  the username to disable
     * @param  aoServer  the server the account is on
     * @param  disableReason  the reason the account is being disabled
     *
     * @return  the pkey of the new <code>DisableLog</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Username</code>,
     *                                  <code>LinuxAccount</code>, or <code>LinuxServerAccount</code>
     */
    public int disableLinuxServerAccount(
        String username,
        String aoServer,
        String disableReason
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "disableLinuxServerAccount(String,String,String)", null);
        try {
            LinuxServerAccount lsa=getLinuxServerAccount(aoServer, username);
            DisableLog dl=connector.disableLogs.get(lsa.getLinuxAccount().getUsername().getPackage().getBusiness().addDisableLog(disableReason));
            disableLinuxServerAccount(dl, lsa);
            return dl.getPkey();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
    private void disableLinuxServerAccount(DisableLog dl, LinuxServerAccount lsa) {
        Profiler.startProfile(Profiler.UNKNOWN, SimpleAOClient.class, "disableLinuxServerAccount(DisableLog,LinuxServerAccount)", null);
        try {
            for(CvsRepository cr : lsa.getCvsRepositories()) if(cr.disable_log==-1) cr.disable(dl);
            lsa.disable(dl);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    /**
     * Disables a <code>CvsRepository</code>.
     *
     * @param  pkey  the pkey of the repository to disable
     * @param  disableReason  the reason the account is being disabled
     *
     * @return  the pkey of the new <code>DisableLog</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>CvsRepository</code>
     */
    public int disableCvsRepository(
        int pkey,
        String disableReason
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "disableCvsRepository(int,String)", null);
        try {
            CvsRepository cr=connector.cvsRepositories.get(pkey);
            if(cr==null) throw new IllegalArgumentException("Unable to find CvsRepository: "+pkey);
            DisableLog dl=connector
                .disableLogs
                .get(
                    cr
                    .getLinuxServerAccount()
                    .getLinuxAccount()
                    .getUsername()
                    .getPackage()
                    .getBusiness()
                    .addDisableLog(disableReason)
                )
            ;
            cr.disable(dl);
            return dl.getPkey();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Disables a <code>MySQLUser</code>.
     *
     * @param  username  the username to disable
     * @param  disableReason  the reason the account is being disabled
     *
     * @return  the pkey of the new <code>DisableLog</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Username</code> or <code>MySQLUser</code>
     */
    public int disableMySQLUser(
        String username,
        String disableReason
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "disableMySQLUser(String,String)", null);
        try {
            MySQLUser mu=getMySQLUser(username);
            DisableLog dl=connector.disableLogs.get(mu.getUsername().getPackage().getBusiness().addDisableLog(disableReason));
            disableMySQLUser(dl, mu);
            return dl.getPkey();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
    private void disableMySQLUser(DisableLog dl, MySQLUser mu) {
        Profiler.startProfile(Profiler.UNKNOWN, SimpleAOClient.class, "disableMySQLUser(DisableLog,MySQLUser)", null);
        try {
            for(MySQLServerUser msu : mu.getMySQLServerUsers()) if(msu.disable_log==-1) msu.disable(dl);
            mu.disable(dl);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    /**
     * Disables a <code>MySQLServerUser</code>.
     *
     * @param  username  the username to disable
     * @param  aoServer  the server the account is on
     * @param  disableReason  the reason the account is being disabled
     *
     * @return  the pkey of the new <code>DisableLog</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>MySQLUser</code>
     */
    public int disableMySQLServerUser(
        String username,
        String mysqlServer,
        String aoServer,
        String disableReason
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "disableMySQLServerUser(String,String,String,String)", null);
        try {
            MySQLServerUser msu=getMySQLServerUser(aoServer, mysqlServer, username);
            DisableLog dl=connector.disableLogs.get(msu.getMySQLUser().getUsername().getPackage().getBusiness().addDisableLog(disableReason));
            msu.disable(dl);
            return dl.getPkey();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Disables a <code>PostgresUser</code>.
     *
     * @param  username  the username to disable
     * @param  disableReason  the reason the account is being disabled
     *
     * @return  the pkey of the new <code>DisableLog</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Username</code> or <code>PostgresUser</code>
     */
    public int disablePostgresUser(
        String username,
        String disableReason
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "disablePostgresUser(String,String)", null);
        try {
            Username un=getUsername(username);
            PostgresUser pu=un.getPostgresUser();
            if(pu==null) throw new IllegalArgumentException("Unable to find PostgresUser: "+username);
            DisableLog dl=connector.disableLogs.get(un.getPackage().getBusiness().addDisableLog(disableReason));
            disablePostgresUser(dl, pu);
            return dl.getPkey();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
    private void disablePostgresUser(DisableLog dl, PostgresUser pu) {
        Profiler.startProfile(Profiler.UNKNOWN, SimpleAOClient.class, "disablePostgresUser(DisableLog,PostgresUser)", null);
        try {
            for(PostgresServerUser psu : pu.getPostgresServerUsers()) if(psu.disable_log==-1) psu.disable(dl);
            pu.disable(dl);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    /**
     * Disables a <code>PostgresServerUser</code>.
     *
     * @param  username  the username to disable
     * @param  postgresServer  the name of the PostgresServer
     * @param  aoServer  the server the account is on
     * @param  disableReason  the reason the account is being disabled
     *
     * @return  the pkey of the new <code>DisableLog</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>PostgresUser</code>
     */
    public int disablePostgresServerUser(
        String username,
        String postgresServer,
        String aoServer,
        String disableReason
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "disablePostgresServerUser(String,String,String,String)", null);
        try {
            PostgresServerUser psu=getPostgresServerUser(aoServer, postgresServer, username);
            DisableLog dl=connector
                .disableLogs
                .get(
                    psu
                    .getPostgresUser()
                    .getUsername()
                    .getPackage()
                    .getBusiness()
                    .addDisableLog(disableReason)
                )
            ;
            psu.disable(dl);
            return dl.getPkey();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Disables a <code>BusinessAdministrator</code>.
     *
     * @param  username  the username to disable
     * @param  disableReason  the reason the account is being disabled
     *
     * @return  the pkey of the new <code>DisableLog</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Username</code> or <code>BusinessAdministrator</code>
     */
    public int disableBusinessAdministrator(
        String username,
        String disableReason
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "disableBusinessAdministrator(String,String)", null);
        try {
            Username un=getUsername(username);
            BusinessAdministrator ba=un.getBusinessAdministrator();
            if(ba==null) throw new IllegalArgumentException("Unable to find BusinessAdministrator: "+username);
            DisableLog dl=connector.disableLogs.get(un.getPackage().getBusiness().addDisableLog(disableReason));
            ba.disable(dl);
            return dl.getPkey();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Enables a business, recursively enabling all of its disabled child components.
     *
     * @param  accounting  the accounting code to enable
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the necessary <code>Business</code>s
     */
    public void enableBusiness(String accounting) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.UNKNOWN, SimpleAOClient.class, "enableBusiness(String)", null);
        try {
            Business bu=getBusiness(accounting);
            DisableLog dl=bu.getDisableLog();
            if(dl==null) throw new IllegalArgumentException("Business not disabled: "+accounting);
            bu.enable();
            for(Package pk : bu.getPackages()) if(pk.disable_log==dl.pkey) enablePackage(dl, pk);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    /**
     * Enables a package, recursively enabling all of its disabled child components.
     *
     * @param  name  the name of the package
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the necessary <code>AOServObject</code>s
     */
    public void enablePackage(String name) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "enablePackage(String)", null);
        try {
            Package pk=getPackage(name);
            DisableLog dl=pk.getDisableLog();
            if(dl==null) throw new IllegalArgumentException("Package not disabled: "+name);
            enablePackage(dl, pk);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
    private void enablePackage(DisableLog dl, Package pk) {
        Profiler.startProfile(Profiler.UNKNOWN, SimpleAOClient.class, "enablePackage(DisableLog,Package)", null);
        try {
            pk.enable();

            /*
             * Email stuff
             */
            for(EmailList el : pk.getEmailLists()) if(el.disable_log==dl.pkey) el.enable();
            for(EmailPipe ep : pk.getEmailPipes()) if(ep.disable_log==dl.pkey) ep.enable();
            for(EmailSmtpRelay ssr : pk.getEmailSmtpRelays()) if(ssr.disable_log==dl.pkey) ssr.enable();

            // Various accounts
            List<AOServer> linuxAccountServers=new SortedArrayList<AOServer>();
            List<AOServer> mysqlServers=new SortedArrayList<AOServer>();
            List<AOServer> postgresServers=new SortedArrayList<AOServer>();
            for(Username un : pk.getUsernames()) {
                if(un.disable_log==dl.pkey) enableUsername(
                    dl,
                    un,
                    linuxAccountServers,
                    mysqlServers,
                    postgresServers
                );
            }

            // Wait for rebuilds
            for(int c=0;c<linuxAccountServers.size();c++) {
                linuxAccountServers.get(c).waitForLinuxAccountRebuild();
            }
            for(int c=0;c<mysqlServers.size();c++) {
                mysqlServers.get(c).waitForMySQLUserRebuild();
            }
            for(int c=0;c<postgresServers.size();c++) {
                postgresServers.get(c).waitForPostgresUserRebuild();
            }

            // Start up the web sites
            for(HttpdSharedTomcat hst : pk.getHttpdSharedTomcats()) if(hst.disable_log==dl.pkey) hst.enable();

            for(HttpdSite hs : pk.getHttpdSites()) if(hs.disable_log==dl.pkey) enableHttpdSite(dl, hs);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    /**
     * Enables a <code>HttpdSharedTomcat</code>.
     *
     * @param  name  the name of the tomcat JVM
     * @param  aoServer  the server that hosts the JVM
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>AOServer</code>, or <code>HttpdSharedTomcat</code>
     */
    public void enableHttpdSharedTomcat(
        String name,
        String aoServer
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "enableHttpdSharedTomcat(String,String)", null);
        try {
            HttpdSharedTomcat hst=getHttpdSharedTomcat(aoServer, name);
            DisableLog dl=hst.getDisableLog();
            if(dl==null) throw new IllegalArgumentException("HttpdSharedTomcat not disabled: "+name+" on "+aoServer);
            hst.enable();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Enables an <code>EmailPipe</code>.
     *
     * @param  pkey  the pkey of the pipe
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>EmailPipe</code>
     */
    public void enableEmailPipe(
        int pkey
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "enableEmailPipe(int)", null);
        try {
            EmailPipe ep=connector.emailPipes.get(pkey);
            if(ep==null) throw new IllegalArgumentException("Unable to find EmailPipe: "+pkey);
            DisableLog dl=ep.getDisableLog();
            if(dl==null) throw new IllegalArgumentException("EmailPipe not disabled: "+pkey);
            ep.enable();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Enables a <code>HttpdSite</code>.
     *
     * @param  name  the name of the site
     * @param  aoServer  the server that hosts the site
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>AOServer</code>, or <code>HttpdSite</code>
     */
    public void enableHttpdSite(
        String name,
        String aoServer
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "enableHttpdSite(String,String)", null);
        try {
            HttpdSite hs=getHttpdSite(aoServer, name);
            DisableLog dl=hs.getDisableLog();
            if(dl==null) throw new IllegalArgumentException("HttpdSite not disabled: "+name+" on "+aoServer);
            enableHttpdSite(dl, hs);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
    private void enableHttpdSite(DisableLog dl, HttpdSite hs) {
        Profiler.startProfile(Profiler.UNKNOWN, SimpleAOClient.class, "enableHttpdSite(DisableLog,HttpdSite)", null);
        try {
            hs.enable();
            for(HttpdSiteBind hsb : hs.getHttpdSiteBinds()) if(hsb.disable_log==dl.pkey) hsb.enable();
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    /**
     * Enables a <code>HttpdSiteBind</code>.
     *
     * @param  pkey  the pkey of the bind
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>HttpdSiteBind</code>
     */
    public void enableHttpdSiteBind(
        int pkey
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "enableHttpdSiteBind(int)", null);
        try {
            HttpdSiteBind hsb=connector.httpdSiteBinds.get(pkey);
            if(hsb==null) throw new IllegalArgumentException("Unable to find HttpdSiteBind: "+pkey);
            DisableLog dl=hsb.getDisableLog();
            if(dl==null) throw new IllegalArgumentException("HttpdSiteBind not disabled: "+pkey);
            hsb.enable();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Enables an <code>EmailList</code>.
     *
     * @param  path  the path of the list
     * @param  aoServer  the server the list is part of
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>EmailList</code>
     */
    public void enableEmailList(
        String path,
        String aoServer
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "enableEmailList(String,String)", null);
        try {
            EmailList el=getEmailList(aoServer, path);
            DisableLog dl=el.getDisableLog();
            if(dl==null) throw new IllegalArgumentException("EmailList not disabled: "+path+" on "+aoServer);
            el.enable();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Enables a <code>EmailSmtpRelay</code>.
     *
     * @param  pkey  the pkey of the relay
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>EmailSmtpRelay</code>
     */
    public void enableEmailSmtpRelay(
        int pkey
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "enableEmailSmtpRelay(int)", null);
        try {
            EmailSmtpRelay ssr=connector.emailSmtpRelays.get(pkey);
            if(ssr==null) throw new IllegalArgumentException("Unable to find EmailSmtpRelay: "+pkey);
            DisableLog dl=ssr.getDisableLog();
            if(dl==null) throw new IllegalArgumentException("EmailSmtpRelay not disabled: "+pkey);
            ssr.enable();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Enables a <code>Username</code> and all uses of the username.
     *
     * @param  username  the username to enable
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Username</code>
     */
    public void enableUsername(
        String username
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "enableUsername(String)", null);
        try {
            Username un=getUsername(username);
            DisableLog dl=un.getDisableLog();
            if(dl==null) throw new IllegalArgumentException("Username not disabled: "+username);
            enableUsername(dl, un, null, null, null);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
    private void enableUsername(
        DisableLog dl,
        Username un,
        List<AOServer> linuxAccountServers,
        List<AOServer> mysqlServers,
        List<AOServer> postgresServers
    ) {
        Profiler.startProfile(Profiler.UNKNOWN, SimpleAOClient.class, "enableUsername(DisableLog,Username,List<AOServer>,List<AOServer>,List<AOServer>)", null);
        try {
            un.enable();

            BusinessAdministrator ba=un.getBusinessAdministrator();
            if(ba!=null && ba.disable_log==dl.pkey) ba.enable();

            LinuxAccount la=un.getLinuxAccount();
            if(la!=null && la.disable_log==dl.pkey) enableLinuxAccount(dl, la, linuxAccountServers);

            MySQLUser mu=un.getMySQLUser();
            if(mu!=null && mu.disable_log==dl.pkey) enableMySQLUser(dl, mu, mysqlServers);

            PostgresUser pu=un.getPostgresUser();
            if(pu!=null && pu.disable_log==dl.pkey) enablePostgresUser(dl, pu, postgresServers);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    /**
     * Enables a <code>LinuxAccount</code>.
     *
     * @param  username  the username to enable
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Username</code> or <code>LinuxAccount</code>
     */
    public void enableLinuxAccount(
        String username
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "enableLinuxAccount(String)", null);
        try {
            LinuxAccount la=getLinuxAccount(username);
            DisableLog dl=la.getDisableLog();
            if(dl==null) throw new IllegalArgumentException("LinuxAccount not disabled: "+username);
            enableLinuxAccount(dl, la, null);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
    private void enableLinuxAccount(DisableLog dl, LinuxAccount la, List<AOServer> linuxAccountServers) {
        Profiler.startProfile(Profiler.UNKNOWN, SimpleAOClient.class, "enableLinuxAccount(DisableLog,LinuxAccount,List<AOServer>)", null);
        try {
            la.enable();

            for(LinuxServerAccount lsa : la.getLinuxServerAccounts()) {
                if(lsa.disable_log==dl.pkey) {
                    enableLinuxServerAccount(dl, lsa);
                    if(linuxAccountServers!=null) {
                        AOServer ao=lsa.getAOServer();
                        if(!linuxAccountServers.contains(ao)) linuxAccountServers.add(ao);
                    }
                }
            }
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    /**
     * Enables a <code>LinuxServerAccount</code>.
     *
     * @param  username  the username to enable
     * @param  aoServer  the server the account is on
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Username</code>,
     *                                  <code>LinuxAccount</code>, or <code>LinuxServerAccount</code>
     */
    public void enableLinuxServerAccount(
        String username,
        String aoServer
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "enableLinuxServerAccount(String,String)", null);
        try {
            LinuxServerAccount lsa=getLinuxServerAccount(aoServer, username);
            DisableLog dl=lsa.getDisableLog();
            if(dl==null) throw new IllegalArgumentException("LinuxServerAccount not disabled: "+username+" on "+aoServer);
            enableLinuxServerAccount(dl, lsa);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
    private void enableLinuxServerAccount(DisableLog dl, LinuxServerAccount lsa) {
        Profiler.startProfile(Profiler.UNKNOWN, SimpleAOClient.class, "enableLinuxServerAccount(DisableLog,LinuxServerAccount)", null);
        try {
            lsa.enable();
            for(CvsRepository cr : lsa.getCvsRepositories()) if(cr.disable_log==dl.pkey) cr.enable();
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    /**
     * Enables a <code>CvsRepository</code>.
     *
     * @param  pkey  the pkey of the repository to enable
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>CvsRepository</code>
     */
    public void enableCvsRepository(
        int pkey
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "enableCvsRepository(int)", null);
        try {
            CvsRepository cr=connector.cvsRepositories.get(pkey);
            if(cr==null) throw new IllegalArgumentException("Unable to find CvsRepository: "+pkey);
            DisableLog dl=cr.getDisableLog();
            if(dl==null) throw new IllegalArgumentException("CvsRepository not disabled: "+pkey);
            cr.enable();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Enables a <code>MySQLUser</code>.
     *
     * @param  username  the username to enable
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Username</code> or <code>MySQLUser</code>
     */
    public void enableMySQLUser(
        String username
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "enableMySQLUser(String)", null);
        try {
            MySQLUser mu=getMySQLUser(username);
            DisableLog dl=mu.getDisableLog();
            if(dl==null) throw new IllegalArgumentException("MySQLUser not disabled: "+username);
            enableMySQLUser(dl, mu, null);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
    private void enableMySQLUser(DisableLog dl, MySQLUser mu, List<AOServer> mysqlServers) {
        Profiler.startProfile(Profiler.UNKNOWN, SimpleAOClient.class, "enableMySQLUser(DisableLog,MySQLUser,List<AOServer>)", null);
        try {
            mu.enable();
            for(MySQLServerUser msu : mu.getMySQLServerUsers()) {
                if(msu.disable_log==dl.pkey) {
                    msu.enable();
                    if(mysqlServers!=null) {
                        AOServer ao=msu.getMySQLServer().getAOServer();
                        if(!mysqlServers.contains(ao)) mysqlServers.add(ao);
                    }
                }
            }
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    /**
     * Enables a <code>MySQLServerUser</code>.
     *
     * @param  username  the username to enable
     * @param  aoServer  the server the account is on
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>MySQLUser</code>
     */
    public void enableMySQLServerUser(
        String username,
        String mysqlServer,
        String aoServer
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "enableMySQLServerUser(String,String,String)", null);
        try {
            MySQLServerUser msu=getMySQLServerUser(aoServer, mysqlServer, username);
            DisableLog dl=msu.getDisableLog();
            if(dl==null) throw new IllegalArgumentException("MySQLServerUser not disabled: "+username+" on "+mysqlServer+" on "+aoServer);
            msu.enable();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Enables a <code>PostgresUser</code>.
     *
     * @param  username  the username to enable
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Username</code> or <code>PostgresUser</code>
     */
    public void enablePostgresUser(
        String username
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "enablePostgresUser(String)", null);
        try {
            Username un=getUsername(username);
            PostgresUser pu=un.getPostgresUser();
            if(pu==null) throw new IllegalArgumentException("Unable to find PostgresUser: "+username);
            DisableLog dl=pu.getDisableLog();
            if(dl==null) throw new IllegalArgumentException("PostgresUser not disabled: "+username);
            enablePostgresUser(dl, pu, null);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
    private void enablePostgresUser(DisableLog dl, PostgresUser pu, List<AOServer> postgresServers) {
        Profiler.startProfile(Profiler.UNKNOWN, SimpleAOClient.class, "enablePostgresUser(DisableLog,PostgresUser,List<AOServer>)", null);
        try {
            pu.enable();

            for(PostgresServerUser psu : pu.getPostgresServerUsers()) {
                if(psu.disable_log==dl.pkey) {
                    psu.enable();
                    if(postgresServers!=null) {
                        AOServer ao=psu.getPostgresServer().getAOServer();
                        if(!postgresServers.contains(ao)) postgresServers.add(ao);
                    }
                }
            }
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    /**
     * Enables a <code>PostgresServerUser</code>.
     *
     * @param  username  the username to enable
     * @param  postgresServer  the name of the PostgreSQL server
     * @param  aoServer  the server the account is on
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>PostgresUser</code>
     */
    public void enablePostgresServerUser(
        String username,
        String postgresServer,
        String aoServer
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "enablePostgresServerUser(String,String,String)", null);
        try {
            PostgresServerUser psu=getPostgresServerUser(aoServer, postgresServer, username);
            DisableLog dl=psu.getDisableLog();
            if(dl==null) throw new IllegalArgumentException("PostgresServerUser not disabled: "+username+" on "+aoServer);
            psu.enable();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Enables a <code>BusinessAdministrator</code>.
     *
     * @param  username  the username to enable
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Username</code> or <code>BusinessAdministrator</code>
     */
    public void enableBusinessAdministrator(
        String username
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "enableBusinessAdministrator(String)", null);
        try {
            Username un=getUsername(username);
            BusinessAdministrator ba=un.getBusinessAdministrator();
            if(ba==null) throw new IllegalArgumentException("Unable to find BusinessAdministrator: "+username);
            DisableLog dl=ba.getDisableLog();
            if(dl==null) throw new IllegalArgumentException("BusinessAdministrator not disabled: "+username);
            ba.enable();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Dumps the contents of a <code>MySQLDatabase</code> to a <code>Writer</code>.
     *
     * @param  name  the name of the <code>MySQLDatabase</code>
     * @param  aoServer  the hostname of the <code>AOServer</code>
     * @param  out  the <code>Writer</code> to dump to
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or
     *					<code>MySQLDatabase</code>
     *
     * @see  MySQLDatabase#dump
     * @see  MySQLDatabase
     * @see  #backupMySQLDatabase
     */
    public void dumpMySQLDatabase(
        String name,
        String mysqlServer,
        String aoServer,
        Writer out
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "dumpMySQLDatabase(String,String,String,Writer)", null);
        try {
            getMySQLDatabase(aoServer, mysqlServer, name).dump(out);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Dumps the contents of a <code>PostgresDatabase</code> to a <code>Writer</code>.
     *
     * @param  name  the name of the <code>PostgresDatabase</code>
     * @param  postgresServer  the name of the PostgreSQL server
     * @param  aoServer  the hostname of the <code>Server</code>
     * @param  out  the <code>Writer</code> to dump to
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or
     *					<code>PostgresDatabase</code>
     *
     * @see  PostgresDatabase#dump
     * @see  PostgresDatabase
     * @see  #backupPostgresDatabase
     */
    public void dumpPostgresDatabase(
        String name,
        String postgresServer,
        String aoServer,
        Writer out
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "dumpPostgresDatabase(String,String,String,Writer)", null);
        try {
            getPostgresDatabase(aoServer, postgresServer, name).dump(out);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Generates a unique accounting code that may be used to create a new <code>Business</code>.
     *
     * @param  accountingTemplate  the beginning part of the accounting code, such as <code>"AO_"</code>
     *
     * @return  the available accounting code
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     *
     * @see  BusinessTable#generateAccountingCode
     * @see  #addBusiness
     * @see  Business
     */
    public String generateAccountingCode(
        String accountingTemplate
    ) {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "generateAccountingCode(String)", null);
        try {
            return connector.businesses.generateAccountingCode(accountingTemplate);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Generates a unique MySQL database name.
     *
     * @param  template_base  the beginning part of the template, such as <code>"AO"</code>
     * @param  template_added  the part of the template added between the <code>template_base</code> and
     *					the generated number, such as <code>"_"</code>
     *
     * @return  the available database name
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     *
     * @see  MySQLDatabaseTable#generateMySQLDatabaseName
     * @see  #addMySQLDatabase
     * @see  MySQLDatabase
     */
    public String generateMySQLDatabaseName(
        String template_base,
        String template_added
    ) {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "generateMySQLDatabaseName(String,String)", null);
        try {
            return connector.mysqlDatabases.generateMySQLDatabaseName(template_base, template_added);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Generates a unique <code>Package</code> name.
     *
     * @param  template  the beginning part of the template, such as <code>"AO_"</code>
     *
     * @return  the available <code>Package</code> name
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     *
     * @see  PackageTable#generatePackageName
     * @see  #addPackage
     * @see  Package
     */
    public String generatePackageName(
        String template
    ) {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "generatePackageName(String)", null);
        try {
            return connector.packages.generatePackageName(template);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Generates a random, valid password.
     *
     * @return  the password
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     *
     */
    public String generatePassword() {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "generatePassword()", null);
        try {
            return connector.linuxAccounts.generatePassword();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Generates a unique PostgreSQL database name.
     *
     * @param  template_base  the beginning part of the template, such as <code>"AO"</code>
     * @param  template_added  the part of the template added between the <code>template_base</code> and
     *					the generated number, such as <code>"_"</code>
     *
     * @return  the available database name
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     *
     * @see  PostgresDatabaseTable#generatePostgresDatabaseName
     * @see  #addPostgresDatabase
     * @see  PostgresDatabase
     */
    public String generatePostgresDatabaseName(
        String template_base,
        String template_added
    ) {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "generatePostgresDatabaseName(String,String)", null);
        try {
            return connector.postgresDatabases.generatePostgresDatabaseName(template_base, template_added);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Generates a unique <code>HttpdSharedTomcat</code> name.
     *
     * @param  template  the beginning part of the template, such as <code>"ao"</code>
     *
     * @return  the available <code>HttpdSharedTomcat</code> name
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     *
     * @see  HttpdSharedTomcatTable#generateSharedTomcatName
     * @see  #addHttpdSharedTomcat
     * @see  #addHttpdTomcatSharedSite
     * @see  HttpdSite
     */
    public String generateSharedTomcatName(
        String template
    ) {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "generateSharedTomcatName(String)", null);
        try {
            return connector.httpdSharedTomcats.generateSharedTomcatName(template);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Generates a unique <code>HttpdSite</code> name.
     *
     * @param  template  the beginning part of the template, such as <code>"ao"</code>
     *
     * @return  the available <code>HttpdSite</code> name
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     *
     * @see  HttpdSiteTable#generateSiteName
     * @see  #addHttpdTomcatStdSite
     * @see  HttpdSite
     */
    public String generateSiteName(
        String template
    ) {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "generateSiteName(String)", null);
        try {
            return connector.httpdSites.generateSiteName(template);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Gets the autoresponder content.
     *
     * @param  username  the username of the <code>LinuxServerAccount</code>
     * @param  aoServer  the server to get the data from
     *
     * @return  the autoresponder content
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     * @exception  IllegalArgumentException  if unable to find the source <code>LinuxServerAccount</code>
     *
     * @see  LinuxServerAccount#getAutoresponderContent
     * @see  #setAutoresponder
     */
    public String getAutoresponderContent(
        String username,
        String aoServer
    ) {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "getAutoresponderContent(String,String)", null);
        try {
            return getLinuxServerAccount(aoServer, username).getAutoresponderContent();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Gets the <code>AOServConnector</code> used for communication with the server.
     */
    public AOServConnector getConnector() {
        return connector;
    }

    /**
     * Gets a user's cron table on one server.
     *
     * @param  username  the username of the <code>LinuxAccount</code>
     * @param  aoServer  the server to get the data from
     *
     * @return  the cron table
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     * @exception  IllegalArgumentException  if unable to find the source <code>LinuxServerAccount</code>
     *
     * @see  LinuxServerAccount#getCronTable
     * @see  #setCronTable
     * @see  #addLinuxServerAccount
     * @see  #removeLinuxServerAccount
     */
    public String getCronTable(
        String username,
        String aoServer
    ) {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "getCronTable(String,String)", null);
        try {
            return getLinuxServerAccount(aoServer, username).getCronTable();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Gets the list of email addresses that an <code>EmailList</code> will be forwarded to.
     *
     * @param  path  the path of the list
     * @param  aoServer  the server this list is part of
     *
     * @return  the list of addresses, one address per line separated by <code>'\n'</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     * @exception  IllegalArgumentException  if unable to find the <code>EmailList</code>
     *
     * @see  EmailList#getAddressList
     * @see  #addEmailList
     * @see  #setEmailListAddressList
     * @see  EmailList
     */
    public String getEmailListAddressList(
        String path,
        String aoServer
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "getEmailListAddressList(String,String)", null);
        try {
            return getEmailList(aoServer, path).getAddressList();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Gets the total size of a <code>BackupPartition</code>.
     *
     * @param  aoServer  the hostname of the server
     * @param  path  the path of the <code>BackupPartition</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     * @exception  IllegalArgumentException  if unable to find the <code>AOServer</code> or <cdoe>BackupPartition</code>
     *
     * @see  BackupPartition#getDiskTotalSize
     */
    public long getBackupPartitionTotalSize(
        String aoServer,
        String path
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "getBackupPartitionTotalSize(String,String)", null);
        try {
            BackupPartition bp=getAOServer(aoServer).getBackupPartitionForPath(path);
            if(bp==null) throw new IllegalArgumentException("Unable to find BackupPartition: "+path+" on "+aoServer);
            return bp.getDiskTotalSize();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Gets the used size of a <code>BackupPartition</code>.
     *
     * @param  aoServer  the hostname of the server
     * @param  path  the path of the <code>BackupPartition</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     * @exception  IllegalArgumentException  if unable to find the <code>AOServer</code> or <cdoe>BackupPartition</code>
     *
     * @see  BackupPartition#getDiskUsedSize
     */
    public long getBackupPartitionUsedSize(
        String aoServer,
        String path
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "getBackupPartitionUsedSize(String,String)", null);
        try {
            BackupPartition bp=getAOServer(aoServer).getBackupPartitionForPath(path);
            if(bp==null) throw new IllegalArgumentException("Unable to find BackupPartition: "+path+" on "+aoServer);
            return bp.getDiskUsedSize();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Gets the attributes of an inbox.
     *
     * @param  username  the username of the <code>LinuxServerAccount</code>
     * @param  aoServer    the server hosting the account
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     * @exception  IllegalArgumentException  if unable to find the <cdoe>LinuxServerAccount</code>
     *
     * @see  LinuxServerAccount#getInboxAttributes
     */
    public InboxAttributes getInboxAttributes(
        String username,
        String aoServer
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "getInboxAttributes(String,String)", null);
        try {
            return getLinuxServerAccount(aoServer, username).getInboxAttributes();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Gets the IMAP folder sizes for an  inbox.
     *
     * @param  username  the username of the <code>LinuxServerAccount</code>
     * @param  aoServer    the server hosting the account
     * @param  folderNames  the folder names
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     * @exception  IllegalArgumentException  if unable to find the <cdoe>LinuxServerAccount</code>
     *
     * @see  LinuxServerAccount#getImapFolderSizes
     */
    public long[] getImapFolderSizes(
        String username,
        String aoServer,
        String[] folderNames
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "getImapFolderSizes(String,String,String[])", null);
        try {
            return getLinuxServerAccount(aoServer, username).getImapFolderSizes(folderNames);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Gets the info file for a <code>MajordomoList</code>.
     *
     * @param  domain  the domain of the <code>MajordomoServer</code>
     * @param  aoServer  the hostname of the <code>AOServer</code>
     * @param  listName  the name of the new list
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data
     *					integrity violation occurs
     * @exception  IllegalArgumentException  if the name is not valid or unable to find the
     *                                  <code>Server</code>, code>EmailDomain</code>,
     *                                  <code>MajordomoServer</code>, or <code>MajordomoList</code>
     *
     * @see  MajordomoList#getInfoFile
     * @see  #addMajordomoList
     * @see  #removeEmailList
     */
    public String getMajordomoInfoFile(
        String domain,
        String aoServer,
        String listName
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "getMajordomoInfoFile(String,String,String)", null);
        try {
            EmailDomain ed=getEmailDomain(aoServer, domain);
            MajordomoServer ms=ed.getMajordomoServer();
            if(ms==null) throw new IllegalArgumentException("Unable to find MajordomoServer: "+domain+" on "+aoServer);
            MajordomoList ml=ms.getMajordomoList(listName);
            if(ml==null) throw new IllegalArgumentException("Unable to find MajordomoList: "+listName+'@'+domain+" on "+aoServer);
            return ml.getInfoFile();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Gets the intro file for a <code>MajordomoList</code>.
     *
     * @param  domain  the domain of the <code>MajordomoServer</code>
     * @param  aoServer  the hostname of the <code>AOServer</code>
     * @param  listName  the name of the new list
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data
     *					integrity violation occurs
     * @exception  IllegalArgumentException  if the name is not valid or unable to find the
     *                                  <code>Server</code>, code>EmailDomain</code>,
     *                                  <code>MajordomoServer</code>, or <code>MajordomoList</code>
     *
     * @see  MajordomoList#getIntroFile
     * @see  #addMajordomoList
     * @see  #removeEmailList
     */
    public String getMajordomoIntroFile(
        String domain,
        String aoServer,
        String listName
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "getMajordomoIntroFile(String,String,String)", null);
        try {
            EmailDomain ed=getEmailDomain(aoServer, domain);
            MajordomoServer ms=ed.getMajordomoServer();
            if(ms==null) throw new IllegalArgumentException("Unable to find MajordomoServer: "+domain+" on "+aoServer);
            MajordomoList ml=ms.getMajordomoList(listName);
            if(ml==null) throw new IllegalArgumentException("Unable to find MajordomoList: "+listName+'@'+domain+" on "+aoServer);
            return ml.getIntroFile();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Gets the contents of a MRTG file.
     *
     * @param  aoServer  the hostname of the server to get the file from
     * @param  filename  the filename on the server
     * @param  out  the <code>OutputStream</code> to write the file to
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     * @exception  IllegalArgumentException  if unable to find the <cdoe>Server</code> or <code>AOServer</code>
     *
     * @see  AOServer#getMrtgFile
     */
    public void getMrtgFile(
        String aoServer,
        String filename,
        OutputStream out
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "getMrtgFile(String,String,OutputStream)", null);
        try {
            getAOServer(aoServer).getMrtgFile(filename, out);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Gets the contents of an AWStats file.
     *
     * @param  siteName  the site name
     * @param  aoServer  the hostname of the server to get the file from
     * @param  path  the filename on the server
     * @param  queryString  the query string for the request
     * @param  out  the <code>OutputStream</code> to write the file to
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     * @exception  IllegalArgumentException  if unable to find the <cdoe>Server</code>, <code>AOServer</code>, or <code>HttpdSite</code>
     *
     * @see  HttpdSite#getAWStatsFile
     */
    public void getAWStatsFile(
        String siteName,
        String aoServer,
        String path,
        String queryString,
        OutputStream out
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "getAWStatsFile(String,String,String,String,OutputStream)", null);
        try {
            getHttpdSite(aoServer, siteName).getAWStatsFile(path, queryString, out);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Gets the name of the root <code>Business</code> in the tree of <code>Business</code>es.
     *
     * @return  the accounting code of the root <code>Business</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     *
     * @see  BusinessTable#getRootAccounting
     */
    public String getRootBusiness() {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "getRootBusiness()", null);
        try {
            return connector.businesses.getRootAccounting();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Places a <code>Ticket</code> in the hold state.  When in a hold state, a <code>Ticket</code>
     * is not being worked on because the support personnel are waiting for something out of their
     * immediate control.
     *
     * @param  ticket_id  the pkey of the <code>Ticket</code>
     * @param  comments  the details of the change
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Ticket</code>,
     *					<code>TicketType</code>, or <code>BusinessAdministrator</code>
     *
     * @see  Ticket#actHoldTicket
     * @see  #addTicket
     * @see  Action
     */
    public void holdTicket(
        int ticket_id,
        String comments
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "holdTicket(int,String)", null);
        try {
            Ticket ti=connector.tickets.get(ticket_id);
            if(ti==null) throw new IllegalArgumentException("Unable to find Ticket: "+ticket_id);
            ti.actHoldTicket(comments);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Initializes the password files for an <code>HttpdSite</code>.  These files are
     * typically contained in <code>/www/<i>sitename</i>/conf/passwd</code> and
     * <code>/www/<i>sitename</i>/conf/group</code>.
     *
     * @param  siteName  the name of the site to initialize
     * @param  aoServer  the hostname of the <code>AOServer</code>
     * @param  username  the username granted access to the site
     * @param  password  the password for that username
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>AOServer</code> or
     *					<code>HttpdSite</code>
     *
     * @see  HttpdSite#initializePasswdFile
     * @see  #addHttpdTomcatStdSite
     */
    /*
    public void initializeHttpdSitePasswdFile(
        String siteName,
        String aoServer,
        String username,
        String password
    ) {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "initializeHttpdSitePasswdFile(String,String,String,String)", null);
        try {
            getHttpdSite(aoServer, siteName).initializePasswdFile(username, password);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
     */

    private static final int numTables = SchemaTable.TableID.values().length;

    /**
     * Invalidates a table, causing all caches of the table to be removed and all configurations
     * based on the table to be reevaluated.
     *
     * @param  tableID  the ID of the <code>AOServTable</code> to invalidate
     * @param  server  the server that should be invalidated or <code>null or ""</code> for none, accepts ao_servers.hostname, servers.package||'/'||servers.name, or servers.pkey
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if the table ID is invalid
     *
     * @see  AOServConnector#invalidateTable
     * @see  BusinessAdministrator#isActiveTableInvalidator
     */
    public void invalidate(
        int tableID,
        String server
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "invalidate(int,String)", null);
        try {
            if(tableID<0 || tableID>=numTables) throw new IllegalArgumentException("Invalid table ID: "+tableID);
            Server se;
            if(server!=null && server.length()==0) server=null;
            if(server==null) se=null;
            else {
                se = connector.servers.get(server);
                if(se==null) throw new IllegalArgumentException("Unable to find Server: "+server);
            }
            connector.invalidateTable(tableID, se==null ? -1 : se.pkey);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Determines if an accounting code is available.
     *
     * @param  accounting  the accounting code
     *
     * @return  <code>true</code> if the accounting code is available
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     * @exception  IllegalArgumentException  if the accounting code is invalid
     *
     * @see  BusinessTable#isAccountingAvailable
     * @see  #checkAccounting
     * @see  #addBusiness
     * @see  #generateAccountingCode
     * @see  Business
     */
    public boolean isAccountingAvailable(
        String accounting
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "isAccountingAvailable(String)", null);
        try {
            checkAccounting(accounting);
            return connector.businesses.isAccountingAvailable(accounting);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Determines if a <code>BusinessAdministrator</code> currently has a password set.
     *
     * @param  username  the username of the administrator
     *
     * @return  if the <code>BusinessAdministrator</code> has a password set
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     * @exception  IllegalArgumentException  if the <code>BusinessAdministrator</code> is not found
     *
     * @see  BusinessAdministrator#arePasswordsSet
     * @see  #setBusinessAdministratorPassword
     * @see  BusinessAdministrator
     */
    public boolean isBusinessAdministratorPasswordSet(
        String username
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "isBusinessAdministratorPasswordSet(String)", null);
        try {
            BusinessAdministrator ba=connector.businessAdministrators.get(username);
            if(ba==null) throw new IllegalArgumentException("Unable to find BusinessAdministrator: "+username);
            return ba.arePasswordsSet()==PasswordProtected.ALL;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Determines if a <code>DNSZone</code> is available.
     *
     * @param  zone  the zone in <code>domain.tld.</code> format
     *
     * @return  <code>true</code> if the <code>DNSZone</code> is available
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     * @exception  IllegalArgumentException  if the <code>DNSZone</code> is invalid
     *
     * @see  DNSZoneTable#isDNSZoneAvailable
     * @see  #addDNSZone
     * @see  DNSZone
     */
    public boolean isDNSZoneAvailable(
        String zone
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "isDNSZoneAvailable(String)", null);
        try {
            return connector.dnsZones.isDNSZoneAvailable(zone);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Determines if an <code>IPAddress</code> is currently being used.
     *
     * @param  ipAddress  the IP address
     *
     * @return  <code>true</code> if the <code>IPAddress</code> is in use
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     * @exception  IllegalArgumentException  if unable to find the <code>IPAddress</code>
     *
     * @see  IPAddress#isUsed
     * @see  #setIPAddressPackage
     */
    public boolean isIPAddressUsed(
        String ipAddress,
        String aoServer,
        String net_device
    ) throws IllegalArgumentException {
        return getIPAddress(aoServer, net_device, ipAddress).isUsed();
    }

    /**
     * Determines if a groupname is available.
     *
     * @param  groupname  the groupname
     *
     * @return  <code>true</code> if the groupname is available
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     * @exception  IllegalArgumentException  if the groupname is invalid
     *
     * @see  LinuxGroupTable#isLinuxGroupNameAvailable
     * @see  #addLinuxGroup
     * @see  LinuxGroup
     */
    public boolean isLinuxGroupNameAvailable(
        String groupname
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "isLinuxGroupNameAvailable(String)", null);
        try {
            checkLinuxGroupname(groupname);
            return connector.linuxGroups.isLinuxGroupNameAvailable(groupname);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Determines if a <code>LinuxServerAccount</code> currently has a password set.
     *
     * @param  username  the username of the account
     * @param  aoServer  the server the account is hosted on
     *
     * @return  if the <code>LinuxServerAccount/code> has a password set
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     * @exception  IllegalArgumentException  if the <code>LinuxServerAccount</code> is not found
     *
     * @see  LinuxServerAccount#arePasswordsSet
     * @see  #setLinuxServerAccountPassword
     * @see  LinuxServerAccount
     */
    public boolean isLinuxServerAccountPasswordSet(
        String username,
        String aoServer
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "isLinuxServerAccountPasswordSet(String,String)", null);
        try {
            return getLinuxServerAccount(aoServer, username).arePasswordsSet()==PasswordProtected.ALL;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Determines if a <code>LinuxServerAccount</code> is currently in manual procmail mode.  Manual
     * procmail mode is initiated when the header comment in the .procmailrc file is altered or removed.
     *
     * @param  username  the username of the account
     * @param  aoServer  the server the account is hosted on
     *
     * @return  if the <code>LinuxServerAccount/code> is in manual mode
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     * @exception  IllegalArgumentException  if the <code>LinuxServerAccount</code> is not found
     *
     * @see  LinuxServerAccount#isProcmailManual
     * @see  LinuxServerAccount
     */
    public int isLinuxServerAccountProcmailManual(
        String username,
        String aoServer
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "isLinuxServerAccountProcmailManual(String,String)", null);
        try {
            return getLinuxServerAccount(aoServer, username).isProcmailManual();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Determines if a <code>MySQLDatabase</code> name is available on the specified
     * <code>Server</code>.
     *
     * @param  name  the name of the database
     * @param  aoServer  the hostname of the <code>Server</code>
     *
     * @return  <code>true</code> if the <code>MySQLDatabase</code> is available
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     * @exception  IllegalArgumentException  if the database name is invalid or unable
     *					to find the <code>Server</code>
     *
     * @see  MySQLServer#isMySQLDatabaseNameAvailable
     * @see  #checkMySQLDatabaseName
     */
    public boolean isMySQLDatabaseNameAvailable(
        String name,
        String mysqlServer,
        String aoServer
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "isMySQLDatabaseNameAvailable(String,String,String)", null);
        try {
            checkMySQLDatabaseName(name);
            return getMySQLServer(aoServer, mysqlServer).isMySQLDatabaseNameAvailable(name);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Determines if a <code>MySQLServer</code> name is available on the specified
     * <code>Server</code>.
     *
     * @param  name  the name of the MySQL server
     * @param  aoServer  the hostname of the <code>Server</code>
     *
     * @return  <code>true</code> if the <code>MySQLServer</code> is available
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     * @exception  IllegalArgumentException  if the server name is invalid or unable
     *					to find the <code>Server</code>
     *
     * @see  AOServer#isMySQLServerNameAvailable
     * @see  #checkMySQLServerName
     */
    public boolean isMySQLServerNameAvailable(
        String name,
        String aoServer
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "isMySQLServerNameAvailable(String,String)", null);
        try {
            checkMySQLServerName(name);
            return getAOServer(aoServer).isMySQLServerNameAvailable(name);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Determines if a <code>MySQLServerUser</code> currently has a password set.
     *
     * @param  username  the username of the account
     * @param  aoServer  the server the account is hosted on
     *
     * @return  if the <code>MySQLServerUser</code> has a password set
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     * @exception  IllegalArgumentException  if the <code>MySQLServerUser</code> is not found
     *
     * @see  MySQLServerUser#arePasswordsSet
     * @see  #setMySQLServerUserPassword
     * @see  MySQLServerUser
     */
    public boolean isMySQLServerUserPasswordSet(
        String username,
        String mysqlServer,
        String aoServer
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "isMySQLServerUserPasswordSet(String,String,String)", null);
        try {
            return getMySQLServerUser(aoServer, mysqlServer, username).arePasswordsSet()==PasswordProtected.ALL;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Determines if a <code>Package</code> name is available.
     *
     * @param  packageName  the name of the <code>Package</code>
     *
     * @return  <code>true</code> if the <code>Package</code> name is available
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     * @exception  IllegalArgumentException  if the <code>Package</code> name is invalid
     *
     * @see  PackageTable#isPackageNameAvailable
     * @see  #generatePackageName
     * @see  #addPackage
     * @see  Package
     */
    public boolean isPackageNameAvailable(
        String packageName
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "isPackageNameAvailable(String)", null);
        try {
            checkPackageName(packageName);
            return connector.packages.isPackageNameAvailable(packageName);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Determines if a <code>PostgresDatabase</code> name is available on the specified
     * <code>Server</code>.
     *
     * @param  name  the name of the database
     * @param  postgresServer  the name of the PostgreSQL server
     * @param  aoServer  the hostname of the <code>Server</code>
     *
     * @return  <code>true</code> if the <code>PostgresDatabase</code> is available
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     * @exception  IllegalArgumentException  if the database name is invalid or unable
     *					to find the <code>Server</code>
     *
     * @see  PostgresServer#isPostgresDatabaseNameAvailable
     * @see  #checkPostgresDatabaseName
     */
    public boolean isPostgresDatabaseNameAvailable(
        String name,
        String postgresServer,
        String aoServer
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "isPostgresDatabaseNameAvailable(String,String,String)", null);
        try {
            checkPostgresDatabaseName(name);
            return getPostgresServer(aoServer, postgresServer).isPostgresDatabaseNameAvailable(name);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Determines if a <code>PostgresServer</code> name is available on the specified
     * <code>Server</code>.
     *
     * @param  name  the name of the PostgreSQL server
     * @param  aoServer  the hostname of the <code>Server</code>
     *
     * @return  <code>true</code> if the <code>PostgresServer</code> is available
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     * @exception  IllegalArgumentException  if the server name is invalid or unable
     *					to find the <code>Server</code>
     *
     * @see  AOServer#isPostgresServerNameAvailable
     * @see  #checkPostgresServerName
     */
    public boolean isPostgresServerNameAvailable(
        String name,
        String aoServer
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "isPostgresServerNameAvailable(String,String)", null);
        try {
            checkPostgresServerName(name);
            return getAOServer(aoServer).isPostgresServerNameAvailable(name);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Determines if a <code>PostgresServerUser</code> currently has a password set.
     *
     * @param  username  the username of the account
     * @param  postgresServer  the name of the PostgreSQL server
     * @param  aoServer  the server the account is hosted on
     *
     * @return  if the <code>PostgresServerUser</code> has a password set
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     * @exception  IllegalArgumentException  if the <code>PostgresServerUser</code> is not found
     *
     * @see  PostgresServerUser#arePasswordsSet
     * @see  #setPostgresServerUserPassword
     * @see  PostgresServerUser
     */
    public boolean isPostgresServerUserPasswordSet(
        String username,
        String postgresServer,
        String aoServer
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "isPostgresServerUserPasswordSet(String,String,String)", null);
        try {
            return getPostgresServerUser(aoServer, postgresServer, username).arePasswordsSet()==PasswordProtected.ALL;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Determines if a <code>EmailDomain</code> is available.
     *
     * @param  domain  the domain
     * @param  aoServer  the hostname of the server
     *
     * @return  <code>true</code> if the <code>EmailDomain</code> is available
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     * @exception  IllegalArgumentException  if the <code>EmailDomain</code> is invalid
     *
     * @see  AOServer#isEmailDomainAvailable
     * @see  #addEmailDomain
     * @see  EmailDomain
     */
    public boolean isEmailDomainAvailable(
        String domain,
        String aoServer
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "isEmailDomainAvailable(String,String)", null);
        try {
            checkEmailDomain(domain);
            return getAOServer(aoServer).isEmailDomainAvailable(domain);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Determines if a name is available for use as a <code>HttpdSharedTomcat</code>.
     *
     * @param  name  the name
     *
     * @return  <code>true</code> if the name is available
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     *
     * @see  HttpdSharedTomcatTable#isSharedTomcatNameAvailable
     * @see  #generateSharedTomcatName
     * @see  HttpdSharedTomcat
     */
    public boolean isSharedTomcatNameAvailable(
        String name
    ) {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "isSharedTomcatNameAvailable(String)", null);
        try {
            return connector.httpdSharedTomcats.isSharedTomcatNameAvailable(name);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Determines if a site name is available.
     *
     * @param  siteName  the site name
     *
     * @return  <code>true</code> if the site name is available
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     * @exception  IllegalArgumentException  if the site name is invalid
     *
     * @see  HttpdSiteTable#isSiteNameAvailable
     */
    public boolean isSiteNameAvailable(
        String siteName
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "isSiteNameAvailable(String)", null);
        try {
            checkSiteName(siteName);
            return connector.httpdSites.isSiteNameAvailable(siteName);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Determines if a <code>Username</code> is available.
     *
     * @param  username  the username
     *
     * @return  <code>true</code> if the <code>Username</code> is available
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     * @exception  IllegalArgumentException  if the <code>Username</code> is invalid
     *
     * @see  UsernameTable#isUsernameAvailable
     * @see  #addUsername
     * @see  Username
     */
    public boolean isUsernameAvailable(
        String username
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "isUsernameAvailable(String)", null);
        try {
            checkUsername(username);
            return connector.usernames.isUsernameAvailable(username, Locale.getDefault());
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Kills a <code>Ticket</code>.  Once killed, a <code>Ticket</code> may not be modified in
     * any way.
     *
     * @param  ticket_id  the pkey of the <code>Ticket</code>
     * @param  business_administrator  the username of the <code>BusinessAdministrator</code>
     *					making the change
     * @param  comments  the details of the change
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Ticket</code>,
     *					<code>TicketType</code>, or <code>BusinessAdministrator</code>
     *
     * @see  Ticket#actKillTicket
     * @see  #addTicket
     * @see  Action
     */
    public void killTicket(
        int ticket_id,
        String business_administrator,
        String comments
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "killTicket(int,String,String)", null);
        try {
            Ticket ti=connector.tickets.get(ticket_id);
            if(ti==null) throw new IllegalArgumentException("Unable to find Ticket: "+ticket_id);
            BusinessAdministrator pe=connector.businessAdministrators.get(business_administrator);
            if(pe==null) throw new IllegalArgumentException("Unable to find BusinessAdministrator: "+business_administrator);
            ti.actKillTicket(pe, comments);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Moves all resources for one <code>Business</code> from one <code>Server</code>
     * to another <code>Server</code>.
     *
     * @param  business  the accounting code of the <code>Business</code>
     * @param  from  the hostname of the <code>Server</code> to get all the resources from
     * @param  to  the hostname of the <code>Server</code> to place all the resources on
     * @param  out  an optional <code>PrintWriter</code> to send diagnostic output to
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     * @exception  IllegalArgumentException  if unable to find the <code>Business</code> or either
     *					of the <code>Server</code>s
     *
     * @see  Business#move
     */
    public void moveBusiness(
        String business,
        String from,
        String to,
        TerminalWriter out
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "moveBusiness(String,String,String,TerminalWriter)", null);
        try {
            getBusiness(business).move(getAOServer(from), getAOServer(to), out);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Moves an <code>IPAddress</code> from one <code>AOServer</code> to another.
     *
     * @param  ip_address  the IP address to move
     * @param  to_server  the destination server
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     * @exception  IllegalArgumentException  if unable to find the <code>IPAddress</code> or
     *					the <code>AOServer</code>
     *
     * @see  IPAddress#moveTo
     */
    public void moveIPAddress(
        String ip_address,
        String from_server,
        String from_net_device,
        String to_server
    ) throws IllegalArgumentException {
        getIPAddress(from_server, from_net_device, ip_address).moveTo(getAOServer(to_server));
    }

    /**
     * Times the latency of the communication with the server.
     *
     * @return  the latency of the communication in milliseconds
     *
     * @see  AOServConnector#ping
     */
    public int ping() {
        return connector.ping();
    }

    /**
     * Prints the contents of a <code>DNSZone</code> as used by the <code>named</code> process.
     *
     * @param  zone  the name of the <code>DNSZone</code>
     * @param  out  the <code>PrintWriter</code> to write to
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>DNSZone</code>
     *
     * @see  DNSZone#printZoneFile
     * @see  #addDNSZone
     */
    public void printZoneFile(
        String zone,
        PrintWriter out
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "printZoneFile(String,PrintWriter)", null);
        try {
            getDNSZone(zone).printZoneFile(out);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Reactivates a <code>Ticket</code> that is in the hold state.
     *
     * @param  ticket_id  the pkey of the <code>Ticket</code>
     * @param  business_administrator  the username of the <code>BusinessAdministrator</code>
     *					making the change
     * @param  comments  the details of the change
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Ticket</code>,
     *					<code>TicketType</code>, or <code>BusinessAdministrator</code>
     *
     * @see  Ticket#actReactivateTicket
     * @see  #addTicket
     * @see  Action
     */
    public void reactivateTicket(
        int ticket_id,
        String business_administrator,
        String comments
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "reactivateTicket(int,String,String)", null);
        try {
            Ticket ti=connector.tickets.get(ticket_id);
            if(ti==null) throw new IllegalArgumentException("Unable to find Ticket: "+ticket_id);
            BusinessAdministrator pe=connector.businessAdministrators.get(business_administrator);
            if(pe==null) throw new IllegalArgumentException("Unable to find BusinessAdministrator: "+business_administrator);
            ti.actReactivateTicket(pe, comments);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Refreshes the time window for SMTP server access by resetting the expiration to 24 hours from the current time.
     *
     * @param  pkey  the <code>pkey</code> of the <code>EmailSmtpRelay</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>EmailSmtpRelay</code>
     *
     * @see  EmailSmtpRelay#refresh
     * @see  #addEmailSmtpRelay
     * @see  EmailSmtpRelay
     */
    public void refreshEmailSmtpRelay(
        int pkey,
        long minDuration
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "refreshEmailSmtpRelay(int,long)", null);
        try {
            EmailSmtpRelay sr=connector.emailSmtpRelays.get(pkey);
            if(sr==null) throw new IllegalArgumentException("Unable to find EmailSmtpRelay: "+pkey);
            sr.refresh(minDuration);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Removes a <code>BlackholeEmailAddress</code> from the system.
     *
     * @param  address  the part of the email address before the <code>@</code>
     * @param  domain  the part of the email address after the <code>@</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>EmailDomain</code>,
     *					<code>EmailAddress</code>, or <code>BlackholeEmailAddress</code>
     *
     * @see  BlackholeEmailAddress#remove
     */
    public void removeBlackholeEmailAddress(
        String address,
        String domain,
        String aoServer
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "removeBlackholeEmailAddress(String,String,String)", null);
        try {
            EmailAddress addr=getEmailAddress(aoServer, domain, address);
            BlackholeEmailAddress bea=addr.getBlackholeEmailAddress();
            if(bea==null) throw new IllegalArgumentException("Unable to find BlackholeEmailAddress: "+address+'@'+domain+" on "+aoServer);
            bea.remove();
            if(addr.getCannotRemoveReasons().isEmpty() && !addr.isUsed()) addr.remove();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Removes a <code>BusinessAdministrator</code> from the system.
     *
     * @param  username  the <code>username</code> of the <code>BusinessAdministrator</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Username</code> or
     *                                  <code>BusinessAdministrator</code>
     *
     * @see  BusinessAdministrator#remove
     * @see  #addBusinessAdministrator
     */
    public void removeBusinessAdministrator(
        String username
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "removeBusinessAdministrator(String)", null);
        try {
            Username un=getUsername(username);
            BusinessAdministrator ba=un.getBusinessAdministrator();
            if(ba==null) throw new IllegalArgumentException("Unable to find BusinessAdministrator: "+username);
            ba.remove();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Revokes a <code>Business</code>es access to a <code>Server</code>.  The server
     * must not have any resources allocated for the business, and the server must not
     * be the default server for the business.
     *
     * @param  accounting  the accounting code of the business
     * @param  server  the hostname of the server
     *
     * @exception  IOException  if unable to communicate with the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the business or server
     *
     * @see  BusinessServer
     * @see  BusinessServer#remove
     * @see  #addBusinessServer
     * @see  #setDefaultBusinessServer
     */
    public void removeBusinessServer(
        String accounting,
        String server
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "removeBusinessServer(String,String)", null);
        try {
            Business bu=getBusiness(accounting);
            Server se=getServer(server);
            BusinessServer bs=bu.getBusinessServer(se);
            if(bs==null) throw new IllegalArgumentException("Unable to find BusinessServer: accounting="+accounting+" and server="+server);
            bs.remove();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Removes a <code>CreditCard</code>.
     *
     * @param  pkey  the <code>pkey</code> of the <code>CreditCard</code> to remove
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>CreditCard</code>
     *
     * @see  CreditCard#remove
     */
    public void removeCreditCard(
        int pkey
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "removeCreditCard(int)", null);
        try {
            CreditCard cc=connector.creditCards.get(pkey);
            if(cc==null) throw new IllegalArgumentException("Unable to find CreditCard: "+pkey);
            cc.remove();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Removes a <code>CvsRepository</code>.
     *
     * @param  aoServer  the hostname of the <code>Server</code>
     * @param  path  the path of the repository
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or
     *                                  <code>CvsRepository</code>
     *
     * @see  CvsRepository#remove
     * @see  #addCvsRepository
     * @see  CvsRepository
     */
    public void removeCvsRepository(
        String aoServer,
        String path
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "removeCvsRepository(String,String)", null);
        try {
            AOServer ao=getAOServer(aoServer);
            CvsRepository cr=ao.getCvsRepository(path);
            if(cr==null) throw new IllegalArgumentException("Unable to find CvsRepository: "+path+" on "+aoServer);
            cr.remove();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Removes one record from a <code>DNSZone</code>.
     *
     * @param  pkey  the <code>pkey</code> of the <code>DNSRecord</code> to remove
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>DNSRecord</code>
     *
     * @see  DNSRecord#remove
     * @see  #addDNSRecord
     * @see  DNSRecord
     */
    public void removeDNSRecord(
        int pkey
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "removeDNSRecord(int)", null);
        try {
            DNSRecord nr=connector.dnsRecords.get(pkey);
            if(nr==null) throw new IllegalArgumentException("Unable to find DNSRecord: "+pkey);
            nr.remove();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Completely removes a <code>DNSZone</code> from the servers.
     *
     * @param  zone  the name of the <code>DNSZone</code> to remove
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>DNSZone</code>
     *
     * @see  DNSZone#remove
     * @see  #addDNSZone
     * @see  DNSZone
     */
    public void removeDNSZone(
        String zone
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "removeDNSZone(String)", null);
        try {
            getDNSZone(zone).remove();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
    
    /**
     * Completely removes a <code>DNSZone</code> from the servers.
     *
     * @param  zone  the name of the <code>DNSZone</code> to remove
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>DNSZone</code>
     *
     * @see  DNSZone#remove
     * @see  #addDNSZone
     * @see  DNSZone
     */
    public void setDNSZoneTTL(
        String zone,
        int ttl
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "setDNSZoneTTL(String,int)", null);
        try {
            getDNSZone(zone).setTTL(ttl);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Removes an <code>EmailAddress</code> from the system.  If the <code>EmailAddress</code> is used
     * by other resources, such as <code>EmailListAddress</code>es, those resources are also removed.
     *
     * @param  address  the part of the email address before the <code>@</code>
     * @param  domain  the part of the email address after the <code>@</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>EmailDomain</code> or
     *					<code>EmailAddress</code>
     *
     * @see  EmailAddress#remove
     * @see  #addEmailForwarding
     * @see  #addEmailListAddress
     * @see  #addEmailPipeAddress
     * @see  #addLinuxAccAddress
     */
    public void removeEmailAddress(
        String address,
        String domain,
        String aoServer
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "removeEmailAddress(String,String,String)", null);
        try {
            getEmailAddress(aoServer, domain, address).remove();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Removes an <code>EmailForwarding</code> from the system.
     *
     * @param  address  the part of the email address before the <code>@</code>
     * @param  domain  the part of the email address after the <code>@</code>
     * @param  aoServer  the hostname of the server that hosts this domain
     * @param  destination  the destination of the email forwarding
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>EmailDomain</code>,
     *					<code>EmailAddress</code>, or <code>EmailForwarding</code>
     *
     * @see  EmailForwarding#remove
     * @see  #addEmailForwarding
     */
    public void removeEmailForwarding(
        String address,
        String domain,
        String aoServer,
        String destination
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "removeEmailForwarding(String,String,String,String)", null);
        try {
            EmailAddress addr=getEmailAddress(aoServer, domain, address);
            EmailForwarding ef=addr.getEmailForwarding(destination);
            if(ef==null) throw new IllegalArgumentException("Unable to find EmailForwarding: "+address+'@'+domain+"->"+destination+" on "+aoServer);
            ef.remove();
            if(addr.getCannotRemoveReasons().isEmpty() && !addr.isUsed()) addr.remove();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Removes an <code>EmailList</code> from the system.  All <code>EmailAddress</code>es that are directed
     * to the list are also removed.  The file that stores the list contents is removed from the file system.
     *
     * @param  path  the path of the <code>EmailList</code> to remove
     * @param  aoServer  the server that hosts this list
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>EmailList</code>
     *
     * @see  EmailList#remove
     * @see  #addEmailList
     */
    public void removeEmailList(
        String path,
        String aoServer
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "removeEmailList(String,String)", null);
        try {
            getEmailList(aoServer, path).remove();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Removes an <code>EmailListAddress</code> from the system.
     *
     * @param  address  the part of the email address before the <code>@</code>
     * @param  domain  the part of the email address after the <code>@</code>
     * @param  path  the list the emails are sent to
     * @param  aoServer  the hostname of the server hosting the list
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>EmailDomain</code>,
     *					<code>EmailAddress</code>, <code>EmailList</code>, or
     *                                  <code>EmailListAddress</code>
     *
     * @see  EmailListAddress#remove
     * @see  #addEmailListAddress
     */
    public void removeEmailListAddress(
        String address,
        String domain,
        String path,
        String aoServer
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "removeEmailListAddress(String,String,String,String)", null);
        try {
            EmailAddress addr=getEmailAddress(aoServer, domain, address);
            EmailList el=getEmailList(aoServer, path);
            EmailListAddress ela=addr.getEmailListAddress(el);
            if(ela==null) throw new IllegalArgumentException("Unable to find EmailListAddress: "+address+'@'+domain+"->"+path+" on "+aoServer);
            ela.remove();
            if(addr.getCannotRemoveReasons().isEmpty() && !addr.isUsed()) addr.remove();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Removes an <code>EmailPipe</code> from the system.  All <code>EmailAddress</code>es that are directed
     * to the pipe are also removed.
     *
     * @param  pkey  the <code>pkey</code> of the <code>EmailPipe</code> to remove
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>EmailPipe</code>
     *
     * @see  EmailPipe#remove
     * @see  #addEmailPipe
     */
    public void removeEmailPipe(
        int pkey
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "removeEmailPipe(int)", null);
        try {
            EmailPipe ep=connector.emailPipes.get(pkey);
            if(ep==null) throw new IllegalArgumentException("Unable to find EmailPipe: "+pkey);
            ep.remove();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Removes an <code>EmailPipeAddress</code> from the system.
     *
     * @param  address  the part of the email address before the <code>@</code>
     * @param  domain  the part of the email address after the <code>@</code>
     * @param  pipe  the pkey of the email pipe
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>EmailDomain</code>,
     *					<code>EmailAddress</code>, <code>EmailPipe</code>, or
     *                                  <code>EmailPipeAddress</code>
     *
     * @see  EmailPipeAddress#remove
     * @see  #addEmailPipeAddress
     */
    public void removeEmailPipeAddress(
        String address,
        String domain,
        int pipe
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "removeEmailPipeAddress(String,String,int)", null);
        try {
            EmailPipe ep=connector.emailPipes.get(pipe);
            if(ep==null) throw new IllegalArgumentException("Unable to find EmailPipe: "+pipe);
            AOServer ao=ep.getAOServer();
            EmailDomain sd=ao.getEmailDomain(domain);
            if(sd==null) throw new IllegalArgumentException("Unable to find EmailDomain: "+domain+" on "+ao.getHostname());
            EmailAddress addr=connector.emailAddresses.getEmailAddress(address, sd);
            if(addr==null) throw new IllegalArgumentException("Unable to find EmailAddress: "+address+'@'+domain+" on "+ao.getHostname());
            EmailPipeAddress epa=addr.getEmailPipeAddress(ep);
            if(epa==null) throw new IllegalArgumentException("Unable to find EmailPipeAddress: "+address+'@'+domain+"->"+ep);
            epa.remove();
            if(addr.getCannotRemoveReasons().isEmpty() && !addr.isUsed()) addr.remove();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Removes the <code>FTPGuestUser</code> flag from a <code>LinuxAccount</code>, allowing access
     * to the server root directory.
     *
     * @param  username  the username of the <code>FTPGuestUser</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>FTPGuestUser</code>
     *
     * @see  FTPGuestUser#remove
     * @see  #addFTPGuestUser
     */
    public void removeFTPGuestUser(
        String username
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "removeFTPGuestUser(String)", null);
        try {
            FTPGuestUser ftpUser=connector.ftpGuestUsers.get(username);
            if(ftpUser==null) throw new IllegalArgumentException("Unable to find FTPGuestUser: "+username);
            ftpUser.remove();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Completely removes a <code>HttpdSharedTomcat</code> from the servers.
     *
     * @param  name  the name of the site
     * @param  aoServer  the server the site runs on
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>HttpdSharedTomcat</code>
     *
     * @see  HttpdSharedTomcat#remove
     * @see  HttpdSharedTomcat
     */
    public void removeHttpdSharedTomcat(
        String name,
        String aoServer
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "removeHttpdSharedTomcat(String,String)", null);
        try {
            getHttpdSharedTomcat(aoServer, name).remove();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Completely removes a <code>HttpdSite</code> from the servers.
     *
     * @param  name  the name of the site
     * @param  aoServer  the server the site runs on
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>HttpdSite</code>
     *
     * @see  HttpdSite#remove
     * @see  HttpdSite
     */
    public void removeHttpdSite(
        String name,
        String aoServer
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "removeHttpdSite(String,String)", null);
        try {
            getHttpdSite(aoServer, name).remove();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Removes a <code>HttpdSiteURL</code> from the servers.
     *
     * @param  pkey  the pkey of the site URL
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>HttpdSiteURL</code>
     *
     * @see  HttpdSiteURL#remove
     */
    public void removeHttpdSiteURL(
        int pkey
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "removeHttpdSiteURL(int)", null);
        try {
            HttpdSiteURL hsu=connector.httpdSiteURLs.get(pkey);
            if(hsu==null) throw new IllegalArgumentException("Unable to find HttpdSiteURL: "+pkey);
            hsu.remove();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Removes a <code>HttpdTomcatContext</code> from the servers.
     *
     * @param  pkey  the pkey of the context
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>HttpdTomcatContext</code>
     *
     * @see  HttpdTomcatContext#remove
     */
    public void removeHttpdTomcatContext(
        int pkey
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "removeHttpdTomcatContext(int)", null);
        try {
            HttpdTomcatContext htc=connector.httpdTomcatContexts.get(pkey);
            if(htc==null) throw new IllegalArgumentException("Unable to find HttpdTomcatContext: "+pkey);
            htc.remove();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Removes a <code>HttpdTomcatDataSource</code> from a <code>HttpdTomcatContext</code>.
     *
     * @param  pkey  the pkey of the data source
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>HttpdTomcatDataSource</code>
     *
     * @see  HttpdTomcatContext#remove
     */
    public void removeHttpdTomcatDataSource(
        int pkey
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "removeHttpdTomcatDataSource(int)", null);
        try {
            HttpdTomcatDataSource htds=connector.httpdTomcatDataSources.get(pkey);
            if(htds==null) throw new IllegalArgumentException("Unable to find HttpdTomcatDataSource: "+pkey);
            htds.remove();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Removes a <code>HttpdTomcatParameter</code> from a <code>HttpdTomcatContext</code>.
     *
     * @param  pkey  the pkey of the parameter
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>HttpdTomcatParameter</code>
     *
     * @see  HttpdTomcatContext#remove
     */
    public void removeHttpdTomcatParameter(
        int pkey
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "removeHttpdTomcatParameter(int)", null);
        try {
            HttpdTomcatParameter htp=connector.httpdTomcatParameters.get(pkey);
            if(htp==null) throw new IllegalArgumentException("Unable to find HttpdTomcatParameter: "+pkey);
            htp.remove();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Removes a <code>LinuxAccAddress</code> from the system.
     *
     * @param  address  the part of the email address before the <code>@</code>
     * @param  domain  the part of the email address after the <code>@</code>
     * @param  username  the account the emails are sent to
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>EmailDomain</code>,
     *					<code>EmailAddress</code>, <code>Username</code>,
     *                                  <code>LinuxAccount</code>, or <code>LinuxAccAddress</code>
     *
     * @see  LinuxAccAddress#remove
     * @see  #addLinuxAccAddress
     */
    public void removeLinuxAccAddress(
        String address,
        String domain,
        String aoServer,
        String username
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "removeLinuxAccAddress(String,String,String,String)", null);
        try {
            EmailAddress addr=getEmailAddress(aoServer, domain, address);
            LinuxServerAccount lsa=getLinuxServerAccount(aoServer, username);
            LinuxAccAddress laa=addr.getLinuxAccAddress(lsa);
            if(laa==null) throw new IllegalArgumentException("Unable to find LinuxAccAddress: "+address+'@'+domain+"->"+username+" on "+aoServer);
            laa.remove();
            if(addr.getCannotRemoveReasons().isEmpty() && !addr.isUsed()) addr.remove();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Removes a <code>LinuxAccount</code> and all related data from the system.
     *
     * @param  username  the username of the <code>LinuxAccount</code> to remove
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>LinuxAccount</code>
     *
     * @see  LinuxAccount#remove
     * @see  #addLinuxAccount
     */
    public void removeLinuxAccount(
        String username
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "removeLinuxAccount(String)", null);
        try {
            getLinuxAccount(username).remove();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Removes a <code>LinuxGroup</code> and all related data from the system.
     *
     * @param  name  the name of the <code>LinuxGroup</code> to remove
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>LinuxGroup</code>
     *
     * @see  LinuxGroup#remove
     * @see  #addLinuxGroup
     */
    public void removeLinuxGroup(
        String name
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "removeLinuxGroup(String)", null);
        try {
            getLinuxGroup(name).remove();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Removes a <code>LinuxAccount</code>'s access to a <code>LinuxGroup</code>.
     *
     * @param  group  the name of the <code>LinuxGroup</code> to remove access to
     * @param  username  the username of the <code>LinuxAccount</code> to remove access from
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>LinuxGroup</code>,
     *					<code>LinuxAccount</code>, or <code>LinuxGroupAccount</code>
     *
     * @see  LinuxGroupAccount#remove
     * @see  #addLinuxGroupAccount
     * @see  #addLinuxGroup
     * @see  #addLinuxAccount
     */
    public void removeLinuxGroupAccount(
        String group,
        String username
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "removeLinuxGroupAccount(String,String)", null);
        try {
            LinuxGroup lg=getLinuxGroup(group);
            LinuxAccount la=getLinuxAccount(username);
            LinuxGroupAccount lga=connector.linuxGroupAccounts.getLinuxGroupAccount(group, username);
            if(lga==null) throw new IllegalArgumentException(username+" is not part of the "+group+" group");
            lga.remove();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Removes a <code>LinuxServerAccount</code> from a <code>Server</code>.
     *
     * @param  username  the username of the <code>LinuxServerAccount</code> to remove
     * @param  aoServer  the hostname of the <code>Server</code> to remove the account from
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>LinuxAccount</code>,
     *					<code>Server</code>, or <code>LinuxServerAccount</code>
     *
     * @see  LinuxServerAccount#remove
     * @see  #addLinuxServerAccount
     */
    public void removeLinuxServerAccount(
        String username,
        String aoServer
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "removeLinuxServerAccount(String,String)", null);
        try {
            getLinuxServerAccount(aoServer, username).remove();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Removes a <code>LinuxServerGroup</code> from a <code>Server</code>.
     *
     * @param  group  the name of the <code>LinuxServerGroup</code> to remove
     * @param  aoServer  the hostname of the <code>AOServer</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>LinuxGroup</code>,
     *					<code>Server</code>, or <code>LinuxServerGroup</code>
     *
     * @see  LinuxServerGroup#remove
     * @see  #addLinuxServerGroup
     */
    public void removeLinuxServerGroup(
        String group,
        String aoServer
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "removeLinuxServerGroup(String,String)", null);
        try {
            getLinuxServerGroup(aoServer, group).remove();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Removes a <code>MySQLDatabase</code> from the system.  All related
     * <code>MySQLDBUser</code>s are also removed, and all data is removed
     * from the MySQL server.  The data is not dumped or backed-up during
     * the removal, if a backup is desired, use <code>backupMySQLDatabase</code>
     * or <code>dumpMySQLDatabase</code>.
     *
     * @param  name  the name of the database
     * @param  aoServer  the server the database is hosted on
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or
     *					<code>MySQLDatabase</code>
     *
     * @see  MySQLDatabase#remove
     * @see  #addMySQLDatabase
     * @see  #backupMySQLDatabase
     * @see  #dumpMySQLDatabase
     */
    public void removeMySQLDatabase(
        String name,
        String mysqlServer,
        String aoServer
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "removeMySQLDatabase(String,String,String)", null);
        try {
            getMySQLDatabase(aoServer, mysqlServer, name).remove();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Removes a <code>MySQLDBUser</code> from the system.  The <code>MySQLUser</code> is
     * no longer allowed to access the <code>MySQLDatabase</code>.
     *
     * @param  name  the name of the <code>MySQLDatabase</code>
     * @param  aoServer  the hostname of the <code>AOServer</code>
     * @param  username  the username of the <code>MySQLUser</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code>,
     *					<code>MySQLDatabase</code>, <code>MySQLServerUser</code>, or
     *					<code>MySQLDBUser</code>
     *
     * @see  MySQLDBUser#remove
     * @see  #addMySQLDBUser
     */
    public void removeMySQLDBUser(
        String name,
        String mysqlServer,
        String aoServer,
        String username
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "removeMySQLDBUser(String,String,String,String)", null);
        try {
            MySQLDatabase md=getMySQLDatabase(aoServer, mysqlServer, name);
            MySQLServerUser msu=getMySQLServerUser(aoServer, mysqlServer, username);
            MySQLDBUser mdu=md.getMySQLDBUser(msu);
            if(mdu==null) throw new IllegalArgumentException("Unable to find MySQLDBUser on MySQLServer "+mysqlServer+" on AOServer "+aoServer+" for MySQLDatabase named "+name+" and MySQLServerUser named "+username);
            mdu.remove();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Removes a <code>MySQLServerUser</code> from a the system..  The <code>MySQLUser</code> is
     * no longer allowed to access the <code>Server</code>.
     *
     * @param  username  the username of the <code>MySQLServerUser</code>
     * @param  aoServer  the hostname of the <code>Server</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or
     *					<code>MySQLServerUser</code>
     *
     * @see  MySQLServerUser#remove
     * @see  #addMySQLServerUser
     */
    public void removeMySQLServerUser(
        String username,
        String mysqlServer,
        String aoServer
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "removeMySQLServerUser(String,String,String)", null);
        try {
            getMySQLServerUser(aoServer, mysqlServer, username).remove();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Removes a <code>MySQLUser</code> from a the system.  All of the associated
     * <code>MySQLServerUser</code>s and <code>MySQLDBUser</code>s are also removed.
     *
     * @param  username  the username of the <code>MySQLUser</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>MySQLUser</code>
     *
     * @see  MySQLUser#remove
     * @see  #addMySQLUser
     * @see  #removeMySQLServerUser
     */
    public void removeMySQLUser(
        String username
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "removeMySQLUser(String)", null);
        try {
            getMySQLUser(username).remove();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Removes a <code>NetBind</code> from a the system.
     *
     * @param  pkey  the primary key of the <code>NetBind</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>NetBind</code>
     *
     * @see  NetBind#remove
     */
    public void removeNetBind(
        int pkey
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "removeNetBind(int)", null);
        try {
            getNetBind(pkey).remove();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Removes a <code>PostgresDatabase</code> from the system.  All data is removed
     * from the PostgreSQL server.  The data is not dumped or backed-up during
     * the removal, if a backup is desired, use <code>backupPostgresDatabase</code>
     * or <code>dumpPostgresDatabase</code>.
     *
     * @param  name  the name of the database
     * @param  postgresServer  the name of the PostgreSQL server
     * @param  aoServer  the server the database is hosted on
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or
     *					<code>PostgresDatabase</code>
     *
     * @see  PostgresDatabase#remove
     * @see  #addPostgresDatabase
     * @see  #backupPostgresDatabase
     * @see  #dumpPostgresDatabase
     */
    public void removePostgresDatabase(
        String name,
        String postgresServer,
        String aoServer
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "removePostgresDatabase(String,String,String)", null);
        try {
            getPostgresDatabase(aoServer, postgresServer, name).remove();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Removes a <code>PostgresServerUser</code> from a the system..  The <code>PostgresUser</code> is
     * no longer allowed to access the <code>Server</code>.
     *
     * @param  username  the username of the <code>PostgresServerUser</code>
     * @param  postgresServer  the name of the PostgreSQL server
     * @param  aoServer  the hostname of the <code>Server</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or
     *					<code>PostgresServerUser</code>
     *
     * @see  PostgresServerUser#remove
     */
    public void removePostgresServerUser(
        String username,
        String postgresServer,
        String aoServer
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "removePostgresServerUser(String,String,String)", null);
        try {
            getPostgresServerUser(aoServer, postgresServer, username).remove();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Removes a <code>PostgresUser</code> from a the system..  All of the associated
     * <code>PostgresServerUser</code>s are also removed.
     *
     * @param  username  the username of the <code>PostgresUser</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>PostgresUser</code>
     *
     * @see  PostgresUser#remove
     * @see  #addPostgresUser
     * @see  #removePostgresServerUser
     */
    public void removePostgresUser(
        String username
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "removePostgresUser(String)", null);
        try {
            getPostgresUser(username).remove();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Removes an <code>EmailDomain</code> and all of its <code>EmailAddress</code>es.
     *
     * @param  domain  the name of the <code>EmailDomain</code>
     * @param  aoServer  the server hosting this domain
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>EmailDomain</code>
     *
     * @see  EmailDomain#remove
     * @see  #addEmailDomain
     * @see  #removeEmailAddress
     */
    public void removeEmailDomain(
        String domain,
        String aoServer
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "removeEmailDomain(String,String)", null);
        try {
            getEmailDomain(aoServer, domain).remove();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Removes an <code>EmailSMTPRelay</code> from the system, revoking access to the SMTP
     * server from one IP address.
     *
     * @param  pkey  the <code>pkey</code> of the <code>EmailDomain</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>EmailSmtpRelay</code>
     *
     * @see  EmailSmtpRelay#remove
     * @see  #addEmailSmtpRelay
     * @see  #refreshEmailSmtpRelay
     */
    public void removeEmailSmtpRelay(
        int pkey
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "removeEmailSmtpRelay(int)", null);
        try {
            EmailSmtpRelay sr=connector.emailSmtpRelays.get(pkey);
            if(sr==null) throw new IllegalArgumentException("Unable to find EmailSmtpRelay: "+pkey);
            sr.remove();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Removes a <code>FileBackupSetting</code> from the system.
     *
     * @param  replication  the pkey of the <code>FailoverFileReplication</code>
     * @param  path  the path of the setting
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>FailoverFileReplication</code> or <code>FileBackupSetting</code>
     *
     * @see  FileBackupSetting#remove
     * @see  #addFileBackupSetting
     */
    public void removeFileBackupSetting(
        int replication,
        String path
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "removeFileBackupSetting(int,String)", null);
        try {
            FailoverFileReplication ffr = getConnector().failoverFileReplications.get(replication);
            if(ffr==null) throw new IllegalArgumentException("Unable to find FailoverFileReplication: "+replication);
            FileBackupSetting fbs=ffr.getFileBackupSetting(path);
            if(fbs==null) throw new IllegalArgumentException("Unable to find FileBackupSetting: "+path+" on "+replication);
            fbs.remove();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Removes a <code>MajordomoServer</code> and all of its <code>MajordomoList</code>s.
     *
     * @param  domain  the name of the <code>MajordomoServer</code>
     * @param  aoServer  the server hosting the list
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code>,
     *                                  <code>EmailDomain</code> or <code>MajordomoServer</code>
     *
     * @see  MajordomoServer#remove
     * @see  #addMajordomoServer
     */
    public void removeMajordomoServer(
        String domain,
        String aoServer
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "removeMajordomoServer(String,String)", null);
        try {
            EmailDomain sd=getEmailDomain(aoServer, domain);
            MajordomoServer ms=sd.getMajordomoServer();
            if(ms==null) throw new IllegalArgumentException("Unable to find MajordomoServer: "+domain+" on "+aoServer);
            ms.remove();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Removes a <code>Username</code> from the system.
     *
     * @param  username  the <code>username</code> of the <code>Username</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Username</code>
     *
     * @see  Username#remove
     * @see  #addUsername
     */
    public void removeUsername(
        String username
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "removeUsername(String)", null);
        try {
            getUsername(username).remove();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Restarts the Apache web server.
     *
     * @param  aoServer       the public hostname of the <code>AOServer</code>
     *
     * @exception  IOException  if not able to communicate with the server
     * @exception  SQLException  if not able to access the database
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
     *
     * @see  AOServer#restartApache
     */
    public void restartApache(String aoServer) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "restartApache(String)", null);
        try {
            getAOServer(aoServer).restartApache();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Restarts the cron doggie.
     *
     * @param  aoServer       the public hostname of the <code>AOServer</code>
     *
     * @exception  IOException  if not able to communicate with the server
     * @exception  SQLException  if not able to access the database
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
     *
     * @see  AOServer#restartCron
     */
    public void restartCron(String aoServer) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "restartCron(String)", null);
        try {
            getAOServer(aoServer).restartCron();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Restarts the MySQL database server.
     *
     * @param  aoServer       the public hostname of the <code>AOServer</code>
     *
     * @exception  IOException  if not able to communicate with the server
     * @exception  SQLException  if not able to access the database
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
     *
     * @see  MySQLServer#restartMySQL
     */
    public void restartMySQL(String mysqlServer, String aoServer) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "restartMySQL(String,String)", null);
        try {
            getMySQLServer(aoServer, mysqlServer).restartMySQL();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Restarts the PostgreSQL database server.
     *
     * @param  postgresServer  the name of the PostgreSQL server
     * @param  aoServer  the public hostname of the <code>AOServer</code>
     *
     * @exception  IOException  if not able to communicate with the server
     * @exception  SQLException  if not able to access the database
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
     *
     * @see  PostgresServer#restartPostgreSQL
     */
    public void restartPostgreSQL(String postgresServer, String aoServer) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "restartPostgreSQL(String,String)", null);
        try {
            getPostgresServer(aoServer, postgresServer).restartPostgreSQL();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Restarts the X Font Server.
     *
     * @param  aoServer       the public hostname of the <code>AOServer</code>
     *
     * @exception  IOException  if not able to communicate with the server
     * @exception  SQLException  if not able to access the database
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
     *
     * @see  AOServer#restartXfs
     */
    public void restartXfs(String aoServer) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "restartXfs(String)", null);
        try {
            getAOServer(aoServer).restartXfs();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Restarts the X Virtual Frame Buffer.
     *
     * @param  aoServer       the public hostname of the <code>AOServer</code>
     *
     * @exception  IOException  if not able to communicate with the server
     * @exception  SQLException  if not able to access the database
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
     *
     * @see  AOServer#restartXvfb
     */
    public void restartXvfb(String aoServer) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "restartXvfb(String)", null);
        try {
            getAOServer(aoServer).restartXvfb();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Sets the autoresponder behavior for a Linux server account.
     *
     * @param  username  the username of the account
     * @param  aoServer  the server the account is on
     * @param  address  the address part of the email address
     * @param  domain  the domain of the email address
     * @param  subject  the subject of the email
     * @param  content  the content of the email
     * @param  enabled  if the autoresponder is enabled or not
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>EmailAddress</code> or
     *                                  the <code>LinuxServerAccount</code>
     *
     * @see  LinuxServerAccount#setAutoresponder
     */
    public void setAutoresponder(
        String username,
        String aoServer,
        String address,
        String domain,
        String subject,
        String content,
        boolean enabled
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "setAutoresponder(String,String,String,String,String,String,boolean)", null);
        try {
            LinuxServerAccount lsa=getLinuxServerAccount(aoServer, username);
            if(domain!=null && domain.length()==0) domain=null;
            if(address==null) address="";
            EmailDomain sd;
            EmailAddress ea;
            if(domain==null) {
                sd=null;
                if(address.length()>0) throw new IllegalArgumentException("Cannot have an address without a domain: "+address);
                ea=null;
            } else {
                sd=getEmailDomain(aoServer, domain);
                ea=sd.getEmailAddress(address);
                if(ea==null) throw new IllegalArgumentException("Unable to find EmailAddress: "+address+'@'+domain+" on "+aoServer);
            }
            if(subject!=null && subject.length()==0) subject=null;
            if(content!=null && content.length()==0) content=null;
            LinuxAccAddress laa=ea.getLinuxAccAddress(lsa);
            if(laa==null) throw new IllegalArgumentException("Unable to find LinuxAccAddress: "+address+" on "+aoServer);
            lsa.setAutoresponder(laa, subject, content, enabled);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Sets the accounting code for the business.  The accounting code is the value that uniquely
     * identifies an account within the system.
     *
     * @param  oldAccounting  the old accounting code
     * @param  newAccounting  the new accounting code
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Business</code> or
     *                                  the requested accounting code is not valid
     *
     * @see  Business#setAccounting
     */
    public void setBusinessAccounting(
        String oldAccounting,
        String newAccounting
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "setBusinessAccounting(String,String)", null);
        try {
            getBusiness(oldAccounting).setAccounting(newAccounting);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Sets the password for a <code>BusinessAdministrator</code>.  This password must pass the security
     * checks provided by <code>checkBusinessAdministratorPassword</code>.
     *
     * @param  username  the username of the <code>BusinessAdministrator</code>
     * @param  password  the new password
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>BusinessAdministrator</code>
     *
     * @see  BusinessAdministrator#setPassword
     * @see  #addBusinessAdministrator
     */
    public void setBusinessAdministratorPassword(
        String username,
        String password
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "setBusinessAdministratorPassword(String,String)", null);
        try {
            BusinessAdministrator pe=connector.businessAdministrators.get(username);
            if(pe==null) throw new IllegalArgumentException("Unable to find BusinessAdministrator: "+username);
            pe.setPassword(password);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Sets the profile of a <code>BusinessAdministrator</code>, which is all of their contact
     * information and other details.
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>BusinessAdministrator</code>
     *
     * @see  BusinessAdministrator#setProfile
     * @see  #addBusinessAdministrator
     */
    public void setBusinessAdministratorProfile(
        String username,
        String name,
        String title,
        long birthday,
        boolean isPrivate,
        String workPhone,
        String homePhone,
        String cellPhone,
        String fax,
        String email,
        String address1,
        String address2,
        String city,
        String state,
        String country,
        String zip
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "setBusinessAdministratorProfile(String,String,String,long,boolean,String,String,String,String,String,String,String,String,String,String,String)", null);
        try {
            BusinessAdministrator business_administrator=connector.businessAdministrators.get(username);
            if(business_administrator==null) throw new IllegalArgumentException("Unable to find BusinessAdministrator: "+username);
            business_administrator.setProfile(
                name,
                title,
                birthday,
                isPrivate,
                workPhone,
                homePhone,
                cellPhone,
                fax,
                email,
                address1,
                address2,
                city,
                state,
                country,
                zip
            );
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Sets a user's cron table on one server.
     *
     * @param  username  the username of the <code>LinuxAccount</code>
     * @param  aoServer  the server to get the data from
     * @param  cronTable  the new cron table
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     * @exception  IllegalArgumentException  if unable to find the source <code>LinuxServerAccount</code>
     *
     * @see  LinuxServerAccount#setCronTable
     * @see  #getCronTable
     * @see  #addLinuxServerAccount
     * @see  #removeLinuxServerAccount
     */
    public void setCronTable(
        String username,
        String aoServer,
        String cronTable
    ) {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "setCronTable(String,String,String)", null);
        try {
            getLinuxServerAccount(aoServer, username).setCronTable(cronTable);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Sets the permissions for a CVS repository directory.
     *
     * @param  aoServer  the server the repository exists on
     * @param  path  the path of the server
     * @param  mode  the permission bits
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     * @exception  IllegalArgumentException  if unable to find the source <code>CvsRepository</code>
     *
     * @see  CvsRepository#setMode
     * @see  #addCvsRepository
     * @see  #removeCvsRepository
     */
    public void setCvsRepositoryMode(
        String aoServer,
        String path,
        long mode
    ) {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "setCvsRepositoryMode(String,String,long)", null);
        try {
            AOServer ao=getAOServer(aoServer);
            CvsRepository cr=ao.getCvsRepository(path);
            if(cr==null) throw new IllegalArgumentException("Unable to find CvsRepository: "+path+" on "+aoServer);
            cr.setMode(mode);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Sets the default <code>Server</code> for a <code>Business</code>.
     *
     * @param  accounting  the accounting code of the business
     * @param  server  the hostname of the server
     *
     * @exception  IOException  if unable to communicate with the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the business or server
     *
     * @see  BusinessServer
     * @see  BusinessServer#setAsDefault
     * @see  #addBusinessServer
     * @see  #removeBusinessServer
     */
    public void setDefaultBusinessServer(
        String accounting,
        String server
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "setDefaultBusinessServer(String,String)", null);
        try {
            Business bu=getBusiness(accounting);
            Server se=getServer(server);
            BusinessServer bs=bu.getBusinessServer(se);
            if(bs==null) throw new IllegalArgumentException("Unable to find BusinessServer: accounting="+accounting+" and server="+server);
            bs.setAsDefault();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Sets the list of addresses that an <code>EmailList</code> will forward messages
     * to.
     *
     * @param  path  the path of the <code>EmailList</code>
     * @param  aoServer  the server hosting the list
     * @param  addresses  the list of addresses, one address per line
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>EmailList</code>
     *
     * @see  EmailList#setAddressList
     * @see  #getEmailListAddressList
     * @see  #addEmailList
     */
    public void setEmailListAddressList(
        String path,
        String aoServer,
        String addresses
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "setEmailList(String,String,String)", null);
        try {
            getEmailList(aoServer, path).setAddressList(addresses);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Sets the settings contained by one <code>FileBackupSetting</code>
     *
     * @param  replication  the hostname of the <code>FailoverFileReplication</code>
     * @param  path  the path of the setting
     * @param  backupEnabled  the enabled flag for the prefix
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>FailoverFileReplication</code> or <code>FileBackupSetting</code>
     *
     * @see  FileBackupSetting#setSettings
     * @see  #addFileBackupSetting
     */
    public void setFileBackupSetting(
        int replication,
        String path,
        boolean backupEnabled
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "setFileBackupSetting(int,String,boolean)", null);
        try {
            FailoverFileReplication ffr = getConnector().failoverFileReplications.get(replication);
            if(ffr==null) throw new IllegalArgumentException("Unable to find FailoverFileReplication: "+replication);
            FileBackupSetting fbs=ffr.getFileBackupSetting(path);
            if(fbs==null) throw new IllegalArgumentException("Unable to find FileBackupSetting: "+path+" on "+replication);
            fbs.setSettings(
                path,
                backupEnabled
            );
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Sets the <code>is_manual</code> flag for a <code>HttpdSharedTomcat</code>
     *
     * @param  name  the name of the JVM
     * @param  aoServer  the hostname of the <code>AOServer</code>
     * @param  isManual  the new flag
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>AOServer</code> or <code>HttpdSharedTomcat</code>
     *
     * @see  HttpdSharedTomcat#setIsManual
     */
    public void setHttpdSharedTomcatIsManual(
        String name,
        String aoServer,
        boolean isManual
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "setHttpdSharedTomcatIsManual(String,String,boolean)", null);
        try {
            getHttpdSharedTomcat(aoServer, name).setIsManual(isManual);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Sets the <code>is_manual</code> flag for a <code>HttpdSiteBind</code>
     *
     * @param  pkey  the primary key of the <code>HttpdSiteBind</code>
     * @param  isManual  the new flag
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>HttpdSiteBind</code>
     *
     * @see  HttpdSiteBind#setIsManual
     */
    public void setHttpdSiteBindIsManual(
        int pkey,
        boolean isManual
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "setHttpdSiteBindIsManual(int,boolean)", null);
        try {
            HttpdSiteBind hsb=connector.httpdSiteBinds.get(pkey);
            if(hsb==null) throw new IllegalArgumentException("Unable to find HttpdSiteBind: "+pkey);
            hsb.setIsManual(isManual);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Sets the <code>redirect_to_primary_hostname</code> flag for a <code>HttpdSiteBind</code>
     *
     * @param  pkey  the primary key of the <code>HttpdSiteBind</code>
     * @param  redirectToPrimaryHostname  the new flag
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>HttpdSiteBind</code>
     *
     * @see  HttpdSiteBind#setRedirectToPrimaryHostname
     */
    public void setHttpdSiteBindRedirectToPrimaryHostname(
        int pkey,
        boolean redirectToPrimaryHostname
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "setHttpdSiteBindRedirectToPrimaryHostname(int,boolean)", null);
        try {
            HttpdSiteBind hsb=connector.httpdSiteBinds.get(pkey);
            if(hsb==null) throw new IllegalArgumentException("Unable to find HttpdSiteBind: "+pkey);
            hsb.setRedirectToPrimaryHostname(redirectToPrimaryHostname);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Sets the <code>is_manual</code> flag for a <code>HttpdSite</code>
     *
     * @param  siteName  the name of the site
     * @param  aoServer  the hostname of the <code>AOServer</code>
     * @param  isManual  the new flag
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>AOServer</code> or <code>HttpdSite</code>
     *
     * @see  HttpdSite#setIsManual
     */
    public void setHttpdSiteIsManual(
        String siteName,
        String aoServer,
        boolean isManual
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "setHttpdSiteIsManual(String,String,boolean)", null);
        try {
            getHttpdSite(aoServer, siteName).setIsManual(isManual);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Sets the administrative email address for a <code>HttpdSite</code>.
     *
     * @param  siteName  the name of the <code>HttpdSite</code>
     * @param  aoServer  the hostname of the server that hosts the site
     * @param  emailAddress  the new adminstrative email address
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>AOServer</code> or <code>HttpdSite</code>,
     *                                  or the email address is not in a valid format
     *
     * @see  HttpdSite#setServerAdmin
     */
    public void setHttpdSiteServerAdmin(
        String siteName,
        String aoServer,
        String emailAddress
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "setHttpdSiteServerAdmin(String,String,String)", null);
        try {
            if(!EmailAddress.isValidEmailAddress(emailAddress)) throw new IllegalArgumentException("Invalid email address: "+emailAddress);
            getHttpdSite(aoServer, siteName).setServerAdmin(emailAddress);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Sets the attributes for a <code>HttpdTomcatContext</code>.
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     * @exception  IllegalArgumentException  if unable to find the <code>AOServer</code>, <code>HttpdSite</code>,
     *                                  or <code>HttpdTomcatSite</code>
     */
    public void setHttpdTomcatContextAttributes(
        String siteName,
        String aoServer,
        String oldPath,
        String className,
        boolean cookies,
        boolean crossContext,
        String docBase,
        boolean override,
        String newPath,
        boolean privileged,
        boolean reloadable,
        boolean useNaming,
        String wrapperClass,
        int debug,
        String workDir
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "setHttpdTomcatContextAttributes(String,String,String,String,boolean,boolean,String,boolean,String,boolean,boolean,boolean,String,int,String)", null);
        try {
            HttpdSite hs=getHttpdSite(aoServer, siteName);
            HttpdTomcatSite hts=hs.getHttpdTomcatSite();
            if(hts==null) throw new IllegalArgumentException("Unable to find HttpdTomcatSite: "+siteName+" on "+aoServer);
            HttpdTomcatContext htc=hts.getHttpdTomcatContext(oldPath);
            if(htc==null) throw new IllegalArgumentException("Unable to find HttpdTomcatContext: "+siteName+" on "+aoServer+" path='"+oldPath+'\'');
            htc.setAttributes(
                className,
                cookies,
                crossContext,
                docBase,
                override,
                newPath,
                privileged,
                reloadable,
                useNaming,
                wrapperClass,
                debug,
                workDir
            );
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Sets the hostname of an <code>IPAddress</code>.
     *
     * @param  ipAddress  the <code>IPAddress</code> being modified
     * @param  hostname  the new hostname
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>IPAddress</code> or
     *					hostname is not valid format
     *
     * @see  IPAddress#setHostname
     * @see  EmailDomain#isValidFormat
     */
    public void setIPAddressHostname(
        String ipAddress,
        String aoServer,
        String net_device,
        String hostname
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "setIPAddressHostname(String,String,String,String)", null);
        try {
            if(!EmailDomain.isValidFormat(hostname)) throw new IllegalArgumentException("Invalid hostname: "+hostname);
            getIPAddress(aoServer, net_device, ipAddress).setHostname(hostname);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Sets the IP address of a DHCP-enabled <code>IPAddress</code>.
     *
     * @param  ipAddress  the pkey of the <code>IPAddress</code>
     * @param  dhcpAddress  the new IP address
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>IPAddress</code> or
     *					DHCP address is not valid format
     *
     * @see  IPAddress#setDHCPAddress
     * @see  EmailDomain#isValidFormat
     */
    public void setIPAddressDHCPAddress(
        int ipAddress,
        String dhcpAddress
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "setIPAddressDHCPAddress(int,String)", null);
        try {
            IPAddress ia=connector.ipAddresses.get(ipAddress);
            if(ia==null) throw new IllegalArgumentException("Unable to find IPAddress: "+ipAddress);
            if(!IPAddress.isValidIPAddress(dhcpAddress)) throw new IllegalArgumentException("Invalid IP address: "+ipAddress);
            ia.setDHCPAddress(dhcpAddress);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Sets the ownership of a <code>IPAddress</code>.  The <code>Package</code> may only be set
     * if the <code>IPAddress</code> is not being used by any resources.
     *
     * @param  ipAddress  the <code>IPAddress</code> being modified
     * @param  newPackage  the name of the <code>Package</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>IPAddress</code> or
     *					<code>Package</code>
     *
     * @see  IPAddress#setPackage
     * @see  #addPackage
     */
    public void setIPAddressPackage(
        String ipAddress,
        String aoServer,
        String net_device,
        String newPackage
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "setIPAddressPackage(String,String,String,String)", null);
        try {
            getIPAddress(aoServer, net_device, ipAddress).setPackage(getPackage(newPackage));
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Sets the home phone number associated with a <code>LinuxAccount</code>.
     *
     * @param  username  the username of the <code>LinuxAccount</code>
     * @param  phone  the new office phone
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>LinuxAccount</code>
     *
     * @see  LinuxAccount#setHomePhone
     * @see  #addLinuxAccount
     */
    public void setLinuxAccountHomePhone(
        String username,
        String phone
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "setLinuxAccountHomePhone(String,String)", null);
        try {
            getLinuxAccount(username).setHomePhone(phone==null||phone.length()==0?null:phone);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Sets the full name associated with a <code>LinuxAccount</code>.
     *
     * @param  username  the username of the <code>LinuxAccount</code>
     * @param  name  the new full name for the account
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if the name is not in a valid format or unable to
     *					find the <code>LinuxAccount</code>
     *
     * @see  LinuxAccount#setName
     * @see  LinuxAccount#checkGECOS
     * @see  #addLinuxAccount
     */
    public void setLinuxAccountName(
        String username,
        String name
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "setLinuxAccountName(String,String)", null);
        try {
            LinuxAccount la=getLinuxAccount(username);
            String validity=LinuxAccount.checkGECOS(name, "full name");
            if(validity!=null) throw new IllegalArgumentException(validity);
            la.setName(name);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Sets the office location associated with a <code>LinuxAccount</code>.
     *
     * @param  username  the username of the <code>LinuxAccount</code>
     * @param  location  the new office location
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>LinuxAccount</code>
     *
     * @see  LinuxAccount#setOfficeLocation
     * @see  #addLinuxAccount
     */
    public void setLinuxAccountOfficeLocation(
        String username,
        String location
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "setLinuxAccountOfficeLocation(String,String)", null);
        try {
            getLinuxAccount(username).setOfficeLocation(location==null||location.length()==0?null:location);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Sets the office phone number associated with a <code>LinuxAccount</code>.
     *
     * @param  username  the username of the <code>LinuxAccount</code>
     * @param  phone  the new office phone
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>LinuxAccount</code>
     *
     * @see  LinuxAccount#setOfficePhone
     * @see  #addLinuxAccount
     */
    public void setLinuxAccountOfficePhone(
        String username,
        String phone
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "setLinuxAccountOfficePhone(String,String)", null);
        try {
            getLinuxAccount(username).setOfficePhone(phone==null||phone.length()==0?null:phone);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Sets the password for a <code>LinuxAccount</code> by setting the password
     * for each one of its <code>LinuxServerAccount</code>s.
     *
     * @param  username  the username of the <code>LinuxAccount</code>
     * @param  password  the new password
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>LinuxAccount</code>
     *
     * @see  LinuxAccount#setPassword
     * @see  #addLinuxAccount
     */
    public void setLinuxAccountPassword(
        String username,
        String password
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "setLinuxAccountPassword(String,String)", null);
        try {
            getLinuxAccount(username).setPassword(password);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Sets the shell used by a <code>LinuxAccount</code>.
     *
     * @param  username  the username of the <code>LinuxAccount</code>
     * @param  path  the full path of the shell
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>LinuxAccount</code> or <code>Shell</code>
     *
     * @see  LinuxAccount#setShell
     * @see  #addLinuxAccount
     */
    public void setLinuxAccountShell(
        String username,
        String path
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "setLinuxAccountShell(String,String)", null);
        try {
            LinuxAccount la=getLinuxAccount(username);
            Shell sh=connector.shells.get(path);
            if(sh==null) throw new IllegalArgumentException("Unable to find Shell: "+path);
            la.setShell(sh);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Sets the password for a <code>LinuxServerAccount</code>.
     *
     * @param  username  the username of the <code>LinuxServerAccount</code>
     * @param  aoServer  the hostname of the <code>AOServer</code>
     * @param  password  the new password
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code>, <code>AOServer</code> or
     *					<code>LinuxServerAccount</code>
     *
     * @see  LinuxServerAccount#setPassword
     * @see  #addLinuxServerAccount
     */
    public void setLinuxServerAccountPassword(
        String username,
        String aoServer,
        String password
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "setLinuxServerAccountPassword(String,String,String)", null);
        try {
            getLinuxServerAccount(aoServer, username).setPassword(password);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Sets the number of days junk email is kept.
     *
     * @param  username  the username of the <code>LinuxServerAccount</code>
     * @param  aoServer  the hostname of the <code>Server</code>
     * @param  days  the new number of days, <code>-1</code> causes the junk to not be automatically removed
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>LinuxServerAccount</code>
     *
     * @see  LinuxServerAccount#setJunkEmailRetention
     * @see  #addLinuxServerAccount
     */
    public void setLinuxServerAccountJunkEmailRetention(
        String username,
        String aoServer,
        int days
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "setLinuxServerAccountJunkEmailRetention(String,String,int)", null);
        try {
            getLinuxServerAccount(aoServer, username).setJunkEmailRetention(days);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Sets the SpamAssassin integration mode for an email account.
     *
     * @param  username  the username of the <code>LinuxServerAccount</code>
     * @param  aoServer  the hostname of the <code>Server</code>
     * @param  mode      the new mode
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code>, <code>LinuxServerAccount</code>, or <code>EmailSpamAssassinIntegrationMode</code>
     *
     * @see  LinuxServerAccount#setEmailSpamAssassinIntegrationMode
     * @see  #addLinuxServerAccount
     * @see  EmailSpamAssassinIntegrationMode
     */
    public void setLinuxServerAccountSpamAssassinIntegrationMode(
        String username,
        String aoServer,
        String mode
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "setLinuxServerAccountSpamAssassinIntegrationMode(String,String,String)", null);
        try {
            getLinuxServerAccount(aoServer, username).setEmailSpamAssassinIntegrationMode(getEmailSpamAssassinIntegrationMode(mode));
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Sets the SpamAssassin required score for an email account.
     *
     * @param  username        the username of the <code>LinuxServerAccount</code>
     * @param  aoServer        the hostname of the <code>Server</code>
     * @param  required_score  the new required score
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>LinuxServerAccount</code>
     *
     * @see  LinuxServerAccount#setSpamAssassinRequiredScore
     * @see  #addLinuxServerAccount
     */
    public void setLinuxServerAccountSpamAssassinRequiredScore(
        String username,
        String aoServer,
        float required_score
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "setLinuxServerAccountSpamAssassinRequiredScore(String,String,float)", null);
        try {
            getLinuxServerAccount(aoServer, username).setSpamAssassinRequiredScore(required_score);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Sets the number of days trash email is kept.
     *
     * @param  username  the username of the <code>LinuxServerAccount</code>
     * @param  aoServer  the hostname of the <code>Server</code>
     * @param  days  the new number of days, <code>-1</code> causes the trash to not be automatically removed
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>LinuxServerAccount</code>
     *
     * @see  LinuxServerAccount#setTrashEmailRetention
     * @see  #addLinuxServerAccount
     */
    public void setLinuxServerAccountTrashEmailRetention(
        String username,
        String aoServer,
        int days
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "setLinuxServerAccountTrashEmailRetention(String,String,int)", null);
        try {
            getLinuxServerAccount(aoServer, username).setTrashEmailRetention(days);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Sets the <code>use_inbox</code> flag on a <code>LinuxServerAccount</code>.
     *
     * @param  username  the username of the <code>LinuxServerAccount</code>
     * @param  aoServer  the hostname of the <code>Server</code>
     * @param  useInbox  the new flag
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>LinuxServerAccount</code>
     *
     * @see  LinuxServerAccount#setUseInbox
     * @see  LinuxServerAccount#useInbox
     * @see  #addLinuxServerAccount
     */
    public void setLinuxServerAccountUseInbox(
        String username,
        String aoServer,
        boolean useInbox
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "setLinuxServerAccountUseInbox(String,String,boolean)", null);
        try {
            getLinuxServerAccount(aoServer, username).setUseInbox(useInbox);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Sets the info file for a <code>MajordomoList</code>.
     *
     * @param  domain  the domain of the <code>MajordomoServer</code>
     * @param  aoServer  the hostname of the <code>Server</code>
     * @param  listName  the name of the new list
     * @param  file  the new file contents
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data
     *					integrity violation occurs
     * @exception  IllegalArgumentException  if the name is not valid or unable to find the
     *                                  <code>Server</code>, code>EmailDomain</code>,
     *                                  <code>MajordomoServer</code>, or <code>MajordomoList</code>
     *
     * @see  MajordomoList#setInfoFile
     * @see  #addMajordomoList
     * @see  #removeEmailList
     */
    public void setMajordomoInfoFile(
        String domain,
        String aoServer,
        String listName,
        String file
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "setMajordomoInfoFile(String,String,String,String)", null);
        try {
            EmailDomain ed=getEmailDomain(aoServer, domain);
            MajordomoServer ms=ed.getMajordomoServer();
            if(ms==null) throw new IllegalArgumentException("Unable to find MajordomoServer: "+domain+" on "+aoServer);
            MajordomoList ml=ms.getMajordomoList(listName);
            if(ml==null) throw new IllegalArgumentException("Unable to find MajordomoList: "+listName+'@'+domain+" on "+aoServer);
            ml.setInfoFile(file);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Sets the intro file for a <code>MajordomoList</code>.
     *
     * @param  domain  the domain of the <code>MajordomoServer</code>
     * @param  aoServer  the hostname of the <code>AOServer</code>
     * @param  listName  the name of the new list
     * @param  file  the new file contents
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data
     *					integrity violation occurs
     * @exception  IllegalArgumentException  if the name is not valid or unable to find the
     *                                  <code>Server</code>, code>EmailDomain</code>,
     *                                  <code>MajordomoServer</code>, or <code>MajordomoList</code>
     *
     * @see  MajordomoList#setIntroFile
     * @see  #addMajordomoList
     * @see  #removeEmailList
     */
    public void setMajordomoIntroFile(
        String domain,
        String aoServer,
        String listName,
        String file
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "setMajordomoIntroFile(String,String,String,String)", null);
        try {
            EmailDomain ed=getEmailDomain(aoServer, domain);
            MajordomoServer ms=ed.getMajordomoServer();
            if(ms==null) throw new IllegalArgumentException("Unable to find MajordomoServer: "+domain+" on "+aoServer);
            MajordomoList ml=ms.getMajordomoList(listName);
            if(ml==null) throw new IllegalArgumentException("Unable to find MajordomoList: "+listName+'@'+domain+" on "+aoServer);
            ml.setIntroFile(file);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Sets the password for a <code>MySQLServerUser</code>.
     *
     * @param  username  the username of the <code>MySQLServerUser</code>
     * @param  aoServer  the hostname of the <code>AOServer</code>
     * @param  password  the new password
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>MySQLUser</code>,
     *					<code>Server</code>, or <code>MySQLServerUser</code>
     *
     * @see  MySQLServerUser#setPassword
     */
    public void setMySQLServerUserPassword(
        String username,
        String mysqlServer,
        String aoServer,
        String password
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "setMySQLServerUserPassword(String,String,String,String)", null);
        try {
            getMySQLServerUser(aoServer, mysqlServer, username).setPassword(password==null || password.length()==0?null:password);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Sets the password for a <code>MySQLUser</code> by settings the password for
     * all of its <code>MySQLServerUser</code>s.
     *
     * @param  username  the username of the <code>MySQLUser</code>
     * @param  password  the new password
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>MySQLUser</code>
     *
     * @see  MySQLUser#setPassword
     */
    public void setMySQLUserPassword(
        String username,
        String password
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "setMySQLUserPassword(String,String)", null);
        try {
            getMySQLUser(username).setPassword(password==null || password.length()==0?null:password);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Sets the firewall status for a <code>NetBind</code>
     *
     * @param  pkey  the pkey of the <code>NetBind</code>
     * @param  open_firewall  the new firewall state
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>NetBind</code>
     *
     * @see  NetBind#setOpenFirewall
     */
    public void setNetBindOpenFirewall(
        int pkey,
        boolean open_firewall
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "setNetBindOpenFirewall(int,boolean)", null);
        try {
            getNetBind(pkey).setOpenFirewall(open_firewall);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Sets the monitoring status for a <code>NetBind</code>
     *
     * @param  pkey  the pkey of the <code>NetBind</code>
     * @param  enabled  the new monitoring state
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>NetBind</code>
     *
     * @see  NetBind#setMonitoringEnabled
     */
    public void setNetBindMonitoringEnabled(
        int pkey,
        boolean enabled
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "setNetBindMonitoringEnabled(int,boolean)", null);
        try {
            getNetBind(pkey).setMonitoringEnabled(enabled);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Sets the password for a <code>PostgresServerUser</code>.
     *
     * @param  username  the username of the <code>PostgresServerUser</code>
     * @param  postgresServer  the name of the PostgreSQL server
     * @param  aoServer  the hostname of the <code>Server</code>
     * @param  password  the new password
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>PostgresUser</code>,
     *					<code>Server</code>, or <code>PostgresServerUser</code>
     *
     * @see  PostgresServerUser#setPassword
     */
    public void setPostgresServerUserPassword(
        String username,
        String postgresServer,
        String aoServer,
        String password
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "setPostgresServerUserPassword(String,String,String,String)", null);
        try {
            getPostgresServerUser(aoServer, postgresServer, username).setPassword(password==null || password.length()==0?null:password);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Sets the password for a <code>PostgresUser</code> by settings the password for
     * all of its <code>PostgresServerUser</code>s.
     *
     * @param  username  the username of the <code>PostgresUser</code>
     * @param  password  the new password
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>PostgresUser</code>
     *
     * @see  PostgresUser#setPassword
     */
    public void setPostgresUserPassword(
        String username,
        String password
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "setPostgresUserPassword(String,String)", null);
        try {
            getPostgresUser(username).setPassword(password==null || password.length()==0?null:password);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Sets the primary URL for a <code>HttpdSiteBind</code>.
     *
     * @param  pkey  the pkey of the <code>HttpdSiteURL</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>HttpdSiteURL</code>
     *
     * @see  HttpdSiteURL#setAsPrimary()
     */
    public void setPrimaryHttpdSiteURL(
        int pkey
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "setPrimaryHttpdSiteURL(int)", null);
        try {
            HttpdSiteURL hsu=connector.httpdSiteURLs.get(pkey);
            if(hsu==null) throw new IllegalArgumentException("Unable to find HttpdSiteURL: "+pkey);
            hsu.setAsPrimary();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Sets the primary group for a <code>LinuxAccount</code>.
     *
     * @param  group_name  the name of the <code>LinuxGroup</code>
     * @param  username  the username of the <code>LinuxAccount</code>
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if the name is not in a valid format or unable to
     *					find the <code>LinuxAccount</code>
     *
     * @see  LinuxAccount#setPrimaryLinuxGroup
     */
    public void setPrimaryLinuxGroupAccount(
        String group_name,
        String username
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "setPrimaryLinuxGroupAccount(String,String)", null);
        try {
            getLinuxAccount(username).setPrimaryLinuxGroup(getLinuxGroup(group_name));
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Sets the password for a <code>Username</code>.  This password must pass the security
     * checks provided by <code>checkUsernamePassword</code>.
     *
     * @param  username  the username
     * @param  password  the new password
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database or a data integrity
     *					violation occurs
     * @exception  IllegalArgumentException  if unable to find the <code>Username</code>
     *
     * @see  Username#setPassword
     * @see  #checkUsernamePassword
     * @see  #addUsername
     */
    public void setUsernamePassword(
        String username,
        String password
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "setUsernamePassword(String,String)", null);
        try {
            getUsername(username).setPassword(password);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Starts the Apache web server if it is not already running.
     *
     * @param  aoServer       the public hostname of the <code>AOServer</code>
     *
     * @exception  IOException  if not able to communicate with the server
     * @exception  SQLException  if not able to access the database
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
     *
     * @see  AOServer#startApache
     */
    public void startApache(String aoServer) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "startApache(String)", null);
        try {
            getAOServer(aoServer).startApache();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Starts the cron process if it is not already running.
     *
     * @param  aoServer       the public hostname of the <code>AOServer</code>
     *
     * @exception  IOException  if not able to communicate with the server
     * @exception  SQLException  if not able to access the database
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
     *
     * @see  AOServer#startCron
     */
    public void startCron(String aoServer) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "startCron(String)", null);
        try {
            getAOServer(aoServer).startCron();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Starts the distribution on a server and/or changes the setting of the user file scanning.
     *
     * @param  aoServer     the public hostname of the <code>AOServer</code> to start the scan on
     * @param  includeUser  the flag indicating whether to include user files
     *
     * @exception  IOException  if not able to communicate with the server
     * @exception  SQLException  if not able to access the database
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code>
     *
     * @see  AOServer#startDistro
     */
    public void startDistro(String aoServer, boolean includeUser) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "startDistro(String,boolean)", null);
        try {
            getAOServer(aoServer).startDistro(includeUser);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Starts and/or restarts the Tomcat or JBoss Java VM for the provided site.
     *
     * @param  siteName  the name of the site, which is the directory name under <code>/www/</code>
     * @param  aoServer    the public hostname of the <code>AOServer</code> the site is hosted on
     *
     * @return  an error message if the Java VM cannot currently be restarted or
     *          <code>null</code> on success
     *
     * @exception  IOException  if not able to communicate with the server
     * @exception  SQLException  if not able to access the database
     * @exception  IllegalArgumentException  if unable to find the <code>AOServer</code>,
     *					<code>HttpdSite</code>, or <code>HttpdTomcatSite</code>
     *
     * @see  HttpdTomcatSite#startJVM
     * @see  #addHttpdTomcatStdSite
     */
    public String startJVM(String siteName, String aoServer) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "startJVM(String,String)", null);
        try {
            HttpdSite site=getHttpdSite(aoServer, siteName);
            HttpdTomcatSite tomcatSite=site.getHttpdTomcatSite();
            if(tomcatSite==null) throw new IllegalArgumentException("HttpdSite "+siteName+" on "+aoServer+" is not a HttpdTomcatSite");
            return tomcatSite.startJVM();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Starts the MySQL database server if it is not already running.
     *
     * @param  aoServer       the public hostname of the <code>AOServer</code>
     *
     * @exception  IOException  if not able to communicate with the server
     * @exception  SQLException  if not able to access the database
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
     *
     * @see  MySQLServer#startMySQL
     */
    public void startMySQL(String mysqlServer, String aoServer) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "startMySQL(String,String)", null);
        try {
            getMySQLServer(aoServer, mysqlServer).startMySQL();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Starts the PostgreSQL database server if it is not already running.
     *
     * @param  postgresServer  the name of the PostgreSQL server
     * @param  aoServer  the public hostname of the <code>AOServer</code>
     *
     * @exception  IOException  if not able to communicate with the server
     * @exception  SQLException  if not able to access the database
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
     *
     * @see  PostgresServer#startPostgreSQL
     */
    public void startPostgreSQL(String postgresServer, String aoServer) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "startPostgreSQL(String,String)", null);
        try {
            getPostgresServer(aoServer, postgresServer).startPostgreSQL();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Starts the X Font Server if it is not already running.
     *
     * @param  aoServer       the public hostname of the <code>AOServer</code>
     *
     * @exception  IOException  if not able to communicate with the server
     * @exception  SQLException  if not able to access the database
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
     *
     * @see  AOServer#startXfs
     */
    public void startXfs(String aoServer) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "startXfs(String)", null);
        try {
            getAOServer(aoServer).startXfs();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Starts the X Virtual Frame Buffer if it is not already running.
     *
     * @param  aoServer       the public hostname of the <code>AOServer</code>
     *
     * @exception  IOException  if not able to communicate with the server
     * @exception  SQLException  if not able to access the database
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
     *
     * @see  AOServer#startXvfb
     */
    public void startXvfb(String aoServer) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "startXvfb(String)", null);
        try {
            getAOServer(aoServer).startXvfb();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Stops the Apache web server if it is running.
     *
     * @param  aoServer       the public hostname of the <code>AOServer</code>
     *
     * @exception  IOException  if not able to communicate with the server
     * @exception  SQLException  if not able to access the database
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
     *
     * @see  AOServer#stopApache
     */
    public void stopApache(String aoServer) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "stopApache(String)", null);
        try {
            getAOServer(aoServer).stopApache();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Stops the cron daemon if it is running.
     *
     * @param  aoServer       the public hostname of the <code>AOServer</code>
     *
     * @exception  IOException  if not able to communicate with the server
     * @exception  SQLException  if not able to access the database
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
     *
     * @see  AOServer#stopCron
     */
    public void stopCron(String aoServer) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "stopCron(String)", null);
        try {
            getAOServer(aoServer).stopCron();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Stops the Tomcat or JBoss Java VM for the provided site.
     *
     * @param  siteName  the name of the site, which is the directory name under <code>/www/</code>
     * @param  aoServer    the public hostname of the <code>AOServer</code> the site is hosted on
     *
     * @return  an error message if the Java VM cannot currently be stopped
     *          <code>null</code> on success
     *
     * @exception  IOException  if not able to communicate with the server
     * @exception  SQLException  if not able to access the database
     * @exception  IllegalArgumentException  if unable to find the <code>AOServer</code>,
     *					<code>HttpdSite</code>, or <code>HttpdTomcatSite</code>
     *
     * @see  HttpdTomcatSite#stopJVM
     * @see  #addHttpdTomcatStdSite
     */
    public String stopJVM(String siteName, String aoServer) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "stopJVM(String,String)", null);
        try {
            HttpdSite site=getHttpdSite(aoServer, siteName);
            HttpdTomcatSite tomcatSite=site.getHttpdTomcatSite();
            if(tomcatSite==null) throw new IllegalArgumentException("HttpdSite "+siteName+" on "+aoServer+" is not a HttpdTomcatSite");
            return tomcatSite.stopJVM();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Stops the MySQL database server if it is running.
     *
     * @param  aoServer       the public hostname of the <code>AOServer</code>
     *
     * @exception  IOException  if not able to communicate with the server
     * @exception  SQLException  if not able to access the database
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
     *
     * @see  MySQLServer#stopMySQL
     */
    public void stopMySQL(String mysqlServer, String aoServer) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "stopMySQL(String,String)", null);
        try {
            getMySQLServer(aoServer, mysqlServer).stopMySQL();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Stops the PostgreSQL database server if it is running.
     *
     * @param  postgresServer  the name of the PostgreSQL server
     * @param  aoServer  the public hostname of the <code>AOServer</code>
     *
     * @exception  IOException  if not able to communicate with the server
     * @exception  SQLException  if not able to access the database
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
     *
     * @see  PostgresServer#stopPostgreSQL
     */
    public void stopPostgreSQL(String postgresServer, String aoServer) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "stopPostgreSQL(String,String)", null);
        try {
            getPostgresServer(aoServer, postgresServer).stopPostgreSQL();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Stops the X Font Server if it is running.
     *
     * @param  aoServer       the public hostname of the <code>AOServer</code>
     *
     * @exception  IOException  if not able to communicate with the server
     * @exception  SQLException  if not able to access the database
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
     *
     * @see  AOServer#stopXfs
     */
    public void stopXfs(String aoServer) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "stopXfs(String)", null);
        try {
            getAOServer(aoServer).stopXfs();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Stops the X Virtual Frame Buffer if it is running.
     *
     * @param  aoServer       the public hostname of the <code>AOServer</code>
     *
     * @exception  IOException  if not able to communicate with the server
     * @exception  SQLException  if not able to access the database
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
     *
     * @see  AOServer#stopXvfb
     */
    public void stopXvfb(String aoServer) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "stopXvfb(String)", null);
        try {
            getAOServer(aoServer).stopXvfb();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Updates a <code>HttpdTomcatContext</code> data source.
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code>, <code>HttpdSite</code>,
     *                                  <code>HttpdTomcatSite</code> or <code>HttpdTomcatContext</code>.
     */
    public void updateHttpdTomcatDataSource(
        String siteName,
        String aoServer,
        String path,
        String oldName,
        String newName,
        String driverClassName,
        String url,
        String username,
        String password,
        int maxActive,
        int maxIdle,
        int maxWait,
        String validationQuery
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "updateHttpdTomcatDataSource(String,String,String,String,String,String,String,String,String,int,int,int,String)", null);
        try {
            HttpdSite hs=getHttpdSite(aoServer, siteName);
            HttpdTomcatSite hts=hs.getHttpdTomcatSite();
            if(hts==null) throw new IllegalArgumentException("Unable to find HttpdTomcatSite: "+siteName+" on "+aoServer);
            HttpdTomcatContext htc=hts.getHttpdTomcatContext(path);
            if(htc==null) throw new IllegalArgumentException("Unable to find HttpdTomcatContext: "+siteName+" on "+aoServer+" path='"+path+'\'');
            HttpdTomcatDataSource htds=htc.getHttpdTomcatDataSource(oldName);
            if(htds==null) throw new IllegalArgumentException("Unable to find HttpdTomcatDataSource: "+siteName+" on "+aoServer+" path='"+path+"' name='"+oldName+'\'');
            htds.update(
                newName,
                driverClassName,
                url,
                username,
                password,
                maxActive,
                maxIdle,
                maxWait,
                validationQuery
            );
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Updates a <code>HttpdTomcatContext</code> parameter.
     *
     * @exception  IOException  if unable to contact the server
     * @exception  SQLException  if unable to access the database
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code>, <code>HttpdSite</code>,
     *                                  <code>HttpdTomcatSite</code> or <code>HttpdTomcatContext</code>.
     */
    public void updateHttpdTomcatParameter(
        String siteName,
        String aoServer,
        String path,
        String oldName,
        String newName,
        String value,
        boolean override,
        String description
    ) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "updateHttpdTomcatParameter(String,String,String,String,String,String,boolean,String)", null);
        try {
            HttpdSite hs=getHttpdSite(aoServer, siteName);
            HttpdTomcatSite hts=hs.getHttpdTomcatSite();
            if(hts==null) throw new IllegalArgumentException("Unable to find HttpdTomcatSite: "+siteName+" on "+aoServer);
            HttpdTomcatContext htc=hts.getHttpdTomcatContext(path);
            if(htc==null) throw new IllegalArgumentException("Unable to find HttpdTomcatContext: "+siteName+" on "+aoServer+" path='"+path+'\'');
            HttpdTomcatParameter htp=htc.getHttpdTomcatParameter(oldName);
            if(htp==null) throw new IllegalArgumentException("Unable to find HttpdTomcatParameter: "+siteName+" on "+aoServer+" path='"+path+"' name='"+oldName+'\'');
            htp.update(
                newName,
                value,
                override,
                description
            );
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Waits for any processing or pending updates of the Apache configurations to complete.
     *
     * @param  aoServer  the hostname of the <code>AOServer</code> to wait for
     *
     * @exception  IOException  if not able to communicate with the server
     * @exception  SQLException  if not able to access the database
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
     *
     * @see  AOServer#waitForHttpdSiteRebuild
     * @see  #addHttpdTomcatStdSite
     */
    public void waitForHttpdSiteRebuild(String aoServer) {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "waitForHttpdSiteRebuild(String)", null);
        try {
            getAOServer(aoServer).waitForHttpdSiteRebuild();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Waits for any processing or pending updates of the Linux account configurations to complete.
     *
     * @param  aoServer  the hostname of the <code>AOServer</code> to wait for
     *
     * @exception  IOException  if not able to communicate with the server
     * @exception  SQLException  if not able to access the database
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
     *
     * @see  AOServer#waitForLinuxAccountRebuild
     */
    public void waitForLinuxAccountRebuild(String aoServer) {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "waitForLinuxAccountRebuild(String)", null);
        try {
            getAOServer(aoServer).waitForLinuxAccountRebuild();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Waits for any processing or pending updates of the MySQL configurations to complete.
     *
     * @param  aoServer  the hostname of the <code>AOServer</code> to wait for
     *
     * @exception  IOException  if not able to communicate with the server
     * @exception  SQLException  if not able to access the database
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
     *
     * @see  AOServer#waitForMySQLDatabaseRebuild
     */
    public void waitForMySQLDatabaseRebuild(String aoServer) {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "waitForMySQLDatabaseRebuild(String)", null);
        try {
            getAOServer(aoServer).waitForMySQLDatabaseRebuild();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Waits for any processing or pending updates of the MySQL configurations to complete.
     *
     * @param  aoServer  the hostname of the <code>AOServer</code> to wait for
     *
     * @exception  IOException  if not able to communicate with the server
     * @exception  SQLException  if not able to access the database
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
     *
     * @see  AOServer#waitForMySQLDBUserRebuild
     */
    public void waitForMySQLDBUserRebuild(String aoServer) {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "waitForMySQLDBUserRebuild(String)", null);
        try {
            getAOServer(aoServer).waitForMySQLDBUserRebuild();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Waits for any processing or pending updates of the MySQL server configurations to complete.
     *
     * @param  aoServer  the hostname of the <code>AOServer</code> to wait for
     *
     * @exception  IOException  if not able to communicate with the server
     * @exception  SQLException  if not able to access the database
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
     *
     * @see  AOServer#waitForMySQLServerRebuild
     */
    public void waitForMySQLServerRebuild(String aoServer) {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "waitForMySQLServerRebuild(String)", null);
        try {
            getAOServer(aoServer).waitForMySQLServerRebuild();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Waits for any processing or pending updates of the MySQL configurations to complete.
     *
     * @param  aoServer  the hostname of the <code>AOServer</code> to wait for
     *
     * @exception  IOException  if not able to communicate with the server
     * @exception  SQLException  if not able to access the database
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
     *
     * @see  AOServer#waitForMySQLUserRebuild
     */
    public void waitForMySQLUserRebuild(String aoServer) {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "waitForMySQLUserRebuild(String)", null);
        try {
            getAOServer(aoServer).waitForMySQLUserRebuild();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Waits for any processing or pending updates of the PostgreSQL configurations to complete.
     *
     * @param  aoServer  the hostname of the <code>AOServer</code> to wait for
     *
     * @exception  IOException  if not able to communicate with the server
     * @exception  SQLException  if not able to access the database
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
     *
     * @see  AOServer#waitForPostgresDatabaseRebuild
     */
    public void waitForPostgresDatabaseRebuild(String aoServer) {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "waitForPostgresDatabaseRebuild(String)", null);
        try {
            getAOServer(aoServer).waitForPostgresDatabaseRebuild();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Waits for any processing or pending updates of the PostgreSQL server configurations to complete.
     *
     * @param  aoServer  the hostname of the <code>AOServer</code> to wait for
     *
     * @exception  IOException  if not able to communicate with the server
     * @exception  SQLException  if not able to access the database
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
     *
     * @see  AOServer#waitForPostgresServerRebuild
     */
    public void waitForPostgresServerRebuild(String aoServer) {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "waitForPostgresServerRebuild(String)", null);
        try {
            getAOServer(aoServer).waitForPostgresServerRebuild();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Waits for any processing or pending updates of the PostgreSQL configurations to complete.
     *
     * @param  aoServer  the hostname of the <code>AOServer</code> to wait for
     *
     * @exception  IOException  if not able to communicate with the server
     * @exception  SQLException  if not able to access the database
     * @exception  IllegalArgumentException  if unable to find the <code>Server</code> or <code>AOServer</code>
     *
     * @see  AOServer#waitForPostgresUserRebuild
     */
    public void waitForPostgresUserRebuild(String aoServer) {
        Profiler.startProfile(Profiler.FAST, SimpleAOClient.class, "waitForPostgresUserRebuild(String)", null);
        try {
            getAOServer(aoServer).waitForPostgresUserRebuild();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
}
