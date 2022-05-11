/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2012, 2016, 2017, 2018, 2021, 2022  AO Industries, Inc.
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

import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  CachedObjectLongKey
 *
 * @author  AO Industries, Inc.
 */
public abstract class CachedTableLongKey<V extends CachedObjectLongKey<V>> extends CachedTable<Long, V> {

  protected CachedTableLongKey(AoservConnector connector, Class<V> clazz) {
    super(connector, clazz);
  }

  /**
   * Gets the object with the provided key.  The key must be either a Long or a String.
   * If a String, will be parsed to a long.
   *
   * @exception IllegalArgumentException if pkey is neither a Long nor a String.
   * @exception NumberFormatException if String cannot be parsed to a Long
   *
   * @deprecated  Always try to lookup by specific keys; the compiler will help you more when types change.
   */
  @Deprecated
  @Override
  public V get(Object pkey) throws IOException, SQLException, IllegalArgumentException, NumberFormatException {
    if (pkey == null) {
      return null;
    } else if (pkey instanceof Long) {
      return get(((Long) pkey).longValue());
    } else if (pkey instanceof String) {
      return get(Long.parseLong((String) pkey));
    } else {
      throw new IllegalArgumentException("pkey is neither a Long nor a String: " + pkey);
    }
  }

  /**
   * @see  #get(java.lang.Object)
   */
  public abstract V get(long pkey) throws IOException, SQLException;
}
