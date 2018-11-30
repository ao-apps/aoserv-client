/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2009, 2016, 2017, 2018  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.mysql;

import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.CannotRemoveReason;
import com.aoindustries.aoserv.client.Removable;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A <code>MySQLDBUser</code> grants a <code>MySQLServerUser</code>
 * access to a <code>MySQLDatabase</code>.  The database and
 * user must be on the same server.
 *
 * @see  Database
 * @see  UserServer
 *
 * @author  AO Industries, Inc.
 */
final public class DatabaseUser extends CachedObjectIntegerKey<DatabaseUser> implements Removable {

	static final int
		COLUMN_PKEY=0,
		COLUMN_MYSQL_DATABASE=1,
		COLUMN_MYSQL_SERVER_USER=2
	;
	static final String COLUMN_MYSQL_DATABASE_name = "mysql_database";
	static final String COLUMN_MYSQL_SERVER_USER_name = "mysql_server_user";

	int mysql_database;
	int mysql_server_user;

	private boolean
		select_priv,
		insert_priv,
		update_priv,
		delete_priv,
		create_priv,
		drop_priv,
		grant_priv,
		references_priv,
		index_priv,
		alter_priv,
		create_tmp_table_priv,
		lock_tables_priv,
		create_view_priv,
		show_view_priv,
		create_routine_priv,
		alter_routine_priv,
		execute_priv,
		event_priv,
		trigger_priv
	;

	public boolean canAlter() {
		return alter_priv;
	}

	public boolean canCreateTempTable() {
		return create_tmp_table_priv;
	}

	public boolean canLockTables() {
		return lock_tables_priv;
	}

	public boolean canCreate() {
		return create_priv;
	}

	public boolean canDelete() {
		return delete_priv;
	}

	public boolean canDrop() {
		return drop_priv;
	}

	public boolean canGrant() {
		return grant_priv;
	}

	public boolean canIndex() {
		return index_priv;
	}

	public boolean canInsert() {
		return insert_priv;
	}

	public boolean canReference() {
		return references_priv;
	}

	public boolean canSelect() {
		return select_priv;
	}

	public boolean canUpdate() {
		return update_priv;
	}

	public boolean canCreateView() {
		return create_view_priv;
	}

	public boolean canShowView() {
		return show_view_priv;
	}

	public boolean canCreateRoutine() {
		return create_routine_priv;
	}

	public boolean canAlterRoutine() {
		return alter_routine_priv;
	}

	public boolean canExecute() {
		return execute_priv;
	}

	public boolean canEvent() {
		return event_priv;
	}

	public boolean canTrigger() {
		return trigger_priv;
	}

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_MYSQL_DATABASE: return mysql_database;
			case COLUMN_MYSQL_SERVER_USER: return mysql_server_user;
			case 3: return select_priv;
			case 4: return insert_priv;
			case 5: return update_priv;
			case 6: return delete_priv;
			case 7: return create_priv;
			case 8: return drop_priv;
			case 9: return grant_priv;
			case 10: return references_priv;
			case 11: return index_priv;
			case 12: return alter_priv;
			case 13: return create_tmp_table_priv;
			case 14: return lock_tables_priv;
			case 15: return create_view_priv;
			case 16: return show_view_priv;
			case 17: return create_routine_priv;
			case 18: return alter_routine_priv;
			case 19: return execute_priv;
			case 20: return event_priv;
			case 21: return trigger_priv;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public Database getMySQLDatabase() throws IOException, SQLException {
		// May be null due to filtering or a recently removed table
		return table.getConnector().getMysqlDatabases().get(mysql_database);
	}

	public UserServer getMySQLServerUser() throws IOException, SQLException {
		// May be null due to filtering or a recently removed table
		return table.getConnector().getMysqlServerUsers().get(mysql_server_user);
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.MYSQL_DB_USERS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey=result.getInt(1);
		mysql_database=result.getInt(2);
		mysql_server_user=result.getInt(3);
		select_priv=result.getBoolean(4);
		insert_priv=result.getBoolean(5);
		update_priv=result.getBoolean(6);
		delete_priv=result.getBoolean(7);
		create_priv=result.getBoolean(8);
		drop_priv=result.getBoolean(9);
		grant_priv=result.getBoolean(10);
		references_priv=result.getBoolean(11);
		index_priv=result.getBoolean(12);
		alter_priv=result.getBoolean(13);
		create_tmp_table_priv=result.getBoolean(14);
		lock_tables_priv=result.getBoolean(15);
		create_view_priv=result.getBoolean(16);
		show_view_priv=result.getBoolean(17);
		create_routine_priv=result.getBoolean(18);
		alter_routine_priv=result.getBoolean(19);
		execute_priv=result.getBoolean(20);
		event_priv=result.getBoolean(21);
		trigger_priv=result.getBoolean(22);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readCompressedInt();
		mysql_database=in.readCompressedInt();
		mysql_server_user=in.readCompressedInt();
		select_priv=in.readBoolean();
		insert_priv=in.readBoolean();
		update_priv=in.readBoolean();
		delete_priv=in.readBoolean();
		create_priv=in.readBoolean();
		drop_priv=in.readBoolean();
		grant_priv=in.readBoolean();
		references_priv=in.readBoolean();
		index_priv=in.readBoolean();
		alter_priv=in.readBoolean();
		create_tmp_table_priv=in.readBoolean();
		lock_tables_priv=in.readBoolean();
		create_view_priv=in.readBoolean();
		show_view_priv=in.readBoolean();
		create_routine_priv=in.readBoolean();
		alter_routine_priv=in.readBoolean();
		execute_priv=in.readBoolean();
		event_priv=in.readBoolean();
		trigger_priv=in.readBoolean();
	}

	@Override
	public List<CannotRemoveReason<?>> getCannotRemoveReasons() throws IOException, SQLException {
		List<CannotRemoveReason<?>> reasons=new ArrayList<>();
		reasons.addAll(getMySQLServerUser().getCannotRemoveReasons());
		reasons.addAll(getMySQLDatabase().getCannotRemoveReasons());
		return reasons;
	}

	@Override
	public void remove() throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true,
			AoservProtocol.CommandID.REMOVE,
			Table.TableID.MYSQL_DB_USERS,
			pkey
		);
	}

	@Override
	public void write(CompressedDataOutputStream out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(mysql_database);
		out.writeCompressedInt(mysql_server_user);
		out.writeBoolean(select_priv);
		out.writeBoolean(insert_priv);
		out.writeBoolean(update_priv);
		out.writeBoolean(delete_priv);
		out.writeBoolean(create_priv);
		out.writeBoolean(drop_priv);
		out.writeBoolean(grant_priv);
		out.writeBoolean(references_priv);
		out.writeBoolean(index_priv);
		out.writeBoolean(alter_priv);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_111)>=0) {
			out.writeBoolean(create_tmp_table_priv);
			out.writeBoolean(lock_tables_priv);
		}
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_4)>=0) {
			out.writeBoolean(create_view_priv);
			out.writeBoolean(show_view_priv);
			out.writeBoolean(create_routine_priv);
			out.writeBoolean(alter_routine_priv);
			out.writeBoolean(execute_priv);
		}
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_54)>=0) {
			out.writeBoolean(event_priv);
			out.writeBoolean(trigger_priv);
		}
	}
}
