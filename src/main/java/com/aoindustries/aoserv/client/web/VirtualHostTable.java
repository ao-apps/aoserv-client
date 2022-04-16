/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2012, 2016, 2017, 2018, 2020, 2021, 2022  AO Industries, Inc.
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
 * along with aoserv-client.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.aoindustries.aoserv.client.web;

import com.aoapps.hodgepodge.io.TerminalWriter;
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.net.Bind;
import com.aoindustries.aoserv.client.net.Device;
import com.aoindustries.aoserv.client.net.IpAddress;
import com.aoindustries.aoserv.client.pki.Certificate;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see  VirtualHost
 *
 * @author  AO Industries, Inc.
 */
public final class VirtualHostTable extends CachedTableIntegerKey<VirtualHost> {

	VirtualHostTable(AOServConnector connector) {
		super(connector, VirtualHost.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(VirtualHost.COLUMN_HTTPD_SITE_name+'.'+Site.COLUMN_NAME_name, ASCENDING),
		new OrderBy(VirtualHost.COLUMN_HTTPD_SITE_name+'.'+Site.COLUMN_AO_SERVER_name+'.'+Server.COLUMN_HOSTNAME_name, ASCENDING),
		new OrderBy(VirtualHost.COLUMN_HTTPD_BIND_name+'.'+HttpdBind.COLUMN_NET_BIND_name+'.'+Bind.COLUMN_IP_ADDRESS_name+'.'+IpAddress.COLUMN_IP_ADDRESS_name, ASCENDING),
		new OrderBy(VirtualHost.COLUMN_HTTPD_BIND_name+'.'+HttpdBind.COLUMN_NET_BIND_name+'.'+Bind.COLUMN_IP_ADDRESS_name+'.'+IpAddress.COLUMN_DEVICE_name+'.'+Device.COLUMN_DEVICE_ID_name, ASCENDING),
		new OrderBy(VirtualHost.COLUMN_HTTPD_BIND_name+'.'+HttpdBind.COLUMN_NET_BIND_name+'.'+Bind.COLUMN_PORT_name, ASCENDING),
		new OrderBy(VirtualHost.COLUMN_NAME_name, ASCENDING)
	};
	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public VirtualHost get(int pkey) throws IOException, SQLException {
		return getUniqueRow(VirtualHost.COLUMN_PKEY, pkey);
	}

	List<VirtualHost> getHttpdSiteBinds(Site site) throws IOException, SQLException {
		return getIndexedRows(VirtualHost.COLUMN_HTTPD_SITE, site.getPkey());
	}

	List<VirtualHost> getHttpdSiteBinds(Site site, HttpdServer server) throws SQLException, IOException {
		int serverPKey=server.getPkey();

		// Use the index first
		List<VirtualHost> cached=getHttpdSiteBinds(site);
		int size=cached.size();
		List<VirtualHost> matches=new ArrayList<>(size);
		for(int c=0;c<size;c++) {
			VirtualHost siteBind=cached.get(c);
			if(siteBind.getHttpdBind().getHttpdServer_pkey()==serverPKey) matches.add(siteBind);
		}
		return matches;
	}

	public List<VirtualHost> getHttpdSiteBinds(Certificate sslCert) throws IOException, SQLException {
		return getIndexedRows(VirtualHost.COLUMN_SSL_CERTIFICATE, sslCert.getPkey());
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.HTTPD_SITE_BINDS;
	}

	@Override
	public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, SQLException, IOException {
		String command=args[0];
		if(command.equalsIgnoreCase(Command.DISABLE_HTTPD_SITE_BIND)) {
			if(AOSH.checkParamCount(Command.DISABLE_HTTPD_SITE_BIND, args, 2, err)) {
				out.println(
					connector.getSimpleAOClient().disableHttpdSiteBind(
						AOSH.parseInt(args[1], "pkey"),
						args[2]
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.ENABLE_HTTPD_SITE_BIND)) {
			if(AOSH.checkParamCount(Command.ENABLE_HTTPD_SITE_BIND, args, 1, err)) {
				connector.getSimpleAOClient().enableHttpdSiteBind(AOSH.parseInt(args[1], "pkey"));
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.SET_HTTPD_SITE_BIND_IS_MANUAL)) {
			if(AOSH.checkParamCount(Command.SET_HTTPD_SITE_BIND_IS_MANUAL, args, 2, err)) {
				connector.getSimpleAOClient().setHttpdSiteBindIsManual(
					AOSH.parseInt(args[1], "pkey"),
					AOSH.parseBoolean(args[2], "is_manual")
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.SET_HTTPD_SITE_BIND_REDIRECT_TO_PRIMARY_HOSTNAME)) {
			if(AOSH.checkParamCount(Command.SET_HTTPD_SITE_BIND_REDIRECT_TO_PRIMARY_HOSTNAME, args, 2, err)) {
				connector.getSimpleAOClient().setHttpdSiteBindRedirectToPrimaryHostname(
					AOSH.parseInt(args[1], "pkey"),
					AOSH.parseBoolean(args[2], "redirect_to_primary_hostname")
				);
			}
			return true;
		} else return false;
	}
}
