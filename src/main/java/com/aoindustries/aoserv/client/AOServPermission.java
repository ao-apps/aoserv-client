/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2007-2012, 2016, 2017  AO Industries, Inc.
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
 * along with aoserv-client.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.aoserv.client;

import static com.aoindustries.aoserv.client.ApplicationResources.accessor;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * All of the permissions within the system.
 *
 * @author  AO Industries, Inc.
 */
final public class AOServPermission extends GlobalObjectStringKey<AOServPermission> {

	static final int COLUMN_NAME=0;
	static final String COLUMN_SORT_ORDER_name = "sort_order";

	/**
	 * The possible permissions.
	 */
	public enum Permission {
		// business_administrators
		set_business_administrator_password,
		// businesses
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
		vnc_console
		;

		/**
		 * Gets the permission display value in the thread locale.
		 */
		@Override
		public String toString() {
			return accessor.getMessage("AOServPermission."+name()+".toString");
		}
	}

	// From database
	private short sort_order;

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_NAME: return pkey;
			case 1: return sort_order;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	@Override
	String toStringImpl() {
		return accessor.getMessage("AOServPermission."+pkey+".toString");
	}

	/**
	 * Gets the locale-specific description of this permission.
	 */
	public String getDescription() {
		return accessor.getMessage("AOServPermission."+pkey+".description");
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.AOSERV_PERMISSIONS;
	}

	public String getName() {
		return pkey;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey = result.getString(1);
		sort_order = result.getShort(2);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readUTF().intern();
		sort_order = in.readShort();
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version protocolVersion) throws IOException {
		out.writeUTF(pkey);
		out.writeShort(sort_order);
	}
}
