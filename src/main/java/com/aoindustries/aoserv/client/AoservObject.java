/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2024, 2025, 2026  AO Industries, Inc.
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

import com.aoapps.hodgepodge.io.stream.Streamable;
import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.hodgepodge.table.Row;
import com.aoapps.lang.dto.DtoFactory;
import com.aoapps.lang.exception.WrappedException;
import com.aoapps.lang.util.ComparatorUtils;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.net.DomainLabel;
import com.aoapps.net.DomainLabels;
import com.aoapps.net.DomainName;
import com.aoapps.net.Email;
import com.aoapps.net.HostAddress;
import com.aoapps.net.InetAddress;
import com.aoapps.net.MacAddress;
import com.aoapps.security.HashedKey;
import com.aoapps.security.HashedPassword;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.account.User;
import com.aoindustries.aoserv.client.linux.Group;
import com.aoindustries.aoserv.client.linux.PosixPath;
import com.aoindustries.aoserv.client.linux.User.Gecos;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.schema.Type;
import com.aoindustries.aoserv.client.sql.SqlExpression;
import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * An <code>AoservObject</code> is the lowest level object
 * for all data in the system.  Each <code>AoservObject</code>
 * belongs to a <code>AoservTable</code>, and each table
 * contains <code>AoservObject</code>s.
 *
 * @author  AO Industries, Inc.
 *
 * @see  AoservTable
 */
public abstract class AoservObject<K, T extends AoservObject<K, T>> implements Row, AoservStreamable {

  /**
   * Enables the use of {@link SQLData}.  This currently requires our forked PostgreSQL JDBC driver to
   * function correctly.  See <a href="https://github.com/pgjdbc/pgjdbc/issues/641">Issue #641</a> for more
   * details.
   */
  public static final boolean USE_SQL_DATA = false;

  /**
   * Enables the use of {@link SQLData} on writeObject.  This currently requires our forked PostgreSQL JDBC driver to
   * function correctly.  See <a href="https://github.com/pgjdbc/pgjdbc/pull/1377">Pull request #1377</a> for more
   * details.
   */
  public static final boolean USE_SQL_DATA_WRITE = false;

  /**
   * Enables the use of arrays of domains.  This is currently not supported by PostgreSQL.
   */
  public static final boolean USE_ARRAY_OF_DOMAIN = false;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  AoservObject#init(java.sql.ResultSet)
   * @see  AoservObject#read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  protected AoservObject() {
    // Do nothing
  }

  // <editor-fold defaultstate="collapsed" desc="Ordering">
  /**
   * @deprecated  This method moved to {@link ComparatorUtils#compareIgnoreCaseConsistentWithEquals(java.lang.String, java.lang.String)}
   */
  @Deprecated
  public static int compareIgnoreCaseConsistentWithEquals(String s1, String s2) {
    return ComparatorUtils.compareIgnoreCaseConsistentWithEquals(s1, s2);
  }

  /**
   * @deprecated  This method moved to {@link ComparatorUtils#compare(int, int)}
   */
  @Deprecated
  public static int compare(int i1, int i2) {
    return ComparatorUtils.compare(i1, i2);
  }

  /**
   * @deprecated  This method moved to {@link ComparatorUtils#compare(short, short)}
   */
  @Deprecated
  public static int compare(short s1, short s2) {
    return ComparatorUtils.compare(s1, s2);
  }

  /**
   * @deprecated  This method moved to {@link ComparatorUtils#compare(long, long)}
   */
  @Deprecated
  public static int compare(long l1, long l2) {
    return ComparatorUtils.compare(l1, l2);
  }

  /**
   * Compares two objects allowing for nulls, sorts non-null before null.
   */
  public static <T extends Comparable<T>> int compare(T obj1, T obj2) {
    if (obj1 != null) {
      return obj2 != null ? obj1.compareTo(obj2) : -1;
    } else {
      return obj2 != null ? 1 : 0;
    }
  }

  // TODO: Remove in AOServ 2
  @SuppressWarnings("BroadCatchBlock")
  public final int compareTo(
      AoservConnector conn,
      AoservObject<?, ?> other,
      SqlExpression[] sortExpressions,
      boolean[] sortOrders
  ) throws IllegalArgumentException, SQLException, UnknownHostException, IOException {
    int len = sortExpressions.length;
    for (int c = 0; c < len; c++) {
      SqlExpression expr = sortExpressions[c];
      Type type = expr.getType();
      Object value1 = null;
      boolean value1Set = false;
      Object value2 = null;
      boolean value2Set = false;
      try {
        value1 = expr.evaluate(conn, this);
        value1Set = true;
        value2 = expr.evaluate(conn, other);
        value2Set = true;
        int diff = type.compare(value1, value2);
        if (diff != 0) {
          return sortOrders[c] ? diff : -diff;
        }
      } catch (RuntimeException e) {
        String getString1;
        if (!value1Set) {
          getString1 = "<unset>";
        } else if (value1 == null) {
          getString1 = "[NULL]";
        } else {
          try {
            getString1 = type.getString(value1, -1);
          } catch (RuntimeException e2) {
            getString1 = value1.toString();
          }
        }
        String getString2;
        if (!value2Set) {
          getString2 = "<unset>";
        } else if (value2 == null) {
          getString2 = "[NULL]";
        } else {
          try {
            getString2 = type.getString(value2, -1);
          } catch (RuntimeException e2) {
            getString2 = value2.toString();
          }
        }
        throw new SQLException(
            "expr......: " + expr + "\n"
                + "type......: " + type + "\n"
                + "this......: " + this + "\n"
                + "other.....: " + other + "\n"
                + "thisKey...: " + this.getKey() + "\n"
                + "otherKey..: " + other.getKey() + "\n"
                + "thisValue.: " + getString1 + "\n"
                + "otherValue: " + getString2,
            e
        );
      }
    }
    return 0;
  }

  // TODO: Remove in AOServ 2
  public final int compareTo(
      AoservConnector conn,
      Comparable<?> value,
      SqlExpression[] sortExpressions,
      boolean[] sortOrders
  ) throws IllegalArgumentException, SQLException, UnknownHostException, IOException {
    int len = sortExpressions.length;
    for (int c = 0; c < len; c++) {
      SqlExpression expr = sortExpressions[c];
      Type type = expr.getType();
      int diff = type.compare(
          expr.evaluate(conn, this),
          value
      );
      if (diff != 0) {
        return sortOrders[c] ? diff : -diff;
      }
    }
    return 0;
  }

  // TODO: Remove in AOServ 2
  public final int compareTo(
      AoservConnector conn,
      Object[] objects,
      SqlExpression[] sortExpressions,
      boolean[] sortOrders
  ) throws IllegalArgumentException, SQLException, UnknownHostException, IOException {
    int len = sortExpressions.length;
    if (len != objects.length) {
      throw new IllegalArgumentException("Array length mismatch when comparing AoservObject to Object[]: sortExpressions.length=" + len + ", objects.length=" + objects.length);
    }

    for (int c = 0; c < len; c++) {
      SqlExpression expr = sortExpressions[c];
      Type type = expr.getType();
      int diff = type.compare(
          expr.evaluate(conn, this),
          objects[c]
      );
      if (diff != 0) {
        return sortOrders[c] ? diff : -diff;
      }
    }
    return 0;
  }

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="DTO">
  /**
   * null-safe getDto.
   */
  protected static <B> B getDto(DtoFactory<B> dtoFactory) {
    return dtoFactory == null ? null : dtoFactory.getDto();
  }

  /**
   * null-safe accounting code conversion.
   */
  protected static Account.Name getAccountingCode(com.aoindustries.aoserv.client.dto.AccountName accounting) throws ValidationException {
    if (accounting == null) {
      return null;
    }
    return Account.Name.valueOf(accounting.getAccounting());
  }

  /**
   * null-safe domain label conversion.
   */
  protected static DomainLabel getDomainLabel(com.aoapps.net.dto.DomainLabel domainLabel) throws ValidationException {
    if (domainLabel == null) {
      return null;
    }
    return DomainLabel.valueOf(domainLabel.getLabel());
  }

  // /**
  //  * null-safe conversion from java.util.Date to java.sql.Date, will cast when possible.
  //  */
  // protected static java.sql.Date getDate(Date date) {
  //   if (date == null) {
  // return null;
  //   if (date instanceof java.sql.Date) {
  // return (java.sql.Date)date;
  //   return new java.sql.Date(date.getTime());
  // }

  /**
   * null-safe domain labels conversion.
   */
  protected static DomainLabels getDomainLabels(com.aoapps.net.dto.DomainLabels domainLabels) throws ValidationException {
    if (domainLabels == null) {
      return null;
    }
    return DomainLabels.valueOf(domainLabels.getLabels());
  }

  /**
   * null-safe domain name conversion.
   */
  protected static DomainName getDomainName(com.aoapps.net.dto.DomainName domainName) throws ValidationException {
    if (domainName == null) {
      return null;
    }
    return DomainName.valueOf(domainName.getDomain());
  }

  /**
   * null-safe email conversion.
   */
  protected static Email getEmail(com.aoapps.net.dto.Email email) throws ValidationException {
    if (email == null) {
      return null;
    }
    return Email.valueOf(email.getLocalPart(), DomainName.valueOf(email.getDomain().getDomain()));
  }

  /**
   * null-safe GECOS conversion.
   */
  protected static Gecos getGecos(com.aoindustries.aoserv.client.dto.Gecos gecos) throws ValidationException {
    if (gecos == null) {
      return null;
    }
    return Gecos.valueOf(gecos.getValue());
  }

  /**
   * null-safe group id conversion.
   */
  protected static Group.Name getGroupId(com.aoindustries.aoserv.client.dto.LinuxGroupName gid) throws ValidationException {
    if (gid == null) {
      return null;
    }
    return Group.Name.valueOf(gid.getName());
  }

  /**
   * null-safe hashed key conversion.
   */
  protected static HashedKey getHashedKey(com.aoindustries.aoserv.client.dto.HashedKey hashedKey) throws IllegalArgumentException {
    if (hashedKey == null) {
      return null;
    }
    return HashedKey.valueOf(hashedKey.getHashedKey());
  }

  /**
   * null-safe hashed password conversion.
   */
  protected static HashedPassword getHashedPassword(com.aoindustries.aoserv.client.dto.HashedPassword hashedPassword) throws IllegalArgumentException {
    if (hashedPassword == null) {
      return null;
    }
    return HashedPassword.valueOf(hashedPassword.getHashedPassword());
  }

  /**
   * null-safe hostname conversion.
   */
  protected static HostAddress getHostname(com.aoapps.net.dto.HostAddress hostname) throws ValidationException {
    if (hostname == null) {
      return null;
    }
    return HostAddress.valueOf(hostname.getAddress());
  }

  /**
   * null-safe inet address conversion.
   */
  protected static InetAddress getInetAddress(com.aoapps.net.dto.InetAddress inetAddress) throws ValidationException {
    if (inetAddress == null) {
      return null;
    }
    return InetAddress.valueOf(inetAddress.getAddress());
  }

  /**
   * null-safe Linux id conversion.
   */
  protected static com.aoindustries.aoserv.client.linux.LinuxId getLinuxId(com.aoindustries.aoserv.client.dto.LinuxId lid) throws ValidationException {
    if (lid == null) {
      return null;
    }
    return com.aoindustries.aoserv.client.linux.LinuxId.valueOf(lid.getId());
  }

  /**
   * null-safe Linux user name conversion.
   */
  protected static com.aoindustries.aoserv.client.linux.User.Name getLinuxUserName(com.aoindustries.aoserv.client.dto.LinuxUserName linuxUserName) throws ValidationException {
    if (linuxUserName == null) {
      return null;
    }
    return com.aoindustries.aoserv.client.linux.User.Name.valueOf(linuxUserName.getName());
  }

  /**
   * null-safe MAC address conversion.
   */
  protected static MacAddress getMacAddress(com.aoapps.net.dto.MacAddress macAddress) throws ValidationException {
    if (macAddress == null) {
      return null;
    }
    return MacAddress.valueOf(macAddress.getAddress());
  }

  /**
   * null-safe MySQL database name conversion.
   */
  protected static com.aoindustries.aoserv.client.mysql.Database.Name getMysqlDatabaseName(com.aoindustries.aoserv.client.dto.MysqlDatabaseName databaseName) throws ValidationException {
    if (databaseName == null) {
      return null;
    }
    return com.aoindustries.aoserv.client.mysql.Database.Name.valueOf(databaseName.getName());
  }

  /**
   * null-safe MySQL server name conversion.
   */
  protected static com.aoindustries.aoserv.client.mysql.Server.Name getMysqlServerName(com.aoindustries.aoserv.client.dto.MysqlServerName serverName) throws ValidationException {
    if (serverName == null) {
      return null;
    }
    return com.aoindustries.aoserv.client.mysql.Server.Name.valueOf(serverName.getName());
  }

  /**
   * null-safe MySQL user name conversion.
   */
  // TODO: Move this, and others to schemas?
  protected static com.aoindustries.aoserv.client.mysql.User.Name getMysqlUserName(com.aoindustries.aoserv.client.dto.MysqlUserName mysqlUserId) throws ValidationException {
    if (mysqlUserId == null) {
      return null;
    }
    return com.aoindustries.aoserv.client.mysql.User.Name.valueOf(mysqlUserId.getName());
  }

  /**
   * null-safe port conversion.
   */
  protected static com.aoapps.net.Port getPort(com.aoapps.net.dto.Port port) throws ValidationException {
    if (port == null) {
      return null;
    }
    return com.aoapps.net.Port.valueOf(
        port.getPort(),
        com.aoapps.net.Protocol.valueOf(port.getProtocol())
    );
  }

  /**
   * null-safe PostgreSQL database name conversion.
   */
  protected static com.aoindustries.aoserv.client.postgresql.Database.Name getPostgresDatabaseName(com.aoindustries.aoserv.client.dto.PostgresDatabaseName databaseName) throws ValidationException {
    if (databaseName == null) {
      return null;
    }
    return com.aoindustries.aoserv.client.postgresql.Database.Name.valueOf(databaseName.getName());
  }

  /**
   * null-safe PostgreSQL server name conversion.
   */
  protected static com.aoindustries.aoserv.client.postgresql.Server.Name getPostgresServerName(com.aoindustries.aoserv.client.dto.PostgresServerName serverName) throws ValidationException {
    if (serverName == null) {
      return null;
    }
    return com.aoindustries.aoserv.client.postgresql.Server.Name.valueOf(serverName.getName());
  }

  /**
   * null-safe PostgreSQL user id conversion.
   */
  protected static com.aoindustries.aoserv.client.postgresql.User.Name getPostgresUserId(com.aoindustries.aoserv.client.dto.PostgresUserName postgresUserId) throws ValidationException {
    if (postgresUserId == null) {
      return null;
    }
    return com.aoindustries.aoserv.client.postgresql.User.Name.valueOf(postgresUserId.getName());
  }

  /**
   * null-safe conversion from Date to Long.
   */
  protected static Long getTimeMillis(Date date) {
    if (date == null) {
      return null;
    }
    return date.getTime();
  }

  /**
   * null-safe conversion from Calendar to Long.
   */
  protected static Long getTimeMillis(Calendar datetime) {
    if (datetime == null) {
      return null;
    }
    return datetime.getTimeInMillis();
  }

  /**
   * null-safe Unix path conversion.
   */
  protected static PosixPath getUnixPath(com.aoindustries.aoserv.client.dto.PosixPath unixPath) throws ValidationException {
    if (unixPath == null) {
      return null;
    }
    return PosixPath.valueOf(unixPath.getPath());
  }

  /**
   * null-safe user id conversion.
   */
  protected static User.Name getUserId(com.aoindustries.aoserv.client.dto.UserName userId) throws ValidationException {
    if (userId == null) {
      return null;
    }
    return User.Name.valueOf(userId.getName());
  }

  // </editor-fold>

  /**
   * {@inheritDoc}
   *
   * <p><b>Implementation Note:</b><br>
   * This default implementation considers the object equal when it is the same class
   * (via {@link AoservObject#getClass()} and has equal keys (via {@link AoservObject#getKey()}).</p>
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    Class<?> class1 = getClass();
    Class<?> class2 = obj.getClass();
    if (class1 == class2) {
      K pkey1 = getKey();
      Object pkey2 = ((AoservObject) obj).getKey();
      if (pkey1 == null || pkey2 == null) {
        throw new NullPointerException("No primary key available.");
      }
      return pkey1.equals(pkey2);
    }
    return false;
  }

  @Override
  // TODO: 'tis a shame we have this duality just to catch exceptions.
  // TODO: Would it be better to allow exceptions through the interface this implements?
  // TODO: It could be generic exceptions, like we've done other places (a bit tricky but works, except doesn't interact well with lambdas).
  public final Object getColumn(int i) {
    try {
      return getColumnImpl(i);
    } catch (IOException | SQLException err) {
      throw new WrappedException(err);
    }
  }

  protected abstract Object getColumnImpl(int i) throws IOException, SQLException;

  public final List<Object> getColumns(AoservConnector connector) throws IOException, SQLException {
    int len = getTableSchema(connector).getSchemaColumns(connector).size();
    List<Object> buff = new ArrayList<>(len);
    for (int c = 0; c < len; c++) {
      buff.add(getColumn(c));
    }
    return buff;
  }

  public final int getColumns(AoservConnector connector, List<Object> buff) throws IOException, SQLException {
    int len = getTableSchema(connector).getSchemaColumns(connector).size();
    for (int c = 0; c < len; c++) {
      buff.add(getColumn(c));
    }
    return len;
  }

  public abstract K getKey();

  public abstract Table.TableId getTableId();

  public final Table getTableSchema(AoservConnector connector) throws IOException, SQLException {
    return connector.getSchema().getTable().get(getTableId());
  }

  /**
   * {@inheritDoc}
   *
   * <p><b>Implementation Note:</b><br>
   * This default implementation calls {@link AoservObject#hashCode()} on the key (from {@link AoservObject#getKey()}).</p>
   */
  @Override
  public int hashCode() {
    K pkey = getKey();
    if (pkey == null) {
      throw new NullPointerException("No primary key available.");
    }
    return pkey.hashCode();
  }

  /**
   * Initializes this object from the raw database contents.
   *
   * @param  results  the <code>ResultSet</code> containing the row
   *                  to copy into this object
   */
  public abstract void init(ResultSet results) throws SQLException;

  /**
   * {@inheritDoc}
   *
   * @deprecated  This is maintained only for compatibility with the {@link Streamable} interface.
   *
   * @see  AoservObject#read(StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated
  @Override
  public final void read(StreamableInput in, String protocolVersion) throws IOException {
    read(in, AoservProtocol.Version.getVersion(protocolVersion));
  }

  @Override
  public abstract void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException;

  /**
   * {@inheritDoc}
   *
   * <p><b>Implementation Note:</b><br>
   * This default implementation calls {@link AoservObject#toStringImpl()}, which
   * is allowed to throw exceptions.</p>
   *
   * @see  AoservObject#toStringImpl()
   *
   * @throws  WrappedException when {@link AoservObject#toStringImpl()} throws an exception
   */
  @Override
  public final String toString() {
    try {
      return toStringImpl();
    } catch (IOException | SQLException err) {
      throw new WrappedException(err);
    }
  }

  /**
   * {@link AoservObject#toString()} implementation that is allowed to throw exceptions.
   *
   * <p><b>Implementation Note:</b><br>
   * This default implementation calls {@link AoservObject#toString()} on the key (from {@link AoservObject#getKey()}).
   * When the key is {@code null}, uses the default implementation from {@link Object#toString()}.</p>
   */
  public String toStringImpl() throws IOException, SQLException {
    K pkey = getKey();
    if (pkey == null) {
      return super.toString();
    }
    return pkey.toString();
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  This is maintained only for compatibility with the {@link Streamable} interface.
   *
   * @see  AoservObject#write(StreamableOutput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated
  @Override
  public final void write(StreamableOutput out, String protocolVersion) throws IOException {
    write(out, AoservProtocol.Version.getVersion(protocolVersion));
  }

  @Override
  public abstract void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException;
}
