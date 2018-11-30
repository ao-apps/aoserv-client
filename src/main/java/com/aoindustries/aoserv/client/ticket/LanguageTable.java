/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2009, 2016, 2017, 2018  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.ticket;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.GlobalTableStringKey;
import com.aoindustries.aoserv.client.schema.SchemaTable;
import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  Language
 *
 * @author  AO Industries, Inc.
 */
final public class LanguageTable extends GlobalTableStringKey<Language> {

	public LanguageTable(AOServConnector connector) {
		super(connector, Language.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(Language.COLUMN_CODE_name, ASCENDING)
	};
	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public Language get(String code) throws IOException, SQLException {
		return getUniqueRow(Language.COLUMN_CODE, code);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.LANGUAGES;
	}
}
