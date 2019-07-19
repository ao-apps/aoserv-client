/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2017, 2018, 2019  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.net.monitoring;

import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.net.IpAddress;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @see  IpAddress
 *
 * @author  AO Industries, Inc.
 */
final public class IpAddressMonitoring extends CachedObjectIntegerKey<IpAddressMonitoring> {

	static final int COLUMN_ID = 0;
	static final String COLUMN_ID_name = "id";

	private boolean enabled;
	private boolean pingMonitorEnabled;
	private boolean checkBlacklistsOverSmtp;
	private boolean verifyDnsPtr;
	private boolean verifyDnsA;

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_ID: return pkey;
			case 1: return enabled;
			case 2: return pingMonitorEnabled;
			case 3: return checkBlacklistsOverSmtp;
			case 4: return verifyDnsPtr;
			case 5: return verifyDnsA;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	public int getId() {
		return pkey;
	}

	public IpAddress getIpAddress() throws SQLException, IOException {
		IpAddress obj = table.getConnector().getNet().getIpAddress().get(pkey);
		if(obj == null) throw new SQLException("Unable to find IPAddress: " + pkey);
		return obj;
	}

	public boolean getEnabled() {
		return enabled;
	}

	public boolean getPingMonitorEnabled() {
		return pingMonitorEnabled;
	}

	/**
	 * When the IP address is assigned to a {@link Server}, blacklist status
	 * may be further determined by making SMTP connections out from the
	 * server point of view.  This allows the detection of blocks by some
	 * providers that give no other way to query, such as Comcast and the
	 * AT&amp;T family of companies.
	 */
	public boolean getCheckBlacklistsOverSmtp() {
		return checkBlacklistsOverSmtp;
	}

	public boolean getVerifyDnsPtr() {
		return verifyDnsPtr;
	}

	public boolean getVerifyDnsA() {
		return verifyDnsA;
	}

	public void setEnabled(boolean enabled) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(
			true,
			AoservProtocol.CommandID.SET_IP_ADDRESS_MONITORING_ENABLED,
			pkey,
			enabled
		);
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.IpAddressMonitoring;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		int pos = 1;
		pkey = result.getInt(pos++);
		enabled = result.getBoolean(pos++);
		pingMonitorEnabled = result.getBoolean(pos++);
		checkBlacklistsOverSmtp = result.getBoolean(pos++);
		verifyDnsPtr = result.getBoolean(pos++);
		verifyDnsA = result.getBoolean(pos++);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey = in.readCompressedInt();
		enabled = in.readBoolean();
		pingMonitorEnabled = in.readBoolean();
		checkBlacklistsOverSmtp = in.readBoolean();
		verifyDnsPtr = in.readBoolean();
		verifyDnsA = in.readBoolean();
	}

	@Override
	public void write(CompressedDataOutputStream out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeBoolean(enabled);
		out.writeBoolean(pingMonitorEnabled);
		out.writeBoolean(checkBlacklistsOverSmtp);
		out.writeBoolean(verifyDnsPtr);
		out.writeBoolean(verifyDnsA);
	}
}
