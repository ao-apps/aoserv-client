package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

/**
 * An <code>HttpdTomcatSharedSite</code> is an <code>HttpdTomcatSite</code>
 * running under an <code>HttpdSharedTomcat</code>.
 *
 * @see  HttpdSharedTomcat
 * @see  HttpdTomcatSite
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdTomcatSharedSite extends CachedObjectIntegerKey<HttpdTomcatSharedSite> {

    static final int
        COLUMN_TOMCAT_SITE=0,
        COLUMN_HTTPD_SHARED_TOMCAT=1
    ;
    static final String COLUMN_TOMCAT_SITE_name = "tomcat_site";

    int httpd_shared_tomcat;

    public static final String DEFAULT_TOMCAT_VERSION_PREFIX=HttpdTomcatVersion.VERSION_6_0_PREFIX;

    /**
     * Determines if the API user is allowed to stop the Java virtual machine associated
     * with this site.
     */
    public boolean canStop() throws SQLException, IOException {
        HttpdSharedTomcat hst=getHttpdSharedTomcat();
        return getHttpdSharedTomcat()!=null && !hst.isDisabled();
    }

    /**
     * Determines if the API user is allowed to start the Java virtual machine associated
     * with this site.
     */
    public boolean canStart() throws SQLException, IOException {
        HttpdSharedTomcat hst=getHttpdSharedTomcat();
        return getHttpdSharedTomcat()==null || !hst.isDisabled();
    }

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_TOMCAT_SITE: return Integer.valueOf(pkey);
            case COLUMN_HTTPD_SHARED_TOMCAT: return Integer.valueOf(httpd_shared_tomcat);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public HttpdSharedTomcat getHttpdSharedTomcat() throws SQLException, IOException {
	// May be null when filtered
	return table.connector.getHttpdSharedTomcats().get(httpd_shared_tomcat);
    }

    public HttpdTomcatSite getHttpdTomcatSite() throws SQLException, IOException {
	HttpdTomcatSite obj=table.connector.getHttpdTomcatSites().get(pkey);
	if(obj==null) throw new SQLException("Unable to find HttpdTomcatSite: "+pkey);
	return obj;
    }

    public SchemaTable.TableID getTableID() {
    	return SchemaTable.TableID.HTTPD_TOMCAT_SHARED_SITES;
    }

    public void init(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
        httpd_shared_tomcat=result.getInt(2);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        httpd_shared_tomcat=in.readCompressedInt();
    }

    public List<AOServObject> getDependencies() throws IOException, SQLException {
        return createDependencyList(
            getHttpdTomcatSite(),
            getHttpdSharedTomcat()
        );
    }

    public List<AOServObject> getDependentObjects() throws IOException, SQLException {
        return createDependencyList(
        );
    }

    @Override
    String toStringImpl(Locale userLocale) throws SQLException, IOException {
        return getHttpdTomcatSite().toStringImpl(userLocale);
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeCompressedInt(httpd_shared_tomcat);
    }
}