/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2018, 2020, 2021  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.distribution;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.AOServTable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author  AO Industries, Inc.
 */
public class Schema extends com.aoindustries.aoserv.client.Schema {

	private final ArchitectureTable Architecture;
	public ArchitectureTable getArchitecture() {
		return Architecture;
	}

	private final OperatingSystemTable OperatingSystem;
	public OperatingSystemTable getOperatingSystem() {
		return OperatingSystem;
	}

	private final OperatingSystemVersionTable OperatingSystemVersion;
	public OperatingSystemVersionTable getOperatingSystemVersion() {
		return OperatingSystemVersion;
	}

	private final SoftwareTable Software;
	public SoftwareTable getSoftware() {
		return Software;
	}

	private final SoftwareCategorizationTable SoftwareCategorization;
	public SoftwareCategorizationTable getSoftwareCategorization() {
		return SoftwareCategorization;
	}

	private final SoftwareCategoryTable SoftwareCategory;
	public SoftwareCategoryTable getSoftwareCategory() {
		return SoftwareCategory;
	}

	private final SoftwareVersionTable SoftwareVersion;
	public SoftwareVersionTable getSoftwareVersion() {
		return SoftwareVersion;
	}

	private final List<? extends AOServTable<?, ?>> tables;

	public Schema(AOServConnector connector) {
		super(connector);

		ArrayList<AOServTable<?, ?>> newTables = new ArrayList<>();
		newTables.add(Architecture = new ArchitectureTable(connector));
		newTables.add(OperatingSystem = new OperatingSystemTable(connector));
		newTables.add(OperatingSystemVersion = new OperatingSystemVersionTable(connector));
		newTables.add(Software = new SoftwareTable(connector));
		newTables.add(SoftwareCategorization = new SoftwareCategorizationTable(connector));
		newTables.add(SoftwareCategory = new SoftwareCategoryTable(connector));
		newTables.add(SoftwareVersion = new SoftwareVersionTable(connector));
		newTables.trimToSize();
		tables = Collections.unmodifiableList(newTables);
	}

	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField") // Returning unmodifiable
	public List<? extends AOServTable<?, ?>> getTables() {
		return tables;
	}

	@Override
	public String getName() {
		return "distribution";
	}
}
