/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2009, 2016, 2017, 2018, 2019, 2020, 2021  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.postgresql;

import com.aoindustries.aoserv.client.CannotRemoveReason;
import com.aoindustries.aoserv.client.Disablable;
import com.aoindustries.aoserv.client.Removable;
import com.aoindustries.aoserv.client.account.DisableLog;
import com.aoindustries.aoserv.client.password.PasswordChecker;
import com.aoindustries.aoserv.client.password.PasswordProtected;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.i18n.Resources;
import com.aoindustries.io.FastExternalizable;
import com.aoindustries.io.stream.StreamableInput;
import com.aoindustries.io.stream.StreamableOutput;
import com.aoindustries.validation.InvalidResult;
import com.aoindustries.validation.ValidResult;
import com.aoindustries.validation.ValidationException;
import com.aoindustries.validation.ValidationResult;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A <code>PostgresUser</code> may have access to multiple servers.  The information
 * common to all servers is contained in <code>PostgresUser</code>.
 *
 * @see  UserServer
 *
 * @author  AO Industries, Inc.
 */
final public class User extends CachedObjectUserNameKey<User> implements Removable, PasswordProtected, Disablable {

	private static final Resources RESOURCES = Resources.getResources(User.class);

	/**
	 * Represents a PostgreSQL user ID.  PostgreSQL user ids must:
	 * <ul>
	 *   <li>Be non-null</li>
	 *   <li>Be non-empty</li>
	 *   <li>Be between 1 and 31 characters</li>
	 *   <li>Must start with <code>[a-z]</code></li>
	 *   <li>The rest of the characters may contain [a-z], [0-9], and underscore (_)</li>
	 *   <li>Must be a valid {@link com.aoindustries.aoserv.client.linux.User.Name} - this is implied by the above rules</li>
	 * </ul>
	 *
	 * @author  AO Industries, Inc.
	 */
	final static public class Name extends com.aoindustries.aoserv.client.linux.User.Name implements
		FastExternalizable
	{

		/**
		 * The maximum length of a PostgreSQL username.
		 */
		public static final int POSTGRESQL_NAME_MAX_LENGTH = 31;

		/**
		 * Validates a PostgreSQL user id.
		 */
		public static ValidationResult validate(String id) {
			if(id==null) return new InvalidResult(RESOURCES, "Name.validate.isNull");
			int len = id.length();
			if(len==0) return new InvalidResult(RESOURCES, "Name.validate.isEmpty");
			if(len > POSTGRESQL_NAME_MAX_LENGTH) return new InvalidResult(RESOURCES, "Name.validate.tooLong", POSTGRESQL_NAME_MAX_LENGTH, len);

			// The first character must be [a-z] or [0-9]
			char ch = id.charAt(0);
			if(
				(ch < 'a' || ch > 'z')
				&& (ch<'0' || ch>'9')
			) return new InvalidResult(RESOURCES, "Name.validate.startAtoZor0to9");

			// The rest may have additional characters
			for (int c = 1; c < len; c++) {
				ch = id.charAt(c);
				if (
					(ch<'a' || ch>'z')
					&& (ch<'0' || ch>'9')
					&& ch!='_'
				) return new InvalidResult(RESOURCES, "Name.validate.illegalCharacter");
			}
			assert com.aoindustries.aoserv.client.linux.User.Name.validate(id).isValid() : "A PostgreSQL User.Name is always a valid Linux User.Name.";
			return ValidResult.getInstance();
		}

		private static final ConcurrentMap<String,Name> interned = new ConcurrentHashMap<>();

		/**
		 * @param id  when {@code null}, returns {@code null}
		 */
		public static Name valueOf(String id) throws ValidationException {
			if(id == null) return null;
			//Name existing = interned.get(id);
			//return existing!=null ? existing : new Name(id);
			return new Name(id, true);
		}

		private Name(String id, boolean validate) throws ValidationException {
			super(id, validate);
		}

		/**
		 * @param  id  Does not validate, should only be used with a known valid value.
		 */
		private Name(String id) {
			super(id);
		}

		@Override
		protected void validate() throws ValidationException {
			ValidationResult result = validate(name);
			if(!result.isValid()) throw new ValidationException(result);
		}

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
		public com.aoindustries.aoserv.client.dto.PostgresUserName getDto() {
			return new com.aoindustries.aoserv.client.dto.PostgresUserName(name);
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
	 * The maximum length of a PostgreSQL username.
	 *
	 * @deprecated  Please use {@link Name#MAX_LENGTH} instead.
	 */
	@Deprecated
	public static final int MAX_USERNAME_LENGTH = Name.POSTGRESQL_NAME_MAX_LENGTH;

	/**
	 * The username of the PostgreSQL special users.
	 */
	public static final Name
		/** Super user */
		POSTGRES,
		/** System roles, PostgreSQL 10+ */
		PG_MONITOR,
		PG_READ_ALL_SETTINGS,
		PG_READ_ALL_STATS,
		PG_SIGNAL_BACKEND,
		PG_STAT_SCAN_TABLES,
		/** System roles, PostgreSQL 11+ */
		PG_EXECUTE_SERVER_PROGRAM,
		PG_READ_SERVER_FILES,
		PG_WRITE_SERVER_FILES,
		/** Monitoring */
		POSTGRESMON,
		/** AO Admin */
		AOADMIN,
		/** AO Platform Components */
		AOSERV_APP,
		AOWEB_APP;

	static {
		try {
			// Super user
			POSTGRES = Name.valueOf("postgres");
			// System roles, PostgreSQL 10+
			PG_MONITOR = Name.valueOf("pg_monitor");
			PG_READ_ALL_SETTINGS = Name.valueOf("pg_read_all_settings");
			PG_READ_ALL_STATS = Name.valueOf("pg_read_all_stats");
			PG_SIGNAL_BACKEND = Name.valueOf("pg_signal_backend");
			PG_STAT_SCAN_TABLES = Name.valueOf("pg_stat_scan_tables");
			// System roles, PostgreSQL 11+
			PG_EXECUTE_SERVER_PROGRAM = Name.valueOf("pg_execute_server_program");
			PG_READ_SERVER_FILES = Name.valueOf("pg_read_server_files");
			PG_WRITE_SERVER_FILES = Name.valueOf("pg_write_server_files");
			// Monitoring
			POSTGRESMON = Name.valueOf("postgresmon");
			// AO Admin
			AOADMIN = Name.valueOf("aoadmin");
			// AO Platform Components
			AOSERV_APP = Name.valueOf("aoserv_app");
			AOWEB_APP = Name.valueOf("aoweb_app");
		} catch(ValidationException e) {
			throw new AssertionError("These hard-coded values are valid", e);
		}
	}

	/**
	 * Special PostgreSQL users may not be added or removed.
	 */
	public static boolean isSpecial(Name username) {
		return
			// Super user
			username.equals(POSTGRES)
			// System roles, PostgreSQL 10+
			|| username.equals(PG_MONITOR)
			|| username.equals(PG_READ_ALL_SETTINGS)
			|| username.equals(PG_READ_ALL_STATS)
			|| username.equals(PG_SIGNAL_BACKEND)
			|| username.equals(PG_STAT_SCAN_TABLES)
			// System roles, PostgreSQL 11+
			|| username.equals(PG_EXECUTE_SERVER_PROGRAM)
			|| username.equals(PG_READ_SERVER_FILES)
			|| username.equals(PG_WRITE_SERVER_FILES)
			// Monitoring
			|| username.equals(POSTGRESMON)
			// AO Admin
			|| username.equals(AOADMIN)
			// AO Platform Components
			|| username.equals(AOSERV_APP)
			|| username.equals(AOWEB_APP);
	}

	/**
	 * A password may be set to null, which means that the account will
	 * be disabled.
	 */
	public static final String NO_PASSWORD=null;

	public static final String NO_PASSWORD_DB_VALUE="";

	private boolean
		createdb,
		trace,
		superPriv,
		catupd
	;
	private int disable_log;

	public int addPostgresServerUser(Server postgresServer) throws IOException, SQLException {
		return table.getConnector().getPostgresql().getUserServer().addPostgresServerUser(pkey, postgresServer);
	}

	@Override
	public int arePasswordsSet() throws IOException, SQLException {
		if(isSpecial()) throw new SQLException("Refusing to check if passwords set on special PostgreSQL user: " + this);
		return com.aoindustries.aoserv.client.account.User.groupPasswordsSet(getPostgresServerUsers());
	}

	public boolean canCatUPD() {
		return catupd;
	}

	public boolean canCreateDB() {
		return createdb;
	}

	@Override
	public boolean canDisable() throws IOException, SQLException {
		if(isDisabled() || isSpecial()) return false;
		for(UserServer psu : getPostgresServerUsers()) {
			if(!psu.isDisabled()) return false;
		}
		return true;
	}

	@Override
	public boolean canEnable() throws SQLException, IOException {
		if(isSpecial()) return false;
		DisableLog dl = getDisableLog();
		if(dl == null) return false;
		else return dl.canEnable() && !getUsername().isDisabled();
	}

	public boolean canTrace() {
		return trace;
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
		if(isSpecial()) throw new SQLException("Refusing to disable special PostgreSQL user: " + this);
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.DISABLE, Table.TableID.POSTGRES_USERS, dl.getPkey(), pkey);
	}

	@Override
	public void enable() throws IOException, SQLException {
		if(isSpecial()) throw new SQLException("Refusing to enable special PostgreSQL user: " + this);
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.ENABLE, Table.TableID.POSTGRES_USERS, pkey);
	}

	@Override
	protected Object getColumnImpl(int i) {
		if(i==COLUMN_USERNAME) return pkey;
		if(i==1) return createdb;
		if(i==2) return trace;
		if(i==3) return superPriv;
		if(i==4) return catupd;
		if(i==5) return getDisableLog_id();
		throw new IllegalArgumentException("Invalid index: " + i);
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
		if(disable_log==-1) return null;
		DisableLog obj=table.getConnector().getAccount().getDisableLog().get(disable_log);
		if(obj==null) throw new SQLException("Unable to find DisableLog: "+disable_log);
		return obj;
	}

	public UserServer getPostgresServerUser(Server server) throws IOException, SQLException {
		return server.getPostgresServerUser(pkey);
	}

	public List<UserServer> getPostgresServerUsers() throws IOException, SQLException {
		return table.getConnector().getPostgresql().getUserServer().getPostgresServerUsers(this);
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.POSTGRES_USERS;
	}

	public Name getUsername_username_id() {
		return pkey;
	}

	public com.aoindustries.aoserv.client.account.User getUsername() throws SQLException, IOException {
		com.aoindustries.aoserv.client.account.User username=table.getConnector().getAccount().getUser().get(this.pkey);
		if(username==null) throw new SQLException("Unable to find Username: "+this.pkey);
		return username;
	}

	public boolean isSpecial() {
		return isSpecial(pkey);
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			pkey = Name.valueOf(result.getString(1));
			createdb=result.getBoolean(2);
			trace=result.getBoolean(3);
			superPriv=result.getBoolean(4);
			catupd=result.getBoolean(5);
			disable_log=result.getInt(6);
			if(result.wasNull()) disable_log=-1;
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	public boolean isDatabaseAdmin() {
		return superPriv;
	}

	@Override
	public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
		try {
			pkey = Name.valueOf(in.readUTF()).intern();
			createdb=in.readBoolean();
			trace=in.readBoolean();
			superPriv=in.readBoolean();
			catupd=in.readBoolean();
			disable_log=in.readCompressedInt();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public List<CannotRemoveReason<?>> getCannotRemoveReasons() throws SQLException, IOException {
		List<CannotRemoveReason<?>> reasons=new ArrayList<>();
		if(isSpecial()) {
			reasons.add(
				new CannotRemoveReason<>(
					"Not allowed to remove a special PostgreSQL user: " + pkey,
					this
				)
			);
		}
		for(UserServer psu : getPostgresServerUsers()) {
			reasons.addAll(psu.getCannotRemoveReasons());
		}
		return reasons;
	}

	@Override
	public void remove() throws IOException, SQLException {
		if(isSpecial()) throw new SQLException("Refusing to remove special PostgreSQL user: " + this);
		table.getConnector().requestUpdateIL(
			true,
			AoservProtocol.CommandID.REMOVE,
			Table.TableID.POSTGRES_USERS,
			pkey
		);
	}

	@Override
	public void setPassword(String password) throws IOException, SQLException {
		for(UserServer user : getPostgresServerUsers()) {
			if(user.canSetPassword()) user.setPassword(password);
		}
	}

	@Override
	public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeUTF(pkey.toString());
		out.writeBoolean(createdb);
		out.writeBoolean(trace);
		out.writeBoolean(superPriv);
		out.writeBoolean(catupd);
		out.writeCompressedInt(disable_log);
	}

	@Override
	public boolean canSetPassword() {
		return !isDisabled() && !isSpecial();
	}
}
