/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2013, 2016, 2017, 2018, 2019, 2021, 2022, 2025  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.distribution.management;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.validation.ValidationException;
import com.aoindustries.aoserv.client.FilesystemCachedObject;
import com.aoindustries.aoserv.client.distribution.OperatingSystemVersion;
import com.aoindustries.aoserv.client.linux.Group;
import com.aoindustries.aoserv.client.linux.PosixPath;
import com.aoindustries.aoserv.client.linux.User;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * For AO Industries use only.
 *
 * @author  AO Industries, Inc.
 */
public final class DistroFile extends FilesystemCachedObject<Integer, DistroFile> {

  static final int COLUMN_PKEY = 0;
  public static final int COLUMN_OPERATING_SYSTEM_VERSION = 1;
  public static final int COLUMN_PATH = 2;
  static final String COLUMN_PATH_name = "path";
  static final String COLUMN_OPERATING_SYSTEM_VERSION_name = "operating_system_version";

  // TODO: These fixed sizes being hard-coded is not very nice.  Maybe query
  //       the master for the longest sizes before downloading the records?
  //       Or hack the protocol a bit for this table and begin the transfer with a set of int's giving the lengths.
  static final int MAX_PATH_LENGTH = 194; // select max(length(path)) from distro_files;
  static final int MAX_TYPE_LENGTH = 10;
  static final int MAX_SYMLINK_TARGET_LENGTH = 96; // select max(length(symlink_target)) from distro_files;
  static final int MAX_LINUX_ACCOUNT_LENGTH = 15; // select max(length(linux_account)) from distro_files;
  static final int MAX_LINUX_GROUP_LENGTH = 15; // select max(length(linux_group)) from distro_files;

  /**
   * The size may not be available for certain file types.
   */
  public static final long NULL_SIZE = -1;

  private int pkey;
  private int operatingSystemVersion;
  private PosixPath path;
  private boolean optional;
  private String type;
  private long mode;
  private User.Name linuxAccount;
  private Group.Name linuxGroup;
  private long size;
  private boolean hasFileSha;
  private long fileSha0;
  private long fileSha1;
  private long fileSha2;
  private long fileSha3;
  private String symlinkTarget;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  DistroFile#init(java.sql.ResultSet)
   * @see  DistroFile#read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public DistroFile() {
    // Do nothing
  }

  @Override
  public boolean equals(Object obj) {
    return
        (obj instanceof DistroFile)
            && ((DistroFile) obj).pkey == pkey;
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_PKEY:
        return pkey;
      case COLUMN_OPERATING_SYSTEM_VERSION:
        return operatingSystemVersion;
      case COLUMN_PATH:
        return path;
      case 3:
        return optional;
      case 4:
        return type;
      case 5:
        return mode;
      case 6:
        return linuxAccount;
      case 7:
        return linuxGroup;
      case 8:
        return size == NULL_SIZE ? null : size;
      case 9:
        return hasFileSha ? fileSha0 : null;
      case 10:
        return hasFileSha ? fileSha1 : null;
      case 11:
        return hasFileSha ? fileSha2 : null;
      case 12:
        return hasFileSha ? fileSha3 : null;
      case 13:
        return symlinkTarget;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public int getPkey() {
    return pkey;
  }

  public OperatingSystemVersion getOperatingSystemVersion() throws SQLException, IOException {
    OperatingSystemVersion osv = table.getConnector().getDistribution().getOperatingSystemVersion().get(operatingSystemVersion);
    if (osv == null) {
      throw new SQLException("Unable to find OperatingSystemVersion: " + operatingSystemVersion);
    }
    return osv;
  }

  public PosixPath getPath() {
    return path;
  }

  public boolean isOptional() {
    return optional;
  }

  public DistroFileType getType() throws SQLException, IOException {
    DistroFileType fileType = table.getConnector().getDistribution_management().getDistroFileType().get(type);
    if (fileType == null) {
      throw new SQLException("Unable to find DistroFileType: " + type);
    }
    return fileType;
  }

  public long getMode() {
    return mode;
  }

  public User getLinuxAccount() throws SQLException, IOException {
    if (table == null) {
      throw new NullPointerException("table is null");
    }
    if (table.getConnector() == null) {
      throw new NullPointerException("table.getConnector() is null");
    }
    User linuxAccount = table.getConnector().getLinux().getUser().get(this.linuxAccount);
    if (linuxAccount == null) {
      throw new SQLException("Unable to find LinuxAccount: " + this.linuxAccount);
    }
    return linuxAccount;
  }

  public Group getLinuxGroup() throws SQLException, IOException {
    Group linuxGroup = table.getConnector().getLinux().getGroup().get(this.linuxGroup);
    if (linuxGroup == null) {
      throw new SQLException("Unable to find LinuxGroup: " + this.linuxGroup);
    }
    return linuxGroup;
  }

  public long getSize() {
    return size;
  }

  public boolean hasFileSha256() {
    return hasFileSha;
  }

  public long getFileSha256_0() {
    return fileSha0;
  }

  public long getFileSha256_1() {
    return fileSha1;
  }

  public long getFileSha256_2() {
    return fileSha2;
  }

  public long getFileSha256_3() {
    return fileSha3;
  }

  public String getSymlinkTarget() {
    return symlinkTarget;
  }

  @Override
  public Integer getKey() {
    return pkey;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.DISTRO_FILES;
  }

  @Override
  public int hashCode() {
    return pkey;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    try {
      int pos = 1;
      pkey = result.getInt(pos++);
      operatingSystemVersion = result.getInt(pos++);
      path = PosixPath.valueOf(result.getString(pos++));
      optional = result.getBoolean(pos++);
      type = result.getString(pos++);
      mode = result.getLong(pos++);
      linuxAccount = User.Name.valueOf(result.getString(pos++));
      linuxGroup = Group.Name.valueOf(result.getString(pos++));
      size = result.getLong(pos++);
      if (result.wasNull()) {
        size = NULL_SIZE;
      }
      fileSha0 = result.getLong(pos++);
      fileSha1 = result.getLong(pos++);
      fileSha2 = result.getLong(pos++);
      fileSha3 = result.getLong(pos++);
      hasFileSha = !result.wasNull();
      symlinkTarget = result.getString(pos++);
    } catch (ValidationException e) {
      throw new SQLException(e);
    }
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    try {
      pkey = in.readCompressedInt();
      operatingSystemVersion = in.readCompressedInt();
      path = PosixPath.valueOf(in.readCompressedUTF());
      optional = in.readBoolean();
      type = in.readCompressedUTF().intern();
      mode = in.readLong();
      linuxAccount = User.Name.valueOf(in.readCompressedUTF()).intern();
      linuxGroup = Group.Name.valueOf(in.readCompressedUTF()).intern();
      size = in.readLong();
      hasFileSha = in.readBoolean();
      if (hasFileSha) {
        fileSha0 = in.readLong();
        fileSha1 = in.readLong();
        fileSha2 = in.readLong();
        fileSha3 = in.readLong();
      } else {
        fileSha0 = 0;
        fileSha1 = 0;
        fileSha2 = 0;
        fileSha3 = 0;
      }
      symlinkTarget = in.readBoolean() ? in.readCompressedUTF() : null;
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  private static void writeChars(String s, DataOutputStream out) throws IOException {
    out.writeInt(s.length());
    out.writeChars(s);
  }

  private static String readChars(DataInputStream in) throws IOException {
    int len = in.readInt();
    char[] chars = new char[len];
    for (int i = 0; i < len; i++) {
      chars[i] = in.readChar();
    }
    return new String(chars);
  }

  @Override
  public void readRecord(DataInputStream in) throws IOException {
    try {
      pkey = in.readInt();
      operatingSystemVersion = in.readInt();
      path = PosixPath.valueOf(readChars(in));
      optional = in.readBoolean();
      type = readChars(in).intern();
      mode = in.readLong();
      linuxAccount = User.Name.valueOf(readChars(in)).intern();
      linuxGroup = Group.Name.valueOf(readChars(in)).intern();
      size = in.readLong();
      hasFileSha = in.readBoolean();
      if (hasFileSha) {
        fileSha0 = in.readLong();
        fileSha1 = in.readLong();
        fileSha2 = in.readLong();
        fileSha3 = in.readLong();
      } else {
        fileSha0 = 0;
        fileSha1 = 0;
        fileSha2 = 0;
        fileSha3 = 0;
      }
      symlinkTarget = in.readBoolean() ? readChars(in) : null;
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_108) >= 0) {
      out.writeCompressedInt(pkey);
      out.writeCompressedInt(operatingSystemVersion);
    }
    out.writeCompressedUTF(path.toString(), 0);
    out.writeBoolean(optional);
    out.writeCompressedUTF(type, 1);
    out.writeLong(mode);
    out.writeCompressedUTF(linuxAccount.toString(), 2);
    out.writeCompressedUTF(linuxGroup.toString(), 3);
    out.writeLong(size);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_80) >= 0) {
      out.writeBoolean(hasFileSha);
      if (hasFileSha) {
        out.writeLong(fileSha0);
        out.writeLong(fileSha1);
        out.writeLong(fileSha2);
        out.writeLong(fileSha3);
      }
    } else {
      out.writeBoolean(false); // has_file_md5
    }
    out.writeBoolean(symlinkTarget != null);
    if (symlinkTarget != null) {
      out.writeCompressedUTF(symlinkTarget, 4);
    }
  }

  @Override
  public void writeRecord(DataOutputStream out) throws IOException {
    out.writeInt(pkey);
    out.writeInt(operatingSystemVersion);
    String pathStr = path.toString();
    if (pathStr.length() > MAX_PATH_LENGTH) {
      throw new IOException("path.length()>" + MAX_PATH_LENGTH + ": " + pathStr.length());
    }
    writeChars(pathStr, out);
    out.writeBoolean(optional);
    if (type.length() > MAX_TYPE_LENGTH) {
      throw new IOException("type.length()>" + MAX_TYPE_LENGTH + ": " + type.length());
    }
    writeChars(type, out);
    out.writeLong(mode);
    {
      String linuxAccountStr = linuxAccount.toString();
      if (linuxAccountStr.length() > MAX_LINUX_ACCOUNT_LENGTH) {
        throw new IOException("linux_account.length()>" + MAX_LINUX_ACCOUNT_LENGTH + ": " + linuxAccountStr.length());
      }
      writeChars(linuxAccountStr, out);
    }
    {
      String linuxGroupStr = linuxGroup.toString();
      if (linuxGroupStr.length() > MAX_LINUX_GROUP_LENGTH) {
        throw new IOException("linux_group.length()>" + MAX_LINUX_GROUP_LENGTH + ": " + linuxGroupStr.length());
      }
      writeChars(linuxGroupStr, out);
    }
    out.writeLong(size);
    out.writeBoolean(hasFileSha);
    if (hasFileSha) {
      out.writeLong(fileSha0);
      out.writeLong(fileSha1);
      out.writeLong(fileSha2);
      out.writeLong(fileSha3);
    }
    out.writeBoolean(symlinkTarget != null);
    if (symlinkTarget != null) {
      if (symlinkTarget.length() > MAX_SYMLINK_TARGET_LENGTH) {
        throw new IOException("symlink_target.length()>" + MAX_SYMLINK_TARGET_LENGTH + ": " + symlinkTarget.length());
      }
      writeChars(symlinkTarget, out);
    }
  }
}
