/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2024  AO Industries, Inc.
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
import com.aoapps.net.DomainName;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A <code>MajordomoList</code> is one list within a <code>MajordomoServer</code>.
 *
 * @see  MajordomoServer
 *
 * @author  AO Industries, Inc.
 */
public final class MajordomoList extends CachedObjectIntegerKey<MajordomoList> {

  static final int COLUMN_EMAIL_LIST = 0;
  static final int COLUMN_MAJORDOMO_SERVER = 1;
  static final String COLUMN_NAME_name = "name";
  static final String COLUMN_MAJORDOMO_SERVER_name = "majordomo_server";

  /**
   * The maximum length of an email list name.
   */
  public static final int MAX_NAME_LENGTH = 64;

  private int majordomoServer;
  private String name;
  private int listnamePipeAdd;
  private int listnameListAdd;
  private int ownerListnameAdd;
  private int listnameOwnerAdd;
  private int listnameApprovalAdd;
  private int listnameRequestPipeAdd;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated // Java 9: (forRemoval = true)
  public MajordomoList() {
    // Do nothing
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_EMAIL_LIST:
        return pkey;
      case COLUMN_MAJORDOMO_SERVER:
        return majordomoServer;
      case 2:
        return name;
      case 3:
        return listnamePipeAdd;
      case 4:
        return listnameListAdd;
      case 5:
        return ownerListnameAdd;
      case 6:
        return listnameOwnerAdd;
      case 7:
        return listnameApprovalAdd;
      case 8:
        return listnameRequestPipeAdd;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public static String getDefaultInfoFile(DomainName domain, String listName) {
    return
        "Information about the " + listName + " mailing list:\n"
            + "\n"
            + "HOW TO POST A MESSAGE TO THE LIST:\n"
            + "Just send an email message to " + listName + "@" + domain + ". The message will\n"
            + "be distributed to all the members of the list.\n"
            + "\n"
            + "HOW TO UNSUBSCRIBE:\n"
            + "Send an email message to majordomo@" + domain + " with one line in the\n"
            + "body of the message:\n"
            + "\n"
            + "unsubscribe " + listName + "\n"
            + "\n"
            + "\n"
            + "FOR QUESTIONS:\n"
            + "If you ever need to get in contact with the owner of the list,\n"
            + "(if you have trouble unsubscribing, or have questions about the\n"
            + "list itself) send email to owner-" + listName + '@' + domain + ".\n";
  }

  public String getDefaultInfoFile() throws SQLException, IOException {
    return getDefaultInfoFile(getMajordomoServer().getDomain().getDomain(), name);
  }

  public static String getDefaultIntroFile(DomainName domain, String listName) {
    return
        "Welcome to the " + listName + " mailing list.\n"
            + "\n"
            + "Please save this message for future reference.\n"
            + "\n"
            + "HOW TO POST A MESSAGE TO THE LIST:\n"
            + "Just send an email message to " + listName + '@' + domain + ". The message will\n"
            + "be distributed to all the members of the list.\n"
            + "\n"
            + "HOW TO UNSUBSCRIBE:\n"
            + "Send an email message to majordomo@" + domain + " with one line in the\n"
            + "body of the message:\n"
            + "\n"
            + "unsubscribe " + listName + "\n"
            + "\n"
            + "\n"
            + "FOR QUESTIONS:\n"
            + "If you ever need to get in contact with the owner of the list,\n"
            + "(if you have trouble unsubscribing, or have questions about the\n"
            + "list itself) send email to owner-" + listName + '@' + domain + ".\n";
  }

  public String getDefaultIntroFile() throws SQLException, IOException {
    return getDefaultIntroFile(getMajordomoServer().getDomain().getDomain(), name);
  }

  public List getEmailList() throws SQLException, IOException {
    List obj = table.getConnector().getEmail().getList().get(pkey);
    if (obj == null) {
      throw new SQLException("Unable to find EmailList: " + pkey);
    }
    return obj;
  }

  /**
   * Gets the info file for the list.
   */
  public String getInfoFile() throws IOException, SQLException {
    return table.getConnector().requestStringQuery(true, AoservProtocol.CommandId.GET_MAJORDOMO_INFO_FILE, pkey);
  }

  /**
   * Gets the intro file for the list.
   */
  public String getIntroFile() throws IOException, SQLException {
    return table.getConnector().requestStringQuery(true, AoservProtocol.CommandId.GET_MAJORDOMO_INTRO_FILE, pkey);
  }

  public PipeAddress getListPipeAddress() throws SQLException, IOException {
    PipeAddress pipeAddress = table.getConnector().getEmail().getPipeAddress().get(listnamePipeAdd);
    if (pipeAddress == null) {
      throw new SQLException("Unable to find EmailPipeAddress: " + listnamePipeAdd);
    }
    return pipeAddress;
  }

  public int getListApprovalAddress_id() {
    return listnameApprovalAdd;
  }

  public Address getListApprovalAddress() throws SQLException, IOException {
    Address address = table.getConnector().getEmail().getAddress().get(listnameApprovalAdd);
    if (address == null) {
      throw new SQLException("Unable to find EmailAddress: " + listnameApprovalAdd);
    }
    return address;
  }

  public ListAddress getListListAddress() throws SQLException, IOException {
    ListAddress listAddress = table.getConnector().getEmail().getListAddress().get(listnameListAdd);
    if (listAddress == null) {
      throw new SQLException("Unable to find EmailListAddress: " + listnameListAdd);
    }
    return listAddress;
  }

  public int getListOwnerAddress_id() {
    return listnameOwnerAdd;
  }

  public Address getListOwnerAddress() throws SQLException, IOException {
    Address address = table.getConnector().getEmail().getAddress().get(listnameOwnerAdd);
    if (address == null) {
      throw new SQLException("Unable to find EmailAddress: " + listnameOwnerAdd);
    }
    return address;
  }

  public PipeAddress getListRequestPipeAddress() throws SQLException, IOException {
    PipeAddress pipeAddress = table.getConnector().getEmail().getPipeAddress().get(listnameRequestPipeAdd);
    if (pipeAddress == null) {
      throw new SQLException("Unable to find EmailPipeAddress: " + listnameRequestPipeAdd);
    }
    return pipeAddress;
  }

  public String getName() {
    return name;
  }

  public int getOwnerListAddress_id() {
    return ownerListnameAdd;
  }

  public Address getOwnerListAddress() throws SQLException, IOException {
    Address address = table.getConnector().getEmail().getAddress().get(ownerListnameAdd);
    if (address == null) {
      throw new SQLException("Unable to find EmailAddress: " + ownerListnameAdd);
    }
    return address;
  }

  public int getMajordomoServer_domain_id() {
    return majordomoServer;
  }

  public MajordomoServer getMajordomoServer() throws SQLException, IOException {
    MajordomoServer obj = table.getConnector().getEmail().getMajordomoServer().get(majordomoServer);
    if (obj == null) {
      throw new SQLException("Unable to find MajordomoServer: " + majordomoServer);
    }
    return obj;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.MAJORDOMO_LISTS;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    pkey = result.getInt(1);
    majordomoServer = result.getInt(2);
    name = result.getString(3);
    listnamePipeAdd = result.getInt(4);
    listnameListAdd = result.getInt(5);
    ownerListnameAdd = result.getInt(6);
    listnameOwnerAdd = result.getInt(7);
    listnameApprovalAdd = result.getInt(8);
    listnameRequestPipeAdd = result.getInt(9);
  }

  /**
   * Checks the validity of a list name.
   *
   * <p>TODO: Self-validating type</p>
   */
  public static boolean isValidListName(String name) {
    int len = name.length();
    if (len < 1 || len > MAX_NAME_LENGTH) {
      return false;
    }
    for (int c = 0; c < len; c++) {
      char ch = name.charAt(c);
      if (c == 0) {
        if ((ch < '0' || ch > '9') && (ch < 'a' || ch > 'z') && (ch < 'A' || ch > 'Z')) {
          return false;
        }
      } else if ((ch < '0' || ch > '9') && (ch < 'a' || ch > 'z') && (ch < 'A' || ch > 'Z') && ch != '.' && ch != '-' && ch != '_') {
        return false;
      }
    }
    return true;
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    pkey = in.readCompressedInt();
    majordomoServer = in.readCompressedInt();
    name = in.readUTF();
    listnamePipeAdd = in.readCompressedInt();
    listnameListAdd = in.readCompressedInt();
    ownerListnameAdd = in.readCompressedInt();
    listnameOwnerAdd = in.readCompressedInt();
    listnameApprovalAdd = in.readCompressedInt();
    listnameRequestPipeAdd = in.readCompressedInt();
  }

  public void setInfoFile(String file) throws IOException, SQLException {
    table.getConnector().requestUpdate(true, AoservProtocol.CommandId.SET_MAJORDOMO_INFO_FILE, pkey, file);
  }

  public void setIntroFile(String file) throws IOException, SQLException {
    table.getConnector().requestUpdate(true, AoservProtocol.CommandId.SET_MAJORDOMO_INTRO_FILE, pkey, file);
  }

  @Override
  public String toStringImpl() throws SQLException, IOException {
    return name + '@' + getMajordomoServer().getDomain().getDomain().toString();
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeCompressedInt(majordomoServer);
    out.writeUTF(name);
    out.writeCompressedInt(listnamePipeAdd);
    out.writeCompressedInt(listnameListAdd);
    out.writeCompressedInt(ownerListnameAdd);
    out.writeCompressedInt(listnameOwnerAdd);
    out.writeCompressedInt(listnameApprovalAdd);
    out.writeCompressedInt(listnameRequestPipeAdd);
  }
}
