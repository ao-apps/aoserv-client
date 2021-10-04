/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2002-2012, 2016, 2017, 2018, 2019, 2020, 2021  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.scm;

import com.aoapps.hodgepodge.io.TerminalWriter;
import com.aoapps.lang.validation.ValidationException;
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.linux.GroupServer;
import com.aoindustries.aoserv.client.linux.PosixPath;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.linux.UserServer;
import com.aoindustries.aoserv.client.linux.UserType;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.web.Site;
import com.aoindustries.aoserv.client.web.tomcat.SharedTomcat;
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
public final class CvsRepositoryTable extends CachedTableIntegerKey<CvsRepository> {

	CvsRepositoryTable(AOServConnector connector) {
		super(connector, CvsRepository.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(CvsRepository.COLUMN_LINUX_SERVER_ACCOUNT_name+'.'+UserServer.COLUMN_AO_SERVER_name+'.'+Server.COLUMN_HOSTNAME_name, ASCENDING),
		new OrderBy(CvsRepository.COLUMN_PATH_name, ASCENDING)
	};
	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	public int addCvsRepository(
		Server ao,
		PosixPath path,
		UserServer lsa,
		GroupServer lsg,
		long mode
	) throws IOException, SQLException {
		return connector.requestIntQueryIL(
			true,
			AoservProtocol.CommandID.ADD,
			Table.TableID.CVS_REPOSITORIES,
			ao.getPkey(),
			path,
			lsa.getPkey(),
			lsg.getPkey(),
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
	public CvsRepository getCvsRepository(Server aoServer, PosixPath path) throws IOException, SQLException {
		int aoPKey=aoServer.getPkey();

		List<CvsRepository> cached=getRows();
		int size=cached.size();
		for(int c=0;c<size;c++) {
			CvsRepository cr=cached.get(c);
			if(cr.getPath().equals(path) && cr.getLinuxServerAccount().getAoServer_server_id()==aoPKey) return cr;
		}
		return null;
	}

	public List<CvsRepository> getCvsRepositories(Server aoServer) throws IOException, SQLException {
		int aoPKey=aoServer.getPkey();

		List<CvsRepository> cached=getRows();
		int size=cached.size();
		List<CvsRepository> matches=new ArrayList<>(size);
		for(int c=0;c<size;c++) {
			CvsRepository cr=cached.get(c);
			if(cr.getLinuxServerAccount().getAoServer_server_id()==aoPKey) matches.add(cr);
		}
		return matches;
	}

	public List<CvsRepository> getCvsRepositories(Package pk) throws IOException, SQLException {
		Account.Name pkname = pk.getName();

		List<CvsRepository> cached=getRows();
		int size=cached.size();
		List<CvsRepository> matches=new ArrayList<>(size);
		for(int c=0;c<size;c++) {
			CvsRepository cr=cached.get(c);
			if(cr.getLinuxServerAccount().getLinuxAccount().getUsername().getPackage_name().equals(pkname)) matches.add(cr);
		}
		return matches;
	}

	public List<CvsRepository> getCvsRepositories(UserServer lsa) throws IOException, SQLException {
		return getIndexedRows(CvsRepository.COLUMN_LINUX_SERVER_ACCOUNT, lsa.getPkey());
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.CVS_REPOSITORIES;
	}

	public SortedSet<PosixPath> getValidPrefixes() throws IOException, SQLException {
		try {
			SortedSet<PosixPath> prefixes=new TreeSet<>();

			// Home directories
			for(UserServer lsa : connector.getLinux().getUserServer().getRows()) {
				if(lsa.getLinuxAccount().getType().getName().equals(UserType.USER) && !lsa.isDisabled()) {
					prefixes.add(lsa.getHome());
				}
			}

			// HttpdSites
			for(Site site : connector.getWeb().getSite().getRows()) {
				if(!site.isDisabled()) prefixes.add(site.getInstallDirectory());
			}

			// HttpdSharedTomcats
			for(SharedTomcat tomcat : connector.getWeb_tomcat().getSharedTomcat().getRows()) {
				if(!tomcat.isDisabled()) {
					prefixes.add(PosixPath.valueOf(
							tomcat.getLinuxServer().getHost().getOperatingSystemVersion().getHttpdSharedTomcatsDirectory().toString()
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
	public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(Command.ADD_CVS_REPOSITORY)) {
			if(AOSH.checkParamCount(Command.ADD_CVS_REPOSITORY, args, 5, err)) {
				out.println(
					connector.getSimpleAOClient().addCvsRepository(
						args[1],
						AOSH.parseUnixPath(args[2], "path"),
						AOSH.parseLinuxUserName(args[3], "username"),
						AOSH.parseGroupName(args[4], "group"),
						AOSH.parseOctalLong(args[5], "mode")
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.DISABLE_CVS_REPOSITORY)) {
			if(AOSH.checkParamCount(Command.DISABLE_CVS_REPOSITORY, args, 2, err)) {
				out.println(
					connector.getSimpleAOClient().disableCvsRepository(
						AOSH.parseInt(args[1], "pkey"),
						args[2]
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.ENABLE_CVS_REPOSITORY)) {
			if(AOSH.checkParamCount(Command.ENABLE_CVS_REPOSITORY, args, 1, err)) {
				connector.getSimpleAOClient().enableCvsRepository(AOSH.parseInt(args[1], "pkey"));
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.REMOVE_CVS_REPOSITORY)) {
			if(AOSH.checkParamCount(Command.ADD_CVS_REPOSITORY, args, 2, err)) {
				connector.getSimpleAOClient().removeCvsRepository(
					args[1],
					AOSH.parseUnixPath(args[2], "path")
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.SET_CVS_REPOSITORY_MODE)) {
			if(AOSH.checkParamCount(Command.SET_CVS_REPOSITORY_MODE, args, 3, err)) {
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
