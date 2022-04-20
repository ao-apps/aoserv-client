/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2003-2009, 2016, 2017, 2019, 2021, 2022  AO Industries, Inc.
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

import com.aoapps.hodgepodge.io.FileListObject;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * A <code>FilesystemCachedObject</code> is stored in
 * a temporary file on disk for local-speed performance while using
 * minimal heap space.
 *
 * @author  AO Industries, Inc.
 */
// TODO: Is this worth maintaining?
public abstract class FilesystemCachedObject<K, T extends FilesystemCachedObject<K, T>> extends AOServObject<K, T> implements SingleTableObject<K, T>, FileListObject {

  protected AOServTable<K, T> table;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated/* Java 9: (forRemoval = true) */
  protected FilesystemCachedObject() {
    // Do nothing
  }

  /*
  public FileListObject createInstance() throws IOException {
    T fco=table.getNewObject();
    if (table != null) {
      fco.setTable(table);
    }
    return fco;
  }
   */

  @Override
  public final AOServTable<K, T> getTable() {
    return table;
  }

  @Override
  public final void setTable(AOServTable<K, T> table) {
    if (this.table != null) {
      throw new IllegalStateException("table already set");
    }
    this.table=table;
  }

  @Override
  public abstract void writeRecord(DataOutputStream out) throws IOException;

  @Override
  public abstract void readRecord(DataInputStream in) throws IOException;
}
