/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2012, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.web;

import com.aoapps.hodgepodge.io.TerminalWriter;
import com.aoapps.lang.io.WriterOutputStream;
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.SimpleAOClient;
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.linux.User;
import com.aoindustries.aoserv.client.linux.UserServer;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see  Site
 *
 * @author  AO Industries, Inc.
 */
public final class SiteTable extends CachedTableIntegerKey<Site> {

  SiteTable(AOServConnector connector) {
    super(connector, Site.class);
  }

  private static final OrderBy[] defaultOrderBy = {
    new OrderBy(Site.COLUMN_NAME_name, ASCENDING),
    new OrderBy(Site.COLUMN_AO_SERVER_name+'.'+Server.COLUMN_HOSTNAME_name, ASCENDING)
  };
  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  public String generateSiteName(String template) throws IOException, SQLException {
    return connector.requestStringQuery(true, AoservProtocol.CommandID.GENERATE_SITE_NAME, template);
  }

  @Override
  public Site get(int id) throws IOException, SQLException {
    return getUniqueRow(Site.COLUMN_ID, id);
  }

  public Site getHttpdSite(String siteName, Server ao) throws IOException, SQLException {
    int aoPKey=ao.getPkey();

    List<Site> cached=getRows();
    int size=cached.size();
    for (int c=0;c<size;c++) {
      Site site=cached.get(c);
      if (
        site.getAoServer_server_pkey() == aoPKey
        && site.getName().equals(siteName)
      ) {
        return site;
      }
    }
    return null;
  }

  List<Site> getHttpdSites(HttpdServer server) throws IOException, SQLException {
    int serverPKey=server.getPkey();

    List<Site> cached=getRows();
    int size=cached.size();
    List<Site> matches=new ArrayList<>(size);
    for (int c=0;c<size;c++) {
      Site site=cached.get(c);
      for (VirtualHost bind : site.getHttpdSiteBinds()) {
        if (bind.getHttpdBind().getHttpdServer_pkey() == serverPKey) {
          matches.add(site);
          break;
        }
      }
    }
    return matches;
  }

  public List<Site> getHttpdSites(Server ao) throws IOException, SQLException {
    return getIndexedRows(Site.COLUMN_AO_SERVER, ao.getPkey());
  }

  public List<Site> getHttpdSites(Package pk) throws IOException, SQLException {
    return getIndexedRows(Site.COLUMN_PACKAGE, pk.getName());
  }

  public List<Site> getHttpdSites(UserServer lsa) throws IOException, SQLException {
    User.Name lsaUsername = lsa.getLinuxAccount_username_id();
    int aoServer = lsa.getAoServer_server_id();

    List<Site> cached=getRows();
    int size=cached.size();
    List<Site> matches=new ArrayList<>(size);
    for (int c=0;c<size;c++) {
      Site site=cached.get(c);
      if (
        site.getAoServer_server_pkey() == aoServer
        && site.getLinuxAccount_username().equals(lsaUsername)
      ) {
        matches.add(site);
      }
    }
    return matches;
  }

  @Override
  public Table.TableID getTableID() {
    return Table.TableID.HTTPD_SITES;
  }

  @Override
  public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, SQLException, IOException {
    String command=args[0];
    if (command.equalsIgnoreCase(Command.CHECK_SITE_NAME)) {
      if (AOSH.checkParamCount(Command.CHECK_SITE_NAME, args, 1, err)) {
        try {
          SimpleAOClient.checkSiteName(args[1]);
          out.println("true");
        } catch (IllegalArgumentException iae) {
          out.print("aosh: "+Command.CHECK_SITE_NAME+": ");
          out.println(iae.getMessage());
        }
        out.flush();
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.DISABLE_HTTPD_SITE)) {
      if (AOSH.checkParamCount(Command.DISABLE_HTTPD_SITE, args, 3, err)) {
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
    } else if (command.equalsIgnoreCase(Command.ENABLE_HTTPD_SITE)) {
      if (AOSH.checkParamCount(Command.ENABLE_HTTPD_SITE, args, 2, err)) {
        connector.getSimpleAOClient().enableHttpdSite(args[1], args[2]);
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.GENERATE_SITE_NAME)) {
      if (AOSH.checkParamCount(Command.GENERATE_SITE_NAME, args, 1, err)) {
        out.println(connector.getSimpleAOClient().generateSiteName(args[1]));
        out.flush();
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.GET_AWSTATS_FILE)) {
      if (AOSH.checkParamCount(Command.GET_AWSTATS_FILE, args, 4, err)) {
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
    /*} else if (command.equalsIgnoreCase(AOSHCommand.INITIALIZE_HTTPD_SITE_PASSWD_FILE)) {
      if (AOSH.checkParamCount(AOSHCommand.INITIALIZE_HTTPD_SITE_PASSWD_FILE, args, 4, err)) {
        connector.getSimpleAOClient().initializeHttpdSitePasswdFile(
          args[1],
          args[2],
          args[3],
          args[4]
        );
      }
      return true;
     */
    } else if (command.equalsIgnoreCase(Command.IS_SITE_NAME_AVAILABLE)) {
      if (AOSH.checkParamCount(Command.IS_SITE_NAME_AVAILABLE, args, 1, err)) {
        out.println(connector.getSimpleAOClient().isSiteNameAvailable(args[1]));
        out.flush();
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.REMOVE_HTTPD_SITE)) {
      if (AOSH.checkParamCount(Command.REMOVE_HTTPD_SITE, args, 2, err)) {
        connector.getSimpleAOClient().removeHttpdSite(args[1], args[2]);
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.SET_HTTPD_SITE_SERVER_ADMIN)) {
      if (AOSH.checkParamCount(Command.SET_HTTPD_SITE_SERVER_ADMIN, args, 3, err)) {
        connector.getSimpleAOClient().setHttpdSiteServerAdmin(
          args[1],
          args[2],
          AOSH.parseEmail(args[3], "email_address")
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.SET_HTTPD_SITE_IS_MANUAL)) {
      if (AOSH.checkParamCount(Command.SET_HTTPD_SITE_IS_MANUAL, args, 3, err)) {
        connector.getSimpleAOClient().setHttpdSiteIsManual(
          args[1],
          args[2],
          AOSH.parseBoolean(args[3], "is_manual")
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.WAIT_FOR_HTTPD_SITE_REBUILD)) {
      if (AOSH.checkParamCount(Command.WAIT_FOR_HTTPD_SITE_REBUILD, args, 1, err)) {
        connector.getSimpleAOClient().waitForHttpdSiteRebuild(args[1]);
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.SET_HTTPD_SITE_PHP_VERSION)) {
      if (AOSH.checkParamCount(Command.SET_HTTPD_SITE_PHP_VERSION, args, 3, err)) {
        connector.getSimpleAOClient().setHttpdSitePhpVersion(
          args[1],
          args[2],
          args[3]
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.SET_HTTPD_SITE_ENABLE_CGI)) {
      if (AOSH.checkParamCount(Command.SET_HTTPD_SITE_ENABLE_CGI, args, 3, err)) {
        connector.getSimpleAOClient().setHttpdSiteEnableCgi(
          args[1],
          args[2],
          AOSH.parseBoolean(args[3], "enable_cgi")
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.SET_HTTPD_SITE_ENABLE_SSI)) {
      if (AOSH.checkParamCount(Command.SET_HTTPD_SITE_ENABLE_SSI, args, 3, err)) {
        connector.getSimpleAOClient().setHttpdSiteEnableSsi(
          args[1],
          args[2],
          AOSH.parseBoolean(args[3], "enable_ssi")
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.SET_HTTPD_SITE_ENABLE_HTACCESS)) {
      if (AOSH.checkParamCount(Command.SET_HTTPD_SITE_ENABLE_HTACCESS, args, 3, err)) {
        connector.getSimpleAOClient().setHttpdSiteEnableHtaccess(
          args[1],
          args[2],
          AOSH.parseBoolean(args[3], "enable_htaccess")
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.SET_HTTPD_SITE_ENABLE_INDEXES)) {
      if (AOSH.checkParamCount(Command.SET_HTTPD_SITE_ENABLE_INDEXES, args, 3, err)) {
        connector.getSimpleAOClient().setHttpdSiteEnableIndexes(
          args[1],
          args[2],
          AOSH.parseBoolean(args[3], "enable_indexes")
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.SET_HTTPD_SITE_ENABLE_FOLLOW_SYMLINKS)) {
      if (AOSH.checkParamCount(Command.SET_HTTPD_SITE_ENABLE_FOLLOW_SYMLINKS, args, 3, err)) {
        connector.getSimpleAOClient().setHttpdSiteEnableFollowSymlinks(
          args[1],
          args[2],
          AOSH.parseBoolean(args[3], "enable_follow_symlinks")
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.SET_HTTPD_SITE_ENABLE_ANONYMOUS_FTP)) {
      if (AOSH.checkParamCount(Command.SET_HTTPD_SITE_ENABLE_ANONYMOUS_FTP, args, 3, err)) {
        connector.getSimpleAOClient().setHttpdSiteEnableAnonymousFtp(
          args[1],
          args[2],
          AOSH.parseBoolean(args[3], "enable_anonymous_ftp")
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.SET_HTTPD_SITE_BLOCK_TRACE_TRACK)) {
      if (AOSH.checkParamCount(Command.SET_HTTPD_SITE_BLOCK_TRACE_TRACK, args, 3, err)) {
        connector.getSimpleAOClient().setHttpdSiteBlockTraceTrack(
          args[1],
          args[2],
          AOSH.parseBoolean(args[3], "block_trace_track")
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.SET_HTTPD_SITE_BLOCK_SCM)) {
      if (AOSH.checkParamCount(Command.SET_HTTPD_SITE_BLOCK_SCM, args, 3, err)) {
        connector.getSimpleAOClient().setHttpdSiteBlockScm(
          args[1],
          args[2],
          AOSH.parseBoolean(args[3], "block_scm")
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.SET_HTTPD_SITE_BLOCK_CORE_DUMPS)) {
      if (AOSH.checkParamCount(Command.SET_HTTPD_SITE_BLOCK_CORE_DUMPS, args, 3, err)) {
        connector.getSimpleAOClient().setHttpdSiteBlockCoreDumps(
          args[1],
          args[2],
          AOSH.parseBoolean(args[3], "block_core_dumps")
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.SET_HTTPD_SITE_BLOCK_EDITOR_BACKUPS)) {
      if (AOSH.checkParamCount(Command.SET_HTTPD_SITE_BLOCK_EDITOR_BACKUPS, args, 3, err)) {
        connector.getSimpleAOClient().setHttpdSiteBlockEditorBackups(
          args[1],
          args[2],
          AOSH.parseBoolean(args[3], "block_editor_backups")
        );
      }
      return true;
    } else {
      return false;
    }
  }

  public boolean isSiteNameAvailable(String sitename) throws IOException, SQLException {
    return connector.requestBooleanQuery(true, AoservProtocol.CommandID.IS_SITE_NAME_AVAILABLE, sitename);
  }

  public void waitForRebuild(Server aoServer) throws IOException, SQLException {
    connector.requestUpdate(
      true,
      AoservProtocol.CommandID.WAIT_FOR_REBUILD,
      Table.TableID.HTTPD_SITES,
      aoServer.getPkey()
    );
  }
}
