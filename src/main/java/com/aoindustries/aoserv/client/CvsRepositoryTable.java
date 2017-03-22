/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2002-2012, 2016, 2017  AO Industries, Inc.
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

import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.aoserv.client.validator.UnixPath;
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @see  CvsRepository
 *
 * @author  AO Industries, Inc.
 */
final public class CvsRepositoryTable extends CachedTableIntegerKey<CvsRepository> {

	CvsRepositoryTable(AOServConnector connector) {
		super(connector, CvsRepository.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(CvsRepository.COLUMN_LINUX_SERVER_ACCOUNT_name+'.'+LinuxServerAccount.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING),
		new OrderBy(CvsRepository.COLUMN_PATH_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	int addCvsRepository(
		AOServer ao,
		UnixPath path,
		LinuxServerAccount lsa,
		LinuxServerGroup lsg,
		long mode
	) throws IOException, SQLException {
		return connector.requestIntQueryIL(
			true,
			AOServProtocol.CommandID.ADD,
			SchemaTable.TableID.CVS_REPOSITORIES,
			ao.pkey,
			path,
			lsa.pkey,
			lsg.pkey,
			mode
		);
	}

	@Override
	public CvsRepository get(int pkey) throws IOException, SQLException {
		return getUniqueRow(CvsRepository.COLUMN_PKEY, pkey);
	}

	/**
	 * Gets one <code>CvsRepository</code> from the database.
	 */
	CvsRepository getCvsRepository(AOServer aoServer, UnixPath path) throws IOException, SQLException {
		int aoPKey=aoServer.pkey;

		List<CvsRepository> cached=getRows();
		int size=cached.size();
		for(int c=0;c<size;c++) {
			CvsRepository cr=cached.get(c);
			if(cr.path.equals(path) && cr.getLinuxServerAccount().ao_server==aoPKey) return cr;
		}
		return null;
	}

	List<CvsRepository> getCvsRepositories(AOServer aoServer) throws IOException, SQLException {
		int aoPKey=aoServer.pkey;

		List<CvsRepository> cached=getRows();
		int size=cached.size();
		List<CvsRepository> matches=new ArrayList<>(size);
		for(int c=0;c<size;c++) {
			CvsRepository cr=cached.get(c);
			if(cr.getLinuxServerAccount().ao_server==aoPKey) matches.add(cr);
		}
		return matches;
	}

	List<CvsRepository> getCvsRepositories(Package pk) throws IOException, SQLException {
		AccountingCode pkname = pk.name;

		List<CvsRepository> cached=getRows();
		int size=cached.size();
		List<CvsRepository> matches=new ArrayList<>(size);
		for(int c=0;c<size;c++) {
			CvsRepository cr=cached.get(c);
			if(cr.getLinuxServerAccount().getLinuxAccount().getUsername().packageName.equals(pkname)) matches.add(cr);
		}
		return matches;
	}

	List<CvsRepository> getCvsRepositories(LinuxServerAccount lsa) throws IOException, SQLException {
		return getIndexedRows(CvsRepository.COLUMN_LINUX_SERVER_ACCOUNT, lsa.pkey);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.CVS_REPOSITORIES;
	}

	public SortedSet<UnixPath> getValidPrefixes() throws IOException, SQLException {
		try {
			SortedSet<UnixPath> prefixes=new TreeSet<>();

			// Home directories
			for(LinuxServerAccount lsa : connector.getLinuxServerAccounts().getRows()) {
				if(lsa.getLinuxAccount().getType().getName().equals(LinuxAccountType.USER) && !lsa.isDisabled()) {
					prefixes.add(lsa.getHome());
				}
			}

			// HttpdSites
			for(HttpdSite site : connector.getHttpdSites().getRows()) {
				if(!site.isDisabled()) prefixes.add(site.getInstallDirectory());
			}

			// HttpdSharedTomcats
			for(HttpdSharedTomcat tomcat : connector.getHttpdSharedTomcats().getRows()) {
				if(!tomcat.isDisabled()) {
					prefixes.add(
						UnixPath.valueOf(
							tomcat.getAOServer().getServer().getOperatingSystemVersion().getHttpdSharedTomcatsDirectory().toString()
							+ '/' + tomcat.getName()
						)
					);
				}
			}

			// The global directory
			prefixes.add(CvsRepository.DEFAULT_CVS_DIRECTORY);

			return prefixes;
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(AOSHCommand.ADD_CVS_REPOSITORY)) {
			if(AOSH.checkParamCount(AOSHCommand.ADD_CVS_REPOSITORY, args, 5, err)) {
				int pkey=connector.getSimpleAOClient().addCvsRepository(
					args[1],
					AOSH.parseUnixPath(args[2], "path"),
					AOSH.parseUserId(args[3], "username"),
					AOSH.parseGroupId(args[4], "group"),
					AOSH.parseOctalLong(args[5], "mode")
				);
				out.println(pkey);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.DISABLE_CVS_REPOSITORY)) {
			if(AOSH.checkParamCount(AOSHCommand.DISABLE_CVS_REPOSITORY, args, 2, err)) {
				out.println(
					connector.getSimpleAOClient().disableCvsRepository(
						AOSH.parseInt(args[1], "pkey"),
						args[2]
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.ENABLE_CVS_REPOSITORY)) {
			if(AOSH.checkParamCount(AOSHCommand.ENABLE_CVS_REPOSITORY, args, 1, err)) {
				connector.getSimpleAOClient().enableCvsRepository(AOSH.parseInt(args[1], "pkey"));
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_CVS_REPOSITORY)) {
			if(AOSH.checkParamCount(AOSHCommand.ADD_CVS_REPOSITORY, args, 2, err)) {
				connector.getSimpleAOClient().removeCvsRepository(
					args[1],
					AOSH.parseUnixPath(args[2], "path")
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.SET_CVS_REPOSITORY_MODE)) {
			if(AOSH.checkParamCount(AOSHCommand.SET_CVS_REPOSITORY_MODE, args, 3, err)) {
				connector.getSimpleAOClient().setCvsRepositoryMode(
					args[1],
					AOSH.parseUnixPath(args[2], "path"),
					AOSH.parseOctalLong(args[3], "mode")
				);
			}
			return true;
		}
		return false;
	}
}
