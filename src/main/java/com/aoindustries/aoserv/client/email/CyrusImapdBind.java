/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2017, 2018, 2019, 2021, 2022, 2024  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.net.Bind;
import com.aoindustries.aoserv.client.pki.Certificate;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Each <code>CyrusImapdServer</code> may listen for network connections on
 * multiple <code>NetBind</code>s.  A <code>CyrusImapdBind</code> ties
 * <code>CyrusImapdServer</code>s to <code>NetBinds</code>.
 *
 * @see  CyrusImapdServer
 * @see  Bind
 *
 * @author  AO Industries, Inc.
 */
public final class CyrusImapdBind extends CachedObjectIntegerKey<CyrusImapdBind> {

  static final int COLUMN_NET_BIND = 0;
  static final int COLUMN_CYRUS_IMAPD_SERVER = 1;
  static final int COLUMN_SSL_CERTIFICATE = 3;
  static final String COLUMN_NET_BIND_name = "net_bind";

  private int cyrusImapdServer;
  private DomainName servername;
  private int certificate;
  private Boolean allowPlaintextAuth;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated // Java 9: (forRemoval = true)
  public CyrusImapdBind() {
    // Do nothing
  }

  @Override
  public String toStringImpl() throws SQLException, IOException {
    CyrusImapdServer server = getCyrusImapdServer();
    Bind bind = getNetBind();
    return server.toStringImpl() + '|' + bind.toStringImpl();
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_NET_BIND:
        return pkey;
      case COLUMN_CYRUS_IMAPD_SERVER:
        return cyrusImapdServer;
      case 2:
        return servername;
      case COLUMN_SSL_CERTIFICATE:
        return certificate == -1 ? null : certificate;
      case 4:
        return allowPlaintextAuth;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.CYRUS_IMAPD_BINDS;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    try {
      int pos = 1;
      pkey = result.getInt(pos++);
      cyrusImapdServer = result.getInt(pos++);
      servername = DomainName.valueOf(result.getString(pos++));
      certificate = result.getInt(pos++);
      if (result.wasNull()) {
        certificate = -1;
      }
      allowPlaintextAuth = result.getBoolean(pos++);
      if (result.wasNull()) {
        allowPlaintextAuth = null;
      }
    } catch (ValidationException e) {
      throw new SQLException(e);
    }
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    try {
      pkey = in.readCompressedInt();
      cyrusImapdServer = in.readCompressedInt();
      servername = DomainName.valueOf(in.readNullUTF());
      certificate = in.readCompressedInt();
      allowPlaintextAuth = in.readNullBoolean();
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeCompressedInt(cyrusImapdServer);
    out.writeNullUTF(Objects.toString(servername, null));
    out.writeCompressedInt(certificate);
    out.writeNullBoolean(allowPlaintextAuth);
  }

  public Bind getNetBind() throws SQLException, IOException {
    Bind obj = table.getConnector().getNet().getBind().get(pkey);
    if (obj == null) {
      throw new SQLException("Unable to find NetBind: " + pkey);
    }
    return obj;
  }

  public CyrusImapdServer getCyrusImapdServer() throws SQLException, IOException {
    CyrusImapdServer obj = table.getConnector().getEmail().getCyrusImapdServer().get(cyrusImapdServer);
    if (obj == null) {
      throw new SQLException("Unable to find CyrusImapd: " + cyrusImapdServer);
    }
    return obj;
  }

  /**
   * The fully qualified hostname for <code>servername</code>.
   *
   * <p>When {@code null}, defaults to {@link CyrusImapdServer#getServername()}.</p>
   */
  public DomainName getServername() {
    return servername;
  }

  /**
   * Gets the SSL certificate for this server.
   *
   * @return  the SSL certificate or {@code null} when filtered or defaulting to {@link CyrusImapdServer#getCertificate()}
   */
  public Certificate getCertificate() throws SQLException, IOException {
    if (certificate == -1) {
      return null;
    }
    // May be filtered
    return table.getConnector().getPki().getCertificate().get(certificate);
  }

  /**
   * Allows plaintext authentication (PLAIN/LOGIN) on non-TLS links.
   *
   * <p>When {@code null}, defaults to {@link CyrusImapdServer#getAllowPlaintextAuth()}.</p>
   */
  public Boolean getAllowPlaintextAuth() {
    return allowPlaintextAuth;
  }
}
