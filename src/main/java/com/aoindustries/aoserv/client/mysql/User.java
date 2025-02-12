/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2009, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2024, 2025  AO Industries, Inc.
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

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.i18n.Resources;
import com.aoapps.lang.io.FastExternalizable;
import com.aoapps.lang.validation.InvalidResult;
import com.aoapps.lang.validation.ValidResult;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.lang.validation.ValidationResult;
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
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A <code>MysqlUser</code> stores the details of a MySQL account
 * that are common to all servers.
 *
 * @see  UserServer
 * @see  DatabaseUser
 *
 * @author  AO Industries, Inc.
 */
public final class User extends CachedObjectUserNameKey<User> implements PasswordProtected, Removable, Disablable {

  private static final Resources RESOURCES = Resources.getResources(ResourceBundle::getBundle, User.class);

  /**
   * Represents a MySQL user ID.  {@link User} ids must:
   * <ul>
   *   <li>Be non-null</li>
   *   <li>Be non-empty</li>
   *   <li>Be between 1 and 32 characters</li>
   *   <li>Must start with <code>[a-z]</code></li>
   *   <li>The rest of the characters may contain <code>[a-z,0-9,_]</code></li>
   *   <li>A special exemption is made for the <code>mysql.session</code> and <code>mysql.sys</code> reserved users added in MySQL 5.7.</li>
   *   <li>Must be a valid {@link com.aoindustries.aoserv.client.linux.User.Name} - this is implied by the above rules</li>
   * </ul>
   *
   * @author  AO Industries, Inc.
   */
  public static final class Name extends com.aoindustries.aoserv.client.linux.User.Name implements
      FastExternalizable {

    /**
     * The maximum length of a MySQL username.
     *
     * <p><b>Implementation Note:</b><br>
     * 32 characters as of <a href="https://dev.mysql.com/doc/relnotes/mysql/5.7/en/news-5-7-8.html">MySQL 5.7.8</a></p>
     */
    public static final int MYSQL_NAME_MAX_LENGTH = 32;

    /**
     * Validates a {@link User} name.
     */
    public static ValidationResult validate(String name) {
      if (name == null) {
        return new InvalidResult(RESOURCES, "Name.validate.isNull");
      }
      if (
          // Allow specific system users that otherwise do not match our allowed username pattern
          !"mysql.sys".equals(name)
              && !"mysql.session".equals(name)
      ) {
        int len = name.length();
        if (len == 0) {
          return new InvalidResult(RESOURCES, "Name.validate.isEmpty");
        }
        if (len > MYSQL_NAME_MAX_LENGTH) {
          return new InvalidResult(RESOURCES, "Name.validate.tooLong", MYSQL_NAME_MAX_LENGTH, len);
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
                  && ch != '_'
          ) {
            return new InvalidResult(RESOURCES, "Name.validate.illegalCharacter");
          }
        }
      }
      assert com.aoindustries.aoserv.client.linux.User.Name.validate(name).isValid() : "A MySQL User.Name is always a valid Linux User.Name.";
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

    private Name(String name, boolean validate) throws ValidationException {
      super(name, validate);
    }

    /**
     * @param  name  Does not validate, should only be used with a known valid value.
     */
    private Name(String name) {
      super(name);
    }

    /**
     * @deprecated  Only required for implementation, do not use directly.
     *
     * @see  FastExternalizable
     */
    @Deprecated(forRemoval = false)
    public Name() {
      // Do nothing
    }

    @Override
    protected void validate() throws ValidationException {
      ValidationResult result = validate(name);
      if (!result.isValid()) {
        throw new ValidationException(result);
      }
    }

    @Override
    public Name intern() {
      Name existing = interned.get(name);
      if (existing == null) {
        String internedId = name.intern();
        @SuppressWarnings("StringEquality")
        Name addMe = (name == internedId) ? this : new Name(internedId);
        existing = interned.putIfAbsent(internedId, addMe);
        if (existing == null) {
          existing = addMe;
        }
      }
      return existing;
    }

    @Override
    public com.aoindustries.aoserv.client.dto.MysqlUserName getDto() {
      return new com.aoindustries.aoserv.client.dto.MysqlUserName(name);
    }

    // <editor-fold defaultstate="collapsed" desc="FastExternalizable">
    private static final long serialVersionUID = 2L;

    @Override
    public long getSerialVersionUID() {
      return serialVersionUID;
    }
    // </editor-fold>
  }

  static final int COLUMN_USERNAME = 0;
  static final String COLUMN_USERNAME_name = "username";

  /**
   * The maximum length of a MySQL username.
   *
   * @deprecated  Please use {@link Name#MAX_LENGTH} instead.
   */
  @Deprecated
  public static final int MAX_USERNAME_LENGTH = Name.MYSQL_NAME_MAX_LENGTH;

  /** The username of the MySQL super user. */
  public static final Name ROOT;
  /** The username of the MySQL <code>mysql.session</code> user added in MySQL 5.7. */
  public static final Name MYSQL_SESSION;
  /** The username of the MySQL <code>mysql.sys</code> user added in MySQL 5.7. */
  public static final Name MYSQL_SYS;
  /** The default username for MySQL monitoring. */
  public static final Name MYSQLMON;

  static {
    try {
      // The username of the MySQL super user.
      ROOT = Name.valueOf("root").intern();
      // The username of the MySQL <code>mysql.session</code> user added in MySQL 5.7.
      MYSQL_SESSION = Name.valueOf("mysql.session").intern();
      // The username of the MySQL <code>mysql.sys</code> user added in MySQL 5.7.
      MYSQL_SYS = Name.valueOf("mysql.sys").intern();
      // Monitoring
      MYSQLMON = Name.valueOf("mysqlmon").intern();
    } catch (ValidationException e) {
      throw new AssertionError("These hard-coded values are valid", e);
    }
  }

  /**
   * Special MySQL users may not be added or removed.
   */
  public static boolean isSpecial(Name username) {
    return
        // The username of the MySQL super user.
        username.equals(ROOT)
            // The username of the MySQL <code>mysql.session</code> user added in MySQL 5.7.
            || username.equals(MYSQL_SESSION)
            // The username of the MySQL <code>mysql.sys</code> user added in MySQL 5.7.
            || username.equals(MYSQL_SYS)
            // Monitoring
            || username.equals(MYSQLMON);
  }

  /**
   * A password may be set to null, which means that the account will
   * be disabled.
   */
  public static final String NO_PASSWORD = null;

  public static final String NO_PASSWORD_DB_VALUE = "*";

  private boolean selectPriv;
  private boolean insertPriv;
  private boolean updatePriv;
  private boolean deletePriv;
  private boolean createPriv;
  private boolean dropPriv;
  private boolean reloadPriv;
  private boolean shutdownPriv;
  private boolean processPriv;
  private boolean filePriv;
  private boolean grantPriv;
  private boolean referencesPriv;
  private boolean indexPriv;
  private boolean alterPriv;
  private boolean showDbPriv;
  private boolean superPriv;
  private boolean createTmpTablePriv;
  private boolean lockTablesPriv;
  private boolean executePriv;
  private boolean replSlavePriv;
  private boolean replClientPriv;
  private boolean createViewPriv;
  private boolean showViewPriv;
  private boolean createRoutinePriv;
  private boolean alterRoutinePriv;
  private boolean createUserPriv;
  private boolean eventPriv;
  private boolean triggerPriv;

  private int disableLog;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public User() {
    // Do nothing
  }

  public int addMysqlServerUser(Server mysqlServer, String host) throws IOException, SQLException {
    return table.getConnector().getMysql().getUserServer().addMysqlServerUser(pkey, mysqlServer, host);
  }

  @Override
  public int arePasswordsSet() throws IOException, SQLException {
    if (isSpecial()) {
      throw new SQLException("Refusing to check if passwords set on special MySQL user: " + this);
    }
    return com.aoindustries.aoserv.client.account.User.groupPasswordsSet(getMysqlServerUsers());
  }

  public boolean canAlter() {
    return alterPriv;
  }

  public boolean canShowDb() {
    return showDbPriv;
  }

  public boolean isSuper() {
    return superPriv;
  }

  public boolean canCreateTempTable() {
    return createTmpTablePriv;
  }

  public boolean canLockTables() {
    return lockTablesPriv;
  }

  public boolean canExecute() {
    return executePriv;
  }

  public boolean isReplicationSlave() {
    return replSlavePriv;
  }

  public boolean isReplicationClient() {
    return replClientPriv;
  }

  public boolean canCreateView() {
    return createViewPriv;
  }

  public boolean canShowView() {
    return showViewPriv;
  }

  public boolean canCreateRoutine() {
    return createRoutinePriv;
  }

  public boolean canAlterRoutine() {
    return alterRoutinePriv;
  }

  public boolean canCreateUser() {
    return createUserPriv;
  }

  public boolean canEvent() {
    return eventPriv;
  }

  public boolean canTrigger() {
    return triggerPriv;
  }

  public boolean canCreate() {
    return createPriv;
  }

  public boolean canDelete() {
    return deletePriv;
  }

  @Override
  public boolean canDisable() throws IOException, SQLException {
    if (isDisabled() || isSpecial()) {
      return false;
    }
    for (UserServer msu : getMysqlServerUsers()) {
      if (!msu.isDisabled()) {
        return false;
      }
    }
    return true;
  }

  public boolean canDrop() {
    return dropPriv;
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
      return dl.canEnable() && !getUsername().isDisabled();
    }
  }

  public boolean canFile() {
    return filePriv;
  }

  public boolean canGrant() {
    return grantPriv;
  }

  public boolean canIndex() {
    return indexPriv;
  }

  public boolean canInsert() {
    return insertPriv;
  }

  public boolean canProcess() {
    return processPriv;
  }

  public boolean canReference() {
    return referencesPriv;
  }

  public boolean canReload() {
    return reloadPriv;
  }

  public boolean canSelect() {
    return selectPriv;
  }

  public boolean canShutdown() {
    return shutdownPriv;
  }

  public boolean canUpdate() {
    return updatePriv;
  }

  @Override
  public List<PasswordChecker.Result> checkPassword(String password) throws IOException {
    return checkPassword(pkey, password);
  }

  public static List<PasswordChecker.Result> checkPassword(Name username, String password) throws IOException {
    return PasswordChecker.checkPassword(username, password, PasswordChecker.PasswordStrength.STRICT);
  }

  /*public String checkPasswordDescribe(String password) {
    return checkPasswordDescribe(pkey, password);
  }

  public static String checkPasswordDescribe(String username, String password) {
    return PasswordChecker.checkPasswordDescribe(username, password, true, false);
  }*/

  @Override
  public void disable(DisableLog dl) throws IOException, SQLException {
    if (isSpecial()) {
      throw new SQLException("Refusing to disable special MySQL user: " + this);
    }
    table.getConnector().requestUpdateInvalidating(true, AoservProtocol.CommandId.DISABLE, Table.TableId.MYSQL_USERS, dl.getPkey(), pkey);
  }

  @Override
  public void enable() throws IOException, SQLException {
    if (isSpecial()) {
      throw new SQLException("Refusing to enable special MySQL user: " + this);
    }
    table.getConnector().requestUpdateInvalidating(true, AoservProtocol.CommandId.ENABLE, Table.TableId.MYSQL_USERS, pkey);
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_USERNAME:
        return pkey;
      case 1:
        return selectPriv;
      case 2:
        return insertPriv;
      case 3:
        return updatePriv;
      case 4:
        return deletePriv;
      case 5:
        return createPriv;
      case 6:
        return dropPriv;
      case 7:
        return reloadPriv;
      case 8:
        return shutdownPriv;
      case 9:
        return processPriv;
      case 10:
        return filePriv;
      case 11:
        return grantPriv;
      case 12:
        return referencesPriv;
      case 13:
        return indexPriv;
      case 14:
        return alterPriv;
      case 15:
        return showDbPriv;
      case 16:
        return superPriv;
      case 17:
        return createTmpTablePriv;
      case 18:
        return lockTablesPriv;
      case 19:
        return executePriv;
      case 20:
        return replSlavePriv;
      case 21:
        return replClientPriv;
      case 22:
        return createViewPriv;
      case 23:
        return showViewPriv;
      case 24:
        return createRoutinePriv;
      case 25:
        return alterRoutinePriv;
      case 26:
        return createUserPriv;
      case 27:
        return eventPriv;
      case 28:
        return triggerPriv;
      case 29:
        return getDisableLog_id();
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

  public UserServer getMysqlServerUser(Server mysqlServer) throws IOException, SQLException {
    return table.getConnector().getMysql().getUserServer().getMysqlServerUser(pkey, mysqlServer);
  }

  public List<UserServer> getMysqlServerUsers() throws IOException, SQLException {
    return table.getConnector().getMysql().getUserServer().getMysqlServerUsers(this);
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.MYSQL_USERS;
  }

  public Name getUsername_id() {
    return pkey;
  }

  public com.aoindustries.aoserv.client.account.User getUsername() throws SQLException, IOException {
    com.aoindustries.aoserv.client.account.User obj = table.getConnector().getAccount().getUser().get(pkey);
    if (obj == null) {
      throw new SQLException("Unable to find Username: " + pkey);
    }
    return obj;
  }

  public boolean isSpecial() {
    return isSpecial(pkey);
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    try {
      pkey = Name.valueOf(result.getString(1));
      selectPriv = result.getBoolean(2);
      insertPriv = result.getBoolean(3);
      updatePriv = result.getBoolean(4);
      deletePriv = result.getBoolean(5);
      createPriv = result.getBoolean(6);
      dropPriv = result.getBoolean(7);
      reloadPriv = result.getBoolean(8);
      shutdownPriv = result.getBoolean(9);
      processPriv = result.getBoolean(10);
      filePriv = result.getBoolean(11);
      grantPriv = result.getBoolean(12);
      referencesPriv = result.getBoolean(13);
      indexPriv = result.getBoolean(14);
      alterPriv = result.getBoolean(15);
      showDbPriv = result.getBoolean(16);
      superPriv = result.getBoolean(17);
      createTmpTablePriv = result.getBoolean(18);
      lockTablesPriv = result.getBoolean(19);
      executePriv = result.getBoolean(20);
      replSlavePriv = result.getBoolean(21);
      replClientPriv = result.getBoolean(22);
      createViewPriv = result.getBoolean(23);
      showViewPriv = result.getBoolean(24);
      createRoutinePriv = result.getBoolean(25);
      alterRoutinePriv = result.getBoolean(26);
      createUserPriv = result.getBoolean(27);
      eventPriv = result.getBoolean(28);
      triggerPriv = result.getBoolean(29);
      disableLog = result.getInt(30);
      if (result.wasNull()) {
        disableLog = -1;
      }
    } catch (ValidationException e) {
      throw new SQLException(e);
    }
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    try {
      pkey = Name.valueOf(in.readUTF()).intern();
      selectPriv = in.readBoolean();
      insertPriv = in.readBoolean();
      updatePriv = in.readBoolean();
      deletePriv = in.readBoolean();
      createPriv = in.readBoolean();
      dropPriv = in.readBoolean();
      reloadPriv = in.readBoolean();
      shutdownPriv = in.readBoolean();
      processPriv = in.readBoolean();
      filePriv = in.readBoolean();
      grantPriv = in.readBoolean();
      referencesPriv = in.readBoolean();
      indexPriv = in.readBoolean();
      alterPriv = in.readBoolean();
      showDbPriv = in.readBoolean();
      superPriv = in.readBoolean();
      createTmpTablePriv = in.readBoolean();
      lockTablesPriv = in.readBoolean();
      executePriv = in.readBoolean();
      replSlavePriv = in.readBoolean();
      replClientPriv = in.readBoolean();
      createViewPriv = in.readBoolean();
      showViewPriv = in.readBoolean();
      createRoutinePriv = in.readBoolean();
      alterRoutinePriv = in.readBoolean();
      createUserPriv = in.readBoolean();
      eventPriv = in.readBoolean();
      triggerPriv = in.readBoolean();
      disableLog = in.readCompressedInt();
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  @Override
  public List<CannotRemoveReason<User>> getCannotRemoveReasons() {
    List<CannotRemoveReason<User>> reasons = new ArrayList<>();
    if (isSpecial()) {
      reasons.add(
          new CannotRemoveReason<>(
              "Not allowed to remove a special MySQL user: " + pkey,
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
        Table.TableId.MYSQL_USERS,
        pkey
    );
  }

  @Override
  public void setPassword(String password) throws IOException, SQLException {
    for (UserServer user : getMysqlServerUsers()) {
      user.setPassword(password);
    }
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeUTF(pkey.toString());
    out.writeBoolean(selectPriv);
    out.writeBoolean(insertPriv);
    out.writeBoolean(updatePriv);
    out.writeBoolean(deletePriv);
    out.writeBoolean(createPriv);
    out.writeBoolean(dropPriv);
    out.writeBoolean(reloadPriv);
    out.writeBoolean(shutdownPriv);
    out.writeBoolean(processPriv);
    out.writeBoolean(filePriv);
    out.writeBoolean(grantPriv);
    out.writeBoolean(referencesPriv);
    out.writeBoolean(indexPriv);
    out.writeBoolean(alterPriv);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_111) >= 0) {
      out.writeBoolean(showDbPriv);
      out.writeBoolean(superPriv);
      out.writeBoolean(createTmpTablePriv);
      out.writeBoolean(lockTablesPriv);
      out.writeBoolean(executePriv);
      out.writeBoolean(replSlavePriv);
      out.writeBoolean(replClientPriv);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_4) >= 0) {
      out.writeBoolean(createViewPriv);
      out.writeBoolean(showViewPriv);
      out.writeBoolean(createRoutinePriv);
      out.writeBoolean(alterRoutinePriv);
      out.writeBoolean(createUserPriv);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_54) >= 0) {
      out.writeBoolean(eventPriv);
      out.writeBoolean(triggerPriv);
    }
    out.writeCompressedInt(disableLog);
  }

  @Override
  public boolean canSetPassword() {
    return !isDisabled() && !isSpecial();
  }
}
