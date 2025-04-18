/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2023, 2025  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.web.tomcat;

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
 * @see  PrivateTomcatSite
 *
 * @author  AO Industries, Inc.
 */
public final class PrivateTomcatSiteTable extends CachedTableIntegerKey<PrivateTomcatSite> {

  PrivateTomcatSiteTable(AoservConnector connector) {
    super(connector, PrivateTomcatSite.class);
  }

  private static final OrderBy[] defaultOrderBy = {
      new OrderBy(PrivateTomcatSite.COLUMN_TOMCAT_SITE_name + '.' + Site.COLUMN_HTTPD_SITE_name
          + '.' + com.aoindustries.aoserv.client.web.Site.COLUMN_NAME_name, ASCENDING),
      new OrderBy(PrivateTomcatSite.COLUMN_TOMCAT_SITE_name + '.' + Site.COLUMN_HTTPD_SITE_name
          + '.' + com.aoindustries.aoserv.client.web.Site.COLUMN_AO_SERVER_name + '.' + Server.COLUMN_HOSTNAME_name, ASCENDING)
  };

  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  public int addHttpdTomcatStdSite(
      final Server aoServer,
      final String siteName,
      final Package packageObj,
      final User jvmUser,
      final Group jvmGroup,
      final Email serverAdmin,
      final boolean useApache,
      final IpAddress ipAddress,
      final DomainName primaryHttpHostname,
      final DomainName[] altHttpHostnames,
      final Version tomcatVersion
  ) throws IOException, SQLException {
    return connector.requestResult(
        true,
        AoservProtocol.CommandId.ADD,
        new AoservConnector.ResultRequest<>() {
          private int pkey;
          private IntList invalidateList;

          @Override
          public void writeRequest(StreamableOutput out) throws IOException {
            out.writeCompressedInt(Table.TableId.HTTPD_TOMCAT_STD_SITES.ordinal());
            out.writeCompressedInt(aoServer.getPkey());
            out.writeUTF(siteName);
            out.writeUTF(packageObj.getName().toString());
            out.writeUTF(jvmUser.getUsername_id().toString());
            out.writeUTF(jvmGroup.getName().toString());
            out.writeUTF(serverAdmin.toString());
            out.writeBoolean(useApache);
            out.writeCompressedInt(ipAddress == null ? -1 : ipAddress.getPkey());
            out.writeUTF(primaryHttpHostname.toString());
            out.writeCompressedInt(altHttpHostnames.length);
            for (DomainName altHttpHostname : altHttpHostnames) {
              out.writeUTF(altHttpHostname.toString());
            }
            out.writeCompressedInt(tomcatVersion.getPkey());
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
  public PrivateTomcatSite get(int pkey) throws IOException, SQLException {
    return getUniqueRow(PrivateTomcatSite.COLUMN_TOMCAT_SITE, pkey);
  }

  public PrivateTomcatSite getHttpdTomcatStdSiteByShutdownPort(Bind bind) throws IOException, SQLException {
    return getUniqueRow(PrivateTomcatSite.COLUMN_TOMCAT4_SHUTDOWN_PORT, bind.getId());
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.HTTPD_TOMCAT_STD_SITES;
  }

  @Override
  public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, SQLException, IOException {
    String command = args[0];
    if (command.equalsIgnoreCase(Command.ADD_HTTPD_TOMCAT_STD_SITE)) {
      if (Aosh.checkMinParamCount(Command.ADD_HTTPD_TOMCAT_STD_SITE, args, 11, err)) {
        // Create an array of all the alternate hostnames
        DomainName[] altHostnames = new DomainName[args.length - 12];
        for (int i = 12; i < args.length; i++) {
          altHostnames[i - 12] = Aosh.parseDomainName(args[i], "alternate_http_hostname");
        }
        out.println(
            connector.getSimpleClient().addHttpdTomcatStdSite(
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
    } else if (command.equalsIgnoreCase(Command.web_tomcat_PrivateTomcatSite_maxParameterCount_set)) {
      if (Aosh.checkParamCount(Command.web_tomcat_PrivateTomcatSite_maxParameterCount_set, args, 3, err)) {
        connector.getSimpleClient().setHttpdTomcatStdSiteMaxParameterCount(
            args[1],
            args[2],
            args[3].isEmpty() ? -1 : Aosh.parseInt(args[3], "maxParameterCount")
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.SET_HTTPD_TOMCAT_STD_SITE_MAX_POST_SIZE)) {
      if (Aosh.checkParamCount(Command.SET_HTTPD_TOMCAT_STD_SITE_MAX_POST_SIZE, args, 3, err)) {
        connector.getSimpleClient().setHttpdTomcatStdSiteMaxPostSize(
            args[1],
            args[2],
            args[3].isEmpty() ? -1 : Aosh.parseInt(args[3], "max_post_size")
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.SET_HTTPD_TOMCAT_STD_SITE_UNPACK_WARS)) {
      if (Aosh.checkParamCount(Command.SET_HTTPD_TOMCAT_STD_SITE_UNPACK_WARS, args, 3, err)) {
        connector.getSimpleClient().setHttpdTomcatStdSiteUnpackWars(
            args[1],
            args[2],
            Aosh.parseBoolean(args[3], "unpack_wars")
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.SET_HTTPD_TOMCAT_STD_SITE_AUTO_DEPLOY)) {
      if (Aosh.checkParamCount(Command.SET_HTTPD_TOMCAT_STD_SITE_AUTO_DEPLOY, args, 3, err)) {
        connector.getSimpleClient().setHttpdTomcatStdSiteAutoDeploy(
            args[1],
            args[2],
            Aosh.parseBoolean(args[3], "auto_deploy")
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.web_tomcat_PrivateTomcatSite_undeployOldVersions_set)) {
      if (Aosh.checkParamCount(Command.web_tomcat_PrivateTomcatSite_undeployOldVersions_set, args, 3, err)) {
        connector.getSimpleClient().setHttpdTomcatStdSiteUndeployOldVersions(
            args[1],
            args[2],
            Aosh.parseBoolean(args[3], "undeployOldVersions")
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.web_tomcat_PrivateTomcatSite_tomcatAuthentication_set)) {
      if (Aosh.checkParamCount(Command.web_tomcat_PrivateTomcatSite_tomcatAuthentication_set, args, 3, err)) {
        connector.getSimpleClient().setHttpdTomcatStdSiteTomcatAuthentication(
            args[1],
            args[2],
            Aosh.parseBoolean(args[3], "tomcatAuthentication")
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.SET_HTTPD_TOMCAT_STD_SITE_VERSION)) {
      if (Aosh.checkParamCount(Command.SET_HTTPD_TOMCAT_STD_SITE_VERSION, args, 3, err)) {
        connector.getSimpleClient().setHttpdTomcatStdSiteVersion(
            args[1],
            args[2],
            args[3]
        );
      }
      return true;
    }
    return false;
  }
}
