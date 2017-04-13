/*
 * aoserv-client - Java client for the AOServ Platform.
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

import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.io.WriterOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see  HttpdSite
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdSiteTable extends CachedTableIntegerKey<HttpdSite> {

	HttpdSiteTable(AOServConnector connector) {
		super(connector, HttpdSite.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(HttpdSite.COLUMN_SITE_NAME_name, ASCENDING),
		new OrderBy(HttpdSite.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	public String generateSiteName(String template) throws IOException, SQLException {
		return connector.requestStringQuery(true, AOServProtocol.CommandID.GENERATE_SITE_NAME, template);
	}

	@Override
	public HttpdSite get(int pkey) throws IOException, SQLException {
		return getUniqueRow(HttpdSite.COLUMN_PKEY, pkey);
	}

	HttpdSite getHttpdSite(String siteName, AOServer ao) throws IOException, SQLException {
		int aoPKey=ao.pkey;

		List<HttpdSite> cached=getRows();
		int size=cached.size();
		for(int c=0;c<size;c++) {
			HttpdSite site=cached.get(c);
			if(
				site.ao_server==aoPKey
				&& site.site_name.equals(siteName)
			) return site;
		}
		return null;
	}

	List<HttpdSite> getHttpdSites(HttpdServer server) throws IOException, SQLException {
		int serverPKey=server.pkey;

		List<HttpdSite> cached=getRows();
		int size=cached.size();
		List<HttpdSite> matches=new ArrayList<>(size);
		for(int c=0;c<size;c++) {
			HttpdSite site=cached.get(c);
			for(HttpdSiteBind bind : site.getHttpdSiteBinds()) {
				if(bind.getHttpdBind().httpd_server==serverPKey) {
					matches.add(site);
					break;
				}
			}
		}
		return matches;
	}

	List<HttpdSite> getHttpdSites(AOServer ao) throws IOException, SQLException {
		return getIndexedRows(HttpdSite.COLUMN_AO_SERVER, ao.pkey);
	}

	List<HttpdSite> getHttpdSites(Package pk) throws IOException, SQLException {
		return getIndexedRows(HttpdSite.COLUMN_PACKAGE, pk.name);
	}

	List<HttpdSite> getHttpdSites(LinuxServerAccount lsa) throws IOException, SQLException {
		UserId lsaUsername = lsa.username;
		int aoServer = lsa.ao_server;

		List<HttpdSite> cached=getRows();
		int size=cached.size();
		List<HttpdSite> matches=new ArrayList<>(size);
		for(int c=0;c<size;c++) {
			HttpdSite site=cached.get(c);
			if(
				site.ao_server==aoServer
				&& site.linuxAccount.equals(lsaUsername)
			) matches.add(site);
		}
		return matches;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.HTTPD_SITES;
	}

	@Override
	boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, SQLException, IOException {
		String command=args[0];
		if(command.equalsIgnoreCase(AOSHCommand.CHECK_SITE_NAME)) {
			if(AOSH.checkParamCount(AOSHCommand.CHECK_SITE_NAME, args, 1, err)) {
				try {
					SimpleAOClient.checkSiteName(args[1]);
					out.println("true");
				} catch(IllegalArgumentException iae) {
					out.print("aosh: "+AOSHCommand.CHECK_SITE_NAME+": ");
					out.println(iae.getMessage());
				}
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.DISABLE_HTTPD_SITE)) {
			if(AOSH.checkParamCount(AOSHCommand.DISABLE_HTTPD_SITE, args, 3, err)) {
				out.println(
					connector.getSimpleAOClient().disableHttpdSite(
						args[1],
						args[2],
						args[3]
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.ENABLE_HTTPD_SITE)) {
			if(AOSH.checkParamCount(AOSHCommand.ENABLE_HTTPD_SITE, args, 2, err)) {
				connector.getSimpleAOClient().enableHttpdSite(args[1], args[2]);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.GENERATE_SITE_NAME)) {
			if(AOSH.checkParamCount(AOSHCommand.GENERATE_SITE_NAME, args, 1, err)) {
				out.println(connector.getSimpleAOClient().generateSiteName(args[1]));
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.GET_AWSTATS_FILE)) {
			if(AOSH.checkParamCount(AOSHCommand.GET_AWSTATS_FILE, args, 4, err)) {
				connector.getSimpleAOClient().getAWStatsFile(
					args[1],
					args[2],
					args[3],
					args[4],
					new WriterOutputStream(out)
				);
				out.flush();
			}
			return true;
		/*} else if(command.equalsIgnoreCase(AOSHCommand.INITIALIZE_HTTPD_SITE_PASSWD_FILE)) {
			if(AOSH.checkParamCount(AOSHCommand.INITIALIZE_HTTPD_SITE_PASSWD_FILE, args, 4, err)) {
				connector.getSimpleAOClient().initializeHttpdSitePasswdFile(
					args[1],
					args[2],
					args[3],
					args[4]
				);
			}
			return true;
		 */
		} else if(command.equalsIgnoreCase(AOSHCommand.IS_SITE_NAME_AVAILABLE)) {
			if(AOSH.checkParamCount(AOSHCommand.IS_SITE_NAME_AVAILABLE, args, 1, err)) {
				out.println(connector.getSimpleAOClient().isSiteNameAvailable(args[1]));
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_HTTPD_SITE)) {
			if(AOSH.checkParamCount(AOSHCommand.REMOVE_HTTPD_SITE, args, 2, err)) {
				connector.getSimpleAOClient().removeHttpdSite(args[1], args[2]);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.SET_HTTPD_SITE_SERVER_ADMIN)) {
			if(AOSH.checkParamCount(AOSHCommand.SET_HTTPD_SITE_SERVER_ADMIN, args, 3, err)) {
				connector.getSimpleAOClient().setHttpdSiteServerAdmin(
					args[1],
					args[2],
					AOSH.parseEmail(args[3], "email_address")
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.SET_HTTPD_SITE_IS_MANUAL)) {
			if(AOSH.checkParamCount(AOSHCommand.SET_HTTPD_SITE_IS_MANUAL, args, 3, err)) {
				connector.getSimpleAOClient().setHttpdSiteIsManual(
					args[1],
					args[2],
					AOSH.parseBoolean(args[3], "is_manual")
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.WAIT_FOR_HTTPD_SITE_REBUILD)) {
			if(AOSH.checkParamCount(AOSHCommand.WAIT_FOR_HTTPD_SITE_REBUILD, args, 1, err)) {
				connector.getSimpleAOClient().waitForHttpdSiteRebuild(args[1]);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.SET_HTTPD_SITE_PHP_VERSION)) {
			if(AOSH.checkParamCount(AOSHCommand.SET_HTTPD_SITE_PHP_VERSION, args, 3, err)) {
				connector.getSimpleAOClient().setHttpdSitePhpVersion(
					args[1],
					args[2],
					args[3]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.SET_HTTPD_SITE_ENABLE_CGI)) {
			if(AOSH.checkParamCount(AOSHCommand.SET_HTTPD_SITE_ENABLE_CGI, args, 3, err)) {
				connector.getSimpleAOClient().setHttpdSiteEnableCgi(
					args[1],
					args[2],
					AOSH.parseBoolean(args[3], "enable_cgi")
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.SET_HTTPD_SITE_ENABLE_SSI)) {
			if(AOSH.checkParamCount(AOSHCommand.SET_HTTPD_SITE_ENABLE_SSI, args, 3, err)) {
				connector.getSimpleAOClient().setHttpdSiteEnableSsi(
					args[1],
					args[2],
					AOSH.parseBoolean(args[3], "enable_ssi")
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.SET_HTTPD_SITE_ENABLE_HTACCESS)) {
			if(AOSH.checkParamCount(AOSHCommand.SET_HTTPD_SITE_ENABLE_HTACCESS, args, 3, err)) {
				connector.getSimpleAOClient().setHttpdSiteEnableHtaccess(
					args[1],
					args[2],
					AOSH.parseBoolean(args[3], "enable_htaccess")
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.SET_HTTPD_SITE_ENABLE_INDEXES)) {
			if(AOSH.checkParamCount(AOSHCommand.SET_HTTPD_SITE_ENABLE_INDEXES, args, 3, err)) {
				connector.getSimpleAOClient().setHttpdSiteEnableIndexes(
					args[1],
					args[2],
					AOSH.parseBoolean(args[3], "enable_indexes")
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.SET_HTTPD_SITE_ENABLE_FOLLOW_SYMLINKS)) {
			if(AOSH.checkParamCount(AOSHCommand.SET_HTTPD_SITE_ENABLE_FOLLOW_SYMLINKS, args, 3, err)) {
				connector.getSimpleAOClient().setHttpdSiteEnableFollowSymlinks(
					args[1],
					args[2],
					AOSH.parseBoolean(args[3], "enable_follow_symlinks")
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.SET_HTTPD_SITE_ENABLE_ANONYMOUS_FTP)) {
			if(AOSH.checkParamCount(AOSHCommand.SET_HTTPD_SITE_ENABLE_ANONYMOUS_FTP, args, 3, err)) {
				connector.getSimpleAOClient().setHttpdSiteEnableAnonymousFtp(
					args[1],
					args[2],
					AOSH.parseBoolean(args[3], "enable_anonymous_ftp")
				);
			}
			return true;
		} else return false;
	}

	public boolean isSiteNameAvailable(String sitename) throws IOException, SQLException {
		return connector.requestBooleanQuery(true, AOServProtocol.CommandID.IS_SITE_NAME_AVAILABLE, sitename);
	}

	void waitForRebuild(AOServer aoServer) throws IOException, SQLException {
		connector.requestUpdate(
			true,
			AOServProtocol.CommandID.WAIT_FOR_REBUILD,
			SchemaTable.TableID.HTTPD_SITES,
			aoServer.pkey
		);
	}
}
