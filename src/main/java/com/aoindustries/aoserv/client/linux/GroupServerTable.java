/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018, 2019, 2020, 2021  AO Industries, Inc.
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

import com.aoapps.hodgepodge.io.TerminalWriter;
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @see  GroupServer
 *
 * @author  AO Industries, Inc.
 */
public final class GroupServerTable extends CachedTableIntegerKey<GroupServer> {

	GroupServerTable(AOServConnector connector) {
		super(connector, GroupServer.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(GroupServer.COLUMN_NAME_name, ASCENDING),
		new OrderBy(GroupServer.COLUMN_AO_SERVER_name+'.'+Server.COLUMN_HOSTNAME_name, ASCENDING)
	};
	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	int addLinuxServerGroup(Group linuxGroup, Server aoServer) throws IOException, SQLException {
		int pkey=connector.requestIntQueryIL(
			true,
			AoservProtocol.CommandID.ADD,
			Table.TableID.LINUX_SERVER_GROUPS,
			linuxGroup.getName(),
			aoServer.getPkey()
		);
		return pkey;
	}

	int addSystemGroup(Server aoServer, Group.Name groupName, int gid) throws IOException, SQLException {
		return connector.requestIntQueryIL(
			true,
			AoservProtocol.CommandID.ADD_SYSTEM_GROUP,
			aoServer.getPkey(),
			groupName,
			gid
		);
	}

	@Override
	public void clearCache() {
		super.clearCache();
		synchronized(gidHash) {
			gidHashBuilt=false;
		}
		synchronized(nameHash) {
			nameHashBuilt=false;
		}
	}

	@Override
	public GroupServer get(int pkey) throws IOException, SQLException {
		return getUniqueRow(GroupServer.COLUMN_PKEY, pkey);
	}

	public GroupServer getLinuxServerGroup(Server aoServer, Account business) throws IOException, SQLException {
		Account.Name accounting=business.getName();
		int aoPKey=aoServer.getPkey();

		List<GroupServer> list = getRows();
		int len = list.size();
		for (int c = 0; c < len; c++) {
			// Must be for the correct server
			GroupServer group = list.get(c);
			if (aoPKey==group.getServer_host_id()) {
				// Must be for the correct business
				Group linuxGroup = group.getLinuxGroup();
				Package pk=linuxGroup.getPackage();
				if (pk!=null && pk.getAccount_name().equals(accounting)) {
					// Must be a user group
					if (linuxGroup.getLinuxGroupType().getName().equals(GroupType.USER)) return group;
				}
			}
		}
		return null;
	}

	private boolean nameHashBuilt=false;
	private final Map<Integer, Map<Group.Name, GroupServer>> nameHash=new HashMap<>();

	public GroupServer getLinuxServerGroup(Server aoServer, Group.Name group_name) throws IOException, SQLException {
		synchronized(nameHash) {
			if(!nameHashBuilt) {
				nameHash.clear();

				List<GroupServer> list=getRows();
				int len=list.size();
				for(int c=0; c<len; c++) {
					GroupServer lsg=list.get(c);
					Integer I=lsg.getServer_host_id();
					Map<Group.Name, GroupServer> serverHash=nameHash.get(I);
					if(serverHash==null) nameHash.put(I, serverHash=new HashMap<>());
					if(serverHash.put(lsg.getLinuxGroup_name(), lsg)!=null) throw new SQLException("LinuxServerGroup name exists more than once on server: "+lsg.getLinuxGroup_name()+" on "+I);

				}
				nameHashBuilt=true;
			}
			Map<Group.Name, GroupServer> serverHash=nameHash.get(aoServer.getPkey());
			if(serverHash==null) return null;
			return serverHash.get(group_name);
		}
	}

	private boolean gidHashBuilt=false;
	private final Map<Integer, Map<LinuxId, GroupServer>> gidHash=new HashMap<>();

	public GroupServer getLinuxServerGroup(Server aoServer, LinuxId gid) throws IOException, SQLException {
		synchronized(gidHash) {
			if(!gidHashBuilt) {
				gidHash.clear();

				List<GroupServer> list=getRows();
				int len=list.size();
				for(int c=0; c<len; c++) {
					GroupServer lsg=list.get(c);
					Integer serverI=lsg.getServer_host_id();
					Map<LinuxId, GroupServer> serverHash = gidHash.get(serverI);
					if(serverHash==null) gidHash.put(serverI, serverHash=new HashMap<>());
					LinuxId gidI=lsg.getGid();
					if(serverHash.put(gidI, lsg)!=null) throw new SQLException("GID exists more than once on server: "+gidI+" on "+serverI);
				}
				gidHashBuilt=true;
			}
			Map<LinuxId, GroupServer> serverHash=gidHash.get(aoServer.getPkey());
			if(serverHash==null) return null;
			return serverHash.get(gid);
		}
	}

	List<GroupServer> getLinuxServerGroups(Server aoServer) throws IOException, SQLException {
		return getIndexedRows(GroupServer.COLUMN_AO_SERVER, aoServer.getPkey());
	}

	List<GroupServer> getLinuxServerGroups(Group lg) throws IOException, SQLException {
		return getIndexedRows(GroupServer.COLUMN_NAME, lg.getName());
	}

	/**
	 * Gets the primary <code>LinuxServerGroup</code> for this <code>LinuxServerAccount</code>
	 *
	 * @exception  SQLException  if the primary group is not found
	 *                           or two or more groups are marked as primary
	 *                           or the primary group does not exist on the same server
	 */
	GroupServer getPrimaryLinuxServerGroup(UserServer account) throws SQLException, IOException {
		if(account==null) throw new IllegalArgumentException("account=null");

		// Find the primary group for the account
		User linuxAccount=account.getLinuxAccount();
		Group linuxGroup=connector.getLinux().getGroupUser().getPrimaryGroup(linuxAccount);
		if(linuxGroup==null) throw new SQLException("Unable to find primary LinuxGroup for username="+linuxAccount.getUsername_id());
		GroupServer lsg=getLinuxServerGroup(account.getServer(), linuxGroup.getName());
		if(lsg==null) throw new SQLException("Unable to find LinuxServerGroup: "+linuxGroup.getName()+" on "+account.getAoServer_server_id());
		return lsg;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.LINUX_SERVER_GROUPS;
	}

	@Override
	public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(Command.ADD_LINUX_SERVER_GROUP)) {
			if(AOSH.checkParamCount(Command.ADD_LINUX_SERVER_GROUP, args, 2, err)) {
				out.println(
					connector.getSimpleAOClient().addLinuxServerGroup(
						AOSH.parseGroupName(args[1], "group"),
						args[2]
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.REMOVE_LINUX_SERVER_GROUP)) {
			if(AOSH.checkParamCount(Command.REMOVE_LINUX_SERVER_GROUP, args, 2, err)) {
				connector.getSimpleAOClient().removeLinuxServerGroup(
					AOSH.parseGroupName(args[1], "group"),
					args[2]
				);
			}
			return true;
		}
		return false;
	}
}
