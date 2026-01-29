/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2025  AO Industries, Inc.
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
import com.aoapps.net.DomainName;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.CannotRemoveReason;
import com.aoindustries.aoserv.client.Removable;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.dns.Record;
import com.aoindustries.aoserv.client.dns.RecordType;
import com.aoindustries.aoserv.client.linux.GroupServer;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.linux.UserServer;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A <code>EmailDomain</code> is one hostname/domain of email
 * addresses hosted on a <code>Server</code>.  Multiple, unique
 * email addresses may be hosted within the <code>EmailDomain</code>.
 * In order for mail to be routed to the <code>Server</code>, a
 * <code>DNSRecord</code> entry of type <code>MX</code> must
 * point to the <code>Server</code>.
 *
 * @see  Address
 * @see  Record
 * @see  RecordType#MX
 * @see  Server
 *
 * @author  AO Industries, Inc.
 */
public final class Domain extends CachedObjectIntegerKey<Domain> implements Removable {

  static final int COLUMN_PKEY = 0;
  static final int COLUMN_AO_SERVER = 2;
  static final int COLUMN_PACKAGE = 3;
  static final String COLUMN_AO_SERVER_name = "ao_server";
  static final String COLUMN_DOMAIN_name = "domain";

  private DomainName domain;
  private int aoServer;
  private Account.Name packageName;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  Domain#init(java.sql.ResultSet)
   * @see  Domain#read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public Domain() {
    // Do nothing
  }

  public int addEmailAddress(String address) throws SQLException, IOException {
    return table.getConnector().getEmail().getAddress().addEmailAddress(address, this);
  }

  public void addMajordomoServer(
      UserServer linuxServerAccount,
      GroupServer linuxServerGroup,
      MajordomoVersion majordomoVersion
  ) throws IOException, SQLException {
    table.getConnector().getEmail().getMajordomoServer().addMajordomoServer(
        this,
        linuxServerAccount,
        linuxServerGroup,
        majordomoVersion
    );
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_PKEY:
        return pkey;
      case 1:
        return domain;
      case COLUMN_AO_SERVER:
        return aoServer;
      case COLUMN_PACKAGE:
        return packageName;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public DomainName getDomain() {
    return domain;
  }

  public Address getEmailAddress(String address) throws IOException, SQLException {
    return table.getConnector().getEmail().getAddress().getEmailAddress(address, this);
  }

  public List<Address> getEmailAddresses() throws IOException, SQLException {
    return table.getConnector().getEmail().getAddress().getEmailAddresses(this);
  }

  public MajordomoServer getMajordomoServer() throws IOException, SQLException {
    return table.getConnector().getEmail().getMajordomoServer().get(pkey);
  }

  public Package getPackage() throws SQLException, IOException {
    Package packageObject = table.getConnector().getBilling().getPackage().get(packageName);
    if (packageObject == null) {
      throw new SQLException("Unable to find Package: " + packageName);
    }
    return packageObject;
  }

  public int getLinuxServer_host_id() {
    return aoServer;
  }

  public Server getLinuxServer() throws SQLException, IOException {
    Server ao = table.getConnector().getLinux().getServer().get(aoServer);
    if (ao == null) {
      throw new SQLException("Unable to find linux.Server: " + aoServer);
    }
    return ao;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.EMAIL_DOMAINS;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    try {
      pkey = result.getInt(1);
      domain = DomainName.valueOf(result.getString(2));
      aoServer = result.getInt(3);
      packageName = Account.Name.valueOf(result.getString(4));
    } catch (ValidationException e) {
      throw new SQLException(e);
    }
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    try {
      pkey = in.readCompressedInt();
      domain = DomainName.valueOf(in.readUTF());
      aoServer = in.readCompressedInt();
      packageName = Account.Name.valueOf(in.readUTF()).intern();
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  @Override
  public List<CannotRemoveReason<?>> getCannotRemoveReasons() throws SQLException, IOException {
    List<CannotRemoveReason<?>> reasons = new ArrayList<>();

    MajordomoServer ms = getMajordomoServer();
    if (ms != null) {
      Domain ed = ms.getDomain();
      reasons.add(new CannotRemoveReason<>("Used by Majordomo server " + ed.getDomain() + " on " + ed.getLinuxServer().getHostname(), ms));
    }

    for (Address ea : getEmailAddresses()) {
      reasons.addAll(ea.getCannotRemoveReasons());
    }

    return reasons;
  }

  @Override
  public void remove() throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(
        true,
        AoservProtocol.CommandId.REMOVE,
        Table.TableId.EMAIL_DOMAINS,
        pkey
    );
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeUTF(domain.toString());
    out.writeCompressedInt(aoServer);
    out.writeUTF(packageName.toString());
  }
}
