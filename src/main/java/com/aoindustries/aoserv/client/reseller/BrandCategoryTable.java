/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2009, 2016, 2017, 2018, 2019, 2020, 2021  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.reseller;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.ticket.Ticket;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @see BrandCategory
 * @see Ticket
 *
 * @author  AO Industries, Inc.
 */
public final class BrandCategoryTable extends CachedTableIntegerKey<BrandCategory> {

	BrandCategoryTable(AOServConnector connector) {
		super(connector, BrandCategory.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(BrandCategory.COLUMN_BRAND_name, ASCENDING),
		new OrderBy(BrandCategory.COLUMN_CATEGORY_name, ASCENDING)
	};
	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public BrandCategory get(int pkey) throws IOException, SQLException {
		return getUniqueRow(BrandCategory.COLUMN_PKEY, pkey);
	}

	List<BrandCategory> getTicketBrandCategories(Brand brand) throws IOException, SQLException {
		return getIndexedRows(BrandCategory.COLUMN_BRAND, brand.getAccount_name());
	}

	List<BrandCategory> getTicketBrandCategories(Category category) throws IOException, SQLException {
		return getIndexedRows(BrandCategory.COLUMN_CATEGORY, category.getPkey());
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.TICKET_BRAND_CATEGORIES;
	}
}
