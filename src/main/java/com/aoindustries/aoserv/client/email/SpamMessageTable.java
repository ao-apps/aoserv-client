/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2003-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

import com.aoapps.hodgepodge.io.TerminalWriter;
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.AOServTable;
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @see  SpamMessage
 *
 * @author  AO Industries, Inc.
 */
public final class SpamMessageTable extends AOServTable<Integer, SpamMessage> {

  SpamMessageTable(AOServConnector connector) {
    super(connector, SpamMessage.class);
  }

  private static final OrderBy[] defaultOrderBy = {
      new OrderBy(SpamMessage.COLUMN_PKEY_name, ASCENDING)
  };

  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  int addSpamEmailMessage(SmtpRelay esr, String message) throws IOException, SQLException {
    return connector.requestIntQueryIL(
        true,
        AoservProtocol.CommandID.ADD,
        Table.TableID.SPAM_EMAIL_MESSAGES,
        esr.getPkey(),
        message
    );
  }

  @Override
  public List<SpamMessage> getRowsCopy() throws IOException, SQLException {
    List<SpamMessage> list = new ArrayList<>();
    getObjects(true, list, AoservProtocol.CommandID.GET_TABLE, Table.TableID.SPAM_EMAIL_MESSAGES);
    return list;
  }

  @Override
  public Table.TableID getTableID() {
    return Table.TableID.SPAM_EMAIL_MESSAGES;
  }

  /**
   * @deprecated  Always try to lookup by specific keys; the compiler will help you more when types change.
   */
  @Deprecated
  @Override
  public SpamMessage get(Object pkey) throws IOException, SQLException {
    if (pkey == null) {
      return null;
    }
    return get(((Integer) pkey).intValue());
  }

  /**
   * @see  #get(java.lang.Object)
   */
  public SpamMessage get(int pkey) throws IOException, SQLException {
    return getObject(true, AoservProtocol.CommandID.GET_OBJECT, Table.TableID.SPAM_EMAIL_MESSAGES, pkey);
  }

  List<SpamMessage> getSpamEmailMessages(SmtpRelay esr) throws IOException, SQLException {
    return getSpamEmailMessages(esr.getPkey());
  }

  List<SpamMessage> getSpamEmailMessages(int esr) throws IOException, SQLException {
    return getObjects(true, AoservProtocol.CommandID.GET_SPAM_EMAIL_MESSAGES_FOR_EMAIL_SMTP_RELAY, esr);
  }

  @Override
  public List<SpamMessage> getIndexedRows(int col, Object value) throws IOException, SQLException {
    if (col == SpamMessage.COLUMN_PKEY) {
      SpamMessage sem = get(value);
      if (sem == null) {
        return Collections.emptyList();
      } else {
        return Collections.singletonList(sem);
      }
    }
    if (col == SpamMessage.COLUMN_EMAIL_RELAY) {
      return getSpamEmailMessages(((Integer) value));
    }
    throw new UnsupportedOperationException("Not an indexed column: " + col);
  }

  @Override
  protected SpamMessage getUniqueRowImpl(int col, Object value) throws IOException, SQLException {
    if (col == SpamMessage.COLUMN_PKEY) {
      return get(value);
    }
    throw new IllegalArgumentException("Not a unique column: " + col);
  }

  @Override
  public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
    String command = args[0];
    if (command.equalsIgnoreCase(Command.ADD_SPAM_EMAIL_MESSAGE)) {
      if (AOSH.checkParamCount(Command.ADD_SPAM_EMAIL_MESSAGE, args, 2, err)) {
        out.println(
            connector.getSimpleAOClient().addSpamEmailMessage(
                AOSH.parseInt(args[1], "email_relay"),
                args[2]
            )
        );
        out.flush();
      }
      return true;
    }
    return false;
  }
}
