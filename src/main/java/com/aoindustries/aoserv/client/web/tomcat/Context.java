/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2002-2009, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2025  AO Industries, Inc.
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

import com.aoapps.collections.IntList;
import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.validation.ValidationException;
import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.CannotRemoveReason;
import com.aoindustries.aoserv.client.Removable;
import com.aoindustries.aoserv.client.linux.PosixPath;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents one context within a <code>HttpdTomcatSite</code>.
 *
 * @see  Site
 *
 * @author  AO Industries, Inc.
 */
public final class Context extends CachedObjectIntegerKey<Context> implements Removable {

  static final int COLUMN_PKEY = 0;
  static final int COLUMN_TOMCAT_SITE = 1;
  static final String COLUMN_TOMCAT_SITE_name = "tomcat_site";
  static final String COLUMN_PATH_name = "path";

  /**
   * These are the default values for a new site.
   */
  public static final String DEFAULT_CLASS_NAME = null;
  public static final boolean DEFAULT_COOKIES = true;
  public static final boolean DEFAULT_CROSS_CONTEXT = false;
  public static final boolean DEFAULT_OVERRIDE = false;
  public static final boolean DEFAULT_PRIVILEGED = false;
  public static final boolean DEFAULT_RELOADABLE = false;
  public static final boolean DEFAULT_USE_NAMING = true;
  public static final String DEFAULT_WRAPPER_CLASS = null;
  public static final int DEFAULT_DEBUG = 0;
  public static final PosixPath DEFAULT_WORK_DIR = null;
  public static final boolean DEFAULT_SERVER_XML_CONFIGURED = true;

  /**
   * The ROOT webapp details.
   */
  public static final String ROOT_PATH = "";
  public static final String ROOT_DOC_BASE = "ROOT";

  private int tomcatSite;
  private String className;
  private boolean cookies;
  private boolean crossContext;
  private PosixPath docBase;
  private boolean override;
  private String path;
  private boolean privileged;
  private boolean reloadable;
  private boolean useNaming;
  private String wrapperClass;
  private int debug;
  private PosixPath workDir;
  private boolean serverXmlConfigured;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public Context() {
    // Do nothing
  }

  public int addHttpdTomcatDataSource(
      String name,
      String driverClassName,
      String url,
      String username,
      String password,
      int maxActive,
      int maxIdle,
      int maxWait,
      String validationQuery
  ) throws IOException, SQLException {
    return table.getConnector().getWeb_tomcat().getContextDataSource().addHttpdTomcatDataSource(
        this,
        name,
        driverClassName,
        url,
        username,
        password,
        maxActive,
        maxIdle,
        maxWait,
        validationQuery
    );
  }

  public int addHttpdTomcatParameter(String name, String value, boolean override, String description) throws IOException, SQLException {
    return table.getConnector().getWeb_tomcat().getContextParameter().addHttpdTomcatParameter(this, name, value, override, description);
  }

  @Override
  public List<CannotRemoveReason<Context>> getCannotRemoveReasons() {
    List<CannotRemoveReason<Context>> reasons = new ArrayList<>();
    if (path.length() == 0) {
      reasons.add(new CannotRemoveReason<>("Not allowed to remove the root context", this));
    }
    return reasons;
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_PKEY:
        return pkey;
      case COLUMN_TOMCAT_SITE:
        return tomcatSite;
      case 2:
        return className;
      case 3:
        return cookies;
      case 4:
        return crossContext;
      case 5:
        return docBase;
      case 6:
        return override;
      case 7:
        return path;
      case 8:
        return privileged;
      case 9:
        return reloadable;
      case 10:
        return useNaming;
      case 11:
        return wrapperClass;
      case 12:
        return debug;
      case 13:
        return workDir;
      case 14:
        return serverXmlConfigured;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public int getHttpdTomcatSite_httpdSite_id() {
    return tomcatSite;
  }

  public Site getHttpdTomcatSite() throws SQLException, IOException {
    Site obj = table.getConnector().getWeb_tomcat().getSite().get(tomcatSite);
    if (obj == null) {
      throw new SQLException("Unable to find HttpdTomcatSite: " + tomcatSite);
    }
    return obj;
  }

  public String getClassName() {
    return className;
  }

  public boolean useCookies() {
    return cookies;
  }

  public boolean allowCrossContext() {
    return crossContext;
  }

  public PosixPath getDocBase() {
    return docBase;
  }

  public boolean allowOverride() {
    return override;
  }

  public String getPath() {
    return path;
  }

  public boolean isPrivileged() {
    return privileged;
  }

  public boolean isReloadable() {
    return reloadable;
  }

  public boolean useNaming() {
    return useNaming;
  }

  public String getWrapperClass() {
    return wrapperClass;
  }

  public int getDebugLevel() {
    return debug;
  }

  public PosixPath getWorkDir() {
    return workDir;
  }

  public boolean isServerXmlConfigured() {
    return serverXmlConfigured;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.HTTPD_TOMCAT_CONTEXTS;
  }

  public List<ContextDataSource> getHttpdTomcatDataSources() throws IOException, SQLException {
    return table.getConnector().getWeb_tomcat().getContextDataSource().getHttpdTomcatDataSources(this);
  }

  public ContextDataSource getHttpdTomcatDataSource(String name) throws IOException, SQLException {
    return table.getConnector().getWeb_tomcat().getContextDataSource().getHttpdTomcatDataSource(this, name);
  }

  public List<ContextParameter> getHttpdTomcatParameters() throws IOException, SQLException {
    return table.getConnector().getWeb_tomcat().getContextParameter().getHttpdTomcatParameters(this);
  }

  public ContextParameter getHttpdTomcatParameter(String name) throws IOException, SQLException {
    return table.getConnector().getWeb_tomcat().getContextParameter().getHttpdTomcatParameter(this, name);
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    try {
      pkey = result.getInt(1);
      tomcatSite = result.getInt(2);
      className = result.getString(3);
      cookies = result.getBoolean(4);
      crossContext = result.getBoolean(5);
      docBase = PosixPath.valueOf(result.getString(6));
      override = result.getBoolean(7);
      path = result.getString(8);
      privileged = result.getBoolean(9);
      reloadable = result.getBoolean(10);
      useNaming = result.getBoolean(11);
      wrapperClass = result.getString(12);
      debug = result.getInt(13);
      workDir = PosixPath.valueOf(result.getString(14));
      serverXmlConfigured = result.getBoolean(15);
    } catch (ValidationException e) {
      throw new SQLException(e);
    }
  }

  public static boolean isValidDocBase(PosixPath docBase) {
    String docBaseStr = docBase.toString();
    return
        /* UnixPath checks these:
        docBase.length()>1
        && docBase.charAt(0) == '/'
        && !docBase.contains("//")
        && !docBase.contains("..")
        */
        docBaseStr.indexOf('"') == -1
            && docBaseStr.indexOf('\\') == -1
            && docBaseStr.indexOf('\n') == -1
            && docBaseStr.indexOf('\r') == -1;
  }

  public static boolean isValidPath(String path) {
    try {
      return
          path.length() == 0
              || (
              PosixPath.validate(path).isValid()
                  && isValidDocBase(PosixPath.valueOf(path))
          );
    } catch (ValidationException e) {
      throw new AssertionError("Already validated", e);
    }
  }

  public static boolean isValidWorkDir(PosixPath workDir) {
    return workDir == null || isValidDocBase(workDir);
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    try {
      pkey = in.readCompressedInt();
      tomcatSite = in.readCompressedInt();
      className = in.readNullUTF();
      cookies = in.readBoolean();
      crossContext = in.readBoolean();
      docBase = PosixPath.valueOf(in.readUTF());
      override = in.readBoolean();
      path = in.readUTF();
      privileged = in.readBoolean();
      reloadable = in.readBoolean();
      useNaming = in.readBoolean();
      wrapperClass = in.readNullUTF();
      debug = in.readCompressedInt();
      workDir = PosixPath.valueOf(in.readNullUTF());
      serverXmlConfigured = in.readBoolean();
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  public void setAttributes(
      final String className,
      final boolean cookies,
      final boolean crossContext,
      final PosixPath docBase,
      final boolean override,
      final String path,
      final boolean privileged,
      final boolean reloadable,
      final boolean useNaming,
      final String wrapperClass,
      final int debug,
      final PosixPath workDir,
      final boolean serverXmlConfigured
  ) throws IOException, SQLException {
    table.getConnector().requestUpdate(
        true,
        AoservProtocol.CommandId.SET_HTTPD_TOMCAT_CONTEXT_ATTRIBUTES,
        new AoservConnector.UpdateRequest() {
          private IntList invalidateList;

          @Override
          public void writeRequest(StreamableOutput out) throws IOException {
            out.writeCompressedInt(pkey);
            out.writeNullUTF(className);
            out.writeBoolean(cookies);
            out.writeBoolean(crossContext);
            out.writeUTF(docBase.toString());
            out.writeBoolean(override);
            out.writeUTF(path);
            out.writeBoolean(privileged);
            out.writeBoolean(reloadable);
            out.writeBoolean(useNaming);
            out.writeNullUTF(wrapperClass);
            out.writeCompressedInt(debug);
            out.writeNullUTF(Objects.toString(workDir, null));
            out.writeBoolean(serverXmlConfigured);
          }

          @Override
          public void readResponse(StreamableInput in) throws IOException, SQLException {
            int code = in.readByte();
            if (code == AoservProtocol.DONE) {
              invalidateList = AoservConnector.readInvalidateList(in);
            } else {
              AoservProtocol.checkResult(code, in);
              throw new IOException("Unexpected response code: " + code);
            }
          }

          @Override
          public void afterRelease() {
            table.getConnector().tablesUpdated(invalidateList);
          }
        }
    );
  }

  @Override
  public void remove() throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(true, AoservProtocol.CommandId.REMOVE, Table.TableId.HTTPD_TOMCAT_CONTEXTS, pkey);
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeCompressedInt(tomcatSite);
    out.writeNullUTF(className);
    out.writeBoolean(cookies);
    out.writeBoolean(crossContext);
    out.writeUTF(docBase.toString());
    out.writeBoolean(override);
    out.writeUTF(path);
    out.writeBoolean(privileged);
    out.writeBoolean(reloadable);
    out.writeBoolean(useNaming);
    out.writeNullUTF(wrapperClass);
    out.writeCompressedInt(debug);
    out.writeNullUTF(Objects.toString(workDir, null));
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_81_3) >= 0) {
      out.writeBoolean(serverXmlConfigured);
    }
  }
}
