/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2002-2009, 2016, 2017, 2018  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.postgresql;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.GlobalTableIntegerKey;
import com.aoindustries.aoserv.client.distribution.OperatingSystemVersion;
import com.aoindustries.aoserv.client.distribution.SoftwareVersion;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  Version
 *
 * @author  AO Industries, Inc.
 */
final public class VersionTable extends GlobalTableIntegerKey<Version> {

	public VersionTable(AOServConnector connector) {
		super(connector, Version.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(Version.COLUMN_VERSION_name+'.'+SoftwareVersion.COLUMN_VERSION_name, ASCENDING)
	};
	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public Version get(int version) throws IOException, SQLException {
		return getUniqueRow(Version.COLUMN_VERSION, version);
	}

	public Version getPostgresVersion(String version, OperatingSystemVersion osv) throws IOException, SQLException {
		return get(connector.getTechnologyNames()
			.get(Version.TECHNOLOGY_NAME)
			.getTechnologyVersion(connector, version, osv)
			.getPkey()
		);
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.POSTGRES_VERSIONS;
	}
}
