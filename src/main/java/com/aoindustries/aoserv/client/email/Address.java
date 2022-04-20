/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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
import com.aoapps.net.Email;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.CannotRemoveReason;
import com.aoindustries.aoserv.client.Removable;
import com.aoindustries.aoserv.client.linux.UserServer;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * An <code>EmailAddress</code> represents a unique email
 * address hosted on an AOServ server.  This email address
 * may only go to one destination,
 *
 *
 * @see  BlackholeAddress
 * @see  Forwarding
 * @see  ListAddress
 * @see  PipeAddress
 * @see  InboxAddress
 *
 * @author  AO Industries, Inc.
 */
public final class Address extends CachedObjectIntegerKey<Address> implements Removable {

  static final int
    COLUMN_PKEY=0,
    COLUMN_DOMAIN=2
  ;
  static final String COLUMN_DOMAIN_name = "domain";
  static final String COLUMN_ADDRESS_name = "address";

  private String address;
  private int domain;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated/* Java 9: (forRemoval = true) */
  public Address() {
    // Do nothing
  }

  public int addEmailForwarding(Email destination) throws IOException, SQLException {
    return table.getConnector().getEmail().getForwarding().addEmailForwarding(this, destination);
  }

  public String getAddress() {
    return address;
  }

  public BlackholeAddress getBlackholeEmailAddress() throws IOException, SQLException {
    return table.getConnector().getEmail().getBlackholeAddress().get(pkey);
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_PKEY: return pkey;
      case 1: return address;
      case COLUMN_DOMAIN: return domain;
      default: throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public Domain getDomain() throws SQLException, IOException {
    Domain domainObject = table.getConnector().getEmail().getDomain().get(domain);
    if (domainObject == null) {
      throw new SQLException("Unable to find EmailDomain: " + domain);
    }
    return domainObject;
  }

  public java.util.List<Forwarding> getEmailForwardings() throws IOException, SQLException {
    return table.getConnector().getEmail().getForwarding().getEmailForwardings(this);
  }

  public java.util.List<Forwarding> getEnabledEmailForwardings() throws SQLException, IOException {
    return table.getConnector().getEmail().getForwarding().getEnabledEmailForwardings(this);
  }

  public Forwarding getEmailForwarding(Email destination) throws IOException, SQLException {
    return table.getConnector().getEmail().getForwarding().getEmailForwarding(this, destination);
  }

  public java.util.List<List> getEmailLists() throws IOException, SQLException {
    return table.getConnector().getEmail().getListAddress().getEmailLists(this);
  }

  public java.util.List<ListAddress> getEmailListAddresses() throws IOException, SQLException {
    return table.getConnector().getEmail().getListAddress().getEmailListAddresses(this);
  }

  public java.util.List<ListAddress> getEnabledEmailListAddresses() throws IOException, SQLException {
    return table.getConnector().getEmail().getListAddress().getEnabledEmailListAddresses(this);
  }

  public ListAddress getEmailListAddress(List list) throws IOException, SQLException {
    return table.getConnector().getEmail().getListAddress().getEmailListAddress(this, list);
  }

  public java.util.List<Pipe> getEmailPipes() throws IOException, SQLException {
    return table.getConnector().getEmail().getPipeAddress().getEmailPipes(this);
  }

  public java.util.List<PipeAddress> getEmailPipeAddresses() throws IOException, SQLException {
    return table.getConnector().getEmail().getPipeAddress().getEmailPipeAddresses(this);
  }

  public java.util.List<PipeAddress> getEnabledEmailPipeAddresses() throws IOException, SQLException {
    return table.getConnector().getEmail().getPipeAddress().getEnabledEmailPipeAddresses(this);
  }

  public PipeAddress getEmailPipeAddress(Pipe pipe) throws IOException, SQLException {
    return table.getConnector().getEmail().getPipeAddress().getEmailPipeAddress(this, pipe);
  }

  public java.util.List<UserServer> getLinuxServerAccounts() throws IOException, SQLException {
    return table.getConnector().getEmail().getInboxAddress().getLinuxServerAccounts(this);
  }

  public java.util.List<InboxAddress> getLinuxAccAddresses() throws IOException, SQLException {
    return table.getConnector().getEmail().getInboxAddress().getLinuxAccAddresses(this);
  }

  public InboxAddress getLinuxAccAddress(UserServer lsa) throws IOException, SQLException {
    return table.getConnector().getEmail().getInboxAddress().getLinuxAccAddress(this, lsa);
  }

  @Override
  public Table.TableID getTableID() {
    return Table.TableID.EMAIL_ADDRESSES;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    pkey = result.getInt(1);
    address = result.getString(2);
    domain = result.getInt(3);
  }

  public boolean isUsed() throws IOException, SQLException {
    // Anything using this address must be removable
    return
      getBlackholeEmailAddress() != null
      || !getEmailForwardings().isEmpty()
      || !getEmailListAddresses().isEmpty()
      || !getEmailPipeAddresses().isEmpty()
      || !getLinuxAccAddresses().isEmpty()
    ;
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    pkey=in.readCompressedInt();
    address=in.readUTF();
    domain=in.readCompressedInt();
  }

  @Override
  public java.util.List<CannotRemoveReason<?>> getCannotRemoveReasons() throws SQLException, IOException {
    java.util.List<CannotRemoveReason<?>> reasons = new ArrayList<>();

    // Everything using this address must be removable
    BlackholeAddress bea=getBlackholeEmailAddress();
    if (bea != null) {
      reasons.addAll(bea.getCannotRemoveReasons());
    }

    for (Forwarding ef : getEmailForwardings()) {
      reasons.addAll(ef.getCannotRemoveReasons());
    }

    for (ListAddress ela : table.getConnector().getEmail().getListAddress().getEmailListAddresses(this)) {
      reasons.addAll(ela.getCannotRemoveReasons());
    }

    for (PipeAddress epa : getEmailPipeAddresses()) {
      reasons.addAll(epa.getCannotRemoveReasons());
    }

    for (InboxAddress laa : getLinuxAccAddresses()) {
      reasons.addAll(laa.getCannotRemoveReasons());
    }

    // Cannot be used as any part of a majordomo list
    for (MajordomoList ml : table.getConnector().getEmail().getMajordomoList().getRows()) {
      if (
        ml.getOwnerListAddress_id() == pkey
        || ml.getListOwnerAddress_id() == pkey
        || ml.getListApprovalAddress_id() == pkey
      ) {
        Domain ed=ml.getMajordomoServer().getDomain();
        reasons.add(new CannotRemoveReason<>("Used by Majordomo list "+ml.getName()+'@'+ed.getDomain()+" on "+ed.getLinuxServer().getHostname(), ml));
      }
    }

    // Cannot be used as any part of a majordomo server
    for (MajordomoServer ms : table.getConnector().getEmail().getMajordomoServer().getRows()) {
      if (
        ms.getOwnerMajordomoAddress_id() == pkey
        || ms.getMajordomoOwnerAddress_id() == pkey
      ) {
        Domain ed=ms.getDomain();
        reasons.add(new CannotRemoveReason<>("Used by Majordomo server "+ed.getDomain()+" on "+ed.getLinuxServer().getHostname(), ms));
      }
    }

    return reasons;
  }

  /**
   * Removes this <code>EmailAddress</code> any any references to it.  For example, if
   * this address is sent to a <code>LinuxAccount</code>, the address is disassociated
   * before it is removed.  For integrity, this entire operation is handled in a single
   * database transaction.
   */
  @Override
  public void remove() throws IOException, SQLException {
    table.getConnector().requestUpdateIL(
      true,
      AoservProtocol.CommandID.REMOVE,
      Table.TableID.EMAIL_ADDRESSES,
      pkey
    );
  }

  @Override
  public String toStringImpl() throws SQLException, IOException {
    return address+'@'+getDomain().getDomain().toString();
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeUTF(address);
    out.writeCompressedInt(domain);
  }
}
