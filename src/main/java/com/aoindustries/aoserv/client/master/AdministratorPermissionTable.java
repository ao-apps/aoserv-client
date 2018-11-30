/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2007-2009, 2016, 2017, 2018  AO Industries, Inc.
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

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.account.Administrator;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.validator.UserId;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @see  AdministratorPermission
 *
 * @author  AO Industries, Inc.
 */
final public class AdministratorPermissionTable extends CachedTableIntegerKey<AdministratorPermission> {

	public AdministratorPermissionTable(AOServConnector connector) {
		super(connector, AdministratorPermission.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(AdministratorPermission.COLUMN_USERNAME_name, ASCENDING),
		new OrderBy(AdministratorPermission.COLUMN_PERMISSION_name+'.'+Permission.COLUMN_SORT_ORDER_name, ASCENDING)
	};
	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public AdministratorPermission get(int pkey) throws IOException, SQLException {
		return getUniqueRow(AdministratorPermission.COLUMN_PKEY, pkey);
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.BUSINESS_ADMINISTRATOR_PERMISSIONS;
	}

	public List<AdministratorPermission> getPermissions(Administrator ba) throws IOException, SQLException {
		return getIndexedRows(AdministratorPermission.COLUMN_USERNAME, ba.getUsername_userId());
	}

	/**
	 * Caches the permission lookups for speed.
	 */
	private Map<UserId,SortedSet<String>> cachedPermissions;

	@Override
	public void clearCache() {
		super.clearCache();
		synchronized(this) {
			cachedPermissions = null;
		}
	}

	public boolean hasPermission(Administrator ba, String permission) throws IOException, SQLException {
		synchronized(this) {
			if(cachedPermissions==null) {
				Map<UserId,SortedSet<String>> newCachedPermissions = new HashMap<>();
				List<AdministratorPermission> baps = getRows();
				for(AdministratorPermission bap : baps) {
					UserId bapUsername = bap.username;
					String bapPermission = bap.permission;
					SortedSet<String> perms = newCachedPermissions.get(bapUsername);
					if(perms==null) newCachedPermissions.put(bapUsername, perms = new TreeSet<>());
					perms.add(bapPermission);
				}
				cachedPermissions = newCachedPermissions;
			}
			SortedSet<String> perms = cachedPermissions.get(ba.getUsername_userId());
			return perms!=null && perms.contains(permission);
		}
	}
}
