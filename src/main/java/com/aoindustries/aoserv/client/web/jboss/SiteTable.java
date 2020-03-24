/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018, 2019, 2020  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.web.jboss;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.linux.Group;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.linux.User;
import com.aoindustries.aoserv.client.net.Bind;
import com.aoindustries.aoserv.client.net.IpAddress;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.collections.IntList;
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.io.stream.StreamableInput;
import com.aoindustries.io.stream.StreamableOutput;
import com.aoindustries.net.DomainName;
import com.aoindustries.net.Email;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  Site
 *
 * @author  AO Industries, Inc.
 */
final public class SiteTable extends CachedTableIntegerKey<Site> {

	SiteTable(AOServConnector connector) {
		super(connector, Site.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(Site.COLUMN_TOMCAT_SITE_name+'.'+com.aoindustries.aoserv.client.web.tomcat.Site.COLUMN_HTTPD_SITE_name+'.'+com.aoindustries.aoserv.client.web.Site.COLUMN_NAME_name, ASCENDING),
		new OrderBy(Site.COLUMN_TOMCAT_SITE_name+'.'+com.aoindustries.aoserv.client.web.tomcat.Site.COLUMN_HTTPD_SITE_name+'.'+com.aoindustries.aoserv.client.web.Site.COLUMN_AO_SERVER_name+'.'+Server.COLUMN_HOSTNAME_name, ASCENDING)
	};
	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	public int addHttpdJBossSite(
		final Server aoServer,
		final String siteName,
		final Package packageObj,
		final User siteUser,
		final Group siteGroup,
		final Email serverAdmin,
		final boolean useApache,
		final IpAddress ipAddress,
		final DomainName primaryHttpHostname,
		final DomainName[] altHttpHostnames,
		final Version jBossVersion
	) throws IOException, SQLException {
		return connector.requestResult(
			true,
			AoservProtocol.CommandID.ADD,
			new AOServConnector.ResultRequest<Integer>() {
				int pkey;
				IntList invalidateList;

				@Override
				public void writeRequest(StreamableOutput out) throws IOException {
					out.writeCompressedInt(Table.TableID.HTTPD_JBOSS_SITES.ordinal());
					out.writeCompressedInt(aoServer.getPkey());
					out.writeUTF(siteName);
					out.writeUTF(packageObj.getName().toString());
					out.writeUTF(siteUser.getUsername_id().toString());
					out.writeUTF(siteGroup.getName().toString());
					out.writeUTF(serverAdmin.toString());
					out.writeBoolean(useApache);
					out.writeCompressedInt(ipAddress==null?-1:ipAddress.getPkey());
					out.writeUTF(primaryHttpHostname.toString());
					out.writeCompressedInt(altHttpHostnames.length);
					for(int c=0;c<altHttpHostnames.length;c++) out.writeUTF(altHttpHostnames[c].toString());
					out.writeCompressedInt(jBossVersion.getPkey());
				}

				@Override
				public void readResponse(StreamableInput in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AoservProtocol.DONE) {
						pkey=in.readCompressedInt();
						invalidateList=AOServConnector.readInvalidateList(in);
					} else {
						AoservProtocol.checkResult(code, in);
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
	public Site get(int pkey) throws SQLException, IOException {
		return getUniqueRow(Site.COLUMN_TOMCAT_SITE, pkey);
	}

	public Site getHttpdJBossSiteByRMIPort(Bind nb) throws IOException, SQLException {
		int pkey=nb.getId();

		List<Site> cached=getRows();
		int size=cached.size();
		for(int c=0;c<size;c++) {
			Site jboss=cached.get(c);
			if(jboss.rmiBind==pkey) return jboss;
		}
		return null;
	}

	public Site getHttpdJBossSiteByJNPPort(Bind nb) throws IOException, SQLException {
		int pkey=nb.getId();

		List<Site> cached=getRows();
		int size=cached.size();
		for(int c=0;c<size;c++) {
			Site jboss=cached.get(c);
			if(jboss.jnpBind==pkey) return jboss;
		}
		return null;
	}

	public Site getHttpdJBossSiteByWebserverPort(Bind nb) throws IOException, SQLException {
		int pkey=nb.getId();

		List<Site> cached=getRows();
		int size=cached.size();
		for(int c=0;c<size;c++) {
			Site jboss=cached.get(c);
			if(jboss.webserverBind==pkey) return jboss;
		}
		return null;
	}

	public Site getHttpdJBossSiteByHypersonicPort(Bind nb) throws IOException, SQLException {
		int pkey=nb.getId();

		List<Site> cached=getRows();
		int size=cached.size();
		for(int c=0;c<size;c++) {
			Site jboss=cached.get(c);
			if(jboss.hypersonicBind==pkey) return jboss;
		}
		return null;
	}

	public Site getHttpdJBossSiteByJMXPort(Bind nb) throws IOException, SQLException {
		int pkey=nb.getId();

		List<Site> cached=getRows();
		int size=cached.size();
		for(int c=0;c<size;c++) {
			Site jboss=cached.get(c);
			if(jboss.jmxBind==pkey) return jboss;
		}
		return null;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.HTTPD_JBOSS_SITES;
	}

	@Override
	public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, SQLException, IOException {
		String command=args[0];
		if(command.equalsIgnoreCase(Command.ADD_HTTPD_JBOSS_SITE)) {
			if(AOSH.checkMinParamCount(Command.ADD_HTTPD_JBOSS_SITE, args, 11, err)) {
				// Create an array of all the alternate hostnames
				DomainName[] altHostnames=new DomainName[args.length-12];
				for(int i=12; i<args.length; i++) {
					altHostnames[i-12] = AOSH.parseDomainName(args[i], "alternate_http_hostname");
				}
				out.println(
					connector.getSimpleAOClient().addHttpdJBossSite(
						args[1],
						args[2],
						AOSH.parseAccountingCode(args[3], "package"),
						AOSH.parseLinuxUserName(args[4], "username"),
						AOSH.parseGroupName(args[5], "group"),
						AOSH.parseEmail(args[6], "server_admin_email"),
						AOSH.parseBoolean(args[7], "use_apache"),
						args[8].length()==0 ? null : AOSH.parseInetAddress(args[8], "ip_address"),
						args[9],
						AOSH.parseDomainName(args[11], "primary_http_hostname"),
						altHostnames,
						args[10]
					)
				);
				out.flush();
			}
			return true;
		}
		return false;
	}
}
