/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018, 2019, 2021, 2022, 2024  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.dns;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.net.DomainName;
import com.aoindustries.aoserv.client.GlobalObjectDomainNameKey;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A <code>DNSTLD</code> is a name server top level domain.  A top level domain
 * is a domain that is one level above that which is controlled by AO Industries'
 * name servers.  Some common examples include <code>com</code>, <code>net</code>,
 * <code>org</code>, <code>co.uk</code>, <code>aq</code> (OK - not so common), and
 * <code>med.pro</code>.  The domains added to the name servers must be in the
 * format <code>subdomain</code>.<code>dns_tld</code>, where <code>subdomain</code>
 * is a word without dots (<code>.</code>), and <code>dns_tld</code> is one of
 * the top level domains in the database.  If a top level domain does not exist
 * that properly should, please contact AO Industries to have it added.
 *
 * <p>Also, this is a list of effective top-level domains, for the purposes of
 * domain allocation.  This means it includes things like <code>com.au</code>,
 * whereas the {@link com.aoapps.tlds.TopLevelDomain} only includes <code>au</code>.</p>
 *
 * @see  Zone
 *
 * @author  AO Industries, Inc.
 */
// TODO: Rename something different, to distinguish from top-level domains proper.
//       Perhaps "RegistrationDomain"/"RegistrarDomain"/"RegistrableDomain"?
//       Or "Tier"/"UsableDomain"?
// Evaluated: https://github.com/whois-server-list/public-suffix-list
//            Seems dead, also not self-updating
//
// https://wiki.mozilla.org/Public_Suffix_List
// https://publicsuffix.org/
// https://publicsuffix.org/list/
// https://publicsuffix.org/list/public_suffix_list.dat

public final class TopLevelDomain extends GlobalObjectDomainNameKey<TopLevelDomain> {

  static final int COLUMN_DOMAIN = 0;
  static final String COLUMN_DOMAIN_name = "domain";

  private String description;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated // Java 9: (forRemoval = true)
  public TopLevelDomain() {
    // Do nothing
  }

  @Override
  protected Object getColumnImpl(int i) {
    if (i == COLUMN_DOMAIN) {
      return pkey;
    }
    if (i == 1) {
      return description;
    }
    throw new IllegalArgumentException("Invalid index: " + i);
  }

  public String getDescription() {
    return description;
  }

  public DomainName getDomain() {
    return pkey;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.DNS_TLDS;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    try {
      pkey = DomainName.valueOf(result.getString(1));
      description = result.getString(2);
    } catch (ValidationException e) {
      throw new SQLException(e);
    }
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    try {
      pkey = DomainName.valueOf(in.readUTF()).intern();
      description = in.readUTF();
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeUTF(pkey.toString());
    out.writeUTF(description);
  }
}
