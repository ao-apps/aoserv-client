package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.sql.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
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

    /**
     * The default daily SMTP limit for a new package.
     */
    public static final int DEFAULT_DAILY_SMTP_IN_LIMIT=256;
    
    /**
     * The default daily SMTP bandwidth for a new package.
     */
    public static final long DEFAULT_DAILY_SMTP_IN_BANDWIDTH_LIMIT=64*1024*1024;

    /**
     * The default daily SMTP limit for a new package.
     */
    public static final int DEFAULT_DAILY_SMTP_OUT_LIMIT=256;
    
    /**
     * The default daily SMTP bandwidth for a new package.
     */
    public static final long DEFAULT_DAILY_SMTP_OUT_BANDWIDTH_LIMIT=64*1024*1024;

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
    private int daily_smtp_in_limit;
    private long daily_smtp_in_bandwidth_limit;
    private int daily_smtp_out_limit;
    private long daily_smtp_out_bandwidth_limit;
    int disable_log;
    private int email_in_burst;
    private float email_in_rate;
    private int email_out_burst;
    private float email_out_rate;
    private int email_relay_burst;
    private float email_relay_rate;

    public void addDNSZone(String zone, String ip, int ttl) {
	table.connector.dnsZones.addDNSZone(this, zone, ip, ttl);
    }
    
    public int addEmailSmtpRelay(AOServer aoServer, String host, EmailSmtpRelayType type, long duration) {
        return table.connector.emailSmtpRelays.addEmailSmtpRelay(this, aoServer, host, type, duration);
    }

    public void addLinuxGroup(String name, LinuxGroupType type) {
	addLinuxGroup(name, type.pkey);
    }

    public void addLinuxGroup(String name, String type) {
	table.connector.linuxGroups.addLinuxGroup(name, this, type);
    }

    public int addSendmailSmtpStat(long date, AOServer ao, int in_count, long in_bandwidth, int out_count, long out_bandwidth) {
        return table.connector.sendmailSmtpStats.addSendmailSmtpStat(this, date, ao, in_count, in_bandwidth, out_count, out_bandwidth);
    }

    public void addUsername(String username) {
	table.connector.usernames.addUsername(this, username);
    }

    public boolean canDisable() {
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

    public boolean canEnable() {
        DisableLog dl=getDisableLog();
        if(dl==null) return false;
        else return dl.canEnable() && getBusiness().disable_log==-1;
    }

    public void disable(DisableLog dl) {
        table.connector.requestUpdateIL(AOServProtocol.CommandID.DISABLE, SchemaTable.TableID.PACKAGES, dl.pkey, name);
    }
    
    public void enable() {
        table.connector.requestUpdateIL(AOServProtocol.CommandID.ENABLE, SchemaTable.TableID.PACKAGES, name);
    }

    public List<BackupReport> getBackupReports() {
	return table.connector.backupReports.getBackupReports(this);
    }

    public Business getBusiness() {
	Business accountingObject = table.connector.businesses.get(accounting);
	if (accountingObject == null) throw new WrappedException(new SQLException("Unable to find Business: " + accounting));
	return accountingObject;
    }

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_NAME: return name;
            case COLUMN_ACCOUNTING: return accounting;
            case COLUMN_PACKAGE_DEFINITION: return Integer.valueOf(package_definition);
            case 4: return new java.sql.Date(created);
            case 5: return created_by;
            case 6: return Integer.valueOf(daily_smtp_in_limit);
            case 7: return Long.valueOf(daily_smtp_in_bandwidth_limit);
            case 8: return Integer.valueOf(daily_smtp_out_limit);
            case 9: return Long.valueOf(daily_smtp_out_bandwidth_limit);
            case 10: return disable_log==-1?null:Integer.valueOf(disable_log);
            case 11: return email_in_burst==-1 ? null : Integer.valueOf(email_in_burst);
            case 12: return Float.isNaN(email_in_rate) ? null : Float.valueOf(email_in_rate);
            case 13: return email_out_burst==-1 ? null : Integer.valueOf(email_out_burst);
            case 14: return Float.isNaN(email_out_rate) ? null : Float.valueOf(email_out_rate);
            case 15: return email_relay_burst==-1 ? null : Integer.valueOf(email_relay_burst);
            case 16: return Float.isNaN(email_relay_rate) ? null : Float.valueOf(email_relay_rate);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public long getCreated() {
	return created;
    }

    public BusinessAdministrator getCreatedBy() {
	BusinessAdministrator createdByObject = table.connector.usernames.get(created_by).getBusinessAdministrator();
	if (createdByObject == null) throw new WrappedException(new SQLException("Unable to find BusinessAdministrator: " + created_by));
	return createdByObject;
    }

    public List<CvsRepository> getCvsRepositories() {
	return table.connector.cvsRepositories.getCvsRepositories(this);
    }

    public int getDailySmtpInLimit() {
        return daily_smtp_in_limit;
    }

    public long getDailySmtpInBandwidthLimit() {
        return daily_smtp_in_bandwidth_limit;
    }

    public int getDailySmtpOutLimit() {
        return daily_smtp_out_limit;
    }

    public long getDailySmtpOutBandwidthLimit() {
        return daily_smtp_out_bandwidth_limit;
    }

    public DisableLog getDisableLog() {
        if(disable_log==-1) return null;
        DisableLog obj=table.connector.disableLogs.get(disable_log);
        if(obj==null) throw new WrappedException(new SQLException("Unable to find DisableLog: "+disable_log));
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

    public List<DNSZone> getDNSZones() {
	return table.connector.dnsZones.getDNSZones(this);
    }

    public List<EmailList> getEmailLists() {
	return table.connector.emailLists.getEmailLists(this);
    }

    public List<EmailPipe> getEmailPipes() {
	return table.connector.emailPipes.getEmailPipes(this);
    }

    public List<HttpdSharedTomcat> getHttpdSharedTomcats() {
	return table.connector.httpdSharedTomcats.getHttpdSharedTomcats(this);
    }

    public List<HttpdServer> getHttpdServers() {
	return table.connector.httpdServers.getHttpdServers(this);
    }

    public List<HttpdSite> getHttpdSites() {
	return table.connector.httpdSites.getHttpdSites(this);
    }

    public List<IPAddress> getIPAddresses() {
	return table.connector.ipAddresses.getIPAddresses(this);
    }

    public List<LinuxGroup> getLinuxGroups() {
	return table.connector.linuxGroups.getLinuxGroups(this);
    }

    public List<MySQLDatabase> getMySQLDatabases() {
	return table.connector.mysqlDatabases.getMySQLDatabases(this);
    }

    public List<FailoverMySQLReplication> getFailoverMySQLReplications() {
        return table.connector.failoverMySQLReplications.getFailoverMySQLReplications(this);
    }

    public List<MySQLServer> getMySQLServers() {
	return table.connector.mysqlServers.getMySQLServers(this);
    }

    public List<MySQLUser> getMySQLUsers() {
	return table.connector.mysqlUsers.getMySQLUsers(this);
    }

    public String getName() {
	return name;
    }

    public List<NetBind> getNetBinds() {
	return table.connector.netBinds.getNetBinds(this);
    }

    public List<NetBind> getNetBinds(IPAddress ip) {
	return table.connector.netBinds.getNetBinds(this, ip);
    }

    public PackageDefinition getPackageDefinition() {
        PackageDefinition pd = table.connector.packageDefinitions.get(package_definition);
        if(pd == null) throw new WrappedException(new SQLException("Unable to find PackageDefinition: "+package_definition));
        return pd;
    }

    public List<PostgresDatabase> getPostgresDatabases() {
	return table.connector.postgresDatabases.getPostgresDatabases(this);
    }

    public List<PostgresUser> getPostgresUsers() {
	return table.connector.postgresUsers.getPostgresUsers(this);
    }

    public List<EmailDomain> getEmailDomains() {
	return table.connector.emailDomains.getEmailDomains(this);
    }

    public List<EmailSmtpRelay> getEmailSmtpRelays() {
	return table.connector.emailSmtpRelays.getEmailSmtpRelays(this);
    }

    public List<SendmailSmtpStat> getSendmailSmtpStats() {
	return table.connector.sendmailSmtpStats.getSendmailSmtpStats(this);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.PACKAGES;
    }

    public List<Username> getUsernames() {
	return table.connector.usernames.getUsernames(this);
    }

    void initImpl(ResultSet result) throws SQLException {
        int pos = 1;
        pkey = result.getInt(pos++);
	name = result.getString(pos++);
	accounting = result.getString(pos++);
        package_definition = result.getInt(pos++);
	Timestamp temp5 = result.getTimestamp(pos++);
	created = temp5 == null ? -1 : temp5.getTime();
	created_by = result.getString(pos++);
        daily_smtp_in_limit=result.getInt(pos++);
        daily_smtp_in_bandwidth_limit=result.getLong(pos++);
        daily_smtp_out_limit=result.getInt(pos++);
        daily_smtp_out_bandwidth_limit=result.getLong(pos++);
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
        daily_smtp_in_limit=in.readCompressedInt();
        daily_smtp_in_bandwidth_limit=in.readLong();
        daily_smtp_out_limit=in.readCompressedInt();
        daily_smtp_out_bandwidth_limit=in.readLong();
        disable_log=in.readCompressedInt();
        email_in_burst=in.readCompressedInt();
        email_in_rate=in.readFloat();
        email_out_burst=in.readCompressedInt();
        email_out_rate=in.readFloat();
        email_relay_burst=in.readCompressedInt();
        email_relay_rate=in.readFloat();
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        out.writeCompressedInt(pkey);
	out.writeUTF(name);
	out.writeUTF(accounting);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_0_A_122)<=0) {
            out.writeUTF("unknown");
            out.writeCompressedInt(0);
        }
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_0_A_123)>=0) {
            out.writeCompressedInt(package_definition);
        }
	out.writeLong(created);
	out.writeUTF(created_by);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_0_A_122)<=0) {
            out.writeCompressedInt(-1);
            out.writeCompressedInt(200);
            out.writeCompressedInt(-1);
            out.writeCompressedInt(100);
        }
        out.writeCompressedInt(daily_smtp_in_limit);
        out.writeLong(daily_smtp_in_bandwidth_limit);
        out.writeCompressedInt(daily_smtp_out_limit);
        out.writeLong(daily_smtp_out_bandwidth_limit);
        out.writeCompressedInt(disable_log);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_24)>=0) {
            out.writeCompressedInt(email_in_burst);
            out.writeFloat(email_in_rate);
            out.writeCompressedInt(email_out_burst);
            out.writeFloat(email_out_rate);
            out.writeCompressedInt(email_relay_burst);
            out.writeFloat(email_relay_rate);
        }        
    }
}
