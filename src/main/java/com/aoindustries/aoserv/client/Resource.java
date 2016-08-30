/*
 * Copyright 2000-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import static com.aoindustries.aoserv.client.ApplicationResources.accessor;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A <code>Resource</code> is a measurable hardware resource.  A <code>Package</code>
 * comes with a set of resources, and when those <code>PackageDefinitionLimit</code>s are exceeded,
 * an additional amount is charged to the <code>Business</code>.
 *
 * @see  Package
 *
 * @author  AO Industries, Inc.
 */
final public class Resource extends GlobalObjectStringKey<Resource> {

	static final int COLUMN_NAME=0;
	static final String COLUMN_NAME_name = "name";

	public static final String
		AOSERV_DAEMON="aoserv_daemon",
		AOSERV_MASTER="aoserv_master",
		BANDWIDTH="bandwidth",
		CONSULTING="consulting",
		DISK="disk",
		DISTRIBUTION_SCAN="distribution_scan",
		DRUPAL="drupal",
		EMAIL="email",
		FAILOVER="failover",
		HARDWARE_DISK_7200_120="hardware_disk_7200_120",
		HTTPD="httpd",
		IP="ip",
		JAVAVM="javavm",
		JOOMLA="joomla",
		MYSQL_REPLICATION="mysql_replication",
		RACK="rack",
		SERVER_DATABASE="server_database",
		SERVER_ENTERPRISE="server_enterprise",
		SERVER_P4="server_p4",
		SERVER_SCSI="server_scsi",
		SERVER_XEON="server_xeon",
		SITE="site",
		SYSADMIN="sysadmin",
		USER="user"
	;

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_NAME: return pkey;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	/**
	 * Gets the unique name of this resource.
	 */
	public String getName() {
		return pkey;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.RESOURCES;
	}

	public String getDisplayUnit(int quantity) {
		if(quantity==1) return accessor.getMessage("Resource."+pkey+".singularDisplayUnit", quantity);
		else return accessor.getMessage("Resource."+pkey+".pluralDisplayUnit", quantity);
	}

	public String getPerUnit(Object amount) {
		return accessor.getMessage("Resource."+pkey+".perUnit", amount);
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey = result.getString(1);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readUTF().intern();
	}

	@Override
	String toStringImpl() {
		return accessor.getMessage("Resource."+pkey+".toString");
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeUTF(pkey);
		if(version.compareTo(AOServProtocol.Version.VERSION_1_60)<=0) {
			out.writeUTF(accessor.getMessage("Resource."+pkey+".singularDisplayUnit", ""));
		}
		if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_123)>=0 && version.compareTo(AOServProtocol.Version.VERSION_1_60)<=0) {
			out.writeUTF(accessor.getMessage("Resource."+pkey+".pluralDisplayUnit", ""));
			out.writeUTF(getPerUnit(""));
		}
		if(version.compareTo(AOServProtocol.Version.VERSION_1_60)<=0) out.writeUTF(toString()); // description
	}
}