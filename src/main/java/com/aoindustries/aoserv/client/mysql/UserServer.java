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

package com.aoindustries.aoserv.client.mysql;

import com.aoapps.collections.IntList;
import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.util.InternUtils;
import com.aoapps.lang.validation.ValidationException;
import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.CannotRemoveReason;
import com.aoindustries.aoserv.client.Disablable;
import com.aoindustries.aoserv.client.Removable;
import com.aoindustries.aoserv.client.account.DisableLog;
import com.aoindustries.aoserv.client.password.PasswordChecker;
import com.aoindustries.aoserv.client.password.PasswordProtected;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A <code>MysqlServerUser</code> grants a <code>MysqlUser</code> access
 * to a {@link Server}.  Once access is granted to the <code>Server</code>,
 * access may then be granted to individual <code>MysqlDatabase</code>s via
 * <code>MysqlDbUser</code>s.
 *
 * @see  User
 * @see  Database
 * @see  DatabaseUser
 * @see  Server
 *
 * @author  AO Industries, Inc.
 */
public final class UserServer extends CachedObjectIntegerKey<UserServer> implements Removable, PasswordProtected, Disablable {

  static final int COLUMN_PKEY = 0;
  static final int COLUMN_USERNAME = 1;
  static final int COLUMN_MYSQL_SERVER = 2;
  static final String COLUMN_USERNAME_name = "username";
  static final String COLUMN_MYSQL_SERVER_name = "mysql_server";

  public static final int UNLIMITED_QUESTIONS = 0;
  public static final int DEFAULT_MAX_QUESTIONS = UNLIMITED_QUESTIONS;

  public static final int UNLIMITED_UPDATES = 0;
  public static final int DEFAULT_MAX_UPDATES = UNLIMITED_UPDATES;

  public static final int UNLIMITED_CONNECTIONS = 0;
  public static final int DEFAULT_MAX_CONNECTIONS = UNLIMITED_CONNECTIONS;

  public static final int UNLIMITED_USER_CONNECTIONS = 0;
  public static final int DEFAULT_MAX_USER_CONNECTIONS = UNLIMITED_USER_CONNECTIONS;

  public static final int MAX_HOST_LENGTH = 60;

  /**
   * Convenience constants for the most commonly used host values.
   */
  public static final String
      ANY_HOST = "%",
      ANY_LOCAL_HOST = null;

  private User.Name username;
  private int mysqlServer;
  private String host;
  private int disableLog;
  private String predisablePassword;
  private int maxQuestions;
  private int maxUpdates;
  private int maxConnections;
  private int maxUserConnections;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public UserServer() {
    // Do nothing
  }

  @Override
  public int arePasswordsSet() throws IOException, SQLException {
    if (isSpecial()) {
      throw new SQLException("Refusing to check if passwords set on special MySQL user: " + this);
    }
    return table.getConnector().requestBooleanQuery(true, AoservProtocol.CommandId.IS_MYSQL_SERVER_USER_PASSWORD_SET, pkey)
        ? PasswordProtected.ALL
        : PasswordProtected.NONE;
  }

  @Override
  public boolean canDisable() {
    return !isDisabled() && !isSpecial();
  }

  @Override
  public boolean canEnable() throws SQLException, IOException {
    if (isSpecial()) {
      return false;
    }
    DisableLog dl = getDisableLog();
    if (dl == null) {
      return false;
    } else {
      return dl.canEnable() && !getMysqlUser().isDisabled();
    }
  }

  @Override
  public List<PasswordChecker.Result> checkPassword(String password) throws IOException {
    return User.checkPassword(username, password);
  }

  /*
  public String checkPasswordDescribe(String password) {
    return MysqlUser.checkPasswordDescribe(username, password);
  }
  */
  @Override
  public void disable(DisableLog dl) throws IOException, SQLException {
    if (isSpecial()) {
      throw new SQLException("Refusing to disable special MySQL user: " + this);
    }
    table.getConnector().requestUpdateInvalidating(true, AoservProtocol.CommandId.DISABLE, Table.TableId.MYSQL_SERVER_USERS, dl.getPkey(), pkey);
  }

  @Override
  public void enable() throws IOException, SQLException {
    if (isSpecial()) {
      throw new SQLException("Refusing to enable special MySQL user: " + this);
    }
    table.getConnector().requestUpdateInvalidating(true, AoservProtocol.CommandId.ENABLE, Table.TableId.MYSQL_SERVER_USERS, pkey);
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_PKEY:
        return pkey;
      case COLUMN_USERNAME:
        return username;
      case COLUMN_MYSQL_SERVER:
        return mysqlServer;
      case 3:
        return host;
      case 4:
        return getDisableLog_id();
      case 5:
        return predisablePassword;
      case 6:
        return maxQuestions;
      case 7:
        return maxUpdates;
      case 8:
        return maxConnections;
      case 9:
        return maxUserConnections;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  @Override
  public boolean isDisabled() {
    return disableLog != -1;
  }

  public Integer getDisableLog_id() {
    return disableLog == -1 ? null : disableLog;
  }

  @Override
  public DisableLog getDisableLog() throws SQLException, IOException {
    if (disableLog == -1) {
      return null;
    }
    DisableLog obj = table.getConnector().getAccount().getDisableLog().get(disableLog);
    if (obj == null) {
      throw new SQLException("Unable to find DisableLog: " + disableLog);
    }
    return obj;
  }

  public String getHost() {
    return host;
  }

  public List<DatabaseUser> getMysqlDbUsers() throws IOException, SQLException {
    return table.getConnector().getMysql().getDatabaseUser().getMysqlDbUsers(this);
  }

  public User.Name getMysqlUser_username() {
    return username;
  }

  public User getMysqlUser() throws SQLException, IOException {
    User obj = table.getConnector().getMysql().getUser().get(username);
    if (obj == null) {
      throw new SQLException("Unable to find MysqlUser: " + username);
    }
    return obj;
  }

  public boolean isSpecial() {
    return User.isSpecial(username);
  }

  public String getPredisablePassword() {
    return predisablePassword;
  }

  public int getMaxQuestions() {
    return maxQuestions;
  }

  public int getMaxUpdates() {
    return maxUpdates;
  }

  public int getMaxConnections() {
    return maxConnections;
  }

  public int getMaxUserConnections() {
    return maxUserConnections;
  }

  public int getMysqlServer_id() {
    return mysqlServer;
  }

  public Server getMysqlServer() throws IOException, SQLException {
    // May be filtered
    return table.getConnector().getMysql().getServer().get(mysqlServer);
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.MYSQL_SERVER_USERS;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    try {
      pkey = result.getInt(1);
      username = User.Name.valueOf(result.getString(2));
      mysqlServer = result.getInt(3);
      host = result.getString(4);
      disableLog = result.getInt(5);
      if (result.wasNull()) {
        disableLog = -1;
      }
      predisablePassword = result.getString(6);
      maxQuestions = result.getInt(7);
      maxUpdates = result.getInt(8);
      maxConnections = result.getInt(9);
      maxUserConnections = result.getInt(10);
    } catch (ValidationException e) {
      throw new SQLException(e);
    }
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    try {
      pkey = in.readCompressedInt();
      username = User.Name.valueOf(in.readUTF()).intern();
      mysqlServer = in.readCompressedInt();
      host = InternUtils.intern(in.readNullUTF());
      disableLog = in.readCompressedInt();
      predisablePassword = in.readNullUTF();
      maxQuestions = in.readCompressedInt();
      maxUpdates = in.readCompressedInt();
      maxConnections = in.readCompressedInt();
      maxUserConnections = in.readCompressedInt();
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  @Override
  public List<CannotRemoveReason<UserServer>> getCannotRemoveReasons() throws SQLException, IOException {
    List<CannotRemoveReason<UserServer>> reasons = new ArrayList<>();
    if (isSpecial()) {
      Server ms = getMysqlServer();
      reasons.add(
          new CannotRemoveReason<>(
              "Not allowed to remove a special MySQL user: "
                  + username
                  + " on "
                  + ms.getName()
                  + " on "
                  + ms.getLinuxServer().getHostname(),
              this
          )
      );
    }
    return reasons;
  }

  @Override
  public void remove() throws IOException, SQLException {
    if (isSpecial()) {
      throw new SQLException("Refusing to remove special MySQL user: " + this);
    }
    table.getConnector().requestUpdateInvalidating(
        true,
        AoservProtocol.CommandId.REMOVE,
        Table.TableId.MYSQL_SERVER_USERS,
        pkey
    );
  }

  @Override
  public void setPassword(final String password) throws IOException, SQLException {
    if (isSpecial()) {
      throw new SQLException("Refusing to set the password for a special MySQL user: " + this);
    }

    AoservConnector connector = table.getConnector();
    if (!connector.isSecure()) {
      throw new IOException("Passwords for MySQL users may only be set when using secure protocols.  Currently using the " + connector.getProtocol() + " protocol, which is not secure.");
    }

    connector.requestUpdate(
        true,
        AoservProtocol.CommandId.SET_MYSQL_SERVER_USER_PASSWORD,
        new AoservConnector.UpdateRequest() {
          @Override
          public void writeRequest(StreamableOutput out) throws IOException {
            out.writeCompressedInt(pkey);
            out.writeNullUTF(password);
          }

          @Override
          public void readResponse(StreamableInput in) throws IOException, SQLException {
            int code = in.readByte();
            if (code != AoservProtocol.DONE) {
              AoservProtocol.checkResult(code, in);
              throw new IOException("Unexpected response code: " + code);
            }
          }

          @Override
          public void afterRelease() {
            // Do nothing
          }
        }
    );
  }

  public void setPredisablePassword(final String password) throws IOException, SQLException {
    if (isSpecial()) {
      throw new SQLException("May not disable special MySQL user: " + username);
    }
    table.getConnector().requestUpdate(
        true,
        AoservProtocol.CommandId.SET_MYSQL_SERVER_USER_PREDISABLE_PASSWORD,
        new AoservConnector.UpdateRequest() {
          private IntList invalidateList;

          @Override
          public void writeRequest(StreamableOutput out) throws IOException {
            out.writeCompressedInt(pkey);
            out.writeNullUTF(password);
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
  public String toStringImpl() throws IOException, SQLException {
    return username + " on " + getMysqlServer().toStringImpl();
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeUTF(username.toString());
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_4) < 0) {
      out.writeCompressedInt(-1);
    } else {
      out.writeCompressedInt(mysqlServer);
    }
    out.writeNullUTF(host);
    out.writeCompressedInt(disableLog);
    out.writeNullUTF(predisablePassword);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_4) >= 0) {
      out.writeCompressedInt(maxQuestions);
      out.writeCompressedInt(maxUpdates);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_111) >= 0) {
      out.writeCompressedInt(maxConnections);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_4) >= 0) {
      out.writeCompressedInt(maxUserConnections);
    }
  }

  @Override
  public boolean canSetPassword() throws SQLException, IOException {
    return !isDisabled() && !isSpecial();
  }
}
