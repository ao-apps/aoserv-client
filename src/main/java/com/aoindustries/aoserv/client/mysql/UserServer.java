/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2013, 2016, 2017, 2018  AO Industries, Inc.
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

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.CannotRemoveReason;
import com.aoindustries.aoserv.client.Disablable;
import com.aoindustries.aoserv.client.Removable;
import com.aoindustries.aoserv.client.account.DisableLog;
import com.aoindustries.aoserv.client.password.PasswordChecker;
import com.aoindustries.aoserv.client.password.PasswordProtected;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.validator.MySQLUserId;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.util.IntList;
import com.aoindustries.util.InternUtils;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A <code>MySQLServerUser</code> grants a <code>MySQLUser</code> access
 * to an <code>AOServer</code>.  Once access is granted to the <code>Server</code>,
 * access may then be granted to individual <code>MySQLDatabase</code>s via
 * <code>MySQLDBUser</code>s.
 *
 * @see  User
 * @see  MySQLDatabase
 * @see  DatabaseUser
 * @see  Server
 *
 * @author  AO Industries, Inc.
 */
final public class UserServer extends CachedObjectIntegerKey<UserServer> implements Removable, PasswordProtected, Disablable {

	static final int
		COLUMN_PKEY=0,
		COLUMN_USERNAME=1,
		COLUMN_MYSQL_SERVER=2
	;
	static final String COLUMN_USERNAME_name = "username";
	static final String COLUMN_MYSQL_SERVER_name = "mysql_server";

	public static final int
		UNLIMITED_QUESTIONS=0,
		DEFAULT_MAX_QUESTIONS=UNLIMITED_QUESTIONS
	;

	public static final int
		UNLIMITED_UPDATES=0,
		DEFAULT_MAX_UPDATES=UNLIMITED_UPDATES
	;

	public static final int
		UNLIMITED_CONNECTIONS=0,
		DEFAULT_MAX_CONNECTIONS=UNLIMITED_CONNECTIONS
	;

	public static final int
		UNLIMITED_USER_CONNECTIONS=0,
		DEFAULT_MAX_USER_CONNECTIONS=UNLIMITED_USER_CONNECTIONS
	;

	public static final int MAX_HOST_LENGTH=60;

	/**
	 * Convenience constants for the most commonly used host values.
	 */
	public static final String
		ANY_HOST="%",
		ANY_LOCAL_HOST=null
	;

	MySQLUserId username;
	int mysql_server;
	String host;
	int disable_log;
	private String predisable_password;
	int max_questions;
	int max_updates;
	int max_connections;
	int max_user_connections;

	@Override
	public int arePasswordsSet() throws IOException, SQLException {
		return table.getConnector().requestBooleanQuery(true, AoservProtocol.CommandID.IS_MYSQL_SERVER_USER_PASSWORD_SET, pkey)?PasswordProtected.ALL:PasswordProtected.NONE;
	}

	@Override
	public boolean canDisable() {
		return disable_log==-1;
	}

	@Override
	public boolean canEnable() throws SQLException, IOException {
		DisableLog dl=getDisableLog();
		if(dl==null) return false;
		else return dl.canEnable() && !getMySQLUser().isDisabled();
	}

	@Override
	public List<PasswordChecker.Result> checkPassword(String password) throws IOException {
		return User.checkPassword(username, password);
	}
	/*
	public String checkPasswordDescribe(String password) {
		return MySQLUser.checkPasswordDescribe(username, password);
	}
	*/
	@Override
	public void disable(DisableLog dl) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.DISABLE, Table.TableID.MYSQL_SERVER_USERS, dl.getPkey(), pkey);
	}

	@Override
	public void enable() throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.ENABLE, Table.TableID.MYSQL_SERVER_USERS, pkey);
	}

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_USERNAME: return username;
			case COLUMN_MYSQL_SERVER: return mysql_server;
			case 3: return host;
			case 4: return disable_log==-1?null:disable_log;
			case 5: return predisable_password;
			case 6: return max_questions;
			case 7: return max_updates;
			case 8: return max_connections;
			case 9: return max_user_connections;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
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

	public String getHost() {
		return host;
	}

	public List<DatabaseUser> getMySQLDBUsers() throws IOException, SQLException {
		return table.getConnector().getMysql().getMysqlDBUsers().getMySQLDBUsers(this);
	}

	public User getMySQLUser() throws SQLException, IOException {
		User obj=table.getConnector().getMysql().getMysqlUsers().get(username);
		if(obj==null) throw new SQLException("Unable to find MySQLUser: "+username);
		return obj;
	}

	public String getPredisablePassword() {
		return predisable_password;
	}

	public int getMaxQuestions() {
		return max_questions;
	}

	public int getMaxUpdates() {
		return max_updates;
	}

	public int getMaxConnections() {
		return max_connections;
	}

	public int getMaxUserConnections() {
		return max_user_connections;
	}

	public Server getMySQLServer() throws IOException, SQLException{
		// May be filtered
		return table.getConnector().getMysql().getMysqlServers().get(mysql_server);
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.MYSQL_SERVER_USERS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			pkey=result.getInt(1);
			username = MySQLUserId.valueOf(result.getString(2));
			mysql_server=result.getInt(3);
			host=result.getString(4);
			disable_log=result.getInt(5);
			if(result.wasNull()) disable_log=-1;
			predisable_password=result.getString(6);
			max_questions=result.getInt(7);
			max_updates=result.getInt(8);
			max_connections=result.getInt(9);
			max_user_connections=result.getInt(10);
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			pkey=in.readCompressedInt();
			username = MySQLUserId.valueOf(in.readUTF()).intern();
			mysql_server=in.readCompressedInt();
			host=InternUtils.intern(in.readNullUTF());
			disable_log=in.readCompressedInt();
			predisable_password=in.readNullUTF();
			max_questions=in.readCompressedInt();
			max_updates=in.readCompressedInt();
			max_connections=in.readCompressedInt();
			max_user_connections=in.readCompressedInt();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public List<CannotRemoveReason<UserServer>> getCannotRemoveReasons() {
		List<CannotRemoveReason<UserServer>> reasons=new ArrayList<>();
		if(
			username.equals(User.ROOT)
			|| username.equals(User.MYSQL_SESSION)
			|| username.equals(User.MYSQL_SYS)
		) {
			reasons.add(new CannotRemoveReason<>("Not allowed to remove the " + username + " MySQL user", this));
		}
		return reasons;
	}

	@Override
	public void remove() throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true,
			AoservProtocol.CommandID.REMOVE,
			Table.TableID.MYSQL_SERVER_USERS,
			pkey
		);
	}

	@Override
	public void setPassword(final String password) throws IOException, SQLException {
		AOServConnector connector=table.getConnector();
		if(!connector.isSecure()) throw new IOException("Passwords for MySQL users may only be set when using secure protocols.  Currently using the "+connector.getProtocol()+" protocol, which is not secure.");

		connector.requestUpdate(true,
			AoservProtocol.CommandID.SET_MYSQL_SERVER_USER_PASSWORD,
			new AOServConnector.UpdateRequest() {
			@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(pkey);
					out.writeNullUTF(password);
				}

			@Override
				public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
					int code=in.readByte();
					if(code!=AoservProtocol.DONE) {
						AoservProtocol.checkResult(code, in);
						throw new IOException("Unexpected response code: "+code);
					}
				}

			@Override
				public void afterRelease() {
				}
			}
		);
	}

	public void setPredisablePassword(final String password) throws IOException, SQLException {
		table.getConnector().requestUpdate(true,
			AoservProtocol.CommandID.SET_MYSQL_SERVER_USER_PREDISABLE_PASSWORD,
			new AOServConnector.UpdateRequest() {
				IntList invalidateList;

				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(pkey);
					out.writeNullUTF(password);
				}

				@Override
				public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AoservProtocol.DONE) invalidateList=AOServConnector.readInvalidateList(in);
					else {
						AoservProtocol.checkResult(code, in);
						throw new IOException("Unexpected response code: "+code);
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
		return username+" on "+getMySQLServer().toStringImpl();
	}

	@Override
	public void write(CompressedDataOutputStream out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeUTF(username.toString());
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_4)<0) out.writeCompressedInt(-1);
		else out.writeCompressedInt(mysql_server);
		out.writeNullUTF(host);
		out.writeCompressedInt(disable_log);
		out.writeNullUTF(predisable_password);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_4)>=0) {
			out.writeCompressedInt(max_questions);
			out.writeCompressedInt(max_updates);
		}
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_111)>=0) out.writeCompressedInt(max_connections);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_4)>=0) out.writeCompressedInt(max_user_connections);
	}

	@Override
	public boolean canSetPassword() throws SQLException, IOException {
		return disable_log==-1 && getMySQLUser().canSetPassword();
	}
}
