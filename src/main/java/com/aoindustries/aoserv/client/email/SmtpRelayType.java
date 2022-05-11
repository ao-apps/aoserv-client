/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2003-2009, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoindustries.aoserv.client.GlobalObjectStringKey;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The <code>EmailSmtpRelayType</code> of an <code>EmailSmtpRelay</code>
 * controls the servers response.
 *
 * @see  SmtpRelay
 *
 * @author  AO Industries, Inc.
 */
public final class SmtpRelayType extends GlobalObjectStringKey<SmtpRelayType> {

  static final int COLUMN_NAME = 0;
  static final String COLUMN_NAME_name = "name";

  /**
   * The different relay types.
   */
  public static final String
      ALLOW = "allow",
      ALLOW_RELAY = "allow_relay",
      DENY_SPAM = "deny_spam",
      DENY = "deny";

  private String sendmailConfig;
  private String qmailConfig;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated // Java 9: (forRemoval = true)
  public SmtpRelayType() {
    // Do nothing
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_NAME:
        return pkey;
      case 1:
        return sendmailConfig;
      case 2:
        return qmailConfig;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public String getName() {
    return pkey;
  }

  public String getSendmailConfig() {
    return sendmailConfig;
  }

  public String getQmailConfig() {
    return qmailConfig;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.EMAIL_SMTP_RELAY_TYPES;
  }

  public String getVerb() throws SQLException {
    if (pkey.equals(ALLOW)) {
      return "allowed regular access";
    }
    if (pkey.equals(ALLOW_RELAY)) {
      return "allowed unauthenticated relay access";
    }
    if (pkey.equals(DENY_SPAM)) {
      return "blocked for sending unsolicited bulk email";
    }
    if (pkey.equals(DENY)) {
      return "blocked";
    }
    throw new SQLException("Unknown value for name: " + pkey);
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    pkey = result.getString(1);
    sendmailConfig = result.getString(2);
    qmailConfig = result.getString(3);
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    pkey = in.readUTF().intern();
    sendmailConfig = in.readUTF();
    qmailConfig = in.readUTF();
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeUTF(pkey);
    out.writeUTF(sendmailConfig);
    out.writeUTF(qmailConfig);
  }
}
