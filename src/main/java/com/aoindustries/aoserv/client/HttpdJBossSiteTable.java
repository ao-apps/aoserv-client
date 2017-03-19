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

import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.util.IntList;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  HttpdJBossSite
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdJBossSiteTable extends CachedTableIntegerKey<HttpdJBossSite> {

	protected HttpdJBossSiteTable(AOServConnector connector) {
		super(connector, HttpdJBossSite.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(HttpdJBossSite.COLUMN_TOMCAT_SITE_name+'.'+HttpdTomcatSite.COLUMN_HTTPD_SITE_name+'.'+HttpdSite.COLUMN_SITE_NAME_name, ASCENDING),
		new OrderBy(HttpdJBossSite.COLUMN_TOMCAT_SITE_name+'.'+HttpdTomcatSite.COLUMN_HTTPD_SITE_name+'.'+HttpdSite.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	int addHttpdJBossSite(
		final AOServer aoServer,
		final String siteName,
		final Package packageObj,
		final LinuxAccount siteUser,
		final LinuxGroup siteGroup,
		final String serverAdmin,
		final boolean useApache,
		final IPAddress ipAddress,
		final String primaryHttpHostname,
		final String[] altHttpHostnames,
		final int jBossVersion,
		final String contentSrc
	) throws IOException, SQLException {
		return connector.requestResult(
			true,
			new AOServConnector.ResultRequest<Integer>() {
				int pkey;
				IntList invalidateList;

				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(AOServProtocol.CommandID.ADD.ordinal());
					out.writeCompressedInt(SchemaTable.TableID.HTTPD_JBOSS_SITES.ordinal());
					out.writeCompressedInt(aoServer.pkey);
					out.writeUTF(siteName);
					out.writeUTF(packageObj.name.toString());
					out.writeUTF(siteUser.pkey);
					out.writeUTF(siteGroup.pkey);
					out.writeUTF(serverAdmin);
					out.writeBoolean(useApache);
					out.writeCompressedInt(ipAddress==null?-1:ipAddress.pkey);
					out.writeUTF(primaryHttpHostname);
					out.writeCompressedInt(altHttpHostnames.length);
					for(int c=0;c<altHttpHostnames.length;c++) out.writeUTF(altHttpHostnames[c]);
					out.writeCompressedInt(jBossVersion);
					out.writeBoolean(contentSrc!=null);
					if (contentSrc!=null) out.writeUTF(contentSrc);
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
	public HttpdJBossSite get(int pkey) throws SQLException, IOException {
		return getUniqueRow(HttpdJBossSite.COLUMN_TOMCAT_SITE, pkey);
	}

	HttpdJBossSite getHttpdJBossSiteByRMIPort(NetBind nb) throws IOException, SQLException {
		int pkey=nb.pkey;

		List<HttpdJBossSite> cached=getRows();
		int size=cached.size();
		for(int c=0;c<size;c++) {
			HttpdJBossSite jboss=cached.get(c);
			if(jboss.rmiBind==pkey) return jboss;
		}
		return null;
	}

	HttpdJBossSite getHttpdJBossSiteByJNPPort(NetBind nb) throws IOException, SQLException {
		int pkey=nb.pkey;

		List<HttpdJBossSite> cached=getRows();
		int size=cached.size();
		for(int c=0;c<size;c++) {
			HttpdJBossSite jboss=cached.get(c);
			if(jboss.jnpBind==pkey) return jboss;
		}
		return null;
	}

	HttpdJBossSite getHttpdJBossSiteByWebserverPort(NetBind nb) throws IOException, SQLException {
		int pkey=nb.pkey;

		List<HttpdJBossSite> cached=getRows();
		int size=cached.size();
		for(int c=0;c<size;c++) {
			HttpdJBossSite jboss=cached.get(c);
			if(jboss.webserverBind==pkey) return jboss;
		}
		return null;
	}

	HttpdJBossSite getHttpdJBossSiteByHypersonicPort(NetBind nb) throws IOException, SQLException {
		int pkey=nb.pkey;

		List<HttpdJBossSite> cached=getRows();
		int size=cached.size();
		for(int c=0;c<size;c++) {
			HttpdJBossSite jboss=cached.get(c);
			if(jboss.hypersonicBind==pkey) return jboss;
		}
		return null;
	}

	HttpdJBossSite getHttpdJBossSiteByJMXPort(NetBind nb) throws IOException, SQLException {
		int pkey=nb.pkey;

		List<HttpdJBossSite> cached=getRows();
		int size=cached.size();
		for(int c=0;c<size;c++) {
			HttpdJBossSite jboss=cached.get(c);
			if(jboss.jmxBind==pkey) return jboss;
		}
		return null;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.HTTPD_JBOSS_SITES;
	}

	@Override
	boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, SQLException, IOException {
		String command=args[0];
		if(command.equalsIgnoreCase(AOSHCommand.ADD_HTTPD_JBOSS_SITE)) {
			if(AOSH.checkMinParamCount(AOSHCommand.ADD_HTTPD_JBOSS_SITE, args, 12, err)) {
				// Create an array of all the alternate hostnames
				String[] altHostnames=new String[args.length-13];
				System.arraycopy(args, 13, altHostnames, 0, args.length-13);
				out.println(
					connector.getSimpleAOClient().addHttpdJBossSite(
						args[1],
						args[2],
						args[3],
						args[4],
						args[5],
						args[6],
						AOSH.parseBoolean(args[7], "use_apache"),
						args[8].length()==0 ? null : AOSH.parseInetAddress(args[8], "ip_address"),
						args[9],
						args[11],
						altHostnames,
						args[10],
						args[12]
					)
				);
				out.flush();
			}
			return true;
		}
		return false;
	}
}
