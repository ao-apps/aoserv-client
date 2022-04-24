/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2012, 2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.net.reputation;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.math.LongLong;
import com.aoapps.lang.math.SafeMath;
import com.aoapps.net.InetAddress;
import com.aoindustries.aoserv.client.CachedObjectLongKey;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * One host tracked by an <code>IpReputationSet</code>.
 *
 * @author  AO Industries, Inc.
 */
public final class Host extends CachedObjectLongKey<Host> {

  static final int
      COLUMN_PKEY = 0,
      COLUMN_SET = 1
  ;
  static final String
      COLUMN_SET_name  = "set",
      COLUMN_HOST_name = "host"
  ;

  private int set;
  private int host;
  private short goodReputation;
  private short badReputation;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated // Java 9: (forRemoval = true)
  public Host() {
    // Do nothing
  }

  @Override
  public Table.TableID getTableID() {
    return Table.TableID.IP_REPUTATION_SET_HOSTS;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    int pos = 1;
    pkey           = result.getLong(pos++);
    set            = result.getInt(pos++);
    host           = result.getInt(pos++);
    goodReputation = result.getShort(pos++);
    badReputation  = result.getShort(pos++);
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeLong(pkey);
    out.writeCompressedInt(set);
    out.writeInt(host);
    out.writeShort(goodReputation);
    out.writeShort(badReputation);
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    pkey           = in.readLong();
    set            = in.readCompressedInt();
    host           = in.readInt();
    goodReputation = in.readShort();
    badReputation  = in.readShort();
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_PKEY     : return pkey;
      case COLUMN_SET      : return set;
      case 2               : return getHostAddress();
      case 3               : return goodReputation;
      case 4               : return badReputation;
      default: throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public Set getSet() throws SQLException, IOException {
    Set obj = table.getConnector().getNet().getReputation().getSet().get(set);
    if (obj == null) {
      throw new SQLException("Unable to find IpReputationSet: " + set);
    }
    return obj;
  }

  /**
   * Gets the 32-bit host address.
   */
  public int getHost() {
    return host;
  }

  /**
   * Gets the IPv4 host address.
   */
  public InetAddress getHostAddress() {
    return InetAddress.valueOf(
        LongLong.valueOf(
            0,
            host & 0x00000000ffffffffL
        )
    );
  }

  /**
   * Gets the current good reputation for this host.
   */
  public short getGoodReputation() {
    return goodReputation;
  }

  /**
   * Gets the current bad reputation for this host.
   */
  public short getBadReputation() {
    return badReputation;
  }

  /**
   * The effective reputation is the good minus the bad.
   */
  public short getReputation() {
    return SafeMath.castShort(goodReputation - badReputation);
  }
}
