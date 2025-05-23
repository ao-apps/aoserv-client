/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2025  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.postgresql;

import com.aoapps.hodgepodge.io.ByteCountInputStream;
import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.Strings;
import com.aoapps.lang.dto.DtoFactory;
import com.aoapps.lang.i18n.Resources;
import com.aoapps.lang.io.IoUtils;
import com.aoapps.lang.util.Internable;
import com.aoapps.lang.validation.InvalidResult;
import com.aoapps.lang.validation.ValidResult;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.lang.validation.ValidationResult;
import com.aoapps.net.InetAddress;
import com.aoapps.net.Port;
import com.aoapps.net.URIEncoder;
import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.CannotRemoveReason;
import com.aoindustries.aoserv.client.Dumpable;
import com.aoindustries.aoserv.client.JdbcProvider;
import com.aoindustries.aoserv.client.NestedInputStream;
import com.aoindustries.aoserv.client.Removable;
import com.aoindustries.aoserv.client.StreamHandler;
import com.aoindustries.aoserv.client.net.Bind;
import com.aoindustries.aoserv.client.net.IpAddress;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A <code>PostgresDatabase</code> corresponds to a unique PostgreSQL table
 * space on one server.  The database name must be unique per server
 * and, to aid in account portability, will typically be unique
 * across the entire system.
 *
 * @see  Encoding
 * @see  UserServer
 *
 * @author  AO Industries, Inc.
 */
public final class Database extends CachedObjectIntegerKey<Database> implements Dumpable, Removable, JdbcProvider {

  private static final Resources RESOURCES = Resources.getResources(ResourceBundle::getBundle, Database.class);

  /**
   * Represents a name that may be used for a PostgreSQL database.  Database names must:
   * <ul>
   *   <li>Be non-null</li>
   *   <li>Be non-empty</li>
   *   <li>Be between 1 and 31 characters</li>
   *   <li>Characters may contain <code>[a-z,A-Z,0-9,_,-,.,(space)]</code></li>
   * </ul>
   *
   * @author  AO Industries, Inc.
   */
  public static final class Name implements
      Comparable<Name>,
      Serializable,
      DtoFactory<com.aoindustries.aoserv.client.dto.PostgresDatabaseName>,
      Internable<Name> {

    private static final long serialVersionUID = 5843440870677129701L;

    /**
     * The name of a database is limited by the internal data type of
     * the <code>pg_database</code> table.  The type is <code>name</code>
     * which has a maximum length of 31 characters.
     */
    public static final int MAX_LENGTH = 31;

    /**
     * Validates a PostgreSQL database name.
     */
    public static ValidationResult validate(String name) {
      if (name == null) {
        return new InvalidResult(RESOURCES, "Name.validate.isNull");
      }
      int len = name.length();
      if (len == 0) {
        return new InvalidResult(RESOURCES, "Name.validate.isEmpty");
      }
      if (len > MAX_LENGTH) {
        return new InvalidResult(RESOURCES, "Name.validate.tooLong", MAX_LENGTH, len);
      }

      // Characters may contain [a-z,A-Z,0-9,_,-,.]
      for (int c = 0; c < len; c++) {
        char ch = name.charAt(c);
        if (
            (ch < 'a' || ch > 'z')
                && (ch < 'A' || ch > 'Z')
                && (ch < '0' || ch > '9')
                && ch != '_'
                && ch != '-'
                && ch != '.'
                && ch != ' '
        ) {
          return new InvalidResult(RESOURCES, "Name.validate.illegalCharacter");
        }
      }
      return ValidResult.getInstance();
    }

    private static final ConcurrentMap<String, Name> interned = new ConcurrentHashMap<>();

    /**
     * @param name  when {@code null}, returns {@code null}
     */
    public static Name valueOf(String name) throws ValidationException {
      if (name == null) {
        return null;
      }
      //Name existing = interned.get(name);
      //return existing != null ? existing : new Name(name);
      return new Name(name, true);
    }

    private final String name;

    private Name(String name, boolean validate) throws ValidationException {
      this.name = name;
      if (validate) {
        validate();
      }
    }

    /**
     * @param  name  Does not validate, should only be used with a known valid value.
     */
    private Name(String name) {
      ValidationResult result;
      assert (result = validate(name)).isValid() : result.toString();
      this.name = name;
    }

    private void validate() throws ValidationException {
      ValidationResult result = validate(name);
      if (!result.isValid()) {
        throw new ValidationException(result);
      }
    }

    /**
     * Perform same validation as constructor on readObject.
     */
    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
      ois.defaultReadObject();
      try {
        validate();
      } catch (ValidationException err) {
        InvalidObjectException newErr = new InvalidObjectException(err.getMessage());
        newErr.initCause(err);
        throw newErr;
      }
    }

    @Override
    public boolean equals(Object obj) {
      return
          (obj instanceof Name)
              && name.equals(((Name) obj).name);
    }

    @Override
    public int hashCode() {
      return name.hashCode();
    }

    @Override
    public int compareTo(Name other) {
      return this == other ? 0 : name.compareTo(other.name);
    }

    @Override
    public String toString() {
      return name;
    }

    /**
     * Interns this name much in the same fashion as <code>String.intern()</code>.
     *
     * @see  String#intern()
     */
    @Override
    public Name intern() {
      Name existing = interned.get(name);
      if (existing == null) {
        String internedName = name.intern();
        @SuppressWarnings("StringEquality")
        Name addMe = (name == internedName) ? this : new Name(internedName);
        existing = interned.putIfAbsent(internedName, addMe);
        if (existing == null) {
          existing = addMe;
        }
      }
      return existing;
    }

    @Override
    public com.aoindustries.aoserv.client.dto.PostgresDatabaseName getDto() {
      return new com.aoindustries.aoserv.client.dto.PostgresDatabaseName(name);
    }
  }

  static final int COLUMN_PKEY = 0;
  static final int COLUMN_POSTGRES_SERVER = 2;
  static final int COLUMN_DATDBA = 3;
  static final String COLUMN_NAME_name = "name";
  static final String COLUMN_POSTGRES_SERVER_name = "postgres_server";

  /**
   * The classname of the JDBC driver used for the <code>PostgresDatabase</code>.
   */
  public static final String JDBC_DRIVER = "org.postgresql.Driver";

  /** Templates. */
  public static final Name
      TEMPLATE0,
      TEMPLATE1;
  /** Monitoring. */
  public static final Name POSTGRESMON;
  /** AO Platform Components. */
  public static final Name
      AOINDUSTRIES, // TODO: Remove once renamed to "aoserv-master"
      AOSERV,
      AOSERV_MASTER,
      AOWEB;

  static {
    try {
      TEMPLATE0 = Name.valueOf("template0");
      TEMPLATE1 = Name.valueOf("template1");
      POSTGRESMON = Name.valueOf("postgresmon");
      AOINDUSTRIES = Name.valueOf("aoindustries");
      AOSERV = Name.valueOf("aoserv");
      AOSERV_MASTER = Name.valueOf("aoserv-master");
      AOWEB = Name.valueOf("aoweb");
    } catch (ValidationException e) {
      throw new AssertionError("These hard-coded values are valid", e);
    }
  }

  /**
   * Special PostgreSQL databases may not be added or removed.
   */
  public static boolean isSpecial(Name name) {
    return
        // Templates
        name.equals(TEMPLATE0)
            || name.equals(TEMPLATE1)
            // Monitoring
            || name.equals(POSTGRESMON)
            // AO Platform Components
            || name.equals(AOINDUSTRIES)
            || name.equals(AOSERV)
            || name.equals(AOSERV_MASTER)
            || name.equals(AOWEB);
  }

  private Name name;
  private int postgresServer;
  private int datdba;
  private int encoding;
  private boolean isTemplate;
  private boolean allowConn;
  private boolean enablePostgis;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public Database() {
    // Do nothing
  }

  public boolean allowsConnections() {
    return allowConn;
  }

  /**
   * {@inheritDoc}
   *
   * @see  #dump(java.io.Writer)
   */
  @Override
  public void dump(PrintWriter out) throws IOException, SQLException {
    dump((Writer) out);
  }

  /**
   * The character set used by the dumps.
   */
  public static final Charset DUMP_ENCODING = StandardCharsets.ISO_8859_1;

  /**
   * Dumps the database into textual representation, not gzipped.
   */
  public void dump(final Writer out) throws IOException, SQLException {
    table.getConnector().requestUpdate(
        false,
        AoservProtocol.CommandId.DUMP_POSTGRES_DATABASE,
        new AoservConnector.UpdateRequest() {
          @Override
          public void writeRequest(StreamableOutput masterOut) throws IOException {
            masterOut.writeCompressedInt(pkey);
            masterOut.writeBoolean(false);
          }

          @Override
          public void readResponse(StreamableInput masterIn) throws IOException, SQLException {
            long dumpSize = masterIn.readLong();
            if (dumpSize < 0) {
              throw new IOException("dumpSize < 0: " + dumpSize);
            }
            long bytesRead;
            try (
                ByteCountInputStream byteCountIn = new ByteCountInputStream(new NestedInputStream(masterIn));
                Reader nestedIn = new InputStreamReader(byteCountIn, DUMP_ENCODING)
                ) {
              IoUtils.copy(nestedIn, out);
              bytesRead = byteCountIn.getCount();
            }
            if (bytesRead < dumpSize) {
              throw new IOException("Too few bytes read: " + bytesRead + " < " + dumpSize);
            }
            if (bytesRead > dumpSize) {
              throw new IOException("Too many bytes read: " + bytesRead + " > " + dumpSize);
            }
          }

          @Override
          public void afterRelease() {
            // Do nothing
          }
        }
    );
  }

  /**
   * Dumps the database in {@link #DUMP_ENCODING} encoding into binary form, optionally gzipped.
   */
  public void dump(
      final boolean gzip,
      final StreamHandler streamHandler
  ) throws IOException, SQLException {
    table.getConnector().requestUpdate(
        false,
        AoservProtocol.CommandId.DUMP_POSTGRES_DATABASE,
        new AoservConnector.UpdateRequest() {
          @Override
          public void writeRequest(StreamableOutput masterOut) throws IOException {
            masterOut.writeCompressedInt(pkey);
            masterOut.writeBoolean(gzip);
          }

          @Override
          public void readResponse(StreamableInput masterIn) throws IOException, SQLException {
            long dumpSize = masterIn.readLong();
            if (dumpSize < -1) {
              throw new IOException("dumpSize < -1: " + dumpSize);
            }
            streamHandler.onDumpSize(dumpSize);
            long bytesRead;
            try (InputStream nestedIn = new NestedInputStream(masterIn)) {
              bytesRead = IoUtils.copy(nestedIn, streamHandler.getOut());
            }
            if (dumpSize != -1) {
              if (bytesRead < dumpSize) {
                throw new IOException("Too few bytes read: " + bytesRead + " < " + dumpSize);
              }
              if (bytesRead > dumpSize) {
                throw new IOException("Too many bytes read: " + bytesRead + " > " + dumpSize);
              }
            }
          }

          @Override
          public void afterRelease() {
            // Do nothing
          }
        }
    );
  }

  /**
   * Indicates that PostGIS should be enabled for this database.
   */
  public boolean getEnablePostgis() {
    return enablePostgis;
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_PKEY:
        return pkey;
      case 1:
        return name;
      case COLUMN_POSTGRES_SERVER:
        return postgresServer;
      case COLUMN_DATDBA:
        return datdba;
      case 4:
        return encoding;
      case 5:
        return isTemplate;
      case 6:
        return allowConn;
      case 7:
        return enablePostgis;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public int getDatdba_id() {
    return datdba;
  }

  public UserServer getDatdba() throws SQLException, IOException {
    UserServer obj = table.getConnector().getPostgresql().getUserServer().get(datdba);
    if (obj == null) {
      throw new SQLException("Unable to find PostgresServerUser: " + datdba);
    }
    return obj;
  }

  @Override
  public String getJdbcDriver() {
    return JDBC_DRIVER;
  }

  @Override
  public String getJdbcUrl(boolean ipOnly) throws SQLException, IOException {
    Server ps = getPostgresServer();
    com.aoindustries.aoserv.client.linux.Server ao = ps.getLinuxServer();
    StringBuilder jdbcUrl = new StringBuilder();
    jdbcUrl.append("jdbc:postgresql://");
    Bind nb = ps.getBind();
    IpAddress ip = nb.getIpAddress();
    InetAddress ia = ip.getInetAddress();
    if (ipOnly) {
      if (ia.isUnspecified()) {
        jdbcUrl.append(ao.getHost().getNetDevice(ao.getDaemonDeviceId().getName()).getPrimaryIpAddress().getInetAddress().toBracketedString());
      } else {
        jdbcUrl.append(ia.toBracketedString());
      }
    } else {
      if (ia.isUnspecified()) {
        jdbcUrl.append(ao.getHostname());
      } else if (ia.isLoopback()) {
        // Loopback as IP addresses to avoid ambiguity about which stack (IPv4/IPv6) used for "localhost" in Java
        jdbcUrl.append(ia.toBracketedString());
      } else {
        jdbcUrl.append(ip.getHostname());
      }
    }
    Port port = nb.getPort();
    if (!port.equals(Server.DEFAULT_PORT)) {
      jdbcUrl
          .append(':')
          .append(port.getPort());
    }
    jdbcUrl.append('/');
    URIEncoder.encodeURIComponent(getName().toString(), jdbcUrl);
    return jdbcUrl.toString();
  }

  @Override
  public String getJdbcDocumentationUrl() throws SQLException, IOException {
    final String list = "https://jdbc.postgresql.org/documentation/documentation.html";
    final String head = "https://jdbc.postgresql.org/documentation/head/index.html";
    String version = getPostgresServer().getVersion().getTechnologyVersion(table.getConnector()).getVersion();
    List<String> split = Strings.split(version, '.');
    if (split.size() < 2) {
      return list;
    } else {
      String major = split.get(0);
      String minor = split.get(1);
      if ("7".equals(major)) {
        return "https://www.postgresql.org/docs/" + URIEncoder.encodeURIComponent(major) + "." + URIEncoder.encodeURIComponent(minor) + "/jdbc.html";
      }
      if (
          "8".equals(major)
              || (
              "9".equals(major)
                  && (
                  "0".equals(minor)
                      || "1".equals(minor)
                      || "2".equals(minor)
                      || "3".equals(minor)
                      || "4".equals(minor)
              )
            )
      ) {
        return "https://jdbc.postgresql.org/documentation/" + URIEncoder.encodeURIComponent(major) + URIEncoder.encodeURIComponent(minor) + "/index.html";
      }
      return head;
    }
  }

  public Name getName() {
    return name;
  }

  public boolean isSpecial() {
    return isSpecial(name);
  }

  public Encoding getPostgresEncoding() throws SQLException, IOException {
    Encoding obj = table.getConnector().getPostgresql().getEncoding().get(encoding);
    if (obj == null) {
      throw new SQLException("Unable to find PostgresEncoding: " + encoding);
    }
    // Make sure the postgres encoding postgresql version matches the server this database is part of
    if (
        obj.getPostgresVersion(table.getConnector()).getPkey()
            != getPostgresServer().getVersion().getPkey()
    ) {
      throw new SQLException("encoding/postgres server version mismatch on PostgresDatabase: #" + pkey);
    }

    return obj;
  }

  public int getPostgresServer_bind_id() {
    return postgresServer;
  }

  public Server getPostgresServer() throws SQLException, IOException {
    Server obj = table.getConnector().getPostgresql().getServer().get(postgresServer);
    if (obj == null) {
      throw new SQLException("Unable to find PostgresServer: " + postgresServer);
    }
    return obj;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.POSTGRES_DATABASES;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    try {
      pkey = result.getInt(1);
      name = Name.valueOf(result.getString(2));
      postgresServer = result.getInt(3);
      datdba = result.getInt(4);
      encoding = result.getInt(5);
      isTemplate = result.getBoolean(6);
      allowConn = result.getBoolean(7);
      enablePostgis = result.getBoolean(8);
    } catch (ValidationException e) {
      throw new SQLException(e);
    }
  }

  public boolean isTemplate() {
    return isTemplate;
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    try {
      pkey = in.readCompressedInt();
      name = Name.valueOf(in.readUTF());
      postgresServer = in.readCompressedInt();
      datdba = in.readCompressedInt();
      encoding = in.readCompressedInt();
      isTemplate = in.readBoolean();
      allowConn = in.readBoolean();
      enablePostgis = in.readBoolean();
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  @Override
  public List<CannotRemoveReason<Database>> getCannotRemoveReasons() throws SQLException, IOException {
    List<CannotRemoveReason<Database>> reasons = new ArrayList<>();

    Server ps = getPostgresServer();
    if (!allowConn) {
      reasons.add(new CannotRemoveReason<>("Not allowed to drop a PostgreSQL database that does not allow connections: "
          + name + " on " + ps.getName() + " on " + ps.getLinuxServer().getHostname(), this));
    }
    if (isTemplate) {
      reasons.add(new CannotRemoveReason<>("Not allowed to drop a template PostgreSQL database: " + name + " on "
          + ps.getName() + " on " + ps.getLinuxServer().getHostname(), this));
    }
    if (isSpecial()) {
      reasons.add(
          new CannotRemoveReason<>(
              "Not allowed to drop a special PostgreSQL database: "
                  + name
                  + " on "
                  + ps.getName()
                  + " on "
                  + ps.getLinuxServer().getHostname(),
              this
          )
      );
    }

    return reasons;
  }

  @Override
  public void remove() throws IOException, SQLException {
    if (isSpecial()) {
      throw new SQLException("Refusing to remove special PostgreSQL database: " + this);
    }
    table.getConnector().requestUpdateInvalidating(
        true,
        AoservProtocol.CommandId.REMOVE,
        Table.TableId.POSTGRES_DATABASES,
        pkey
    );
  }

  @Override
  public String toStringImpl() {
    return name.toString();
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeUTF(name.toString());
    out.writeCompressedInt(postgresServer);
    out.writeCompressedInt(datdba);
    out.writeCompressedInt(encoding);
    out.writeBoolean(isTemplate);
    out.writeBoolean(allowConn);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_30) <= 0) {
      out.writeShort(0);
      out.writeShort(7);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_27) >= 0) {
      out.writeBoolean(enablePostgis);
    }
  }
}
