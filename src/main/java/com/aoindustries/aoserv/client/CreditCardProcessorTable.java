/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2007-2009, 2016, 2017  AO Industries, Inc.
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
import java.util.List;

/**
 * @see  CreditCardProcessor
 *
 * @author  AO Industries, Inc.
 */
final public class CreditCardProcessorTable extends CachedTableStringKey<CreditCardProcessor> {

	CreditCardProcessorTable(AOServConnector connector) {
		super(connector, CreditCardProcessor.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(CreditCardProcessor.COLUMN_ACCOUNTING_name, ASCENDING),
		new OrderBy(CreditCardProcessor.COLUMN_PROVIDER_ID_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public CreditCardProcessor get(String providerId) throws IOException, SQLException {
		return getUniqueRow(CreditCardProcessor.COLUMN_PROVIDER_ID, providerId);
	}

	List<CreditCardProcessor> getCreditCardProcessors(Business business) throws IOException, SQLException {
		return getIndexedRows(CreditCardProcessor.COLUMN_ACCOUNTING, business.pkey);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.CREDIT_CARD_PROCESSORS;
	}
}
