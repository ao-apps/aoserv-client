/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2023  AO Industries, Inc.
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
import com.aoapps.lang.validation.ValidationException;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.CannotRemoveReason;
import com.aoindustries.aoserv.client.Removable;
import com.aoindustries.aoserv.client.linux.User;
import com.aoindustries.aoserv.client.linux.UserServer;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * A <code>LinuxAccount</code> may have any number of email
 * addresses routed to it to become an email inbox.
 * <code>LinuxAccAddress</code>es make this connection.  Once
 * email arrives in the inbox, it may be retrieved via one of
 * several mail protocols, including the common POP3 and IMAP.
 *
 * @see  User
 * @see  Address
 *
 * @author  AO Industries, Inc.
 */
public final class InboxAddress extends CachedObjectIntegerKey<InboxAddress> implements Removable {

  static final int COLUMN_PKEY = 0;
  static final int COLUMN_EMAIL_ADDRESS = 1;
  static final int COLUMN_LINUX_SERVER_ACCOUNT = 2;
  static final String COLUMN_EMAIL_ADDRESS_name = "email_address";
  static final String COLUMN_LINUX_SERVER_ACCOUNT_name = "linux_server_account";

  private int emailAddress;
  private int linuxServerAccount;

  // Protocol conversion <= 1.30:
  private User.Name linuxAccount;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated // Java 9: (forRemoval = true)
  public InboxAddress() {
    // Do nothing
  }

  @Override
  protected Object getColumnImpl(int i) {
    if (i == COLUMN_PKEY) {
      return pkey;
    }
    if (i == COLUMN_EMAIL_ADDRESS) {
      return emailAddress;
    }
    if (i == COLUMN_LINUX_SERVER_ACCOUNT) {
      return linuxServerAccount;
    }
    throw new IllegalArgumentException("Invalid index: " + i);
  }

  public int getEmailAddress_id() {
    return emailAddress;
  }

  public Address getEmailAddress() throws SQLException, IOException {
    Address emailAddressObject = table.getConnector().getEmail().getAddress().get(emailAddress);
    if (emailAddressObject == null) {
      throw new SQLException("Unable to find EmailAddress: " + emailAddress);
    }
    return emailAddressObject;
  }

  public int getLinuxServerAccount_id() {
    return linuxServerAccount;
  }

  public UserServer getLinuxServerAccount() throws SQLException, IOException {
    UserServer lsa = table.getConnector().getLinux().getUserServer().get(linuxServerAccount);
    if (lsa == null) {
      throw new SQLException("Unable to find LinuxServerAccount: " + linuxServerAccount);
    }
    return lsa;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.LINUX_ACC_ADDRESSES;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    try {
      int pos = 1;
      pkey = result.getInt(pos++);
      emailAddress = result.getInt(pos++);
      linuxServerAccount = result.getInt(pos++);
      linuxAccount = User.Name.valueOf(result.getString(pos++));
    } catch (ValidationException e) {
      throw new SQLException(e);
    }
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    pkey = in.readCompressedInt();
    emailAddress = in.readCompressedInt();
    linuxServerAccount = in.readCompressedInt();
  }

  @Override
  public List<CannotRemoveReason<?>> getCannotRemoveReasons() {
    return Collections.emptyList();
  }

  @Override
  public void remove() throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(
        true,
        AoservProtocol.CommandId.REMOVE,
        Table.TableId.LINUX_ACC_ADDRESSES,
        pkey
    );
  }

  @Override
  public String toStringImpl() throws SQLException, IOException {
    return getEmailAddress().toStringImpl() + "→" + getLinuxServerAccount().toStringImpl();
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeCompressedInt(emailAddress);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_30) <= 0) {
      out.writeUTF(linuxAccount.toString());
    } else {
      out.writeCompressedInt(linuxServerAccount);
    }
  }
}
