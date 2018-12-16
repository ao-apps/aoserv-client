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
package com.aoindustries.aoserv.client.web.tomcat;

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

	private final ContextTable httpdTomcatContexts;
	public ContextTable getHttpdTomcatContexts() {return httpdTomcatContexts;}

	private final ContextDataSourceTable httpdTomcatDataSources;
	public ContextDataSourceTable getHttpdTomcatDataSources() {return httpdTomcatDataSources;}

	private final ContextParameterTable httpdTomcatParameters;
	public ContextParameterTable getHttpdTomcatParameters() {return httpdTomcatParameters;}

	private final JkMountTable httpdTomcatSiteJkMounts;
	public JkMountTable getHttpdTomcatSiteJkMounts() {return httpdTomcatSiteJkMounts;}

	private final JkProtocolTable httpdJKProtocols;
	public JkProtocolTable getHttpdJKProtocols() {return httpdJKProtocols;}

	private final PrivateTomcatSiteTable httpdTomcatStdSites;
	public PrivateTomcatSiteTable getHttpdTomcatStdSites() {return httpdTomcatStdSites;}

	private final SharedTomcatTable httpdSharedTomcats;
	public SharedTomcatTable getHttpdSharedTomcats() {return httpdSharedTomcats;}

	private final SharedTomcatSiteTable httpdTomcatSharedSites;
	public SharedTomcatSiteTable getHttpdTomcatSharedSites() {return httpdTomcatSharedSites;}

	private final SiteTable httpdTomcatSites;
	public SiteTable getHttpdTomcatSites() {return httpdTomcatSites;}

	private final VersionTable httpdTomcatVersions;
	public VersionTable getHttpdTomcatVersions() {return httpdTomcatVersions;}

	private final WorkerTable httpdWorkers;
	public WorkerTable getHttpdWorkers() {return httpdWorkers;}

	private final WorkerNameTable httpdJKCodes;
	public WorkerNameTable getHttpdJKCodes() {return httpdJKCodes;}

	final List<? extends AOServTable<?,?>> tables;

	public Schema(AOServConnector connector) throws IOException {
		super(connector);

		ArrayList<AOServTable<?,?>> newTables = new ArrayList<>();
		newTables.add(httpdTomcatContexts = new ContextTable(connector));
		newTables.add(httpdTomcatDataSources = new ContextDataSourceTable(connector));
		newTables.add(httpdTomcatParameters = new ContextParameterTable(connector));
		newTables.add(httpdTomcatSiteJkMounts = new JkMountTable(connector));
		newTables.add(httpdJKProtocols = new JkProtocolTable(connector));
		newTables.add(httpdTomcatStdSites = new PrivateTomcatSiteTable(connector));
		newTables.add(httpdSharedTomcats = new SharedTomcatTable(connector));
		newTables.add(httpdTomcatSharedSites = new SharedTomcatSiteTable(connector));
		newTables.add(httpdTomcatSites = new SiteTable(connector));
		newTables.add(httpdTomcatVersions = new VersionTable(connector));
		newTables.add(httpdWorkers = new WorkerTable(connector));
		newTables.add(httpdJKCodes = new WorkerNameTable(connector));
		newTables.trimToSize();
		tables = Collections.unmodifiableList(newTables);
	}

	@Override
	public List<? extends AOServTable<?,?>> getTables() {
		return tables;
	}

	@Override
	public String getName() {
		return "web.tomcat";
	}
}
