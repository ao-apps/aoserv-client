/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2001-2012, 2016, 2017  AO Industries, Inc.
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

import com.aoindustries.aoserv.client.validator.GroupId;
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.util.Tuple2;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @see  LinuxGroupAccount
 *
 * @author  AO Industries, Inc.
 */
final public class LinuxGroupAccountTable extends CachedTableIntegerKey<LinuxGroupAccount> {

	private boolean hashBuilt=false;
	private final Map<Tuple2<GroupId,UserId>,LinuxGroupAccount> hash=new HashMap<>();

	/**
	 * The group name of the primary group is hashed on first use for fast
	 * lookups.
	 */
	private boolean primaryHashBuilt=false;
	private final Map<UserId,LinuxGroupAccount> primaryHash=new HashMap<>();

	LinuxGroupAccountTable(AOServConnector connector) {
		super(connector, LinuxGroupAccount.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(LinuxGroupAccount.COLUMN_GROUP_NAME_name, ASCENDING),
		new OrderBy(LinuxGroupAccount.COLUMN_USERNAME_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	int addLinuxGroupAccount(
		LinuxGroup groupNameObject,
		LinuxAccount usernameObject
	) throws IOException, SQLException {
		int pkey=connector.requestIntQueryIL(
			true,
			AOServProtocol.CommandID.ADD,
			SchemaTable.TableID.LINUX_GROUP_ACCOUNTS,
			groupNameObject.pkey,
			usernameObject.pkey
		);
		return pkey;
	}

	@Override
	public LinuxGroupAccount get(int pkey) throws IOException, SQLException {
		return getUniqueRow(LinuxGroupAccount.COLUMN_PKEY, pkey);
	}

	LinuxGroupAccount getLinuxGroupAccount(
		GroupId groupName,
		UserId username
	) throws IOException, SQLException {
		synchronized(hash) {
			if(!hashBuilt) {
				hash.clear();
				List<LinuxGroupAccount> list=getRows();
				int len=list.size();
				for(int c=0;c<len;c++) {
					LinuxGroupAccount lga=list.get(c);
					hash.put(new Tuple2<>(lga.group_name, lga.username), lga);
				}
				hashBuilt=true;
			}
			return hash.get(new Tuple2<>(groupName, username));
		}
	}

	List<LinuxGroup> getLinuxGroups(LinuxAccount linuxAccount) throws IOException, SQLException {
		UserId username = linuxAccount.pkey;
		List<LinuxGroupAccount> cached = getRows();
		int len = cached.size();
		List<LinuxGroup> matches=new ArrayList<>(LinuxGroupAccount.MAX_GROUPS);
		for (int c = 0; c < len; c++) {
			LinuxGroupAccount lga = cached.get(c);
			if (lga.username.equals(username)) matches.add(lga.getLinuxGroup());
		}
		return matches;
	}

	LinuxGroup getPrimaryGroup(LinuxAccount account) throws IOException, SQLException {
		synchronized(primaryHash) {
			if(account==null) throw new IllegalArgumentException("param account is null");
			// Rebuild the hash if needed
			if(!primaryHashBuilt) {
				List<LinuxGroupAccount> cache=getRows();
				primaryHash.clear();
				int len=cache.size();
				for(int c=0;c<len;c++) {
					LinuxGroupAccount lga=cache.get(c);
					if(lga.isPrimary()) primaryHash.put(lga.username, lga);
				}
				primaryHashBuilt=true;
			}
			LinuxGroupAccount lga=primaryHash.get(account.pkey);
			// May be filtered
			if(lga==null) return null;
			return lga.getLinuxGroup();
		}
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.LINUX_GROUP_ACCOUNTS;
	}

	@Override
	boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(AOSHCommand.ADD_LINUX_GROUP_ACCOUNT)) {
			if(AOSH.checkParamCount(AOSHCommand.ADD_LINUX_GROUP_ACCOUNT, args, 2, err)) {
				connector.getSimpleAOClient().addLinuxGroupAccount(
					AOSH.parseGroupId(args[1], "group"),
					AOSH.parseUserId(args[2], "username")
				);
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
			hashBuilt=false;
		}
		synchronized(primaryHash) {
			primaryHashBuilt=false;
		}
	}
}
