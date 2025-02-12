/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2025  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.web.tomcat;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.linux.PosixPath;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * An <code>HttpdTomcatSite</code> indicates that an <code>HttpdSite</code>
 * uses the Jakarta Tomcat project as its servlet engine.  The servlet
 * engine may be configured in several ways, only what is common to
 * every type of Tomcat installation is stored in <code>HttpdTomcatSite</code>.
 *
 * @see  Site
 * @see  PrivateTomcatSite
 *
 * @author  AO Industries, Inc.
 */
public final class Site extends CachedObjectIntegerKey<Site> {

  static final int COLUMN_HTTPD_SITE = 0;
  public static final String COLUMN_HTTPD_SITE_name = "httpd_site";

  private int version;
  private boolean blockWebinf;

  private boolean useApache; // Only used for protocol compatibility on the server side

  /**
   * The minimum amount of time in milliseconds between Java VM starts.
   */
  public static final int MINIMUM_START_JVM_DELAY = 30000;

  /**
   * The minimum amount of time in milliseconds between Java VM start and stop.
   */
  public static final int MINIMUM_STOP_JVM_DELAY = 15000;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public Site() {
    // Do nothing
  }

  public int addHttpdTomcatContext(
      String className,
      boolean cookies,
      boolean crossContext,
      PosixPath docBase,
      boolean override,
      String path,
      boolean privileged,
      boolean reloadable,
      boolean useNaming,
      String wrapperClass,
      int debug,
      PosixPath workDir,
      boolean serverXmlConfigured
  ) throws IOException, SQLException {
    return table.getConnector().getWeb_tomcat().getContext().addHttpdTomcatContext(
        this,
        className,
        cookies,
        crossContext,
        docBase,
        override,
        path,
        privileged,
        reloadable,
        useNaming,
        wrapperClass,
        debug,
        workDir,
        serverXmlConfigured
    );
  }

  public int addJkMount(
      String path,
      boolean mount
  ) throws IOException, SQLException {
    return table.getConnector().getWeb_tomcat().getJkMount().addHttpdTomcatSiteJkMount(
        this,
        path,
        mount
    );
  }

  /**
   * Determines if the API user is allowed to stop the Java virtual machine associated
   * with this site.
   */
  public boolean canStop() throws SQLException, IOException {
    if (getHttpdSite().isDisabled()) {
      return false;
    }
    SharedTomcatSite shr = getHttpdTomcatSharedSite();
    if (shr != null) {
      return shr.canStop();
    }
    return true;
  }

  /**
   * Determines if the API user is allowed to start the Java virtual machine associated
   * with this site.
   */
  public boolean canStart() throws SQLException, IOException {
    if (getHttpdSite().isDisabled()) {
      return false;
    }
    SharedTomcatSite shr = getHttpdTomcatSharedSite();
    if (shr != null) {
      return shr.canStart();
    }
    return true;
  }

  @Override
  protected Object getColumnImpl(int i) {
    if (i == COLUMN_HTTPD_SITE) {
      return pkey;
    }
    if (i == 1) {
      return version;
    }
    if (i == 2) {
      return blockWebinf;
    }
    throw new IllegalArgumentException("Invalid index: " + i);
  }

  public com.aoindustries.aoserv.client.web.jboss.Site getHttpdJbossSite() throws SQLException, IOException {
    return table.getConnector().getWeb_jboss().getSite().get(pkey);
  }

  public com.aoindustries.aoserv.client.web.Site getHttpdSite() throws SQLException, IOException {
    com.aoindustries.aoserv.client.web.Site obj = table.getConnector().getWeb().getSite().get(pkey);
    if (obj == null) {
      throw new SQLException("Unable to find HttpdSite: " + pkey);
    }
    return obj;
  }

  public Context getHttpdTomcatContext(String path) throws IOException, SQLException {
    return table.getConnector().getWeb_tomcat().getContext().getHttpdTomcatContext(this, path);
  }

  public List<Context> getHttpdTomcatContexts() throws IOException, SQLException {
    return table.getConnector().getWeb_tomcat().getContext().getHttpdTomcatContexts(this);
  }

  public SharedTomcatSite getHttpdTomcatSharedSite() throws IOException, SQLException {
    return table.getConnector().getWeb_tomcat().getSharedTomcatSite().get(pkey);
  }

  public PrivateTomcatSite getHttpdTomcatStdSite() throws IOException, SQLException {
    return table.getConnector().getWeb_tomcat().getPrivateTomcatSite().get(pkey);
  }

  public Version getHttpdTomcatVersion() throws SQLException, IOException {
    Version obj = table.getConnector().getWeb_tomcat().getVersion().get(version);
    if (obj == null) {
      throw new SQLException("Unable to find HttpdTomcatVersion: " + version);
    }
    if (
        obj.getTechnologyVersion(table.getConnector()).getOperatingSystemVersion(table.getConnector()).getPkey()
            != getHttpdSite().getLinuxServer().getHost().getOperatingSystemVersion_id()
    ) {
      throw new SQLException("resource/operating system version mismatch on HttpdTomcatSite: #" + pkey);
    }
    // Make sure version shared JVM if is a shared site
    SharedTomcatSite sharedSite = getHttpdTomcatSharedSite();
    if (sharedSite != null) {
      if (
          obj.getPkey()
              != sharedSite.getHttpdSharedTomcat().getHttpdTomcatVersion().getPkey()
      ) {
        throw new SQLException("HttpdTomcatSite/HttpdSharedTomcat version mismatch on HttpdTomcatSite: #" + pkey);
      }
    }
    return obj;
  }

  public List<Worker> getHttpdWorkers() throws IOException, SQLException {
    return table.getConnector().getWeb_tomcat().getWorker().getHttpdWorkers(this);
  }

  public List<JkMount> getJkMounts() throws IOException, SQLException {
    return table.getConnector().getWeb_tomcat().getJkMount().getHttpdTomcatSiteJkMounts(this);
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.HTTPD_TOMCAT_SITES;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    pkey = result.getInt(1);
    version = result.getInt(2);
    blockWebinf = result.getBoolean(3);
    useApache = result.getBoolean(4);
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    pkey = in.readCompressedInt();
    version = in.readCompressedInt();
    blockWebinf = in.readBoolean();
  }

  public String startJvm() throws IOException, SQLException {
    return table.getConnector().requestResult(
        false,
        AoservProtocol.CommandId.START_JVM,
        new AoservConnector.ResultRequest<>() {
          private String result;
          @Override
          public void writeRequest(StreamableOutput out) throws IOException {
            out.writeCompressedInt(pkey);
          }

          @Override
          public void readResponse(StreamableInput in) throws IOException, SQLException {
            int code = in.readByte();
            if (code == AoservProtocol.DONE) {
              result = in.readNullUTF();
              return;
            }
            AoservProtocol.checkResult(code, in);
            throw new IOException("Unexpected response code: " + code);
          }

          @Override
          public String afterRelease() {
            return result;
          }
        }
    );
  }

  public String stopJvm() throws IOException, SQLException {
    return table.getConnector().requestResult(
        false,
        AoservProtocol.CommandId.STOP_JVM,
        new AoservConnector.ResultRequest<>() {
          private String result;
          @Override
          public void writeRequest(StreamableOutput out) throws IOException {
            out.writeCompressedInt(pkey);
          }

          @Override
          public void readResponse(StreamableInput in) throws IOException, SQLException {
            int code = in.readByte();
            if (code == AoservProtocol.DONE) {
              result = in.readNullUTF();
              return;
            }
            AoservProtocol.checkResult(code, in);
            throw new IOException("Unexpected response code: " + code);
          }

          @Override
          public String afterRelease() {
            return result;
          }
        }
    );
  }

  @Override
  public String toStringImpl() throws SQLException, IOException {
    return getHttpdSite().toStringImpl();
  }

  /**
   * Blocks access to <code>/META-INF</code>
   * and <code>/WEB-INF</code> at the <a href="https://httpd.apache.org/">Apache</a> level.  When
   * <a href="https://httpd.apache.org/">Apache</a> serves content directly, instead of passing all
   * requests to <a href="http://tomcat.apache.org/">Tomcat</a>, this helps ensure proper protection
   * of these paths.
   */
  public boolean getBlockWebinf() {
    return blockWebinf;
  }

  public void setBlockWebinf(boolean blockWebinf) throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(true, AoservProtocol.CommandId.SET_HTTPD_TOMCAT_SITE_BLOCK_WEBINF, pkey, blockWebinf);
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeCompressedInt(version);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_81_6) >= 0) {
      out.writeBoolean(blockWebinf);
    } else  {
      out.writeBoolean(useApache);
    }
  }
}
