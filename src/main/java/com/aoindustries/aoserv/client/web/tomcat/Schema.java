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

	private final ContextTable Context;
	public ContextTable getContext() {return Context;}

	private final ContextDataSourceTable ContextDataSource;
	public ContextDataSourceTable getContextDataSource() {return ContextDataSource;}

	private final ContextParameterTable ContextParameter;
	public ContextParameterTable getContextParameter() {return ContextParameter;}

	private final JkMountTable JkMount;
	public JkMountTable getJkMount() {return JkMount;}

	private final JkProtocolTable JkProtocol;
	public JkProtocolTable getJkProtocol() {return JkProtocol;}

	private final PrivateTomcatSiteTable PrivateTomcatSite;
	public PrivateTomcatSiteTable getPrivateTomcatSite() {return PrivateTomcatSite;}

	private final SharedTomcatTable SharedTomcat;
	public SharedTomcatTable getSharedTomcat() {return SharedTomcat;}

	private final SharedTomcatSiteTable SharedTomcatSite;
	public SharedTomcatSiteTable getSharedTomcatSite() {return SharedTomcatSite;}

	private final SiteTable Site;
	public SiteTable getSite() {return Site;}

	private final VersionTable Version;
	public VersionTable getVersion() {return Version;}

	private final WorkerTable Worker;
	public WorkerTable getWorker() {return Worker;}

	private final WorkerNameTable WorkerName;
	public WorkerNameTable getWorkerName() {return WorkerName;}

	private final List<? extends AOServTable<?,?>> tables;

	public Schema(AOServConnector connector) throws IOException {
		super(connector);

		ArrayList<AOServTable<?,?>> newTables = new ArrayList<>();
		newTables.add(Context = new ContextTable(connector));
		newTables.add(ContextDataSource = new ContextDataSourceTable(connector));
		newTables.add(ContextParameter = new ContextParameterTable(connector));
		newTables.add(JkMount = new JkMountTable(connector));
		newTables.add(JkProtocol = new JkProtocolTable(connector));
		newTables.add(PrivateTomcatSite = new PrivateTomcatSiteTable(connector));
		newTables.add(SharedTomcat = new SharedTomcatTable(connector));
		newTables.add(SharedTomcatSite = new SharedTomcatSiteTable(connector));
		newTables.add(Site = new SiteTable(connector));
		newTables.add(Version = new VersionTable(connector));
		newTables.add(Worker = new WorkerTable(connector));
		newTables.add(WorkerName = new WorkerNameTable(connector));
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
