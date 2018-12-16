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
package com.aoindustries.aoserv.client.postgresql;

import com.aoindustries.aoserv.client.CachedObjectPostgresUserIdKey;
import com.aoindustries.aoserv.client.CannotRemoveReason;
import com.aoindustries.aoserv.client.Disablable;
import com.aoindustries.aoserv.client.Removable;
import com.aoindustries.aoserv.client.account.DisableLog;
import com.aoindustries.aoserv.client.account.Username;
import com.aoindustries.aoserv.client.password.PasswordChecker;
import com.aoindustries.aoserv.client.password.PasswordProtected;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.validator.PostgresUserId;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A <code>PostgresUser</code> may have access to multiple servers.  The information
 * common to all servers is contained in <code>PostgresUser</code>.
 *
 * @see  UserServer
 *
 * @author  AO Industries, Inc.
 */
final public class User extends CachedObjectPostgresUserIdKey<User> implements Removable, PasswordProtected, Disablable {

	static final int COLUMN_USERNAME=0;
	static final String COLUMN_USERNAME_name = "username";

	/**
	 * The maximum length of a PostgreSQL username.
	 *
	 * @deprecated  Please use {@link PostgresUserId#MAX_LENGTH} instead.
	 */
	@Deprecated
	public static final int MAX_USERNAME_LENGTH = PostgresUserId.MAX_LENGTH;

	/**
	 * The username of the PostgreSQL special users.
	 */
	public static final PostgresUserId
		POSTGRES,
		AOADMIN,
		AOSERV_APP,
		AOWEB_APP
	;
	static {
		try {
			POSTGRES = PostgresUserId.valueOf("postgres");
			AOADMIN = PostgresUserId.valueOf("aoadmin");
			AOSERV_APP = PostgresUserId.valueOf("aoserv_app");
			AOWEB_APP = PostgresUserId.valueOf("aoweb_app");
		} catch(ValidationException e) {
			throw new AssertionError("These hard-coded values are valid", e);
		}
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
	int disable_log;

	public int addPostgresServerUser(Server postgresServer) throws IOException, SQLException {
		return table.getConnector().getPostgresql().getPostgresServerUsers().addPostgresServerUser(pkey, postgresServer);
	}

	@Override
	public int arePasswordsSet() throws IOException, SQLException {
		return Username.groupPasswordsSet(getPostgresServerUsers());
	}

	public boolean canCatUPD() {
		return catupd;
	}

	public boolean canCreateDB() {
		return createdb;
	}

	@Override
	public boolean canDisable() throws IOException, SQLException {
		if(disable_log!=-1) return false;
		for(UserServer psu : getPostgresServerUsers()) if(!psu.isDisabled()) return false;
		return true;
	}

	@Override
	public boolean canEnable() throws SQLException, IOException {
		DisableLog dl=getDisableLog();
		if(dl==null) return false;
		else return dl.canEnable() && !getUsername().isDisabled();
	}

	public boolean canTrace() {
		return trace;
	}

	@Override
	public List<PasswordChecker.Result> checkPassword(String password) throws IOException {
		return checkPassword(pkey, password);
	}

	public static List<PasswordChecker.Result> checkPassword(PostgresUserId username, String password) throws IOException {
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
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.DISABLE, Table.TableID.POSTGRES_USERS, dl.getPkey(), pkey);
	}

	@Override
	public void enable() throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.ENABLE, Table.TableID.POSTGRES_USERS, pkey);
	}

	@Override
	protected Object getColumnImpl(int i) {
		if(i==COLUMN_USERNAME) return pkey;
		if(i==1) return createdb;
		if(i==2) return trace;
		if(i==3) return superPriv;
		if(i==4) return catupd;
		if(i==5) return disable_log == -1 ? null : disable_log;
		throw new IllegalArgumentException("Invalid index: "+i);
	}

	@Override
	public boolean isDisabled() {
		return disable_log!=-1;
	}

	@Override
	public DisableLog getDisableLog() throws SQLException, IOException {
		if(disable_log==-1) return null;
		DisableLog obj=table.getConnector().getAccount().getDisableLogs().get(disable_log);
		if(obj==null) throw new SQLException("Unable to find DisableLog: "+disable_log);
		return obj;
	}

	public UserServer getPostgresServerUser(Server server) throws IOException, SQLException {
		return server.getPostgresServerUser(pkey);
	}

	public List<UserServer> getPostgresServerUsers() throws IOException, SQLException {
		return table.getConnector().getPostgresql().getPostgresServerUsers().getPostgresServerUsers(this);
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.POSTGRES_USERS;
	}

	public PostgresUserId getUsername_username_id() {
		return pkey;
	}

	public Username getUsername() throws SQLException, IOException {
		Username username=table.getConnector().getAccount().getUsernames().get(this.pkey);
		if(username==null) throw new SQLException("Unable to find Username: "+this.pkey);
		return username;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			pkey = PostgresUserId.valueOf(result.getString(1));
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
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			pkey = PostgresUserId.valueOf(in.readUTF()).intern();
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
		for(UserServer psu : getPostgresServerUsers()) reasons.addAll(psu.getCannotRemoveReasons());
		return reasons;
	}

	@Override
	public void remove() throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true,
			AoservProtocol.CommandID.REMOVE,
			Table.TableID.POSTGRES_USERS,
			pkey
		);
	}

	@Override
	public void setPassword(String password) throws IOException, SQLException {
		for(UserServer user : getPostgresServerUsers()) if(user.canSetPassword()) user.setPassword(password);
	}

	@Override
	public void write(CompressedDataOutputStream out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeUTF(pkey.toString());
		out.writeBoolean(createdb);
		out.writeBoolean(trace);
		out.writeBoolean(superPriv);
		out.writeBoolean(catupd);
		out.writeCompressedInt(disable_log);
	}

	@Override
	public boolean canSetPassword() {
		return disable_log==-1 && !POSTGRES.equals(pkey);
	}
}
