/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2009, 2016, 2017, 2018, 2020, 2021, 2022  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.master;

import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.account.CachedTableUserNameKey;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  User
 *
 * @author  AO Industries, Inc.
 */
public final class UserTable extends CachedTableUserNameKey<User> {

  UserTable(AoservConnector connector) {
    super(connector, User.class);
  }

  private static final OrderBy[] defaultOrderBy = {
      new OrderBy(User.COLUMN_USERNAME_name, ASCENDING)
  };

  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  @Override
  public User get(com.aoindustries.aoserv.client.account.User.Name username) throws IOException, SQLException {
    return getUniqueRow(User.COLUMN_USERNAME, username);
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.MASTER_USERS;
  }
}
