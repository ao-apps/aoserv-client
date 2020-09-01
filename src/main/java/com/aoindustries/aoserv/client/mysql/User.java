/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2009, 2016, 2017, 2018, 2019, 2020  AO Industries, Inc.
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

import com.aoindustries.aoserv.client.CannotRemoveReason;
import com.aoindustries.aoserv.client.Disablable;
import com.aoindustries.aoserv.client.Removable;
import com.aoindustries.aoserv.client.account.DisableLog;
import static com.aoindustries.aoserv.client.mysql.ApplicationResources.accessor;
import com.aoindustries.aoserv.client.password.PasswordChecker;
import com.aoindustries.aoserv.client.password.PasswordProtected;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.io.FastExternalizable;
import com.aoindustries.io.stream.StreamableInput;
import com.aoindustries.io.stream.StreamableOutput;
import com.aoindustries.validation.InvalidResult;
import com.aoindustries.validation.ValidResult;
import com.aoindustries.validation.ValidationException;
import com.aoindustries.validation.ValidationResult;
import java.io.IOException;
import java.io.ObjectInputValidation;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A <code>MySQLUser</code> stores the details of a MySQL account
 * that are common to all servers.
 *
 * @see  UserServer
 * @see  DatabaseUser
 *
 * @author  AO Industries, Inc.
 */
final public class User extends CachedObjectUserNameKey<User> implements PasswordProtected, Removable, Disablable {

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
	final static public class Name extends com.aoindustries.aoserv.client.linux.User.Name implements
		FastExternalizable,
		ObjectInputValidation
	{

		/**
		 * The maximum length of a MySQL username.
		 * <p>
		 * <b>Implementation Note:</b><br>
		 * 32 characters as of <a href="https://dev.mysql.com/doc/relnotes/mysql/5.7/en/news-5-7-8.html">MySQL 5.7.8</a>
		 * </p>
		 */
		public static final int MYSQL_NAME_MAX_LENGTH = 32;

		/**
		 * Validates a {@link User} name.
		 */
		public static ValidationResult validate(String name) {
			if(name==null) return new InvalidResult(accessor, "User.Name.validate.isNull");
			if(
				// Allow specific system users that otherwise do not match our allowed username pattern
				!"mysql.sys".equals(name)
				&& !"mysql.session".equals(name)
			) {
				int len = name.length();
				if(len==0) return new InvalidResult(accessor, "User.Name.validate.isEmpty");
				if(len > MYSQL_NAME_MAX_LENGTH) return new InvalidResult(accessor, "User.Name.validate.tooLong", MYSQL_NAME_MAX_LENGTH, len);

				// The first character must be [a-z] or [0-9]
				char ch = name.charAt(0);
				if(
					(ch < 'a' || ch > 'z')
					&& (ch<'0' || ch>'9')
				) return new InvalidResult(accessor, "User.Name.validate.startAtoZor0to9");

				// The rest may have additional characters
				for (int c = 1; c < len; c++) {
					ch = name.charAt(c);
					if (
						(ch<'a' || ch>'z')
						&& (ch<'0' || ch>'9')
						&& ch!='_'
					) return new InvalidResult(accessor, "User.Name.validate.illegalCharacter");
				}
			}
			assert com.aoindustries.aoserv.client.linux.User.Name.validate(name).isValid() : "A MySQL User.Name is always a valid Linux User.Name.";
			return ValidResult.getInstance();
		}

		private static final ConcurrentMap<String,Name> interned = new ConcurrentHashMap<>();

		/**
		 * @param name  when {@code null}, returns {@code null}
		 */
		public static Name valueOf(String name) throws ValidationException {
			if(name == null) return null;
			//Name existing = interned.get(name);
			//return existing!=null ? existing : new Name(name);
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

		@Override
		protected void validate() throws ValidationException {
			ValidationResult result = validate(name);
			if(!result.isValid()) throw new ValidationException(result);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Name intern() {
			Name existing = interned.get(name);
			if(existing==null) {
				String internedId = name.intern();
				@SuppressWarnings("StringEquality")
				Name addMe = (name == internedId) ? this : new Name(internedId);
				existing = interned.putIfAbsent(internedId, addMe);
				if(existing==null) existing = addMe;
			}
			return existing;
		}

		@Override
		public com.aoindustries.aoserv.client.dto.MySQLUserName getDto() {
			return new com.aoindustries.aoserv.client.dto.MySQLUserName(name);
		}

		// <editor-fold defaultstate="collapsed" desc="FastExternalizable">
		private static final long serialVersionUID = 2L;

		public Name() {
		}

		@Override
		public long getSerialVersionUID() {
			return serialVersionUID;
		}
		// </editor-fold>
	}

	static final int COLUMN_USERNAME=0;
	static final String COLUMN_USERNAME_name = "username";

	/**
	 * The maximum length of a MySQL username.
	 *
	 * @deprecated  Please use {@link Name#MAX_LENGTH} instead.
	 */
	@Deprecated
	public static final int MAX_USERNAME_LENGTH = Name.MYSQL_NAME_MAX_LENGTH;

	/**
	 * The username of the MySQL special users.
	 */
	public static final Name
		/** The username of the MySQL super user. */
		ROOT,
		/** The username of the MySQL <code>mysql.session</code> user added in MySQL 5.7. */
		MYSQL_SESSION,
		/** The username of the MySQL <code>mysql.sys</code> user added in MySQL 5.7. */
		MYSQL_SYS,
		/** Monitoring */
		MYSQLMON;

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
		} catch(ValidationException e) {
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
	public static final String NO_PASSWORD=null;

	public static final String NO_PASSWORD_DB_VALUE="*";

	private boolean
		select_priv,
		insert_priv,
		update_priv,
		delete_priv,
		create_priv,
		drop_priv,
		reload_priv,
		shutdown_priv,
		process_priv,
		file_priv,
		grant_priv,
		references_priv,
		index_priv,
		alter_priv,
		show_db_priv,
		super_priv,
		create_tmp_table_priv,
		lock_tables_priv,
		execute_priv,
		repl_slave_priv,
		repl_client_priv,
		create_view_priv,
		show_view_priv,
		create_routine_priv,
		alter_routine_priv,
		create_user_priv,
		event_priv,
		trigger_priv
	;

	private int disable_log;

	public int addMySQLServerUser(Server mysqlServer, String host) throws IOException, SQLException {
		return table.getConnector().getMysql().getUserServer().addMySQLServerUser(pkey, mysqlServer, host);
	}

	@Override
	public int arePasswordsSet() throws IOException, SQLException {
		if(isSpecial()) throw new SQLException("Refusing to check if passwords set on special MySQL user: " + this);
		return com.aoindustries.aoserv.client.account.User.groupPasswordsSet(getMySQLServerUsers());
	}

	public boolean canAlter() {
		return alter_priv;
	}

	public boolean canShowDB() {
		return show_db_priv;
	}

	public boolean isSuper() {
		return super_priv;
	}

	public boolean canCreateTempTable() {
		return create_tmp_table_priv;
	}

	public boolean canLockTables() {
		return lock_tables_priv;
	}

	public boolean canExecute() {
		return execute_priv;
	}

	public boolean isReplicationSlave() {
		return repl_slave_priv;
	}

	public boolean isReplicationClient() {
		return repl_client_priv;
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

	public boolean canCreateUser() {
		return create_user_priv;
	}

	public boolean canEvent() {
		return event_priv;
	}

	public boolean canTrigger() {
		return trigger_priv;
	}

	public boolean canCreate() {
		return create_priv;
	}

	public boolean canDelete() {
		return delete_priv;
	}

	@Override
	public boolean canDisable() throws IOException, SQLException {
		if(isDisabled() || isSpecial()) return false;
		for(UserServer msu : getMySQLServerUsers()) {
			if(!msu.isDisabled()) return false;
		}
		return true;
	}

	public boolean canDrop() {
		return drop_priv;
	}

	@Override
	public boolean canEnable() throws SQLException, IOException {
		if(isSpecial()) return false;
		DisableLog dl = getDisableLog();
		if(dl == null) return false;
		else return dl.canEnable() && !getUsername().isDisabled();
	}

	public boolean canFile() {
		return file_priv;
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

	public boolean canProcess() {
		return process_priv;
	}

	public boolean canReference() {
		return references_priv;
	}

	public boolean canReload() {
		return reload_priv;
	}

	public boolean canSelect() {
		return select_priv;
	}

	public boolean canShutdown() {
		return shutdown_priv;
	}

	public boolean canUpdate() {
		return update_priv;
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
		if(isSpecial()) throw new SQLException("Refusing to disable special MySQL user: " + this);
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.DISABLE, Table.TableID.MYSQL_USERS, dl.getPkey(), pkey);
	}

	@Override
	public void enable() throws IOException, SQLException {
		if(isSpecial()) throw new SQLException("Refusing to enable special MySQL user: " + this);
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.ENABLE, Table.TableID.MYSQL_USERS, pkey);
	}

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_USERNAME: return pkey;
			case 1: return select_priv;
			case 2: return insert_priv;
			case 3: return update_priv;
			case 4: return delete_priv;
			case 5: return create_priv;
			case 6: return drop_priv;
			case 7: return reload_priv;
			case 8: return shutdown_priv;
			case 9: return process_priv;
			case 10: return file_priv;
			case 11: return grant_priv;
			case 12: return references_priv;
			case 13: return index_priv;
			case 14: return alter_priv;
			case 15: return show_db_priv;
			case 16: return super_priv;
			case 17: return create_tmp_table_priv;
			case 18: return lock_tables_priv;
			case 19: return execute_priv;
			case 20: return repl_slave_priv;
			case 21: return repl_client_priv;
			case 22: return create_view_priv;
			case 23: return show_view_priv;
			case 24: return create_routine_priv;
			case 25: return alter_routine_priv;
			case 26: return create_user_priv;
			case 27: return event_priv;
			case 28: return trigger_priv;
			case 29: return getDisableLog_id();
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	@Override
	public boolean isDisabled() {
		return disable_log!=-1;
	}

	public Integer getDisableLog_id() {
		return disable_log == -1 ? null : disable_log;
	}

	@Override
	public DisableLog getDisableLog() throws SQLException, IOException {
		if(disable_log == -1) return null;
		DisableLog obj = table.getConnector().getAccount().getDisableLog().get(disable_log);
		if(obj == null) throw new SQLException("Unable to find DisableLog: " + disable_log);
		return obj;
	}

	public UserServer getMySQLServerUser(Server mysqlServer) throws IOException, SQLException {
		return table.getConnector().getMysql().getUserServer().getMySQLServerUser(pkey, mysqlServer);
	}

	public List<UserServer> getMySQLServerUsers() throws IOException, SQLException {
		return table.getConnector().getMysql().getUserServer().getMySQLServerUsers(this);
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.MYSQL_USERS;
	}

	public Name getUsername_id() {
		return pkey;
	}
	public com.aoindustries.aoserv.client.account.User getUsername() throws SQLException, IOException {
		com.aoindustries.aoserv.client.account.User obj=table.getConnector().getAccount().getUser().get(pkey);
		if(obj==null) throw new SQLException("Unable to find Username: "+pkey);
		return obj;
	}

	public boolean isSpecial() {
		return isSpecial(pkey);
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			pkey = Name.valueOf(result.getString(1));
			select_priv=result.getBoolean(2);
			insert_priv=result.getBoolean(3);
			update_priv=result.getBoolean(4);
			delete_priv=result.getBoolean(5);
			create_priv=result.getBoolean(6);
			drop_priv=result.getBoolean(7);
			reload_priv=result.getBoolean(8);
			shutdown_priv=result.getBoolean(9);
			process_priv=result.getBoolean(10);
			file_priv=result.getBoolean(11);
			grant_priv=result.getBoolean(12);
			references_priv=result.getBoolean(13);
			index_priv=result.getBoolean(14);
			alter_priv=result.getBoolean(15);
			show_db_priv=result.getBoolean(16);
			super_priv=result.getBoolean(17);
			create_tmp_table_priv=result.getBoolean(18);
			lock_tables_priv=result.getBoolean(19);
			execute_priv=result.getBoolean(20);
			repl_slave_priv=result.getBoolean(21);
			repl_client_priv=result.getBoolean(22);
			create_view_priv=result.getBoolean(23);
			show_view_priv=result.getBoolean(24);
			create_routine_priv=result.getBoolean(25);
			alter_routine_priv=result.getBoolean(26);
			create_user_priv=result.getBoolean(27);
			event_priv=result.getBoolean(28);
			trigger_priv=result.getBoolean(29);
			disable_log = result.getInt(30);
			if(result.wasNull()) disable_log = -1;
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
		try {
			pkey = Name.valueOf(in.readUTF()).intern();
			select_priv=in.readBoolean();
			insert_priv=in.readBoolean();
			update_priv=in.readBoolean();
			delete_priv=in.readBoolean();
			create_priv=in.readBoolean();
			drop_priv=in.readBoolean();
			reload_priv=in.readBoolean();
			shutdown_priv=in.readBoolean();
			process_priv=in.readBoolean();
			file_priv=in.readBoolean();
			grant_priv=in.readBoolean();
			references_priv=in.readBoolean();
			index_priv=in.readBoolean();
			alter_priv=in.readBoolean();
			show_db_priv=in.readBoolean();
			super_priv=in.readBoolean();
			create_tmp_table_priv=in.readBoolean();
			lock_tables_priv=in.readBoolean();
			execute_priv=in.readBoolean();
			repl_slave_priv=in.readBoolean();
			repl_client_priv=in.readBoolean();
			create_view_priv=in.readBoolean();
			show_view_priv=in.readBoolean();
			create_routine_priv=in.readBoolean();
			alter_routine_priv=in.readBoolean();
			create_user_priv=in.readBoolean();
			event_priv=in.readBoolean();
			trigger_priv=in.readBoolean();
			disable_log = in.readCompressedInt();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public List<CannotRemoveReason<User>> getCannotRemoveReasons() {
		List<CannotRemoveReason<User>> reasons=new ArrayList<>();
		if(isSpecial()) {
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
		if(isSpecial()) throw new SQLException("Refusing to remove special MySQL user: " + this);
		table.getConnector().requestUpdateIL(
			true,
			AoservProtocol.CommandID.REMOVE,
			Table.TableID.MYSQL_USERS,
			pkey
		);
	}

	@Override
	public void setPassword(String password) throws IOException, SQLException {
		for(UserServer user : getMySQLServerUsers()) {
			user.setPassword(password);
		}
	}

	@Override
	public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeUTF(pkey.toString());
		out.writeBoolean(select_priv);
		out.writeBoolean(insert_priv);
		out.writeBoolean(update_priv);
		out.writeBoolean(delete_priv);
		out.writeBoolean(create_priv);
		out.writeBoolean(drop_priv);
		out.writeBoolean(reload_priv);
		out.writeBoolean(shutdown_priv);
		out.writeBoolean(process_priv);
		out.writeBoolean(file_priv);
		out.writeBoolean(grant_priv);
		out.writeBoolean(references_priv);
		out.writeBoolean(index_priv);
		out.writeBoolean(alter_priv);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_111)>=0) {
			out.writeBoolean(show_db_priv);
			out.writeBoolean(super_priv);
			out.writeBoolean(create_tmp_table_priv);
			out.writeBoolean(lock_tables_priv);
			out.writeBoolean(execute_priv);
			out.writeBoolean(repl_slave_priv);
			out.writeBoolean(repl_client_priv);
		}
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_4)>=0) {
			out.writeBoolean(create_view_priv);
			out.writeBoolean(show_view_priv);
			out.writeBoolean(create_routine_priv);
			out.writeBoolean(alter_routine_priv);
			out.writeBoolean(create_user_priv);
		}
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_54)>=0) {
			out.writeBoolean(event_priv);
			out.writeBoolean(trigger_priv);
		}
		out.writeCompressedInt(disable_log);
	}

	@Override
	public boolean canSetPassword() {
		return !isDisabled() && !isSpecial();
	}
}
