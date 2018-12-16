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
package com.aoindustries.aoserv.client.net.reputation;

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

	private final HostTable Host;
	public HostTable getHost() {return Host;}

	private final LimiterTable Limiter;
	public LimiterTable getLimiter() {return Limiter;}

	private final LimiterClassTable LimiterClass;
	public LimiterClassTable getLimiterClass() {return LimiterClass;}

	private final LimiterSetTable LimiterSet;
	public LimiterSetTable getLimiterSet() {return LimiterSet;}

	private final NetworkTable Network;
	public NetworkTable getNetwork() {return Network;}

	private final SetTable Set;
	public SetTable getSet() {return Set;}

	final List<? extends AOServTable<?,?>> tables;

	public Schema(AOServConnector connector) throws IOException {
		super(connector);

		ArrayList<AOServTable<?,?>> newTables = new ArrayList<>();
		newTables.add(Host = new HostTable(connector));
		newTables.add(Limiter = new LimiterTable(connector));
		newTables.add(LimiterClass = new LimiterClassTable(connector));
		newTables.add(LimiterSet = new LimiterSetTable(connector));
		newTables.add(Network = new NetworkTable(connector));
		newTables.add(Set = new SetTable(connector));
		newTables.trimToSize();
		tables = Collections.unmodifiableList(newTables);
	}

	@Override
	public List<? extends AOServTable<?,?>> getTables() {
		return tables;
	}

	@Override
	public String getName() {
		return "net.reputation";
	}
}
