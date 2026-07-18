/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2026  AO Industries, Inc.
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

import java.util.function.Predicate;

/**
 * The platform-supported set of permissions for MySQL servers.
 *
 * @see  Server.Version
 * @see  User
 * @see  DatabaseUser
 *
 * @author  AO Industries, Inc.
 */
public enum Permission {
  SELECT("Select", "Select_priv", "SELECT", Server.Version.VERSION_4_1, User::canSelect, DatabaseUser::canSelect),
  INSERT("Insert", "Insert_priv", "INSERT", Server.Version.VERSION_4_1, User::canInsert, DatabaseUser::canInsert),
  UPDATE("Update", "Update_priv", "UPDATE", Server.Version.VERSION_4_1, User::canUpdate, DatabaseUser::canUpdate),
  DELETE("Delete", "Delete_priv", "DELETE", Server.Version.VERSION_4_1, User::canDelete, DatabaseUser::canDelete),
  CREATE("Create", "Create_priv", "CREATE", Server.Version.VERSION_4_1, User::canCreate, DatabaseUser::canCreate),
  DROP("Drop", "Drop_priv", "DROP", Server.Version.VERSION_4_1, User::canDrop, DatabaseUser::canDrop),
  RELOAD("Reload", "Reload_priv", "RELOAD", Server.Version.VERSION_4_1, User::canReload, null),
  SHUTDOWN("Shutdown", "Shutdown_priv", "SHUTDOWN", Server.Version.VERSION_4_1, User::canShutdown, null),
  PROCESS("Process", "Process_priv", "PROCESS", Server.Version.VERSION_4_1, User::canProcess, null),
  FILE("File", "File_priv", "FILE", Server.Version.VERSION_4_1, User::canFile, null),
  GRANT("Grant", "Grant_priv", "GRANT OPTION", Server.Version.VERSION_4_1, User::canGrant, DatabaseUser::canGrant),
  REFERENCES("Reference", "References_priv", "REFERENCES", Server.Version.VERSION_4_1, User::canReference, DatabaseUser::canReference),
  INDEX("Index", "Index_priv", "INDEX", Server.Version.VERSION_4_1, User::canIndex, DatabaseUser::canIndex),
  ALTER("Alter", "Alter_priv", "ALTER", Server.Version.VERSION_4_1, User::canAlter, DatabaseUser::canAlter),
  SHOW_DB("Show Db", "Show_db_priv", "SHOW DATABASES", Server.Version.VERSION_4_1, User::canShowDb, null),
  SUPER("Super", "Super_priv", "SUPER", Server.Version.VERSION_4_1, User::isSuper, null),
  CREATE_TMP_TABLE("Create Temp", "Create_tmp_table_priv", "CREATE TEMPORARY TABLES", Server.Version.VERSION_4_1, User::canCreateTempTable, DatabaseUser::canCreateTempTable),
  LOCK_TABLES("Lock Tables", "Lock_tables_priv", "LOCK TABLES", Server.Version.VERSION_4_1, User::canLockTables, DatabaseUser::canLockTables),
  EXECUTE("Execute", "Execute_priv", "EXECUTE", Server.Version.VERSION_4_1, User::canExecute, DatabaseUser::canExecute),
  REPL_SLAVE("Repl Slave", "Repl_slave_priv", "REPLICATION SLAVE", Server.Version.VERSION_4_1, User::isReplicationSlave, null),
  REPL_CLIENT("Repl Client", "Repl_client_priv", "REPLICATION CLIENT", Server.Version.VERSION_4_1, User::isReplicationClient, null),
  CREATE_VIEW("Create View", "Create_view_priv", "CREATE VIEW", Server.Version.VERSION_5_0, User::canCreateView, DatabaseUser::canCreateView),
  SHOW_VIEW("Show View", "Show_view_priv", "SHOW VIEW", Server.Version.VERSION_5_0, User::canShowView, DatabaseUser::canShowView),
  CREATE_ROUTINE("Create Routine", "Create_routine_priv", "CREATE ROUTINE", Server.Version.VERSION_5_0, User::canCreateRoutine, DatabaseUser::canCreateRoutine),
  ALTER_ROUTINE("Alter Routine", "Alter_routine_priv", "ALTER ROUTINE", Server.Version.VERSION_5_0, User::canAlterRoutine, DatabaseUser::canAlterRoutine),
  CREATE_USER("Create User", "Create_user_priv", "CREATE USER", Server.Version.VERSION_5_0, User::canCreateUser, null),
  EVENT("Event", "Event_priv", "EVENT", Server.Version.VERSION_5_6, User::canEvent, DatabaseUser::canEvent),
  TRIGGER("Trigger", "Trigger_priv", "TRIGGER", Server.Version.VERSION_5_6, User::canTrigger, DatabaseUser::canTrigger);

  private final String displayName;
  private final String mysqlColumn;
  private final String mysqlPrivilegeType;
  private final Server.Version since;
  private final Predicate<User> userAccessor;
  private final Predicate<DatabaseUser> databaseUserAccessor;

  private Permission(String displayName, String mysqlColumn, String mysqlPrivilegeType, Server.Version since, Predicate<User> userAccessor, Predicate<DatabaseUser> databaseUserAccessor) {
    this.displayName = displayName;
    this.mysqlColumn = mysqlColumn;
    this.mysqlPrivilegeType = mysqlPrivilegeType;
    this.since = since;
    this.userAccessor = userAccessor;
    this.databaseUserAccessor = databaseUserAccessor;
  }

  /**
   * @see Permission#getDisplayName()
   */
  @Override
  public String toString() {
    return getDisplayName();
  }

  /**
   * Gets the display name of this permission.
   */
  public String getDisplayName() {
    return displayName;
  }

  /**
   * The column name used in the MySQL grant tables.
   */
  public String getMysqlColumn() {
    return mysqlColumn;
  }

  /**
   * The privilege type used in MySQL GRANT and REVOKE.
   */
  public String getMysqlPrivilegeType() {
    return mysqlPrivilegeType;
  }

  /**
   * Is this permission supported on the given MySQL version?
   *
   * @see  Server.Version#getDatabaseUserPermissions()
   * @see  Server.Version#getUserPermissions()
   */
  public boolean isSupportedOn(Server.Version version) {
    return version.isAtLeast(since);
  }

  /**
   * Does this permission have a representation in {@link User}?
   *
   * @see  Server.Version#getUserPermissions()
   */
  public boolean isUserPermission() {
    return userAccessor != null;
  }

  /**
   * Is this permission granted to the given {@link User}?
   *
   * @throws  IllegalStateException when not {@link Permission#isUserPermission()}.
   *
   * @see  Server.Version#getUserPermissions()
   */
  public boolean isUserGranted(User user) throws IllegalStateException {
    if (userAccessor == null) {
      throw new IllegalStateException(name() + " is not represented in " + User.class.getName());
    }
    return userAccessor.test(user);
  }

  /**
   * Does this permission have a representation in {@link DatabaseUser}?
   *
   * @see  Server.Version#getDatabaseUserPermissions()
   */
  public boolean isDatabaseUserPermission() {
    return databaseUserAccessor != null;
  }

  /**
   * Is this permission granted to the given {@link DatabaseUser}?
   *
   * @throws  IllegalStateException when not {@link Permission#isDatabaseUserPermission()}.
   *
   * @see  Server.Version#getDatabaseUserPermissions()
   */
  public boolean isDatabaseUserGranted(DatabaseUser databaseUser) throws IllegalStateException {
    if (databaseUserAccessor == null) {
      throw new IllegalStateException(name() + " is not represented in " + DatabaseUser.class.getName());
    }
    return databaseUserAccessor.test(databaseUser);
  }
}
