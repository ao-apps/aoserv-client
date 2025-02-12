/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2003-2009, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2025  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.distribution;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.validation.ValidationException;
import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.GlobalObjectIntegerKey;
import com.aoindustries.aoserv.client.email.List;
import com.aoindustries.aoserv.client.linux.PosixPath;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * One version of a operating system.
 *
 * @see OperatingSystem
 *
 * @author  AO Industries, Inc.
 */
public final class OperatingSystemVersion extends GlobalObjectIntegerKey<OperatingSystemVersion> {

  static final int COLUMN_PKEY = 0;
  static final String COLUMN_SORT_ORDER_name = "sort_order";

  //public static final String VERSION_1_4 = "1.4";
  //public static final String VERSION_7_2 = "7.2";
  //public static final String VERSION_9_2 = "9.2";
  public static final String VERSION_5 = "5";
  public static final String VERSION_5_DOM0 = "5.dom0";
  public static final String VERSION_7 = "7";
  public static final String VERSION_7_DOM0 = "7.dom0";
  public static final String VERSION_9 = "9";
  public static final String VERSION_2006_0 = "2006.0";
  public static final String VERSION_ES_4 = "ES 4";

  /**
   * @deprecated  What is this used for?
   *
   * @see  OperatingSystem#DEFAULT_OPERATING_SYSTEM
   */
  @Deprecated
  public static final String DEFAULT_OPERATING_SYSTEM_VERSION = VERSION_9;

  public static final int CENTOS_5_DOM0_X86_64 = 63;
  public static final int CENTOS_5_DOM0_I686 = 64;
  public static final int CENTOS_5_I686_AND_X86_64 = 67;
  public static final int CENTOS_7_DOM0_X86_64 = 69;
  public static final int CENTOS_7_X86_64 = 70;
  public static final int ROCKY_9_X86_64 = 71;

  private String operatingSystem;
  private String versionNumber;
  private String versionName;
  private String architecture;
  private String display;
  private boolean isAoservDaemonSupported;
  private short sortOrder;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public OperatingSystemVersion() {
    // Do nothing
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_PKEY:
        return pkey;
      case 1:
        return operatingSystem;
      case 2:
        return versionNumber;
      case 3:
        return versionName;
      case 4:
        return architecture;
      case 5:
        return display;
      case 6:
        return isAoservDaemonSupported;
      case 7:
        return sortOrder;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public OperatingSystem getOperatingSystem(AoservConnector conn) throws IOException, SQLException {
    return conn.getDistribution().getOperatingSystem().get(operatingSystem);
  }

  public String getVersionNumber() {
    return versionNumber;
  }

  public String getVersionName() {
    return versionName;
  }

  public String getArchitecture_name() {
    return architecture;
  }

  public Architecture getArchitecture(AoservConnector connector) throws SQLException, IOException {
    Architecture ar = connector.getDistribution().getArchitecture().get(architecture);
    if (ar == null) {
      throw new SQLException("Unable to find Architecture: " + architecture);
    }
    return ar;
  }

  public String getDisplay() {
    return display;
  }

  public boolean isAoservDaemonSupported() {
    return isAoservDaemonSupported;
  }

  public short getSortOrder() {
    return sortOrder;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.OPERATING_SYSTEM_VERSIONS;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    pkey = result.getInt(1);
    operatingSystem = result.getString(2);
    versionNumber = result.getString(3);
    versionName = result.getString(4);
    architecture = result.getString(5);
    display = result.getString(6);
    isAoservDaemonSupported = result.getBoolean(7);
    sortOrder = result.getShort(8);
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    pkey = in.readCompressedInt();
    operatingSystem = in.readUTF().intern();
    versionNumber = in.readUTF();
    versionName = in.readUTF();
    architecture = in.readUTF().intern();
    display = in.readUTF();
    isAoservDaemonSupported = in.readBoolean();
    sortOrder = in.readShort();
  }

  @Override
  public String toStringImpl() {
    return display;
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeUTF(operatingSystem);
    out.writeUTF(versionNumber);
    out.writeUTF(versionName);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_108) >= 0) {
      out.writeUTF(architecture);
    }
    out.writeUTF(display);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_108) >= 0) {
      out.writeBoolean(isAoservDaemonSupported);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_3) >= 0) {
      out.writeShort(sortOrder);
    }
  }

  /**
   * Gets the directory that stores websites for this operating system or {@code null}
   * if this OS doesn't support web sites.
   */
  public PosixPath getHttpdSitesDirectory() {
    return getHttpdSitesDirectory(pkey);
  }

  private static final PosixPath WWW;
  private static final PosixPath VAR_WWW;

  static {
    try {
      WWW = PosixPath.valueOf("/www").intern();
      VAR_WWW = PosixPath.valueOf("/var/www").intern();
    } catch (ValidationException e) {
      throw new AssertionError("These hard-coded values are valid", e);
    }
  }

  /**
   * Gets the directory that stores websites for this operating system or {@code null}
   * if this OS doesn't support web sites.
   */
  public static PosixPath getHttpdSitesDirectory(int osv) {
    switch (osv) {
      case CENTOS_5_I686_AND_X86_64:
        return WWW;
      case CENTOS_7_X86_64:
      case ROCKY_9_X86_64:
        return VAR_WWW;
      case CENTOS_5_DOM0_I686:
      case CENTOS_5_DOM0_X86_64:
      case CENTOS_7_DOM0_X86_64:
        return null;
      default:
        throw new AssertionError("Unexpected OperatingSystemVersion: " + osv);
    }
  }

  /**
   * Gets the directory that contains the shared tomcat directories or {@code null}
   * if this OS doesn't support shared tomcats.
   */
  public PosixPath getHttpdSharedTomcatsDirectory() {
    return getHttpdSharedTomcatsDirectory(pkey);
  }

  private static final PosixPath WWWGROUP;
  private static final PosixPath VAR_OPT_APACHE_TOMCAT;

  static {
    try {
      WWWGROUP = PosixPath.valueOf("/wwwgroup").intern();
      VAR_OPT_APACHE_TOMCAT = PosixPath.valueOf("/var/opt/apache-tomcat").intern();
    } catch (ValidationException e) {
      throw new AssertionError("These hard-coded values are valid", e);
    }
  }

  /**
   * Gets the directory that contains the shared tomcat directories or {@code null}
   * if this OS doesn't support shared tomcats.
   */
  public static PosixPath getHttpdSharedTomcatsDirectory(int osv) {
    switch (osv) {
      case CENTOS_5_I686_AND_X86_64:
        return WWWGROUP;
      case CENTOS_7_X86_64:
      case ROCKY_9_X86_64:
        return VAR_OPT_APACHE_TOMCAT;
      case CENTOS_5_DOM0_I686:
      case CENTOS_5_DOM0_X86_64:
      case CENTOS_7_DOM0_X86_64:
        return null;
      default:
        throw new AssertionError("Unexpected OperatingSystemVersion: " + osv);
    }
  }

  /**
   * Gets the directory that contains the per-virtual-host HTTP logs or {@code null}
   * if this OS doesn't support web sites.
   */
  public PosixPath getHttpdSiteLogsDirectory() {
    return getHttpdSiteLogsDirectory(pkey);
  }

  private static final PosixPath LOGS;
  private static final PosixPath VAR_LOG_HTTPD_SITES;

  static {
    try {
      LOGS = PosixPath.valueOf("/logs").intern();
      VAR_LOG_HTTPD_SITES = PosixPath.valueOf("/var/log/httpd-sites").intern();
    } catch (ValidationException e) {
      throw new AssertionError("These hard-coded values are valid", e);
    }
  }

  /**
   * Gets the directory that contains the per-virtual-host HTTP logs or {@code null}
   * if this OS doesn't support web sites.
   */
  public static PosixPath getHttpdSiteLogsDirectory(int osv) {
    switch (osv) {
      case CENTOS_5_I686_AND_X86_64:
        return LOGS;
      case CENTOS_7_X86_64:
      case ROCKY_9_X86_64:
        return VAR_LOG_HTTPD_SITES;
      case CENTOS_5_DOM0_I686:
      case CENTOS_5_DOM0_X86_64:
      case CENTOS_7_DOM0_X86_64:
        return null;
      default:
        throw new AssertionError("Unexpected OperatingSystemVersion: " + osv);
    }
  }

  /**
   * @see  List#getListPath(java.lang.String, int)
   */
  public PosixPath getEmailListPath(String name) throws ValidationException {
    return List.getListPath(name, pkey);
  }

  /**
   * @see  List#isValidRegularPath(com.aoindustries.aoserv.client.linux.PosixPath, int)
   */
  public boolean isValidEmailListRegularPath(PosixPath path) {
    return List.isValidRegularPath(path, pkey);
  }
}
