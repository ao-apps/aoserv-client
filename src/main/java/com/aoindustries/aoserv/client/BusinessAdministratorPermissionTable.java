/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2007-2009, 2016  AO Industries, Inc.
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

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @see  BusinessAdministratorPermission
 *
 * @author  AO Industries, Inc.
 */
final public class BusinessAdministratorPermissionTable extends CachedTableIntegerKey<BusinessAdministratorPermission> {

	BusinessAdministratorPermissionTable(AOServConnector connector) {
		super(connector, BusinessAdministratorPermission.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(BusinessAdministratorPermission.COLUMN_USERNAME_name, ASCENDING),
		new OrderBy(BusinessAdministratorPermission.COLUMN_PERMISSION_name+'.'+AOServPermission.COLUMN_SORT_ORDER_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public BusinessAdministratorPermission get(int pkey) throws IOException, SQLException {
		return getUniqueRow(BusinessAdministratorPermission.COLUMN_PKEY, pkey);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.BUSINESS_ADMINISTRATOR_PERMISSIONS;
	}

	List<BusinessAdministratorPermission> getPermissions(BusinessAdministrator ba) throws IOException, SQLException {
		return getIndexedRows(BusinessAdministratorPermission.COLUMN_USERNAME, ba.pkey);
	}

	/**
	 * Caches the permission lookups for speed.
	 */
	private Map<String,SortedSet<String>> cachedPermissions;

	@Override
	public void clearCache() {
		super.clearCache();
		synchronized(this) {
			cachedPermissions = null;
		}
	}

	boolean hasPermission(BusinessAdministrator ba, String permission) throws IOException, SQLException {
		synchronized(this) {
			if(cachedPermissions==null) {
				Map<String,SortedSet<String>> newCachedPermissions = new HashMap<>();
				List<BusinessAdministratorPermission> baps = getRows();
				for(BusinessAdministratorPermission bap : baps) {
					String bapUsername = bap.username;
					String bapPermission = bap.permission;
					SortedSet<String> perms = newCachedPermissions.get(bapUsername);
					if(perms==null) newCachedPermissions.put(bapUsername, perms = new TreeSet<>());
					perms.add(bapPermission);
				}
				cachedPermissions = newCachedPermissions;
			}
			SortedSet<String> perms = cachedPermissions.get(ba.pkey);
			return perms!=null && perms.contains(permission);
		}
	}
}
