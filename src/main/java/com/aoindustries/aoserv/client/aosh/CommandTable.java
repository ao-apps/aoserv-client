/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018, 2020, 2021, 2022  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.aosh;

import com.aoapps.hodgepodge.io.TerminalWriter;
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.GlobalTableStringKey;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.schema.TableTable;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @see  Command
 *
 * @author  AO Industries, Inc.
 */
public final class CommandTable extends GlobalTableStringKey<Command> {

  private static final String GLOBAL_COMMANDS="[[GLOBAL]]";

  private final Map<String, List<Command>> tableCommands=new HashMap<>();

  CommandTable(AOServConnector connector) {
    super(connector, Command.class);
  }

  private static final OrderBy[] defaultOrderBy = {
    new OrderBy(Command.COLUMN_COMMAND_name, ASCENDING)
  };
  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  public List<Command> getAOSHCommands(Table table) throws IOException, SQLException {
    synchronized (this) {
      // Table might be null
      String name = (table == null) ? GLOBAL_COMMANDS : table.getName();
      List<Command> list=tableCommands.get(name);
      if (list != null) {
        return list;
      }

      List<Command> cached=getRows();
      List<Command> matches=new ArrayList<>();
      int size=cached.size();
      for (int c=0;c<size;c++) {
        Command command=cached.get(c);
        if (
          table == null
          ?command.getTable_name() == null
          :name.equals(command.getTable_name())
        ) {
          matches.add(command);
        }
      }
      matches=Collections.unmodifiableList(matches);
      tableCommands.put(name, matches);
      return matches;
    }
  }

  public List<Command> getGlobalAOSHCommands() throws IOException, SQLException {
    return getAOSHCommands(null);
  }

  @Override
  public Table.TableID getTableID() {
    return Table.TableID.AOSH_COMMANDS;
  }

  /**
   * Avoid repeated array copies.
   */
  private static final int numTables = Table.TableID.values().length;

  @Override
  public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IOException, SQLException {
    String command=args[0];
    if (command.equalsIgnoreCase(Command.HELP) || command.equals("?")) {
      int argCount=args.length;
      if (argCount == 1) {
        TableTable schemaTableTable=connector.getSchema().getTable();
        for (int c=-1;c<numTables;c++) {
          String title;
          List<Command> commands;
          if (c == -1) {
            title = "Global Commands:";
            commands = getGlobalAOSHCommands();
          } else {
            Table schemaTable = schemaTableTable.get(c);
            title = schemaTable.getDisplay() + ':';
            commands = schemaTable.getAOSHCommands(connector);
          }
          printHelpList(out, title, commands, true, c >= 0);
        }
        out.flush();
      } else if (argCount == 2) {
        if (args[1].equalsIgnoreCase("syntax")) {
          TableTable schemaTableTable=connector.getSchema().getTable();
          for (int c=-1;c<numTables;c++) {
            String title;
            List<Command> commands;
            if (c == -1) {
              title = "Global Commands:";
              commands = getGlobalAOSHCommands();
            } else {
              Table schemaTable = schemaTableTable.get(c);
              title = schemaTable.getDisplay() + ':';
              commands = schemaTable.getAOSHCommands(connector);
            }
            printHelpList(out, title, commands, false, c >= 0);
          }
          out.flush();
        } else {
          // Try to find the command
          String comName = args[1];
          Command aoshCommand = get(comName);
          if (aoshCommand == null) {
            // Case-insensitive search
            for (Command com : getRows()) {
              if (com.getCommand().equalsIgnoreCase(comName)) {
                aoshCommand = com;
                break;
              }
            }
          }
          if (aoshCommand != null) {
            aoshCommand.printCommandHelp(out);
            out.flush();
          } else {
            err.print("aosh: help: help on command not found: ");
            err.println(comName);
            err.flush();
          }
        }
      } else {
        // Get help for one specific task here
        err.println("aosh: help: too many parameters");
        err.flush();
      }
      return true;
    }
    return false;
  }

  private void printHelpList(TerminalWriter out, String title, List<Command> commands, boolean shortOrSchema, boolean println) throws IOException {
    int len=commands.size();
    if (len>0) {
      if (println) {
        out.println();
      }
      out.boldOn();
      out.println(title);
      out.attributesOff();
      for (int c=0;c<len;c++) {
        Command aoshCom=commands.get(c);
        String command=aoshCom.getCommand();
        out.print("    ");
        out.print(command);
        int space=Math.max(1, 40-command.length());
        for (int d=0;d<space;d++) {
          out.print(d>0 && d<(space-1)?'.':' ');
        }
        // Print the description without the HTML tags
        String desc = shortOrSchema ? aoshCom.getDescription() : aoshCom.getSyntax();
        Command.printNoHTML(out, desc);
        out.println();
      }
    }
  }

  @Override
  public void clearCache() {
    super.clearCache();
    synchronized (this) {
      tableCommands.clear();
    }
  }

  @Override
  public Command get(String command) throws IOException, SQLException {
    return getUniqueRow(Command.COLUMN_COMMAND, command);
  }
}
