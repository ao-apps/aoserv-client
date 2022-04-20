/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2009, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * All of the possible Linux login shells are provided as
 * <code>Shell</code>s.
 *
 * @see  User
 * @see  UserType
 *
 * @author  AO Industries, Inc.
 */
public final class Shell extends GlobalObjectPosixPathKey<Shell> {

  static final int COLUMN_PATH=0;
  static final String COLUMN_PATH_name = "path";

  public static final PosixPath
    BASH,
    FALSE,
    KSH,
    SH,
    SYNC,
    TCSH,
    HALT,
    NOLOGIN,
    SHUTDOWN,
    FTPONLY,
    FTPPASSWD,
    GIT_SHELL,
    PASSWD
  ;
  static {
    try {
      BASH = PosixPath.valueOf("/bin/bash").intern();
      FALSE = PosixPath.valueOf("/bin/false").intern();
      KSH = PosixPath.valueOf("/bin/ksh").intern();
      SH = PosixPath.valueOf("/bin/sh").intern();
      SYNC = PosixPath.valueOf("/bin/sync").intern();
      TCSH = PosixPath.valueOf("/bin/tcsh").intern();
      HALT = PosixPath.valueOf("/sbin/halt").intern();
      NOLOGIN = PosixPath.valueOf("/sbin/nologin").intern();
      SHUTDOWN = PosixPath.valueOf("/sbin/shutdown").intern();
      FTPONLY = PosixPath.valueOf("/usr/bin/ftponly").intern();
      FTPPASSWD = PosixPath.valueOf("/usr/bin/ftppasswd").intern();
      GIT_SHELL = PosixPath.valueOf("/usr/bin/git-shell").intern();
      PASSWD = PosixPath.valueOf("/usr/bin/passwd").intern();
    } catch (ValidationException e) {
      throw new AssertionError("These hard-coded values are valid", e);
    }
  }

  private boolean is_login;
  private boolean is_system;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated/* Java 9: (forRemoval = true) */
  public Shell() {
    // Do nothing
  }

  @Override
  protected Object getColumnImpl(int i) {
    if (i == COLUMN_PATH) {
      return pkey;
    }
    if (i == 1) {
      return is_login;
    }
    if (i == 2) {
      return is_system;
    }
    throw new IllegalArgumentException("Invalid index: " + i);
  }

  public PosixPath getPath() {
    return pkey;
  }

  @Override
  public Table.TableID getTableID() {
    return Table.TableID.SHELLS;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    try {
      pkey = PosixPath.valueOf(result.getString(1));
      is_login = result.getBoolean(2);
      is_system = result.getBoolean(3);
    } catch (ValidationException e) {
      throw new SQLException(e);
    }
  }

  public boolean isLogin() {
    return is_login;
  }

  public boolean isSystem() {
    return is_system;
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    try {
      pkey = PosixPath.valueOf(in.readUTF()).intern();
      is_login=in.readBoolean();
      is_system=in.readBoolean();
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeUTF(pkey.toString());
    out.writeBoolean(is_login);
    out.writeBoolean(is_system);
  }
}
