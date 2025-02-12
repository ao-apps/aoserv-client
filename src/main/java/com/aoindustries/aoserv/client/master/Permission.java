/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2007-2012, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2025  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.master;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.i18n.Resources;
import com.aoindustries.aoserv.client.GlobalObjectStringKey;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * All of the permissions within the system.
 *
 * @author  AO Industries, Inc.
 */
public final class Permission extends GlobalObjectStringKey<Permission> {

  private static final Resources RESOURCES = Resources.getResources(ResourceBundle::getBundle, Permission.class);

  static final int COLUMN_NAME = 0;
  static final String COLUMN_SORT_ORDER_name = "sort_order";

  /**
   * The possible permissions.
   */
  public enum Name {
    // administrators
    set_business_administrator_password,
    // accounts
    cancel_business,
    // credit_card_processors
    get_credit_card_processors,
    // credit_card_transactions
    add_credit_card_transaction,
    credit_card_transaction_authorize_completed,
    credit_card_transaction_sale_completed,
    get_credit_card_transactions,
    // credit_cards
    get_credit_cards,
    add_credit_card,
    delete_credit_card,
    edit_credit_card,
    // linux_server_accounts
    set_linux_server_account_password,
    // mysql_databases
    check_mysql_tables,
    get_mysql_table_status,
    // mysql_server_users
    set_mysql_server_user_password,
    // mysql_servers
    get_mysql_master_status,
    get_mysql_slave_status,
    // postgres_server_users
    set_postgres_server_user_password,
    // tickets
    add_ticket,
    edit_ticket,
    // virtual_servers
    control_virtual_server,
    get_virtual_server_status,
    vnc_console;

    /**
     * Gets the permission display value in the thread locale.
     */
    @Override
    public String toString() {
      return RESOURCES.getMessage(name() + ".toString");
    }
  }

  // From database
  private short sortOrder;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public Permission() {
    // Do nothing
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_NAME:
        return pkey;
      case 1:
        return sortOrder;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  @Override
  public String toStringImpl() {
    return RESOURCES.getMessage(pkey + ".toString");
  }

  /**
   * Gets the locale-specific description of this permission.
   */
  public String getDescription() {
    return RESOURCES.getMessage(pkey + ".description");
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.AOSERV_PERMISSIONS;
  }

  public String getName() {
    return pkey;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    pkey = result.getString(1);
    sortOrder = result.getShort(2);
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    pkey = in.readUTF().intern();
    sortOrder = in.readShort();
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeUTF(pkey);
    out.writeShort(sortOrder);
  }
}
