/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2003-2009, 2016, 2017  AO Industries, Inc.
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
package com.aoindustries.aoserv.client;

import java.io.IOException;
import java.sql.SQLException;

/**
 * All of the operating system versions referenced from other tables.
 *
 * @see OperatingSystemVersion
 *
 * @author  AO Industries, Inc.
 */
final public class OperatingSystemVersionTable extends GlobalTableIntegerKey<OperatingSystemVersion> {

	OperatingSystemVersionTable(AOServConnector connector) {
		super(connector, OperatingSystemVersion.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(OperatingSystemVersion.COLUMN_SORT_ORDER_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	OperatingSystemVersion getOperatingSystemVersion(OperatingSystem os, String version, Architecture architecture) throws IOException, SQLException {
		String name=os.pkey;
		String arch=architecture.pkey;
		for(OperatingSystemVersion osv : getRows()) {
			if(
				osv.version_name.equals(name)
				&& osv.version_number.equals(version)
				&& osv.architecture.equals(arch)
			) return osv;
		}
		return null;
	}

	@Override
	public OperatingSystemVersion get(int pkey) throws IOException, SQLException {
		return getUniqueRow(OperatingSystemVersion.COLUMN_PKEY, pkey);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.OPERATING_SYSTEM_VERSIONS;
	}
}
