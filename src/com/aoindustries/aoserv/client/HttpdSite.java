package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.BufferManager;
import java.io.*;
import java.sql.*;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * An <code>HttpdSite</code> is one unique set of web content and resides in
 * its own directory under <code>/www</code>.  Each <code>HttpdSite</code>
 * has a unique name per server, and may be served simultaneously on any
 * number of <code>HttpdBind</code>s through any number of
 * <code>HttpdServer</code>s.
 * <p>
 * An <code>HttpdSite</code> only stores the information that is common to
 * all site types.  The site will always reference one, and only one, other
 * type of entry, indicating the type of site and providing the rest of the
 * information about the site.
 *
 * @see  HttpdSiteBind
 * @see  HttpdBind
 * @see  HttpdServer
 * @see  HttpdStaticSite
 * @see  HttpdTomcatSite
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdSite extends CachedObjectIntegerKey<HttpdSite> implements Disablable, Removable {

    static final int
        COLUMN_PKEY=0,
        COLUMN_AO_SERVER=1,
        COLUMN_PACKAGE=4
    ;
    static final String COLUMN_SITE_NAME_name = "site_name";
    static final String COLUMN_AO_SERVER_name = "ao_server";

    public static final int MAX_SITE_NAME_LENGTH=255;

    /**
     * @deprecated  The directory for websites is now operating-system specific.
     * 
     * @see  OperatingSystemVersion#getHttpdSitesDirectory()
     * @see  OperatingSystemVersion#getHttpdSitesDirectory(int)
     */
    @Deprecated
    public static final String WWW_DIRECTORY="/www";

    /**
     * The site name used when an account is disabled.
     */
    public static final String DISABLED="disabled";
    
    int ao_server;
    String site_name;
    private boolean list_first;
    String packageName;
    String linuxAccount;
    String linuxGroup;
    private String
        serverAdmin,
        contentSrc
    ;
    int disable_log;
    private boolean isManual;
    private String awstatsSkipFiles;

    public int addHttpdSiteAuthenticatedLocation(
        String path,
        boolean isRegularExpression,
        String authName,
        String authGroupFile,
        String authUserFile,
        String require
    ) throws IOException, SQLException {
        return table.connector.getHttpdSiteAuthenticatedLocationTable().addHttpdSiteAuthenticatedLocation(
            this,
            path,
            isRegularExpression,
            authName,
            authGroupFile,
            authUserFile,
            require
        );
    }

    public boolean canDisable() throws IOException, SQLException {
        if(disable_log!=-1) return false;
        for(HttpdSiteBind hsb : getHttpdSiteBinds()) if(hsb.disable_log==-1) return false;
        return true;
    }

    public boolean canEnable() throws SQLException, IOException {
        DisableLog dl=getDisableLog();
        if(dl==null) return false;
        else return
            dl.canEnable()
            && getPackage().disable_log==-1
            && getLinuxServerAccount().disable_log==-1
        ;
    }

    public List<CannotRemoveReason> getCannotRemoveReasons(Locale userLocale) {
        return Collections.emptyList();
    }

    public void disable(DisableLog dl) throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.DISABLE, SchemaTable.TableID.HTTPD_SITES, dl.pkey, pkey);
    }
    
    public void enable() throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.ENABLE, SchemaTable.TableID.HTTPD_SITES, pkey);
    }

    /**
     * Gets the directory where this site is installed.
     */
    public String getInstallDirectory() throws SQLException, IOException {
        return getAOServer().getServer().getOperatingSystemVersion().getHttpdSitesDirectory()+'/'+site_name;
    }

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_AO_SERVER: return Integer.valueOf(ao_server);
            case 2: return site_name;
            case 3: return list_first?Boolean.TRUE:Boolean.FALSE;
            case COLUMN_PACKAGE: return packageName;
            case 5: return linuxAccount;
            case 6: return linuxGroup;
            case 7: return serverAdmin;
            case 8: return contentSrc;
            case 9: return disable_log==-1?null:Integer.valueOf(disable_log);
            case 10: return isManual?Boolean.TRUE:Boolean.FALSE;
            case 11: return awstatsSkipFiles;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public String getContentSrc() {
        return contentSrc;
    }

    public boolean isDisabled() {
        return disable_log!=-1;
    }

    public DisableLog getDisableLog() throws SQLException, IOException {
        if(disable_log==-1) return null;
        DisableLog obj=table.connector.getDisableLogs().get(disable_log);
        if(obj==null) throw new SQLException("Unable to find DisableLog: "+disable_log);
        return obj;
    }

    public List<HttpdSiteAuthenticatedLocation> getHttpdSiteAuthenticatedLocations() throws IOException, SQLException {
        return table.connector.getHttpdSiteAuthenticatedLocationTable().getHttpdSiteAuthenticatedLocations(this);
    }

    public List<HttpdSiteBind> getHttpdSiteBinds() throws IOException, SQLException {
        return table.connector.getHttpdSiteBinds().getHttpdSiteBinds(this);
    }

    public List<HttpdSiteBind> getHttpdSiteBinds(HttpdServer server) throws SQLException, IOException {
        return table.connector.getHttpdSiteBinds().getHttpdSiteBinds(this, server);
    }

    public HttpdStaticSite getHttpdStaticSite() throws IOException, SQLException {
        return table.connector.getHttpdStaticSites().get(pkey);
    }

    public HttpdTomcatSite getHttpdTomcatSite() throws IOException, SQLException {
        return table.connector.getHttpdTomcatSites().get(pkey);
    }

    public LinuxServerAccount getLinuxServerAccount() throws SQLException, IOException {
        // May be filtered
        LinuxAccount obj=table.connector.getLinuxAccounts().get(linuxAccount);
        if(obj==null) return null;

        LinuxServerAccount lsa = obj.getLinuxServerAccount(getAOServer());
        if (lsa==null) throw new SQLException("Unable to find LinuxServerAccount: "+linuxAccount+" on "+ao_server);
        return lsa;
    }

    public LinuxServerGroup getLinuxServerGroup() throws SQLException, IOException {
        LinuxGroup obj=table.connector.getLinuxGroups().get(linuxGroup);
        if(obj==null) throw new SQLException("Unable to find LinuxGroup: "+linuxGroup);
        LinuxServerGroup lsg = obj.getLinuxServerGroup(getAOServer());
        if(lsg==null) throw new SQLException("Unable to find LinuxServerGroup: "+linuxGroup+" on "+ao_server);
        return lsg;
    }

    public Package getPackage() throws SQLException, IOException {
        Package obj=table.connector.getPackages().get(packageName);
        if(obj==null) throw new SQLException("Unable to find Package: "+packageName);
        return obj;
    }

    public HttpdSiteURL getPrimaryHttpdSiteURL() throws SQLException, IOException {
        List<HttpdSiteBind> binds=getHttpdSiteBinds();
        if(binds.isEmpty()) return null;

        // Find the first one that binds to the default HTTP port, if one exists
        NetPort httpPort=table.connector.getProtocols().get(Protocol.HTTP).getPort(table.connector);

        int index=-1;
        for(int c=0;c<binds.size();c++) {
            HttpdSiteBind bind=binds.get(c);
            if(bind.getHttpdBind().getNetBind().getPort().equals(httpPort)) {
                index=c;
                break;
            }
        }
        if(index==-1) index=0;

        return binds.get(index).getPrimaryHttpdSiteURL();
    }

    public AOServer getAOServer() throws SQLException, IOException {
        AOServer obj=table.connector.getAoServers().get(ao_server);
        if(obj==null) throw new SQLException("Unable to find AOServer: "+ao_server);
        return obj;
    }

    public String getServerAdmin() {
        return serverAdmin;
    }

    public String getSiteName() {
        return site_name;
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.HTTPD_SITES;
    }

    //public void initializePasswdFile(String username, String password) {
    //    table.connector.requestUpdate(AOServProtocol.INITIALIZE_HTTPD_SITE_PASSWD_FILE, pkey, username, UnixCrypt.crypt(username, password));
    //}

    public void init(ResultSet result) throws SQLException {
        int pos = 1;
        pkey=result.getInt(pos++);
        ao_server=result.getInt(pos++);
        site_name=result.getString(pos++);
        list_first=result.getBoolean(pos++);
        packageName=result.getString(pos++);
        linuxAccount=result.getString(pos++);
        linuxGroup=result.getString(pos++);
        serverAdmin=result.getString(pos++);
        contentSrc=result.getString(pos++);
        disable_log=result.getInt(pos++);
        if(result.wasNull()) disable_log=-1;
        isManual=result.getBoolean(pos++);
        awstatsSkipFiles=result.getString(pos++);
    }

    public boolean isManual() {
        return isManual;
    }

    public String getAwstatsSkipFiles() {
        return awstatsSkipFiles;
    }

    /**
     * Checks the format of the name of the site, as used in the <code>/www</code>
     * directory.  The site name must be 255 characters or less, and comprised of
     * only <code>a-z</code>, <code>0-9</code>, <code>.</code> or <code>-</code>.  The first
     * character must be <code>a-z</code> or <code>0-9</code>.
     */
    public static boolean isValidSiteName(String name) {
        // These are the other files/directories that may exist under /www.  To avoid
        // potential conflicts, these may not be used as site names.
        if(
            "lost+found".equals(name)
            || ".backup".equals(name)
            || "aquota.user".equals(name)
        ) return false;

        int len = name.length();
        if (len == 0 || len > MAX_SITE_NAME_LENGTH)
                return false;
        // The first character must be [a-z] or [0-9]
        char ch = name.charAt(0);
        if (
            (ch < 'a' || ch > 'z')
            && (ch<'0' || ch>'9')
        ) return false;
        // The rest may have additional characters
        for (int c = 1; c < len; c++) {
            ch = name.charAt(c);
            if(
                (ch < 'a' || ch > 'z')
                && (ch < '0' || ch > '9')
                && ch != '.'
                && ch != '-'
            ) return false;
        }
        return true;
    }

    public boolean listFirst() {
        return list_first;
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        ao_server=in.readCompressedInt();
        site_name=in.readUTF();
        list_first=in.readBoolean();
        packageName=in.readUTF().intern();
        linuxAccount=in.readUTF().intern();
        linuxGroup=in.readUTF().intern();
        serverAdmin=in.readUTF();
        contentSrc=in.readNullUTF();
        disable_log=in.readCompressedInt();
        isManual=in.readBoolean();
        awstatsSkipFiles=in.readNullUTF();
    }

    public void remove() throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.REMOVE, SchemaTable.TableID.HTTPD_SITES, pkey);
    }

    public void setIsManual(boolean isManual) throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.SET_HTTPD_SITE_IS_MANUAL, pkey, isManual);
    }

    public void setServerAdmin(String address) throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.SET_HTTPD_SITE_SERVER_ADMIN, pkey, address);
    }

    @Override
    String toStringImpl(Locale userLocale) throws SQLException, IOException {
        return site_name+" on "+getAOServer().getHostname();
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeCompressedInt(ao_server);
        out.writeUTF(site_name);
        out.writeBoolean(list_first);
        out.writeUTF(packageName);
        out.writeUTF(linuxAccount);
        out.writeUTF(linuxGroup);
        out.writeUTF(serverAdmin);
        out.writeNullUTF(contentSrc);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_30)<=0) {
            out.writeShort(0);
            out.writeShort(7);
            out.writeShort(0);
            out.writeShort(7);
            out.writeShort(0);
            out.writeShort(7);
            out.writeShort(0);
            out.writeShort(7);
        }
        out.writeCompressedInt(disable_log);
        out.writeBoolean(isManual);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_129)>=0) out.writeNullUTF(awstatsSkipFiles);
    }

    public void getAWStatsFile(final String path, final String queryString, final OutputStream out) throws IOException, SQLException {
        table.connector.requestUpdate(
            false,
            new AOServConnector.UpdateRequest() {
                public void writeRequest(CompressedDataOutputStream masterOut) throws IOException {
                    masterOut.writeCompressedInt(AOServProtocol.CommandID.GET_AWSTATS_FILE.ordinal());
                    masterOut.writeCompressedInt(pkey);
                    masterOut.writeUTF(path);
                    masterOut.writeUTF(queryString==null ? "" : queryString);
                }

                public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
                    byte[] buff=BufferManager.getBytes();
                    try {
                        int code;
                        while((code=in.readByte())==AOServProtocol.NEXT) {
                            int len=in.readShort();
                            in.readFully(buff, 0, len);
                            out.write(buff, 0, len);
                        }
                        AOServProtocol.checkResult(code, in);
                    } finally {
                        BufferManager.release(buff);
                    }
                }

                public void afterRelease() {
                }
            }
        );
    }
}