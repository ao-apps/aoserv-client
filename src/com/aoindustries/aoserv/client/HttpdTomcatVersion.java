/*
 * Copyright 2001-2013 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

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
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdTomcatVersion extends GlobalObjectIntegerKey<HttpdTomcatVersion> {

	static final int COLUMN_VERSION=0;

	static final String COLUMN_VERSION_name = "version";

	private String install_dir;
	private boolean requires_mod_jk;

	public static final String TECHNOLOGY_NAME="jakarta-tomcat";

	public static final String
		VERSION_3_1="3.1",
		VERSION_3_2_4="3.2.4",
		VERSION_4_1_PREFIX="4.1.",
		VERSION_5_5_PREFIX="5.5.",
		VERSION_6_0_PREFIX="6.0.",
		VERSION_7_0_PREFIX="7.0."
	;

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_VERSION: return Integer.valueOf(pkey);
			case 1: return install_dir;
			case 2: return requires_mod_jk?Boolean.TRUE:Boolean.FALSE;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public String getInstallDirectory() {
		return install_dir;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.HTTPD_TOMCAT_VERSIONS;
	}

	public TechnologyVersion getTechnologyVersion(AOServConnector connector) throws SQLException, IOException {
		TechnologyVersion obj=connector.getTechnologyVersions().get(pkey);
		if(obj==null) throw new SQLException("Unable to find TechnologyVersion: "+pkey);
		return obj;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey=result.getInt(1);
		install_dir=result.getString(2);
		requires_mod_jk=result.getBoolean(3);
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

	public boolean isTomcat7_0_X(AOServConnector connector) throws SQLException, IOException {
		String version = getTechnologyVersion(connector).getVersion();
		return version.startsWith(VERSION_7_0_PREFIX);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readCompressedInt();
		install_dir=in.readUTF();
		requires_mod_jk=in.readBoolean();
	}

	public boolean requiresModJK() {
		return requires_mod_jk;
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeUTF(install_dir);
		out.writeBoolean(requires_mod_jk);
	}
}