/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2002-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

import com.aoapps.collections.AoCollections;
import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.dto.DtoFactory;
import com.aoapps.lang.i18n.Resources;
import com.aoapps.lang.util.Internable;
import com.aoapps.lang.validation.InvalidResult;
import com.aoapps.lang.validation.ValidResult;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.lang.validation.ValidationResult;
import com.aoapps.net.Port;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.linux.PosixPath;
import com.aoindustries.aoserv.client.net.Bind;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A <code>PostgresServer</code> corresponds to a unique PostgreSQL install
 * space on one server.  The server name must be unique per server.
 * <code>PostgresDatabase</code>s and <code>PostgresServerUser</code>s are
 * unique per <code>PostgresServer</code>.
 *
 * @see  Version
 * @see  Database
 * @see  UserServer
 *
 * @author  AO Industries, Inc.
 */
public final class Server extends CachedObjectIntegerKey<Server> {

  private static final Resources RESOURCES = Resources.getResources(ResourceBundle::getBundle, Server.class);

  /**
   * Represents a name that may be used for a PostgreSQL installation.  Names must:
   * <ul>
   *   <li>Be non-null</li>
   *   <li>Be non-empty</li>
   *   <li>Be between 1 and 31 characters</li>
   *   <li>Must start with <code>[a-z,0-9]</code></li>
   *   <li>The rest of the characters may contain [a-z], [0-9], period (.), hyphen (-), and underscore (_)</li>
   * </ul>
   *
   * @author  AO Industries, Inc.
   */
  public static final class Name implements
      Comparable<Name>,
      Serializable,
      DtoFactory<com.aoindustries.aoserv.client.dto.PostgresServerName>,
      Internable<Name>
  {

    private static final long serialVersionUID = 7935268259991524802L;

    public static final int MAX_LENGTH = 255;

    /**
     * Validates a PostgreSQL server name.
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

      // The first character must be [a-z] or [0-9]
      char ch = name.charAt(0);
      if (
          (ch < 'a' || ch > 'z')
              && (ch < '0' || ch > '9')
      ) {
        return new InvalidResult(RESOURCES, "Name.validate.startAtoZor0to9");
      }

      // The rest may have additional characters
      for (int c = 1; c < len; c++) {
        ch = name.charAt(c);
        if (
            (ch < 'a' || ch > 'z')
                && (ch < '0' || ch > '9')
                && ch != '.'
                && ch != '-'
                && ch != '_'
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
              && name.equals(((Name) obj).name)
      ;
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
    public com.aoindustries.aoserv.client.dto.PostgresServerName getDto() {
      return new com.aoindustries.aoserv.client.dto.PostgresServerName(name);
    }
  }

  // <editor-fold defaultstate="collapsed" desc="Constants">
  /**
   * The default PostSQL port.
   */
  public static final Port DEFAULT_PORT;

  static {
    try {
      DEFAULT_PORT = Port.valueOf(5432, com.aoapps.net.Protocol.TCP);
    } catch (ValidationException e) {
      throw new AssertionError("These hard-coded values are valid", e);
    }
  }

  /**
   * The directory that contains the PostgreSQL data files.
   */
  public static final PosixPath DATA_BASE_DIR;

  static {
    try {
      DATA_BASE_DIR = PosixPath.valueOf("/var/lib/pgsql");
    } catch (ValidationException e) {
      throw new AssertionError("These hard-coded values are valid", e);
    }
  }

  /**
   * @deprecated 2019-07-14: Is this still used?
   */
  @Deprecated
  // TODO: Move to top-level class in schema, add to SQL implementation
  public enum ReservedWord {
    ABORT,
    ALL,
    ANALYSE,
    ANALYZE,
    AND,
    ANY,
    AS,
    ASC,
    BETWEEN,
    BINARY,
    BIT,
    BOTH,
    CASE,
    CAST,
    CHAR,
    CHARACTER,
    CHECK,
    CLUSTER,
    COALESCE,
    COLLATE,
    COLUMN,
    CONSTRAINT,
    COPY,
    CROSS,
    CURRENT_DATE,
    CURRENT_TIME,
    CURRENT_TIMESTAMP,
    CURRENT_USER,
    DEC,
    DECIMAL,
    DEFAULT,
    DEFERRABLE,
    DESC,
    DISTINCT,
    DO,
    ELSE,
    END,
    EXCEPT,
    EXISTS,
    EXPLAIN,
    EXTEND,
    EXTRACT,
    FALSE,
    FLOAT,
    FOR,
    FOREIGN,
    FROM,
    FULL,
    GLOBAL,
    GROUP,
    HAVING,
    ILIKE,
    IN,
    INITIALLY,
    INNER,
    INOUT,
    INTERSECT,
    INTO,
    IS,
    ISNULL,
    JOIN,
    LEADING,
    LEFT,
    LIKE,
    LIMIT,
    LISTEN,
    LOAD,
    LOCAL,
    LOCK,
    MOVE,
    NATURAL,
    NCHAR,
    NEW,
    NOT,
    NOTNULL,
    NULL,
    NULLIF,
    NUMERIC,
    OFF,
    OFFSET,
    OLD,
    ON,
    ONLY,
    OR,
    ORDER,
    OUT,
    OUTER,
    OVERLAPS,
    POSITION,
    PRECISION,
    PRIMARY,
    PUBLIC,
    REFERENCES,
    RESET,
    RIGHT,
    SELECT,
    SESSION_USER,
    SETOF,
    SHOW,
    SOME,
    SUBSTRING,
    TABLE,
    THEN,
    TO,
    TRAILING,
    TRANSACTION,
    TRIM,
    TRUE,
    UNION,
    UNIQUE,
    USER,
    USING,
    VACUUM,
    VARCHAR,
    VERBOSE,
    WHEN,
    WHERE;

    private static volatile Set<String> reservedWords = null;

    /**
     * Case-insensitive check for if the provided string is a reserved word.
     */
    public static boolean isReservedWord(String value) {
      Set<String> words = reservedWords;
      if (words == null) {
        ReservedWord[] values = values();
        words = AoCollections.newHashSet(values.length);
        for (ReservedWord word : values) {
          words.add(word.name().toUpperCase(Locale.ROOT));
        }
        reservedWords = words;
      }
      return words.contains(value.toUpperCase(Locale.ROOT));
    }
  }
  // </editor-fold>

  static final int
      COLUMN_BIND = 0,
      COLUMN_AO_SERVER = 2
  ;
  static final String COLUMN_NAME_name = "name";
  static final String COLUMN_AO_SERVER_name = "ao_server";

  private Name name;
  private int ao_server;
  private int version;
  private int max_connections;
  private int sort_mem;
  private int shared_buffers;
  private boolean fsync;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated // Java 9: (forRemoval = true)
  public Server() {
    // Do nothing
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_BIND: return pkey;
      case 1: return name;
      case COLUMN_AO_SERVER: return ao_server;
      case 3: return version;
      case 4: return max_connections;
      case 5: return sort_mem;
      case 6: return shared_buffers;
      case 7: return fsync;
      default: throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public int getBind_id() {
    return pkey;
  }

  public Bind getBind() throws SQLException, IOException {
    Bind nb = table.getConnector().getNet().getBind().get(pkey);
    if (nb == null) {
      throw new SQLException("Unable to find NetBind: " + pkey);
    }
    return nb;
  }

  public int getAoServer_server_pkey() {
    return ao_server;
  }

  public com.aoindustries.aoserv.client.linux.Server getLinuxServer() throws SQLException, IOException {
    com.aoindustries.aoserv.client.linux.Server ao = table.getConnector().getLinux().getServer().get(ao_server);
    if (ao == null) {
      throw new SQLException("Unable to find linux.Server: " + ao_server);
    }
    return ao;
  }

  public int getVersion_version_id() {
    return version;
  }

  public Version getVersion() throws SQLException, IOException {
    Version obj = table.getConnector().getPostgresql().getVersion().get(version);
    if (obj == null) {
      throw new SQLException("Unable to find PostgresVersion: " + version);
    }
    if (
        obj.getTechnologyVersion(table.getConnector()).getOperatingSystemVersion(table.getConnector()).getPkey()
            != getLinuxServer().getHost().getOperatingSystemVersion_id()
    ) {
      throw new SQLException("resource/operating system version mismatch on PostgresServer: #" + pkey);
    }
    return obj;
  }

  public int getMaxConnections() {
    return max_connections;
  }

  public int getSortMem() {
    return sort_mem;
  }

  public int getSharedBuffers() {
    return shared_buffers;
  }

  public boolean getFSync() {
    return fsync;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    try {
      int pos = 1;
      pkey = result.getInt(pos++);
      name = Name.valueOf(result.getString(pos++));
      ao_server = result.getInt(pos++);
      version = result.getInt(pos++);
      max_connections = result.getInt(pos++);
      sort_mem = result.getInt(pos++);
      shared_buffers = result.getInt(pos++);
      fsync = result.getBoolean(pos++);
    } catch (ValidationException e) {
      throw new SQLException(e);
    }
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    try {
      pkey = in.readCompressedInt();
      name = Name.valueOf(in.readUTF()).intern();
      ao_server = in.readCompressedInt();
      version = in.readCompressedInt();
      max_connections = in.readCompressedInt();
      sort_mem = in.readCompressedInt();
      shared_buffers = in.readCompressedInt();
      fsync = in.readBoolean();
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeUTF(name.toString());
    out.writeCompressedInt(ao_server);
    out.writeCompressedInt(version);
    out.writeCompressedInt(max_connections);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_81_17) <= 0) {
      out.writeCompressedInt(pkey); // net_bind
    }
    out.writeCompressedInt(sort_mem);
    out.writeCompressedInt(shared_buffers);
    out.writeBoolean(fsync);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_130) <= 0) {
      out.writeCompressedInt(-1);
    }
  }

  @Override
  public Table.TableID getTableID() {
    return Table.TableID.POSTGRES_SERVERS;
  }

  @Override
  public String toStringImpl() throws SQLException, IOException {
    return name + " on " + getLinuxServer().getHostname();
  }

  public int addPostgresDatabase(
      Database.Name name,
      UserServer datdba,
      Encoding encoding,
      boolean enablePostgis
  ) throws IOException, SQLException {
    return table.getConnector().getPostgresql().getDatabase().addPostgresDatabase(
        name,
        this,
        datdba,
        encoding,
        enablePostgis
    );
  }

  public PosixPath getDataDirectory() {
    try {
      return PosixPath.valueOf(DATA_BASE_DIR.toString() + '/' + name.toString());
    } catch (ValidationException e) {
      throw new AssertionError("Generated data directory should always be valid", e);
    }
  }

  public Name getName() {
    return name;
  }

  public Database getPostgresDatabase(Database.Name name) throws IOException, SQLException {
    return table.getConnector().getPostgresql().getDatabase().getPostgresDatabase(name, this);
  }

  public List<Database> getPostgresDatabases() throws IOException, SQLException {
    return table.getConnector().getPostgresql().getDatabase().getPostgresDatabases(this);
  }

  public UserServer getPostgresServerUser(User.Name username) throws IOException, SQLException {
    return table.getConnector().getPostgresql().getUserServer().getPostgresServerUser(username, this);
  }

  public List<UserServer> getPostgresServerUsers() throws IOException, SQLException {
    return table.getConnector().getPostgresql().getUserServer().getPostgresServerUsers(this);
  }

  public List<User> getPostgresUsers() throws SQLException, IOException {
    List<UserServer> psu = getPostgresServerUsers();
    int len = psu.size();
    List<User> pu = new ArrayList<>(len);
    for (int c = 0; c < len; c++) {
      pu.add(psu.get(c).getPostgresUser());
    }
    return pu;
  }

  public boolean isPostgresDatabaseNameAvailable(Database.Name name) throws IOException, SQLException {
    return table.getConnector().getPostgresql().getDatabase().isPostgresDatabaseNameAvailable(name, this);
  }

  public void restartPostgreSQL() throws IOException, SQLException {
    table.getConnector().requestUpdate(false, AoservProtocol.CommandID.RESTART_POSTGRESQL, pkey);
  }

  public void startPostgreSQL() throws IOException, SQLException {
    table.getConnector().requestUpdate(false, AoservProtocol.CommandID.START_POSTGRESQL, pkey);
  }

  public void stopPostgreSQL() throws IOException, SQLException {
    table.getConnector().requestUpdate(false, AoservProtocol.CommandID.STOP_POSTGRESQL, pkey);
  }
}
