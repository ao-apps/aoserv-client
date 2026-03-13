/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2026  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.email;

import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.dns.Record;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * @see  DkimKey
 *
 * @author  AO Industries, Inc.
 */
public final class DkimKeyTable extends CachedTableIntegerKey<DkimKey> {

  DkimKeyTable(AoservConnector connector) {
    super(connector, DkimKey.class);
  }

  private static final OrderBy[] defaultOrderBy = {
      new OrderBy(DkimKey.COLUMN_DOMAIN_name + '.' + Domain.COLUMN_DOMAIN_name, ASCENDING),
      new OrderBy(DkimKey.COLUMN_DOMAIN_name + '.' + Domain.COLUMN_AO_SERVER_name + '.' + Server.COLUMN_HOSTNAME_name, ASCENDING),
      new OrderBy(DkimKey.COLUMN_SELECTOR_name, ASCENDING)
  };

  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  @Override
  public DkimKey get(int id) throws IOException, SQLException {
    return getUniqueRow(DkimKey.COLUMN_ID, id);
  }

  public Optional<DkimKey> getDkimKeyByDnsRecord(Record record) throws IOException, SQLException {
    return Optional.ofNullable(getUniqueRow(DkimKey.COLUMN_DNS_RECORD, record.getId()));
  }

  public List<DkimKey> getDkimKeys(Domain domain) throws IOException, SQLException {
    return getIndexedRows(DkimKey.COLUMN_DOMAIN, domain.getPkey());
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.email_DkimKey;
  }
}
