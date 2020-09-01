/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2019, 2020  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.billing;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.GlobalTableStringKey;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  Currency
 *
 * @author  AO Industries, Inc.
 */
public final class CurrencyTable extends GlobalTableStringKey<Currency> {

	CurrencyTable(AOServConnector connector) {
		super(connector, Currency.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(Currency.COLUMN_currencyCode_name, ASCENDING)
	};
	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public Currency get(String currencyCode) throws IOException, SQLException {
		return getUniqueRow(Currency.COLUMN_currencyCode, currencyCode);
	}

	/**
	 * @see  #get(java.lang.String)
	 */
	public Currency get(java.util.Currency currency) throws IOException, SQLException {
		if(currency == null) return null;
		return get(currency.getCurrencyCode());		
	}

	/**
	 * @see  #get(java.lang.String)
	 * @see  #get(java.util.Currency)
	 *
	 * @deprecated  Always try to lookup by specific keys; the compiler will help you more when types change.
	 */
	@Deprecated
	@Override
	public Currency get(Object pkey) throws IOException, SQLException {
		if(pkey == null) return null;
		if(pkey instanceof String) return get((String)pkey);
		else if(pkey instanceof java.util.Currency) return get((java.util.Currency)pkey);
		else throw new IllegalArgumentException("pkey is neither a String nor a Currency: " + pkey);
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.Currency;
	}
}
