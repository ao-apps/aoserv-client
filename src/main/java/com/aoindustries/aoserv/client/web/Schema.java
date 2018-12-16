/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2018  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.AOServTable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author  AO Industries, Inc.
 */
public class Schema extends com.aoindustries.aoserv.client.Schema {

	private final HeaderTable Header;
	public HeaderTable getHeader() {return Header;}

	private final HttpdBindTable HttpdBind;
	public HttpdBindTable getHttpdBind() {return HttpdBind;}

	private final HttpdServerTable HttpdServer;
	public HttpdServerTable getHttpdServer() {return HttpdServer;}

	private final LocationTable Location;
	public LocationTable getLocation() {return Location;}

	private final RewriteRuleTable RewriteRule;
	public RewriteRuleTable getRewriteRule() {return RewriteRule;}

	private final SiteTable Site;
	public SiteTable getSite() {return Site;}

	private final StaticSiteTable StaticSite;
	public StaticSiteTable getStaticSite() {return StaticSite;}

	private final VirtualHostTable VirtualHost;
	public VirtualHostTable getVirtualHost() {return VirtualHost;}

	private final VirtualHostNameTable VirtualHostName;
	public VirtualHostNameTable getVirtualHostName() {return VirtualHostName;}

	private final List<? extends AOServTable<?,?>> tables;

	public Schema(AOServConnector connector) throws IOException {
		super(connector);

		ArrayList<AOServTable<?,?>> newTables = new ArrayList<>();
		newTables.add(Header = new HeaderTable(connector));
		newTables.add(HttpdBind = new HttpdBindTable(connector));
		newTables.add(HttpdServer = new HttpdServerTable(connector));
		newTables.add(Location = new LocationTable(connector));
		newTables.add(RewriteRule = new RewriteRuleTable(connector));
		newTables.add(Site = new SiteTable(connector));
		newTables.add(StaticSite = new StaticSiteTable(connector));
		newTables.add(VirtualHost = new VirtualHostTable(connector));
		newTables.add(VirtualHostName = new VirtualHostNameTable(connector));
		newTables.trimToSize();
		tables = Collections.unmodifiableList(newTables);
	}

	@Override
	public List<? extends AOServTable<?,?>> getTables() {
		return tables;
	}

	@Override
	public String getName() {
		return "web";
	}
}
