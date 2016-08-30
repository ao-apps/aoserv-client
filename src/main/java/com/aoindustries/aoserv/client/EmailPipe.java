/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2000-2009, 2016  AO Industries, Inc.
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

import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * Incoming email addressed to an <code>EmailPipe</code> is piped
 * into a native process.  This process may then take any action
 * desired for mail delivery or handling.
 *
 * @see  EmailAddress
 *
 * @author  AO Industries, Inc.
 */
final public class EmailPipe extends CachedObjectIntegerKey<EmailPipe> implements Removable, Disablable {

	static final int
		COLUMN_PKEY=0,
		COLUMN_AO_SERVER=1,
		COLUMN_PACKAGE=3
	;
	static final String COLUMN_AO_SERVER_name = "ao_server";
	static final String COLUMN_PATH_name = "path";

	int ao_server;
	private String path;
	String packageName;
	int disable_log;

	public int addEmailAddress(EmailAddress address) throws IOException, SQLException {
		return table.connector.getEmailPipeAddresses().addEmailPipeAddress(address, this);
	}

	@Override
	public boolean canDisable() {
		return disable_log==-1;
	}

	@Override
	public boolean canEnable() throws SQLException, IOException {
		DisableLog dl=getDisableLog();
		if(dl==null) return false;
		else return dl.canEnable() && getPackage().disable_log==-1;
	}

	@Override
	public void disable(DisableLog dl) throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.DISABLE, SchemaTable.TableID.EMAIL_PIPES, dl.pkey, pkey);
	}

	@Override
	public void enable() throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.ENABLE, SchemaTable.TableID.EMAIL_PIPES, pkey);
	}

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_AO_SERVER: return ao_server;
			case 2: return path;
			case COLUMN_PACKAGE: return packageName;
			case 4: return disable_log==-1?null:disable_log;
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

	public Package getPackage() throws IOException, SQLException {
		Package packageObject = table.connector.getPackages().get(packageName);
		if (packageObject == null) throw new SQLException("Unable to find Package: " + packageName);
		return packageObject;
	}

	public String getPath() {
		return path;
	}

	public AOServer getAOServer() throws SQLException, IOException {
		AOServer ao=table.connector.getAoServers().get(ao_server);
		if(ao==null) throw new SQLException("Unable to find AOServer: "+ao_server);
		return ao;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.EMAIL_PIPES;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey = result.getInt(1);
		ao_server = result.getInt(2);
		path = result.getString(3);
		packageName = result.getString(4);
		disable_log=result.getInt(5);
		if(result.wasNull()) disable_log=-1;
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readCompressedInt();
		ao_server=in.readCompressedInt();
		path=in.readUTF();
		packageName=in.readUTF().intern();
		disable_log=in.readCompressedInt();
	}

	@Override
	public List<CannotRemoveReason> getCannotRemoveReasons() {
		return Collections.emptyList();
	}

	@Override
	public void remove() throws IOException, SQLException {
		table.connector.requestUpdateIL(
			true,
			AOServProtocol.CommandID.REMOVE,
			SchemaTable.TableID.EMAIL_PIPES,
			pkey
		);
	}

	@Override
	String toStringImpl() {
		return ao_server+':'+path;
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(ao_server);
		out.writeUTF(path);
		out.writeUTF(packageName);
		out.writeCompressedInt(disable_log);
	}
}
