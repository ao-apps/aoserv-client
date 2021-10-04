/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2012, 2014, 2016, 2017, 2018, 2019, 2020, 2021  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.master;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.validation.ValidationException;
import com.aoindustries.aoserv.client.account.Administrator;
import com.aoindustries.aoserv.client.account.CachedObjectUserNameKey;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A <code>MasterUser</code> is an {@link Administrator} who
 * has greater permissions.  Their access is secure on a per-<code>Server</code>
 * basis, and may also include full access to DNS, backups, and other
 * systems.
 *
 * @see  Administrator
 * @see  UserHost
 * @see  UserAcl
 *
 * @author  AO Industries, Inc.
 */
public final class User extends CachedObjectUserNameKey<User> {

	static final int COLUMN_USERNAME=0;
	static final String COLUMN_USERNAME_name = "username";

	private boolean
		is_active,
		can_access_accounting,
		can_access_bank_account,
		can_invalidate_tables,
		can_access_admin_web,
		is_dns_admin,
		is_router,
		is_cluster_admin
	;

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.MASTER_USERS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			pkey                   = com.aoindustries.aoserv.client.account.User.Name.valueOf(result.getString(1));
			is_active              = result.getBoolean(2);
			can_access_accounting  = result.getBoolean(3);
			can_access_bank_account= result.getBoolean(4);
			can_invalidate_tables  = result.getBoolean(5);
			can_access_admin_web   = result.getBoolean(6);
			is_dns_admin           = result.getBoolean(7);
			is_router              = result.getBoolean(8);
			is_cluster_admin       = result.getBoolean(9);
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeUTF(pkey.toString());
		out.writeBoolean(is_active);
		out.writeBoolean(can_access_accounting);
		out.writeBoolean(can_access_bank_account);
		out.writeBoolean(can_invalidate_tables);
		out.writeBoolean(can_access_admin_web);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_43)<=0)     out.writeBoolean(false); // is_ticket_admin
		out.writeBoolean(is_dns_admin);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_118)<0) out.writeBoolean(false);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_65)>=0)     out.writeBoolean(is_router);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_73)>=0)     out.writeBoolean(is_cluster_admin);
	}

	@Override
	public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
		try {
			pkey                    = com.aoindustries.aoserv.client.account.User.Name.valueOf(in.readUTF()).intern();
			is_active               = in.readBoolean();
			can_access_accounting   = in.readBoolean();
			can_access_bank_account = in.readBoolean();
			can_invalidate_tables   = in.readBoolean();
			can_access_admin_web    = in.readBoolean();
			is_dns_admin            = in.readBoolean();
			is_router               = in.readBoolean();
			is_cluster_admin        = in.readBoolean();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_USERNAME: return pkey;
			case 1: return is_active;
			case 2: return can_access_accounting;
			case 3: return can_access_bank_account;
			case 4: return can_invalidate_tables;
			case 5: return can_access_admin_web;
			case 6: return is_dns_admin;
			case 7: return is_router;
			case 8: return is_cluster_admin;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	public Administrator getAdministrator() throws SQLException, IOException {
		Administrator obj = table.getConnector().getAccount().getAdministrator().get(pkey);
		if(obj == null) throw new SQLException("Unable to find Administrator: " + pkey);
		return obj;
	}

	public boolean isActive() {
		return is_active;
	}

	public boolean canAccessAccounting() {
		return can_access_accounting;
	}

	public boolean canAccessBankAccount() {
		return can_access_bank_account;
	}

	public boolean canInvalidateTables() {
		return can_invalidate_tables;
	}

	public boolean isWebAdmin() {
		return can_access_admin_web;
	}

	public boolean isDNSAdmin() {
		return is_dns_admin;
	}

	public boolean isRouter() {
		return is_router;
	}

	public boolean isClusterAdmin() {
		return is_cluster_admin;
	}
}
