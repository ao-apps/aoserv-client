/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2009, 2016, 2017, 2018, 2019, 2020, 2021  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.billing;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.i18n.Resources;
import com.aoindustries.aoserv.client.GlobalObjectStringKey;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * A <code>Resource</code> is a measurable hardware resource.  A <code>Package</code>
 * comes with a set of resources, and when those <code>PackageDefinitionLimit</code>s are exceeded,
 * an additional amount is charged to the {@link Account}.
 *
 * @see  Package
 *
 * @author  AO Industries, Inc.
 */
public final class Resource extends GlobalObjectStringKey<Resource> {

	private static final Resources RESOURCES = Resources.getResources(ResourceBundle::getBundle, Resource.class);

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
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_NAME: return pkey;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	/**
	 * Gets the unique name of this resource.
	 */
	public String getName() {
		return pkey;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.RESOURCES;
	}

	public String getDisplayUnit(int quantity) {
		if(quantity==1) return RESOURCES.getMessage(pkey + ".singularDisplayUnit", quantity);
		else return RESOURCES.getMessage(pkey + ".pluralDisplayUnit", quantity);
	}

	public String getPerUnit(Object amount) {
		return RESOURCES.getMessage(pkey + ".perUnit", amount);
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey = result.getString(1);
	}

	@Override
	public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
		pkey=in.readUTF().intern();
	}

	@Override
	public String toStringImpl() {
		return RESOURCES.getMessage(pkey + ".toString");
	}

	@Override
	public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeUTF(pkey);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_60)<=0) {
			out.writeUTF(RESOURCES.getMessage(pkey + ".singularDisplayUnit", ""));
		}
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_123)>=0 && protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_60)<=0) {
			out.writeUTF(RESOURCES.getMessage(pkey + ".pluralDisplayUnit", ""));
			out.writeUTF(getPerUnit(""));
		}
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_60)<=0) out.writeUTF(toString()); // description
	}
}
