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
final public class HttpdTomcatStdSiteTable extends CachedTableIntegerKey<HttpdTomcatStdSite> {

	HttpdTomcatStdSiteTable(AOServConnector connector) {
		super(connector, HttpdTomcatStdSite.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(HttpdTomcatStdSite.COLUMN_TOMCAT_SITE_name+'.'+HttpdTomcatSite.COLUMN_HTTPD_SITE_name+'.'+HttpdSite.COLUMN_SITE_NAME_name, ASCENDING),
		new OrderBy(HttpdTomcatStdSite.COLUMN_TOMCAT_SITE_name+'.'+HttpdTomcatSite.COLUMN_HTTPD_SITE_name+'.'+HttpdSite.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	int addHttpdTomcatStdSite(
		final AOServer aoServer,
		final String siteName,
		final Package packageObj,
		final LinuxAccount jvmUser,
		final LinuxGroup jvmGroup,
		final Email serverAdmin,
		final boolean useApache,
		final IPAddress ipAddress,
		final DomainName primaryHttpHostname,
		final DomainName[] altHttpHostnames,
		final HttpdTomcatVersion tomcatVersion,
		final UnixPath contentSrc
	) throws IOException, SQLException {
		return connector.requestResult(
			true,
			AOServProtocol.CommandID.ADD,
			new AOServConnector.ResultRequest<Integer>() {
				int pkey;
				IntList invalidateList;

				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(SchemaTable.TableID.HTTPD_TOMCAT_STD_SITES.ordinal());
					out.writeCompressedInt(aoServer.pkey);
					out.writeUTF(siteName);
					out.writeUTF(packageObj.name.toString());
					out.writeUTF(jvmUser.pkey.toString());
					out.writeUTF(jvmGroup.pkey.toString());
					out.writeUTF(serverAdmin.toString());
					out.writeBoolean(useApache);
					out.writeCompressedInt(ipAddress==null?-1:ipAddress.pkey);
					out.writeUTF(primaryHttpHostname.toString());
					out.writeCompressedInt(altHttpHostnames.length);
					for(int c=0;c<altHttpHostnames.length;c++) out.writeUTF(altHttpHostnames[c].toString());
					out.writeCompressedInt(tomcatVersion.pkey);
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
	public HttpdTomcatStdSite get(int pkey) throws IOException, SQLException {
		return getUniqueRow(HttpdTomcatStdSite.COLUMN_TOMCAT_SITE, pkey);
	}

	public HttpdTomcatStdSite getHttpdTomcatStdSiteByShutdownPort(NetBind nb) throws IOException, SQLException {
		int pkey=nb.pkey;

		List<HttpdTomcatStdSite> cached=getRows();
		int size=cached.size();
		for(int c=0;c<size;c++) {
			HttpdTomcatStdSite tomcat=cached.get(c);
			if(tomcat.tomcat4_shutdown_port==pkey) return tomcat;
		}
		return null;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.HTTPD_TOMCAT_STD_SITES;
	}

	@Override
	boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, SQLException, IOException {
		String command=args[0];
		if(command.equalsIgnoreCase(AOSHCommand.ADD_HTTPD_TOMCAT_STD_SITE)) {
			if(AOSH.checkMinParamCount(AOSHCommand.ADD_HTTPD_TOMCAT_STD_SITE, args, 12, err)) {
				// Create an array of all the alternate hostnames
				DomainName[] altHostnames=new DomainName[args.length-13];
				for(int i=13; i<args.length; i++) {
					altHostnames[i-13] = AOSH.parseDomainName(args[i], "alternate_http_hostname");
				}
				out.println(
					connector.getSimpleAOClient().addHttpdTomcatStdSite(
						args[1],
						args[2],
						AOSH.parseAccountingCode(args[3], "package"),
						AOSH.parseUserId(args[4], "username"),
						AOSH.parseGroupId(args[5], "group"),
						AOSH.parseEmail(args[6], "server_admin_email"),
						AOSH.parseBoolean(args[7], "use_apache"),
						args[8].length()==0 ? null : AOSH.parseInetAddress(args[8], "ip_address"),
						args[9],
						AOSH.parseDomainName(args[11], "primary_http_hostname"),
						altHostnames,
						args[10],
						args[12].isEmpty() ? null : AOSH.parseUnixPath(args[12], "content_source")
					)
				);
				out.flush();
			}
			return true;
		}
		return false;
	}
}
