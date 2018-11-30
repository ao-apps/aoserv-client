/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2012, 2016, 2017, 2018  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.net.reputation;

import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.net.NetDevice;
import com.aoindustries.aoserv.client.schema.AOServProtocol;
import com.aoindustries.aoserv.client.schema.SchemaTable;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * An <code>IpReputationLimiter</code> rate-limits traffic by class and type.
 *
 * @author  AO Industries, Inc.
 */
final public class IpReputationLimiter extends CachedObjectIntegerKey<IpReputationLimiter> {

	static final int
		COLUMN_PKEY=0,
		COLUMN_NET_DEVICE=1
	;

	static final String COLUMN_NET_DEVICE_name= "net_device";
	static final String COLUMN_IDENTIFIER_name= "identifier";

	int netDevice;
	private String identifier;
	private String description;

	@Override
	public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.IP_REPUTATION_LIMITERS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		int pos = 1;
		pkey        = result.getInt(pos++);
		netDevice   = result.getInt(pos++);
		identifier  = result.getString(pos++);
		description = result.getString(pos++);
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(netDevice);
		out.writeUTF          (identifier);
		out.writeNullUTF      (description);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey        = in.readCompressedInt();
		netDevice   = in.readCompressedInt();
		identifier  = in.readUTF();
		description = in.readNullUTF();
	}

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY :       return pkey;
			case COLUMN_NET_DEVICE : return netDevice;
			case 2 :                 return identifier;
			case 3 :                 return description;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public NetDevice getNetDevice() throws SQLException, IOException {
		NetDevice nd = table.getConnector().getNetDevices().get(netDevice);
		if(nd==null) throw new SQLException("Unable to find NetDevice: " + netDevice);
		return nd;
	}

	/**
	 * Gets the per-net device unique identifier for this reputation limiter.
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * Gets the optional description of the limiter.
	 */
	public String getDescription() {
		return description;
	}

	public List<IpReputationLimiterLimit> getLimits() throws IOException, SQLException {
		return table.getConnector().getIpReputationLimiterLimits().getLimits(this);
	}

	public List<IpReputationLimiterSet> getSets() throws IOException, SQLException {
		return table.getConnector().getIpReputationLimiterSets().getSets(this);
	}
}
