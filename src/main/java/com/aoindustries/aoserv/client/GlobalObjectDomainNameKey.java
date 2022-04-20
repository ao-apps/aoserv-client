/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2013, 2016, 2017, 2018, 2021, 2022  AO Industries, Inc.
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

package com.aoindustries.aoserv.client;

import com.aoapps.net.DomainName;

/**
 * An object that is cached and uses a DomainName as its primary key,
 *
 * @author  AO Industries, Inc.
 */
public abstract class GlobalObjectDomainNameKey<T extends GlobalObjectDomainNameKey<T>> extends GlobalObject<DomainName, T> {

  protected DomainName pkey;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated/* Java 9: (forRemoval = true) */
  protected GlobalObjectDomainNameKey() {
    // Do nothing
  }

  @Override
  public boolean equals(Object obj) {
    return
      obj != null
      && obj.getClass() == getClass()
      && ((GlobalObjectDomainNameKey)obj).pkey.equals(pkey)
    ;
  }

  @Override
  public DomainName getKey() {
    return pkey;
  }

  @Override
  public int hashCode() {
    return pkey.hashCode();
  }

  @Override
  public String toStringImpl() {
    return pkey.toString();
  }
}
