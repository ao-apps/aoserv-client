/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2023, 2025  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.CannotRemoveReason;
import com.aoindustries.aoserv.client.Removable;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * An {@link List} may receive email on multiple addresses, and
 * then forward those emails to the list of destinations.  An
 * <code>EmailListAddress</code> directs incoming emails to the email list.
 *
 * @see  List
 * @see  Address
 *
 * @author  AO Industries, Inc.
 */
public final class ListAddress extends CachedObjectIntegerKey<ListAddress> implements Removable {

  static final int COLUMN_PKEY = 0;
  static final int COLUMN_EMAIL_ADDRESS = 1;
  static final int COLUMN_EMAIL_LIST = 2;
  static final String COLUMN_EMAIL_ADDRESS_name = "email_address";
  static final String COLUMN_EMAIL_LIST_name = "email_list";

  private int emailAddress;
  private int emailList;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public ListAddress() {
    // Do nothing
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_PKEY:
        return pkey;
      case COLUMN_EMAIL_ADDRESS:
        return emailAddress;
      case COLUMN_EMAIL_LIST:
        return emailList;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public int getEmailAddress_pkey() {
    return emailAddress;
  }

  public Address getEmailAddress() throws SQLException, IOException {
    Address emailAddressObject = table.getConnector().getEmail().getAddress().get(emailAddress);
    if (emailAddressObject == null) {
      throw new SQLException("Unable to find EmailAddress: " + emailAddress);
    }
    return emailAddressObject;
  }

  public int getEmailList_pkey() {
    return emailList;
  }

  public List getEmailList() throws SQLException, IOException {
    List emailListObject = table.getConnector().getEmail().getList().get(emailList);
    if (emailListObject == null) {
      throw new SQLException("Unable to find EmailList: " + emailList);
    }
    return emailListObject;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.EMAIL_LIST_ADDRESSES;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    pkey = result.getInt(1);
    emailAddress = result.getInt(2);
    emailList = result.getInt(3);
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    pkey = in.readCompressedInt();
    emailAddress = in.readCompressedInt();
    emailList = in.readCompressedInt();
  }

  @Override
  public java.util.List<CannotRemoveReason<MajordomoList>> getCannotRemoveReasons() throws SQLException, IOException {
    java.util.List<CannotRemoveReason<MajordomoList>> reasons = new ArrayList<>();

    // Cannot be used as the list for a majordomo list
    for (MajordomoList ml : table.getConnector().getEmail().getMajordomoList().getRows()) {
      if (ml.getListListAddress().getPkey() == pkey) {
        Domain ed = ml.getMajordomoServer().getDomain();
        reasons.add(new CannotRemoveReason<>("Used by Majordomo list " + ml.getName() + '@' + ed.getDomain() + " on " + ed.getLinuxServer().getHostname(), ml));
      }
    }

    return reasons;
  }

  @Override
  public void remove() throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(
        true,
        AoservProtocol.CommandId.REMOVE,
        Table.TableId.EMAIL_LIST_ADDRESSES,
        pkey
    );
  }

  @Override
  public String toStringImpl() throws SQLException, IOException {
    return getEmailAddress().toStringImpl() + "→" + getEmailList().getPath();
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeCompressedInt(emailAddress);
    out.writeCompressedInt(emailList);
  }
}
