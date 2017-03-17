/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2001-2009, 2016, 2017  AO Industries, Inc.
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

import com.aoindustries.aoserv.client.validator.PostgresUserId;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.util.IntList;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A <code>PostgresServerUser</code> grants a <code>PostgresUser</code>
 * access to a <code>Server</code>.
 *
 * @see  PostgresUser
 * @see  PostgresServer
 *
 * @author  AO Industries, Inc.
 */
final public class PostgresServerUser extends CachedObjectIntegerKey<PostgresServerUser> implements Removable, PasswordProtected, Disablable {

	static final int
		COLUMN_PKEY=0,
		COLUMN_USERNAME=1,
		COLUMN_POSTGRES_SERVER=2
	;
	static final String COLUMN_USERNAME_name = "username";
	static final String COLUMN_POSTGRES_SERVER_name = "postgres_server";

	String username;
	int postgres_server;
	int disable_log;
	private String predisable_password;

	@Override
	public int arePasswordsSet() throws IOException, SQLException {
		return table.connector.requestBooleanQuery(true, AOServProtocol.CommandID.IS_POSTGRES_SERVER_USER_PASSWORD_SET, pkey)
			?PasswordProtected.ALL
			:PasswordProtected.NONE
		;
	}

	@Override
	public boolean canDisable() {
		return disable_log==-1;
	}

	@Override
	public boolean canEnable() throws SQLException, IOException {
		DisableLog dl=getDisableLog();
		if(dl==null) return false;
		else return dl.canEnable() && getPostgresUser().disable_log==-1;
	}

	@Override
	public List<PasswordChecker.Result> checkPassword(String password) throws IOException {
		try {
			return PostgresUser.checkPassword(PostgresUserId.valueOf(username), password);
		} catch(ValidationException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/*public String checkPasswordDescribe(String password) {
		return PostgresUser.checkPasswordDescribe(username, password);
	}*/

	@Override
	public void disable(DisableLog dl) throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.DISABLE, SchemaTable.TableID.POSTGRES_SERVER_USERS, dl.pkey, pkey);
	}

	@Override
	public void enable() throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.ENABLE, SchemaTable.TableID.POSTGRES_SERVER_USERS, pkey);
	}

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_USERNAME: return username;
			case COLUMN_POSTGRES_SERVER: return postgres_server;
			case 3: return disable_log==-1?null:disable_log;
			case 4: return predisable_password;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	@Override
	public boolean isDisabled() {
		return disable_log!=-1;
	}

	@Override
	public DisableLog getDisableLog() throws IOException, SQLException {
		if(disable_log==-1) return null;
		DisableLog obj=table.connector.getDisableLogs().get(disable_log);
		if(obj==null) throw new SQLException("Unable to find DisableLog: "+disable_log);
		return obj;
	}

	public List<PostgresDatabase> getPostgresDatabases() throws IOException, SQLException {
		return table.connector.getPostgresDatabases().getPostgresDatabases(this);
	}

	public PostgresUser getPostgresUser() throws SQLException, IOException {
		PostgresUser obj=table.connector.getPostgresUsers().get(username);
		if(obj==null) throw new SQLException("Unable to find PostgresUser: "+username);
		return obj;
	}

	public String getPredisablePassword() {
		return predisable_password;
	}

	public PostgresServer getPostgresServer() throws IOException, SQLException{
		// May be filtered
		return table.connector.getPostgresServers().get(postgres_server);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.POSTGRES_SERVER_USERS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey=result.getInt(1);
		username=result.getString(2);
		postgres_server=result.getInt(3);
		disable_log=result.getInt(4);
		if(result.wasNull()) disable_log=-1;
		predisable_password=result.getString(5);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readCompressedInt();
		username=in.readUTF().intern();
		postgres_server=in.readCompressedInt();
		disable_log=in.readCompressedInt();
		predisable_password=in.readNullUTF();
	}

	@Override
	public List<CannotRemoveReason> getCannotRemoveReasons() throws SQLException, IOException {
		List<CannotRemoveReason> reasons=new ArrayList<>();

		if(username.equals(PostgresUser.POSTGRES)) reasons.add(new CannotRemoveReason<>("Not allowed to remove the "+PostgresUser.POSTGRES+" PostgreSQL user", this));

		for(PostgresDatabase pd : getPostgresDatabases()) {
			PostgresServer ps=pd.getPostgresServer();
			reasons.add(new CannotRemoveReason<>("Used by PostgreSQL database "+pd.getName()+" on "+ps.getName()+" on "+ps.getAOServer().getHostname(), pd));
		}

		return reasons;
	}

	@Override
	public void remove() throws IOException, SQLException {
		table.connector.requestUpdateIL(
			true,
			AOServProtocol.CommandID.REMOVE,
			SchemaTable.TableID.POSTGRES_SERVER_USERS,
			pkey
		);
	}

	@Override
	public void setPassword(final String password) throws IOException, SQLException {
		AOServConnector connector=table.connector;
		if(!connector.isSecure()) throw new IOException("Passwords for PostgreSQL users may only be set when using secure protocols.  Currently using the "+connector.getProtocol()+" protocol, which is not secure.");

		connector.requestUpdate(
			true,
			new AOServConnector.UpdateRequest() {
				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(AOServProtocol.CommandID.SET_POSTGRES_SERVER_USER_PASSWORD.ordinal());
					out.writeCompressedInt(pkey);
					out.writeBoolean(password!=null); if(password!=null) out.writeUTF(password);
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
			new AOServConnector.UpdateRequest() {
				IntList invalidateList;
				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(AOServProtocol.CommandID.SET_POSTGRES_SERVER_USER_PREDISABLE_PASSWORD.ordinal());
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
		return username+" on "+getPostgresServer().toStringImpl();
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeUTF(username);
		out.writeCompressedInt(postgres_server);
		if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_130)<=0) {
			out.writeCompressedInt(-1);
		}
		out.writeCompressedInt(disable_log);
		out.writeNullUTF(predisable_password);
	}

	@Override
	public boolean canSetPassword() throws SQLException, IOException {
		return disable_log==-1 && getPostgresUser().canSetPassword();
	}
}
