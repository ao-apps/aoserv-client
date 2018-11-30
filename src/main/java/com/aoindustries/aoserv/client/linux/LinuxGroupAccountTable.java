/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2012, 2016, 2017, 2018  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.linux;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.aosh.AOSHCommand;
import com.aoindustries.aoserv.client.schema.AOServProtocol;
import com.aoindustries.aoserv.client.schema.SchemaTable;
import com.aoindustries.aoserv.client.validator.GroupId;
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.lang.NullArgumentException;
import com.aoindustries.util.AoCollections;
import com.aoindustries.util.Tuple2;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @see  LinuxGroupAccount
 *
 * @author  AO Industries, Inc.
 */
final public class LinuxGroupAccountTable extends CachedTableIntegerKey<LinuxGroupAccount> {

	private boolean hashBuilt = false;
	private final Map<Tuple2<GroupId,UserId>,List<LinuxGroupAccount>> hash = new HashMap<>();

	/**
	 * The group name of the primary group is hashed on first use for fast
	 * lookups.
	 */
	private boolean primaryHashBuilt = false;
	private final Map<UserId,GroupId> primaryHash = new HashMap<>();

	public LinuxGroupAccountTable(AOServConnector connector) {
		super(connector, LinuxGroupAccount.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(LinuxGroupAccount.COLUMN_GROUP_name, ASCENDING),
		new OrderBy(LinuxGroupAccount.COLUMN_USER_name, ASCENDING)
	};
	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	int addLinuxGroupAccount(
		LinuxGroup groupObject,
		LinuxAccount userObject
	) throws IOException, SQLException {
		int pkey=connector.requestIntQueryIL(
			true,
			AOServProtocol.CommandID.ADD,
			SchemaTable.TableID.LINUX_GROUP_ACCOUNTS,
			groupObject.getName(),
			userObject.getUsername()
		);
		return pkey;
	}

	@Override
	public LinuxGroupAccount get(int id) throws IOException, SQLException {
		return getUniqueRow(LinuxGroupAccount.COLUMN_ID, id);
	}

	public List<LinuxGroupAccount> getLinuxGroupAccounts(
		GroupId group,
		UserId user
	) throws IOException, SQLException {
		synchronized(hash) {
			if(!hashBuilt) {
				hash.clear();
				for(LinuxGroupAccount lga : getRows()) {
					Tuple2<GroupId,UserId> key = new Tuple2<>(lga.getGroup_name(), lga.getUser_username());
					List<LinuxGroupAccount> list = hash.get(key);
					if(list == null) hash.put(key, list = new ArrayList<>());
					list.add(lga);
				}
				// Make entries unmodifiable
				for(Map.Entry<Tuple2<GroupId,UserId>,List<LinuxGroupAccount>> entry : hash.entrySet()) {
					entry.setValue(
						AoCollections.optimalUnmodifiableList(entry.getValue())
					);
				}
				hashBuilt = true;
			}
			List<LinuxGroupAccount> lgas = hash.get(new Tuple2<>(group, user));
			if(lgas == null) return Collections.emptyList();
			return lgas;
		}
	}

	List<LinuxGroup> getLinuxGroups(LinuxAccount linuxAccount) throws IOException, SQLException {
		UserId username = linuxAccount.getUsername_id();
		List<LinuxGroupAccount> rows = getRows();
		int len = rows.size();
		List<LinuxGroup> matches = new ArrayList<>(LinuxGroupAccount.MAX_GROUPS);
		for(int c = 0; c < len; c++) {
			LinuxGroupAccount lga = rows.get(c);
			if(lga.getUser_username().equals(username)) {
				LinuxGroup lg = lga.getGroup();
				// Avoid duplicates that are now possible due to operating_system_version
				if(!matches.contains(lg)) matches.add(lg);
			}
		}
		return matches;
	}

	LinuxGroup getPrimaryGroup(LinuxAccount account) throws IOException, SQLException {
		NullArgumentException.checkNotNull(account, "account");
		synchronized(primaryHash) {
			// Rebuild the hash if needed
			if(!primaryHashBuilt) {
				List<LinuxGroupAccount> cache = getRows();
				primaryHash.clear();
				int len = cache.size();
				for(int c = 0; c < len; c++) {
					LinuxGroupAccount lga = cache.get(c);
					if(lga.isPrimary()) {
						UserId username = lga.getUser_username();
						GroupId groupName = lga.getGroup_name();
						GroupId existing = primaryHash.put(username, groupName);
						if(
							existing != null
							&& !existing.equals(groupName)
						) {
							throw new SQLException(
								"Conflicting primary groups for "
									+ username
									+ ": "
									+ existing
									+ " and "
									+ groupName
							);
						}
					}
				}
				primaryHashBuilt = true;
			}
			GroupId groupName = primaryHash.get(account.getUsername_id());
			if(groupName == null) return null;
			// May be filtered
			return connector.getLinuxGroups().get(groupName);
		}
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.LINUX_GROUP_ACCOUNTS;
	}

	@Override
	public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(AOSHCommand.ADD_LINUX_GROUP_ACCOUNT)) {
			if(AOSH.checkParamCount(AOSHCommand.ADD_LINUX_GROUP_ACCOUNT, args, 2, err)) {
				out.println(
					connector.getSimpleAOClient().addLinuxGroupAccount(
						AOSH.parseGroupId(args[1], "group"),
						AOSH.parseUserId(args[2], "username")
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_LINUX_GROUP_ACCOUNT)) {
			if(AOSH.checkParamCount(AOSHCommand.REMOVE_LINUX_GROUP_ACCOUNT, args, 2, err)) {
				connector.getSimpleAOClient().removeLinuxGroupAccount(
					AOSH.parseGroupId(args[1], "group"),
					AOSH.parseUserId(args[2], "username")
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.SET_PRIMARY_LINUX_GROUP_ACCOUNT)) {
			if(AOSH.checkParamCount(AOSHCommand.SET_PRIMARY_LINUX_GROUP_ACCOUNT, args, 2, err)) {
				connector.getSimpleAOClient().setPrimaryLinuxGroupAccount(
					AOSH.parseGroupId(args[1], "group"),
					AOSH.parseUserId(args[2], "username")
				);
			}
			return true;
		}
		return false;
	}

	@Override
	public void clearCache() {
		super.clearCache();
		synchronized(hash) {
			hashBuilt = false;
		}
		synchronized(primaryHash) {
			primaryHashBuilt = false;
		}
	}
}
