/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2009, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2025  AO Industries, Inc.
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

import com.aoapps.collections.IntList;
import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
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
 * A <code>PostgresServerUser</code> grants a <code>PostgresUser</code>
 * access to a <code>Server</code>.
 *
 * @see  User
 * @see  Server
 *
 * @author  AO Industries, Inc.
 */
public final class UserServer extends CachedObjectIntegerKey<UserServer> implements Removable, PasswordProtected, Disablable {

  static final int COLUMN_PKEY = 0;
  static final int COLUMN_USERNAME = 1;
  static final int COLUMN_POSTGRES_SERVER = 2;
  static final String COLUMN_USERNAME_name = "username";
  static final String COLUMN_POSTGRES_SERVER_name = "postgres_server";

  private User.Name username;
  private int postgresServer;
  private int disableLog;
  private String predisablePassword;

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
      throw new SQLException("Refusing to check if passwords set on special PostgreSQL user: " + this);
    }
    return table.getConnector().requestBooleanQuery(true, AoservProtocol.CommandId.IS_POSTGRES_SERVER_USER_PASSWORD_SET, pkey)
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
      return dl.canEnable() && !getPostgresUser().isDisabled();
    }
  }

  @Override
  public List<PasswordChecker.Result> checkPassword(String password) throws IOException {
    return User.checkPassword(username, password);
  }

  /*public String checkPasswordDescribe(String password) {
    return PostgresUser.checkPasswordDescribe(username, password);
  }*/

  @Override
  public void disable(DisableLog dl) throws IOException, SQLException {
    if (isSpecial()) {
      throw new SQLException("Refusing to disable special PostgreSQL user: " + this);
    }
    table.getConnector().requestUpdateInvalidating(true, AoservProtocol.CommandId.DISABLE, Table.TableId.POSTGRES_SERVER_USERS, dl.getPkey(), pkey);
  }

  @Override
  public void enable() throws IOException, SQLException {
    if (isSpecial()) {
      throw new SQLException("Refusing to enable special PostgreSQL user: " + this);
    }
    table.getConnector().requestUpdateInvalidating(true, AoservProtocol.CommandId.ENABLE, Table.TableId.POSTGRES_SERVER_USERS, pkey);
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_PKEY:
        return pkey;
      case COLUMN_USERNAME:
        return username;
      case COLUMN_POSTGRES_SERVER:
        return postgresServer;
      case 3:
        return getDisableLog_id();
      case 4:
        return predisablePassword;
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
  public DisableLog getDisableLog() throws IOException, SQLException {
    if (disableLog == -1) {
      return null;
    }
    DisableLog obj = table.getConnector().getAccount().getDisableLog().get(disableLog);
    if (obj == null) {
      throw new SQLException("Unable to find DisableLog: " + disableLog);
    }
    return obj;
  }

  public List<Database> getPostgresDatabases() throws IOException, SQLException {
    return table.getConnector().getPostgresql().getDatabase().getPostgresDatabases(this);
  }

  public User.Name getPostgresUser_username() {
    return username;
  }

  public User getPostgresUser() throws SQLException, IOException {
    User obj = table.getConnector().getPostgresql().getUser().get(username);
    if (obj == null) {
      throw new SQLException("Unable to find PostgresUser: " + username);
    }
    return obj;
  }

  public boolean isSpecial() {
    return User.isSpecial(username);
  }

  public String getPredisablePassword() {
    return predisablePassword;
  }

  public int getPostgresServer_bind_id() {
    return postgresServer;
  }

  public Server getPostgresServer() throws IOException, SQLException {
    // May be filtered
    return table.getConnector().getPostgresql().getServer().get(postgresServer);
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.POSTGRES_SERVER_USERS;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    try {
      pkey = result.getInt(1);
      username = User.Name.valueOf(result.getString(2));
      postgresServer = result.getInt(3);
      disableLog = result.getInt(4);
      if (result.wasNull()) {
        disableLog = -1;
      }
      predisablePassword = result.getString(5);
    } catch (ValidationException e) {
      throw new SQLException(e);
    }
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    try {
      pkey = in.readCompressedInt();
      username = User.Name.valueOf(in.readUTF()).intern();
      postgresServer = in.readCompressedInt();
      disableLog = in.readCompressedInt();
      predisablePassword = in.readNullUTF();
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  @Override
  public List<CannotRemoveReason<?>> getCannotRemoveReasons() throws SQLException, IOException {
    List<CannotRemoveReason<?>> reasons = new ArrayList<>();
    Server ps = getPostgresServer();
    if (isSpecial()) {
      reasons.add(
          new CannotRemoveReason<>(
              "Not allowed to remove a special PostgreSQL user: "
                  + username
                  + " on "
                  + ps.getName()
                  + " on "
                  + ps.getLinuxServer().getHostname(),
              this
          )
      );
    }

    for (Database pd : getPostgresDatabases()) {
      assert ps.equals(pd.getPostgresServer());
      reasons.add(new CannotRemoveReason<>("Used by PostgreSQL database " + pd.getName() + " on " + ps.getName() + " on " + ps.getLinuxServer().getHostname(), pd));
    }

    return reasons;
  }

  @Override
  public void remove() throws IOException, SQLException {
    if (isSpecial()) {
      throw new SQLException("Refusing to remove special PostgreSQL user: " + this);
    }
    table.getConnector().requestUpdateInvalidating(
        true,
        AoservProtocol.CommandId.REMOVE,
        Table.TableId.POSTGRES_SERVER_USERS,
        pkey
    );
  }

  @Override
  public void setPassword(final String password) throws IOException, SQLException {
    if (isSpecial()) {
      throw new SQLException("Refusing to set the password for a special PostgreSQL user: " + this);
    }

    AoservConnector connector = table.getConnector();
    if (!connector.isSecure()) {
      throw new IOException("Passwords for PostgreSQL users may only be set when using secure protocols.  Currently using the " + connector.getProtocol() + " protocol, which is not secure.");
    }

    connector.requestUpdate(
        true,
        AoservProtocol.CommandId.SET_POSTGRES_SERVER_USER_PASSWORD,
        new AoservConnector.UpdateRequest() {
          @Override
          public void writeRequest(StreamableOutput out) throws IOException {
            out.writeCompressedInt(pkey);
            out.writeBoolean(password != null);
            if (password != null) {
              out.writeUTF(password);
            }
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
      throw new SQLException("May not disable special PostgreSQL user: " + username);
    }
    table.getConnector().requestUpdate(
        true,
        AoservProtocol.CommandId.SET_POSTGRES_SERVER_USER_PREDISABLE_PASSWORD,
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
    return username + " on " + getPostgresServer().toStringImpl();
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeUTF(username.toString());
    out.writeCompressedInt(postgresServer);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_130) <= 0) {
      out.writeCompressedInt(-1);
    }
    out.writeCompressedInt(disableLog);
    out.writeNullUTF(predisablePassword);
  }

  @Override
  public boolean canSetPassword() throws SQLException, IOException {
    return !isDisabled() && !isSpecial();
  }
}
