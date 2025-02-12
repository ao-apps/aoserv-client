/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2017, 2018, 2021, 2022, 2025  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.mysql;

import com.aoindustries.aoserv.client.CachedObject;

/**
 * An object that is cached and uses {@link User.Name} as its primary key.
 *
 * @author  AO Industries, Inc.
 */
public abstract class CachedObjectUserNameKey<V extends CachedObjectUserNameKey<V>> extends CachedObject<User.Name, V> {

  protected User.Name pkey;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  protected CachedObjectUserNameKey() {
    // Do nothing
  }

  @Override
  public boolean equals(Object obj) {
    return
        obj != null
            && obj.getClass() == getClass()
            && ((CachedObjectUserNameKey<?>) obj).pkey.equals(pkey);
  }

  @Override
  public User.Name getKey() {
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
