/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2017, 2018, 2020, 2021, 2022  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.pki.Certificate;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  SendmailServer
 *
 * @author  AO Industries, Inc.
 */
public final class SendmailServerTable extends CachedTableIntegerKey<SendmailServer> {

  SendmailServerTable(AoservConnector connector) {
    super(connector, SendmailServer.class);
  }

  private static final OrderBy[] defaultOrderBy = {
      new OrderBy(SendmailServer.COLUMN_AO_SERVER_name + '.' + Server.COLUMN_HOSTNAME_name, ASCENDING),
      new OrderBy(SendmailServer.COLUMN_NAME_name, ASCENDING)
  };

  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  @Override
  public SendmailServer get(int id) throws IOException, SQLException {
    return getUniqueRow(SendmailServer.COLUMN_ID, id);
  }

  public List<SendmailServer> getSendmailServers(Server ao) throws IOException, SQLException {
    return getIndexedRows(SendmailServer.COLUMN_AO_SERVER, ao.getPkey());
  }

  public List<SendmailServer> getSendmailServers(Package pk) throws IOException, SQLException {
    return getIndexedRows(SendmailServer.COLUMN_PACKAGE, pk.getPkey());
  }

  public List<SendmailServer> getSendmailServersByServerCertificate(Certificate sslCert) throws IOException, SQLException {
    return getIndexedRows(SendmailServer.COLUMN_SERVER_CERTIFICATE, sslCert.getPkey());
  }

  public List<SendmailServer> getSendmailServersByClientCertificate(Certificate sslCert) throws IOException, SQLException {
    return getIndexedRows(SendmailServer.COLUMN_CLIENT_CERTIFICATE, sslCert.getPkey());
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.SENDMAIL_SERVERS;
  }
}
