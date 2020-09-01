/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2018, 2020  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.dns;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.AOServTable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author  AO Industries, Inc.
 */
public class Schema extends com.aoindustries.aoserv.client.Schema {

	private final ForbiddenZoneTable ForbiddenZone;
	public ForbiddenZoneTable getForbiddenZone() {return ForbiddenZone;}

	private final RecordTable Record;
	public RecordTable getRecord() {return Record;}

	private final RecordTypeTable RecordType;
	public RecordTypeTable getRecordType() {return RecordType;}

	private final TopLevelDomainTable TopLevelDomain;
	public TopLevelDomainTable getTopLevelDomain() {return TopLevelDomain;}

	private final ZoneTable Zone;
	public ZoneTable getZone() {return Zone;}

	private final List<? extends AOServTable<?,?>> tables;

	public Schema(AOServConnector connector) {
		super(connector);

		ArrayList<AOServTable<?,?>> newTables = new ArrayList<>();
		newTables.add(ForbiddenZone = new ForbiddenZoneTable(connector));
		newTables.add(Record = new RecordTable(connector));
		newTables.add(RecordType = new RecordTypeTable(connector));
		newTables.add(TopLevelDomain = new TopLevelDomainTable(connector));
		newTables.add(Zone = new ZoneTable(connector));
		newTables.trimToSize();
		tables = Collections.unmodifiableList(newTables);
	}

	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField") // Returning unmodifiable
	public List<? extends AOServTable<?,?>> getTables() {
		return tables;
	}

	@Override
	public String getName() {
		return "dns";
	}
}
