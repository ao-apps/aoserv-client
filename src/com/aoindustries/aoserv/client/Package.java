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
import java.sql.Timestamp;
import java.util.List;

/**
 * A <code>Package</code> is a set of resources and its associated price.
 * A <code>Business</code> may multiple <code>Package</code>s, each with
 * their own monthly fee and sets of resources such as web sites, databases
 * and users.  Security is not maintained between <code>Package</code>s,
 * only between <code>Business</code>es.  If intra-account security is
 * required, please use child <code>Business</code>es.
 *
 * @see  Business
 * @see  PackageDefinition
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class Package extends CachedObjectIntegerKey<Package> implements Disablable {

    static final int
        COLUMN_PKEY=0,
        COLUMN_NAME=1,
        COLUMN_ACCOUNTING=2,
        COLUMN_PACKAGE_DEFINITION=3
    ;
    static final String COLUMN_NAME_name = "name";

    /**
     * The default inbound email burst before rate limiting.
     */
    public static final int DEFAULT_EMAIL_IN_BURST = 1000;

    /**
     * The default sustained inbound email rate in emails/second.
     */
    public static final float DEFAULT_EMAIL_IN_RATE = 10f;

    /**
     * The default outbound email burst before rate limiting.
     */
    public static final int DEFAULT_EMAIL_OUT_BURST = 200;

    /**
     * The default sustained outbound email rate in emails/second.
     */
    public static final float DEFAULT_EMAIL_OUT_RATE = .2f;

    /**
     * The default relay email burst before rate limiting.
     */
    public static final int DEFAULT_EMAIL_RELAY_BURST = 100;

    /**
     * The default sustained relay email rate in emails/second.
     */
    public static final float DEFAULT_EMAIL_RELAY_RATE = .1f;

    String name;
    String accounting;
    int package_definition;
    private long created;
    private String created_by;
    int disable_log;
    private int email_in_burst;
    private float email_in_rate;
    private int email_out_burst;
    private float email_out_rate;
    private int email_relay_burst;
    private float email_relay_rate;

    public void addDNSZone(String zone, String ip, int ttl) throws IOException, SQLException {
	    table.connector.getDnsZones().addDNSZone(this, zone, ip, ttl);
    }
    
    public int addEmailSmtpRelay(AOServer aoServer, String host, EmailSmtpRelayType type, long duration) throws IOException, SQLException {
        return table.connector.getEmailSmtpRelays().addEmailSmtpRelay(this, aoServer, host, type, duration);
    }

    public void addLinuxGroup(String name, LinuxGroupType type) throws IOException, SQLException {
	addLinuxGroup(name, type.pkey);
    }

    public void addLinuxGroup(String name, String type) throws IOException, SQLException {
	    table.connector.getLinuxGroups().addLinuxGroup(name, this, type);
    }

    public void addUsername(String username) throws IOException, SQLException {
	    table.connector.getUsernames().addUsername(this, username);
    }

    public boolean canDisable() throws IOException, SQLException {
        // Is already disabled
        if(disable_log!=-1) return false;

        // Can only disabled when all dependent objects are already disabled
        for(HttpdSharedTomcat hst : getHttpdSharedTomcats()) if(hst.disable_log==-1) return false;
        for(EmailPipe ep : getEmailPipes()) if(ep.disable_log==-1) return false;
        for(CvsRepository cr : getCvsRepositories()) if(cr.disable_log==-1) return false;
        for(Username un : getUsernames()) if(un.disable_log==-1) return false;
        for(HttpdSite hs : getHttpdSites()) if(hs.disable_log==-1) return false;
        for(EmailList el : getEmailLists()) if(el.disable_log==-1) return false;
        for(EmailSmtpRelay ssr : getEmailSmtpRelays()) if(ssr.disable_log==-1) return false;

        return true;
    }

    public boolean canEnable() throws SQLException, IOException {
        DisableLog dl=getDisableLog();
        if(dl==null) return false;
        else return dl.canEnable() && getBusiness().disable_log==-1;
    }

    public void disable(DisableLog dl) throws IOException, SQLException {
        table.connector.requestUpdateIL(AOServProtocol.CommandID.DISABLE, SchemaTable.TableID.PACKAGES, dl.pkey, name);
    }
    
    public void enable() throws IOException, SQLException {
        table.connector.requestUpdateIL(AOServProtocol.CommandID.ENABLE, SchemaTable.TableID.PACKAGES, name);
    }

    public List<BackupReport> getBackupReports() throws IOException, SQLException {
	return table.connector.getBackupReports().getBackupReports(this);
    }

    public Business getBusiness() throws SQLException {
	Business accountingObject = table.connector.getBusinesses().get(accounting);
	if (accountingObject == null) throw new SQLException("Unable to find Business: " + accounting);
	return accountingObject;
    }

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_NAME: return name;
            case COLUMN_ACCOUNTING: return accounting;
            case COLUMN_PACKAGE_DEFINITION: return Integer.valueOf(package_definition);
            case 4: return new java.sql.Date(created);
            case 5: return created_by;
            case 6: return disable_log==-1?null:Integer.valueOf(disable_log);
            case 7: return email_in_burst==-1 ? null : Integer.valueOf(email_in_burst);
            case 8: return Float.isNaN(email_in_rate) ? null : Float.valueOf(email_in_rate);
            case 9: return email_out_burst==-1 ? null : Integer.valueOf(email_out_burst);
            case 10: return Float.isNaN(email_out_rate) ? null : Float.valueOf(email_out_rate);
            case 11: return email_relay_burst==-1 ? null : Integer.valueOf(email_relay_burst);
            case 12: return Float.isNaN(email_relay_rate) ? null : Float.valueOf(email_relay_rate);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public long getCreated() {
	return created;
    }

    public BusinessAdministrator getCreatedBy() throws SQLException {
	BusinessAdministrator createdByObject = table.connector.getUsernames().get(created_by).getBusinessAdministrator();
	if (createdByObject == null) throw new SQLException("Unable to find BusinessAdministrator: " + created_by);
	return createdByObject;
    }

    public List<CvsRepository> getCvsRepositories() throws IOException, SQLException {
	return table.connector.getCvsRepositories().getCvsRepositories(this);
    }

    public DisableLog getDisableLog() throws SQLException, IOException {
        if(disable_log==-1) return null;
        DisableLog obj=table.connector.getDisableLogs().get(disable_log);
        if(obj==null) throw new SQLException("Unable to find DisableLog: "+disable_log);
        return obj;
    }
    
    /**
     * Gets the inbound burst limit for emails, the number of emails that may be sent before limiting occurs.
     * A value of <code>-1</code> indicates unlimited.
     */
    public int getEmailInBurst() {
        return email_in_burst;
    }

    /**
     * Gets the inbound sustained email rate in emails/second.
     * A value of <code>Float.NaN</code> indicates unlimited.
     */
    public float getEmailInRate() {
        return email_in_rate;
    }

    /**
     * Gets the outbound burst limit for emails, the number of emails that may be sent before limiting occurs.
     * A value of <code>-1</code> indicates unlimited.
     */
    public int getEmailOutBurst() {
        return email_out_burst;
    }

    /**
     * Gets the outbound sustained email rate in emails/second.
     * A value of <code>Float.NaN</code> indicates unlimited.
     */
    public float getEmailOutRate() {
        return email_out_rate;
    }

    /**
     * Gets the relay burst limit for emails, the number of emails that may be sent before limiting occurs.
     * A value of <code>-1</code> indicates unlimited.
     */
    public int getEmailRelayBurst() {
        return email_relay_burst;
    }

    /**
     * Gets the relay sustained email rate in emails/second.
     * A value of <code>Float.NaN</code> indicates unlimited.
     */
    public float getEmailRelayRate() {
        return email_relay_rate;
    }

    public List<DNSZone> getDNSZones() throws IOException, SQLException {
	return table.connector.getDnsZones().getDNSZones(this);
    }

    public List<EmailList> getEmailLists() throws IOException, SQLException {
	return table.connector.getEmailLists().getEmailLists(this);
    }

    public List<EmailPipe> getEmailPipes() throws IOException, SQLException {
	return table.connector.getEmailPipes().getEmailPipes(this);
    }

    public List<HttpdSharedTomcat> getHttpdSharedTomcats() throws IOException, SQLException {
	return table.connector.getHttpdSharedTomcats().getHttpdSharedTomcats(this);
    }

    public List<HttpdServer> getHttpdServers() throws IOException, SQLException {
	return table.connector.getHttpdServers().getHttpdServers(this);
    }

    public List<HttpdSite> getHttpdSites() throws IOException, SQLException {
	return table.connector.getHttpdSites().getHttpdSites(this);
    }

    public List<IPAddress> getIPAddresses() throws IOException, SQLException {
	return table.connector.getIpAddresses().getIPAddresses(this);
    }

    public List<LinuxGroup> getLinuxGroups() throws IOException, SQLException {
	return table.connector.getLinuxGroups().getLinuxGroups(this);
    }

    public List<MySQLDatabase> getMySQLDatabases() throws IOException, SQLException {
	return table.connector.getMysqlDatabases().getMySQLDatabases(this);
    }

    public List<FailoverMySQLReplication> getFailoverMySQLReplications() throws IOException, SQLException {
        return table.connector.getFailoverMySQLReplications().getFailoverMySQLReplications(this);
    }

    public List<MySQLServer> getMySQLServers() throws IOException, SQLException {
	return table.connector.getMysqlServers().getMySQLServers(this);
    }

    public List<MySQLUser> getMySQLUsers() throws IOException, SQLException {
	return table.connector.getMysqlUsers().getMySQLUsers(this);
    }

    public String getName() {
	return name;
    }

    public List<NetBind> getNetBinds() throws IOException, SQLException {
	return table.connector.getNetBinds().getNetBinds(this);
    }

    public List<NetBind> getNetBinds(IPAddress ip) throws IOException, SQLException {
	return table.connector.getNetBinds().getNetBinds(this, ip);
    }

    public PackageDefinition getPackageDefinition() throws SQLException, IOException {
        PackageDefinition pd = table.connector.getPackageDefinitions().get(package_definition);
        if(pd == null) throw new SQLException("Unable to find PackageDefinition: "+package_definition);
        return pd;
    }

    public List<PostgresDatabase> getPostgresDatabases() throws IOException, SQLException {
	return table.connector.getPostgresDatabases().getPostgresDatabases(this);
    }

    public List<PostgresUser> getPostgresUsers() throws SQLException, IOException {
	return table.connector.getPostgresUsers().getPostgresUsers(this);
    }

    public Server getServer(String name) throws IOException, SQLException {
	return table.connector.getServers().getServer(this, name);
    }

    public List<Server> getServers() throws IOException, SQLException {
	return table.connector.getServers().getServers(this);
    }

    public List<EmailDomain> getEmailDomains() throws IOException, SQLException {
	return table.connector.getEmailDomains().getEmailDomains(this);
    }

    public List<EmailSmtpRelay> getEmailSmtpRelays() throws IOException, SQLException {
	return table.connector.getEmailSmtpRelays().getEmailSmtpRelays(this);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.PACKAGES;
    }

    public List<Username> getUsernames() throws IOException, SQLException {
	return table.connector.getUsernames().getUsernames(this);
    }

    public void init(ResultSet result) throws SQLException {
        int pos = 1;
        pkey = result.getInt(pos++);
	name = result.getString(pos++);
	accounting = result.getString(pos++);
        package_definition = result.getInt(pos++);
	Timestamp temp5 = result.getTimestamp(pos++);
	created = temp5 == null ? -1 : temp5.getTime();
	created_by = result.getString(pos++);
        disable_log=result.getInt(pos++);
        if(result.wasNull()) disable_log = -1;
        email_in_burst=result.getInt(pos++);
        if(result.wasNull()) email_in_burst = -1;
        email_in_rate=result.getFloat(pos++);
        if(result.wasNull()) email_in_rate = Float.NaN;
        email_out_burst=result.getInt(pos++);
        if(result.wasNull()) email_out_burst = -1;
        email_out_rate=result.getFloat(pos++);
        if(result.wasNull()) email_out_rate = Float.NaN;
        email_relay_burst=result.getInt(pos++);
        if(result.wasNull()) email_relay_burst = -1;
        email_relay_rate=result.getFloat(pos++);
        if(result.wasNull()) email_relay_rate = Float.NaN;
    }

    public static boolean isValidPackageName(String packageName) {
	return Business.isValidAccounting(packageName);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
	name=in.readUTF().intern();
	accounting=in.readUTF().intern();
        package_definition=in.readCompressedInt();
	created=in.readLong();
	created_by=in.readUTF().intern();
        disable_log=in.readCompressedInt();
        email_in_burst=in.readCompressedInt();
        email_in_rate=in.readFloat();
        email_out_burst=in.readCompressedInt();
        email_out_rate=in.readFloat();
        email_relay_burst=in.readCompressedInt();
        email_relay_rate=in.readFloat();
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeCompressedInt(pkey);
	out.writeUTF(name);
	out.writeUTF(accounting);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_122)<=0) {
            out.writeUTF("unknown");
            out.writeCompressedInt(0);
        }
        if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_123)>=0) {
            out.writeCompressedInt(package_definition);
        }
	out.writeLong(created);
	out.writeUTF(created_by);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_122)<=0) {
            out.writeCompressedInt(-1);
            out.writeCompressedInt(200);
            out.writeCompressedInt(-1);
            out.writeCompressedInt(100);
        }
        if(version.compareTo(AOServProtocol.Version.VERSION_1_30)<=0) {
            out.writeCompressedInt(256);
            out.writeLong(64*1024*1024);
            out.writeCompressedInt(256);
            out.writeLong(64*1024*1024);
        }
        out.writeCompressedInt(disable_log);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_24)>=0) {
            out.writeCompressedInt(email_in_burst);
            out.writeFloat(email_in_rate);
            out.writeCompressedInt(email_out_burst);
            out.writeFloat(email_out_rate);
            out.writeCompressedInt(email_relay_burst);
            out.writeFloat(email_relay_rate);
        }        
    }
}
