/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2000-2013, 2016, 2017  AO Industries, Inc.
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
 * @see  MySQLUser
 * @see  MySQLDatabase
 * @see  MySQLDBUser
 * @see  MySQLServer
 *
 * @author  AO Industries, Inc.
 */
final public class MySQLServerUser extends CachedObjectIntegerKey<MySQLServerUser> implements Removable, PasswordProtected, Disablable {

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
		return table.connector.requestBooleanQuery(true, AOServProtocol.CommandID.IS_MYSQL_SERVER_USER_PASSWORD_SET, pkey)?PasswordProtected.ALL:PasswordProtected.NONE;
	}

	@Override
	public boolean canDisable() {
		return disable_log==-1;
	}

	@Override
	public boolean canEnable() throws SQLException, IOException {
		DisableLog dl=getDisableLog();
		if(dl==null) return false;
		else return dl.canEnable() && getMySQLUser().disable_log==-1;
	}

	@Override
	public List<PasswordChecker.Result> checkPassword(String password) throws IOException {
		return MySQLUser.checkPassword(username, password);
	}
	/*
	public String checkPasswordDescribe(String password) {
		return MySQLUser.checkPasswordDescribe(username, password);
	}
	*/
	@Override
	public void disable(DisableLog dl) throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.DISABLE, SchemaTable.TableID.MYSQL_SERVER_USERS, dl.pkey, pkey);
	}

	@Override
	public void enable() throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.ENABLE, SchemaTable.TableID.MYSQL_SERVER_USERS, pkey);
	}

	@Override
	Object getColumnImpl(int i) {
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
		DisableLog obj=table.connector.getDisableLogs().get(disable_log);
		if(obj==null) throw new SQLException("Unable to find DisableLog: "+disable_log);
		return obj;
	}

	public String getHost() {
		return host;
	}

	public List<MySQLDBUser> getMySQLDBUsers() throws IOException, SQLException {
		return table.connector.getMysqlDBUsers().getMySQLDBUsers(this);
	}

	public MySQLUser getMySQLUser() throws SQLException, IOException {
		MySQLUser obj=table.connector.getMysqlUsers().get(username);
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

	public MySQLServer getMySQLServer() throws IOException, SQLException{
		// May be filtered
		return table.connector.getMysqlServers().get(mysql_server);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.MYSQL_SERVER_USERS;
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
	public List<CannotRemoveReason<MySQLServerUser>> getCannotRemoveReasons() {
		List<CannotRemoveReason<MySQLServerUser>> reasons=new ArrayList<>();
		if(username.equals(MySQLUser.ROOT)) reasons.add(new CannotRemoveReason<>("Not allowed to remove the "+MySQLUser.ROOT+" MySQL user", this));
		return reasons;
	}

	@Override
	public void remove() throws IOException, SQLException {
		table.connector.requestUpdateIL(
			true,
			AOServProtocol.CommandID.REMOVE,
			SchemaTable.TableID.MYSQL_SERVER_USERS,
			pkey
		);
	}

	@Override
	public void setPassword(final String password) throws IOException, SQLException {
		AOServConnector connector=table.connector;
		if(!connector.isSecure()) throw new IOException("Passwords for MySQL users may only be set when using secure protocols.  Currently using the "+connector.getProtocol()+" protocol, which is not secure.");

		connector.requestUpdate(
			true,
			AOServProtocol.CommandID.SET_MYSQL_SERVER_USER_PASSWORD,
			new AOServConnector.UpdateRequest() {
			@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(pkey);
					out.writeNullUTF(password);
				}

			@Override
				public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
					int code=in.readByte();
					if(code!=AOServProtocol.DONE) {
						AOServProtocol.checkResult(code, in);
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
		table.connector.requestUpdate(
			true,
			AOServProtocol.CommandID.SET_MYSQL_SERVER_USER_PREDISABLE_PASSWORD,
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
					if(code==AOServProtocol.DONE) invalidateList=AOServConnector.readInvalidateList(in);
					else {
						AOServProtocol.checkResult(code, in);
						throw new IOException("Unexpected response code: "+code);
					}
				}

				@Override
				public void afterRelease() {
					table.connector.tablesUpdated(invalidateList);
				}
			}
		);
	}

	@Override
	String toStringImpl() throws IOException, SQLException {
		return username+" on "+getMySQLServer().toStringImpl();
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeUTF(username.toString());
		if(version.compareTo(AOServProtocol.Version.VERSION_1_4)<0) out.writeCompressedInt(-1);
		else out.writeCompressedInt(mysql_server);
		out.writeNullUTF(host);
		out.writeCompressedInt(disable_log);
		out.writeNullUTF(predisable_password);
		if(version.compareTo(AOServProtocol.Version.VERSION_1_4)>=0) {
			out.writeCompressedInt(max_questions);
			out.writeCompressedInt(max_updates);
		}
		if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_111)>=0) out.writeCompressedInt(max_connections);
		if(version.compareTo(AOServProtocol.Version.VERSION_1_4)>=0) out.writeCompressedInt(max_user_connections);
	}

	@Override
	public boolean canSetPassword() throws SQLException, IOException {
		return disable_log==-1 && getMySQLUser().canSetPassword();
	}
}
