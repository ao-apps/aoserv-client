/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2001-2013, 2016  AO Industries, Inc.
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

import com.aoindustries.io.TerminalWriter;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @see  AOSHCommand
 *
 * @author  AO Industries, Inc.
 */
final public class AOSHCommandTable extends GlobalTableStringKey<AOSHCommand> {

	private static final String GLOBAL_COMMANDS="[[GLOBAL]]";

	private final Map<String,List<AOSHCommand>> tableCommands=new HashMap<>();

	AOSHCommandTable(AOServConnector connector) {
		super(connector, AOSHCommand.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(AOSHCommand.COLUMN_COMMAND_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	List<AOSHCommand> getAOSHCommands(SchemaTable table) throws IOException, SQLException {
		synchronized(this) {
			// Table might be null
			String name=table==null?GLOBAL_COMMANDS:table.name;
			List<AOSHCommand> list=tableCommands.get(name);
			if(list!=null) return list;

			List<AOSHCommand> cached=getRows();
			List<AOSHCommand> matches=new ArrayList<>();
			int size=cached.size();
			for(int c=0;c<size;c++) {
				AOSHCommand command=cached.get(c);
				if(
					table==null
					?command.table_name==null
					:name.equals(command.table_name)
				) matches.add(command);
			}
			matches=Collections.unmodifiableList(matches);
			tableCommands.put(name, matches);
			return matches;
		}
	}

	public List<AOSHCommand> getGlobalAOSHCommands() throws IOException, SQLException {
		return getAOSHCommands(null);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.AOSH_COMMANDS;
	}

	/**
	 * Avoid repeated array copies.
	 */
	private static final int numTables = SchemaTable.TableID.values().length;

	@Override
	boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(AOSHCommand.HELP) || command.equals("?")) {
			int argCount=args.length;
			if(argCount==1) {
				SchemaTableTable schemaTableTable=connector.getSchemaTables();
				for(int c=-1;c<numTables;c++) {
					SchemaTable schemaTable=c==-1?null:schemaTableTable.get(c);
					String title=c==-1?"Global Commands:":(schemaTable.getDisplay()+':');
					List<AOSHCommand> commands=c==-1?getGlobalAOSHCommands():schemaTable.getAOSHCommands(connector);
					printHelpList(out, title, commands, true, c>=0);
				}
				out.flush();
			} else if(argCount==2) {
				if(args[1].equals("syntax")) {
					SchemaTableTable schemaTableTable=connector.getSchemaTables();
					for(int c=-1;c<numTables;c++) {
						SchemaTable schemaTable=c==-1?null:schemaTableTable.get(c);
						String title=c==-1?"Global Commands:":(schemaTable.getDisplay()+':');
						List<AOSHCommand> commands=c==-1?getGlobalAOSHCommands():schemaTable.getAOSHCommands(connector);
						printHelpList(out, title, commands, false, c>=0);
					}
					out.flush();
				} else {
					// Try to find the command
					AOSHCommand aoshCom=get(args[1].toLowerCase());
					if(aoshCom!=null) {
						aoshCom.printCommandHelp(out);
						out.flush();
					} else {
						err.print("aosh: help: help on command not found: ");
						err.println(args[1]);
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

	private void printHelpList(TerminalWriter out, String title, List<AOSHCommand> commands, boolean shortOrSchema, boolean println) throws IOException {
		int len=commands.size();
		if(len>0) {
			if(println) out.println();
			out.boldOn();
			out.println(title);
			out.attributesOff();
			for(int c=0;c<len;c++) {
				AOSHCommand aoshCom=commands.get(c);
				String command=aoshCom.getCommand();
				out.print("    ");
				out.print(command);
				int space=Math.max(1, 40-command.length());
				for(int d=0;d<space;d++) out.print(d>0 && d<(space-1)?'.':' ');
				// Print the description without the HTML tags
				String desc=shortOrSchema?aoshCom.getShortDesc():aoshCom.getSyntax();
				AOSHCommand.printNoHTML(out, desc);
				out.println();
			}
		}
	}

	@Override
	public void clearCache() {
		super.clearCache();
		synchronized(this) {
			tableCommands.clear();
		}
	}

	@Override
	public AOSHCommand get(String command) throws IOException, SQLException {
		return getUniqueRow(AOSHCommand.COLUMN_COMMAND, command);
	}
}
