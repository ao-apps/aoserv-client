/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2003-2009, 2016, 2017, 2018, 2019, 2020  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aoserv-client.
 *
 * aoserv-client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aoserv-client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aoserv-client.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.aoserv.client.distribution;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.GlobalObjectIntegerKey;
import com.aoindustries.aoserv.client.email.List;
import com.aoindustries.aoserv.client.linux.PosixPath;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.io.stream.StreamableInput;
import com.aoindustries.io.stream.StreamableOutput;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * One version of a operating system.
 *
 * @see OperatingSystem
 *
 * @author  AO Industries, Inc.
 */
final public class OperatingSystemVersion extends GlobalObjectIntegerKey<OperatingSystemVersion> {

	static final int COLUMN_PKEY=0;
	static final String COLUMN_SORT_ORDER_name = "sort_order";

	public static final String
		//VERSION_1_4="1.4",
		//VERSION_7_2="7.2",
		//VERSION_9_2="9.2",
		VERSION_5="5",
		VERSION_5_DOM0="5.dom0",
		VERSION_7="7",
		VERSION_7_DOM0="7.dom0",
		VERSION_2006_0="2006.0",
		VERSION_ES_4="ES 4"
	;

	/**
	 * @deprecated  Mandrake 10.1 no longer used.
	 */
	@Deprecated
	public static final String VERSION_10_1="10.1";

	/**
	 * @deprecated  What is this used for?
	 */
	@Deprecated
	public static final String DEFAULT_OPERATING_SYSTEM_VERSION=VERSION_2006_0;

	public static final int
		CENTOS_5_DOM0_X86_64 = 63,
		CENTOS_5_DOM0_I686 = 64,
		CENTOS_5_I686_AND_X86_64 = 67,
		CENTOS_7_DOM0_X86_64 = 69,
		CENTOS_7_X86_64 = 70,
		//GENTOO_1_4_I686=5,
		//MANDRAKE_9_2_I586=12,
		//REDHAT_7_2_I686=27
		MANDRIVA_2006_0_I586=45,
		REDHAT_ES_4_X86_64=47
	;

	/**
	 * @deprecated  Mandrake 10.1 no longer used.
	 */
	@Deprecated
	public static final int MANDRAKE_10_1_I586=14;

	private String operating_system;
	private String version_number;
	private String version_name;
	private String architecture;
	private String display;
	private boolean is_aoserv_daemon_supported;
	private short sort_order;

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case 1: return operating_system;
			case 2: return version_number;
			case 3: return version_name;
			case 4: return architecture;
			case 5: return display;
			case 6: return is_aoserv_daemon_supported;
			case 7: return sort_order;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	public OperatingSystem getOperatingSystem(AOServConnector conn) throws IOException, SQLException {
		return conn.getDistribution().getOperatingSystem().get(operating_system);
	}

	public String getVersionNumber() {
		return version_number;
	}

	public String getVersionName() {
		return version_name;
	}

	public String getArchitecture_name() {
		return architecture;
	}

	public Architecture getArchitecture(AOServConnector connector) throws SQLException, IOException {
		Architecture ar=connector.getDistribution().getArchitecture().get(architecture);
		if(ar==null) throw new SQLException("Unable to find Architecture: "+architecture);
		return ar;
	}

	public String getDisplay() {
		return display;
	}

	public boolean isAOServDaemonSupported() {
		return is_aoserv_daemon_supported;
	}

	public short getSortOrder() {
		return sort_order;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.OPERATING_SYSTEM_VERSIONS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey = result.getInt(1);
		operating_system = result.getString(2);
		version_number = result.getString(3);
		version_name = result.getString(4);
		architecture = result.getString(5);
		display = result.getString(6);
		is_aoserv_daemon_supported = result.getBoolean(7);
		sort_order = result.getShort(8);
	}

	@Override
	public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
		pkey = in.readCompressedInt();
		operating_system = in.readUTF().intern();
		version_number = in.readUTF();
		version_name = in.readUTF();
		architecture = in.readUTF().intern();
		display = in.readUTF();
		is_aoserv_daemon_supported = in.readBoolean();
		sort_order = in.readShort();
	}

	@Override
	public String toStringImpl() {
		return display;
	}

	@Override
	public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeUTF(operating_system);
		out.writeUTF(version_number);
		out.writeUTF(version_name);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_108)>=0) out.writeUTF(architecture);
		out.writeUTF(display);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_108)>=0) out.writeBoolean(is_aoserv_daemon_supported);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_3)>=0) out.writeShort(sort_order);
	}

	/**
	 * Gets the directory that stores websites for this operating system or {@code null}
	 * if this OS doesn't support web sites.
	 */
	public PosixPath getHttpdSitesDirectory() {
		return getHttpdSitesDirectory(pkey);
	}

	private static final PosixPath WWW, VAR_WWW;
	static {
		try {
			WWW = PosixPath.valueOf("/www").intern();
			VAR_WWW = PosixPath.valueOf("/var/www").intern();
		} catch(ValidationException e) {
			throw new AssertionError("These hard-coded values are valid", e);
		}
	}

	/**
	 * Gets the directory that stores websites for this operating system or {@code null}
	 * if this OS doesn't support web sites.
	 */
	public static PosixPath getHttpdSitesDirectory(int osv) {
		switch(osv) {
			case MANDRIVA_2006_0_I586 :
			case REDHAT_ES_4_X86_64 :
			case CENTOS_5_I686_AND_X86_64 :
				return WWW;
			case CENTOS_7_X86_64 :
				return VAR_WWW;
			case CENTOS_5_DOM0_I686 :
			case CENTOS_5_DOM0_X86_64 :
			case CENTOS_7_DOM0_X86_64 :
				return null;
			default :
				throw new AssertionError("Unexpected OperatingSystemVersion: "+osv);
		}
	}

	/**
	 * Gets the directory that contains the shared tomcat directories or {@code null}
	 * if this OS doesn't support shared tomcats.
	 */
	public PosixPath getHttpdSharedTomcatsDirectory() {
		return getHttpdSharedTomcatsDirectory(pkey);
	}

	private static final PosixPath WWWGROUP, VAR_OPT_APACHE_TOMCAT;
	static {
		try {
			WWWGROUP = PosixPath.valueOf("/wwwgroup").intern();
			VAR_OPT_APACHE_TOMCAT = PosixPath.valueOf("/var/opt/apache-tomcat").intern();
		} catch(ValidationException e) {
			throw new AssertionError("These hard-coded values are valid", e);
		}
	}

	/**
	 * Gets the directory that contains the shared tomcat directories or {@code null}
	 * if this OS doesn't support shared tomcats.
	 */
	public static PosixPath getHttpdSharedTomcatsDirectory(int osv) {
		switch(osv) {
			case MANDRIVA_2006_0_I586 :
			case REDHAT_ES_4_X86_64 :
			case CENTOS_5_I686_AND_X86_64 :
				return WWWGROUP;
			case CENTOS_7_X86_64 :
				return VAR_OPT_APACHE_TOMCAT;
			case CENTOS_5_DOM0_I686 :
			case CENTOS_5_DOM0_X86_64 :
			case CENTOS_7_DOM0_X86_64 :
				return null;
			default :
				throw new AssertionError("Unexpected OperatingSystemVersion: "+osv);
		}
	}

	/**
	 * Gets the directory that contains the per-virtual-host HTTP logs or {@code null}
	 * if this OS doesn't support web sites.
	 */
	public PosixPath getHttpdSiteLogsDirectory() {
		return getHttpdSiteLogsDirectory(pkey);
	}

	private static final PosixPath LOGS, VAR_LOG_HTTPD_SITES;
	static {
		try {
			LOGS = PosixPath.valueOf("/logs").intern();
			VAR_LOG_HTTPD_SITES = PosixPath.valueOf("/var/log/httpd-sites").intern();
		} catch(ValidationException e) {
			throw new AssertionError("These hard-coded values are valid", e);
		}
	}

	/**
	 * Gets the directory that contains the per-virtual-host HTTP logs or {@code null}
	 * if this OS doesn't support web sites.
	 */
	public static PosixPath getHttpdSiteLogsDirectory(int osv) {
		switch(osv) {
			case MANDRIVA_2006_0_I586 :
			case REDHAT_ES_4_X86_64 :
			case CENTOS_5_I686_AND_X86_64 :
				return LOGS;
			case CENTOS_7_X86_64 :
				return VAR_LOG_HTTPD_SITES;
			case CENTOS_5_DOM0_I686 :
			case CENTOS_5_DOM0_X86_64 :
			case CENTOS_7_DOM0_X86_64 :
				return null;
			default :
				throw new AssertionError("Unexpected OperatingSystemVersion: "+osv);
		}
	}

	/**
	 * @see  List#getListPath(java.lang.String, int)
	 */
	public PosixPath getEmailListPath(String name) throws ValidationException {
		return List.getListPath(name, pkey);
	}

	/**
	 * @see  List#isValidRegularPath(com.aoindustries.aoserv.client.linux.PosixPath, int)
	 */
	public boolean isValidEmailListRegularPath(PosixPath path) {
		return List.isValidRegularPath(path, pkey);
	}
}
