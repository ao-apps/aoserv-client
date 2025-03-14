/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2025  AO Industries, Inc.
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
 * along with aoserv-client.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.aoindustries.aoserv.client.web.jboss;

import com.aoapps.collections.IntList;
import com.aoapps.hodgepodge.io.TerminalWriter;
import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.net.DomainName;
import com.aoapps.net.Email;
import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.aosh.Aosh;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.linux.Group;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.linux.User;
import com.aoindustries.aoserv.client.net.Bind;
import com.aoindustries.aoserv.client.net.IpAddress;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;

/**
 * @see  Site
 *
 * @author  AO Industries, Inc.
 */
public final class SiteTable extends CachedTableIntegerKey<Site> {

  SiteTable(AoservConnector connector) {
    super(connector, Site.class);
  }

  private static final OrderBy[] defaultOrderBy = {
      new OrderBy(Site.COLUMN_TOMCAT_SITE_name + '.' + com.aoindustries.aoserv.client.web.tomcat.Site.COLUMN_HTTPD_SITE_name
          + '.' + com.aoindustries.aoserv.client.web.Site.COLUMN_NAME_name, ASCENDING),
      new OrderBy(Site.COLUMN_TOMCAT_SITE_name + '.' + com.aoindustries.aoserv.client.web.tomcat.Site.COLUMN_HTTPD_SITE_name
          + '.' + com.aoindustries.aoserv.client.web.Site.COLUMN_AO_SERVER_name + '.' + Server.COLUMN_HOSTNAME_name, ASCENDING)
  };

  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  public int addHttpdJbossSite(
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
      final Version jbossVersion
  ) throws IOException, SQLException {
    return connector.requestResult(
        true,
        AoservProtocol.CommandId.ADD,
        new AoservConnector.ResultRequest<>() {
          private int pkey;
          private IntList invalidateList;

          @Override
          public void writeRequest(StreamableOutput out) throws IOException {
            out.writeCompressedInt(Table.TableId.HTTPD_JBOSS_SITES.ordinal());
            out.writeCompressedInt(aoServer.getPkey());
            out.writeUTF(siteName);
            out.writeUTF(packageObj.getName().toString());
            out.writeUTF(siteUser.getUsername_id().toString());
            out.writeUTF(siteGroup.getName().toString());
            out.writeUTF(serverAdmin.toString());
            out.writeBoolean(useApache);
            out.writeCompressedInt(ipAddress == null ? -1 : ipAddress.getPkey());
            out.writeUTF(primaryHttpHostname.toString());
            out.writeCompressedInt(altHttpHostnames.length);
            for (DomainName altHttpHostname : altHttpHostnames) {
              out.writeUTF(altHttpHostname.toString());
            }
            out.writeCompressedInt(jbossVersion.getPkey());
          }

          @Override
          public void readResponse(StreamableInput in) throws IOException, SQLException {
            int code = in.readByte();
            if (code == AoservProtocol.DONE) {
              pkey = in.readCompressedInt();
              invalidateList = AoservConnector.readInvalidateList(in);
            } else {
              AoservProtocol.checkResult(code, in);
              throw new IOException("Unknown response code: " + code);
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

  public Site getHttpdJbossSiteByRmiPort(Bind bind) throws IOException, SQLException {
    return getUniqueRow(Site.COLUMN_RMI_BIND, bind.getId());
  }

  public Site getHttpdJbossSiteByJnpPort(Bind bind) throws IOException, SQLException {
    return getUniqueRow(Site.COLUMN_JNP_BIND, bind.getId());
  }

  public Site getHttpdJbossSiteByWebserverPort(Bind bind) throws IOException, SQLException {
    return getUniqueRow(Site.COLUMN_WEBSERVER_BIND, bind.getId());
  }

  public Site getHttpdJbossSiteByHypersonicPort(Bind bind) throws IOException, SQLException {
    return getUniqueRow(Site.COLUMN_HYPERSONIC_BIND, bind.getId());
  }

  public Site getHttpdJbossSiteByJmxPort(Bind bind) throws IOException, SQLException {
    return getUniqueRow(Site.COLUMN_JMX_BIND, bind.getId());
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.HTTPD_JBOSS_SITES;
  }

  @Override
  public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, SQLException, IOException {
    String command = args[0];
    if (command.equalsIgnoreCase(Command.ADD_HTTPD_JBOSS_SITE)) {
      if (Aosh.checkMinParamCount(Command.ADD_HTTPD_JBOSS_SITE, args, 11, err)) {
        // Create an array of all the alternate hostnames
        DomainName[] altHostnames = new DomainName[args.length - 12];
        for (int i = 12; i < args.length; i++) {
          altHostnames[i - 12] = Aosh.parseDomainName(args[i], "alternate_http_hostname");
        }
        out.println(
            connector.getSimpleClient().addHttpdJbossSite(
                args[1],
                args[2],
                Aosh.parseAccountingCode(args[3], "package"),
                Aosh.parseLinuxUserName(args[4], "username"),
                Aosh.parseGroupName(args[5], "group"),
                Aosh.parseEmail(args[6], "server_admin_email"),
                Aosh.parseBoolean(args[7], "use_apache"),
                args[8].length() == 0 ? null : Aosh.parseInetAddress(args[8], "ip_address"),
                args[9],
                Aosh.parseDomainName(args[11], "primary_http_hostname"),
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
