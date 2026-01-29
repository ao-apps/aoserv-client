/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2009-2013, 2016, 2017, 2018, 2019, 2021, 2022, 2025  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.net.Bind;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Provides SMTP relay services for one or more non-managed servers.
 *
 * @author  AO Industries, Inc.
 */
public final class SmtpSmartHost extends CachedObjectIntegerKey<SmtpSmartHost> {

  static final int COLUMN_NET_BIND = 0;
  static final String COLUMN_NET_BIND_name = "net_bind";

  private int totalOutBurst;
  private float totalOutRate;
  private int defaultDomainOutBurst;
  private float defaultDomainOutRate;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  SmtpSmartHost#init(java.sql.ResultSet)
   * @see  SmtpSmartHost#read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public SmtpSmartHost() {
    // Do nothing
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_NET_BIND:
        return pkey;
      case 1:
        return totalOutBurst == -1 ? null : totalOutBurst;
      case 2:
        return Float.isNaN(totalOutRate) ? null : totalOutRate;
      case 3:
        return defaultDomainOutBurst == -1 ? null : defaultDomainOutBurst;
      case 4:
        return Float.isNaN(defaultDomainOutRate) ? null : defaultDomainOutRate;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public Bind getNetBind() throws IOException, SQLException {
    Bind obj = table.getConnector().getNet().getBind().get(pkey);
    if (obj == null) {
      throw new SQLException("Unable to find NetBind: " + pkey);
    }
    return obj;
  }

  /**
   * Gets the total smart host outbound burst limit for emails, the number of emails that may be sent before limiting occurs.
   * A value of <code>-1</code> indicates unlimited.
   */
  public int getTotalEmailOutBurst() {
    return totalOutBurst;
  }

  /**
   * Gets the total smart host outbound sustained email rate in emails/second.
   * A value of <code>Float.NaN</code> indicates unlimited.
   */
  public float getTotalEmailOutRate() {
    return totalOutRate;
  }

  /**
   * Gets the default per-domain outbound burst limit for emails, the number of emails that may be sent before limiting occurs.
   * A value of <code>-1</code> indicates unlimited.
   */
  public int getDefaultDomainOutBurst() {
    return defaultDomainOutBurst;
  }

  /**
   * Gets the default per-domain outbound sustained email rate in emails/second.
   * A value of <code>Float.NaN</code> indicates unlimited.
   */
  public float getDefaultDomainOutRate() {
    return defaultDomainOutRate;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.EMAIL_SMTP_SMART_HOSTS;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    int pos = 1;
    pkey = result.getInt(pos++);
    totalOutBurst = result.getInt(pos++);
    if (result.wasNull()) {
      totalOutBurst = -1;
    }
    totalOutRate = result.getFloat(pos++);
    if (result.wasNull()) {
      totalOutRate = Float.NaN;
    }
    defaultDomainOutBurst = result.getInt(pos++);
    if (result.wasNull()) {
      defaultDomainOutBurst = -1;
    }
    defaultDomainOutRate = result.getFloat(pos++);
    if (result.wasNull()) {
      defaultDomainOutRate = Float.NaN;
    }
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    pkey = in.readCompressedInt();
    totalOutBurst = in.readCompressedInt();
    totalOutRate = in.readFloat();
    defaultDomainOutBurst = in.readCompressedInt();
    defaultDomainOutRate = in.readFloat();
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeCompressedInt(totalOutBurst);
    out.writeFloat(totalOutRate);
    out.writeCompressedInt(defaultDomainOutBurst);
    out.writeFloat(defaultDomainOutRate);
  }
}
