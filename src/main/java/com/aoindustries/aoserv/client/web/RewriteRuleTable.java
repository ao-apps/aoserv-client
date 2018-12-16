/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2017, 2018  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.web;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.net.Bind;
import com.aoindustries.aoserv.client.net.Device;
import com.aoindustries.aoserv.client.net.IpAddress;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  RewriteRule
 *
 * @author  AO Industries, Inc.
 */
final public class RewriteRuleTable extends CachedTableIntegerKey<RewriteRule> {

	RewriteRuleTable(AOServConnector connector) {
		super(connector, RewriteRule.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(RewriteRule.COLUMN_virtualHost_name + '.' + VirtualHost.COLUMN_HTTPD_SITE_name + '.' + Site.COLUMN_NAME_name, ASCENDING),
		new OrderBy(RewriteRule.COLUMN_virtualHost_name + '.' + VirtualHost.COLUMN_HTTPD_SITE_name + '.' + Site.COLUMN_AO_SERVER_name + '.' + Server.COLUMN_HOSTNAME_name, ASCENDING),
		new OrderBy(RewriteRule.COLUMN_virtualHost_name + '.' + VirtualHost.COLUMN_HTTPD_BIND_name + '.' + HttpdBind.COLUMN_NET_BIND_name + '.' + Bind.COLUMN_IP_ADDRESS_name + '.' + IpAddress.COLUMN_IP_ADDRESS_name, ASCENDING),
		new OrderBy(RewriteRule.COLUMN_virtualHost_name + '.' + VirtualHost.COLUMN_HTTPD_BIND_name + '.' + HttpdBind.COLUMN_NET_BIND_name + '.' + Bind.COLUMN_IP_ADDRESS_name + '.' + IpAddress.COLUMN_DEVICE_name + '.' + Device.COLUMN_DEVICE_ID_name, ASCENDING),
		new OrderBy(RewriteRule.COLUMN_virtualHost_name + '.' + VirtualHost.COLUMN_HTTPD_BIND_name + '.' + HttpdBind.COLUMN_NET_BIND_name + '.' + Bind.COLUMN_PORT_name, ASCENDING),
		new OrderBy(RewriteRule.COLUMN_virtualHost_name + '.' + VirtualHost.COLUMN_NAME_name, ASCENDING),
		new OrderBy(RewriteRule.COLUMN_sortOrder_name, ASCENDING)
	};
	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public RewriteRule get(int id) throws IOException, SQLException {
		return getUniqueRow(RewriteRule.COLUMN_id, id);
	}

	List<RewriteRule> getRewriteRules(VirtualHost virtualHost) throws IOException, SQLException {
		return getIndexedRows(RewriteRule.COLUMN_virtualHost, virtualHost.getPkey());
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.RewriteRule;
	}
}
