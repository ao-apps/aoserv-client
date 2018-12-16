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
package com.aoindustries.aoserv.client.distribution;

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

	private final ArchitectureTable architectureTable;
	public ArchitectureTable getArchitectures() {
		return architectureTable;
	}

	private final OperatingSystemTable operatingSystemTable;
	public OperatingSystemTable getOperatingSystems() {
		return operatingSystemTable;
	}

	private final OperatingSystemVersionTable operatingSystemVersionTable;
	public OperatingSystemVersionTable getOperatingSystemVersions() {
		return operatingSystemVersionTable;
	}

	private final SoftwareTable softwareTable;
	public SoftwareTable getTechnologyNames() {
		return softwareTable;
	}

	private final SoftwareCategorizationTable softwareCategorizationTable;
	public SoftwareCategorizationTable getTechnologies() {
		return softwareCategorizationTable;
	}

	private final SoftwareCategoryTable softwareCategoryTable;
	public SoftwareCategoryTable getTechnologyClasses() {
		return softwareCategoryTable;
	}

	private final SoftwareVersionTable softwareVersionTable;
	public SoftwareVersionTable getTechnologyVersions() {
		return softwareVersionTable;
	}

	final List<? extends AOServTable<?,?>> tables;

	public Schema(AOServConnector connector) throws IOException {
		super(connector);

		ArrayList<AOServTable<?,?>> newTables = new ArrayList<>();
		newTables.add(architectureTable = new ArchitectureTable(connector));
		newTables.add(operatingSystemTable = new OperatingSystemTable(connector));
		newTables.add(operatingSystemVersionTable = new OperatingSystemVersionTable(connector));
		newTables.add(softwareTable = new SoftwareTable(connector));
		newTables.add(softwareCategorizationTable = new SoftwareCategorizationTable(connector));
		newTables.add(softwareCategoryTable = new SoftwareCategoryTable(connector));
		newTables.add(softwareVersionTable = new SoftwareVersionTable(connector));
		newTables.trimToSize();
		tables = Collections.unmodifiableList(newTables);
	}

	@Override
	public List<? extends AOServTable<?,?>> getTables() {
		return tables;
	}

	@Override
	public String getName() {
		return "distribution";
	}
}
