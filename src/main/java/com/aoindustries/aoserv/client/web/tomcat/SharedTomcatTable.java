/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2012, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2023  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.SimpleAoservClient;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.aosh.Aosh;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.linux.GroupServer;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.linux.UserServer;
import com.aoindustries.aoserv.client.net.Bind;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see  SharedTomcat
 *
 * @author  AO Industries, Inc.
 */
public final class SharedTomcatTable extends CachedTableIntegerKey<SharedTomcat> {

  SharedTomcatTable(AoservConnector connector) {
    super(connector, SharedTomcat.class);
  }

  private static final OrderBy[] defaultOrderBy = {
      new OrderBy(SharedTomcat.COLUMN_NAME_name, ASCENDING),
      new OrderBy(SharedTomcat.COLUMN_AO_SERVER_name + '.' + Server.COLUMN_HOSTNAME_name, ASCENDING)
  };

  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  public int addHttpdSharedTomcat(
      final String name,
      final Server aoServer,
      Version version,
      final UserServer lsa,
      final GroupServer lsg
  ) throws IOException, SQLException {
    final int tvPkey = version.getTechnologyVersion(connector).getPkey();
    return connector.requestResult(
        true,
        AoservProtocol.CommandId.ADD,
        // Java 9: new AoservConnector.ResultRequest<>
        new AoservConnector.ResultRequest<Integer>() {
          private int pkey;
          private IntList invalidateList;

          @Override
          public void writeRequest(StreamableOutput out) throws IOException {
            out.writeCompressedInt(Table.TableId.HTTPD_SHARED_TOMCATS.ordinal());
            out.writeUTF(name);
            out.writeCompressedInt(aoServer.getPkey());
            out.writeCompressedInt(tvPkey);
            out.writeUTF(lsa.getLinuxAccount_username_id().toString());
            out.writeUTF(lsg.getLinuxGroup_name().toString());
          }

          @Override
          public void readResponse(StreamableInput in) throws IOException, SQLException {
            int code = in.readByte();
            if (code == AoservProtocol.DONE) {
              pkey = in.readCompressedInt();
              invalidateList = AoservConnector.readInvalidateList(in);
            } else {
              AoservProtocol.checkResult(code, in);
              throw new IOException("Unexpected response code: " + code);
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

  public String generateSharedTomcatName(String template) throws IOException, SQLException {
    return connector.requestStringQuery(true, AoservProtocol.CommandId.GENERATE_SHARED_TOMCAT_NAME, template);
  }

  @Override
  public SharedTomcat get(int pkey) throws SQLException, IOException {
    return getUniqueRow(SharedTomcat.COLUMN_PKEY, pkey);
  }

  public SharedTomcat getHttpdSharedTomcat(Worker hw) throws SQLException, IOException {
    return getUniqueRow(SharedTomcat.COLUMN_TOMCAT4_WORKER, hw.getPkey());
  }

  public SharedTomcat getHttpdSharedTomcatByShutdownPort(Bind nb) throws SQLException, IOException {
    return getUniqueRow(SharedTomcat.COLUMN_TOMCAT4_SHUTDOWN_PORT, nb.getId());
  }

  public List<SharedTomcat> getHttpdSharedTomcats(UserServer lsa) throws IOException, SQLException {
    return getIndexedRows(SharedTomcat.COLUMN_LINUX_SERVER_ACCOUNT, lsa.getPkey());
  }

  public List<SharedTomcat> getHttpdSharedTomcats(Package pk) throws IOException, SQLException {
    Account.Name pkname = pk.getName();

    List<SharedTomcat> cached = getRows();
    int size = cached.size();
    List<SharedTomcat> matches = new ArrayList<>(size);
    for (int c = 0; c < size; c++) {
      SharedTomcat hst = cached.get(c);
      if (hst.getLinuxServerGroup().getLinuxGroup().getPackage_name().equals(pkname)) {
        matches.add(hst);
      }
    }
    return matches;
  }

  public List<SharedTomcat> getHttpdSharedTomcats(Server ao) throws IOException, SQLException {
    return getIndexedRows(SharedTomcat.COLUMN_AO_SERVER, ao.getPkey());
  }

  public SharedTomcat getHttpdSharedTomcat(String name, Server ao) throws IOException, SQLException {
    // Use the index first
    List<SharedTomcat> cached = getHttpdSharedTomcats(ao);
    int size = cached.size();
    for (int c = 0; c < size; c++) {
      SharedTomcat tomcat = cached.get(c);
      if (tomcat.getName().equals(name)) {
        return tomcat;
      }
    }
    return null;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.HTTPD_SHARED_TOMCATS;
  }

  @Override
  public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, SQLException, IOException {
    String command = args[0];
    if (command.equalsIgnoreCase(Command.ADD_HTTPD_SHARED_TOMCAT)) {
      if (Aosh.checkMinParamCount(Command.ADD_HTTPD_SHARED_TOMCAT, args, 5, err)) {
        // Create an array of all the alternate hostnames
        out.println(
            connector.getSimpleClient().addHttpdSharedTomcat(
                args[1],
                args[2],
                args[3],
                Aosh.parseLinuxUserName(args[4], "linux_server_account"),
                Aosh.parseGroupName(args[5], "linux_server_group")
            )
        );
        out.flush();
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.CHECK_SHARED_TOMCAT_NAME)) {
      if (Aosh.checkParamCount(Command.CHECK_SHARED_TOMCAT_NAME, args, 1, err)) {
        try {
          SimpleAoservClient.checkSharedTomcatName(args[1]);
          out.println("true");
        } catch (IllegalArgumentException iae) {
          out.print("aosh: " + Command.CHECK_SHARED_TOMCAT_NAME + ": ");
          out.println(iae.getMessage());
        }
        out.flush();
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.DISABLE_HTTPD_SHARED_TOMCAT)) {
      if (Aosh.checkParamCount(Command.DISABLE_HTTPD_SHARED_TOMCAT, args, 3, err)) {
        out.println(
            connector.getSimpleClient().disableHttpdSharedTomcat(
                args[1],
                args[2],
                args[3]
            )
        );
        out.flush();
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.ENABLE_HTTPD_SHARED_TOMCAT)) {
      if (Aosh.checkParamCount(Command.ENABLE_HTTPD_SHARED_TOMCAT, args, 2, err)) {
        connector.getSimpleClient().enableHttpdSharedTomcat(args[1], args[2]);
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.GENERATE_SHARED_TOMCAT_NAME)) {
      if (Aosh.checkParamCount(Command.GENERATE_SHARED_TOMCAT_NAME, args, 1, err)) {
        out.println(connector.getSimpleClient().generateSharedTomcatName(args[1]));
        out.flush();
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.IS_SHARED_TOMCAT_NAME_AVAILABLE)) {
      if (Aosh.checkParamCount(Command.IS_SHARED_TOMCAT_NAME_AVAILABLE, args, 1, err)) {
        out.println(connector.getSimpleClient().isSharedTomcatNameAvailable(args[1]));
        out.flush();
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.REMOVE_HTTPD_SHARED_TOMCAT)) {
      if (Aosh.checkParamCount(Command.REMOVE_HTTPD_SHARED_TOMCAT, args, 2, err)) {
        connector.getSimpleClient().removeHttpdSharedTomcat(args[1], args[2]);
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.SET_HTTPD_SHARED_TOMCAT_IS_MANUAL)) {
      if (Aosh.checkParamCount(Command.SET_HTTPD_SHARED_TOMCAT_IS_MANUAL, args, 3, err)) {
        connector.getSimpleClient().setHttpdSharedTomcatIsManual(
            args[1],
            args[2],
            Aosh.parseBoolean(args[3], "is_manual")
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.SET_HTTPD_SHARED_TOMCAT_MAX_POST_SIZE)) {
      if (Aosh.checkParamCount(Command.SET_HTTPD_SHARED_TOMCAT_MAX_POST_SIZE, args, 3, err)) {
        connector.getSimpleClient().setHttpdSharedTomcatMaxPostSize(
            args[1],
            args[2],
            args[3].isEmpty() ? -1 : Aosh.parseInt(args[3], "max_post_size")
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.web_tomcat_SharedTomcat_maxParameterCount_set)) {
      if (Aosh.checkParamCount(Command.web_tomcat_SharedTomcat_maxParameterCount_set, args, 3, err)) {
        connector.getSimpleClient().setHttpdSharedTomcatMaxParameterCount(
            args[1],
            args[2],
            args[3].isEmpty() ? -1 : Aosh.parseInt(args[3], "maxParameterCount")
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.SET_HTTPD_SHARED_TOMCAT_UNPACK_WARS)) {
      if (Aosh.checkParamCount(Command.SET_HTTPD_SHARED_TOMCAT_UNPACK_WARS, args, 3, err)) {
        connector.getSimpleClient().setHttpdSharedTomcatIsManual(
            args[1],
            args[2],
            Aosh.parseBoolean(args[3], "unpack_wars")
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.SET_HTTPD_SHARED_TOMCAT_AUTO_DEPLOY)) {
      if (Aosh.checkParamCount(Command.SET_HTTPD_SHARED_TOMCAT_AUTO_DEPLOY, args, 3, err)) {
        connector.getSimpleClient().setHttpdSharedTomcatAutoDeploy(
            args[1],
            args[2],
            Aosh.parseBoolean(args[3], "auto_deploy")
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.web_tomcat_SharedTomcat_undeployOldVersions_set)) {
      if (Aosh.checkParamCount(Command.web_tomcat_SharedTomcat_undeployOldVersions_set, args, 3, err)) {
        connector.getSimpleClient().setHttpdSharedTomcatUndeployOldVersions(
            args[1],
            args[2],
            Aosh.parseBoolean(args[3], "undeployOldVersions")
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.web_tomcat_SharedTomcat_tomcatAuthentication_set)) {
      if (Aosh.checkParamCount(Command.web_tomcat_SharedTomcat_tomcatAuthentication_set, args, 3, err)) {
        connector.getSimpleClient().setHttpdSharedTomcatTomcatAuthentication(
            args[1],
            args[2],
            Aosh.parseBoolean(args[3], "tomcatAuthentication")
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.SET_HTTPD_SHARED_TOMCAT_VERSION)) {
      if (Aosh.checkParamCount(Command.SET_HTTPD_SHARED_TOMCAT_VERSION, args, 3, err)) {
        connector.getSimpleClient().setHttpdSharedTomcatVersion(
            args[1],
            args[2],
            args[3]
        );
      }
      return true;
    }
    return false;
  }

  public boolean isSharedTomcatNameAvailable(String name) throws IOException, SQLException {
    return connector.requestBooleanQuery(
        true,
        AoservProtocol.CommandId.IS_SHARED_TOMCAT_NAME_AVAILABLE,
        name
    );
  }
}
