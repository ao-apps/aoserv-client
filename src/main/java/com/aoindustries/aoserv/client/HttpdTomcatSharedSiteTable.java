/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2001-2013, 2016, 2017  AO Industries, Inc.
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

import com.aoindustries.aoserv.client.validator.UnixPath;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.net.DomainName;
import com.aoindustries.net.Email;
import com.aoindustries.util.IntList;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  HttpdTomcatStdSite
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdTomcatSharedSiteTable extends CachedTableIntegerKey<HttpdTomcatSharedSite> {

	HttpdTomcatSharedSiteTable(AOServConnector connector) {
		super(connector, HttpdTomcatSharedSite.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(HttpdTomcatSharedSite.COLUMN_TOMCAT_SITE_name+'.'+HttpdTomcatSite.COLUMN_HTTPD_SITE_name+'.'+HttpdSite.COLUMN_SITE_NAME_name, ASCENDING),
		new OrderBy(HttpdTomcatSharedSite.COLUMN_TOMCAT_SITE_name+'.'+HttpdTomcatSite.COLUMN_HTTPD_SITE_name+'.'+HttpdSite.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	int addHttpdTomcatSharedSite(
		final AOServer aoServer,
		final String siteName,
		final Package packageObj,
		final LinuxAccount siteUser,
		final LinuxGroup siteGroup,
		final Email serverAdmin,
		final boolean useApache,
		final IPAddress ipAddress,
		final DomainName primaryHttpHostname,
		final DomainName[] altHttpHostnames,
		final String sharedTomcatName,
		HttpdTomcatVersion version,
		final UnixPath contentSrc
	) throws IOException, SQLException {
		final int tv = version==null?-1:version.getTechnologyVersion(connector).getPkey();
		return connector.requestResult(
			true,
			AOServProtocol.CommandID.ADD,
			new AOServConnector.ResultRequest<Integer>() {
				int pkey;
				IntList invalidateList;

				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(SchemaTable.TableID.HTTPD_TOMCAT_SHARED_SITES.ordinal());
					out.writeCompressedInt(aoServer.pkey);
					out.writeUTF(siteName);
					out.writeUTF(packageObj.name.toString());
					out.writeUTF(siteUser.pkey.toString());
					out.writeUTF(siteGroup.pkey.toString());
					out.writeUTF(serverAdmin.toString());
					out.writeBoolean(useApache);
					out.writeCompressedInt(ipAddress==null?-1:ipAddress.pkey);
					out.writeUTF(primaryHttpHostname.toString());
					out.writeCompressedInt(altHttpHostnames.length);
					for(int c=0;c<altHttpHostnames.length;c++) out.writeUTF(altHttpHostnames[c].toString());
					out.writeBoolean(sharedTomcatName!=null);
					if(sharedTomcatName!=null) out.writeUTF(sharedTomcatName);
					out.writeCompressedInt(tv);
					out.writeBoolean(contentSrc!=null);
					if (contentSrc!=null) out.writeUTF(contentSrc.toString());
				}

				@Override
				public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AOServProtocol.DONE) {
						pkey=in.readCompressedInt();
						invalidateList=AOServConnector.readInvalidateList(in);
					} else {
						AOServProtocol.checkResult(code, in);
						throw new IOException("Unknown response code: "+code);
					}
				}

				@Override
				public Integer afterRelease() {
					connector.tablesUpdated(invalidateList);
					return pkey;
				}
			}
		);
	}

	@Override
	public HttpdTomcatSharedSite get(int pkey) throws IOException, SQLException {
		return getUniqueRow(HttpdTomcatSharedSite.COLUMN_TOMCAT_SITE, pkey);
	}

	List<HttpdTomcatSharedSite> getHttpdTomcatSharedSites(HttpdSharedTomcat tomcat) throws IOException, SQLException {
		return getIndexedRows(HttpdTomcatSharedSite.COLUMN_HTTPD_SHARED_TOMCAT, tomcat.pkey);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.HTTPD_TOMCAT_SHARED_SITES;
	}

	@Override
	boolean handleCommand(
		String[] args,
		Reader in,
		TerminalWriter out,
		TerminalWriter err,
		boolean isInteractive
	) throws IllegalArgumentException, SQLException, IOException {
		String command=args[0];
		if(command.equalsIgnoreCase(AOSHCommand.ADD_HTTPD_TOMCAT_SHARED_SITE)) {
			if(AOSH.checkMinParamCount(AOSHCommand.ADD_HTTPD_TOMCAT_SHARED_SITE, args, 13, err)) {
				// Create an array of all the alternate hostnames
				DomainName[] altHostnames=new DomainName[args.length-14];
				for(int i=14; i<args.length; i++) {
					altHostnames[i-14] = AOSH.parseDomainName(args[i], "alternate_http_hostname");
				}
				out.println(
					connector.getSimpleAOClient().addHttpdTomcatSharedSite(
						args[1],
						args[2],
						AOSH.parseAccountingCode(args[3], "package"),
						AOSH.parseUserId(args[4], "username"),
						AOSH.parseGroupId(args[5], "group"),
						AOSH.parseEmail(args[6], "server_admin_email"),
						AOSH.parseBoolean(args[7], "use_apache"),
						args[8].length()==0 ? null : AOSH.parseInetAddress(args[8], "ip_address"),
						args[9],
						AOSH.parseDomainName(args[12], "primary_http_hostname"),
						altHostnames,
						args[10],
						args[11],
						args[13].isEmpty() ? null : AOSH.parseUnixPath(args[13], "content_source")
					)
				);
				out.flush();
			}
			return true;
		}
		return false;
	}
}
