/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2009, 2016, 2017, 2018, 2019  AO Industries, Inc.
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
 * @see  User
 * @see  Server
 *
 * @author  AO Industries, Inc.
 */
final public class UserServer extends CachedObjectIntegerKey<UserServer> implements Removable, PasswordProtected, Disablable {

	static final int
		COLUMN_PKEY=0,
		COLUMN_USERNAME=1,
		COLUMN_POSTGRES_SERVER=2
	;
	static final String COLUMN_USERNAME_name = "username";
	static final String COLUMN_POSTGRES_SERVER_name = "postgres_server";

	User.Name username;
	int postgres_server;
	int disable_log;
	private String predisable_password;

	@Override
	public int arePasswordsSet() throws IOException, SQLException {
		return table.getConnector().requestBooleanQuery(true, AoservProtocol.CommandID.IS_POSTGRES_SERVER_USER_PASSWORD_SET, pkey)
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
		else return dl.canEnable() && !getPostgresUser().isDisabled();
	}

	@Override
	public List<PasswordChecker.Result> checkPassword(String password) throws IOException {
		return User.checkPassword(username, password);
	}

	/*public String checkPasswordDescribe(String password) {
		return PostgresUser.checkPasswordDescribe(username, password);
	}*/

	@Override
	public void disable(DisableLog dl) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.DISABLE, Table.TableID.POSTGRES_SERVER_USERS, dl.getPkey(), pkey);
	}

	@Override
	public void enable() throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.ENABLE, Table.TableID.POSTGRES_SERVER_USERS, pkey);
	}

	@Override
	protected Object getColumnImpl(int i) {
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
		DisableLog obj=table.getConnector().getAccount().getDisableLog().get(disable_log);
		if(obj==null) throw new SQLException("Unable to find DisableLog: "+disable_log);
		return obj;
	}

	public List<Database> getPostgresDatabases() throws IOException, SQLException {
		return table.getConnector().getPostgresql().getDatabase().getPostgresDatabases(this);
	}

	public User getPostgresUser() throws SQLException, IOException {
		User obj=table.getConnector().getPostgresql().getUser().get(username);
		if(obj==null) throw new SQLException("Unable to find PostgresUser: "+username);
		return obj;
	}

	public String getPredisablePassword() {
		return predisable_password;
	}

	public Server getPostgresServer() throws IOException, SQLException{
		// May be filtered
		return table.getConnector().getPostgresql().getServer().get(postgres_server);
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.POSTGRES_SERVER_USERS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			pkey=result.getInt(1);
			username = User.Name.valueOf(result.getString(2));
			postgres_server=result.getInt(3);
			disable_log=result.getInt(4);
			if(result.wasNull()) disable_log=-1;
			predisable_password=result.getString(5);
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			pkey=in.readCompressedInt();
			username = User.Name.valueOf(in.readUTF()).intern();
			postgres_server=in.readCompressedInt();
			disable_log=in.readCompressedInt();
			predisable_password=in.readNullUTF();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public List<CannotRemoveReason<?>> getCannotRemoveReasons() throws SQLException, IOException {
		List<CannotRemoveReason<?>> reasons=new ArrayList<>();
		Server ps = getPostgresServer();
		if(
			username.equals(User.POSTGRES)
			|| username.equals(User.POSTGRESMON)
			|| username.equals(User.AOADMIN)
			|| username.equals(User.AOSERV_APP)
			|| username.equals(User.AOWEB_APP)
		) {
			reasons.add(
				new CannotRemoveReason<>(
					"Not allowed to remove a special PostgreSQL user: "
						+ username
						+ " on "
						+ ps.getName()
						+ " on "
						+ ps.getAoServer().getHostname(),
					this
				)
			);
		}

		for(Database pd : getPostgresDatabases()) {
			assert ps.equals(pd.getPostgresServer());
			reasons.add(new CannotRemoveReason<>("Used by PostgreSQL database "+pd.getName()+" on "+ps.getName()+" on "+ps.getAoServer().getHostname(), pd));
		}

		return reasons;
	}

	@Override
	public void remove() throws IOException, SQLException {
		table.getConnector().requestUpdateIL(
			true,
			AoservProtocol.CommandID.REMOVE,
			Table.TableID.POSTGRES_SERVER_USERS,
			pkey
		);
	}

	@Override
	public void setPassword(final String password) throws IOException, SQLException {
		AOServConnector connector=table.getConnector();
		if(!connector.isSecure()) throw new IOException("Passwords for PostgreSQL users may only be set when using secure protocols.  Currently using the "+connector.getProtocol()+" protocol, which is not secure.");

		connector.requestUpdate(
			true,
			AoservProtocol.CommandID.SET_POSTGRES_SERVER_USER_PASSWORD,
			new AOServConnector.UpdateRequest() {
				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(pkey);
					out.writeBoolean(password!=null); if(password!=null) out.writeUTF(password);
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
		table.getConnector().requestUpdate(
			true,
			AoservProtocol.CommandID.SET_POSTGRES_SERVER_USER_PREDISABLE_PASSWORD,
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
		return username+" on "+getPostgresServer().toStringImpl();
	}

	@Override
	public void write(CompressedDataOutputStream out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeUTF(username.toString());
		out.writeCompressedInt(postgres_server);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_130)<=0) {
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
