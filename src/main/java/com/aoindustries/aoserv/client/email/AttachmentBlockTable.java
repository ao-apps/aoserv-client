/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2004-2009, 2016, 2017, 2018, 2020, 2022  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.linux.UserServer;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  InboxAddress
 *
 * @author  AO Industries, Inc.
 */
public final class AttachmentBlockTable extends CachedTableIntegerKey<AttachmentBlock> {

  AttachmentBlockTable(AoservConnector connector) {
    super(connector, AttachmentBlock.class);
  }

  private static final OrderBy[] defaultOrderBy = {
      new OrderBy(AttachmentBlock.COLUMN_LINUX_SERVER_ACCOUNT_name + '.' + UserServer.COLUMN_USERNAME_name, ASCENDING),
      new OrderBy(AttachmentBlock.COLUMN_LINUX_SERVER_ACCOUNT_name + '.' + UserServer.COLUMN_AO_SERVER_name + '.' + Server.COLUMN_HOSTNAME_name, ASCENDING),
      new OrderBy(AttachmentBlock.COLUMN_EXTENSION_name, ASCENDING)
  };

  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  @Override
  public AttachmentBlock get(int pkey) throws IOException, SQLException {
    return getUniqueRow(AttachmentBlock.COLUMN_PKEY, pkey);
  }

  public List<AttachmentBlock> getEmailAttachmentBlocks(UserServer lsa) throws IOException, SQLException {
    return getIndexedRows(AttachmentBlock.COLUMN_LINUX_SERVER_ACCOUNT, lsa.getPkey());
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.EMAIL_ATTACHMENT_BLOCKS;
  }
}
