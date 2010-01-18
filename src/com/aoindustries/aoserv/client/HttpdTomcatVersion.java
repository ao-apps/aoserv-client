/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.UnixPath;

/**
 * An <code>HttpdTomcatVersion</code> flags which
 * <code>TechnologyVersion</code>s are a version of the Jakarta
 * Tomcat servlet engine.  Multiple versions of the Tomcat servlet
 * engine are supported, but only one version may be configured within
 * each Java virtual machine.
 *
 * @see  HttpdTomcatSite
 * @see  TechnologyVersion
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdTomcatVersion extends AOServObjectIntegerKey<HttpdTomcatVersion> implements BeanFactory<com.aoindustries.aoserv.client.beans.HttpdTomcatVersion> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    public static final String TECHNOLOGY_NAME="jakarta-tomcat";

    public static final String
        VERSION_3_1="3.1",
        VERSION_3_2_4="3.2.4",
        VERSION_4_1_PREFIX="4.1.",
        VERSION_5_5_PREFIX="5.5.",
        VERSION_6_0_PREFIX="6.0."
    ;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private UnixPath installDir;
    final private boolean requiresModJk;

    public HttpdTomcatVersion(HttpdTomcatVersionService<?,?> service, int version, UnixPath installDir, boolean requiresModJk) {
        super(service, version);
        this.installDir = installDir.intern();
        this.requiresModJk = requiresModJk;
    }
    // </editor-fold>

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(HttpdTomcatVersion.COLUMN_VERSION_name+'.'+TechnologyVersion.COLUMN_VERSION_name, ASCENDING)
    };

    public String getInstallDirectory() {
	return installDir;
    }

    public TechnologyVersion getTechnologyVersion() throws SQLException, IOException {
        return connector.getTechnologyVersions().get(pkey);
    }

    public void init(ResultSet result) throws SQLException {
	pkey=result.getInt(1);
	installDir=result.getString(2);
        requiresModJk=result.getBoolean(3);
    }

    /**
     * @deprecated  Please check all uses of this, because it also returns <code>true</code> for Tomcat 5, which doesn't seem
     *              to match the method name very well.
     *
     * @see  #isTomcat4_1_X(AOServConnector)
     * @see  #isTomcat5_5_X(AOServConnector)
     * @see  #isTomcat6_0_X(AOServConnector)
     */
    public boolean isTomcat4(AOServConnector connector) throws SQLException, IOException {
        String version = getTechnologyVersion(connector).getVersion();
        return version.startsWith("4.") || version.startsWith("5.");
    }

    public boolean isTomcat3_1(AOServConnector connector) throws SQLException, IOException {
        String version = getTechnologyVersion(connector).getVersion();
        return version.equals(VERSION_3_1);
    }

    public boolean isTomcat3_2_4(AOServConnector connector) throws SQLException, IOException {
        String version = getTechnologyVersion(connector).getVersion();
        return version.equals(VERSION_3_2_4);
    }

    public boolean isTomcat4_1_X(AOServConnector connector) throws SQLException, IOException {
        String version = getTechnologyVersion(connector).getVersion();
        return version.startsWith(VERSION_4_1_PREFIX);
    }

    public boolean isTomcat5_5_X(AOServConnector connector) throws SQLException, IOException {
        String version = getTechnologyVersion(connector).getVersion();
        return version.startsWith(VERSION_5_5_PREFIX);
    }

    public boolean isTomcat6_0_X(AOServConnector connector) throws SQLException, IOException {
        String version = getTechnologyVersion(connector).getVersion();
        return version.startsWith(VERSION_6_0_PREFIX);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readCompressedInt();
	installDir=in.readUTF();
        requiresModJk=in.readBoolean();
    }

    public boolean requiresModJK() {
        return requiresModJk;
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version protocolVersion) throws IOException {
	out.writeCompressedInt(pkey);
	out.writeUTF(installDir);
        out.writeBoolean(requiresModJk);
    }
}