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

package com.aoindustries.aoserv.client.linux;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.sql.SQLStreamables;
import com.aoapps.sql.UnmodifiableTimestamp;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.CannotRemoveReason;
import com.aoindustries.aoserv.client.Removable;
import com.aoindustries.aoserv.client.email.Domain;
import com.aoindustries.aoserv.client.email.MajordomoServer;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.scm.CvsRepository;
import com.aoindustries.aoserv.client.web.HttpdServer;
import com.aoindustries.aoserv.client.web.Site;
import com.aoindustries.aoserv.client.web.tomcat.SharedTomcat;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A <code>LinuxServerGroup</code> adds a <code>LinuxGroup</code>
 * to a {@link Server}, so that <code>LinuxServerAccount</code> with
 * access to the group may use the group on the server.
 *
 * @see  Group
 * @see  UserServer
 * @see  Server
 *
 * @author  AO Industries, Inc.
 */
public final class GroupServer extends CachedObjectIntegerKey<GroupServer> implements Removable {

  static final int
      COLUMN_PKEY = 0,
      COLUMN_NAME = 1,
      COLUMN_AO_SERVER = 2
  ;
  static final String COLUMN_NAME_name = "name";
  static final String COLUMN_AO_SERVER_name = "ao_server";

  private Group.Name name;
  private int ao_server;
  private LinuxId gid;
  private UnmodifiableTimestamp created;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated // Java 9: (forRemoval = true)
  public GroupServer() {
    // Do nothing
  }

  public List<UserServer> getAlternateLinuxServerAccounts() throws SQLException, IOException {
    return table.getConnector().getLinux().getUserServer().getAlternateLinuxServerAccounts(this);
  }

  @Override
  @SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_PKEY: return pkey;
      case COLUMN_NAME: return name;
      case COLUMN_AO_SERVER: return ao_server;
      case 3: return gid;
      case 4: return created;
      default: throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public LinuxId getGid() {
    return gid;
  }

  @SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
  public UnmodifiableTimestamp getCreated() {
    return created;
  }

  public Group.Name getLinuxGroup_name() {
    return name;
  }

  public Group getLinuxGroup() throws SQLException, IOException {
    Group group = table.getConnector().getLinux().getGroup().get(name);
    if (group == null) {
      throw new SQLException("Unable to find LinuxGroup: " + name);
    }
    return group;
  }

  public int getServer_host_id() {
    return ao_server;
  }

  public Server getServer() throws SQLException, IOException {
    Server ao = table.getConnector().getLinux().getServer().get(ao_server);
    if (ao == null) {
      throw new SQLException("Unable to find linux.Server: " + ao_server);
    }
    return ao;
  }

  @Override
  public Table.TableID getTableID() {
    return Table.TableID.LINUX_SERVER_GROUPS;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    try {
      pkey = result.getInt(1);
      name = Group.Name.valueOf(result.getString(2));
      ao_server = result.getInt(3);
      gid = LinuxId.valueOf(result.getInt(4));
      created = UnmodifiableTimestamp.valueOf(result.getTimestamp(5));
    } catch (ValidationException e) {
      throw new SQLException(e);
    }
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    try {
      pkey = in.readCompressedInt();
      name = Group.Name.valueOf(in.readUTF()).intern();
      ao_server = in.readCompressedInt();
      gid = LinuxId.valueOf(in.readCompressedInt());
      created = SQLStreamables.readUnmodifiableTimestamp(in);
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  @Override
  public List<CannotRemoveReason<?>> getCannotRemoveReasons() throws SQLException, IOException {
    List<CannotRemoveReason<?>> reasons = new ArrayList<>();

    Server ao = getServer();

    for (CvsRepository cr : ao.getCvsRepositories()) {
      if (cr.getLinuxServerGroup_pkey() == pkey) {
        reasons.add(new CannotRemoveReason<>("Used by CVS repository " + cr.getPath() + " on " + cr.getLinuxServerGroup().getServer().getHostname(), cr));
      }
    }

    for (com.aoindustries.aoserv.client.email.List el : table.getConnector().getEmail().getList().getRows()) {
      if (el.getLinuxServerGroup_pkey() == pkey) {
        reasons.add(new CannotRemoveReason<>("Used by email list " + el.getPath() + " on " + el.getLinuxServerGroup().getServer().getHostname(), el));
      }
    }

    for (HttpdServer hs : ao.getHttpdServers()) {
      if (hs.getLinuxServerGroup_pkey() == pkey) {
        String hs_name = hs.getName();
        reasons.add(
            new CannotRemoveReason<>(
                hs_name == null
                    ? "Used by Apache HTTP Server on " + hs.getLinuxServer().getHostname()
                    : "Used by Apache HTTP Server (" + hs_name + ") on " + hs.getLinuxServer().getHostname(),
                hs
            )
        );
      }
    }

    for (SharedTomcat hst : ao.getHttpdSharedTomcats()) {
      if (hst.getLinuxServerGroup_pkey() == pkey) {
        reasons.add(new CannotRemoveReason<>("Used by Multi-Site Tomcat JVM " + hst.getInstallDirectory() + " on " + hst.getLinuxServer().getHostname(), hst));
      }
    }

    // httpd_sites
    for (Site site : ao.getHttpdSites()) {
      if (site.getLinuxGroup_name().equals(name)) {
        reasons.add(new CannotRemoveReason<>("Used by website " + site.getInstallDirectory() + " on " + site.getLinuxServer().getHostname(), site));
      }
    }

    for (MajordomoServer ms : ao.getMajordomoServers()) {
      if (ms.getLinuxServerGroup_pkey() == pkey) {
        Domain ed = ms.getDomain();
        reasons.add(new CannotRemoveReason<>("Used by Majordomo server " + ed.getDomain() + " on " + ed.getLinuxServer().getHostname(), ms));
      }
    }

    /*for (PrivateFTPServer pfs : ao.getPrivateFTPServers()) {
      if (pfs.pub_linux_server_group == pkey) {
        reasons.add(new CannotRemoveReason<>("Used by private FTP server "+pfs.getRoot()+" on "+pfs.getLinuxServerGroup().getServer().getHostname(), pfs));
      }
    }*/

    return reasons;
  }

  @Override
  public void remove() throws IOException, SQLException {
    table.getConnector().requestUpdateIL(
        true,
        AoservProtocol.CommandID.REMOVE,
        Table.TableID.LINUX_SERVER_GROUPS,
        pkey
    );
  }

  @Override
  public String toStringImpl() {
    return name.toString();
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeUTF(name.toString());
    out.writeCompressedInt(ao_server);
    out.writeCompressedInt(gid.getId());
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
      out.writeLong(created.getTime());
    } else {
      SQLStreamables.writeTimestamp(created, out);
    }
  }
}
