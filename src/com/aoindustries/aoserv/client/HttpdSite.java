/*
 * Copyright 2001-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.WrappedException;
import java.rmi.RemoteException;

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
 * @author  AO Industries, Inc.
 */
final public class HttpdSite extends AOServerResource implements Comparable<HttpdSite>, DtoFactory<com.aoindustries.aoserv.client.dto.HttpdSite> /*, Disablable, Removable */ {

    // <editor-fold defaultstate="collapsed" desc="Constants">
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
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Fields">
    private static final long serialVersionUID = -2644853941410100318L;

    private DomainName siteName;
    final private boolean listFirst;
    final private int linuxAccountGroup;
    private Email serverAdmin;
    final private boolean manualConfig;
    private String awstatsSkipFiles;

    public HttpdSite(
        AOServConnector connector,
        int pkey,
        String resourceType,
        AccountingCode accounting,
        long created,
        UserId createdBy,
        Integer disableLog,
        long lastEnabled,
        int aoServer,
        int businessServer,
        DomainName siteName,
        boolean listFirst,
        int linuxAccountGroup,
        Email serverAdmin,
        boolean isManualConfig,
        String awstatsSkipFiles
    ) {
        super(connector, pkey, resourceType, accounting, created, createdBy, disableLog, lastEnabled, aoServer, businessServer);
        this.siteName = siteName;
        this.listFirst = listFirst;
        this.linuxAccountGroup = linuxAccountGroup;
        this.serverAdmin = serverAdmin;
        this.manualConfig = isManualConfig;
        this.awstatsSkipFiles = awstatsSkipFiles;
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        siteName = intern(siteName);
        serverAdmin = intern(serverAdmin);
        awstatsSkipFiles = intern(awstatsSkipFiles);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(HttpdSite other) {
        try {
            if(this==other) return 0;
            int diff = siteName.compareTo(other.siteName);
            if(diff!=0) return diff;
            return aoServer==other.aoServer ? 0 : getAoServer().compareTo(other.getAoServer());
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+1, description="the name of the site, as used in the /www directory.")
    public DomainName getSiteName() {
        return siteName;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+2, description="if <code>true</code>, this site will be listed first in the Apache configs.  This is normally used only for the \"not found\" site for each httpd_server.")
    public boolean isListFirst() {
        return listFirst;
    }

    public static final MethodColumn COLUMN_LINUX_ACCOUNT_GROUP = getMethodColumn(HttpdSite.class, "linuxAccountGroup");
    @DependencySingleton
    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+3, index=IndexType.INDEXED, description="the user the site \"runs as\"")
    public LinuxAccountGroup getLinuxAccountGroup() throws RemoteException {
        return getConnector().getLinuxAccountGroups().get(linuxAccountGroup);
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+4, description="the email address of the server administrator.  This address is provided when an error occurs.  The value is most often <code>webmaster@<i>domain.com</i></code>")
    public Email getServerAdmin() {
        return serverAdmin;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+5, description="configuration of this site config file is performed manually")
    public boolean isManualConfig() {
        return manualConfig;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+6, description="the SkipFiles setting for AWStats")
    public String getAwstatsSkipFiles() {
        return awstatsSkipFiles;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public HttpdSite(AOServConnector connector, com.aoindustries.aoserv.client.dto.HttpdSite dto) throws ValidationException {
        this(
            connector,
            dto.getPkey(),
            dto.getResourceType(),
            getAccountingCode(dto.getAccounting()),
            getTimeMillis(dto.getCreated()),
            getUserId(dto.getCreatedBy()),
            dto.getDisableLog(),
            getTimeMillis(dto.getLastEnabled()),
            dto.getAoServer(),
            dto.getBusinessServer(),
            getDomainName(dto.getSiteName()),
            dto.isListFirst(),
            dto.getLinuxAccountGroup(),
            getEmail(dto.getServerAdmin()),
            dto.isManualConfig(),
            dto.getAwstatsSkipFiles()
        );
    }

    @Override
    public com.aoindustries.aoserv.client.dto.HttpdSite getDto() {
        return new com.aoindustries.aoserv.client.dto.HttpdSite(
            getKeyInt(),
            getResourceTypeName(),
            getDto(getAccounting()),
            created,
            getDto(getCreatedByUsername()),
            disableLog,
            lastEnabled,
            aoServer,
            businessServer,
            getDto(siteName),
            listFirst,
            linuxAccountGroup,
            getDto(serverAdmin),
            manualConfig,
            awstatsSkipFiles
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() throws RemoteException {
        return ApplicationResources.accessor.getMessage("HttpdSite.toString", siteName, getAoServer().getHostname());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    /* TODO
    @DependentObjectSet
    public List<HttpdSiteAuthenticatedLocation> getHttpdSiteAuthenticatedLocations() throws IOException, SQLException {
        return getConnector().getHttpdSiteAuthenticatedLocationTable().getHttpdSiteAuthenticatedLocations(this);
    }

    @DependentObjectSet
    public List<HttpdSiteBind> getHttpdSiteBinds() throws IOException, SQLException {
        return getConnector().getHttpdSiteBinds().getHttpdSiteBinds(this);
    }

    @DependentObjectSet
    public HttpdStaticSite getHttpdStaticSite() throws IOException, SQLException {
        return getConnector().getHttpdStaticSites().get(pkey);
    }

    @DependentObjectSet
    public HttpdTomcatSite getHttpdTomcatSite() throws IOException, SQLException {
        return getConnector().getHttpdTomcatSites().get(pkey);
    }
     */
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
    /* TODO
    public int addHttpdSiteAuthenticatedLocation(
        String path,
        boolean isRegularExpression,
        String authName,
        String authGroupFile,
        String authUserFile,
        String require
    ) throws IOException, SQLException {
        return getConnector().getHttpdSiteAuthenticatedLocationTable().addHttpdSiteAuthenticatedLocation(
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
            && getBusiness().disable_log==-1
            && getLinuxServerAccount().disable_log==-1
        ;
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        return Collections.emptyList();
    }

    public void disable(DisableLog dl) throws IOException, SQLException {
        getConnector().requestUpdateIL(true, AOServProtocol.CommandID.DISABLE, SchemaTable.TableID.HTTPD_SITES, dl.pkey, pkey);
    }
    
    public void enable() throws IOException, SQLException {
        getConnector().requestUpdateIL(true, AOServProtocol.CommandID.ENABLE, SchemaTable.TableID.HTTPD_SITES, pkey);
    }
    */
    /**
     * Gets the directory where this site is installed.
     */
    /* TODO
    public String getInstallDirectory() throws SQLException, IOException {
        return getAOServer().getServer().getOperatingSystemVersion().getHttpdSitesDirectory()+'/'+site_name;
    }

    public boolean isDisabled() {
        return disable_log!=-1;
    }

    public List<HttpdSiteBind> getHttpdSiteBinds(HttpdServer server) throws SQLException, IOException {
        return getConnector().getHttpdSiteBinds().getHttpdSiteBinds(this, server);
    }

    public HttpdSiteURL getPrimaryHttpdSiteURL() throws SQLException, IOException {
        List<HttpdSiteBind> binds=getHttpdSiteBinds();
        if(binds.isEmpty()) return null;

        // Find the first one that binds to the default HTTP port, if one exists
        NetPort httpPort=getConnector().getProtocols().get(Protocol.HTTP).getPort(getConnector());

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
    */
    /**
     * Checks the format of the name of the site, as used in the <code>/www</code>
     * directory.  The site name must be 255 characters or less, and comprised of
     * only <code>a-z</code>, <code>0-9</code>, <code>.</code> or <code>-</code>.  The first
     * character must be <code>a-z</code> or <code>0-9</code>.
     */
    /* TODO
    public void remove() throws IOException, SQLException {
        getConnector().requestUpdateIL(true, AOServProtocol.CommandID.REMOVE, SchemaTable.TableID.HTTPD_SITES, pkey);
    }

    public void setIsManual(boolean isManual) throws IOException, SQLException {
        getConnector().requestUpdateIL(true, AOServProtocol.CommandID.SET_HTTPD_SITE_IS_MANUAL, pkey, isManual);
    }

    public void setServerAdmin(String address) throws IOException, SQLException {
        getConnector().requestUpdateIL(true, AOServProtocol.CommandID.SET_HTTPD_SITE_SERVER_ADMIN, pkey, address);
    }

    public void getAWStatsFile(final String path, final String queryString, final OutputStream out) throws IOException, SQLException {
        getConnector().requestUpdate(
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
     */
    // </editor-fold>
}