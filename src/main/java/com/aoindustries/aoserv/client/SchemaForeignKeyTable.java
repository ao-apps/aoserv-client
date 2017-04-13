/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2009, 2016, 2017  AO Industries, Inc.
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @see  SchemaForeignKey
 *
 * @author  AO Industries, Inc.
 */
final public class SchemaForeignKeyTable extends GlobalTableIntegerKey<SchemaForeignKey> {

	private static final Map<String,List<SchemaForeignKey>> tableKeys=new HashMap<>();
	private static final Map<Integer,List<SchemaForeignKey>> referencesHash=new HashMap<>();
	private static final Map<Integer,List<SchemaForeignKey>> referencedByHash=new HashMap<>();

	SchemaForeignKeyTable(AOServConnector connector) {
		super(connector, SchemaForeignKey.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(SchemaForeignKey.COLUMN_PKEY_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public void clearCache() {
		super.clearCache();
		synchronized(SchemaForeignKeyTable.class) {
			tableKeys.clear();
			referencesHash.clear();
			referencedByHash.clear();
		}
	}

	@Override
	public SchemaForeignKey get(int pkey) throws IOException, SQLException {
		return getUniqueRow(SchemaForeignKey.COLUMN_PKEY, pkey);
	}

	List<SchemaForeignKey> getSchemaForeignKeys(SchemaTable table) throws IOException, SQLException {
		synchronized(SchemaForeignKeyTable.class) {
			if(tableKeys.isEmpty()) {
				List<SchemaForeignKey> cached=getRows();
				int size=cached.size();
				for(int c=0;c<size;c++) {
					SchemaForeignKey key=cached.get(c);
					String tableName=key.getKeyColumn(connector).table_name;
					List<SchemaForeignKey> keys=tableKeys.get(tableName);
					if(keys==null) tableKeys.put(tableName, keys=new ArrayList<>());
					keys.add(key);
				}
			}
			List<SchemaForeignKey> matches=tableKeys.get(table.getName());
			if(matches!=null) return matches;
			return Collections.emptyList();
		}
	}

	private void rebuildReferenceHashes() throws IOException, SQLException {
		if(
			referencedByHash.isEmpty()
			|| referencesHash.isEmpty()
		) {
			// All methods that call this are already synched
			List<SchemaForeignKey> cached=getRows();
			int size=cached.size();
			for(int c=0;c<size;c++) {
				SchemaForeignKey key=cached.get(c);
				Integer keyColumnPKey=key.key_column;
				Integer foreignColumnPKey=key.foreign_column;

				// Referenced By
				List<SchemaForeignKey> referencedBy=referencedByHash.get(keyColumnPKey);
				if(referencedBy==null) referencedByHash.put(keyColumnPKey, referencedBy=new ArrayList<>());
				referencedBy.add(key);

				// References
				List<SchemaForeignKey> references=referencesHash.get(foreignColumnPKey);
				if(references==null) referencesHash.put(foreignColumnPKey, references=new ArrayList<>());
				references.add(key);
			}
		}
	}

	List<SchemaForeignKey> getSchemaForeignKeysReferencedBy(SchemaColumn column) throws IOException, SQLException {
		synchronized(SchemaForeignKeyTable.class) {
			rebuildReferenceHashes();
			List<SchemaForeignKey> matches=referencedByHash.get(column.getPkey());
			if(matches!=null) return matches;
			else return Collections.emptyList();
		}
	}

	List<SchemaForeignKey> getSchemaForeignKeysReferencing(SchemaColumn column) throws IOException, SQLException {
		synchronized(SchemaForeignKeyTable.class) {
			rebuildReferenceHashes();
			List<SchemaForeignKey> matches=referencesHash.get(column.getPkey());
			if(matches!=null) return matches;
			else return Collections.emptyList();
		}
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.SCHEMA_FOREIGN_KEYS;
	}
}
