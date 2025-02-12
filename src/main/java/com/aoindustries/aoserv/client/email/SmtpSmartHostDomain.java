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
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.net.DomainName;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Provides non-default per-domain smart host limits.
 *
 * @author  AO Industries, Inc.
 */
public final class SmtpSmartHostDomain extends CachedObjectIntegerKey<SmtpSmartHostDomain> {

  static final int COLUMN_PKEY = 0;
  static final int COLUMN_SMART_HOST = 1;
  static final String COLUMN_SMART_HOST_name = "smart_host";
  static final String COLUMN_DOMAIN_name = "domain";

  private int smartHost;
  private DomainName domain;
  private int domainOutBurst;
  private float domainOutRate;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public SmtpSmartHostDomain() {
    // Do nothing
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_PKEY:
        return pkey;
      case COLUMN_SMART_HOST:
        return smartHost;
      case 2:
        return domain;
      case 3:
        return domainOutBurst == -1 ? null : domainOutBurst;
      case 4:
        return Float.isNaN(domainOutRate) ? null : domainOutRate;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public SmtpSmartHost getEmailSmtpSmartHost() throws SQLException, IOException {
    SmtpSmartHost obj = table.getConnector().getEmail().getSmtpSmartHost().get(smartHost);
    if (obj == null) {
      throw new SQLException("Unable to find EmailSmtpSmartHost: " + smartHost);
    }
    return obj;
  }

  public DomainName getDomain() {
    return domain;
  }

  /**
   * Gets the domain-specific outbound burst limit for emails, the number of emails that may be sent before limiting occurs.
   * A value of <code>-1</code> indicates unlimited.
   */
  public int getDomainOutBurst() {
    return domainOutBurst;
  }

  /**
   * Gets the domain-specific outbound sustained email rate in emails/second.
   * A value of <code>Float.NaN</code> indicates unlimited.
   */
  public float getDomainOutRate() {
    return domainOutRate;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.EMAIL_SMTP_SMART_HOST_DOMAINS;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    try {
      int pos = 1;
      pkey = result.getInt(pos++);
      smartHost = result.getInt(pos++);
      domain = DomainName.valueOf(result.getString(pos++));
      domainOutBurst = result.getInt(pos++);
      if (result.wasNull()) {
        domainOutBurst = -1;
      }
      domainOutRate = result.getFloat(pos++);
      if (result.wasNull()) {
        domainOutRate = Float.NaN;
      }
    } catch (ValidationException e) {
      throw new SQLException(e);
    }
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    try {
      pkey = in.readCompressedInt();
      smartHost = in.readCompressedInt();
      domain = DomainName.valueOf(in.readUTF());
      domainOutBurst = in.readCompressedInt();
      domainOutRate = in.readFloat();
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeCompressedInt(smartHost);
    out.writeCompressedInt(domainOutBurst);
    out.writeFloat(domainOutRate);
  }
}
