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
package com.aoindustries.aoserv.client.schema;

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

	private final AoservProtocolTable aoservProtocolTable;
	public AoservProtocolTable getAoservProtocols() {return aoservProtocolTable;}

	private final ColumnTable columnTable;
	public ColumnTable getSchemaColumns() {return columnTable;}

	private final ForeignKeyTable foreignKeyTable;
	public ForeignKeyTable getSchemaForeignKeys() {return foreignKeyTable;}

	private final TableTable tableTable;
	public TableTable getSchemaTables() {return tableTable;}

	private final TypeTable typeTable;
	public TypeTable getSchemaTypes() {return typeTable;}

	final List<? extends AOServTable<?,?>> tables;

	public Schema(AOServConnector connector) throws IOException {
		super(connector);

		ArrayList<AOServTable<?,?>> newTables = new ArrayList<>();
		newTables.add(aoservProtocolTable = new AoservProtocolTable(connector));
		newTables.add(columnTable = new ColumnTable(connector));
		newTables.add(foreignKeyTable = new ForeignKeyTable(connector));
		newTables.add(tableTable = new TableTable(connector));
		newTables.add(typeTable = new TypeTable(connector));
		newTables.trimToSize();
		tables = Collections.unmodifiableList(newTables);
	}

	@Override
	public List<? extends AOServTable<?,?>> getTables() {
		return tables;
	}

	@Override
	public String getName() {
		return "schema";
	}
}
