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

	private final HeaderTable httpdSiteBindHeaders;
	public HeaderTable getHttpdSiteBindHeaders() {return httpdSiteBindHeaders;}

	private final HttpdBindTable httpdBinds;
	public HttpdBindTable getHttpdBinds() {return httpdBinds;}

	private final HttpdServerTable httpdServers;
	public HttpdServerTable getHttpdServers() {return httpdServers;}

	private final LocationTable httpdSiteAuthenticatedLocationTable;
	public LocationTable getHttpdSiteAuthenticatedLocationTable() {return httpdSiteAuthenticatedLocationTable;}

	private final RewriteRuleTable rewriteRuleTable;
	public RewriteRuleTable getRewriteRuleTable() {return rewriteRuleTable;}

	private final SiteTable httpdSites;
	public SiteTable getHttpdSites() {return httpdSites;}

	private final StaticSiteTable httpdStaticSites;
	public StaticSiteTable getHttpdStaticSites() {return httpdStaticSites;}

	private final VirtualHostTable httpdSiteBinds;
	public VirtualHostTable getHttpdSiteBinds() {return httpdSiteBinds;}

	private final VirtualHostNameTable httpdSiteURLs;
	public VirtualHostNameTable getHttpdSiteURLs() {return httpdSiteURLs;}

	final List<? extends AOServTable<?,?>> tables;

	public Schema(AOServConnector connector) throws IOException {
		super(connector);

		ArrayList<AOServTable<?,?>> newTables = new ArrayList<>();
		newTables.add(httpdSiteBindHeaders = new HeaderTable(connector));
		newTables.add(httpdBinds = new HttpdBindTable(connector));
		newTables.add(httpdServers = new HttpdServerTable(connector));
		newTables.add(httpdSiteAuthenticatedLocationTable = new LocationTable(connector));
		newTables.add(rewriteRuleTable = new RewriteRuleTable(connector));
		newTables.add(httpdSites = new SiteTable(connector));
		newTables.add(httpdStaticSites = new StaticSiteTable(connector));
		newTables.add(httpdSiteBinds = new VirtualHostTable(connector));
		newTables.add(httpdSiteURLs = new VirtualHostNameTable(connector));
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
